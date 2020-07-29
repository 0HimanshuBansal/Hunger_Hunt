package com.himanshu.hungerhunt.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.activity.CartActivity
import com.himanshu.hungerhunt.databse.FoodEntity

class CartRecyclerAdapter(val context: Context, private val orderList: List<FoodEntity>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItem: TextView = view.findViewById(R.id.txtItem)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_item, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.count()
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = orderList[position]

        holder.txtItem.text = (position + 1).toString()
        holder.txtFoodName.text = order.food_name
        holder.txtFoodPrice.text = context.getString(R.string.price, order.food_price)
    }
}