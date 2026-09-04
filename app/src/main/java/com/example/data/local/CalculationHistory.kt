package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.BinanceMode
import com.example.model.CalculationResult
import com.example.model.ConversionDirection

@Entity(tableName = "calculation_history")
data class CalculationHistory(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val amount: Double,
  val direction: String,
  val tasaBcv: Double,
  val tasaIntervencion: Double,
  val tasaBinance: Double,
  val binanceMode: String,
  val totalBcv: Double,
  val totalIntervencion: Double,
  val totalBinance: Double,
  val brechaPercent: Double
) {
  companion object {
    fun fromResult(result: CalculationResult): CalculationHistory {
      return CalculationHistory(
        amount = result.amountInput,
        direction = result.direction.name,
        tasaBcv = result.rates.tasaBcv,
        tasaIntervencion = result.rates.tasaIntervencion,
        tasaBinance = result.rates.getBinanceRate(result.binanceMode),
        binanceMode = result.binanceMode.name,
        totalBcv = result.totalBcv,
        totalIntervencion = result.totalIntervencion,
        totalBinance = result.totalBinance,
        brechaPercent = result.brechaPercent
      )
    }
  }

  val parsedDirection: ConversionDirection
    get() = try {
      ConversionDirection.valueOf(direction)
    } catch (_: Exception) {
      ConversionDirection.USD_TO_VES
    }

  val parsedBinanceMode: BinanceMode
    get() = try {
      BinanceMode.valueOf(binanceMode)
    } catch (_: Exception) {
      BinanceMode.VENTA
    }
}
