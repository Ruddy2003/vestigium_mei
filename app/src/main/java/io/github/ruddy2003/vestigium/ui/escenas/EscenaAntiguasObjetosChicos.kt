package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.boca
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.contacto
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.empolvado
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.halo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.labio
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.punto
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.redondo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.sombra
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.trazo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.tubo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los siete objetos chicos de Brenna, sobre la losa del poyo, a la derecha de la puerta (`ObjetosAntiguas.chico`). La
 * losa es estrecha y se ve casi de canto (los ojos quedan metro y medio por encima, a diez metros): lo plano no se tumba
 * en ella, va recostado en el muro (el libro) o colgado de un clavo, con el pie en la losa (el mapa, la cecina), y eso
 * está en [PapelesAntiguas]. Lo demás va de pie, en tres cuartos. En invierno, sobre la nieve de la losa, con su labio delante.
 */
internal object ChicosAntiguas {
    private val C = ColorAntiguas
    private val O = ColorObjetos

    /** El muro, detrás del punto de apoyo, y el canto de delante de la losa, en metros (en los ejes del muro). */
    val MURO = PuertaAntiguas.POYO_FONDO * 0.55f
    val CANTO = -PuertaAntiguas.POYO_FONDO * 0.45f

    /** De noche les llega la lumbre de la puerta, a su izquierda y detrás, y la del ventanuco, encima (en el mundo). */
    private val LUMBRE = V3(-0.32f, 0.5f, 0.82f)

    /** El clavo del muro, bajo el ventanuco, de donde cuelgan el mapa y la vara de la cecina (en los ejes del muro). */
    val CLAVO = V3(0.1f, 0.62f, MURO)

    fun DrawScope.pintar(id: String, p: Pincel, sobre: Offset, escala: Float, e: EntornoObjeto) {
        val k = escala * 48f
        val y0 = if (e.nieve > 0.3f) 0.09f * e.nieve else 0f
        val pie = if (y0 > 0f) Sitio(SitioAntiguas.MIRADA, sobre, k, LuzObjeto.GIRO_MURO).p(0f, y0, 0f) else sobre
        val lm = LuzObjeto(Sitio(SitioAntiguas.MIRADA, pie, k, LuzObjeto.GIRO_MURO), p, LUMBRE, e)
        when (id) {
            "linterna" -> linterna(lm)
            "cuerno" -> cuerno(lm)
            "libro" -> with(PapelesAntiguas) { libro(lm) }
            "mapa" -> with(PapelesAntiguas) { mapa(lm) }
            "cecina" -> cecina(lm)
            "jarra" -> jarra(lm)
            "pedernal" -> pedernal(lm)
            else -> Unit
        }
    }

    // ---------------------------------------------------------------- Ayudas

    /** El suelo de la losa y el muro de detrás, donde caen las sombras (en los ejes del muro [lm]). */
    fun losa(lm: LuzObjeto): Path = poligono(lm.q(-0.3f, 0f, CANTO), lm.q(1.3f, 0f, CANTO), lm.q(1.3f, 0f, MURO), lm.q(-0.3f, 0f, MURO))
    fun muro(lm: LuzObjeto): Path = poligono(lm.q(-0.3f, 0f, MURO), lm.q(1.5f, 0f, MURO), lm.q(1.5f, 1.7f, MURO), lm.q(-0.3f, 1.7f, MURO))

    /** La sombra de una cosa dada por su envolvente en sus propios ejes ([lo]), sobre la losa y el muro. */
    fun DrawScope.sombraDe(lm: LuzObjeto, lo: LuzObjeto, puntos: List<V3>) {
        val d = lo.s.giro - lm.s.giro
        sombra(lm, puntos.map { lm.girado(it, d) }, MURO, losa(lm), muro(lm))
    }

    /** Las esquinas de una caja de [w] de ancho, [h] de alto y [f] de fondo, centrada en el pie (para las sombras). */
    private fun caja(w: Float, h: Float, f: Float): List<V3> = listOf(-1f, 1f).flatMap { sx -> listOf(-1f, 1f).flatMap { sz -> listOf(V3(sx * w / 2f, 0f, sz * f / 2f), V3(sx * w / 2f, h, sz * f / 2f)) } }

    /** Un anillo de radio [r] alrededor del punto [c], en el plano perpendicular a [eje] (la boca de un cuerno, una copa). */
    private fun anillo(l: LuzObjeto, c: V3, eje: V3, r: Float, achata: Float = 1f): Path {
        val t = LuzObjeto.unidad(eje)
        val b1 = LuzObjeto.unidad(V3(t.y, -t.x, 0f).let { if (it.x * it.x + it.y * it.y < 1e-4f) V3(1f, 0f, 0f) else it })
        val b2 = V3(t.y * b1.z - t.z * b1.y, t.z * b1.x - t.x * b1.z, t.x * b1.y - t.y * b1.x)
        return poligono((0 until 18).map { j -> val a = 2f * PI.toFloat() * j / 18f; l.q(c + b1 * (r * cos(a)) + b2 * (r * achata * sin(a))) })
    }

    // ---------------------------------------------------------------- La linterna

    private val CARAS = listOf(V3(0f, 0f, -1f), V3(1f, 0f, 0f), V3(0f, 0f, 1f), V3(-1f, 0f, 0f))

