package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ServiceRegistration
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationDao {
    @Query("SELECT * FROM service_registrations ORDER BY timestamp DESC")
    fun getAllRegistrations(): Flow<List<ServiceRegistration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: ServiceRegistration): Long

    @Delete
    suspend fun deleteRegistration(registration: ServiceRegistration)

    @Query("DELETE FROM service_registrations")
    suspend fun clearAll()
}
