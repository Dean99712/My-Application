package com.example.myapplication.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.util.SwipeToDeleteCallBack
import com.example.myapplication.R
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.person.*
import com.example.myapplication.util.ImageManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREFERENCES = "sharedPref"
    private val SHARED_PREFERENCES_NAME = "name"
    private var person: Person? = null
    private lateinit var adapter: PersonAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PersonAdapter(
            arrayListOf(),
            onEditButtonClick(),
            onPersonImageClick(),
            onPersonCardClick(),
            requireContext()
        )
        displayUserName()
        requestCameraPermission(requireContext())
        createRecyclerView()
        adapter.notifyDataSetChanged()
    }

    private fun onEditButtonClick(): (Person) -> Unit = { person ->
        val directions = PersonUpdateFragmentDirections.actionGlobalPersonUpdateFragment(person)
        findNavController().navigate(directions)
    }

    private fun onPersonCardClick(): (Person) -> Unit = {

            val directions = HomeFragmentDirections.actionHomeFragmentToPersonCardFragment(it)
            findNavController().navigate(directions)
        }

    @SuppressLint("SetTextI18n")
    private fun displayUserName() {
        val name = sharedPreferences.getString(SHARED_PREFERENCES_NAME, null)
        binding.textView.text = "Hello $name"
    }

    private fun requestCameraPermission(context: Context) {
        when {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED -> Log.i("Log", "Permission Granted")

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), Manifest.permission.CAMERA
            ) -> Log.i("Log", "Show Camera Permission Dialog")

            else -> requestPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private val getContentFromCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            ImageManager.onImageResultFromCamera(result, person!!, requireContext())
        }

    private val getContentFromGallery = registerForActivityResult(
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
                binding.homeFragment, "You removed ${person.name}!", Snackbar.LENGTH_LONG
            ).setAction("Undo") {
                personList.add(position, person)
                adapter.notifyItemInserted(position)

                GlobalScope.launch(Dispatchers.IO) {
                    Repository.getInstance(requireContext()).addPerson(person)
                }
            }.show()
        }
    }

    private fun createRecyclerView() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView?.adapter = adapter
        val peopleListLiveData = Repository.getInstance(requireContext()).getAllPeopleAsLiveData()
        peopleListLiveData.observe(viewLifecycleOwner) { personList ->
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
            itemTouchHelper.attachToRecyclerView(recyclerView)
            updateUi(personList)
            adapter.updateRecyclerView(personList)
        }
    }

    private fun updateUi(list: List<Person>) {
        if (list.isNotEmpty()) {
            binding.recyclerView.apply {
                visibility = View.VISIBLE
            }
            binding.isListEmpty.apply {
                visibility = View.GONE
                animation = AnimationUtils.loadAnimation(requireContext(), R.anim.remove_item)
            }
        } else {
            binding.recyclerView.apply {
                visibility = View.GONE
            }
            binding.isListEmpty.apply {
                visibility = View.VISIBLE
                animation = AnimationUtils.loadAnimation(requireContext(), R.anim.add_item)
            }
        }
    }
}