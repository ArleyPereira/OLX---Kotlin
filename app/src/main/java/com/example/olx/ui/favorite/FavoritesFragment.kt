package com.example.olx.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.adapter.AdapterPost
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
    private lateinit var adapterPost: AdapterPost

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
        if (FirebaseHelper.isAutenticated()) {
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
        } else {
            binding.progressBar.visibility = View.GONE
            binding.textInfo.text = "Você não está autenticado no app."

            binding.btnLogin.visibility = View.VISIBLE
            binding.btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_menu_favorites_to_navigation)
            }
        }
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
                            adapterPost.notifyDataSetChanged()
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
        adapterPost = AdapterPost(postList, requireContext()){}
        binding.recyclerFavorites.adapter = adapterPost

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
            adapterPost.notifyDataSetChanged()
        }
        builder.setPositiveButton("Sim") { dialogInterface, _ ->
            idsFavoritesList.remove(post.id)
            postList.remove(post)

            if (postList.size == 0) binding.textInfo.text = "Nenhum anúncio favoritado"

            val favorito = Favorite(idsFavoritesList)
            favorito.salvar()

            dialogInterface.dismiss()
            adapterPost.notifyDataSetChanged()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}