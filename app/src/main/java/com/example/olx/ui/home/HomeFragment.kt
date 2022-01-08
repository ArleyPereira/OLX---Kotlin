package com.example.olx.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.adapter.PostAdapter
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
    private lateinit var postAdapter: PostAdapter

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
        binding.btnNewPost.setOnClickListener {
            if (FirebaseHelper.isAutenticated()) {
                val action = HomeFragmentDirections
                    .actionMenuHomeToFormPostFragment(null)
                findNavController().navigate(action)
            } else {
                findNavController().navigate(
                    HomeFragmentDirections.actionGlobalVisitorFragment().actionId
                )
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            getPosts()
        }
    }

    // Configurações iniciais do RecyclerView
    private fun initRecyclerView() {
        binding.rvAnuncios.layoutManager = LinearLayoutManager(activity)
        binding.rvAnuncios.setHasFixedSize(true)
        postAdapter = PostAdapter(anuncioList, requireContext()) { post ->
            val action = HomeFragmentDirections
                .actionMenuHomeToDetailPostFragment(post)

            findNavController().navigate(action)
        }
        binding.rvAnuncios.adapter = postAdapter
    }

    // Recupera anúncios do Firebase
    private fun getPosts() {
        FirebaseHelper.getDatabase()
            .child("publicPosts")
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

                    if (binding.swipeRefreshLayout.isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    anuncioList.reverse()
                    binding.progressBar.visibility = View.GONE
                    postAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}