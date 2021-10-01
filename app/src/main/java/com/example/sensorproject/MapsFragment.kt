package com.example.sensorproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.sensorproject.PermissionUtils.requestPermission
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

internal const val FILENAME = "Polyline"
class MapsFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private lateinit var map: GoogleMap
    var lat = 0.0
    var lng = 0.0
    private lateinit var bt: Button
    private lateinit var polyline: Polyline
    var start = false
    private var permissionDenied = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_maps, container, false)
        bt = Button(activity)

        //Find the layout with the id you gave in on the xml
        val rl = rootView.findViewById<View>(R.id.map) as FrameLayout

        //And now you can add the buttons you need, because it's a fragment, use getActivity() as context
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        bt.setText("Start")

        //You can add LayoutParams to put the button where you want it and the just add it
        rl.addView(bt, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        bt.setOnClickListener {
            if (bt.text == "Start") {
                start = true
                bt.setText("Stop")
            } else if (bt.text == "Stop") {
                start = false
                bt.setText("Start")
            }
        }

        return rootView
        //return inflater.inflate(R.layout.fragment_maps, container, false)
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



    override fun onResume() {
        super.onResume()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
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
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 16f))
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
                lat = location.latitude
                lng = location.longitude
            }
        } else if (start == false) {
            context?.openFileOutput(FILENAME, Context.MODE_APPEND).use {
                it?.write("${polyline}\n".toByteArray())
                Log.d("Polyline", polyline.toString())
                map.clear()
            }
        }


    }

}