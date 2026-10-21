package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ServiceRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrationFormState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val serviceType: String = "บริการระบุพิกัดและสำรวจพื้นที่",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val notes: String = "",
    val preferredDate: String = "",
    val preferredTime: String = "",
    val isSubmitting: Boolean = false,
    val submissionSuccessMessage: String? = null,
    val errorMessage: String? = null
)

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).registrationDao()

    val submittedRegistrations: StateFlow<List<ServiceRegistration>> = dao.getAllRegistrations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()

    val officialGoogleFormUrl = "https://forms.gle/Vzq6PAesAujb3KSE7"

    fun onFullNameChange(value: String) = _formState.update { it.copy(fullName = value) }
    fun onPhoneChange(value: String) = _formState.update { it.copy(phoneNumber = value) }
    fun onEmailChange(value: String) = _formState.update { it.copy(email = value) }
    fun onServiceTypeChange(value: String) = _formState.update { it.copy(serviceType = value) }
    fun onAddressChange(value: String) = _formState.update { it.copy(address = value) }
    fun onNotesChange(value: String) = _formState.update { it.copy(notes = value) }
    fun onPreferredDateChange(value: String) = _formState.update { it.copy(preferredDate = value) }
    fun onPreferredTimeChange(value: String) = _formState.update { it.copy(preferredTime = value) }

    fun populateWithCurrentLocation(lat: Double, lng: Double, address: String?) {
        _formState.update {
            it.copy(
                latitude = lat,
                longitude = lng,
                address = if (address.isNullOrBlank()) "${String.format("%.6f", lat)}, ${String.format("%.6f", lng)}" else address
            )
        }
    }

    fun submitRegistration() {
        val current = _formState.value
        if (current.fullName.isBlank()) {
            _formState.update { it.copy(errorMessage = "กรุณากรอกชื่อ-นามสกุล") }
            return
        }
        if (current.phoneNumber.isBlank()) {
            _formState.update { it.copy(errorMessage = "กรุณากรอกเบอร์โทรศัพท์ติดต่อ") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, errorMessage = null) }
            try {
                val registration = ServiceRegistration(
                    fullName = current.fullName.trim(),
                    phoneNumber = current.phoneNumber.trim(),
                    email = current.email.trim(),
                    serviceType = current.serviceType,
                    latitude = current.latitude,
                    longitude = current.longitude,
                    address = current.address.trim(),
                    notes = current.notes.trim(),
                    preferredDate = current.preferredDate.trim(),
                    preferredTime = current.preferredTime.trim()
                )
                val id = dao.insertRegistration(registration)
                _formState.update {
                    it.copy(
                        isSubmitting = false,
                        submissionSuccessMessage = "บันทึกคำขอรับบริการรหัส #$id เรียบร้อยแล้ว ทีมงานจะติดต่อกลับโดยเร็วที่สุด",
                        fullName = "",
                        phoneNumber = "",
                        email = "",
                        notes = "",
                        preferredDate = "",
                        preferredTime = ""
                    )
                }
            } catch (e: Exception) {
                _formState.update {
                    it.copy(isSubmitting = false, errorMessage = "เกิดข้อผิดพลาดในการบันทึก: ${e.localizedMessage}")
                }
            }
        }
    }

    fun deleteRegistration(item: ServiceRegistration) {
        viewModelScope.launch {
            dao.deleteRegistration(item)
        }
    }

    fun clearFeedback() {
        _formState.update { it.copy(submissionSuccessMessage = null, errorMessage = null) }
    }
}
