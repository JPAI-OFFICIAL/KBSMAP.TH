package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AiGroundingScreen
import com.example.ui.screens.AiThinkingAndImageScreen
import com.example.ui.screens.GoogleFormWebViewScreen
import com.example.ui.screens.LocationScreen
import com.example.ui.screens.MapPreviewScreen
import com.example.ui.screens.RegistrationFormScreen
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.LocationViewModel
import com.example.ui.viewmodel.RegistrationViewModel

enum class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    TRACKER("พิกัดสด", Icons.Filled.MyLocation, Icons.Outlined.MyLocation, "tab_tracker"),
    MAP("แผนที่", Icons.Filled.Map, Icons.Outlined.Map, "tab_map"),
    FORM("ลงทะเบียน", Icons.Filled.Assignment, Icons.Outlined.Assignment, "tab_form"),
    GROUNDING("ค้นหา AI", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_grounding"),
    THINKING("Thinking/ภาพ", Icons.Filled.Psychology, Icons.Outlined.Psychology, "tab_thinking")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    locationViewModel: LocationViewModel,
    registrationViewModel: RegistrationViewModel,
    aiViewModel: AiViewModel
) {
    var selectedTab by remember { mutableStateOf(NavTab.TRACKER) }
    var showWebViewForm by remember { mutableStateOf(false) }

    val locationState by locationViewModel.uiState.collectAsStateWithLifecycle()

    // Handle back button on sub-screen or pop to Tracker
    BackHandler(enabled = showWebViewForm || selectedTab != NavTab.TRACKER) {
        if (showWebViewForm) {
            showWebViewForm = false
        } else {
            selectedTab = NavTab.TRACKER
        }
    }

    if (showWebViewForm) {
        GoogleFormWebViewScreen(
            url = registrationViewModel.officialGoogleFormUrl,
            onBack = { showWebViewForm = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (selectedTab) {
                                NavTab.TRACKER -> "Maps Locator • ติดตามพิกัดสด"
                                NavTab.MAP -> "แผนที่เชิงโต้ตอบ (Interactive Map)"
                                NavTab.FORM -> "บริการกรอกแบบฟอร์มลงทะเบียน"
                                NavTab.GROUNDING -> "Gemini Grounding (Maps & Search)"
                                NavTab.THINKING -> "Thinking Mode & Image Studio"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav_bar")
                ) {
                    NavTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

            when (selectedTab) {
                NavTab.TRACKER -> {
                    LocationScreen(
                        viewModel = locationViewModel,
                        onNavigateToRegistration = { lat, lng, addr ->
                            registrationViewModel.populateWithCurrentLocation(lat, lng, addr)
                            selectedTab = NavTab.FORM
                        },
                        modifier = contentModifier
                    )
                }
                NavTab.MAP -> {
                    MapPreviewScreen(
                        currentLocation = locationState.currentPoint,
                        history = locationState.history,
                        onCenterLocation = { locationViewModel.fetchInitialLocation() },
                        modifier = contentModifier
                    )
                }
                NavTab.FORM -> {
                    RegistrationFormScreen(
                        viewModel = registrationViewModel,
                        currentLocation = locationState.currentPoint,
                        onOpenWebViewForm = { showWebViewForm = true },
                        modifier = contentModifier
                    )
                }
                NavTab.GROUNDING -> {
                    AiGroundingScreen(
                        aiViewModel = aiViewModel,
                        currentLocation = locationState.currentPoint,
                        modifier = contentModifier
                    )
                }
                NavTab.THINKING -> {
                    AiThinkingAndImageScreen(
                        aiViewModel = aiViewModel,
                        modifier = contentModifier
                    )
                }
            }
        }
    }
}