    /**
     * La linterna de mina, «de hierro y de cuerno raspado tan fino que deja pasar la luz», de pie en tres cuartos: la base,
     * cuatro ventanas de cuerno con sus rayas de raspado entre los montantes, la portezuela con su pestillo, el tejadillo en
     * punta con su chimenea y su aro de colgar, y el hollín arriba. De día se ve dentro, a través del cuerno, el cabo de
     * vela de sebo; de noche (y con niebla, «su luz amarilla es lo último que se pierde de vista») está encendida: el cuerno
     * se enciende de ámbar y alumbra la losa y el muro. Va 1,75 veces más grande que una de verdad.
     */
    private fun DrawScope.linterna(lm: LuzObjeto) {
        val lo = lm.conGiro(LuzObjeto.giroDeFrente(lm.s.pie, lm.s.k) + 0.62f)
        val w = 0.096f; val alto = 0.33f
        val luz = encendida(lo.noche, lo.e)
        contacto(lm, 0f, 0f, 0.3f, 0.2f)
        sombraDe(lm, lo, caja(2f * w, 0.5f, 2f * w))
        if (luz > 0.02f) luzDeLaLinterna(lm, lo, luz)
        base(lo, 0.11f, 0.035f, O.HIERRO)
        for (n in CARAS) if (lo.s.seVe(V3(n.x * w, 0.18f, n.z * w), n)) ventana(lo, n, w, alto, luz)
        tejadillo(lo, w, alto)
        aro(lo, alto)
        empolvado(lo, listOf(lo.q(-0.07f, alto + 0.04f, 0f), lo.q(0f, alto + 0.12f, 0f), lo.q(0.07f, alto + 0.04f, 0f)), 1.2f)
        labio(lm, 0f, 0f, 0.3f)
    }

    /** Cuánto está encendida la linterna, de 0 a 1: de noche y, con niebla, a cualquier hora. */
    fun encendida(noche: Float, e: EntornoObjeto): Float = if (e.niebla) 1f else ((noche - 0.4f) / 0.25f).coerceIn(0f, 1f)

    /** La luz de la linterna encendida: el charco en la losa y el resplandor en el muro de detrás (antes de pintarla). */
    private fun DrawScope.luzDeLaLinterna(lm: LuzObjeto, lo: LuzObjeto, luz: Float) {
        val k = lo.k()
        halo(lm.q(0.02f, 0.2f, MURO), 0.75f * k, 0.55f * luz)
        clipPath(losa(lm)) { halo(lm.q(0f, 0f, 0.02f), 0.45f * k, 0.8f * luz) }
        halo(lo.q(0f, 0.18f, 0f), 0.42f * k, 0.5f * luz, Color(0xFFFFD890))
    }

    /** La base de hierro: una caja chata de [m] de medio lado y [h] de alto, con sus caras que se ven. */
    private fun DrawScope.base(lo: LuzObjeto, m: Float, h: Float, color: Color) {
        for (n in CARAS) {
            val lado = V3(-n.z, 0f, n.x)
            if (!lo.s.seVe(V3(n.x * m, h / 2f, n.z * m), n)) continue
            fun q(u: Float, y: Float) = lo.q(n.x * m + lado.x * u, y, n.z * m + lado.z * u)
            drawPath(poligono(q(-m, 0f), q(m, 0f), q(m, h), q(-m, h)), lo.c(color, n))
        }
        drawPath(poligono(lo.q(-m, h, -m), lo.q(m, h, -m), lo.q(m, h, m), lo.q(-m, h, m)), lo.c(color, V3(0f, 1f, 0f), 0.15f))
    }

    /** Una de las cuatro ventanas, la que mira a [n]: el cuerno raspado entre dos montantes, con la vela de día o la luz de noche. */
    private fun DrawScope.ventana(lo: LuzObjeto, n: V3, w: Float, alto: Float, luz: Float) {
        val lado = V3(-n.z, 0f, n.x)
        fun q(u: Float, y: Float) = lo.q(n.x * w + lado.x * u, y, n.z * w + lado.z * u)
        val cuerno = poligono(q(-w * 0.8f, 0.06f), q(w * 0.8f, 0.06f), q(w * 0.8f, alto - 0.03f), q(-w * 0.8f, alto - 0.03f))
        drawPath(poligono(q(-w, 0.035f), q(w, 0.035f), q(w, alto), q(-w, alto)), lo.c(O.HIERRO, n))
        val propio = lo.c(O.CUERNO_RASPADO, n, 0.1f)
        if (luz > 0.02f) {
            drawPath(cuerno, Brush.verticalGradient(listOf(lerp(propio, C.LUMBRE, luz), lerp(propio, Color(0xFFFFE6A8), luz), lerp(propio, C.LUMBRE, luz)), q(0f, alto).y, q(0f, 0.06f).y))
            halo(lo.q(0f, 0.17f, 0f), 0.06f * lo.k(), luz, Color(0xFFFFF0C0))
        } else {
            drawPath(cuerno, propio)
            // Dentro, a través del cuerno: el cabo de vela de sebo y su pabilo.
            val v0 = lo.q(-0.022f, 0.06f, 0f); val v1 = lo.q(0.022f, 0.14f, 0f)
            clipPath(cuerno) {
                drawRect(lo.c(O.SEBO, n, -0.3f).copy(alpha = 0.5f), Offset(minOf(v0.x, v1.x), v1.y), Size(kotlin.math.abs(v1.x - v0.x), v0.y - v1.y))
                drawLine(C.ESCORIA.copy(alpha = 0.6f), lo.q(0f, 0.14f, 0f), lo.q(0f, 0.16f, 0f), 0.5f)
            }
        }
        // Las rayas del raspado y el hollín de arriba.
        clipPath(cuerno) {
            for (j in 0 until 9) { val y = 0.07f + j * 0.027f; drawLine(lo.c(O.CUERNO_RASPADO, n, if (j % 2 == 0) 0.3f else -0.3f).copy(alpha = 0.35f * (1f - luz * 0.6f)), q(-w, y), q(w, y + 0.004f), 0.45f) }
            drawRect(Brush.verticalGradient(listOf(C.ESCORIA.copy(alpha = 0.55f), Color.Transparent), q(0f, alto).y, q(0f, alto - 0.11f).y), Offset(q(-w, alto).x - 40f, q(0f, alto).y - 2f), Size(80f, 30f))
        }
        montantes(lo, n, w, alto, ::q)
    }

