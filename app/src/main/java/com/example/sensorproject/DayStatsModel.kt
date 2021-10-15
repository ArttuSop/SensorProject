package com.example.sensorproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class DayStatsModel(application: Application) :
    AndroidViewModel(application) {

    private val days: LiveData<List<DayStatsEntity>> =
        DayStatsDB.get(getApplication()).dayStatsDao().getAllDays()

    fun getDayStats() = days
}