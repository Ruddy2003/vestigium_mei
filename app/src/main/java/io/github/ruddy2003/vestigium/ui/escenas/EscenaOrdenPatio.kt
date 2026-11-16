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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.palo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.sillares
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.cos
import kotlin.math.sin

/**
 * El patio de Peñaseca, abajo a la izquierda, visto desde lo alto del adarve. **[Diseño propio]**: el cuento no da un
 * plano; junta aquí lo que nombra (la fragua «junto a la muralla del oeste», la Cámara «en el corazón de la ciudadela»
 * con su única ventana, el archivo con su ventana alta, la sala del Código, la enfermería, la cisterna con su escalera y
 * sus canales, el corral de las cabras, la campana y la puerta de la escalera de la meseta en el cuarto lado).
 *
 * Los edificios son de la misma piedra sin labrar que las murallas, con azoteas planas de losa (por ahí bajan los canales
 * a la cisterna), cada uno girado a su modo (la ciudadela no está trazada a cordel) para que se vean en tres cuartos.
 */
internal object PatioOrden {
    private val C = ColorOrden
    private const val P = CamOrden.PATIO

    /** La cara de dentro de la muralla del fondo (la del cuarto lado, con la puerta de la escalera). */
    const val FONDO = 60f
    /** Lo más a la izquierda que se pinta del patio. */
    private const val IZQ = -80f

    /** Un edificio del patio: su base (centro, giro, ancho y fondo) y lo alto de su azotea. */
    class Edificio(val s: Float, val v: Float, val giro: Float, val ancho: Float, val fondo: Float, val alto: Float) {
        val esquinas: List<V3> get() = PiezasOrden.esquinas(s, v, giro, ancho, fondo).flatMap { listOf(V3(it.x, P, it.y), V3(it.x, alto, it.y)) }
        /** Un punto de su cara [i] (0 delante, 1 detrás, 2 izquierda, 3 derecha) a lo largo [u] (de 0 a 1) y a la altura [h]. */
        fun enCara(i: Int, u: Float, h: Float): V3 {
            val q = PiezasOrden.esquinas(s, v, giro, ancho, fondo)
            val (a, b) = when (i) { 0 -> q[0] to q[1]; 1 -> q[3] to q[2]; 2 -> q[0] to q[3]; else -> q[1] to q[2] }
            return V3(a.x + (b.x - a.x) * u, h, a.y + (b.y - a.y) * u)
        }
    }

    /** La Cámara: «una sala de muros gruesos con una sola ventana y una puerta de hierro». Su ventana, en la cara de la derecha. */
    val CAMARA = Edificio(-9.5f, 27f, 0.55f, 6.5f, 6f, -1.8f)
    /** El archivo, «una sala larga sin más ventana que una alta», contra la muralla del fondo. */
    val ARCHIVO = Edificio(-12f, 50.5f, -0.12f, 11f, 6f, -0.8f)
    /** La sala del Código y el refectorio: la nave grande, con sus ventanas altas y la espadaña de la campana. */
    val SALA = Edificio(-31f, 46f, 0.28f, 15f, 8f, 0.2f)
    /** La enfermería, en el ala sur, «con una ventana que da al camino de las patrullas». */
    val ENFERMERIA = Edificio(-22f, 33f, 0.42f, 8f, 5.5f, -3.2f)

    val EDIFICIOS = listOf(SALA, ARCHIVO, ENFERMERIA, CAMARA)

    /** La boca de la cisterna: el centro, el ancho (a través) y el largo (a lo largo de la muralla), en el patio junto a la muralla. */
    val CISTERNA = Offset(-3.9f, 19.5f)
    const val CISTERNA_ANCHO = 2.6f
    const val CISTERNA_LARGO = 3.6f
    /** La fragua, arrimada a la muralla del oeste: de [FRAGUA_V0] a [FRAGUA_V1] a lo largo y hasta [FRAGUA_S] hacia dentro. */
    const val FRAGUA_V0 = 29f
    const val FRAGUA_V1 = 36f
    const val FRAGUA_S = -5.6f
    /** La puerta de la escalera de la meseta, en la muralla del fondo. */
    const val PUERTA_S = -24f

    // ---------------------------------------------------------------- El suelo

    /** El suelo del patio en el lienzo (para recortar las sombras que caen en él). */
    fun formaDelSuelo(): Path = poligono(listOf(V3(CamOrden.BORDE, P, 12f), V3(CamOrden.BORDE, P, FONDO), V3(IZQ, P, FONDO), V3(IZQ, P, 45f), V3(-30f, P, 12f)).map { CamOrden.p(it) })

    /** La cara de dentro de la muralla del fondo, en el lienzo. */
    fun formaDelFondo(): Path = poligono(listOf(V3(CamOrden.BORDE, P, FONDO), V3(CamOrden.BORDE, 0f, FONDO), V3(IZQ, 0f, FONDO), V3(IZQ, P, FONDO)).map { CamOrden.p(it) })

