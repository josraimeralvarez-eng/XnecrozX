package com.example.model

enum class BinanceMode(val label: String, val shortLabel: String) {
  VENTA("Venta", "Venta"),
  COMPRA("Compra", "Compra")
}

enum class ConversionDirection(val fromSymbol: String, val toSymbol: String, val fromName: String, val toName: String) {
  USD_TO_VES("$", "Bs.", "Dólares (USD)", "Bolívares (VES)"),
  VES_TO_USD("Bs.", "$", "Bolívares (VES)", "Dólares (USD)")
}

data class ExchangeRates(
  val tasaBcv: Double = 794.992,
  val intervencionMarginPercent: Double = 0.5,
  val tasaBinanceVenta: Double = 931.00,
  val tasaBinanceCompra: Double = 925.00
) {
  val tasaIntervencion: Double
    get() = tasaBcv * (1.0 + (intervencionMarginPercent / 100.0))

  fun getBinanceRate(mode: BinanceMode): Double = when (mode) {
    BinanceMode.VENTA -> tasaBinanceVenta
    BinanceMode.COMPRA -> tasaBinanceCompra
  }

  fun getBrechaPercent(mode: BinanceMode): Double {
    val binance = getBinanceRate(mode)
    return if (tasaBcv > 0) ((binance - tasaBcv) / tasaBcv) * 100.0 else 0.0
  }

  fun getBrechaAbsoluta(mode: BinanceMode): Double {
    val binance = getBinanceRate(mode)
    return binance - tasaBcv
  }
}

data class CalculationResult(
  val amountInput: Double,
  val direction: ConversionDirection,
  val binanceMode: BinanceMode,
  val rates: ExchangeRates
) {
  val totalBcv: Double
    get() = when (direction) {
      ConversionDirection.USD_TO_VES -> amountInput * rates.tasaBcv
      ConversionDirection.VES_TO_USD -> if (rates.tasaBcv > 0) amountInput / rates.tasaBcv else 0.0
    }

  val totalIntervencion: Double
    get() = when (direction) {
      ConversionDirection.USD_TO_VES -> amountInput * rates.tasaIntervencion
      ConversionDirection.VES_TO_USD -> if (rates.tasaIntervencion > 0) amountInput / rates.tasaIntervencion else 0.0
    }

  val totalBinance: Double
    get() {
      val rate = rates.getBinanceRate(binanceMode)
      return when (direction) {
        ConversionDirection.USD_TO_VES -> amountInput * rate
        ConversionDirection.VES_TO_USD -> if (rate > 0) amountInput / rate else 0.0
      }
    }

  val totalBinanceVenta: Double
    get() = when (direction) {
      ConversionDirection.USD_TO_VES -> amountInput * rates.tasaBinanceVenta
      ConversionDirection.VES_TO_USD -> if (rates.tasaBinanceVenta > 0) amountInput / rates.tasaBinanceVenta else 0.0
    }

  val totalBinanceCompra: Double
    get() = when (direction) {
      ConversionDirection.USD_TO_VES -> amountInput * rates.tasaBinanceCompra
      ConversionDirection.VES_TO_USD -> if (rates.tasaBinanceCompra > 0) amountInput / rates.tasaBinanceCompra else 0.0
    }

  val brechaPercent: Double
    get() = rates.getBrechaPercent(binanceMode)

  val brechaAbsolutaPorDolar: Double
    get() = rates.getBrechaAbsoluta(binanceMode)

  val diferenciaTotalBs: Double
    get() = when (direction) {
      ConversionDirection.USD_TO_VES -> (totalBinance - totalBcv)
      ConversionDirection.VES_TO_USD -> (totalBcv - totalBinance) // Difference in USD received
    }
}
