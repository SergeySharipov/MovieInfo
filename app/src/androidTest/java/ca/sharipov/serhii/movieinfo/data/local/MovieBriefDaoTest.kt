package ca.sharipov.serhii.movieinfo.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import ca.sharipov.serhii.movieinfo.data.models.MovieBrief
import ca.sharipov.serhii.movieinfo.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class MovieBriefDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: MoviesDatabase

    private lateinit var dao: MovieBriefDao
    private val testMovieBrief: MovieBrief = MovieBrief(
        155,
        "The Dark Knight",
        "Batman raises the stakes in his war on crime. With the help of Lt. " +
                "Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle " +
                "the remaining criminal organizations that plague the streets.",
        "2008-07-14",
        listOf(18, 28, 80, 53),
        false,
        "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
        "/nMKdUUepR0i5zn0y1T4CsSB5chy.jpg",
        false,
        8.5,
        26643,
        "The Dark Knight",
        "en",
        85.878
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        dao = database.movieBriefDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllMovieBriefs() = runBlockingTest {
        dao.upsertMovieBrief(testMovieBrief)

        val allMovieBriefs = dao.observeAllMovieBriefs().getOrAwaitValue()

        assertThat(allMovieBriefs.contains(testMovieBrief))
    }

    @Test
    fun getMovieBrief() = runBlockingTest {
        val id = dao.upsertMovieBrief(testMovieBrief)

        val movieBriefFromDb = dao.observeMovieBrief(id.toInt()).getOrAwaitValue()

        assertThat(movieBriefFromDb == testMovieBrief)
    }

    @Test
    fun deleteMovieBrief() = runBlockingTest {
        dao.upsertMovieBrief(testMovieBrief)
        dao.deleteMovieBrief(testMovieBrief)

        val allShoppingItems = dao.observeAllMovieBriefs().getOrAwaitValue()

        assertThat(allShoppingItems).doesNotContain(testMovieBrief)
    }
}