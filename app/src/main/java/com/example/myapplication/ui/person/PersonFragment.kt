package com.example.myapplication.ui.person

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentPersonBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.home.HomeFragmentDirections
import com.example.myapplication.util.NotificationsManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PersonFragment : Fragment() {

    private lateinit var binding: FragmentPersonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentPersonBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigationIcon.setOnClickListener {
            findNavController().navigateUp()
        }
        addPerson()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_action_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun addPerson() {
        binding.submitButton.setOnClickListener {
            val personName = binding.etAddPersonName.text.toString()
            val personDetails = binding.etAddPersonDetails.text.toString()
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

}