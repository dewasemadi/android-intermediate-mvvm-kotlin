package com.bangkit.story.ui.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bangkit.story.R
import com.bangkit.story.data.local.preferences.SessionManager
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.databinding.ActivityMainBinding
import com.bangkit.story.ui.adapter.ListStoryAdapter
import com.bangkit.story.ui.adapter.LoadingStateAdapter
import com.bangkit.story.ui.viewmodel.MainViewModel
import com.bangkit.story.ui.viewmodel.ViewModelFactory
import com.bangkit.story.utils.DELAY

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
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

    private fun initSessionManager() {
        sessionManager = SessionManager(this)
    }

    private fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initSwipeRefresh() {
        binding.swipeRefresh.apply {
            setOnRefreshListener(this@MainActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.maps -> {
                val toMaps = Intent(this, MapsActivity::class.java)
                startActivity(toMaps)
                true
            }
            R.id.settings -> {
                val toSettings = Intent(this, SettingsActivity::class.java)
                startActivity(toSettings)
                true
            }
            R.id.action_sort -> {
                showSortingPopupMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSortingPopupMenu() {
        val view = findViewById<View>(R.id.action_sort) ?: return

        PopupMenu(this, view).run {
            menuInflater.inflate(R.menu.sorting_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_with_location -> sessionManager.setIsWithLocation(true)
                    R.id.action_without_location -> sessionManager.setIsWithLocation(false)
                }
                listStoryAdapter.refresh()
                true
            }
            show()
        }
    }

    private fun initRecycleView() {
        listStoryAdapter = ListStoryAdapter()

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listStoryAdapter.retry()
                }
            )
        }

        mainViewModel.getAllStories(this).observe(this) {
            listStoryAdapter.submitData(lifecycle, it)
        }

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
        listStoryAdapter.refresh()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.swipeRefresh.isRefreshing = false
        }, DELAY)
    }
}
