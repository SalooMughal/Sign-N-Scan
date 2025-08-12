package com.pixelz360.docsign.imagetopdf.creator.language


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.findphone.whistleclapfinder.languageClasess.LanguageAdapter
import com.findphone.whistleclapfinder.languageClasess.LanguageModel
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.ActionFragment
import com.pixelz360.docsign.imagetopdf.creator.SettingActivity
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.getAdsForLiftTimeString
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager.AdCallback
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityLanguageBinding
import com.pixelz360.docsign.imagetopdf.creator.onboarding.OnboardingActivity
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.DigitalSignatureActivity

class LanguageActivity : AppCompatActivity(), LanguageAdapter.OnLanguageItemClickListener {

    lateinit var languageRecyclerView: RecyclerView
    lateinit var checkBtn: TextView
    lateinit var toolbarTitle: TextView
    lateinit var toolbarTitleFirst: TextView
    lateinit var backPressBtn: ImageView

    var languageCode:String = ""
    var lanugageNameCheck:String = ""

    var firstOrNo:String=""

    private var adManager11: AdManager? = null

    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private val handler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable = Runnable {}


    lateinit var binding:ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Change the status bar color for this activity
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // Ensure system bar backgrounds can be modified
        window.statusBarColor = resources.getColor(R.color.white) // Set your custom color


        //        loadInterstitialAd();
        adManager11 = AdManager()

        //        adManager11.loadAd(requireActivity(), "ca-app-pub-3097817755522298/5613637998");
        adManager11!!.loadAd(this@LanguageActivity, getString(R.string.language_screen_intertial_ad))

        languageRecyclerView = findViewById(R.id.languageRecyclerView)
        checkBtn = findViewById(R.id.checkBtn)
        backPressBtn = findViewById(R.id.backPressBtn)
        toolbarTitle = findViewById(R.id.toolbarTitle)
        toolbarTitleFirst = findViewById(R.id.toolbarTitleFirst)

         firstOrNo = intent.getStringExtra("namelanguage").toString()
        if (firstOrNo == "yes") {
            toolbarTitleFirst.text = resources.getString(R.string.select_language)
            toolbarTitleFirst.visibility = View.VISIBLE
            toolbarTitle.visibility = View.GONE
            backPressBtn.visibility = View.GONE


            Log.d("check","no first time")


        } else {
            toolbarTitle.text = resources.getString(R.string.select_language)
            toolbarTitleFirst.visibility = View.GONE
            toolbarTitle.visibility = View.VISIBLE
            backPressBtn.visibility = View.VISIBLE

            Log.d("check","yes first time")
        }

        backPressBtn.setOnClickListener {
          onBackPressed()
            finish()
        }

        val languages = listOf(
            LanguageModel(getString(R.string.english), "en", R.drawable.ic_english_flag),
            LanguageModel(getString(R.string.franch), "fr", R.drawable.ic_franch_flag),
            LanguageModel(getString(R.string.spanish), "es", R.drawable.ic_spanish_flag),
            LanguageModel(getString(R.string.arabic), "ar", R.drawable.ic_arabic_flag),
            LanguageModel(getString(R.string.german), "de", R.drawable.ic_german_flag),
            LanguageModel(getString(R.string.mandarin), "zh", R.drawable.ic_mandarin_flag),
            LanguageModel(getString(R.string.portuguese), "pt", R.drawable.ic_portuguese_flag),
            LanguageModel(getString(R.string.indonesian), "in", R.drawable.ic_indonesian_flag),

        )

        val adapter = LanguageAdapter( this@LanguageActivity, languages, this)
        languageRecyclerView.layoutManager = LinearLayoutManager(this)
        languageRecyclerView.adapter = adapter





        checkBtn.setOnClickListener {


            if (firstOrNo == "yes") {


                Log.d("check","yes first time")

                if (!isInternetAvailable()) {
                    goToNextActivity() // No internet, proceed to the next activity
                } else {
                    // Show the ad or proceed if timeout

                    if (PrefUtilForAppAdsFree.isPremium(this@LanguageActivity) || (getAdsForLiftTimeString(this@LanguageActivity) == "ads_free_life_time")
                    ) {
                        goToNextActivity()
                        Log.d("checkbilling", " add remove " + PrefUtilForAppAdsFree.isPremium(this@LanguageActivity))
                    }else{

                        Log.d("checkbilling", " add not remove " + PrefUtilForAppAdsFree.isPremium(this@LanguageActivity))


//                        adManager11!!.showAdIfAvailable(this@LanguageActivity, object : AdCallback {
//                            override fun onAdDismissed() {
//                                // Define your custom action here after the ad is dismissed
//                                goToNextActivity()
//                            }
//
//                            override fun onAdFailedToShow() {
//                                // Define your custom action here if the ad fails to show
//                                goToNextActivity()
//                                Log.d("AdManager", "onAdFailedToShow")
//                            }
//                        })


                        // Show ad first, then navigate
                        adManager11!!.showAdIfAvailable(this@LanguageActivity, getString(R.string.language_screen_intertial_ad),
                            object : AdCallback {
                                override fun onAdDismissed() {
                                    goToNextActivity()
                                }

                                override fun onAdFailedToShow() {
                                    goToNextActivity()
                                }
                            })



                    }



                }





            } else {

                goToNextActivity()

                Log.d("check","no first time")
            }



//            if (!isInternetAvailable()) {
//                goToNextActivity()
//            } else {
//                showAdOrLoad()
//            }

        }

//        loadInterstitialAd()
//

//        val adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)

    }








    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }


 fun goToNextActivity() {
        if (lanugageNameCheck.equals("")){
            val languageNameGet = LanguageHelper.getMyPrefsLanguageName(this@LanguageActivity)
            setLocale(this@LanguageActivity, "en", languageNameGet!!)
            Log.e("checklanguagename", "1  languageCode: $languageCode")
        }else{
            setLocale(this@LanguageActivity, languageCode, lanugageNameCheck)
            Log.e("checklanguagename", " 1 else languageCode: $languageCode")
            Toast.makeText(this@LanguageActivity,"Change Language",Toast.LENGTH_SHORT).show()

        }
    }


    // Implement the interface to handle the item click
    override fun onItemClick(language: LanguageModel, languageCode: String, lanugageNameCheck: String) {
//        Toast.makeText(this, "Selected language: ${language.name}", Toast.LENGTH_SHORT).show()
        Log.d("LanguageActivity", "Selected language: ${language.name}")


        this.languageCode = languageCode
        this.lanugageNameCheck = lanugageNameCheck


    }

    private fun setLocale(context: Context, languageCode: String, languageName: String) {

        LanguageHelper.setLocale(context, languageCode)  // SET LANGUAGE FUNCTION
        LanguageHelper.addMyPrefsLanguageName(context,languageName) // SET LANGUAGE NAME FUNCTION
        Log.d("checklanguagename",languageName)


        if (firstOrNo == "yes") {

//            val intent = Intent(context, AddFreeActivity::class.java)
            val intent = Intent(context, OnboardingActivity::class.java)
//            val intent = Intent(context, GetStartedActivity::class.java)
            context.startActivity(intent)
            finish()



        }else{
            val intent = Intent(this@LanguageActivity, HomeActivity::class.java)
            intent.putExtra("ScannerSide","SubscribtionSide")
            startActivity(intent)
            finish()
        }




    }

    override fun onPause() {
//        binding.adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
//        binding.adView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
//        binding.adView.destroy()
        handler.removeCallbacks(timeoutRunnable)
    }


}


//    override fun onBackPressed() {
//        onBackPressedDispatcher.onBackPressed()
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//    }
