package com.example.shoppinglist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.DialogAddItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: ShoppingViewModel
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var shoppingRepo: ShoppingListRepository
    private lateinit var shoppingDao: ShoppingListDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.title = "Shopping List"

        val database = ShoppingListDb.getInstance(this)

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
        // Add item and category
        addButton.setOnClickListener {
            // Open a custom alert dialog to add items
            val dialog = LayoutInflater.from(this@HomeActivity).inflate(R.layout.dialog_add_item, null, false)
            val binder = DialogAddItemBinding.bind(dialog)
            val builder = MaterialAlertDialogBuilder(this@HomeActivity)
            builder.setView(dialog)
                .setTitle("Add Item")
                .setPositiveButton("Add", null)

            val alertDialog = builder.show() // Show the dialog

            // Set a custom positive button click listener to handle the add action
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val itemName = binder.etItemName.text.toString()
                val category = binder.etCategory.text.toString().lowercase() // category is always lowercase

                // Check if both fields are not empty
                if (itemName.isNotBlank() && category.isNotBlank()) {
                    viewModel.insert(ShoppingList(itemName = itemName, category = category))
                    adapter.notifyDataSetChanged()
                    alertDialog.dismiss()
                } else {
                    // Error message if any of the fields is empty
                    Toast.makeText(this@HomeActivity, "Both fields are required.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


