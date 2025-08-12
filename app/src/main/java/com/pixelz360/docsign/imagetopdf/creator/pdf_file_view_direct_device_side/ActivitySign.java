package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pixelz360.docsign.imagetopdf.creator.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ActivitySign extends AppCompatActivity {
    private static final int FREEHAND_Request_CODE = 43;
    private UtilEmptyRViewSign mRecyclerView;
    List<File> items = null;
    String message;
    private AdapterSignRViewSign mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature12);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.create_signature);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityFHand.class);
                startActivityForResult(intent, FREEHAND_Request_CODE);
            }
        });

        InitRecycleViewer();

        Intent intent = getIntent();
        message = intent.getStringExtra("ActivityAction");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void InitRecycleViewer() {
        Log.e("checkFlow","aaa");
        mRecyclerView = findViewById(R.id.mainRecycleView1);
        mRecyclerView.setEmptyView(findViewById(R.id.toDoEmptyView1));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        Log.e("checkFlow","bbbb");

        CreateDataSource();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == FREEHAND_Request_CODE && resultCode == Activity.RESULT_OK) {
            if (result != null) {
                CreateDataSource();
                mAdapter.notifyItemInserted(items.size() - 1);
            }
        }
    }

    private void CreateDataSource() {

        items = new ArrayList<>();

        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF Converter";
        File myDir = new File(fullPath + "/FreeHand");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File[] files = myDir.listFiles();

        if (files != null) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    long result = file2.lastModified() - file1.lastModified();
                    if (result < 0) {
                        return -1;
                    } else if (result > 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            items.addAll(Arrays.asList(files));
        }



//        if(items.size()>0){
//            findViewById(R.id.toDoEmptyView1).setVisibility(View.GONE);
//        }
        //set data and list adapter
        mAdapter = new AdapterSignRViewSign(items);
        mAdapter.setOnItemClickListener(new AdapterSignRViewSign.OnItemClickListener() {
            @Override
            public void onItemClick(View view, File value, int position) {
                if (message == null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("FileName", value.getPath());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
            @Override
            public void onDeleteItemClick(View view, final File obj, final int pos) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySign.this);
                builder.setMessage(getResources().getText(R.string.delete_sign_detail))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getText(R.string.delete), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (obj.exists()) {
                                    obj.delete();
                                }
                                CreateDataSource();
                                mAdapter.notifyItemInserted(items.size() - 1);
                            }
                        })
                        .setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();

            }

        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(mAdapter);
    }
}
