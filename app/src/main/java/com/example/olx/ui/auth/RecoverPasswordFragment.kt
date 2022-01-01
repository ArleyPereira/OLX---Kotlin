package com.example.olx.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.olx.R
import com.example.olx.databinding.FragmentRecoverPasswordBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.example.olx.util.showBottomSheetInfo
import com.example.olx.util.toast

class RecoverPasswordFragment : BaseFragment() {

    private var _binding: FragmentRecoverPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
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
        binding.btnRecover.setOnClickListener { validData() }
    }

    // Valida as informações inseridas
    private fun validData() {
        val email = binding.editEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            hideKeyboard()

            binding.progressBar.visibility = View.VISIBLE

            sendEmail(email)
        } else {
            showBottomSheetInfo(R.string.text_email_empty)
        }
    }

    // Envia link para o e-mail informado
    private fun sendEmail(email: String) {
        FirebaseHelper
            .getAuth()
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    requireContext().toast(R.string.text_recover_password_fragment)
                } else {
                    showBottomSheetInfo(FirebaseHelper.validError(task.exception?.message!!))
                }
                binding.progressBar.visibility = View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}