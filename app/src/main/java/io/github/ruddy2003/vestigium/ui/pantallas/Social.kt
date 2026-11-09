package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Personas
import io.github.ruddy2003.vestigium.data.RedPersonas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.porNumero
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.EstiloGrafica
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.figuraTema
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Social (9 nov 2026): Tu gente, la Red y la Constelación en un solo lugar. Las dos vistas del grafo usan las mismas
 * menciones, los mismos colores por relación y la misma ficha; cambia solo cómo se acomodan las personas:
 * - **red**: un gráfico de fuerzas (Fruchterman–Reingold, `RedPersonas.acomodar`); las líneas firmes son lo que dijiste en
 *   Tu gente y las finas, personas que aparecen juntas al menos dos días;
 * - **cielo**: más cerca del centro, más reciente; más grande, más días mencionada (`Constelacion.calcular`).
 * El grafo se abre en pantalla completa y se navega como el mapa: pellizcar, arrastrar, − / + / «todo» y doble toque.
 */
object GrafoSocial {
    /** La vista y el periodo que se recuerdan al entrar (las rutas viejas «red» y «constelacion» eligen su vista). */
    var vista by mutableStateOf("red")
    var periodo by mutableStateOf(365)
    var soloDichas by mutableStateOf(false)

    /** Una persona del grafo, con su lugar en un cuadrado de 0 a 1. */
    class Nodo(val nombre: String, val dias: Int, val ultima: String?, val relacion: String?, val grupo: String?, val grado: Int, val x: Float, val y: Float)
    /** Una línea: [firme] si la dijiste en Tu gente. */
    class Lazo(val a: String, val b: String, val tipo: String?, val juntos: Int)
    class Modelo(val vista: String, val nodos: List<Nodo>, val lazos: List<Lazo>, val grupos: Int) {
        val porNombre: Map<String, Nodo> = nodos.associateBy { it.nombre }
        val maxDias: Int = nodos.maxOfOrNull { it.dias }?.coerceAtLeast(1) ?: 1
    }

    fun red(soloDichas: Boolean): Modelo {
        val (n, a) = RedPersonas.calcular(minJuntos = 2)
        val aa = (if (soloDichas) a.filter { it.tipo != null } else a)
        RedPersonas.grados(n, aa)
        RedPersonas.acomodar(n, aa)
        val grupos = RedPersonas.componentes(n, aa)
        val lazos = aa.filter { it.tipo != "no_se_conocen" }.map { Lazo(it.a, it.b, it.tipo, it.juntos) }
        return Modelo("red", n.map { Nodo(it.nombre, it.dias, null, it.relacion, it.grupo, it.grado, it.x, it.y) }, lazos, grupos)
    }

    /** El cielo de la Constelación, con las mismas reglas que tenía su pantalla, en coordenadas de 0 a 1. */
    fun cielo(periodo: Int, hoy: String = Fechas.diaLogico()): Modelo {
        val desde = if (periodo == 0) "0000-01-01" else Fechas.sumar(hoy, -periodo.toLong())
        val (estrellas, juntos) = Constelacion.calcular(desde, hoy)
        val diasPeriodo = if (periodo == 0) (Repo.dias.keys.minOrNull()?.let { Fechas.diasEntre(it, hoy).toFloat() } ?: 365f).coerceAtLeast(30f) else periodo.toFloat()
        val radio = 0.5f * 0.94f
        val nodos = estrellas.mapIndexed { i, e ->
            val h = (e.nombre.hashCode() and 0x7fffffff)
            // El ángulo no significa nada (H-6-14): el de girasol con un poco de azar fijo por nombre, para que no se encimen.
            val ang = i * 2.399963 + (h % 1000) / 1000.0 * 0.35
            val dias = Fechas.diasEntre(e.ultima, hoy).toFloat().coerceAtLeast(0f)
            val d = (0.30f + 0.64f * (dias / diasPeriodo).coerceIn(0f, 1f).pow(0.6f)) * radio
            Nodo(e.nombre, e.dias, e.ultima, e.relacion, Personas.grupo(e.relacion), 0, 0.5f + (d * cos(ang)).toFloat(), 0.5f + (d * sin(ang)).toFloat())
        }
        val lazos = juntos.map { (k, n) -> Lazo(k.first, k.second, null, n) }
        val grados = HashMap<String, Int>(); lazos.forEach { l -> grados[l.a] = (grados[l.a] ?: 0) + 1; grados[l.b] = (grados[l.b] ?: 0) + 1 }
        return Modelo("cielo", nodos.map { Nodo(it.nombre, it.dias, it.ultima, it.relacion, it.grupo, grados[it.nombre] ?: 0, it.x, it.y) }, lazos, 0)
    }

