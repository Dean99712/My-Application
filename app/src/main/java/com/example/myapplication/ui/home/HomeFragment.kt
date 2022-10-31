package com.example.myapplication.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
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
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.util.SwipeToDeleteCallBack
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.model.user.User
import com.example.myapplication.ui.person.*
import com.example.myapplication.util.ImageManager
import com.example.myapplication.util.SharedPreferencesManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), PersonAdapter.PersonAdapterListener {

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var currentUser: User
    private var personLiveDataList: MutableLiveData<ArrayList<Person>> =
        MutableLiveData<ArrayList<Person>>()
    private var currentPerson: Person? = null
    private lateinit var adapter: PersonAdapter
    private lateinit var binding: FragmentHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())

        currentUser = User(
            sharedPreferencesManager.sharedPreferences.getString(
                sharedPreferencesManager.SHARED_PREFERENCES_EMAIL,
                ""
            ).toString(),
            sharedPreferencesManager.sharedPreferences.getString(
                sharedPreferencesManager.SHARED_PREFERENCES_NAME,
                ""
            ).toString(),
            sharedPreferencesManager.sharedPreferences.getString(
                sharedPreferencesManager.SHARED_PREFERENCES_EMAIL,
                ""
            ).toString()
        )

        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PersonAdapter(
            arrayListOf(),
            this,
            requireContext()
        )

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }


        println("FRAGMENT : HomeFragment")

        createRecyclerView()
        displayUserName()
        requestCameraPermission(requireContext())
    }

    @SuppressLint("SetTextI18n")
    private fun displayUserName() {
        val name = sharedPreferencesManager.sharedPreferences.getString(
            sharedPreferencesManager.SHARED_PREFERENCES_NAME,
            null
        )
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
            ImageManager.onImageResultFromCamera(result, currentPerson!!, requireContext())
        }

    private val getContentFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        ImageManager.onImageResultFromGallery(result, currentPerson!!, requireContext())
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestCameraPermission(requireContext())
            } else {
                Log.i("Log", "Cannot access Camera")
            }

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
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
//        val roomDatabasePersonList = Repository.getInstance(requireContext()).getAllPeopleAsLiveData()
//        roomDatabasePersonList.observe(viewLifecycleOwner) {
//            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
//            (view?.parent as? ViewGroup)?.doOnPreDraw {
//                startPostponedEnterTransition()
//            }
//            itemTouchHelper.attachToRecyclerView(recyclerView)
//            updateUi(it)
//            adapter.updateRecyclerView(it)
//        }
        personLiveDataList.observe(viewLifecycleOwner) { personList ->
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
            itemTouchHelper.attachToRecyclerView(recyclerView)
            updateUi(personList)
            adapter.updateRecyclerView(personList)
        }
        eventChangeListener()
    }

    private fun eventChangeListener() {
        FirebaseManager.db.collection("users/${currentUser.email}/personList").get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val personList = ArrayList<Person>()
                    val document = snapshot.documents
                    document.forEach {
                        val person = it.toObject(Person::class.java)
                        personList.add(person!!)
                    }
                    personLiveDataList.value = personList
                }
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

    override fun onEditClicked(person: Person) {

        val directions = PersonUpdateFragmentDirections.actionGlobalPersonUpdateFragment(person)
        findNavController().navigate(directions)
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCardClicked(cardView: View, person: Person) {

        returnTransition = MaterialElevationScale(true).apply {
            duration = 300L
        }
        exitTransition = MaterialElevationScale(false).apply {
            duration = 300L
        }

        val personCardDetailsTransitionName =
            requireContext().getString(R.string.person_card_details_transition_name)

        val extras = FragmentNavigatorExtras(cardView to personCardDetailsTransitionName)
        val directions = HomeFragmentDirections.actionHomeFragmentToPersonCardFragment(person)
        findNavController().navigate(directions, extras)
    }

    override fun onImageClicked(person: Person) {

        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle("Choose an image")
        dialog.setMessage("Choose image for ${person.name}")
        dialog.setNeutralButton("Camera") { _: DialogInterface, _: Int ->
            ImageManager.takePicture(person, getContentFromCamera)
        }
        dialog.setPositiveButton("Gallery") { _: DialogInterface, _: Int ->

            ImageManager.getImageFromGallery(person, getContentFromGallery)
        }
        dialog.setNegativeButton("Network") { _: DialogInterface, _: Int ->
            ImageManager.getImageFromApi(person, requireActivity())
        }
        dialog.show()
    }
}

