package com.example.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AiRepository
import com.example.data.repository.GroundedResponse
import com.example.data.repository.ThinkingResponse
import com.google.firebase.ai.type.AspectRatio
import com.google.firebase.ai.type.ImageSize
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GroundingUiState {
    data object Idle : GroundingUiState
    data object Loading : GroundingUiState
    data class Success(val response: GroundedResponse) : GroundingUiState
    data class Error(val message: String) : GroundingUiState
}

sealed interface ThinkingUiState {
    data object Idle : ThinkingUiState
    data object Loading : ThinkingUiState
    data class Success(val response: ThinkingResponse) : ThinkingUiState
    data class Error(val message: String) : ThinkingUiState
}

sealed interface ImageGenUiState {
    data object Idle : ImageGenUiState
    data object Loading : ImageGenUiState
    data class Success(val bitmap: Bitmap) : ImageGenUiState
    data class Error(val message: String) : ImageGenUiState
}

class AiViewModel : ViewModel() {
    private val repository = AiRepository()

    private val _mapsGroundingState = MutableStateFlow<GroundingUiState>(GroundingUiState.Idle)
    val mapsGroundingState: StateFlow<GroundingUiState> = _mapsGroundingState.asStateFlow()

    private val _searchGroundingState = MutableStateFlow<GroundingUiState>(GroundingUiState.Idle)
    val searchGroundingState: StateFlow<GroundingUiState> = _searchGroundingState.asStateFlow()

    private val _thinkingState = MutableStateFlow<ThinkingUiState>(ThinkingUiState.Idle)
    val thinkingState: StateFlow<ThinkingUiState> = _thinkingState.asStateFlow()

    private val _imageGenState = MutableStateFlow<ImageGenUiState>(ImageGenUiState.Idle)
    val imageGenState: StateFlow<ImageGenUiState> = _imageGenState.asStateFlow()

    // Selected image generation resolution: 1K (default), 2K, 4K
    private val _selectedImageResolution = MutableStateFlow("1K")
    val selectedImageResolution: StateFlow<String> = _selectedImageResolution.asStateFlow()

    fun setImageResolution(res: String) {
        _selectedImageResolution.value = res
    }

    fun queryMapsGrounding(prompt: String, lat: Double?, lng: Double?) {
        viewModelScope.launch {
            _mapsGroundingState.value = GroundingUiState.Loading
            try {
                val result = repository.queryWithMapsGrounding(prompt, lat, lng)
                _mapsGroundingState.value = GroundingUiState.Success(result)
            } catch (e: TimeoutCancellationException) {
                _mapsGroundingState.value = GroundingUiState.Error("คำขอค้นหาด้วย Maps หมดเวลา (60s) กรุณาลองใหม่อีกครั้ง")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AiViewModel", "Maps Grounding failed", e)
                _mapsGroundingState.value = GroundingUiState.Error(e.localizedMessage ?: "การค้นหาด้วย Google Maps ล้มเหลว")
            }
        }
    }

    fun querySearchGrounding(prompt: String) {
        viewModelScope.launch {
            _searchGroundingState.value = GroundingUiState.Loading
            try {
                val result = repository.queryWithSearchGrounding(prompt)
                _searchGroundingState.value = GroundingUiState.Success(result)
            } catch (e: TimeoutCancellationException) {
                _searchGroundingState.value = GroundingUiState.Error("คำขอค้นหาด้วย Google Search หมดเวลา (60s)")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AiViewModel", "Search Grounding failed", e)
                _searchGroundingState.value = GroundingUiState.Error(e.localizedMessage ?: "การค้นหาด้วย Google Search ล้มเหลว")
            }
        }
    }

    fun queryThinking(prompt: String) {
        viewModelScope.launch {
            _thinkingState.value = ThinkingUiState.Loading
            try {
                val result = repository.queryWithHighThinking(prompt)
                _thinkingState.value = ThinkingUiState.Success(result)
            } catch (e: TimeoutCancellationException) {
                _thinkingState.value = ThinkingUiState.Error("การคิดคำนวณขั้นสูงใช้เวลานานเกิน 60s")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AiViewModel", "Thinking mode failed", e)
                _thinkingState.value = ThinkingUiState.Error(e.localizedMessage ?: "การประมวลผล Thinking Mode ล้มเหลว")
            }
        }
    }

    fun generateImage(prompt: String, aspectRatio: AspectRatio = AspectRatio.SQUARE_1x1) {
        viewModelScope.launch {
            _imageGenState.value = ImageGenUiState.Loading
            try {
                val size = when (_selectedImageResolution.value) {
                    "2K" -> ImageSize.SIZE_2K
                    "4K" -> ImageSize.SIZE_4K
                    else -> ImageSize.SIZE_1K
                }
                val bitmap = repository.generateImage(prompt, size, aspectRatio)
                if (bitmap != null) {
                    _imageGenState.value = ImageGenUiState.Success(bitmap)
                } else {
                    _imageGenState.value = ImageGenUiState.Error("โมเดลไม่ส่งคืนรูปภาพ กรุณาลองปรับคำสั่ง Prompt")
                }
            } catch (e: TimeoutCancellationException) {
                _imageGenState.value = ImageGenUiState.Error("การสร้างภาพหมดเวลาหลังจาก 60 วินาที")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AiViewModel", "Image generation failed", e)
                _imageGenState.value = ImageGenUiState.Error(e.localizedMessage ?: "การสร้างภาพล้มเหลว")
            }
        }
    }
}
