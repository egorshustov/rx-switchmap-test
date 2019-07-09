package com.egorshustov.rxswitchmaptest.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    var comments: List<Comment>? = null
) : Parcelable