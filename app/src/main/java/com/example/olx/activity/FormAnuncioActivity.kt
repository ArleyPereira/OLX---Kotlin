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
import com.example.olx.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_form_anuncio.*
import kotlinx.android.synthetic.main.activity_form_anuncio.btnSalvar
import kotlinx.android.synthetic.main.activity_form_anuncio.editNome
import kotlinx.android.synthetic.main.activity_form_anuncio.editTelefone
import kotlinx.android.synthetic.main.activity_form_anuncio.progressBar
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.toolbar_voltar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class FormAnuncioActivity : AppCompatActivity() {

    private val REQUESTCATEGORIA = 10

    private var novoAnuncio = true
    private lateinit var anuncio: Anuncio
    private var categoriaSelecinada: String = ""
    private var local: Local? = null
    private val imagemList = mutableListOf<Imagem>()
    private lateinit var retrofit: Retrofit
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_anuncio)

        // Inicia Retrofit
        retrofitConfig()

        // Inicia componentes de tela
        iniciaComponentes()

        // Recupera informações via Intent
        getInfoIntent()

        // Ouvinte Cliques
        configCliques()

        // Configura o Cep para busca
        configCep()

        // Recupera Usuário
        recuperaUsuario();

    }

    // Recupera Usuário
    private fun recuperaUsuario(){
        val usuarioRef = GetFirebase.getDatabase()
            .child("usuarios")
            .child(GetFirebase.getIdFirebase())
        usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usuario = snapshot.getValue(Usuario::class.java) as Usuario

                usuario.telefone.let {
                    editTelefone.setText(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // Recupera informações via Intent
    private fun getInfoIntent() {
        val bundle = intent.extras
        if (bundle != null) {
            anuncio = intent.getSerializableExtra("anuncio") as Anuncio
            novoAnuncio = intent.getBooleanExtra("novoAnuncio", false)
            configDados()
        }
    }

    // Ouvinte Cliques
    private fun configCliques() {
        ibVoltar.setOnClickListener { finish() }
        btnSalvar.setOnClickListener { validaDados() }
        btnCategoria.setOnClickListener {
            val intent = Intent(this, CategoriasActivity::class.java)
            intent.putExtra("todasCategorias", false)
            startActivityForResult(intent, REQUESTCATEGORIA)
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

        // TextView Toolbar
        textToolbar.text = "Edição anúncio"

        // Fotos
        Picasso.get().load(anuncio.urlFotos[0]).into(imagem0)
        Picasso.get().load(anuncio.urlFotos[1]).into(imagem1)
        Picasso.get().load(anuncio.urlFotos[2]).into(imagem2)

        // Titulo
        editNome.setText(anuncio.titulo)

        // Preço
        editPreco.setText((anuncio.preco * 10).toString())

        // Categoria
        btnCategoria.text = anuncio.categoria
        categoriaSelecinada = anuncio.categoria

        //Descrição
        editDescricao.setText(anuncio.descricao)

        // CEP
        editCep.setText(anuncio.local.cep)
        local = anuncio.local
        textLocal.text =
            getString(R.string.publicacao_local, local?.bairro, local?.localidade, local?.uf)

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

    // Realiza a busca do endereço
    // com base no CEP digitado
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
        val telefone = editTelefone.unMasked

        if (titulo.isNotBlank()) {
            if (preco > 0) {
                if (categoriaSelecinada.isNotBlank()) {
                    if (descricao.isNotBlank()) {
                        if (telefone.isNotBlank()) {
                            if (telefone.length == 11) {

                                if (local != null) {

                                    // Oculta o teclado do dispositivo
                                    ocultaTeclado()

                                    // Exibe a progressBar
                                    progressBar.visibility = View.VISIBLE

                                    if (novoAnuncio) anuncio = Anuncio()

                                    anuncio.idUsuario = GetFirebase.getIdFirebase()
                                    anuncio.titulo = titulo
                                    anuncio.preco = preco.toDouble()
                                    anuncio.categoria = categoriaSelecinada
                                    anuncio.descricao = descricao
                                    anuncio.telefone = telefone
                                    anuncio.local = local!!

                                    if (novoAnuncio) { // Novo Anúncio

                                        anuncio.id = GetFirebase.getDatabase().push().key.toString()

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

                                    } else { // Edita Anúncio

                                        if (imagemList.isNotEmpty()) {

                                            for (i in imagemList.indices) {
                                                salvaImagemFirebase(i)
                                            }

                                        } else { // Não teve edições de imagens

                                            // Edita o Anúncio no Firebase
                                            editaAnuncio()

                                        }


                                    }

                                } else {
                                    editCep.error = "Informe o CEP."
                                    editCep.requestFocus()
                                }

                            } else {
                                editTelefone.requestFocus()
                                editTelefone.error = "Telefone inválido."
                            }
                        } else {
                            editTelefone.error = "Informe o telefone"
                            editTelefone.requestFocus()
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
        val storageReference = GetFirebase.getStorage()
            .child("imagens")
            .child("anuncios")
            .child(anuncio.id)
            .child("imagem$index.jpeg")

        val uploadTask = storageReference.putFile(Uri.parse(imagemList[index].caminhoImagem))
        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnCompleteListener { task ->

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

        // Salva o Anúncio no Firebase
        anuncio.salvar()

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("id", 2)
        startActivity(intent)

        // Fecha a tela
        finish()

    }

    // Edita o Anúncio no Firebase
    private fun editaAnuncio() {
        // Edita o Anúncio no Firebase
        anuncio.editar()

        // Fecha a tela
        finish()
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

            if (requestCode == REQUESTCATEGORIA) {
                val categoria = data!!.getSerializableExtra("categoriaSelecionada") as Categoria?
                if (categoria != null) {
                    categoriaSelecinada = categoria.nome
                    btnCategoria.text = categoriaSelecinada
                }

            } else if (requestCode <= 2) { // Galeria

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