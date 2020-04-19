package com.example.finalproject.Fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.finalproject.Data.BlogContent
import com.example.finalproject.Data.VideoContent
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.bloglayout.*
import kotlinx.android.synthetic.main.fragment_video.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoFragment : Fragment() {

    private val REQUEST_CAMERA_PERMISSIONS = 1;
    private val VIDEO_CAPTURE = 9
    private var videoUri : Uri ? = null
    private val GALLERY = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.finalproject.R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        takeVideo_button.setOnClickListener{
            var permission = ContextCompat.checkSelfPermission(this@VideoFragment.context!!, android.Manifest.permission.CAMERA)

            if(permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),REQUEST_CAMERA_PERMISSIONS)
            }
            else {
                val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                // set file path by using fileprovider
                if (takeVideoIntent.resolveActivity(context!!.getPackageManager()) != null) {
                    val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        File(
                            context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/attachments")!!.path,
                            System.currentTimeMillis().toString() + ".mp4"
                        )
                    } else {
                        TODO("VERSION.SDK_INT < KITKAT")
                    }
                    // get uri from filepath
                    videoUri = FileProvider.getUriForFile(
                        this@VideoFragment.context!!,
                        this@VideoFragment.context!!.applicationContext.packageName + ".provider",
                        file
                    )
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1)
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        takeVideoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                    startActivityForResult(takeVideoIntent, VIDEO_CAPTURE)


                }
            }

        }
        takeVideo_button.setOnLongClickListener{
            Toast.makeText(context,it.getContentDescription(), Toast.LENGTH_SHORT).show();
            true
        }
        gallery_button.setOnClickListener{
//            chooseVideoFromGallary()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(intent, 3)
        }
        gallery_button.setOnLongClickListener{
            Toast.makeText(context,it.getContentDescription(), Toast.LENGTH_SHORT).show();
            true
        }
        videoSubmit_button.setOnClickListener{
            upLoadVideoToFirebaseStorage()

            Toast.makeText(context, "Video upload success, please return",Toast.LENGTH_SHORT).show()

        }
        videoSubmit_button.setOnLongClickListener{
            Toast.makeText(context,it.getContentDescription(), Toast.LENGTH_SHORT).show();
            true
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // load uri to video view
       if(requestCode == VIDEO_CAPTURE && resultCode == Activity.RESULT_OK ) {
           Video.setVideoURI(videoUri)
           val mediaController = MediaController(this@VideoFragment.context)
           mediaController?.setAnchorView(Video)
           Video.setMediaController(mediaController)
           Video.requestFocus()
           Video.start()
       }
        if (requestCode == GALLERY&& resultCode == Activity.RESULT_OK) {
            Log.d("what", "gale")
            if (data != null) {
               videoUri = data!!.data

//

                Video.setVideoURI(videoUri)
                val mediaController = MediaController(this@VideoFragment.context)
                mediaController?.setAnchorView(Video)
                Video.setMediaController(mediaController)
                Video.requestFocus()
                Video.start()

            }
        }
        if(requestCode ==3 && resultCode == Activity.RESULT_OK){
            videoUri = data!!.data

//            val selectedVideoPath = getPath(videoUri)
            Video.setVideoURI(videoUri)
            val mediaController = MediaController(this@VideoFragment.context)
            mediaController?.setAnchorView(Video)
            Video.setMediaController(mediaController)
            Video.requestFocus()
            Video.start()
        }
    }
    private fun upLoadVideoToFirebaseStorage(){
        if(videoUri == null) return
        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/video/$fileName")
        ref.putFile(videoUri!!)
            .addOnSuccessListener {
                Log.d("Video", "Upload video uri successful")
                ref.downloadUrl.addOnSuccessListener {
                    upLoadInformation(it.toString())
                }
            }
    }
    private fun upLoadInformation(uri : String) {
        val uid = arguments!!.getString("uid")
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        val formatedDate = formatter.format(date)
        val filename = UUID.randomUUID().toString()
        val description = video_describtion.text.toString()
        val blogInformation =
            VideoContent(uid, filename, uri, description,formatedDate,0)

        val ref = FirebaseDatabase.getInstance().getReference("/video/$filename/")
        ref.setValue(blogInformation)
            .addOnSuccessListener {
                Log.d("AddVideo", "Add Video Success")

            }
            .addOnFailureListener {
                Log.d("AddVideo", "Failed to set value to database: ${it.message}")
            }
    }
//    private fun chooseVideoFromGallary() {
//        val galleryIntent = Intent(
//            Intent.ACTION_PICK,
//            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//        )
//
//        startActivityForResult(galleryIntent, GALLERY)
//    }
//    private fun getPath(uri: Uri?): String? {
//        val projection = arrayOf(MediaStore.Video.Media.DATA)
//        val cursor = context!!.contentResolver.query(uri!!, projection, null, null, null)
//        if (cursor != null) {
//            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
//            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
//            val column_index = cursor!!
//                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//            cursor!!.moveToFirst()
//            return cursor!!.getString(column_index)
//        } else
//            return null
//    }
}