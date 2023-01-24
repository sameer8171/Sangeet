package com.example.sangeet.common

import android.app.Activity
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sangeet.R
import com.example.sangeet.services.MusicService
import com.example.sangeet.ui.activities.PlayerActivity
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

/**
 * Created by Mohd Sameer on 20 Jan 2023
 */

/**
 * Show Toast On Activity
 */
fun Context.showToast(message: String?, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message ?: "Something went Wrong", length).show()
}

/** Show Toast on Fragment */
fun Fragment.showToast(message: String?, length: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, length)
}

/** View Visible Ktx */
fun View.show() {
    visibility = View.VISIBLE
}

/** View Hide Ktx */
fun View.hide() {
    visibility = View.GONE
}

/** View Invisible */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/** Back Button Setup */
fun ImageView.back(activity: Activity) {
    setOnClickListener {
        activity.finish()
    }
}

/** Song Duration Convert */
fun formatDuration(duration: Long): String? {
    val minutes: Long = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds: Long = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
            - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}

/** Set Song Position */
fun setSongPosition(increment: Boolean) {
    if (!PlayerActivity.isRepeat) {
        if (increment) {
            if (PlayerActivity.songPosition == PlayerActivity.MusicListPA.size - 1) {
                PlayerActivity.songPosition = 0
            } else {
                ++PlayerActivity.songPosition
            }
        } else {
            if (0 == PlayerActivity.songPosition) {
                PlayerActivity.songPosition = PlayerActivity.MusicListPA.size - 1
            } else {
                --PlayerActivity.songPosition
            }
        }
    }
}

/** Play Song */
fun playSong() {
    PlayerActivity.isPlaying = true
    PlayerActivity.binding.btnPause.setIconResource(R.drawable.pause)
    PlayerActivity.musicService?.showNotification(R.drawable.pause)
    PlayerActivity.musicService?.mediaPlayer?.start()
}

/** Pause Song */
fun pauseSong() {
    PlayerActivity.isPlaying = false
    PlayerActivity.binding.btnPause.setIconResource(R.drawable.play)
    PlayerActivity.musicService?.showNotification(R.drawable.play)
    PlayerActivity.musicService?.mediaPlayer?.pause()
}

/** Repeat Song */
fun repeatSong(context: Context) {
    if (!PlayerActivity.isRepeat) {
        PlayerActivity.isRepeat = true
        PlayerActivity.binding.btnRepeat.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.primary
            )
        )
    } else {
        PlayerActivity.isRepeat = false
        PlayerActivity.binding.btnRepeat.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )
    }
}

/** Seekbar Runnable */
fun seekbarSetup() {
    PlayerActivity.musicService?.runnable = Runnable {
        PlayerActivity.musicService?.mediaPlayer?.currentPosition?.let {
            PlayerActivity.binding.tvSeekbarStart.text = formatDuration(it.toLong())
        }
        PlayerActivity.musicService?.mediaPlayer?.currentPosition?.let {
            PlayerActivity.binding.btnSeekbar.progress = it
        }
        PlayerActivity.musicService?.runnable?.let {
            Handler(Looper.getMainLooper()).postDelayed(
                it, 200
            )
        }
    }
    PlayerActivity.musicService?.runnable?.let {
        Handler(Looper.getMainLooper()).postDelayed(
            it,
            0
        )
    }
}

/** ExitApplication */
fun exit() {
    if (PlayerActivity.musicService != null) {
        PlayerActivity.musicService?.stopForeground(true)
        PlayerActivity.musicService?.mediaPlayer?.release()
        PlayerActivity.musicService = null
        exitProcess(1)
    } else {
        exitProcess(1)
    }
}

/** Image ByteArray*/

fun imageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

/** Next and Previous Song For Notification */
fun preNextSong(increment: Boolean, context: Context) {
    setSongPosition(increment)
    songList()
    Glide.with(context)
        .load(PlayerActivity.MusicListPA[PlayerActivity.songPosition].artUri)
        .apply(RequestOptions().placeholder(R.drawable.img_song_cover)).centerCrop()
        .into(PlayerActivity.binding.imgSongCoverPA)
    PlayerActivity.binding.tvSongName.text =
        PlayerActivity.MusicListPA[PlayerActivity.songPosition].title
    playSong()

}

/** Get Song List */
fun songList() {
    if (PlayerActivity.musicService?.mediaPlayer == null) PlayerActivity.musicService?.mediaPlayer =
        MediaPlayer()
    PlayerActivity.musicService?.mediaPlayer?.reset()
    PlayerActivity.musicService?.mediaPlayer?.setDataSource(PlayerActivity.MusicListPA[PlayerActivity.songPosition].path)
    PlayerActivity.musicService?.mediaPlayer?.prepare()
    PlayerActivity.binding.btnPause.setIconResource(R.drawable.pause)
    PlayerActivity.musicService?.mediaPlayer?.currentPosition?.let {
        PlayerActivity.binding.tvSeekbarStart.text = formatDuration(it.toLong())
    }
    PlayerActivity.musicService?.mediaPlayer?.currentPosition?.let {
        PlayerActivity.binding.btnSeekbar.progress = it
    }
    PlayerActivity.binding.btnSeekbar.progress = 0

    PlayerActivity.musicService?.mediaPlayer?.duration?.let {
        PlayerActivity.binding.btnSeekbar.max = it
    }


}
