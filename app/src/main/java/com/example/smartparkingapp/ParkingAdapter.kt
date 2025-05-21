package com.example.smartparkingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ParkingAdapter(private val parkingList: List<ParkingSpot>) :
    RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder>() {

    class ParkingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.parking_name)
        val locationText: TextView = view.findViewById(R.id.parking_location)
        val priceText: TextView = view.findViewById(R.id.parking_price)
        val bookButton: Button = view.findViewById(R.id.book_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parking_spot, parent, false)
        return ParkingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParkingViewHolder, position: Int) {
        val spot = parkingList[position]
        holder.nameText.text = spot.name
        holder.locationText.text = spot.location
        holder.priceText.text = "$${spot.price}"

        // Handle Book Button Click
        holder.bookButton.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "You booked ${spot.name}!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = parkingList.size
}

