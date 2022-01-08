package com.example.olx.ui.post

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.olx.MainGraphDirections
import com.example.olx.R
import com.example.olx.adapter.SliderAdapter
import com.example.olx.databinding.FragmentDetailPostBinding
import com.example.olx.helper.FirebaseHelper
import com.example.olx.model.Favorite
import com.example.olx.model.Post
import com.example.olx.util.GetMask
import com.example.olx.util.initToolbar
import com.example.olx.util.showBottomSheetInfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.like.LikeButton
import com.like.OnLikeListener
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations

class DetailPostFragment : Fragment() {

    private val args: DetailPostFragmentArgs by navArgs()

    private var _binding: FragmentDetailPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var post: Post
    private var favoriteList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        getExtra()

        getFavorites()

        configLikeButton()
    }

    // Recupera o objeto usuário selecionado
    private fun getExtra() {
        post = args.post

        binding.titleToolbar.text = post.title

        initData()
    }

    // Recupera favoritos do firebase e verifica se este post se está contido na lista recuperada
    private fun getFavorites() {
        if (FirebaseHelper.isAutenticated()) {
            val favoritoRef = FirebaseHelper.getDatabase()
                .child("favorites")
                .child(FirebaseHelper.getIdUser())
            favoritoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {

                        val id = ds.getValue(String::class.java)
                        if (id != null) {
                            favoriteList.add(id)
                        }
                    }

                    if (favoriteList.contains(post.id)) {
                        binding.likeButton.isLiked = true
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    showBottomSheetInfo(R.string.error_generic)
                }

            })
        }
    }

    // Ouvinte Like Button
    private fun configLikeButton() {
        binding.likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                if (FirebaseHelper.isAutenticated()) {
                    configSnackBar(
                        "DESFAZER",
                        "Anúncio salvo",
                        R.drawable.ic_like, true
                    )
                } else {
                    val action = MainGraphDirections
                        .actionGlobalVisitorFragment()
                    findNavController().navigate(action.actionId)
                    likeButton?.isLiked = false
                }
            }

            override fun unLiked(likeButton: LikeButton?) {
                configSnackBar(
                    "",
                    "Anúncio removido",
                    R.drawable.ic_unlike, false
                )
            }

        })

    }

    // Salva o Anúncio como favorito
    // e Exibe SnackBar Custom
    private fun configSnackBar(actionMsg: String, msg: String, icon: Int, like: Boolean) {
        // Configura status do Firito
        configFavorite(like)

        val snackbar = Snackbar.make(binding.likeButton, msg, Snackbar.LENGTH_SHORT)
        snackbar.setAction(actionMsg) {
            // Configura status do Firito
            configFavorite(false)
        }

        val textView = snackbar.view.findViewById<TextView>(R.id.snackbar_text)
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        textView.compoundDrawablePadding = 24
        snackbar.setActionTextColor(Color.parseColor("#F78323"))
            .setTextColor(Color.parseColor("#FFFFFF"))
            .show()

    }

    // Configura status do Firito
    private fun configFavorite(like: Boolean) {
        binding.likeButton.isLiked = like

        if (like) {
            favoriteList.add(post.id)
        } else {
            favoriteList.remove(post.id)
        }

        val favorite = Favorite(favoriteList)
        favorite.salvar()
    }

    // Abre o aplicativo de chamadas do aparelho
    private fun call() {
        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.fromParts("tel", post.phone, null)
        )
        startActivity(intent)
    }

    // Configura as informações nos componentes em tela
    private fun initData() {
        binding.sliderView.setSliderAdapter(SliderAdapter(post.urlImages))
        binding.sliderView.startAutoCycle()
        binding.sliderView.scrollTimeInSec = 4
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)

        binding.textPrice.text = getString(R.string.valor_anuncio, GetMask.getValor(post.price))
        binding.textTitle.text = post.title
        binding.textDescription.text = post.description
        binding.textPublicado.text = getString(
            R.string.publicacao_detalhe,
            GetMask.getDate(post.registrationDate, GetMask.DIA_MES_HORA)
        )
        binding.textCagegory.text = post.category
        binding.textCep.text = post.state?.cep
        binding.textMunicipio.text = post.state?.localidade
        binding.textBairro.text = post.state?.bairro
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_call, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_call) {
            call()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}