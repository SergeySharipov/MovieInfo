package ca.sharipov.movieinfo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.sharipov.movieinfo.models.MovieBrief

@Database(
    entities = [MovieBrief::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MovieBriefDatabase : RoomDatabase() {

    abstract fun getMovieBriefDao(): MovieBriefDao

    companion object {
        @Volatile
        private var instance: MovieBriefDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MovieBriefDatabase::class.java,
                "movies.db"
            ).build()
    }
}