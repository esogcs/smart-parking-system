package com.example.smartparkingapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val dateTimeFormatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm")
    .withZone(ZoneId.systemDefault())
    .withLocale(Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingHistoryScreen(parkingHistoryList: List<ParkingHistoryEntity>) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Parking History") }) }
    ) { innerPadding ->
        if (parkingHistoryList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No parking history found.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = parkingHistoryList,
                    key = { it.id }  // stable key
                ) { history ->
                    ParkingHistoryItem(history)
                }
            }
        }
    }
}

@Composable
private fun ParkingHistoryItem(history: ParkingHistoryEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Location: ${history.locationName}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Parked at: ${dateTimeFormatter.format(Instant.ofEpochMilli(history.parkedAt))}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Duration: ${history.durationMinutes} minutes",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
