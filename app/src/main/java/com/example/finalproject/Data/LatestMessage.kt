package com.example.finalproject.Data

class LatestMessage (val id : String,
                  val text : String,
                  val fromId : String,
                  val toId : String,
                  val timestamp : Long, val readOrNot : Boolean) {
    constructor() : this("","","","", -1, true)
}