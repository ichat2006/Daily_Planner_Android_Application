package com.example.myapplication.ui.social

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

// Assuming Contact data class is accessible, if not, it might need to be moved or imported differently.
// For now, assuming it's in the same package or accessible.

class ContactsAdapter(private val onDeleteClick: (Contact) -> Unit) :
    ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact, onDeleteClick)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        private val notesTextView: TextView = itemView.findViewById(R.id.contact_notes)
        private val phoneTextView: TextView = itemView.findViewById(R.id.contact_phone)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(contact: Contact, onDeleteClick: (Contact) -> Unit) {
            nameTextView.text = contact.name
            notesTextView.text = contact.notes
            phoneTextView.text = contact.phone
            deleteButton.setOnClickListener { onDeleteClick(contact) }
        }
    }

    private class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
} 