    /** Dónde queda en pantalla un punto del cuadrado [p] (0 a 1), con el acercamiento alrededor del centro. */
    fun aPantalla(p: Offset, w: Float, h: Float, zoom: Float, pan: Offset): Offset {
        val c = Offset(w / 2, h / 2)
        return (Offset(p.x * w, p.y * h) - c) * zoom + c + pan
    }

    /** El pan nuevo al acercar con factor [factor] alrededor de [centroide]: el punto bajo los dedos se queda donde está. */
    fun acercar(zoom: Float, pan: Offset, factor: Float, centroide: Offset, desplazamiento: Offset, w: Float, h: Float, max: Float = 16f): Pair<Float, Offset> {
        val nz = (zoom * factor).coerceIn(1f, max)
        val c = Offset(w / 2, h / 2)
        val p = (centroide - c - pan) / zoom
        val np = centroide - c - p * nz + desplazamiento
        val lim = Offset(w * (nz - 1) / 2 + w * 0.2f, h * (nz - 1) / 2 + h * 0.2f)
        return nz to Offset(np.x.coerceIn(-lim.x, lim.x), np.y.coerceIn(-lim.y, lim.y))
    }
}

private fun tonos(cs: ColorScheme) = mapOf("familia" to cs.tertiary, "pareja" to cs.error, "amistad" to cs.secondary, "casa" to cs.primary,
    "trabajo" to cs.outline, "expareja" to cs.error.copy(alpha = 0.45f))

