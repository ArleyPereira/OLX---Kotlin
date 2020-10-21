package com.example.olx.autenticacao

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.olx.R

class CriarContaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        // Inicia componentes de tela
        iniciaComponentes()

        // Ouvinte Cliques
        configCliques()

    }

    // Ouvinte Cliques
    private fun configCliques(){
        findViewById<ImageButton>(R.id.ibVoltar).setOnClickListener {
            finish()
        }
    }

    // Inicia componentes de tela
    private fun iniciaComponentes(){
        val textToolbar = findViewById<TextView>(R.id.textToolbar)
        textToolbar.text = "Criar conta"
    }

}