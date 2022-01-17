package com.example.olx.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    @SerializedName("cep")
    var zipCode: String = "",

    @SerializedName("uf")
    var state: String = "",

    @SerializedName("localidade")
    var city: String = "",

    @SerializedName("bairro")
    var district: String = "",

    @SerializedName("ddd")
    var ddd: String = ""
) : Parcelable {
    fun save() {

    }
}