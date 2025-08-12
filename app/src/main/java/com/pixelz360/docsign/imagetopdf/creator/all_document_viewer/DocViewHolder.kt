package com.cherry.doc

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.cherry.doc.data.DocGroupInfo
import com.pixelz360.docsign.imagetopdf.creator.databinding.RvDocCellBinding

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DocViewHolder
 * Author: Victor
 * Date: 2023/10/26 10:57
 * Description: 
 * -----------------------------------------------------------------
 */

class DocViewHolder : RecyclerView.ViewHolder,OnClickListener {
    var mOnItemClickListener: OnItemClickListener? = null
    lateinit var binding: RvDocCellBinding
    lateinit var context: Context
    constructor(binding: RvDocCellBinding, context: Context) : super(binding.root) {
        binding.mainitem.setOnClickListener(this)
        this.binding = binding
        this.context = context
    }

    fun bindData(data: DocGroupInfo?) {
        binding.mTvTypeName.text = data?.typeName

//        binding.mRvDocCell.onFlingListener = null
//        LinearSnapHelper().attachToRecyclerView(binding.mRvDocCell)

        var cellAdapter = DocCellAdapter(context,mOnItemClickListener,
            adapterPosition)
        cellAdapter.showDatas(data?.docList)

        binding.mRvDocCell.adapter = cellAdapter
    }

    override fun onClick(v: View?) {
        mOnItemClickListener?.onItemClick(null,v,adapterPosition,0)
    }

}