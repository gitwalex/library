package com.gerwalex.lib.main;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;

public class BasicViewModel extends AndroidViewModel {
    public final ObservableBoolean finish = new ObservableBoolean(false);

    public BasicViewModel(@NonNull Application application) {
        super(application);
    }

    public Resources getResources() {
        return getApplication().getResources();
    }

    public String getString(@StringRes int resId) {
        return getResources().getString(resId);
    }
}
