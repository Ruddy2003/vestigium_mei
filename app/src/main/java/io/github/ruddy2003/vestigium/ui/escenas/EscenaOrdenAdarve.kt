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
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.palo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.sillares
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen

/**
 * El adarve de la muralla del oeste: el sitio de quien mira, el suelo del gato y el marco del cuadro.
 *
 * - **El suelo**: losas de la misma piedra roja, gastadas en el medio por las rondas, con la arena que el viento arrima al
 *   pie del parapeto; a la izquierda, el borde sin pretil que cae al patio.
 * - **El parapeto y las almenas**, de piedra sin labrar, que se fugan hacia la torre: cada almena con su candil de latón
 *   en una hornacina (los «candiles de las almenas» que de noche hacen de Peñaseca «una corona»), y la de delante, grande
 *   y cortada por el borde, con **las muescas** («una por cada año»: rayas, nunca números) y su liquen.
 * - **El sitio del objeto del día**: el chico, en el fondo de la primera tronera, contra el páramo; el grande, de pie en el
 *   adarve, apoyado en la segunda almena.
 */
internal object AdarveOrden {
    private val C = ColorOrden
    private const val BORDE = CamOrden.BORDE
    private const val PARAPETO = CamOrden.PARAPETO
    private const val FUERA = CamOrden.FUERA

    /** Dónde empieza la primera almena, cuánto mide cada una y cada cuánto se repiten. */
    const val PRIMERA = 1.5f
    const val ALMENA = 1.25f
    const val PASO = 2.35f
    /** Hasta dónde llega el adarve: la puerta de la torre. */
    val FIN: Float get() = TorreOrden.V - TorreOrden.LADO / 2f

    /** Dónde empieza la almena [i] (0 la de delante). */
    fun almena(i: Int): Float = PRIMERA + i * PASO

    /** La caja de la almena [i]: sus ocho esquinas (para su sombra). */
    fun cuerpoDeLaAlmena(i: Int): List<V3> {
        val v0 = almena(i); val v1 = v0 + ALMENA
        return listOf(V3(PARAPETO, 0f, v0), V3(FUERA, 0f, v0), V3(FUERA, 0f, v1), V3(PARAPETO, 0f, v1),
            V3(PARAPETO, CamOrden.ALTO_ALMENA, v0), V3(FUERA, CamOrden.ALTO_ALMENA, v0), V3(FUERA, CamOrden.ALTO_ALMENA, v1), V3(PARAPETO, CamOrden.ALTO_ALMENA, v1))
    }

    /** El parapeto bajo, de punta a punta (para su sombra en el adarve). */
    fun cuerpoDelParapeto(): List<V3> = listOf(V3(PARAPETO, 0f, 0.3f), V3(PARAPETO, 0f, FIN), V3(PARAPETO, CamOrden.ALTO_PARAPETO, 0.3f), V3(PARAPETO, CamOrden.ALTO_PARAPETO, FIN),
        V3(FUERA, CamOrden.ALTO_PARAPETO, 0.3f), V3(FUERA, CamOrden.ALTO_PARAPETO, FIN))

    /** El suelo del adarve en el lienzo (para recortar lo que cae en él). */
    fun formaDelSuelo(): Path = poligono(listOf(V3(BORDE, 0f, 0.6f), V3(PARAPETO, 0f, 0.6f), V3(PARAPETO, 0f, FIN), V3(BORDE, 0f, FIN)).map { CamOrden.p(it) })

    // ---------------------------------------------------------------- El objeto del día

    /** El objeto chico, en el fondo de la primera tronera (en medio del grueso del parapeto). */
    val SOPORTE: V3 = V3((PARAPETO + FUERA) / 2f - 0.1f, CamOrden.ALTO_PARAPETO, almena(0) + ALMENA + (PASO - ALMENA) / 2f)
    /** El objeto grande: pisa en el adarve, junto a la segunda almena, y se apoya en ella. */
    val PIE_GRANDE: V3 = V3(PARAPETO - 0.35f, 0f, almena(1) + 0.75f)
    val APOYO_GRANDE: V3 = V3(PARAPETO, 1.15f, almena(1) + 0.75f)

    /** La escala de los objetos del día en un punto: la de Mongol, 60 unidades por metro a escala 1. */
    fun escalaEn(a: V3): Float = CamOrden.k(a.x, a.z) / 60f

    // ---------------------------------------------------------------- El suelo

