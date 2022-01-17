package com.example.olx.ui.filters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.adapter.StateAdapter
import com.example.olx.databinding.FragmentStatesBinding
import com.example.olx.helper.SPFilters
import com.example.olx.model.State
import com.example.olx.ui.post.PostsFragment
import com.example.olx.util.RegionsList
import com.example.olx.util.initToolbar

class StatesFragment : Fragment() {

    private var _binding: FragmentStatesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initRecyclerView()

        listenerFilters()
    }

    private fun initRecyclerView() {
        binding.rvStates.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStates.setHasFixedSize(true)
        binding.rvStates.adapter = StateAdapter(RegionsList.getStatesList()) { state ->
            openRegions(state)
        }
    }

    private fun openRegions(state: State) {
        if (state.name != "Brasil") {
            SPFilters.setFilters(requireActivity(), "stateName", state.name)
            SPFilters.setFilters(requireActivity(), "stateUF", state.uf)

            findNavController().navigate(R.id.action_statesFragment_to_regionsFragment)
        } else {
            SPFilters.setFilters(requireActivity(), "stateName", "")
            SPFilters.setFilters(requireActivity(), "stateUF", "")
            SPFilters.setFilters(requireActivity(), "region", "")
            SPFilters.setFilters(requireActivity(), "ddd", "")

            findNavController().popBackStack()
        }
    }

    private fun listenerFilters() {
        parentFragmentManager.setFragmentResultListener(
            PostsFragment.LISTENER_FILTERS, this,
            { _, _ ->
                findNavController().popBackStack()
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}