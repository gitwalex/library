package com.gerwalex.lib.preferences

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.gerwalex.lib.R
import xyz.aprildown.ultimateringtonepicker.RingtonePickerDialog
import xyz.aprildown.ultimateringtonepicker.UltimateRingtonePicker

class RingTonePreference : Preference {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs,
        defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    private var st: TextView? = null
    var uri: String? = null
        set(value) {
            field = value
            val editor = prefs.edit()
            val text = getTitle(uri)
            text?.run {
                editor.putString(key, field)
                st?.text = text
            } ?: run {
                editor.remove(key)
                st?.text = context.getString(xyz.aprildown.ultimateringtonepicker.R.string.urp_silent_ringtone_title)
            }
            editor.apply()
        }

    private fun getTitle(uri: String?): String? {
        uri?.let {
            val mUri = Uri.parse(uri)
            context.contentResolver
                .query(mUri, null, null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    }
                }
        }
        return null
    }

    init {
        widgetLayoutResource = R.layout.ringtone_preference
        uri = prefs.getString(key, null)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        st = (holder.findViewById(R.id.ringtoneTitle) as TextView).also {
            it.text = getTitle(uri)
        }
    }

    fun openRingtoneDialog(title: String?, ringtoneTypes: List<Int>, fragmentManager: FragmentManager) {
        val preSelectedUris = ArrayList<Uri>()
        uri?.let {
            preSelectedUris.add(Uri.parse(it))
        }
        val settings = UltimateRingtonePicker.Settings(
            preSelectUris = preSelectedUris,
            systemRingtonePicker = UltimateRingtonePicker.SystemRingtonePicker(
                defaultSection = UltimateRingtonePicker.SystemRingtonePicker.DefaultSection(),
                ringtoneTypes = ringtoneTypes
            )
        )
        RingtonePickerDialog
            .createEphemeralInstance(
                settings = settings,
                dialogTitle = title,
                listener = object : UltimateRingtonePicker.RingtonePickerListener {
                    override fun onRingtonePicked(ringtones: List<UltimateRingtonePicker.RingtoneEntry>) {
                        if (ringtones.isNotEmpty()) {
                            ringtones[0].run {
                                this@RingTonePreference.uri = uri.toString()
                            }
                        }
                    }
                }
            )
            .show(fragmentManager, null)
    }
}