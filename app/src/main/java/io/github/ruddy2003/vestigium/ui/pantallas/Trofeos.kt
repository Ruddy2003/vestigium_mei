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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TrofeosTema
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Los metales de cada nivel: oscuro, base y brillo. Bronce, plata, oro y cristal (legendario). */
private val METAL = listOf(
    Triple(Color(0xFF6E4423), Color(0xFFB27A45), Color(0xFFE9BE8F)),
    Triple(Color(0xFF6F7781), Color(0xFFBFC6CF), Color(0xFFF4F7FA)),
    Triple(Color(0xFF8E6812), Color(0xFFE3B748), Color(0xFFFFEBA6)),
    Triple(Color(0xFF237E95), Color(0xFF7FD8E8), Color(0xFFE9FCFF)),
)
val NIVELES = listOf("Bronce", "Plata", "Oro", "Legendario")

/** La figura de cada categoría. */
private fun figura(categoria: String) = when (categoria) {
    "Diario" -> "pluma"; "Mundo" -> "globo"; "Cuerpo" -> "antorcha"; "Mente" -> "lampara"; "Cultura" -> "libro"
    "Gente" -> "corazon"; "Curiosos" -> "estrella"; else -> "copa"
}

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
 * Un trofeo: pedestal de dos escalones con su placa y, encima, la figura de su categoría (pluma para el diario, globo para
 * el mundo, antorcha para el cuerpo, lámpara para la mente, libro para la cultura, corazón para la gente, estrella para
 * los curiosos y copa para la constancia y los tuyos), en el metal de su nivel. Si falta, es una silueta.
 */
@Composable
fun TrofeoLogro(l: Logros.Logro, alto: Dp, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val secreto = l.secreto && !l.hecho
    val pedestal = if (oscuro) Color(0xFF2B2622) else Color(0xFF4A3A2C)
    Box(modifier.size(width = alto * 0.82f, height = alto), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            if (tema in TrofeosTema.TEMAS) dibujarTrofeoTema(tema, l.categoria, l.nivel.coerceIn(1, 4), l.hecho, pedestal, cs.onSurface)
            else dibujarTrofeo(figura(l.categoria), (l.nivel - 1).coerceIn(0, 3), l.hecho, pedestal, cs.onSurface)
        }
        if (secreto) Text("?", fontSize = (alto.value * 0.26f).sp, color = cs.onSurfaceVariant.copy(alpha = 0.7f), fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = alto * 0.3f))
    }
}

/** El trofeo de los temas históricos: la obra o el objeto de su categoría (ver [TrofeosTema]) sobre el pedestal del tema. */
internal fun DrawScope.dibujarTrofeoTema(tema: TemaId, categoria: String, nivel: Int, hecho: Boolean, colorPedestal: Color, tinta: Color) {
    val (oscuro, base, brillo) = METAL[nivel - 1]
    val p = TrofeosTema.Pincel(size.width, size.height, hecho, nivel, oscuro, base, brillo, tinta.copy(alpha = 0.13f))
    with(TrofeosTema) { pedestal(tema, p, colorPedestal); figura(tema, categoria, p) }
    destellos(nivel - 1, hecho)
}

/** Los legendarios brillan. */
private fun DrawScope.destellos(nivel: Int, hecho: Boolean) {
    val w = size.width; val h = size.height
    if (hecho && nivel == 3) listOf(Offset(w * 0.14f, h * 0.12f), Offset(w * 0.88f, h * 0.22f), Offset(w * 0.2f, h * 0.5f), Offset(w * 0.84f, h * 0.6f)).forEachIndexed { i, o ->
        val s = w * (if (i % 2 == 0) 0.05f else 0.035f)
        drawPath(Path().apply { moveTo(o.x, o.y - s); lineTo(o.x + s * 0.25f, o.y - s * 0.25f); lineTo(o.x + s, o.y); lineTo(o.x + s * 0.25f, o.y + s * 0.25f)
            lineTo(o.x, o.y + s); lineTo(o.x - s * 0.25f, o.y + s * 0.25f); lineTo(o.x - s, o.y); lineTo(o.x - s * 0.25f, o.y - s * 0.25f); close() }, Color.White.copy(alpha = 0.9f))
    }
}

