package com.example.sensorproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorproject.R
import kotlinx.android.synthetic.main.fragment_day.*


class dayFragment : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_day)

        val formattedDate = intent.getStringExtra(Formatted)

        dayDate.text = formattedDate

        Log.d("Formatted", formattedDate.toString())
    }

}