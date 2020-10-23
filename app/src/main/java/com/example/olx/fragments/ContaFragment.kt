package com.example.olx.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.olx.R
import com.example.olx.activity.PerfilActivity
import com.example.olx.autenticacao.LoginActivity
import com.example.olx.helper.GetFirebase
import kotlinx.android.synthetic.main.fragment_conta.*

class ContaFragment : Fragment() {

    private lateinit var textConta: TextView

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

        // Verifica se o Usuário está autenticado
        configInfo()

        return view
    }

    // Verifica se o Usuário está autenticado
    private fun configInfo(){
        if(GetFirebase.getAutenticado()){
            textConta.text = "Sair"
        }else {
            textConta.text = "Clique aqui"
        }
    }

    // Ouvinte Cliques
    private fun configCliques(view: View){
        view.findViewById<TextView>(R.id.btnPerfil).setOnClickListener {
            if(GetFirebase.getAutenticado()){
                startActivity(Intent(activity, PerfilActivity::class.java))
            }else {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
        textConta.setOnClickListener {

            if(GetFirebase.getAutenticado()){

                // Desloga o Usuário do App
                GetFirebase.getAuth().signOut()

                // Verifica se o Usuário está autenticado
                configInfo()

            }else {
                startActivity(Intent(activity, LoginActivity::class.java))
            }

        }
    }

    // Inicia componentes de tela
    private fun iniciaComponentes(view: View){
        textConta = view.findViewById(R.id.textConta)
    }

}