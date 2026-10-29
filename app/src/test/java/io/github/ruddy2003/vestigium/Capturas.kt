package io.github.ruddy2003.vestigium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.CapaDeshacer
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

    /** [nombre]: para varias capturas en una misma prueba (cada una con su nombre). */
    private fun captura(tema: TemaId, oscuro: Boolean, alto: Int = 2400, nombre: String? = null, contenido: @Composable () -> Unit = { Muestra() }) {
        Repo.configEnMemoria(config(tema, oscuro))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot(nombre) { Lienzo(contenido) }
    }

    /**
     * Una hoja con muchos temas a la vez. Se reparte en partes de [porParte] temas para que ninguna captura pase de unos
     * 5000 px (memoria del CI), y [alto] calcula el alto de cada parte según cuántos temas lleva: crece sola al agregar temas.
     * Con una sola parte, la captura conserva su nombre de siempre.
     */
    private fun hojaTemas(temas: List<TemaId> = TemaId.entries, porParte: Int, alto: (Int) -> Int, base: TemaId = TemaId.ROMANO, oscuro: Boolean = false,
                          contenido: @Composable (List<TemaId>) -> Unit) {
        val partes = temas.chunked(porParte)
        partes.forEachIndexed { i, parte -> captura(base, oscuro, alto(parte.size), if (partes.size > 1) "parte${i + 1}" else null) { contenido(parte) } }
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
    @Test fun napoleonClaro() = captura(TemaId.NAPOLEON, false)
    @Test fun napoleonOscuro() = captura(TemaId.NAPOLEON, true)
    @Test fun botanicoClaro() = captura(TemaId.BOTANICO, false)
    @Test fun botanicoOscuro() = captura(TemaId.BOTANICO, true)
    @Test fun navidadClaro() = captura(TemaId.NAVIDAD, false)
    @Test fun navidadOscuro() = captura(TemaId.NAVIDAD, true)
    @Test fun sedaClaro() = captura(TemaId.SEDA, false)
    @Test fun sedaOscuro() = captura(TemaId.SEDA, true)
    @Test fun urssClaro() = captura(TemaId.URSS, false)
    @Test fun urssOscuro() = captura(TemaId.URSS, true)
    @Test fun goticoClaro() = captura(TemaId.GOTICO, false)
    @Test fun goticoOscuro() = captura(TemaId.GOTICO, true)
    @Test fun espacioClaro() = captura(TemaId.ESPACIO, false)
    @Test fun espacioOscuro() = captura(TemaId.ESPACIO, true)
    @Test fun andalusClaro() = captura(TemaId.ANDALUS, false)
    @Test fun andalusOscuro() = captura(TemaId.ANDALUS, true)
    @Test fun mongolClaro() = captura(TemaId.MONGOL, false)
    @Test fun mongolOscuro() = captura(TemaId.MONGOL, true)
    @Test fun renacimientoClaro() = captura(TemaId.RENACIMIENTO, false)
    @Test fun renacimientoOscuro() = captura(TemaId.RENACIMIENTO, true)

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
    @Test fun diaNapoleon() = dia(TemaId.NAPOLEON, false)
    @Test fun diaBotanico() = dia(TemaId.BOTANICO, false)
    @Test fun diaGotico() = dia(TemaId.GOTICO, true)
    @Test fun diaEspacio() = dia(TemaId.ESPACIO, true)
    @Test fun diaAndalus() = dia(TemaId.ANDALUS, false)
    @Test fun diaMongol() = dia(TemaId.MONGOL, false)
    @Test fun diaRenacimiento() = dia(TemaId.RENACIMIENTO, false)
    @Test fun diaNavidad() = dia(TemaId.NAVIDAD, false)
    @Test fun diaSeda() = dia(TemaId.SEDA, true)
    @Test fun diaUrss() = dia(TemaId.URSS, false)

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
    @Test fun widgetsNapoleon() = widgets(TemaId.NAPOLEON)
    @Test fun widgetsBotanico() = widgets(TemaId.BOTANICO)
    @Test fun widgetsGotico() = widgets(TemaId.GOTICO)
    @Test fun widgetsEspacio() = widgets(TemaId.ESPACIO)
    @Test fun widgetsAndalus() = widgets(TemaId.ANDALUS)
    @Test fun widgetsMongol() = widgets(TemaId.MONGOL)
    @Test fun widgetsRenacimiento() = widgets(TemaId.RENACIMIENTO)
    @Test fun widgetsNavidad() = widgets(TemaId.NAVIDAD)
    @Test fun widgetsSeda() = widgets(TemaId.SEDA)
    @Test fun widgetsUrss() = widgets(TemaId.URSS)
    @Test fun widgetsRevolucion() = widgets(TemaId.REVOLUCION)

    private fun pantalla(tema: TemaId, oscuro: Boolean, alto: Int, nombre: String? = null, cont: @Composable () -> Unit) {
        Ejemplo.cargar()
        Repo.configEnMemoria(config(tema, oscuro).copy(habitos = listOf(io.github.ruddy2003.vestigium.data.Habito("h1", "Leer 20 min"), io.github.ruddy2003.vestigium.data.Habito("h2", "Sin pantalla después de las 23"))))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot(nombre) { Lienzo(cont) }
    }
    private val navVacia = io.github.ruddy2003.vestigium.ui.Nav(ir = {}, atras = {})
    @Test fun nocheMedieval() = pantalla(TemaId.MEDIEVAL, false, 5200) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(navVacia) }
    @Test fun diariosColonos() = pantalla(TemaId.COLONOS, false, 3400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
    @Test fun entenderRomano() = pantalla(TemaId.ROMANO, true, 2600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaExplorar(navVacia) }
    @Test fun masLaboratorio() = pantalla(TemaId.LABORATORIO, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
    @Test fun aparienciaOrden() = pantalla(TemaId.ORDEN, true, 3600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaAjustes(navVacia, "apariencia") }
    /** La barra inferior en cada tema, con «Día» activa. */
    @Test fun barras() = hojaTemas(porParte = 28, alto = { 100 + it * 157 }) { temas ->
        Column(Modifier.padding(vertical = 16.dp)) {
            temas.forEach { t ->
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
    @Test fun iconos() = hojaTemas(porParte = 30, alto = { 100 + it * 129 }) { temas ->
        Column(Modifier.padding(16.dp)) {
            temas.forEach { t ->
                val e = io.github.ruddy2003.vestigium.ui.esquemaDe(t, t.soloOscuro)
                Row(Modifier.fillMaxWidth().background(e.surface).padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides e.primary) {
                        listOf("dia", "diario", "registrar", "vida", "entender", "mas", "ajustes").forEach { io.github.ruddy2003.vestigium.ui.IconoPestana(it, t) }
                    }
                }
            }
        }
    }

    /** Los nueve temas en claro y en oscuro, con sus emblemas grandes. */
    @Test fun selectorTemas() = hojaTemas(porParte = 21, alto = { 100 + 2 * ((it + 2) / 3) * 370 + (it + 6) / 7 * 130 }) { temas ->
        Column(Modifier.padding(16.dp)) {
            listOf(false, true).forEach { o ->
                temas.chunked(3).forEach { fila ->
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { VistaPreviaTema(it, o, it == TemaId.ROMANO, Modifier.weight(1f)) {} }
                        repeat(3 - fila.size) { Box(Modifier.weight(1f)) }
                    }
                }
            }
            temas.chunked(7).forEach { fila -> Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { t ->
                    val e = io.github.ruddy2003.vestigium.ui.esquemaDe(t, true)
                    Box(Modifier.weight(1f).background(e.background).padding(4.dp)) { io.github.ruddy2003.vestigium.ui.Emblema(t, 40.dp, e, animado = false) }
                }
            } }
        }
    }

    // ------------------------------------------------------------ huellas, diario y biblioteca con el diseño de cada tema

    /** Los sellos de huella de todos los temas. */
    @Test fun sellos() = hojaTemas(porParte = 40, alto = { 100 + (it + 3) / 4 * 417 }) { temas ->
        Column(Modifier.padding(12.dp)) {
            temas.chunked(4).forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                    fila.forEach { t -> io.github.ruddy2003.vestigium.ui.SelloHuella(Modifier.weight(1f).aspectRatio(1f), t) }
                    repeat(4 - fila.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
    }

    private val TEXTO = "Hoy fue un día largo pero bueno. En la mañana salí a caminar con @ana por el parque y hablamos de los planes para el viaje. " +
        "Después trabajé casi toda la tarde en el proyecto; avancé más de lo que esperaba. En la noche leí un rato antes de dormir. #lectura #trabajo"

    private fun objetos(temas: List<TemaId>, nombre: String) = captura(temas.first(), false, alto = 2800, nombre = nombre) {
        Column(Modifier.padding(16.dp)) {
            temas.forEachIndexed { i, t -> io.github.ruddy2003.vestigium.ui.ObjetoDiario("2026-10-16", if (i == 1) "" else TEXTO, Modifier.padding(bottom = 14.dp), t) {} }
        }
    }
    /** El diario cerrado en todos los temas, siete por captura. */
    @Test fun objetos() = TemaId.entries.chunked(7).forEachIndexed { i, temas -> objetos(temas, "parte${i + 1}") }

    private fun biblioteca(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "diario") }
    @Test fun bibliotecaMedieval() = biblioteca(TemaId.MEDIEVAL, false)
    @Test fun bibliotecaRomano() = biblioteca(TemaId.ROMANO, true)
    @Test fun bibliotecaCiber() = biblioteca(TemaId.CIBER, true)
    @Test fun bibliotecaDistopia() = biblioteca(TemaId.DISTOPIA, false)
    @Test fun bibliotecaNapoleon() = biblioteca(TemaId.NAPOLEON, false)
    @Test fun bibliotecaRefugio() = biblioteca(TemaId.REFUGIO, true)
    @Test fun bibliotecaGotico() = biblioteca(TemaId.GOTICO, true)
    @Test fun bibliotecaMongol() = biblioteca(TemaId.MONGOL, false)
    @Test fun bibliotecaEspacio() = biblioteca(TemaId.ESPACIO, true)

    private fun lectura(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 2400) {
        io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.entrada = null
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaEntrada(navVacia)
    }
    @Test fun lecturaMedieval() = lectura(TemaId.MEDIEVAL, false)
    @Test fun lecturaCiber() = lectura(TemaId.CIBER, true)
    @Test fun lecturaRomano() = lectura(TemaId.ROMANO, false)
    @Test fun lecturaUrss() = lectura(TemaId.URSS, false)
    @Test fun lecturaNavidad() = lectura(TemaId.NAVIDAD, false)
    @Test fun lecturaSeda() = lectura(TemaId.SEDA, true)
    @Test fun lecturaRefugio() = lectura(TemaId.REFUGIO, true)
    @Test fun lecturaLaboratorio() = lectura(TemaId.LABORATORIO, false)
    @Test fun lecturaBotanico() = lectura(TemaId.BOTANICO, false)
    @Test fun lecturaGotico() = lectura(TemaId.GOTICO, false)
    @Test fun lecturaEspacio() = lectura(TemaId.ESPACIO, true)
    @Test fun lecturaAndalus() = lectura(TemaId.ANDALUS, false)
    @Test fun lecturaMongol() = lectura(TemaId.MONGOL, false)
    @Test fun lecturaRenacimiento() = lectura(TemaId.RENACIMIENTO, false)

    private fun escritor(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 2400) {
        io.github.ruddy2003.vestigium.ui.EscritorTema("2026-10-16", TEXTO, {}, {}, tema)
    }
    @Test fun escritorMedieval() = escritor(TemaId.MEDIEVAL, false)
    @Test fun escritorCiber() = escritor(TemaId.CIBER, true)
    @Test fun escritorColonos() = escritor(TemaId.COLONOS, false)
    @Test fun escritorBotanico() = escritor(TemaId.BOTANICO, false)
    @Test fun escritorGotico() = escritor(TemaId.GOTICO, true)
    @Test fun escritorRenacimiento() = escritor(TemaId.RENACIMIENTO, false)
    @Test fun gastosAndalus() = pantalla(TemaId.ANDALUS, false, 3800) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaGastos(navVacia) }
    @Test fun gastosEspacio() = pantalla(TemaId.ESPACIO, true, 3800) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaGastos(navVacia) }
    @Test fun frasesRenacimiento() = pantalla(TemaId.RENACIMIENTO, false, 2400) {
        Repo.configEnMemoria(Repo.config.copy(frases_guardadas = listOf(
            io.github.ruddy2003.vestigium.data.FraseGuardada("Así como una jornada bien empleada da un dulce dormir, una vida bien usada da un dulce morir.", "Siccome una giornata bene spesa dà lieto dormire, così una vita bene usata dà lieto morire.", "Leonardo da Vinci, Códice Trivulziano", "renacimiento", "2026-10-12"),
            io.github.ruddy2003.vestigium.data.FraseGuardada("Hoy, vence a tu yo de ayer.", "今日は昨日の我に勝ち", "Miyamoto Musashi, El libro de los cinco anillos", "orden", "2026-10-03"))))
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaFrasesGuardadas(navVacia)
    }
    @Test fun huellaGotico() = pantalla(TemaId.GOTICO, true, 2400) { io.github.ruddy2003.vestigium.ui.VistaHuella(1f, io.github.ruddy2003.vestigium.data.Fechas.diaLogico()) }
    @Test fun huellaEspacio() = pantalla(TemaId.ESPACIO, true, 2400) { io.github.ruddy2003.vestigium.ui.VistaHuella(0.6f, "") }
    @Test fun diarioNapoleon() = pantalla(TemaId.NAPOLEON, false, 4200) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
    @Test fun huellaMedieval() = pantalla(TemaId.MEDIEVAL, false, 2400) { io.github.ruddy2003.vestigium.ui.VistaHuella(0.5f, "") }
    @Test fun huellaNapoleon() = pantalla(TemaId.NAPOLEON, true, 2400) { io.github.ruddy2003.vestigium.ui.VistaHuella(1f, io.github.ruddy2003.vestigium.data.Fechas.diaLogico()) }

    // ------------------------------------------------------------ obras y relaciones

    private fun obrasEjemplo() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        fun d(k: Long) = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -k)
        Repo.obrasEnMemoria(listOf(
            io.github.ruddy2003.vestigium.data.Obra("o1", "libro", "Don Quijote de la Mancha", "Miguel de Cervantes", 1605, "en_curso", inicio = d(20), progreso = 412, total = 1056,
                agregado = d(30), dias = listOf(d(0), d(1), d(3)), citas = listOf(
                    io.github.ruddy2003.vestigium.data.Cita("c1", "La libertad, Sancho, es uno de los más preciosos dones que a los hombres dieron los cielos.", "II, 58", d(2)),
                    io.github.ruddy2003.vestigium.data.Cita("c2", "Cada uno es artífice de su ventura.", "II, 66", d(1), favorita = false))),
            io.github.ruddy2003.vestigium.data.Obra("o2", "serie", "Una serie larga", "Showrunner inventado", 2019, "en_curso", inicio = d(9), progreso = 14, total = 40, agregado = d(9), dias = listOf(d(1))),
            io.github.ruddy2003.vestigium.data.Obra("o3", "pelicula", "Una película de domingo", "Directora inventada", 2023, "terminado", inicio = d(5), fin = d(5), calificacion = 4, agregado = d(6)),
            io.github.ruddy2003.vestigium.data.Obra("o4", "juego", "Un juego de exploración", "Estudio inventado", 2021, "quiero", agregado = d(12)),
            io.github.ruddy2003.vestigium.data.Obra("o5", "articulo", "Sleep and affect: a daily diary study", "A. Autora; B. Autor", 2022, "en_curso", inicio = d(3), agregado = d(3),
                revista = "Journal of Sleep Research", doi = "10.1111/jsr.00000"),
            io.github.ruddy2003.vestigium.data.Obra("o6", "libro", "Pedro Páramo", "Juan Rulfo", 1955, "terminado", inicio = d(60), fin = d(41), calificacion = 5, agregado = d(70)),
        ))
    }
    private fun obras(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 4200) { obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaBiblioteca(navVacia) }
    @Test fun obrasBotanico() = obras(TemaId.BOTANICO, true)
    @Test fun obrasMedieval() = obras(TemaId.MEDIEVAL, false)
    @Test fun obrasCiber() = obras(TemaId.CIBER, true)
    @Test fun obraRenacimiento() = pantalla(TemaId.RENACIMIENTO, false, 5600) {
        obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.EdicionObra.id = "o1"
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaObra(navVacia)
    }
    @Test fun obraArticuloEspacio() = pantalla(TemaId.ESPACIO, true, 3000) {
        obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.EdicionObra.id = "o5"
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaObra(navVacia)
    }
    @Test fun buscarObraAndalus() = pantalla(TemaId.ANDALUS, false, 2000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaBuscarObra(navVacia) }
    @Test fun leidoHoyMongol() = pantalla(TemaId.MONGOL, false, 1400) {
        obrasEjemplo()
        Column(Modifier.padding(16.dp)) { io.github.ruddy2003.vestigium.ui.pantallas.ObrasDeHoy(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), navVacia) }
    }
    @Test fun frasesConCitas() = pantalla(TemaId.GOTICO, false, 2400) { obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaFrasesGuardadas(navVacia) }

    private fun relaciones(tema: TemaId, oscuro: Boolean, nombre: String? = null) = captura(tema, oscuro, alto = 1500, nombre = nombre) {
        fun V(id: String, n: String, g: String) = io.github.ruddy2003.vestigium.ciencia.Variable(id, n, g)
        Hoja("Relaciones", subtitulo = null) {
            Seccion("Las relaciones más marcadas") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    io.github.ruddy2003.vestigium.ui.TarjetaRelacion(V("pasos", "Pasos", "Cuerpo"), V("personas", "Personas distintas por llamada o SMS", "Social"), 0.62, "31 días · fuerte", false,
                        "También con «minutos de llamadas» (+0.48)")
                    io.github.ruddy2003.vestigium.ui.TarjetaRelacion(V("pantalla", "Minutos de pantalla", "Teléfono"), V("sueno", "Horas de sueño (reloj)", "Sueño"), -0.41, "58 días · moderada", false, null)
                    io.github.ruddy2003.vestigium.ui.TarjetaRelacion(V("cafe", "Café", "Hábitos"), V("valencia", "Valencia del día", "Ánimo"), 0.18, "22 días · débil · podría ser azar", true, null)
                }
            }
        }
    }
    @Test fun relacionesBotanico() = relaciones(TemaId.BOTANICO, true)
    @Test fun relacionesCiber() = relaciones(TemaId.CIBER, true)
    @Test fun relacionesMedieval() = relaciones(TemaId.MEDIEVAL, false)
    @Test fun relacionesAndalus() = relaciones(TemaId.ANDALUS, false)
    @Test fun relacionesMongol() = relaciones(TemaId.MONGOL, false)
    @Test fun relacionesSeda() = relaciones(TemaId.SEDA, true)
    @Test fun relacionesUrss() = relaciones(TemaId.URSS, false)
    @Test fun relacionesGotico() = relaciones(TemaId.GOTICO, true)
    @Test fun relacionesRomano() = relaciones(TemaId.ROMANO, false)
    @Test fun relacionesNavidad() = relaciones(TemaId.NAVIDAD, false)

    // ------------------------------------------------------------ mundo, teclado y fondos del teléfono

    private fun mundoEjemplo() {
        val hoy = java.time.LocalDate.now().toEpochDay().toInt()
        fun e(clave: String, nombre: String, pais: String, dias: Int, horas: Double) =
            io.github.ruddy2003.vestigium.data.Mundo.Estancia(clave, nombre, pais, 0.0, 0.0).apply { this.horas = horas; (0 until dias).forEach { this.dias.add(hoy - it * 2) } }
        io.github.ruddy2003.vestigium.data.Mundo.resumenFijo = io.github.ruddy2003.vestigium.data.Mundo.Resumen(
            ciudades = listOf(e("c1", "San Luis Potosí", "MX", 420, 6800.0), e("c2", "Rennes", "FR", 150, 3100.0), e("c3", "Ciudad de México", "MX", 38, 610.0),
                e("c4", "París", "FR", 9, 120.0), e("c5", "Lisboa", "PT", 4, 60.0), e("c6", "Londres", "GB", 3, 45.0)),
            paises = listOf(e("MX", "México", "MX", 470, 7500.0), e("FR", "Francia", "FR", 165, 3300.0), e("ES", "España", "ES", 6, 90.0), e("PT", "Portugal", "PT", 4, 60.0),
                e("GB", "Reino Unido", "GB", 3, 45.0), e("BE", "Bélgica", "BE", 2, 20.0), e("US", "Estados Unidos", "US", 3, 40.0), e("VA", "Ciudad del Vaticano", "VA", 1, 3.0)),
            sitios = mapOf(414 to hoy - 40, 412 to hoy - 60, 80 to hoy - 200, 600 to hoy - 210), muestras = 12000, desde = null)
    }
    @Test fun mundoBotanico() = pantalla(TemaId.BOTANICO, true, 4200) { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMundo(navVacia) }
    @Test fun mundoMedieval() = pantalla(TemaId.MEDIEVAL, false, 4200) { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMundo(navVacia) }
    @Test fun rascarCiber() = pantalla(TemaId.CIBER, true, 2400) { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaRascar(navVacia) }
    @Test fun rascarRomano() = pantalla(TemaId.ROMANO, false, 2400) { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaRascar(navVacia) }
    @Test fun unescoGotico() = pantalla(TemaId.GOTICO, false, 3200) { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaUnesco(navVacia) }

    private fun fondo(tema: TemaId, oscuro: Boolean) = captura(tema, oscuro, alto = 2400) { io.github.ruddy2003.vestigium.ui.FondoTelefonoVista(tema, oscuro, Modifier.fillMaxSize()) }
    @Test fun fondoEspacio() = fondo(TemaId.ESPACIO, true)
    @Test fun fondoBotanico() = fondo(TemaId.BOTANICO, false)
    @Test fun fondoGotico() = fondo(TemaId.GOTICO, true)
    @Test fun fondoRomano() = fondo(TemaId.ROMANO, false)
    @Test fun fondoCiber() = fondo(TemaId.CIBER, true)
    @Test fun fondoAndalus() = fondo(TemaId.ANDALUS, false)
    @Test fun fondoMongol() = fondo(TemaId.MONGOL, false)
    @Test fun fondoNavidad() = fondo(TemaId.NAVIDAD, true)
    private fun fondoInicio(tema: TemaId, oscuro: Boolean) = captura(tema, oscuro, alto = 2400) { io.github.ruddy2003.vestigium.ui.FondoTelefonoVista(tema, oscuro, Modifier.fillMaxSize(), "inicio") }
    @Test fun inicioGotico() = fondoInicio(TemaId.GOTICO, true)
    @Test fun inicioBotanico() = fondoInicio(TemaId.BOTANICO, false)
    @Test fun inicioEspacio() = fondoInicio(TemaId.ESPACIO, true)
    @Test fun inicioRomano() = fondoInicio(TemaId.ROMANO, false)

    private fun presentacion(tema: TemaId, oscuro: Boolean) = captura(tema, oscuro, alto = 2400) {
        io.github.ruddy2003.vestigium.ui.PresentacionTema.frase = io.github.ruddy2003.vestigium.ui.Frases.de(tema).first()
        io.github.ruddy2003.vestigium.ui.PresentacionTema.tema = tema
        io.github.ruddy2003.vestigium.ui.CapaPresentacionTema()
    }
    @Test fun presentacionGotico() = presentacion(TemaId.GOTICO, false)
    @Test fun presentacionCiber() = presentacion(TemaId.CIBER, true)
    @Test fun presentacionAndalus() = presentacion(TemaId.ANDALUS, false)

    // ------------------------------------------------------------ sueños, cartas, conocerte, brújula, mapas y obras

    private fun suenosEjemplo() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        val textos = listOf(
            "Estaba en una estación de tren que también era la casa de mis abuelos. @ana me decía que el tren salía por la ventana y no me sorprendía.",
            "Volaba bajito sobre un mar verde. Sabía que era un sueño y traté de subir más, pero el cielo era de tela.",
            "Llegaba tarde a un examen de una materia que nunca había cursado. Las preguntas estaban en un idioma inventado. #escuela",
            "Una ciudad de noche llena de faroles; caminaba con @luis buscando una librería que cambiaba de calle.",
            "Un perro enorme me acompañaba por el bosque y me enseñaba un camino de piedras blancas.",
        )
        val lucidez = listOf("no", "total", "no", "parcial", "no")
        val tipo = listOf("mundano", "bizarro_surreal", "ansioso_pesadilla", "recurrente", "mundano")
        val d = Repo.dias.toMutableMap()
        (0 until 14).forEach { k ->
            val f = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -(k * 2L + 1))
            val base = d[f] ?: io.github.ruddy2003.vestigium.data.Dia(fecha = f)
            d[f] = base.copy(sueno_recordado = true, sueno_texto = textos[k % textos.size], sueno_valencia = 2 + (k * 3) % 6, sueno_lucidez = lucidez[k % 5],
                sueno_tipo = tipo[k % 5], sueno_vividez = if (k % 2 == 0) "muy_vivido" else "moderado", sueno_temas = if (k % 3 == 0) listOf("@ana", "#casa") else emptyList())
        }
        Repo.diasEnMemoria(d)
    }
    private fun suenos(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 3400) { suenosEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "suenos") }
    @Test fun suenosGotico() = suenos(TemaId.GOTICO, false)
    @Test fun suenosRomano() = suenos(TemaId.ROMANO, false)
    @Test fun suenosNapoleon() = suenos(TemaId.NAPOLEON, false)
    @Test fun suenosCiber() = suenos(TemaId.CIBER, true)
    @Test fun suenosDistopia() = suenos(TemaId.DISTOPIA, false)
    @Test fun suenosBotanico() = suenos(TemaId.BOTANICO, true)
    private fun suenoLectura(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 2400) {
        suenosEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.sueno = null
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaSueno(navVacia)
    }
    @Test fun suenoLecturaMedieval() = suenoLectura(TemaId.MEDIEVAL, false)
    @Test fun suenoLecturaEspacio() = suenoLectura(TemaId.ESPACIO, true)
    @Test fun suenoLecturaRefugio() = suenoLectura(TemaId.REFUGIO, true)
    @Test fun suenoLecturaAndalus() = suenoLectura(TemaId.ANDALUS, false)
    @Test fun escritorSuenoRenacimiento() = pantalla(TemaId.RENACIMIENTO, false, 2400) {
        io.github.ruddy2003.vestigium.ui.EscritorTema("2026-10-16", "Caminaba por un taller lleno de máquinas de madera que se movían solas.", {}, {}, TemaId.RENACIMIENTO,
            modo = io.github.ruddy2003.vestigium.ui.Escrito.SUENO)
    }

    private fun cartasEjemplo() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        fun d(k: Long) = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, k)
        Repo.cartasEnMemoria(listOf(
            io.github.ruddy2003.vestigium.data.Carta("k1", d(-30), d(335), "Querido yo de dentro de un año: ojalá ya hayas terminado la tesis.", hora = "22:14", lugar = "San Luis Potosí"),
            io.github.ruddy2003.vestigium.data.Carta("k2", d(-400), d(-2), "Hace más de un año te escribo desde la mesa de la cocina.", hora = "07:40", lugar = "Rennes"),
            io.github.ruddy2003.vestigium.data.Carta("k3", d(-800), d(-70), "Si estás leyendo esto, ya pasaron dos años. Espero que sigas escalando.", abierta = true, abierta_el = d(-70), hora = "23:02"),
            io.github.ruddy2003.vestigium.data.Carta("k4", d(-5), d(-5), "Ana: gracias por la tarde de ayer. Me acordé de cuando nos perdimos buscando la librería.", abierta = true, para = "ana", hora = "21:40", lugar = "San Luis Potosí", abierta_el = d(-5)),
            io.github.ruddy2003.vestigium.data.Carta("k5", d(-60), d(-60), "Luis: te debo el café y la conversación de los planes.", abierta = true, para = "luis", hora = "18:05", abierta_el = d(-60)),
            io.github.ruddy2003.vestigium.data.Carta("k6", d(-3), d(1826), "Para dentro de cinco años: ¿dónde vives?", hora = "10:12"),
        ))
    }
    private fun cartas(tema: TemaId, oscuro: Boolean, nombre: String? = null) = pantalla(tema, oscuro, 3400, nombre) { cartasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "cartas") }
    @Test fun cartasMedieval() = cartas(TemaId.MEDIEVAL, false)
    @Test fun cartasCiber() = cartas(TemaId.CIBER, true)
    @Test fun cartasNavidad() = cartas(TemaId.NAVIDAD, false)
    @Test fun cartasBotanico() = cartas(TemaId.BOTANICO, false)
    @Test fun cartasDistopia() = cartas(TemaId.DISTOPIA, false)
    @Test fun cartasSeda() = cartas(TemaId.SEDA, true)

    private fun sobres(temas: List<TemaId>, nombre: String) = captura(TemaId.ROMANO, false, alto = 3000, nombre = nombre) {
        cartasEjemplo()
        val l = Repo.cartas
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            temas.forEach { t ->
                androidx.compose.material3.MaterialTheme(colorScheme = io.github.ruddy2003.vestigium.ui.esquemaDe(t, t.soloOscuro), typography = io.github.ruddy2003.vestigium.ui.tipoDe(t, t.soloOscuro)) {
                    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(l[0], l[1], l[3]).forEach { c ->
                            io.github.ruddy2003.vestigium.ui.SobreCarta(c, Modifier.weight(1f).aspectRatio(if (c.abierta) 1.05f else 1.5f), tema = t) {}
                        }
                    }
                }
            }
        }
    }
    /** Los sobres de las cartas en todos los temas, siete por captura. */
    @Test fun sobres() = TemaId.entries.chunked(7).forEachIndexed { i, temas -> sobres(temas, "parte${i + 1}") }

    private fun cartaLectura(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 2400) {
        Repo.configEnMemoria(Repo.config.copy(perfil = io.github.ruddy2003.vestigium.data.Perfil(nombre = "Ana")))
        cartasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.carta = "k4"
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaCarta(navVacia)
    }
    @Test fun cartaLecturaGotico() = cartaLectura(TemaId.GOTICO, false)
    @Test fun cartaLecturaSeda() = cartaLectura(TemaId.SEDA, true)
    @Test fun cartaLecturaUrss() = cartaLectura(TemaId.URSS, false)
    @Test fun cartaLecturaCiber() = cartaLectura(TemaId.CIBER, true)
    private fun cartaNueva(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 2600) {
        io.github.ruddy2003.vestigium.ui.BorradorCarta.texto = "Querido yo del futuro: hoy fue un buen día. Escribí, escalé y cené con amigos."
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaCartaNueva(navVacia)
    }
    @Test fun cartaNuevaBotanico() = cartaNueva(TemaId.BOTANICO, false)
    @Test fun cartaNuevaEspacio() = cartaNueva(TemaId.ESPACIO, true)
    @Test fun escritorCartaNapoleon() = pantalla(TemaId.NAPOLEON, false, 2400) {
        io.github.ruddy2003.vestigium.ui.EscritorTema("2026-10-18", "Querido yo del futuro: te escribo desde la mesa de siempre.", {}, {}, TemaId.NAPOLEON, modo = io.github.ruddy2003.vestigium.ui.Escrito.CARTA,
            encabezado = { p -> io.github.ruddy2003.vestigium.ui.EncabezadoCarta(TemaId.NAPOLEON, p, "2026-10-18", "11:33", "San Luis Potosí", "", "2027-10-18") }, textoBoton = "listo")
    }

    private val perfilEjemplo = io.github.ruddy2003.vestigium.data.Perfil(nombre = "Javier", nacimiento = "1998-05-12", ciudad = "San Luis Potosí", pais = "MX",
        vive_con = listOf("padres", "hermanos"), convivientes = listOf("mama", "pedro"), mascotas = listOf(io.github.ruddy2003.vestigium.data.Mascota("Canela", "perro")),
        cercanas = listOf(io.github.ruddy2003.vestigium.data.PersonaCercana("ana", "pareja"), io.github.ruddy2003.vestigium.data.PersonaCercana("luis", "amistad")),
        pareja = "pareja", ocupacion = listOf("estudio", "trabajo"), ocupacion_detalle = "doctorado en psicología", horario = "flexible", cronotipo = "noche",
        ejercicio = "3_4", alcohol = "ocasional", tabaco = "no", cafe = "2_3", momento = "Terminando la tesis.")
    @Test fun conocerRomano() = pantalla(TemaId.ROMANO, false, 5200) {
        Repo.configEnMemoria(Repo.config.copy(conocer_visto = false))
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaConocer(navVacia)
    }
    @Test fun conocerLaboratorio() = pantalla(TemaId.LABORATORIO, false, 5600) {
        Repo.configEnMemoria(Repo.config.copy(conocer_visto = true, perfil = perfilEjemplo, perfil_historial = listOf(
            io.github.ruddy2003.vestigium.data.CambioPerfil("2026-09-01", "pareja", "Sin pareja", "En pareja"),
            io.github.ruddy2003.vestigium.data.CambioPerfil("2026-06-15", "ciudad", "Rennes, FR", "San Luis Potosí, MX"))))
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaConocer(navVacia)
    }

    private fun testsEjemplo(): List<io.github.ruddy2003.vestigium.data.TestPolitico> {
        val P = io.github.ruddy2003.vestigium.data.Politica
        fun t(f: String, k: Int) = P.AFIRMACIONES.associate { a -> a.id to (((a.id.hashCode() + k * 7) and 0x7fffffff) % 5 + 1) }.let { r -> io.github.ruddy2003.vestigium.data.TestPolitico(f, r, P.ejes(r)) }
        return listOf(t("2023-10-20", 1), t("2024-10-11", 2), t("2025-10-15", 4))
    }
    @Test fun politicaUrss() = pantalla(TemaId.URSS, false, 4200) {
        obrasEjemplo(); Repo.configEnMemoria(Repo.config.copy(politica = testsEjemplo()))
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaPolitica(navVacia)
    }
    @Test fun politicaHeleno() = pantalla(TemaId.HELENO, true, 4200) {
        Repo.configEnMemoria(Repo.config.copy(politica = testsEjemplo()))
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaPolitica(navVacia)
    }
    @Test fun politicaVaciaMedieval() = pantalla(TemaId.MEDIEVAL, false, 2400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaPolitica(navVacia) }

    @Test fun mapasBotanico() = pantalla(TemaId.BOTANICO, false, 3400) { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMapas(navVacia) }
    @Test fun mapasEspacio() = pantalla(TemaId.ESPACIO, true, 3400) { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMapas(navVacia) }
    @Test fun masGotico() = pantalla(TemaId.GOTICO, false, 3600) { mundoEjemplo(); obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
    @Test fun obrasGaleriaMedieval() = pantalla(TemaId.MEDIEVAL, false, 3000) { obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaObras(navVacia) }
    @Test fun obrasGaleriaCiber() = pantalla(TemaId.CIBER, true, 3000) { obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaObras(navVacia) }
    @Test fun calificaciones() = hojaTemas(porParte = 40, alto = { 100 + it * 91 }) { temas ->
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            temas.forEach { t ->
                androidx.compose.material3.MaterialTheme(colorScheme = io.github.ruddy2003.vestigium.ui.esquemaDe(t, t.soloOscuro), typography = io.github.ruddy2003.vestigium.ui.tipoDe(t, t.soloOscuro)) {
                    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(6.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(t.nombre, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                        io.github.ruddy2003.vestigium.ui.pantallas.Calificacion(7, tam = 18.dp, tema = t)
                        io.github.ruddy2003.vestigium.ui.pantallas.Calificacion(10, tam = 18.dp, tema = t)
                    }
                }
            }
        }
    }
    /** Revisa que todo se conecte: variables, panel, relaciones por día y por semana, panel extendido y diccionario. */
    @Test fun conexiones() = pantalla(TemaId.LABORATORIO, false, 3600) {
        suenosEjemplo(); cartasEjemplo(); obrasEjemplo()
        Repo.configEnMemoria(Repo.config.copy(perfil = perfilEjemplo, politica = testsEjemplo(), perfil_historial = listOf(
            io.github.ruddy2003.vestigium.data.CambioPerfil(io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), -15), "pareja", "Sin pareja", "En pareja"))))
        val E = io.github.ruddy2003.vestigium.ciencia.Estadistica
        val v = E.variables(); val p = E.panel()
        val rel = E.relaciones(p, v, false)
        val sem = E.semanal(p)
        val relSem = E.relaciones(sem, v, false, minN = 3, paso = 7)
        val ext = io.github.ruddy2003.vestigium.data.Exportar.filasPanelExtendido()
        val cols = LinkedHashSet<String>(); ext.forEach { cols.addAll(it.keys) }
        val dic = io.github.ruddy2003.vestigium.data.Diccionario.csv(listOf(io.github.ruddy2003.vestigium.data.Exportar.Tabla("vm_panel_extendido.csv", "", ext)))
        val sinDescripcion = dic.lines().drop(1).filter { it.isNotBlank() && it.split(",").getOrNull(3).isNullOrBlank() }.map { it.split(",").getOrNull(1) }
        val dinamicas = v.filter { io.github.ruddy2003.vestigium.ciencia.Dinamicas.prefijo(it.id) != null }
        Column(Modifier.padding(16.dp)) {
            Text("variables: ${v.size} (dinámicas ${dinamicas.size}) · panel: ${p.size} días (${p.keys.minOrNull()} a ${p.keys.maxOrNull()})", style = MaterialTheme.typography.titleMedium)
            Text("relaciones por día: ${rel.lista.size} · por semana: ${sem.size} semanas, ${relSem.lista.size} relaciones", style = MaterialTheme.typography.bodyMedium)
            Text("extendido: ${ext.size} filas · ${cols.size} columnas", style = MaterialTheme.typography.bodyMedium)
            Text("sin descripción (${sinDescripcion.size}): " + sinDescripcion.take(80).joinToString(", "), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            Text("ejemplo: " + rel.lista.take(6).joinToString(" | ") { "${it.a.id}~${it.b.id} ${"%.2f".format(it.r)}${if (it.obvia) " (obvia)" else ""}" }, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            Text("nombres: " + dinamicas.take(12).joinToString(" · ") { it.nombre }, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
        }
    }
    @Test fun datosGotico() = pantalla(TemaId.GOTICO, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDatos(navVacia) }

    // ------------------------------------------------------------ salud, tus apps y otras vidas
    /** Treinta días de MacroFactor con micronutrientes, entrenos y chats inventados. */
    private fun saludEjemplo() {
        io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono = true
        val D = io.github.ruddy2003.vestigium.data.Fechas
        val hoy = D.diaLogico(); val r = java.util.Random(3)
        val fechas = (0..40).map { D.sumar(hoy, -it.toLong()) }
        val nut = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.NutricionDia(f, mapOf(
            "kcal" to 2300.0 + r.nextInt(500), "proteina_g" to 120.0 + r.nextInt(40), "carbohidratos_g" to 250.0 + r.nextInt(60), "grasa_g" to 80.0 + r.nextInt(20),
            "fibra_g" to 17.0 + r.nextInt(8), "azucares_anadidos_g" to 45.0 + r.nextInt(40), "azucares_g" to 90.0, "sodio_mg" to 2600.0 + r.nextInt(900), "potasio_mg" to 2600.0 + r.nextInt(800),
            "grasa_saturada_g" to 26.0 + r.nextInt(10), "alcohol_g" to (if (r.nextInt(5) == 0) 28.0 else 0.0), "cafeina_mg" to 180.0 + r.nextInt(150), "agua_g" to 1700.0 + r.nextInt(800),
            "vitamin_c_mg" to 70.0 + r.nextInt(60), "vitamin_d_mcg" to 3.0 + r.nextInt(4), "vitamin_a_mcg" to 600.0 + r.nextInt(400), "calcio_mg" to 800.0 + r.nextInt(300),
            "hierro_mg" to 14.0 + r.nextInt(6), "magnesio_mg" to 280.0 + r.nextInt(80), "zinc_mg" to 10.0 + r.nextInt(5), "folate_mcg" to 350.0 + r.nextInt(150),
            "b12_cobalamin_mcg" to 4.0 + r.nextInt(3), "vitamin_e_mg" to 7.0 + r.nextInt(5), "selenium_mcg" to 90.0, "gasto_kcal" to 2600.0, "peso_tendencia_kg" to 74.0 - fechas.indexOf(f) * 0.03),
            comidas = 4, primera = "08:40", ultima = "21:15") }
        val ent = fechas.filterIndexed { i, _ -> i % 2 == 0 }.associateWith { f -> io.github.ruddy2003.vestigium.data.EntrenoDia(f, listOf("Torso"), 70.0, 18 + r.nextInt(6), 16, 140, 9000.0 + r.nextInt(3000),
            mapOf("pecho" to 6.0, "espalda_alta" to 5.0, "biceps" to 3.0, "cuadriceps" to 4.0)) }
        val chats = fechas.flatMap { f -> listOf("Ana", "Luis", "Familia").map { c -> io.github.ruddy2003.vestigium.data.ChatDia("$f$c", f, c, c == "Familia", mios = r.nextInt(30), otros = r.nextInt(40), palabras_mias = 200, resp_mia_min = 4.0 + r.nextInt(20), resp_otros_min = 6.0, inicie = 1, conversaciones = 2) } }
        Repo.nutricionEnMemoria(nut, ent, chats)
        Repo.configEnMemoria(Repo.config.copy(perfil = perfilEjemplo, salud_sexo = "hombre"))
    }
    @Test fun saludBotanico() = pantalla(TemaId.BOTANICO, false, 5200) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaSalud(navVacia) }
    @Test fun saludGotico() = pantalla(TemaId.GOTICO, true, 3000) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaSalud(navVacia) }
    @Test fun tusAppsComida() = pantalla(TemaId.CIBER, true, 3800) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaTusApps(navVacia, "comida") }
    @Test fun tusAppsGimnasio() = pantalla(TemaId.ROMANO, false, 3400) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaTusApps(navVacia, "gimnasio") }
    @Test fun tusAppsWhatsapp() = pantalla(TemaId.ANDALUS, false, 2600) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaTusApps(navVacia, "whatsapp") }
    @Test fun otrasVidasRomano() = pantalla(TemaId.ROMANO, false, 6400) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaOtrasVidas(navVacia) }
    @Test fun otrasVidasLaboratorio() = pantalla(TemaId.LABORATORIO, false, 6400) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaOtrasVidas(navVacia) }
    @Test fun otrasVidasDistopia() = pantalla(TemaId.DISTOPIA, true, 6400) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaOtrasVidas(navVacia) }
    @Test fun otrasVidasBotanico() = pantalla(TemaId.BOTANICO, true, 6400) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaOtrasVidas(navVacia) }
    @Test fun medallasVidas() = captura(TemaId.RENACIMIENTO, false, alto = 1400) {
        Column(Modifier.padding(12.dp)) {
            val todas = io.github.ruddy2003.vestigium.data.OtrasVidas.BASE + io.github.ruddy2003.vestigium.data.OtrasVidas.DE_TEMAS
            todas.chunked(6).forEach { fila ->
                Row(Modifier.padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    fila.forEach { v ->
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            io.github.ruddy2003.vestigium.ui.pantallas.MedallaVida(v, 58.dp)
                            Text(v.id.take(10), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
    @Test fun masBotanico() = pantalla(TemaId.BOTANICO, false, 3800) { mundoEjemplo(); obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
    @Test fun masBotanicoOscuro() = pantalla(TemaId.BOTANICO, true, 2400) { mundoEjemplo(); obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
    @Test fun emblemasBotanico() = captura(TemaId.BOTANICO, false, alto = 1400) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                io.github.ruddy2003.vestigium.ui.Emblema(TemaId.BOTANICO, 150.dp, animado = false)
                io.github.ruddy2003.vestigium.ui.Emblema(TemaId.BOTANICO, 60.dp, animado = false)
            }
            io.github.ruddy2003.vestigium.ui.Enredadera(Modifier)
            io.github.ruddy2003.vestigium.ui.SelloHuella(Modifier.fillMaxWidth(0.5f).aspectRatio(1f), TemaId.BOTANICO)
        }
    }
    // ------------------------------------------------------------ logros, «Tu año» y reportes
    @Test fun logrosBotanico() = pantalla(TemaId.BOTANICO, false, 4200) {
        io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono = true
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia)
    }
    @Test fun logrosCiber() = pantalla(TemaId.CIBER, true, 3000) {
        io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono = true
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia)
    }
    private fun tarjetaAnio(tema: TemaId, oscuro: Boolean, id: String, nombre: String? = null) = pantalla(tema, oscuro, 2340, nombre) {
        val ctx = androidx.compose.ui.platform.LocalContext.current
        val d = androidx.compose.runtime.remember { io.github.ruddy2003.vestigium.ciencia.TuAnio.calcular(ctx, io.github.ruddy2003.vestigium.data.Fechas.diaLogico().take(4).toInt()) }
        io.github.ruddy2003.vestigium.ui.pantallas.MuestraTarjetaAnio(d, id)
    }
    @Test fun anioPortadaRomano() = tarjetaAnio(TemaId.ROMANO, true, "portada")
    @Test fun anioAnimoBotanico() = tarjetaAnio(TemaId.BOTANICO, false, "animo")
    @Test fun anioSuenoEspacio() = tarjetaAnio(TemaId.ESPACIO, true, "sueno")
    @Test fun anioComunDistopia() = tarjetaAnio(TemaId.DISTOPIA, false, "comun")
    @Test fun anioArquetipoGotico() = tarjetaAnio(TemaId.GOTICO, true, "arquetipo")
    @Test fun anioCartelCiber() = tarjetaAnio(TemaId.CIBER, true, "cartel")
    @Test fun anioTelefonoLaboratorio() = tarjetaAnio(TemaId.LABORATORIO, false, "telefono")
    @Test fun anioPantallaMedieval() = pantalla(TemaId.MEDIEVAL, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaTuAnio(navVacia) }
    private fun reporte(tema: TemaId, oscuro: Boolean, alto: Int, anual: Boolean, nombre: String? = null) = pantalla(tema, oscuro, alto, nombre) {
        val ctx = androidx.compose.ui.platform.LocalContext.current
        val r = androidx.compose.runtime.remember {
            runCatching {
                if (anual) io.github.ruddy2003.vestigium.reportes.Reportes.imagenAnio(ctx, io.github.ruddy2003.vestigium.data.Fechas.diaLogico().take(4).toInt())
                else io.github.ruddy2003.vestigium.reportes.Reportes.imagenSemana(ctx, io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.lunesDe(io.github.ruddy2003.vestigium.data.Fechas.diaLogico()), -7))
            }
        }
        val b = r.getOrNull()
        if (b != null) androidx.compose.foundation.Image(b.asImageBitmap(), null, Modifier.fillMaxWidth())
        else Text("Falló: ${r.exceptionOrNull()}")
    }
    @Test fun reporteSemanaRomano() = reporte(TemaId.ROMANO, true, 4200, false)
    @Test fun reporteSemanaBotanico() = reporte(TemaId.BOTANICO, false, 4200, false)
    @Test fun reporteAnioGotico() = reporte(TemaId.GOTICO, false, 5200, true)
    @Test fun epigrafeMedieval() = pantalla(TemaId.MEDIEVAL, false, 2400) {
        val ayer = io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), -1)
        Repo.configEnMemoria(Repo.config.copy(frases_guardadas = listOf(io.github.ruddy2003.vestigium.data.FraseGuardada("Quien no sabe callar, no sabe hablar.", null, "Refrán castellano", "medieval", ayer))))
        io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.entrada = null
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaEntrada(navVacia)
    }
    // ------------------------------------------------------------ versión 67: cinta, constelación, hall, campañas, personaje, marcas

    private fun ejemploV67() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        fun iso(f: String, h: Int, m: Int = 0) = java.time.LocalDate.parse(f).atTime(h, m).atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime().withNano(0).toString()
        val ayer = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -1)
        Repo.tramosEnMemoria(listOf(
            io.github.ruddy2003.vestigium.data.Tramo("t1", hoy, "tesis", iso(hoy, 9, 30), iso(hoy, 11, 10), 100, "capítulo 2 #tesis"),
            io.github.ruddy2003.vestigium.data.Tramo("t2", hoy, "siesta", iso(hoy, 15, 10), iso(hoy, 15, 35), 25),
            io.github.ruddy2003.vestigium.data.Tramo("t3", hoy, "idiomas", iso(hoy, 20, 0), iso(hoy, 20, 40), 40, "chino: tonos"),
            io.github.ruddy2003.vestigium.data.Tramo("t4", ayer, "lectura", iso(ayer, 22, 0), iso(ayer, 22, 50), 50),
        ))
        val momentos = Repo.momentos + listOf(
            io.github.ruddy2003.vestigium.data.Momento("m3", iso(hoy, 13, 5), hoy, "muestreo", valencia = 6, con_quien = listOf("amigos")),
            io.github.ruddy2003.vestigium.data.Momento("m4", iso(hoy, 19, 40), hoy, "alegria", texto = "cena con @ana y @luis #cumple", intensidad = 4),
            io.github.ruddy2003.vestigium.data.Momento("m5", iso(ayer, 23, 10), ayer, "impulso", intensidad = 3),
            io.github.ruddy2003.vestigium.data.Momento("m6", iso(ayer, 23, 50), ayer, "recaida", detonantes = listOf("noche_insomnio"), antes = "cama", dispositivo = "telefono", lugar = "casa", duracion_min = 25),
        )
        Repo.ejemploEnMemoria(Repo.dias, Repo.salud, Repo.pantalla.mapValues { (_, p) -> p.copy(por_hora_min = List(24) { h -> if (h in 8..23) (h * 7) % 45 else 0 }) }, Repo.gastos,
            momentos, Repo.notifs, Repo.entorno, Repo.nutricion, Repo.lugares, Repo.visitas, Repo.sesiones, Repo.ingresos)
    }
    private fun v67(tema: TemaId, oscuro: Boolean, alto: Int, extra: (Config) -> Config = { it }, nombre: String? = null, cont: @Composable () -> Unit) {
        Ejemplo.cargar(); ejemploV67()
        Repo.configEnMemoria(extra(config(tema, oscuro).copy(seguimiento_privado = true, habitos = listOf(io.github.ruddy2003.vestigium.data.Habito("h1", "Leer 20 min"), io.github.ruddy2003.vestigium.data.Habito("h2", "No ver contenido adulto")))))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot(nombre) { Lienzo(cont) }
    }
    @Test fun cintaRomano() = v67(TemaId.ROMANO, false, 2600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaCinta(navVacia) }
    @Test fun cintaCiber() = v67(TemaId.CIBER, true, 2600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaCinta(navVacia) }
    @Test fun constelacionGotico() = v67(TemaId.GOTICO, true, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaConstelacion(navVacia) }
    @Test fun constelacionNavidad() = v67(TemaId.NAVIDAD, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaConstelacion(navVacia) }
    @Test fun recaidasMedieval() = v67(TemaId.MEDIEVAL, false, 3600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaRecaidas(navVacia) }
    @Test fun enfoqueLaboratorio() = v67(TemaId.LABORATORIO, false, 2800) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaEnfoque(navVacia) }
    @Test fun estadoDatosOrden() = v67(TemaId.ORDEN, true, 2400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaEstadoDatos(navVacia) }
    @Test fun nocheV67Seda() = v67(TemaId.SEDA, true, 4400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(navVacia) }
    @Test fun masV67Romano() = v67(TemaId.ROMANO, false, 4400, { it.copy(favoritos_mas = listOf("cinta", "enfoque", "estado_datos")) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
    private fun campanas(tema: TemaId, oscuro: Boolean, nombre: String? = null) = v67(tema, oscuro, 3000, { c ->
        val l = io.github.ruddy2003.vestigium.data.Fechas.lunesDe(io.github.ruddy2003.vestigium.data.Fechas.diaLogico())
        c.copy(campanas = listOf(io.github.ruddy2003.vestigium.data.Campana("c1", l, "diario", 5, tema.id), io.github.ruddy2003.vestigium.data.Campana("c2", l, "sueno7", 4, tema.id),
            io.github.ruddy2003.vestigium.data.Campana("c0", io.github.ruddy2003.vestigium.data.Fechas.sumar(l, -7), "pasos", 5, tema.id)))
    }, nombre) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaCampanas(navVacia) }
    @Test fun campanasNapoleon() = campanas(TemaId.NAPOLEON, false)
    @Test fun campanasEspacio() = campanas(TemaId.ESPACIO, true)
    @Test fun campanasRomano() = campanas(TemaId.ROMANO, false)
    @Test fun campanasSeda() = campanas(TemaId.SEDA, true)
    // El hall vive dentro de Logros (la pantalla PantallaHall se quitó en N-25; la ruta «hall» abre Logros).
    private fun hall(tema: TemaId, oscuro: Boolean, nombre: String? = null) = v67(tema, oscuro, 3400, { it.copy(hall_logros = listOf("diario_30", "ciudades_2", "paises_5", "unesco_1", "continentes_2", "pasos_dia_10000", "racha_diario_7", "diario_7")) }, nombre) {
        mundoEjemplo(); io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono = true
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia)
    }
    @Test fun hallRomano() = hall(TemaId.ROMANO, false)
    @Test fun hallBotanico() = hall(TemaId.BOTANICO, false)
    @Test fun hallAndalus() = hall(TemaId.ANDALUS, true)

    /** Todas las piezas de logros, en cuatro temas. */
    private fun piezas(tema: TemaId, oscuro: Boolean, nombre: String? = null) = captura(tema, oscuro, alto = 2400, nombre = nombre) {
        val ids = listOf("ciudades_10", "paises_5", "continentes_3", "unesco_1", "lejos_500", "pasos_dia_10000", "entrenos_10", "escalada_1", "diario_100", "suenos_10", "carta_1",
            "momentos_100", "reaccion_1", "brujula", "obras_10", "frases_10", "personas_10", "c_hab_h1_20_30", "campanas_5", "madrugador", "gastos_100", "enfoque_10", "p_x", "lucido")
        Column(Modifier.padding(12.dp)) {
            ids.chunked(4).forEachIndexed { f, fila ->
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    fila.forEachIndexed { k, id ->
                        val l = io.github.ruddy2003.vestigium.ciencia.Logros.Logro(id, id, "", "★", "Diario", (f + k) % 4 + 1, 1.0, 1.0, "2026-10-19")
                        io.github.ruddy2003.vestigium.ui.pantallas.PiezaLogro(l, 84.dp, tema = tema)
                    }
                }
            }
        }
    }
    @Test fun piezasRomano() = piezas(TemaId.ROMANO, false)
    @Test fun piezasCiber() = piezas(TemaId.CIBER, true)
    @Test fun piezasSeda() = piezas(TemaId.SEDA, false)
    @Test fun piezasGotico() = piezas(TemaId.GOTICO, true)

    /** El personaje de cada tema, con estados distintos (cansado, feliz, lluvia, frío, calor, noche, racha). */
    @Test fun personajes() = hojaTemas(porParte = 36, alto = { 100 + (it + 3) / 4 * 517 }) { temas ->
        val estados = listOf(
            io.github.ruddy2003.vestigium.ui.Personaje.Estado(false, 6.5, 8, false, false, false, false, ""),
            io.github.ruddy2003.vestigium.ui.Personaje.Estado(true, 3.0, 2, false, false, false, false, ""),
            io.github.ruddy2003.vestigium.ui.Personaje.Estado(false, 5.0, 31, true, false, false, false, ""),
            io.github.ruddy2003.vestigium.ui.Personaje.Estado(false, 4.0, 0, false, true, false, true, ""),
            io.github.ruddy2003.vestigium.ui.Personaje.Estado(false, 6.0, 3, false, false, true, false, ""),
        )
        Column(Modifier.padding(8.dp)) {
            temas.chunked(4).forEachIndexed { f, fila ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    fila.forEachIndexed { k, t ->
                        androidx.compose.material3.MaterialTheme(colorScheme = io.github.ruddy2003.vestigium.ui.esquemaDe(t, t.soloOscuro), typography = io.github.ruddy2003.vestigium.ui.tipoDe(t, t.soloOscuro)) {
                            Box(Modifier.background(MaterialTheme.colorScheme.background).padding(4.dp)) {
                                io.github.ruddy2003.vestigium.ui.FiguraPersonaje(estados[(f * 4 + k) % estados.size], tam = 120.dp, tema = t)
                            }
                        }
                    }
                }
            }
        }
    }

    /** Las @personas y los #temas marcados en cada tema. */
    @Test fun marcas() = hojaTemas(porParte = 28, alto = { 100 + it * 167 }) { temas ->
        Column(Modifier.padding(12.dp)) {
            temas.forEach { t ->
                androidx.compose.material3.MaterialTheme(colorScheme = io.github.ruddy2003.vestigium.ui.esquemaDe(t, t.soloOscuro), typography = io.github.ruddy2003.vestigium.ui.tipoDe(t, t.soloOscuro)) {
                    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(horizontal = 10.dp, vertical = 6.dp)) {
                        io.github.ruddy2003.vestigium.ui.TextoMarcado("${t.nombre}: café con @ana y @luis_m después del #protocolo de la #tesis", style = MaterialTheme.typography.bodyLarge, tema = t)
                    }
                }
            }
        }
    }

    @Test fun celebracionGotico() = v67(TemaId.GOTICO, true, 2400) {
        io.github.ruddy2003.vestigium.ui.Celebracion.mostrar(listOf(io.github.ruddy2003.vestigium.ciencia.Logros.Logro("paises_5", "Pasaporte con sellos", "Visita 5 países", "⚐", "Mundo", 2, 5.0, 5.0, "2026-10-19")))
        Box(Modifier.fillMaxSize()) { io.github.ruddy2003.vestigium.ui.CapaLogro() }
    }
    @Test fun celebracionUrss() = v67(TemaId.URSS, false, 2400) {
        io.github.ruddy2003.vestigium.ui.Celebracion.mostrar(listOf(io.github.ruddy2003.vestigium.ciencia.Logros.Logro("ciudades_10", "Viajero", "Pasa tiempo en 10 ciudades distintas", "⌂", "Mundo", 2, 10.0, 10.0, "2026-10-19")))
        Box(Modifier.fillMaxSize()) { io.github.ruddy2003.vestigium.ui.CapaLogro() }
    }

    // ---------------- v73: ánimo unido, sustancias, tu gente, sueños, Vida ----------------
    private fun ejemploV73() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        val ayer = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -1)
        fun iso(f: String, h: Int, m: Int = 0) = java.time.LocalDate.parse(f).atTime(h, m).atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime().withNano(0).toString()
        Repo.consumosEnMemoria(listOf(
            io.github.ruddy2003.vestigium.data.Consumo("c1", ayer, "alcohol", inicio = iso(ayer, 21, 0), fin = iso(ayer, 23, 59),
                niveles = listOf(io.github.ruddy2003.vestigium.data.Lectura(iso(ayer, 22, 0), 3), io.github.ruddy2003.vestigium.data.Lectura(iso(ayer, 23, 0), 6)),
                nivel_max = 6, cantidad = 4.0, con_quien = listOf("amigos")),
            io.github.ruddy2003.vestigium.data.Consumo("c2", hoy, "cafeina", inicio = iso(hoy, 9, 0), fin = iso(hoy, 9, 0), cantidad = 95.0, completo = true),
            io.github.ruddy2003.vestigium.data.Consumo("c3", hoy, "cafeina", inicio = iso(hoy, 15, 30), fin = iso(hoy, 15, 30), cantidad = 64.0, completo = true),
            io.github.ruddy2003.vestigium.data.Consumo("c4", hoy, "cannabis", inicio = iso(hoy, 20, 30), fin = iso(hoy, 22, 30), nivel_max = 4, via = "vape", completo = true),
        ))
        val momentos = Repo.momentos + listOf(
            io.github.ruddy2003.vestigium.data.Momento("a1", iso(hoy, 10, 15), hoy, "animo", valencia = 5, activacion = 5, emociones = listOf("calma", "esperanza"), pensamiento = "plan"),
            io.github.ruddy2003.vestigium.data.Momento("a2", iso(hoy, 17, 45), hoy, "animo", valencia = 2, activacion = 6, emociones = listOf("estres", "frustracion"), texto = "la entrega con @luis"),
            io.github.ruddy2003.vestigium.data.Momento("a3", iso(ayer, 12, 0), ayer, "animo", valencia = 6, emociones = listOf("diversion"), texto = "comida con @marta"),
            io.github.ruddy2003.vestigium.data.Momento("a4", iso(hoy, 12, 30), hoy, "animo", valencia = 5, emociones = listOf("calma"), texto = "café con @marta y @anita"),
        )
        Repo.ejemploEnMemoria(Repo.dias + (ayer to (Repo.dias[ayer] ?: io.github.ruddy2003.vestigium.data.Dia(ayer)).copy(sueno_recordado = true, sueno_texto = "Volvía a la casa de mi abuela con @ana",
            sueno_categorias = listOf("tema_regreso", "per_familia", "est_conexion"))), Repo.salud, Repo.pantalla, Repo.gastos,
            momentos, Repo.notifs, Repo.entorno, Repo.nutricion, Repo.lugares, Repo.visitas, Repo.sesiones, Repo.ingresos)
    }
    private fun v73(tema: TemaId, oscuro: Boolean, alto: Int, extra: (Config) -> Config = { it }, nombre: String? = null, cont: @Composable () -> Unit) {
        Ejemplo.cargar(); ejemploV67(); ejemploV73()
        Repo.configEnMemoria(extra(config(tema, oscuro).copy(personas = listOf(
            io.github.ruddy2003.vestigium.data.Persona("ana", "amistad_cercana", "de la prepa", 6, "2026-01-01"),
            io.github.ruddy2003.vestigium.data.Persona("luis", "colega", "", 4, "2026-01-01", listOf(io.github.ruddy2003.vestigium.data.CambioRelacion("2026-06-01", "amistad", 5)))))))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = alto, nightMode = if (oscuro) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot(nombre) { Lienzo(cont) }
    }
    @Test fun vidaRomano() = v73(TemaId.ROMANO, false, 4200) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaVida(navVacia) }
    @Test fun vidaCiber() = v73(TemaId.CIBER, true, 4200) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaVida(navVacia) }
    @Test fun personasBotanico() = v73(TemaId.BOTANICO, false, 3200) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaPersonas(navVacia) }
    @Test fun sustanciasGotico() = v73(TemaId.GOTICO, true, 3200) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaSustancias(navVacia) }
    @Test fun sustanciasRomano() = v73(TemaId.ROMANO, false, 3200) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaSustancias(navVacia) }
    @Test fun diarioV73Seda() = v73(TemaId.SEDA, false, 2600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
    @Test fun diaV73Romano() = v73(TemaId.ROMANO, false, 8000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }
    @Test fun cintaV73Laboratorio() = v73(TemaId.LABORATORIO, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaCinta(navVacia) }
    @Test fun libretaMomentosMedieval() = v73(TemaId.MEDIEVAL, false, 2600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "momentos") }
    @Test fun hojaMomentosRomano() = v73(TemaId.ROMANO, false, 2400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaHojaMomentos(navVacia) }
    @Test fun logrosConHallAndalus() = v73(TemaId.ANDALUS, true, 4400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) }
    @Test fun momentosV73Napoleon() = v73(TemaId.NAPOLEON, false, 2600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaMomentos(navVacia) }

    // ---------------- v77: reloj de enfoque, trofeos ----------------
    private fun planEnfoque(segmento: Int, haceMin: Int, esperando: Boolean = false, hechoMin: Int = 0): io.github.ruddy2003.vestigium.data.PlanEnfoque {
        val ahora = System.currentTimeMillis()
        return io.github.ruddy2003.vestigium.data.PlanEnfoque("tesis", 120, 3, 10, inicio = ahora - 90 * 60_000L, segmento = segmento,
            seg_inicio = if (esperando) null else ahora - haceMin * 60_000L, seg_primer_inicio = if (esperando) null else ahora - haceMin * 60_000L,
            esperando = esperando, hecho_ms = hechoMin * 60_000L)
    }
    @Test fun enfoquePlanearRomano() = v73(TemaId.ROMANO, false, 2400) { io.github.ruddy2003.vestigium.ui.RelojEnfoque(false) {} }
    @Test fun enfoqueSiestaGotico() = v73(TemaId.GOTICO, true, 2000) { io.github.ruddy2003.vestigium.ui.RelojEnfoque(true) {} }
    @Test fun enfoqueCorriendoCiber() = v73(TemaId.CIBER, true, 2400, { it.copy(enfoque_plan = planEnfoque(0, 12)) }) { io.github.ruddy2003.vestigium.ui.RelojEnfoque(false) {} }
    @Test fun enfoqueDescansoBotanico() = v73(TemaId.BOTANICO, false, 2400, { it.copy(enfoque_plan = planEnfoque(1, 4, hechoMin = 30)) }) { io.github.ruddy2003.vestigium.ui.RelojEnfoque(false) {} }
    @Test fun enfoqueEsperandoSeda() = v73(TemaId.SEDA, false, 2400, { it.copy(enfoque_plan = planEnfoque(2, 0, esperando = true, hechoMin = 30)) }) { io.github.ruddy2003.vestigium.ui.RelojEnfoque(false) {} }
    @Test fun trofeosRomano() = v73(TemaId.ROMANO, false, 7000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) }
    @Test fun trofeosOrden() = v73(TemaId.ORDEN, true, 5000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) }

    // ---------------- lote 1 (v79): Revolución, Imperio, trofeos y hall por tema, mapas de campaña, libretas, reporte visual ----------------
    @Test fun revolucionClaro() = captura(TemaId.REVOLUCION, false)
    @Test fun revolucionOscuro() = captura(TemaId.REVOLUCION, true)
    @Test fun diaRevolucion() = dia(TemaId.REVOLUCION, false)

    /** Los trofeos de las nueve categorías en los siete temas históricos (el último de cada fila, sin conseguir). */
    private fun trofeosTemas(oscuro: Boolean) = hojaTemas(io.github.ruddy2003.vestigium.ui.TrofeosTema.TEMAS.toList(), porParte = 24, alto = { 100 + it * 200 }, oscuro = oscuro) { temas ->
        Column(Modifier.padding(6.dp)) {
            temas.forEach { t ->
                androidx.compose.material3.MaterialTheme(colorScheme = io.github.ruddy2003.vestigium.ui.esquemaDe(t, oscuro), typography = io.github.ruddy2003.vestigium.ui.tipoDe(t, oscuro)) {
                    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 4.dp)) {
                        Text(t.nombre, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            io.github.ruddy2003.vestigium.ciencia.Logros.CATEGORIAS.forEachIndexed { k, cat ->
                                val l = io.github.ruddy2003.vestigium.ciencia.Logros.Logro("x_$k", cat, "", "★", cat, k % 4 + 1, 1.0, if (k == 8) 0.3 else 1.0, if (k == 8) null else "2026-10-19")
                                io.github.ruddy2003.vestigium.ui.pantallas.TrofeoLogro(l, 46.dp, tema = t)
                            }
                        }
                    }
                }
            }
        }
    }
    @Test fun trofeosTemasClaro() = trofeosTemas(false)
    @Test fun trofeosTemasOscuro() = trofeosTemas(true)
    @Test fun trofeosRevolucion() = v73(TemaId.REVOLUCION, false, 6000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) }

    @Test fun hallRevolucion() = hall(TemaId.REVOLUCION, false)
    @Test fun hallNapoleon() = hall(TemaId.NAPOLEON, true)
    @Test fun hallGotico() = hall(TemaId.GOTICO, false)
    @Test fun hallRenacimiento() = hall(TemaId.RENACIMIENTO, false)
    @Test fun hallHeleno() = hall(TemaId.HELENO, true)
    private fun hallImagen(tema: TemaId, oscuro: Boolean, textos: Boolean, nombre: String? = null) = v67(tema, oscuro, 2700, { it.copy(hall_logros = listOf("diario_30", "ciudades_2", "paises_5", "unesco_1", "continentes_2", "pasos_dia_10000", "racha_diario_7", "diario_7")) }, nombre) {
        mundoEjemplo(); io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono = true
        val ctx = androidx.compose.ui.platform.LocalContext.current
        val cs = MaterialTheme.colorScheme
        val r = androidx.compose.runtime.remember {
            runCatching {
                val todos = io.github.ruddy2003.vestigium.ciencia.Logros.todos(ctx)
                val enHall = Repo.config.hall_logros.mapNotNull { id -> todos.firstOrNull { it.id == id && it.hecho } }
                android.graphics.BitmapFactory.decodeFile(io.github.ruddy2003.vestigium.ui.pantallas.HallImagen.crear(ctx, enHall, tema, cs, textos).path)
            }
        }
        val b = r.getOrNull()
        if (b != null) androidx.compose.foundation.Image(b.asImageBitmap(), null, Modifier.fillMaxWidth())
        else Text("Falló: ${r.exceptionOrNull()}")
    }
    @Test fun hallImagenRevolucion() = hallImagen(TemaId.REVOLUCION, false, true)
    @Test fun hallImagenRomanoSinTextos() = hallImagen(TemaId.ROMANO, true, false)

    @Test fun campanasRevolucion() = campanas(TemaId.REVOLUCION, false)
    @Test fun campanasGotico() = campanas(TemaId.GOTICO, true)
    @Test fun campanasRenacimiento() = campanas(TemaId.RENACIMIENTO, false)
    @Test fun campanasMedieval() = campanas(TemaId.MEDIEVAL, false)
    @Test fun campanasHeleno() = campanas(TemaId.HELENO, true)

    private fun libreta(tema: TemaId, oscuro: Boolean, nombre: String? = null) = v73(tema, oscuro, 2600, nombre = nombre) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "momentos") }
    @Test fun libretaRomano() = libreta(TemaId.ROMANO, false)
    @Test fun libretaHeleno() = libreta(TemaId.HELENO, false)
    @Test fun libretaGotico() = libreta(TemaId.GOTICO, true)
    @Test fun libretaRenacimiento() = libreta(TemaId.RENACIMIENTO, false)
    @Test fun libretaNapoleon() = libreta(TemaId.NAPOLEON, false)
    @Test fun libretaRevolucion() = libreta(TemaId.REVOLUCION, false)

    @Test fun reporteSemanaRevolucion() = reporte(TemaId.REVOLUCION, false, 5200, false)
    @Test fun reporteSemanaNapoleon() = reporte(TemaId.NAPOLEON, true, 5200, false)

    @Test fun tusTemasRomano() = v73(TemaId.ROMANO, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaTusTemas(navVacia) }
    @Test fun presenciasBotanico() = v73(TemaId.BOTANICO, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaPresencias(navVacia) }
    @Test fun otrasVidasRevolucion() = v73(TemaId.REVOLUCION, false, 4000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaOtrasVidas(navVacia) }
    @Test fun libroDiarioRevolucion() = v73(TemaId.REVOLUCION, false, 2600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
    @Test fun temasAjustes() = hojaTemas(porParte = 30, alto = { 100 + (it + 2) / 3 * 443 }, base = TemaId.REVOLUCION) { temas ->
        Column(Modifier.padding(8.dp)) {
            temas.chunked(3).forEach { fila ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    fila.forEach { t -> VistaPreviaTema(t, false, t == TemaId.REVOLUCION, Modifier.weight(1f)) {} }
                    repeat(3 - fila.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
    }
    // ---------------------------------------------------------------- lote 2 (v80)
    @Test fun l2HallAndalus() = hall(TemaId.ANDALUS, false)
    @Test fun l2LibretaAndalus() = libreta(TemaId.ANDALUS, false)
    @Test fun l2CampanaAndalus() = campanas(TemaId.ANDALUS, false)
    @Test fun l2HallSeda() = hall(TemaId.SEDA, true)
    @Test fun l2LibretaSeda() = libreta(TemaId.SEDA, true)
    @Test fun l2CampanaSeda() = campanas(TemaId.SEDA, true)
    @Test fun l2HallMongol() = hall(TemaId.MONGOL, false)
    @Test fun l2LibretaMongol() = libreta(TemaId.MONGOL, false)
    @Test fun l2CampanaMongol() = campanas(TemaId.MONGOL, false)
    @Test fun l2HallColonos() = hall(TemaId.COLONOS, false)
    @Test fun l2LibretaColonos() = libreta(TemaId.COLONOS, false)
    @Test fun l2CampanaColonos() = campanas(TemaId.COLONOS, false)
    @Test fun l2HallBotanico() = hall(TemaId.BOTANICO, false)
    @Test fun l2LibretaBotanico() = libreta(TemaId.BOTANICO, false)
    @Test fun l2CampanaBotanico() = campanas(TemaId.BOTANICO, false)
    @Test fun l2HallNavidad() = hall(TemaId.NAVIDAD, true)
    @Test fun l2LibretaNavidad() = libreta(TemaId.NAVIDAD, true)
    @Test fun l2CampanaNavidad() = campanas(TemaId.NAVIDAD, true)
    @Test fun l2HallAntiguas() = hall(TemaId.ANTIGUAS, false)
    @Test fun l2LibretaAntiguas() = libreta(TemaId.ANTIGUAS, false)
    @Test fun l2CampanaAntiguas() = campanas(TemaId.ANTIGUAS, false)
    @Test fun antiguasClaro() = captura(TemaId.ANTIGUAS, false)
    @Test fun antiguasOscuro() = captura(TemaId.ANTIGUAS, true)
    @Test fun diaAntiguas() = dia(TemaId.ANTIGUAS, false)
    @Test fun widgetsAntiguas() = widgets(TemaId.ANTIGUAS)
    @Test fun trofeosAntiguas() = v73(TemaId.ANTIGUAS, false, 6000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) }
    @Test fun reporteSemanaAntiguas() = reporte(TemaId.ANTIGUAS, true, 5200, false)
    private fun reporteMes(tema: TemaId, oscuro: Boolean, nombre: String? = null) = v73(tema, oscuro, 7600, nombre = nombre) {
        val ctx = androidx.compose.ui.platform.LocalContext.current
        val r = androidx.compose.runtime.remember { runCatching { io.github.ruddy2003.vestigium.reportes.Reportes.imagenMes(ctx, io.github.ruddy2003.vestigium.data.Fechas.diaLogico().take(7)) } }
        val b = r.getOrNull()
        if (b != null) androidx.compose.foundation.Image(b.asImageBitmap(), null, Modifier.fillMaxWidth())
        else Text("Falló: ${r.exceptionOrNull()}")
    }
    @Test fun reporteMesRomano() = reporteMes(TemaId.ROMANO, false)
    @Test fun reporteMesAntiguas() = reporteMes(TemaId.ANTIGUAS, true)
    @Test fun reporteMesBotanico() = reporteMes(TemaId.BOTANICO, false)
    private fun notasYMetas() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        Repo.notasEnMemoria(listOf(
            io.github.ruddy2003.vestigium.data.Nota("n1", hoy, hoy, "Probar escribir a mano antes de dormir; con @ana salió la idea de #lectura.", "idea", fijada = true),
            io.github.ruddy2003.vestigium.data.Nota("n2", hoy, hoy, "Que el lunes empiece sin el teléfono en la cama.", "proposito"),
            io.github.ruddy2003.vestigium.data.Nota("n3", hoy, io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -2), "Llamar a @luis por lo del viaje.", "recordatorio", recordar_el = hoy),
            io.github.ruddy2003.vestigium.data.Nota("n4", hoy, io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -5), "Me gusta más el parque temprano, cuando no hay nadie.", "pensamiento")),
            listOf(
                io.github.ruddy2003.vestigium.data.Meta("m1", "Dormir 7 horas", "Entre semana", "corto", hoy, medida_tipo = "variable", medida_clave = "sueno_horas", medida_modo = "dias", umbral = 7.0, objetivo = 20.0),
                io.github.ruddy2003.vestigium.data.Meta("m2", "Leer 12 libros", "", "largo", hoy, fecha_objetivo = hoy.take(4) + "-12-31", progreso_manual = 40, anio_proposito = hoy.take(4).toInt()),
                io.github.ruddy2003.vestigium.data.Meta("m3", "Correr 5 km", "", "mediano", hoy, estado = "pausada", progreso_manual = 15)))
    }
    @Test fun notasAntiguas() = v73(TemaId.ANTIGUAS, false, 3000) { notasYMetas(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaNotas(navVacia) }
    @Test fun notasRomano() = v73(TemaId.ROMANO, true, 3000) { notasYMetas(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaNotas(navVacia) }
    @Test fun metasBotanico() = v73(TemaId.BOTANICO, false, 3600) { notasYMetas(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMetas(navVacia) }
    @Test fun metasCiber() = v73(TemaId.CIBER, true, 3600) { notasYMetas(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMetas(navVacia) }
    @Test fun coberturaLaboratorio() = v73(TemaId.LABORATORIO, false, 4200) { io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono = true; io.github.ruddy2003.vestigium.ui.pantallas.PantallaCoberturaVariables(navVacia) }
    @Test fun lugarNuevoSeda() = v73(TemaId.SEDA, false, 3000, { c -> c.copy(lugares_candidatos = listOf(io.github.ruddy2003.vestigium.data.CandidatoLugar(22.17, -100.97,
        System.currentTimeMillis() - 50 * 60_000L, System.currentTimeMillis() - 10 * 60_000L, preguntas = 1, sugerencia = "Avenida Carranza"))) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLugarNuevo(navVacia) }
    @Test fun semanaCortaAntiguas() = v73(TemaId.ANTIGUAS, true, 2400, { c -> c.copy(semana_corta = true) }) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
            io.github.ruddy2003.vestigium.ui.pantallas.SemanaCorta(io.github.ruddy2003.vestigium.data.Dia(fecha = io.github.ruddy2003.vestigium.data.Fechas.diaLogico())) {}
        }
    }
    @Test fun recaidasLibidoMedieval() = v67(TemaId.MEDIEVAL, false, 4400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaRecaidas(navVacia) }
    // ---------------------------------------------------------------- lote 3 (v81)
    @Test fun l3HallGotico() = hall(TemaId.GOTICO, true)
    @Test fun l3HallNapoleon() = hall(TemaId.NAPOLEON, false)
    @Test fun l3HallDistopia() = hall(TemaId.DISTOPIA, false)
    @Test fun l3HallRomano() = hall(TemaId.ROMANO, false)
    @Test fun l3HallSeda() = hall(TemaId.SEDA, true)
    @Test fun l3HallBotanico() = hall(TemaId.BOTANICO, false)
    @Test fun l3HallCiber() = hall(TemaId.CIBER, true)
    @Test fun l3LibretaDistopia() = libreta(TemaId.DISTOPIA, false)
    @Test fun l3CampanaDistopia() = campanas(TemaId.DISTOPIA, false)
    @Test fun l3CampanaDistopiaOscuro() = campanas(TemaId.DISTOPIA, true)
    @Test fun l3TrofeosDistopia() = v73(TemaId.DISTOPIA, false, 6000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) }
    @Test fun l3AnioLibrosGotico() = tarjetaAnio(TemaId.GOTICO, true, "libros")
    @Test fun l3AnioLibrosNapoleon() = tarjetaAnio(TemaId.NAPOLEON, false, "libros")
    @Test fun l3AnioLibrosDistopia() = tarjetaAnio(TemaId.DISTOPIA, false, "libros")
    @Test fun l3AnioLibrosRomano() = tarjetaAnio(TemaId.ROMANO, false, "libros")
    @Test fun l3AnioLibrosSeda() = tarjetaAnio(TemaId.SEDA, true, "libros")
    @Test fun l3AnioLibrosBotanico() = tarjetaAnio(TemaId.BOTANICO, false, "libros")
    @Test fun l3AnioLibrosAntiguas() = tarjetaAnio(TemaId.ANTIGUAS, false, "libros")
    @Test fun l3AnioCosasRomano() = tarjetaAnio(TemaId.ROMANO, false, "cosas")
    @Test fun l3AnioCosasCiber() = tarjetaAnio(TemaId.CIBER, true, "cosas")
    private fun datosLote3() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        val tomas = (0 until 40).filter { it % 6 != 5 }.map { i ->
            val f = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -i.toLong())
            io.github.ruddy2003.vestigium.data.TomaSuplemento("t$i", "creatina", f + "T08:30:00-06:00", f, if (i > 33) 20.0 else 5.0)
        } + listOf(io.github.ruddy2003.vestigium.data.TomaSuplemento("m1", "magnesio", io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -1) + "T22:00:00-06:00", io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -1), 300.0))
        val r = java.util.Random(3)
        val laptop = (0 until 14).associate { i ->
            val f = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -i.toLong())
            f to io.github.ruddy2003.vestigium.data.LaptopDia(f, apps = mapOf("chrome" to 60.0 + r.nextInt(80), "claude" to 20.0 + r.nextInt(70), "winword" to r.nextInt(60).toDouble(),
                "minecraft" to (if (i % 5 == 0) 70.0 else 0.0), "otras apps" to 10.0 + r.nextInt(30)).filterValues { it > 0 },
                sitios = mapOf("gemini.google.com" to 5.0 + r.nextInt(20), "keep.google.com" to r.nextInt(15).toDouble(), "youtube.com" to r.nextInt(40).toDouble(), "otros sitios" to 10.0 + r.nextInt(30)).filterValues { it > 0 })
        }
        Repo.lote3EnMemoria(tomas, laptop)
    }
    @Test fun l3SuplementosRomano() = v73(TemaId.ROMANO, false, 3200, { c -> c.copy(suplementos = io.github.ruddy2003.vestigium.data.Suplementos.CATALOGO.take(3), suplementos_hora = 9) }) {
        datosLote3(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaSuplementos(navVacia) }
    @Test fun l3SuplementosDistopia() = v73(TemaId.DISTOPIA, true, 2400) { datosLote3(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaSuplementos(navVacia) }
    @Test fun l3LaptopGotico() = v73(TemaId.GOTICO, false, 3600) { datosLote3(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaLaptop(navVacia) }
    @Test fun l3RedBotanico() = v73(TemaId.BOTANICO, false, 3000, { c -> c.copy(vinculos = listOf(io.github.ruddy2003.vestigium.data.Vinculo("ana", "luis", "colegas"),
        io.github.ruddy2003.vestigium.data.Vinculo("ana", "marta", "familia"))) }) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaRed(navVacia) }
    @Test fun l3RedCiber() = v73(TemaId.CIBER, true, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaRed(navVacia) }
    @Test fun l3ExperimentosSeda() = v73(TemaId.SEDA, false, 4200) {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        Repo.experimentosEnMemoria(listOf(io.github.ruddy2003.vestigium.data.Experimento("e1", "Hora de dormir", "Estar en la cama antes de las 00:00", "valencia",
            io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.lunesDe(hoy), -28), 6, listOf(true, false, false, true, true, false),
            pregunta = "¿Dormirme antes de las 00:00 mejora cómo me siento?", hipotesis = "Me sentiré un poco mejor al día siguiente", sellada = hoy + "T21:10:00-06:00", lavado_dias = 1)))
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaExperimentos(navVacia)
    }
    @Test fun l3GuiaNapoleon() = v73(TemaId.NAPOLEON, false, 3400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaGuiaUso(navVacia, "diario") }
    @Test fun l3GuiaCompletaRomano() = v73(TemaId.ROMANO, true, 3400) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaGuiaUso(navVacia) }

    // ---------------------------------------------------------------- capturas por tema (plan de diseño, lote 0)

    /**
     * Una superficie en todos los temas, en claro y en oscuro (el que solo existe en oscuro, una vez). Cada imagen se llama
     * `<superficie>_<tema>_<claro|oscuro>`. Son muchas, así que solo corren las superficies que pide la variable CAPTURAS:
     * una lista separada por comas («huella,campana») o «todas». En Actions se pasa con la entrada `superficies` de
     * capturas.yml; sin ella, estas pruebas se saltan y las capturas de siempre no cambian.
     */
    // Un alto menor que el ancho (1080) hace que Paparazzi gire la captura: aquí ninguna baja de 1200.
    private fun porTema(superficie: String, captura: (tema: TemaId, oscuro: Boolean, nombre: String) -> Unit) {
        val pedidas = (System.getenv("CAPTURAS") ?: "").split(',').map { it.trim() }
        org.junit.Assume.assumeTrue("«$superficie» no está en CAPTURAS", "todas" in pedidas || superficie in pedidas)
        TemaId.entries.forEach { t ->
            (if (t.soloOscuro) listOf(true) else listOf(false, true)).forEach { o -> captura(t, o, "${t.id}_${if (o) "oscuro" else "claro"}") }
        }
    }
    private val logroEjemplo = io.github.ruddy2003.vestigium.ciencia.Logros.Logro("paises_5", "Pasaporte con sellos", "Visita 5 países", "⚐", "Mundo", 2, 5.0, 5.0, "2026-10-19")

    // Campaña, salón, hall y trofeos.
    @Test fun temaTrofeos() = porTema("trofeos") { t, o, n -> v73(t, o, 7000, nombre = n) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) } }
    @Test fun temaSalon() = porTema("salon") { t, o, n -> hall(t, o, n) }
    @Test fun temaPiezas() = porTema("piezas") { t, o, n -> piezas(t, o, n) }
    @Test fun temaHallImagen() = porTema("hallImagen") { t, o, n -> hallImagen(t, o, true, n) }
    @Test fun temaCampana() = porTema("campana") { t, o, n -> campanas(t, o, n) }
    @Test fun temaLibreta() = porTema("libreta") { t, o, n -> libreta(t, o, n) }

    // Animación y celebración: un instante quieto de cada una.
    @Test fun temaFondoVivo() = porTema("fondoVivo") { t, o, n -> captura(t, o, 2400, n) { io.github.ruddy2003.vestigium.ui.FondoVivo(t, fijo = 0.4f) } }
    @Test fun temaFiesta() = porTema("fiesta") { t, o, n ->
        captura(t, o, 2400, n + "_lluvia") { Box(Modifier.fillMaxSize()) { io.github.ruddy2003.vestigium.ui.CapaFiesta(fijo = 0.45f) } }
        v67(t, o, 2400, nombre = n + "_logro") {
            io.github.ruddy2003.vestigium.ui.Celebracion.mostrar(listOf(logroEjemplo))
            Box(Modifier.fillMaxSize()) { io.github.ruddy2003.vestigium.ui.CapaLogro() }
        }
    }
    @Test fun temaRelacion() = porTema("relacion") { t, o, n -> relaciones(t, o, n) }

    // Huella, ícono del lanzador y formas del mapa.
    @Test fun temaHuella() = porTema("huella") { t, o, n ->
        pantalla(t, o, 2400, n) { io.github.ruddy2003.vestigium.ui.VistaHuella(1f, io.github.ruddy2003.vestigium.data.Fechas.diaLogico()) }
    }
    /** El ícono del lanzador (fondo + huella; cuadrado completo y recortado en círculo), el de las notificaciones y los de las pestañas. */
    @Test fun temaIcono() = porTema("icono") { t, o, n -> captura(t, o, 1200, n) {
        val ctx = androidx.compose.ui.platform.LocalContext.current
        fun id(nombre: String) = ctx.resources.getIdentifier(nombre, "drawable", ctx.packageName)
        val capas: @Composable (Modifier) -> Unit = { m ->
            Box(m) {
                listOf("logo_fondo_${t.id}", "logo_${t.id}").map { id(it) }.filter { it != 0 }.forEach {
                    androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(it), null, Modifier.fillMaxSize(), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                }
            }
        }
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                capas(Modifier.size(130.dp))
                Box(Modifier.size(120.dp).clip(androidx.compose.foundation.shape.CircleShape)) { capas(Modifier.fillMaxSize().graphicsLayer(scaleX = 1.5f, scaleY = 1.5f)) }
                id("ic_stat").takeIf { it != 0 }?.let {
                    androidx.compose.material3.Icon(androidx.compose.ui.res.painterResource(it), null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onBackground)
                }
            }
            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                androidx.compose.runtime.CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.primary) {
                    listOf("dia", "diario", "registrar", "vida", "entender", "mas").forEach { io.github.ruddy2003.vestigium.ui.IconoPestana(it, t) }
                }
            }
        }
    } }
    /** Las figuras con que el mapa marca los lugares (`Formas`) y el marco con su rosa de los vientos (`MarcoMapa`), sin el mapa de osmdroid. */
    @Test fun temaMapaFormas() = porTema("mapaFormas") { t, o, n -> captura(t, o, 1400, n) {
        val e = MaterialTheme.colorScheme
        Box(Modifier.fillMaxWidth().height(460.dp)) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                val relleno = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = e.primary.toArgb() }
                val borde = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { style = android.graphics.Paint.Style.STROKE; strokeWidth = 4f; color = e.onBackground.toArgb() }
                drawIntoCanvas { c ->
                    listOf(0.2f to 0.3f, 0.45f to 0.55f, 0.7f to 0.35f, 0.3f to 0.75f, 0.62f to 0.8f).forEachIndexed { i, (x, y) ->
                        io.github.ruddy2003.vestigium.ui.pantallas.Formas.dibujar(c.nativeCanvas, t, size.width * x, size.height * y, (14 + 8 * i).dp.toPx(), relleno, if (i % 2 == 0) borde else null)
                    }
                }
            }
            io.github.ruddy2003.vestigium.ui.pantallas.MarcoMapa(t, Modifier.fillMaxSize())
        }
    } }

    // Galerías, reportes y Tu año.
    @Test fun temaCalificacion() = porTema("calificacion") { t, o, n -> captura(t, o, 1200, n) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(2, 7, 10).forEach { v -> io.github.ruddy2003.vestigium.ui.pantallas.Calificacion(v, tam = 26.dp, tema = t) }
        }
    } }
    @Test fun temaReporte() = porTema("reporte") { t, o, n -> reporte(t, o, 5200, false, n) }
    @Test fun temaReporteMes() = porTema("reporteMes") { t, o, n -> reporteMes(t, o, n) }
    @Test fun temaVozAnio() = porTema("vozAnio") { t, o, n -> tarjetaAnio(t, o, "portada", n) }

    // Pantallas genéricas y gráficas.
    @Test fun temaCalendario() = porTema("calendario") { t, o, n -> v73(t, o, 3000, nombre = n) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaCalendario(navVacia) } }
    @Test fun temaConstelacion() = porTema("constelacion") { t, o, n -> v67(t, o, 3000, nombre = n) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaConstelacion(navVacia) } }
    /** Las medallas de logro (los cuatro niveles y una a medias) y las de las otras vidas. */
    @Test fun temaMedalla() = porTema("medalla") { t, o, n -> captura(t, o, 1200, n) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                (1..5).forEach { k ->
                    val l = io.github.ruddy2003.vestigium.ciencia.Logros.Logro("m$k", "Logro", "", "★", "Diario", k.coerceAtMost(4), 1.0, if (k == 5) 0.4 else 1.0, if (k == 5) null else "2026-10-19")
                    io.github.ruddy2003.vestigium.ui.pantallas.MedallaLogro(l, 64.dp)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                io.github.ruddy2003.vestigium.data.OtrasVidas.DE_TEMAS.take(5).forEach { io.github.ruddy2003.vestigium.ui.pantallas.MedallaVida(it, 58.dp) }
            }
        }
    } }
    @Test fun temaGraficas() = porTema("graficas") { t, o, n -> pantalla(t, o, 3000, n) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaSalud(navVacia) } }

    // Controles, barra, enfoque, bloqueo y cartas.
    /**
     * Los diálogos de Material se abren en otra ventana y Paparazzi no los dibuja; hasta que el lote 4 cree `Dialogo` con su
     * contenido a la vista, aquí sale la barra de Deshacer, que es el otro control que flota sobre la pantalla.
     */
    @Test fun temaDialogo() = porTema("dialogo") { t, o, n ->
        io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Se borró un momento") {}
        captura(t, o, 1200, n) { Box(Modifier.fillMaxSize()) { CapaDeshacer() } }
        io.github.ruddy2003.vestigium.ui.Deshacer.deshacer()
    }
    @Test fun temaBarra() = porTema("barra") { t, o, n -> captura(t, o, 1200, n) {
        Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("dia", "diario", "vida").forEach { io.github.ruddy2003.vestigium.ui.BarraPestanas(it, t, {}, {}) }
        }
    } }
    @Test fun temaEnfoque() = porTema("enfoque") { t, o, n ->
        v73(t, o, 2400, { it.copy(enfoque_plan = planEnfoque(0, 12)) }, n) { io.github.ruddy2003.vestigium.ui.RelojEnfoque(false) {} }
    }
    @Test fun temaBloqueo() = porTema("bloqueo") { t, o, n -> captura(t, o, 2400, n) { io.github.ruddy2003.vestigium.ui.Bloqueo {} } }
    @Test fun temaCartas() = porTema("cartas") { t, o, n -> cartas(t, o, n) }

    // Lote 3: superficies que antes no tenían captura por tema.
    /** La campaña ganada (escena de la meta) y otra a medio camino (el marcador). */
    @Test fun temaCampanaVictoria() = porTema("campanaVictoria") { t, o, n -> captura(t, o, 1400, n) {
        val f = io.github.ruddy2003.vestigium.data.Fechas
        val l = f.lunesDe(f.diaLogico())
        val c = io.github.ruddy2003.vestigium.data.Campana("cv", l, "diario", 5, t.id)
        val dias = (0 until 7).map { f.sumar(l, it.toLong()) }
        Column(Modifier.padding(16.dp)) {
            io.github.ruddy2003.vestigium.ui.pantallas.MapaCampana(io.github.ruddy2003.vestigium.ciencia.Campanas.Estado(c, null, dias.map { it to true }, 7, true, false, true, dias[4]), tema = t)
            io.github.ruddy2003.vestigium.ui.pantallas.MapaCampana(io.github.ruddy2003.vestigium.ciencia.Campanas.Estado(c, null, dias.mapIndexed { i, d -> d to (if (i < 3) true else null) }, 3, false, false, false, null), Modifier.padding(top = 16.dp), tema = t)
        }
    } }
    /** Emblema grande y chico, la textura del fondo (la pone Lienzo) y los botones de arriba. */
    @Test fun temaDecoracion() = porTema("decoracion") { t, o, n -> captura(t, o, 1600, n) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            io.github.ruddy2003.vestigium.ui.Emblema(t, 200.dp, animado = false)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                io.github.ruddy2003.vestigium.ui.Emblema(t, 30.dp, animado = false)
                io.github.ruddy2003.vestigium.ui.BotonModo()
                io.github.ruddy2003.vestigium.ui.BotonBuscar {}
                io.github.ruddy2003.vestigium.ui.BotonAjustes {}
            }
        }
    } }
    /** Los componentes en columna: banda, secciones (abierta y plegada), tarjeta, campos, escala, opciones, listas, editor, cifras, menú, pastillas, vacío, aviso y botones. */
    @Test fun temaComponentes() = porTema("componentes") { t, o, n -> captura(t, o, 3800, n) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            io.github.ruddy2003.vestigium.ui.Meandro(Modifier.padding(top = 12.dp))
            Seccion("Sueño", "Lo que recuerdas al despertar.", numero = 1, plegable = true) {
                Tarjeta {
                    Etiqueta("Calidad")
                    io.github.ruddy2003.vestigium.ui.Escala(4, 1, 7, anclas = mapOf(1 to "muy mal", 4 to "regular", 7 to "muy bien")) {}
                    Clave("somnus", "7 h 24 · profundo 1 h 32")
                    Interruptor("Recordé un sueño", true, "Se guarda con su valencia y temas.") {}
                    Interruptor("Fue una pesadilla", false) {}
                }
            }
            Seccion("Notas", numero = 2, plegable = true, plegadaPorDefecto = true, resumen = "3 notas") {}
            Seccion("Registro", numero = 3) {
                io.github.ruddy2003.vestigium.ui.Opciones(listOf(io.github.ruddy2003.vestigium.data.Opcion("a", "En casa", "Sin salir"), io.github.ruddy2003.vestigium.data.Opcion("b", "Fuera", "Calle o trabajo")), "a") {}
                Etiqueta("Etiquetas")
                io.github.ruddy2003.vestigium.ui.ListaEtiquetas(listOf("tesis", "escalar"), "nueva etiqueta", prefijo = "#", normalizar = { it.trim() }) {}
                io.github.ruddy2003.vestigium.ui.ListaEtiquetas(emptyList(), "nueva etiqueta", normalizar = { it.trim() }) {}
                Etiqueta("Diario")
                io.github.ruddy2003.vestigium.ui.Editor("Hoy avancé el capítulo dos.\nEn la tarde fui a escalar.", "somnium.txt", "Escribe…", minimo = 110, pie = "9 verba") {}
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Cifra("7.4 h", "sueño", Modifier.weight(1f)); Cifra("9,812", "pasos", Modifier.weight(1f)); Cifra("\$245", "gasto", Modifier.weight(1f))
                }
                io.github.ruddy2003.vestigium.ui.FilaMenu("Respaldos", "Copia en el teléfono") {}
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { io.github.ruddy2003.vestigium.ui.Pastilla("nuevo"); io.github.ruddy2003.vestigium.ui.Pastilla("3 días", MaterialTheme.colorScheme.secondary) }
                io.github.ruddy2003.vestigium.ui.Vacio("Aún no hay registros este mes.")
                io.github.ruddy2003.vestigium.ui.Aviso("Hay 2 días sin respaldo.")
                Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { BotonPrincipal("Guardar") {}; BotonSecundario("cancelar") {} }
            }
        }
    } }
    /** La vista previa del tema, sin elegir y elegida. */
    @Test fun temaPrevia() = porTema("previa") { t, o, n -> captura(t, o, 1200, n) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VistaPreviaTema(t, o, false, Modifier.weight(1f)) {}
            VistaPreviaTema(t, o, true, Modifier.weight(1f)) {}
            Box(Modifier.weight(1f))
        }
    } }
    /** El fondo de pantalla del teléfono (marca y marco). */
    @Test fun temaFondoTelefono() = porTema("fondoTelefono") { t, o, n -> captura(t, o, 2400, n) { io.github.ruddy2003.vestigium.ui.FondoTelefonoVista(t, o, Modifier.fillMaxSize()) } }
    /** El personaje con su globo y las marcas de @persona y #tema. */
    @Test fun temaPersonaje() = porTema("personaje") { t, o, n -> pantalla(t, o, 1400, n) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            io.github.ruddy2003.vestigium.ui.TarjetaPersonaje(io.github.ruddy2003.vestigium.data.Fechas.diaLogico())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    io.github.ruddy2003.vestigium.ui.Personaje.Estado(false, 6.5, 8, false, false, false, false, ""),
                    io.github.ruddy2003.vestigium.ui.Personaje.Estado(true, 3.0, 31, true, true, false, true, ""),
                ).forEach { io.github.ruddy2003.vestigium.ui.FiguraPersonaje(it, tam = 120.dp, tema = t) }
            }
            io.github.ruddy2003.vestigium.ui.TextoMarcado("Café con @ana y @luis_m después del #protocolo de la #tesis", style = MaterialTheme.typography.bodyLarge, tema = t)
        }
    } }
    /** La lámina para rascar del mapa del mundo. */
    @Test fun temaLamina() = porTema("lamina") { t, o, n -> captura(t, o, 1400, n) {
        Column(Modifier.padding(16.dp)) {
            io.github.ruddy2003.vestigium.ui.pantallas.MapaRascar(setOf("MX", "ES", "FR", "JP"), Modifier.fillMaxWidth().aspectRatio(2.1f))
        }
    } }
    @Test fun temaDia() = porTema("dia") { t, o, n -> v73(t, o, 4200, nombre = n) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) } }
    /** Frase, franja y cifras de Hoy; en oscuro la frase va guardada, para ver el corazón relleno. */
    @Test fun temaHoy() = porTema("hoy") { t, o, n ->
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        v73(t, o, 2600, extra = { c -> if (o) c.copy(frases_guardadas = listOf(io.github.ruddy2003.vestigium.ui.Frases.del(hoy, t).let { f ->
            io.github.ruddy2003.vestigium.data.FraseGuardada(f.texto, f.original, f.autor, t.id, hoy) })) else c }, nombre = n) {
            Column(Modifier.padding(16.dp)) {
                io.github.ruddy2003.vestigium.ui.pantallas.FraseDelDia(hoy)
                io.github.ruddy2003.vestigium.ui.pantallas.FranjaDias(hoy, abrir = {}, ponerse = {})
                io.github.ruddy2003.vestigium.ui.pantallas.Tablero(hoy, max = 4)
            }
        }
    }
    /** La carta abierta (papelería, capitular, filete y sello) y la hoja de la libreta de momentos. */
    @Test fun temaCartaAbierta() = porTema("cartaAbierta") { t, o, n -> pantalla(t, o, 2400, n) {
        Repo.configEnMemoria(Repo.config.copy(perfil = io.github.ruddy2003.vestigium.data.Perfil(nombre = "Ana")))
        cartasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.carta = "k4"
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaCarta(navVacia)
    } }
    @Test fun temaHojaLibreta() = porTema("hojaLibreta") { t, o, n -> v73(t, o, 2400, nombre = n) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaHojaMomentos(navVacia) } }
    /** El diario: objeto cerrado de día y de noche, el estante, el estante de sueños bajo su cielo, la página abierta y el escritor del sueño. */
    @Test fun temaLibros() = porTema("libros") { t, o, n ->
        val dias = listOf("2026-10-12", "2026-10-13", "2026-10-14", "2026-10-15", "2026-10-16").mapIndexed { i, f ->
            io.github.ruddy2003.vestigium.data.Dia(fecha = f, valencia = i * 2 - 4, nota = TEXTO.take(60 + i * 90), sueno_texto = "Volaba sobre un río.", sueno_valencia = 2 - i)
        }
        captura(t, o, 3200, n + "_estante") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                io.github.ruddy2003.vestigium.ui.ObjetoDiario("2026-10-16", TEXTO, tema = t) {}
                io.github.ruddy2003.vestigium.ui.ObjetoDiario("2026-10-16", "", tema = t, noche = true) {}
                io.github.ruddy2003.vestigium.ui.EstanteDiario(dias, t) {}
                io.github.ruddy2003.vestigium.ui.CieloNoche(tema = t) { io.github.ruddy2003.vestigium.ui.EstanteDiario(dias, t, noche = true) {} }
            }
        }
        captura(t, o, 2400, n + "_pagina") {
            io.github.ruddy2003.vestigium.ui.LecturaTema("2026-10-16", dias.last().copy(mejor_momento = "La caminata.", peor_momento = "El tráfico."), {}, null, null, emptyList(), tema = t)
        }
        captura(t, o, 2400, n + "_sueno") {
            io.github.ruddy2003.vestigium.ui.EscritorTema("2026-10-16", "", {}, {}, t, modo = io.github.ruddy2003.vestigium.ui.Escrito.SUENO)
        }
    }
    @Test fun temaLibrosAnio() = porTema("librosAnio") { t, o, n -> tarjetaAnio(t, o, "libros", n) }
    @Test fun temaCosasAnio() = porTema("cosasAnio") { t, o, n -> tarjetaAnio(t, o, "cosas", n) }
    /** «Más» (recuadros con el marco del tema), «Mapas» y la galería de obras. */
    @Test fun temaGalerias() = porTema("galerias") { t, o, n ->
        pantalla(t, o, 3800, n + "_mas") { mundoEjemplo(); obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
        pantalla(t, o, 3400, n + "_mapas") { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMapas(navVacia) }
        pantalla(t, o, 3000, n + "_obras") { obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaObras(navVacia) }
    }
    @Test fun temaBibliotecas() = porTema("bibliotecas") { t, o, n -> pantalla(t, o, 3000, n) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "diario") } }
    /** Los widgets en modo discreto (sus títulos), con la forma del tema y el calendario del mes. */
    @Test fun temaWidgets() = porTema("widgets") { t, o, n ->
        Ejemplo.cargar()
        Repo.configEnMemoria(config(t, o).copy(discreto = true))
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_6.copy(screenHeight = 2600, nightMode = if (o) NightMode.NIGHT else NightMode.NOTNIGHT))
        val ctx = paparazzi.context
        val col = android.widget.LinearLayout(ctx).apply { orientation = android.widget.LinearLayout.VERTICAL; setBackgroundColor(0xFF5A6B7A.toInt()); setPadding(30, 30, 30, 30) }
        fun agregar(rv: android.widget.RemoteViews, alto: Int) {
            col.addView(rv.apply(ctx, col), android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, alto).apply { bottomMargin = 24 })
        }
        val w = io.github.ruddy2003.vestigium.widget.Widgets
        agregar(w.vistaAnimo(ctx), 330)
        agregar(w.vistaMomento(ctx), 420)
        agregar(w.vistaSueno(ctx), 330)
        agregar(w.vistaMes(ctx), 900)
        agregar(w.vistaHabitos(ctx), 420)
        paparazzi.snapshot(col, n)
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
        val lugares = listOf(io.github.ruddy2003.vestigium.data.Lugar("casa", "Casa", 22.15, -100.98, tipo = "casa"), io.github.ruddy2003.vestigium.data.Lugar("col", "Colegio", 22.16, -100.99, tipo = "escuela"),
            io.github.ruddy2003.vestigium.data.Lugar("gym", "Gimnasio", 22.145, -100.965, tipo = "ejercicio"), io.github.ruddy2003.vestigium.data.Lugar("parque", "Parque", 22.135, -101.0, tipo = "naturaleza"),
            io.github.ruddy2003.vestigium.data.Lugar("cafe", "Café", 22.152, -100.975, tipo = "social"))
        val visitas = listOf(
            io.github.ruddy2003.vestigium.data.Visita("v1", "casa", iso(hoy, 0, 0), iso(hoy, 8, 40)),
            io.github.ruddy2003.vestigium.data.Visita("v2", "col", iso(hoy, 9, 5), iso(hoy, 14, 30)),
            io.github.ruddy2003.vestigium.data.Visita("v3", "casa", iso(hoy, 15, 0), null),
        ) + fechas.drop(1).take(20).flatMapIndexed { i, f -> listOfNotNull(
            io.github.ruddy2003.vestigium.data.Visita("vc$i", "col", iso(f, 9, 0), iso(f, 14, 0)),
            if (i % 2 == 0) io.github.ruddy2003.vestigium.data.Visita("vg$i", "gym", iso(f, 18, 0), iso(f, 19, 30)) else null,
            if (i % 5 == 0) io.github.ruddy2003.vestigium.data.Visita("vp$i", "parque", iso(f, 16, 0), iso(f, 17, 40)) else null,
            if (i % 3 == 1) io.github.ruddy2003.vestigium.data.Visita("vf$i", "cafe", iso(f, 15, 0), iso(f, 16, 10)) else null,
        ) }
        val momentos = listOf(
            io.github.ruddy2003.vestigium.data.Momento("m1", iso(hoy, 11, 47), hoy, "muestreo", valencia = 5),
            io.github.ruddy2003.vestigium.data.Momento("m2", iso(hoy, 16, 20), hoy, "preocupacion", texto = "entrega", valencia = 3),
        )
        val sesiones = listOf(io.github.ruddy2003.vestigium.data.Sesion("s1", hoy, iso(hoy, 18, 30), iso(hoy, 19, 40), 1, "Pesas", min = 70))
        val mesActual = io.github.ruddy2003.vestigium.data.Fechas.hoyCalendario().take(7)
        val ingresos = listOf(
            io.github.ruddy2003.vestigium.data.Ingreso("i1", "$mesActual-01", "09:00", 9500.0, "Sueldo", "quincena", true),
            io.github.ruddy2003.vestigium.data.Ingreso("i2", "$mesActual-15", "09:00", 9500.0, "Sueldo", "quincena", true),
            io.github.ruddy2003.vestigium.data.Ingreso("i3", "$mesActual-10", "18:30", 1200.0, "Trabajo extra", "diseño de un cartel"))
        Repo.ejemploEnMemoria(dias, salud, pantalla, gastos, momentos, notifs, entorno, nutricion, lugares, visitas, sesiones, ingresos)
    }

}
