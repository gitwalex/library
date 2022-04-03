package com.gerwalex.lib.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.MainThread;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;

import com.gerwalex.lib.database.MyConverter;

import java.sql.Date;

/**
 * TextView fuer Eingabe Datum. Bei Klick wird ein DatePickerDialog gezeigt und eine Aenderung durch
 * dem OnDateSetListener bekanntgegeben
 */
public class DateTextView extends AppCompatTextView {
    @BindingAdapter(value = {"date"}, requireAll = false)
    public static void setDate(DateTextView view, Date date) {
        if (date != null) {
            view.setDate(date);
        }
    }

    public DateTextView(Context context) {
        super(context);
    }

    public DateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            setDate(new Date(System.currentTimeMillis()));
        }
    }

    public void postDate(Date date) {
        post(new Runnable() {
            @Override
            public void run() {
                setDate(date);
            }
        });
    }

    /**
     * Setzt das uebergebene Datum. Die Zeit wird auf 00:00 gesetzt
     *
     * @param date Datum
     */
    @MainThread
    public void setDate(Date date) {
        super.setText(MyConverter.convertDate(date));
    }
}
