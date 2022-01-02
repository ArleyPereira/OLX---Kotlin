package com.example.olx.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.adapter.AdapterAnuncio
import com.example.olx.databinding.FragmentHomeBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var anuncioList = mutableListOf<Post>()
    private lateinit var adapterAnuncio: AdapterAnuncio

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurações iniciais do RecyclerView
        initRecyclerView()

        // Recupera anúncios do Firebase
        getPosts()

        // Ouvinte Cliques dos componentes
        initClicks()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
//        binding.btnInserir.setOnClickListener {
//            if (FirebaseHelper.isAutenticated()) {
//                startActivity(Intent(activity, FormAnuncioActivity::class.java))
//            } else {
//                startActivity(Intent(activity, LoginActivity::class.java))
//            }
//        }
    }

    // Configurações iniciais do RecyclerView
    private fun initRecyclerView() {
        binding.rvAnuncios.layoutManager = LinearLayoutManager(activity)
        binding.rvAnuncios.setHasFixedSize(true)
        adapterAnuncio = AdapterAnuncio(anuncioList, requireContext()) { post ->
            Toast.makeText(requireContext(), post.title, Toast.LENGTH_SHORT).show()
        }
        binding.rvAnuncios.adapter = adapterAnuncio
    }

    // Recupera anúncios do Firebase
    private fun getPosts() {
        FirebaseHelper.getDatabase()
            .child("anunciosPublicos")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    anuncioList.clear()
                    if (snapshot.exists()) {
                        for (ds in snapshot.children) {
                            val anuncio = ds.getValue(Post::class.java) as Post
                            anuncio.let {
                                anuncioList.add(it)
                            }
                        }
                        binding.textInfo.text = ""
                    } else binding.textInfo.text = "Nenhum anúncio cadastrado."

                    anuncioList.reverse()
                    binding.progressBar.visibility = View.GONE
                    adapterAnuncio.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}