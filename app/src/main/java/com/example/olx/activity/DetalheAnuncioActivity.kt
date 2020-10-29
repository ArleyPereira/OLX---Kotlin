package com.example.olx.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.olx.R
import com.example.olx.Util.GetMask
import com.example.olx.adapter.SliderAdapter
import com.example.olx.autenticacao.LoginActivity
import com.example.olx.helper.GetFirebase
import com.example.olx.model.Anuncio
import com.example.olx.model.Favorito
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.like.LikeButton
import com.like.OnLikeListener
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_detalhe_anuncio.*
import kotlinx.android.synthetic.main.toolbar_favorito_ligar.*

class DetalheAnuncioActivity : AppCompatActivity() {

    private lateinit var anuncio: Anuncio
    private var favoritos: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_anuncio)

        anuncio = intent.getSerializableExtra("anuncio") as Anuncio

        // Configura os dados nos elementos
        configDados()

        // Ouvinte Cliques
        configCliques()

        // Recupera favoritos
        recuperaFavoritos()

        // Ouvinte Like Button
        configLikeButton()

    }

    // Recupera favoritos
    private fun recuperaFavoritos() {
        if (GetFirebase.getAutenticado()) {
            val favoritoRef = GetFirebase.getDatabase()
                .child("favoritos")
                .child(GetFirebase.getIdFirebase())
            favoritoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {

                        val id = ds.getValue(String::class.java)
                        if (id != null) {
                            favoritos.add(id)
                        }
                    }

                    if (favoritos.contains(anuncio.id)) {
                        likeButton.isLiked = true
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    // Ouvinte Cliques
    private fun configCliques() {
        ibVoltar.setOnClickListener { finish() }
        ibLigar.setOnClickListener { ligar() }
    }

    // Ouvinte Like Button
    private fun configLikeButton() {
        likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                if (GetFirebase.getAutenticado()) {
                    configSnackBar(
                        "DESFAZER",
                        "Anúncio salvo",
                        R.drawable.ic_favorito_like_red, true
                    )

                } else {
                    alertaAutenticacao()
                }
            }

            override fun unLiked(likeButton: LikeButton?) {
                configSnackBar(
                    "",
                    "Anúncio removido",
                    R.drawable.ic_favorito_unlike, false
                )
            }

        })

    }

    // Salva o Anúncio como favorito
    // e Exibe SnackBar Custom
    private fun configSnackBar(actionMsg: String, msg: String, icon: Int, like: Boolean) {

        // Configura status do Firito
        configFavorito(like)

        val snackbar = Snackbar.make(likeButton, msg, Snackbar.LENGTH_SHORT)
        snackbar.setAction(actionMsg) {
            // Configura status do Firito
            configFavorito(false)
        }

        val textView = snackbar.view.findViewById<TextView>(R.id.snackbar_text)
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        textView.compoundDrawablePadding = 24
        snackbar.setActionTextColor(Color.parseColor("#F78323"))
            .setTextColor(Color.parseColor("#FFFFFF"))
            .show()

    }

    // Configura status do Firito
    private fun configFavorito(like: Boolean) {

        likeButton.isLiked = like

        if (like) {
            favoritos.add(anuncio.id)
        } else {
            favoritos.remove(anuncio.id)
        }

        val favorito = Favorito(favoritos)
        favorito.salvar()

    }

    // Abre o aplicativo de chamadas do aparelho
    private fun ligar() {
        if (GetFirebase.getAutenticado()) {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", anuncio.telefone, null)
            )
            startActivity(intent)
        } else {
            alertaAutenticacao()
        }
    }

    // Dialog Usuário não ligado
    private fun alertaAutenticacao() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atenção")
        builder.setMessage("Para entrar em contato com anunciantes é preciso está logado no app.")
        builder.setCancelable(false)
        builder.setNegativeButton("Entendi", null)
        builder.setPositiveButton(
            "Fazer login"
        ) { _: DialogInterface?, _: Int ->
            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
        }

        val dialog = builder.create()
        dialog.show()
    }

    // Configura os dados nos elementos
    private fun configDados() {

        // Buttons Toolbar
        if (GetFirebase.getAutenticado()) {
            if (GetFirebase.getIdFirebase() == anuncio.idUsuario) {
                ibLigar.visibility = View.GONE
                likeButton.visibility = View.GONE
            }
        }

        // Adapter imagens
        sliderView.setSliderAdapter(SliderAdapter(anuncio.urlFotos))
        sliderView.startAutoCycle()
        sliderView.scrollTimeInSec = 4
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)

        textPrice.text = getString(R.string.valor_anuncio, GetMask.getValor(anuncio.preco))
        textTitle.text = anuncio.titulo
        textDescription.text = anuncio.descricao
        textPublicado.text = getString(
            R.string.publicacao_detalhe,
            GetMask.getDate(anuncio.dataCadastro, GetMask.DIA_MES_HORA)
        )
        textCagegory.text = anuncio.categoria
        textCep.text = anuncio.local.cep
        textMunicipio.text = anuncio.local.localidade
        textBairro.text = anuncio.local.bairro

    }

}