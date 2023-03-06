package com.example.appserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
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
                    Thread.sleep(2000) // симулируем задержку
                    // throw RuntimeException("Невозможно вычислить результат")
                    callback?.onSuccess(aidlResult)
                } catch (e: Throwable) {
                    Log.e(TAG, e.message, e)
                    if (e !is RemoteException) {
                        val errorCode = when (e) {
                            is ArithmeticException -> AidlException.ARITHMETIC_EXCEPTION
                            is RuntimeException -> AidlException.RUNTIME_EXCEPTION
                            else -> AidlException.RUNTIME_EXCEPTION
                        }
                        val aidlException = AidlException(e.message, errorCode)
                        callback?.onError(aidlException)
                    }
                }
            }
        }
    }
}