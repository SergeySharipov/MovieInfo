package ca.sharipov.serhii.movieinfo.data

import ca.sharipov.serhii.movieinfo.data.local.MovieBriefDao
import ca.sharipov.serhii.movieinfo.data.models.MovieBrief
import ca.sharipov.serhii.movieinfo.data.remote.TmdbAPI
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

    override suspend fun upsertMovieBrief(movieBrief: MovieBrief) =
        movieBriefDao.upsertMovieBrief(movieBrief)

    override fun observeAllMovieBriefs() = movieBriefDao.observeAllMovieBriefs()

    override suspend fun deleteMovieBrief(movieBrief: MovieBrief) =
        movieBriefDao.deleteMovieBrief(movieBrief)

    override fun observeMovieBrief(id: Int) =
        movieBriefDao.observeMovieBrief(id)
}