package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ExchangeRates

class RatesPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("calculadora_cambiaria_prefs", Context.MODE_PRIVATE)

  companion object {
    private const val KEY_TASA_BCV = "tasa_bcv"
    private const val KEY_INTERVENCION_MARGIN = "intervencion_margin"
    private const val KEY_TASA_BINANCE_VENTA = "tasa_binance_venta"
    private const val KEY_TASA_BINANCE_COMPRA = "tasa_binance_compra"
    private const val KEY_LAST_AMOUNT = "last_amount"
    private const val KEY_LAST_DIRECTION = "last_direction"
    private const val KEY_LAST_BINANCE_MODE = "last_binance_mode"

    const val DEFAULT_TASA_BCV = 794.992
    const val DEFAULT_MARGIN_PERCENT = 0.5
    const val DEFAULT_TASA_BINANCE_VENTA = 931.00
    const val DEFAULT_TASA_BINANCE_COMPRA = 925.00
  }

  fun loadRates(): ExchangeRates {
    val bcv = prefs.getFloat(KEY_TASA_BCV, DEFAULT_TASA_BCV.toFloat()).toDouble()
    val margin = prefs.getFloat(KEY_INTERVENCION_MARGIN, DEFAULT_MARGIN_PERCENT.toFloat()).toDouble()
    val binanceVenta = prefs.getFloat(KEY_TASA_BINANCE_VENTA, DEFAULT_TASA_BINANCE_VENTA.toFloat()).toDouble()
    val binanceCompra = prefs.getFloat(KEY_TASA_BINANCE_COMPRA, DEFAULT_TASA_BINANCE_COMPRA.toFloat()).toDouble()

    return ExchangeRates(
      tasaBcv = bcv,
      intervencionMarginPercent = margin,
      tasaBinanceVenta = binanceVenta,
      tasaBinanceCompra = binanceCompra
    )
  }

  fun saveRates(rates: ExchangeRates) {
    prefs.edit()
      .putFloat(KEY_TASA_BCV, rates.tasaBcv.toFloat())
      .putFloat(KEY_INTERVENCION_MARGIN, rates.intervencionMarginPercent.toFloat())
      .putFloat(KEY_TASA_BINANCE_VENTA, rates.tasaBinanceVenta.toFloat())
      .putFloat(KEY_TASA_BINANCE_COMPRA, rates.tasaBinanceCompra.toFloat())
      .apply()
  }

  fun saveLastAmount(amountStr: String) {
    prefs.edit().putString(KEY_LAST_AMOUNT, amountStr).apply()
  }

  fun loadLastAmount(): String {
    return prefs.getString(KEY_LAST_AMOUNT, "100") ?: "100"
  }

  fun resetDefaults(): ExchangeRates {
    val defaultRates = ExchangeRates(
      tasaBcv = DEFAULT_TASA_BCV,
      intervencionMarginPercent = DEFAULT_MARGIN_PERCENT,
      tasaBinanceVenta = DEFAULT_TASA_BINANCE_VENTA,
      tasaBinanceCompra = DEFAULT_TASA_BINANCE_COMPRA
    )
    saveRates(defaultRates)
    return defaultRates
  }
}
