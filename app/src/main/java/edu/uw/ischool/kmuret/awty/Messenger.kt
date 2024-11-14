package edu.uw.ischool.kmuret.awty

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.telephony.SmsManager
import android.widget.Toast

class Messenger : Service() {
    private var isRunning = false
    private lateinit var phoneNumber: String
    private lateinit var message: String
    private var intervalMinutes: Int = 0

    private val handler = Handler()
    private val smsRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                sendSmsMessage(phoneNumber, message)
                handler.postDelayed(this, (intervalMinutes * 60 * 1000).toLong())
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning && intent != null) {
            phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
            phoneNumber = formatPhoneNumber(phoneNumber)
            message = intent.getStringExtra("message") ?: ""
            intervalMinutes = intent.getIntExtra("intervalMinutes", 0)

            startSendingMessages()
        }

        return START_STICKY
    }

    private fun startSendingMessages() {
        isRunning = true
        handler.postDelayed(smsRunnable, 0)
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val area = phoneNumber.substring(0, 3)
        val first = phoneNumber.substring(3, 6)
        val second = phoneNumber.substring(6, 10)

        return "($area) $first-$second"
    }

    private fun sendSmsMessage(phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        showToast("$phoneNumber: $message")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacks(smsRunnable)
    }
}