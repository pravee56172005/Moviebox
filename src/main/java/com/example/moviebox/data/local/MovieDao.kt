package com.example.moviebox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow // Use Flow for reactive updates

@Dao
interface MovieDao {

    // Insert a movie or replace if it exists (based on primary key imdbID)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    // Insert multiple movies
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    // Get all movies (consider Flow for reactive UI updates)
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

    // Get a movie by its IMDb ID
    @Query("SELECT * FROM movies WHERE imdbID = :imdbId")
    suspend fun getMovieById(imdbId: String): MovieEntity?

    // Search movies by actor name (case-insensitive substring search)
    @Query("SELECT * FROM movies WHERE LOWER(actors) LIKE '%' || LOWER(:actorName) || '%' ")
    fun searchMoviesByActor(actorName: String): Flow<List<MovieEntity>>

    // --- Potentially useful queries (not explicitly required by spec but good practice) ---

    // Get movie by Title (might not be unique, returns a list)
    @Query("SELECT * FROM movies WHERE LOWER(title) = LOWER(:title)")
    suspend fun getMoviesByTitle(title: String): List<MovieEntity>

    // Delete all movies (useful for clearing DB if needed)
    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()
}
