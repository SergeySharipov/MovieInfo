package ca.sharipov.serhii.movieinfo.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ca.sharipov.serhii.movieinfo.models.MovieBrief

@Dao
interface MovieBriefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: MovieBrief): Long

    @Query("SELECT * FROM movie_briefs")
    fun getAllMovieBriefs(): LiveData<List<MovieBrief>>

    @Delete
    suspend fun deleteMovieBrief(article: MovieBrief)

    @Query("SELECT * from movie_briefs where id=:id")
    fun getMovieBrief(id: Int): LiveData<MovieBrief>
}