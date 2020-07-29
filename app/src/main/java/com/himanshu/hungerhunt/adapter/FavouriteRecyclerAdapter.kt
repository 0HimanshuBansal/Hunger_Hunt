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
import com.himanshu.hungerhunt.fragment.FavouriteFragment
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, private val favouriteList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {
    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantCost: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val imgFavoriteRestaurant: ImageView = view.findViewById(R.id.imgFavoriteRestaurant)
        val iconFavouriteRestaurant: ImageButton = view.findViewById(R.id.iconFavouriteRestaurant)
        val linearLayout: LinearLayout = view.findViewById(R.id.linearLayoutFavouriteRestaurant)
        lateinit var name: String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourite_list, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favouriteList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = favouriteList[position]
        val resId = restaurant.restaurant_Id
        val resName = restaurant.restaurantName
        val resRating = restaurant.restaurantRating
        val resCost = restaurant.restaurantCost
        val resImage = restaurant.restaurantImage
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantCost.text = context.getString(R.string.price_per_person, restaurant.restaurantCost)
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.burger_icon)
            .into(holder.imgFavoriteRestaurant)

        val restaurantEntity = RestaurantEntity(
            resId,
            resName,
            resRating,
            resCost,
            resImage
        )
        val checkFav = FavouriteFragment.DBAsyncTask(
            context, restaurantEntity, 1
        ).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.iconFavouriteRestaurant.setImageResource(R.drawable.favorite_red)
        } else {
            holder.iconFavouriteRestaurant.setImageResource(R.drawable.favourite_dark)
        }

        holder.iconFavouriteRestaurant.setOnClickListener {
            if (!FavouriteFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()
            ) {
                val async = FavouriteFragment.DBAsyncTask(context, restaurantEntity, 2).execute()
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
                val async = FavouriteFragment.DBAsyncTask(
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
        holder.linearLayout.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("App Preferences", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("resId", restaurant.restaurant_Id).apply()
            sharedPreferences.edit().putString("resName", restaurant.restaurantName).apply()
            context.startActivity(intent)
        }
    }
}