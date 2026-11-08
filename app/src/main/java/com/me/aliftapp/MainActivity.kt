package com.me.aliftapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.me.aliftapp.ui.settings.SettingsScreen
import com.me.aliftapp.ui.settings.SettingsViewModel
import com.me.aliftapp.ui.theme.ALiftAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsStore = (application as ALiftApp).appSettingsStore

        setContent {
            ALiftAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val settingsViewModel: SettingsViewModel =
                        viewModel(factory = SettingsViewModel.provideFactory(settingsStore))

                    Box(modifier = Modifier.padding(innerPadding)) {
                        SettingsScreen(viewModel = settingsViewModel)
                    }
                }
            }
        }
    }
}
