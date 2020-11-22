package ca.sharipov.movieinfo.models

import ca.sharipov.movieinfo.models.MovieBrief
import com.google.gson.annotations.SerializedName


data class MovieBriefsResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("results")
    val results: MutableList<MovieBrief>
)