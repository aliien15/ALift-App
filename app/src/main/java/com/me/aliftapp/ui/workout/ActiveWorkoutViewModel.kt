package com.me.aliftapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.SessionWithSets
import com.me.aliftapp.data.local.WorkoutSet
import com.me.aliftapp.data.local.preferences.AppSettings
import com.me.aliftapp.data.local.preferences.AppSettingsStore
import com.me.aliftapp.data.repository.WorkoutRepository
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActiveWorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val settingsStore: AppSettingsStore,
) : ViewModel() {
    val workoutState: StateFlow<SessionWithSets?> =
        workoutRepository
            .getActiveSessionStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    val settingsState: StateFlow<AppSettings?> =
        settingsStore.settingsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    // Rest timer state
    private val _isRestTimerActive = MutableStateFlow(false)
    val isRestTimerActive: StateFlow<Boolean> = _isRestTimerActive.asStateFlow()

    private val _restTimeRemaining = MutableStateFlow(0)
    val restTimeRemaining: StateFlow<Int> = _restTimeRemaining.asStateFlow()

    private var restTimerJob: Job? = null

    init {
        // TODO: Remove this once the Main Menu is built.
        // The session should be started via navigation arguments, not on init.
        startNewSession("New Workout")
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel() // Cancel any existing countdown if they tap another set quickly

        _restTimeRemaining.value = seconds
        _isRestTimerActive.value = true

        restTimerJob =
            viewModelScope.launch {
                while (_restTimeRemaining.value > 0) {
                    delay(1.seconds)
                    _restTimeRemaining.value -= 1
                }
                _isRestTimerActive.value = false
                // TODO: Add vibration logic here later
            }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        _isRestTimerActive.value = false
        _restTimeRemaining.value = 0
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
                val sessionId = workoutState.value?.session?.sessionId ?: 0L
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
