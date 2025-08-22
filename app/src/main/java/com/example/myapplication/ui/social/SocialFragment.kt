package com.example.myapplication.ui.social
import android.widget.EditText
import android.widget.RadioButton
import com.example.myapplication.ui.social.ContactDbHelper
import com.example.myapplication.ui.social.ContactRecord
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSocialBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout

data class Contact(
    val id: Long = 0,
    val name: String,
    val notes: String,
    val phone: String,
    val email: String,
    val category: String
)

class SocialFragment : Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { ContactDbHelper(requireContext()) }
    private val allContacts = mutableListOf<Contact>()
    private lateinit var adapter: ContactsAdapter
    private var currentCategory = "All"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, saved: Bundle?) {
        super.onViewCreated(view, saved)
        setupRecycler()
        setupTabs()
        binding.fabAddContact.setOnClickListener { showAddDialog() }
        refreshList()
    }

    private fun setupRecycler() {
        adapter = ContactsAdapter { contact: Contact ->
            db.delete(contact.id)
            refreshList()
        }
        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SocialFragment.adapter
        }
    }

    private fun setupTabs() {
        listOf("All", "Family", "Friends", "Coworkers", "Other").forEach {
            binding.socialCategoriesTabs.addTab(
                binding.socialCategoriesTabs.newTab().setText(it)
            )
        }
        binding.socialCategoriesTabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    currentCategory = tab.text.toString()
                    applyFilter()
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            }
        )
    }

    private fun refreshList() {
        allContacts.clear()
        allContacts += db.getAll().map {
            Contact(it.id, it.name, it.notes, it.phone, it.email, it.category)
        }
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = if (currentCategory == "All")
            allContacts
        else
            allContacts.filter { it.category == currentCategory }

        adapter.submitList(filtered)
        binding.emptyStateLayout.visibility =
            if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showAddDialog() {
        val dlg = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val nameIn  = dlg.findViewById<EditText>(R.id.input_contact_name)
        val notesIn = dlg.findViewById<EditText>(R.id.input_contact_notes)
        val phoneIn = dlg.findViewById<EditText>(R.id.input_contact_phone)
        val emailIn = dlg.findViewById<EditText>(R.id.input_contact_email)
        val catGrp  = dlg.findViewById<RadioGroup>(R.id.contact_category_radio_group)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Contact")
            .setView(dlg)
            .setPositiveButton("Save") { _, _ ->
                val name = nameIn.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Name required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val notes = notesIn.text.toString().trim()
                val phone = phoneIn.text.toString().trim()
                val email = emailIn.text.toString().trim()
                val selectedId = catGrp.checkedRadioButtonId
                val cat = if (selectedId != -1)
                    dlg.findViewById<RadioButton>(selectedId).text.toString()
                else
                    "Other"

                db.insert(ContactRecord(0, name, notes, phone, email, cat))
                refreshList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
