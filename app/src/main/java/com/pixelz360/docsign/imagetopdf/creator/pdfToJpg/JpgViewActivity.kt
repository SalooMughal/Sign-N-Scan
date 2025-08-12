package com.pixelz360.docsign.imagetopdf.creator.pdfToJpg

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityJpgViewBinding
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityTtfSvgViewBinding

class JpgViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityJpgViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJpgViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var jpgfilePath = intent.getStringExtra("jpgfilePath")

        val bitmap = BitmapFactory.decodeFile(jpgfilePath)

        Glide.with(this@JpgViewActivity)
            .load(bitmap)
            .placeholder(R.drawable.ic_pdf_black)
            .into(binding.imageIv)
    }
}