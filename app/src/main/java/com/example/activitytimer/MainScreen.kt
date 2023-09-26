package com.example.activitytimer

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytimer.ui.theme.grayBack
import com.example.activitytimer.ui.theme.light

@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)
) {
    val itemsList = mainViewModel.itemsList.collectAsState(initial = emptyList())
    var addIconChange by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                    Text(text = "ActivityName...")
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Black
                )
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
                        addIconChange = false
                    }) {

                    Icon(
                        imageVector =
                        if(addIconChange)
                            Icons.Default.Check else Icons.Default.Add,
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
                ) {  item ->
                ListItem(item, mainViewModel, {
                    mainViewModel.nameEntity = it
                    mainViewModel.newName.value = it.name
                    addIconChange = true
                },
                    {
                        mainViewModel.deleteItem(it)
                    }
                )
            }
        }
    }
}