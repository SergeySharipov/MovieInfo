package ca.sharipov.movieinfo.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.adapters.MovieBriefsAdapter
import ca.sharipov.movieinfo.ui.MoviesActivity
import ca.sharipov.movieinfo.ui.MoviesViewModel
import ca.sharipov.movieinfo.util.Constants
import ca.sharipov.movieinfo.util.Constants.Companion.SEARCH_MOVIES_TIME_DELAY
import ca.sharipov.movieinfo.util.Resource
import kotlinx.android.synthetic.main.fragment_search_movies.*
import kotlinx.android.synthetic.main.item_error_message.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchMoviesFragment : Fragment(R.layout.fragment_search_movies) {

    lateinit var viewModel: MoviesViewModel
    lateinit var movieBriefsAdapter: MovieBriefsAdapter
    val TAG = "SearchMoviesFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? MoviesActivity
        activity?.supportActionBar?.title = "Search Movies"
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(false)

        viewModel = (activity as MoviesActivity).viewModel
        setupRecyclerView()

        movieBriefsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("movieBrief", it)
            }
            findNavController().navigate(
                R.id.action_searchMoviesFragment_to_movieFragment,
                bundle
            )
        }

        etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_MOVIES_TIME_DELAY)
                editable?.let {
                    performSearch(editable.toString())
                }
            }
        }

        viewModel.searchMovieBriefs.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { movieBriefsResponse ->
                        movieBriefsAdapter.differ.submitList(movieBriefsResponse.results.toList())
                        val totalPages =
                            movieBriefsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchMovieBriefsPage == totalPages
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_SHORT)
                            .show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        btnRetry.setOnClickListener {
            if (etSearch.text.toString().isNotEmpty()) {
                viewModel.searchMovieBriefs(etSearch.text.toString())
            } else {
                hideErrorMessage()
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.length > 1) {
            viewModel.searchMovieBriefs(query)
        }
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemErrorMessage.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemErrorMessage.visibility = View.VISIBLE
        tvErrorMessage.text = message
        isError = true
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchMovieBriefs(etSearch.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }


    private fun setupRecyclerView() {
        movieBriefsAdapter = MovieBriefsAdapter()
        rvSearchMovies.apply {
            adapter = movieBriefsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchMoviesFragment.scrollListener)
        }
    }
}