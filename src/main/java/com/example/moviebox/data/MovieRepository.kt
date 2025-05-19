package com.example.moviebox.data

import android.content.Context
import android.util.Log
import com.example.moviebox.data.local.MovieDao
import com.example.moviebox.data.local.MovieEntity
import com.example.moviebox.data.remote.OmdbApiService // Placeholder
import com.example.moviebox.data.remote.OmdbSearchResponse
import com.example.moviebox.data.remote.OmdbSearchResultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MovieRepository(
    private val movieDao: MovieDao,
    private val context: Context // Needed for API key retrieval if stored securely
) {
    // OMDB API Key - Replace with your actual key securely!
    // Avoid hardcoding keys directly in source code for production apps.
    // Consider build configs or secure storage.
    private val apiKey = "3813e5b0" // Use the provided API key

    // Hardcoded initial movie data (as per the spec link)
    private val initialMovies = listOf(
        // Note: This data matches the structure in the provided movies.txt
        // but maps to our MovieEntity. imdbID is crucial.
        MovieEntity(
            title = "The Shawshank Redemption", year = "1994", rated = "R",
            released = "14 Oct 1994", runtime = "142 min", genre = "Drama",
            director = "Frank Darabont", writer = "Stephen King, Frank Darabont",
            actors = "Tim Robbins, Morgan Freeman, Bob Gunton",
            plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
            language = "English", country = "USA", awards = "Nominated for 7 Oscars.",
            poster = "https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmNhMC00ZDIzLWFmNTEtODM1ZmRlYWMwMWFmXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg",
            imdbRating = "9.3", imdbVotes = "2,343,111", imdbID = "tt0111161", type = "movie", response = "True",
            metascore = "80" // Add other fields if available/needed
        ),
         MovieEntity(
            title = "The Godfather", year = "1972", rated = "R",
            released = "24 Mar 1972", runtime = "175 min", genre = "Crime, Drama",
            director = "Francis Ford Coppola", writer = "Mario Puzo, Francis Ford Coppola",
            actors = "Marlon Brando, Al Pacino, James Caan",
            plot = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
            language = "English, Italian, Latin", country = "USA", awards = "Won 3 Oscars.",
            poster = "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",
            imdbRating = "9.2", imdbVotes = "1,620,367", imdbID = "tt0068646", type = "movie", response = "True",
            metascore = "100"
        ),
         MovieEntity(
            title = "The Dark Knight", year = "2008", rated = "PG-13",
            released = "18 Jul 2008", runtime = "152 min", genre = "Action, Crime, Drama",
            director = "Christopher Nolan", writer = "Jonathan Nolan, Christopher Nolan, David S. Goyer",
            actors = "Christian Bale, Heath Ledger, Aaron Eckhart",
            plot = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            language = "English, Mandarin", country = "USA, UK", awards = "Won 2 Oscars.",
            poster = "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SX300.jpg",
            imdbRating = "9.0", imdbVotes = "2,303,232", imdbID = "tt0468569", type = "movie", response = "True",
            metascore = "84"
        ),
        MovieEntity(
            title = "Pulp Fiction", year = "1994", rated = "R",
            released = "14 Oct 1994", runtime = "154 min", genre = "Crime, Drama",
            director = "Quentin Tarantino", writer = "Quentin Tarantino, Roger Avary",
            actors = "John Travolta, Uma Thurman, Samuel L. Jackson",
            plot = "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
            language = "English, Spanish, French", country = "USA", awards = "Won 1 Oscar.",
            poster = "https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",
            imdbRating = "8.9", imdbVotes = "1,826,188", imdbID = "tt0110912", type = "movie", response = "True",
            metascore = "94"
        ),
        MovieEntity(
            title = "Inception", year = "2010", rated = "PG-13",
            released = "16 Jul 2010", runtime = "148 min", genre = "Action, Adventure, Sci-Fi",
            director = "Christopher Nolan", writer = "Christopher Nolan",
            actors = "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page", // Note: Actor's name is Elliot Page now
            plot = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
            language = "English, Japanese, French", country = "USA, UK", awards = "Won 4 Oscars.",
            poster = "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg",
            imdbRating = "8.8", imdbVotes = "2,067,042", imdbID = "tt1375666", type = "movie", response = "True",
            metascore = "74"
        ),
         MovieEntity(
            title = "Fight Club", year = "1999", rated = "R",
            released = "15 Oct 1999", runtime = "139 min", genre = "Drama",
            director = "David Fincher", writer = "Chuck Palahniuk, Jim Uhls",
            actors = "Brad Pitt, Edward Norton, Meat Loaf",
            plot = "An insomniac office worker looking for a way to change his life crosses paths with a devil-may-care soap maker and they form an underground fight club that evolves into something much, much more.",
            language = "English", country = "USA, Germany", awards = "Nominated for 1 Oscar.",
            poster = "https://m.media-amazon.com/images/M/MV5BMmEzNTkxYjQtZTc0MC00YTVjLTg5ZTEtZWMwOWVlYzY0NWIwXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",
            imdbRating = "8.8", imdbVotes = "1,854,740", imdbID = "tt0137523", type = "movie", response = "True",
            metascore = "66"
        ),
        MovieEntity(
            title = "Forrest Gump", year = "1994", rated = "PG-13",
            released = "06 Jul 1994", runtime = "142 min", genre = "Comedy, Drama, Romance",
            director = "Robert Zemeckis", writer = "Winston Groom, Eric Roth",
            actors = "Tom Hanks, Robin Wright, Gary Sinise",
            plot = "The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.",
            language = "English", country = "USA", awards = "Won 6 Oscars.",
            poster = "https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg",
            imdbRating = "8.8", imdbVotes = "1,809,221", imdbID = "tt0109830", type = "movie", response = "True",
            metascore = "82"
        ),
        MovieEntity(
            title = "The Matrix", year = "1999", rated = "R",
            released = "31 Mar 1999", runtime = "136 min", genre = "Action, Sci-Fi",
            director = "Lana Wachowski, Lilly Wachowski", writer = "Lilly Wachowski, Lana Wachowski",
            actors = "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
            plot = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
            language = "English", country = "USA", awards = "Won 4 Oscars.",
            poster = "https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg",
            imdbRating = "8.7", imdbVotes = "1,676,426", imdbID = "tt0133093", type = "movie", response = "True",
            metascore = "73"
        ),
        MovieEntity( // Adding one more as example
             title = "Goodfellas", year = "1990", rated = "R",
             released = "21 Sep 1990", runtime = "146 min", genre = "Biography, Crime, Drama",
             director = "Martin Scorsese", writer = "Nicholas Pileggi, Martin Scorsese",
             actors = "Robert De Niro, Ray Liotta, Joe Pesci",
             plot = "The story of Henry Hill and his life in the mob, covering his relationship with his wife Karen Hill and his mob partners Jimmy Conway and Tommy DeVito in the Italian-American crime syndicate.",
             language = "English, Italian", country = "USA", awards = "Won 1 Oscar.",
             poster = "https://m.media-amazon.com/images/M/MV5BY2NkZjEzMDgtN2RjYy00YzM1LWI4ZmQtMjIwYjFjNmI3ZGEwXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",
             imdbRating = "8.7", imdbVotes = "1,020,727", imdbID = "tt0099685", type = "movie", response = "True",
             metascore = "90"
        )
        // Add more movies if needed following the MovieEntity structure
    )

    // Function to insert the initial hardcoded movies into the database
    suspend fun addInitialMovies() {
        withContext(Dispatchers.IO) { // Perform DB operations on IO thread
            try {
                Log.d("MovieRepository", "Attempting to insert ${initialMovies.size} initial movies.")
                movieDao.insertMovies(initialMovies)
                Log.d("MovieRepository", "Successfully inserted initial movies.")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error inserting initial movies", e)
                // Handle error appropriately - maybe notify UI
            }
        }
    }

    // Search DB for actors (case-insensitive, substring)
    fun searchMoviesByActor(actorName: String): Flow<List<MovieEntity>> {
        return movieDao.searchMoviesByActor(actorName)
    }

    // Fetch movie details from OMDB API by title
    suspend fun fetchMovieFromApi(title: String): MovieEntity? {
        // Using HttpURLConnection as per spec (no 3rd party libs)
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            var result: MovieEntity? = null
            val urlString = "http://www.omdbapi.com/?t=${title.replace(" ", "+")}&apikey=$apiKey"
            Log.d("MovieRepository", "Fetching from URL: $urlString")

            try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000 // 5 seconds
                connection.readTimeout = 5000 // 5 seconds
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val buffer = StringBuffer()
                    if (inputStream == null) {
                        return@withContext null // Nothing to do.
                    }
                    reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        buffer.append(line + "\n")
                    }

                    if (buffer.isEmpty()) {
                        return@withContext null // Stream was empty. No point in parsing.
                    }
                    val jsonResponse = buffer.toString()
                    Log.d("MovieRepository", "API Response: $jsonResponse")
                    result = parseMovieJson(jsonResponse)
                } else {
                    Log.e("MovieRepository", "API Error - Response Code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching data from API", e)
            } finally {
                connection?.disconnect()
                reader?.close()
            }
            result
        }
    }

     // Search movies from OMDB API by title substring
    suspend fun searchMoviesFromDb(titleSubstring: String): List<MovieEntity> {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            val results = mutableListOf<MovieEntity>()
            // OMDB search uses 's=' parameter
            val urlString = "http://www.omdbapi.com/?s=${titleSubstring.replace(" ", "+")}&apikey=$apiKey"
            Log.d("MovieRepository", "Searching API URL: $urlString")

            try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000 // Longer timeout for search
                connection.readTimeout = 8000
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val buffer = StringBuffer()
                    if (inputStream != null) {
                        reader = BufferedReader(InputStreamReader(inputStream))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            buffer.append(line).append("\n")
                        }
                    }

                    if (buffer.isNotEmpty()) {
                        val jsonResponse = buffer.toString()
                        Log.d("MovieRepository", "API Search Response: $jsonResponse")
                        // Parse the list of movies from the 'Search' array
                        val jsonObject = JSONObject(jsonResponse)
                        if (jsonObject.optString("Response") == "True") {
                            val searchArray = jsonObject.getJSONArray("Search")
                            for (i in 0 until searchArray.length()) {
                                val movieJson = searchArray.getJSONObject(i)
                                // We only get basic details in search results (Title, Year, imdbID, Type, Poster)
                                // We might need another call to get full details if required,
                                // but for now, let's map what we have.
                                val movie = MovieEntity(
                                    imdbID = movieJson.getString("imdbID"),
                                    title = movieJson.getString("Title"),
                                    year = movieJson.getString("Year"),
                                    type = movieJson.getString("Type"),
                                    poster = movieJson.getString("Poster"),
                                    // Fill other fields with null or default values
                                    rated = null, released = null, runtime = null, genre = null,
                                    director = null, writer = null, actors = null, plot = null,
                                    language = null, country = null, awards = null, metascore = null,
                                    imdbRating = null, imdbVotes = null, dvd = null, boxOffice = null,
                                    production = null, website = null, response = "True" // Assuming valid if in list
                                )
                                results.add(movie)
                            }
                        } else {
                            Log.w("MovieRepository", "API Search returned Response=False: ${jsonObject.optString("Error")}")
                        }
                    }
                } else {
                    Log.e("MovieRepository", "API Search Error - Response Code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error searching data from API", e)
            } finally {
                connection?.disconnect()
                reader?.close()
            }
            results
        }
    }

    // Search multiple movies from OMDB API by search query (uses 's=')
    suspend fun searchMoviesFromApi(query: String): List<OmdbSearchResultItem> {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            var resultList: List<OmdbSearchResultItem> = emptyList()
            // Use 's=' for searching multiple titles
            val urlString = "http://www.omdbapi.com/?s=${query.replace(" ", "+")}&apikey=$apiKey"
            Log.d("MovieRepository", "Searching API URL: $urlString")

            try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val buffer = StringBuffer()
                    if (inputStream == null) return@withContext emptyList()

                    reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        buffer.append(line + "\n")
                    }

                    if (buffer.isEmpty()) return@withContext emptyList()

                    val jsonResponse = buffer.toString()
                    Log.d("MovieRepository", "API Search Response: $jsonResponse")
                    val searchResponse = parseMovieSearchResponseJson(jsonResponse)
                    if (searchResponse != null && searchResponse.Response == "True") {
                        resultList = searchResponse.Search ?: emptyList() // Assign empty list if Search is null
                    } else {
                         Log.w("MovieRepository", "API search returned Response=False or null: ${searchResponse?.Response}")
                    }
                } else {
                    Log.e("MovieRepository", "API Search Error - Response Code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error searching data from API", e)
            } finally {
                connection?.disconnect()
                reader?.close()
            }
            resultList
        }
    }

    // Helper to parse JSON response into OmdbSearchResponse
    private fun parseMovieSearchResponseJson(jsonString: String): OmdbSearchResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            OmdbSearchResponse(
                Search = jsonObject.optJSONArray("Search")?.let { jsonArray ->
                    mutableListOf<OmdbSearchResultItem>().apply {
                        for (i in 0 until jsonArray.length()) {
                            add(
                                OmdbSearchResultItem(
                                    Title = jsonArray.getJSONObject(i).optString("Title"),
                                    Year = jsonArray.getJSONObject(i).optString("Year"),
                                    imdbID = jsonArray.getJSONObject(i).optString("imdbID"),
                                    Type = jsonArray.getJSONObject(i).optString("Type"),
                                    Poster = jsonArray.getJSONObject(i).optString("Poster")
                                )
                            )
                        }
                    }
                },
                Response = jsonObject.optString("Response"),
                totalResults = jsonObject.optString("totalResults")
            )
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error parsing JSON", e)
            null
        }
    }

    // Save a single movie (fetched from API) to the database
    suspend fun saveMovieToDb(movie: MovieEntity) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Saving movie to DB: ${movie.title}")
                movieDao.insertMovie(movie)
                Log.d("MovieRepository", "Successfully saved movie: ${movie.title}")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error saving movie ${movie.title} to DB", e)
            }
        }
    }

    // Helper to parse JSON response into MovieEntity
    private fun parseMovieJson(jsonString: String): MovieEntity? {
        return try {
            val jsonObject = JSONObject(jsonString)
            if (jsonObject.getString("Response") == "True") {
                 MovieEntity(
                    imdbID = jsonObject.getString("imdbID"),
                    title = jsonObject.optString("Title", null),
                    year = jsonObject.optString("Year", null),
                    rated = jsonObject.optString("Rated", null),
                    released = jsonObject.optString("Released", null),
                    runtime = jsonObject.optString("Runtime", null),
                    genre = jsonObject.optString("Genre", null),
                    director = jsonObject.optString("Director", null),
                    writer = jsonObject.optString("Writer", null),
                    actors = jsonObject.optString("Actors", null),
                    plot = jsonObject.optString("Plot", null),
                    language = jsonObject.optString("Language", null),
                    country = jsonObject.optString("Country", null),
                    awards = jsonObject.optString("Awards", null),
                    poster = jsonObject.optString("Poster", null),
                    metascore = jsonObject.optString("Metascore", null),
                    imdbRating = jsonObject.optString("imdbRating", null),
                    imdbVotes = jsonObject.optString("imdbVotes", null),
                    type = jsonObject.optString("Type", null),
                    dvd = jsonObject.optString("DVD", null),
                    boxOffice = jsonObject.optString("BoxOffice", null),
                    production = jsonObject.optString("Production", null),
                    website = jsonObject.optString("Website", null),
                    response = jsonObject.getString("Response")
                 )
            } else {
                 Log.w("MovieRepository", "API returned Response=False: ${jsonObject.optString("Error")}")
                 null
            }
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error parsing JSON", e)
            null
        }
    }
}
