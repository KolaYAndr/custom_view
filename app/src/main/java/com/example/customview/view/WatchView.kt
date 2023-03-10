package com.example.customview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.time.LocalTime
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class WatchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var timeInSeconds = LocalTime.now().toSecondOfDay()
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val timer = Timer()

    init {
        moveWithDelay()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.save()

        //drawing elements
        drawBackground(canvas)
        drawScaleLines(canvas)
        drawHands(canvas)

        canvas?.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val usualSize = 500
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = if (widthMode == MeasureSpec.UNSPECIFIED) usualSize else widthSize
        val height = if (heightMode == MeasureSpec.UNSPECIFIED) usualSize else heightSize
        val size = min(height, width)

        setMeasuredDimension(size, size)
    }


    private fun drawBackground(canvas: Canvas?) {
        canvas?.scale(.5f * width, -.5f * height)
        canvas?.translate(1f, -1f)

        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL

        canvas?.drawCircle(0f, 0f, 1f, paint)

        paint.color = Color.DKGRAY

        canvas?.drawCircle(0f, 0f, 0.85f, paint)
    }

    private fun drawScaleLines(canvas: Canvas?) {
        paint.color = Color.GRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = Constants.secondHandStrokeWidth

        val step = 2 * PI / 60
        val scale = Constants.normalScaleSize

        for (i in 0..60) {
            val x1 = cos(2 * PI - step * i).toFloat()
            val y1 = sin(2 * PI - step * i).toFloat()

            var x2: Float
            var y2: Float

            if (i % 5 == 0) {
                x2 = x1 * Constants.largeScaleSize
                y2 = y1 * Constants.largeScaleSize
            } else {
                x2 = x1 * scale
                y2 = y1 * scale
            }

            canvas?.drawLine(x1, y1, x2, y2, paint)
        }
    }

    private fun drawHands(canvas: Canvas?) {
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE

        drawHourHand(canvas)
        drawMinuteHand(canvas)
        drawSecondHand(canvas)
    }

    private fun drawHourHand(canvas: Canvas?) {
        canvas?.save()

        canvas?.rotate(-(timeInSeconds / 3600.0 * 30).toFloat())

        paint.strokeWidth = Constants.hourHandStrokeWidth
        canvas?.drawLine(0f, 0f, 0f, .6f, paint)

        canvas?.restore()
    }

    private fun drawMinuteHand(canvas: Canvas?) {
        canvas?.save()

        canvas?.rotate(-(timeInSeconds / 60.0 * 6).toFloat())

        paint.strokeWidth = Constants.minuteHandStrokeWidth
        canvas?.drawLine(0f, 0f, 0f, .75f, paint)

        canvas?.restore()
    }

    private fun drawSecondHand(canvas: Canvas?) {
        canvas?.save()

        canvas?.rotate(-timeInSeconds * 6f)

        paint.strokeWidth = Constants.secondHandStrokeWidth
        canvas?.drawLine(0f, 0f, 0f, .9f, paint)

        canvas?.restore()
    }

    private fun moveWithDelay() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timeInSeconds = LocalTime.now().toSecondOfDay()
                invalidate()
            }
        }, 0, 1000)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timer.cancel()
    }

    object Constants {
        const val secondHandStrokeWidth = .01f
        const val hourHandStrokeWidth = .05f
        const val minuteHandStrokeWidth = .03f
        const val normalScaleSize = .95f
        const val largeScaleSize = .95f * .95f
    }

}