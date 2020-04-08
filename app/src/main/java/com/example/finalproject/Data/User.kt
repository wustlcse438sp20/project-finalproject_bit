package com.example.finalproject.Data


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid : String, val userName : String, val profileImage : String) : Parcelable {
    constructor() : this("","","")
}