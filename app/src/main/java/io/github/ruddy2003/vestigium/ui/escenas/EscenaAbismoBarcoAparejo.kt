package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.casco
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.gancho
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.palo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.ristra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El aparejo del barco: lo que enmarca el cuadro por la izquierda y por arriba y lo que cruza las franjas.
 * - **Lejos** ([lejos]): el trinquete, con la vela aferrada y su botavara, el estay de la proa, el cabo de palo a palo con
 *   sus ristras chicas, el hornillo de la proa y las banderas de los días de fiesta.
 * - **Cerca** ([marco]): los obenques de estribor con sus flechastes (la diagonal que sube de la borda y cruza el cielo),
 *   el cabo del palo mayor a los obenques con **las ristras de esponjas** colgadas a secar (más o menos según el arco), y
 *   el palo mayor, cerca, cortado por el borde de la izquierda y por arriba, con sus ganchos: **los cascos de cobre** y el
 *   traje de lona colgados a secar. El cobre es lo que la luz enciende.
 */
internal object AparejoAbismo {
    private val C = ColorAbismo
    private val B = BarcoAbismo

    // ---------------------------------------------------------------- La jarcia, en metros del barco (u, h, v)

    /** Los obenques de estribor: de sus cadenotes en la borda al tope del palo mayor. */
    val OBENQUES = listOf(V3(B.estribor(2.5f), B.REGALA, 2.5f), V3(B.estribor(3.3f), B.REGALA, 3.3f))
    val TOPE = V3(B.EJE, 10f, B.PALO_MAYOR)

    /** Un punto del obenque [i] a [h] metros de altura. */
    fun obenque(i: Int, h: Float): V3 { val a = OBENQUES[i]; val t = (h - a.y) / (TOPE.y - a.y); return a + (TOPE - a) * t }

    /** El cabo de las ristras: del palo mayor al obenque de popa (el que cruza el cielo por arriba) y del palo mayor al trinquete (el lejano). */
    val CABO_CERCA = V3(B.EJE + 0.16f, 2.95f, B.PALO_MAYOR) to obenque(0, 2.55f)
    val CABO_LEJOS = V3(B.EJE, 3.1f, B.PALO_MAYOR + 0.1f) to V3(B.EJE, 4.4f, B.TRINQUETE)

    /** Un punto del barco (u, h, v) en el lienzo. */
    private fun p(a: V3): Offset = B.pb(a.x, a.y, a.z)
    private fun z(a: V3): Float = B.cam(a.x, a.z).y

    /** Lo que cuelga de un cabo combado: el punto a [t] del cabo de [a] a [b] que baja [caida] metros en medio. */
    private fun enElCabo(a: V3, b: V3, t: Float, caida: Float): V3 = a + (b - a) * t + V3(0f, -caida * 4f * t * (1f - t), 0f)

    /** Una ristra colgada: de dónde cuelga (en metros del barco), cuánto mide, cuántas esponjas se ven, si va en racimo. */
    internal class Ristra(val arriba: V3, val largo: Float, val cuantas: Int, val racimo: Boolean, val semilla: Long)

    /**
     * Las ristras colgadas hoy, tantas como diga [EscenaAbismo.ristras] (lo vigila una prueba). Se llenan en este orden: el
     * cabo de cerca (las grandes, que cruzan el cielo y echan su sombra en la cubierta) y luego el de palo a palo.
     */
    internal fun ristras(m: Momento): List<Ristra> {
        val n = EscenaAbismo.ristras(m)
        val az = Azar(Semilla.de("abismo", "ristras"))
        val huecos = ArrayList<Ristra>()
        val cerca = listOf(0.62f, 0.3f, 0.82f, 0.46f, 0.14f, 0.72f, 0.38f)
        for ((i, t) in cerca.withIndex()) huecos += Ristra(enElCabo(CABO_CERCA.first, CABO_CERCA.second, t, 0.22f), az.entre(0.55f, 0.95f) * (1f - 0.25f * t), 14 + az.entero(7), i % 3 == 1, Semilla.de("abismo", "ristra", i))
        val lejos = listOf(0.3f, 0.55f, 0.75f, 0.15f, 0.88f, 0.42f, 0.65f)
        for ((i, t) in lejos.withIndex()) huecos += Ristra(enElCabo(CABO_LEJOS.first, CABO_LEJOS.second, t, 0.35f), az.entre(0.7f, 1.2f), 8 + az.entero(4), i % 2 == 0, Semilla.de("abismo", "ristraLejos", i))
        return huecos.take(n)
    }

