package com.example.appclient

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.Toast
import com.example.aidl.*
import com.example.aidl.base.AidlException
import com.example.aidl.base.AidlResult
import com.example.aidl.callback.AsyncCallback
import com.example.aidl.models.Sum

class MainActivity : AppCompatActivity() {

    companion object {
        private const val APP_SERVER_PACKAGE = "com.example.appserver"
        private const val APP_SERVER_ACTION = "com.example.aidl.REMOTE_CONNECTION"
    }

    private val appUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.data?.schemeSpecificPart == APP_SERVER_PACKAGE) {
                unregisterReceiver(this)
                reconnect()
            }
        }
    }
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            calculator = Calculator.Stub.asInterface(service)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            calculator = null
        }
    }

    private var calculator: Calculator? = null

    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.textView)
        button.setOnClickListener {
            calculator?.sum(2, 2, object : AsyncCallback.Stub() {
                override fun onSuccess(aidlResult: AidlResult<*>?) {
                    val sum = aidlResult?.data as? Sum
                    Toast.makeText(this@MainActivity, sum.toString(), Toast.LENGTH_SHORT).show()
                }
                override fun onError(aidlException: AidlException?) {
                    val exception = aidlException?.toException()
                    Toast.makeText(this@MainActivity, exception?.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        reconnect()
    }

    override fun onStop() {
        super.onStop()
        disconnect()
    }

    private fun reconnect() {
        bindService(createExplicitIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
        registerReceiver(appUpdateReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        })
    }

    private fun disconnect() {
        unregisterReceiver(appUpdateReceiver)
        unbindService(serviceConnection)
    }

    private fun createExplicitIntent(): Intent {
        val intent = Intent(APP_SERVER_ACTION)
        val services = packageManager.queryIntentServices(intent, 0)
        if (services.isEmpty()) {
            throw IllegalStateException("Приложение-сервер не установлено")
        }
        return Intent(intent).apply {
            val resolveInfo = services[0]
            val packageName = resolveInfo.serviceInfo.packageName
            val className = resolveInfo.serviceInfo.name
            component = ComponentName(packageName, className)
        }
    }
}