    /** El suelo del adarve: las losas en perspectiva, gastadas en el medio, y la arena al pie del parapeto. */
    fun DrawScope.suelo(v: VistaOrden) {
        val lz = v.lz; val forma = formaDelSuelo(); val az = PiezasOrden.fijo("losas")
        val arriba = V3(0f, 1f, 0f)
        val cerca = lz.color(C.LOSA, arriba); val lejos = lz.color(C.LOSA, arriba, 0.2f)
        drawPath(forma, Brush.verticalGradient(0f to lejos, 1f to cerca, startY = CamOrden.H, endY = 285f))
        clipPath(forma) {
            // Las losas: hiladas de través, cada una partida en dos o tres, de largos distintos.
            var vv = 0.6f; var fila = 0
            val claro = lerp(cerca, v.l.tono(C.ARENA), 0.18f); val oscuro = lerp(cerca, v.l.tono(C.ROCA), 0.22f)
            while (vv < FIN) {
                val largo = az.entre(0.75f, 1.15f)
                var s = BORDE; val cortes = mutableListOf(BORDE)
                while (s < PARAPETO) { s += az.entre(0.8f, 1.6f); cortes += minOf(s, PARAPETO) }
                for (i in 0 until cortes.size - 1) {
                    val q = listOf(V3(cortes[i] + 0.03f, 0f, vv + 0.03f), V3(cortes[i + 1] - 0.03f, 0f, vv + 0.03f), V3(cortes[i + 1] - 0.03f, 0f, vv + largo - 0.03f), V3(cortes[i] + 0.03f, 0f, vv + largo - 0.03f)).map { CamOrden.p(it) }
                    val k = az.real()
                    if (k < 0.4f) drawPath(poligono(q), if (k < 0.2f) claro.copy(alpha = 0.55f) else oscuro.copy(alpha = 0.45f))
                }
                val junta = v.l.tono(lerp(C.LOSA, C.HOLLIN, 0.5f)).copy(alpha = (0.45f - vv * 0.01f).coerceAtLeast(0.15f))
                drawLine(junta, CamOrden.p(BORDE, 0f, vv), CamOrden.p(PARAPETO, 0f, vv), (CamOrden.k(1f, vv) * 0.025f).coerceIn(0.3f, 1.2f))
                for (c in cortes.drop(1).dropLast(1)) drawLine(junta, CamOrden.p(c, 0f, vv), CamOrden.p(c, 0f, vv + largo), (CamOrden.k(c, vv) * 0.02f).coerceIn(0.3f, 1f))
                vv += largo; fila++
            }
            // El gastado de las rondas, en el medio, y la arena arrimada al pie del parapeto.
            for (i in 0 until 14) { val vv2 = 1f + i * i * 0.22f; val q = CamOrden.p(1.1f, 0f, vv2); val k = CamOrden.k(1.1f, vv2); mancha(q, k * 0.9f, k * 0.3f, v.l.tono(lerp(C.LOSA, C.ARENA, 0.5f)), 0.22f) }
            for (i in 0 until 18) { val vv2 = 0.8f + i * i * 0.13f; val q = CamOrden.p(PARAPETO - 0.25f, 0f, vv2); val k = CamOrden.k(PARAPETO, vv2); mancha(q, k * az.entre(0.3f, 0.7f), k * 0.12f, v.l.tono(C.ARENA), 0.5f) }
        }
        // Las flores amarillas de la primavera, en las juntas al pie del parapeto.
        if (EscenaOrden.flores(v.m)) {
            val fl = PiezasOrden.fijo("flores"); val amarillo = v.lz.color(Color(0xFFE8C23A), V3(0f, 1f, 0f)); val hoja = v.lz.color(C.LIQUEN, V3(0f, 1f, 0f))
            repeat(26) { val s = PARAPETO - fl.entre(0.05f, 0.5f); val vv = 0.8f + 16f * fl.real() * fl.real(); val k = CamOrden.k(s, vv); val q = CamOrden.p(s, 0f, vv)
                drawLine(hoja, q, q + Offset(0f, -k * 0.08f), (k * 0.015f).coerceAtLeast(0.4f)); drawCircle(amarillo, (k * 0.03f).coerceAtLeast(0.5f), q + Offset(0f, -k * 0.09f)) }
        }
        // El canto del borde de dentro: una tira de losas más claras, sin pretil, sobre la caída al patio.
        val canto = poligono(listOf(V3(BORDE, 0.02f, 0.6f), V3(BORDE + 0.3f, 0.02f, 0.6f), V3(BORDE + 0.3f, 0.02f, FIN), V3(BORDE, 0.02f, FIN)).map { CamOrden.p(it) })
        drawPath(canto, lz.color(lerp(C.PIEDRA, C.ARENA, 0.25f), arriba, 0f, 0.2f))
        if (v.nieve > 0f) drawPath(forma, v.l.tono(Color(0xFFEEF0F4)).copy(alpha = 0.8f * v.nieve))
    }

