package com.himanshu.hungerhunt.adapter

import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.model.Food
import com.himanshu.hungerhunt.databse.FoodEntity
import com.himanshu.hungerhunt.activity.RestaurantMenuActivity
import android.widget.Toast
import android.widget.Button
import android.widget.TextView
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView

class RestaurantMenuRecyclerAdapter(val context: Context, private val itemList: ArrayList<Food>, private val mCallBack : RestaurantMenuListener) :
    RecyclerView.Adapter<RestaurantMenuRecyclerAdapter.RestaurantMenuViewHolder>() {

    interface RestaurantMenuListener {
        fun onAddClick(food: Food)
        fun onRemoveClick(food: Food)
    }

    var total: Int = 0

    class RestaurantMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFoodId: TextView = view.findViewById(R.id.txtFoodId)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnAddFood: Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_food_item, parent, false)
        return RestaurantMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {
        val foodMenu = itemList[position]

        val foodName = foodMenu.foodName
        val foodId = foodMenu.foodId
        val foodPrice = foodMenu.foodPrice

        holder.txtFoodId.text = (position + 1).toString()
        holder.txtFoodName.text = foodMenu.foodName
        holder.txtFoodPrice.text = context.getString(R.string.price, foodMenu.foodPrice)

        val foodEntity = FoodEntity(foodId, foodName, foodPrice)

        holder.btnAddFood.setOnClickListener {

            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences("App Preferences", Context.MODE_PRIVATE)
            if (!RestaurantMenuActivity.DBAsyncTask(context, foodEntity, 1).execute().get()) {
                val async = RestaurantMenuActivity.DBAsyncTask(context, foodEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "${holder.txtFoodName.text} Added", Toast.LENGTH_SHORT)
                        .show()
                    holder.btnAddFood.setBackgroundResource(R.color.lightOrange)
                    holder.btnAddFood.text = context.getString(R.string.remove)
                    total += foodMenu.foodPrice.toInt()
                    sharedPreferences.edit().putInt("orderTotal", total).apply()
                    mCallBack.onAddClick(foodMenu)
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = RestaurantMenuActivity.DBAsyncTask(context, foodEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${holder.txtFoodName.text} Removed",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.btnAddFood.setBackgroundResource(R.color.materialRed)
                    holder.btnAddFood.text = context.getString(R.string.add)
                    total -= foodMenu.foodPrice.toInt()
                    mCallBack.onRemoveClick(foodMenu)
                    sharedPreferences.edit().putInt("orderTotal", total).apply()
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}