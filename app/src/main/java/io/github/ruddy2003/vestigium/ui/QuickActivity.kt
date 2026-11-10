@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.graphics.luminance
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.widget.Widgets
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Captura instantánea, sin abrir la app completa. Se abre desde widgets, el panel de ajustes rápidos,
 * los accesos directos del ícono y las notificaciones. Lo primero que hace es guardar; los detalles son opcionales.
 */
class QuickActivity : FragmentActivity() {
    companion object {
        const val MODO = "modo"; const val TIPO = "tipo"; const val VALENCIA = "valencia"
        const val ORIGEN = "origen"; const val AVISO_MS = "aviso_ms"; const val NOTIF = "notif"
        const val M_ANIMO = "animo"; const val M_MOMENTO = "momento"; const val M_MENU = "menu"
        const val M_GASTO = "gasto"; const val M_SUENO = "sueno"; const val M_MINIMO = "minimo"; const val M_ENFOQUE = "enfoque"; const val M_SIESTA = "siesta"
        /** Sustancias: con [CONSUMO] abre ese registro para completarlo (lo de anoche); con [SUSTANCIA], esa sustancia. */
        const val M_CONSUMO = "consumo"; const val CONSUMO = "consumo_id"; const val SUSTANCIA = "sustancia"
        const val CATEGORIA = "categoria"; const val CALIDAD = "calidad"; const val RECORDADO = "recordado"
        /** Subcategoría y monto de un gasto frecuente del widget: se registra con el toque. */
        const val SUBCATEGORIA = "subcategoria"; const val MONTO = "monto"
        /** Día (AAAA-MM-DD) para el que se anota, desde «＋ agregar a este día». */
        const val FECHA = "fecha"
        /** Con [TIPO] = "episodio": el id del tipo de episodio (Catalogo.EPISODIOS) que ya va marcado al abrir. */
        const val EPISODIO = "episodio_tipo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Repo.init(this)
        val modo = intent.getStringExtra(MODO) ?: M_MENU
        // El enfoque y la siesta viven en su propio reloj a pantalla completa.
        if (modo == M_ENFOQUE || modo == M_SIESTA) { abrirReloj(modo == M_SIESTA); finish(); return }
        val origen = intent.getStringExtra(ORIGEN) ?: if (intent.hasExtra(AVISO_MS)) "notificacion" else "acceso_directo"
        val avisoMs = intent.getLongExtra(AVISO_MS, 0L).takeIf { it > 0 }
        intent.getIntExtra(NOTIF, 0).takeIf { it != 0 }?.let { NotificationManagerCompat.from(this).cancel(it) }

        // Guardado inmediato cuando el toque ya trae la información.
        var inicial: Momento? = null
        val tipo = intent.getStringExtra(TIPO)
        var pantallaInicial = when (modo) { M_MOMENTO, M_ANIMO -> M_ANIMO; else -> modo }
        if (modo == M_MOMENTO && tipo != null) {
            if (tipo == "impulso" || tipo == "recaida" || (tipo in PRIVADOS_CON_SEGUIMIENTO && Repo.config.seguimiento_privado)) { inicial = crear(tipo, origen, null, null); pantallaInicial = "privado" }
            // Un episodio (normal, no privado) va por la misma hoja de captura; puede llegar con su tipo ya elegido.
            else if (tipo == "episodio") { inicial = crearEpisodio(origen, intent.getStringExtra(EPISODIO), intent.getStringExtra(FECHA)); pantallaInicial = "privado" }
            // Sin el seguimiento privado no se anota nada privado (antes la libido caía aquí como un ánimo vacío).
            else if (tipo in PRIVADOS_CON_SEGUIMIENTO) pantallaInicial = M_MENU
            else {
                // Un sentimiento (del widget) o un tipo de momento de antes: se guarda como registro de ánimo.
                val m0 = crear("animo", origen, null, null)
                val n = when {
                    Catalogo.sentimiento(tipo) != null -> m0.copy(emociones = listOf(tipo))
                    Catalogo.PENSAMIENTOS.any { it.id == tipo } -> m0.copy(pensamiento = tipo, pensamientos = listOf(tipo))
                    else -> m0
                }
                Repo.guardarMomento(n); inicial = n
            }
        }
        val val0 = intent.getIntExtra(VALENCIA, 0).takeIf { it in 1..7 }
        if (modo == M_ANIMO && val0 != null) inicial = crear(if (avisoMs != null) "muestreo" else "animo", origen, val0, avisoMs)
        val consumoId = intent.getStringExtra(CONSUMO)
        val sustanciaInicial = intent.getStringExtra(SUSTANCIA)
        // Desde el widget de sueño: la calidad se guarda con el toque.
        if (modo == M_SUENO) {
            val cal = intent.getIntExtra(CALIDAD, 0).takeIf { it in 1..5 }
            val rec = intent.getBooleanExtra(RECORDADO, false)
            if (cal != null || rec) {
                // Va al día del despertar (la fecha del calendario), como las horas del reloj, aunque sea antes de las 5:00 (H-NAV-7).
                val d = Repo.dia(Fechas.hoyCalendario())
                Repo.guardarDia(d.copy(sueno_calidad = cal ?: d.sueno_calidad, sueno_recordado = rec || d.sueno_recordado))
            }
        }
        val catInicial = intent.getStringExtra(CATEGORIA)
        // Un frecuente del widget de gasto: se registra con el toque (N-29); la hoja deja cambiar el monto o deshacerlo.
        val subInicial = intent.getStringExtra(SUBCATEGORIA)
        val gastoInicial = if (modo == M_GASTO && catInicial != null && subInicial != null)
            intent.getDoubleExtra(MONTO, 0.0).takeIf { it > 0 }?.let { m -> io.github.ruddy2003.vestigium.data.Dinero.registrarFrecuente(catInicial, subInicial, m).also { (g, nuevo) -> if (nuevo) ubicarGasto(g) }.first }
        else null

        setContent {
            TemaVestigium {
                Box(
                    Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { tocarFuera() },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    var pantalla by remember { mutableStateOf(pantallaInicial) }
                    var privado by remember { mutableStateOf(inicial?.takeIf { pantallaInicial == "privado" }) }
                    // El momento («¿Cómo estás ahora?») sigue vivo debajo de lo que se abre desde él (impulso, libido, sustancia…):
                    // al terminar ahí se vuelve a él con todo lo que llevabas, en vez de cerrarse la hoja entera.
                    var momentoAbierto by remember { mutableStateOf(pantallaInicial == M_ANIMO) }
                    var desdeMomento by remember { mutableStateOf(false) }
                    var anotadoFuera by remember { mutableStateOf<List<String>>(emptyList()) }
                    // El registro de ánimo que creó el toque en la cuadrícula del menú ⊕ (hallazgo 7): se sigue completando en «¿Cómo estás ahora?».
                    var delMenu by remember { mutableStateOf<Momento?>(null) }
                    // Lo que se abrió desde el menú ⊕ y todavía no guarda nada: «atrás» regresa al menú.
                    var desdeMenu by remember { mutableStateOf(false) }
                    // «Otra vez: <última sustancia>» del menú: abre Sustancias con esa ya elegida.
                    var sustanciaMenu by remember { mutableStateOf<String?>(null) }
                    val foco = androidx.compose.ui.platform.LocalFocusManager.current
                    androidx.compose.runtime.DisposableEffect(Unit) {
                        volverAlMomento = {
                            if (desdeMomento && pantalla != M_ANIMO) {
                                // Lo que se anotó (si no se borró) queda dicho en el momento.
                                privado?.takeIf { pantalla == "privado" }?.let { p -> Repo.momento(p.id) }?.let { p ->
                                    val t = Catalogo.tipo(p.tipo)
                                    val nombre = if (Repo.config.discreto) t?.discreto ?: "Registro" else t?.nombre ?: p.tipo
                                    anotadoFuera = anotadoFuera + "$nombre · ${Fechas.horaDe(p.ts)}"
                                }
                                desdeMomento = false; pantalla = M_ANIMO; true
                            } else false
                        }
                        onDispose { volverAlMomento = null }
                    }
                    // «Atrás», igual en toda la hoja (hallazgo 8): con algo sin guardar, el mismo aviso que al tocar fuera; desde lo
                    // que se abrió en el momento, de vuelta a él; desde un formulario abierto en el menú, de vuelta al menú; si no, cierra.
                    androidx.activity.compose.BackHandler {
                        if (avisarSinGuardar()) return@BackHandler
                        if (desdeMomento && pantalla != M_ANIMO) terminar()
                        else if (desdeMenu && (pantalla == M_GASTO || pantalla == M_CONSUMO || pantalla == M_SUENO || pantalla == M_MINIMO)) {
                            sinGuardar = false; avisado = false; desdeMenu = false; foco.clearFocus(); pantalla = M_MENU
                        } else terminar()
                    }
                    // «Listo» fijo al pie donde todo se guarda al tocar (ánimo, impulso y demás capturas, sueño): no hay que desplazar para cerrar.
                    val conPie = pantalla == M_ANIMO || pantalla == M_SUENO || (pantalla == "privado" && privado != null)
                    val pieListo: @Composable () -> Unit = {
                        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.End) { BotonPrincipal("Listo") { terminar() } }
                    }
                    HojaQuick(Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                        .navigationBarsPadding().imePadding(), pie = if (conPie) pieListo else null) {
                        val ir: (String) -> Unit = { destino ->
                            if (pantalla == M_ANIMO && destino != M_ANIMO) { desdeMomento = true; foco.clearFocus() }
                            desdeMenu = pantalla == M_MENU
                            if (destino == M_ANIMO) momentoAbierto = true
                            when (destino) {
                                // «privado» es la hoja de captura de un momento; el episodio, que no es privado, también va por ahí.
                                "impulso", "recaida", "libido", "masturbacion", "intimidad", "episodio" -> { privado = crear(destino, origen, null, null); pantalla = "privado" }
                                else -> pantalla = destino
                            }
                        }
                        // Oculto no se mide ni se dibuja ni recibe toques, pero conserva lo escrito.
                        if (momentoAbierto) Column(Modifier.fillMaxWidth().soloVisible(pantalla == M_ANIMO)) {
                            Animo(inicial ?: delMenu, origen, avisoMs, anotadoFuera, ir, deMenu = inicial == null && delMenu != null)
                        }
                        // El toque en la cuadrícula del menú guarda el registro de ánimo (mismo tipo y mismo origen que «Ánimo ahora») y lo abre.
                        val animoDelMenu: (Int, Int) -> Unit = { v, a ->
                            if (momentoAbierto) pantalla = M_ANIMO
                            else if (delMenu == null) {
                                val n = crear("animo", origen, v, null).copy(activacion = a)
                                Repo.guardarMomento(n)
                                delMenu = n; momentoAbierto = true; desdeMenu = false; pantalla = M_ANIMO
                            }
                        }
                        val menu: @Composable () -> Unit = { Registrar(ir, origen, animoDelMenu) { s -> sustanciaMenu = s; ir(M_CONSUMO) } }
                        when (pantalla) {
                            M_ANIMO -> Unit
                            "privado" -> privado?.let { androidx.compose.runtime.key(it.id) { MomentoCaptura(it) } } ?: menu()
                            M_MENU -> menu()
                            M_GASTO -> GastoRapido(catInicial, subInicial, gastoInicial)
                            M_SUENO -> SuenoRapido()
                            M_MINIMO -> Minimo()
                            M_ENFOQUE, M_SIESTA -> androidx.compose.runtime.LaunchedEffect(Unit) { abrirReloj(pantalla == M_SIESTA); terminar() }
                            M_CONSUMO -> ConsumoRapido(consumoId, sustanciaMenu ?: sustanciaInicial, origen)
                            else -> menu()
                        }
                    }
                    // La barra de «deshacer» también aquí (por ejemplo, al borrar un consumo).
                    CapaDeshacer()
                }
            }
        }
    }

    /** Si lo que está abierto se abrió desde el momento, regresa a él y devuelve true; si no, false y la hoja se cierra. */
    private var volverAlMomento: (() -> Boolean)? = null

    /** Cierra lo que está abierto: de vuelta al momento si se abrió desde él; si no, la hoja entera. */
    private fun terminar() { runCatching { Widgets.actualizar(this) }; if (volverAlMomento?.invoke() == true) return; finish() }

    /** Hay algo escrito que no se ha guardado (el monto de un gasto, una sustancia a medias): tocar fuera o «atrás» avisan una vez antes de cerrar. */
    private var sinGuardar = false
    private var avisado = false
    /** Qué es lo que falta guardar, para el aviso. */
    private var queFalta = "un monto sin guardar"
    /** Si hay algo sin guardar y todavía no se avisó, avisa y devuelve true (no se cierra); si no, false. */
    private fun avisarSinGuardar(): Boolean {
        if (!sinGuardar || avisado) return false
        avisado = true
        Toast.makeText(this, "Tienes $queFalta. Toca fuera o «atrás» otra vez para salir sin guardarlo.", Toast.LENGTH_LONG).show()
        return true
    }
    private fun tocarFuera() { if (!avisarSinGuardar()) terminar() }

    /** Cómo estaba el día antes de registrar una recaída, para que «Deshacer» lo deje igual. */
    private var previoRecaida: io.github.ruddy2003.vestigium.data.Recaidas.Previo? = null

    /**
     * Crea y guarda el momento al instante; la ubicación se añade en cuanto llega. Con [ms] (lo anotas tarde) el momento
     * va a esa hora y a su día lógico, sin la ubicación, la luz ni el ruido de ahora.
     */
    fun crear(tipo: String, origen: String, valencia: Int?, avisoMs: Long?, ms: Long? = null): Momento {
        val m0 = Momento(
            id = Repo.nuevoId(), ts = Fechas.ahoraIso(), fecha = Fechas.diaLogico(), tipo = tipo, valencia = valencia,
            origen = origen, aviso_ts = avisoMs?.let { Fechas.isoDeMillis(it) }, ts_creado = Fechas.ahoraIso(),
        )
        if (ms != null) {
            val m = io.github.ruddy2003.vestigium.data.Recaidas.aOtraHora(m0, ms)
            Repo.guardarMomento(m)
            if (tipo == "recaida") previoRecaida = io.github.ruddy2003.vestigium.data.Recaidas.registrar(m)
            return m
        }
        val m = m0
        Repo.guardarMomento(m)
        if (tipo == "recaida") previoRecaida = io.github.ruddy2003.vestigium.data.Recaidas.registrar(m)
        // Luz y, si lo activaste, ruido del ambiente en ese instante (no se guarda audio).
        lifecycleScope.launch {
            val ctx = this@QuickActivity
            val luz = if (Repo.config.medir_luz) runCatching { io.github.ruddy2003.vestigium.sensores.Percepcion.lux(ctx) }.getOrNull() else null
            kotlinx.coroutines.delay(250)
            val ruido = if (Repo.config.medir_ruido) runCatching { io.github.ruddy2003.vestigium.sensores.Ambiente.ruido(ctx) }.getOrNull() else null
            if (luz != null || ruido != null) actualizar(m.id) { if (it.ts != m.ts) it else it.copy(luz_lux = luz ?: it.luz_lux, ruido_db = ruido ?: it.ruido_db) }
        }
        lifecycleScope.launch {
            val loc = Ubicacion.actual(this@QuickActivity) ?: return@launch
            val actual = Repo.momento(m.id) ?: m
            // Si ya le cambiaste la hora, la ubicación de ahora no es la de ese momento.
            if (actual.ts != m.ts) return@launch
            val lugar = Repo.lugarCercano(loc.latitude, loc.longitude)
            Repo.guardarMomento(actual.copy(lat = loc.latitude, lon = loc.longitude, precision_m = loc.accuracy, lugar = lugar?.id))
            Repo.db().ponerUbicacion(System.currentTimeMillis(), loc.latitude, loc.longitude, loc.accuracy, "momento")
        }
        return m
    }

    private fun actualizar(id: String, f: (Momento) -> Momento) { Repo.momento(id)?.let { Repo.guardarMomento(f(it)) } }

    /** Privados que solo se anotan con el seguimiento privado encendido. */
    private val PRIVADOS_CON_SEGUIMIENTO = setOf("libido", "masturbacion", "intimidad")

    /**
     * Un episodio se guarda al instante, como los demás; con [tipoEpisodio] (desde el día) ya va con su tipo. Con [fecha] de otro
     * día («＋ agregar a este día») va a mediodía de ese día, sin la ubicación de ahora; la hora se corrige en la hoja.
     */
    private fun crearEpisodio(origen: String, tipoEpisodio: String?, fecha: String?): Momento {
        val ms = fecha?.takeIf { it != Fechas.diaLogico() }?.let { f -> runCatching { Fechas.parse(f).atTime(12, 0).aMillis() }.getOrNull() }
        val m = crear("episodio", origen, null, null, ms)
        val id = tipoEpisodio?.takeIf { t -> Catalogo.EPISODIOS.any { it.id == t } } ?: return m
        val n = m.copy(episodio = id)
        Repo.guardarMomento(n)
        return n
    }

    @Composable
    private fun Encabezado(titulo: String, sub: String, fecha: String? = null) {
        // El día va también en palabras: con algunos temas la fecha del estilo no se lee de un vistazo (N-08).
        val dia = fecha ?: Fechas.diaLogico()
        EncabezadoQuick(titulo, Fechas.horaAhora() + " · " + io.github.ruddy2003.vestigium.ui.Estilo.fecha(dia) + " · " + Fechas.titulo(dia).lowercase(), sub) {
            // Con una fecha propia (el sueño va al día del despertar) el aviso del día lógico no aplica.
            if (fecha == null) io.github.ruddy2003.vestigium.ui.AvisoMadrugada()
        }
    }

    /** El menú del botón ⊕: todo lo que se registra, en botones grandes. */
    @Composable
    private fun Registrar(ir: (String) -> Unit, origen: String, alAnimo: (Int, Int) -> Unit, otraVez: (String) -> Unit) {
        val cs = MaterialTheme.colorScheme
        val forma = formaPiezaQuick(Estilo.tema)
        // Lo que llega a la app desde aquí se apila sobre lo que tenías abierto si el ⊕ se tocó dentro de la app (hallazgo 15).
        fun abrirEnApp(ruta: String) {
            startActivity(android.content.Intent(this@QuickActivity, MainActivity::class.java).putExtra(MainActivity.PANTALLA, ruta)
                .putExtra(MainActivity.APILAR, origen == "app")
                .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish()
        }
        Encabezado("Registrar", "Un toque en el cuadro guarda tu registro de ánimo. Lo demás, debajo.")
        val botones = listOfNotNull(
            "Enfoque" to M_ENFOQUE, "Siesta" to M_SIESTA, "Sustancias" to M_CONSUMO, "Gasto o ingreso" to M_GASTO, "Sueño" to M_SUENO, "Llenar el día" to "diario",
            "Nota" to "notas", "Idea" to "idea_nueva", "Episodio" to "episodio", "Impulso" to "impulso", if (Repo.config.seguimiento_privado) "Recaída" to "recaida" else null,
            if (Repo.config.seguimiento_privado) "Libido" to "libido" else null,
            if (Repo.config.seguimiento_privado) "Masturbación" to "masturbacion" else null,
            if (Repo.config.seguimiento_privado) "Intimidad" to "intimidad" else null,
        )
        // Arriba, la cuadrícula del ánimo ya a la vista (hallazgo 7): el toque guarda y abre «¿Cómo estás ahora?» con lo demás.
        MenuQuick(botones, arriba = {
            CuadriculaAfecto(null, null) { v, a -> alAnimo(v, a) }
            BotonTexto(onClick = { ir(M_ANIMO) }) { Text("sin el cuadro: qué sientes, qué piensas o anotarlo en otra hora →", style = MaterialTheme.typography.labelLarge) }
        }) { d ->
            if (d == "diario" || d == "notas" || d == "idea_nueva") abrirEnApp(d)
            else ir(d)
        }
        // Gastos frecuentes: un toque registra (los cuatro primeros; los demás, en «Gasto o ingreso»).
        val frecuentes = remember { io.github.ruddy2003.vestigium.data.Dinero.frecuentes(4) }
        val abiertosAhora = io.github.ruddy2003.vestigium.data.Sustancias.abiertos()
        // «Otra vez»: la última sustancia que anotaste (si no la tienes oculta ni sigue en curso).
        val ultima = Repo.consumos.lastOrNull()?.takeIf { u -> io.github.ruddy2003.vestigium.data.Sustancias.visible(u) && abiertosAhora.none { it.id == u.id } }
        if (frecuentes.isNotEmpty() || ultima != null) {
            FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                frecuentes.forEach { f ->
                    val rotulo = "${f.sub} ${io.github.ruddy2003.vestigium.ui.pantallas.dinero(f.monto)}"
                    Ficha(rotulo, false) {
                        val (g, nuevo) = io.github.ruddy2003.vestigium.data.Dinero.registrarFrecuente(f.categoria, f.sub, f.monto)
                        if (nuevo) {
                            ubicarGasto(g)
                            io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Gasto registrado: $rotulo") { Repo.borrarGasto(g.id) }
                        } else Toast.makeText(this@QuickActivity, "Ya estaba registrado: $rotulo", Toast.LENGTH_SHORT).show()
                    }
                }
                ultima?.let { u -> Ficha("otra vez: " + Catalogo.nombreSustancia(u).lowercase(), false) { otraVez(u.sustancia) } }
            }
        }
        // Suplementos: un toque anota la dosis habitual. Con una toma ya hecha hoy, el toque suma otra (esquema 21: se pueden
        // varias al día) y la barra de abajo deja deshacerla durante 5 segundos.
        val supl = io.github.ruddy2003.vestigium.data.Suplementos.activos()
        if (supl.isNotEmpty()) {
            val hoySupl = Fechas.diaLogico()
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 2) {
                supl.forEach { sp ->
                    // Repo.tomas es estado: al anotar o deshacer, la cuenta se vuelve a leer sola.
                    val n = io.github.ruddy2003.vestigium.data.Suplementos.tomasDe(sp.id, hoySupl).size
                    val ya = n > 0
                    val dosis = if (sp.dosis == Math.floor(sp.dosis)) "%.0f".format(sp.dosis) else "%.1f".format(sp.dosis)
                    Surface(onClick = {
                        val t = io.github.ruddy2003.vestigium.data.Suplementos.registrar(sp)
                        io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer(if (ya) "Otra toma: ${sp.nombre.lowercase()}" else "Anotado: ${sp.nombre.lowercase()}") { Repo.borrarToma(t.id) }
                    }, modifier = Modifier.weight(1f).heightIn(min = 48.dp), shape = forma, color = if (ya) cs.primary.copy(alpha = 0.12f) else cs.surface,
                        border = BorderStroke(1.dp, if (ya) cs.primary else cs.outline)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(if (ya) "✓ Tomé ${sp.nombre.lowercase()} ($n hoy)" else "Tomé ${sp.nombre.lowercase()} · $dosis ${sp.unidad}", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
        val abiertos = io.github.ruddy2003.vestigium.data.Sustancias.abiertos()
        if (abiertos.isNotEmpty()) Ayuda("En curso: " + abiertos.joinToString(", ") { if (io.github.ruddy2003.vestigium.data.Sustancias.visible(it)) Catalogo.nombreSustancia(it).lowercase() else "un registro" } +
            ". Tócalo en «Sustancias» para cambiar el nivel o decir que ya se te pasó.", Modifier.padding(top = 10.dp))
        // «Tus atajos» ★ (los que fijaste en «Más»), también aquí al pie (hallazgo 17).
        val atajos = Repo.config.favoritos_mas.mapNotNull { io.github.ruddy2003.vestigium.ui.pantallas.Pantallas.de(it) }
        if (atajos.isNotEmpty()) {
            Etiqueta("★ Tus atajos")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                atajos.forEach { a -> Ficha(Estilo.seccion(a.titulo, Romano.latin(a.titulo)).let { (n, _) -> Estilo.conEspanol(n, a.titulo) }, false) {
                    io.github.ruddy2003.vestigium.ui.pantallas.Seleccion.limpiarFechas(); abrirEnApp(a.ruta)
                } }
            }
        }
    }

    /**
     * Ánimo ahora: un registro con tres partes. Un toque en la cuadrícula (qué tan agradable y cuánta energía),
     * y opcionales: qué sientes (varios, «otro» o «no sé»), en qué piensas y con quién estás.
     * De un aviso se guarda como «muestreo»; si lo abres tú, como «animo».
     */
    @Composable
    private fun Animo(inicial: Momento?, origen: String, avisoMs: Long?, anotadoFuera: List<String>, ir: (String) -> Unit, deMenu: Boolean = false) {
        var m by remember { mutableStateOf(inicial) }
        var valencia by remember { mutableStateOf(inicial?.valencia) }
        var activacion by remember { mutableStateOf(inicial?.activacion) }
        var emociones by remember { mutableStateOf(inicial?.emociones.orEmpty()) }
        var otra by remember { mutableStateOf(inicial?.emocion_otra.orEmpty()) }
        var intensidades by remember { mutableStateOf(inicial?.intensidades.orEmpty()) }
        var pensamientos by remember { mutableStateOf(inicial?.let { Catalogo.pensamientosDe(it) }.orEmpty()) }
        var nota by remember { mutableStateOf(inicial?.texto.orEmpty()) }
        var con by remember { mutableStateOf(inicial?.con_quien.orEmpty()) }
        val discreto = Repo.config.discreto
        // Cuándo: null = ahora. Para anotar tarde («ayer», otra hora); no en la respuesta a un aviso, que es de ese momento.
        // Desde «＋ agregar a este día» de otro día: empieza a mediodía de ese día, con el selector abierto (N-26).
        val fechaPedida = intent.getStringExtra(FECHA)?.takeIf { it != Fechas.diaLogico() }
        var cuando by remember { mutableStateOf<java.time.LocalDateTime?>(fechaPedida?.let { Fechas.parse(it).atTime(12, 0) }) }
        var elegirCuando by remember { mutableStateOf(fechaPedida != null) }
        /**
         * El registro se crea con lo primero que diga algo del ánimo: la cuadrícula, una emoción, un pensamiento o el texto.
         * «Con quién» solo no crea nada: espera y se guarda junto con lo demás.
         */
        fun asegurar(f: (Momento) -> Momento) {
            val nuevo = m == null
            val actual = m ?: crear(if (avisoMs != null) "muestreo" else "animo", origen, null, avisoMs, cuando?.aMillis())
            var base = Repo.momento(actual.id) ?: actual
            if (nuevo && con.isNotEmpty()) base = base.copy(con_quien = con)
            val n = f(base)
            Repo.guardarMomento(n); m = n
        }
        Encabezado("¿Cómo estás ahora?", if (m != null) "Guardado a las ${Fechas.horaDe(m!!.ts)} del ${Fechas.titulo(m!!.fecha).lowercase()}. Lo demás es opcional; puedes cerrar." else "Un toque en el cuadro basta.")
        // Lo guardó el toque en el widget o en el aviso: se dice qué quedó y se puede borrar si fue sin querer (N-15).
        if (inicial != null && !deMenu && m != null) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Guardado con el toque" + (m!!.valencia?.let { ": $it/7" } ?: "") + ".", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            BotonTexto(onClick = { Repo.borrarMomento(m!!.id); Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar() }) { Text("borrar") }
        }
        // También cuando lo guardó el toque en la cuadrícula del menú ⊕: se puede mover a otra hora.
        if (avisoMs == null && (inicial == null || deMenu)) {
            if (!elegirCuando) BotonTexto(onClick = { elegirCuando = true }) {
                Text(if (cuando == null) "¿fue antes? anotarlo en otra hora o día" else "cuándo: ${Fechas.corto(Fechas.diaLogicoDeMillis(cuando!!.aMillis()))} ${"%02d:%02d".format(cuando!!.hour, cuando!!.minute)}",
                    style = MaterialTheme.typography.labelLarge)
            } else io.github.ruddy2003.vestigium.ui.SelectorHora(cuando, ayer = true, atajos = listOf(60, 180)) { nuevo ->
                cuando = nuevo
                // Si ya estaba creado, se mueve a esa hora (y a su día).
                m?.let { actual -> val ms = (nuevo ?: java.time.LocalDateTime.now()).aMillis(); val n = io.github.ruddy2003.vestigium.data.Recaidas.aOtraHora(Repo.momento(actual.id) ?: actual, ms); Repo.guardarMomento(n); m = n }
            }
        }
        BannerConsumo(ir)
        CuadriculaAfecto(valencia, activacion) { v, a -> valencia = v; activacion = a; asegurar { it.copy(valencia = v, activacion = a) } }
        Etiqueta("¿Qué sientes? (opcional, varios)")
        io.github.ruddy2003.vestigium.ui.SelectorEmociones(io.github.ruddy2003.vestigium.ui.EleccionEmociones(emociones, otra, intensidades), discreto) { e ->
            emociones = e.emociones; otra = e.otra; intensidades = e.intensidades
            asegurar { it.copy(emociones = e.emociones, emocion_otra = if (Catalogo.OTRO in e.emociones) e.otra.trim().ifBlank { null } else null, intensidades = e.intensidades) }
        }
        Etiqueta("¿En qué piensas? (opcional, varios)")
        io.github.ruddy2003.vestigium.ui.SelectorPensamientos(pensamientos, discreto) { l ->
            pensamientos = l
            asegurar { it.copy(pensamientos = l, pensamiento = l.firstOrNull()) }
        }
        CampoTexto(value = nota, onValueChange = { t -> nota = t; asegurar { it.copy(texto = t) } },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp), minLines = 2, placeholder = { Text("¿Qué está pasando? Usa @persona y #tema") }, visualTransformation = rememberMarcas())
        Row(Modifier.padding(top = 6.dp)) { BotonDictado { d -> nota = (nota + " " + d).trim(); val t = nota; asegurar { it.copy(texto = t) } } }
        Etiqueta("¿Con quién estás? (opcional)")
        io.github.ruddy2003.vestigium.ui.SelectorConQuien(con) { c ->
            con = c
            // Solo se guarda si ya hay registro; si no, espera a lo primero del ánimo.
            if (m != null) asegurar { it.copy(con_quien = c) }
        }
        // Lo que no es ánimo, a un toque: dinero, sustancias, impulso y recaída.
        var dinero by remember { mutableStateOf(false) }
        var anotado by remember { mutableStateOf<List<String>>(emptyList()) }
        FlowRow(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonSecundario(if (dinero) "− gasto o ingreso" else "＋ gasto o ingreso") { dinero = !dinero }
            BotonSecundario("＋ sustancia") { ir(M_CONSUMO) }
            BotonSecundario("＋ impulso") { ir("impulso") }
            if (Repo.config.seguimiento_privado) BotonSecundario("＋ recaída") { ir("recaida") }
            if (Repo.config.seguimiento_privado) BotonSecundario("＋ libido") { ir("libido") }
            if (Repo.config.seguimiento_privado) BotonSecundario("＋ masturbación") { ir("masturbacion") }
            if (Repo.config.seguimiento_privado) BotonSecundario("＋ intimidad") { ir("intimidad") }
        }
        // Para no olvidar el dinero: cuánto llevas anotado hoy, lo que acabas de anotar aquí y, al abrirlo, el mismo formulario
        // del ⊕ (con la hora del momento si lo estás anotando tarde). Registrar no cierra la hoja.
        val hoyDia = m?.fecha ?: Fechas.diaLogico()
        val deHoy = Repo.gastos.filter { it.dia == hoyDia }
        Text(if (deHoy.isEmpty()) "Hoy no has anotado gastos." else "Hoy: ${deHoy.size} ${if (deHoy.size == 1) "gasto" else "gastos"} · ${io.github.ruddy2003.vestigium.ui.pantallas.dinero(deHoy.sumOf { it.monto })}",
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        (anotadoFuera + anotado).forEach { Text("✓ $it", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp)) }
        if (dinero) Surface(shape = formaPiezaQuick(Estilo.tema), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                GastoRapido(null, null, null, embebido = true, cuandoInicial = cuando) { t -> anotado = anotado + t; dinero = false }
            }
        }
        // «Listo» va fijo al pie de la hoja (hallazgo 7); aquí solo queda borrar.
        if (m != null) BotonTexto(onClick = { Repo.borrarMomento(m!!.id); Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar() },
            modifier = Modifier.padding(top = 14.dp)) { Text("Borrar este registro") }
    }

    /** Si hay una sustancia en curso: su nivel ahora y «ya se me pasó», sin salir del ánimo. */
    @Composable
    private fun BannerConsumo(ir: (String) -> Unit) {
        val abiertos = io.github.ruddy2003.vestigium.data.Sustancias.abiertos()
        if (abiertos.isEmpty()) return
        val cs = MaterialTheme.colorScheme
        abiertos.forEach { c ->
            var nivel by remember(c.id) { mutableStateOf(c.niveles.lastOrNull()?.nivel) }
            Surface(color = cs.secondaryContainer, shape = formaPiezaQuick(Estilo.tema), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Column(Modifier.padding(12.dp)) {
                    val nombre = if (io.github.ruddy2003.vestigium.data.Sustancias.visible(c)) Catalogo.nombreSustancia(c) else "Registro en curso"
                    Text("$nombre desde las ${Fechas.horaDe(c.inicio)} · ¿cómo vas?", style = MaterialTheme.typography.titleSmall, color = cs.onSecondaryContainer)
                    Escala(nivel, 0, 10, Catalogo.anclasNivel(c.sustancia)) { v -> nivel = v; if (v != null) io.github.ruddy2003.vestigium.data.Sustancias.lectura(Repo.consumo(c.id) ?: c, v) }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                        BotonTexto(onClick = { io.github.ruddy2003.vestigium.data.Sustancias.cerrar(Repo.consumo(c.id) ?: c); Toast.makeText(this@QuickActivity, "Anotado: ya se te pasó", Toast.LENGTH_SHORT).show() }) { Text("ya se me pasó") }
                        BotonTexto(onClick = { ir(M_CONSUMO) }) { Text("más detalles") }
                    }
                }
            }
        }
    }

    /** Impulso o recaída: lo que pasó, qué tan fuerte y (en la recaída) hora, duración y detalles. */
    @Composable
    private fun MomentoCaptura(inicial: Momento) {
        var m by remember { mutableStateOf(inicial) }
        var texto by remember { mutableStateOf("") }
        var intensidad by remember { mutableStateOf<Int?>(null) }
        val discreto = Repo.config.discreto
        val mm = m
        val t = Catalogo.tipo(mm.tipo)
        Encabezado("Guardado: ${if (discreto) t?.discreto ?: "" else t?.nombre ?: mm.tipo}", "A las ${Fechas.horaDe(mm.ts)}. Lo demás es opcional; puedes cerrar.")
        if (mm.tipo == "libido") { DetalleLibido(mm); return }
        if (mm.tipo == "masturbacion") { DetalleMasturbacion(mm) { m = it }; return }
        if (mm.tipo == "intimidad") { DetalleIntimidad(mm) { m = it }; return }
        if (mm.tipo == "episodio") { DetalleEpisodio(mm) { m = it }; return }
        if (mm.tipo == "recaida") DetalleRecaida(mm) { m = it }
        Etiqueta(t?.pregunta ?: "Detalle")
        CampoTexto(value = texto, onValueChange = { texto = it; actualizar(mm.id) { x -> x.copy(texto = it) } },
            modifier = Modifier.fillMaxWidth(), minLines = 2, placeholder = { Text("Usa @persona y #tema") }, visualTransformation = rememberMarcas())
        Row(Modifier.padding(top = 8.dp)) { BotonDictado { d -> texto = (texto + " " + d).trim(); actualizar(mm.id) { x -> x.copy(texto = texto) } } }
        Etiqueta(if (mm.tipo == "recaida") "Qué tan fuerte era el impulso" else "Intensidad")
        Escala(intensidad, 1, 5, mapOf(1 to "Muy leve", 2 to "Leve", 3 to "Moderada", 4 to "Fuerte", 5 to "Muy fuerte")) { v ->
            intensidad = v; actualizar(mm.id) { it.copy(intensidad = v) }
        }
        // «Listo» va fijo al pie de la hoja.
        BotonTexto(onClick = {
            Repo.borrarMomento(mm.id)
            previoRecaida?.let { io.github.ruddy2003.vestigium.data.Recaidas.deshacer(it) }; previoRecaida = null
            Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar()
        }, modifier = Modifier.padding(top = 14.dp)) { Text("Borrar este registro") }
    }

    /**
     * Libido (seguimiento privado): qué tan presente, de 0 a 5, y en qué está el foco. Distinta del impulso; sin texto,
     * sin logros, rachas ni widget. «También es un impulso» crea el registro de impulso a la misma hora.
     */
    @Composable
    private fun DetalleLibido(mm: Momento) {
        var nivel by remember(mm.id) { mutableStateOf(mm.intensidad) }
        var foco by remember(mm.id) { mutableStateOf(mm.foco) }
        var impulso by remember(mm.id) { mutableStateOf<Momento?>(null) }
        Etiqueta("Qué tan presente (0 a 5)")
        Escala(nivel, 0, 5, Catalogo.ANCLAS["libido"]) { v -> nivel = v; actualizar(mm.id) { it.copy(intensidad = v) } }
        Etiqueta("En qué está el foco")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.FOCOS.forEach { o -> Ficha(o.nombre, foco == o.id) { foco = if (foco == o.id) null else o.id; actualizar(mm.id) { it.copy(foco = foco) } } }
        }
        Ayuda("Solo para ti: no sale en reportes, logros, «Tu año», mapas ni widgets. Sí en tu exportación a R.", Modifier.padding(top = 10.dp))
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            if (impulso == null) BotonTexto(onClick = {
                val i = Momento(Repo.nuevoId(), mm.ts, mm.fecha, "impulso", intensidad = nivel?.coerceIn(1, 5), lat = mm.lat, lon = mm.lon, lugar = mm.lugar, origen = mm.origen, ts_creado = Fechas.ahoraIso())
                Repo.guardarMomento(i); impulso = i
                Toast.makeText(this@QuickActivity, "Impulso registrado a la misma hora", Toast.LENGTH_SHORT).show()
            }) { Text("también es un impulso") }
            else BotonTexto(onClick = { impulso?.let { Repo.borrarMomento(it.id) }; impulso = null }) { Text("quitar el impulso") }
        }
        BotonTexto(onClick = { Repo.borrarMomento(mm.id); impulso?.let { Repo.borrarMomento(it.id) }; Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar() }) { Text("Borrar este registro") }
    }

    /** Lo que ayuda a entender una recaída: qué la detonó, en qué aparato y qué hacías antes. Todo opcional. */
    @Composable
    private fun DetalleRecaida(mm: Momento, alCambiar: (Momento) -> Unit) {
        var det by remember(mm.id) { mutableStateOf(mm.detonantes) }
        var inicio by remember(mm.id) { mutableStateOf(Fechas.millis(mm.ts)) }
        var dur by remember(mm.id) { mutableStateOf(mm.duracion_min) }
        val ahoraMs = remember(mm.id) { Fechas.millis(mm.ts).takeIf { it > 0 } ?: System.currentTimeMillis() }
        /** Otra hora de inicio: se guarda con su día y con la ubicación que había a esa hora. */
        fun cambiarHora(ms: Long) {
            inicio = ms
            lifecycleScope.launch {
                val actual = Repo.momento(mm.id) ?: return@launch
                val n = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { io.github.ruddy2003.vestigium.data.Recaidas.aOtraHora(actual, ms) }
                Repo.guardarMomento(n)
                if (n.fecha != actual.fecha) {
                    previoRecaida?.let { io.github.ruddy2003.vestigium.data.Recaidas.deshacer(it) }
                    previoRecaida = io.github.ruddy2003.vestigium.data.Recaidas.registrar(n)
                }
                alCambiar(n)
            }
        }
        Etiqueta("¿Cuándo empezó?")
        var cuando by remember(mm.id) { mutableStateOf<java.time.LocalDateTime?>(null) }
        SelectorHora(cuando, atajos = listOf(15, 30, 60, 120, 180)) { t -> cuando = t; cambiarHora(t?.aMillis() ?: ahoraMs) }
        if (kotlin.math.abs(inicio - ahoraMs) > 60_000L) Ayuda("El lugar se toma de tus ubicaciones de esa hora.", Modifier.padding(top = 4.dp))
        Etiqueta("¿Cuánto duró?")
        SelectorDuracion(dur) { d -> dur = d; actualizar(mm.id) { it.copy(duracion_min = d) } }
        var aparato by remember(mm.id) { mutableStateOf(mm.dispositivo) }
        var antes by remember(mm.id) { mutableStateOf(mm.antes) }
        val h = io.github.ruddy2003.vestigium.data.Recaidas.habito()
        Ayuda("Solo para ti: no sale en reportes, logros ni «Tu año». " + (if (h != null) "El hábito «${h.nombre}» quedó en 0 hoy." else "Puedes ligarla a un hábito en Privacidad."),
            Modifier.padding(bottom = 4.dp))
        Etiqueta("Qué la detonó")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.DETONANTES.forEach { d ->
                val activo = d.id in det
                Ficha(d.nombre, activo) { det = if (activo) det - d.id else det + d.id; actualizar(mm.id) { it.copy(detonantes = det, detonante_otro = if ("otro" in det) it.detonante_otro else null) } }
            }
        }
        if ("otro" in det) {
            var otro by remember(mm.id) { mutableStateOf(mm.detonante_otro.orEmpty()) }
            CampoTexto(value = otro, onValueChange = { t -> otro = t; actualizar(mm.id) { it.copy(detonante_otro = t.trim().ifBlank { null }) } },
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("¿Cuál?") })
        }
        Etiqueta("Qué hacías justo antes")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.ANTES.forEach { o -> Ficha(o.nombre, antes == o.id) { antes = if (antes == o.id) null else o.id; actualizar(mm.id) { it.copy(antes = antes) } } }
        }
        Etiqueta("En qué aparato")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.DISPOSITIVOS.forEach { o -> Ficha(o.nombre, aparato == o.id) { aparato = if (aparato == o.id) null else o.id; actualizar(mm.id) { it.copy(dispositivo = aparato) } } }
        }
    }

    /**
     * «¿Cuándo empezó?», para registrarlo a otra hora: el momento se mueve con su día y con la ubicación que había a esa hora
     * (como en [DetalleRecaida]). [alMover] recibe cómo estaba y cómo quedó.
     */
    @Composable
    private fun CuandoEmpezo(mm: Momento, alMover: (antes: Momento, despues: Momento) -> Unit) {
        val ahoraMs = remember(mm.id) { Fechas.millis(mm.ts).takeIf { it > 0 } ?: System.currentTimeMillis() }
        var cuando by remember(mm.id) { mutableStateOf<java.time.LocalDateTime?>(null) }
        Etiqueta("¿Cuándo empezó?")
        SelectorHora(cuando, atajos = listOf(15, 30, 60, 120, 180), ayer = true) { t ->
            cuando = t
            val ms = t?.aMillis() ?: ahoraMs
            lifecycleScope.launch {
                val actual = Repo.momento(mm.id) ?: return@launch
                if (Fechas.millis(actual.ts) / 60_000L == ms / 60_000L) return@launch
                val n = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { io.github.ruddy2003.vestigium.data.Recaidas.aOtraHora(actual, ms) }
                Repo.guardarMomento(n)
                alMover(actual, n)
            }
        }
        if (cuando != null) Ayuda("El lugar se toma de tus ubicaciones de esa hora.", Modifier.padding(top = 4.dp))
    }

    /** Borra el momento de la hoja (con su recaída gemela, si la tiene) y deja el día como estaba antes de anotarlo. */
    private fun borrarCaptura(mm: Momento) {
        val actual = Repo.momento(mm.id) ?: mm
        io.github.ruddy2003.vestigium.data.Recaidas.alBorrar(actual)
        Repo.borrarMomento(actual.id)
        previoRecaida?.let { io.github.ruddy2003.vestigium.data.Recaidas.deshacer(it) }; previoRecaida = null
        Toast.makeText(this, "Borrado", Toast.LENGTH_SHORT).show(); terminar()
    }

    @Composable
    private fun PieCaptura(mm: Momento) {
        // «Listo» va fijo al pie de la hoja.
        BotonTexto(onClick = { borrarCaptura(mm) }, modifier = Modifier.padding(top = 14.dp)) { Text("Borrar este registro") }
    }

    /**
     * Masturbación (seguimiento privado): cuándo y cuánto duró, con qué, dónde, en qué aparato, satisfacción, emociones de
     * después y cómo fue. Con pornografía cuenta como recaída: se crea su recaída gemela (y se quita si cambias el estímulo).
     */
    @Composable
    private fun DetalleMasturbacion(mm: Momento, alCambiar: (Momento) -> Unit) {
        var m by remember(mm.id) { mutableStateOf(Repo.momento(mm.id) ?: mm) }
        var preguntar by remember(mm.id) { mutableStateOf(false) }
        fun cambiar(f: (Momento) -> Momento) {
            val n = f(Repo.momento(mm.id) ?: m)
            Repo.guardarMomento(n); sincronizarGemela(n); m = n
        }
        CuandoEmpezo(mm) { _, n ->
            m = n; alCambiar(n)
            // La recaída gemela se mueve con ella (y, si cambia de día, la marca del día también).
            val g = io.github.ruddy2003.vestigium.data.Recaidas.gemela(n)?.takeIf { it.tipo == "recaida" }
            if (g != null) {
                val g2 = io.github.ruddy2003.vestigium.data.Recaidas.aOtraHora(g, Fechas.millis(n.ts))
                Repo.guardarMomento(g2)
                if (g2.fecha != g.fecha) {
                    previoRecaida?.let { io.github.ruddy2003.vestigium.data.Recaidas.deshacer(it) }
                    previoRecaida = io.github.ruddy2003.vestigium.data.Recaidas.registrar(g2)
                }
            }
        }
        CamposMasturbacion(m, { f -> cambiar(f) }) { antes, despues ->
            val actual = Repo.momento(mm.id) ?: m
            if (despues == "pornografia") {
                io.github.ruddy2003.vestigium.data.Recaidas.registrarDesdeMasturbacion(actual)?.let { (_, previo) -> previoRecaida = previo }
                Repo.momento(mm.id)?.let { m = it }
            } else if (antes == "pornografia" && io.github.ruddy2003.vestigium.data.Recaidas.gemela(actual)?.tipo == "recaida") preguntar = true
        }
        if (preguntar) PreguntaGemela(Repo.momento(mm.id) ?: m) { quitada ->
            preguntar = false
            if (quitada) { previoRecaida?.let { io.github.ruddy2003.vestigium.data.Recaidas.deshacer(it) }; previoRecaida = null }
            Repo.momento(mm.id)?.let { m = it }
        }
        Ayuda("Solo para ti: no sale en reportes, logros, «Tu año», mapas ni widgets. Sí en tu exportación a R.", Modifier.padding(top = 10.dp))
        PieCaptura(mm)
    }

    /** Intimidad (seguimiento privado): qué pasó, cómo fue, con quién, las escalas, los detalles plegados y la hora. */
    @Composable
    private fun DetalleIntimidad(mm: Momento, alCambiar: (Momento) -> Unit) {
        var m by remember(mm.id) { mutableStateOf(Repo.momento(mm.id) ?: mm) }
        CamposIntimidad(m) { f -> val n = f(Repo.momento(mm.id) ?: m); Repo.guardarMomento(n); m = n }
        CuandoEmpezo(mm) { _, n -> m = n; alCambiar(n) }
        Ayuda("Solo para ti: no sale en reportes, logros, «Tu año», mapas, menciones ni widgets. Sí en tu exportación a R.", Modifier.padding(top = 10.dp))
        PieCaptura(mm)
    }

    /** Episodio (normal, no privado): qué fue, qué tan fuerte, cuánto duró, síntomas, qué lo detonó, con quién y una nota. */
    @Composable
    private fun DetalleEpisodio(mm: Momento, alCambiar: (Momento) -> Unit) {
        var m by remember(mm.id) { mutableStateOf(Repo.momento(mm.id) ?: mm) }
        CamposEpisodio(m) { f -> val n = f(Repo.momento(mm.id) ?: m); Repo.guardarMomento(n); m = n }
        CuandoEmpezo(mm) { _, n -> m = n; alCambiar(n) }
        PieCaptura(mm)
    }

    /** El cronómetro: elegir qué empieza, ver cuánto lleva y terminarlo; también anotar una siesta que ya pasó. */
    private fun abrirReloj(siesta: Boolean) {
        startActivity(android.content.Intent(this, EnfoqueActivity::class.java).putExtra(EnfoqueActivity.SIESTA, siesta)
            .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }

    /** Guarda dónde estabas en un consumo recién creado (si es de ahora). */
    private fun ubicarConsumo(c: io.github.ruddy2003.vestigium.data.Consumo) {
        lifecycleScope.launch {
            val loc = Ubicacion.actual(this@QuickActivity) ?: return@launch
            val actual = Repo.consumo(c.id) ?: return@launch
            Repo.guardarConsumo(actual.copy(lat = loc.latitude, lon = loc.longitude, lugar = Repo.lugarCercano(loc.latitude, loc.longitude)?.id))
        }
    }

    /**
     * Sustancias. Sin nada abierto: qué, desde cuándo, cómo vas (0 a 10), cuánto y con quién (lo básico).
     * Con un consumo en curso: subir o bajar el nivel, sumar bebidas, «ya se me pasó».
     * Con [consumoId]: completar lo de anoche (hasta qué hora, máximo, cantidad, cruda y síntomas).
     */
    @Composable
    private fun ConsumoRapido(consumoId: String?, sustanciaInicial: String?, origen: String) {
        val ctx = this
        val sus = io.github.ruddy2003.vestigium.data.Sustancias
        val completar = consumoId?.let { Repo.consumo(it) }
        if (completar != null) { CompletarConsumo(completar); return }
        var sustancia by remember { mutableStateOf(sustanciaInicial) }
        Encabezado("Sustancias", "Lo básico ahora; la duración y la cruda, mañana. Es un dato como cualquier otro.")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.SUSTANCIAS.forEach { x -> Ficha(x.nombre, sustancia == x.id) { sustancia = x.id } }
        }
        val id = sustancia ?: return
        val info = Catalogo.sustancia(id) ?: return
        val abierto = sus.abiertos().firstOrNull { it.sustancia == id }
        if (abierto != null) {
            var nivel by remember(abierto.id) { mutableStateOf(abierto.niveles.lastOrNull()?.nivel) }
            var cantidad by remember(abierto.id) { mutableStateOf(abierto.cantidad ?: 0.0) }
            var fin by remember(abierto.id) { mutableStateOf<java.time.LocalDateTime?>(null) }
            Ayuda("En curso desde las ${Fechas.horaDe(abierto.inicio)}" + (sus.maximo(abierto)?.let { " · máximo hasta ahora $it" } ?: ""), Modifier.padding(top = 10.dp))
            Etiqueta("¿Cómo vas ahora? (0 a 10)")
            Escala(nivel, 0, 10, Catalogo.anclasNivel(id)) { v -> nivel = v; if (v != null) sus.lectura(Repo.consumo(abierto.id) ?: abierto, v) }
            if (id == "alcohol") CantidadConsumo("alcohol", "bebidas", cantidad) { q -> cantidad = q; Repo.consumo(abierto.id)?.let { Repo.guardarConsumo(it.copy(cantidad = q.takeIf { v -> v > 0 })) } }
            Etiqueta("¿Ya se te pasó? ¿A qué hora?")
            SelectorHora(fin, atajos = listOf(15, 30, 60)) { fin = it }
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                BotonTexto(onClick = { terminar() }) { Text("Listo") }
                BotonPrincipal("Ya se me pasó") {
                    sus.cerrar(Repo.consumo(abierto.id) ?: abierto, (fin ?: java.time.LocalDateTime.now()).aMillis())
                    Toast.makeText(ctx, "Anotado. Mañana te pregunto cómo terminó.", Toast.LENGTH_SHORT).show(); terminar()
                }
            }
            return
        }
        var cuando by remember(id) { mutableStateOf<java.time.LocalDateTime?>(null) }
        var guardado by remember(id) { mutableStateOf(false) }
        var nivel by remember(id) { mutableStateOf<Int?>(null) }
        var cantidad by remember(id) { mutableStateOf(0.0) }
        var via by remember(id) { mutableStateOf<String?>(null) }
        var otra by remember(id) { mutableStateOf("") }
        var con by remember(id) { mutableStateOf<List<String>>(emptyList()) }
        // Con algo ya puesto y sin guardar, tocar fuera o «atrás» avisan una vez (hallazgo 8).
        androidx.compose.runtime.SideEffect {
            sinGuardar = !guardado && (cuando != null || nivel != null || cantidad > 0 || via != null || otra.isNotBlank() || con.isNotEmpty())
            if (sinGuardar) queFalta = "una sustancia sin guardar"
        }
        androidx.compose.runtime.DisposableEffect(Unit) { onDispose { sinGuardar = false; avisado = false } }
        if (id == "otra") CampoTexto(value = otra, onValueChange = { otra = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("¿Cuál?") })
        Etiqueta(if (info.conNivel) "¿Desde cuándo?" else "¿Cuándo?")
        SelectorHora(cuando, atajos = listOf(30, 60, 120, 180)) { cuando = it }
        if (info.conNivel) {
            Etiqueta("¿Cómo vas ahora? (0 a 10)")
            Escala(nivel, 0, 10, Catalogo.anclasNivel(id)) { nivel = it }
        }
        if (id != "alcohol" && id != "cafeina" && info.vias.isNotEmpty()) {
            Etiqueta("¿Cómo?")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { info.vias.forEach { o -> Ficha(o.nombre, via == o.id) { via = if (via == o.id) null else o.id } } }
        }
        CantidadConsumo(id, info.unidad, cantidad) { cantidad = it }
        if (id == "cafeina") Ayuda("Si registras la cafeína en MacroFactor, esos días se usa la de MacroFactor (con la hora de cada alimento) y no se cuenta dos veces.", Modifier.padding(top = 4.dp))
        Etiqueta("¿Con quién? (opcional)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.CON_QUIEN.forEach { o -> Ficha(o.nombre, o.id in con) { con = Catalogo.alternarConQuien(con, o.id) } }
        }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End) {
            BotonPrincipal("Guardar", habilitado = (id != "otra" || otra.isNotBlank()) && !guardado) {
                // Un toque doble no guarda dos consumos.
                if (guardado) return@BotonPrincipal
                guardado = true; sinGuardar = false
                val ms = (cuando ?: java.time.LocalDateTime.now()).aMillis()
                val c = sus.nuevo(id, ms, nivel, cantidad.takeIf { it > 0 }, via, otra, con, origen)
                if (cuando == null) ubicarConsumo(c)
                Toast.makeText(ctx, if (info.conNivel) "Guardado. Desde tu registro de ánimo puedes decir cómo vas." else "Guardado", Toast.LENGTH_SHORT).show()
                terminar()
            }
        }
    }

    /**
     * Cuánto: en alcohol y cafeína, fichas que suman (y «quitar el último» para deshacer la última que sumaste); en lo demás,
     * − y +. Siempre se puede escribir el total (con decimales) y «poner en 0».
     */
    @Composable
    private fun CantidadConsumo(sustancia: String, unidad: String, cantidad: Double, onCambio: (Double) -> Unit) {
        val D = io.github.ruddy2003.vestigium.data.Dinero
        var pila by remember(sustancia) { mutableStateOf(listOf<Double>()) }
        var texto by remember(sustancia) { mutableStateOf(if (cantidad > 0) D.texto(cantidad) else "") }
        fun poner(v: Double) { val n = (Math.round(v.coerceAtLeast(0.0) * 100) / 100.0); texto = if (n > 0) D.texto(n) else ""; onCambio(n) }
        val fichas = when (sustancia) { "alcohol" -> Catalogo.BEBIDAS; "cafeina" -> Catalogo.CAFES; else -> emptyList() }
        Etiqueta(when (sustancia) {
            "alcohol" -> "Bebidas: ${"%.1f".format(cantidad).removeSuffix(".0").removeSuffix(",0")} (opcional)"
            "cafeina" -> "¿Qué tomaste? ${cantidad.toInt()} mg"
            else -> "¿Cuántos $unidad?"
        })
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            fichas.forEach { (n, eq) -> Ficha("+ $n", false) { pila = pila + eq; poner(cantidad + eq) } }
            if (fichas.isEmpty()) {
                Ficha("−", false) { poner(cantidad - 1) }
                Ficha("+", false) { poner(cantidad + 1) }
            } else if (pila.isNotEmpty()) Ficha("quitar el último", false) { val u = pila.last(); pila = pila.dropLast(1); poner(cantidad - u) }
            else if (cantidad > 0 && sustancia == "alcohol") Ficha("− 1", false) { poner(cantidad - 1) }
            if (cantidad > 0) Ficha("poner en 0", false) { pila = emptyList(); poner(0.0) }
        }
        CampoTexto(value = texto, onValueChange = { t -> texto = D.limpiarMonto(t); onCambio(texto.toDoubleOrNull() ?: 0.0) },
            modifier = Modifier.padding(top = 6.dp).width(200.dp), singleLine = true, label = { Text("o escribe el total") },
            suffix = { Text(if (sustancia == "cafeina") "mg" else unidad) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
    }

    /** La mañana siguiente (o al corregir): hasta qué hora duró, el máximo, cuánto, cómo, con quién, la cruda y cómo te sentiste. */
    @Composable
    private fun CompletarConsumo(c0: io.github.ruddy2003.vestigium.data.Consumo) {
        val ctx = this
        val info = Catalogo.sustancia(c0.sustancia)
        var inicio by remember { mutableStateOf<java.time.LocalDateTime?>(horaLocal(Fechas.millis(c0.inicio))) }
        var fin by remember { mutableStateOf(c0.fin?.let { horaLocal(Fechas.millis(it)) }) }
        var max by remember { mutableStateOf(io.github.ruddy2003.vestigium.data.Sustancias.maximo(c0)) }
        var cantidad by remember { mutableStateOf(c0.cantidad ?: 0.0) }
        var cruda by remember { mutableStateOf(c0.cruda) }
        var sintomas by remember { mutableStateOf(c0.sintomas) }
        var nota by remember { mutableStateOf(c0.nota) }
        var via by remember { mutableStateOf(c0.via) }
        var con by remember { mutableStateOf(c0.con_quien) }
        var otra by remember { mutableStateOf(c0.otra_nombre) }
        Encabezado((if (c0.fecha < Fechas.diaLogico()) "Lo de anoche: " else "Editar: ") + Catalogo.nombreSustancia(c0).lowercase(), "Unos toques. Lo que no recuerdes, déjalo vacío.")
        if (c0.sustancia == "otra") CampoTexto(value = otra, onValueChange = { otra = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("¿Cuál?") })
        Etiqueta("¿Desde cuándo?")
        SelectorHora(inicio, etiquetaAhora = "no sé") { inicio = it }
        Etiqueta("¿Hasta cuándo te duró el efecto?")
        SelectorHora(fin, etiquetaAhora = "no sé") { fin = it }
        if (info?.conNivel == true) {
            Etiqueta("¿Hasta qué nivel llegaste? (0 a 10)")
            Escala(max, 0, 10, Catalogo.anclasNivel(c0.sustancia)) { max = it }
        }
        if (info != null && c0.sustancia != "alcohol" && c0.sustancia != "cafeina" && info.vias.isNotEmpty()) {
            Etiqueta("¿Cómo?")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { info.vias.forEach { o -> Ficha(o.nombre, via == o.id) { via = if (via == o.id) null else o.id } } }
        }
        CantidadConsumo(c0.sustancia, info?.unidad ?: "", cantidad) { cantidad = it }
        Etiqueta("¿Con quién? (opcional)")
        SelectorConQuien(con) { con = it }
        Etiqueta("Cruda o bajón al día siguiente (0 a 5)")
        Escala(cruda, 0, 5, Catalogo.CRUDA) { cruda = it }
        Etiqueta("Síntomas (opcional)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.SINTOMAS.forEach { o -> val a = o.id in sintomas; Ficha(o.nombre, a) { sintomas = if (a) sintomas - o.id else sintomas + o.id } }
        }
        if ("otro" in sintomas) Ayuda("Escribe cuál en la nota.", Modifier.padding(top = 4.dp))
        CampoTexto(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholder = { Text("Nota (opcional): @persona, #lugar…") },
            visualTransformation = rememberMarcas())
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            var confirmar by remember { mutableStateOf(false) }
            BotonTexto(onClick = {
                if (!confirmar) { confirmar = true; return@BotonTexto }
                Repo.borrarConsumo(c0.id); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Registro borrado") { Repo.guardarConsumo(c0) }
                Toast.makeText(ctx, "Borrado. Queda 7 días en la papelera.", Toast.LENGTH_LONG).show(); terminar()
            }) { Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = if (confirmar) MaterialTheme.colorScheme.error else androidx.compose.ui.graphics.Color.Unspecified) }
            BotonPrincipal("Guardar") {
                val actual = Repo.consumo(c0.id) ?: c0
                val ini = inicio?.aMillis() ?: Fechas.millis(actual.inicio)
                // «no sé» en el fin lo deja vacío (antes conservaba el que hubiera); la cantidad en 0 la quita.
                Repo.guardarConsumo(actual.copy(inicio = Fechas.isoDeMillis(ini), fecha = Fechas.diaLogicoDeMillis(ini), fin = fin?.aMillis()?.coerceAtLeast(ini)?.let { Fechas.isoDeMillis(it) },
                    nivel_max = max, cantidad = cantidad.takeIf { it > 0 }, via = via, con_quien = con, otra_nombre = if (actual.sustancia == "otra") otra.trim() else actual.otra_nombre,
                    cruda = cruda, sintomas = sintomas, nota = nota.trim(), completo = true))
                Toast.makeText(ctx, "Guardado", Toast.LENGTH_SHORT).show(); terminar()
            }
        }
    }

    /** La ubicación de ahora, para un gasto de ahora. */
    private fun ubicarGasto(g: Gasto) {
        lifecycleScope.launch {
            val loc = Ubicacion.ultima(this@QuickActivity)
            if (loc != null) Repo.gasto(g.id)?.let { Repo.guardarGasto(it.copy(lat = loc.latitude, lon = loc.longitude)) }
        }
    }

    /**
     * Gasto o ingreso (N-29). Arriba, «Gasto | Ingreso». En gasto, los frecuentes registran con un toque (⊕ → Gasto →
     * Café: tres toques); si no, categoría → subcategoría (con su monto habitual) → «Guardar gasto». Escribir el monto o la nota
     * queda para lo muy específico.
     */
    @Composable
    private fun GastoRapido(catInicial: String?, subInicial: String?, inicial: Gasto?, embebido: Boolean = false,
                            cuandoInicial: java.time.LocalDateTime? = null, alGuardar: ((String) -> Unit)? = null) {
        val D = io.github.ruddy2003.vestigium.data.Dinero
        fun dinero(v: Double) = io.github.ruddy2003.vestigium.ui.pantallas.dinero(v)
        var entra by remember { mutableStateOf(false) }
        var monto by remember { mutableStateOf("") }
        // El monto que puso la subcategoría: si eliges otra, se reemplaza; si lo escribiste tú, se respeta.
        var auto by remember { mutableStateOf<String?>(null) }
        var guardando by remember { mutableStateOf(false) }
        androidx.compose.runtime.SideEffect { sinGuardar = monto.isNotBlank() && monto != auto && !guardando; if (sinGuardar) queFalta = "un monto sin guardar" }
        androidx.compose.runtime.DisposableEffect(Unit) { onDispose { sinGuardar = false; avisado = false } }
        // El monto toma el foco al abrir «Gasto o ingreso» (hallazgo 17); no dentro del momento ni si un frecuente ya registró.
        val focoMonto = remember { androidx.compose.ui.focus.FocusRequester() }
        if (!embebido && inicial == null) androidx.compose.runtime.LaunchedEffect(Unit) { runCatching { focoMonto.requestFocus() } }
        var cat by remember { mutableStateOf(catInicial.takeIf { inicial == null }) }
        var sub by remember { mutableStateOf("") }
        var imprevisto by remember { mutableStateOf(false) }
        var repite by remember { mutableStateOf(false) }
        var fijo by remember { mutableStateOf(false) }
        androidx.compose.runtime.LaunchedEffect(Unit) { D.asegurarCategoriasNuevas() }
        var nota by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        var registrado by remember { mutableStateOf(inicial) }
        var corrigiendo by remember { mutableStateOf(false) }
        var cuando by remember { mutableStateOf(cuandoInicial) }
        val c = Repo.config
        // Dentro del momento no se cierra la hoja: se avisa a quien lo abrió y el formulario queda limpio para otro.
        fun listo(texto: String) {
            if (alGuardar == null) { Toast.makeText(this@QuickActivity, texto, Toast.LENGTH_SHORT).show(); terminar(); return }
            alGuardar(texto); monto = ""; auto = null; cat = null; sub = ""; nota = ""; imprevisto = false; repite = false; fijo = false; guardando = false
        }
        if (!embebido) Encabezado("Gasto o ingreso", if (entra) "Lo que entra: sueldo, beca, venta, regalo." else "En el momento de pagar. Un frecuente registra con un toque.")
        Row(Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Ficha("− Gasto", !entra) { if (entra) { entra = false; cat = null; sub = ""; error = null } }
            Ficha("+ Ingreso", entra) { if (!entra) { entra = true; cat = null; sub = ""; error = null } }
        }
        // Lo que acaba de registrar un frecuente: se ve, se corrige o se deshace.
        registrado?.let { r ->
            val g = Repo.gasto(r.id) ?: r
            Surface(shape = formaPiezaQuick(Estilo.tema), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${g.subcategoria.ifBlank { g.categoria }} ${dinero(g.monto)} registrado", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                    BotonTexto(onClick = { corrigiendo = true }) { Text("cambiar monto") }
                    BotonTexto(onClick = { Repo.borrarGasto(g.id); registrado = null; Toast.makeText(this@QuickActivity, "Gasto quitado", Toast.LENGTH_SHORT).show() }) { Text("deshacer") }
                }
            }
        }
        if (!entra) {
            val frecuentes = remember(registrado) { D.frecuentes() }
            if (frecuentes.isNotEmpty()) {
                Etiqueta("Frecuentes · un toque registra")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    frecuentes.forEach { f ->
                        Ficha("${f.sub} ${dinero(f.monto)}", registrado?.let { it.categoria == f.categoria && it.subcategoria == f.sub } == true) {
                            val (g, nuevo) = D.registrarFrecuente(f.categoria, f.sub, f.monto)
                            if (nuevo) ubicarGasto(g)
                            registrado = g
                            if (embebido) alGuardar?.invoke("${f.sub} ${dinero(f.monto)} registrado")
                        }
                    }
                }
            }
        }
        Etiqueta(if (entra) "Monto" else "Otro gasto")
        CampoTexto(value = monto, onValueChange = { monto = D.limpiarMonto(it); error = null }, modifier = Modifier.fillMaxWidth().focusRequester(focoMonto),
            prefix = { Text("$ ") }, placeholder = { Text("0") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), textStyle = MaterialTheme.typography.headlineSmall)
        Etiqueta("Categoría")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            (if (entra) c.categorias_ingreso else c.categorias).forEach { k -> Ficha(k, cat == k) { cat = if (cat == k) null else k; sub = ""; error = null } }
        }
        val subs = cat?.takeIf { !entra }?.let { D.subcategorias(it) }.orEmpty()
        if (subs.isNotEmpty()) {
            Etiqueta("Subcategoría")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                subs.forEach { s ->
                    val h = D.montoHabitual(cat!!, s.nombre)
                    Ficha(s.nombre + (h?.let { " · ${dinero(it)}" } ?: ""), sub == s.nombre) {
                        sub = if (sub == s.nombre) "" else s.nombre
                        if (sub.isNotEmpty() && h != null && (monto.isBlank() || monto == auto)) { monto = D.texto(h); auto = monto }
                    }
                }
            }
        }
        Etiqueta("Cuándo")
        SelectorHora(cuando) { cuando = it }
        if (entra) Interruptor("¿Se repite? (sueldo, beca, renta)", fijo) { fijo = it }
        else {
            Interruptor("¿Fue imprevisto?", imprevisto) { imprevisto = it }
            Interruptor("¿Se repite? (renta, suscripción, servicios)", repite) { repite = it }
        }
        CampoTexto(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, minLines = 2)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (registrado != null && !embebido) BotonTexto(onClick = { terminar() }) { Text("Listo") }
            BotonPrincipal(if (entra) "Guardar ingreso" else "Guardar gasto") {
                // Un toque doble no registra dos veces.
                if (guardando) return@BotonPrincipal
                val v = monto.toDoubleOrNull()
                val k = cuando
                val fecha = k?.let { Fechas.iso(it.toLocalDate()) } ?: Fechas.hoyCalendario()
                val hora = k?.let { "%02d:%02d".format(it.hour, it.minute) } ?: Fechas.horaAhora()
                when {
                    v == null || v <= 0 -> error = "Escribe un monto mayor a cero."
                    cat == null -> error = "Elige una categoría."
                    entra -> {
                        val i = io.github.ruddy2003.vestigium.data.Ingreso(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, nota.trim(), fijo, ts_creado = Fechas.ahoraIso())
                        Repo.guardarIngreso(i)
                        guardando = true
                        listo("Ingreso registrado: ${dinero(i.monto)} · ${i.categoria}")
                    }
                    else -> {
                        val g = Gasto(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, imprevisto, nota.trim(), subcategoria = sub, se_repite = repite, ts_creado = Fechas.ahoraIso())
                        Repo.guardarGasto(g)
                        // La ubicación de ahora solo sirve si el gasto es de ahora.
                        if (k == null) ubicarGasto(g)
                        guardando = true
                        listo("Gasto registrado: ${dinero(g.monto)} · ${g.categoria}" + (if (sub.isNotBlank()) " → $sub" else ""))
                    }
                }
            }
        }
        if (corrigiendo) registrado?.let { r ->
            io.github.ruddy2003.vestigium.ui.pantallas.DialogoGasto(Repo.gasto(r.id) ?: r) { corrigiendo = false; registrado = Repo.gasto(r.id) ?: registrado }
        }
    }

    @Composable
    private fun SuenoRapido() {
        // El sueño va al día del despertar (la fecha del calendario), como las horas del reloj (H-NAV-7).
        val fecha = Fechas.hoyCalendario()
        var d by remember { mutableStateOf(Repo.dia(fecha)) }
        fun guardar(n: io.github.ruddy2003.vestigium.data.Dia) { d = n; Repo.guardarDia(n) }
        Encabezado("Tu sueño", "Primero cuéntalo, antes de levantarte: los detalles se van en minutos. Lo demás, después.", fecha)
        // La calidad que guardó el toque en el widget: se ve y se quita si fue sin querer (N-15).
        val delWidget = remember { intent.getIntExtra(CALIDAD, 0).takeIf { it in 1..5 } }
        if (delWidget != null && d.sueno_calidad == delWidget) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Guardado con el toque: calidad $delWidget.", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            BotonTexto(onClick = { guardar(d.copy(sueno_calidad = null)) }) { Text("quitar") }
        }
        // La narración arriba, en el libro de noche del tema (es lo que se olvida primero); lo demás, debajo.
        // «Listo» va fijo al pie de la hoja: todo esto se guarda al tocar.
        io.github.ruddy2003.vestigium.ui.FormularioSueno(fecha, d, narracionPrimero = true) { guardar(it) }
    }

    @Composable
    private fun Minimo() {
        val fecha = Fechas.diaLogico()
        var d by remember { mutableStateOf(Repo.dia(fecha)) }
        Encabezado("Día difícil", "Solo un número. Un día registrado así vale mucho más que un día vacío.")
        Escala(d.valencia, 1, 7, Catalogo.ANCLAS["valencia"]) { v -> d = d.copy(valencia = v, dia_dificil = true); Repo.guardarDia(d) }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End) {
            Spacer(Modifier.width(1.dp))
            BotonPrincipal("Listo", habilitado = d.valencia != null) { terminar() }
        }
    }
}

