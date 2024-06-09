package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultValue
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity
import com.dicoding.picodiploma.loginwithanimation.view.tambah.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    private val storyAdapter = StoriesAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.txt_logout))
                        setMessage(getString(R.string.txt_logout_msg))
                        setPositiveButton(getString(R.string.txt_yes)) { _, _ ->
                            viewModel.logout()
                            showToast(getString(R.string.txt_logout_success))
                        }
                        setNegativeButton(getString(R.string.txt_no)) { _, _ ->
                        }
                        create()
                        show()
                    }
                    true
                }

                else -> false
            }
        }

        binding.addStoryButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddStoryActivity::class.java
                )
            )
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStories().observe(this) { result ->
                    when (result) {
                        is ResultValue.Loading -> {
                            showLoading(true)
                        }

                        is ResultValue.Success -> {
                            showViewModel(result.data.listStory)
                            showLoading(false)
                        }

                        is ResultValue.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }

                        else -> showToast(getString(R.string.txt_no_item))
                    }
                }
            }
        }
        showRecycleView()
        setupView()
    }

    private fun showRecycleView() {
        val mLayoutManager = LinearLayoutManager(this)
        binding.rvStories.apply {
            layoutManager = mLayoutManager
            setHasFixedSize(true)
            adapter = storyAdapter
        }

        storyAdapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                showSelectedUser(data)
            }
        })
    }

    private fun showViewModel(storiesItem: List<ListStoryItem>) {
        if (storiesItem.isNotEmpty()) {
            binding.rvStories.visibility = View.VISIBLE
            storyAdapter.submitList(storiesItem)
        } else {
            binding.rvStories.visibility = View.INVISIBLE
        }
    }

    private fun showSelectedUser(stories: ListStoryItem) {
        val intentToDetail = Intent(this@MainActivity, DetailActivity::class.java)
        intentToDetail.putExtra("STORY_ID", stories.id)
        startActivity(intentToDetail)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar3.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}