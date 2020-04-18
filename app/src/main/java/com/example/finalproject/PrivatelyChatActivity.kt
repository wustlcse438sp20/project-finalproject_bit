package com.example.finalproject

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.Data.ChatMessage
import com.example.finalproject.Data.User
import com.example.finalproject.Fragment.FriendsFragment
import com.example.finalproject.Fragment.FriendsListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

import kotlinx.android.synthetic.main.activity_privatelychat.*
import kotlinx.android.synthetic.main.message_from_row.view.*
import kotlinx.android.synthetic.main.message_to_row.view.*

class PrivatelyChatActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    val uid = FirebaseAuth.getInstance().uid
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privatelychat)

        user = intent.getParcelableExtra<User>(FriendsListFragment.USER_KEY)

        supportActionBar?.title = user.userName

        recyclerview_messages.adapter = adapter
        listenForMessages(user)

        message_send_button.setOnClickListener {
            SendMessage(user)
        }
    }


    private fun listenForMessages(user : User){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage :: class.java)
                Log.d("chatMessage", chatMessage!!.text)

                if(chatMessage.fromId == FirebaseAuth.getInstance().uid && chatMessage.toId == user.uid) {
                    val currentUser = FriendsListFragment.currentUser
                    adapter.add(ChatToItem(chatMessage.text, currentUser!!))
                }
                if(chatMessage.fromId == user.uid && chatMessage.toId == FirebaseAuth.getInstance().uid){
                    adapter.add(ChatFromItem(chatMessage.text, user))
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }


    private fun SendMessage( user : User) {
        val text = message_input.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId!!, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("Saved our chat message ", reference.key)

            }

        latestMessageRef.setValue(chatMessage)
        latestMessageToRef.setValue(chatMessage)

    }
}


class ChatFromItem(val text : String, val user : User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_from_text.text = text

        var url = user.profileImage
        var targetImageView = viewHolder.itemView.message_from_image
        Picasso.get().load(url).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.message_from_row
    }
}

class ChatToItem(val text : String, val user : User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_to_text.text = text

        var url = user.profileImage
        var targetImageView = viewHolder.itemView.message_to_image
        Picasso.get().load(url).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.message_to_row
    }
}

