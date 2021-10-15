package com.example.sensorproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DayStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val date: String?,
    val steps: String?,
    val kilometers: String?,
) {
    //constructor, getter and setter are implicit :)
    override fun toString() = "($uid) $date $steps $kilometers"
}