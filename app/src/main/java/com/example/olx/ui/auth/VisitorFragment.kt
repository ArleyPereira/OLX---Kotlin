package com.example.olx.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.olx.MainGraphDirections
import com.example.olx.R
import com.example.olx.databinding.FragmentVisitorBinding
import com.example.olx.util.initToolbar

class VisitorFragment : Fragment() {

    private var _binding: FragmentVisitorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(
            toolbar = binding.toolbar,
            HomeAsUpEnabled = arguments?.getBoolean("enable_toolbar") == null
        )

        // Ouvinte Cliques dos componentes
        initClicks()

        // Recupera o retorno e verifica se o usuário se autenticou no app
        listenerAuthenticatedApp()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_visitorFragment_to_navLogin)
        }
    }

    // Recupera o retorno e verifica se o usuário se autenticou no app
    private fun listenerAuthenticatedApp() {
        parentFragmentManager.setFragmentResultListener(LoginFragment.LOGIN_SUCESS,
            this,
            { key, bundle ->
                findNavController().popBackStack()
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}