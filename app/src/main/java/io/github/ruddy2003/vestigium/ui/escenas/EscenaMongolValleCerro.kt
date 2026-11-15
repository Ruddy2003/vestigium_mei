package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
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
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ovoo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.rebano
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.alturaEn
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.en
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El cerro que abriga el valle, la diagonal grande del cuadro, en dos planos: **la cima** detrás, con el ovoo recortado
 * contra el cielo, y **un hombro** delante, más cerca, más cálido y con más detalle. Cada uno está hecho de caras: de
 * cada espolón baja una arista que separa una cara que mira a la derecha de otra que mira a la izquierda, y entre dos
 * espolones baja una cárcava. Con el sol de la mañana (a la derecha) se encienden unas caras y las otras quedan en
 * sombra azul; por la tarde, al revés (regla 1).
 *
 * El viento barre los lomos y deja asomar la hierba seca y la roca: la nieve se queda en las cárcavas, en las caras que
 * menos ven el sol y al pie del cerro, en ventisqueros (regla 4, sin agua: la nieve). Afloramientos de roca en estratos,
 * sus canchales debajo y matas secas rompen las caras (reglas 7 y 9). **[Diseño propio]**: el cuento solo dice «el cerro».
 */
internal object ValleCerro {
    private val C = ColorMongol

    /** La cresta de la cima, puesta a mano: sube de detrás de la tienda al ovoo y baja larga hasta donde asoma el río. */
    val CRESTA = pts(
        -6f, 70f, 12f, 66.4f, 28f, 65.8f, 44f, 62.8f, 58f, 63.4f, 72f, 60.2f, 88f, 59.6f, 102f, 56.8f, 116f, 57.6f, 128f, 55.2f, 140f, 55.8f,
        152f, 53.8f, 162f, 54.6f, 172f, 53.2f, 182f, 54.8f, 190f, 54.4f, 198f, 55.8f, 206f, 58.6f, 214f, 60.2f, 222f, 64f, 230f, 65.8f,
        238f, 70.4f, 246f, 73f, 254f, 78.2f, 262f, 81f, 270f, 86.4f, 278f, 89.6f, 286f, 95.8f, 292f, 100.4f, 298f, 105.2f, 303f, 110.6f,
        308f, 115.2f, 313f, 119f, 317f, 121.2f,
    )

    /** El pie del cerro: donde la ladera se encuentra con el fondo del valle (y donde se escalonan las tiendas). */
    val PIE = pts(120f, 214f, 140f, 207.6f, 158f, 199.4f, 176f, 190.6f, 196f, 180.4f, 212f, 171.8f, 230f, 162.4f, 248f, 153.6f, 264f, 146f, 278f, 138.4f, 290f, 132f, 300f, 127f, 310f, 123.4f, 317f, 121.2f)

    /** La cresta del hombro, el plano de delante: baja de detrás de la tienda y se junta con el pie hacia la derecha. */
    val HOMBRO = pts(56f, 89.6f, 76f, 92.6f, 92f, 93.4f, 108f, 96.8f, 122f, 98f, 136f, 101.6f, 150f, 102.6f, 162f, 106.8f, 174f, 108.2f, 186f, 112.6f, 198f, 114f, 210f, 118.4f, 222f, 120.2f, 234f, 124.8f, 244f, 126.2f, 254f, 130.4f, 264f, 133.6f, 272f, 136.6f, 279f, 138.4f)

    /** El ovoo, en la cima. */
    val OVOO = Offset(190f, 55.4f)

    /**
     * Una arista (espolón, `true`) o una cárcava, de arriba abajo; y la cara que queda a su derecha, con su normal (las
     * caras que miran a la derecha se encienden con el sol de la mañana; las que miran a la izquierda, por la tarde).
     */
    private class Linea(val puntos: List<Offset>, val espolon: Boolean, val cara: V3)

    private val DERECHA = V3(0.74f, 0.42f, -0.52f)
    private val IZQUIERDA = V3(-0.74f, 0.42f, -0.52f)
    private val FRENTE = V3(-0.18f, 0.5f, -0.85f)

