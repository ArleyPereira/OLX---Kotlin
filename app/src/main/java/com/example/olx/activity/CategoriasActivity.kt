package com.example.olx.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.util.CategoriaList
import com.example.olx.adapter.AdapterCategoria
import com.example.olx.model.Categoria

class CategoriasActivity : AppCompatActivity(), AdapterCategoria.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        // Inicia componentes de tela
        iniciaComponentes()

        // Inicia RecyclerView
        val todasCategorias = intent.getBooleanExtra("todasCategorias", true)
        configRv(todasCategorias)

    }

    // Inicia RecyclerView
    private fun configRv(todas: Boolean){
        rvCategorias.layoutManager = LinearLayoutManager(this)
        rvCategorias.adapter = AdapterCategoria(CategoriaList.getList(todas), this)
    }

    // Inicia componentes de tela
    private fun iniciaComponentes(){
        findViewById<View>(R.id.ibVoltar).setOnClickListener { finish() }

        val textToolbar = findViewById<TextView>(R.id.textToolbar)
        textToolbar.text = "Categorias"
    }

    override fun onItemClick(categoria: Categoria) {
        val intent = Intent()
        intent.putExtra("categoriaSelecionada", categoria)
        setResult(RESULT_OK, intent)
        finish()
    }

}