package com.example.myapplication.ui.productivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class WordScrambleFragment : Fragment() {
    private var score = 0
    private lateinit var currentWord: String
    private lateinit var scrambledWord: String
    private val words = listOf("android", "kotlin", "fragment", "layout", "activity", "intent", "recycler", "adapter", "viewmodel", "navigation")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_word_scramble, container, false)
        val scoreText = view.findViewById<TextView>(R.id.word_scramble_score)
        val scrambledText = view.findViewById<TextView>(R.id.word_scramble_scrambled)
        val answerInput = view.findViewById<EditText>(R.id.word_scramble_answer)
        val submitButton = view.findViewById<MaterialButton>(R.id.word_scramble_submit)

        fun scramble(word: String): String {
            val chars = word.toCharArray()
            chars.shuffle()
            return String(chars)
        }

        fun generateWord() {
            currentWord = words[Random.nextInt(words.size)]
            scrambledWord = scramble(currentWord)
            // Ensure scrambled word is not the same as the original
            while (scrambledWord == currentWord) {
                scrambledWord = scramble(currentWord)
            }
            scrambledText.text = scrambledWord
            answerInput.text.clear()
        }

        generateWord()

        submitButton.setOnClickListener {
            val userAnswer = answerInput.text.toString().trim().lowercase()
            if (userAnswer == currentWord) {
                score++
                Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Wrong! The word was $currentWord", Toast.LENGTH_SHORT).show()
            }
            scoreText.text = "Score: $score"
            generateWord()
        }

        return view
    }
} 