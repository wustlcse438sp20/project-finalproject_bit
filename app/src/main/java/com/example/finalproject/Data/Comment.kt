package com.example.finalproject.Data


class Comment(
    val uid : String? ,
    val blogId: String,
    val content : String
) {
    constructor():this("","","")
}
