package com.pixelz360.docsign.imagetopdf.creator


import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.AppDatabase
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.ActionFragment
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.FavoriteFragment
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.PremiumFragment
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.RecentFragment
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.RoomListFragment
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.SettingFragment
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.ToolbarController
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments.ToolbarSettings
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityHomeBinding
import com.pixelz360.docsign.imagetopdf.creator.editModule.EditMoudleActivity
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.models.ModelImage
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.DigitalSignatureActivity
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), ToolbarController {

    private lateinit var viewpager: NonSwipeableViewPager
    private lateinit var tabs: TabLayout
    private lateinit var binding: ActivityHomeBinding

    private var pdfFileViewModel: PdfFileViewModel? = null
    var pdfFiles: List<PdfFile>? = null



    private var pressedTime: Long = 0
    private lateinit var progressDialog: ProgressDialog
    private lateinit var db: AppDatabase
    private var bannerAdIsLoaded = false
    private val allImageArrayList = ArrayList<ModelImage>()
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Log.d("Permission111", "POST_NOTIFICATIONS permission granted")
        } else {
            Log.d("Permission111", "POST_NOTIFICATIONS permission denied")
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(binding.root)



        pdfFileViewModel = ViewModelProvider(this).get(PdfFileViewModel::class.java)







//        loadInterstitialAd()

        Log.d("checksize12", "out 1 "+pdfFiles?.size.toString())


        viewpager = findViewById(R.id.viewpager)
        tabs = findViewById(R.id.tabs)

//        viewpager.setPagingEnabled(false)


        TabLayoutSetting()



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
        }

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "pdf_database").build()
        Log.d("checkdebug", "onCreate")

        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait")
            setCanceledOnTouchOutside(false)
        }



        val intent = intent
        val scannerSide = intent.getStringExtra("ScannerSide")
        Log.d("checkfragmentselected", "scannerSide. "+scannerSide)

        if (scannerSide == "ScannerSide") {
//            binding.mainHome.setBackgroundResource(R.drawable.un_selected_main_icon_bg)
//            binding.mainToolsLayout.setBackgroundResource(R.drawable.selected_main_icon_bg)
            loadActionFragment()

            Log.d("checkfragmentselected", "ScannerSide if")





        }else if (scannerSide == "PreviewSide") {

            setSelectedTab(1)


            binding.mainRalativelayout.visibility = VISIBLE

            // Call setupToolbar on the initial fragment to set up the toolbar right away
            val initialFragment = (viewpager.getAdapter() as ViewPagerAdapter).getItem(viewpager.getCurrentItem())
            if (initialFragment is ToolbarSettings) {
                (initialFragment as ToolbarSettings).setupToolbar(this)
            }

            Log.d("checkfragmentselected", "homeLayout")

            binding.frameLayout.visibility = GONE
            binding.settingBtn.visibility = GONE



//            val actionFragment = ActionFragment()



            val fragment = supportFragmentManager.findFragmentByTag("ActionFragment")
            if (fragment is ActionFragment) {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.remove(fragment).commit()
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            } else {
                Log.e("FragmentError", "Fragment not found or not an instance of ActionFragment")
            }



        }else if (scannerSide == "SubscribtionSide") {

            setSelectedTab(3)


        }
        else {
//            loadPDFFragment(bannerAdIsLoaded)

//            binding.mainRalativelayout.visibility = GONE
//            binding.frameLayout.visibility = VISIBLE

//            goToNextActivity()
            setSelectedTab(2)

            binding.mainRalativelayout.visibility = GONE
            binding.frameLayout.visibility = VISIBLE

            Log.d("checkfragmentselected", "ScannerSide else")

        }

        binding.fab.setOnClickListener {
            val dialog = Dialog(this, R.style.CustomDialogStyle)
            dialog.setContentView(R.layout.fab_bottom_sheet_layout)
            dialog.window?.apply {
                setGravity(Gravity.BOTTOM)
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            dialog.findViewById<LinearLayout>(R.id.convertedPdfLayout).setOnClickListener {
                pickImageFromGallery()
                dialog.dismiss()
            }
            dialog.findViewById<LinearLayout>(R.id.signPdfLayout).setOnClickListener {
                startActivityForResult(Intent(this, DigitalSignatureActivity::class.java).apply {
                    putExtra("ActivityAction", "FileSearch")
                }, Merge_Request_CODE)
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.homeLayout.setOnClickListener {

            setSelectedTab(1)

            binding.mainRalativelayout.visibility = VISIBLE

            // Call setupToolbar on the initial fragment to set up the toolbar right away
//            val initialFragment = (viewpager.getAdapter() as ViewPagerAdapter).getItem(viewpager.getCurrentItem())
//            if (initialFragment is ToolbarSettings) {
//                (initialFragment as ToolbarSettings).setupToolbar(this)
//            }


            TabLayoutSetting()




            Log.d("checkfragmentselected", "homeLayout")

            binding.frameLayout.visibility = GONE
            binding.settingBtn.visibility = GONE


            val fragment = supportFragmentManager.findFragmentByTag("ActionFragment")
            if (fragment is ActionFragment) {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.remove(fragment).commit()
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            } else {
                Log.e("FragmentError", "Fragment not found or not an instance of ActionFragment")
            }



        }

        binding.toolsLayout.setOnClickListener {

            setSelectedTab(2)

//            goToNextActivity() // No internet, proceed to the next activity


//            if (!isInternetAvailable()) {
//                goToNextActivity() // No internet, proceed to the next activity
//            } else {
//
//                adManager11!!.showAdIfAvailable(this@HomeActivity, object : AdCallback {
//                    override fun onAdDismissed() {
//                        // Define your custom action here after the ad is dismissed
//                        goToNextActivity()
//                    }
//
//                    override fun onAdFailedToShow() {
//                        // Define your custom action here if the ad fails to show
//                        goToNextActivity()
//                    }
//                })
//
//
//
//
//            }










//            if (!isInternetAvailable()) {
//                goToNextActivity()
//            } else {
//                showAdOrLoad()
//            }


//            binding.imageHome.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
//            binding.toolsIcon.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)
//            binding.mainHome.setBackgroundResource(R.drawable.un_selected_main_icon_bg)
//            binding.mainToolsLayout.setBackgroundResource(R.drawable.selected_main_icon_bg)
//            loadActionFragment()

        }

        binding.settingLayout.setOnClickListener {

//            binding.imageHome.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
//            binding.textHome.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))
//
//            binding.toolsIcon.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
//            binding.toolsText.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))
//
//            binding.settingIcon.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)
//            binding.settingText.setTextColor(ContextCompat.getColor(this, R.color.red))
//
//            binding.premiumProfileImage.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
//            binding.premiumText.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))

            setSelectedTab(3)



        }

        binding.premiumLayout.setOnClickListener {


            binding.imageHome.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
            binding.textHome.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))

            binding.toolsIcon.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
            binding.toolsText.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))

            binding.settingIcon.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
            binding.settingText.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))

            binding.premiumProfileImage.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)
            binding.premiumText.setTextColor(ContextCompat.getColor(this, R.color.red))


            loadPremiumFragment()

        }



        Log.d("checkdebug11", "3")

        setupFirebaseAnalytics()









    }

    private fun TabLayoutSetting() {
        setupViewPager(viewpager)
        tabs.setupWithViewPager(viewpager)

        // Apply custom views to each tab
        for (i in 0 until tabs.tabCount) {
            val tab = tabs.getTabAt(i)
            if (tab != null) {

                tab.customView = createTextTabView(tabs.getTabAt(i)?.text.toString())

            }
        }

        // Initial setup for tab background
        updateTabBackgrounds(0)


        // Call setupToolbar on the initial fragment to set up the toolbar right away
        val initialFragment = (viewpager.getAdapter() as ViewPagerAdapter).getItem(viewpager.getCurrentItem())
        if (initialFragment is ToolbarSettings) {
            (initialFragment as ToolbarSettings).setupToolbar(this@HomeActivity)

            Log.d("checkfragmentselected","initialFragment. "+initialFragment)

        }


        Log.d("checkfragmentselected","viewpager.getCurrentItem(). "+viewpager.getCurrentItem())

        // Set up tab selection listeners
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {


                updateTabBackgrounds(tab.position)

                // Get current fragment and update toolbar
                val fragment = (viewpager.getAdapter() as ViewPagerAdapter).getItem(tab.position)
                if (fragment is ToolbarSettings) {
                    (fragment as ToolbarSettings).setupToolbar(this@HomeActivity)
                }

                Log.d("checkfragmentselected","onTabSelected. "+tab.position)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // No action needed; handled by updateTabBackgrounds

                Log.d("checkfragmentselected","onTabUnselected "+tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

                Log.d("checkfragmentselected","onTabReselected "+tab.position)

            }
        })




    }

    fun setSelectedTab(selectedTab: Int) {
        // Reset all icons to unselected state (gray)
//        binding.imageHome.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
//        binding.toolsIcon.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
//        binding.settingIcon.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)

        binding.imageHome.setAnimation(R.raw.main_un_selected_home) // Change animation
        binding.imageHome.cancelAnimation()

        binding.toolsIcon.setAnimation(R.raw.main_un_selected_tools) // Change animation
        binding.toolsIcon.cancelAnimation()

        binding.settingIcon.setAnimation(R.raw.main_un_selected_settings) // Change animation
        binding.settingIcon.cancelAnimation()

        binding.textHome.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))
        binding.toolsText.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))
        binding.settingText.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))

        // Apply red color to selected tab
        when (selectedTab) {
            1 -> {
//                binding.imageHome.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)
                binding.imageHome.setAnimation(R.raw.main_home) // Change animation
                binding.imageHome.playAnimation()
                binding.textHome.setTextColor(ContextCompat.getColor(this, R.color.red))
                binding.imageHome.loop(false)
            }
            2 -> {
//                binding.toolsIcon.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)

                binding.toolsIcon.setAnimation(R.raw.main_tools) // Change animation
                binding.toolsIcon.playAnimation()

                binding.toolsText.setTextColor(ContextCompat.getColor(this, R.color.red))
                binding.toolsIcon.loop(false)



//        binding.mainHome.setBackgroundResource(R.drawable.un_selected_main_icon_bg)
//        binding.mainToolsLayout.setBackgroundResource(R.drawable.selected_main_icon_bg)
                loadActionFragment()
            }
            3 -> {
//                binding.settingIcon.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)

                binding.settingIcon.setAnimation(R.raw.main_settings) // Change animation
                binding.settingIcon.playAnimation()
                binding.settingText.setTextColor(ContextCompat.getColor(this, R.color.red))
                binding.settingIcon.loop(false)
                loadSettinngFragment()
            }
        }
    }

    private fun loadPremiumFragment() {

        val premiumFragment = PremiumFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, premiumFragment, "PdfListFragment").commitAllowingStateLoss()
        binding.mainRalativelayout.visibility = GONE

        (premiumFragment as ToolbarSettings).setupToolbar(this@HomeActivity)

        binding.frameLayout.visibility = VISIBLE

        Log.d("checkdebug11", "4")


    }

    private fun loadSettinngFragment() {

        removeHomeViewPagerFragments() // 🔥


        val settingFragment = SettingFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, settingFragment, "SettingFragment").commitAllowingStateLoss()
        binding.mainRalativelayout.visibility = GONE

        (settingFragment as ToolbarSettings).setupToolbar(this@HomeActivity)






        binding.frameLayout.visibility = VISIBLE
        binding.searchBtn.visibility = GONE
        binding.allSelctedItemBtn.visibility = GONE

        Log.d("checkdebug11", "4")





    }

    private fun removeHomeViewPagerFragments() {
        val adapter = viewpager.adapter as? ViewPagerAdapter ?: return

        for (i in 0 until adapter.count) {
            val tag = "android:switcher:${viewpager.id}:$i"
            val fragment = supportFragmentManager.findFragmentByTag(tag)
            if (fragment != null) {
                Log.d("FragmentCleanup", "Removing fragment at $tag -> ${fragment::class.java.simpleName}")
                supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitNowAllowingStateLoss() // 🔥 Use commitNow to remove instantly
            }
        }

        // Optional: reset ViewPager adapter if needed
        viewpager.adapter = null
    }



