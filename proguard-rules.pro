# Add project specific ProGuard rules here.
# You can find more information about ProGuard here: https://developer.android.com/studio/build/shrink-code

# If your project uses androidx libraries, you might want to uncomment the following lines:
#-keep class androidx.lifecycle.ViewModel { *; }
#-keep class androidx.room.RoomDatabase { *; }

# Keep Room related classes if using obfuscation
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static final androidx.room.RoomDatabase$Callback sCallback;
}
-keep class androidx.room.** { *; }
-keep class com.example.moviebox.data.local.** { *; } # Keep your data entities and DAO


