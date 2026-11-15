# Project specific ProGuard / R8 rules for C1 Deutsch Release

# Room Database Keep Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class * implements androidx.room.migration.Migration { *; }

-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# C1 Domain Models & Enums
-keep class com.example.data.model.** { *; }
-keepclassmembers enum com.example.data.model.** { *; }
-keep class com.example.data.local.entity.** { *; }

# Coroutines and AndroidX Lifecycle
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Android Speech & TTS
-keep class android.speech.tts.** { *; }
