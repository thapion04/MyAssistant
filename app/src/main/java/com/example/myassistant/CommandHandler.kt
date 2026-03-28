package com.example.myassistant

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

class CommandHandler(private val context: Context) {

    fun handleCommand(text: String): String {
        val command = text.lowercase()

        return when {
            command.contains("abre youtube") || command.contains("abrir youtube") -> {
                openApp("com.google.android.youtube")
                "Abriendo YouTube"
            }
            command.contains("abre google") || command.contains("abrir google") -> {
                openUrl("https://www.google.com")
                "Abriendo Google"
            }
            command.contains("enciende la linterna") || command.contains("prende la linterna") -> {
                toggleFlashlight(true)
                "Encendiendo la linterna"
            }
            command.contains("apaga la linterna") -> {
                toggleFlashlight(false)
                "Apagando la linterna"
            }
            command.contains("abre ajustes") || command.contains("abrir configuración") -> {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                "Abriendo ajustes"
            }
            command.contains("busca") || command.contains("buscar") -> {
                val query = command.replace("busca", "").replace("buscar", "").trim()
                if (query.isNotEmpty()) {
                    openUrl("https://www.google.com/search?q=$query")
                    "Buscando $query en Google"
                } else {
                    "¿Qué quieres que busque?"
                }
            }
            command.contains("hora") -> {
                val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                "Son las " + sdf.format(java.util.Date())
            }
            command.contains("batería") || command.contains("bateria") -> {
                getBatteryStatus()
            }
            command.contains("envía un mensaje de prueba") || command.contains("enviar mensaje de prueba") -> {
                sendTestMessage()
                "Preparando mensaje de prueba"
            }
            command.contains("hola") -> "Hola, ¿en qué puedo ayudarte?"
            command.contains("quién eres") -> "Soy tu asistente virtual personalizado."
            else -> "No estoy seguro de cómo hacer eso todavía, pero puedo aprender."
        }
    }

    private fun getBatteryStatus(): String {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = (level * 100 / scale.toFloat()).toInt()
        
        return "Tienes un $batteryPct por ciento de batería."
    }

    private fun sendTestMessage() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Este es un mensaje de prueba de mi asistente virtual.")
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    private fun openApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "La aplicación no está instalada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun toggleFlashlight(status: Boolean) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, status)
        } catch (e: Exception) {
            Toast.makeText(context, "Error con la linterna: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
