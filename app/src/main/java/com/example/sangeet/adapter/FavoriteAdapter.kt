package com.example.sangeet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sangeet.R
import com.example.sangeet.databinding.FavoriteListBinding
import com.example.sangeet.model.Music

class FavoriteAdapter(
    private val context: Context,
    private var itemList: List<Music>,
    private val action: (Int) -> Unit
) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: FavoriteListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Music) {
            binding.tvSongTitle.text = data.title
            Glide.with(context)
                .load(data.artUri)
                .apply(RequestOptions().placeholder(R.drawable.img_song_cover)).centerCrop()
                .into(binding.imgSongCover)
            binding.root.setOnClickListener {
                action.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FavoriteListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Music>) {
        itemList = newList
        notifyDataSetChanged()
    }
}