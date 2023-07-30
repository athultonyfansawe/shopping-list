package com.example.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
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

        val listView = findViewById<RecyclerView>(R.id.list_view_shopping)
        val addButton = findViewById<ExtendedFloatingActionButton>(R.id.btn_add)
        val search = findViewById<SearchView>(R.id.searchView)
        val noDataTv = findViewById<TextView>(R.id.tv_no_items)

        if (database != null) {
            shoppingDao = database.shoppingListDao()
            shoppingRepo = ShoppingListRepository(shoppingDao)

            // Create an empty adapter
            adapter = ShoppingListAdapter(this, shoppingRepo)
            listView.setHasFixedSize(true)
            listView.setItemViewCacheSize(20)
            listView.layoutManager = LinearLayoutManager(this) // items will be shown vertically by default
            listView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL)) // Divider decoration... showing a horizontal line
            listView.adapter = adapter


            // Observe the data from the ViewModel and update the adapter
            viewModel.allShoppingItems.observe(this) { shoppingList ->
                adapter.updateItems(shoppingList)

                // Check if the database is empty
                if (shoppingList.size == 1) {
                    search.visibility = View.GONE // Hide the search view when 1 data is available
                    noDataTv.visibility = View.GONE // Hide the no data text when 1 data is available
                } else if(shoppingList.size > 1) {
                    search.visibility = View.VISIBLE // Hide the search view when more than 1 data is available
                    noDataTv.visibility = View.GONE // Show the no data text when more than 1 data is available
                }else{
                    search.visibility = View.GONE // Hide the search view data is available
                    noDataTv.visibility = View.VISIBLE // Show the no data text when data is available
                }

            }
        } else {
            Toast.makeText(this, "Failed to initialize the database", Toast.LENGTH_SHORT).show()
        }

        // Search view
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    if (::adapter.isInitialized) {
                        adapter.filter.filter(query) // Trigger the filtering process
                    }
                }
                return true
            }

        })


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
}


