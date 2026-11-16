package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Clima

/**
 * Lo que los días señalados dejan en el patio y en las murallas **[Diseño propio]** a partir del cuento. Son días de gente,
 * y sin personas se cuentan por lo que dejan:
 * - **La Fundación**: los caparazones recién pintados, en fila sobre sus armazones de madera, abiertos y vacíos, por piezas
 *   (el peto, las hombreras, la mochila del motor colgada aparte), cada uno con su dibujo: un candil, un lobo, una mano.
 * - **El mercado de las caravanas**: los toldos, los fardos de sal y de tela, las mulas atadas.
 * - **Año nuevo**: «con los candiles encendidos aunque sea de día»: en el patio, en corro, donde se lee el Código.
 * - **La caza grande**: las cuerdas con la carne salada al sol.
 * - **Los canales**: las tapas levantadas (en el patio) y las escobas de ramas apoyadas.
 * - **El inventario**: las cajas de la Cámara, fuera.
 * - **La alarma**: de noche, antorchas en el adarve; el día del pozo, las de los saqueadores, abajo, en el páramo.
 * - **La Vigilia**: las lámparas viejas encendidas en las almenas, cada una distinta.
 * - **La sequía**: la tierra del patio cuarteada.
 * - **El granizo**: los granos blancos en el adarve.
 */
internal object DiasOrden {
    private val C = ColorOrden
    private const val P = CamOrden.PATIO

    /** Lo del patio, después de los edificios del fondo y antes de lo de cerca. */
    fun DrawScope.enElPatio(v: VistaOrden) {
        val m = v.m
        if (EscenaOrden.sequia(m)) grietas(v)
        if (EscenaOrden.caparazonesPintados(m)) caparazones(v)
        if (EscenaOrden.mercado(m)) mercado(v)
        if (EscenaOrden.caza(m)) carneAlSol(v)
        if (EscenaOrden.hoyEs(m, "anio_nuevo")) corroDeCandiles(v)
        if (EscenaOrden.inventario(m)) cajasDeLaCamara(v)
        if (EscenaOrden.tapasLevantadas(m)) escobas(v)
    }

    /** Lo de las murallas, después del adarve. */
    fun DrawScope.enLasMurallas(v: VistaOrden) {
        val m = v.m
        if (EscenaOrden.alarma(m) && v.l.noche > 0.3f) antorchas(v)
        if (EscenaOrden.vigilia(m)) lamparasViejas(v)
        if (EscenaOrden.granizo(m)) granizo(v)
    }

    /** En el páramo: las antorchas de los saqueadores que atacan el pozo («Desde la muralla se veían sus antorchas»). */
    fun DrawScope.enElParamo(v: VistaOrden) {
        if (!EscenaOrden.hoyEs(v.m, "pozo") || v.l.noche < 0.3f) return
        val az = v.m.azar("saqueadores")
        repeat(5) { val q = Offset(296f + az.entre(-12f, 18f), 192f + az.entre(-3f, 2f)); drawCircle(C.LUMBRE, 0.8f, q); resplandor(q, 6f, 0.6f, C.FRAGUA) }
    }

    // ---------------------------------------------------------------- La Fundación

