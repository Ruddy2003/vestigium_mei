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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    /** Si el teléfono tiene barómetro (el Pixel sí). */
    fun tieneBarometro(ctx: Context): Boolean = ctx.getSystemService(SensorManager::class.java)?.getDefaultSensor(Sensor.TYPE_PRESSURE) != null

    /**
     * Lecturas válidas del barómetro (P-33): el rango de medida de los sensores de teléfono más comunes (300 a 1 100 hPa en el
     * BMP280 de Bosch; 260 a 1 260 hPa en el LPS22HB de ST). Antes era 850 a 1 090 y por encima de unos 1 450 m se perdía todo.
     */
    val RANGO_HPA = 300.0..1100.0

    /** Una lectura del barómetro (hPa), o null si no hay sensor. */
    suspend fun presion(ctx: Context, esperaMs: Long = 1500): Double? {
        val sm = ctx.getSystemService(SensorManager::class.java) ?: return null
        val sensor = sm.getDefaultSensor(Sensor.TYPE_PRESSURE) ?: return null
        return withTimeoutOrNull(esperaMs) {
            suspendCancellableCoroutine { cont ->
                val l = object : SensorEventListener {
                    override fun onSensorChanged(e: SensorEvent) { sm.unregisterListener(this); if (cont.isActive) cont.resume(e.values[0].toDouble()) }
                    override fun onAccuracyChanged(s: Sensor?, a: Int) {}
                }
                sm.registerListener(l, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                cont.invokeOnCancellation { sm.unregisterListener(l) }
            }
        }?.takeIf { it in RANGO_HPA }
    }

    /**
     * Cambio de presión en unas tres horas: la lectura de ahora menos la más cercana a hace tres horas (entre 2.5 y 3.5 h).
     * Las lecturas de las últimas cuatro horas se guardan en las preferencias como «ms:hPa;…».
     */
    private fun cambio3h(p: android.content.SharedPreferences, ahora: Long, hpa: Double): Double? {
        val previas = p.getString("presiones", "").orEmpty().split(";").mapNotNull { x ->
            val (a, b) = x.split(":").takeIf { it.size == 2 } ?: return@mapNotNull null
            (a.toLongOrNull() ?: return@mapNotNull null) to (b.toDoubleOrNull() ?: return@mapNotNull null)
        }.filter { ahora - it.first < 4 * 3_600_000L }
        p.edit().putString("presiones", (previas + (ahora to hpa)).joinToString(";") { "${it.first}:${"%.2f".format(java.util.Locale.US, it.second)}" }).apply()
        val objetivo = ahora - 3 * 3_600_000L
        val ref = previas.filter { ahora - it.first in (150 * 60_000L)..(210 * 60_000L) }.minByOrNull { kotlin.math.abs(it.first - objetivo) } ?: return null
        return hpa - ref.second
    }

    /** Lo que se sabe del wifi en la muestra: conectado a una red, sin wifi, o sin dato (no se pudo saber; H-3-7). */
    private sealed interface Wifi {
        data class Red(val nombre: String) : Wifi
        object Sin : Wifi
        object SinDato : Wifi
    }

    /**
     * Si hay wifi. El nombre de la red puede venir oculto (sin permiso de ubicación, o en Android reciente aunque estés
     * conectado), así que la conexión se pregunta aparte a ConnectivityManager: si alguna red activa es wifi, cuenta como wifi
     * aunque sea sin nombre. «Sin wifi» solo cuando el wifi está apagado o el sistema dice que no hay red wifi; si no se pudo
     * saber, es «sin dato» y no se suma a sin_wifi_min.
     */
    @Suppress("DEPRECATION")
    private fun wifi(ctx: Context): Wifi {
        val wm = ctx.applicationContext.getSystemService(WifiManager::class.java)
        if (runCatching { wm?.isWifiEnabled == false }.getOrDefault(false)) return Wifi.Sin
        val cm = ctx.applicationContext.getSystemService(ConnectivityManager::class.java)
        val enWifi: Boolean? = runCatching { cm?.allNetworks?.any { cm.getNetworkCapabilities(it)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true } }.getOrNull()
        val info = runCatching { wm?.connectionInfo }.getOrNull()
        val conectado = info != null && !(info.networkId == -1 && info.ipAddress == 0)
        if (enWifi == true || conectado) {
            val s = info?.ssid?.trim('"')
            return Wifi.Red(if (s.isNullOrBlank() || s == "<unknown ssid>") "wifi (sin nombre)" else s)
        }
        return if (enWifi == false) Wifi.Sin else Wifi.SinDato
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
        val hora = Fechas.local(ahora).hour

        val red = runCatching { wifi(ctx) }.getOrDefault(Wifi.SinDato)
        val sonidos = runCatching { audio(ctx) }.getOrDefault(emptyList())
        val musica = runCatching { ctx.getSystemService(AudioManager::class.java)?.isMusicActive == true }.getOrDefault(false)
        val bat = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val enchufado = (bat?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0) != 0
        val nivel = bat?.let { val l = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1); val e = it.getIntExtra(BatteryManager.EXTRA_SCALE, 100); if (l >= 0 && e > 0) l * 100 / e else null }
        val luz = if (Repo.config.medir_luz) runCatching { lux(ctx) }.getOrNull() else null
        val hpa = if (Repo.config.barometro) runCatching { presion(ctx) }.getOrNull() else null
        val cambio = hpa?.let { cambio3h(p, ahora, it) }

        // Carga de noche: si se conectó entre las 20:00 y las 4:00 y duró más de 3 horas, marca aproximada de cuándo dormiste.
        val desde = p.getLong("carga_desde", 0L)
        if (enchufado && desde == 0L) p.edit().putLong("carga_desde", ahora).apply()
        if (!enchufado && desde > 0L) {
            p.edit().remove("carga_desde").apply()
            val h = Fechas.local(desde).hour
            if ((h >= 20 || h < 4) && ahora - desde > 3 * 3_600_000L && ahora - desde < 16 * 3_600_000L) {
                Repo.cambiarConexion(Fechas.diaLogicoDeMillis(desde)) { it.copy(carga_noche_inicio = Fechas.isoDeMillis(desde), carga_noche_fin = Fechas.isoDeMillis(ahora)) }
            }
        }

        Repo.cambiarConexion(fecha) { c ->
            var n = c.copy(muestras = c.muestras + 1)
            n = when (red) {
                is Wifi.Red -> n.copy(wifi_min = suma(n.wifi_min, red.nombre, min))
                Wifi.Sin -> n.copy(sin_wifi_min = n.sin_wifi_min + min)
                Wifi.SinDato -> n.copy(wifi_sin_dato_min = n.wifi_sin_dato_min + min)
            }
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
            if (hpa != null) {
                n = n.copy(presion_suma = n.presion_suma + hpa, presion_n = n.presion_n + 1, presion_min = minOf(n.presion_min ?: hpa, hpa), presion_max = maxOf(n.presion_max ?: hpa, hpa))
                // Del día se guarda el cambio a tres horas más grande (con su signo): las bajadas bruscas son las que se notan.
                if (cambio != null && kotlin.math.abs(cambio) > kotlin.math.abs(n.cambio_presion_3h ?: 0.0)) n = n.copy(cambio_presion_3h = Math.round(cambio * 100) / 100.0)
            }
            n
        }
    }
}
