package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import com.example.data.model.LocationPoint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class LocationTracker(private val context: Context) {
    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): LocationPoint? = suspendCancellableCoroutine { continuation ->
        try {
            client.lastLocation
                .addOnSuccessListener { loc: Location? ->
                    if (loc != null) {
                        val point = LocationPoint(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            altitude = loc.altitude,
                            accuracy = loc.accuracy,
                            speed = loc.speed,
                            bearing = loc.bearing,
                            timestamp = loc.time,
                            address = getAddressFromCoordinates(loc.latitude, loc.longitude)
                        )
                        continuation.resume(point)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LocationTracker", "Failed to get last location", e)
                    continuation.resume(null)
                }
        } catch (e: SecurityException) {
            Log.e("LocationTracker", "SecurityException fetching location", e)
            continuation.resume(null)
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(intervalMs: Long = 3000L): Flow<LocationPoint> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .setMinUpdateDistanceMeters(1.0f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (loc in result.locations) {
                    val point = LocationPoint(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        altitude = loc.altitude,
                        accuracy = loc.accuracy,
                        speed = loc.speed,
                        bearing = loc.bearing,
                        timestamp = loc.time,
                        address = getAddressFromCoordinates(loc.latitude, loc.longitude)
                    )
                    trySend(point)
                }
            }
        }

        try {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.e("LocationTracker", "Failed to start location updates due to permissions", e)
            close(e)
        }

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

    fun getAddressFromCoordinates(lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("th", "TH"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    addr.getAddressLine(0) ?: "${addr.subLocality ?: ""}, ${addr.locality ?: ""}, ${addr.adminArea ?: ""}"
                } else null
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    addr.getAddressLine(0) ?: "${addr.subLocality ?: ""}, ${addr.locality ?: ""}, ${addr.adminArea ?: ""}"
                } else null
            }
        } catch (e: Exception) {
            Log.w("LocationTracker", "Geocoding error", e)
            null
        }
    }
}
