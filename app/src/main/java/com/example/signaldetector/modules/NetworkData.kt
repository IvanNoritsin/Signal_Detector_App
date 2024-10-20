package com.example.signaldetector.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.*
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity

class NetworkData(
    private val activity: AppCompatActivity,
    private val networkDataTextView: TextView
) {

    private var phoneStateListener: PhoneStateListener? = null
    private val tm: TelephonyManager by lazy {
        activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    fun getNetworkData() {
        phoneStateListener = object : PhoneStateListener() {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                if (ActivityCompat.checkSelfPermission(
                        activity,
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
                            }
                        }
                    }
                }
            }
        }

        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    }

    fun stopListening() {
        phoneStateListener?.let {
            tm.listen(it, PhoneStateListener.LISTEN_NONE)
        }
    }
}
