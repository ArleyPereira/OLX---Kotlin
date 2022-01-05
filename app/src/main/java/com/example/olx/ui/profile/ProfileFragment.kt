package com.example.olx.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.olx.databinding.FragmentProfileBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.User
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var user: User

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
    }

    // Configura as informações nos componentes em tela
    private fun configData() {
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

                    saveProfile()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}