package com.example.finalproject.Fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalproject.Data.BlogContent
import com.example.finalproject.Data.Comment
import com.example.finalproject.Data.User

import com.example.finalproject.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.bloglayout.view.*
import kotlinx.android.synthetic.main.commentlayout.view.*
import kotlinx.android.synthetic.main.fragment_comment.*


class CommentFragment : Fragment() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentRecycleview.adapter = adapter

        fetchComment()
    }


    private fun fetchComment(){
        val blogId = arguments?.getString("blogId")
        Log.d("CID","blogId" + blogId)
//        val blogId ="1968edb1-d9e4-4d5a-a22b-c3f6fa54a496"
        val ref = FirebaseDatabase.getInstance().getReference("/comment/$blogId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val comment = it.getValue(Comment::class.java)
                    Log.d("CG","comment" + comment)
                    if (comment != null){
                        adapter.add(CommentItem(comment))
//                        Log.d("CF","comment" + comment)
                    }
                }
            }


        })
    }
    class CommentItem(val blog: Comment) : Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            val uid = blog.uid

            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            ref.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    viewHolder.itemView.username1.text = user?.userName
                    Picasso.get().load(user?.profileImage).into(viewHolder.itemView.avatar1)
//                    Log.d("CF","Username& Profileiamge"+ user?.userName  + user?.profileImage)
                }

            })
            viewHolder.itemView.comment1.text =":" +   blog.content
        }

        override fun getLayout(): Int {
            return R.layout.commentlayout
        }
    }

}