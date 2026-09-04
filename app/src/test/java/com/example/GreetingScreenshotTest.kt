package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.BinanceMode
import com.example.model.CalculationResult
import com.example.model.ConversionDirection
import com.example.model.ExchangeRates
import com.example.ui.components.AmountInputSection
import com.example.ui.components.BrechaAnalyticsCard
import com.example.ui.components.RatesTickerBanner
import com.example.ui.components.ResultsSection
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val rates = ExchangeRates(
      tasaBcv = 794.992,
      intervencionMarginPercent = 0.5,
      tasaBinanceVenta = 931.00,
      tasaBinanceCompra = 925.00
    )
    val result = CalculationResult(
      amountInput = 100.0,
      direction = ConversionDirection.USD_TO_VES,
      binanceMode = BinanceMode.VENTA,
      rates = rates
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        ResultsSection(
          result = result,
          selectedBinanceMode = BinanceMode.VENTA,
          onBinanceModeChange = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

