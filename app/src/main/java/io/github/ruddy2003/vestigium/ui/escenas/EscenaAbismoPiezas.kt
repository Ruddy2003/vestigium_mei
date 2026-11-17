package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Las piezas de Abismo: lo de a bordo
// =====================================================================
//
// Lo que usan los dos cuadros o pide cuidado: el casco de buzo de cobre, el traje de lona, las botas de plomo, la bomba
// de aire de mano, la manguera y la cuerda de vida, la escala de cuerda, las esponjas (sueltas, en ristras y en montón),
// la tina y el mazo, el bote con su cubo de cristal, el hornillo, la hornacina del icono con su lamparilla, el farol, el
// palo con sus ganchos y el barco esponjero lejano. El agua transparente vista desde arriba es una pieza aparte
// ([AguaClara], en `EscenaAbismoAgua.kt`), y las piezas del muelle están en `EscenaAbismoPiezasTierra.kt`.
//
// Los cuerpos de caras planas (la bomba, la tina) se arman en metros, en sus propios ejes, y se proyectan con la
// perspectiva cónica de Mongol (`V3`, `Mirada`, `Sitio`, `Sol3`, usadas tal cual, como hizo Botánico): así se ven en tres
// cuartos, con su lado de luz y su lado de sombra. Las cosas redondas o blandas (el casco, el traje, las esponjas) se
// dibujan en el lienzo, con su volumen y su luz.

/** Una línea suave y cerrada que pasa por todos los [pts] (curvas de Catmull-Rom). */
internal fun lazo(pts: List<Offset>): Path {
    val p = Path(); val n = pts.size
    p.moveTo(pts[0].x, pts[0].y)
    for (i in 0 until n) {
        val a = pts[(i - 1 + n) % n]; val b = pts[i]; val c = pts[(i + 1) % n]; val d = pts[(i + 2) % n]
        p.cubicTo(b.x + (c.x - a.x) / 6f, b.y + (c.y - a.y) / 6f, c.x - (d.x - b.x) / 6f, c.y - (d.y - b.y) / 6f, c.x, c.y)
    }
    p.close(); return p
}

internal object PiezasAbismo {
    private val C = ColorAbismo

    /** El lado del sol en el lienzo: 1 a la derecha, −1 a la izquierda. */
    private fun lado(p: Pincel): Float = if (p.solALaDerecha) 1f else -1f

    /**
     * De noche, junto a una lumbre: el color con que se enfría la cara que le da la espalda (azul hondo, con la luna
     * detrás) y cuánto (su alfa). Transparente si no hay lumbre cerca. Así cada cosa tiene un lado naranja y otro azul: el
     * `Pincel` deja esa cara en un pardo oscuro.
     */
    fun ladoFrio(p: Pincel): Color = if (p.l.noche > 0.3f && p.calida > 0.08f) lerp(C.SOMBRA, C.MAR_HONDO, 0.4f).copy(alpha = (0.6f * p.l.noche).coerceIn(0f, 0.6f)) else Color.Transparent

    /** Enfría con [ladoFrio] la mitad de [forma] que da la espalda a la lumbre, en degradado desde [c] hasta [r] unidades. */
    private fun DrawScope.enfriar(p: Pincel, forma: Path, c: Offset, r: Float) {
        val frio = ladoFrio(p); if (frio.alpha <= 0.01f) return
        val ld = lado(p)
        clipPath(forma) {
            if (ld > 0) drawRect(Brush.horizontalGradient(listOf(frio, Color.Transparent), c.x - r, c.x + r * 0.1f), Offset(c.x - r * 1.5f, c.y - r * 3f), Size(r * 1.7f, r * 6f))
            else drawRect(Brush.horizontalGradient(listOf(Color.Transparent, frio), c.x - r * 0.1f, c.x + r), Offset(c.x - r * 0.2f, c.y - r * 3f), Size(r * 1.7f, r * 6f))
        }
    }

    // =================================================================
    //  Las esponjas
    // =================================================================

