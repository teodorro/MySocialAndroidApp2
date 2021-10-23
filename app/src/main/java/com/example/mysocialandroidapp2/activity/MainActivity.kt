package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)
        menu?.let {
//            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
//            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
            it.setGroupVisible(R.id.unauthenticated, true)
            it.setGroupVisible(R.id.authenticated, false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_authFragment)
                true
            }
            R.id.signup -> {
                true
            }
            R.id.signout -> {
//                viewModel.signOutInvoke()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}