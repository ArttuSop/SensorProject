package com.example.sensorproject.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.sensorproject.DayStatsDB
import com.example.sensorproject.DayStatsEntity
import com.example.sensorproject.R
import kotlinx.android.synthetic.main.fragment_walk.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val Formatted = "formatted"
class WalkFragment : Fragment(R.layout.fragment_walk), SensorEventListener, DateSelected {

    private val db by lazy { DayStatsDB.get(this.requireContext()) }
    private var sSteps: Sensor? = null
    private lateinit var sm: SensorManager
    var currentSteps = 0
    private val dayFragment = dayFragment()
    var simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    var currentDateTime = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sm = (requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        sSteps = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_walk, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun showDatePicker() {
        val datePickerFragment = DatePickerFragment(this)
        datePickerFragment.show(fragmentManager!!, "datePicker")
    }

    fun getDate() {
        //val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        currentDateTime = simpleDateFormat.format(Date())
        dateTv.text = currentDateTime
    }
    companion object {

    }

    override fun onSensorChanged(p0: SensorEvent?) {

        if (currentDateTime != simpleDateFormat.format(Date())) {
            //currentSteps = p0?.values?.get(0)?.toInt()!!
            //reset = false
        }
        val stepVal = p0?.values?.get(0)
        val stepsI = stepVal?.toInt()?.minus(currentSteps)
        steps.text = stepsI.toString()
        val kilometerInt = (stepsI?.times(0.0007))
        kilometers.text = String.format("%.2f",kilometerInt) + " km"

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("Accuracy changed")
    }

    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()
        sm.registerListener(this, sSteps, SensorManager.SENSOR_DELAY_NORMAL)
        getDate()
        btnDatePlanted.setOnClickListener {
            showDatePicker()
        }

    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sm.unregisterListener(this)
    }

    class DatePickerFragment(val dateSelected : DateSelected) : DialogFragment(), DatePickerDialog.OnDateSetListener {
        @SuppressLint("UseRequireInsteadOfGet")
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(context!!, this, year, month, dayOfMonth)
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            dateSelected.receiveDate(year, month, dayOfMonth)
            Log.d(TAG, "Got the date")

        }
    }

    override fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = GregorianCalendar()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        val viewFormatter = SimpleDateFormat("dd.MM.yyyy")
        var viewFormattedDate = viewFormatter.format(calendar.getTime())
        dateTv.setText(viewFormattedDate)

        val intent = Intent(this.requireContext(), dayFragment::class.java).apply {
            putExtra(Formatted, viewFormattedDate)
        }
        GlobalScope.launch {
            db.dayStatsDao().insert(DayStatsEntity(0, "13.10.2021", "1000", "6"))
        }
        startActivity(intent)
    }
}

interface DateSelected {
    fun receiveDate(year: Int, month: Int, dayOfMonth: Int)
}
