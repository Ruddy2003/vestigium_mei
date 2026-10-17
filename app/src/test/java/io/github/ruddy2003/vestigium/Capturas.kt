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
import io.github.ruddy2003.vestigium.ui.fondoTema
import org.junit.Rule
import org.junit.Test

/** Capturas de una pantalla de muestra en cada tema, para revisar el diseño sin teléfono. */
class Capturas {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, theme = "android:Theme.Material.NoActionBar", showSystemUi = false, useDeviceResolution = true)

    /** Fondo del tema con su textura, como en la app. */
    @Composable
    private fun Lienzo(contenido: @Composable () -> Unit) {
        TemaVestigium {
            androidx.compose.material3.Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                val registro = object : androidx.activity.result.ActivityResultRegistryOwner {
                    override val activityResultRegistry = object : androidx.activity.result.ActivityResultRegistry() {
                        override fun <I, O> onLaunch(requestCode: Int, contract: androidx.activity.result.contract.ActivityResultContract<I, O>, input: I, options: androidx.core.app.ActivityOptionsCompat?) {}
                    }
                }
                androidx.compose.runtime.CompositionLocalProvider(androidx.activity.compose.LocalActivityResultRegistryOwner provides registro) {
                    Box(Modifier.fillMaxSize().fondoTema(io.github.ruddy2003.vestigium.ui.Estilo.tema, MaterialTheme.colorScheme)) { contenido() }
                }
            }
        }
    }

    private fun config(tema: TemaId, oscuro: Boolean) =
        Config(tema = tema.id, modo = if (oscuro) "oscuro" else "claro", animaciones = false, bienvenida_vista = true, widget_cifra = "racha")

    private fun captura(tema: TemaId, oscuro: Boolean, alto: Int = 2400, contenido: @Composable () -> Unit = { Muestra() }) {
        Repo.configEnMemoria(config(tema, oscuro))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot { Lienzo(contenido) }
    }

    @Test fun romanoClaro() = captura(TemaId.ROMANO, false)
    @Test fun romanoOscuro() = captura(TemaId.ROMANO, true)
    @Test fun ciber() = captura(TemaId.CIBER, true)
    @Test fun helenoClaro() = captura(TemaId.HELENO, false)
    @Test fun helenoOscuro() = captura(TemaId.HELENO, true)
    @Test fun medievalClaro() = captura(TemaId.MEDIEVAL, false)
    @Test fun medievalOscuro() = captura(TemaId.MEDIEVAL, true)
    @Test fun distopiaClaro() = captura(TemaId.DISTOPIA, false)
    @Test fun distopiaOscuro() = captura(TemaId.DISTOPIA, true)
    @Test fun refugioClaro() = captura(TemaId.REFUGIO, false)
    @Test fun refugioOscuro() = captura(TemaId.REFUGIO, true)
    @Test fun ordenClaro() = captura(TemaId.ORDEN, false)
    @Test fun ordenOscuro() = captura(TemaId.ORDEN, true)
    @Test fun laboratorioClaro() = captura(TemaId.LABORATORIO, false)
    @Test fun laboratorioOscuro() = captura(TemaId.LABORATORIO, true)
    @Test fun colonosClaro() = captura(TemaId.COLONOS, false)
    @Test fun colonosOscuro() = captura(TemaId.COLONOS, true)

    private fun dia(tema: TemaId, oscuro: Boolean) {
        Ejemplo.cargar()
        Repo.configEnMemoria(config(tema, oscuro))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = 6200, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot {
            Lienzo {
                // Las pantallas reales piden lanzadores de actividades; en la captura basta uno vacío.
                val registro = object : androidx.activity.result.ActivityResultRegistryOwner {
                    override val activityResultRegistry = object : androidx.activity.result.ActivityResultRegistry() {
                        override fun <I, O> onLaunch(requestCode: Int, contract: androidx.activity.result.contract.ActivityResultContract<I, O>, input: I, options: androidx.core.app.ActivityOptionsCompat?) {}
                    }
                }
                androidx.compose.runtime.CompositionLocalProvider(androidx.activity.compose.LocalActivityResultRegistryOwner provides registro) {
                    io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(io.github.ruddy2003.vestigium.ui.Nav(ir = {}, atras = {}))
                }
            }
        }
    }
    @Test fun diaRomano() = dia(TemaId.ROMANO, false)
    @Test fun diaCiber() = dia(TemaId.CIBER, true)
    @Test fun diaHeleno() = dia(TemaId.HELENO, true)
    @Test fun diaMedieval() = dia(TemaId.MEDIEVAL, false)
    @Test fun diaDistopia() = dia(TemaId.DISTOPIA, false)
    @Test fun diaRefugio() = dia(TemaId.REFUGIO, true)
    @Test fun diaOrden() = dia(TemaId.ORDEN, true)
    @Test fun diaLaboratorio() = dia(TemaId.LABORATORIO, false)
    @Test fun diaColonos() = dia(TemaId.COLONOS, true)

    @Test fun semanaHeleno() {
        Ejemplo.cargar()
        Repo.configEnMemoria(config(TemaId.HELENO, false))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = 3600, nightMode = NightMode.NOTNIGHT))
        paparazzi.snapshot { Lienzo { io.github.ruddy2003.vestigium.ui.pantallas.PantallaSemana(io.github.ruddy2003.vestigium.ui.Nav(ir = {}, atras = {})) } }
    }

    /** Los widgets se arman con RemoteViews y se pegan en una columna para verlos con cada tema. */
    private fun widgets(tema: TemaId) {
        Ejemplo.cargar()
        Repo.configEnMemoria(config(tema, true))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = 2600, nightMode = NightMode.NOTNIGHT))
        val ctx = paparazzi.context
        val col = android.widget.LinearLayout(ctx).apply { orientation = android.widget.LinearLayout.VERTICAL; setBackgroundColor(0xFF5A6B7A.toInt()); setPadding(30, 30, 30, 30) }
        fun agregar(rv: android.widget.RemoteViews, alto: Int) {
            val v = rv.apply(ctx, col)
            col.addView(v, android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, alto).apply { bottomMargin = 24 })
        }
        val W = io.github.ruddy2003.vestigium.widget.Widgets
        agregar(W.vistaAnimo(ctx), 330)
        agregar(W.vistaSueno(ctx), 330)
        agregar(W.vistaAnillos(ctx), 640)
        agregar(W.vistaCifra(ctx), 360)
        agregar(W.vistaHoy(ctx), 520)
        paparazzi.snapshot(col)
    }
    @Test fun widgetsRomano() = widgets(TemaId.ROMANO)
    @Test fun widgetsCiber() = widgets(TemaId.CIBER)
    @Test fun widgetsHeleno() = widgets(TemaId.HELENO)
    @Test fun widgetsMedieval() = widgets(TemaId.MEDIEVAL)
    @Test fun widgetsDistopia() = widgets(TemaId.DISTOPIA)
    @Test fun widgetsRefugio() = widgets(TemaId.REFUGIO)
    @Test fun widgetsOrden() = widgets(TemaId.ORDEN)
    @Test fun widgetsLaboratorio() = widgets(TemaId.LABORATORIO)
    @Test fun widgetsColonos() = widgets(TemaId.COLONOS)

    private fun pantalla(tema: TemaId, oscuro: Boolean, alto: Int, cont: @Composable () -> Unit) {
        Ejemplo.cargar()
        Repo.configEnMemoria(config(tema, oscuro).copy(habitos = listOf(io.github.ruddy2003.vestigium.data.Habito("h1", "Leer 20 min"), io.github.ruddy2003.vestigium.data.Habito("h2", "Sin pantalla después de las 23"))))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot { Lienzo(cont) }
    }
    private val navVacia = io.github.ruddy2003.vestigium.ui.Nav(ir = {}, atras = {})
    @Test fun nocheMedieval() = pantalla(TemaId.MEDIEVAL, false, 5200) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(navVacia) }
    @Test fun diariosColonos() = pantalla(TemaId.COLONOS, false, 3400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
    @Test fun entenderRomano() = pantalla(TemaId.ROMANO, true, 2600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaExplorar(navVacia) }
    @Test fun masLaboratorio() = pantalla(TemaId.LABORATORIO, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
    @Test fun aparienciaOrden() = pantalla(TemaId.ORDEN, true, 3600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaAjustes(navVacia, "apariencia") }
    /** La barra inferior en cada tema, con «Día» activa. */
    @Test fun barras() = captura(TemaId.ROMANO, false, alto = 1700) {
        Column(Modifier.padding(vertical = 16.dp)) {
            TemaId.entries.forEach { t ->
                androidx.compose.material3.MaterialTheme(colorScheme = io.github.ruddy2003.vestigium.ui.esquemaDe(t, t.soloOscuro), typography = io.github.ruddy2003.vestigium.ui.tipoDe(t, t.soloOscuro)) {
                    Box(Modifier.padding(bottom = 10.dp)) { io.github.ruddy2003.vestigium.ui.BarraPestanas(if (t.ordinal % 2 == 0) "dia" else "diario", t, {}, {}) }
                }
            }
        }
    }
    @Test fun entradaDistopia() = pantalla(TemaId.DISTOPIA, true, 2400) {
        io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.entrada = null
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaEntrada(navVacia)
    }
    @Test fun huellaRefugio() = pantalla(TemaId.REFUGIO, true, 2400) { io.github.ruddy2003.vestigium.ui.VistaHuella(0.45f, "") }
    @Test fun huellaRomano() = pantalla(TemaId.ROMANO, false, 2400) { io.github.ruddy2003.vestigium.ui.VistaHuella(1f, io.github.ruddy2003.vestigium.data.Fechas.diaLogico()) }

    /** Los íconos de la barra inferior en cada tema. */
    @Test fun iconos() = captura(TemaId.ROMANO, false, alto = 1500) {
        Column(Modifier.padding(16.dp)) {
            TemaId.entries.forEach { t ->
                val e = io.github.ruddy2003.vestigium.ui.esquemaDe(t, t.soloOscuro)
                Row(Modifier.fillMaxWidth().background(e.surface).padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides e.primary) {
                        listOf("dia", "momentos", "gastos", "explorar", "mas").forEach { io.github.ruddy2003.vestigium.ui.IconoPestana(it, t) }
                    }
                }
            }
        }
    }

    /** Los nueve temas en claro y en oscuro, con sus emblemas grandes. */
    @Test fun selectorTemas() = captura(TemaId.ROMANO, false, alto = 2600) {
        Column(Modifier.padding(16.dp)) {
            listOf(false, true).forEach { o ->
                TemaId.entries.chunked(3).forEach { fila ->
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { VistaPreviaTema(it, o, it == TemaId.ROMANO, Modifier.weight(1f)) {} }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TemaId.entries.forEach { t ->
                    val e = io.github.ruddy2003.vestigium.ui.esquemaDe(t, true)
                    Box(Modifier.weight(1f).background(e.background).padding(4.dp)) { io.github.ruddy2003.vestigium.ui.Emblema(t, 30.dp, e, animado = false) }
                }
            }
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
        val notas = listOf(
            "Día normal con @ana. Avancé el capítulo dos de la #tesis y en la tarde fui a escalar; los bloques del muro nuevo están más duros de lo que parecen.",
            "Me desperté tarde y me costó arrancar. Comida en casa y una siesta larga. En la noche leí un rato.",
            "Escribí 300 palabras del #protocolo y por fin entendí cómo ordenar el marco teórico.",
            "Salí a correr temprano, hacía frío. Café con @luis en el centro; hablamos de trabajo y de planes para el año que viene.",
        )
        val dias = fechas.drop(1).associateWith { f -> io.github.ruddy2003.vestigium.data.Dia(fecha = f, valencia = 3 + r.nextInt(4), nota = notas[r.nextInt(notas.size)], sueno_calidad = 3, mejor_momento = "el café de la tarde") } +
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
