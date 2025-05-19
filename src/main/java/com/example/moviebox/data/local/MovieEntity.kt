package com.example.moviebox.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val imdbID: String, // Use IMDb ID as primary key as it's unique
    val title: String?,
    val year: String?,
    val rated: String?,
    val released: String?,
    val runtime: String?,
    val genre: String?,
    val director: String?,
    val writer: String?,
    val actors: String?,
    val plot: String?,
    val language: String?,
    val country: String?,
    val awards: String?,
    // Ratings can be complex, storing as a simple string for now.
    // A better approach might be a separate table or type converter.
    val poster: String?, // Added Poster URL, often useful
    val metascore: String?,
    val imdbRating: String?,
    val imdbVotes: String?,
    val type: String?,
    val dvd: String? = null, // Fields from OMDB not in the initial hardcoded list
    val boxOffice: String? = null,
    val production: String? = null,
    val website: String? = null,
    val response: String? // "True" or "False"
)
