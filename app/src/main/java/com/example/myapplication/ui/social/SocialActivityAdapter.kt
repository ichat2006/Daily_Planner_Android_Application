package com.example.myapplication.ui.social

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton

class SocialActivityAdapter(
    private val onJoinClick: (SocialActivity) -> Unit
) : ListAdapter<SocialActivity, SocialActivityAdapter.ViewHolder>(SocialActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_social_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.activity_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.activity_description)
        private val locationTextView: TextView = itemView.findViewById(R.id.activity_location)
        private val dateTimeTextView: TextView = itemView.findViewById(R.id.activity_datetime)
        private val participantsTextView: TextView = itemView.findViewById(R.id.activity_participants)
        private val joinButton: MaterialButton = itemView.findViewById(R.id.join_button)

        fun bind(activity: SocialActivity) {
            titleTextView.text = activity.title
            descriptionTextView.text = activity.description
            locationTextView.text = activity.location
            dateTimeTextView.text = activity.dateTime
            participantsTextView.text = "${activity.currentParticipants}/${activity.maxParticipants} participants"
            
            joinButton.isEnabled = activity.currentParticipants < activity.maxParticipants
            joinButton.setOnClickListener { onJoinClick(activity) }
        }
    }
}

class SocialActivityDiffCallback : DiffUtil.ItemCallback<SocialActivity>() {
    override fun areItemsTheSame(oldItem: SocialActivity, newItem: SocialActivity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SocialActivity, newItem: SocialActivity): Boolean {
        return oldItem == newItem
    }
} 