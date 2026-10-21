package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.ImageGenUiState
import com.example.ui.viewmodel.ThinkingUiState
import com.google.firebase.ai.type.AspectRatio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiThinkingAndImageScreen(
    aiViewModel: AiViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val thinkingState by aiViewModel.thinkingState.collectAsStateWithLifecycle()
    val imageGenState by aiViewModel.imageGenState.collectAsStateWithLifecycle()
    val selectedResolution by aiViewModel.selectedImageResolution.collectAsStateWithLifecycle()

    var thinkingPrompt by remember {
        mutableStateOf("ช่วยวางแผนเส้นทางส่งของ 5 จุดแบบประหยัดพลังงานที่สุด พร้อมวิธีคำนวณระยะทางและช่วงเวลาเลี่ยงรถติด")
    }
    var imagePrompt by remember {
        mutableStateOf("Futuristic 3D map holographic navigation display with glowing cyan routes and pins, cinematic lighting, 8k resolution, photorealistic")
    }

    val thinkingTemplates = listOf(
        "วางแผนจัดส่งของ 5 จุดในกรุงเทพฯ ให้เร็วที่สุด",
        "คำนวณความสูงและผลกระทบของภูมิประเทศต่อการขนส่ง",
        "วิเคราะห์จุดตั้งศูนย์กระจายสินค้าที่คุ้มค่าที่สุด"
    )

    val imageTemplates = listOf(
        "ภาพถ่ายดาวเทียมล้ำยุคของโครงข่ายเมืองและเส้นทางเดินรถ",
        "สถาปัตยกรรมศูนย์ควบคุมข้อมูลพิกัดทางภูมิศาสตร์อัจฉริยะ",
        "ภาพทิวทัศน์ 3 มิติแผนที่จำลองเกาะและเส้นทางเดินเรือ"
    )

    Column(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("High Thinking Mode", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Image Generation", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedTabIndex == 0) {
                // THINKING MODE SECTION (gemini-4-argon with ThinkingLevel.HIGH)
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "gemini-4-argon (ThinkingLevel.HIGH)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "โมเดลการใช้เหตุผลเชิงลึกระดับสูงสุด สำหรับการแก้ปัญหาซับซ้อน การคำนวณโลจิสติกส์ และการวางแผนพิกัดเส้นทาง",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(text = "โจทย์ตัวอย่างสำหรับการวิเคราะห์:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(thinkingTemplates) { template ->
                            SuggestionChip(
                                onClick = {
                                    thinkingPrompt = template
                                    aiViewModel.queryThinking(template)
                                },
                                label = { Text(template, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = thinkingPrompt,
                        onValueChange = { thinkingPrompt = it },
                        label = { Text("คำถามหรือโจทย์ที่ต้องการให้ AI วิเคราะห์เชิงลึก") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("thinking_prompt_input"),
                        minLines = 3,
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (thinkingPrompt.isNotBlank()) {
                                aiViewModel.queryThinking(thinkingPrompt)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_thinking_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("เริ่มการประมวลผล Thinking Mode (High)")
                    }
                }

                // Thinking Result
                when (val state = thinkingState) {
                    is ThinkingUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "โมเดล gemini-4-argon กำลังคิดวิเคราะห์ด้วยเหตุผลขั้นสูง...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    is ThinkingUiState.Error -> {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "เกิดข้อผิดพลาด: ${state.message}",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    is ThinkingUiState.Success -> {
                        item {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Psychology,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.tertiary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "ผลการวิเคราะห์ระดับสูง (High Thinking)",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }

                                        if (state.response.thoughtsTokenCount != null) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.tertiaryContainer
                                            ) {
                                                Text(
                                                    text = "Thinking: ${state.response.thoughtsTokenCount} tokens",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = state.response.answer,
                                        style = MaterialTheme.typography.bodyLarge,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                        }
                    }
                    is ThinkingUiState.Idle -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "ใส่โจทย์ที่ต้องการการวางแผนหลายขั้นตอน การคำนวณจุดพิกัด หรือเส้นทางที่ซับซ้อน",
                                    modifier = Modifier.padding(20.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                // IMAGE GENERATION SECTION (gemini-3-pro-image-preview with 1K, 2K, 4K affordance)
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "gemini-3-pro-image-preview (Image Generation)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "สร้างรูปภาพแผนที่ งานออกแบบทางภูมิศาสตร์ หรือภาพทิวทัศน์คุณภาพสูง พร้อมตัวเลือกความละเอียด",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Resolution Selector Affordance (1K, 2K, 4K)
                item {
                    Column {
                        Text(
                            text = "เลือกความละเอียดรูปภาพ (Image Resolution):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("1K", "2K", "4K").forEach { res ->
                                FilterChip(
                                    selected = selectedResolution == res,
                                    onClick = { aiViewModel.setImageResolution(res) },
                                    label = { Text("ความละเอียด $res", fontWeight = FontWeight.SemiBold) },
                                    modifier = Modifier.testTag("res_${res}_chip")
                                )
                            }
                        }
                    }
                }

                // Quick image suggestions
                item {
                    Text(text = "คำสั่งตัวอย่าง:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(imageTemplates) { template ->
                            SuggestionChip(
                                onClick = {
                                    imagePrompt = template
                                    aiViewModel.generateImage(template)
                                },
                                label = { Text(template, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = imagePrompt,
                        onValueChange = { imagePrompt = it },
                        label = { Text("คำบรรยายภาพที่ต้องการสร้าง (Prompt)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("image_prompt_input"),
                        minLines = 3,
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (imagePrompt.isNotBlank()) {
                                aiViewModel.generateImage(imagePrompt)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_image_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("สร้างรูปภาพ ($selectedResolution)")
                    }
                }

                // Image Result
                when (val state = imageGenState) {
                    is ImageGenUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "กำลังสร้างภาพด้วย gemini-3-pro-image-preview (อาจใช้เวลา 30-60 วินาที)...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    is ImageGenUiState.Error -> {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "เกิดข้อผิดพลาดในการสร้างภาพ: ${state.message}",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    is ImageGenUiState.Success -> {
                        item {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp)
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
                                        Text(
                                            text = "รูปภาพที่สร้างเสร็จ ($selectedResolution)",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Image(
                                        bitmap = state.bitmap.asImageBitmap(),
                                        contentDescription = "AI Generated Location Visual",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(320.dp)
                                            .clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                    is ImageGenUiState.Idle -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "ระบุคำบรรยายภาพและเลือกความละเอียด (1K / 2K / 4K) เพื่อสร้างรูปภาพแผนที่และทิวทัศน์คุณภาพสูง",
                                    modifier = Modifier.padding(20.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
