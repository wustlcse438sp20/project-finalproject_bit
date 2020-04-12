package com.example.finalproject.Data


import android.os.Parcelable
import android.provider.ContactsContract
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid : String?, val email: String, val userName : String, val profileImage : String) : Parcelable {
    constructor() : this("","","","")
}