// =====================================================================
//  La hoja del registro rápido, fuera de la Activity, para que se pueda capturar en Paparazzi con datos de ejemplo.
// =====================================================================

/**
 * El marco de la hoja del registro rápido: el borde de arriba con la silueta del tema (frontón, arco polilobulado, alero de
 * pagoda, pestaña de carpeta, cartel arrancado…), la textura del fondo, el adorno de ese borde y la banda del tema.
 * [contenido] va debajo y se desplaza; el adorno se queda quieto arriba y [pie] (por ejemplo «Listo»), fijo abajo.
 */
@Composable
internal fun HojaQuick(modifier: Modifier = Modifier, pie: (@Composable () -> Unit)? = null, contenido: @Composable ColumnScope.() -> Unit) {
    val t = Estilo.tema
    val e = MaterialTheme.colorScheme
    Surface(modifier = modifier, shape = formaHojaQuick(t), color = e.background, border = BorderStroke(1.dp, e.outline)) {
        Column(
            Modifier.fondoTema(t, e).drawWithContent { drawContent(); with(io.github.ruddy2003.vestigium.ui.comun.disenoComun(t)) { adornoQuick(io.github.ruddy2003.vestigium.ui.comun.EstadoAdorno(e.primary, e.secondary, e.background), e.background.luminance() < 0.5f) } }
                .padding(start = 18.dp, end = 18.dp, bottom = 18.dp, top = 12.dp + perfilHojaQuick(t)),
        ) {
            // Lo que se desplaza ocupa lo que haga falta, sin pasar del alto de la pantalla; [pie] se queda siempre a la vista.
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                Meandro(Modifier.padding(bottom = 12.dp))
                contenido()
            }
            pie?.invoke()
        }
    }
}

