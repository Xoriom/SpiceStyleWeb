package com.example.spicestyle

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NowPlayingActivity : AppCompatActivity() {

    private val tokenStore by lazy { TokenStore(this) }
    private val api by lazy { SpotifyClient.authed(tokenStore.accessToken ?: "") }

    private lateinit var coverArt: ImageView
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var elapsed: TextView
    private lateinit var duration: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var prevBtn: MaterialButton
    private lateinit var playPauseBtn: MaterialButton
    private lateinit var nextBtn: MaterialButton
    private lateinit var shuffleBtn: MaterialButton
    private lateinit var repeatBtn: MaterialButton

    private var isPlaying: Boolean = false
    private var totalMs: Long = 0L
    private var progressMs: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_playing)

        if (tokenStore.accessToken.isNullOrEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        coverArt = findViewById(R.id.coverArt)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        elapsed = findViewById(R.id.elapsed)
        duration = findViewById(R.id.duration)
        seekBar = findViewById(R.id.seekBar)
        prevBtn = findViewById(R.id.prevBtn)
        playPauseBtn = findViewById(R.id.playPauseBtn)
        nextBtn = findViewById(R.id.nextBtn)
        shuffleBtn = findViewById(R.id.shuffleBtn)
        repeatBtn = findViewById(R.id.repeatBtn)

        // Controls
        prevBtn.setOnClickListener { lifecycleScope.launch { api.previous(); refresh() } }
        nextBtn.setOnClickListener { lifecycleScope.launch { api.next(); refresh() } }
        playPauseBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    if (isPlaying) api.pause() else api.play()
                } catch (_: Throwable) {}
                refresh()
            }
        }
        shuffleBtn.setOnClickListener {
            lifecycleScope.launch { api.shuffle(true); Toast.makeText(this@NowPlayingActivity, "Shuffle on", Toast.LENGTH_SHORT).show() }
        }
        repeatBtn.setOnClickListener {
            lifecycleScope.launch { api.repeat("context"); Toast.makeText(this@NowPlayingActivity, "Repeat context", Toast.LENGTH_SHORT).show() }
        }

        // Seekbar drag → seek
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {
                val newPos = (sb?.progress ?: 0).toLong()
                lifecycleScope.launch { api.seek(newPos); refresh() }
            }
        })

        // Initial refresh
        lifecycleScope.launch { refresh() }

        // Poll/ticker loop while resumed
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (true) {
                    if (isPlaying) {
                        progressMs = (progressMs + 1000L).coerceAtMost(totalMs)
                        updateProgressUI()
                    }
                    delay(1000L)
                    refresh()
                }
            }
        }
    }

    private suspend fun refresh() {
        try {
            val resp = api.currentlyPlaying()
            if (resp.isSuccessful) {
                val cp = resp.body()
                val track = cp?.item
                isPlaying = cp?.isPlaying == true
                progressMs = cp?.progressMs ?: 0L
                totalMs = track?.durationMs ?: 0L

                title.text = track?.name ?: "Nothing playing"
                val artist = track?.artists?.joinToString(", ") { it.name.orEmpty() }.orEmpty()
                val album = track?.album?.name.orEmpty()
                subtitle.text = if (artist.isBlank() && album.isBlank()) "" else "$artist • $album"

                val imgUrl = track?.album?.images?.maxByOrNull { it.width ?: 0 }?.url
                if (!imgUrl.isNullOrEmpty()) {
                    Glide.with(this@NowPlayingActivity).load(imgUrl).into(coverArt)
                } else {
                    coverArt.setImageDrawable(null)
                }

                updateProgressUI()
                playPauseBtn.setIconResource(
                    if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
                )
            } else if (resp.code() == 204) {
                title.text = "Open Spotify and start playback"
                subtitle.text = ""
                coverArt.setImageDrawable(null)
                isPlaying = false
                totalMs = 0
                progressMs = 0
                updateProgressUI()
                playPauseBtn.setIconResource(android.R.drawable.ic_media_play)
            }
        } catch (e: HttpException) {
            // often 403/404 when no active device—ignore gently
        } catch (_: Throwable) { }
    }

    private fun updateProgressUI() {
        seekBar.max = totalMs.toInt()
        seekBar.progress = progressMs.toInt()
        elapsed.text = formatMs(progressMs)
        duration.text = formatMs(totalMs)
    }

    private fun formatMs(ms: Long): String {
        val totalSeconds = (ms / 1000).toInt()
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "%d:%02d".format(m, s)
    }
}
