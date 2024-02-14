package com.shinjaehun.androidsnow.makingcustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.shinjaehun.androidsnow.R
import com.shinjaehun.androidsnow.makingcustomview.item.AbstractItem
import com.shinjaehun.androidsnow.makingcustomview.item.SnowItem
import java.util.*

private const val TAG = "EffectView"

class EffectView(
    context: Context?,
    attrs: AttributeSet? = null // 헐 이 줄 없으면 요상한 오류가 발생함!!
) : View(context) {
    private val items: MutableList<AbstractItem> = arrayListOf()
    private val random: Random = Random()
    private var snowDrawable: Drawable = resources.getDrawable(R.drawable.ic_snowflake)

    init {
        for (i in 0 until 30) {
            items.add(SnowItem())
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        for (item in items) {
            item.viewWidth = right
            item.viewHeight = bottom
        }
        postDelayed(object : Runnable{
            override fun run() {
                val randomInteger: Int = random.nextInt(2) + 3
                var index: Int = 0

                for (item in items) {
                    if (index >= randomInteger) {
                        break
                    }
                    if (!item.isDisplay) {
                        item.isDisplay = true
                        index++
                    }
                }
                postDelayed(this, 3000)
            }
        }, 10)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (item in items) {
            if (item.isDisplay) {
                snowDrawable.bounds = Rect(
                    item.pointX,
                    item.pointY,
                    item.pointX + item.getSize(),
                    item.pointY + item.getSize()
                )
                snowDrawable.draw(canvas)
                item.fallDown()
                item.drift()
            }
        }
        Log.i(TAG, "the size of items: ${items.size}")

        postInvalidateDelayed(15)
    }
}