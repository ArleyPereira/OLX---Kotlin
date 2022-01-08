package com.example.olx.model

import android.os.Parcelable
import com.example.olx.helper.FirebaseHelper
import com.google.firebase.database.ServerValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    var id: String = "",
    var idUser: String = "",
    var title: String = "",
    var phone: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var description: String = "",
    var state: State? = null,
    var registrationDate: Long = 0,
    var urlImages: MutableList<String> = mutableListOf()
) : Parcelable {

    fun remove() {
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

    fun save() {
        val publicPostsRef = FirebaseHelper.getDatabase()
            .child("publicPosts")
            .child(id)
        publicPostsRef.setValue(this).addOnCompleteListener {
            val datePublicPosts = publicPostsRef
                .child("registrationDate")
            datePublicPosts.setValue(ServerValue.TIMESTAMP)
        }

        val myPostsRef = FirebaseHelper.getDatabase()
            .child("myPosts")
            .child(FirebaseHelper.getIdUser())
            .child(id)
        myPostsRef.setValue(this).addOnCompleteListener {
            val dateMyPosts = myPostsRef
                .child("registrationDate")
            dateMyPosts.setValue(ServerValue.TIMESTAMP)
        }
    }

    fun update() {
        val publicPostsRef = FirebaseHelper.getDatabase()
            .child("publicPosts")
            .child(id)
        publicPostsRef.setValue(this)

        val myPostsRef = FirebaseHelper.getDatabase()
            .child("myPosts")
            .child(FirebaseHelper.getIdUser())
            .child(id)
        myPostsRef.setValue(this)
    }
}