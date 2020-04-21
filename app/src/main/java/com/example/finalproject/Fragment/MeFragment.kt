package com.example.finalproject.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.finalproject.Data.User
import com.example.finalproject.MainActivity
import com.example.finalproject.MainScreenActivity
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bloglayout.*
import kotlinx.android.synthetic.main.bloglayout.view.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.fragment_me.Reset_username
import java.io.ByteArrayOutputStream
import java.io.File



class MeFragment : Fragment(){
    private  var selectedPhotoUri: Uri? = null
    private  val REQUEST_CAMERA_PERMISSIONS = 1;
    private  val IMAGE_CAPTURE_CODE = 2;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var logout_Button : Button = view.findViewById(R.id.logout_button)
        logout_Button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            var intent = Intent(activity, MainActivity::class.java);
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity?.startActivity(intent)
        }
        Avatar_Button.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        Camera_Button.setOnClickListener{
            var permission = ContextCompat.checkSelfPermission(this@MeFragment.context!!, android.Manifest.permission.CAMERA)

            if(permission != PackageManager.PERMISSION_GRANTED) {
               requestPermissions(arrayOf(android.Manifest.permission.CAMERA),REQUEST_CAMERA_PERMISSIONS)
            } else {

//                val cIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(cIntent, IMAGE_CAPTURE_CODE)

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if (takePictureIntent.resolveActivity(context?.getPackageManager()) != null) {
                    val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        File(context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/attachments")!!.path,
                            System.currentTimeMillis().toString() + ".jpg")
                    } else {
                        TODO("VERSION.SDK_INT < KITKAT")
                    }

                    selectedPhotoUri = FileProvider.getUriForFile(this@MeFragment.context!!, this@MeFragment.context!!.applicationContext.packageName + ".provider", file!!)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedPhotoUri)
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                    startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
                }




            }
        }
        Reset_Button.setOnClickListener{
            upLoadPhotoToFirebaseStorage()
        }
        ResetPassword.setOnClickListener{


            val password = Reset_password.text.toString()
            if(password.isEmpty() || password.length < 6){
                Toast.makeText(this@MeFragment.context,"Please type valid password",Toast.LENGTH_SHORT).show()
            }else {

                val user = FirebaseAuth.getInstance().currentUser
                user!!.updatePassword(password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("RestPassword", "Rest Password Success")
                    } else {
                        Log.d("RestPassword", "Rest Password Fail")
                    }
                }
            }
        }
        fetchUserData()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if(hidden) {

        }
        else {
            (activity as MainScreenActivity).supportActionBar?.title = "Me"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            Log.d("Register", "Photo was selected")

            selectedPhotoUri = data.data


            val bitmap =
                MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedPhotoUri)

            Reset_imageview.setImageBitmap(bitmap)


        }
        if(requestCode == IMAGE_CAPTURE_CODE &&resultCode == Activity.RESULT_OK  ) {
//                val bitmap = data!!.extras!!["data"] as Bitmap


            val bitmap =
                MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedPhotoUri)
                Reset_imageview.setImageBitmap(bitmap)
//            selectedPhotoUri = getImageUriFromBitmap(this@MeFragment.context!!,bitmap)
            Log.d("GetPhotp", "Photo uri" + selectedPhotoUri)

        }
    }

    private fun fetchUserData(){
        getInformationFromFirebase(object: UserListCallback {
            override fun onCallback(user: User) {
                Reset_username.setText(user.userName)
                Picasso.get().load(user.profileImage).into(Reset_imageview)

            }
        })
    }
    private fun getInformationFromFirebase(myCallback:UserListCallback) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("users").child(uid)

        uidRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    myCallback.onCallback(user)

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })

    }


    interface UserListCallback {
        fun onCallback(value: User)
    }
    private fun upLoadPhotoToFirebaseStorage(){
        if(selectedPhotoUri == null) return
        val fileName = FirebaseAuth.getInstance().currentUser!!.uid
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
        var email : String = ""


        var rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        var uidRef: DatabaseReference = rootRef.child("users").child(uid!!)
        uidRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p1: DataSnapshot) {
                var me = p1.getValue(User::class.java)
                email = me!!.email
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        val user = User(uid!!, email, Reset_username.text.toString(), profileImageUri)
        myRef.setValue(user).addOnSuccessListener {
            Log.d("Register", "Upload information to database successful")
        }

    }
}