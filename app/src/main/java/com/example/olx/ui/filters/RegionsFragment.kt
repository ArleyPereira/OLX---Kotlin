package com.example.olx.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.adapter.RegionAdapter
import com.example.olx.databinding.FragmentRegionsBinding
import com.example.olx.helper.SPFilters
import com.example.olx.ui.post.PostsFragment
import com.example.olx.util.RegionsList
import com.example.olx.util.initToolbar

class RegionsFragment : Fragment() {

    private var _binding: FragmentRegionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvRegions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRegions.adapter =
            RegionAdapter(RegionsList.getRegionsList(SPFilters.getFilters(requireActivity()).state.uf)) { region ->
                listenerFilters(region)
            }
    }

    private fun listenerFilters(region: String) {
        if(region != "Todas as regiões"){
            SPFilters.setFilters(requireActivity(), "region", region)
            SPFilters.setFilters(requireActivity(), "ddd", region.substring(4, 6))
        }else {
            SPFilters.setFilters(requireActivity(), "region", "")
            SPFilters.setFilters(requireActivity(), "ddd", "")
        }

        parentFragmentManager.setFragmentResult(PostsFragment.LISTENER_FILTERS, bundleOf())
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}