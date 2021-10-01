package com.example.sensorproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sensorproject.fragments.WalkFragment
import com.example.sensorproject.fragments.SavedFragment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.sensorproject.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.sensorproject.PermissionUtils.isPermissionGranted
import com.example.sensorproject.PermissionUtils.requestPermission
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), /*OnMapReadyCallback,*/
    ActivityCompat.OnRequestPermissionsResultCallback, SavedFragment.RecyclerFragmentListener {

    private val walkFragment = WalkFragment()
    private val mapsFragment = MapsFragment()
    private val savedFragment = SavedFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       hasPermissions()


        replaceFragment(walkFragment)

        bottomBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.map -> replaceFragment(mapsFragment)
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
            Log.d("DBG", "No activity recognition access")
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1)
            return true // assuming that the user grants permission
        }
        return true
    }
    
    override fun onButtonClick(position: Int) {

    }


}
