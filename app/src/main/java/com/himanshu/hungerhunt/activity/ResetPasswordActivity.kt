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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etOTP: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSubmit: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOTP = findViewById(R.id.etOTP)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        sharedPreferences = getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE)

        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
        val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
        val mobileNumber = intent.getStringExtra("mobile_number")

        btnSubmit.setOnClickListener {
            val otp = etOTP.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("password", password)
            jsonParams.put("otp", otp)

            if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {

                if (password == confirmPassword) {
                    val jsonRequest = object : JsonObjectRequest(
                        Method.POST, url, jsonParams, Response.Listener {
                            println("reset response $it")

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if (success) {
                                    sharedPreferences.edit().clear().apply()
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        "Password has successfully changed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        "success is false",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "Catch Error $it",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@ResetPasswordActivity,
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
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Password did not matched",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                dialog.setTitle("No Connection")
                dialog.setMessage("Please Turn on Internet")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
    }
}