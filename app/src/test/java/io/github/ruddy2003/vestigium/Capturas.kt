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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.lineaMeta
import io.github.ruddy2003.vestigium.ui.pincelDeCifras
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import io.github.ruddy2003.vestigium.ui.rejillaGrafica
import io.github.ruddy2003.vestigium.ui.VistaPreviaTema
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.fondoTema
import org.junit.Rule
import org.junit.Test

/** Capturas de una pantalla de muestra en cada tema, para revisar el diseño sin teléfono. */
class Capturas {
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, theme = "android:Theme.Material.NoActionBar", showSystemUi = false, useDeviceResolution = true)

    /** Paparazzi, envuelto: en una corrida «solo» las capturas de siempre se saltan antes de prepararlo ([CapturasAPedido]). */
    @get:Rule
    val reglas: org.junit.rules.TestRule = CapturasAPedido.regla(paparazzi)

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

    private fun objetos(temas: List<TemaId>, nombre: String) = captura(temas.first(), false, alto = 1200, nombre = nombre) {
        Column(Modifier.padding(16.dp)) {
            temas.forEachIndexed { i, t -> io.github.ruddy2003.vestigium.ui.ObjetoDiario("2026-10-16", if (i == 1) "" else TEXTO, Modifier.padding(bottom = 14.dp), t) {} }
        }
    }
    /** El diario cerrado de cada tema, con texto y vacío (una captura por tema). */
    @Test fun objetos() = TemaId.entries.forEach { t -> objetos(listOf(t, t), "tema_${t.id}") }

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
    /** El Ideario con ideas inventadas: de una obra (y una de sus citas), de un video, contadas y propias. */
    private fun ideasEjemplo() {
        obrasEjemplo()
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        fun d(k: Long) = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -k)
        fun i(id: String, k: Long, texto: String, origen: String, titulo: String = "", quien: List<String> = emptyList(), obra: String? = null, cita: String? = null,
              fuente: String = "", donde: String = "", postura: String = "", fijada: Boolean = false) =
            io.github.ruddy2003.vestigium.data.Idea(id, d(k) + "T21:15:00-06:00", d(k), texto, titulo, origen, quien, obra, cita, fuente, "", donde, postura, fijada = fijada)
        Repo.ideasEnMemoria(listOf(
            i("i1", 0, "Un objeto se puede describir por cómo se relaciona con otros: el mismo vaso es peso para la báscula y alivio para la sed. #fisica #filosofia",
                "visto", "Las cosas son sus relaciones", listOf("Física inventada", "Entrevistador inventado"), fuente = "Una entrevista en video", donde = "41:20", postura = "piensa"),
            i("i2", 2, "La libertad se defiende con actos pequeños, no solo con discursos. #filosofia", "leido", quien = listOf("Miguel de Cervantes"), obra = "o1", cita = "c1",
                donde = "II, 58", postura = "convence", fijada = true),
            i("i3", 9, "Dormir poco no solo cansa: también vuelve más intensas las emociones negativas al día siguiente. #sueno", "leido", obra = "o5", postura = "duda"),
            i("i4", 33, "Explicar algo en voz alta muestra qué parte todavía no entiendo. #aprender", "propia", postura = "convence"),
            i("i5", 40, "Los mercados también coordinan a desconocidos, no solo intercambian cosas.", "contado", quien = listOf("Un profesor"), postura = "desacuerdo"),
        ))
    }
    private fun ideario(tema: TemaId, oscuro: Boolean, vista: String = "mes") = pantalla(tema, oscuro, 3600) {
        ideasEjemplo()
        if (vista == "mes") io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "ideas")
        else io.github.ruddy2003.vestigium.ui.Hoja("Ideario", {}) { io.github.ruddy2003.vestigium.ui.pantallas.BibliotecaIdeas(navVacia, { it }, vistaInicial = vista) }
    }
    @Test fun idearioMedieval() = ideario(TemaId.MEDIEVAL, false)
    @Test fun idearioCiber() = ideario(TemaId.CIBER, true)
    @Test fun idearioTemasBotanico() = ideario(TemaId.BOTANICO, false, "temas")
    @Test fun idearioGriego() = ideario(TemaId.HELENO, false)
    @Test fun ideaRenacimiento() = pantalla(TemaId.RENACIMIENTO, false, 3000) {
        ideasEjemplo()
        io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.idea = Repo.ideas.first { it.id == "i2" }
        io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.contexto = "«La libertad, Sancho, es uno de los más preciosos dones que a los hombres dieron los cielos.»"
        io.github.ruddy2003.vestigium.ui.pantallas.EscritorIdea(navVacia)
    }
    @Test fun ideaNuevaRomano() = pantalla(TemaId.ROMANO, true, 2200) {
        ideasEjemplo()
        io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.idea = null; io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.contexto = null
        io.github.ruddy2003.vestigium.ui.pantallas.EscritorIdea(navVacia)
    }
    /** Más ideas en un mismo tema, para ver la cuadrícula del contenedor llena. */
    private fun ideasArchivo() {
        ideasEjemplo()
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        val extra = listOf(
            "Lo que no depende de mí no es asunto mío: me toca cómo respondo.", "Escribir la preocupación la vuelve más pequeña.",
            "Un hábito se sostiene mejor si es fácil de empezar que si es heroico.", "La atención es el gasto más caro del día.",
            "Pedir ayuda también es una forma de fuerza.", "Lo urgente casi nunca es lo importante.",
        ).mapIndexed { k, t ->
            val f = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -(k * 3L + 1))
            io.github.ruddy2003.vestigium.data.Idea("e$k", f + "T20:00:00-06:00", f, t + " #filosofia", if (k % 2 == 0) "Idea ${k + 1}" else "", if (k % 3 == 0) "propia" else "leido",
                postura = listOf("convence", "piensa", "duda", "")[k % 4], fijada = k == 1)
        }
        Repo.ideasEnMemoria(Repo.ideas + extra)
        io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.cajon = "filosofia"
        io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.fichaIdea = "i2"
    }

    /** El archivo del Ideario: el fichero (sus contenedores), un contenedor abierto en cuadrícula y una ficha abierta. */
    private fun archivo(t: TemaId, oscuro: Boolean, nombre: String?) = pantalla(t, oscuro, 3900, nombre) {
        ideasArchivo()
        Column {
            Box(Modifier.padding(16.dp)) { io.github.ruddy2003.vestigium.ui.pantallas.FicheroIdeas(navVacia, Repo.ideas, max = 4) }
            Box(Modifier.fillMaxWidth().height(600.dp)) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaCajonIdeas(navVacia) }
            Box(Modifier.fillMaxWidth().height(560.dp)) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaFichaIdea(navVacia) }
        }
    }
    /** Una muestra de siempre, de épocas distintas; los 34 temas, claro y oscuro, con CAPTURAS=archivo ([temaArchivo]). */
    @Test fun archivoIdeas() = listOf(TemaId.ROMANO, TemaId.EDO, TemaId.TEMPLARIO, TemaId.NAVIDAD, TemaId.DECO, TemaId.CIBER, TemaId.NEON, TemaId.CUEVA).forEach { t ->
        archivo(t, t.soloOscuro, "tema_${t.id}")
    }
    /** En modo oscuro, el papel de las fichas (antes salía de un gris raro). */
    @Test fun archivoIdeasOscuro() = listOf(TemaId.MEDIEVAL, TemaId.BOTANICO, TemaId.URBE, TemaId.EDO).forEach { t -> archivo(t, true, "tema_${t.id}") }
    /**
     * Ideas inventadas repartidas en [n] temas, cada uno con más o menos fichas; uno de nombre largo y, con más de uno, el
     * último es el cajón de las ideas sin tema.
     */
    private fun ideasConTemas(n: Int): List<io.github.ruddy2003.vestigium.data.Idea> {
        val temas = listOf("filosofia", "dinero", "historia_de_la_ciencia_y_de_la_tecnica", "salud", "aprender", "fisica", "amistad", "escritura", "cocina")
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        return (0 until n).flatMap { k ->
            (0..(k * 3 % 7)).map { j ->
                val f = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -(k * 2L + j))
                val sinTema = n > 1 && k == n - 1
                io.github.ruddy2003.vestigium.data.Idea("m${k}_$j", f + "T19:00:00-06:00", f, "Una idea inventada número ${j + 1}.",
                    temas = if (sinTema) emptyList() else listOf(temas[k % temas.size]))
            }
        }
    }
    /**
     * El mueble del Ideario: vacío, con 1 y con 5 temas; con 9; y la vista corta (6 y «y N más»). En tres capturas: con muchos
     * degradados en una sola imagen, el simulador de Paparazzi pinta de negro los últimos (en el teléfono no pasa).
     */
    private fun mueble(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        listOf("a" to listOf(0, 1, 5), "b" to listOf(9), "c" to emptyList()).forEach { (parte, ns) ->
            pantalla(t, oscuro, 2600, "tema_${t.id}_${modo}_$parte") {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    ns.forEach { n -> io.github.ruddy2003.vestigium.ui.pantallas.FicheroIdeas(navVacia, ideasConTemas(n)) }
                    if (ns.isEmpty()) io.github.ruddy2003.vestigium.ui.pantallas.FicheroIdeas(navVacia, ideasConTemas(9), max = 6)
                }
            }
        }
    }
    /** Los muebles del lote 0, en claro y en oscuro (los 34, con CAPTURAS=mueble, [temaMueble]). */
    @Test fun muebleIdeas() = listOf(TemaId.EDO, TemaId.CUEVA).forEach { t -> mueble(t, false); mueble(t, true) }
    /** El archivo de los temas del lote 0, en oscuro (el claro ya sale en [archivoIdeas]). */
    @Test fun archivoIdeasPropio() = listOf(TemaId.EDO, TemaId.CUEVA).forEach { t -> archivo(t, true, "propio_${t.id}") }
    /** Escribir una idea en los temas del lote 0, con el reverso a la vista. */
    @Test fun escritorIdeaPropio() = listOf(TemaId.EDO, TemaId.CUEVA).forEach { t ->
        pantalla(t, false, 3400, "propio_${t.id}") {
            ideasEjemplo()
            io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.idea = Repo.ideas.first { it.id == "i2" }; io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.contexto = null
            io.github.ruddy2003.vestigium.ui.pantallas.EscritorIdea(navVacia)
        }
    }
    /** La biblioteca del Ideario entera en los temas del lote 0: el mueble, las fijadas y la lista por mes. */
    @Test fun idearioPropio() = listOf(TemaId.EDO, TemaId.CUEVA).forEach { t -> pantalla(t, false, 4000, "propio_${t.id}") {
        ideasArchivo()
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "ideas")
    } }
    /** Los temas del lote 1 del Ideario: Antigüedad y fantasía. */
    private val LOTE1 = listOf(TemaId.ROMANO, TemaId.HELENO, TemaId.EGIPTO, TemaId.MESO, TemaId.ARCANO, TemaId.ANTIGUAS)
    @Test fun muebleIdeasLote1() = LOTE1.forEach { t -> mueble(t, false); mueble(t, true) }
    @Test fun archivoIdeasLote1() = LOTE1.forEach { t -> archivo(t, false, "lote1_${t.id}_claro"); archivo(t, true, "lote1_${t.id}_oscuro") }
    @Test fun escritorIdeaLote1() = LOTE1.forEach { t ->
        pantalla(t, false, 3400, "lote1_${t.id}") {
            ideasEjemplo()
            io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.idea = Repo.ideas.first { it.id == "i2" }; io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.contexto = null
            io.github.ruddy2003.vestigium.ui.pantallas.EscritorIdea(navVacia)
        }
    }
    @Test fun idearioLote1() = LOTE1.forEach { t -> pantalla(t, false, 4000, "lote1_${t.id}") {
        ideasArchivo()
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "ideas")
    } }
    /** Los temas del lote 2 del Ideario: Edad Media y Moderna. */
    private val LOTE2 = listOf(TemaId.MEDIEVAL, TemaId.TEMPLARIO, TemaId.SEDA, TemaId.GOTICO, TemaId.ANDALUS, TemaId.MONGOL, TemaId.RENACIMIENTO, TemaId.PIRATA, TemaId.REVOLUCION)
    @Test fun muebleIdeasLote2() = LOTE2.forEach { t -> mueble(t, false); mueble(t, true) }
    @Test fun archivoIdeasLote2() = LOTE2.forEach { t -> archivo(t, false, "lote2_${t.id}_claro"); archivo(t, true, "lote2_${t.id}_oscuro") }
    @Test fun escritorIdeaLote2() = LOTE2.forEach { t ->
        pantalla(t, false, 3400, "lote2_${t.id}") {
            ideasEjemplo()
            io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.idea = Repo.ideas.first { it.id == "i2" }; io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.contexto = null
            io.github.ruddy2003.vestigium.ui.pantallas.EscritorIdea(navVacia)
        }
    }
    @Test fun idearioLote2() = LOTE2.forEach { t -> pantalla(t, false, 4000, "lote2_${t.id}") {
        ideasArchivo()
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "ideas")
    } }
    /** Los temas del lote 3 del Ideario: Edad Contemporánea. */
    private val LOTE3 = listOf(TemaId.URBE, TemaId.CIBER, TemaId.VAPOR, TemaId.NAPOLEON, TemaId.DECO, TemaId.BOTANICO, TemaId.NAVIDAD, TemaId.ABISMO, TemaId.URSS)
    @Test fun muebleIdeasLote3() = LOTE3.forEach { t -> mueble(t, t.soloOscuro); if (!t.soloOscuro) mueble(t, true) }
    @Test fun archivoIdeasLote3() = LOTE3.forEach { t -> archivo(t, t.soloOscuro, "lote3_${t.id}_claro"); if (!t.soloOscuro) archivo(t, true, "lote3_${t.id}_oscuro") }
    @Test fun escritorIdeaLote3() = LOTE3.forEach { t ->
        pantalla(t, t.soloOscuro, 3400, "lote3_${t.id}") {
            ideasEjemplo()
            io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.idea = Repo.ideas.first { it.id == "i2" }; io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.contexto = null
            io.github.ruddy2003.vestigium.ui.pantallas.EscritorIdea(navVacia)
        }
    }
    @Test fun idearioLote3() = LOTE3.forEach { t -> pantalla(t, t.soloOscuro, 4000, "lote3_${t.id}") {
        ideasArchivo()
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "ideas")
    } }
    /** Los temas del lote 4 del Ideario: Futuro. */
    private val LOTE4 = listOf(TemaId.NEON, TemaId.DISTOPIA, TemaId.REFUGIO, TemaId.ORDEN, TemaId.LABORATORIO, TemaId.COLONOS, TemaId.RUINAS, TemaId.ESPACIO)
    @Test fun muebleIdeasLote4() = LOTE4.forEach { t -> mueble(t, t.soloOscuro); if (!t.soloOscuro) mueble(t, true) }
    @Test fun archivoIdeasLote4() = LOTE4.forEach { t -> archivo(t, t.soloOscuro, "lote4_${t.id}_claro"); if (!t.soloOscuro) archivo(t, true, "lote4_${t.id}_oscuro") }
    @Test fun escritorIdeaLote4() = LOTE4.forEach { t ->
        pantalla(t, t.soloOscuro, 3400, "lote4_${t.id}") {
            ideasEjemplo()
            io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.idea = Repo.ideas.first { it.id == "i2" }; io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.contexto = null
            io.github.ruddy2003.vestigium.ui.pantallas.EscritorIdea(navVacia)
        }
    }
    @Test fun idearioLote4() = LOTE4.forEach { t -> pantalla(t, t.soloOscuro, 4000, "lote4_${t.id}") {
        ideasArchivo()
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "ideas")
    } }

    // ---------------------------------------------------------------- Bibliotecas: el estante del diario (fase A)

    /** [n] días inventados de octubre y noviembre, con más o menos palabras, ánimo y sueño. */
    private fun diasEstante(n: Int, desde: Int = 0) = (0 until n).map { i ->
        val k = desde + i
        val fecha = if (k < 31) "2026-10-%02d".format(31 - k) else "2026-09-%02d".format(30 - (k - 31) % 30)
        io.github.ruddy2003.vestigium.data.Dia(fecha = fecha, valencia = (k * 3) % 7 - 3, nota = TEXTO.repeat(1 + k % 5).take(40 + (k * 211) % 1600),
            sueno_texto = "Volaba sobre un río. ".repeat(1 + k % 6), sueno_valencia = 3 - (k * 2) % 7)
    }
    /** Los temas con la biblioteca nueva. */
    private val ESTANTE_A0 = listOf(TemaId.EDO, TemaId.DISTOPIA)
    /**
     * El estante de un tema: (a) con 1 y con 12 entradas; (b) 40 entradas en tres meses, con sus rótulos; (c) de noche, bajo
     * su cielo. En partes: con muchos degradados en una imagen, Paparazzi pinta de negro los últimos.
     */
    private fun estante(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(t)
        captura(t, oscuro, 2200, "tema_${t.id}_${modo}_a") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                io.github.ruddy2003.vestigium.ui.EstanteDiario(diasEstante(1), t) {}
                io.github.ruddy2003.vestigium.ui.EstanteDiario(diasEstante(12), t) {}
            }
        }
        captura(t, oscuro, 2600, "tema_${t.id}_${modo}_b") {
            val todos = diasEstante(40)
            val grupos = todos.groupBy { it.fecha.take(7) }.map { (mes, l) -> io.github.ruddy2003.vestigium.ui.biblioteca.GrupoEstante(if (mes.endsWith("10")) "octubre · 2026" else "septiembre · 2026", l) } +
                io.github.ruddy2003.vestigium.ui.biblioteca.GrupoEstante("un mes con un nombre muy largo · 2026", diasEstante(3, 50))
            Box(Modifier.padding(16.dp)) { io.github.ruddy2003.vestigium.ui.biblioteca.EstanteTema(d, grupos, false, {}) }
        }
        captura(t, oscuro, 2000, "tema_${t.id}_${modo}_c") {
            Box(Modifier.padding(16.dp)) {
                io.github.ruddy2003.vestigium.ui.CieloNoche(tema = t) {
                    io.github.ruddy2003.vestigium.ui.biblioteca.EstanteTema(d, listOf(io.github.ruddy2003.vestigium.ui.biblioteca.GrupoEstante("octubre · 2026", diasEstante(12))), true, {})
                }
            }
        }
    }
    @Test fun estanteA0() = ESTANTE_A0.forEach { t -> estante(t, t.soloOscuro); if (!t.soloOscuro) estante(t, true) }
    /** La biblioteca del diario entera, con el estante por meses. */
    @Test fun bibliotecaDiarioA0() = ESTANTE_A0.forEach { t -> pantalla(t, false, 3600, "a0_${t.id}") {
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "diario")
    } }
    /** El estante de los otros 32 temas, por épocas (lote A1), en claro y en oscuro; los de solo oscuro, solo en oscuro. */
    private val ESTANTE_A1A = listOf(TemaId.CUEVA, TemaId.ROMANO, TemaId.HELENO, TemaId.EGIPTO, TemaId.MESO, TemaId.ARCANO, TemaId.ANTIGUAS)
    private val ESTANTE_A1B = listOf(TemaId.MEDIEVAL, TemaId.TEMPLARIO, TemaId.SEDA, TemaId.GOTICO, TemaId.ANDALUS, TemaId.MONGOL, TemaId.RENACIMIENTO, TemaId.PIRATA, TemaId.REVOLUCION)
    private val ESTANTE_A1C = listOf(TemaId.URBE, TemaId.CIBER, TemaId.VAPOR, TemaId.NAPOLEON, TemaId.DECO, TemaId.BOTANICO, TemaId.NAVIDAD, TemaId.ABISMO, TemaId.URSS)
    private val ESTANTE_A1D = listOf(TemaId.NEON, TemaId.REFUGIO, TemaId.ORDEN, TemaId.LABORATORIO, TemaId.COLONOS, TemaId.RUINAS, TemaId.ESPACIO)
    @Test fun estanteA1a() = ESTANTE_A1A.forEach { t -> estante(t, t.soloOscuro); if (!t.soloOscuro) estante(t, true) }
    @Test fun estanteA1b() = ESTANTE_A1B.forEach { t -> estante(t, t.soloOscuro); if (!t.soloOscuro) estante(t, true) }
    @Test fun estanteA1c() = ESTANTE_A1C.forEach { t -> estante(t, t.soloOscuro); if (!t.soloOscuro) estante(t, true) }
    @Test fun estanteA1d() = ESTANTE_A1D.forEach { t -> estante(t, t.soloOscuro); if (!t.soloOscuro) estante(t, true) }
    /**
     * La tapa del diario (lote A2a): con texto, en blanco, de noche y a medio abrir, en claro y en oscuro (los de solo
     * oscuro, solo en oscuro).
     */
    private fun tapa(t: TemaId, oscuro: Boolean) = captura(t, oscuro, 2700, "tema_${t.id}_${if (oscuro) "oscuro" else "claro"}") {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            io.github.ruddy2003.vestigium.ui.TapaDiario("2026-10-16", TEXTO, tema = t)
            io.github.ruddy2003.vestigium.ui.TapaDiario("2026-10-17", "", tema = t)
            io.github.ruddy2003.vestigium.ui.TapaDiario("2026-10-16", "Volaba sobre un río y el agua subía hasta el puente.", tema = t, noche = true)
            io.github.ruddy2003.vestigium.ui.TapaDiario("2026-10-16", TEXTO, tema = t) { 0.55f }
        }
    }
    @Test fun tapaA2a() = ESTANTE_A1A.forEach { t -> tapa(t, t.soloOscuro); if (!t.soloOscuro) tapa(t, true) }
    @Test fun tapaA2b() = (ESTANTE_A1B + TemaId.EDO).forEach { t -> tapa(t, t.soloOscuro); if (!t.soloOscuro) tapa(t, true) }
    @Test fun tapaA2c() = ESTANTE_A1C.forEach { t -> tapa(t, t.soloOscuro); if (!t.soloOscuro) tapa(t, true) }
    @Test fun tapaA2d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { t -> tapa(t, t.soloOscuro); if (!t.soloOscuro) tapa(t, true) }
    /**
     * La página abierta (lote A2b): la entrada leída con su encabezado y su barra, el sueño leído de noche y el escritor con
     * su botón de guardar, en claro y en oscuro (los de solo oscuro, solo en oscuro).
     */
    private fun paginaAbierta(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val dia = io.github.ruddy2003.vestigium.data.Dia(fecha = "2026-10-16", valencia = 2, nota = TEXTO, mejor_momento = "La caminata.", peor_momento = "El tráfico.",
            sueno_texto = "Volaba sobre un río y el agua subía hasta el puente.", sueno_valencia = 1)
        captura(t, oscuro, 2000, "tema_${t.id}_${modo}_lectura") { io.github.ruddy2003.vestigium.ui.LecturaTema("2026-10-16", dia, {}, {}, null, emptyList(), tema = t) }
        captura(t, oscuro, 1600, "tema_${t.id}_${modo}_sueno") { io.github.ruddy2003.vestigium.ui.LecturaTema("2026-10-16", dia, {}, null, null, emptyList(), tema = t, modo = io.github.ruddy2003.vestigium.ui.Escrito.SUENO) }
        captura(t, oscuro, 1600, "tema_${t.id}_${modo}_escritor") { io.github.ruddy2003.vestigium.ui.EscritorTema("2026-10-16", TEXTO.take(220), {}, {}, t) }
    }
    @Test fun paginaA2a() = ESTANTE_A1A.forEach { t -> paginaAbierta(t, t.soloOscuro); if (!t.soloOscuro) paginaAbierta(t, true) }
    @Test fun paginaA2b() = (ESTANTE_A1B + TemaId.EDO).forEach { t -> paginaAbierta(t, t.soloOscuro); if (!t.soloOscuro) paginaAbierta(t, true) }
    @Test fun paginaA2c() = ESTANTE_A1C.forEach { t -> paginaAbierta(t, t.soloOscuro); if (!t.soloOscuro) paginaAbierta(t, true) }
    @Test fun paginaA2d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { t -> paginaAbierta(t, t.soloOscuro); if (!t.soloOscuro) paginaAbierta(t, true) }
    /**
     * La libreta de momentos (lote A3): sola con 1 y con 40 hojas y a medio abrir; la pestaña Momentos de la biblioteca (la
     * libreta y su índice) y una hoja abierta con sus barras. En claro y en oscuro (los de solo oscuro, solo en oscuro).
     */
    private fun libretaA3(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(t)
        val c = io.github.ruddy2003.vestigium.ui.contenedorDe(t)
        captura(t, oscuro, 2000, "tema_${t.id}_${modo}_sola") {
            val medidor = androidx.compose.ui.text.rememberTextMeasurer()
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(Triple(1, 0f, "1 ${c.hoja} · desde el 6 nov"), Triple(40, 0f, "40 ${io.github.ruddy2003.vestigium.ui.pantallas.pluralHoja(c.hoja)} · desde el 3 sep"), Triple(40, 0.55f, "40 ${io.github.ruddy2003.vestigium.ui.pantallas.pluralHoja(c.hoja)} · desde el 3 sep")).forEach { (h, a, cuenta) ->
                    androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(d.altoLibreta)) {
                        with(d) { libreta(io.github.ruddy2003.vestigium.ui.biblioteca.EstadoLibreta(c.nombre, cuenta, h, a, 7), medidor, oscuro) }
                    }
                }
            }
        }
        v73(t, oscuro, 3000, nombre = "tema_${t.id}_${modo}_biblioteca") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "momentos") }
        v73(t, oscuro, 2000, nombre = "tema_${t.id}_${modo}_hoja") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaHojaMomentos(navVacia) }
    }
    @Test fun libretaA3a() = ESTANTE_A1A.forEach { t -> libretaA3(t, t.soloOscuro); if (!t.soloOscuro) libretaA3(t, true) }
    @Test fun libretaA3b() = (ESTANTE_A1B + TemaId.EDO).forEach { t -> libretaA3(t, t.soloOscuro); if (!t.soloOscuro) libretaA3(t, true) }
    @Test fun libretaA3c() = ESTANTE_A1C.forEach { t -> libretaA3(t, t.soloOscuro); if (!t.soloOscuro) libretaA3(t, true) }
    @Test fun libretaA3d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { t -> libretaA3(t, t.soloOscuro); if (!t.soloOscuro) libretaA3(t, true) }
    /**
     * Las cartas (lote A4): el sobre del tema en cinco momentos (sellado y atado, listo con su brillo, abriéndose con el sello
     * partido, abierto con la carta afuera y cerrándose con el sello que cae), la biblioteca de cartas con su buzón y una
     * carta abierta con su encabezado. En claro y en oscuro (los de solo oscuro, solo en oscuro).
     */
    private fun cartasA4(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(t)
        val pg = io.github.ruddy2003.vestigium.ui.pagina(t)
        captura(t, oscuro, 2500, "tema_${t.id}_${modo}_sobres") {
            val medidor = androidx.compose.ui.text.rememberTextMeasurer()
            // linea1, linea2, abrir, romper, subir, cordón, brillo, escala y alfa del sello, ¿abierta?
            class M(val l1: String, val l2: String, val a: Float, val r: Float, val s: Float, val c: Float, val b: Float, val es: Float, val al: Float, val abierta: Boolean)
            val momentos = listOf(
                M("A mí, dentro de un año", "se abre 7 nov 2027 · en 365 días", 0f, 0f, 0f, 1f, 0f, 1f, 1f, false),
                M("Ya se puede abrir", "escrita el 2 oct 2025", 0f, 0f, 0f, 0f, 0.9f, 1f, 1f, false),
                M("", "", 0.5f, 0.8f, 0f, 0f, 0.3f, 1f, 1f, false),
                M("Para @ana", "2 nov 2026 · 21:40", 1f, 1f, 0.55f, 0f, 0f, 1f, 1f, true),
                M("", "", 0f, 0f, 0f, 0.4f, 0f, 1.6f, 0.6f, false),
            )
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                momentos.forEach { m ->
                    androidx.compose.foundation.Canvas(Modifier.width(300.dp).height(if (m.abierta) 290.dp else 190.dp)) {
                        val cuerpo = if (m.abierta) androidx.compose.ui.geometry.Rect(0f, size.height * 0.34f, size.width, size.height) else androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height)
                        with(d) { sobre(io.github.ruddy2003.vestigium.ui.biblioteca.EstadoCarta(m.l1, m.l2, cuerpo, m.a, m.r, m.s, m.c, m.b, m.es, m.al, pg.color, pg.tinta.copy(alpha = 0.2f), 7, true), medidor, oscuro) }
                    }
                }
            }
        }
        cartas(t, oscuro, "tema_${t.id}_${modo}_biblioteca")
        pantalla(t, oscuro, 2200, "tema_${t.id}_${modo}_carta") {
            Repo.configEnMemoria(Repo.config.copy(perfil = io.github.ruddy2003.vestigium.data.Perfil(nombre = "Ana")))
            cartasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.carta = "k4"
            io.github.ruddy2003.vestigium.ui.pantallas.PantallaCarta(navVacia)
        }
    }
    @Test fun cartasA4a() = ESTANTE_A1A.forEach { t -> cartasA4(t, t.soloOscuro); if (!t.soloOscuro) cartasA4(t, true) }
    @Test fun cartasA4b() = (ESTANTE_A1B + TemaId.EDO).forEach { t -> cartasA4(t, t.soloOscuro); if (!t.soloOscuro) cartasA4(t, true) }
    @Test fun cartasA4c() = ESTANTE_A1C.forEach { t -> cartasA4(t, t.soloOscuro); if (!t.soloOscuro) cartasA4(t, true) }
    // El futuro, tema por tema (si uno falla, la prueba dice cuál).
    private fun cartasA4Tema(t: TemaId) { cartasA4(t, t.soloOscuro); if (!t.soloOscuro) cartasA4(t, true) }
    @Test fun cartasA4Neon() = cartasA4Tema(TemaId.NEON)
    @Test fun cartasA4Refugio() = cartasA4Tema(TemaId.REFUGIO)
    @Test fun cartasA4Orden() = cartasA4Tema(TemaId.ORDEN)
    @Test fun cartasA4Laboratorio() = cartasA4Tema(TemaId.LABORATORIO)
    @Test fun cartasA4Colonos() = cartasA4Tema(TemaId.COLONOS)
    @Test fun cartasA4Ruinas() = cartasA4Tema(TemaId.RUINAS)
    @Test fun cartasA4Espacio() = cartasA4Tema(TemaId.ESPACIO)
    @Test fun cartasA4Distopia() = cartasA4Tema(TemaId.DISTOPIA)
    /**
     * Lo de alrededor (lote A5): las pestañas de las bibliotecas (con la primera, la tercera y la última elegidas), el
     * buscador vacío y con texto, el recuadro de Obras, tapas dibujadas de 40, 78 y 118 dp, y dos tarjetas de «hace un año»
     * (una completa y una con solo la franja); aparte, la galería de Obras. Datos inventados.
     */
    private fun alrededorA5(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(t)
        captura(t, oscuro, 3800, "tema_${t.id}_${modo}_alrededor") {
            obrasEjemplo()
            val medidor = androidx.compose.ui.text.rememberTextMeasurer()
            val nombres = listOf("Diario", "Momentos", "Sueños", "Ideario", "Cartas")
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(0, 2, 4).forEach { k ->
                    androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(d.altoPestanas)) {
                        with(d) { pestanas(io.github.ruddy2003.vestigium.ui.biblioteca.EstadoPestanas(nombres, listOf(128, 46, 0, 12, 7), k, 3), medidor, oscuro) }
                    }
                }
                io.github.ruddy2003.vestigium.ui.biblioteca.CampoTema("", {}, "buscar en tu diario, @personas, #temas")
                io.github.ruddy2003.vestigium.ui.biblioteca.CampoTema("mar y montaña", {}, "buscar en tus sueños")
                io.github.ruddy2003.vestigium.ui.pantallas.RecuadroObras(navVacia)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                    val l = Repo.obras
                    io.github.ruddy2003.vestigium.ui.pantallas.PortadaObra(l[0], Modifier.width(40.dp).aspectRatio(2f / 3f))
                    io.github.ruddy2003.vestigium.ui.pantallas.PortadaObra(l[4], Modifier.width(40.dp).aspectRatio(2f / 3f))
                    io.github.ruddy2003.vestigium.ui.pantallas.PortadaObra(l[2], Modifier.width(78.dp).aspectRatio(2f / 3f))
                    io.github.ruddy2003.vestigium.ui.pantallas.PortadaObra(l[0], Modifier.width(118.dp).aspectRatio(2f / 3f))
                }
                io.github.ruddy2003.vestigium.ui.pantallas.TarjetaRecuerdo(io.github.ruddy2003.vestigium.ui.pantallas.Recuerdo(
                    io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), -365), 1,
                    listOf("Un café inventado", "El parque"), "Una canción · Un grupo", "Fue un día tranquilo, con lluvia por la tarde y una caminata larga.",
                    "☂ lluvia 18°", 5, null)) {}
                io.github.ruddy2003.vestigium.ui.pantallas.TarjetaRecuerdo(io.github.ruddy2003.vestigium.ui.pantallas.Recuerdo(
                    io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), -1096), 3,
                    emptyList(), null, null, "☀ despejado 24°", null, null)) {}
            }
        }
        pantalla(t, oscuro, 4200, "tema_${t.id}_${modo}_obras") { obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaObras(navVacia) }
    }
    private fun alrededorA5Tema(t: TemaId) { alrededorA5(t, t.soloOscuro); if (!t.soloOscuro) alrededorA5(t, true) }
    @Test fun alrededorA5a() = ESTANTE_A1A.forEach { alrededorA5Tema(it) }
    @Test fun alrededorA5b() = (ESTANTE_A1B + TemaId.EDO).forEach { alrededorA5Tema(it) }
    @Test fun alrededorA5c() = ESTANTE_A1C.forEach { alrededorA5Tema(it) }
    @Test fun alrededorA5Neon() = alrededorA5Tema(TemaId.NEON)
    @Test fun alrededorA5Refugio() = alrededorA5Tema(TemaId.REFUGIO)
    @Test fun alrededorA5Orden() = alrededorA5Tema(TemaId.ORDEN)
    @Test fun alrededorA5Laboratorio() = alrededorA5Tema(TemaId.LABORATORIO)
    @Test fun alrededorA5Colonos() = alrededorA5Tema(TemaId.COLONOS)
    @Test fun alrededorA5Ruinas() = alrededorA5Tema(TemaId.RUINAS)
    @Test fun alrededorA5Espacio() = alrededorA5Tema(TemaId.ESPACIO)
    @Test fun alrededorA5Distopia() = alrededorA5Tema(TemaId.DISTOPIA)
    /**
     * Los campos de texto (lote B1): vacío con rótulo y pista, con texto, con error y prefijo, de varios renglones, apagado,
     * con sufijo, la lista de etiquetas y el marco con el foco (de un renglón y de varios), dibujado directo porque en la
     * captura no se puede enfocar. En claro y en oscuro. Datos inventados.
     */
    private fun camposB1(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val d = io.github.ruddy2003.vestigium.ui.comun.disenoComun(t)
        captura(t, oscuro, 3400, "tema_${t.id}_${modo}_campos") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                io.github.ruddy2003.vestigium.ui.comun.CampoTexto("", {}, Modifier.fillMaxWidth(), singleLine = true,
                    label = { Text("Nombre") }, placeholder = { Text("como aparece en tu diario") })
                io.github.ruddy2003.vestigium.ui.comun.CampoTexto("Paseo largo por el parque", {}, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Nota") })
                io.github.ruddy2003.vestigium.ui.comun.CampoTexto("doce", {}, Modifier.fillMaxWidth(), singleLine = true, isError = true,
                    label = { Text("Monto") }, prefix = { Text("$ ") })
                io.github.ruddy2003.vestigium.ui.comun.CampoTexto("Hoy aprendí a cocinar algo nuevo. Salió bien a la segunda y lo repetiría el domingo.", {},
                    Modifier.fillMaxWidth(), minLines = 3, label = { Text("Qué pasó") })
                io.github.ruddy2003.vestigium.ui.comun.CampoTexto("solo lectura", {}, Modifier.fillMaxWidth(), enabled = false, singleLine = true, label = { Text("Apagado") })
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    io.github.ruddy2003.vestigium.ui.comun.CampoTexto("72.5", {}, Modifier.weight(1f), singleLine = true, label = { Text("Peso") }, suffix = { Text(" kg") })
                    io.github.ruddy2003.vestigium.ui.comun.CampoTexto("", {}, Modifier.weight(1f), singleLine = true, placeholder = { Text("0") })
                }
                io.github.ruddy2003.vestigium.ui.ListaEtiquetas(listOf("trabajo", "familia"), "otra etiqueta", normalizar = { it.trim() }) {}
                androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(d.altoCampo)) {
                    with(d) { marcoCampo(io.github.ruddy2003.vestigium.ui.comun.EstadoMarco(true, false, true, false, false, 5), oscuro) }
                }
                androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(110.dp)) {
                    with(d) { marcoCampo(io.github.ruddy2003.vestigium.ui.comun.EstadoMarco(true, false, true, true, false, 6), oscuro) }
                }
            }
        }
    }
    private fun camposB1Tema(t: TemaId) { camposB1(t, t.soloOscuro); if (!t.soloOscuro) camposB1(t, true) }
    @Test fun camposB1a() = ESTANTE_A1A.forEach { camposB1Tema(it) }
    @Test fun camposB1b() = (ESTANTE_A1B + TemaId.EDO).forEach { camposB1Tema(it) }
    @Test fun camposB1c() = ESTANTE_A1C.forEach { camposB1Tema(it) }
    @Test fun camposB1d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { camposB1Tema(it) }
    /**
     * Separadores y botones de texto (lote B2): el separador a todo el ancho, tenue y angosto; una lista corta separada;
     * botones de texto de varios anchos, apagado y presionado (este dibujado directo); y la pantalla Más, que tiene muchos
     * separadores. En claro y en oscuro.
     */
    private fun separadoresB2(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val d = io.github.ruddy2003.vestigium.ui.comun.disenoComun(t)
        captura(t, oscuro, 2600, "tema_${t.id}_${modo}_separadores") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                io.github.ruddy2003.vestigium.ui.comun.Separador()
                io.github.ruddy2003.vestigium.ui.comun.Separador(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                androidx.compose.foundation.layout.Box(Modifier.width(140.dp)) { io.github.ruddy2003.vestigium.ui.comun.Separador() }
                Column {
                    listOf("Pasos", "Sueño", "Ánimo").forEachIndexed { k, x ->
                        if (k > 0) io.github.ruddy2003.vestigium.ui.comun.Separador()
                        Text(x, Modifier.padding(vertical = 10.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    io.github.ruddy2003.vestigium.ui.comun.BotonTexto({}) { Text("×") }
                    io.github.ruddy2003.vestigium.ui.comun.BotonTexto({}) { Text("cancelar") }
                    io.github.ruddy2003.vestigium.ui.comun.BotonTexto({}, enabled = false) { Text("deshacer") }
                }
                io.github.ruddy2003.vestigium.ui.comun.BotonTexto({}, Modifier.fillMaxWidth()) { Text("ver más meses") }
                androidx.compose.foundation.Canvas(Modifier.width(140.dp).height(48.dp)) {
                    with(d) { botonTexto(io.github.ruddy2003.vestigium.ui.comun.EstadoBotonTexto(true, true, 3), oscuro) }
                }
            }
        }
        pantalla(t, oscuro, 4200, "tema_${t.id}_${modo}_mas") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
    }
    private fun separadoresB2Tema(t: TemaId) { separadoresB2(t, t.soloOscuro); if (!t.soloOscuro) separadoresB2(t, true) }
    @Test fun separadoresB2a() = ESTANTE_A1A.forEach { separadoresB2Tema(it) }
    @Test fun separadoresB2b() = (ESTANTE_A1B + TemaId.EDO).forEach { separadoresB2Tema(it) }
    @Test fun separadoresB2c() = ESTANTE_A1C.forEach { separadoresB2Tema(it) }
    @Test fun separadoresB2d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { separadoresB2Tema(it) }
    /**
     * Filas, vacío, aviso y cifra (lote B3): dos filas de menú, una presionada (dibujada directo), dos filas de Más (una
     * fijada), un vacío, un aviso y tres cifras. En claro y en oscuro. Datos inventados.
     */
    private fun filasB3(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val d = io.github.ruddy2003.vestigium.ui.comun.disenoComun(t)
        captura(t, oscuro, 2800, "tema_${t.id}_${modo}_filas") {
            Repo.configEnMemoria(Repo.config.copy(favoritos_mas = Repo.config.favoritos_mas + "gastos"))
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                io.github.ruddy2003.vestigium.ui.FilaMenu("Respaldo automático", "cada noche, en la carpeta elegida") {}
                io.github.ruddy2003.vestigium.ui.FilaMenu("Acerca de") {}
                androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(56.dp)) {
                    with(d) { fila(io.github.ruddy2003.vestigium.ui.comun.EstadoFila(true, false, 4), oscuro) }
                }
                io.github.ruddy2003.vestigium.ui.pantallas.FilaMas("gastos", "Gastos", "Gastos e ingresos del mes") {}
                io.github.ruddy2003.vestigium.ui.pantallas.FilaMas("escalada", "Escalada", null) {}
                io.github.ruddy2003.vestigium.ui.Vacio("Todavía no hay entradas. Escribe la de hoy desde la pestaña Diario.")
                io.github.ruddy2003.vestigium.ui.Aviso("Los datos se guardan solo en este teléfono. Haz un respaldo de vez en cuando.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    io.github.ruddy2003.vestigium.ui.Cifra("128", "entradas", Modifier.width(100.dp))
                    io.github.ruddy2003.vestigium.ui.Cifra("6.4", "ánimo medio", Modifier.width(100.dp))
                    io.github.ruddy2003.vestigium.ui.Cifra("-0.12", "tendencia", Modifier.width(100.dp))
                }
            }
        }
    }
    private fun filasB3Tema(t: TemaId) { filasB3(t, t.soloOscuro); if (!t.soloOscuro) filasB3(t, true) }
    @Test fun filasB3a() = ESTANTE_A1A.forEach { filasB3Tema(it) }
    @Test fun filasB3b() = (ESTANTE_A1B + TemaId.EDO).forEach { filasB3Tema(it) }
    @Test fun filasB3c() = ESTANTE_A1C.forEach { filasB3Tema(it) }
    @Test fun filasB3d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { filasB3Tema(it) }
    /**
     * Las tarjetas (lote B4): «Te falta hoy», el tablero de Hoy y la rejilla de Vida en una hoja; aparte, Día con su tarjeta
     * de la noche. En claro y en oscuro. Datos inventados (los de cada pantalla vacía).
     */
    private fun tarjetasB4(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        captura(t, oscuro, 3600, "tema_${t.id}_${modo}_tarjetas") {
            Column(Modifier.padding(16.dp)) {
                io.github.ruddy2003.vestigium.ui.pantallas.TeFaltaHoy(io.github.ruddy2003.vestigium.data.Dia(fecha = hoy), hoy) {}
                io.github.ruddy2003.vestigium.ui.pantallas.Tablero(hoy, max = 4)
                io.github.ruddy2003.vestigium.ui.pantallas.RejillaTarjetas(listOf(
                    io.github.ruddy2003.vestigium.ui.pantallas.Tarjetita("Resumen semanal", "5.8 ↑", "ánimo de esta semana", "semana"),
                    io.github.ruddy2003.vestigium.ui.pantallas.Tarjetita("Calendario", "12 / 20", "días con diario este mes", "calendario"),
                    io.github.ruddy2003.vestigium.ui.pantallas.Tarjetita("Tu gente", "14", "personas que mencionas", "personas"),
                )) {}
            }
        }
        pantalla(t, oscuro, 4200, "tema_${t.id}_${modo}_dia") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }
    }
    private fun tarjetasB4Tema(t: TemaId) { tarjetasB4(t, t.soloOscuro); if (!t.soloOscuro) tarjetasB4(t, true) }
    @Test fun tarjetasB4a() = ESTANTE_A1A.forEach { tarjetasB4Tema(it) }
    @Test fun tarjetasB4b() = (ESTANTE_A1B + TemaId.EDO).forEach { tarjetasB4Tema(it) }
    @Test fun tarjetasB4c() = ESTANTE_A1C.forEach { tarjetasB4Tema(it) }
    @Test fun tarjetasB4d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { tarjetasB4Tema(it) }
    /** Gastos e ingresos inventados de este mes, para las capturas de Gastos. */
    private fun gastosEjemplo() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        fun d(k: Long) = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -k).let { if (it.take(7) == hoy.take(7)) it else hoy }
        Repo.gastosEnMemoria(listOf(
            io.github.ruddy2003.vestigium.data.Gasto("g1", d(0), "13:20", 85.0, Repo.config.categorias.first(), nota = "comida"),
            io.github.ruddy2003.vestigium.data.Gasto("g2", d(0), "09:05", 32.5, Repo.config.categorias.last(), imprevisto = true),
            io.github.ruddy2003.vestigium.data.Gasto("g3", d(1), "19:40", 240.0, Repo.config.categorias.first(), se_repite = true),
        ), listOf(io.github.ruddy2003.vestigium.data.Ingreso("i1", d(2), "10:00", 1500.0, Repo.config.categorias_ingreso.first(), "", true)))
    }
    /** Gastos (lote B5): la pantalla entera con la balanza, el mes, los movimientos y sus rótulos. En claro y en oscuro. */
    private fun gastosB5(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        pantalla(t, oscuro, 4600, "tema_${t.id}_${modo}_gastos") { gastosEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaGastos(navVacia) }
    }
    private fun gastosB5Tema(t: TemaId) { gastosB5(t, t.soloOscuro); if (!t.soloOscuro) gastosB5(t, true) }
    @Test fun gastosB5a() = ESTANTE_A1A.forEach { gastosB5Tema(it) }
    @Test fun gastosB5b() = (ESTANTE_A1B + TemaId.EDO).forEach { gastosB5Tema(it) }
    @Test fun gastosB5c() = ESTANTE_A1C.forEach { gastosB5Tema(it) }
    @Test fun gastosB5d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { gastosB5Tema(it) }
    /**
     * Pedestales y placa (lote C1): los trofeos del tema en tres tamaños (46, 96 y 180 dp), en los cuatro niveles y sin
     * conseguir; y el hall de logros con sus pedestales y placas. En claro y en oscuro. Datos inventados.
     */
    private fun pedestalesC1(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        captura(t, oscuro, 2200, "tema_${t.id}_${modo}_pedestales") {
            val cats = io.github.ruddy2003.vestigium.ciencia.Logros.CATEGORIAS
            fun logro(k: Int, nivel: Int, hecho: Boolean) = io.github.ruddy2003.vestigium.ciencia.Logros.Logro("x_$k", cats[k % cats.size], "", "★", cats[k % cats.size], nivel, 1.0,
                if (hecho) 1.0 else 0.3, if (hecho) "2026-10-19" else null)
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(46.dp, 96.dp).forEach { alto ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                        (1..4).forEach { n -> io.github.ruddy2003.vestigium.ui.pantallas.TrofeoLogro(logro(n, n, true), alto, tema = t) }
                        io.github.ruddy2003.vestigium.ui.pantallas.TrofeoLogro(logro(5, 1, false), alto, tema = t)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                    io.github.ruddy2003.vestigium.ui.pantallas.TrofeoLogro(logro(2, 3, true), 180.dp, tema = t)
                    io.github.ruddy2003.vestigium.ui.pantallas.TrofeoLogro(logro(6, 4, true), 180.dp, tema = t)
                }
            }
        }
        hall(t, oscuro, "tema_${t.id}_${modo}_hall")
    }
    private fun pedestalesC1Tema(t: TemaId) { pedestalesC1(t, t.soloOscuro); if (!t.soloOscuro) pedestalesC1(t, true) }
    @Test fun pedestalesC1a() = ESTANTE_A1A.forEach { pedestalesC1Tema(it) }
    @Test fun pedestalesC1b() = (ESTANTE_A1B + TemaId.EDO).forEach { pedestalesC1Tema(it) }
    @Test fun pedestalesC1c() = ESTANTE_A1C.forEach { pedestalesC1Tema(it) }
    @Test fun pedestalesC1Neon() = pedestalesC1Tema(TemaId.NEON)
    @Test fun pedestalesC1Refugio() = pedestalesC1Tema(TemaId.REFUGIO)
    @Test fun pedestalesC1Orden() = pedestalesC1Tema(TemaId.ORDEN)
    @Test fun pedestalesC1Laboratorio() = pedestalesC1Tema(TemaId.LABORATORIO)
    @Test fun pedestalesC1Colonos() = pedestalesC1Tema(TemaId.COLONOS)
    @Test fun pedestalesC1Ruinas() = pedestalesC1Tema(TemaId.RUINAS)
    @Test fun pedestalesC1Espacio() = pedestalesC1Tema(TemaId.ESPACIO)
    @Test fun pedestalesC1Distopia() = pedestalesC1Tema(TemaId.DISTOPIA)
    /** El salón del hall (lote C2): el hall de logros y la imagen para compartir (con 3 columnas y textos). En claro y en oscuro. */
    private fun salonesC2(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        hall(t, oscuro, "tema_${t.id}_${modo}_salon")
        hallImagen(t, oscuro, true, "tema_${t.id}_${modo}_imagen")
    }
    private fun salonesC2Tema(t: TemaId) { salonesC2(t, t.soloOscuro); if (!t.soloOscuro) salonesC2(t, true) }
    @Test fun salonesC2a() = ESTANTE_A1A.forEach { salonesC2Tema(it) }
    @Test fun salonesC2b() = (ESTANTE_A1B + TemaId.EDO).forEach { salonesC2Tema(it) }
    @Test fun salonesC2c() = ESTANTE_A1C.forEach { salonesC2Tema(it) }
    @Test fun salonesC2Neon() = salonesC2Tema(TemaId.NEON)
    @Test fun salonesC2Refugio() = salonesC2Tema(TemaId.REFUGIO)
    @Test fun salonesC2Orden() = salonesC2Tema(TemaId.ORDEN)
    @Test fun salonesC2Laboratorio() = salonesC2Tema(TemaId.LABORATORIO)
    @Test fun salonesC2Colonos() = salonesC2Tema(TemaId.COLONOS)
    @Test fun salonesC2Ruinas() = salonesC2Tema(TemaId.RUINAS)
    @Test fun salonesC2Espacio() = salonesC2Tema(TemaId.ESPACIO)
    @Test fun salonesC2Distopia() = salonesC2Tema(TemaId.DISTOPIA)
    /** Las campañas (lote C3): una ganada (con la victoria en la meta) y una a medias (con el marcador). En claro y en oscuro. */
    private fun campanasC3(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        captura(t, oscuro, 1400, "tema_${t.id}_${modo}_campana") {
            val f = io.github.ruddy2003.vestigium.data.Fechas
            val l = f.lunesDe(f.diaLogico())
            val c = io.github.ruddy2003.vestigium.data.Campana("cv", l, "diario", 5, t.id)
            val dias = (0 until 7).map { f.sumar(l, it.toLong()) }
            Column(Modifier.padding(16.dp)) {
                io.github.ruddy2003.vestigium.ui.pantallas.MapaCampana(io.github.ruddy2003.vestigium.ciencia.Campanas.Estado(c, null, dias.map { it to true }, 7, true, false, true, dias[4]), tema = t)
                io.github.ruddy2003.vestigium.ui.pantallas.MapaCampana(io.github.ruddy2003.vestigium.ciencia.Campanas.Estado(c, null, dias.mapIndexed { i, d -> d to (if (i < 3) true else null) }, 3, false, false, false, null), Modifier.padding(top = 16.dp), tema = t)
            }
        }
    }
    private fun campanasC3Tema(t: TemaId) { campanasC3(t, t.soloOscuro); if (!t.soloOscuro) campanasC3(t, true) }
    @Test fun campanasC3a() = ESTANTE_A1A.forEach { campanasC3Tema(it) }
    @Test fun campanasC3b() = (ESTANTE_A1B + TemaId.EDO).forEach { campanasC3Tema(it) }
    @Test fun campanasC3c() = ESTANTE_A1C.forEach { campanasC3Tema(it) }
    @Test fun campanasC3d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { campanasC3Tema(it) }
    /** Los objetos de Tu año (lote C4): la tarjeta de las cosas contadas del año. En claro y en oscuro. */
    private fun anioC4Tema(t: TemaId) {
        tarjetaAnio(t, t.soloOscuro, "cosas", "tema_${t.id}_${if (t.soloOscuro) "oscuro" else "claro"}_cosas")
        if (!t.soloOscuro) tarjetaAnio(t, true, "cosas", "tema_${t.id}_oscuro_cosas")
        tarjetaAnio(t, t.soloOscuro, "sueno", "tema_${t.id}_${if (t.soloOscuro) "oscuro" else "claro"}_sueno")
    }
    @Test fun anioC4a() = ESTANTE_A1A.forEach { anioC4Tema(it) }
    @Test fun anioC4b() = (ESTANTE_A1B + TemaId.EDO).forEach { anioC4Tema(it) }
    @Test fun anioC4c() = ESTANTE_A1C.forEach { anioC4Tema(it) }
    @Test fun anioC4d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { anioC4Tema(it) }
    /**
     * Los adornos (lote D1): la hoja rápida con su menú, tres tarjetas de Relaciones e iconos de la cinta en tres tamaños.
     * En claro y en oscuro.
     */
    private fun adornosD1(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        captura(t, oscuro, 2000, "tema_${t.id}_${modo}_quick") {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.BottomCenter) {
                io.github.ruddy2003.vestigium.ui.HojaQuick(Modifier.fillMaxWidth()) {
                    io.github.ruddy2003.vestigium.ui.EncabezadoQuick("Registrar", "10:32 · sábado", "Lo de todos los días en un toque.")
                    io.github.ruddy2003.vestigium.ui.MenuQuick(listOf("Enfoque" to "enfoque", "Siesta" to "siesta", "Sustancias" to "consumo", "Gasto o ingreso" to "gasto",
                        "Sueño" to "sueno", "Llenar el día" to "diario", "Nota" to "notas")) {}
                }
            }
        }
        relaciones(t, oscuro, "tema_${t.id}_${modo}_relaciones")
        captura(t, oscuro, 700, "tema_${t.id}_${modo}_pistas") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(14.dp, 18.dp, 28.dp).forEach { tam ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("animo", "sueno", "pasos", "comida", "cafe", "nota").forEach { id ->
                            io.github.ruddy2003.vestigium.ui.IconoPista(id, MaterialTheme.colorScheme.primary, tam = tam, tema = t)
                        }
                    }
                }
            }
        }
    }
    private fun adornosD1Tema(t: TemaId) { adornosD1(t, t.soloOscuro); if (!t.soloOscuro) adornosD1(t, true) }
    @Test fun adornosD1a() = ESTANTE_A1A.forEach { adornosD1Tema(it) }
    @Test fun adornosD1b() = (ESTANTE_A1B + TemaId.EDO).forEach { adornosD1Tema(it) }
    @Test fun adornosD1c() = ESTANTE_A1C.forEach { adornosD1Tema(it) }
    @Test fun adornosD1d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { adornosD1Tema(it) }
    /**
     * El pulido por archivo (lote D2): las marcas de @persona y #tema, la banda del título y la textura del fondo (todo
     * captura lleva el fondo del tema), el cielo de noche y la esfera de enfoque. En claro y en oscuro.
     */
    private fun pulidoD2(t: TemaId, oscuro: Boolean) {
        val modo = if (oscuro) "oscuro" else "claro"
        captura(t, oscuro, 1300, "tema_${t.id}_${modo}_marcas") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                io.github.ruddy2003.vestigium.ui.Meandro()
                io.github.ruddy2003.vestigium.ui.TextoMarcado("Café con @ana y @luis_m después del #protocolo de la #tesis", style = MaterialTheme.typography.bodyLarge, tema = t)
                io.github.ruddy2003.vestigium.ui.CieloNoche { Box(Modifier.fillMaxWidth().height(160.dp)) }
            }
        }
        v73(t, oscuro, 2400, { it.copy(enfoque_plan = planEnfoque(0, 12)) }, "tema_${t.id}_${modo}_enfoque") { io.github.ruddy2003.vestigium.ui.RelojEnfoque(false) {} }
    }
    private fun pulidoD2Tema(t: TemaId) { pulidoD2(t, t.soloOscuro); if (!t.soloOscuro) pulidoD2(t, true) }
    @Test fun pulidoD2a() = ESTANTE_A1A.forEach { pulidoD2Tema(it) }
    @Test fun pulidoD2b() = (ESTANTE_A1B + TemaId.EDO).forEach { pulidoD2Tema(it) }
    @Test fun pulidoD2c() = ESTANTE_A1C.forEach { pulidoD2Tema(it) }
    @Test fun pulidoD2d() = (ESTANTE_A1D + TemaId.DISTOPIA).forEach { pulidoD2Tema(it) }
    /** La biblioteca del diario entera de los 32, en su modo de siempre. */
    @Test fun bibliotecaDiarioA1() = (ESTANTE_A1A + ESTANTE_A1B + ESTANTE_A1C + ESTANTE_A1D).forEach { t -> pantalla(t, t.soloOscuro, 3600, "a1_${t.id}") {
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "diario")
    } }
    /** Escribir una idea: una muestra por forma de ficha (tarjeta, tablilla, tira, ticket, expediente, ventana, laja y recorte). */
    @Test fun escritorIdea() = listOf(TemaId.MEDIEVAL, TemaId.ROMANO, TemaId.EDO, TemaId.NEON, TemaId.DISTOPIA, TemaId.CIBER, TemaId.CUEVA, TemaId.PIRATA).forEach { t ->
        pantalla(t, t.soloOscuro, 2400, "tema_${t.id}") {
            ideasEjemplo()
            io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.idea = Repo.ideas.first { it.id == "i1" }; io.github.ruddy2003.vestigium.ui.pantallas.EdicionIdea.contexto = null
            io.github.ruddy2003.vestigium.ui.pantallas.EscritorIdea(navVacia)
        }
    }
    @Test fun deseadosColonos() = pantalla(TemaId.COLONOS, false, 3200) {
        obrasEjemplo()
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        Repo.obrasEnMemoria(Repo.obras + listOf(
            io.github.ruddy2003.vestigium.data.Obra("d1", "libro", "Un libro pendiente", "Autora inventada", estado = "quiero", agregado = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -20), prioridad = "pronto", recomendo = listOf("ana"), donde = "biblioteca"),
            io.github.ruddy2003.vestigium.data.Obra("d2", "serie", "Una serie inventada", estado = "quiero", agregado = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -3), prioridad = "algun_dia", donde = "plataforma"),
            io.github.ruddy2003.vestigium.data.Obra("d3", "pelicula", "Una película inventada", estado = "quiero", agregado = hoy),
        ))
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaDeseados(navVacia)
    }
    @Test fun listaArcano() = pantalla(TemaId.ARCANO, false, 2400) {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        fun e(id: String, t: String, p: String = "", hecho: String? = null) = io.github.ruddy2003.vestigium.data.ElementoLista(id, t, prioridad = p, agregado = io.github.ruddy2003.vestigium.data.Fechas.sumar(hoy, -10), hecho = hecho)
        Repo.listasEnMemoria(listOf(io.github.ruddy2003.vestigium.data.Lista("l1", "Lugares por visitar", hoy + "T10:00:00-06:00",
            elementos = listOf(e("a", "Un museo inventado", "pronto"), e("b", "Un mirador en la sierra", "algun_dia"), e("c", "La biblioteca vieja"), e("d", "Un mercado de fin de semana", hecho = hoy)))))
        io.github.ruddy2003.vestigium.ui.pantallas.EdicionLista.id = "l1"
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaLista(navVacia)
    }
    @Test fun ideaDeAntesNavidad() = pantalla(TemaId.NAVIDAD, false, 1400) {
        ideasEjemplo()
        Column(Modifier.padding(horizontal = 16.dp)) { io.github.ruddy2003.vestigium.ui.pantallas.UnaIdeaDeAntes(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), navVacia) }
    }
    @Test fun obraConIdeasMongol() = pantalla(TemaId.MONGOL, false, 6200) {
        ideasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.EdicionObra.id = "o1"
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
    /** La pantalla de Reportes con la sección «Para terapia». */
    @Test fun reportesBotanico() = pantalla(TemaId.BOTANICO, false, 4600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaReportes(navVacia) }
    /** Las páginas del reporte para terapia de 14 días, con todas las secciones (los datos son los inventados de [Ejemplo]). */
    @Test fun reporteTerapia() = pantalla(TemaId.BOTANICO, false, 8400) {
        val r = androidx.compose.runtime.remember {
            runCatching {
                io.github.ruddy2003.vestigium.reportes.ReporteTerapia.imagenes(io.github.ruddy2003.vestigium.ciencia.Terapia.Opciones(
                    dias = 14, fin = io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), -1),
                ).conTodo(true))
            }
        }
        Column {
            r.getOrNull()?.forEach { androidx.compose.foundation.Image(it.asImageBitmap(), null, Modifier.fillMaxWidth()) } ?: Text("Falló: ${r.exceptionOrNull()}")
        }
    }
    @Test fun epigrafeMedieval() = pantalla(TemaId.MEDIEVAL, false, 2400) {
        val ayer = io.github.ruddy2003.vestigium.data.Fechas.sumar(io.github.ruddy2003.vestigium.data.Fechas.diaLogico(), -1)
        Repo.configEnMemoria(Repo.config.copy(frases_guardadas = listOf(io.github.ruddy2003.vestigium.data.FraseGuardada("Quien no sabe callar, no sabe hablar.", null, "Refrán castellano", "medieval", ayer))))
        io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.entrada = null
        io.github.ruddy2003.vestigium.ui.pantallas.PantallaEntrada(navVacia)
    }
    // ------------------------------------------------------------ versión 67: cinta, constelación, hall, campañas, marcas

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
    @Test fun constelacionGotico() = v67(TemaId.GOTICO, true, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.GrafoSocial.vista = "cielo"; io.github.ruddy2003.vestigium.ui.pantallas.PantallaSocial(navVacia) }
    @Test fun constelacionNavidad() = v67(TemaId.NAVIDAD, false, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.GrafoSocial.vista = "cielo"; io.github.ruddy2003.vestigium.ui.pantallas.PantallaSocial(navVacia) }
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

    /** La banda del gato en cada tema que lo tiene (los 21 originales), quieta en una de sus dos animaciones propias (de día la primera, de noche la segunda). */
    private val temasConGato = TemaId.entries.filter { io.github.ruddy2003.vestigium.ui.Gato.tieneGato(it) }
    private fun gatos(oscuro: Boolean) = captura(TemaId.ROMANO, oscuro, alto = 100 + 295 * temasConGato.size) {
        Column(Modifier.padding(8.dp)) {
            temasConGato.forEachIndexed { k, t ->
                val o = oscuro || t.soloOscuro
                androidx.compose.material3.MaterialTheme(colorScheme = io.github.ruddy2003.vestigium.ui.esquemaDe(t, o), typography = io.github.ruddy2003.vestigium.ui.tipoDe(t, o)) {
                    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(t.nombre, style = MaterialTheme.typography.labelMedium)
                        io.github.ruddy2003.vestigium.ui.BandaGato(
                            io.github.ruddy2003.vestigium.ui.Gato.Humor(io.github.ruddy2003.vestigium.ui.Gato.Animo.NORMAL, cansado = false, noche = false), tema = t,
                            fijo = io.github.ruddy2003.vestigium.ui.Gato.Paso("esp_${t.id}_${if (oscuro) 1 else 0}", 1, 40f + (k % 6) * 55f, if (k % 2 == 0) 1 else -1),
                        )
                    }
                }
            }
        }
    }
    @Test fun gatosClaro() = gatos(false)
    @Test fun gatosOscuro() = gatos(true)

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
    // ---------------- esquema 21: el enfoque sigue contando, descanso «otra», suplementos varias veces, fechas señaladas y episodios ----------------
    private fun planExtra(segmento: Int, haceMin: Int, hechoMin: Int = 0) = planEnfoque(segmento, haceMin, hechoMin = hechoMin).copy(avisado = true)
    private fun enfoque21(tema: TemaId, oscuro: Boolean, plan: io.github.ruddy2003.vestigium.data.PlanEnfoque?, nombre: String) =
        v73(tema, oscuro, 2400, { c -> if (plan != null) c.copy(enfoque_plan = plan) else c.copy(enfoque_ultimo = io.github.ruddy2003.vestigium.data.PlanEnfoque("tesis", 120, 3, 7)) }, nombre) {
            io.github.ruddy2003.vestigium.ui.RelojEnfoque(false) {}
        }
    // Bloque 1 de 4 (30 min) con 10 de más; descanso de 10 con 4 de más; el último bloque con 5 de más; planear con descanso de 7 (otra).
    private val ENFOQUE21 = listOf(TemaId.ROMANO to false, TemaId.GOTICO to true, TemaId.CIBER to true, TemaId.BOTANICO to false, TemaId.SEDA to true, TemaId.NAPOLEON to false)
    @Test fun enfoqueExtra21() = ENFOQUE21.forEach { (t, o) ->
        enfoque21(t, o, planExtra(0, 40), "e21_bloque_${t.id}")
        enfoque21(t, o, planExtra(1, 14, 40), "e21_descanso_${t.id}")
        enfoque21(t, o, planExtra(6, 35, 90), "e21_ultimo_${t.id}")
        enfoque21(t, o, null, "e21_planear_${t.id}")
    }

    /** Un día de ejemplo con dos fechas señaladas (una de antes, como hito), dos episodios y dos tomas de creatina de tres al día. */
    private fun ejemplo21() {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        fun iso(h: Int, m: Int = 0) = java.time.LocalDate.parse(hoy).atTime(h, m).atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime().withNano(0).toString()
        Repo.diasEnMemoria(Repo.dias + (hoy to (Repo.dias[hoy] ?: io.github.ruddy2003.vestigium.data.Dia(hoy)).copy(hitos = listOf("primer_dia_de_clases"),
            senaladas = listOf(io.github.ruddy2003.vestigium.data.Senalada("viaje", "Viaje corto a la sierra"), io.github.ruddy2003.vestigium.data.Senalada("logro")))))
        Repo.momentosEnMemoria(Repo.momentos + listOf(
            io.github.ruddy2003.vestigium.data.Momento("e21a", iso(9, 30), hoy, "episodio", intensidad = 4, episodio = "crisis", duracion_min = 20, sintomas = listOf("corazon", "aire")),
            io.github.ruddy2003.vestigium.data.Momento("e21b", iso(13, 10), hoy, "episodio", intensidad = 2, episodio = "pelea", personas = listOf("luis"))))
        Repo.lote3EnMemoria(listOf(
            io.github.ruddy2003.vestigium.data.TomaSuplemento("t21a", "creatina", iso(8), hoy, 5.0),
            io.github.ruddy2003.vestigium.data.TomaSuplemento("t21b", "creatina", iso(14), hoy, 5.0)), emptyMap())
    }
    private fun dia21(tema: TemaId, oscuro: Boolean, nombre: String, alto: Int, cont: @Composable () -> Unit) =
        v73(tema, oscuro, alto, { c -> ejemplo21(); io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.noche = null
            c.copy(suplementos = listOf(io.github.ruddy2003.vestigium.data.Suplementos.CATALOGO[0].copy(tomas_dia = 3),
            io.github.ruddy2003.vestigium.data.Suplementos.CATALOGO[2]), suplementos_horas = listOf(8, 14, 21)) }, nombre) { cont() }
    @Test fun nocheSenaladas21() = ENFOQUE21.forEach { (t, o) -> dia21(t, o, "e21_noche_${t.id}", 9000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaNoche(navVacia) } }
    @Test fun suplementos21() = ENFOQUE21.forEach { (t, o) -> dia21(t, o, "e21_supl_${t.id}", 3600) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaSuplementos(navVacia) } }
    @Test fun diaEpisodios21() = ENFOQUE21.take(3).forEach { (t, o) -> dia21(t, o, "e21_dia_${t.id}", 8000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) } }

    // Las formas del esquema 21 (masturbación, intimidad, episodio) con datos inventados, y la pantalla Privado con ellas.
    private fun momento21(tipo: String, f: (io.github.ruddy2003.vestigium.data.Momento) -> io.github.ruddy2003.vestigium.data.Momento): io.github.ruddy2003.vestigium.data.Momento {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        return f(io.github.ruddy2003.vestigium.data.Momento("p21_$tipo", hoy + "T22:10:00-06:00", hoy, tipo))
    }
    private val MASTURBACION21 get() = momento21("masturbacion") { it.copy(duracion_min = 15, estimulo = "pornografia", dispositivo = "telefono", sitio = "cama", satisfaccion = 2, emociones = listOf("culpa", "alivio")) }
    private val INTIMIDAD21 get() = momento21("intimidad") { it.copy(practicas = listOf("beso_profundo", "caricias_ropa"), nivel = 2, personas = listOf("ana"), satisfaccion = 4, placer = 4, conexion = 5, comodidad = 5,
        emociones = listOf("ternura", "alegria"), texto = "Texto inventado de prueba.", orgasmo = "no", iniciativa = "ambos") }
    private val EPISODIO21 get() = momento21("episodio") { it.copy(episodio = "crisis", intensidad = 4, duracion_min = 20, sintomas = listOf("corazon", "aire", "mareo"), detonantes = listOf("estres")) }
    private fun forma21(tema: TemaId, oscuro: Boolean, nombre: String, m: io.github.ruddy2003.vestigium.data.Momento, alto: Int,
                        campos: @Composable (io.github.ruddy2003.vestigium.data.Momento, ((io.github.ruddy2003.vestigium.data.Momento) -> io.github.ruddy2003.vestigium.data.Momento) -> Unit) -> Unit) =
        v73(tema, oscuro, alto, { it.copy(seguimiento_privado = true) }, nombre) {
            Column(Modifier.padding(16.dp)) { campos(m) { } }
        }
    @Test fun formasPrivadas21() = ENFOQUE21.forEach { (t, o) ->
        forma21(t, o, "e21_masturbacion_${t.id}", MASTURBACION21, 2600) { m, c -> io.github.ruddy2003.vestigium.ui.CamposMasturbacion(m, c) }
        forma21(t, o, "e21_intimidad_${t.id}", INTIMIDAD21, 4200) { m, c -> io.github.ruddy2003.vestigium.ui.CamposIntimidad(m, c) }
        forma21(t, o, "e21_episodio_${t.id}", EPISODIO21, 2600) { m, c -> io.github.ruddy2003.vestigium.ui.CamposEpisodio(m, c) }
    }
    @Test fun privado21() = ENFOQUE21.take(4).forEach { (t, o) ->
        v73(t, o, 6000, { c -> Repo.momentosEnMemoria(Repo.momentos + listOf(MASTURBACION21, INTIMIDAD21)); c.copy(seguimiento_privado = true) }, "e21_privado_${t.id}") {
            io.github.ruddy2003.vestigium.ui.pantallas.PantallaRecaidas(navVacia)
        }
    }

    @Test fun trofeosRomano() = v73(TemaId.ROMANO, false, 7000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) }
    @Test fun trofeosOrden() = v73(TemaId.ORDEN, true, 5000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaLogros(navVacia) }

    // ---------------- lote 1 (v79): Revolución, Imperio, trofeos y hall por tema, mapas de campaña, libretas, reporte visual ----------------
    @Test fun revolucionClaro() = captura(TemaId.REVOLUCION, false)
    @Test fun revolucionOscuro() = captura(TemaId.REVOLUCION, true)
    @Test fun diaRevolucion() = dia(TemaId.REVOLUCION, false)

    /** Los trofeos de las nueve categorías en todos los temas (el último de cada fila, sin conseguir). */
    private fun trofeosTemas(oscuro: Boolean) = hojaTemas(TemaId.entries, porParte = 24, alto = { 100 + it * 200 }, oscuro = oscuro) { temas ->
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
        io.github.ruddy2003.vestigium.data.Vinculo("ana", "marta", "familia"))) }) { io.github.ruddy2003.vestigium.ui.pantallas.GrafoSocial.vista = "red"; io.github.ruddy2003.vestigium.ui.pantallas.PantallaSocial(navVacia) }
    // Social (9 nov 2026): el grafo en pantalla completa y la pantalla con Tu gente plegada.
    @Test fun socialGrafoRomano() = v73(TemaId.ROMANO, false, 1800) { io.github.ruddy2003.vestigium.ui.pantallas.GrafoSocial.vista = "red"; io.github.ruddy2003.vestigium.ui.pantallas.PantallaGrafoSocial(navVacia) }
    @Test fun socialGrafoGoticoOscuro() = v73(TemaId.GOTICO, true, 1800) { io.github.ruddy2003.vestigium.ui.pantallas.GrafoSocial.vista = "cielo"; io.github.ruddy2003.vestigium.ui.pantallas.PantallaGrafoSocial(navVacia) }
    @Test fun l3RedCiber() = v73(TemaId.CIBER, true, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.GrafoSocial.vista = "red"; io.github.ruddy2003.vestigium.ui.pantallas.PantallaSocial(navVacia) }
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
     * capturas.yml; sin ella, estas pruebas se saltan y las capturas de siempre no cambian. Con CAPTURAS_TEMAS (la
     * entrada `temas`) solo salen esos temas (ver [CapturasAPedido]).
     */
    // Un alto menor que el ancho (1080) hace que Paparazzi gire la captura: aquí ninguna baja de 1200.
    private fun porTema(superficie: String, captura: (tema: TemaId, oscuro: Boolean, nombre: String) -> Unit) {
        CapturasAPedido.pedida(superficie)
        CapturasAPedido.temas().forEach { t ->
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
    @Test fun temaArchivo() = porTema("archivo") { t, o, n -> archivo(t, o, n) }
    @Test fun temaMueble() = porTema("mueble") { t, o, _ -> mueble(t, o) }

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
    // Lote E6 (9 nov 2026): Vida con sus muebles y la pestaña Diario con el tablero de notas, en claro y oscuro (CAPTURAS=vida).
    @Test fun temaVida() = porTema("vida") { t, o, n ->
        v73(t, o, 5400, nombre = n + "_vida") { mundoEjemplo(); obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaVida(navVacia) }
        v73(t, o, 9600, nombre = n + "_vida_diario") { notasYMetas(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
        v73(t, o, 3000, nombre = n + "_vida_mapas") { mundoEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMapas(navVacia) }
    }
    @Test fun temaConstelacion() = porTema("constelacion") { t, o, n -> v67(t, o, 3000, nombre = n) { io.github.ruddy2003.vestigium.ui.pantallas.GrafoSocial.vista = "cielo"; io.github.ruddy2003.vestigium.ui.pantallas.PantallaSocial(navVacia) } }
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
    /** El cuerpo del diálogo del tema (Paparazzi no dibuja la ventana) y la barra de Deshacer. */
    @Test fun temaDialogo() = porTema("dialogo") { t, o, n -> captura(t, o, 1600, n) {
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            io.github.ruddy2003.vestigium.ui.CuerpoDialogo(
                confirmButton = { androidx.compose.material3.TextButton(onClick = {}) { Text("borrar") } },
                dismissButton = { androidx.compose.material3.TextButton(onClick = {}) { Text("cancelar") } },
                title = { Text("¿Borrar este momento?") },
                text = { Text("Se borra de este día. Tendrás 5 segundos para deshacerlo.") },
            )
            io.github.ruddy2003.vestigium.ui.BarraDeshacer("Se borró un momento", {})
        }
    } }
    /** Deslizador, progreso, interruptores, fichas, cuadrícula de afecto y menú abierto. */
    @Test fun temaControles() = porTema("controles") { t, o, n -> captura(t, o, 3000, n) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            io.github.ruddy2003.vestigium.ui.Deslizador(value = 0.6f, onValueChange = {}, valueRange = 0f..1f, steps = 4)
            io.github.ruddy2003.vestigium.ui.Deslizador(value = 0.25f, onValueChange = {})
            io.github.ruddy2003.vestigium.ui.Progreso(progress = { 0.35f }, modifier = Modifier.fillMaxWidth().height(6.dp))
            io.github.ruddy2003.vestigium.ui.Progreso(progress = { 0.8f }, modifier = Modifier.fillMaxWidth(0.8f).height(3.dp))
            Interruptor("Recordé un sueño", true, "Se guarda con su valencia y temas.") {}
            Interruptor("Fue una pesadilla", false) {}
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Ficha("ahora", false) {}
                Ficha("hace 15 min", true) {}
                Ficha("ayer", false) {}
            }
            io.github.ruddy2003.vestigium.ui.CuadriculaAfecto(5, 6) { _, _ -> }
            io.github.ruddy2003.vestigium.ui.MenuVista {
                io.github.ruddy2003.vestigium.ui.OpcionMenu(text = { Text("Guardar y compartir esta") }, onClick = {})
                io.github.ruddy2003.vestigium.ui.OpcionMenu(text = { Text("Guardar todas (PNG)") }, onClick = {})
            }
        }
    } }
    /** Los selectores de día y de hora, sin la ventana. */
    @Test fun temaSelectorFecha() = porTema("selectorFecha") { t, o, n -> captura(t, o, 3400, n) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            io.github.ruddy2003.vestigium.ui.CuerpoSelectorFecha(java.time.LocalDate.of(2026, 10, 19), maximo = java.time.LocalDate.of(2026, 10, 29))
            io.github.ruddy2003.vestigium.ui.CuerpoSelectorHora(22, 30)
        }
    } }
    @Test fun temaGraficasAnno() = porTema("graficasAnno") { t, o, n -> pantalla(t, o, 2600, n) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaAnno(navVacia) } }
    @Test fun temaBarra() = porTema("barra") { t, o, n -> captura(t, o, 1200, n) {
        Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("dia", "diario", "vida").forEach { io.github.ruddy2003.vestigium.ui.BarraPestanas(it, t, {}, {}) }
        }
    } }
    @Test fun temaEnfoque() = porTema("enfoque") { t, o, n ->
        v73(t, o, 2400, { it.copy(enfoque_plan = planEnfoque(0, 12)) }, n) { io.github.ruddy2003.vestigium.ui.RelojEnfoque(false) {} }
    }
    @Test fun temaBloqueo() = porTema("bloqueo") { t, o, n -> captura(t, o, 2400, n) { io.github.ruddy2003.vestigium.ui.Bloqueo {} } }

    // Lote 4: presentación del tema, registro rápido, cinta, Otras vidas y estilo de gráfica.
    @Test fun temaPresentacion() = porTema("presentacion") { t, o, n -> captura(t, o, 2400, n) {
        io.github.ruddy2003.vestigium.ui.PresentacionTema.frase = io.github.ruddy2003.vestigium.ui.Frases.de(t).first()
        io.github.ruddy2003.vestigium.ui.PresentacionTema.tema = t
        io.github.ruddy2003.vestigium.ui.CapaPresentacionTema()
    } }
    /** La hoja del registro rápido con el menú de Registrar, sobre el fondo del tema. */
    @Test fun temaQuick() = porTema("quick") { t, o, n -> captura(t, o, 2000, n) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.BottomCenter) {
            io.github.ruddy2003.vestigium.ui.HojaQuick(Modifier.fillMaxWidth()) {
                io.github.ruddy2003.vestigium.ui.EncabezadoQuick("Registrar", "10:32 · " + io.github.ruddy2003.vestigium.ui.Estilo.fecha("2026-10-17") + " · sábado 17 de octubre",
                    "Lo de todos los días en un toque. Lo demás, cuando quieras.")
                io.github.ruddy2003.vestigium.ui.MenuQuick(listOf("Enfoque" to "enfoque", "Siesta" to "siesta", "Sustancias" to "consumo", "Gasto o ingreso" to "gasto",
                    "Sueño" to "sueno", "Llenar el día" to "diario", "Nota" to "notas", "Impulso" to "impulso")) {}
            }
        }
    } }
    @Test fun temaCinta() = porTema("cinta") { t, o, n -> v73(t, o, 3000, nombre = n) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaCinta(navVacia) } }
    @Test fun temaOtrasVidas() = porTema("otrasVidas") { t, o, n -> pantalla(t, o, 6400, n) { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaOtrasVidas(navVacia) } }

    /**
     * Tanda P3a del plan de historia y escenas: «Hoy, en su vida», el cuento del día (CAPTURAS=cuento). Cuatro días de
     * Demeas con fecha y hora fijas (uno normal, el primero de una fiesta, el eclipse en el mar y el avance de 2026) y
     * la pantalla entera de «Otras vidas» en Helénico, para ver dónde queda la sección.
     */
    @Test fun cuentoDelDia() {
        CapturasAPedido.pedida("cuento")
        val demeas = io.github.ruddy2003.vestigium.data.OtrasVidas.porId("remero")!!
        listOf(Triple("normal_claro", "2027-02-03" to 9.0, false), Triple("fiesta_claro", "2027-03-18" to 11.0, false),
            Triple("eclipse_oscuro", "2027-08-03" to 14.0, true), Triple("avance_oscuro", "2026-11-11" to 20.0, true)).forEach { (nombre, cuando, oscuro) ->
            pantalla(TemaId.HELENO, oscuro, 4800, "cuento_$nombre") {
                Column(Modifier.padding(horizontal = 16.dp)) { io.github.ruddy2003.vestigium.ui.pantallas.HoyEnSuVida(demeas, cuando.first, cuando.second) }
            }
        }
        pantalla(TemaId.HELENO, false, 9400, "cuento_pantalla_claro") { saludEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaOtrasVidas(navVacia, "remero", "cuento") }
    }

    // ---------------------------------------------------------------- el cuadro de Hoy (plan de historia y escenas)

    /** Lo que la ficha dice de un momento, para leerlo encima de su cuadro. */
    private fun rotuloCuadro(ficha: io.github.ruddy2003.vestigium.vidas.FichaDelDia, hora: Double): String {
        val reloj = "%d:%02d".format(hora.toInt(), Math.round((hora - hora.toInt()) * 60).toInt())
        val dias = ficha.especiales.joinToString(" + ") { it.dia.titulo }.ifEmpty { "día normal" }
        return "${ficha.dia} · $reloj · ${ficha.estacion.nombre}, ${ficha.clima.nombre} · objeto: ${ficha.objeto ?: "—"} · estado: ${ficha.estado ?: "—"}\n$dias"
    }

    /** Una imagen con varios cuadros, uno debajo de otro, cada uno con su rótulo. Sin animación: la parte viva queda en su instante fijo. */
    private fun cuadros(tema: TemaId, oscuro: Boolean, nombre: String, banderita: io.github.ruddy2003.vestigium.ui.escenas.Banderita?,
                        celdas: List<Triple<io.github.ruddy2003.vestigium.ui.escenas.EscenaVida, io.github.ruddy2003.vestigium.vidas.FichaDelDia, Double>>) =
        captura(tema, oscuro, alto = 140 + celdas.size * 900, nombre = nombre) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                celdas.forEach { (escena, ficha, hora) ->
                    Text(rotuloCuadro(ficha, hora), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground, maxLines = 2)
                    io.github.ruddy2003.vestigium.ui.escenas.VistaEscena(escena, ficha, hora, Modifier.fillMaxWidth(), MaterialTheme.shapes.medium, animar = false, banderita = banderita)
                }
            }
        }

    /**
     * Tanda P4a del plan de historia y escenas: el cuadro de cada tema que ya tiene escena (CAPTURAS=cuadro; con
     * CAPTURAS_TEMAS=heleno, solo ese). Una imagen por día de muestra ([DIAS_CUADRO]) con el cuadro a tres horas, que
     * caen en tres luces distintas. La escena es igual en claro y en oscuro, así que sale una vez por tema. Se llaman
     * `…_temaCuadro_<tema>_<vida>_<día>.png`.
     */
    @Test fun temaCuadro() = porTema("cuadro") { t, o, _ ->
        val escena = io.github.ruddy2003.vestigium.ui.escenas.escenaDe(t)
        if (escena != null && o == t.soloOscuro) escena.vidas.forEach { vida ->
            DIAS_CUADRO[vida].orEmpty().forEach { (nombre, dia, horas) ->
                val ficha = io.github.ruddy2003.vestigium.vidas.FichaDelDia.de(dia, vida)!!
                cuadros(t, o, "${t.id}_${vida}_$nombre", io.github.ruddy2003.vestigium.ui.escenas.banderitaHistoria(t), horas.map { Triple(escena, ficha, it) })
            }
        }
    }

    /**
     * La muestra de la biblioteca de paisaje (CAPTURAS=cuadro): la escena llana, que solo usa piezas de la biblioteca,
     * con los tiempos que el paisaje de un tema quizá no tenga (nieve, niebla, tormenta, polvo) y los cinco techos, uno
     * por temporada. Las fichas son las de Demeas con el tiempo y la estación cambiados, así que traen también sus
     * arcos (el campo, los estragos) y sus días especiales. No lleva banderita: no es de ningún tema.
     */
    @Test fun cuadroBiblioteca() {
        CapturasAPedido.pedida("cuadro")
        val muestras = listOf(
            Triple("2027-01-15", 13.0, io.github.ruddy2003.vestigium.vidas.Clima.NIEVE to io.github.ruddy2003.vestigium.vidas.Estacion.INVIERNO),
            Triple("2028-10-15", 8.0, io.github.ruddy2003.vestigium.vidas.Clima.NIEBLA to io.github.ruddy2003.vestigium.vidas.Estacion.OTONO),
            Triple("2029-07-10", 18.0, io.github.ruddy2003.vestigium.vidas.Clima.TORMENTA to io.github.ruddy2003.vestigium.vidas.Estacion.VERANO),
            Triple("2030-04-10", 10.0, io.github.ruddy2003.vestigium.vidas.Clima.NUBES to io.github.ruddy2003.vestigium.vidas.Estacion.PRIMAVERA),
            Triple("2031-08-20", 13.0, io.github.ruddy2003.vestigium.vidas.Clima.POLVO to io.github.ruddy2003.vestigium.vidas.Estacion.SECAS),
            Triple("2029-06-01", 21.5, io.github.ruddy2003.vestigium.vidas.Clima.DESPEJADO to io.github.ruddy2003.vestigium.vidas.Estacion.LLUVIAS),
        ).map { (dia, hora, tiempo) ->
            val ficha = io.github.ruddy2003.vestigium.vidas.FichaDelDia.de(dia, "remero")!!.copy(clima = tiempo.first, estacion = tiempo.second)
            Triple<io.github.ruddy2003.vestigium.ui.escenas.EscenaVida, io.github.ruddy2003.vestigium.vidas.FichaDelDia, Double>(io.github.ruddy2003.vestigium.ui.escenas.EscenaPlana, ficha, hora)
        }
        muestras.chunked(3).forEachIndexed { i, parte -> cuadros(TemaId.HELENO, false, "biblioteca_${i + 1}", null, parte) }
    }

    /** El estilo de gráfica del tema: serie con puntos (uno hueco) y meta, rejilla, ejes, cifras, barras con su carril y una barra horizontal. */
    @Test fun temaEstiloGrafica() = porTema("estiloGrafica") { t, o, n -> captura(t, o, 1400, n) {
        val e = estiloGrafica(t)
        val cs = MaterialTheme.colorScheme
        val cifras = estiloCifras(MaterialTheme.typography.labelMedium, t)
        val pincel = pincelDeCifras(cs.onSurfaceVariant, 30f, tema = t)
        val serie = listOf(3.2f, 4.1f, null, 5.6f, 4.8f, 6.3f, 5.1f)
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("4,7", style = cifras.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize), color = cs.onSurface)
            androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(200.dp)) {
                val izq = 36.dp.toPx(); val arriba = 8.dp.toPx(); val abajo = size.height - 22.dp.toPx(); val der = size.width - 6.dp.toPx()
                fun px(i: Int) = izq + i * (der - izq) / 6f
                fun py(v: Float) = abajo - (v - 1f) / 6f * (abajo - arriba)
                val niveles = listOf(1f, 3f, 5f, 7f)
                rejillaGrafica(e, niveles.map { py(it) }, cs.outline, (0..6).map { px(it) }, androidx.compose.ui.geometry.Rect(izq, arriba, der, abajo))
                ejeGrafica(e, Offset(izq, abajo), Offset(der, abajo), cs.onSurfaceVariant, (0..6).map { px(it) })
                ejeGrafica(e, Offset(izq, abajo), Offset(izq, arriba), cs.onSurfaceVariant, niveles.map { py(it) })
                lineaMeta(e, Offset(izq, py(4.5f)), Offset(der, py(4.5f)), cs.tertiary)
                lineaGrafica(e, serie.mapIndexed { i, v -> v?.let { Offset(px(i), py(it)) } }, cs.primary)
                serie.forEachIndexed { i, v -> if (v != null) puntoGrafica(e, Offset(px(i), py(v)), 5.dp.toPx(), cs.primary, hueco = i == 1) }
                niveles.forEach { v -> drawContext.canvas.nativeCanvas.drawText(v.toInt().toString(), 4.dp.toPx(), py(v) + 10f, pincel) }
            }
            androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(110.dp)) {
                val w = size.width / 7
                listOf(0.35f, 0.8f, 0.55f, 1f, 0.15f, 0.65f, 0.45f).forEachIndexed { i, f ->
                    barraGrafica(e, Offset(i * w + w * 0.18f, 0f), Size(w * 0.64f, size.height), cs.surfaceVariant, pista = true)
                    barraGrafica(e, Offset(i * w + w * 0.18f, size.height * (1 - f)), Size(w * 0.64f, size.height * f), Tonos.secuencial(f, o))
                }
            }
            androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(14.dp)) {
                barraGrafica(e, Offset.Zero, size, cs.surfaceVariant, horizontal = true, pista = true)
                barraGrafica(e, Offset.Zero, Size(size.width * 0.62f, size.height), cs.primary, horizontal = true)
            }
        }
    } }
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
    /** Las marcas de @persona y #tema dentro de un texto. */
    @Test fun temaMarcas() = porTema("marcas") { t, o, n -> pantalla(t, o, 400, n) {
        Column(Modifier.padding(16.dp)) {
            io.github.ruddy2003.vestigium.ui.TextoMarcado("Café con @ana y @luis_m después del #protocolo de la #tesis", style = MaterialTheme.typography.bodyLarge, tema = t)
        }
    } }
    /** La lámina para rascar del mapa del mundo. */
    @Test fun temaLamina() = porTema("lamina") { t, o, n -> captura(t, o, 1400, n) {
        Column(Modifier.padding(16.dp)) {
            io.github.ruddy2003.vestigium.ui.pantallas.MapaRascar(setOf("MX", "ES", "FR", "JP"), Modifier.fillMaxWidth().aspectRatio(2.1f))
        }
    } }
    /** Lote 5: letras, nombres, fechas, números, claves, textos cortos y esquinas del tema, en una hoja. */
    @Test fun temaTextos() = porTema("textos") { t, o, n -> captura(t, o, 2600, n) {
        val E = io.github.ruddy2003.vestigium.ui.Estilo
        val ty = androidx.compose.material3.MaterialTheme.typography
        val cs = androidx.compose.material3.MaterialTheme.colorScheme
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val (nombre, com) = E.pantalla("Hoy", null)
            Text(nombre + E.sufijoTitulo, style = ty.headlineMedium, color = cs.primary)
            com?.let { Text(it, style = ty.labelSmall, color = cs.onSurfaceVariant) }
            Text(E.fecha("2026-10-17"), style = ty.titleLarge)
            Text(UnicidadTemas.PANTALLAS.joinToString("  ·  ") { E.pantalla(it, null).first }, style = ty.titleMedium)
            Text("Un día cualquiera también deja rastro: lo que comiste, con quién hablaste, cómo dormiste. 0123456789 ¿Ñandú? ¡Sí!", style = ty.bodyLarge)
            Text("Texto mediano para leer con calma, y una línea más pequeña debajo.", style = ty.bodyMedium)
            Text(E.prefijo + "etiqueta · " + E.comentario + "comentario · " + E.atras, style = ty.labelLarge, color = cs.primary)
            Text("numeral(17) = ${E.numeral(17)}   año ${E.numeral(2026)}   mes ${E.mes(10)} · ${E.mes(10, 2026)}", style = ty.labelLarge)
            Text(E.semana.joinToString("  "), style = ty.labelMedium)
            Text(E.semanaCorta.joinToString("  "), style = ty.labelSmall)
            Text(UnicidadTemas.CLAVES_DIA.joinToString("\n") { E.clave(it).padEnd(12) + "= 7" }, style = ty.labelMedium)
            Text(listOf("dia", "diario", "registrar", "vida", "entender").joinToString("  |  ") { E.texto("pestana_$it") }, style = ty.labelLarge, color = cs.secondary)
            Text(io.github.ruddy2003.vestigium.ui.Textos.CLAVES.drop(5).joinToString("\n") { E.texto(it).replace("%d", "120") }, style = ty.bodySmall)
            Text(E.racha(5) + "   " + (E.marca("2026-12-21") ?: E.marca("2026-07-16") ?: E.marca("2026-10-23") ?: "—"), style = ty.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                androidx.compose.material3.Surface(shape = androidx.compose.material3.MaterialTheme.shapes.small, color = cs.primaryContainer, modifier = Modifier.size(64.dp)) {}
                androidx.compose.material3.Surface(shape = androidx.compose.material3.MaterialTheme.shapes.medium, color = cs.secondaryContainer, modifier = Modifier.size(64.dp)) {}
                androidx.compose.material3.Surface(shape = androidx.compose.material3.MaterialTheme.shapes.large, color = cs.tertiaryContainer, modifier = Modifier.size(64.dp)) {}
                androidx.compose.material3.Button(onClick = {}) { Text(io.github.ruddy2003.vestigium.ui.textoGuardar(t)) }
            }
        }
    } }
    /** El reporte del día (Día → «Lo que registró el teléfono») con un día completo: fases del sueño, corazón, apps, cielo y comida. */
    @Test fun temaReporteDia() = porTema("reporteDia") { t, o, n ->
        Ejemplo.cargar()
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        val salud = Repo.salud.toMutableMap()
        salud[hoy] = salud.getValue(hoy).copy(sueno_profundo_min = 82, sueno_rem_min = 96, sueno_ligero_min = 220, sueno_despierto_min = 14,
            fc_media = 71.0, fc_min = 52.0, fc_max = 148.0, fc_reposo = 58.0, spo2 = 96.4)
        val pantalla = Repo.pantalla.toMutableMap()
        pantalla[hoy] = pantalla.getValue(hoy).copy(por_app_min = mapOf("Mensajes" to 64, "Mapas" to 31, "Lector" to 48))
        val entorno = Repo.entorno.toMutableMap()
        entorno[hoy] = entorno.getValue(hoy).copy(codigo = 2, luz_min = 706)
        val nutricion = Repo.nutricion.toMutableMap()
        nutricion[hoy] = io.github.ruddy2003.vestigium.data.NutricionDia(hoy, mapOf("kcal" to 2140.0, "proteina_g" to 118.0, "carbohidratos_g" to 240.0, "grasa_g" to 72.0))
        val notifs = Repo.notifs.toMutableMap()
        notifs[hoy] = io.github.ruddy2003.vestigium.data.NotifDia(hoy, 143, mapOf("Mensajes" to 61, "Correo" to 22, "Noticias" to 14))
        Repo.ejemploEnMemoria(Repo.dias, salud, pantalla, Repo.gastos, Repo.momentos, notifs, entorno, nutricion, Repo.lugares, Repo.visitas, Repo.sesiones, Repo.ingresos)
        captura(t, o, 2400, n) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                io.github.ruddy2003.vestigium.ui.pantallas.RejillaFichas(io.github.ruddy2003.vestigium.ui.pantallas.fichasDelDia(hoy, 6.4, 37, "La banda de ejemplo"))
            }
        }
    }
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

    // ---------------------------------------------------------------- Básico (el tema sin diseño, 9 nov 2026)

    /** Básico, en claro o en oscuro: Hoy, Diario, Vida, Análisis, Más, el Hall de logros, Apariencia y la biblioteca del diario. */
    private fun basico(oscuro: Boolean) {
        val t = TemaId.BASICO
        val modo = if (oscuro) "oscuro" else "claro"
        v73(t, oscuro, 8000, nombre = "basico_${modo}_hoy") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDia(navVacia) }
        v73(t, oscuro, 3400, nombre = "basico_${modo}_diario") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaDiario(navVacia) }
        v73(t, oscuro, 4200, nombre = "basico_${modo}_vida") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaVida(navVacia) }
        pantalla(t, oscuro, 2600, "basico_${modo}_analisis") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaExplorar(navVacia) }
        pantalla(t, oscuro, 4200, "basico_${modo}_mas") { mundoEjemplo(); obrasEjemplo(); io.github.ruddy2003.vestigium.ui.pantallas.PantallaMas(navVacia) }
        hall(t, oscuro, "basico_${modo}_hall")
        pantalla(t, oscuro, 3600, "basico_${modo}_apariencia") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaAjustes(navVacia, "apariencia") }
        pantalla(t, oscuro, 3000, "basico_${modo}_biblioteca") { io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecas(navVacia, "diario") }
    }
    @Test fun basicoClaro() = basico(false)
    @Test fun basicoOscuro() = basico(true)
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
        val dias = fechas.drop(1).associateWith { f -> io.github.ruddy2003.vestigium.data.Dia(fecha = f, valencia = 3 + r.nextInt(4), nota = notas[r.nextInt(notas.size)], sueno_calidad = 3, mejor_momento = "el café de la tarde", sueno_recordado = f.takeLast(2).toInt() % 3 == 0) } +
            (hoy to io.github.ruddy2003.vestigium.data.Dia(fecha = hoy, sueno_calidad = null, valencia = null))
        val salud = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.SaludDia(fecha = f, sueno_min = 380 + r.nextInt(90), pasos = 6000L + r.nextInt(6000),
            sueno_inicio = iso(io.github.ruddy2003.vestigium.data.Fechas.sumar(f, -1), 23, 40), sueno_fin = iso(f, 7, 10), hrv_rmssd = 40.0 + r.nextInt(15)) }
        val pantalla = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.PantallaDia(fecha = f, total_min = 180 + r.nextInt(120), desbloqueos = 60 + r.nextInt(40)) }
        val gastos = fechas.flatMap { f -> (0 until r.nextInt(3)).map { k -> io.github.ruddy2003.vestigium.data.Gasto("g$f$k", f, "%02d:15".format(9 + k * 5), 50.0 + r.nextInt(300), "comida") } }
        val notifs = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.NotifDia(fecha = f, total = 120, por_hora = List(24) { h -> if (h in 8..23) r.nextInt(14) else 0 }) }
        val entorno = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.EntornoDia(fecha = f, amanecer = "07:12", atardecer = "18:58", t_max = 24.0, t_min = 11.0) }
        val nutricion = fechas.associateWith { f -> io.github.ruddy2003.vestigium.data.NutricionDia(fecha = f, valores = mapOf("kcal" to 1900.0 + r.nextInt(700), "proteina_g" to 110.0 + r.nextInt(40), "cafeina_mg" to 190.0)) }
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


