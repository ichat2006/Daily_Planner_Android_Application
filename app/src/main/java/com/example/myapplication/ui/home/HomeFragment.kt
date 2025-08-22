package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import android.widget.TextView
import android.widget.ImageView
import com.example.myapplication.ui.tasks.TaskTree
import com.example.myapplication.ui.tasks.TaskDbHelper
import com.example.myapplication.ui.tasks.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskTree: TaskTree
    private lateinit var db: TaskDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        
        // Initialize task tree and database
        taskTree = TaskTree()
        db = TaskDbHelper(requireContext())
        
        // Load tasks and check for urgent ones
        loadAndCheckUrgentTasks(root)
        
        return root
    }

    override fun onResume() {
        super.onResume()
        // Refresh urgent tasks when returning to this fragment
        view?.let { loadAndCheckUrgentTasks(it) }
    }

    private fun loadAndCheckUrgentTasks(view: View) {
        // Load all tasks into the tree
        val tasks = db.getAll().map { r ->
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
        
        taskTree.clear()
        tasks.forEach { taskTree.insert(it) }
        
        // Get tasks due within 6 hours
        val urgentTasks = taskTree.getTasksDueWithinHours(6)
        
        // Find high priority tasks
        val highPriorityTasks = tasks.filter { 
            it.priority.equals("High", ignoreCase = true) && !it.isCompleted 
        }
        
        // Combine urgent and high priority tasks, removing duplicates
        val tasksToShow = (urgentTasks + highPriorityTasks).distinctBy { it.id }.sortedBy { 
            // Sort by due date for display, urgent first
            try {
                LocalDateTime.parse(it.dueDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            } catch (e: Exception) {
                LocalDateTime.MAX // Place tasks with invalid dates at the end
            }
        }
        
        // Update UI
        val currentTaskImage = view.findViewById<ImageView>(R.id.currentTaskImage)
        val currentTaskLabel = view.findViewById<TextView>(R.id.current_task_label)
        val currentTaskDetails = view.findViewById<TextView>(R.id.current_task_details)
        
        if (tasksToShow.isNotEmpty()) {
            // Show all urgent and high priority tasks
            currentTaskImage.setImageResource(R.drawable.ic_task)
            currentTaskLabel.text = if (urgentTasks.isNotEmpty() && highPriorityTasks.isNotEmpty()) {
                "URGENT & HIGH PRIORITY TASKS"
            } else if (urgentTasks.isNotEmpty()) {
                "URGENT TASKS"
            } else {
                "HIGH PRIORITY TASKS"
            }
            currentTaskDetails.text = buildString {
                tasksToShow.forEach { task ->
                    append("• ${task.name}\n")
                    append("  Due: ${formatDateTime(task.dueDate)}")
                    if (task.priority.equals("High", ignoreCase = true)) {
                        append(" (High Priority)")
                    }
                    // Indicate if it's also urgent (only if not already covered by the main label)
                    if (!urgentTasks.contains(task) && highPriorityTasks.contains(task) && isTaskUrgent(task)) {
                         append(" (Urgent)")
                    }
                    append("\n\n")
                }
            }.trim()
        } else {
            // No urgent or high priority tasks
            currentTaskImage.setImageResource(R.drawable.ic_empty_tasks)
            currentTaskLabel.text = "Current Task"
            currentTaskDetails.text = "No urgent tasks right now!"
        }
    }

    private fun isTaskUrgent(task: Task): Boolean {
         return try {
             val dueDate = LocalDateTime.parse(task.dueDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
             val now = LocalDateTime.now()
             dueDate.isAfter(now) && dueDate.isBefore(now.plusHours(6))
         } catch (e: Exception) {
             false
         }
    }

    private fun formatDateTime(dateTimeStr: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val dateTime = LocalDateTime.parse(dateTimeStr, formatter)
            val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
            dateTime.format(displayFormatter)
        } catch (e: Exception) {
            dateTimeStr
        }
    }
}