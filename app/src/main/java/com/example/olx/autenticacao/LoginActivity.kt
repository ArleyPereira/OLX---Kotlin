package com.example.olx.autenticacao

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.olx.R
import com.example.olx.activity.MainActivity
import com.example.olx.helper.GetFirebase
import com.example.olx.model.Usuario
import kotlinx.android.synthetic.main.activity_criar_conta.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicia componentes de tela
        iniciaComponentes()

        // Ouvinte Cliques
        configCliques()

    }

    // Valida as informações inseridas
    private fun validaDados() {

        val email = editEmail.text.toString()
        val senha = editSenha.text.toString()

        if (!email.isBlank()) {
            if (!senha.isBlank()) {

                // Oculta o teclado do dispositivo
                ocultaTeclado()

                // Exibe a progressBar
                progressBar.visibility = View.VISIBLE

                // Loga o Usuário no App
                logarUsuario(Usuario(email = email, senha = senha))

            } else {
                editSenha.requestFocus()
                editSenha.error = "Informe uma senha."
            }
        } else {
            editEmail.requestFocus()
            editEmail.error = "Informe seu e-mail."
        }

    }

    // Loga o Usuário no App
    private fun logarUsuario(usuario: Usuario) {
        GetFirebase.getAuth().signInWithEmailAndPassword(
            usuario.email, usuario.senha
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                // Fecha a Activity
                finish()

                // Leva o Usuário para página home do app
                startActivity(Intent(this, MainActivity::class.java))

            } else {
                GetFirebase.getMsg(task.exception?.message.toString())?.let { showDialog(it) }
            }
        }
    }

    // Exibe mensagem para o Usuário em caso de erro
    private fun showDialog(msg: String) {

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

    // Oculta o teclado do dispositivo
    private fun ocultaTeclado() {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.SHOW_FORCED
        )
    }

    // Ouvinte Cliques
    private fun configCliques() {
        findViewById<ImageButton>(R.id.ibVoltar).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.textCriarConta).setOnClickListener {
            startActivity(Intent(this, CriarContaActivity::class.java))
        }
        findViewById<TextView>(R.id.textRecuperarSenha).setOnClickListener {
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }
        findViewById<Button>(R.id.btnLogar).setOnClickListener { validaDados() }
    }

    // Inicia componentes de tela
    private fun iniciaComponentes() {
        val textToolbar = findViewById<TextView>(R.id.textToolbar)
        textToolbar.text = "Login"
    }

}