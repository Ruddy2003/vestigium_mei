package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El Sena: el agua (o el hielo de enero de 1789), los reflejos de la otra orilla y del puente, las barcazas, el
 * barco-lavadero de enfrente, el bote amarrado a la popa y los postes de amarre.
 */
internal object RioRevolucion {
    private val C = ColorRevolucion
    private val E = EscenaRevolucion
    private const val WL = EscenaRevolucion.ORILLA
    private const val BORDA = EscenaRevolucion.BORDA
    private val ZONA = Rect(0f, WL, ANCHO, BORDA + 2f)

    /** La altura en el cuadro de un punto del agua a la distancia [d] (0, la otra orilla; 1, la borda). */
    internal fun y(d: Float) = WL + (BORDA - WL) * d

    /** Cuánto se agranda lo que flota a esa distancia. */
    internal fun talla(d: Float) = 0.3f + 0.7f * d

    private fun t(m: Momento, l: Luces, c: Color, lejos: Float): Color = l.tono(c, E.perdido(m, lejos))

    /** El agua del río, que cambia de lejos a cerca; con la crecida baja parda y revuelta. En la helada, el hielo. */
    fun DrawScope.rio(m: Momento, l: Luces) {
        if (E.helada(m)) { hielo(m, l); return }
        val crecida = E.crecida(m)
        agua(l, ZONA, m.azar("agua"), if (crecida) lerp(C.SENA, C.MADERA, 0.5f) else C.SENA, viento = (m.viento + (if (crecida) 0.45f else 0f)).coerceAtMost(1f),
            hondo = if (crecida) lerp(C.SENA_HONDO, C.MADERA_OSC, 0.5f) else C.SENA_HONDO, orilla = if (crecida) lerp(C.SENA_ORILLA, C.MADERA, 0.4f) else C.SENA_ORILLA)
    }

