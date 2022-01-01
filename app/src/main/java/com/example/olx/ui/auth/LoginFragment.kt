package com.example.olx.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.olx.R
import com.example.olx.databinding.FragmentLoginBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.util.BaseFragment
import com.example.olx.util.showBottomSheetInfo

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ouvinte Cliques dos componentes
        initClicks()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnRecoverPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
        }

        binding.btnLogin.setOnClickListener { validData() }
    }

    // Valida as informações inseridas
    private fun validData() {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {
                hideKeyboard()

                binding.progressBar.visibility = View.VISIBLE

                loginApp(email, password)
            } else {
                binding.editPassword.requestFocus()
                binding.editPassword.error = "Informe uma senha."
            }
        } else {
            binding.editEmail.requestFocus()
            binding.editEmail.error = "Informe seu e-mail."
        }
    }

    // Efetua login no app pelo firebase autentication
    private fun loginApp(email: String, password: String) {
        FirebaseHelper.getAuth().signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {



            } else {
                showBottomSheetInfo(
                    FirebaseHelper.validError(task.exception?.message.toString())
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}