package com.example.data.repository

import com.example.data.local.CalculationHistory
import com.example.data.local.CalculationHistoryDao
import kotlinx.coroutines.flow.Flow

class CalculationHistoryRepository(private val dao: CalculationHistoryDao) {
  val recentCalculations: Flow<List<CalculationHistory>> = dao.getRecentCalculations(limit = 5)

  suspend fun saveCalculation(history: CalculationHistory): Long {
    return dao.insertCalculation(history)
  }

  suspend fun deleteById(id: Long) {
    dao.deleteById(id)
  }

  suspend fun clearHistory() {
    dao.clearAll()
  }
}