    /** Los montantes y los zunchos de hierro de una ventana; en la de delante, las bisagras y el pestillo de la portezuela. */
    private fun DrawScope.montantes(lo: LuzObjeto, n: V3, w: Float, alto: Float, q: (Float, Float) -> Offset) {
        for (u in listOf(-w, w)) drawLine(lo.c(O.HIERRO, n, 0.1f), q(u * 0.9f, 0.035f), q(u * 0.9f, alto), 0.018f * lo.k(), StrokeCap.Square)
        for (y in listOf(0.05f, alto - 0.012f)) drawLine(lo.c(O.HIERRO, V3(0f, 1f, 0f) + n, 0.2f), q(-w, y), q(w, y), 0.022f * lo.k())
        if (n.z < 0f) {
            for (y in listOf(0.1f, 0.26f)) punto(q(-w * 0.86f, y), 1.4f, 1.6f, lo.c(O.HIERRO, n, 0.4f))
            drawLine(lo.c(O.HIERRO, n, 0.45f), q(w * 0.55f, 0.19f), q(w * 0.95f, 0.19f), 0.9f, StrokeCap.Round)
        }
    }

    /** El tejadillo de hierro en punta, con la chimenea agujereada. */
    private fun DrawScope.tejadillo(lo: LuzObjeto, w: Float, alto: Float) {
        val cima = V3(0f, alto + 0.1f, 0f); val m = w * 1.12f
        for (n in CARAS) {
            val lado = V3(-n.z, 0f, n.x)
            val nn = LuzObjeto.unidad(V3(n.x, 0.9f, n.z))
            if (!lo.s.seVe(V3(n.x * w * 0.6f, alto + 0.04f, n.z * w * 0.6f), nn)) continue
            val cara = poligono(lo.q(n.x * m - lado.x * m, alto, n.z * m - lado.z * m), lo.q(n.x * m + lado.x * m, alto, n.z * m + lado.z * m), lo.q(cima))
            drawPath(cara, lo.c(O.HIERRO, nn, 0.1f))
            drawPath(cara, C.ESCORIA.copy(alpha = 0.25f), style = Stroke(0.4f))
        }
        redondo(lo, 0f, 0f, listOf(alto + 0.07f to 0.032f, alto + 0.13f to 0.03f), O.HIERRO)
        drawPath(boca(lo, 0f, alto + 0.13f, 0f, 0.03f), lo.c(O.HIERRO, V3(0f, 1f, 0f), 0.2f))
        for (j in 0..2) punto(lo.q(-0.015f + j * 0.015f, alto + 0.1f, -0.03f), 0.9f, 0.9f, C.ESCORIA)
    }

    /** El aro de colgar, de alambre grueso. */
    private fun DrawScope.aro(lo: LuzObjeto, alto: Float) {
        val pts = (0..18).map { j -> val a = PI.toFloat() * (0.1f + 0.8f * j / 18f); lo.q(0.05f * cos(a), alto + 0.13f + 0.065f * sin(a), 0f) }
        trazo(pts, lo.c(O.HIERRO, V3(0f, 0.6f, -1f), 0.2f), 0.012f * lo.k())
        trazo(pts.take(9), lo.base.luz(lerp(O.HIERRO, C.NIEVE, 0.4f)).copy(alpha = 0.5f * lo.r.contraste), 0.004f * lo.k())
    }

    // ---------------------------------------------------------------- El cuerno

    /** El eje del cuerno de guía, de la boquilla a la boca ancha, en metros: una media luna que apoya la panza en la losa. */
    private val EJE_CUERNO = listOf(V3(-0.29f, 0.135f, 0f), V3(-0.22f, 0.07f, 0.012f), V3(-0.13f, 0.036f, 0.026f), V3(-0.03f, 0.033f, 0.036f), V3(0.07f, 0.05f, 0.04f), V3(0.16f, 0.1f, 0.034f), V3(0.22f, 0.165f, 0.02f), V3(0.25f, 0.22f, 0.006f)).map { it + V3(0.1f, 0f, 0f) }
    private val RADIO_CUERNO = listOf(0.012f, 0.016f, 0.022f, 0.03f, 0.037f, 0.044f, 0.052f, 0.06f)

    private fun enEje(t: Float): V3 { val f = t.coerceIn(0f, 1f) * (EJE_CUERNO.size - 1); val i = f.toInt().coerceAtMost(EJE_CUERNO.size - 2); val u = f - i; return EJE_CUERNO[i] * (1f - u) + EJE_CUERNO[i + 1] * u }
    private fun radioEn(t: Float): Float { val f = t.coerceIn(0f, 1f) * (RADIO_CUERNO.size - 1); val i = f.toInt().coerceAtMost(RADIO_CUERNO.size - 2); val u = f - i; return RADIO_CUERNO[i] * (1f - u) + RADIO_CUERNO[i + 1] * u }

