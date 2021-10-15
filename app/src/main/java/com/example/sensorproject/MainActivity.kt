package com.example.sensorproject

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sensorproject.fragments.WalkFragment
import com.example.sensorproject.fragments.SavedFragment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback {

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
}
