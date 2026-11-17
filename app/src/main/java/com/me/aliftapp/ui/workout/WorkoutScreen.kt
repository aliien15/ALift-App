package com.me.aliftapp.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WorkoutScreen(viewModel: ActiveWorkoutViewModel) {
    val workout by viewModel.workoutState.collectAsStateWithLifecycle()

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
                text = "Workout",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)

        // Middle Scrollable Content
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .weight(
                        1f
                    ) // This forces the column to take all remaining space, pushing the bottom
                    // button down
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
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                            CardDefaults.cardColors(
                                                containerColor =
                                                    MaterialTheme.colorScheme
                                                        .background // Contrast for inner boxes
                                            ),
                                    ) {
                                        Row(
                                            modifier =
                                                Modifier.fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(text = "$setCounter", fontWeight = FontWeight.Bold)
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
            ) {
                Button(
                    onClick = {
                        // TODO: discard workout and back to the main menu with confirmation menu
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                ) {
                    Text("-")
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
