package com.example.olx.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.adapter.CategoriesAdapter
import com.example.olx.databinding.FragmentCategoriesBinding
import com.example.olx.model.Category
import com.example.olx.util.CategoriaList
import com.example.olx.util.initToolbar

class CategoriesFragment : Fragment() {

    private val args: CategoriesFragmentArgs by navArgs()

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    companion object {
        val SELECT_CATEGORY = "SELECT_CATEGORY"
    }

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
        initRecyclerView(args.allCategorys)
    }

    // Configurações iniciais do RecyclerView
    private fun initRecyclerView(allCategories: Boolean) {
        binding.rvCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategorias.adapter =
            CategoriesAdapter(CategoriaList.getList(allCategories)) { category ->
                setResultCategory(category)
            }
    }

    // Retorna para tela anterior com a categoria selecionada
    private fun setResultCategory(category: Category) {
        val bundle = Bundle()
        bundle.putParcelable(SELECT_CATEGORY, category)
        parentFragmentManager.setFragmentResult(SELECT_CATEGORY, bundle)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}