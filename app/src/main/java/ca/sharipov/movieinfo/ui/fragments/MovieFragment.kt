package ca.sharipov.movieinfo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.models.MovieBrief
import ca.sharipov.movieinfo.ui.MoviesActivity
import ca.sharipov.movieinfo.ui.MoviesViewModel
import ca.sharipov.movieinfo.util.Constants
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movie.*

class MovieFragment : Fragment(R.layout.fragment_movie) {

    lateinit var viewModel: MoviesViewModel
    val args: MovieFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MoviesActivity).viewModel
        val movieBrief: MovieBrief = args.movieBrief
        Glide.with(this).load(Constants.POSTER_URL + movieBrief.posterPath).into(ivMovieImage)
        tvTitle.text = movieBrief.title
        tvReleaseDate.text = movieBrief.releaseDate
        tvVoteAverage.text = movieBrief.voteAverage.toString()
        tvOverview.text = movieBrief.overview

        fab.setOnClickListener {
            viewModel.saveMovieBrief(movieBrief)
            Snackbar.make(view, "Movie saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}