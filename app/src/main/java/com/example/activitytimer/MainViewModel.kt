package com.example.activitytimer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.activitytimer.data.Entity
import com.example.activitytimer.data.MainDb
import kotlinx.coroutines.launch

class MainViewModel(val dataBase: MainDb) : ViewModel() {
    val itemsList = dataBase.dao.getAllItems()
    val newName = mutableStateOf("")
    var nameEntity: Entity? = null

    fun insertItem() = viewModelScope.launch {
        val nameItem = nameEntity?.copy(name = newName.value)
            ?: Entity(name = newName.value)
        dataBase.dao.insertItem(nameItem)
        nameEntity = null
        newName.value = ""
    }

    fun deleteItem(item: Entity) = viewModelScope.launch {
        dataBase.dao.deleteItem(item)
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