    /**
     * El suelo del patio: tierra batida y losas, más gastado en los caminos (de la puerta a la Cámara, a la cisterna y a
     * la fragua), con los canales de piedra tapados que bajan de las azoteas a la cisterna.
     */
    fun DrawScope.suelo(v: VistaOrden) {
        val forma = formaDelSuelo(); val lz = v.lz
        val tierra = lz.color(lerp(C.ARENA, C.LOSA, 0.12f), V3(0f, 1f, 0f), 0.08f)
        val lejos = lz.color(lerp(C.ARENA, C.LOSA, 0.12f), V3(0f, 1f, 0f), 0.3f)
        drawPath(forma, Brush.verticalGradient(0f to lejos, 1f to tierra, startY = 150f, endY = 285f))
        val az = PiezasOrden.fijo("patio")
        clipPath(forma) {
            // Losas en perspectiva: hiladas a lo largo de la muralla, cada vez más finas al fondo.
            val junta = lz.color(lerp(C.LOSA, C.HOLLIN, 0.35f), V3(0f, 1f, 0f), 0.1f).copy(alpha = 0.35f)
            var vv = 12f
            while (vv < FONDO) { drawLine(junta, CamOrden.p(CamOrden.BORDE, P, vv), CamOrden.p(IZQ, P, vv), 0.5f); vv += 1.6f + vv * 0.02f }
            var ss = CamOrden.BORDE
            while (ss > IZQ) { drawLine(junta.copy(alpha = 0.22f), CamOrden.p(ss + az.entre(-0.3f, 0.3f), P, 12f), CamOrden.p(ss, P, FONDO), 0.4f); ss -= az.entre(1.2f, 2.4f) }
            // Manchas de tierra y de humedad, y la arena que el viento arrima a los pies de los muros.
            repeat(30) {
                val s = az.entre(-40f, -1f); val vv2 = az.entre(14f, FONDO); val q = CamOrden.p(s, P, vv2); val k = CamOrden.k(s, vv2)
                mancha(q, k * az.entre(0.8f, 2.4f), k * az.entre(0.25f, 0.6f), if (it % 2 == 0) lz.color(C.ARENA, V3(0f, 1f, 0f), 0.1f) else lz.color(C.LOSA, V3(0f, 1f, 0f), 0.1f), 0.35f)
            }
            canales(v)
        }
    }

    /** Los canales de piedra que bajan de las azoteas y van, tapados con losas, hasta la boca de la cisterna. */
    private fun DrawScope.canales(v: VistaOrden) {
        val destino = V3(CISTERNA.x - CISTERNA_ANCHO / 2f, P, CISTERNA.y)
        val origenes = listOf(CAMARA.enCara(3, 0.5f, P), ENFERMERIA.enCara(3, 0.3f, P), ARCHIVO.enCara(0, 0.8f, P), V3(FRAGUA_S, P, FRAGUA_V0))
        val tapa = v.lz.color(lerp(C.PIEDRA, C.ARENA, 0.3f), V3(0f, 1f, 0f), 0.1f); val canto = v.lz.color(C.PIEDRA_OSC, V3(0f, 1f, 0f), 0.1f)
        val agua = EscenaOrden.canalesCorren(v.m) || EscenaOrden.tapasLevantadas(v.m)
        for (o in origenes) {
            val n = 8
            for (i in 0 until n) {
                val t0 = i / n.toFloat(); val t1 = (i + 0.86f) / n
                val a = V3(o.x + (destino.x - o.x) * t0, P, o.z + (destino.z - o.z) * t0); val b = V3(o.x + (destino.x - o.x) * t1, P, o.z + (destino.z - o.z) * t1)
                val w = 0.28f
                val q = listOf(V3(a.x - w, P, a.z), V3(b.x - w, P, b.z), V3(b.x + w, P, b.z), V3(a.x + w, P, a.z)).map { CamOrden.p(it) }
                val levantada = EscenaOrden.tapasLevantadas(v.m) && i % 2 == 0
                drawPath(poligono(q), if (levantada) (if (agua) v.l.tono(lerp(C.VIDRIO, C.HOLLIN, 0.45f)) else canto) else tapa)
                drawPath(poligono(q), canto.copy(alpha = 0.5f), style = Stroke(0.4f))
            }
        }
    }

    // ---------------------------------------------------------------- La muralla del fondo y la puerta de la escalera

