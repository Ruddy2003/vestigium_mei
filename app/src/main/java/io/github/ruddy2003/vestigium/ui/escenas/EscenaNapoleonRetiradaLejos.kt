package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Tramo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.casaQuemada
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.hoguera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pino
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pinoAlbar
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaCamino.RUMBO
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaCamino.k
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaCamino.w
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.H
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.sin

/**
 * La lejanía de la retirada: el cielo de invierno, las lomas que se enciman con sus bosques como manchas, el humo de
 * otros fuegos y, en el llano de la derecha, lo de cada tramo ([TramosRetirada.lejania]):
 * - **los campos** de la salida, con un caserío de madera entero a lo lejos y, los dos primeros días, las ruinas de
 *   Moscú humeando a la derecha;
 * - **el pueblo quemado** del camino viejo, un campo de chimeneas («Los pueblos del camino de vuelta están quemados»),
 *   que el día de Maloyaroslavets arde;
 * - **el llano blanco** de la nieve, con la granja quemada y su humo;
 * - **las colinas de Krasnoi**, junto al camino, con el humo de los cañones de día;
 * - **el Berezina**, con el hielo suelto, los dos puentes de caballetes y las fogatas de la orilla; el día que queman
 *   los puentes, la columna de humo del puente;
 * - **el bosque cerrado** del final, al fondo de los dos lados.
 */
internal object RetiradaLejos {
    private val C = ColorNapoleon

    /** Las lomas lejanas, de la más honda a la más cercana: su altura sobre el horizonte y su semilla. */
    private val LOMAS = listOf(Triple(13f, 0.92f, "a"), Triple(9f, 0.8f, "b"), Triple(5.5f, 0.66f, "c"))

    /** La cresta de una loma: de lado a lado, ondulada, con su altura [alto] sobre el horizonte. */
    private fun cresta(alto: Float, semilla: String): List<Offset> {
        val az = Azar(Semilla.de("napoleon", "loma", semilla)); val f1 = az.entre(0f, 6f); val f2 = az.entre(0f, 6f)
        return (0..24).map { i -> val x = -20f + (ANCHO + 40f) * i / 24f; val u = i / 24f
            Offset(x, H - alto * (0.45f + 0.3f * sin(u * 5.2f + f1) + 0.18f * sin(u * 11f + f2) + az.entre(-0.06f, 0.06f))) }
    }

    /** Dónde echan humo los fuegos de lejos (de otros grupos, de los pueblos que arden). */
    val HUMOS = listOf(Offset(262f, 112f), Offset(352f, 116f), Offset(168f, 108f))

    /** La granja quemada del llano, a la derecha, y su compañera más lejos. */
    val GRANJA: Sitio = PiezasNapoleon.sitio(MIRADA, w(19f, 72f), 0.5f)
    val GRANJA_LEJOS: Sitio = PiezasNapoleon.sitio(MIRADA, w(15f, 120f), -0.3f)

    /** Por dónde sale el humo de la granja (lo que queda de la estufa). */
    val HUMO_GRANJA: Offset get() = GRANJA.p(0.4f, 3.2f, 0.3f)

    // ---------------------------------------------------------------- El cielo

    fun DrawScope.cielo(v: VistaNapoleon) {
        val m = v.m
        cielo(v.l, H, m.semilla("cielo"))
        astros(v.l, H, m.semilla("astros"))
        nubes(v.l, H - 4f, m.ficha.clima, m.semilla("nubes"))
    }

    // ---------------------------------------------------------------- Las lomas y los bosques lejanos

