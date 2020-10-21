package com.example.olx.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.olx.R
import com.example.olx.autenticacao.LoginActivity

class ContaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_conta, container, false)

        // Ouvinte Cliques
        configCliques(view)

        return view
    }

    // Ouvinte Cliques
    private fun configCliques(view: View){
        view.findViewById<TextView>(R.id.textConta).setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }

}