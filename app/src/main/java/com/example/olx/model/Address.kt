package com.example.olx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address (
    val id: String? = null,
    val cep: String,
    val uf: String,
    val localidade: String,
    val bairro: String
) : Parcelable