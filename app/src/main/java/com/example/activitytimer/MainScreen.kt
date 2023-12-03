package com.example.activitytimer

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytimer.ui.theme.grayBack
import com.example.activitytimer.ui.theme.light

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)
) {
    val context = LocalContext.current
    val itemsList = mainViewModel.itemsList.collectAsState(initial = emptyList())
    var changeAddConfirmIcon by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val inputNameFocusRequester = remember { FocusRequester() }

    val onDeleteItem = remember { mutableStateOf(fun() {}) }
    val openDeleteItemDialog = remember { mutableStateOf(false) }
    val onClose = remember { mutableStateOf(fun() { openDeleteItemDialog.value = false }) }
    if(openDeleteItemDialog.value) {
        DeleteItemDialog(onDeleteItem.value, onClose.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    changeAddConfirmIcon = false
                    mainViewModel.apply {
                        entity = null
                        newName.value = ""
                    }
                })
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = mainViewModel.newName.value,
                onValueChange = { text ->
                    mainViewModel.newName.value = text
                },
                label = {
                    Text(text = "Activity name...")
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(inputNameFocusRequester),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Black
                ),
            )

            Row(
                modifier = Modifier
                    .padding(top = 10.dp, start = 5.dp, end = 5.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .background(color = grayBack, shape = CircleShape),
                    onClick = {
                        mainViewModel.insertItem()
                        changeAddConfirmIcon = false
                    }) {

                    Icon(
                        imageVector =
                        if(!changeAddConfirmIcon)
                            Icons.Default.Add else Icons.Default.Check,
                        contentDescription = "Add/Check",
                        tint = light
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(
                items = itemsList.value,
                key = { item ->
                    item.hashCode()
                },
            ) { item ->
                ListItem(item, mainViewModel, {
                    mainViewModel.apply {
                        entity = it
                        newName.value = it.name
                    }
                    changeAddConfirmIcon = true
                    inputNameFocusRequester.requestFocus()
                }) {
                    onDeleteItem.value = fun() {
                        mainViewModel.deleteItem(it)
                        Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
                        onDeleteItem.value = fun() {}
                    }
                    openDeleteItemDialog.value = true
                    return@ListItem true
                }
            }
        }
    }
}

@Composable
fun DeleteItemDialog(
    onYes: () -> Unit,
    onClose: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    val dialogWidth = 300.dp
    val dialogHeight = 150.dp

    if(!openDialog.value) {
        onClose()
        return
    }

    Dialog(onDismissRequest = { openDialog.value = false }) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(dialogWidth)
                .height(dialogHeight)
        ) {
            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text(
                        text = "Confirm delete",
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        fontSize = 25.sp,
                        color = Color(0xFFD69677)
                    )

                    IconButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "CloseDialog"
                        )
                    }
                }
                Text(text = "Are you sure want to delete?")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    TextButton(onClick = {
                        openDialog.value = false
                    }, border = BorderStroke(2.dp, Color.Gray)) {
                        Text("CANCEL", color = Color.White)
                    }

                    TextButton(onClick = {
                        openDialog.value = false
                        onYes()
                    }, border = BorderStroke(2.dp, Color.Gray)) {
                        Text("DELETE", color = Color.Red)
                    }

                }
            }
        }
    }
}