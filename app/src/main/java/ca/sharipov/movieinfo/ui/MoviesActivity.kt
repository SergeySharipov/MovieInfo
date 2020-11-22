package ca.sharipov.movieinfo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import ca.sharipov.movieinfo.R
import kotlinx.android.synthetic.main.activity_movies.*

class MoviesActivity : AppCompatActivity() {

    lateinit var viewModel: MoviesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        val moviesRepository = MoviesRepository()
        val viewModelProviderFactory = MoviesViewModelProviderFactory(application, moviesRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MoviesViewModel::class.java)
        bottomNavigationView.setupWithNavController(moviesNavHostFragment.findNavController())
    }
}
