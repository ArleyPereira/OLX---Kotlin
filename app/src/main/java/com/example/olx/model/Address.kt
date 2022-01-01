package com.example.olx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address (
    var id: String = "",
    var cep: String = "",
    var uf: String = "",
    var localidade: String = "",
    var bairro: String = ""
) : Parcelable