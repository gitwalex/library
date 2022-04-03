package com.gerwalex.lib.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import com.gerwalex.lib.databinding.DialogfragmentBinding;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    // TODO: 10.11.2021 style festlegen

    private String checkboxPreferenceKey;
    private int checkboxText;
    private AlertDialog dialog;
    private String message;
    private int messageResID;
    private Drawable msgIcon;
    private DialogInterface.OnClickListener negativBtnClickListener;
    private int negativBtnText;
    private DialogInterface.OnClickListener neutralBtnClickListener;
    private int neutralBtnText;
    private DialogInterface.OnClickListener positivBtnClickListener;
    private int positivBtnText;
    private SharedPreferences prefs;
    private String title;
    private int titleResId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogfragmentBinding binding = DialogfragmentBinding.inflate(getLayoutInflater());
        if (titleResId != 0) {
            builder.setTitle(titleResId);
        } else {
            builder.setTitle(title);
        }
        builder.setView(binding.getRoot());
        if (messageResID != 0) {
            binding.message.setText(messageResID);
        } else {
            binding.message.setText(message);
        }
        if (checkboxPreferenceKey != null) {
            binding.checkBox.setVisibility(View.VISIBLE);
            binding.checkBox.setText(checkboxText);
            binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    prefs.edit().putBoolean(checkboxPreferenceKey, isChecked).apply();
                }
            });
        }
        if (msgIcon != null) {
            binding.dialogIcon.setImageDrawable(msgIcon);
        }
        if (positivBtnClickListener != null) {
            builder.setPositiveButton(positivBtnText, positivBtnClickListener);
        }
        if (negativBtnClickListener != null) {
            builder.setNegativeButton(negativBtnText, negativBtnClickListener);
        }
        if (negativBtnClickListener != null) {
            builder.setNeutralButton(neutralBtnText, neutralBtnClickListener);
        }
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissAllowingStateLoss();
    }

    public void setCheckBox(@StringRes int message, @NonNull String preferenceKey) {
        checkboxText = message;
        checkboxPreferenceKey = preferenceKey;
    }

    public void setMessage(@StringRes int message) {
        messageResID = message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public void setNegativeButton(@StringRes int btnText, @NonNull DialogInterface.OnClickListener onClickListener) {
        negativBtnText = btnText;
        negativBtnClickListener = onClickListener;
    }

    public void setNeutralButton(@StringRes int btnText, @NonNull DialogInterface.OnClickListener onClickListener) {
        neutralBtnText = btnText;
        neutralBtnClickListener = onClickListener;
    }

    public void setPositiveButton(@StringRes int btnText, @NonNull DialogInterface.OnClickListener onClickListener) {
        positivBtnText = btnText;
        positivBtnClickListener = onClickListener;
    }

    public void setTitle(@StringRes int title) {
        titleResId = title;
    }

    private void setTitle(String title) {
        this.title = title;
    }
}
