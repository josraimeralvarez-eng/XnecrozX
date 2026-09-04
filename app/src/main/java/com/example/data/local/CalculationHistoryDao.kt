package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationHistoryDao {
  @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentCalculations(limit: Int = 5): Flow<List<CalculationHistory>>

  @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
  fun getAllCalculations(): Flow<List<CalculationHistory>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCalculation(history: CalculationHistory): Long

  @Query("DELETE FROM calculation_history WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM calculation_history")
  suspend fun clearAll()
}
