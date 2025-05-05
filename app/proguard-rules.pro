#################################################
# Retrofit
#################################################
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowobfuscation interface * extends <1>

-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response

#################################################
# Gson
#################################################
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-if class com.google.gson.reflect.TypeToken
-keep,allowobfuscation class com.google.gson.reflect.TypeToken
-keep,allowobfuscation class * extends com.google.gson.reflect.TypeToken

-keep,allowobfuscation,allowoptimization @com.google.gson.annotations.JsonAdapter class *

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.Expose <fields>;
  @com.google.gson.annotations.JsonAdapter <fields>;
  @com.google.gson.annotations.Since <fields>;
  @com.google.gson.annotations.Until <fields>;
}

-keepclassmembers class * extends com.google.gson.TypeAdapter {
  <init>();
}
-keepclassmembers class * implements com.google.gson.TypeAdapterFactory {
  <init>();
}
-keepclassmembers class * implements com.google.gson.JsonSerializer {
  <init>();
}
-keepclassmembers class * implements com.google.gson.JsonDeserializer {
  <init>();
}

-if class *
-keepclasseswithmembers,allowobfuscation class <1> {
  @com.google.gson.annotations.SerializedName <fields>;
}
-if class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers,allowobfuscation,allowoptimization class <1> {
  <init>();
}

#################################################
# Coroutines & Flow
#################################################
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepclassmembers class * implements kotlinx.coroutines.flow.FlowCollector {
    *;
}

#################################################
# Lambda, Callbacks, Listeners
#################################################
-keepclassmembers class * {
    *** lambda*(...);
}
-keepclassmembers class * {
    *** *.on*(...);
}

#################################################
# Android MediaPlayer & related callbacks
#################################################
-keep class android.media.MediaPlayer { *; }

-keepclassmembers class android.media.MediaPlayer {
    public void setOnSeekCompleteListener(...);
    public void setOnPreparedListener(...);
    public void setOnCompletionListener(...);
    public void setOnErrorListener(...);
    public void seekTo(...);
}

#################################################
# Keep SelectVoiceActivity to avoid MediaPlayer crash
#################################################
-keep class com.texttomp3.texttospeech.ui.activities.SelectVoiceActivity { *; }

#################################################
# Prevent obfuscation of all your models (optional but safer)
#################################################
-keep class com.texttomp3.texttospeech.data.models.** { *; }
