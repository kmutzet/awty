package edu.uw.ischool.kmuret.awty

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var phoneNumberEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var intervalEditText: EditText
    private lateinit var startStopButton: Button
    private var isServiceRunning = false

    private val SEND_SMS_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        messageEditText = findViewById(R.id.messageEditText)
        intervalEditText = findViewById(R.id.intervalEditText)
        startStopButton = findViewById(R.id.startStopButton)

        phoneNumberEditText.addTextChangedListener(textWatcher)
        messageEditText.addTextChangedListener(textWatcher)
        intervalEditText.addTextChangedListener(textWatcher)

        updateButtonState()

        startStopButton.setOnClickListener {
            if (!isServiceRunning) {
                checkAndRequestPermissions()
            } else {
                stopService()
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                SEND_SMS_PERMISSION_REQUEST_CODE
            )
        } else {
            startService()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val message = messageEditText.text.toString().trim()
        val interval = intervalEditText.text.toString().trim()

        val isPhoneNumberValid = isValidPhoneNumber(phoneNumber)
        val areAllFieldsFilled = phoneNumber.isNotEmpty() && message.isNotEmpty() && interval.isNotEmpty()

        startStopButton.isEnabled = isPhoneNumberValid && areAllFieldsFilled
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && phoneNumber.all {
            it.isDigit()
        }
    }

    private fun startService() {
        val phoneNumber = phoneNumberEditText.text.toString()
        val message = messageEditText.text.toString()
        val intervalMinutes = intervalEditText.text.toString().toIntOrNull()

        if (intervalMinutes != null && intervalMinutes > 0) {
            val intent = Intent(this, Messenger::class.java)
            intent.putExtra("phoneNumber", phoneNumber)
            intent.putExtra("message", message)
            intent.putExtra("intervalMinutes", intervalMinutes)

            sendSmsMessage(phoneNumber, message)

            startService(intent)

            isServiceRunning = true
            startStopButton.text = "Stop"
        } else {
            showToast("Please enter a valid positive interval.")
        }
    }

    private fun stopService() {
        val intent = Intent(this, Messenger::class.java)
        stopService(intent)
        isServiceRunning = false
        startStopButton.text = "Start"
    }

    private fun sendSmsMessage(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: SecurityException) {
            showToast("Error sending SMS. Please check permissions.")
        } catch (e: Exception) {
            showToast("Error sending SMS.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}