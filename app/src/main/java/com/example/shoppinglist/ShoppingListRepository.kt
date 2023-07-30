package com.example.shoppinglist

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao) {

    val allShoppingList: LiveData<MutableList<ShoppingList>> = shoppingListDao.getAllShoppingList()
    suspend fun insertShoppingItem(shoppingItem: ShoppingList) = withContext(Dispatchers.IO) {
        shoppingListDao.insertShoppingItem(shoppingItem)
    }

    suspend fun deleteShoppingItem(shoppingItem: ShoppingList) = withContext(Dispatchers.IO) {
        shoppingListDao.deleteShoppingItem(shoppingItem)
    }

    suspend fun updateShoppingItem(shoppingItem: ShoppingList) = withContext(Dispatchers.IO) {
        shoppingListDao.updateShoppingItem(shoppingItem)
    }
}