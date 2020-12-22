package ca.sharipov.movieinfo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import ca.sharipov.movieinfo.R
import ca.sharipov.movieinfo.models.Genre
import kotlinx.android.synthetic.main.item_genre.view.*

class GenresAdapter(private var genres: List<Genre>) :
    RecyclerView.Adapter<GenresAdapter.GenreViewHolder>() {

    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_genre, parent, false)
        return GenreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        holder.itemView.apply {
            tvGenre.text = genre.name
        }
    }

    override fun getItemCount(): Int {
        return genres.size
    }
}