package com.example.olx.model

import android.util.Log
import com.example.olx.helper.GetFirebase
import com.google.firebase.database.ServerValue
import java.io.Serializable

data class Anuncio(
    var id: String = "",
    var titulo: String = "",
    var preco: Double = 0.0,
    var categoria: String = "",
    var descricao: String = "",
    var local: Local = Local(),
    var dataCadastro: Long = 0,
    var urlFotos: MutableList<String> = mutableListOf()
) : Serializable {

    fun remover() {
        val anuncioPublicosRef = GetFirebase.getDatabase()
            .child("anunciosPublicos")
            .child(this.id)
        anuncioPublicosRef.removeValue()

        val meusAnunciosRef = GetFirebase.getDatabase()
            .child("meusAnuncios")
            .child(GetFirebase.getIdFirebase())
            .child(this.id)
        meusAnunciosRef.removeValue()

        for (imagem in this.urlFotos.indices){
            val imagemAnuncio = GetFirebase.getStorage()
                .child("imagens")
                .child("anuncios")
                .child(this.id)
                .child("imagem$imagem.jpeg")
            imagemAnuncio.delete()
        }

    }

    fun salvar(novoAnuncio: Boolean) {

        Log.i("INFOTESTE", "salvar: $novoAnuncio")

        val anuncioPublicosRef = GetFirebase.getDatabase()
            .child("anunciosPublicos")
            .child(id)
        anuncioPublicosRef.setValue(this)

        val meusAnunciosRef = GetFirebase.getDatabase()
            .child("meusAnuncios")
            .child(GetFirebase.getIdFirebase())
            .child(id)
        meusAnunciosRef.setValue(this)

        if(novoAnuncio){
            val dataAnuncioPublico = anuncioPublicosRef
                .child("dataCadastro")
            dataAnuncioPublico.setValue(ServerValue.TIMESTAMP)

            val dataMeuAnuncio = meusAnunciosRef
                .child("dataCadastro")
            dataMeuAnuncio.setValue(ServerValue.TIMESTAMP)
        }

    }

}