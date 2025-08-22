package com.example.myapplication.ui.productivity

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ProductivityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_productivity, container, false)

        // Configure background gradient
        val rootLayout = view.findViewById<LinearLayout>(R.id.productivity_root_layout)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.parseColor("#F8CDDA"),
                Color.parseColor("#D8E3F8")
            )
        )
        rootLayout.background = gradient

        // Style cards
        val timerCard = view.findViewById<MaterialCardView>(R.id.timer_card)
        val musicCard = view.findViewById<MaterialCardView>(R.id.music_card)
        val gamesCard = view.findViewById<MaterialCardView>(R.id.games_card)
        setupCardStyle(timerCard)
        setupCardStyle(musicCard)
        setupCardStyle(gamesCard)

        // Navigation buttons
        view.findViewById<MaterialButton>(R.id.timer_button)?.setOnClickListener {
            findNavController().navigate(R.id.action_productivityFragment_to_timerFragment)
        }
        view.findViewById<MaterialButton>(R.id.stopwatch_button)?.setOnClickListener {
            findNavController().navigate(R.id.action_productivityFragment_to_timerFragment)
        }
        view.findViewById<MaterialButton>(R.id.music_player_button)?.setOnClickListener {
            findNavController().navigate(R.id.action_productivityFragment_to_musicPlayerFragment)
        }
        view.findViewById<MaterialButton>(R.id.games_button)?.setOnClickListener {
            findNavController().navigate(R.id.action_productivityFragment_to_gamesFragment)
        }

        return view
    }

    private fun setupCardStyle(card: MaterialCardView) {
        card.apply {
            radius = 16f
            cardElevation = 8f
            setCardBackgroundColor(Color.WHITE)
            strokeColor = Color.parseColor("#E0E0E0")
            strokeWidth = 1
        }
    }
}