    /** Los caparazones pintados, en fila delante de la sala del Código: cada uno, piezas colgadas de su armazón, sin nadie dentro. */
    private fun DrawScope.caparazones(v: VistaOrden) {
        val lz = v.lz; val dibujos = listOf(C.CARMESI, C.LATON, C.PAPEL, C.CARMESI, C.LATON)
        for (i in 4 downTo 0) {
            val s = -18.5f - i * 2.3f; val vv = 23f + i * 1.1f
            // El armazón: un poste y su travesaño.
            palo(lz, V3(s, P, vv), V3(s, P + 1.9f, vv), 0.1f, C.MADERA, 0.2f)
            palo(lz, V3(s - 0.5f, P + 1.75f, vv), V3(s + 0.5f, P + 1.75f, vv), 0.08f, C.MADERA, 0.2f)
            // El peto colgado (abierto: se ve el hueco de dentro), las dos hombreras sueltas y la mochila del motor, en el suelo.
            val peto = listOf(V3(s - 0.32f, P + 1.65f, vv - 0.05f), V3(s + 0.32f, P + 1.65f, vv - 0.05f), V3(s + 0.26f, P + 0.95f, vv - 0.05f), V3(s - 0.26f, P + 0.95f, vv - 0.05f))
            val forma = poligono(peto.map { CamOrden.p(it) })
            drawPath(forma, lz.color(C.HIERRO, V3(0f, 0f, -1f), 0.2f))
            // La pintura del día: el peto entero de un color, con una banda cruzada del otro.
            val pintado = listOf(V3(s - 0.26f, P + 1.58f, vv - 0.06f), V3(s + 0.26f, P + 1.58f, vv - 0.06f), V3(s + 0.21f, P + 1.02f, vv - 0.06f), V3(s - 0.21f, P + 1.02f, vv - 0.06f))
            drawPath(poligono(pintado.map { CamOrden.p(it) }), lz.color(dibujos[i], V3(0f, 0f, -1f), 0.2f, 0.2f))
            val banda = listOf(V3(s - 0.26f, P + 1.45f, vv - 0.07f), V3(s - 0.26f, P + 1.3f, vv - 0.07f), V3(s + 0.21f, P + 1.05f, vv - 0.07f), V3(s + 0.21f, P + 1.2f, vv - 0.07f))
            drawPath(poligono(banda.map { CamOrden.p(it) }), lz.color(dibujos[(i + 1) % 5], V3(0f, 0f, -1f), 0.2f, 0.2f))
            val k = CamOrden.k(s, vv)
            for (lado in listOf(-1f, 1f)) { val h = CamOrden.p(V3(s + lado * 0.5f, P + 1.6f, vv - 0.05f)); drawOval(lz.color(C.HIERRO_CLARO, V3(lado, 0.3f, -1f), 0.2f), Offset(h.x - k * 0.16f, h.y), Size(k * 0.32f, k * 0.28f)) }
            caja(lz, s + 0.7f, vv - 0.2f, 0.3f, 0.5f, 0.4f, P, P + 0.55f, C.HIERRO, 0.2f)
        }
    }

    // ---------------------------------------------------------------- El mercado

