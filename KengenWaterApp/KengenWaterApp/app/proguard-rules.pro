# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

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
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

-keep class androidx.renderscript.** { *; }

#Mine
-dontwarn javax.**
-dontwarn java.lang.management.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn android.support.**
-dontwarn com.google.ads.**
-dontwarn org.slf4j.**
-dontwarn org.json.**
-keep class com.shockwave.**
-keepclassmembers class com.shockwave.** { *; }
# Google Play Admob
    -keep public class com.google.android.gms.ads.** {
        public *;
    }

    -keep public class com.google.ads.** {
         public *;
    }
-dontwarn com.squareup.okhttp.**
# Facebook
-keep class com.facebook.** {*;}
-dontwarn com.facebook.**

-keep class com.tapdaq.sdk.** { *; }
-keep class com.tapdaq.adapters.* { *; }
-keep class com.tapdaq.unityplugin.* { *; }
-keep class com.google.android.gms.ads.identifier.** { *; }

# Dagger 2
 -dontwarn com.google.errorprone.annotations.**

 # Firebase
 -keep class com.firebase.** { *; }
 -dontwarn com.firebase.**

-keep class org.apache.** { *; }
-dontwarn org.apache.**

#ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
# Retrofit 1.X

-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# Retrofit 2.X
## https://square.github.io/retrofit/ ##

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# If in your rest service interface you use methods with Callback argument.
-keepattributes Exceptions

# If your rest service methods throw custom exceptions, because you've defined an ErrorHandler.
-keepattributes Signature

# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
# Here include the POJO's that have you have created for mapping JSON response to POJO for example.
# Retrofit 2.X
## https://square.github.io/retrofit/ ##
# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

#Code obfuscation
-keepclassmembers class com.water.alkaline.kengen.model** { <fields>; }
-keep public class com.water.alkaline.kengen.model** {
  public protected *;
}

-keepclassmembers class com.google.gms.ads.model** { <fields>; }
-keep public class ccom.google.gms.ads.model** {
  public protected *;
}
#OLD
# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
#-dontoptimize
#-dontpreverify

# If you want to enable optimization, you should include the
# following:
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
#
# Note that you cannot just include these flags in your own
# configuration file; if you are including this file, optimization
# will be turned off. You'll need to either edit this file, or
# duplicate the contents of this file and remove the include of this
# file from your project's proguard.config path property.

#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgent
#-keep public class * extends android.preference.Preference
##-keep public class * extends android.support.v4.app.Fragment
##-keep public class * extends android.support.v4.app.DialogFragment
##-keep public class * extends com.actionbarsherlock.app.SherlockListFragment
##-keep public class * extends com.actionbarsherlock.app.SherlockFragment
##-keep public class * extends com.actionbarsherlock.app.SherlockFragmentActivity
#-keep public class * extends android.app.Fragment
#-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
#-keepclasseswithmembernames class * {
# native <methods>;
#}

#-keep public class * extends android.view.View {
# public <init>(android.content.Context);
# public <init>(android.content.Context, android.util.AttributeSet);
# public <init>(android.content.Context, android.util.AttributeSet, int);
# public void set*(...);
#}

#-keepclasseswithmembers class * {
# public <init>(android.content.Context, android.util.AttributeSet);
#}

#-keepclasseswithmembers class * {
# public <init>(android.content.Context, android.util.AttributeSet, int);
#}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

#-keep class * implements android.os.Parcelable {
# public static final android.os.Parcelable$Creator *;
#}

#-keepclassmembers class **.R$* {
# public static <fields>;
#}

#-keep class com.actionbarsherlock.** { *; }
#-keep interface com.actionbarsherlock.** { *; }


# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontnote androidx.**
-dontwarn android.support.**
-dontwarn androidx.**
-dontwarn com.google.ads.**


# Add this global rule
-keepattributes Signature

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models. Modify to fit the structure
# of your app.
-keep class org.apache.http.** { *; }

-keep class org.codehaus.jackson.** { *; }

-keep class cz.msebera.android.httpclient.** { *; }
-keep class com.loopj.android.http.** { *; }
-keep class com.dd.** {*;}

-keep class com.squareup.okhttp3.** {
*;
}

-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.* { *; }
-keep interface com.squareup.okhttp.* { *; }


-dontwarn okhttp3.internal.platform.*
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

-dontwarn org.apache.http.**
-dontwarn android.net.**
-keep class org.apache.** {*;}
-keep class org.apache.http.** { *; }

#-keep class videostatusmakerapp.Data.** { *; }

-keep class android.support.v8.renderscript.** { *; }


 -dontwarn okio.**
    -keepattributes InnerClasses
    -dontwarn sun.misc.**
    -dontwarn java.lang.invoke.**
    -dontwarn okhttp3.**
    -dontwarn com.anchorfree.sdk.**
    -dontwarn okio.**
    -dontwarn javax.annotation.**
    -dontwarn org.conscrypt.**
    -keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
    #DNSJava
    -keep class org.xbill.DNS.** {*;}
    -dontnote org.xbill.DNS.spi.DNSJavaNameServiceDescriptor
    -dontwarn org.xbill.DNS.spi.DNSJavaNameServiceDescriptor
    -keep class * implements com.google.gson.TypeAdapterFactory
    -keep class * implements com.google.gson.JsonSerializer
    -keep class * implements com.google.gson.JsonDeserializer
    -keep class com.anchorfree.sdk.SessionConfig { *; }
    -keep class com.anchorfree.sdk.fireshield.** { *; }
    -keep class com.anchorfree.sdk.dns.** { *; }
    -keep class com.anchorfree.sdk.HydraSDKConfig { *; }
    -keep class com.anchorfree.partner.api.ClientInfo { *; }
    -keep class com.anchorfree.sdk.NotificationConfig { *; }
    -keep class com.anchorfree.sdk.NotificationConfig$Builder { *; }
    -keep class com.anchorfree.sdk.NotificationConfig$StateNotification { *; }
    -keepclassmembers public class com.anchorfree.ucr.transport.DefaultTrackerTransport {
       public <init>(...);
     }
     -keepclassmembers class com.anchorfree.ucr.SharedPrefsStorageProvider{
        public <init>(...);
     }
     -keepclassmembers class com.anchorfree.sdk.InternalReporting$InternalTrackingTransport{
     public <init>(...);
     }
     -keep class com.anchorfree.sdk.exceptions.* {
        *;
     }

    -keepclassmembers class * implements javax.net.ssl.SSLSocketFactory {
        final javax.net.ssl.SSLSocketFactory delegate;
    }

    # https://stackoverflow.com/questions/56142150/fatal-exception-java-lang-nullpointerexception-in-release-build
    -keepclassmembers,allowobfuscation class * {
      @com.google.gson.annotations.SerializedName <fields>;
    }

    # Please add these rules to your existing keep rules in order to suppress warnings.
    # This is generated automatically by the Android Gradle plugin.
    -dontwarn com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter
    -dontwarn com.thoughtbot.expandablerecyclerview.models.ExpandableGroup




# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation