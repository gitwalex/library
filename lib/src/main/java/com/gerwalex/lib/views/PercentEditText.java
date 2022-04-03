package com.gerwalex.lib.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.gerwalex.lib.R;
import com.gerwalex.lib.database.MyConverter;

public class PercentEditText extends AppCompatEditText {
    private InverseBindingListener mBindingListener;
    private float value;

    @InverseBindingAdapter(attribute = "value")
    public static float getValue(PercentEditText view) {
        return view.getValue();
    }

    @BindingAdapter(value = {"value", "valueAttrChanged"}, requireAll = false)
    public static void setValue(PercentEditText view, float value, InverseBindingListener listener) {
        view.setText(MyConverter.convertPercent(value));
        view.mBindingListener = listener;
        view.setValue(value);
    }

    public PercentEditText(@NonNull Context context) {
        this(context, null);
    }

    public PercentEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.style.editTextStyle);
    }

    public PercentEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float percent) {
        if (value != percent) {
            value = percent;
            if (mBindingListener != null) {
                mBindingListener.onChange();
            }
        }
    }
}
