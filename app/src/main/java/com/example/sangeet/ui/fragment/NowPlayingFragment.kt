package com.example.sangeet.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sangeet.R
import com.example.sangeet.common.*
import com.example.sangeet.databinding.FragmentNowPlayingBinding
import com.example.sangeet.ui.activities.PlayerActivity


class NowPlayingFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNowPlayingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.invisible()
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.isPlaying) {
            binding.root.show()
            binding.btnPlay.setIconResource(R.drawable.pause)
            binding.tvSongTitle.isSelected = true
            setLayout(requireContext())
        }
        initView()
    }

    private fun initView() {
        binding.btnPlay.setOnClickListener {
            if (!PlayerActivity.isPlaying) {
                playSong()
            } else {
                pauseSong()
            }
        }
        binding.btnNext.setOnClickListener {
            preNextSong(
                increment = true,
                context = requireContext(),
                imgId = binding.imgSongCover,
                tvId = binding.tvSongTitle
            )
        }

        binding.root.setOnClickListener {
            Intent(requireContext(), PlayerActivity::class.java).also { intent ->
                intent.putExtra("index", PlayerActivity.songPosition)
                intent.putExtra("class", "NowPlayer")
                startActivity(intent)
            }
        }

    }


}