    /**
     * El cuerno de guía, «de carnero, con la boca de latón»: tumbado sobre su panza en la losa, de perfil, con la curva en
     * alto. Liso y pulido por el uso, con sus anillos de crecimiento; claro en la boca ancha y oscuro hacia la punta, donde
     * lleva la boquilla de latón; dos abrazaderas de latón para la correa de cuero, que cae floja por la losa. No es el
     * cuerno de la puerta (que da vuelta y media en espiral y está clavado). 1,35 veces más grande que uno de verdad.
     */
    private fun DrawScope.cuerno(lm: LuzObjeto) {
        val lo = lm.conGiro(LuzObjeto.giroDeFrente(lm.s.pie, lm.s.k) + 0.15f)
        contacto(lo, 0.08f, 0.03f, 0.22f, 0.08f)
        sombraDe(lm, lo, EJE_CUERNO.flatMapIndexed { i, a -> listOf(a + V3(0f, RADIO_CUERNO[i], 0f), a + V3(0f, -RADIO_CUERNO[i], 0f), a + V3(0f, 0f, RADIO_CUERNO[i])) })
        val forma = tubo(lo, EJE_CUERNO, RADIO_CUERNO, O.HUESO, 0.7f)
        clipPath(forma) {
            // Más oscuro hacia la punta; los anillos de crecimiento, más marcados en la parte ancha.
            drawRect(Brush.linearGradient(listOf(lo.c(O.PUNTA, V3(0f, 0.3f, -1f)).copy(alpha = 0.92f), Color.Transparent), lo.q(EJE_CUERNO[0]), lo.q(EJE_CUERNO[4])), Offset(0f, 0f), Size(500f, 400f))
            for (i in 1 until 22) {
                val t = i / 22f; val a = enEje(t); val d = enEje(t + 0.02f) - enEje(t - 0.02f)
                val r = radioEn(t) * 1.1f; val n = LuzObjeto.unidad(V3(-d.y, d.x, 0f)) * r
                drawLine(lo.c(O.HUESO, V3(0f, 0.2f, -1f), -0.5f).copy(alpha = 0.18f + 0.3f * t), lo.q(a + n), lo.q(a - n), 0.4f)
            }
        }
        bocaDelCuerno(lo)
        laton(lo)
        correa(lm, lo)
        empolvado(lo, (2..7).map { lo.q(EJE_CUERNO[it] + V3(0f, RADIO_CUERNO[it] * 0.95f, 0f)) }, 1.1f)
        labio(lm, 0f, 0f, 0.5f)
    }

    /** La boca ancha: el canto del cuerno, que se ve de sesgo, y lo hueco por dentro. */
    private fun DrawScope.bocaDelCuerno(lo: LuzObjeto) {
        val a = EJE_CUERNO.last(); val eje = a - EJE_CUERNO[EJE_CUERNO.size - 2]; val r = RADIO_CUERNO.last()
        drawPath(anillo(lo, a, eje, r), lo.c(O.HUESO, eje, 0.35f))
        drawPath(anillo(lo, a + LuzObjeto.unidad(eje) * 0.004f, eje, r * 0.78f), lerp(lo.c(C.ESCORIA, V3(-1f, 0f, 0f)), C.CUERO, 0.25f))
    }

    /** La boquilla de latón en la punta y las dos abrazaderas de la correa. */
    private fun DrawScope.laton(lo: LuzObjeto) {
        val p0 = EJE_CUERNO[0]; val eje = V3(-0.045f, 0.034f, 0f)
        tubo(lo, listOf(p0 + V3(0.012f, -0.012f, 0f), p0, p0 + eje * 0.6f, p0 + eje), listOf(0.015f, 0.013f, 0.01f, 0.011f), O.LATON, 0.95f)
        drawPath(anillo(lo, p0 + eje * 1.08f, eje, 0.017f), lo.c(O.LATON, eje, 0.3f))
        drawPath(anillo(lo, p0 + eje * 1.1f, eje, 0.007f), lo.c(C.ESCORIA, V3(0f, 0f, -1f)))
        for (t in listOf(0.11f, 0.8f)) { val r = radioEn(t) * 1.12f; tubo(lo, listOf(enEje(t), enEje(t + 0.025f)), listOf(r, r), O.LATON, 0.9f) }
    }

    /** La correa de cuero, de abrazadera a abrazadera: sale hacia delante, cruza la losa y cuelga en lazo por el canto del poyo. */
    private fun DrawScope.correa(lm: LuzObjeto, lo: LuzObjeto) {
        val d = lo.s.giro - lm.s.giro
        val a = lm.girado(enEje(0.11f) + V3(0f, -0.01f, -0.02f), d); val b = lm.girado(enEje(0.8f) + V3(0f, -0.02f, -0.03f), d)
        val m = (a.x + b.x) / 2f
        val tramo = listOf(a, V3(a.x + 0.012f, 0.04f, a.z - 0.03f), V3(a.x + 0.03f, 0.005f, a.z - 0.07f), V3(a.x + 0.08f, 0.004f, CANTO + 0.01f), V3(m - 0.04f, -0.035f, CANTO - 0.012f),
            V3(m + 0.05f, -0.045f, CANTO - 0.013f), V3(b.x - 0.1f, 0.004f, CANTO + 0.01f), V3(b.x - 0.05f, 0.005f, b.z - 0.08f), V3(b.x - 0.02f, 0.07f, b.z - 0.05f), b)
        tubo(lm, tramo, tramo.map { 0.0095f }, lerp(C.CUERO, C.ESCORIA, 0.18f), 0.35f)
    }

    // ---------------------------------------------------------------- La cecina

