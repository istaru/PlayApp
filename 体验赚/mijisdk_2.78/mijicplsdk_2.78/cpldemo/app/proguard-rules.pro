# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\prog\android-sdk-windows_r07/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#mituo plat start
-keep class mituo.plat.** {*;}
-dontwarn mituo.plat.**

#picasso start
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
#picasso end

#okhttp start
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#okhttp end

##tencentloc start
-keepclassmembers class ** {
    public void on*Event(...);
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}
-keep class com.tencent.tencentmap.lbssdk.service.**{*;}

-dontwarn org.eclipse.jdt.annotation.**
-dontwarn c.t.**
##tencentloc end

#mituo plat end




