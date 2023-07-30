package com.example.shoppinglist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shoppingLists ORDER BY id DESC")
    fun getAllShoppingList(): LiveData<MutableList<ShoppingList>>

    @Insert
    suspend fun insertShoppingItem(shoppingItem: ShoppingList)

    @Delete
    suspend fun deleteShoppingItem(shoppingItem: ShoppingList)

    @Update
    suspend fun updateShoppingItem(shoppingItem: ShoppingList)
}