    /**
     * Las tiras de cecina, «de cabra o de oveja, salada y curada al humo del hogar»: dobladas sobre una vara que cuelga
     * del clavo del muro por su cordel, sobre el poyo, a secar; cada tira distinta, arrugada, con su costra oscura de humo,
     * sus vetas de grasa y sus puntas cortadas más rojas. Debajo, en la losa, el paño de lana cruda en que se bajaron,
     * caído por el canto. 1,3 veces más grande.
     */
    private fun DrawScope.cecina(lm: LuzObjeto) {
        val zv = MURO - 0.05f; val yv = 0.52f; val xa = -0.15f; val xb = 0.35f
        val tiras = (0 until 5).map { i -> Triple(xa + 0.07f + i * (xb - xa - 0.14f) / 4f, 0.27f + 0.1f * ((i * 37 + lm.e.dia) % 5) / 4f, 0.062f + 0.016f * ((i * 13) % 3) / 2f) }
        sombra(lm, tiras.flatMap { (x, largo, w) -> listOf(V3(x - w, yv, zv), V3(x + w, yv, zv), V3(x, yv - largo, zv - 0.01f)) } + listOf(V3(xa, yv, zv), V3(xb, yv, zv)), MURO, losa(lm), muro(lm))
        pano(lm)
        for ((x, largo, w) in tiras) tira(lm, x + 0.012f, yv, zv + 0.015f, largo * 0.6f, w * 0.9f, atras = true)
        for (s in listOf(xa + 0.02f, xb - 0.02f)) trazo(listOf(lm.q(CLAVO), lm.q(s, yv + 0.01f, zv)), lm.c(lerp(C.LANA, C.CUERO, 0.3f), V3(0f, 0f, -1f)), 0.5f)
        punto(lm.q(CLAVO), 1.6f, 1.6f, lm.c(O.HIERRO, V3(0f, 0.3f, -1f), 0.3f))
        tubo(lm, listOf(V3(xa, yv, zv), V3(xb, yv + 0.01f, zv)), listOf(0.011f, 0.011f), C.FRESNO, 0.5f)
        for ((x, largo, w) in tiras) tira(lm, x, yv, zv - 0.012f, largo, w, atras = false)
        empolvado(lm, listOf(lm.q(xa, yv + 0.012f, zv), lm.q(xb, yv + 0.02f, zv)), 1f)
        labio(lm, 0.1f, CANTO + 0.05f, 0.5f)
    }

    /** Una tira de cecina colgada de la vara en [x]: la mitad de delante (o la de detrás), arrugada, con la costra, la grasa y la punta cortada. */
    private fun DrawScope.tira(lm: LuzObjeto, x: Float, yv: Float, z: Float, largo: Float, w: Float, atras: Boolean) {
        val az = SitioAntiguas.fijo("cecina", (x * 1000).toInt())
        val n = 8
        // El ancho cambia a lo largo (la tira se tuerce y se encoge al secarse) y los bordes van arrugados.
        val ancho = (0..n).map { j -> w * (0.75f + 0.25f * sin(j * 1.3f + x * 40f)) * (1f - 0.25f * j / n) }
        val izq = (0..n).map { j -> val v = j / n.toFloat(); lm.q(x - ancho[j] / 2f + az.entre(-0.007f, 0.007f) + 0.012f * v, yv - largo * v, z - 0.004f * v) }
        val der = (0..n).map { j -> val v = j / n.toFloat(); lm.q(x + ancho[j] / 2f + az.entre(-0.007f, 0.007f) + 0.006f * v, yv - largo * v - 0.012f * v, z - 0.004f * v) }
        val forma = poligono(izq + der.reversed())
        val nf = V3(0f, 0.15f, -1f)
        drawPath(forma, Brush.horizontalGradient(listOf(lm.c(O.CECINA, V3(-0.7f, 0f, -0.7f), if (atras) -0.4f else 0f), lm.c(O.CECINA, nf, if (atras) -0.3f else 0.1f), lm.c(lerp(O.CECINA, C.ESCORIA, 0.35f), V3(0.7f, 0f, -0.7f))), izq[0].x, der[0].x))
        if (atras) return
        clipPath(forma) {
            for (k in 0..1) { val u = az.entre(-0.3f, 0.3f) * w; trazo((0..4).map { j -> lm.q(x + u + az.entre(-0.006f, 0.006f) + 0.008f * j / 4f, yv - largo * (0.08f + 0.2f * j), z - 0.005f) }, lm.c(O.SEBO, nf, 0.1f).copy(alpha = 0.6f), 0.55f) }
            for (j in 1 until n) drawLine(lm.c(C.ESCORIA, nf).copy(alpha = 0.35f), izq[j], izq[j] + (der[j] - izq[j]) * az.entre(0.3f, 0.7f), 0.4f)
            drawPath(poligono(izq + der.reversed()), lm.c(lerp(O.CECINA, C.ESCORIA, 0.5f), nf).copy(alpha = 0.5f), style = Stroke(0.9f))
            drawLine(lm.c(lerp(C.ROJIZA, C.RUBI, 0.4f), nf, 0.25f), izq[n], der[n], 1.8f)
        }
        // El doblez sobre la vara, más claro.
        drawLine(lm.c(lerp(O.CECINA, C.ROJIZA, 0.3f), V3(0f, 1f, -0.4f), 0.3f), izq[0], der[0], 1.2f, StrokeCap.Round)
    }

