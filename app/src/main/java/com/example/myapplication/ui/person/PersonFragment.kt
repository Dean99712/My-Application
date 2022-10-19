package com.example.myapplication.ui.person

import android.graphics.Color
import android.os.Bundle
import android.transition.Visibility.MODE_OUT
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.transition.Explode
import androidx.transition.Slide
import androidx.transition.Visibility
import com.example.myapplication.R
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentPersonBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.home.HomeFragmentDirections
import com.example.myapplication.util.NotificationsManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class PersonFragment : Fragment() {

    private lateinit var binding: FragmentPersonBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPersonBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun submitDetails() {

        binding.submitButton.setOnClickListener {
            val personName = binding.etPersonUpdateName.text.toString()
            val personDetails = binding.etPersonUpdateDetails.text.toString()
            val person = Person(personName, personDetails)

            if (!personName.contains("^[a-zA-Z]*$".toRegex()) && personName.isNotEmpty() ||
                !personDetails.contains("^[a-zA-Z]*$".toRegex()) && personDetails.isNotEmpty()
            ) {
                GlobalScope.launch(Dispatchers.IO) {
                    Repository.getInstance(requireContext()).addPerson(person)
                }
                Snackbar.make(
                    binding.personFragment,
                    "User Successfully added!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                NotificationsManager.displayNotification(requireContext(), person)

                val direction = HomeFragmentDirections.actionGlobalHomeFragment()
                findNavController().navigate(direction)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitDetails()
        enterTransition = MaterialContainerTransform().apply {
            // Manually add the Views to be shared since this is not a standard Fragment to
            // Fragment shared element transition.
            startView = requireActivity().findViewById(R.id.fab)
            endView = binding.personFragment
            duration = 300L
            scrimColor = Color.TRANSPARENT
//            containerColor = requireContext().setTheme(R.attr.colorSurface)
//            containerColor = requireContext().themeColor(R.attr.colorSurface)
//            startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
//            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = 225L
            addTarget(R.id.personFragment)
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = 500L
            addTarget(R.id.personFragment)
        }

    }
}