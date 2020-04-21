package com.example.finalproject.Fragment

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.BoringLayout
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.*
import com.example.finalproject.Data.BlogContent
import com.example.finalproject.Data.Comment
import com.example.finalproject.Data.Friends
import com.example.finalproject.Data.User
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.bloglayout.*
import kotlinx.android.synthetic.main.bloglayout.view.*
import kotlinx.android.synthetic.main.fragment_blogs.*
import kotlinx.android.synthetic.main.fragment_friends.*
import java.util.*
import kotlin.collections.ArrayList


class BlogsFragment : Fragment() {

    var friendsUid_List: ArrayList<String> = ArrayList()
    var adapter = GroupAdapter<GroupieViewHolder>()
    var mSearchView : SearchView? = null
    var currentId : String = FirebaseAuth.getInstance().uid.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        //(activity as MainScreenActivity).supportActionBar?.title = "Blog"

    }

    override fun onResume() {
        super.onResume()

        fetchBlog()
        fetchFriends()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.finalproject.R.layout.fragment_blogs, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView_Infromation.adapter = adapter
        fetchBlog()
        fetchFriends()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.blogmenu, menu);

        var searchItem = menu.findItem(R.id.blog_search);
        mSearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        mSearchView!!.setSubmitButtonEnabled(true)
        mSearchView!!.setQueryHint("Search blog by key words");

        setMenuListener();

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.blog_add -> {
                addNewBlog()
                true
            }
            R.id.blog_search -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setMenuListener(){

        mSearchView!!.setOnCloseListener(SearchView.OnCloseListener {
            fetchBlog()
            //Toast.makeText(activity, "Close", Toast.LENGTH_SHORT).show()
            false
        })


        mSearchView!!.setOnSearchClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //Toast.makeText(activity, "Open", Toast.LENGTH_SHORT).show()
            }
        })

        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {

                searchBlog(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {

                return false
            }
        })

    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if(hidden) {

        }
        else {
            (activity as MainScreenActivity).supportActionBar?.title = "Blogs"
            fetchBlog()
            fetchFriends()
        }
    }

    fun fetchFriends() {
        var uid: String = FirebaseAuth.getInstance().currentUser!!.uid
        var rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        var uidRef: DatabaseReference = rootRef.child("friends").child(uid)
        uidRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val friends = p0.getValue(Friends::class.java)
                friendsUid_List = ArrayList(friends!!.friends_uid_list)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun addNewBlog() {
        val intent = Intent(this@BlogsFragment.context, AddBlogActivity::class.java)
        startActivity(intent)

    }


    private fun fetchBlog() {
        adapter = GroupAdapter<GroupieViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference().child("blog")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Blog", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("Blog",it.toString())
                    val blog = it.getValue(BlogContent::class.java)

                    if(blog !=null){

                        adapter.add(BlogItem(this@BlogsFragment.context,blog))
                    }
                }

            }
        })
        recyclerView_Infromation.adapter = adapter
    }

    fun searchBlog(key_words : String)  {
        adapter = GroupAdapter<GroupieViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("/blog/")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    val blog = it.getValue(BlogContent :: class.java)
                    if (blog!!.title.contains( key_words) || blog!!.description.contains(key_words)) {
                        adapter.add(BlogItem(this@BlogsFragment.context, blog))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        recyclerView_Infromation.adapter = adapter
    }

    inner class BlogItem(val Context : Context?, val blog: BlogContent) : Item<GroupieViewHolder>() {

        var favoriteNum : Int = blog.favorite

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            //val commentFragment = CommentFragment()
            val blogId = blog.blogId
            val uid = blog.uid
            var isMyFavorite : Boolean = false

            viewHolder.itemView.title.text = blog.title
            viewHolder.itemView.description.text = blog.description
            viewHolder.itemView.favorite_number.text = blog.favorite.toString()
            if(blog.showDate)viewHolder.itemView.date.text=blog.date
            if(blog.showAddress) viewHolder.itemView.address.text=blog.address
            Picasso.get().load(blog.imageUri).into(viewHolder.itemView.image)
            if(blog.favoriteList.contains(FirebaseAuth.getInstance().uid)) {
                viewHolder.itemView.favorite_button.setImageResource(R.drawable.baseline_thumb_up_black_18dp)
                isMyFavorite = true
            }

            Log.d("Blog","Uid" + uid)
            val refer = FirebaseDatabase.getInstance().getReference("/users/$uid")
            refer.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    viewHolder.itemView.username.text = user?.userName
                    Picasso.get().load(user?.profileImage).into(viewHolder.itemView.avatar)
                    Log.d("Blog","Username& Profileiamge"+ user?.userName  + user?.profileImage)
                }

            })

            val ref = FirebaseDatabase.getInstance().getReference("/comment/$blogId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val comment = it.getValue(Comment::class.java)
                        if (comment != null){
                            val ref = FirebaseDatabase.getInstance().getReference("/users/${comment.uid}")
                            ref.addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(p0: DataSnapshot) {
                                    val user = p0.getValue(User::class.java)
                                    if(user != null) {
                                        viewHolder.itemView.simpleComment_username.text = user.userName + " :"
                                    }
                                }
                            })
                            viewHolder.itemView.simpleComment_comment.text = comment.content
                        }
                    }
                }
            })

            viewHolder.itemView.favorite_button.setOnClickListener{
                if (isMyFavorite) {
                    isMyFavorite = !isMyFavorite
                    viewHolder.itemView.favorite_button.setImageResource(R.drawable.baseline_thumb_up_white_18dp)
                    favoriteNum --

                    val myref = FirebaseDatabase.getInstance().getReference("/blog/$blogId").child("favorite")
                    myref.setValue(favoriteNum)
                    val myref2 = FirebaseDatabase.getInstance().getReference("/blog/$blogId").child("favoriteList")
                    val currentList = ArrayList(blog.favoriteList)
                    currentList.remove(currentId)
                    myref2.setValue(currentList)
                    viewHolder.itemView.favorite_number.text = favoriteNum.toString()
                    refresh()
                }
                else {
                    isMyFavorite = !isMyFavorite
                    viewHolder.itemView.favorite_button.setImageResource(R.drawable.baseline_thumb_up_black_18dp)
                    favoriteNum ++

                    val myref = FirebaseDatabase.getInstance().getReference("/blog/$blogId").child("favorite")
                    myref.setValue(favoriteNum)
                    val myref2 = FirebaseDatabase.getInstance().getReference("/blog/$blogId").child("favoriteList")
                    val currentList = ArrayList(blog.favoriteList)
                    currentList.add(currentId)
                    myref2.setValue(currentList)
                    viewHolder.itemView.favorite_number.text = favoriteNum.toString()
                    refresh()
                }
            }

            viewHolder.itemView.comment_button.setOnClickListener{
                val comment = viewHolder.itemView.comment.text.toString()
                val commentUid = FirebaseAuth.getInstance().uid
                val id = UUID.randomUUID().toString()
                val ref = FirebaseDatabase.getInstance().getReference("/comment/$blogId/$id")
                val commentContent = Comment(id,commentUid,blogId,comment)
                ref.setValue(commentContent).addOnSuccessListener {
                    Log.d("AC","add comment success")
                    Toast.makeText(
                        activity,
                        "Comment successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                viewHolder.itemView.comment.text.clear()
                refresh()

                val ref2 = FirebaseDatabase.getInstance().getReference("/comment/$blogId")
                ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach {
                            val comment = it.getValue(Comment::class.java)
                            if (comment != null){
                                val ref3 = FirebaseDatabase.getInstance().getReference("/users/${comment.uid}")
                                ref3.addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {
                                    }
                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user = p0.getValue(User::class.java)
                                        if(user != null) {
                                            viewHolder.itemView.simpleComment_username.text = user.userName + " :"
                                        }
                                    }
                                })
                                viewHolder.itemView.simpleComment_comment.text = comment.content
                            }
                        }
                    }
                })
            }

            viewHolder.itemView.addFriend_button.setOnClickListener{
                // Add friends funciton

                var friendId: String? = blog.uid
                Log.d("chosen friend id", blog.uid)
                Log.d("friends list", friendsUid_List.toString())
                var Uid: String? = FirebaseAuth.getInstance().uid
                if (friendsUid_List.contains(friendId)) {
                    Toast.makeText(activity, "This user already exists in your friend list.", Toast.LENGTH_LONG).show()
                } else {

                    friendsUid_List.add(friendId!!)
                    var database: DatabaseReference = Firebase.database.reference
                    database.child("friends").child(Uid.toString())
                        .child("friends_uid_list").setValue(friendsUid_List)

                    Toast.makeText(
                        activity,
                        "Added new friend successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

            viewHolder.itemView.allCommentsButton.setOnClickListener {
                var intent = Intent(activity, AllCommentsActivity::class.java)
                intent.putExtra("blog_id", blogId)
                activity?.startActivity(intent)
            }
        }


        override fun getLayout(): Int {
            return R.layout.bloglayout
        }

        private fun refresh(){
            val adapter = GroupAdapter<GroupieViewHolder>()
            adapter.notifyDataSetChanged()
        }

    }
}