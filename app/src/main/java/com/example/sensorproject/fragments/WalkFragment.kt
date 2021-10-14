package com.example.sensorproject.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.example.sensorproject.MainActivity
import com.example.sensorproject.MapsFragment
import com.example.sensorproject.R
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.fragment_walk.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class WalkFragment : Fragment(), SensorEventListener, DateSelected {


    private var sSteps: Sensor? = null
    private lateinit var sm: SensorManager

    private val dayFragment = dayFragment()

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
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        val currentDateTime = simpleDateFormat.format(Date())
        dateTv.text = currentDateTime
    }
    companion object {

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val stepVal = p0?.values?.get(0)
        val stepsI = stepVal?.toInt()
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
    fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container, fragment)
            transaction?.commit()
        }
    }
    override fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = GregorianCalendar()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        val viewFormatter = SimpleDateFormat("ddMMyyyy")
        var viewFormattedDate = viewFormatter.format(calendar.getTime())
        dateTv.setText(viewFormattedDate)

        replaceFragment(dayFragment)

    }
}

interface DateSelected {
    fun receiveDate(year: Int, month: Int, dayOfMonth: Int)
}
