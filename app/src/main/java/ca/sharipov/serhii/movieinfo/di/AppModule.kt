package ca.sharipov.serhii.movieinfo.di

import android.content.Context
import android.os.Environment
import androidx.room.Room
import ca.sharipov.serhii.movieinfo.data.DefaultMoviesRepository
import ca.sharipov.serhii.movieinfo.data.MoviesRepository
import ca.sharipov.serhii.movieinfo.data.local.MovieBriefDao
import ca.sharipov.serhii.movieinfo.data.local.MoviesDatabase
import ca.sharipov.serhii.movieinfo.data.remote.RequestInterceptor
import ca.sharipov.serhii.movieinfo.data.remote.TmdbAPI
import ca.sharipov.serhii.movieinfo.utils.Constants
import ca.sharipov.serhii.movieinfo.utils.Constants.Companion.DATABASE_NAME
import ca.sharipov.serhii.movieinfo.utils.InternetConnectionUtil
import com.bumptech.glide.Glide
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMoviesRepository(
        dao: MovieBriefDao,
        api: TmdbAPI
    ) = DefaultMoviesRepository(dao, api) as MoviesRepository

    @Singleton
    @Provides
    fun provideMovieBriefDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, MoviesDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideMovieBriefDao(
        database: MoviesDatabase
    ) = database.movieBriefDao()

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context)

    @Singleton
    @Provides
    fun provideInternetConnectionUtil(
        @ApplicationContext context: Context
    ) = InternetConnectionUtil(context)

    @Singleton
    @Provides
    fun provideTmdbApi(client: OkHttpClient): TmdbAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TmdbAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(cache: Cache, logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(RequestInterceptor())
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideCache(): Cache {
        val cacheSize: Long = 20 * 1024 * 1024 // 20 MB
        return Cache(Environment.getDownloadCacheDirectory(), cacheSize)
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    }
}
