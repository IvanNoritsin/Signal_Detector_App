package com.example.signaldetector

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.telephony.*
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var networkDataTextView: TextView
    private lateinit var tm: TelephonyManager
    private lateinit var locationManager: LocationManager
    private var phoneStateListener: PhoneStateListener? = null

    fun getNetworkData() {
        phoneStateListener = object : PhoneStateListener() {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                    return
                }

                val infoList = tm.allCellInfo
                val cellSignalStrengths = signalStrength.cellSignalStrengths
                val cellSignalStrength = cellSignalStrengths[0]

                for (i in infoList.indices) {
                    val cellInfo = infoList[i]

                    when (cellInfo) {
                        is CellInfoLte -> {
                            val cellSignalStrengthLte = if (i == 0) {
                                cellSignalStrength as CellSignalStrengthLte
                            } else {
                                cellInfo.cellSignalStrength
                            }
                            val cellIdentityLte = cellInfo.cellIdentity

                            val rsrpLte = cellSignalStrengthLte.rsrp
                            val rsrqLte = cellSignalStrengthLte.rsrq
                            val rssiLte = cellSignalStrengthLte.rssi
                            val asuLevelLte = cellSignalStrengthLte.asuLevel
                            val levelLte = cellSignalStrengthLte.level
                            val registeredLte = cellInfo.isRegistered
                            val operatorLte = cellIdentityLte.operatorAlphaShort
                            val mncLte = cellIdentityLte.mncString
                            val mccLte = cellIdentityLte.mccString
                            val bandwidthLte = cellIdentityLte.bandwidth
                            Log.d("df", "$cellSignalStrengthLte")

                            if (i == 0) {
                                var interfaceApp = ""
                                interfaceApp += "Technology: LTE\n\n\n"
                                interfaceApp += "RSRP: $rsrpLte\n\n"
                                interfaceApp += "RSRQ: $rsrqLte\n\n"
                                interfaceApp += "RSSI: $rssiLte\n\n"
                                interfaceApp += "ASU Level: $asuLevelLte\n\n"
                                interfaceApp += "Level: $levelLte\n\n\n"
                                interfaceApp += "Operator: $operatorLte\n\n"
                                interfaceApp += "Mnc: $mncLte\n\n"
                                interfaceApp += "Mcc: $mccLte\n\n"
                                interfaceApp += "Bandwidth: $bandwidthLte\n\n"
                                networkDataTextView.text = interfaceApp
                                interfaceApp = ""
                            }
                        }
                    }
                }
            }
        }

        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    }

    fun getLocationData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitudeTextView.text = "${location.latitude}"
                longitudeTextView.text = "${location.longitude}"
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 1)
        } else {
            getNetworkData()
            getLocationData()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                getNetworkData()
                getLocationData()
            } else {
                Toast.makeText(this, "Не все разрешения предоставлены", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latitudeTextView = findViewById(R.id.latitude_TextView)
        longitudeTextView = findViewById(R.id.longitude_TextView)
        networkDataTextView = findViewById(R.id.networkData_TextView)

        tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        requestPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        phoneStateListener?.let {
            tm.listen(it, PhoneStateListener.LISTEN_NONE)
        }
        locationManager.removeUpdates {  }
    }
}
