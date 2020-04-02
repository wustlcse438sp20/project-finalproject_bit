package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.R.menu
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class BlogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)


//        setSupportActionBar(findViewById(R.id.toolbar))
//        // Now get the support action bar
//        val actionBar = supportActionBar
//
//        // Set toolbar title/app title
//        actionBar!!.title = "Hello APP"
//        //home navigation
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.blogmenu, menu)
        return true

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.blog_add -> {
                addNewBlog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addNewBlog(){
        val intent = Intent(this,AddBlogActivity::class.java)
        startActivity(intent)
    }
}
