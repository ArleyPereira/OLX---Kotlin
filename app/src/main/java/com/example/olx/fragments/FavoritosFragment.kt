package com.example.olx.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.adapter.AdapterAnuncio
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Post
import com.example.olx.model.Favorite
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import kotlinx.android.synthetic.main.fragment_favoritos.*
import kotlinx.android.synthetic.main.fragment_favoritos.view.*


class FavoritosFragment : Fragment(), AdapterAnuncio.OnClickListener {

    private val favoritos: MutableList<String> = mutableListOf()
    private val postList: MutableList<Post> = mutableListOf()
    private lateinit var adapterAnuncio: AdapterAnuncio

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favoritos, container, false)

        // Inicia RecyclerView
        configRv(view)

        return view
    }

    override fun onStart() {
        super.onStart()

        // Configura os favoritos do Firebase
        recuperaFavoritos()
    }

    // Configura os favoritos do Firebase
    private fun recuperaFavoritos() {
        if (FirebaseHelper.isAutenticated()) {
            val favoritoRef = FirebaseHelper.getDatabase()
                .child("favoritos")
                .child(FirebaseHelper.getIdUser())
            favoritoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    favoritos.clear()
                    for (ds in snapshot.children) {
                        val id = ds.getValue(String::class.java)

                        if (id != null) {
                            favoritos.add(id)
                        }
                    }

                    if (favoritos.size > 0) {
                        recuperaAnuncios()
                    } else {
                        progressBar.visibility = View.GONE
                        textInfo.text = "Nenhum anúncio favoritado."
                    }

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

    // Recupera Anúncios
    private fun recuperaAnuncios() {
        postList.clear()
        for (idAnuncio in favoritos) {
            val anunciosRef = FirebaseHelper.getDatabase()
                .child("anunciosPublicos")
                .child(idAnuncio)
            anunciosRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val anuncio = snapshot.getValue(Post::class.java)

                        if(anuncio != null){
                            postList.add(anuncio)
                        }

                        if(postList.size == favoritos.size){
                            textInfo.text = ""
                            progressBar.visibility = View.GONE
                            adapterAnuncio.notifyDataSetChanged()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

    }

    // Inicia RecyclerView
    private fun configRv(view: View){
        view.rvAnuncios.layoutManager = LinearLayoutManager(activity)
        view.rvAnuncios.setHasFixedSize(true)
        adapterAnuncio = AdapterAnuncio(postList, this, requireActivity())
        view.rvAnuncios.adapter = adapterAnuncio

        view.rvAnuncios.setListener(object : SwipeLeftRightCallback.Listener{
            override fun onSwipedLeft(position: Int) {
                removerAnuncio(postList[position])
            }

            override fun onSwipedRight(position: Int) {
            }

        })

    }

    // Exibe dialog para deleção do anúncio
    private fun removerAnuncio(post: Post){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Deseja remover este anúncio dos favoritos ?")
        builder.setMessage("Aperte em sim para confirmar ou aperte em não para sair.")
        builder.setNegativeButton("Não") { dialogInterface, _ ->
            dialogInterface.dismiss()
            adapterAnuncio.notifyDataSetChanged()
        }
        builder.setPositiveButton("Sim") { dialogInterface, _ ->
            favoritos.remove(post.id)
            postList.remove(post)

            if(postList.size == 0) textInfo.text = "Nenhum anúncio favoritado"

            val favorito = Favorite(favoritos)
            favorito.salvar()

            dialogInterface.dismiss()
            adapterAnuncio.notifyDataSetChanged()

        }

        val dialog = builder.create()
        dialog.show()

    }

    override fun onItemClick(post: Post) {
        val intent = Intent(activity, DetalheAnuncioActivity::class.java)
        intent.putExtra("anuncio", post)
        startActivity(intent)
    }

}