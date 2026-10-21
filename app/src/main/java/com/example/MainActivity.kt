package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.LocationViewModel
import com.example.ui.viewmodel.RegistrationViewModel
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {
    private val locationViewModel: LocationViewModel by viewModels()
    private val registrationViewModel: RegistrationViewModel by viewModels()
    private val aiViewModel: AiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                AppNavigation(
                    locationViewModel = locationViewModel,
                    registrationViewModel = registrationViewModel,
                    aiViewModel = aiViewModel
                )
            }
        }
    }
}
