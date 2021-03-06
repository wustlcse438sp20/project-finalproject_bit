package com.example.finalproject

import android.app.Fragment
import android.app.FragmentTransaction
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.finalproject.Fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_mainscreen.*


class MainScreenActivity : AppCompatActivity() {

    private var blogsFragment: BlogsFragment? = null
    private var ShowVideoFragment: ShowVideoFragment? = null
    private var friendsFragment: FriendsFragment? = null
    private var meFragment: MeFragment? = null
    private var currentIndex = 0

    private val fragmentManager by lazy {
        supportFragmentManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainscreen)

        var intent = intent
        var fromWhere = intent.getStringExtra("FromWhere")

        var navigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        if (fromWhere == "VideoFragment") {
            navigation.selectedItemId = R.id.bottom_navigation_video
        } else {
            navigation.selectedItemId = R.id.bottom_navigation_blogs
        }
    }


    override fun onAttachFragment(fragment: androidx.fragment.app.Fragment) {
        super.onAttachFragment(fragment)
        when(fragment) {
            is BlogsFragment -> blogsFragment ?: let { blogsFragment = fragment }
            is ShowVideoFragment -> ShowVideoFragment ?: let { ShowVideoFragment = fragment}
            is FriendsFragment -> friendsFragment ?: let { friendsFragment = fragment}
            is MeFragment -> meFragment ?: let { meFragment = fragment }
        }
    }

    private fun setFragment(index: Int) {
        fragmentManager.beginTransaction().apply {
            blogsFragment ?: let {
                BlogsFragment().let {
                    blogsFragment = it
                    add(R.id.container, it)
                }
            }
            ShowVideoFragment ?: let {
                ShowVideoFragment().let {
                    ShowVideoFragment = it
                    add(R.id.container, it)
                }
            }
            friendsFragment ?: let {
                FriendsFragment().let {
                    friendsFragment = it
                    add(R.id.container, it)
                }
            }
            meFragment ?: let {
                MeFragment().let {
                    meFragment = it
                    add(R.id.container, it)
                }
            }
            hideFragment(this)
            when (index) {
                R.id.bottom_navigation_blogs -> {
                    blogsFragment?.let {
                        this.show(it)
                    }
                }
                R.id.bottom_navigation_video -> {
                    ShowVideoFragment?.let {
                        this.show(it)
                    }
                }
                R.id.bottom_navigation_friends -> {
                    friendsFragment?.let {
                        this.show(it)
                    }
                }
                R.id.bottom_navigation_me -> {
                    meFragment?.let {
                        this.show(it)
                    }
                }
            }
        }.commit()
    }

    /**
     * 隐藏所有fragment
     */
    private fun hideFragment(transaction: androidx.fragment.app.FragmentTransaction) {

        blogsFragment?.let {
            transaction.hide(it)
        }
        ShowVideoFragment?.let {
            transaction.hide(it)
        }
        friendsFragment?.let {
            transaction.hide(it)
        }
        meFragment?.let {
            transaction.hide(it)
        }
    }

    /**
     * NavigationItemSelect监听
     */
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            setFragment(item.itemId)
            return@OnNavigationItemSelectedListener when (item.itemId) {
                R.id.bottom_navigation_blogs -> {
                    currentIndex = R.id.bottom_navigation_blogs
                    true
                }
                R.id.bottom_navigation_video -> {
                    currentIndex = R.id.bottom_navigation_video
                    true
                }
                R.id.bottom_navigation_friends -> {
                    currentIndex = R.id.bottom_navigation_friends
                    true
                }
                R.id.bottom_navigation_me -> {
                    currentIndex = R.id.bottom_navigation_me
                    true
                }
                else -> {
                    false
                }
            }
        }

}