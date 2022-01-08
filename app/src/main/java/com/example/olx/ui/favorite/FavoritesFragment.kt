package com.example.olx.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.adapter.PostAdapter
import com.example.olx.databinding.FragmentFavoritesBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Favorite
import com.example.olx.model.Post
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

        // Configurações iniciais do RecyclerView
        initRecycler()

        // Configura os favoritos do firebase
        getIdsFavorites()
    }

    // Configura os favoritos do firebase
    private fun getIdsFavorites() {
        FirebaseHelper.getDatabase()
            .child("favoritos")
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
                    TODO("Not yet implemented")
                }
            })
    }

    // Recupera posts do firebase
    private fun getPosts() {
        postList.clear()
        for (idAnuncio in idsFavoritesList) {
            FirebaseHelper.getDatabase()
                .child("anunciosPublicos")
                .child(idAnuncio)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val anuncio = snapshot.getValue(Post::class.java)

                        if (anuncio != null) {
                            postList.add(anuncio)
                        }

                        if (postList.size == idsFavoritesList.size) {
                            binding.textInfo.text = ""
                            binding.progressBar.visibility = View.GONE
                            postAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    // Configurações iniciais do RecyclerView
    private fun initRecycler() {
        binding.recyclerFavorites.layoutManager = LinearLayoutManager(activity)
        binding.recyclerFavorites.setHasFixedSize(true)
        postAdapter = PostAdapter(postList, requireContext()){}
        binding.recyclerFavorites.adapter = postAdapter

        binding.recyclerFavorites.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                removePost(postList[position])
            }

            override fun onSwipedRight(position: Int) {
            }
        })
    }

    // Exibe dialog para confirmar deleção do post
    private fun removePost(post: Post) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Deseja remover este anúncio dos favoritos ?")
        builder.setMessage("Aperte em sim para confirmar ou aperte em não para sair.")
        builder.setNegativeButton("Não") { dialogInterface, _ ->
            dialogInterface.dismiss()
            postAdapter.notifyDataSetChanged()
        }
        builder.setPositiveButton("Sim") { dialogInterface, _ ->
            idsFavoritesList.remove(post.id)
            postList.remove(post)

            if (postList.size == 0) binding.textInfo.text = "Nenhum anúncio favoritado"

            val favorito = Favorite(idsFavoritesList)
            favorito.salvar()

            dialogInterface.dismiss()
            postAdapter.notifyDataSetChanged()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}