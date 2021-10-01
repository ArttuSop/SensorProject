package com.example.sensorproject.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sensorproject.FILENAME
import com.example.sensorproject.R
import com.example.sensorproject.RoutesModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SavedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedFragment : Fragment(R.layout.fragment_saved) {
    internal var activityCallBack: RecyclerFragmentListener? = null
    var polyline = ""
    inner class ItemHolder (view: View): RecyclerView.ViewHolder(view) {
        var textField = view.findViewById<TextView>(android.R.id.text1)
    }

    override fun onViewCreated (view: View, savedInstanceState: Bundle?) {
        polyline = context?.openFileInput(FILENAME)?.bufferedReader().use {
            it?.readText() ?: getString(R.string.read_file_failed)
        }
        Log.d("Polyline2", polyline)
        val rvitems = view.findViewById<RecyclerView>(R.id.recyclerView);
        rvitems.layoutManager = LinearLayoutManager(context)
        rvitems.adapter = object : RecyclerView.Adapter<ItemHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context).inflate(
                        android.R.layout.simple_list_item_1, parent,
                        false
                    )
                )
            }

            override fun getItemCount(): Int {
                return RoutesModel.routes.size
            }

            override fun onBindViewHolder(holder: ItemHolder, position: Int) {
                holder.textField.text = RoutesModel.routes[position].speedV + RoutesModel.routes[position].kiloV + RoutesModel.routes[position].polylineV + RoutesModel.routes[position].stepsV
                holder.textField.setOnClickListener {
                    Log.d("USR", "Clicked $position")
                    activityCallBack!!.onButtonClick(position)
                }
            }
        }
    }

    interface RecyclerFragmentListener {
        fun onButtonClick(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("USR", "onAttach received!!!")
        activityCallBack = context as RecyclerFragmentListener
    }
}