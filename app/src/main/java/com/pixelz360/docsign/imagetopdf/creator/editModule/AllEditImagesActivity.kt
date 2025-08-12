package com.pixelz360.docsign.imagetopdf.creator.editModule

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.firebaseAnalytics
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityAllEditImagesBinding
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class AllEditImagesActivity : AppCompatActivity() {

    lateinit var binding: ActivityAllEditImagesBinding
//    private lateinit var uris: List<Uri>
    private lateinit var renameFileName: String
    private lateinit var modulename: String

    private var singleItemsSelected = false // To track the selection state


    private var allItemsSelected = false // To track the selection state
    private val CAMERA_PERMISSION_CODE = 101


    private var isFileValieOrNo = false


    private val CAMERA_REQUEST_CODE = 100
    private var cameraImageUri: Uri? = null
    private lateinit var adapter: AllEditImagesAdapter
    private var uris: MutableList<Uri> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityAllEditImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@AllEditImagesActivity, R.color.white)
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor, this@AllEditImagesActivity)

        uris = intent.getParcelableArrayListExtra<Uri>("images")?.toMutableList() ?: mutableListOf()


//        uris = intent.getParcelableArrayListExtra("images") ?: emptyList()
        renameFileName = intent.getStringExtra("renameFileName")!!
        modulename = intent.getStringExtra("modulename")!!


        // In your Activity or Fragment
//        val space = resources.getDimensionPixelSize(R.dimen.grid_item_margin)
//        binding.rv.addItemDecoration(GridSpacingItemDecoration(3, space, true))
//

        Log.d("checkimage", " 2   " + renameFileName)
        Log.d("checkimage", " AllEditImagesActivity   " + modulename)
        binding.rv.layoutManager = GridLayoutManager(this, 3)
        binding.rv.setHasFixedSize(true)

         adapter = AllEditImagesAdapter(uris, this, binding.deleteBtn, binding.selectAllBtn, binding.toolbarTitle, binding.backButton, binding.clearButton,binding.rv)
        binding.rv.adapter = adapter


        val callback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.onItemMove(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No swipe action needed
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.7f
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rv)
        adapter.setItemTouchHelper(itemTouchHelper)







        binding.saveBtntv.setOnClickListener {

            handleImageSelection(uris)

//            val sharedPreferences = getSharedPreferences("image_change_done_prefs", MODE_PRIVATE)
//            val freeUsageCount = sharedPreferences.getInt("free_usage_count", 0)
//
//            if (freeUsageCount < 1) {
//                // Free unlimited selection for the first 3 times
//                sharedPreferences.edit().putInt("free_usage_count", freeUsageCount + 1).apply()
//                handleImageSelection(uris)
//                Log.d("RewardedAd", "reeUsageCount < 3")
//            } else if (uris.size <= 15) {
//                // Free user selection (limit 15 images)
//                handleImageSelection(uris)
//                Log.d("RewardedAd", "<= 15")
//
//            } else {
//                // Show Rewarded Ad for unlimited selection
//
//
//            }




        }

        binding.backButton.setOnClickListener {
            discardDailog()
        }

        binding.shakeButton.setOnClickListener {
            for (i in 0 until binding.rv.childCount) {
                val viewHolder = binding.rv.findViewHolderForAdapterPosition(i)
                viewHolder?.itemView?.let {
                    val shake = TranslateAnimation(0f, 10f, 0f, 0f)
                    shake.duration = 500
                    shake.interpolator = CycleInterpolator(7f)
                    it.startAnimation(shake)
                }
            }
        }

