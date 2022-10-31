package com.example.myapplication.ui.person

import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.transition.Fade.OUT
import android.transition.Visibility.MODE_IN
import android.view.Gravity.BOTTOM
import android.view.Gravity.TOP
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.compose.animation.slideOut
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Explode
import androidx.transition.Slide
import com.example.myapplication.R
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentPersonUpdateBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.home.HomeFragmentDirections
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialContainerTransform.*
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PersonUpdateFragment : Fragment() {

    private val args: PersonCardFragmentArgs by navArgs()
    private lateinit var currentPerson: Person
    private lateinit var binding: FragmentPersonUpdateBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPersonUpdateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.item_edit)
            endView = binding.personUpdateFragment
            duration = 300L
            scrimColor = Color.TRANSPARENT
            setPathMotion(MaterialArcMotion())
            interpolator = FastOutSlowInInterpolator()
            fadeMode = FADE_MODE_THROUGH
        }

        returnTransition = Slide(BOTTOM).apply {
            duration = 250L
        }
        exitTransition = MaterialFadeThrough().apply {
            duration = 450L
        }

        updatePerson()

        binding.navigationIcon.setOnClickListener {
            val directions = PersonUpdateFragmentDirections.actionGlobalHomeFragment()
            findNavController().navigate(directions)
        }

        currentPerson = args.person

        binding.etPersonUpdateName.setText(currentPerson.name)
        binding.etPersonUpdateDetails.setText(currentPerson.details)

    }

    private fun updatePerson() {
        binding.updateSubmitButton.setOnClickListener {
            val personName = binding.etPersonUpdateName.text
            val personDetails = binding.etPersonUpdateDetails.text

            if (!personName.contains("^[a-zA-Z]*$".toRegex()) && personName.isNotEmpty() ||
                !personDetails.contains("^[a-zA-Z]*$".toRegex()) && personDetails.isNotEmpty()
            ) {
                GlobalScope.launch(Dispatchers.IO) {
                    Repository.getInstance(requireContext()).updatePerson(
                        currentPerson.id,
                        personName.toString(),
                        personDetails.toString()
                    )
                }

                Snackbar.make(
                    binding.personUpdateFragment,
                    "User Successfully updated!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                findNavController().navigateUp()

            } else {
                Snackbar.make(
                    binding.personUpdateFragment,
                    "Please enter a valid Input",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}