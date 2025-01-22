package com.example.bibliophilesjournal.repository


import android.util.Log
import com.example.bibliophilesjournal.data.Resource
import com.example.bibliophilesjournal.model.Item
import com.example.bibliophilesjournal.network.BooksApi
import javax.inject.Inject
import kotlin.Exception

class BookRepository @Inject constructor(private val api: BooksApi) {
     suspend fun getBooks(searchQuery: String): Resource<List<Item>> {
          return try {
               Resource.Loading(data = true)
               val itemList = api.getAllBooks(searchQuery).items
               Resource.Loading(data = false)
               Resource.Success(data = itemList)
          } catch (exception: Exception) {
               // Log the exception
               Log.e("BookRepository", "Error fetching books: ${exception.message}")
               Resource.Error(message = exception.message.toString())
          }
     }

     suspend fun getBookInfo(bookId: String): Resource<Item> {
          return try {
               Resource.Loading(data = true)
               val response = api.getBookInfo(bookId)
               Resource.Loading(data = false)
               Resource.Success(data = response)
          } catch (exception: Exception) {
               // Log the exception
               Log.e("BookRepository", "Error fetching book info: ${exception.message}")
               Resource.Error(message = "An error occurred: ${exception.message}")
          }
     }
}