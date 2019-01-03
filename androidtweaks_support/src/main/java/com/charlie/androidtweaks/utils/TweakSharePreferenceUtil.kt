package com.charlie.androidtweaks.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.charlie.androidtweaks.core.TweakManager
import com.charlie.androidtweaks.data.TAG_ANDROIDTWEAKS
import kotlin.reflect.KProperty

class TweakSharePreferenceUtil<T>(val name: String, val default: T) {

    private val spName = "sp_tweak_file"

    private val sharedPreferences: SharedPreferences? by lazy {
        TweakManager.weakReference?.let {
            it.get()?.getSharedPreferences(spName, Context.MODE_PRIVATE)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getTweakValue(name, default)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = putTweakValue(name, value)

    private fun <T> putTweakValue(key: String, value: T) = with(sharedPreferences?.edit()) {
        Log.d(TAG_ANDROIDTWEAKS, "putValue $key   $value")
        try {
            when (value) {
                is Int -> this?.putInt(key, value)?.apply()
                is Boolean -> this?.putBoolean(key, value)?.apply()
                is Float -> this?.putFloat(key, value)?.apply()
                is String -> this?.putString(key, value)?.apply()
                else -> {
                    throw IllegalArgumentException("SharePreference can't put this value.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG_ANDROIDTWEAKS, e.toString())
        }
    }

    private fun getTweakValue(key: String, default: T): T = with(sharedPreferences) {

        var value: Any? = null
        try {
            value = when (default) {
                is Int -> this?.getInt(key, default)
                is Boolean -> this?.getBoolean(key, default)
                is Float -> this?.getFloat(key, default)
                is String -> this?.getString(key, default)
                else -> {
                    throw IllegalArgumentException("SharePreference can't get this value.")
                }
            }
        } catch (e: ClassCastException) {
            //remove the key
            this?.edit()?.remove(key)?.apply()
            Log.d(TAG_ANDROIDTWEAKS, e.toString())
        }
        Log.d(TAG_ANDROIDTWEAKS, "getValue $key   $value")
        return if (value == null) default else value as T
    }
}