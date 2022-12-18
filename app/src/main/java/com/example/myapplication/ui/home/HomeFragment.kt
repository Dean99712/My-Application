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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.util.SwipeToDeleteCallBack
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.ui.person.*
import com.example.myapplication.util.ImageManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), PersonAdapter.PersonAdapterListener {

    private var personLiveDataList: MutableLiveData<ArrayList<Person>> =
        MutableLiveData<ArrayList<Person>>()
    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var currentPerson: Person? = null
    private lateinit var adapter: PersonAdapter
    private lateinit var binding: FragmentHomeBinding



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

        createRecyclerView()
        requestCameraPermission(requireContext())
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
                Log.d("Log", "Cannot access Camera")
            }
        }

//    fun changeAvatarPicture(user: User) {
//        ImageManager.
//    }

    private val swipeToDeleteCallBack = object : SwipeToDeleteCallBack() {
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
            FirebaseManager.db.collection("users/${firebaseUser!!.email}/personList")
                .document(person.id).delete()

            Snackbar.make(
                binding.homeFragment, "You removed ${person.name}!", Snackbar.LENGTH_LONG
            ).setAction("Undo") {
                personList.add(position, person)
                adapter.notifyItemInserted(position)

                GlobalScope.launch(Dispatchers.IO) {
                    Repository.getInstance(requireContext()).addPerson(person)
                }
                FirebaseManager.getInstance(requireContext()).addPersonToUser(person) {}
            }.show()
        }
    }

    private fun createRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
//        val roomDatabasePersonList = Repository.getInstance(requireContext()).getAllPeopleAsLiveData()
//        roomDatabasePersonList.observe(viewLifecycleOwner) {
//            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
//            itemTouchHelper.attachToRecyclerView(recyclerView)
//            updateUi(it)
//            adapter.updateRecyclerView(it)
//        }
        personLiveDataList.observe(viewLifecycleOwner) { personList ->
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
            itemTouchHelper.attachToRecyclerView(recyclerView)
//            updateUi(personList)
            adapter.updateRecyclerView(personList)
        }
        eventChangeListener()
    }

    private fun eventChangeListener() {
        val email = firebaseUser!!.email
        FirebaseManager.db.collection("users/${email}/personList")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val documents = snapshot.documents
                        val personList = ArrayList<Person>()
                        documents.forEach {
                            val person = it.toObject(Person::class.java)
                            if (person != null) {
                                personList.add(person)
                            }
                            personLiveDataList.value = personList
                        }
                    }
                }
            }
    }

    override fun onEditClicked(person: Person) {

        val directions = PersonUpdateFragmentDirections.actionGlobalPersonUpdateFragment(person)
        findNavController().navigate(directions)
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCardClicked(cardView: View, person: Person) {

        val directions = HomeFragmentDirections.actionHomeFragmentToPersonCardFragment(person)
        findNavController().navigate(directions)
    }

    override fun onImageClicked(person: Person) {
        currentPerson = person

        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle("Choose an image")
        dialog.setMessage("Choose image for ${currentPerson!!.name}")
        dialog.setNeutralButton("Camera") { _: DialogInterface, _: Int ->
            ImageManager.takePicture(currentPerson!!, getContentFromCamera)
        }
        dialog.setPositiveButton("Gallery") { _: DialogInterface, _: Int ->

            ImageManager.getImageFromGallery(currentPerson!!, getContentFromGallery)
        }
        dialog.setNegativeButton("Network") { _: DialogInterface, _: Int ->
            ImageManager.getImageFromApi(currentPerson!!, requireActivity())
        }
        dialog.show()
    }
}
