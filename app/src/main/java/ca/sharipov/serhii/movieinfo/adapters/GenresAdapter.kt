package ca.sharipov.serhii.movieinfo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.serhii.movieinfo.databinding.ItemGenreBinding
import ca.sharipov.serhii.movieinfo.models.Genre

class GenresAdapter(private var genres: List<Genre>) :
    RecyclerView.Adapter<GenresAdapter.GenreViewHolder>() {

    inner class GenreViewHolder(val binding: ItemGenreBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGenreBinding.inflate(layoutInflater, parent, false)

        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        holder.itemView.apply {
            holder.binding.tvGenre.text = genre.name
        }
    }

    override fun getItemCount(): Int {
        return genres.size
    }
}