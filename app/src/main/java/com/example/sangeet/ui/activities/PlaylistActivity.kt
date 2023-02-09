package com.example.sangeet.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sangeet.adapter.PlaylistAdapter
import com.example.sangeet.databinding.ActivityPlaylistBinding

class PlaylistActivity : AppCompatActivity() {
     private lateinit var binding:ActivityPlaylistBinding
     private lateinit var adapter:PlaylistAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        val spacing = 50
        binding.rvList.layoutManager = GridLayoutManager(this,2)
        
        binding.rvList.setHasFixedSize(true)
        binding.rvList.hasFixedSize()
        val list = ArrayList<String>()
        list.add("PlayList")
        list.add("PlayList Ham Tumhare Hai")
        list.add("PlayList Arijit Singh")
        list.add("PlayList Mohd Rafi")
        list.add("PlayList Sonu Nigam")
        list.add("PlayList Kumar Shanu")
        adapter = PlaylistAdapter(list)
        binding.rvList.adapter = adapter

    }
}