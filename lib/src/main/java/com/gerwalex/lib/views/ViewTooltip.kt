package com.gerwalex.lib.views

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.widget.NestedScrollView
import com.gerwalex.animations.view.Animation
import com.gerwalex.animations.view.AnimationListener
import com.gerwalex.animations.view.FadeInAnimation
import com.gerwalex.animations.view.FadeOutAnimation
import kotlin.math.max

/**
 * Created by florentchampigny on 02/06/2017.
 */
class ViewTooltip {

    private val tooltip_view: TooltipView
    private val view: View
    private var rootView: View? = null

    private constructor(view: View) {
        this.view = view
        tooltip_view = TooltipView(view.context)
        val scrollParent = findScrollParent(view)
        scrollParent?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY -> tooltip_view.translationY = tooltip_view.translationY - (scrollY - oldScrollY) })
    }

    private constructor(rootView: View, view: View) : this(view) {
        this.rootView = rootView
    }

    fun align(align: ALIGN?): ViewTooltip {
        tooltip_view.setAlign(align)
        return this
    }

    fun animation(tooltipAnimation: TooltipAnimation): ViewTooltip {
        tooltip_view.setTooltipAnimation(tooltipAnimation)
        return this
    }

    fun arrowHeight(arrowHeight: Int): ViewTooltip {
        tooltip_view.setArrowHeight(arrowHeight)
        return this
    }

    fun arrowSourceMargin(arrowSourceMargin: Int): ViewTooltip {
        tooltip_view.setArrowSourceMargin(arrowSourceMargin)
        return this
    }

    fun arrowTargetMargin(arrowTargetMargin: Int): ViewTooltip {
        tooltip_view.setArrowTargetMargin(arrowTargetMargin)
        return this
    }

    fun arrowWidth(arrowWidth: Int): ViewTooltip {
        tooltip_view.setArrowWidth(arrowWidth)
        return this
    }

    fun autoHide(autoHide: Boolean, duration: Long): ViewTooltip {
        tooltip_view.setAutoHide(autoHide)
        tooltip_view.setDuration(duration)
        return this
    }

    fun border(color: Int, width: Float): ViewTooltip {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.color = color
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = width
        tooltip_view.setBorderPaint(borderPaint)
        return this
    }

    fun clickToHide(clickToHide: Boolean): ViewTooltip {
        tooltip_view.setClickToHide(clickToHide)
        return this
    }

    fun close() {
        tooltip_view.close()
    }

    fun color(color: Int): ViewTooltip {
        tooltip_view.setColor(color)
        return this
    }

    fun color(paint: Paint): ViewTooltip {
        tooltip_view.setPaint(paint)
        return this
    }

    fun corner(corner: Int): ViewTooltip {
        tooltip_view.setCorner(corner)
        return this
    }

    fun customView(customView: View): ViewTooltip {
        tooltip_view.setCustomView(customView)
        return this
    }

    fun customView(viewId: Int): ViewTooltip {
        tooltip_view.setCustomView((view.context as Activity).findViewById(viewId))
        return this
    }

    fun distanceWithView(distance: Int): ViewTooltip {
        tooltip_view.setDistanceWithView(distance)
        return this
    }

    fun duration(duration: Long): ViewTooltip {
        tooltip_view.setDuration(duration)
        return this
    }

    private fun findScrollParent(view: View): NestedScrollView? {
        return if (view.parent == null || view.parent !is View) {
            null
        } else if (view.parent is NestedScrollView) {
            view.parent as NestedScrollView
        } else {
            findScrollParent(view.parent as View)
        }
    }

    fun margin(left: Int, top: Int, right: Int, bottom: Int): ViewTooltip {
        tooltip_view.setMargin(left, top, right, bottom)
        return this
    }

    fun onDisplay(listener: OnShowListener?): ViewTooltip {
        tooltip_view.setListenerDisplay(listener)
        return this
    }

    fun onHide(listener: OnHideListener?): ViewTooltip {
        tooltip_view.setListenerHide(listener)
        return this
    }

    fun position(position: Position): ViewTooltip {
        tooltip_view.setPosition(position)
        return this
    }

    fun setTextGravity(textGravity: Int): ViewTooltip {
        tooltip_view.setTextGravity(textGravity)
        return this
    }

    fun shadowColor(@ColorInt shadowColor: Int): ViewTooltip {
        tooltip_view.setShadowColor(shadowColor)
        return this
    }

    fun show(): TooltipView {
        val activityContext = tooltip_view.context
        if (activityContext != null && activityContext is Activity) {
            val decorView = if (rootView != null) rootView as ViewGroup else (activityContext
                .window
                .decorView as ViewGroup)
            view.postDelayed({
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                val rootGlobalRect = Rect()
                val rootGlobalOffset = Point()
                decorView.getGlobalVisibleRect(rootGlobalRect, rootGlobalOffset)
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                rect.left = location[0]
                rect.top -= rootGlobalOffset.y
                rect.bottom -= rootGlobalOffset.y
                rect.left -= rootGlobalOffset.x
                rect.right -= rootGlobalOffset.x
                decorView.addView(tooltip_view, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                tooltip_view
                    .viewTreeObserver
                    .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            tooltip_view.setup(rect, decorView.width)
                            tooltip_view
                                .viewTreeObserver
                                .removeOnPreDrawListener(this)
                            return false
                        }
                    })
            }, 100)
        }
        return tooltip_view
    }

    fun text(text: String?): ViewTooltip {
        tooltip_view.setText(text)
        return this
    }

    fun text(@StringRes text: Int): ViewTooltip {
        tooltip_view.setText(text)
        return this
    }

    fun textColor(textColor: Int): ViewTooltip {
        tooltip_view.setTextColor(textColor)
        return this
    }

    fun textSize(unit: Int, textSize: Float): ViewTooltip {
        tooltip_view.setTextSize(unit, textSize)
        return this
    }

    fun textTypeFace(typeface: Typeface?): ViewTooltip {
        tooltip_view.setTextTypeFace(typeface)
        return this
    }

    fun withShadow(withShadow: Boolean): ViewTooltip {
        tooltip_view.setWithShadow(withShadow)
        return this
    }

    enum class ALIGN {
        START, CENTER, END
    }

    enum class Position {
        LEFT, RIGHT, TOP, BOTTOM
    }

    fun interface OnShowListener {

        fun onShow(view: View?)
    }

    fun interface OnHideListener {

        fun onHide(view: View?)
    }

    interface TooltipAnimation {

        fun animateEnter(view: View, animatorListener: AnimationListener? = null)
        fun animateExit(view: View, animatorListener: AnimationListener? = null)
    }

    class ViewTooltipAnimation(private val enterAnimation: Animation, private val exitAnimation: Animation) :
        TooltipAnimation {

        override fun animateEnter(view: View, animatorListener: AnimationListener?) {
            enterAnimation.animate()
            animatorListener?.onAnimationEnd(enterAnimation)
        }

        override fun animateExit(view: View, animatorListener: AnimationListener?) {
            exitAnimation.animate()
            animatorListener?.onAnimationEnd(exitAnimation)
        }
    }

    class FadeTooltipAnimation(private val fadeDuration: Long = 400) : TooltipAnimation {

        override fun animateEnter(view: View, animatorListener: AnimationListener?) {
            val anim = FadeInAnimation(view).apply {
                duration = fadeDuration
                listener = animatorListener
            }
            anim.animate()
        }

        override fun animateExit(view: View, animatorListener: AnimationListener?) {
            val anim = FadeOutAnimation(view).apply {
                duration = fadeDuration
                listener = animatorListener
            }
            anim.animate()
        }
    }

    class TooltipView(context: Context) : FrameLayout(context) {

        protected var childView: View
        var shadowPadding = 4
        var shadowWidth = 8
        private var align: ALIGN? = ALIGN.CENTER
        private var arrowHeight = 15
        private var arrowSourceMargin = 0
        private var arrowTargetMargin = 0
        private var arrowWidth = 15
        private var autoHide = true
        private var borderPaint: Paint?
        private var bubblePaint: Paint
        private var bubblePath: Path? = null
        private var clickToHide = false
        private var color = Color.parseColor("#1F7C82")
        private var corner = 30
        private var distanceWithView = 0
        private var duration: Long = 4000
        private var onShowListener: OnShowListener? = null
        private var onHideListener: OnHideListener? = null
        private var marginBottom = 0
        private var marginLeft = 0
        private var marginRight = 0
        private var marginTop = 0
        private var position = Position.BOTTOM
        private var shadowColor = Color.parseColor("#aaaaaa")
        private var tooltipAnimation: TooltipAnimation = FadeTooltipAnimation()
        private var viewRect: Rect? = null

        init {
            setWillNotDraw(false)
            childView = TextView(context)
            (childView as TextView).setTextColor(Color.WHITE)
            addView(childView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            childView.setPadding(0, 0, 0, 0)
            bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            bubblePaint.color = color
            bubblePaint.style = Paint.Style.FILL
            borderPaint = null
            setLayerType(LAYER_TYPE_SOFTWARE, bubblePaint)
            setWithShadow(true)
        }

        fun adjustSize(rect: Rect, screenWidth: Int): Boolean {
            val r = Rect()
            getGlobalVisibleRect(r)
            var changed = false
            val layoutParams = layoutParams
            if (position == Position.LEFT && width > rect.left) {
                layoutParams.width = rect.left - MARGIN_SCREEN_BORDER_TOOLTIP - distanceWithView
                changed = true
            } else if (position == Position.RIGHT && rect.right + width > screenWidth) {
                layoutParams.width = screenWidth - rect.right - MARGIN_SCREEN_BORDER_TOOLTIP - distanceWithView
                changed = true
            } else if (position == Position.TOP || position == Position.BOTTOM) {
                var adjustedLeft = rect.left
                var adjustedRight = rect.right
                if (rect.centerX() + width / 2f > screenWidth) {
                    val diff = rect.centerX() + width / 2f - screenWidth
                    adjustedLeft -= diff.toInt()
                    adjustedRight -= diff.toInt()
                    setAlign(ALIGN.CENTER)
                    changed = true
                } else if (rect.centerX() - width / 2f < 0) {
                    val diff = -(rect.centerX() - width / 2f)
                    adjustedLeft += diff.toInt()
                    adjustedRight += diff.toInt()
                    setAlign(ALIGN.CENTER)
                    changed = true
                }
                if (adjustedLeft < 0) {
                    adjustedLeft = 0
                }
                if (adjustedRight > screenWidth) {
                    adjustedRight = screenWidth
                }
                rect.left = adjustedLeft
                rect.right = adjustedRight
            }
            setLayoutParams(layoutParams)
            postInvalidate()
            return changed
        }

        fun close() {
            remove()
        }

        fun closeNow() {
            removeNow()
        }

        private fun drawBubble(
            myRect: RectF, topLeftDiameter: Float, topRightDiameter: Float, bottomRightDiameter: Float,
            bottomLeftDiameter: Float,
        ): Path {
            val mTopLeftDiameter = max(0f, topLeftDiameter)
            val mTopRightDiameter = max(0f, topRightDiameter)
            val mBottomRightDiameter = max(0f, bottomRightDiameter)
            val mBottomLeftDiameter = max(0f, bottomLeftDiameter)
            val path = Path()
            viewRect?.let {
                val spacingLeft = if (position == Position.RIGHT) arrowHeight.toFloat() else marginLeft.toFloat()
                val spacingTop = if (position == Position.BOTTOM) arrowHeight.toFloat() else marginTop.toFloat()
                val spacingRight = if (position == Position.LEFT) arrowHeight.toFloat() else marginRight.toFloat()
                val spacingBottom = if (position == Position.TOP) arrowHeight.toFloat() else marginBottom.toFloat()
                val left = spacingLeft + myRect.left
                val top = spacingTop + myRect.top
                val right = myRect.right - spacingRight
                val bottom = myRect.bottom - spacingBottom
                val centerX = it.centerX() - x
                val arrowSourceX = if (listOf(Position.TOP, Position.BOTTOM).contains(position))
                    centerX + arrowSourceMargin else centerX
                val arrowTargetX = if (listOf(Position.TOP, Position.BOTTOM).contains(position))
                    centerX + arrowTargetMargin else centerX
                val arrowSourceY = if (listOf(Position.RIGHT, Position.LEFT).contains(position))
                    bottom / 2f - arrowSourceMargin else bottom / 2f
                val arrowTargetY = if (listOf(Position.RIGHT, Position.LEFT).contains(position))
                    bottom / 2f - arrowTargetMargin else bottom / 2f
                path.moveTo(left + mTopLeftDiameter / 2f, top)
                //LEFT, TOP
                if (position == Position.BOTTOM) {
                    path.lineTo(arrowSourceX - arrowWidth, top)
                    path.lineTo(arrowTargetX, myRect.top)
                    path.lineTo(arrowSourceX + arrowWidth, top)
                }
                path.lineTo(right - mTopRightDiameter / 2f, top)
                path.quadTo(right, top, right, top + mTopRightDiameter / 2)
                //RIGHT, TOP
                if (position == Position.LEFT) {
                    path.lineTo(right, arrowSourceY - arrowWidth)
                    path.lineTo(myRect.right, arrowTargetY)
                    path.lineTo(right, arrowSourceY + arrowWidth)
                }
                path.lineTo(right, bottom - mBottomRightDiameter / 2)
                path.quadTo(right, bottom, right - mBottomRightDiameter / 2, bottom)
                //RIGHT, BOTTOM
                if (position == Position.TOP) {
                    path.lineTo(arrowSourceX + arrowWidth, bottom)
                    path.lineTo(arrowTargetX, myRect.bottom)
                    path.lineTo(arrowSourceX - arrowWidth, bottom)
                }
                path.lineTo(left + mBottomLeftDiameter / 2, bottom)
                path.quadTo(left, bottom, left, bottom - mBottomLeftDiameter / 2)
                //LEFT, BOTTOM
                if (position == Position.RIGHT) {
                    path.lineTo(left, arrowSourceY + arrowWidth)
                    path.lineTo(myRect.left, arrowTargetY)
                    path.lineTo(left, arrowSourceY - arrowWidth)
                }
                path.lineTo(left, top + mTopLeftDiameter / 2)
                path.quadTo(left, top, left + mTopLeftDiameter / 2, top)
                path.close()
            }
            return path
        }

        private fun getAlignOffset(myLength: Int, hisLength: Int): Int {
            return when (align) {
                ALIGN.END -> hisLength - myLength
                ALIGN.CENTER -> (hisLength - myLength) / 2
                else -> 0
            }
        }

        fun getArrowHeight(): Int {
            return arrowHeight
        }

        fun setArrowHeight(arrowHeight: Int) {
            this.arrowHeight = arrowHeight
            postInvalidate()
        }

        fun getArrowSourceMargin(): Int {
            return arrowSourceMargin
        }

        fun setArrowSourceMargin(arrowSourceMargin: Int) {
            this.arrowSourceMargin = arrowSourceMargin
            postInvalidate()
        }

        fun getArrowTargetMargin(): Int {
            return arrowTargetMargin
        }

        fun setArrowTargetMargin(arrowTargetMargin: Int) {
            this.arrowTargetMargin = arrowTargetMargin
            postInvalidate()
        }

        fun getArrowWidth(): Int {
            return arrowWidth
        }

        fun setArrowWidth(arrowWidth: Int) {
            this.arrowWidth = arrowWidth
            postInvalidate()
        }

        protected fun handleAutoRemove() {
            if (clickToHide) {
                setOnClickListener {
                    if (clickToHide) {
                        remove()
                    }
                }
            }
            if (autoHide) {
                postDelayed({ remove() }, duration)
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            if (bubblePath != null) {
                canvas.drawPath(bubblePath!!, bubblePaint)
                if (borderPaint != null) {
                    canvas.drawPath(bubblePath!!, borderPaint!!)
                }
            }
        }

        private fun onSetup(myRect: Rect) {
            setupPosition(myRect)
            bubblePath = drawBubble(RectF(shadowPadding.toFloat(), shadowPadding.toFloat(), width - shadowPadding * 2f,
                height - shadowPadding * 2f), corner.toFloat(), corner.toFloat(), corner.toFloat(), corner.toFloat())
            startEnterAnimation()
            handleAutoRemove()
        }

        override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(width, height, oldw, oldh)
            bubblePath = drawBubble(
                RectF(shadowPadding.toFloat(),
                    shadowPadding.toFloat(),
                    (width - shadowPadding * 2).toFloat(),
                    (height - shadowPadding * 2).toFloat()),
                corner.toFloat(), corner.toFloat(), corner.toFloat(), corner.toFloat())
        }

        fun remove() {
            startExitAnimation()
        }

        fun removeNow() {
            if (parent != null) {
                val parent = parent as ViewGroup
                parent.removeView(this@TooltipView)
            }
        }

        fun setAlign(align: ALIGN?) {
            this.align = align
            postInvalidate()
        }

        fun setAutoHide(autoHide: Boolean) {
            this.autoHide = autoHide
        }

        fun setBorderPaint(borderPaint: Paint?) {
            this.borderPaint = borderPaint
            postInvalidate()
        }

        fun setClickToHide(clickToHide: Boolean) {
            this.clickToHide = clickToHide
        }

        fun setColor(color: Int) {
            this.color = color
            bubblePaint.color = color
            postInvalidate()
        }

        fun setCorner(corner: Int) {
            this.corner = corner
        }

        fun setCustomView(customView: View) {
            removeView(childView)
            childView = customView
            addView(childView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        fun setDistanceWithView(distanceWithView: Int) {
            this.distanceWithView = distanceWithView
        }

        fun setDuration(duration: Long) {
            this.duration = duration
        }

        fun setListenerDisplay(listener: OnShowListener?) {
            onShowListener = listener
        }

        fun setListenerHide(listener: OnHideListener?) {
            onHideListener = listener
        }

        fun setMargin(left: Int, top: Int, right: Int, bottom: Int) {
            marginLeft = left
            marginTop = top
            marginRight = right
            marginBottom = top
            childView.setPadding(childView.paddingLeft + left, childView.paddingTop + top,
                childView.paddingRight + right, childView.paddingBottom + bottom)
            postInvalidate()
        }

        fun setPaint(paint: Paint) {
            bubblePaint = paint
            setLayerType(LAYER_TYPE_SOFTWARE, paint)
            postInvalidate()
        }

        fun setPosition(position: Position) {
            this.position = position
            when (position) {
                Position.TOP -> setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + arrowHeight)
                Position.BOTTOM -> setPadding(paddingLeft, paddingTop + arrowHeight, paddingRight, paddingBottom)
                Position.LEFT -> setPadding(paddingLeft, paddingTop, paddingRight + arrowHeight, paddingBottom)
                Position.RIGHT -> setPadding(paddingLeft + arrowHeight, paddingTop, paddingRight, paddingBottom)
            }
            postInvalidate()
        }

        fun setShadowColor(color: Int) {
            shadowColor = color
            postInvalidate()
        }

        fun setText(text: String?) {
            if (childView is TextView) {
                (childView as TextView).text = Html.fromHtml(text)
            }
            postInvalidate()
        }

        fun setText(text: Int) {
            if (childView is TextView) {
                (childView as TextView).setText(text)
            }
            postInvalidate()
        }

        fun setTextColor(textColor: Int) {
            if (childView is TextView) {
                (childView as TextView).setTextColor(textColor)
            }
            postInvalidate()
        }

        fun setTextGravity(textGravity: Int) {
            if (childView is TextView) {
                (childView as TextView).gravity = textGravity
            }
            postInvalidate()
        }

        fun setTextSize(unit: Int, size: Float) {
            if (childView is TextView) {
                (childView as TextView).setTextSize(unit, size)
            }
            postInvalidate()
        }

        fun setTextTypeFace(textTypeFace: Typeface?) {
            if (childView is TextView) {
                (childView as TextView).typeface = textTypeFace
            }
            postInvalidate()
        }

        fun setTooltipAnimation(tooltipAnimation: TooltipAnimation) {
            this.tooltipAnimation = tooltipAnimation
        }

        fun setWithShadow(withShadow: Boolean) {
            if (withShadow) {
                bubblePaint.setShadowLayer(shadowWidth.toFloat(), 0f, 0f, shadowColor)
            } else {
                bubblePaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
            }
        }

        fun setup(viewRect: Rect?, screenWidth: Int) {
            this.viewRect = Rect(viewRect)
            val myRect = Rect(viewRect)
            val changed = adjustSize(myRect, screenWidth)
            if (!changed) {
                onSetup(myRect)
            } else {
                viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        onSetup(myRect)
                        viewTreeObserver.removeOnPreDrawListener(this)
                        return false
                    }
                })
            }
        }

        fun setupPosition(rect: Rect) {
            val x: Int
            val y: Int
            if (position == Position.LEFT || position == Position.RIGHT) {
                x = if (position == Position.LEFT) {
                    rect.left - width - distanceWithView
                } else {
                    rect.right + distanceWithView
                }
                y = rect.top + getAlignOffset(height, rect.height())
            } else {
                y = if (position == Position.BOTTOM) {
                    rect.bottom + distanceWithView
                } else { // top
                    rect.top - height - distanceWithView
                }
                x = rect.left + getAlignOffset(width, rect.width())
            }
            translationX = x.toFloat()
            translationY = y.toFloat()
        }

        protected fun startEnterAnimation() {
            tooltipAnimation.animateEnter(this) {
                onShowListener?.onShow(this@TooltipView)
            }
        }

        protected fun startExitAnimation() {
            tooltipAnimation.animateExit(this) {
                onHideListener?.onHide(this@TooltipView)
                removeNow()
            }
        }

        companion object {

            private const val MARGIN_SCREEN_BORDER_TOOLTIP = 30
        }
    }

    companion object {

        fun on(view: View): ViewTooltip {
            return ViewTooltip(view)
        }

        fun on(rootView: View, view: View): ViewTooltip {
            return ViewTooltip(rootView, view)
        }
    }
}