package com.example.olx.model

import com.google.firebase.database.Exclude

class Usuario(
    var id: String = "",
    var nome: String = "",
    var email: String = "",
    var endereco: Endereco? = null,
    @get:Exclude
    var senha: String = "",
    var telefone: String = "",
    var urlImagem: String = ""
)