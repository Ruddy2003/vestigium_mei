package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Logros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.grecaEscalonada
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TrofeosTema
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.signoEgipcio
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import io.github.ruddy2003.vestigium.ui.Dialogo
import io.github.ruddy2003.vestigium.ui.Progreso

/** Los metales de cada nivel: oscuro, base y brillo. Bronce, plata, oro y cristal (legendario). */
private val METAL = listOf(
    Triple(Color(0xFF6E4423), Color(0xFFB27A45), Color(0xFFE9BE8F)),
    Triple(Color(0xFF6F7781), Color(0xFFBFC6CF), Color(0xFFF4F7FA)),
    Triple(Color(0xFF8E6812), Color(0xFFE3B748), Color(0xFFFFEBA6)),
    Triple(Color(0xFF237E95), Color(0xFF7FD8E8), Color(0xFFE9FCFF)),
)
val NIVELES = listOf("Bronce", "Plata", "Oro", "Legendario")

private val SINGULAR = mapOf("días" to "día", "sueños" to "sueño", "cartas" to "carta", "momentos" to "momento", "avisos" to "aviso", "veces" to "vez",
    "libros" to "libro", "ciudades" to "ciudad", "países" to "país", "palabras" to "palabra", "semanas" to "semana", "sesiones" to "sesión",
    "kilómetros" to "kilómetro", "horas" to "hora", "bloques" to "bloque", "obras" to "obra", "películas" to "película", "series" to "serie",
    "juegos" to "juego", "personas" to "persona", "lugares" to "lugar", "fotos" to "foto", "pruebas" to "prueba", "noches" to "noche")
private val RX_UNO = Regex("(?<![\\d.,])1 (" + SINGULAR.keys.joinToString("|") + ")\\b")

/** «1 días» → «1 día» (las descripciones se arman con la meta como número). */
fun singular(desc: String): String = RX_UNO.replace(desc) { m -> "1 " + (SINGULAR[m.groupValues[1]] ?: m.groupValues[1]) }

/** «Llena el diario 100 días» → «Llenaste el diario 100 días». Lo que no empieza con un verbo queda igual. */
fun pasado(desc0: String): String {
    val desc = singular(desc0)
    val i = desc.indexOf(' ').takeIf { it > 0 } ?: return desc
    val v = desc.substring(0, i); val resto = desc.substring(i)
    val especiales = mapOf("Haz" to "Hiciste", "Duerme" to "Dormiste", "Lee" to "Leíste", "Ve" to "Viste", "Mide" to "Mediste", "Ten" to "Tuviste",
        "Mantén" to "Mantuviste", "Sal" to "Saliste", "Pon" to "Pusiste", "Di" to "Dijiste", "Consigue" to "Conseguiste", "Sigue" to "Seguiste",
        "Despierta" to "Despertaste", "Recuerda" to "Recordaste", "Cuenta" to "Contaste", "Juega" to "Jugaste", "Vuelve" to "Volviste", "Prueba" to "Probaste",
        "Encuentra" to "Encontraste", "Pisa" to "Pisaste", "Vuela" to "Volaste", "Sueña" to "Soñaste", "Muévete" to "Te moviste")
    val p = especiales[v] ?: when {
        !v[0].isUpperCase() -> return desc
        v.endsWith("a") && v.length > 3 -> v + "ste"
        v.endsWith("e") && v.length > 3 -> v.dropLast(1) + "iste"
        else -> return desc
    }
    return p + resto
}

/**
 * Un trofeo: el pedestal del tema y, encima, la obra o el objeto de su categoría ([TrofeosTema]), en el metal de su
 * nivel. Si falta, es una silueta.
 */
@Composable
fun TrofeoLogro(l: Logros.Logro, alto: Dp, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val secreto = l.secreto && !l.hecho
    val pedestal = if (oscuro) Color(0xFF2B2622) else Color(0xFF4A3A2C)
    Box(modifier.size(width = alto * 0.82f, height = alto), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            dibujarTrofeoTema(tema, l.categoria, l.nivel.coerceIn(1, 4), l.hecho, pedestal, cs.onSurface)
        }
        if (secreto) Text("?", fontSize = (alto.value * 0.26f).sp, color = cs.onSurfaceVariant.copy(alpha = 0.7f), fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = alto * 0.3f))
    }
}

/** El trofeo de un logro en un lienzo cualquiera (la imagen del hall). */
internal fun DrawScope.dibujarTrofeoDe(l: Logros.Logro, tema: TemaId, oscuro: Boolean, tinta: Color) {
    val pedestal = if (oscuro) Color(0xFF2B2622) else Color(0xFF4A3A2C)
    dibujarTrofeoTema(tema, l.categoria, l.nivel.coerceIn(1, 4), l.hecho, pedestal, tinta)
}

/** El trofeo del tema: la obra o el objeto de su categoría (ver [TrofeosTema]) sobre el pedestal del tema. */
internal fun DrawScope.dibujarTrofeoTema(tema: TemaId, categoria: String, nivel: Int, hecho: Boolean, colorPedestal: Color, tinta: Color) {
    val (oscuro, base, brillo) = METAL[nivel - 1]
    val p = TrofeosTema.Pincel(size.width, size.height, hecho, nivel, oscuro, base, brillo, tinta.copy(alpha = 0.13f))
    with(TrofeosTema) { pedestal(tema, p, colorPedestal); figura(tema, categoria, p) }
    destellos(tema, nivel - 1, hecho)
}

/** Los legendarios brillan, cada tema con su propia chispa (ver [chispa]). */
private fun DrawScope.destellos(tema: TemaId, nivel: Int, hecho: Boolean) {
    if (!hecho || nivel != 3) return
    val w = size.width; val h = size.height
    listOf(Offset(w * 0.14f, h * 0.12f), Offset(w * 0.88f, h * 0.22f), Offset(w * 0.2f, h * 0.5f), Offset(w * 0.84f, h * 0.6f)).forEachIndexed { i, o ->
        chispa(tema, o, w * (if (i % 2 == 0) 0.05f else 0.035f), i)
    }
}

/**
 * La placa debajo de la repisa: nombre, lo que lograste y cuándo (o cuánto falta). Su forma es la del tema (ver [fondoPlaca]):
 * tabula ansata, corchetes de consola, rollo, etiqueta de herbario, cartela oval…
 */
@Composable
private fun Placa(l: Logros.Logro, tema: TemaId, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val secreto = l.secreto && !l.hecho
    Column(modifier.padding(horizontal = 3.dp).drawBehind { fondoPlaca(tema, cs.onSurface, cs.primary, l.hecho) }.padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (secreto) "Secreto" else l.nombre, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
            maxLines = 2, overflow = TextOverflow.Ellipsis, color = if (l.hecho) cs.onSurface else cs.onSurfaceVariant)
        when {
            l.hecho -> {
                Text(pasado(l.descripcion), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 3, overflow = TextOverflow.Ellipsis, color = cs.onSurfaceVariant)
                Text(Fechas.corto(l.conseguido!!), style = MaterialTheme.typography.labelSmall, color = cs.primary)
            }
            secreto -> Text("aparece cuando lo logras", style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, color = cs.onSurfaceVariant)
            else -> {
                Text(singular(l.descripcion), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, color = cs.onSurfaceVariant)
                Progreso(progress = { l.fraccion }, modifier = Modifier.fillMaxWidth(0.8f).padding(top = 3.dp).height(3.dp))
            }
        }
    }
}

/**
 * La sala de trofeos: repisas con tres trofeos cada una y, debajo de cada uno, su placa con lo que lograste.
 * Los que faltan se ven como silueta, con la barra de cuánto llevas.
 */
