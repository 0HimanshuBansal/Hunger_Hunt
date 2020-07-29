package com.himanshu.hungerhunt.activity

import android.app.AlertDialog
import android.content.Intent
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
import com.himanshu.hungerhunt.R
import com.himanshu.hungerhunt.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etMobileNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnRequestOTP: Button
    private lateinit var txtRegister: TextView
    private lateinit var txtLogIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnRequestOTP = findViewById(R.id.btnRequestOTP)
        txtRegister = findViewById(R.id.txtRegister)
        txtLogIn = findViewById(R.id.txtLogIn)

        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)

        btnRequestOTP.setOnClickListener {
            val mobileNumber = etMobileNumber.text.toString()
            val email = etEmail.text.toString()

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("email", email)

            if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
                val jsonRequest = object :
                    JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                        println("Forgot Password Response is $it")
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {
                                val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                                intent.putExtra("mobile_number", mobileNumber)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Success Error",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Catch Error $e",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
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
                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                dialog.setTitle("No Connection")
                dialog.setMessage("Please Turn on Internet")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                }
                dialog.create()
                dialog.show()
            }
        }

        txtRegister.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        txtLogIn.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
    }
}