    // ---------------------------------------------------------------- El parapeto y las almenas

    /**
     * El parapeto y sus almenas, de la torre hacia acá (las de delante tapan a las de atrás): su cara de dentro, el fondo
     * de las troneras y las almenas, cada una con su hornacina y su candil.
     */
    fun DrawScope.parapeto(v: VistaOrden) {
        val lz = v.lz; val n = V3(-1f, 0f, 0f)
        // La cara de dentro del parapeto, entera, y el fondo de las troneras.
        cara(lz, listOf(V3(PARAPETO, 0f, 0.3f), V3(PARAPETO, 0f, FIN), V3(PARAPETO, CamOrden.ALTO_PARAPETO, FIN), V3(PARAPETO, CamOrden.ALTO_PARAPETO, 0.3f)), n, C.PIEDRA)?.let { f ->
            clipPath(f) { sillares(V3(PARAPETO, 0f, FIN), V3(PARAPETO, 0f, 0.3f), CamOrden.ALTO_PARAPETO, lz.color(C.PIEDRA, n), PiezasOrden.fijo("parapeto"), 0.38f, 0.62f) }
        }
        cara(lz, listOf(V3(PARAPETO, CamOrden.ALTO_PARAPETO, 0.3f), V3(PARAPETO, CamOrden.ALTO_PARAPETO, FIN), V3(FUERA, CamOrden.ALTO_PARAPETO, FIN), V3(FUERA, CamOrden.ALTO_PARAPETO, 0.3f)), V3(0f, 1f, 0f), lerp(C.PIEDRA, C.ARENA, 0.2f), 0f, 0.2f)
        // Las gárgolas del pie del parapeto, por donde sale el agua de la lluvia: una cada dos almenas.
        for (i in 0 until EscenaOrden.ALMENAS step 2) {
            val vv = almena(i) + ALMENA + (PASO - ALMENA) / 2f
            drawPath(poligono(listOf(V3(PARAPETO - 0.01f, 0.02f, vv - 0.12f), V3(PARAPETO - 0.01f, 0.02f, vv + 0.12f), V3(PARAPETO - 0.01f, 0.16f, vv + 0.12f), V3(PARAPETO - 0.01f, 0.16f, vv - 0.12f)).map { CamOrden.p(it) }), v.l.tono(lerp(C.HOLLIN, C.ROCA, 0.2f)))
        }
        for (i in EscenaOrden.ALMENAS - 1 downTo 0) { almenaEntera(v, i); if (i == CABRIA + 1) cabria(v) }
    }

    /** En qué tronera está la cabria (la que sigue a esta almena). */
    const val CABRIA = 3

    /** La punta de la cabria, por fuera de la muralla, sobre el vacío. */
    val PUNTA_CABRIA: V3 get() = V3(FUERA + 2.3f, 2.0f, almena(CABRIA) + ALMENA + (PASO - ALMENA) / 2f + 0.1f)

    /**
     * La cabria del pozo **[Diseño propio]**: un brazo de madera que sale por una tronera sobre el vacío, con su polea en
     * la punta y la cuerda que baja recta por la pared de la meseta hasta el pozo del pie; en el adarve, el torno. Así se lee
     * la altura («paredes que caen a pico») y de dónde sube el agua. El verano del pozo seco, la cuerda está recogida.
     */
    private fun DrawScope.cabria(v: VistaOrden) {
        val lz = v.lz; val vv = almena(CABRIA) + ALMENA + (PASO - ALMENA) / 2f
        val pie = V3(PARAPETO + 0.2f, CamOrden.ALTO_PARAPETO, vv); val punta = PUNTA_CABRIA
        // El poste, el brazo y el tornapuntas.
        palo(lz, V3(PARAPETO - 0.25f, 0f, vv), V3(PARAPETO - 0.25f, 1.7f, vv), 0.2f, C.MADERA)
        palo(lz, pie, punta, 0.2f, C.MADERA)
        palo(lz, V3(PARAPETO - 0.25f, 1.6f, vv), V3(FUERA + 1.2f, 1.55f, vv + 0.05f), 0.12f, C.MADERA_OSC)
        val q = CamOrden.p(punta); val k = CamOrden.k(punta.x, punta.z)
        drawCircle(lz.color(C.MADERA_OSC, V3(0f, 0f, -1f)), (k * 0.22f).coerceAtLeast(1f), q)
        // El torno en el adarve, con la cuerda enrollada.
        palo(lz, V3(PARAPETO - 1.1f, 0.55f, vv - 0.4f), V3(PARAPETO - 1.1f, 0.55f, vv + 0.4f), 0.36f, lerp(C.MADERA, C.ARENA, 0.3f))
        for (dv in listOf(-0.45f, 0.45f)) palo(lz, V3(PARAPETO - 1.1f, 0f, vv + dv), V3(PARAPETO - 1.1f, 0.62f, vv + dv), 0.12f, C.MADERA_OSC)
    }

