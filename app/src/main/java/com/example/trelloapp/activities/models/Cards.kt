package com.example.trelloapp.activities.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
 data class Cards(
    var cardName : String = "",
    val createdBy : String = "",
    val assignedTo : ArrayList<String> = ArrayList(),
    var colorTint : String = "",
    var dueDate : Long = 0
):Parcelable
