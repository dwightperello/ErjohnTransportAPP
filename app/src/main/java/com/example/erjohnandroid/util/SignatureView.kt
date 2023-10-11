package com.example.erjohnandroid.util
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SignatureView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var path = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 5f // Adjust as needed
    }

    init {
        isDrawingCacheEnabled = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }
            else -> return false
        }

        // Redraw the view
        invalidate()
        return true
    }

    fun clear() {
        path.reset()
        invalidate()
    }

    fun isSignaturePresent(): Boolean {
        return !path.isEmpty
    }
}
