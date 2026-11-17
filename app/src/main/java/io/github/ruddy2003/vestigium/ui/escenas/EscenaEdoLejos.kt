package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.arbol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo lejano del cuadro de Japón Edo: el cielo, el Fuji con su nieve, los montes de delante con sus bancos de bruma, el
 * llano de arrozales con el río del vado y sus pueblos, la fila de pinos del camino, y un cerezo junto al jizō. De atrás adelante, cada plano con menos bruma que el de detrás.
 */
internal object LejosEdo {
    private val C = ColorEdo

    /** El horizonte: al 46 % del alto. */
    const val H = 132f
    /** Donde empieza el camino (su borde de enfrente). */
    const val CAMINO = 212f

    /** La cumbre del Fuji y su ladera, puesta a mano: la cumbre roma, las faldas cóncavas y largas. */
    internal val FUJI = listOf(
        Offset(-40f, H + 2f), Offset(10f, 124f), Offset(46f, 110f), Offset(78f, 96f), Offset(104f, 80f), Offset(128f, 62f), Offset(146f, 48f), Offset(160f, 39f),
        Offset(168f, 35.4f), Offset(176f, 34.6f), Offset(184f, 35.6f), Offset(190f, 34.4f), Offset(198f, 36f), Offset(212f, 46f), Offset(232f, 60f), Offset(256f, 78f),
        Offset(282f, 94f), Offset(312f, 108f), Offset(350f, 120f), Offset(410f, H + 2f),
    )

    /** La x de la ladera del Fuji a la altura [y], del lado izquierdo o del derecho: para la nieve y para el reflejo. */
    private fun ladera(y: Float, derecha: Boolean): Float {
        val pts = if (derecha) FUJI.drop(11) else FUJI.take(9).reversed()
        for (i in 0 until pts.size - 1) { val a = pts[i]; val b = pts[i + 1]; if ((y - a.y) * (y - b.y) <= 0f && a.y != b.y) return a.x + (b.x - a.x) * (y - a.y) / (b.y - a.y) }
        return if (derecha) 410f else -40f
    }

    fun DrawScope.lejos(m: Momento, l: Luces, p: Pincel) {
        val semilla = m.ficha.semilla
        val fijo = Azar(Semilla.de("edo", "tierra"))
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        fuji(m, l, p, fijo)
        montes(m, l, p, fijo)
        llano(m, l, p, fijo)
    }

    // ---------------------------------------------------------------- El Fuji

