package com.tontowi0086.assesment3.network

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tontowi0086.assesment3.model.Barang
import com.tontowi0086.assesment3.model.LoginResponse
import com.tontowi0086.assesment3.model.OpStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

// GANTI DENGAN URL API RAILWAY ANDA!
private const val BASE_URL = "https://barangapi-production.up.railway.app/"

interface BarangApiService {
    @POST("auth/google/app-login")
    suspend fun login(@Body body: Map<String, String>): LoginResponse

    @GET("barang")
    suspend fun getBarang(): List<Barang>

    @Multipart
    @POST("barang")
    suspend fun addBarang(
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part gambar: MultipartBody.Part
    ): Barang

    @Multipart
    @PUT("barang/{id}")
    suspend fun updateBarang(
        @Path("id") id: Long,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part gambar: MultipartBody.Part? = null
    ): Barang

    @DELETE("barang/{id}")
    suspend fun deleteBarang(@Path("id") id: Long): OpStatus

}

class AuthInterceptor(context: Context) : Interceptor {
    private val dataStore = UserDataStore(context)

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = runBlocking { dataStore.authToken.first() }
        val requestBuilder = chain.request().newBuilder()
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(requestBuilder.build())
    }
}

object BarangApi {
    private var instance: BarangApiService? = null

    fun getInstance(context: Context): BarangApiService {
        if (instance == null) {
            val authInterceptor = AuthInterceptor(context)
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            instance = retrofit.create(BarangApiService::class.java)
        }
        return instance!!
    }

    fun getBaseUrl(): String {
        return BASE_URL
    }
}