package com.example.sensorproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DayStatsEntity::class), version = 1)
abstract class DayStatsDB : RoomDatabase() {
    abstract fun dayStatsDao(): DayStatsDao

    companion object {
        private var sInstance: DayStatsDB? = null

        @Synchronized
        fun get(context: Context): DayStatsDB {
            if (sInstance == null) {
                sInstance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        DayStatsDB::class.java, "days.db"
                    ).build()
            }
            return sInstance!!
        }
    }
}