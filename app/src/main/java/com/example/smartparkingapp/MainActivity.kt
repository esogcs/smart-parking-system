package com.example.smartparkingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
    private lateinit var mapFab: FloatingActionButton
    private lateinit var searchView: SearchView

    private val allSpots = mutableListOf<ParkingSpot>()
    private val filteredSpots = mutableListOf<ParkingSpot>()

    private var currentUserType: String? = null
    private lateinit var historyFab: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupRecyclerView()
        setupFabListeners()
        setupSearch()
        fetchUserRole()
    }

    /** Initializes view references */
    private fun initViews() {
        recyclerView = findViewById(R.id.parkingSpotsRecyclerView)
        addParkingSpotFab = findViewById(R.id.addParkingSpotFab)
        mapFab = findViewById(R.id.mapFab)
        searchView = findViewById(R.id.searchView)
        historyFab = findViewById(R.id.historyFab)
    }

    /** Configures RecyclerView and adapter */
    private fun setupRecyclerView() {
        adapter = ParkingSpotAdapter(filteredSpots) { spot ->
            if (spot.isAvailable) {
                startBookingActivity(spot)
            } else {
                Toast.makeText(this, "This parking spot is not available", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    /** Sets up Floating Action Button click listeners */
    private fun setupFabListeners() {
        addParkingSpotFab.setOnClickListener { addSpotAction() }
        mapFab.setOnClickListener { openMap() }
        historyFab.setOnClickListener {
            startActivity(Intent(this, ParkingHistoryActivity::class.java))
        }

    }

    /** Enables search filtering */
    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterParkingSpots(newText)
                return true
            }
        })
    }

    /** Filters parking spots based on search query */
    private fun filterParkingSpots(query: String?) {
        val search = query.orEmpty().trim().lowercase()
        filteredSpots.clear()

        val results = if (search.isEmpty()) {
            allSpots
        } else {
            allSpots.filter {
                it.name.lowercase().contains(search) || it.address.lowercase().contains(search)
            }
        }

        filteredSpots.addAll(results)
        adapter.updateSpots(filteredSpots)
    }

    /** Loads user role and parking data */
    private fun fetchUserRole() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                currentUserType = document.getString("userType")
                configureFabVisibility()
                loadParkingSpots()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show()
                configureFabVisibility()
                loadParkingSpots()
            }
    }

    /** Shows/hides FAB based on user type */
    private fun configureFabVisibility() {
        if (currentUserType == "Parking Lot Owner") {
            addParkingSpotFab.show()
        } else {
            addParkingSpotFab.hide()
        }
    }

    /** Loads parking spots from Firestore */
    private fun loadParkingSpots() {
        db.collection("parkingSpots")
            .get()
            .addOnSuccessListener { documents ->
                allSpots.clear()
                for (doc: QueryDocumentSnapshot in documents) {
                    allSpots.add(doc.toObject(ParkingSpot::class.java))
                }
                updateFilteredSpots(allSpots)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Using sample data: ${e.message}", Toast.LENGTH_SHORT).show()
                showSampleData()
            }
    }

    /** Updates filtered list and notifies adapter */
    private fun updateFilteredSpots(spots: List<ParkingSpot>) {
        filteredSpots.clear()
        filteredSpots.addAll(spots)
        adapter.updateSpots(filteredSpots)
    }

    /** Launches AddParkingSpotActivity */
    private fun addSpotAction() {
        if (currentUserType == "Parking Lot Owner") {
            startActivity(Intent(this, AddParkingSpotActivity::class.java))
        } else {
            Toast.makeText(this, "Only parking lot owners can add spots", Toast.LENGTH_SHORT).show()
        }
    }

    /** Opens map activity with current filtered spots */
    private fun openMap() {
        val intent = Intent(this, MapsActivity::class.java).apply {
            putParcelableArrayListExtra("spots", ArrayList(filteredSpots))
        }
        startActivity(intent)
    }

    /** Loads fallback data if Firestore fails */
    private fun showSampleData() {
        allSpots.clear()
        allSpots.addAll(
            listOf(
                ParkingSpot("1", "Downtown Garage", "123 Main Street", 5.00, true, "owner1", 50, 25, 40.7128, -74.0060, "Secure parking garage"),
                ParkingSpot("2", "Mall Parking", "456 Shopping Ave", 3.50, true, "owner2", 100, 75, 40.7589, -73.9851, "Large parking lot"),
                ParkingSpot("3", "Airport Parking", "789 Airport Blvd", 7.25, false, "owner3", 200, 0, 40.6413, -73.7781, "Long-term parking"),
                ParkingSpot("4", "City Center Lot", "321 Downtown Rd", 4.00, true, "owner4", 30, 15, 40.7829, -73.9654, "City center parking")
            )
        )
        updateFilteredSpots(allSpots)
    }

    /** Starts booking activity for selected spot */
    private fun startBookingActivity(spot: ParkingSpot) {
        val intent = Intent(this, BookingActivity::class.java).apply {
            putExtra("parkingSpot", spot)
        }
        startActivity(intent)
    }
}