//
//    private fun loadInterstitialAd() {
//        val adRequest = AdRequest.Builder().build()
//        isAdLoading = true
//        binding.progressBar.visibility = View.GONE
//
//        timeoutRunnable = Runnable {
//            if (isAdLoading) {
//                isAdLoading = false
//                binding.progressBar.visibility = View.GONE
//                goToNextActivity()
//            }
//        }
//        handler.postDelayed(timeoutRunnable, 10000)
//
//        InterstitialAd.load(this, getString(R.string.language_intertial_ad), adRequest, object : InterstitialAdLoadCallback() {
//            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                mInterstitialAd = interstitialAd
//                isAdLoading = false
//                binding.progressBar.visibility = View.GONE
//                handler.removeCallbacks(timeoutRunnable)
//                setAdCallbacks()
//            }
//
//            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                mInterstitialAd = null
//                isAdLoading = false
//                binding.progressBar.visibility = View.GONE
//                handler.removeCallbacks(timeoutRunnable)
//                goToNextActivity()
//            }
//        })
//    }
//
//    private fun setAdCallbacks() {
//        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//            override fun onAdDismissedFullScreenContent() {
//                mInterstitialAd = null
//                loadInterstitialAd()
//                goToNextActivity()
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                mInterstitialAd = null
//                loadInterstitialAd()
//                goToNextActivity()
//            }
//        }
//    }
//
//    private fun showAdOrLoad() {
//        if (mInterstitialAd != null) {
//            mInterstitialAd?.show(this)
//        } else if (isAdLoading) {
//            binding.progressBar.visibility = View.VISIBLE
//        } else {
//            goToNextActivity()
//        }
//    }

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }


    fun goToNextActivity() {
        binding.imageHome.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
        binding.textHome.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))

        binding.toolsIcon.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)
        binding.toolsText.setTextColor(ContextCompat.getColor(this, R.color.red))

        binding.settingIcon.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
        binding.settingText.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))

        binding.premiumProfileImage.setColorFilter(ContextCompat.getColor(this, R.color.unselected), PorterDuff.Mode.SRC_ATOP)
        binding.premiumText.setTextColor(ContextCompat.getColor(this, R.color.action_main_icon_color))