    /** La cuerda de la cabria, que baja recta al vacío: va con lo lejano (el parapeto y el adarve, delante, la tapan). */
    fun DrawScope.cuerdaDeLaCabria(v: VistaOrden) {
        if (EscenaOrden.pozoSeco(v.m)) return
        val punta = PUNTA_CABRIA
        drawLine(v.l.tono(lerp(C.MADERA, C.HOLLIN, 0.3f)).copy(alpha = 0.75f), CamOrden.p(punta), CamOrden.p(punta.x, CamOrden.LLANO, punta.z), 0.5f)
    }

    /** Una almena: la caja de piedra, su liquen, la hornacina con el candil y, en las dos de delante, las muescas. */
    private fun DrawScope.almenaEntera(v: VistaOrden, i: Int) {
        val lz = v.lz; val v0 = almena(i); val v1 = v0 + ALMENA
        val az = PiezasOrden.fijo("almena", i)
        val lejos = (v0 / 120f).coerceIn(0f, 0.2f)
        caja(lz, (PARAPETO + FUERA) / 2f, (v0 + v1) / 2f, 0f, FUERA - PARAPETO, ALMENA, CamOrden.ALTO_PARAPETO - 0.01f, CamOrden.ALTO_ALMENA, C.PIEDRA, lejos, arriba = lerp(C.PIEDRA, C.ARENA, 0.25f)) { lado, _ ->
            when (lado) {
                2 -> sillares(V3(PARAPETO, CamOrden.ALTO_PARAPETO, v0), V3(PARAPETO, CamOrden.ALTO_PARAPETO, v1), 1f, lz.color(C.PIEDRA, V3(-1f, 0f, 0f), lejos), az, 0.33f, 0.55f, lejos = lejos)
                0 -> sillares(V3(PARAPETO, CamOrden.ALTO_PARAPETO, v0), V3(FUERA, CamOrden.ALTO_PARAPETO, v0), 1f, lz.color(C.PIEDRA, V3(0f, 0f, -1f), lejos), az, 0.33f, 0.55f, lejos = lejos)
                4 -> liquen(v, i, az)
                else -> Unit
            }
        }
        if (i <= 1) muescas(v, i)
        hornacina(v, i)
    }

    /** El liquen en lo alto de la almena: manchas de un verde gris pálido, del lado de la sombra. */
    private fun DrawScope.liquen(v: VistaOrden, i: Int, az: io.github.ruddy2003.vestigium.vidas.Azar) {
        val v0 = almena(i)
        repeat(4) { val q = CamOrden.p(az.entre(PARAPETO + 0.1f, FUERA - 0.1f), CamOrden.ALTO_ALMENA, v0 + az.entre(0.1f, ALMENA - 0.1f)); val k = CamOrden.k(PARAPETO, v0); mancha(q, k * az.entre(0.1f, 0.25f), k * 0.06f, v.lz.color(C.LIQUEN, V3(0f, 1f, 0f)), 0.6f) }
    }

