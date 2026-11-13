package com.me.aliftapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.SessionWithSets
import com.me.aliftapp.data.local.WorkoutSet
import com.me.aliftapp.data.repository.WorkoutRepository
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
}
