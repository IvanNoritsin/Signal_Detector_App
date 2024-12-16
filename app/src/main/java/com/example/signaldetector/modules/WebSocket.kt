package com.example.signaldetector.modules

import android.util.Log
import okhttp3.*
import okio.ByteString
import java.io.File
import java.io.FileWriter
import java.util.concurrent.TimeUnit

class WebSocketSender(
    private val serverUrl: String,
    private val filePath: String,
    private val dataLogger: DataLogger
) {

    private var webSocket: WebSocket? = null
    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)  // Отправка пинга каждые 30 секунд для поддержания соединения
        .build()

    private val request: Request = Request.Builder()
        .url(serverUrl)
        .build()

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val sendRunnable = object : Runnable {
        override fun run() {
            sendJsonFile()
            handler.postDelayed(this, 20000) // Повтор через 5 минут
        }
    }

    fun start() {
        webSocket = client.newWebSocket(request, WebSocketListenerImpl())
        handler.post(sendRunnable)
    }

    fun stop() {
        webSocket?.close(1000, "Closing connection")
        handler.removeCallbacks(sendRunnable)
    }

    private fun sendJsonFile() {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                Log.e("WebSocketSender", "Файл не найден: $filePath")
                return
            }

            val jsonContent = file.readText()
            webSocket?.send(jsonContent)
            Log.d("WebSocketSender", "JSON отправлен: $jsonContent")
            dataLogger.clearJsonObject()
        } catch (e: Exception) {
            Log.e("WebSocketSender", "Ошибка при отправке файла", e)
        }
    }

    private inner class WebSocketListenerImpl : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocketSender", "WebSocket подключен")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocketSender", "Сообщение от сервера: $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("WebSocketSender", "Бинарное сообщение от сервера: $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocketSender", "WebSocket закрывается: Код=$code, Причина=$reason")
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocketSender", "WebSocket закрыт: Код=$code, Причина=$reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocketSender", "Ошибка в WebSocket", t)
        }
    }
}
