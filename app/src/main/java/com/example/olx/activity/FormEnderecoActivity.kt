package com.example.olx.activity

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.olx.R
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Address
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_form_endereco.*
import kotlinx.android.synthetic.main.toolbar_voltar.*


class FormEnderecoActivity : AppCompatActivity() {

    private lateinit var address: Address
    private var novoEndereco: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_endereco)

        // Ouvinte Cliques
        configCliques()

        // Recupera endereço
        recuperaEndereco()

        // Inicia componentes de tela
        iniciaComponentes()

    }

    // Recupera endereço
    private fun recuperaEndereco(){
        val enderecoRef = FirebaseHelper.getDatabase()
            .child("enderecos")
            .child(FirebaseHelper.getIdUser())
        enderecoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    address = snapshot.getValue(Address::class.java) as Address

                    // Configura as informações recuperadas
                    configDados()

                    novoEndereco = false
                }else {
                    // Oculta progressbar
                    progressBar.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    // Configura as informações recuperadas
    private fun configDados(){
        editCep.setText(address.cep)
        editEstado.setText(address.uf)
        editCidade.setText(address.localidade)
        editBairro.setText(address.bairro)

        // Oculta progressbar
        progressBar.visibility = View.GONE

        // Exibe Título na Toolbar
        textToolbar.text = "Endereço"

    }

    // Ouvinte Cliques
    private fun configCliques() {
        btnSalvar.setOnClickListener { validaDados() }
        ibVoltar.setOnClickListener { finish() }
    }

    // Valida as informações inseridas
    private fun validaDados() {

        val cep = editCep.text.toString()
        val uf = editEstado.text.toString()
        val cidade = editCidade.text.toString()
        val bairro = editBairro.text.toString()

        if (cep.isNotBlank()) {
            if (uf.isNotBlank()) {
                if (cidade.isNotBlank()) {
                    if (bairro.isNotBlank()) {

                        // Exibe progressbar
                        progressBar.visibility = View.VISIBLE

                        // Oculta o teclado do dispositivo
                        ocultaTeclado()

                        if(novoEndereco) address = Address()
                        address.cep = cep
                        address.uf = uf
                        address.localidade = cidade
                        address.bairro = bairro
                        address.salvar(FirebaseHelper.getIdUser())

                        Snackbar.make(
                            btnSalvar,
                            "Endereço salvo com sucesso.",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        // Oculta progressbar
                        progressBar.visibility = View.GONE

                    } else {
                        editBairro.requestFocus()
                        editBairro.error = "Informe o bairro."
                    }
                } else {
                    editCidade.requestFocus()
                    editCidade.error = "Informe a cidade."
                }
            } else {
                editEstado.requestFocus()
                editEstado.error = "Informe o Estado."
            }
        } else {
            editCep.requestFocus()
            editCep.error = "Informe o CEP."
        }

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
    private fun iniciaComponentes(){
        editEstado.filters = arrayOf<InputFilter>(AllCaps())
    }

}










