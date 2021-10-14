package com.example.sensorproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.single_route.*


class SingleRoute: AppCompatActivity (R.layout.single_route) {
    private val mapsFragmentSingle = MapsFragmentSingle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_route)

        replaceFragment(mapsFragmentSingle)

        val steps = intent.getStringExtra(Steps)
        val kilometers = intent.getStringExtra(Kilometers)
        val speed = intent.getStringExtra(Speed)
        val date = intent.getStringExtra(Date)
        val time =  intent.getStringExtra(Time)

        steps_single.text = getString(R.string.steps, steps)
        kilometers_single.text = getString(R.string.kilometers, kilometers)
        speed_single.text = getString(R.string.speed, String.format("%.2f",speed?.toDouble()))
        date_single.text = date
        if (time?.toDouble()!! < 1.0) {
            val timeD = time.toDouble() * 60
            time_single.text = getString(R.string.minutes, String.format("%.2f",timeD))
        } else {
            time_single.text = getString(R.string.hours, String.format("%.2f",time.toDouble()))
        }

    }

    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mapContainer, fragment)
            transaction.commit()
        }
    }
}