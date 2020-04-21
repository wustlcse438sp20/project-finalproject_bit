package com.example.finalproject.Fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.finalproject.Data.*
import com.example.finalproject.MainScreenActivity

import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.bloglayout.view.*
import kotlinx.android.synthetic.main.fragment_show_video.*
import kotlinx.android.synthetic.main.fragment_video.view.*
import kotlinx.android.synthetic.main.videolayout.*
import kotlinx.android.synthetic.main.videolayout.view.*
import kotlinx.android.synthetic.main.videolayout.view.addFriend_button
import kotlinx.android.synthetic.main.videolayout.view.avatar
import kotlinx.android.synthetic.main.videolayout.view.date
import kotlinx.android.synthetic.main.videolayout.view.favorite_button
import kotlinx.android.synthetic.main.videolayout.view.favorite_number
import kotlinx.android.synthetic.main.videolayout.view.username
import java.util.ArrayList

class ShowVideoFragment : Fragment() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    val currentId : String = FirebaseAuth.getInstance().uid.toString()
    private var friendsUid_List: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView_Video_Infromation.adapter = adapter
        fetchVideo()
        fetchFriends()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.blogmenu, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.blog_add -> {
                addNewVideo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if(hidden) {

        }
        else {
            (activity as MainScreenActivity).supportActionBar?.title = "Video"
            fetchVideo()
            fetchFriends()
        }
    }

    private fun addNewVideo(){
        val fragment = VideoFragment()
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        var uid: String = FirebaseAuth.getInstance().currentUser!!.uid
        bundle.putString("uid",uid)
        fragment.arguments = bundle
        transaction.replace(R.id.showVideo,fragment)
        transaction.commit()
    }
    private fun fetchVideo(){

        val ref = FirebaseDatabase.getInstance().getReference().child("video")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Blog", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("Blog",it.toString())
                    val video = it.getValue(VideoContent::class.java)

                    if(video !=null){

                        adapter.add(VideoItem(this@ShowVideoFragment.context,video))
                    }
                }

            }
        })

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

    inner class VideoItem(val Context : Context?, val Video: VideoContent) : Item<GroupieViewHolder>() {

        var favoriteNum : Int = Video.favorite

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            val uid = Video.uid
            val videoId = Video.videoId
            var isMyFavorite : Boolean = false
            viewHolder.itemView.video_description.text = Video.description
            viewHolder.itemView.date.text = Video.date
            viewHolder.itemView.favorite_number.text = Video.favorite.toString()

            if(Video.favoriteList.contains(FirebaseAuth.getInstance().uid)) {
                viewHolder.itemView.favorite_button.setImageResource(R.drawable.baseline_thumb_up_black_18dp)
                isMyFavorite = true
            }

            viewHolder.itemView.videoView.setVideoURI(Video.videoUri.toUri())
            val mediaController = MediaController(this@ShowVideoFragment.context)
            mediaController.setAnchorView(viewHolder.itemView.videoView)
            viewHolder.itemView.videoView.setMediaController(mediaController)
            viewHolder.itemView.videoView.start()
            // fetch user avatar and username
            val refer = FirebaseDatabase.getInstance().getReference("/users/$uid")
            refer.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    viewHolder.itemView.username.text = user?.userName
                    Picasso.get().load(user?.profileImage).into(viewHolder.itemView.avatar)
                    Log.d("Blog", "Username& Profileiamge" + user?.userName + user?.profileImage)
                }

            })

            viewHolder.itemView.favorite_button.setOnClickListener {
                if (isMyFavorite) {
                    isMyFavorite = !isMyFavorite
                    viewHolder.itemView.favorite_button.setImageResource(R.drawable.baseline_thumb_up_white_18dp)
                    favoriteNum --

                    val myref = FirebaseDatabase.getInstance().getReference("/video/$videoId").child("favorite")
                    myref.setValue(favoriteNum)
                    val myref2 = FirebaseDatabase.getInstance().getReference("/blog/$videoId").child("favoriteList")
                    val currentList = ArrayList(Video.favoriteList)
                    currentList.remove(currentId)
                    myref2.setValue(currentList)
                    viewHolder.itemView.favorite_number.text = favoriteNum.toString()
                    refresh()
                }
                else {
                    isMyFavorite = !isMyFavorite
                    viewHolder.itemView.favorite_button.setImageResource(R.drawable.baseline_thumb_up_black_18dp)
                    favoriteNum ++

                    val myref = FirebaseDatabase.getInstance().getReference("/video/$videoId").child("favorite")
                    myref.setValue(favoriteNum)
                    val myref2 = FirebaseDatabase.getInstance().getReference("/blog/$videoId").child("favoriteList")
                    val currentList = ArrayList(Video.favoriteList)
                    currentList.add(currentId)
                    myref2.setValue(currentList)
                    viewHolder.itemView.favorite_number.text = favoriteNum.toString()
                    refresh()
                }
            }

            viewHolder.itemView.addFriend_button.setOnClickListener{
                // Add friends funciton

                var friendId: String? = Video.uid
                Log.d("chosen friend id", Video.uid)
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
        }


        override fun getLayout(): Int {
            return R.layout.videolayout
        }

        private fun refresh() {
            val adapter = GroupAdapter<GroupieViewHolder>()
            adapter.notifyDataSetChanged()
        }
    }
}

