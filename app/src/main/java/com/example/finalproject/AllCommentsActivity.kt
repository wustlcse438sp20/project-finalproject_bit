package com.example.finalproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.Data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_allcomments.*
import kotlinx.android.synthetic.main.commentlayout.view.*

class AllCommentsActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allcomments)
        supportActionBar?.title = "Comments"

        var intent = intent
        var blogId = intent.getStringExtra("blog_id")

        fetchComment(blogId)

    }

    private fun fetchComment(blogId : String){

        val ref = FirebaseDatabase.getInstance().getReference("/comment/$blogId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val comment = it.getValue(com.example.finalproject.Data.Comment::class.java)
                    if (comment != null){
                        adapter.add(CommentItem(comment))
                    }
                }
            }


        })

        allCommentsRecycleview.adapter = adapter
    }

    inner class CommentItem(var comment: com.example.finalproject.Data.Comment) : Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            val User_id = comment.uid

            val ref = FirebaseDatabase.getInstance().getReference("/users/$User_id")
            ref.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    if(user != null) {
                        viewHolder.itemView.username1.text = user.userName + " :"
                        Picasso.get().load(user.profileImage).into(viewHolder.itemView.avatar1)
//                    Log.d("CF","Username& Profileiamge"+ user?.userName  + user?.profileImage)
                    }
                }

            })
            viewHolder.itemView.comment1.text = comment.content
        }

        override fun getLayout(): Int {
            return R.layout.commentlayout
        }
    }
}