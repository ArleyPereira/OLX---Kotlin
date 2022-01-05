package com.example.olx.model

class Filtro(
    var estado: Estado,
    var categoria: String = "",
    var pesquisa: String = "",
    var valorMin: Int = 0,
    var valorMax: Int = 0
)