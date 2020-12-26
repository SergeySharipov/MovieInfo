package ca.sharipov.movieinfo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.databinding.ActivityMoviesBinding
import ca.sharipov.movieinfo.db.MovieBriefDatabase
import ca.sharipov.movieinfo.repository.MoviesRepository

class MoviesActivity : AppCompatActivity() {

    lateinit var viewModel: MoviesViewModel
    private lateinit var binding: ActivityMoviesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val moviesRepository = MoviesRepository(MovieBriefDatabase(this))
        val viewModelProviderFactory = MoviesViewModelProviderFactory(application, moviesRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MoviesViewModel::class.java)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.moviesNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}
