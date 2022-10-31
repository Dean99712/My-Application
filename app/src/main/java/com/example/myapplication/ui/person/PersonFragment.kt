package com.example.myapplication.ui.person

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.Gravity.*
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentPersonBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.model.user.User
import com.example.myapplication.util.NotificationsManager
import com.example.myapplication.util.SharedPreferencesManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialContainerTransform.FADE_MODE_THROUGH
import kotlin.concurrent.thread

class PersonFragment : Fragment() {
    private lateinit var user: User
    private lateinit var binding: FragmentPersonBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())

        println("FRAGMENT: PersonFragment")

        initializeUser()

        addPerson()

        binding.run {

            navigationIcon.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)

        enterTransition = MaterialContainerTransform().apply {
            startView = fab
            endView = binding.personFragment
            duration = 325L
            scrimColor = Color.TRANSPARENT
            setPathMotion(MaterialArcMotion())
            interpolator = FastOutSlowInInterpolator()
            fadeMode = FADE_MODE_THROUGH
        }

        returnTransition = Slide(RIGHT).apply {
            duration = 300L
        }

        exitTransition = Slide(LEFT).apply {
            duration = 300L
        }
    }

    private fun addPerson() {
        binding.submitButton.setOnClickListener {
            val personName = binding.etAddPersonName.text.toString()
            val personDetails = binding.etAddPersonDetails.text.toString()
            val person = Person(personName, personDetails)

            if (personName.contains("^[a-zA-Z]*$".toRegex()) && personName.isNotEmpty() ||
                !personDetails.contains("^[a-zA-Z]*$".toRegex()) && personDetails.isNotEmpty()
            ) {

                thread(start = true) {
                    Repository.getInstance(requireContext()).addPerson(person)
                }

                FirebaseManager.getInstance(requireContext()).addPersonToUser(user, person)

                Snackbar.make(
                    binding.personFragment,
                    "User Successfully added!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                NotificationsManager.displayNotification(requireContext(), person)
                findNavController().navigateUp()

            } else {
                Snackbar.make(
                    binding.personFragment,
                    "Please enter a valid Input",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun initializeUser(): User {
        user = User()
        user.email = sharedPreferencesManager.sharedPreferences.getString(
            sharedPreferencesManager.SHARED_PREFERENCES_EMAIL,
            ""
        )
            .toString()
        user.firstName = sharedPreferencesManager.sharedPreferences.getString(
            sharedPreferencesManager.SHARED_PREFERENCES_NAME,
            ""
        ).toString()
        user.lastName = sharedPreferencesManager.sharedPreferences.getString(
            sharedPreferencesManager.SHARED_PREFERENCES_LAST_NAME,
            ""
        ).toString()

        return user
    }

}

