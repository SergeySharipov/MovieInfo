package ca.sharipov.serhii.movieinfo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.sharipov.serhii.movieinfo.R
import ca.sharipov.serhii.movieinfo.data.models.Genre
import ca.sharipov.serhii.movieinfo.data.models.MovieBrief
import ca.sharipov.serhii.movieinfo.databinding.FragmentMovieDetailsBinding
import ca.sharipov.serhii.movieinfo.ui.MoviesActivity
import ca.sharipov.serhii.movieinfo.ui.MoviesViewModel
import ca.sharipov.serhii.movieinfo.ui.adapters.SimilarMoviesAdapter
import ca.sharipov.serhii.movieinfo.utils.Constants
import ca.sharipov.serhii.movieinfo.utils.Resource
import ca.sharipov.serhii.movieinfo.utils.copyToClipboard
import com.bumptech.glide.RequestManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    @Inject
    lateinit var glide: RequestManager

    lateinit var viewModel: MoviesViewModel
    val args: MovieDetailsFragmentArgs by navArgs()
    lateinit var moviesAdapter: SimilarMoviesAdapter
    private var isSaved: Boolean = false
    private var movieTitleAndReleaseYear: String = ""

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val bindingContent get() = binding.contentMovieDetails

    val TAG = "MovieFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingContent.rvSimilarMovies.adapter = null
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val activity = activity as? MoviesActivity
        activity?.menuInflater?.inflate(R.menu.movie_details_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val activity = activity as? MoviesActivity
        return when (item.itemId) {
            R.id.menuBtnCopy -> {
                copyMovieDetailsToClipboard()
                true
            }
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as? MoviesActivity
        activity?.setSupportActionBar(binding.toolbar)
        activity?.supportActionBar?.title = ""
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        viewModel = (activity as MoviesActivity).viewModel
        setupSimilarMoviesRecyclerView()

        val movieBrief: MovieBrief = args.movieBrief

        glide
            .load(Constants.BACKDROP_URL + movieBrief.backdropPath)
            .centerCrop()
            .placeholder(R.drawable.ic_no_image)
            .into(binding.toolbarBackground)
        glide
            .load(Constants.POSTER_URL + movieBrief.posterPath)
            .centerCrop()
            .placeholder(R.drawable.ic_no_image)
            .into(bindingContent.ivMovieImage)

        bindingContent.tvTitle.text = movieBrief.title

        var releaseYear = ""
        if (movieBrief.releaseDate != null && movieBrief.releaseDate.length > 4) {
            releaseYear = movieBrief.releaseDate.subSequence(0, 4).toString()
            bindingContent.tvReleaseDate.text = releaseYear
        }

        bindingContent.tvVoteAverage.text = movieBrief.voteAverage.toString().substring(0, 3)
        bindingContent.tvOverview.text = movieBrief.overview

        viewModel.observeMovieBrief(movieBrief.id!!)
            .observe(viewLifecycleOwner) { movieBriefSaved ->
                isSaved = movieBriefSaved != null
                isSaved(isSaved)
            }

        getSimilarMovies(movieBrief.id!!)
        getMovieDetails(movieBrief.id!!)

        movieTitleAndReleaseYear = movieBrief.title + " " + releaseYear
        bindingContent.tvTitle.setOnClickListener {
            copyMovieDetailsToClipboard()
        }

        binding.fab.setOnClickListener {
            if (isSaved) {
                viewModel.deleteMovieBrief(movieBrief)
            } else {
                viewModel.saveMovieBrief(movieBrief)
            }
        }
    }

    private fun copyMovieDetailsToClipboard() {
        activity.let {
            it?.copyToClipboard(movieTitleAndReleaseYear)
            Toast.makeText(requireContext(), R.string.msg_copied, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSimilarMovies(movieId: Int) {
        viewModel.getSimilarMovieBriefs(movieId)
        viewModel.similarMovieBriefs.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { moviesResponse ->
                        moviesAdapter.differ.submitList(moviesResponse.results.toList())
                    }
                }
                is Resource.Error -> {
                    response.messageResId?.let { messageResId ->
                        Log.e(TAG, "getSimilarMovies: " + getString(messageResId))
                    }
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    private fun getMovieDetails(movieId: Int) {
        viewModel.getMovie(movieId)
        viewModel.movie.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { movieResponse ->
                        setupGenresChipGroup(movieResponse.genres)
                    }
                }
                is Resource.Error -> {
                    response.messageResId?.let { messageResId ->
                        Log.e(TAG, "getMovieDetails: " + getString(messageResId))
                    }
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    private fun setupGenresChipGroup(genres: List<Genre>?) {
        if (activity != null && genres != null) {
            bindingContent.cgGenres.removeAllViewsInLayout()
            for (genre in genres) {
                val chip = Chip(context)
                chip.text = genre.name
                bindingContent.cgGenres.addView(chip)
            }
        }
    }

    private fun isSaved(isSaved: Boolean) {
        if (isSaved)
            binding.fab.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_saved
                )
            )
        else
            binding.fab.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_not_saved
                )
            )
    }

    private fun setupSimilarMoviesRecyclerView() {
        moviesAdapter = SimilarMoviesAdapter()
        bindingContent.rvSimilarMovies.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
        moviesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("movieBrief", it)
            }
            findNavController().navigate(
                R.id.action_movieFragment_to_movieFragment,
                bundle
            )
        }
    }

}