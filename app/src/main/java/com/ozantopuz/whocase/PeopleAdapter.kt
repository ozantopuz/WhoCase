package com.ozantopuz.whocase

import Person
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozantopuz.whocase.databinding.LayoutItemPeopleBinding

class PeopleAdapter : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    private var list : ArrayList<Person> = arrayListOf()

    fun refreshList(list: List<Person>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun updateList(list: List<Person>){
        this.list.addAll(list)
        this.list = ArrayList(this.list.distinctBy { it.id })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val itemBinding =
            LayoutItemPeopleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PeopleViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val person: Person = list[position]
        holder.bind(person)
    }

    override fun getItemCount() = list.size

    class PeopleViewHolder(
        private val itemBinding: LayoutItemPeopleBinding
        ) : RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(person: Person) {
            itemBinding.textView.text = "${person.fullName} (${person.id})"
        }
    }
}