    fun DrawScope.lejos(v: VistaNapoleon) {
        val l = v.l; val p = v.p
        val lado = if (p.solALaDerecha) 1f else -1f
        val tierra = lerp(lerp(C.HOJA, C.BARRO, 0.35f), C.NIEVE, v.nieve)
        for ((j, lm) in LOMAS.withIndex()) {
            val (alto, lejos, sem) = lm
            val cr = cresta(alto, sem)
            val forma = curva(cr, H + 4f)
            drawPath(forma, p.medio(tierra, lejos))
            // La cara de sombra de cada loma (del lado contrario al sol) y su cresta al sol.
            clipPath(forma) {
                for (i in 1 until cr.size - 1) {
                    val a = cr[i - 1]; val b = cr[i]; val c = cr[i + 1]
                    val sube = (b.y - a.y) * lado                                      // mira al sol si sube hacia él
                    if (sube > 0f) drawPath(poligono(a, b, Offset(b.x, H + 4f), Offset(a.x, H + 4f)), p.sombra(tierra, lejos).copy(alpha = 0.5f))
                    if (c.y > b.y && b.y < a.y) mancha(b + Offset(0f, 1.5f), 8f, 2f, p.luz(tierra, lejos), 0.5f)
                }
                // Los bosques como manchas: grupos de pinos chicos, con su lado de luz.
                val az = Azar(Semilla.de("napoleon", "manchas", sem))
                repeat(5 + j * 2) {
                    val cx = az.entre(-10f, ANCHO + 10f); val y = H - alto * az.entre(0.05f, 0.45f); val ancho = az.entre(14f, 46f) * (1.2f - lejos * 0.5f)
                    val n = (ancho / 2.6f).toInt()
                    for (i in 0 until n) {
                        val x = cx - ancho / 2f + ancho * i / n + az.entre(-1f, 1f); val h = az.entre(2.2f, 4.6f) * (1.3f - lejos * 0.6f)
                        val y0 = y + az.entre(-1f, 1f)
                        drawPath(poligono(Offset(x - h * 0.28f, y0), Offset(x - h * 0.12f, y0 - h * 0.55f), Offset(x, y0 - h), Offset(x + h * 0.12f, y0 - h * 0.6f), Offset(x + h * 0.28f, y0)),
                            if (i % 3 == 0) p.lado(C.PINO, lejos) else p.medio(C.PINO_OSC, lejos))
                    }
                }
            }
        }
        // El bosque lejano de la derecha, una banda oscura y desigual que se apoya en el horizonte, con la bruma al pie.
        val az = Azar(Semilla.de("napoleon", "bandaDerecha"))
        val banda = Path().apply {
            moveTo(250f, H + 1f); var x = 250f
            while (x < ANCHO + 12f) { val h = az.entre(2f, 7f) * (if (az.real() < 0.15f) 0.3f else 1f); lineTo(x + 0.8f, H - h * 0.5f); lineTo(x + 1.6f, H - h); lineTo(x + 2.4f, H - h * 0.55f); x += az.entre(2.2f, 4.2f) }
            lineTo(ANCHO + 12f, H + 1f); close()
        }
        drawPath(banda, p.medio(C.PINO_OSC, 0.78f))
        if (v.nieve > 0.4f) repeat(40) { val x = az.entre(250f, ANCHO); mancha(Offset(x, H - az.entre(1f, 4f)), 1.4f, 0.4f, p.arriba(C.NIEVE, 0.75f), 0.6f) }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.bruma.copy(alpha = 0.55f)), H - 6f, H + 1f), Offset(0f, H - 6f), Size(ANCHO, 7f))
        when (TramosRetirada.lejania(if (v.vivac) null else v.tramo)) {
            TramosRetirada.Lejania.COLINAS -> colinas(v)
            TramosRetirada.Lejania.BOSQUE -> bosqueDelFondo(v)
            TramosRetirada.Lejania.CAMPOS -> if (TramosRetirada.moscu(v.m)) moscu(v)
            TramosRetirada.Lejania.QUEMADOS, TramosRetirada.Lejania.LLANO, TramosRetirada.Lejania.RIO -> Unit
        }
    }

    /** La cresta de la colina de Krasnoi, a la derecha del camino: de dónde a dónde y su cumbre. */
    private val COLINA: List<Offset> by lazy {
        val az = Azar(Semilla.de("napoleon", "colina"))
        (0..20).map { i -> val x = 228f + 170f * i / 20f; val u = i / 20f
            Offset(x, H - 31f * (sin(PI.toFloat() * (0.15f + 0.85f * u)).coerceAtLeast(0f) * (0.75f + 0.25f * sin(u * 9f))) - az.entre(0f, 1.2f) + 1f) }
    }

    /**
     * Las colinas de Krasnoi, «junto al camino»: una loma grande y blanca a la derecha, con sus bosques como manchas y
     * su lado de sombra, y en la cresta las baterías: los cañones, chicos, contra el cielo (sin nadie: el cuento dice
     * «con cañones y cosacos»; los cosacos no se pintan) y, de día, el humo blanco de los disparos.
     */
    private fun DrawScope.colinas(v: VistaNapoleon) {
        val l = v.l; val p = v.p; val az = Azar(Semilla.de("napoleon", "colina", "manchas"))
        val tierra = lerp(lerp(C.HOJA, C.BARRO, 0.35f), C.NIEVE, v.nieve)
        val forma = curva(COLINA, H + 3f)
        drawPath(forma, p.medio(tierra, 0.5f))
        val lado = if (p.solALaDerecha) 1f else -1f
        clipPath(forma) {
            // La ladera de sombra (la que no mira al sol) y la luz rasante en la cresta.
            val cumbre = COLINA.minByOrNull { it.y }!!
            drawRect(Brush.horizontalGradient(if (lado > 0f) listOf(p.sombra(tierra, 0.5f).copy(alpha = 0.55f), Color.Transparent) else listOf(Color.Transparent, p.sombra(tierra, 0.5f).copy(alpha = 0.55f)), cumbre.x - 50f, cumbre.x + 50f), Offset(220f, H - 40f), Size(180f, 44f))
            // Los bosques de la ladera, como manchas de pinos chicos.
            repeat(9) {
                val cx = az.entre(236f, 392f); val y = H - az.entre(1f, 14f); val ancho = az.entre(10f, 30f)
                val n = (ancho / 2.4f).toInt()
                for (i in 0 until n) {
                    val x = cx - ancho / 2f + ancho * i / n + az.entre(-0.8f, 0.8f); val h = az.entre(2.6f, 5.2f)
                    drawPath(poligono(Offset(x - h * 0.28f, y), Offset(x - h * 0.12f, y - h * 0.55f), Offset(x, y - h), Offset(x + h * 0.12f, y - h * 0.6f), Offset(x + h * 0.28f, y)), if (i % 3 == 0) p.lado(C.PINO, 0.5f) else p.medio(C.PINO_OSC, 0.5f))
                }
            }
            // Las rodadas y los surcos que bajan de la batería, y la nieve pisada en la cresta.
            for (j in 0 until 4) { val x0 = az.entre(270f, 360f); drawLine(p.medio(lerp(C.NIEVE_SUCIA, C.CENIZA, 0.3f), 0.5f).copy(alpha = 0.5f), Offset(x0, H - 22f), Offset(x0 - 18f + j * 6f, H + 2f), 0.6f) }
        }
        drawPath(curva(COLINA), lerp(p.arriba(C.NIEVE, 0.5f), Color.White, 0.3f).copy(alpha = 0.6f * (0.3f + 0.7f * v.r.dia)), style = Stroke(0.8f))
        // Los cañones de la batería en la cresta, contra el cielo: el tubo y las dos ruedas.
        for (x in listOf(286f, 304f, 322f, 344f)) {
            val c = COLINA.minByOrNull { kotlin.math.abs(it.x - x) }!!
            val col = p.medio(lerp(C.CENIZA, C.MADERA_OSC, 0.3f), 0.55f)
            drawCircle(col, 1.6f, c + Offset(-1.2f, -1.4f)); drawCircle(col, 1.6f, c + Offset(1.6f, -1.4f))
            drawLine(col, c + Offset(0f, -2.2f), c + Offset(-5f, -3.2f), 1.1f, StrokeCap.Round)
        }
        // El humo blanco de los disparos, de día, que se queda colgado sobre la cresta.
        if (EscenaNapoleon.canonesEnLasColinas(v.m)) {
            val ak = Azar(Semilla.de("napoleon", "krasnoi", v.m.hora.toInt()))
            for (i in 0 until 8) {
                val c = COLINA[ak.entero(8) + 6]; val q = c + Offset(ak.entre(-6f, 6f), -ak.entre(3f, 9f))
                mancha(q, ak.entre(6f, 13f), ak.entre(3f, 6f), lerp(l.tono(C.NIEVE), Color.White, 0.2f), 0.8f)
                mancha(q + Offset(4f, -3f), ak.entre(5f, 10f), ak.entre(2.5f, 5f), l.tono(lerp(C.NIEVE, C.CAPOTE, 0.3f)), 0.55f)
            }
        }
    }

    /**
     * El bosque cerrado del final, al fondo: la pared de pinos de la derecha, que cierra el llano, alta y oscura, con su
     * fleco de copas y la nieve en ellas, y la bruma al pie. No sube hasta donde van el sol y la luna.
     */
    private fun DrawScope.bosqueDelFondo(v: VistaNapoleon) {
        val p = v.p; val az = Azar(Semilla.de("napoleon", "bosqueFondo"))
        val pinos = (0 until 70).map { val x = az.entre(232f, ANCHO + 10f); Triple(x, az.entre(16f, 34f) * (0.55f + 0.45f * ((x - 232f) / 150f).coerceIn(0f, 1f)), az.entre(4f, 8f)) }.sortedBy { it.second }
        for ((x, h, w) in pinos) {
            val pie = Offset(x, H + 1f)
            val lejos = 0.72f - 0.25f * ((x - 232f) / 150f).coerceIn(0f, 1f)
            val pts = ArrayList<Offset>(); pts += pie + Offset(-w * 0.55f, 0f)
            for (i in 0..5) { val t = i / 5f; val y = pie.y - h * (0.15f + 0.85f * t); val ww = w * 0.55f * (1f - t); pts += Offset(x - ww, y + h * 0.04f); pts += Offset(x - ww * 0.4f, y - h * 0.04f) }
            pts += Offset(x, pie.y - h)
            for (i in 5 downTo 0) { val t = i / 5f; val y = pie.y - h * (0.15f + 0.85f * t); val ww = w * 0.55f * (1f - t); pts += Offset(x + ww * 0.4f, y - h * 0.04f); pts += Offset(x + ww, y + h * 0.04f) }
            pts += pie + Offset(w * 0.55f, 0f)
            drawPath(poligono(pts), p.medio(lerp(C.PINO_OSC, C.CENIZA, 0.2f), lejos))
            if (v.nieve > 0.4f) for (i in 1..4) { val t = i / 5f; val y = pie.y - h * (0.15f + 0.85f * t); drawLine(p.arriba(C.NIEVE, lejos).copy(alpha = 0.7f), Offset(x - w * 0.4f * (1f - t), y + h * 0.03f), Offset(x + w * 0.1f * (1f - t), y + h * 0.01f), 0.6f) }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, v.l.bruma.copy(alpha = 0.5f)), H - 10f, H + 1f), Offset(228f, H - 10f), Size(ANCHO - 228f, 11f))
    }

    /**
     * Moscú, los dos primeros días, a la derecha, lejos: «un campo de chimeneas negras», paredes caídas, una torre y
     * dos cúpulas que quedaron (una dorada, otra pelada, en su esqueleto), y el humo que todavía sube ([vivo]).
     */
    private fun DrawScope.moscu(v: VistaNapoleon) {
        val p = v.p; val az = Azar(Semilla.de("napoleon", "moscuLejos"))
        val muro = lerp(C.CENIZA, C.LONA, 0.3f)
        // El caserío quemado: una banda baja y desigual, y las chimeneas encima.
        val banda = Path().apply { moveTo(262f, H + 1f); var x = 262f; while (x < ANCHO + 8f) { lineTo(x, H - az.entre(1.5f, 4.5f)); x += az.entre(2f, 5f) }; lineTo(ANCHO + 8f, H + 1f); close() }
        drawPath(banda, p.medio(muro, 0.7f))
        repeat(34) { val x = az.entre(264f, ANCHO + 4f); val h = az.entre(4f, 9f); drawRect(p.medio(lerp(C.CENIZA, Color.Black, 0.2f), 0.65f), Offset(x, H - h), Size(az.entre(0.8f, 1.6f), h)) }
        // La torre del Kremlin y dos cúpulas: una dorada que brilla, otra quemada, solo las costillas.
        val tx = 318f
        drawRect(p.lado(lerp(C.ROJO, C.LONA, 0.35f), 0.6f, false), Offset(tx - 2.2f, H - 15f), Size(4.4f, 15f))
        drawPath(poligono(Offset(tx - 2.8f, H - 15f), Offset(tx, H - 22f), Offset(tx + 2.8f, H - 15f)), p.lado(lerp(C.PINO_LUZ, C.CENIZA, 0.3f), 0.6f, true))
        for ((cx, oro) in listOf(296f to true, 342f to false)) {
            val r = 3.2f; val base = H - 11f
            drawRect(p.medio(lerp(C.LONA, C.CENIZA, 0.35f), 0.6f), Offset(cx - r * 0.75f, base), Size(r * 1.5f, 11f))
            val cupula = poligono(listOf(Offset(cx - r, base), Offset(cx - r * 1.05f, base - r * 0.9f), Offset(cx, base - r * 2.4f), Offset(cx + r * 1.05f, base - r * 0.9f), Offset(cx + r, base)))
            if (oro) { drawPath(cupula, p.lado(C.ORO, 0.5f, cx > 300f)); drawLine(p.medio(C.ORO, 0.5f), Offset(cx, base - r * 2.4f), Offset(cx, base - r * 3.4f), 0.5f) }
            else for (j in -2..2) drawLine(p.medio(Color.Black, 0.55f), Offset(cx + j * r * 0.45f, base), Offset(cx, base - r * 2.3f), 0.45f)
        }
    }

    // ---------------------------------------------------------------- Los pueblos del llano

    /**
     * Lo del llano de la derecha, según el tramo: el caserío de madera entero de la salida; el pueblo quemado, más cerca y
     * más grande, del camino viejo (ardiendo el día de Maloyaroslavets); la granja quemada de la nieve y del Berezina.
     * En Krasnoi la tapa la colina y en el final, el bosque: solo queda el bosquecillo.
     */
    fun DrawScope.granja(v: VistaNapoleon) {
        when (TramosRetirada.lejania(if (v.vivac) null else v.tramo)) {
            TramosRetirada.Lejania.CAMPOS -> if (TramosRetirada.moscu(v.m)) bosquecillo(v) else caserio(v)
            TramosRetirada.Lejania.QUEMADOS -> { granjaQuemada(v); puebloQuemado(v) }
            TramosRetirada.Lejania.LLANO, TramosRetirada.Lejania.RIO -> granjaQuemada(v)
            TramosRetirada.Lejania.COLINAS, TramosRetirada.Lejania.BOSQUE -> bosquecillo(v)
        }
    }

    private fun DrawScope.bosquecillo(v: VistaNapoleon) {
        for (pn in RetiradaCamino.bosquecillo()) {
            val q = pn.pie; val lejos = (pn.z / 180f).coerceIn(0f, 0.85f)
            if (pn.albar) pinoAlbar(v.en(q), q, pn.altoL, pn.anchoL, v.nieve * 0.7f, Azar(pn.semilla), lejos, (pn.k / 24f).coerceIn(0.2f, 1f))
            else pino(v.en(q), q, pn.altoL, pn.anchoL, v.nieve * 0.6f, Azar(pn.semilla), lejos, (pn.k / 24f).coerceIn(0.2f, 1f))
        }
    }

    /**
     * El caserío de madera de la salida, entero, lejos en el llano: casas bajas de troncos con su tejado de dos aguas y
     * la iglesia de madera con su cúpula de cebolla **[Diseño propio]**: el cuento no dice cómo eran los pueblos del
     * camino de Kaluga; dice «casas de madera» y «iglesias con cúpulas».
     */
    private fun DrawScope.caserio(v: VistaNapoleon) {
        val p = v.p; val az = Azar(Semilla.de("napoleon", "caserio"))
        bosquecillo(v)
        val casas = listOf(Triple(46f, 150f, 1f), Triple(36f, 168f, 0.8f), Triple(60f, 182f, 1.1f), Triple(28f, 196f, 0.7f), Triple(74f, 210f, 0.9f), Triple(52f, 236f, 1f))
        for ((x, z, t) in casas.sortedByDescending { it.second }) {
            val q = w(x, z); val kk = k(z); val lejos = (z / 180f).coerceIn(0.4f, 0.85f)
            val ancho = 6f * t * kk; val alto = 2.6f * kk; val tejado = 2.2f * kk
            val izq = az.real() < 0.5f
            drawRect(p.lado(lerp(C.MADERA_GRIS, C.MADERA, 0.3f), lejos, izq), Offset(q.x - ancho / 2f, q.y - alto), Size(ancho, alto))
            drawRect(p.lado(lerp(C.MADERA_GRIS, C.MADERA, 0.3f), lejos, !izq), Offset(q.x + (if (izq) ancho / 2f else -ancho / 2f - ancho * 0.25f), q.y - alto * 0.98f), Size(ancho * 0.25f, alto * 0.98f))
            val techo = poligono(Offset(q.x - ancho / 2f - 0.3f * kk, q.y - alto), Offset(q.x, q.y - alto - tejado), Offset(q.x + ancho / 2f + 0.3f * kk, q.y - alto))
            drawPath(techo, p.lado(lerp(C.HOJA, C.MADERA_GRIS, 0.5f), lejos, p.solALaDerecha))
            if (v.nieve > 0.3f) drawPath(poligono(Offset(q.x - ancho / 2f - 0.3f * kk, q.y - alto), Offset(q.x, q.y - alto - tejado), Offset(q.x + ancho / 2f + 0.3f * kk, q.y - alto)), p.arriba(C.NIEVE, lejos).copy(alpha = 0.8f * v.nieve))
            drawRect(p.medio(C.CENIZA, lejos), Offset(q.x - 0.4f * kk, q.y - alto * 0.62f), Size(0.8f * kk, 0.7f * kk))
        }
        // La iglesia de madera, con su torre y su cebolla verde.
        val qi = w(66f, 250f); val ki = k(250f); val lejos = 0.8f
        drawRect(p.lado(lerp(C.MADERA_GRIS, C.MADERA, 0.25f), lejos, false), Offset(qi.x - 3f * ki, qi.y - 5f * ki), Size(6f * ki, 5f * ki))
        drawRect(p.lado(lerp(C.MADERA_GRIS, C.MADERA, 0.25f), lejos, true), Offset(qi.x - 1.2f * ki, qi.y - 11f * ki), Size(2.4f * ki, 6f * ki))
        val cb = Offset(qi.x, qi.y - 11f * ki); val r = 1.6f * ki
        drawPath(poligono(listOf(cb + Offset(-r, 0f), cb + Offset(-r * 1.1f, -r * 0.9f), cb + Offset(0f, -r * 2.5f), cb + Offset(r * 1.1f, -r * 0.9f), cb + Offset(r, 0f))), p.lado(lerp(C.PINO_LUZ, C.AZUL, 0.25f), lejos, p.solALaDerecha))
        drawLine(p.medio(C.ORO, lejos), cb + Offset(0f, -r * 2.5f), cb + Offset(0f, -r * 3.6f), 0.5f)
    }

    /**
     * El pueblo quemado del camino viejo, más cerca que la granja, a la derecha: un campo de chimeneas en pie entre los
     * restos negros de las casas, con su humo ([vivo]); el día de Maloyaroslavets, ardiendo.
     */
    private val CASAS_QUEMADAS = listOf(Triple(9f, 44f, 0.4f), Triple(14f, 52f, -0.5f), Triple(22f, 58f, 0.9f), Triple(11f, 64f, 0.2f), Triple(28f, 90f, -0.3f))

    private fun DrawScope.puebloQuemado(v: VistaNapoleon) {
        val arde = EscenaNapoleon.puebloArde(v.m)
        for ((i, c) in CASAS_QUEMADAS.sortedByDescending { it.second }.withIndex()) {
            val s = PiezasNapoleon.sitio(MIRADA, w(c.first, c.second), c.third)
            sombraCorta(v.r, s.pie, 3.5f * s.k, 2.2f * s.k)
            casaQuemada(s, v.en(s.pie), 5.5f, 4.5f, 120L + i, v.nieve, arde, (c.second / 140f).coerceIn(0.25f, 0.7f))
        }
    }


    // ---------------------------------------------------------------- La granja quemada

    /** La granja quemada del llano, con su compañera más lejos, la cerca rota, más casas del pueblo y el bosquecillo de detrás. */
    private fun DrawScope.granjaQuemada(v: VistaNapoleon) {
        val arde = EscenaNapoleon.puebloArde(v.m)
        val lejos0 = 0.55f; val lejos1 = 0.75f
        sombraCorta(v.r, GRANJA_LEJOS.pie, 30f, 18f)
        casaQuemada(GRANJA_LEJOS, v.en(GRANJA_LEJOS.pie), 5f, 4f, 31L, v.nieve, arde, lejos1)
        // La cerca rota, de estacas, que va de la granja al camino.
        val az = Azar(Semilla.de("napoleon", "cerca"))
        val estacas = (0 until 14).map { i -> val z = 56f - i * 2.6f; w(9.5f - i * 0.15f + az.entre(-0.2f, 0.2f), z) }
        for ((i, q) in estacas.withIndex()) {
            if (az.real() < 0.25f) continue
            val kk = k(RetiradaCamino.zEn(q.y)); val alto = az.entre(0.7f, 1.2f) * kk; val ladeo = az.entre(-0.25f, 0.25f) * alto
            drawLine(v.en(q).plano(C.MADERA_GRIS, q, 0f, 0.5f), q, q + Offset(ladeo, -alto), (0.1f * kk).coerceIn(0.4f, 2f), StrokeCap.Round)
            if (i > 0 && az.real() < 0.6f) { val r = estacas[i - 1]; drawLine(v.en(q).plano(C.MADERA_GRIS, q, 0f, 0.5f), q + Offset(0f, -alto * 0.6f), r + Offset(0f, -alto * 0.55f), (0.06f * kk).coerceIn(0.3f, 1.4f)) }
        }
        casaQuemada(GRANJA, v.en(GRANJA.pie), 6f, 5f, 17L, v.nieve, arde, lejos0)
        // Más casas del pueblo quemado, más allá, y el bosquecillo de pinos de detrás.
        for ((i, q) in listOf(w(27f, 150f) to 0.8f, w(9f, 190f) to -0.6f, w(38f, 240f) to 0.3f).withIndex()) {
            val s = PiezasNapoleon.sitio(MIRADA, q.first, q.second); casaQuemada(s, v.en(s.pie), 5f, 4f, 40L + i, v.nieve, arde, 0.75f + 0.05f * i)
        }
        for (pn in RetiradaCamino.bosquecillo()) {
            val q = pn.pie; val lejos = (pn.z / 180f).coerceIn(0f, 0.85f)
            if (pn.albar) pinoAlbar(v.en(q), q, pn.altoL, pn.anchoL, v.nieve * 0.7f, Azar(pn.semilla), lejos, (pn.k / 24f).coerceIn(0.2f, 1f))
            else pino(v.en(q), q, pn.altoL, pn.anchoL, v.nieve * 0.6f, Azar(pn.semilla), lejos, (pn.k / 24f).coerceIn(0.2f, 1f))
        }
        // Un fuego apagado junto a la granja, de los que pasaron antes.
        hoguera(PiezasNapoleon.sitio(MIRADA, w(11f, 46f)), v.en(w(11f, 46f)), 0f, v.nieve, 99L, 0.5f)
    }

    // ---------------------------------------------------------------- El Berezina

    /** La orilla de acá y la de allá del Berezina, en metros de profundidad. */
    const val ORILLA_ACA = 15f
    const val ORILLA_ALLA = 42f

    /** El río del Berezina, como forma. */
    fun rio(): Path {
        val az = Azar(Semilla.de("napoleon", "berezina"))
        return poligono(ORILLA_DE_ALLA + ORILLA_DE_ACA.reversed())
    }

    /** Las dos orillas del Berezina, puestas a mano: entrantes, salientes y lenguas de hielo pegadas a la de acá. */
    private val ORILLA_DE_ACA: List<Offset> by lazy {
        val az = Azar(Semilla.de("napoleon", "berezina", "aca"))
        (0..22).map { i -> val x = -20f + 420f * i / 22f; Offset(x, w(0f, ORILLA_ACA).y + az.entre(-4.5f, 2.5f) + 3f * sin(i * 0.9f)) }
    }
    private val ORILLA_DE_ALLA: List<Offset> by lazy {
        val az = Azar(Semilla.de("napoleon", "berezina", "alla"))
        (0..18).map { i -> val x = -20f + 420f * i / 18f; Offset(x, w(0f, ORILLA_ALLA).y + az.entre(-1f, 1f)) }
    }

    /**
     * El Berezina, que «no estaba helado… sino lleno de hielo suelto»: el agua negra con las placas blancas, los dos
     * puentes de caballetes de los pontoneros, el camino que acaba en la orilla, y las fogatas de la orilla de acá.
     */
    fun DrawScope.berezina(v: VistaNapoleon) {
        if (v.tramo != Tramo.BEREZINA) return
        val p = v.p; val l = v.l; val forma = rio(); val az = Azar(Semilla.de("napoleon", "berezina", "hielo"))
        val y0 = w(0f, ORILLA_ALLA).y; val y1 = w(0f, ORILLA_ACA).y
        drawPath(forma, Brush.verticalGradient(listOf(lerp(p.medio(lerp(C.AZUL, C.CENIZA, 0.6f), 0.35f), l.bajo, 0.2f), p.medio(lerp(C.AZUL, C.CENIZA, 0.72f), 0.15f)), y0, y1))
        clipPath(forma) {
            // El cielo reflejado en el agua, y las placas de hielo, más grandes y más separadas cerca.
            drawRect(Brush.verticalGradient(listOf(l.bajo.copy(alpha = 0.35f), Color.Transparent), y0, y1), Offset(-10f, y0), Size(ANCHO + 20f, y1 - y0))
            repeat(34) {
                val z = az.entre(ORILLA_ACA, ORILLA_ALLA); val q = w(az.entre(-30f, 40f), z); val kk = k(z)
                val placa = TrazosValle.borron(q, az.entre(0.6f, 1.8f) * kk, az.entre(0.1f, 0.22f) * kk, az, 7, 0.3f, 0.6f)
                drawPath(placa, p.arriba(C.NIEVE, (z / 140f)))
                drawPath(placa, v.a.plano(C.NIEVE_SOMBRA, q, 0f, z / 140f), style = Stroke(0.4f))
            }
            // Las fogatas reflejadas en el agua, de noche.
            if (EscenaNapoleon.fuegosLejos(v.m)) for (q in fogatas()) drawLine(C.LUMBRE.copy(alpha = 0.45f * v.noche), Offset(q.x, y1 - 1f), Offset(q.x + 0.5f, y0 + 2f), 1.2f, blendMode = BlendMode.Plus)
            // Las rachas del agua que corre entre el hielo, y el reflejo oscuro de los puentes.
            repeat(30) { val z = az.entre(ORILLA_ACA, ORILLA_ALLA); val q = w(az.entre(-25f, 35f), z); val kk = k(z); drawLine(lerp(l.bajo, Color.White, 0.2f).copy(alpha = 0.25f), q, q + Offset(az.entre(0.8f, 2.5f) * kk, 0f), (0.05f * kk).coerceIn(0.3f, 1.5f), StrokeCap.Round) }
        }
        // La orilla de acá: el labio de nieve que cuelga sobre el agua, con su canto azul y su sombra en el agua.
        val labio = ORILLA_DE_ACA
        drawPath(curva(labio.map { it + Offset(0f, 2.2f) }), lerp(p.medio(lerp(C.AZUL, C.CENIZA, 0.7f), 0.1f), C.CENIZA, 0.3f), style = Stroke(3.2f, cap = StrokeCap.Round))
        drawPath(TrazosValle.cerrada(labio.map { it + Offset(0f, -1.5f) } + labio.reversed().map { it + Offset(0f, 5f) }), p.arriba(if (v.nieve > 0.3f) C.NIEVE else lerp(C.BARRO, C.HOJA, 0.4f), 0.15f))
        drawPath(curva(labio.map { it + Offset(0f, 0.6f) }), v.a.plano(C.NIEVE_SOMBRA, labio[5], 0f, 0.15f), style = Stroke(1.4f, cap = StrokeCap.Round))
        // Los dos puentes de caballetes, del camino a la otra orilla, con sus patas en el agua.
        for (x0 in listOf(-1.5f, 2.4f)) {
            // De lejos a cerca: los caballetes (patas en A metidas en el agua) y el tablero de tablas encima, que se va al fondo.
            val zs = generateSequence(ORILLA_ALLA + 0.5f) { it - 2.6f }.takeWhile { it >= ORILLA_ACA - 1.5f }.toList()
            fun borde(dx: Float, z: Float) = w(x0 + RUMBO * z + dx * 0.8f, z) + Offset(0f, -1.0f * k(z))
            for (z in zs) {
                val kk = k(z); val x = x0 + RUMBO * z; val lejos = z / 140f
                for (dx in listOf(-1.3f, 1.3f)) {
                    val pata = w(x + dx, z); val pata2 = w(x + dx * 0.6f, z)
                    drawLine(v.a.plano(C.MADERA_OSC, pata, 0f, lejos), pata + Offset(0f, 0.15f * kk), borde(dx * 0.9f, z), (0.1f * kk).coerceIn(0.3f, 1.6f))
                    drawLine(v.a.plano(C.MADERA_OSC, pata2, 0f, lejos), pata2 + Offset(0f, 0.15f * kk), borde(dx * 0.9f, z), (0.07f * kk).coerceIn(0.25f, 1.2f))
                }
            }
            val tablero = poligono(zs.map { borde(-1.5f, it) } + zs.reversed().map { borde(1.5f, it) })
            drawPath(tablero, p.arriba(C.MADERA_GRIS, 0.45f))
            clipPath(tablero) { for (z in zs) for (j in 0..2) { val zz = z - j * 0.85f; drawLine(v.a.plano(C.MADERA_OSC, borde(0f, zz), 0f, 0.5f), borde(-1.5f, zz), borde(1.5f, zz), 0.35f) } }
            // El canto del tablero, en sombra, y la nieve pisada encima.
            drawPath(curva(zs.map { borde(1.5f, it) + Offset(0f, 0.12f * k(it)) }), v.a.plano(C.MADERA_OSC, borde(1.5f, zs.last()), 0f, 0.5f), style = Stroke(1.1f))
            if (v.nieve > 0.3f) drawPath(curva(zs.map { borde(0f, it) }), p.arriba(C.NIEVE_SUCIA, 0.45f), style = Stroke(1.2f, cap = StrokeCap.Round))
        }
        // Las fogatas de la orilla de acá: muchas, de los que esperan.
        for ((i, q) in fogatas().withIndex()) { val s = PiezasNapoleon.sitio(MIRADA, q, i * 0.7f); hoguera(s, v.en(q), if (EscenaNapoleon.fuegosLejos(v.m) || v.cubierto > 0.4f) 0.7f else 0.12f, v.nieve, Semilla.de("napoleon", "fogata", i), 0.45f) }
    }

    /** Las fogatas de la orilla de acá del Berezina. */
    fun fogatas(): List<Offset> {
        val az = Azar(Semilla.de("napoleon", "fogatas"))
        val aca = (0 until 2).map { val z = az.entre(13.2f, ORILLA_ACA - 0.6f); w(az.entre(3.4f, 5.6f) + RUMBO * z, z) }
        val alla = (0 until 7).map { val z = az.entre(ORILLA_ALLA + 2f, 80f); w(az.entre(-9f, 12f) + RUMBO * z, z) }
        return aca + alla
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaNapoleon, reloj: Float) {
        val l = v.l
        // El humo de otros fuegos, lejos.
        for ((j, q) in HUMOS.withIndex()) humo(l, q, 34f, 2.2f, reloj, v.m.viento + 0.2f, 0.25f, 0.7f, j + 10, 5, 0.35f)
        // La granja, que humea; en Maloyaroslavets, que arde.
        val arde = EscenaNapoleon.puebloArde(v.m)
        if (arde) {
            val c = GRANJA.p(0f, 1.2f, 0f); val kk = GRANJA.k
            resplandor(c, 6f * kk, 0.5f + 0.5f * v.noche)
            for (i in 0 until 5) llama(GRANJA.p(-2.4f + i * 1.2f, 1.6f + (i % 2) * 0.6f, 0f), (0.55f + (i % 3) * 0.25f) * kk * (0.6f + 0.4f * v.noche), reloj, i + 60)
            humo(l, c + Offset(0f, -2f * kk), 70f, 14f, reloj, v.m.viento + 0.3f, 0.7f, 0.3f, 61, 9, 0.85f)
        } else humo(l, HUMO_GRANJA, 46f, 6f, reloj, v.m.viento + 0.25f, 0.45f, 0.4f, 62, 7, 0.55f)
        // El pueblo quemado del camino viejo, que todavía humea; el día de Maloyaroslavets, arde.
        if (!v.vivac && TramosRetirada.lejania(v.tramo) == TramosRetirada.Lejania.QUEMADOS) for ((i, c) in CASAS_QUEMADAS.withIndex()) {
            val s = PiezasNapoleon.sitio(MIRADA, w(c.first, c.second), c.third); val q = s.p(0f, 1.2f, 0f)
            if (arde) { resplandor(q, 3f * s.k, 0.25f + 0.55f * v.noche); for (j in 0 until 3) llama(s.p(-1.6f + j * 1.5f, 1.1f + (j % 2) * 0.4f, 0f), (0.45f + (j % 2) * 0.25f) * s.k * (0.6f + 0.4f * v.noche), reloj, 140 + i * 3 + j) }
            if (arde || i % 2 == 0) humo(l, q + Offset(0f, -1.2f * s.k), if (arde) 90f else 34f, (if (arde) 12f else 4f), reloj, v.m.viento + 0.3f, if (arde) 0.75f else 0.5f, 0.35f, 150 + i, if (arde) 9 else 6, if (arde) 0.85f else 0.4f)
        }
        // Moscú, los dos primeros días: el humo que todavía sube de las ruinas.
        if (TramosRetirada.moscu(v.m)) for (i in 0 until 4) humo(l, Offset(282f + i * 26f, H - 4f), 60f, 9f, reloj, v.m.viento + 0.3f, 0.6f, 0.3f, 160 + i, 8, 0.55f)
        // El humo del puente que arde, del otro lado: una columna oscura a lo lejos.
        if (EscenaNapoleon.humoDelPuente(v.m)) humo(l, Offset(206f, H - 2f), 96f, 16f, reloj, 0.35f, 0.75f, 0.35f, 63, 10, 0.9f)
        // Krasnoi: el humo de los cañones, que deriva.
        if (EscenaNapoleon.canonesEnLasColinas(v.m)) for (i in 0 until 3) humo(l, Offset(180f + i * 70f, H - 8f), 22f, 6f, reloj, 0.5f, 0.1f, 0.6f, 64 + i, 5, 0.5f)
        // Las fogatas del Berezina.
        if (v.tramo == Tramo.BEREZINA) for ((i, q) in fogatas().withIndex()) {
            val kk = k(RetiradaCamino.zEn(q.y))
            if (EscenaNapoleon.fuegosLejos(v.m) || v.cubierto > 0.4f) { resplandor(q, 1.4f * kk, 0.2f + 0.45f * v.noche); llama(q, 0.38f * kk, reloj, i + 80) }
            humo(l, q, 7f * kk, 0.7f * kk, reloj, v.m.viento, 0.3f, 0.5f, i + 80, 4, 0.3f)
        }
    }

}
