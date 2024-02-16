package com.shinjaehun.androidsnowshow

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shinjaehun.androidsnowshow.XSnowStyleActivity.Companion.refresh_FperS
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.math.floor

private const val TAG = "XSnowStyleActivity"

class XSnowStyleActivity : AppCompatActivity() {


    companion object {
        var screenHeight = 0
        var screenWidth = 0

        var snowList: ArrayList<SnowFlake> = ArrayList()

        //        val bgHandler: Handler = Handler()
        lateinit var bgHandler: Handler
        lateinit var runnable: Runnable
        var isNotPaused = true

        var job1: Job = Job()

        //lateinit var ttimer : Timer
        var ttimerState = false
//        var bgHandlerState = false

        const val disappear_margin = 32				// pixels from each border where objects disappear
        const val flakes = 25 // total number of flakes
        const val flake_speed_PperS = 30 // flake speed in pixel/second
        const val flake_TX: Float = 1f // max. sec. of flake's constant X-movement on fluttering

        const val flake_XperY: Float = 2f // fluttering movement's max. vx/vy ratio
        const val strom_duration_S = 10.0 // storm duration in seconds: about 1-2 seconds for deceleration
        const val storm_lag_S = 60.0 // no-storm in seconds
        const val storm_YperX: Float = 1/3f // storm's max. vy/vx ratio

//        var refresh_FperS = 20f					// initial frames/second, recalculated.
//        var refresh 	  = 1000/refresh_FperS;	// ms/frame

        var refresh_FperS = 100f					// initial frames/second, recalculated.


        var timer_id = 0
//        var timer_sum = refresh
        var timer_count = 1
        var flake_id	  = 0		// timer id of make_flake_visible

        var flake_speed 	= 0.3f				// flake speed in pixel/frame
//        var flake_visible = false

        var flakeDX  = 0			// X-movement in pixel/frame, caused by storm
        var flakeDY  = 0			// Y-movement in pixel/frame, caused by storm
    }

    var storm_speed 	= 0				// storm speed in pixel/frame
    var storm_YperX_current = storm_YperX;  // storm direction varies each time
    var storm_v_sin     = 0                // storm speed's sine
    var storm_v_cos     = 1                // storm speed's cosine
    var storm_direction = 0				// storm X-direction, -1/0=quiet/+1
    var storm_id    	= 0				// ID of storm timer

    var storm_blowing	= 1				// start with storm=ON

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
//        Log.i(TAG, " screenWidth: $screenWidth, screenHeight: $screenHeight")
//
//        Log.i(TAG, "oncreate refresh: $refresh")

//        bgHandler = Handler()
        setUpSnowEffect()
//        rebuild_speed_timer()
//        move_snow()

        job1 = lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                updateSnowFlakes(10L)
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
//        bgHandler.removeCallbacksAndMessages(null);

//        if (ttimerState) {
//            ttimerState = false
//            ttimer.cancel()
//        }
//        if (bgHandlerState) {
//            bgHandlerState = false
//            bgHandler.removeCallbacksAndMessages(null)
//        bgHandler.removeCallbacks(runnable)
//        }
    }

//    override fun onStop() {
//        super.onStop()
//        bgHandler.removeCallbacks(runnable)
//
//   }

//    lateinit var r: Runnable

    fun setUpSnowEffect() {
        // generate 200 snow flake
        var container: ViewGroup = window.decorView as ViewGroup
        for (i in 0 until 30) {
            snowList.add(
                SnowFlake(
                    baseContext,
                    container,
                    screenWidth.toFloat(),
                    screenHeight.toFloat(),
                    false,
//                    flakeDX,
//                    flakeDY,
//                    flake_speed
                )
            )
        }

        Log.i(TAG, "the size of snowList: ${snowList.size}")

        // setup runnable and postDelay
//        if (bgHandlerState) {
//            bgHandler.removeCallbacksAndMessages(null)
//        } else {
//            r = Runnable {
//                bgHandlerState = true
//                for (snow: SnowFlake in snowList)
//                    snow.update()
//                bgHandler.postDelayed(r, 10)
//            }
//            bgHandler.post(r)
//        }
    }

