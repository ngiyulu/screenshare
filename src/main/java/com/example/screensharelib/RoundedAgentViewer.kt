package com.example.screensharelib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import net.glance.android.SessionView

class RoundedAgentViewer : SessionView {
    private val path = Path()

    constructor(context: Context) : super(context) {
        configure()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        configure()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        configure()
    }

    private fun configure() {
        //this.setScaleType(ImageView.ScaleType.CENTER_CROP)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // compute the path
        val halfWidth = w / 2f
        val halfHeight = h / 2f
        path.reset()
        path.addCircle(halfWidth, halfHeight, Math.min(halfWidth, halfHeight), Path.Direction.CW)
        path.close()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }
}
