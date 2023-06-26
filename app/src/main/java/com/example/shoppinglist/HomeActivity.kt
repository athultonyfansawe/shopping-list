package com.example.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: ShoppingViewModel
    private lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = ViewModelProvider(this)[ShoppingViewModel::class.java]

        val addButton = findViewById<Button>(R.id.button_addItem)
        val editText = findViewById<EditText>(R.id.editText)
        val listView = findViewById<ListView>(R.id.list_view_shopping)

        // Create an empty adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        // Add button
        addButton.setOnClickListener {
            val item = editText.text.toString()

            if (item.isNotEmpty()) {
                viewModel.insert(ShoppingList(itemName = item))
                Log.d("HomeActivitys", "Added item: $item")

                adapter.add(item) // Add item to the adapter
                adapter.notifyDataSetChanged() // Notify the adapter that data has changed
                editText.text.clear() // Clear the text input once a shopping item is added
            }
        }

        // Observe the data from the ViewModel and update the adapter
        viewModel.allshoppingItems.observe(this) { shoppingList ->
            adapter.clear()
            shoppingList.forEach { item ->
                adapter.add(item.itemName)
            }
            adapter.notifyDataSetChanged()
        }
    }
}