package com.cherry.lib.doc.pdf

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cherry.lib.doc.R
import com.cherry.lib.doc.databinding.PageItemPdfBinding  // Generated ViewBinding class for page_item_pdf.xml
import com.cherry.lib.doc.util.ViewUtils.hide
import com.cherry.lib.doc.util.ViewUtils.show

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PdfPageViewAdapter
 * Author: Victor
 * Date: 2023/09/28 11:17
 * Description:
 * -----------------------------------------------------------------
 */

internal class PdfPageViewAdapter(
    private val renderer: PdfRendererCore?,
    private val pageSpacing: Rect,
    private val enableLoadingForPages: Boolean
) :
    RecyclerView.Adapter<PdfPageViewAdapter.PdfPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        // Use ViewBinding to inflate the layout instead of LayoutInflater
        val binding = PageItemPdfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfPageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return renderer?.getPageCount() ?: 0
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        holder.bindView(position)
    }

    inner class PdfPageViewHolder(private val binding: PageItemPdfBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnAttachStateChangeListener {

        fun bindView(position: Int) {
            handleLoadingForPage(position)

            // Bind any data to the views here if needed
        }

        private fun handleLoadingForPage(position: Int) {
            if (!enableLoadingForPages) {
                binding.pdfViewPageLoadingProgress.hide()
                return
            }

            if (renderer?.pageExistInCache(position) == true) {
                binding.pdfViewPageLoadingProgress.hide()
            } else {
                binding.pdfViewPageLoadingProgress.show()
            }
        }

        init {
            binding.root.addOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(p0: View) {
            handleLoadingForPage(adapterPosition)
            renderer?.renderPage(adapterPosition) { bitmap: Bitmap?, pageNo: Int ->
                if (pageNo == adapterPosition) {
                    bitmap?.let {
                        binding.pageView.setImageBitmap(bitmap)

                        // Apply fade-in animation on the image view
                        binding.pageView.animation = AlphaAnimation(0F, 1F).apply {
                            interpolator = LinearInterpolator()
                            duration = 200
                        }

                        // Hide the loading progress bar after rendering
                        binding.pdfViewPageLoadingProgress.hide()
                    }
                }
            }
        }

        override fun onViewDetachedFromWindow(p0: View) {
            binding.pageView.setImageBitmap(null)
            binding.pageView.clearAnimation()
        }
    }
}