/** Dibuja el grafo de [m] con el acercamiento [zoom] y [pan]. Los puntos y las letras no crecen al acercar: se separan. */
internal fun DrawScope.dibujarGrafo(m: GrafoSocial.Modelo, zoom: Float, pan: Offset, elegido: String?, cs: ColorScheme, eg: EstiloGrafica, tema: TemaId,
                                    medidor: TextMeasurer, estilo: TextStyle, fase: Float, animar: Boolean) {
    val w = size.width; val h = size.height
    val tonos = tonos(cs)
    fun pos(n: GrafoSocial.Nodo) = GrafoSocial.aPantalla(Offset(n.x, n.y), w, h, zoom, pan)
    clipRect {
        if (m.vista == "cielo") {
            val c = GrafoSocial.aPantalla(Offset(0.5f, 0.5f), w, h, zoom, pan)
            val radio = minOf(w, h) / 2 * zoom
            drawCircle(Brush.radialGradient(listOf(cs.primary.copy(alpha = 0.10f), cs.surface.copy(alpha = 0f)), c, radio), radio, c)
            // Polvo de estrellas de fondo, siempre en el mismo sitio.
            val azar = java.util.Random(11)
            repeat(90) { val p = GrafoSocial.aPantalla(Offset(azar.nextFloat(), azar.nextFloat()), w, h, zoom, pan); drawCircle(cs.onSurface.copy(alpha = 0.10f + azar.nextFloat() * 0.12f), 1.2f, p) }
            listOf(0.33f, 0.66f, 1f).forEach { k -> drawCircle(cs.outline.copy(alpha = 0.25f), radio * 0.94f * k, c, style = Stroke(1f)) }
            m.lazos.forEach { l ->
                val a = m.porNombre[l.a] ?: return@forEach; val b = m.porNombre[l.b] ?: return@forEach
                val res = elegido != null && (l.a == elegido || l.b == elegido)
                drawLine(cs.primary.copy(alpha = if (res) 0.85f else (0.12f + 0.06f * l.juntos).coerceAtMost(0.5f)), pos(a), pos(b), (0.6f + l.juntos * 0.15f).coerceAtMost(2f) * density)
            }
            // Tú, en el centro, con la figura del tema.
            figuraTema(tema, cs.primary, c, 6f * density, cs.surface); drawCircle(cs.primary.copy(alpha = 0.3f), 10f * density, c, style = Stroke(1f * density))
        } else {
            m.lazos.forEach { l ->
                val a = m.porNombre[l.a] ?: return@forEach; val b = m.porNombre[l.b] ?: return@forEach
                val res = elegido != null && (l.a == elegido || l.b == elegido)
                val col = if (l.tipo != null) cs.primary else cs.onSurfaceVariant
                val colLazo = col.copy(alpha = if (res) 0.9f else if (l.tipo != null) 0.55f else 0.22f)
                val ancho = (if (l.tipo != null) 2f else 1f) * density
                // Los colegas conservan su guion propio (distingue el tipo de lazo); los demás llevan el trazo del tema.
                if (l.tipo == "colegas") drawLine(colLazo, pos(a), pos(b), ancho, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f * density, 4f * density)))
                else lineaGrafica(eg, listOf(pos(a), pos(b)), colLazo, ancho)
            }
        }
        // Más letras a la vista cuanto más cerca: al principio, las de quienes más aparecen; con zoom, todas.
        val umbral = m.maxDias / (4f * zoom * zoom)
        m.nodos.forEachIndexed { i, n ->
            val p = pos(n)
            if (p.x < -40f || p.y < -40f || p.x > w + 40f || p.y > h + 40f) return@forEachIndexed
            val col = tonos[n.grupo ?: n.relacion] ?: if (m.vista == "cielo") cs.onSurface else cs.onSurfaceVariant
            val r = if (m.vista == "cielo") (2.5f + 7f * sqrt(n.dias.toFloat() / m.maxDias)) * density else (4f + 8f * sqrt(n.dias.toFloat() / m.maxDias)) * density
            if (m.vista == "cielo") {
                val brillo = if (animar) 0.65f + 0.35f * ((sin((fase + i * 0.137f) * 2 * Math.PI).toFloat() + 1) / 2) else 1f
                drawCircle(col.copy(alpha = 0.18f * brillo), r * 2.2f, p)
                figuraTema(tema, col.copy(alpha = brillo), p, r * 1.25f, cs.surface)
            } else puntoGrafica(eg, p, r, col)
            if (n.nombre == elegido) drawCircle(cs.onSurface, r + 4f * density, p, style = Stroke(1.5f * density))
            if (n.dias >= umbral || n.nombre == elegido || m.nodos.size <= 15) {
                val t = medidor.measure("@" + n.nombre, estilo)
                // Debajo del punto; si no cabe (puntos pegados al borde de abajo), encima, para no taparlo.
                val abajo = p.y + r + 2f
                val y = if (abajo + t.size.height <= h) abajo else (p.y - r - 2f - t.size.height).coerceAtLeast(0f)
                drawText(t, topLeft = Offset((p.x - t.size.width / 2).coerceIn(0f, (w - t.size.width).coerceAtLeast(0f)), y))
            }
        }
    }
}

/** La persona más cercana al toque [o] (en pantalla), si cae cerca de su punto. */
private fun tocada(m: GrafoSocial.Modelo, o: Offset, w: Float, h: Float, zoom: Float, pan: Offset, dens: Float): String? =
    m.nodos.minByOrNull { (GrafoSocial.aPantalla(Offset(it.x, it.y), w, h, zoom, pan) - o).getDistanceSquared() }
        ?.takeIf { (GrafoSocial.aPantalla(Offset(it.x, it.y), w, h, zoom, pan) - o).getDistance() < 36f * dens }?.nombre

