package com.example.olx.model

import android.util.Log
import android.view.View
import com.example.olx.helper.GetFirebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_meus_anuncios.*
import java.io.Serializable

data class Anuncio(var id: String = "") : Serializable {

    var idUsuario: String = ""
    var titulo: String = ""
    var preco: Double = 0.0
    var categoria: String = ""
    var descricao: String = ""
    var local: Local = Local()
    var dataCadastro: Long = 0
    var urlFotos: MutableList<String> = mutableListOf()

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

        for (imagem in this.urlFotos.indices) {
            val imagemAnuncio = GetFirebase.getStorage()
                .child("imagens")
                .child("anuncios")
                .child(this.id)
                .child("imagem$imagem.jpeg")
            imagemAnuncio.delete()
        }

    }

    fun salvar() {
        val anuncioPublicosRef = GetFirebase.getDatabase()
            .child("anunciosPublicos")
            .child(id)
        anuncioPublicosRef.setValue(this)

        val dataAnuncioPublico = anuncioPublicosRef
            .child("dataCadastro")
        dataAnuncioPublico.setValue(ServerValue.TIMESTAMP).addOnCompleteListener {

            val anuncioRef = GetFirebase.getDatabase()
                .child("anunciosPublicos")
                .child(id)
            anuncioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val anuncio = snapshot.getValue(Anuncio::class.java) as Anuncio

                    val meusAnunciosRef = GetFirebase.getDatabase()
                        .child("meusAnuncios")
                        .child(GetFirebase.getIdFirebase())
                        .child(id)
                    meusAnunciosRef.setValue(anuncio)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

    }

    fun editar() {
        val anuncioPublicosRef = GetFirebase.getDatabase()
            .child("anunciosPublicos")
            .child(id)
        anuncioPublicosRef.setValue(this)

        val meusAnunciosRef = GetFirebase.getDatabase()
            .child("meusAnuncios")
            .child(GetFirebase.getIdFirebase())
            .child(id)
        meusAnunciosRef.setValue(this)
    }

}