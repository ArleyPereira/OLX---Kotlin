package com.example.olx.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.olx.R
import com.example.olx.databinding.FragmentAccountBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.User
import com.example.olx.ui.home.HomeFragmentDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserProfile()

        initClicks()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
        binding.btnProfile.setOnClickListener {
            loginApp(R.id.action_menu_account_to_profileFragment)
        }

        binding.btnAddress.setOnClickListener {
            loginApp(R.id.action_menu_account_to_formAddressFragment)
        }

        binding.textAccount.setOnClickListener {
            if (FirebaseHelper.isAutenticated()) {
                FirebaseHelper.getAuth().signOut()
                configData()
            } else {
                loginApp()
            }
        }
    }

    // redireciona o usuário para tela de login
    private fun loginApp(destination: Int = 0) {
        if(FirebaseHelper.isAutenticated() && destination != 0){
            findNavController().navigate(destination)
        }else {
            findNavController().navigate(
                HomeFragmentDirections.actionGlobalVisitorFragment().actionId
            )
        }
    }

    // Recupera dados do perfil do usuário do firebase
    private fun getUserProfile() {
        if (FirebaseHelper.isAutenticated()) {
            FirebaseHelper.getDatabase()
                .child("usuarios")
                .child(FirebaseHelper.getIdUser())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        user = snapshot.getValue(User::class.java) as User
                        configData()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    // Configura as informações nos componentes em tela
    private fun configData() {
        if (FirebaseHelper.isAutenticated()) {
            if (user.urlProfile.isNotBlank()) {
                Picasso.get()
                    .load(user.urlProfile)
                    .placeholder(R.drawable.loading)
                    .into(binding.imgProfile)
            }
            binding.textName.text = user.name
            binding.textAccount.text = "Sair"
        } else {
            binding.textName.text = "Acesso sua conta agora!"
            binding.textAccount.text = "Clique aqui"
            binding.imgProfile.setImageResource(R.drawable.ic_user_cinza)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}