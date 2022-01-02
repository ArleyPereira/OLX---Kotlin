package com.example.olx.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.adapter.CategoriesAdapter
import com.example.olx.databinding.FragmentCategoriesBinding
import com.example.olx.util.CategoriaList
import com.example.olx.util.initToolbar

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        // Receber dados via argumentos
        getExtras()
    }

    // Receber dados via argumentos
    private fun getExtras() {
        val allCategories = intent.getBooleanExtra("todasCategorias", true)

        initRecyclerView(allCategories)
    }

    // Configurações iniciais do RecyclerView
    private fun initRecyclerView(allCategories: Boolean) {
        binding.rvCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategorias.adapter =
            CategoriesAdapter(CategoriaList.getList(allCategories)) { category ->
                Toast.makeText(requireContext(), category.name, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}