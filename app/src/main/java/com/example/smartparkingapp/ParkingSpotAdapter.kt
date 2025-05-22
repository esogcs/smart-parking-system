package com.example.smartparkingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParkingSpotAdapter(
    private var parkingSpots: List<ParkingSpot>,
    private val onItemClick: (ParkingSpot) -> Unit
) : RecyclerView.Adapter<ParkingSpotAdapter.ParkingSpotViewHolder>() {

    class ParkingSpotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val spotNameText: TextView = view.findViewById(R.id.spotNameText)
        val addressText: TextView = view.findViewById(R.id.addressText)
        val priceText: TextView = view.findViewById(R.id.priceText)
        val statusIndicator: View = view.findViewById(R.id.statusIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingSpotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parking_spot, parent, false)
        return ParkingSpotViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParkingSpotViewHolder, position: Int) {
        val spot = parkingSpots[position]
        holder.spotNameText.text = spot.name
        holder.addressText.text = spot.address
        holder.priceText.text = "$${String.format("%.2f", spot.price)}/hour"
        holder.statusIndicator.setBackgroundResource(
            if (spot.isAvailable) android.R.color.holo_green_light
            else android.R.color.holo_red_light
        )

        holder.itemView.setOnClickListener { onItemClick(spot) }
    }

    override fun getItemCount() = parkingSpots.size

    fun updateSpots(newSpots: List<ParkingSpot>) {
        parkingSpots = newSpots
        notifyDataSetChanged()
    }
} 