    /**
     * Una esponja de [r] unidades de radio con el centro en [c]. Ninguna es igual a otra: su contorno de lóbulos, su
     * tamaño y su tono salen de [semilla]. Seca, «dorada y blanda», con sus poros; viva ([seca] = false), «negra y
     * viscosa», con su brillo mojado. [alarga] la estira de lado (las «de dedo»).
     */
    fun DrawScope.esponja(p: Pincel, c: Offset, r: Float, semilla: Long, seca: Boolean = true, lejos: Float = 0f, alarga: Float = 1f) {
        if (r < 0.3f) return
        val az = Azar(semilla)
        val n = 7 + az.entero(4)
        val ex = alarga * az.entre(0.86f, 1.18f); val ey = az.entre(0.74f, 0.92f)
        val giro = az.entre(-0.4f, 0.4f)
        val pts = (0 until n).map { i ->
            val a = i * 2f * PI.toFloat() / n + az.entre(-0.22f, 0.22f)
            val rr = r * az.entre(0.76f, 1.12f) * (if (sin(a) > 0.6f) 0.92f else 1f)
            val x = cos(a) * rr * ex; val y = sin(a) * rr * ey
            Offset(c.x + x * cos(giro) - y * sin(giro), c.y + x * sin(giro) + y * cos(giro))
        }
        val forma = if (r < 1.4f) Path().apply { addOval(Rect(c, r)) } else lazo(pts)
        // Seca, cada una de su clase («las de lana, las amarillas, las de hierba, las de dedo»), y alguna todavía mojada.
        val clase = az.real()
        val propio = if (!seca) lerp(C.ESPONJA_VIVA, C.HIERBA, az.entre(0f, 0.15f)) else when {
            clase < 0.42f -> lerp(lerp(C.ESPONJA, C.ESPONJA_OSC, az.entre(0.1f, 0.45f)), C.MADERA, az.entre(0f, 0.15f))       // de lana: tostada
            clase < 0.64f -> lerp(C.ESPONJA, lerp(C.ARENA, C.BLANCO, 0.4f), az.entre(0.35f, 0.6f))                           // amarilla: pálida
            clase < 0.82f -> lerp(lerp(C.ESPONJA, C.HIERBA, az.entre(0.18f, 0.32f)), C.LONA, 0.2f)                            // de hierba: verdosa
            clase < 0.92f -> lerp(C.ESPONJA, C.ARENA, az.entre(0.1f, 0.3f))                                                  // de dedo
            else -> lerp(C.ESPONJA_OSC, C.BREA, az.entre(0.2f, 0.35f))                                                       // mojada todavía
        }
        val ld = lado(p)
        val foco = c + Offset(ld * r * 0.38f, -r * 0.42f)
        drawPath(forma, Brush.radialGradient(0f to p.luz(propio, lejos), 0.5f to p.medio(propio, lejos), 1f to p.sombra(propio, lejos), center = foco, radius = r * 1.55f))
        if (r < 2f) return
        clipPath(forma) {
            // La sombra propia de abajo, del lado contrario al sol.
            drawOval(p.sombra(lerp(propio, C.BREA, 0.3f), lejos).copy(alpha = 0.45f), Offset(c.x - r * 1.1f - ld * r * 0.3f, c.y + r * 0.25f), Size(r * 2.2f, r * 1.1f))
            // Los poros: hoyos oscuros con su labio de luz abajo, más chicos hacia el borde.
            val poros = (2 + r * 0.8f).toInt().coerceAtMost(14)
            val hoyo = p.sombra(lerp(propio, C.BREA, if (seca) 0.55f else 0.2f), lejos)
            val labio = p.luz(lerp(propio, C.ARENA, 0.3f), lejos)
            repeat(poros) {
                val a = az.entre(0f, 6.28f); val d = sqrt(az.real()) * r * 0.75f
                val q = c + Offset(cos(a) * d * ex, sin(a) * d * ey)
                val pr = r * az.entre(0.06f, 0.15f) * (1f - 0.5f * d / r)
                drawOval(hoyo.copy(alpha = 0.85f), Offset(q.x - pr, q.y - pr * 0.75f), Size(pr * 2f, pr * 1.5f))
                if (pr > 0.5f) drawArc(labio.copy(alpha = 0.55f), 20f, 140f, false, Offset(q.x - pr, q.y - pr * 0.6f), Size(pr * 2f, pr * 1.5f), style = Stroke(pr * 0.35f))
            }
            if (!seca) {
                // El brillo mojado.
                val b = c + Offset(ld * r * 0.3f, -r * 0.45f)
                drawArc(Color(0xFFEFF6F2).copy(alpha = 0.45f * p.r.contraste), 200f, 80f, false, Offset(b.x - r * 0.45f, b.y - r * 0.2f), Size(r * 0.9f, r * 0.5f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
            } else {
                // La textura de lana: punteado claro del lado de la luz.
                repeat((r * 1.2f).toInt().coerceAtMost(16)) {
                    val a = az.entre(-1.6f, 1.6f) + (if (ld > 0) 0f else PI.toFloat()); val d = az.entre(0.2f, 0.85f) * r
                    drawCircle(labio.copy(alpha = 0.35f), r * 0.05f + 0.2f, c + Offset(cos(a) * d * ex * 0.9f, sin(a) * d * ey - r * 0.15f))
                }
            }
        }
    }

    /**
     * Una ristra de esponjas: la cuerda que cuelga de [arriba] a lo largo de [largo] unidades, con [cuantas] esponjas
     * ensartadas («Cuarenta en cada cuerda»: a este tamaño se ven las de delante). [r] es el radio de una esponja media.
     * Corta o larga según [largo]; en [racimo], las esponjas se juntan en la mitad de abajo, como un racimo. [sesgo]
     * tuerce la cuerda (el viento, o colgada de lado). Cada esponja, distinta.
     */
    fun DrawScope.ristra(p: Pincel, arriba: Offset, largo: Float, r: Float, cuantas: Int, semilla: Long, lejos: Float = 0f, racimo: Boolean = false, sesgo: Float = 0f) {
        val az = Azar(semilla)
        val abajo = arriba + Offset(sesgo * largo, largo)
        val medio = arriba + (abajo - arriba) * 0.5f + Offset(-sesgo * largo * 0.12f, 0f)
        val cuerda = Path().apply { moveTo(arriba); cubicTo(medio, medio, abajo) }
        drawPath(cuerda, p.sombra(C.CANAMO, lejos), style = Stroke((r * 0.14f).coerceAtLeast(0.35f), cap = StrokeCap.Round))
        drawPath(cuerda, p.luz(C.CANAMO, lejos).copy(alpha = 0.5f), style = Stroke((r * 0.05f).coerceAtLeast(0.2f), cap = StrokeCap.Round))
        for (i in 0 until cuantas) {
            val t0 = if (racimo) 0.42f + 0.58f * (i + 0.5f) / cuantas else 0.1f + 0.9f * (i + 0.5f) / cuantas
            val t = (t0 + az.entre(-0.03f, 0.03f)).coerceIn(0.05f, 1f)
            val q = Offset(arriba.x + (abajo.x - arriba.x) * t, arriba.y + (abajo.y - arriba.y) * t) + Offset(az.entre(-0.4f, 0.4f) * r * (if (racimo) 1.6f else 0.8f), 0f)
            esponja(p, q, r * az.entre(0.62f, 1.18f) * (if (racimo) 1f + 0.25f * t else 1f), semilla * 31 + i, true, lejos, az.entre(0.85f, 1.35f))
        }
    }

    /**
     * Un montón de esponjas apoyado en el suelo en [pie], de [ancho] unidades: unas encima de otras, las de abajo más
     * grandes, con la sombra de contacto. [seca] = false: las negras recién sacadas, que chorrean.
     */
    fun DrawScope.monton(p: Pincel, pie: Offset, ancho: Float, cuantas: Int, semilla: Long, seca: Boolean, lejos: Float = 0f) {
        if (cuantas <= 0) return
        val az = Azar(semilla)
        val r = ancho / 5.2f
        drawOval(p.sombra(C.BREA, lejos).copy(alpha = 0.35f), Offset(pie.x - ancho * 0.55f, pie.y - r * 0.35f), Size(ancho * 1.1f, r * 0.8f))
        if (!seca) drawOval(p.sombra(C.MAR_HONDO, lejos).copy(alpha = 0.3f), Offset(pie.x - ancho * 0.62f, pie.y - r * 0.2f), Size(ancho * 1.24f, r * 0.7f))   // el charco
        val filas = ArrayList<Pair<Offset, Float>>()
        var quedan = cuantas; var fila = 0
        while (quedan > 0 && fila < 4) {
            val enFila = minOf(quedan, maxOf(1, 4 - fila))
            for (i in 0 until enFila) {
                val w = ancho * (1f - 0.24f * fila)
                val x = pie.x - w / 2f + w * (i + 0.5f) / enFila + az.entre(-0.1f, 0.1f) * r
                filas += Offset(x, pie.y - r * 0.55f - fila * r * 1.15f) to r * az.entre(0.85f, 1.15f) * (1f - 0.1f * fila)
            }
            quedan -= enFila; fila++
        }
        filas.forEachIndexed { i, (q, rr) -> esponja(p, q, rr, semilla * 17 + i, seca, lejos) }
    }

    // =================================================================
    //  El casco de buzo
    // =================================================================

    /**
     * El casco de buzo de cobre y bronce, «redondo como una calabaza, con tres ventanillas de cristal grueso y una válvula
     * para que salga el aire viejo». [c] es el centro de la cúpula y [r] su radio; [mira], de −1 a 1, hacia dónde mira
     * la ventanilla de delante (0 de frente, 1 a la derecha). Con el peto atornillado debajo, el cuello de latón, las
     * tres ventanillas con su aro y sus pernos (la de delante, las dos de los lados: se ven las que dan a quien mira), el
     * cuello de ganso del aire detrás y la válvula a un lado. El cobre es lo que la luz enciende: brilla en el lado del sol
     * y en la sombra recoge el verde del agua ([rebote]). Golpes, cardenillo y el latón gastado: es un casco con años.
     */
    fun DrawScope.casco(p: Pincel, c: Offset, r: Float, mira: Float, semilla: Long, lejos: Float = 0f, rebote: Color = C.MAR) {
        val az = Azar(semilla)
        val ld = lado(p)
        // Cada casco con sus años: uno bruñido, casi rosa; otro mate y pardo; otro con el cardenillo comiéndolo.
        val patina = az.real()
        val cobre = lerp(lerp(C.COBRE, C.LATON, az.entre(0f, 0.15f)), if (patina < 0.33f) lerp(C.COBRE, Color(0xFFE0906A), 0.5f) else if (patina < 0.66f) C.COBRE_OSC else lerp(C.COBRE, C.VERDIN, 0.25f), if (patina < 0.33f) 0.35f else 0.3f)
        val luz = p.luz(lerp(cobre, C.LATON, 0.3f), lejos); val medio = p.medio(cobre, lejos); val sombra = p.sombra(C.COBRE_OSC, lejos)
        val brillo = lerp(luz, Color(0xFFFFF0D2), 0.55f * p.r.contraste)
        val reb = lerp(sombra, p.medio(rebote, lejos), 0.42f)
        val laton = p.medio(C.LATON, lejos); val latonLuz = p.luz(C.LATON, lejos); val latonSombra = p.sombra(lerp(C.LATON, C.COBRE_OSC, 0.4f), lejos)

        // ---- El peto (el corselete), atornillado al cuello.
        val peto = Path().apply {
            moveTo(c.x - r * 0.66f, c.y + r * 0.66f)
            cubicTo(c.x - r * 0.95f, c.y + r * 0.78f, c.x - r * 1.28f, c.y + r * 0.98f, c.x - r * 1.22f, c.y + r * 1.42f)
            cubicTo(c.x - r * 0.6f, c.y + r * 1.66f, c.x + r * 0.6f, c.y + r * 1.66f, c.x + r * 1.22f, c.y + r * 1.42f)
            cubicTo(c.x + r * 1.28f, c.y + r * 0.98f, c.x + r * 0.95f, c.y + r * 0.78f, c.x + r * 0.66f, c.y + r * 0.66f); close()
        }
        drawPath(peto, Brush.horizontalGradient(0f to (if (ld > 0) reb else luz), 0.25f to (if (ld > 0) sombra else brillo), 0.55f to medio, 0.8f to (if (ld > 0) brillo else sombra), 1f to (if (ld > 0) luz else reb), startX = c.x - r * 1.3f, endX = c.x + r * 1.3f))
        clipPath(peto) {
            // La sombra que la cúpula echa sobre el peto, y el canto de latón de abajo con sus pernos.
            drawOval(sombra.copy(alpha = 0.55f), Offset(c.x - r * 0.95f, c.y + r * 0.55f), Size(r * 1.9f, r * 0.45f))
            drawPath(Path().apply { moveTo(c.x - r * 1.3f, c.y + r * 1.36f); cubicTo(c.x - r * 0.6f, c.y + r * 1.58f, c.x + r * 0.6f, c.y + r * 1.58f, c.x + r * 1.3f, c.y + r * 1.36f) }, laton, style = Stroke(r * 0.16f))
        }
        for (i in 0..6) {
            val u = -0.95f + 1.9f * i / 6f
            val q = Offset(c.x + u * r * 1.1f, c.y + r * (1.43f + 0.12f * (1f - u * u)))
            drawCircle(if (u * ld > 0) latonLuz else latonSombra, r * 0.06f, q)
        }
        // Las palomillas de delante (las tuercas de orejas que cierran el traje).
        for (u in listOf(-0.62f, 0.62f)) {
            val q = Offset(c.x + u * r, c.y + r * 1.32f)
            drawOval(if (u * ld > 0) latonLuz else laton, Offset(q.x - r * 0.15f, q.y - r * 0.05f), Size(r * 0.3f, r * 0.1f))
            drawCircle(latonSombra, r * 0.05f, q)
        }
        // ---- El cuello de latón.
        drawOval(Brush.horizontalGradient(listOf(if (ld > 0) latonSombra else latonLuz, laton, if (ld > 0) latonLuz else latonSombra), c.x - r * 0.72f, c.x + r * 0.72f), Offset(c.x - r * 0.72f, c.y + r * 0.58f), Size(r * 1.44f, r * 0.3f))

        // ---- El cuello de ganso del aire, detrás, del lado contrario a la ventanilla.
        val atras = if (mira >= 0f) -1f else 1f
        val g0 = Offset(c.x + atras * r * 0.72f, c.y - r * 0.5f)
        val ganso = Path().apply { moveTo(g0); cubicTo(g0 + Offset(atras * r * 0.35f, -r * 0.22f), g0 + Offset(atras * r * 0.55f, r * 0.05f), g0 + Offset(atras * r * 0.5f, r * 0.32f)) }
        drawPath(ganso, latonSombra, style = Stroke(r * 0.2f, cap = StrokeCap.Round))
        drawPath(ganso, latonLuz.copy(alpha = 0.7f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
        drawCircle(laton, r * 0.13f, g0 + Offset(atras * r * 0.5f, r * 0.34f))

        // ---- La cúpula.
        val cupula = Path().apply { addOval(Rect(c.x - r, c.y - r * 1.03f, c.x + r, c.y + r * 0.92f)) }
        drawPath(cupula, Brush.radialGradient(0f to brillo, 0.13f to luz, 0.46f to medio, 0.86f to sombra, 1f to reb, center = c + Offset(ld * r * 0.4f, -r * 0.45f), radius = r * 1.95f))
        clipPath(cupula) {
            // El cardenillo en las juntas de abajo y detrás, y los golpes del cobre (arcos de luz y de sombra).
            repeat(if (patina > 0.66f) 11 else 5) { val a = az.entre(0.3f, 2.8f); val q = c + Offset(cos(a) * r * 0.85f * atras, sin(a) * r * 0.75f); drawOval(p.medio(C.VERDIN, lejos).copy(alpha = if (patina > 0.66f) 0.45f else 0.28f), Offset(q.x - r * 0.16f, q.y - r * 0.07f), Size(r * 0.32f, r * 0.14f)) }
            // La abolladura: un golpe hondo con su sombra arriba y su luz abajo.
            if (patina in 0.33f..0.66f) { val q = c + Offset(-ld * r * 0.35f, r * 0.15f); drawArc(sombra.copy(alpha = 0.7f), 200f, 140f, false, Offset(q.x - r * 0.3f, q.y - r * 0.16f), Size(r * 0.6f, r * 0.36f), style = Stroke(r * 0.07f)); drawArc(brillo.copy(alpha = 0.5f), 20f, 140f, false, Offset(q.x - r * 0.28f, q.y - r * 0.12f), Size(r * 0.56f, r * 0.32f), style = Stroke(r * 0.05f)) }
            repeat(6) {
                val q = c + Offset(az.entre(-0.7f, 0.7f) * r, az.entre(-0.7f, 0.5f) * r); val w = r * az.entre(0.12f, 0.24f)
                drawArc((if (az.real() > 0.5f) luz else sombra).copy(alpha = 0.4f), 200f, 120f, false, Offset(q.x - w, q.y - w * 0.4f), Size(w * 2f, w * 0.8f), style = Stroke(r * 0.03f))
            }
            // La costura que une las dos mitades de la cúpula, de lado a lado.
            drawArc(sombra.copy(alpha = 0.5f), 180f, 180f, false, Offset(c.x - r * 0.12f + mira * r * 0.4f, c.y - r * 1.03f), Size(r * 0.24f + abs(mira) * r * 0.6f, r * 2f), style = Stroke(r * 0.025f))
        }
        enfriar(p, cupula, c, r * 1.05f); enfriar(p, peto, c + Offset(0f, r * 1.1f), r * 1.3f)
        // El reflejo del cielo y del agua sobre el cobre: una franja clara curva del lado del sol.
        drawArc(lerp(brillo, Color.White, 0.2f).copy(alpha = 0.45f * p.r.contraste), if (ld > 0) 290f else 190f, 60f, false, Offset(c.x - r * 0.78f, c.y - r * 0.82f), Size(r * 1.56f, r * 1.5f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))

        // ---- Las ventanillas: la de delante y las de los lados, cada una con su escorzo.
        val fi = mira.coerceIn(-1f, 1f) * 0.95f
        val ventanas = listOf(fi to 0.43f, fi - PI.toFloat() / 2f to 0.3f, fi + PI.toFloat() / 2f to 0.3f).filter { cos(it.first) > 0.12f }.sortedBy { cos(it.first) }
        for ((b, rw) in ventanas) {
            val fx = cos(b); val q = Offset(c.x + sin(b) * r * 0.84f, c.y + r * (if (rw > 0.4f) 0.08f else 0.0f))
            val rx = rw * r * fx; val ry = rw * r
            // El aro de latón, grueso, con su lado de luz y sus pernos.
            drawOval(Brush.horizontalGradient(listOf(if (ld > 0) latonSombra else latonLuz, laton, if (ld > 0) latonLuz else latonSombra), q.x - rx * 1.32f, q.x + rx * 1.32f), Offset(q.x - rx * 1.32f, q.y - ry * 1.32f), Size(rx * 2.64f, ry * 2.64f))
            drawOval(latonSombra.copy(alpha = 0.7f), Offset(q.x - rx * 1.32f, q.y - ry * 1.32f), Size(rx * 2.64f, ry * 2.64f), style = Stroke(r * 0.025f))
            // El cristal grueso: oscuro, verdoso, con el reflejo del cielo.
            val vidrio = lerp(p.sombra(C.MAR_HONDO, lejos), p.sombra(C.BREA, lejos), 0.4f)
            drawOval(Brush.verticalGradient(listOf(lerp(vidrio, p.medio(C.MAR, lejos), 0.35f), vidrio), q.y - ry, q.y + ry), Offset(q.x - rx, q.y - ry), Size(rx * 2f, ry * 2f))
            drawArc(lerp(p.luz(C.BLANCO, lejos), Color.White, 0.2f).copy(alpha = 0.6f), 200f, 70f, false, Offset(q.x - rx * 0.7f, q.y - ry * 0.75f), Size(rx * 1.4f, ry * 1.3f), style = Stroke(ry * 0.14f, cap = StrokeCap.Round))
            // La reja de las de los lados.
            if (rw < 0.4f) for (k in listOf(-0.4f, 0f, 0.4f)) drawLine(latonSombra, Offset(q.x + k * rx, q.y - ry * 0.95f), Offset(q.x + k * rx, q.y + ry * 0.95f), r * 0.035f)
            // Los pernos del aro.
            if (r > 6f) for (k in 0 until 6) { val a = k * PI.toFloat() / 3f + 0.3f; drawCircle(if (cos(a) * ld > 0) latonLuz else latonSombra, r * 0.04f, Offset(q.x + cos(a) * rx * 1.16f, q.y + sin(a) * ry * 1.16f)) }
        }
        // ---- La válvula del aire viejo, a un lado.
        val v = Offset(c.x + sin(fi + 1.1f) * r * 0.92f, c.y + r * 0.25f)
        if (cos(fi + 1.1f) > -0.2f) { drawCircle(laton, r * 0.11f, v); drawCircle(latonLuz, r * 0.05f, v + Offset(-r * 0.02f, -r * 0.03f)) }
    }

    /** La sombra que un casco colgado echa en lo que tiene detrás (va dentro de una `capaSombra`). */
    fun cascoSilueta(c: Offset, r: Float): Path = Path().apply {
        addOval(Rect(c.x - r, c.y - r * 1.03f, c.x + r, c.y + r * 0.92f))
        addOval(Rect(c.x - r * 1.24f, c.y + r * 0.6f, c.x + r * 1.24f, c.y + r * 1.62f))
    }

    // =================================================================
    //  El traje de lona
    // =================================================================

    /**
     * Un traje de lona tendido a secar doblado por la cintura sobre un cabo (que pasa por [a] y [b], en el lienzo), de [k]
     * unidades por metro, como se tiende un mono mojado, y caído de través, sin la forma de nadie: por el lado de acá
     * cuelgan las dos perneras con sus pies de lona; por el de allá, corrido a lo largo del cabo, el cuerpo del revés, que
     * acaba abajo en el cuello de goma con su babero ancho y los pernos del peto, bien a la vista, y una manga caída con su
     * puño de goma negra (la otra queda detrás de las perneras). Lo de abajo, todavía mojado.
     */
    fun DrawScope.trajeDoblado(p: Pincel, a: Offset, b: Offset, k: Float, semilla: Long, mojado: Float = 0.5f, lejos: Float = 0f) {
        val az = Azar(semilla)
        val m = (a + b) * 0.5f
        var u = (b - a).let { it / it.getDistance().coerceAtLeast(0.01f) }; if (u.x < 0f) u = -u          // a lo largo del cabo, hacia la derecha
        fun enCabo(d: Float) = m + u * (d * k)
        val lona = lerp(C.LONA, C.MADERA, az.entre(0.05f, 0.18f))
        val ld = lado(p)
        val luzL = p.luz(lona, lejos); val medioL = p.medio(lona, lejos); val sombraL = p.sombra(lona, lejos)
        val mojadaL = p.sombra(lerp(lona, C.LONA_MOJADA, 0.85f), lejos)
        fun tela(x0: Float, x1: Float) = Brush.horizontalGradient(listOf(if (ld > 0) sombraL else luzL, medioL, if (ld > 0) luzL else sombraL), x0, x1)
        val mojar = Brush.verticalGradient(listOf(Color.Transparent, mojadaL.copy(alpha = 0.8f * mojado)), m.y + 0.3f * k, m.y + 1.0f * k)
        // ---- Detrás: el cuerpo del revés, corrido a la derecha por el cabo, con la manga de ese lado y el cuello abajo.
        val c0 = enCabo(0.04f); val c1 = enCabo(0.42f); val largo = 0.62f * k
        val cuerpo = Path().apply {
            moveTo(c0.x, c0.y); cubicTo(c0.x - 0.03f * k, c0.y + largo * 0.4f, c0.x + 0.0f * k, c0.y + largo * 0.8f, c0.x + 0.03f * k, c0.y + largo)
            lineTo(c1.x + 0.04f * k, c1.y + largo * 0.97f); cubicTo(c1.x + 0.06f * k, c1.y + largo * 0.7f, c1.x + 0.04f * k, c1.y + largo * 0.3f, c1.x, c1.y); close()
        }
        // La manga de la derecha: cae del hombro, abajo, y se dobla en el codo hacia dentro.
        val hombro = Offset(c1.x + 0.02f * k, c1.y + largo * 0.9f); val g = 0.085f * k
        val codo = hombro + Offset(0.05f * k, 0.22f * k); val puno = codo + Offset(-0.05f * k, 0.2f * k)
        drawLine(sombraL, hombro, codo, g, StrokeCap.Round); drawLine(sombraL, codo, puno, g * 0.95f, StrokeCap.Round)
        drawLine(medioL, hombro + Offset(-g * 0.15f, 0f), codo + Offset(-g * 0.15f, 0f), g * 0.6f, StrokeCap.Round); drawLine(medioL, codo + Offset(-g * 0.12f, 0f), puno + Offset(-g * 0.1f, 0f), g * 0.55f, StrokeCap.Round)
        drawLine(lerp(sombraL, mojadaL, mojado), codo, puno, g * 0.4f, StrokeCap.Round)
        drawLine(p.medio(C.BREA, lejos), puno + Offset(-g * 0.45f, -g * 0.1f), puno + Offset(g * 0.45f, g * 0.05f), g * 0.55f, StrokeCap.Round)    // el puño de goma
        drawPath(cuerpo, tela(c0.x, c1.x + 0.05f * k)); drawPath(cuerpo, mojar)
        clipPath(cuerpo) {
            repeat(6) { val x = c0.x + (c1.x - c0.x) * az.entre(0.1f, 0.9f); val y0 = c0.y + largo * az.entre(0.05f, 0.3f); drawLine(sombraL.copy(alpha = 0.5f), Offset(x, y0), Offset(x + az.entre(-0.02f, 0.02f) * k, y0 + largo * az.entre(0.3f, 0.6f)), 0.012f * k, StrokeCap.Round) }
            drawRect(p.medio(lerp(C.BREA, C.LONA, 0.3f), lejos), Offset(c0.x + (c1.x - c0.x) * 0.55f, c0.y + largo * 0.35f), Size(0.07f * k, 0.06f * k))
        }
        // El cuello de goma y su babero, abajo, colgando hacia el agua, con los pernos del peto: lo que dice que es de buzo.
        val cu = Offset((c0.x + c1.x) / 2f + 0.04f * k, maxOf(c0.y, c1.y) + largo + 0.04f * k); val bw = 0.21f * k; val bh = 0.11f * k
        drawOval(p.medio(lerp(C.BREA, C.PLOMO, 0.25f), lejos), Offset(cu.x - bw, cu.y - bh * 0.6f), Size(bw * 2f, bh * 1.35f))
        drawArc(p.luz(lerp(C.BREA, C.PLOMO, 0.55f), lejos), 190f, 150f, false, Offset(cu.x - bw, cu.y - bh * 0.6f), Size(bw * 2f, bh * 1.35f), style = Stroke(0.014f * k))
        for (i in 0 until 9) { val an = PI.toFloat() * (0.06f + 0.88f * i / 8f); drawCircle(p.luz(C.LATON, lejos), 0.012f * k, Offset(cu.x + cos(an) * bw * 0.8f, cu.y + bh * 0.05f + sin(an) * bh * 0.42f)) }
        drawOval(p.sombra(C.BREA, lejos), Offset(cu.x - bw * 0.4f, cu.y - bh * 0.3f), Size(bw * 0.8f, bh * 0.5f))
        if (mojado > 0.3f) repeat(3) { drawCircle(p.luz(C.MAR_CLARO, lejos).copy(alpha = 0.6f), 0.01f * k, cu + Offset(az.entre(-0.15f, 0.15f) * k, az.entre(0.12f, 0.24f) * k)) }
        // ---- El doblez sobre el cabo: la cintura, abultada, con su costura.
        val f0 = enCabo(-0.2f); val f1 = enCabo(0.44f)
        val doblez = lazo(listOf(f0 + Offset(0f, -0.02f * k), (f0 + f1) * 0.5f + Offset(0f, -0.07f * k), f1 + Offset(0f, -0.03f * k), f1 + Offset(0f, 0.05f * k), (f0 + f1) * 0.5f + Offset(0f, 0.06f * k), f0 + Offset(0f, 0.05f * k)))
        drawPath(doblez, Brush.verticalGradient(listOf(luzL, medioL), m.y - 0.07f * k, m.y + 0.06f * k))
        drawLine(sombraL.copy(alpha = 0.7f), f0 + Offset(0f, 0.03f * k), f1 + Offset(0f, 0.035f * k), 0.012f * k)
        // ---- Delante: las dos perneras, a la izquierda del doblez, una más corta (se dobla en la rodilla), con sus pies de lona.
        for ((i, d) in listOf(-0.15f, -0.02f).withIndex()) {
            val r = enCabo(d); val gp = 0.12f * k; val lg = (if (i == 0) 0.8f else 0.66f) * k
            val pr = Path().apply { moveTo(r.x - gp * 0.5f, r.y); cubicTo(r.x - gp * 0.55f, r.y + lg * 0.5f, r.x - gp * 0.45f + i * 0.03f * k, r.y + lg * 0.85f, r.x - gp * 0.42f + i * 0.04f * k, r.y + lg); lineTo(r.x + gp * 0.45f + i * 0.04f * k, r.y + lg); cubicTo(r.x + gp * 0.5f + i * 0.03f * k, r.y + lg * 0.8f, r.x + gp * 0.55f, r.y + lg * 0.4f, r.x + gp * 0.5f, r.y); close() }
            drawPath(pr, tela(r.x - gp * 0.6f, r.x + gp * 0.6f)); drawPath(pr, mojar)
            clipPath(pr) { repeat(3) { val y = r.y + lg * az.entre(0.15f, 0.85f); drawArc(sombraL.copy(alpha = 0.5f), 10f, 160f, false, Offset(r.x - gp * 0.5f, y), Size(gp, gp * 0.35f), style = Stroke(0.012f * k)) } }
            val pie = Offset(r.x + (if (i == 0) -0.03f else 0.06f) * k, r.y + lg + 0.03f * k)
            drawOval(lerp(medioL, mojadaL, 0.7f * mojado), Offset(pie.x - gp * 0.6f, pie.y - gp * 0.4f), Size(gp * 1.2f, gp * 0.8f))
            drawArc(sombraL, 20f, 140f, false, Offset(pie.x - gp * 0.6f, pie.y - gp * 0.4f), Size(gp * 1.2f, gp * 0.8f), style = Stroke(0.012f * k))
        }
    }

    // =================================================================
    //  Las botas de plomo
    // =================================================================

    /**
     * Una bota de buzo apoyada en la cubierta en [pie], de [k] unidades por metro, con la punta hacia [mira] (±1): «suelas
     * de plomo que pesan como dos piedras de molino pequeñas». La suela de plomo, gruesa, con su cara de arriba y su canto,
     * la puntera de latón, la caña de lona y cuero con sus correas y hebillas, y la boca oscura. [tumbada]: caída de lado,
     * enseñando la suela con sus clavos.
     */
    fun DrawScope.bota(p: Pincel, pie: Offset, k: Float, mira: Float, semilla: Long, tumbada: Boolean = false, lejos: Float = 0f) {
        val az = Azar(semilla); val m = mira; val u = k
        fun q(x: Float, y: Float) = Offset(pie.x + m * x * u, pie.y - y * u)
        val cuero = lerp(C.MADERA, C.LONA, az.entre(0.15f, 0.35f))
        val ld = lado(p); val deCara = ld * m > 0
        val plomoLuz = p.medio(lerp(C.PLOMO, C.BLANCO, 0.12f), lejos); val plomo = p.medio(C.PLOMO, lejos); val plomoSombra = p.sombra(C.PLOMO, lejos)
        if (!tumbada) {
            drawOval(p.sombra(C.BREA, lejos).copy(alpha = 0.35f), Offset(pie.x - 0.24f * u, pie.y - 0.035f * u), Size(0.48f * u, 0.08f * u))
            // La suela de plomo: un bloque grueso, más largo que el pie, con su cara de arriba clara y el canto en sombra.
            val canto = poligono(listOf(q(-0.16f, 0f), q(0.2f, 0f), q(0.235f, 0.03f), q(0.22f, 0.075f), q(-0.16f, 0.075f)))
            drawPath(canto, Brush.verticalGradient(listOf(plomo, plomoSombra), pie.y - 0.075f * u, pie.y))
            drawPath(poligono(listOf(q(-0.16f, 0.075f), q(0.22f, 0.075f), q(0.2f, 0.095f), q(-0.15f, 0.095f))), plomoLuz)
            // Los golpes del plomo, blando.
            repeat(3) { val x = az.entre(-0.12f, 0.15f); drawCircle(plomoSombra.copy(alpha = 0.6f), 0.01f * u, q(x, az.entre(0.02f, 0.05f))) }
            // La puntera de latón, redonda, que asoma delante.
            drawPath(poligono(listOf(q(0.12f, 0.095f), q(0.2f, 0.095f), q(0.25f, 0.06f), q(0.235f, 0.02f), q(0.14f, 0.075f))), Brush.horizontalGradient(listOf(p.medio(C.LATON, lejos), p.luz(C.LATON, lejos)), q(0.12f, 0f).x, q(0.25f, 0f).x))
            // El empeine y la caña, de lona y cuero: una L clara, con el talón atrás.
            val cana = Path().apply {
                moveTo(q(-0.13f, 0.095f)); lineTo(q(0.15f, 0.095f)); cubicTo(q(0.17f, 0.15f), q(0.1f, 0.19f), q(0.04f, 0.2f))
                cubicTo(q(0.045f, 0.26f), q(0.05f, 0.3f), q(0.06f, 0.34f)); lineTo(q(-0.08f, 0.34f)); cubicTo(q(-0.085f, 0.28f), q(-0.12f, 0.2f), q(-0.13f, 0.095f)); close()
            }
            drawPath(cana, Brush.horizontalGradient(listOf(if (deCara) p.sombra(cuero, lejos) else p.luz(cuero, lejos), p.medio(cuero, lejos), if (deCara) p.luz(cuero, lejos) else p.sombra(cuero, lejos)), minOf(q(-0.13f, 0f).x, q(0.17f, 0f).x), maxOf(q(-0.13f, 0f).x, q(0.17f, 0f).x)))
            clipPath(cana) {
                repeat(4) { val y = az.entre(0.21f, 0.33f); drawLine(p.sombra(cuero, lejos).copy(alpha = 0.45f), q(-0.1f, y), q(0.06f, y + az.entre(-0.02f, 0.02f)), 0.008f * u) }
                drawRect(p.sombra(lerp(cuero, C.BREA, 0.3f), lejos).copy(alpha = 0.45f), Offset(minOf(q(-0.14f, 0f).x, q(0.18f, 0f).x), q(0f, 0.15f).y), Size(0.32f * u, 0.04f * u))
            }
            // La boca de la caña y las correas con su hebilla de latón: una sobre el empeine y otra en el tobillo.
            drawOval(p.sombra(C.BREA, lejos), Offset(minOf(q(-0.08f, 0f).x, q(0.06f, 0f).x), q(0f, 0.355f).y), Size(0.14f * u, 0.035f * u))
            drawLine(p.sombra(C.BREA, lejos), q(0.02f, 0.12f), q(0.14f, 0.19f), 0.022f * u, StrokeCap.Round)
            drawLine(p.sombra(C.BREA, lejos), q(-0.12f, 0.25f), q(0.05f, 0.26f), 0.024f * u)
            for (h in listOf(0.165f to 0.1f, 0.255f to 0.0f)) drawRect(p.luz(C.LATON, lejos), Offset(q(h.second, h.first).x - 0.016f * u, q(h.second, h.first).y - 0.015f * u), Size(0.032f * u, 0.03f * u))
        } else {
            // Caída de lado: la suela de plomo hacia quien mira, con sus clavos y su puntera, y la caña tendida detrás.
            drawOval(p.sombra(C.BREA, lejos).copy(alpha = 0.35f), Offset(pie.x - 0.3f * u, pie.y - 0.04f * u), Size(0.62f * u, 0.1f * u))
            val cana = lazo(listOf(q(-0.05f, 0.03f), q(0.18f, 0.02f), q(0.4f, 0.04f), q(0.42f, 0.13f), q(0.2f, 0.15f), q(-0.02f, 0.16f)))
            drawPath(cana, Brush.verticalGradient(listOf(p.luz(cuero, lejos), p.sombra(cuero, lejos)), q(0f, 0.16f).y, q(0f, 0.02f).y))
            drawOval(p.sombra(C.BREA, lejos), Offset(minOf(q(0.39f, 0f).x, q(0.44f, 0f).x) - 0.01f * u, q(0f, 0.15f).y), Size(0.05f * u, 0.11f * u))
            drawLine(p.sombra(C.BREA, lejos), q(0.2f, 0.02f), q(0.2f, 0.15f), 0.022f * u)
            val suela = poligono(listOf(q(-0.26f, 0.0f), q(0.08f, 0.0f), q(0.1f, 0.08f), q(0.08f, 0.17f), q(-0.26f, 0.16f), q(-0.28f, 0.08f)))
            drawPath(suela, Brush.horizontalGradient(listOf(plomoSombra, plomo, plomoLuz), minOf(q(-0.28f, 0f).x, q(0.1f, 0f).x), maxOf(q(-0.28f, 0f).x, q(0.1f, 0f).x)))
            drawLine(plomoLuz, q(-0.25f, 0.165f), q(0.07f, 0.17f), 0.012f * u)
            for (i in 0 until 6) drawCircle(p.sombra(C.BREA, lejos), 0.009f * u, q(-0.22f + 0.055f * i, 0.07f + 0.025f * (i % 2)))
            drawOval(Brush.horizontalGradient(listOf(p.medio(C.LATON, lejos), p.luz(C.LATON, lejos)), minOf(q(-0.32f, 0f).x, q(-0.24f, 0f).x), maxOf(q(-0.32f, 0f).x, q(-0.24f, 0f).x)), Offset(minOf(q(-0.32f, 0f).x, q(-0.24f, 0f).x), q(0f, 0.15f).y), Size(0.08f * u, 0.14f * u))
        }
    }

    // =================================================================
    //  La bomba de aire
    // =================================================================

    /** Las medidas de la bomba, en metros: la caja, las ruedas a los lados y su eje. */
    const val BOMBA_ANCHO = 0.44f
    const val BOMBA_ALTO = 0.78f
    const val BOMBA_FONDO = 0.3f
    const val RUEDA_R = 0.47f
    const val RUEDA_ALTO = 0.6f
    const val RUEDA_X = 0.53f

    /** Un punto del aro de la rueda del lado [lado] (−1 o 1) en el ángulo [a]. */
    private fun rueda(lado: Float, a: Float, r: Float = RUEDA_R) = V3(lado * RUEDA_X, RUEDA_ALTO + r * sin(a), r * cos(a))

    /** Las esquinas de la bomba (para su sombra en la cubierta). */
    fun bombaEsquinas(): List<V3> = esquinas(-RUEDA_X - 0.03f, RUEDA_X + 0.03f, 0f, RUEDA_ALTO + RUEDA_R, -RUEDA_R, RUEDA_R)

    /**
     * La bomba del aire: «una caja de madera con dos ruedas a los lados, que mueven dos hombres; empuja el aire por la
     * manguera hasta el casco». En [s] (su frente, con el manómetro, mira hacia −z). La caja de caoba con sus cantoneras
     * de latón, la tapa con sus bisagras, el manómetro (una esfera blanca con su aguja, sin cifras), la boca de latón de la
     * manguera; a cada lado, la rueda de hierro con sus seis rayos y su manivela de madera gastada. [marcha]: girando (la
     * rueda se ve borrosa; los rayos los pinta la parte viva, [radiosEnMarcha]); [angulo], dónde quedó la manivela.
     */
    fun DrawScope.bomba(p: Pincel, s: Sitio, sol: Sol3, marcha: Boolean, angulo: Float, semilla: Long, lejos: Float = 0f) {
        val az = Azar(semilla)
        val e = s.escala(0f, 0f)
        val caoba = lerp(C.MADERA, C.ROJO, 0.3f)
        val cerca = if (s.seVe(V3(RUEDA_X, RUEDA_ALTO, 0f), V3(1f, 0f, 0f))) 1f else -1f
        // La rueda de detrás.
        ruedaDeBomba(p, s, sol, -cerca, marcha, angulo + 1.1f, lejos)
        // La caja: el zócalo y el cuerpo.
        val caja = carasDeCaja(-BOMBA_ANCHO / 2f, BOMBA_ANCHO / 2f, 0.06f, BOMBA_ALTO, -BOMBA_FONDO / 2f, BOMBA_FONDO / 2f)
        for (cara in visibles(s, carasDeCaja(-BOMBA_ANCHO / 2f - 0.03f, BOMBA_ANCHO / 2f + 0.03f, 0f, 0.07f, -BOMBA_FONDO / 2f - 0.03f, BOMBA_FONDO / 2f + 0.03f))) drawPath(forma(s, cara), p.por(C.MADERA_OSC, luzDe(s, sol, cara), lejos))
        for (cara in visibles(s, caja)) {
            val t = luzDe(s, sol, cara); val f = forma(s, cara)
            drawPath(f, p.por(caoba, t, lejos))
            clipPath(f) {
                // Las vetas de la caoba y el barniz gastado.
                repeat(6) { val v = az.entre(0.05f, 0.95f); linea(s, cara, 0f, v, 1f, v + az.entre(-0.04f, 0.04f), p.sombra(caoba, lejos).copy(alpha = 0.3f), (0.006f * e).coerceIn(0.3f, 1f)) }
                if (!cara.arriba) linea(s, cara, 0.03f, 0.04f, 0.97f, 0.04f, p.sombra(C.BREA, lejos).copy(alpha = 0.4f), (0.03f * e).coerceIn(0.4f, 3f))
                repeat(4) { val u = az.entre(0.1f, 0.9f); val v = az.entre(0.2f, 0.9f); linea(s, cara, u, v, u + az.entre(0.03f, 0.08f), v, p.luz(lerp(caoba, C.LONA, 0.5f), lejos).copy(alpha = 0.45f), (0.006f * e).coerceIn(0.3f, 0.9f), StrokeCap.Round) }
            }
            // Las cantoneras de latón en las esquinas.
            for ((u, v) in listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f)) {
                val q = s.p(cara.en(u, v)); drawCircle(p.por(C.LATON, t + 0.2f, lejos), 0.028f * e, q)
            }
            if (cara.arriba) {
                // La tapa: la junta y las dos bisagras.
                linea(s, cara, 0f, 0.5f, 1f, 0.5f, p.sombra(C.BREA, lejos).copy(alpha = 0.6f), (0.008f * e).coerceIn(0.3f, 1.2f))
                for (u in listOf(0.25f, 0.75f)) drawLine(p.por(C.LATON, t, lejos), s.p(cara.en(u - 0.06f, 0.5f)), s.p(cara.en(u + 0.06f, 0.5f)), (0.025f * e).coerceAtLeast(0.6f))
            } else if (cara.n.z < -0.5f) {
                // El frente: el manómetro y la boca de la manguera.
                val m0 = s.p(cara.en(0.5f, 0.68f)); val mr = 0.085f * e
                drawCircle(p.por(C.LATON, t + 0.15f, lejos), mr * 1.25f, m0)
                drawCircle(p.luz(C.BLANCO, lejos), mr, m0)
                drawCircle(p.sombra(C.BLANCO, lejos), mr, m0, style = Stroke(mr * 0.12f))
                for (k in 0..8) { val a = 2.5f + k * 0.55f; drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.7f), m0 + Offset(cos(a) * mr * 0.72f, sin(a) * mr * 0.72f), m0 + Offset(cos(a) * mr * 0.88f, sin(a) * mr * 0.88f), mr * 0.07f) }
                val aguja = if (marcha) 4.6f + 0.4f * az.real() else 2.6f
                drawLine(p.medio(C.ROJO, lejos), m0, m0 + Offset(cos(aguja) * mr * 0.75f, sin(aguja) * mr * 0.75f), mr * 0.1f, StrokeCap.Round)
                drawArc(Color.White.copy(alpha = 0.5f * p.r.contraste), 200f, 60f, false, Offset(m0.x - mr * 0.8f, m0.y - mr * 0.8f), Size(mr * 1.6f, mr * 1.6f), style = Stroke(mr * 0.12f))
                // La placa del fabricante (lisa, sin letras) y la boca de latón.
                val pl = listOf(cara.en(0.25f, 0.3f), cara.en(0.75f, 0.3f), cara.en(0.75f, 0.42f), cara.en(0.25f, 0.42f)).map { s.p(it) }
                drawPath(poligono(pl), p.por(C.LATON, t, lejos))
                val boca = s.p(cara.en(0.8f, 0.16f))
                drawCircle(p.por(C.LATON, t + 0.2f, lejos), 0.04f * e, boca); drawCircle(p.sombra(C.BREA, lejos), 0.018f * e, boca)
            }
            drawPath(f, p.sombra(C.BREA, lejos).copy(alpha = 0.45f), style = Stroke((0.006f * e).coerceIn(0.3f, 1f)))
            if (t < -0.1f) { val frio = ladoFrio(p); if (frio.alpha > 0.01f) drawPath(f, frio.copy(alpha = frio.alpha * (0.6f + 0.4f * (-0.1f - t).coerceAtMost(1f)))) }
        }
        // El eje que cruza la caja, y la rueda de delante.
        drawLine(p.sombra(C.PLOMO, lejos), s.p(cerca * BOMBA_ANCHO / 2f, RUEDA_ALTO, 0f), s.p(cerca * RUEDA_X, RUEDA_ALTO, 0f), (0.04f * e).coerceAtLeast(0.6f))
        ruedaDeBomba(p, s, sol, cerca, marcha, angulo, lejos)
    }

    private fun DrawScope.ruedaDeBomba(p: Pincel, s: Sitio, sol: Sol3, ld: Float, marcha: Boolean, angulo: Float, lejos: Float) {
        val e = s.escala(ld * RUEDA_X, 0f)
        val t = sol.t(s.normal(V3(ld, 0f, 0f)))
        val aro = (0..32).map { s.p(rueda(ld, it * 2f * PI.toFloat() / 32f)) }
        val dentro = (0..32).map { s.p(rueda(ld, it * 2f * PI.toFloat() / 32f, RUEDA_R * 0.86f)) }
        val hierro = C.PLOMO
        if (marcha) {
            // Girando: los rayos corridos, como un abanico de trazos tenues (los de la rueda de delante los mueve la parte viva).
            for (k in 0 until 18) { val a = angulo + k * PI.toFloat() / 9f; drawLine(p.por(hierro, t, lejos).copy(alpha = 0.16f), s.p(rueda(ld, a, 0.06f)), s.p(rueda(ld, a, RUEDA_R * 0.86f)), (0.035f * e).coerceAtLeast(0.6f), StrokeCap.Round) }
        } else {
            for (k in 0 until 6) { val a = angulo + k * PI.toFloat() / 3f; drawLine(p.por(hierro, t, lejos), s.p(rueda(ld, a, 0.06f)), s.p(rueda(ld, a, RUEDA_R * 0.88f)), (0.028f * e).coerceAtLeast(0.5f), StrokeCap.Round) }
        }
        val anillo = Path().apply { addPath(poligono(aro)); addPath(poligono(dentro.reversed())) }
        drawPath(anillo, p.por(hierro, t, lejos))
        // El filo de luz del canto de arriba del aro y su sombra abajo.
        drawPath(curva(aro.subList(18, 30)), p.luz(hierro, lejos).copy(alpha = 0.55f), style = Stroke((0.018f * e).coerceAtLeast(0.4f)))
        drawPath(curva(aro.subList(2, 14)), p.sombra(C.BREA, lejos).copy(alpha = 0.5f), style = Stroke((0.018f * e).coerceAtLeast(0.4f)))
        // El cubo.
        val cubo = s.p(rueda(ld, 0f, 0f))
        drawCircle(p.por(C.LATON, t, lejos), 0.06f * e, cubo); drawCircle(p.sombra(C.BREA, lejos), 0.025f * e, cubo)
        // La manivela: un mango de madera que sale hacia fuera en el aro.
        if (!marcha) manivela(p, s, ld, angulo, lejos)
    }

    private fun DrawScope.manivela(p: Pincel, s: Sitio, ld: Float, angulo: Float, lejos: Float) {
        val e = s.escala(ld * RUEDA_X, 0f)
        val a = s.p(rueda(ld, angulo, RUEDA_R * 0.82f)); val b = s.p(rueda(ld, angulo, RUEDA_R * 0.82f) + V3(ld * 0.2f, 0f, 0f))
        drawLine(p.sombra(C.MADERA_OSC, lejos), a, b, (0.06f * e).coerceAtLeast(1f), StrokeCap.Round)
        drawLine(p.luz(C.MADERA, lejos), a + Offset(0f, -0.012f * e), b + Offset(0f, -0.012f * e), (0.02f * e).coerceAtLeast(0.4f), StrokeCap.Round)
    }

    /** Los rayos y la manivela de la rueda de delante, girando con el [reloj]: la parte viva de la bomba en marcha (sin capas, unos pocos trazos). */
    fun DrawScope.radiosEnMarcha(p: Pincel, s: Sitio, reloj: Float, lejos: Float = 0f) {
        val ld = if (s.seVe(V3(RUEDA_X, RUEDA_ALTO, 0f), V3(1f, 0f, 0f))) 1f else -1f
        val e = s.escala(ld * RUEDA_X, 0f)
        val ang = reloj * 3.4f
        for (k in 0 until 6) { val a = ang + k * PI.toFloat() / 3f; drawLine(p.medio(C.PLOMO, lejos).copy(alpha = 0.85f), s.p(rueda(ld, a, 0.06f)), s.p(rueda(ld, a, RUEDA_R * 0.86f)), (0.024f * e).coerceAtLeast(0.5f), StrokeCap.Round) }
        manivela(p, s, ld, ang, lejos)
    }

    // =================================================================
    //  Cabos, manguera, escala
    // =================================================================

    /** Un cabo por los [puntos] (una curva suave), de [grueso] unidades: la sombra, el cuerpo, el filo de luz y la colcha. */
    fun DrawScope.cabo(p: Pincel, puntos: List<Offset>, grueso: Float, color: Color = C.CANAMO, lejos: Float = 0f, alfa: Float = 1f) {
        val camino = curva(puntos); val g = grueso.coerceAtLeast(0.3f)
        drawPath(camino, p.sombra(color, lejos).copy(alpha = alfa), style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
        if (g > 0.6f) drawPath(Path().apply { addPath(camino, Offset(-g * 0.18f, -g * 0.2f)) }, p.luz(color, lejos).copy(alpha = 0.6f * alfa), style = Stroke(g * 0.36f, cap = StrokeCap.Round))
        if (g > 1.4f) drawPath(camino, p.medio(lerp(color, C.BREA, 0.25f), lejos).copy(alpha = 0.5f * alfa), style = Stroke(g * 0.85f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.2f, g * 0.7f))))
    }

