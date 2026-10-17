package io.github.ruddy2003.vestigium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TemaVestigium
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.VistaPreviaTema
import io.github.ruddy2003.vestigium.ui.esOscuro
import org.junit.Rule
import org.junit.Test

/** Capturas de una pantalla de muestra en cada tema, para revisar el diseño sin teléfono. */
class Capturas {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, theme = "android:Theme.Material.NoActionBar", showSystemUi = false, useDeviceResolution = true)

    private fun captura(tema: TemaId, oscuro: Boolean, contenido: @Composable () -> Unit = { Muestra() }) {
        Repo.configEnMemoria(Config(tema = tema.id))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot { TemaVestigium { androidx.compose.material3.Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { contenido() } } }
    }

    @Test fun romanoClaro() = captura(TemaId.ROMANO, false)
    @Test fun romanoOscuro() = captura(TemaId.ROMANO, true)
    @Test fun ciber() = captura(TemaId.CIBER, true)
    @Test fun helenoClaro() = captura(TemaId.HELENO, false)
    @Test fun helenoOscuro() = captura(TemaId.HELENO, true)
    private fun dia(tema: TemaId, oscuro: Boolean) {
        Ejemplo.cargar()
        Repo.configEnMemoria(Config(tema = tema.id, bienvenida_vista = true))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = 6200, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot {
            TemaVestigium { androidx.compose.material3.Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                // Las pantallas reales piden lanzadores de actividades; en la captura basta uno vacío.
                val registro = object : androidx.activity.result.ActivityResultRegistryOwner {
                    override val activityResultRegistry = object : androidx.activity.result.ActivityResultRegistry() {
                        override fun <I, O> onLaunch(requestCode: Int, contract: androidx.activity.result.contract.ActivityResultContract<I, O>, input: I, options: androidx.core.app.ActivityOptionsCompat?) {}
                    }
                }
                androidx.compose.runtime.CompositionLocalProvider(androidx.activity.compose.LocalActivityResultRegistryOwner provides registro) {
                    io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(io.github.ruddy2003.vestigium.ui.Nav(ir = {}, atras = {}))
                }
            } }
        }
    }
    @Test fun diaRomano() = dia(TemaId.ROMANO, false)
    @Test fun diaCiber() = dia(TemaId.CIBER, true)
    @Test fun diaHeleno() = dia(TemaId.HELENO, true)

    @Test fun selectorTemas() = captura(TemaId.ROMANO, false) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TemaId.entries.forEach { VistaPreviaTema(it, false, it == TemaId.ROMANO, Modifier.weight(1f)) {} }
        }
    }
}

@Composable
private fun Muestra() {
    Hoja("Día", subtitulo = null) {
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("7.4 h", "sueño", Modifier.weight(1f)); Cifra("9,812", "pasos", Modifier.weight(1f)); Cifra("$245", "gasto", Modifier.weight(1f))
        }
        Seccion("Sueño", "Lo que recuerdas al despertar.", numero = 1) {
            Tarjeta {
                Etiqueta("Calidad")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf("1", "2", "3", "4", "5").forEach { Ficha(it, it == "4") {} } }
                Interruptor("Recordé un sueño", true, "Se guarda con su valencia y temas.") {}
                Clave("somnus", "7 h 24 · profundo 1 h 32")
                Clave("hrv", "48 ms")
            }
        }
        Seccion("Las relaciones más marcadas", numero = 2) {
            Tarjeta {
                Text("Horas de sueño  ↔  Valencia del día", style = MaterialTheme.typography.bodyMedium)
                Ayuda("r = +0.42 · 118 días · moderada")
                val o = esOscuro()
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) { for (k in 0..10) Box(Modifier.weight(1f).padding(1.dp).background(Tonos.divergente(k / 10f, o)).padding(6.dp)) }
                Row(Modifier.fillMaxWidth().padding(top = 4.dp)) { for (k in 0..10) Box(Modifier.weight(1f).padding(1.dp).background(Tonos.calor(k / 10f, o)).padding(6.dp)) }
            }
        }
        Seccion("Diario", numero = 3) {
            Text("Hoy fui con @ana al Colegio y avancé el capítulo de la tesis. #tesis", style = MaterialTheme.typography.bodyLarge)
            Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonPrincipal("Guardar") {}; BotonSecundario("cancelar") {}
            }
        }
    }
}

