package ca.sharipov.movieinfo.api

import ca.sharipov.movieinfo.models.Movie
import ca.sharipov.movieinfo.models.MovieBriefsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbAPI {

    @GET("3/movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id")
        movieId: Int
    ): Response<Movie>

    @GET("3/movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id")
        movieId: Int
    ): Response<MovieBriefsResponse>

    @GET("3/discover/movie?sort_by=popularity.desc")
    suspend fun getPopularMovies(
        @Query("page")
        page: Int
    ): Response<MovieBriefsResponse>

    @GET("3/search/movie")
    suspend fun searchMovieByName(
        @Query("query")
        searchQuery: String?,
        @Query("page")
        pageNumber: Int = 1
    ): Response<MovieBriefsResponse>

}