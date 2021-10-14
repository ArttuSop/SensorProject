package com.example.sensorproject

import androidx.room.Embedded

class DayStatsContact {
    @Embedded
    var day: DayStatsEntity? = null
}