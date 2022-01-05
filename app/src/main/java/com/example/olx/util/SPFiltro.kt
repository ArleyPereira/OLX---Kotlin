package com.example.olx.util

import android.app.Activity
import android.content.SharedPreferences
import com.example.olx.model.Province
import com.example.olx.model.Filter

class SPFiltro {

    companion object {

        const val ARQUIVO_PREFERENCIA = "ArquivoPreferencia"

        // Salva Filtro em SharedPreferences
        fun setFiltro(activity: Activity, chave: String, valor: String){
            val preferences = activity.getSharedPreferences(ARQUIVO_PREFERENCIA, 0)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString(chave, valor)
            editor.apply()
        }

        // Recupera Filtro em SharedPreferences
        fun getFiltro(activity: Activity): Filter {

            val preferences = activity.getSharedPreferences(ARQUIVO_PREFERENCIA, 0)

            val pesquisa = preferences.getString("pesquisa", "")
            val ufEstado = preferences.getString("ufEstado", "")
            val categoria = preferences.getString("categoria", "")
            val nomeEstado = preferences.getString("nomeEstado", "")
            val regiao = preferences.getString("regiao", "")
            val ddd = preferences.getString("ddd", "")

            val province = Province(
                uf = ufEstado!!,
                region = regiao!!,
                name = nomeEstado!!,
                ddd = ddd!!
            )

            val filter = Filter(
                province = province,
                category = categoria!!,
                search = pesquisa!!,
            )

            preferences.getString("valorMin", "")?.let {
                if(it.isNotBlank()) filter.valueMin = it.toInt()
            }

            preferences.getString("valorMax", "")?.let {
                if(it.isNotBlank()) filter.valueMax = it.toInt()
            }

            return filter
        }

        // Limpa todos os Filtros em SharedPreferences
        fun limpaFiltros(activity: Activity?) {
            setFiltro(activity!!, "pesquisa", "")
            setFiltro(activity, "nomeEstado", "")
            setFiltro(activity, "ufEstado", "")
            setFiltro(activity, "categoria", "")
            setFiltro(activity, "regiao", "")
            setFiltro(activity, "ddd", "")
            setFiltro(activity, "valorMin", "")
            setFiltro(activity, "valorMax", "")
        }

    }

}