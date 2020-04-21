package com.example.finalproject.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Data.ChatMessage
import com.example.finalproject.Data.LatestMessage
import com.example.finalproject.Data.User
import com.example.finalproject.PrivatelyChatActivity
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.latestmessage_list_row.view.*



class MessageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenForLatestMessages()
    }

    override fun onResume() {
        super.onResume()

        listenForLatestMessages()
    }

    fun listenForLatestMessages() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        val fromId = FirebaseAuth.getInstance().uid
        Log.d("uid", fromId)
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val latestMessage = p0.getValue(LatestMessage :: class.java)
                Log.d("from id", latestMessage!!.fromId)
                Log.d("to id", latestMessage!!.toId)
                Log.d("is Read", latestMessage!!.readOrNot.toString())
                val userId : String
                    if (fromId != latestMessage!!.fromId){
                        userId = latestMessage.fromId
                    }
                    else{
                        userId = latestMessage.toId
                    }
                val userInforRef = FirebaseDatabase.getInstance().getReference("/users/$userId")
                userInforRef.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(p1: DataSnapshot) {
                        var user = p1.getValue(User::class.java)
                        adapter.add(LatestMessageItem(user!!, latestMessage))
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

//        var latestMessages_Recyclerview =
//            view.findViewById<RecyclerView>(R.id.latestMessages_recyclerview)
        latestMessages_recyclerview.adapter = adapter

    }


    inner class LatestMessageItem (val user: User, val latestMessage: LatestMessage) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.latestMessage_item_name.text = user.userName
            viewHolder.itemView.latestMessage_item_text.text = latestMessage.text

            var user_photo: ImageView = viewHolder.itemView.latestMessage_item_photo
            var url = user.profileImage
            Picasso.get()
                .load(url)
                .into(user_photo)

            Log.d("username", user.userName)
            Log.d("isRead", latestMessage.readOrNot.toString())

            if(!latestMessage.readOrNot) {
                Log.d("red dot", user.userName)
                viewHolder.itemView.red_dot.visibility = View.VISIBLE
            }

            viewHolder.itemView.setOnClickListener {

                viewHolder.itemView.red_dot.visibility = View.INVISIBLE
                val myref2 = FirebaseDatabase.getInstance().getReference("/latest-messages/${latestMessage.toId}/${latestMessage.fromId}").child("readOrNot")
                myref2.setValue(true)

                var intent = Intent(activity, PrivatelyChatActivity::class.java)
                intent.putExtra("USER_KEY", user)
                activity?.startActivity(intent)
            }
        }

        override fun getLayout(): Int {
            return R.layout.latestmessage_list_row
        }
    }
}