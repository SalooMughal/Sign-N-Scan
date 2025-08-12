package com.pixelz360.docsign.imagetopdf.creator.googleSignInClasses;


import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.pixelz360.docsign.imagetopdf.creator.R;
public class ProfileActivity extends AppCompatActivity {
    // Initialize variables
    private ImageView ivImage;
    private TextView tvName;
    private TextView tv_email;
    private Button btLogout;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Assign variables
        ivImage = findViewById(R.id.iv_image);
        tvName = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        btLogout = findViewById(R.id.bt_logout);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


            // When Firebase user is not null, set image and name
            Glide.with(this).load(account.getPhotoUrl()).into(ivImage);
            tvName.setText(account.getDisplayName());
        tv_email.setText(account.getEmail());


        // Initialize Credential Manager
//        credentialManager = CredentialManager.create(this);

        btLogout.setOnClickListener(view -> {
            mGoogleSignInClient.signOut();
            // Display Toast
            Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
            // Finish activity
            finish();

//            // Sign out using Credential Manager
//            SignOutRequest signOutRequest = new SignOutRequest.Builder().build();
//            credentialManager.signOut(signOutRequest)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                // Sign out from Firebase
//                                firebaseAuth.signOut();
//                                // Display Toast
//                                Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
//                                // Finish activity
//                                finish();
//                            } else {
//                                // Handle failure
//                                Toast.makeText(getApplicationContext(), "Logout failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
        });
    }
}