private fun DrawScope.dibujarTrofeo(fig: String, nivel: Int, hecho: Boolean, pedestal: Color, tinta: Color) {
    val w = size.width; val h = size.height
    val (oscuro, base, brillo) = METAL[nivel]
    val sombra = tinta.copy(alpha = 0.13f)
    fun metal(x0: Float, x1: Float): Brush = if (hecho) Brush.horizontalGradient(listOf(oscuro, base, brillo, base, oscuro), x0, x1) else SolidColor(sombra)
    val brochaFig = metal(w * 0.18f, w * 0.82f)
    // Pedestal: dos escalones de madera o piedra oscura, con una placa de metal.
    val ped = if (hecho) pedestal else tinta.copy(alpha = 0.08f)
    drawRoundRect(Color.Black.copy(alpha = if (hecho) 0.18f else 0.05f), Offset(w * 0.14f, h * 0.955f), Size(w * 0.72f, h * 0.04f), CornerRadius(h * 0.02f))
    drawRect(ped, Offset(w * 0.17f, h * 0.84f), Size(w * 0.66f, h * 0.12f))
    drawRect(Brush.verticalGradient(listOf(lerp(ped, Color.White, 0.12f), ped), h * 0.77f, h * 0.84f), Offset(w * 0.25f, h * 0.77f), Size(w * 0.5f, h * 0.075f))
    drawRect(metal(w * 0.33f, w * 0.67f), Offset(w * 0.33f, h * 0.865f), Size(w * 0.34f, h * 0.06f))
    when (fig) {
        "globo" -> {
            val c = Offset(w * 0.5f, h * 0.36f); val r = w * 0.27f
            drawCircle(if (hecho) Brush.radialGradient(listOf(brillo, base, oscuro), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.5f) else SolidColor(sombra), r, c)
            if (hecho) {
                val linea = oscuro.copy(alpha = 0.55f)
                drawOval(linea, Offset(c.x - r * 0.45f, c.y - r), Size(r * 0.9f, r * 2), style = Stroke(w * 0.012f))
                drawLine(linea, Offset(c.x - r, c.y), Offset(c.x + r, c.y), w * 0.012f)
                drawLine(linea, Offset(c.x - r * 0.87f, c.y - r * 0.5f), Offset(c.x + r * 0.87f, c.y - r * 0.5f), w * 0.01f)
                drawLine(linea, Offset(c.x - r * 0.87f, c.y + r * 0.5f), Offset(c.x + r * 0.87f, c.y + r * 0.5f), w * 0.01f)
            }
            // El arco que lo sostiene, el eje y el pie.
            drawArc(brochaFig, 40f, 220f, false, Offset(c.x - r * 1.18f, c.y - r * 1.18f), Size(r * 2.36f, r * 2.36f), style = Stroke(w * 0.035f, cap = StrokeCap.Round))
            drawRect(brochaFig, Offset(w * 0.47f, c.y + r * 1.1f), Size(w * 0.06f, h * 0.77f - (c.y + r * 1.1f)))
            drawOval(brochaFig, Offset(w * 0.34f, h * 0.73f), Size(w * 0.32f, h * 0.05f))
        }
        "pluma" -> {
            // El tintero.
            drawRoundRect(brochaFig, Offset(w * 0.32f, h * 0.58f), Size(w * 0.36f, h * 0.19f), CornerRadius(w * 0.05f))
            drawRoundRect(brochaFig, Offset(w * 0.42f, h * 0.53f), Size(w * 0.16f, h * 0.06f), CornerRadius(w * 0.02f))
            // La pluma, curva, saliendo del tintero.
            val p = Path().apply {
                moveTo(w * 0.5f, h * 0.56f)
                cubicTo(w * 0.52f, h * 0.40f, w * 0.62f, h * 0.18f, w * 0.82f, h * 0.05f)
                cubicTo(w * 0.76f, h * 0.22f, w * 0.66f, h * 0.42f, w * 0.52f, h * 0.56f)
                close()
            }
            drawPath(p, metal(w * 0.45f, w * 0.85f))
            drawPath(Path().apply { moveTo(w * 0.5f, h * 0.56f); cubicTo(w * 0.56f, h * 0.38f, w * 0.66f, h * 0.2f, w * 0.8f, h * 0.07f) },
                if (hecho) oscuro.copy(alpha = 0.6f) else sombra, style = Stroke(w * 0.012f))
            drawPath(Path().apply {
                moveTo(w * 0.52f, h * 0.48f); cubicTo(w * 0.40f, h * 0.36f, w * 0.44f, h * 0.20f, w * 0.62f, h * 0.10f)
                cubicTo(w * 0.56f, h * 0.24f, w * 0.56f, h * 0.36f, w * 0.55f, h * 0.46f); close()
            }, metal(w * 0.4f, w * 0.65f))
        }
        "antorcha" -> {
            // Mango, copa y llama.
            drawPath(Path().apply { moveTo(w * 0.43f, h * 0.36f); lineTo(w * 0.57f, h * 0.36f); lineTo(w * 0.53f, h * 0.77f); lineTo(w * 0.47f, h * 0.77f); close() }, brochaFig)
            drawPath(Path().apply { moveTo(w * 0.32f, h * 0.27f); lineTo(w * 0.68f, h * 0.27f); lineTo(w * 0.58f, h * 0.38f); lineTo(w * 0.42f, h * 0.38f); close() }, brochaFig)
            val llama = Path().apply {
                moveTo(w * 0.5f, h * 0.02f)
                cubicTo(w * 0.62f, h * 0.10f, w * 0.70f, h * 0.20f, w * 0.62f, h * 0.27f)
                lineTo(w * 0.38f, h * 0.27f)
                cubicTo(w * 0.30f, h * 0.20f, w * 0.40f, h * 0.12f, w * 0.46f, h * 0.06f)
                cubicTo(w * 0.46f, h * 0.12f, w * 0.50f, h * 0.15f, w * 0.52f, h * 0.15f)
                cubicTo(w * 0.54f, h * 0.10f, w * 0.52f, h * 0.06f, w * 0.5f, h * 0.02f); close()
            }
            drawPath(llama, if (hecho) Brush.verticalGradient(listOf(Color(0xFFFFE27A), Color(0xFFFF9A2E), Color(0xFFE2522B)), 0f, h * 0.27f) else SolidColor(sombra))
        }
        "lampara" -> {
            // Una lámpara de aceite con su flama.
            drawOval(brochaFig, Offset(w * 0.2f, h * 0.44f), Size(w * 0.6f, h * 0.2f))
            drawPath(Path().apply { moveTo(w * 0.72f, h * 0.50f); lineTo(w * 0.92f, h * 0.44f); lineTo(w * 0.90f, h * 0.50f); lineTo(w * 0.74f, h * 0.58f); close() }, brochaFig)
            drawArc(brochaFig, 90f, 180f, false, Offset(w * 0.08f, h * 0.45f), Size(w * 0.18f, h * 0.16f), style = Stroke(w * 0.04f))
            drawOval(brochaFig, Offset(w * 0.38f, h * 0.39f), Size(w * 0.24f, h * 0.07f))
            drawRect(brochaFig, Offset(w * 0.45f, h * 0.62f), Size(w * 0.1f, h * 0.1f))
            drawOval(brochaFig, Offset(w * 0.33f, h * 0.71f), Size(w * 0.34f, h * 0.06f))
            val f = Path().apply { moveTo(w * 0.9f, h * 0.26f); cubicTo(w * 0.98f, h * 0.36f, w * 0.96f, h * 0.43f, w * 0.91f, h * 0.44f); cubicTo(w * 0.85f, h * 0.43f, w * 0.83f, h * 0.36f, w * 0.9f, h * 0.26f); close() }
            drawPath(f, if (hecho) Brush.verticalGradient(listOf(Color(0xFFFFE27A), Color(0xFFFF8A2E)), h * 0.26f, h * 0.44f) else SolidColor(sombra))
        }
        "libro" -> {
            // Un libro abierto sobre un atril.
            val izq = Path().apply { moveTo(w * 0.5f, h * 0.30f); cubicTo(w * 0.38f, h * 0.24f, w * 0.22f, h * 0.24f, w * 0.12f, h * 0.28f); lineTo(w * 0.12f, h * 0.56f); cubicTo(w * 0.24f, h * 0.52f, w * 0.38f, h * 0.53f, w * 0.5f, h * 0.60f); close() }
            val der = Path().apply { moveTo(w * 0.5f, h * 0.30f); cubicTo(w * 0.62f, h * 0.24f, w * 0.78f, h * 0.24f, w * 0.88f, h * 0.28f); lineTo(w * 0.88f, h * 0.56f); cubicTo(w * 0.76f, h * 0.52f, w * 0.62f, h * 0.53f, w * 0.5f, h * 0.60f); close() }
            drawPath(izq, metal(w * 0.1f, w * 0.5f)); drawPath(der, metal(w * 0.5f, w * 0.9f))
            if (hecho) for (k in 1..4) {
                val y = h * (0.32f + k * 0.045f)
                drawLine(oscuro.copy(alpha = 0.45f), Offset(w * 0.18f, y), Offset(w * 0.44f, y + h * 0.02f), w * 0.01f)
                drawLine(oscuro.copy(alpha = 0.45f), Offset(w * 0.56f, y + h * 0.02f), Offset(w * 0.82f, y), w * 0.01f)
            }
            drawRect(brochaFig, Offset(w * 0.47f, h * 0.6f), Size(w * 0.06f, h * 0.13f))
            drawOval(brochaFig, Offset(w * 0.34f, h * 0.71f), Size(w * 0.32f, h * 0.06f))
        }
        "corazon" -> {
            val p = Path().apply {
                moveTo(w * 0.5f, h * 0.60f)
                cubicTo(w * 0.18f, h * 0.42f, w * 0.12f, h * 0.22f, w * 0.30f, h * 0.14f)
                cubicTo(w * 0.40f, h * 0.10f, w * 0.48f, h * 0.16f, w * 0.5f, h * 0.24f)
                cubicTo(w * 0.52f, h * 0.16f, w * 0.60f, h * 0.10f, w * 0.70f, h * 0.14f)
                cubicTo(w * 0.88f, h * 0.22f, w * 0.82f, h * 0.42f, w * 0.5f, h * 0.60f); close()
            }
            drawPath(p, brochaFig)
            drawRect(brochaFig, Offset(w * 0.47f, h * 0.6f), Size(w * 0.06f, h * 0.13f))
            drawOval(brochaFig, Offset(w * 0.34f, h * 0.71f), Size(w * 0.32f, h * 0.06f))
        }
        "estrella" -> {
            val c = Offset(w * 0.5f, h * 0.32f); val R = w * 0.32f; val r = R * 0.45f
            val p = Path()
            for (k in 0 until 10) {
                val a = -PI / 2 + k * PI / 5
                val rr = if (k % 2 == 0) R else r
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (k == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close()
            drawPath(p, brochaFig)
            drawRect(brochaFig, Offset(w * 0.47f, h * 0.56f), Size(w * 0.06f, h * 0.17f))
            drawOval(brochaFig, Offset(w * 0.34f, h * 0.71f), Size(w * 0.32f, h * 0.06f))
        }
        else -> {
            // La copa con dos asas.
            val copa = Path().apply {
                moveTo(w * 0.24f, h * 0.10f); lineTo(w * 0.76f, h * 0.10f)
                cubicTo(w * 0.76f, h * 0.40f, w * 0.64f, h * 0.54f, w * 0.5f, h * 0.56f)
                cubicTo(w * 0.36f, h * 0.54f, w * 0.24f, h * 0.40f, w * 0.24f, h * 0.10f); close()
            }
            drawArc(brochaFig, 90f, 180f, false, Offset(w * 0.10f, h * 0.14f), Size(w * 0.22f, h * 0.22f), style = Stroke(w * 0.045f))
            drawArc(brochaFig, -90f, 180f, false, Offset(w * 0.68f, h * 0.14f), Size(w * 0.22f, h * 0.22f), style = Stroke(w * 0.045f))
            drawPath(copa, brochaFig)
            if (hecho) drawOval(lerp(brillo, Color.White, 0.3f), Offset(w * 0.25f, h * 0.085f), Size(w * 0.5f, h * 0.045f))
            drawRect(brochaFig, Offset(w * 0.46f, h * 0.56f), Size(w * 0.08f, h * 0.12f))
            drawPath(Path().apply { moveTo(w * 0.36f, h * 0.77f); lineTo(w * 0.64f, h * 0.77f); lineTo(w * 0.58f, h * 0.68f); lineTo(w * 0.42f, h * 0.68f); close() }, brochaFig)
        }
    }
    destellos(nivel, hecho)
}

/** La placa debajo de la repisa: nombre, lo que lograste y cuándo (o cuánto falta). */
@Composable
private fun Placa(l: Logros.Logro, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val secreto = l.secreto && !l.hecho
    Column(modifier.padding(horizontal = 3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
                LinearProgressIndicator(progress = { l.fraccion }, modifier = Modifier.fillMaxWidth(0.8f).padding(top = 3.dp).height(3.dp))
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
    val pared = lerp(cs.surface, if (oscuro) Color.Black else cs.primary, if (oscuro) 0.25f else 0.05f)
    val madera = if (oscuro) Color(0xFF3A2E25) else Color(0xFF7A5A3E)
    Surface(shape = MaterialTheme.shapes.medium, color = pared, border = BorderStroke(1.dp, cs.outline), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Column(Modifier.padding(horizontal = 6.dp, vertical = 10.dp)) {
            if (titulo != null) Text(titulo.uppercase(), style = MaterialTheme.typography.labelLarge, color = cs.primary, letterSpacing = 2.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
            lista.chunked(porFila).forEach { fila ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    fila.forEach { l -> Box(Modifier.weight(1f).clickable { abrir(l) }, contentAlignment = Alignment.BottomCenter) { TrofeoLogro(l, 96.dp) } }
                    repeat(porFila - fila.size) { Spacer(Modifier.weight(1f)) }
                }
                // La repisa, con su canto y su sombra.
                Box(Modifier.fillMaxWidth().height(11.dp).background(Brush.verticalGradient(listOf(lerp(madera, Color.White, 0.18f), madera, lerp(madera, Color.Black, 0.35f)))))
                Box(Modifier.fillMaxWidth().height(7.dp).background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent))))
                Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.Top) {
                    fila.forEach { l -> Placa(l, Modifier.weight(1f).clickable { abrir(l) }) }
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
    AlertDialog(onDismissRequest = cerrar, confirmButton = { TextButton(onClick = cerrar) { Text("cerrar") } },
        dismissButton = {
            if (l.hecho) {
                val enHall = l.id in Repo.config.hall_logros
                TextButton(onClick = {
                    Repo.cambiarConfig { c -> c.copy(hall_logros = if (enHall) c.hall_logros - l.id else (c.hall_logros + l.id).takeLast(12)) }
                }) { Text(if (enHall) "quitar del hall" else "ponerlo en el hall") }
            } else if (l.propio) TextButton(onClick = {
                Repo.cambiarConfig { c -> c.copy(logros_propios = c.logros_propios.filterNot { "p_" + it.id == l.id }) }; cerrar()
            }) { Text("borrar este logro") }
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
                        LinearProgressIndicator(progress = { l.fraccion }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(6.dp))
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
