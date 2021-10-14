package com.example.sensorproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.sensorproject.*
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.fragment_saved.*
import kotlinx.coroutines.GlobalScope
import java.text.SimpleDateFormat

class dayFragment : AppCompatActivity() {
    private val db by lazy { DayStatsDB.get(this) }
    var test = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_day)

        val formattedDate = intent.getStringExtra(Formatted)
        dayDate.text = formattedDate
        val values = db.dayStatsDao().loadAllByIds(formattedDate.toString())

        val ump: DayStatsModel by viewModels()
        ump.getDayStats().observe(this) {
            for (i in 0..it.size-1) {
                if (it[i].date.toString() == formattedDate) {
                    step.text = it[i].steps
                    km.text = it[i].kilometers
                }
        }
        }
       Log.d("Values", values.value?.get(0)?.kilometers.toString())

            Log.d("Days", db.dayStatsDao().loadAllByIds(formattedDate.toString()).toString())


        Log.d("Formatted", formattedDate.toString())
    }
}