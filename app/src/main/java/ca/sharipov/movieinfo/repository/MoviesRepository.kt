package ca.sharipov.movieinfo.repository

import ca.sharipov.movieinfo.api.RetrofitInstance
import ca.sharipov.movieinfo.db.MovieBriefDatabase
import ca.sharipov.movieinfo.models.MovieBrief

class MoviesRepository(
    val db: MovieBriefDatabase
) {
    suspend fun getMovie(movieId: Int) =
        RetrofitInstance.api.getMovie(movieId)

    suspend fun getPopularMovies(pageNumber: Int) =
        RetrofitInstance.api.getPopularMovies(pageNumber)

    suspend fun searchMovieByName(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchMovieByName(searchQuery, pageNumber)

    suspend fun upsert(movieBrief: MovieBrief) = db.getMovieBriefDao().upsert(movieBrief)

    fun getAllMovieBriefs() = db.getMovieBriefDao().getAllMovieBriefs()

    suspend fun deleteMovieBrief(movieBrief: MovieBrief) =
        db.getMovieBriefDao().deleteMovieBrief(movieBrief)
}