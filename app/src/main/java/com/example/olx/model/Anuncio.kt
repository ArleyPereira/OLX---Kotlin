package com.example.olx.model

import com.example.olx.helper.GetFirebase

class Anuncio(
    var id: String = "",
    var titulo: String = "",
    var preco: Double = 0.0,
    var categoria: String = "",
    var descricao: String = "",
    var local: Local,
    var urlFotos: MutableList<String> = mutableListOf()
) {

    init {
        this.id = GetFirebase.getDatabase().push().key.toString()
    }

}