@Composable
fun SalaTrofeos(lista: List<Logros.Logro>, titulo: String? = null, porFila: Int = 3, abrir: (Logros.Logro) -> Unit) {
    if (lista.isEmpty()) return
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val tema = Estilo.tema
    val pared = lerp(lerp(cs.surface, if (oscuro) Color.Black else cs.primary, if (oscuro) 0.25f else 0.05f), tonoPared(tema), if (oscuro) 0.14f else 0.12f)
    Surface(shape = MaterialTheme.shapes.medium, color = pared, border = BorderStroke(1.dp, cs.outline), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Column(Modifier.drawBehind { paredTema(tema, cs.onSurface, cs.primary) }.padding(horizontal = 6.dp, vertical = 10.dp)) {
            if (titulo != null) Text(titulo.uppercase(), style = MaterialTheme.typography.labelLarge, color = cs.primary, letterSpacing = 2.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
            lista.chunked(porFila).forEach { fila ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    fila.forEach { l -> Box(Modifier.weight(1f).clickable { abrir(l) }, contentAlignment = Alignment.BottomCenter) { TrofeoLogro(l, 96.dp) } }
                    repeat(porFila - fila.size) { Spacer(Modifier.weight(1f)) }
                }
                // La repisa del tema, con su canto y su sombra.
                Canvas(Modifier.fillMaxWidth().height(14.dp)) { repisaTema(tema, oscuro) }
                Box(Modifier.fillMaxWidth().height(7.dp).background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent))))
                Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.Top) {
                    fila.forEach { l -> Placa(l, tema, Modifier.weight(1f).clickable { abrir(l) }) }
                    repeat(porFila - fila.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

/** El trofeo de cerca: grande, con lo que lograste y cuándo, o el reto y cuánto llevas, y el siguiente de la serie. */
@Composable
fun DetalleTrofeo(l: Logros.Logro, todos: List<Logros.Logro>, cerrar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val secreto = l.secreto && !l.hecho
    val serie = l.id.substringBeforeLast('_')
    val siguiente = todos.filter { it.id != l.id && it.id.substringBeforeLast('_') == serie && it.categoria == l.categoria && !it.hecho }.minByOrNull { it.meta }
    Dialogo(onDismissRequest = cerrar, confirmButton = { TextButton(onClick = cerrar) { Text("cerrar") } },
        dismissButton = {
            if (l.hecho) {
                val enHall = l.id in Repo.config.hall_logros
                TextButton(onClick = {
                    Repo.cambiarConfig { c -> c.copy(hall_logros = if (enHall) c.hall_logros - l.id else (c.hall_logros + l.id).takeLast(12)) }
                }) { Text(if (enHall) "quitar del hall" else "ponerlo en el hall") }
            } else if (l.propio) {
                var confirmar by remember(l.id) { mutableStateOf(false) }
                TextButton(onClick = {
                    if (!confirmar) { confirmar = true; return@TextButton }
                    val previos = Repo.config.logros_propios.filter { "p_" + it.id == l.id }
                    Repo.cambiarConfig { c -> c.copy(logros_propios = c.logros_propios.filterNot { "p_" + it.id == l.id }) }; cerrar()
                    io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Logro borrado") { Repo.cambiarConfig { c -> c.copy(logros_propios = c.logros_propios + previos.filter { p -> c.logros_propios.none { it.id == p.id } }) } }
                }) { Text(if (confirmar) "¿borrar de verdad?" else "borrar este logro", color = MaterialTheme.colorScheme.error) }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                TrofeoLogro(l, 180.dp)
                Text(if (secreto) "Logro secreto" else l.nombre, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Text("${NIVELES[(l.nivel - 1).coerceIn(0, 3)]} · ${l.categoria}" + (TrofeosTema.nombre(Estilo.tema, l.categoria)?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.labelLarge, color = cs.primary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                when {
                    l.hecho -> {
                        Text(pasado(l.descripcion) + ".", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                        Text("Lo lograste el " + Fechas.titulo(l.conseguido!!).lowercase() + ".", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
                        Logros.comoSeConsiguio(l)?.let { Text(it, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp)) }
                    }
                    secreto -> Text("Sigue viviendo y escribiendo: aparece cuando lo logras.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    else -> {
                        Text("Reto: " + singular(l.descripcion).replaceFirstChar { it.lowercase() } + ".", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                        Progreso(progress = { l.fraccion }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(6.dp))
                        Text("llevas ${cifraLogro(l.progreso)} de ${cifraLogro(l.meta)}" + if (l.unidad.isNotEmpty()) " ${l.unidad}" else "", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
                    }
                }
                if (siguiente != null && l.hecho) Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    TrofeoLogro(siguiente, 54.dp)
                    Column(Modifier.padding(start = 10.dp)) {
                        Text("Siguiente: ${siguiente.nombre}", style = MaterialTheme.typography.labelLarge)
                        Text(siguiente.descripcion + " · llevas ${cifraLogro(siguiente.progreso)}", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    }
                }
            }
        })
}

fun cifraLogro(x: Double): String = when {
    x >= 1e6 -> "%.1f M".format(x / 1e6)
    x == Math.floor(x) -> "%,d".format(x.toLong())
    else -> "%.1f".format(x)
}

/** Para listas cortas (Vida, Tu año): el trofeo con su nombre debajo. */
@Composable
fun TrofeoConNombre(l: Logros.Logro, alto: Dp = 72.dp) {
    Column(Modifier.width(alto * 1.1f), horizontalAlignment = Alignment.CenterHorizontally) {
        TrofeoLogro(l, alto)
        Text(if (l.secreto && !l.hecho) "?" else l.nombre, style = MaterialTheme.typography.labelSmall, maxLines = 2, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis)
    }
}

// ================================================================== la sala de cada tema: destellos, pared, repisa y placa

/** Un polígono estrellado de [puntas] puntas entre los radios [R] y [r]. */
private fun estrellada(c: Offset, R: Float, r: Float, puntas: Int, giro: Double = -PI / 2): Path = Path().apply {
    for (k in 0 until puntas * 2) {
        val a = giro + k * PI / puntas
        val rr = if (k % 2 == 0) R else r
        val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
        if (k == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Una greca de ganchos, de [x0] hasta [ancho], con módulo [a] y su borde de arriba en [y0]. */
private fun Path.grecaRepisa(x0: Float, y0: Float, a: Float, ancho: Float) {
    val y1 = y0 + 3 * a
    var x = x0
    moveTo(x, y1)
    while (x < ancho) {
        lineTo(x, y0); lineTo(x + 3 * a, y0); lineTo(x + 3 * a, y0 + 2 * a); lineTo(x + 2 * a, y0 + 2 * a)
        lineTo(x + 2 * a, y0 + a); lineTo(x + a, y0 + a); lineTo(x + a, y1); lineTo(x + 4 * a, y1)
        x += 4 * a
    }
}

/**
 * El destello de un trofeo legendario, propio de cada tema: estrella romana, cruz de píxeles de consola, sol de Vergina,
 * hojuelas de pan de oro, el ojo del ministerio, un átomo, chispas de fragua, una burbuja, un asterisco de imprenta…
 */
private fun DrawScope.chispa(tema: TemaId, o: Offset, s: Float, i: Int) {
    when (tema) {
        TemaId.ROMANO -> drawPath(estrellada(o, s, s * 0.3f, 4), Color(0xFFFFF1C2).copy(alpha = 0.95f))
        // La moneda que brilla al caer en la fuente: el disco y su destello.
        TemaId.URBE -> { drawCircle(Color(0xFFE8C08A).copy(alpha = 0.95f), s * 0.42f, o); drawLine(Color.White.copy(alpha = 0.8f), Offset(o.x - s * 0.8f, o.y), Offset(o.x + s * 0.8f, o.y), s * 0.08f, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.8f), Offset(o.x, o.y - s * 0.8f), Offset(o.x, o.y + s * 0.8f), s * 0.08f, StrokeCap.Round) }
        TemaId.CIBER -> {
            val q = s * 0.42f
            listOf(0 to 0, 1 to 0, -1 to 0, 0 to 1, 0 to -1).forEach { (dx, dy) ->
                drawRect(Color(0xFFA6F4FF), Offset(o.x + dx * q - q * 0.45f, o.y + dy * q - q * 0.45f), Size(q * 0.9f, q * 0.9f))
            }
        }
        // Un chispazo de letrero que se enciende: cuatro rayitas de tubo alrededor de un punto, en los tres colores.
        TemaId.NEON -> {
            val col = listOf(Color(0xFFFF3D9A), Color(0xFFFFB238), Color(0xFF8CFF5A))[i % 3]
            for (k in 0 until 4) { val a = k * PI / 2 + PI / 4; val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
                drawLine(col.copy(alpha = 0.3f), o + dir * (s * 0.3f), o + dir * s, s * 0.3f, StrokeCap.Round)
                drawLine(col, o + dir * (s * 0.3f), o + dir * s, s * 0.1f, StrokeCap.Round) }
            drawCircle(Color.White, s * 0.12f, o)
        }
        TemaId.HELENO -> {
            for (k in 0 until 16) {
                val a = k * PI / 8; val l = if (k % 2 == 0) s else s * 0.6f
                drawLine(Color(0xFFFFE08A), o, Offset(o.x + (l * cos(a)).toFloat(), o.y + (l * sin(a)).toFloat()), s * 0.09f)
            }
            drawCircle(Color(0xFFFFE08A), s * 0.28f, o)
        }
        // Chispas de oro con forma de signo: el ankh, el ojo, la pluma y el escarabajo, uno tras otro.
        TemaId.EGIPTO -> signoEgipcio(i, o, s * 1.6f, Color(0xFFFFE08A).copy(alpha = 0.95f), s * 0.16f)
        TemaId.MEDIEVAL -> listOf(Offset(0f, -0.5f), Offset(-0.45f, 0.35f), Offset(0.5f, 0.3f)).forEachIndexed { k, d ->
            val p = Offset(o.x + d.x * s, o.y + d.y * s); val t = s * (0.38f - k * 0.06f)
            drawPath(Path().apply { moveTo(p.x, p.y - t); lineTo(p.x + t * 0.8f, p.y - t * 0.1f); lineTo(p.x + t * 0.1f, p.y + t); lineTo(p.x - t * 0.9f, p.y + t * 0.2f); close() }, Color(0xFFF2C94C))
        }
        // Chispas de la fragua del armero: tres puntos encendidos y una estela que cae.
        TemaId.TEMPLARIO -> listOf(Offset(0f, -0.45f), Offset(-0.5f, 0.3f), Offset(0.45f, 0.4f)).forEachIndexed { k, d ->
            val p = Offset(o.x + d.x * s, o.y + d.y * s); val t = s * (0.18f - k * 0.03f)
            drawLine(Color(0xFFF0A040).copy(alpha = 0.6f), p, Offset(p.x + d.x * s * 0.25f, p.y + s * 0.3f), t * 0.6f, StrokeCap.Round)
            drawCircle(Color(0xFFFFE2A8), t, p)
        }
        TemaId.DISTOPIA -> {
            val ojo = Path().apply { moveTo(o.x - s, o.y); quadraticTo(o.x, o.y - s * 0.9f, o.x + s, o.y); quadraticTo(o.x, o.y + s * 0.9f, o.x - s, o.y); close() }
            drawPath(ojo, Color.White.copy(alpha = 0.92f)); drawPath(ojo, Color(0xFF2B2B2B), style = Stroke(s * 0.1f))
            drawCircle(Color(0xFFD32F2F), s * 0.3f, o)
        }
        TemaId.REFUGIO -> {
            val col = Color(0xFFFFF59D)
            listOf(-60f, 0f, 60f).forEach { g -> rotate(g, o) { drawOval(col, Offset(o.x - s, o.y - s * 0.35f), Size(s * 2, s * 0.7f), style = Stroke(s * 0.1f)) } }
            drawCircle(col, s * 0.22f, o)
        }
        TemaId.ORDEN -> {
            val col = Color(0xFFFFB347)
            listOf(Offset(0.9f, -0.9f), Offset(1f, -0.2f), Offset(0.3f, -1f), Offset(-0.5f, -0.6f)).forEach { d ->
                drawLine(col, Offset(o.x + d.x * s * 0.25f, o.y + d.y * s * 0.25f), Offset(o.x + d.x * s, o.y + d.y * s), s * 0.12f, StrokeCap.Round)
            }
            drawCircle(Color(0xFFFFE0A0), s * 0.2f, o)
        }
        TemaId.LABORATORIO -> {
            drawCircle(Color(0xFFB8F4FF).copy(alpha = 0.35f), s * 0.8f, o); drawCircle(Color(0xFFB8F4FF), s * 0.8f, o, style = Stroke(s * 0.1f))
            drawArc(Color.White, 200f, 70f, false, Offset(o.x - s * 0.55f, o.y - s * 0.55f), Size(s * 1.1f, s * 1.1f), style = Stroke(s * 0.12f, cap = StrokeCap.Round))
        }
        TemaId.COLONOS -> for (k in 0 until 3) {
            val a = k * PI / 3 + PI / 2; val dx = (s * cos(a)).toFloat(); val dy = (s * sin(a)).toFloat()
            drawLine(Color(0xFFC0533A), Offset(o.x - dx, o.y - dy), Offset(o.x + dx, o.y + dy), s * 0.2f)
        }
        TemaId.NAPOLEON -> {
            // La abeja imperial, en silueta: alas, cuerpo y dos franjas.
            val ala = Color(0xFFFFF4D6).copy(alpha = 0.85f)
            drawOval(ala, Offset(o.x - s, o.y - s * 0.7f), Size(s * 0.9f, s * 0.6f)); drawOval(ala, Offset(o.x + s * 0.1f, o.y - s * 0.7f), Size(s * 0.9f, s * 0.6f))
            drawOval(Color(0xFFE8C252), Offset(o.x - s * 0.28f, o.y - s * 0.45f), Size(s * 0.56f, s * 1.3f))
            listOf(0.1f, 0.4f).forEach { f -> drawLine(Color(0xFF6B4E10), Offset(o.x - s * 0.25f, o.y + s * f), Offset(o.x + s * 0.25f, o.y + s * f), s * 0.09f) }
        }
        TemaId.BOTANICO -> {
            for (k in 0 until 5) { val a = -PI / 2 + k * 2 * PI / 5; drawCircle(Color(0xFFFFF0A8), s * 0.34f, Offset(o.x + (s * 0.5f * cos(a)).toFloat(), o.y + (s * 0.5f * sin(a)).toFloat())) }
            drawCircle(Color(0xFFB0702A), s * 0.24f, o)
        }
        TemaId.RUINAS -> {
            // Un vilano de diente de león: seis pelos y su semilla verde.
            for (k in 0 until 6) { val a = k * PI / 3; drawLine(Color(0xFFF2F5E6), o, Offset(o.x + (s * 0.6f * cos(a)).toFloat(), o.y + (s * 0.6f * sin(a)).toFloat()), s * 0.08f) }
            drawCircle(Color(0xFF8FCB6A), s * 0.18f, o)
        }
        TemaId.NAVIDAD -> {
            val col = Color(0xFFF4FBFF)
            for (k in 0 until 6) {
                val a = k * PI / 3
                drawLine(col, o, Offset(o.x + s * cos(a).toFloat(), o.y + s * sin(a).toFloat()), s * 0.1f, StrokeCap.Round)
                val m = Offset(o.x + s * 0.6f * cos(a).toFloat(), o.y + s * 0.6f * sin(a).toFloat())
                listOf(a + 0.6, a - 0.6).forEach { b -> drawLine(col, m, Offset(m.x + s * 0.3f * cos(b).toFloat(), m.y + s * 0.3f * sin(b).toFloat()), s * 0.08f, StrokeCap.Round) }
            }
        }
        TemaId.SEDA -> {
            drawCircle(Color(0xFFF0C75E), s * 0.75f, o)
            drawRect(Color(0xFF8A5A12), Offset(o.x - s * 0.22f, o.y - s * 0.22f), Size(s * 0.44f, s * 0.44f))
            drawCircle(Color(0xFF8A5A12), s * 0.75f, o, style = Stroke(s * 0.08f))
        }
        // Una flor de cerezo que cae.
        TemaId.EDO -> {
            for (k in 0 until 5) { val a = -kotlin.math.PI / 2 + k * 2 * kotlin.math.PI / 5; drawCircle(Color(0xFFF2B8C6), s * 0.34f, Offset(o.x + (s * 0.4f * kotlin.math.cos(a)).toFloat(), o.y + (s * 0.4f * kotlin.math.sin(a)).toFloat())) }
            drawCircle(Color(0xFFF2C94C), s * 0.16f, o)
        }
        TemaId.GOTICO -> {
            val col = if (i % 2 == 0) Color(0xFFE0475B) else Color(0xFF4F7BE8)
            listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { d -> drawCircle(col, s * 0.38f, Offset(o.x + d.x * s * 0.36f, o.y + d.y * s * 0.36f)) }
            drawCircle(Color(0xFFF5D76E), s * 0.2f, o)
        }
        // Un destello pálido de faro, con su halo verde de niebla.
        TemaId.ARCANO -> {
            drawCircle(Color(0xFF7DBBA4).copy(alpha = 0.3f), s * 0.8f, o)
            listOf(0f, 90f).forEach { g -> rotate(g + i * 15f, o) { drawLine(Color(0xFFF2E3A0), Offset(o.x - s, o.y), Offset(o.x + s, o.y), s * 0.12f, cap = StrokeCap.Round) } }
            drawCircle(Color(0xFFF2E3A0), s * 0.22f, o)
        }
        TemaId.ESPACIO -> {
            val col = Color(0xFFE6F0FF)
            drawLine(col, Offset(o.x - s * 1.3f, o.y), Offset(o.x + s * 1.3f, o.y), s * 0.06f); drawLine(col, Offset(o.x, o.y - s * 1.3f), Offset(o.x, o.y + s * 1.3f), s * 0.06f)
            drawCircle(col.copy(alpha = 0.5f), s * 0.6f, o, style = Stroke(s * 0.06f)); drawCircle(col, s * 0.22f, o)
        }
        // Una burbuja que sube, con su brillo; una de cada tres, de luz viva verde agua.
        TemaId.ABISMO -> {
            val col = if (i % 3 == 0) Color(0xFF5CF2C6) else Color(0xFFE6F6FA)
            drawCircle(col.copy(alpha = 0.85f), s * 0.7f, o, style = Stroke(s * 0.08f))
            drawCircle(col, s * 0.16f, Offset(o.x - s * 0.28f, o.y - s * 0.28f))
        }
        TemaId.ANDALUS -> drawPath(estrellada(o, s, s * 0.74f, 8), Color(0xFFF5D27A))
        // Chispas de jade: una cuenta con su brillo, o un punto de numeral.
        TemaId.MESO -> { drawCircle(Color(0xFF6FCB8F), s * 0.45f, o); drawCircle(Color.White.copy(alpha = 0.6f), s * 0.15f, Offset(o.x - s * 0.15f, o.y - s * 0.15f)) }
        TemaId.MONGOL -> {
            val col = Color(0xFF8FD3FF)
            drawPath(Path().apply { moveTo(o.x, o.y - s); lineTo(o.x + s, o.y); lineTo(o.x, o.y + s); lineTo(o.x - s, o.y); close() }, col, style = Stroke(s * 0.12f))
            drawLine(col, Offset(o.x - s * 0.5f, o.y - s * 0.5f), Offset(o.x + s * 0.5f, o.y + s * 0.5f), s * 0.1f)
            drawLine(col, Offset(o.x + s * 0.5f, o.y - s * 0.5f), Offset(o.x - s * 0.5f, o.y + s * 0.5f), s * 0.1f)
        }
        TemaId.RENACIMIENTO -> {
            val col = Color(0xFFD8743E)
            drawCircle(col, s * 0.6f, o, style = Stroke(s * 0.07f))
            drawLine(col, Offset(o.x - s * 1.1f, o.y), Offset(o.x + s * 1.1f, o.y), s * 0.06f); drawLine(col, Offset(o.x, o.y - s * 1.1f), Offset(o.x, o.y + s * 1.1f), s * 0.06f)
            drawCircle(col, s * 0.12f, o)
        }
        // El destello de un doblón: una moneda con su cruz y cuatro rayos cortos.
        TemaId.PIRATA -> {
            val oro = Color(0xFFF2C14E)
            drawCircle(oro, s * 0.5f, o); drawCircle(Color(0xFF8A6418), s * 0.36f, o, style = Stroke(s * 0.07f))
            drawLine(Color(0xFF8A6418), Offset(o.x - s * 0.22f, o.y), Offset(o.x + s * 0.22f, o.y), s * 0.07f); drawLine(Color(0xFF8A6418), Offset(o.x, o.y - s * 0.22f), Offset(o.x, o.y + s * 0.22f), s * 0.07f)
            for (k in 0 until 4) { val a = PI / 4 + k * PI / 2; drawLine(oro, Offset(o.x + (s * 0.65f * cos(a)).toFloat(), o.y + (s * 0.65f * sin(a)).toFloat()), Offset(o.x + (s * 1.05f * cos(a)).toFloat(), o.y + (s * 1.05f * sin(a)).toFloat()), s * 0.08f) }
        }
        TemaId.URSS -> drawPath(estrellada(o, s, s * 0.4f, 5), Color(0xFFE53935))
        TemaId.REVOLUCION -> { drawCircle(Color(0xFF1F3A8A), s * 0.8f, o); drawCircle(Color.White, s * 0.55f, o); drawCircle(Color(0xFFC62828), s * 0.3f, o) }
        TemaId.ANTIGUAS -> {
            val col = Color(0xFF9FE8FF)
            drawCircle(col.copy(alpha = 0.22f), s, o)
            drawLine(col, Offset(o.x - s * 0.2f, o.y - s * 0.8f), Offset(o.x - s * 0.2f, o.y + s * 0.8f), s * 0.12f, StrokeCap.Round)
            drawLine(col, Offset(o.x - s * 0.2f, o.y - s * 0.3f), Offset(o.x + s * 0.45f, o.y - s * 0.75f), s * 0.12f, StrokeCap.Round)
            drawLine(col, Offset(o.x - s * 0.2f, o.y + s * 0.15f), Offset(o.x + s * 0.45f, o.y - s * 0.3f), s * 0.12f, StrokeCap.Round)
        }
        TemaId.CUEVA -> {
            // Una chispa del fuego que sube, con su halo y su estela.
            val col = Color(0xFFFFB347)
            drawCircle(col.copy(alpha = 0.25f), s, o)
            drawCircle(col, s * 0.32f, o)
            drawLine(col.copy(alpha = 0.6f), o, Offset(o.x + s * 0.2f, o.y + s * 1.1f), s * 0.14f, StrokeCap.Round)
        }
    }
}

/** El tono de la pared de la sala de trofeos en cada tema (se mezcla, tenue, con la superficie). */
private fun tonoPared(tema: TemaId): Color = when (tema) {
    TemaId.ROMANO -> Color(0xFFD9CBB0)
    TemaId.URBE -> Color(0xFFE2B57A)
    TemaId.CIBER -> Color(0xFF06262C)
    TemaId.NEON -> Color(0xFF2A1430)
    TemaId.HELENO -> Color(0xFFC9D8E6)
    TemaId.EGIPTO -> Color(0xFFE6CF9A)
    TemaId.MEDIEVAL -> Color(0xFFD8C08E)
    TemaId.TEMPLARIO -> Color(0xFFCDBB97)
    TemaId.DISTOPIA -> Color(0xFF8E9296)
    TemaId.REFUGIO -> Color(0xFF9CC9C1)
    TemaId.ORDEN -> Color(0xFF6F7882)
    TemaId.LABORATORIO -> Color(0xFFDDF3F6)
    TemaId.COLONOS -> Color(0xFFCDB89A)
    TemaId.NAPOLEON -> Color(0xFF2E4A8C)
    TemaId.BOTANICO -> Color(0xFFB9C49A)
    TemaId.RUINAS -> Color(0xFFA9AA9C)   // hormigón
    TemaId.NAVIDAD -> Color(0xFF8E2A2A)
    TemaId.SEDA -> Color(0xFFD9A35A)
    TemaId.EDO -> Color(0xFFC9B48A)
    TemaId.GOTICO -> Color(0xFF2C3A6B)
    TemaId.ARCANO -> Color(0xFF2E4A3E)
    TemaId.ESPACIO -> Color(0xFF1B2140)
    TemaId.ABISMO -> Color(0xFF0B2A3A)          // el agua honda tras el cristal
    TemaId.ANDALUS -> Color(0xFFE2C98F)
    TemaId.MESO -> Color(0xFFE6D6B6)          // el estuco de la sala del templo
    TemaId.MONGOL -> Color(0xFF4F86C6)
    TemaId.RENACIMIENTO -> Color(0xFFD9B98F)
    TemaId.PIRATA -> Color(0xFF7A5634)          // la tablazón de roble de la cámara
    TemaId.URSS -> Color(0xFFC8B79A)
    TemaId.REVOLUCION -> Color(0xFFD6CCB8)
    TemaId.ANTIGUAS -> Color(0xFF7D7668)
    TemaId.CUEVA -> Color(0xFFB59A78)          // la caliza de la cueva
}

/**
 * La pared de la sala, muy tenue, detrás de las repisas: vetas de mármol, líneas de barrido de la consola, estrías,
 * pautado de pergamino, paneles atornillados, azulejo, chapa estriada, tablero perforado, tablas con tapajuntas,
 * papel a rayas con rombos, pliegos de herbario, guirnalda con nieve, monedas entrelazadas, sillares con arcos ciegos,
 * estrellas, lazo de ocho, celosía de la ger, trazos de construcción, cuña constructivista, carteles y piedra tosca.
 */
private fun DrawScope.paredTema(tema: TemaId, tinta: Color, acento: Color) {
    val w = size.width; val h = size.height; val d = 1.dp.toPx()
    val v = tinta.copy(alpha = 0.07f); val a = acento.copy(alpha = 0.09f)
    when (tema) {
        TemaId.ROMANO -> for (k in 0 until 7) {
            val y0 = h * (0.06f + k * 0.15f)
            drawPath(Path().apply { moveTo(0f, y0); cubicTo(w * 0.28f, y0 - h * 0.05f, w * 0.52f, y0 + h * 0.07f, w * 0.74f, y0 + h * 0.01f); cubicTo(w * 0.86f, y0 - h * 0.02f, w * 0.94f, y0 + h * 0.04f, w, y0 + h * 0.03f) },
                v, style = Stroke(1.3f * d))
        }
        // El muro de revoque ocre, con manchas donde se cayó el enlucido y asoma el ladrillo en hiladas.
        TemaId.URBE -> listOf(0.2f to 0.25f, 0.72f to 0.6f, 0.35f to 0.85f).forEach { (fx, fy) ->
            val cx = w * fx; val cy = h * fy; val rx = w * 0.16f; val ry = h * 0.07f
            drawOval(a, Offset(cx - rx, cy - ry), Size(2 * rx, 2 * ry), style = Stroke(1.2f * d))
            var y = cy - ry * 0.6f; var fila = 0
            while (y < cy + ry * 0.6f) { drawLine(v, Offset(cx - rx * 0.8f, y), Offset(cx + rx * 0.8f, y), d)
                var x = cx - rx * 0.8f + (if (fila % 2 == 0) 0f else 5 * d); while (x < cx + rx * 0.8f) { drawLine(v, Offset(x, y), Offset(x, y + 4 * d), d); x += 10 * d }
                y += 4 * d; fila++ }
        }
        TemaId.CIBER -> {
            var y = 0f
            while (y < h) { drawLine(Color(0xFF00E5FF).copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), d); y += 3 * d }
            drawLine(Color(0xFF00E5FF).copy(alpha = 0.12f), Offset(14 * d, 0f), Offset(14 * d, h), d)
        }
        // La pared del callejón: ladrillos húmedos y el reflejo de un letrero que baña la mitad de arriba.
        TemaId.NEON -> {
            var y = 0f; var fila = 0
            while (y < h) {
                drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, y), Offset(w, y), d)
                var x = if (fila % 2 == 0) 0f else 11 * d; while (x < w) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(x, y), Offset(x, y + 8 * d), d); x += 22 * d }
                y += 8 * d; fila++
            }
            drawRect(Brush.verticalGradient(listOf(Color(0xFFFF3D9A).copy(alpha = 0.12f), Color.Transparent), 0f, h * 0.5f), Offset.Zero, Size(w, h * 0.5f))
        }
        TemaId.HELENO -> {
            var x = 8 * d; var k = 0
            while (x < w) { drawLine(if (k % 2 == 0) v else a, Offset(x, 22 * d), Offset(x, h), 2 * d); x += 12 * d; k++ }
            drawPath(Path().apply { grecaRepisa(0f, 6 * d, 3 * d, w) }, a, style = Stroke(1.2f * d))
        }
        // Un muro de tumba en registros: franjas separadas por líneas y, arriba, el friso jeker de juncos atados.
        TemaId.EGIPTO -> {
            for (k in 1 until 6) drawLine(a, Offset(0f, h * k / 6f), Offset(w, h * k / 6f), 1.4f * d)
            var x = 4 * d; var k = 0
            while (x < w) { drawRect(if (k % 2 == 0) v else a, Offset(x - 1.2f * d, 10 * d), Size(2.4f * d, 10 * d)); drawCircle(if (k % 2 == 0) v else a, 1.6f * d, Offset(x, 9 * d)); x += 7 * d; k++ }
        }
        TemaId.MEDIEVAL -> {
            var y = 18 * d
            while (y < h) { drawLine(v, Offset(0f, y), Offset(w, y), d); drawLine(v, Offset(0f, y + 4 * d), Offset(w, y + 4 * d), d * 0.6f); y += 26 * d }
            listOf(0.05f, 0.95f).forEach { fx -> drawLine(Color(0xFFB0302A).copy(alpha = 0.12f), Offset(w * fx, 0f), Offset(w * fx, h), d) }
        }
        // Los sillares de la sala de armas, a soga, con la banda roja de la casa a media altura.
        TemaId.TEMPLARIO -> {
            val alto = 20 * d; val largo = 44 * d; var y = alto; var fila = 0
            while (y < h) {
                drawLine(v, Offset(0f, y), Offset(w, y), d)
                var x = if (fila % 2 == 0) largo / 2 else 0f
                while (x < w) { drawLine(v, Offset(x, y - alto), Offset(x, y), d); x += largo }
                y += alto; fila++
            }
            drawRect(Color(0xFFB0191F).copy(alpha = 0.1f), Offset(0f, h * 0.36f), Size(w, 6 * d))
        }
        TemaId.DISTOPIA -> {
            val pw = w / 3; val ph = 70 * d; var y = 0f
            while (y < h) {
                for (k in 0 until 3) {
                    drawRect(v, Offset(k * pw + 2 * d, y + 2 * d), Size(pw - 4 * d, ph - 4 * d), style = Stroke(d))
                    listOf(Offset(k * pw + 7 * d, y + 7 * d), Offset((k + 1) * pw - 7 * d, y + 7 * d), Offset(k * pw + 7 * d, y + ph - 7 * d), Offset((k + 1) * pw - 7 * d, y + ph - 7 * d))
                        .forEach { drawCircle(v, 1.6f * d, it) }
                }
                y += ph
            }
        }
        TemaId.REFUGIO -> {
            val t = 22 * d; var y = 0f
            while (y < h) { var x = 0f; while (x < w) { drawRoundRect(v, Offset(x + d, y + d), Size(t - 2 * d, t - 2 * d), CornerRadius(3 * d), style = Stroke(d)); x += t }; y += t }
            drawRect(a, Offset(0f, 0f), Size(w, 6 * d))
        }
        TemaId.ORDEN -> {
            val t = 16 * d; var y = 0f; var f = 0
            while (y < h) {
                var x = if (f % 2 == 0) 0f else t / 2; var k = 0; val e = 3 * d
                while (x < w) {
                    if ((k + f) % 2 == 0) drawLine(v, Offset(x - e, y + e), Offset(x + e, y - e), 1.8f * d, StrokeCap.Round)
                    else drawLine(v, Offset(x - e, y - e), Offset(x + e, y + e), 1.8f * d, StrokeCap.Round)
                    x += t; k++
                }
                y += t; f++
            }
        }
        TemaId.LABORATORIO -> { val t = 18 * d; var y = t / 2; while (y < h) { var x = t / 2; while (x < w) { drawCircle(v, 1.3f * d, Offset(x, y)); x += t }; y += t } }
        TemaId.COLONOS -> { var x = 0f; while (x < w) { drawLine(v, Offset(x, 0f), Offset(x, h), d); drawRect(v, Offset(x - 2 * d, 0f), Size(4 * d, h), style = Stroke(d * 0.6f)); x += 28 * d } }
        TemaId.NAPOLEON -> {
            val t = 22 * d; var x = 0f; var k = 0
            while (x < w) {
                if (k % 2 == 0) {
                    drawRect(a, Offset(x, 0f), Size(t, h))
                    var y = 20 * d; val cx = x + t / 2
                    while (y < h) { drawPath(Path().apply { moveTo(cx, y - 4 * d); lineTo(cx + 3 * d, y); lineTo(cx, y + 4 * d); lineTo(cx - 3 * d, y); close() }, v); y += 34 * d }
                }
                x += t; k++
            }
        }
        TemaId.BOTANICO -> {
            val hw = 70 * d; val hh = 90 * d; var y = 8 * d; var f = 0
            while (y < h) {
                var x = if (f % 2 == 0) 6 * d else 6 * d - hw / 2
                while (x < w) {
                    drawRect(v, Offset(x, y), Size(hw - 12 * d, hh - 12 * d), style = Stroke(d))
                    drawRect(v, Offset(x + hw - 34 * d, y + hh - 24 * d), Size(18 * d, 8 * d))
                    val cx = x + (hw - 12 * d) / 2
                    drawLine(v, Offset(cx, y + hh - 26 * d), Offset(cx, y + 14 * d), d)
                    for (j in 0 until 4) { val yy = y + 22 * d + j * 12 * d; drawLine(v, Offset(cx, yy + 4 * d), Offset(cx - (10 - j) * d, yy), d); drawLine(v, Offset(cx, yy + 4 * d), Offset(cx + (10 - j) * d, yy), d) }
                    x += hw
                }
                y += hh; f++
            }
        }
        TemaId.RUINAS -> {
            // Un muro de bloques de hormigón, con algunas grietas y hiedra que trepa desde abajo por los dos lados.
            val bw = 44 * d; val bh = 22 * d; var y = 0f; var f = 0
            while (y < h) { var x = if (f % 2 == 0) 0f else -bw / 2; while (x < w) { drawRect(v, Offset(x, y), Size(bw, bh), style = Stroke(d)); x += bw }; y += bh; f++ }
            val az = java.util.Random(7)
            repeat(6) {
                var x = az.nextFloat() * w; var yy = az.nextFloat() * h
                val p = Path().apply { moveTo(x, yy); repeat(4) { x += (az.nextFloat() - 0.5f) * 14 * d; yy += 8 * d; lineTo(x, yy) } }
                drawPath(p, v, style = Stroke(d))
            }
            listOf(0.12f, 0.88f).forEach { fx ->
                var yy = h; var x = w * fx; var k = 0
                while (yy > h * 0.35f) { drawOval(a, Offset(x - 5 * d, yy - 4 * d), Size(10 * d, 7 * d)); yy -= 9 * d; x += (if (k % 2 == 0) 3f else -3f) * d; k++ }
            }
        }
        TemaId.NAVIDAD -> {
            for (k in 0 until 60) drawCircle(Color.White.copy(alpha = 0.12f), (1 + k % 3) * d, Offset(((k * 73) % 100) / 100f * w, ((k * 37 + 11) % 100) / 100f * h))
            val paso = 48 * d; var x = 0f
            while (x < w) {
                drawArc(Color(0xFF2E6B3A).copy(alpha = 0.35f), 0f, 180f, false, Offset(x, -10 * d), Size(paso, 26 * d), style = Stroke(4 * d, cap = StrokeCap.Round))
                drawCircle(Color(0xFFC62828).copy(alpha = 0.5f), 3 * d, Offset(x, 3 * d))
                x += paso
            }
        }
        TemaId.SEDA -> { val r = 14 * d; val t = 20 * d; var y = 0f; while (y < h + r) { var x = 0f; while (x < w + r) { drawCircle(v, r, Offset(x, y), style = Stroke(d)); x += t }; y += t } }
        // Un shōji: la celosía de listones y, cada tanto, el marco más grueso.
        TemaId.EDO -> {
            var x = 0f; var k = 0; while (x < w) { drawLine(v, Offset(x, 0f), Offset(x, h), if (k % 4 == 0) 2.4f * d else d); x += 40 * d; k++ }
            var y = 0f; while (y < h) { drawLine(v, Offset(0f, y), Offset(w, y), d); y += 28 * d }
        }
        TemaId.GOTICO -> {
            val bw = 40 * d; val bh = 20 * d; var y = 0f; var f = 0
            while (y < h) { var x = if (f % 2 == 0) 0f else -bw / 2; while (x < w) { drawRect(v, Offset(x, y), Size(bw, bh), style = Stroke(d)); x += bw }; y += bh; f++ }
            val aw = 30 * d; var x = 4 * d
            while (x + aw < w) {
                drawPath(Path().apply { moveTo(x, 44 * d); lineTo(x, 22 * d); quadraticTo(x, 8 * d, x + aw / 2, 4 * d); quadraticTo(x + aw, 8 * d, x + aw, 22 * d); lineTo(x + aw, 44 * d) }, a, style = Stroke(1.6f * d))
                x += aw + 6 * d
            }
        }
        // El pizarrón de corcho del investigador: recortes clavados, torcidos, unidos por hilos.
        TemaId.ARCANO -> {
            val az = java.util.Random(13)
            val pines = mutableListOf<Offset>()
            repeat(((w * h) / (110 * d * 110 * d)).toInt().coerceIn(6, 40)) {
                val o = Offset(az.nextFloat() * w, az.nextFloat() * h); val g = az.nextFloat() * 16f - 8f
                rotate(g, o) {
                    drawRect(v, Offset(o.x - 22 * d, o.y - 15 * d), Size(44 * d, 30 * d), style = Stroke(d))
                    for (k in 0 until 3) drawLine(v, Offset(o.x - 16 * d, o.y - 6 * d + k * 6 * d), Offset(o.x + 16 * d, o.y - 6 * d + k * 6 * d), 0.8f * d)
                }
                pines += Offset(o.x, o.y - 15 * d)
            }
            pines.zipWithNext().forEachIndexed { k, (p, q) -> if (k % 2 == 0) drawLine(a, p, q, 0.9f * d) }
            pines.forEach { drawCircle(a, 2.2f * d, it) }
        }
        TemaId.ESPACIO -> {
            for (k in 0 until 90) drawCircle(Color.White.copy(alpha = if (k % 7 == 0) 0.3f else 0.12f), if (k % 7 == 0) 1.6f * d else 0.9f * d,
                Offset(((k * 61 + 7) % 113) / 113f * w, ((k * 47 + 3) % 127) / 127f * h))
            drawOval(a, Offset(-w * 0.2f, h * 0.1f), Size(w * 1.4f, h * 0.3f), style = Stroke(d))
            drawOval(a, Offset(-w * 0.1f, h * 0.55f), Size(w * 1.2f, h * 0.25f), style = Stroke(d))
        }
        // La red de luz del oleaje arriba (cáusticas) y columnas de burbujas que suben.
        TemaId.ABISMO -> {
            for (f in 0 until 5) {
                val y0 = h * (0.06f + f * 0.07f); val p = Path(); var x = 0f
                p.moveTo(0f, y0)
                while (x <= w) { p.lineTo(x, y0 + 5 * d * kotlin.math.sin(x / (21 * d) + f * 1.3f) + 2 * d * kotlin.math.sin(x / (7 * d) + f)); x += 4 * d }
                drawPath(p, a, style = Stroke(d))
            }
            listOf(0.12f, 0.37f, 0.63f, 0.88f).forEachIndexed { j, fx ->
                var y = h * 0.95f; var k = 0
                while (y > h * 0.4f) { drawCircle(v.copy(alpha = 0.14f), (1.2f + (k + j) % 3) * d, Offset(w * fx + 3 * d * kotlin.math.sin(k * 1.7f), y), style = Stroke(0.8f * d)); y -= 14 * d; k++ }
            }
        }
        TemaId.ANDALUS -> { val t = 28 * d; var y = t / 2; while (y < h) { var x = t / 2; while (x < w) { drawPath(estrellada(Offset(x, y), t * 0.42f, t * 0.32f, 8), v, style = Stroke(d)); x += t }; y += t } }
        // Muro de sillares con el friso de grecas escalonadas arriba.
        TemaId.MESO -> {
            val alto = 16 * d; var y = 30 * d; var fila = 0
            while (y < h) { drawLine(v, Offset(0f, y), Offset(w, y), d); var x = if (fila % 2 == 0) 0f else 14 * d; while (x < w) { drawLine(v, Offset(x, y), Offset(x, y + alto), d); x += 28 * d }; y += alto; fila++ }
            grecaEscalonada(0f, w, 6 * d, 22 * d, a, 1.6f * d)
        }
        TemaId.MONGOL -> {
            val t = 18 * d; var x = -h
            while (x < w) { drawLine(v, Offset(x, 0f), Offset(x + h, h), d); drawLine(v, Offset(x + h, 0f), Offset(x, h), d); x += t }
            listOf(Color(0xFFC62828), Color(0xFF1565C0), Color(0xFFC62828)).forEachIndexed { k, col -> drawRect(col.copy(alpha = 0.12f), Offset(0f, k * 4 * d), Size(w, 4 * d)) }
        }
        TemaId.RENACIMIENTO -> {
            val r = w * 0.32f; var y = r; var k = 0
            while (y < h + r) {
                val cx = if (k % 2 == 0) w * 0.3f else w * 0.7f
                drawCircle(a, r, Offset(cx, y), style = Stroke(d))
                drawRect(v, Offset(cx - r, y - r), Size(2 * r, 2 * r), style = Stroke(d))
                drawLine(v, Offset(cx - r, y - r), Offset(cx + r, y + r), d); drawLine(v, Offset(cx + r, y - r), Offset(cx - r, y + r), d)
                y += r * 1.6f; k++
            }
        }
        // La pared de tablones con sus clavos y, colgada, una carta náutica: líneas de rumbo que salen de una rosa.
        TemaId.PIRATA -> {
            var x = 0f; while (x < w) { drawLine(v, Offset(x, 0f), Offset(x, h), 1.2f * d); var y = 20 * d; while (y < h) { drawCircle(v, 1.2f * d, Offset(x + 6 * d, y)); y += 90 * d }; x += 26 * d }
            val o = Offset(w * 0.62f, h * 0.3f); val largo = w * 0.6f
            for (i in 0 until 16) { val an = i * PI / 8; drawLine(a, o, Offset(o.x + (largo * cos(an)).toFloat(), o.y + (largo * sin(an)).toFloat()), d) }
            drawCircle(a, 18 * d, o, style = Stroke(d)); drawCircle(a, 10 * d, o, style = Stroke(d))
        }
        TemaId.URSS -> {
            var y = 0f
            while (y < h) {
                drawPath(Path().apply { moveTo(0f, y + 120 * d); lineTo(w, y); lineTo(w, y + 40 * d); lineTo(0f, y + 160 * d); close() }, Color(0xFFC62828).copy(alpha = 0.07f))
                drawCircle(v, 46 * d, Offset(w * 0.78f, y + 150 * d), style = Stroke(3 * d))
                drawRect(v, Offset(w * 0.06f, y + 200 * d), Size(10 * d, 90 * d))
                y += 320 * d
            }
        }
        TemaId.REVOLUCION -> {
            listOf(Color(0xFF1F3A8A), Color.White, Color(0xFFC62828)).forEachIndexed { k, col -> drawRect(col.copy(alpha = 0.25f), Offset(k * w / 3, 0f), Size(w / 3, 4 * d)) }
            var y = 20 * d; var k = 0
            while (y < h) {
                val x = if (k % 2 == 0) w * 0.08f else w * 0.55f
                rotate(if (k % 2 == 0) -3f else 2.5f, Offset(x + w * 0.18f, y + 50 * d)) {
                    drawRect(v, Offset(x, y), Size(w * 0.36f, 100 * d), style = Stroke(d))
                    for (j in 0 until 4) drawLine(v, Offset(x + 8 * d, y + 20 * d + j * 16 * d), Offset(x + w * 0.3f, y + 20 * d + j * 16 * d), 2 * d)
                }
                y += 70 * d; k++
            }
        }
        TemaId.ANTIGUAS -> {
            val anchos = listOf(46f, 30f, 58f, 38f, 52f); var y = 0f; var f = 0
            while (y < h) {
                var x = -((f * 17) % 40) * d; var k = f
                while (x < w) { val bw = anchos[k % anchos.size] * d; drawRoundRect(v, Offset(x + 1.5f * d, y + 1.5f * d), Size(bw - 3 * d, 23 * d), CornerRadius(5 * d), style = Stroke(1.2f * d)); x += bw; k++ }
                y += 26 * d; f++
            }
        }
        TemaId.CUEVA -> {
            // La roca de la cueva: vetas que ondulan y, apenas, manos de ocre con su hilera de puntos.
            var y = 18 * d; var k = 0
            while (y < h) {
                val veta = Path().apply { moveTo(0f, y); var x = 0f; var j = k; while (x < w) { val nx = x + 60 * d; quadraticTo(x + 30 * d, y + (if (j % 2 == 0) 6f else -6f) * d, nx, y); x = nx; j++ } }
                drawPath(veta, v, style = Stroke(1.2f * d)); y += 34 * d; k++
            }
            var x = 34 * d; var i = 0
            while (x < w) {
                val m = Offset(x, h * (if (i % 2 == 0) 0.3f else 0.62f))
                drawPath(io.github.ruddy2003.vestigium.ui.manoUnida(m, 11 * d), a)
                for (j in 0 until 4) drawCircle(a, 2 * d, Offset(m.x - 9 * d + j * 6 * d, m.y + 18 * d))
                x += 110 * d; i++
            }
        }
    }
}

/**
 * La repisa de cada tema: losa de mármol con dentículos, riel de servidores con luces, mármol con greca, tabla de roble
 * con herrajes, pupitre de acero, estante esmaltado, hierro forjado con ménsulas, vidrio de laboratorio, tabla de pino,
 * caoba con bronce dorado, anaquel de herbario con portaetiquetas, repisa de chimenea con acebo, cañas de bambú,
 * cornisa de piedra con arquillos, consola con pilotos, cedro con mocárabes y alicatado, arcón pintado, taracea de nogal,
 * viga de acero calada, tabla con la cinta tricolor y viga de piedra con runas.
 */
private fun DrawScope.repisaTema(tema: TemaId, oscuro: Boolean) {
    val w = size.width; val h = size.height; val d = 1.dp.toPx()
    fun m(c: Color) = if (oscuro) lerp(c, Color.Black, 0.4f) else c
    fun tabla(c: Color, y0: Float = 0f, alto: Float = h) =
        drawRect(Brush.verticalGradient(listOf(lerp(c, Color.White, 0.18f), c, lerp(c, Color.Black, 0.35f)), y0, y0 + alto), Offset(0f, y0), Size(w, alto))
    when (tema) {
        TemaId.ROMANO -> {
            val c = m(Color(0xFFD9CDB2)); tabla(c, 0f, h * 0.5f)
            drawLine(lerp(c, Color.Black, 0.3f), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), d)
            var x = 2 * d; while (x < w) { drawRect(lerp(c, Color.Black, 0.12f), Offset(x, h * 0.5f), Size(5 * d, h * 0.32f)); x += 8 * d }
        }
        // El alféizar de travertino con sus poros alargados y, colgando debajo, una hilera de macetas de geranios.
        TemaId.URBE -> {
            val c = m(Color(0xFFE6D8BC)); tabla(c, 0f, h * 0.55f)
            var x = 6 * d; var k = 0; while (x < w) { drawOval(lerp(c, Color.Black, 0.25f), Offset(x, h * (0.15f + (k % 3) * 0.12f)), Size(6 * d, 1.4f * d)); x += 13 * d; k++ }
            x = 18 * d; while (x < w - 10 * d) { drawRect(m(Color(0xFFA8432A)), Offset(x, h * 0.55f), Size(8 * d, h * 0.3f)); drawCircle(m(Color(0xFFC8364A)), 2.4f * d, Offset(x + 4 * d, h * 0.55f)); x += 46 * d }
        }
        TemaId.CIBER -> {
            tabla(m(Color(0xFF1A1F24)))
            var x = 6 * d; var k = 0
            while (x < w) {
                drawRect(Color.Black, Offset(x, h * 0.25f), Size(3 * d, 3 * d)); drawRect(Color.Black, Offset(x, h * 0.6f), Size(3 * d, 3 * d))
                if (k % 4 == 1) drawCircle(if (k % 12 == 5) Color(0xFFC8FBFF) else Color(0xFF00E5FF), 1.4f * d, Offset(x + 9 * d, h * 0.45f))
                x += 14 * d; k++
            }
        }
        // La repisa de la rejilla del puesto, con su borde de tubo de neón.
        TemaId.NEON -> {
            tabla(m(Color(0xFF3A2E42)))
            var x = 8 * d; while (x < w) { drawCircle(Color(0xFF0C0811), 2.2f * d, Offset(x, h * 0.55f)); x += 10 * d }
            drawLine(Color(0xFFFF3D9A).copy(alpha = 0.3f), Offset(0f, 1.5f * d), Offset(w, 1.5f * d), 4 * d)
            drawLine(Color(0xFFFF3D9A), Offset(0f, 1.5f * d), Offset(w, 1.5f * d), 1.2f * d)
        }
        TemaId.HELENO -> { tabla(m(Color(0xFFE9E4D8))); drawPath(Path().apply { grecaRepisa(0f, h * 0.2f, h * 0.2f, w) }, m(Color(0xFF1E5F8C)), style = Stroke(1.2f * d)) }
        // Una repisa de acacia con la cenefa de rectángulos de lapislázuli, oro y cornalina.
        TemaId.EGIPTO -> {
            tabla(m(Color(0xFFB08A58)))
            val colores = listOf(0xFF1D2F7A, 0xFFC9971C, 0xFFB8471E, 0xFFC9971C)
            var x = 0f; var k = 0
            while (x < w) { drawRect(m(Color(colores[k % 4])), Offset(x, h * 0.15f), Size(6 * d, h * 0.22f)); x += 8 * d; k++ }
        }
        TemaId.MEDIEVAL -> {
            tabla(m(Color(0xFF6B4528)))
            var x = 30 * d
            while (x < w) {
                drawRect(m(Color(0xFF2E2A27)), Offset(x, 0f), Size(8 * d, h))
                drawCircle(m(Color(0xFF9A9389)), 1.3f * d, Offset(x + 4 * d, h * 0.3f)); drawCircle(m(Color(0xFF9A9389)), 1.3f * d, Offset(x + 4 * d, h * 0.7f))
                x += 90 * d
            }
        }
        // El armero: tabla de roble negro, colgaderos de hierro y, entre ellos, escudetes con la cruz.
        TemaId.TEMPLARIO -> {
            tabla(m(Color(0xFF4A3524)))
            var x = 24 * d; var k = 0
            while (x < w) {
                if (k % 2 == 0) { drawRect(m(Color(0xFF3A3D42)), Offset(x, h * 0.2f), Size(10 * d, h * 0.6f)); drawCircle(m(Color(0xFF9A9389)), 1.4f * d, Offset(x + 5 * d, h * 0.5f)) }
                else drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(x + 5 * d, h * 0.5f), h * 0.3f), m(Color(0xFFB0191F)))
                x += 60 * d; k++
            }
        }
        TemaId.DISTOPIA -> {
            tabla(m(Color(0xFF7D8287)))
            drawRect(m(Color(0xFFB71C1C)), Offset(0f, h * 0.78f), Size(w, h * 0.12f))
            var x = 24 * d
            while (x < w - 30 * d) {
                drawRect(m(Color(0xFFEDEDE6)), Offset(x, h * 0.18f), Size(26 * d, h * 0.45f)); drawRect(m(Color(0xFF3A3D40)), Offset(x, h * 0.18f), Size(26 * d, h * 0.45f), style = Stroke(d))
                x += 110 * d
            }
        }
        TemaId.REFUGIO -> {
            drawRoundRect(m(Color(0xFFE9DDBF)), Offset.Zero, Size(w, h * 0.8f), CornerRadius(h * 0.4f))
            drawRect(Brush.verticalGradient(listOf(Color.White, m(Color(0xFFA8B0B5)), Color.White), h * 0.45f, h * 0.7f), Offset(h * 0.4f, h * 0.45f), Size(w - h * 0.8f, h * 0.25f))
            drawRect(m(Color(0xFF3FA7A0)), Offset(0f, h * 0.8f), Size(w, h * 0.2f))
            var x = 20 * d; while (x < w) { drawCircle(m(Color(0xFF8A8F93)), 1.3f * d, Offset(x, h * 0.22f)); x += 40 * d }
        }
        TemaId.ORDEN -> {
            val c = m(Color(0xFF3B3F45)); tabla(c, 0f, h * 0.6f)
            listOf(0.12f, 0.88f).forEach { fx -> drawPath(Path().apply { moveTo(w * fx - 10 * d, h * 0.6f); lineTo(w * fx + 10 * d, h * 0.6f); lineTo(w * fx, h); close() }, c) }
            var x = 10 * d; while (x < w) { drawCircle(m(Color(0xFFC9A44C)), 1.6f * d, Offset(x, h * 0.3f)); x += 26 * d }
        }
        TemaId.LABORATORIO -> {
            drawRect(Color(0xFF7FD8E8).copy(alpha = if (oscuro) 0.25f else 0.35f), Offset.Zero, Size(w, h * 0.55f))
            drawLine(Color.White.copy(alpha = 0.9f), Offset(0f, d), Offset(w, d), 1.5f * d)
            drawLine(m(Color(0xFF4FB3C4)), Offset(0f, h * 0.55f), Offset(w, h * 0.55f), d)
            listOf(0.15f, 0.85f).forEach { fx -> drawRect(m(Color(0xFFB8C2C8)), Offset(w * fx - 3 * d, h * 0.55f), Size(6 * d, h * 0.45f)) }
        }
        TemaId.COLONOS -> {
            val c = m(Color(0xFFB88A55)); tabla(c)
            for (k in 0 until 3) drawLine(lerp(c, Color.Black, 0.2f), Offset(0f, h * (0.3f + k * 0.2f)), Offset(w, h * (0.32f + k * 0.18f)), 0.7f * d)
            listOf(0.23f, 0.61f, 0.87f).forEach { fx -> drawOval(lerp(c, Color.Black, 0.3f), Offset(w * fx, h * 0.35f), Size(7 * d, h * 0.3f)) }
            listOf(0.05f, 0.5f, 0.95f).forEach { fx -> drawCircle(m(Color(0xFF55504A)), 1.4f * d, Offset(w * fx, h * 0.5f)) }
        }
        TemaId.NAPOLEON -> {
            val c = m(Color(0xFF5A1E14)); tabla(c); val oro = m(Color(0xFFD4AF37))
            drawRect(oro, Offset.Zero, Size(w, h * 0.16f)); drawRect(oro, Offset(0f, h * 0.84f), Size(w, h * 0.16f))
            var x = 40 * d; while (x < w) { drawCircle(oro, 2.5f * d, Offset(x, h * 0.5f)); drawCircle(c, 1.1f * d, Offset(x, h * 0.5f)); x += 80 * d }
        }
        TemaId.BOTANICO -> {
            tabla(m(Color(0xFF8A6A3E)))
            var x = 22 * d
            while (x < w - 30 * d) { drawRect(m(Color(0xFFF3EBD5)), Offset(x, h * 0.22f), Size(30 * d, h * 0.5f)); drawRect(m(Color(0xFFC9A44C)), Offset(x, h * 0.22f), Size(30 * d, h * 0.5f), style = Stroke(1.4f * d)); x += 100 * d }
        }
        TemaId.RUINAS -> {
            // Una tabla rescatada sobre dos bloques de hormigón, con musgo en el canto.
            listOf(0.1f, 0.84f).forEach { fx -> drawRect(m(Color(0xFF9C9C92)), Offset(w * fx, h * 0.45f), Size(w * 0.06f, h * 0.55f)) }
            tabla(m(Color(0xFF8A7458)), 0f, h * 0.45f)
            var x = 8 * d; var k = 0
            while (x < w) { drawCircle(m(Color(0xFF4F7A33)).copy(alpha = 0.7f), (1.2f + (k % 3) * 0.6f) * d, Offset(x, h * 0.04f)); x += (7 + (k * 13) % 19) * d; k++ }
        }
        TemaId.NAVIDAD -> {
            tabla(m(Color(0xFFEDE6DA)), 0f, h * 0.55f)
            val hoja = m(Color(0xFF2E6B3A)); val baya = m(Color(0xFFC62828)); var x = 0f; var k = 0
            while (x < w) {
                val cx = x + 9 * d; val cy = h * 0.72f
                drawPath(Path().apply {
                    moveTo(cx - 8 * d, cy); lineTo(cx - 5 * d, cy - 3 * d); lineTo(cx - 2 * d, cy - 1 * d); lineTo(cx, cy - 4 * d); lineTo(cx + 2 * d, cy - 1 * d); lineTo(cx + 5 * d, cy - 3 * d)
                    lineTo(cx + 8 * d, cy); lineTo(cx + 5 * d, cy + 3 * d); lineTo(cx + 2 * d, cy + 1 * d); lineTo(cx, cy + 4 * d); lineTo(cx - 2 * d, cy + 1 * d); lineTo(cx - 5 * d, cy + 3 * d); close()
                }, hoja)
                if (k % 2 == 0) { drawCircle(baya, 2 * d, Offset(x + 18 * d, h * 0.62f)); drawCircle(baya, 2 * d, Offset(x + 21 * d, h * 0.78f)) }
                x += 22 * d; k++
            }
        }
        TemaId.SEDA -> {
            val c = m(Color(0xFFC9B26A))
            listOf(0f, h * 0.5f).forEach { y0 -> drawRect(Brush.verticalGradient(listOf(lerp(c, Color.White, 0.3f), c, lerp(c, Color.Black, 0.3f)), y0, y0 + h * 0.5f), Offset(0f, y0), Size(w, h * 0.5f)) }
            var x = 30 * d; while (x < w) { drawLine(lerp(c, Color.Black, 0.35f), Offset(x, 0f), Offset(x, h), 1.5f * d); x += 55 * d }
            listOf(0.1f, 0.9f).forEach { fx -> for (j in 0 until 3) drawLine(m(Color(0xFF7A3B1E)), Offset(w * fx - 4 * d + j * 3 * d, 0f), Offset(w * fx - 4 * d + j * 3 * d, h), d) }
        }
        // La repisa del tokonoma: madera oscura pulida con su veta y el filo claro.
        TemaId.EDO -> {
            val c = m(Color(0xFF5A3A22))
            tabla(c)
            listOf(0.3f, 0.55f, 0.78f).forEach { f -> drawLine(lerp(c, Color.Black, 0.3f).copy(alpha = 0.6f), Offset(0f, h * f), Offset(w, h * f + 1.5f * d), 0.8f * d) }
            drawRect(m(Color(0xFFB08A5A)), Offset(0f, 0f), Size(w, 1.5f * d))
        }
        TemaId.GOTICO -> {
            val c = m(Color(0xFF6B6F7A)); tabla(c, 0f, h * 0.5f)
            val aw = 14 * d; var x = 0f
            while (x < w) { drawArc(lerp(c, Color.Black, 0.2f), 180f, 180f, false, Offset(x + 1 * d, h * 0.5f), Size(aw - 2 * d, aw), style = Stroke(1.5f * d)); x += aw }
        }
        // El estante metálico del archivo, pintado de verde, con los portaetiquetas y el óxido de los tornillos.
        TemaId.ARCANO -> {
            val c = m(Color(0xFF4F6158)); tabla(c, 0f, h * 0.6f)
            drawLine(lerp(c, Color.Black, 0.4f), Offset(0f, h * 0.6f), Offset(w, h * 0.6f), 1.2f * d)
            var x = 16 * d
            while (x < w - 24 * d) {
                drawRect(m(Color(0xFFE8DDC0)), Offset(x, h * 0.16f), Size(22 * d, h * 0.3f)); drawRect(lerp(c, Color.Black, 0.4f), Offset(x, h * 0.16f), Size(22 * d, h * 0.3f), style = Stroke(0.8f * d))
                drawCircle(m(Color(0xFF8C5A34)), 1.4f * d, Offset(x - 8 * d, h * 0.3f))
                x += 72 * d
            }
        }
        TemaId.ESPACIO -> {
            tabla(m(Color(0xFF262B33)))
            drawLine(Color(0xFF4FC3F7), Offset(0f, h * 0.2f), Offset(w, h * 0.2f), 1.2f * d)
            val luces = listOf(Color(0xFF66BB6A), Color(0xFFFFCA28), Color(0xFF4FC3F7), Color(0xFFEF5350)); var x = 16 * d; var k = 0
            while (x < w) { drawCircle(luces[k % 4], 1.6f * d, Offset(x, h * 0.6f)); x += 18 * d; k++ }
        }
        // Una viga del casco, de acero pintado, con su fila de remaches de latón.
        TemaId.ABISMO -> {
            tabla(m(Color(0xFF3E4A52)))
            drawLine(m(Color(0xFF1C252B)), Offset(0f, h * 0.18f), Offset(w, h * 0.18f), 1.2f * d)
            var x = 10 * d; while (x < w) { drawCircle(m(Color(0xFFB8913E)), 1.5f * d, Offset(x, h * 0.58f)); drawCircle(Color.White.copy(alpha = 0.3f), 0.6f * d, Offset(x - 0.5f * d, h * 0.58f - 0.5f * d)); x += 14 * d }
        }
        TemaId.ANDALUS -> {
            val c = m(Color(0xFF7A4A2A)); tabla(c, 0f, h * 0.6f)
            val t = 6 * d; var x = 0f; var k = 0
            while (x < w) { drawRect(if (k % 2 == 0) m(Color(0xFF2E7D5B)) else m(Color(0xFFF1E3C8)), Offset(x, h * 0.22f), Size(t, t)); x += t; k++ }
            x = 0f
            while (x < w) { drawPath(Path().apply { moveTo(x, h * 0.6f); lineTo(x + 5 * d, h); lineTo(x + 10 * d, h * 0.6f); close() }, lerp(c, Color.Black, 0.15f)); x += 10 * d }
        }
        // Una banca de piedra estucada con la franja de cinabrio y una fila de cuentas de jade.
        TemaId.MESO -> {
            val c = m(Color(0xFFE2D6BC)); tabla(c)
            drawRect(m(Color(0xFFA3301E)), Offset(0f, h * 0.12f), Size(w, h * 0.16f))
            var x = 6 * d; while (x < w) { drawCircle(m(Color(0xFF2F7D57)), h * 0.12f, Offset(x, h * 0.62f)); x += 14 * d }
        }
        TemaId.MONGOL -> {
            tabla(m(Color(0xFFC2551F))); val oro = m(Color(0xFFE0B040))
            drawRect(oro, Offset.Zero, Size(w, 1.5f * d)); drawRect(oro, Offset(0f, h - 1.5f * d), Size(w, 1.5f * d))
            drawPath(Path().apply { grecaRepisa(0f, h * 0.25f, h * 0.17f, w) }, m(Color(0xFF2E5C9A)), style = Stroke(1.6f * d))
        }
        TemaId.RENACIMIENTO -> {
            val c = m(Color(0xFF5B3B26)); tabla(c)
            val t = 7 * d; var x = 0f; var k = 0
            while (x < w) { drawRect(if (k % 2 == 0) m(Color(0xFFD8B98A)) else lerp(c, Color.Black, 0.2f), Offset(x, h * 0.35f), Size(t, t * 0.6f)); x += t; k++ }
        }
        // La repisa es una regala de barco: tablón de roble, sus cabillas de latón y un cabo que cuelga en guirnalda.
        TemaId.PIRATA -> {
            val c = m(Color(0xFF6B4A2B)); tabla(c)
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), d)
            var x = 18 * d; while (x < w) { drawCircle(m(Color(0xFFC9A04A)), 2.2f * d, Offset(x, h * 0.3f)); x += 48 * d }
            val cabo = m(Color(0xFFB59A62)); var x0 = 0f
            while (x0 < w) { drawPath(Path().apply { moveTo(x0, h * 0.72f); quadraticTo(x0 + 24 * d, h * 0.72f + 7 * d, x0 + 48 * d, h * 0.72f) }, cabo, style = Stroke(1.8f * d)); x0 += 48 * d }
        }
        TemaId.URSS -> {
            val c = m(Color(0xFFB3261E))
            drawRect(c, Offset.Zero, Size(w, h * 0.22f)); drawRect(c, Offset(0f, h * 0.78f), Size(w, h * 0.22f))
            val p = 14 * d; var x = 0f
            while (x < w) { drawLine(c, Offset(x, h * 0.22f), Offset(x + p, h * 0.78f), 1.6f * d); drawLine(c, Offset(x + p, h * 0.78f), Offset(x + 2 * p, h * 0.22f), 1.6f * d); x += 2 * p }
        }
        TemaId.REVOLUCION -> {
            tabla(m(Color(0xFF8B6B47)))
            listOf(Color(0xFF1F3A8A), Color.White, Color(0xFFC62828)).forEachIndexed { k, col -> drawRect(m(col), Offset(0f, h * (0.08f + k * 0.1f)), Size(w, h * 0.1f)) }
        }
        TemaId.ANTIGUAS -> {
            val c = m(Color(0xFF6E6A62))
            drawPath(Path().apply {
                moveTo(0f, h * 0.15f); var x = 0f; var k = 0
                while (x < w) { x += (14 + (k * 7) % 11) * d; lineTo(x, h * (if (k % 2 == 0) 0.05f else 0.2f)); k++ }
                lineTo(w, h); lineTo(0f, h); close()
            }, Brush.verticalGradient(listOf(lerp(c, Color.White, 0.15f), c, lerp(c, Color.Black, 0.35f)), 0f, h))
            val runa = lerp(c, Color.Black, 0.45f); var x = 40 * d
            while (x < w) {
                drawLine(runa, Offset(x, h * 0.35f), Offset(x, h * 0.8f), 1.3f * d)
                drawLine(runa, Offset(x, h * 0.45f), Offset(x + 4 * d, h * 0.3f), 1.3f * d); drawLine(runa, Offset(x, h * 0.6f), Offset(x + 4 * d, h * 0.45f), 1.3f * d)
                x += 70 * d
            }
        }
        TemaId.CUEVA -> {
            // Una cornisa de roca natural, de borde desparejo, con una hilera de puntos de ocre y el hollín de las lámparas.
            val c = m(Color(0xFFB59A78))
            drawPath(Path().apply {
                moveTo(0f, h * 0.2f); var x = 0f; var k = 0
                while (x < w) { x += (18 + (k * 11) % 13) * d; quadraticTo(x - 9 * d, h * (if (k % 2 == 0) 0.02f else 0.16f), x, h * (0.1f + (k % 3) * 0.05f)); k++ }
                lineTo(w, h); lineTo(0f, h); close()
            }, Brush.verticalGradient(listOf(lerp(c, Color.White, 0.15f), c, lerp(c, Color.Black, 0.4f)), 0f, h))
            var x = 26 * d; while (x < w) { for (j in 0 until 3) drawCircle(m(Color(0xFFA2401F)), 1.8f * d, Offset(x + j * 6 * d, h * 0.55f)); x += 84 * d }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.18f)), h * 0.75f, h), Offset(0f, h * 0.75f), Size(w, h * 0.25f))
        }
    }
}

