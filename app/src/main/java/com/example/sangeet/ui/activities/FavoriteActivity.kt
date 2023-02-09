package com.example.sangeet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sangeet.adapter.FavoriteAdapter
import com.example.sangeet.databinding.ActivityFavoriteBinding
import com.example.sangeet.model.Music

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavoriteAdapter

    companion object {
        var favoriteSong: ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initView() {
        binding.rvList.hasFixedSize()
        binding.rvList.setHasFixedSize(true)
        binding.rvList.layoutManager = GridLayoutManager(this, 3)
        adapter = FavoriteAdapter(context = this, itemList = favoriteSong) { index ->
            Intent(this, PlayerActivity::class.java).also { intent ->
                intent.putExtra("index", index)
                intent.putExtra("class", "FavoritePlayer")
                startActivity(intent)
            }
        }
        binding.rvList.adapter = adapter
        if(favoriteSong.size < 1){
             binding.btnShuffle.hide()
        }else{
            binding.btnShuffle.show()
        }

        binding.btnShuffle.setOnClickListener {
            Intent(this, PlayerActivity::class.java).also { intent ->
                intent.putExtra("class", "FavoriteShuffle")
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (favoriteSong != null) {
            adapter.updateList(favoriteSong)
        }else{
            adapter.updateList(emptyList())
        }
    }
}