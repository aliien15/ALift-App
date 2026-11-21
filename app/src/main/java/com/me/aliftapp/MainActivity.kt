package com.me.aliftapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.WORKOUT,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                ) {
                    composable(Routes.WORKOUT) {
                        WorkoutScreen(
                            viewModel = workoutViewModel,
                            onOpenSettings = {
                                navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                            },
                        )
                    }
                    composable(Routes.SETTINGS) {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }

    // Basically an ID for each menu, so Nav can handle the navigation between the different menus
    private object Routes {
        const val WORKOUT = "workout"
        const val SETTINGS = "settings"
    }
}
