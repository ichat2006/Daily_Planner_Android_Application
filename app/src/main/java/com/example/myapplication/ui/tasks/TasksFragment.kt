package com.example.myapplication.ui.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTasksBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class Task(
    val id: Long,
    val name: String,
    val description: String,
    val priority: String,
    val category: String,
    val dueDate: String,
    val plannedDate: String,
    val isCompleted: Boolean
)

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: TaskDbHelper
    private lateinit var adapter: TasksAdapter
    private var currentCategory = "All"
    private var allTasks = listOf<Task>()
    private val taskTree = TaskTree()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, saved: Bundle?) {
        super.onViewCreated(view, saved)
        db = TaskDbHelper(requireContext())
        setupRecycler()
        setupTabs()
        binding.fabAddTask.setOnClickListener { showAddDialog() }
        loadAndShowAll()
    }

    private fun setupRecycler() {
        adapter = TasksAdapter(
            onDelete = { id ->
                db.delete(id)
                loadAndShowAll()
            },
            onEdit = { task ->
                showEditDialog(task)
            }
        )
        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TasksFragment.adapter
        }
    }

    private fun setupTabs() {
        listOf("All", "Today", "Upcoming", "Completed").forEach {
            binding.taskCategoriesTabs.addTab(
                binding.taskCategoriesTabs.newTab().setText(it)
            )
        }
        binding.taskCategoriesTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentCategory = tab.text.toString()
                applyFilter()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun loadAndShowAll() {
        allTasks = db.getAll().map { r ->
            Task(
                id = r.id,
                name = r.name,
                description = r.description,
                priority = r.priority,
                category = r.category,
                dueDate = r.dueDate,
                plannedDate = r.plannedDate,
                isCompleted = r.isCompleted
            )
        }

        // Mark overdue as completed
        val now = LocalDateTime.now()
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        allTasks.forEach { t ->
            try {
                val due = LocalDateTime.parse(t.dueDate, fmt)
                if (due.isBefore(now) && !t.isCompleted) {
                    db.update(TaskRecord(
                        id = t.id,
                        name = t.name,
                        description = t.description,
                        priority = t.priority,
                        category = t.category,
                        dueDate = t.dueDate,
                        plannedDate = t.plannedDate,
                        isCompleted = true
                    ))
                }
            } catch (_: Exception) { /* ignore */ }
        }

        // reload & rebuild tree
        allTasks = db.getAll().map { r ->
            Task(r.id, r.name, r.description, r.priority, r.category, r.dueDate, r.plannedDate, r.isCompleted)
        }
        taskTree.clear()
        allTasks.forEach { taskTree.insert(it) }

        applyFilter()
    }

    private fun applyFilter() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val filtered = when (currentCategory) {
            "Today"     -> allTasks.filter { it.dueDate.startsWith(today) && !it.isCompleted }
            "Upcoming"  -> allTasks.filter { it.dueDate > today && !it.isCompleted }
            "Completed" -> allTasks.filter { it.isCompleted }
            else        -> allTasks
        }
        val sorted = taskTree.getTasksInOrder().filter { sorted -> filtered.any { it.id == sorted.id } }
        adapter.submitList(sorted)
        binding.emptyStateLayout.visibility =
            if (sorted.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showAddDialog() {
        val dlg = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val titleIn = dlg.findViewById<EditText>(R.id.input_task_title)
        val notesIn = dlg.findViewById<EditText>(R.id.input_task_notes)
        val catGrp  = dlg.findViewById<RadioGroup>(R.id.input_category_group)
        val priGrp  = dlg.findViewById<RadioGroup>(R.id.input_priority_group)
        val dueTv   = dlg.findViewById<TextView>(R.id.input_due_date)
        val planTv  = dlg.findViewById<TextView>(R.id.input_planned_date)

        // Set up date and time pickers for Due Date
        dueTv.setOnClickListener { showDateTimePicker(dueTv) }

        // Set up date and time pickers for Planned Date
        planTv.setOnClickListener { showDateTimePicker(planTv) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Task")
            .setView(dlg)
            .setPositiveButton("Save") { _, _ ->
                val name = titleIn.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(context, "Name required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val cat = when (catGrp.checkedRadioButtonId) {
                    R.id.radio_work    -> "Work"
                    R.id.radio_personal-> "Personal"
                    R.id.radio_study   -> "Study"
                    else               -> "Personal"
                }
                val pri = when (priGrp.checkedRadioButtonId) {
                    R.id.radio_high   -> "High"
                    R.id.radio_medium -> "Medium"
                    R.id.radio_low    -> "Low"
                    else              -> "Medium"
                }
                val due  = dueTv.text.toString().trim()
                val plan = planTv.text.toString().trim()
                if (due.isBlank() || plan.isBlank()) {
                    Toast.makeText(context, "Pick both dates", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                db.insert(TaskRecord(
                    id = 0L,
                    name = name,
                    description = notesIn.text.toString().trim(),
                    priority = pri,
                    category = cat,
                    dueDate = due,
                    plannedDate = plan,
                    isCompleted = false
                ))
                loadAndShowAll()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(task: Task) {
        // (left for you, same as add)
    }

    private fun showDateTimePicker(dateTextView: TextView) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), {
                _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            TimePickerDialog(requireContext(), {
                    _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                dateTextView.text = format.format(calendar.time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
