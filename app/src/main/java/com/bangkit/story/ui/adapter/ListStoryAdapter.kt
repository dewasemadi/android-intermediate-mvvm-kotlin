package com.bangkit.story.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.databinding.LayoutStoryBinding
import com.bangkit.story.utils.setImage
import com.bangkit.story.utils.withDateFormat

class ListStoryAdapter : PagingDataAdapter<Story, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = LayoutStoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(data) }
        }
    }

    class ListViewHolder(private val binding: LayoutStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            binding.apply {
                storyPhoto.setImage(itemView.context, data.photoUrl)
                tvName.text = data.name
                tvDate.text = data.createdAt?.withDateFormat()
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Story)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}