package com.example.signaldetector

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.signaldetector.modules.DataLogger
import com.example.signaldetector.modules.Location
import com.example.signaldetector.modules.NetworkData
import com.example.signaldetector.modules.Permissions
import com.example.signaldetector.modules.WebSocketSender

class MainActivity : AppCompatActivity() {

    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var networkDataTextView: TextView
    private lateinit var location: Location
    private lateinit var networkData: NetworkData
    private lateinit var permissions: Permissions
    private lateinit var dataLogger: DataLogger
    private lateinit var webSocketSender: WebSocketSender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latitudeTextView = findViewById(R.id.latitude_TextView)
        longitudeTextView = findViewById(R.id.longitude_TextView)
        networkDataTextView = findViewById(R.id.networkData_TextView)
        dataLogger = DataLogger()

        val filePath = "/data/data/com.example.signaldetector/files/network_data.json"
        val serverUrl = "wss://4f99-45-136-49-30.ngrok-free.app/ws"
        webSocketSender = WebSocketSender(serverUrl, filePath, dataLogger)

        location = Location(this, latitudeTextView, longitudeTextView, dataLogger)
        networkData = NetworkData(this, networkDataTextView, dataLogger)
        permissions = Permissions(this)

        permissions.requestPermissions {
            networkData.getNetworkData()
            location.getLocationData()
            webSocketSender.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkData.stopListening()
        location.stopLocationUpdates()
        webSocketSender.stop()
    }
}

