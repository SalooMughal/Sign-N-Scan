package com.pixelz360.docsign.imagetopdf.creator

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyPolicyActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        val privacyPolicyContent: TextView = findViewById(R.id.privacyPolicyContent)
        privacyPolicyContent.text = Html.fromHtml(getPrivacyPolicyText())
        privacyPolicyContent.movementMethod = LinkMovementMethod.getInstance()

       val backButton:ImageView  = findViewById(R.id.backButton)

        backButton.setOnClickListener {
//            val  intent = Intent(this@PrivacyPolicyActivity,MainActivity::class.java)
//            val  intent = Intent(this@PrivacyPolicyActivity,HomeActivity::class.java)
//            startActivity(intent)

            onBackPressed()
        }

        // Log a predefined event





        // Log a predefined event


        // Log a predefined event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("activity_name", "PrivacyPolicyActivity")
        analytics.logEvent("activity_created", bundle)
// Using predefined Firebase Analytics events
        // Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "PrivacyPolicyActivity")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

    }

    private fun getPrivacyPolicyText(): String {
        return """
                        <h3><strong>SignNScan PDF: PDF Toolkit</strong></h3>
                   <p><strong>Last Updated:</strong> [March 2025]</p>
            <p>Welcome to <strong>SignNScan PDF: PDF Toolkit</strong>. We are committed to protecting your privacy and ensuring transparency in how we collect, use, and share information. This Privacy Policy explains our data practices when you use our mobile application (the "App") and outlines your rights.</p>
            <p>By using our App, you agree to this Privacy Policy. If you have any questions, please contact us at <a href=\"mailto:support@pixelz360.com.au\"><strong style="color: #0000F5;">support@pixelz360.com.au</strong></p>
            <hr>
            <h3>1. <strong>Information Collection & Use</strong></h3>
            <p>We collect certain types of information to provide and improve our services.</p>
            <h5><strong>Personal Data</strong></h5>
            <p>We do not require account registration, but you may voluntarily provide:</p>
            <ul>
                <li> <strong>Email address</strong> (for support, feedback, or inquiries)</li>
                <li> <strong>Location data</strong> (if enabled, to improve certain services)</li>
            </ul>
            <p>This information is used for customer support, troubleshooting, and service improvements.</p>
            <h3><strong>Usage Data & Analytics</strong></h3>
            <p>We automatically collect:</p>
            <ul>
                <li> <strong>Device information</strong> (device type, operating system)</li>
                <li> <strong>App usage data</strong> (feature interactions, session duration)</li>
            </ul>
            <p>This helps us <strong>analyze trends, enhance performance, and improve user experience.</strong></p>
            <hr>
            <h3>2. <strong>App Permissions</strong></h3>
            <p>For the App to function properly, we may request access to:</p>
            <ul>
                <li> <strong>Storage</strong> – To save and access PDFs</li>
                <li> <strong>Camera</strong> – To scan documents</li>
                <li> <strong>Downloads</strong> – For file management</li>
            </ul>
            <p>You can <strong>deny or revoke permissions</strong> in your device settings, but this may affect certain features.</p>
            <hr>
            <h3>3. <strong>Subscription & Payment Handling</strong></h3>
            <h5>Subscription Payments:</h5>
            <ul>
                <li> Our App offers <strong>subscription-based services</strong> via <strong>Google Play</strong>.</li>
                <li> <strong>No free trial</strong> is provided.</li>
                <li> Subscriptions <strong>automatically renew</strong> unless canceled at least <strong>24 hours</strong> before the next billing cycle.</li>
                <li> You can <strong>manage or cancel</strong> your subscription in <strong>Google Play Subscriptions</strong>.</li>
            </ul>
            <p>We do not <strong>process or store</strong> payment details—Google Play handles all transactions securely.</p>
            <hr>
            <h3>4. <strong>Rewarded Ads & Third-Party Services</strong></h3>
            <h5>Rewarded Ads</h5>
            <p>Our App allows users to <strong>watch ads to unlock premium features temporarily</strong>.</p>
            <ul>
                <li> Ads are provided by <strong>third-party ad networks</strong> (e.g., Google AdMob).</li>
                <li> Ad providers may collect data (device ID, ad interactions) to personalize ads.</li>
            </ul>
            <h3>Analytics & Advertising Partners</h3>
            <p>We use <strong>third-party services</strong> to improve app functionality and monetization:</p>
            <ul>
                <li> <strong>Google AdMob</strong> – For ad-based revenue</li>
                <li> <strong>Firebase Analytics</strong> – To track app performance</li>
                <li> <strong>Crashlytics</strong> – For app stability monitoring</li>
            </ul>
            <hr>
            <h3>5. <strong>Data Retention & Security</strong></h3>
            <ul>
                <li> We do <strong>not store personal data</strong> unless voluntarily provided.</li>
                <li> Any contact details (e.g., email) are <strong>deleted after resolving inquiries</strong>.</li>
                <li> App data is <strong>stored locally</strong> on your device.</li>
                <li> You can <strong>clear your data</strong> anytime via <strong>App Settings</strong>.</li>
            </ul>
            <hr>
            <h3>6. <strong>User Rights & Data Control</strong></h3>
            <ul>
                <li> <strong>Opt-out</strong> of personalized ads via <strong>Google Ads Settings</strong>.</li>
                <li> <strong>Revoke permissions</strong> anytime in <strong>device settings</strong>.</li>
                <li> <strong>Request data deletion</strong> by contacting <a href=\"mailto:support@pixelz360.com.au\">support@pixelz360.com.au</a>.</li>
            </ul>
            <hr>
            <h3>7. <strong>Children’s Privacy</strong></h3>
            <p>Our App is <strong>not intended for children under 13</strong>. If you believe a child has provided data, contact us, and we will <strong>remove it immediately</strong>.</p>
            <hr>
            <h3>8. <strong>Changes to This Privacy Policy</strong></h3>
            <p>We may update this policy periodically. Changes will be <strong>posted on this page</strong>, and we encourage users to <strong>review it regularly</strong>.</p>
            <hr>
            <p>📩 <strong>Contact Us:</strong><br>For any questions, email us at <a href=\"mailto:support@pixelz360.com.au\">support@pixelz360.com.au</a>.</p>
        """.trimIndent()
    }
}
