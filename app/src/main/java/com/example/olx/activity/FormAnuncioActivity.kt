package com.example.olx.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blackcat.currencyedittext.CurrencyEditText
import com.example.olx.R
import com.example.olx.helper.GetFirebase
import com.example.olx.model.Anuncio
import kotlinx.android.synthetic.main.activity_form_anuncio.*
import kotlinx.android.synthetic.main.activity_form_anuncio.btnSalvar
import kotlinx.android.synthetic.main.activity_form_anuncio.editNome
import kotlinx.android.synthetic.main.activity_form_anuncio.progressBar
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.toolbar_voltar.*
import java.util.*

class FormAnuncioActivity : AppCompatActivity() {

    private lateinit var anuncio: Anuncio
    private var categoria: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_anuncio)

        // Inicia componentes de tela
        iniciaComponentes()

        // Ouvinte Cliques
        configCliques()

    }

    // Valida as informações inseridas
    private fun validaDados() {

        val titulo = editNome.text.toString()
        val preco = editPreco.rawValue / 100
        val descricao = editDescricao.text.toString()

        if (titulo.isNotBlank()) {
            if (preco > 0) {
                if (descricao.isNotBlank()) {

                    // Oculta o teclado do dispositivo
                    ocultaTeclado()

                    // Exibe a progressBar
                    progressBar.visibility = View.VISIBLE

                    anuncio = Anuncio(titulo = titulo, preco =  preco.toDouble(), descricao =  descricao)

                    // Salva o Anúncio no Firebase
                    salvaAnuncio()

                } else {
                    editDescricao.error = "Informe a descrição."
                    editDescricao.requestFocus()
                }
            } else {
                editPreco.error = "Informe o preço."
                editPreco.requestFocus()
            }
        } else {
            editNome.error = "Informe o título."
            editNome.requestFocus()
        }

    }

    // Salva o Anúncio no Firebase
    private fun salvaAnuncio() {
        val anuncioRef = GetFirebase.getDatabase()
            .child("anuncios")
            .child(GetFirebase.getIdFirebase())
            .child(anuncio.id)
        anuncioRef.setValue(anuncio).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                // Fecha a tela
                finish()

                // Leva o Usuário para tela de Meus Anúncio
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("id", 2)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Falha ao salvar o anúncio.", Toast.LENGTH_SHORT).show()
            }

            // Oculta a progressBar
            progressBar.visibility = View.GONE

        }
    }

    // Ouvinte Cliques
    private fun configCliques() {
        ibVoltar.setOnClickListener { finish() }
        btnSalvar.setOnClickListener { validaDados() }
    }

    // Oculta o teclado do dispositivo
    private fun ocultaTeclado() {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.SHOW_FORCED
        )
    }

    // Inicia componentes de tela
    private fun iniciaComponentes() {
        findViewById<CurrencyEditText>(R.id.editPreco).locale = Locale("PT", "br")
        findViewById<TextView>(R.id.textToolbar).text = "Novo anúncio"
    }

}