    /** El Sena helado de orilla a orilla: placas, grietas, crestas, la nieve encima, las veredas de los que cruzan y los agujeros de los aguadores. */
    private fun DrawScope.hielo(m: Momento, l: Luces) {
        val az = Azar(Semilla.de("revolucion", "hielo"))
        val lejos = l.tono(lerp(C.HIELO, C.PIZARRA, 0.2f), 0.3f); val cerca = l.tono(lerp(C.HIELO, Color.White, 0.55f))
        drawRect(Brush.verticalGradient(0f to lejos, 0.45f to l.tono(C.HIELO, 0.12f), 1f to cerca, startY = WL, endY = BORDA + 2f), ZONA.topLeft, ZONA.size)
        repeat(34) { i -> val d = az.real(); mancha(Offset(az.entre(0f, ANCHO), y(d)), az.entre(16f, 54f) * talla(d) * 1.5f, az.entre(1.2f, 3.6f) * talla(d) * 1.8f, l.tono(if (i % 3 == 0) lerp(C.HIELO, C.ANIL, 0.3f) else Color.White), 0.3f) }
        // Los ventisqueros: lomos de nieve largos, con su sombra azul del lado contrario a la luz.
        val lado = if (l.solX > 0.5f) -1f else 1f
        repeat(18) { val d = az.entre(0.06f, 0.98f); val s = talla(d); val p = Offset(az.entre(-20f, ANCHO), y(d)); val w = az.entre(30f, 84f) * s; val h = az.entre(2.2f, 4.6f) * s
            drawPath(Path().apply { moveTo(p.x + lado * 3f * s, p.y + 0.8f * s); cubicTo(p.x + w * 0.3f, p.y - h * 0.4f, p.x + w * 0.7f, p.y - h * 0.4f, p.x + w + lado * 3f * s, p.y + 0.8f * s); cubicTo(p.x + w * 0.7f, p.y + h * 0.8f, p.x + w * 0.3f, p.y + h * 0.8f, p.x + lado * 3f * s, p.y + 0.8f * s); close() }, l.tono(lerp(C.ANIL, C.SOMBRA, 0.4f)).copy(alpha = 0.28f))
            drawPath(Path().apply { moveTo(p.x, p.y); cubicTo(p.x + w * 0.3f, p.y - h, p.x + w * 0.7f, p.y - h, p.x + w, p.y); cubicTo(p.x + w * 0.7f, p.y + h * 0.3f, p.x + w * 0.3f, p.y + h * 0.3f, p.x, p.y); close() }, l.tono(Color.White)) }
        // Las grietas: líneas quebradas, más finas lejos.
        val grieta = l.tono(lerp(C.ANIL, C.PIZARRA_OSC, 0.4f))
        repeat(24) {
            val d = az.real(); var p = Offset(az.entre(0f, ANCHO), y(d)); val s = talla(d)
            val trazo = Path().apply { moveTo(p.x, p.y); repeat(4) { p += Offset(az.entre(6f, 22f) * s * (if (az.entero(2) == 0) 1f else -1f), az.entre(-2.4f, 2.4f) * s); lineTo(p.x, p.y) } }
            drawPath(trazo, grieta.copy(alpha = 0.45f + 0.35f * d), style = Stroke(0.35f + 0.7f * s, cap = StrokeCap.Round))
        }
        // Las crestas donde las placas se montan: un filo blanco con su sombra.
        repeat(16) { val d = az.entre(0.1f, 1f); val p = Offset(az.entre(0f, ANCHO), y(d)); val s = talla(d); val w = az.entre(8f, 26f) * s
            drawLine(l.tono(lerp(C.ANIL, C.SOMBRA, 0.5f)).copy(alpha = 0.4f), p + Offset(0f, 1.1f * s), p + Offset(w, 1.1f * s + az.entre(-1f, 1f)), 1.2f * s, StrokeCap.Round)
            drawLine(l.tono(Color.White), p, p + Offset(w, az.entre(-1f, 1f)), 0.9f * s, StrokeCap.Round) }
        // Cerca del barco, las placas que el hielo monta unas sobre otras: cada una con su canto azul y su sombra.
        val pl = Azar(Semilla.de("revolucion", "placas"))
        repeat(10) {
            val d = pl.entre(0.56f, 0.9f); val s = talla(d); val o = Offset(pl.entre(56f, ANCHO - 30f), y(d)); val w = pl.entre(22f, 46f) * s; val alza = pl.entre(2.4f, 5f) * s; val canto = pl.entre(1.4f, 2.8f) * s
            val arriba = poligono(o, o + Offset(w * 0.34f, -alza), o + Offset(w, -alza * 0.5f), o + Offset(w * 0.76f, alza * 0.25f))
            drawPath(Path().apply { addPath(arriba, Offset(lado * 3.4f * s, canto + 1.2f * s)) }, l.tono(lerp(C.ANIL, C.SOMBRA, 0.4f)).copy(alpha = 0.3f))
            drawPath(Path().apply { addPath(arriba, Offset(0f, canto)) }, l.tono(lerp(C.HIELO, C.ANIL, 0.5f)))
            drawPath(arriba, l.tono(Color.White))
            drawLine(l.tono(lerp(C.HIELO, C.ANIL, 0.35f)).copy(alpha = 0.7f), o + Offset(w * 0.3f, -alza * 0.55f), o + Offset(w * 0.72f, -alza * 0.2f), 0.4f * s + 0.2f)
        }
        // Y las grietas de cerca, anchas, con su ramal.
        repeat(9) {
            val d = pl.entre(0.55f, 0.97f); val s = talla(d); val nudos = ArrayList<Offset>(); nudos += Offset(pl.entre(40f, ANCHO), y(d))
            repeat(4) { nudos += nudos.last() + Offset(pl.entre(10f, 26f) * s, pl.entre(-2.6f, 2.6f) * s) }
            val medio = nudos[2]; val trazo = Path().apply { moveTo(nudos[0].x, nudos[0].y); for (q in nudos.drop(1)) lineTo(q.x, q.y) }
            drawPath(trazo, grieta.copy(alpha = 0.75f), style = Stroke(0.5f + 0.8f * s, cap = StrokeCap.Round))
            drawPath(trazo, l.tono(Color.White).copy(alpha = 0.6f), style = Stroke(0.3f, cap = StrokeCap.Round))
            drawLine(grieta.copy(alpha = 0.65f), medio, medio + Offset(pl.entre(6f, 14f) * s, pl.entre(3f, 6f) * s), 0.4f + 0.5f * s, StrokeCap.Round)
        }
        // Las veredas: «la gente cruza a pie por encima». Dos sendas pisadas, del puerto de enfrente a este lado; cerca, se
        // cuenta cada huella. Y otra hilera de huellas que cruza el río de lado a lado.
        val huella = l.tono(lerp(C.ANIL, C.PIZARRA_OSC, 0.45f))
        for (fin in listOf(Offset(112f, BORDA - 4f), Offset(338f, BORDA - 8f))) {
            val pts = ArrayList<Offset>(); val o = Offset(180f, WL + 1.5f)
            for (i in 0..46) { val k = i / 46f; val q = o + (fin - o) * k + Offset(8f * kotlin.math.sin(k * 7f) * k, 0f); pts += q + Offset(az.entre(-1f, 1f) * (0.4f + 2f * k), az.entre(-0.4f, 0.4f)) }
            drawPoints(pts, PointMode.Polygon, l.tono(lerp(C.HIELO, C.PIZARRA, 0.5f)).copy(alpha = 0.5f), 1.6f, StrokeCap.Round)
            drawPoints(pts, PointMode.Points, l.tono(lerp(C.PIZARRA, C.BREA, 0.3f)).copy(alpha = 0.55f), 1.1f, StrokeCap.Round)
            for (i in 22..46) { val k = i / 46f; val q = pts[i] + Offset((if (i % 2 == 0) -1f else 1f) * (0.8f + 1.8f * k), 0f); drawOval(huella.copy(alpha = 0.8f), q + Offset(-0.5f - 0.7f * k, -0.4f - 0.5f * k), Size(1f + 1.4f * k, 0.8f + 1f * k)) }
        }
        for (i in 0..44) { val k = i / 44f; val q = Offset(30f + 330f * k, y(0.7f + 0.1f * k) + 3f * kotlin.math.sin(k * 9f)); drawOval(huella.copy(alpha = 0.75f), q + Offset(0f, if (i % 2 == 0) -1.5f else 1.1f), Size(2.6f, 1.5f)) }
        // Los agujeros de los aguadores, con su cubo y su barra.
        for ((p, s) in listOf(Offset(226f, y(0.22f)) to 0.6f, Offset(64f, y(0.52f)) to 0.85f)) {
            drawOval(l.tono(Color.White), Offset(p.x - 8f * s, p.y - 2.6f * s), Size(16f * s, 5.2f * s)); drawOval(l.tono(C.SENA_HONDO), Offset(p.x - 6f * s, p.y - 1.7f * s), Size(12f * s, 3.4f * s))
            drawRoundRect(l.tono(C.MADERA), Offset(p.x + 7f * s, p.y - 5f * s), Size(4.4f * s, 4.6f * s), androidx.compose.ui.geometry.CornerRadius(0.8f * s)); drawLine(l.tono(C.HIERRO), Offset(p.x - 9f * s, p.y + 1f * s), Offset(p.x - 5f * s, p.y - 9f * s), 0.7f * s, StrokeCap.Round)
        }
        // De noche manda la luna: «solo brilla el río helado, blanco bajo la luna». La claridad se suma a todo el hielo,
        // más cerca que lejos; debajo de la luna, su camino.
        if (l.noche > 0.05f) {
            drawRect(Brush.verticalGradient(0f to Color(0xFFBCCBEE).copy(alpha = 0.24f * l.noche), 1f to Color(0xFFD4E0FF).copy(alpha = 0.4f * l.noche), startY = WL, endY = BORDA), ZONA.topLeft, ZONA.size, blendMode = BlendMode.Plus)
            val luna = E.luna(l)
            if (luna > 0.05f) { mancha(Offset(l.lunaX * ANCHO, y(0.5f)), 74f, 50f, Color(0xFFE6EEFF), 0.3f * luna); estela(l, Rect(0f, WL, ANCHO, BORDA), Azar(Semilla.de("revolucion", "luna en el hielo"))) }
        }
    }

