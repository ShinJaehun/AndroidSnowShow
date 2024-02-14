package com.shinjaehun.androidsnowshow.makingcustomview.item

import java.util.*

private const val TAG = "SnowItem"

class SnowItem: AbstractItem() {

    private var radiusSize: Int = 0
    private val random: Random = Random()
    private val fallingLength: Int = random.nextInt(3) + 1
    private val amplitude: Int = random.nextInt(10) + 20
    private val waveLength: Int = random.nextInt(50 * fallingLength) + 50
    private var startPointX: Int = 0

    override var viewHeight: Int
        get() = super.viewHeight
        set(viewHeight) {
            super.viewHeight = viewHeight
            radiusSize = (random.nextInt(20)) + 20
            pointY = - radiusSize
        }

    override var viewWidth: Int
        get() = super.viewWidth
        set(viewWidth) {
            super.viewWidth = viewWidth
            randomSetPointX()
            startPointX = pointX
        }

//    fun setViewHeight2(viewHeight: Int) {
//        super.viewHeight = viewHeight
//        radiusSize = (random.nextInt(20)) + 20
//        pointY = - radiusSize
//    }
//
//    fun setViewWidth2(viewWidth: Int) {
//        super.viewWidth = viewWidth
//        randomSetPointX()
//        startPointX = pointX
//    }

    private fun randomSetPointX(){
        pointX = if (random.nextInt(viewWidth - radiusSize) <= 0) {
            radiusSize + radiusSize
        } else {
            random.nextInt(viewWidth - radiusSize)
        }
    }

    override fun fallDown() {
        var fallDownPoint: Int = pointY + fallingLength
        if (fallDownPoint > viewHeight + radiusSize) {
            fallDownPoint = - radiusSize
            isDisplay = false
            randomSetPointX()
        }
        pointY = fallDownPoint
    }

    override fun drift() {
        var driftPoint: Int
        val angularFrequency: Double = pointY.toDouble() / waveLength
        val movementDistance: Int = (amplitude * Math.sin(angularFrequency)).toInt()
        driftPoint = startPointX + movementDistance
        pointX = driftPoint
    }

    override fun getSize(): Int {
        return radiusSize
    }
}