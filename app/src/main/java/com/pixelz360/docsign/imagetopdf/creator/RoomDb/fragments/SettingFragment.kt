package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixelz360.docsign.imagetopdf.creator.FirstActivity
import com.pixelz360.docsign.imagetopdf.creator.PrivacyPolicyActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.AddFreeActivity
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.BillingIASForPdfTools
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForPdfTools
import com.pixelz360.docsign.imagetopdf.creator.databinding.FragmentSettingBinding
import com.pixelz360.docsign.imagetopdf.creator.language.LanguageActivity
import com.pixelz360.docsign.imagetopdf.creator.pdf_tools_billing.AddFreeForPdfToolsActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingFragment : Fragment(), ToolbarSettings {

    private var binding: FragmentSettingBinding? = null
    private lateinit var analytics: FirebaseAnalytics

    private var homeToolbarTitle: ConstraintLayout? = null
    private var homeSelectAllButton: TextView? = null
    private var homeToolbarSelectedItem: TextView? = null
    private var homeSearchButton: ImageButton? = null
    private var homeSortButton: ImageButton? = null
    private var homeSettingButton: ImageButton? = null
    private var homeDeleteButton: ImageButton? = null
    private var homeSearchLayout: LinearLayout? = null
    private var homeToolbar: Toolbar? = null
    private var homeSearchBackBtn: ImageView? = null
    private var homeClearButton: ImageView? = null
    private var homeSearchEditText: EditText? = null

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding?.root
    }
    val RC_SIGN_IN = 100
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        analytics = FirebaseAnalytics.getInstance(requireContext())
        Log.d("checkfragmentselected", "SettingFragment  onCreateView")

        setAppVersion()
        setupThemeSwitch()
        setupButtonListeners()
        logAnalyticsEvent()


// Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

