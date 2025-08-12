package com.pixelz360.docsign.imagetopdf.creator.language

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

object LanguageHelper {
    private const val PREFS_NAME = "MyPrefsLanguage"
    private const val MY_PREFS_LANGUAGE_MAIN_KEY = "Language_MAIN_KEY"
    private const val MY_PREFS_LANGUAGE_NAME = "LANGUAGE_NAME"


    @JvmStatic
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration()
        configuration.locale = locale
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

        // Save the selected language in SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(MY_PREFS_LANGUAGE_MAIN_KEY, languageCode)
        editor.apply()
    }
    @JvmStatic
    fun loadLocale(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val language = prefs.getString(MY_PREFS_LANGUAGE_MAIN_KEY, "en")
        if (!language.isNullOrEmpty()) {
            setLocale(context, language)

        }
        return language
    }

    fun restartActivity(activity: AppCompatActivity) {
        val intent = Intent(activity, activity::class.java)
        activity.finish()
        activity.startActivity(intent)
    }


    fun addMyPrefsLanguageName(context: Context, code: String?) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(MY_PREFS_LANGUAGE_NAME, code)
        editor.apply()
    }

    fun getMyPrefsLanguageName(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(MY_PREFS_LANGUAGE_NAME, "English")
    }
}