    /** El mercado: tres toldos de tela en sus palos, los fardos debajo y las mulas atadas junto a la puerta. */
    private fun DrawScope.mercado(v: VistaOrden) {
        val lz = v.lz; val telas = listOf(lerp(C.ARENA, C.CARMESI, 0.35f), lerp(C.PAPEL, C.ARENA, 0.3f), lerp(C.VIDRIO, C.HIERRO, 0.3f))
        val az = v.m.azar("mercado")
        for (i in 2 downTo 0) {
            val s = -21f - i * 4.6f; val vv = 28f + i * 2.4f
            for ((ds, dv) in listOf(-1.4f to -1f, 1.4f to -1f, -1.4f to 1f, 1.4f to 1f)) palo(lz, V3(s + ds, P, vv + dv), V3(s + ds, P + 2.1f, vv + dv), 0.08f, C.MADERA, 0.2f)
            val toldo = listOf(V3(s - 1.6f, P + 2.2f, vv - 1.1f), V3(s + 1.6f, P + 2.2f, vv - 1.1f), V3(s + 1.6f, P + 2.4f, vv + 1.1f), V3(s - 1.6f, P + 2.4f, vv + 1.1f))
            drawPath(poligono(toldo.map { CamOrden.p(it) }), lz.color(telas[i], V3(0f, 1f, 0f), 0.2f))
            drawPath(poligono(listOf(toldo[0], toldo[1], V3(s + 1.6f, P + 1.8f, vv - 1.15f), V3(s - 1.6f, P + 1.8f, vv - 1.15f)).map { CamOrden.p(it) }), lz.color(telas[i], V3(0f, 0f, -1f), 0.2f))
            repeat(3) { caja(lz, s + az.entre(-1f, 1f), vv + az.entre(-0.6f, 0.6f), az.entre(-0.5f, 0.5f), 0.6f, 0.5f, P, P + 0.45f, lerp(C.ARENA, C.MADERA, az.real()), 0.2f) }
        }
        // Las mulas, atadas junto a la puerta de la escalera.
        for (i in 0 until 3) {
            val pie = V3(PatioOrden.PUERTA_S + 2f + i * 1.3f, P, PatioOrden.FONDO - 4f); val q = CamOrden.p(pie); val k = CamOrden.k(pie.x, pie.z)
            val cuerpo = poligono(q + Offset(-k * 0.8f, -k * 1.3f), q + Offset(k * 0.7f, -k * 1.35f), q + Offset(k * 0.75f, -k * 0.8f), q + Offset(-k * 0.8f, -k * 0.75f))
            for (px in listOf(-0.65f, -0.45f, 0.5f, 0.65f)) drawLine(v.l.tono(C.CABRA_OSC, 0.2f), q + Offset(px * k, -k * 0.8f), q + Offset(px * k, 0f), (k * 0.1f).coerceAtLeast(0.4f))
            drawPath(cuerpo, lz.color(lerp(C.CABRA_OSC, C.MADERA, 0.4f), V3(0.3f, 0.5f, -1f), 0.2f))
            drawLine(lz.color(C.CABRA_OSC, V3(0f, 0f, -1f), 0.2f), q + Offset(k * 0.7f, -k * 1.2f), q + Offset(k * 1.1f, -k * 1.7f), (k * 0.28f).coerceAtLeast(0.6f), StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- La caza, año nuevo, el inventario, los canales

    /** La carne salada de la caza, colgada en tiras de dos cuerdas tendidas en el patio. */
    private fun DrawScope.carneAlSol(v: VistaOrden) {
        val lz = v.lz; val cuerda = v.l.tono(C.MADERA_OSC, 0.2f)
        for ((a, b) in listOf(V3(-20f, P + 2.4f, 25f) to V3(-36f, P + 2.4f, 33f), V3(-22f, P + 2.2f, 31f) to V3(-34f, P + 2.2f, 38f))) {
            palo(lz, V3(a.x, P, a.z), a, 0.1f, C.MADERA, 0.2f); palo(lz, V3(b.x, P, b.z), b, 0.1f, C.MADERA, 0.2f)
            cabo(a, b, 0.25f, cuerda, 0.5f)
            for (i in 1 until 12) {
                val t = i / 12f; val q = V3(a.x + (b.x - a.x) * t, a.y - 1f * t * (1f - t) - 0.02f, a.z + (b.z - a.z) * t)
                val p0 = CamOrden.p(q); val k = CamOrden.k(q.x, q.z)
                drawLine(lz.color(lerp(C.CARMESI, C.MADERA, 0.4f), V3(0f, 0f, -1f), 0.2f), p0, p0 + Offset(0f, k * 0.6f), (k * 0.18f).coerceAtLeast(0.6f), StrokeCap.Round)
            }
        }
    }

    /** Año nuevo: los candiles en corro sobre las losas del patio, encendidos de día, donde se ha leído el Código. */
    private fun DrawScope.corroDeCandiles(v: VistaOrden) {
        val c = V3(-28f, P, 32f)
        for (i in 0 until 22) {
            val a = i * 6.2832f / 22f; val q = CamOrden.p(c.x + 4f * kotlin.math.cos(a), P + 0.1f, c.z + 3f * kotlin.math.sin(a)); val k = CamOrden.k(c.x, c.z)
            drawRect(v.lz.color(C.LATON, V3(0f, 0f, -1f), 0.2f), Offset(q.x - k * 0.08f, q.y - k * 0.12f), Size(k * 0.16f, k * 0.12f))
            drawCircle(C.LUMBRE, (k * 0.07f).coerceAtLeast(0.5f), q + Offset(0f, -k * 0.2f)); resplandor(q + Offset(0f, -k * 0.2f), k * 0.6f, 0.5f, C.LUMBRE)
        }
    }

    /** El inventario: las cajas de madera sacadas de la Cámara, junto a su puerta, en dos montones. */
    private fun DrawScope.cajasDeLaCamara(v: VistaOrden) {
        val e = PatioOrden.CAMARA; val az = v.m.azar("inventario")
        for (i in 0 until 6) { val q = e.enCara(3, 0.2f + 0.12f * i, P); caja(v.lz, q.x + 1.2f + az.entre(0f, 0.5f), q.z + az.entre(-0.3f, 0.3f), az.entre(-0.3f, 0.3f), 0.8f, 0.6f, P + (if (i % 3 == 2) 0.5f else 0f), P + (if (i % 3 == 2) 1f else 0.5f), lerp(C.MADERA, C.ARENA, 0.25f), 0.12f) }
    }

    /** Las escobas de ramas, apoyadas en el muro de la Cámara, el día que se barren los canales. */
    private fun DrawScope.escobas(v: VistaOrden) {
        val e = PatioOrden.CAMARA
        for (i in 0 until 3) {
            val pie = e.enCara(3, 0.65f + i * 0.08f, P); val apoyo = V3(pie.x + 0.05f, P + 1.6f, pie.z + 0.25f)
            val a = V3(pie.x + 0.9f, P, pie.z)
            palo(v.lz, a, apoyo, 0.06f, C.MADERA, 0.12f)
            val q = CamOrden.p(a); val k = CamOrden.k(a.x, a.z)
            drawPath(poligono(q + Offset(-k * 0.2f, 0f), q + Offset(k * 0.2f, 0f), q + Offset(k * 0.08f, -k * 0.45f), q + Offset(-k * 0.08f, -k * 0.45f)), v.lz.color(lerp(C.ARENA, C.LIQUEN, 0.4f), V3(0f, 0f, -1f), 0.12f))
        }
    }

    /** La sequía: la tierra del patio cuarteada en placas. */
    private fun DrawScope.grietas(v: VistaOrden) {
        val az = v.m.azar("grietas"); val c = v.l.tono(lerp(C.LOSA, C.HOLLIN, 0.4f), 0.1f).copy(alpha = 0.5f)
        repeat(60) {
            val s = az.entre(-30f, -1f); val vv = az.entre(15f, 58f); val k = CamOrden.k(s, vv)
            val a = CamOrden.p(s, P, vv); drawLine(c, a, a + Offset(az.entre(-1f, 1f) * k * 0.9f, az.entre(-0.3f, 0.3f) * k * 0.3f), 0.5f)
        }
    }

    // ---------------------------------------------------------------- En las murallas

    /** La alarma, de noche: antorchas clavadas en el adarve, junto a las almenas. */
    private fun DrawScope.antorchas(v: VistaOrden) {
        for (i in listOf(1, 4, 8)) {
            val pie = V3(CamOrden.PARAPETO - 0.2f, 0f, AdarveOrden.almena(i) + AdarveOrden.ALMENA + 0.3f)
            palo(v.lz, pie, V3(pie.x, 1.9f, pie.z), 0.06f, C.MADERA_OSC)
            val q = CamOrden.p(pie.x, 1.95f, pie.z); val k = CamOrden.k(pie.x, pie.z)
            resplandor(q, k * 1.8f, 0.7f, C.FRAGUA)
        }
    }

    /** Dónde arde la antorcha [i] de la alarma (para su llama, en la parte viva). */
    fun antorcha(i: Int): V3 = V3(CamOrden.PARAPETO - 0.2f, 1.95f, AdarveOrden.almena(listOf(1, 4, 8)[i]) + AdarveOrden.ALMENA + 0.3f)

    /** La Vigilia: en lo alto de cada almena, una lámpara vieja distinta (de aceite, de cristal, de bronce), todas encendidas. */
    private fun DrawScope.lamparasViejas(v: VistaOrden) {
        val az = io.github.ruddy2003.vestigium.vidas.Azar(io.github.ruddy2003.vestigium.vidas.Semilla.de("orden", "vigilia"))
        for (i in 0 until EscenaOrden.ALMENAS) {
            val c = V3((CamOrden.PARAPETO + CamOrden.FUERA) / 2f, CamOrden.ALTO_ALMENA, AdarveOrden.almena(i) + AdarveOrden.ALMENA / 2f)
            val q = CamOrden.p(c); val k = CamOrden.k(c.x, c.z)
            val forma = az.entero(3); val alto = k * az.entre(0.2f, 0.35f); val ancho = k * az.entre(0.12f, 0.22f)
            val color = v.lz.color(if (forma == 1) C.VIDRIO else if (forma == 2) lerp(C.LATON, C.ROCA, 0.4f) else C.LATON, V3(0f, 0f, -1f))
            if (forma == 0) drawOval(color, Offset(q.x - ancho, q.y - alto * 0.5f), Size(ancho * 2f, alto * 0.5f))
            else drawRect(color, Offset(q.x - ancho * 0.6f, q.y - alto), Size(ancho * 1.2f, alto))
            // El halo, según lo cerca que esté: las del fondo, solo un punto de luz (juntas harían un foco).
            val luz = if (forma == 1) Color(0xFFFFE6A0) else C.LUMBRE
            if (k < 3f) drawCircle(luz.copy(alpha = 0.8f), 0.6f, q + Offset(0f, -alto * 0.8f))
            else resplandor(q + Offset(0f, -alto * 0.8f), k * 0.8f + 1f, 0.25f + 0.55f * (k / 12f).coerceIn(0f, 1f), luz)
        }
    }

    /** El granizo: los granos blancos en las losas del adarve y en lo alto del parapeto. */
    private fun DrawScope.granizo(v: VistaOrden) {
        val az = v.m.azar("granizo"); val blanco = v.l.tono(C.NIEVE)
        repeat(160) {
            val s = az.entre(CamOrden.BORDE, CamOrden.PARAPETO); val vv = 0.8f + 18f * az.real() * az.real(); val k = CamOrden.k(s, vv)
            drawCircle(blanco, (k * 0.025f).coerceAtLeast(0.35f), CamOrden.p(s, 0.01f, vv))
        }
    }

    /** El suelo mojado de la lluvia y la tormenta: el adarve más oscuro, con charcos que devuelven el cielo. */
    fun DrawScope.mojado(v: VistaOrden) {
        if (v.m.ficha.clima != Clima.LLUVIA && v.m.ficha.clima != Clima.TORMENTA) return
        drawPath(AdarveOrden.formaDelSuelo(), v.l.tono(C.HOLLIN).copy(alpha = 0.16f))
        val az = io.github.ruddy2003.vestigium.vidas.Azar(io.github.ruddy2003.vestigium.vidas.Semilla.de("orden", "charcos"))
        val cielo = lerp(v.l.medio, v.l.bajo, 0.4f)
        repeat(9) {
            val s = az.entre(0f, 1.6f); val vv = 1.5f + 14f * az.real() * az.real(); val k = CamOrden.k(s, vv); val q = CamOrden.p(s, 0.01f, vv)
            val charco = Path().apply { addOval(androidx.compose.ui.geometry.Rect(q.x - k * az.entre(0.2f, 0.45f), q.y - k * 0.04f, q.x + k * az.entre(0.2f, 0.45f), q.y + k * 0.05f)) }
            drawPath(charco, cielo.copy(alpha = 0.28f)); drawPath(charco, lerp(cielo, Color.White, 0.2f).copy(alpha = 0.22f), style = Stroke(0.4f))
        }
    }
}
