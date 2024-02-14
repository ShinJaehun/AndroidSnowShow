package com.shinjaehun.androidsnowshow.makingcustomview.item

import android.graphics.Paint

abstract class AbstractItem {

    val paint: Paint = Paint()
//    val path: Path = Path()
    var pointX: Int = 0
    var pointY: Int = 0
    open var viewHeight: Int = 0
    open var viewWidth: Int = 0
    var isDisplay: Boolean = false

    init {
        paint.strokeWidth = 3.0f
        paint.style = Paint.Style.STROKE
    }

//    open fun setViewHeight(viewHeight: Int) {
//        this.viewHeight = viewHeight
//    }
//
//    open fun setViewWidth(viewWidth: Int) {
//        this.viewWidth = viewWidth
//    }

    abstract fun fallDown()
    abstract fun drift()
    abstract fun getSize(): Int

}