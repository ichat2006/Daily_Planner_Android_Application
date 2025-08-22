// TasksAdapter.kt
package com.example.myapplication.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

public class TasksAdapter(
    private val onDelete: (Long)->Unit,
    private val onEdit: (Task)->Unit
) : ListAdapter<Task, TasksAdapter.VH>(object: DiffUtil.ItemCallback<Task>(){
    override fun areItemsTheSame(a:Task,b:Task)=a.id==b.id
    override fun areContentsTheSame(a:Task,b:Task)=a==b
}) {
    inner class VH(v:View):RecyclerView.ViewHolder(v){
        private val title = v.findViewById<TextView>(R.id.task_title)
        private val desc  = v.findViewById<TextView>(R.id.task_description)
        private val due   = v.findViewById<TextView>(R.id.task_due_date)
        private val plan  = v.findViewById<TextView>(R.id.task_planned_date)
        private val pri   = v.findViewById<TextView>(R.id.task_priority)
        private val cat   = v.findViewById<TextView>(R.id.task_category)
        private val del   = v.findViewById<ImageButton>(R.id.btn_delete)

        fun bind(t:Task){
            title.text = t.name
            desc .text = t.description
            due  .text = t.dueDate
            plan .text = t.plannedDate
            
            // Set priority with appropriate color
            pri.text = t.priority
            pri.setBackgroundResource(when(t.priority.lowercase()) {
                "high" -> R.drawable.priority_high_background
                "medium" -> R.drawable.priority_medium_background
                else -> R.drawable.priority_low_background
            })
            
            // Set category
            cat.text = t.category
            cat.setBackgroundResource(R.drawable.category_background)
            
            del.setOnClickListener { onDelete(t.id) }
            itemView.setOnClickListener{ onEdit(t) }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, i: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_task,p,false))

    override fun onBindViewHolder(h: VH, pos: Int) =
        h.bind(getItem(pos))
}