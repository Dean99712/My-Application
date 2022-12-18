package com.example.myapplication.ui.person

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemsLayoutBinding
import com.example.myapplication.model.person.Person

class PersonAdapter(
    val dataList: ArrayList<Person>,
    private val listener: PersonAdapterListener,
    val context: Context
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    interface PersonAdapterListener {
        fun onEditClicked(person: Person)
        fun onCardClicked(cardView: View, person: Person)
        fun onImageClicked(person: Person)
    }

    class ViewHolder(
        val binding: ItemsLayoutBinding,
        listener: PersonAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        val textView: TextView = itemView.findViewById(R.id.item_name)
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val personDetails: TextView = itemView.findViewById(R.id.item_details)
        val editButton: ImageButton = itemView.findViewById(R.id.item_edit)

        init {
            binding.run {
                this.listener = listener
            }
        }

        fun bind(person: Person) {
            binding.person = person
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), listener
        )
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val person = dataList[position]

        holder.textView.text = person.name
        holder.personDetails.text = person.details

        holder.bind(person)

        Glide.with(context).load(person.imagePath).placeholder(R.drawable.ic_baseline_person_24)
            .into(holder.imageView)
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


