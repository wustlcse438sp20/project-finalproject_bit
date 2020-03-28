package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalproject.Fragment.LoginFragment
import com.example.finalproject.Fragment.RegisterFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(LoginFragment(), "Login");
        adapter.addFragment(RegisterFragment(), "Register")
        viewPager?.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
}
