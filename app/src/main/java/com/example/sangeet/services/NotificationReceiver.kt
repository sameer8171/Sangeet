package com.example.sangeet.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sangeet.common.*
import com.example.sangeet.ui.fragment.NowPlayingFragment
import com.example.sangeet.ui.activities.PlayerActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Constants.PREVIOUS -> {
                if (context != null) {
                    preNextSong(
                        false,
                        context = context,
                        imgId = PlayerActivity.binding.imgSongCoverPA,
                        PlayerActivity.binding.tvSongName
                    )
                    setLayout(context)
                }
            }
            Constants.PLAY -> {
                if (!PlayerActivity.isPlaying) {
                    playSong()
                    NowPlayingFragment.binding.root.show()
                } else {
                    pauseSong()
                    NowPlayingFragment.binding.root.invisible()
                }
            }
            Constants.NEXT -> {
                if (context != null) {
                    preNextSong(
                        true,
                        context = context,
                        imgId = PlayerActivity.binding.imgSongCoverPA,
                        PlayerActivity.binding.tvSongName
                    )
                    setLayout(context)
                }
            }
            Constants.EXIT -> {
                exit()
            }
        }
    }
}