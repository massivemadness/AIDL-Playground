package com.example.appserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.aidl.*
import com.example.aidl.base.AidlException
import com.example.aidl.base.AidlResult
import com.example.aidl.callback.AsyncCallback
import com.example.aidl.models.Sum

class BoundService : Service() {

    companion object {
        private const val TAG = "BoundService"
    }

    override fun onBind(intent: Intent?): IBinder {
        return object : Calculator.Stub() {
            override fun sum(first: Int, second: Int, callback: AsyncCallback?) {
                try {
                    val sum = Sum(first + second)
                    val aidlResult = AidlResult(sum)
                    Thread.sleep(2000) // TODO вынести в другой поток
                    callback?.onSuccess(aidlResult)
                } catch (e: Throwable) {
                    Log.e(TAG, e.message, e)
                    if (e is RuntimeException) {
                        val aidlException = AidlException(e.message, AidlException.ARITHMETIC_EXCEPTION)
                        callback?.onError(aidlException)
                    }
                }
            }
        }
    }
}