    /**
     * Las muescas de la almena: rayas cortas grabadas en la piedra, en grupos, una por cada año («la escriba las ha contado
     * dos veces y le han salido dos números distintos»). Rayas, nunca números.
     */
    private fun DrawScope.muescas(v: VistaOrden, i: Int) {
        val v0 = almena(i); val az = PiezasOrden.fijo("muescas", i)
        val cara = poligono(listOf(V3(PARAPETO, CamOrden.ALTO_PARAPETO, v0), V3(PARAPETO, CamOrden.ALTO_PARAPETO, v0 + ALMENA), V3(PARAPETO, CamOrden.ALTO_ALMENA, v0 + ALMENA), V3(PARAPETO, CamOrden.ALTO_ALMENA, v0)).map { CamOrden.p(it) })
        val hondo = v.l.tono(lerp(C.PIEDRA_OSC, C.HOLLIN, 0.4f)); val filo = v.lz.color(lerp(C.PIEDRA, C.ARENA, 0.5f), V3(-1f, 0f, -0.3f))
        val filas = if (i == 0) 2 else 1
        clipPath(cara) {
            for (f in 0 until filas) {
                val h = CamOrden.ALTO_PARAPETO + 0.32f + f * 0.15f
                val cuantas = if (i == 0) 14 - f * 3 else 8
                for (j in 0 until cuantas) {
                    if (j % 5 == 4) continue                                     // el hueco entre grupos
                    val vv = v0 + 0.15f + j * 0.042f + az.entre(-0.005f, 0.005f)
                    val a = CamOrden.p(PARAPETO - 0.005f, h, vv); val b = CamOrden.p(PARAPETO - 0.005f, h + 0.1f, vv + az.entre(-0.012f, 0.012f))
                    val w = (CamOrden.k(PARAPETO, vv) * 0.007f).coerceAtLeast(0.3f)
                    drawLine(hondo.copy(alpha = 0.6f), a, b, w, StrokeCap.Round); drawLine(filo.copy(alpha = 0.35f), a + Offset(w * 0.9f, 0f), b + Offset(w * 0.9f, 0f), w * 0.6f)
                }
            }
        }
    }

    /** Dónde arde el candil de la almena [i] (en su hornacina, en la cara de dentro). */
    fun candil(i: Int): V3 = V3(PARAPETO - 0.02f, CamOrden.ALTO_PARAPETO + 0.55f, almena(i) + ALMENA * 0.62f)

    /**
     * La hornacina de cada almena con su candil de latón: «un vaso de latón con aceite de cabra y una mecha». El de la
     * almena de delante no está los días que el candil es el objeto (está en la tronera, como objeto del día).
     */
    private fun DrawScope.hornacina(v: VistaOrden, i: Int) {
        val c = candil(i); val k = CamOrden.k(c.x, c.z)
        if (k < 6f) return
        val q = CamOrden.p(c); val w = 0.32f * k; val h = 0.42f * k
        val hueco = Path().apply { moveTo(q.x - w * 0.35f, q.y + h * 0.15f); lineTo(q.x - w * 0.35f, q.y - h * 0.45f); cubicTo(q.x - w * 0.35f, q.y - h * 0.85f, q.x + w * 0.35f, q.y - h * 0.85f, q.x + w * 0.35f, q.y - h * 0.45f); lineTo(q.x + w * 0.35f, q.y + h * 0.15f); close() }
        drawPath(hueco, v.l.tono(lerp(C.PIEDRA_OSC, C.HOLLIN, 0.45f)))
        val encendido = i < EscenaOrden.candiles(v.m)
        if (i == 0 && !ObjetosOrden.elCuadroPinta(v.m, "candil")) return
        // El vaso de latón.
        val vaso = Path().apply { moveTo(q.x - w * 0.18f, q.y + h * 0.12f); lineTo(q.x + w * 0.18f, q.y + h * 0.12f); lineTo(q.x + w * 0.22f, q.y - h * 0.06f); lineTo(q.x - w * 0.22f, q.y - h * 0.06f); close() }
        drawPath(vaso, if (encendido && v.l.noche > 0.3f) lerp(C.LATON, C.LUMBRE, 0.5f) else v.lz.color(C.LATON, V3(-1f, 0.3f, -0.4f)))
        if (encendido) resplandor(q + Offset(0f, -h * 0.25f), w * 1.2f, 0.4f + 0.5f * v.l.noche, C.LUMBRE)
    }