    /**
     * La manguera del aire por los [puntos]: de goma negra forrada de lona, con las vueltas de cinta cada tanto y el brillo
     * de lo mojado. [alfa] la apaga (la parte que va bajo el agua).
     */
    fun DrawScope.manguera(p: Pincel, puntos: List<Offset>, grueso: Float, lejos: Float = 0f, alfa: Float = 1f) {
        val camino = curva(puntos); val g = grueso.coerceAtLeast(0.4f)
        val goma = lerp(C.BREA, C.PLOMO, 0.3f)
        drawPath(camino, p.sombra(goma, lejos).copy(alpha = alfa), style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(camino, p.medio(goma, lejos).copy(alpha = alfa), style = Stroke(g * 0.6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(Path().apply { addPath(camino, Offset(-g * 0.15f, -g * 0.22f)) }, p.luz(C.BLANCO, lejos).copy(alpha = 0.35f * alfa * p.r.contraste), style = Stroke(g * 0.18f, cap = StrokeCap.Round))
        if (g > 1f) drawPath(camino, p.medio(C.LONA, lejos).copy(alpha = 0.8f * alfa), style = Stroke(g * 1.05f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.5f, g * 7f))))
    }

    /** Una aduja (un cabo o la manguera en espiral, en el suelo) con centro [c] y radios [rx] × [ry]. */
    fun DrawScope.aduja(p: Pincel, c: Offset, rx: Float, ry: Float, vueltas: Int, grueso: Float, color: Color, lejos: Float = 0f, manguera: Boolean = false) {
        drawOval(p.sombra(C.BREA, lejos).copy(alpha = if (manguera) 0.18f else 0.3f), Offset(c.x - rx * 1.1f, c.y - ry * 0.9f), Size(rx * 2.2f, ry * 2.1f))
        for (i in 0 until vueltas) {
            val k = 1f - i * 0.6f / vueltas; val y = c.y - i * grueso * 0.55f
            val o = Offset(c.x - rx * k, y - ry * k); val z = Size(rx * k * 2f, ry * k * 2f)
            drawOval((if (manguera) p.medio(lerp(C.BREA, C.PLOMO, 0.45f), lejos) else p.sombra(color, lejos)), o, z, style = Stroke(grueso))
            drawArc((if (manguera) p.luz(lerp(C.PLOMO, C.BLANCO, 0.2f), lejos) else p.luz(color, lejos)).copy(alpha = 0.7f), 190f, 150f, false, o + Offset(0f, -grueso * 0.25f), z, style = Stroke(grueso * 0.35f))
            if (manguera) drawOval(p.medio(C.LONA, lejos).copy(alpha = 0.5f), o, z, style = Stroke(grueso * 1.05f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(grueso * 0.6f, grueso * 6f))))
        }
    }

