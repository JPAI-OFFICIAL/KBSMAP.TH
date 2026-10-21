package com.example.ui.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.LocationPoint
import com.example.service.LocationTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationUiState(
    val currentPoint: LocationPoint? = null,
    val isTracking: Boolean = false,
    val history: List<LocationPoint> = emptyList(),
    val hasPermission: Boolean = false,
    val totalDistanceMeters: Double = 0.0,
    val errorMessage: String? = null
)

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationTracker = LocationTracker(application)
    private var trackingJob: Job? = null

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    fun onPermissionResult(isGranted: Boolean) {
        _uiState.update { it.copy(hasPermission = isGranted) }
        if (isGranted) {
            fetchInitialLocation()
            startContinuousTracking()
        } else {
            _uiState.update { it.copy(errorMessage = "ต้องการสิทธิ์การเข้าถึงตำแหน่งเพื่อใช้งานฟีเจอร์นี้") }
        }
    }

    fun fetchInitialLocation() {
        viewModelScope.launch {
            val point = locationTracker.getLastKnownLocation()
            if (point != null) {
                _uiState.update { state ->
                    val newHistory = if (state.history.isEmpty()) listOf(point) else state.history
                    state.copy(currentPoint = point, history = newHistory, errorMessage = null)
                }
            }
        }
    }

    fun toggleTracking(enable: Boolean) {
        if (enable) {
            startContinuousTracking()
        } else {
            stopContinuousTracking()
        }
    }

    private fun startContinuousTracking() {
        trackingJob?.cancel()
        _uiState.update { it.copy(isTracking = true, errorMessage = null) }
        trackingJob = viewModelScope.launch {
            try {
                locationTracker.getLocationUpdates(3000L).collect { point ->
                    _uiState.update { state ->
                        val last = state.currentPoint
                        val addedDist = if (last != null) {
                            val results = FloatArray(1)
                            Location.distanceBetween(
                                last.latitude, last.longitude,
                                point.latitude, point.longitude,
                                results
                            )
                            results[0].toDouble()
                        } else 0.0

                        // Append to history (keep max 100 points for memory)
                        val updatedHistory = (listOf(point) + state.history).take(100)
                        state.copy(
                            currentPoint = point,
                            history = updatedHistory,
                            totalDistanceMeters = state.totalDistanceMeters + addedDist
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isTracking = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun stopContinuousTracking() {
        trackingJob?.cancel()
        trackingJob = null
        _uiState.update { it.copy(isTracking = false) }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList(), totalDistanceMeters = 0.0) }
    }

    override fun onCleared() {
        super.onCleared()
        stopContinuousTracking()
    }
}
