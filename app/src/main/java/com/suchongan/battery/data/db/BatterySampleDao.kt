package com.suchongan.battery.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BatterySampleDao {

    @Insert
    suspend fun insert(sample: BatterySample)

    @Query("SELECT * FROM battery_samples WHERE timestampMillis >= :sinceMillis ORDER BY timestampMillis ASC")
    fun getSamplesSince(sinceMillis: Long): Flow<List<BatterySample>>

    @Query("DELETE FROM battery_samples WHERE timestampMillis < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)
}
