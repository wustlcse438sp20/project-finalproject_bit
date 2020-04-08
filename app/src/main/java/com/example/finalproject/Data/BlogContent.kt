package com.example.finalproject.Data



class BlogContent (val uid : String?,
                   val blogId : String,
                   val title: String,
                   val description: String,
                   val imageUri: String,
                   val address : String,
                   val date : String,
                   val favorite : Int,
                   val isPublic : Boolean,
                   val showDate: Boolean,
                   val showAddress: Boolean

){
    constructor():this("","","","","","","",0,true,true,true)
}



