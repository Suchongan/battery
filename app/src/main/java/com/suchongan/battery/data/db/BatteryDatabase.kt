package com.suchongan.battery.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BatterySample::class], version = 1, exportSchema = false)
abstract class BatteryDatabase : RoomDatabase() {

    abstract fun batterySampleDao(): BatterySampleDao

    companion object {
        @Volatile private var instance: BatteryDatabase? = null

        fun getInstance(context: Context): BatteryDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    BatteryDatabase::class.java,
                    "battery.db",
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
