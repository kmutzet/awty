package edu.uw.ischool.kmuret.awty

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast


class Messenger : Service() {
    private var isRunning = false
    private val handler = Handler()
    private lateinit var phoneNumber: String
    private lateinit var message: String
    private var intervalMinutes: Int = 0

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
        handler.postDelayed({
            if (isRunning) {
                showToast("$phoneNumber: $message")
                startSendingMessages()
            }
        }, (intervalMinutes * 60 * 1000).toLong())
    }
    private fun formatPhoneNumber(phoneNumber: String) : String {
        val area = phoneNumber.substring(0, 3)
        val first = phoneNumber.substring(3, 6)
        val second = phoneNumber.substring(6, 10)
        return "($area) $first-$second"
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}