package com.example.olx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Filter(
    var province: Province,
    var category: String = "",
    var search: String = "",
    var valueMin: Int = 0,
    var valueMax: Int = 0
): Parcelable