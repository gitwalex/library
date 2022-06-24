package com.gerwalex.lib.main

import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.gerwalex.lib.R
import com.gerwalex.lib.databinding.RateDailogBinding
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import java.sql.Date

/**
 * Fragment, welches die RawAppAction aus args liest und belegt. Properties sind Observable (und Bindable)
 */
abstract class BasicFragment : Fragment(), Observable {

    private val mCallbacks by lazy { PropertyChangeRegistry() }
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        synchronized(this) {
            mCallbacks.add(callback)
        }
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        synchronized(this) {
            mCallbacks.remove(callback)
        }
        /**
         * Notifies listeners that all properties of this instance have changed.
         */
    }

    fun notifyChange() {
        synchronized(this) {
            mCallbacks.notifyCallbacks(this, 0, null)
        }
        /**
         * Notifies listeners that a specific property has changed. The getter for the property
         * that changes should be marked with [Bindable] to generate a field in
         * `BR` to be used as `fieldId`.
         *
         * @param fieldId The generated BR id for the Bindable field.
         */
    }

    fun notifyPropertyChanged(fieldId: Int) {
        synchronized(this) {
            mCallbacks.notifyCallbacks(this, fieldId, null)
        }
    }

    protected val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }

    /**
     * Bundle fuer ein Fragment. Wird in onCreate() wiederhergestellt und in OnSaveStateInstance()
     * gesichert.
     */
    val args = Bundle()

    /**
     * Layout des Fragments
     */
    protected var layout = NOLAYOUT

    /**
     * Merker, ob der ActionBarSubtitle ueberschrieben wurde.
     */
    private var isSavedActionBarSubtitle = false

    /**
     * Gemerkter ActionBarSubTitle. Wird in onPause() wiederhergestellt.
     */
    private var mSavedActionBarSubtitle: CharSequence? = null
    fun askForRating(appname: String) {
        val rejected = prefs.getBoolean(RATINGREJECTED, false)
        val accomplished = prefs.getString(RATINGACCOMPLISHED, null)
        if (accomplished == null && !rejected) {
            val manager = ReviewManagerFactory.create(requireContext())
            val request = manager.requestReviewFlow()
            Log.d("gerwalex", "start review ")
            request.addOnCompleteListener { task: Task<ReviewInfo?> ->
                if (task.isSuccessful) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(getString(R.string.bewerteApp, appname))
                    val rateDialogView = RateDailogBinding.inflate(layoutInflater)
                    builder.setView(rateDialogView.root)
                    builder.setPositiveButton(R.string.ok) { dialog, which ->
                        Log.d("gerwalex", "ok, now rating!!: ")
                        // We can get the ReviewInfo object
                        val reviewInfo = task.result
                        val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                        flow.addOnCompleteListener { task2: Task<Void?>? ->
                            prefs
                                .edit()
                                .putString(RATINGACCOMPLISHED, Date(System.currentTimeMillis()).toString())
                                .apply()
                        }
                    }
                    builder.setNegativeButton(R.string.later) { dialog, which -> onRatingPostponed() }
                    builder.setNeutralButton(R.string.never) { dialog, which ->
                        prefs
                            .edit()
                            .putBoolean(RATINGREJECTED, true)
                            .apply()
                        onRatingRejected()
                    }
                    builder.show()
                } else {
                    Log.d("gerwalex", "review unsuccessful: ")
                }
            }
        }
    }

    /**
     * Setzen der durch setArguments(args) erhaltenen bzw. Ruecksichern der Argumente im Bundle
     * args. Gibt es keine MainAction unter AWLIBACTION, wird MainAction.SHOW verwendet.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            args.putAll(savedInstanceState)
        } else {
            arguments?.let {
                args.putAll(it)
            }
        }
    }

    /**
     * Deregistrierung als OnSharedPreferenceListener, wenn die Klasse eine Instanz von
     * OnSharedPreferenceChangeListener ist. Wiederherstellen eines Subtitles, wenn vorhanden.
     */
    override fun onPause() {
        super.onPause()
        if (this is OnSharedPreferenceChangeListener) {
            prefs.unregisterOnSharedPreferenceChangeListener(this)
        }
        if (isSavedActionBarSubtitle) {
            setSubTitle(mSavedActionBarSubtitle)
        }
    }

    protected open fun onRatingPostponed() {}
    protected open fun onRatingRejected() {}

    /**
     * Ist die Klasse eine Instanz von OnSharedPreferenceChangeListener, wird diese als [ ][SharedPreferences.registerOnSharedPreferenceChangeListener] registriert.
     */
    override fun onResume() {
        super.onResume()
        if (this is OnSharedPreferenceChangeListener) {
            prefs.registerOnSharedPreferenceChangeListener(this as OnSharedPreferenceChangeListener)
        }
        args
            .getString(BasicActivity.ACTIONBARTITLE)
            ?.let { setTitle(it) }
        args
            .getString(BasicActivity.ACTIONBARSUBTITLE)
            ?.let { setSubTitle(it) }
    }

    /**
     * Sichern des bereitgestellten Bundles args. Wird in onCreate() wiederhergestellt.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putAll(args)
        super.onSaveInstanceState(outState)
    }

    /**
     * Setzt den SubTitle in der SupportActionBar. Rettet vorher den aktuellen Subtitle, der wird
     * dann in onPause() wiederhergestellt.
     *
     * @param subTitle Text des Subtitles
     */
    fun setSubTitle(subTitle: CharSequence?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.let {
            if (!isSavedActionBarSubtitle) {
                mSavedActionBarSubtitle = it.subtitle
                isSavedActionBarSubtitle = true
            }
            it.subtitle = subTitle
        }
        args.putCharSequence(BasicActivity.ACTIONBARSUBTITLE, subTitle)
    }

    /**
     * Setzt den SubTitle in der SupportActionBar
     *
     * @param titleResID resID des Titles
     */
    fun setSubTitle(titleResID: Int) {
        setSubTitle(getString(titleResID))
    }

    /**
     * Setzt den Title in der SupportActionBar
     *
     * @param title Text des STitles
     */
    fun setTitle(title: CharSequence?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.let {
            it.title = title
            args.putCharSequence(BasicActivity.ACTIONBARTITLE, title)
        }
    }

    /**
     * Setzt den Title in der SupportActionBar
     *
     * @param titleResID resID des Titles
     */
    fun setTitle(titleResID: Int) {
        setTitle(getString(titleResID))
    }

    companion object {

        private const val NOLAYOUT = 0
        private const val RATINGACCOMPLISHED = "RATINGACCOMPLISHED"
        private const val RATINGREJECTED = "RATINGREJECTED"
    }
}