package com.example.myapplication.ui.person

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.person.Person

class PersonAdapter(
    val dataList: ArrayList<Person>,
    private val onEditButtonClick: (Person) -> Unit,
    private val onPersonImageClick: (Person) -> Unit,
    private val onPersonCardClick: (Person) -> Unit,
    val context: Context
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val personCard: ConstraintLayout = itemView.findViewById(R.id.person_card_recycler)
        val textView: TextView = itemView.findViewById(R.id.item_name)
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val personDetails: TextView = itemView.findViewById(R.id.item_details)
        val editButton: ImageButton = itemView.findViewById(R.id.item_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = dataList[position]

        holder.textView.text = person.name

        when (person.imageType) {
            null -> Glide.with(context).load(R.drawable.ic_baseline_person_24).centerInside()
                .into(holder.imageView)
            else -> Glide.with(context).load(person.imagePath).into(holder.imageView)
        }

        if (person.details != null) {
            holder.personDetails.text = person.details
        } else {
            holder.personDetails.text = "Details"
        }

        holder.imageView.setOnClickListener {
            onPersonImageClick(person)
        }

        holder.personCard.setOnClickListener {
            onPersonCardClick(person)
        }

        holder.editButton.setOnClickListener {
            onEditButtonClick(person)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecyclerView(personList: List<Person>) {
        dataList.clear()
        dataList.addAll(personList)
        notifyDataSetChanged()

    }
}


