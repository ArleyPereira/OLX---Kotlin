package com.example.olx.model

import android.os.Parcelable
import com.example.olx.helper.FirebaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.techiness.progressdialoglibrary.ProgressDialog
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
        publicPostsRef.setValue(this)

        val datePublicPosts = publicPostsRef
            .child("dataCadastro")
        datePublicPosts.setValue(ServerValue.TIMESTAMP).addOnCompleteListener {

            FirebaseHelper.getDatabase()
                .child("publicPosts")
                .child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val post = snapshot.getValue(Post::class.java) as Post

                        val myPostsRef = FirebaseHelper.getDatabase()
                            .child("myPosts")
                            .child(FirebaseHelper.getIdUser())
                            .child(id)
                        myPostsRef.setValue(post)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }

    }

    fun update() {
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