package com.example.smartparkingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val spots by lazy {
        intent.getParcelableArrayListExtra<ParkingSpot>("spots") ?: emptyList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val fragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, fragment)
            .commit()
        fragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        spots.forEach { spot ->
            val position = LatLng(spot.latitude, spot.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(spot.name)
            )
        }

        if (spots.isNotEmpty()) {
            val firstLocation = LatLng(spots[0].latitude, spots[0].longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 13f))
        }
    }
}
