package ca.sharipov.movieinfo.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "genres"
)
data class Genre(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val name: String?
) : Serializable