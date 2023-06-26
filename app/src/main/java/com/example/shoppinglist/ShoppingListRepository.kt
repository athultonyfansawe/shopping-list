package com.example.shoppinglist

import androidx.lifecycle.LiveData

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao) {

    val allShoppingList: LiveData<MutableList<ShoppingList>> = shoppingListDao.getAllShoppingList()

    suspend fun insertShoppingItem(shoppingItem: ShoppingList) =
        shoppingListDao.insertShoppingItem(shoppingItem)
}