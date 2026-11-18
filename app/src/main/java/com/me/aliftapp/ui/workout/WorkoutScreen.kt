package com.me.aliftapp.ui.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.me.aliftapp.data.local.preferences.AppSettings
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun WorkoutScreen(viewModel: ActiveWorkoutViewModel) {
    val workout by viewModel.workoutState.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    // Rest timer state
    val isRestTimerActive by viewModel.isRestTimerActive.collectAsStateWithLifecycle()
    val restTimeRemaining by viewModel.restTimeRemaining.collectAsStateWithLifecycle()

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val completedSetColor =
        if (isSystemInDarkTheme()) {
            Color(0xFF2E7D32)
        } else {
            Color(0xFFC8E6C9)
        }

    LaunchedEffect(Unit) {
        do {
            delay(1.seconds)
            currentTime = System.currentTimeMillis()
        } while (true)
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Top Header
        Box(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .statusBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = workout?.session?.name ?: "New Workout",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)

        // Middle Scrollable Content
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Button made for testing, not meant to go on production
            Button(
                onClick = { viewModel.generateDummyWorkoutForUi() },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            ) {
                Text("Add Dummy Exercises (Test)")
            }

            // Exercise Boxes
            workout
                ?.workoutSets
                ?.groupBy { it.exerciseName }
                ?.let { groupedSets ->
                    for ((exerciseName, workoutSets) in groupedSets) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = exerciseName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                                )

                                var setCounter = 1
                                for (workoutSet in workoutSets) {
                                    val backgroundColor =
                                        if (workoutSet.isCompleted) completedSetColor
                                        else MaterialTheme.colorScheme.background
                                    Card(
                                        modifier =
                                            Modifier.fillMaxWidth().clickable {
                                                val isNowComplete = !workoutSet.isCompleted
                                                val updatedSet =
                                                    workoutSet.copy(isCompleted = isNowComplete)
                                                viewModel.updateSet(updatedSet)

                                                val autoStart =
                                                    settings?.autoStartRestTimer
                                                        ?: AppSettings.DEFAULT_AUTO_START_REST_TIMER
                                                val timerSeconds =
                                                    settings?.restTimerSeconds
                                                        ?: AppSettings.DEFAULT_REST_TIMER_SECONDS

                                                if (isNowComplete && autoStart) {
                                                    viewModel.startRestTimer(timerSeconds)
                                                }
                                            },
                                        colors =
                                            CardDefaults.cardColors(
                                                containerColor = backgroundColor
                                            ),
                                    ) {
                                        Row(
                                            modifier =
                                                Modifier.fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            if (workoutSet.isCompleted) {
                                                Text(text = "✓", fontWeight = FontWeight.Bold)
                                            } else {
                                                Text(
                                                    text = "$setCounter",
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                            Text(text = "--- kg")
                                            Text(text = "--- reps")
                                        }
                                    }
                                    setCounter++
                                }
                            }
                        }
                    }
                }

            // New Add Exercise Button
            OutlinedButton(
                onClick = { /* TODO: Add exercise searching sub-menu */ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("+ Add Exercise")
            }
        }

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)

        // Fixed Bottom Area
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .navigationBarsPadding()
                    .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        workout?.session?.let { activeSession ->
                            viewModel.discardSession(activeSession)
                        }
                        // TODO: back to the main menu with confirmation menu
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                ) {
                    Text("-")
                }

                Surface(
                    shape = ButtonDefaults.shape,
                    color =
                        if (isRestTimerActive) completedSetColor
                        else MaterialTheme.colorScheme.primary,
                    contentColor =
                        if (isRestTimerActive) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onPrimary,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    val timerText =
                        if (isRestTimerActive) {
                            val m = (restTimeRemaining / 60).toString().padStart(2, '0')
                            val s = (restTimeRemaining % 60).toString().padStart(2, '0')
                            "Rest $m:$s"
                        } else {
                            formatSessionTime(
                                workout?.session?.startTimestamp ?: currentTime,
                                currentTime,
                            )
                        }

                    Text(
                        text = timerText,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Button(
                    onClick = {
                        workout?.session?.let { activeSession ->
                            viewModel.finishSession(activeSession)
                        }
                        // TODO: Confirmation screen with workout finishing screen
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                ) {
                    Text("✓")
                }
            }
        }
    }
}

internal fun formatSessionTime(startTime: Long, currentTime: Long): String {
    require(startTime <= currentTime) {
        "Contract Violation: startTime ($startTime) cannot be greater than currentTime ($currentTime)"
    }

    val currentMillis = currentTime - startTime
    val totalSeconds = currentMillis / 1000

    val seconds = (totalSeconds % 60).toString().padStart(2, '0')
    val minutes = ((totalSeconds / 60) % 60).toString().padStart(2, '0')
    val hours = (totalSeconds / 3600).toString().padStart(2, '0')

    return if (hours != "00") {
        "$hours:$minutes:$seconds"
    } else {
        "$minutes:$seconds"
    }
}
