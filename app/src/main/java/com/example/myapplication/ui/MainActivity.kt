package com.example.myapplication.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.ui.register.LoginActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.home.HomeFragmentDirections
import com.example.myapplication.util.ForegroundService
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.MaterialSharedAxis

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var toggle: ActionBarDrawerToggle
    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            ?.childFragmentManager
            ?.fragments
            ?.first()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.bottomNavBar)

        toggle = ActionBarDrawerToggle(
            this,
            binding.mainActivity,
            R.string.open,
            androidx.compose.ui.R.string.close_drawer
        )
        binding.mainActivity.addDrawerListener(toggle)
        toggle.syncState()

        changeuserDetails()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.account -> {}
                R.id.favourites -> {}
                R.id.about -> {
                }
                R.id.logout -> {
                    logoutUser()
                }
            }
            true
        }
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        FacebookSdk.sdkInitialize(this)
        setBottomNavigationAndFab()
        setButtonClickListener()

        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragmentContainerView).navigateUp()
    }

    private fun setButtonClickListener() {
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                navigateToPersonFragment()
                hideBottomAppBar()
            }
        }
    }

    private fun changeuserDetails() {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)

        val userName = headerView.findViewById<TextView>(R.id.user_name)
        val userImage = headerView.findViewById<CircleImageView>(R.id.user_image)
        val userEmail = headerView.findViewById<TextView>(R.id.user_email)

        if (firebaseUser != null) {
            userName.text = firebaseUser.displayName
            userEmail.text = firebaseUser.email
            Glide.with(this).load(firebaseUser.photoUrl)
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(userImage)
        }
    }

    private fun navigateToPersonFragment() {

        currentNavigationFragment?.apply {

            enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
                duration = 300L
            }
        }

        val directions = HomeFragmentDirections.actionHomeFragmentToPersonFragment()
        findNavController(R.id.fragmentContainerView).navigate(directions)
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomNavBar.performHide()

            bottomNavBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (isCanceled) return
                    fab.hide()
                    bottomNavBar.performHide()
                }

                override fun onAnimationCancel(animation: Animator) {
                    super.onAnimationCancel(animation)
                    isCanceled = true
                }
            })
        }
    }

    private fun setBottomNavigationAndFab() {
        binding.run {
            findNavController(R.id.fragmentContainerView).addOnDestinationChangedListener(
                this@MainActivity
            )
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> {
                setBottomAppBarForHome()
            }
            R.id.personFragment -> {
                setBottomAppBarForPerson()
            }
            R.id.personCardFragment -> {
                setBottomAppBarForPersonCard()
            }
            R.id.personUpdateFragment -> {
                setBottomAppBarForPersonUpdate()
            }
        }
    }

    private fun setBottomAppBarForPerson() {
        binding.run {
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            hideBottomAppBar()
        }
    }

    private fun setBottomAppBarForPersonCard() {
        binding.run {
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            hideBottomAppBar()
        }
    }

    private fun setBottomAppBarForPersonUpdate() {
        binding.run {
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            hideBottomAppBar()
        }
    }

    private fun setBottomAppBarForHome() {
        binding.run {
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomNavBar.performHide()
            bottomNavBar.performShow()
            fab.show()
        }
    }

    private fun logoutUser() {
        LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )
            .signOut()
        startActivity(
            Intent(
                this, LoginActivity::
                class.java
            )
        )
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return when (item.itemId) {
            R.id.logout_om -> {
                logoutUser()
                true
            }
            R.id.search_om -> {
                true
            }
            R.id.placeholder -> {
                true
            }
            R.id.item1 -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}