package com.water.alkaline.kengen.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.preference.PowerPreference
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.api.retroAPI
import com.water.alkaline.kengen.data.db.AppDB
import com.water.alkaline.kengen.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {


    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1
        return OkHttpClient.Builder()
            .addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                    if (Constant.isVpnConnected()) {
                        Response.Builder()
                            .code(404)
                            .body(ResponseBody.create(null, ""))
                            .protocol(Protocol.HTTP_2)
                            .message("not-verified").request(chain.request())
                            .build()
                    } else {
                        val original = chain.request()
                        val requestBuilder = original.newBuilder()
                            .method(original.method, original.body)

                        val request = requestBuilder.build()
                        chain.proceed(request)
                    }
                }
            ).connectTimeout(20, TimeUnit.SECONDS).dispatcher(dispatcher).build()
    }

    @Singleton
    @Provides
    @Named("Retro")
    fun providesRetrofitClient(
        gson: Gson,
        okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(
                PowerPreference.getDefaultFile()
                    .getString(Constant.apiKey, MyApplication.getSub(context))
            ).addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
    }


    @Singleton
    @Provides
    @Named("You")
    fun providesYoutubeClient(
        gson: Gson,
        okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(MyApplication.getSub(context))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
    }

    @Singleton
    @Provides
    fun providesRetroAPI(@Named("Retro") retrofit: Retrofit.Builder): retroAPI {
        return retrofit.addConverterFactory(GsonConverterFactory.create()).build()
            .create(retroAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesYouAPI(@Named("You") retrofit: Retrofit.Builder): retroAPI {
        return retrofit.addConverterFactory(GsonConverterFactory.create()).build()
            .create(retroAPI::class.java)
    }

}