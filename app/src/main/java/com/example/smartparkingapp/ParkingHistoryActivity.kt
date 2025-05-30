package com.example.smartparkingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val dateTimeFormatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm")
    .withZone(ZoneId.systemDefault())
    .withLocale(Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
class ParkingHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // grab current user
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return finish()

        val dao = AppDatabase.getInstance(this).parkingHistoryDao()

        setContent {
            // collect the Flow<List<ParkingHistoryEntity>> as State
            val historyList by produceState<List<ParkingHistoryEntity>>(initialValue = emptyList()) {
                dao.getHistoryForUser(userId).collect { value = it }
            }

            Scaffold(topBar = { TopAppBar(title = { Text("Parking History") }) }) { padding ->
                if (historyList.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No parking history found.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(historyList, key = { it.id }) { history ->
                            ParkingHistoryItem(history)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParkingHistoryItem(history: ParkingHistoryEntity) {
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Location: ${history.locationName}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Parked at: ${dateTimeFormatter.format(Instant.ofEpochMilli(history.parkedAt))}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Duration: ${history.durationMinutes} minutes",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
