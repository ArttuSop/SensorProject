package com.example.sensorproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorproject.fragments.SavedFragment
import com.google.android.gms.maps.model.Polyline
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.schedule

object RoutesModel {
    val routes: kotlin.collections.MutableList<Route> = java.util.ArrayList()
    val polylineS = SavedFragment().polyline
    init {


        Log.d("USR", "This ($this) is a singleton")
// construct the data source

                routes.add(Route("5km/h", "20km", "200Steps", polylineS))
    }
}