package io.github.ruddy2003.vestigium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
    @Test fun barras() = captura(TemaId.ROMANO, false, alto = 3400) {
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
    @Test fun iconos() = captura(TemaId.ROMANO, false, alto = 2800) {
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
    @Test fun selectorTemas() = captura(TemaId.ROMANO, false, alto = 5600) {
        Column(Modifier.padding(16.dp)) {
            listOf(false, true).forEach { o ->
                TemaId.entries.chunked(3).forEach { fila ->
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { VistaPreviaTema(it, o, it == TemaId.ROMANO, Modifier.weight(1f)) {} }
                        repeat(3 - fila.size) { Box(Modifier.weight(1f)) }
                    }
                }
            }
            TemaId.entries.chunked(7).forEach { fila -> Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { t ->
                    val e = io.github.ruddy2003.vestigium.ui.esquemaDe(t, true)
                    Box(Modifier.weight(1f).background(e.background).padding(4.dp)) { io.github.ruddy2003.vestigium.ui.Emblema(t, 40.dp, e, animado = false) }
                }
            } }
        }
    }

    // ------------------------------------------------------------ huellas, diario y biblioteca con el diseño de cada tema

    /** Los sellos de huella de todos los temas. */
    @Test fun sellos() = captura(TemaId.ROMANO, false, alto = 2600) {
        Column(Modifier.padding(12.dp)) {
            TemaId.entries.chunked(4).forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                    fila.forEach { t -> io.github.ruddy2003.vestigium.ui.SelloHuella(Modifier.weight(1f).aspectRatio(1f), t) }
                    repeat(4 - fila.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
    }

    private val TEXTO = "Hoy fue un día largo pero bueno. En la mañana salí a caminar con @ana por el parque y hablamos de los planes para el viaje. " +
        "Después trabajé casi toda la tarde en el proyecto; avancé más de lo que esperaba. En la noche leí un rato antes de dormir. #lectura #trabajo"

    private fun objetos(temas: List<TemaId>) = captura(temas.first(), false, alto = 2800) {
        Column(Modifier.padding(16.dp)) {
            temas.forEachIndexed { i, t -> io.github.ruddy2003.vestigium.ui.ObjetoDiario("2026-10-16", if (i == 1) "" else TEXTO, Modifier.padding(bottom = 14.dp), t) {} }
        }
    }
    @Test fun objetos1() = objetos(listOf(TemaId.MEDIEVAL, TemaId.ROMANO, TemaId.HELENO, TemaId.SEDA, TemaId.COLONOS, TemaId.ORDEN, TemaId.BOTANICO))
    @Test fun objetos2() = objetos(listOf(TemaId.CIBER, TemaId.URSS, TemaId.LABORATORIO, TemaId.NAPOLEON, TemaId.NAVIDAD, TemaId.DISTOPIA, TemaId.REFUGIO))
    @Test fun objetos3() = objetos(listOf(TemaId.GOTICO, TemaId.ESPACIO, TemaId.ANDALUS, TemaId.MONGOL, TemaId.RENACIMIENTO, TemaId.BOTANICO))

    private fun biblioteca(tema: TemaId, oscuro: Boolean) = pantalla(tema, oscuro, 3000) { io.github.ruddy2003.vestigium.ui.pantallas.PantallaBibliotecaDiario(navVacia) }
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

    private fun relaciones(tema: TemaId, oscuro: Boolean) = captura(tema, oscuro, alto = 1500) {
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

    private fun teclados(temas: List<Pair<TemaId, Boolean>>) = captura(temas.first().first, temas.first().second, alto = 2600) {
        Column {
            temas.forEach { (t, _) ->
                io.github.ruddy2003.vestigium.ui.TecladoTema(t, io.github.ruddy2003.vestigium.ui.pagina(t), androidx.compose.ui.text.input.TextFieldValue("Hoy fui"), {}, {}, {})
                Box(Modifier.padding(4.dp))
            }
        }
    }
    @Test fun teclados1() = teclados(listOf(TemaId.MEDIEVAL to false, TemaId.DISTOPIA to false, TemaId.CIBER to true, TemaId.BOTANICO to false, TemaId.ESPACIO to true))
    @Test fun teclados2() = teclados(listOf(TemaId.GOTICO to false, TemaId.SEDA to true, TemaId.LABORATORIO to false, TemaId.REFUGIO to true, TemaId.ANDALUS to false))

    private fun fondo(tema: TemaId, oscuro: Boolean) = captura(tema, oscuro, alto = 2400) { io.github.ruddy2003.vestigium.ui.FondoTelefonoVista(tema, oscuro, Modifier.fillMaxSize()) }
    @Test fun fondoEspacio() = fondo(TemaId.ESPACIO, true)
    @Test fun fondoBotanico() = fondo(TemaId.BOTANICO, false)
    @Test fun fondoGotico() = fondo(TemaId.GOTICO, true)
    @Test fun fondoRomano() = fondo(TemaId.ROMANO, false)
    @Test fun fondoCiber() = fondo(TemaId.CIBER, true)
    @Test fun fondoAndalus() = fondo(TemaId.ANDALUS, false)
    @Test fun fondoMongol() = fondo(TemaId.MONGOL, false)
    @Test fun fondoNavidad() = fondo(TemaId.NAVIDAD, true)
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
        val mesActual = io.github.ruddy2003.vestigium.data.Fechas.hoyCalendario().take(7)
        val ingresos = listOf(
            io.github.ruddy2003.vestigium.data.Ingreso("i1", "$mesActual-01", "09:00", 9500.0, "Sueldo", "quincena", true),
            io.github.ruddy2003.vestigium.data.Ingreso("i2", "$mesActual-15", "09:00", 9500.0, "Sueldo", "quincena", true),
            io.github.ruddy2003.vestigium.data.Ingreso("i3", "$mesActual-10", "18:30", 1200.0, "Trabajo extra", "diseño de un cartel"))
        Repo.ejemploEnMemoria(dias, salud, pantalla, gastos, momentos, notifs, entorno, nutricion, lugares, visitas, sesiones, ingresos)
    }
}
