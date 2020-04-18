package com.example.finalproject.Fragment

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.finalproject.MainScreenActivity
import com.example.finalproject.R
import com.example.finalproject.ViewPagerAdapter2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter2(this@FriendsFragment.fragmentManager!!)
        adapter.addFragment(FriendsListFragment(), "FriendsList")
        adapter.addFragment(MessageFragment(), "Message")
        viewPager2?.adapter = adapter
        tabs2.setupWithViewPager(viewPager2)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if(hidden) {

        }
        else {
            FriendsListFragment().fetchCurrentUser()
            FriendsListFragment().fetchUsers()
            FriendsListFragment().fetchFriends(view!!)
        }
    }
}