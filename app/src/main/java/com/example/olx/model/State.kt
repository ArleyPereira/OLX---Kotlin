package com.example.olx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class State (
    val uf: String = "",
    val region: String = "",
    val name: String = "",
    val ddd: String = ""
): Parcelable