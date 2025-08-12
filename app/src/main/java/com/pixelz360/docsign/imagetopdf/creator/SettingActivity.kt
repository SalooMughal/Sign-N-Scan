package com.pixelz360.docsign.imagetopdf.creator


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.AddFreeActivity
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivitySettingBinding
import com.pixelz360.docsign.imagetopdf.creator.language.LanguageActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)





//        val adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)


        try {
            val pInfo: PackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0)
            val versionName = pInfo.versionName
            binding.appVersioinNumber.text = versionName

            Log.d("versionName","versionName  "+versionName)

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        // Check if dark mode is enabled
        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)


        if (isDarkMode) {
            binding.themeSwitch.isChecked = true
            binding.adsFree.setBackgroundResource(R.drawable.remove_ad_dark_mode_icon)

        } else {
            binding.themeSwitch.isChecked = false
            binding.adsFree.setBackgroundResource(R.drawable.remove_ad_light_mode_icon)

        }

        binding.themeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            if (isChecked) {
                editor.putBoolean("isDarkMode", true)
                editor.apply()
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                val intent = Intent(this@SettingActivity,FirstActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                editor.putBoolean("isDarkMode", false)
                editor.apply()
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                val intent = Intent(this@SettingActivity,FirstActivity::class.java)
                startActivity(intent)
                finish()
            }
            recreate() // Recreate activity to apply new theme
        }


//        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
//        val isRemoveAdsEnabled = preferences.getBoolean("isRemoveAdsEnabled", false)
//
//        if (isRemoveAdsEnabled){
//            binding.isRemoveAdsEnabled.isChecked = true
//        }else{
//            binding.isRemoveAdsEnabled.isChecked = false
//        }
//
//        binding.isRemoveAdsEnabled.setOnClickListener { v: View? ->
//            // Pass the vibration state to the SoundActivity
//            val isRemoveAdsEnabled: Boolean = binding.isRemoveAdsEnabled.isChecked()
//
//            Log.d("isRemoveAdsEnabled", "SettingActivity  isRemoveAdsEnabled  " + isRemoveAdsEnabled)
//
//
//            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
//            val editor = preferences.edit()
//            editor.putBoolean("isRemoveAdsEnabled", isRemoveAdsEnabled)
//            editor.apply()
//
//
//        }





            // Log a predefined event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("activity_name", "SettingActivity")
        analytics.logEvent("activity_created", bundle)
