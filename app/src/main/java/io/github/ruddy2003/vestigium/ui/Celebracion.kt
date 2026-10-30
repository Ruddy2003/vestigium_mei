package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Logros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Los logros recién conseguidos que esperan su celebración a pantalla completa. */
object Celebracion {
    var cola by mutableStateOf<List<Logros.Logro>>(emptyList()); private set
    fun mostrar(l: List<Logros.Logro>) { if (l.isNotEmpty()) cola = cola + l.filter { n -> cola.none { it.id == n.id } } }
    fun siguiente() { cola = cola.drop(1) }
    fun cerrar() { cola = emptyList() }
}

/**
 * Vigila los datos: cuando algo que guardaste te da un logro nuevo, lo celebra en ese momento (no hay que abrir Logros).
 * La primera vez solo los cuenta, para no festejar todo tu historial de golpe; los que llegan de golpe de una importación
 * vieja (más de una semana) tampoco se festejan, solo quedan vistos.
 */
@Composable
fun VigiaLogros() {
    val ctx = LocalContext.current
    LaunchedEffect(Repo.dias, Repo.momentos, Repo.salud, Repo.entrenos, Repo.obras, Repo.cartas, Repo.reacciones, Repo.respuestas, Repo.gastos,
        Repo.ascensos, Repo.config.logros_propios, Repo.config.campanas, Repo.config.politica, Repo.tramos) {
        delay(1800)
        val l = withContext(Dispatchers.Default) { runCatching { Logros.todos(ctx) }.getOrNull() } ?: return@LaunchedEffect
        val vistos = Repo.config.logros_vistos
        val nuevos = l.filter { it.hecho && it.id !in vistos }
        if (nuevos.isEmpty()) return@LaunchedEffect
        Logros.marcarVistos(nuevos)
        if (vistos.isEmpty()) return@LaunchedEffect
        val hoy = Fechas.diaLogico()
        val recientes = nuevos.filter { it.conseguido?.let { f -> f >= Fechas.sumar(hoy, -7) } == true }
            .sortedWith(compareByDescending<Logros.Logro> { it.nivel }.thenByDescending { it.conseguido })
        Celebracion.mostrar(recientes.take(5))
    }
}

