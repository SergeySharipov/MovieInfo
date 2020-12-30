package ca.sharipov.serhii.movieinfo.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "movies"
)
data class Movie(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("title")
    val title: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("genres")
    val genres: List<Genre>?,
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("original_title")
    val originalTitle: String?,
    @SerializedName("budget")
    val budget: Int?,
    @SerializedName("homepage")
    val homepage: String?,
    @SerializedName("revenue")
    val revenue: Int?,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tagline")
    val tagline: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("imdb_id")
    val imdbId: String?,
    @SerializedName("video")
    val video: Boolean?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?,
    @SerializedName("popularity")
    val popularity: Double?,
) : Serializable