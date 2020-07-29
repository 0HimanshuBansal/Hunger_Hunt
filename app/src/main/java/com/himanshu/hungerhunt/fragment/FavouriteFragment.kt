package com.himanshu.hungerhunt.fragment

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.adapter.FavouriteRecyclerAdapter
import com.himanshu.hungerhunt.adapter.RestaurantRecyclerAdapter
import com.himanshu.hungerhunt.databse.RestaurantDatabase
import com.himanshu.hungerhunt.databse.RestaurantEntity

class FavouriteFragment : Fragment() {

    lateinit var relativeLayoutNoFavourite: RelativeLayout
    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var imgAnimFavourite: ImageView
    lateinit var avd: AnimatedVectorDrawableCompat
    lateinit var avd2: AnimatedVectorDrawable
    var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        imgAnimFavourite = view.findViewById(R.id.imgAnimFavourite)
        relativeLayoutNoFavourite = view.findViewById(R.id.relativeLayoutNoFavourite)
        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        layoutManager = GridLayoutManager(activity as Context, 2)
        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()

        val drawable: Drawable = imgAnimFavourite.drawable

        if (activity != null) {
            progressLayout.visibility = View.GONE
            if (dbRestaurantList.isEmpty()) {
                when (drawable) {
                    is AnimatedVectorDrawableCompat -> {
                        avd = drawable
                        avd.start()
                    }
                    is AnimatedVectorDrawable -> {
                        avd2 = drawable
                        avd2.start()
                    }
                }
            } else {
                relativeLayoutNoFavourite.visibility = View.GONE
            }
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {

        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db =
                Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db")
                    .build()
            return db.restaurantDao().getAllRestaurant()
        }
    }

    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db")
                .build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_Id)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}