    /**
     * La escala de cuerda: los dos cabos de [a0]/[a1] (arriba, en la regala) a [b0]/[b1] (abajo), con sus [peldanos] de
     * madera. [alfa] la apaga (bajo el agua); [desdeAlfa] hace que se apague hacia abajo.
     */
    fun DrawScope.escala(p: Pincel, a0: Offset, a1: Offset, b0: Offset, b1: Offset, peldanos: Int, grueso: Float, lejos: Float = 0f, alfaAbajo: Float = 1f) {
        for (i in 0 until peldanos) {
            val t = (i + 0.6f) / peldanos; val al = 1f - (1f - alfaAbajo) * t
            val q0 = a0 + (b0 - a0) * t; val q1 = a1 + (b1 - a1) * t
            drawLine(p.sombra(C.MADERA_OSC, lejos).copy(alpha = al), q0 + Offset(0f, grueso * 0.5f), q1 + Offset(0f, grueso * 0.5f), grueso * 1.1f)
            drawLine(p.luz(C.MADERA, lejos).copy(alpha = al), q0, q1, grueso * 0.8f, StrokeCap.Round)
        }
        drawLine(p.sombra(C.CANAMO, lejos), a0, b0, grueso * 0.55f, StrokeCap.Round)
        drawLine(p.sombra(C.CANAMO, lejos), a1, b1, grueso * 0.55f, StrokeCap.Round)
    }

