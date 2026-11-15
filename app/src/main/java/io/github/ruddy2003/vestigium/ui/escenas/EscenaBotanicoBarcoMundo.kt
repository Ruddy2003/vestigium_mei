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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.H
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.Vista
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Costa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El mundo del bergantín: el cielo, la costa del tramo del viaje y el mar ([BarcoBotanico], cuadro A de Botánico).
 *
 * **La costa** ([EscenaBotanico.costa]) se arma por planos, como la lejanía de Helénico (regla 10): una sierra lejana y
 * azulada, la bruma, unos cerros o morros con su cara de luz y su cara de sombra y lo que crece en ellos (selva, monte
 * seco, bosque oscuro, nieve), y delante una punta baja con su roca, su playa y su rompiente; si hay puerto
 * ([EscenaBotanico.puerto]), el pueblo en hileras que suben, con su iglesia, sus barcas fondeadas y, de noche, sus
 * ventanas. Fondeado, la costa está más cerca y es más grande que navegando. Nada sube por encima del horizonte en la
 * ventana de los astros (x de 262 a 338).
 *
 * **El mar** es la superficie grande (regla 4): hondo y oscuro bajo la raya clara del horizonte y claro cerca, con el
 * color de las aguas del tramo ([aguaDe]); la mar de fondo en filas de lomos que se agrandan al acercarse, con su
 * cara de sombra y su cresta de luz; los borreguillos con viento; la estela del sol o de la luna; el reflejo de la
 * costa; la espuma de la marcha que el barco abre por la amura y corre por el costado; y, por la tarde, la sombra del
 * barco en el agua de estribor.
 */
internal object MundoBarco {
    private val C = ColorBotanico

    /** El cielo, la costa y el mar; con tormenta, inclinados unos grados alrededor del horizonte (se mueve el mundo, no el barco). */
    fun DrawScope.mundo(v: Vista) {
        val g = EscenaBotanico.inclinacion(v.m)
        if (g == 0f) { cieloYMar(v); return }
        drawRect(v.l.alto, Offset.Zero, Size(ANCHO, ALTO))
        withTransform({ rotate(g, Offset(ANCHO / 2f, H)); scale(1.18f, 1.18f, Offset(ANCHO / 2f, H)) }) { cieloYMar(v) }
    }

