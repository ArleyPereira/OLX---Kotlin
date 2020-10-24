package com.example.olx.api

import com.example.olx.model.Local
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CEPService {

    @GET("{cep}/json/")
    fun recuperarCEP(@Path("cep") cep: String): Call<Local>

}