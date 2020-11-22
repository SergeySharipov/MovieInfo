package ca.sharipov.movieinfo.ui

import ca.sharipov.movieinfo.api.RetrofitInstance

class MoviesRepository() {
    suspend fun getMovie(movieId: Int) =
        RetrofitInstance.api.getMovie(movieId)

    suspend fun getPopularMovies(pageNumber: Int) =
        RetrofitInstance.api.getPopularMovies(pageNumber)

    suspend fun searchMovieByName(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchMovieByName(searchQuery, pageNumber)
}