/** El modelo de la vista elegida, calculado fuera del hilo principal (el acomodo de fuerzas son 300 vueltas). */
@Composable
internal fun rememberModelo(vistaFija: String? = null): GrafoSocial.Modelo? {
    val vista = vistaFija ?: GrafoSocial.vista; val periodo = GrafoSocial.periodo; val solo = GrafoSocial.soloDichas
    // En las capturas (síncrono) se calcula de una vez, como en Logros y Cobertura.
    val sinc = io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono
    val inicial = if (sinc) runCatching { if (vista == "cielo") GrafoSocial.cielo(periodo) else GrafoSocial.red(solo) }.getOrNull() else null
    val m by produceState(inicial, Repo.dias, Repo.momentos, Repo.config.vinculos, Repo.config.personas, vista, periodo, solo) {
        if (!sinc) value = withContext(Dispatchers.Default) { runCatching { if (vista == "cielo") GrafoSocial.cielo(periodo) else GrafoSocial.red(solo) }.getOrNull() }
    }
    return m?.takeIf { it.vista == vista }
}

@Composable
private fun SelectorVista(alCambiar: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Ficha("red", GrafoSocial.vista == "red") { GrafoSocial.vista = "red"; alCambiar() }
        Ficha("cielo", GrafoSocial.vista == "cielo") { GrafoSocial.vista = "cielo"; alCambiar() }
        Text("·", color = MaterialTheme.colorScheme.outline, modifier = Modifier.align(Alignment.CenterVertically))
        if (GrafoSocial.vista == "red") {
            Ficha("todo", !GrafoSocial.soloDichas) { GrafoSocial.soloDichas = false; alCambiar() }
            Ficha("solo lo que dije", GrafoSocial.soloDichas) { GrafoSocial.soloDichas = true; alCambiar() }
        } else listOf(90 to "3 meses", 365 to "un año", 0 to "siempre").forEach { (d, n) -> Ficha(n, GrafoSocial.periodo == d) { GrafoSocial.periodo = d; alCambiar() } }
    }
}

