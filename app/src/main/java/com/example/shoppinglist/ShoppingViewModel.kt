package com.example.shoppinglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ShoppingViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ShoppingListRepository
    var allShoppingItems: LiveData<MutableList<ShoppingList>>

    init {
        val shoppingDao = ShoppingListDb.getInstance(application)?.shoppingListDao()
        repository = shoppingDao?.let { ShoppingListRepository(it) }!!
        allShoppingItems = repository.allShoppingList
    }

    fun insert(shoppingItem: ShoppingList) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    fun delete(shoppingItem: ShoppingList) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    fun update(shoppingItem: ShoppingList) = viewModelScope.launch {
        repository.updateShoppingItem(shoppingItem)
    }
}