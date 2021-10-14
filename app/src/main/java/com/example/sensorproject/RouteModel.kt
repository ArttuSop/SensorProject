package com.example.sensorproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class RouteModel(application: Application):
    AndroidViewModel(application) {

    private val routes: LiveData<List<RouteEntity>> =
        RouteDB.get(getApplication()).routeDao().getAll()

    fun getUsers() = routes
}