package ca.sharipov.serhii.movieinfo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.sharipov.serhii.movieinfo.data.MoviesRepository
import ca.sharipov.serhii.movieinfo.utils.InternetConnectionUtil

class MoviesViewModelProviderFactory(
    private val moviesRepository: MoviesRepository,
    private val connection: InternetConnectionUtil
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MoviesViewModel(moviesRepository, connection) as T
    }
}