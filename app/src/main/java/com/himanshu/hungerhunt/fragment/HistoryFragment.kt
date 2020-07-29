package com.himanshu.hungerhunt.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.adapter.HistoryRestaurantRecyclerAdapter
import com.himanshu.hungerhunt.model.HistoryRestaurant
import com.himanshu.hungerhunt.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class HistoryFragment : Fragment() {

    private lateinit var avd: AnimatedVectorDrawableCompat
    private lateinit var avd2: AnimatedVectorDrawable
    private lateinit var imgAnimCart: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutManagerGroup: LinearLayoutManager
    private lateinit var restaurantAdapter: HistoryRestaurantRecyclerAdapter
    private lateinit var progressLayout: RelativeLayout
    private lateinit var recyclerOrderHistory: RecyclerView
    private lateinit var animOrderHistory: RelativeLayout
    var restaurantHistoryList = arrayListOf<HistoryRestaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        imgAnimCart = view.findViewById(R.id.imgAnimCart)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        layoutManagerGroup = LinearLayoutManager(context)
        animOrderHistory = view.findViewById(R.id.animOrderHistory)

        val userId =
            context?.getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE)
                ?.getString("userId", "0")
        if (userId == "0") {
            Toast.makeText(activity as Context, "user id is zero", Toast.LENGTH_SHORT).show()
        }
        if (userId == null) {
            Toast.makeText(activity as Context, "user id is null", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object :
                    JsonObjectRequest(Method.GET, url, null, Response.Listener<JSONObject> {
                        println("History Response is $it")
                        try {
                            val dataObject = it.getJSONObject("data")
                            val success = dataObject.getBoolean("success")
                            if (success) {
                                val data = dataObject.getJSONArray("data")
                                if (data.length() == 0) {
                                    progressLayout.visibility = View.GONE
                                    animOrderHistory.visibility = View.VISIBLE
                                } else {
                                    for (i in 0 until data.length()) {
                                        val restaurantJsonObject = data.getJSONObject(i)
                                        val foodItems =
                                            restaurantJsonObject.getJSONArray("food_items")

                                        val orderDetails = HistoryRestaurant(
                                            restaurantJsonObject.getString("order_id"),
                                            restaurantJsonObject.getString("restaurant_name"),
                                            restaurantJsonObject.getString("order_placed_at"),
                                            foodItems
                                        )
                                        restaurantHistoryList.add(orderDetails)
                                        if (restaurantHistoryList.isEmpty()) {
                                            progressLayout.visibility = View.GONE
                                            animOrderHistory.visibility = View.VISIBLE
                                            animateEmptyCart()
                                        } else {
                                            progressLayout.visibility = View.GONE
                                            animOrderHistory.visibility = View.GONE
                                            if (activity != null) {
                                                restaurantAdapter =
                                                    HistoryRestaurantRecyclerAdapter(
                                                        activity as Context,
                                                        restaurantHistoryList
                                                    )
                                                val mLayoutManager =
                                                    LinearLayoutManager(activity as Context)
                                                recyclerOrderHistory.layoutManager = mLayoutManager
                                                recyclerOrderHistory.itemAnimator =
                                                    DefaultItemAnimator()
                                                recyclerOrderHistory.adapter = restaurantAdapter
                                            } else {
                                                queue.cancelAll(this::class.java.simpleName)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "success is false",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                activity as Context,
                                "$e Catch Error",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                        Response.ErrorListener {
                            progressLayout.visibility = View.GONE
                            if (activity != null) {
                                Toast.makeText(
                                    activity as Context,
                                    "Volley Error $it",
                                    Toast.LENGTH_SHORT
                                )
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

    fun animateEmptyCart() {
        val drawable: Drawable = imgAnimCart.drawable
        if (drawable is AnimatedVectorDrawableCompat) {
            avd = drawable
            avd.start()
        } else if (drawable is AnimatedVectorDrawable) {
            avd2 = drawable
            avd2.start()
        }
    }
}