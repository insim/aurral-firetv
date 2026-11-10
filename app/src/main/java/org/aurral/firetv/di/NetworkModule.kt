package org.aurral.firetv.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.interceptor.PLACEHOLDER_BASE_URL
import org.aurral.firetv.data.api.interceptor.SubsonicRequestInterceptor
import org.aurral.firetv.data.session.CredentialsProvider
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(credentialsProvider: CredentialsProvider): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(SubsonicRequestInterceptor(credentialsProvider))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(PLACEHOLDER_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideSubsonicApi(retrofit: Retrofit): SubsonicApi = retrofit.create(SubsonicApi::class.java)
}
