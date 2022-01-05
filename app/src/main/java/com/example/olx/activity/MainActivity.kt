package com.example.olx.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.olx.MainGraphDirections
import com.example.olx.R
import com.example.olx.databinding.ActivityMainBinding
import com.example.olx.helper.FirebaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.btnv, navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            if (destination.id == R.id.menu_home ||
                destination.id == R.id.menu_my_posts ||
                destination.id == R.id.menu_favorites ||
                destination.id == R.id.menu_account
            ) {
                if (destination.id == R.id.menu_my_posts || destination.id == R.id.menu_favorites) {
                    if(!FirebaseHelper.isAutenticated()){
                        val action = MainGraphDirections
                            .actionGlobalVisitorFragment().actionId
                        controller.navigate(action, bundleOf("enable_toolbar" to false))
                    }
                }
                binding.btnv.visibility = View.VISIBLE
            }else {
                binding.btnv.visibility = View.GONE
            }
        }
    }
}