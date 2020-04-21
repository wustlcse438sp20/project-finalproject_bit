package com.example.finalproject.Data


class VideoContent (val uid : String?,
                    val videoId : String,
                    val videoUri : String,
                    val description: String,
                    //val address: String,
                    val date : String,
                    val favorite : Int,
                    val favoriteList : ArrayList<String>
){
    constructor():this("","","","","",0, ArrayList())
}
