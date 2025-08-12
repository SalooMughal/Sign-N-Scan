package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pixelz360.docsign.imagetopdf.creator.FirstActivity
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityAddSuccessfullyBinding

class AddSuccessfullyActivity : AppCompatActivity() {


    lateinit var binding: ActivityAddSuccessfullyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSuccessfullyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red) // here is your color


        binding.startedBtn.setOnClickListener {
//            val intent = Intent(this@AddSuccessfullyActivity, MainActivity::class.java)
            val intent = Intent(this@AddSuccessfullyActivity, HomeActivity::class.java)
            intent.putExtra("ScannerSide","SubscribtionSide")
            startActivity(intent)
            finish()
        }

    }
}