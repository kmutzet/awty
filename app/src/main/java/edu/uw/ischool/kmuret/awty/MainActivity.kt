package edu.uw.ischool.kmuret.awty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private lateinit var phoneNumberEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var intervalEditText: EditText
    private lateinit var startStopButton: Button
    private var isServiceRunning = false

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
                startService()
            } else {
                stopService()
            }
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

        // Log values for debugging
        Log.d("MainActivity", "Phone: $phoneNumber, Message: $message, Interval: $interval")
        Log.d("MainActivity", "Phone valid: $isPhoneNumberValid, All fields filled: $areAllFieldsFilled")

        startStopButton.isEnabled = isPhoneNumberValid && areAllFieldsFilled
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    }

    private fun startService() {
        val phoneNumber = phoneNumberEditText.text.toString()
        val message = messageEditText.text.toString()
        val intervalMinutes = intervalEditText.text.toString().toIntOrNull()

        val intent = Intent(this, Messenger::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("message", message)
        intent.putExtra("intervalMinutes", intervalMinutes)
        startService(intent)
        isServiceRunning = true
        startStopButton.text = "Stop"
    }

    private fun stopService() {
        val intent = Intent(this, Messenger::class.java)
        stopService(intent)
        isServiceRunning = false
        startStopButton.text = "Start"
    }
}