    /** La cara de dentro de la muralla del cuarto lado, con sus almenas, y la torre de la puerta de la escalera. */
    fun DrawScope.fondo(v: VistaOrden) {
        val lz = v.lz; val n = V3(0f, 0f, -1f)
        val cara = cara(lz, listOf(V3(CamOrden.BORDE, P, FONDO), V3(IZQ, P, FONDO), V3(IZQ, 0f, FONDO), V3(CamOrden.BORDE, 0f, FONDO)), n, C.PIEDRA, 0.22f)
        if (cara != null) clipPath(cara) { sillares(V3(IZQ, P, FONDO), V3(CamOrden.BORDE, P, FONDO), 8f, lz.color(C.PIEDRA, n, 0.22f), PiezasOrden.fijo("fondo"), 0.55f, 0.95f, lejos = 0.22f) }
        // El parapeto de arriba y las almenas, contra el cielo.
        caja(lz, (IZQ + CamOrden.BORDE) / 2f, FONDO + 0.5f, 0f, -IZQ + CamOrden.BORDE, 1f, 0f, CamOrden.ALTO_PARAPETO, C.PIEDRA, 0.22f)
        var s = CamOrden.BORDE - 1f
        while (s > IZQ) { caja(lz, s - 0.8f, FONDO + 0.5f, 0f, 1.6f, 1f, CamOrden.ALTO_PARAPETO, CamOrden.ALTO_ALMENA, C.PIEDRA, 0.22f); s -= 2.6f }
        puertaDeLaEscalera(v)
    }

