package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * La bolsa de piel de la libreta de Prehistoria, lo que cambia con las hojas y al abrirse (se pinta en cada dibujo): el candil
 * de asta de donde cuelga y su correa; el zurrón grande de cuero curtido, asentado en la repisa, que engorda con las pieles; el
 * cuello de pelo de reno fruncido por la correa, con las pieles enrolladas que asoman por la boca (más cuantas más hay); en la
 * panza, el parche de piel raspada cosido con tendón, con el nombre pintado con ocre y la cuenta con carbón; delante, la piedra
 * de mezclar y el pincel.
 */
internal object BolsaLibretaCueva {
    /** Cuánto está la bolsa por delante de la pared, en altos (la mancha de la boca se le corre). */
    private const val DELANTE = 0.07f
    /** El cuero curtido de la bolsa, el de su sombra y el pelo de reno del cuello (pardo gris, con las puntas claras). */
    private val CUERO_BOLSA = Color(0xFF94683F)
    private val CUERO_HONDO = Color(0xFF5A3A22)
    private val RENO = Color(0xFF8A7A66)
    private val RENO_PUNTA = Color(0xFFD8CCB6)
    /** La piel raspada del parche y de las pieles enrolladas. */
    private val RASPADA = Color(0xFFCDAE80)

    fun DrawScope.bolsa(g: MarcoLibretaCueva, e: EstadoLibreta, medidor: TextMeasurer) {
        val h = g.h; val u = density
        val ab = e.abierto.coerceIn(0f, 1f)
        val cx = g.cx; val pz = g.panza
        val cuello = h * 0.27f
        val nk = h * (0.085f + 0.04f * ab)
        val pie = g.repisa + h * 0.004f
        val hacia = g.hacia(cx + pz)
        // El cuerpo: el cuello fruncido, la panza que se hincha y el fondo que se aplasta en la repisa.
        fun q(x: Float, y: Float) = Offset(cx + x, y)
        // Blando y vencido por su peso: más ancho abajo, donde se desparrama en la repisa, y no del todo simétrico.
        val cuerpo = cerradaCueva(listOf(q(-nk, cuello + h * 0.01f), q(-nk - h * 0.05f, h * 0.335f), q(-pz * 0.8f, h * 0.42f), q(-pz * 0.97f, h * 0.54f),
            q(-pz * 1.06f, h * 0.65f), q(-pz * 1.1f, pie - h * 0.03f), q(-pz * 0.96f, pie + h * 0.002f), q(-pz * 0.3f, pie + h * 0.007f), q(pz * 0.5f, pie + h * 0.005f),
            q(pz * 1.01f, pie - h * 0.008f), q(pz * 1.06f, pie - h * 0.045f), q(pz * 1.0f, h * 0.62f), q(pz * 0.88f, h * 0.5f), q(pz * 0.68f, h * 0.4f),
            q(nk + h * 0.045f, h * 0.335f), q(nk, cuello + h * 0.01f)))
        // La sombra en la pared (lejos de la luz) y en la repisa.
        val lejos = if (g.a.oscuro) -1f else 1f
        drawPath(corridoCueva(cuerpo, lejos * h * 0.045f, h * 0.012f), g.luz(Offset(cx, h * 0.5f)).s(0.34f))
        drawOval(g.luz(Offset(cx, pie)).s(0.5f), Offset(cx - pz * 1.05f + lejos * h * 0.03f, pie - h * 0.012f), Size(pz * 2.15f, h * 0.03f))
        candilYCorrea(g, cuello, nk, ab)
        conManchaLibretaCueva(g, DELANTE) { s ->
            val l = g.luz(Offset(cx, h * 0.52f), DELANTE, s)
            bultoRapidoCueva(cuerpo, l.c(CUERO_BOLSA), l, hacia, 1.2f, 0.34f)
            cuero(g, cuerpo, cx, pz, cuello, pie, l, hacia)
        }
        cuelloYBoca(g, e, cx, nk, cuello, ab)
        parche(g, e, medidor, cx, pz)
        piedraYPincel(g)
    }

