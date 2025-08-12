package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing

import android.content.Context

class PrefUtilForPdfTools(private val context: Context) {
    fun setInt(key: String?, value: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String?, defValue: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getInt(key, defValue)
    }

    fun setString(key: String?, value: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String?): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getString(key, "")
    }

    fun setLong(key: String?, value: Long?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putLong(key, value!!)
        editor.apply()
    }

    fun getLong(key: String?,defaultValue: Long): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getLong(key,defaultValue )
    }

    fun setBool(key: String?, value: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBool(key: String?): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getBoolean(key, true)
    }

    fun getBool(key: String?, defaultValue: Boolean): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getBoolean(key, defaultValue)
    }

    companion object {
        const val PREFS_NAME = "ai_assistant_pdf_tools_pref"
        private const val key = "isFirstTime"
        private const val premiumKey = "PREMIUM"
        private const val premiumCheck = "CHECK"

        fun setFirstTime(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putBoolean(key, false)
            editor.apply()
        }

        fun isFirstTime(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getBoolean(key, true)
        }

        @JvmStatic
        fun setPremiumString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(premiumKey, value)
            editor.apply()
        }

        @JvmStatic
        fun getPremiumString(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(premiumKey, "Free")
        }

        fun setPremiumItemPrice(index: Int, value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString("$index", value)
            editor.apply()
        }

        fun getPremiumItemPrice(context: Context, index: Int): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString("$index", "__")
        }

        fun setPremium(context: Context, value: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putBoolean(premiumCheck, value)
            editor.apply()
        }
        @JvmStatic
        fun isPremium(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getBoolean(premiumCheck, false)
        }



    }


}


