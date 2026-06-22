# Project ProGuard / R8 rules. Applied only to minified (release) builds.
# See http://developer.android.com/guide/developing/tools/proguard.html

# Keep generic signatures + annotations so Gson/Retrofit reflection survives shrinking.
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses, RuntimeVisibleAnnotations

# Readable stack traces from release crashes (obfuscation map still produced).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Gson-serialized DTOs (reflective field access) ---
-keep class com.example.newsapp.data.remote.dto.** { *; }
-keep class com.example.newsapp.data.ai.dto.** { *; }
# Honor @SerializedName even if a field is otherwise unused.
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Domain models (java.io.Serializable, passed through Compose navigation) ---
-keep class com.example.newsapp.domain.model.** { *; }
-keepnames class com.example.newsapp.domain.model.** implements java.io.Serializable

# --- Retrofit / OkHttp / Gson (library reflection) ---
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
# Keep Retrofit service interfaces and their annotated methods.
-keep interface com.example.newsapp.data.remote.api.** { *; }
-keep interface com.example.newsapp.data.ai.** { *; }

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Tink (via androidx.security:security-crypto) references compile-only annotations and optional
# deps (ErrorProne, Google API client, Joda) that aren't on the Android classpath.
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.api.client.**
-dontwarn org.joda.time.**
-keep class com.google.crypto.tink.** { *; }

# Gson keeps its own TypeAdapters via reflection.
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
