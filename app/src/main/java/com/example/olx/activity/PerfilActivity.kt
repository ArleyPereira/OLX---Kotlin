package com.example.olx.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.olx.R
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_perfil.*

class PerfilActivity : AppCompatActivity() {

    private lateinit var user: User

    private val SELECAOGALERIA: Int = 200
    private var caminhoImagem: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Ouvinte Cliques
        configCliques()

        // Inicia componentes de tela
        iniciaComponentes()

        // Recupera dados do Perfil
        recuperaDados()

    }

    // Recupera dados do Perfil
    private fun recuperaDados() {
        val usuarioRef = FirebaseHelper.getDatabase()
            .child("usuarios")
            .child(FirebaseHelper.getIdUser())
        usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Oculta o teclado do dispositivo
                ocultaTeclado()

                // Exibe a progressBar
                progressBar.visibility = View.VISIBLE

                user = snapshot.getValue(User::class.java)!!

                // Configura as informações nos elementos
                configDados()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // Configura as informações nos elementos
    private fun configDados() {

        if (user.urlImagem.isNotBlank()) {
            Picasso.get()
                .load(user.urlImagem)
                .placeholder(R.drawable.loading)
                .into(imagemPerfil)
        }
        editNome.setText(user.nome)
        editTelefone.setText(user.telefone)
        editEmail.setText(user.email)

        // Oculta a progressBar
        progressBar.visibility = View.GONE

    }

    // Valida as informações inseridas
    private fun validaDados() {

        val nome = editNome.text.toString()
        val telefone = editTelefone.unMasked

        if (!nome.isBlank()) {
            if (!telefone.isBlank()) {
                if (telefone.length == 11) {

                    // Oculta o teclado do dispositivo
                    ocultaTeclado()

                    // Exibe a progressBar
                    progressBar.visibility = View.VISIBLE

                    user.nome = nome
                    user.telefone = telefone

                    if (caminhoImagem.isBlank()) {
                        // Salva os dados do Usuário no Firebase
                        salvarDados()
                    } else {
                        // Salva a Imagem no Firebase Storage e recupera a URL
                        salvaImagemFirebase()
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
            editNome.requestFocus()
            editNome.error = "Informe seu nome."
        }

    }

    // Salva a Imagem no Firebase Storage e recupera a URL
    private fun salvaImagemFirebase() {
        val perfil = FirebaseHelper.getStorage()
            .child("imagens")
            .child("perfil")
            .child(FirebaseHelper.getIdUser() + ".jpeg")

        val uploadTask = perfil.putFile(Uri.parse(caminhoImagem))
        uploadTask.addOnSuccessListener {
            perfil.downloadUrl.addOnCompleteListener { task ->

                user.urlImagem = task.result.toString()

                // Salva os dados do Usuário no Firebase Data Base
                salvarDados()

            }
        }.addOnFailureListener(this) {
            Toast.makeText(this, "Falha no Upload.", Toast.LENGTH_SHORT).show()
        }
    }

    // Salva os dados do Usuário no Firebase Data Base
    private fun salvarDados() {
        val usuarioRef = FirebaseHelper.getDatabase()
            .child("usuarios")
            .child(user.id)
        usuarioRef.setValue(user).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Snackbar.make(btnSalvar, "Informações salvas com sucesso.", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Não foi possível salvar os dados.", Toast.LENGTH_SHORT).show()
            }
        }

        // Oculta a progressBar
        progressBar.visibility = View.GONE

    }

    // Ouvinte Cliques
    private fun configCliques() {
        findViewById<ImageButton>(R.id.ibVoltar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnSalvar).setOnClickListener { validaDados() }
        findViewById<ImageView>(R.id.imagemPerfil).setOnClickListener { abrirGaleria() }
    }

    // Abre a galeria do dispotivo
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECAOGALERIA)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {

            // Recupera imagem
            val imagemSelecionada = data?.data
            val imagemRecuperada: Bitmap

            try {

                imagemRecuperada = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        baseContext.contentResolver,
                        imagemSelecionada
                    )
                } else {
                    val source =
                        imagemSelecionada?.let {
                            ImageDecoder.createSource(
                                this.contentResolver,
                                it
                            )
                        }
                    source?.let { ImageDecoder.decodeBitmap(it) }!!
                }

                imagemPerfil.setImageBitmap(imagemRecuperada)
                caminhoImagem = imagemSelecionada.toString()


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    // Inicia componentes de tela
    private fun iniciaComponentes() {
        val textToolbar = findViewById<TextView>(R.id.textToolbar)
        textToolbar.text = "Perfil"
    }

}