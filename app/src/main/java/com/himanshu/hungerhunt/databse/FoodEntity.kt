package com.himanshu.hungerhunt.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class FoodEntity(
    @PrimaryKey val food_id: String,
    @ColumnInfo(name = "food_name") val food_name: String,
    @ColumnInfo(name = "food_price") val food_price: String
)