/**
 * El título de cada vista de la hoja, con el nombre que le da el tema (`Estilo.pantallaEnLinea`). Debajo, una línea con el
 * nombre en español (si el tema lo cambia) o el folio del tema, y [linea] (hora y día); luego [aviso] y la ayuda [sub].
 */
@Composable
internal fun EncabezadoQuick(titulo: String, linea: String, sub: String, aviso: @Composable () -> Unit = {}) {
    val (nombre, espanol, extra) = Estilo.pantallaEnLinea(titulo, Romano.latin(titulo))
    Text(nombre, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
    val cabeza = extra?.let { "$it · " } ?: (Estilo.comentario + (espanol?.let { it.lowercase() + " · " } ?: ""))
    Text(cabeza + linea, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
    aviso()
    Ayuda(sub, Modifier.padding(top = 2.dp, bottom = 12.dp))
}

/**
 * El menú del botón ⊕: arriba [arriba] (la cuadrícula del ánimo, ya a la vista) o, sin ella, el botón grande del registro de
 * ánimo; debajo, [botones] (texto y destino) de dos en dos, con la forma del tema.
 */
@Composable
internal fun MenuQuick(botones: List<Pair<String, String>>, arriba: (@Composable () -> Unit)? = null, alElegir: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val forma = formaPiezaQuick(Estilo.tema)
    if (arriba != null) arriba()
    else Surface(onClick = { alElegir(QuickActivity.M_ANIMO) }, modifier = Modifier.fillMaxWidth().heightIn(min = 76.dp), shape = forma, color = cs.primary) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Registro de ánimo", style = MaterialTheme.typography.titleLarge, color = cs.onPrimary)
            Text("cómo estás, qué sientes y qué piensas", style = MaterialTheme.typography.labelMedium, color = cs.onPrimary.copy(alpha = 0.85f))
        }
    }
    FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 2) {
        botones.forEach { (texto, destino) ->
            Surface(onClick = { alElegir(destino) }, modifier = Modifier.weight(1f).heightIn(min = 56.dp), shape = forma, color = cs.surface, border = BorderStroke(1.dp, cs.outline)) {
                Box(contentAlignment = Alignment.Center) { Text(texto, style = MaterialTheme.typography.titleSmall) }
            }
        }
    }
}