    /**
     * El Fuji: azul violeta, con la ladera del sol más clara; la nieve baja más en invierno y deja dedos blancos por los
     * barrancos; de noche, la nieve recoge la luna. A veces, el sombrero de nubes en la cumbre (cuando va a llover).
     */
    private fun DrawScope.fuji(m: Momento, l: Luces, p: Pincel, fijo: Azar) {
        val lejos = 0.6f
        val forma = curva(FUJI, H + 3f)
        val luz = p.luz(C.FUJI, lejos); val sombra = p.sombra(C.FUJI, lejos); val medio = p.medio(C.FUJI, lejos)
        drawPath(forma, Brush.horizontalGradient(if (p.solALaDerecha) listOf(sombra, medio, luz) else listOf(luz, medio, sombra), 40f, 330f))
        val nieve = EscenaEdo.nieveEnElFuji(m)
        clipPath(forma) {
            // Las cárcavas: rayas oscuras que bajan de la cumbre.
            for (i in 0 until 14) {
                val t = i / 13f; val x0 = 166f + 28f * t; val dir = (t - 0.5f) * 2f
                drawPath(Path().apply { moveTo(x0, 38f); cubicTo(x0 + dir * 20f, 64f, x0 + dir * 60f, 96f, x0 + dir * 110f, H + 2f) }, mult(medio, C.SOMBRA, 0.4f).copy(alpha = 0.22f), style = Stroke(1f))
            }
            // La nieve: hasta la línea de la estación, con dedos que bajan por los barrancos.
            val linea = 40f + (H - 46f) * (0.1f + 0.6f * nieve)
            val blanco = l.tono(C.NIEVE, lejos * 0.8f); val blancoSombra = l.tono(mult(C.NIEVE, C.SOMBRA, 0.6f), lejos * 0.8f)
            val borde = Path().apply {
                moveTo(-40f, 0f); lineTo(420f, 0f); lineTo(ladera(linea, true) + 4f, linea)
                var x = ladera(linea, true)
                val x0 = ladera(linea, false)
                var i = 0
                while (x > x0) {
                    // La nieve no acaba en una raya: unas pocas lenguas largas bajan por las cárcavas; entre ellas, el borde casi llano.
                    val w = fijo.entre(18f, 30f); x -= w; i++
                    val dedo = if (i % 3 == 1) fijo.entre(6f, 12f) * (0.6f + 0.4f * nieve) else fijo.entre(0f, 2f)
                    cubicTo(x + w * 0.8f, linea + dedo * 0.15f, x + w * 0.62f, linea + dedo, x + w * 0.5f, linea + dedo); cubicTo(x + w * 0.38f, linea + dedo, x + w * 0.2f, linea + dedo * 0.15f, x, linea + fijo.entre(-0.8f, 0.8f))
                }
                lineTo(x0 - 4f, linea); close()
            }
            clipPath(borde) { drawPath(forma, Brush.horizontalGradient(if (p.solALaDerecha) listOf(blancoSombra, blanco, blanco) else listOf(blanco, blanco, blancoSombra), 120f, 250f)) }
            if (l.noche > 0.4f) clipPath(borde) { drawPath(forma, Color(0xFFDDE4FF).copy(alpha = 0.18f * l.noche * l.estrellas)) }
        }
        // El sombrero de nubes: si va a llover (el cuento: «si se pone el sombrero de nubes, llueve»).
        if (m.ficha.clima == io.github.ruddy2003.vestigium.vidas.Clima.NUBES && m.azar("sombrero").real() > 0.5f) {
            val c = Offset(180f, 30f)
            for ((dx, w) in listOf(-14f to 26f, 0f to 34f, 14f to 26f)) mancha(c + Offset(dx, 0f), w, 4f, l.tono(Color(0xFFF2F2EE), 0.4f), 0.9f)
            mancha(c + Offset(0f, 3f), 30f, 2f, l.tono(mult(Color.White, C.SOMBRA, 0.5f), 0.4f), 0.6f)
        }
    }

    // ---------------------------------------------------------------- Los montes y la bruma

    private fun DrawScope.montes(m: Momento, l: Luces, p: Pincel, fijo: Azar) {
        // Una banda de bruma al pie del Fuji, que lo despega de los montes.
        mancha(Offset(190f, H - 6f), 260f, 7f, l.bruma, 0.5f)
        monte(l, perfil(fijo, -20f, 410f, H + 2f, 20f, 0.45f, 30), H + 6f, C.MONTE, 0.55f)
        // Bancos de bruma entre los montes, a lo largo (la «kasumi» de las estampas, aquí en pinceladas de gouache).
        for ((y, a) in listOf(H - 1f to 0.55f, H + 4f to 0.45f)) mancha(Offset(180f, y), 240f, 3.4f, lerp(l.bruma, Color.White, 0.25f), a * (1f - 0.5f * l.noche))
        monte(l, perfil(fijo, -20f, 240f, H + 8f, 14f, 0.5f, 22), H + 10f, lerp(C.PINO, C.MONTE, 0.4f), 0.4f)
        // Los pinos de la loma: puntas oscuras en la cresta.
        val az = Azar(Semilla.de("edo", "loma"))
        repeat(26) { val x = az.entre(0f, 236f); val y = H + 3f + az.entre(0f, 5f); drawPath(poligono(Offset(x - 1.6f, y + 2f), Offset(x + 1.6f, y + 2f), Offset(x, y - 3f)), l.tono(lerp(C.PINO, C.SUMI, 0.3f), 0.38f)) }
        mancha(Offset(120f, H + 10f), 200f, 2.6f, lerp(l.bruma, Color.White, 0.2f), 0.4f * (1f - 0.5f * l.noche))
    }

    // ---------------------------------------------------------------- El llano: arrozales, río, pueblos y pinos