/** La celebración: el logro dibujado en grande, el motivo del tema girando detrás, su nombre, su nivel en la voz del tema y la lluvia de figuras del tema. */
@Composable
fun CapaLogro() {
    val l = Celebracion.cola.firstOrNull()
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    val tema = Estilo.tema
    val cs = MaterialTheme.colorScheme
    val animar = Repo.config.animaciones
    LaunchedEffect(l?.id) {
        if (l == null) return@LaunchedEffect
        sentir(Sentidos.Evento.LOGRO)
        if (animar) Fiesta.lanzar()
    }
    AnimatedVisibility(l != null, enter = fadeIn(tween(250)), exit = fadeOut(tween(250))) {
        val g = l ?: return@AnimatedVisibility
        val escala = remember(g.id) { Animatable(if (animar) 0.3f else 1f) }
        LaunchedEffect(g.id) { if (animar) escala.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow)) }
        val giro = rememberInfiniteTransition(label = "rayos")
        val angulo by giro.animateFloat(0f, 360f, infiniteRepeatable(tween(24000, easing = LinearEasing), RepeatMode.Restart), label = "angulo")
        Box(Modifier.fillMaxSize().background(cs.background.copy(alpha = 0.96f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { Celebracion.siguiente() },
            contentAlignment = Alignment.Center) {
            // Detrás del logro, el motivo del tema (laureles, haces de fósforo, rosetón…) girando despacio.
            Canvas(Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2, size.height * 0.42f)
                drawCircle(Brush.radialGradient(listOf(cs.primary.copy(alpha = 0.28f), cs.background.copy(alpha = 0f)), c, size.minDimension * 0.6f), size.minDimension * 0.6f, c)
                motivoLogro(tema, c, if (animar) angulo else 0f, cs)
            }
            Column(Modifier.statusBarsPadding().navigationBarsPadding().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(Estilo.seccion("Logro conseguido", null).first, style = MaterialTheme.typography.titleLarge, color = cs.primary, textAlign = TextAlign.Center)
                Box(Modifier.size(230.dp).graphicsLayer { scaleX = escala.value; scaleY = escala.value }, contentAlignment = Alignment.Center) {
                    io.github.ruddy2003.vestigium.ui.pantallas.TrofeoLogro(g, 220.dp)
                }
                Text(g.nombre, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Text(io.github.ruddy2003.vestigium.ui.pantallas.pasado(g.descripcion) + ".", style = MaterialTheme.typography.bodyLarge, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
                Text(nivelesLogro(tema)[(g.nivel - 1).coerceIn(0, 3)] + " · " + g.categoria.lowercase() + (g.conseguido?.let { " · " + Fechas.titulo(it).lowercase() } ?: ""),
                    style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    val enHall = g.id in Repo.config.hall_logros
                    TextButton(onClick = {
                        if (!enHall && Repo.config.hall_logros.size < 12) Repo.cambiarConfig { c -> c.copy(hall_logros = c.hall_logros + g.id) }
                    }, enabled = !enHall) { Text(if (enHall) "ya está en tu hall" else "ponerlo en el hall", style = MaterialTheme.typography.labelLarge) }
                    BotonPrincipal(if (Celebracion.cola.size > 1) "siguiente (${Celebracion.cola.size - 1})" else "seguir") { Celebracion.siguiente() }
                }
            }
        }
    }
}

/** Los cuatro niveles de un logro (bronce, plata, oro y legendario) dichos en la voz de cada tema. */
private fun nivelesLogro(t: TemaId): List<String> = when (t) {
    TemaId.ROMANO -> listOf("ex aere", "ex argento", "ex auro", "triumphalis")
    TemaId.URBE -> listOf("di bronzo", "d'argento", "d'oro", "da Campidoglio")
    TemaId.CIBER -> listOf("rango: usuario", "rango: operador", "rango: admin", "rango: root")
    TemaId.NEON -> listOf("rango: usuario", "rango: operador", "rango: admin", "rango: root")
    TemaId.HELENO -> listOf("χάλκεος", "ἀργύρεος", "χρύσεος", "ἀθάνατος")
    TemaId.MEDIEVAL -> listOf("en tinta", "en minio", "en pan de oro", "códice purpúreo")
    TemaId.DISTOPIA -> listOf("clase C", "clase B", "clase A", "alto secreto")
    TemaId.REFUGIO -> listOf("residente", "jefe de turno", "jefe de sector", "director del refugio")
    TemaId.ORDEN -> listOf("escudero", "caballero", "paladín", "gran maestre")
    TemaId.LABORATORIO -> listOf("hipótesis", "resultado", "replicado", "ley natural")
    TemaId.COLONOS -> listOf("nota breve", "crónica", "primera plana", "edición extra")
    TemaId.NAPOLEON -> listOf("citation", "croix d'argent", "Légion d'honneur", "maréchal d'Empire")
    TemaId.BOTANICO -> listOf("ejemplar común", "ejemplar raro", "especie nueva", "género nuevo")
    TemaId.NAVIDAD -> listOf("cascabel", "campana de plata", "estrella de oro", "milagro de Navidad")
    TemaId.SEDA -> listOf("moneda de cobre", "lingote de plata", "oro de Samarcanda", "seda del emperador")
    TemaId.GOTICO -> listOf("cirio", "cáliz de plata", "custodia de oro", "reliquia")
    TemaId.ESPACIO -> listOf("magnitud 3", "magnitud 2", "magnitud 1", "supernova")
    TemaId.ANDALUS -> listOf("feluz de cobre", "dírham de plata", "dinar de oro", "dinar de la Alhambra")
    TemaId.MONGOL -> listOf("paiza de madera", "paiza de plata", "paiza de oro", "paiza del Gran Kan")
    TemaId.RENACIMIENTO -> listOf("bronzo", "argento", "oro", "capolavoro")
    TemaId.URSS -> listOf("trabajador de choque", "plan cumplido", "plan en cuatro años", "héroe del trabajo")
    TemaId.REVOLUCION -> listOf("ciudadano activo", "guardia nacional", "héroe de la patria", "al Panteón")
    TemaId.ANTIGUAS -> listOf("hierro de la mina", "plata lunar", "oro de las profundidades", "gema de las edades")
}

/** Un polígono estrellado: [puntas] vértices a [fuera] y otros tantos a [dentro]; con los dos iguales, un polígono regular. */
private fun estrellaLogro(c: Offset, puntas: Int, fuera: Float, dentro: Float, giro: Double): Path = Path().apply {
    for (i in 0 until puntas * 2) {
        val r = if (i % 2 == 0) fuera else dentro
        val a = giro - PI / 2 + PI * i / puntas
        val x = c.x + (r * cos(a)).toFloat(); val y = c.y + (r * sin(a)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Una rueda dentada de dientes trapeciales. */
private fun engraneLogro(c: Offset, dientes: Int, fuera: Float, dentro: Float): Path = Path().apply {
    val paso = 2 * PI / dientes
    for (i in 0 until dientes) {
        val a = i * paso
        listOf(a to dentro, a + paso * 0.15 to fuera, a + paso * 0.5 to fuera, a + paso * 0.65 to dentro).forEachIndexed { j, (b, r) ->
            val x = c.x + (r * cos(b)).toFloat(); val y = c.y + (r * sin(b)).toFloat()
            if (i == 0 && j == 0) moveTo(x, y) else lineTo(x, y)
        }
    }
    close()
}

/** Una hoja (o pétalo) centrada en [p], a lo largo del ángulo [ang] (radianes), de largo 2·[l] y ancho relativo [ancho]. */
private fun DrawScope.hojaLogro(p: Offset, ang: Float, l: Float, ancho: Float, color: Color) {
    val d = Offset(cos(ang), sin(ang)); val n = Offset(-d.y, d.x)
    val a = p - d * l; val z = p + d * l; val k = l * ancho * 2f
    drawPath(Path().apply {
        moveTo(a.x, a.y); quadraticTo(p.x + n.x * k, p.y + n.y * k, z.x, z.y); quadraticTo(p.x - n.x * k, p.y - n.y * k, a.x, a.y); close()
    }, color)
}

/**
 * El motivo que gira detrás del logro, propio de cada tema. [giro]: grados (0 a 360 en 24 s); cada motivo gira una
 * fracción que es múltiplo de su simetría, para que la vuelta vuelva a empezar sin salto.
 */
private fun DrawScope.motivoLogro(t: TemaId, c: Offset, giro: Float, cs: ColorScheme) {
    val m = size.minDimension; val lejos = size.maxDimension; val u = 1.dp.toPx()
    val oscuro = cs.background.luminance() < 0.5f
    fun pt(r: Float, a: Double) = Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat())
    when (t) {
        TemaId.ROMANO -> {
            // Corona de laurel: dos ramas que suben por los lados y casi se cierran arriba, atadas abajo con una cinta.
            val rl = m * 0.36f
            val rama = cs.tertiary.copy(alpha = 0.14f)
            drawArc(rama, 100f, 150f, false, Offset(c.x - rl, c.y - rl), Size(2 * rl, 2 * rl), style = Stroke(1.5f * u))
            drawArc(rama, 290f, 150f, false, Offset(c.x - rl, c.y - rl), Size(2 * rl, 2 * rl), style = Stroke(1.5f * u))
            for (lado in listOf(-1f, 1f)) for (i in 0 until 12) {
                val a = Math.toRadians(if (lado < 0) 100.0 + i * 12.5 else 80.0 - i * 12.5)
                val p = pt(rl, a)
                val tangente = if (lado < 0) atan2(cos(a), -sin(a)) else atan2(-cos(a), sin(a))
                val l = (13f - i * 0.4f) * u
                for (s in listOf(-0.55, 0.55)) {
                    val ang = tangente + s
                    hojaLogro(Offset(p.x + (l * cos(ang)).toFloat(), p.y + (l * sin(ang)).toFloat()), ang.toFloat(), l, 0.38f, cs.tertiary.copy(alpha = 0.16f))
                }
            }
            val nudo = Offset(c.x, c.y + rl)
            drawLine(cs.primary.copy(alpha = 0.16f), nudo, Offset(nudo.x - 18f * u, nudo.y + 28f * u), 3f * u, cap = StrokeCap.Round)
            drawLine(cs.primary.copy(alpha = 0.16f), nudo, Offset(nudo.x + 18f * u, nudo.y + 28f * u), 3f * u, cap = StrokeCap.Round)
        }
        TemaId.URBE -> {
            // La cúpula del Panteón vista desde abajo: cinco anillos de 28 casetones que se achican hacia el óculo, y el
            // disco de luz que entra por él. Los anillos giran en sentidos alternos, de casetón en casetón.
            val luz = if (oscuro) cs.tertiary else cs.primary
            for (anillo in 0 until 5) {
                val ra = m * (0.46f - anillo * 0.065f); val lado = m * (0.05f - anillo * 0.006f)
                val sentido = if (anillo % 2 == 0) 1f else -1f
                rotate(sentido * giro * (anillo + 1) / 28f, c) {
                    for (k in 0 until 28) rotate(k * 360f / 28f, c) {
                        drawRect(luz.copy(alpha = 0.12f), Offset(c.x - lado / 2, c.y - ra - lado / 2), Size(lado, lado))
                        drawRect(luz.copy(alpha = 0.2f), Offset(c.x - lado * 0.3f, c.y - ra - lado * 0.3f), Size(lado * 0.6f, lado * 0.6f), style = Stroke(0.8f * u))
                    }
                }
            }
            val oculo = m * 0.11f
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.16f else 0.3f), Color.Transparent), center = c, radius = oculo * 2.2f), oculo * 2.2f, c)
            drawCircle(luz.copy(alpha = 0.3f), oculo, c, style = Stroke(1.6f * u))
        }
        TemaId.CIBER -> {
            // Haces de fósforo: renglones de luz que se acortan hacia arriba y abajo, un barrido que baja y escuadras de selección.
            val fosforo = cs.primary
            for (k in -22..22) {
                val f = 1f - abs(k) / 23f
                val y = c.y + k * 8f * u
                val mitad = size.width * 0.5f * f
                drawLine(fosforo.copy(alpha = 0.08f * f), Offset(c.x - mitad, y), Offset(c.x + mitad, y), 1.4f * u)
            }
            val barrido = (giro / 60f) % 1f * size.height
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, fosforo.copy(alpha = 0.10f)), startY = barrido - 40f * u, endY = barrido), Offset(0f, barrido - 40f * u), Size(size.width, 40f * u))
            val q = m * 0.34f; val l = 22f * u
            listOf(Offset(-1f, -1f), Offset(1f, -1f), Offset(-1f, 1f), Offset(1f, 1f)).forEach { d ->
                val o = Offset(c.x + d.x * q, c.y + d.y * q)
                drawLine(fosforo.copy(alpha = 0.35f), o, Offset(o.x - d.x * l, o.y), 2f * u)
                drawLine(fosforo.copy(alpha = 0.35f), o, Offset(o.x, o.y - d.y * l), 2f * u)
            }
        }
        TemaId.NEON -> {
            // Haces de fósforo: renglones de luz que se acortan hacia arriba y abajo, un barrido que baja y escuadras de selección.
            val fosforo = cs.primary
            for (k in -22..22) {
                val f = 1f - abs(k) / 23f
                val y = c.y + k * 8f * u
                val mitad = size.width * 0.5f * f
                drawLine(fosforo.copy(alpha = 0.08f * f), Offset(c.x - mitad, y), Offset(c.x + mitad, y), 1.4f * u)
            }
            val barrido = (giro / 60f) % 1f * size.height
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, fosforo.copy(alpha = 0.10f)), startY = barrido - 40f * u, endY = barrido), Offset(0f, barrido - 40f * u), Size(size.width, 40f * u))
            val q = m * 0.34f; val l = 22f * u
            listOf(Offset(-1f, -1f), Offset(1f, -1f), Offset(-1f, 1f), Offset(1f, 1f)).forEach { d ->
                val o = Offset(c.x + d.x * q, c.y + d.y * q)
                drawLine(fosforo.copy(alpha = 0.35f), o, Offset(o.x - d.x * l, o.y), 2f * u)
                drawLine(fosforo.copy(alpha = 0.35f), o, Offset(o.x, o.y - d.y * l), 2f * u)
            }
        }
        TemaId.HELENO -> rotate(giro * 0.5f, c) {
            // El sol de Vergina: dieciséis rayos que alternan largo y corto alrededor de un disco.
            val oro = cs.tertiary
            drawCircle(oro.copy(alpha = 0.10f), m * 0.07f, c)
            for (i in 0 until 16) {
                val a = 2 * PI * i / 16
                val largo = if (i % 2 == 0) m * 0.48f else m * 0.36f
                val ancho = PI / 16 * 0.55
                val base = m * 0.09f
                val p1 = pt(base, a - ancho); val p2 = pt(largo, a); val p3 = pt(base, a + ancho)
                drawPath(Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close() }, oro.copy(alpha = 0.09f))
            }
        }
        TemaId.MEDIEVAL -> rotate(giro * 0.25f, c) {
            // Nimbo de pan de oro punzonado: dos anillos con una hilera de puntos y rosetas de cuatro granos alrededor.
            val oro = cs.tertiary
            drawCircle(oro.copy(alpha = 0.06f), m * 0.38f, c)
            drawCircle(oro.copy(alpha = 0.18f), m * 0.30f, c, style = Stroke(1.5f * u))
            drawCircle(oro.copy(alpha = 0.18f), m * 0.38f, c, style = Stroke(1.5f * u))
            for (i in 0 until 48) drawCircle(oro.copy(alpha = 0.22f), 1.8f * u, pt(m * 0.34f, 2 * PI * i / 48))
            for (i in 0 until 12) {
                val p = pt(m * 0.44f, 2 * PI * i / 12)
                drawCircle(cs.primary.copy(alpha = 0.14f), 2.6f * u, p)
                for (j in 0 until 4) {
                    val b = PI / 2 * j
                    drawCircle(oro.copy(alpha = 0.16f), 2f * u, Offset(p.x + (5f * u * cos(b)).toFloat(), p.y + (5f * u * sin(b)).toFloat()))
                }
            }
        }
        TemaId.DISTOPIA -> {
            // Dos reflectores que barren desde abajo y un ojo que lo vigila todo.
            val haz = cs.onBackground
            listOf(Offset(0f, size.height) to 1.0, Offset(size.width, size.height) to -1.0).forEach { (o, lado) ->
                val a = Math.toRadians((if (lado > 0) -60.0 else -120.0) + lado * 22.0 * sin(Math.toRadians(giro * 4.0)))
                val l = lejos * 1.3f
                val p1 = Offset(o.x + (l * cos(a - 0.12)).toFloat(), o.y + (l * sin(a - 0.12)).toFloat())
                val p2 = Offset(o.x + (l * cos(a + 0.12)).toFloat(), o.y + (l * sin(a + 0.12)).toFloat())
                drawPath(Path().apply { moveTo(o.x, o.y); lineTo(p1.x, p1.y); lineTo(p2.x, p2.y); close() }, Brush.radialGradient(listOf(haz.copy(alpha = 0.10f), Color.Transparent), o, l))
            }
            val a = m * 0.62f; val k = m * 0.8f
            drawPath(Path().apply { moveTo(c.x - a, c.y); quadraticTo(c.x, c.y - k, c.x + a, c.y); quadraticTo(c.x, c.y + k, c.x - a, c.y); close() }, cs.primary.copy(alpha = 0.16f), style = Stroke(2f * u))
            drawCircle(cs.primary.copy(alpha = 0.12f), m * 0.36f, c, style = Stroke(1.5f * u))
        }
        TemaId.REFUGIO -> {
            // Un átomo: tres órbitas con su electrón y el núcleo, el emblema del optimismo atómico.
            val rx = m * 0.44f; val ry = m * 0.15f
            for (k in 0 until 3) rotate(k * 60f + giro * 0.5f, c) {
                drawOval(cs.primary.copy(alpha = 0.16f), Offset(c.x - rx, c.y - ry), Size(2 * rx, 2 * ry), style = Stroke(1.8f * u))
                val f = Math.toRadians(giro * 3.0 + k * 120.0)
                drawCircle(cs.secondary.copy(alpha = 0.35f), 4f * u, Offset(c.x + (rx * cos(f)).toFloat(), c.y + (ry * sin(f)).toFloat()))
            }
        }
        TemaId.ORDEN -> {
            // Dos engranes que engranan: el grande gira despacio y el chico al revés, más rápido.
            rotate(giro * 0.5f, c) {
                drawPath(engraneLogro(c, 16, m * 0.46f, m * 0.40f), cs.primary.copy(alpha = 0.14f), style = Stroke(2f * u))
                drawCircle(cs.primary.copy(alpha = 0.12f), m * 0.30f, c, style = Stroke(1.5f * u))
                for (i in 0 until 8) drawCircle(cs.secondary.copy(alpha = 0.25f), 3f * u, pt(m * 0.35f, 2 * PI * i / 8))
            }
            val c2 = Offset(c.x + m * 0.40f, c.y - m * 0.46f)
            rotate(-giro * 0.8f, c2) { drawPath(engraneLogro(c2, 10, m * 0.16f, m * 0.12f), cs.secondary.copy(alpha = 0.16f), style = Stroke(1.6f * u)) }
        }
        TemaId.LABORATORIO -> {
            // Ondas que se abren como en un líquido y un anillo de hexágonos, como una red de moléculas.
            for (k in 0 until 3) {
                val f = ((giro / 90f) + k / 3f) % 1f
                drawCircle(cs.primary.copy(alpha = 0.12f * (1f - f)), m * (0.2f + 0.4f * f), c, style = Stroke(1.5f * u))
            }
            rotate(giro * 0.5f, c) {
                for (i in 0 until 18) {
                    val a = 2 * PI * i / 18
                    drawPath(estrellaLogro(pt(m * 0.40f, a), 3, m * 0.05f, m * 0.05f, a), cs.secondary.copy(alpha = 0.16f), style = Stroke(1.3f * u))
                }
            }
        }
        TemaId.COLONOS -> rotate(giro * 0.25f, c) {
            // Rayos de semitono, como el grabado de una gaceta: hileras de puntos que adelgazan hacia afuera.
            val tinta = cs.onBackground
            for (i in 0 until 12) {
                val a = 2 * PI * i / 12
                var r = m * 0.2f; var k = 0
                while (r < lejos * 0.7f) {
                    val radio = (3.4f - k * 0.12f).coerceAtLeast(0.8f) * u
                    for (s in -1..1) drawCircle(tinta.copy(alpha = 0.12f), if (s == 0) radio else radio * 0.6f, pt(r, a + s * 0.05))
                    r += 11f * u; k++
                }
            }
        }
        TemaId.NAPOLEON -> rotate(giro * 0.2f, c) {
            // La estrella de cinco brazos bífidos de una condecoración imperial, con sus perlas en las puntas.
            val oro = cs.tertiary
            val largo = m * 0.44f; val w2 = m * 0.11f; val w1 = m * 0.025f; val ri = m * 0.07f
            for (i in 0 until 5) rotate(72f * i - 90f, c) {
                val brazo = Path().apply { moveTo(c.x + ri, c.y - w1); lineTo(c.x + largo, c.y - w2); lineTo(c.x + largo * 0.86f, c.y); lineTo(c.x + largo, c.y + w2); lineTo(c.x + ri, c.y + w1); close() }
                drawPath(brazo, cs.primary.copy(alpha = 0.07f))
                drawPath(brazo, oro.copy(alpha = 0.18f), style = Stroke(1.4f * u))
                drawCircle(oro.copy(alpha = 0.22f), 3.5f * u, Offset(c.x + largo, c.y - w2))
                drawCircle(oro.copy(alpha = 0.22f), 3.5f * u, Offset(c.x + largo, c.y + w2))
            }
            drawCircle(oro.copy(alpha = 0.14f), ri * 1.3f, c, style = Stroke(1.5f * u))
        }
        TemaId.BOTANICO -> rotate(giro * 0.125f, c) {
            // Una corola de dos vueltas de pétalos, con sus nervios, como una lámina de herbario.
            for (i in 0 until 16) {
                val a = 2 * PI * i / 16
                hojaLogro(pt(m * 0.28f, a + PI / 16), (a + PI / 16).toFloat(), m * 0.13f, 0.32f, cs.secondary.copy(alpha = 0.07f))
                hojaLogro(pt(m * 0.30f, a), a.toFloat(), m * 0.17f, 0.28f, cs.primary.copy(alpha = 0.08f))
                drawLine(cs.primary.copy(alpha = 0.10f), pt(m * 0.15f, a), pt(m * 0.45f, a), 0.8f * u)
            }
        }
        TemaId.NAVIDAD -> rotate(giro / 6f, c) {
            // Un copo de nieve grande, de seis brazos con sus ramitas.
            val nieve = (if (oscuro) Color.White else cs.secondary).copy(alpha = 0.16f)
            for (i in 0 until 6) {
                val a = 2 * PI * i / 6
                drawLine(nieve, c, pt(m * 0.45f, a), 3f * u, cap = StrokeCap.Round)
                listOf(0.15f to 0.09f, 0.25f to 0.08f, 0.35f to 0.06f).forEach { (d, l) ->
                    val base = pt(m * d, a)
                    listOf(-PI / 3, PI / 3).forEach { s ->
                        drawLine(nieve, base, Offset(base.x + (m * l * cos(a + s)).toFloat(), base.y + (m * l * sin(a + s)).toFloat()), 2.2f * u, cap = StrokeCap.Round)
                    }
                }
            }
            drawPath(estrellaLogro(c, 6, m * 0.08f, m * 0.05f, 0.0), nieve)
        }
        TemaId.SEDA -> {
            // Monedas de agujero cuadrado que orbitan y, por dentro, una hilera de rombos que gira al revés.
            rotate(giro * 0.4f, c) {
                for (i in 0 until 10) {
                    val p = pt(m * 0.42f, 2 * PI * i / 10); val r = m * 0.05f
                    drawCircle(cs.tertiary.copy(alpha = 0.14f), r, p)
                    drawCircle(cs.tertiary.copy(alpha = 0.24f), r, p, style = Stroke(1.2f * u))
                    drawRect(cs.background, Offset(p.x - r * 0.3f, p.y - r * 0.3f), Size(r * 0.6f, r * 0.6f))
                }
            }
            rotate(-giro * 0.25f, c) {
                for (i in 0 until 16) {
                    val a = 2 * PI * i / 16
                    drawPath(estrellaLogro(pt(m * 0.30f, a), 2, m * 0.018f, m * 0.009f, a), cs.primary.copy(alpha = 0.16f))
                }
            }
        }
        TemaId.GOTICO -> rotate(giro * 0.25f, c) {
            // Un rosetón: doce pétalos de vidrio rubí y zafiro entre plomos, con su aro de lóbulos.
            val rr = m * 0.42f
            val rubi = Color(0xFF9B1B30); val zafiro = Color(0xFF1F4E9B)
            for (i in 0 until 12) {
                val a = 2 * PI * i / 12
                hojaLogro(pt(rr * 0.55f, a), a.toFloat(), rr * 0.40f, 0.42f, (if (i % 2 == 0) rubi else zafiro).copy(alpha = 0.12f))
                drawLine(cs.primary.copy(alpha = 0.16f), pt(rr * 0.16f, a + PI / 12), pt(rr, a + PI / 12), 1.2f * u)
            }
            drawCircle(cs.primary.copy(alpha = 0.2f), rr, c, style = Stroke(2f * u))
            drawCircle(cs.primary.copy(alpha = 0.2f), rr * 0.16f, c, style = Stroke(1.5f * u))
            for (i in 0 until 24) drawCircle(Color(0xFFE2B34A).copy(alpha = 0.14f), rr * 0.06f, pt(rr * 1.07f, 2 * PI * i / 24), style = Stroke(1f * u))
        }
        TemaId.ESPACIO -> rotate(giro, c) {
            // Una galaxia espiral de tres brazos, con su núcleo brillante.
            val luz = if (oscuro) Color.White else cs.primary
            drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.14f), Color.Transparent), c, m * 0.16f), m * 0.16f, c)
            for (brazo in 0 until 3) for (k in 0 until 40) {
                val a = Math.toRadians(brazo * 120.0 + k * 9.0)
                drawCircle(luz.copy(alpha = 0.22f * (1f - k / 44f)), (2.6f - k * 0.04f) * u, pt(m * (0.05f + k * 0.011f), a))
            }
        }
        TemaId.ANDALUS -> rotate(giro * 0.25f, c) {
            // Estrella de ocho puntas de lazo (dos cuadrados cruzados, de cinta doble), dentro de una de dieciséis.
            listOf(0.36f, 0.22f).forEach { f ->
                val s = m * f
                for (giroCuadro in listOf(0f, 45f)) rotate(giroCuadro, c) {
                    drawRect(cs.tertiary.copy(alpha = 0.08f), Offset(c.x - s, c.y - s), Size(2 * s, 2 * s), style = Stroke(6f * u))
                    drawRect(cs.primary.copy(alpha = 0.18f), Offset(c.x - s, c.y - s), Size(2 * s, 2 * s), style = Stroke(1.2f * u))
                }
            }
            drawPath(estrellaLogro(c, 16, m * 0.56f, m * 0.52f, 0.0), cs.primary.copy(alpha = 0.12f), style = Stroke(1.4f * u))
        }
        TemaId.MONGOL -> rotate(giro * 0.25f, c) {
            // La corona de una yurta vista desde abajo: el aro, sus varas en cruz y las veinticuatro varas del techo.
            val col = cs.primary
            for (i in 0 until 24) {
                val a = 2 * PI * i / 24
                drawLine(col.copy(alpha = 0.12f), pt(m * 0.17f, a), pt(m * 0.46f, a), 1.6f * u)
            }
            drawCircle(col.copy(alpha = 0.2f), m * 0.16f, c, style = Stroke(3f * u))
            drawCircle(col.copy(alpha = 0.14f), m * 0.46f, c, style = Stroke(1.2f * u))
            for (s in listOf(-1f, 1f)) {
                drawLine(col.copy(alpha = 0.16f), Offset(c.x - m * 0.15f, c.y + s * m * 0.04f), Offset(c.x + m * 0.15f, c.y + s * m * 0.04f), 2f * u)
                drawLine(col.copy(alpha = 0.16f), Offset(c.x + s * m * 0.04f, c.y - m * 0.15f), Offset(c.x + s * m * 0.04f, c.y + m * 0.15f), 2f * u)
            }
        }
        TemaId.RENACIMIENTO -> {
            // Una rosa de los vientos de dieciséis puntas sobre líneas y círculos de construcción, a tinta y sanguina.
            val tinta = cs.onBackground; val sanguina = cs.secondary
            val punteado = PathEffect.dashPathEffect(floatArrayOf(4f * u, 6f * u))
            drawLine(tinta.copy(alpha = 0.10f), Offset(0f, c.y), Offset(size.width, c.y), 0.8f * u, pathEffect = punteado)
            drawLine(tinta.copy(alpha = 0.10f), Offset(c.x, 0f), Offset(c.x, size.height), 0.8f * u, pathEffect = punteado)
            drawCircle(tinta.copy(alpha = 0.12f), m * 0.22f, c, style = Stroke(0.8f * u))
            drawCircle(tinta.copy(alpha = 0.12f), m * 0.40f, c, style = Stroke(0.8f * u))
            rotate(giro * 0.25f, c) {
                listOf(Triple(0.0, 0.46f, 4), Triple(45.0, 0.32f, 4), Triple(22.5, 0.2f, 8)).forEach { (a0, largo, n) ->
                    for (i in 0 until n) {
                        val a = Math.toRadians(a0 + 360.0 / n * i)
                        val punta = pt(m * largo, a); val ancho = m * largo * 0.09f
                        val izq = pt(ancho, a - PI / 2); val der = pt(ancho, a + PI / 2)
                        drawPath(Path().apply { moveTo(c.x, c.y); lineTo(punta.x, punta.y); lineTo(izq.x, izq.y); close() }, sanguina.copy(alpha = 0.16f))
                        drawPath(Path().apply { moveTo(c.x, c.y); lineTo(punta.x, punta.y); lineTo(der.x, der.y); close() }, tinta.copy(alpha = 0.08f))
                    }
                }
            }
        }
        TemaId.URSS -> {
            // Cinco haces anchos que salen de una gran estrella roja, como en un cartel constructivista.
            rotate(giro * 0.2f, c) {
                for (i in 0 until 5) {
                    val a = Math.toRadians(i * 72.0 - 90.0); val d = Math.toRadians(9.0)
                    val p1 = pt(lejos, a - d); val p2 = pt(lejos, a + d)
                    drawPath(Path().apply { moveTo(c.x, c.y); lineTo(p1.x, p1.y); lineTo(p2.x, p2.y); close() }, cs.primary.copy(alpha = 0.08f))
                }
            }
            drawPath(estrellaLogro(c, 5, m * 0.32f, m * 0.13f, 0.0), cs.primary.copy(alpha = 0.10f))
            drawPath(estrellaLogro(c, 5, m * 0.32f, m * 0.13f, 0.0), cs.onBackground.copy(alpha = 0.10f), style = Stroke(1.5f * u))
        }
        TemaId.REVOLUCION -> {
            // Una escarapela: el borde rojo plisado que gira, el anillo blanco y el centro azul.
            rotate(giro * 0.125f, c) { drawPath(estrellaLogro(c, 48, m * 0.46f, m * 0.42f, 0.0), Color(0xFFC8352E).copy(alpha = 0.12f)) }
            drawCircle(if (oscuro) Color.White.copy(alpha = 0.10f) else cs.surface.copy(alpha = 0.9f), m * 0.34f, c)
            drawCircle(cs.onBackground.copy(alpha = 0.08f), m * 0.34f, c, style = Stroke(1f * u))
            drawCircle(Color(0xFF2B4BA8).copy(alpha = 0.14f), m * 0.22f, c)
        }
        TemaId.ANTIGUAS -> rotate(giro, c) {
            // Un anillo de dieciséis runas entre dos círculos, como el de una puerta de piedra que se abre.
            val luz = if (oscuro) Color(0xFF9FD8FF) else cs.tertiary
            drawCircle(luz.copy(alpha = 0.16f), m * 0.36f, c, style = Stroke(1.4f * u))
            drawCircle(luz.copy(alpha = 0.16f), m * 0.46f, c, style = Stroke(1.4f * u))
            for (i in 0 until 16) {
                val a = 2 * PI * i / 16
                val p = pt(m * 0.41f, a)
                val runa = RUNAS_VIVO[i % RUNAS_VIVO.size]
                rotate(Math.toDegrees(a).toFloat() + 90f, p) {
                    runaVivo(runa, p, m * 0.07f, luz.copy(alpha = 0.10f), 4f * u)
                    runaVivo(runa, p, m * 0.07f, luz.copy(alpha = 0.30f), 1.3f * u)
                }
            }
        }
    }
}
