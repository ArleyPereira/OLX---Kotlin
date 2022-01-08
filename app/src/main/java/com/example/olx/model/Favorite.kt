package com.example.olx.model

import com.example.olx.helper.FirebaseHelper

data class Favorite(
    var favoritos: MutableList<String> = mutableListOf()
) {

    fun salvar() {
        val favoritosRef = FirebaseHelper.getDatabase()
            .child("favorites")
            .child(FirebaseHelper.getIdUser())
        favoritosRef.setValue(this.favoritos)
    }

}