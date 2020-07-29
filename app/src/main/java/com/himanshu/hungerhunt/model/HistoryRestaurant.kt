package com.himanshu.hungerhunt.model

import org.json.JSONArray

data class HistoryRestaurant (
    val orderId: String,
    val restaurantName: String,
    val orderDate: String,
    val foodItems: JSONArray
)