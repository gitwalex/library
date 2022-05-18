package com.gerwalex.demo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.gerwalex.demo.R;
import com.gerwalex.demo.databinding.FragmentImageDecoViewBinding;
import com.gerwalex.lib.main.ActivityResultWrapper;
import com.gerwalex.lib.main.BasicFragment;

public class FragmentImageDecoView extends BasicFragment {
    private static final String ARCCOLOR = "ARCCOLOR";
    private static final String LINEWIDTH = "LINEWIDTH";
    private static final String PLACEHOLDER = "PLACEHOLDER";
    protected ActivityResultWrapper<Intent, ActivityResult> activityLauncher =
            ActivityResultWrapper.registerActivityForResult(this);
    protected FragmentImageDecoViewBinding binding;
    private int borderColor;
    private float borderWidth;
    private @DrawableRes
    int placeholder;

    public int getBorderColor() {
        return borderColor;
    }

    private int getDefaultArcColor(Context context) {
        return ResourcesCompat.getColor(context.getResources(), R.color.teal_200, context.getTheme());
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeholder = requireArguments().getInt(PLACEHOLDER, com.gerwalex.lib.R.drawable.img);
        borderColor = requireArguments().getInt(ARCCOLOR, getDefaultArcColor(requireContext()));
        borderWidth = requireArguments().getFloat(LINEWIDTH, 30f);
    }

    @CallSuper
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImageDecoViewBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a = context.obtainStyledAttributes(attrs, com.gerwalex.lib.R.styleable.ImageDecoView);
        try {
            borderColor =
                    a.getColor(com.gerwalex.lib.R.styleable.ImageDecoView_borderColor, getDefaultArcColor(context));
            placeholder = a.getResourceId(com.gerwalex.lib.R.styleable.ImageDecoView_placeholder, 0);
            borderWidth = a.getDimension(com.gerwalex.lib.R.styleable.ImageDecoView_borderWidth, 30f);
        } finally {
            a.recycle();
        }
        requireArguments().putInt(PLACEHOLDER, placeholder);
        requireArguments().putInt(ARCCOLOR, borderColor);
        requireArguments().putFloat(LINEWIDTH, borderWidth);
    }
    
}
