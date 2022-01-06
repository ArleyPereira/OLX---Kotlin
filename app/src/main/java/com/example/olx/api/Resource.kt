package com.example.olx.api

sealed class Resource<T>(val data: T? = null) {
    class onSuccess<T>(data: T) : Resource<T>(data)
    class onFailure<T> : Resource<T>()
}