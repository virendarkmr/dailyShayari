# Add project specific ProGuard rules here.

# --- Kotlinx Serialization ---
-keepattributes *Annotation*, EnclosingMethod, Signature
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class * extends kotlinx.serialization.KSerializer {
    public static ** INSTANCE;
}
# Keep your data classes used for serialization
-keep class com.dailyshayari.db.** { *; }
-keep class com.dailyshayari.data.** { *; }

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- Firebase ---
# Firebase usually bundles its own rules, but keeping common ones safe
-keep class com.google.firebase.** { *; }

# --- Coil ---
-keep class coil.** { *; }
-dontwarn coil.util.CoilUtils

# --- Capturable ---
-keep class dev.shreyaspatil.capturable.** { *; }
