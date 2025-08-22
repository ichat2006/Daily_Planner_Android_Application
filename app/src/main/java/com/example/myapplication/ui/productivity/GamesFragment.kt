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
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView

class GamesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games, container, false)

        try {
            // Set background gradient
            val rootLayout = view.findViewById<LinearLayout>(R.id.games_root_layout)
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.parseColor("#D4F1F9"),
                    Color.parseColor("#E2F8CB")
                )
            )
            rootLayout.background = gradient

            // Setup card styling
            val quickMathCard = view.findViewById<MaterialCardView>(R.id.quick_math_card)
            val wordScrambleCard = view.findViewById<MaterialCardView>(R.id.word_scramble_card)
            // val memoryMatchCard = view.findViewById<MaterialCardView>(R.id.memory_match_card)
            
            setupCardStyle(quickMathCard)
            setupCardStyle(wordScrambleCard)
            // setupCardStyle(memoryMatchCard)

            // Setup navigation
            view.findViewById<MaterialButton>(R.id.play_quick_math)?.setOnClickListener {
                try {
                    findNavController().navigate(R.id.action_games_to_quick_math)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error navigating to Quick Math: ${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            view.findViewById<MaterialButton>(R.id.play_word_scramble)?.setOnClickListener {
                try {
                    findNavController().navigate(R.id.action_games_to_word_scramble)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error navigating to Word Scramble: ${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            // Remove memory game button listener
            // view.findViewById<MaterialButton>(R.id.play_memory_game)?.setOnClickListener {
            //     try {
            //         findNavController().navigate(R.id.action_games_to_memory_match)
            //     } catch (e: Exception) {
            //         Toast.makeText(context, "Error navigating to Memory Match: ${e.message}", Toast.LENGTH_SHORT).show()
            //         e.printStackTrace()
            //     }
            // }
        } catch (e: Exception) {
            Toast.makeText(context, "Error initializing games screen: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

        return view
    }
    
    private fun setupCardStyle(card: MaterialCardView) {
        try {
            card.apply {
                radius = 16f
                cardElevation = 8f
                setCardBackgroundColor(Color.WHITE)
                strokeColor = Color.parseColor("#E0E0E0")
                strokeWidth = 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 