package com.example.sensorproject

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.sensorproject.fragments.WalkFragment
import com.example.sensorproject.fragments.SavedFragment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback  {

    private val walkFragment = WalkFragment()
    private val mapsFragment = MapsFragment()
    private val savedFragment = SavedFragment()

    @RequiresApi(Build.VERSION_CODES.Q)
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
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun hasPermissions(): Boolean {
        if ((checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) &&
            (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DBG", "No activity recognition access")
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return true // assuming that the user grants permission
        }
        return true
    }

}
