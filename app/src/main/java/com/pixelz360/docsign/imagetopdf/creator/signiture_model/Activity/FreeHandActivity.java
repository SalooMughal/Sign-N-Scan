package com.pixelz360.docsign.imagetopdf.creator.signiture_model.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityFreeHandBinding;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.DigitalSignatureActivity;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FreeHandActivity extends AppCompatActivity {
    private boolean isFreeHandCreated = false;
    private SignatureView signatureView;
    private SeekBar inkWidth;
    private Menu menu = null;
    MenuItem saveItem;
    boolean checkClick = false;

    ActivityFreeHandBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFreeHandBinding.inflate(getLayoutInflater());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        setContentView(binding.getRoot());
//        ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);
        signatureView = findViewById(R.id.inkSignatureOverlayView);
        inkWidth = findViewById(R.id.seekBar);
        inkWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                signatureView.setStrokeWidth(progress);

                binding.progressCountTextview.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // Get color from resources
        int statusBarColor = ContextCompat.getColor(FreeHandActivity.this, R.color.white);
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor,FreeHandActivity.this);




        // Log a predefined event
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle1 = new Bundle();
        bundle1.putString("activity_name", "FreeHandActivity");
        analytics.logEvent("activity_created", bundle1);
// Using predefined Firebase Analytics events
        bundle1.putString(FirebaseAnalytics.Param.ITEM_NAME, "FreeHandActivity");
        bundle1.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle1);

        findViewById(R.id.action_clear).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FreeHandActivity.this.clearSignature();
                FreeHandActivity.this.enableClear(false);
                FreeHandActivity.this.enableSave(false);
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList localArrayList = binding.inkSignatureOverlayView.mInkList;
                if ((localArrayList != null) && (localArrayList.size() > 0)) {
                    isFreeHandCreated = true;
                    Log.d("checksigchoose","localArrayList != null "+isFreeHandCreated);

                    Intent data = new Intent();
                    String text = "Result OK";
                    data.setAction(text);
                    setResult(RESULT_OK, data);
                    finish();

                    SignatureUtils.saveSignature(getApplicationContext(), binding.inkSignatureOverlayView);

                }else {
                    Toast.makeText(FreeHandActivity.this, "First Choose Sig", Toast.LENGTH_SHORT).show();
                    Log.d("checksigchoose","else "+localArrayList.size());
                }




//                if (isFreeHandCreated){
//                    saveFreeHand();
//                    Intent data = new Intent();
//                    String text = "Result OK";
//                    data.setAction(text);
//                    setResult(RESULT_OK, data);
//                    finish();
//
//                    Log.d("checksigchoose","if (isFreeHandCreated){ "+isFreeHandCreated);
//
//                }else {
//                    Toast.makeText(FreeHandActivity.this,"First Choose Sig",Toast.LENGTH_SHORT);
//
//                    Log.d("checksigchoose","else "+isFreeHandCreated);
//                }



            }
        });


        // List of colors
        List<Integer> colors = Arrays.asList(
                ContextCompat.getColor(FreeHandActivity.this, R.color.inkblack),
                ContextCompat.getColor(FreeHandActivity.this, R.color.light_gray),
                ContextCompat.getColor(FreeHandActivity.this, R.color.yellow_color),
                ContextCompat.getColor(FreeHandActivity.this, R.color.red),
                ContextCompat.getColor(FreeHandActivity.this, R.color.green_color),
                ContextCompat.getColor(FreeHandActivity.this, R.color.blue_color),
                ContextCompat.getColor(FreeHandActivity.this, R.color.purple), // New color 1
                ContextCompat.getColor(FreeHandActivity.this, R.color.orange), // New color 2
                ContextCompat.getColor(FreeHandActivity.this, R.color.cyan),   // New color 3
                ContextCompat.getColor(FreeHandActivity.this, R.color.teal),   // New color 4
                ContextCompat.getColor(FreeHandActivity.this, R.color.brown)   // New color 5
        );


        // Set up the adapter
        ColorAdapter adapter = new ColorAdapter(this, colors,signatureView);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);

    }

    public void clearSignature() {
        signatureView.clear();
        signatureView.setEditable(true);
    }

    public void enableClear(boolean z) {
        ImageButton button = findViewById(R.id.action_clear);
        button.setEnabled(z);
        if (z) {
            button.setAlpha(1.0f);
        } else {
            button.setAlpha(0.5f);
        }
    }

    public void enableSave(boolean z) {
        if (z) {
//            saveItem.getIcon().setAlpha(255);
        } else {
//            saveItem.getIcon().setAlpha(130);
        }
//        saveItem.setEnabled(z);
    }

    public void saveFreeHand() {

        SignatureUtils.saveSignature(getApplicationContext(), binding.inkSignatureOverlayView);
    }
}
