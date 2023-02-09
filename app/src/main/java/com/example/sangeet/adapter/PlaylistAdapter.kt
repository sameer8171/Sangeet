package com.example.sangeet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sangeet.databinding.PlaylistBinding

class PlaylistAdapter(private val listItem:List<String>):RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    class ViewHolder(private val binding:PlaylistBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data:String){
            binding.tvPlaylistName.text = data
            binding.tvPlaylistName.isSelected = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PlaylistBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listItem[position])
    }

    override fun getItemCount() = listItem.size
}