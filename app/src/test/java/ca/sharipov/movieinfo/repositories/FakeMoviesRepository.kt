package ca.sharipov.movieinfo.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ca.sharipov.movieinfo.models.Movie
import ca.sharipov.movieinfo.models.MovieBrief
import ca.sharipov.movieinfo.models.MovieBriefsResponse
import ca.sharipov.movieinfo.repository.MoviesRepository
import retrofit2.Response

class FakeMoviesRepository : MoviesRepository {

    private val movieBriefItems = mutableListOf<MovieBrief>()

    private val observableMovieBriefItems = MutableLiveData<List<MovieBrief>>(movieBriefItems)
    private val observableMovieBrief = MutableLiveData<MovieBrief>()

    private fun refreshLiveData() {
        observableMovieBriefItems.postValue(movieBriefItems)
    }

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
        movieBriefItems.add(movieBrief)
        refreshLiveData()
        return 0
    }

    override suspend fun deleteMovieBrief(movieBrief: MovieBrief) {
        movieBriefItems.remove(movieBrief)
        refreshLiveData()
    }

    override fun getAllMovieBriefs(): LiveData<List<MovieBrief>> {
        return observableMovieBriefItems
    }

    override fun getMovieBrief(id: Int): LiveData<MovieBrief> {
        return observableMovieBrief
    }
}

