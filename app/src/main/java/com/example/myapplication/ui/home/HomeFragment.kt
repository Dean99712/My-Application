package com.example.myapplication.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.example.hackeruapp.util.SwipeToDeleteCallBack
import com.example.myapplication.R
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.person.PersonAdapter
import com.example.myapplication.ui.person.PersonCardFragmentDirections
import com.example.myapplication.ui.person.PersonFragmentDirections
import com.example.myapplication.util.ImageManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ModalBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}

class HomeFragment : Fragment() {


    private var person: Person? = null
    private lateinit var adapter: PersonAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfListIsEmpty()
        adapter = PersonAdapter(
            arrayListOf(),
            onPersonTitleClick(),
            onPersonImageClick(),
            onPersonCardClick(),
            requireContext().applicationContext
        )

        requestCameraPermission(requireContext())
        createRecyclerView()
        adapter.notifyDataSetChanged()
    }

    private fun onPersonTitleClick(): (Person) -> Unit = {
        val directions = PersonFragmentDirections.actionGlobalPersonFragment()
        findNavController().navigate(directions)
    }

    private fun onPersonCardClick(): (Person) -> Unit = { person ->

        val directions = HomeFragmentDirections.actionHomeFragmentToPersonCardFragment(person)
        findNavController().navigate(directions)
    }


    private fun requestCameraPermission(context: Context) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED -> Log.i("Log", "Permission Granted")

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> Log.i("Log", "Show Camera Permission Dialog")

            else -> requestPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private val getContentFromCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            ImageManager.onImageResultFromCamera(result, person!!, requireContext())
        }

    private val getContentFromGallery =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            ImageManager.onImageResultFromGallery(result, person!!, requireContext())
        }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestCameraPermission(requireContext())
            } else {
                Log.i("Log", "Cannot access Camera")
            }

        }

    private fun onPersonImageClick(): (person: Person) -> Unit = {

//        val modalBottomSheet = ModalBottomSheet()
//        modalBottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)


        person = it

        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle("Choose an image")
        dialog.setMessage("Choose image for ${person!!.name}")

        dialog.setNeutralButton("Camera") { _: DialogInterface, _: Int ->
            ImageManager.takePicture(person!!, getContentFromCamera)
        }
        dialog.setPositiveButton("Gallery") { _: DialogInterface, _: Int ->

            ImageManager.getImageFromGallery(person!!, getContentFromGallery)
        }
        dialog.setNegativeButton("Network") { _: DialogInterface, _: Int ->
            ImageManager.getImageFromApi(person!!, requireActivity())
        }
        dialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    val swipeToDeleteCallBack = object : SwipeToDeleteCallBack() {
        @SuppressLint("UseRequireInsteadOfGet")
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val position = viewHolder.adapterPosition
            val personList = adapter.dataList
            val person = adapter.dataList[position]

            personList.removeAt(position)
            adapter.notifyItemRemoved(position)

            GlobalScope.launch(Dispatchers.IO) {
                Repository.getInstance(requireContext()).deletePerson(person)
            }

            Snackbar.make(
                binding.homeFragment,
                "You removed ${person.name}!",
                Snackbar.LENGTH_LONG
            )
                .setAction("Undo") {
                    personList.add(position, person)
                    adapter.notifyItemInserted(position)

                    GlobalScope.launch(Dispatchers.IO) {
                        Repository.getInstance(requireContext()).addPerson(person)
                    }
                }.show()
        }
    }

    private fun checkIfListIsEmpty() {
        val personList = Repository.getInstance(requireContext()).getAllPeopleAsLiveData()
        if (personList.toString().isEmpty()) {
            binding.isListEmpty.visibility = View.VISIBLE
        } else
            false
    }


    private fun createRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view)

        if (recyclerView != null) {
            recyclerView.layoutManager = layoutManager
        }
        recyclerView?.adapter = adapter
        val peopleListLiveData = Repository.getInstance(requireContext()).getAllPeopleAsLiveData()
        peopleListLiveData.observe(viewLifecycleOwner) { personList ->
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
            itemTouchHelper.attachToRecyclerView(recyclerView)
            adapter.updateRecyclerView(personList)
            updateUi(personList)
        }
    }

    private fun updateUi(person: List<Person>) {
        if (person.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.isListEmpty.visibility = View.GONE
        } else {

            binding.recyclerView.visibility = View.GONE
            binding.isListEmpty.visibility = View.VISIBLE
        }
    }
}