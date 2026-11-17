package com.me.aliftapp.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WorkoutScreen(viewModel: ActiveWorkoutViewModel) {
    val workout by viewModel.workoutState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
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

        Column(
            modifier =
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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

            workout
                ?.workoutSets
                ?.groupBy { it.exerciseName }
                ?.let { groupedSets ->
                    for ((exerciseName, workoutSets) in groupedSets) {
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = exerciseName)
                            }

                            var setCounter = 1
                            for (workoutSet in workoutSets) {
                                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier =
                                            Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(text = "#${setCounter}: TODO")
                                    }
                                }
                                setCounter++
                            }
                        }
                    }
                }

            Button(
                onClick = {
                    workout?.session?.let { activeSession ->
                        viewModel.finishSession(activeSession)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            ) {
                Text("Finish Workout")
            }
        }
    }
}
