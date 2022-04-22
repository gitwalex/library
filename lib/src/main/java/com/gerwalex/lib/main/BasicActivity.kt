package com.gerwalex.lib.main

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Basis-Activity  mit einem simplen Layout. Stellt ein Bundle args mit den extras aus Intent
 * bereit.
 */
abstract class BasicActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job

    /**
     * Bundle fuer Argumente. Wird in SaveStateInstance gesichert und in onCreate
     * wiederhergestellt.
     */
    protected val args = Bundle()
    protected lateinit var prefs: SharedPreferences
    private lateinit var handler: Handler

    /**
     * Hides a Keyboard
     *
     * @see "stackoverflow.com/questions/1109022/close-hide-the-android-soft -keyboard"
     */
    fun hide_keyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Cancelt eventuell laufende Jobs
     */
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /**
     * Bereitstellen der Argumente aus Intent bzw savedStateInstance in args. Ist in den Args ein
     * String unter "NEXTACTIVIT" vorhanden, wird diese Activity nachgestartet Setzen der View
     *
     *
     * @param savedInstanceState SavedState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (savedInstanceState != null) {
            args.putAll(savedInstanceState)
        } else {
            val extras = intent
                .extras
            if (extras != null) {
                args.putAll(extras)
            }
        }
    }

    /**
     * Setzt den Subtitle aus args neu
     */
    override fun onResume() {
        super.onResume()
        setSubTitle(args.getString(ACTIONBARSUBTITLE))
    }

    /**
     * Sicherung aller Argumente
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putAll(args)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Startet ein Runnable verzoegert auf dem MainThread
     *
     * @param r           Runnable
     * @param delayMillis Verzoegerung in Millis
     */
    fun postOnUiThread(r: Runnable, delayMillis: Long) {
        handler = Handler(mainLooper)
        handler.postDelayed(r, delayMillis)
    }

    /**
     * Setzt den SubTitle in der SupportActionBar
     *
     * @param subTitleResID resID des Subtitles
     */
    fun setSubTitle(@StringRes subTitleResID: Int) {
        setSubTitle(getString(subTitleResID))
    }

    /**
     * Setzt den SubTitle in der SupportActionBar, wenn vorhanden
     *
     * @param subTitle Text des Subtitles
     */
    fun setSubTitle(subTitle: CharSequence?) {
        actionBar?.let {
            it.subtitle = subTitle
        }
        args.putCharSequence(ACTIONBARSUBTITLE, subTitle)
    }

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
    }

    companion object {
        const val ACTIONBARSUBTITLE = "ACTIONBARSUBTITLE"
        const val ACTIONBARTITLE = "ACTIONBARTITLE"
    }
}