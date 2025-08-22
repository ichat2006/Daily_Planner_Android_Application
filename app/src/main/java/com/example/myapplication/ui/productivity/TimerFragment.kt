package com.example.myapplication.ui.productivity

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {
    private var timer: CountDownTimer? = null
    private var stopwatchStartTime: Long = 0
    private var stopwatchRunning = false
    private var stopwatchHandler: android.os.Handler? = null
    private var stopwatchRunnable: Runnable? = null
    private val lapTimes = mutableListOf<String>()
    private lateinit var lapTimesAdapter: LapTimesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Initialize views
            val timerDisplay = view.findViewById<TextView>(R.id.timer_display)
            val startTimerButton = view.findViewById<MaterialButton>(R.id.start_timer_button)
            val resetTimerButton = view.findViewById<MaterialButton>(R.id.reset_timer_button)
            val timerSlider = view.findViewById<Slider>(R.id.timer_slider)
            val timerSliderValue = view.findViewById<TextView>(R.id.timer_slider_value)
            val stopwatchDisplay = view.findViewById<TextView>(R.id.stopwatch_display)
            val startStopwatchButton = view.findViewById<MaterialButton>(R.id.start_stopwatch_button)
            val lapStopwatchButton = view.findViewById<MaterialButton>(R.id.lap_stopwatch_button)
            val resetStopwatchButton = view.findViewById<MaterialButton>(R.id.reset_stopwatch_button)
            val lapTimesRecyclerView = view.findViewById<RecyclerView>(R.id.lap_times_recycler_view)

            // Setup lap times RecyclerView
            lapTimesAdapter = LapTimesAdapter(lapTimes)
            lapTimesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = lapTimesAdapter
            }

            // Timer functionality
            var isTimerRunning = false
            var timeLeftInMillis: Long = 0

            timerSlider.addOnChangeListener { _, value, _ ->
                try {
                    val minutes = value.toInt()
                    timeLeftInMillis = minutes * 60 * 1000L
                    updateTimerDisplay(timerDisplay, timeLeftInMillis)
                    timerSliderValue.text = "$minutes minutes"
                } catch (e: Exception) {
                    Toast.makeText(context, "Error updating timer: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            startTimerButton.setOnClickListener {
                try {
                    if (!isTimerRunning) {
                        if (timeLeftInMillis > 0) {
                            startTimer(timeLeftInMillis, timerDisplay, startTimerButton)
                            isTimerRunning = true
                            startTimerButton.text = "Pause"
                        } else {
                            Toast.makeText(context, "Please set a time first", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        pauseTimer()
                        isTimerRunning = false
                        startTimerButton.text = "Resume"
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error controlling timer: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            resetTimerButton.setOnClickListener {
                try {
                    resetTimer(timerDisplay, startTimerButton)
                    isTimerRunning = false
                    startTimerButton.text = "Start"
                } catch (e: Exception) {
                    Toast.makeText(context, "Error resetting timer: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Stopwatch functionality
            stopwatchHandler = android.os.Handler(android.os.Looper.getMainLooper())
            stopwatchRunnable = object : Runnable {
                override fun run() {
                    try {
                        val elapsedMillis = System.currentTimeMillis() - stopwatchStartTime
                        updateStopwatchDisplay(stopwatchDisplay, elapsedMillis)
                        stopwatchHandler?.postDelayed(this, 10)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Error updating stopwatch: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            startStopwatchButton.setOnClickListener {
                try {
                    if (!stopwatchRunning) {
                        startStopwatch(stopwatchDisplay, startStopwatchButton)
                        stopwatchRunning = true
                        startStopwatchButton.text = "Pause"
                    } else {
                        pauseStopwatch()
                        stopwatchRunning = false
                        startStopwatchButton.text = "Resume"
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error controlling stopwatch: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            lapStopwatchButton.setOnClickListener {
                try {
                    if (stopwatchRunning) {
                        val elapsedMillis = System.currentTimeMillis() - stopwatchStartTime
                        val lapTime = formatTime(elapsedMillis)
                        lapTimes.add(0, "Lap ${lapTimes.size + 1}: $lapTime")
                        lapTimesAdapter.notifyItemInserted(0)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error recording lap: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            resetStopwatchButton.setOnClickListener {
                try {
                    resetStopwatch(stopwatchDisplay, startStopwatchButton)
                    stopwatchRunning = false
                    startStopwatchButton.text = "Start"
                    lapTimes.clear()
                    lapTimesAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error resetting stopwatch: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error initializing timer/stopwatch: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun startTimer(timeInMillis: Long, display: TextView, button: MaterialButton) {
        try {
            timer?.cancel()
            timer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    try {
                        updateTimerDisplay(display, millisUntilFinished)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFinish() {
                    try {
                        display.text = "00:00:00"
                        button.text = "Start"
                        context?.let {
                            Toast.makeText(it, "Timer finished!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
            context?.let {
                Toast.makeText(it, "Error starting timer: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pauseTimer() {
        try {
            timer?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
            context?.let {
                Toast.makeText(it, "Error pausing timer: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetTimer(display: TextView, button: MaterialButton) {
        try {
            timer?.cancel()
            display.text = "00:00:00"
            button.text = "Start"
        } catch (e: Exception) {
            e.printStackTrace()
            context?.let {
                Toast.makeText(it, "Error resetting timer: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startStopwatch(display: TextView, button: MaterialButton) {
        try {
            stopwatchStartTime = System.currentTimeMillis() - (if (stopwatchStartTime > 0) System.currentTimeMillis() - stopwatchStartTime else 0)
            stopwatchHandler?.post(stopwatchRunnable!!)
        } catch (e: Exception) {
            e.printStackTrace()
            context?.let {
                Toast.makeText(it, "Error starting stopwatch: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pauseStopwatch() {
        try {
            stopwatchHandler?.removeCallbacks(stopwatchRunnable!!)
        } catch (e: Exception) {
            e.printStackTrace()
            context?.let {
                Toast.makeText(it, "Error pausing stopwatch: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetStopwatch(display: TextView, button: MaterialButton) {
        try {
            stopwatchHandler?.removeCallbacks(stopwatchRunnable!!)
            stopwatchStartTime = 0
            display.text = "00:00:00"
            button.text = "Start"
        } catch (e: Exception) {
            e.printStackTrace()
            context?.let {
                Toast.makeText(it, "Error resetting stopwatch: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTimerDisplay(display: TextView, timeInMillis: Long) {
        try {
            display.text = formatTime(timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateStopwatchDisplay(display: TextView, timeInMillis: Long) {
        try {
            display.text = formatTime(timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun formatTime(timeInMillis: Long): String {
        return try {
            val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } catch (e: Exception) {
            e.printStackTrace()
            "00:00:00"
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            timer?.cancel()
            stopwatchHandler?.removeCallbacks(stopwatchRunnable!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        stopwatchHandler?.removeCallbacks(stopwatchRunnable!!)
        stopwatchHandler = null
        stopwatchRunnable = null
    }
}

class LapTimesAdapter(private val lapTimes: List<String>) : RecyclerView.Adapter<LapTimesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = lapTimes[position]
    }

    override fun getItemCount() = lapTimes.size
} 