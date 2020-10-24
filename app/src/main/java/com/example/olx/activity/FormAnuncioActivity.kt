package com.example.olx.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blackcat.currencyedittext.CurrencyEditText
import com.example.olx.R
import com.example.olx.api.CEPService
import com.example.olx.helper.GetFirebase
import com.example.olx.model.Anuncio
import com.example.olx.model.Local
import kotlinx.android.synthetic.main.activity_form_anuncio.*
import kotlinx.android.synthetic.main.activity_form_anuncio.btnSalvar
import kotlinx.android.synthetic.main.activity_form_anuncio.editNome
import kotlinx.android.synthetic.main.activity_form_anuncio.progressBar
import kotlinx.android.synthetic.main.toolbar_voltar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class FormAnuncioActivity : AppCompatActivity() {

    private lateinit var anuncio: Anuncio
    private var categoria: String = ""
    private var local: Local? = null

    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_anuncio)

        // Inicia Retrofit
        retrofitConfig()

        // Inicia componentes de tela
        iniciaComponentes()

        // Ouvinte Cliques
        configCliques()

        // Configura o Cep para busca
        configCep()

    }

    // Inicia Retrofit
    private fun retrofitConfig(){
        retrofit = Retrofit
            .Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Configura o Cep para busca
    private fun configCep(){

        editCep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val cep = p0.toString()
                    .replace("-", "")
                    .replace("_".toRegex(), "")

                if(cep.length == 8){

                    // Oculta o teclado do dispotivo
                    ocultaTeclado()

                    // Realiza a chamada da busca
                    // do endereço com base no cep digitado
                    buscarEndereco(cep)


                }else {

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    private fun buscarEndereco(cep: String) {

        // Exibe a progressBar
        progressBar.visibility = View.VISIBLE

        val cepService = retrofit.create(CEPService::class.java)
        val call = cepService.recuperarCEP(cep)

        call.enqueue(object: Callback<Local?> {
            override fun onResponse(call: Call<Local?>?, response: Response<Local?>?) {

                response?.body()?.let {
                    local = it
                    configEndereco()
                }

            }

            override fun onFailure(call: Call<Local?>?, t: Throwable?) {

            }
        })

    }

    // Exibe um TextView com o endereço
    // correspondente ao CEP digitado
    private fun configEndereco() {

        local?.let {
            val endereco = StringBuffer()
            endereco
                .append(it.bairro)
                .append(", ")
                .append(it.localidade)
                .append(", ")
                .append(it.uf)

            textLocal.text = endereco
        }

        // Oculta a progressBar
        progressBar.visibility = View.GONE

    }

    // Valida as informações inseridas
    private fun validaDados() {

        val titulo = editNome.text.toString()
        val preco = editPreco.rawValue / 100
        val descricao = editDescricao.text.toString()

        if (titulo.isNotBlank()) {
            if (preco > 0) {
                if (descricao.isNotBlank()) {

                    if(local != null){

                        // Oculta o teclado do dispositivo
                        ocultaTeclado()

                        // Exibe a progressBar
                        progressBar.visibility = View.VISIBLE

                        anuncio = Anuncio(
                            titulo = titulo,
                            preco =  preco.toDouble(),
                            descricao =  descricao,
                            local = local!!
                        )

                        // Salva o Anúncio no Firebase
                        salvaAnuncio()
                    }else{
                        editCep.error = "Informe o CEP."
                        editCep.requestFocus()
                    }

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