    /** El paño de lana cruda en la losa, caído por el canto de delante del poyo, con su borde deshilachado y su raya roja. */
    private fun DrawScope.pano(lm: LuzObjeto) {
        val x0 = -0.08f; val x1 = 0.3f; val cae = 0.13f
        drawPath(poligono(lm.q(x0, 0.004f, CANTO), lm.q(x1, 0.004f, CANTO), lm.q(x1 - 0.02f, 0.004f, CANTO + 0.2f), lm.q(x0 + 0.03f, 0.004f, CANTO + 0.2f)), lm.c(C.LANA, V3(0f, 1f, 0f), 0.1f))
        val caida = poligono(lm.q(x0, 0.004f, CANTO), lm.q(x1, 0.004f, CANTO), lm.q(x1 + 0.01f, -cae, CANTO - 0.012f), lm.q(x0 + 0.02f, -cae + 0.02f, CANTO - 0.012f))
        drawPath(caida, Brush.horizontalGradient(listOf(lm.c(C.LANA, V3(0f, 0f, -1f), -0.1f), lm.c(C.LANA, V3(0.3f, 0f, -1f), 0.15f), lm.c(C.LANA, V3(-0.4f, 0f, -1f), -0.25f)), lm.q(x0, 0f, CANTO).x, lm.q(x1, 0f, CANTO).x))
        clipPath(caida) { for (j in 1..3) { val x = x0 + (x1 - x0) * j / 4f; drawLine(lm.c(C.LANA, V3(0.6f, 0f, -0.8f), -0.35f).copy(alpha = 0.45f), lm.q(x, 0f, CANTO - 0.012f), lm.q(x + 0.01f, -cae, CANTO - 0.012f), 1.2f) } }
        drawLine(lm.c(C.RUBI, V3(0f, 0f, -1f)).copy(alpha = 0.75f), lm.q(x0 + 0.012f, -cae + 0.055f, CANTO - 0.013f), lm.q(x1 + 0.004f, -cae + 0.04f, CANTO - 0.013f), 0.9f)
    }

    // ---------------------------------------------------------------- La jarra

    /**
     * La jarra de barro, «de barro cocido con una raya negra», con su marca: la de esta tiene rayada una raya con una tilde
     * arriba y otra abajo, de la familia de las marcas de la piedra de los años. Llena de cerveza oscura con su espuma, el
     * asa a la derecha y el cordel con que cuelga de la viga, suelto en la losa. 2,5 veces más grande.
     */
    private fun DrawScope.jarra(lm: LuzObjeto) {
        val lo = lm.conGiro(LuzObjeto.giroDeFrente(lm.s.pie, lm.s.k) + 0.25f)
        val g = 2.5f; val r = 0.0445f * g; val y = 0.156f * g
        val perfil = listOf(0f to 0.04f, 0.012f to 0.045f, 0.06f to 0.05f, 0.11f to 0.048f, 0.15f to 0.044f, 0.156f to 0.0445f).map { (a, b) -> a * g to b * g }
        contacto(lm, 0f, 0f, 0.3f, 0.22f)
        sombraDe(lm, lo, (0 until 8).flatMap { j -> val a = j * PI.toFloat() / 4f; listOf(V3(0.12f * cos(a), 0f, 0.12f * sin(a)), V3(0.12f * cos(a), 0.39f, 0.12f * sin(a))) } + V3(0.22f, 0.25f, 0f))
        val cuerpo = redondo(lo, 0f, 0f, perfil, O.BARRO)
        clipPath(cuerpo) {
            banda(lo, 0.098f * g, 0.12f * g, 0.0495f * g, C.ESCORIA)
            for (j in 0 until 6) drawLine(lo.c(O.BARRO, V3(0f, 0f, -1f), -0.25f).copy(alpha = 0.25f), lo.q(-0.06f * g, (0.02f + 0.02f * j) * g, -0.05f * g), lo.q(0.06f * g, (0.02f + 0.02f * j) * g, -0.05f * g), 0.3f)
        }
        marcaDeDueno(lo, g)
        drawPath(boca(lo, 0f, y, 0f, r), lo.c(O.BARRO, V3(0f, 1f, 0f), 0.2f))
        drawPath(boca(lo, 0f, y - 0.004f, 0f, r * 0.84f), lo.c(lerp(O.SEBO, O.CERVEZA, 0.25f), V3(0f, 1f, 0f), 0.1f))
        drawPath(boca(lo, 0f, y - 0.004f, 0.01f, r * 0.56f), lo.c(O.CERVEZA, V3(0f, 1f, 0f)))
        val asa = listOf(V3(0.046f, 0.13f, 0f), V3(0.075f, 0.128f, 0f), V3(0.083f, 0.09f, 0f), V3(0.07f, 0.05f, 0f), V3(0.048f, 0.04f, 0f)).map { it * g }
        tubo(lo, asa, asa.map { 0.0085f * g }, O.BARRO, 0.5f)
        cordel(lo, g)
        empolvado(lo, listOf(lo.q(-r, y, 0f), lo.q(0f, y, -r), lo.q(r, y, 0f)), 0.9f)
        labio(lm, 0f, 0f, 0.3f)
    }

    /** Una banda de [color] alrededor de un cuerpo redondo, de [y0] a [y1] con radio [r]: la mitad que mira a quien mira. */
    private fun DrawScope.banda(lo: LuzObjeto, y0: Float, y1: Float, r: Float, color: Color) {
        val cam = kotlin.math.atan2(lo.aCamara.z, lo.aCamara.x)
        fun arco(y: Float) = (0..16).map { j -> val th = cam - PI.toFloat() / 2f + PI.toFloat() * j / 16f; val d = lo.girado(V3(cos(th), 0f, sin(th)), -lo.s.giro); lo.q(d.x * r, y, d.z * r) }
        val a0 = arco(y0); val a1 = arco(y1)
        val paradas = listOf(0f, 0.15f, 0.5f, 0.85f, 1f).map { it to lo.redondo(color, it) }
        drawPath(poligono(a0 + a1.reversed()), Brush.horizontalGradient(*paradas.toTypedArray(), startX = (a0 + a1).minOf { it.x }, endX = (a0 + a1).maxOf { it.x }))
    }

