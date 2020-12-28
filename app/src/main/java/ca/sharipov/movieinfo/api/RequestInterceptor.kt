package ca.sharipov.movieinfo.api

import ca.sharipov.movieinfo.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("language", Locale.getDefault().language)
            .addQueryParameter("api_key", BuildConfig.API_KEY)
            .build()
        val request = original.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}