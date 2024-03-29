package com.example.olx.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.olx.R
import com.example.olx.databinding.FragmentRegisterBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.User
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.example.olx.util.showBottomSheet

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    companion object {
        val REGISTER_SUCESS = "REGISTER_SUCESS"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        // Ouvinte Cliques dos componentes
        initClicks()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
        binding.btnRegister.setOnClickListener { validData() }
    }

    // Valida as informações inseridas
    private fun validData() {
        val name = binding.editName.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val phone = binding.editPhone.unMasked.trim()
        val password = binding.editPassword.text.toString().trim()

        if (name.isNotEmpty()) {
            if (email.isNotEmpty()) {
                if (phone.isNotEmpty()) {
                    if (phone.length == 11) {
                        if (password.isNotEmpty()) {

                            hideKeyboard()

                            binding.progressBar.visibility = View.VISIBLE

                            // Criar a conta do Usuário no Firebase Autentication
                            createAccount(
                                User(
                                    name = name,
                                    email = email,
                                    password = password,
                                    phone = phone
                                )
                            )

                        } else {
                            showBottomSheet(message = getString(R.string.password_empty_register_fragment))
                        }
                    } else {
                        showBottomSheet(message = getString(R.string.phone_not_valid_register_fragment))
                    }
                } else {
                    showBottomSheet(message = getString(R.string.phone_empty_register_fragment))
                }
            } else {
                showBottomSheet(message = getString(R.string.email_empty_register_fragment))
            }
        } else {
            showBottomSheet(message = getString(R.string.name_empty_register_fragment))
        }
    }

    // Salva usuario no firebase autentication
    private fun createAccount(user: User) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
            user.email, user.password
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                user.id = FirebaseHelper.getAuth().currentUser!!.uid
                saveProfile(user)
            } else {
                showBottomSheet(
                    message = getString(FirebaseHelper.validError(task.exception?.message.toString()))
                )
            }
        }
    }

    // Salva dados do usuário no Firebase
    private fun saveProfile(user: User) {
        val usuarioRef = FirebaseHelper.getDatabase()
            .child("users")
            .child(user.id)
        usuarioRef.setValue(user).addOnCompleteListener {
            parentFragmentManager.setFragmentResult(
                REGISTER_SUCESS,
                bundleOf(Pair(REGISTER_SUCESS, true))
            )
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}