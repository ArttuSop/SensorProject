package com.example.sensorproject

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DayStatsDao {


    @Query("SELECT * FROM daystatsentity")
    fun getAllDays(): LiveData<List<DayStatsEntity>>

    @Query("SELECT * FROM daystatsentity WHERE date = :dayIds")
    fun loadAllByIds(dayIds: String): LiveData<List<DayStatsEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dayStatsEntity: DayStatsEntity): Long

    @Delete
    fun delete(dayStatsEntity: DayStatsEntity)
}