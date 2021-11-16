package com.example.mysocialandroidapp2.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.mysocialandroidapp2.R
import com.example.mysocialandroidapp2.auth.AppAuth
import com.example.mysocialandroidapp2.databinding.ActivityMainBinding
import com.example.mysocialandroidapp2.databinding.DrawerHeaderBinding
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

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels()

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_posts, R.id.nav_events, R.id.nav_jobs
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        authViewModel.moveToAuthEvent.observe(this) {
            var fragment = getCurrentFragment()
            if (fragment is PostsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_posts_to_authFragment)
            else if (fragment is EventsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_events_to_authFragment)
            else if (fragment is JobsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_jobs_to_authFragment)
        }

        authViewModel.moveToSignUpEvent.observe(this) {
            var fragment = getCurrentFragment()
            if (fragment is PostsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_posts_to_regFragment)
            else if (fragment is EventsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_events_to_regFragment)
            else if (fragment is JobsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_jobs_to_regFragment)
        }

        authViewModel.signOutEvent.observe(this) {
            authViewModel.signOut()
        }

        authViewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful)
                Log.d(null, "Something wrong happened: ${task.exception}")
            val token = task.result
            Log.d(null, token)
        }

        // Настройка drawer header
        var headerView = binding.navView.getHeaderView(0)
        val headerBinding: DrawerHeaderBinding = DrawerHeaderBinding.bind(headerView)
        authViewModel.user.observe(this){
            if (authViewModel.authenticated) {
                headerBinding.textViewName.text = authViewModel.user.value?.name
            } else {
                with(headerBinding){
                    textViewName.text = "Not authenticated"
                    imageViewAvatar.setImageResource(R.drawable.ic_avatar)
                }
            }
        }

        with(authViewModel){
            if (authenticated) {
                val prefs = getSharedPreferences(
                    "auth",
                    Context.MODE_PRIVATE)
                initUser(prefs.getLong("id", 0))
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_posts, R.id.wallFragment, R.id.nav_events, R.id.nav_jobs
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        {
//            val userIdBundle = bundleOf(POST_ID to authViewModel.user.value?.id)
//            Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
//                .navigate(
//                    R.id.action_wallFragment_to_authFragment,
//                    userIdBundle
//                )
//        }
        authViewModel.moveToAuthEvent.observe(this) {
            var fragment = getCurrentFragment()
            if (fragment is WallFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_wallFragment_to_authFragment)
            else if (fragment is EventsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_events_to_authFragment)
            else if (fragment is JobsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_jobs_to_authFragment)
            else if (fragment is PostsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_posts_to_authFragment)
            else if (fragment is RegFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_regFragment_to_authFragment)
        }

        authViewModel.moveToSignUpEvent.observe(this) {
            var fragment = getCurrentFragment()
            if (fragment is WallFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_wallFragment_to_regFragment)
            else if (fragment is EventsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_events_to_regFragment)
            else if (fragment is JobsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_jobs_to_regFragment)
            else if (fragment is PostsFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_nav_posts_to_regFragment)
            else if (fragment is AuthFragment)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_authFragment_to_regFragment)
        }

        authViewModel.signOutEvent.observe(this) {
            authViewModel.signOut()
        }

        authViewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful)
                Log.d(null, "Something wrong happened: ${task.exception}")
            val token = task.result
            Log.d(null, token)
        }

        // Настройка drawer header
        var headerView = binding.navView.getHeaderView(0)
        val headerBinding: DrawerHeaderBinding = DrawerHeaderBinding.bind(headerView)
        authViewModel.user.observe(this){
            if (authViewModel.authenticated) {
                headerBinding.textViewName.text = authViewModel.user.value?.name
            } else {
                with(headerBinding){
                    textViewName.text = "Not authenticated"
                    imageViewAvatar.setImageResource(R.drawable.ic_avatar)
                }
            }
        }

        with(authViewModel){
            if (authenticated) {
                val prefs = getSharedPreferences(
                    "auth",
                    Context.MODE_PRIVATE)
                initUser(prefs.getLong("id", 0))
            }
        }
    }

    private fun getCurrentFragment() : Fragment? {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu?.let {
            it.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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