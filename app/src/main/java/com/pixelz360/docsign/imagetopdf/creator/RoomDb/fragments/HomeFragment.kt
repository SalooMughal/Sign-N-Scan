package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity.ViewPagerAdapter
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityHomeBinding
import com.pixelz360.docsign.imagetopdf.creator.databinding.FragmentHomeBinding
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel


class HomeFragment : Fragment() {

//    private lateinit var viewpager: ViewPager
//    private lateinit var tabs: TabLayout
//
//    private var pdfFileViewModel: PdfFileViewModel? = null
//    var pdfFiles: List<PdfFile>? = null





    lateinit var binding:FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val view: View = binding.root

//
//        pdfFileViewModel = ViewModelProvider(this).get(PdfFileViewModel::class.java)
//
//        pdfFileViewModel!!.allPdfFiles.observe(requireActivity()) { pdfFileList ->
//            pdfFiles = pdfFileList
//
//        }
//
//
//        Log.d("checksize12", pdfFiles?.size.toString())
//
//
//
//        viewpager = view.findViewById(R.id.viewpager)
//        tabs = view.findViewById(R.id.tabs)
//
//        setupViewPager(viewpager)
//        tabs.setupWithViewPager(viewpager)
//
//        // Apply custom views to each tab
//        for (i in 0 until tabs.tabCount) {
//            val tab = tabs.getTabAt(i)
//            if (tab != null) {
//                if (i == 3) { // Assuming the Favorite tab is the fourth tab (index 3)
//                    tab.customView = createFavoriteTabView()
//                } else {
//                    tab.customView = createTextTabView(tabs.getTabAt(i)?.text.toString())
//                }
//            }
//        }
//
//        // Initial setup for tab background
//        updateTabBackgrounds(0)
//
//        // Call setupToolbar on the initial fragment to set up the toolbar right away
//        val initialFragment =
//            (viewpager.getAdapter() as ViewPagerAdapter).getItem(viewpager.getCurrentItem())
//        if (initialFragment is ToolbarSettings) {
////            (initialFragment as ToolbarSettings).setupToolbar(this)
//        }
//
//        // Set up tab selection listeners
//        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                updateTabBackgrounds(tab.position)
//
//                // Get current fragment and update toolbar
//                val fragment = (viewpager.getAdapter() as ViewPagerAdapter).getItem(tab.position)
//                if (fragment is ToolbarSettings) {
////                    (fragment as ToolbarSettings).setupToolbar(this@HomeFragment)
//                }
//
//
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                // No action needed; handled by updateTabBackgrounds
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
//
//
//




        return view
    }


//    private fun createTextTabView(title: String): View {
//        val view = layoutInflater.inflate(R.layout.custom_tab_item, null)
//        val textView = view.findViewById<TextView>(R.id.tab_text)
//        textView.text = title
//        return view
//    }
//
//    private fun createFavoriteTabView(): View {
//        return layoutInflater.inflate(R.layout.custom_tab_favorite, null)
//    }
//    private fun setupViewPager(viewpager: ViewPager) {
//        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
//        adapter.addFragment(RoomListFragment(), getString(R.string.all_files))
////        adapter.addFragment(RoomListFragment(), getString(R.string.all_files))
//        adapter.addFragment(RoomSignaturesFilesFragment(), getString(R.string.signed_docs))
//        adapter.addFragment(RoomScannerFragment(), getString(R.string.scanned_docs))
//        adapter.addFragment(FavoriteFragment(), getString(R.string.home))
//        viewpager.adapter = adapter
//    }
//
//
//
//    @SuppressLint("ResourceAsColor")
//    private fun updateTabBackgrounds(selectedTabIndex: Int) {
//        for (i in 0 until tabs.tabCount) {
//            val tab = tabs.getTabAt(i)
//            if (tab != null) {
//                if (i == 3) { // Favorite tab
//                    val icon = tab.customView?.findViewById<ImageView>(R.id.tab_icon)
//                    if (i == selectedTabIndex) {
//                        icon?.setImageResource(R.drawable.favoraite_fragment_selected_icon) // Selected heart icon
//                        tab.customView?.setBackgroundColor(android.R.color.transparent)
//
//                    } else {
//                        icon?.setImageResource(R.drawable.favoraite_fragment_un_selected_icon) // Unselected heart icon
//                        tab.customView?.setBackgroundColor(android.R.color.transparent)
//                    }
//                } else { // Regular text tabs
//                    val textView = tab.customView?.findViewById<TextView>(R.id.tab_text)
//                    if (i == selectedTabIndex) {
//                        tab.customView?.setBackgroundResource(R.drawable.tab_selected_background)
//                        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
////                        textView?.setTypeface(Typeface.DEFAULT_BOLD)
//                    } else {
//                        tab.customView?.setBackgroundResource(R.drawable.tab_unselected_background)
//                        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
////                        textView?.setTypeface(Typeface.DEFAULT)
//                    }
//                }
//            }
//        }
//    }
//
//    internal class ViewPagerAdapter(manager: FragmentManager) :
//        FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//
//        private val fragmentList = mutableListOf<Fragment>()
//        private val fragmentTitleList = mutableListOf<String>()
//
//        override fun getItem(position: Int): Fragment = fragmentList[position]
//        override fun getCount(): Int = fragmentList.size
//
//        fun addFragment(fragment: Fragment, title: String) {
//            fragmentList.add(fragment)
//            fragmentTitleList.add(title)
//            Log.d("checkfragment",fragmentList.size.toString())
//        }
//
//        override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]
//    }
//
////    override fun onBackPressed() {
////        val builder = AlertDialog.Builder(this)
////        builder.setTitle("EXIT")
////            .setMessage("Are you sure you want to exit the app?")
////            .setPositiveButton("Yes") { _, _ -> finishAffinity() }
////            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
////        val dialog = builder.create()
////        dialog.setOnShowListener {
////            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
////                .setTextColor(ContextCompat.getColor(this, R.color.black))
////            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
////                .setTextColor(ContextCompat.getColor(this, R.color.black))
////        }
////        dialog.show()
////    }
//
//    fun getToolbarTitle(): TextView = binding.toolbarTitle
//    fun getSearchButton(): ImageButton = binding.searchBtn
//    fun getSortButton(): ImageButton = binding.fileSortingBtn
//    fun getDeleteButton(): ImageButton = binding.deleteBtn
//    fun getSelectAllButton(): TextView = binding.allSelctedItemBtn
//    fun getToolbarSelectedItem(): TextView = binding.toolbarSelectedItem
//    fun getSearchLayout(): LinearLayout = binding.searchLayout
//    fun getToolbar(): androidx.appcompat.widget.Toolbar = binding.toolbar
//    fun getSearchBackBtn(): ImageView = binding.searchBackBtn
//    fun getSearchEditText(): EditText = binding.searchEditText
//    fun getClearButton(): ImageView = binding.clearButton


}