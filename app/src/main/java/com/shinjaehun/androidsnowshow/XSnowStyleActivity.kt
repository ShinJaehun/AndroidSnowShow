package com.shinjaehun.androidsnowshow

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

private const val TAG = "XSnowStyleActivity"

class XSnowStyleActivity : AppCompatActivity() {


    companion object {
        var screenHeight = 0
        var screenWidth = 0

        var bgHandler = Handler()

        const val disappear_margin = 32				// pixels from each border where objects disappear
        const val flakes = 25 // total number of flakes
        const val flake_speed_PperS = 30 // flake speed in pixel/second
        const val flake_TX: Float = 1f // max. sec. of flake's constant X-movement on fluttering

        const val flake_XperY: Float = 2f // fluttering movement's max. vx/vy ratio
        const val strom_duration_S = 10.0 // storm duration in seconds: about 1-2 seconds for deceleration
        const val storm_lag_S = 60.0 // no-storm in seconds

        const val storm_YperX: Float = 1/3f // storm's max. vy/vx ratio
        const val refresh_FperS = 20					// initial frames/second, recalculated.

        const val refresh 	  = 1000/refresh_FperS;	// ms/frame


    }

    var storm_speed 	= 0				// storm speed in pixel/frame
    var storm_YperX_current = storm_YperX;  // storm direction varies each time
    var storm_v_sin     = 0                // storm speed's sine
    var storm_v_cos     = 1                // storm speed's cosine
    var storm_direction = 0				// storm X-direction, -1/0=quiet/+1
    var storm_id    	= 0				// ID of storm timer

    var storm_blowing	= 1				// start with storm=ON

    var flake_speed 	= 0f				// flake speed in pixel/frame
    var flake_visible = false

    var flakeDX  = 0			// X-movement in pixel/frame, caused by storm
    var flakeDY  = 0			// Y-movement in pixel/frame, caused by storm

    var flake_id	  = 0		// timer id of make_flake_visible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xsnow_style)

        val wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = wm.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            screenWidth = windowMetrics.bounds.width() - insets.left - insets.right
            screenHeight = windowMetrics.bounds.height() - insets.bottom - insets.top
        } else {
            var point = Point()
//            val displayMetrics = DisplayMetrics()
            wm.defaultDisplay.getRealSize(point)
            screenWidth = point.x
            screenHeight = point.y
        }
        Log.i(TAG, " screenWidth: $screenWidth, screenHeight: $screenHeight")

        setUpSnowEffect()
    }

    lateinit var r: Runnable
    var snowList: ArrayList<Snow> = ArrayList()

    fun setUpSnowEffect() {
        // generate 200 snow flake
        var container: ViewGroup = window.decorView as ViewGroup
        for (i in 0 until 30) {
            snowList.add(
                Snow(
                    baseContext,
                    container,
                    screenWidth.toFloat(),
                    screenHeight.toFloat(),
                    true,
                    flakeDX,
                    flakeDY,
                    flake_speed
                )
            )
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

    class Snow(
        context: Context,
        parent: ViewGroup,
        private val screenW: Float,
        private val screenH: Float,
        private val flake_visible: Boolean,
        private val flakeDX: Int,
        private val flakeDY: Int,
        private val flake_speed: Float
    ) {
        private val snowRes: IntArray = intArrayOf(R.drawable.snow0, R.drawable.snow1, R.drawable.snow2, R.drawable.snow3, R.drawable.snow4, R.drawable.snow5, R.drawable.snow6)
        private var iv: ImageView = ImageView(context)

        var flakeX = 0
        var flakeY = 0
        var flakeSX: Float = 0f
        var flakeVX: Float = 0f
        var flakeVY: Float = 0f
        var flakeVIS = false

//        private var distance = 0.7f // 0.5 ~ 1.0f
//        private var randomParam = 0.0f // -0.3 ~ 0.3 f for some special effects
//        private val fallingSpeed = 6
//        private val windSpeed = 4

        init {
            flakeX = (Random().nextFloat() * screenW).toInt()
            flakeY = (Random().nextFloat() * screenH).toInt()
            flakeSX = 0f
            flakeVX = 0f
            flakeVY = 1f
            flakeVIS = flake_visible

//            randomParam = Random().nextFloat() * 0.6f - 0.3f
//            distance = Random().nextFloat() * 0.5f + 0.5f
            iv.setBackgroundResource(snowRes[Random().nextInt(6)])
            parent.addView(iv)

            // size
            iv.layoutParams.height = (screenH * 0.01).toInt()
            iv.layoutParams.width = (screenH * 0.01).toInt()
        }

        fun update() {
            flakeX += (flakeVX + flakeDX).toInt()
            flakeY += (flakeVY + flakeDY).toInt()
            if (flakeY > screenH - disappear_margin) {
                flakeX = (Random().nextFloat() * screenW).toInt()
                flakeY = 0
                flakeVY = flake_speed + Random().nextFloat() * flake_speed
                if (Random().nextFloat()<0.1) flakeVY *= 2
                if (flake_visible) flakeVIS=true
            }

            flakeSX--
            if (flakeSX <= 0) {
                flakeSX = Random().nextFloat() * refresh_FperS.toFloat() * flake_TX
                flakeVX = (2f*Random().nextFloat()-1f) * flake_XperY * flake_speed
            }

            if (flakeX < -disappear_margin)
                flakeX += screenW.toInt()
            if (flakeX >= screenW - disappear_margin)
                flakeX -= screenW.toInt()

            if (flakeVIS) {
                iv.translationX = flakeX.toFloat()
                iv.translationY = flakeY.toFloat()
            }

            // far=slow, close=fast
//            iv.translationY = (iv.translationY + fallingSpeed * (1 - distance * 0.7)).toFloat()
//            iv.translationX = (iv.translationX + windSpeed * (1 - distance * 0.7)).toFloat()
//            var time: Float = System.currentTimeMillis() % 1000 / 1000.toFloat()
//            if (iv.translationY > screenH)
//                iv.translationY = iv.translationY - screenH
//            if (iv.translationX > screenW)
//                iv.translationX = iv.translationX - screenW
//            iv.rotation = iv.rotation +  randomParam * 5 // rotation parameter


        }
    }

}