package com.example.bibliophilesjournal.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.bibliophilesjournal.components.ReaderAppBar
import com.example.bibliophilesjournal.model.MBook
import com.example.bibliophilesjournal.screens.home.HomeScreenViewModel
import com.example.bibliophilesjournal.utils.AppColor
import com.example.bibliophilesjournal.utils.formatDate
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun BookStatsScreen(navController: NavController,
                      viewModel: HomeScreenViewModel = hiltViewModel()) {

     var books: List<MBook>
     val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            ReaderAppBar(title = "Book Stats",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                showProfile = false,
                navController = navController){
                navController.popBackStack()
            }

        },
            ) {
        Surface(modifier = Modifier.padding(it),
            color = AppColor.b0) {
            //only show books by this user that have been read
            books = if (!viewModel.data.value.data.isNullOrEmpty()) {
                viewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            }else {
                emptyList()

            }
            Column {
                Row {
                    Box(modifier = Modifier
                        .size(45.dp)
                        .padding(10.dp)) {
                        Icon(imageVector = Icons.Sharp.Person,
                            contentDescription = "icon",
                            tint = AppColor.b3)
                    }
                    //paul @ me.com
                    Text(text = "Hi, ${
                        currentUser?.email.toString().split("@")[0].uppercase(Locale.getDefault())
                    }",
                        color = AppColor.b3,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 20.sp
                    )

                }
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(5.dp),
                    colors = CardDefaults.cardColors(AppColor.b1)
                    ) {
                    val readBooksList: List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()) {
                        books.filter { mBook ->
                            (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                        }

                    }else {
                        emptyList()
                    }

                    val readingBooks = books.filter { mBook ->
                        (mBook.startedReading != null && mBook.finishedReading == null)
                    }
                    
                    Column(modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                          horizontalAlignment = Alignment.Start) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.headlineLarge,
                            color = AppColor.b3)
                        HorizontalDivider(modifier = Modifier.height(1.5.dp))
                        Text(text = "You're reading: ${readingBooks.size} books",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AppColor.b3
                            )
                        Text(text = "You've read: ${readBooksList.size} books", style = MaterialTheme.typography.headlineMedium,
                            color = AppColor.b3
                        )
                        
                    }

                }

                if (viewModel.data.value.loading == true) {
                    LinearProgressIndicator()
                }else {
                    HorizontalDivider()
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                              contentPadding = PaddingValues(16.dp)){
                        //filter books by finished ones
                        val readBooks: List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()){
                            viewModel.data.value.data!!.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                            }
                        }else {
                            emptyList()

                        }
                        items(items = readBooks) {book ->
                            BookRowStats(book =book )
                        }

                    }
                }

            }
        }

    }

}




@Composable
fun BookRowStats(
    book: MBook) {
    Card(modifier = Modifier
        .clickable {
            //navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
        }
        .fillMaxWidth()
        .height(100.dp)
        .padding(3.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(AppColor.b1),
        elevation = CardDefaults.cardElevation(7.dp)) {
        Row(modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Top) {

            val imageUrl: String = if(book.photoUrl.toString().isEmpty())
                "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
            else {
                book.photoUrl.toString()
            }
            val secureUrl = imageUrl.replace("http://", "https://")
            Image(
                painter = rememberAsyncImagePainter(secureUrl),
                contentDescription = "book image",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp),
                 )

            Column {
                
                Row(horizontalArrangement = Arrangement.SpaceBetween) {

                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis,
                        color = AppColor.b3)
                    if (book.rating!! >= 4) {
                        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                         Icon(imageVector = Icons.Default.ThumbUp,
                             contentDescription = "Thumbs up",
                              tint = AppColor.b3)
                    }else {
                        Box{}
                    }
                }
                Text(text =  "Author: ${book.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColor.b3)

                Text(text =  "Started: ${formatDate(book.startedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelSmall,color = AppColor.b3)

                Text(text =  "Finished ${formatDate(book.finishedReading!!)}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelSmall,color = AppColor.b3)


            }

        }

    }
}
