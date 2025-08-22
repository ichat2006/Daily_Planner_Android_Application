package com.example.myapplication.ui.productivity

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import java.util.concurrent.TimeUnit

class MusicPlayerFragment : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var updateSeekBarHandler: Handler? = null
    private var updateSeekBarRunnable: Runnable? = null
    private var currentSongIndex = 0
    private val songNames = arrayOf("Relaxing Music", "Focus Music", "Study Music")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        val songTitle = view.findViewById<TextView>(R.id.song_title)
        val musicSeekBar = view.findViewById<SeekBar>(R.id.music_seekbar)
        val currentTime = view.findViewById<TextView>(R.id.current_time)
        val totalTime = view.findViewById<TextView>(R.id.total_time)
        val playPauseButton = view.findViewById<MaterialButton>(R.id.play_pause_button)
        val previousButton = view.findViewById<MaterialButton>(R.id.previous_button)
        val nextButton = view.findViewById<MaterialButton>(R.id.next_button)
        val volumeSlider = view.findViewById<Slider>(R.id.volume_slider)

        try {
            // Set initial song title
            songTitle.text = songNames[currentSongIndex]

            // Initialize MediaPlayer
            initializeMediaPlayer(currentSongIndex)

            // Set initial volume
            mediaPlayer?.setVolume(0.5f, 0.5f)
            volumeSlider.value = 50f

            // Setup seekbar
            musicSeekBar.max = mediaPlayer?.duration ?: 0
            totalTime.text = formatTime(mediaPlayer?.duration?.toLong() ?: 0)

            // Setup handlers for updating seekbar
            updateSeekBarHandler = Handler(Looper.getMainLooper())
            updateSeekBarRunnable = object : Runnable {
                override fun run() {
                    mediaPlayer?.let { player ->
                        if (isPlaying) {
                            try {
                                val currentPosition = player.currentPosition
                                musicSeekBar.progress = currentPosition
                                currentTime.text = formatTime(currentPosition.toLong())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    updateSeekBarHandler?.postDelayed(this, 1000)
                }
            }

            // Play/Pause button click listener
            playPauseButton.setOnClickListener {
                try {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                        playPauseButton.text = "Play"
                        isPlaying = false
                    } else {
                        mediaPlayer?.start()
                        playPauseButton.text = "Pause"
                        isPlaying = true
                        updateSeekBarHandler?.post(updateSeekBarRunnable!!)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error playing music: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // SeekBar change listener
            musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        try {
                            mediaPlayer?.seekTo(progress)
                            currentTime.text = formatTime(progress.toLong())
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error seeking: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // Volume slider change listener
            volumeSlider.addOnChangeListener { _, value, _ ->
                try {
                    val volume = value / 100f
                    mediaPlayer?.setVolume(volume, volume)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error adjusting volume: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Previous button click listener
            previousButton.setOnClickListener {
                try {
                    currentSongIndex = (currentSongIndex - 1 + songNames.size) % songNames.size
                    changeSong(currentSongIndex, songTitle, musicSeekBar, totalTime, playPauseButton)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error changing song: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Next button click listener
            nextButton.setOnClickListener {
                try {
                    currentSongIndex = (currentSongIndex + 1) % songNames.size
                    changeSong(currentSongIndex, songTitle, musicSeekBar, totalTime, playPauseButton)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error changing song: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Set completion listener to automatically play next song
            mediaPlayer?.setOnCompletionListener {
                try {
                    currentSongIndex = (currentSongIndex + 1) % songNames.size
                    changeSong(currentSongIndex, songTitle, musicSeekBar, totalTime, playPauseButton)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error on completion: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Error initializing music player: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun initializeMediaPlayer(songIndex: Int) {
        try {
            // Release any existing MediaPlayer
            mediaPlayer?.release()
            
            // Create a new MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                
                // Try to load from assets
                try {
                    val assetFileName = when (songIndex) {
                        0 -> "relaxing_music.mp3"
                        1 -> "focus_music.mp3"
                        2 -> "study_music.mp3"
                        else -> "relaxing_music.mp3"
                    }
                    
                    // Attempt to load from assets
                    context?.assets?.openFd(assetFileName)?.use { afd ->
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    }
                } catch (e: Exception) {
                    // If asset not found, use raw resource as fallback
                    val rawResId = when (songIndex) {
                        0 -> R.raw.relaxing_music
                        1 -> R.raw.focus_music
                        2 -> R.raw.study_music
                        else -> R.raw.relaxing_music
                    }
                    
                    context?.resources?.openRawResourceFd(rawResId)?.use { rfd ->
                        setDataSource(rfd.fileDescriptor, rfd.startOffset, rfd.length)
                    }
                }
                
                prepare()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error initializing player: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun changeSong(songIndex: Int, titleView: TextView, seekBar: SeekBar, totalTimeView: TextView, playPauseButton: MaterialButton) {
        try {
            // Update song title
            titleView.text = songNames[songIndex]
            
            // Initialize new MediaPlayer
            initializeMediaPlayer(songIndex)
            
            // Update seekbar
            seekBar.max = mediaPlayer?.duration ?: 0
            seekBar.progress = 0
            
            // Update time displays
            totalTimeView.text = formatTime(mediaPlayer?.duration?.toLong() ?: 0)
            
            // Start playing
            mediaPlayer?.start()
            isPlaying = true
            playPauseButton.text = "Pause"
            
        } catch (e: Exception) {
            Toast.makeText(context, "Error changing song: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        try {
            mediaPlayer?.pause()
            isPlaying = false
            updateSeekBarHandler?.removeCallbacks(updateSeekBarRunnable!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            updateSeekBarHandler?.removeCallbacks(updateSeekBarRunnable!!)
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 