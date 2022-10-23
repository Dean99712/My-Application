package com.example.myapplication.ui.person

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentPersonUpdateBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.home.HomeFragmentDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

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

        binding.navigationIcon.setOnClickListener{
            findNavController().navigateUp()
        }

        currentPerson = args.person

        binding.etPersonUpdateName.setText(currentPerson.name)
        binding.etPersonUpdateDetails.setText(currentPerson.details)

        updatePerson()

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