    /**
     * Lo que alumbran de noche los candiles encendidos: el charco cálido en las losas al pie de cada almena y el halo en
     * su cara; el de delante, grande. Se suma a lo pintado (va después de las sombras).
     */
    fun DrawScope.luzDeLosCandiles(v: VistaOrden) {
        val n = EscenaOrden.candiles(v.m); if (n <= 0) return
        val k = 0.2f + 0.8f * v.l.noche
        for (i in 0 until n) {
            if (i == 0 && !ObjetosOrden.elCuadroPinta(v.m, "candil")) continue
            val c = candil(i); val e = CamOrden.k(c.x, c.z)
            val q = CamOrden.p(c)
            // El halo en la piedra de la almena, alrededor de la hornacina.
            resplandor(q, (e * 0.75f).coerceAtLeast(3f), 0.5f * k, C.LUMBRE)
            // El charco en las losas al pie, chico y tendido; los lejanos ya no se distinguen.
            if (e < 9f) continue
            val pie = CamOrden.p(PARAPETO - 0.35f, 0f, c.z)
            drawOval(Brush.radialGradient(listOf(C.LUMBRE.copy(alpha = (0.26f * k).coerceIn(0f, 1f)), Color.Transparent), pie, e * 0.8f), Offset(pie.x - e * 0.8f, pie.y - e * 0.22f), Size(e * 1.6f, e * 0.44f), blendMode = BlendMode.Plus)
        }
    }

    /**
     * El paño oscuro del luto, colgado de lo alto de la almena de delante **[Diseño propio]**; y en las fiestas, el pendón
     * carmesí de dos puntas en su asta, en lo alto de la aguja de la torre, que ondea con el viento, con el candil de la orden bordado
     * en latón (sin letras).
     */
    /** El pie del asta del pendón de las fiestas, en lo alto de la aguja de la torre, sobre la argolla. */
    val PENDON: V3 get() = V3(TorreOrden.S, TorreOrden.ARGOLLA + 0.7f, TorreOrden.V)

    fun DrawScope.panos(v: VistaOrden) {
        if (EscenaOrden.luto(v.m)) {
            val a = CamOrden.p(PARAPETO - 0.02f, CamOrden.ALTO_ALMENA - 0.02f, almena(0) + 0.25f); val b = CamOrden.p(PARAPETO - 0.02f, CamOrden.ALTO_ALMENA - 0.02f, almena(0) + 0.75f)
            pano(v.l, a, b, CamOrden.k(PARAPETO, almena(0)) * 0.55f, lerp(C.HOLLIN, C.ROCA, 0.15f))
        }
        if (!EscenaOrden.fiesta(v.m)) return
        val lz = v.lz; val pie = PENDON; val punta = V3(pie.x, pie.y + 2.6f, pie.z)
        palo(lz, pie, punta, 0.12f, C.HIERRO)
        val q = CamOrden.p(punta); val k = CamOrden.k(punta.x, punta.z)
        val largo = k * 2.6f; val ancho = k * 1.0f; val ondea = 0.15f + 0.25f * v.m.viento
        val tela = Path().apply {
            moveTo(q.x, q.y)
            cubicTo(q.x - largo * 0.35f, q.y - ancho * ondea, q.x - largo * 0.65f, q.y + ancho * ondea, q.x - largo, q.y + ancho * 0.1f)
            lineTo(q.x - largo * 0.78f, q.y + ancho * 0.5f); lineTo(q.x - largo, q.y + ancho * 0.92f)
            cubicTo(q.x - largo * 0.65f, q.y + ancho * (1f + ondea), q.x - largo * 0.35f, q.y + ancho * (1f - ondea), q.x, q.y + ancho)
            close()
        }
        drawPath(tela, Brush.horizontalGradient(listOf(lz.color(C.CARMESI, V3(0f, 0f, -1f), 0f, 0.3f), lz.color(C.CARMESI, V3(0f, 0f, 1f))), q.x - largo, q.x))
        // El candil bordado: el vaso y la llama, de latón.
        val c = Offset(q.x - largo * 0.38f, q.y + ancho * 0.55f)
        drawPath(poligono(c + Offset(-ancho * 0.12f, 0f), c + Offset(ancho * 0.12f, 0f), c + Offset(ancho * 0.16f, -ancho * 0.12f), c + Offset(-ancho * 0.16f, -ancho * 0.12f)), lz.color(C.LATON, V3(0f, 0f, -1f)))
        drawPath(poligono(c + Offset(-ancho * 0.05f, -ancho * 0.14f), c + Offset(ancho * 0.05f, -ancho * 0.14f), c + Offset(0f, -ancho * 0.34f)), lz.color(lerp(C.LATON, C.LUMBRE, 0.4f), V3(0f, 0f, -1f)))
    }

