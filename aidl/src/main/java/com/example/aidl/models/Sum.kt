package com.example.aidl.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sum(val sum: Int) : Parcelable