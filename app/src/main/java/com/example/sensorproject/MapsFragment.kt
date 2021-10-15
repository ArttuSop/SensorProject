package com.example.sensorproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sensorproject.PermissionUtils.requestPermission
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.gms.maps.SupportMapFragment
import java.text.SimpleDateFormat
import java.util.*

import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.sensorproject.fragments.WalkFragment
import kotlinx.android.synthetic.main.fragment_walk.*
import kotlinx.android.synthetic.main.fragment_walk.view.*


class MapsFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener,
    SensorEventListener {

    private lateinit var map: GoogleMap
    var lat = 0.0
    var lng = 0.0
    private lateinit var bt: Button
    private lateinit var polyline: Polyline
    var start = false
    var polylineList = emptyList<Coordinate>()
    var encodedPolyline = ""
    var stepsRoute = 0
    var kilometersS = ""
    var kilometersDouble = 0.0
    var seconds = 0
    var stepsWalk = 0
    var kilometersWalk = 0.0
    var running = false
    private var myLocation = false
    var getCurrentSteps = false
    private var sSteps: Sensor? = null
    private lateinit var sm: SensorManager
    private var permissionDenied = false
    private val db by lazy { RouteDB.get(this.requireContext()) }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_maps, container, false)
        bt = Button(activity)
        bt.setBackgroundColor(Color.parseColor("#1565C0"))
        //Find the layout with the id you gave in on the xml
        val rl = rootView.findViewById<View>(R.id.map) as FrameLayout

        //And now you can add the buttons you need, because it's a fragment, use getActivity() as context
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        bt.text = getString(R.string.start)

        //You can add LayoutParams to put the button where you want it and the just add it
        rl.addView(bt, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        sm = (requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager)

        sSteps = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        bt.setOnClickListener {
            if (bt.text == "Start") {
                start = true
                running = true
                getCurrentSteps = true
                runTimer()
                bt.text = getString(R.string.stop)
            } else if (bt.text == "Stop") {
                start = false
                running = false
                bt.text = getString(R.string.start)
                Log.d("Seconds in stop", seconds.toString())
                encodedPolyline = encode(polylineList)
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
                val currentDateTime = simpleDateFormat.format(Date())
                val hours = seconds.toDouble() / 3600
                Log.d("Hours in stop", hours.toString())
                Log.d("StepsWalk", stepsWalk.toString())
                Log.d("KilometersWalk", kilometersWalk.toString())
                stepsRoute -= stepsWalk
                kilometersDouble -= kilometersWalk
                val avgSpeed = kilometersDouble / hours
                kilometersS = String.format("%.2f",kilometersDouble) + " km"
                GlobalScope.launch {
                    db.routeDao().insert(RouteEntity(0, currentDateTime, kilometersS, encodedPolyline, avgSpeed.toString(), stepsRoute.toString(), hours.toString()))
                }
                map.clear()
            }
        }
        return rootView
    }

    private fun runTimer() {

        // Creates a new Handler
        val handler = Handler()

        handler.post(object : Runnable {
            override fun run() {

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                    Log.d("Seconds", seconds.toString())
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (getCurrentSteps) {
            stepsWalk = p0?.values?.get(0)?.toInt()!!
            kilometersWalk = p0.values?.get(0)?.toInt()!!.times(0.0007)
            getCurrentSteps = false
        }
        Log.d("Steps", stepsRoute.toString())
        Log.d("Kilometers", kilometersDouble.toString())
        val stepVal = p0?.values?.get(0)
        val stepsI = stepVal?.toInt()
        if (stepsI != null) {
            stepsRoute = stepsI
        }
        kilometersDouble = (stepsI?.times(0.0007)!!)

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

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(requireContext(), "Current location:\n$location", Toast.LENGTH_LONG).show()
        Log.d("CurrentLocation", location.longitude.toString())
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap ?: return
        polyline = map.addPolyline(PolylineOptions().add(LatLng(0.0, 0.0))) ?: return
        myLocation = true
        enableMyLocation()
        map.isMyLocationEnabled = true
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        map.setOnMyLocationChangeListener(this)
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(this, MapsFragment.LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    private fun showMissingPermissionError() {
      PermissionUtils.PermissionDeniedDialog.newInstance(true).show(childFragmentManager, "dialog")
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onMyLocationChange(location: Location) {
        Log.d("LocationChanging", location.longitude.toString())
        if (myLocation == true) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 12f))
            myLocation = false
        }

        if (start == true) {
            if (lat == 0.0 && lng == 0.0) {
                lat = location.latitude
                lng = location.longitude
            } else if (location.latitude != lat && location.longitude != lng) {
              polyline = map.addPolyline(
                    PolylineOptions().add(
                        LatLng(lat, lng),
                        LatLng(location.latitude, location.longitude)
                    )
                )
                polylineList = polylineList + Coordinate(lat, lng) + Coordinate(location.latitude, location.longitude)
                Log.d("polylineList", polylineList.toString())
                lat = location.latitude
                lng = location.longitude

            }
        }
    }

}