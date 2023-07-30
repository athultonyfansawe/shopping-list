package com.example.shoppinglist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShoppingList::class], version = 2, exportSchema = false)
abstract class ShoppingListDb : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao


    companion object {
        private var INSTANCE: ShoppingListDb? = null

        fun getInstance(context: Context): ShoppingListDb? {
            if (INSTANCE == null) {
                synchronized(ShoppingListDb::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        ShoppingListDb::class.java,
                        "shopping_list_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                }
            }
            return INSTANCE
        }
    }
}