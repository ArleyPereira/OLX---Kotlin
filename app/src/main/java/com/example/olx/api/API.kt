package com.example.olx.api

import com.example.olx.model.Address
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface API {

    @GET("{zipCode}/json/")
    suspend fun getAddress(
        @Path("zipCode") zipCode: String
    ): Address

    companion object {
        fun initRetrofit(): API {
            val retrofit = Retrofit
                .Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(API::class.java)
        }
    }

}