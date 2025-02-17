package com.example.bibliophilesjournal.screens.update


import androidx.compose.material3.AlertDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.bibliophilesjournal.components.RatingBar
import com.example.bibliophilesjournal.components.ReaderAppBar
import com.example.bibliophilesjournal.components.RoundedButton
import com.example.bibliophilesjournal.components.showToast
import com.example.bibliophilesjournal.data.DataOrException
import com.example.bibliophilesjournal.model.MBook
import com.example.bibliophilesjournal.navigation.BibliophileScreens
import com.example.bibliophilesjournal.screens.home.HomeScreenViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.bibliophilesjournal.R
import com.example.bibliophilesjournal.components.InputField
import com.example.bibliophilesjournal.model.Book
import com.example.bibliophilesjournal.utils.AppColor
import com.example.bibliophilesjournal.utils.formatDate
import com.google.firebase.Timestamp


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookUpdateScreen(navController: NavHostController,
                     bookItemId: String,
                     viewModel: HomeScreenViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        ReaderAppBar(title = "Update Book",
            icon = ImageVector.vectorResource(id = R.drawable.bbj),
            showProfile = false,
            navController = navController){
            navController.popBackStack()
        }
    },
        containerColor = AppColor.b0) {

        val bookInfo = produceState<DataOrException<List<MBook>,
                Boolean,
                Exception>>(initialValue = DataOrException(data = emptyList(),
            true, Exception(""))){
            value = viewModel.data.value
        }.value

        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(it),
            color = AppColor.b0) {
            Column(
                modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = CenterHorizontally
            ) {
                Log.d("INFO", "BookUpdateScreen: ${viewModel.data.value.data.toString()}")
                if (bookInfo.loading == true) {
                    LinearProgressIndicator(
                        color= AppColor.b3,
                        trackColor= AppColor.b1
                    )
                    bookInfo.loading = false

                }else {
                    Surface(modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth(),
                        shape = CircleShape,
                        shadowElevation = 0.dp,
                        color = AppColor.b1) {
                        ShowBookUpdate(bookInfo = viewModel.data.value,
                            bookItemId = bookItemId,navController)

                    }

                    ShowSimpleForm(book = viewModel.data.value.data?.first { mBook ->
                        mBook.googleBookId == bookItemId
                    }!!, navController)

                }


            }
        }
    }
}


@ExperimentalComposeUiApi
@Composable
fun ShowSimpleForm(book: MBook,
                   navController: NavHostController) {
    val context = LocalContext.current

    val notesText = remember {
        mutableStateOf("")
    }
    val isStartedReading = remember {
        mutableStateOf(false)
    }

    val isFinishedReading = remember {
        mutableStateOf(false)

    }
    val ratingVal = remember {
        mutableStateOf(0)
    }
    SimpleForm(defaultValue = if (book.notes.toString().isNotEmpty()) book.notes.toString()
    else "apple"){ note ->
        notesText.value = note


    }

    Row(modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start) {
        TextButton(onClick = { isStartedReading.value = true},
            enabled = book.startedReading == null) {
            if (book.startedReading == null) {
                if (!isStartedReading.value) {
                    Text(text = "Start Reading",
                        fontSize = 15.sp)
                } else {
                    Text(
                        text = "Started Reading!",
                        modifier = Modifier.alpha(0.6f),
                        color = AppColor.b3,
                        fontSize = 15.sp
                    )
                }

            }else{
                Text("Started on: ${formatDate(book.startedReading!!)}")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = { isFinishedReading.value = true },
            enabled = book.finishedReading == null) {
            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Mark as Read",
                        fontSize = 15.sp)
                }else {
                    Text(text = "Finished Reading!",
                        fontSize = 15.sp,
                        color = AppColor.b3)
                }
            }else {
                Text(text = "Finished on: ${formatDate(book.finishedReading!!)}",
                    fontSize = 15.sp)
            }

        }

    }
    Spacer(modifier = Modifier.height(10.dp))
    Text(text = "Rating", modifier = Modifier.padding(bottom = 3.dp),
        fontSize = 20.sp)
    book.rating?.toInt().let {
        RatingBar(rating = it!!){ rating->
            ratingVal.value = rating
            Log.d("TAG", "ShowSimpleForm: ${ratingVal.value}")
        }

    }

    Spacer(modifier = Modifier.padding(bottom = 15.dp))
    Row {

        val changedNotes = book.notes != notesText.value
        val changedRating = book.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp = if (isFinishedReading.value) Timestamp.now() else book.finishedReading
        val isStartedTimeStamp = if (isStartedReading.value) Timestamp.now() else book.startedReading

        val bookUpdate = changedNotes || changedRating || isStartedReading.value || isFinishedReading.value

        val bookToUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to notesText.value).toMap()

        RoundedButton(label = "Update"){
            if (bookUpdate) {
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .update(bookToUpdate)
                    .addOnCompleteListener {
                        showToast(context, "Book Updated Successfully!")
                        navController.navigate(BibliophileScreens.BookHomeScreen.name)

                        // Log.d("Update", "ShowSimpleForm: ${task.result.toString()}")

                    }.addOnFailureListener{
                        Log.w("Error", "Error updating document" , it)
                    }
            }




        }
        Spacer(modifier = Modifier.width(100.dp))
        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(message = stringResource(id = R.string.sure) + "\n" +
                    stringResource(id = R.string.action), openDialog){
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            openDialog.value = false
                            /*
                             Don't popBackStack() if we want the immediate recomposition
                             of the MainScreen UI, instead navigate to the mainScreen!
                            */

                            navController.navigate(BibliophileScreens.BookHomeScreen.name)
                        }
                    }

            }
        }

        RoundedButton("Delete"){
            openDialog.value = true
        }

    }




}

@Composable
fun ShowAlertDialog(
    message: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = "Delete Book") },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(onClick = {
                    onYesPressed.invoke()
                    openDialog.value = false
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(text = "No")
                }
            }
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book!",
    onSearch: (String) -> Unit
){
    Column {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value) { textFieldValue.value.trim().isNotEmpty() }

        InputField(
            modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(6.dp)
                .background(AppColor.b0)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            valueState = textFieldValue,
            labelId = "Enter Your thoughts",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid)return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            })

    }

}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>,
        Boolean, Exception>, bookItemId: String,navController: NavController) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        if (bookInfo.data != null) {
            Column(modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CardListItem(navController,book = bookInfo.data!!.first{mBook ->
                    mBook.googleBookId == bookItemId

                }, onPressDetails = {})

            }
        }

    }



}

@Composable
fun CardListItem(navController: NavController, book: MBook,
                 onPressDetails: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(
                start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable {
            },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(AppColor.b1)
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            val secureUrl = book.photoUrl?.replace("http://", "https://")
            Image(
                painter = rememberAsyncImagePainter(secureUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 20.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = AppColor.b3
                )

                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 2.dp,
                        bottom = 0.dp
                    ),
                    color = AppColor.b3
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 0.dp,
                        bottom = 8.dp
                    ),
                    fontWeight = FontWeight.SemiBold,
                    color = AppColor.b3
                )
            }
        }
    }
}