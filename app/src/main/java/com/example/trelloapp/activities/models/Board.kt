package com.example.trelloapp.activities.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    val name : String = "",
    val imageBoard : String = "",
    val createdBy: String = "",
    val joiners : ArrayList<String> = ArrayList(),
    var documentId: String="",
    val taskList: ArrayList<Task> = ArrayList()
) : Parcelable