    /** Lo que se ve del cuero: el fondo que se apoya, los pliegues que bajan del cuello, la costura de un lado y las rozaduras. */
    private fun DrawScope.cuero(g: MarcoLibretaCueva, cuerpo: Path, cx: Float, pz: Float, cuello: Float, pie: Float, l: LuzCueva, hacia: Float) {
        val h = g.h; val u = density
        drawOval(l.s(0.16f), Offset(cx - pz * 0.95f, pie - h * 0.06f), Size(pz * 1.9f, h * 0.065f))
        // Los pliegues que el frunce le saca al cuero: bajan del cuello abriéndose, y la arruga larga donde el fondo se aplasta.
        val pliegues = Path()
        for (k in 0 until 5) {
            val dx = (k - 2f) * pz * 0.2f + pz * 0.04f * (azar(7701, k) - 0.5f)
            pliegues.moveTo(cx + dx * 0.25f, cuello + h * 0.022f)
            pliegues.quadraticTo(cx + dx * 0.9f + pz * 0.05f * (azar(7702, k) - 0.5f), cuello + h * 0.05f, cx + dx * 1.5f, cuello + h * (0.07f + 0.05f * azar(7700, k)))
        }
        pliegues.moveTo(cx - pz * 0.9f, pie - h * 0.045f); pliegues.quadraticTo(cx - pz * 0.2f, pie - h * 0.02f, cx + pz * 0.85f, pie - h * 0.035f)
        drawPath(pliegues, l.s(0.34f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(pliegues, hacia * 1.6f * u, -0.4f * u), l.b(0.28f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        // La costura del lado contrario a la luz: el tendón en puntadas, a lo largo del costado.
        val lado = -hacia
        val costura = abiertaCueva(listOf(Offset(cx + lado * pz * 0.55f, cuello + h * 0.1f), Offset(cx + lado * pz * 0.86f, h * 0.52f), Offset(cx + lado * pz * 0.72f, pie - h * 0.02f)))
        drawPath(costura, l.s(0.45f), style = Stroke(1.4f * u))
        drawPath(costura, l.c(mezcla(P.CUERO, P.CUERO_OSC, 0.55f)), style = Stroke(1.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.6f * u, 2.8f * u))))
        // Una vuelta de puntos de ocre rojo pintada con el dedo alrededor de la panza, que sigue su curva.
        for (k in 0 until 9) {
            val f = -0.8f + 1.6f * k / 8f
            val q = Offset(cx + pz * f, h * 0.69f + h * 0.022f * (1f - f * f))
            drawOval(l.c(P.OCRE_ROJO).copy(alpha = 0.85f), Offset(q.x - h * 0.011f * (1f - 0.35f * f * f), q.y - h * 0.009f), Size(h * 0.022f * (1f - 0.35f * f * f), h * 0.018f))
        }
        // Las rozaduras del cuero, más claras, en lo que abulta.
        drawOval(l.b(0.24f), Offset(cx - hacia * pz * 0.15f - pz * 0.35f, h * 0.45f), Size(pz * 0.7f, h * 0.12f))
        drawOval(l.c(CUERO_HONDO).copy(alpha = 0.18f), Offset(cx + hacia * pz * 0.1f - pz * 0.2f, h * 0.66f), Size(pz * 0.5f, h * 0.05f))
    }

    /**
     * El candil de asta clavado en la grieta de la pared y la correa de cuero que baja de él a los dos lados del cuello (al
     * abrirse, la correa se afloja).
     */
    private fun DrawScope.candilYCorrea(g: MarcoLibretaCueva, cuello: Float, nk: Float, ab: Float) {
        val h = g.h; val u = density
        val c = g.candil
        val l = g.luz(c, 0.03f)
        val punta = c + Offset(-h * 0.045f, -h * 0.05f)
        val asta = abiertaCueva(listOf(c + Offset(h * 0.006f, h * 0.004f), c + Offset(-h * 0.03f, -h * 0.008f), punta))
        drawPath(corridoCueva(asta, h * 0.012f, h * 0.012f), l.s(0.4f), style = Stroke(h * 0.022f, cap = StrokeCap.Round))
        drawPath(asta, l.c(mezcla(P.HUESO, P.PINO_OSC, 0.25f)), style = Stroke(h * 0.022f, cap = StrokeCap.Round))
        drawPath(corridoCueva(asta, -h * 0.004f, -h * 0.004f), l.b(0.5f), style = Stroke(h * 0.007f, cap = StrokeCap.Round))
        drawCircle(l.c(mezcla(P.HUESO_OSC, P.PINO_OSC, 0.4f)), h * 0.016f, c)
        // La correa: de la gaza en el candil a los dos lados del cuello.
        val gaza = c + Offset(-h * 0.022f, -h * 0.002f)
        val izq = Offset(g.cx - nk - h * 0.01f, cuello + h * 0.02f + h * 0.03f * ab)
        val der = Offset(g.cx + nk + h * 0.01f, cuello + h * 0.02f + h * 0.03f * ab)
        val correa = Path().apply {
            moveTo(izq.x, izq.y); quadraticTo(gaza.x - h * 0.02f, (gaza.y + izq.y) * 0.5f, gaza.x, gaza.y)
            moveTo(der.x, der.y); quadraticTo(gaza.x + h * 0.03f, (gaza.y + der.y) * 0.5f, gaza.x + h * 0.004f, gaza.y)
        }
        val lc = g.luz(Offset(g.cx, (cuello + c.y) / 2f), DELANTE)
        drawPath(corridoCueva(correa, h * 0.01f, h * 0.006f), lc.s(0.35f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawPath(correa, lc.c(mezcla(P.CUERO_OSC, CUERO_HONDO, 0.3f)), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(correa, -0.6f * u, -0.4f * u), lc.b(0.35f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        drawCircle(lc.c(CUERO_HONDO), 2.6f * u, gaza)
    }

    /**
     * El cuello: el cuero que sigue por encima de la correa, fruncido en pliegues que se abren hacia arriba, con el pelo de reno
     * del forro asomando por la orilla; detrás, el labio de atrás, y entre los dos las pieles enrolladas que asoman (más cuantas
     * más hojas; suben al abrirse). La correa del frunce, con su nudo y sus dos cabos.
     */
    private fun DrawScope.cuelloYBoca(g: MarcoLibretaCueva, e: EstadoLibreta, cx: Float, nk: Float, cuello: Float, ab: Float) {
        val h = g.h; val u = density
        val l = g.luz(Offset(cx, cuello), DELANTE)
        val hacia = g.hacia(cx)
        val abre = h * 0.045f * ab
        val ancho = nk + h * 0.06f + abre
        // La orilla de un labio: ondulada, con las puntas de los pliegues.
        fun orilla(y: Float, alto: Float, sem: Int): List<Offset> = List(7) { k ->
            val t = k / 6f
            Offset(cx - ancho + 2f * ancho * t, y - alto * (0.6f + 0.4f * azar(sem, k)) * (if (k % 2 == 0) 1f else 0.55f))
        }
        // El labio de atrás: se ve por dentro, el forro de pelo, en sombra.
        val atras = orilla(cuello - h * 0.06f, h * 0.05f, 7730)
        val labioAtras = cerradaCueva(atras + listOf(Offset(cx + nk, cuello), Offset(cx - nk, cuello)))
        drawPath(labioAtras, l.c(mezcla(RENO, CUERO_HONDO, 0.55f)))
        drawPath(labioAtras, l.s(0.3f))
        // Las pieles enrolladas que asoman entre los labios.
        val n = if (e.hojas <= 0) 0 else (1 + e.hojas / 20).coerceIn(1, 5)
        val sube = h * (0.04f + 0.07f * g.gordo + 0.12f * ab)
        val base = cuello - h * 0.02f
        for (j in 0 until n) {
            val px = cx - nk * 0.8f + nk * 1.6f * (j + 0.5f) / n + (azar(7710, j) - 0.5f) * h * 0.015f
            val an = h * (0.045f + 0.018f * azar(7711, j))
            val tope = base - h * 0.065f - sube * (0.55f + 0.45f * azar(7712, j))
            val lj = g.luz(Offset(px, tope), DELANTE)
            val giro = (j - (n - 1) / 2f) * (9f + 8f * ab) + (azar(7714, j) - 0.5f) * 8f
            val rollo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(px - an / 2, tope, px + an / 2, base + h * 0.02f), an * 0.42f, an * 0.42f)) }
            withTransformGiro(giro, Offset(px, base)) {
                bultoRapidoCueva(rollo, lj.c(mezcla(RASPADA, P.PIEL, 0.15f + 0.45f * azar(7713, j))), lj, hacia, 1.1f, 0.3f)
                // La boca de arriba: la piel enrollada vista de canto (una espiral fina) y la orilla pintada de la última vuelta.
                drawOval(lj.c(mezcla(RASPADA, P.PIEL_OSC, 0.35f)), Offset(px - an / 2, tope - an * 0.12f), Size(an, an * 0.42f))
                drawArc(lj.c(mezcla(P.PELO, P.PIEL_OSC, 0.3f)), 200f, 280f, false, Offset(px - an * 0.32f, tope - an * 0.05f), Size(an * 0.64f, an * 0.27f), style = Stroke(0.8f * u))
                drawOval(lj.c(mezcla(RASPADA, P.PIEL_OSC, 0.2f)), Offset(px - an / 2, tope - an * 0.12f), Size(an, an * 0.42f), style = Stroke(0.9f * u))
                drawLine(lj.c(if (j % 2 == 0) P.OCRE_ROJO else P.CARBON).copy(alpha = 0.8f), Offset(px + an * 0.18f, tope + an * 0.3f), Offset(px + an * 0.22f, base), 1.3f * u)
            }
        }
        // El labio de delante: el cuero fruncido por la correa, con sus pliegues que suben y el pelo del forro en la orilla.
        val delante = orilla(cuello - h * 0.03f, h * 0.035f, 7731)
        val labio = cerradaCueva(listOf(Offset(cx - nk - h * 0.005f, cuello + h * 0.016f)) + delante + listOf(Offset(cx + nk + h * 0.005f, cuello + h * 0.016f)))
        bultoRapidoCueva(labio, l.c(CUERO_BOLSA), l, hacia, 1.2f, 0.36f)
        val pliegues = Path()
        delante.forEachIndexed { k, p ->
            if (k == 0 || k == delante.size - 1) return@forEachIndexed
            pliegues.moveTo(cx + (p.x - cx) * 0.25f, cuello + h * 0.012f); pliegues.lineTo(p.x, p.y + h * 0.008f)
        }
        drawPath(pliegues, l.s(0.4f), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(pliegues, hacia * 1.2f * u, 0f), l.b(0.3f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        val pelo = Path(); val puntas = Path()
        for (k in 0 until 18) {
            val t = k / 17f
            val i = (t * (delante.size - 1)).toInt().coerceAtMost(delante.size - 2)
            val f = t * (delante.size - 1) - i
            val p = delante[i] + (delante[i + 1] - delante[i]) * f
            val lg = h * (0.018f + 0.016f * azar(7720, k))
            val dx = (t - 0.5f) * h * 0.03f
            pelo.moveTo(p.x, p.y + h * 0.006f); pelo.lineTo(p.x + dx * 0.5f, p.y - lg * 0.6f)
            puntas.moveTo(p.x + dx * 0.4f, p.y - lg * 0.4f); puntas.lineTo(p.x + dx, p.y - lg)
        }
        drawPath(pelo, l.c(mezcla(RENO, CUERO_HONDO, 0.3f)), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(puntas, l.c(RENO_PUNTA), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        // La correa del frunce, con su nudo y sus dos cabos (al abrirse, baja floja).
        val baja = h * 0.03f * ab
        val y = cuello + h * 0.014f + baja
        drawLine(l.s(0.45f), Offset(cx - nk - h * 0.012f, y + 2f * u), Offset(cx + nk + h * 0.012f, y + 3f * u), 3.2f * u)
        drawLine(l.c(CUERO_HONDO), Offset(cx - nk - h * 0.012f, y), Offset(cx + nk + h * 0.012f, y + 1f * u), 3.2f * u, StrokeCap.Round)
        drawLine(l.b(0.4f), Offset(cx - nk, y - 1f * u), Offset(cx, y - 0.6f * u), 0.8f * u)
        val nudo = Offset(cx + nk * 0.55f, y + 0.5f * u)
        drawCircle(l.c(mezcla(CUERO_HONDO, P.CUERO, 0.3f)), 3.4f * u, nudo)
        drawLine(l.c(CUERO_HONDO), nudo, nudo + Offset(h * 0.012f, h * 0.075f), 2f * u, StrokeCap.Round)
        drawLine(l.c(CUERO_HONDO), nudo, nudo + Offset(h * 0.032f, h * 0.06f), 2f * u, StrokeCap.Round)
    }

    private fun DrawScope.withTransformGiro(grados: Float, pivote: Offset, bloque: DrawScope.() -> Unit) {
        if (grados == 0f) bloque() else rotate(grados, pivote) { bloque() }
    }

    // ---------------------------------------------------------------- el parche con el nombre

    /** Las medidas de los textos del parche, guardadas (se repiten en cada dibujo y no cambian). */
    private val medidas = HashMap<String, TextLayoutResult>()

    private fun DrawScope.texto(medidor: TextMeasurer, s: String, r: Rect, color: Color, tam: Float, letra: androidx.compose.ui.text.font.FontFamily, peso: FontWeight, minimo: Float) {
        val clave = "$s|${r.width.toInt()}|${r.height.toInt()}|$color|$tam|$density|$fontScale"
        if (medidas.size > 24) medidas.clear()
        val capa = medidas[clave] ?: (capaEn(medidor, s, r, letra, color, tam, peso = peso, minimo = minimo) ?: return).also { medidas[clave] = it }
        drawText(capa, topLeft = Offset(r.center.x - capa.size.width / 2f, r.center.y - capa.size.height / 2f))
    }

    /**
     * El parche de piel raspada cosido en la panza con tendón (puntadas de un solo trazo, nada de aspas): el nombre pintado con
     * ocre y la cuenta con carbón, en dos renglones si es larga.
     */
    private fun DrawScope.parche(g: MarcoLibretaCueva, e: EstadoLibreta, medidor: TextMeasurer, cx: Float, pz: Float) {
        val h = g.h; val u = density
        val pw = minOf(h * 0.44f, pz * 1.62f); val ph = h * 0.22f
        val r = Rect(cx - pw / 2f + h * 0.01f, h * 0.43f, cx + pw / 2f + h * 0.01f, h * 0.43f + ph)
        val forma = cerradaCueva(listOf(Offset(r.left, r.top + ph * 0.08f), Offset(r.center.x, r.top - ph * 0.02f), Offset(r.right, r.top + ph * 0.05f),
            Offset(r.right + pw * 0.015f, r.center.y), Offset(r.right - pw * 0.01f, r.bottom), Offset(r.center.x, r.bottom + ph * 0.03f),
            Offset(r.left + pw * 0.01f, r.bottom - ph * 0.02f), Offset(r.left - pw * 0.012f, r.center.y)))
        val dentro = cerradaCueva(listOf(Offset(r.left + 3f * u, r.top + 3f * u), Offset(r.right - 3f * u, r.top + 2.6f * u), Offset(r.right - 2.6f * u, r.bottom - 3f * u), Offset(r.left + 3.2f * u, r.bottom - 3f * u)))
        // La piel raspada, cosida a la bolsa: la mancha de la boca la cruza igual que al cuero de alrededor.
        conManchaLibretaCueva(g, DELANTE) { s ->
            val ls = g.luz(r.center, DELANTE, s)
            drawPath(corridoCueva(forma, 1.2f * u, 1.6f * u), ls.s(0.35f))
            drawPath(forma, ls.c(RASPADA))
            drawPath(bordeCueva(forma, -g.hacia(cx) * h * 0.012f, h * 0.008f), ls.s(0.16f))
            // Las puntadas de tendón, de un solo trazo, a lo largo de la orilla.
            drawPath(dentro, ls.c(mezcla(P.CUERO, P.CUERO_OSC, 0.5f)), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 3f * u))))
        }
        // El nombre y la cuenta: de día, ocre hondo y carbón sobre la piel (en su sombra y al sol); de noche, la piel se apaga y la
        // tinta va clara, la que más se lea.
        val raspada = g.luz(r.center, DELANTE, 0f).c(RASPADA)
        val ocre = if (!g.a.oscuro) mezcla(P.OCRE_ROJO, P.CARBON, 0.45f) else legibleCueva(raspada, P.OCRE_ROJO, mezcla(P.OCRE_AMAR, P.CAOLIN, 0.62f))
        val carbon = if (!g.a.oscuro) P.CARBON else legibleCueva(raspada, P.CARBON, P.CAOLIN)
        texto(medidor, e.nombre, Rect(r.left + 6f * u, r.top + ph * 0.06f, r.right - 6f * u, r.top + ph * 0.48f), ocre, 15f, LetraExtra.trazo, FontWeight.Bold, 8f)
        val partes = e.cuenta.split(" · ", limit = 2)
        if (partes.size == 2) {
            texto(medidor, partes[0], Rect(r.left + 6f * u, r.top + ph * 0.47f, r.right - 6f * u, r.top + ph * 0.71f), carbon, 10.5f, P.LETRA, FontWeight.Bold, 6.5f)
            texto(medidor, partes[1], Rect(r.left + 6f * u, r.top + ph * 0.7f, r.right - 6f * u, r.top + ph * 0.94f), carbon, 9.5f, P.LETRA, FontWeight.Normal, 6.5f)
        } else texto(medidor, e.cuenta, Rect(r.left + 6f * u, r.top + ph * 0.5f, r.right - 6f * u, r.top + ph * 0.92f), carbon, 10.5f, P.LETRA, FontWeight.Bold, 6.5f)
    }

    /** [oscura] si se lee sobre [fondo] (contraste de 4.5 a 1 o más); si no, la que más se lea de las dos. */
    private fun legibleCueva(fondo: Color, oscura: Color, clara: Color): Color {
        fun contraste(a: Color, b: Color): Float {
            val la = a.luminance(); val lb = b.luminance()
            return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
        }
        val co = contraste(fondo, oscura)
        return if (co >= 4.5f || co >= contraste(fondo, clara)) oscura else clara
    }

    // ---------------------------------------------------------------- la piedra de mezclar y el pincel

    /** Delante de la bolsa, en la repisa: la piedra de mezclar con sus pegotes de ocre rojo, amarillo y negro, y el pincel de pelo. */
    private fun DrawScope.piedraYPincel(g: MarcoLibretaCueva) {
        val h = g.h; val u = density
        val c = Offset(maxOf(g.boca + h * 0.09f, g.cx - g.panza - h * 0.02f), g.repisa - h * 0.008f)
        conManchaLibretaCueva(g, 0.09f) { s ->
            val l = g.luz(c, 0.09f, s)
            val piedra = piedraCueva(c, h * 0.075f, h * 0.022f, 7800, 9, 0.18f)
            drawOval(l.s(0.45f), Offset(c.x - h * 0.07f, c.y + h * 0.002f), Size(h * 0.16f, h * 0.03f))
            bultoRapidoCueva(piedra, l.c(mezcla(P.CALIZA, P.CALIZA_FRIA, 0.35f)), l, g.hacia(c.x), 1.1f, 0.4f)
            listOf(Triple(-0.035f, P.OCRE_ROJO, 0.016f), Triple(0.002f, P.OCRE_AMAR, 0.014f), Triple(0.036f, P.MANGANESO, 0.012f)).forEach { (dx, col, r) ->
                val p = Offset(c.x + h * dx, c.y - h * 0.006f)
                drawOval(l.c(col), Offset(p.x - h * r, p.y - h * r * 0.45f), Size(h * r * 2f, h * r * 0.9f))
                drawOval(l.b(0.4f), Offset(p.x - h * r * 0.5f, p.y - h * r * 0.4f), Size(h * r * 0.8f, h * r * 0.3f))
            }
            // El pincel: un palito con el mechón de pelo atado, la punta untada de ocre, cruzado sobre la piedra.
            val a = Offset(c.x - h * 0.09f, c.y + h * 0.004f); val b = Offset(c.x + h * 0.03f, c.y - h * 0.03f)
            drawLine(l.s(0.4f), a + Offset(h * 0.006f, h * 0.01f), b + Offset(h * 0.006f, h * 0.01f), 2.4f * u, StrokeCap.Round)
            drawLine(l.c(P.PINO_OSC), a, b, 2.4f * u, StrokeCap.Round)
            val dir = (b - a) / (b - a).getDistance()
            val mechon = Path().apply {
                val n = Offset(-dir.y, dir.x)
                moveTo(b.x + n.x * h * 0.006f, b.y + n.y * h * 0.006f); lineTo(b.x + dir.x * h * 0.04f, b.y + dir.y * h * 0.04f)
                lineTo(b.x - n.x * h * 0.006f, b.y - n.y * h * 0.006f); close()
            }
            drawPath(mechon, l.c(P.PELO))
            drawLine(l.c(P.OCRE_ROJO), b + dir * h * 0.028f, b + dir * h * 0.04f, 3f * u, StrokeCap.Round)
            drawLine(l.c(P.CUERO), b - dir * h * 0.004f + Offset(-dir.y, dir.x) * h * 0.008f, b - dir * h * 0.004f - Offset(-dir.y, dir.x) * h * 0.008f, 1.6f * u)
        }
    }
}
