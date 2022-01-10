package com.example.olx.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import com.example.olx.R
import com.example.olx.api.Resource
import com.example.olx.databinding.FragmentFormAddressBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Address
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.example.olx.util.showBottomSheetInfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FormAddressFragment : BaseFragment() {

    private val addressViewModel: AddressViewModel by activityViewModels()

    private var _binding: FragmentFormAddressBinding? = null
    private val binding get() = _binding!!

    private var address: Address? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getAddress()
    }

    // Ouvinte de evento dos componentes
    private fun initListeners() {
        binding.btnSave.setOnClickListener { validData() }

        binding.editZipCode.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val zipCode = v.text.toString()
                    .replace("_", "")
                    .replace("-", "")

                if (zipCode.length == 8) {
                    hideKeyboard()
                    searchAddress(zipCode)
                    true
                } else {
                    showBottomSheetInfo(R.string.zip_code_invalid_save_post_form_post_fragment)
                    false
                }
            } else false
        }
    }

    // Recupera endereço do firebase
    private fun getAddress() {
        FirebaseHelper.getDatabase()
            .child("address")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        address = snapshot.getValue(Address::class.java) as Address
                        searchAddress(address!!.zipCode)
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showBottomSheetInfo(R.string.error_generic)
                }
            })
    }

    // Retorna o endereço do CEP informado
    private fun searchAddress(zipCode: String) {
        binding.progressBar.visibility = View.VISIBLE

        addressViewModel.getAddress(zipCode).observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.onSuccess -> {
                    address = resource.data
                    if (address?.city == null) {
                        address = null
                        binding.progressBar.visibility = View.GONE
                        showBottomSheetInfo(R.string.address_invalid_form_post_fragment)
                    }
                    configAddress()
                }
                is Resource.onFailure -> {
                    binding.progressBar.visibility = View.GONE
                    showBottomSheetInfo(R.string.address_invalid_form_post_fragment)
                }
            }
        })
    }

    // Exibe as informações do post nos componentes
    private fun configAddress() {
        binding.editState.setText(address?.state)
        binding.editCity.setText(address?.city)
        binding.editDistrict.setText(address?.district)

        binding.progressBar.visibility = View.GONE
    }

    // Valida se as informacoes foram preenchidas
    private fun validData() {
        val zipCode = binding.editZipCode.text.toString().trim()
        val state = binding.editState.text.toString().trim()
        val cicy = binding.editCity.text.toString().trim()
        val district = binding.editDistrict.text.toString().trim()

        if (zipCode.isNotEmpty()) {
            if(zipCode.length < 8){
                if (state.isNotEmpty()) {
                    if (cicy.isNotEmpty()) {
                        if (district.isNotEmpty()) {

                            hideKeyboard()

                            binding.progressBar.visibility = View.VISIBLE

                            address = Address(
                                zipCode = zipCode,
                                state = state,
                                city = cicy,
                                district = district
                            )
                            address?.save()

                            Snackbar.make(
                                binding.btnSave,
                                "Endereço salvo com sucesso.",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            binding.progressBar.visibility = View.GONE
                        } else {
                            showBottomSheetInfo(R.string.district_empty_form_address_fragment)
                        }
                    } else {
                        showBottomSheetInfo(R.string.city_empty_form_address_fragment)
                    }
                } else {
                    showBottomSheetInfo(R.string.state_empty_form_address_fragment)
                }
            }else {
                showBottomSheetInfo(R.string.zip_code_invalid_form_address_fragment)
            }
        } else {
            showBottomSheetInfo(R.string.zip_code_empty_form_address_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}