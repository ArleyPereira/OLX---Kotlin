package com.example.olx.model

import com.example.olx.helper.FirebaseHelper

data class Favorito(
    var favoritos: MutableList<String> = mutableListOf()
) {

    public fun salvar() {
        val favoritosRef = FirebaseHelper.getDatabase()
            .child("favoritos")
            .child(FirebaseHelper.getIdUser())
        favoritosRef.setValue(this.favoritos)
    }

}