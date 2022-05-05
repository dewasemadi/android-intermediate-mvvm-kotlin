package com.bangkit.story.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bangkit.story.R
import com.bangkit.story.data.local.SessionManager
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.databinding.ActivityMainBinding
import com.bangkit.story.ui.adapter.ListStoryAdapter
import com.bangkit.story.ui.viewmodel.MainViewModel
import com.bangkit.story.ui.viewmodel.ViewModelFactory
import com.bangkit.story.utils.State

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private var currentPage = 1
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var listStoryAdapter: ListStoryAdapter
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSessionManager()
        initViewBinding()
        initRecycleView()
        initSwipeRefresh()
        onAddNewStoryPressed()
    }

    private fun initSessionManager(){
        sessionManager = SessionManager(this)
    }

    private fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val toSettings = Intent(this, SettingsActivity::class.java)
                startActivity(toSettings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initObserver() {
        mainViewModel.getAllStories(currentPage, 20).observe(this) { response ->
            when (response) {
                is State.Loading -> {
                    if (currentPage == 1) {
                        binding.apply {
                            storySkeleton.visibility = View.VISIBLE
                            storyLoading.visibility = View.GONE
                            rvStories.visibility = View.GONE
                        }
                    } else {
                        binding.apply {
                            storySkeleton.visibility = View.GONE
                            storyLoading.visibility = View.VISIBLE
                            rvStories.visibility = View.VISIBLE
                        }
                    }
                }
                is State.Success -> {
                    binding.apply {
                        storySkeleton.visibility = View.GONE
                        storyLoading.visibility = View.GONE
                        rvStories.visibility = View.VISIBLE
                        swipeRefresh.isRefreshing = false
                    }
                    response.data.listStory?.let {
                        listStoryAdapter.addList(it)
                        if (currentPage == 1) {
                            saveImages(it)
                        }
                    }
                }
                is State.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    val size = listStoryAdapter.itemCount
                    if (size == 0) {
                        binding.apply {
                            storySkeleton.visibility = View.VISIBLE
                            storyLoading.visibility = View.GONE
                            rvStories.visibility = View.GONE
                        }
                    } else {
                        binding.apply {
                            storySkeleton.visibility = View.GONE
                            storyLoading.visibility = View.GONE
                            rvStories.visibility = View.VISIBLE
                        }
                    }
                    Toast.makeText(this, response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // this function will save images to shared preferences that will consume on widget
    private fun saveImages(data: ArrayList<Story>) {
        try {
            sessionManager.apply {
                for (i in 0..5)
                    saveImage("image-$i", data[i].photoUrl.toString())
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    private fun initRecycleView() {
        layoutManager = LinearLayoutManager(this)
        listStoryAdapter = ListStoryAdapter()

        binding.rvStories.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listStoryAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)) {
                        currentPage++
                        initObserver()
                    }
                }
            })
        }

        initObserver()

        listStoryAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                showDetailStory(data)
            }
        })
    }

    private fun showDetailStory(data: Story) {
        val toDetail = Intent(this, DetailActivity::class.java)
        toDetail.putExtra(DetailActivity.EXTRA_STORY, data)
        startActivity(toDetail)
    }

    private fun onAddNewStoryPressed() {
        binding.addNewStory.setOnClickListener {
            val toAddStory = Intent(this, NewStoryActivity::class.java)
            startActivity(toAddStory)
        }
    }

    override fun onRefresh() {
        listStoryAdapter.clear()
        currentPage = 1
        initObserver()
    }
}
