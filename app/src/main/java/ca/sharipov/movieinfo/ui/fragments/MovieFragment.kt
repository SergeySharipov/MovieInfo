package ca.sharipov.movieinfo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.adapters.SimilarMoviesAdapter
import ca.sharipov.movieinfo.models.MovieBrief
import ca.sharipov.movieinfo.ui.MoviesActivity
import ca.sharipov.movieinfo.ui.MoviesViewModel
import ca.sharipov.movieinfo.util.Constants
import ca.sharipov.movieinfo.util.Resource
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_movie.*

class MovieFragment : NavigationChildFragment(R.layout.fragment_movie) {

    lateinit var viewModel: MoviesViewModel
    val args: MovieFragmentArgs by navArgs()
    var isSaved: Boolean = false
    lateinit var moviesAdapter: SimilarMoviesAdapter

    val TAG = "MovieFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MoviesActivity).viewModel
        setupRecyclerView()

        val movieBrief: MovieBrief = args.movieBrief
        Glide.with(this).load(Constants.POSTER_URL + movieBrief.posterPath).into(ivMovieImage)
        tvTitle.text = movieBrief.title
        tvReleaseDate.text = movieBrief.releaseDate
        tvVoteAverage.text = movieBrief.voteAverage.toString()
        tvOverview.text = movieBrief.overview

        viewModel.getMovieBrief(movieBrief.id!!)
            .observe(viewLifecycleOwner, { movieBriefSaved ->
                isSaved = movieBriefSaved != null
                isSaved(isSaved)
            })

        getSimilarMovies(movieBrief.id!!)

        fab.setOnClickListener {
            if (isSaved) {
                viewModel.deleteMovieBrief(movieBrief)
            } else {
                viewModel.saveMovieBrief(movieBrief)
            }
        }
    }

    private fun getSimilarMovies(movieId: Int) {
        viewModel.getSimilarMovieBriefs(movieId)
        viewModel.similarMovieBriefs.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { moviesResponse ->
                        moviesAdapter.differ.submitList(moviesResponse.results.toList())
                    }
                }
                is Resource.Error -> {
                    Log.d(TAG, "getSimilarMovieBriefs: error - " + response.message)
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    private fun isSaved(isSaved: Boolean) {
        if (isSaved)
            fab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_saved))
        else
            fab.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_not_saved
                )
            )
    }

    private fun setupRecyclerView() {
        moviesAdapter = SimilarMoviesAdapter()
        rvSimilarMovies.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

}