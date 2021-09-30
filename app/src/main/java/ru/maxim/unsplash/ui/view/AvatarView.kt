package ru.maxim.unsplash.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * Custom ImageView that provides rounded image for avatars
 */
class AvatarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {

    private var maskPath: Path? = null
    private val maskPaint = Paint()
    private var cornerRadius = width/2

    init {
        maskPaint.apply {
            setLayerType(LAYER_TYPE_HARDWARE, null)
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            maskPaint.color = Color.TRANSPARENT
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if(canvas?.isOpaque != false) {
            canvas?.saveLayerAlpha(0F, 0F, width.toFloat(), height.toFloat(), 255)
        }
        super.onDraw(canvas)
        if(maskPath != null) {
            canvas?.drawPath(maskPath!!, maskPaint)
        }
    }

    private fun setCornerRadius(newCornerRadius: Int) {
        cornerRadius = newCornerRadius
        generateMaskPath(width, height)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        if (w != oldW || h != oldH) {
            setCornerRadius(w/2)
            generateMaskPath(w, h)
        }
    }

    private fun generateMaskPath(w: Int, h: Int) {
        maskPath = Path().apply {
            addRoundRect(
                RectF(0F, 0F, w.toFloat(), h.toFloat()),
                cornerRadius.toFloat(),
                cornerRadius.toFloat(),
                Path.Direction.CW
            )
            fillType = Path.FillType.INVERSE_WINDING
        }
    }
}