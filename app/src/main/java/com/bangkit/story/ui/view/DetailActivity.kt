package com.bangkit.story.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.databinding.ActivityDetailBinding
import com.bangkit.story.utils.setImage
import com.bangkit.story.utils.withDateFormat

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var rotationValue = 0f
    private var tap = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        initDetail()
        onRotatePressed()
    }

    private fun initViewBinding() {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initDetail() {
        val story = intent.getParcelableExtra<Story>(EXTRA_STORY) as Story
        binding.apply {
            detailStoryPhoto.setImage(this@DetailActivity, story.photoUrl)
            tvName.text = story.name
            tvDate.text = story.createdAt?.withDateFormat()
            tvDescription.text = story.description
        }
        story.name?.let { initToolbar(it) }
    }

    private fun onRotatePressed() {
        binding.apply {
            rotateButton.setOnClickListener {
                if (tap == 4) {
                    tap = 1
                    rotationValue = 0f
                } else {
                    tap++
                    rotationValue += 90f
                }
                detailStoryPhoto.rotation = rotationValue
            }
        }
    }

    private fun initToolbar(name: String) {
        supportActionBar?.apply {
            this.title = name
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}