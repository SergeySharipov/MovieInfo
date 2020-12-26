package ca.sharipov.movieinfo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.adapters.MovieBriefsAdapter
import ca.sharipov.movieinfo.databinding.FragmentSavedMoviesBinding
import ca.sharipov.movieinfo.ui.AboutActivity
import ca.sharipov.movieinfo.ui.MoviesActivity
import ca.sharipov.movieinfo.ui.MoviesViewModel
import com.google.android.material.snackbar.Snackbar

class SavedMoviesFragment : Fragment(R.layout.fragment_saved_movies) {

    lateinit var viewModel: MoviesViewModel
    lateinit var movieBriefsAdapter: MovieBriefsAdapter

    private var _binding: FragmentSavedMoviesBinding? = null
    private val binding get() = _binding!!
    private val bindingToolbar get() = binding.toolbarSaved

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
        activity?.supportActionBar?.title = "Saved Movies"
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
                val article = movieBriefsAdapter.differ.currentList[position]
                viewModel.deleteMovieBrief(article)
                Snackbar.make(view, "Successfully deleted movie", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveMovieBrief(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedMovies)
        }

        viewModel.getSavedMovieBriefs().observe(viewLifecycleOwner, { movieBrief ->
            movieBriefsAdapter.differ.submitList(movieBrief)
        })
    }

    private fun setupRecyclerView() {
        movieBriefsAdapter = MovieBriefsAdapter()
        binding.rvSavedMovies.apply {
            adapter = movieBriefsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}