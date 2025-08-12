package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup.MarginLayoutParams

object ListGridItemStorage {
    private const val PREFS_NAME = "MyPrefsListGrid"
    private const val MY_PREFS_LANGUAGE_MAIN_KEY = "Language_MAIN_KEY"
    private const val MY_PREFS_IS_CHECK_LIST_GRID = false


    @JvmStatic
    fun setListOrGrid(context: Context, listOrGrid: Boolean) {

        // Save the selected language in SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putBoolean(MY_PREFS_LANGUAGE_MAIN_KEY, listOrGrid)
        editor.apply()
    }
    @JvmStatic
    fun isCheckedListOrGrid(context: Context): Boolean? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val language = prefs.getBoolean(MY_PREFS_LANGUAGE_MAIN_KEY, false)
//        if (!language.isNullOrEmpty()) {
//            setLocale(context, language)
//
//        }
        return language
    }

    @JvmStatic
    public fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }



//    fun addMyPrefsLanguageName(context: Context, code: Boolean?) {
//        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putBoolean(MY_PREFS_IS_CHECK_LIST_GRID, code)
//        editor.apply()
//    }
//
//    fun getMyPrefsLanguageName(context: Context): String? {
//        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        return sharedPreferences.getString(MY_PREFS_IS_CHECK_LIST_GRID, "English")
//    }
}
