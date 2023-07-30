package com.example.shoppinglist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.CategoryHeaderBinding
import com.example.shoppinglist.databinding.ItemShoppingListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListAdapter(
    private val context: Context,
    private val shoppingRepo: ShoppingListRepository
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var shoppingList: MutableList<ShoppingList> = mutableListOf()
    private var filteredShoppingList: MutableList<ShoppingList> = mutableListOf()
    private var groupedDisplayList: MutableList<Any> = mutableListOf() // Combined list of categories and shopping items

    // View types
    private val VIEW_TYPE_CATEGORY_HEADER = 0
    private val VIEW_TYPE_SHOPPING_ITEM = 1

    class MyHolder(binding: ItemShoppingListBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvName
        val checkbox = binding.checkbox
        val delete = binding.btnDelete
    }

    class CategoryHeaderHolder(binding: CategoryHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCategory: TextView = binding.tvCategoryHeader
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY_HEADER -> {
                val headerBinding = CategoryHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
                CategoryHeaderHolder(headerBinding)
            }
            VIEW_TYPE_SHOPPING_ITEM -> {
                val itemBinding = ItemShoppingListBinding.inflate(LayoutInflater.from(context), parent, false)
                MyHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = groupedDisplayList.size

    override fun getItemViewType(position: Int): Int {
        return if (groupedDisplayList[position] is String) {
            VIEW_TYPE_CATEGORY_HEADER
        } else {
            VIEW_TYPE_SHOPPING_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = groupedDisplayList[position]

        when (holder) {
            is CategoryHeaderHolder -> {
                holder.tvCategory.text = item as String
            }
            is MyHolder -> {
                val model = item as ShoppingList
                holder.name.text = model.itemName
                holder.checkbox.isChecked = model.isChecked

                holder.checkbox.setOnCheckedChangeListener(null) // Clear the previous listener
                holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        shoppingRepo.updateShoppingItem(model.copy(isChecked = isChecked))
                    }
                }

                holder.delete.setOnClickListener {
                    // Alert dialog for deleting
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Delete")
                    builder.setMessage("Are you sure you want to delete?")
                    builder.setPositiveButton("Yes") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            shoppingRepo.deleteShoppingItem(model)
                        }
                    }
                    builder.setNegativeButton("No") { _, _ -> }
                    builder.show()
                }
            }
        }
    }

    fun updateItems(newItems: MutableList<ShoppingList>) {
        shoppingList.clear()
        shoppingList.addAll(newItems)
        filter.filter(null) // Reset the filter when updating items
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()
                val results = FilterResults()

                if (query.isNullOrEmpty()) {
                    results.values = shoppingList
                } else {
                    val filteredResults = ArrayList<ShoppingList>()
                    for (item in shoppingList) {
                        if (item.itemName.lowercase().contains(query)) {
                            filteredResults.add(item)
                        }
                    }
                    results.values = filteredResults
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredShoppingList = results?.values as? MutableList<ShoppingList> ?: mutableListOf()
                updateDisplayList()
                notifyDataSetChanged()
            }
        }
    }

    private fun updateDisplayList() {
        groupedDisplayList.clear()

        val groupedShoppingItems = filteredShoppingList.groupBy { it.category }
        for ((category, shoppingItems) in groupedShoppingItems) {
            groupedDisplayList.add(category)
            groupedDisplayList.addAll(shoppingItems)
        }
    }

}