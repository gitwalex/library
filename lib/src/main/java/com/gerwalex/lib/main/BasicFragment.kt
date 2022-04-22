package com.gerwalex.lib.main

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.gerwalex.lib.R
import com.gerwalex.lib.databinding.RateDailogBinding
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.sql.Date
import kotlin.coroutines.CoroutineContext

/**
 * Fragment, welches die RawAppAction aus args liest und belegt.
 */
abstract class BasicFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job

    val backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }
    protected lateinit var prefs: SharedPreferences

    /**
     * Bundle fuer ein Fragment. Wird in onCreate() wiederhergestellt und in OnSaveStateInstance()
     * gesichert.
     */
    @JvmField
    protected val args = Bundle()

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

    /**
     * Callback für Zurück-Button aktivieren. Wenn das Callback nicht mehr benötigt wird, @{link
     * RawAppFragment#removeOnBackPressedCallback aufrufen}
     */
    protected fun addOnBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
        backPressedCallback.isEnabled = true
    }

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        arguments?.apply { args.putAll(arguments) }

    }

    /**
     * Wird immer gerufen, bevor in Activity backPressed ausgeführt wird.
     */
    fun onBackPressed() {}

    /**
     * Cancelt eventuell laufende Jobs
     */
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    /**
     * Setzen der durch setArguments(args) erhaltenen bzw. Ruecksichern der Argumente im Bundle
     * args. Gibt es keine MainAction unter AWLIBACTION, wird MainAction.SHOW verwendet.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        if (savedInstanceState != null) {
            args.putAll(savedInstanceState)
        }
    }

    /**
     * Deregistrierung als OnSharedPreferenceListener, wenn die Klasse eine Instanz von
     * OnSharedPreferenceChangeListener ist. Wiederherstellen eines Subtitles, wenn vorhanden.
     */
    override fun onPause() {
        super.onPause()
        if (this is OnSharedPreferenceChangeListener) {
            prefs.unregisterOnSharedPreferenceChangeListener(this as OnSharedPreferenceChangeListener)
        }
        if (isSavedActionBarSubtitle) {
            setSubTitle(mSavedActionBarSubtitle)
        }
    }

    protected fun onRatingPostponed() {}
    protected fun onRatingRejected() {}

    /**
     * Ist die Klasse eine Instanz von OnSharedPreferenceChangeListener, wird diese als [ ][SharedPreferences.registerOnSharedPreferenceChangeListener] registriert.
     */
    override fun onResume() {
        super.onResume()
        if (this is OnSharedPreferenceChangeListener) {
            prefs.registerOnSharedPreferenceChangeListener(this as OnSharedPreferenceChangeListener)
        }
        val title = args.getString(BasicActivity.ACTIONBARTITLE)
        title?.let { setTitle(it) }
        val subTitle = args.getString(BasicActivity.ACTIONBARSUBTITLE)
        subTitle?.let { setSubTitle(it) }
    }

    /**
     * Sichern des bereitgestellten Bundles args. Wird in onCreate() wiederhergestellt.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putAll(args)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        val title = args.getString(BasicActivity.ACTIONBARTITLE)
        title?.let { setTitle(it) }
        val subTitle = args.getString(BasicActivity.ACTIONBARSUBTITLE)
        subTitle?.let { setSubTitle(it) }
    }

    protected fun removeOnBackPressedCallback() {
        backPressedCallback.isEnabled = false
    }

    /**
     * Setzt den SubTitle in der SupportActionBar. Rettet vorher den aktuellen Subtitle, der wird
     * dann in onPause() wiederhergestellt.
     *
     * @param subTitle Text des Subtitles
     */
    fun setSubTitle(subTitle: CharSequence?) {
        val bar = (activity as AppCompatActivity?)!!.supportActionBar
        if (bar != null) {
            if (!isSavedActionBarSubtitle) {
                mSavedActionBarSubtitle = bar.subtitle
                isSavedActionBarSubtitle = true
            }
            bar.subtitle = subTitle
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
        val bar = (activity as AppCompatActivity?)!!.supportActionBar
        if (bar != null) {
            bar.title = title
        }
        args.putCharSequence(BasicActivity.ACTIONBARTITLE, title)
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

        /**
         * SharedPreferences werden allen abgeleiteten Fragmenten bereitgestellt
         */
    }
}