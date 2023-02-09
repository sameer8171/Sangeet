package com.example.sangeet.ui.activities

import android.R.attr.*
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sangeet.MainActivity
import com.example.sangeet.R
import com.example.sangeet.common.*
import com.example.sangeet.databinding.ActivityPlayerBinding
import com.example.sangeet.model.Music
import com.example.sangeet.services.MusicService
import com.example.sangeet.ui.fragment.BottomSheetFragment
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var musicService: MusicService? = null
        var songPosition: Int = 0
        lateinit var MusicListPA: ArrayList<Music>
        var isRepeat = false
        var isPlaying = false
        var nowPlayingId = ""
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var isFavorite: Boolean = false
        var fIndex: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializer()
        initView()

        val ac: ActionBar? = supportActionBar
        ac?.hide()
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
            preNextSong(
                increment = true,
                context = this,
                imgId = binding.imgSongCoverPA,
                tvId = binding.tvSongName
            )
        }
        binding.btnPrevious.setOnClickListener {
            preNextSong(
                increment = false,
                context = this,
                imgId = binding.imgSongCoverPA,
                tvId = binding.tvSongName
            )
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

        binding.btnEqualizer.setOnClickListener {
            try {
                soundEqualizer()
            } catch (e: Exception) {
                showToast("Equalizer Feature not Supported!! ")
            }
        }
        binding.btnTimer.setOnClickListener {
            val frag = BottomSheetFragment()
            frag.show(supportFragmentManager, "bottomSheet")
        }
        binding.btnShare.setOnClickListener { shareSong() }

        binding.navbar.imgFavorite.setOnClickListener {
            setLayout()
            if (isFavorite) {
                isFavorite = false
                binding.navbar.imgFavorite.setImageResource(R.drawable.favorite_empty)
                FavoriteActivity.favoriteSong.removeAt(fIndex)
            } else {
                isFavorite = true
                binding.navbar.imgFavorite.setImageResource(R.drawable.favorite)
                FavoriteActivity.favoriteSong.add(MusicListPA[songPosition])
            }
        }
        binding.navbar.btnBack.setOnClickListener {
            finish()
        }


    }


    private fun startMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun initializer() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "FavoriteShuffle" -> {
                startMusicService()
                MusicListPA = ArrayList()
                MusicListPA = (FavoriteActivity.favoriteSong)
                MusicListPA.shuffle()
                setLayout()
            }

            "FavoritePlayer" -> {
                startMusicService()
                MusicListPA = ArrayList()
                MusicListPA = (FavoriteActivity.favoriteSong)
                setLayout()
            }

            "NowPlayer" -> {
                binding.tvSeekbarStart.text =
                    musicService?.mediaPlayer?.currentPosition?.toLong()?.let { formatDuration(it) }
                binding.tvSeekbarEnd.text =
                    musicService?.mediaPlayer?.duration?.toLong()?.let { formatDuration(it) }
                musicService?.mediaPlayer?.currentPosition?.let {
                    binding.btnSeekbar.progress = it
                }
                musicService?.mediaPlayer?.duration?.let {
                    binding.btnSeekbar.max = it
                }
                setLayout()
                if (isPlaying) binding.btnPause.setIconResource(R.drawable.pause)
                else binding.btnPause.setIconResource(R.drawable.play)
            }
            "SearchAdapter" -> {
                startMusicService()
                MusicListPA = ArrayList()
                MusicListPA = (MainActivity.MusicListMASearch)
                setLayout()
            }
            "MusicAdapter" -> {
                startMusicService()
                MusicListPA = ArrayList()
                MusicListPA = (MainActivity.MusicListMA)
                setLayout()
            }
            "MainActivity" -> {
                startMusicService()
                MusicListPA = ArrayList()
                MusicListPA = (MainActivity.MusicListMA)
                MusicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun setLayout() {
        MainActivity.search = false
        fIndex = favoriteChecker(MusicListPA[songPosition].id)
        Glide.with(this)
            .load(MusicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.img_song_cover)).centerCrop()
            .into(binding.imgSongCoverPA)
        binding.tvSongName.text = MusicListPA[songPosition].title
        if (isFavorite) binding.navbar.imgFavorite.setImageResource(R.drawable.favorite)
        else binding.navbar.imgFavorite.setImageResource(R.drawable.favorite_empty)
    }

    private fun createMediaPlayer() {
        try {
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
            binding.tvSeekbarEnd.text =
                musicService?.mediaPlayer?.duration?.toLong()?.let { formatDuration(it) }
            musicService?.mediaPlayer?.duration?.let {
                binding.btnSeekbar.max = it
            }
            musicService?.mediaPlayer?.setOnCompletionListener(this)
            nowPlayingId = MusicListPA[songPosition].id

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        initializer()
        createMediaPlayer()
        musicService?.showNotification(R.drawable.pause)
        seekbarSetup()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        try {
            createMediaPlayer()
            setLayout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun soundEqualizer() {
        val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        eqIntent.putExtra(
            AudioEffect.EXTRA_AUDIO_SESSION,
            musicService!!.mediaPlayer!!.audioSessionId
        )
        eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
        eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
        startActivityForResult(eqIntent, 13)
    }

    private fun shareSong() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "audio/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MusicListPA[songPosition].path))
        startActivity(Intent.createChooser(shareIntent, "Sharing Music File!! "))
    }




}