package com.dailystudio.devbricksx.preference

import android.content.*
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn

/**
 * Data class representing a preference change event.
 *
 * @property appPrefs The [AbsPrefs] instance where the change occurred.
 * @property prefKey The key of the preference that changed.
 */
data class PrefsChange(val appPrefs: AbsPrefs,
                       val prefKey: String)

/**
 * Abstract base class for managing SharedPreferences.
 *
 * It provides typed accessors and mutators for preferences, and notifies observers about changes.
 */
abstract class AbsPrefs {

    /**
     * LiveData emitting preference changes.
     */
    val prefsChange: MutableLiveData<PrefsChange> = MutableLiveData()

    /**
     * SharedFlow emitting preference changes.
     */
    val prefsChanges: MutableSharedFlow<PrefsChange> = MutableSharedFlow<PrefsChange>(replay = 0, extraBufferCapacity = 64)

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        val sharedPref = getSharedPreferences(context)

        return sharedPref.edit()
    }

    /**
     * Sets a String preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param sValue The new value.
     */
    fun setStringPrefValue(context: Context,
                           pref: String, sValue: String?) {
        val editor = getEditor(context)
        editor.putString(pref, sValue)
        editor.commit()

        notifyPrefChanged(pref)
    }

    /**
     * Sets a Boolean preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param bValue The new value.
     */
    fun setBooleanPrefValue(context: Context,
                            pref: String,
                            bValue: Boolean) {
        val editor = getEditor(context)
        editor.putBoolean(pref, bValue)
        editor.commit()

        notifyPrefChanged(pref)
    }

    /**
     * Sets a Long preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param lValue The new value.
     */
    fun setLongPrefValue(context: Context,
                         pref: String,
                         lValue: Long) {
        val editor = getEditor(context)
        editor.putLong(pref, lValue)
        editor.commit()

        notifyPrefChanged(pref)
    }

    /**
     * Sets an Integer preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param iValue The new value.
     */
    fun setIntegerPrefValue(context: Context,
                            pref: String,
                            iValue: Int) {
        val editor = getEditor(context)
        editor.putInt(pref, iValue)
        editor.commit()

        notifyPrefChanged(pref)
    }

    /**
     * Sets a Float preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param fValue The new value.
     */
    fun setFloatPrefValue(context: Context,
                          pref: String,
                          fValue: Float) {
        val editor = getEditor(context)
        editor.putFloat(pref, fValue)
        editor.commit()

        notifyPrefChanged(pref)
    }

    /**
     * Gets a String preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @return The value, or null if not found.
     */
    fun getStringPrefValue(context: Context,
                           pref: String): String? {
        val sharedPref = getSharedPreferences(context)
        return sharedPref.getString(pref, null)
    }

    /**
     * Gets a Boolean preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @return The value, or false if not found.
     */
    fun getBooleanPrefValue(context: Context,
                            pref: String): Boolean {
        return getBooleanPrefValue(context, pref, false)
    }

    /**
     * Gets a Boolean preference value with a default.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param defVal The default value.
     * @return The value, or [defVal] if not found.
     */
    fun getBooleanPrefValue(context: Context,
                            pref: String,
                            defVal: Boolean): Boolean {
        val sharedPref = getSharedPreferences(context)

        return sharedPref.getBoolean(pref, defVal)
    }

    /**
     * Gets a Long preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @return The value, or 0 if not found.
     */
    fun getLongPrefValue(context: Context,
                         pref: String): Long {
        return getLongPrefValue(context, pref, 0L)
    }

    /**
     * Gets a Long preference value with a default.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param defVal The default value.
     * @return The value, or [defVal] if not found.
     */
    fun getLongPrefValue(context: Context,
                         pref: String,
                         defVal: Long): Long {
        val sharedPref = getSharedPreferences(context)

        return sharedPref.getLong(pref, defVal)
    }

    /**
     * Gets an Integer preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @return The value, or 0 if not found.
     */
    fun getIntegerPrefValue(context: Context,
                            pref: String): Int {
        return getIntegerPrefValue(context, pref, 0)
    }

    /**
     * Gets an Integer preference value with a default.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param defVal The default value.
     * @return The value, or [defVal] if not found.
     */
    fun getIntegerPrefValue(context: Context,
                            pref: String,
                            defVal: Int): Int {
        val sharedPref = getSharedPreferences(context) ?: return defVal

        return sharedPref.getInt(pref, defVal)
    }

    /**
     * Gets a Float preference value.
     *
     * @param context The context.
     * @param pref The preference key.
     * @return The value, or 0.0f if not found.
     */
    fun getFloatPrefValue(context: Context,
                          pref: String): Float {
        return getFloatPrefValue(context, pref, 0.0f)
    }

    /**
     * Gets a Float preference value with a default.
     *
     * @param context The context.
     * @param pref The preference key.
     * @param defVal The default value.
     * @return The value, or [defVal] if not found.
     */
    fun getFloatPrefValue(context: Context,
                          pref: String,
                          defVal: Float): Float {
        val sharedPref = getSharedPreferences(context)

        return sharedPref.getFloat(pref, defVal)
    }

    /**
     * Notifies listeners that a preference has changed.
     *
     * @param key The key of the changed preference.
     */
    protected fun notifyPrefChanged(key: String) {
        if (TextUtils.isEmpty(key)) {
            return
        }

        val change = PrefsChange(this, key)
        Logger.debug("preference changed: [$key]")
        prefsChange.postValue(change)

        prefsChanges.tryEmit(change)
    }

    /**
     * The name of the SharedPreferences file.
     */
    protected abstract val prefName: String

}
