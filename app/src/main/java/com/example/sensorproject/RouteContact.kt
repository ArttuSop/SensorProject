package com.example.sensorproject

import androidx.room.Embedded

class RouteContact {
    @Embedded
    var route: RouteEntity? = null
}