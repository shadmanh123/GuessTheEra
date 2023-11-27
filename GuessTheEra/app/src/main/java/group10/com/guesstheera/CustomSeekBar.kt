package group10.com.guesstheera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat

//class created with sole help of ChatGPT, to create a seekbar that works will all android phone sizes, as it custom draws the seekbar
//Image of conversation: https://gyazo.com/9d2239a678c0263c3056cefcc924b5fd
class CustomSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.seekBarStyle
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    private val notchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coffee) // Replace with your actual color reference
        strokeWidth = 10f // Set the desired notch width
    }

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coffee) // Replace with your actual color reference
        strokeWidth = 15f // Set the desired bar height
    }

    // This creates 13 notches for the 12 intervals (1/12 increments)
    private val notchPositions = FloatArray(13) { index -> index / 12f }

    init {
        // Set the progress and secondary progress drawables to a transparent color
        progressDrawable = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
    }

    override fun onDraw(canvas: Canvas) {
        val width = width - paddingLeft - paddingRight
        val notchHeight = 50f // Set the desired notch height
        val barHeight = 3f

        // Draw notches at 1/12 increments
        notchPositions.forEach { position ->
            val x = paddingLeft + position * width
            // Draw the line for each notch
            canvas.drawLine(x, (height / 2 - notchHeight / 2), x, (height / 2 + notchHeight / 2), notchPaint)
        }

        canvas.drawLine(
            paddingLeft.toFloat(),
            (height / 2).toFloat(),
            (width + paddingLeft).toFloat(),
            (height / 2).toFloat(),
            barPaint
        )

        super.onDraw(canvas)
    }
}
