package com.example.olx.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.olx.R
import com.example.olx.activity.FormAnuncioActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Ouvinte Cliques
        configCliques(view)

        return view

    }

    // Ouvinte Cliques
    private fun configCliques(view: View){
        view.btnInserir.setOnClickListener { startActivity(Intent(activity, FormAnuncioActivity::class.java)) }
    }

}