//        binding.mainHome.setBackgroundResource(R.drawable.un_selected_main_icon_bg)
//        binding.mainToolsLayout.setBackgroundResource(R.drawable.selected_main_icon_bg)
        loadActionFragment()

//
//        // Set visibility to GONE for each item
//        binding.toolbarTitle.setVisibility(VISIBLE)
//        binding.allSelctedItemBtn.setVisibility(GONE)
//        binding.toolbarSelectedItem.setVisibility(GONE)
//        binding.searchBtn.setVisibility(GONE)
//        binding.fileSortingBtn.setVisibility(GONE)
//        binding.deleteBtn.setVisibility(GONE)
//        binding.searchLayout.setVisibility(GONE)
//        binding.searchBackBtn.setVisibility(GONE)
//        binding.searchEditText.setVisibility(GONE)
//        binding.clearButton.setVisibility(GONE)
//        binding.settingBtn.setVisibility(VISIBLE)

        Log.d("checkfragmentselected", "goToNextActivity")


    }


    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission111", "POST_NOTIFICATIONS permission already granted")
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
            Log.d("Permission111", "Showing rationale for POST_NOTIFICATIONS permission")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun loadActionFragment() {



        val actionFragment = ActionFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, actionFragment, "ActionFragment").commitAllowingStateLoss()
        binding.mainRalativelayout.visibility = GONE

        (actionFragment as ToolbarSettings).setupToolbar(this@HomeActivity)

        binding.frameLayout.visibility = VISIBLE

        Log.d("checkdebug11", "4")



