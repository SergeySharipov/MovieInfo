package ca.sharipov.movieinfo.api

import ca.sharipov.movieinfo.util.Constants
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("language", Constants.APP_LANGUAGE)
            .addQueryParameter("api_key", Constants.API_KEY)
            .build()
        val request = original.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}