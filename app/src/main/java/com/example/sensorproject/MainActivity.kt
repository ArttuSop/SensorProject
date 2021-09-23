package com.example.sensorproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sensorproject.fragments.WalkFragment
import com.example.sensorproject.fragments.MapFragment
import com.example.sensorproject.fragments.SavedFragment
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val walkFragment = WalkFragment()
    private val mapFragment = MapFragment()
    private val savedFragment = SavedFragment()

    private lateinit var sm: SensorManager
    private var sSteps: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       hasPermissions()

        sm = getSystemService(SENSOR_SERVICE) as
                SensorManager

        sSteps = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        replaceFragment(walkFragment)

        bottomBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.map -> replaceFragment(mapFragment)
                R.id.saved -> replaceFragment(savedFragment)
                R.id.walk -> replaceFragment(walkFragment)
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    private fun hasPermissions(): Boolean {
        if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DBG", "No audio recorder access")
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1);
            return true // assuming that the user grants permission
        }
        return true
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val stepVal = p0?.values?.get(0)
        val stepsI = stepVal?.toInt()
        steps.text = stepsI.toString()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("Accuracy changed")
    }

    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()
        sm.registerListener(this, sSteps, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sm.unregisterListener(this)
    }
}