package com.example.olx.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.activity.DetalheAnuncioActivity
import com.example.olx.activity.FormAnuncioActivity
import com.example.olx.adapter.AdapterAnuncio
import com.example.olx.autenticacao.LoginActivity
import com.example.olx.helper.GetFirebase
import com.example.olx.model.Anuncio
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment(), AdapterAnuncio.OnClickListener {

    private var anuncioList = mutableListOf<Anuncio>()
    private lateinit var adapterAnuncio: AdapterAnuncio

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inicia RecyclerView
        configRv(view)

        // Recupera anúncios do Firebase
        recuperaAnuncio()

        // Ouvinte Cliques
        configCliques(view)

        return view

    }

    // Inicia RecyclerView
    private fun configRv(view: View) {
        view.rvAnuncios.layoutManager = LinearLayoutManager(activity)
        view.rvAnuncios.setHasFixedSize(true)
        adapterAnuncio = AdapterAnuncio(anuncioList, this, requireActivity())
        view.rvAnuncios.adapter = adapterAnuncio
    }

    // Recupera anúncios do Firebase
    private fun recuperaAnuncio() {
        val anuncioRef = GetFirebase.getDatabase()
            .child("anunciosPublicos")
        anuncioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                anuncioList.clear()
                if (snapshot.exists()) {

                    for (ds in snapshot.children) {
                        val anuncio = ds.getValue(Anuncio::class.java) as Anuncio
                        anuncio.let {
                            anuncioList.add(it)
                        }

                    }

                    textInfo.text = ""

                } else {
                    textInfo.text = "Você ainda não possui nenhum anúncio cadastrado."
                }

                anuncioList.reverse()
                progressBar.visibility = View.GONE
                adapterAnuncio.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // Ouvinte Cliques
    private fun configCliques(view: View){
        view.btnInserir.setOnClickListener {
            if(GetFirebase.getAutenticado()){
                startActivity(Intent(activity, FormAnuncioActivity::class.java))
            }else {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
    }

    override fun onItemClick(anuncio: Anuncio) {
        val intent = Intent(activity, DetalheAnuncioActivity::class.java)
        intent.putExtra("anuncio", anuncio)
        startActivity(intent)
    }

}