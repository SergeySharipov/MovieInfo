package ca.sharipov.serhii.movieinfo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.serhii.movieinfo.R
import ca.sharipov.serhii.movieinfo.databinding.FragmentSavedMoviesBinding
import ca.sharipov.serhii.movieinfo.ui.AboutActivity
import ca.sharipov.serhii.movieinfo.ui.MoviesActivity
import ca.sharipov.serhii.movieinfo.ui.MoviesViewModel
import ca.sharipov.serhii.movieinfo.ui.adapters.MovieBriefsAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedMoviesFragment : Fragment(R.layout.fragment_saved_movies) {

    lateinit var viewModel: MoviesViewModel
    lateinit var movieBriefsAdapter: MovieBriefsAdapter

    private var _binding: FragmentSavedMoviesBinding? = null
    private val binding get() = _binding!!
    private val bindingToolbar get() = binding.toolbarSaved
    private val bindingContent get() = binding.contentSavedMovies

    val TAG = "PopularMoviesFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingContent.rvSavedMovies.adapter = null
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val activity = activity as? MoviesActivity
        activity?.menuInflater?.inflate(R.menu.saved_fragment_menu, menu)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? MoviesActivity
        activity?.setSupportActionBar(bindingToolbar.toolbar)
        activity?.supportActionBar?.title = getString(R.string.title_saved)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)

        viewModel = (activity as MoviesActivity).viewModel
        setupRecyclerView()

        movieBriefsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("movieBrief", it)
            }
            findNavController().navigate(
                R.id.action_savedMoviesFragment_to_moviesFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val movieBrief = movieBriefsAdapter.differ.currentList[position]
                viewModel.deleteMovieBrief(movieBrief)
                Snackbar.make(view, getString(R.string.msg_movie_deleted), Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(getString(R.string.msg_undo)) {
                            viewModel.saveMovieBrief(movieBrief)
                        }
                        show()
                    }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(bindingContent.rvSavedMovies)
        }

        viewModel.observeAllMovieBriefs().observe(viewLifecycleOwner, { movieBrief ->
            if (movieBrief.isEmpty()) {
                showNothingSavedMessage()
            } else {
                hideNothingSavedMessage()
            }
            movieBriefsAdapter.differ.submitList(movieBrief)
        })
    }

    private fun hideNothingSavedMessage() {
        bindingContent.messageNothingSaved.visibility = View.INVISIBLE
    }

    private fun showNothingSavedMessage() {
        bindingContent.messageNothingSaved.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        movieBriefsAdapter = MovieBriefsAdapter()
        bindingContent.rvSavedMovies.apply {
            adapter = movieBriefsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}