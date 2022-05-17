package com.gerwalex.lib.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.gerwalex.lib.R
import com.hookedonplay.decoviewlib.DecoView
import com.hookedonplay.decoviewlib.charts.SeriesItem
import com.hookedonplay.decoviewlib.events.DecoEvent
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.io.File

class ImageDecoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : FrameLayout(context, attrs, defStyle) {

    val imageView = ImageView(context, attrs)
    val decoView = DecoView(context, attrs)
    val textView = TextView(context, attrs)

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageDecoView)
        try {
            val borderWidth = a
                .getDimension(R.styleable.ImageDecoView_borderWidth, 0f)
                .toInt()
            if (borderWidth != 0) {
                val borderColor = a.getColor(
                    R.styleable.ImageDecoView_borderColor, ContextCompat.getColor(
                        context,
                        R.color
                            .defaultBorderColor
                    )
                )
                setBorder(borderWidth.toFloat(), borderColor)
            }
            val placeholder = a.getResourceId(R.styleable.ImageDecoView_placeholder, -1)
            if (placeholder != -1) {
                if (isInEditMode) {
                    imageView.setImageResource(placeholder)
                } else {
                    loadImage(placeholder)
                }
            }
            setText(a.getString(R.styleable.ImageDecoView_imageText))
            val textBackground = a.getColor(
                R.styleable.ImageDecoView_imageTextBackground,
                ContextCompat.getColor(context, android.R.color.transparent)
            )
            textView.setBackgroundColor(textBackground)
            //            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } finally {
            a.recycle()
        }
        var lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        lp.gravity = Gravity.CENTER
        imageView.layoutParams = lp
        decoView.layoutParams = lp
        lp = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        textView.setPadding(0, 0, 0, 0)
        textView.layoutParams = lp
        textView.maxLines = 1
        addView(imageView)
        addView(decoView)
        addView(textView)
    }

    fun loadImage(file: File) {
        val request = Picasso
            .get()
            .load(file)
        request.transform(CropCircleTransformation())
        request.into(imageView)
    }

    fun loadImage(@DrawableRes resourceId: Int) {
        val request = Picasso
            .get()
            .load(resourceId)
        request.transform(CropCircleTransformation())
        request.into(imageView)
    }

    fun loadImage(uri: Uri) {
        val request = Picasso
            .get()
            .load(uri)
        request.transform(CropCircleTransformation())
        request.into(imageView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val size = Math.min(width, height)
        setMeasuredDimension(size, size)
    }

    fun setBorder(borderWidth: Float, borderColor: Int) {
        val bw = borderWidth.toInt()
        imageView.setPadding(
            paddingLeft + bw, paddingTop + bw, paddingRight + bw,
            paddingBottom + bw
        )
        val seriesItem = SeriesItem
            .Builder(borderColor)
            .setRange(0f, 100f, 0f)
            .setLineWidth(bw.toFloat())
            .build()
        decoView.addSeries(seriesItem)
        decoView.addEvent(
            DecoEvent
                .Builder(100f)
                .setIndex(0)
                .setDuration(0)
                .build()
        )
    }

    fun setText(@StringRes text: Int) {
        setText(
            context
                .getString(text)
        )
    }

    fun setText(text: CharSequence?) {
        var t = text
        if (isInEditMode) {
            t = "ImageDecoView Text"
        }
        if (!TextUtils.isEmpty(text)) {
            textView.text = t
        }
    }

    fun setTextStyle(tf: Typeface?) {
        textView.typeface = tf
    }
}