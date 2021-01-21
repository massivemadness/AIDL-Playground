package com.example.aidl.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.lang.RuntimeException

@Parcelize
class AidlException(
    private val errorMessage: String?,
    private val errorCode: Int = RUNTIME_EXCEPTION
) : Parcelable {

    companion object {
        const val RUNTIME_EXCEPTION = 1000
        const val ARITHMETIC_EXCEPTION = 1001
        // TODO другие коды ошибок...
    }

    // конвертация на стороне клиента
    fun toException(): Exception {
        return when (errorCode) {
            RUNTIME_EXCEPTION -> RuntimeException(errorMessage)
            ARITHMETIC_EXCEPTION -> ArithmeticException(errorMessage)
            else -> RuntimeException(errorMessage)
        }
    }
}