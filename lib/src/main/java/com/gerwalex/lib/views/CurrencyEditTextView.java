package com.gerwalex.lib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.gerwalex.lib.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Zeigt einen Betrag in der jeweiligen Waehrung an. Als Defult wird bei negativen Werten der Text
 * in rot gezeigt.
 */
public class CurrencyEditTextView extends AppCompatEditText {
    private final DecimalFormat cf;
    private final int currencyUnits;
    private boolean colorMode;
    private int defaultColor;
    private InverseBindingListener mBindingListener;
    private Long value;

    @InverseBindingAdapter(attribute = "value")
    public static long getValue(CurrencyEditTextView view) {
        return view.getValue();
    }

    @BindingAdapter(value = {"value", "valueAttrChanged"}, requireAll = false)
    public static void setValue(CurrencyEditTextView view, long value, InverseBindingListener listener) {
        view.mBindingListener = listener;
        view.setValue(value);
        view.convertCurrency();
    }

    {
        cf = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.getDefault());
        currencyUnits = (int) Math.pow(10, Currency.getInstance(Locale.getDefault()).getDefaultFractionDigits());
    }

    public CurrencyEditTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CurrencyEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CurrencyEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void convertCurrency() {
        setText(cf.format(value / currencyUnits));
    }

    public long getValue() {
        return value;
    }

    /**
     * Setzt einen long-Wert als Text. Dieser wird in das entsprechende Currency-Format
     * umformatiert.
     *
     * @param amount Wert zur Anzeige
     */
    @CallSuper
    @MainThread
    public void setValue(long amount) {
        if (!Objects.equals(value, amount)) {
            value = amount;
            if (mBindingListener != null) {
                mBindingListener.onChange();
            }
            if (colorMode & value < 0) {
                setTextColor(Color.RED);
            } else {
                setTextColor(defaultColor);
            }
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CurrencyEditTextView);
        try {
            colorMode = a.getBoolean(R.styleable.CurrencyTextView_colorMode, true);
        } finally {
            a.recycle();
        }
        defaultColor = getCurrentTextColor();
        setGravity(Gravity.END);
        setEms(7);
        setInputType(
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        setSelectAllOnFocus(true);
        value = isInEditMode() ? 123_456_789L : 0;
        convertCurrency();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            setText(String.valueOf((float) value / currencyUnits));
        } else {
            convertCurrency();
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (hasFocus()) {
            try {
                float amount = Float.parseFloat(text.toString());
                setValue(TextUtils.isEmpty(text) ? 0 : (long) (amount * currencyUnits));
            } catch (NumberFormatException e) {
                // Kann leer bleiben - View ist als SignedDecimalNumber konfiguriert
            }
        }
    }
}
