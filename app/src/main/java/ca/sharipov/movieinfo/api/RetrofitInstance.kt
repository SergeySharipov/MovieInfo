package ca.sharipov.movieinfo.api

import android.os.Environment
import ca.sharipov.movieinfo.util.Constants.Companion.BASE_URL
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {
    companion object {

        private val retrofit by lazy {
            val cacheSize: Long = 20 * 1024 * 1024 // 20 MB
            val cache = Cache(Environment.getDownloadCacheDirectory(), cacheSize)

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

            val client = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(RequestInterceptor())
                .addInterceptor(logging)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(TmdbAPI::class.java)
        }
    }
}