/** Cinco semanas de datos inventados para ver el tablero y la línea del tiempo. */
object Ejemplo {
    fun cargar() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        val r = java.util.Random(7)
        val fechas = (0..35).map { io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -it.toLong()) }
        fun iso(f: String, h: Int, m: Int = 0) = java.time.LocalDate.parse(f).atTime(h, m).atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime().withNano(0).toString()
        val dias = fechas.drop(1).associateWith { f -> io.github.ruddy2003.vestigium.data.Dia(fecha = f, valencia = 3 + r.nextInt(4), nota = "Día normal con @ana.", sueno_calidad = 3) } +
            (hoy to io.github.ruddy2003.vestigium.data.Dia(fecha = hoy, sueno_calidad = null, valencia = null))
        val salud = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.SaludDia(fecha = f, sueno_min = 380 + r.nextInt(90), pasos = 6000L + r.nextInt(6000),
            sueno_inicio = iso(io.github.ruddy2003.vestigium.data.Fechas.sumar(f, -1), 23, 40), sueno_fin = iso(f, 7, 10), hrv_rmssd = 40.0 + r.nextInt(15)) }
        val pantalla = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.PantallaDia(fecha = f, total_min = 180 + r.nextInt(120), desbloqueos = 60 + r.nextInt(40)) }
        val gastos = fechas.flatMap { f -> (0 until r.nextInt(3)).map { k -> io.github.ruddy2003.vestigium.data.Gasto("g$f$k", f, "%02d:15".format(9 + k * 5), 50.0 + r.nextInt(300), "comida") } }
        val notifs = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.NotifDia(fecha = f, total = 120, por_hora = List(24) { h -> if (h in 8..23) r.nextInt(14) else 0 }) }
        val entorno = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.EntornoDia(fecha = f, amanecer = "07:12", atardecer = "18:58", t_max = 24.0, t_min = 11.0) }
        val nutricion = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.NutricionDia(fecha = f, valores = mapOf("kcal" to 1900.0 + r.nextInt(700), "proteina_g" to 110.0 + r.nextInt(40))) }
        val lugares = listOf(io.github.ruddy2003.vestigium.data.Lugar("casa", "Casa", 22.15, -100.98), io.github.ruddy2003.vestigium.data.Lugar("col", "Colegio", 22.16, -100.99))
        val visitas = listOf(
            io.github.ruddy2003.vestigium.data.Visita("v1", "casa", iso(hoy, 0, 0), iso(hoy, 8, 40)),
            io.github.ruddy2003.vestigium.data.Visita("v2", "col", iso(hoy, 9, 5), iso(hoy, 14, 30)),
            io.github.ruddy2003.vestigium.data.Visita("v3", "casa", iso(hoy, 15, 0), null),
        )
        val momentos = listOf(
            io.github.ruddy2003.vestigium.data.Momento("m1", iso(hoy, 11, 47), hoy, "muestreo", valencia = 5),
            io.github.ruddy2003.vestigium.data.Momento("m2", iso(hoy, 16, 20), hoy, "preocupacion", texto = "entrega", valencia = 3),
        )
        val sesiones = listOf(io.github.ruddy2003.vestigium.data.Sesion("s1", hoy, iso(hoy, 18, 30), iso(hoy, 19, 40), 1, "Pesas", min = 70))
        Repo.ejemploEnMemoria(dias, salud, pantalla, gastos, momentos, notifs, entorno, nutricion, lugares, visitas, sesiones)
    }
}