/** Con [visible] en false la pieza sigue compuesta (conserva su estado), pero no ocupa lugar, no se dibuja ni recibe toques. */
private fun Modifier.soloVisible(visible: Boolean): Modifier = if (visible) this else this.layout { medible, limites ->
    medible.measure(limites)
    layout(0, 0) { }
}

/** Una forma que se traza en píxeles con [u] = píxeles por dp, para que las medidas no dependan del tamaño de la pieza. */
private fun formaDp(trazo: Path.(w: Float, h: Float, u: Float) -> Unit): Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(Path().apply { trazo(size.width, size.height, density.density) })
}

/** Cuánto baja, desde lo más alto, el borde de arriba de la hoja: lo que la hoja deja libre arriba para su adorno. */
private fun perfilHojaQuick(t: TemaId): Dp = when (t) {
    TemaId.ROMANO -> 12.dp
    TemaId.URBE -> 16.dp
    TemaId.CIBER -> 4.dp
    TemaId.NEON -> 6.dp
    TemaId.HELENO -> 14.dp
    TemaId.EGIPTO -> 16.dp
    TemaId.MEDIEVAL -> 10.dp
    TemaId.TEMPLARIO -> 9.dp
    TemaId.DISTOPIA -> 12.dp
    TemaId.REFUGIO -> 6.dp
    TemaId.ORDEN -> 6.dp
    TemaId.VAPOR -> 7.dp
    TemaId.LABORATORIO -> 8.dp
    TemaId.COLONOS -> 4.dp
    TemaId.NAPOLEON -> 14.dp
    TemaId.DECO -> 16.dp
    TemaId.BOTANICO -> 8.dp
    TemaId.RUINAS -> 10.dp
    TemaId.NAVIDAD -> 7.dp
    TemaId.SEDA -> 14.dp
    TemaId.EDO -> 16.dp
    TemaId.GOTICO -> 18.dp
    TemaId.ARCANO -> 10.dp
    TemaId.ESPACIO -> 12.dp
    TemaId.ABISMO -> 9.dp
    TemaId.ANDALUS -> 18.dp
    TemaId.MESO -> 16.dp
    TemaId.MONGOL -> 14.dp
    TemaId.RENACIMIENTO -> 6.dp
    TemaId.PIRATA -> 8.dp
    TemaId.URSS -> 20.dp
    TemaId.REVOLUCION -> 9.dp
    TemaId.ANTIGUAS -> 16.dp
    TemaId.CUEVA -> 18.dp
    TemaId.BASICO -> 4.dp
}

