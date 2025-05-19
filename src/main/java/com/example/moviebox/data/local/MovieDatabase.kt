package com.example.moviebox.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database" // Name of the database file
                )
                // Wipes and rebuilds instead of migrating if no Migration object.
                // Migration is not covered in this scope, so destructive migration is fine.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
