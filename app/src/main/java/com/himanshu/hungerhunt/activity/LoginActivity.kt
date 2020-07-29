package com.himanshu.hungerhunt.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtForgotPassword: TextView
    private lateinit var txtRegister: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences =
            getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val queue = Volley.newRequestQueue(this@LoginActivity)
        val url = "http://13.235.250.119/v2/login/fetch_result"

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val etMobileNumber = etMobileNumber.text.toString()
            val etPassword = etPassword.text.toString()

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", etMobileNumber)
            jsonParams.put("password", etPassword)

            if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                val jsonRequest = object :
                    JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                        println("Response is $it")
                        try {
                            val dataObject = it.getJSONObject("data")
                            val success = dataObject.getBoolean("success")
                            if (success) {
                                val data = dataObject.getJSONObject("data")
                                val userId = data.getString("user_id")
                                val name = data.getString("name")
                                val email = data.getString("email")
                                val mobileNumber = data.getString("mobile_number")
                                val address = data.getString("address")

                                sharedPreferences.edit().putString("userId", userId).apply()
                                sharedPreferences.edit().putString("userMobileNumber", mobileNumber).apply()
                                sharedPreferences.edit().putString("userName", name).apply()
                                sharedPreferences.edit().putString("userEmail", email).apply()
                                sharedPreferences.edit().putString("userAddress", address).apply()
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Invalid credentials $it",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@LoginActivity,
                                "$e Catch Error",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@LoginActivity,
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
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("No Connection")
                dialog.setMessage("Please Turn on Internet")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }

        }

        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this@LoginActivity)
    }
}