package ca.sharipov.movieinfo.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "movies"
)
data class Movie(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val title: String?,
    val overview: String?,
    val releaseDate: String?,
    val genres: String?,
    val adult: String?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val budget: String?,
    val homepage: String?,
    val revenue: String?,
    val runtime: String?,
    val status: String?,
    val tagline: String?,
    val posterPath: String?,
    @SerializedName("ef")
    val backdropPath: String?,
    val imdbId: String?,
    val video: String?,
    val voteAverage: String?,
    val voteCount: String?,
    val popularity: String?
) : Serializable