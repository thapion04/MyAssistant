package com.example.myassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var speechManager: SpeechManager
    private lateinit var commandHandler: CommandHandler
    private lateinit var tvStatus: TextView
    private lateinit var btnSpeak: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnSpeak = findViewById(R.id.btnSpeak)

        commandHandler = CommandHandler(this)
        speechManager = SpeechManager(this) { result ->
            tvStatus.text = "Dijiste: $result"
            val response = commandHandler.handleCommand(result)
            speechManager.speak(response)
        }

        btnSpeak.setOnClickListener {
            if (checkPermissions()) {
                speechManager.startListening()
                tvStatus.text = "Escuchando..."
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA), 1)
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        speechManager.destroy()
    }
}
