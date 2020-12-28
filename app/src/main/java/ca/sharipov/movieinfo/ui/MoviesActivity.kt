package ca.sharipov.movieinfo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.databinding.ActivityMoviesBinding
import ca.sharipov.movieinfo.repository.MoviesRepository
import ca.sharipov.movieinfo.util.InternetConnectionUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoviesActivity : AppCompatActivity() {

    lateinit var viewModel: MoviesViewModel
    private lateinit var binding: ActivityMoviesBinding

    @Inject
    lateinit var moviesRepository: MoviesRepository

    @Inject
    lateinit var connection: InternetConnectionUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val viewModelProviderFactory = MoviesViewModelProviderFactory(moviesRepository, connection)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MoviesViewModel::class.java)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.moviesNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}
