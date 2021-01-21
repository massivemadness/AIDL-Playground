package com.example.aidl.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AidlResult<T : Parcelable>(val data: T) : Parcelable