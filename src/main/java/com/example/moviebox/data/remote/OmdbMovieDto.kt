package com.example.moviebox.data.remote

// Data Transfer Object representing the movie details from OMDB API JSON response.
data class OmdbMovieDto(
    val Title: String? = null,
    val Year: String? = null,
    val Rated: String? = null,
    val Released: String? = null,
    val Runtime: String? = null,
    val Genre: String? = null,
    val Director: String? = null,
    val Writer: String? = null,
    val Actors: String? = null,
    val Plot: String? = null,
    val Language: String? = null,
    val Country: String? = null,
    val Awards: String? = null,
    val Poster: String? = null,
    val Ratings: List<Rating>? = null, // Nested object for ratings
    val Metascore: String? = null,
    val imdbRating: String? = null,
    val imdbVotes: String? = null,
    val imdbID: String, // Primary key, should be non-null if response is valid
    val Type: String? = null,
    val DVD: String? = null,
    val BoxOffice: String? = null,
    val Production: String? = null,
    val Website: String? = null,
    val Response: String // "True" or "False"
) {
    // Represents rating source and value
    data class Rating(
        val Source: String? = null,
        val Value: String? = null
    )
}

// Represents the overall response when searching for multiple movies
data class OmdbSearchResponse(
    val Search: List<OmdbSearchResultItem>? = null, // List of movie summaries
    val totalResults: String? = null, // Total number of results found
    val Response: String // "True" or "False"
)

// Represents a single movie item within the search results list
data class OmdbSearchResultItem(
    val Title: String? = null,
    val Year: String? = null,
    val imdbID: String, // Need this to potentially fetch full details later
    val Type: String? = null,
    val Poster: String? = null
)
