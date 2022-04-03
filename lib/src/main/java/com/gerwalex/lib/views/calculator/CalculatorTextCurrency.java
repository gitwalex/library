package com.gerwalex.lib.views.calculator;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.gerwalex.lib.R;
import com.gerwalex.lib.views.CurrencyTextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Zeigt einen Betrag in der jeweiligen Waehrung an. Als Defult wird bei negativen Werten der Text
 * in rot gezeigt.
 */
public class CalculatorTextCurrency extends CurrencyTextView
        implements CalculatorView.ResultListener, View.OnClickListener {
    public static final long currencyUnits = 100;
    private PopupWindow calculatorPopUp;
    private BigDecimal initialValue;
    private OnClickListener mOnClickListener;
    private Long oldAmount;

    public CalculatorTextCurrency(Context context) {
        super(context);
        if (isInEditMode()) {
            showCalculator();
        }
    }

    public CalculatorTextCurrency(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.editTextStyle);
        if (isInEditMode()) {
            showCalculator();
        }
    }

    public CalculatorTextCurrency(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            showCalculator();
        }
    }

    public void hideCalculator() {
        if (calculatorPopUp.isShowing()) {
            calculatorPopUp.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
        if (calculatorPopUp.isShowing()) {
            hideCalculator();
        } else {
            showCalculator();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        calculatorPopUp.dismiss();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //        setFocusable(true);
        setClickable(true);
        super.setOnClickListener(this);
        setFocusableInTouchMode(true);
        CalculatorView mCalculator = new CalculatorView(getContext());
        if (initialValue != null) {
            mCalculator.setInitialValue(initialValue);
        }
        mCalculator.setResultListener(this);
        calculatorPopUp =
                new PopupWindow(mCalculator, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        calculatorPopUp.setClippingEnabled(true);
        calculatorPopUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setTypeface(Typeface.DEFAULT);
            }
        });
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            showCalculator();
        } else {
            hideCalculator();
        }
    }

    @Override
    public void onResultChanged(BigDecimal result) {
        double res = result.doubleValue() * 100;
        double erg;
        if (res > 0) {
            erg = res + 0.5;
        } else {
            erg = res - 0.5;
        }
        setValue((long) erg);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    /**
     * Setzt einen long-Wert als Text. Dieser wird in das entsprechende Currency-Format
     * umformatiert.
     *
     * @param amount Wert zur Anzeige
     */
    @Override
    public void setValue(long amount) {
        if (!isInEditMode()) {
            if (!Objects.equals(oldAmount, amount)) {
                oldAmount = amount;
                super.setValue(amount);
                initialValue = new BigDecimal(amount).divide(new BigDecimal(currencyUnits), RoundingMode.HALF_UP);
            }
        } else {
            super.setValue(amount);
        }
    }

    public void showCalculator() {
        if (hasFocus() && !calculatorPopUp.isShowing()) {
            int[] loc_int = new int[2];
            getLocationOnScreen(loc_int);
            Rect location = new Rect();
            location.left = loc_int[0];
            location.top = loc_int[1];
            location.right = location.left + getWidth();
            location.bottom = location.top + getHeight();
            calculatorPopUp.setOutsideTouchable(true);
            calculatorPopUp.showAtLocation(this, Gravity.TOP | Gravity.START, location.left, location.bottom);
            setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
}