    /**
     * Lo que deja la guardia en el adarve **[Diseño propio]**: el taburete de la ronda junto al borde de dentro, el cántaro
     * del agua arrimado al parapeto y un rollo de cuerda en las losas. Donde los dejaría alguien, no en fila.
     */
    fun DrawScope.enseres(v: VistaOrden) {
        val lz = v.lz
        // El rollo de cuerda, tendido en las losas: tres vueltas que se enciman.
        val c = V3(0.55f, 0.02f, 4.3f); val cuerda = lz.color(lerp(C.MADERA, C.ARENA, 0.45f), V3(0f, 1f, 0f))
        // El cabo suelto, que se va hacia el borde.
        val cola = listOf(V3(c.x - 0.3f, 0.02f, c.z + 0.05f), V3(c.x - 0.55f, 0.02f, c.z - 0.25f), V3(c.x - 0.62f, 0.02f, c.z - 0.7f)).map { CamOrden.p(it) }
        drawPath(Path().apply { moveTo(cola[0].x, cola[0].y); cubicTo(cola[1].x, cola[1].y, cola[1].x, cola[1].y, cola[2].x, cola[2].y) }, cuerda, style = Stroke((CamOrden.k(c.x, c.z) * 0.05f).coerceAtLeast(0.5f), cap = StrokeCap.Round))
        for (k in 0 until 3) {
            val r = 0.34f - k * 0.06f; val cs = c.x + 0.04f * k; val cv = c.z - 0.03f * k
            val pts = (0..20).map { j -> val a = j * 6.2832f / 20f; val ondula = 1f + 0.06f * kotlin.math.sin(a * 3f + k); CamOrden.p(cs + r * ondula * kotlin.math.cos(a), c.y + 0.05f * k, cv + r * ondula * kotlin.math.sin(a) * 0.9f) }
            val vuelta = Path().apply { moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) lineTo(q.x, q.y) }
            drawPath(vuelta, lerp(cuerda, v.l.tono(C.HOLLIN), 0.25f), style = Stroke((CamOrden.k(c.x, c.z) * 0.07f).coerceAtLeast(0.6f)))
            drawPath(vuelta, cuerda, style = Stroke((CamOrden.k(c.x, c.z) * 0.04f).coerceAtLeast(0.4f)))
        }
        // El taburete, de tres patas, con la tapa gastada.
        val t = V3(0.25f, 0f, 6.6f)
        for (a in listOf(0.3f, 2.4f, 4.5f)) palo(lz, V3(t.x + 0.17f * kotlin.math.cos(a), 0f, t.z + 0.17f * kotlin.math.sin(a)), V3(t.x + 0.1f * kotlin.math.cos(a), 0.44f, t.z + 0.1f * kotlin.math.sin(a)), 0.05f, C.MADERA_OSC)
        caja(lz, t.x, t.z, 0.3f, 0.36f, 0.36f, 0.42f, 0.48f, C.MADERA, arriba = lerp(C.MADERA, C.ARENA, 0.3f))
        // Los cubos de la cabria, junto al torno: uno de pie y otro volcado.
        val tv = almena(CABRIA) + ALMENA + (PASO - ALMENA) / 2f
        for ((ds, dv) in listOf(-0.55f to -0.75f, -0.2f to -1.05f)) {
            val b = V3(PARAPETO + ds - 0.2f, 0f, tv + dv); val qb = CamOrden.p(b); val kb = CamOrden.k(b.x, b.z)
            val cubo = poligono(listOf(qb + Offset(-kb * 0.16f, 0f), qb + Offset(kb * 0.16f, 0f), qb + Offset(kb * 0.19f, -kb * 0.34f), qb + Offset(-kb * 0.19f, -kb * 0.34f)))
            drawPath(cubo, Brush.horizontalGradient(listOf(lz.color(C.MADERA, V3(1f, 0f, -0.5f)), lz.color(C.MADERA, V3(-1f, 0f, -0.5f))), qb.x - kb * 0.19f, qb.x + kb * 0.19f))
            drawLine(lz.color(C.HIERRO, V3(0f, 0f, -1f)), qb + Offset(-kb * 0.18f, -kb * 0.25f), qb + Offset(kb * 0.18f, -kb * 0.25f), (kb * 0.03f).coerceAtLeast(0.4f))
            drawOval(v.l.tono(C.MADERA_OSC), Offset(qb.x - kb * 0.19f, qb.y - kb * 0.38f), Size(kb * 0.38f, kb * 0.08f))
        }
        // El banco de la guardia, arrimado al parapeto, con el candil tapado de las rondas de noche encima.
        val b0 = 5.7f; val b1 = 7.3f
        for (vv in listOf(b0 + 0.15f, b1 - 0.15f)) caja(lz, PARAPETO - 0.25f, vv, 0f, 0.32f, 0.1f, 0f, 0.42f, C.MADERA_OSC)
        caja(lz, PARAPETO - 0.25f, (b0 + b1) / 2f, 0f, 0.4f, b1 - b0, 0.42f, 0.48f, C.MADERA, arriba = lerp(C.MADERA, C.ARENA, 0.3f))
        val farol = V3(PARAPETO - 0.25f, 0.48f, b1 - 0.35f); val qf = CamOrden.p(farol); val kf = CamOrden.k(farol.x, farol.z)
        drawRect(lz.color(C.HIERRO_CLARO, V3(-1f, 0f, -0.5f)), Offset(qf.x - kf * 0.09f, qf.y - kf * 0.26f), Size(kf * 0.18f, kf * 0.26f))
        drawPath(poligono(qf + Offset(-kf * 0.11f, -kf * 0.26f), qf + Offset(kf * 0.11f, -kf * 0.26f), qf + Offset(0f, -kf * 0.36f)), lz.color(C.HIERRO, V3(0f, 1f, -0.5f)))
        if (v.l.noche > 0.4f) drawLine(C.LUMBRE.copy(alpha = 0.8f), qf + Offset(-kf * 0.06f, -kf * 0.05f), qf + Offset(kf * 0.06f, -kf * 0.05f), (kf * 0.025f).coerceAtLeast(0.4f))
        // El cántaro, contra el parapeto, delante del banco, con su boca.
        val j = V3(PARAPETO - 0.24f, 0f, 5.3f); val q = CamOrden.p(j); val k = CamOrden.k(j.x, j.z)
        val alto = 0.55f * k; val ancho = 0.36f * k
        val forma = Path().apply {
            moveTo(q.x - ancho * 0.22f, q.y); cubicTo(q.x - ancho * 0.75f, q.y - alto * 0.1f, q.x - ancho * 0.7f, q.y - alto * 0.75f, q.x - ancho * 0.18f, q.y - alto * 0.88f)
            lineTo(q.x - ancho * 0.2f, q.y - alto); lineTo(q.x + ancho * 0.2f, q.y - alto); lineTo(q.x + ancho * 0.18f, q.y - alto * 0.88f)
            cubicTo(q.x + ancho * 0.7f, q.y - alto * 0.75f, q.x + ancho * 0.75f, q.y - alto * 0.1f, q.x + ancho * 0.22f, q.y); close()
        }
        volumen(v.l, v.r, forma, q.x - ancho * 0.75f, q.x + ancho * 0.75f, lerp(C.ROCA, C.ARENA, 0.3f))
        drawOval(v.l.tono(C.HOLLIN), Offset(q.x - ancho * 0.16f, q.y - alto * 1.02f), Size(ancho * 0.32f, ancho * 0.1f))
    }

    /** Dónde se pone el gato: el suelo del adarve, delante de todo. */
    const val PISO_GATO = 266f
    const val GATO_DESDE = 116f
    const val GATO_HASTA = 258f

    /** Sobre la tronera de la segunda almena, sentado en el parapeto. */
    val MIRADOR: Offset get() = CamOrden.p(PARAPETO + 0.35f, CamOrden.ALTO_PARAPETO, almena(2) - (PASO - ALMENA) / 2f)

    /** Un velo cálido sobre el adarve cuando se adelantan las luces con el cielo cubierto (el polvo, la tormenta). */
    fun DrawScope.nieveEnLasAlmenas(v: VistaOrden) {
        if (v.nieve <= 0f) return
        for (i in 0 until EscenaOrden.ALMENAS) {
            val v0 = almena(i)
            val q = listOf(V3(PARAPETO, CamOrden.ALTO_ALMENA + 0.08f, v0), V3(FUERA, CamOrden.ALTO_ALMENA + 0.08f, v0), V3(FUERA, CamOrden.ALTO_ALMENA + 0.08f, v0 + ALMENA), V3(PARAPETO, CamOrden.ALTO_ALMENA + 0.08f, v0 + ALMENA)).map { CamOrden.p(it) }
            drawPath(poligono(q), v.l.tono(Color(0xFFF2F2F4)))
            drawPath(poligono(q), v.l.tono(Color(0xFFDDE2EE)), style = Stroke(0.6f))
        }
    }
}
