package com.example.olx.model

import com.example.olx.helper.GetFirebase

data class Favorito(
    var favoritos: MutableList<String> = mutableListOf()
) {

    public fun salvar() {
        val favoritosRef = GetFirebase.getDatabase()
            .child("favoritos")
            .child(GetFirebase.getIdFirebase())
        favoritosRef.setValue(this.favoritos)
    }

}