package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LocationPoint
import com.example.data.model.ServiceRegistration
import com.example.ui.viewmodel.RegistrationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationFormScreen(
    viewModel: RegistrationViewModel,
    currentLocation: LocationPoint?,
    onOpenWebViewForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val submittedList by viewModel.submittedRegistrations.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val serviceTypes = listOf(
        "บริการระบุพิกัดและสำรวจพื้นที่",
        "บริการจัดส่งพัสดุและขนส่งด่วน",
        "บริการช่างเทคนิคตรวจเช็คอุปกรณ์หน้างาน",
        "บริการตรวจวัดและเก็บข้อมูลทางภูมิศาสตร์",
        "บริการพิเศษอื่นๆ"
    )
    var isDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Official Google Form External Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Official Form",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "แบบฟอร์มลงทะเบียนบริการของฉัน",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "คุณสามารถกรอกแบบฟอร์มลงทะเบียนผ่าน Google Forms ทางการได้โดยตรง หรือกรอกในแอปนี้เพื่อบันทึกประวัติการขอรับบริการ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.officialGoogleFormUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_external_form_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in browser", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("เปิดฟอร์มในเบราว์เซอร์", fontSize = 12.sp, maxLines = 1)
                        }

                        FilledTonalButton(
                            onClick = onOpenWebViewForm,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_webview_form_button")
                        ) {
                            Icon(Icons.Default.Web, contentDescription = "Open inside app", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("เปิดฟอร์มในแอป", fontSize = 12.sp, maxLines = 1)
                        }
                    }
                }
            }
        }

        // In-App Registration Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "กรอกแบบฟอร์มเพื่อใช้บริการแอปของฉัน",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "กรอกรายละเอียดเพื่อส่งคำขอรับบริการ พร้อมพิกัดตำแหน่งระบุจุดเป้าหมาย",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Success or error alerts
                    AnimatedVisibility(visible = formState.submissionSuccessMessage != null) {
                        Surface(
                            color = Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF059669))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formState.submissionSuccessMessage ?: "",
                                    color = Color(0xFF065F46),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = formState.errorMessage != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formState.errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Field 1: Full Name
                    OutlinedTextField(
                        value = formState.fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        label = { Text("ชื่อ-นามสกุล *") },
                        placeholder = { Text("เช่น สมชาย มีสุข") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("full_name_input"),
                        singleLine = true
                    )

                    // Field 2: Phone Number
                    OutlinedTextField(
                        value = formState.phoneNumber,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        label = { Text("เบอร์โทรศัพท์ติดต่อ *") },
                        placeholder = { Text("เช่น 081-234-5678") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_input"),
                        singleLine = true
                    )

                    // Field 3: Email
                    OutlinedTextField(
                        value = formState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = { Text("อีเมลติดต่อ (Email)") },
                        placeholder = { Text("example@gmail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        singleLine = true
                    )

                    // Field 4: Service Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = formState.serviceType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("ประเภทบริการที่ต้องการ") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("service_type_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            serviceTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        viewModel.onServiceTypeChange(type)
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Field 5: Location Coordinates & Auto-fill from GPS
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ตำแหน่งเป้าหมายบริการ (GPS)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (currentLocation != null) {
                                FilledTonalButton(
                                    onClick = {
                                        viewModel.populateWithCurrentLocation(
                                            currentLocation.latitude,
                                            currentLocation.longitude,
                                            currentLocation.address
                                        )
                                    },
                                    modifier = Modifier.testTag("autofill_location_button")
                                ) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ดึงพิกัดปัจจุบัน", fontSize = 11.sp)
                                }
                            }
                        }

                        if (formState.latitude != 0.0 || formState.longitude != 0.0) {
                            Text(
                                text = "พิกัด: ${String.format("%.6f", formState.latitude)}, ${String.format("%.6f", formState.longitude)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = formState.address,
                            onValueChange = { viewModel.onAddressChange(it) },
                            label = { Text("ที่อยู่ / จุดสังเกตของสถานที่") },
                            placeholder = { Text("เช่น ตรอก/ซอย, แขวง/ตำบล, ใกล้แยก...") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("address_input"),
                            maxLines = 2
                        )
                    }

                    // Field 6: Preferred Date & Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.preferredDate,
                            onValueChange = { viewModel.onPreferredDateChange(it) },
                            label = { Text("วันที่ต้องการ") },
                            placeholder = { Text("เช่น 25 ต.ค. 2569") },
                            leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preferred_date_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = formState.preferredTime,
                            onValueChange = { viewModel.onPreferredTimeChange(it) },
                            label = { Text("เวลา") },
                            placeholder = { Text("เช่น 10:00 น.") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preferred_time_input"),
                            singleLine = true
                        )
                    }

                    // Field 7: Notes / Instructions
                    OutlinedTextField(
                        value = formState.notes,
                        onValueChange = { viewModel.onNotesChange(it) },
                        label = { Text("รายละเอียดเพิ่มเติมหรือความต้องการเฉพาะ") },
                        placeholder = { Text("ระบุข้อมูลเพิ่มเติม...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notes_input"),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Submit Button
                    Button(
                        onClick = { viewModel.submitRegistration() },
                        enabled = !formState.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_registration_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (formState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("ส่งคำขอลงทะเบียนรับบริการ (Submit Request)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Submitted List Section
        item {
            Text(
                text = "ประวัติคำขอรับบริการที่บันทึกแล้ว (${submittedList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (submittedList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "ยังไม่มีประวัติการส่งคำขอรับบริการ",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(submittedList) { item ->
                SubmittedRequestCard(item = item, onDelete = { viewModel.deleteRegistration(item) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SubmittedRequestCard(
    item: ServiceRegistration,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "คำขอ #${item.id}: ${item.fullName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "เบอร์: ${item.phoneNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = item.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "บริการ: ${item.serviceType}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            if (item.address.isNotBlank()) {
                Text(
                    text = "สถานที่: ${item.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (item.latitude != 0.0) {
                Text(
                    text = "พิกัด: ${String.format("%.5f", item.latitude)}, ${String.format("%.5f", item.longitude)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "บันทึก: ${item.notes}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(item.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
