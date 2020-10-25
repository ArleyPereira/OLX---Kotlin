package com.example.olx.model

import java.io.Serializable

data class Local (
    var cep: String = "",
    var uf: String = "",
    var localidade: String = "",
    var bairro: String = "",
    var ddd: String = ""
) : Serializable