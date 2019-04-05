package com.willkernel.kotlinapp.net

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.willkernel.kotlinapp.Constants
import com.willkernel.kotlinapp.MyApplication
import com.willkernel.kotlinapp.api.ApiService
import com.willkernel.kotlinapp.utils.AppUtils
import com.willkernel.kotlinapp.utils.NetworkUtils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object RetrofitManager {
    val service: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(getOkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val cacheFile = File(MyApplication.context.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50)

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(getQueryParameterInterceptor())
            .addInterceptor(getHeaderInterceptor())
            .cache(cache)
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private var token: String by com.willkernel.kotlinapp.utils.Preference("token", "")

    private fun getHeaderInterceptor(): Interceptor {
        return Interceptor {
            val originalRequest = it.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("token", token)
                .method(originalRequest.method(), originalRequest.body())
            val request = requestBuilder.build()
            it.proceed(request)
        }
    }

    private fun getQueryParameterInterceptor(): Interceptor {
        return Interceptor {
            val originalRequest = it.request()
            val request: Request
            val modifierUrl = originalRequest.url().newBuilder()
                .addQueryParameter("udid", "d2807c895f0348a180148c9dfa6f2feeac0781b5")
                .addQueryParameter("deviceModel", AppUtils.getModel())
                .build()
            request = originalRequest.newBuilder().url(modifierUrl).build()
            it.proceed(request)
        }
    }

    private fun getCacheInterceptro(): Interceptor {
        return Interceptor {
            var request = it.request()
            if (!NetworkUtils.isNetworkAvailable()) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE).build()
            }
            val response = it.proceed(request)

            if (NetworkUtils.isNetworkAvailable()) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                response.newBuilder()
                    .header("Cache-Control", "public,max-age=" + maxAge)
                    // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .removeHeader("Retrofit")
                    .build()
            } else {
                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("nyn")
                    .build()
            }
            response

        }
    }
}