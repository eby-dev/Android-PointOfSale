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

-keep public class com.ahmadabuhasan.pointofsale.**
# https://github.com/airbnb/lottie-android/blob/master/sample/proguard-multidex-rules.pro
-keep class com.airbnb.lottie.samples.** { *; }
# https://github.com/androidmads/SQLite2XL
-dontwarn org.apache.poi.**
# https://github.com/balysv/material-ripple
# https://github.com/barteksc/AndroidPdfViewer
-keep class com.shockwave.**
# https://github.com/PhilJay/MPAndroidChart
# https://github.com/sd6352051/NiftyDialogEffects
# iText
#-keep class com.itextpdf.text.** { *; }
-dontwarn com.itextpdf.text.**
# https://github.com/Karumi/Dexter/blob/master/dexter/proguard-rules.pro
#Preserve all Dexter classes and method names
-keepattributes InnerClasses, Signature, *Annotation*
-keep class com.karumi.dexter.** { *; }
-keep interface com.karumi.dexter.** { *; }
-keepclasseswithmembernames class com.karumi.dexter.** { *; }
-keepclasseswithmembernames interface com.karumi.dexter.** { *; }
# https://github.com/hedzr/android-file-chooser
# https://github.com/jgilfelt/android-sqlite-asset-helper
# https://github.com/barteksc/PdfiumAndroid
# https://github.com/premkumarroyal/MonthAndYearPicker
# https://github.com/GrenderG/Toasty
# https://github.com/maayyaannkk/ImagePicker
# https://github.com/dm77/barcodescanner
# https://github.com/centic9/poi-on-android
#Optimize
-optimizations !field/*,!class/merging/*,*
#-mergeinterfacesaggressively
# will keep line numbers and file name obfuscation
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
# Apache POI
-dontwarn org.apache.**
-dontwarn org.openxmlformats.schemas.**
-dontwarn org.etsi.**
-dontwarn org.w3.**
-dontwarn com.microsoft.schemas.**
-dontwarn com.graphbuilder.**
-dontwarn javax.naming.**
-dontwarn java.lang.management.**
-dontwarn org.slf4j.impl.**
-dontnote org.apache.**
-dontnote org.openxmlformats.schemas.**
-dontnote org.etsi.**
-dontnote org.w3.**
-dontnote com.microsoft.schemas.**
-dontnote com.graphbuilder.**
-dontnote javax.naming.**
-dontnote java.lang.management.**
-dontnote org.slf4j.impl.**
-keeppackagenames org.apache.poi.ss.formula.function
# https://bumptech.github.io/glide/doc/download-setup.html#proguard
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# If you're targeting any API level less than Android API 27, also include:
# ```pro
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

-keep class com.google.firebase.provider.FirebaseInitProvider
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModule