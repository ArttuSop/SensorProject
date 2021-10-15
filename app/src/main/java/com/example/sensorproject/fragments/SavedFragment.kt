package com.example.sensorproject.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sensorproject.*
import kotlinx.android.synthetic.main.fragment_saved.*
import androidx.fragment.app.*


class SavedFragment : Fragment(R.layout.fragment_saved) {
    override fun onViewCreated(
        view: View, savedInstanceState:
        Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        val ump: RouteModel by viewModels()
        ump.getRoutes().observe(this) {
            recyclerView.adapter = RouteAdapter(it?.sortedBy { that ->
                that.uid
            })
        }
    }
}