    // ---------------------------------------------------------------- Lejos

    fun DrawScope.lejos(v: BarcoAbismo.Vista) {
        val m = v.m; val p = v.p
        // El trinquete: del pie, en la cubierta de proa, hasta salir por arriba, con su vela aferrada sobre la botavara.
        val pie = p(V3(B.EJE, 0f, B.TRINQUETE)); val alto = p(V3(B.EJE, 9.5f, B.TRINQUETE))
        val k = B.k(z(V3(B.EJE, 0f, B.TRINQUETE)))
        palo(p, pie, alto, 0.26f * k, 0.18f * k, zunchos = listOf(0.08f, 0.22f))
        val bot0 = p(V3(B.EJE, 1.35f, B.TRINQUETE)); val bot1 = p(V3(B.EJE, 1.6f, B.TRINQUETE - 3.6f))
        drawLine(p.sombra(C.MADERA_OSC), bot0, bot1, 0.12f * k, StrokeCap.Round)
        drawLine(p.por(C.LONA, 0.3f), bot0 + Offset(0f, -0.14f * k), bot1 + Offset(0f, -0.12f * k), 0.24f * k, StrokeCap.Round)
        drawLine(p.luz(C.LONA).copy(alpha = 0.6f), bot0 + Offset(0f, -0.22f * k), bot1 + Offset(0f, -0.2f * k), 0.06f * k, StrokeCap.Round)
        // El estay: del trinquete a la roda.
        val roda = p(V3(B.EJE, B.regala(B.RODA) + 0.2f, B.RODA + 0.3f))
        drawLine(p.sombra(C.CANAMO).copy(alpha = 0.8f), p(V3(B.EJE, 6.2f, B.TRINQUETE)), roda, 0.45f)
        // El cabo de palo a palo, con sus ristras chicas.
        val (a, b) = CABO_LEJOS
        cabo(p, (0..10).map { p(enElCabo(a, b, it / 10f, 0.35f)) }, 0.6f)
        for (r in ristras(m).filter { it.arriba.z > B.PALO_MAYOR + 0.05f }) pintarRistra(v, r)
        // Las banderas: el empavesado de fiesta, la bandera azul y blanca (y la americana) y el gallardete del luto.
        banderas(v)
    }

    // ---------------------------------------------------------------- Cerca: el marco

    fun DrawScope.marco(v: BarcoAbismo.Vista) {
        val m = v.m; val p = v.p
        // Los obenques de estribor, con sus flechastes: suben de la borda y cruzan el cielo.
        val k0 = B.k(z(OBENQUES[0]))
        for (i in OBENQUES.indices) cabo(p, listOf(p(OBENQUES[i]), p(obenque(i, 6f))), 0.03f * k0, lerp(C.CANAMO, C.BREA, 0.55f))
        for (j in 1..9) { val h = B.REGALA + 0.38f * j; drawLine(p.sombra(C.CANAMO).copy(alpha = 0.85f), p(obenque(0, h)), p(obenque(1, h)), 0.012f * k0) }
        // El cabo de las ristras grandes y sus ristras.
        val (a, b) = CABO_CERCA
        cabo(p, (0..12).map { p(enElCabo(a, b, it / 12f, 0.22f)) }, 0.022f * B.k(z(a)))
        for (r in ristras(m).filter { it.arriba.z <= B.PALO_MAYOR + 0.05f }) pintarRistra(v, r)
        // El farol, colgado de una driza larga del cabo, sobre la bomba y el corro: entero de 18:00 a 20:00 y antes del alba,
        // bajo las primeras horas del sueño; si no, apagado.
        val cuelga = p(enElCabo(a, b, 0.42f, 0.22f))
        drawLine(v.p.sombra(C.CANAMO), cuelga, B.FAROL + Offset(0f, -0.2f * B.K_FAROL), 0.01f * B.K_FAROL)
        PiezasAbismo.run { farol(v.pincel(B.FAROL), B.FAROL, B.K_FAROL, v.lu.farol) }
        paloMayor(v)
    }