    /** La marca del dueño, rayada en el barro de delante: una raya con un punto encima a un lado y otro debajo al otro (no es una letra). */
    private fun DrawScope.marcaDeDueno(lo: LuzObjeto, g: Float) {
        val z = -0.05f * g
        val c = lo.c(lerp(O.BARRO, C.LANA, 0.55f), V3(0f, 0f, -1f), 0.25f)
        val a = lo.q(-0.024f * g, 0.062f * g, z); val b = lo.q(0.022f * g, 0.065f * g, z)
        drawLine(c, a, b, 0.75f, StrokeCap.Round)
        punto(a + (b - a) * 0.22f + Offset(0f, -2.2f), 1.3f, 1.2f, c)
        punto(a + (b - a) * 0.78f + Offset(0f, 2.2f), 1.3f, 1.2f, c)
    }

    /** El cordel de colgar de la viga, atado al asa y suelto en la losa, en un par de vueltas flojas por delante. */
    private fun DrawScope.cordel(lo: LuzObjeto, g: Float) {
        val eje = listOf(V3(0.072f * g, 0.05f * g, -0.004f), V3(0.09f * g, 0.025f * g, -0.02f), V3(0.1f * g, 0.004f, -0.05f), V3(0.16f * g, 0.004f, -0.07f),
            V3(0.17f * g, 0.004f, -0.13f), V3(0.11f * g, 0.004f, -0.15f), V3(0.07f * g, 0.004f, -0.1f), V3(0.12f * g, 0.004f, -0.06f))
        tubo(lo, eje, eje.map { 0.0045f }, lerp(C.LANA, C.CUERO, 0.35f), 0.2f)
    }

    // ---------------------------------------------------------------- El pedernal

    /**
     * El pedernal y el eslabón, con «una bolsita de cuero con yesca seca de hongo»: la bolsita de pie, abierta, con su
     * cordón y la yesca que asoma; el eslabón de acero en C recostado en ella, con el canto de golpear brillante; el
     * pedernal delante, con su corteza blanca y una cara de lasca lisa que brilla; y un trozo de yesca suelto. 2,7 veces más grande.
     */
    private fun DrawScope.pedernal(lm: LuzObjeto) {
        val lo = lm.conGiro(LuzObjeto.giroDeFrente(lm.s.pie, lm.s.k) + 0.3f)
        val g = 2.7f
        contacto(lm, 0f, 0f, 0.42f, 0.24f)
        sombraDe(lm, lo, (0 until 8).flatMap { j -> val a = j * PI.toFloat() / 4f; listOf(V3(-0.04f + 0.12f * cos(a), 0f, 0.03f + 0.12f * sin(a)), V3(-0.04f + 0.08f * cos(a), 0.28f, 0.03f + 0.08f * sin(a))) } + listOf(V3(0.2f, 0.18f, -0.05f), V3(0.24f, 0f, -0.05f)))
        bolsita(lo, g)
        yescaSuelta(lo, g)
        eslabon(lo, g)
        piedra(lo, g)
        labio(lm, 0f, -0.02f, 0.45f)
    }

    /** La bolsita de cuero de pie, abultada, con su costura, el fruncido del cuello, el cordón y la yesca que asoma. */
    private fun DrawScope.bolsita(lo: LuzObjeto, g: Float) {
        val cx = -0.015f * g; val cz = 0.012f * g
        val perfil = listOf(0f to 0.028f, 0.012f to 0.043f, 0.04f to 0.047f, 0.07f to 0.04f, 0.088f to 0.027f, 0.096f to 0.022f, 0.104f to 0.026f).map { (y, r) -> y * g to r * g }
        val cuero = lerp(C.CUERO, C.TIERRA, 0.2f); val cordon = lerp(C.LANA, C.CUERO, 0.3f)
        val forma = redondo(lo, cx, cz, perfil, cuero)
        clipPath(forma) {
            for (j in 0 until 5) trazo(listOf(lo.q(cx - 0.03f * g + j * 0.015f * g, 0.012f * g, cz - 0.045f * g), lo.q(cx - 0.02f * g + j * 0.01f * g, 0.05f * g, cz - 0.05f * g), lo.q(cx - 0.012f * g + j * 0.006f * g, 0.09f * g, cz - 0.03f * g)), lo.c(cuero, V3(0f, 0f, -1f), -0.5f).copy(alpha = 0.35f), 0.35f)
            for (j in 0 until 7) punto(lo.q(cx + 0.038f * g, (0.015f + j * 0.011f) * g, cz - 0.022f * g), 0.5f, 0.9f, lo.c(lerp(C.LANA, cuero, 0.3f), V3(1f, 0f, -1f)).copy(alpha = 0.8f))
        }
        val yesca = lerp(O.YESCA, C.ESCORIA, 0.25f)
        val mata = listOf(V3(-0.026f, 0.098f, -0.01f), V3(-0.02f, 0.118f, -0.016f), V3(-0.006f, 0.112f, -0.02f), V3(0.004f, 0.124f, -0.018f), V3(0.018f, 0.114f, -0.016f), V3(0.026f, 0.1f, -0.01f)).map { lo.q(cx + it.x * g, it.y * g, cz + it.z * g) }
        drawPath(poligono(mata), lo.c(yesca, V3(0f, 1f, -0.4f), 0.05f))
        for (j in 0 until 7) { val a = mata[1] + (mata[4] - mata[1]) * (j / 6f); drawLine(lo.c(lerp(yesca, C.LANA, 0.3f), V3(0f, 1f, -0.4f)).copy(alpha = 0.6f), a, a + Offset((j % 3 - 1) * 0.7f, -1.2f - (j % 2) * 0.6f), 0.35f) }
        val cuello = (0 until 9).map { j -> val a = PI.toFloat() * (1f + j / 8f); lo.q(cx + 0.024f * g * cos(a), 0.1f * g, cz + 0.02f * g * sin(a)) }
        trazo(cuello, lo.c(cordon, V3(0f, 0.2f, -1f), 0.2f), 0.012f * lo.k())
        tubo(lo, listOf(V3(cx, 0.098f * g, cz - 0.022f * g), V3(cx + 0.03f * g, 0.07f * g, cz - 0.05f * g), V3(cx + 0.05f * g, 0.004f, cz - 0.07f * g)), listOf(0.004f, 0.004f, 0.004f), cordon, 0.2f)
        punto(cuello[4], 1.6f, 1.3f, lo.c(cordon, V3(0f, 0.4f, -1f), 0.2f))
        empolvado(lo, listOf(lo.q(cx - 0.03f * g, 0.104f * g, cz), lo.q(cx + 0.03f * g, 0.104f * g, cz)), 0.9f)
    }

