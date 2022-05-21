package com.gerwalex.lib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.gerwalex.lib.R;
import com.gerwalex.lib.database.MyConverter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Zeigt einen Betrag in der jeweiligen Waehrung an. Als Defult wird bei negativen Werten der Text
 * in rot gezeigt.
 */
public class CurrencyTextView extends AppCompatTextView {
    private boolean colorMode;
    private int defaultColor;
    private InverseBindingListener mBindingListener;
    private BigDecimal value = new BigDecimal(0);

    @InverseBindingAdapter(attribute = "value")
    public static BigDecimal getBigDecimal(CurrencyTextView view) {
        return view.getBigDecimal();
    }

    @InverseBindingAdapter(attribute = "value")
    public static long getValue(CurrencyTextView view) {
        return view.getValue();
    }

    @BindingAdapter(value = {"value", "valueAttrChanged"}, requireAll = false)
    public static void setBigDecimal(CurrencyTextView view, BigDecimal value, InverseBindingListener listener) {
        view.mBindingListener = listener;
        view.setBigDecimal(value);
    }

    @BindingAdapter(value = {"value", "valueAttrChanged"}, requireAll = false)
    public static void setValue(CurrencyTextView view, long value, InverseBindingListener listener) {
        view.mBindingListener = listener;
        view.setValue(new BigDecimal(value));
    }

    public CurrencyTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CurrencyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CurrencyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public BigDecimal getBigDecimal() {
        return value;
    }

    public void setBigDecimal(BigDecimal value) {
        this.value = value;
    }

    public long getValue() {
        return value.longValue();
    }

    /**
     * Setzt einen long-Wert als Text. Dieser wird in das entsprechende Currency-Format
     * umformatiert.
     *
     * @param amount Wert zur Anzeige
     */
    @CallSuper
    @MainThread
    public void setValue(BigDecimal amount) {
        if (!Objects.equals(value, amount)) {
            value = amount;
            if (mBindingListener != null) {
                mBindingListener.onChange();
            }
            if (colorMode & value.compareTo(BigDecimal.ZERO) < 0) {
                setTextColor(Color.RED);
            } else {
                setTextColor(defaultColor);
            }
            setText(MyConverter.convertCurrency(value));
        }
    }

    public void setValue(long amount) {
        setValue(new BigDecimal(amount));
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CurrencyTextView);
        if (isInEditMode()) {
            value = new BigDecimal(123_456_789L);
        }
        colorMode = a.getBoolean(R.styleable.CurrencyTextView_colorMode, true);
        a.recycle();
        defaultColor = getCurrentTextColor();
        setGravity(Gravity.END);
        setEms(7);
        setText(MyConverter.convertCurrency(value));
        setFocusable(false);
        setCursorVisible(false);
    }

    public void postValue(long value) {
        post(() -> setValue(new BigDecimal(value)));
    }
}