    /** Una ristra en su sitio, con su escala, y a la luz de su sitio (de noche, la lamparilla o el farol si están cerca). */
    private fun DrawScope.pintarRistra(v: BarcoAbismo.Vista, r: Ristra) {
        val arriba = p(r.arriba); val kk = B.k(z(r.arriba))
        ristra(v.pincel(arriba), arriba, r.largo * kk, 0.05f * kk, r.cuantas, r.semilla, racimo = r.racimo, sesgo = if (v.mar == EscenaAbismo.Mar.NORTE) 0.18f else 0.03f)
    }

    /**
     * Los ganchos del palo mayor, de arriba abajo (en metros del barco: u, h, v): tres buzos, tres cascos. Cada gancho sale
     * por un lado distinto del palo, para que los cascos no cuelguen uno encima de otro; el de abajo es el de Nikos.
     */
    internal val GANCHOS = listOf(V3(B.EJE + 0.17f, 2.25f, B.PALO_MAYOR - 0.1f), V3(B.EJE + 0.2f, 1.66f, B.PALO_MAYOR - 0.2f), V3(B.EJE + 0.16f, 1.08f, B.PALO_MAYOR - 0.08f))

    /** Cuánto se separa del palo cada casco colgado (en metros, hacia estribor): no cuelgan a plomo uno sobre otro. */
    private val VUELO = listOf(0.16f, 0.36f, 0.22f)

    /** Cuántos cascos cuelgan hoy de sus ganchos: los de [EscenaAbismo.cascosColgados], uno menos si el casco es el objeto del día. */
    internal fun cascosHoy(m: Momento): Int = EscenaAbismo.cascosColgados(m) - (if (ObjetosAbismo.elCuadroPinta(m, "casco")) 0 else 1)

