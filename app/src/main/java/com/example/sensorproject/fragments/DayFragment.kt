package com.example.sensorproject.fragments

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorproject.*
import kotlinx.android.synthetic.main.fragment_day.*


class DayFragment : AppCompatActivity() {
    var test = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_day)

        val formattedDate = intent.getStringExtra(Formatted)
        dayDate.text = formattedDate

        val ump: DayStatsModel by viewModels()
        ump.getDayStats().observe(this) {
            for (i in 0..it.size - 1) {
                if (it[i].date.toString() == formattedDate) {
                    step.text = it[i].steps
                    km.text = it[i].kilometers
                    val calo = String.format("%.2f",
                        it[i].kilometers?.get(0)?.let { it.code.toDouble() })
                    cal.text = calo
                }
            }
        }
    }
}