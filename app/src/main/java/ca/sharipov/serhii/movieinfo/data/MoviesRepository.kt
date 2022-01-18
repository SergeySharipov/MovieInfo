package ca.sharipov.serhii.movieinfo.data

import androidx.lifecycle.LiveData
import ca.sharipov.serhii.movieinfo.data.models.Movie
import ca.sharipov.serhii.movieinfo.data.models.MovieBrief
import ca.sharipov.serhii.movieinfo.data.models.MovieBriefsResponse
import retrofit2.Response

interface MoviesRepository {

    suspend fun getMovie(movieId: Int): Response<Movie>

    suspend fun getPopularMovies(pageNumber: Int): Response<MovieBriefsResponse>

    suspend fun searchMovieByName(searchQuery: String, pageNumber: Int): Response<MovieBriefsResponse>

    suspend fun getSimilarMovies(movieId: Int): Response<MovieBriefsResponse>

    suspend fun upsertMovieBrief(movieBrief: MovieBrief): Long

    fun observeAllMovieBriefs(): LiveData<List<MovieBrief>>

    suspend fun deleteMovieBrief(movieBrief: MovieBrief)

    fun observeMovieBrief(id: Int): LiveData<MovieBrief>
}