//        val fragment = supportFragmentManager.findFragmentByTag("PdfListFragment")
//        if (fragment is RoomListFragment) {
//            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
//            ft.remove(fragment).commit()
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
//        } else {
//            Log.e("FragmentError", "Fragment not found or not an instance of RoomListFragment")
//        }




    }

    private fun pickImageFromGallery() {
        val intent = Intent(this, EditMoudleActivity::class.java)
        startActivity(intent)
    }



    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()



        } else {
            Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT).show()






            val currentTabIndex = tabs.selectedTabPosition

            if (currentTabIndex==0){
                val intent = Intent("com.example.MY_CUSTOM_ACTION_RoomListFragment")
                intent.putExtra("onBackPressed_RoomListFragment", "onBackPressed")
                sendBroadcast(intent)
            }else if (currentTabIndex==1){

                val intent = Intent("com.example.MY_CUSTOM_ACTION_RecentFragment")
                intent.putExtra("onBackPressed_RecentFragment", "onBackPressed")
                sendBroadcast(intent)

            }else if (currentTabIndex==2){

                val intent = Intent("com.example.MY_CUSTOM_ACTION_FavoriteFragment")
                intent.putExtra("onBackPressed_FavoriteFragment", "onBackPressed")
                sendBroadcast(intent)

            }else if (currentTabIndex==3){

                val intent = Intent("com.example.MY_CUSTOM_ACTION_PdfToJpgFragment")
                intent.putExtra("onBackPressed_PdfToJpgFragment", "onBackPressed")
                sendBroadcast(intent)

            }


            Log.d("checkbackpress",currentTabIndex.toString())



        }
        pressedTime = System.currentTimeMillis()
    }

    fun getNavigationLayout(): LinearLayout {
        return findViewById(R.id.navigationLayout)
    }

    private fun setupFirebaseAnalytics() {
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle().apply {
            putString("activity_name", "MainActivity")
        }
        analytics.logEvent("activity_created", bundle)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 111
        private const val PICK_IMAGE_REQUEST = 101
        private const val Merge_Request_CODE = 43
    }


    private fun createTextTabView(title: String): View {
        val view = layoutInflater.inflate(R.layout.custom_tab_item, null)
        val textView = view.findViewById<TextView>(R.id.tab_text)
//        textView.text = title

//        Log.d("checksize12", "out 2 "+pdfFiles?.size.toString())

        pdfFileViewModel!!.getPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(this@HomeActivity),"pdf").observe(this){ pdfFileList ->

            pdfFiles = pdfFileList

            Log.d("checksize12", "inside  "+pdfFiles?.size.toString())


            if (pdfFiles.isNullOrEmpty()){
                Log.d("checkview", "data no avalible home screen side  "+pdfFiles?.size.toString())
            }else{
                Log.d("checkview", "data is avalible home screen side  "+pdfFiles?.size.toString())
            }


            if (title.equals(" File ")) {
                val fileCount = pdfFiles?.size?.toInt()
                val fileText = if (fileCount == 1) "1 File" else "$fileCount Files"
                textView.text = fileText


//                textView.text = title+"("+pdfFiles?.size.toString()+")"

            }else{
                textView.text = title
            }


        }

