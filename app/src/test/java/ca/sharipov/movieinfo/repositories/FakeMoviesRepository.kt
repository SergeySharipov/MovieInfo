package ca.sharipov.movieinfo.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ca.sharipov.movieinfo.models.Movie
import ca.sharipov.movieinfo.models.MovieBrief
import ca.sharipov.movieinfo.models.MovieBriefsResponse
import ca.sharipov.movieinfo.repository.MoviesRepository
import retrofit2.Response

class FakeMoviesRepository : MoviesRepository {

    override suspend fun getMovie(movieId: Int): Response<Movie> {
        TODO("Not yet implemented")
    }

    override suspend fun getPopularMovies(pageNumber: Int): Response<MovieBriefsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun searchMovieByName(
        searchQuery: String,
        pageNumber: Int
    ): Response<MovieBriefsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSimilarMovies(movieId: Int): Response<MovieBriefsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(movieBrief: MovieBrief): Long {
        TODO("Not yet implemented")
    }

    override fun getAllMovieBriefs(): LiveData<List<MovieBrief>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMovieBrief(movieBrief: MovieBrief) {
        TODO("Not yet implemented")
    }

    override fun getMovieBrief(id: Int): LiveData<MovieBrief> {
        TODO("Not yet implemented")
    }
}
