package com.example.olx.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.activity.FormAnuncioActivity
import com.example.olx.adapter.AdapterAnuncio
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Anuncio
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import kotlinx.android.synthetic.main.fragment_meus_anuncios.*
import kotlinx.android.synthetic.main.fragment_meus_anuncios.view.*

class MeusAnunciosFragment : Fragment(), AdapterAnuncio.OnClickListener {

    private var anuncioList = mutableListOf<Anuncio>()
    private lateinit var adapterAnuncio: AdapterAnuncio

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_meus_anuncios, container, false)

        adapterAnuncio = AdapterAnuncio(anuncioList, this, requireActivity())

        // Inicia RecyclerView
        configRv(view)

        return view

    }

    override fun onStart() {
        super.onStart()

        // Recupera anúncios do Firebase
        recuperaAnuncio()
    }

    // Inicia RecyclerView
    private fun configRv(view: View) {
        view.rvAnuncios.layoutManager = LinearLayoutManager(activity)
        view.rvAnuncios.setHasFixedSize(true)
        view.rvAnuncios.adapter = adapterAnuncio

        view.rvAnuncios.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                removerAnuncio(anuncioList[position])
            }

            override fun onSwipedRight(position: Int) {

                // Exibe Dialog para editar do anúncio
                editAnuncio(anuncioList[position])

            }
        })

    }

    // Exibe Dialog para editar do anúncio
    private fun editAnuncio(anuncio: Anuncio){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Deseja editar este anúncio ?")
        builder.setMessage("Aperte em sim para confirmar ou aperte em não para sair.")
        builder.setNegativeButton("Não") { dialog: DialogInterface, _: Int ->

            dialog.dismiss()
            adapterAnuncio.notifyDataSetChanged()

        }.setPositiveButton("Sim") { _: DialogInterface, _: Int ->

            val intent = Intent(activity, FormAnuncioActivity::class.java)
            intent.putExtra("anuncio", anuncio)
            startActivity(intent)

            adapterAnuncio.notifyDataSetChanged()

        }

        builder.create().show()
    }

    // Exibe Dialog para deleção do Anúncio
    private fun removerAnuncio(anuncio: Anuncio) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Deseja remover este anúncio ?")
        builder.setMessage("Aperte em sim para confirmar ou aperte em não para sair.")
        builder.setNegativeButton("Não") { dialog: DialogInterface, _: Int ->

            dialog.dismiss()
            adapterAnuncio.notifyDataSetChanged()

        }.setPositiveButton("Sim") { dialog: DialogInterface, _: Int ->

            anuncioList.remove(anuncio)
            anuncio.remover()
            dialog.dismiss()
            adapterAnuncio.notifyDataSetChanged()

            if(anuncioList.isEmpty()){
                textInfo.text = "Você ainda não possui nenhum anúncio cadastrado."
            }else {
                textInfo.text = ""
            }

        }

        builder.create().show()

    }

    // Recupera anúncios do Firebase
    private fun recuperaAnuncio() {
        if (FirebaseHelper.isAutenticated()) {

            val anuncioRef = FirebaseHelper.getDatabase()
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

        } else {
            progressBar.visibility = View.GONE
            textInfo.text = "Você não está autenticado no app."

            btnLogin.visibility = View.VISIBLE
            btnLogin.setOnClickListener {
                startActivity(
                    Intent(
                        activity,
                        LoginActivity::class.java
                    )
                )
            }
        }
    }

    override fun onItemClick(anuncio: Anuncio) {
    }

}