    /** El río: de la izquierda, lejos, hacia el vado, por detrás de las casas. Puesto a mano, con sus meandros. */
    private val RIO_ARRIBA = listOf(Offset(-10f, 147f), Offset(20f, 148.6f), Offset(48f, 147.4f), Offset(80f, 150f), Offset(110f, 152.6f), Offset(140f, 152f), Offset(168f, 155.4f), Offset(200f, 157f), Offset(240f, 158f))
    private val RIO_ABAJO = listOf(Offset(-10f, 151f), Offset(22f, 153.6f), Offset(50f, 152.2f), Offset(82f, 156.2f), Offset(112f, 159.4f), Offset(142f, 159f), Offset(170f, 162.4f), Offset(200f, 164.4f), Offset(240f, 165.6f))

    private fun DrawScope.llano(m: Momento, l: Luces, p: Pincel, fijo: Azar) {
        val az = Azar(Semilla.de("edo", "llano"))
        val estado = EscenaEdo.arrozal(m)
        // La tierra del llano, de la loma al camino.
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(C.ARROZ, C.MONTE, 0.35f), 0.4f), l.tono(lerp(C.ARROZ, C.TIERRA, 0.3f), 0.15f)), H + 6f, CAMINO), Offset(0f, H + 6f), Size(ANCHO, CAMINO - H - 6f))
        // Los arrozales: bancales que se agrandan hacia quien mira, separados por caballones que corren hacia el fondo
        // (hacia el pie del Fuji) y por los que van de lado.
        val filas = listOf(H + 8f, 142f, 146.4f, 160f, 167f, 176f, 188f, 200f, CAMINO)
        val fuga = Offset(176f, H + 2f)
        fun xEn(k: Float, y: Float) = fuga.x + k * (y - fuga.y)                          // un caballón que va hacia el fondo
        val rayos = (-14..14).map { it * 2.2f + az.entre(-0.6f, 0.6f) }
        for (f in 0 until filas.size - 1) {
            val y0 = filas[f]; val y1 = filas[f + 1]; val lejos = (0.42f - f * 0.05f).coerceAtLeast(0.05f)
            if (y0 >= 146f && y1 <= 160f) continue                                          // el río va por aquí
            for (i in 0 until rayos.size - 1) {
                val q = listOf(Offset(xEn(rayos[i], y0), y0), Offset(xEn(rayos[i + 1], y0), y0), Offset(xEn(rayos[i + 1], y1), y1), Offset(xEn(rayos[i], y1), y1))
                if (q.maxOf { it.x } < -10f || q.minOf { it.x } > ANCHO + 10f) continue
                bancal(m, l, p, poligono(q), Rect(q.minOf { it.x }, y0, q.maxOf { it.x }, y1), estado, lejos, az)
            }
            // El caballón de lado: una franja de hierba clara con su lado en sombra.
            drawLine(l.tono(lerp(C.ARROZ, C.PAJA, 0.4f), lejos), Offset(0f, y1 - 0.4f), Offset(ANCHO, y1 - 0.4f), 0.8f + f * 0.14f)
            drawLine(p.sombra(C.TIERRA, lejos).copy(alpha = 0.5f), Offset(0f, y1 + 0.4f), Offset(ANCHO, y1 + 0.4f), 0.5f)
        }
        // Los caballones que van hacia el fondo, encima.
        for (k in rayos) for (f in 0 until filas.size - 1) {
            val y0 = filas[f]; val y1 = filas[f + 1]; if (y0 >= 146f && y1 <= 160f) continue
            drawLine(l.tono(lerp(C.ARROZ, C.PAJA, 0.35f), 0.3f).copy(alpha = 0.8f), Offset(xEn(k, y0), y0), Offset(xEn(k, y1), y1), 0.5f + 0.06f * f)
        }
        rio(m, l, p, fijo)
        pueblos(m, l, p)
        pinos(m, l, p)
    }

    /** Un bancal de arroz según la época: el espejo del agua, las matas en fila, el verde, el oro o el rastrojo con sus haces. */
    private fun DrawScope.bancal(m: Momento, l: Luces, p: Pincel, forma: Path, r: Rect, estado: EscenaEdo.Arrozal, lejos: Float, az: Azar) {
        val nevado = EscenaEdo.nevado(m)
        val tono = az.entre(-0.12f, 0.12f)
        clipPath(forma) { when {
            nevado -> drawRect(l.tono(lerp(C.NIEVE, C.FUJI, 0.08f), lejos), r.topLeft, r.size)
            estado == EscenaEdo.Arrozal.INUNDADO || estado == EscenaEdo.Arrozal.PLANTADO -> {
                // El agua de los bancales devuelve el cielo, más claro arriba; y, en los de cerca, el Fuji del revés.
                drawRect(Brush.verticalGradient(listOf(lerp(lerp(l.bajo, l.medio, 0.4f), l.tono(C.AGUA, lejos), 0.25f), lerp(lerp(l.medio, l.alto, 0.3f), l.tono(C.AGUA_HONDA, lejos), 0.3f)), r.top, r.bottom), r.topLeft, r.size)
                if (r.top > 170f) clipRect(r.left, r.top, r.right, r.bottom) { val c = Offset(176f, r.top); drawPath(poligono(c + Offset(-90f, 0f), c + Offset(90f, 0f), c + Offset(0f, (r.bottom - r.top) * 2.2f)), p.medio(C.FUJI, 0.5f).copy(alpha = 0.18f)) }
                if (estado == EscenaEdo.Arrozal.PLANTADO && lejos < 0.3f) { var y = r.top + 2f; while (y < r.bottom - 0.5f) { var x = r.left + 2f; while (x < r.right) { drawLine(l.tono(C.ARROZ, lejos).copy(alpha = 0.7f), Offset(x, y), Offset(x + 0.4f, y - 1.2f - (1f - lejos)), 0.5f); x += 3.4f + 3f * (1f - lejos) }; y += 2.6f + 3.4f * (1f - lejos) } }
            }
            estado == EscenaEdo.Arrozal.VERDE -> { drawRect(l.tono(lerp(C.ARROZ, if (az.real() > 0.5f) C.PINO else Color(0xFF9CC060), az.entre(0f, 0.3f)), lejos), r.topLeft, r.size); var x = r.left; while (x < r.right) { drawLine(l.tono(lerp(C.ARROZ, C.PINO, 0.4f), lejos).copy(alpha = 0.4f), Offset(x, r.top), Offset(x - 2f, r.bottom), 0.4f); x += 2.4f } }
            estado == EscenaEdo.Arrozal.DORADO -> { drawRect(Brush.verticalGradient(listOf(l.tono(lerp(C.ORO, C.PAJA, 0.3f), lejos), l.tono(lerp(C.ORO, C.ARROZ, az.entre(0f, 0.25f)), lejos)), r.top, r.bottom), r.topLeft, r.size); if (lejos < 0.3f) repeat((r.width / 3f).toInt()) { val q = Offset(az.entre(r.left, r.right), az.entre(r.top + 1f, r.bottom)); drawLine(l.tono(Color(0xFFF2D88A), lejos), q, q + Offset(0.5f, -1.6f), 0.5f) } }
            estado == EscenaEdo.Arrozal.SEGADO -> {
                drawRect(l.tono(lerp(C.PAJA, C.TIERRA, 0.4f), lejos), r.topLeft, r.size)
                // Los varales donde se secan los haces (hasa), en los de cerca.
                if (lejos < 0.32f && az.real() > 0.4f) { val y = r.bottom - 1f; val a = r.left + 4f; val b = minOf(r.right - 4f, a + 26f); val h = 4f + 6f * (1f - lejos)
                    drawLine(l.tono(C.MADERA_OSC, lejos), Offset(a, y), Offset(a, y - h), 0.6f); drawLine(l.tono(C.MADERA_OSC, lejos), Offset(b, y), Offset(b, y - h), 0.6f)
                    drawRect(Brush.verticalGradient(listOf(l.tono(C.ORO, lejos), l.tono(lerp(C.PAJA, C.TIERRA, 0.3f), lejos)), y - h, y - h * 0.3f), Offset(a, y - h), Size(b - a, h * 0.7f)) }
            }
            else -> {
                // Seco: el rastrojo en filas, y escarcha las mañanas frías.
                drawRect(l.tono(lerp(C.TIERRA, C.PAJA, az.entre(0.1f, 0.4f)), lejos), r.topLeft, r.size)
                var x = r.left; while (x < r.right) { drawLine(p.sombra(C.TIERRA, lejos).copy(alpha = 0.35f), Offset(x, r.top), Offset(x - 1f, r.bottom), 0.4f); x += 2f }
                if (m.hora < 9.0 && EscenaEdo.mes(m) in setOf(12, 1, 2)) drawRect(l.tono(C.NIEVE, lejos).copy(alpha = 0.3f), r.topLeft, r.size)
            }
        }
            // Cada bancal, un poco distinto del de al lado.
            drawRect(if (tono > 0f) Color.White.copy(alpha = tono * 0.5f) else Color.Black.copy(alpha = -tono * 0.5f), r.topLeft, r.size)
        }
    }

    /** El río y su vado: el agua con sus orillas de grava, el reflejo de los montes y el banco de los porteadores en la orilla. */
    private fun DrawScope.rio(m: Momento, l: Luces, p: Pincel, fijo: Azar) {
        val crecido = EscenaEdo.rioCerrado(m)
        val arriba = if (crecido) RIO_ARRIBA.map { it + Offset(0f, -1.6f) } else RIO_ARRIBA; val abajo = if (crecido) RIO_ABAJO.map { it + Offset(0f, 2.4f) } else RIO_ABAJO
        // La grava de las orillas: la franja de paso entre el arrozal y el agua.
        drawPath(Path().apply { val a = arriba.map { it + Offset(0f, -1.6f) }; val b = abajo.map { it + Offset(0f, 2f) }; moveTo(a.first().x, a.first().y); a.drop(1).forEach { lineTo(it.x, it.y) }; b.reversed().forEach { lineTo(it.x, it.y) }; close() }, l.tono(lerp(C.YESO, C.TIERRA, 0.4f), 0.3f))
        val forma = Path().apply { moveTo(arriba.first().x, arriba.first().y); arriba.drop(1).forEach { lineTo(it.x, it.y) }; abajo.reversed().forEach { lineTo(it.x, it.y) }; close() }
        clipPath(forma) {
            if (crecido) drawRect(Brush.verticalGradient(listOf(l.tono(lerp(C.BARRO, C.AGUA, 0.3f), 0.3f), l.tono(lerp(C.BARRO, C.TIERRA, 0.3f), 0.2f)), 144f, 168f), Offset(-10f, 144f), Size(260f, 24f))
            else agua(l, Rect(-10f, 146f, 250f, 167f), m.azar("rio"), C.AGUA, m.viento, null, C.AGUA_HONDA, C.AGUA_ORILLA)
            val az = Azar(Semilla.de("edo", "rio"))
            for (i in 0 until arriba.size - 1) reflejoRoto(arriba[i].x, arriba[i + 1].x, arriba[i].y, 3f, l.tono(lerp(C.PINO, C.MONTE, 0.4f), 0.35f), 0.4f, az, 0.6f)
            estela(l, Rect(-10f, 146f, 250f, 167f), m.azar("estela"))
            if (crecido) repeat(30) { val q = Offset(az.entre(-10f, 240f), az.entre(146f, 166f)); drawLine(l.tono(C.YESO, 0.25f).copy(alpha = 0.5f), q, q + Offset(az.entre(3f, 7f), 0f), 0.6f) }
        }
        // El banco de los porteadores (rendai) en la orilla de este lado; el día que el río está cerrado, subido a lo alto.
        val b = if (crecido) Offset(98f, 168f) else Offset(104f, 163.4f)
        drawRect(p.arriba(C.MADERA, 0.25f), Offset(b.x - 7f, b.y - 3.2f), Size(14f, 1.4f))
        for (dx in listOf(-6f, 6f)) drawLine(p.medio(C.MADERA_OSC, 0.25f), Offset(b.x + dx, b.y - 2f), Offset(b.x + dx, b.y + 1f), 0.6f)
        for (dx in listOf(-8f, 8f)) drawLine(p.medio(C.MADERA, 0.25f), Offset(b.x + dx * 1.3f, b.y - 2.6f), Offset(b.x + dx * 0.8f, b.y - 2.6f), 0.6f)
        if (crecido) { drawLine(p.medio(C.MADERA, 0.25f), Offset(116f, 168f), Offset(116f, 156f), 0.7f); drawRect(l.tono(C.BERMELLON, 0.25f), Offset(116.4f, 156f), Size(4f, 3f)) }
    }

    /** Dos pueblos de casas de labor con tejado de paja, lejos en el llano, con su humo. */
    private fun DrawScope.pueblos(m: Momento, l: Luces, p: Pincel) {
        val az = Azar(Semilla.de("edo", "pueblos"))
        val noche = l.noche > 0.4f
        for ((x0, base, lejos, n) in listOf(Quad(40f, 145.6f, 0.38f, 5), Quad(130f, 145.8f, 0.38f, 4), Quad(160f, 173.6f, 0.22f, 3))) {
            var x = x0
            repeat(n) {
                val w = az.entre(5f, 8f) * (1.3f - lejos); val h = w * 0.45f
                sombraCorta(p.r, Offset(x + w / 2f, base), w, h)
                drawRect(p.frente(C.YESO, lejos), Offset(x, base - h), Size(w, h))
                drawRect(l.tono(C.MADERA_OSC, lejos), Offset(x + w * 0.3f, base - h * 0.7f), Size(w * 0.2f, h * 0.7f))
                if (noche && az.real() > 0.5f) drawRect(LUZ_DE_CASA.copy(alpha = 0.8f), Offset(x + w * 0.62f, base - h * 0.6f), Size(maxOf(0.6f, w * 0.12f), maxOf(0.6f, h * 0.3f)))
                // El tejado de paja, alto y redondeado, con su cumbrera.
                drawPath(Path().apply { moveTo(x - w * 0.12f, base - h); cubicTo(x + w * 0.1f, base - h - w * 0.55f, x + w * 0.9f, base - h - w * 0.55f, x + w * 1.12f, base - h); close() },
                    Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.PAJA, lejos), p.luz(C.PAJA, lejos)) else listOf(p.luz(C.PAJA, lejos), p.sombra(C.PAJA, lejos)), x, x + w))
                drawLine(l.tono(C.MADERA_OSC, lejos), Offset(x + w * 0.25f, base - h - w * 0.4f), Offset(x + w * 0.75f, base - h - w * 0.4f), 0.6f)
                if (EscenaEdo.nevado(m)) drawPath(Path().apply { moveTo(x + w * 0.05f, base - h - w * 0.2f); cubicTo(x + w * 0.2f, base - h - w * 0.5f, x + w * 0.8f, base - h - w * 0.5f, x + w * 0.95f, base - h - w * 0.2f); close() }, l.tono(C.NIEVE, lejos))
                x += w + az.entre(1f, 4f)
            }
            // Los árboles del pueblo.
            repeat(3) { val q = Offset(x0 + az.entre(-6f, 34f), base + 0.4f); drawCircle(l.tono(lerp(C.PINO, C.MONTE, 0.3f), lejos), az.entre(2f, 3.4f), q + Offset(0f, -3f)) }
        }
    }

    /**
     * La fila de pinos negros que bordea el camino real hacia el oeste: los de cerca grandes, con sus pisos de ramas
     * tendidas, cada uno con su lado de luz; los de lejos, más chicos y azulados.
     */
    private fun DrawScope.pinos(m: Momento, l: Luces, p: Pincel) {
        val az = Azar(Semilla.de("edo", "pinos"))
        val sitios = listOf(Triple(222f, 178f, 18f), Triple(206f, 184f, 26f), Triple(184f, 192f, 40f), Triple(150f, CAMINO + 1f, 72f), Triple(96f, CAMINO + 2f, 96f))
        cerezo(m, l, p, Offset(42f, CAMINO + 1f), az)
        for ((x, pie, alto) in sitios) pino(m, l, p, Offset(x, pie), alto, (0.4f - alto / 300f).coerceIn(0.04f, 0.36f), az)
    }

    /**
     * El cerezo del borde del camino, junto al jizō: en flor del 1 al 15 de abril, verde en verano, rojizo en otoño y
     * pelado en invierno.
     */
    private fun DrawScope.cerezo(m: Momento, l: Luces, p: Pincel, pie: Offset, az: Azar) {
        val est = m.ficha.estacion
        val hoja = when {
            EscenaEdo.cerezoEnFlor(m) -> C.CEREZO
            EscenaEdo.mes(m) in 10..11 -> lerp(C.BERMELLON, C.ORO, 0.4f)
            else -> hojaDe(est)?.let { lerp(it, C.PINO, 0.3f) }
        }
        sombraCorta(p.r, pie, 14f, 50f)
        arbol(l, pie, 58f, est, 0.08f, Azar(Semilla.de("edo", "cerezo")), hoja = hoja, tronco = Color(0xFF5A4038))
        if (EscenaEdo.cerezoEnFlor(m)) repeat(40) { val q = pie + Offset(az.entre(-22f, 22f), -az.entre(26f, 52f)); drawCircle(l.tono(lerp(C.CEREZO, Color.White, 0.5f)), az.entre(0.8f, 1.6f), q) }
    }

    /** Un pino negro japonés: el tronco torcido y los pisos de agujas, como nubes tendidas. */
    internal fun DrawScope.pino(m: Momento, l: Luces, p: Pincel, pie: Offset, alto: Float, lejos: Float, az: Azar) {
        val corteza = Color(0xFF5A4636)
        sombraCorta(p.r, pie, alto * 0.25f, alto)
        // El tronco, que se inclina hacia el camino y tuerce.
        val cima = pie + Offset(alto * 0.18f, -alto * 0.92f)
        drawPath(Path().apply { moveTo(pie.x - alto * 0.04f, pie.y); cubicTo(pie.x - alto * 0.02f, pie.y - alto * 0.4f, cima.x - alto * 0.18f, pie.y - alto * 0.55f, cima.x, cima.y); cubicTo(cima.x - alto * 0.12f, pie.y - alto * 0.55f, pie.x + alto * 0.06f, pie.y - alto * 0.4f, pie.x + alto * 0.05f, pie.y); close() },
            Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(corteza, lejos), p.luz(corteza, lejos)) else listOf(p.luz(corteza, lejos), p.sombra(corteza, lejos)), pie.x - alto * 0.06f, pie.x + alto * 0.06f))
        // Los pisos: cada uno, una masa en sombra debajo y la luz arriba, del lado del sol, con el borde de agujas.
        val pisos = listOf(0.92f to 0.24f, 0.72f to 0.34f, 0.52f to 0.3f, 0.34f to 0.22f)
        for ((k, w) in pisos) {
            val c = pie + Offset(alto * (0.18f * k + az.entre(-0.08f, 0.08f)), -alto * k)
            val rx = alto * w; val ry = alto * 0.07f
            val rama = c + Offset(-rx * 0.5f, ry * 0.5f)
            drawLine(l.tono(corteza, lejos), Offset(pie.x + alto * 0.12f * k, c.y + ry), rama, alto * 0.018f, StrokeCap.Round)
            val sombra = l.tono(lerp(C.PINO, C.SUMI, 0.35f), lejos); val cuerpo = l.tono(C.PINO, lejos); val luz = l.tono(lerp(C.PINO, Color(0xFFC8D8A0), 0.35f), lejos)
            val apaga = 1f - 0.4f * l.noche
            fun t(x: Color) = Color(x.red * apaga, x.green * apaga, x.blue * apaga)
            // Un piso no es un disco: son matas de agujas, cada una con su borde roto, que se enciman; abajo la sombra.
            val matas = (3 + (alto / 20f).toInt()).coerceAtMost(7)
            for (j in 0 until matas) {
                val u = (j + 0.5f) / matas; val q = Offset(c.x - rx + 2f * rx * u, c.y + az.entre(-ry * 0.3f, ry * 0.3f)); val w = rx * az.entre(0.32f, 0.5f); val h = ry * az.entre(0.9f, 1.3f)
                drawOval(t(sombra), Offset(q.x - w, q.y - h * 0.2f), Size(w * 2f, h * 1.1f))
                drawOval(t(cuerpo), Offset(q.x - w * 0.94f, q.y - h * 0.7f), Size(w * 1.88f, h * 1.1f))
                drawOval(t(luz), Offset(q.x - w * 0.5f + (if (p.solALaDerecha) w * 0.3f else -w * 0.3f), q.y - h * 0.8f), Size(w, h * 0.55f))
                // Las agujas del borde de arriba y del de abajo.
                var a = 0f; while (a < 6.28f) { val e = q + Offset(cos(a) * w, sin(a) * h * 0.55f - h * 0.15f); drawLine(t(if (sin(a) < 0f) cuerpo else sombra), e, e + Offset(cos(a) * maxOf(0.8f, alto * 0.02f), sin(a) * maxOf(0.6f, alto * 0.016f)), 0.5f); a += 0.45f }
            }
            if (EscenaEdo.nevado(m)) drawOval(l.tono(C.NIEVE, lejos), Offset(c.x - rx * 0.8f, c.y - ry * 1.1f), Size(rx * 1.6f, ry * 0.7f))
        }
    }

    private data class Quad(val a: Float, val b: Float, val c: Float, val d: Int)

}

