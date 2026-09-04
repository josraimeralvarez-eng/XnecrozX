package com.example

import com.example.data.local.CalculationHistory
import com.example.model.BinanceMode
import com.example.model.CalculationResult
import com.example.model.ConversionDirection
import com.example.model.ExchangeRates
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testCurrencyCalculations() {
    val rates = ExchangeRates(
      tasaBcv = 794.992,
      intervencionMarginPercent = 0.5,
      tasaBinanceVenta = 931.00,
      tasaBinanceCompra = 925.00
    )

    // Test Intervencion Rate (BCV + 0.5%)
    val expectedIntervencion = 794.992 * 1.005
    assertEquals(expectedIntervencion, rates.tasaIntervencion, 0.001)

    // Test Conversion for 100 USD
    val result = CalculationResult(
      amountInput = 100.0,
      direction = ConversionDirection.USD_TO_VES,
      binanceMode = BinanceMode.VENTA,
      rates = rates
    )

    assertEquals(79499.20, result.totalBcv, 0.01)
    assertEquals(79896.696, result.totalIntervencion, 0.01)
    assertEquals(93100.00, result.totalBinance, 0.01)

    // Test Brecha Binance vs BCV
    val expectedBrecha = ((931.00 - 794.992) / 794.992) * 100.0
    assertEquals(expectedBrecha, result.brechaPercent, 0.01)
    assertEquals(136.008, result.brechaAbsolutaPorDolar, 0.001)

    // Test CalculationHistory fromResult
    val history = CalculationHistory.fromResult(result)
    assertEquals(100.0, history.amount, 0.001)
    assertEquals("USD_TO_VES", history.direction)
    assertEquals(ConversionDirection.USD_TO_VES, history.parsedDirection)
    assertEquals(BinanceMode.VENTA, history.parsedBinanceMode)
    assertEquals(79499.20, history.totalBcv, 0.01)
  }
}


