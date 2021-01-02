package ca.sharipov.serhii.movieinfo.repository

import android.content.Context
import ca.sharipov.serhii.movieinfo.api.TmdbAPI
import ca.sharipov.serhii.movieinfo.db.MovieBriefDao
import ca.sharipov.serhii.movieinfo.models.MovieBrief
import javax.inject.Inject

class DefaultMoviesRepository @Inject constructor(
    private val movieBriefDao: MovieBriefDao,
    private val tmdbAPI: TmdbAPI
) : MoviesRepository {

    override suspend fun getMovie(movieId: Int) =
        tmdbAPI.getMovie(movieId)

    override suspend fun getPopularMovies(pageNumber: Int) =
        tmdbAPI.getPopularMovies(pageNumber)

    override suspend fun searchMovieByName(searchQuery: String, pageNumber: Int) =
        tmdbAPI.searchMovieByName(searchQuery, pageNumber)

    override suspend fun getSimilarMovies(movieId: Int) =
        tmdbAPI.getSimilarMovies(movieId)

    override suspend fun upsert(movieBrief: MovieBrief) = movieBriefDao.upsert(movieBrief)

    override fun getAllMovieBriefs() = movieBriefDao.getAllMovieBriefs()

    override suspend fun deleteMovieBrief(movieBrief: MovieBrief) =
        movieBriefDao.deleteMovieBrief(movieBrief)

    override fun getMovieBrief(id: Int) =
        movieBriefDao.getMovieBrief(id)
}