/** La silueta de la hoja: solo cambia el borde de arriba; abajo llega al borde de la pantalla. */
private fun formaHojaQuick(t: TemaId): Shape = when (t) {
    // Cartela de esquinas cóncavas, como una placa con inscripción.
    TemaId.ROMANO -> formaDp { w, h, u ->
        val r = 12 * u
        moveTo(r, 0f); lineTo(w - r, 0f); quadraticTo(w - r, r, w, r); lineTo(w, h); lineTo(0f, h); lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }
    // El arco rebajado de un portón, de lado a lado.
    TemaId.URBE -> formaDp { w, h, u ->
        val f = 16 * u
        moveTo(0f, f); quadraticTo(w / 2, -f, w, f); lineTo(w, h); lineTo(0f, h); close()
    }
    // Ventana de consola con una esquina cortada.
    TemaId.CIBER -> formaDp { w, h, u ->
        val c = 18 * u
        moveTo(0f, 0f); lineTo(w - c, 0f); lineTo(w, c); lineTo(w, h); lineTo(0f, h); close()
    }
    // La placa del letrero, con las esquinas de arriba redondas.
    TemaId.NEON -> formaDp { w, h, u ->
        val c = 22 * u
        moveTo(0f, c); quadraticTo(0f, 0f, c, 0f); lineTo(w - c, 0f); quadraticTo(w, 0f, w, c); lineTo(w, h); lineTo(0f, h); close()
    }
    // Frontón de templo de lado a lado.
    TemaId.HELENO -> formaDp { w, h, u ->
        val f = 14 * u
        moveTo(0f, f); lineTo(w / 2, 0f); lineTo(w, f); lineTo(w, h); lineTo(0f, h); close()
    }
    // Estela de remate redondo: un arco rebajado de lado a lado.
    TemaId.EGIPTO -> formaDp { w, h, u ->
        val f = 16 * u
        moveTo(0f, h); lineTo(0f, f); quadraticTo(w / 2, -f * 0.9f, w, f); lineTo(w, h); close()
    }
    // Borde de pergamino que ondula, como un rollo a medio abrir.
    TemaId.MEDIEVAL -> formaDp { w, h, u ->
        val a = 5 * u
        moveTo(0f, a)
        var x = 0f
        while (x < w) { lineTo(x, a + a * sin(2 * PI * 3 * x / w).toFloat()); x += 3 * u }
        lineTo(w, a); lineTo(w, h); lineTo(0f, h); close()
    }
    // El adarve: merlones y troneras en el borde de arriba.
    TemaId.TEMPLARIO -> formaDp { w, h, u ->
        val m = 16 * u; val a = 9 * u
        moveTo(0f, h); lineTo(0f, 0f)
        var x = 0f; var k = 0
        while (x + m < w) { val y = if (k % 2 == 0) 0f else a; lineTo(x, y); lineTo(x + m, y); x += maxOf(m, 1f); k++ }
        lineTo(x, if (k % 2 == 0) 0f else a); lineTo(w, if (k % 2 == 0) 0f else a); lineTo(w, h); close()
    }
    // Carpeta de expediente con su pestaña a la izquierda.
    TemaId.DISTOPIA -> formaDp { w, h, u ->
        val p = 12 * u; val a = w * 0.38f
        moveTo(0f, 0f); lineTo(a, 0f); lineTo(a + p, p); lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Pantalla de tubo, de esquinas muy redondas.
    TemaId.REFUGIO -> formaDp { w, h, u ->
        addRoundRect(RoundRect(0f, 0f, w, h, topLeftCornerRadius = CornerRadius(26 * u), topRightCornerRadius = CornerRadius(26 * u),
            bottomRightCornerRadius = CornerRadius.Zero, bottomLeftCornerRadius = CornerRadius.Zero))
    }
    // Compuerta de esquinas cortadas.
    TemaId.ORDEN -> formaDp { w, h, u ->
        val c = 14 * u
        moveTo(c, 0f); lineTo(w - c, 0f); lineTo(w, c); lineTo(w, h); lineTo(0f, h); lineTo(0f, c); close()
    }
    // El billete de tren: las dos esquinas de arriba recortadas hacia dentro.
    TemaId.VAPOR -> formaDp { w, h, u ->
        val c = 13 * u
        moveTo(c, 0f); lineTo(w - c, 0f); quadraticTo(w - c, c, w, c); lineTo(w, h); lineTo(0f, h); lineTo(0f, c); quadraticTo(c, c, c, 0f); close()
    }
    // Vaso de precipitados: el pico para verter, a la izquierda.
    TemaId.LABORATORIO -> formaDp { w, h, u ->
        val p = 8 * u; val r = 3 * u
        moveTo(0f, 0f); quadraticTo(6 * u, p, 22 * u, p); lineTo(w - r, p); quadraticTo(w, p, w, p + r); lineTo(w, h); lineTo(0f, h); close()
    }
    // Boleto de gaceta: una muesca a cada lado.
    TemaId.COLONOS -> formaDp { w, h, u ->
        val r = 6 * u; val m = 16 * u
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, m - r)
        arcTo(Rect(Offset(w, m), r), 270f, -180f, false)
        lineTo(w, h); lineTo(0f, h); lineTo(0f, m + r)
        arcTo(Rect(Offset(0f, m), r), 90f, -180f, false)
        close()
    }
    // Chapa con un medallón al centro, para la estrella.
    TemaId.NAPOLEON -> formaDp { w, h, u ->
        val p = 14 * u
        moveTo(0f, p); lineTo(w / 2 - p, p)
        arcTo(Rect(Offset(w / 2, p), p), 180f, 180f, false)
        lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // La hoja sube al centro en tres escalones, como la corona de una torre.
    TemaId.DECO -> formaDp { w, h, u ->
        val p = 16 * u; val a = p / 3; val s = 14 * u
        moveTo(0f, p); lineTo(w / 2 - 3 * s, p); lineTo(w / 2 - 3 * s, 2 * a); lineTo(w / 2 - 2 * s, 2 * a); lineTo(w / 2 - 2 * s, a)
        lineTo(w / 2 - s, a); lineTo(w / 2 - s, 0f); lineTo(w / 2 + s, 0f); lineTo(w / 2 + s, a); lineTo(w / 2 + 2 * s, a)
        lineTo(w / 2 + 2 * s, 2 * a); lineTo(w / 2 + 3 * s, 2 * a); lineTo(w / 2 + 3 * s, p); lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Hoja: una esquina muy redonda y la otra en punta.
    TemaId.BOTANICO -> formaDp { w, h, u ->
        addRoundRect(RoundRect(0f, 0f, w, h, topLeftCornerRadius = CornerRadius(30 * u), topRightCornerRadius = CornerRadius(2 * u),
            bottomRightCornerRadius = CornerRadius.Zero, bottomLeftCornerRadius = CornerRadius.Zero))
    }
    // Una losa de hormigón con el borde de arriba quebrado y la esquina derecha desprendida en escalones.
    TemaId.RUINAS -> formaDp { w, h, u ->
        moveTo(0f, 6 * u); lineTo(w * 0.3f, 3 * u); lineTo(w * 0.34f, 7 * u); lineTo(w * 0.62f, 2 * u); lineTo(w - 26 * u, 4 * u)
        lineTo(w - 18 * u, 10 * u); lineTo(w - 10 * u, 8 * u); lineTo(w, 16 * u); lineTo(w, h); lineTo(0f, h); close()
    }
    // Glaseado de nieve: festones arriba.
    TemaId.NAVIDAD -> formaDp { w, h, u ->
        val f = 7 * u; val n = (w / (44 * u)).toInt().coerceAtLeast(3); val paso = w / n
        moveTo(0f, f)
        for (i in 0 until n) quadraticTo(i * paso + paso / 2, -f, (i + 1) * paso, f)
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Alero de pagoda con las puntas hacia arriba.
    TemaId.SEDA -> formaDp { w, h, u ->
        val p = 12 * u
        moveTo(0f, 0f); quadraticTo(w * 0.1f, p, w * 0.28f, p); lineTo(w * 0.72f, p); quadraticTo(w * 0.9f, p, w, 0f)
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Karahafu: el alero ondulado de las puertas de templos y castillos, que sube en curva hasta el centro.
    TemaId.EDO -> formaDp { w, h, u ->
        val p = 16 * u
        moveTo(0f, p); cubicTo(w * 0.25f, p, w * 0.38f, p * 0.9f, w * 0.44f, p * 0.35f); quadraticTo(w * 0.5f, -p * 0.05f, w * 0.56f, p * 0.35f)
        cubicTo(w * 0.62f, p * 0.9f, w * 0.75f, p, w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Gablete apuntado al centro, como sobre una portada.
    TemaId.GOTICO -> formaDp { w, h, u ->
        val a = 18 * u; val b = 36 * u
        moveTo(0f, a); lineTo(w / 2 - b, a)
        quadraticTo(w / 2 - b, a * 0.3f, w / 2, 0f); quadraticTo(w / 2 + b, a * 0.3f, w / 2 + b, a)
        lineTo(w, a); lineTo(w, h); lineTo(0f, h); close()
    }
    // La hoja del legajo: la orilla de arriba rasgada a mano.
    TemaId.ARCANO -> formaDp { w, h, u ->
        val d = 5 * u; var x = 0f; var k = 0
        moveTo(0f, d)
        while (x < w) { x = minOf(x + 11 * u, w); lineTo(x, if (k % 2 == 0) 0f else if (k % 3 == 0) d * 1.6f else d); k++ }
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Consola de mando: la parte central más alta.
    TemaId.ESPACIO -> formaDp { w, h, u ->
        val p = 12 * u; val a = w * 0.3f; val r = 14 * u
        moveTo(0f, p + r); quadraticTo(0f, p, r, p); lineTo(a, p); lineTo(a + p, 0f); lineTo(w - a - p, 0f); lineTo(w - a, p)
        lineTo(w - r, p); quadraticTo(w, p, w, p + r); lineTo(w, h); lineTo(0f, h); close()
    }
    // La superficie del mar: el borde de arriba es una ola de cuatro crestas.
    TemaId.ABISMO -> formaDp { w, h, u ->
        val p = 8 * u; val n = 4
        moveTo(0f, p)
        for (i in 0 until n) {
            val x0 = w * i / n; val x1 = w * (i + 1) / n
            quadraticTo((x0 + x1) / 2, if (i % 2 == 0) -p * 0.4f else p * 2.2f, x1, p)
        }
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Arco polilobulado al centro: cinco lóbulos que suben hacia la clave.
    TemaId.ANDALUS -> formaDp { w, h, u ->
        val p = 18 * u; val r = 8 * u; val x0 = w / 2 - 5 * r
        moveTo(0f, p); lineTo(x0, p)
        floatArrayOf(2f, 6f, 8f, 6f, 2f).forEachIndexed { i, k -> arcTo(Rect(Offset(x0 + r + 2 * r * i, p - k * u), r), 180f, 180f, false) }
        lineTo(x0 + 10 * r, p); lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Un templo sobre la hoja: al centro, el borde sube en gradas como el perfil de una pirámide.
    TemaId.MESO -> formaDp { w, h, u ->
        val p = 16 * u; val g = 4 * u; val s = 10 * u; val x0 = w / 2 - 4 * s
        moveTo(0f, p); lineTo(x0, p)
        for (k in 0 until 3) { lineTo(x0 + k * s, p - (k + 1) * g); lineTo(x0 + (k + 1) * s, p - (k + 1) * g) }
        for (k in 3 downTo 1) { lineTo(x0 + (9 - k) * s, p - k * g); lineTo(x0 + (9 - k) * s, p - (k - 1) * g) }
        lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Esquinas escalonadas, como la orilla de un fieltro bordado.
    TemaId.MONGOL -> formaDp { w, h, u ->
        val s = 7 * u
        moveTo(0f, 2 * s); lineTo(s, 2 * s); lineTo(s, s); lineTo(2 * s, s); lineTo(2 * s, 0f)
        lineTo(w - 2 * s, 0f); lineTo(w - 2 * s, s); lineTo(w - s, s); lineTo(w - s, 2 * s); lineTo(w, 2 * s)
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Hoja vista en perspectiva: un trapecio.
    TemaId.RENACIMIENTO -> formaDp { w, h, u ->
        val d = 14 * u
        moveTo(d, 0f); lineTo(w - d, 0f); lineTo(w, h); lineTo(0f, h); close()
    }
    // La lona de una vela tensada: el borde de arriba se comba hacia adentro entre dos ollaos.
    TemaId.PIRATA -> formaDp { w, h, u ->
        val f = 8 * u
        moveTo(0f, 0f); quadraticTo(w / 2, f * 2, w, 0f); lineTo(w, h); lineTo(0f, h); close()
    }
    // Diagonal de cartel: el borde baja de izquierda a derecha.
    TemaId.URSS -> formaDp { w, h, u -> moveTo(0f, 0f); lineTo(w, 20 * u); lineTo(w, h); lineTo(0f, h); close() }
    // Cartel arrancado: el borde de arriba en jirones.
    TemaId.REVOLUCION -> formaDp { w, h, u ->
        val f = 9 * u; val paso = 7 * u
        val jirones = floatArrayOf(0.1f, 0.9f, 0.3f, 1f, 0f, 0.7f, 0.4f, 0.95f, 0.15f, 0.6f, 0.05f)
        moveTo(0f, f * 0.5f)
        var x = 0f; var i = 0
        while (x < w) { x = (x + paso).coerceAtMost(w); lineTo(x, f * jirones[i % jirones.size]); i++ }
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Losa de piedra tallada a golpes.
    TemaId.ANTIGUAS -> formaDp { w, h, u ->
        moveTo(0f, 16 * u); lineTo(16 * u, 0f); lineTo(w * 0.42f, 3 * u); lineTo(w * 0.58f, 0f); lineTo(w - 16 * u, 4 * u); lineTo(w, 16 * u)
        lineTo(w, h); lineTo(0f, h); close()
    }
    // El borde de arriba de una laja partida: ondula, más alto a un lado.
    TemaId.CUEVA -> formaDp { w, h, u ->
        moveTo(0f, 14 * u); quadraticTo(w * 0.2f, 2 * u, w * 0.45f, 8 * u); quadraticTo(w * 0.7f, 14 * u, w * 0.86f, 4 * u); quadraticTo(w * 0.95f, 0f, w, 10 * u)
        lineTo(w, h); lineTo(0f, h); close()
    }
    TemaId.BASICO -> RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
}


/** Estrella de [puntas] puntas; [dentro] es el radio de los valles respecto al de las puntas. */
private fun DrawScope.estrellaHoja(c: Offset, r: Float, color: Color, puntas: Int = 5, dentro: Float = 0.45f) {
    val p = Path()
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * dentro
        val a = Math.toRadians(-90.0 + 180.0 / puntas * i)
        val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    drawPath(p, color)
}

/**
 * La forma de las piezas de la hoja (el botón grande, los del menú y los avisos), en el mismo lenguaje que los botones
 * del tema: frontón bajo, arco apuntado, boleto con muescas, banderola… Las medidas van en dp, no en proporción, para
 * que sirvan igual en un botón que en un aviso alto.
 */
private fun formaPiezaQuick(t: TemaId): Shape = when (t) {
    TemaId.ROMANO -> RoundedCornerShape(2.dp)
    TemaId.URBE -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 2.dp, bottomStart = 2.dp)
    TemaId.CIBER -> CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp)
    TemaId.NEON -> RoundedCornerShape(50)
    // Frontón muy bajo.
    TemaId.HELENO -> formaDp { w, h, u -> val f = 8 * u; moveTo(0f, f); lineTo(w / 2, 0f); lineTo(w, f); lineTo(w, h); lineTo(0f, h); close() }
    // Cartucho: redondo a la izquierda y cerrado por la barra del nudo a la derecha.
    TemaId.EGIPTO -> formaDp { w, h, _ ->
        val r = minOf(h / 2, w / 2)
        moveTo(r, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(r, h); arcTo(Rect(0f, 0f, 2 * r, h), 90f, 180f, false); close()
    }
    // Esquinas cóncavas, como un marco de miniatura.
    TemaId.MEDIEVAL -> formaDp { w, h, u ->
        val r = 7 * u
        moveTo(r, 0f); lineTo(w - r, 0f); quadraticTo(w - r, r, w, r); lineTo(w, h - r); quadraticTo(w - r, h - r, w - r, h)
        lineTo(r, h); quadraticTo(r, h - r, 0f, h - r); lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }
    // El escudete: recto arriba y redondeado abajo.
    TemaId.TEMPLARIO -> RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
    TemaId.DISTOPIA -> RectangleShape
    TemaId.REFUGIO -> RoundedCornerShape(18.dp)
    TemaId.ORDEN -> CutCornerShape(8.dp)
    TemaId.VAPOR -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 2.dp, bottomStart = 2.dp)
    TemaId.LABORATORIO -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 4.dp, bottomStart = 4.dp)
    // Boleto: una muesca a cada lado, a media altura.
    TemaId.COLONOS -> formaDp { w, h, u ->
        val r = 6 * u; val m = h / 2
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, m - r)
        arcTo(Rect(Offset(w, m), r), 270f, -180f, false)
        lineTo(w, h); lineTo(0f, h); lineTo(0f, m + r)
        arcTo(Rect(Offset(0f, m), r), 90f, -180f, false)
        close()
    }
    // Placa de puntas, como una chapa de chacó.
    TemaId.NAPOLEON -> formaDp { w, h, u ->
        val c = minOf(h / 2, 12 * u)
        moveTo(c, 0f); lineTo(w - c, 0f); lineTo(w, h / 2); lineTo(w - c, h); lineTo(c, h); lineTo(0f, h / 2); close()
    }
    // Placa de ascensor: cada esquina metida en un escalón.
    TemaId.DECO -> formaDp { w, h, u ->
        val a = minOf(h * 0.22f, 6 * u)
        moveTo(a, 0f); lineTo(w - a, 0f); lineTo(w - a, a); lineTo(w, a); lineTo(w, h - a); lineTo(w - a, h - a); lineTo(w - a, h)
        lineTo(a, h); lineTo(a, h - a); lineTo(0f, h - a); lineTo(0f, a); lineTo(a, a); close()
    }
    // Hoja: dos esquinas redondas y dos en punta.
    TemaId.BOTANICO -> RoundedCornerShape(topStart = 16.dp, topEnd = 2.dp, bottomEnd = 16.dp, bottomStart = 2.dp)
    // Un bloque desportillado: dos esquinas cortadas, desiguales.
    TemaId.RUINAS -> CutCornerShape(topStart = 3.dp, topEnd = 10.dp, bottomEnd = 3.dp, bottomStart = 6.dp)
    // Caja de regalo con su cinta: esquinas suaves y una muesca arriba al centro.
    TemaId.NAVIDAD -> formaDp { w, h, u ->
        val r = 8 * u; val n = 5 * u
        moveTo(r, 0f); lineTo(w / 2 - n, 0f); lineTo(w / 2, n * 0.8f); lineTo(w / 2 + n, 0f); lineTo(w - r, 0f); quadraticTo(w, 0f, w, r)
        lineTo(w, h - r); quadraticTo(w, h, w - r, h); lineTo(r, h); quadraticTo(0f, h, 0f, h - r); lineTo(0f, r); quadraticTo(0f, 0f, r, 0f); close()
    }
    // Tablilla con alero, como el rótulo de una puerta.
    TemaId.SEDA -> formaDp { w, h, u ->
        val a = 5 * u
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - a, 8 * u); lineTo(w - a, h); lineTo(a, h); lineTo(a, 8 * u); close()
    }
    // Ema: la tablilla votiva, con sus dos esquinas de arriba en chaflán.
    TemaId.EDO -> formaDp { w, h, u ->
        val a = 7 * u
        moveTo(0f, a); lineTo(a, 0f); lineTo(w - a, 0f); lineTo(w, a); lineTo(w, h); lineTo(0f, h); close()
    }
    // Arco apuntado.
    TemaId.GOTICO -> formaDp { w, h, u ->
        val a = 14 * u
        moveTo(0f, h); lineTo(0f, a); quadraticTo(0f, a * 0.25f, w / 2, 0f); quadraticTo(w, a * 0.25f, w, a); lineTo(w, h); close()
    }
    // La ficha del fichero: la esquina de arriba a la derecha recortada.
    TemaId.ARCANO -> formaDp { w, h, u -> val d = 8 * u; moveTo(0f, 0f); lineTo(w - d, 0f); lineTo(w, d); lineTo(w, h); lineTo(0f, h); close() }
    // Tecla de consola, más angosta arriba.
    TemaId.ESPACIO -> formaDp { w, h, u -> val d = 6 * u; moveTo(d, 0f); lineTo(w - d, 0f); lineTo(w, h); lineTo(0f, h); close() }
    // El flotador del batiscafo: proa redonda a la izquierda, popa de esquinas suaves a la derecha.
    TemaId.ABISMO -> formaDp { w, h, u ->
        val r = h / 2; val r2 = 4 * u
        moveTo(r, 0f); lineTo(w - r2, 0f); quadraticTo(w, 0f, w, r2); lineTo(w, h - r2); quadraticTo(w, h, w - r2, h); lineTo(r, h)
        quadraticTo(0f, h, 0f, h / 2); quadraticTo(0f, 0f, r, 0f); close()
    }
    // Arco de medio punto, de radio fijo.
    TemaId.ANDALUS -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
    // El bloque de un glifo: un cuadro de esquinas muy redondeadas.
    TemaId.MESO -> RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
    // Esquinas escalonadas, como la orilla del fieltro.
    TemaId.MONGOL -> formaDp { w, h, u ->
        val s = 4 * u
        moveTo(2 * s, 0f); lineTo(w - 2 * s, 0f); lineTo(w - 2 * s, s); lineTo(w - s, s); lineTo(w - s, 2 * s); lineTo(w, 2 * s)
        lineTo(w, h - 2 * s); lineTo(w - s, h - 2 * s); lineTo(w - s, h - s); lineTo(w - 2 * s, h - s); lineTo(w - 2 * s, h)
        lineTo(2 * s, h); lineTo(2 * s, h - s); lineTo(s, h - s); lineTo(s, h - 2 * s); lineTo(0f, h - 2 * s)
        lineTo(0f, 2 * s); lineTo(s, 2 * s); lineTo(s, s); lineTo(2 * s, s); close()
    }
    // Hoja con la esquina doblada.
    TemaId.RENACIMIENTO -> CutCornerShape(bottomEnd = 12.dp)
    // La duela del barril: arriba y abajo, un poco combada.
    TemaId.PIRATA -> formaDp { w, h, u -> val f = 3 * u; moveTo(0f, f); quadraticTo(w / 2, -f, w, f); lineTo(w, h - f); quadraticTo(w / 2, h + f, 0f, h - f); close() }
    // Paralelogramo de cartel.
    TemaId.URSS -> formaDp { w, h, u -> val d = 10 * u; moveTo(d, 0f); lineTo(w, 0f); lineTo(w - d, h); lineTo(0f, h); close() }
    // Banderola con cola de golondrina.
    TemaId.REVOLUCION -> formaDp { w, h, u -> val d = minOf(h * 0.25f, 10 * u); moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - d, h / 2); lineTo(w, h); lineTo(0f, h); close() }
    // Losa de piedra irregular.
    TemaId.ANTIGUAS -> formaDp { w, h, u ->
        moveTo(6 * u, 0f); lineTo(w - 2 * u, 3 * u); lineTo(w, h - 10 * u); lineTo(w - 8 * u, h); lineTo(2 * u, h - 3 * u); lineTo(0f, 8 * u); close()
    }
    // Un canto rodado aplastado: esquinas muy blandas y desparejas.
    TemaId.CUEVA -> formaDp { w, h, u ->
        moveTo(10 * u, 0f); lineTo(w - 14 * u, 2 * u); quadraticTo(w, 2 * u, w, h * 0.5f); quadraticTo(w - 2 * u, h, w - 16 * u, h); lineTo(12 * u, h - 1 * u)
        quadraticTo(0f, h - 2 * u, 0f, h * 0.5f); quadraticTo(0f, 0f, 10 * u, 0f); close()
    }
    TemaId.BASICO -> RoundedCornerShape(12.dp)
}

// ============================================================================ esquema 21: masturbación, intimidad y episodios
// Los campos van aquí, fuera de la actividad, para que la hoja rápida y el diálogo de editar un momento (Momentos.kt) usen los
// mismos. Reciben el momento como está y [cambiar], que dice cómo cambiarlo: la hoja lo guarda al instante; el diálogo, al
// tocar «Guardar». La hora va aparte, porque cada uno la mueve a su manera.

/** Si la masturbación tiene su recaída gemela, la duración y el aparato de la recaída siguen a los de la masturbación. */
internal fun sincronizarGemela(n: Momento) {
    if (n.tipo != "masturbacion") return
    val g = io.github.ruddy2003.vestigium.data.Recaidas.gemela(n)?.takeIf { it.tipo == "recaida" } ?: return
    if (g.duracion_min != n.duracion_min || g.dispositivo != n.dispositivo) Repo.guardarMomento(g.copy(duracion_min = n.duracion_min, dispositivo = n.dispositivo))
}

/** La recaída se queda aunque la masturbación ya no sea con pornografía: las dos pierden el vínculo y la recaída sigue sola. */
internal fun desligarGemela(m: Momento) {
    val g = io.github.ruddy2003.vestigium.data.Recaidas.gemela(m) ?: return
    Repo.guardarMomento(g.copy(vinculo = null))
    (Repo.momento(m.id) ?: m).let { Repo.guardarMomento(it.copy(vinculo = null)) }
}

/**
 * La masturbación dejó de ser con pornografía y tenía su recaída gemela: «¿Quitar también la recaída?». «Quitarla» la borra
 * ([io.github.ruddy2003.vestigium.data.Recaidas.soltarGemela]); «dejarla» la deja como recaída sola. [alTerminar] dice si se quitó.
 */
@Composable
internal fun PreguntaGemela(m: Momento, alTerminar: (quitada: Boolean) -> Unit) {
    Dialogo(
        onDismissRequest = { alTerminar(false) },
        title = { Text("¿Quitar también la recaída?") },
        text = { Text("Con pornografía se anotó también una recaída a la misma hora. Si ya no fue así, puedes quitarla.") },
        confirmButton = { BotonTexto(onClick = {
            val actual = Repo.momento(m.id) ?: m
            io.github.ruddy2003.vestigium.data.Recaidas.soltarGemela(actual)
            Repo.momento(actual.id)?.let { Repo.guardarMomento(it.copy(vinculo = null)) }
            alTerminar(true)
        }) { Text("quitarla") } },
        dismissButton = { BotonTexto(onClick = { desligarGemela(m); alTerminar(false) }) { Text("dejarla") } },
    )
}

/** Una ficha con su nombre y, al lado, una aclaración entre paréntesis en letra chica (como [Ficha], con la forma del tema). */
@Composable
private fun FichaConAyuda(texto: String, ayuda: String, activa: Boolean, onClick: () -> Unit) {
    if (ayuda.isBlank()) { Ficha(texto, activa, onClick); return }
    val sentir = rememberSentir()
    val toque = remember { MutableInteractionSource() }
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    Surface(
        onClick = { sentir(Sentidos.Evento.ELEGIR); onClick() }, shape = formaBotonTema(tema), interactionSource = toque, modifier = Modifier.presion(toque, 0.93f),
        color = if (activa) e.primaryContainer else e.surface,
        border = BorderStroke(1.dp, if (activa) e.primary else e.outline),
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            if (activa) {
                androidx.compose.foundation.Canvas(Modifier.size(11.dp)) { perillaTema(tema, e, center, size.minDimension / 2f, e.primary, e.primaryContainer) }
                Spacer(Modifier.width(7.dp))
            }
            val nombre = MaterialTheme.typography.labelLarge.copy(fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal)
            // Una aclaración corta va al lado («pico»); una larga, debajo, para que no se encime con el nombre.
            if (ayuda.length <= 18) {
                Text(texto, style = nombre)
                Text(" ($ayuda)", style = MaterialTheme.typography.labelSmall, color = e.onSurfaceVariant)
            } else Column {
                Text(texto, style = nombre)
                Text("($ayuda)", style = MaterialTheme.typography.labelSmall, color = e.onSurfaceVariant)
            }
        }
    }
}

/** Un texto libre que se guarda limpio (sin espacios a los lados; vacío = null) sin estorbar mientras escribes. */
@Composable
private fun CampoOtro(clave: String, inicial: String?, etiqueta: String = "¿Cuál?", alCambiar: (String?) -> Unit) {
    var t by remember(clave) { mutableStateOf(inicial.orEmpty()) }
    CampoTexto(value = t, onValueChange = { v -> t = v; alCambiar(v.trim().ifBlank { null }) },
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text(etiqueta) })
}

/** «¿Cómo fue? (opcional)»: varias líneas, en [Momento.texto]. */
@Composable
private fun CampoComoFue(m: Momento, placeholder: String, cambiar: ((Momento) -> Momento) -> Unit) {
    var t by remember(m.id) { mutableStateOf(m.texto) }
    CampoTexto(value = t, onValueChange = { v -> t = v; cambiar { it.copy(texto = v) } },
        modifier = Modifier.fillMaxWidth(), minLines = 3, placeholder = { Text(placeholder) }, visualTransformation = rememberMarcas())
}

/** Las emociones de después: el mismo selector que en «¿Cómo estás ahora?». */
@Composable
private fun EmocionesDespues(m: Momento, cambiar: ((Momento) -> Momento) -> Unit) {
    var eleccion by remember(m.id) { mutableStateOf(EleccionEmociones(m.emociones, m.emocion_otra.orEmpty(), m.intensidades)) }
    Etiqueta("¿Qué sentiste después? (opcional, varios)")
    SelectorEmociones(eleccion, Repo.config.discreto) { e ->
        eleccion = e
        cambiar { it.copy(emociones = e.emociones, emocion_otra = if (Catalogo.OTRO in e.emociones) e.otra.trim().ifBlank { null } else null, intensidades = e.intensidades) }
    }
}

/** Dónde (Catalogo.SITIOS); «otro» con su texto. */
@Composable
private fun CampoSitio(m: Momento, cambiar: ((Momento) -> Momento) -> Unit) {
    FilaFichas(Catalogo.SITIOS, m.sitio) { v -> cambiar { it.copy(sitio = v, sitio_otro = if (v == "otro") it.sitio_otro else null) } }
    if (m.sitio == "otro") CampoOtro(m.id + "|sitio", m.sitio_otro) { t -> cambiar { it.copy(sitio_otro = t) } }
}

/**
 * Con quién, de tu gente: las de Tu gente y las que más mencionas (como «¿A quién conoce?»), varias a la vez, y un campo para
 * escribir otra. Se guardan sin @ y en minúsculas, como las menciones.
 */
@Composable
internal fun ConQuienPersonas(clave: String, sel: List<String>, onCambio: (List<String>) -> Unit) {
    val c = Repo.config
    val menc = remember(Repo.dias, Repo.momentos, c.equivalencias) { io.github.ruddy2003.vestigium.data.Personas.menciones() }
    val conocidas = remember(menc, c.personas, c.perfil, c.equivalencias) { io.github.ruddy2003.vestigium.data.Personas.todas(menc, c) }
    var otra by remember(clave) { mutableStateOf("") }
    // Como una mención: sin @, en minúsculas, los espacios a guion bajo y solo letras, números y guion bajo.
    val nombre = io.github.ruddy2003.vestigium.data.Texto.etiqueta(otra).filter { it.isLetterOrDigit() || it == '_' }
    // A la vista, las 24 que más aparecen; al escribir en el campo salen todas las que coinciden (antes, quien no estaba entre
    // esas 24 solo se podía poner escribiendo su nombre completo).
    val todas = (sel + if (nombre.isEmpty()) conocidas.take(24) else conocidas.filter { nombre in it }).distinct()
    if (todas.isNotEmpty()) FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        todas.forEach { p -> Ficha("@$p", p in sel) { onCambio(if (p in sel) sel - p else sel + p) } }
    }
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        CampoTexto(value = otra, onValueChange = { otra = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("@persona") })
        BotonTexto(onClick = { if (nombre.isNotEmpty() && nombre !in sel) onCambio(sel + nombre); otra = "" }, enabled = nombre.isNotEmpty()) { Text("agregar") }
    }
}

/**
 * Masturbación: cuánto duró, con qué (con pornografía, «Cuenta como recaída»), en qué aparato (solo con los estímulos de
 * Catalogo.ESTIMULOS_CON_APARATO), dónde, satisfacción, emociones de después y cómo fue. [alCambiarEstimulo] recibe el
 * estímulo de antes y el nuevo, para crear o quitar la recaída gemela.
 */
@Composable
internal fun CamposMasturbacion(m: Momento, cambiar: ((Momento) -> Momento) -> Unit, alCambiarEstimulo: (antes: String?, despues: String?) -> Unit = { _, _ -> }) {
    Etiqueta("¿Cuánto duró?")
    SelectorDuracion(m.duracion_min) { d -> cambiar { it.copy(duracion_min = d) } }
    Etiqueta("Con qué")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Catalogo.ESTIMULOS.forEach { o ->
            Ficha(o.nombre, m.estimulo == o.id) {
                val antes = m.estimulo
                val nuevo = if (antes == o.id) null else o.id
                cambiar { it.copy(estimulo = nuevo, estimulo_otro = if (nuevo == "otro") it.estimulo_otro else null,
                    dispositivo = if (nuevo in Catalogo.ESTIMULOS_CON_APARATO) it.dispositivo else null) }
                alCambiarEstimulo(antes, nuevo)
            }
        }
    }
    if (m.estimulo == "pornografia") Ayuda("Cuenta como recaída: se anota también una recaída a la misma hora.", Modifier.padding(top = 6.dp))
    if (m.estimulo == "otro") CampoOtro(m.id + "|estimulo", m.estimulo_otro) { t -> cambiar { it.copy(estimulo_otro = t) } }
    if (m.estimulo in Catalogo.ESTIMULOS_CON_APARATO) {
        Etiqueta("En qué aparato")
        FilaFichas(Catalogo.DISPOSITIVOS, m.dispositivo) { v -> cambiar { it.copy(dispositivo = v) } }
    }
    Etiqueta("Dónde")
    CampoSitio(m, cambiar)
    Etiqueta("Satisfacción (1 a 5)")
    Escala(m.satisfaccion, 1, 5, Catalogo.ANCLAS["satisfaccion"]) { v -> cambiar { it.copy(satisfaccion = v) } }
    EmocionesDespues(m, cambiar)
    Etiqueta("Descripción")
    CampoComoFue(m, "¿Cómo fue? (opcional)", cambiar)
}

/**
 * Intimidad, en este orden: qué pasó (varias, por grupo; guarda también el nivel, Catalogo.nivelIntimidad), cómo fue, con
 * quién, las escalas de 1 a 5 y las emociones de después, y los detalles plegados (orgasmo, protección, quién empezó, dónde y
 * duración). Todo es opcional salvo lo que pasó.
 */
@Composable
internal fun CamposIntimidad(m: Momento, cambiar: ((Momento) -> Momento) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Etiqueta("Qué pasó (varias)")
    Catalogo.PRACTICAS.groupBy { it.grupo }.forEach { (grupo, l) ->
        Text(grupo, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp, bottom = 2.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            l.forEach { p ->
                val activa = p.id in m.practicas
                FichaConAyuda(p.nombre, p.ayuda, activa) {
                    val nuevas = if (activa) m.practicas - p.id else m.practicas + p.id
                    cambiar { it.copy(practicas = nuevas, nivel = Catalogo.nivelIntimidad(nuevas), practica_otra = if ("otra" in nuevas) it.practica_otra else null) }
                }
            }
        }
    }
    if ("otra" in m.practicas) CampoOtro(m.id + "|practica", m.practica_otra) { t -> cambiar { it.copy(practica_otra = t) } }
    Etiqueta("Descripción")
    CampoComoFue(m, "¿Cómo fue? (opcional)", cambiar)
    Etiqueta("Con quién")
    ConQuienPersonas(m.id, m.personas) { l -> cambiar { it.copy(personas = l) } }
    Etiqueta("Satisfacción (1 a 5)")
    Escala(m.satisfaccion, 1, 5, Catalogo.ANCLAS["satisfaccion"]) { v -> cambiar { it.copy(satisfaccion = v) } }
    Etiqueta("Placer (1 a 5)")
    Escala(m.placer, 1, 5, Catalogo.ANCLAS["placer"]) { v -> cambiar { it.copy(placer = v) } }
    Etiqueta("Conexión (1 a 5)")
    Escala(m.conexion, 1, 5, Catalogo.ANCLAS["conexion"]) { v -> cambiar { it.copy(conexion = v) } }
    Etiqueta("Comodidad (1 a 5)")
    Escala(m.comodidad, 1, 5, Catalogo.ANCLAS["comodidad"]) { v -> cambiar { it.copy(comodidad = v) } }
    EmocionesDespues(m, cambiar)
    // Los detalles, plegados; se abren solos si ya hay alguno.
    var detalles by remember(m.id) { mutableStateOf(listOf(m.orgasmo, m.orgasmo_otra, m.proteccion, m.iniciativa, m.sitio).any { it != null } || m.duracion_min != null) }
    BotonTexto(onClick = { detalles = !detalles }, modifier = Modifier.padding(top = 8.dp)) {
        Text(if (detalles) "− detalles" else "＋ detalles: orgasmo, protección, quién empezó, dónde, duración", style = MaterialTheme.typography.labelLarge)
    }
    if (detalles) {
        Etiqueta("Orgasmo, tú")
        FilaFichas(Catalogo.SI_NO_NOSE, m.orgasmo) { v -> cambiar { it.copy(orgasmo = v) } }
        Etiqueta("Orgasmo, la otra persona")
        FilaFichas(Catalogo.SI_NO_NOSE, m.orgasmo_otra) { v -> cambiar { it.copy(orgasmo_otra = v) } }
        Etiqueta("Protección")
        FilaFichas(Catalogo.PROTECCION, m.proteccion) { v -> cambiar { it.copy(proteccion = v) } }
        Etiqueta("Quién empezó")
        FilaFichas(Catalogo.INICIATIVA, m.iniciativa) { v -> cambiar { it.copy(iniciativa = v) } }
        Etiqueta("Dónde")
        CampoSitio(m, cambiar)
        Etiqueta("¿Cuánto duró?")
        SelectorDuracion(m.duracion_min) { d -> cambiar { it.copy(duracion_min = d) } }
    }
}

/**
 * Episodio: qué fue («otro» con su texto), qué tan fuerte (1 a 5), cuánto duró (en la enfermedad, en días: se guarda
 * días × 1440 minutos), los síntomas (solo en una crisis), qué lo detonó, con quién (solo en una pelea) y una nota.
 */
@Composable
internal fun CamposEpisodio(m: Momento, cambiar: ((Momento) -> Momento) -> Unit) {
    val enDias = m.episodio in Catalogo.EPISODIOS_EN_DIAS
    Etiqueta("¿Qué fue?")
    FilaFichas(Catalogo.EPISODIOS, m.episodio) { v ->
        val nuevoEnDias = v in Catalogo.EPISODIOS_EN_DIAS
        cambiar { it.copy(episodio = v, episodio_otro = if (v == "otro") it.episodio_otro else null,
            sintomas = if (v == "crisis") it.sintomas else emptyList(), personas = if (v == "pelea") it.personas else emptyList(),
            // Minutos y días no se mezclan: al cambiar de unidad, la duración se vuelve a decir.
            duracion_min = if (nuevoEnDias != enDias) null else it.duracion_min) }
    }
    if (m.episodio == "otro") CampoOtro(m.id + "|episodio", m.episodio_otro) { t -> cambiar { it.copy(episodio_otro = t) } }
    Etiqueta("Qué tan fuerte (1 a 5)")
    Escala(m.intensidad, 1, 5, Catalogo.ANCLAS["episodio"], secuencial = true) { v -> cambiar { it.copy(intensidad = v) } }
    if (enDias) {
        Etiqueta("¿Cuántos días duró?")
        SelectorDias(m.id, m.duracion_min?.let { (it + 720) / 1440 }) { d -> cambiar { it.copy(duracion_min = d?.let { n -> n * 1440 }) } }
    } else {
        Etiqueta("¿Cuánto duró?")
        SelectorDuracion(m.duracion_min, listOf(5, 10, 15, 30, 45, 60, 90, 120, 180, 240)) { d -> cambiar { it.copy(duracion_min = d) } }
    }
    if (m.episodio == "crisis") {
        Etiqueta("Síntomas (varios)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.SINTOMAS_CRISIS.forEach { o ->
                val activo = o.id in m.sintomas
                Ficha(o.nombre, activo) { val l = if (activo) m.sintomas - o.id else m.sintomas + o.id; cambiar { it.copy(sintomas = l) } }
            }
        }
    }
    Etiqueta("Qué lo detonó (varios)")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Catalogo.DETONANTES.forEach { d ->
            val activo = d.id in m.detonantes
            Ficha(d.nombre, activo) {
                val l = if (activo) m.detonantes - d.id else m.detonantes + d.id
                cambiar { it.copy(detonantes = l, detonante_otro = if ("otro" in l) it.detonante_otro else null) }
            }
        }
    }
    if ("otro" in m.detonantes) CampoOtro(m.id + "|detonante", m.detonante_otro) { t -> cambiar { it.copy(detonante_otro = t) } }
    if (m.episodio == "pelea") {
        Etiqueta("Con quién")
        ConQuienPersonas(m.id, m.personas) { l -> cambiar { it.copy(personas = l) } }
    }
    Etiqueta("Nota (opcional)")
    CampoComoFue(m, "¿Qué pasó? Usa @persona y #tema", cambiar)
}

/** Duración en días (la de una enfermedad): atajos y «otra». */
@Composable
private fun SelectorDias(clave: String, dias: Int?, onCambio: (Int?) -> Unit) {
    val opciones = listOf(1, 2, 3, 5, 7, 14)
    var texto by remember(clave) { mutableStateOf(if (dias != null && dias !in opciones) "$dias" else "") }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        opciones.forEach { d -> Ficha(if (d == 1) "1 día" else "$d días", dias == d && texto.isEmpty()) { texto = ""; onCambio(if (dias == d) null else d) } }
    }
    CampoTexto(value = texto, onValueChange = { t -> texto = t.filter { it.isDigit() }.take(3); onCambio(texto.toIntOrNull()?.takeIf { it > 0 }) },
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Otra (días)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
}
