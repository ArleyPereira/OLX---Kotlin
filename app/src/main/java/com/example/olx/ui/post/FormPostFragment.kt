package com.example.olx.ui.post

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.olx.BuildConfig
import com.example.olx.R
import com.example.olx.databinding.BottomSheetSelectImageBinding
import com.example.olx.databinding.FragmentFormPostBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.*
import com.example.olx.ui.filters.CategoriesFragment
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.example.olx.util.showBottomSheetInfo
import com.example.olx.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.squareup.picasso.Picasso
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*

class FormPostFragment : BaseFragment() {

    private var selectImageRequest = -1

    private val args: FormPostFragmentArgs by navArgs()

    private var _binding: FragmentFormPostBinding? = null
    private val binding get() = _binding!!

    private var newPost = true
    private lateinit var post: Post
    private var categorySelected: String = ""
    private lateinit var address: Address
    private val imageList = mutableListOf<Image>()
    private lateinit var retrofit: Retrofit
    private lateinit var user: User

    private var latestTmpUri: Uri? = null

    private lateinit var valueEventListener: ValueEventListener

    private var userRef: DatabaseReference? = null
    private var addressRef: DatabaseReference? = null

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

        getData()

        initListeners()
    }

    // Recupera dados locais e online
    private fun getData() {
        getExtras()

        getUser()

        getAddressUser()
    }

    // Ouvinte de evento dos componentes
    private fun initListeners() {
        // Recupera a categoria selecionada para o post
        listenerSelectcCategory()

        // Seta locale para configuração da mascara de valor
        binding.editPrice.locale = Locale("PT", "br")

        // Inicia o serviço da retrofit
        initRetrofit()

        // Ouvinte digitação do campo de CEP
        binding.editZipCode.addTextChangedListener(object : TextWatcher {
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

        binding.btnSave.setOnClickListener { validData() }
        binding.btnCategory.setOnClickListener {
            findNavController().navigate(R.id.action_formPostFragment_to_categoriesFragment)
        }

        binding.image0.setOnClickListener { bottomSheetSelectImage(0) }
        binding.image1.setOnClickListener { bottomSheetSelectImage(1) }
        binding.image2.setOnClickListener { bottomSheetSelectImage(2) }
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

    // Recupera a categoria selecionada para o post
    private fun listenerSelectcCategory() {
        parentFragmentManager.setFragmentResultListener(
            CategoriesFragment.SELECT_CATEGORY,
            this,
            { key, bundle ->
                val category =
                    bundle.getParcelable<Category>(CategoriesFragment.SELECT_CATEGORY)

                categorySelected = category?.name.toString()
                binding.btnCategory.text = categorySelected
            })
    }

    // Recupera o endereço do usuário que está cadastrando o post
    private fun getAddressUser() {
        addressRef = FirebaseHelper.getDatabase()
            .child("enderecos")
            .child(FirebaseHelper.getIdUser())
        valueEventListener = addressRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    address = snapshot.getValue(Address::class.java) as Address

                    address.cep.let {
                        binding.editZipCode.setText(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    // Recupera os dados do usuário do firebase
    private fun getUser() {
        userRef = FirebaseHelper.getDatabase()
            .child("usuarios")
            .child(FirebaseHelper.getIdUser())
        valueEventListener = userRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User::class.java) as User
                    user.phone.let { binding.editPhone.setText(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showBottomSheetInfo(R.string.error_generic)
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

            selectImageRequest = requestCode
            checkPermissionCamera()
        }

        sheetBinding.btnGallery.setOnClickListener {
            dialog.dismiss()

            selectImageRequest = requestCode
            checkPermissionGallery()
        }

        sheetBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    // Solicita permissções de acesso a galeria
    private fun checkPermissionGallery() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openGallery()
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
    private fun checkPermissionCamera() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openCamera()
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
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectGalleryResult.launch(intent)
    }

    // Abre a câmera do dispositivo do usuário
    private fun openCamera() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                selectCameraResult.launch(uri)
            }
        }
    }

    // Criar um arquivo temporário utilizado na captura da imagem pela câmera
    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    // Recebe a imagem selecionada da galeria do dispositivo
    private val selectGalleryResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap

            val imagemSelecionada = result.data?.data
            val caminho: String

            try {
                caminho = imagemSelecionada.toString()

                bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        imagemSelecionada
                    )
                } else {
                    val source = ImageDecoder.createSource(
                        requireContext().contentResolver,
                        imagemSelecionada!!
                    )
                    ImageDecoder.decodeBitmap(source)
                }

                when(selectImageRequest){
                    0 -> binding.image0.setImageBitmap(bitmap)
                    1 -> binding.image1.setImageBitmap(bitmap)
                    else -> binding.image2.setImageBitmap(bitmap)
                }

                configUpload(selectImageRequest, caminho)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    // Recebe a imagem selecionada da câmera do dispositivo
    private val selectCameraResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    try {
                        val request = when (selectImageRequest) {
                            0 -> 3
                            1 -> 4
                            else -> 5
                        }

                        when (request) {
                            3 -> binding.image0.setImageURI(uri)
                            4 -> binding.image1.setImageURI(uri)
                            else -> binding.image2.setImageURI(uri)
                        }

                        val pathImage = uri.toString()
                        configUpload(request, pathImage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
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
        Picasso.get().load(post.urlImages[0]).into(binding.image0)
        Picasso.get().load(post.urlImages[1]).into(binding.image1)
        Picasso.get().load(post.urlImages[2]).into(binding.image2)

        // Titulo
        binding.editTitle.setText(post.title)

        // Preço
        binding.editPrice.setText((post.price * 10).toString())

        // Categoria
        binding.btnCategory.text = post.category
        categorySelected = post.category

        //Descrição
        binding.editDescription.setText(post.description)

        // CEP
        binding.editZipCode.setText(post.address?.cep)
        post.address.let {
            if (it != null) {
                address = it
            }
        }
        binding.textAddress.text =
            getString(R.string.publicacao_local, address.bairro, address.localidade, address.uf)
    }

    // Inicia o serviço da retrofit
    private fun initRetrofit() {
        retrofit = Retrofit
            .Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Realiza a busca do endereço
    // com base no CEP digitado
    private fun getAddress(cep: String) {
//        binding.progressBar.visibility = View.VISIBLE
//
//        val cepService = retrofit.create(CEPService::class.java)
//        val call = cepService.recuperarCEP(cep)
//
//        call.enqueue(object : Callback<Address?> {
//            override fun onResponse(call: Call<Address?>?, response: Response<Address?>?) {
//                response?.body()?.let {
//                    address = it
//                    configAddress()
//                }
//            }
//
//            override fun onFailure(call: Call<Address?>?, t: Throwable?) {
//
//            }
//        })
    }

    // Exibe um TextView com o endereço
    // correspondente ao CEP digitado
    private fun configAddress() {
        address.let {
            val address = StringBuffer()
            address
                .append(it.bairro)
                .append(", ")
                .append(it.localidade)
                .append(", ")
                .append(it.uf)

            binding.textAddress.text = address
        }
        binding.progressBar.visibility = View.GONE
    }

    // Valida se as informacoes foram preenchidas
    private fun validData() {
        val title = binding.editTitle.text.toString().trim()
        val price = binding.editPrice.rawValue / 100
        val description = binding.editDescription.text.toString().trim()
        val phone = binding.editPhone.unMasked.trim()

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
                                            binding.btnSave,
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
                                binding.editPhone.requestFocus()
                                binding.editPhone.error = "Telefone inválido."
                            }
                        } else {
                            binding.editPhone.error = "Informe o telefone"
                            binding.editPhone.requestFocus()
                        }
                    } else {
                        binding.editDescription.error = "Informe a descrição."
                        binding.editDescription.requestFocus()
                    }
                } else {
                    Snackbar.make(
                        binding.btnCategory,
                        "Selecione a categoria.",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                binding.editPrice.error = "Informe o preço."
                binding.editPrice.requestFocus()
            }
        } else {
            binding.editTitle.error = "Informe o título."
            binding.editTitle.requestFocus()
        }

    }

    // Salva a Imagem no Firebase Storage e recupera a URL
    private fun saveImagePost(index: Int) {
        val storageReference = FirebaseHelper.getStorage()
            .child("imagens")
            .child("anuncios")
            .child(post.id)
            .child("imagem$index.jpeg")

        val uploadTask = storageReference.putFile(Uri.parse(imageList[index].pathImage))
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

    // Configura index das imagens
    private fun configUpload(requestCode: Int, pathImage: String) {
        val image = Image(pathImage, requestCode)

        if (imageList.isNotEmpty()) {
            var encontrou = false
            for (i in imageList.indices) {
                if (imageList[i].index == requestCode) {
                    encontrou = true
                }
            }

            if (encontrou) { // já está na lista ( Atualiza )
                imageList[requestCode] = image
            } else { // Não está na lista ( Adiciona )
                imageList.add(image)
            }
        } else {
            imageList.add(image)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (userRef != null) userRef?.removeEventListener(valueEventListener)
        _binding = null
    }

}
