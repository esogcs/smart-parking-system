package com.example.smartparkingapp

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddParkingSpotActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var totalSpotsInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parking_spot)

        nameInput         = findViewById(R.id.nameInput)
        addressInput      = findViewById(R.id.addressInput)
        priceInput        = findViewById(R.id.priceInput)
        totalSpotsInput   = findViewById(R.id.totalSpotsInput)
        descriptionInput  = findViewById(R.id.descriptionInput)
        saveButton        = findViewById(R.id.saveButton)

        saveButton.setOnClickListener { saveParkingSpot() }
    }

    private fun saveParkingSpot() {
        val name       = nameInput.text.toString().trim()
        val addressStr = addressInput.text.toString().trim()
        val price      = priceInput.text.toString().toDoubleOrNull()
        val totalSpots = totalSpotsInput.text.toString().toIntOrNull()
        val description= descriptionInput.text.toString().trim()
        val ownerId    = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isEmpty() || addressStr.isEmpty() || price == null || totalSpots == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // 1) Geocode the address
        val geocoder = Geocoder(this, Locale.getDefault())
        val results = try {
            geocoder.getFromLocationName(addressStr, 1)
        } catch (e: Exception) {
            emptyList()
        }

        if (results.isNullOrEmpty()) {
            Toast.makeText(this, "Unable to find location for address", Toast.LENGTH_LONG).show()
            return
        }

        val location = results[0]
        val lat = location.latitude
        val lng = location.longitude

        // 2) Build your ParkingSpot with real coords
        val id = UUID.randomUUID().toString()
        val spot = ParkingSpot(
            id = id,
            name = name,
            address = addressStr,
            price = price,
            isAvailable = true,
            ownerId = ownerId,
            totalSpots = totalSpots,
            availableSpots = totalSpots,
            latitude = lat,
            longitude = lng,
            description = description
        )

        // 3) Return it to MainActivity (or persist to Firestore)
        // If you still want to save to Firestore:
        db.collection("parkingSpots").document(id)
            .set(spot)
            .addOnSuccessListener {
                Toast.makeText(this, "Parking spot added", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent().apply {
                    putExtra("newSpot", spot)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add spot: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
