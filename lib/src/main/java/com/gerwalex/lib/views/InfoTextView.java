package com.gerwalex.lib.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.gerwalex.lib.R;

import java.util.Objects;

/**
 * Eine TextView mit einem Hinweis-Icon. Bei Click wird ein PopupWindow (default) oder ein Dilaog mit einem Infotext
 * gezeigt.
 * Der Text kann im xml durch das Attribut 'infoText' festgelegt werden, im Code durch
 * {@link InfoTextView#setInfoText(int)} oder {@link InfoTextView#setInfoText(String)}
 * <p>
 * Die Breite des Popup-Textes wird im Attribut 'textWidth' festgelegt. Default: 100dp.
 * <p>
 * Der Abstand zwischen Icon und Text wird durc 'iconPadding' festgelegt. Default: 8dp
 * Ein Dialog kaann durch das Attribut 'app:infoType=dailog' erzwungen werden.
 */
public class InfoTextView extends AppCompatTextView {
    private final InfoType infoType;
    private String infoText;
    private PopupWindow popup;
    private int popupWidth;
    private AppCompatTextView textView;

    public InfoTextView(@NonNull Context context) {
        this(context, null);
    }

    public InfoTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.style.Gerwalex_InfoTextViewStyle);
    }

    public InfoTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        infoText = getResources().getString(R.string.defaultInfoText);
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.InfoTextView, R.attr.infoTextViewStyle,
                        R.style.Gerwalex_InfoTextViewStyle);
        try {
            int infotext_padding = (int) a.getDimension(R.styleable.InfoTextView_iconPadding, -1);
            setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_info_outline_24dp, 0, 0, 0);
            setCompoundDrawablePadding(infotext_padding);
            if (a.hasValue(R.styleable.InfoTextView_infoText)) {
                infoText = a.getString(R.styleable.InfoTextView_infoText);
            }
            infoType = InfoType.values()[a.getInt(R.styleable.InfoTextView_infoType, 0)];
            if (infoType == InfoType.PopUp) {
                textView = new AppCompatTextView(context, attrs);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setBackgroundColor(ContextCompat.getColor(context, R.color.infotext_popupbackground));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                popupWidth = (int) a.getDimension(R.styleable.InfoTextView_popupWidth, -1);
            }
            // TODO: 10.11.2021 PopupWindow auslagern
        } finally {
            a.recycle();
        }
        setClickable(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (popup != null) {
            popup.dismiss();
        }
    }

    @Override
    public boolean performClick() {
        if (infoType == InfoType.PopUp) {
            showPopup();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setIcon(R.drawable.ic_info_outline_24dp);
            builder.setTitle(R.string.infoDialogTitle);
            builder.setMessage(infoText);
            builder.create().show();
        }
        return super.performClick();
    }

    public void setInfoText(@NonNull String text) {
        infoText = Objects.requireNonNull(text);
    }

    public void setInfoText(@StringRes int text) {
        infoText = getResources().getString(text);
    }

    private void showPopup() {
        textView.setText(infoText);
        popup = new PopupWindow(popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popup = null;
            }
        });
        popup.setOutsideTouchable(true);
        popup.setContentView(textView);
        int[] loc_int = new int[2];
        getLocationOnScreen(loc_int);
        Rect location = new Rect();
        location.left = loc_int[0] - popupWidth;
        location.top = loc_int[1];
        location.right = loc_int[0];
        location.bottom = loc_int[1] + getHeight();
        popup.showAtLocation(this, Gravity.TOP | Gravity.LEFT, location.left, location.bottom);
    }

    public enum InfoType {
        PopUp, Dailog
    }
}