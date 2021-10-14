package com.example.sensorproject.fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.example.sensorproject.MainActivity
import com.example.sensorproject.MapsFragment
import com.example.sensorproject.R
import kotlinx.android.synthetic.main.fragment_walk.*
import java.lang.String.format

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WalkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalkFragment : Fragment(), SensorEventListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var sSteps: Sensor? = null
    private lateinit var sm: SensorManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

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
    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sm.unregisterListener(this)
    }
}