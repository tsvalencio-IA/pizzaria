package br.com.thiaguinho.pizzariarastreamento

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

object Battery {
    fun percent(context: Context): Int? {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return null
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null
        return ((level * 100f) / scale).toInt()
    }
}