//        val callback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {
//
//            override fun onMove(@NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, @NonNull target: RecyclerView.ViewHolder ): Boolean {
//                val fromPosition = viewHolder.adapterPosition
//                val toPosition = target.adapterPosition
//                Collections.swap(uris+1, fromPosition, toPosition)
//                recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
//                recyclerView.adapter!!.notifyItemChanged(fromPosition)
//                recyclerView.adapter!!.notifyItemChanged(toPosition)
//
//                Log.d("checkimage", "onMove")
//
//                return true
//            }
//
//            override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                // No swipe action
//                Log.d("checkimage", "onSwiped")
//            }
//
//            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//                super.onSelectedChanged(viewHolder, actionState)
//                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//                    viewHolder!!.itemView.alpha = 0.7f
//
//                    Log.d("checkimage", "onSelectedChanged")
//                }
//            }
//
//            override fun clearView(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder
//            ) {
//                super.clearView(recyclerView, viewHolder)
//                viewHolder.itemView.alpha = 1.0f
//            }
//        }
//
//        val itemTouchHelper = ItemTouchHelper(callback)
//        itemTouchHelper.attachToRecyclerView(binding.rv)
//        adapter.setItemTouchHelper(itemTouchHelper)



        // Log a predefined event
        val analytics = FirebaseAnalytics.getInstance(this)

        val bundle = Bundle()
        bundle.putString("activity_name", "AllEditImagesActivity")
        analytics.logEvent("activity_created", bundle)

// Using predefined Firebase Analytics events

// Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "AllEditImagesActivity")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


        firebaseAnalytics(this@AllEditImagesActivity,"AllEditImagesActivity")





        if (uris.isEmpty()){
            //
            isFileValieOrNo = false
        }else{
            //
            isFileValieOrNo = true
        }




        binding.selectAllBtn.setOnClickListener(View.OnClickListener {
            if (!isFileValieOrNo) {
                Log.d("checkfileyet", "not File Avalibele 1")
                Toast.makeText(this@AllEditImagesActivity, "No Files to Select", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("checkfileyet", "File Avalibele 1")
                if (singleItemsSelected) {
                    adapter.unselectAllItems()
                    singleItemsSelected = false
                    allItemsSelected = false
                    binding.selectAllBtn.setText("Select")
                    Toast.makeText(this@AllEditImagesActivity, "No File to Select", Toast.LENGTH_SHORT).show()
                    binding.selectAllBtn.setBackground(resources.getDrawable(R.drawable.btn_un_select_bg))
                    binding.selectAllBtn.setTextColor(ContextCompat.getColor(this@AllEditImagesActivity, R.color.select_text_un_selected))


                    Log.d("checkfileyet111", "if singleItemsSelected  "+singleItemsSelected)

                } else {

                    if (binding.selectAllBtn.text.equals("Cancel")){
                        Log.d("checkfileyet111", "click Deselect All")

                        adapter.unselectAllItems()
                        binding.selectAllBtn.setBackground(resources.getDrawable(R.drawable.btn_un_select_bg))
                        binding.selectAllBtn.setTextColor(ContextCompat.getColor(this@AllEditImagesActivity, R.color.select_text_un_selected))

                        singleItemsSelected = false // To track the selection state

                        allItemsSelected = false // To track the selection state


                        binding.selectAllBtn.setText("Select")


                    }else{
                        adapter.selectAllItems("Cancel")

                        Log.d("checkfileyet111", "else singleItemsSelected  "+singleItemsSelected)


                        binding.selectAllBtn.setText("Cancel")
                        Toast.makeText(this@AllEditImagesActivity, "Select All", Toast.LENGTH_SHORT).show()
                        binding.selectAllBtn.setBackground(resources.getDrawable(R.drawable.btn_select_bg))
                        binding.selectAllBtn.setTextColor(ContextCompat.getColor(this@AllEditImagesActivity, R.color.white))

                        allItemsSelected = false

//                        if (!allItemsSelected) {
//
//                            Log.d("checkfileyet111", "if allItemsSelected  "+allItemsSelected)
//
//
//                            allItemsSelected = true
//
//
//
//
//
//                        } else {
//                            adapter.selectAllItems("Select All")
//                            allItemsSelected = false
//                            binding.selectAllBtn.setText("Deselect All")
//
//
//                            Log.d("checkfileyet111", "else allItemsSelected  "+allItemsSelected)
//
//
//                        }
                    }



                }
            }
        })


//        selectAllBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (allItemsSelected) {
//                    unselectAllItems();
//                    allItemsSelected = false;
////                    selectAllButton.setText("Select All");
//                    Toast.makeText(context,"No File to Select",Toast.LENGTH_SHORT).show();
//
//                    selectAllBtn.setBackground(context.getResources().getDrawable(R.drawable.btn_un_select_bg));
//                    selectAllBtn.setTextColor(ContextCompat.getColor(context, R.color.select_text_un_selected));
//
//                } else {
//                    selectAllItems();
//                    allItemsSelected = true;
////                    selectAllButton.setText("No File to Select");
//                    Toast.makeText(context,"Select All",Toast.LENGTH_SHORT).show();
//
//                    selectAllBtn.setBackground(context.getResources().getDrawable(R.drawable.btn_select_bg));
//                    selectAllBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
//
//                }
//
//
//            }
//        });
            binding.clearButton.setOnClickListener(View.OnClickListener {
            adapter.unselectAllItems()
                binding.selectAllBtn.setBackground(resources.getDrawable(R.drawable.btn_un_select_bg))
                binding.selectAllBtn.setTextColor(ContextCompat.getColor(this@AllEditImagesActivity, R.color.select_text_un_selected))

                singleItemsSelected = false // To track the selection state

                allItemsSelected = false // To track the selection state


                binding.selectAllBtn.setText("Select")

        })


        // Set camera click listener
        adapter.setCameraClickListener {
//            openCamera()

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, proceed with camera capture
                launchCamera()
            } else {
                // Request camera permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }


    }

    // Function to handle the permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                launchCamera()
            } else {
                // Permission denied, show a toast or dialog
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to actually open the camera
    private fun launchCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = createImageFile()
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            }
        }
    }

    // Function to open the camera
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            val photoFile: File = createImageFile()!!
            if (photoFile != null) {
//                cameraImageUri = FileProvider.getUriForFile(this, "com.pixelz360.fileprovider", photoFile)
                cameraImageUri = FileProvider.getUriForFile(this@AllEditImagesActivity, getPackageName() + ".provider", photoFile)

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            }
        }
    }


    // Handle the result of the captured image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            cameraImageUri?.let {
                uris.add(it)
                adapter.notifyItemInserted(uris.size+1)
            }
        }
    }

    // Create a file to store the captured image
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            File.createTempFile("IMG_$timeStamp", ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }




    private fun discardDailog() {

        val dialogRename = Dialog(this@AllEditImagesActivity, R.style.renameDialogStyle)
        dialogRename.setContentView(R.layout.discard_dailog_images_list)

        val window = dialogRename.window
        if (window != null) {
            window.setGravity(Gravity.CENTER)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val metrics = Resources.getSystem().displayMetrics
            val screenWidth = metrics.widthPixels
            val desiredWidth = screenWidth - 2 * dpToPx(this@AllEditImagesActivity, 30)
            val params = dialogRename.window!!.attributes
            params.width = desiredWidth
            window.attributes = params
        }


        val disCardBtn = dialogRename.findViewById<TextView>(R.id.disCardBtn)
        val continueBtn = dialogRename.findViewById<TextView>(R.id.continueBtn)



        disCardBtn.setOnClickListener {
            dialogRename.dismiss()
            val intent =  Intent(this@AllEditImagesActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        continueBtn.setOnClickListener {

            dialogRename.dismiss()



        }

        dialogRename.show()

    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return Math.round(dp * context.resources.displayMetrics.density)
    }



    private fun handleImageSelection(uris: List<Uri>) {


        val intent = Intent(this, ConvertToPdfActivity::class.java)
        intent.putParcelableArrayListExtra("images", ArrayList(uris))
        intent.putExtra("renameFileName", renameFileName)
        intent.putExtra("modulename", modulename)
        startActivity(intent)
//        finish()


    }

    override fun onBackPressed() {
        discardDailog()
    }
}
