package ca.sharipov.serhii.movieinfo.ui

import android.net.NetworkCapabilities.*
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sharipov.serhii.movieinfo.R
import ca.sharipov.serhii.movieinfo.models.Movie
import ca.sharipov.serhii.movieinfo.models.MovieBrief
import ca.sharipov.serhii.movieinfo.models.MovieBriefsResponse
import ca.sharipov.serhii.movieinfo.repository.MoviesRepository
import ca.sharipov.serhii.movieinfo.util.InternetConnectionUtil
import ca.sharipov.serhii.movieinfo.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class MoviesViewModel @ViewModelInject constructor(
    private val repository: MoviesRepository,
    private val connection: InternetConnectionUtil
) : ViewModel() {

    private val TAG = "MoviesViewModel"

    val popularMovieBriefs: MutableLiveData<Resource<MovieBriefsResponse>> = MutableLiveData()
    var popularMovieBriefsPage = 1
    var popularMovieBriefsResponse: MovieBriefsResponse? = null

    val searchMovieBriefs: MutableLiveData<Resource<MovieBriefsResponse>> = MutableLiveData()
    var searchMovieBriefsPage = 1
    var searchMovieBriefsResponse: MovieBriefsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    val similarMovieBriefs: MutableLiveData<Resource<MovieBriefsResponse>> = MutableLiveData()
    var similarMovieBriefsResponse: MovieBriefsResponse? = null

    val movie: MutableLiveData<Resource<Movie>> = MutableLiveData()
    var movieResponse: Movie? = null


    init {
        getPopularMovieBriefs()
    }

    fun getPopularMovieBriefs() = viewModelScope.launch {
        safePopularMovieBriefsCall()
    }

    fun searchMovieBriefs(searchQuery: String) = viewModelScope.launch {
        safeSearchMovieBriefsCall(searchQuery)
    }

    fun getSimilarMovieBriefs(movieId: Int) = viewModelScope.launch {
        safeSimilarMovieBriefsCall(movieId)
    }

    fun getMovie(movieId: Int) = viewModelScope.launch {
        safeGetMovieCall(movieId)
    }

    private fun handlePopularMovieBriefsResponse(response: Response<MovieBriefsResponse>): Resource<MovieBriefsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                popularMovieBriefsPage++
                if (popularMovieBriefsResponse == null) {
                    popularMovieBriefsResponse = resultResponse
                } else {
                    val oldMovies = popularMovieBriefsResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(popularMovieBriefsResponse ?: resultResponse)
            }
        }
        Log.e(TAG, "handlePopularMovieBriefsResponse: " + response.message())
        return Resource.Error(R.string.msg_unknown_error)
    }

    private fun handleSearchMovieBriefsResponse(response: Response<MovieBriefsResponse>): Resource<MovieBriefsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchMovieBriefsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchMovieBriefsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchMovieBriefsResponse = resultResponse
                } else {
                    searchMovieBriefsPage++
                    val oldMovies = searchMovieBriefsResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(searchMovieBriefsResponse ?: resultResponse)
            }
        }
        Log.e(TAG, "handleSearchMovieBriefsResponse: " + response.message())
        return Resource.Error(R.string.msg_unknown_error)
    }

    private fun handleSimilarMovieBriefsResponse(response: Response<MovieBriefsResponse>): Resource<MovieBriefsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(similarMovieBriefsResponse ?: resultResponse)
            }
        }
        Log.e(TAG, "handleSimilarMovieBriefsResponse: " + response.message())
        return Resource.Error(R.string.msg_unknown_error)
    }

    private fun handleGetMovieResponse(response: Response<Movie>): Resource<Movie> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(movieResponse ?: resultResponse)
            }
        }
        Log.e(TAG, "handleGetMovieResponse: " + response.message())
        return Resource.Error(R.string.msg_unknown_error)
    }

    private suspend fun safeSearchMovieBriefsCall(searchQuery: String) {
        newSearchQuery = searchQuery
        searchMovieBriefs.postValue(Resource.Loading())
        try {
            if (connection.hasInternetConnection()) {
                val response =
                    repository.searchMovieByName(searchQuery, searchMovieBriefsPage)
                searchMovieBriefs.postValue(handleSearchMovieBriefsResponse(response))
            } else {
                searchMovieBriefs.postValue(Resource.Error(R.string.msg_no_connection))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchMovieBriefs.postValue(Resource.Error(R.string.msg_network_failure))
                else -> searchMovieBriefs.postValue(Resource.Error(R.string.msg_conversion_error))
            }
        }
    }

    private suspend fun safePopularMovieBriefsCall() {
        popularMovieBriefs.postValue(Resource.Loading())
        try {
            if (connection.hasInternetConnection()) {
                val response = repository.getPopularMovies(popularMovieBriefsPage)
                popularMovieBriefs.postValue(handlePopularMovieBriefsResponse(response))
            } else {
                popularMovieBriefs.postValue(Resource.Error(R.string.msg_no_connection))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> popularMovieBriefs.postValue(Resource.Error(R.string.msg_network_failure))
                else -> popularMovieBriefs.postValue(Resource.Error(R.string.msg_conversion_error))
            }
        }
    }

    private suspend fun safeSimilarMovieBriefsCall(movieId: Int) {
        similarMovieBriefs.postValue(Resource.Loading())
        try {
            if (connection.hasInternetConnection()) {
                val response =
                    repository.getSimilarMovies(movieId)
                similarMovieBriefs.postValue(handleSimilarMovieBriefsResponse(response))
            } else {
                similarMovieBriefs.postValue(Resource.Error(R.string.msg_no_connection))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> similarMovieBriefs.postValue(Resource.Error(R.string.msg_network_failure))
                else -> similarMovieBriefs.postValue(Resource.Error(R.string.msg_conversion_error))
            }
        }
    }

    private suspend fun safeGetMovieCall(movieId: Int) {
        movie.postValue(Resource.Loading())
        try {
            if (connection.hasInternetConnection()) {
                val response =
                    repository.getMovie(movieId)
                movie.postValue(handleGetMovieResponse(response))
            } else {
                movie.postValue(Resource.Error(R.string.msg_no_connection))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> movie.postValue(Resource.Error(R.string.msg_network_failure))
                else -> movie.postValue(Resource.Error(R.string.msg_conversion_error))
            }
        }
    }

    fun saveMovieBrief(movieBrief: MovieBrief) = viewModelScope.launch {
        repository.upsert(movieBrief)
    }

    fun getMovieBrief(movieId: Int) =
        repository.getMovieBrief(movieId)

    fun getSavedMovieBriefs() = repository.getAllMovieBriefs()

    fun deleteMovieBrief(movieBrief: MovieBrief) = viewModelScope.launch {
        repository.deleteMovieBrief(movieBrief)
    }
}