/** La ficha de la persona tocada: relación, días, con quién aparece. */
@Composable
private fun FichaPersona(m: GrafoSocial.Modelo, e: String, nav: Nav) {
    val n = m.porNombre[e] ?: return
    val suyos = m.lazos.filter { it.a == e || it.b == e }.sortedByDescending { it.juntos }
    io.github.ruddy2003.vestigium.ui.TextoMarcado("@$e", style = MaterialTheme.typography.titleMedium)
    Ayuda(listOfNotNull(n.relacion?.let { Personas.nombreRelacion(it) }, porNumero(n.dias, "1 día", "${n.dias} días") + " mencionada",
        n.ultima?.let { "la última vez ${Fechas.relativo(it)}" }, if (n.grado > 0) "unida a ${n.grado}" else null).joinToString(" · "))
    if (suyos.isNotEmpty()) io.github.ruddy2003.vestigium.ui.TextoMarcado(
        "Con " + suyos.take(8).joinToString(" · ") { l -> "@" + (if (l.a == e) l.b else l.a) + (l.tipo?.let { " (${RedPersonas.nombreTipo(it).lowercase()})" } ?: " (${porNumero(l.juntos, "1 día", "${l.juntos} días")} juntas)") },
        style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
    BotonTexto(onClick = { nav.ir("personas") }) { Text("ver en Tu gente →", style = MaterialTheme.typography.labelLarge) }
}

@Composable
private fun animacionCielo(): Float {
    val t = rememberInfiniteTransition(label = "cielo")
    return t.animateFloat(0f, 1f, infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "titila").value
}

@Composable
fun PantallaSocial(nav: Nav) {
    val cs = MaterialTheme.colorScheme
    val m = rememberModelo()
    var elegido by remember { mutableStateOf<String?>(null) }
    val medidor = rememberTextMeasurer()
    val estilo = MaterialTheme.typography.labelSmall.copy(color = cs.onSurface)
    val eg = estiloGrafica()
    val tema = Estilo.tema
    val animar = Repo.config.animaciones && GrafoSocial.vista == "cielo"
    val fase = if (animar) animacionCielo() else 0f
    Hoja("Social", nav.atras, "Tu gente y cómo se conecta. «Red»: quién conoce a quién. «Cielo»: más cerca del centro, más reciente; más grande, más días mencionada.") {
        SelectorVista { elegido = null }
        when {
            m == null -> Ayuda("Acomodando a tu gente…", Modifier.padding(top = 12.dp))
            m.nodos.isEmpty() -> Vacio("Menciona a personas con @ en tu diario o en un momento, o di quién conoce a quién en Tu gente.")
            else -> {
                if (m.vista == "red") {
                    val n = m.nodos.size
                    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Cifra("$n", "personas", Modifier.weight(1f))
                        Cifra(if (n > 1) "%.0f%%".format(100.0 * m.lazos.size / (n * (n - 1) / 2.0)) else "—", "densidad", Modifier.weight(1f))
                        Cifra("${m.grupos}", if (m.grupos == 1) "grupo" else "grupos", Modifier.weight(1f))
                    }
                }
                val dens = androidx.compose.ui.platform.LocalDensity.current.density
                Canvas(Modifier.fillMaxWidth().aspectRatio(1f).padding(top = 10.dp).pointerInput(m) {
                    detectTapGestures { o -> elegido = tocada(m, o, size.width.toFloat(), size.height.toFloat(), 1f, Offset.Zero, dens) }
                }) { dibujarGrafo(m, 1f, Offset.Zero, elegido, cs, eg, tema, medidor, estilo, fase, animar) }
                BotonPrincipal("abrir en pantalla completa", Modifier.fillMaxWidth().padding(top = 6.dp)) { nav.ir("social_grafo") }
                Tarjeta(Modifier.padding(top = 10.dp)) {
                    val e = elegido
                    if (e == null) Ayuda("Toca a una persona para ver con quién aparece.") else FichaPersona(m, e, nav)
                }
                Ayuda("Colores: quien vive contigo, familia, pareja, amistades, colegas y exparejas (tenue); las demás, del color del texto. Las relaciones vienen de «Tu gente» y de «Conocerte». En R: vm_red().",
                    Modifier.padding(top = 8.dp))
            }
        }
        Seccion("Tu gente", "Quién es cada @persona para ti, sus fechas y a quién conoce.", plegable = true, plegadaPorDefecto = true) {
            ContenidoGente(nav)
        }
    }
}

