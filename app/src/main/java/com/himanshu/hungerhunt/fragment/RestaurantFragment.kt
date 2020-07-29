package com.himanshu.hungerhunt.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.adapter.RestaurantRecyclerAdapter
import com.himanshu.hungerhunt.databse.RestaurantDatabase
import com.himanshu.hungerhunt.databse.RestaurantEntity
import com.himanshu.hungerhunt.model.Restaurant
import com.himanshu.hungerhunt.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class RestaurantFragment : Fragment() {

    lateinit var recyclerRestaurant: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    var restaurantInfoList = arrayListOf<Restaurant>()

    private var lowToHighCostComparator = Comparator<Restaurant> { cost1, cost2 ->
        if (cost1.restaurantCost.compareTo(cost2.restaurantCost, true) == 0) {
            cost1.restaurantRating.compareTo(cost2.restaurantRating, true)
        } else {
            cost1.restaurantCost.compareTo(cost2.restaurantCost, true)
        }
    }

    private var highToLowCostComparator = Comparator<Restaurant> { cost1, cost2 ->
        if (cost1.restaurantCost.compareTo(cost2.restaurantCost, true) == 0) {
            cost1.restaurantRating.compareTo(cost2.restaurantRating, true)
        } else {
            cost1.restaurantCost.compareTo(cost2.restaurantCost, true)
        }
    }

    private var ratingComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
        if (restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true) == 0) {
            restaurant1.restaurantCost.compareTo(restaurant2.restaurantCost, true)
        } else {
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)
        setHasOptionsMenu(true)

        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurant)
        layoutManager = LinearLayoutManager(activity)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object :
                    JsonObjectRequest(Method.GET, url, null, Response.Listener<JSONObject> {
                        progressLayout.visibility = View.GONE
                        println("Response is $it")
                        try {
                            val dataObject = it.getJSONObject("data")
                            val success = dataObject.getBoolean("success")
                            if (success) {
                                val data = dataObject.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val restaurantJsonObject = data.getJSONObject(i)
                                    val restaurantObject = Restaurant(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("rating"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("image_url")
                                    )
                                    restaurantInfoList.add(restaurantObject)
                                    recyclerAdapter = RestaurantRecyclerAdapter(
                                        activity as Context,
                                        restaurantInfoList
                                    )

                                    recyclerRestaurant.adapter = recyclerAdapter
                                    recyclerRestaurant.layoutManager = layoutManager
                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "success is false",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                activity as Context,
                                "$e Catch Error",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }, Response.ErrorListener {
                        if (activity != null) {
                            Toast.makeText(activity as Context, "Volley Error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "407f933240a78e"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("No Internet Connection")
            dialog.setMessage("Turn on Internet")
            dialog.setPositiveButton("Open Setting") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.sort_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.highToLow -> {
                Collections.sort(restaurantInfoList, highToLowCostComparator)
                restaurantInfoList.reverse()
            }
            R.id.lowToHigh -> {
                Collections.sort(restaurantInfoList, lowToHighCostComparator)
            }
            R.id.rating -> {
                Collections.sort(restaurantInfoList, ratingComparator)
                restaurantInfoList.reverse()
            }
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

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