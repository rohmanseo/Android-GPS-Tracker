package com.example.android.walkmyandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.walkmyandroid.model.LocationModel
import com.example.android.walkmyandroid.R

class LocationAdapter(private val context: Context, private val list: ArrayList<LocationModel>) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime = view.findViewById(R.id.tvTime) as TextView
        val tvAddress = view.findViewById(R.id.locAddress) as TextView
        val tvLatLong = view.findViewById(R.id.locLatLong) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTime.text = list[position].time
        holder.tvAddress.text = list[position].address
        holder.tvLatLong.text = with(list[position]){
            "Latitude : $latitude, Longitude : $longitude"
        }
    }

}