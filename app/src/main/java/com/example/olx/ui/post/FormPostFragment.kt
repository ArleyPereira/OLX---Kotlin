package com.example.olx.ui.post

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.olx.R
import com.example.olx.api.CEPService
import com.example.olx.databinding.BottomSheetSelectImageBinding
import com.example.olx.databinding.FragmentFormPostBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.*
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.example.olx.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FormPostFragment : BaseFragment() {

    private val args: FormPostFragmentArgs by navArgs()

    private var _binding: FragmentFormPostBinding? = null
    private val binding get() = _binding!!

    private val REQUESTCATEGORIA = 10

    private var newPost = true
    private lateinit var post: Post
    private var categorySelected: String = ""
    private lateinit var address: Address
    private val imageList = mutableListOf<Imagem>()
    private lateinit var retrofit: Retrofit
    private lateinit var user: User

    private var currentPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        initRetrofit()

        iniciaComponentes()

        getExtras()

        initClicks()

        configCep()

        getUser()

        getAddressUser()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
        binding.btnSalvar.setOnClickListener { validData() }
        binding.btnCategoria.setOnClickListener {
            findNavController().navigate(R.id.action_formPostFragment_to_categoriesFragment)
        }

        binding.imagem0.setOnClickListener { bottomSheetSelectImage(0) }
        binding.imagem1.setOnClickListener { bottomSheetSelectImage(1) }
        binding.imagem2.setOnClickListener { bottomSheetSelectImage(2) }
    }

    // Receber dados via argumentos
    private fun getExtras() {
         args.post.let {
             if (it != null) {
                 post = it

                 newPost = false
                 configData()
             }
         }
    }

    // Recupera o endereço do usuário que está cadastrando o post
    private fun getAddressUser() {
        FirebaseHelper.getDatabase()
            .child("enderecos")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    address = snapshot.getValue(Address::class.java) as Address

                    address.cep.let {
                        binding.editCep.setText(it)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    // Recupera os dados do usuário do firebase
    private fun getUser() {
        FirebaseHelper.getDatabase()
            .child("usuarios")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java) as User

                    user.phone.let { binding.editTelefone.setText(it) }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    // Abre bottom sheet para seleção das imagens do post ( câmera e galeria )
    private fun bottomSheetSelectImage(requestCode: Int) {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val sheetBinding: BottomSheetSelectImageBinding =
            BottomSheetSelectImageBinding.inflate(layoutInflater, null, false)

        sheetBinding.btnCamera.setOnClickListener {
            dialog.dismiss()
            checkPermissionCamera(requestCode)
        }

        sheetBinding.btnGallery.setOnClickListener {
            dialog.dismiss()
            checkPermissionGallery(requestCode)
        }

        sheetBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    // Solicita permissções de acesso a galeria
    private fun checkPermissionGallery(requestCode: Int) {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openGallery(requestCode)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                requireContext().toast(R.string.permission_denied)
            }
        }
        showDialogPermission(
            permissionlistener, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            R.string.gallery_permission_denied
        )
    }

    // Solicita permissções de acesso a câmera
    private fun checkPermissionCamera(requestCode: Int) {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openCamera(requestCode)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                requireContext().toast(R.string.permission_denied)
            }
        }
        showDialogPermission(
            permissionlistener, arrayOf(Manifest.permission.CAMERA),
            R.string.camera_permission_denied
        )
    }

    // Abre a galeria do dispositivo do usuário
    private fun openGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }

    // Abre a câmera do dispositivo do usuário
    private fun openCamera(requestCode: Int) {
        val request = when (requestCode) {
            0 -> 3
            1 -> 4
            2 -> 5
            else -> 6
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photoFile: File? = null

        try {
            photoFile = createImageFile()
        } catch (ignored: IOException) {
        }

        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.example.olx.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, request)
        }

    }

    // Cria um arquivo foto no dispositivo
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale("pt", "BR")).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    // Exibe dialog permissões negadas
    private fun showDialogPermission(
        permissionlistener: PermissionListener,
        permissions: Array<String>,
        msg: Int
    ) {
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedTitle(R.string.permission_denied)
            .setDeniedMessage(msg)
            .setDeniedCloseButtonText("Não")
            .setGotoSettingButtonText("Sim")
            .setPermissions(*permissions)
            .check()
    }

    // Exibe as informações do post nos componentes
    private fun configData() {
        Picasso.get().load(post.urlImages[0]).into(binding.imagem0)
        Picasso.get().load(post.urlImages[1]).into(binding.imagem1)
        Picasso.get().load(post.urlImages[2]).into(binding.imagem2)

        // Titulo
        binding.editNome.setText(post.title)

        // Preço
        binding.editPreco.setText((post.price * 10).toString())

        // Categoria
        binding.btnCategoria.text = post.category
        categorySelected = post.category

        //Descrição
        binding.editDescricao.setText(post.description)

        // CEP
        binding.editCep.setText(post.address?.cep)
        post.address.let {
            if (it != null) {
                address = it
            }
        }
        binding.textLocal.text =
            getString(R.string.publicacao_local, address.bairro, address.localidade, address.uf)
    }

    private fun initRetrofit() {
        retrofit = Retrofit
            .Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Configura o Cep para busca
    private fun configCep() {
        binding.editCep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val cep = p0.toString()
                    .replace("-", "")
                    .replace("_".toRegex(), "")

                if (cep.length == 8) {
                    hideKeyboard()

                    // Realiza a chamada da busca
                    // do endereço com base no cep digitado
                    getAddress(cep)
                } else {

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    // Realiza a busca do endereço
    // com base no CEP digitado
    private fun getAddress(cep: String) {
        binding.progressBar.visibility = View.VISIBLE

        val cepService = retrofit.create(CEPService::class.java)
        val call = cepService.recuperarCEP(cep)

        call.enqueue(object : Callback<Address?> {
            override fun onResponse(call: Call<Address?>?, response: Response<Address?>?) {
                response?.body()?.let {
                    address = it
                    configAddress()
                }
            }

            override fun onFailure(call: Call<Address?>?, t: Throwable?) {

            }
        })
    }

    // Exibe um TextView com o endereço
    // correspondente ao CEP digitado
    private fun configAddress() {
        address.let {
            val endereco = StringBuffer()
            endereco
                .append(it.bairro)
                .append(", ")
                .append(it.localidade)
                .append(", ")
                .append(it.uf)

            binding.textLocal.text = endereco
        }
        binding.progressBar.visibility = View.GONE
    }

    // Valida se as informacoes foram preenchidas
    private fun validData() {
        val title = binding.editNome.text.toString().trim()
        val price = binding.editPreco.rawValue / 100
        val description = binding.editDescricao.text.toString().trim()
        val phone = binding.editTelefone.unMasked.trim()

        if (title.isNotEmpty()) {
            if (price > 0) {
                if (categorySelected.isNotBlank()) {
                    if (description.isNotEmpty()) {
                        if (phone.isNotEmpty()) {
                            if (phone.length == 11) {

                                hideKeyboard()

                                binding.progressBar.visibility = View.VISIBLE

                                if (newPost) post = Post()

                                post.idUsuario = FirebaseHelper.getIdUser()
                                post.title = title
                                post.price = price.toDouble()
                                post.category = categorySelected
                                post.description = description
                                post.phone = phone
                                post.address = address

                                if (newPost) { // Novo Anúncio

                                    post.id = FirebaseHelper.getDatabase().push().key.toString()

                                    if (imageList.size == 3) {
                                        for (i in imageList.indices) {
                                            saveImagePost(i)
                                        }
                                    } else {
                                        Snackbar.make(
                                            binding.btnSalvar,
                                            "Selecione 3 imagens.",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                } else { // Edita Anúncio
                                    if (imageList.isNotEmpty()) {
                                        for (i in imageList.indices) {
                                            saveImagePost(i)
                                        }
                                    } else { // Não teve edições de imagens
                                        post.editar()
                                    }
                                }
                            } else {
                                binding.editTelefone.requestFocus()
                                binding.editTelefone.error = "Telefone inválido."
                            }
                        } else {
                            binding.editTelefone.error = "Informe o telefone"
                            binding.editTelefone.requestFocus()
                        }
                    } else {
                        binding.editDescricao.error = "Informe a descrição."
                        binding.editDescricao.requestFocus()
                    }
                } else {
                    Snackbar.make(binding.btnCategoria, "Selecione a categoria.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.editPreco.error = "Informe o preço."
                binding.editPreco.requestFocus()
            }
        } else {
            binding.editNome.error = "Informe o título."
            binding.editNome.requestFocus()
        }

    }

    // Salva a Imagem no Firebase Storage e recupera a URL
    private fun saveImagePost(index: Int) {
        val storageReference = FirebaseHelper.getStorage()
            .child("imagens")
            .child("anuncios")
            .child(post.id)
            .child("imagem$index.jpeg")

        val uploadTask = storageReference.putFile(Uri.parse(imageList[index].caminhoImagem))
        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnCompleteListener { task ->

                if (newPost) {
                    post.urlImages.add(task.result.toString())
                } else {
                    post.urlImages[index] = task.result.toString()
                }

                if (imageList.size == index + 1) { // Hora de Salvar
                    post.salvar()
                    findNavController().popBackStack()
                }

            }
        }.addOnFailureListener(requireActivity()) {
            Toast.makeText(requireContext(), "Falha no Upload.", Toast.LENGTH_SHORT).show()
        }
    }

    // Inicia componentes de tela
    private fun iniciaComponentes() {
        binding.editPreco.locale = Locale("PT", "br")
    }

    // Configura index das imagens
    private fun configUpload(requestCode: Int, caminho: String) {
        val imagem = Imagem(caminho, requestCode)

        if (imageList.isNotEmpty()) {

            var encontrou = false
            for (i in imageList.indices) {
                if (imageList[i].index == requestCode) {
                    encontrou = true
                }
            }

            if (encontrou) { // já está na lista ( Atualiza )
                imageList[requestCode] = imagem
            } else { // Não está na lista ( Adiciona )
                imageList.add(imagem)
            }
        } else {
            imageList.add(imagem)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {

            val bitmap0: Bitmap
            val bitmap1: Bitmap
            val bitmap2: Bitmap

            val imagemSelecionada = data?.data
            val caminho: String

            if (requestCode == REQUESTCATEGORIA) {
                val categoria = data!!.getSerializableExtra("categoriaSelecionada") as Category?
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

                try {

                    val f = File(currentPhotoPath!!)

                    // Recupera o caminho da imagem
                    caminho = f.toURI().toString()

                    when (requestCode) {
                        3 -> imagem0.setImageURI(Uri.fromFile(f))
                        4 -> imagem1.setImageURI(Uri.fromFile(f))
                        5 -> imagem2.setImageURI(Uri.fromFile(f))
                    }

                    // Configura index das imagens
                    configUpload(requestCode, caminho)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}