package com.example.olx.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.adapter.PostAdapter
import com.example.olx.databinding.FragmentFavoritesBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Favorite
import com.example.olx.model.Post
import com.example.olx.util.Contants.Companion.TAG
import com.example.olx.util.showBottomSheet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tsuryo.swipeablerv.SwipeLeftRightCallback

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val idsFavoritesList: MutableList<String> = mutableListOf()
    private val postList: MutableList<Post> = mutableListOf()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura os favoritos do firebase
        getIdsFavorites()
    }

    // Configura os favoritos do firebase
    private fun getIdsFavorites() {
        FirebaseHelper.getDatabase()
            .child("favorites")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    idsFavoritesList.clear()
                    for (ds in snapshot.children) {
                        val id = ds.getValue(String::class.java)
                        if (id != null) {
                            idsFavoritesList.add(id)
                        }
                    }

                    if (idsFavoritesList.size > 0) {
                        getPosts()
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.textInfo.text = "Nenhum anúncio favoritado."
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "onCancelled")
                }
            })
    }

    // Recupera posts do firebase
    private fun getPosts() {
        postList.clear()
        for (idPost in idsFavoritesList) {
            FirebaseHelper.getDatabase()
                .child("publicPosts")
                .child(idPost)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val post = snapshot.getValue(Post::class.java) as Post
                        postList.add(post)

                        if (postList.size == idsFavoritesList.size) {
                            binding.textInfo.text = ""
                            binding.progressBar.visibility = View.GONE

                            initRecycler()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i(TAG, "onCancelled")
                    }
                })
        }
    }

    // Configurações iniciais do RecyclerView
    private fun initRecycler() {
        binding.recyclerFavorites.layoutManager = LinearLayoutManager(activity)
        binding.recyclerFavorites.setHasFixedSize(true)
        postAdapter = PostAdapter(postList, requireContext()) { post ->
            val action =
                FavoritesFragmentDirections
                    .actionMenuFavoritesToDetailPostFragment(post)

            findNavController().navigate(action)
        }
        binding.recyclerFavorites.adapter = postAdapter

        binding.recyclerFavorites.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                showBottomSheet(
                    message = getString(R.string.text_delete_post_my_posts_fragment),
                    titleButton = R.string.text_button_delete_post_my_posts_fragment,
                    onClick = {
                        val post = postList[position]

                        idsFavoritesList.remove(post.id)
                        postList.remove(post)

                        if (postList.size == 0) binding.textInfo.text = "Nenhum anúncio favoritado"

                        val favorito = Favorite(idsFavoritesList)
                        favorito.salvar()

                        postAdapter.notifyItemRemoved(position)
                    }
                )
            }

            override fun onSwipedRight(position: Int) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}