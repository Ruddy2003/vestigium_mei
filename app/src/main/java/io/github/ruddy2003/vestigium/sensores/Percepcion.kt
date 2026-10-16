package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Instant
import java.time.ZoneId
import kotlin.coroutines.resume

/**
 * Cada 15 minutos el teléfono anota lo que percibe: a qué wifi está conectado, si hay audífonos,
 * bocina o el coche por Bluetooth, si suena música, si está cargando, la batería y la luz del ambiente.
 * Con eso se estiman minutos por día. No se escucha nada ni se guardan contraseñas.
 */
object Percepcion {
    private const val PREFS = "percepcion"

    fun bluetooth(ctx: Context) = Build.VERSION.SDK_INT < 31 || ctx.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED

    /** Una lectura del sensor de luz, o null si no hay sensor o no respondió a tiempo. */
    suspend fun lux(ctx: Context, esperaMs: Long = 1500): Double? {
        val sm = ctx.getSystemService(SensorManager::class.java) ?: return null
        val sensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT) ?: return null
        return withTimeoutOrNull(esperaMs) {
            suspendCancellableCoroutine { cont ->
                val l = object : SensorEventListener {
                    override fun onSensorChanged(e: SensorEvent) { sm.unregisterListener(this); if (cont.isActive) cont.resume(e.values[0].toDouble()) }
                    override fun onAccuracyChanged(s: Sensor?, a: Int) {}
                }
                sm.registerListener(l, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                cont.invokeOnCancellation { sm.unregisterListener(l) }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun wifi(ctx: Context): String? {
        val wm = ctx.applicationContext.getSystemService(WifiManager::class.java) ?: return null
        val info = wm.connectionInfo ?: return null
        if (info.networkId == -1 && info.ipAddress == 0) return null
        val s = info.ssid?.trim('"') ?: return null
        return if (s.isBlank() || s == "<unknown ssid>") "wifi (sin nombre)" else s
    }

    /** Clasifica lo que está sonando por Bluetooth o cable: audífonos, coche, bocina u otro. */
    @SuppressLint("MissingPermission")
    private fun audio(ctx: Context): List<Pair<String, String>> {
        val am = ctx.getSystemService(AudioManager::class.java) ?: return emptyList()
        val salidas = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val clases = HashMap<String, Int>()
        if (bluetooth(ctx)) runCatching {
            ctx.getSystemService(BluetoothManager::class.java)?.adapter?.bondedDevices?.forEach { d ->
                val n = d.name ?: return@forEach
                clases[n] = d.bluetoothClass?.deviceClass ?: 0
            }
        }
        val out = ArrayList<Pair<String, String>>()
        for (d in salidas) {
            val nombre = d.productName?.toString()?.trim().orEmpty()
            val cat = when (d.type) {
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES, AudioDeviceInfo.TYPE_WIRED_HEADSET, AudioDeviceInfo.TYPE_USB_HEADSET -> "audifonos"
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP, AudioDeviceInfo.TYPE_BLE_HEADSET, AudioDeviceInfo.TYPE_BLE_SPEAKER -> when (clases[nombre]) {
                    BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO, BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE -> "coche"
                    BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES, BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> "audifonos"
                    BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER, BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO, BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO -> "bocina"
                    else -> if (d.type == AudioDeviceInfo.TYPE_BLE_SPEAKER) "bocina" else if (d.type == AudioDeviceInfo.TYPE_BLE_HEADSET) "audifonos" else "bluetooth"
                }
                AudioDeviceInfo.TYPE_USB_DEVICE, AudioDeviceInfo.TYPE_USB_ACCESSORY -> "usb"
                AudioDeviceInfo.TYPE_HDMI -> "pantalla"
                else -> continue
            }
            if (out.none { it.second == nombre && it.first == cat }) out.add(cat to nombre.ifBlank { cat })
        }
        return out
    }

    private fun suma(m: Map<String, Int>, k: String, v: Int) = m + (k to ((m[k] ?: 0) + v))

    /** Se llama desde el trabajo de fondo. Atribuye al día el tiempo transcurrido desde la muestra anterior. */
    suspend fun muestrear(ctx: Context) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ahora = System.currentTimeMillis()
        val antes = p.getLong("ultimo", 0L)
        val hueco = ahora - antes
        // Si el teléfono durmió mucho tiempo entre muestras, solo se cuentan 15 minutos para no inventar.
        val min = when { antes == 0L -> 15; hueco <= 0 -> 0; hueco > 2 * 3_600_000L -> 15; else -> (hueco / 60_000).toInt().coerceAtMost(60) }
        p.edit().putLong("ultimo", ahora).apply()
        if (min <= 0) return
        val fecha = Fechas.diaLogicoDeMillis(ahora)
        val hora = Instant.ofEpochMilli(ahora).atZone(ZoneId.systemDefault()).hour

        val red = runCatching { wifi(ctx) }.getOrNull()
        val sonidos = runCatching { audio(ctx) }.getOrDefault(emptyList())
        val musica = runCatching { ctx.getSystemService(AudioManager::class.java)?.isMusicActive == true }.getOrDefault(false)
        val bat = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val enchufado = (bat?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0) != 0
        val nivel = bat?.let { val l = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1); val e = it.getIntExtra(BatteryManager.EXTRA_SCALE, 100); if (l >= 0 && e > 0) l * 100 / e else null }
        val luz = if (Repo.config.medir_luz) runCatching { lux(ctx) }.getOrNull() else null

        // Carga de noche: si se conectó entre las 20:00 y las 4:00 y duró más de 3 horas, marca aproximada de cuándo dormiste.
        val desde = p.getLong("carga_desde", 0L)
        if (enchufado && desde == 0L) p.edit().putLong("carga_desde", ahora).apply()
        if (!enchufado && desde > 0L) {
            p.edit().remove("carga_desde").apply()
            val h = Instant.ofEpochMilli(desde).atZone(ZoneId.systemDefault()).hour
            if ((h >= 20 || h < 4) && ahora - desde > 3 * 3_600_000L && ahora - desde < 16 * 3_600_000L) {
                Repo.cambiarConexion(Fechas.diaLogicoDeMillis(desde)) { it.copy(carga_noche_inicio = Fechas.isoDeMillis(desde), carga_noche_fin = Fechas.isoDeMillis(ahora)) }
            }
        }

        Repo.cambiarConexion(fecha) { c ->
            var n = c.copy(muestras = c.muestras + 1)
            n = if (red != null) n.copy(wifi_min = suma(n.wifi_min, red, min)) else n.copy(sin_wifi_min = n.sin_wifi_min + min)
            sonidos.forEach { (cat, nombre) -> n = n.copy(audio_min = suma(n.audio_min, cat, min), dispositivos_min = suma(n.dispositivos_min, nombre, min)) }
            if (musica) n = n.copy(musica_min = n.musica_min + min)
            if (enchufado) n = n.copy(cargando_min = n.cargando_min + min)
            if (nivel != null) n = n.copy(bateria_suma = n.bateria_suma + nivel, bateria_min = minOf(n.bateria_min ?: 100, nivel))
            if (luz != null) {
                val s = n.luz_suma.toMutableList().also { while (it.size < 24) it.add(0.0) }
                val k = n.luz_n.toMutableList().also { while (it.size < 24) it.add(0) }
                s[hora] = s[hora] + luz; k[hora] = k[hora] + 1
                n = n.copy(luz_suma = s, luz_n = k, luz_max = maxOf(n.luz_max ?: 0.0, luz), luz_alta_min = n.luz_alta_min + if (luz >= 1000) min else 0)
            }
            n
        }
    }
}
