package ca.sharipov.movieinfo.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.sharipov.movieinfo.MovieInfoApplication
import ca.sharipov.movieinfo.models.MovieBriefsResponse
import ca.sharipov.movieinfo.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

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


    init {
        getPopularMovieBriefs()
    }

    fun getPopularMovieBriefs() = viewModelScope.launch {
        safePopularMovieBriefsCall()
    }

    fun searchMovieBriefs(searchQuery: String) = viewModelScope.launch {
        safeSearchMovieBriefsCall(searchQuery)
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
                searchMovieBriefsPage++
                if (searchMovieBriefsResponse == null) {
                    searchMovieBriefsResponse = resultResponse
                } else {
                    val oldMovies = searchMovieBriefsResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(searchMovieBriefsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchMovieBriefsCall(searchQuery: String) {
        searchMovieBriefs.postValue(Resource.Loading())
//        try {
            if (hasInternetConnection()) {
                val response =
                    moviesRepository.searchMovieByName(searchQuery, searchMovieBriefsPage)
                searchMovieBriefs.postValue(handleSearchMovieBriefsResponse(response))
            } else {
                searchMovieBriefs.postValue(Resource.Error("No internet connection"))
            }
//        } catch (t: Throwable) {
//            when (t) {
//                is IOException -> searchMovieBriefs.postValue(Resource.Error("Network Failure"))
//                else -> searchMovieBriefs.postValue(Resource.Error("Conversion Error"))
//            }
//        }
    }

    private suspend fun safePopularMovieBriefsCall() {
        popularMovieBriefs.postValue(Resource.Loading())
//        try {
            if (hasInternetConnection()) {
                val response = moviesRepository.getPopularMovies(popularMovieBriefsPage)
                popularMovieBriefs.postValue(handlePopularMovieBriefsResponse(response))
            } else {
                popularMovieBriefs.postValue(Resource.Error("No internet connection"))
            }
//        } catch (t: Throwable) {
//            when (t) {
//                is IOException -> popularMovieBriefs.postValue(Resource.Error("Network Failure"))
//                else -> popularMovieBriefs.postValue(Resource.Error("Conversion Error"))
//            }
//        }
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
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}