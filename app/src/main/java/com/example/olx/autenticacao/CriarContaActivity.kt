package com.example.olx.autenticacao

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.olx.R
import com.example.olx.activity.MainActivity
import com.example.olx.helper.GetFirebase
import com.example.olx.model.Usuario
import kotlinx.android.synthetic.main.activity_criar_conta.*
import kotlinx.android.synthetic.main.toolbar_voltar.*
import java.util.*

class CriarContaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        // Inicia componentes de tela
        iniciaComponentes()

        // Ouvinte Cliques
        configCliques()

    }

    // Valida as informações inseridas
    private fun validaDados() {

        val nome = editNome.text.toString()
        val email = editEmail.text.toString()
        val telefone = editTelefone.unMasked
        val senha = editSenha.text.toString()

        if (!nome.isBlank()) {
            if (!email.isBlank()) {
                if (!telefone.isBlank()) {
                    if (telefone.length == 11) {
                        if (!senha.isBlank()) {

                            // Oculta o teclado do dispositivo
                            ocultaTeclado()

                            // Exibe a progressBar
                            progressBar.visibility = View.VISIBLE

                            // Criar a conta do Usuário no Firebase Autentication
                            criarConta(
                                Usuario(
                                    nome = nome,
                                    email = email,
                                    senha = senha,
                                    telefone = telefone
                                )
                            )

                        } else {
                            editSenha.requestFocus()
                            editSenha.error = "Informe uma senha."
                        }
                    } else {
                        editTelefone.requestFocus()
                        editTelefone.error = "Telefone inválido."
                    }
                } else {
                    editTelefone.requestFocus()
                    editTelefone.error = "Informe seu telefone."
                }
            } else {
                editEmail.requestFocus()
                editEmail.error = "Informe seu e-mail."
            }
        } else {
            editNome.requestFocus()
            editNome.error = "Informe seu nome."
        }

    }

    // Criar a conta do Usuário no Firebase Autentication
    private fun criarConta(usuario: Usuario) {

        GetFirebase.getAuth().createUserWithEmailAndPassword(
            usuario.email, usuario.senha
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                // Recupera o ID do cadastro
                usuario.id = GetFirebase.getAuth().currentUser!!.uid

                // Salva os dados do Usuário no Firebase Data Base
                salvarCadastro(usuario)

                // Fecha a Activity
                finish()

                // Leva o Usuário para página home do app
                startActivity(Intent(this, MainActivity::class.java))

            } else {
                // Exibe mensagem para o Usuário em caso de erro
                GetFirebase.getMsg(task.exception?.message.toString())?.let { showDialog(it) }
            }
        }
    }

    // Exibe mensagem para o Usuário em caso de erro
    private fun showDialog(msg: String){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erro")
        builder.setMessage(msg)

        builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()

        progressBar.visibility = View.GONE

    }

    // Salva os dados do Usuário no Firebase Data Base
    private fun salvarCadastro(usuario: Usuario) {
     val usuarioRef = GetFirebase.getDatabase()
            .child("usuarios")
            .child(usuario.id)
        usuarioRef.setValue(usuario)
    }

    // Ouvinte Cliques
    private fun configCliques() {
        ibVoltar.setOnClickListener {
            finish()
        }
        btnCriarConta.setOnClickListener { validaDados() }
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
        val textToolbar = findViewById<TextView>(R.id.textToolbar)
        textToolbar.text = "Criar conta"
    }

}