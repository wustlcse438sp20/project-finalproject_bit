//package com.example.finalproject
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import androidx.core.content.ContextCompat.startActivity
//import kotlinx.android.synthetic.main.activity_main.*
//import android.R.menu
//import android.app.PendingIntent.getActivity
//import android.app.SearchManager
//import android.content.ClipDescription
//import android.content.Context
//import androidx.core.app.ComponentActivity.ExtraData
//import androidx.core.content.ContextCompat.getSystemService
//import android.icu.lang.UCharacter.GraphemeClusterBreak.T
//import android.util.Log
//import android.widget.ArrayAdapter
//import androidx.core.net.toUri
//import com.example.finalproject.Data.BlogContent
//import com.example.finalproject.Data.Comment
//import com.example.finalproject.Data.User
//import com.example.finalproject.Fragment.CommentFragment
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.squareup.picasso.Picasso
//import com.xwray.groupie.GroupAdapter
//
//import com.xwray.groupie.Item
//import kotlinx.android.synthetic.main.activity_blog.*
//import kotlinx.android.synthetic.main.bloglayout.*
//import kotlinx.android.synthetic.main.bloglayout.view.*
//import androidx.core.content.ContextCompat.getSystemService
//import android.icu.lang.UCharacter.GraphemeClusterBreak.T
//import android.widget.SearchView
//import android.widget.Toast
//import com.google.firebase.auth.FirebaseAuth
//import com.xwray.groupie.GroupieViewHolder
//import java.util.*
//
//
//class BlogActivity : AppCompatActivity() {
//     val adapter = GroupAdapter<GroupieViewHolder>()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_blog)
//        supportActionBar?.title = "Blog"
//        recyclerView_Infromation.adapter = adapter
//        fetchBlog()
//
//
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.blogmenu, menu)
//        val manager = getSystemService(Context.SEARCH_SERVICE)as SearchManager
//        val searchItem = menu?.findItem(R.id.blog_search)
//        val searchView = searchItem?.actionView as SearchView
//
//        searchView.setOnQueryTextListener((object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//               searchView.clearFocus()
//               searchView.setQuery("",false)
//                searchItem.collapseActionView()
//                Toast.makeText(this@BlogActivity,"Looking for $query",Toast.LENGTH_SHORT).show()
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//
//        }))
//
//        return true
//
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle item selection
//        return when (item.itemId) {
//            R.id.blog_add -> {
//                addNewBlog()
//                true
//            }
////            R.id.blog_search -> {
////                searchBlog()
////                true
////            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    private fun addNewBlog() {
//        val intent = Intent(this, AddBlogActivity::class.java)
//        startActivity(intent)
//    }
//
//
//    private fun fetchBlog() {
//        val ref = FirebaseDatabase.getInstance().getReference().child("blog")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                Log.d("Blog", p0.message)
//        }
//            val list = arrayListOf<BlogContent>()
//            override fun onDataChange(p0: DataSnapshot) {
//                p0.children.forEach {
//                    Log.d("Blog",it.toString())
//                    val blog = it.getValue(BlogContent::class.java)
//
//                    if(blog !=null){
//
//                        adapter.add(BlogItem(this@BlogActivity,blog))
//                    }
//                }
//
//            }
//        })
//
//
//
//    }
//
//
//    class BlogItem(val Context : Context, val blog: BlogContent) : Item<GroupieViewHolder>() {
//
//        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            val commentFragment = CommentFragment()
//            val blogId = blog.blogId
//            val uid = blog.uid
//            val bundle = Bundle()
//            bundle.putString("blogId", blogId)
//            commentFragment.arguments = bundle
//            val fragmentTransaction= (Context as AppCompatActivity).supportFragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.comment_Fragment, commentFragment)
//            fragmentTransaction.commit()
//
//
//            viewHolder.itemView.title.text = blog.title
//            viewHolder.itemView.description.text = blog.description
//            if(blog.showDate)viewHolder.itemView.date.text=blog.date
//            if(blog.showAddress) viewHolder.itemView.address.text=blog.address
//            Picasso.get().load(blog.imageUri).into(viewHolder.itemView.image)
//
//            Log.d("Blog","Uid" + uid)
//            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//            ref.addListenerForSingleValueEvent(object :ValueEventListener{
//                override fun onCancelled(p0: DatabaseError) {
//                }
//
//                override fun onDataChange(p0: DataSnapshot) {
//                    val user = p0.getValue(User::class.java)
//                    viewHolder.itemView.username.text = user?.userName
//                    Picasso.get().load(user?.profileImage).into(viewHolder.itemView.avatar)
//                    Log.d("Blog","Username& Profileiamge"+ user?.userName  + user?.profileImage)
//                }
//
//            })
//
//            viewHolder.itemView.favorite_button.setOnClickListener{
//                viewHolder.itemView.favorite_button.setImageResource(R.drawable.baseline_thumb_up_black_18dp)
//                var favoriteNumber = blog.favorite
//                favoriteNumber ++
//
//                val myref = FirebaseDatabase.getInstance().getReference("/blog/$blogId").child("favorite")
//                myref.setValue(favoriteNumber)
//                viewHolder.itemView.favorite_number.text = favoriteNumber.toString()
//                refresh()
//
//            }
//            viewHolder.itemView.comment_button.setOnClickListener({
//                val comment = viewHolder.itemView.comment.text.toString()
//                val commentUid = FirebaseAuth.getInstance().uid
//                val ref = FirebaseDatabase.getInstance().getReference("/comment/$blogId/$commentUid")
//                val commentContent = Comment(commentUid,blogId,comment)
//                ref.setValue(commentContent).addOnSuccessListener {
//                    Log.d("AC","add comment success")
//                }
//                viewHolder.itemView.comment.text.clear()
//                refresh()
//            })
//
//        }
//
//        override fun getLayout(): Int {
//            return R.layout.bloglayout
//        }
//        private fun refresh(){
//            val adapter = GroupAdapter<GroupieViewHolder>()
//            adapter.notifyDataSetChanged()
//        }
//
//    }
//
//
//
//
//}