    /**
     * La puerta de la escalera de la meseta: una torre baja en la muralla del fondo con su arco y su puerta de madera
     * herrada; abierta (la patrulla está fuera), cerrada, o atrancada con la barra de hierro todo el invierno.
     */
    private fun DrawScope.puertaDeLaEscalera(v: VistaOrden) {
        val lz = v.lz; val s = PUERTA_S
        caja(lz, s, FONDO - 1.2f, 0f, 6f, 3f, P, 3f, C.PIEDRA, 0.2f, arriba = C.LOSA) { i, _ ->
            if (i == 0) sillares(V3(s - 3f, P, FONDO - 2.7f), V3(s + 3f, P, FONDO - 2.7f), 11f, lz.color(C.PIEDRA, V3(0f, 0f, -1f), 0.2f), PiezasOrden.fijo("puerta"), 0.5f, 0.8f, lejos = 0.2f)
        }
        val vv = FONDO - 2.71f
        val arco = Path().apply {
            val a = CamOrden.p(s - 1.4f, P, vv); moveTo(a.x, a.y)
            val b = CamOrden.p(s - 1.4f, P + 3.2f, vv); lineTo(b.x, b.y)
            val c1 = CamOrden.p(s - 1.4f, P + 4.6f, vv); val c2 = CamOrden.p(s + 1.4f, P + 4.6f, vv); val d = CamOrden.p(s + 1.4f, P + 3.2f, vv)
            cubicTo(c1.x, c1.y, c2.x, c2.y, d.x, d.y)
            val e = CamOrden.p(s + 1.4f, P, vv); lineTo(e.x, e.y); close()
        }
        when (EscenaOrden.puerta(v.m)) {
            Puerta.ABIERTA -> {
                // Por la puerta abierta, el cielo de afuera y el arranque de la escalera que baja.
                drawPath(arco, lerp(v.l.bajo, v.l.tono(C.PARAMO, 0.4f), 0.4f))
                clipPath(arco) { drawRect(lz.color(C.ROCA, V3(0f, 1f, -0.4f), 0.2f), Offset(0f, CamOrden.p(s, P + 1.2f, vv).y), Size(400f, 40f)) }
                // La hoja abierta, contra el muro de dentro.
                val h = listOf(V3(s - 1.4f, P, vv), V3(s - 2.9f, P, vv - 0.9f), V3(s - 2.9f, P + 3.6f, vv - 0.9f), V3(s - 1.4f, P + 3.6f, vv)).map { CamOrden.p(it) }
                drawPath(poligono(h), lz.color(C.MADERA, V3(0.6f, 0f, -0.8f), 0.2f))
            }
            Puerta.CERRADA, Puerta.ATRANCADA -> {
                drawPath(arco, lz.color(C.MADERA_OSC, V3(0f, 0f, -1f), 0.2f))
                clipPath(arco) { for (i in 1..4) { val y = CamOrden.p(s, P + i * 0.9f, vv).y; drawLine(lz.color(C.HIERRO, V3(0f, 0f, -1f), 0.2f), Offset(0f, y), Offset(380f, y), 0.6f) } }
                if (EscenaOrden.puerta(v.m) == Puerta.ATRANCADA) {
                    // La barra de hierro, de lado a lado de la puerta, en sus dos argollas.
                    palo(lz, V3(s - 1.9f, P + 1.6f, vv - 0.1f), V3(s + 1.9f, P + 1.7f, vv - 0.1f), 0.18f, C.HIERRO, 0.2f)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Los edificios

    /** Los edificios de atrás adelante, con sus ventanas y lo que el cuento pone en ellos. */
    fun DrawScope.edificios(v: VistaOrden) {
        for (e in EDIFICIOS.sortedByDescending { CamOrden.hondo(it.s, it.v) }) {
            val lejos = (CamOrden.hondo(e.s, e.v) / 140f).coerceIn(0f, 0.35f)
            val az = PiezasOrden.fijo("edificio", e.s)
            caja(v.lz, e.s, e.v, e.giro, e.ancho, e.fondo, P, e.alto, C.PIEDRA, lejos, arriba = C.LOSA) { i, _ ->
                if (i < 4) {
                    val (a, b) = when (i) { 0 -> e.enCara(0, 0f, P) to e.enCara(0, 1f, P); 1 -> e.enCara(1, 0f, P) to e.enCara(1, 1f, P); 2 -> e.enCara(2, 0f, P) to e.enCara(2, 1f, P); else -> e.enCara(3, 0f, P) to e.enCara(3, 1f, P) }
                    sillares(a, b, e.alto - P, v.lz.color(C.PIEDRA, normalDe(e, i), lejos), az, 0.5f, 0.85f, lejos = lejos)
                }
            }
            when (e) {
                CAMARA -> ventanaDeLaCamara(v)
                ARCHIVO -> ventanaAlta(v, e, 0, 0.62f, P + 5.2f, 1.1f, 0.9f, EscenaOrden.ventanaDelArchivo(v.m))
                SALA -> { for (u in listOf(0.2f, 0.4f, 0.6f, 0.8f)) ventanaAlta(v, e, 0, u, P + 4.2f, 0.8f, 2f, EscenaOrden.salaDelCodigo(v.m)); espadana(v) }
                ENFERMERIA -> ventanaAlta(v, e, 3, 0.5f, P + 1.6f, 1.2f, 1.2f, EscenaOrden.enfermeria(v.m))
            }
        }
    }

    /** La normal de la cara [i] de un edificio. */
    fun normalDe(e: Edificio, i: Int): V3 {
        val ux = V3(cos(e.giro), 0f, -sin(e.giro)); val uz = V3(sin(e.giro), 0f, cos(e.giro))
        return when (i) { 0 -> uz * -1f; 1 -> uz; 2 -> ux * -1f; else -> ux }
    }

    /**
     * Una ventana en la cara [i] de un edificio, a lo largo [u] y con el alféizar a [h]: honda (los muros son gruesos), con
     * su derrame en sombra; de noche, encendida con [luz] (de 0 a 1), con su halo en el muro.
     */
    private fun DrawScope.ventanaAlta(v: VistaOrden, e: Edificio, i: Int, u: Float, h: Float, ancho: Float, alto: Float, luz: Float) {
        val n = normalDe(e, i); val c = e.enCara(i, u, h)
        if (!CamOrden.seVe(c, n)) return
        val largo = if (i < 2) e.ancho else e.fondo; val du = ancho / 2f / largo
        val q = listOf(e.enCara(i, u - du, h), e.enCara(i, u + du, h), e.enCara(i, u + du, h + alto), e.enCara(i, u - du, h + alto)).map { CamOrden.p(it) }
        val hueco = poligono(q)
        val postigo = EscenaOrden.postigos(v.m)
        drawPath(hueco, if (postigo) v.lz.color(C.HIERRO, n, 0.2f) else v.l.tono(lerp(C.HOLLIN, C.ROCA, 0.25f)))
        if (luz > 0.01f && !postigo) {
            val k = luz * (0.35f + 0.65f * v.l.noche)
            drawPath(hueco, C.LUMBRE.copy(alpha = (0.9f * k).coerceIn(0f, 1f)))
            val centro = (q[0] + q[2]) / 2f
            resplandor(centro, (q[1] - q[0]).getDistance() * 3.2f + 6f, 0.5f * k * v.l.noche, C.LUMBRE)
        } else if (luz > 0.01f && postigo && v.l.noche > 0.3f) {
            // Con el postigo cerrado, la luz se cuela por las rendijas.
            drawPath(hueco, C.LUMBRE.copy(alpha = 0.6f * luz), style = Stroke(0.5f))
        }
        // El derrame del muro, a un lado.
        drawLine(v.l.tono(C.HOLLIN).copy(alpha = 0.35f), q[0], q[3], ((q[1] - q[0]).getDistance() * 0.22f).coerceAtLeast(0.4f))
    }

    /**
     * La única ventana de la Cámara, en su cara de la derecha: honda, con la mesa detrás; el núcleo con su luz verde y la
     * pila de hojas sujetas con piedras ([EscenaOrden.pila]), y el candil de la tarde de taller.
     */
    private fun DrawScope.ventanaDeLaCamara(v: VistaOrden) {
        val e = CAMARA; val i = 3; val n = normalDe(e, i)
        if (!CamOrden.seVe(e.enCara(i, 0.5f, P + 3f), n)) return
        val h0 = P + 2.6f; val alto = 1.7f; val du = 0.95f / e.fondo
        val q = listOf(e.enCara(i, 0.5f - du, h0), e.enCara(i, 0.5f + du, h0), e.enCara(i, 0.5f + du, h0 + alto), e.enCara(i, 0.5f - du, h0 + alto)).map { CamOrden.p(it) }
        val hueco = poligono(q)
        val vc = EscenaOrden.ventanaDeLaCamara(v.m)
        val postigo = EscenaOrden.postigos(v.m)
        if (postigo) {
            drawPath(hueco, v.lz.color(C.HIERRO, n, 0.1f))
            if (vc.verde > 0f) drawPath(hueco, C.NUCLEO.copy(alpha = 0.7f), style = Stroke(0.6f))
            return
        }
        drawPath(hueco, v.l.tono(lerp(C.HOLLIN, C.MADERA, 0.25f)))
        clipPath(hueco) {
            val abajo = (q[0] + q[1]) / 2f; val w = (q[1] - q[0]).getDistance(); val alt = (q[3] - q[0]).getDistance()
            // La mesa, a la altura del alféizar.
            drawRect(v.l.tono(C.MADERA), Offset(abajo.x - w, abajo.y - alt * 0.32f), Size(w * 2f, alt * 0.12f))
            // El núcleo: una caja negra con su luz verde.
            if (EscenaOrden.nucleoEnLaCamara(v.m)) {
                val nc = Offset(abajo.x - w * 0.18f, abajo.y - alt * 0.42f)
                drawRect(v.l.tono(C.HOLLIN), Offset(nc.x - w * 0.16f, nc.y - alt * 0.1f), Size(w * 0.32f, alt * 0.12f))
                if (vc.verde > 0f) { drawCircle(C.NUCLEO, (w * 0.05f).coerceAtLeast(0.6f), nc + Offset(w * 0.08f, -alt * 0.04f)); resplandor(nc, w * 0.9f, 0.35f + 0.5f * v.l.noche, C.NUCLEO) }
                // La pila de hojas, cada vez más alta.
                val pisos = when (EscenaOrden.pila(v.m)) { EscenaOrden.Pila.NINGUNA -> 0; EscenaOrden.Pila.POCAS -> 2; EscenaOrden.Pila.MEDIA -> 4; EscenaOrden.Pila.ALTA -> 7 }
                for (k in 0 until pisos) drawRect(v.l.tono(if (k % 2 == 0) C.PAPEL else lerp(C.PAPEL, C.ARENA, 0.3f)), Offset(abajo.x + w * 0.08f, abajo.y - alt * (0.33f + 0.055f * (k + 1))), Size(w * 0.3f, alt * 0.05f))
                if (pisos > 0) drawCircle(v.l.tono(C.ROCA), (w * 0.05f).coerceAtLeast(0.5f), Offset(abajo.x + w * 0.23f, abajo.y - alt * (0.36f + 0.055f * pisos)))
            }
            if (vc.candil > 0f) resplandor(abajo + Offset(w * 0.3f, -alt * 0.5f), w * 1.2f, 0.4f + 0.5f * v.l.noche, C.LUMBRE)
        }
        drawPath(hueco, v.lz.color(C.PIEDRA_OSC, n, 0.1f), style = Stroke(0.7f))
    }

    /** La espadaña de la campana, sobre la sala del Código. */
    private fun DrawScope.espadana(v: VistaOrden) {
        val e = SALA; val base = e.enCara(0, 0.82f, e.alto)
        val lz = v.lz
        caja(lz, base.x, base.z + 0.4f, e.giro, 2.4f, 0.7f, e.alto, e.alto + 3.2f, C.PIEDRA, 0.25f)
        val campana = V3(base.x, e.alto + 1.6f, base.z - 0.1f)
        val q = CamOrden.p(campana); val k = CamOrden.k(campana.x, campana.z)
        drawPath(poligono(q + Offset(-0.45f * k, 0.5f * k), q + Offset(0.45f * k, 0.5f * k), q + Offset(0.25f * k, -0.35f * k), q + Offset(-0.25f * k, -0.35f * k)), lz.color(C.LATON, V3(0.3f, 0f, -1f), 0.2f))
    }

    // ---------------------------------------------------------------- La cisterna

    /**
     * La boca de la cisterna: un brocal bajo de piedra alrededor del hueco y, dentro, la escalera que baja pegada a la
     * pared del fondo, con el agua donde diga [EscenaOrden.peldano] (y el verde del fondo cuando está baja). La cisterna
     * es un aljibe cubierto **[Diseño propio]**: se baja «a revisar las rejillas con una lámpara».
     */
    fun DrawScope.cisterna(v: VistaOrden) {
        val lz = v.lz; val s0 = CISTERNA.x - CISTERNA_ANCHO / 2f; val s1 = CISTERNA.x + CISTERNA_ANCHO / 2f
        val v0 = CISTERNA.y - CISTERNA_LARGO / 2f; val v1 = CISTERNA.y + CISTERNA_LARGO / 2f
        val boca = poligono(listOf(V3(s0, P, v0), V3(s1, P, v0), V3(s1, P, v1), V3(s0, P, v1)).map { CamOrden.p(it) })
        drawPath(boca, v.l.tono(lerp(C.HOLLIN, C.ROCA_HONDA, 0.3f)))
        clipPath(boca) {
            // La pared del fondo del pozo (la de v1), que se ve hacia abajo, con la escalera pegada.
            val hondo = 7f; val nivel = P - hondo * EscenaOrden.peldano(v.m) / EscenaOrden.PELDANOS.toFloat()
            val pared = poligono(listOf(V3(s0, P, v1), V3(s1, P, v1), V3(s1, P - hondo, v1), V3(s0, P - hondo, v1)).map { CamOrden.p(it) })
            drawPath(pared, lz.color(C.PIEDRA_OSC, V3(0f, 0f, -1f), 0.15f).let { lerp(it, v.l.tono(C.HOLLIN), 0.45f) })
            for (i in 0 until EscenaOrden.PELDANOS) {
                val h = P - (i + 1) * hondo / EscenaOrden.PELDANOS; val sa = s0 + 0.15f + (i % 12) * 0.18f
                val a = CamOrden.p(sa, h, v1 - 0.05f); val b = CamOrden.p(sa + 0.5f, h, v1 - 0.05f)
                drawLine(lz.color(C.PIEDRA, V3(0f, 1f, 0f), 0.15f).let { lerp(it, v.l.tono(C.HOLLIN), 0.25f) }, a, b, 0.9f)
            }
            // El agua: un plano oscuro que refleja la boca; con la cisterna baja, verde y con el fondo a la vista.
            val agua = poligono(listOf(V3(s0, nivel, v0), V3(s1, nivel, v0), V3(s1, nivel, v1), V3(s0, nivel, v1)).map { CamOrden.p(it) })
            val tramo = EscenaOrden.tramoDeLaCisterna(v.m)
            val color = when (tramo) { EscenaOrden.Cisterna.BAJA -> lerp(C.NUCLEO, C.HOLLIN, 0.62f); EscenaOrden.Cisterna.MEDIA -> lerp(C.VIDRIO, C.HOLLIN, 0.6f); EscenaOrden.Cisterna.LLENA -> lerp(C.VIDRIO, C.HOLLIN, 0.45f) }
            drawPath(agua, v.l.tono(color))
            val ra = CamOrden.p(CISTERNA.x, nivel, v0 + 0.4f)
            mancha(ra, 6f, 1.2f, lerp(v.l.bajo, Color.White, 0.2f), 0.18f * (1f - v.l.noche))
        }
        // El brocal: un murete bajo en tres lados (el cuarto, hacia la muralla, es la entrada de la escalera).
        val alto = 0.55f; val g = 0.35f
        caja(lz, s0 - g / 2f, CISTERNA.y, 0f, g, CISTERNA_LARGO + g * 2f, P, P + alto, C.PIEDRA, 0.1f)
        caja(lz, CISTERNA.x, v0 - g / 2f, 0f, CISTERNA_ANCHO, g, P, P + alto, C.PIEDRA, 0.1f)
        caja(lz, CISTERNA.x, v1 + g / 2f, 0f, CISTERNA_ANCHO, g, P, P + alto, C.PIEDRA, 0.1f)
    }

    // ---------------------------------------------------------------- La fragua

    /**
     * La fragua: un cobertizo de piedra con el tejadillo de losas apoyado en la muralla, abierto hacia el patio y hacia
     * quien mira, con la lumbre al fondo (que de noche se queda en rescoldo), el yunque, y en su costado las placas de
     * caparazón colgadas por piezas; la chimenea sube pegada a la muralla.
     */
    fun DrawScope.fragua(v: VistaOrden) {
        val lz = v.lz; val s0 = CamOrden.BORDE; val s1 = FRAGUA_S; val v0 = FRAGUA_V0; val v1 = FRAGUA_V1
        val alto0 = P + 4.2f; val alto1 = P + 3f
        // El costado del fondo (v1) y el suelo, en sombra.
        cara(lz, listOf(V3(s1, P, v1), V3(s0, P, v1), V3(s0, alto0, v1), V3(s1, alto1, v1)), V3(0f, 0f, -1f), lerp(C.PIEDRA, C.HOLLIN, 0.35f), 0.12f)
        val dentro = poligono(listOf(V3(s1, P, v0), V3(s0, P, v0), V3(s0, alto0, v0), V3(s1, alto1, v0)).map { CamOrden.p(it) })
        val fuego = EscenaOrden.fragua(v.m)
        clipPath(dentro) {
            drawPath(dentro, v.l.tono(lerp(C.HOLLIN, C.ROCA, 0.25f)))
            // El hogar al fondo y su lumbre.
            val hogar = CamOrden.p(s0 - 1.2f, P + 1f, v1 - 1f); val k = CamOrden.k(s0 - 1.2f, v1 - 1f)
            drawRect(lz.color(C.PIEDRA_OSC, V3(0f, 0f, -1f), 0.12f), Offset(hogar.x - k * 1f, hogar.y - k * 0.2f), Size(k * 2f, k * 1.2f))
            drawOval(lerp(C.FRAGUA, C.LUMBRE, 0.3f).copy(alpha = 0.35f + 0.65f * fuego), Offset(hogar.x - k * 0.7f, hogar.y - k * 0.35f), Size(k * 1.4f, k * 0.4f))
            resplandor(hogar, k * 5f, (0.25f + 0.75f * v.l.noche) * fuego, C.FRAGUA)
            // El yunque, delante.
            val y = CamOrden.p(s1 + 1.6f, P + 0.8f, v0 + 2f)
            drawRect(lz.color(C.HIERRO, V3(0f, 1f, 0f), 0.1f), Offset(y.x - k * 0.5f, y.y - k * 0.15f), Size(k * 1f, k * 0.3f))
        }
        // El poste de la esquina y el tejadillo de losas, que baja hacia el patio.
        palo(lz, V3(s1, P, v0), V3(s1, alto1, v0), 0.35f, C.MADERA, 0.1f)
        val tejado = listOf(V3(s1 - 0.4f, alto1 - 0.1f, v0 - 0.5f), V3(s0, alto0 + 0.1f, v0 - 0.5f), V3(s0, alto0 + 0.1f, v1 + 0.3f), V3(s1 - 0.4f, alto1 - 0.1f, v1 + 0.3f))
        cara(lz, tejado, V3(-0.4f, 1f, 0f), C.LOSA, 0.1f, 0.15f)?.let { t -> clipPath(t) { for (j in 0..10) drawLine(v.l.tono(C.HOLLIN).copy(alpha = 0.25f), CamOrden.p(s1 - 0.4f, alto1, v0 - 0.5f + j * 0.75f), CamOrden.p(s0, alto0, v0 - 0.5f + j * 0.75f), 0.5f) } }
        // La chimenea, pegada a la muralla, hasta por encima del adarve.
        caja(lz, s0 - 0.55f, v1 - 1.2f, 0f, 1.1f, 1.1f, alto0, 1.4f, lerp(C.PIEDRA, C.HOLLIN, 0.15f), 0.1f)
        placas(v)
    }

    /** Las esquinas de la fragua (para las sombras). */
    fun cuerpoDeLaFragua(): List<V3> = listOf(V3(CamOrden.BORDE, P, FRAGUA_V0), V3(FRAGUA_S, P, FRAGUA_V0), V3(CamOrden.BORDE, P, FRAGUA_V1), V3(FRAGUA_S, P, FRAGUA_V1),
        V3(CamOrden.BORDE, P + 4.2f, FRAGUA_V0), V3(FRAGUA_S, P + 3f, FRAGUA_V0), V3(CamOrden.BORDE, P + 4.2f, FRAGUA_V1), V3(FRAGUA_S, P + 3f, FRAGUA_V1))

    /** Dónde sale el humo de la fragua (la boca de la chimenea). */
    val CHIMENEA: V3 get() = V3(CamOrden.BORDE - 0.55f, 1.5f, FRAGUA_V1 - 1.2f)

    /**
     * Las placas de caparazón colgadas en el costado de la fragua, por piezas: hombreras, petos y grebas de hierro
     * abollado, cada una de su gancho; nunca una figura entera. Desde la muerte de Ulises… desde que Ulises lo cuelga, el
     * suyo, con más remiendos y una placa de latón.
     */
    private fun DrawScope.placas(v: VistaOrden) {
        val lz = v.lz; val v0 = FRAGUA_V0 - 0.02f; val n = V3(0f, 0f, -1f)
        val az = PiezasOrden.fijo("placas")
        val piezas = if (EscenaOrden.caparazonDeUlises(v.m)) 6 else 4
        for (j in 0 until piezas) {
            val s = FRAGUA_S - 0.5f - j * 0.75f; val h = P + 2.3f - (j % 2) * 0.9f
            if (s < FRAGUA_S - 4.2f) break
            val a = CamOrden.p(s, h, v0); val k = CamOrden.k(s, v0)
            val w = k * az.entre(0.45f, 0.6f); val alto = k * az.entre(0.5f, 0.75f)
            val forma = Path().apply { moveTo(a.x - w / 2f, a.y); lineTo(a.x + w / 2f, a.y); cubicTo(a.x + w / 2f, a.y + alto * 0.7f, a.x + w * 0.2f, a.y + alto, a.x, a.y + alto); cubicTo(a.x - w * 0.2f, a.y + alto, a.x - w / 2f, a.y + alto * 0.7f, a.x - w / 2f, a.y); close() }
            val color = if (j >= 4) lerp(C.HIERRO, C.LATON, 0.25f) else C.HIERRO
            drawPath(forma, Brush.horizontalGradient(listOf(lz.color(color, V3(1f, 0f, -1f), 0.1f), lz.color(color, V3(-1f, 0f, -1f), 0.1f)), a.x - w / 2f, a.x + w / 2f))
            drawLine(lz.color(C.HIERRO_CLARO, V3(0f, 1f, -1f), 0.1f), Offset(a.x - w * 0.3f, a.y + alto * 0.3f), Offset(a.x + w * 0.3f, a.y + alto * 0.32f), 0.5f)
            drawLine(v.l.tono(C.MADERA_OSC), a, a + Offset(0f, -k * 0.2f), 0.5f)
        }
    }

    // ---------------------------------------------------------------- El corral

    /** El corral de las cabras, abajo a la izquierda: un murete de piedra seca, el abrevadero y las cabras apretujadas. */
    fun DrawScope.corral(v: VistaOrden) {
        val lz = v.lz; val s0 = -6.5f; val s1 = -12f; val v0 = 15.5f; val v1 = 21f
        val az = PiezasOrden.fijo("cabras")
        // Las cabras, de atrás adelante.
        val noche = v.m.hora >= 20.0 || v.m.hora < 6.0
        val cuantas = if (noche) 9 else 6
        val sitios = (0 until cuantas).map { V3(az.entre(s1 + 0.6f, s0 - 0.6f), P, az.entre(v0 + 0.6f, v1 - 0.8f)) }.sortedByDescending { CamOrden.hondo(it) }
        for ((j, q) in sitios.withIndex()) cabra(v, q, az.entre(-1f, 1f) > 0f, j % 3 == 0)
        // El murete: tres lados y el abrevadero contra el de delante.
        for ((a, b) in listOf(V3(s1, P, v0) to V3(s1, P, v1), V3(s1, P, v1) to V3(s0, P, v1), V3(s0, P, v0) to V3(s1, P, v0))) {
            val ab = V3((a.x + b.x) / 2f, P, (a.z + b.z) / 2f)
            val largo = maxOf(kotlin.math.abs(b.x - a.x), kotlin.math.abs(b.z - a.z))
            val horizontal = kotlin.math.abs(b.x - a.x) > kotlin.math.abs(b.z - a.z)
            caja(lz, ab.x, ab.z, 0f, if (horizontal) largo else 0.5f, if (horizontal) 0.5f else largo, P, P + 1f, lerp(C.PIEDRA, C.ARENA, 0.2f), 0.08f)
        }
        caja(lz, (s0 + s1) / 2f, v0 + 0.6f, 0f, 2.4f, 0.7f, P, P + 0.55f, C.PIEDRA_OSC, 0.08f, arriba = lerp(C.VIDRIO, C.HOLLIN, 0.5f))
    }

    /** Una cabra de [pie], vista de lado (clara u oscura), con su sombra corta. Pequeña pero entera: lomo, patas, cuernos. */
    private fun DrawScope.cabra(v: VistaOrden, pie: V3, derecha: Boolean, oscura: Boolean) {
        val q = CamOrden.p(pie); val k = CamOrden.k(pie.x, pie.z); val d = if (derecha) 1f else -1f
        val pelo = if (oscura) C.CABRA_OSC else C.CABRA
        val cuerpo = Path().apply {
            moveTo(q.x - 0.55f * k * d, q.y - 0.55f * k)
            cubicTo(q.x - 0.3f * k * d, q.y - 0.78f * k, q.x + 0.3f * k * d, q.y - 0.78f * k, q.x + 0.5f * k * d, q.y - 0.62f * k)
            lineTo(q.x + 0.5f * k * d, q.y - 0.4f * k)
            cubicTo(q.x + 0.2f * k * d, q.y - 0.32f * k, q.x - 0.3f * k * d, q.y - 0.32f * k, q.x - 0.55f * k * d, q.y - 0.42f * k)
            close()
        }
        val lado = if (derecha == v.p.solALaDerecha) 0.5f else -0.5f
        for (px in listOf(-0.42f, -0.3f, 0.3f, 0.42f)) drawLine(v.lz.color(lerp(pelo, C.HOLLIN, 0.3f), V3(0f, 0f, -1f), 0.1f), Offset(q.x + px * k * d, q.y - 0.4f * k), Offset(q.x + px * k * d, q.y), (0.07f * k).coerceAtLeast(0.4f))
        drawPath(cuerpo, v.lz.color(pelo, V3(lado, 0.6f, -0.5f), 0.1f))
        // La cabeza y los cuernos.
        val c = Offset(q.x + 0.62f * k * d, q.y - 0.74f * k)
        drawOval(v.lz.color(pelo, V3(lado, 0.4f, -0.6f), 0.1f), Offset(c.x - 0.14f * k, c.y - 0.1f * k), Size(0.28f * k, 0.2f * k))
        drawLine(v.l.tono(C.MADERA_OSC), c + Offset(-0.05f * k * d, -0.08f * k), c + Offset(-0.22f * k * d, -0.3f * k), (0.05f * k).coerceAtLeast(0.35f), StrokeCap.Round)
    }

    /** Un azar del día para lo del patio que cambia. */
    fun azar(v: VistaOrden, parte: String): Azar = v.m.azar("patio", parte)

    /** El resplandor de la lumbre de la fragua en el patio, de noche: por la boca del cobertizo, sobre el suelo. */
    fun DrawScope.luzDeLaFragua(v: VistaOrden) {
        val f = EscenaOrden.fragua(v.m); if (f <= 0.05f) return
        val c = CamOrden.p(FRAGUA_S + 1f, P, FRAGUA_V0 - 1f); val k = CamOrden.k(FRAGUA_S, FRAGUA_V0)
        drawOval(Brush.radialGradient(listOf(C.FRAGUA.copy(alpha = (0.4f * f * v.l.noche).coerceIn(0f, 1f)), Color.Transparent), c, k * 4f), Offset(c.x - k * 4f, c.y - k * 1.6f), Size(k * 8f, k * 3.2f), blendMode = BlendMode.Plus)
    }
}
