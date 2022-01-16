package ca.sharipov.serhii.movieinfo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.serhii.movieinfo.databinding.ItemMoviePreviewBinding
import ca.sharipov.serhii.movieinfo.models.MovieBrief
import ca.sharipov.serhii.movieinfo.util.Constants
import com.bumptech.glide.Glide


class MovieBriefsAdapter : RecyclerView.Adapter<MovieBriefsAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMoviePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMoviePreviewBinding.inflate(layoutInflater, parent, false)

        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((MovieBrief) -> Unit)? = null

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(Constants.POSTER_URL + movie.posterPath)
                .into(holder.binding.ivMovieImage)
            holder.binding.tvTitle.text = movie.title
            if (movie.releaseDate != null && movie.releaseDate.length > 4) {
                holder.binding.tvReleaseDate.text = movie.releaseDate.subSequence(0, 4)
            }
            holder.binding.tvVoteAverage.text = movie.voteAverage.toString().substring(0,3)
            holder.binding.tvOverview.text = movie.overview

            setOnClickListener {
                onItemClickListener?.let { it(movie) }
            }
        }
    }

    fun setOnItemClickListener(listener: (MovieBrief) -> Unit) {
        onItemClickListener = listener
    }
}