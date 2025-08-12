package com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code

import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityGenerateCodeBinding


class GenerateCodeActivity : AppCompatActivity() {

    private lateinit var editTextInput: EditText
    private lateinit var buttonGenerate: TextView
    private lateinit var imageViewQRCode: ImageView
    private lateinit var clearTextIcon: ImageView
    private lateinit var buttonShare: LinearLayout
    private var bmp: Bitmap? = null

    lateinit var binding: ActivityGenerateCodeBinding

    var isbuttonShareSelected = false

    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeoutRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@GenerateCodeActivity, R.color.white)
        // Change status bar color
        com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils.changeStatusBarColor(statusBarColor, this@GenerateCodeActivity)





        // Initialize Views
        editTextInput = findViewById(R.id.editTextInput)
        buttonGenerate = findViewById(R.id.buttonGenerate)
        imageViewQRCode = findViewById(R.id.imageViewQRCode)
        buttonShare = findViewById(R.id.buttonShare)
         clearTextIcon  = findViewById(R.id.clearTextIcon)

        clearTextIcon.setOnClickListener { editTextInput.setText("") }

        firebaseAnalytics(this@GenerateCodeActivity,"GenerateCodeActivity")




        editTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // you can call or do what you want with your EditText here

                // yourEditText...
                Log.d("checkbtn","afterTextChanged "+s.toString())

                if (s.toString().isEmpty()){
                    editTextInput.background = resources.getDrawable(R.drawable.qr_code_editext_un_selected)

                }else{
                    editTextInput.background = resources.getDrawable(R.drawable.qr_code_editext_selected)

                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                Log.d("checkbtn","beforeTextChanged "+s.toString())
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                Log.d("checkbtn","onTextChanged "+s.toString())

            }
        })



        // Set Generate Button Click Listener
        buttonGenerate.setOnClickListener {


            if (buttonShare.visibility == VISIBLE) {
                Log.d("checkbtn","share button is visible")

                binding.editTextLayout.visibility = VISIBLE
                buttonShare.visibility = View.GONE
                imageViewQRCode.visibility = View.GONE

            }else{
                Log.d("checkbtn","share button is not visible")
                var content = editTextInput.text.toString()


                if (content.isNotBlank()) {



                    bmp = generateQrCode(content)
                    bmp?.let {
                        imageViewQRCode.visibility = android.view.View.VISIBLE
                        imageViewQRCode.setImageBitmap(it)

                        imageViewQRCode.visibility = android.view.View.VISIBLE
                        buttonShare.visibility = android.view.View.VISIBLE
                        binding.editTextLayout.visibility = android.view.View.GONE
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                    }
                }

                editTextInput.getText()?.clear();

            }



        }

        binding.backButton.setOnClickListener {

//            val intent = Intent(this@GenerateCodeActivity,MainActivity::class.java)
            val intent = Intent(this@GenerateCodeActivity,HomeActivity::class.java)
            intent.putExtra("ScannerSide","ScannerSide")
            startActivity(intent)
            finish()

        }

        // Set Share Button Click Listener
        buttonShare.setOnClickListener {
            bmp?.let { bitmap ->
                // Share the QR code (saveImage and shareImageUri would be similar to your existing code)
                val imageUri = saveImage(applicationContext, bitmap)
                imageUri?.let { uri -> shareImageUri(applicationContext, uri) }
            }
        }
    }






    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }


    fun goToNextActivity() {



        if (buttonShare.visibility == VISIBLE) {
            Log.d("checkbtn","share button is visible")

//            binding.editTextLayout.visibility = VISIBLE
//            buttonShare.visibility = View.GONE
//            imageViewQRCode.visibility = View.GONE

        }else{
            Log.d("checkbtn","share button is not visible")
            var content = editTextInput.text.toString()


            if (content.isNotBlank()) {
//                bmp = generateQrCode(content)
//                bmp?.let {
//                    imageViewQRCode.visibility = android.view.View.VISIBLE
//                    imageViewQRCode.setImageBitmap(it)
//
//                    imageViewQRCode.visibility = android.view.View.VISIBLE
//                    buttonShare.visibility = android.view.View.VISIBLE
//                    binding.editTextLayout.visibility = android.view.View.GONE
//                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
//
//                }
            }

            editTextInput.getText()?.clear();

        }

    }


    // Generate QR Code
    private fun generateQrCode(content: String): Bitmap? {
        val writer = QRCodeWriter()
        return try {
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                    )
                }
            }
            bmp
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}
