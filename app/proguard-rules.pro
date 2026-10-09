# Keep Room database annotations and generated database access.
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keep class **_Impl

# Keep Hilt-generated entry points and workers when shrinking is enabled.
-keep class dagger.hilt.** { *; }
-keep class androidx.hilt.** { *; }
