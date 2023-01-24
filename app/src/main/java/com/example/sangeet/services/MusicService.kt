package com.example.sangeet.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.sangeet.R
import com.example.sangeet.common.Constants
import com.example.sangeet.common.imageArt
import com.example.sangeet.ui.activities.PlayerActivity

class MusicService : Service() {
    private var myBinder = MyBinder()
    private lateinit var mediaSession: MediaSessionCompat
    lateinit var runnable: Runnable
    var mediaPlayer: MediaPlayer? = null
    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseIcon: Int) {

        val prevIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Constants.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Constants.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Constants.PLAY)
        val playPausePendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Constants.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val imgArt = imageArt(PlayerActivity.MusicListPA[PlayerActivity.songPosition].path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0,imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.img_song_cover)
        }

        val notification = NotificationCompat.Builder(baseContext, Constants.CHANNEL_ID)
            .setContentTitle(PlayerActivity.MusicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.MusicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_note)
            .setLargeIcon(image)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous, "previous", prevPendingIntent)
            .addAction(playPauseIcon, "previous", playPausePendingIntent)
            .addAction(R.drawable.next, "previous", nextPendingIntent)
            .addAction(R.drawable.exit, "previous", exitPendingIntent)
            .build()
        startForeground(13, notification)
    }
}