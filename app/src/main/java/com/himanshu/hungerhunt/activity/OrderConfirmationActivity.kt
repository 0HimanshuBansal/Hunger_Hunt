package com.himanshu.hungerhunt.activity

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.himanshu.hungerhunt.R

class OrderConfirmationActivity : AppCompatActivity() {
    lateinit var animDone: ImageView
    lateinit var btnOk: Button
    lateinit var avd: AnimatedVectorDrawableCompat
    lateinit var avd2: AnimatedVectorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        animDone = findViewById(R.id.animDone)
        btnOk = findViewById(R.id.btnOk)

        RestaurantMenuActivity.ClearOrder(applicationContext).execute().get()

        val drawable: Drawable = animDone.drawable

        if (drawable is AnimatedVectorDrawableCompat) {
            avd = drawable
            avd.start()
        } else if (drawable is AnimatedVectorDrawable) {
            avd2 = drawable
            avd2.start()
        }

        btnOk.setOnClickListener {
            finish()
            val intent = Intent(this@OrderConfirmationActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@OrderConfirmationActivity, MainActivity::class.java))
    }
}