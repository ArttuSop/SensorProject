package com.example.sensorproject

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RouteDao {

        @Query("SELECT * FROM routeEntity")
        fun getAll(): LiveData<List<RouteEntity>>

        @Query("SELECT * FROM routeentity WHERE uid = :routeIds")
        fun loadAllByIds(routeIds: Long): RouteContact

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(routesEntity: RouteEntity): Long

        @Delete
        fun delete(routeEntity: RouteEntity)
    }
