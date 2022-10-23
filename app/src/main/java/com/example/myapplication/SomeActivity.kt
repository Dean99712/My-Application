//package com.example.myapplication
//
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.snackbar.Snackbar
//
//class SomeActivity : AppCompatActivity() {
//
//    // on below line we are creating variables for
//    // our swipe to refresh layout, recycler view, adapter and list.
//    lateinit var courseRV: RecyclerView
//    lateinit var courseRVAdapter: CourseRVAdapter
//    lateinit var courseList: ArrayList<CourseRVModal>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // on below line we are initializing
//        // our views with their ids.
//        courseRV = findViewById(R.id.idRVCourses)
//
//        // on below line we are initializing our list
//        courseList = ArrayList()
//
//        // on below line we are initializing our adapter
//        courseRVAdapter = CourseRVAdapter(courseList, this)
//
//        // on below line we are setting adapter
//        // to our recycler view.
//        courseRV.adapter = courseRVAdapter
//
//        // on below line we are adding data to our list
//        courseList.add(CourseRVModal("Android Development", R.drawable.android))
//        courseList.add(CourseRVModal("C++ Development", R.drawable.c))
//        courseList.add(CourseRVModal("Java Development", R.drawable.java))
//        courseList.add(CourseRVModal("Python Development", R.drawable.python))
//        courseList.add(CourseRVModal("JavaScript Development", R.drawable.js))
//
//        // on below line we are notifying adapter
//        // that data has been updated.
//        courseRVAdapter.notifyDataSetChanged()
//
//        // on below line we are creating a method to create item touch helper
//        // method for adding swipe to delete functionality.
//        // in this we are specifying drag direction and position to right
//        // on below line we are creating a method to create item touch helper
//        // method for adding swipe to delete functionality.
//        // in this we are specifying drag direction and position to right
//        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                // this method is called
//                // when the item is moved.
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                // this method is called when we swipe our item to right direction.
//                // on below line we are getting the item at a particular position.
//                val deletedCourse: CourseRVModal =
//                    courseList.get(viewHolder.adapterPosition)
//
//                // below line is to get the position
//                // of the item at that position.
//                val position = viewHolder.adapterPosition
//
//                // this method is called when item is swiped.
//                // below line is to remove item from our array list.
//                courseList.removeAt(viewHolder.adapterPosition)
//
//                // below line is to notify our item is removed from adapter.
//                courseRVAdapter.notifyItemRemoved(viewHolder.adapterPosition)
//
//                // below line is to display our snackbar with action.
//                // below line is to display our snackbar with action.
//                // below line is to display our snackbar with action.
//                Snackbar.make(courseRV, "Deleted " + deletedCourse.courseName, Snackbar.LENGTH_LONG)
//                    .setAction(
//                        "Undo",
//                        View.OnClickListener {
//                            // adding on click listener to our action of snack bar.
//                            // below line is to add our item to array list with a position.
//                            courseList.add(position, deletedCourse)
//
//                            // below line is to notify item is
//                            // added to our adapter class.
//                            courseRVAdapter.notifyItemInserted(position)
//                        }).show()
//            }
//            // at last we are adding this
//            // to our recycler view.
//        }).attachToRecyclerView(courseRV)
//
//    }
//}