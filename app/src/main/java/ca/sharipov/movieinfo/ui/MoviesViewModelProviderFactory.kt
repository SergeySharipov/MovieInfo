package ca.sharipov.movieinfo.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MoviesViewModelProviderFactory(
    val app: Application,
    val moviesRepository: MoviesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MoviesViewModel(app, moviesRepository) as T
    }
}