package com.himanshu.hungerhunt.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.adapter.RestaurantMenuRecyclerAdapter
import com.himanshu.hungerhunt.databse.FoodEntity
import com.himanshu.hungerhunt.databse.RestaurantDatabase
import com.himanshu.hungerhunt.model.Food
import org.json.JSONException
import org.json.JSONObject

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var recyclerAdapter: RestaurantMenuRecyclerAdapter
    lateinit var btnProceedToCart: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerFoodMenu: RecyclerView
    private var restaurantName: String? = "Restaurant"
    private var restaurantId: String? = "0"
    var foodMenuInfoList = arrayListOf<Food>()
    var tempFoodMenuInfoList = arrayListOf<Food>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        layoutManager = LinearLayoutManager(this@RestaurantMenuActivity)
        progressLayout = findViewById(R.id.progressLayout)
        recyclerFoodMenu = findViewById(R.id.recyclerFoodMenu)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        sharedPreferences =
            getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE)
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE

        recyclerFoodMenu.addItemDecoration(
            DividerItemDecoration(
                recyclerFoodMenu.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )
        setUpToolbar()
        ClearOrder(applicationContext).execute().get()

        if (intent != null) {
            restaurantId = sharedPreferences.getString("resId", "0")
            restaurantName = sharedPreferences.getString("resName", "Restaurant")
            supportActionBar?.title = restaurantName
        } else {
            finish()
            Toast.makeText(this@RestaurantMenuActivity, "intent is Null", Toast.LENGTH_SHORT).show()
        }

        if (restaurantId == "0") {
            finish()
            Toast.makeText(this@RestaurantMenuActivity, "Id is 000", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
            Response.Listener<JSONObject> {
                progressLayout.visibility = View.GONE
                println("Menu Response is $it")
                try {
                    val dataObject = it.getJSONObject("data")
                    val success = dataObject.getBoolean("success")
                    if (success) {
                        val data = dataObject.getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val foodMenuJSONObject = data.getJSONObject(i)
                            val foodMenuObject = Food(
                                foodMenuJSONObject.getString("id"),
                                foodMenuJSONObject.getString("name"),
                                foodMenuJSONObject.getString("cost_for_one")
                            )
                            foodMenuInfoList.add(foodMenuObject)
                            recyclerAdapter = RestaurantMenuRecyclerAdapter(
                                this@RestaurantMenuActivity,
                                foodMenuInfoList,
                                object : RestaurantMenuRecyclerAdapter.RestaurantMenuListener {
                                    override fun onAddClick(food: Food) {
                                        tempFoodMenuInfoList.add(food)
                                        if (tempFoodMenuInfoList.size > 0) {
                                            btnProceedToCart.visibility = View.VISIBLE
                                        }
                                    }

                                    override fun onRemoveClick(food: Food) {
                                        tempFoodMenuInfoList.remove(food)
                                        if (tempFoodMenuInfoList.isEmpty()) {
                                            btnProceedToCart.visibility = View.GONE
                                        }
                                    }
                                }
                            )
                            recyclerFoodMenu.adapter = recyclerAdapter
                            recyclerFoodMenu.layoutManager = layoutManager
                        }
                    } else {
                        Toast.makeText(
                            this@RestaurantMenuActivity,
                            "success is false",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        this@RestaurantMenuActivity,
                        "$e Catch Error",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            },
            Response.ErrorListener {
                if (this@RestaurantMenuActivity != null) {
                    Toast.makeText(this@RestaurantMenuActivity, "Volley Error", Toast.LENGTH_SHORT)
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
        btnProceedToCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class DBAsyncTask(
        val context: Context,
        private val foodEntity: FoodEntity,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val food: FoodEntity? = db.foodDao().getFoodById(foodEntity.food_id)
                    db.close()
                    return food != null
                }
                2 -> {
                    db.foodDao().addFood(foodEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.foodDao().removeFood(foodEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    override fun onBackPressed() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("Cart Preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("total", 0).apply()
        ClearOrder(applicationContext).execute().get()
        super.onBackPressed()
    }

    class ClearOrder(val context: Context) : AsyncTask<Void, Void, Unit>() {
        override fun doInBackground(vararg params: Void?){
            val db =
                Room.databaseBuilder(context, RestaurantDatabase::class.java, "food-db").build()
            return db.foodDao().delete()
        }
    }
}