package com.example.finalproject.Data


class VideoContent (val uid : String?,
                    val videoId : String,
                    val videoUri : String,
                    val description: String,
                    val date : String,
                    val favorite : Int
){
    constructor():this("","","","","",0)
}