    /**
     * Hacia dónde tumba las sombras sobre el hielo la luz de ahora, por cada unidad de alto, y con cuánta fuerza: de día,
     * el sol; de noche, la luna (o la claridad del cielo, si no ha salido).
     */
    private fun tumba(l: Luces, r: Rayo): Pair<Offset, Float>? = when {
        r.fuerza > 0.05f -> Offset((if (r.lat > 0f) -1f else 1f) * r.cot.coerceIn(0.3f, 2.4f), 0.34f) to (0.5f * r.fuerza).coerceIn(0.1f, 0.4f)
        l.noche > 0.5f -> Offset((if (l.lunaX > 0.5f) -1f else 1f) * 1.25f, 0.42f) to 0.46f * l.noche * (0.55f + 0.45f * E.luna(l))
        else -> null
    }

    /** La sombra azul de algo que pisa el hielo entre [x0] y [x1], a la altura [pie] del cuadro, y que mide [alto]. */
    private fun DrawScope.sombraEnHielo(l: Luces, r: Rayo, x0: Float, x1: Float, pie: Float, alto: Float) {
        val (d, fuerza) = tumba(l, r) ?: return
        drawPath(poligono(Offset(x0, pie), Offset(x1, pie), Offset(x1 + d.x * alto, pie + d.y * alto), Offset(x0 + d.x * alto, pie + d.y * alto)), lerp(C.ANIL, C.SOMBRA, 0.5f).copy(alpha = fuerza), blendMode = BlendMode.Multiply)
    }

    /** Una barcaza del Sena: el casco embreado, largo y bajo, con su carga (0, leña; 1, heno; 2, toneles), su caseta y su remo de popa. */
    private fun DrawScope.barcaza(m: Momento, l: Luces, r: Rayo, centro: Offset, largo: Float, carga: Int, lj: Float, nevada: Boolean) {
        val s = largo / 74f; val x0 = centro.x - largo / 2f; val x1 = centro.x + largo / 2f; val y = centro.y
        val casco = Path().apply { moveTo(x0, y - 3.6f * s); lineTo(x1 - 7f * s, y - 3.6f * s); cubicTo(x1 - 3f * s, y - 3.8f * s, x1 - 1f * s, y - 4.6f * s, x1, y - 5.6f * s); lineTo(x1 - 3f * s, y + 1.4f * s); lineTo(x0 + 4f * s, y + 1.4f * s); close() }
        drawPath(casco, t(m, l, C.BREA, lj)); clipPath(casco) { drawRect(t(m, l, lerp(C.BREA, C.MADERA, 0.4f), lj), Offset(x0, y - 3.8f * s), Size(largo, 1.1f * s)); for (i in 0..9) drawLine(t(m, l, C.MADERA_OSC, lj).copy(alpha = 0.5f), Offset(x0 + i * largo / 9f, y - 3f * s), Offset(x0 + i * largo / 9f + s, y + 1.4f * s), 0.3f) }
        val der = r.lat > 0f; val k = r.contraste * r.dia
        when (carga) {
            0 -> { // la leña, en rimeros
                for (i in 0..3) { val xa = x0 + 8f * s + i * 12f * s; drawRect(t(m, l, C.MADERA, lj), Offset(xa, y - 9.6f * s + (i % 2) * 1.4f * s), Size(11f * s, 6.2f * s - (i % 2) * 1.4f * s)) ; val pts = ArrayList<Offset>(); for (a in 0..5) for (b in 0..2) pts += Offset(xa + 1.2f * s + a * 1.7f * s, y - 4.6f * s - b * 1.7f * s); drawPoints(pts, PointMode.Points, t(m, l, lerp(C.OCRE, C.CAL, 0.3f), lj), 1.2f * s, StrokeCap.Round) }
            }
            1 -> { // el heno, un monte dorado con su lona
                val monte = Path().apply { moveTo(x0 + 6f * s, y - 3.6f * s); cubicTo(x0 + 8f * s, y - 13f * s, x0 + 24f * s, y - 15f * s, x0 + 34f * s, y - 13.6f * s); cubicTo(x0 + 46f * s, y - 12f * s, x0 + 52f * s, y - 9f * s, x0 + 54f * s, y - 3.6f * s); close() }
                drawPath(monte, Brush.horizontalGradient(if (der) listOf(t(m, l, mult(C.OCRE, r.color, 0.6f * k), lj), t(m, l, C.OCRE, lj), t(m, l, lerp(C.PAJA, Color.White, 0.2f * k), lj)) else listOf(t(m, l, lerp(C.PAJA, Color.White, 0.2f * k), lj), t(m, l, C.OCRE, lj), t(m, l, mult(C.OCRE, r.color, 0.6f * k), lj)), x0 + 6f * s, x0 + 54f * s))
                clipPath(monte) { val az = Azar(Semilla.de("revolucion", "heno")); repeat(30) { val p = Offset(x0 + az.entre(8f, 52f) * s, y - az.entre(4f, 14f) * s); drawLine(t(m, l, C.MADERA, lj).copy(alpha = 0.4f), p, p + Offset(az.entre(-2f, 2f) * s, 2.2f * s), 0.3f) } }
                drawLine(t(m, l, C.MADERA_OSC, lj), Offset(x0 + 12f * s, y - 12.6f * s), Offset(x0 + 13f * s, y - 3.6f * s), 0.4f); drawLine(t(m, l, C.MADERA_OSC, lj), Offset(x0 + 44f * s, y - 11.4f * s), Offset(x0 + 45f * s, y - 3.6f * s), 0.4f)
            }
            else -> for (i in 0..6) { val xa = x0 + 7f * s + i * 6.6f * s; drawOval(t(m, l, C.MADERA, lj), Offset(xa, y - 9f * s), Size(6f * s, 5.8f * s)); drawOval(t(m, l, C.MADERA_OSC, lj), Offset(xa + 1.4f * s, y - 7.8f * s), Size(3.2f * s, 3.4f * s), style = Stroke(0.4f * s)); drawOval(t(m, l, lerp(C.MADERA, Color(0xFFFFF0D0), 0.3f * k), lj), Offset(xa + (if (der) 3.6f else 0.6f) * s, y - 8.6f * s), Size(1.6f * s, 2f * s)) }
        }
        // La caseta de popa, el remo largo del timón y el palo abatido.
        drawRect(t(m, l, C.MADERA, lj), Offset(x0 + 1.6f * s, y - 9.4f * s), Size(8.4f * s, 5.8f * s)); drawPath(poligono(Offset(x0 + 0.6f * s, y - 9.2f * s), Offset(x0 + 11f * s, y - 9.2f * s), Offset(x0 + 9f * s, y - 11.6f * s), Offset(x0 + 2.4f * s, y - 11.6f * s)), t(m, l, C.PIZARRA_OSC, lj))
        drawRect(t(m, l, C.BREA, lj), Offset(x0 + 4f * s, y - 8f * s), Size(2.4f * s, 3f * s))
        drawLine(t(m, l, C.MADERA, lj), Offset(x0 + 4f * s, y - 7f * s), Offset(x0 - 12f * s, y + 0.6f * s), 0.7f * s, StrokeCap.Round)
        drawLine(t(m, l, C.MADERA_OSC, lj), Offset(x1 - 16f * s, y - 4f * s), Offset(x1 - 4f * s, y - 12f * s), 0.6f * s, StrokeCap.Round)
        if (nevada) { drawRect(t(m, l, C.CAL, lj - 0.1f), Offset(x0 + 6f * s, y - 11.6f * s), Size(largo - 20f * s, 1.6f * s)); drawRect(t(m, l, C.CAL, lj - 0.1f), Offset(x0 + 0.6f * s, y - 12.2f * s), Size(10f * s, 1.2f * s)) }
    }