// Using predefined Firebase Analytics events
        // Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SettingActivity")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)



        binding.shareApps.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage += "https://play.google.com/store/apps/details?id=${packageName}\n\n"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                // e.toString()
            }
        }


        binding.rateUs.setOnClickListener {
            rateUsDailog()
        }

        binding.requestNewFeaterBtn.setOnClickListener {

        }

        binding.feadback.setOnClickListener {


            feedBackDailog()

        }

        binding.privacyPolicy.setOnClickListener {
            privacyPolicy()
        }


        binding.backButton.setOnClickListener {
            val intent = Intent(this@SettingActivity, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.language.setOnClickListener {
            val intent = Intent(this@SettingActivity, LanguageActivity::class.java)
            intent.putExtra("namelanguage","no")
            startActivity(intent)
        }



        binding.adsFree.setOnClickListener {
            val intent = Intent(this@SettingActivity, AddFreeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun feedBackDailog() {

        val dialog = Dialog(this@SettingActivity, R.style.renameDialogStyle)
        dialog.setContentView(R.layout.feadback_dailog)

        val window = dialog.window
        if (window != null) {
            window.setGravity(Gravity.CENTER)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val metrics = Resources.getSystem().displayMetrics
            val screenWidth = metrics.widthPixels
            val desiredWidth = screenWidth - 2 * dpToPx(this@SettingActivity, 30)
            val params = dialog.window!!.attributes
            params.width = desiredWidth
            window.attributes = params
        }

        val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
        val rateUsBtn = dialog.findViewById<TextView>(R.id.rateUsBtn)


        rateUsBtn.setOnClickListener {
            sendFeedbackEmail(this@SettingActivity)
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()

        }

        dialog.window?.apply {
            // Set the layout parameters to match parent width and wrap content in height
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)




            // Set the background to be transparent
            setBackgroundDrawableResource(android.R.color.transparent)
            // Apply additional custom styling or layout parameters here
            val metrics = Resources.getSystem().displayMetrics
            val screenWidth = metrics.widthPixels
            val desiredWidth = screenWidth - 2 * dpToPx(context, 30) // For example
            val params = attributes
            params.width = desiredWidth
            attributes = params
        }


        dialog.show()


    }

    private fun sendFeedbackEmail(activity: Context) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:") // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("feedback@example.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback")

        try {
            activity.startActivity(Intent.createChooser(emailIntent, "Send feedback using:"))
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(activity, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun moreApps() {
        val uri = Uri.parse("http://www.google.com") // missing 'http://' will cause crashed
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun privacyPolicy() {
//        val intent = Intent(this@MainActivity,privacyPolicayActivity::class.java)
//        startActivity(intent)
        val intent = Intent(this@SettingActivity,PrivacyPolicyActivity::class.java)
        startActivity(intent)
    }




    private fun rateUsDailog() {

//        val customDialog = CustomDialog(this@SettingActivity)
//        customDialog.show()


        val dialog = Dialog(this@SettingActivity, R.style.renameDialogStyle)
        dialog.setContentView(R.layout.rate_us_dailog)

        val window = dialog.window
        if (window != null) {
            window.setGravity(Gravity.CENTER)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val metrics = Resources.getSystem().displayMetrics
            val screenWidth = metrics.widthPixels
            val desiredWidth = screenWidth - 2 * dpToPx(this@SettingActivity, 30)
            val params = dialog.window!!.attributes
            params.width = desiredWidth
            window.attributes = params
        }



        val startOne = dialog.findViewById<ImageView>(R.id.star1)
        val startTow = dialog.findViewById<ImageView>(R.id.star2)
        val startThree = dialog.findViewById<ImageView>(R.id.star3)
        val startFour = dialog.findViewById<ImageView>(R.id.star4)
        val startFive = dialog.findViewById<ImageView>(R.id.star5)
        val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
        val rateUsBtn = dialog.findViewById<TextView>(R.id.rateUsBtn)

        var starIsCheckedOne = true
        var starIsCheckedTow = true
        var starIsCheckedThree = true
        var starIsCheckedFour = true
        var starIsCheckedFive = true



        startOne.setOnClickListener {

            if (starIsCheckedOne){
                startOne.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startTow.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startThree.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startFour.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startFive.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                starIsCheckedOne = false
                starIsCheckedTow = false
                starIsCheckedThree = false
                starIsCheckedFour = false
                starIsCheckedFive = false
            }else{
                startOne.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                starIsCheckedOne = true
            }
        }

        startTow.setOnClickListener {
            if (starIsCheckedTow){
                startTow.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startThree.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startFour.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startFive.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                starIsCheckedTow = false
                starIsCheckedThree = false
                starIsCheckedFour = false
                starIsCheckedFive = false
            }else{
                startOne.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startTow.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                starIsCheckedOne = true
                starIsCheckedTow = true
            }
        }

        startThree.setOnClickListener {
            if (starIsCheckedThree){
                startThree.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startFour.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startFive.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                starIsCheckedThree = false
                starIsCheckedFour = false
                starIsCheckedFive = false
            }else{
                startOne.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startTow.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startThree.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                starIsCheckedOne = true
                starIsCheckedTow = true
                starIsCheckedThree = true
            }
        }

        startFour.setOnClickListener {
            if (starIsCheckedFour){
                startFour.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                startFive.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                starIsCheckedFour = false
                starIsCheckedFive = false
            }else{
                startOne.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startTow.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startThree.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startFour.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                starIsCheckedOne = true
                starIsCheckedTow = true
                starIsCheckedThree = true
                starIsCheckedFour = true
            }
        }


        startFive.setOnClickListener {
            if (starIsCheckedFive){
                startFive.setImageDrawable(resources.getDrawable(R.drawable.un_selected_start))
                starIsCheckedFive = false
            }else{
                startOne.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startTow.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startThree.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startFour.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                startFive.setImageDrawable(resources.getDrawable(R.drawable.selected_start))
                starIsCheckedOne = true
                starIsCheckedTow = true
                starIsCheckedThree = true
                starIsCheckedFour = true
                starIsCheckedFive = true
            }
        }


        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }


        rateUsBtn.setOnClickListener {



            if (starIsCheckedOne && !starIsCheckedTow && !starIsCheckedThree && !starIsCheckedFour && !starIsCheckedFive){
//                    feedBackDailog()
//                val alert = ViewSettingDialog()
//                alert.showDialog(this@SettingActivity)

                feedBackDailog()

            }else if (starIsCheckedOne && starIsCheckedTow && !starIsCheckedThree && !starIsCheckedFour && !starIsCheckedFive){
//                    feedBackDailog()

//                val alert = ViewSettingDialog()
//                alert.showDialog(this@SettingActivity)

                feedBackDailog()
            }else if (starIsCheckedOne && starIsCheckedTow && starIsCheckedThree && !starIsCheckedFour && !starIsCheckedFive){

//                val alert = ViewPlayStoreDialog()
//                alert.showDialog(this@SettingActivity)

                playStoreDailog()


            }else if (starIsCheckedOne && starIsCheckedTow && starIsCheckedThree && starIsCheckedFour && !starIsCheckedFive){

//                val alert = ViewPlayStoreDialog()
//                alert.showDialog(this@SettingActivity)

                playStoreDailog()
            }
            else if (starIsCheckedOne && starIsCheckedTow && starIsCheckedThree && starIsCheckedFour && starIsCheckedFive){
//                val alert = ViewPlayStoreDialog()
//                alert.showDialog(this@SettingActivity)

                playStoreDailog()
            }else{
                Toast.makeText(this@SettingActivity,"Please Select Rarting", Toast.LENGTH_SHORT).show()
            }



//                val totalStars = "Total Stars:: " + simpleRatingBar.numStars
//                val rating = "Rating :: " + simpleRatingBar.rating
//                val ratingqq = simpleRatingBar.rating
//
//                Log.d("checkstar",totalStars)
//                Log.d("checkstar",rating)
//
//
//                if (ratingqq <= 2.0f) {
//                    feedBackDailog()
//                } else {
//                    rateUsPlaystoreDailog()
//                }




        }



        dialog.show()


    }



    private fun playStoreDailog() {
        val dialog = Dialog(this@SettingActivity, R.style.renameDialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.play_store_dailog)


        val window = dialog.window
        if (window != null) {
            window.setGravity(Gravity.CENTER)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val metrics = Resources.getSystem().displayMetrics
            val screenWidth = metrics.widthPixels
            val desiredWidth = screenWidth - 2 * dpToPx(this@SettingActivity, 30)
            val params = dialog.window!!.attributes
            params.width = desiredWidth
            window.attributes = params
        }



        val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
        val rateUsBtn = dialog.findViewById<TextView>(R.id.rateUsBtn)


        rateUsBtn.setOnClickListener {
            openPlayStore(this@SettingActivity)
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }


        dialog.window?.apply {
            // Set the layout parameters to match parent width and wrap content in height
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)




            // Set the background to be transparent
            setBackgroundDrawableResource(android.R.color.transparent)
            // Apply additional custom styling or layout parameters here
            val metrics = Resources.getSystem().displayMetrics
            val screenWidth = metrics.widthPixels
            val desiredWidth = screenWidth - 2 * dpToPx(context, 30) // For example
            val params = attributes
            params.width = desiredWidth
            attributes = params
        }


        dialog.show()
    }

    private fun openPlayStore(context: Context) {
        val packageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: android.content.ActivityNotFoundException) {
            // If Play Store app is not installed, open the app page in a browser
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }



    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onRestart() {
        super.onRestart()
        Log.d("checklifecycler","onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d("checklifecycler","onStart")
    }

}