    /**
     * El palo mayor, cerca, cortado por la izquierda y por arriba: redondo, con la cara del sol encendida y la otra en
     * sombra de color, la veta larga, los nudos y las grietas de la madera, los zunchos de hierro con sus remaches y su
     * chorrera de óxido, la trinca de cabo donde se amarra el de las ristras, la brea y el roce junto a los ganchos; al pie,
     * el cabillero con sus cabillas y sus adujas; y los tres ganchos con los cascos colgados (o vacío el de Nikos).
     */
    private fun DrawScope.paloMayor(v: BarcoAbismo.Vista) {
        val m = v.m; val p = v.p
        val base = V3(B.EJE, 0f, B.PALO_MAYOR); val zz = z(base); val k = B.k(zz)
        val pie = p(base); val alto = p(V3(B.EJE, 6f, B.PALO_MAYOR))
        val g0 = 0.32f * k; val g1 = 0.28f * k
        palo(p, pie, alto, g0, g1, zunchos = emptyList())
        val forma = androidx.compose.ui.graphics.Path().apply { moveTo(pie.x - g0 / 2f, pie.y); lineTo(alto.x - g1 / 2f, alto.y); lineTo(alto.x + g1 / 2f, alto.y); lineTo(pie.x + g0 / 2f, pie.y); close() }
        val ld = if (p.solALaDerecha) 1f else -1f
        val az = Azar(Semilla.de("abismo", "paloMayor"))
        fun en(u: Float, t: Float): Offset { val a = pie + (alto - pie) * t; val g = g0 + (g1 - g0) * t; return Offset(a.x + u * g * 0.5f, a.y) }
        clipPath(forma) {
            // La veta: hilos largos que ondean, más juntos hacia los cantos (el palo es redondo).
            repeat(16) {
                val u = az.entre(-0.95f, 0.95f).let { it * (0.6f + 0.4f * kotlin.math.abs(it)) }; val t0 = az.entre(0f, 0.6f); val t1 = t0 + az.entre(0.2f, 0.5f)
                val a = en(u, t0); val b = en(u + az.entre(-0.06f, 0.06f), t1); val mm = (a + b) * 0.5f + Offset(az.entre(-0.8f, 0.8f), 0f)
                drawPath(androidx.compose.ui.graphics.Path().apply { moveTo(a.x, a.y); cubicTo(mm.x, mm.y + (b.y - a.y) * 0.2f, mm.x, mm.y - (b.y - a.y) * 0.2f, b.x, b.y) },
                    (if (u * ld > 0) p.luz(C.MADERA) else p.sombra(C.MADERA)).copy(alpha = 0.3f), style = Stroke(az.entre(0.3f, 0.7f)))
            }
            // Los nudos y las grietas (fendas secas, oscuras, a lo largo).
            repeat(3) { val q = en(az.entre(-0.3f, 0.6f), az.entre(0.05f, 0.7f)); val w = g0 * az.entre(0.08f, 0.14f); drawOval(p.sombra(lerp(C.MADERA, C.BREA, 0.4f)).copy(alpha = 0.6f), Offset(q.x - w, q.y - w * 1.6f), Size(w * 2f, w * 3.2f)); drawOval(p.medio(C.MADERA_OSC).copy(alpha = 0.5f), Offset(q.x - w * 1.8f, q.y - w * 3f), Size(w * 3.6f, w * 6f), style = Stroke(0.4f)) }
            repeat(4) { val u = az.entre(-0.2f, 0.7f); val t0 = az.entre(0.05f, 0.8f); drawLine(p.sombra(C.BREA).copy(alpha = 0.55f), en(u, t0), en(u + 0.02f, t0 + az.entre(0.05f, 0.14f)), 0.5f, StrokeCap.Round) }
            // La brea y el roce: manchas oscuras junto a los ganchos y abajo, donde se agarran las manos.
            for (g in GANCHOS) { val q = p(g); mancha(q + Offset(-g0 * 0.15f, g0 * 0.4f), g0 * 0.35f, g0 * 0.8f, p.sombra(lerp(C.MADERA, C.BREA, 0.5f)), 0.35f) }
            mancha(en(0.1f, 0.12f), g0 * 0.5f, g0 * 1.4f, p.luz(lerp(C.MADERA, C.LONA, 0.4f)), 0.3f)
            // La cara del sol, encendida: una franja clara que corre a lo largo; y del otro lado, el reflejo frío del agua.
            drawLine(p.luz(lerp(C.MADERA, Color(0xFFFFF2D0), 0.3f)).copy(alpha = 0.45f * p.r.contraste), en(ld * 0.45f, 0f), en(ld * 0.45f, 1f), g0 * 0.12f)
            drawLine(p.medio(C.MAR).copy(alpha = 0.18f), en(-ld * 0.85f, 0f), en(-ld * 0.85f, 1f), g0 * 0.1f)
        }
        // Los zunchos de hierro, con sus remaches y la chorrera de óxido.
        for (h in listOf(0.55f, 2.62f, 4.4f)) {
            val t = h / 6f; val a = en(-1.08f, t); val b = en(1.08f, t); val g = g0 + (g1 - g0) * t
            drawLine(p.sombra(C.PLOMO), a + Offset(0f, g * 0.05f), b + Offset(0f, g * 0.05f), g * 0.13f)
            drawLine(p.por(C.PLOMO, 0.3f), a, b, g * 0.08f)
            for (u in listOf(-0.5f, 0f, 0.5f)) drawCircle(if (u * ld >= 0f) p.luz(C.PLOMO) else p.sombra(C.PLOMO), g * 0.035f, en(u, t))
            drawLine(p.medio(lerp(C.COBRE_OSC, C.COBRE, 0.3f)).copy(alpha = 0.35f), en(0.2f, t), en(0.24f, t) + Offset(0f, g * 0.9f), g * 0.05f, StrokeCap.Round)
        }
        // La trinca donde se amarra el cabo de las ristras: vueltas de cabo apretadas.
        val tr = 2.95f / 6f
        for (i in 0 until 6) { val q = en(0f, tr) + Offset(0f, (i - 2.5f) * g0 * 0.07f); drawLine(p.sombra(C.CANAMO), q + Offset(-g0 * 0.56f, 0f), q + Offset(g0 * 0.56f, g0 * 0.03f), g0 * 0.06f); drawLine(p.luz(C.CANAMO).copy(alpha = 0.6f), q + Offset(-g0 * 0.2f + ld * g0 * 0.2f, -g0 * 0.015f), q + Offset(g0 * 0.2f + ld * g0 * 0.25f, 0f), g0 * 0.02f) }
        // El cabillero al pie del palo: la barra con sus cabillas y los cabos adujados que cuelgan.
        val c0 = p(V3(B.EJE + 0.22f, 0.62f, B.PALO_MAYOR - 0.18f)); val c1 = p(V3(B.EJE + 0.6f, 0.62f, B.PALO_MAYOR + 0.2f))
        drawLine(p.sombra(C.MADERA_OSC), c0 + Offset(0f, 0.03f * k), c1 + Offset(0f, 0.03f * k), 0.09f * k)
        drawLine(p.luz(C.MADERA), c0, c1, 0.06f * k)
        for (i in 0 until 3) {
            val q = c0 + (c1 - c0) * (0.2f + 0.3f * i)
            drawLine(p.medio(C.MADERA_OSC), q + Offset(0f, -0.1f * k), q + Offset(0f, 0.06f * k), 0.035f * k, StrokeCap.Round)
            drawLine(p.luz(C.MADERA).copy(alpha = 0.6f), q + Offset(-0.008f * k, -0.1f * k), q + Offset(-0.008f * k, -0.02f * k), 0.012f * k, StrokeCap.Round)
        }
        // Un cabo dado vuelta en una cabilla: las vueltas en ocho y el chicote que cuelga.
        run { val q = c0 + (c1 - c0) * 0.5f; drawLine(p.sombra(C.CANAMO), q + Offset(-0.05f * k, -0.05f * k), q + Offset(0.05f * k, 0.04f * k), 0.022f * k, StrokeCap.Round); drawLine(p.sombra(C.CANAMO), q + Offset(0.05f * k, -0.05f * k), q + Offset(-0.05f * k, 0.04f * k), 0.022f * k, StrokeCap.Round); drawLine(p.medio(C.CANAMO), q + Offset(0.02f * k, 0.04f * k), q + Offset(0.035f * k, 0.3f * k), 0.018f * k, StrokeCap.Round) }
        // Los cascos: tres ganchos, y de cada uno, por su cordel, un casco girado a su manera. Mientras Nikos está abajo, el
        // suyo (el de abajo) queda vacío, con el cordel suelto.
        val cascos = cascosHoy(m)
        val miras = listOf(0.5f, -0.45f, 0.15f)
        for ((i, g) in GANCHOS.withIndex()) {
            val q = p(g); val kk = B.k(z(g)); val r = 0.2f * kk
            gancho(p, q, 0.13f * kk, 1f)
            val cordel = q + Offset(0.15f * kk, -0.02f * kk)
            if (i < cascos) {
                val c = q + Offset(VUELO[i] * kk, 0.12f * kk + r)
                val pc = v.pincel(c)
                drawLine(pc.sombra(C.CANAMO), cordel, c + Offset(0f, -r * 0.95f), 0.018f * kk)
                casco(pc, c, r, miras[i], Semilla.de("abismo", "casco", i))
                lunaEnLaCupula(v, c, r)
            } else {
                drawPath(androidx.compose.ui.graphics.Path().apply { moveTo(cordel.x, cordel.y); cubicTo(cordel.x + 0.03f * kk, cordel.y + 0.1f * kk, cordel.x - 0.05f * kk, cordel.y + 0.16f * kk, cordel.x - 0.01f * kk, cordel.y + 0.26f * kk) }, p.sombra(C.CANAMO), style = Stroke(0.016f * kk, cap = StrokeCap.Round))
            }
        }
        // El filo de luna en el palo.
        if (v.luna > 0.05f) drawLine(B.plata(v).copy(alpha = 0.4f * v.luna), pie + Offset(0.12f * k, 0f), alto + Offset(0.1f * k, 0f), 0.04f * k)
    }

