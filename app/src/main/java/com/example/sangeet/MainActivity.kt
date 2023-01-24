package com.example.sangeet


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sangeet.adapter.MusicAdapter
import com.example.sangeet.common.exit
import com.example.sangeet.common.showToast
import com.example.sangeet.databinding.ActivityMainBinding
import com.example.sangeet.model.Music
import com.example.sangeet.ui.activities.PlayerActivity
import java.io.File
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var adapter: MusicAdapter

    companion object {
        lateinit var MusicListMA: ArrayList<Music>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        runTimePermission()
    }

    private fun runTimePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawerMenu, R.string.nav_open, R.string.nav_close)
        binding.drawerMenu.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.drawerView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.about -> {
                    showToast("About")
                    true
                }
                R.id.setting -> {
                    showToast("Setting")
                    true
                }
                R.id.feedBack -> {
                    showToast("FeedBack")
                    true
                }
                R.id.exit -> {
                    exit()
                    true
                }

                else -> {
                    true
                }

            }
        }
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.setHasFixedSize(true)
        binding.rvList.setItemViewCacheSize(13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MusicListMA = getSongList()
        }
        adapter = MusicAdapter(this, MusicListMA) {
            Intent(this, PlayerActivity::class.java).also { intent ->
                intent.putExtra("index", it)
                intent.putExtra("class", "MusicAdapter")
                startActivity(intent)
            }
        }
        binding.rvList.adapter = adapter
        binding.tvTotalSong.text = " Total Song : ${adapter.itemCount}"
        binding.btnShuffle.setOnClickListener {
            Intent(this, PlayerActivity::class.java).also { intent ->
                intent.putExtra("class", "MainActivity")
                startActivity(intent)
            }
        }

    }

    @SuppressLint("Range")
    private fun getSongList(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED, null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUri = Uri.withAppendedPath(uri, albumIdC).toString()

                    val music = Music(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        artUri = artUri
                    )
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }

        return tempList
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerMenu.isDrawerOpen(GravityCompat.START)) {
            binding.drawerMenu.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Permission Granted")
                initView()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view, menu)
        val searchView = menu?.findItem(R.id.etSearch)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                val newList = MusicListMA.filter { it.toString().lowercase().contains(newText.toString().lowercase()) }
                adapter.updateList(newList)

                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

}