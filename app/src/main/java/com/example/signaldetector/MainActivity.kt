package com.example.signaldetector

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.signaldetector.helpers.Location
import com.example.signaldetector.helpers.NetworkData
import com.example.signaldetector.helpers.Permissions

class MainActivity : AppCompatActivity() {

    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var networkDataTextView: TextView
    private lateinit var location: Location
    private lateinit var networkData: NetworkData
    private lateinit var permissions: Permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latitudeTextView = findViewById(R.id.latitude_TextView)
        longitudeTextView = findViewById(R.id.longitude_TextView)
        networkDataTextView = findViewById(R.id.networkData_TextView)

        location = Location(this, latitudeTextView, longitudeTextView)
        networkData = NetworkData(this, networkDataTextView)
        permissions = Permissions(this)

        permissions.requestPermissions {
            networkData.getNetworkData()
            location.getLocationData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkData.stopListening()
        location.stopLocationUpdates()
    }
}