    /** El barco-lavadero de enfrente: la casa de madera de dos pisos, con el secadero arriba y su ropa. */
    private fun DrawScope.lavaderoLejano(m: Momento, l: Luces, r: Rayo) {
        val lj = 0.3f; val x0 = 204f; val x1 = 243f; val helada = E.helada(m)
        drawRect(t(m, l, C.BREA, lj), Offset(x0 - 2f, WL - 1.2f), Size(x1 - x0 + 4f, 2.4f))
        drawRect(t(m, l, C.MADERA_OSC, lj), Offset(x0, WL - 6.4f), Size(x1 - x0, 5.4f))
        for (i in 0..7) drawRect(t(m, l, C.BREA, lj).copy(alpha = 0.75f), Offset(x0 + 1.4f + i * 4.8f, WL - 5.4f), Size(3.2f, 3.2f))                 // los puestos, abiertos al agua
        drawRect(t(m, l, C.MADERA, lj), Offset(x0 - 0.6f, WL - 7.2f), Size(x1 - x0 + 1.2f, 0.9f))
        for (i in 0..5) drawLine(t(m, l, C.MADERA_OSC, lj), Offset(x0 + 0.6f + i * (x1 - x0 - 1.2f) / 5f, WL - 7.2f), Offset(x0 + 0.6f + i * (x1 - x0 - 1.2f) / 5f, WL - 13.4f), 0.5f)
        if (!helada && E.ropaTendida(m) > 0.2f) { val az = m.azar("lavadero"); var x = x0 + 1.4f; while (x < x1 - 2f) { val w = az.entre(1.6f, 3.4f); drawRect(t(m, l, if (az.entero(5) == 0) C.ANIL else C.CAL, lj - 0.06f), Offset(x, WL - 12.4f), Size(w, az.entre(2.6f, 4.6f))); x += w + az.entre(0.4f, 1.6f) } }
        drawPath(poligono(Offset(x0 - 1.4f, WL - 13.2f), Offset(x1 + 1.4f, WL - 13.2f), Offset(x1 - 1f, WL - 15.4f), Offset(x0 + 1f, WL - 15.4f)), t(m, l, if (E.nevado(m)) C.CAL else C.PIZARRA_OSC, lj))
    }

    /** Un poste de amarre hincado en el río, con su zuncho, su lado de luz y su cabo. */
    private fun DrawScope.poste(m: Momento, l: Luces, r: Rayo, x: Float, arriba: Float, agua: Float) {
        val w = 3.4f; val der = r.lat > 0f; val k = r.contraste * r.dia
        drawRect(t(m, l, C.MADERA_OSC, 0.06f), Offset(x - w / 2f, arriba), Size(w, agua - arriba))
        drawRect(t(m, l, lerp(C.MADERA, Color(0xFFFFF0D0), 0.25f * k), 0.06f), Offset(if (der) x + 0.3f else x - w / 2f, arriba), Size(w / 2f - 0.3f, agua - arriba))
        drawRect(t(m, l, lerp(C.HOJA, C.BREA, 0.5f), 0.06f).copy(alpha = 0.7f), Offset(x - w / 2f, agua - 4f), Size(w, 4f))
        drawOval(t(m, l, lerp(C.MADERA, C.CAL, 0.3f), 0.04f), Offset(x - w / 2f, arriba - 0.9f), Size(w, 1.8f)); drawRect(t(m, l, C.HIERRO, 0.06f), Offset(x - w / 2f - 0.2f, arriba + 2.6f), Size(w + 0.4f, 1f))
        if (E.nevado(m)) drawOval(t(m, l, C.CAL, 0f), Offset(x - w / 2f - 0.3f, arriba - 1.6f), Size(w + 0.6f, 2.2f))
    }