/** El grafo en pantalla completa, sin la barra de pestañas: se navega como el mapa para rascar. */
@Composable
fun PantallaGrafoSocial(nav: Nav) {
    val cs = MaterialTheme.colorScheme
    val m = rememberModelo()
    var zoom by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var elegido by remember { mutableStateOf<String?>(null) }
    val medidor = rememberTextMeasurer()
    val estilo = MaterialTheme.typography.labelSmall.copy(color = cs.onSurface)
    val eg = estiloGrafica()
    val tema = Estilo.tema
    val animar = Repo.config.animaciones && GrafoSocial.vista == "cielo"
    val fase = if (animar) animacionCielo() else 0f
    val dens = androidx.compose.ui.platform.LocalDensity.current.density
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            BotonTexto(onClick = nav.atras) { Text(Estilo.atras, color = cs.primary) }
            Column(Modifier.weight(1f)) {
                Text(Estilo.pantalla("Social", io.github.ruddy2003.vestigium.ui.Romano.latin("Social")).first, style = MaterialTheme.typography.titleLarge)
                Ayuda((m?.let { porNumero(it.nodos.size, "1 persona", "${it.nodos.size} personas") } ?: "…") + " · pellizca, arrastra o usa − y +")
            }
            // Alternativa visible a pellizcar y al doble toque (N-24).
            BotonTexto(onClick = { zoom = (zoom / 1.6f).coerceAtLeast(1f); if (zoom == 1f) pan = Offset.Zero }) { Text("−", style = MaterialTheme.typography.titleLarge) }
            BotonTexto(onClick = { zoom = (zoom * 1.6f).coerceAtMost(16f) }) { Text("+", style = MaterialTheme.typography.titleLarge) }
            BotonTexto(onClick = { zoom = 1f; pan = Offset.Zero }) { Text("todo", style = MaterialTheme.typography.labelLarge) }
        }
        Box(Modifier.padding(horizontal = 12.dp)) { SelectorVista { elegido = null; zoom = 1f; pan = Offset.Zero } }
        Box(Modifier.weight(1f).fillMaxWidth()
            .pointerInput(Unit) {
                detectTransformGestures { centroide, desplazamiento, factor, _ ->
                    val (z, p) = GrafoSocial.acercar(zoom, pan, factor, centroide, desplazamiento, size.width.toFloat(), size.height.toFloat())
                    zoom = z; pan = p
                }
            }
            .pointerInput(m) {
                detectTapGestures(
                    onDoubleTap = { zoom = 1f; pan = Offset.Zero },
                    onTap = { o -> m?.let { elegido = tocada(it, o, size.width.toFloat(), size.height.toFloat(), zoom, pan, dens) } },
                )
            }) {
            when {
                m == null -> Ayuda("Acomodando a tu gente…", Modifier.align(Alignment.Center))
                m.nodos.isEmpty() -> Vacio("Todavía no hay personas en tu diario.")
                else -> Canvas(Modifier.fillMaxSize()) { dibujarGrafo(m, zoom, pan, elegido, cs, eg, tema, medidor, estilo, fase, animar) }
            }
        }
        Surface(Modifier.fillMaxWidth(), color = cs.surface, tonalElevation = 2.dp) {
            Column(Modifier.padding(16.dp)) {
                val e = elegido
                if (e == null || m == null) {
                    Ayuda("Toca a una persona. Dos toques: todo el grafo.")
                    // Las más presentes, para ir directo a una.
                    m?.nodos?.sortedByDescending { it.dias }?.take(12)?.takeIf { it.isNotEmpty() }?.let { l ->
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            l.forEach { n -> Ficha("@${n.nombre}", false) { elegido = n.nombre } }
                        }
                    }
                } else FichaPersona(m, e, nav)
                BotonSecundario("Tu gente", Modifier.padding(top = 6.dp)) { nav.ir("personas") }
            }
        }
    }
}

/** Social en Vida: el grafo de tu gente a la vista, con cuántas personas hay y cuántas faltan por decir quiénes son. */
@Composable
fun RecuadroSocial(nav: Nav) {
    val cs = MaterialTheme.colorScheme
    val m = rememberModelo("red")
    val medidor = rememberTextMeasurer()
    val estilo = MaterialTheme.typography.labelSmall.copy(color = cs.onSurface)
    val eg = estiloGrafica()
    val tema = Estilo.tema
    val pendientes = remember(Repo.dias, Repo.config.personas) { Personas.pendientes().size }
    val n = m?.nodos?.size ?: 0
    Recuadro("Social", if (n == 0) "Tu gente, quién conoce a quién y tu cielo de personas" else
        porNumero(n, "1 persona", "$n personas") + (if (pendientes > 0) " · $pendientes por clasificar" else "") + " · red y cielo", alto = 230.dp, onClick = { nav.ir("social") }) {
        // El mueble del tema donde se reúne tu gente (lote E6), y el grafo encima, en el centro.
        val d = io.github.ruddy2003.vestigium.ui.vida.disenoVida(tema)
        val oscuro = io.github.ruddy2003.vestigium.ui.esOscuro()
        Canvas(Modifier.fillMaxSize()) { with(d) { muebleSocial(io.github.ruddy2003.vestigium.ui.vida.EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 5), oscuro) } }
        io.github.ruddy2003.vestigium.ui.vida.EnNicho(d.area(io.github.ruddy2003.vestigium.ui.vida.MuebleVida.SOCIAL)) {
            if (m != null && m.nodos.isNotEmpty()) Canvas(Modifier.fillMaxSize().padding(6.dp)) { dibujarGrafo(m, 1f, Offset.Zero, null, cs, eg, tema, medidor, estilo, 0f, false) }
            else Text("menciona a alguien con @", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.align(Alignment.Center))
        }
    }
}
