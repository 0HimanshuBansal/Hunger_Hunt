package com.himanshu.hungerhunt.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.fragment.*
import com.himanshu.hungerhunt.util.ConnectionManager


class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var frameLayout: FrameLayout
    private var previousMenuItem: MenuItem? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE)
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frameLayout)
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        setUpToolbar()
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isChecked = true
            it.isCheckable = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.appHome -> {
                    openHome()
                }
                R.id.myProfile -> {
                    openMyProfile()
                }
                R.id.favouriteRestaurant -> {
                    openFavourite()
                }
                R.id.orderHistory -> {
                    openHistory()
                }
                R.id.faqs -> {
                    openFAQs()
                }
                R.id.logOut -> {
                    logOut()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Restaurants"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, RestaurantFragment())
            .commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.appHome)
    }

    private fun openMyProfile() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, MyProfileFragment())
            .commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "My Profile"
    }

    private fun openFavourite() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, FavouriteFragment())
            .commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "Favourite Restaurants"
    }

    private fun openHistory() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, HistoryFragment())
            .commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "Order History"
    }

    private fun openFAQs() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, FAQsFragment())
            .commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "FAQs"
    }

    private fun logOut() {
        val logOutDialog = AlertDialog.Builder(this@MainActivity)
        logOutDialog.setMessage("Are you sure you want to logout of this app?")
        logOutDialog.setPositiveButton("Yes") { _, _ ->
            sharedPreferences.edit().clear().apply()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
        logOutDialog.setNegativeButton("No") { _, _ ->
            openHome()
        }
        logOutDialog.setCancelable(false)
        logOutDialog.create()
        logOutDialog.show()
        drawerLayout.closeDrawers()
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            !is RestaurantFragment -> openHome()
            else -> super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        if (ConnectionManager().checkConnectivity(this@MainActivity)){
            return
        } else {
            val dialog = android.app.AlertDialog.Builder(this@MainActivity)
            dialog.setTitle("No Connection")
            dialog.setMessage("Please Turn on Internet")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@MainActivity)
            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        }
    }
}