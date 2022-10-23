package com.example.myapplication.ui.person

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPersonCardBinding
import com.example.myapplication.model.person.Person
import com.google.android.material.transition.MaterialContainerTransform

class PersonCardFragment : Fragment() {

    private val args: PersonCardFragmentArgs by navArgs()
    private lateinit var person: Person
    private lateinit var binding: FragmentPersonCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        if (person.imageType != null) {
            Glide.with(requireContext()).load(person.imagePath).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            }).into(binding.personImage)
        } else {
            Glide.with(requireContext()).load(R.drawable.ic_baseline_person_24)
                .into(binding.personImage)
        }
    }
}


