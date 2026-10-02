package com.example.data.remote

import com.example.data.local.BookingEntity
import com.example.data.local.ServiceEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/v1/services")
    suspend fun getServices(
        @Query("block") block: String? = null
    ): Response<List<ServiceEntity>>

    @POST("api/v1/bookings")
    suspend fun createBooking(
        @Body booking: BookingEntity
    ): Response<BookingEntity>

    @GET("api/v1/bookings/{id}")
    suspend fun getBookingStatus(
        @Path("id") bookingId: Long
    ): Response<BookingEntity>

    companion object {
        private const val BASE_URL = "https://api.localservicehub.in/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
