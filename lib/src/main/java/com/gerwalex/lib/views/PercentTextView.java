package com.gerwalex.lib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.gerwalex.lib.R;
import com.gerwalex.lib.database.MyConverter;

/**
 * Zeigt einen Text als Prozentwert.
 */
public class PercentTextView extends AppCompatTextView {
    protected boolean colorMode = true;
    private int defaultColor;
    private InverseBindingListener mBindingListener;
    private float value;

    @InverseBindingAdapter(attribute = "value")
    public static float getValue(PercentTextView view) {
        return view.getValue();
    }

    @BindingAdapter(value = {"value", "valueAttrChanged"}, requireAll = false)
    public static void setValue(PercentTextView view, float value, InverseBindingListener listener) {
        view.setText(MyConverter.convertPercent(value));
        view.mBindingListener = listener;
        view.setValue(value);
    }

    public PercentTextView(Context context) {
        this(context, null);
    }

    public PercentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PercentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private float getValue() {
        return value;
    }

    public void setValue(float percent) {
        if (value != percent) {
            value = percent;
            if (mBindingListener != null) {
                mBindingListener.onChange();
            }
            if (colorMode & value < 0) {
                setTextColor(Color.RED);
            } else {
                setTextColor(defaultColor);
            }
            String text = MyConverter.convertPercent(value);
            setText(text);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentTextView);
        colorMode = a.getBoolean(R.styleable.PercentTextView_colorMode, true);
        a.recycle();
        defaultColor = getCurrentTextColor();
        setGravity(Gravity.END);
        setEms(5);
        setText(MyConverter.convertPercent(value));
    }
}
