package com.mirza.e_kart.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.mirza.e_kart.R
import com.mirza.e_kart.utils.dpToPx

class LoadingView : View {
    private var TAG = LoadingView::class.java.simpleName
    private var arcPaint: Paint? = null
    private var arcPath: Path? = null
    private var fillProgress: Paint? = null
    private var progressPath: Path? = null
    private var mCircleRectF: RectF? = null
    private var endAngleR = 0f
    private var endAngleL = 0f
    private var progressColor = Color.parseColor("#D9905B")
    internal lateinit var handler: Handler
    private lateinit var thread: HandlerThread


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        obtainAttributes(attrs)
        init()
    }


    private fun obtainAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val a = context.theme.obtainStyledAttributes(
                it,
                R.styleable.LoadingView,
                0, 0
            )

            try {
                progressColor = a.getColor(R.styleable.LoadingView_progress_color, progressColor)
            } finally {
                a.recycle()
            }
        }

    }


    private var valueAnimator: ValueAnimator? = null
    private var runnable: Runnable = Runnable {

        valueAnimator = ValueAnimator.ofFloat(0f, 1f)

        with(valueAnimator!!) {
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            setEvaluator(android.animation.FloatEvaluator())
            addUpdateListener { valueAnimator ->
                val factor = valueAnimator.animatedValue as Float
                invalidate()
                endAngleL = 181 * factor
                endAngleR = -181 * factor
                if (endAngleL < 0.5) {
                    endAngleL = 0.5f
                }
                if (endAngleR > -0.5) {
                    endAngleR = -0.5f
                }
            }
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        refresh()
        start()
    }

    private fun refresh() {
        val width = width.toFloat()
        val height = height.toFloat()

        val widthFactor = width / 4
        val heightFactor = height / 4

        arcPath = Path()
        arcPath!!.moveTo(widthFactor, heightFactor)
        arcPath!!.addCircle(
            (getWidth() / 2).toFloat(),
            (getHeight() / 2).toFloat(),
            (getWidth() / 4).toFloat(),
            Path.Direction.CW
        )
        val center_x: Float = (getWidth() / 2).toFloat()
        val center_y: Float = (getHeight() / 2).toFloat()
        mCircleRectF = RectF()

        mCircleRectF!!.set(
            center_x - getWidth() / 4,
            center_y - getWidth() / 4,
            center_x + getWidth() / 4,
            center_y + getWidth() / 4
        )

        progressPath = Path()
        progressPath!!.addArc(mCircleRectF, 90f, 181f)
        progressPath!!.addArc(mCircleRectF, 181f, 270f)
    }


    fun start() {
        thread = HandlerThread("LoadingViewThread" + System.currentTimeMillis())
        handler = Handler()
        handler.post(runnable)
    }

    fun setColor(color: Int) {
        progressColor = color
        invalidate()
    }


    private fun init() {

        Log.d(TAG, " Init called !")
        arcPaint = Paint()
        arcPaint!!.style = Paint.Style.STROKE
        arcPaint!!.isAntiAlias = true
        arcPaint!!.color = Color.argb(0, 255, 255, 255)
        arcPaint!!.strokeWidth = dpToPx(5).toFloat()
        arcPaint!!.strokeCap = Paint.Cap.ROUND
        arcPaint!!.setShadowLayer(10.0f, 0.0f, 2.0f, R.color.shadow)

        fillProgress = Paint()
        fillProgress!!.style = Paint.Style.STROKE
        fillProgress!!.color = progressColor
        fillProgress!!.strokeWidth = dpToPx(5).toFloat()
        fillProgress!!.strokeCap = Paint.Cap.ROUND
        fillProgress!!.isAntiAlias = true
        fillProgress!!.setShadowLayer(10.0f, 0.0f, 2.0f, R.color.shadow)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(progressPath!!, arcPaint!!)
        canvas.drawArc(mCircleRectF!!, 90f, endAngleL, false, fillProgress!!)
        canvas.drawArc(mCircleRectF!!, 90f, endAngleR, false, fillProgress!!)
    }

}