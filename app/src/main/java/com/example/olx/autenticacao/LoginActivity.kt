package com.example.olx.autenticacao

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.olx.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        findViewById<TextView>(R.id.textCriarConta).setOnClickListener {
            startActivity(Intent(this, CriarContaActivity::class.java))
        }
        findViewById<TextView>(R.id.textRecuperarSenha).setOnClickListener {
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }
    }

    // Inicia componentes de tela
    private fun iniciaComponentes(){
        val textToolbar = findViewById<TextView>(R.id.textToolbar)
        textToolbar.text = "Login"
    }

}