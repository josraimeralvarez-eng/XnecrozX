package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RatesPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.CalculationHistory
import com.example.data.repository.CalculationHistoryRepository
import com.example.model.BinanceMode
import com.example.model.CalculationResult
import com.example.model.ConversionDirection
import com.example.model.ExchangeRates
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class CalculatorUiState(
  val amountInput: String = "100",
  val direction: ConversionDirection = ConversionDirection.USD_TO_VES,
  val binanceMode: BinanceMode = BinanceMode.VENTA,
  val rates: ExchangeRates = ExchangeRates(),
  val showEditRatesDialog: Boolean = false,
  val showShareDialog: Boolean = false,
  val showInfoDialog: Boolean = false,
  val showHistoryDialog: Boolean = false,
  val lastSavedNotification: String? = null
) {
  val parsedAmount: Double
    get() {
      val sanitized = amountInput.replace(',', '.').trim()
      return sanitized.toDoubleOrNull() ?: 0.0
    }

  val result: CalculationResult
    get() = CalculationResult(
      amountInput = parsedAmount,
      direction = direction,
      binanceMode = binanceMode,
      rates = rates
    )
}

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
  private val preferences = RatesPreferences(application)
  private val database = AppDatabase.getDatabase(application)
  private val historyRepository = CalculationHistoryRepository(database.calculationHistoryDao())

  val recentHistory: StateFlow<List<CalculationHistory>> = historyRepository.recentCalculations
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _uiState = MutableStateFlow(
    CalculatorUiState(
      amountInput = preferences.loadLastAmount(),
      rates = preferences.loadRates()
    )
  )
  val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

  private var autoSaveJob: Job? = null

  init {
    // Save initial calculation on launch if valid
    triggerAutoSave()
  }

  private fun triggerAutoSave() {
    autoSaveJob?.cancel()
    autoSaveJob = viewModelScope.launch {
      delay(1200) // Debounce auto-save so typing doesn't spam DB
      val state = _uiState.value
      if (state.parsedAmount > 0) {
        val historyItem = CalculationHistory.fromResult(state.result)
        historyRepository.saveCalculation(historyItem)
      }
    }
  }

  fun saveCurrentCalculationNow() {
    viewModelScope.launch {
      val state = _uiState.value
      if (state.parsedAmount > 0) {
        val historyItem = CalculationHistory.fromResult(state.result)
        historyRepository.saveCalculation(historyItem)
        _uiState.update { it.copy(lastSavedNotification = "Cálculo guardado en historial") }
        delay(2000)
        _uiState.update { it.copy(lastSavedNotification = null) }
      }
    }
  }

  fun restoreCalculation(history: CalculationHistory) {
    _uiState.update { current ->
      val formattedAmount = if (history.amount % 1.0 == 0.0) {
        history.amount.toInt().toString()
      } else {
        history.amount.toString()
      }
      current.copy(
        amountInput = formattedAmount,
        direction = history.parsedDirection,
        binanceMode = history.parsedBinanceMode,
        showHistoryDialog = false
      )
    }
    preferences.saveLastAmount(_uiState.value.amountInput)
  }

  fun deleteHistoryItem(id: Long) {
    viewModelScope.launch {
      historyRepository.deleteById(id)
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      historyRepository.clearHistory()
    }
  }

  fun updateAmount(newAmount: String) {
    // Only allow valid numeric string format with at most one comma/dot
    val cleaned = newAmount.filter { it.isDigit() || it == '.' || it == ',' }
    val dotCount = cleaned.count { it == '.' || it == ',' }
    if (dotCount <= 1 && cleaned.length <= 14) {
      _uiState.update { it.copy(amountInput = cleaned) }
      preferences.saveLastAmount(cleaned)
      triggerAutoSave()
    }
  }

  fun appendDigit(digit: String) {
    val current = _uiState.value.amountInput
    val next = if (current == "0" && digit != "." && digit != ",") digit else current + digit
    updateAmount(next)
  }

  fun backspace() {
    val current = _uiState.value.amountInput
    if (current.isNotEmpty()) {
      val next = current.dropLast(1)
      updateAmount(if (next.isEmpty()) "" else next)
    }
  }

  fun clearAmount() {
    updateAmount("")
  }

  fun setQuickAmount(amount: Double) {
    val formatted = if (amount % 1.0 == 0.0) {
      amount.toInt().toString()
    } else {
      amount.toString()
    }
    updateAmount(formatted)
  }

  fun toggleDirection() {
    _uiState.update { current ->
      val newDirection = if (current.direction == ConversionDirection.USD_TO_VES) {
        ConversionDirection.VES_TO_USD
      } else {
        ConversionDirection.USD_TO_VES
      }
      current.copy(direction = newDirection)
    }
    triggerAutoSave()
  }

  fun setBinanceMode(mode: BinanceMode) {
    _uiState.update { it.copy(binanceMode = mode) }
    triggerAutoSave()
  }

  fun updateRates(
    tasaBcv: Double,
    tasaBinanceVenta: Double,
    tasaBinanceCompra: Double,
    intervencionMargin: Double = _uiState.value.rates.intervencionMarginPercent
  ) {
    val updated = ExchangeRates(
      tasaBcv = if (tasaBcv > 0) tasaBcv else _uiState.value.rates.tasaBcv,
      intervencionMarginPercent = intervencionMargin,
      tasaBinanceVenta = if (tasaBinanceVenta > 0) tasaBinanceVenta else _uiState.value.rates.tasaBinanceVenta,
      tasaBinanceCompra = if (tasaBinanceCompra > 0) tasaBinanceCompra else _uiState.value.rates.tasaBinanceCompra
    )
    _uiState.update { it.copy(rates = updated) }
    preferences.saveRates(updated)
    triggerAutoSave()
  }

  fun resetRatesToDefault() {
    val defaults = preferences.resetDefaults()
    _uiState.update { it.copy(rates = defaults) }
    triggerAutoSave()
  }

  fun openEditRatesDialog() {
    _uiState.update { it.copy(showEditRatesDialog = true) }
  }

  fun closeEditRatesDialog() {
    _uiState.update { it.copy(showEditRatesDialog = false) }
  }

  fun openShareDialog() {
    _uiState.update { it.copy(showShareDialog = true) }
  }

  fun closeShareDialog() {
    _uiState.update { it.copy(showShareDialog = false) }
  }

  fun openInfoDialog() {
    _uiState.update { it.copy(showInfoDialog = true) }
  }

  fun closeInfoDialog() {
    _uiState.update { it.copy(showInfoDialog = false) }
  }

  fun openHistoryDialog() {
    _uiState.update { it.copy(showHistoryDialog = true) }
  }

  fun closeHistoryDialog() {
    _uiState.update { it.copy(showHistoryDialog = false) }
  }

  companion object {
    private val symbols = DecimalFormatSymbols(Locale.GERMANY).apply {
      groupingSeparator = '.'
      decimalSeparator = ','
    }

    private val currencyFormat2Dec = DecimalFormat("#,##0.00", symbols)
    private val currencyFormat3Dec = DecimalFormat("#,##0.000", symbols)
    private val rateFormat = DecimalFormat("#,##0.00#", symbols)

    fun formatMoney(value: Double, decimals: Int = 2): String {
      return if (decimals == 3) {
        currencyFormat3Dec.format(value)
      } else {
        currencyFormat2Dec.format(value)
      }
    }

    fun formatRate(value: Double): String {
      return rateFormat.format(value)
    }

    fun formatPercent(value: Double): String {
      val sign = if (value > 0) "+" else ""
      return "$sign${String.format(Locale.US, "%.2f", value)}%"
    }
  }

  fun generateFormattedSummary(): String {
    val state = _uiState.value
    val res = state.result
    val amountFormatted = formatMoney(state.parsedAmount, 2)
    val bcvRateFormatted = formatRate(state.rates.tasaBcv)
    val intervRateFormatted = formatRate(state.rates.tasaIntervencion)
    val binanceRateFormatted = formatRate(state.rates.getBinanceRate(state.binanceMode))
    val brechaPct = formatPercent(res.brechaPercent)
    val diffBs = formatMoney(res.brechaAbsolutaPorDolar, 2)

    return if (state.direction == ConversionDirection.USD_TO_VES) {
      val totalBcvStr = formatMoney(res.totalBcv, 2)
      val totalIntervStr = formatMoney(res.totalIntervencion, 2)
      val totalBinanceStr = formatMoney(res.totalBinance, 2)

      """
💱 *Cotización y Conversión Cambiaria*
━━━━━━━━━━━━━━━━━━━━
💵 *Monto:* $amountFormatted USD

🏛️ *Tasa BCV Oficial:* Bs. $bcvRateFormatted
👉 *Total BCV:* Bs. $totalBcvStr

🏦 *Tasa Intervención (+0.5%):* Bs. $intervRateFormatted
👉 *Total Intervención:* Bs. $totalIntervStr

🟡 *Tasa Binance (${state.binanceMode.label}):* Bs. $binanceRateFormatted
👉 *Total Binance:* Bs. $totalBinanceStr
━━━━━━━━━━━━━━━━━━━━
📊 *Brecha Binance vs BCV:* $brechaPct (+Bs. $diffBs/$)
      """.trimIndent()
    } else {
      val totalBcvStr = formatMoney(res.totalBcv, 2)
      val totalIntervStr = formatMoney(res.totalIntervencion, 2)
      val totalBinanceStr = formatMoney(res.totalBinance, 2)

      """
💱 *Cotización y Conversión Cambiaria*
━━━━━━━━━━━━━━━━━━━━
🇻🇪 *Monto:* Bs. $amountFormatted

🏛️ *Tasa BCV Oficial:* Bs. $bcvRateFormatted
👉 *Total en USD:* $$totalBcvStr

🏦 *Tasa Intervención (+0.5%):* Bs. $intervRateFormatted
👉 *Total en USD:* $$totalIntervStr

🟡 *Tasa Binance (${state.binanceMode.label}):* Bs. $binanceRateFormatted
👉 *Total en USD:* $$totalBinanceStr
━━━━━━━━━━━━━━━━━━━━
📊 *Brecha Binance vs BCV:* $brechaPct
      """.trimIndent()
    }
  }
}
