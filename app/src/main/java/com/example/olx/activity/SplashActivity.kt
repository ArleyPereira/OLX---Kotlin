package com.example.olx.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.olx.R
import com.example.olx.Util.SPFiltro

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Full Screen
        fullScreen()

        // Leva o Usuário para tela principal
        Handler(Looper.getMainLooper()).postDelayed({ run { homeApp() } }, 1000)

        // Limpa todos os filtros
        SPFiltro.limpaFiltros(this)

    }

    // Leva o Usuário para tela principal
    private fun homeApp() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    // Full Screen
    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = window.insetsController
            if (insetsController != null) {
                window.insetsController!!.hide(WindowInsets.Type.statusBars())
            }
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

}