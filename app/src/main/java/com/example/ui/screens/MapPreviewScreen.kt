package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.LocationPoint

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapPreviewScreen(
    currentLocation: LocationPoint?,
    history: List<LocationPoint>,
    onCenterLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mapType by remember { mutableStateOf("roadmap") } // roadmap, satellite, hybrid, terrain
    var zoomLevel by remember { mutableIntStateOf(16) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val lat = currentLocation?.latitude ?: 13.7563
    val lng = currentLocation?.longitude ?: 100.5018

    // Generate lightweight Leaflet / OpenStreetMap / Google Maps hybrid viewer HTML
    val htmlContent = remember(lat, lng, zoomLevel, mapType) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html, #map { margin: 0; padding: 0; height: 100%; width: 100%; background: #0f172a; }
                .pulse-icon {
                    width: 20px;
                    height: 20px;
                    background: #2563eb;
                    border: 3px solid #ffffff;
                    border-radius: 50%;
                    box-shadow: 0 0 12px rgba(37,99,235,0.8);
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', { zoomControl: false }).setView([$lat, $lng], $zoomLevel);
                
                var tileUrl = 'https://tile.openstreetmap.org/{z}/{x}/{y}.png';
                if ('$mapType' === 'satellite') {
                    tileUrl = 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}';
                } else if ('$mapType' === 'terrain') {
                    tileUrl = 'https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png';
                }
                
                L.tileLayer(tileUrl, {
                    maxZoom: 19,
                    attribution: 'Map Data &copy; OpenStreetMap & Google Maps SDK'
                }).addTo(map);

                var userIcon = L.divIcon({
                    className: 'pulse-icon',
                    iconSize: [20, 20],
                    iconAnchor: [10, 10]
                });

                var marker = L.marker([$lat, $lng], { icon: userIcon }).addTo(map);
                marker.bindPopup("<b>ตำแหน่งของคุณ</b><br/>Lat: $lat<br/>Lng: $lng").openPopup();

                var circle = L.circle([$lat, $lng], {
                    color: '#3b82f6',
                    fillColor: '#60a5fa',
                    fillOpacity: 0.15,
                    radius: ${currentLocation?.accuracy ?: 25f}
                }).addTo(map);
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Embedded Map View
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://maps.google.com", htmlContent, "text/html", "UTF-8", null)
                    webViewInstance = this
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://maps.google.com", htmlContent, "text/html", "UTF-8", null)
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top Layer Selection Controls
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Map Layers",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                FilterChip(
                    selected = mapType == "roadmap",
                    onClick = { mapType = "roadmap" },
                    label = { Text("แผนที่ถนน", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = mapType == "satellite",
                    onClick = { mapType = "satellite" },
                    label = { Text("ดาวเทียม", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = mapType == "terrain",
                    onClick = { mapType = "terrain" },
                    label = { Text("ภูมิประเทศ", fontSize = 12.sp) }
                )
            }
        }

        // Floating Zoom and Center Controls
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = { if (zoomLevel < 19) zoomLevel++ },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
            }

            FilledTonalIconButton(
                onClick = { if (zoomLevel > 3) zoomLevel-- },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
            }

            FloatingActionButton(
                onClick = {
                    onCenterLocation()
                    webViewInstance?.reload()
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("center_map_button"),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location", tint = Color.White)
            }
        }

        // Bottom Info Card
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "พิกัดแผนที่แบบเรียลไทม์",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${String.format("%.6f", lat)}, ${String.format("%.6f", lng)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentLocation?.address ?: "พิกัดพร้อมแสดงผลบนแผนที่",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "ซูม: ${zoomLevel}x",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
