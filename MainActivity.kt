package com.shiva.motochargedisplay

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {
    private lateinit var percent: TextView
    private lateinit var headline: TextView
    private lateinit var detail: TextView
    private lateinit var plug: TextView

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateBattery(intent ?: registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.statusBarColor = android.graphics.Color.rgb(10, 24, 31)
        window.navigationBarColor = android.graphics.Color.rgb(10, 24, 31)
        window.decorView.setBackgroundColor(android.graphics.Color.rgb(10, 24, 31))

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 36, 28, 36)
        }
        val brand = text("MOTOROLA  •  TURBOPOWER", 14f, 0xFF9FC5D1.toInt()).apply {
            letterSpacing = 0.12f
        }
        percent = text("—%", 64f, android.graphics.Color.WHITE)
        headline = text("Connect charger", 23f, android.graphics.Color.WHITE)
        detail = text("Charging estimate will appear when available", 15f, 0xFFB8C8CE.toInt())
        plug = text("Charging display preview", 13f, 0xFF78909C.toInt())
        root.addView(brand, centeredParams(32))
        root.addView(percent, centeredParams(18))
        root.addView(headline, centeredParams(14))
        root.addView(detail, centeredParams(24))
        root.addView(plug, centeredParams(0))
        setContentView(root)
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val initial = registerReceiver(batteryReceiver, filter)
        updateBattery(initial)
    }

    private fun text(value: String, size: Float, color: Int) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.CENTER
    }

    private fun centeredParams(bottom: Int) = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply { bottomMargin = bottom }

    private fun updateBattery(intent: Intent?) {
        if (intent == null || !::percent.isInitialized) return
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        val pct = if (level >= 0) level * 100 / scale else -1
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val bm = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val remaining = bm.computeChargeTimeRemaining()
        percent.text = if (pct >= 0) "$pct%" else "—%"
        when {
            status == BatteryManager.BATTERY_STATUS_FULL || pct == 100 -> {
                headline.text = "Charged"
                detail.text = "Battery is full"
            }
            status == BatteryManager.BATTERY_STATUS_CHARGING || plugged != 0 -> {
                headline.text = "Charging"
                detail.text = if (remaining > 0) "Full in ${duration(remaining)}"
                    else "Time estimate unavailable on this firmware"
            }
            else -> {
                headline.text = "Connect charger"
                detail.text = "No charging estimate available"
            }
        }
        plug.text = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_USB -> "USB charging"
            BatteryManager.BATTERY_PLUGGED_AC -> "AC charging"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless charging"
            else -> "Charging display preview"
        }
    }

    private fun duration(ms: Long): String {
        val mins = TimeUnit.MILLISECONDS.toMinutes(ms).coerceAtLeast(1)
        val hours = mins / 60
        val remainder = mins % 60
        return if (hours > 0) "${hours}h ${remainder}m" else "$mins min"
    }

    override fun onDestroy() {
        try { unregisterReceiver(batteryReceiver) } catch (_: Exception) {}
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }
}
