package com.example.sensorproject

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Polyline

@Entity
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val date: String?,
    val kilometers: String?,
    val polyline: String?,
    val speed: String?,
    val steps: String?,
    val time: String?,
) {
    //constructor, getter and setter are implicit :)
    override fun toString() = "($uid) $date $kilometers $polyline $speed $steps $time"
}
