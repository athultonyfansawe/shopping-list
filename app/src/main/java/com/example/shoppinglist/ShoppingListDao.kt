package com.example.shoppinglist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shoppingLists ORDER BY id DESC")
    fun getAllShoppingList(): LiveData<MutableList<ShoppingList>>

    @Insert
    suspend fun insertShoppingItem(shoppingItem: ShoppingList)
}