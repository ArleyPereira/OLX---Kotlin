package com.example.olx.ui.post

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olx.R
import com.example.olx.adapter.PostAdapter
import com.example.olx.databinding.FragmentPostsBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.helper.SPFilters
import com.example.olx.model.Category
import com.example.olx.model.Post
import com.example.olx.ui.filters.CategoriesFragment
import com.example.olx.util.BaseFragment
import com.example.olx.util.initToolbar
import com.example.olx.util.showBottomSheetInfo
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class PostsFragment : BaseFragment() {

    private val TAG = "INFOTESTE"

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val postList = mutableListOf<Post>()
    private var categorySelected: String = ""

    private lateinit var postAdapter: PostAdapter

    private var clearSearch: Boolean = true

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

        initListeners()

        showFilters()
    }

    private fun initListeners() {
        clearSearch = true

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

        binding.btnCategory.setOnClickListener {
            val action = PostsFragmentDirections
                .actionMenuHomeToCategoriesFragment(true)

            findNavController().navigate(action)
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
                if (query.isNotEmpty()) {
                    hideKeyboard()
                    SPFilters.setFilters(requireActivity(), "search", query)
                    checkFilterPoster()
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
                if (clearSearch) {
                    SPFilters.setFilters(requireActivity(), "search", "")
                    checkFilterPoster()
                } else {
                    SPFilters.setFilters(
                        requireActivity(),
                        "search",
                        SPFilters.getFilters(requireActivity()).search
                    )
                }
            }

            override fun onSearchViewClosedAnimation() {
            }

            override fun onSearchViewShown() {
                if (clearSearch) {
                    binding.searchView.showSearch()
                    binding.searchView.setQuery(
                        SPFilters.getFilters(requireActivity()).search,
                        false
                    )
                }
            }

            override fun onSearchViewShownAnimation() {
            }
        })
    }

    private fun showFilters() {
        if (SPFilters.isFilter(requireActivity())) {
            val filters = SPFilters.getFilters(requireActivity())

            if (filters.category.isNotEmpty()) {
                binding.btnCategory.text = filters.category
            }

        }
    }

    override fun onPause() {
        super.onPause()
        clearSearch = false
        binding.searchView.closeSearch()
    }

    // Recupera a categoria selecionada para o post
    private fun listenerSelectcCategory() {
        parentFragmentManager.setFragmentResultListener(
            CategoriesFragment.SELECT_CATEGORY,
            this,
            { _, bundle ->
                val category =
                    bundle.getParcelable<Category>(CategoriesFragment.SELECT_CATEGORY)

                categorySelected = category?.name.toString()
                binding.btnCategory.text = categorySelected
            })
    }

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
                            postList.add(post)
                        }
                    }

                    checkFilterPoster()

                    if (binding.swipeRefreshLayout.isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showBottomSheetInfo(R.string.error_generic)
                }
            })
    }

    private fun checkFilterPoster() {
        if (SPFilters.isFilter(requireActivity())) {
            val filterPosts = mutableListOf<Post>()
            filterPosts.addAll(postList)

            // Filtra pelo nome
            if (SPFilters.getFilters(requireActivity()).search.isNotEmpty()) {
                for (post in ArrayList(filterPosts)) {
                    if (!post.title.contains(
                            SPFilters.getFilters(requireActivity()).search,
                            true
                        )
                    ) {
                        filterPosts.remove(post)
                    }
                }
            }

            // Filtra pela categoria
            if (SPFilters.getFilters(requireActivity()).category.isNotEmpty()) {
                for (post in ArrayList(filterPosts)) {
                    if (!post.category.contains(
                            SPFilters.getFilters(requireActivity()).category,
                            true
                        )
                    ) {
                        filterPosts.remove(post)
                    }
                }
            }

            initRecyclerView(filterPosts)
        } else {
            initRecyclerView(postList)
        }
    }

    private fun postListEmpty(postList: List<Post>) {
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