/**
 * El fondo de la placa de cada trofeo: tabula ansata, corchetes de consola, frontón, rollo, pestaña de expediente, insignia
 * redondeada, chapa remachada, etiqueta de muestra, recorte de gaceta, cartela oval, etiqueta de herbario pautada, etiqueta
 * de regalo, sello rojo, arco apuntado, marcas de visor, arco lobulado, cinta azul de seda, cartellino, cuña roja, franja
 * tricolor y tablilla de piedra desportillada.
 */
private fun DrawScope.fondoPlaca(tema: TemaId, tinta: Color, acento: Color, hecho: Boolean) {
    val w = size.width; val h = size.height; val d = 1.dp.toPx()
    val f = if (hecho) 1f else 0.55f
    val fondo = tinta.copy(alpha = 0.05f * f); val borde = tinta.copy(alpha = 0.28f * f); val ac = acento.copy(alpha = 0.6f * f)
    val fino = Stroke(d)
    when (tema) {
        TemaId.ROMANO -> {
            val e = 6 * d
            val p = Path().apply {
                moveTo(e, 0f); lineTo(w - e, 0f); lineTo(w - e, h * 0.3f); lineTo(w, h * 0.15f); lineTo(w, h * 0.85f); lineTo(w - e, h * 0.7f); lineTo(w - e, h)
                lineTo(e, h); lineTo(e, h * 0.7f); lineTo(0f, h * 0.85f); lineTo(0f, h * 0.15f); lineTo(e, h * 0.3f); close()
            }
            drawPath(p, fondo); drawPath(p, borde, style = fino)
        }
        // La placa de la calle: mármol claro, marco doble y un tornillo a cada lado.
        TemaId.URBE -> {
            drawRect(Color(0xFFF4EEE2).copy(alpha = 0.35f * f), Offset.Zero, size)
            drawRect(borde, Offset(d, d), Size(w - 2 * d, h - 2 * d), style = Stroke(1.8f * d))
            drawRect(borde.copy(alpha = borde.alpha * 0.6f), Offset(4 * d, 4 * d), Size(w - 8 * d, h - 8 * d), style = fino)
            listOf(7 * d, w - 7 * d).forEach { x -> drawCircle(ac, 1.6f * d, Offset(x, h / 2)) }
        }
        TemaId.CIBER -> {
            val l = 7 * d
            listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, s) ->
                drawLine(ac, o, Offset(o.x + s.x * l, o.y), 1.5f * d); drawLine(ac, o, Offset(o.x, o.y + s.y * l), 1.5f * d)
            }
        }
        // La placa como un letrerito con su tubo alrededor, redondeado, y dos grapas.
        TemaId.NEON -> {
            drawRoundRect(ac.copy(alpha = ac.alpha * 0.3f), Offset(1.5f * d, 1.5f * d), Size(w - 3 * d, h - 3 * d), CornerRadius(h * 0.3f), style = Stroke(4 * d))
            drawRoundRect(ac, Offset(1.5f * d, 1.5f * d), Size(w - 3 * d, h - 3 * d), CornerRadius(h * 0.3f), style = Stroke(1.2f * d))
            listOf(0.25f, 0.75f).forEach { f -> drawRect(tinta.copy(alpha = 0.6f), Offset(w * f - 1.5f * d, 0f), Size(3 * d, 4 * d)) }
        }
        TemaId.HELENO -> {
            val t = 5 * d
            drawRect(fondo, Offset(0f, t), Size(w, h - t)); drawRect(borde, Offset(0f, t), Size(w, h - t), style = fino)
            drawPath(Path().apply { moveTo(0f, t); lineTo(w / 2, 0f); lineTo(w, t); close() }, borde, style = fino)
        }
        // La placa es un cartucho: redonda a la izquierda, con la barra del nudo a la derecha.
        TemaId.EGIPTO -> {
            val b = 4 * d
            drawRoundRect(fondo, Offset.Zero, Size(w - b, h), CornerRadius(h / 2)); drawRoundRect(borde, Offset.Zero, Size(w - b, h), CornerRadius(h / 2), style = fino)
            drawRect(ac, Offset(w - 2.5f * d, 1 * d), Size(2.5f * d, h - 2 * d))
        }
        TemaId.MEDIEVAL -> {
            val r = 4 * d
            drawRect(fondo, Offset(r, 0f), Size(w - 2 * r, h))
            drawLine(borde, Offset(r, 0f), Offset(w - r, 0f), d); drawLine(borde, Offset(r, h), Offset(w - r, h), d)
            listOf(r, w - r).forEach { x -> drawOval(fondo, Offset(x - r, 0f), Size(2 * r, h)); drawOval(borde, Offset(x - r, 0f), Size(2 * r, h), style = fino) }
        }
        // Una placa de hierro remachada con el borde de abajo en punta, como un gallardete.
        TemaId.TEMPLARIO -> {
            val p = 6 * d
            val placa = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - p); lineTo(w / 2, h); lineTo(0f, h - p); close() }
            drawPath(placa, fondo); drawPath(placa, borde, style = fino)
            listOf(Offset(4 * d, 4 * d), Offset(w - 4 * d, 4 * d)).forEach { drawCircle(borde, 1.4f * d, it) }
        }
        TemaId.DISTOPIA -> {
            val t = 6 * d
            drawRect(fondo, Offset(0f, t), Size(w, h - t)); drawRect(fondo, Offset.Zero, Size(w * 0.4f, t))
            drawPath(Path().apply { moveTo(0f, h); lineTo(0f, 0f); lineTo(w * 0.4f, 0f); lineTo(w * 0.4f + t, t); lineTo(w, t); lineTo(w, h); close() }, borde, style = fino)
            drawLine(Color(0xFFB71C1C).copy(alpha = 0.5f * f), Offset(0f, h - 2 * d), Offset(w, h - 2 * d), 2 * d)
        }
        TemaId.REFUGIO -> {
            drawRoundRect(fondo, Offset.Zero, size, CornerRadius(12 * d)); drawRoundRect(ac, Offset.Zero, size, CornerRadius(12 * d), style = Stroke(1.5f * d))
            drawRoundRect(borde, Offset(3 * d, 3 * d), Size(w - 6 * d, h - 6 * d), CornerRadius(9 * d), style = fino)
        }
        TemaId.ORDEN -> {
            val e = 5 * d
            val p = Path().apply { moveTo(e, 0f); lineTo(w - e, 0f); lineTo(w, e); lineTo(w, h - e); lineTo(w - e, h); lineTo(e, h); lineTo(0f, h - e); lineTo(0f, e); close() }
            drawPath(p, fondo); drawPath(p, borde, style = Stroke(1.5f * d))
            listOf(Offset(e + 2 * d, e + 2 * d), Offset(w - e - 2 * d, e + 2 * d), Offset(e + 2 * d, h - e - 2 * d), Offset(w - e - 2 * d, h - e - 2 * d)).forEach { drawCircle(ac, 1.6f * d, it) }
        }
        TemaId.LABORATORIO -> { drawRect(fondo, Offset.Zero, size); drawRect(borde, Offset.Zero, size, style = fino); drawRect(ac, Offset.Zero, Size(w, 3 * d)) }
        TemaId.COLONOS -> {
            val p = Path().apply {
                moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - 3 * d)
                var x = w; var k = 0
                while (x > 0f) { x -= 4 * d; lineTo(x.coerceAtLeast(0f), if (k % 2 == 0) h else h - 3 * d); k++ }
                close()
            }
            drawPath(p, fondo); drawPath(p, borde, style = fino)
        }
        TemaId.NAPOLEON -> {
            // Cartela de esquinas muy redondas (el óvalo entero dejaba el texto fuera) con un medallón oval arriba.
            val r = CornerRadius(minOf(16 * d, h / 2.5f))
            drawRoundRect(fondo, Offset.Zero, size, r); drawRoundRect(ac, Offset.Zero, size, r, style = Stroke(1.5f * d))
            drawRoundRect(borde, Offset(3 * d, 3 * d), Size(w - 6 * d, h - 6 * d), CornerRadius(r.x - 3 * d), style = fino)
            drawOval(ac, Offset(w / 2 - 5 * d, -2.5f * d), Size(10 * d, 6 * d))
        }
        TemaId.BOTANICO -> {
            drawRect(fondo, Offset.Zero, size); drawRect(borde, Offset.Zero, size, style = fino)
            var y = 14 * d; while (y < h) { drawLine(tinta.copy(alpha = 0.12f * f), Offset(4 * d, y), Offset(w - 4 * d, y), d); y += 12 * d }
        }
        TemaId.RUINAS -> {
            // Una placa de calle atornillada, con su filete y dos tornillos.
            drawRoundRect(fondo, Offset.Zero, size, CornerRadius(5 * d)); drawRoundRect(borde, Offset(2 * d, 2 * d), Size(w - 4 * d, h - 4 * d), CornerRadius(4 * d), style = fino)
            listOf(6 * d, w - 6 * d).forEach { x -> drawCircle(tinta.copy(alpha = 0.4f * f), 1.6f * d, Offset(x, h / 2)) }
        }
        TemaId.NAVIDAD -> {
            val pt = 8 * d
            val p = Path().apply { moveTo(pt, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(pt, h); lineTo(0f, h / 2); close() }
            drawPath(p, fondo); drawPath(p, borde, style = fino); drawCircle(ac, 2 * d, Offset(pt, h / 2), style = fino)
        }
        TemaId.SEDA -> {
            drawRect(fondo, Offset.Zero, size); drawRect(borde, Offset.Zero, size, style = fino)
            val s = 10 * d
            drawRect(Color(0xFFC0392B).copy(alpha = 0.55f * f), Offset(w - s - 2 * d, 2 * d), Size(s, s))
            drawRect(Color.White.copy(alpha = 0.6f * f), Offset(w - s + 1 * d, 5 * d), Size(s - 6 * d, s - 6 * d), style = fino)
        }
        // Una ema: la tablilla de madera con las esquinas de arriba en chaflán y el cordón rojo para colgarla.
        TemaId.EDO -> {
            val k = 8 * d
            val tabla = Path().apply { moveTo(0f, k); lineTo(k, 0f); lineTo(w - k, 0f); lineTo(w, k); lineTo(w, h); lineTo(0f, h); close() }
            drawPath(tabla, fondo); drawPath(tabla, borde, style = fino)
            drawLine(Color(0xFFC9442E).copy(alpha = 0.7f * f), Offset(w / 2 - 7 * d, 0f), Offset(w / 2, 5 * d), 1.2f * d)
            drawLine(Color(0xFFC9442E).copy(alpha = 0.7f * f), Offset(w / 2 + 7 * d, 0f), Offset(w / 2, 5 * d), 1.2f * d)
            drawCircle(ac, 1.4f * d, Offset(w / 2, 5.5f * d))
        }
        TemaId.GOTICO -> {
            val t = 10 * d
            val p = Path().apply { moveTo(0f, h); lineTo(0f, t); quadraticTo(0f, t * 0.2f, w / 2, 0f); quadraticTo(w, t * 0.2f, w, t); lineTo(w, h); close() }
            drawPath(p, fondo); drawPath(p, borde, style = fino)
        }
        // La ficha de catálogo: la esquina de arriba doblada y el agujero del fichero abajo.
        TemaId.ARCANO -> {
            val e = 9 * d
            val p = Path().apply { moveTo(0f, 0f); lineTo(w - e, 0f); lineTo(w, e); lineTo(w, h); lineTo(0f, h); close() }
            drawPath(p, fondo); drawPath(p, borde, style = fino)
            drawPath(Path().apply { moveTo(w - e, 0f); lineTo(w - e, e); lineTo(w, e) }, borde, style = fino)
            drawCircle(ac, 2.4f * d, Offset(w / 2, h - 5 * d), style = fino)
        }
        TemaId.ESPACIO -> {
            val l = 6 * d
            listOf(Offset(0f, 0f), Offset(w - l, 0f), Offset(0f, h - 1.5f * d), Offset(w - l, h - 1.5f * d)).forEach { drawRect(ac, it, Size(l, 1.5f * d)) }
            drawLine(borde, Offset(0f, h / 2), Offset(3 * d, h / 2), d); drawLine(borde, Offset(w - 3 * d, h / 2), Offset(w, h / 2), d)
            drawLine(borde, Offset(l + 2 * d, h - d), Offset(w - l - 2 * d, h - d), d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 3 * d)))
        }
        // Una placa de latón atornillada: un perno en cada esquina y la ola grabada abajo.
        TemaId.ABISMO -> {
            drawRoundRect(fondo, Offset.Zero, Size(w, h), CornerRadius(4 * d))
            drawRoundRect(borde, Offset(d / 2, d / 2), Size(w - d, h - d), CornerRadius(4 * d), style = fino)
            listOf(Offset(4 * d, 4 * d), Offset(w - 4 * d, 4 * d), Offset(4 * d, h - 4 * d), Offset(w - 4 * d, h - 4 * d)).forEach { drawCircle(ac, 1.6f * d, it) }
            val p = Path(); var x = 10 * d
            p.moveTo(x, h - 3 * d)
            while (x <= w - 10 * d) { p.lineTo(x, h - 3 * d + 1.2f * d * kotlin.math.sin(x / (3 * d))); x += 1.5f * d }
            drawPath(p, ac, style = fino)
        }
        TemaId.ANDALUS -> {
            val t = 6 * d
            val p = Path().apply {
                moveTo(0f, h); lineTo(0f, t)
                for (k in 0 until 3) arcTo(Rect(k * w / 3, 0f, (k + 1) * w / 3, 2 * t), 180f, 180f, false)
                lineTo(w, h); close()
            }
            drawPath(p, fondo); drawPath(p, borde, style = fino)
        }
        // La placa con la bóveda en saledizo arriba: los lados suben en dos gradas hasta la losa.
        TemaId.MESO -> {
            val g = 4 * d
            val p = Path().apply {
                moveTo(0f, h); lineTo(0f, 2 * g); lineTo(g, 2 * g); lineTo(g, g); lineTo(2 * g, g); lineTo(2 * g, 0f)
                lineTo(w - 2 * g, 0f); lineTo(w - 2 * g, g); lineTo(w - g, g); lineTo(w - g, 2 * g); lineTo(w, 2 * g); lineTo(w, h); close()
            }
            drawPath(p, fondo); drawPath(p, borde, style = fino)
            drawLine(ac, Offset(3 * g, g * 0.6f), Offset(w - 3 * g, g * 0.6f), d)
        }
        TemaId.MONGOL -> {
            val e = 6 * d; val azul = Color(0xFF4FA3E0)
            val p = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - e, h / 2); lineTo(w, h); lineTo(0f, h); lineTo(e, h / 2); close() }
            drawPath(p, azul.copy(alpha = 0.16f * f)); drawPath(p, azul.copy(alpha = 0.5f * f), style = fino)
        }
        TemaId.RENACIMIENTO -> {
            val pl = 9 * d
            val p = Path().apply { moveTo(0f, 0f); lineTo(w - pl, 0f); lineTo(w, pl); lineTo(w, h); lineTo(0f, h); close() }
            drawPath(p, Color(0xFFF3E3C6).copy(alpha = 0.5f * f)); drawPath(p, borde, style = fino)
            drawPath(Path().apply { moveTo(w - pl, 0f); lineTo(w - pl, pl); lineTo(w, pl); close() }, tinta.copy(alpha = 0.18f * f))
        }
        // Una tablilla de pergamino clavada con cuatro clavos, con la esquina rota.
        TemaId.PIRATA -> {
            val pl = 7 * d
            val p = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - pl); lineTo(w - pl * 0.6f, h - pl * 0.4f); lineTo(w - pl, h); lineTo(0f, h); close() }
            drawPath(p, Color(0xFFE6DCBA).copy(alpha = 0.55f * f)); drawPath(p, borde, style = fino)
            listOf(Offset(4 * d, 4 * d), Offset(w - 4 * d, 4 * d), Offset(4 * d, h - 4 * d)).forEach { q -> drawCircle(ac, 1.6f * d, q) }
        }
        TemaId.URSS -> {
            val t = 12 * d
            drawRect(fondo, Offset.Zero, size)
            drawPath(Path().apply { moveTo(0f, 0f); lineTo(t, 0f); lineTo(0f, t); close() }, Color(0xFFC62828).copy(alpha = 0.75f * f))
            drawRect(tinta.copy(alpha = 0.6f * f), Offset(0f, h - 3 * d), Size(w, 3 * d))
        }
        TemaId.REVOLUCION -> {
            drawRect(fondo, Offset.Zero, size); drawRect(borde, Offset.Zero, size, style = fino)
            listOf(Color(0xFF1F3A8A), Color.White, Color(0xFFC62828)).forEachIndexed { k, col -> drawRect(col.copy(alpha = 0.7f * f), Offset(k * 2 * d, 0f), Size(2 * d, h)) }
        }
        TemaId.ANTIGUAS -> {
            val e = 4 * d
            val p = Path().apply { moveTo(e, 0f); lineTo(w - 2 * e, 0f); lineTo(w, e * 1.5f); lineTo(w - e * 0.5f, h - e); lineTo(w - e * 1.5f, h); lineTo(e * 0.8f, h); lineTo(0f, h - e * 2); lineTo(e * 0.4f, e); close() }
            drawPath(p, tinta.copy(alpha = 0.09f * f)); drawPath(p, borde, style = Stroke(1.3f * d))
            drawLine(ac, Offset(w * 0.35f, h - 2.5f * d), Offset(w * 0.65f, h - 2.5f * d), d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 1.5f * d, 4 * d, 1.5f * d)))
        }
        TemaId.CUEVA -> {
            // Un retazo de piel: el borde ondulado, la costura de tendón y una tira de puntos de ocre abajo.
            val p = Path().apply {
                moveTo(3 * d, 2 * d); quadraticTo(w * 0.5f, -2 * d, w - 3 * d, 3 * d); quadraticTo(w + 1 * d, h * 0.5f, w - 2 * d, h - 3 * d)
                quadraticTo(w * 0.5f, h + 2 * d, 2 * d, h - 2 * d); quadraticTo(-1 * d, h * 0.5f, 3 * d, 2 * d); close()
            }
            drawPath(p, tinta.copy(alpha = 0.08f * f)); drawPath(p, borde, style = Stroke(1.2f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 2 * d))))
            for (k in 0 until 5) drawCircle(ac, 1.4f * d, Offset(w * 0.4f + k * 5 * d, h - 4 * d))
        }
    }
}
