package ca.sharipov.movieinfo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.models.MovieBrief
import ca.sharipov.movieinfo.util.Constants
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_similar_movie.view.*

class SimilarMoviesAdapter : RecyclerView.Adapter<SimilarMoviesAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<MovieBrief>() {
        override fun areItemsTheSame(oldItem: MovieBrief, newItem: MovieBrief): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieBrief, newItem: MovieBrief): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_similar_movie,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((MovieBrief) -> Unit)? = null

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(Constants.POSTER_URL + movie.posterPath).into(ivMovieImage)
            tvTitle.text = movie.title
            if (movie.releaseDate?.length!! > 4) {
                tvReleaseDate.text = movie.releaseDate.subSequence(0, 4)
            }
            tvVoteAverage.text = movie.voteAverage.toString()

            setOnClickListener {
                onItemClickListener?.let { it(movie) }
            }
        }
    }

    fun setOnItemClickListener(listener: (MovieBrief) -> Unit) {
        onItemClickListener = listener
    }
}













