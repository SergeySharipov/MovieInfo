package ca.sharipov.movieinfo.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.adapters.MoviesAdapter
import ca.sharipov.movieinfo.ui.MoviesActivity
import ca.sharipov.movieinfo.ui.MoviesViewModel
import ca.sharipov.movieinfo.util.Constants.Companion.QUERY_PAGE_SIZE
import ca.sharipov.movieinfo.util.Resource
import kotlinx.android.synthetic.main.fragment_popular_movies.*

class PopularMoviesFragment : Fragment(R.layout.fragment_popular_movies) {

    lateinit var viewModel: MoviesViewModel
    lateinit var moviesAdapter: MoviesAdapter

    val TAG = "PopularMoviesFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MoviesActivity).viewModel
        setupRecyclerView()

        moviesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("movieBrief", it)
            }
            findNavController().navigate(
                R.id.action_popularMoviesFragment_to_movieFragment,
                bundle
            )
        }

        viewModel.popularMovieBriefs.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { moviesResponse ->
                        moviesAdapter.differ.submitList(moviesResponse.results.toList())
                        val totalPages = moviesResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.popularMovieBriefsPage == totalPages
                        if(isLastPage) {
                            // rvPopularMovies.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

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

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                Toast.makeText(activity, "shouldPaginate: $totalItemCount", Toast.LENGTH_LONG).show()
                viewModel.getPopularMovieBriefs()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        moviesAdapter = MoviesAdapter()
        rvPopularMovies.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@PopularMoviesFragment.scrollListener)
        }
    }
}








