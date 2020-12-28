package ca.sharipov.movieinfo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.sharipov.movieinfo.models.MovieBrief
import ca.sharipov.movieinfo.util.Constants.Companion.DATABASE_NAME

@Database(
    entities = [MovieBrief::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoviesDatabase : RoomDatabase() {

    abstract fun movieBriefDao(): MovieBriefDao
}