// Check if user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

        if (account != null) {
            // When Firebase user is not null, set image and name
            binding!!.tvEmail.visibility = VISIBLE

            // Check if the profile image is available
            if (account.photoUrl != null) {
                // If photo is available, load it using Glide

                binding!!.ivImage.visibility = VISIBLE
                binding!!.tvAvtaar.visibility = GONE

                Glide.with(requireActivity())
                    .load(account.photoUrl)
                    .transform(CenterInside(), RoundedCorners(11))
                    .into(binding!!.ivImage)
            } else {
                // If no photo, set a default avatar with the first two letters of the name
                val name = account.displayName ?: ""
                val initials = if (name.isNotEmpty()) {
                    val splitName = name.split(" ")
                    val firstName = splitName[0]
                    // Take the first two letters of the first name
                    firstName.take(2).uppercase()
                } else {
                    "NA"  // Fallback if name is empty
                }

                // Create a circular TextView with initials and set it as the image source
//                val avatarTextView = TextView(requireContext()).apply {
//                    text = initials
//                    textSize = 20f
//                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//                    gravity = Gravity.CENTER
//                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))  // Use any default background color you like
//                    layoutParams = ViewGroup.LayoutParams(200, 200)  // Size of the avatar
//                    background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_avatar_background)  // Apply circular shape (see below)
//
//                    Log.d("checkname","name   "+initials)
//                }

                Log.d("checkname","name   "+initials)

                binding!!.ivImage.visibility = GONE
                binding!!.tvAvtaar.visibility = VISIBLE

                binding!!.tvAvtaar.setText(initials)

                // Set the TextView as the image in the ImageView
//                binding!!.ivImage.setImageDrawable(avatarTextView.background)


                // Optional: Set a circular shape for the background (create a drawable in `res/drawable/circular_avatar_background.xml`)
            }

            binding!!.tvName.text = account.displayName
            binding!!.tvEmail.text = account.email
        }

        Log.d("checkcheck","onViewCreated")


        binding!!.loginAccount.setOnClickListener {
            signIn()
        }



        binding!!.logoutBtn.setOnClickListener {
            mGoogleSignInClient!!.signOut()
            // Display Toast
//            Toast.makeText(requireActivity(), "Logout successful", Toast.LENGTH_SHORT).show()
//            // Finish activity
//            requireActivity().finish()


            val intent = Intent(requireActivity(),FirstActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

            Toast.makeText(requireActivity(), "Logout successful", Toast.LENGTH_SHORT).show()
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
        val intent = Intent(requireActivity(),FirstActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun setAppVersion() {
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val versionName = packageInfo.versionName
            binding?.appVersioinNumber?.text = versionName
            Log.d("versionName", "App version: $versionName")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun setupThemeSwitch() {
        val sharedPreferences = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        binding?.themeSwitch?.isChecked = isDarkMode

        binding?.themeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("isDarkMode", isChecked)
            editor.apply()

            requireActivity().recreate()
        }
    }

    private fun setupButtonListeners() {
        binding?.shareApps?.setOnClickListener { shareApp() }
        binding?.rateUs?.setOnClickListener { rateUsDailog() }
        binding?.feadback?.setOnClickListener { feedBackDailog() }
        binding?.privacyPolicy?.setOnClickListener { openPrivacyPolicy() }
        binding?.language?.setOnClickListener {
            val intent = Intent(requireContext(), LanguageActivity::class.java)
            intent.putExtra("namelanguage", "no")
            startActivity(intent)
        }
        binding?.adsFree?.setOnClickListener {
            val intent = Intent(requireContext(), AddFreeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding?.pdfToolsPurchaseBtn?.setOnClickListener {
            val intent = Intent(requireContext(), AddFreeForPdfToolsActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


//        if (PrefUtilForAppAdsFree.getAdsForToolsWeeklyString(requireActivity()).equals("pdf_tools_weekly_plan")){
//
//            binding!!.selectedPdfToolsPlanShow.visibility = VISIBLE
//            binding!!.whenAnyAdvanceToolsPurchaseIcon.visibility = VISIBLE
//            binding!!.puchaseIcon.visibility = GONE
//            binding!!.planSetPdfToolsTextview.text = "Weekly Plan"
//
//        }else{
//            binding!!.selectedPdfToolsPlanShow.visibility = GONE
//            binding!!.whenAnyAdvanceToolsPurchaseIcon.visibility = GONE
//            binding!!.puchaseIcon.visibility = VISIBLE
//        }
//
//        if (PrefUtilForAppAdsFree.getAdsForToolsMonthlyString(requireActivity()).equals("pdf_tools_monthly_plan")){
//
//            binding!!.selectedPdfToolsPlanShow.visibility = VISIBLE
//            binding!!.whenAnyAdvanceToolsPurchaseIcon.visibility = VISIBLE
//            binding!!.puchaseIcon.visibility = GONE
//
//            binding!!.planSetPdfToolsTextview.text = "Monthly Plan"
//
//        }else{
//            binding!!.selectedPdfToolsPlanShow.visibility = GONE
//            binding!!.whenAnyAdvanceToolsPurchaseIcon.visibility = GONE
//            binding!!.puchaseIcon.visibility = VISIBLE
//        }
//
//        if (PrefUtilForAppAdsFree.getAdsForToolsYearlyString(requireActivity()).equals("pdf_tools_annual_plan")){
//
//            binding!!.selectedPdfToolsPlanShow.visibility = VISIBLE
//            binding!!.whenAnyAdvanceToolsPurchaseIcon.visibility = VISIBLE
//            binding!!.puchaseIcon.visibility = GONE
//
//            binding!!.planSetPdfToolsTextview.text = "Annual Plan"
//
//        }else{
//            binding!!.selectedPdfToolsPlanShow.visibility = GONE
//            binding!!.whenAnyAdvanceToolsPurchaseIcon.visibility = GONE
//            binding!!.puchaseIcon.visibility = VISIBLE
//        }
//
//
//         if (PrefUtilForAppAdsFree.getAdsForWeeklyString(requireActivity()).equals("weekly_plan")){
//
//
//             binding!!.selectedAddFreePlanShow.visibility = VISIBLE
//             binding!!.whenAnyAddRemovePurchaseIcon.visibility = VISIBLE
//             binding!!.iconAdsfree.visibility = GONE
//
//             binding!!.planSetAddFreeTextview.text = "Weekly Plan"
//
//
//         }else{
//             binding!!.selectedAddFreePlanShow.visibility = GONE
//             binding!!.whenAnyAddRemovePurchaseIcon.visibility = GONE
//             binding!!.iconAdsfree.visibility = VISIBLE
//         }
//
//
//             if (PrefUtilForAppAdsFree.getAdsForMonthlyString(requireActivity()).equals("monthly_plan")){
//
//             binding!!.selectedAddFreePlanShow.visibility = VISIBLE
//             binding!!.whenAnyAddRemovePurchaseIcon.visibility = VISIBLE
//             binding!!.iconAdsfree.visibility = GONE
//
//             binding!!.planSetAddFreeTextview.text = "Monthly Plan"
//
//
//         }else{
//                 binding!!.selectedAddFreePlanShow.visibility = GONE
//                 binding!!.whenAnyAddRemovePurchaseIcon.visibility = GONE
//                 binding!!.iconAdsfree.visibility = VISIBLE
//             }
//
//
//             if (PrefUtilForAppAdsFree.getAdsForYearlyString(requireActivity()).equals("yearly_plan")){
//
//             binding!!.selectedAddFreePlanShow.visibility = VISIBLE
//             binding!!.whenAnyAddRemovePurchaseIcon.visibility = VISIBLE
//             binding!!.iconAdsfree.visibility = GONE
//
//             binding!!.planSetAddFreeTextview.text = "Annual Plan"
//
//
//         }else{
//             binding!!.selectedAddFreePlanShow.visibility = GONE
//             binding!!.whenAnyAddRemovePurchaseIcon.visibility = GONE
//             binding!!.iconAdsfree.visibility = VISIBLE
//         }




        binding!!.manageSubscrpionInRemoveAdSide.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/account/subscriptions")
            intent.setPackage("com.android.vending") // Ensures the Play Store app handles it
            startActivity(intent)

        }


        binding!!.MemberShipCancelBtnTextview.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/account/subscriptions")
            intent.setPackage("com.android.vending") // Ensures the Play Store app handles it
            startActivity(intent)

        }



        if (PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")){
            binding!!.adsFree.visibility = GONE

            binding!!.pdfToolsPurchaseBtn.setLayoutParams(
                LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    5f
                )
            )


            val widthInDp = 73
            val heightInDp = 60
            val scale = binding!!.root.resources.displayMetrics.density

            val imgParams = RelativeLayout.LayoutParams(
                (widthInDp * scale).toInt(),
                (heightInDp * scale).toInt()
            ).apply {
                addRule(RelativeLayout.CENTER_VERTICAL)
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                setMargins(0, 0, 41, 0) // Margin end in dp
            }

            binding!!.puchaseIcon.layoutParams = imgParams



            val textViewParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, // Keeps original width
                ViewGroup.LayoutParams.WRAP_CONTENT  // Keeps original height
            ).apply {
                addRule(RelativeLayout.CENTER_VERTICAL)
                setMargins(40, 0, 0, 0) // Sets top and start margins (matching XML)
            }

            binding!!.advanceToolsTextview.layoutParams = textViewParams


            val textViewParams1 = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, R.id.advanceToolsTextview) // Position below
                setMargins(40, 0, 0, 0) // Optional margin
            }

            binding!!.subTitleToolsPdf.layoutParams = textViewParams1



        }else{
            binding!!.adsFree.visibility = VISIBLE
        }


        if (PrefUtilForAppAdsFree.isPremium(requireActivity())) {
//            binding?.freePlanTextview?.text = "Premium Plan"
        }else if (PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
//            binding?.freePlanTextview?.text = "Premium Plan"
        }else if (PrefUtilForPdfTools.isPremium(requireActivity())) {
            binding?.freePlanTextview?.text = "Advance Tools Access"
        }
        else {
            binding?.freePlanTextview?.text = "Free Plan"
        }
    }

    private fun updateSubscriptionUI() {
        val context = requireActivity()

        when {
            PrefUtilForAppAdsFree.getAdsForToolsWeeklyString(context) == "pdf_tools_weekly_plan" -> showSubscriptionUI("Weekly Plan")
            PrefUtilForAppAdsFree.getAdsForToolsMonthlyString(context) == "pdf_tools_monthly_plan" -> showSubscriptionUI("Monthly Plan")
            PrefUtilForAppAdsFree.getAdsForToolsYearlyString(context) == "pdf_tools_annual_plan" -> showSubscriptionUI("Annual Plan")
            else -> hideSubscriptionUI()
        }

        when {
            PrefUtilForAppAdsFree.getAdsForWeeklyString(context) == "weekly_plan" -> showAdsFreeUI("Weekly Plan")
            PrefUtilForAppAdsFree.getAdsForMonthlyString(context) == "monthly_plan" -> showAdsFreeUI("Monthly Plan")
            PrefUtilForAppAdsFree.getAdsForYearlyString(context) == "yearly_plan" -> showAdsFreeUI("Annual Plan")
            else -> hideAdsFreeUI()
        }
    }

    private fun showSubscriptionUI(plan: String) {
        binding!!.selectedPdfToolsPlanShow.visibility = VISIBLE
        binding!!.whenAnyAdvanceToolsPurchaseIcon.visibility = VISIBLE
        binding!!.freePlanTextviewPlan.visibility = VISIBLE
        binding!!.MemberShipCancelBtnTextview.visibility = VISIBLE
        binding!!.puchaseIcon.visibility = GONE
        binding!!.planSetPdfToolsTextview.text = plan
        binding!!.subTitleToolsPdf.text = "You have premium access to\n advance tools"
        binding!!.subTitleToolsPdf.setTextColor(resources.getColor(R.color.pdf_tools_sub_title));


        binding?.freePlanTextview?.text = "Advance Tools "
        binding?.freePlanTextviewPlan?.text = plan


    }

    private fun hideSubscriptionUI() {
        binding!!.selectedPdfToolsPlanShow.visibility = GONE
        binding!!.whenAnyAdvanceToolsPurchaseIcon.visibility = GONE
        binding!!.freePlanTextviewPlan.visibility = GONE
        binding!!.MemberShipCancelBtnTextview.visibility = GONE
        binding!!.puchaseIcon.visibility = VISIBLE

        binding!!.subTitleToolsPdf.text = "Purchase now"
        binding!!.subTitleToolsPdf.setTextColor(resources.getColor(R.color.red));

        binding?.freePlanTextview?.text = "Free Plan"
    }

    private fun showAdsFreeUI(plan: String) {
        binding!!.selectedAddFreePlanShow.visibility = VISIBLE
        binding!!.whenAnyAddRemovePurchaseIcon.visibility = VISIBLE
        binding!!.manageSubscrpionInRemoveAdSide.visibility = VISIBLE
        binding!!.iconAdsfree.visibility = GONE
        binding!!.planSetAddFreeTextview.text = plan

        binding!!.subTitleRemoveAds.text = "No ads for one year with this plan"
        binding!!.subTitleRemoveAds.setTextColor(resources.getColor(R.color.pdf_tools_sub_title));
    }

    private fun hideAdsFreeUI() {
        binding!!.selectedAddFreePlanShow.visibility = GONE
        binding!!.whenAnyAddRemovePurchaseIcon.visibility = GONE
        binding!!.manageSubscrpionInRemoveAdSide.visibility = GONE
        binding!!.iconAdsfree.visibility = VISIBLE

        binding!!.subTitleRemoveAds.text = "Purchase now"
        binding!!.subTitleRemoveAds.setTextColor(resources.getColor(R.color.red));
    }


    private fun rateUsDailog() {

//        val customDialog = CustomDialog(this@SettingActivity)
//        customDialog.show()


        val dialog = Dialog(requireActivity(), R.style.renameDialogStyle)
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
            val desiredWidth = screenWidth - 2 * dpToPx(requireActivity(), 30)
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
                dialog.dismiss()

            }else if (starIsCheckedOne && starIsCheckedTow && !starIsCheckedThree && !starIsCheckedFour && !starIsCheckedFive){
//                    feedBackDailog()

//                val alert = ViewSettingDialog()
//                alert.showDialog(this@SettingActivity)

                feedBackDailog()
                dialog.dismiss()
            }else if (starIsCheckedOne && starIsCheckedTow && starIsCheckedThree && !starIsCheckedFour && !starIsCheckedFive){

//                val alert = ViewPlayStoreDialog()
//                alert.showDialog(this@SettingActivity)

                playStoreDailog()
                dialog.dismiss()

            }else if (starIsCheckedOne && starIsCheckedTow && starIsCheckedThree && starIsCheckedFour && !starIsCheckedFive){

//                val alert = ViewPlayStoreDialog()
//                alert.showDialog(this@SettingActivity)

                playStoreDailog()
                dialog.dismiss()
            }
            else if (starIsCheckedOne && starIsCheckedTow && starIsCheckedThree && starIsCheckedFour && starIsCheckedFive){
//                val alert = ViewPlayStoreDialog()
//                alert.showDialog(this@SettingActivity)

                playStoreDailog()
                dialog.dismiss()
            }else{
                Toast.makeText(requireActivity(),"Please Select Rarting", Toast.LENGTH_SHORT).show()
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
        val dialog = Dialog(requireActivity(), R.style.renameDialogStyle)
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
            val desiredWidth = screenWidth - 2 * dpToPx(requireActivity(), 30)
            val params = dialog.window!!.attributes
            params.width = desiredWidth
            window.attributes = params
        }



        val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
        val rateUsBtn = dialog.findViewById<TextView>(R.id.rateUsBtn)


        rateUsBtn.setOnClickListener {
            openPlayStore(requireActivity())
            dialog.dismiss()
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

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                val shareMessage = "\nLet me recommend you this application\n\n" +
                        "https://play.google.com/store/apps/details?id=" + requireContext().packageName + "\n\n"
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }
            startActivity(Intent.createChooser(shareIntent, "Choose one"))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to share the app", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendFeedbackEmail(activity: FragmentActivity) {
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

    private fun feedBackDailog() {

        val dialog = Dialog(requireActivity(), R.style.renameDialogStyle)
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
            val desiredWidth = screenWidth - 2 * dpToPx(requireActivity(), 30)
            val params = dialog.window!!.attributes
            params.width = desiredWidth
            window.attributes = params
        }

        val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
        val rateUsBtn = dialog.findViewById<TextView>(R.id.rateUsBtn)


        rateUsBtn.setOnClickListener {
            sendFeedbackEmail(requireActivity())
            dialog.dismiss()
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

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private fun openPrivacyPolicy() {
        val intent = Intent(requireContext(), PrivacyPolicyActivity::class.java)
        startActivity(intent)
    }

    private fun logAnalyticsEvent() {
        val bundle = Bundle().apply {
            putString("fragment_name", "SettingFragment")
            putString(FirebaseAnalytics.Param.ITEM_NAME, "SettingFragment")
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
        }
        analytics.logEvent("fragment_created", bundle)
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    override fun setupToolbar(toolbarController: ToolbarController?) {
        toolbarController?.let {
            homeToolbarTitle = it.getToolbarTitle()
            homeSelectAllButton = it.getSelectAllButton()
            homeToolbarSelectedItem = it.getToolbarSelectedItem()
            homeSearchButton = it.getSearchButton()
            homeSortButton = it.getSortButton()
            homeDeleteButton = it.getDeleteButton()
            homeSearchLayout = it.getSearchLayout()
            homeToolbar = it.getToolbar()
            homeSearchBackBtn = it.getSearchBackBtn()
            homeSearchEditText = it.getSearchEditText()
            homeClearButton = it.getClearButton()
            homeSettingButton = it.getSettingBtn()

            homeToolbarTitle?.visibility = View.VISIBLE
            homeSelectAllButton?.visibility = View.GONE
            homeToolbarSelectedItem?.visibility = View.GONE
            homeSearchButton?.visibility = View.GONE
            homeSortButton?.visibility = View.GONE
            homeDeleteButton?.visibility = View.GONE
//            homeSearchLayout?.visibility = View.GONE
//            homeSearchEditText?.visibility = View.GONE
//            homeSearchBackBtn?.visibility = View.GONE

            homeClearButton?.visibility = View.GONE
            homeSettingButton?.visibility = View.GONE

//            homeToolbarTitle?.setText(R.string.docsign_non)
            Log.d("checkfragmentselected", "Setting Fragment toolbarController != null")

        }
    }

    override fun onResume() {
        super.onResume()
        try {
            homeToolbarTitle?.visibility = View.VISIBLE
            homeSelectAllButton?.visibility = View.GONE
            homeToolbarSelectedItem?.visibility = View.GONE
            homeSearchButton?.visibility = View.GONE
            homeSortButton?.visibility = View.GONE
            homeDeleteButton?.visibility = View.GONE
//            homeSearchLayout?.visibility = View.GONE
//            homeSearchBackBtn?.visibility = View.GONE
//            homeSearchEditText?.visibility = View.GONE
            homeClearButton?.visibility = View.GONE


            Log.d("checkfragmentselected","onResume Setting Frgment")

            // Configure Google Sign-In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


            // Check if user is already signed in
            val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

            if (account!=null){

                binding!!.logoutBtn.visibility = View.VISIBLE
                binding!!.loginAccount.visibility = View.GONE
            }else{
                binding!!.logoutBtn.visibility = View.GONE
                binding!!.loginAccount.visibility = View.VISIBLE
            }



        } catch (e: NullPointerException) {
        }


        // Verify subscriptions and update UI when returning to settings
        BillingIASForPdfTools.verifySubPurchase(requireContext()) {
            requireActivity().runOnUiThread {
                updateSubscriptionUI()
            }
        }


        Log.d("checkfragmentselected","Selected   "+PrefUtilForAppAdsFree.getAdsForWeeklyString(requireActivity()).toString())
    }

    override fun onStart() {
        super.onStart()
        Log.d("checkfragmentselected","onStart Setting Frgment")
    }

    override fun onPause() {
        super.onPause()



        Log.d("checkfragmentselected","onPause Setting Frgment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        Log.d("checkcheck","onDestroyView Setting Frgment")
    }
}