    /** De noche, la luna platea lo alto de la cúpula de un casco. */
    private fun DrawScope.lunaEnLaCupula(v: BarcoAbismo.Vista, c: Offset, r: Float) {
        if (v.luna > 0.05f) drawArc(B.plata(v).copy(alpha = 0.55f * v.luna), 200f, 110f, false, Offset(c.x - r * 0.85f, c.y - r * 0.95f), Size(r * 1.7f, r * 1.6f), style = androidx.compose.ui.graphics.drawscope.Stroke(r * 0.08f))
    }

    /** Las banderas de los días señalados: en el trinquete, a media altura (la única parte que se ve). */
    private fun DrawScope.banderas(v: BarcoAbismo.Vista) {
        val m = v.m; val p = v.p
        val driza = V3(B.EJE, 4.8f, B.TRINQUETE); val q = p(driza); val k = B.k(z(driza))
        val viento = if (v.mar == EscenaAbismo.Mar.CALMA) 0.25f else 1f
        if (EscenaAbismo.fiesta(m) || EscenaAbismo.cruz(m)) {
            // El empavesado: de la roda al trinquete y del trinquete al palo mayor, banderitas azules y blancas, sin letras.
            val tramos = listOf(V3(B.EJE, B.regala(B.RODA) + 0.2f, B.RODA + 0.3f) to driza, driza to V3(B.EJE + 0.1f, 3.2f, B.PALO_MAYOR))
            var i = 0
            for ((a, b) in tramos) for (t in 1..9) {
                val c = p(enElCabo(a, b, t / 10f, 0.25f)); val kk = B.k(z(enElCabo(a, b, t / 10f, 0.25f)))
                val w = 0.16f * kk
                val col = if (i % 2 == 0) p.luz(C.BLANCO) else p.medio(C.AZUL)
                drawPath(Path().apply { moveTo(c.x - w * 0.4f, c.y); lineTo(c.x + w * 0.4f, c.y); lineTo(c.x, c.y + w * 1.1f); close() }, col)
                i++
            }
        }
        if (EscenaAbismo.cuatroDeJulio(m)) {
            // La bandera azul y blanca, a rayas, con su cuartel de la cruz; debajo, la americana: rayas rojas y blancas y su cuartel azul (sin estrellas que contar).
            bandera(p, q, k, viento, listOf(C.AZUL, C.BLANCO), C.AZUL, true)
            bandera(p, q + Offset(0f, 0.5f * k), k, viento, listOf(C.ROJO, C.BLANCO), lerp(C.AZUL, C.BREA, 0.3f), false)
        }
        if (EscenaAbismo.luto(m)) {
            // El gallardete a media asta.
            val g = q + Offset(0f, 0.6f * k)
            drawPath(Path().apply { moveTo(g.x, g.y); lineTo(g.x + 0.9f * k * viento + 0.1f * k, g.y + 0.12f * k + (1f - viento) * 0.5f * k); lineTo(g.x, g.y + 0.22f * k); close() }, p.medio(lerp(C.BREA, C.AZUL, 0.15f)))
        }
    }