    private val CIMA = listOf(
        Linea(pts(-6f, 70f, -6f, 260f), false, IZQUIERDA),
        Linea(pts(116f, 57.6f, 122f, 72f, 128f, 88f, 132f, 104f), true, DERECHA),
        Linea(pts(140f, 55.8f, 147f, 68f, 152f, 82f, 158f, 96f, 162f, 110f), false, FRENTE),
        Linea(pts(190f, 54.4f, 200f, 66f, 214f, 80f, 228f, 95f, 244f, 112f, 258f, 130f, 270f, 142f), true, DERECHA),
        Linea(pts(222f, 64f, 232f, 76f, 242f, 89f, 254f, 104f, 266f, 120f, 280f, 136f), false, IZQUIERDA),
        Linea(pts(246f, 73f, 255f, 85f, 265f, 99f, 276f, 114f, 288f, 130f), true, DERECHA),
        Linea(pts(270f, 86.4f, 278f, 97f, 287f, 110f, 297f, 125f), false, IZQUIERDA),
        Linea(pts(292f, 100.4f, 297f, 108f, 303f, 117f, 308f, 124f), true, DERECHA),
        Linea(pts(317f, 121.2f, 330f, 260f), false, V3(0f, 1f, 0f)),
    )

    private val DELANTE = listOf(
        Linea(pts(-6f, 92f, -6f, 260f), false, FRENTE),
        Linea(pts(150f, 102.6f, 154f, 118f, 158f, 136f, 163f, 156f, 168f, 178f, 172f, 200f), false, IZQUIERDA),
        Linea(pts(174f, 108.2f, 180f, 122f, 184f, 138f, 188f, 156f, 192f, 176f), true, DERECHA),
        Linea(pts(198f, 114f, 203f, 127f, 209f, 141f, 215f, 156f, 220f, 170f), false, IZQUIERDA),
        Linea(pts(222f, 120.2f, 228f, 132f, 234f, 144f, 240f, 156f), true, DERECHA),
        Linea(pts(244f, 126.2f, 249f, 136f, 254f, 147f), false, FRENTE),
        Linea(pts(262f, 133.2f, 266f, 141f, 270f, 146f), true, DERECHA),
        Linea(pts(279f, 138.4f, 290f, 260f), false, V3(0f, 1f, 0f)),
    )

    /** Los afloramientos de roca: dónde pisa cada uno, su ancho y si tiene canchal debajo. */
    private class Pena(val pie: Offset, val ancho: Float, val canchal: Boolean)

    private val PENAS_CIMA = listOf(
        Pena(Offset(128f, 62f), 7f, false), Pena(Offset(146f, 60.6f), 9f, true), Pena(Offset(168f, 60f), 6f, false),
        Pena(Offset(220f, 69.6f), 10f, true), Pena(Offset(262f, 92f), 6f, false), Pena(Offset(288f, 106f), 5f, true),
    )
    private val PENAS_DELANTE = listOf(
        Pena(Offset(164f, 113f), 12f, true), Pena(Offset(184f, 118.6f), 9f, false), Pena(Offset(204f, 123.4f), 14f, true),
        Pena(Offset(196f, 148f), 10f, true), Pena(Offset(236f, 136f), 8f, false), Pena(Offset(170f, 168f), 11f, false),
    )

