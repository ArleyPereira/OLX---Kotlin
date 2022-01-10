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
    var address: Address? = null,
    var registrationDate: Long = 0,
    var urlImages: MutableList<String> = mutableListOf()
) : Parcelable {

    fun remove() {
        val publicPostsRef = FirebaseHelper.getDatabase()
            .child("publicPosts")
            .child(this.id)
        publicPostsRef.removeValue()

        val myPostsRef = FirebaseHelper.getDatabase()
            .child("myPosts")
            .child(FirebaseHelper.getIdUser())
            .child(this.id)
        myPostsRef.removeValue()

        for (image in this.urlImages.indices) {
            FirebaseHelper.getStorage()
                .child("images")
                .child("posts")
                .child(this.id)
                .child("image$image.jpeg")
                .delete()
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