    private fun DrawScope.bandera(p: Pincel, a: Offset, k: Float, viento: Float, rayas: List<Color>, cuartel: Color, cruz: Boolean) {
        val w = 0.75f * k * (0.5f + 0.5f * viento); val h = 0.45f * k
        for (i in 0 until 9) {
            val y = a.y + h * i / 9f
            drawLine(p.medio(rayas[i % 2]), Offset(a.x, y + h / 18f), Offset(a.x + w, y + h / 18f + (1f - viento) * w * 0.4f), h / 9f)
        }
        drawRect(p.medio(cuartel), Offset(a.x, a.y), Size(w * 0.38f, h * 0.55f))
        if (cruz) { drawLine(p.luz(C.BLANCO), Offset(a.x + w * 0.19f, a.y), Offset(a.x + w * 0.19f, a.y + h * 0.55f), h * 0.1f); drawLine(p.luz(C.BLANCO), Offset(a.x, a.y + h * 0.27f), Offset(a.x + w * 0.38f, a.y + h * 0.27f), h * 0.1f) }
    }

    // ---------------------------------------------------------------- Las sombras del aparejo en la cubierta

    /**
     * Las sombras de lo que cuelga sobre la cubierta: los obenques, el cabo y **las ristras** (una mancha calada: cada
     * esponja, su óvalo), el palo y los cascos. Va dentro de la `capaSombra` de la cubierta. Las ristras de cerca son la
     * «luz con forma» del cuadro: por la mañana el sol entra de estribor y tiende su sombra por las tablas.
     */
    fun DrawScope.sombras(v: BarcoAbismo.Vista, c: Color) {
        val sol = v.sol
        fun alSuelo(a: V3): Offset { val q = B.cam(a.x, a.z); val s = B.alSuelo(sol, q.x, a.y, q.y); return B.pc(s.x, 0f, s.y) }
        val k0 = B.k(z(OBENQUES[0]))
        for (o in OBENQUES.indices) drawLine(c, alSuelo(OBENQUES[o]), alSuelo(obenque(o, 5.5f)), 0.03f * k0)
        // Los flechastes: la escala de la jarcia, tendida en las tablas por la mañana.
        for (j in 1..12) { val h = B.REGALA + 0.38f * j; drawLine(c, alSuelo(obenque(0, h)), alSuelo(obenque(1, h)), 0.016f * k0) }
        val (a, b) = CABO_CERCA
        for (t in 0 until 10) drawLine(c, alSuelo(enElCabo(a, b, t / 10f, 0.22f)), alSuelo(enElCabo(a, b, (t + 1) / 10f, 0.22f)), 0.02f * B.k(z(a)))
        val az = Azar(Semilla.de("abismo", "sombraRistras"))
        for (r in ristras(v.m)) {
            val arriba = r.arriba; val kk = B.k(z(arriba))
            for (i in 0 until r.cuantas) {
                val t0 = if (r.racimo) 0.42f + 0.58f * (i + 0.5f) / r.cuantas else 0.1f + 0.9f * (i + 0.5f) / r.cuantas
                val q = alSuelo(arriba + V3(0f, -r.largo * t0, 0f))
                val rr = 0.08f * kk * az.entre(0.7f, 1.2f)
                drawOval(c, Offset(q.x - rr * 1.2f, q.y - rr * 0.45f), Size(rr * 2.4f, rr * 0.9f))
            }
        }
        // El palo mayor y el trinquete: una franja larga; y los cascos colgados del palo, sus redondos.
        for ((pv, g) in listOf(B.PALO_MAYOR to 0.3f, B.TRINQUETE to 0.24f)) {
            val pie = alSuelo(V3(B.EJE, 0f, pv)); val tope = alSuelo(V3(B.EJE, 5f, pv))
            drawLine(c, pie, tope, g * B.k(z(V3(B.EJE, 0f, pv))) * 0.8f, StrokeCap.Round)
        }
        for ((i, g) in GANCHOS.withIndex()) {
            if (i >= cascosHoy(v.m)) continue
            val q = alSuelo(V3(g.x + 0.12f, g.y - 0.32f, g.z)); val kk = B.k(z(g))
            drawOval(c, Offset(q.x - 0.2f * kk, q.y - 0.06f * kk), Size(0.4f * kk, 0.14f * kk))
        }
        // El farol, colgado.
        run { val q = alSuelo(V3(0.35f, B.FAROL_ALTO, 2.85f)); val kk = B.K_FAROL; drawOval(c, Offset(q.x - 0.06f * kk, q.y - 0.03f * kk), Size(0.12f * kk, 0.06f * kk)) }
    }
}
