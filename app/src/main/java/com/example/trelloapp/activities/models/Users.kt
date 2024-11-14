package com.example.trelloapp.activities.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// you need parcelable because the data in the data class won't be saved inside the firestore database
@Parcelize
data class Users (
    val id :String = "",
    val name:String = "",
    val email:String = "",
    val number : Long = 0,
    val profileImageUrl:String = "",
    val token : String = ""
):Parcelable