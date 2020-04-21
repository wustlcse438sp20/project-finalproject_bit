package com.example.finalproject.Data


class Comment(
    val id : String,
    val uid : String? ,
    val blogId: String,
    val content : String
) {
    constructor():this("","","","")
}
