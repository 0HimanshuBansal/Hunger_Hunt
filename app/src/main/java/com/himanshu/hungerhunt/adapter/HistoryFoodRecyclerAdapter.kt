package com.himanshu.hungerhunt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.model.Food

class HistoryFoodRecyclerAdapter(val context: Context, private val foodList: ArrayList<Food>) :
    RecyclerView.Adapter<HistoryFoodRecyclerAdapter.FoodViewHolder>() {
    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItem: TextView = view.findViewById(R.id.txtItem)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_item, parent, false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val historyOrder = foodList[position]

        holder.txtItem.text = (position + 1).toString()
        holder.txtFoodName.text = historyOrder.foodName
        holder.txtFoodPrice.text = context.getString(R.string.price, historyOrder.foodPrice)
    }
}