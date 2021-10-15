package com.example.sensorproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorproject.*
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.fragment_walk.*

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
                    var calo = it[i].kilometers!!.toInt().times(50)
                    cal.text = calo.toString()
                }
        }
        }


       Log.d("Values", values.value?.get(0)?.kilometers.toString())

            Log.d("Days", db.dayStatsDao().loadAllByIds(formattedDate.toString()).toString())


        Log.d("Formatted", formattedDate.toString())
    }
}