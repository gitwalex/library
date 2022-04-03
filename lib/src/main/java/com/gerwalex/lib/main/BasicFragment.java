package com.gerwalex.lib.main;

import static com.gerwalex.lib.main.BasicActivity.ACTIONBARSUBTITLE;
import static com.gerwalex.lib.main.BasicActivity.ACTIONBARTITLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.gerwalex.lib.R;
import com.gerwalex.lib.databinding.RateDailogBinding;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import java.sql.Date;

/**
 * Fragment, welches die RawAppAction aus args liest und belegt.
 */
public abstract class BasicFragment extends Fragment {
    private static final int NOLAYOUT = 0;
    private static final String RATINGACCOMPLISHED = "RATINGACCOMPLISHED";
    private static final String RATINGREJECTED = "RATINGREJECTED";
    /**
     * SharedPreferences werden allen abgeleiteten Fragmenten bereitgestellt
     */
    protected static SharedPreferences prefs;
    public final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            onBackPressed();
        }
    };
    /**
     * Bundle fuer ein Fragment. Wird in onCreate() wiederhergestellt und in OnSaveStateInstance()
     * gesichert.
     */
    protected final Bundle args = new Bundle();
    /**
     * Layout des Fragments
     */
    protected int layout = NOLAYOUT;
    /**
     * Merker, ob der ActionBarSubtitle ueberschrieben wurde.
     */
    private boolean isSavedActionBarSubtitle;
    /**
     * Gemerkter ActionBarSubTitle. Wird in onPause() wiederhergestellt.
     */
    private CharSequence mSavedActionBarSubtitle;

    /**
     * Callback für Zurück-Button aktivieren. Wenn das Callback nicht mehr benötigt wird, @{link
     * RawAppFragment#removeOnBackPressedCallback aufrufen}
     */
    protected void addOnBackPressedCallback() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);
        backPressedCallback.setEnabled(true);
    }

    public void askForRating(String appname) {
        boolean rejected = prefs.getBoolean(RATINGREJECTED, false);
        String accomplished = prefs.getString(RATINGACCOMPLISHED, null);
        if (accomplished == null && !rejected) {
            ReviewManager manager = ReviewManagerFactory.create(requireContext());
            Task<ReviewInfo> request = manager.requestReviewFlow();
            Log.d("gerwalex", "start review ");
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle(getString(R.string.bewerteApp, appname));
                    RateDailogBinding rateDialogView = RateDailogBinding.inflate(getLayoutInflater());
                    builder.setView(rateDialogView.getRoot());
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("gerwalex", "ok, now rating!!: ");
                            // We can get the ReviewInfo object
                            ReviewInfo reviewInfo = task.getResult();
                            Task<Void> flow = manager.launchReviewFlow(requireActivity(), reviewInfo);
                            flow.addOnCompleteListener(task2 -> {
                                prefs.edit()
                                        .putString(RATINGACCOMPLISHED, new Date(System.currentTimeMillis()).toString())
                                        .apply();
                            });
                        }
                    });
                    builder.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onRatingPostponed();
                        }
                    });
                    builder.setNeutralButton(R.string.never, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prefs.edit().putBoolean(RATINGREJECTED, true).apply();
                            onRatingRejected();
                        }
                    });
                    builder.show();
                } else {
                    Log.d("gerwalex", "review unsuccessful: ");
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        }
        Bundle argumente = getArguments();
        if (argumente != null) {
            args.putAll(argumente);
        }
    }

    /**
     * Wird immer gerufen, bevor in Activity backPressed ausgeführt wird.
     */
    public void onBackPressed() {
    }

    /**
     * Setzen der durch setArguments(args) erhaltenen bzw. Ruecksichern der Argumente im Bundle
     * args. Gibt es keine MainAction unter AWLIBACTION, wird MainAction.SHOW verwendet.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            args.putAll(savedInstanceState);
        }
    }

    /**
     * Deregistrierung als OnSharedPreferenceListener, wenn die Klasse eine Instanz von
     * OnSharedPreferenceChangeListener ist. Wiederherstellen eines Subtitles, wenn vorhanden.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (this instanceof SharedPreferences.OnSharedPreferenceChangeListener) {
            prefs.unregisterOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);
        }
        if (isSavedActionBarSubtitle) {
            setSubTitle(mSavedActionBarSubtitle);
        }
    }

    protected void onRatingPostponed() {
    }

    protected void onRatingRejected() {
    }

    /**
     * Ist die Klasse eine Instanz von OnSharedPreferenceChangeListener, wird diese als {@link
     * SharedPreferences#registerOnSharedPreferenceChangeListener} registriert.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (this instanceof SharedPreferences.OnSharedPreferenceChangeListener) {
            prefs.registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);
        }
        String title = args.getString(ACTIONBARTITLE);
        if (title != null) {
            setTitle(title);
        }
        String subTitle = args.getString(ACTIONBARSUBTITLE);
        if (subTitle != null) {
            setSubTitle(subTitle);
        }
    }

    /**
     * Sichern des bereitgestellten Bundles args. Wird in onCreate() wiederhergestellt.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putAll(args);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        String title = args.getString(ACTIONBARTITLE);
        if (title != null) {
            setTitle(title);
        }
        String subTitle = args.getString(ACTIONBARSUBTITLE);
        if (subTitle != null) {
            setSubTitle(subTitle);
        }
    }

    protected void removeOnBackPressedCallback() {
        backPressedCallback.setEnabled(false);
    }

    /**
     * Setzt den SubTitle in der SupportActionBar. Rettet vorher den aktuellen Subtitle, der wird
     * dann in onPause() wiederhergestellt.
     *
     * @param subTitle Text des Subtitles
     */
    public void setSubTitle(CharSequence subTitle) {
        //noinspection ConstantConditions
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            if (!isSavedActionBarSubtitle) {
                mSavedActionBarSubtitle = bar.getSubtitle();
                isSavedActionBarSubtitle = true;
            }
            bar.setSubtitle(subTitle);
        }
        args.putCharSequence(ACTIONBARSUBTITLE, subTitle);
    }

    /**
     * Setzt den SubTitle in der SupportActionBar
     *
     * @param titleResID resID des Titles
     */
    public void setSubTitle(int titleResID) {
        setSubTitle(getString(titleResID));
    }

    /**
     * Setzt den Title in der SupportActionBar
     *
     * @param title Text des STitles
     */
    public void setTitle(CharSequence title) {
        //noinspection ConstantConditions
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(title);
        }
        args.putCharSequence(ACTIONBARTITLE, title);
    }

    /**
     * Setzt den Title in der SupportActionBar
     *
     * @param titleResID resID des Titles
     */
    public void setTitle(int titleResID) {
        setTitle(getString(titleResID));
    }
}