package com.pixelz360.docsign.imagetopdf.creator.googleSignInClasses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.pixelz360.docsign.imagetopdf.creator.FirstActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityGoogleSignInBinding;

public class GoogleSignInActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;

    ActivityGoogleSignInBinding binding;

    Long pressedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleSignInBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(binding.getRoot());


        // Change the status bar color for this activity
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // Ensure system bar backgrounds can be modified
        window.setStatusBarColor(getResources().getColor(R.color.white)); // Set your custom color

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // User is already signed in
            navigateToNextActivity();
            Log.w("GoogleSignIn123", "User is already signed in");
        }

        findViewById(R.id.bt_sign_in).setOnClickListener(v -> signIn());


        binding.guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleSignInActivity.this, FirstActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });



        // Call the method to get the advertising ID
        new GetAdvertisingIdTask().execute();

    }

    private class GetAdvertisingIdTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                String advertisingId = adInfo.getId();
                boolean isLimitAdTrackingEnabled = adInfo.isLimitAdTrackingEnabled();

                // You can log the Advertising ID or use it
                Log.d("AdvertisingID", "ID: " + advertisingId + " Limit Tracking: " + isLimitAdTrackingEnabled);
                return advertisingId;
            } catch (Exception e) {
                Log.e("AdvertisingID", "Error getting Advertising ID", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String advertisingId) {
            if (advertisingId != null) {
                // Use the Advertising ID (e.g., send it to your server)
            }
        }
    }

    private void navigateToNextActivity() {
//        Intent intent = new Intent(this, ProfileActivity.class);
        Intent intent = new Intent(this, FirstActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // Login successful, navigate to next activity
                    navigateToNextActivity();
                }
            } catch (ApiException e) {
                Log.w("GoogleSignIn123", "Sign-in failed", e);
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(GoogleSignInActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();


    }
}