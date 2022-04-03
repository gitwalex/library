package com.gerwalex.lib.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.CallSuper;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import java.sql.Date;
import java.util.Calendar;

/**
 * TextView fuer Eingabe Datum. Bei Klick wird ein DatePickerDialog gezeigt und eine Aenderung durch
 * dem OnDateSetListener bekanntgegeben
 */
public class DatePickerTextView extends DateTextView
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private InverseBindingListener bindingListener;
    private Date mDate;

    @InverseBindingAdapter(attribute = "date")
    public static Date getDate(DatePickerTextView view) {
        return view.mDate;
    }

    @BindingAdapter(value = {"date", "dateAttrChanged"}, requireAll = false)
    public static void setDate(DatePickerTextView view, Date date, final InverseBindingListener attrChange) {
        if (attrChange != null) {
            view.bindingListener = attrChange;
        }
        if (date != null) {
            view.setDate(date);
        }
    }

    public DatePickerTextView(Context context) {
        super(context);
    }

    public DatePickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatePickerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @return Liefert das aktuell angezeigte Datum zurueck
     */
    public Date getDate() {
        return mDate;
    }

    /**
     * Startet den DatumsDialog, wenn focusable
     */
    @Override
    public void onClick(View v) {
        if (isFocusable()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePickerDialog = new DatePickerDialog(getContext(), this, year, month, day);
            mDatePickerDialog.show();
        }
    }

    /**
     * Wird gerufen, wenn das Datum im Dialog gesetzt wurde. In diesem Fall wird der Listener
     * gerufen, dass sich das Datum geaendert hat.
     */
    @CallSuper
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        setDate(year, monthOfYear, dayOfMonth);
    }

    /**
     * Setzt das uebergebene Datum. Die Zeit wird auf 00:00 gesetzt
     *
     * @param date Datum
     */
    public void setDate(Date date) {
        if (mDate == null || !mDate.equals(date)) {
            mDate = date;
            super.setDate(date);
            if (bindingListener != null) {
                bindingListener.onChange();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        super.setOnClickListener(this);
        setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
    }

    /**
     * Setzt die uebergebenen Werte als Datum
     *
     * @param year  Jahr
     * @param month Monat
     * @param day   Tag des Monats
     */
    public void setDate(int year, int month, int day) {
        Calendar newCal = Calendar.getInstance();
        newCal.set(Calendar.YEAR, year);
        newCal.set(Calendar.MONTH, month);
        newCal.set(Calendar.DAY_OF_MONTH, day);
        setDate(new Date(newCal.getTime().getTime()));
    }

    /**
     * OnItemClickListener wird nicht beachtet.
     *
     * @throws IllegalArgumentException bei jedem Aufruf.
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new IllegalArgumentException("Nicht moeglich");
    }
}
