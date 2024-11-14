package com.example.trelloapp.activities.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val name : String = "",
    val createdBy : String = "",
    val cards : ArrayList<Cards> = ArrayList()
):Parcelable
