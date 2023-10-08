package com.example.activitytimer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytimer.ui.theme.grayBack
import com.example.activitytimer.ui.theme.light

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)
) {
    val itemsList = mainViewModel.itemsList.collectAsState(initial = emptyList())
    var changeAddConfirmIcon by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val inputNameFocusRequester = remember { FocusRequester() }

    val openDeleteItemDialog = remember { mutableStateOf(false) }
    if(openDeleteItemDialog.value) { deleteItemDialog() }

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
                ListItem(item, mainViewModel,  {
                    mainViewModel.apply {
                        entity = it
                        newName.value = it.name
                    }
                    changeAddConfirmIcon = true
                    inputNameFocusRequester.requestFocus()
                },
                    {
                        mainViewModel.deleteItem(it)
                    }
                )
            }
        }
    }
}

@Composable
fun deleteItemDialog() {
    val openDialog = remember { mutableStateOf(true) }
    val dialogWidth = 200.dp
    val dialogHeight = 50.dp

    Dialog(onDismissRequest = { openDialog.value = false }) {
        Box(
            Modifier
                .size(dialogWidth, dialogHeight)
                .background(Color.White)
        )
    }
}