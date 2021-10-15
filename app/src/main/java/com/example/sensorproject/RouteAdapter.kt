package com.example.sensorproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item.view.*

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class RouteViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView)
const val Steps = "steps"
const val Kilometers = "kilometers"
const val Speed = "speed"
const val Date = "date"
const val Time = "Time"
class RouteAdapter (private val items: List<RouteEntity>?) :
    RecyclerView.Adapter<RouteViewHolder>(), OnMapReadyCallback {
    var mapView: MapView? = null
    var decodedPolyline = emptyList<Coordinate>()
    private lateinit var map: GoogleMap
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = RouteViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item,
            parent, false
        )
    )

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(
        holder: RouteViewHolder,
        position: Int
    ) {
        mapView = holder.itemView.findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(Bundle())
        mapView!!.onResume()
        mapView!!.getMapAsync(this)
        decodedPolyline = decode(items?.get(position)?.polyline.toString())

        holder.itemView.recycler_steps.text = holder.itemView.context.getString(R.string.steps, items?.get(position)?.steps.toString())
        holder.itemView.recycler_kilometers.text = holder.itemView.context.getString(R.string.kilometers, items?.get(position)?.kilometers.toString())

        holder.itemView.setOnClickListener {
            val steps = items?.get(position)?.steps
            val kilometers = items?.get(position)?.kilometers
            val speed = items?.get(position)?.speed
            val date = items?.get(position)?.date
            val time = items?.get(position)?.time
            MapsFragmentSingle.polylineO = items?.get(position)?.polyline.toString()
            val intent = Intent(holder.itemView.context, SingleRoute::class.java).apply {
                putExtra(Steps, steps)
                putExtra(Kilometers, kilometers)
                putExtra(Speed, speed)
                putExtra(Date, date)
                putExtra(Time, time)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(decodedPolyline[0].longitude, decodedPolyline[0].latitude), 10F))
        for (i in decodedPolyline.indices) {
            map.addPolyline(
                PolylineOptions().add(
                    LatLng(decodedPolyline[i].longitude, decodedPolyline[i].latitude),
                    LatLng(decodedPolyline[1].longitude, decodedPolyline[1].latitude),
                )
            )
        }
    }
}



