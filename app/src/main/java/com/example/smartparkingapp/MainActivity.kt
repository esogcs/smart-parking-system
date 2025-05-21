package com.example.smartparkingapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check Firebase login status safely
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            // Already logged in
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            // Not logged in yet
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish() // Prevent back button from returning to MainActivity
    }
}
