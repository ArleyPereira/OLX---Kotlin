package com.example.olx.ui.post

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.adapter.PostAdapter
import com.example.olx.databinding.FragmentPostsBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Post
import com.example.olx.util.BaseFragment
import com.example.olx.util.Contants.Companion.TAG
import com.example.olx.util.initToolbar
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PostsFragment : BaseFragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val postList = mutableListOf<Post>()
    private var postListFilter = mutableListOf<Post>()

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

        getPosts()

        initListeners()
    }

    private fun initListeners() {
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

        binding.swipeRefreshLayout.setOnRefreshListener {
            getPosts()
        }

        binding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    filterNamePosts(query)
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

    private fun filterNamePosts(query: String) {
        hideKeyboard()

        postListFilter =
            postList.filter { it.title.contains(query, true) }.toMutableList()
        initRecyclerView(postListFilter)
    }

    private fun initRecyclerView(postList: List<Post>) {
        listEmpty(postList)

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
                            postList.add(post)
                        }
                    }

                    initRecyclerView(postList)

                    if (binding.swipeRefreshLayout.isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "onCancelled")
                }
            })
    }

    private fun listEmpty(postList: List<Post>) {
        binding.textInfo.text = if (postList.isEmpty()) {
            getString(R.string.post_not_exists)
        } else {
            ""
        }
        binding.progressBar.visibility = View.GONE
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