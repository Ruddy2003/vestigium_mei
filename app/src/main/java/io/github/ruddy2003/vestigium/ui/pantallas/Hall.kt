@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Logros
import io.github.ruddy2003.vestigium.ciencia.TusNumeros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.LibroDiario
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.SalonesTema
import io.github.ruddy2003.vestigium.ui.signoEgipcio
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Los metales de cada nivel en el hall: bronce, plata, oro y cristal. Cada tema los tiñe con su matiz (ver [metalHall]). */
internal val METALES_HALL = listOf(Color(0xFFB27A45), Color(0xFFBFC6CF), Color(0xFFE3B748), Color(0xFF9AD8E6))

/** El matiz de cada tema con el que se tiñen los metales del hall. */
private fun matizHall(t: TemaId): Color = when (t) {
    TemaId.ROMANO -> Color(0xFF9C2F3F)
    TemaId.CIBER -> Color(0xFF00E5FF)
    TemaId.NEON -> Color(0xFFFF3D9A)
    TemaId.HELENO -> Color(0xFF1E5F8C)
    TemaId.EGIPTO -> Color(0xFFC9971C)
    TemaId.MEDIEVAL -> Color(0xFF7A5C2E)
    TemaId.TEMPLARIO -> Color(0xFF5E6870)
    TemaId.DISTOPIA -> Color(0xFF5D6670)
    TemaId.REFUGIO -> Color(0xFF3FA7A0)
    TemaId.ORDEN -> Color(0xFFA07D2E)
    TemaId.LABORATORIO -> Color(0xFF00ACC1)
    TemaId.COLONOS -> Color(0xFF2F4A6E)
    TemaId.NAPOLEON -> Color(0xFF1F3A8A)
    TemaId.BOTANICO -> Color(0xFFA0522D)
    TemaId.NAVIDAD -> Color(0xFF1B5E20)
    TemaId.SEDA -> Color(0xFFC0392B)
    TemaId.GOTICO -> Color(0xFF1A3A8F)
    TemaId.ESPACIO -> Color(0xFF7B1FA2)
    TemaId.ABISMO -> Color(0xFF0F8A86)          // el verde azul del agua honda
    TemaId.ANDALUS -> Color(0xFF1F7A6B)
    TemaId.MONGOL -> Color(0xFF1E88E5)
    TemaId.RENACIMIENTO -> Color(0xFFB5562F)
    TemaId.URSS -> Color(0xFFD32F2F)
    TemaId.REVOLUCION -> Color(0xFFF2EFE6)
    TemaId.ANTIGUAS -> Color(0xFF8E7CC3)
}

/** El metal de un nivel (1 a 4) en el hall del tema: conserva bronce, plata, oro o cristal, teñido con el matiz del tema. */
internal fun metalHall(tema: TemaId, nivel: Int): Color = lerp(METALES_HALL[(nivel - 1).coerceIn(0, 3)], matizHall(tema), 0.28f)

/**
 * Cada logro, dibujado según lo que celebra: un horizonte de ciudad, un sello de pasaporte con bandera, un globo,
 * un monumento del patrimonio, una brújula, huellas, una pesa, una montaña, un libro, una luna, una carta…
 * El borde de la medalla, los techos de la ciudad, la forma del sello, el monumento, el estandarte, la moneda, el libro y
 * la carta son propios de cada tema; las demás piezas toman el trazo y el material del tema (ver [trazoTema]).
 */
object PiezasLogro {
    private fun pre(id: String) = id.substringBeforeLast('_')

    fun DrawScope.pieza(l: Logros.Logro, tema: TemaId, cs: ColorScheme) {
        val c = Offset(size.width / 2, size.height / 2)
        val r = size.minDimension / 2
        val u = r / 50f
        val metal = metalHall(tema, l.nivel)
        val hecho = l.hecho
        val tinta = if (hecho) Color(0xFF2A1E0E) else cs.onSurfaceVariant.copy(alpha = 0.6f)
        val luz = if (hecho) metal else cs.surfaceVariant
        val tz = trazoTema(tema, u).let { if (hecho) it else it.conMaterial(cs.surface.copy(alpha = 0.6f)) }
        // El marco: un disco con el metal del nivel (teñido con el matiz del tema) y el borde propio del tema.
        drawCircle(Brush.radialGradient(listOf(luz, luz.copy(alpha = 0.75f), luz.copy(alpha = 0.5f)), c, r), r)
        borde(tema, c, r, u, metal, tinta, cs, hecho)
        drawCircle(metal, r, c, style = Stroke(3 * u))
        if (l.nivel >= 4 && hecho) for (i in 0 until 16) { val a = i * PI / 8; drawLine(metal, Offset(c.x + (r * 1.02f * cos(a)).toFloat(), c.y + (r * 1.02f * sin(a)).toFloat()), Offset(c.x + (r * 1.12f * cos(a)).toFloat(), c.y + (r * 1.12f * sin(a)).toFloat()), 2 * u) }
        if (l.secreto && !hecho) return
        val p = pre(l.id)
        val tono = if (hecho) cs.primary else tinta
        when {
            l.id.startsWith("ciudades") -> ciudad(c, u, tema, tinta, tono, l.nivel, tz)
            l.id.startsWith("paises") -> sello(c, u, tema, tinta, cs, tz)
            l.id.startsWith("continentes") -> globo(c, u, tinta, tono, tz)
            l.id.startsWith("unesco") -> templo(c, u, tema, tinta, tz)
            l.id.startsWith("lejos") -> brujula(c, u, tinta, tono, tz)
            p == "pasos_dia" || p == "pasos_total" || l.id.startsWith("c_pasos") || l.id == "maraton" -> huellas(c, u, tinta, tz)
            l.id.startsWith("entrenos") || l.id.startsWith("volumen") || l.id.startsWith("c_gym") -> pesa(c, u, tinta, tz)
            l.id.startsWith("escalada") -> montana(c, u, tinta, tono, tz)
            l.id.startsWith("diario") || l.id.startsWith("palabras") || l.id.startsWith("racha_diario") || l.id.startsWith("c_diario") -> libro(c, u, tema, tinta, tono, tz)
            l.id.startsWith("suenos") || l.id == "lucido" || l.id.startsWith("c_sueno") -> luna(c, u, tinta, tz)
            l.id.startsWith("carta") -> sobre(c, u, tema, tinta, tono, tz)
            l.id.startsWith("momentos") || l.id.startsWith("avisos") || l.id.startsWith("enfoque") -> reloj(c, u, tinta, tz)
            l.id.startsWith("reaccion") || l.id.startsWith("c_reac") -> rayo(c, u, tinta, tono, tz)
            l.id == "brujula" -> brujula(c, u, tinta, tono, tz)
            l.id.startsWith("obras") || l.id.startsWith("libros") || l.id.startsWith("subrayados") -> libros(c, u, tinta, tono, tz)
            l.id.startsWith("frases") -> comillas(c, u, tinta, tz)
            l.id.startsWith("personas") -> gente(c, u, tinta, tono, tz)
            l.id.startsWith("c_hab") -> calendario(c, u, tinta, tono, tz)
            l.id.startsWith("campanas") -> estandarte(c, u, tema, tinta, tono, tz)
            l.id == "madrugador" || l.id == "dia_perfecto" -> sol(c, u, tinta, tono, tz)
            l.id.startsWith("gastos") -> moneda(c, u, tema, tinta, tono, tz)
            else -> signo(l.icono, c, r, tinta)
        }
    }

    // ---------------------------------------------------------------- trazo y material de cada tema

    /**
     * Cómo traza cada tema: [grosor] multiplica los anchos, [cap] y [join] son las puntas y las uniones, [patron] es el
     * efecto de guiones (en unidades de la pieza: píxeles de consola, estarcido, puntadas de fieltro, punteado de grabado…),
     * [esquina] redondea las esquinas, [doble] dibuja cada contorno como doble línea (plomo de vitral, filete, construcción)
     * y [material] es el color del papel, la cera o el cristal de las piezas.
     */
    private class Trazo(val u: Float, val grosor: Float, val cap: StrokeCap, val join: StrokeJoin, val patron: FloatArray?, val esquina: Float, val doble: Boolean, val material: Color) {
        val efecto: PathEffect? = run {
            val guion = patron?.let { pt -> PathEffect.dashPathEffect(FloatArray(pt.size) { pt[it] * u }) }
            val curva = if (esquina > 0f) PathEffect.cornerPathEffect(esquina * u) else null
            if (guion != null && curva != null) PathEffect.chainPathEffect(guion, curva) else guion ?: curva
        }
        fun st(ancho: Float) = Stroke(ancho * grosor, cap = cap, join = join, pathEffect = efecto)
        fun conMaterial(m: Color) = Trazo(u, grosor, cap, join, patron, esquina, doble, m)
    }

    /** El trazo y el material de las piezas en cada tema. */
    private fun trazoTema(t: TemaId, u: Float): Trazo = when (t) {
        TemaId.ROMANO -> Trazo(u, 1.2f, StrokeCap.Butt, StrokeJoin.Miter, null, 0f, false, Color(0xFFEDE6D6))
        TemaId.CIBER -> Trazo(u, 0.9f, StrokeCap.Square, StrokeJoin.Miter, floatArrayOf(2.2f, 1.1f), 0f, false, Color(0xFFC8FBFF))
        TemaId.NEON -> Trazo(u, 1.15f, StrokeCap.Round, StrokeJoin.Round, null, 1.5f, true, Color(0xFFFFD3E8))   // tubo doble: el halo y el núcleo
        TemaId.HELENO -> Trazo(u, 0.95f, StrokeCap.Butt, StrokeJoin.Bevel, null, 0f, true, Color(0xFFE9EEF4))
        // El cálamo del escriba, con su tinta que se corta un poco, sobre papiro.
        TemaId.EGIPTO -> Trazo(u, 1.05f, StrokeCap.Round, StrokeJoin.Round, floatArrayOf(4f, 0.8f), 0.6f, false, Color(0xFFEDDDAA))
        TemaId.MEDIEVAL -> Trazo(u, 1.25f, StrokeCap.Round, StrokeJoin.Round, null, 0f, false, Color(0xFFEADBB8))
        // Trazo de forja: firme, de esquinas vivas y doble, como el ribete de un escudo; el material, lino blanco.
        TemaId.TEMPLARIO -> Trazo(u, 1.4f, StrokeCap.Square, StrokeJoin.Miter, null, 0f, true, Color(0xFFF1ECE0))
        TemaId.DISTOPIA -> Trazo(u, 1.1f, StrokeCap.Butt, StrokeJoin.Miter, floatArrayOf(5f, 1.6f), 0f, false, Color(0xFFD9D9D4))
        TemaId.REFUGIO -> Trazo(u, 1.05f, StrokeCap.Round, StrokeJoin.Round, null, 2.5f, false, Color(0xFFFFF1CF))
        TemaId.ORDEN -> Trazo(u, 1.3f, StrokeCap.Square, StrokeJoin.Bevel, floatArrayOf(4f, 1.2f), 0f, false, Color(0xFFCFD3D8))
        TemaId.LABORATORIO -> Trazo(u, 0.75f, StrokeCap.Round, StrokeJoin.Round, null, 0f, false, Color(0xFFF4FBFC))
        TemaId.COLONOS -> Trazo(u, 1f, StrokeCap.Butt, StrokeJoin.Miter, floatArrayOf(1.2f, 0.8f), 0f, false, Color(0xFFE6DFCC))
        TemaId.NAPOLEON -> Trazo(u, 1.05f, StrokeCap.Butt, StrokeJoin.Miter, null, 0f, true, Color(0xFFF3E6C4))
        TemaId.BOTANICO -> Trazo(u, 0.85f, StrokeCap.Round, StrokeJoin.Round, floatArrayOf(0.01f, 1.8f), 0f, false, Color(0xFFE3D3AE))
        TemaId.NAVIDAD -> Trazo(u, 1.35f, StrokeCap.Round, StrokeJoin.Round, null, 0f, true, Color(0xFFF7FAFF))
        TemaId.SEDA -> Trazo(u, 1.15f, StrokeCap.Round, StrokeJoin.Round, floatArrayOf(9f, 1.2f, 1.5f, 1.2f), 0f, false, Color(0xFFF6E2C8))
        TemaId.GOTICO -> Trazo(u, 1.4f, StrokeCap.Square, StrokeJoin.Miter, null, 0f, true, Color(0xFFD8E2F2))
        TemaId.ESPACIO -> Trazo(u, 0.85f, StrokeCap.Butt, StrokeJoin.Miter, floatArrayOf(6f, 2f, 1f, 2f), 0f, false, Color(0xFFE2E4EA))
        // Trazo blando, de esquinas redondeadas por el agua; el material es el cristal verdoso de un ojo de buey.
        TemaId.ABISMO -> Trazo(u, 1f, StrokeCap.Round, StrokeJoin.Round, null, 3f, false, Color(0xFFDCEFF2))
        TemaId.ANDALUS -> Trazo(u, 1f, StrokeCap.Butt, StrokeJoin.Miter, null, 1f, true, Color(0xFFF1E3C8))
        TemaId.MONGOL -> Trazo(u, 1.3f, StrokeCap.Round, StrokeJoin.Bevel, floatArrayOf(3f, 1.5f), 0f, false, Color(0xFFE8DCC8))
        TemaId.RENACIMIENTO -> Trazo(u, 0.7f, StrokeCap.Round, StrokeJoin.Round, null, 0f, true, Color(0xFFEFD9C0))
        TemaId.URSS -> Trazo(u, 1.5f, StrokeCap.Butt, StrokeJoin.Miter, null, 0f, false, Color(0xFFEDE3CF))
        TemaId.REVOLUCION -> Trazo(u, 1.1f, StrokeCap.Butt, StrokeJoin.Round, floatArrayOf(8f, 2f), 0f, false, Color(0xFFF0E8D8))
        TemaId.ANTIGUAS -> Trazo(u, 1.2f, StrokeCap.Round, StrokeJoin.Bevel, null, 3f, false, Color(0xFFD9D2C2))
    }

    /** Un contorno con el trazo del tema (doble si el tema lo pide). */
    private fun DrawScope.contorno(tz: Trazo, p: Path, col: Color, ancho: Float) {
        if (tz.doble) {
            drawPath(p, col, style = Stroke(ancho * tz.grosor * 2.2f, cap = tz.cap, join = tz.join, pathEffect = tz.efecto))
            drawPath(p, tz.material, style = Stroke(ancho * tz.grosor * 0.8f, cap = tz.cap, join = tz.join, pathEffect = tz.efecto))
        } else drawPath(p, col, style = tz.st(ancho))
    }

    /** Una línea principal con el trazo del tema (doble si el tema lo pide). */
    private fun DrawScope.raya(tz: Trazo, col: Color, a: Offset, b: Offset, ancho: Float) {
        drawLine(col, a, b, ancho * tz.grosor * (if (tz.doble) 2.2f else 1f), tz.cap, tz.efecto)
        if (tz.doble) drawLine(tz.material, a, b, ancho * tz.grosor * 0.8f, tz.cap, tz.efecto)
    }

    /** Una línea de detalle: el grosor, la punta y el efecto del tema, pero nunca doble. */
    private fun DrawScope.fina(tz: Trazo, col: Color, a: Offset, b: Offset, ancho: Float) = drawLine(col, a, b, ancho * tz.grosor, tz.cap, tz.efecto)

