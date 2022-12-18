package com.example.myapplication.ui.person

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.Gravity.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentPersonBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.util.NotificationsManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
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

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addPerson()

        binding.run {

            navigationIcon.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun addPerson() {
        binding.submitButton.setOnClickListener {
            val personName = binding.etAddPersonName.text.toString()
            val personDetails = binding.etAddPersonDetails.text.toString()
            val email = FirebaseAuth.getInstance().currentUser!!.email!!
            val documentId = FirebaseManager.db.collection("users/$email/personList").document().id

            if (personName.contains("^[a-zA-Z]*$".toRegex()) && personName.isNotEmpty() ||
                !personDetails.contains("^[a-zA-Z]*$".toRegex()) && personDetails.isNotEmpty()
            ) {

                val person = Person(personName, personDetails, documentId)

                FirebaseManager.getInstance(requireContext()).addPersonToUser(person) {}

                thread(start = true) {
                    Repository.getInstance(requireContext()).addPerson(person)
                }

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

}

