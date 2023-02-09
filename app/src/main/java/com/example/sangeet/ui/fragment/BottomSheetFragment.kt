package com.example.sangeet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sangeet.common.exit
import com.example.sangeet.common.showToast
import com.example.sangeet.databinding.FragmentBottomSheetBinding
import com.example.sangeet.ui.activities.PlayerActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.btn15Minutes.setOnClickListener {
            showToast("15 Minutes")
            PlayerActivity.min15 = true
            Thread {
                Thread.sleep(15 * 60000)
                if (PlayerActivity.min15) {
                    exit()
                }
            }.start()
        }

        binding.btn30Minute.setOnClickListener {
            showToast("30 Minutes")
            PlayerActivity.min30 = true
            Thread {
                Thread.sleep(30 * 60000)
                if (PlayerActivity.min30) {
                    exit()
                }
            }.start()
        }

        binding.btn60Minute.setOnClickListener {
            showToast("60 Minutes")
            PlayerActivity.min60 = true
            Thread {
                Thread.sleep(60 * 60000)
                if (PlayerActivity.min60) {
                    exit()
                }
            }.start()
        }
    }
}