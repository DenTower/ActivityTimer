package com.example.activitytimer

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.activitytimer.data.Entity
import com.example.activitytimer.data.MainDb
import com.example.activitytimer.time.TimerService
import kotlinx.coroutines.launch

class MainViewModel(val dataBase: MainDb, application: Application) : AndroidViewModel(application) {
    val itemsList = dataBase.dao.getAllItems()
    val timersCount = dataBase.dao.getTimersCount()
    val newName = mutableStateOf("")
    var entity: Entity? = null
    val app = application

    fun insertItem() = viewModelScope.launch {
        val item = entity?.copy(name = newName.value)
            ?: Entity(name = newName.value, isRunning = false)
        dataBase.dao.insertItem(item)
        entity = null
        newName.value = ""
    }

    fun deleteItem(item: Entity) = viewModelScope.launch {
        dataBase.dao.deleteItem(item)
    }

    fun startTimer(item: Entity) {
        Intent(
            app,
            TimerService::class.java
        ).also {
            it.apply {
                action = TimerService.Actions.START.toString()
                putExtra("ItemId", item.id)
                putExtra("ItemName", item.name)
                putExtra("ItemTime", item.time)
                putExtra("ItemIsRunning", true)
            }
            app.startService(it)
        }
        changeTimerRunning(item)
    }

    fun stopTimer(item: Entity) {
        Intent(
            app,
            TimerService::class.java
        ).also {
            it.apply {
                action = TimerService.Actions.STOP.toString()
                putExtra("ItemId", item.id)
                putExtra("ItemName", item.name)
                putExtra("ItemTime", item.time)
                putExtra("ItemIsRunning", false)
            }
            app.startService(it)
        }
        changeTimerRunning(item)
    }

    private fun changeTimerRunning(item: Entity) = viewModelScope.launch {
        val changedItem = item.copy(
            isRunning = !item.isRunning
        )
        dataBase.dao.insertItem(changedItem)
    }
    companion object{
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras): T {
                val application = (checkNotNull(extras[APPLICATION_KEY]) as App)
                val dataBase = application.database
                return MainViewModel(dataBase, application) as T
            }
        }
    }
}