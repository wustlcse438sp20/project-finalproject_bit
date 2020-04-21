//package com.example.finalproject.Fragment
//
//import android.content.Context
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.finalproject.Data.BlogContent
//import com.example.finalproject.Data.Comment
//import com.example.finalproject.Data.User
//
//import com.example.finalproject.R
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.squareup.picasso.Picasso
//import com.xwray.groupie.GroupAdapter
//import com.xwray.groupie.GroupieViewHolder
//
//import com.xwray.groupie.Item
//import kotlinx.android.synthetic.main.bloglayout.*
//import kotlinx.android.synthetic.main.bloglayout.view.*
//import kotlinx.android.synthetic.main.commentlayout.*
//import kotlinx.android.synthetic.main.commentlayout.view.*
//import kotlinx.android.synthetic.main.fragment_comment.*
//import kotlinx.android.synthetic.main.simple_comment_row.view.*
//
//
//class CommentFragment : Fragment() {
//
////    val adapter = GroupAdapter<GroupieViewHolder>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_comment, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        fetchComment()
//    }
//
//
//    private fun fetchComment(){
//        val blogId = arguments?.getString("blogId")
//        Log.d("CID","blogId" + blogId)
////        val blogId ="1968edb1-d9e4-4d5a-a22b-c3f6fa54a496"
//        val ref = FirebaseDatabase.getInstance().getReference("/comment/$blogId")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                p0.children.forEach {
//                    val comment = it.getValue(Comment::class.java)
//                    Log.d("CG","comment" + comment)
//                    if (comment != null){
//
//                        val ref = FirebaseDatabase.getInstance().getReference("/users/${comment.uid}")
//                        ref.addListenerForSingleValueEvent(object :ValueEventListener{
//                            override fun onCancelled(p0: DatabaseError) {
//                            }
//
//                            override fun onDataChange(p0: DataSnapshot) {
//                                val user = p0.getValue(User::class.java)
//                                if(user != null) {
//                                    simpleComment_username.text = user.userName + " :"
//                                }
//                            }
//
//                        })
//
//                        simpleComment_comment.text = comment.content
//
////                        adapter.add(CommentItem(comment))
////                        Log.d("CF","comment" + comment)
//                    }
//                }
//            }
//
//
//        })
//
////        commentRecycleview.adapter = adapter
//    }
////    inner class CommentItem(val blog: Comment) : Item<GroupieViewHolder>() {
////
////        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
////
////            val Uid = blog.uid
////
////            val ref = FirebaseDatabase.getInstance().getReference("/users/$Uid")
////            ref.addListenerForSingleValueEvent(object :ValueEventListener{
////                override fun onCancelled(p0: DatabaseError) {
////                }
////
////                override fun onDataChange(p0: DataSnapshot) {
////                    val user = p0.getValue(User::class.java)
////                    if(user != null) {
////                        viewHolder.itemView.username2.text = user.userName + " :"
////                    }
////                }
////
////            })
////            viewHolder.itemView.comment2.text = blog.content
////        }
////
////        override fun getLayout(): Int {
////            return R.layout.simple_comment_row
////        }
////    }
//
//}