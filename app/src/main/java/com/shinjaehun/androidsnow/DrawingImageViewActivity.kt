package com.shinjaehun.androidsnow

import android.content.Context
import android.graphics.Point
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import java.util.*

private const val TAG = "DrawingImageViewActivity"

class DrawingImageViewActivity : AppCompatActivity() {

    var bgHandler = Handler()
    var screenW: Int = 0
    var screenH: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing_image_view)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        var point = Point()
        windowManager.defaultDisplay.getRealSize(point)
        screenW = point.x
        screenH = point.y
        setUpSnowEffect()
    }

    lateinit var r: Runnable
    var snowList: ArrayList<Snow> = ArrayList()
    fun setUpSnowEffect() {
        // generate 200 snow flake
        var container: ViewGroup = window.decorView as ViewGroup
        for (i in 0 until 200) {
            snowList.add(Snow(baseContext, screenW.toFloat(), screenH.toFloat(), container))
        }

        Log.i(TAG, "the size of snowList: ${snowList.size}")

        // setup runnable and postDelay
        r = Runnable {
            for (snow: Snow in snowList)
                snow.update()
            bgHandler.postDelayed(r, 10)
        }
        bgHandler.post(r)
    }

    class Snow(context: Context, private val screenW: Float, private val screenH: Float, parent: ViewGroup) {
//        private val snowRes: IntArray = intArrayOf(R.drawable.ic_snowflake)
        private var iv: ImageView = ImageView(context)
        private var distance = 0.7f // 0.5 ~ 1.0f
        private var randomParam = 0.0f // -0.3 ~ 0.3 f for some special effects
        private val fallingSpeed = 6
        private val windSpeed = 4
        init {
            randomParam = Random().nextFloat() * 0.6f - 0.3f
            distance = Random().nextFloat() * 0.5f + 0.5f
//            iv.setBackgroundResource(snowRes[Random().nextInt(5)])
            iv.setBackgroundResource(R.drawable.ic_snowflake)
            parent.addView(iv)
            // far=small,blur;  close=big,clear
            iv.layoutParams.height = (Random().nextFloat() * screenH * 0.03 / distance).toInt()
            iv.layoutParams.width = (Random().nextFloat() * screenH * 0.03 / distance).toInt()
            iv.alpha = (1.0f - distance * 0.7).toFloat()
            iv.translationX = Random().nextFloat() * screenW
            iv.translationY = Random().nextFloat() * screenH
            Log.d("translationX", (Random().nextFloat() * screenW).toString())
            iv.rotation = Random().nextFloat() * 360
        }

        fun update() {
            // far=slow, close=fast
            iv.translationY = (iv.translationY + fallingSpeed * (1 - distance * 0.7)).toFloat()
            iv.translationX = (iv.translationX + windSpeed * (1 - distance * 0.7)).toFloat()
            var time: Float = System.currentTimeMillis() % 1000 / 1000.toFloat()
            if (iv.translationY > screenH)
                iv.translationY = iv.translationY - screenH
            if (iv.translationX > screenW)
                iv.translationX = iv.translationX - screenW
            iv.rotation = iv.rotation +  randomParam * 5 // rotation parameter


        }
    }
}