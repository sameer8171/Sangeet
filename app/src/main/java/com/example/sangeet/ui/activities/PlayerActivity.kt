package com.example.sangeet.ui.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sangeet.MainActivity
import com.example.sangeet.R
import com.example.sangeet.common.*
import com.example.sangeet.databinding.ActivityPlayerBinding
import com.example.sangeet.model.Music
import com.example.sangeet.services.MusicService

class PlayerActivity : AppCompatActivity(), ServiceConnection , MediaPlayer.OnCompletionListener{
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var musicService: MusicService? = null
        var songPosition: Int = 0
        lateinit var MusicListPA: ArrayList<Music>
        var isRepeat = false
        var isPlaying = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializer()
        startMusicService()
        initView()
    }

    private fun initView() {
        binding.btnPause.setOnClickListener {
            if (!isPlaying) {
                playSong()
            } else {
                pauseSong()
            }
        }
        binding.btnNext.setOnClickListener {
            setSongPosition(true)
            setLayout()
        }
        binding.btnPrevious.setOnClickListener {
            setSongPosition(false)
            setLayout()
        }
        binding.btnRepeat.setOnClickListener {
            repeatSong(this)
        }

        binding.btnSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

    }

    private fun startMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun initializer() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                MusicListPA = ArrayList()
                MusicListPA = (MainActivity.MusicListMA)
                setLayout()
            }
            "MainActivity" -> {
                MusicListPA = ArrayList()
                MusicListPA = (MainActivity.MusicListMA)
                MusicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun setLayout() {
        Glide.with(this)
            .load(MusicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.img_song_cover)).centerCrop()
            .into(binding.imgSongCoverPA)

        binding.tvSongName.text = MusicListPA[songPosition].title

        if (musicService?.mediaPlayer == null) musicService?.mediaPlayer = MediaPlayer()
        musicService?.mediaPlayer?.reset()
        musicService?.mediaPlayer?.setDataSource(MusicListPA[songPosition].path)
        musicService?.mediaPlayer?.prepare()
        musicService?.showNotification(R.drawable.pause)
        musicService?.mediaPlayer?.start()
        isPlaying = true
        binding.btnSeekbar.progress = 0
        binding.tvSeekbarStart.text = musicService?.mediaPlayer?.currentPosition?.toLong()
            ?.let { formatDuration(it) }
        binding.tvSeekbarEnd.text = musicService?.mediaPlayer?.duration?.toLong()?.let { formatDuration(it) }
        musicService?.mediaPlayer?.duration?.let {
            binding.btnSeekbar.max = it
        }
        musicService?.mediaPlayer?.setOnCompletionListener(this)

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        initializer()
        musicService?.showNotification(R.drawable.pause)
        seekbarSetup()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        try {
            setLayout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}