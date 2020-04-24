package com.example.finalproject

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.finalproject.Data.BlogContent
import com.example.finalproject.Data.Comment
import com.example.finalproject.Fragment.BlogsFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_blog.*
import kotlinx.android.synthetic.main.fragment_register.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList


class AddBlogActivity : AppCompatActivity() {
    var selectedPhotoUri: Uri? = null
    val UserId = FirebaseAuth.getInstance().uid
    var isPublic: Boolean = true
    var showDate : Boolean = false
    var showAddress: Boolean = false
    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap : GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat : Double? = null
    private var currentLong : Double? = null
    var address : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_blog)
        supportActionBar?.title = "Add new blog"

        blog_image.setOnClickListener{

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        submit_button.setOnClickListener{
            val title = blog_title.text.toString()
            val description = blog_describtion.text.toString()

            if(title.isNotEmpty() && description.isNotEmpty()) {
                UploadBlogToStorage(title,description)
            }
            else{
                Toast.makeText(this,"Please input title and description",Toast.LENGTH_SHORT).show()

            }
        }

        checkBox2.setOnClickListener{
            showDate = true
        }
        checkBox3.setOnClickListener{
            showAddress = true
        }
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback{
            googleMap = it
            val latitude = 38.6270
            val longitude = -90.1994
            val zoomLevel = 15f

            val homeLatLng = LatLng(latitude, longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
            googleMap.addMarker(MarkerOptions().position(homeLatLng))
            setMapLongClick(googleMap)
            setPoiClick(googleMap)
            enableMyLocation()
        })
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {



            selectedPhotoUri = data.data
            blog_image.setImageURI(selectedPhotoUri)

        }
    }
    private fun UploadBlogToStorage(title:String,description:String){
        // add default uri when user did not post image
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/blogImages/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    UploadBlogToDatabase(it.toString(),title,description)
                }
            }

    }
    private fun UploadBlogToDatabase(Uri: String,title:String,description: String){
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        val formatedDate = formatter.format(date)
        val filename = UUID.randomUUID().toString()
        val blogInformation = BlogContent(UserId,filename,title,description,Uri,address,formatedDate,0,
            ArrayList<String>(),isPublic,showDate,showAddress)



        val ref = FirebaseDatabase.getInstance().getReference("/blog/$filename/")
        ref.setValue(blogInformation)
            .addOnSuccessListener {
                Log.d("AddBlog","Success")
                Toast.makeText(
                    this,
                    "Uploaded successfully!",
                    Toast.LENGTH_LONG
                ).show()
                val itent = Intent(this,MainScreenActivity::class.java)
                intent.putExtra("FromWhere", "BlogsFragment")
                startActivity(itent)
            }
            .addOnFailureListener {
                Log.d("AddBlog", "Failed to set value to database: ${it.message}")
                Toast.makeText(
                    this,
                    "Failed to upload the new blog!",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            googleMap.clear()

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            //新图标 获取地址报错
//          address = getAddress(latLng.latitude, latLng.longitude)
//            Log.d("AddBlog","new adress" + address)

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)

            )


        }
    }
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            googleMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    //store the location values
                    currentLat =  location?.latitude as Double?
                    currentLong = location?.longitude as Double?


                  address = getAddress(currentLat!!.toDouble(),currentLong!!.toDouble())
                    Log.d("address","adress" + address)
                    //set the location and zoom variables
                    val homeLatLng = LatLng(currentLat as Double, currentLong as Double)
                    val zoomLevel = 15f

                    //move the camera and set a marker
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
                    googleMap.addMarker(MarkerOptions().position(homeLatLng))

                }
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list[0].getAddressLine(0)
    }


}
