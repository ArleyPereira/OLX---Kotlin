package com.example.olx.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.olx.R
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_conta.*

class ContaFragment : Fragment() {

    private lateinit var textConta: TextView
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_conta, container, false)

        // Inicia componentes de tela
        iniciaComponentes(view)

        // Ouvinte Cliques
        configCliques(view)

        return view
    }

    override fun onStart() {
        super.onStart()

        // Recupera dados do Perfil
        recuperaDados()
    }

    // Recupera dados do Perfil
    private fun recuperaDados() {
        if(FirebaseHelper.isAutenticated()){
            val usuarioRef = FirebaseHelper.getDatabase()
                .child("usuarios")
                .child(FirebaseHelper.getIdUser())
            usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    user = snapshot.getValue(User::class.java) as User

                    // Configura as informações nos elementos
                    configDados()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    // Configura as informações nos elementos
    private fun configDados() {

        if(FirebaseHelper.isAutenticated()){

            if (user.urlImagem.isNotBlank()) {
                Picasso.get()
                    .load(user.urlImagem)
                    .placeholder(R.drawable.loading)
                    .into(imagemPerfil)
            }

            textNome.text = user.nome
            textConta.text = "Sair"
        }else {
            textNome.text = "Acesso sua conta agora!"
            textConta.text = "Clique aqui"
            imagemPerfil.setImageResource(R.drawable.ic_user_cinza)
        }

    }

    // Ouvinte Cliques
    private fun configCliques(view: View){
        view.findViewById<TextView>(R.id.btnPerfil).setOnClickListener {
            if(FirebaseHelper.isAutenticated()){
                startActivity(Intent(activity, PerfilActivity::class.java))
            }else {
                fazerLogin() // Leva o Usuário para tela de login
            }
        }
        view.findViewById<TextView>(R.id.btnEndereco).setOnClickListener {
            if(FirebaseHelper.isAutenticated()){
                startActivity(Intent(activity, FormEnderecoActivity::class.java))
            }else {
                fazerLogin() // Leva o Usuário para tela de login
            }
        }
        textConta.setOnClickListener {

            if(FirebaseHelper.isAutenticated()){

                // Desloga o Usuário do App
                FirebaseHelper.getAuth().signOut()

                // Configura as informações nos elementos
                configDados()

            }else {
                fazerLogin() // Leva o Usuário para tela de login
            }

        }
    }

    // Leva o Usuário para tela de login
    private fun fazerLogin(){
        startActivity(Intent(activity, LoginActivity::class.java))
    }

    // Inicia componentes de tela
    private fun iniciaComponentes(view: View){
        textConta = view.findViewById(R.id.textConta)
    }

}