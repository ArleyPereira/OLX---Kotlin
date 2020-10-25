package com.example.olx.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blackcat.currencyedittext.CurrencyEditText
import com.example.olx.R
import com.example.olx.Util.SPFiltro
import com.example.olx.api.CEPService
import com.example.olx.helper.GetFirebase
import com.example.olx.model.Anuncio
import com.example.olx.model.Imagem
import com.example.olx.model.Local
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_form_anuncio.*
import kotlinx.android.synthetic.main.toolbar_voltar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class FormAnuncioActivity : AppCompatActivity() {

    private val novoAnuncio = true
    private lateinit var anuncio: Anuncio
    private var categoria: String = ""
    private var local: Local? = null
    private val imagemList = mutableListOf<Imagem>()
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

    override fun onStart() {
        super.onStart()

        // Exibe as informações nos elementos
        configDados()

    }

    // Ouvinte Cliques
    private fun configCliques() {
        ibVoltar.setOnClickListener { finish() }
        btnSalvar.setOnClickListener { validaDados() }
        btnCategoria.setOnClickListener {
            val intent = Intent(this, CategoriasActivity::class.java)
            intent.putExtra("todasCategorias", false)
            startActivity(intent)
        }

        imagem0.setOnClickListener { showBottomSheet(0) }
        imagem1.setOnClickListener { showBottomSheet(1) }
        imagem2.setOnClickListener { showBottomSheet(2) }
    }

    // Exibe Bottom Sheet
    private fun showBottomSheet(requestCode: Int) {

        val modalbottomsheet = layoutInflater.inflate(R.layout.bottom_sheet_form_anuncio, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        dialog.setContentView(modalbottomsheet)
        dialog.show()

        modalbottomsheet.findViewById<View>(R.id.btnTirarFoto).setOnClickListener {
            dialog.dismiss()
            //verificaPermissaoCamera(requestCode)
        }

        modalbottomsheet.findViewById<View>(R.id.btnGaleria).setOnClickListener {
            dialog.dismiss()
            escolherImagemGaleria(requestCode)
        }

        modalbottomsheet.findViewById<View>(R.id.btnCancelar)
            .setOnClickListener { dialog.dismiss() }

    }

    // Abre a Galeria do dispositivo
    private fun escolherImagemGaleria(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }

    // Exibe as informações nos elementos
    private fun configDados() {

        val filtro = SPFiltro.getFiltro(this)
        categoria = filtro.categoria

        if (categoria.isNotBlank()) {
            btnCategoria.text = categoria
        } else {
            btnCategoria.text = "Selecione uma categoria"
        }

    }

    // Inicia Retrofit
    private fun retrofitConfig() {
        retrofit = Retrofit
            .Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Configura o Cep para busca
    private fun configCep() {

        editCep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val cep = p0.toString()
                    .replace("-", "")
                    .replace("_".toRegex(), "")

                if (cep.length == 8) {

                    // Oculta o teclado do dispotivo
                    ocultaTeclado()

                    // Realiza a chamada da busca
                    // do endereço com base no cep digitado
                    buscarEndereco(cep)


                } else {

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

        call.enqueue(object : Callback<Local?> {
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
                if (categoria.isNotBlank()) {
                    if (descricao.isNotBlank()) {

                        if (local != null) {

                            // Oculta o teclado do dispositivo
                            ocultaTeclado()

                            // Exibe a progressBar
                            progressBar.visibility = View.VISIBLE

                            anuncio = Anuncio(
                                titulo = titulo,
                                preco = preco.toDouble(),
                                categoria = categoria,
                                descricao = descricao,
                                local = local!!
                            )

                            if (novoAnuncio) { // Novo Anúncio

                                if (imagemList.size == 3) {

                                    for (i in imagemList.indices) {
                                        salvaImagemFirebase(i)
                                    }

                                } else {
                                    Snackbar.make(
                                        btnSalvar,
                                        "Selecione 3 imagens.",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }

                            }else { // Edita Anúncio

                                if(imagemList.isNotEmpty()){

                                    for (i in imagemList.indices) {
                                        salvaImagemFirebase(i)
                                    }

                                }else { // Não teve edições de imagens

                                    // Salva o Anúncio no Firebase
                                    salvaAnuncio()

                                }

                            }

                        } else {
                            editCep.error = "Informe o CEP."
                            editCep.requestFocus()
                        }

                    } else {
                        editDescricao.error = "Informe a descrição."
                        editDescricao.requestFocus()
                    }
                } else {
                    Snackbar.make(btnCategoria, "Selecione a categoria.", Snackbar.LENGTH_SHORT)
                        .show()
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

    // Salva a Imagem no Firebase Storage e recupera a URL
    private fun salvaImagemFirebase(index: Int) {
        val StorageReference = GetFirebase.getStorage()
            .child("imagens")
            .child("anuncios")
            .child(anuncio.id)
            .child("imagem$index.jpeg")

        val uploadTask = StorageReference.putFile(Uri.parse(imagemList[index].caminhoImagem))
        uploadTask.addOnSuccessListener {
            StorageReference.downloadUrl.addOnCompleteListener { task ->

                if (novoAnuncio) {
                    anuncio.urlFotos.add(task.result.toString())
                } else {
                    anuncio.urlFotos[index] = task.result.toString()
                }

                if (imagemList.size == index + 1) { // Hora de Salvar
                    // Salva o Anúncio no Firebase
                    salvaAnuncio()
                }

            }
        }.addOnFailureListener(this) {
            Toast.makeText(this, "Falha no Upload.", Toast.LENGTH_SHORT).show()
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

    // Configura index das imagens
    private fun configUpload(requestCode: Int, caminho: String) {

        val imagem = Imagem(caminho, requestCode)

        if (imagemList.isNotEmpty()) {

            var encontrou = false
            for (i in imagemList.indices) {
                if (imagemList[i].index == requestCode) {
                    encontrou = true
                }
            }

            if (encontrou) { // já está na lista ( Atualiza )
                imagemList[requestCode] = imagem
            } else { // Não está na lista ( Adiciona )
                imagemList.add(imagem)
            }

        } else {
            imagemList.add(imagem)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {

            val bitmap0: Bitmap
            val bitmap1: Bitmap
            val bitmap2: Bitmap

            val imagemSelecionada = data?.data
            val caminho: String

            if (requestCode <= 2) { // Galeria

                try {

                    //Recuperar caminho da imagem
                    caminho = imagemSelecionada.toString()

                    when (requestCode) {
                        0 -> {
                            bitmap0 = if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(
                                    baseContext.contentResolver,
                                    imagemSelecionada
                                )
                            } else {
                                val source = ImageDecoder.createSource(
                                    this.contentResolver,
                                    imagemSelecionada!!
                                )
                                ImageDecoder.decodeBitmap(source)
                            }
                            imagem0.setImageBitmap(bitmap0)
                        }
                        1 -> {
                            bitmap1 = if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(
                                    baseContext.contentResolver,
                                    imagemSelecionada
                                )
                            } else {
                                val source = ImageDecoder.createSource(
                                    this.contentResolver,
                                    imagemSelecionada!!
                                )
                                ImageDecoder.decodeBitmap(source)
                            }
                            imagem1.setImageBitmap(bitmap1)
                        }
                        2 -> {
                            bitmap2 = if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(
                                    baseContext.contentResolver,
                                    imagemSelecionada
                                )
                            } else {
                                val source = ImageDecoder.createSource(
                                    this.contentResolver,
                                    imagemSelecionada!!
                                )
                                ImageDecoder.decodeBitmap(source)
                            }
                            imagem2.setImageBitmap(bitmap2)
                        }
                    }

                    // Configura index das imagens
                    configUpload(requestCode, caminho)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else { // Camera

            }

        }

    }

}