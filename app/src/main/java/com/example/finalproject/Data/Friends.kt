package com.example.finalproject.Data

class Friends(
    val uid : String?,
    val friends_uid_list : ArrayList<String>
){
    constructor() : this("",ArrayList())
}