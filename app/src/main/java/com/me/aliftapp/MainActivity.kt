package com.me.aliftapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.me.aliftapp.data.repository.WorkoutRepository
import com.me.aliftapp.ui.settings.SettingsScreen
import com.me.aliftapp.ui.settings.SettingsViewModel
import com.me.aliftapp.ui.theme.ALiftAppTheme
import com.me.aliftapp.ui.workout.ActiveWorkoutViewModel
import com.me.aliftapp.ui.workout.WorkoutScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsStore = (application as ALiftApp).appSettingsStore
        val database = (application as ALiftApp).database

        setContent {
            val settingsViewModel: SettingsViewModel =
                viewModel(factory = SettingsViewModel.provideFactory(settingsStore))

            val settings by settingsViewModel.settingsState.collectAsStateWithLifecycle()
            var showSettings by rememberSaveable { mutableStateOf(false) }
            BackHandler(enabled = showSettings) { showSettings = false }

            val workoutViewModel: ActiveWorkoutViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                val repository =
                                    WorkoutRepository(
                                        database.sessionDao(),
                                        database.workoutSetDao(),
                                        database.userDao(),
                                    )
                                return ActiveWorkoutViewModel(repository, settingsStore) as T
                            }
                        }
                )

            ALiftAppTheme(darkTheme = settings.isDarkMode) {
                if (showSettings) {
                    SettingsScreen(viewModel = settingsViewModel) { showSettings = false }
                } else {
                    WorkoutScreen(viewModel = workoutViewModel) { showSettings = true }
                }
            }
        }
    }
}