//    fun move_snow(){
//        val beginn = System.currentTimeMillis()
//        job1 = lifecycleScope.launch {
//            withContext(Dispatchers.Main) {
////                updateSnowFlakes()
//            }
//        }
//        val ende = System.currentTimeMillis()
//        val diff = if (beginn > ende) 1000 + ende - beginn else ende - beginn
//        timer_sum += diff
//        timer_count ++
//
//        Log.i(TAG, "timer_sum: $timer_sum")
//        Log.i(TAG, "timer_count: $timer_count")
//
//        if (timer_count>10) {
//            rebuild_speed_timer()
//        }
//    }

//    fun rebuild_speed_timer() {
//        val old = refresh_FperS
//
//        refresh = floor(timer_sum / timer_count * 2) + 10
//        refresh_FperS = floor(1000 / refresh)
//        flake_speed = flake_speed_PperS / refresh_FperS
//
//        Log.i(TAG, "flake_speed : $flake_speed")
//
//        if (job1.isActive) job1.cancel()
//        job1 = lifecycleScope.launch {
//            withContext(Dispatchers.Main) {
//
//                if (old != refresh_FperS) {
//                    Log.i(TAG, "이건가요?")
//                    var ratio = old / refresh_FperS
//                    for (snow: SnowFlake in snowList) {
////                snow.flakeSX*=ratio
////                snow.flakeVX*=ratio
//                        snow.flakeVY*=ratio
//                        Log.i(TAG, "vy in 이건가요: ${snow.flakeVY}" )
//                    }
//                }
//
////                updateSnowFlakes()
//            }
//        }
//
//
////        r = Runnable {
////            for (snow: SnowFlake in snowList) {
////                snow.update()
////                Log.i(TAG, "hey")
////            }
////            bgHandler.postDelayed(r, 10)
////        }
////        bgHandler.post(r)
//
////        runOnInterval({
////            for (snow: SnowFlake in snowList) {
////                snow.update()
//////                Log.i(TAG, "hey")
////            }
////        }, refresh.toLong())
//
////        runOnInterval({
////            for (snow: SnowFlake in snowList) {
////                snow.update()
//////                Log.i(TAG, "hey")
////            }
////        }, 10)
//
////        val runnable = object : Runnable {
////            override fun run() {
////                for (snow: SnowFlake in snowList) {
////                    snow.update()
////                    Log.i(TAG, "hey")
////                }
////
////                bgHandler.postDelayed(this, 1000)
////            }
////        }
////        bgHandler.post(runnable)
////        bgHandler.removeCallbacks(runnable)
//
////        runOnInterval({
////            for (snow: SnowFlake in snowList) {
////                snow.update()
////                Log.i(TAG, "hey")
////            }
////        }, refresh.toLong())
//
//
////        if (ttimerState) {
////            ttimer.cancel()
////        } else {
////            ttimer = Timer()
////            val ttimerTask = object: TimerTask() {
////                override fun run() {
////                    ttimerState = true
////                    for (snow: SnowFlake in snowList)
////                        snow.update()
////                    Log.i(TAG, "hey")
////                }
////            }
////            ttimer.scheduleAtFixedRate(ttimerTask, 0, refresh.toLong())
////        }
//
////        withContext(Dispatchers.Main) {
////            ttimerState = true
////
////            for (snow: SnowFlake in snowList){
////                snow.update()
////                Log.i(TAG, "hey")
////            }
////
////            delay(10)
////        }
//        Log.i(TAG, "rebuild_speed_timer refresh: $refresh")
//
////        if (old != refresh_FperS) {
////            Log.i(TAG, "이건가요?")
////            var ratio = old / refresh_FperS
////            for (snow: SnowFlake in snowList) {
//////                snow.flakeSX*=ratio
//////                snow.flakeVX*=ratio
////                snow.flakeVY*=ratio
////            }
////        }
//
//        timer_count /= 2
//        timer_sum /= 2
//    }

    suspend fun updateSnowFlakes(delay_refresh: Long){
        while (isNotPaused) {
            for (snow: SnowFlake in snowList){
                snow.update()
//                Log.i(TAG, "hey")
            }
            delay(delay_refresh)
        }
    }


//    inline fun runOnTimeout(crossinline block: () -> Unit, timeoutMillis: Long) {
//        Handler(Looper.getMainLooper()).postDelayed({
//            block()
//        }, timeoutMillis)
//    }

