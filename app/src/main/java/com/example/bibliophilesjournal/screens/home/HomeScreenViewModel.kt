package com.example.bibliophilesjournal.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliophilesjournal.data.DataOrException
import com.example.bibliophilesjournal.model.MBook
import com.example.bibliophilesjournal.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: FireRepository): ViewModel() {
    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>> =
        mutableStateOf(DataOrException(listOf(), true, Exception("")))

    init {
        getAllBooksFromDatabase()
    }

    private fun getAllBooksFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            try {
                data.value = repository.getAllBooksFromDatabase()
                data.value.loading = false
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error fetching books: ${e.message}")
                data.value.e = e
                data.value.loading = false
            }
            Log.d("GET", "Books fetched: ${data.value.data?.toList()}")
        }
    }
}
