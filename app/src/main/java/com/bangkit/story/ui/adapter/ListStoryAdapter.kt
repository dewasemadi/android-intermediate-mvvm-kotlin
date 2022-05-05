package com.bangkit.story.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.databinding.LayoutStoryBinding
import com.bangkit.story.utils.setImage
import com.bangkit.story.utils.withDateFormat

class ListStoryAdapter: RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {
    private var listStory = ArrayList<Story>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(var binding: LayoutStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = LayoutStoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (_, name, _, photoUrl, createdAt) = listStory[position]

        holder.binding.apply {
            storyPhoto.setImage(holder.itemView.context, photoUrl)
            tvName.text = name
            tvDate.text = createdAt?.withDateFormat()
        }
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listStory[holder.adapterPosition]) }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Story)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun getItemCount() = listStory.size

    @SuppressLint("NotifyDataSetChanged")
    fun addList(listStory: ArrayList<Story>){
        this.listStory.addAll(listStory)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear(){
        this.listStory.clear()
        notifyDataSetChanged()
    }
}