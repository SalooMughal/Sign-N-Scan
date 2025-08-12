package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments

import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.tabs.TabLayout
import com.pixelz360.docsign.imagetopdf.creator.NonSwipeableViewPager

interface ToolbarController {
    fun getToolbarTitle(): ConstraintLayout
    fun getSearchButton(): ImageButton
    fun getSortButton(): ImageButton
    fun getfileListOrGridButton(): ImageButton
    fun getDeleteButton(): ImageButton
    fun getSettingBtn(): ImageButton
    fun getSelectAllButton(): TextView
    fun getToolbarSelectedItem(): TextView
    fun getSearchLayout(): LinearLayout
    fun getToolbar(): Toolbar
    fun getSearchBackBtn(): ImageView
    fun getClearButton(): ImageView
    fun getSearchEditText(): EditText
    fun getTabs(): TabLayout
    fun getViewpager(): NonSwipeableViewPager
    fun getBannerAdsLayout(): LinearLayout
    fun getnavigationLayout(): LinearLayout
    fun gettabsMainLayout(): RelativeLayout
}