/** Las tres horas de las capturas del cuadro: amanecer, mediodía y noche. */
private val HORAS_CUADRO = listOf(7.0, 13.0, 21.0)

/**
 * Los días de muestra de las capturas `cuadro`, por personaje: nombre de la imagen, día lógico y horas. Los pide el
 * plan de historia y escenas (un día normal, una fiesta, la guerra y un día en que el cuento cambia lo que se ve) y se
 * agregan los que enseñan algo más de la escena. Cada tanda de escenas agrega los de sus personajes.
 */
private val DIAS_CUADRO: Map<String, List<Triple<String, String, List<Double>>>> = mapOf(
    // Demeas (Helénico, tanda P4a).
    "remero" to listOf(
        Triple("normal", "2027-05-05", HORAS_CUADRO),                     // primavera: el «Aura» en el agua y la cebada dorada
        Triple("fiesta", "2027-05-12", HORAS_CUADRO),                     // las Targelias: guirnaldas y, de noche, antorchas
        Triple("guerra", "2027-06-25", HORAS_CUADRO),                     // la primera invasión: humo en el llano y chozas junto al muro
        Triple("zarpa", "2027-07-05", HORAS_CUADRO),                      // zarpa la flota: su nave ya no está (decisión 18)
        Triple("invierno", "2027-02-03", HORAS_CUADRO),                   // lluvia, y el «Aura» en su cobertizo
        Triple("eclipse", "2027-08-03", listOf(13.0, 14.7, 16.5)),        // antes, en el máximo y después
        Triple("regreso", "2027-09-18", HORAS_CUADRO),                    // la flota vuelve
        Triple("luto", "2027-11-22", HORAS_CUADRO),                       // el entierro de los primeros muertos
        Triple("siega", "2027-05-20", HORAS_CUADRO),                      // las gavillas en los campos
        Triple("peste", "2028-08-16", HORAS_CUADRO),                      // las Grandes Panateneas en el año de la peste: campos arrasados y piras
        Triple("antorchas", "2029-06-01", listOf(13.0, 20.5, 22.5)),      // las Bendidias: la carrera de antorchas en el Pireo
        Triple("higuera", "2031-09-01", HORAS_CUADRO),                    // la higuera, cuatro años después, con sus primeros higos
    ),
    // Pierre (Gótico, tanda P4b).
    "cantero" to listOf(
        Triple("normal", "2027-05-10", HORAS_CUADRO),                     // la obra en marcha, con la grúa cargada
        Triple("fiesta", "2030-06-09", HORAS_CUADRO),                     // la boda de Jehanne: guirnaldas y banderines
        Triple("parada", "2027-09-03", HORAS_CUADRO),                     // se acaba el dinero: sin andamios y los muros tapados con paja (decisión 18)
        Triple("herido", "2028-05-27", HORAS_CUADRO),                     // cede el andamio: caído, y su piedra tapada con un saco
        Triple("crecida", "2029-01-21", HORAS_CUADRO),                    // el Somme fuera de cauce, con lluvia
        Triple("procesion", "2027-05-16", HORAS_CUADRO),                  // pendones en la calle y colgaduras
        Triple("invierno", "2027-01-20", HORAS_CUADRO),                   // la obra tapada con paja
        Triple("coro", "2031-10-10", HORAS_CUADRO),                       // la cabecera, a media altura
        Triple("eclipse", "2028-10-06", listOf(11.4, 12.4, 13.4)),        // el sol tapado a mediodía
        Triple("hogueras", "2027-06-24", listOf(13.0, 20.5, 22.5)),       // San Juan: la leña de día y las hogueras de noche
    ),
    // Amaru (Ciudad neón, tanda P4b). Los días son de los que le tocan a él: se alterna con Kei.
    "fideos" to listOf(
        Triple("normal", "2027-05-05", HORAS_CUADRO),                     // las tres luces del callejón: dormido, la farmacia abierta y el letrero
        Triple("faroles", "2028-02-13", HORAS_CUADRO),                    // la Noche de los Faroles
        Triple("apagon", "2028-08-21", HORAS_CUADRO),                     // sin letreros: gas y velas
        Triple("roto", "2028-11-15", HORAS_CUADRO),                       // el letrero roto con el puesto abierto
        Triple("marea", "2027-07-22", HORAS_CUADRO),                      // el agua en el suelo y los tablones
        Triple("velas", "2027-11-01", HORAS_CUADRO),                      // la Noche de los Ausentes
        Triple("nochevieja", "2027-12-31", HORAS_CUADRO),                 // las ristras de luces
        Triple("maquinas", "2030-09-02", HORAS_CUADRO),                   // las máquinas en la boca del callejón, las pintadas y el mural
    ),
    // Kei (Ciudad neón, tanda P4b). A las 17:00 y no a las 13:00, que es cuando la bici está en el callejón.
    "repartidora" to listOf(
        Triple("normal", "2027-05-06", listOf(7.0, 17.0, 21.0)),          // la bici esperando y, a la hora de cenar, enchufada
        Triple("sinbici", "2028-03-25", listOf(7.0, 17.0, 21.0)),         // después del choque: la bici no está (decisión 18)
        Triple("soldada", "2028-04-08", listOf(7.0, 17.0, 21.0)),         // vuelve, con el cuadro soldado
        Triple("huelga", "2029-03-08", listOf(7.0, 17.0, 21.0)),          // pancartas y cintas rojas
        Triple("toque", "2029-03-18", listOf(7.0, 17.0, 21.0)),           // toque de queda: el puesto cerrado a las 21:00
        Triple("cadena", "2030-11-10", listOf(7.0, 17.0, 21.0)),          // la cuerda de trapos delante de las máquinas
        Triple("tribunal", "2031-03-20", listOf(7.0, 17.0, 21.0)),        // la fiesta, con las máquinas todavía ahí
    ),
)
