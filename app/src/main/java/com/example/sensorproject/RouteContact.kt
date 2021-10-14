package com.example.sensorproject

import androidx.room.Embedded
import androidx.room.Relation

class RouteContact {
    @Embedded
    var route: RouteEntity? = null
}