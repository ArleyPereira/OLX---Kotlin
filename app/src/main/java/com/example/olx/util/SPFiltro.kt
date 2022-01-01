package com.example.olx.util

import android.app.Activity
import android.content.SharedPreferences
import com.example.olx.model.Estado
import com.example.olx.model.Filtro

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
        fun getFiltro(activity: Activity): Filtro {

            val preferences = activity.getSharedPreferences(ARQUIVO_PREFERENCIA, 0)

            val pesquisa = preferences.getString("pesquisa", "")
            val ufEstado = preferences.getString("ufEstado", "")
            val categoria = preferences.getString("categoria", "")
            val nomeEstado = preferences.getString("nomeEstado", "")
            val regiao = preferences.getString("regiao", "")
            val ddd = preferences.getString("ddd", "")

            val estado = Estado(
                uf = ufEstado!!,
                regiao = regiao!!,
                nome = nomeEstado!!,
                ddd = ddd!!
            )

            val filtro = Filtro(
                estado = estado,
                categoria = categoria!!,
                pesquisa = pesquisa!!,
            )

            preferences.getString("valorMin", "")?.let {
                if(it.isNotBlank()) filtro.valorMin = it.toInt()
            }

            preferences.getString("valorMax", "")?.let {
                if(it.isNotBlank()) filtro.valorMax = it.toInt()
            }

            return filtro
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