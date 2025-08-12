package com.pixelz360.docsign.imagetopdf.creator.language

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.util.Locale

object AccountsOrGuesHelper {
    private var mGoogleSignInClient: GoogleSignInClient? = null

    var isAcountOrNot = "Guest"

    @JvmStatic
    fun checkAccountOrNot(context: Context): String {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)


        // Check if user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(context)

        if (account!=null){
            isAcountOrNot = account.email.toString()
        }else{
            isAcountOrNot = "Guest"
        }


        return isAcountOrNot
    }


}
