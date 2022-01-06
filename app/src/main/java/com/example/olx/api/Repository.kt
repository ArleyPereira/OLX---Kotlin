package com.example.olx.api

class Repository {

    private val api = API.initRetrofit()

    suspend fun getAddress(zipCode: String) = api.getAddress(zipCode)
}