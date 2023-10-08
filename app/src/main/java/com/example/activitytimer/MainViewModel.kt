package com.example.activitytimer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.activitytimer.data.Entity
import com.example.activitytimer.data.MainDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(val dataBase: MainDb) : ViewModel() {
    val itemsList = dataBase.dao.getAllItems()
    val newName = mutableStateOf("")
    var entity: Entity? = null

    val timerState = MutableStateFlow<Timer>(Timer())

    fun insertItem() = viewModelScope.launch {
        val item = entity?.copy(name = newName.value)
            ?: Entity(name = newName.value)
        dataBase.dao.insertItem(item)
        entity = null
        newName.value = ""
    }

    fun updateItemTime() = viewModelScope.launch {
        val item = entity?.copy(
            time = timerState.value.currentTime
        )
        if(item != null) {
            dataBase.dao.insertItem(item)
        }
        entity = null
    }

    fun deleteItem(item: Entity) = viewModelScope.launch {
        dataBase.dao.deleteItem(item)
    }

    fun startTimer(item: Entity) {
        timerState.tryEmit(timerState.value.copy(item.time,true, item.id))
        CoroutineScope(Dispatchers.IO).launch {
            while (timerState.value.isTimerRunning) {
                withContext(Dispatchers.Main) {
                    timerState.tryEmit(
                        timerState.value.copy(currentTime = timerState.value.currentTime + 1L)
                    )
                    entity = item
                    updateItemTime()
                }
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        timerState.tryEmit(timerState.value.copy(isTimerRunning = false))
        timerState.value = Timer()
    }


    companion object{
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras): T {
                val dataBase = (checkNotNull(extras[APPLICATION_KEY]) as App).database
                return MainViewModel(dataBase) as T
            }
        }
    }
}