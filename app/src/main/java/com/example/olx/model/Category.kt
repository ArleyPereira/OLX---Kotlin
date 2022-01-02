package com.example.olx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category (
    var image: Int,
    var name: String
) : Parcelable