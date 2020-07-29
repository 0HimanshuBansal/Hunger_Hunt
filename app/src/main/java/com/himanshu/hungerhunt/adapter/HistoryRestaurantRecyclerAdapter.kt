package com.himanshu.hungerhunt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.model.Food
import com.himanshu.hungerhunt.model.HistoryRestaurant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryRestaurantRecyclerAdapter(
    val context: Context,
    private val restaurantList: ArrayList<HistoryRestaurant>
) :
    RecyclerView.Adapter<HistoryRestaurantRecyclerAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        val foodRecycler: RecyclerView = view.findViewById(R.id.recyclerOrderHistoryChild)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_history_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val historyRestaurant = restaurantList[position]
        holder.txtRestaurantName.text = historyRestaurant.restaurantName
        holder.txtOrderDate.text = formatDate(historyRestaurant.orderDate)

        setUpRecycler(holder.foodRecycler, historyRestaurant)
    }

    private fun setUpRecycler(recyclerResHistory: RecyclerView, historyRestaurant: HistoryRestaurant) {
        val foodItemsList = ArrayList<Food>()
        for (i in 0 until historyRestaurant.foodItems.length()) {
            val foodJson = historyRestaurant.foodItems.getJSONObject(i)
            foodItemsList.add(
                Food(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val historyFoodRecyclerAdapter = HistoryFoodRecyclerAdapter(context, foodItemsList)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerResHistory.layoutManager = mLayoutManager
        recyclerResHistory.itemAnimator = DefaultItemAnimator()
        recyclerResHistory.adapter = historyFoodRecyclerAdapter
    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }

}