package com.nikm3.sketchpad

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

class MyCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private var backgroundColor = ResourcesCompat.getColor(resources, R.color.off_white, null)
    private var drawColor = ResourcesCompat.getColor(resources, R.color.black, null)

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }
    private var path = Path()
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * Clear the bitmap on initialization and when the device is rotated
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        reset()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    /**
     * When the screen is interacted with, check which type it is and react accordingly
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    /**
     * On initial touch, create a new path and start it at the touch point
     */
    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    /**
     * As the User moves their finger, draw the path
     */
    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    /**
     * When the User finishes their touch, end the current path
     */
    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
        path.reset()
    }

    /**
     * Return the currently selected paint color
     */
    fun getPaintColor(): Int {
        return paint.color
    }

    /**
     * Set the paint color to the new one selected by the User
     */
    fun setPaintColor(newColor: Int) {
        paint.color = newColor
    }

    /**
     * Return the currently selected background color
     */
    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    /**
     * Set the background color to the new one selected by the User
     */
    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        backgroundColor = color
    }

    /**
     * Make a new bitmap and redraw, effectively erasing the current drawing
     */
    fun reset() {
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        this.setBackgroundColor(backgroundColor)
        invalidate()
    }

    companion object {
        private const val STROKE_WIDTH = 12f // has to be float

    }
}
