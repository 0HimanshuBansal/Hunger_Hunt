package com.himanshu.hungerhunt.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.activity.RestaurantMenuActivity
import com.himanshu.hungerhunt.databse.RestaurantEntity
import com.himanshu.hungerhunt.fragment.RestaurantFragment
import com.himanshu.hungerhunt.model.Restaurant
import com.squareup.picasso.Picasso

class RestaurantRecyclerAdapter(val context: Context, private val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<RestaurantRecyclerAdapter.RestaurantViewHolder>() {
    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgRestaurantIcon: ImageView = view.findViewById(R.id.imgRestaurantIcon)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val iconFavouriteRestaurant: ImageButton = view.findViewById(R.id.iconFavouriteRestaurant)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val linearLayoutRestaurant: LinearLayout =
            view.findViewById(R.id.linearLayoutRestaurantInfo)
        lateinit var name: String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurant_list, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = itemList[position]

        val resId = restaurant.restaurantId
        val resName = restaurant.restaurantName
        val resRating = restaurant.restaurantRating
        val resCost = restaurant.restaurantCost
        val resImage = restaurant.restaurantImage
        holder.txtRestaurantName.text = resName
        holder.txtRestaurantPrice.text = context.getString(R.string.price_per_person, resCost)
        holder.txtRestaurantRating.text = resRating
        Picasso.get().load(resImage).error(R.drawable.hunger_hunt)
            .into(holder.imgRestaurantIcon)

        val restaurantEntity = RestaurantEntity(
            resId,
            resName,
            resRating,
            resCost,
            resImage
        )
        val checkFav = RestaurantFragment.DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.iconFavouriteRestaurant.setImageResource(R.drawable.favorite_red)
        } else {
            holder.iconFavouriteRestaurant.setImageResource(R.drawable.favourite_dark)
        }

        holder.iconFavouriteRestaurant.setOnClickListener {
            if (!RestaurantFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()
            ) {
                val async = RestaurantFragment.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${holder.name} Added To Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.iconFavouriteRestaurant.setImageResource(R.drawable.favorite_red)
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = RestaurantFragment.DBAsyncTask(
                    context, restaurantEntity, 3
                ).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${holder.name} Removed From Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.iconFavouriteRestaurant.setImageResource(R.drawable.favourite_dark)
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.name = restaurant.restaurantName
        holder.linearLayoutRestaurant.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)

            val sharedPreferences: SharedPreferences = context.getSharedPreferences("App Preferences", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("resId", restaurant.restaurantId).apply()
            sharedPreferences.edit().putString("resName", restaurant.restaurantName).apply()
            context.startActivity(intent)
        }
    }
}