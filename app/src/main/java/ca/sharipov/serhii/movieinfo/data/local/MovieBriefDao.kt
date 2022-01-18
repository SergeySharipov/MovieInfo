package ca.sharipov.serhii.movieinfo.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import ca.sharipov.serhii.movieinfo.data.models.MovieBrief

@Dao
interface MovieBriefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieBrief(movieBrief: MovieBrief): Long

    @Query("SELECT * FROM movie_briefs")
    fun observeAllMovieBriefs(): LiveData<List<MovieBrief>>

    @Delete
    suspend fun deleteMovieBrief(article: MovieBrief)

    @Query("SELECT * from movie_briefs where id=:id")
    fun observeMovieBrief(id: Int): LiveData<MovieBrief>
}