package com.example.olx.ui.splash

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.olx.R
import com.example.olx.databinding.FragmentSplashBinding
import com.example.olx.util.SPFiltro

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Full Screen
        //fullScreen()

        // Leva o Usuário para tela principal
        Handler(Looper.getMainLooper()).postDelayed({ run { homeApp() } }, 1000)

        // Limpa todos os filtros
        SPFiltro.limpaFiltros(activity)
    }

    // Leva o Usuário para tela principal
    private fun homeApp() {
        findNavController().navigate(R.id.action_splashFragment_to_menu_home)
    }

    // Full Screen
    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = requireActivity().window.insetsController
            if (insetsController != null) {
                requireActivity().window.insetsController!!.hide(WindowInsets.Type.statusBars())
            }
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}