package com.example.olx.ui.post

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.adapter.PostAdapter
import com.example.olx.databinding.FragmentPostsBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Post
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PostsFragment : BaseFragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private var postList = mutableListOf<Post>()
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar, false)

        // Recupera anúncios do Firebase
        getPosts()

        // Ouvinte Cliques dos componentes
        initClicks()
    }

    // Ouvinte Cliques dos componentes
    private fun initClicks() {
        binding.btnNewPost.setOnClickListener {
            if (FirebaseHelper.isAutenticated()) {
                val action =
                    PostsFragmentDirections.actionMenuHomeToFormPostFragment(
                        null
                    )
                findNavController().navigate(action)
            } else {
                findNavController().navigate(
                    PostsFragmentDirections.actionGlobalVisitorFragment().actionId
                )
            }
        }

        binding.btnCategories.setOnClickListener {
            findNavController().navigate(R.id.action_menu_home_to_categoriesFragment)
        }

        binding.btnRegions.setOnClickListener {
            findNavController().navigate(R.id.action_menu_home_to_regionsFragment)
        }

        binding.btnFilters.setOnClickListener {
            findNavController().navigate(R.id.action_menu_home_to_filtersFragment)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            getPosts()
        }

        binding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                hideKeyboard()
                if (query.isNotEmpty()) {
                    searchPosts(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })

        binding.searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                initRecyclerView(postList)
            }

            override fun onSearchViewClosedAnimation() {
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
            }
        })
    }

    // Configurações iniciais do RecyclerView
    private fun initRecyclerView(postList: List<Post>) {
        postListEmpty(postList)

        binding.rvAnuncios.layoutManager = LinearLayoutManager(activity)
        binding.rvAnuncios.setHasFixedSize(true)
        postAdapter = PostAdapter(postList.reversed(), requireContext()) { post ->
            val action =
                PostsFragmentDirections.actionMenuHomeToDetailPostFragment(
                    post
                )

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
                    postList.clear()
                    if (snapshot.exists()) {
                        for (ds in snapshot.children) {
                            val post = ds.getValue(Post::class.java) as Post
                            post.let {
                                postList.add(it)
                            }
                        }

                        // Configurações iniciais do RecyclerView
                        initRecyclerView(postList)

                        binding.textInfo.text = ""
                    } else binding.textInfo.text = "Nenhum anúncio cadastrado."

                    if (binding.swipeRefreshLayout.isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    binding.progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun searchPosts(query: String) {
        val postList = postList.filter { it.title.contains(query, ignoreCase = true) }
        initRecyclerView(postList)

        postListEmpty(postList, query)
    }

    private fun postListEmpty(postList: List<Post>, query: String? = "") {
        binding.textInfo.text = if (postList.isEmpty()) {
            getString(R.string.post_not_exists, query)
        } else {
            ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_pots, menu)
        val item = menu.findItem(R.id.action_search)
        binding.searchView.setMenuItem(item)
        super.onCreateOptionsMenu(menu, inflater)
    }

}