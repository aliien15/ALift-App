package com.me.aliftapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.SessionWithSets
import com.me.aliftapp.data.local.WorkoutSet
import com.me.aliftapp.data.repository.WorkoutRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActiveWorkoutViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {
    val workoutState: StateFlow<SessionWithSets?> =
        workoutRepository
            .getActiveSessionStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    init {
        // TODO: Remove this once the Main Menu is built.
        // The session should be started via navigation arguments, not on init.
        startNewSession("New Workout")
    }

    fun startNewSession(name: String) {
        viewModelScope.launch { workoutRepository.startNewSession(name) }
    }

    fun addSetToSession(sessionId: Long, exerciseName: String) {
        viewModelScope.launch { workoutRepository.addSetToSession(sessionId, exerciseName) }
    }

    fun updateSet(set: WorkoutSet) {
        viewModelScope.launch { workoutRepository.updateSet(set) }
    }

    fun finishSession(session: Session) {
        viewModelScope.launch { workoutRepository.finishSession(session) }
    }

    fun discardSession(session: Session) {
        viewModelScope.launch { workoutRepository.discardSession(session) }
    }

    fun generateDummyWorkoutForUi() {
        viewModelScope.launch {
            try {
                val sessionId = workoutRepository.startNewSession("Dummy Chest Day")
                workoutRepository.addSetToSession(sessionId, "Bench Press")
                workoutRepository.addSetToSession(sessionId, "Bench Press")
                workoutRepository.addSetToSession(sessionId, "Squat")
            } catch (e: Exception) {
                if (e is CancellationException) {
                    ensureActive()
                }
                e.printStackTrace()
            }
        }
    }
}
