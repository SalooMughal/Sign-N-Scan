package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.pixelz360.docsign.imagetopdf.creator.R;

import java.util.ArrayList;

public class ActivityFHand extends AppCompatActivity {
    private boolean isFreeHandCreated = false;
    private ViewSign viewSign;
    private SeekBar inkWidth;
    private Menu menu = null;
    MenuItem saveItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_hand112);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        viewSign = findViewById(R.id.inkSignatureOverlayView11);
        inkWidth = findViewById(R.id.seekBar);
        inkWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewSign.setStrokeWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.action_clear).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ActivityFHand.this.clearSignature();
                ActivityFHand.this.enableClear(false);
                ActivityFHand.this.enableSave(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.freehandmenu, menu);
        this.menu = menu;
        saveItem = menu.findItem(R.id.signature_save);
        saveItem.setEnabled(false);
        saveItem.getIcon().setAlpha(130);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signature_save) {
            saveFreeHand();
            Intent data = new Intent();
            String text = "Result OK";
            data.setAction(text);
            setResult(RESULT_OK, data);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        int id = view.getId();
        if (id == R.id.radioBlack) {
            if (checked) {
                viewSign.setStrokeColor(ContextCompat.getColor(ActivityFHand.this, R.color.inkblack));
            }
        } else if (id == R.id.radioRed) {
            if (checked)
                viewSign.setStrokeColor(ContextCompat.getColor(ActivityFHand.this, R.color.inkred));
        } else if (id == R.id.radioBlue) {
            if (checked)
                viewSign.setStrokeColor(ContextCompat.getColor(ActivityFHand.this, R.color.inkblue));
        } else if (id == R.id.radiogreen) {
            if (checked)
                viewSign.setStrokeColor(ContextCompat.getColor(ActivityFHand.this, R.color.inkgreen));
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);

    }

    public void clearSignature() {
        viewSign.clear();
        viewSign.setEditable(true);
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
            saveItem.getIcon().setAlpha(255);
        } else {
            saveItem.getIcon().setAlpha(130);
        }
        saveItem.setEnabled(z);
    }

    public void saveFreeHand() {
        ViewSign localViewSign = findViewById(R.id.inkSignatureOverlayView11);
        ArrayList localArrayList = localViewSign.mInkList;
        if ((localArrayList != null) && (localArrayList.size() > 0)) {
            isFreeHandCreated = true;
        }
        UtilsSign.saveSignature(getApplicationContext(), localViewSign);
    }
}