    private fun DrawScope.cieloYMar(v: Vista) {
        val l = v.l; val semilla = v.m.ficha.semilla
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, v.m.ficha.clima, semilla)
        pajaros(v)
        costa(v)
        mar(v)
        if (v.m.ficha.clima == Clima.NIEBLA) nieblaLejos(v)
    }

    /** Las aves que siguen al barco («pájaros que siguen al barco»): unas pocas, planeando, distintas cada día. De día. */
    private fun DrawScope.pajaros(v: Vista) {
        val l = v.l; if (l.noche > 0.5f || EscenaBotanico.tormenta(v.m)) return
        val az = v.m.azar("pajaros"); val n = az.entero(4)
        repeat(n) {
            val x = az.entre(170f, 372f); val y = az.entre(18f, 92f)
            if (x in 262f..338f && y in 40f..112f) return@repeat
            val w = az.entre(3.5f, 7f); val ala = az.entre(-0.4f, 0.4f)
            val c = l.tono(lerp(C.BREA, C.ROCA, 0.4f), 0.35f)
            drawPath(Path().apply { moveTo(x - w, y - w * (0.18f + ala * 0.2f)); cubicTo(x - w * 0.6f, y - w * 0.36f, x - w * 0.2f, y - w * 0.22f, x, y); cubicTo(x + w * 0.2f, y - w * 0.22f, x + w * 0.6f, y - w * 0.36f, x + w, y - w * (0.18f - ala * 0.2f)) }, c, style = Stroke(0.9f, cap = StrokeCap.Round))
        }
    }

    // ================================================================ Las aguas de cada costa

    /** Los tres colores del agua de cada costa (el propio, el hondo y el de la orilla): con la costa cambia el agua. */
    internal fun aguaDe(costa: Costa): Triple<Color, Color, Color> = when (costa) {
        Costa.MAR_ABIERTO -> Triple(lerp(C.MAR, C.MAR_HONDO, 0.42f), lerp(C.MAR_HONDO, Color(0xFF173A58), 0.5f), lerp(C.MAR, C.ORILLA, 0.15f))
        Costa.TROPICAL -> Triple(lerp(C.MAR, C.HOJA, 0.08f), C.MAR_HONDO, C.ORILLA)
        Costa.VOLCANICA -> Triple(C.MAR, lerp(C.MAR_HONDO, C.BREA, 0.15f), lerp(C.ORILLA, C.MAR, 0.3f))
        Costa.ATOLON -> Triple(lerp(C.MAR, C.ORILLA, 0.3f), C.MAR, lerp(C.ORILLA, C.ARENA, 0.25f))
        Costa.PATAGONIA -> Triple(lerp(C.MAR, C.ARENA, 0.32f), lerp(C.MAR_HONDO, C.ROCA, 0.3f), lerp(C.ARENA, C.CUERO, 0.2f))
        Costa.FUEGO -> Triple(lerp(C.MAR_HONDO, C.ROCA, 0.28f), lerp(C.MAR_HONDO, C.BREA, 0.42f), lerp(C.MAR, C.ROCA, 0.4f))
        Costa.ANDES -> Triple(lerp(C.MAR, C.MAR_HONDO, 0.35f), C.MAR_HONDO, lerp(C.MAR, C.ROCA, 0.3f))
        Costa.COLINAS -> Triple(lerp(C.MAR, C.MAR_HONDO, 0.2f), C.MAR_HONDO, C.ORILLA)
        Costa.INGLESA -> Triple(lerp(C.MAR, C.ROCA, 0.38f), lerp(C.MAR_HONDO, C.ROCA, 0.32f), lerp(C.MAR, C.ROCA, 0.42f))
    }

    // ================================================================ La costa

    /** Lo que queda de la costa a la vista: más cerca y más alta fondeado que navegando. */
    private fun cerca(v: Vista): Float = if (EscenaBotanico.fondeado(v.m)) 1f else 0.72f

    /** Un perfil que no sube en la ventana de los astros (x de 262 a 338): ahí se queda a ras del horizonte. */
    private fun sinTaparAstros(pts: List<Offset>): List<Offset> = pts.map { q -> if (q.x in 258f..342f) Offset(q.x, maxOf(q.y, H - 1.2f - 0.8f * abs(sin(q.x * 0.31f)))) else q }

    private fun DrawScope.costa(v: Vista) {
        val az = EscenaBotanico.aguas(v.m, "costa")
        when (EscenaBotanico.costa(v.m)) {
            Costa.MAR_ABIERTO -> marAbierto(v, az)
            Costa.TROPICAL -> tropical(v, az)
            Costa.VOLCANICA -> volcanica(v, az)
            Costa.PATAGONIA -> patagonia(v, az)
            Costa.FUEGO -> fuego(v, az)
            Costa.ANDES -> andes(v, az)
            Costa.ATOLON -> atolon(v, az)
            Costa.COLINAS -> colinas(v, az)
            Costa.INGLESA -> inglesa(v, az)
        }
    }

    /** Qué hay en una ladera: selva, monte seco, bosque oscuro, roca pelada o hierba. */
    private enum class Manto { SELVA, SECO, BOSQUE, ROCA, HIERBA, NIEVE }

    /**
     * Un cerro o una sierra de perfil [cresta], hasta [base]: el cuerpo, la cara de sombra de cada pico (del lado contrario
     * al sol, azul violeta), la de luz (cálida), las quebradas que bajan, el [manto] que lo cubre y la bruma del pie.
     * [nieve], de 0 a 1: hasta dónde baja la nieve desde las cumbres.
     */
    private fun DrawScope.cerro(v: Vista, cresta: List<Offset>, base: Float, propio: Color, lejos: Float, az: Azar, manto: Manto, nieve: Float = 0f) {
        if (cresta.size < 3) return
        val l = v.l; val dia = v.r.dia * (1f - 0.7f * l.nublado)
        val forma = curva(cresta, base)
        val arriba = cresta.minOf { it.y }
        val c = l.tono(propio, lejos); val luz = l.tono(lerp(propio, Color(0xFFFFE6C0), 0.42f), lejos); val sombra = l.tono(mult(propio, Color(0xFF6A68A8), 0.75f), lejos)
        drawPath(forma, Brush.verticalGradient(listOf(lerp(c, luz, 0.3f * dia), c, lerp(c, l.bruma, 0.45f * lejos)), arriba, base))
        val cae = if (v.p.solALaDerecha) -1f else 1f
        clipPath(forma) {
            // La cara de sombra de cada pico: del pico baja por la cresta del lado contrario al sol y vuelve por una quebrada.
            for (i in 1 until cresta.size - 1) {
                val q = cresta[i]
                if (!(q.y <= cresta[i - 1].y && q.y <= cresta[i + 1].y)) continue
                var j = i; while (j + cae.toInt() in cresta.indices && cresta[j + cae.toInt()].y >= cresta[j].y - 0.2f) j += cae.toInt()
                val valle = cresta[j]; val h = base - q.y
                val pie = Offset(q.x + cae * h * az.entre(0.15f, 0.45f), base + 1f)
                val pts = ArrayList<Offset>(); pts += q
                var k = i; while (k != j) { k += cae.toInt(); pts += cresta[k] }
                pts += Offset(valle.x, base + 1f); pts += pie
                // La quebrada, quebrada: tres codos entre el pie y el pico.
                for (s in 3 downTo 1) { val t = s / 4f; pts += Offset(q.x + (pie.x - q.x) * t + az.entre(-1.5f, 1.5f) * h * 0.06f, q.y + (pie.y - q.y) * t) }
                drawPath(poligono(pts), sombra.copy(alpha = 0.25f + 0.5f * dia))
                // La cara de luz junto a la cresta del otro lado.
                drawPath(Path().apply { moveTo(q); lineTo(Offset(q.x - cae * h * 0.5f, q.y + h * 0.55f)); lineTo(Offset(q.x - cae * h * 0.15f, q.y + h * 0.7f)); close() }, luz.copy(alpha = 0.32f * dia))
            }
            // Las quebradas: rayas oscuras que bajan de la cresta, y el manto.
            val ancho = cresta.last().x - cresta.first().x
            repeat((ancho / 14f).toInt().coerceIn(2, 14)) {
                val x = az.entre(cresta.first().x, cresta.last().x); val y0 = alto(cresta, x)
                val largo = (base - y0) * az.entre(0.3f, 0.8f)
                drawPath(Path().apply { moveTo(x, y0 + 1f); cubicTo(x + az.entre(-2f, 2f), y0 + largo * 0.3f, x + cae * largo * 0.15f, y0 + largo * 0.7f, x + cae * largo * 0.25f, y0 + largo) }, sombra.copy(alpha = 0.35f * (1f - 0.5f * lejos)), style = Stroke(az.entre(0.5f, 1.3f) * (1.2f - lejos)))
            }
            when (manto) {
                Manto.SELVA, Manto.BOSQUE -> {
                    // Las copas, en masa: borbotones con su lado de luz arriba y su sombra debajo, más apretados abajo.
                    val verde = if (manto == Manto.SELVA) lerp(C.SELVA, C.HOJA, 0.25f) else C.SELVA_OSC
                    val vc = l.tono(verde, lejos); val vl = l.tono(lerp(verde, C.HOJA_LUZ, 0.5f), lejos); val vs = l.tono(mult(verde, Color(0xFF5A5C98), 0.6f), lejos)
                    repeat((ancho * 1.4f).toInt().coerceIn(20, 300)) {
                        val x = az.entre(cresta.first().x, cresta.last().x); val y0 = alto(cresta, x)
                        val f = az.real(); val y = y0 + 2f + (base - y0) * (1f - f * f)
                        val r = (1.1f + 1.6f * az.real()) * (1.15f - lejos)
                        drawOval(vs.copy(alpha = 0.7f), Offset(x - r * 1.1f, y - r * 0.4f), Size(r * 2.2f, r * 1.1f))
                        drawOval(vc, Offset(x - r, y - r * 0.9f), Size(r * 2f, r * 1.3f))
                        drawOval(vl.copy(alpha = 0.6f * dia), Offset(x - r * 0.6f - cae * r * 0.3f, y - r * 0.95f), Size(r * 1.1f, r * 0.6f))
                    }
                }
                Manto.SECO -> repeat((ancho * 0.8f).toInt().coerceIn(10, 160)) {
                    val x = az.entre(cresta.first().x, cresta.last().x); val y = alto(cresta, x) + az.entre(2f, base - alto(cresta, x)); val r = az.entre(0.5f, 1.3f)
                    drawOval(l.tono(lerp(C.SELVA, C.ARENA, 0.45f), lejos).copy(alpha = 0.7f), Offset(x - r, y - r * 0.5f), Size(r * 2f, r))
                }
                Manto.HIERBA -> repeat(6) { val x = az.entre(cresta.first().x, cresta.last().x); mancha(Offset(x, alto(cresta, x) + 6f), az.entre(10f, 30f), az.entre(2f, 4f), if (az.real() > 0.5f) luz else sombra, 0.25f) }
                Manto.ROCA -> repeat((ancho / 6f).toInt().coerceIn(4, 40)) {
                    val x = az.entre(cresta.first().x, cresta.last().x); val y = alto(cresta, x) + az.entre(1f, (base - alto(cresta, x)) * 0.8f)
                    drawLine(lerp(sombra, c, 0.3f).copy(alpha = 0.5f), Offset(x, y), Offset(x + az.entre(-2f, 2f), y + az.entre(2f, 6f)), az.entre(0.5f, 1f))
                }
                Manto.NIEVE -> Unit
            }
            if (nieve > 0f) {
                // La nieve: en las cumbres, en lenguas que bajan por las quebradas; azul en la cara de sombra.
                val blanca = l.tono(C.NIEVE, lejos); val azul = l.tono(lerp(C.NIEVE, Color(0xFF8A9CD0), 0.5f), lejos)
                for (i in 1 until cresta.size - 1) {
                    val q = cresta[i]; val h = base - q.y; if (h < 8f) continue
                    val baja = h * nieve * az.entre(0.25f, 0.55f)
                    val pts = listOf(q + Offset(-h * 0.22f, h * 0.06f), q + Offset(0f, -0.5f), q + Offset(h * 0.24f, h * 0.07f), q + Offset(h * 0.12f, baja * 0.6f), q + Offset(h * 0.04f, baja), q + Offset(-h * 0.06f, baja * 0.5f), q + Offset(-h * 0.14f, baja * 0.7f))
                    drawPath(curva(pts + pts.first()), blanca)
                    drawPath(poligono(listOf(q, q + Offset(cae * h * 0.24f, h * 0.07f), q + Offset(cae * h * 0.1f, baja * 0.8f))), azul)
                }
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.bruma.copy(alpha = (0.55f * lejos + 0.15f).coerceIn(0f, 1f))), arriba + (base - arriba) * 0.45f, base), Offset(cresta.first().x, arriba), Size(cresta.last().x - cresta.first().x, base - arriba + 2f))
        }
    }

    /** La altura de una cresta en [x] (interpolada). */
    private fun alto(cresta: List<Offset>, x: Float): Float {
        for (i in 0 until cresta.size - 1) { val a = cresta[i]; val b = cresta[i + 1]; if (x >= a.x && x <= b.x) return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x).coerceAtLeast(0.001f)) }
        return cresta.last().y
    }

    /** La bruma entre dos planos: una franja blanda a la altura [y], de [x0] a [x1]. */
    private fun DrawScope.bruma(v: Vista, x0: Float, x1: Float, y: Float, alto: Float, fuerza: Float = 1f) {
        val c = lerp(v.l.bruma, Color.White, 0.15f * (1f - v.l.noche))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, c.copy(alpha = 0.5f * fuerza), c.copy(alpha = 0.2f * fuerza)), y - alto, y + 1f), Offset(x0, y - alto), Size(x1 - x0, alto + 1f))
    }

    /**
     * La orilla de delante, de [x0] a [x1]: la franja de roca mojada o de playa al pie de la tierra, la espuma de la
     * rompiente en golpes sueltos y las peñas que la rompen, con su reflejo. [arena] dice si es playa.
     */
    private fun DrawScope.orilla(v: Vista, x0: Float, x1: Float, y: Float, az: Azar, roca: Color, arena: Boolean, lejos: Float = 0.5f) {
        val l = v.l; val dia = 1f - 0.6f * l.noche
        var x = x0
        while (x < x1) {
            val w = az.entre(4f, 16f); val xa = x; val xb = minOf(x + w, x1)
            val playa = arena && az.real() > 0.3f
            val c = if (playa) l.tono(lerp(C.ARENA, Color.White, 0.15f), lejos) else l.tono(lerp(roca, C.BREA, 0.25f), lejos)
            drawPath(poligono(listOf(Offset(xa, y - az.entre(0.4f, 1.4f)), Offset(xb, y - az.entre(0.4f, 1.4f)), Offset(xb + 0.6f, y + 0.9f), Offset(xa - 0.4f, y + 0.9f))), c)
            if (!playa && az.real() > 0.6f) { val px = az.entre(xa, xb); drawPath(poligono(Offset(px - 1.6f, y + 0.8f), Offset(px - 0.6f, y - 1.2f), Offset(px + 1.1f, y - 0.6f), Offset(px + 1.6f, y + 0.8f)), l.tono(roca, lejos)) }
            x = xb + az.entre(0f, 3f)
        }
        // La rompiente: la espuma en golpes, más blanca donde hay roca; y una segunda línea, más afuera.
        val blanco = l.tono(Color.White, lejos * 0.6f)
        x = x0
        while (x < x1) { val w = az.entre(2f, 9f); if (az.real() > 0.2f) drawLine(blanco.copy(alpha = az.entre(0.45f, 0.85f) * dia), Offset(x, y + 1.3f), Offset(minOf(x + w, x1), y + 1.5f), az.entre(0.7f, 1.3f), StrokeCap.Round); x += w + az.entre(1f, 6f) }
        repeat(((x1 - x0) / 12f).toInt()) { val px = az.entre(x0, x1); drawLine(blanco.copy(alpha = az.entre(0.2f, 0.5f) * dia), Offset(px, y + 3f), Offset(px + az.entre(3f, 9f), y + 3.2f), 0.6f, StrokeCap.Round) }
    }

    /**
     * Un pueblo en una ladera, de [x0] a [x1] con el pie en [base]: casas en tres hileras que suben (cada una más chica,
     * más pálida y un poco más arriba), con su lado de luz y de sombra, sus tejados, una iglesia con torre, unos árboles
     * delante; de noche, ventanas encendidas. [gris]: de piedra (la costa inglesa). [ruinas]: caído, con su polvo.
     */
    private fun DrawScope.pueblo(v: Vista, x0: Float, x1: Float, base: Float, casa: Float, lejos: Float, az: Azar, gris: Boolean = false, ruinas: Boolean = false) {
        val l = v.l; val solDer = v.p.solALaDerecha
        val pared = if (gris) lerp(C.ROCA, C.LONA, 0.5f) else C.CAL; val tejado = if (gris) lerp(C.ROCA, C.BREA, 0.35f) else C.TEJA
        for (fila in 2 downTo 0) {
            val esc = 1f - 0.18f * fila; val lj = lejos + 0.06f * fila
            var x = x0 + az.entre(0f, 4f) + fila * 3f
            val y = base - fila * casa * 1.15f
            while (x < x1 - fila * 5f) {
                val w = casa * esc * az.entre(0.7f, 1.3f); val h = casa * esc * az.entre(0.55f, 0.85f); val yy = y + az.entre(-0.6f, 0.6f)
                if (ruinas && az.real() > 0.35f) {
                    // Una casa caída: el muro roto en escalones y los escombros a su pie.
                    val alto = h * az.entre(0.3f, 0.7f)
                    drawPath(poligono(listOf(Offset(x, yy), Offset(x, yy - alto), Offset(x + w * 0.3f, yy - alto * 0.7f), Offset(x + w * 0.5f, yy - alto), Offset(x + w * 0.8f, yy - alto * 0.4f), Offset(x + w, yy))), l.tono(pared, lj))
                    drawOval(l.tono(lerp(pared, C.ARENA, 0.4f), lj), Offset(x - w * 0.1f, yy - 1f), Size(w * 1.2f, 2f))
                } else {
                    val lado = w * 0.3f
                    drawRect(l.tono(pared, lj), Offset(x, yy - h), Size(w, h))
                    drawRect(l.tono(mult(pared, Color(0xFF8688C6), 0.55f), lj), Offset(if (solDer) x else x + w - lado, yy - h), Size(lado, h))
                    drawPath(poligono(Offset(x - w * 0.06f, yy - h), Offset(x + w * 1.06f, yy - h), Offset(x + w * 0.62f, yy - h - casa * 0.32f * esc), Offset(x + w * 0.3f, yy - h - casa * 0.32f * esc)), l.tono(tejado, lj))
                    drawLine(l.tono(mult(tejado, Color(0xFF6A68A8), 0.5f), lj), Offset(x - w * 0.06f, yy - h + 0.3f), Offset(x + w * 1.06f, yy - h + 0.3f), 0.5f)
                    if (az.real() > 0.5f) drawRect(l.tono(lerp(pared, C.BREA, 0.6f), lj), Offset(x + w * 0.4f, yy - h * 0.55f), Size(maxOf(0.5f, w * 0.15f), maxOf(0.5f, h * 0.25f)))
                    if (l.noche > 0.35f && az.real() > 0.45f) drawRect(C.LUMBRE.copy(alpha = (0.9f * l.noche).coerceIn(0f, 1f)), Offset(x + w * 0.42f, yy - h * 0.6f), Size(maxOf(0.6f, w * 0.16f), maxOf(0.6f, h * 0.24f)))
                }
                x += w * az.entre(1.05f, 1.5f)
            }
        }
        // La iglesia: el cuerpo, la torre con su campanario y su chapitel, del lado alto del pueblo.
        val ix = x0 + (x1 - x0) * az.entre(0.35f, 0.65f); val iy = base - casa * 2.1f
        if (!ruinas) {
            drawRect(l.tono(pared, lejos), Offset(ix, iy - casa * 1.2f), Size(casa * 2.2f, casa * 1.2f))
            drawRect(l.tono(mult(pared, Color(0xFF8688C6), 0.5f), lejos), Offset(if (solDer) ix else ix + casa * 1.6f, iy - casa * 1.2f), Size(casa * 0.6f, casa * 1.2f))
            drawPath(poligono(Offset(ix - casa * 0.1f, iy - casa * 1.2f), Offset(ix + casa * 2.3f, iy - casa * 1.2f), Offset(ix + casa * 1.1f, iy - casa * 1.75f)), l.tono(tejado, lejos))
            val tx = ix + casa * 1.8f
            drawRect(l.tono(pared, lejos), Offset(tx, iy - casa * 3f), Size(casa * 0.75f, casa * 3f))
            drawRect(l.tono(mult(pared, Color(0xFF8688C6), 0.55f), lejos), Offset(if (solDer) tx else tx + casa * 0.45f, iy - casa * 3f), Size(casa * 0.3f, casa * 3f))
            drawRect(l.tono(lerp(pared, C.BREA, 0.6f), lejos), Offset(tx + casa * 0.25f, iy - casa * 2.6f), Size(casa * 0.25f, casa * 0.4f))
            drawPath(poligono(Offset(tx - casa * 0.05f, iy - casa * 3f), Offset(tx + casa * 0.8f, iy - casa * 3f), Offset(tx + casa * 0.38f, iy - casa * 3.9f)), l.tono(tejado, lejos))
        } else {
            // La torre partida y la nube de polvo que todavía cuelga.
            drawRect(l.tono(pared, lejos), Offset(ix + casa * 1.8f, iy - casa * 1.9f), Size(casa * 0.75f, casa * 1.9f))
            drawPath(poligono(Offset(ix + casa * 1.8f, iy - casa * 1.9f), Offset(ix + casa * 2.55f, iy - casa * 1.9f), Offset(ix + casa * 2.55f, iy - casa * 2.4f)), l.tono(pared, lejos))
            repeat(7) { mancha(Offset(az.entre(x0, x1), base - az.entre(2f, casa * 4f)), az.entre(6f, 16f), az.entre(2f, 5f), l.tono(lerp(C.ARENA, C.LONA, 0.3f), lejos), 0.35f) }
        }
        // Unos árboles oscuros delante del pueblo, que lo despegan del agua.
        repeat(((x1 - x0) / 8f).toInt()) { val x = az.entre(x0 - 4f, x1 + 4f); val r = az.entre(0.9f, 1.8f) * casa * 0.4f
            drawOval(l.tono(lerp(C.SELVA, C.HOJA, 0.15f), lejos - 0.05f), Offset(x - r, base - r * 1.4f), Size(r * 2f, r * 1.5f)) }
    }

    /** Unas barcas fondeadas frente al puerto: el casco oscuro, uno o dos palos con las velas aferradas, su reflejo. */
    private fun DrawScope.barcas(v: Vista, x0: Float, x1: Float, y: Float, cuantas: Int, az: Azar) {
        val l = v.l
        repeat(cuantas) {
            val x = az.entre(x0, x1); val yy = y + az.entre(1f, 5f); val u = 1f + (yy - y) * 0.15f; val w = az.entre(4f, 8f) * u
            val casco = l.tono(lerp(C.BREA, C.MADERA, 0.25f), 0.45f)
            drawPath(poligono(Offset(x - w / 2f, yy - 1.2f * u), Offset(x + w / 2f, yy - 1.5f * u), Offset(x + w * 0.38f, yy), Offset(x - w * 0.42f, yy)), casco)
            for (k in 0 until 1 + az.entero(2)) { val px = x - w * 0.15f + k * w * 0.3f; drawLine(l.tono(C.MADERA_OSC, 0.45f), Offset(px, yy - 1.2f * u), Offset(px, yy - (5f + 2f * az.real()) * u), 0.45f); drawLine(l.tono(C.LONA, 0.45f), Offset(px - 1.2f * u, yy - 4f * u), Offset(px + 1.2f * u, yy - 4.2f * u), 0.7f * u) }
            reflejoRoto(x - w / 2f, x + w / 2f, yy + 0.3f, 4f * u, casco, 0.35f, az, 0.7f)
            if (l.noche > 0.4f) drawCircle(C.LUMBRE.copy(alpha = 0.8f * l.noche), 0.6f, Offset(x + w * 0.3f, yy - 2.5f * u))
        }
    }

    // ---------------------------------------------------------------- Cada costa

    private fun DrawScope.marAbierto(v: Vista, az: Azar) {
        // Solo mar; a veces, un chubasco lejano que cuelga de una nube sobre el horizonte, o una vela muy lejos.
        if (az.real() > 0.5f && v.m.ficha.clima != Clima.TORMENTA) {
            val x = az.entre(40f, 200f)
            nube(v.l, x, H - 3f, az.entre(40f, 70f), az, carga = 0.45f, lejos = 0.75f, alza = 0.8f)
            for (k in 0..12) drawLine(v.l.tono(lerp(C.ROCA, C.LONA, 0.3f), 0.8f).copy(alpha = 0.16f), Offset(x + 6f + k * 4f, H - 5f), Offset(x + 2f + k * 4f, H + 0.5f), 1.4f)
        }
        val dia = v.m.azar("vela lejana")
        if (dia.real() > 0.6f && v.l.noche < 0.6f) { val x = dia.entre(345f, 372f); drawPath(poligono(Offset(x, H - 0.4f), Offset(x + 2.4f, H - 0.4f), Offset(x + 1.2f, H - 6f)), v.l.tono(C.LONA, 0.7f)); drawLine(v.l.tono(C.BREA, 0.7f), Offset(x - 0.6f, H), Offset(x + 3f, H), 0.8f) }
    }

    private fun DrawScope.tropical(v: Vista, az: Azar) {
        val l = v.l; val k = cerca(v); val fondeado = EscenaBotanico.fondeado(v.m)
        // Detrás, la sierra azul con su selva «hasta el cielo»; la bruma; los morros de granito; las lomas verdes; la punta y la playa.
        cerro(v, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "sierra tropical", az.entero(1000))), 110f, ANCHO + 30f, H + 1f, 40f * k, 0.6f, 34)), H + 2f, lerp(C.SELVA, Color(0xFF7A90A8), 0.55f), 0.85f, az, Manto.SELVA)
        bruma(v, 110f, ANCHO, H + 1f, 14f * k)
        morro(v, az.entre(196f, 232f), az.entre(30f, 42f) * k, az.entre(40f, 54f) * k, az.entre(-4f, 4f), 0.55f, az)
        cerro(v, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "lomas tropical", az.entero(1000))), 140f, 262f, H + 1f, 16f * k, 0.35f, 22, isla = true)), H + 2f, lerp(C.SELVA, C.HOJA, 0.3f), 0.5f, az, Manto.SELVA)
        morro(v, az.entre(346f, 366f), az.entre(26f, 34f) * k, az.entre(34f, 46f) * k, az.entre(-3f, 3f), 0.5f, az)
        cerro(v, perfil(Azar(Semilla.de("botanico", "punta tropical", az.entero(1000))), 318f, ANCHO + 20f, H + 1f, 10f * k, 0.3f, 14), H + 2f, lerp(C.SELVA, C.HOJA, 0.35f), 0.42f, az, Manto.SELVA)
        if (EscenaBotanico.puerto(v.m)) {
            // «Una ciudad de iglesias sobre una colina verde»: el pueblo blanco que sube por la loma, y las barcas.
            pueblo(v, az.entre(236f, 246f), az.entre(262f, 270f), H + 0.4f, 3.2f * k, 0.48f, az)
            barcas(v, 210f, 330f, H + 1f, if (fondeado) 5 else 2, az)
        }
        palmeras(v, 150f, 250f, H + 0.2f, az, 0.5f, 4)
        orilla(v, 140f, ANCHO, H + 1f, az, C.ROCA, true)
        // Por babor, lejos, otra punta de tierra.
        cerro(v, perfil(Azar(Semilla.de("botanico", "babor tropical", az.entero(1000))), -10f, 120f, H + 1f, 16f * k, 0.5f, 18, isla = true), H + 2f, lerp(C.SELVA, Color(0xFF7A90A8), 0.6f), 0.82f, az, Manto.SELVA)
    }

    /** Un morro de granito, como los de Río: casi vertical, liso, con regueros oscuros, su cara de sombra y una falda de selva. */
    private fun DrawScope.morro(v: Vista, cx: Float, ancho: Float, alto: Float, sesgo: Float, lejos: Float, az: Azar) {
        val base = H + 1f; val l = v.l; val dia = v.r.dia * (1f - 0.7f * l.nublado)
        val pts = listOf(Offset(cx - ancho / 2f, base), Offset(cx - ancho * 0.44f, base - alto * 0.3f), Offset(cx - ancho * 0.36f + sesgo, base - alto * 0.72f), Offset(cx - ancho * 0.22f + sesgo, base - alto * 0.94f),
            Offset(cx - ancho * 0.04f + sesgo, base - alto), Offset(cx + ancho * 0.14f + sesgo, base - alto * 0.95f), Offset(cx + ancho * 0.3f + sesgo, base - alto * 0.74f), Offset(cx + ancho * 0.4f, base - alto * 0.34f), Offset(cx + ancho / 2f, base))
        val forma = curva(pts, base + 1f)
        val roca = lerp(C.ROCA, C.TEJA, 0.14f)
        val lado = if (v.p.solALaDerecha) 1f else -1f
        drawPath(forma, Brush.horizontalGradient(listOf(l.tono(mult(roca, Color(0xFF6A68A8), 0.6f), lejos), l.tono(roca, lejos), l.tono(lerp(roca, Color(0xFFFFE6C0), 0.4f * dia), lejos)), cx - lado * ancho * 0.45f, cx + lado * ancho * 0.45f))
        clipPath(forma) {
            // La cara de sombra, de borde neto (el granito es liso): media cúpula del lado contrario al sol.
            drawPath(Path().apply { moveTo(cx + sesgo - lado * ancho * 0.02f, base - alto - 2f); cubicTo(cx - lado * ancho * 0.12f, base - alto * 0.6f, cx - lado * ancho * 0.18f, base - alto * 0.3f, cx - lado * ancho * 0.1f, base + 1f); lineTo(cx - lado * ancho, base + 1f); lineTo(cx - lado * ancho, base - alto - 2f); close() }, l.tono(mult(roca, Color(0xFF6A68A8), 0.7f), lejos).copy(alpha = 0.3f + 0.45f * dia))
            repeat(12) { val x = cx + az.entre(-0.42f, 0.42f) * ancho; drawLine(l.tono(lerp(roca, C.BREA, 0.45f), lejos).copy(alpha = 0.3f), Offset(x, base - alto * az.entre(0.55f, 0.98f)), Offset(x + az.entre(-1f, 1f), base - alto * az.entre(0.05f, 0.3f)), az.entre(0.4f, 1.2f)) }
            drawLine(l.tono(lerp(roca, Color(0xFFFFE6C0), 0.6f), lejos).copy(alpha = 0.5f * dia), Offset(cx + sesgo + lado * ancho * 0.05f, base - alto + 0.6f), Offset(cx + lado * ancho * 0.3f, base - alto * 0.72f), 1.1f)
            // La falda de selva, de borde rizado, que sube más por las quebradas.
            val falda = (0..18).map { val x = cx - ancho / 2f + ancho * it / 18f; Offset(x, base - alto * (0.16f + 0.18f * az.real() + 0.1f * sin(it * 1.3f))) }
            cerro(v, falda, base + 2f, lerp(C.SELVA, C.HOJA, 0.15f), lejos - 0.05f, az, Manto.SELVA)
        }
        reflejoRoto(cx - ancho * 0.4f, cx + ancho * 0.4f, base + 1.4f, alto * 0.25f, l.tono(roca, lejos), 0.18f, az, 0.8f)
    }

    /** Unas palmeras sueltas y en grupos sobre la orilla, de [x0] a [x1]. */
    private fun DrawScope.palmeras(v: Vista, x0: Float, x1: Float, y: Float, az: Azar, lejos: Float, grupos: Int) {
        val l = v.l
        repeat(grupos) {
            val gx = az.entre(x0, x1); if (gx in 258f..342f) return@repeat
            repeat(2 + az.entero(3)) {
                val px = gx + az.entre(-6f, 6f); val h = az.entre(5f, 10f); val inc = az.entre(-2.5f, 2.5f)
                drawPath(Path().apply { moveTo(px, y); cubicTo(px + inc * 0.2f, y - h * 0.4f, px + inc * 0.7f, y - h * 0.8f, px + inc, y - h) }, l.tono(lerp(C.ROCA, C.MADERA, 0.4f), lejos), style = Stroke(0.7f))
                val c = Offset(px + inc, y - h)
                for (kk in 0..6) { val a = -3.1f + kk * 0.52f + az.entre(-0.1f, 0.1f); val f = Offset(cos(a), sin(a) * 0.5f + 0.55f)
                    drawPath(Path().apply { moveTo(c); cubicTo(c + Offset(f.x * 2f, -1.2f), c + Offset(f.x * 3.4f, f.y * 1.5f - 0.6f), c + Offset(f.x * 4.2f, f.y * 3f)) }, l.tono(if (kk % 2 == 0) C.SELVA else lerp(C.SELVA, C.HOJA, 0.35f), lejos), style = Stroke(0.9f, cap = StrokeCap.Round)) }
            }
        }
    }

    private fun DrawScope.volcanica(v: Vista, az: Azar) {
        val l = v.l; val k = cerca(v)
        // Tahití y Mauricio, verdes; Cabo Verde, Santa Elena y las Galápagos, secas y negras.
        val tramo = EscenaBotanico.tramo(v.m)
        val verde = EscenaBotanico.lugar(v.m) == EscenaBotanico.Lugar.BERGANTIN && ((tramo.temporada == 4 && tramo.desde == "11-15") || (tramo.temporada == 5 && tramo.desde == "04-29"))
        val ladera = if (verde) lerp(C.SELVA, C.HOJA, 0.2f) else lerp(C.ROCA, C.CUERO, 0.3f)
        val lava = lerp(C.BREA, C.ROCA, 0.3f)
        // Detrás, otra isla azul; el cono grande con su cráter mellado; delante, un cono chico y la punta de lava.
        cerro(v, perfil(Azar(Semilla.de("botanico", "isla lejana", az.entero(1000))), 120f, 250f, H + 1f, 22f * k, 0.5f, 18, isla = true), H + 2f, lerp(ladera, Color(0xFF7A90A8), 0.55f), 0.86f, az, if (verde) Manto.SELVA else Manto.ROCA)
        bruma(v, 120f, ANCHO, H + 1f, 10f)
        val cx = az.entre(190f, 222f); val alto = az.entre(42f, 54f) * k; val ancho = az.entre(110f, 140f) * k
        val cono = listOf(Offset(cx - ancho / 2f, H + 1f), Offset(cx - ancho * 0.32f, H - alto * 0.38f), Offset(cx - ancho * 0.16f, H - alto * 0.8f), Offset(cx - ancho * 0.07f, H - alto), Offset(cx - ancho * 0.02f, H - alto * 0.9f),
            Offset(cx + ancho * 0.04f, H - alto * 0.88f), Offset(cx + ancho * 0.08f, H - alto * 0.98f), Offset(cx + ancho * 0.2f, H - alto * 0.62f), Offset(minOf(cx + ancho * 0.38f, 256f), H - alto * 0.2f), Offset(minOf(cx + ancho * 0.6f, 262f), H + 1f))
        cerro(v, cono, H + 2f, ladera, 0.6f, az, if (verde) Manto.SELVA else Manto.SECO)
        clipPath(curva(cono, H + 2f)) {
            // Las coladas de lava: lenguas oscuras que bajan del cráter y se abren al llegar abajo.
            repeat(5) { val x = cx + az.entre(-0.1f, 0.1f) * ancho; drawPath(Path().apply { moveTo(x, H - alto * 0.88f); cubicTo(x + az.entre(-8f, 8f), H - alto * 0.5f, x + az.entre(-14f, 14f), H - alto * 0.2f, x + az.entre(-22f, 22f), H + 1f) }, l.tono(lava, 0.58f).copy(alpha = 0.45f), style = Stroke(az.entre(1.5f, 4f))) }
        }
        cerro(v, perfil(Azar(Semilla.de("botanico", "cono chico", az.entero(1000))), 334f, ANCHO + 30f, H + 1f, 24f * k, 0.5f, 16, isla = true), H + 2f, lerp(ladera, lava, 0.3f), 0.5f, az, if (verde) Manto.SELVA else Manto.ROCA)
        // El acantilado de la orilla: roca negra con la capa blanca de conchas en medio (Praia), con su borde roto.
        val x0 = cx - ancho * 0.48f; val x1 = minOf(cx + ancho * 0.6f, 258f)
        val frente = (0..14).map { Offset(x0 + (x1 - x0) * it / 14f, H - 4.5f * k + az.entre(-0.8f, 0.8f)) }
        drawPath(curva(frente, H + 1f), l.tono(lava, 0.52f))
        clipPath(curva(frente, H + 1f)) {
            drawPath(Path().apply { moveTo(x0, H - 2.6f * k); for (i in 0..14) lineTo(Offset(x0 + (x1 - x0) * i / 14f, H - 2.6f * k + az.entre(-0.3f, 0.3f))); for (i in 14 downTo 0) lineTo(Offset(x0 + (x1 - x0) * i / 14f, H - 1.6f * k + az.entre(-0.3f, 0.3f))); close() }, l.tono(C.LONA, 0.52f))
            repeat(14) { val x = az.entre(x0, x1); drawLine(l.tono(C.BREA, 0.5f).copy(alpha = 0.5f), Offset(x, H - 4.5f * k), Offset(x + 0.4f, H), 0.5f) }
        }
        if (EscenaBotanico.puerto(v.m)) { pueblo(v, x0 + 6f, x0 + 46f, H - 4.5f * k, 2.8f * k, 0.5f, az); barcas(v, x0, x1, H + 1f, 3, az) }
        if (!verde) palmeras(v, x0, x1, H - 4f * k, az, 0.5f, 2) else palmeras(v, 140f, 258f, H, az, 0.5f, 4)
        orilla(v, x0 - 10f, ANCHO, H + 1f, az, lava, verde)
    }

    private fun DrawScope.patagonia(v: Vista, az: Azar) {
        val l = v.l; val k = cerca(v)
        // La costa baja: la meseta parda y larga, en dos planos, y las barrancas claras con sus capas, que caen al agua.
        cerro(v, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "meseta lejana", az.entero(1000))), 100f, ANCHO + 40f, H + 1f, 8f * k, 0.25f, 30)), H + 2f, lerp(C.ARENA, Color(0xFF8A90A8), 0.5f), 0.82f, az, Manto.SECO)
        if (az.real() > 0.35f) cerro(v, perfil(Azar(Semilla.de("botanico", "cerro", az.entero(1000))), 170f, 246f, H + 1f, 18f * k, 0.35f, 16, isla = true), H + 2f, lerp(C.ARENA, C.ROCA, 0.4f), 0.66f, az, Manto.SECO)
        bruma(v, 100f, ANCHO, H + 1f, 6f)
        val alto = az.entre(6f, 10f) * k
        val meseta = (0..32).map { val x = 116f + (ANCHO + 30f - 116f) * it / 32f; Offset(x, H - alto * (0.62f + 0.22f * sin(it * 0.55f + az.real()) + 0.16f * az.real())) }
        cerro(v, meseta, H + 2f, lerp(C.ARENA, C.CUERO, 0.3f), 0.55f, az, Manto.SECO)
        // La barranca: un frente claro de borde mordido, con sus capas y sus cárcavas.
        val arriba = meseta.map { Offset(it.x, it.y + alto * 0.35f + az.entre(-0.4f, 0.4f)) }
        val frente = curva(arriba, H + 1f)
        drawPath(frente, Brush.verticalGradient(listOf(l.tono(lerp(C.ARENA, C.LONA, 0.35f), 0.52f), l.tono(lerp(C.ARENA, C.CUERO, 0.25f), 0.52f)), H - alto, H))
        clipPath(frente) {
            for (j in 0..3) drawPath(curva(arriba.map { Offset(it.x, it.y + (H - it.y) * (0.25f + 0.2f * j) + az.entre(-0.2f, 0.2f)) }), l.tono(lerp(C.ARENA, C.CUERO, 0.55f), 0.52f).copy(alpha = 0.55f), style = Stroke(0.5f))
            repeat(16) { val x = az.entre(116f, ANCHO); drawLine(l.tono(mult(C.ARENA, Color(0xFF6A68A8), 0.6f), 0.52f).copy(alpha = 0.5f), Offset(x, H - alto * 0.7f), Offset(x + az.entre(-1f, 1f), H), az.entre(0.8f, 2f)) }
        }
        if (EscenaBotanico.puerto(v.m)) { pueblo(v, az.entre(176f, 196f), az.entre(236f, 256f), H - alto * 0.7f, 2.6f * k, 0.55f, az); barcas(v, 170f, 320f, H + 1f, 3, az) }
        orilla(v, 116f, ANCHO, H + 1f, az, lerp(C.ARENA, C.CUERO, 0.3f), true)
    }

    private fun DrawScope.fuego(v: Vista, az: Azar) {
        val l = v.l; val k = cerca(v)
        // Montañas negras y quebradas, con nieve en las cumbres y un glaciar que baja al mar; delante, el bosque oscuro.
        cerro(v, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "fuego lejos", az.entero(1000))), 100f, ANCHO + 30f, H + 1f, 54f * k, 0.7f, 13)), H + 2f, lerp(C.BREA, Color(0xFF6A7494), 0.55f), 0.78f, az, Manto.ROCA, nieve = 1f)
        bruma(v, 100f, ANCHO, H + 1f, 12f)
        val sierra = sinTaparAstros(perfil(Azar(Semilla.de("botanico", "fuego sierra", az.entero(1000))), 120f, ANCHO + 20f, H + 1f, 34f * k, 0.6f, 11))
        cerro(v, sierra, H + 2f, lerp(C.BREA, Color(0xFF5A6488), 0.4f), 0.55f, az, Manto.BOSQUE, nieve = 0.55f)
        // El glaciar: una lengua blanca y azulada que baja de un valle hasta el agua, con su frente cortado.
        val gx = az.entre(176f, 232f)
        drawPath(Path().apply { moveTo(gx - 5f, H - 28f * k); cubicTo(gx - 9f, H - 16f * k, gx - 15f, H - 6f * k, gx - 18f, H + 0.5f); lineTo(gx + 15f, H + 0.5f); cubicTo(gx + 11f, H - 8f * k, gx + 6f, H - 18f * k, gx + 4f, H - 28f * k); close() },
            Brush.horizontalGradient(listOf(l.tono(lerp(C.NIEVE, Color(0xFF8AB4D0), 0.45f), 0.5f), l.tono(C.NIEVE, 0.5f)), gx - 18f, gx + 15f))
        drawRect(l.tono(lerp(C.NIEVE, Color(0xFF6A9CC0), 0.55f), 0.48f), Offset(gx - 18f, H - 2.2f), Size(33f, 2.8f))
        repeat(6) { val x = az.entre(gx - 16f, gx + 13f); drawLine(l.tono(Color(0xFF6A9CC0), 0.48f).copy(alpha = 0.6f), Offset(x, H - 2.2f), Offset(x, H + 0.6f), 0.5f) }
        // El día que llegan, un humo en la orilla, de un fuego que no se ve.
        if (v.m.con("nieve")) humo(l, Offset(az.entre(300f, 330f), H), 30f, 3f, 2f, 0.6f, 0.2f, 0.5f, 3, 6, 0.7f)
        cerro(v, perfil(Azar(Semilla.de("botanico", "fuego babor", az.entero(1000))), -10f, 122f, H + 1f, 30f * k, 0.85f, 20, isla = true), H + 2f, lerp(C.BREA, Color(0xFF6A7494), 0.5f), 0.78f, az, Manto.BOSQUE, nieve = 0.6f)
        orilla(v, 120f, ANCHO, H + 1f, az, C.ROCA, false)
    }

    private fun DrawScope.andes(v: Vista, az: Azar) {
        val l = v.l; val k = cerca(v)
        // La cordillera nevada, alta y lejana; el volcán, un cono blanco; delante, los cerros secos de la costa con el puerto.
        cerro(v, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "cordillera", az.entero(1000))), 100f, ANCHO + 30f, H - 4f, 40f, 0.75f, 36)), H + 2f, lerp(C.ROCA, Color(0xFF8A9CC0), 0.5f), 0.84f, az, Manto.ROCA, nieve = 1.3f)
        val vx = az.entre(192f, 218f); val va = az.entre(48f, 58f)
        val cono = listOf(Offset(vx - 56f, H - 2f), Offset(vx - 32f, H - va * 0.42f), Offset(vx - 14f, H - va * 0.82f), Offset(vx - 5f, H - va), Offset(vx + 2f, H - va * 0.96f), Offset(vx + 6f, H - va), Offset(vx + 15f, H - va * 0.8f), Offset(vx + 30f, H - va * 0.44f), Offset(vx + 46f, H - va * 0.14f), Offset(vx + 54f, H - 2f))
        cerro(v, cono, H + 2f, lerp(C.ROCA, Color(0xFF8A9CC0), 0.35f), 0.74f, az, Manto.ROCA, nieve = 1.6f)
        // El humo del volcán; el día del fuego, la columna y, de noche, el resplandor rojo, la boca y la lava que baja.
        val e = EscenaBotanico.erupcion(v.m)
        if (e > 0f) {
            humo(l, Offset(vx, H - va), 64f, 16f, 2f, 0.3f, 0.6f, 0.4f, 7, 10, 0.5f + 0.5f * e)
            if (l.noche > 0.3f) {
                resplandor(Offset(vx, H - va), 46f, 0.95f * e * l.noche, Color(0xFFFF6A2A)); resplandor(Offset(vx, H - va - 18f), 30f, 0.5f * e * l.noche, Color(0xFFFF8A3A))
                for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(vx + s * 2f, H - va); cubicTo(vx + s * 6f, H - va * 0.65f, vx + s * 10f, H - va * 0.35f, vx + s * 16f, H - 8f) }, Color(0xFFFF7A30).copy(alpha = 0.85f * e), style = Stroke(1.4f))
                drawOval(Color(0xFFFFC46A).copy(alpha = 0.9f * e), Offset(vx - 4f, H - va - 1.2f), Size(8f, 2.4f))
            }
        } else mancha(Offset(vx + 6f, H - va - 4f), 10f, 3f, l.tono(C.LONA, 0.7f), 0.4f)
        bruma(v, 100f, ANCHO, H + 1f, 12f)
        val cerros = sinTaparAstros(perfil(Azar(Semilla.de("botanico", "cerros secos", az.entero(1000))), 120f, ANCHO + 20f, H + 1f, 20f * k, 0.5f, 28))
        cerro(v, cerros, H + 2f, lerp(C.ARENA, C.CUERO, 0.42f), 0.55f, az, Manto.SECO)
        if (EscenaBotanico.puerto(v.m)) { pueblo(v, az.entre(166f, 186f), az.entre(232f, 252f), H - 1f, 2.9f * k, 0.52f, az, ruinas = EscenaBotanico.ruinas(v.m)); barcas(v, 160f, 330f, H + 1f, 4, az) }
        if (EscenaBotanico.terremoto(v.m)) {
            // El terremoto: los cerros se desmoronan en derrumbes claros y el polvo sube en nubes pardas sobre la costa.
            val tz = v.m.azar("terremoto")
            repeat(7) { val x = tz.entre(130f, 372f); if (x in 258f..342f) return@repeat; val y = alto(cerros, x) + 1f; drawPath(poligono(Offset(x - 1.5f, y), Offset(x + 1.5f, y), Offset(x + 5f, H), Offset(x - 3f, H)), l.tono(lerp(C.ARENA, C.CUERO, 0.2f), 0.5f)) }
            repeat(12) { val x = tz.entre(130f, 372f); if (x in 262f..338f) return@repeat; mancha(Offset(x, H - tz.entre(3f, 22f)), tz.entre(10f, 26f), tz.entre(4f, 9f), l.tono(lerp(C.ARENA, C.CUERO, 0.35f), 0.55f), 0.4f) }
        }
        orilla(v, 120f, ANCHO, H + 1f, az, lerp(C.ROCA, C.ARENA, 0.3f), true)
    }

    private fun DrawScope.atolon(v: Vista, az: Azar) {
        val l = v.l
        // Un anillo de coral a flor de agua: la rompiente de fuera, la raya de arena y los grupos de palmeras; dentro, la laguna clara.
        val y0 = H + 0.4f
        drawRect(l.tono(lerp(C.ORILLA, C.ARENA, 0.45f), 0.45f), Offset(140f, y0 + 1.4f), Size(ANCHO - 140f, 2.4f))
        var x = 140f
        while (x < ANCHO) {
            val w = az.entre(16f, 50f)
            drawLine(l.tono(C.ARENA, 0.5f), Offset(x, y0), Offset(minOf(x + w, ANCHO), y0 - az.entre(0.2f, 0.6f)), 1.6f, StrokeCap.Round)
            if (x !in 254f..346f && az.real() > 0.25f) {
                // La masa oscura del monte bajo y, encima, las palmeras.
                drawOval(l.tono(C.SELVA_OSC, 0.55f), Offset(x + w * 0.1f, y0 - 3f), Size(w * 0.8f, 3.4f))
                palmeras(v, x, x + w, y0, az, 0.55f, 1)
            }
            x += w + az.entre(4f, 16f)
        }
        repeat(36) { val px = az.entre(140f, ANCHO); drawLine(l.tono(Color.White, 0.35f).copy(alpha = az.entre(0.45f, 0.85f) * (1f - 0.5f * l.noche)), Offset(px, y0 + 3.4f), Offset(px + az.entre(3f, 10f), y0 + 3.6f), 1f, StrokeCap.Round) }
    }

    private fun DrawScope.colinas(v: Vista, az: Azar) {
        val l = v.l; val k = cerca(v)
        val tramo = EscenaBotanico.tramo(v.m)
        val cabo = tramo.temporada == 5 && tramo.desde == "05-31"
        val pardo = tramo.temporada == 5 && tramo.desde == "01-12"                  // Australia: colinas pardas y eucaliptos
        val verde = if (pardo) lerp(C.ARENA, C.SELVA, 0.35f) else lerp(C.HOJA, C.SELVA, 0.45f)
        cerro(v, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "colinas lejos", az.entero(1000))), 100f, ANCHO + 30f, H + 1f, 26f * k, 0.35f, 28)), H + 2f, lerp(verde, Color(0xFF7A90A8), 0.5f), 0.84f, az, Manto.BOSQUE)
        bruma(v, 100f, ANCHO, H + 1f, 10f)
        if (cabo) {
            // La montaña de la Mesa: un frente de roca con sus canales y la cima plana, con su mantel de nube.
            val x0 = az.entre(166f, 184f); val x1 = x0 + 74f
            val mesa = listOf(Offset(x0 - 18f, H + 1f), Offset(x0 - 6f, H - 24f), Offset(x0, H - 40f), Offset(x0 + 20f, H - 41f), Offset(x0 + 50f, H - 40.5f), Offset(x1, H - 41f), Offset(x1 + 8f, H - 22f), Offset(x1 + 20f, H + 1f))
            cerro(v, mesa, H + 2f, lerp(C.ROCA, Color(0xFF7A90A8), 0.3f), 0.68f, az, Manto.ROCA)
            mancha(Offset(x0 + 37f, H - 41f), 42f, 3.5f, l.tono(Color.White, 0.4f), 0.8f)
            mancha(Offset(x0 + 52f, H - 37f), 18f, 4f, l.tono(Color.White, 0.4f), 0.5f)
        }
        cerro(v, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "colinas cerca", az.entero(1000))), 126f, ANCHO + 20f, H + 1f, 15f * k, 0.3f, 28)), H + 2f, verde, 0.55f, az, if (pardo) Manto.SECO else Manto.BOSQUE)
        if (EscenaBotanico.puerto(v.m)) { pueblo(v, az.entre(214f, 230f), az.entre(250f, 258f), H, 2.7f * k, 0.52f, az); barcas(v, 190f, 330f, H + 1f, 3, az) }
        orilla(v, 126f, ANCHO, H + 1f, az, C.ROCA, true)
    }

    private fun DrawScope.inglesa(v: Vista, az: Azar) {
        val l = v.l; val k = cerca(v)
        // Los acantilados verdes y grises, los campos en damero con sus setos, el pueblo de piedra con su iglesia y el castillo en la punta.
        cerro(v, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "ingles lejos", az.entero(1000))), 100f, ANCHO + 30f, H + 1f, 12f * k, 0.25f, 26)), H + 2f, lerp(C.HOJA, Color(0xFF7A90A8), 0.55f), 0.84f, az, Manto.HIERBA)
        bruma(v, 100f, ANCHO, H + 1f, 6f)
        val tierra = (0..26).map { val x = 116f + (ANCHO + 40f - 116f) * it / 26f; Offset(x, H - (if (x in 258f..342f) 1.6f else (11f + 5f * sin(it * 0.5f + az.real())) * k)) }
        cerro(v, tierra, H + 2f, lerp(C.HOJA, C.SELVA, 0.4f), 0.56f, az, Manto.HIERBA)
        clipPath(curva(tierra, H + 2f)) {
            var x = 116f
            while (x < ANCHO) { val w = az.entre(9f, 20f); val y = alto(tierra, x)
                drawRect(l.tono(if (az.real() > 0.5f) lerp(C.HOJA, C.ARENA, 0.35f) else lerp(C.HOJA, C.SELVA, 0.5f), 0.58f), Offset(x, y + 2f), Size(w, (H - y - 6f).coerceAtLeast(0.5f)))
                drawLine(l.tono(C.SELVA_OSC, 0.55f), Offset(x, y + 2f), Offset(x + az.entre(-2f, 2f), H - 4f), 0.7f); x += w }
            // El acantilado: el frente gris con sus repisas.
            drawRect(l.tono(lerp(C.ROCA, C.LONA, 0.28f), 0.54f), Offset(116f, H - 4.5f * k), Size(ANCHO, 6f))
            repeat(20) { val xx = az.entre(116f, ANCHO); drawLine(l.tono(mult(C.ROCA, Color(0xFF6A68A8), 0.6f), 0.54f).copy(alpha = 0.6f), Offset(xx, H - 4.5f * k), Offset(xx + az.entre(-1f, 1f), H + 1f), az.entre(0.6f, 1.6f)) }
        }
        pueblo(v, az.entre(166f, 186f), az.entre(230f, 248f), H - 4f * k, 2.6f * k, 0.55f, az, gris = true)
        val cx = az.entre(346f, 362f)
        drawRect(l.tono(lerp(C.ROCA, C.LONA, 0.35f), 0.55f), Offset(cx, H - 17f * k), Size(8f, 6f)); drawRect(l.tono(lerp(C.ROCA, C.LONA, 0.25f), 0.55f), Offset(cx + 2f, H - 20f * k), Size(4f, 3f))
        drawRect(l.tono(mult(C.ROCA, Color(0xFF6A68A8), 0.5f), 0.55f), Offset(if (v.p.solALaDerecha) cx else cx + 5f, H - 17f * k), Size(3f, 6f))
        barcas(v, 180f, 330f, H + 1f, 4, az)
        orilla(v, 116f, ANCHO, H + 1f, az, C.ROCA, false)
    }

    // ================================================================ El mar

    /** La distancia del agua a los ojos (en metros) que queda a la altura [y] del lienzo: los ojos van a 4.2 m sobre el agua. */
    private fun yDelAgua(d: Float): Float = H + BarcoBotanico.F * 4.2f / d

    /**
     * El mar de estribor y el que asoma por babor: el color de las aguas del tramo, hondo bajo el horizonte y claro cerca;
     * las rachas y las calmas; la mar de fondo en filas; los borreguillos; la estela del sol o de la luna; el reflejo de
     * la costa; la espuma de la marcha y, con el sol de babor, la sombra del barco en el agua.
     */
    private fun DrawScope.mar(v: Vista) {
        val l = v.l; val m = v.m; val costa = EscenaBotanico.costa(m)
        val (propio, hondo, orilla) = aguaDe(costa)
        val marea = EscenaBotanico.marejada(m); val tormenta = EscenaBotanico.tormenta(m); val calma = EscenaBotanico.calma(m)
        val zona = Rect(-80f, H, ANCHO + 80f, ALTO + 40f)
        val lejosC = lerp(l.tono(hondo, 0.2f), l.bajo, 0.22f); val medioC = lerp(l.tono(propio), l.medio, 0.1f); val cercaC = lerp(l.tono(lerp(propio, orilla, 0.5f)), l.alto, 0.08f)
        drawRect(Brush.verticalGradient(0f to lerp(lejosC, l.bajo, 0.6f), 0.012f to lerp(lejosC, l.bajo, 0.25f), 0.05f to lejosC, 0.2f to lerp(lejosC, medioC, 0.55f), 0.5f to medioC, 1f to cercaC, startY = zona.top, endY = zona.bottom), zona.topLeft, zona.size)
        val az = m.azar("mar")
        // Las rachas del viento (más oscuras, rizadas) y las calmas (más claras, que reflejan el cielo), tendidas.
        repeat(9) { val pr = az.real(); val y = yDelAgua(18f + 900f * pr * pr * pr); mancha(Offset(az.entre(-40f, ANCHO + 40f), y), az.entre(40f, 150f) * (0.4f + (y - H) / 60f), 1f + (y - H) * 0.05f, l.tono(hondo), 0.22f + 0.1f * marea) }
        repeat(7) { val pr = az.real(); val y = yDelAgua(18f + 900f * pr * pr * pr); mancha(Offset(az.entre(-40f, ANCHO + 40f), y), az.entre(40f, 160f) * (0.4f + (y - H) / 60f), 0.8f + (y - H) * 0.04f, lerp(l.bajo, Color.White, 0.2f), if (calma) 0.4f else 0.16f) }
        costaEnElAgua(v, hondo)
        // La mar de fondo: filas de lomos que se agrandan al acercarse, cada uno con su cara de sombra y su cresta de luz.
        val oscuro = lerp(l.tono(hondo), Color(0xFF0C2238), 0.3f); val claro = lerp(l.tono(orilla), lerp(Color.White, l.solColor, 0.3f), 0.35f)
        val alza = if (calma) 0.15f else 0.45f + 1.2f * marea
        var d = 15f; var fila = 0
        while (d < 1600f) {
            val y = yDelAgua(d); val esc = (BarcoBotanico.F * 4.2f / d) / 40f              // 1 a unos 20 m
            var x = -60f + az.entre(0f, 40f) * esc
            while (x < ANCHO + 40f) {
                val w = (30f + 90f * esc) * az.entre(0.6f, 1.5f); val alto = (0.5f + 5f * esc) * alza * az.entre(0.6f, 1.2f); val yy = y + az.entre(-1f, 1f) * esc * 2f
                if (az.real() > 0.18f) {
                    drawPath(Path().apply { moveTo(x, yy); cubicTo(x + w * 0.3f, yy - alto, x + w * 0.62f, yy - alto * 1.1f, x + w, yy); cubicTo(x + w * 0.7f, yy + alto * 0.7f, x + w * 0.3f, yy + alto * 0.7f, x, yy); close() }, oscuro.copy(alpha = (0.12f + 0.12f * marea) * (0.6f + 0.4f * esc.coerceAtMost(1f))))
                    drawPath(Path().apply { moveTo(x + w * 0.1f, yy - alto * 0.3f); cubicTo(x + w * 0.33f, yy - alto * 1.0f, x + w * 0.6f, yy - alto * 1.08f, x + w * 0.9f, yy - alto * 0.24f) }, claro.copy(alpha = (0.16f + 0.2f * marea) * (1f - 0.55f * l.noche)), style = Stroke((0.4f + 1.1f * esc).coerceAtMost(2.2f), cap = StrokeCap.Round))
                    // Los borreguillos: la cresta que rompe en blanco cuando sopla.
                    if (marea > 0.42f && az.real() < 0.25f + 0.5f * (marea - 0.42f)) {
                        val cx = x + w * az.entre(0.35f, 0.62f); val bw = w * az.entre(0.12f, 0.22f)
                        drawPath(Path().apply { moveTo(cx - bw, yy - alto * 0.75f); cubicTo(cx - bw * 0.4f, yy - alto * 1.5f, cx + bw * 0.3f, yy - alto * 1.4f, cx + bw, yy - alto * 0.7f); cubicTo(cx + bw * 0.3f, yy - alto * 0.5f, cx - bw * 0.4f, yy - alto * 0.5f, cx - bw, yy - alto * 0.75f); close() }, l.tono(Color.White).copy(alpha = 0.75f * (1f - 0.5f * l.noche)))
                        drawLine(l.tono(Color.White).copy(alpha = 0.3f), Offset(cx - bw * 1.5f, yy - alto * 0.3f), Offset(cx + bw * 1.2f, yy - alto * 0.25f), (0.4f + 0.5f * esc).coerceAtMost(1.4f), StrokeCap.Round)
                    }
                }
                x += w * az.entre(0.9f, 1.4f)
            }
            d *= 1.32f; fila++
        }
        // Los rizos cortos entre los lomos.
        repeat(320) {
            val pr = az.real(); val y = yDelAgua(14f + 1400f * pr * pr * pr); val esc = ((y - H) / 40f).coerceIn(0.05f, 2.5f)
            val largo = (1.5f + 9f * esc) * az.entre(0.5f, 1.3f); val x = az.entre(-20f, ANCHO + 20f)
            val c = (if (az.real() > 0.5f) lerp(l.bajo, Color.White, 0.3f) else oscuro).copy(alpha = (0.1f + 0.15f * az.real()) * (1f + 0.5f * marea))
            drawPath(Path().apply { moveTo(x, y); cubicTo(x + largo * 0.3f, y - 0.6f * esc, x + largo * 0.7f, y - 0.6f * esc, x + largo, y + 0.2f) }, c, style = Stroke(0.45f + 0.5f * esc.coerceAtMost(1.5f), cap = StrokeCap.Round))
        }
        if (tormenta) olasGrandes(v, hondo, orilla)
        if (calma) {
            // La calma chicha: el agua como un plato, que copia el cielo en franjas largas y lisas.
            drawRect(Brush.verticalGradient(listOf(l.bajo.copy(alpha = 0.55f), l.medio.copy(alpha = 0.25f), Color.Transparent), H, H + 70f), Offset(-40f, H), Size(ANCHO + 80f, 70f))
            repeat(18) { val y = H + 2f + 80f * az.real() * az.real(); drawLine(lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.3f), Offset(az.entre(-40f, ANCHO), y), Offset(az.entre(-40f, ANCHO) + az.entre(60f, 200f), y), 0.5f + (y - H) * 0.02f) }
        }
        estela(l, zona, m.azar("estela"))
        calleDeLuz(v)
        // La sombra del barco en el agua de estribor, cuando el sol viene de babor: una franja más honda junto al costado.
        if (v.r.fuerza > 0.05f && v.sol.sx > 0.05f) {
            val pts = (0..20).map { val z = 2f + 24f * it / 20f; BarcoBotanico.pc(BarcoBotanico.estribor(z) + v.sol.sx * 2.8f, -2.6f, z + v.sol.sz * 2.8f) }
            val pegada = (0..20).map { val z = 2f + 24f * it / 20f; BarcoBotanico.pc(BarcoBotanico.estribor(z), -2.6f, z) }
            drawPath(poligono(pegada + pts.reversed()), Color(0xFF14304A).copy(alpha = 0.3f * v.r.fuerza), blendMode = BlendMode.Multiply)
        }
        espumaDeLaMarcha(v)
    }

    /**
     * La calle de luz del sol bajo (o de la luna) en el agua: trazos tendidos bajo el astro, estrechos y apretados junto al
     * horizonte y más abiertos y sueltos hacia quien mira, cálidos al alba y a la tarde, de plata con la luna.
     */
    private fun DrawScope.calleDeLuz(v: Vista) {
        val l = v.l
        val conSol = l.solAltura > -0.05f && l.solAltura < 0.75f && l.solFuerza > 0.3f && l.noche < 0.6f
        val conLuna = !conSol && v.luna > 0.08f
        if (!conSol && !conLuna) return
        val x0 = (if (conSol) l.solX else l.lunaX) * ANCHO
        val color = if (conSol) lerp(Color(0xFFFFF0C8), l.solColor, 0.45f) else EscenaBotanico.LUZ_DE_LUNA
        val fuerza = if (conSol) l.solFuerza * (1f - 0.8f * ((l.solAltura - 0.2f) / 0.55f).coerceIn(0f, 1f)) else 1.6f * v.luna
        val az = Azar(Semilla.de(v.m.ficha.semilla, "calle de luz"))
        repeat(150) {
            val pr = az.real(); val y = H + 1f + 110f * pr * pr
            val ancho = 6f + 70f * pr
            val u = (az.real() + az.real() - 1f)
            val x = x0 + u * ancho
            val largo = (1.5f + 9f * pr) * az.entre(0.5f, 1.3f)
            drawLine(color.copy(alpha = (0.7f * fuerza * (1f - 0.7f * kotlin.math.abs(u)) * az.entre(0.4f, 1f)).coerceIn(0f, 1f)), Offset(x - largo / 2f, y), Offset(x + largo / 2f, y), 0.5f + 0.8f * pr, StrokeCap.Round, blendMode = BlendMode.Screen)
        }
    }

    /**
     * La niebla por capas, no como un velo encima del cuadro (la de Horror cósmico): lo lejano (la costa, el horizonte, el
     * mar de lejos) se pierde del todo; el mar de cerca, a medias.
     */
    internal fun DrawScope.nieblaLejos(v: Vista) {
        val c = lerp(v.l.bruma, Color.White, 0.2f); val az = v.m.azar("niebla lejos")
        drawRect(Brush.verticalGradient(0f to c.copy(alpha = 0.35f), 0.45f to c.copy(alpha = 0.92f), 0.62f to c.copy(alpha = 0.7f), 1f to c.copy(alpha = 0.18f), startY = H - 60f, endY = H + 90f), Offset(0f, H - 60f), Size(ANCHO, 150f))
        repeat(10) { mancha(Offset(az.entre(-20f, ANCHO + 20f), az.entre(H - 30f, H + 60f)), az.entre(70f, 160f), az.entre(8f, 18f), lerp(c, Color.White, 0.2f), 0.45f) }
    }

    /** La niebla entre la proa y la popa: lo de la mitad del barco (el trinquete, los botes, el palo) se aclara; lo de cerca, no. */
    internal fun DrawScope.nieblaSobreLaProa(v: Vista) {
        val c = lerp(v.l.bruma, Color.White, 0.2f); val az = v.m.azar("niebla proa")
        drawRect(Brush.verticalGradient(0f to c.copy(alpha = 0.1f), 0.35f to c.copy(alpha = 0.55f), 0.6f to c.copy(alpha = 0.3f), 1f to Color.Transparent, startY = 0f, endY = H + 60f), Offset(0f, 0f), Size(ANCHO, H + 60f))
        repeat(6) { mancha(Offset(az.entre(0f, ANCHO), az.entre(H - 20f, H + 30f)), az.entre(60f, 130f), az.entre(6f, 14f), lerp(c, Color.White, 0.25f), 0.3f) }
    }

    /** Con tormenta, la mar gruesa: olas grandes que se levantan a estribor, con la cresta rota en espuma y los regueros por el lomo. */
    private fun DrawScope.olasGrandes(v: Vista, hondo: Color, orilla: Color) {
        val l = v.l; val az = v.m.azar("olas grandes")
        val oscuro = lerp(l.tono(hondo), Color(0xFF0C2238), 0.4f); val verde = l.tono(lerp(hondo, orilla, 0.45f)); val blanco = l.tono(Color.White)
        for ((x, y, w, alto) in listOf(listOf(140f, 126f, 120f, 9f), listOf(250f, 136f, 160f, 14f), listOf(330f, 124f, 110f, 8f), listOf(196f, 156f, 190f, 20f), listOf(306f, 172f, 170f, 26f))) {
            val ola = Path().apply { moveTo(x, y); cubicTo(x + w * 0.24f, y - alto * 0.6f, x + w * 0.4f, y - alto * 1.1f, x + w * 0.56f, y - alto); cubicTo(x + w * 0.68f, y - alto * 0.86f, x + w * 0.84f, y - alto * 0.3f, x + w, y); cubicTo(x + w * 0.7f, y + alto * 0.5f, x + w * 0.3f, y + alto * 0.5f, x, y); close() }
            drawPath(ola, Brush.verticalGradient(listOf(verde, oscuro.copy(alpha = 0.85f), oscuro.copy(alpha = 0f)), y - alto, y + alto * 0.5f))
            drawPath(Path().apply { moveTo(x + w * 0.3f, y - alto * 0.8f); cubicTo(x + w * 0.42f, y - alto * 1.34f, x + w * 0.54f, y - alto * 1.24f, x + w * 0.7f, y - alto * 0.74f); cubicTo(x + w * 0.58f, y - alto * 0.86f, x + w * 0.44f, y - alto * 0.9f, x + w * 0.3f, y - alto * 0.8f); close() }, blanco.copy(alpha = 0.85f))
            repeat(7) { val kk = az.entre(0.2f, 0.8f); val px = x + w * kk; val py = y - alto * (1f - abs(kk - 0.52f) * 1.9f).coerceAtLeast(0.1f) * 0.8f; drawPath(Path().apply { moveTo(px, py); cubicTo(Offset(px - 4f, py + 3f), Offset(px - 9f, py + 5f), Offset(px - az.entre(10f, 22f), py + az.entre(5f, 9f))) }, blanco.copy(alpha = 0.4f), style = Stroke(0.7f, cap = StrokeCap.Round)) }
            // El viento arranca la espuma de la cresta.
            repeat(5) { mancha(Offset(x + w * az.entre(0.4f, 0.7f) - az.entre(0f, 14f), y - alto * az.entre(1.1f, 1.6f)), az.entre(4f, 10f), az.entre(1f, 2.4f), blanco, 0.35f) }
        }
    }

    /** Lo que la costa deja en el agua: su reflejo roto bajo la orilla, más largo fondeado (el agua está más quieta). */
    private fun DrawScope.costaEnElAgua(v: Vista, hondo: Color) {
        val c = EscenaBotanico.costa(v.m); if (c == Costa.MAR_ABIERTO) return
        val color = v.l.tono(lerp(hondo, C.SELVA, 0.35f), 0.55f)
        reflejoRoto(110f, ANCHO, H + 2.4f, if (EscenaBotanico.fondeado(v.m)) 11f else 6f, color, 0.42f, EscenaBotanico.aguas(v.m, "reflejo"), 0.8f)
        reflejoRoto(-20f, 120f, H + 2.4f, 5f, color, 0.3f, EscenaBotanico.aguas(v.m, "reflejo babor"), 0.8f)
    }

    /**
     * La espuma de la marcha: el barco abre el agua por la amura y la deja correr por el costado hacia popa, en regueros
     * que se abren y se alejan del casco; asoma por encima de la regala de estribor. Fondeado, solo el agua que lame el casco.
     */
    private fun DrawScope.espumaDeLaMarcha(v: Vista) {
        val l = v.l; val m = v.m
        val navega = !EscenaBotanico.fondeado(m) && !EscenaBotanico.calma(m)
        val ez = Azar(Semilla.de("botanico", "espuma"))
        val viva = if (navega) 0.5f + 0.5f * EscenaBotanico.marejada(m) else 0.25f
        val blanco = l.tono(Color.White); val noche = 1f - 0.45f * l.noche
        // La ola de proa, que se abre en abanico desde la roda.
        if (navega) for (j in 0..5) {
            val pts = (0..14).map { i -> val z = 26f - 24f * i / 14f; val fuera = 0.4f + (26f - z) * (0.16f + 0.07f * j) + 0.3f * sin(z * 0.9f + j); BarcoBotanico.pc(BarcoBotanico.estribor(z) + fuera, -2.6f, z) }
            drawPath(curva(pts), blanco.copy(alpha = (0.5f - 0.07f * j) * viva * noche), style = Stroke((1.4f - 0.15f * j), cap = StrokeCap.Round))
        }
        repeat(if (navega) 110 else 40) {
            val z = ez.entre(2.5f, 26f); val fuera = ez.entre(0.1f, 1f) * (if (navega) 1f + (26f - z) * 0.5f else 0.6f)
            val q = BarcoBotanico.pc(BarcoBotanico.estribor(z) + fuera, -2.6f, z)
            val w = ez.entre(3f, 12f) * (BarcoBotanico.k(z) / 22f).coerceIn(0.3f, 2.4f)
            drawPath(Path().apply { moveTo(q); cubicTo(q + Offset(w * 0.3f, -0.6f), q + Offset(w * 0.7f, 0.6f), q + Offset(w, 0.2f)) }, blanco.copy(alpha = ez.entre(0.25f, 0.75f) * viva * noche), style = Stroke(ez.entre(0.5f, 1.5f) * (BarcoBotanico.k(z) / 30f).coerceIn(0.6f, 2.2f), cap = StrokeCap.Round))
        }
    }

    /** El color del agua a la altura [y], para lo que se refleja o asoma (lo usa la noche). */
    internal fun colorDelAgua(l: Luces, costa: Costa): Color = lerp(l.tono(aguaDe(costa).first), l.medio, 0.1f)
}
