package ca.sharipov.serhii.movieinfo.repository

import androidx.lifecycle.LiveData
import ca.sharipov.serhii.movieinfo.models.Movie
import ca.sharipov.serhii.movieinfo.models.MovieBrief
import ca.sharipov.serhii.movieinfo.models.MovieBriefsResponse
import retrofit2.Response

interface MoviesRepository {

    suspend fun getMovie(movieId: Int): Response<Movie>

    suspend fun getPopularMovies(pageNumber: Int): Response<MovieBriefsResponse>

    suspend fun searchMovieByName(searchQuery: String, pageNumber: Int): Response<MovieBriefsResponse>

    suspend fun getSimilarMovies(movieId: Int): Response<MovieBriefsResponse>

    suspend fun upsert(movieBrief: MovieBrief): Long

    fun getAllMovieBriefs(): LiveData<List<MovieBrief>>

    suspend fun deleteMovieBrief(movieBrief: MovieBrief)

    fun getMovieBrief(id: Int): LiveData<MovieBrief>
}