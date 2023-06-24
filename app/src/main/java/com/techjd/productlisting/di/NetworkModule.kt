package com.techjd.productlisting.di

import com.techjd.productlisting.api.ProductsApi
import com.techjd.productlisting.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit.Builder {
      return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
    }

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
      val loggingInterceptor = HttpLoggingInterceptor()
      loggingInterceptor.setLevel(Level.BODY)
      return loggingInterceptor
    }

  @Provides
  @Singleton
  fun providesProductsApi(retrofitBuilder: Retrofit.Builder, loggingInterceptor: HttpLoggingInterceptor): ProductsApi {
    val client: OkHttpClient = OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .build()
    return retrofitBuilder.client(client).build().create(ProductsApi::class.java)
  }

}