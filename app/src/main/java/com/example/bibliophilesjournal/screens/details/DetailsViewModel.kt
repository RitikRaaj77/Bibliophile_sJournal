package com.example.bibliophilesjournal.screens.details

import androidx.lifecycle.ViewModel
import com.example.bibliophilesjournal.data.Resource
import com.example.bibliophilesjournal.model.Item
import com.example.bibliophilesjournal.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: BookRepository)
    : ViewModel(){
    suspend fun getBookInfo(bookId: String): Resource<Item> {
        return repository.getBookInfo(bookId)
    }
}
