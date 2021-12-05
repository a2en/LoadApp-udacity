package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText: String
    private var valueAnimator = ValueAnimator()
    private var textSize: Float = resources.getDimension(R.dimen.default_text_size)
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.default_text_size)
    }
    private var progressWidth = 0f
    private var progressCircle = 0f

    private var buttonBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var loadingColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var circleColor = ContextCompat.getColor(context, R.color.colorAccent)
    private var circleRectF = RectF(0f, 0f, textSize, textSize)
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Clicked -> {

            }
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.duration = 3000
                valueAnimator.addUpdateListener { animation ->
                    progressWidth = animation.animatedValue as Float
                    progressCircle = (widthSize.toFloat()/365)*progressWidth
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        progressWidth = 0f
                        if(buttonState == ButtonState.Loading){
                            buttonState = ButtonState.Loading
                        }
                    }
                })
                valueAnimator.start()

            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                progressWidth = 0f
                progressCircle = 0f
                buttonText = resources.getString(R.string.download)
                invalidate()
            }
        }
    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {

            try {
                buttonText = getString(R.styleable.LoadingButton_text).toString()
                buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, context.getColor(R.color.colorPrimary));
            } finally {
                recycle()
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // draw background
        paint.color = buttonBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        // draw progress
        paint.color = loadingColor
        canvas?.drawRect(0f, 0f, progressWidth, heightSize.toFloat(), paint)

        // draw text
        paint.color = Color.WHITE
        val textWidth = paint.measureText(buttonText);
        canvas?.drawText(buttonText, widthSize / 2 - textWidth / 2, heightSize / 2 - (paint.descent() + paint.ascent()) / 2, paint)

        //Draw circle progress animation
        canvas?.save()
        canvas?.translate(widthSize / 2 + textWidth / 2 + (textSize / 2), heightSize / 2 - textSize / 2)
        paint.color = circleColor
        canvas?.drawArc(circleRectF, 0F, progressCircle * 0.365f, true,  paint)
        canvas?.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setLoadingState(state: ButtonState) {
        buttonState = state
    }

}