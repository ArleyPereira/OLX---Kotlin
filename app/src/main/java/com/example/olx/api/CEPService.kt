package com.example.olx.api

import android.location.Address
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CEPService {

    @GET("{cep}/json/")
    fun recuperarCEP(@Path("cep") cep: String): Call<Address>

}