    private fun circulo(c: Offset, r: Float) = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }

    private fun caja(l: Float, t: Float, r: Float, b: Float, radio: Float = 0f) = Path().apply { addRoundRect(RoundRect(l, t, r, b, CornerRadius(radio))) }

    /** Una estrella de [n] puntas; [ri] es el radio de adentro respecto al de afuera. */
    private fun estrellaN(c: Offset, R: Float, n: Int, ri: Float = 0.42f): Path = Path().apply {
        for (k in 0 until n * 2) {
            val a = -PI / 2 + k * PI / n; val rr = if (k % 2 == 0) R else R * ri
            val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Un engrane de [n] dientes trapezoidales. */
    private fun engrane(c: Offset, rExt: Float, rInt: Float, n: Int): Path = Path().apply {
        val paso = 2 * PI / n
        for (i in 0 until n) {
            val a0 = i * paso
            listOf(0.0 to rInt, 0.1 to rExt, 0.45 to rExt, 0.55 to rInt).forEachIndexed { k, (f, rr) ->
                val a = a0 + f * paso; val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0 && k == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        close()
    }

    /** Un texto corto centrado en [p] (letras de monedas, rótulos). */
    private fun DrawScope.letra(s: String, p: Offset, tam: Float, col: Color, negrita: Boolean = true) {
        val pintura = android.graphics.Paint().apply { color = col.toArgb(); textSize = tam; textAlign = android.graphics.Paint.Align.CENTER; isAntiAlias = true; isFakeBoldText = negrita }
        drawContext.canvas.nativeCanvas.drawText(s, p.x, p.y + tam * 0.35f, pintura)
    }

    private fun DrawScope.signo(s: String, c: Offset, r: Float, tinta: Color) {
        val p = android.graphics.Paint().apply { color = android.graphics.Color.argb((tinta.alpha * 255).toInt(), (tinta.red * 255).toInt(), (tinta.green * 255).toInt(), (tinta.blue * 255).toInt()); textSize = r * 0.9f; textAlign = android.graphics.Paint.Align.CENTER; isAntiAlias = true }
        drawContext.canvas.nativeCanvas.drawText(s, c.x, c.y + r * 0.32f, p)
    }

    // ---------------------------------------------------------------- borde de la medalla

    /**
     * El borde de la medalla en cada tema: perlas, píxeles, estrías, cordón, abrazaderas, remaches, dientes de engrane,
     * doble aro graduado, cuerda torcida, laurel, enredadera, copos, sartas de monedas, arquillos lobulados hacia dentro,
     * arcos de órbita, festón hacia fuera, rombos cardinales, graduación de instrumento, estrellas, tricolor y muescas.
     */
    private fun DrawScope.borde(tema: TemaId, c: Offset, r: Float, u: Float, metal: Color, tinta: Color, cs: ColorScheme, hecho: Boolean) {
        val m = metal.copy(alpha = if (hecho) 0.9f else 0.3f); val t = tinta.copy(alpha = 0.35f)
        val pri = cs.primary.copy(alpha = if (hecho) 0.8f else 0.25f)
        fun en(a: Double, k: Float) = Offset(c.x + (r * k * cos(a)).toFloat(), c.y + (r * k * sin(a)).toFloat())
        fun grados(a: Double) = (a * 180 / PI).toFloat()
        when (tema) {
            TemaId.ROMANO -> for (i in 0 until 24) drawCircle(m, 1.4f * u, en(i * PI / 12, 0.92f))
            TemaId.CIBER -> for (i in 0 until 28) { val p = en(i * PI / 14, 0.91f); drawRect(pri, Offset(p.x - 1.3f * u, p.y - 1.3f * u), Size(2.6f * u, 2.6f * u)) }
            // Un aro de tubo de neón con su halo, cortado en tramos como los letreros viejos.
            TemaId.NEON -> {
                drawCircle(pri.copy(alpha = pri.alpha * 0.3f), r * 0.91f, c, style = Stroke(4.5f * u))
                drawCircle(pri, r * 0.91f, c, style = Stroke(1.3f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(22 * u, 3 * u))))
            }
            TemaId.HELENO -> for (i in 0 until 40) drawLine(m, en(i * PI / 20, 0.86f), en(i * PI / 20, 0.95f), 1.1f * u)
            // La cenefa de rectángulos, curvada alrededor: oro y el color del tema, uno y uno.
            TemaId.EGIPTO -> for (i in 0 until 32) { val a = i * PI / 16; drawLine(if (i % 2 == 0) pri else m, en(a - 0.07, 0.9f), en(a + 0.07, 0.9f), 2.6f * u) }
            TemaId.MEDIEVAL -> drawCircle(t, r * 0.9f, c, style = Stroke(2.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * u, 3 * u))))
            // Un anillo de cota de malla: treinta anillas.
            TemaId.TEMPLARIO -> for (i in 0 until 30) drawCircle(t, r * 0.06f, en(i * PI / 15, 0.9f), style = Stroke(0.9f * u))
            TemaId.DISTOPIA -> {
                drawCircle(t, r * 0.9f, c, style = Stroke(1.2f * u)); drawCircle(t, r * 0.84f, c, style = Stroke(0.8f * u))
                for (i in 0 until 4) { val a = PI / 4 + i * PI / 2; val p = en(a, 0.87f); rotate(grados(a), p) { drawRect(tinta.copy(alpha = 0.6f), Offset(p.x - 3 * u, p.y - 1.5f * u), Size(6 * u, 3 * u)) } }
            }
            TemaId.REFUGIO -> { drawCircle(pri, r * 0.9f, c, style = Stroke(3 * u)); for (i in 0 until 8) drawCircle(Color.White.copy(alpha = 0.7f), 1.2f * u, en(i * PI / 4 + PI / 8, 0.9f)) }
            TemaId.ORDEN -> for (i in 0 until 16) { val a = i * PI / 8; val p = en(a, 0.93f); rotate(grados(a) + 90f, p) { drawRect(m, Offset(p.x - 2.5f * u, p.y - 2 * u), Size(5 * u, 4 * u)) } }
            TemaId.LABORATORIO -> {
                drawCircle(pri, r * 0.93f, c, style = Stroke(1.6f * u)); drawCircle(cs.primary.copy(alpha = 0.25f), r * 0.86f, c, style = Stroke(0.8f * u))
                for (i in 0 until 8) drawLine(cs.primary.copy(alpha = 0.5f), en(i * PI / 4, 0.86f), en(i * PI / 4, 0.93f), 0.8f * u)
            }
            TemaId.COLONOS -> for (i in 0 until 36) { val a = i * PI / 18; drawLine(t, en(a - 0.05, 0.85f), en(a + 0.05, 0.95f), 1.4f * u) }
            TemaId.NAPOLEON -> for (i in 0 until 20) { val a = i * PI / 10; val p = en(a, 0.89f); rotate(grados(a) + 45f, p) { drawOval(m, Offset(p.x - 3 * u, p.y - 1.3f * u), Size(6 * u, 2.6f * u)) } }
            TemaId.BOTANICO -> {
                drawCircle(t, r * 0.89f, c, style = Stroke(0.9f * u))
                for (i in 0 until 14) { val a = i * PI / 7; val p = en(a, if (i % 2 == 0) 0.84f else 0.94f); rotate(grados(a), p) { drawOval(Color(0xFF5C7A3A).copy(alpha = if (hecho) 0.8f else 0.3f), Offset(p.x - 1.4f * u, p.y - 3 * u), Size(2.8f * u, 6 * u)) } }
            }
            TemaId.NAVIDAD -> for (i in 0 until 12) {
                val p = en(i * PI / 6, 0.9f)
                for (k in 0 until 3) { val a = k * PI / 3; val dx = (2.4f * u * cos(a)).toFloat(); val dy = (2.4f * u * sin(a)).toFloat(); drawLine(Color.White.copy(alpha = if (hecho) 0.9f else 0.4f), Offset(p.x - dx, p.y - dy), Offset(p.x + dx, p.y + dy), 0.8f * u) }
            }
            TemaId.SEDA -> for (i in 0 until 12) { val p = en(i * PI / 6, 0.89f); drawCircle(m, 3.2f * u, p); drawRect(tinta.copy(alpha = 0.6f), Offset(p.x - 1 * u, p.y - 1 * u), Size(2 * u, 2 * u)) }
            TemaId.GOTICO -> {
                val p = Path()
                for (i in 0 until 12) { val a0 = i * PI / 6; val a1 = (i + 1) * PI / 6; val p0 = en(a0, 0.92f); val pm = en((a0 + a1) / 2, 0.8f); val p1 = en(a1, 0.92f); if (i == 0) p.moveTo(p0.x, p0.y); p.quadraticTo(pm.x, pm.y, p1.x, p1.y) }
                drawPath(p, m, style = Stroke(1.4f * u))
            }
            TemaId.ESPACIO -> {
                for (i in 0 until 4) drawArc(pri, i * 90f + 8f, 74f, false, Offset(c.x - r * 0.9f, c.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(1.6f * u))
                drawCircle(cs.primary, 2 * u, en(-PI / 4, 0.9f))
            }
            // Un anillo de burbujas de varios tamaños y cuatro pernos de latón.
            TemaId.ABISMO -> {
                for (i in 0 until 18) drawCircle(pri, (1f + (i % 3) * 0.5f) * u, en(i * PI / 9, 0.9f), style = Stroke(0.8f * u))
                for (i in 0 until 4) drawCircle(m, 1.8f * u, en(PI / 4 + i * PI / 2, 0.9f))
            }
            TemaId.ANDALUS -> {
                val p = Path()
                for (i in 0 until 16) { val a0 = i * PI / 8; val a1 = (i + 1) * PI / 8; val p0 = en(a0, 0.86f); val pm = en((a0 + a1) / 2, 0.99f); val p1 = en(a1, 0.86f); if (i == 0) p.moveTo(p0.x, p0.y); p.quadraticTo(pm.x, pm.y, p1.x, p1.y) }
                drawPath(p, m, style = Stroke(1.3f * u))
            }
            TemaId.MONGOL -> {
                drawCircle(m, r * 0.94f, c, style = Stroke(0.9f * u)); drawCircle(m, r * 0.84f, c, style = Stroke(0.9f * u))
                for (i in 0 until 4) { val p = en(i * PI / 2, 0.89f); drawPath(Path().apply { moveTo(p.x, p.y - 3.5f * u); lineTo(p.x + 3.5f * u, p.y); lineTo(p.x, p.y + 3.5f * u); lineTo(p.x - 3.5f * u, p.y); close() }, m) }
            }
            TemaId.RENACIMIENTO -> { drawCircle(t, r * 0.95f, c, style = Stroke(0.6f * u)); for (i in 0 until 36) drawLine(t, en(i * PI / 18, if (i % 3 == 0) 0.82f else 0.89f), en(i * PI / 18, 0.95f), 0.6f * u) }
            TemaId.URSS -> for (i in 0 until 5) drawPath(estrellaN(en(-PI / 2 + i * 2 * PI / 5, 0.88f), 3.6f * u, 5), Color(0xFFD32F2F).copy(alpha = if (hecho) 0.9f else 0.35f))
            TemaId.REVOLUCION -> listOf(Color(0xFF1F3A8A), Color.White, Color(0xFFC62828)).forEachIndexed { i, col ->
                drawArc(col.copy(alpha = if (hecho) 0.85f else 0.3f), -90f + i * 120f, 120f, false, Offset(c.x - r * 0.9f, c.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(3 * u))
            }
            TemaId.ANTIGUAS -> drawCircle(t, r * 0.9f, c, style = Stroke(2 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(9 * u, 2 * u, 2 * u, 2 * u))))
        }
    }

    // ---------------------------------------------------------------- ciudad y sello

    private fun DrawScope.ciudad(c: Offset, u: Float, tema: TemaId, tinta: Color, tono: Color, nivel: Int, tz: Trazo) {
        val base = c.y + 22 * u
        val n = 3 + nivel
        val ancho = 60 * u / n
        for (i in 0 until n) {
            val x = c.x - 30 * u + i * ancho
            val h = (14 + ((i * 37 + nivel * 11) % 22)) * u
            drawRect(tinta, Offset(x + 1 * u, base - h), Size(ancho - 2 * u, h))
            techo(tema, x, ancho, base - h, u, tinta, tz)
            // Ventanas encendidas.
            for (k in 0 until (h / (6 * u)).toInt()) drawRect(tono.copy(alpha = 0.85f), Offset(x + ancho / 2 - 1.2f * u, base - h + 3 * u + k * 6 * u), Size(2.4f * u, 2.4f * u))
        }
        raya(tz, tinta, Offset(c.x - 32 * u, base), Offset(c.x + 32 * u, base), 1.6f * u)
    }

    /**
     * El techo de cada edificio según el tema: hastial bajo, antena con piloto, frontón con acrótera, almenas, cartel,
     * ala en voladizo, torre con banderola, diente de sierra, tejado de una sola caída con chimenea, mansarda, invernadero,
     * hastial nevado, alero curvo, aguja, antena parabólica, almenas escalonadas, cúpula de fieltro, cúpula con linterna,
     * estrella, cinc con chimeneas y techo de paja.
     */
    private fun DrawScope.techo(tema: TemaId, x: Float, an: Float, y: Float, u: Float, tinta: Color, tz: Trazo) {
        val cx = x + an / 2
        fun tri(alto: Float, x0: Float = x, x1: Float = x + an) = drawPath(Path().apply { moveTo(x0, y); lineTo(cx, y - alto); lineTo(x1, y); close() }, tinta)
        when (tema) {
            TemaId.ROMANO -> tri(5 * u)
            TemaId.CIBER -> { drawLine(tinta, Offset(cx, y), Offset(cx, y - 6 * u), 1 * u); drawRect(tinta, Offset(cx - 0.8f * u, y - 7.6f * u), Size(1.6f * u, 1.6f * u)) }
            TemaId.NEON -> { drawLine(tinta, Offset(cx - an * 0.3f, y), Offset(cx - an * 0.3f, y - 3 * u), 0.8f * u); drawLine(tinta, Offset(cx + an * 0.3f, y), Offset(cx + an * 0.3f, y - 3 * u), 0.8f * u); drawRect(tinta, Offset(cx - an * 0.45f, y - 6.5f * u), Size(an * 0.9f, 3.5f * u)) }   // el letrero de la azotea
            TemaId.HELENO -> { tri(3.5f * u, x - 0.6f * u, x + an + 0.6f * u); drawCircle(tinta, 0.9f * u, Offset(cx, y - 4.2f * u)) }
            // Azotea plana con su gola.
            TemaId.EGIPTO -> { drawRect(tinta, Offset(x - 0.8f * u, y - 2.2f * u), Size(an + 1.6f * u, 2.2f * u)); drawLine(tinta, Offset(x - 1.4f * u, y - 2.8f * u), Offset(x + an + 1.4f * u, y - 2.8f * u), 0.8f * u) }
            TemaId.MEDIEVAL -> { val m = an / 5; for (k in 0 until 3) drawRect(tinta, Offset(x + k * 2 * m, y - 3 * u), Size(m, 3 * u)) }
            // El parapeto liso con la cruz patada alzada en medio.
            TemaId.TEMPLARIO -> { drawRect(tinta, Offset(x, y - 1.6f * u), Size(an, 1.6f * u)); drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(cx, y - 4.4f * u), 3f * u), tinta) }
            TemaId.DISTOPIA -> { drawLine(tinta, Offset(cx, y), Offset(cx, y - 3 * u), 0.8f * u); drawRect(tinta, Offset(cx - an * 0.4f, y - 6 * u), Size(an * 0.8f, 3 * u)) }
            TemaId.REFUGIO -> drawPath(Path().apply { moveTo(x - 2 * u, y - 4 * u); lineTo(x + an + 2 * u, y - 1 * u); lineTo(x + an + 2 * u, y); lineTo(x - 2 * u, y - 3 * u); close() }, tinta)
            TemaId.ORDEN -> {
                tri(7 * u, x + an * 0.15f, x + an * 0.85f)
                drawLine(tinta, Offset(cx, y - 7 * u), Offset(cx, y - 11 * u), 0.8f * u)
                drawPath(Path().apply { moveTo(cx, y - 11 * u); lineTo(cx + 3 * u, y - 10 * u); lineTo(cx, y - 9 * u); close() }, tinta)
            }
            TemaId.LABORATORIO -> { val m = an / 2; for (k in 0 until 2) drawPath(Path().apply { moveTo(x + k * m, y); lineTo(x + k * m, y - 3.5f * u); lineTo(x + (k + 1) * m, y); close() }, tinta) }
            TemaId.COLONOS -> { drawPath(Path().apply { moveTo(x, y); lineTo(x + an * 0.35f, y - 5 * u); lineTo(x + an, y); close() }, tinta); drawRect(tinta, Offset(x + an * 0.65f, y - 5 * u), Size(1.6f * u, 4 * u)) }
            TemaId.NAPOLEON -> drawPath(Path().apply { moveTo(x, y); lineTo(x + 1.5f * u, y - 4 * u); lineTo(x + an - 1.5f * u, y - 4 * u); lineTo(x + an, y); close() }, tinta)
            TemaId.BOTANICO -> {
                val d = an - 1 * u
                drawArc(tz.material.copy(alpha = 0.5f), 180f, 180f, true, Offset(x + 0.5f * u, y - d / 2), Size(d, d))
                drawArc(tinta, 180f, 180f, false, Offset(x + 0.5f * u, y - d / 2), Size(d, d), style = Stroke(0.9f * u))
                drawLine(tinta, Offset(cx, y), Offset(cx, y - d / 2), 0.6f * u)
            }
            TemaId.NAVIDAD -> {
                tri(6 * u, x - 1 * u, x + an + 1 * u)
                drawPath(Path().apply { moveTo(cx - 2.2f * u, y - 3.8f * u); lineTo(cx, y - 6 * u); lineTo(cx + 2.2f * u, y - 3.8f * u); close() }, Color.White.copy(alpha = 0.85f))
            }
            TemaId.SEDA -> drawPath(Path().apply {
                moveTo(x - 3 * u, y - 4 * u); quadraticTo(x, y, x + 2 * u, y); lineTo(x + an - 2 * u, y); quadraticTo(x + an, y, x + an + 3 * u, y - 4 * u)
                lineTo(x + an - 2 * u, y - 4.5f * u); lineTo(cx, y - 6 * u); lineTo(x + 2 * u, y - 4.5f * u); close()
            }, tinta)
            TemaId.GOTICO -> { tri(11 * u, x + 1 * u, x + an - 1 * u); drawCircle(tinta, 0.9f * u, Offset(cx, y - 11.5f * u)) }
            TemaId.ESPACIO -> { drawLine(tinta, Offset(cx, y), Offset(cx, y - 3 * u), 0.8f * u); drawArc(tinta, 20f, 140f, false, Offset(cx - 3 * u, y - 9 * u), Size(6 * u, 6 * u), style = Stroke(1 * u)) }
            // La cúpula de una escotilla, con su respiradero.
            TemaId.ABISMO -> { drawArc(tinta, 180f, 180f, true, Offset(cx - an * 0.32f, y - an * 0.32f), Size(an * 0.64f, an * 0.64f)); drawLine(tinta, Offset(cx, y - an * 0.3f), Offset(cx, y - an * 0.3f - 3 * u), 0.9f * u); drawCircle(tinta, 1 * u, Offset(cx, y - an * 0.3f - 3.5f * u)) }
            TemaId.ANDALUS -> {
                val m = (an - 2 * u) / 5
                for (k in 0 until 3) { val mx = x + 1 * u + k * 2 * m; drawRect(tinta, Offset(mx, y - 1.6f * u), Size(m, 1.6f * u)); drawRect(tinta, Offset(mx + m * 0.25f, y - 3.2f * u), Size(m * 0.5f, 1.6f * u)) }
            }
            TemaId.MONGOL -> { drawArc(tinta, 180f, 180f, true, Offset(x, y - an * 0.25f), Size(an, an * 0.5f)); drawCircle(tinta, 1.1f * u, Offset(cx, y - an * 0.25f - 0.6f * u)) }
            TemaId.RENACIMIENTO -> { val d = an - 3 * u; drawArc(tinta, 180f, 180f, true, Offset(cx - d / 2, y - d / 2), Size(d, d)); drawLine(tinta, Offset(cx, y - d / 2), Offset(cx, y - d / 2 - 3 * u), 1 * u) }
            TemaId.URSS -> drawPath(estrellaN(Offset(cx, y - 3 * u), 2.6f * u, 5), Color(0xFFD32F2F))
            TemaId.REVOLUCION -> { drawRect(tinta, Offset(x, y - 2 * u), Size(an, 2 * u)); listOf(0.25f, 0.7f).forEach { f -> drawRect(tinta, Offset(x + an * f, y - 5 * u), Size(1.4f * u, 3 * u)) } }
            TemaId.ANTIGUAS -> {
                drawPath(Path().apply { moveTo(x - 2 * u, y + 1 * u); lineTo(cx, y - 7 * u); lineTo(x + an + 2 * u, y + 1 * u); close() }, tinta)
                drawLine(Color.White.copy(alpha = 0.35f), Offset(cx - 2 * u, y - 4 * u), Offset(cx + 2 * u, y - 4 * u), 0.6f * u)
            }
        }
    }

    /**
     * Sello de pasaporte: la forma es la del tema (círculo de guiones, corchetes, doble aro, lacre, sello de goma, óvalo,
     * escudo, hexágono, sello dentado, cartela con corona, mandorla, festón, sello chino, arco apuntado, retícula, estrella
     * de ocho, rombo, círculo en el cuadrado, esquina cortada, cinta de dos colas y octógono de piedra); adentro, una
     * bandera de tres franjas.
     */
    private fun DrawScope.sello(c: Offset, u: Float, tema: TemaId, tinta: Color, cs: ColorScheme, tz: Trazo) {
        val tinto = tinta.copy(alpha = 0.8f)
        fun traza(p: Path) = contorno(tz, p, tinto, 1.6f * u)
        fun poligono(n: Int, radio: Float, giro: Double, irregular: Boolean = false) = Path().apply {
            for (i in 0 until n) {
                val a = giro + i * 2 * PI / n; val rr = if (irregular) radio * (0.92f + 0.04f * ((i * 5) % 3)) else radio
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        when (tema) {
            TemaId.ROMANO -> drawCircle(tinto, 27 * u, c, style = Stroke(1.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
            TemaId.CIBER -> {
                val x0 = c.x - 27 * u; val x1 = c.x + 27 * u; val y0 = c.y - 22 * u; val y1 = c.y + 22 * u; val l = 8 * u
                traza(Path().apply { moveTo(x0 + l, y0); lineTo(x0, y0); lineTo(x0, y1); lineTo(x0 + l, y1); moveTo(x1 - l, y0); lineTo(x1, y0); lineTo(x1, y1); lineTo(x1 - l, y1) })
            }
            // El letrero de «abierto»: una cápsula de tubo con su halo y una raya debajo.
            TemaId.NEON -> {
                val capsula = Path().apply { addRoundRect(RoundRect(c.x - 28 * u, c.y - 16 * u, c.x + 28 * u, c.y + 16 * u, CornerRadius(16 * u))) }
                drawPath(capsula, tinto.copy(alpha = 0.25f), style = Stroke(5 * u))
                traza(capsula)
                drawLine(tinto, Offset(c.x - 14 * u, c.y + 22 * u), Offset(c.x + 14 * u, c.y + 22 * u), 1.4f * u, StrokeCap.Round)
            }
            TemaId.HELENO -> { traza(circulo(c, 27 * u)); drawCircle(tinto, 23 * u, c, style = Stroke(0.8f * u)) }
            // Un cartucho tendido, con la barra del nudo a la derecha.
            TemaId.EGIPTO -> {
                traza(Path().apply { addRoundRect(RoundRect(c.x - 27 * u, c.y - 18 * u, c.x + 23 * u, c.y + 18 * u, CornerRadius(18 * u))) })
                drawLine(tinto, Offset(c.x + 27 * u, c.y - 18 * u), Offset(c.x + 27 * u, c.y + 18 * u), 2.4f * u)
            }
            TemaId.MEDIEVAL -> traza(Path().apply {
                for (i in 0 until 36) {
                    val a = i * PI / 18; val rr = 27 * u * (1f + 0.05f * sin(i * 2.3).toFloat())
                    val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            })
            // El escudo de la casa con la cruz patada.
            TemaId.TEMPLARIO -> { traza(Path().apply { moveTo(c.x - 13 * u, c.y - 15 * u); lineTo(c.x + 13 * u, c.y - 15 * u); quadraticTo(c.x + 14 * u, c.y + 5 * u, c.x, c.y + 17 * u); quadraticTo(c.x - 14 * u, c.y + 5 * u, c.x - 13 * u, c.y - 15 * u); close() }); traza(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(c.x, c.y - 2 * u), 9 * u)) }
            TemaId.DISTOPIA -> rotate(-6f, c) { traza(caja(c.x - 28 * u, c.y - 20 * u, c.x + 28 * u, c.y + 20 * u, 2 * u)); drawRect(tinto, Offset(c.x - 25 * u, c.y - 17 * u), Size(50 * u, 34 * u), style = Stroke(0.8f * u)) }
            TemaId.REFUGIO -> traza(Path().apply { addOval(Rect(c.x - 30 * u, c.y - 20 * u, c.x + 30 * u, c.y + 20 * u)) })
            TemaId.ORDEN -> traza(Path().apply { moveTo(c.x - 26 * u, c.y - 20 * u); lineTo(c.x + 26 * u, c.y - 20 * u); lineTo(c.x + 26 * u, c.y + 8 * u); lineTo(c.x, c.y + 26 * u); lineTo(c.x - 26 * u, c.y + 8 * u); close() })
            TemaId.LABORATORIO -> traza(poligono(6, 27 * u, PI / 6))
            TemaId.COLONOS -> {
                traza(caja(c.x - 26 * u, c.y - 22 * u, c.x + 26 * u, c.y + 22 * u))
                for (k in 0 until 9) { drawCircle(tinto, 1.3f * u, Offset(c.x - 24 * u + k * 6 * u, c.y - 22 * u)); drawCircle(tinto, 1.3f * u, Offset(c.x - 24 * u + k * 6 * u, c.y + 22 * u)) }
            }
            TemaId.NAPOLEON -> {
                traza(Path().apply { addOval(Rect(c.x - 20 * u, c.y - 24 * u, c.x + 20 * u, c.y + 26 * u)) })
                listOf(-6f, 0f, 6f).forEach { dx -> drawCircle(tinto, (if (dx == 0f) 2.4f else 1.8f) * u, Offset(c.x + dx * u, c.y - 27 * u)) }
            }
            TemaId.BOTANICO -> traza(Path().apply { moveTo(c.x - 30 * u, c.y); quadraticTo(c.x, c.y - 34 * u, c.x + 30 * u, c.y); quadraticTo(c.x, c.y + 34 * u, c.x - 30 * u, c.y); close() })
            TemaId.NAVIDAD -> traza(Path().apply {
                for (i in 0 until 12) {
                    val a0 = i * PI / 6; val a1 = (i + 1) * PI / 6
                    val p0 = Offset(c.x + (24 * u * cos(a0)).toFloat(), c.y + (24 * u * sin(a0)).toFloat())
                    val pm = Offset(c.x + (31 * u * cos((a0 + a1) / 2)).toFloat(), c.y + (31 * u * sin((a0 + a1) / 2)).toFloat())
                    val p1 = Offset(c.x + (24 * u * cos(a1)).toFloat(), c.y + (24 * u * sin(a1)).toFloat())
                    if (i == 0) moveTo(p0.x, p0.y); quadraticTo(pm.x, pm.y, p1.x, p1.y)
                }
                close()
            })
            TemaId.SEDA -> { traza(poligono(8, 30 * u, PI / 8)); drawRect(tinto, Offset(c.x - 21 * u, c.y - 21 * u), Size(42 * u, 42 * u), style = Stroke(0.8f * u)) }
            TemaId.GOTICO -> traza(Path().apply {
                moveTo(c.x - 22 * u, c.y + 22 * u); lineTo(c.x - 22 * u, c.y - 6 * u); quadraticTo(c.x - 22 * u, c.y - 22 * u, c.x, c.y - 32 * u)
                quadraticTo(c.x + 22 * u, c.y - 22 * u, c.x + 22 * u, c.y - 6 * u); lineTo(c.x + 22 * u, c.y + 22 * u); close()
            })
            TemaId.ESPACIO -> {
                traza(circulo(c, 25 * u))
                listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { d -> drawLine(tinto, Offset(c.x + d.x * 22 * u, c.y + d.y * 22 * u), Offset(c.x + d.x * 31 * u, c.y + d.y * 31 * u), 1.2f * u) }
            }
            // Un ojo de buey: dos aros, ocho pernos y el agua ondulada que se ve por el cristal.
            TemaId.ABISMO -> {
                traza(circulo(c, 27 * u)); traza(circulo(c, 18 * u))
                for (i in 0 until 8) { val a = PI / 8 + i * PI / 4; drawCircle(tinto, 1.5f * u, Offset(c.x + (22.5f * u * cos(a)).toFloat(), c.y + (22.5f * u * sin(a)).toFloat())) }
                val ola = Path(); var x = c.x - 14 * u
                ola.moveTo(x, c.y)
                while (x <= c.x + 14 * u) { ola.lineTo(x, c.y + 2.2f * u * sin((x - c.x) / (3 * u))); x += 1.5f * u }
                drawPath(ola, tinto, style = tz.st(1.2f * u))
            }
            TemaId.ANDALUS -> traza(estrellaN(c, 31 * u, 8, 0.8f))
            TemaId.MONGOL -> traza(Path().apply { moveTo(c.x, c.y - 32 * u); lineTo(c.x + 32 * u, c.y); lineTo(c.x, c.y + 32 * u); lineTo(c.x - 32 * u, c.y); close() })
            TemaId.RENACIMIENTO -> { traza(caja(c.x - 25 * u, c.y - 25 * u, c.x + 25 * u, c.y + 25 * u)); drawCircle(tinto, 25 * u, c, style = Stroke(0.7f * u)) }
            TemaId.URSS -> traza(Path().apply { moveTo(c.x - 27 * u, c.y - 20 * u); lineTo(c.x + 15 * u, c.y - 20 * u); lineTo(c.x + 27 * u, c.y - 8 * u); lineTo(c.x + 27 * u, c.y + 20 * u); lineTo(c.x - 27 * u, c.y + 20 * u); close() })
            TemaId.REVOLUCION -> traza(Path().apply {
                moveTo(c.x - 32 * u, c.y - 18 * u); lineTo(c.x + 32 * u, c.y - 18 * u); lineTo(c.x + 26 * u, c.y); lineTo(c.x + 32 * u, c.y + 18 * u)
                lineTo(c.x - 32 * u, c.y + 18 * u); lineTo(c.x - 26 * u, c.y); close()
            })
            TemaId.ANTIGUAS -> traza(poligono(8, 29 * u, PI / 8 + 0.1, irregular = true))
        }
        val col = listOf(cs.primary, cs.secondary, cs.tertiary)
        drawLine(tinta, Offset(c.x - 14 * u, c.y + 16 * u), Offset(c.x - 14 * u, c.y - 16 * u), 1.6f * u)
        for (i in 0 until 3) drawRect(col[i], Offset(c.x - 13 * u, c.y - 16 * u + i * 5 * u), Size(26 * u, 5 * u))
        drawRect(tinta, Offset(c.x - 13 * u, c.y - 16 * u), Size(26 * u, 15 * u), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- piezas con el trazo del tema

    private fun DrawScope.globo(c: Offset, u: Float, tinta: Color, tono: Color, tz: Trazo) {
        drawCircle(tono.copy(alpha = 0.35f), 24 * u, c)
        contorno(tz, circulo(c, 24 * u), tinta, 1.6f * u)
        listOf(0.4f, 0.8f).forEach { k -> drawOval(tinta, Offset(c.x - 24 * u * k, c.y - 24 * u), Size(48 * u * k, 48 * u), style = tz.st(1f * u)) }
        listOf(-12f, 0f, 12f).forEach { dy -> val w = kotlin.math.sqrt(24f * 24f - dy * dy) * u; fina(tz, tinta, Offset(c.x - w, c.y + dy * u), Offset(c.x + w, c.y + dy * u), 1f * u) }
    }

    private fun DrawScope.brujula(c: Offset, u: Float, tinta: Color, tono: Color, tz: Trazo) {
        drawCircle(tz.material.copy(alpha = 0.5f), 24 * u, c)
        contorno(tz, circulo(c, 24 * u), tinta, 1.4f * u)
        for (i in 0 until 4) { val a = i * PI / 2; raya(tz, tinta, Offset(c.x + (20 * u * cos(a)).toFloat(), c.y + (20 * u * sin(a)).toFloat()), Offset(c.x + (24 * u * cos(a)).toFloat(), c.y + (24 * u * sin(a)).toFloat()), 1.4f * u) }
        rotate(30f, c) {
            drawPath(Path().apply { moveTo(c.x, c.y - 20 * u); lineTo(c.x + 5 * u, c.y); lineTo(c.x - 5 * u, c.y); close() }, tono)
            drawPath(Path().apply { moveTo(c.x, c.y + 20 * u); lineTo(c.x + 5 * u, c.y); lineTo(c.x - 5 * u, c.y); close() }, tinta)
        }
        drawCircle(tinta, 2 * u, c)
    }

    private fun DrawScope.huellas(c: Offset, u: Float, tinta: Color, tz: Trazo) {
        listOf(Offset(-10f, 14f) to -12f, Offset(6f, 2f) to 8f, Offset(-6f, -12f) to -10f, Offset(10f, -24f) to 10f).forEach { (o, giro) ->
            val p = c + Offset(o.x * u, o.y * u)
            rotate(giro, p) {
                val planta = Path().apply { addOval(Rect(p.x - 3.5f * u, p.y - 5 * u, p.x + 3.5f * u, p.y + 5 * u)) }
                drawPath(planta, tz.material); contorno(tz, planta, tinta, 1.1f * u)
                for (k in 0 until 3) drawCircle(tinta, 1.3f * u, Offset(p.x - 2.4f * u + k * 2.4f * u, p.y - 7.5f * u))
            }
        }
    }

    private fun DrawScope.pesa(c: Offset, u: Float, tinta: Color, tz: Trazo) {
        raya(tz, tinta, Offset(c.x - 22 * u, c.y), Offset(c.x + 22 * u, c.y), 3 * u)
        listOf(-1f, 1f).forEach { s ->
            val grande = caja(c.x + s * 14 * u - 3 * u, c.y - 12 * u, c.x + s * 14 * u + 3 * u, c.y + 12 * u, 1.5f * u)
            val chica = caja(c.x + s * 20 * u - 2.5f * u, c.y - 8 * u, c.x + s * 20 * u + 2.5f * u, c.y + 8 * u, 1.5f * u)
            drawPath(grande, tinta); contorno(tz, grande, tinta, 0.8f * u)
            drawPath(chica, tz.material); contorno(tz, chica, tinta, 1f * u)
        }
    }

    private fun DrawScope.montana(c: Offset, u: Float, tinta: Color, tono: Color, tz: Trazo) {
        val monte = Path().apply { moveTo(c.x - 28 * u, c.y + 20 * u); lineTo(c.x - 4 * u, c.y - 18 * u); lineTo(c.x + 6 * u, c.y - 4 * u); lineTo(c.x + 14 * u, c.y - 12 * u); lineTo(c.x + 28 * u, c.y + 20 * u); close() }
        drawPath(monte, tinta); contorno(tz, monte, tinta, 1f * u)
        drawPath(Path().apply { moveTo(c.x - 9 * u, c.y - 10 * u); lineTo(c.x - 4 * u, c.y - 18 * u); lineTo(c.x + 1 * u, c.y - 10 * u); close() }, tz.material.copy(alpha = 0.85f))
        raya(tz, tinta, Offset(c.x - 4 * u, c.y - 18 * u), Offset(c.x - 4 * u, c.y - 30 * u), 1.2f * u)
        drawPath(Path().apply { moveTo(c.x - 4 * u, c.y - 30 * u); lineTo(c.x + 6 * u, c.y - 27 * u); lineTo(c.x - 4 * u, c.y - 24 * u); close() }, tono)
    }

    private fun DrawScope.luna(c: Offset, u: Float, tinta: Color, tz: Trazo) {
        val p = Path().apply { addOval(Rect(c.x - 20 * u, c.y - 20 * u, c.x + 20 * u, c.y + 20 * u)) }
        val hueco = Path().apply { addOval(Rect(c.x - 10 * u, c.y - 26 * u, c.x + 26 * u, c.y + 14 * u)) }
        val creciente = Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, p, hueco)
        drawPath(creciente, tz.material); contorno(tz, creciente, tinta, 1.2f * u)
        listOf(Offset(14f, -14f) to 3f, Offset(20f, 6f) to 2f, Offset(6f, 18f) to 1.6f).forEach { (o, s) -> estrella(c + Offset(o.x * u, o.y * u), s * u, tinta) }
    }

    private fun DrawScope.estrella(p: Offset, s: Float, col: Color) {
        drawPath(Path().apply { for (i in 0 until 8) { val rr = if (i % 2 == 0) s * 1.6f else s * 0.5f; val a = -PI / 2 + i * PI / 4; val x = p.x + (rr * cos(a)).toFloat(); val y = p.y + (rr * sin(a)).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y) }; close() }, col)
    }

    private fun DrawScope.reloj(c: Offset, u: Float, tinta: Color, tz: Trazo) {
        contorno(tz, Path().apply { moveTo(c.x - 14 * u, c.y - 22 * u); lineTo(c.x + 14 * u, c.y - 22 * u); lineTo(c.x, c.y); lineTo(c.x + 14 * u, c.y + 22 * u); lineTo(c.x - 14 * u, c.y + 22 * u); lineTo(c.x, c.y); close() }, tinta, 1.6f * u)
        drawPath(Path().apply { moveTo(c.x - 9 * u, c.y + 18 * u); lineTo(c.x + 9 * u, c.y + 18 * u); lineTo(c.x, c.y + 6 * u); close() }, tinta)
        drawPath(Path().apply { moveTo(c.x - 4 * u, c.y - 8 * u); lineTo(c.x + 4 * u, c.y - 8 * u); lineTo(c.x, c.y - 3 * u); close() }, tz.material.copy(alpha = 0.8f))
        raya(tz, tinta, Offset(c.x - 18 * u, c.y - 22 * u), Offset(c.x + 18 * u, c.y - 22 * u), 2 * u); raya(tz, tinta, Offset(c.x - 18 * u, c.y + 22 * u), Offset(c.x + 18 * u, c.y + 22 * u), 2 * u)
    }

    private fun DrawScope.rayo(c: Offset, u: Float, tinta: Color, tono: Color, tz: Trazo) {
        val p = Path().apply { moveTo(c.x + 4 * u, c.y - 26 * u); lineTo(c.x - 12 * u, c.y + 4 * u); lineTo(c.x - 1 * u, c.y + 4 * u); lineTo(c.x - 6 * u, c.y + 26 * u); lineTo(c.x + 14 * u, c.y - 6 * u); lineTo(c.x + 2 * u, c.y - 6 * u); close() }
        drawPath(p, tono); contorno(tz, p, tinta, 1.2f * u)
    }

    private fun DrawScope.libros(c: Offset, u: Float, tinta: Color, tono: Color, tz: Trazo) {
        val cols = listOf(tono, tinta, tono.copy(alpha = 0.6f), tinta.copy(alpha = 0.7f))
        cols.forEachIndexed { i, col ->
            val lomo = caja(c.x - 22 * u + i * 2 * u, c.y + 14 * u - (i + 1) * 8 * u, c.x + 18 * u + i * 2 * u, c.y + 21 * u - (i + 1) * 8 * u, 1.5f * u)
            drawPath(lomo, col); contorno(tz, lomo, tinta, 0.6f * u)
            fina(tz, tz.material.copy(alpha = 0.7f), Offset(c.x + 10 * u + i * 2 * u, c.y + 15.5f * u - (i + 1) * 8 * u), Offset(c.x + 10 * u + i * 2 * u, c.y + 19.5f * u - (i + 1) * 8 * u), 0.8f * u)
        }
        raya(tz, tinta, Offset(c.x - 22 * u, c.y + 15 * u), Offset(c.x + 24 * u, c.y + 15 * u), 2 * u)
    }

    private fun DrawScope.comillas(c: Offset, u: Float, tinta: Color, tz: Trazo) {
        listOf(-10f, 10f).forEach { dx ->
            val p = Path().apply {
                addOval(Rect(c.x + (dx - 7) * u, c.y - 11 * u, c.x + (dx + 7) * u, c.y + 3 * u))
                moveTo(c.x + (dx + 6) * u, c.y - 2 * u); quadraticTo(c.x + (dx + 6) * u, c.y + 12 * u, c.x + (dx - 4) * u, c.y + 16 * u); lineTo(c.x + (dx - 2) * u, c.y + 10 * u); close()
            }
            drawPath(p, tinta); contorno(tz, p, tinta, 0.6f * u)
        }
    }

    private fun DrawScope.gente(c: Offset, u: Float, tinta: Color, tono: Color, tz: Trazo) {
        listOf(Offset(-14f, 4f) to tinta, Offset(14f, 4f) to tinta, Offset(0f, -2f) to tono).forEach { (o, col) ->
            val p = c + Offset(o.x * u, o.y * u)
            val cabeza = circulo(p - Offset(0f, 8 * u), 6 * u)
            drawPath(cabeza, col); contorno(tz, cabeza, tinta, 0.7f * u)
            drawArc(col, 180f, 180f, true, Offset(p.x - 10 * u, p.y), Size(20 * u, 20 * u))
        }
    }

    private fun DrawScope.calendario(c: Offset, u: Float, tinta: Color, tono: Color, tz: Trazo) {
        val hoja = caja(c.x - 22 * u, c.y - 18 * u, c.x + 22 * u, c.y + 20 * u, 3 * u)
        drawPath(hoja, tz.material.copy(alpha = 0.9f)); contorno(tz, hoja, tinta, 0.9f * u)
        drawRect(tinta, Offset(c.x - 22 * u, c.y - 18 * u), Size(44 * u, 8 * u))
        for (f in 0 until 4) for (k in 0 until 5) {
            val o = Offset(c.x - 18 * u + k * 8 * u, c.y - 6 * u + f * 6.5f * u)
            if ((f * 5 + k) % 4 != 3) raya(tz, tono, o, o + Offset(2.5f * u, 2.5f * u), 1.3f * u) else drawCircle(tinta.copy(alpha = 0.3f), 1 * u, o)
        }
    }

    private fun DrawScope.sol(c: Offset, u: Float, tinta: Color, tono: Color, tz: Trazo) {
        drawCircle(tono, 11 * u, c); contorno(tz, circulo(c, 11 * u), tinta, 0.8f * u)
        for (i in 0 until 12) { val a = i * PI / 6; raya(tz, tinta, Offset(c.x + (15 * u * cos(a)).toFloat(), c.y + (15 * u * sin(a)).toFloat()), Offset(c.x + (22 * u * cos(a)).toFloat(), c.y + (22 * u * sin(a)).toFloat()), 1.6f * u) }
    }

    // ---------------------------------------------------------------- monumento del patrimonio

    /**
     * El monumento de cada tema: anfiteatro, torre de servidores, templo de frontón, iglesia románica, ministerio
     * escalonado, puerta del refugio, ciudadela de acero, edificio de cristal, casa de reunión, arco de triunfo,
     * invernadero de palmeras, árbol de la plaza, pagoda, catedral de dos torres, observatorio, puerta de herradura,
     * ger, cúpula con linterna, rascacielos escalonado, fortaleza de torres redondas y círculo de piedras.
     */
    private fun DrawScope.templo(c: Offset, u: Float, tema: TemaId, tinta: Color, tz: Trazo) {
        val base = c.y + 20 * u
        val hueco = tz.material.copy(alpha = 0.75f)
        fun arco(x: Float, y: Float, an: Float, alto: Float, col: Color = hueco) {
            drawRect(col, Offset(x, y - alto + an / 2), Size(an, alto - an / 2)); drawArc(col, 180f, 180f, true, Offset(x, y - alto), Size(an, an))
        }
        fun suelo() = raya(tz, tinta, Offset(c.x - 28 * u, base), Offset(c.x + 28 * u, base), 1.6f * u)
        when (tema) {
            TemaId.ROMANO -> {
                drawPath(caja(c.x - 26 * u, base - 32 * u, c.x + 26 * u, base, 5 * u), tinta)
                for (f in 0 until 2) for (k in 0 until 6) arco(c.x - 23 * u + k * 8 * u, base - 2 * u - f * 12 * u, 5 * u, 9 * u)
                for (k in 0 until 8) drawRect(hueco, Offset(c.x - 22 * u + k * 6 * u, base - 29 * u), Size(2.5f * u, 3 * u))
                suelo()
            }
            TemaId.CIBER -> {
                drawRect(tinta, Offset(c.x - 12 * u, base - 40 * u), Size(24 * u, 40 * u))
                for (k in 0 until 7) {
                    val y = base - 36 * u + k * 5 * u
                    drawRect(hueco, Offset(c.x - 9 * u, y), Size(14 * u, 1.6f * u)); drawRect(if (k % 3 == 0) Color(0xFFC8FBFF) else hueco, Offset(c.x + 7 * u, y), Size(1.8f * u, 1.8f * u))
                }
                raya(tz, tinta, Offset(c.x, base - 40 * u), Offset(c.x, base - 46 * u), 1 * u)
                suelo()
            }
            // Una torre de departamentos angosta, con ventanas encendidas al azar y su letrero vertical colgado al lado.
            TemaId.NEON -> {
                drawRect(tinta, Offset(c.x - 14 * u, base - 46 * u), Size(20 * u, 46 * u))
                for (f in 0 until 9) for (k in 0 until 3) if ((f * 3 + k) % 4 != 1) drawRect(hueco.copy(alpha = if ((f + k) % 3 == 0) 0.9f else 0.35f), Offset(c.x - 11.5f * u + k * 6 * u, base - 42 * u + f * 4.6f * u), Size(3.5f * u, 2.4f * u))
                drawRect(Color(0xFFFF3D9A).copy(alpha = 0.3f), Offset(c.x + 7 * u, base - 40 * u), Size(8 * u, 28 * u))
                drawRect(Color(0xFFFF3D9A), Offset(c.x + 9 * u, base - 38 * u), Size(4 * u, 24 * u))
                for (k in 0 until 3) drawRect(tinta, Offset(c.x + 9.8f * u, base - 35 * u + k * 7.5f * u), Size(2.4f * u, 4 * u))
                raya(tz, tinta, Offset(c.x - 8 * u, base - 46 * u), Offset(c.x - 8 * u, base - 54 * u), 1 * u)
                drawCircle(Color(0xFFFF6B5E), 1.2f * u, Offset(c.x - 8 * u, base - 54 * u))
                suelo()
            }
            TemaId.HELENO -> {
                drawPath(Path().apply { moveTo(c.x - 26 * u, base - 26 * u); lineTo(c.x, base - 38 * u); lineTo(c.x + 26 * u, base - 26 * u); close() }, tinta)
                drawCircle(hueco, 2 * u, Offset(c.x, base - 30 * u))
                for (i in 0 until 5) drawRect(tinta, Offset(c.x - 22 * u + i * 10 * u, base - 24 * u), Size(4 * u, 22 * u))
                drawRect(tinta, Offset(c.x - 28 * u, base - 2 * u), Size(56 * u, 4 * u))
            }
            // El pilono del templo: dos torres en talud con su gola, la puerta en medio y los mástiles con banderolas.
            TemaId.EGIPTO -> {
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(c.x + s * 4 * u, base); lineTo(c.x + s * 4 * u, base - 34 * u); lineTo(c.x + s * 22 * u, base - 34 * u); lineTo(c.x + s * 26 * u, base); close() }, tinta)
                    drawLine(hueco, Offset(c.x + s * 5 * u, base - 30 * u), Offset(c.x + s * 22.5f * u, base - 30 * u), 0.8f * u)
                    drawLine(tinta, Offset(c.x + s * 10 * u, base - 34 * u), Offset(c.x + s * 10 * u, base - 44 * u), 1 * u)
                    drawPath(Path().apply { moveTo(c.x + s * 10 * u, base - 44 * u); lineTo(c.x + s * 15 * u, base - 42 * u); lineTo(c.x + s * 10 * u, base - 40 * u); close() }, tinta)
                }
                drawRect(tinta, Offset(c.x - 4 * u, base - 24 * u), Size(8 * u, 3 * u))
                drawRect(hueco, Offset(c.x - 3 * u, base - 21 * u), Size(6 * u, 21 * u))
                drawCircle(hueco, 1.6f * u, Offset(c.x, base - 22.5f * u))
                suelo()
            }
            TemaId.MEDIEVAL -> {
                drawRect(tinta, Offset(c.x - 24 * u, base - 18 * u), Size(32 * u, 18 * u))
                drawPath(Path().apply { moveTo(c.x - 26 * u, base - 18 * u); lineTo(c.x - 8 * u, base - 27 * u); lineTo(c.x + 10 * u, base - 18 * u); close() }, tinta)
                drawRect(tinta, Offset(c.x + 8 * u, base - 34 * u), Size(13 * u, 34 * u))
                drawPath(Path().apply { moveTo(c.x + 7 * u, base - 34 * u); lineTo(c.x + 14.5f * u, base - 43 * u); lineTo(c.x + 22 * u, base - 34 * u); close() }, tinta)
                arco(c.x - 11 * u, base, 6 * u, 10 * u); arco(c.x + 12.5f * u, base - 24 * u, 4 * u, 6 * u)
                suelo()
            }
            // La iglesia redonda del Temple (las de la orden imitaban el Santo Sepulcro): el tambor con sus arcos, la
            // cúpula y la linterna con la cruz.
            TemaId.TEMPLARIO -> {
                drawRect(tinta, Offset(c.x - 20 * u, base - 22 * u), Size(40 * u, 22 * u))
                drawArc(tinta, 180f, 180f, true, Offset(c.x - 20 * u, base - 36 * u), Size(40 * u, 28 * u))
                drawRect(tinta, Offset(c.x - 3 * u, base - 42 * u), Size(6 * u, 7 * u))
                drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(c.x, base - 46 * u), 3.4f * u), tinta)
                for (k in 0 until 5) arco(c.x - 17 * u + k * 7.4f * u, base - 4 * u, 4.4f * u, 9 * u)
                suelo()
            }
            TemaId.DISTOPIA -> {
                for (k in 0 until 4) { val an = (52 - k * 12) * u; drawRect(tinta, Offset(c.x - an / 2, base - (k + 1) * 9 * u), Size(an, 9 * u)) }
                val o = Offset(c.x, base - 31.5f * u)
                drawPath(Path().apply { moveTo(o.x - 5 * u, o.y); quadraticTo(o.x, o.y - 4.5f * u, o.x + 5 * u, o.y); quadraticTo(o.x, o.y + 4.5f * u, o.x - 5 * u, o.y); close() }, hueco)
                drawCircle(tinta, 1.6f * u, o)
                for (k in 0 until 3) for (j in 0 until 6 - k * 2) drawRect(hueco, Offset(c.x - (22 - k * 6) * u + j * 7 * u, base - (k + 1) * 9 * u + 3 * u), Size(2 * u, 4 * u))
            }
            TemaId.REFUGIO -> {
                drawArc(tinta.copy(alpha = 0.55f), 180f, 180f, true, Offset(c.x - 28 * u, base - 30 * u), Size(56 * u, 60 * u))
                val o = Offset(c.x, base - 15 * u)
                drawPath(engrane(o, 15 * u, 13 * u, 14), tinta)
                drawCircle(hueco, 10 * u, o, style = Stroke(1.2f * u)); drawCircle(hueco, 3 * u, o)
                for (k in 0 until 3) { val a = k * 2 * PI / 3; drawLine(hueco, o, Offset(o.x + (8 * u * cos(a)).toFloat(), o.y + (8 * u * sin(a)).toFloat()), 1.2f * u) }
                suelo()
            }
            TemaId.ORDEN -> {
                drawPath(Path().apply { moveTo(c.x - 22 * u, base); lineTo(c.x - 16 * u, base - 32 * u); lineTo(c.x + 16 * u, base - 32 * u); lineTo(c.x + 22 * u, base); close() }, tinta)
                for (k in 0 until 5) { val x = c.x - 16 * u + k * 8 * u; drawPath(Path().apply { moveTo(x - 2.5f * u, base - 32 * u); lineTo(x, base - 37 * u); lineTo(x + 2.5f * u, base - 32 * u); close() }, tinta) }
                listOf(-8f, 0f, 8f).forEach { dx -> drawRect(hueco, Offset(c.x + dx * u - 0.8f * u, base - 26 * u), Size(1.6f * u, 7 * u)) }
                arco(c.x - 4 * u, base, 8 * u, 12 * u)
                suelo()
            }
            TemaId.LABORATORIO -> {
                drawRect(tinta, Offset(c.x - 26 * u, base - 18 * u), Size(36 * u, 18 * u))
                for (k in 1 until 6) drawLine(hueco, Offset(c.x - 26 * u + k * 6 * u, base - 18 * u), Offset(c.x - 26 * u + k * 6 * u, base), 0.6f * u)
                drawLine(hueco, Offset(c.x - 26 * u, base - 9 * u), Offset(c.x + 10 * u, base - 9 * u), 0.6f * u)
                drawRect(tinta, Offset(c.x + 12 * u, base - 36 * u), Size(12 * u, 36 * u))
                drawOval(hueco, Offset(c.x + 12 * u, base - 38 * u), Size(12 * u, 4 * u))
                for (k in 0 until 5) drawRect(hueco, Offset(c.x + 14 * u, base - 30 * u + k * 6 * u), Size(8 * u, 1.4f * u))
                suelo()
            }
            TemaId.COLONOS -> {
                drawRect(tinta, Offset(c.x - 20 * u, base - 18 * u), Size(40 * u, 18 * u))
                drawPath(Path().apply { moveTo(c.x - 23 * u, base - 18 * u); lineTo(c.x, base - 28 * u); lineTo(c.x + 23 * u, base - 18 * u); close() }, tinta)
                drawRect(tinta, Offset(c.x - 4 * u, base - 36 * u), Size(8 * u, 10 * u))
                drawPath(Path().apply { moveTo(c.x - 5 * u, base - 36 * u); lineTo(c.x, base - 45 * u); lineTo(c.x + 5 * u, base - 36 * u); close() }, tinta)
                arco(c.x - 2 * u, base - 28 * u, 4 * u, 6 * u)
                drawRect(hueco, Offset(c.x - 3 * u, base - 9 * u), Size(6 * u, 9 * u))
                listOf(-13f, 9f).forEach { dx -> drawRect(hueco, Offset(c.x + dx * u, base - 13 * u), Size(4 * u, 5 * u)) }
                suelo()
            }
            TemaId.NAPOLEON -> {
                drawRect(tinta, Offset(c.x - 24 * u, base - 34 * u), Size(48 * u, 34 * u))
                drawRect(tinta, Offset(c.x - 26 * u, base - 37 * u), Size(52 * u, 3 * u))
                arco(c.x - 8 * u, base, 16 * u, 26 * u)
                drawLine(hueco, Offset(c.x - 24 * u, base - 29 * u), Offset(c.x + 24 * u, base - 29 * u), 0.8f * u)
                listOf(-18f, 14f).forEach { dx -> drawRect(hueco, Offset(c.x + dx * u, base - 22 * u), Size(4 * u, 10 * u), style = Stroke(0.7f * u)) }
                suelo()
            }
            TemaId.BOTANICO -> {
                val cristal = tz.material.copy(alpha = 0.45f)
                listOf(Rect(c.x - 30 * u, base - 18 * u, c.x - 12 * u, base), Rect(c.x + 12 * u, base - 18 * u, c.x + 30 * u, base), Rect(c.x - 14 * u, base - 32 * u, c.x + 14 * u, base)).forEach { r ->
                    val forma = Path().apply { moveTo(r.left, r.bottom); lineTo(r.left, r.top + r.width / 2); arcTo(Rect(r.left, r.top, r.right, r.top + r.width), 180f, 180f, false); lineTo(r.right, r.bottom); close() }
                    drawPath(forma, tinta.copy(alpha = 0.35f)); drawPath(forma, cristal); contorno(tz, forma, tinta, 1.2f * u)
                    var x = r.left + 4 * u
                    while (x < r.right - 1 * u) { drawLine(tinta.copy(alpha = 0.6f), Offset(x, r.bottom), Offset(x, r.top + r.width / 2), 0.6f * u); x += 4 * u }
                }
                suelo()
            }
            TemaId.NAVIDAD -> {
                for (k in 0 until 3) { val an = (40 - k * 10) * u; val y = base - 6 * u - k * 10 * u; drawPath(Path().apply { moveTo(c.x - an / 2, y); lineTo(c.x, y - 16 * u); lineTo(c.x + an / 2, y); close() }, tinta) }
                drawRect(tinta, Offset(c.x - 3 * u, base - 6 * u), Size(6 * u, 6 * u))
                drawPath(estrellaN(Offset(c.x, base - 42 * u), 4.5f * u, 5), Color(0xFFF2C94C))
                listOf(Offset(-10f, -9f), Offset(8f, -11f), Offset(-5f, -20f), Offset(5f, -28f), Offset(-2f, -14f)).forEachIndexed { k, o -> drawCircle(if (k % 2 == 0) Color(0xFFC62828) else hueco, 1.8f * u, Offset(c.x + o.x * u, base + o.y * u)) }
                suelo()
            }
            TemaId.SEDA -> {
                for (k in 0 until 3) {
                    val w = (34 - k * 9) * u; val y = base - k * 11 * u
                    drawRect(tinta, Offset(c.x - w / 2 + 3 * u, y - 9 * u), Size(w - 6 * u, 9 * u))
                    drawPath(Path().apply { moveTo(c.x - w / 2 - 3 * u, y - 9 * u); lineTo(c.x + w / 2 + 3 * u, y - 9 * u); lineTo(c.x + w / 2 - 2 * u, y - 13 * u); lineTo(c.x - w / 2 + 2 * u, y - 13 * u); close() }, tinta)
                }
                raya(tz, tinta, Offset(c.x, base - 35 * u), Offset(c.x, base - 41 * u), 1 * u)
            }
            TemaId.GOTICO -> {
                drawRect(tinta, Offset(c.x - 16 * u, base - 24 * u), Size(32 * u, 24 * u))
                listOf(-12f, 12f).forEach { dx -> drawPath(Path().apply { moveTo(c.x + (dx - 5) * u, base - 24 * u); lineTo(c.x + dx * u, base - 40 * u); lineTo(c.x + (dx + 5) * u, base - 24 * u); close() }, tinta) }
                drawCircle(hueco, 4 * u, Offset(c.x, base - 15 * u))
                for (k in 0 until 6) { val a = k * PI / 3; drawLine(tinta, Offset(c.x, base - 15 * u), Offset(c.x + (4 * u * cos(a)).toFloat(), base - 15 * u + (4 * u * sin(a)).toFloat()), 0.6f * u) }
                drawPath(Path().apply { moveTo(c.x - 3 * u, base); lineTo(c.x - 3 * u, base - 5 * u); lineTo(c.x, base - 9 * u); lineTo(c.x + 3 * u, base - 5 * u); lineTo(c.x + 3 * u, base); close() }, hueco)
            }
            TemaId.ESPACIO -> {
                drawRect(tinta, Offset(c.x - 18 * u, base - 14 * u), Size(36 * u, 14 * u))
                drawArc(tinta, 180f, 180f, true, Offset(c.x - 18 * u, base - 32 * u), Size(36 * u, 36 * u))
                rotate(25f, Offset(c.x, base - 14 * u)) { drawRect(hueco, Offset(c.x - 2.5f * u, base - 32 * u), Size(5 * u, 17 * u)) }
                raya(tz, tinta, Offset(c.x + 2 * u, base - 22 * u), Offset(c.x + 12 * u, base - 38 * u), 3 * u)
                arco(c.x - 3 * u, base, 6 * u, 9 * u)
                suelo()
            }
            // El pecio: el casco de un barco hundido, escorado en el fondo, con sus ojos de buey y el mástil roto.
            TemaId.ABISMO -> {
                rotate(-8f, Offset(c.x, base)) {
                    drawPath(Path().apply { moveTo(c.x - 27 * u, base - 18 * u); lineTo(c.x + 27 * u, base - 18 * u); lineTo(c.x + 20 * u, base - 2 * u); quadraticTo(c.x, base + 2 * u, c.x - 22 * u, base - 4 * u); close() }, tinta)
                    for (k in 0 until 5) drawCircle(hueco, 1.8f * u, Offset(c.x - 18 * u + k * 8.5f * u, base - 11 * u))
                    raya(tz, tinta, Offset(c.x - 4 * u, base - 18 * u), Offset(c.x - 2 * u, base - 36 * u), 2.4f * u)
                    raya(tz, tinta, Offset(c.x - 2 * u, base - 36 * u), Offset(c.x + 8 * u, base - 30 * u), 1.6f * u)
                }
                suelo()
            }
            TemaId.ANDALUS -> {
                drawRect(tinta, Offset(c.x - 22 * u, base - 30 * u), Size(44 * u, 30 * u))
                drawArc(hueco, 180f, 180f, true, Offset(c.x - 9 * u, base - 26 * u), Size(18 * u, 18 * u)); drawRect(hueco, Offset(c.x - 9 * u, base - 17 * u), Size(18 * u, 17 * u))
                drawRect(hueco, Offset(c.x - 13 * u, base - 29 * u), Size(26 * u, 29 * u), style = Stroke(0.8f * u))
                for (k in 0 until 6) drawPath(Path().apply { val x = c.x - 22 * u + k * 8 * u; moveTo(x, base - 30 * u); lineTo(x + 2 * u, base - 33 * u); lineTo(x + 4 * u, base - 33 * u); lineTo(x + 6 * u, base - 30 * u); close() }, tinta)
            }
            TemaId.MONGOL -> {
                drawRect(tinta, Offset(c.x - 22 * u, base - 14 * u), Size(44 * u, 14 * u))
                drawPath(Path().apply { moveTo(c.x - 25 * u, base - 13 * u); quadraticTo(c.x - 12 * u, base - 24 * u, c.x - 3 * u, base - 27 * u); lineTo(c.x + 3 * u, base - 27 * u); quadraticTo(c.x + 12 * u, base - 24 * u, c.x + 25 * u, base - 13 * u); close() }, tinta)
                var x = c.x - 20 * u
                while (x < c.x + 16 * u) { drawLine(hueco, Offset(x, base - 2 * u), Offset(x + 5 * u, base - 12 * u), 0.5f * u); drawLine(hueco, Offset(x + 5 * u, base - 2 * u), Offset(x, base - 12 * u), 0.5f * u); x += 5 * u }
                drawRect(Color(0xFFC2551F), Offset(c.x - 3.5f * u, base - 10 * u), Size(7 * u, 10 * u))
                drawCircle(hueco, 2.4f * u, Offset(c.x, base - 27 * u), style = Stroke(0.8f * u))
                suelo()
            }
            TemaId.RENACIMIENTO -> {
                drawRect(tinta, Offset(c.x - 16 * u, base - 18 * u), Size(32 * u, 18 * u))
                drawPath(Path().apply { moveTo(c.x - 16 * u, base - 18 * u); quadraticTo(c.x - 15 * u, base - 38 * u, c.x, base - 40 * u); quadraticTo(c.x + 15 * u, base - 38 * u, c.x + 16 * u, base - 18 * u); close() }, tinta)
                listOf(-8f, 8f).forEach { dx -> drawPath(Path().apply { moveTo(c.x + dx * u, base - 18 * u); quadraticTo(c.x + dx * 0.8f * u, base - 34 * u, c.x, base - 40 * u) }, hueco, style = Stroke(0.7f * u)) }
                drawRect(tinta, Offset(c.x - 2.5f * u, base - 45 * u), Size(5 * u, 5 * u))
                drawPath(Path().apply { moveTo(c.x - 2.5f * u, base - 45 * u); lineTo(c.x, base - 48 * u); lineTo(c.x + 2.5f * u, base - 45 * u); close() }, tinta)
                listOf(-9f, 0f, 9f).forEach { dx -> drawCircle(hueco, 2 * u, Offset(c.x + dx * u, base - 10 * u)) }
                suelo()
            }
            TemaId.URSS -> {
                listOf(Triple(48f, 0f, 14f), Triple(30f, 14f, 14f), Triple(16f, 28f, 10f)).forEach { (an, y0, alto) -> drawRect(tinta, Offset(c.x - an / 2 * u, base - (y0 + alto) * u), Size(an * u, alto * u)) }
                raya(tz, tinta, Offset(c.x, base - 38 * u), Offset(c.x, base - 44 * u), 1.4f * u)
                drawPath(estrellaN(Offset(c.x, base - 46 * u), 3.2f * u, 5), Color(0xFFD32F2F))
                for (k in 0 until 6) drawRect(hueco, Offset(c.x - 20 * u + k * 7 * u, base - 10 * u), Size(2 * u, 6 * u))
                for (k in 0 until 4) drawRect(hueco, Offset(c.x - 12 * u + k * 7 * u, base - 24 * u), Size(2 * u, 6 * u))
                suelo()
            }
            TemaId.REVOLUCION -> {
                drawRect(tinta, Offset(c.x - 14 * u, base - 24 * u), Size(28 * u, 24 * u))
                listOf(-20f, 20f).forEach { dx ->
                    drawRoundRect(tinta, Offset(c.x + (dx - 6) * u, base - 32 * u), Size(12 * u, 32 * u), CornerRadius(5 * u, 2 * u))
                    for (k in 0 until 3) drawRect(tinta, Offset(c.x + (dx - 6) * u + k * 4.5f * u, base - 35 * u), Size(3 * u, 3 * u))
                }
                arco(c.x - 4 * u, base, 8 * u, 13 * u)
                raya(tz, tinta, Offset(c.x - 20 * u, base - 35 * u), Offset(c.x - 20 * u, base - 43 * u), 0.8f * u)
                listOf(Color(0xFF1F3A8A), Color.White, Color(0xFFC62828)).forEachIndexed { k, col -> drawRect(col, Offset(c.x - 20 * u + k * 2.5f * u, base - 43 * u), Size(2.5f * u, 4 * u)) }
                suelo()
            }
            TemaId.ANTIGUAS -> {
                fun trilito(x0: Float, x1: Float, alto: Float, an: Float) {
                    drawPath(caja(x0, base - alto, x0 + an, base, 1.2f * u), tinta); drawPath(caja(x1 - an, base - alto, x1, base, 1.2f * u), tinta)
                    drawPath(caja(x0 - 2 * u, base - alto - 5 * u, x1 + 2 * u, base - alto + 0.5f * u, 1.5f * u), tinta)
                }
                trilito(c.x - 20 * u, c.x + 2 * u, 26 * u, 6 * u); trilito(c.x + 7 * u, c.x + 24 * u, 18 * u, 5 * u)
                drawPath(caja(c.x - 28 * u, base - 14 * u, c.x - 24 * u, base, 1.5f * u), tinta.copy(alpha = 0.7f))
                drawLine(hueco, Offset(c.x - 17 * u, base - 18 * u), Offset(c.x - 17 * u, base - 10 * u), 0.7f * u); drawLine(hueco, Offset(c.x - 17 * u, base - 16 * u), Offset(c.x - 15 * u, base - 18 * u), 0.7f * u)
                suelo()
            }
        }
    }

    // ---------------------------------------------------------------- estandarte

    /**
     * El estandarte de las campañas, con paño y remate propios: vexillum con flecos, paño con el sol de Vergina,
     * gonfalón de colas, oriflama, pendón de punta de escudo, bandera de águila, tricolor con pica, paño ondeante con
     * estrella, pancarta colgada, bandera de píxeles, paño de punta redonda, banderín de medición, bandera sostenida
     * por una barra, cola de golondrina, filacteria, guirnalda de banderines, estandarte vertical, paño con borlas y
     * yamur, tug de crines, lambrequín con roeles y paño raído con runa.
     */
    private fun DrawScope.estandarte(c: Offset, u: Float, tema: TemaId, tinta: Color, tono: Color, tz: Trazo) {
        val px = c.x - 14 * u; val arriba = c.y - 24 * u
        val claro = tz.material
        fun asta(x: Float = px, y0: Float = c.y - 26 * u) = raya(tz, tinta, Offset(x, c.y + 26 * u), Offset(x, y0), 2 * u)
        fun pano(p: Path, col: Color = tono) { drawPath(p, col); contorno(tz, p, tinta, 0.9f * u) }
        fun travesano(x0: Float, x1: Float) = raya(tz, tinta, Offset(x0 - 2 * u, arriba), Offset(x1 + 2 * u, arriba), 1.6f * u)
        when (tema) {
            TemaId.ROMANO -> {
                val x0 = c.x - 14 * u; val x1 = c.x + 14 * u
                asta(c.x, c.y - 30 * u); travesano(x0, x1)
                pano(caja(x0, arriba, x1, c.y + 6 * u))
                for (k in 0 until 9) { val x = x0 + 1.5f * u + k * 3.25f * u; drawLine(tinta, Offset(x, c.y + 6 * u), Offset(x, c.y + 10 * u), 0.8f * u) }
                drawPath(Path().apply { moveTo(c.x, c.y - 36 * u); lineTo(c.x + 2.5f * u, c.y - 30 * u); lineTo(c.x - 2.5f * u, c.y - 30 * u); close() }, tinta)
                letra("SPQR", Offset(c.x, c.y - 9 * u), 6.5f * u, claro)
            }
            TemaId.CIBER -> {
                raya(tz, tinta, Offset(px, c.y + 26 * u), Offset(px, c.y - 26 * u), 2 * u)
                val q = 4 * u
                for (col in 0 until 8) {
                    val dy = if (col % 4 == 1 || col % 4 == 2) -q / 2 else 0f
                    for (fil in 0 until 5) drawRect(if ((col + fil) % 2 == 0) tono else tono.copy(alpha = 0.7f), Offset(px + col * q, arriba + fil * q + dy), Size(q - 0.6f * u, q - 0.6f * u))
                }
                drawRect(tinta, Offset(px - 2 * u, c.y - 30 * u), Size(4 * u, 4 * u))
            }
            // La cortina del puesto: tela partida en tiras que cuelga de una barra, con un farolito de papel al lado.
            TemaId.NEON -> {
                asta()
                travesano(px, px + 28 * u)
                for (k in 0 until 4) pano(caja(px + 1 * u + k * 7 * u, arriba, px + 7 * u + k * 7 * u, c.y + 8 * u))
                letra("麺", Offset(px + 14 * u, c.y - 10 * u), 11 * u, claro)
                drawOval(Color(0xFFFF6B5E), Offset(px + 30 * u, arriba + 6 * u), Size(7 * u, 10 * u))
                drawLine(tinta, Offset(px + 33.5f * u, arriba), Offset(px + 33.5f * u, arriba + 6 * u), 0.8f * u)
            }
            TemaId.HELENO -> {
                asta()
                pano(caja(px, arriba, c.x + 20 * u, c.y + 2 * u))
                val s = Offset(c.x + 3 * u, c.y - 11 * u)
                for (k in 0 until 16) { val a = k * PI / 8; drawLine(claro, s, Offset(s.x + (7 * u * cos(a)).toFloat(), s.y + (7 * u * sin(a)).toFloat()), 0.8f * u) }
                drawCircle(claro, 2.5f * u, s)
                drawPath(Path().apply { moveTo(px, c.y - 33 * u); quadraticTo(px + 3 * u, c.y - 29 * u, px, c.y - 25 * u); quadraticTo(px - 3 * u, c.y - 29 * u, px, c.y - 33 * u); close() }, tinta)
            }
            // El mástil del pilono con su banderola de dos puntas, y el ankh pintado en ella.
            TemaId.EGIPTO -> {
                asta()
                pano(Path().apply { moveTo(px, arriba); lineTo(c.x + 16 * u, arriba + 3 * u); lineTo(c.x + 11 * u, arriba + 9 * u); lineTo(c.x + 16 * u, arriba + 15 * u); lineTo(px, arriba + 18 * u); close() })
                signoEgipcio(0, Offset(c.x + 1 * u, arriba + 9 * u), 12 * u, claro, 1.2f * u)
                drawPath(Path().apply { moveTo(px - 2.5f * u, c.y - 26 * u); lineTo(px, c.y - 31 * u); lineTo(px + 2.5f * u, c.y - 26 * u); close() }, tinta)
            }
            TemaId.MEDIEVAL -> {
                val x0 = c.x - 14 * u; val x1 = c.x + 14 * u
                asta(c.x, c.y - 30 * u); travesano(x0, x1)
                pano(Path().apply {
                    moveTo(x0, arriba); lineTo(x1, arriba); lineTo(x1, c.y + 14 * u); lineTo(c.x + 9.3f * u, c.y + 2 * u); lineTo(c.x + 4.7f * u, c.y + 14 * u); lineTo(c.x, c.y + 2 * u)
                    lineTo(c.x - 4.7f * u, c.y + 14 * u); lineTo(c.x - 9.3f * u, c.y + 2 * u); lineTo(x0, c.y + 14 * u); close()
                })
                drawLine(tinta, Offset(c.x, c.y - 36 * u), Offset(c.x, c.y - 30 * u), 1.4f * u); drawLine(tinta, Offset(c.x - 3 * u, c.y - 33.5f * u), Offset(c.x + 3 * u, c.y - 33.5f * u), 1.4f * u)
                drawCircle(claro, 4 * u, Offset(c.x, c.y - 10 * u), style = Stroke(1.2f * u))
            }
            // El «baucent»: negro arriba y blanco abajo, colgado de su travesaño, con la cruz roja en la parte blanca.
            TemaId.TEMPLARIO -> {
                val x0 = c.x - 14 * u; val x1 = c.x + 14 * u
                asta(c.x, c.y - 30 * u); travesano(x0, x1)
                pano(caja(x0, arriba, x1, c.y + 16 * u), claro)
                drawRect(tinta, Offset(x0, arriba), Size(x1 - x0, (c.y + 16 * u - arriba) / 2))
                drawPath(Path().apply { moveTo(c.x, c.y - 36 * u); lineTo(c.x + 2.5f * u, c.y - 30 * u); lineTo(c.x - 2.5f * u, c.y - 30 * u); close() }, tinta)
                drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(c.x, c.y + 6 * u), 6 * u), tono)
            }
            TemaId.DISTOPIA -> {
                raya(tz, tinta, Offset(c.x - 16 * u, c.y - 28 * u), Offset(c.x + 16 * u, c.y - 28 * u), 2 * u)
                listOf(-14f, 14f).forEach { dx -> drawLine(tinta, Offset(c.x + dx * u, c.y - 28 * u), Offset(c.x + dx * u * 0.85f, c.y - 24 * u), 0.8f * u) }
                pano(caja(c.x - 12 * u, c.y - 24 * u, c.x + 12 * u, c.y + 24 * u))
                val o = Offset(c.x, c.y - 6 * u)
                drawPath(Path().apply { moveTo(o.x - 9 * u, o.y); quadraticTo(o.x, o.y - 8 * u, o.x + 9 * u, o.y); quadraticTo(o.x, o.y + 8 * u, o.x - 9 * u, o.y); close() }, claro)
                drawCircle(tinta, 3 * u, o)
                for (k in 0 until 3) drawLine(claro, Offset(c.x - 8 * u, c.y + 8 * u + k * 4 * u), Offset(c.x + 8 * u, c.y + 8 * u + k * 4 * u), 1.2f * u)
            }
            TemaId.REFUGIO -> {
                asta()
                pano(Path().apply { moveTo(px, arriba); lineTo(c.x + 8 * u, arriba); arcTo(Rect(c.x - 2 * u, arriba, c.x + 18 * u, arriba + 22 * u), -90f, 180f, false); lineTo(px, arriba + 22 * u); close() })
                val g = Offset(c.x + 1 * u, arriba + 11 * u)
                drawPath(engrane(g, 7 * u, 5.5f * u, 10), claro); drawCircle(tono, 2.5f * u, g)
                drawCircle(tinta, 2.4f * u, Offset(px, c.y - 28 * u))
            }
            TemaId.ORDEN -> {
                val x0 = c.x - 13 * u; val x1 = c.x + 13 * u
                asta(c.x, c.y - 30 * u); travesano(x0, x1)
                pano(Path().apply { moveTo(x0, arriba); lineTo(x1, arriba); lineTo(x1, c.y + 2 * u); lineTo(c.x, c.y + 14 * u); lineTo(x0, c.y + 2 * u); close() })
                drawPath(engrane(Offset(c.x, c.y - 9 * u), 7 * u, 5.5f * u, 8), claro); drawCircle(tono, 2.4f * u, Offset(c.x, c.y - 9 * u))
                drawPath(Path().apply { moveTo(c.x, c.y - 37 * u); lineTo(c.x + 2 * u, c.y - 30 * u); lineTo(c.x - 2 * u, c.y - 30 * u); close() }, tinta)
            }
            TemaId.LABORATORIO -> {
                asta()
                pano(Path().apply { moveTo(px, arriba); lineTo(c.x + 22 * u, arriba + 10 * u); lineTo(px, arriba + 20 * u); close() })
                drawPath(Path().apply { moveTo(px + 2 * u, arriba + 13 * u); lineTo(px + 7 * u, arriba + 13 * u); lineTo(px + 10 * u, arriba + 5 * u); lineTo(px + 13 * u, arriba + 15 * u); lineTo(px + 16 * u, arriba + 10 * u); lineTo(px + 22 * u, arriba + 10 * u) }, claro, style = Stroke(1.1f * u))
                drawCircle(tinta, 1.6f * u, Offset(px, c.y - 27 * u), style = Stroke(1 * u))
            }
            TemaId.COLONOS -> {
                asta()
                pano(Path().apply { moveTo(px, arriba); lineTo(c.x + 22 * u, arriba); lineTo(c.x + 12 * u, arriba + 10 * u); lineTo(c.x + 22 * u, arriba + 20 * u); lineTo(px, arriba + 20 * u); close() })
                for (k in 0 until 2) drawLine(claro, Offset(px + 2 * u, arriba + (7 + k * 6) * u), Offset(c.x + 8 * u, arriba + (7 + k * 6) * u), 1.4f * u)
                drawCircle(tinta, 2.4f * u, Offset(px, c.y - 28 * u))
            }
            TemaId.NAPOLEON -> {
                asta()
                pano(caja(px, arriba, c.x + 18 * u, c.y + 6 * u))
                val n = Offset(c.x + 2 * u, c.y - 9 * u)
                drawCircle(claro, 7 * u, n, style = Stroke(1.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1 * u))))
                letra("N", n, 8 * u, claro)
                drawPath(Path().apply { moveTo(px, c.y - 27 * u); lineTo(px - 6 * u, c.y - 32 * u); lineTo(px - 2 * u, c.y - 29 * u); lineTo(px, c.y - 33 * u); lineTo(px + 2 * u, c.y - 29 * u); lineTo(px + 6 * u, c.y - 32 * u); close() }, tinta)
            }
            TemaId.BOTANICO -> {
                asta()
                pano(Path().apply { moveTo(px, arriba); cubicTo(px + 12 * u, arriba + 4 * u, c.x + 8 * u, arriba - 4 * u, c.x + 20 * u, arriba + 4 * u); lineTo(c.x + 20 * u, arriba + 12 * u); cubicTo(c.x + 8 * u, arriba + 4 * u, px + 12 * u, arriba + 12 * u, px, arriba + 8 * u); close() })
                drawArc(tono, 90f, 270f, false, Offset(c.x + 16 * u, arriba + 10 * u), Size(8 * u, 8 * u), style = Stroke(2.4f * u))
                drawPath(Path().apply { moveTo(px, c.y - 36 * u); quadraticTo(px + 5 * u, c.y - 31 * u, px, c.y - 26 * u); quadraticTo(px - 5 * u, c.y - 31 * u, px, c.y - 36 * u); close() }, Color(0xFF5C7A3A))
            }
            TemaId.NAVIDAD -> {
                asta()
                val p0 = Offset(px, arriba); val p1 = Offset(c.x + 4 * u, arriba + 14 * u); val p2 = Offset(c.x + 24 * u, arriba + 6 * u)
                drawPath(Path().apply { moveTo(p0.x, p0.y); quadraticTo(p1.x, p1.y, p2.x, p2.y) }, tinta, style = Stroke(0.8f * u))
                val cols = listOf(Color(0xFFC62828), Color(0xFF2E6B3A), Color(0xFFF2C94C))
                for (k in 0 until 5) {
                    val t = (k + 0.5f) / 5f
                    val x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x
                    val y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y
                    val b = Path().apply { moveTo(x - 3 * u, y); lineTo(x + 3 * u, y); lineTo(x, y + 8 * u); close() }
                    drawPath(b, cols[k % 3]); contorno(tz, b, tinta, 0.5f * u)
                }
                drawPath(estrellaN(Offset(px, c.y - 29 * u), 3.5f * u, 5), Color(0xFFF2C94C))
            }
            TemaId.SEDA -> {
                asta(); raya(tz, tinta, Offset(px, arriba), Offset(px + 16 * u, arriba), 1.4f * u)
                pano(caja(px, arriba, px + 16 * u, c.y + 22 * u))
                for (k in 0 until 4) {
                    val y = arriba + (6 + k * 10) * u
                    drawLine(claro, Offset(px + 4 * u, y), Offset(px + 12 * u, y), 1.3f * u); drawLine(claro, Offset(px + 8 * u, y - 2.5f * u), Offset(px + 8 * u, y + 3.5f * u), 1.3f * u)
                }
                drawCircle(Color(0xFFF2C94C), 2.6f * u, Offset(px, c.y - 28 * u))
            }
            TemaId.GOTICO -> {
                asta()
                pano(Path().apply {
                    moveTo(px, arriba); cubicTo(px + 14 * u, arriba - 2 * u, px + 26 * u, arriba + 4 * u, c.x + 26 * u, arriba + 2 * u)
                    quadraticTo(c.x + 12 * u, arriba + 8 * u, c.x + 22 * u, arriba + 14 * u); cubicTo(c.x + 8 * u, arriba + 14 * u, px + 10 * u, arriba + 16 * u, px, arriba + 20 * u); close()
                })
                listOf(Offset(0f, -2.4f), Offset(-2f, 0.6f), Offset(2f, 0.6f)).forEach { o -> drawCircle(tinta, 1.6f * u, Offset(px + o.x * u, c.y - 31 * u + o.y * u)) }
            }
            TemaId.ESPACIO -> {
                asta()
                raya(tz, tinta, Offset(px, arriba), Offset(c.x + 22 * u, arriba), 1.4f * u)
                pano(caja(px, arriba, c.x + 22 * u, arriba + 20 * u))
                drawOval(claro, Offset(c.x - 6 * u, arriba + 6 * u), Size(20 * u, 8 * u), style = Stroke(1 * u)); drawCircle(claro, 2.6f * u, Offset(c.x + 4 * u, arriba + 10 * u))
            }
            // La bandera de buzo abajo: roja con la franja blanca en diagonal.
            TemaId.ABISMO -> {
                asta()
                val p = caja(px, arriba, c.x + 22 * u, arriba + 22 * u)
                pano(p, Color(0xFFC62828))
                clipPath(p) { drawLine(Color.White, Offset(px, arriba), Offset(c.x + 22 * u, arriba + 22 * u), 5 * u) }
                contorno(tz, p, tinta, 0.9f * u)
            }
            TemaId.ANDALUS -> {
                asta()
                pano(caja(px, arriba, c.x + 20 * u, arriba + 22 * u))
                listOf(4f, 18f).forEach { dy -> drawRect(claro, Offset(px + 1 * u, arriba + dy * u - 1.5f * u), Size(33 * u, 3 * u)) }
                drawPath(estrellaN(Offset(c.x + 3 * u, arriba + 11 * u), 5 * u, 8, 0.7f), claro)
                listOf(arriba, arriba + 22 * u).forEach { y -> drawLine(tinta, Offset(c.x + 20 * u, y), Offset(c.x + 20 * u, y + 4 * u), 0.9f * u); drawCircle(tono, 1.6f * u, Offset(c.x + 20 * u, y + 5 * u)) }
                listOf(2.4f, 1.9f, 1.4f).forEachIndexed { i, rr -> drawCircle(tinta, rr * u, Offset(px, c.y - 27 * u - i * 4.4f * u)) }
            }
            TemaId.MONGOL -> {
                raya(tz, tinta, Offset(c.x, c.y + 26 * u), Offset(c.x, c.y - 30 * u), 2 * u)
                listOf(-5f, 0f, 5f).forEach { dx -> drawLine(tinta, Offset(c.x + dx * u, c.y - 26 * u), Offset(c.x + dx * u, c.y - (if (dx == 0f) 36 else 32) * u), 1.4f * u) }
                drawLine(tinta, Offset(c.x - 5 * u, c.y - 26 * u), Offset(c.x + 5 * u, c.y - 26 * u), 1.4f * u)
                drawOval(tono, Offset(c.x - 6 * u, c.y - 25 * u), Size(12 * u, 5 * u))
                for (k in 0 until 13) { val x = c.x - 9 * u + k * 1.5f * u; val largo = (16 + (k * 5) % 7) * u; drawLine(tono, Offset(x * 0.4f + c.x * 0.6f, c.y - 22 * u), Offset(x, c.y - 22 * u + largo), 1.2f * u, StrokeCap.Round) }
            }
            TemaId.RENACIMIENTO -> {
                val x0 = c.x - 14 * u; val x1 = c.x + 14 * u
                asta(c.x, c.y - 30 * u); travesano(x0, x1)
                pano(Path().apply {
                    moveTo(x0, arriba); lineTo(x1, arriba); lineTo(x1, c.y + 4 * u)
                    for (k in 0 until 4) { val xa = x1 - k * 7 * u; quadraticTo(xa - 3.5f * u, c.y + 11 * u, xa - 7 * u, c.y + 4 * u) }
                    close()
                })
                listOf(Offset(-5f, -16f), Offset(5f, -16f), Offset(-7f, -9f), Offset(7f, -9f), Offset(-3f, -2f), Offset(3f, -2f)).forEach { o -> drawCircle(claro, 2.2f * u, Offset(c.x + o.x * u, c.y + o.y * u)) }
                drawCircle(tinta, 2.4f * u, Offset(c.x, c.y - 31 * u))
            }
            TemaId.URSS -> {
                asta()
                pano(Path().apply {
                    moveTo(px, arriba); cubicTo(px + 10 * u, arriba - 4 * u, px + 22 * u, arriba + 4 * u, c.x + 20 * u, arriba); lineTo(c.x + 20 * u, arriba + 22 * u)
                    cubicTo(px + 22 * u, arriba + 26 * u, px + 10 * u, arriba + 18 * u, px, arriba + 22 * u); close()
                }, Color(0xFFC62828))
                drawPath(estrellaN(Offset(px + 7 * u, arriba + 6 * u), 3.5f * u, 5), Color(0xFFFFD54F))
                drawPath(estrellaN(Offset(px, c.y - 30 * u), 3.5f * u, 5), tinta)
            }
            TemaId.REVOLUCION -> {
                asta()
                val an = 32 * u / 3
                listOf(Color(0xFF1F3A8A), Color.White, Color(0xFFC62828)).forEachIndexed { i, k -> drawRect(k, Offset(px + i * an, arriba), Size(an, 22 * u)) }
                contorno(tz, caja(px, arriba, px + 32 * u, arriba + 22 * u), tinta, 0.9f * u)
                drawPath(Path().apply { moveTo(px, c.y - 34 * u); lineTo(px + 2 * u, c.y - 27 * u); lineTo(px - 2 * u, c.y - 27 * u); close() }, tinta)
                drawPath(Path().apply { moveTo(px, c.y - 27 * u); quadraticTo(px - 6 * u, c.y - 24 * u, px - 4 * u, c.y - 18 * u) }, Color(0xFFC62828), style = Stroke(1.4f * u))
            }
            TemaId.ANTIGUAS -> {
                asta()
                pano(Path().apply {
                    moveTo(px, arriba); lineTo(c.x + 18 * u, arriba); lineTo(c.x + 18 * u, arriba + 18 * u); lineTo(c.x + 14 * u, arriba + 24 * u); lineTo(c.x + 10 * u, arriba + 19 * u)
                    lineTo(c.x + 5 * u, arriba + 27 * u); lineTo(c.x, arriba + 20 * u); lineTo(c.x - 5 * u, arriba + 25 * u); lineTo(c.x - 9 * u, arriba + 19 * u); lineTo(px, arriba + 23 * u); close()
                })
                val r0 = Offset(c.x + 2 * u, arriba + 4 * u)
                drawLine(claro, r0, Offset(r0.x, r0.y + 14 * u), 1.6f * u); drawLine(claro, Offset(r0.x, r0.y + 3 * u), Offset(r0.x + 5 * u, r0.y), 1.6f * u); drawLine(claro, Offset(r0.x, r0.y + 7 * u), Offset(r0.x - 5 * u, r0.y + 3 * u), 1.6f * u)
                drawPath(Path().apply { moveTo(px, c.y - 35 * u); lineTo(px + 3 * u, c.y - 29 * u); lineTo(px, c.y - 26 * u); lineTo(px - 3 * u, c.y - 29 * u); close() }, tinta)
            }
        }
    }

    // ---------------------------------------------------------------- moneda

    /**
     * La moneda de los gastos en cada tema: canto de perlas con S·C, chip de consola, flan irregular con cuadrado incuso,
     * dinero con cruz larga, ficha de racionamiento, chapa de botella, ficha dentada, ficha con el símbolo del oro, canto
     * estriado con estrellas, veinte francos con laurel, hoja grabada, moneda de chocolate, moneda de agujero cuadrado,
     * cuadrifolio, ficha de créditos, dobla de cuadrado inscrito, paiza, florín con lirio, moneda con espigas, asignado
     * de papel y pieza tosca con gema.
     */
    private fun DrawScope.moneda(c: Offset, u: Float, tema: TemaId, tinta: Color, tono: Color, tz: Trazo) {
        val R = 18 * u; val claro = tz.material
        fun en(a: Double, k: Float) = Offset(c.x + (R * k * cos(a)).toFloat(), c.y + (R * k * sin(a)).toFloat())
        fun disco(r: Float = R) { drawCircle(tono.copy(alpha = 0.45f), r, c); contorno(tz, circulo(c, r), tinta, 1.6f * u) }
        fun forma(p: Path, ancho: Float = 1.6f * u) { drawPath(p, tono.copy(alpha = 0.45f)); contorno(tz, p, tinta, ancho) }
        fun irregular(n: Int, salto: Float) = Path().apply {
            for (i in 0 until n) {
                val a = i * 2 * PI / n; val rr = R * (1f - salto * ((i * 7) % 3) / 2f)
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        when (tema) {
            TemaId.ROMANO -> { disco(); for (i in 0 until 32) drawCircle(tinta, 0.8f * u, en(i * PI / 16, 0.85f)); letra("S·C", c, 9 * u, tinta) }
            TemaId.CIBER -> {
                val k = 5 * u
                forma(Path().apply { moveTo(c.x - R + k, c.y - R); lineTo(c.x + R - k, c.y - R); lineTo(c.x + R, c.y - R + k); lineTo(c.x + R, c.y + R - k); lineTo(c.x + R - k, c.y + R); lineTo(c.x - R + k, c.y + R); lineTo(c.x - R, c.y + R - k); lineTo(c.x - R, c.y - R + k); close() })
                for (j in 0 until 4) { val y = c.y - 9 * u + j * 6 * u; drawLine(tinta, Offset(c.x - R, y), Offset(c.x - R - 3 * u, y), 1.2f * u); drawLine(tinta, Offset(c.x + R, y), Offset(c.x + R + 3 * u, y), 1.2f * u) }
                letra("0x01", c, 7 * u, tinta)
            }
            // La ficha de la máquina del callejón: disco con el agujero cuadrado y el signo del crédito.
            TemaId.NEON -> {
                disco()
                drawCircle(tinta, R * 0.82f, c, style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u))))
                drawRect(tinta, Offset(c.x - 3 * u, c.y - 11 * u), Size(6 * u, 6 * u))
                letra("¥", Offset(c.x, c.y + 5 * u), 11 * u, tinta)
            }
            TemaId.HELENO -> {
                forma(irregular(24, 0.06f))
                drawRect(tinta, Offset(c.x - 9 * u, c.y - 9 * u), Size(18 * u, 18 * u), style = Stroke(1 * u))
                letra("Δ", c, 12 * u, tinta)
            }
            // El deben: Egipto no tuvo monedas; se pesaba en anillos de cobre. Este lleva tres rayas de peso.
            TemaId.EGIPTO -> {
                drawCircle(tono.copy(alpha = 0.45f), R, c, style = Stroke(9 * u))
                contorno(tz, circulo(c, R + 4.5f * u), tinta, 1.2f * u); contorno(tz, circulo(c, R - 4.5f * u), tinta, 1.2f * u)
                for (k in 0 until 3) drawLine(tinta, Offset(c.x - 3 * u + k * 3 * u, c.y - 4 * u), Offset(c.x - 3 * u + k * 3 * u, c.y + 4 * u), 1.2f * u)
            }
            TemaId.MEDIEVAL -> {
                forma(irregular(18, 0.07f), 1.4f * u)
                listOf(-1.2f, 1.2f).forEach { d -> drawLine(tinta, Offset(c.x + d * u, c.y - R), Offset(c.x + d * u, c.y + R), 0.9f * u); drawLine(tinta, Offset(c.x - R, c.y + d * u), Offset(c.x + R, c.y + d * u), 0.9f * u) }
                listOf(Offset(1f, 1f), Offset(-1f, 1f), Offset(1f, -1f), Offset(-1f, -1f)).forEach { s -> for (k in 0 until 3) drawCircle(tinta, 1.2f * u, Offset(c.x + s.x * (6 + (k % 2) * 3) * u, c.y + s.y * (6 + (k / 2) * 3) * u)) }
            }
            // El dinero de la casa: la cruz patada al centro y la gráfila de perlas.
            TemaId.TEMPLARIO -> {
                disco()
                drawCircle(tinta, R * 0.78f, c, style = Stroke(0.8f * u))
                for (i in 0 until 24) drawCircle(tinta, 0.9f * u, en(i * PI / 12, 0.88f))
                drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(c, R * 0.55f), tinta)
            }
            TemaId.DISTOPIA -> {
                forma(caja(c.x - 16 * u, c.y - 16 * u, c.x + 16 * u, c.y + 16 * u, 4 * u))
                drawCircle(claro, 3 * u, Offset(c.x, c.y - 10 * u)); drawCircle(tinta, 3 * u, Offset(c.x, c.y - 10 * u), style = Stroke(0.8f * u))
                letra("Nº 07", Offset(c.x, c.y + 2 * u), 7 * u, tinta)
                drawLine(tinta, Offset(c.x - 10 * u, c.y + 10 * u), Offset(c.x + 10 * u, c.y + 10 * u), 1 * u)
            }
            TemaId.REFUGIO -> {
                forma(estrellaN(c, R * 1.08f, 21, 0.88f), 1.2f * u)
                drawCircle(claro, R * 0.72f, c); drawCircle(tinta, R * 0.72f, c, style = Stroke(0.8f * u))
                drawPath(estrellaN(c, R * 0.42f, 5), tono)
            }
            TemaId.ORDEN -> {
                forma(engrane(c, R, R * 0.82f, 14), 1.2f * u)
                drawCircle(claro, 5 * u, c); drawCircle(tinta, 5 * u, c, style = Stroke(1 * u))
                for (i in 0 until 4) drawCircle(tinta, 1.3f * u, en(PI / 4 + i * PI / 2, 0.5f))
            }
            TemaId.LABORATORIO -> {
                disco()
                drawRect(claro, Offset(c.x - 9 * u, c.y - 9 * u), Size(18 * u, 18 * u)); drawRect(tinta, Offset(c.x - 9 * u, c.y - 9 * u), Size(18 * u, 18 * u), style = Stroke(1 * u))
                letra("Au", Offset(c.x, c.y + 1.5f * u), 9 * u, tinta); letra("79", Offset(c.x - 5 * u, c.y - 6 * u), 3.6f * u, tinta, false)
            }
            TemaId.COLONOS -> {
                disco()
                for (i in 0 until 60) { val a = i * PI / 30; drawLine(tinta, en(a, 0.9f), en(a, 1f), 0.6f * u) }
                for (i in 0 until 7) drawPath(estrellaN(en(-PI / 2 + (i - 3) * 0.32, 0.72f), 1.6f * u, 5), tinta)
                letra("1", Offset(c.x, c.y + 2 * u), 14 * u, tinta)
            }
            TemaId.NAPOLEON -> {
                disco()
                listOf(-1f, 1f).forEach { s -> for (k in 0 until 7) { val a = PI / 2 + s * (0.3 + k * 0.33); val p = en(a, 0.78f); rotate((a * 180 / PI).toFloat() + 90f, p) { drawOval(tinta, Offset(p.x - 1.2f * u, p.y - 2.6f * u), Size(2.4f * u, 5.2f * u)) } } }
                letra("20", Offset(c.x, c.y - 1 * u), 10 * u, tinta); letra("FR", Offset(c.x, c.y + 8 * u), 4.5f * u, tinta)
            }
            TemaId.BOTANICO -> {
                disco()
                val hoja = Path().apply { moveTo(c.x - 10 * u, c.y + 8 * u); quadraticTo(c.x - 8 * u, c.y - 10 * u, c.x + 10 * u, c.y - 10 * u); quadraticTo(c.x + 8 * u, c.y + 8 * u, c.x - 10 * u, c.y + 8 * u); close() }
                drawPath(hoja, claro); contorno(tz, hoja, tinta, 0.9f * u)
                drawLine(tinta, Offset(c.x - 10 * u, c.y + 8 * u), Offset(c.x + 6 * u, c.y - 6 * u), 0.8f * u)
            }
            TemaId.NAVIDAD -> {
                val p = Path().apply {
                    for (i in 0 until 48) { val a = i * PI / 24; val rr = R * (if (i % 2 == 0) 1f else 0.95f); val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y) }
                    close()
                }
                drawPath(p, Color(0xFFF2C94C).copy(alpha = 0.7f)); contorno(tz, p, tinta, 1.2f * u)
                drawPath(estrellaN(c, 9 * u, 5), Color(0xFFC62828))
            }
            TemaId.SEDA -> {
                disco()
                drawRect(claro, Offset(c.x - 5 * u, c.y - 5 * u), Size(10 * u, 10 * u)); drawRect(tinta, Offset(c.x - 5 * u, c.y - 5 * u), Size(10 * u, 10 * u), style = Stroke(1.2f * u))
                for (i in 0 until 4) { val p = en(-PI / 2 + i * PI / 2, 0.62f); drawLine(tinta, Offset(p.x - 2 * u, p.y), Offset(p.x + 2 * u, p.y), 1 * u); drawLine(tinta, Offset(p.x, p.y - 2 * u), Offset(p.x, p.y + 2 * u), 1 * u) }
            }
            TemaId.GOTICO -> {
                disco()
                listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { d -> drawCircle(tinta, 5.5f * u, Offset(c.x + d.x * 5.5f * u, c.y + d.y * 5.5f * u), style = Stroke(1 * u)) }
                drawCircle(tono, 3 * u, c)
            }
            TemaId.ESPACIO -> {
                drawCircle(tono.copy(alpha = 0.35f), R, c); contorno(tz, circulo(c, R), tinta, 1.4f * u); contorno(tz, circulo(c, R * 0.8f), tinta, 0.8f * u)
                drawCircle(tinta, 5 * u, c); drawOval(tinta, Offset(c.x - 11 * u, c.y - 3.5f * u), Size(22 * u, 7 * u), style = Stroke(1 * u))
                drawCircle(claro, 1.6f * u, Offset(c.x + 9 * u, c.y - 2 * u))
            }
            // Una perla en su concha abierta: las valvas acanaladas y la perla con su brillo.
            TemaId.ABISMO -> {
                val concha = Path().apply {
                    moveTo(c.x - R, c.y + 2 * u); quadraticTo(c.x - R * 0.9f, c.y - R * 0.9f, c.x, c.y - R); quadraticTo(c.x + R * 0.9f, c.y - R * 0.9f, c.x + R, c.y + 2 * u)
                    quadraticTo(c.x, c.y + R * 0.4f, c.x - R, c.y + 2 * u); close()
                }
                forma(concha, 1.4f * u)
                for (k in -3..3) fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x + k * R * 0.12f, c.y - R * 0.1f), en(-PI / 2 + k * 0.32, 0.86f), 0.8f * u)
                drawCircle(claro, 6 * u, Offset(c.x, c.y + 6 * u)); contorno(tz, circulo(Offset(c.x, c.y + 6 * u), 6 * u), tinta, 0.9f * u)
                drawCircle(Color.White.copy(alpha = 0.8f), 1.6f * u, Offset(c.x - 2 * u, c.y + 4 * u))
            }
            TemaId.ANDALUS -> {
                disco()
                drawRect(tinta, Offset(c.x - 9 * u, c.y - 9 * u), Size(18 * u, 18 * u), style = Stroke(1.2f * u))
                for (k in 0 until 3) fina(tz, tinta, Offset(c.x - 6 * u, c.y - 4 * u + k * 4 * u), Offset(c.x + 6 * u, c.y - 4 * u + k * 4 * u), 0.9f * u)
                for (i in 0 until 4) drawCircle(tinta, 1.2f * u, en(PI / 4 + i * PI / 2, 0.84f))
            }
            TemaId.MONGOL -> {
                forma(caja(c.x - 10 * u, c.y - 22 * u, c.x + 10 * u, c.y + 22 * u, 9 * u))
                drawCircle(claro, 2.6f * u, Offset(c.x, c.y - 16 * u)); drawCircle(tinta, 2.6f * u, Offset(c.x, c.y - 16 * u), style = Stroke(0.8f * u))
                for (k in 0 until 3) {
                    val x = c.x - 5 * u + k * 5 * u
                    drawLine(tinta, Offset(x, c.y - 9 * u), Offset(x, c.y + 16 * u), 1.1f * u)
                    for (j in 0 until 4) drawLine(tinta, Offset(x, c.y - 6 * u + j * 6 * u), Offset(x + 1.8f * u, c.y - 5 * u + j * 6 * u), 0.9f * u)
                }
            }
            TemaId.RENACIMIENTO -> {
                disco()
                drawPath(Path().apply { moveTo(c.x, c.y - 12 * u); quadraticTo(c.x + 4 * u, c.y - 5 * u, c.x, c.y + 3 * u); quadraticTo(c.x - 4 * u, c.y - 5 * u, c.x, c.y - 12 * u); close() }, tinta)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(c.x + s * 1.5f * u, c.y + 1 * u); quadraticTo(c.x + s * 10 * u, c.y - 2 * u, c.x + s * 8 * u, c.y - 9 * u); quadraticTo(c.x + s * 6 * u, c.y - 3 * u, c.x + s * 1.5f * u, c.y + 3 * u); close() }, tinta) }
                drawRect(tinta, Offset(c.x - 6 * u, c.y + 3 * u), Size(12 * u, 2 * u))
                drawPath(Path().apply { moveTo(c.x - 1.5f * u, c.y + 5 * u); lineTo(c.x - 4 * u, c.y + 11 * u); lineTo(c.x + 4 * u, c.y + 11 * u); lineTo(c.x + 1.5f * u, c.y + 5 * u); close() }, tinta)
            }
            TemaId.URSS -> {
                disco()
                drawPath(estrellaN(Offset(c.x, c.y - 4 * u), 7 * u, 5), Color(0xFFC62828))
                listOf(-1f, 1f).forEach { s -> for (k in 0 until 5) { val a = PI / 2 + s * (0.5 + k * 0.32); val p = en(a, 0.74f); rotate((a * 180 / PI).toFloat(), p) { drawOval(tinta, Offset(p.x - 1.1f * u, p.y - 2.3f * u), Size(2.2f * u, 4.6f * u)) } } }
                letra("1", Offset(c.x, c.y + 8 * u), 7 * u, tinta)
            }
            TemaId.REVOLUCION -> {
                val p = caja(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y + 15 * u)
                drawPath(p, claro); contorno(tz, p, tinta, 1.2f * u)
                drawRect(tinta, Offset(c.x - 21 * u, c.y - 12 * u), Size(42 * u, 24 * u), style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * u, 1 * u))))
                drawCircle(tono, 5 * u, Offset(c.x - 13 * u, c.y))
                letra("5 L", Offset(c.x + 7 * u, c.y - 2 * u), 9 * u, tinta)
                drawLine(tinta, Offset(c.x - 2 * u, c.y + 7 * u), Offset(c.x + 17 * u, c.y + 7 * u), 0.8f * u)
            }
            TemaId.ANTIGUAS -> {
                forma(irregular(9, 0.1f))
                for (i in 0 until 9) { val a = i * 2 * PI / 9 + 0.35; drawLine(tinta, en(a, 0.58f), en(a + (if (i % 2 == 0) 0.12 else -0.12), 0.8f), 1 * u) }
                val gema = Path().apply { moveTo(c.x, c.y - 7 * u); lineTo(c.x + 6 * u, c.y); lineTo(c.x, c.y + 7 * u); lineTo(c.x - 6 * u, c.y); close() }
                drawPath(gema, Color(0xFF7FD8E8)); contorno(tz, gema, tinta, 0.8f * u)
            }
        }
    }

    // ---------------------------------------------------------------- libro

    /**
     * El libro del diario con la encuadernación del tema: tablillas de cera, rollo de papiro, códice con bullones, libro
     * de horas, cuaderno de espejo, Código de pie, cuaderno de quejas, libro rojo del plan, terminal, expediente, manual
     * de anillas, tomo con cadena, cuaderno cuadriculado, bitácora en tablilla, libro mayor, libreta de campo, libro
     * envuelto para regalo, libro en acordeón, libro con solapa, hojas entre tablas atadas y gran tomo con gema.
     */
    private fun DrawScope.libro(c: Offset, u: Float, tema: TemaId, tinta: Color, tono: Color, tz: Trazo) {
        val papel = tz.material
        fun paginas(lineas: Boolean = true) {
            val izq = Path().apply { moveTo(c.x, c.y - 14 * u); quadraticTo(c.x - 14 * u, c.y - 20 * u, c.x - 26 * u, c.y - 14 * u); lineTo(c.x - 26 * u, c.y + 16 * u); quadraticTo(c.x - 14 * u, c.y + 10 * u, c.x, c.y + 16 * u); close() }
            val der = Path().apply { moveTo(c.x, c.y - 14 * u); quadraticTo(c.x + 14 * u, c.y - 20 * u, c.x + 26 * u, c.y - 14 * u); lineTo(c.x + 26 * u, c.y + 16 * u); quadraticTo(c.x + 14 * u, c.y + 10 * u, c.x, c.y + 16 * u); close() }
            drawPath(izq, papel); drawPath(der, papel); contorno(tz, izq, tinta, 0.9f * u); contorno(tz, der, tinta, 0.9f * u)
            if (lineas) for (k in 0 until 4) {
                val y = c.y - 8 * u + k * 6 * u
                fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x - 21 * u, y), Offset(c.x - 5 * u, y), 0.9f * u); fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x + 5 * u, y), Offset(c.x + 21 * u, y), 0.9f * u)
            }
        }
        fun tapa(x0: Float, y0: Float, x1: Float, y1: Float, col: Color = tono, radio: Float = 2 * u) { val p = caja(x0, y0, x1, y1, radio); drawPath(p, col); contorno(tz, p, tinta, 1.2f * u) }
        when (tema) {
            TemaId.ROMANO -> {
                listOf(c.x - 25 * u, c.x + 1 * u).forEach { x0 ->
                    tapa(x0, c.y - 16 * u, x0 + 24 * u, c.y + 16 * u, tono, 1 * u)
                    drawRect(tinta.copy(alpha = 0.75f), Offset(x0 + 3 * u, c.y - 13 * u), Size(18 * u, 26 * u))
                    for (k in 0 until 4) fina(tz, papel, Offset(x0 + 5 * u, c.y - 8 * u + k * 6 * u), Offset(x0 + (12 + (k * 5) % 7) * u, c.y - 8 * u + k * 6 * u), 0.8f * u)
                }
                listOf(-8f, 8f).forEach { dy -> drawCircle(tinta, 1.6f * u, Offset(c.x, c.y + dy * u), style = Stroke(0.8f * u)) }
                raya(tz, tinta, Offset(c.x + 10 * u, c.y + 22 * u), Offset(c.x + 26 * u, c.y + 4 * u), 1.4f * u)
            }
            TemaId.CIBER -> {
                val p = caja(c.x - 24 * u, c.y - 18 * u, c.x + 24 * u, c.y + 12 * u, 3 * u); drawPath(p, tinta); contorno(tz, p, tono, 1 * u)
                val y0 = c.y - 11 * u
                drawLine(tono, Offset(c.x - 20 * u, y0 - 2 * u), Offset(c.x - 17 * u, y0), 1 * u); drawLine(tono, Offset(c.x - 17 * u, y0), Offset(c.x - 20 * u, y0 + 2 * u), 1 * u)
                for (k in 0 until 3) fina(tz, tono, Offset(c.x - 14 * u, y0 + k * 6 * u), Offset(c.x + (4 + (k * 9) % 16) * u, y0 + k * 6 * u), 1.2f * u)
                drawRect(tono, Offset(c.x - 14 * u, y0 + 16 * u), Size(3 * u, 4 * u))
                drawRect(tinta, Offset(c.x - 4 * u, c.y + 12 * u), Size(8 * u, 6 * u)); raya(tz, tinta, Offset(c.x - 12 * u, c.y + 19 * u), Offset(c.x + 12 * u, c.y + 19 * u), 1.6f * u)
            }
            // El menú del puesto: una pizarra con platos y precios, enmarcada por un tubo ámbar.
            TemaId.NEON -> {
                tapa(c.x - 24 * u, c.y - 20 * u, c.x + 24 * u, c.y + 16 * u, tinta, 3 * u)
                drawRect(tono.copy(alpha = 0.3f), Offset(c.x - 22 * u, c.y - 18 * u), Size(44 * u, 32 * u), style = Stroke(3 * u))
                for (k in 0 until 4) {
                    val y = c.y - 11 * u + k * 7 * u
                    fina(tz, papel, Offset(c.x - 18 * u, y), Offset(c.x + (2 + (k * 5) % 8) * u, y), 1.2f * u)
                    var x = c.x + 6 * u; while (x < c.x + 13 * u) { drawCircle(papel.copy(alpha = 0.5f), 0.5f * u, Offset(x, y)); x += 2 * u }
                    drawRect(tono, Offset(c.x + 14 * u, y - 1.5f * u), Size(4 * u, 3 * u))
                }
            }
            TemaId.HELENO -> {
                drawRect(papel, Offset(c.x - 18 * u, c.y - 12 * u), Size(36 * u, 24 * u)); contorno(tz, caja(c.x - 18 * u, c.y - 12 * u, c.x + 18 * u, c.y + 12 * u), tinta, 0.8f * u)
                listOf(-1f, 1f).forEach { s -> val x = c.x + s * 21 * u; tapa(x - 3 * u, c.y - 14 * u, x + 3 * u, c.y + 14 * u, papel, 3 * u); drawCircle(tono, 2 * u, Offset(x, c.y - 16 * u)); drawCircle(tono, 2 * u, Offset(x, c.y + 16 * u)) }
                listOf(-14f, 2f).forEach { x0 -> for (k in 0 until 5) fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x + x0 * u, c.y - 8 * u + k * 4 * u), Offset(c.x + (x0 + 11) * u, c.y - 8 * u + k * 4 * u), 0.8f * u) }
            }
            // Un papiro a medio desenrollar, sin varillas, escrito en columnas; la primera, en rojo de rúbrica.
            TemaId.EGIPTO -> {
                drawRect(papel, Offset(c.x - 18 * u, c.y - 12 * u), Size(36 * u, 24 * u)); contorno(tz, caja(c.x - 18 * u, c.y - 12 * u, c.x + 18 * u, c.y + 12 * u), tinta, 0.8f * u)
                listOf(-1f, 1f).forEach { s -> val x = c.x + s * 21 * u; tapa(x - 4 * u, c.y - 13 * u, x + 4 * u, c.y + 13 * u, papel, 4 * u) }
                for (k in 0 until 6) { val x = c.x - 13 * u + k * 5 * u; fina(tz, if (k == 0) Color(0xFFB8471E) else tinta.copy(alpha = 0.6f), Offset(x, c.y - 8 * u), Offset(x, c.y + 8 * u), 0.9f * u) }
            }
            TemaId.MEDIEVAL -> {
                drawRect(papel, Offset(c.x - 14 * u, c.y - 20 * u), Size(30 * u, 42 * u))
                tapa(c.x - 17 * u, c.y - 22 * u, c.x + 15 * u, c.y + 22 * u, tono, 1.5f * u)
                listOf(Offset(-13f, -18f), Offset(11f, -18f), Offset(-13f, 18f), Offset(11f, 18f)).forEach { o -> drawCircle(tinta, 2 * u, Offset(c.x + o.x * u, c.y + o.y * u)) }
                drawCircle(tinta, 3.2f * u, Offset(c.x - 1 * u, c.y))
                listOf(-9f, 9f).forEach { dy -> drawRect(tinta, Offset(c.x + 13 * u, c.y + (dy - 1.5f) * u), Size(6 * u, 3 * u)) }
                drawRect(tinta, Offset(c.x - 9 * u, c.y - 13 * u), Size(16 * u, 26 * u), style = Stroke(0.8f * u))
            }
            // La regla de la casa: tapa blanca con la cruz roja y dos correas con hebilla.
            TemaId.TEMPLARIO -> {
                tapa(c.x - 16 * u, c.y - 21 * u, c.x + 16 * u, c.y + 21 * u, papel, 1.5f * u)
                drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(c.x - 1 * u, c.y - 2 * u), 10 * u), tono)
                listOf(-12f, 12f).forEach { dy -> drawRect(tinta, Offset(c.x + 8 * u, c.y + (dy - 1.8f) * u), Size(11 * u, 3.6f * u)); drawRect(tinta, Offset(c.x + 12 * u, c.y + (dy - 2.8f) * u), Size(3 * u, 5.6f * u), style = Stroke(0.8f * u)) }
            }
            TemaId.DISTOPIA -> {
                drawRect(papel, Offset(c.x - 19 * u, c.y - 17 * u), Size(36 * u, 34 * u))
                val carpeta = Path().apply { moveTo(c.x - 22 * u, c.y - 14 * u); lineTo(c.x - 8 * u, c.y - 14 * u); lineTo(c.x - 5 * u, c.y - 10 * u); lineTo(c.x + 22 * u, c.y - 10 * u); lineTo(c.x + 22 * u, c.y + 20 * u); lineTo(c.x - 22 * u, c.y + 20 * u); close() }
                drawPath(carpeta, tono.copy(alpha = 0.75f)); contorno(tz, carpeta, tinta, 1 * u)
                rotate(-14f, Offset(c.x + 4 * u, c.y + 6 * u)) {
                    drawRect(Color(0xFFB71C1C), Offset(c.x - 10 * u, c.y + 1 * u), Size(28 * u, 9 * u), style = Stroke(1.4f * u))
                    drawLine(Color(0xFFB71C1C), Offset(c.x - 7 * u, c.y + 5.5f * u), Offset(c.x + 15 * u, c.y + 5.5f * u), 1.6f * u)
                }
                for (k in 0 until 2) fina(tz, tinta, Offset(c.x - 16 * u, c.y - 5 * u + k * 4 * u), Offset(c.x + 4 * u, c.y - 5 * u + k * 4 * u), 0.8f * u)
            }
            TemaId.REFUGIO -> {
                tapa(c.x - 18 * u, c.y - 22 * u, c.x + 18 * u, c.y + 22 * u, tono, 5 * u)
                drawRect(tinta.copy(alpha = 0.35f), Offset(c.x - 18 * u, c.y - 20 * u), Size(6 * u, 40 * u))
                listOf(-12f, 0f, 12f).forEach { dy -> drawArc(tinta, 90f, 180f, false, Offset(c.x - 19 * u, c.y + (dy - 3) * u), Size(6 * u, 6 * u), style = Stroke(1.4f * u)) }
                val e = caja(c.x - 6 * u, c.y - 12 * u, c.x + 14 * u, c.y - 2 * u, 2 * u); drawPath(e, papel); contorno(tz, e, tinta, 0.7f * u)
                fina(tz, tinta, Offset(c.x - 3 * u, c.y - 7 * u), Offset(c.x + 11 * u, c.y - 7 * u), 0.9f * u)
            }
            TemaId.ORDEN -> {
                tapa(c.x - 17 * u, c.y - 22 * u, c.x + 15 * u, c.y + 20 * u, tono, 1 * u)
                listOf(Offset(-17f, -22f) to Offset(1f, 1f), Offset(15f, -22f) to Offset(-1f, 1f), Offset(-17f, 20f) to Offset(1f, -1f), Offset(15f, 20f) to Offset(-1f, -1f)).forEach { (o, s) ->
                    val x = c.x + o.x * u; val y = c.y + o.y * u
                    drawPath(Path().apply { moveTo(x, y); lineTo(x + s.x * 7 * u, y); lineTo(x, y + s.y * 7 * u); close() }, papel)
                }
                drawPath(engrane(Offset(c.x - 1 * u, c.y - 2 * u), 7 * u, 5.5f * u, 8), papel); drawCircle(tono, 2.4f * u, Offset(c.x - 1 * u, c.y - 2 * u))
                for (k in 0 until 4) drawOval(tinta, Offset(c.x + 14 * u + k * 2.5f * u, c.y + 14 * u + k * 3 * u), Size(4 * u, 2.6f * u), style = Stroke(1 * u))
            }
            TemaId.LABORATORIO -> {
                paginas(false)
                val rejilla = tono.copy(alpha = 0.45f)
                for (k in 0 until 6) { val y = c.y - 10 * u + k * 4.5f * u; drawLine(rejilla, Offset(c.x - 24 * u, y), Offset(c.x - 2 * u, y), 0.5f * u); drawLine(rejilla, Offset(c.x + 2 * u, y), Offset(c.x + 24 * u, y), 0.5f * u) }
                for (k in 0 until 5) { val dx = (4 + k * 4.5f) * u; drawLine(rejilla, Offset(c.x - dx, c.y - 13 * u), Offset(c.x - dx, c.y + 13 * u), 0.5f * u); drawLine(rejilla, Offset(c.x + dx, c.y - 13 * u), Offset(c.x + dx, c.y + 13 * u), 0.5f * u) }
                drawPath(Path().apply { moveTo(c.x + 4 * u, c.y + 8 * u); lineTo(c.x + 10 * u, c.y + 2 * u); lineTo(c.x + 15 * u, c.y + 5 * u); lineTo(c.x + 22 * u, c.y - 8 * u) }, tinta, style = tz.st(1 * u))
            }
            TemaId.COLONOS -> {
                paginas(false)
                listOf(-18f, -9f, 9f, 18f).forEach { dx -> drawLine(Color(0xFFB5462F).copy(alpha = 0.7f), Offset(c.x + dx * u, c.y - 14 * u), Offset(c.x + dx * u, c.y + 13 * u), 0.7f * u) }
                for (k in 0 until 6) { val y = c.y - 10 * u + k * 4.5f * u; fina(tz, tinta.copy(alpha = 0.55f), Offset(c.x - 25 * u, y), Offset(c.x - 2 * u, y), 0.6f * u); fina(tz, tinta.copy(alpha = 0.55f), Offset(c.x + 2 * u, y), Offset(c.x + 25 * u, y), 0.6f * u) }
            }
            TemaId.NAPOLEON -> {
                tapa(c.x - 11 * u, c.y - 24 * u, c.x + 11 * u, c.y + 22 * u, tono, 1.5f * u)
                listOf(-16f, -8f, 8f, 16f).forEach { dy -> raya(tz, tinta, Offset(c.x - 11 * u, c.y + dy * u), Offset(c.x + 11 * u, c.y + dy * u), 1.2f * u) }
                drawRect(Color(0xFFD4AF37), Offset(c.x - 8 * u, c.y - 6 * u), Size(16 * u, 12 * u))
                letra("N", c, 9 * u, tinta)
                letra("CODE", Offset(c.x, c.y - 12 * u), 4 * u, Color(0xFFD4AF37))
            }
            TemaId.BOTANICO -> {
                paginas(false)
                for (k in 0 until 4) fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x - 21 * u, c.y - 8 * u + k * 6 * u), Offset(c.x - 5 * u, c.y - 8 * u + k * 6 * u), 0.8f * u)
                val hoja = Path().apply { moveTo(c.x + 6 * u, c.y + 10 * u); quadraticTo(c.x + 6 * u, c.y - 8 * u, c.x + 22 * u, c.y - 12 * u); quadraticTo(c.x + 22 * u, c.y + 6 * u, c.x + 6 * u, c.y + 10 * u); close() }
                drawPath(hoja, Color(0xFF5C7A3A)); drawLine(papel, Offset(c.x + 6 * u, c.y + 10 * u), Offset(c.x + 19 * u, c.y - 8 * u), 0.7f * u)
            }
            TemaId.NAVIDAD -> {
                tapa(c.x - 18 * u, c.y - 18 * u, c.x + 18 * u, c.y + 22 * u, tono, 2 * u)
                val oro = Color(0xFFF2C94C)
                drawRect(oro, Offset(c.x - 2 * u, c.y - 18 * u), Size(4 * u, 40 * u)); drawRect(oro, Offset(c.x - 18 * u, c.y), Size(36 * u, 4 * u))
                listOf(-10f, 1f).forEach { dx -> drawOval(oro, Offset(c.x + dx * u, c.y - 25 * u), Size(9 * u, 7 * u), style = Stroke(1.8f * u)) }
                drawCircle(oro, 2.2f * u, Offset(c.x, c.y - 19 * u))
            }
            TemaId.SEDA -> for (k in 0 until 5) {
                val x = c.x - 25 * u + k * 10 * u; val alto = if (k % 2 == 0) 0f else 3 * u
                val panel = Path().apply { moveTo(x, c.y - 16 * u + alto); lineTo(x + 10 * u, c.y - 13 * u - alto); lineTo(x + 10 * u, c.y + 21 * u - alto); lineTo(x, c.y + 18 * u + alto); close() }
                drawPath(panel, if (k % 2 == 0) papel else lerp(papel, tinta, 0.12f)); contorno(tz, panel, tinta, 0.7f * u)
                for (j in 0 until 4) drawLine(tinta.copy(alpha = 0.7f), Offset(x + 5 * u, c.y - 10 * u + j * 7 * u), Offset(x + 5 * u, c.y - 6 * u + j * 7 * u), 1.4f * u)
            }
            TemaId.GOTICO -> {
                paginas()
                drawRect(Color(0xFFB0283F), Offset(c.x - 23 * u, c.y - 13 * u), Size(8 * u, 8 * u)); drawRect(Color(0xFF2952A3), Offset(c.x - 21 * u, c.y - 11 * u), Size(4 * u, 4 * u))
                drawLine(Color(0xFFD4AF37), Offset(c.x - 26 * u, c.y + 17 * u), Offset(c.x - 1 * u, c.y + 17 * u), 1.4f * u); drawLine(Color(0xFFD4AF37), Offset(c.x + 1 * u, c.y + 17 * u), Offset(c.x + 26 * u, c.y + 17 * u), 1.4f * u)
                for (k in 0 until 3) drawCircle(Color(0xFFB0283F), 1 * u, Offset(c.x + 23 * u, c.y - 8 * u + k * 6 * u))
            }
            TemaId.ESPACIO -> {
                tapa(c.x - 17 * u, c.y - 22 * u, c.x + 17 * u, c.y + 24 * u, tono, 2 * u)
                drawRect(papel, Offset(c.x - 14 * u, c.y - 16 * u), Size(28 * u, 37 * u))
                drawRect(tinta, Offset(c.x - 7 * u, c.y - 25 * u), Size(14 * u, 6 * u)); drawCircle(papel, 1.5f * u, Offset(c.x, c.y - 22 * u))
                for (k in 0 until 4) {
                    val y = c.y - 9 * u + k * 7 * u
                    drawRect(tinta, Offset(c.x - 11 * u, y - 2 * u), Size(4 * u, 4 * u), style = Stroke(0.7f * u)); fina(tz, tinta.copy(alpha = 0.7f), Offset(c.x - 5 * u, y), Offset(c.x + 10 * u, y), 0.8f * u)
                    if (k < 2) drawLine(tono, Offset(c.x - 10.5f * u, y), Offset(c.x - 8 * u, y + 1.5f * u), 0.9f * u)
                }
            }
            // La bitácora del batiscafo: tapas de lona con cantoneras, un ojo de buey en la portada y la correa de goma.
            TemaId.ABISMO -> {
                tapa(c.x - 17 * u, c.y - 22 * u, c.x + 17 * u, c.y + 22 * u, tono, 3 * u)
                listOf(Offset(-1f, -1f), Offset(1f, -1f), Offset(-1f, 1f), Offset(1f, 1f)).forEach { d ->
                    val o = Offset(c.x + d.x * 17 * u, c.y + d.y * 22 * u)
                    drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x - d.x * 6 * u, o.y); lineTo(o.x, o.y - d.y * 6 * u); close() }, tinta)
                }
                drawCircle(tinta, 7 * u, Offset(c.x - 2 * u, c.y - 7 * u)); drawCircle(papel, 4.6f * u, Offset(c.x - 2 * u, c.y - 7 * u))
                for (k in 0 until 3) fina(tz, papel.copy(alpha = 0.8f), Offset(c.x - 11 * u, c.y + 5 * u + k * 5 * u), Offset(c.x + 7 * u, c.y + 5 * u + k * 5 * u), 0.8f * u)
                drawRect(tinta, Offset(c.x + 11 * u, c.y - 22 * u), Size(3 * u, 44 * u))
            }
            TemaId.ANDALUS -> {
                tapa(c.x - 18 * u, c.y - 22 * u, c.x + 14 * u, c.y + 22 * u, tono, 1 * u)
                drawRect(papel, Offset(c.x - 16 * u, c.y - 20 * u), Size(28 * u, 40 * u), style = Stroke(0.6f * u))
                val solapa = Path().apply { moveTo(c.x + 14 * u, c.y - 22 * u); lineTo(c.x + 14 * u, c.y + 22 * u); lineTo(c.x - 2 * u, c.y + 8 * u); lineTo(c.x - 2 * u, c.y - 8 * u); close() }
                drawPath(solapa, lerp(tono, tinta, 0.25f)); contorno(tz, solapa, tinta, 0.9f * u)
                drawPath(estrellaN(Offset(c.x - 9 * u, c.y), 6 * u, 8, 0.72f), papel)
            }
            TemaId.MONGOL -> {
                tapa(c.x - 26 * u, c.y + 3 * u, c.x + 26 * u, c.y + 9 * u, tono, 1 * u)
                drawRect(papel, Offset(c.x - 25 * u, c.y - 6 * u), Size(50 * u, 9 * u))
                for (k in 1 until 4) drawLine(tinta.copy(alpha = 0.3f), Offset(c.x - 25 * u, c.y - 6 * u + k * 2.2f * u), Offset(c.x + 25 * u, c.y - 6 * u + k * 2.2f * u), 0.4f * u)
                tapa(c.x - 26 * u, c.y - 12 * u, c.x + 26 * u, c.y - 6 * u, tono, 1 * u)
                listOf(-14f, 14f).forEach { dx -> raya(tz, Color(0xFFC2551F), Offset(c.x + dx * u, c.y - 13 * u), Offset(c.x + dx * u, c.y + 10 * u), 1.2f * u) }
                drawPath(Path().apply { moveTo(c.x + 14 * u, c.y + 10 * u); quadraticTo(c.x + 18 * u, c.y + 16 * u, c.x + 13 * u, c.y + 20 * u) }, Color(0xFFC2551F), style = Stroke(1.2f * u))
            }
            TemaId.RENACIMIENTO -> {
                paginas(false)
                for (k in 0 until 5) { val y = c.y - 9 * u + k * 5 * u; fina(tz, tinta.copy(alpha = 0.7f), Offset(c.x - 4 * u, y), Offset(c.x - (12 + (k * 7) % 10) * u, y), 0.7f * u) }
                val o = Offset(c.x + 13 * u, c.y)
                drawCircle(tono, 7 * u, o, style = Stroke(0.8f * u)); drawRect(tono, Offset(o.x - 7 * u, o.y - 7 * u), Size(14 * u, 14 * u), style = Stroke(0.6f * u))
                drawLine(tono, Offset(o.x - 7 * u, o.y - 7 * u), Offset(o.x + 7 * u, o.y + 7 * u), 0.5f * u); drawLine(tono, Offset(o.x + 7 * u, o.y - 7 * u), Offset(o.x - 7 * u, o.y + 7 * u), 0.5f * u)
            }
            TemaId.URSS -> rotate(-12f, c) {
                drawRect(papel, Offset(c.x - 13 * u, c.y - 19 * u), Size(29 * u, 39 * u))
                tapa(c.x - 15 * u, c.y - 20 * u, c.x + 14 * u, c.y + 20 * u, Color(0xFFB3261E), 1 * u)
                drawPath(estrellaN(Offset(c.x, c.y - 6 * u), 7 * u, 5), Color(0xFFFFD54F))
                for (k in 0 until 2) drawRect(Color(0xFFFFD54F), Offset(c.x - 8 * u, c.y + 6 * u + k * 5 * u), Size(16 * u, 1.6f * u))
            }
            TemaId.REVOLUCION -> {
                val p = caja(c.x - 18 * u, c.y - 22 * u, c.x + 18 * u, c.y + 22 * u); drawPath(p, papel); contorno(tz, p, tinta, 1 * u)
                drawLine(tinta.copy(alpha = 0.5f), Offset(c.x - 14 * u, c.y - 22 * u), Offset(c.x - 14 * u, c.y + 22 * u), 0.8f * u)
                listOf(-10f, 10f).forEach { dy -> drawLine(tinta, Offset(c.x - 15.5f * u, c.y + (dy - 3) * u), Offset(c.x - 15.5f * u, c.y + (dy + 3) * u), 1.2f * u) }
                letra("CAHIER", Offset(c.x + 2 * u, c.y - 13 * u), 5 * u, tinta)
                for (k in 0 until 3) fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x - 9 * u, c.y - 3 * u + k * 5 * u), Offset(c.x + 14 * u, c.y - 3 * u + k * 5 * u), 0.8f * u)
                val e = Offset(c.x + 10 * u, c.y + 16 * u)
                drawCircle(Color(0xFF1F3A8A), 4 * u, e); drawCircle(Color.White, 2.7f * u, e); drawCircle(Color(0xFFC62828), 1.4f * u, e)
            }
            TemaId.ANTIGUAS -> {
                drawRect(papel, Offset(c.x - 15 * u, c.y - 21 * u), Size(31 * u, 42 * u))
                tapa(c.x - 19 * u, c.y - 23 * u, c.x + 15 * u, c.y + 23 * u, tono, 3 * u)
                drawRect(tinta.copy(alpha = 0.4f), Offset(c.x - 19 * u, c.y - 23 * u), Size(5 * u, 46 * u))
                for (k in 0 until 3) { val y = c.y - 14 * u + k * 12 * u; drawLine(papel, Offset(c.x - 16.5f * u, y), Offset(c.x - 16.5f * u, y + 6 * u), 0.8f * u); drawLine(papel, Offset(c.x - 16.5f * u, y + 2 * u), Offset(c.x - 15 * u, y), 0.8f * u) }
                drawRect(tinta, Offset(c.x + 8 * u, c.y - 3 * u), Size(11 * u, 6 * u)); drawRect(papel, Offset(c.x + 15 * u, c.y - 2 * u), Size(3 * u, 4 * u), style = Stroke(0.8f * u))
                val gema = Path().apply { moveTo(c.x - 2 * u, c.y - 8 * u); lineTo(c.x + 4 * u, c.y); lineTo(c.x - 2 * u, c.y + 8 * u); lineTo(c.x - 8 * u, c.y); close() }
                drawPath(gema, Color(0xFF7FD8E8)); contorno(tz, gema, tinta, 0.8f * u)
            }
        }
    }

    // ---------------------------------------------------------------- carta

    /**
     * La carta con la solapa y el cierre del tema: carta enrollada con cordel, papiro con sello de arcilla, carta con
     * sello colgante, solapa apuntada con roseta, carta cerrada con tira, solapa cuadrada con lacre, borde tricolor,
     * triángulo del frente, sobre de píxeles, ventanilla con censura, cápsula neumática, valija con hebilla, sobre de
     * muestra, paquete de datos con ondas, telegrama, sobre de semillas, sobre con sello y acebo, sobre rojo vertical,
     * solapa lobulada, mensaje en una flecha y pergamino con cordel y lacre rúnico.
     */
    private fun DrawScope.sobre(c: Offset, u: Float, tema: TemaId, tinta: Color, tono: Color, tz: Trazo) {
        val papel = tz.material
        fun hoja(x0: Float, y0: Float, x1: Float, y1: Float, col: Color = papel, radio: Float = 0f) { val p = caja(x0, y0, x1, y1, radio); drawPath(p, col); contorno(tz, p, tinta, 1.2f * u) }
        fun solapaV(x0: Float, y0: Float, x1: Float, yPico: Float) = contorno(tz, Path().apply { moveTo(x0, y0); lineTo((x0 + x1) / 2, yPico); lineTo(x1, y0) }, tinta, 1.2f * u)
        fun escarapela(o: Offset, r: Float) { drawCircle(Color(0xFF1F3A8A), r, o); drawCircle(Color.White, r * 0.66f, o); drawCircle(Color(0xFFC62828), r * 0.33f, o) }
        when (tema) {
            TemaId.ROMANO -> {
                hoja(c.x - 22 * u, c.y - 7 * u, c.x + 22 * u, c.y + 7 * u, papel, 7 * u)
                listOf(-22f, 22f).forEach { dx -> drawOval(lerp(papel, tinta, 0.2f), Offset(c.x + dx * u - 2.5f * u, c.y - 7 * u), Size(5 * u, 14 * u)) }
                listOf(-2f, 2f).forEach { dx -> drawLine(tinta, Offset(c.x + dx * u, c.y - 7 * u), Offset(c.x + dx * u, c.y + 7 * u), 1 * u) }
                drawLine(tinta, Offset(c.x, c.y + 7 * u), Offset(c.x, c.y + 13 * u), 1 * u); drawCircle(tono, 5 * u, Offset(c.x, c.y + 17 * u))
            }
            TemaId.CIBER -> {
                val q = 3 * u; val lado = q * 0.8f
                for (k in 0 until 16) { drawRect(tono, Offset(c.x - 24 * u + k * q, c.y - 15 * u), Size(lado, lado)); drawRect(tono, Offset(c.x - 24 * u + k * q, c.y + 13 * u), Size(lado, lado)) }
                for (k in 0 until 10) { drawRect(tono, Offset(c.x - 24 * u, c.y - 15 * u + k * q), Size(lado, lado)); drawRect(tono, Offset(c.x + 21 * u, c.y - 15 * u + k * q), Size(lado, lado)) }
                for (k in 0 until 7) { val y = c.y - 12 * u + k * 2 * u; drawRect(tono, Offset(c.x - 21 * u + k * q, y), Size(lado, lado)); drawRect(tono, Offset(c.x + 18 * u - k * q, y), Size(lado, lado)) }
            }
            // El recado: una bolsa de papel de comida para llevar, doblada arriba y con la etiqueta del puesto.
            TemaId.NEON -> {
                hoja(c.x - 18 * u, c.y - 12 * u, c.x + 18 * u, c.y + 18 * u, papel, 1.5f * u)
                contorno(tz, Path().apply { moveTo(c.x - 18 * u, c.y - 12 * u); lineTo(c.x - 14 * u, c.y - 18 * u); lineTo(c.x + 14 * u, c.y - 18 * u); lineTo(c.x + 18 * u, c.y - 12 * u) }, tinta, 1.2f * u)
                drawLine(tinta, Offset(c.x - 18 * u, c.y - 6 * u), Offset(c.x + 18 * u, c.y - 6 * u), 1 * u)
                drawCircle(tono, 7 * u, Offset(c.x, c.y + 6 * u)); letra("夜", Offset(c.x, c.y + 6 * u), 8 * u, papel)
            }
            TemaId.HELENO -> {
                hoja(c.x - 20 * u, c.y - 10 * u, c.x + 20 * u, c.y + 10 * u)
                listOf(-7f, 7f).forEach { dx -> fina(tz, tinta.copy(alpha = 0.4f), Offset(c.x + dx * u, c.y - 10 * u), Offset(c.x + dx * u, c.y + 10 * u), 0.7f * u) }
                drawLine(tinta, Offset(c.x - 20 * u, c.y), Offset(c.x + 20 * u, c.y), 0.8f * u)
                drawCircle(Color(0xFFB0643A), 4.5f * u, c); drawCircle(tinta, 4.5f * u, c, style = Stroke(0.7f * u))
            }
            // La carta egipcia: el papiro doblado en una tira estrecha, atado con hilo, sellado con barro y con la dirección escrita.
            TemaId.EGIPTO -> {
                hoja(c.x - 24 * u, c.y - 5 * u, c.x + 24 * u, c.y + 5 * u, papel, 1 * u)
                listOf(-6f, -3f).forEach { dx -> drawLine(Color(0xFFCDBB8E), Offset(c.x + dx * u, c.y - 7 * u), Offset(c.x + dx * u, c.y + 7 * u), 1.2f * u) }
                drawOval(Color(0xFF6E4A2E), Offset(c.x - 9.5f * u, c.y + 2 * u), Size(9 * u, 7 * u))
                for (k in 0 until 3) fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x + 2 * u + k * 6 * u, c.y - 2 * u), Offset(c.x + 6 * u + k * 6 * u, c.y - 2 * u), 0.8f * u)
                fina(tz, Color(0xFFB8471E), Offset(c.x + 2 * u, c.y + 2 * u), Offset(c.x + 18 * u, c.y + 2 * u), 0.8f * u)
            }
            TemaId.MEDIEVAL -> {
                hoja(c.x - 22 * u, c.y - 18 * u, c.x + 22 * u, c.y + 6 * u)
                fina(tz, tinta.copy(alpha = 0.4f), Offset(c.x - 22 * u, c.y - 6 * u), Offset(c.x + 22 * u, c.y - 6 * u), 0.7f * u)
                listOf(-3f, 3f).forEach { dx -> drawLine(Color(0xFFB0302A), Offset(c.x + dx * u * 0.4f, c.y + 6 * u), Offset(c.x + dx * u, c.y + 16 * u), 1 * u) }
                drawCircle(tono, 6 * u, Offset(c.x, c.y + 20 * u)); drawCircle(tinta, 3.5f * u, Offset(c.x, c.y + 20 * u), style = Stroke(0.7f * u))
            }
            // La misiva plegada en tres, con el sello pendiente de la casa colgado de una tira.
            TemaId.TEMPLARIO -> {
                hoja(c.x - 20 * u, c.y - 16 * u, c.x + 20 * u, c.y + 6 * u)
                listOf(-7f, 7f).forEach { dx -> fina(tz, tinta.copy(alpha = 0.4f), Offset(c.x + dx * u, c.y - 16 * u), Offset(c.x + dx * u, c.y + 6 * u), 0.7f * u) }
                drawLine(tinta, Offset(c.x, c.y + 6 * u), Offset(c.x, c.y + 14 * u), 1.2f * u)
                drawCircle(tono, 6.5f * u, Offset(c.x, c.y + 20 * u)); drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(c.x, c.y + 20 * u), 4 * u), tinta.copy(alpha = 0.7f))
            }
            TemaId.DISTOPIA -> {
                hoja(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y + 15 * u)
                drawRect(tinta.copy(alpha = 0.4f), Offset(c.x - 18 * u, c.y - 6 * u), Size(24 * u, 12 * u), style = Stroke(0.8f * u))
                drawRect(tinta, Offset(c.x - 16 * u, c.y - 2 * u), Size(20 * u, 4 * u))
                drawCircle(Color(0xFFB71C1C), 5 * u, Offset(c.x + 15 * u, c.y - 7 * u), style = Stroke(1.2f * u)); drawLine(Color(0xFFB71C1C), Offset(c.x + 11 * u, c.y - 7 * u), Offset(c.x + 19 * u, c.y - 7 * u), 1 * u)
            }
            TemaId.REFUGIO -> {
                hoja(c.x - 22 * u, c.y - 8 * u, c.x + 22 * u, c.y + 8 * u, tono, 8 * u)
                listOf(-16f, 16f).forEach { dx -> drawRect(tinta.copy(alpha = 0.5f), Offset(c.x + dx * u - 1 * u, c.y - 8 * u), Size(2 * u, 16 * u)) }
                drawRect(papel, Offset(c.x - 8 * u, c.y - 4 * u), Size(16 * u, 8 * u)); drawRect(tinta, Offset(c.x - 8 * u, c.y - 4 * u), Size(16 * u, 8 * u), style = Stroke(0.7f * u))
                fina(tz, tinta.copy(alpha = 0.5f), Offset(c.x - 6 * u, c.y), Offset(c.x + 6 * u, c.y), 0.8f * u)
            }
            TemaId.ORDEN -> {
                val bolsa = Path().apply { moveTo(c.x - 20 * u, c.y - 14 * u); lineTo(c.x + 20 * u, c.y - 14 * u); lineTo(c.x + 20 * u, c.y + 10 * u); quadraticTo(c.x + 20 * u, c.y + 18 * u, c.x + 12 * u, c.y + 18 * u); lineTo(c.x - 12 * u, c.y + 18 * u); quadraticTo(c.x - 20 * u, c.y + 18 * u, c.x - 20 * u, c.y + 10 * u); close() }
                drawPath(bolsa, tono.copy(alpha = 0.8f)); contorno(tz, bolsa, tinta, 1.2f * u)
                val tapa = Path().apply { moveTo(c.x - 20 * u, c.y - 14 * u); lineTo(c.x + 20 * u, c.y - 14 * u); lineTo(c.x + 20 * u, c.y - 2 * u); quadraticTo(c.x, c.y + 6 * u, c.x - 20 * u, c.y - 2 * u); close() }
                drawPath(tapa, lerp(tono, tinta, 0.3f)); contorno(tz, tapa, tinta, 1 * u)
                drawRect(tinta, Offset(c.x - 2.5f * u, c.y - 2 * u), Size(5 * u, 16 * u))
                drawRect(papel, Offset(c.x - 4 * u, c.y + 2 * u), Size(8 * u, 6 * u), style = Stroke(1.2f * u))
            }
            TemaId.LABORATORIO -> {
                hoja(c.x - 18 * u, c.y - 22 * u, c.x + 18 * u, c.y + 22 * u)
                drawRect(tono.copy(alpha = 0.6f), Offset(c.x - 14 * u, c.y - 18 * u), Size(28 * u, 10 * u))
                fina(tz, tinta, Offset(c.x - 11 * u, c.y - 13 * u), Offset(c.x + 8 * u, c.y - 13 * u), 0.9f * u)
                var x = c.x - 13 * u
                listOf(1f, 0.5f, 1.5f, 0.5f, 0.8f, 1.6f, 0.5f, 1f, 0.6f, 1.4f).forEach { a -> drawRect(tinta, Offset(x, c.y + 6 * u), Size(a * u, 10 * u)); x += (a + 1.1f) * u }
            }
            TemaId.COLONOS -> {
                hoja(c.x - 24 * u, c.y - 16 * u, c.x + 24 * u, c.y + 16 * u)
                drawRect(tono.copy(alpha = 0.8f), Offset(c.x - 24 * u, c.y - 16 * u), Size(48 * u, 7 * u))
                letra("TELEGRAMA", Offset(c.x, c.y - 12.5f * u), 4.5f * u, papel)
                for (k in 0 until 3) {
                    val y = c.y - 5 * u + k * 6 * u
                    drawRect(tinta.copy(alpha = 0.12f), Offset(c.x - 20 * u, y), Size((40 - k * 9) * u, 4 * u))
                    fina(tz, tinta, Offset(c.x - 19 * u, y + 2 * u), Offset(c.x - 21 * u + (40 - k * 9) * u, y + 2 * u), 0.8f * u)
                }
            }
            TemaId.NAPOLEON -> {
                hoja(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y + 15 * u)
                val s = caja(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y - 2 * u); drawPath(s, lerp(papel, tinta, 0.08f)); contorno(tz, s, tinta, 1 * u)
                drawCircle(Color(0xFFB0283F), 6 * u, Offset(c.x, c.y - 2 * u)); letra("N", Offset(c.x, c.y - 2 * u), 7 * u, Color(0xFFF2D58A))
            }
            TemaId.BOTANICO -> {
                val p = Path().apply {
                    moveTo(c.x - 14 * u, c.y + 20 * u); lineTo(c.x - 14 * u, c.y - 18 * u)
                    for (k in 0 until 7) { lineTo(c.x - 14 * u + (k * 4 + 2) * u, c.y - 21 * u); lineTo(c.x - 14 * u + (k * 4 + 4) * u, c.y - 18 * u) }
                    lineTo(c.x + 14 * u, c.y + 20 * u); close()
                }
                drawPath(p, papel); contorno(tz, p, tinta, 1.1f * u)
                drawRect(tinta, Offset(c.x - 10 * u, c.y - 12 * u), Size(20 * u, 20 * u), style = Stroke(0.7f * u))
                drawLine(Color(0xFF5C7A3A), Offset(c.x, c.y + 6 * u), Offset(c.x, c.y - 6 * u), 1.2f * u)
                listOf(-7f, 0f).forEach { dx -> drawOval(Color(0xFF5C7A3A), Offset(c.x + dx * u, c.y - 6 * u), Size(7 * u, 4 * u)) }
                fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x - 9 * u, c.y + 13 * u), Offset(c.x + 9 * u, c.y + 13 * u), 0.8f * u)
            }
            TemaId.NAVIDAD -> {
                hoja(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y + 15 * u)
                solapaV(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y + 3 * u)
                drawRect(Color(0xFFC62828), Offset(c.x + 12 * u, c.y - 13 * u), Size(9 * u, 11 * u))
                drawRect(papel, Offset(c.x + 12 * u, c.y - 13 * u), Size(9 * u, 11 * u), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1 * u, 1 * u))))
                listOf(-10f, 1f).forEach { dx -> drawOval(Color(0xFF2E6B3A), Offset(c.x + dx * u, c.y + 2 * u), Size(9 * u, 4.5f * u)) }
                listOf(Offset(-1.5f, 3f), Offset(1.5f, 3f), Offset(0f, 5.5f)).forEach { o -> drawCircle(Color(0xFFC62828), 1.6f * u, Offset(c.x + o.x * u, c.y + o.y * u)) }
            }
            TemaId.SEDA -> {
                hoja(c.x - 14 * u, c.y - 21 * u, c.x + 14 * u, c.y + 21 * u, Color(0xFFC0392B), 2 * u)
                contorno(tz, Path().apply { moveTo(c.x - 14 * u, c.y - 12 * u); quadraticTo(c.x, c.y - 2 * u, c.x + 14 * u, c.y - 12 * u) }, tinta, 0.9f * u)
                drawCircle(Color(0xFFF2C94C), 5.5f * u, Offset(c.x, c.y - 5 * u)); drawRect(Color(0xFFC0392B), Offset(c.x - 1.6f * u, c.y - 6.6f * u), Size(3.2f * u, 3.2f * u))
                for (k in 0 until 3) drawLine(Color(0xFFF2C94C), Offset(c.x - 3 * u, c.y + 5 * u + k * 4 * u), Offset(c.x + 3 * u, c.y + 5 * u + k * 4 * u), 1.2f * u)
            }
            TemaId.GOTICO -> {
                hoja(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y + 15 * u)
                val s = Path().apply { moveTo(c.x - 24 * u, c.y - 15 * u); lineTo(c.x, c.y + 10 * u); lineTo(c.x + 24 * u, c.y - 15 * u); close() }
                drawPath(s, lerp(papel, tinta, 0.1f)); contorno(tz, s, tinta, 1 * u)
                drawPath(estrellaN(Offset(c.x, c.y + 8 * u), 6 * u, 6, 0.6f), tono); drawCircle(Color(0xFFF5D76E), 1.6f * u, Offset(c.x, c.y + 8 * u))
            }
            TemaId.ESPACIO -> {
                hoja(c.x - 22 * u, c.y - 10 * u, c.x + 18 * u, c.y + 16 * u)
                solapaV(c.x - 22 * u, c.y - 10 * u, c.x + 18 * u, c.y + 5 * u)
                val o = Offset(c.x + 18 * u, c.y - 10 * u)
                for (k in 0 until 3) { val rr = (6 + k * 5) * u; drawArc(tono, -80f, 70f, false, Offset(o.x - rr, o.y - rr), Size(2 * rr, 2 * rr), style = tz.st(1.4f * u)) }
            }
            // Un mensaje en una botella: el vidrio verde tumbado, el corcho y el papel enrollado dentro.
            TemaId.ABISMO -> {
                val botella = Path().apply {
                    moveTo(c.x - 22 * u, c.y - 9 * u); lineTo(c.x + 8 * u, c.y - 9 * u); quadraticTo(c.x + 14 * u, c.y - 9 * u, c.x + 16 * u, c.y - 4 * u)
                    lineTo(c.x + 24 * u, c.y - 3 * u); lineTo(c.x + 24 * u, c.y + 3 * u); lineTo(c.x + 16 * u, c.y + 4 * u)
                    quadraticTo(c.x + 14 * u, c.y + 9 * u, c.x + 8 * u, c.y + 9 * u); lineTo(c.x - 22 * u, c.y + 9 * u)
                    quadraticTo(c.x - 26 * u, c.y, c.x - 22 * u, c.y - 9 * u); close()
                }
                drawPath(botella, tono.copy(alpha = 0.35f))
                drawRoundRect(papel, Offset(c.x - 16 * u, c.y - 4.5f * u), Size(22 * u, 9 * u), CornerRadius(4.5f * u))
                fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x - 13 * u, c.y), Offset(c.x + 3 * u, c.y), 0.8f * u)
                drawRect(Color(0xFFB08A5A), Offset(c.x + 24 * u, c.y - 3 * u), Size(5 * u, 6 * u))
                contorno(tz, botella, tinta, 1.2f * u)
                drawLine(Color.White.copy(alpha = 0.6f), Offset(c.x - 18 * u, c.y - 6.5f * u), Offset(c.x + 4 * u, c.y - 6.5f * u), 1.2f * u)
            }
            TemaId.ANDALUS -> {
                hoja(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y + 15 * u)
                val s = Path().apply {
                    moveTo(c.x - 24 * u, c.y - 15 * u); lineTo(c.x - 24 * u, c.y - 10 * u)
                    for (k in 0 until 3) { val x0 = c.x - 24 * u + k * 16 * u; quadraticTo(x0 + 8 * u, c.y + (if (k == 1) 10 else 2) * u, x0 + 16 * u, c.y - 10 * u) }
                    lineTo(c.x + 24 * u, c.y - 15 * u); close()
                }
                drawPath(s, lerp(papel, tinta, 0.08f)); contorno(tz, s, tinta, 1 * u)
                drawPath(estrellaN(Offset(c.x, c.y + 3 * u), 5.5f * u, 8, 0.72f), tono)
            }
            TemaId.MONGOL -> rotate(-30f, c) {
                raya(tz, tinta, Offset(c.x - 26 * u, c.y), Offset(c.x + 22 * u, c.y), 1.4f * u)
                drawPath(Path().apply { moveTo(c.x + 28 * u, c.y); lineTo(c.x + 21 * u, c.y - 3.5f * u); lineTo(c.x + 21 * u, c.y + 3.5f * u); close() }, tinta)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(c.x - 26 * u, c.y); lineTo(c.x - 20 * u, c.y); lineTo(c.x - 16 * u, c.y + s * 5 * u); lineTo(c.x - 22 * u, c.y + s * 5 * u); close() }, tono) }
                hoja(c.x - 6 * u, c.y - 5 * u, c.x + 8 * u, c.y + 5 * u, papel, 2.5f * u)
                listOf(-3f, 5f).forEach { dx -> drawLine(Color(0xFFC2551F), Offset(c.x + dx * u, c.y - 5 * u), Offset(c.x + dx * u, c.y + 5 * u), 1 * u) }
            }
            TemaId.RENACIMIENTO -> {
                hoja(c.x - 16 * u, c.y - 12 * u, c.x + 16 * u, c.y + 12 * u)
                fina(tz, tinta.copy(alpha = 0.5f), Offset(c.x - 16 * u, c.y - 12 * u), Offset(c.x - 4 * u, c.y), 0.7f * u); fina(tz, tinta.copy(alpha = 0.5f), Offset(c.x + 16 * u, c.y - 12 * u), Offset(c.x + 4 * u, c.y), 0.7f * u)
                drawRect(tono.copy(alpha = 0.85f), Offset(c.x - 2 * u, c.y - 18 * u), Size(4 * u, 36 * u))
                drawCircle(Color(0xFFB0302A), 3.5f * u, Offset(c.x, c.y + 4 * u))
            }
            TemaId.URSS -> {
                val t = Path().apply { moveTo(c.x - 24 * u, c.y - 12 * u); lineTo(c.x + 24 * u, c.y - 12 * u); lineTo(c.x, c.y + 18 * u); close() }
                drawPath(t, papel); contorno(tz, t, tinta, 1.2f * u)
                contorno(tz, Path().apply { moveTo(c.x - 12 * u, c.y - 12 * u); lineTo(c.x, c.y + 3 * u); lineTo(c.x + 12 * u, c.y - 12 * u) }, tinta.copy(alpha = 0.5f), 0.7f * u)
                for (k in 0 until 2) fina(tz, tinta.copy(alpha = 0.6f), Offset(c.x - 17 * u + k * 3 * u, c.y - 8 * u + k * 4 * u), Offset(c.x - 9 * u + k * 1 * u, c.y - 8 * u + k * 4 * u), 0.8f * u)
                drawPath(estrellaN(Offset(c.x + 12 * u, c.y - 6 * u), 3 * u, 5), Color(0xFFC62828))
            }
            TemaId.REVOLUCION -> {
                hoja(c.x - 24 * u, c.y - 15 * u, c.x + 24 * u, c.y + 15 * u)
                val cols = listOf(Color(0xFF1F3A8A), Color(0xFFC62828))
                var x = c.x - 24 * u; var k = 0
                while (x < c.x + 22 * u) { drawRect(cols[k % 2], Offset(x, c.y - 15 * u), Size(3 * u, 2 * u)); drawRect(cols[(k + 1) % 2], Offset(x, c.y + 13 * u), Size(3 * u, 2 * u)); x += 4 * u; k++ }
                solapaV(c.x - 24 * u, c.y - 13 * u, c.x + 24 * u, c.y + 3 * u)
                escarapela(Offset(c.x, c.y + 3 * u), 4 * u)
            }
            TemaId.ANTIGUAS -> {
                val p = Path().apply { moveTo(c.x - 23 * u, c.y - 14 * u); lineTo(c.x - 6 * u, c.y - 16 * u); lineTo(c.x + 24 * u, c.y - 13 * u); lineTo(c.x + 22 * u, c.y + 15 * u); lineTo(c.x + 4 * u, c.y + 16 * u); lineTo(c.x - 24 * u, c.y + 13 * u); close() }
                drawPath(p, papel); contorno(tz, p, tinta, 1.1f * u)
                drawLine(tinta, Offset(c.x - 23 * u, c.y - 14 * u), Offset(c.x + 22 * u, c.y + 15 * u), 1 * u); drawLine(tinta, Offset(c.x + 24 * u, c.y - 13 * u), Offset(c.x - 24 * u, c.y + 13 * u), 1 * u)
                drawCircle(tono, 6 * u, c)
                drawLine(papel, Offset(c.x - 1 * u, c.y - 4 * u), Offset(c.x - 1 * u, c.y + 4 * u), 1 * u); drawLine(papel, Offset(c.x - 1 * u, c.y - 1 * u), Offset(c.x + 3 * u, c.y - 4 * u), 1 * u); drawLine(papel, Offset(c.x - 1 * u, c.y + 2 * u), Offset(c.x + 3 * u, c.y - 1 * u), 1 * u)
            }
        }
    }
}

