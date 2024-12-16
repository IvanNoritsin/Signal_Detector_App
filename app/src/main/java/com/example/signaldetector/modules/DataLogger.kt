package com.example.signaldetector.modules

import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataLogger(private val interval: Long = 5000) {

    private val jsonAllInfo = JSONObject()
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    private var rsrpLte: Int = 0
    private var rsrqLte: Int = 0
    private var asuLevelLte: Int = 0
    private var levelLte: Int = 0
    private var operatorLte: CharSequence? = null
    private var mncLte: String? = ""
    private var mccLte: String? = ""
    private var time: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val dataLogger = object : Runnable {
        override fun run() {
            updateCurrentTime()
            writeToJsonLTE(rsrpLte, rsrqLte, asuLevelLte, levelLte, operatorLte, mncLte, mccLte)
            handler.postDelayed(this, interval)
        }
    }

    fun startLogging() {
        handler.post(dataLogger)
    }

    fun stopLogging() {
        handler.removeCallbacks(dataLogger)
    }

    fun updateDataLTE(
        rsrp: Int, rsrq: Int, asuLevel: Int, level: Int,
        operator: CharSequence?, mnc: String?, mcc: String?
    ) {
        this.rsrpLte = rsrp
        this.rsrqLte = rsrq
        this.asuLevelLte = asuLevel
        this.levelLte = level
        this.operatorLte = operator
        this.mncLte = mnc
        this.mccLte = mcc
    }

    private fun updateCurrentTime() {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        time = currentTime
    }

    fun updateLocation(lat: Double, lon: Double) {
        this.latitude = lat
        this.longitude = lon
    }

    private fun writeToJsonLTE(
        rsrpLte: Int, rsrqLte: Int, asuLevelLte: Int, levelLte: Int,
        operatorLte: CharSequence?, mncLte: String?, mccLte: String?
    ) {
        try {
            if (rsrpLte != 0) {
                val jsonLteCellInfo = JSONObject().apply {
                    put("rsrpLte", rsrpLte)
                    put("rsrqLte", rsrqLte)
                    put("asuLevelLte", asuLevelLte)
                    put("levelLte", levelLte)
                    put("operatorLte", operatorLte)
                    put("mncLte", mncLte)
                    put("mccLte", mccLte)
                    put("time", time)
                    put("latitude", latitude)
                    put("longitude", longitude)
                }

                jsonAllInfo.accumulate("jsonLteCellInfo", jsonLteCellInfo)

                val file = File("/data/data/com.example.signaldetector/files/network_data.json")

                FileWriter(file, false).use { writer ->
                    writer.write(jsonAllInfo.toString())
                    Log.d("DataLogger", "Данные записаны: $jsonAllInfo")
                }
            }

        } catch (e: IOException) {
            Log.e("DataLogger", "Ошибка записи JSON файла", e)
        } catch (e: Exception) {
            Log.e("DataLogger", "Непредвиденная ошибка", e)
        }
    }

    fun clearJsonObject() {
        jsonAllInfo.keys().forEach { key ->
            jsonAllInfo.remove(key)
        }
        Log.d("DataLogger", "JSON объект очищен")
    }
}