    /** El bote amarrado a la popa, visto un poco desde arriba: el casco embreado, las bancadas y los dos remos dentro. */
    private fun DrawScope.bote(m: Momento, l: Luces, r: Rayo) {
        val k = r.contraste * r.dia; val der = r.lat > 0f
        val fuera = Path().apply { moveTo(112f, 192f); cubicTo(126f, 186f, 164f, 185.6f, 181f, 190f); cubicTo(168f, 200.4f, 130f, 200.8f, 112f, 192f); close() }
        val dentro = Path().apply { moveTo(118f, 192f); cubicTo(130f, 188f, 162f, 187.8f, 175f, 190.4f); cubicTo(164f, 196.4f, 134f, 196.8f, 118f, 192f); close() }
        if (!E.helada(m)) { reflejoRoto(116f, 177f, 197.4f, 8f, t(m, l, C.BREA, 0f), 0.36f, Azar(Semilla.de("revolucion", "bote"))); with(E) { sombraDe(r, Path().apply { addPath(fuera, Offset(if (der) -5f else 5f, 3f)) }, 0.4f) } }
        drawPath(fuera, Brush.verticalGradient(0f to t(m, l, lerp(C.BREA, C.MADERA, 0.35f), 0f), 1f to t(m, l, C.BREA, 0f), startY = 186f, endY = 200f))
        drawPath(dentro, t(m, l, if (E.nevado(m)) C.CAL else lerp(C.MADERA, C.MADERA_OSC, 0.4f), 0f))
        clipPath(dentro) { drawRect(t(m, l, mult(C.MADERA, r.color, 0.6f), 0f).copy(alpha = 0.5f * (0.4f + 0.6f * k)), Offset(if (der) 146f else 112f, 185f), Size(36f, 16f)) }
        for (x in listOf(131f, 146f, 161f)) drawLine(t(m, l, lerp(C.MADERA, C.CAL, 0.2f), 0f), Offset(x, 188.8f), Offset(x + 0.8f, 195.6f), 1.8f)
        drawLine(t(m, l, C.MADERA, 0f), Offset(124f, 192.2f), Offset(171f, 189.6f), 0.9f, StrokeCap.Round); drawLine(t(m, l, C.MADERA, 0f), Offset(126f, 193.6f), Offset(172f, 191.6f), 0.9f, StrokeCap.Round)
        drawPath(fuera, t(m, l, lerp(C.MADERA, C.CAL, 0.25f), 0f), style = Stroke(0.6f))
        // El cabo que lo ata al poste.
        drawPath(Path().apply { moveTo(180f, 190.4f); cubicTo(186f, 193f, 191f, 191f, 196f, 186f) }, t(m, l, lerp(C.PAJA, C.MADERA, 0.4f), 0f), style = Stroke(0.6f, cap = StrokeCap.Round))
    }

