package com.example.olx.util

import com.example.olx.R
import com.example.olx.model.Categoria

class CategoriaList {

    companion object {

        fun getList(todas: Boolean): List<Categoria> {

            val categoriaList = mutableListOf<Categoria>()

            if(todas) categoriaList.add(Categoria(R.drawable.ic_todas_as_categorias, "Todas as Categorias"))
            categoriaList.add(Categoria(R.drawable.ic_autos_e_pecas, "Autos e peças"))
            categoriaList.add(Categoria(R.drawable.ic_imoveis, "Imóveis"))
            categoriaList.add(Categoria(R.drawable.ic_eletronico_e_celulares, "Eletrônicos e celulares"))
            categoriaList.add(Categoria(R.drawable.ic_para_a_sua_casa, "Para a sua casa"))
            categoriaList.add(Categoria(R.drawable.ic_moda_e_beleza, "Moda e beleza"))
            categoriaList.add(Categoria(R.drawable.ic_esporte_e_lazer, "Esportes e lazer"))
            categoriaList.add(Categoria(R.drawable.ic_musica_e_hobbies, "Músicas e hobbies"))
            categoriaList.add(Categoria(R.drawable.ic_artigos_infantis, "Artigos infantis"))
            categoriaList.add(Categoria(R.drawable.ic_animais_de_estimacao, "Animais de estimação"))
            categoriaList.add(Categoria(R.drawable.ic_agro_e_industria, "Agro e indústria"))
            categoriaList.add(Categoria(R.drawable.ic_comercio_e_escritorio, "Comércio e escritório"))
            categoriaList.add(Categoria(R.drawable.ic_servicos, "Serviços"))
            categoriaList.add(Categoria(R.drawable.ic_vagas_de_emprego, "Vagas de emprego"))

            return categoriaList
        }

    }

}