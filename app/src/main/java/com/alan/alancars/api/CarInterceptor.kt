package com.alan.alancars.api

import okhttp3.Interceptor
import okhttp3.Response

class CarInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-Api-Key", "kdyWbr2CXZPUYHJJP14/YQ==bQgP3cslUE5XzmVr")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}