    /** Lo que va sobre el agua, después de pintada la otra orilla: reflejos, la calle de luz, sombras y todo lo que flota. */
    fun DrawScope.sobreElRio(m: Momento, l: Luces, r: Rayo) {
        val Ci = CiudadRevolucion
        val helada = E.helada(m)
        val az = m.azar("reflejos")
        if (!helada) {
            // El reflejo roto de cada casa, del caserón, de la iglesia y, más abajo y más tenue, de las torres.
            for (c in Ci.CASAS) reflejoRoto(c.x0, c.x1, WL + 0.3f, (c.base - c.top) * 0.6f, t(m, l, lerp(c.muro, C.SENA_HONDO, 0.35f), 0.3f), 0.4f, az)
            reflejoRoto(202f, 244f, WL + 0.3f, 26f, t(m, l, lerp(C.PIEDRA, C.SENA_HONDO, 0.3f), 0.3f), 0.42f, az)
            reflejoRoto(170f, 194f, WL + 0.3f, 22f, t(m, l, lerp(C.PIEDRA, C.SENA_HONDO, 0.3f), 0.3f), 0.36f, az)
            reflejoRoto(148f, 272f, WL + 0.2f, 5f, t(m, l, C.PIEDRA_OSC, 0.3f), 0.4f, az)
            for (x in Ci.TORRES) reflejoRoto(x, x + Ci.ANCHO_TORRE, WL + 12f, 44f, t(m, l, lerp(C.PIEDRA, C.SENA, 0.3f), 0.3f), 0.26f, az, 1.2f)
            reflejoRoto(46f, 272f, WL + 0.2f, 3.4f, t(m, l, C.SENA_HONDO, 0.2f), 0.5f, az, 0.8f)                                        // la sombra del pie de las casas
            // El puente: el reflejo de cada pila y su sombra tendida en el agua, del lado contrario al sol.
            for (i in 0..4) { val ua = Ci.OJOS[i].second; val ub = Ci.OJOS[i + 1].first; val um = (ua + ub) / 2f; reflejoRoto(Ci.px(ua) - 2f * Ci.esc(um), Ci.px(ub) + 0.5f, Ci.aguaDelPuente(um) + 0.6f * Ci.esc(um), 9f * Ci.esc(um), t(m, l, lerp(C.PIEDRA, C.SENA_HONDO, 0.35f), 0.2f), 0.4f, az, 0.8f + 0.5f * um) }
            val lado = if (r.lat > 0f) -1f else 1f
            val sombra = Path().apply { moveTo(Ci.px(0f), Ci.aguaDelPuente(0f)); lineTo(Ci.px(1.06f), Ci.aguaDelPuente(1.06f)); lineTo(Ci.px(1.06f) + lado * 7f * Ci.esc(1.06f), Ci.aguaDelPuente(1.06f) + 3.6f * Ci.esc(1.06f)); lineTo(Ci.px(0f) + lado * 7f, Ci.aguaDelPuente(0f) + 3.6f); close() }
            with(E) { sombraDe(r, sombra, 0.5f) }
            reflejoRoto(296f, 326f, 138f, 12f, t(m, l, lerp(C.MADERA, C.SENA_HONDO, 0.4f), 0.2f), 0.4f, az)
            // Las luces de la noche, quietas en el agua (lo que tiembla va en la parte viva).
            if (E.faroles(m)) { for (x in listOf(153.4f, 200.6f, 240f)) reflejoRoto(x - 1.2f, x + 1.2f, WL + 0.6f, 15f, C.ACEITE, 0.55f * l.noche, az); reflejoRoto(Ci.BOMBA.x - 2f, Ci.BOMBA.x + 2f, 139f, 18f, C.ACEITE, 0.5f * l.noche, az) }
            // El río devuelve cada luz: cada hilera de ventanas encendidas, cada farol del puente y el farol de popa.
            if (l.noche > 0.3f) {
                for ((x, n) in Ci.lucesDeEnfrente(m)) reflejoRoto(x - 1.3f, x + 1.3f, WL + 0.6f, 6f + 4f * n, C.ACEITE, (0.28f + 0.1f * n).coerceAtMost(0.62f) * l.noche, az)
                if (E.faroles(m)) for (tau in listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f)) { val u = Ci.uDe(tau); val e = Ci.esc(u); reflejoRoto(Ci.px(u) - 0.8f * e, Ci.px(u) + 0.8f * e, Ci.aguaDelPuente(u) + 0.6f * e, 7f * e, C.ACEITE, 0.5f * l.noche, az, 0.8f + 0.4f * e) }
                if (E.farolDePopa(m)) reflejoRoto(E.FAROL.x - 4f, E.FAROL.x + 4f, 197f, 25f, C.ACEITE, 0.55f * l.noche, az, 1.6f)
            }
            estela(l, Rect(0f, WL, ANCHO, BORDA), m.azar("estela"))
            // El camino de la luna: una calle de trazos claros que se abre hacia quien mira.
            val luna = E.luna(l)
            if (luna > 0.05f && l.noche > 0.5f) {
                val x0 = l.lunaX * ANCHO; val h = m.azar("luna")
                mancha(Offset(x0, y(0.5f)), 15f, (BORDA - WL) * 0.5f, Color(0xFFDDE6FF), 0.14f * luna)
                repeat(80) { val d = h.real(); val w = 4f + 24f * d; val x = x0 + (h.real() + h.real() - 1f) * w; val largo = (2f + 9f * d) * h.entre(0.5f, 1.2f); drawLine(Color(0xFFEEF3FF).copy(alpha = (luna * h.entre(0.3f, 0.8f)).coerceIn(0f, 1f)), Offset(x - largo / 2f, y(d)), Offset(x + largo / 2f, y(d)), 0.5f + 1f * d, StrokeCap.Round, blendMode = BlendMode.Screen) }
            }
            // Con niebla, los bancos que se tienden sobre el río, entre la otra orilla y el barco.
            if (E.niebla(m)) for (i in 0..5) mancha(Offset(az.entre(0f, ANCHO), y(0.08f + i * 0.14f)), az.entre(90f, 170f), 4f + i * 2.2f, l.bruma, 0.42f - 0.04f * i)
        }
        // En la helada, los témpanos que el hielo amontona contra el muelle y contra las pilas del puente, y una barcaza presa en medio.
        if (helada) {
            // Las sombras azules de lo que el hielo tiene preso y de lo que pisa en él: las casas, el muelle, las pilas del
            // puente, la bomba, las barcazas, los postes y el bote.
            sombraEnHielo(l, r, 46f, 148f, WL + 0.6f, 22f); sombraEnHielo(l, r, 148f, 272f, WL + 0.6f, 7f)
            for (i in 0..4) { val ua = Ci.OJOS[i].second; val ub = Ci.OJOS[i + 1].first; val um = (ua + ub) / 2f; sombraEnHielo(l, r, Ci.px(ua) - 2f * Ci.esc(um), Ci.px(ub), Ci.aguaDelPuente(um) + 0.6f * Ci.esc(um), 9f * Ci.esc(um)) }
            for (x in listOf(297f, 304f, 311f, 318f, 325f)) sombraEnHielo(l, r, x - 0.6f, x + 0.6f, 137.6f, 26f)
            sombraEnHielo(l, r, 203f, 273f, y(0.4f) + 1.6f, 9f); sombraEnHielo(l, r, 151f, 197f, WL + 2f, 6f); sombraEnHielo(l, r, 204f, 243f, WL + 1.2f, 13f)
            for (x in listOf(196f, 336f)) sombraEnHielo(l, r, x - 1.7f, x + 1.7f, 204f, 23f)
            sombraEnHielo(l, r, 116f, 178f, 198f, 5f)
            // Las pocas velas de las ventanas y la hoguera del muelle, que el hielo devuelve sin romperlas.
            if (l.noche > 0.3f) for ((x, n) in Ci.lucesDeEnfrente(m)) mancha(Offset(x, WL + 4f + n), 2.6f, 5f + 2f * n, C.ACEITE, 0.26f * l.noche)
            if (E.hogueraDelMuelle(m)) { mancha(Offset(Ci.HOGUERA.x, WL + 10f), 24f, 10f, Color(0xFFFF9A4A), 0.36f * l.noche); mancha(Offset(Ci.HOGUERA.x, WL + 5f), 8f, 4f, Color(0xFFFFD27A), 0.42f * l.noche) }
            val h = Azar(Semilla.de("revolucion", "amontonado"))
            fun tempano(p: Offset, s: Float) { val w = h.entre(3f, 7f) * s; val alto = h.entre(1.6f, 3.6f) * s; drawPath(poligono(p, p + Offset(w, 0f), p + Offset(w * 0.7f, -alto), p + Offset(w * 0.2f, -alto * 0.7f)), l.tono(Color.White)); drawPath(poligono(p + Offset(w, 0f), p + Offset(w * 1.3f, -alto * 0.3f), p + Offset(w * 0.7f, -alto)), l.tono(lerp(C.HIELO, C.ANIL, 0.4f))) }
            var x = 46f; while (x < 272f) { tempano(Offset(x, WL + 1.4f), 0.7f); x += h.entre(5f, 14f) }
            for (i in 0..4) { val um = (Ci.OJOS[i].second + Ci.OJOS[i + 1].first) / 2f; val s = Ci.esc(um); repeat(4) { tempano(Offset(Ci.px(um) + h.entre(-5f, 2f) * s, Ci.aguaDelPuente(um) + h.entre(0.6f, 1.8f) * s), 0.5f * s) } }
            val presa = Offset(238f, y(0.4f))
            barcaza(m, l, r, presa, 70f, 0, 0.12f, true)
            drawPath(Path().apply { moveTo(presa.x - 40f, presa.y + 1.6f); cubicTo(presa.x - 20f, presa.y - 1f, presa.x + 20f, presa.y - 0.6f, presa.x + 40f, presa.y + 1.6f); cubicTo(presa.x + 20f, presa.y + 3.4f, presa.x - 20f, presa.y + 3.4f, presa.x - 40f, presa.y + 1.6f); close() }, l.tono(Color.White))
        }
        // Lo que flota (o lo que el hielo tiene preso).
        val dia = m.azar("carga")
        barcaza(m, l, r, Offset(174f, WL + 0.6f), 46f, dia.entero(3), 0.32f, E.nevado(m))
        if (!helada) reflejoRoto(152f, 196f, WL + 2.2f, 5f, t(m, l, C.BREA, 0.3f), 0.5f, az)
        // De noche, la luz de la caseta de la barcaza, donde duerme el barquero.
        if (l.noche > 0.4f && E.ventanas(m) > 0f) { val v = Offset(153.5f, 125.6f); drawRect(C.ACEITE, v, Size(1.6f, 1.9f)); resplandor(v + Offset(0.8f, 0.9f), 5f, 0.5f * l.noche, C.ACEITE); if (!helada) reflejoRoto(152.6f, 156.4f, WL + 2.6f, 8f, C.ACEITE, 0.42f * l.noche, az) }
        lavaderoLejano(m, l, r)
        if (!helada) reflejoRoto(204f, 243f, WL + 1.4f, 8f, t(m, l, lerp(C.MADERA_OSC, C.SENA_HONDO, 0.3f), 0.3f), 0.45f, az)
        if (E.barcaza(m)) {
            val b = m.azar("barcaza2"); val centro = Offset(b.entre(86f, 236f), y(0.36f))
            reflejoRoto(centro.x - 34f, centro.x + 34f, centro.y + 1.6f, 10f, t(m, l, C.BREA, 0.1f), 0.45f, az, 1.2f)
            with(E) { sombraDe(r, Path().apply { moveTo(centro.x - 36f, centro.y + 1.4f); lineTo(centro.x + 34f, centro.y + 1.4f); lineTo(centro.x + 30f + (if (r.lat > 0f) -8f else 8f), centro.y + 5f); lineTo(centro.x - 32f + (if (r.lat > 0f) -8f else 8f), centro.y + 5f); close() }, 0.4f) }
            barcaza(m, l, r, centro, 74f, b.entero(3), 0.14f, false)
            // La estela que deja río arriba y el bigote de la proa.
            for (i in 0..5) drawLine(l.tono(Color.White).copy(alpha = 0.3f - 0.04f * i), Offset(centro.x - 40f - i * 9f, centro.y + 1f + (i % 2) * 1.6f), Offset(centro.x - 33f - i * 9f, centro.y + 1f + (i % 2) * 1.6f), 0.6f, StrokeCap.Round)
            drawPath(Path().apply { moveTo(centro.x + 36f, centro.y); cubicTo(centro.x + 42f, centro.y + 1f, centro.x + 44f, centro.y + 2.4f, centro.x + 50f, centro.y + 2.4f) }, l.tono(Color.White).copy(alpha = 0.4f), style = Stroke(0.6f, cap = StrokeCap.Round))
        }
        // Los postes de amarre, con su reflejo y el cabo que va de uno a la borda.
        for ((x, arriba, agua) in listOf(Triple(196f, 180f, 204f), Triple(336f, 181f, 204f))) {
            if (!helada) reflejoRoto(x - 2f, x + 2f, agua + 0.4f, (agua - arriba) * 0.6f, t(m, l, C.MADERA_OSC, 0f), 0.4f, az, 1.3f)
            poste(m, l, r, x, arriba, agua)
        }
        drawPath(Path().apply { moveTo(196f, 183f); cubicTo(198f, 202f, 202f, 214f, 207f, BORDA - 2f) }, t(m, l, lerp(C.PAJA, C.MADERA, 0.4f), 0f), style = Stroke(0.7f, cap = StrokeCap.Round))
        bote(m, l, r)
        // Los patos del río y las gaviotas posadas en los postes (con el hielo, no hay).
        if (!helada && l.noche < 0.6f) {
            val h = m.azar("patos"); val n = 2 + h.entero(3); val o = Offset(h.entre(40f, 120f), h.entre(170f, 184f))
            for (i in 0 until n) { val q = o + Offset(i * h.entre(7f, 11f), h.entre(-2f, 2f)); drawOval(t(m, l, lerp(C.MADERA_OSC, C.PIEDRA_OSC, 0.3f), 0f), q, Size(4.6f, 2f)); drawCircle(t(m, l, lerp(C.HOJA, C.PIZARRA_OSC, 0.5f), 0f), 0.9f, q + Offset(4.6f, -0.3f)); drawLine(l.tono(Color.White).copy(alpha = 0.4f), q + Offset(-4f, 1.6f), q + Offset(0f, 1.6f), 0.4f) }
            for (x in listOf(196f, 336f)) if (h.entero(2) == 0) { val q = Offset(x, if (x < 300f) 178.4f else 179.4f); drawOval(t(m, l, C.CAL, 0f), q + Offset(-2.2f, -1.6f), Size(4.6f, 2.4f)); drawCircle(t(m, l, C.CAL, 0f), 0.9f, q + Offset(2.2f, -2f)); drawLine(t(m, l, C.PIZARRA, 0f), q + Offset(-2.6f, -0.8f), q + Offset(0.6f, -0.2f), 0.7f) }
        }
        if (helada) {
            // Alrededor del casco, el hielo roto con barras: una faja de agua negra con témpanos.
            // (delante de los cajones de lavar; a los lados, la nieve que el viento amontona contra el casco).
            val faja = Path().apply { moveTo(146f, BORDA + 2f); lineTo(151f, BORDA - 4f); val h = Azar(Semilla.de("revolucion", "faja")); var x = 160f; while (x < 332f) { lineTo(x, BORDA - h.entre(5f, 10f)); x += h.entre(9f, 20f) }; lineTo(337f, BORDA - 4f); lineTo(342f, BORDA + 2f); close() }
            drawPath(faja, l.tono(C.SENA_HONDO))
            val ladoSombra = if (l.noche > 0.5f) (if (l.lunaX > 0.5f) -1f else 1f) else (if (r.lat > 0f) -1f else 1f)
            for ((x0, x1, alto) in listOf(Triple(50f, 150f, 12f), Triple(336f, ANCHO + 8f, 10f))) {
                val lomo = Path().apply { moveTo(x0, BORDA + 2f); cubicTo(x0 + (x1 - x0) * 0.18f, BORDA - alto * 1.25f, x0 + (x1 - x0) * 0.6f, BORDA - alto * 1.15f, x1, BORDA + 2f); close() }
                drawPath(lomo, l.tono(Color.White))
                clipPath(lomo) {
                    drawRect(l.tono(lerp(C.HIELO, C.ANIL, 0.4f)).copy(alpha = 0.55f), Offset(if (ladoSombra < 0f) x0 else (x0 + x1) / 2f, BORDA - alto * 1.4f), Size((x1 - x0) / 2f, alto * 1.7f))
                    for (i in 0..3) drawLine(l.tono(lerp(C.HIELO, C.ANIL, 0.3f)).copy(alpha = 0.5f), Offset(x0 + (x1 - x0) * (0.1f + 0.2f * i), BORDA - alto * 0.3f + i), Offset(x0 + (x1 - x0) * (0.3f + 0.2f * i), BORDA - alto * 0.42f + i), 0.5f)
                }
            }
            clipPath(faja) { val h = Azar(Semilla.de("revolucion", "tempanos")); repeat(12) { val p = Offset(h.entre(152f, 334f), h.entre(BORDA - 7f, BORDA)); drawPath(poligono(p, p + Offset(h.entre(5f, 11f), -h.entre(0.5f, 2f)), p + Offset(h.entre(6f, 12f), h.entre(1f, 2.6f)), p + Offset(h.entre(0f, 3f), h.entre(1.6f, 3f))), l.tono(Color.White)) } }
        } else if (E.lavando(m)) {
            // El jabón que se va con la corriente, río abajo, desde las tablas.
            val h = m.azar("jabon"); repeat(18) { val p = Offset(h.entre(220f, ANCHO), h.entre(207f, BORDA - 2f)); drawOval(l.tono(C.CAL).copy(alpha = h.entre(0.25f, 0.6f)), p, Size(h.entre(3f, 10f), h.entre(0.8f, 1.8f))) }
        }
    }

    /** Lo que se mueve en el agua: los destellos y el temblor de las luces de la noche. */
    fun DrawScope.rioVivo(m: Momento, l: Luces, reloj: Float) {
        val Ci = CiudadRevolucion
        if (E.helada(m)) { if (E.hogueraDelMuelle(m)) resplandor(Offset(Ci.HOGUERA.x, WL + 7f), 17f + 3f * kotlin.math.sin(reloj * 5.3f), 0.22f * l.noche, Color(0xFFFF9A4A)); return }
        destellos(l, Rect(0f, WL + 2f, ANCHO, BORDA - 2f), reloj, m.semilla("destellos"), 30)
        if (E.faroles(m)) {
            val k = 0.7f * l.noche
            for ((i, x) in listOf(153.4f, 200.6f, 240f).withIndex()) reflejo(Offset(x, WL + 1f), 14f, C.ACEITE, k, reloj, i)
            for ((i, tau) in listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f).withIndex()) { val u = Ci.uDe(tau); reflejo(Offset(Ci.px(u), Ci.aguaDelPuente(u) + 1f), 8f * Ci.esc(u), C.ACEITE, k * 0.8f, reloj, 10 + i) }
            reflejo(Offset(Ci.BOMBA.x, 139f), 16f, C.ACEITE, k * 0.8f, reloj, 20)
        }
        if (l.noche > 0.3f) for ((i, par) in Ci.lucesDeEnfrente(m).withIndex()) reflejo(Offset(par.first, WL + 1f), 6f + 3f * par.second, C.ACEITE, 0.45f * l.noche, reloj, 30 + i)
        if (E.farolDePopa(m)) reflejo(Offset(E.FAROL.x, 198f), 21f, C.ACEITE, 0.6f * l.noche, reloj, 70)
        if (E.hogueraDeSanJuan(m)) { reflejo(Offset(366f, 170f), 40f, Color(0xFFFF8A3A), 0.7f, reloj, 40); resplandor(Offset(380f, 180f), 60f, 0.3f, Color(0xFFFF8A3A)) }
    }
}
