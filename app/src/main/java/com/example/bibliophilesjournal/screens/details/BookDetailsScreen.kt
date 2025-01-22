package com.example.bibliophilesjournal.screens.details

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.bibliophilesjournal.components.ReaderAppBar
import com.example.bibliophilesjournal.components.RoundedButton
import com.example.bibliophilesjournal.data.Resource
import com.example.bibliophilesjournal.model.Item
import com.example.bibliophilesjournal.model.MBook
import com.example.bibliophilesjournal.navigation.BibliophileScreens
import com.example.bibliophilesjournal.utils.AppColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BookDetailsScreen(navController: NavController,
                      bookId: String,
                      viewModel: DetailsViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        ReaderAppBar(title = "Book Details",
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            showProfile = false,
            navController = navController){
            navController.navigate(BibliophileScreens.SearchScreen.name)
        }
    },containerColor = AppColor.b0) {

        Surface(modifier = Modifier
            .padding(it)
            .fillMaxSize(),color = AppColor.b0) {
            Column(modifier = Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {

                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()){
                    value = viewModel.getBookInfo(bookId)
                }.value

                if (bookInfo.data == null) {
                    Column(
                        modifier = Modifier.padding(end = 2.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            color= AppColor.b3,
                            trackColor= AppColor.b1
                        )
                        Text(text = "Loading...")
                    }

                }else{
                    ShowBookDetails(bookInfo, navController)
                }
                //  Log.d("Deets", "BookDetailsScreen: ${bookInfo.data.toString()}")
            }
        }
    }
}

@Composable
fun ShowBookDetails(bookInfo: Resource<Item>,
                    navController: NavController) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id
    val secureUrl = bookData?.imageLinks?.thumbnail?.replace("http://", "https://")


    Card(modifier = Modifier.padding(12.dp),
        shape = CircleShape, elevation = CardDefaults.cardElevation(4.dp)) {
        Image(painter = rememberAsyncImagePainter(secureUrl),
            contentDescription = "Book Image",
            modifier = Modifier
                .size(90.dp)
                .padding(1.dp)
        )
    }

    Text(text = bookData?.title.toString(),
        style = MaterialTheme.typography.headlineLarge,
        color = AppColor.b3,
        overflow = TextOverflow.Ellipsis,
        maxLines = 19)

    Text(text = "Authors: ${bookData?.authors.toString()}",
            style = MaterialTheme.typography.bodyMedium,
        color = AppColor.b3,)
    Text(text = "Page Count: ${bookData?.pageCount.toString()}",
        style = MaterialTheme.typography.bodySmall,
        color = AppColor.b3,)
    Text(text = "Categories: ${bookData?.categories.toString()}",
        style = MaterialTheme.typography.bodySmall,
        color = AppColor.b3,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis)
    Text(text = "Published: ${bookData?.publishedDate.toString()}",
        style = MaterialTheme.typography.bodyMedium,
        color = AppColor.b3)

    Spacer(modifier = Modifier.height(5.dp))

    val cleanDescription = HtmlCompat.fromHtml(bookData!!.description,
        HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    val localDims = LocalContext.current.resources.displayMetrics
    Surface(modifier = Modifier
        .height(localDims.heightPixels.dp.times(0.14f))
        .padding(10.dp),
        shape = RoundedCornerShape(29.dp),
        color = AppColor.b0,
        border = BorderStroke(2.8.dp, AppColor.b3)) {

        LazyColumn(modifier = Modifier.padding(12.dp)) {
            item {

                Text(text = cleanDescription,
                    color = AppColor.b3
                )
            }
        }
    }

    //Buttons
    Row(modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround) {
        RoundedButton(label = "Save"){
            //save this book to the firestore database
            val book = MBook(
                title = bookData.title,
                authors = bookData.authors.toString(),
                description = bookData.description,
                categories = bookData.categories.toString(),
                notes = "",
                photoUrl = bookData.imageLinks.thumbnail,
                publishedDate = bookData.publishedDate,
                pageCount = bookData.pageCount.toString(),
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString())

            saveToFirebase(book, navController = navController)

        }
        Spacer(modifier = Modifier.width(25.dp))
        RoundedButton(label = "Cancel"){
            navController.popBackStack()
        }
    }
}



fun saveToFirebase(book: MBook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()){
        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.popBackStack()
                        }


                    }.addOnFailureListener {
                        Log.w("Error", "SaveToFirebase:  Error updating doc",it )
                    }
            }

    }else{

    }
}
