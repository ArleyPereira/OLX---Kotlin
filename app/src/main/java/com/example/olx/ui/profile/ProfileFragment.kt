package com.example.olx.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.olx.MainGraphDirections
import com.example.olx.R
import com.example.olx.databinding.FragmentProfileBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.User
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.example.olx.util.toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.squareup.picasso.Picasso

class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var user: User

    private var imageProfile: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        // Recupera dados do Perfil
        getProfile()

        // Ouvinte Cliques dos componentes
        initClicks()
    }

    // Recupera dados do Perfil
    private fun getProfile() {
        if(FirebaseHelper.isAutenticated()){
            FirebaseHelper.getDatabase()
                .child("usuarios")
                .child(FirebaseHelper.getIdUser())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        hideKeyboard()

                        binding.progressBar.visibility = View.VISIBLE

                        user = snapshot.getValue(User::class.java)!!

                        // Configura as informações nos elementos
                        configData()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }else {
            findNavController().navigate(MainGraphDirections.actionGlobalNavigation())
        }
    }

    // Configura as informações nos componentes em tela
    private fun configData() {
        if (user.urlProfile.isNotBlank()) {
            Picasso.get()
                .load(user.urlProfile)
                .placeholder(R.drawable.loading)
                .into(binding.imgProfile)
        }
        binding.editNome.setText(user.name)
        binding.editTelefone.setText(user.phone)
        binding.editEmail.setText(user.email)

        // Oculta a progressBar
        binding.progressBar.visibility = View.GONE
    }

    // Valida as informações inseridas
    private fun validData() {
        val name = binding.editNome.text.toString().trim()
        val phone = binding.editTelefone.unMasked.trim()

        if (name.isNotEmpty()) {
            if (phone.isNotEmpty()) {
                if (phone.length == 11) {
                    hideKeyboard()

                    binding.progressBar.visibility = View.VISIBLE

                    user.name = name
                    user.phone = phone

                    if (imageProfile != null) {
                        saveProfile()
                    } else {
                        saveImageProfile()
                    }
                } else {
                    binding.editTelefone.requestFocus()
                    binding.editTelefone.error = "Telefone inválido."
                }
            } else {
                binding.editTelefone.requestFocus()
                binding.editTelefone.error = "Informe seu telefone."
            }
        } else {
            binding.editNome.requestFocus()
            binding.editNome.error = "Informe seu nome."
        }
    }

    // Salva a Imagem no Firebase Storage e recupera a URL
    private fun saveImageProfile() {
        val perfil = FirebaseHelper.getStorage()
            .child("imagens")
            .child("perfil")
            .child(FirebaseHelper.getIdUser() + ".jpeg")

        val uploadTask = perfil.putFile(Uri.parse(imageProfile))
        uploadTask.addOnSuccessListener {
            perfil.downloadUrl.addOnCompleteListener { task ->
                user.urlProfile = task.result.toString()
                saveProfile()
            }
        }.addOnFailureListener(requireActivity()) {
            Toast.makeText(requireContext(), "Falha no Upload.", Toast.LENGTH_SHORT).show()
        }
    }

    // Salva os dados do Usuário no Firebase Data Base
    private fun saveProfile() {
        val usuarioRef = FirebaseHelper.getDatabase()
            .child("usuarios")
            .child(user.id)
        usuarioRef.setValue(user).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Snackbar.make(
                    binding.btnSalvar,
                    "Informações salvas com sucesso.",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Não foi possível salvar os dados.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.progressBar.visibility = View.GONE
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
        binding.btnSalvar.setOnClickListener { validData() }
        binding.imgProfile.setOnClickListener { checkPermissionGallery() }
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

        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedTitle(R.string.permission_denied)
            .setDeniedMessage(getString(R.string.gallery_permission_denied))
            .setDeniedCloseButtonText("Não")
            .setGotoSettingButtonText("Sim")
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    // Abre a galeria do dispositivo do usuário
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            // Recupera imagem
            val imagemSelecionada = result.data?.data
            val imagemRecuperada: Bitmap

            try {

                imagemRecuperada = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        imagemSelecionada
                    )
                } else {
                    val source =
                        imagemSelecionada?.let {
                            ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                it
                            )
                        }
                    source?.let { ImageDecoder.decodeBitmap(it) }!!
                }

                binding.imgProfile.setImageBitmap(imagemRecuperada)
                imageProfile = imagemSelecionada.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}