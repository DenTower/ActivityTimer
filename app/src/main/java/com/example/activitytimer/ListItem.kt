package com.example.activitytimer

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.activitytimer.data.Entity
import com.example.activitytimer.time.formatTime
import com.example.activitytimer.ui.theme.grayBack
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListItem(
    item: Entity,
    viewModel: MainViewModel,
    onSwipeToStart: (Entity) -> Unit,
    onRemove: (Entity) -> Boolean
) {

    val context = LocalContext.current
    val timersCount = viewModel.timersCount.collectAsState(initial = 0)

    var show by remember { mutableStateOf(true) }
    val dismissState = rememberDismissState(
        confirmStateChange = {
            when(it) {
                DismissValue.DismissedToEnd -> {
                    show = false
                    show
                }

                DismissValue.DismissedToStart -> {
                    onSwipeToStart(item)
                    false
                }

                else -> false
            }
        }
    )

    AnimatedVisibility(
        show, exit = fadeOut(spring())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(corner = CornerSize(16.dp))

        ) {
            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier,
                background = {
                    DismissBackground(dismissState)
                },
                dismissContent = {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(grayBack),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(10.dp)
                        )
                        Text(
                            formatTime(item.time)
                        )

                        IconButton(
                            onClick = {
                                if(timersCount.value > 0) {
                                    if(item.isRunning) {
                                        viewModel.stopTimer(item)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            R.string.stop_timer_message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } else {
                                    viewModel.startTimer(item)
                                }
                            }) {
                            Icon(
                                imageVector =
                                if(item.isRunning)
                                    Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Start"
                            )
                        }
                    }
                }
            )
        }
    }

    LaunchedEffect(show) {
        if(!show) {
            delay(300)
            show = onRemove(item)
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    val color = when(dismissState.dismissDirection) {
        DismissDirection.StartToEnd -> Color(0xFF940909)
        DismissDirection.EndToStart -> Color(0xFF097016)
        null -> Color.Transparent
    }
    val direction = dismissState.dismissDirection

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if(direction == DismissDirection.StartToEnd) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "DeleteItem"
            )
        }

        Spacer(modifier = Modifier)

        if(direction == DismissDirection.EndToStart) Icon(
            Icons.Default.Edit,
            contentDescription = "EditItem"

        )
    }
}
