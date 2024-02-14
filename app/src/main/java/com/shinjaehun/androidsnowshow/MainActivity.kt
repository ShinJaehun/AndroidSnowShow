package com.shinjaehun.androidsnowshow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btCustomView = findViewById<Button>(R.id.bt_customView)
        btCustomView.setOnClickListener {
            Log.i(TAG, "Button Clicked!")
            startActivity(Intent(this@MainActivity, MakingCustomViewActivity::class.java))
        }

        val btImageView = findViewById<Button>(R.id.bt_imageView)
        btImageView.setOnClickListener {
            startActivity(Intent(this@MainActivity, DrawingImageViewActivity::class.java))
        }

        val btXsnowStyle = findViewById<Button>(R.id.bt_xsnowStyle)
        btXsnowStyle.setOnClickListener {
            startActivity(Intent(this@MainActivity, XSnowStyleActivity::class.java))
        }
    }
}