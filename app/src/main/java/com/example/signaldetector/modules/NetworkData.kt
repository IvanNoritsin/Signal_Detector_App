package com.example.signaldetector.modules

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.*
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity

class NetworkData(
    private val activity: AppCompatActivity,
    private val networkDataTextView: TextView,
    private val dataLogger: DataLogger
) {

    private var phoneStateListener: PhoneStateListener? = null
    private val tm: TelephonyManager by lazy {
        activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private var rsrpLte: Int = 0
    private var rsrqLte: Int = 0
    private var asuLevelLte: Int = 0
    private var levelLte: Int = 0
    private var operatorLte: CharSequence? = null
    private var mncLte: String? = ""
    private var mccLte: String? = ""

    fun getNetworkData() {
        phoneStateListener = object : PhoneStateListener() {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }

                val infoList = tm.allCellInfo
                val cellSignalStrengths = signalStrength.cellSignalStrengths
                val cellSignalStrength = cellSignalStrengths[0]

                for (i in infoList.indices) {
                    val cellInfo = infoList[i]

                    if (cellInfo is CellInfoLte) {
                        val cellSignalStrengthLte = if (i == 0) {
                            cellSignalStrength as CellSignalStrengthLte
                        } else {
                            cellInfo.cellSignalStrength
                        }
                        val cellIdentityLte = cellInfo.cellIdentity

                        rsrpLte = cellSignalStrengthLte.rsrp
                        rsrqLte = cellSignalStrengthLte.rsrq
                        asuLevelLte = cellSignalStrengthLte.asuLevel
                        levelLte = cellSignalStrengthLte.level
                        operatorLte = cellIdentityLte.operatorAlphaShort.toString()
                        mncLte = cellIdentityLte.mncString
                        mccLte = cellIdentityLte.mccString

                        if (i == 0) {
                            var interfaceApp = ""
                            interfaceApp += "Technology: LTE\n\n\n"
                            interfaceApp += "RSRP: $rsrpLte\n\n"
                            interfaceApp += "RSRQ: $rsrqLte\n\n"
                            interfaceApp += "ASU Level: $asuLevelLte\n\n"
                            interfaceApp += "Level: $levelLte\n\n\n"
                            interfaceApp += "Operator: $operatorLte\n\n"
                            interfaceApp += "Mnc: $mncLte\n\n"
                            interfaceApp += "Mcc: $mccLte\n\n"
                            networkDataTextView.text = interfaceApp
                        }

                        dataLogger.updateDataLTE(rsrpLte, rsrqLte, asuLevelLte, levelLte, operatorLte, mncLte, mccLte)
                    }
                }
            }
        }

        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        dataLogger.startLogging()
    }

    fun stopListening() {
        phoneStateListener?.let {
            tm.listen(it, PhoneStateListener.LISTEN_NONE)
        }
        dataLogger.stopLogging()
    }
}