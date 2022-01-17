package ca.sharipov.serhii.movieinfo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.sharipov.serhii.movieinfo.data.models.MovieBrief

@Database(
    entities = [MovieBrief::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoviesDatabase : RoomDatabase() {

    abstract fun movieBriefDao(): MovieBriefDao
}