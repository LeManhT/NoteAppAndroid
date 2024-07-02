package com.example.noteappandroid.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappandroid.repository.notes.NoteRepositoryImple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val noteRepositoryImple: NoteRepositoryImple) :
    ViewModel() {
    private val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes = searchQuery.flatMapLatest { query ->
        noteRepositoryImple.notes.map { it ->
            it.filter {
                it.title?.contains(
                    query,
                    ignoreCase = true
                ) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onSearchQueryChanged(query: String) = viewModelScope.launch {
        searchQuery.emit(query)
    }

}