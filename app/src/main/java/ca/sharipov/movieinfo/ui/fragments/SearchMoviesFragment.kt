package ca.sharipov.movieinfo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.adapters.MovieBriefsAdapter
import ca.sharipov.movieinfo.databinding.FragmentSearchMoviesBinding
import ca.sharipov.movieinfo.ui.AboutActivity
import ca.sharipov.movieinfo.ui.MoviesActivity
import ca.sharipov.movieinfo.ui.MoviesViewModel
import ca.sharipov.movieinfo.util.Constants
import ca.sharipov.movieinfo.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchMoviesFragment : Fragment(R.layout.fragment_search_movies) {

    lateinit var viewModel: MoviesViewModel
    lateinit var movieBriefsAdapter: MovieBriefsAdapter
    lateinit var searchView: SearchView
    private var searchQuery: String? = null
    private val SEARCH_QUERY = "SEARCH_QUERY"

    private var _binding: FragmentSearchMoviesBinding? = null
    private val binding get() = _binding!!
    private val bindingToolbar get() = binding.toolbarSearch
    private val bindingContent get() = binding.contentSearchMovies
    private val bindingErrorMsg get() = bindingContent.itemErrorMessage

    val TAG = "SearchMoviesFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        searchView.clearFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val activity = activity as? MoviesActivity
        activity?.menuInflater?.inflate(R.menu.search_fragment_menu, menu)
        val search: MenuItem = menu.findItem(R.id.menuBtnSearch)
        searchView = search.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.isIconified = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuBtnAbout -> {
                val intent = Intent(activity, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_QUERY, searchQuery)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY)
        }
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? MoviesActivity
        activity?.setSupportActionBar(bindingToolbar.toolbar)
        activity?.supportActionBar?.title = getString(R.string.title_search)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)

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

        viewModel.searchMovieBriefs.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { movieBriefsResponse ->
                        movieBriefsAdapter.differ.submitList(movieBriefsResponse.results.toList())
                        val totalPages: Int =
                            movieBriefsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 1
                        isLastPage = viewModel.searchMovieBriefsPage == totalPages
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.messageResId?.let { messageResId ->
                        showErrorMessage(getString(messageResId))
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        bindingErrorMsg.btnRetry.setOnClickListener {
            performSearch(searchQuery)
            hideErrorMessage()
        }
    }

    private fun performSearch(query: String?) {
        if (query != null && query.length > 1) {
            searchQuery = query
            searchView.clearFocus()
            viewModel.searchMovieBriefs(query)
        }
    }

    private fun hideProgressBar() {
        bindingContent.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        bindingContent.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        bindingContent.itemErrorMessage.root.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        bindingContent.itemErrorMessage.root.visibility = View.VISIBLE
        bindingErrorMsg.tvErrorMessage.text = message
        isError = true
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
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
                performSearch(searchQuery)
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
        bindingContent.rvSearchMovies.apply {
            adapter = movieBriefsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchMoviesFragment.scrollListener)
        }
    }
}