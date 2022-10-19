package com.example.myapplication.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.myapplication.ui.register.LoginActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.person.PersonFragmentDirections
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            ?.childFragmentManager
            ?.fragments
            ?.first()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.bottomNavBar)


        setBottomNavigationAndFab()
        setButtonClickListener()
    }

    @SuppressLint("NotifyDataSetChanged")
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

    private fun navigateToPersonFragment() {

        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = 300L
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = 300L
            }
        }
        val directions = PersonFragmentDirections.actionGlobalPersonFragment()
        findNavController(R.id.fragmentContainerView).navigate(directions)
    }

    @SuppressLint("ResourceAsColor")
    private fun hideBottomAppBar() {
        binding.run {
            bottomNavBar.performHide()

            bottomNavBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (isCanceled) return
                    bottomNavBar.visibility = View.GONE
                    fab.visibility = View.INVISIBLE
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
        }
    }

    private fun setBottomAppBarForPerson() {
        binding.run {
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomNavBar.visibility = View.VISIBLE
            bottomNavBar.performShow()
            fab.show()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setBottomAppBarForPersonCard() {
        binding.run {
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomNavBar.visibility = View.VISIBLE
            bottomNavBar.backgroundTint
            bottomNavBar.performShow()
            fab.show()
            hideBottomAppBar()
        }
    }

    private fun setBottomAppBarForHome() {
        binding.run {
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomNavBar.visibility = View.VISIBLE
            bottomNavBar.performShow()
            fab.show()
        }
    }


    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_om -> {
                logoutUser()
                true
            }
            R.id.search_om -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}