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
import com.google.android.material.textfield.TextInputLayout
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobileNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var txtLogIn: TextView
    private lateinit var textInputConfirmPassword: TextInputLayout
    private lateinit var textInputPassword: TextInputLayout
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences =
            getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        txtLogIn = findViewById(R.id.txtLogIn)
        btnSignUp = findViewById(R.id.btnSignUp)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        textInputPassword = findViewById(R.id.textInputPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        textInputConfirmPassword = findViewById(R.id.textInputConfirmPassword)

        val queue = Volley.newRequestQueue(this@RegisterActivity)
        val url = "http://13.235.250.119/v2/register/fetch_result"

        btnSignUp.setOnClickListener {
            val nameInput = etName.text.toString().trim()
            val emailInput = etEmail.text.toString().trim()
            val mobileNumberInput = etMobileNumber.text.toString().trim()
            val addressInput = etAddress.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (password == confirmPassword) {

                val jsonParams = JSONObject()
                jsonParams.put("name", nameInput)
                jsonParams.put("mobile_number", mobileNumberInput)
                jsonParams.put("password", password)
                jsonParams.put("address", addressInput)
                jsonParams.put("email", emailInput)

                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {
                    val jsonRequest = object :
                        JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                            println("response is $it")

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

                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Success false",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "$e Catch Error",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@RegisterActivity,
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
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("No Connection")
                    dialog.setMessage("Please Turn on Internet")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password did not matched",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        txtLogIn.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
    }
}