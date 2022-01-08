package com.example.olx.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var state: State? = null,
    @get:Exclude
    var password: String = "",
    var phone: String = ""
) : Parcelable