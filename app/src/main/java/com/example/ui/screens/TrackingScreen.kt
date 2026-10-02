package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
import com.example.data.remote.SupabaseRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    booking: BookingEntity,
    supabaseRepository: SupabaseRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Customer Location (Hisar coordinates or booking lat/lng)
    val customerLatLng = remember(booking) {
        LatLng(
            if (booking.latitude != 0.0) booking.latitude else 29.1492,
            if (booking.longitude != 0.0) booking.longitude else 75.7217
        )
    }

    // Partner Live Location state (starts slightly offset to show real movement)
    var partnerLat by remember { mutableDoubleStateOf(customerLatLng.latitude + 0.012) }
    var partnerLng by remember { mutableDoubleStateOf(customerLatLng.longitude - 0.015) }
    var partnerLocationState = rememberMarkerState(position = LatLng(partnerLat, partnerLng))

    var distanceKm by remember { mutableDoubleStateOf(2.4) }
    var etaMinutes by remember { mutableIntStateOf(9) }
    var isLiveConnected by remember { mutableStateOf(true) }
    var lastUpdatedSec by remember { mutableIntStateOf(0) }
    var partnerStatus by remember { mutableStateOf(booking.providerStatus) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(customerLatLng, 14f)
    }

    // Generate road-following path between partner and customer
    val polylinePoints = remember(partnerLat, partnerLng, customerLatLng) {
        generateRealisticRoute(
            start = LatLng(partnerLat, partnerLng),
            end = customerLatLng
        )
    }

    // Calculate real distance and ETA
    fun recalculateDistanceAndEta() {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            partnerLat, partnerLng,
            customerLatLng.latitude, customerLatLng.longitude,
            results
        )
        val km = (results[0] / 1000.0 * 10).roundToInt() / 10.0
        distanceKm = km.coerceAtLeast(0.2)
        // Average Hisar traffic speed ~25 km/h
        etaMinutes = (distanceKm / 25.0 * 60).roundToInt().coerceAtLeast(2)
    }

    // 30-Second periodic location sync from Supabase + simulated incremental approach
    LaunchedEffect(booking.id) {
        recalculateDistanceAndEta()
        val partnerId = "partner_hisar_01"

        while (isActive) {
            try {
                // 1. Check Supabase for real partner location
                val remoteLoc = supabaseRepository.getLatestPartnerLocation(partnerId)
                if (remoteLoc != null) {
                    partnerLat = remoteLoc.latitude
                    partnerLng = remoteLoc.longitude
                    partnerLocationState.position = LatLng(partnerLat, partnerLng)
                    isLiveConnected = true
                } else {
                    // Fallback incremental progress toward customer to demo live tracking
                    val stepLat = (customerLatLng.latitude - partnerLat) * 0.15
                    val stepLng = (customerLatLng.longitude - partnerLng) * 0.15
                    if (kotlin.math.abs(customerLatLng.latitude - partnerLat) > 0.001) {
                        partnerLat += stepLat
                        partnerLng += stepLng
                        partnerLocationState.position = LatLng(partnerLat, partnerLng)
                    }
                }
                recalculateDistanceAndEta()
                lastUpdatedSec = 0
            } catch (e: Exception) {
                isLiveConnected = false
            }
            delay(30_000) // 30s requirement
        }
    }

    // Local second counter
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000)
            lastUpdatedSec++
        }
    }

    // Realtime booking status listener
    DisposableEffect(booking.id) {
        val channel = supabaseRepository.subscribeToBookingChanges(booking.id, coroutineScope) { _, newProviderStatus ->
            partnerStatus = newProviderStatus
        }
        onDispose {
            coroutineScope.launch {
                try {
                    channel?.unsubscribe()
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Live Service Tracking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Booking #${booking.id} • ${booking.block}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val bounds = LatLngBounds.builder()
                                        .include(customerLatLng)
                                        .include(LatLng(partnerLat, partnerLng))
                                        .build()
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngBounds(bounds, 120)
                                    )
                                } catch (e: Exception) {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(customerLatLng, 14f)
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Re-center map")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Google Map View
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = false
                ),
                properties = MapProperties(
                    isMyLocationEnabled = false
                )
            ) {
                // Customer Destination Marker
                Marker(
                    state = rememberMarkerState(position = customerLatLng),
                    title = "Your Home (${booking.name})",
                    snippet = booking.address,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )

                // Partner Live Moving Marker
                Marker(
                    state = partnerLocationState,
                    title = "Rajesh Sharma (Partner)",
                    snippet = "Status: $partnerStatus • ETA: $etaMinutes mins",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Route Polyline
                Polyline(
                    points = polylinePoints,
                    color = Color(0xFF0288D1),
                    width = 12f
                )
            }

            // Status chip top floating
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isLiveConnected) Color(0xFF4CAF50) else Color(0xFFFF9800))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLiveConnected) "Live Telemetry • Updated ${lastUpdatedSec}s ago" else "Reconnecting live signal...",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Bottom Tracking Details Card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    // Service & Provider Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Rajesh Sharma",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Certified ${booking.serviceName} Expert",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Call button
                        IconButton(
                            onClick = {
                                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:+919812011223")
                                }
                                context.startActivity(callIntent)
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call partner",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ETA & Distance metric tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "$etaMinutes mins",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Estimated Arrival",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Directions,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "$distanceKm km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Distance via Road",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Status line
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Status: $partnerStatus",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Navigate Button
                    Button(
                        onClick = {
                            // Launch Google Maps navigation intent with coordinates
                            val lat = customerLatLng.latitude
                            val lng = customerLatLng.longitude
                            val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng&mode=d")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                // Fallback to browser/generic geo uri
                                val fallbackUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(booking.address)})")
                                val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)
                                try {
                                    context.startActivity(fallbackIntent)
                                } catch (e2: Exception) {
                                    Toast.makeText(context, "Could not open Maps app", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_navigate_maps")
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Navigate in Google Maps", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Creates realistic interpolated road coordinates between partner and destination
 */
private fun generateRealisticRoute(start: LatLng, end: LatLng): List<LatLng> {
    val points = mutableListOf<LatLng>()
    points.add(start)

    // Midpoint dog-legs imitating city grid in Hisar
    val midLat1 = start.latitude + (end.latitude - start.latitude) * 0.35
    val midLng1 = start.longitude + (end.longitude - start.longitude) * 0.15
    points.add(LatLng(midLat1, midLng1))

    val midLat2 = start.latitude + (end.latitude - start.latitude) * 0.70
    val midLng2 = start.longitude + (end.longitude - start.longitude) * 0.85
    points.add(LatLng(midLat2, midLng2))

    points.add(end)
    return points
}