//        pdfFileViewModel!!.allPdfFiles.observe(this) { pdfFileList ->
//            pdfFiles = pdfFileList
//
//            Log.d("checksize12", "inside  "+pdfFiles?.size.toString())
//
//
//            if (pdfFiles.isNullOrEmpty()){
//                Log.d("checkview", "data no avalible home screen side  "+pdfFiles?.size.toString())
//            }else{
//                Log.d("checkview", "data is avalible home screen side  "+pdfFiles?.size.toString())
//            }
//
//
//            if (title.equals(" File ")) {
//                val fileCount = pdfFiles?.size?.toInt()
//                val fileText = if (fileCount == 1) "1 File" else "$fileCount Files"
//                textView.text = fileText
//
//
////                textView.text = title+"("+pdfFiles?.size.toString()+")"
//
//            }else{
//                textView.text = title
//            }
//
//        }

        textView.text = title+"(0)"



        return view
    }

    private fun createFavoriteTabView(): View {
        return layoutInflater.inflate(R.layout.custom_tab_favorite, null)
    }
    private fun setupViewPager(viewpager: ViewPager) {


        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(RoomListFragment(), " File ")
//        adapter.addFragment(RoomSignaturesFilesFragment(), " Signed docs ")
//        adapter.addFragment(RoomScannerFragment(), " Scan docs ")
//        adapter.addFragment(MargeFragment(), " Marge docs ")
//        adapter.addFragment(SplitFragment(), " Split docs ")

        adapter.addFragment(RecentFragment(), " Recent ")
        adapter.addFragment(FavoriteFragment(), "Favorites")
//        adapter.addFragment(PdfToJpgFragment(), "Pdf To Jpg")

        viewpager.adapter = adapter

    }



    private fun updateTabBackgrounds(selectedTabIndex: Int) {
        for (i in 0 until tabs.tabCount) {
            val tab = tabs.getTabAt(i)
            if (tab != null) {

                val textView = tab.customView?.findViewById<TextView>(R.id.tab_text)
                if (i == selectedTabIndex) {
                    tab.customView?.setBackgroundResource(R.drawable.tab_selected_background)
                    textView?.setTextColor(ContextCompat.getColor(this, R.color.white))
//                        textView?.setTypeface(Typeface.DEFAULT_BOLD)
//                        textView?.setPadding(4, 0, 4, 0);

                } else {
                    tab.customView?.setBackgroundResource(R.drawable.tab_unselected_background)
                    textView?.setTextColor(ContextCompat.getColor(this, R.color.black))
//                        textView?.setTypeface(Typeface.DEFAULT)
//                        textView?.setPadding(4, 0, 4, 0);
                }

            }
        }
    }

    internal class ViewPagerAdapter(manager: FragmentManager) :
        FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragmentList = mutableListOf<Fragment>()
        private val fragmentTitleList = mutableListOf<String>()

        override fun getItem(position: Int): Fragment = fragmentList[position]
        override fun getCount(): Int = fragmentList.size

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
            Log.d("checkfragment",fragmentList.size.toString())
        }

        override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]
    }

