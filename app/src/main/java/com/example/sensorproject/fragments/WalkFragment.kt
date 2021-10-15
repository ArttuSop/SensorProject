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
internal const val PrevDate = "date.txt"
internal const val PrevSteps = "steps.txt"
internal const val CheckIfEmpty = "check.txt"

class WalkFragment : Fragment(R.layout.fragment_walk), SensorEventListener, DateSelected {

    private val db by lazy { DayStatsDB.get(this.requireContext()) }
    private var sSteps: Sensor? = null
    private lateinit var sm: SensorManager
    var reset = false
    var dateI = 0
    var currentDate = ""
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
        currentDateTime = simpleDateFormat.format(Date())
        dateTv.text = currentDateTime
    }

    companion object {

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val prevSteps = requireContext().openFileInput(PrevSteps)?.bufferedReader().use {
            it?.readText() ?: getString(R.string.read_file_steps)
        }


        if (reset) {
            requireContext().openFileOutput(PrevSteps, Context.MODE_APPEND).use {
                it.write("${p0?.values?.get(0).toString()}\n".toByteArray())
            }

            if (prevSteps.lines().size > 1) {
                val steps = p0?.values?.get(0)?.toDouble()?.toInt()!! - prevSteps.lines()
                    .get(prevSteps.lines().lastIndex.minus(1)).toDouble().toInt()
                val kilometerInt = steps.times(0.0007)
                GlobalScope.launch {
                    db.dayStatsDao().insert(
                        DayStatsEntity(
                            0,
                            currentDate.lines().get(currentDate.lines().lastIndex.minus(1)),
                            steps.toString(),
                            String.format("%.2f", kilometerInt)
                        )
                    )
                }
            } else {
                val kilometerInt = p0?.values?.get(0)?.times(0.0007)
                GlobalScope.launch {
                    db.dayStatsDao().insert(
                        DayStatsEntity(
                            0,
                            currentDate.lines().get(currentDate.lines().lastIndex.minus(1)),
                            p0?.values?.get(0)?.toInt().toString(),
                            String.format("%.2f", kilometerInt)
                        )
                    )
                }
            }

            reset = false
        }


        val check = requireContext().openFileInput(CheckIfEmpty)?.bufferedReader().use {
            it?.readText() ?: getString(R.string.read_file_test)
        }

        if (check.lines().get(check.lines().lastIndex.minus(0)) == "0") {
            val stepVal = p0?.values?.get(0)
            val stepsI = stepVal?.toInt()?.minus(0)
            steps.text = stepsI.toString()
            val kilometerInt = (stepsI?.times(0.0007))
            kilometers.text = String.format("%.2f", kilometerInt) + " km"
        } else if (prevSteps.lines().size > 1) {
            val i = prevSteps.lines().lastIndex.minus(1)
            val stepVal = p0?.values?.get(0)
            val stepsI = stepVal?.toInt()?.minus(prevSteps.lines().get(i).toDouble().toInt())
            steps.text = stepsI.toString()
            val kilometerInt = (stepsI?.times(0.0007))
            kilometers.text = String.format("%.2f", kilometerInt) + " km"
        } else {
            val prevSteps2 = requireContext().openFileInput(PrevSteps)?.bufferedReader().use {
                it?.readText() ?: getString(R.string.read_file_steps)
            }
            val stepVal = p0?.values?.get(0)
            val stepsI = stepVal?.toInt()?.minus(prevSteps2.toDouble().toInt())
            steps.text = stepsI.toString()
            val kilometerInt = (stepsI?.times(0.0007))
            kilometers.text = String.format("%.2f", kilometerInt) + " km"
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("Accuracy changed")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()
        sm.registerListener(this, sSteps, SensorManager.SENSOR_DELAY_NORMAL)
        getDate()
        btnDatePlanted.setOnClickListener {
            showDatePicker()
        }
        requireContext().openFileOutput(PrevDate, Context.MODE_APPEND).use {

        }

        requireContext().openFileOutput(PrevSteps, Context.MODE_APPEND).use {

        }
        currentDate = requireContext().openFileInput(PrevDate)?.bufferedReader().use {
            it?.readText() ?: getString(R.string.read_file_date)
        }

        dateI = currentDate.lines().lastIndex.minus(1)

        if (dateI == -1) {
            requireContext().openFileOutput(PrevDate, Context.MODE_APPEND).use {
                it.write("${simpleDateFormat.format(Date())}\n".toByteArray())
            }
            requireContext().openFileOutput(CheckIfEmpty, Context.MODE_APPEND).use {
                it.write("0".toByteArray())
            }
        } else if (currentDate.lines().get(dateI) != simpleDateFormat.format(Date())) {
            requireContext().openFileOutput(PrevDate, Context.MODE_APPEND).use {
                it.write("${simpleDateFormat.format(Date())}\n".toByteArray())
            }
            Log.d("Date change", "Date change")
            requireContext().openFileOutput(CheckIfEmpty, Context.MODE_APPEND).use {
                it.write("1".toByteArray())
            }
            reset = true
        }
    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sm.unregisterListener(this)
    }

    class DatePickerFragment(val dateSelected: DateSelected) : DialogFragment(),
        DatePickerDialog.OnDateSetListener {
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

        startActivity(intent)
    }
}

interface DateSelected {
    fun receiveDate(year: Int, month: Int, dayOfMonth: Int)
}
