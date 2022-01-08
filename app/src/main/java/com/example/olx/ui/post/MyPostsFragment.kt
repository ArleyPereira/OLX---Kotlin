package com.example.olx.ui.post

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.adapter.PostAdapter
import com.example.olx.databinding.FragmentMyPostsBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tsuryo.swipeablerv.SwipeLeftRightCallback

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    private var postList = mutableListOf<Post>()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurações inicias do RecyclerView
        initRecyclerView()

        // Recupera anúncios do Firebase
        getPosts()
    }

    // Configurações inicias do RecyclerView
    private fun initRecyclerView() {
        binding.recyclerPosts.layoutManager = LinearLayoutManager(activity)
        binding.recyclerPosts.setHasFixedSize(true)
        postAdapter = PostAdapter(postList, requireContext()) {}
        binding.recyclerPosts.adapter = postAdapter

        binding.recyclerPosts.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                removePost(postList[position])
            }

            override fun onSwipedRight(position: Int) {
                val action = MyPostsFragmentDirections
                    .actionMenuMeusAnunciosToFormPostFragment(postList[position])

                findNavController().navigate(action)
            }
        })
    }

    // Exibe Dialog para confirmar deleção do post
    private fun removePost(post: Post) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Deseja remover este anúncio ?")
        builder.setMessage("Aperte em sim para confirmar ou aperte em não para sair.")
        builder.setNegativeButton("Não") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            postAdapter.notifyDataSetChanged()
        }.setPositiveButton("Sim") { dialog: DialogInterface, _: Int ->
            postList.remove(post)
            post.remove()
            dialog.dismiss()
            postAdapter.notifyDataSetChanged()

            if (postList.isEmpty()) {
                binding.textInfo.text = "Você ainda não possui nenhum anúncio cadastrado."
            } else {
                binding.textInfo.text = ""
            }
        }
        builder.create().show()
    }

    // Recupera anúncios do Firebase
    private fun getPosts() {
        FirebaseHelper.getDatabase()
            .child("myPosts")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postList.clear()
                    if (snapshot.exists()) {
                        for (ds in snapshot.children) {
                            val anuncio = ds.getValue(Post::class.java) as Post
                            anuncio.let {
                                postList.add(it)
                            }
                        }
                        binding.textInfo.text = ""
                    } else {
                        binding.textInfo.text = "Você ainda não possui nenhum anúncio cadastrado."
                    }
                    postList.reverse()
                    binding.progressBar.visibility = View.GONE
                    postAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