    // =================================================================
    //  La tina, el mazo, el cubo
    // =================================================================

    /**
     * La tina de duelas donde se lavan las esponjas, en [s], de [radio] y [alto] metros: las duelas con sus juntas, los
     * aros de hierro, el agua dentro (que devuelve el cielo) y [cuantas] esponjas flotando, negras o ya limpias.
     */
    fun DrawScope.tina(p: Pincel, s: Sitio, sol: Sol3, radio: Float, alto: Float, cuantas: Int, negras: Boolean, semilla: Long, lejos: Float = 0f) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        val n = 28
        val boca = (0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(radio * cos(a), alto, radio * sin(a)) }
        val base = (0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(radio * 0.9f * cos(a), 0f, radio * 0.9f * sin(a)) }
        // El cuerpo: la envolvente de la boca y la base.
        val cuerpo = poligono(envolvente(boca + base))
        val madera = lerp(C.MADERA, C.MADERA_GRIS, az.entre(0.2f, 0.5f))
        val x0 = boca.minOf { it.x }; val x1 = boca.maxOf { it.x }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(if (sol.sx < 0f) p.luz(madera, lejos) else p.sombra(madera, lejos), p.medio(madera, lejos), if (sol.sx < 0f) p.sombra(madera, lejos) else p.luz(madera, lejos)), x0, x1))
        enfriar(p, cuerpo, Offset((x0 + x1) / 2f, (boca.minOf { it.y } + base.maxOf { it.y }) / 2f), (x1 - x0) * 0.5f)
        clipPath(cuerpo) {
            // Las duelas.
            for (i in 0 until 14) { val a = PI.toFloat() * (0.08f + 0.84f * i / 13f); drawLine(p.sombra(lerp(madera, C.BREA, 0.4f), lejos).copy(alpha = 0.5f), s.p(radio * cos(a), alto, -radio * sin(a)), s.p(radio * 0.9f * cos(a), 0f, -radio * 0.9f * sin(a)), (0.006f * e).coerceAtLeast(0.3f)) }
            // Los aros de hierro.
            for (h in listOf(0.18f, 0.78f)) {
                val anillo = (0..16).map { val a = PI.toFloat() * it / 16f; val rr = radio * (0.9f + 0.1f * h); s.p(rr * cos(a), alto * h, -rr * sin(a)) }
                drawPath(curva(anillo), p.sombra(C.PLOMO, lejos), style = Stroke((0.03f * e).coerceAtLeast(0.6f)))
                drawPath(curva(anillo.map { it + Offset(0f, -0.01f * e) }), p.luz(C.PLOMO, lejos).copy(alpha = 0.5f), style = Stroke((0.008f * e).coerceAtLeast(0.3f)))
            }
            // El agua que se salió y la mancha de humedad abajo.
            drawRect(p.sombra(lerp(madera, C.BREA, 0.3f), lejos).copy(alpha = 0.35f), Offset(x0, base.maxOf { it.y } - 0.12f * e), Size(x1 - x0, 0.12f * e))
        }
        // La boca: el canto de las duelas, y el agua dentro.
        val bocaP = poligono(boca)
        drawPath(bocaP, p.por(madera, 0.6f, lejos))
        val aguaP = poligono((0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(radio * 0.9f * cos(a), alto * 0.86f, radio * 0.9f * sin(a)) })
        drawPath(aguaP, Brush.verticalGradient(listOf(lerp(p.medio(C.MAR, lejos), p.luz(C.BLANCO, lejos), 0.25f), p.sombra(C.MAR_HONDO, lejos)), boca.minOf { it.y }, boca.maxOf { it.y }))
        clipPath(aguaP) {
            for (i in 0 until cuantas) {
                val a = az.entre(0f, 6.28f); val d = sqrt(az.real()) * radio * 0.6f
                esponja(p, s.p(d * cos(a), alto * 0.88f, d * sin(a)), 0.07f * e * az.entre(0.8f, 1.2f), semilla * 7 + i, !negras, lejos)
            }
            drawArc(Color.White.copy(alpha = 0.35f * p.r.contraste), 200f, 80f, false, Offset(boca.minOf { it.x } + 0.1f * e, boca.minOf { it.y } + 0.02f * e), Size((x1 - x0) * 0.6f, 0.12f * e), style = Stroke((0.012f * e).coerceAtLeast(0.4f)))
        }
    }

    /** Las esquinas de la tina (para su sombra). */
    fun tinaEsquinas(radio: Float, alto: Float): List<V3> = esquinas(-radio, radio, 0f, alto, -radio, radio)

    /** El mazo de madera, recostado de [pie] (la cabeza en el suelo) a [mango] (el extremo del mango, apoyado). */
    fun DrawScope.mazo(p: Pincel, pie: Offset, mango: Offset, grueso: Float, lejos: Float = 0f) {
        drawLine(p.sombra(C.MADERA_OSC, lejos), pie, mango, grueso * 0.32f, StrokeCap.Round)
        drawLine(p.luz(C.MADERA, lejos), pie + Offset(-grueso * 0.06f, -grueso * 0.08f), mango + Offset(-grueso * 0.06f, -grueso * 0.08f), grueso * 0.1f, StrokeCap.Round)
        val d = (mango - pie); val u = d / d.getDistance().coerceAtLeast(0.01f); val n = Offset(-u.y, u.x)
        val c = pie + u * grueso * 0.3f
        val cabeza = poligono(c + n * grueso * 0.75f, c + n * grueso * 0.75f + u * grueso * 0.9f, c - n * grueso * 0.75f + u * grueso * 0.9f, c - n * grueso * 0.75f)
        drawPath(cabeza, Brush.linearGradient(listOf(p.luz(C.MADERA_GRIS, lejos), p.sombra(C.MADERA_OSC, lejos)), c + n * grueso, c - n * grueso))
        drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.5f), c + n * grueso * 0.75f + u * grueso * 0.45f, c - n * grueso * 0.75f + u * grueso * 0.45f, grueso * 0.06f)
    }

    /**
     * Un cubo de madera de duelas con su aro de hierro y su asa de cuerda, lleno de agua de mar que devuelve el cielo, en
     * [s]: para mojar las esponjas y lavarse la cara («un cubo para lavarse la cara con agua del mar»).
     */
    fun DrawScope.cuboDeMadera(p: Pincel, s: Sitio, sol: Sol3, semilla: Long, lejos: Float = 0f) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        val r0 = 0.13f; val r1 = 0.11f; val h = 0.3f; val n = 20
        val boca = (0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(r0 * cos(a), h, r0 * sin(a)) }
        val base = (0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(r1 * cos(a), 0f, r1 * sin(a)) }
        val cuerpo = poligono(envolvente(boca + base))
        val madera = lerp(C.MADERA, C.MADERA_GRIS, az.entre(0.3f, 0.6f))
        val x0 = boca.minOf { it.x }; val x1 = boca.maxOf { it.x }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(if (sol.sx < 0f) p.luz(madera, lejos) else p.sombra(madera, lejos), p.medio(madera, lejos), if (sol.sx < 0f) p.sombra(madera, lejos) else p.luz(madera, lejos)), x0, x1))
        clipPath(cuerpo) {
            for (i in 0 until 8) { val a = PI.toFloat() * (0.1f + 0.8f * i / 7f); drawLine(p.sombra(lerp(madera, C.BREA, 0.4f), lejos).copy(alpha = 0.5f), s.p(r0 * cos(a), h, -r0 * sin(a)), s.p(r1 * cos(a), 0f, -r1 * sin(a)), (0.006f * e).coerceAtLeast(0.3f)) }
            for (hh in listOf(0.22f, 0.8f)) drawPath(curva((0..12).map { val a = PI.toFloat() * it / 12f; val rr = r1 + (r0 - r1) * hh; s.p(rr * cos(a), h * hh, -rr * sin(a)) }), p.sombra(C.PLOMO, lejos), style = Stroke((0.02f * e).coerceAtLeast(0.5f)))
        }
        drawPath(poligono(boca), p.por(madera, 0.6f, lejos))
        val aguaP = poligono((0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(r0 * 0.88f * cos(a), h * 0.88f, r0 * 0.88f * sin(a)) })
        drawPath(aguaP, Brush.verticalGradient(listOf(lerp(p.medio(C.MAR, lejos), p.luz(C.BLANCO, lejos), 0.3f), p.sombra(C.MAR_HONDO, lejos)), boca.minOf { it.y }, boca.maxOf { it.y }))
        // El asa de cuerda, caída de lado.
        val a = s.p(-r0, h, 0f); val b = s.p(r0, h, 0f)
        drawPath(Path().apply { moveTo(a); cubicTo(a + Offset(0f, 0.06f * e), b + Offset(-0.05f * e, 0.14f * e), b) }, p.sombra(C.CANAMO, lejos), style = Stroke((0.012f * e).coerceAtLeast(0.4f)))
    }

    /**
     * La red de las esponjas colgada a escurrir de la regala en [arriba], de [k] unidades por metro: «un saco de malla
     * gruesa, con un aro de hierro en la boca». El aro, abierto, apoyado de canto contra la borda; el saco, flojo, con la
     * malla en rombos y una esponja negra olvidada en el fondo, y el agua que todavía gotea.
     */
    fun DrawScope.redDeEsponjas(p: Pincel, arriba: Offset, k: Float, semilla: Long, lejos: Float = 0f) {
        val az = Azar(semilla)
        val r = 0.2f * k
        val aro = Offset(arriba.x - r * 0.3f, arriba.y + r * 0.9f)
        // El saco: cae del aro y se abomba abajo.
        val saco = lazo(listOf(aro + Offset(-r * 0.95f, 0f), aro + Offset(-r * 0.9f, r * 1.2f), aro + Offset(-r * 0.4f, r * 2.1f), aro + Offset(r * 0.25f, r * 2.2f), aro + Offset(r * 0.8f, r * 1.3f), aro + Offset(r * 0.95f, 0f)))
        drawPath(saco, p.sombra(C.CANAMO, lejos).copy(alpha = 0.25f))
        clipPath(saco) {
            for (i in -6..6) {
                drawLine(p.sombra(C.CANAMO, lejos).copy(alpha = 0.85f), aro + Offset(i * r * 0.32f - r, -r * 0.2f), aro + Offset(i * r * 0.32f + r * 0.6f, r * 2.4f), (0.012f * k).coerceAtLeast(0.35f))
                drawLine(p.sombra(C.CANAMO, lejos).copy(alpha = 0.85f), aro + Offset(i * r * 0.32f + r, -r * 0.2f), aro + Offset(i * r * 0.32f - r * 0.6f, r * 2.4f), (0.012f * k).coerceAtLeast(0.35f))
            }
            esponja(p, aro + Offset(-r * 0.15f, r * 1.75f), r * 0.32f, semilla * 5, false, lejos)
        }
        drawPath(saco, p.luz(C.CANAMO, lejos).copy(alpha = 0.4f), style = Stroke((0.008f * k).coerceAtLeast(0.3f)))
        drawOval(p.sombra(C.PLOMO, lejos), Offset(aro.x - r, aro.y - r * 0.25f), Size(r * 2f, r * 0.5f), style = Stroke((0.03f * k).coerceAtLeast(0.6f)))
        drawArc(p.luz(C.PLOMO, lejos).copy(alpha = 0.6f), 190f, 120f, false, Offset(aro.x - r, aro.y - r * 0.25f), Size(r * 2f, r * 0.5f), style = Stroke((0.01f * k).coerceAtLeast(0.3f)))
        drawLine(p.sombra(C.CANAMO, lejos), arriba, aro + Offset(r * 0.6f, -r * 0.2f), (0.014f * k).coerceAtLeast(0.4f))
        repeat(3) { drawCircle(p.luz(C.MAR_CLARO, lejos).copy(alpha = 0.5f), (0.008f * k).coerceAtLeast(0.3f), aro + Offset(az.entre(-0.3f, 0.3f) * r, r * (2.3f + az.entre(0f, 0.4f)))) }
    }

    /**
     * El cordero de Pascua asándose entero en su espetón, en [pie] (en la cubierta), de [k] unidades por metro: dos
     * horquillas de madera, el espetón de hierro con su manivela, el cordero tostado, con su brillo de grasa y las patas
     * atadas, y debajo la cama de brasas en su bandeja de chapa, que alumbra la panza (el humo lo pone la parte viva).
     */
    fun DrawScope.corderoEnElEspeton(p: Pincel, pie: Offset, k: Float, lejos: Float = 0f) {
        val largo = 0.9f * k; val alto = 0.48f * k
        val i0 = pie + Offset(-largo * 0.5f, 0f); val i1 = pie + Offset(largo * 0.5f, -0.04f * k)
        // La bandeja de las brasas y las brasas.
        drawRect(p.sombra(C.PLOMO, lejos), Offset(pie.x - largo * 0.36f, pie.y - 0.06f * k), Size(largo * 0.72f, 0.06f * k))
        val az = Azar(57)
        repeat(14) { val q = Offset(pie.x + az.entre(-0.33f, 0.33f) * largo, pie.y - az.entre(0.05f, 0.1f) * k); drawCircle(lerp(Color(0xFFB0401E), Color(0xFFFFA040), az.real()), az.entre(0.012f, 0.025f) * k, q) }
        resplandor(pie + Offset(0f, -0.15f * k), largo * 0.6f, 0.55f, C.LUMBRE)
        // Las dos horquillas.
        for (a in listOf(i0, i1)) {
            drawLine(p.sombra(C.MADERA_OSC, lejos), a, a + Offset(0f, -alto), 0.035f * k, StrokeCap.Round)
            drawLine(p.medio(C.MADERA_OSC, lejos), a + Offset(0f, -alto), a + Offset(-0.05f * k, -alto - 0.08f * k), 0.025f * k, StrokeCap.Round)
            drawLine(p.medio(C.MADERA_OSC, lejos), a + Offset(0f, -alto), a + Offset(0.05f * k, -alto - 0.08f * k), 0.025f * k, StrokeCap.Round)
        }
        // El cordero, ensartado: un cuerpo largo y tostado, más oscuro abajo (el lado del fuego, quemado) y con su brillo arriba.
        val e0 = i0 + Offset(-0.04f * k, -alto); val e1 = i1 + Offset(0.08f * k, -alto)
        val c = (e0 + e1) * 0.5f + Offset(0f, 0.01f * k)
        val cuerpo = lazo(listOf(c + Offset(-0.36f * k, 0f), c + Offset(-0.25f * k, -0.1f * k), c + Offset(0.05f * k, -0.12f * k), c + Offset(0.3f * k, -0.08f * k), c + Offset(0.38f * k, 0.01f * k), c + Offset(0.25f * k, 0.1f * k), c + Offset(-0.05f * k, 0.11f * k), c + Offset(-0.3f * k, 0.08f * k)))
        val tostado = lerp(C.MADERA, C.ROJO, 0.25f)
        drawPath(cuerpo, Brush.verticalGradient(listOf(p.luz(lerp(tostado, C.ESPONJA, 0.3f), lejos), p.medio(tostado, lejos), lerp(p.sombra(C.MADERA_OSC, lejos), C.LUMBRE, 0.35f)), c.y - 0.12f * k, c.y + 0.11f * k))
        drawArc(Color.White.copy(alpha = 0.35f * p.r.contraste), 200f, 120f, false, Offset(c.x - 0.25f * k, c.y - 0.11f * k), Size(0.5f * k, 0.12f * k), style = Stroke(0.012f * k, cap = StrokeCap.Round))
        // Las patas atadas, delante y detrás, y el espetón que lo atraviesa, con su manivela.
        for (s in listOf(-1f, 1f)) drawLine(p.sombra(tostado, lejos), c + Offset(s * 0.3f * k, 0.04f * k), c + Offset(s * 0.42f * k, 0.12f * k), 0.025f * k, StrokeCap.Round)
        drawLine(p.sombra(C.PLOMO, lejos), e0 + Offset(-0.06f * k, 0f), e1 + Offset(0.04f * k, 0f), 0.014f * k, StrokeCap.Round)
        drawLine(p.medio(C.PLOMO, lejos), e1 + Offset(0.04f * k, 0f), e1 + Offset(0.04f * k, 0.08f * k), 0.012f * k, StrokeCap.Round)
    }

    // =================================================================
    //  El bote con su cubo de cristal
    // =================================================================

    /** Medio contorno de la borda del bote (de popa a proa) en sus ejes: x a estribor, z hacia proa, en metros. */
    private fun bordaDelBote(largo: Float, manga: Float): List<Pair<Float, Float>> = (0..10).map { i ->
        val t = i / 10f; val z = -largo / 2f + largo * t
        val w = manga / 2f * (if (t < 0.15f) 0.78f + 1.4f * t else sqrt((1f - ((t - 0.15f) / 0.85f).let { it * it * it })).coerceAtLeast(0f))
        w to z
    }

    /**
     * El bote auxiliar a flote en [s] (su z, hacia proa): «el bote pequeño, el que lleva al buzo al sitio exacto… Desde el
     * bote se mira el fondo con un cubo de cristal, como una ventana en el agua». Blanco con la regala azul, las bancadas,
     * los remos recogidos y el cubo de cristal. [alto] es la borda sobre el agua. Su reflejo lo pone el cuadro.
     */
    fun DrawScope.bote(p: Pincel, s: Sitio, sol: Sol3, largo: Float, manga: Float, semilla: Long, lejos: Float = 0f, alto: Float = 0.38f) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        val medio = bordaDelBote(largo, manga)
        val estribor = medio.map { (w, z) -> V3(w, alto, z) }; val babor = medio.map { (w, z) -> V3(-w, alto, z) }
        val borda = estribor + babor.reversed()
        val flota = (medio.map { (w, z) -> V3(w * 0.88f, 0f, z) } + medio.reversed().map { (w, z) -> V3(-w * 0.88f, 0f, z) })
        // El casco por fuera: la envolvente de la borda y la línea de flotación.
        val fuera = poligono(envolvente((borda + flota).map { s.p(it) }))
        val ladoVisto = if (s.seVe(V3(manga / 2f, alto / 2f, 0f), V3(1f, 0f, 0f))) 1f else -1f
        val t = sol.t(s.normal(V3(ladoVisto, 0f, 0f)))
        drawPath(fuera, p.por(C.BLANCO, t, lejos))
        clipPath(fuera) {
            // La franja azul bajo la regala, la tablazón (tingladillo) y lo verde de la flotación.
            val franja = poligono((estribor.map { s.p(it) } + estribor.reversed().map { s.p(it.copy(y = alto * 0.78f)) }).let { if (ladoVisto > 0) it else (babor.map { s.p(it) } + babor.reversed().map { s.p(it.copy(y = alto * 0.78f)) }) })
            drawPath(franja, p.por(C.AZUL, t, lejos))
            for (h in listOf(0.55f, 0.32f)) drawPath(curva((if (ladoVisto > 0) medio.map { (w, z) -> V3(w * (0.94f + 0.06f * h), alto * h, z) } else medio.map { (w, z) -> V3(-w * (0.94f + 0.06f * h), alto * h, z) }).map { s.p(it) }), p.sombra(C.BLANCO, lejos).copy(alpha = 0.6f), style = Stroke((0.012f * e).coerceAtLeast(0.3f)))
            drawPath(curva((if (ladoVisto > 0) medio.map { (w, z) -> V3(w * 0.9f, 0.04f, z) } else medio.map { (w, z) -> V3(-w * 0.9f, 0.04f, z) }).map { s.p(it) }), p.medio(C.VERDIN, lejos).copy(alpha = 0.7f), style = Stroke((0.05f * e).coerceAtLeast(0.6f)))
        }
        // Por dentro: el fondo, las bancadas, los remos y el cubo de cristal.
        val dentro = poligono(borda.map { s.p(it) })
        drawPath(dentro, p.por(C.MADERA_GRIS, 0.2f, lejos))
        clipPath(dentro) {
            val fondoBote = poligono(medio.map { (w, z) -> s.p(V3(w * 0.6f, 0.08f, z * 0.92f)) } + medio.reversed().map { (w, z) -> s.p(V3(-w * 0.6f, 0.08f, z * 0.92f)) })
            drawPath(fondoBote, p.por(C.MADERA, 0.5f, lejos))
            for (k in -3..3) drawLine(p.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.4f), s.p(V3(k * manga * 0.08f, 0.09f, -largo * 0.42f)), s.p(V3(k * manga * 0.08f, 0.09f, largo * 0.4f)), (0.008f * e).coerceAtLeast(0.3f))
            // El agua de la sentina.
            drawOval(p.medio(C.MAR_CLARO, lejos).copy(alpha = 0.5f), Offset(s.p(V3(0f, 0.08f, -largo * 0.2f)).x - 0.25f * e, s.p(V3(0f, 0.08f, -largo * 0.2f)).y - 0.04f * e), Size(0.5f * e, 0.08f * e))
        }
        for (z in listOf(-largo * 0.28f, largo * 0.1f)) {
            val w = medio.minByOrNull { abs(it.second - z) }!!.first
            val a = s.p(V3(-w * 0.95f, alto * 0.72f, z)); val b = s.p(V3(w * 0.95f, alto * 0.72f, z))
            drawLine(p.sombra(C.MADERA_OSC, lejos), a + Offset(0f, 0.03f * e), b + Offset(0f, 0.03f * e), (0.07f * e).coerceAtLeast(0.6f))
            drawLine(p.luz(C.MADERA, lejos), a, b, (0.05f * e).coerceAtLeast(0.5f))
        }
        // Los remos, recogidos a lo largo.
        for (x in listOf(-0.18f, -0.08f)) {
            val a = s.p(V3(x, alto * 0.82f, -largo * 0.45f)); val b = s.p(V3(x + 0.04f, alto * 0.85f, largo * 0.38f))
            drawLine(p.sombra(C.MADERA_OSC, lejos), a, b, (0.04f * e).coerceAtLeast(0.5f), StrokeCap.Round)
            drawLine(p.luz(C.MADERA_GRIS, lejos), a, b, (0.015f * e).coerceAtLeast(0.3f), StrokeCap.Round)
            val pala = s.p(V3(x + 0.04f, alto * 0.85f, largo * 0.38f))
            drawOval(p.por(C.MADERA_GRIS, 0.3f, lejos), Offset(pala.x - 0.05f * e, pala.y - 0.02f * e), Size(0.12f * e, 0.05f * e))
        }
        // El cubo de cristal: un cubo de madera con el fondo de vidrio, que brilla.
        val cb = V3(manga * 0.12f, 0.1f, largo * 0.22f)
        val top = s.p(cb + V3(0f, 0.3f, 0f)); val bot = s.p(cb); val rc = 0.15f * e
        val cubo = poligono(listOf(Offset(top.x - rc, top.y), Offset(top.x + rc, top.y), Offset(bot.x + rc * 0.82f, bot.y), Offset(bot.x - rc * 0.82f, bot.y)))
        drawPath(cubo, Brush.horizontalGradient(listOf(p.luz(C.MADERA_GRIS, lejos), p.sombra(C.MADERA_OSC, lejos)), top.x - rc, top.x + rc))
        drawLine(p.sombra(C.PLOMO, lejos), Offset(top.x - rc * 0.95f, top.y + (bot.y - top.y) * 0.3f), Offset(top.x + rc * 0.95f, top.y + (bot.y - top.y) * 0.3f), (0.015f * e).coerceAtLeast(0.4f))
        drawOval(p.sombra(C.MADERA_OSC, lejos), Offset(top.x - rc, top.y - rc * 0.32f), Size(rc * 2f, rc * 0.64f))
        drawOval(Brush.linearGradient(listOf(p.luz(C.MAR_CLARO, lejos), lerp(p.luz(C.BLANCO, lejos), Color.White, 0.3f), p.medio(C.MAR, lejos)), Offset(top.x - rc, top.y), Offset(top.x + rc, top.y)), Offset(top.x - rc * 0.82f, top.y - rc * 0.24f), Size(rc * 1.64f, rc * 0.48f))
        // La regala: el canto de arriba, azul, con su filo de luz.
        drawPath(curva(borda.map { s.p(it) } + s.p(borda.first())), p.por(C.AZUL, t + 0.3f, lejos), style = Stroke((0.045f * e).coerceAtLeast(0.6f), join = StrokeJoin.Round))
        drawPath(curva(estribor.map { s.p(it) }), p.luz(C.BLANCO, lejos).copy(alpha = 0.5f), style = Stroke((0.012f * e).coerceAtLeast(0.3f)))
    }

    /** El contorno del bote en la flotación (para su reflejo, su sombra y la luz verde del agua de noche). */
    fun boteFlotacion(s: Sitio, largo: Float, manga: Float): Path {
        val medio = bordaDelBote(largo, manga)
        return poligono((medio.map { (w, z) -> V3(w, 0f, z) } + medio.reversed().map { (w, z) -> V3(-w, 0f, z) }).map { s.p(it) })
    }

    // =================================================================
    //  El hornillo, el farol, la hornacina del icono
    // =================================================================

    /** El hornillo de la proa: una caja de hierro sobre ladrillos, con la olla común; [encendido], su boca roja y su humo (el humo lo pone la parte viva). */
    fun DrawScope.hornillo(p: Pincel, pie: Offset, k: Float, encendido: Boolean, lejos: Float = 0f) {
        val w = 0.5f * k; val h = 0.42f * k
        drawRect(p.medio(lerp(C.ROJO, C.MADERA_OSC, 0.4f), lejos), Offset(pie.x - w / 2f, pie.y - h * 0.3f), Size(w, h * 0.3f))
        drawRect(Brush.horizontalGradient(listOf(p.luz(C.PLOMO, lejos), p.sombra(C.PLOMO, lejos)), pie.x - w / 2f, pie.x + w / 2f), Offset(pie.x - w * 0.45f, pie.y - h), Size(w * 0.9f, h * 0.7f))
        val boca = Rect(pie.x - w * 0.18f, pie.y - h * 0.62f, pie.x + w * 0.18f, pie.y - h * 0.4f)
        drawRect(if (encendido) Color(0xFFFF7A2A) else p.sombra(C.BREA, lejos), boca.topLeft, boca.size)
        // La olla, con su asa.
        val o = Offset(pie.x, pie.y - h * 1.18f)
        drawOval(Brush.horizontalGradient(listOf(p.luz(C.PLOMO, lejos), p.sombra(C.BREA, lejos)), o.x - w * 0.35f, o.x + w * 0.35f), Offset(o.x - w * 0.35f, o.y - h * 0.22f), Size(w * 0.7f, h * 0.44f))
        drawArc(p.sombra(C.PLOMO, lejos), 180f, 180f, false, Offset(o.x - w * 0.3f, o.y - h * 0.5f), Size(w * 0.6f, h * 0.6f), style = Stroke(k * 0.015f))
        if (encendido) resplandor(boca.center, w * 1.1f, 0.4f + 0.6f * p.l.noche, C.LUMBRE)
    }

    /**
     * El farol de cubierta, de latón y vidrio, colgado o puesto en [c] (el centro del vidrio), de [k] unidades por metro.
     * Encendido ([fuerza] de 0 a 1), la llama dentro y el halo; apagado, el vidrio ahumado que devuelve el cielo.
     */
    fun DrawScope.farol(p: Pincel, c: Offset, k: Float, fuerza: Float, lejos: Float = 0f) {
        val w = 0.13f * k; val h = 0.2f * k
        drawRect(p.medio(C.LATON, lejos), Offset(c.x - w * 0.7f, c.y + h * 0.5f), Size(w * 1.4f, h * 0.18f))
        val vidrio = Path().apply { moveTo(c.x - w * 0.5f, c.y + h * 0.5f); cubicTo(c.x - w * 0.75f, c.y + h * 0.1f, c.x - w * 0.75f, c.y - h * 0.2f, c.x - w * 0.4f, c.y - h * 0.5f); lineTo(c.x + w * 0.4f, c.y - h * 0.5f); cubicTo(c.x + w * 0.75f, c.y - h * 0.2f, c.x + w * 0.75f, c.y + h * 0.1f, c.x + w * 0.5f, c.y + h * 0.5f); close() }
        if (fuerza > 0.02f) {
            drawPath(vidrio, Brush.radialGradient(listOf(Color(0xFFFFF0B8), Color(0xFFFFB24C), lerp(Color(0xFFB0602A), p.medio(C.LATON, lejos), 1f - fuerza)), c, w * 0.8f))
            drawOval(Color(0xFFFFF6D0), Offset(c.x - w * 0.12f, c.y - h * 0.15f), Size(w * 0.24f, h * 0.36f))
        } else {
            drawPath(vidrio, Brush.verticalGradient(listOf(p.luz(C.BLANCO, lejos).copy(alpha = 0.85f), p.sombra(C.LONA, lejos)), c.y - h * 0.5f, c.y + h * 0.5f))
            drawLine(p.sombra(C.BREA, lejos), Offset(c.x, c.y + h * 0.3f), Offset(c.x, c.y), w * 0.08f)
        }
        for (u in listOf(-0.5f, 0f, 0.5f)) drawLine(p.sombra(C.LATON, lejos), Offset(c.x + u * w * 1.1f, c.y - h * 0.5f), Offset(c.x + u * w, c.y + h * 0.5f), w * 0.07f)
        val tapa = Path().apply { moveTo(c.x - w * 0.6f, c.y - h * 0.5f); lineTo(c.x, c.y - h * 0.85f); lineTo(c.x + w * 0.6f, c.y - h * 0.5f); close() }
        drawPath(tapa, Brush.horizontalGradient(listOf(p.luz(C.LATON, lejos), p.sombra(C.LATON, lejos)), c.x - w * 0.6f, c.x + w * 0.6f))
        drawArc(p.sombra(C.LATON, lejos), 180f, 180f, false, Offset(c.x - w * 0.3f, c.y - h * 1.15f), Size(w * 0.6f, h * 0.5f), style = Stroke(w * 0.08f))
    }

    /**
     * La hornacina del icono, clavada en la popa: una cajita de madera azul con su tejadillo, y dentro la tabla del santo
     * («el santo de barba blanca y un libro en la mano»), sin cara dibujada: la aureola de oro, la mancha clara de la
     * cabeza y la barba, el manto rojo, el libro. Delante cuelga la lamparilla de aceite en su vaso rojo, «que no se apaga
     * nunca, ni con tormenta» ([lumbre], de 0 a 1: de día apenas se nota; la llama la mueve la parte viva). [c] es el centro
     * de la tabla y [k] unidades por metro. [luto]: un paño negro sobre el marco. [velas] y [huevos]: Pascua y la Dormición.
     */
    fun DrawScope.hornacina(p: Pincel, c: Offset, k: Float, lumbre: Float, mira: Float = 0.3f, luto: Boolean = false, velas: Boolean = false, huevos: Boolean = false, lejos: Float = 0f) {
        val w = 0.19f * k; val h = 0.25f * k; val fondo = 0.09f * k
        val ld = lado(p); val s = if (mira < 0f) -1f else 1f
        val noche = p.l.noche * lumbre
        val madera = lerp(C.AZUL, C.MADERA_GRIS, 0.35f)
        val x0 = c.x - w * 0.66f; val x1 = c.x + w * 0.66f; val y0 = c.y - h * 0.62f; val y1 = c.y + h * 0.7f
        // El costado que se ve, de tablas, con su veta.
        val lx = if (s > 0) x1 else x0
        val costado = poligono(Offset(lx, y0), Offset(lx + s * fondo, y0 - fondo * 0.18f), Offset(lx + s * fondo, y1 - fondo * 0.22f), Offset(lx, y1))
        drawPath(costado, if (mira * ld > 0) p.luz(madera, lejos) else p.sombra(madera, lejos))
        for (i in 1..3) drawLine(p.sombra(madera, lejos).copy(alpha = 0.4f), Offset(lx, y0 + (y1 - y0) * i / 4f), Offset(lx + s * fondo, y0 + (y1 - y0) * i / 4f - fondo * 0.2f), (0.004f * k).coerceAtLeast(0.3f))
        // El marco de delante, pintado de azul y gastado, con el hueco.
        drawRect(Brush.verticalGradient(listOf(p.luz(madera, lejos), p.medio(madera, lejos)), y0, y1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
        val hx0 = c.x - w * 0.5f; val hx1 = c.x + w * 0.5f; val hy0 = c.y - h * 0.5f; val hy1 = c.y + h * 0.56f
        val dentro = lerp(p.sombra(lerp(madera, C.BREA, 0.55f), lejos), C.LUMBRE, 0.35f * noche)
        drawRect(dentro, Offset(hx0, hy0), Size(hx1 - hx0, hy1 - hy0))
        // La tabla del santo, un poco metida: el fondo de oro, la aureola, la cabeza sin facciones, la barba, la ropa y el libro.
        val tabla = Rect(hx0 + w * 0.08f, hy0 + h * 0.06f, hx1 - w * 0.08f, hy1 - h * 0.1f)
        val oro = lerp(p.medio(lerp(C.ORO, C.ESPONJA_OSC, 0.3f), lejos), lerp(C.ORO, C.LUMBRE, 0.4f), 0.75f * noche)
        drawRect(Brush.verticalGradient(listOf(lerp(oro, Color.White, 0.12f), oro, lerp(oro, C.MADERA_OSC, 0.25f)), tabla.top, tabla.bottom), tabla.topLeft, tabla.size)
        clipPath(Path().apply { addRect(tabla) }) {
            val cab = Offset(c.x, tabla.top + tabla.height * 0.27f); val rr = tabla.width * 0.13f
            drawCircle(lerp(oro, Color.White, 0.25f), rr * 1.8f, cab)
            drawCircle(mult(oro, C.SOMBRA, 0.5f), rr * 1.8f, cab, style = Stroke(rr * 0.14f))
            drawOval(p.medio(lerp(C.ARENA, C.MADERA, 0.35f), lejos), Offset(cab.x - rr * 0.62f, cab.y - rr * 0.85f), Size(rr * 1.24f, rr * 1.5f))
            drawOval(p.luz(C.BLANCO, lejos), Offset(cab.x - rr * 0.7f, cab.y + rr * 0.15f), Size(rr * 1.4f, rr * 1.25f))                         // la barba blanca
            val ropa = Path().apply { moveTo(cab.x, cab.y + rr * 1.0f); cubicTo(cab.x - tabla.width * 0.3f, cab.y + rr * 1.1f, tabla.left + tabla.width * 0.06f, tabla.bottom - tabla.height * 0.3f, tabla.left + tabla.width * 0.04f, tabla.bottom); lineTo(tabla.right - tabla.width * 0.04f, tabla.bottom); cubicTo(tabla.right - tabla.width * 0.06f, tabla.bottom - tabla.height * 0.3f, cab.x + tabla.width * 0.3f, cab.y + rr * 1.1f, cab.x, cab.y + rr * 1.0f); close() }
            drawPath(ropa, p.medio(lerp(C.ROJO, C.BREA, 0.38f), lejos))
            drawPath(Path().apply { moveTo(cab.x - rr * 0.9f, cab.y + rr * 1.3f); lineTo(cab.x - rr * 0.55f, tabla.bottom); lineTo(cab.x - rr * 0.95f, tabla.bottom); close() }, p.medio(lerp(C.AZUL, C.MAR_HONDO, 0.4f), lejos))
            // El palio blanco que baja por delante, con sus dos cruces pequeñas.
            val pal = Path().apply { moveTo(cab.x - rr * 0.5f, cab.y + rr * 1.5f); lineTo(cab.x + rr * 0.5f, cab.y + rr * 1.5f); lineTo(cab.x + rr * 0.35f, tabla.bottom); lineTo(cab.x - rr * 0.35f, tabla.bottom); close() }
            drawPath(pal, p.luz(lerp(C.BLANCO, C.ARENA, 0.2f), lejos))
            for (t in listOf(0.55f, 0.8f)) { val q = Offset(cab.x, tabla.top + tabla.height * t); drawLine(p.sombra(C.BREA, lejos), q + Offset(-rr * 0.18f, 0f), q + Offset(rr * 0.18f, 0f), rr * 0.08f); drawLine(p.sombra(C.BREA, lejos), q + Offset(0f, -rr * 0.18f), q + Offset(0f, rr * 0.18f), rr * 0.08f) }
            drawRect(p.medio(C.ROJO, lejos), Offset(cab.x - tabla.width * 0.36f, cab.y + tabla.height * 0.3f), Size(tabla.width * 0.2f, tabla.height * 0.17f))   // el libro
            drawLine(oro, Offset(cab.x - tabla.width * 0.36f, cab.y + tabla.height * 0.385f), Offset(cab.x - tabla.width * 0.16f, cab.y + tabla.height * 0.385f), tabla.width * 0.02f)
            // El humo de la lamparilla, que ha ido oscureciendo lo de arriba.
            drawRect(Brush.verticalGradient(listOf(p.sombra(C.BREA, lejos).copy(alpha = 0.4f), Color.Transparent), tabla.top, tabla.top + tabla.height * 0.32f), tabla.topLeft, Size(tabla.width, tabla.height * 0.32f))
        }
        drawRect(p.luz(C.ORO, lejos), tabla.topLeft, tabla.size, style = Stroke(w * 0.035f))
        // La lamparilla: el vaso rojo colgado de sus tres cadenillas, delante de la tabla.
        val vaso = Offset(c.x, c.y + h * 0.4f)
        for (u in listOf(-1f, 0f, 1f)) drawLine(p.sombra(C.LATON, lejos), Offset(c.x + u * w * 0.2f, hy0 + h * 0.02f), vaso + Offset(u * w * 0.1f, -w * 0.08f), w * 0.015f)
        val vasoForma = Path().apply { moveTo(vaso.x - w * 0.13f, vaso.y - w * 0.08f); cubicTo(vaso.x - w * 0.14f, vaso.y + w * 0.12f, vaso.x + w * 0.14f, vaso.y + w * 0.12f, vaso.x + w * 0.13f, vaso.y - w * 0.08f); close() }
        drawPath(vasoForma, lerp(p.medio(C.ROJO, lejos), Color(0xFFFF5A3A), 0.75f * lumbre))
        drawLine(p.luz(C.LATON, lejos), vaso + Offset(-w * 0.14f, -w * 0.08f), vaso + Offset(w * 0.14f, -w * 0.08f), w * 0.03f)
        if (lumbre > 0.02f) {
            resplandor(vaso + Offset(0f, -w * 0.1f), w * (0.8f + 1.7f * p.l.noche), (0.12f + 0.95f * p.l.noche) * lumbre, C.LUMBRE)
            drawOval(Color(0xFFFFE6A0), Offset(vaso.x - w * 0.025f, vaso.y - w * 0.16f), Size(w * 0.05f, w * 0.09f))
        }
        // La puerta de vidrio: su bastidor, el travesaño y el reflejo del cielo (de noche, la lumbre y un brillo de luna).
        val bast = lerp(p.medio(madera, lejos), C.LUMBRE, 0.25f * noche)
        drawRect(bast, Offset(hx0, hy0), Size(hx1 - hx0, hy1 - hy0), style = Stroke(w * 0.07f))
        drawLine(bast, Offset(hx0, c.y + h * 0.08f), Offset(hx1, c.y + h * 0.08f), w * 0.04f)
        val brillo = lerp(lerp(p.l.bajo, Color.White, 0.5f), Color(0xFFE6EEFF), p.l.noche)
        drawPath(poligono(Offset(hx0 + w * 0.08f, hy0 + h * 0.5f), Offset(hx0 + w * 0.45f, hy0 + h * 0.05f), Offset(hx0 + w * 0.58f, hy0 + h * 0.05f), Offset(hx0 + w * 0.2f, hy0 + h * 0.56f)), brillo.copy(alpha = 0.2f + 0.1f * p.r.contraste))
        // El canto de luz del marco y su desgaste.
        drawLine(p.luz(lerp(madera, C.BLANCO, 0.3f), lejos).copy(alpha = 0.7f), Offset(x0, y0), Offset(x0, y1), w * 0.03f)
        drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.4f), Offset(x0, y1), Offset(x1, y1), w * 0.04f)
        // El tejadillo a dos aguas, con su vuelo y la sombra que echa en el marco, y la cruz pequeña de latón.
        drawRect(p.sombra(madera, lejos).copy(alpha = 0.5f), Offset(x0, y0), Size(x1 - x0, h * 0.06f))
        val techo = poligono(Offset(x0 - w * 0.18f, y0 + h * 0.02f), Offset(c.x + s * fondo * 0.5f, y0 - h * 0.34f), Offset(x1 + w * 0.18f + s * fondo, y0 - fondo * 0.1f), Offset(x1 + w * 0.18f, y0 + h * 0.02f))
        drawPath(techo, Brush.horizontalGradient(listOf(if (ld < 0) p.luz(madera, lejos) else p.sombra(madera, lejos), if (ld < 0) p.sombra(madera, lejos) else p.luz(madera, lejos)), x0 - w * 0.2f, x1 + w * 0.2f))
        drawLine(p.luz(madera, lejos).copy(alpha = 0.7f), Offset(x0 - w * 0.18f, y0 + h * 0.02f), Offset(c.x + s * fondo * 0.5f, y0 - h * 0.34f), w * 0.03f)
        val cima = Offset(c.x + s * fondo * 0.5f, y0 - h * 0.34f)
        drawLine(p.luz(C.ORO, lejos), cima, cima + Offset(0f, -h * 0.2f), w * 0.04f)
        drawLine(p.luz(C.ORO, lejos), cima + Offset(-w * 0.07f, -h * 0.13f), cima + Offset(w * 0.07f, -h * 0.13f), w * 0.04f)
        if (luto) {
            val pano = Path().apply { moveTo(x0, y0); lineTo(x1, y0); cubicTo(x1, y0 + h * 0.3f, x1 - w * 0.2f, y0 + h * 0.45f, c.x + w * 0.25f, y0 + h * 0.28f); cubicTo(c.x + w * 0.1f, y0 + h * 0.2f, c.x - w * 0.15f, y0 + h * 0.22f, c.x - w * 0.3f, y0 + h * 0.32f); cubicTo(x0 + w * 0.1f, y0 + h * 0.42f, x0, y0 + h * 0.3f, x0, y0); close() }
            drawPath(pano, p.medio(lerp(C.BREA, C.AZUL, 0.12f), lejos))
        }
        if (velas) for (u in listOf(-0.55f, 0.55f)) {
            val b = Offset(c.x + u * w, y1)
            drawLine(p.luz(lerp(C.BLANCO, C.ESPONJA, 0.3f), lejos), b, b + Offset(0f, -h * 0.35f), w * 0.07f)
            drawOval(Color(0xFFFFE08A), Offset(b.x - w * 0.025f, b.y - h * 0.35f - w * 0.1f), Size(w * 0.05f, w * 0.09f))
            resplandor(b + Offset(0f, -h * 0.38f), w * 0.9f, 0.25f + 0.6f * p.l.noche, C.LUMBRE)
        }
        if (huevos) for (i in 0 until 3) {
            val q = Offset(c.x - w * 0.3f + i * w * 0.28f, y1 + w * 0.03f)
            drawOval(Brush.radialGradient(listOf(p.luz(C.ROJO, lejos), p.sombra(C.ROJO, lejos)), q + Offset(-w * 0.03f, -w * 0.05f), w * 0.12f), Offset(q.x - w * 0.08f, q.y - w * 0.11f), Size(w * 0.16f, w * 0.2f))
        }
    }

    /** Dónde arde la lamparilla de la hornacina de centro [c] y [k] unidades por metro (para su llama en la parte viva y su luz). */
    fun llamaDeLaLamparilla(c: Offset, k: Float): Offset = Offset(c.x, c.y + 0.082f * k)

    // =================================================================
    //  El palo, los ganchos, la jarcia
    // =================================================================

    /**
     * Un palo de [a] (el pie) a [b] (lo alto), de [g0] a [g1] unidades de grueso: redondo, con su filo de luz del lado del
     * sol, la sombra del otro, la veta, los zunchos de hierro en [zunchos] y la mancha de brea y de roce.
     */
    fun DrawScope.palo(p: Pincel, a: Offset, b: Offset, g0: Float, g1: Float, lejos: Float = 0f, zunchos: List<Float> = emptyList(), color: Color = C.MADERA) {
        val ld = lado(p)
        val forma = poligono(Offset(a.x - g0 / 2f, a.y), Offset(b.x - g1 / 2f, b.y), Offset(b.x + g1 / 2f, b.y), Offset(a.x + g0 / 2f, a.y))
        drawPath(forma, Brush.horizontalGradient(0f to (if (ld > 0) p.sombra(color, lejos) else p.medio(color, lejos)), 0.3f to (if (ld > 0) p.medio(color, lejos) else p.luz(color, lejos)), 0.62f to (if (ld > 0) p.luz(color, lejos) else p.medio(color, lejos)), 1f to (if (ld > 0) p.medio(color, lejos) else p.sombra(color, lejos)), startX = minOf(a.x, b.x) - g0 / 2f, endX = maxOf(a.x, b.x) + g0 / 2f))
        if (g0 > 4f) clipPath(forma) {
            val az = Azar((a.x * 31 + b.y).toLong())
            repeat(5) { val u = az.entre(-0.35f, 0.35f); drawLine(p.sombra(color, lejos).copy(alpha = 0.3f), Offset(a.x + u * g0, a.y), Offset(b.x + u * g1, b.y), 0.4f) }
            repeat(4) { val t = az.entre(0.1f, 0.9f); val q = a + (b - a) * t; drawOval(p.sombra(lerp(color, C.BREA, 0.5f), lejos).copy(alpha = 0.3f), Offset(q.x - g0 * 0.3f, q.y - g0 * 0.8f), Size(g0 * 0.6f, g0 * 1.6f)) }
        }
        for (t in zunchos) {
            val q = a + (b - a) * t; val g = g0 + (g1 - g0) * t
            drawLine(p.sombra(C.PLOMO, lejos), Offset(q.x - g * 0.55f, q.y), Offset(q.x + g * 0.55f, q.y), (g * 0.12f).coerceAtLeast(0.6f))
            drawLine(p.luz(C.PLOMO, lejos).copy(alpha = 0.6f), Offset(q.x - g * 0.5f + ld * g * 0.2f, q.y - g * 0.05f), Offset(q.x + ld * g * 0.45f, q.y - g * 0.05f), (g * 0.05f).coerceAtLeast(0.3f))
        }
    }

    /** Un gancho de hierro clavado en el palo en [a] que sale hacia [lado]: su vástago y su punta vuelta hacia arriba. */
    fun DrawScope.gancho(p: Pincel, a: Offset, largo: Float, lado: Float, lejos: Float = 0f) {
        val b = a + Offset(lado * largo, largo * 0.12f)
        drawLine(p.sombra(C.PLOMO, lejos), a, b, largo * 0.14f, StrokeCap.Round)
        drawPath(Path().apply { moveTo(b); cubicTo(b + Offset(lado * largo * 0.25f, 0f), b + Offset(lado * largo * 0.3f, -largo * 0.3f), b + Offset(lado * largo * 0.12f, -largo * 0.38f)) }, p.sombra(C.PLOMO, lejos), style = Stroke(largo * 0.12f, cap = StrokeCap.Round))
        drawLine(p.luz(C.PLOMO, lejos).copy(alpha = 0.5f), a + Offset(0f, -largo * 0.04f), b + Offset(0f, -largo * 0.04f), largo * 0.04f)
    }

    // =================================================================
    //  El barco esponjero, lejos
    // =================================================================

    /**
     * Un barco esponjero lejano, fondeado: «una goleta pequeña, de dos palos, pintada de blanco y azul», de perfil (el
     * casco de [largo] unidades con la proa hacia [mira] = ±1), con su ojo de la proa apenas, la regala azul, la caseta,
     * los dos palos con las velas aferradas, el estay y las ristras colgadas a secar ([ristras], de 0 a 1), el bote atrás
     * y, de noche, su farol ([farol]). [pie] es el centro de su línea de flotación. El reflejo lo pone el cuadro.
     */
    fun DrawScope.barcoLejano(p: Pincel, pie: Offset, largo: Float, mira: Float, semilla: Long, lejos: Float, ristras: Float = 0.5f, farol: Float = 0f, luto: Boolean = false, empavesado: Boolean = false) {
        val az = Azar(semilla)
        val ld = lado(p)
        val L = largo; val alto = L * 0.12f
        fun x(u: Float) = pie.x + mira * u * L                                      // u de −0.5 (popa) a 0.5 (proa)
        val casco = Path().apply {
            moveTo(x(-0.5f), pie.y - alto)
            cubicTo(x(-0.2f), pie.y - alto * 0.86f, x(0.25f), pie.y - alto * 0.9f, x(0.56f), pie.y - alto * 1.18f)   // el arrufo, con la proa alta
            cubicTo(x(0.5f), pie.y - alto * 0.4f, x(0.4f), pie.y + alto * 0.1f, x(0.3f), pie.y + alto * 0.12f)
            lineTo(x(-0.42f), pie.y + alto * 0.1f)
            cubicTo(x(-0.5f), pie.y, x(-0.52f), pie.y - alto * 0.5f, x(-0.5f), pie.y - alto); close()
        }
        val frente = if (mira * ld < 0) 0.25f else -0.15f
        drawPath(casco, p.por(C.BLANCO, frente, lejos))
        clipPath(casco) {
            drawRect(p.por(C.AZUL, frente, lejos), Offset(pie.x - L, pie.y - alto * 1.3f), Size(L * 2f, alto * 0.34f))
            drawRect(p.medio(lerp(C.MAR_HONDO, C.BREA, 0.3f), lejos), Offset(pie.x - L, pie.y - alto * 0.05f), Size(L * 2f, alto * 0.3f))
            drawRect(p.sombra(C.BLANCO, lejos).copy(alpha = 0.5f), Offset(pie.x - L, pie.y - alto * 0.5f), Size(L * 2f, alto * 0.5f))
        }
        if (L > 40f) drawCircle(p.medio(C.BREA, lejos), alto * 0.12f, Offset(x(0.47f), pie.y - alto * 0.7f))   // el ojo de la proa
        // La caseta y la bomba en cubierta.
        drawRect(p.por(C.MADERA_GRIS, frente, lejos), Offset(minOf(x(-0.3f), x(-0.12f)), pie.y - alto * 1.6f), Size(L * 0.18f, alto * 0.65f))
        // Los dos palos (el mayor, a popa, más alto) con las velas aferradas.
        val palos = listOf(-0.18f to 1.0f, 0.22f to 0.86f)
        val mastil = L * 0.75f
        for ((u, k) in palos) {
            val a = Offset(x(u), pie.y - alto); val b = Offset(x(u) - mira * L * 0.02f, pie.y - alto - mastil * k)
            drawLine(p.medio(C.MADERA, lejos), a, b, (L * 0.012f).coerceAtLeast(0.5f))
            // La vela aferrada sobre su botavara.
            val bv = Offset(x(u - 0.3f), pie.y - alto - L * 0.1f)
            drawLine(p.sombra(C.MADERA, lejos), Offset(x(u), pie.y - alto - L * 0.13f), bv, (L * 0.008f).coerceAtLeast(0.4f))
            drawLine(p.por(C.LONA, 0.3f, lejos), Offset(x(u), pie.y - alto - L * 0.15f), bv + Offset(0f, -L * 0.02f), (L * 0.025f).coerceAtLeast(0.6f), StrokeCap.Round)
            if (luto && u < 0f) drawRect(p.medio(C.BREA, lejos), Offset(minOf(b.x, b.x + mira * L * 0.08f), b.y + mastil * 0.45f), Size(L * 0.08f, L * 0.03f))
        }
        // La jarcia: el estay de proa y los obenques.
        val tope = Offset(x(0.22f) - mira * L * 0.02f, pie.y - alto - mastil * 0.86f)
        drawLine(p.sombra(C.CANAMO, lejos).copy(alpha = 0.7f), tope, Offset(x(0.68f), pie.y - alto * 1.4f), 0.35f)
        drawLine(p.sombra(C.CANAMO, lejos).copy(alpha = 0.6f), Offset(x(-0.18f), pie.y - alto - mastil), tope, 0.3f)
        // Las ristras de esponjas, colgadas a secar del cabo entre los palos.
        val nR = (ristras * 9f).toInt()
        for (i in 0 until nR) {
            val t = (i + 0.5f) / maxOf(nR, 1)
            val a = Offset(x(-0.18f + 0.4f * t), pie.y - alto - mastil * (0.55f - 0.1f * t) + L * 0.03f * sin(t * PI.toFloat()))
            val lgo = L * az.entre(0.12f, 0.24f)
            drawLine(lerp(p.medio(C.ESPONJA, lejos), p.sombra(C.ESPONJA, lejos), az.real()), a, a + Offset(0f, lgo), (L * 0.02f).coerceAtLeast(0.6f), StrokeCap.Round)
        }
        if (empavesado) {
            val pts = combada(Offset(x(0.68f), pie.y - alto * 1.4f), tope, L * 0.03f, 8) + combada(tope, Offset(x(-0.18f), pie.y - alto - mastil), L * 0.02f, 6) + combada(Offset(x(-0.18f), pie.y - alto - mastil), Offset(x(-0.5f), pie.y - alto), L * 0.03f, 8)
            pts.forEachIndexed { i, q -> drawRect(if (i % 2 == 0) p.luz(C.BLANCO, lejos) else p.medio(C.AZUL, lejos), Offset(q.x - L * 0.008f, q.y), Size(L * 0.016f, L * 0.025f)) }
        }
        // El bote, atado detrás.
        drawOval(p.por(C.BLANCO, frente, lejos), Offset(x(-0.66f) - L * 0.06f, pie.y - alto * 0.3f), Size(L * 0.12f, alto * 0.45f))
        if (farol > 0.02f) {
            val f = Offset(x(-0.18f), pie.y - alto - mastil * 0.3f)
            resplandor(f, L * 0.18f, farol, C.LUMBRE)
            drawCircle(Color(0xFFFFE2A0).copy(alpha = farol), (L * 0.012f).coerceAtLeast(0.7f), f)
        }
    }
}
