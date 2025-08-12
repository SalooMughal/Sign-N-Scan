# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#sing signiture progard code below

-keep class androidx.appcompat.widget.** { *; }

-keep class javax.xml.crypto.dsig.** { *; }
 -dontwarn javax.xml.crypto.dsig.**
 -keep class javax.xml.crypto.** { *; }
 -dontwarn javax.xml.crypto.**

 -keep class org.spongycastle.** { *; }
 -dontwarn org.spongycastle.**

 -keep class com.itextpdf.** { *; }
 -dontwarn com.itextpdf.**

-dontwarn org.slf4j.impl.StaticLoggerBinder


# Keep the SignatureUtils class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Activity.SignatureUtils {
    public *;
}

# Keep the SignatureView class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Activity.SignatureView {
    public *;
}

# Keep the PDSSignatureUtils class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.utils.PDSSignatureUtils {
    public *;
}

# Keep your adapter class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Adapter.SignatureRecycleViewAdapter {
    public *;
}

# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Activity.SignatureActivity {
    public *;
}

# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.utils.ViewUtils {
    public *;
}

# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.utils.RecyclerViewEmptySupport {
    public *;
}
# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSElementViewer {
    public *;
}

# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSFragment {
    public *;
}

# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSPageViewer {
    public *;
}
# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSRenderPageAsyncTask {
    public *;
}

# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSSaveAsPDFAsyncTask {
    public *;
}

# Keep SignatureActivity class and its methods
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSViewPager {
    public *;
}

# Keep the view IDs and resources
-keepclassmembers class * {
    @android.view.ViewId *;
}

# Keep all R class fields (resources)
-keep class **.R$* {
    public static final int *;
}

# Optionally, keep all classes in your package (if necessary)
-keep class com.pixelz360.docsign.imagetopdf.creator.** { *; }

# Keep all nested classes and their methods
-keepclassmembers class * {
    *;
}

-keepclassmembers class * {
    public <init>(...);
    public void set*(...);
    public android.view.View *;
}
-keep class androidx.constraintlayout.** { *; }
-keepclassmembers class androidx.recyclerview.widget.RecyclerView$Adapter {
    *;
}


# Keep all Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep all activities, fragments, and views
-keep class * extends android.app.Activity {
    public *;
}
-keep class * extends android.app.Fragment {
    public *;
}
-keep class * extends androidx.fragment.app.Fragment {
    public *;
}
-keep class * extends android.view.View {
    public *;
}

-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext

# Keep specific model classes if necessary
-keep class com.pixelz360.docsign.imagetopdf.creator.signiture_model** { *; }
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }


-dontwarn org.apache.poi.poifs.nio.CleanerUtil
-dontwarn org.apache.logging.log4j.util.ServiceLoaderUtil
