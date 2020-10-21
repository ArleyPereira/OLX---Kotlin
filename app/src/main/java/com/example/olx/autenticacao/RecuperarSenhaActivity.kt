package com.example.olx.autenticacao

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.olx.R

class RecuperarSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

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
        textToolbar.text = "Esqueceu sua senha ?"
    }

}