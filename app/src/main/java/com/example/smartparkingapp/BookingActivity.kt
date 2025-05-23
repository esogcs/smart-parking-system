package com.example.smartparkingapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var startDateEditText: TextInputEditText
    private lateinit var endDateEditText: TextInputEditText
    private lateinit var confirmButton: MaterialButton
    private lateinit var parkingSpot: ParkingSpot
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get parking spot from intent
        @Suppress("DEPRECATION")
        parkingSpot = intent.getParcelableExtra("parkingSpot") as? ParkingSpot ?: run {
            Toast.makeText(this, "Error: Parking spot not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        // Initialize views
        startDateEditText = findViewById(R.id.startDateEditText)
        endDateEditText = findViewById(R.id.endDateEditText)
        confirmButton = findViewById(R.id.confirmButton)

        // Setup date/time pickers
        startDateEditText.setOnClickListener { showDateTimePicker(true) }
        endDateEditText.setOnClickListener { showDateTimePicker(false) }

        // Setup confirm button
        confirmButton.setOnClickListener {
            if (validateInputs()) {
                createBooking()
            }
        }
    }

    private fun showDateTimePicker(isStartDate: Boolean) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                showTimePicker(isStartDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(isStartDate: Boolean) {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                updateDateTimeField(isStartDate)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateDateTimeField(isStartDate: Boolean) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val dateTime = dateFormat.format(calendar.time)
        if (isStartDate) {
            startDateEditText.setText(dateTime)
        } else {
            endDateEditText.setText(dateTime)
        }
    }

    private fun validateInputs(): Boolean {
        val startTime = startDateEditText.text.toString()
        val endTime = endDateEditText.text.toString()

        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please select both start and end times", Toast.LENGTH_SHORT).show()
            return false
        }

        // Add more validation as needed
        return true
    }

    private fun createBooking() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Please login to book", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val startTime = dateFormat.parse(startDateEditText.text.toString())?.time ?: 0
        val endTime = dateFormat.parse(endDateEditText.text.toString())?.time ?: 0

        // Calculate total price
        val hours = (endTime - startTime) / (1000 * 60 * 60)
        val totalPrice = hours * parkingSpot.price

        val booking = Booking(
            id = UUID.randomUUID().toString(),
            parkingSpotId = parkingSpot.id,
            userId = userId,
            startTime = startTime,
            endTime = endTime,
            totalPrice = totalPrice
        )

        // Save booking to Firestore
        db.collection("bookings")
            .document(booking.id)
            .set(booking)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking created successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error creating booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
} 