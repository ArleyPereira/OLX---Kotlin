package com.example.olx.model

import android.os.Parcelable
import com.example.olx.helper.FirebaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Post(var id: String = "") : Parcelable {

    var idUsuario: String = ""
    var title: String = ""
    var phone: String = ""
    var price: Double = 0.0
    var category: String = ""
    var description: String = ""
    var address: Address? = null
    var registrationDate: Long = 0
    var urlImages: MutableList<String> = mutableListOf()

    fun remover() {
        val anuncioPublicosRef = FirebaseHelper.getDatabase()
            .child("anunciosPublicos")
            .child(this.id)
        anuncioPublicosRef.removeValue()

        val meusAnunciosRef = FirebaseHelper.getDatabase()
            .child("meusAnuncios")
            .child(FirebaseHelper.getIdUser())
            .child(this.id)
        meusAnunciosRef.removeValue()

        for (imagem in this.urlImages.indices) {
            val imagemAnuncio = FirebaseHelper.getStorage()
                .child("imagens")
                .child("anuncios")
                .child(this.id)
                .child("imagem$imagem.jpeg")
            imagemAnuncio.delete()
        }

    }

    fun salvar() {
        val anuncioPublicosRef = FirebaseHelper.getDatabase()
            .child("anunciosPublicos")
            .child(id)
        anuncioPublicosRef.setValue(this)

        val dataAnuncioPublico = anuncioPublicosRef
            .child("dataCadastro")
        dataAnuncioPublico.setValue(ServerValue.TIMESTAMP).addOnCompleteListener {

            val anuncioRef = FirebaseHelper.getDatabase()
                .child("anunciosPublicos")
                .child(id)
            anuncioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val anuncio = snapshot.getValue(Post::class.java) as Post

                    val meusAnunciosRef = FirebaseHelper.getDatabase()
                        .child("meusAnuncios")
                        .child(FirebaseHelper.getIdUser())
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
        val anuncioPublicosRef = FirebaseHelper.getDatabase()
            .child("anunciosPublicos")
            .child(id)
        anuncioPublicosRef.setValue(this)

        val meusAnunciosRef = FirebaseHelper.getDatabase()
            .child("meusAnuncios")
            .child(FirebaseHelper.getIdUser())
            .child(id)
        meusAnunciosRef.setValue(this)
    }

}