/** Un logro dibujado (su pieza), del tamaño que se pida. */
@Composable
fun PiezaLogro(l: Logros.Logro, tam: Dp, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    Canvas(modifier.size(tam)) { with(PiezasLogro) { pieza(l, tema, cs) } }
}

/** El fondo del salón: la escena del tema ([SalonesTema]). */
@Composable
private fun BoxScope.FondoSalon(tema: TemaId) {
    val cs = MaterialTheme.colorScheme
    Canvas(Modifier.matchParentSize()) {
        with(SalonesTema) { escena(tema, cs) }
    }
}

/** El hall: las piezas en sus pedestales con el salón del tema detrás, y el editor para elegir cuáles (hasta 12). Va arriba de Logros. */
@Composable
fun CuerpoHall(l: List<Logros.Logro>) {
    var editar by remember { mutableStateOf(false) }
    var abierto by remember { mutableStateOf<Logros.Logro?>(null) }
    var conTextos by remember { mutableStateOf(true) }
    val tema = Estilo.tema
    val cs = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    abierto?.let { a -> DetalleTrofeo(a, l) { abierto = null } }
    Column {
        val hechos = l.filter { it.hecho }
        val ids = Repo.config.hall_logros.filter { id -> hechos.any { it.id == id } }
        val enHall = ids.mapNotNull { id -> hechos.firstOrNull { it.id == id } }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${enHall.size} de 12 lugares", style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            TextButton(onClick = { editar = !editar }) { Text(if (editar) "listo" else "elegir piezas", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
        }
        if (enHall.isEmpty() && !editar) {
            Vacio(if (hechos.isEmpty()) "Cuando consigas logros, aquí podrás exhibir los que más te gusten." else "Tu hall está vacío. Elige qué exhibir o llénalo con los mejores.")
            if (hechos.isNotEmpty()) BotonPrincipal("Llenar con mis mejores logros", Modifier.fillMaxWidth().padding(top = 8.dp)) {
                val mejores = hechos.sortedWith(compareByDescending<Logros.Logro> { it.nivel }.thenBy { it.conseguido }).distinctBy { it.id.substringBeforeLast('_') }.take(8).map { it.id }
                Repo.cambiarConfig { c -> c.copy(hall_logros = mejores) }
            }
        }
        if (enHall.isNotEmpty() && !editar) Box(Modifier.fillMaxWidth().padding(top = 8.dp).background(cs.surface.copy(alpha = 0.6f), MaterialTheme.shapes.medium)) {
            FondoSalon(tema)
            Column(Modifier.padding(vertical = 12.dp)) {
                enHall.chunked(2).forEach { fila ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        fila.forEach { g -> Pedestal(g, tema, Modifier.weight(1f).clickable { abierto = g }) }
                        if (fila.size == 1) Box(Modifier.weight(1f))
                    }
                }
            }
        }
        if (enHall.isNotEmpty() && !editar) {
            Ayuda("Toca una pieza para ver cómo la conseguiste.", Modifier.padding(top = 6.dp))
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = conTextos, onCheckedChange = { conTextos = it })
                Text("con nombres y fechas", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f).clickable { conTextos = !conTextos })
                TextButton(onClick = {
                    runCatching { HallImagen.crear(ctx, enHall, tema, cs, conTextos) }.onSuccess { LibroDiario.compartir(ctx, it, "image/png") }
                }) { Text("guardar imagen", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
            }
        }
        if (editar) {
            Ayuda("Toca para poner o quitar. Hasta 12. Las flechas cambian el orden.", Modifier.padding(top = 6.dp))
            if (enHall.isNotEmpty()) Tarjeta(Modifier.padding(top = 8.dp)) {
                Text("En el hall", style = MaterialTheme.typography.titleMedium)
                enHall.forEachIndexed { i, g ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        TrofeoLogro(g, 44.dp, tema = tema)
                        Text(g.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f).padding(start = 10.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        TextButton(onClick = { mover(ids, i, -1) }, enabled = i > 0) { Text("↑") }
                        TextButton(onClick = { mover(ids, i, 1) }, enabled = i < ids.lastIndex) { Text("↓") }
                    }
                }
            }
            var cat by remember { mutableStateOf<String?>(null) }
            Row(Modifier.padding(top = 10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("todos", cat == null) { cat = null }
                    Logros.CATEGORIAS.filter { c -> hechos.any { it.categoria == c } }.forEach { c -> Ficha(c, cat == c) { cat = c } }
                }
            }
            Tarjeta(Modifier.padding(top = 8.dp)) {
                hechos.filter { cat == null || it.categoria == cat }.sortedByDescending { it.conseguido }.forEachIndexed { i, g ->
                    if (i > 0) HorizontalDivider()
                    val dentro = g.id in ids
                    Row(Modifier.fillMaxWidth().clickable {
                        Repo.cambiarConfig { c -> c.copy(hall_logros = if (dentro) c.hall_logros - g.id else if (c.hall_logros.size >= 12) c.hall_logros else c.hall_logros + g.id) }
                    }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = dentro, onCheckedChange = null)
                        TrofeoLogro(g, 40.dp, tema = tema)
                        Column(Modifier.weight(1f).padding(start = 10.dp)) {
                            Text(g.nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(g.categoria + " · " + Fechas.corto(g.conseguido!!), style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private fun mover(ids: List<String>, i: Int, d: Int) {
    val j = i + d
    if (j !in ids.indices) return
    val n = ids.toMutableList(); val x = n[i]; n[i] = n[j]; n[j] = x
    Repo.cambiarConfig { c -> c.copy(hall_logros = n + c.hall_logros.filter { it !in n }) }
}

/** Una pieza en su pedestal, con la placa: nombre, fecha y nivel. */
@Composable
private fun Pedestal(g: Logros.Logro, tema: TemaId, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    val metal = metalHall(tema, g.nivel)
    Column(modifier.padding(horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // El trofeo del tema (el mismo de la sala de trofeos), sobre la repisa del salón.
        TrofeoLogro(g, 118.dp, tema = tema)
        // El pedestal del tema (ver [SalonesTema.pedestal]).
        Canvas(Modifier.fillMaxWidth().height(22.dp).padding(horizontal = 18.dp)) {
            with(SalonesTema) { pedestal(tema, cs, metal) }
        }
        Box(Modifier.padding(top = 4.dp).background(metal.copy(alpha = 0.22f), MaterialTheme.shapes.small).padding(horizontal = 8.dp, vertical = 4.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(g.nombre, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(Fechas.corto(g.conseguido ?: "") + " · " + g.conseguido?.take(4), style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, fontSize = 10.sp)
            }
        }
    }
}

