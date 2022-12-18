package com.example.myapplication.ui.person

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.databinding.FragmentPersonUpdateBinding
import com.example.myapplication.model.person.Person
import com.google.android.material.snackbar.Snackbar

class PersonUpdateFragment : Fragment() {

    private val args: PersonCardFragmentArgs by navArgs()
    private lateinit var currentPerson: Person
    private lateinit var binding: FragmentPersonUpdateBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPersonUpdateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val person = Person(personName.toString(), personDetails.toString())

            if (!personName.contains("^[a-zA-Z]*$".toRegex()) && personName.isNotEmpty() ||
                !personDetails.contains("^[a-zA-Z]*$".toRegex()) && personDetails.isNotEmpty()
            ) {

                FirebaseManager.getInstance(requireContext()).updatePerson(person)

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