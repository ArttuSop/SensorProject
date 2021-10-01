package com.example.sensorproject

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_maps.*

class ReadFile: Activity() {
    var polyline = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        polyline = openFileInput(FILENAME)?.bufferedReader().use {
            it?.readText() ?: getString(R.string.read_file_failed)
        }
        Log.d("Polyline2", polyline)



    }
}