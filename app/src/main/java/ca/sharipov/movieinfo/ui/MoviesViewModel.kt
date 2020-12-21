package ca.sharipov.movieinfo.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.sharipov.movieinfo.MovieInfoApplication
import ca.sharipov.movieinfo.models.MovieBrief
import ca.sharipov.movieinfo.models.MovieBriefsResponse
import ca.sharipov.movieinfo.repository.MoviesRepository
import ca.sharipov.movieinfo.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MoviesViewModel(
    app: Application,
    val moviesRepository: MoviesRepository
) : AndroidViewModel(app) {

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
        return Resource.Error(response.message())
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
        return Resource.Error(response.message())
    }

    private fun handleSimilarMovieBriefsResponse(response: Response<MovieBriefsResponse>): Resource<MovieBriefsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(similarMovieBriefsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchMovieBriefsCall(searchQuery: String) {
        newSearchQuery = searchQuery
        searchMovieBriefs.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    moviesRepository.searchMovieByName(searchQuery, searchMovieBriefsPage)
                searchMovieBriefs.postValue(handleSearchMovieBriefsResponse(response))
            } else {
                searchMovieBriefs.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchMovieBriefs.postValue(Resource.Error("Network Failure"))
                else -> searchMovieBriefs.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safePopularMovieBriefsCall() {
        popularMovieBriefs.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = moviesRepository.getPopularMovies(popularMovieBriefsPage)
                popularMovieBriefs.postValue(handlePopularMovieBriefsResponse(response))
            } else {
                popularMovieBriefs.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> popularMovieBriefs.postValue(Resource.Error("Network Failure"))
                else -> popularMovieBriefs.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSimilarMovieBriefsCall(movieId: Int) {
        similarMovieBriefs.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    moviesRepository.getSimilarMovies(movieId)
                similarMovieBriefs.postValue(handleSimilarMovieBriefsResponse(response))
            } else {
                similarMovieBriefs.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchMovieBriefs.postValue(Resource.Error("Network Failure"))
                else -> searchMovieBriefs.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MovieInfoApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val netInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }

    fun saveMovieBrief(movieBrief: MovieBrief) = viewModelScope.launch {
        moviesRepository.upsert(movieBrief)
    }

    fun getMovieBrief(movieId: Int) =
        moviesRepository.getMovieBrief(movieId)

    fun getSavedMovieBriefs() = moviesRepository.getAllMovieBriefs()

    fun deleteMovieBrief(movieBrief: MovieBrief) = viewModelScope.launch {
        moviesRepository.deleteMovieBrief(movieBrief)
    }
}