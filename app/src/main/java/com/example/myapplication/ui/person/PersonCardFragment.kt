package com.example.myapplication.ui.person

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPersonCardBinding
import com.example.myapplication.model.person.Person
import com.google.android.material.transition.*


class PersonCardFragment : Fragment() {

    private val args: PersonCardFragmentArgs by navArgs()
    private lateinit var person: Person
    private lateinit var binding: FragmentPersonCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPersonCardBinding.inflate(layoutInflater, container, false)

        person = args.person
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.personName.text = person.name
        binding.personDetails.text = person.details
        Glide.with(requireContext()).load(args.person.imagePath).into(binding.personImage)

        enterTransition = MaterialContainerTransform().apply {

            startView = requireActivity().findViewById(R.id.item_name)
            endView = binding.personImage
            duration = 200L
            scrimColor = Color.TRANSPARENT
        }
        returnTransition = MaterialFadeThrough().apply {
            duration = 225L
            addTarget(android.R.id.content)
        }

        exitTransition = MaterialElevationScale(false).apply {
            duration = 200L
        }
    }

}
