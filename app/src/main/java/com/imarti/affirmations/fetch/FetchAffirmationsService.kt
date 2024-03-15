package com.imarti.affirmations.fetch

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


private const val BASE_URL =
    "https://karan31q.pythonanywhere.com"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface FetchAffirmationsService {
    @GET("affirmations")
    suspend fun getAffirmation(): String
}

object AffirmationsApi {
    val retrofitService: FetchAffirmationsService by lazy {
        retrofit.create(FetchAffirmationsService::class.java)
    }

}