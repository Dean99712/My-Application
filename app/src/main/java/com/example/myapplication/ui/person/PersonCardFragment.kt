package com.example.myapplication.ui.person

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPersonCardBinding
import com.example.myapplication.model.person.Person
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialContainerTransform.FADE_MODE_THROUGH

class PersonCardFragment : Fragment() {

    private val args: PersonCardFragmentArgs by navArgs()
    private lateinit var person: Person
    private lateinit var binding: FragmentPersonCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonCardBinding.inflate(layoutInflater, container, false)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragmentContainerView
            duration = 225L
            scrimColor = Color.TRANSPARENT
            setPathMotion(MaterialArcMotion())
            interpolator = FastOutSlowInInterpolator()
            fadeMode = FADE_MODE_THROUGH
        }

        person = args.person
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("FRAGMENT: PersonCardFragment")

        binding.personName.text = person.name
        binding.personDetails.text = person.details

                Glide
                    .with(requireContext())
                    .load(person.imagePath)
                    .apply(
                        RequestOptions().dontTransform()
                    ).placeholder(R.drawable.ic_baseline_person_24).into(binding.personImage)

    }
}


