package com.example.signaldetector.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity

class Location(
    private val activity: AppCompatActivity,
    private val latitudeTextView: TextView,
    private val longitudeTextView: TextView
) {

    private val locationManager: LocationManager by lazy {
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            latitudeTextView.text = "${location.latitude}"
            longitudeTextView.text = "${location.longitude}"
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun getLocationData() {
        if (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }
}
