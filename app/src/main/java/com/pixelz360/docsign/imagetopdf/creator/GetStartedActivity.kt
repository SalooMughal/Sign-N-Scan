package com.pixelz360.docsign.imagetopdf.creator


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.Task
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityGetStartedBinding
import com.pixelz360.docsign.imagetopdf.creator.language.LanguageActivity
import com.pixelz360.docsign.imagetopdf.creator.language.LanguageHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class GetStartedActivity : AppCompatActivity() {

    lateinit var binding: ActivityGetStartedBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    val RC_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Change the status bar color for this activity
        val window1 = window
        window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // Ensure system bar backgrounds can be modified
        window1.statusBarColor = resources.getColor(R.color.white) // Set your custom color


        // Load the saved language
        LanguageHelper.loadLocale(this@GetStartedActivity)

        // Log a predefined event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("activity_name", "GetStartedActivity")
        analytics.logEvent("activity_created", bundle)

        // Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "GetStartedActivity")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val textView = findViewById<TextView>(R.id.textView)

        // Text containing the clickable portion
        val fullText = resources.getString(R.string.by_connecting)

        // Create a SpannableString
        val spannableString = SpannableString(fullText)

        // Create a ClickableSpan
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click action, e.g., open a link
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/terms"))
                val intent = Intent(this@GetStartedActivity,PrivacyPolicyActivity::class.java)
                startActivity(intent)
            }
        }

        // Get the start and end indices for the clickable span
        val termsText = resources.getString(R.string.terms)
        val privacyPolicyText = "Privacy Policy"  // Replace with your actual string

        val clickableStart = fullText.indexOf(termsText)
        val clickableEnd = fullText.indexOf(privacyPolicyText) + privacyPolicyText.length

        // Ensure that the indices are valid
        if (clickableStart >= 0 && clickableEnd >= 0 && clickableEnd <= fullText.length) {
            // Apply ClickableSpan to the clickable portion of the text
            spannableString.setSpan(
                clickableSpan,
                clickableStart,
                clickableEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Apply ForegroundColorSpan to set color
            val color = resources.getColor(R.color.splash_bg_color)  // Replace with your color resource
            spannableString.setSpan(
                ForegroundColorSpan(color),
                clickableStart,
                clickableEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Apply the SpannableString to the TextView
            textView.text = spannableString

            // Enable the movement method to make the link clickable
            textView.movementMethod = LinkMovementMethod.getInstance()
        } else {
            // Log an error if indices are invalid
            Log.e("GetStartedActivity", "Invalid indices for spannable string: start=$clickableStart, end=$clickableEnd")
        }

        // Handle button click
        binding.startedBtn.setOnClickListener {


            signIn()
        }

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val window = this.window
        window.navigationBarColor = this.resources.getColor(R.color.black)


        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check if user is already signed in
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            navigateToNextActivity()
            Log.w("GoogleSignIn123", "User is already signed in")
        }


        binding.guestBtn.setOnClickListener {
//            val intent = Intent(this@GetStartedActivity, FirstActivity::class.java)
//            startActivity(intent)
//            finish() // Close the current activity

            navigateToNextActivity()
        }


        // Call the method to get the advertising ID using Coroutine
        getAdvertisingId()

    }

    private fun getAdvertisingId() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
                val advertisingId = adInfo.id
                val isLimitAdTrackingEnabled = adInfo.isLimitAdTrackingEnabled

                Log.d("AdvertisingID", "ID: $advertisingId, Limit Tracking: $isLimitAdTrackingEnabled")

                withContext(Dispatchers.Main) {
                    // Use advertisingId if needed (e.g., send to server)
                }
            } catch (e: Exception) {
                Log.e("AdvertisingID", "Error getting Advertising ID", e)
            }
        }
    }




    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                if (account != null) {
                    navigateToNextActivity()
                }
            } catch (e: ApiException) {
                Log.w("GoogleSignIn123", "Sign-in failed", e)
            }
        }
        }

    private fun navigateToNextActivity() {
        val intent = Intent(this, LanguageActivity::class.java)
//            val intent = Intent(this, OnboardingActivity::class.java)
        intent.putExtra("namelanguage", "yes")
        startActivity(intent)
        finish()


    }


    override fun onBackPressed() {

        val preferences = getPreferences(MODE_PRIVATE)
        val isFirstTime = preferences.getBoolean("isFirstTimeGetStartedScreen", true)
//        super.onBackPressed()

        val intent: Intent
        if (isFirstTime) {
            // Navigate to GetStartedActivity for the first launch
//            intent = new Intent(FirstActivity.this, GetStartedActivity.class);

            intent = Intent(this@GetStartedActivity, LanguageActivity::class.java)
            intent.putExtra("namelanguage", "yes")
            startActivity(intent)


            preferences.edit().putBoolean("isFirstTimeGetStartedScreen", false).apply() // Update the preference

            Log.d("checkcheckscreen","Second Time Back Click  Back Allow")

        } else {

            Log.d("checkcheckscreen","Second Time Back Click No Back Allow")

        }



    }
}
