package com.me.aliftapp.ui.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.me.aliftapp.data.local.preferences.AppSettings
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun WorkoutScreen(viewModel: ActiveWorkoutViewModel, onOpenSettings: () -> Unit) {
    val workout by viewModel.workoutState.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    // Rest timer state
    val isRestTimerActive by viewModel.isRestTimerActive.collectAsStateWithLifecycle()
    val restTimeRemaining by viewModel.restTimeRemaining.collectAsStateWithLifecycle()

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val groupedExercises =
        remember(workout?.workoutSets) {
            workout?.workoutSets?.groupBy { it.exerciseName }?.entries?.toList().orEmpty()
        }

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
            modifier =
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 56.dp),
            )

            Button(
                modifier = Modifier.align(Alignment.CenterEnd).size(48.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                onClick = onOpenSettings,
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)

        // Middle scrollable content
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Exercise Boxes
            items(items = groupedExercises, key = { (exerciseName, _) -> exerciseName }) {
                (exerciseName, workoutSets) ->
                Card(
                    modifier = Modifier.fillMaxWidth().animateItem(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = exerciseName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                            )

                            Button(
                                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
                                onClick = {
                                    workout?.session?.let { activeSession ->
                                        viewModel.removeExerciseFromSession(
                                            activeSession.sessionId,
                                            exerciseName,
                                        )
                                    }
                                },
                            ) {
                                Text(
                                    text = "-",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        workoutSets.forEachIndexed { index, workoutSet ->
                            key(workoutSet.setId) {
                                val dismissState = rememberSwipeToDismissBoxState()
                                val backgroundColor =
                                    if (workoutSet.isCompleted) completedSetColor
                                    else MaterialTheme.colorScheme.background

                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,
                                    onDismiss = { dismissValue ->
                                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                            viewModel.deleteSetById(workoutSet.setId)
                                        }
                                    },
                                    backgroundContent = {
                                        Box(
                                            modifier =
                                                Modifier.fillMaxSize()
                                                    .clip(CardDefaults.shape)
                                                    .background(
                                                        MaterialTheme.colorScheme.errorContainer
                                                    )
                                                    .padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.CenterEnd,
                                        ) {
                                            Text(
                                                text = "Delete",
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    },
                                ) {
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
                                                    text = "${index + 1}",
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                            Text(text = "--- kg")
                                            Text(text = "--- reps")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // New add exercise button
            item(key = "add_exercise_button") {
                OutlinedButton(
                    onClick = {
                        viewModel.generateDummyWorkoutForUi()

                        /* TODO: Add exercise searching sub-menu */
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                ) {
                    Text("+ Add Exercise")
                }
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