    fun DrawScope.cerro(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val pasto = EscenaMongol.pasto(m)
        // La hierba seca del cerro: un ocre pardo, más oscuro que el fieltro y que la nieve (los tres valores del cuadro).
        val hierba = lerp(lerp(C.ESTEPA, C.CUERO, 0.3f), lerp(C.VERDE, C.ESTEPA, 0.35f), 0.45f * pasto)
        // La cima, detrás: un poco más azulada.
        val cima = poligono(CRESTA + PIE.reversed() + listOf(Offset(-6f, 230f)))
        plano(p, sol, nieve, cima, CRESTA, CIMA, PENAS_CIMA, hierba, 0.13f, fijo("cima"))
        // El ovoo, recortado contra el cielo, con su sombra en la cima.
        val alto = 24f
        capaSombra(sol.r) { c -> sombraArrojada(sol.r, PiezasMongol.siluetaDeOvoo(OVOO, alto), OVOO, c, OVOO.y + 0.6f, OVOO.y + 0.6f) }
        ovoo(p, OVOO, alto, EscenaMongol.cintasDelOvoo(m), Azar(Semilla.de("mongol", "ovoo")), 0.12f, nieve, m.viento)
        // «La última luz del día se queda un momento en lo alto del cerro, sobre el ovoo.»
        val l = p.l
        if (l.solAltura in -0.05f..0.25f && l.noche < 0.6f) mancha(OVOO + Offset(0f, -2f), 60f, 9f, l.solColor, 0.28f * (1f - l.noche))
        // Entre la cima y el hombro, un poco de aire: despega los dos planos.
        val hombro = poligono(listOf(Offset(-6f, 92f)) + HOMBRO + PIE.filter { it.x < 279f }.reversed() + listOf(Offset(-6f, 230f)))
        clipPath(cima) { drawPath(poligono(HOMBRO.map { it + Offset(0f, 3f) } + HOMBRO.reversed().map { it + Offset(0f, -18f) }), Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(l.bruma, l.bajo, 0.4f).copy(alpha = 0.3f * (1f - 0.6f * l.noche)), startY = 76f, endY = 138f)) }
        // El hombro, delante: más cálido, con la roca más grande.
        plano(p, sol, nieve, hombro, HOMBRO, DELANTE, PENAS_DELANTE, lerp(hierba, C.CUERO, 0.12f), 0.03f, fijo("hombro"))
        // La nieve arrimada al pie del cerro: un ventisquero de borde roto.
        if (nieve > 0.1f) {
            val az = fijo("pie")
            val arriba = rota(PIE.map { q -> q + Offset(0f, -(4f + 10f * nieve) * (0.6f + 0.8f * az.real())) }, az, 1.6f, 3)
            val apron = poligono(arriba + PIE.reversed().map { it + Offset(0f, 2f) })
            drawPath(apron, p.por(C.NIEVE, sol.t(V3(0.1f, 0.8f, -0.6f)) + 0.15f, 0.06f).copy(alpha = (nieve * 1.3f).coerceAtMost(1f)))
            drawPath(Path().apply { moveTo(arriba[0]); for (q in arriba.drop(1)) lineTo(q) }, p.luz(C.NIEVE, 0.06f).copy(alpha = 0.7f * nieve), style = Stroke(0.7f))
        }
        // De día, el rebaño pace en la ladera del hombro: «las ovejas, por las laderas del valle». Apretado, para que se
        // lea como una mancha de lomos claros y no como puntos sueltos.
        val fuera = ValleMongol.ovejasEnLaLadera(m)
        if (fuera > 0) {
            val az = Azar(Semilla.de("mongol", "valle", "ladera", m.ficha.dia))
            val centro = az.entre(206f, 236f)
            val sitios = (0 until fuera).map { i ->
                val x = centro + az.entre(-1f, 1f) * (6f + 0.7f * i) + (if (i % 3 == 0) 8f else 0f)
                val y0 = alturaEn(HOMBRO, x) + 10f; val y1 = alturaEn(PIE, x) - 7f
                Offset(x, (y0 + (y1 - y0) * (0.55f + az.entre(-0.18f, 0.18f) * (0.5f + i / fuera.toFloat()))).coerceIn(y0, maxOf(y0, y1)))
            }
            rebano(p, sitios, { y -> 3.4f + (y - 128f) * 0.05f }, az, { 0.08f }, pastan = 0.85f)
        }
    }

    /**
     * Un plano del cerro: su [forma], su [cresta], sus aristas y cárcavas ([lineas], de izquierda a derecha), sus
     * afloramientos de roca, el color de su hierba seca y lo lejos que está.
     */
    private fun DrawScope.plano(p: Pincel, sol: Sol3, nieve: Float, forma: Path, cresta: List<Offset>, lineas: List<Linea>, penas: List<Pena>, hierba: Color, lejos: Float, az: Azar) {
        val l = p.l
        val cerca = lejos < 0.08f
        // Las líneas, rotas a mano; cada cara se alarga por abajo (el plano la recorta).
        val rotas = lineas.map { li -> if (li.puntos.size > 2) rota(li.puntos, az, 1.4f, 2) else li.puntos }
        fun larga(r: List<Offset>): List<Offset> { val d = r.last() - r[r.size - 2]; return r + (r.last() + d / d.getDistance().coerceAtLeast(0.01f) * 90f) }
        val piel = lerp(hierba, C.NIEVE, 0.08f * nieve)
        val hacia = if (p.solALaDerecha) -1f else 1f
        drawPath(forma, p.medio(piel, lejos))
        clipPath(forma) {
            // Cada cara entre dos líneas, con su luz: de la arista a la cárcava, a lo ancho de la cara.
            for (i in 0 until rotas.size - 1) {
                val a = larga(rotas[i]); val b = larga(rotas[i + 1])
                val arriba = cresta.filter { it.x > a.first().x && it.x < b.first().x }
                val cara = poligono(a + b.reversed() + arriba.reversed())
                // La luz de la cara, con el contraste estirado: la que da la espalda al sol se va de verdad a la sombra azul.
                val n = lineas[i].cara; val t = ((sol.t(n) - 0.35f) * 1.6f).coerceIn(-1f, 1f)
                val claro = if (lineas[i].espolon) 0.18f else -0.18f
                drawPath(cara, Brush.linearGradient(0f to p.por(piel, t + claro, lejos), 1f to p.por(piel, t - claro, lejos), start = en(a, 0.25f), end = en(b, 0.25f)))
                // El manto de nieve de la cara: abajo, con el borde de arriba roto y lenguas que suben. Se queda más en las
                // caras que miran a la izquierda **[Aproximación]**: las que menos ven el sol del mediodía.
                if (nieve > 0.08f && n.y < 0.9f) {
                    val ra = rotas[i]; val rb = rotas[i + 1]
                    val desde = when { n.x < -0.3f -> 0.12f + 0.75f * (1f - nieve); n.x > 0.3f -> 0.5f + 0.6f * (1f - nieve); else -> 0.5f + 0.7f * (1f - nieve) }
                    if (desde < 0.97f) clipPath(cara) {
                        val fa = (desde + az.entre(-0.08f, 0.08f)).coerceIn(0.02f, 0.98f); val fb = (desde + az.entre(-0.08f, 0.08f)).coerceIn(0.02f, 0.98f)
                        // Los costados bajan siguiendo las dos líneas, pero apartados del espolón: el viento barre los lomos.
                        fun lado(r1: List<Offset>, r2: List<Offset>, f0: Float, aparta: Boolean) = (0..6).map { k -> val f = f0 + (1f - f0) * k / 6f; val u = if (aparta) az.entre(0.14f, 0.38f) else az.entre(0f, 0.05f); en(r1, f) + (en(r2, f) - en(r1, f)) * u }
                        val izq = lado(ra, rb, fa, lineas[i].espolon); val der = lado(rb, ra, fb, lineas[i + 1].espolon)
                        val qa = izq[0]; val qb = der[0]; val ancho = (qb - qa).getDistance()
                        val borde = (0..8).map { k -> val u = k / 8f; qa + (qb - qa) * u + Offset(0f, az.entre(-1.2f, 1.6f) * (2f + ancho * 0.05f) - (if (k in 2..6 && az.real() < 0.22f) az.entre(3f, 8f) * (0.4f + nieve) else 0f)) }
                        // El borde de arriba, blando (la nieve se amontona redonda); los costados, rotos.
                        val manto = curva(borde).apply { for (q in der.drop(1)) lineTo(q); lineTo(der.last() + Offset(0f, 120f)); lineTo(izq.last() + Offset(0f, 120f)); for (q in izq.drop(1).reversed()) lineTo(q); close() }
                        drawPath(manto, p.por(C.NIEVE, t + 0.1f, lejos))
                        // Calvas de hierba dentro del manto, y el filo del borde.
                        repeat((3 + ancho / 10f).toInt()) { val c = qa + (qb - qa) * az.entre(0.1f, 0.9f) + Offset(0f, az.entre(6f, 40f)); val w = az.entre(2f, 6f) * (if (cerca) 1.3f else 1f); drawPath(borron(c, w, w * 0.32f, az, 7, 0.4f), p.por(piel, t, lejos).copy(alpha = 0.85f)) }
                        drawPath(curva(borde), p.sombra(piel, lejos).copy(alpha = 0.45f), style = Stroke(0.6f))
                    }
                }
            }
            // Las cárcavas: el pliegue hondo, en sombra del lado contrario al sol, con una rama que se abre a media ladera
            // y la nieve dentro: una lengua que se ensancha hacia abajo, de borde roto.
            for ((i, li) in lineas.withIndex()) {
                if (li.espolon || li.puntos.size < 3) continue
                val r = rotas[i]
                val ancho = if (cerca) 3f else 1.8f
                val pliegue = poligono(r + r.reversed().mapIndexed { k, q -> q + Offset(hacia * ancho * (0.4f + 0.6f * (r.size - 1 - k) / r.size), 1f) })
                drawPath(pliegue, p.sombra(lerp(piel, C.TIERRA, 0.5f * (1f - nieve)), lejos).copy(alpha = 0.8f))
                val brote = en(r, az.entre(0.35f, 0.6f)); val rama = rota(listOf(brote, brote + Offset(-hacia * az.entre(5f, 9f), az.entre(-9f, -5f)), brote + Offset(-hacia * az.entre(8f, 14f), az.entre(-16f, -11f))), az, 0.8f, 2)
                drawPath(Path().apply { moveTo(rama[0]); for (q in rama.drop(1)) lineTo(q) }, p.sombra(piel, lejos).copy(alpha = 0.65f), style = Stroke(ancho * 0.5f, cap = StrokeCap.Round))
                if (nieve > 0.12f) {
                    // Una cinta de ancho desigual (no un triángulo), de bordes blandos, que arranca un poco bajo la cresta.
                    val k0 = r.size
                    val izq = r.mapIndexed { k, q -> val w = ancho * (0.35f + 0.55f * nieve) * (0.75f + 0.4f * k / k0) * az.entre(0.55f, 1.35f); q + Offset(-w, 0.4f) }.drop(1)
                    val der = r.mapIndexed { k, q -> val w = ancho * (0.35f + 0.55f * nieve) * (0.75f + 0.4f * k / k0) * az.entre(0.55f, 1.35f); q + Offset(w * 0.8f, 0.4f) }.drop(1)
                    drawPath(curva(izq + der.reversed()).apply { close() }, p.por(C.NIEVE, sol.t(V3(-hacia * 0.4f, 0.6f, -0.7f)) + 0.1f, lejos))
                    drawPath(curva(izq + r.drop(1).reversed()).apply { close() }, p.sombra(C.NIEVE, lejos).copy(alpha = 0.5f))
                    drawPath(Path().apply { val rr = rama.map { it + Offset(0f, 0.4f) }; moveTo(rr[0]); for (q in rr.drop(1)) lineTo(q) }, p.por(C.NIEVE, 0.1f, lejos).copy(alpha = 0.85f * nieve), style = Stroke(ancho * 0.35f, cap = StrokeCap.Round))
                }
            }
            // Los espolones: un filo de luz del lado del sol.
            for ((i, li) in lineas.withIndex()) {
                if (!li.espolon) continue
                val r = rotas[i]
                drawPath(Path().apply { moveTo(r[0]); for (q in r.drop(1)) lineTo(q) }, p.luz(lerp(piel, C.FIELTRO, 0.3f), lejos).copy(alpha = 0.25f + 0.4f * p.r.dia), style = Stroke(if (cerca) 1.1f else 0.7f, cap = StrokeCap.Round))
            }
            // La hierba seca: pinceladas cortas del color de la piel, con su luz y su sombra; más grandes abajo.
            val y0 = cresta.minOf { it.y }
            val claros = ArrayList<Offset>(); val oscuros = ArrayList<Offset>()
            repeat(if (cerca) 420 else 300) { val q = Offset(az.entre(100f, 320f), az.entre(y0, 214f)); if (az.real() < 0.5f) claros += q else oscuros += q }
            drawPoints(claros, PointMode.Points, p.luz(lerp(piel, C.ESTEPA, 0.5f), lejos).copy(alpha = 0.45f), if (cerca) 1f else 0.7f, StrokeCap.Round)
            drawPoints(oscuros, PointMode.Points, p.sombra(lerp(piel, C.CUERO, 0.4f), lejos).copy(alpha = 0.4f), if (cerca) 1f else 0.7f, StrokeCap.Round)
            // Las manchas de la hierba: más rubia en los lomos, más parda en las vaguadas, a pinceladas tendidas.
            repeat(if (cerca) 22 else 16) {
                val x = az.entre(110f, 316f); val yA = alturaEn(cresta, x) + 3f; val yB = alturaEn(PIE, x) - 3f; if (yB <= yA) return@repeat
                val q = Offset(x, az.entre(yA, yB)); val w = az.entre(5f, 14f) * (if (cerca) 1.3f else 1f)
                drawPath(borron(q, w, w * 0.22f, az, 8, 0.35f), (if (it % 2 == 0) p.luz(lerp(piel, C.ESTEPA, 0.6f), lejos) else p.sombra(lerp(piel, C.CUERO, 0.3f), lejos)).copy(alpha = 0.35f * (1f - 0.5f * nieve)))
            }
            // Las matas de espino: bultos oscuros con su luz y su sombra en el suelo.
            repeat(if (cerca) 26 else 14) {
                val x = az.entre(if (cerca) 150f else 200f, if (cerca) 278f else 314f); val yA = alturaEn(cresta, x) + 6f; val yB = alturaEn(PIE, x) - 5f; if (yB <= yA) return@repeat
                val q = Offset(x, az.entre(yA, yB)); val w = (1.4f + (q.y - 110f) * 0.03f).coerceAtLeast(0.9f) * (if (cerca) 1f else 0.7f)
                // Una mata de varas: ramitas que salen de un punto, oscuras, con alguna clara del lado del sol; su sombra tendida.
                drawOval(p.r.color.copy(alpha = 0.4f * p.r.fuerza), q + Offset(-w + p.r.ax.coerceIn(-2f, 2f) * w * 0.6f, -w * 0.15f), Size(w * 2.6f, w * 0.5f), blendMode = BlendMode.Multiply)
                for (j in 0..6) { val punta = q + Offset(az.entre(-1f, 1f) * w, -w * az.entre(0.6f, 1.3f)); drawLine((if (j % 3 == 0) p.luz(lerp(C.CUERO, C.ESTEPA, 0.4f), lejos) else p.por(lerp(C.CUERO, C.HOLLIN, 0.3f), -0.3f, lejos)), q, punta, (0.18f * w).coerceIn(0.3f, 0.8f), StrokeCap.Round) }
            }
            // Los afloramientos de roca rota en estratos, con su canchal.
            for (pe in penas) pena(p, pe, nieve, lejos, az)
        }
        // La cresta, recortada contra el cielo (o contra la cima): un filo de luz, o de luna.
        val filo = if (l.noche > 0.4f) C.LUNA.copy(alpha = 0.4f * l.noche * (1f - l.nublado)) else p.luz(lerp(piel, C.FIELTRO, 0.5f), lejos).copy(alpha = 0.6f)
        drawPath(curva(cresta), filo, style = Stroke(if (cerca) 1.1f else 0.8f))
        // La hierba que asoma por la cresta.
        for (i in cresta.indices step 2) { val q = cresta[i]; for (j in 0..1) drawLine(p.por(hierba, -0.2f, lejos).copy(alpha = 1f - 0.6f * nieve), q + Offset(j * 1.6f - 0.8f, 0.4f), q + Offset(j * 1.6f - 0.8f + az.entre(-0.6f, 0.6f), -1.5f), 0.5f) }
    }

    /**
     * Un afloramiento de roca: una masa quebrada con su cara de luz, su cara de sombra y su tapa (con nieve si la hay),
     * las rayas de los estratos y, si tiene, el canchal que cae en abanico debajo: piedras sueltas más claras que la hierba.
     */
    private fun DrawScope.pena(p: Pincel, pe: Pena, nieve: Float, lejos: Float, az: Azar) {
        val w = pe.ancho; val h = w * az.entre(0.45f, 0.65f); val c = pe.pie; val lado = if (p.solALaDerecha) 1f else -1f
        val roca = lerp(C.PIEDRA, C.HOLLIN, 0.25f); val clara = lerp(C.PIEDRA, C.FIELTRO, 0.2f)
        if (pe.canchal) {
            val fan = poligono(listOf(c + Offset(-w * 0.3f, 0f), c + Offset(w * 0.3f, 0f), c + Offset(w * 0.9f + az.entre(0f, 3f), w * 1.6f), c + Offset(0f, w * 1.9f), c + Offset(-w * 0.8f, w * 1.5f)))
            drawPath(fan, p.por(lerp(clara, C.ESTEPA, 0.3f), 0.1f, lejos).copy(alpha = 0.55f))
            val piedras = (0 until (w * 2.2f).toInt()).map { val f = az.real(); c + Offset(az.entre(-1f, 1f) * (w * 0.3f + f * w * 0.7f), 1f + f * w * 1.6f) }
            drawPoints(piedras, PointMode.Points, p.sombra(roca, lejos).copy(alpha = 0.6f), 0.6f + w * 0.05f, StrokeCap.Round)
            drawPoints(piedras.map { it + Offset(lado * 0.25f, -0.3f) }, PointMode.Points, p.luz(clara, lejos), 0.4f + w * 0.035f, StrokeCap.Round)
        }
        // La masa: baja y quebrada, con dos o tres jorobas, hundida en la ladera (sin base plana).
        val h2 = h * 0.75f
        val borde = listOf(
            c + Offset(-w * 0.55f, h2 * 0.15f), c + Offset(-w * 0.5f, -h2 * az.entre(0.3f, 0.5f)), c + Offset(-w * 0.32f, -h2 * az.entre(0.7f, 0.95f)), c + Offset(-w * 0.16f, -h2 * az.entre(0.55f, 0.75f)),
            c + Offset(w * 0.02f, -h2), c + Offset(w * 0.2f, -h2 * az.entre(0.6f, 0.85f)), c + Offset(w * 0.38f, -h2 * az.entre(0.5f, 0.75f)), c + Offset(w * 0.56f, -h2 * 0.2f), c + Offset(w * 0.5f, h2 * 0.2f),
            c + Offset(w * 0.1f, h2 * 0.32f), c + Offset(-w * 0.25f, h2 * 0.26f),
        )
        // La sombra que echa en la ladera, tendida del lado contrario al sol.
        drawPath(poligono(listOf(borde[0], borde[8], borde[8] + Offset(-lado * w * 0.5f, h2 * 0.25f), borde[0] + Offset(-lado * w * 0.6f, h2 * 0.35f))), p.r.color.copy(alpha = 0.45f * p.r.fuerza), blendMode = BlendMode.Multiply)
        drawPath(poligono(borde), p.por(roca, -0.5f, lejos))
        // La cara del lado del sol: de la joroba más alta hacia ese lado.
        val luz = if (lado > 0f) poligono(listOf(borde[4], borde[5], borde[6], borde[7], borde[8], c + Offset(w * 0.12f, h2 * 0.3f), c + Offset(w * 0.06f, -h2 * 0.4f))) else poligono(listOf(borde[4], borde[3], borde[2], borde[1], borde[0], c + Offset(-w * 0.2f, h2 * 0.25f), c + Offset(-w * 0.02f, -h2 * 0.4f)))
        drawPath(luz, p.por(lerp(roca, clara, 0.5f), 0.6f, lejos))
        // Los estratos: rayas tendidas, cortas y rotas; y las grietas.
        for (k in 1..2) { val y = -h2 * 0.28f * k; drawLine(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.45f), c + Offset(-w * az.entre(0.3f, 0.45f), y), c + Offset(w * az.entre(0.05f, 0.4f), y + az.entre(-0.4f, 0.4f)), 0.35f + 0.02f * w) }
        drawLine(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.55f), borde[4], c + Offset(lado * w * 0.08f, h2 * 0.3f), 0.4f)
        // La nieve: solo en las repisas de arriba, a trozos.
        if (nieve > 0.15f) for (j in listOf(2, 4, 6)) if (az.real() < 0.75f) { val q = borde[j]; drawPath(borron(q + Offset(0f, w * 0.03f), w * az.entre(0.1f, 0.17f), w * 0.04f + 0.3f, az, 6, 0.3f, 0.6f), p.por(C.NIEVE, 0.5f, lejos).copy(alpha = (0.4f + 0.6f * nieve).coerceAtMost(1f))) }
    }
}