//    override fun onBackPressed() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("EXIT")
//            .setMessage("Are you sure you want to exit the app?")
//            .setPositiveButton("Yes") { _, _ -> finishAffinity() }
//            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
//        val dialog = builder.create()
//        dialog.setOnShowListener {
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
//                .setTextColor(ContextCompat.getColor(this, R.color.black))
//            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//                .setTextColor(ContextCompat.getColor(this, R.color.black))
//        }
//        dialog.show()
//    }



    override fun getToolbarTitle(): ConstraintLayout = binding.toolbarTitle
    override fun getSearchButton(): ImageButton = binding.searchBtn
    override fun getSortButton(): ImageButton = binding.fileSortingBtn
    override fun getfileListOrGridButton(): ImageButton = binding.fileListOrGridBtn
    override fun getDeleteButton(): ImageButton = binding.deleteBtn
    override fun getSelectAllButton(): TextView = binding.allSelctedItemBtn
    override fun getToolbarSelectedItem(): TextView = binding.toolbarSelectedItem
    override fun getSearchLayout(): LinearLayout = binding.searchLayout
    override fun getToolbar(): androidx.appcompat.widget.Toolbar = binding.toolbar
    override fun getSearchBackBtn(): ImageView = binding.searchBackBtn
    override fun getSearchEditText(): EditText = binding.searchEditText
    override fun getClearButton(): ImageView = binding.clearButton
    override fun getSettingBtn(): ImageButton = binding.settingBtn
    override fun getTabs(): TabLayout = binding.tabs
    override fun getViewpager(): NonSwipeableViewPager = binding.viewpager
    override fun getBannerAdsLayout(): LinearLayout = binding.homeBannerAdsLayout
    override fun getnavigationLayout(): LinearLayout = binding.navigationLayout
    override fun gettabsMainLayout(): RelativeLayout = binding.tabsMainLayout


    override fun onResume() {
        super.onResume()
        Log.d("checkfragmentselected", "HomeActivity onResume")

    }


    override fun onStart() {
        super.onStart()


        //
//        // Initialize Firebase Remote Config
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(10) // 1 hour
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)


        // Fetch the remote config parameters
        remoteConfig.fetchAndActivate().addOnCompleteListener { task: Task<Boolean?> ->
            if (task.isSuccessful) {
//                long minSupportedVersion = remoteConfig.getLong("min_supported_version");
                val latestVersion = remoteConfig.getLong("latest_version")


                try {
                    val pInfo = packageManager.getPackageInfo(packageName, 0)
                    val currentVersionCode = pInfo.versionCode

                    Log.d("checkupdate", "showSoftUpdateDialog()   currentVersionCode  $currentVersionCode")
                    Log.d("checkupdate", "showSoftUpdateDialog()   latestVersion  $latestVersion")


                    if (currentVersionCode < latestVersion) {
                        // Optionally, prompt for a soft update
                        showForceUpdateDialog()
                        Log.d("checkupdate", "showSoftUpdateDialog()   currentVersionCode  $currentVersionCode")
                        Log.d("checkupdate", "showSoftUpdateDialog()   latestVersion  $latestVersion")
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    Log.d("checkupdate", "NameNotFoundException  " + e.message)
                }
            } else {
            }
        }



        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("checkupdate", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("checkupdate", "FCM Token: $token")
//            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })



    }



    private fun showForceUpdateDialog() {
        AlertDialog.Builder(this@HomeActivity)
            .setTitle("Update Required")
            .setMessage("Please update to the latest version to continue using the app.")
            .setPositiveButton("Update") { dialog, which ->
                // Redirect to Play Store
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                startActivity(intent)
            }
            .setCancelable(false)
            .show()
    }



}