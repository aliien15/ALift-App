package com.me.aliftapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.me.aliftapp.data.local.ExerciseWithEquipment
import com.me.aliftapp.data.repository.ExerciseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class ExerciseSearchViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val exerciseList: StateFlow<List<ExerciseWithEquipment>> =
        _searchQuery
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    exerciseRepository.getAllExercisesWithEquipment()
                } else {
                    exerciseRepository.getSearchedExercisesWithEquipment(query)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }
}
