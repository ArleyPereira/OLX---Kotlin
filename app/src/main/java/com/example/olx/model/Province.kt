package com.example.olx.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Province (
    var uf: String = "",
    var region: String = "",
    var name: String = "",
    var ddd: String = ""
): Parcelable