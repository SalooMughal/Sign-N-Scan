package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing

import android.content.Context

class PrefUtilForAppAdsFree(private val context: Context) {
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
        const val PREFS_NAME = "ai_assistant_pref"
        private const val key = "isFirstTime"
        private const val premiumKey = "PREMIUM"
        private const val AdsForLiftTimeString = "AdsForLiftTimeString"
        private const val AdsForMonthlyString = "Monthly"
        private const val AdsForWeeklyString = "Weekly"
        private const val AdsForYearlyString = "Yearly"

        private const val AdsForToolsWeeklyString = "AdsForToolsWeeklyString"
        private const val AdsForToolsMonthlyString = "AdsForToolsMonthlyString"
        private const val AdsForToolsYearlyString = "AdsForToolsYearlyString"
        private const val premiumCheck = "CHECK"
        private const val CheckActivity = "CheckActivity"

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

        fun setPremiumString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(premiumKey, value)
            editor.apply()
        }

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
        @JvmStatic
        fun setCheckActivityPremium(context: Context, value: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putBoolean(CheckActivity, value)
            editor.apply()
        }
        @JvmStatic
        fun isCheckActivityPremium(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getBoolean(CheckActivity, false)
        }


        @JvmStatic
        fun setAdsForLiftTimeString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(AdsForLiftTimeString, value)
            editor.apply()
        }
        @JvmStatic
        fun getAdsForLiftTimeString(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(AdsForLiftTimeString, "no life time")
        }




        @JvmStatic
        fun setAdsForWeeklyString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(AdsForWeeklyString, value)
            editor.apply()
        }
        @JvmStatic
        fun getAdsForWeeklyString(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(AdsForWeeklyString, "no life time")
        }

        @JvmStatic
        fun setAdsForMonthlyString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(AdsForMonthlyString, value)
            editor.apply()
        }
        @JvmStatic
        fun getAdsForMonthlyString(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(AdsForMonthlyString, "no life time")
        }


        @JvmStatic
        fun setAdsForYearlyString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(AdsForYearlyString, value)
            editor.apply()
        }
        @JvmStatic
        fun getAdsForYearlyString(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(AdsForYearlyString, "no life time")
        }




        @JvmStatic
        fun setAdsForToolsWeeklyString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(AdsForToolsWeeklyString, value)
            editor.apply()
        }
        @JvmStatic
        fun getAdsForToolsWeeklyString(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(AdsForToolsWeeklyString, "no life time")
        }

        @JvmStatic
        fun setAdsFoToolsrMonthlyString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(AdsForToolsMonthlyString, value)
            editor.apply()
        }
        @JvmStatic
        fun getAdsForToolsMonthlyString(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(AdsForToolsMonthlyString, "no life time")
        }


        @JvmStatic
        fun setAdsForToolsYearlyString(value: String, context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(AdsForToolsYearlyString, value)
            editor.apply()
        }
        @JvmStatic
        fun getAdsForToolsYearlyString(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(AdsForToolsYearlyString, "no life time")
        }

    }


}


