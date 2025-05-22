package com.example.smartparkingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ParkingSpotAdapter
    private lateinit var addParkingSpotFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        recyclerView = findViewById(R.id.parkingSpotsRecyclerView)
        addParkingSpotFab = findViewById(R.id.addParkingSpotFab)

        // Setup RecyclerView
        adapter = ParkingSpotAdapter(emptyList()) { spot ->
            // Handle parking spot click
            if (spot.isAvailable) {
                startBookingActivity(spot)
            } else {
                Toast.makeText(this, "This parking spot is not available", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // Setup FAB - Only show for parking lot owners
        addParkingSpotFab.setOnClickListener {
            checkUserTypeAndAddParkingSpot()
        }

        // Load parking spots
        loadParkingSpots()
    }

    private fun startBookingActivity(spot: ParkingSpot) {
        val intent = Intent(this, BookingActivity::class.java).apply {
            putExtra("parkingSpot", spot)
        }
        startActivity(intent)
    }

    private fun checkUserTypeAndAddParkingSpot() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val userType = document.getString("userType")
                if (userType == "Parking Lot Owner") {
                    // TODO: Implement add parking spot functionality
                    Toast.makeText(this, "Add parking spot clicked", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Only parking lot owners can add spots", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error checking user type: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadParkingSpots() {
        // First, try to load from Firestore
        db.collection("parkingSpots")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // If no data in Firestore, show sample data
                    showSampleData()
                } else {
                    val spots = documents.mapNotNull { doc: QueryDocumentSnapshot ->
                        doc.toObject(ParkingSpot::class.java)
                    }
                    adapter.updateSpots(spots)
                }
            }
            .addOnFailureListener { e ->
                // If Firestore fails, show sample data
                Toast.makeText(this, "Using sample data: ${e.message}",
                    Toast.LENGTH_SHORT).show()
                showSampleData()
            }
    }

    private fun showSampleData() {
        val sampleSpots = listOf(
            ParkingSpot(
                id = "1",
                name = "Downtown Garage",
                address = "123 Main Street",
                price = 5.00,
                isAvailable = true,
                ownerId = "owner1",
                totalSpots = 50,
                availableSpots = 25,
                latitude = 40.7128,
                longitude = -74.0060,
                description = "Secure parking garage in downtown area"
            ),
            ParkingSpot(
                id = "2",
                name = "Mall Parking",
                address = "456 Shopping Ave",
                price = 3.50,
                isAvailable = true,
                ownerId = "owner2",
                totalSpots = 100,
                availableSpots = 75,
                latitude = 40.7589,
                longitude = -73.9851,
                description = "Large parking lot near shopping mall"
            ),
            ParkingSpot(
                id = "3",
                name = "Airport Parking",
                address = "789 Airport Blvd",
                price = 7.25,
                isAvailable = false,
                ownerId = "owner3",
                totalSpots = 200,
                availableSpots = 0,
                latitude = 40.6413,
                longitude = -73.7781,
                description = "Long-term parking at airport"
            ),
            ParkingSpot(
                id = "4",
                name = "City Center Lot",
                address = "321 Downtown Rd",
                price = 4.00,
                isAvailable = true,
                ownerId = "owner4",
                totalSpots = 30,
                availableSpots = 15,
                latitude = 40.7829,
                longitude = -73.9654,
                description = "Convenient parking in city center"
            )
        )
        adapter.updateSpots(sampleSpots)
    }
}
