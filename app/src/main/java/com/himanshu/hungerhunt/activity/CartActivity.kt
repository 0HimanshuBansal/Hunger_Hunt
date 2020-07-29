package com.himanshu.hungerhunt.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.adapter.CartRecyclerAdapter
import com.himanshu.hungerhunt.databse.FoodEntity
import com.himanshu.hungerhunt.databse.RestaurantDatabase
import com.himanshu.hungerhunt.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var txtTotal: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerOrder: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var btnPlaceOrder: Button
    private lateinit var progressLayout: RelativeLayout
    private lateinit var restaurantName: TextView
    private lateinit var recyclerAdapter: CartRecyclerAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var dbOrderList = listOf<FoodEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolbar)
        txtTotal = findViewById(R.id.txtTotal)
        progressBar = findViewById(R.id.progressBar)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        recyclerOrder = findViewById(R.id.recyclerOrder)
        progressLayout = findViewById(R.id.progressLayout)
        restaurantName = findViewById(R.id.txtRestaurantName)
        sharedPreferences = getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE)
        layoutManager = LinearLayoutManager(this@CartActivity as Context)
        dbOrderList = RetrieveOrder(this@CartActivity as Context).execute().get()
        restaurantName.text = sharedPreferences.getString("resName", "noName")
        setUpToolbar()

        progressLayout.visibility = View.GONE
        recyclerAdapter = CartRecyclerAdapter(this@CartActivity as Context, dbOrderList)
        recyclerOrder.adapter = recyclerAdapter
        recyclerOrder.layoutManager = layoutManager

        txtTotal.text = getString(R.string.price_per_person,sharedPreferences.getInt("orderTotal", 0).toString())
        val resId = sharedPreferences.getString("resId", "0")
        val userId = sharedPreferences.getString("userId", "0")
        val total = sharedPreferences.getInt("orderTotal", 0).toString()

        val jsonArray = JSONArray()
        for (i in 0 until dbOrderList.size){
            val jsonObjectFoodList = JSONObject()
            jsonObjectFoodList.put("food_item_id", (this.dbOrderList[i]).food_id)
            jsonArray.put(i, jsonObjectFoodList)
        }

        btnPlaceOrder.setOnClickListener {
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            val queue = Volley.newRequestQueue(this@CartActivity)

            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId)
            jsonParams.put("restaurant_id", resId)
            jsonParams.put("total_cost", total)
            jsonParams.put("food", jsonArray)

            if (ConnectionManager().checkConnectivity(this@CartActivity)){

                val jsonRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonParams,
                    Response.Listener {
                        println("cart response is $it")
                        try {
                            val dataObject = it.getJSONObject("data")
                            val success = dataObject.getBoolean("success")
                            if (success){
                                val intent = Intent(this@CartActivity, OrderConfirmationActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@CartActivity,
                                    "Success is false",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception){
                            Toast.makeText(this@CartActivity, "$e catch error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@CartActivity,
                            "Volley Error $it",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "407f933240a78e"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            } else {
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("No Connection")
                dialog.setMessage("Please Turn on Internet")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    class RetrieveOrder(val context: Context) : AsyncTask<Void, Void, List<FoodEntity>>() {
        override fun doInBackground(vararg params: Void?): List<FoodEntity> {
            val db =
                Room.databaseBuilder(context, RestaurantDatabase::class.java, "food-db").build()
            return db.foodDao().getAllFood()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Cart"
    }
}