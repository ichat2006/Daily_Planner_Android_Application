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

class QuickMathFragment : Fragment() {
    private var score = 0
    private var correctAnswer = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quick_math, container, false)
        val scoreText = view.findViewById<TextView>(R.id.quick_math_score)
        val questionText = view.findViewById<TextView>(R.id.quick_math_question)
        val answerInput = view.findViewById<EditText>(R.id.quick_math_answer)
        val submitButton = view.findViewById<MaterialButton>(R.id.quick_math_submit)

        fun generateQuestion() {
            val a = Random.nextInt(1, 20)
            val b = Random.nextInt(1, 20)
            correctAnswer = a + b
            questionText.text = "What is $a + $b?"
            answerInput.text.clear()
        }

        generateQuestion()

        submitButton.setOnClickListener {
            val userAnswer = answerInput.text.toString().toIntOrNull()
            if (userAnswer == correctAnswer) {
                score++
                Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Wrong! The answer was $correctAnswer", Toast.LENGTH_SHORT).show()
            }
            scoreText.text = "Score: $score"
            generateQuestion()
        }

        return view
    }
} 