    /** Un trozo de yesca suelto en la losa: hongo seco, pardo y aterciopelado. */
    private fun DrawScope.yescaSuelta(lo: LuzObjeto, g: Float) {
        val c = V3(-0.09f * g, 0f, -0.04f * g); val t = 1.5f
        val forma = poligono(listOf(V3(-0.03f, 0f, 0f), V3(-0.026f, 0.014f, -0.004f), V3(-0.006f, 0.02f, -0.008f), V3(0.016f, 0.017f, -0.008f), V3(0.03f, 0.006f, -0.004f), V3(0.028f, 0f, 0f)).map { lo.q(c + it * (g * t)) })
        val yesca = lerp(O.YESCA, C.ESCORIA, 0.2f)
        drawPath(forma, Brush.verticalGradient(listOf(lo.c(yesca, V3(0f, 1f, -0.3f), 0.25f), lo.c(yesca, V3(0f, 0f, -1f), -0.25f)), lo.q(c + V3(0f, 0.02f * g * t, 0f)).y, lo.q(c).y))
        for (j in 0 until 4) drawLine(lo.c(lerp(yesca, C.ESCORIA, 0.4f), V3(0f, 0f, -1f)).copy(alpha = 0.5f), lo.q(c + V3(-0.024f, 0.004f + j * 0.004f, -0.006f) * (g * t)), lo.q(c + V3(0.026f, 0.003f + j * 0.003f, -0.006f) * (g * t)), 0.35f)
    }

    /**
     * El eslabón de lazos: una barra de acero, la de golpear, con los dos cabos enroscados hacia arriba en un lazo cada
     * uno (por ahí se agarra con los dedos), recostado en la bolsita; el canto de abajo, limpio y brillante.
     */
    private fun DrawScope.eslabon(lo: LuzObjeto, g: Float) {
        val c = V3(0.055f * g, 0f, -0.04f * g)
        fun lazo(lado: Float) = (0..9).map { j -> val t = j / 9f; val a = -PI.toFloat() / 2f + 5.3f * t; val r = 0.014f - 0.008f * t; V3(lado * (0.034f + r * cos(a)), 0.02f + r * sin(a), 0f) }
        val izq = lazo(-1f).reversed(); val der = lazo(1f)
        val plano = (izq + der).map { a -> c + V3(a.x * g, (a.y + 0.004f) * g, (a.y * 0.35f) * g) }
        tubo(lo, plano, plano.map { 0.0036f * g }, O.HIERRO, 0.9f)
        val canto = plano.subList(izq.size - 1, izq.size + 1)
        trazo(canto.map { lo.q(it + V3(0f, -0.004f * g, -0.003f * g)) }, lo.base.luz(lerp(O.HIERRO, C.NIEVE, 0.55f)).copy(alpha = (0.3f + 0.6f * lo.r.contraste).coerceIn(0f, 1f)), 0.5f)
        if (lo.calida > 0.05f) trazo(canto.map { lo.q(it) }, C.LUMBRE.copy(alpha = (0.6f * lo.calida).coerceIn(0f, 1f)), 0.5f)
    }

    /** El pedernal: un canto de sílex con su corteza blanca y la cara de lasca, lisa y oscura, con su brillo. */
    private fun DrawScope.piedra(lo: LuzObjeto, g: Float) {
        val c = V3(-0.01f * g, 0f, -0.085f * g)
        fun q(x: Float, y: Float, z: Float) = lo.q(c + V3(x, y, z) * (g * 1.35f))
        val corteza = poligono(q(-0.024f, 0f, 0f), q(-0.026f, 0.012f, 0.004f), q(-0.012f, 0.022f, 0.008f), q(0.004f, 0.02f, 0.006f), q(-0.004f, 0.006f, -0.01f))
        val lasca = poligono(q(-0.004f, 0.006f, -0.01f), q(0.004f, 0.02f, 0.006f), q(0.022f, 0.014f, 0f), q(0.026f, 0f, -0.004f), q(0f, 0f, -0.012f))
        drawPath(corteza, lo.c(lerp(O.SEBO, C.PIEDRA, 0.25f), V3(-0.6f, 0.6f, -0.5f), 0.1f))
        drawPath(lasca, lo.c(O.PEDERNAL, V3(0.5f, 0.6f, -0.6f)))
        drawLine(lo.base.luz(lerp(O.PEDERNAL, C.NIEVE, 0.6f)).copy(alpha = (0.25f + 0.55f * lo.r.contraste).coerceIn(0f, 1f)), q(0.004f, 0.016f, 0.002f), q(0.016f, 0.012f, -0.002f), 0.7f, StrokeCap.Round)
        drawPath(lasca, C.ESCORIA.copy(alpha = 0.3f), style = Stroke(0.35f))
    }
}