//    inline fun runOnInterval(crossinline block: () -> Unit, interval: Long): Runnable {
//        val runnable = object : Runnable {
//            override fun run() {
//                block()
//                bgHandler.postDelayed(this, interval)
//            }
//        }
//        bgHandler.post(runnable)
////        bgHandler.postDelayed(runnable, interval)
//        return runnable
//    }

//    inline fun runOnInterval(crossinline block: () -> Unit, interval: Long) {
//        val runnable = object : Runnable {
//            override fun run() {
//                block()
//                bgHandler.postDelayed(this, interval)
//            }
//        }
//        bgHandler.post(runnable)
//    }

    class SnowFlake(
        context: Context,
        parent: ViewGroup,
        private val screenW: Float,
        private val screenH: Float,
        private val flake_visible: Boolean,
//        private val flakeDX: Int,
//        private val flakeDY: Int,
//        private val flake_speed: Float
    ) {
//        private val snowRes: IntArray = intArrayOf(R.drawable.snow0, R.drawable.snow1, R.drawable.snow2, R.drawable.snow3, R.drawable.snow4, R.drawable.snow5, R.drawable.snow6)
//        private var iv: ImageView = ImageView(context)

        var snowRes: IntArray
        var iv: ImageView
        var flakeX: Float
        var flakeY: Float
        var flakeSX: Float
        var flakeVX: Float
        var flakeVY: Float
        // 근데 왜 flakeVY는 처음 실행할 때 2.22 얼마얼마인데 activity를 종료하고 다시 실행하면 1.0이 되는거야????
        var flakeVIS: Boolean

//        private var distance = 0.7f // 0.5 ~ 1.0f
//        private var randomParam = 0.0f // -0.3 ~ 0.3 f for some special effects
//        private val fallingSpeed = 6
//        private val windSpeed = 4

        init {
            snowRes = intArrayOf(R.drawable.snow0, R.drawable.snow1, R.drawable.snow2, R.drawable.snow3, R.drawable.snow4, R.drawable.snow5, R.drawable.snow6)
            iv = ImageView(context)

            flakeX = Random().nextFloat() * screenW
            flakeY = Random().nextFloat() * screenH
//            flakeY = 0f
            flakeSX = 0f
            flakeVX = 0f
            flakeVY = 1f
            flakeVIS = flake_visible

//            randomParam = Random().nextFloat() * 0.6f - 0.3f
//            distance = Random().nextFloat() * 0.5f + 0.5f
            iv!!.setBackgroundResource(snowRes!![Random().nextInt(6)])
            parent.addView(iv)

            // size
//            iv!!.layoutParams.height = (screenH * 0.01).toInt()
//            iv!!.layoutParams.width = (screenH * 0.01).toInt()
            iv.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            iv.layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT

        }

        fun update() {
//            flakeX += flakeVX + flakeDX
//            flakeY += flakeVY + flakeDY

            flakeX += flakeVX
            flakeY += flakeVY

            if (flakeY > screenH - disappear_margin) {
                flakeX = Random().nextFloat() * screenW
                flakeY = 0f
                flakeVY = (Random().nextFloat() * flakeVY) + flake_speed

//                Log.i(TAG, "new flakeVY: $flakeVY")
//                Log.i(TAG, "new flake_speed: $flake_speed")

                if (Random().nextFloat() < 0.1) flakeVY *= 2
                if (!flake_visible) flakeVIS = true
            }

//            Log.i(TAG, "flakeVIS: $flakeVIS")
//            Log.i(TAG, "flakeVY: $flakeVY")
//            Log.i(TAG, "flake_speed: $flake_speed")

            flakeSX--
            if (flakeSX <= 0) {
                flakeSX = Random().nextFloat() * refresh_FperS * flake_TX
                flakeVX = (2f*Random().nextFloat()-1f) * flake_XperY * flake_speed
//                flakeSX = Random().nextFloat() * flake_TX
//                flakeVX = (2f * Random().nextFloat() - 1f) * flake_speed
            }

//            Log.i(TAG, "refresh_FperS: $refresh_FperS")

            if (flakeX < -disappear_margin)
                flakeX += screenW.toInt()
            if (flakeX >= screenW - disappear_margin)
                flakeX -= screenW.toInt()

            if (flakeVIS) {
                iv!!.translationX = flakeX
                iv!!.translationY = flakeY
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