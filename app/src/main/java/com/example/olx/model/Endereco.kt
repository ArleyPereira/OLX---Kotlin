package com.example.olx.model

import com.example.olx.helper.GetFirebase

data class Endereco (
    var id: String = "",
    var cep: String = "",
    var uf: String = "",
    var localidade: String = "",
    var bairro: String = ""
){

    init {
        this.id = GetFirebase.getDatabase().push().key.toString()
    }

    fun salvar(idUsuario: String){
        val firebaseRef = GetFirebase.getDatabase()
            .child("enderecos")
            .child(idUsuario)
        firebaseRef.setValue(this)
    }
}