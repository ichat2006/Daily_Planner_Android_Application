package com.example.myapplication.ui.social

data class SocialActivity(
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val dateTime: String,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val organizer: String,
    val contactInfo: String
) 