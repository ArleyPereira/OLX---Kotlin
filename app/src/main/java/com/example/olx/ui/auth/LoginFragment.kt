package com.example.olx.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.olx.R
import com.example.olx.databinding.FragmentLoginBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.example.olx.util.showBottomSheetInfo

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object {
        val LOGIN_SUCESS = "LOGIN_SUCESS"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        // Ouvinte Cliques dos componentes
        initClicks()

        // Recupera o retorno e verifica se o usuário se cadastrou no app
        listenerRegisterAccount()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
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
                showBottomSheetInfo(R.string.password_empty_login_fragment)
            }
        } else {
            showBottomSheetInfo(R.string.email_empty_login_fragment)
        }
    }

    // Recupera o retorno e verifica se o usuário se cadastrou no app
    private fun listenerRegisterAccount() {
        parentFragmentManager.setFragmentResultListener(RegisterFragment.REGISTER_SUCESS,
            this,
            { key, bundle ->
                val sucess = bundle.getBoolean(RegisterFragment.REGISTER_SUCESS, false)
                if (sucess) {
                    parentFragmentManager.setFragmentResult(LOGIN_SUCESS, bundleOf())
                    findNavController().popBackStack()
                }
            })
    }

    // Efetua login no app pelo firebase autentication
    private fun loginApp(email: String, password: String) {
        FirebaseHelper.getAuth().signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                parentFragmentManager.setFragmentResult(LOGIN_SUCESS, bundleOf())
                findNavController().popBackStack()
            } else {
                binding.progressBar.visibility = View.GONE
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