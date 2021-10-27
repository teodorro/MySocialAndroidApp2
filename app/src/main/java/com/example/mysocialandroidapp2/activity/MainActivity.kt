package com.example.mysocialandroidapp2.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.Navigation
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.viewmodel.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {
    @Inject
    lateinit var appAuth: AppAuth
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
//    @Inject
//    lateinit var googleApiAvailability: GoogleApiAvailability

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        authViewModel.moveToAuthEvent.observe(this) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_authFragment)
        }

        authViewModel.moveToSignUpEvent.observe(this) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_regFragment)
        }

        authViewModel.signOutEvent.observe(this) {
            authViewModel.signOut()
        }

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful)
                Log.d(null, "Something wrong happened: ${task.exception}")
            val token = task.result
            Log.d(null, token)
        }
    }

    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)
        menu?.let {
            it.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                authViewModel.moveToAuthInvoke()
                true
            }
            R.id.signup -> {
                authViewModel.signUpInvoke()
                true
            }
            R.id.signout -> {
                authViewModel.signOutInvoke()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}