package com.gerwalex.lib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.gerwalex.lib.R;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ImageDecoView extends FrameLayout {
    private static final int defaultBorderColor = Color.parseColor("#FFE2E2E2");
    public final DecoView decoView;
    private final ImageView imageView;
    private final TextView textView;

    @BindingAdapter(value = "image")
    public static void loadImage(@NonNull ImageDecoView view, @NonNull String filename) {
        ImageDecoView.loadImage(view, new File(filename));
    }

    @BindingAdapter(value = "image")
    public static void loadImage(@NonNull ImageDecoView view, @NonNull Uri uri) {
        RequestCreator request = Picasso.get()
                                        .load(uri);
        request.transform(new CropCircleTransformation());
        request.into(view.imageView);
    }

    @BindingAdapter(value = "image")
    public static void loadImage(@NonNull ImageDecoView view, @DrawableRes int resourceId) {
        RequestCreator request = Picasso.get()
                                        .load(resourceId);
        request.transform(new CropCircleTransformation());
        request.into(view.imageView);
    }

    @BindingAdapter(value = "image")
    public static void loadImage(@NonNull ImageDecoView view, @NonNull File file) {
        RequestCreator request = Picasso.get()
                                        .load(file);
        request.transform(new CropCircleTransformation());
        request.into(view.imageView);
    }

    public ImageDecoView(@NonNull Context context) {
        this(context, null);
    }

    public ImageDecoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageDecoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageView = new ImageView(context, attrs);
        decoView = new DecoView(context, attrs);
        textView = new TextView(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageDecoView);
        try {
            int borderWidth = (int) a.getDimension(R.styleable.ImageDecoView_borderWidth, 0);
            if (borderWidth != 0) {
                imageView.setPadding(getPaddingLeft() + borderWidth, getPaddingTop() + borderWidth,
                        getPaddingRight() + borderWidth, getPaddingBottom() + borderWidth);
                int borderColor = a.getColor(R.styleable.ImageDecoView_borderColor, defaultBorderColor);
                setBorder(borderWidth, borderColor);
            }
            int placeholder = a.getResourceId(R.styleable.ImageDecoView_placeholder, -1);
            if (placeholder != -1) {
                loadImage(this, placeholder);
            }
            setText(a.getString(R.styleable.ImageDecoView_imageText));
            int textBackground = a.getColor(R.styleable.ImageDecoView_imageTextBackground,
                    ContextCompat.getColor(context, android.R.color.transparent));
            textView.setBackgroundColor(textBackground);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } finally {
            a.recycle();
        }
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        imageView.setLayoutParams(lp);
        decoView.setLayoutParams(lp);
        lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        textView.setPadding(0, 0, 0, 0);
        textView.setLayoutParams(lp);
        textView.setMaxLines(1);
        addView(imageView);
        addView(decoView);
        addView(textView);
    }

    private void setBorder(int borderWidth, int color) {
        SeriesItem seriesItem = new SeriesItem.Builder(color).setRange(0, 100, 0)
                                                             .setLineWidth(borderWidth)
                                                             .build();
        decoView.addSeries(seriesItem);
        decoView.addEvent(new DecoEvent.Builder(100).setIndex(0)
                                                    .setDuration(0)
                                                    .build());
    }

    public void setText(@StringRes int text) {
        setText(getContext().getString(text));
    }

    public void setText(CharSequence text) {
        if (isInEditMode()) {
            text = "ImageDecoView Text";
        }
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }
}
