package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_registrations")
data class ServiceRegistration(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val serviceType: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val notes: String,
    val preferredDate: String,
    val preferredTime: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "รอดำเนินการ (Pending)"
)
