package com.example.olx.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.olx.R
import com.example.olx.Util.GetMask
import com.example.olx.adapter.SliderAdapter
import com.example.olx.helper.GetFirebase
import com.example.olx.model.Anuncio
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_detalhe_anuncio.*
import kotlinx.android.synthetic.main.toolbar_favorito_ligar.*

class DetalheAnuncioActivity : AppCompatActivity() {

    private lateinit var anuncio: Anuncio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_anuncio)

        anuncio = intent.getSerializableExtra("anuncio") as Anuncio

        // Configura os dados nos elementos
        configDados()

        // Ouvinte Cliques
        configCliques()

    }

    // Ouvinte Cliques
    private fun configCliques(){
        ibVoltar.setOnClickListener { finish() }
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