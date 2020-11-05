package io.muhwyndham.temanbusreloaded.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
object ApiModules {

    @Provides
    fun provideTemanBusApi(retrofit: Retrofit) : TemanBusApi {
        return retrofit.create(TemanBusApi::class.java)
    }

    @Provides
    fun retrofit() : Retrofit = Retrofit.Builder()
        .baseUrl("https://backend.btskemenhub.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}