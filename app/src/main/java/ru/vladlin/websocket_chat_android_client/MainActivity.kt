package ru.vladlin.websocket_chat_android_client

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.vladlin.websocket_chat_android_client.databinding.ActivityMainBinding
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var webSocketClient: WebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun initWebSocket() {
        val webSocketUrl = URI(WEB_SOCKET_URL)
        createWebSocketClient(webSocketUrl)
        webSocketClient.connect()
    }

    private fun createWebSocketClient(webSocketUrl: URI?) {
        webSocketClient = object : WebSocketClient(webSocketUrl) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
                webSocketClient.send("/name Android")
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage: $message")
                setUpMessage(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "onError: ${ex?.message}")
            }
        }
    }

    private fun sendMessage() {
        webSocketClient.send(
            binding.editText.text.toString()
        )
        binding.editText.text.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpMessage(message: String?) {
        runOnUiThread {
            binding.log.text = binding.log.text.toString() + "\n-> " + message.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        initWebSocket()
    }

    override fun onPause() {
        super.onPause()
        webSocketClient.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val WEB_SOCKET_URL = "ws://83.220.169.90:8088/ws"
        const val TAG = "TagWebSocket"
    }
}