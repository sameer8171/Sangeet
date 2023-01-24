package com.example.sangeet.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sangeet.common.*
import com.example.sangeet.ui.activities.PlayerActivity

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
             Constants.PREVIOUS ->{
                 if (context != null) {
                     preNextSong(false,context = context)
                 }
             }
            Constants.PLAY ->{
                  if (!PlayerActivity.isPlaying){
                      playSong()
                  }else{
                      pauseSong()
                  }
            }
            Constants.NEXT ->{
                if (context != null) {
                    preNextSong(true,context = context)
                }
            }
            Constants.EXIT ->{
                   exit()
            }
        }
    }
}