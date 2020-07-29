package com.himanshu.hungerhunt.databse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {
    @Insert
    fun addFood(foodEntity: FoodEntity)

    @Delete
    fun removeFood(foodEntity: FoodEntity)

    @Query("SELECT * FROM food")
    fun getAllFood(): List<FoodEntity>

    @Query("DELETE FROM food")
    fun delete()

    @Query("SELECT * FROM food WHERE food_id = :foodId")
    fun getFoodById(foodId: String) : FoodEntity
}