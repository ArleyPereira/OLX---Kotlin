package com.example.olx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter(
    val state: State,
    val category: String = "",
    val search: String = "",
    val valueMin: Float = 0f,
    val valueMax: Float = 0f
): Parcelable