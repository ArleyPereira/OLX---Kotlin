package com.example.olx.util

import com.example.olx.R
import com.example.olx.model.Category

class CategoriaList {

    companion object {

        fun getList(todas: Boolean): List<Category> {

            val categoriaList = mutableListOf<Category>()

            if(todas) categoriaList.add(Category(R.drawable.ic_todas_as_categorias, "Todas as Categorias"))
            categoriaList.add(Category(R.drawable.ic_autos_e_pecas, "Autos e peças"))
            categoriaList.add(Category(R.drawable.ic_imoveis, "Imóveis"))
            categoriaList.add(Category(R.drawable.ic_eletronico_e_celulares, "Eletrônicos e celulares"))
            categoriaList.add(Category(R.drawable.ic_para_a_sua_casa, "Para a sua casa"))
            categoriaList.add(Category(R.drawable.ic_moda_e_beleza, "Moda e beleza"))
            categoriaList.add(Category(R.drawable.ic_esporte_e_lazer, "Esportes e lazer"))
            categoriaList.add(Category(R.drawable.ic_musica_e_hobbies, "Músicas e hobbies"))
            categoriaList.add(Category(R.drawable.ic_artigos_infantis, "Artigos infantis"))
            categoriaList.add(Category(R.drawable.ic_animais_de_estimacao, "Animais de estimação"))
            categoriaList.add(Category(R.drawable.ic_agro_e_industria, "Agro e indústria"))
            categoriaList.add(Category(R.drawable.ic_comercio_e_escritorio, "Comércio e escritório"))
            categoriaList.add(Category(R.drawable.ic_servicos, "Serviços"))
            categoriaList.add(Category(R.drawable.ic_vagas_de_emprego, "Vagas de emprego"))

            return categoriaList
        }

    }

}