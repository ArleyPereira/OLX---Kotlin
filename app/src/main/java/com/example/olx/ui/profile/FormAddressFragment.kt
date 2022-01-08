package com.example.olx.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.olx.databinding.FragmentFormAddressBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.State
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FormAddressFragment : BaseFragment() {

    private var _binding: FragmentFormAddressBinding? = null
    private val binding get() = _binding!!

    private lateinit var state: State
    private var newAddress: Boolean = true

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

        initClicks()

        getAddress()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
        binding.btnSalvar.setOnClickListener { validData() }
    }

    // Recupera endereço do firebase
    private fun getAddress() {
        FirebaseHelper.getDatabase()
            .child("enderecos")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        state = snapshot.getValue(State::class.java) as State
                        configData()
                        newAddress = false
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    // Exibe as informações do post nos componentes
    private fun configData() {
        binding.editCep.setText(state.cep)
        binding.editEstado.setText(state.uf)
        binding.editCidade.setText(state.localidade)
        binding.editBairro.setText(state.bairro)

        binding.progressBar.visibility = View.GONE
    }

    // Valida se as informacoes foram preenchidas
    private fun validData() {
        val cep = binding.editCep.text.toString().trim()
        val uf = binding.editEstado.text.toString().trim()
        val cidade = binding.editCidade.text.toString().trim()
        val bairro = binding.editBairro.text.toString().trim()

        if (cep.isNotEmpty()) {
            if (uf.isNotEmpty()) {
                if (cidade.isNotEmpty()) {
                    if (bairro.isNotEmpty()) {

                        hideKeyboard()

                        binding.progressBar.visibility = View.VISIBLE

                        if (newAddress) state = State(
                            cep = cep,
                            uf = uf,
                            localidade = cidade,
                            bairro = bairro
                        )
                        //address.salvar(FirebaseHelper.getIdUser())

                        Snackbar.make(
                            binding.btnSalvar,
                            "Endereço salvo com sucesso.",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        binding.progressBar.visibility = View.GONE
                    } else {
                        binding.editBairro.requestFocus()
                        binding.editBairro.error = "Informe o bairro."
                    }
                } else {
                    binding.editCidade.requestFocus()
                    binding.editCidade.error = "Informe a cidade."
                }
            } else {
                binding.editEstado.requestFocus()
                binding.editEstado.error = "Informe o Estado."
            }
        } else {
            binding.editCep.requestFocus()
            binding.editCep.error = "Informe o CEP."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}