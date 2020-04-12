package com.example.finalproject.Fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register.*


import com.example.finalproject.Data.Friends
import com.example.finalproject.Data.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage


class RegisterFragment : Fragment() {

    lateinit var Register : Button
    lateinit var Upload_Photo : Button
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.finalproject.R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Upload_Photo = Reset_photo
        Register = Reset_Button
        Register.setOnClickListener {
            val Email = Register_Email.text.toString()
            val Password = Reset_password.text.toString()

            if (Email.isEmpty()||Password.isEmpty()){
                Toast.makeText(context,"Please input Email and Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(Password.length < 6){
                Toast.makeText(context,"Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(ContentValues.TAG, "Register successful")
                        upLoadPhotoToFirebaseStorage()
                        Toast.makeText(
                            context,
                            "Register successful, Please Login",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Log.w(ContentValues.TAG, "Failure", it.exception)
                        Toast.makeText(context, "Authentication fialed", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

        }
        Upload_Photo.setOnClickListener{
            Log.d("Register", "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            Log.d("Register", "Photo was selected")

            selectedPhotoUri = data.data


            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            Upload_Photo.alpha = 0f

        }
    }
    private fun upLoadPhotoToFirebaseStorage(){
        if(selectedPhotoUri == null) return
        val fileName = FirebaseAuth.getInstance().uid
        val ref = FirebaseStorage.getInstance().getReference("/image/$fileName")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "Upload image successful")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Register","image location "+it)
                    upLoadInformation(it.toString())
                }
            }
    }

    private fun upLoadInformation(profileImageUri:String){

        val uid = FirebaseAuth.getInstance().uid
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("/users/$uid")
        val user = User(uid!!, Register_Email.text.toString(), Reset_username.text.toString(), profileImageUri)
        myRef.setValue(user).addOnSuccessListener {
            Log.d("Register", "Upload information to database successful")
        }

        initialFriendsDatabase()
    }

    private fun initialFriendsDatabase(){
        var uid : String? = FirebaseAuth.getInstance().uid
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var myRef : DatabaseReference = database.getReference("/friends/$uid")
        var friends = Friends(uid!!, ArrayList())
        myRef.setValue(friends)
    }



}

