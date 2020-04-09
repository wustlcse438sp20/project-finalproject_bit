package com.example.finalproject.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Data.Friends
import com.example.finalproject.Data.User
import com.example.finalproject.PrivatelyChatActivity
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.friends_list_row.view.*

class FriendsFragment : Fragment() {

    var uid: String? = FirebaseAuth.getInstance().uid
    var database: DatabaseReference = Firebase.database.reference

    var userUid_List: ArrayList<String?> = ArrayList()
    var friendsUid_List: ArrayList<String> = ArrayList()

    lateinit var friend_search_Button: Button
    lateinit var friend_addBySearch_Button: Button

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

        fetchCurrentUser()
        fetchUsers()
        fetchFriends(view)

        friend_search_Button = view.findViewById(R.id.friend_search_button)
        friend_search_Button.setOnClickListener {
            var searchFriend_input: String = friend_search_input.text.toString()
            if (!searchFriend_input.isEmpty()) {

                //......
            }
        }

        friend_addBySearch_Button = view.findViewById(R.id.friend_addBySearch_button)
        friend_addBySearch_Button.setOnClickListener {
            var addFriendBySearch_input: String = friend_addBySearch_input.text.toString()
            if (addFriendBySearch_input != "") {
                var friendId: String = addFriendBySearch_input

                if (friendsUid_List.contains(friendId)) {
                    Toast.makeText(
                        activity,
                        "This user already exists in your friend list.",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (!userUid_List.contains(friendId)) {
                    Toast.makeText(activity, "This user id is invalid.", Toast.LENGTH_LONG).show()
                } else {
                    getFriendsFromFirebase(object : FriendsListCallback {
                        override fun onCallback(friends: Friends) {

                            friendsUid_List = ArrayList(friends.friends_uid_list)
                            friendsUid_List.add(friendId)
                            database.child("friends").child(uid.toString())
                                .child("friends_uid_list").setValue(friendsUid_List)

                            fetchFriends(view)

                            Toast.makeText(
                                activity,
                                "Added new friend successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
                }
            }
        }
    }

    companion object {
        val USER_KEY = "USER_KEY"
        var currentUser : User? = null
    }

    fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users/")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    userUid_List.add(user!!.uid)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User :: class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    fun fetchFriends(view: View) {
        var uid: String = FirebaseAuth.getInstance().currentUser!!.uid
        var rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        var uidRef: DatabaseReference = rootRef.child("friends").child(uid)
        uidRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                var adapter = GroupAdapter<GroupieViewHolder>()
                val friends = p0.getValue(Friends::class.java)
                val friends_Uids = friends!!.friends_uid_list
                Log.d("friends uids", friends_Uids.toString())

                for (i in 0 until friends_Uids.size) {

                    var uidRef2: DatabaseReference = rootRef.child("users").child(friends_Uids[i])
                    uidRef2.addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(p1: DataSnapshot) {
                            var friend = p1.getValue(User::class.java)
                            Log.d("1 row frm frds dtst", friend!!.userName.toString())
                            adapter.add(FriendItem(activity, view, friend))
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }

                    })
                }

                var friendsList_Recyclerview =
                    view.findViewById<RecyclerView>(R.id.friendsList_recyclerview)
                friendsList_Recyclerview.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun getFriendsFromFirebase(myCallback: FriendsListCallback) {
        var uid: String = FirebaseAuth.getInstance().currentUser!!.uid
        var rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        var uidRef: DatabaseReference = rootRef.child("friends").child(uid)

        uidRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var friends = dataSnapshot.getValue(Friends::class.java)
                if (friends != null) {
                    myCallback.onCallback(friends)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }

    interface FriendsListCallback {
        fun onCallback(value: Friends)
    }


    inner class FriendItem(var activity: FragmentActivity?, var view: View, val user: User?) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.friends_item_name.text = user!!.userName
            Log.d("usrname displayed", user.userName)

            var user_photo: ImageView = viewHolder.itemView.friends_item_photo
            var url = user.profileImage
            Picasso.get()
                .load(url)
                .into(user_photo)

            viewHolder.itemView.friends_item_unfollow_button.setOnClickListener {
                var chosenUserId: String? = user.uid

                FriendsFragment().getFriendsFromFirebase(object :
                    FriendsFragment.FriendsListCallback {
                    override fun onCallback(friends: Friends) {

                        var uid: String? = FirebaseAuth.getInstance().uid
                        var database: DatabaseReference = Firebase.database.reference
                        friendsUid_List = ArrayList(friends.friends_uid_list)
                        friendsUid_List.remove(chosenUserId)
                        database.child("friends").child(uid.toString()).child("friends_uid_list")
                            .setValue(friendsUid_List)
                        FriendsFragment().fetchFriends(view)

                    }
                })

            }

            viewHolder.itemView.friends_item_chat_button.setOnClickListener {
                var intent = Intent(activity, PrivatelyChatActivity::class.java)
                //intent.putExtra("chosen_user_id", user.uid)
                intent.putExtra(USER_KEY, user)
                activity?.startActivity(intent)

                // ......
            }

        }

        override fun getLayout(): Int {
            return R.layout.friends_list_row
        }
    }

}