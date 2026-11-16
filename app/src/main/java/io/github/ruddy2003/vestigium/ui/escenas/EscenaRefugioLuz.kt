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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Compuerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La luz de Refugio, que es el cuadro. Sigue la lógica de la capilla, Scriptorium y Renacimiento (`docs/diseno/ESCENAS.md`,
 * sección 2 bis), traída a un sitio sin ventanas:
 * 1. **La luz tiene forma y cae sobre penumbra:** la sala está en penumbra verde azulada (nunca gris) y la lámpara de jaula
 *    deja su charco con la sombra de los alambres de la jaula ([charcoDeLaLampara]); la luz del pasillo entra por el portón
 *    y deja en el suelo de la sala la forma del arco ([luzDelPorton]); el violeta de los tanques sale por el ventanal y
 *    deja su forma en el suelo y la pared del pasillo ([luzDelVentanal]).
 * 2. **Camina con la hora:** la lámpara va con ella (los manómetros, la bomba, la bancada, el filtro); con la Puerta
 *    abierta, la mancha del sol se tiende por el pasillo ([solEnElPasillo]).
 * 3. **Se ve en el aire:** el cono de la lámpara con su polvo, el haz del portón, la bruma del pasillo ([enElAire]).
 * 4. **Enciende lo pintado:** cada cosa toma su color de [VistaRefugio.c], que sube donde da la lámpara: el verde de las
 *    máquinas, las ruedas rojas, el latón de los manómetros, el cuaderno.
 * 5. **De noche, dos luces de color opuesto:** el azul de piscina del pasillo (y el violeta de los tanques) contra el ámbar
 *    de la lucecita de guardia y, en el turno de más, la lámpara; con la Puerta abierta, el fuego de fuera.
 *
 * **[Diseño propio]**: el cuento dice que la luz del refugio es «blanca y sin sombra, que no se sabe de dónde sale»; aquí
 * eso es el pasillo, y la sala, de techo bajo, tiene su lámpara de trabajo.
 */
internal object LuzRefugio {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio
    private val S = SalaRefugio
    private val P = PasilloRefugio

    // =================================================================
    //  En el pasillo
    // =================================================================

    /** El suelo del pasillo, de la boca a la Puerta. */
    private fun sueloDelPasillo(): Path = K.forma(listOf(V3(P.IZQ, 0f, P.BOCA - 0.3f), V3(P.DER, 0f, P.BOCA - 0.3f), V3(P.DER, 0f, P.PUERTA_Z), V3(P.IZQ, 0f, P.PUERTA_Z)))

    /** La pared de la derecha del pasillo (la que recibe el violeta del ventanal y el sol de la tarde). */
    private fun paredDerecha(): Path = K.forma(listOf(V3(P.DER, 0f, P.BOCA), V3(P.DER, 0f, P.PUERTA_Z), V3(P.DER, P.PARED, P.PUERTA_Z), V3(P.DER, P.PARED, P.BOCA)))

    /**
     * Lo que la luz deja en el pasillo, después de pintar sus superficies: el charco de cada tubo en el suelo y su reflejo
     * en el suelo húmedo, los tubos mismos con su halo, el violeta del ventanal, el rojo de la compuerta, el sol que entra
     * por la Puerta y el fuego de fuera; y la bruma del aire, que crece hacia el fondo.
     */
    fun DrawScope.enElPasillo(v: VistaRefugio) {
        val suelo = sueloDelPasillo()
        clipPath(suelo) {
            for (z in P.TUBOS) {
                val c = V3(P.CENTRO_X, 0f, z); val k = K.k(c)
                charco(K.p(c), 1.3f * k, 1.3f * k * (K.OJO / K.hondo(c)).coerceIn(0.05f, 1f), v.colorTubo, 0.14f * v.fuerzaTubo)
                // El reflejo del tubo en el suelo húmedo: una columna de luz que viene hacia los ojos.
                val arriba = K.p(V3(P.CENTRO_X, 0f, z + 0.6f)); val abajo = K.p(V3(P.CENTRO_X, 0f, z - 1.4f))
                drawLine(lerp(v.colorTubo, Color.White, 0.3f).copy(alpha = (0.16f * v.fuerzaTubo).coerceIn(0f, 1f)), arriba, abajo, 0.25f * k, StrokeCap.Round, blendMode = BlendMode.Plus)
            }
            luzDelVentanal(v, true)
            v.sol?.let { solEnElPasillo(v, it) }
            if (v.fuego > 0.02f) { val c = K.p(V3(P.CENTRO_X, 0f, P.PUERTA_Z - 1.5f)); charco(c, 120f, 22f, C.FUEGO, 0.5f * v.fuego) }
            // Con lluvia, la que entra por la Puerta moja el suelo: un charco oscuro que devuelve el hueco de la Puerta.
            val f = v.fuera
            if (f != null && v.puerta == Puerta.ABIERTA && R.climaDeFuera(v.m) == Clima.LLUVIA) {
                val charcoPts = (0 until 14).map { i -> val a = 2f * PI.toFloat() * i / 14; V3(P.CENTRO_X + cos(a) * 2.1f, 0.004f, P.PUERTA_Z - 3.2f + sin(a) * 3.0f * (0.8f + 0.2f * ((i * 7) % 3))) }
                drawPath(K.forma(charcoPts), lerp(C.TINTA, C.PISCINA, 0.3f).copy(alpha = 0.35f))
                val cielo = lerp(f.medio, Color.White, 0.3f)
                for (k in 0..6) { val x = P.CENTRO_X - 1.2f + 0.4f * k; drawLine(cielo.copy(alpha = 0.35f), K.p(V3(x, 0f, P.PUERTA_Z - 0.6f)), K.p(V3(x + 0.05f, 0f, P.PUERTA_Z - 4.5f)), 1.1f, StrokeCap.Round, blendMode = BlendMode.Plus) }
            }
            if (v.compuerta == Compuerta.BAJADA) { val c = K.p(V3(P.IZQ + 0.6f, 0f, (P.COMPUERTA_Z0 + P.COMPUERTA_Z1) / 2f)); charco(c, 70f, 18f, C.ALARMA, 0.4f) }
            if (v.alarma) { val c = K.p(V3(P.CENTRO_X, 0f, 20f)); charco(c, 60f, 12f, C.ALARMA, 0.35f) }
        }
        clipPath(paredDerecha()) { luzDelVentanal(v, false) }
        // Los tubos de luz de la clave, con su halo.
        for (z in P.TUBOS) {
            val a = V3(P.CENTRO_X, P.BOVEDA - 0.18f, z - 0.6f); val b = V3(P.CENTRO_X, P.BOVEDA - 0.18f, z + 0.6f)
            Trazo3.run { barra(a + V3(0f, 0.05f, 0f), b + V3(0f, 0.05f, 0f), 0.16f, v.c(K.medio(a, b), K.ABAJO, C.HIERRO, true), StrokeCap.Butt) }
            val luz = lerp(v.colorTubo, Color.White, 0.55f * v.fuerzaTubo.coerceAtMost(1f))
            Trazo3.run { barra(a, b, 0.07f, luz, StrokeCap.Round) }
            val m = K.p(K.medio(a, b)); val k = K.k(a)
            mancha(m, 0.9f * k, 0.35f * k, v.colorTubo, 0.3f * v.fuerzaTubo.coerceAtMost(1f))
        }
        // El haz del sol en el aire, de la Puerta a su mancha, con su polvo.
        v.sol?.let { s ->
            val col = lerp(s.color, Color(0xFFFFD49A), 0.3f)
            val boca = K.circulo(V3(P.PUERTA_C.x, P.PUERTA_C.y, P.PUERTA_Z), K.FRENTE, P.PUERTA_R * 0.95f, 16)
            val suelo = P.manchaDelSol(s)
            val todos = (boca + suelo.filterIndexed { i, _ -> i % 2 == 0 }).filter { K.hondo(it) > 1f }.map { K.p(it) }
            if (todos.size >= 3) drawPath(poligono(K.casco(todos)), col.copy(alpha = 0.07f * s.fuerza), blendMode = BlendMode.Plus)
            val az = Azar(Semilla.de("refugio", "polvo", "sol"))
            repeat(40) { val a = boca[az.entre(0f, 15.9f).toInt()]; val b = suelo[az.entre(0f, suelo.size - 0.1f).toInt()]; val k = az.entre(0.05f, 0.95f)
                drawCircle(col.copy(alpha = (0.6f * s.fuerza * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.6f), K.p(a + (b - a) * k)) }
        }
        // La bruma del aire del pasillo, que crece hacia la Puerta: la lejanía con profundidad.
        val fuga = K.p(V3(P.CENTRO_X, 2f, P.PUERTA_Z - 3f))
        mancha(fuga + Offset(0f, -8f), 40f, 22f, lerp(v.ambientePasillo, v.colorTubo, 0.4f), 0.07f)
        // El rojo de la compuerta y de la alarma en el aire.
        if (v.compuerta == Compuerta.BAJADA) mancha(K.p(P.LUZ_COMPUERTA), 0.9f * K.k(P.LUZ_COMPUERTA), 0.7f * K.k(P.LUZ_COMPUERTA), C.ALARMA, 0.18f)
    }

    /** Un charco de luz en el suelo: una elipse que suma su color, con el borde blando. */
    private fun DrawScope.charco(c: Offset, rx: Float, ry: Float, color: Color, fuerza: Float) {
        if (fuerza <= 0.01f || rx <= 0.5f || ry <= 0.3f) return
        scale(rx / ry, 1f, c) { drawCircle(Brush.radialGradient(listOf(color.copy(alpha = fuerza.coerceIn(0f, 1f)), color.copy(alpha = (0.45f * fuerza).coerceIn(0f, 1f)), Color.Transparent), c, ry), ry, c, blendMode = BlendMode.Plus) }
    }

    /**
     * El violeta de los tanques que sale por el ventanal: las lámparas de los tanques, detrás del vidrio, proyectan el hueco
     * del ventanal en el suelo del pasillo y en la pared de enfrente; de noche, «la última luz del refugio».
     */
    private fun DrawScope.luzDelVentanal(v: VistaRefugio, enSuelo: Boolean) {
        val fuerza = (0.1f + 0.45f * v.tanques * (0.35f + 0.65f * v.noche)).coerceIn(0f, 1f)
        val fuente = V3(P.IZQ - 1.4f, 3.1f, (P.VENTANAL_Z0 + P.VENTANAL_Z1) / 2f)
        val esquinas = listOf(V3(P.IZQ, P.VENTANAL_Y0, P.VENTANAL_Z0), V3(P.IZQ, P.VENTANAL_Y0, P.VENTANAL_Z1), V3(P.IZQ, P.VENTANAL_Y1, P.VENTANAL_Z1), V3(P.IZQ, P.VENTANAL_Y1, P.VENTANAL_Z0))
        val forma = esquinas.map { q ->
            val d = q - fuente
            if (enSuelo) { val t = -fuente.y / d.y.coerceAtMost(-0.01f); fuente + d * t } else { val t = (P.DER - fuente.x) / d.x; fuente + d * t }
        }
        val camino = K.forma(forma)
        val col = lerp(C.VIOLETA, Color.White, 0.15f)
        for (ancho in listOf(9f, 5f, 2.4f)) drawPath(camino, col.copy(alpha = 0.03f * fuerza), style = Stroke(ancho), blendMode = BlendMode.Plus)
        drawPath(camino, col.copy(alpha = 0.32f * fuerza), blendMode = BlendMode.Plus)
        // Los palos del marco cortan la mancha en cuatro.
        for (k in 1..3) {
            val zz = P.VENTANAL_Z0 + (P.VENTANAL_Z1 - P.VENTANAL_Z0) * k / 4f
            val a = V3(P.IZQ, P.VENTANAL_Y0, zz); val b = V3(P.IZQ, P.VENTANAL_Y1, zz)
            fun lleva(q: V3): V3 { val d = q - fuente; return if (enSuelo) fuente + d * (-fuente.y / d.y.coerceAtMost(-0.01f)) else fuente + d * ((P.DER - fuente.x) / d.x) }
            val pa = lleva(a); val pb = lleva(b)
            Trazo3.run { barra(pa, pb, 0.18f, lerp(C.SOMBRA, C.TINTA, 0.3f).copy(alpha = 0.35f * fuerza)) }
        }
    }

    /** La mancha del sol que entra por la Puerta abierta, en el suelo del pasillo, con su borde blando y su haz en el aire. */
    private fun DrawScope.solEnElPasillo(v: VistaRefugio, s: EscenaRefugio.SolDePuerta) {
        val mancha = P.manchaDelSol(s)
        val forma = K.forma(mancha)
        val col = lerp(s.color, Color(0xFFFFD49A), 0.35f)
        for (ancho in listOf(10f, 6f, 3f)) drawPath(forma, col.copy(alpha = 0.04f * s.fuerza), style = Stroke(ancho), blendMode = BlendMode.Plus)
        drawPath(forma, col.copy(alpha = 0.42f * s.fuerza), blendMode = BlendMode.Overlay)
        drawPath(forma, col.copy(alpha = 0.3f * s.fuerza), blendMode = BlendMode.Plus)
    }

    // =================================================================
    //  En la sala
    // =================================================================

    /** El suelo de la sala, hasta la pared del fondo. */
    private fun sueloDeLaSala(): Path = K.forma(listOf(V3(S.IZQ, 0f, 1.2f), V3(S.DER, 0f, 1.2f), V3(S.DER, 0f, S.FONDO), V3(S.IZQ, 0f, S.FONDO)))

    private fun paredDelFondo(): Path = K.forma(listOf(V3(S.IZQ, 0f, S.FONDO), V3(S.DER, 0f, S.FONDO), V3(S.DER, S.TECHO, S.FONDO), V3(S.IZQ, S.TECHO, S.FONDO)))

    /**
     * Lo que la luz deja en las superficies de la sala, antes de las cosas: la luz del pasillo que entra por el portón y deja
     * en el suelo la forma del arco; el charco de cada lámpara de trabajo, con la sombra de los alambres de su jaula, en el
     * suelo y en la pared; y el halo ámbar de la guardia.
     */
    fun DrawScope.enLaSala(v: VistaRefugio) {
        clipPath(sueloDeLaSala()) { luzDelPorton(v) }
        for (o in v.obras) charcoDeLaLampara(v, S.bombilla(o), S.haciaDonde(o))
        if (v.alarma) { val b = K.p(S.BALIZA); clipPath(paredDelFondo()) { mancha(b, 70f, 55f, C.ALARMA, 0.22f) } }
        // El halo ámbar de la lucecita de guardia en la pared y en el tablero: de noche, la luz cálida de la sala.
        clipPath(paredDelFondo()) { val g = S.GUARDIA; val q = K.p(g + V3(0f, -0.5f, 0f)); val k = K.k(g); charco(q, 1.5f * k, 1.1f * k, C.GUARDIA, 0.3f * v.guardia * (0.4f + 0.6f * v.noche)) }
    }

    /** Desde dónde alumbra el pasillo por el portón: el primer tubo de la clave. */
    private val FUENTE_DEL_PORTON: V3 get() = V3(P.CENTRO_X + 0.2f, P.BOVEDA - 0.3f, P.TUBOS.first())

    /**
     * La luz del pasillo que entra por el portón: el hueco del arco llevado desde el primer tubo hasta el suelo de la sala,
     * con su borde blando. De día, el blanco frío; de noche, el azul de piscina; el día del sol falso, cálido.
     */
    private fun DrawScope.luzDelPorton(v: VistaRefugio) {
        val f = FUENTE_DEL_PORTON
        val arco = S.arcoDelPorton(P.BOCA).map { q -> val d = q - f; val t = (-f.y / d.y.coerceAtMost(-0.01f)).coerceAtMost(12f); f + d * t }
        val forma = K.forma(arco)
        val fuerza = (if (v.solFalso) 0.42f else if (v.dia) 0.2f else 0.22f) * (if (v.puerta == Puerta.ABIERTA && v.dia) 1.15f else 1f)
        val col = lerp(v.colorTubo, Color.White, if (v.dia) 0.2f else 0f)
        for (ancho in listOf(12f, 7f, 3f)) drawPath(forma, col.copy(alpha = 0.035f * fuerza / 0.3f), style = Stroke(ancho), blendMode = BlendMode.Plus)
        drawPath(forma, Brush.linearGradient(listOf(col.copy(alpha = fuerza), col.copy(alpha = fuerza * 0.5f), col.copy(alpha = 0f)), start = K.p(V3(P.CENTRO_X, 0f, S.FONDO)), end = K.p(V3(P.CENTRO_X, 0f, 2.4f))), blendMode = BlendMode.Plus)
    }

    /**
     * El charco de una lámpara de jaula: donde su cono toca el suelo (o la pared), una mancha cálida con el borde blando y,
     * dentro, los rayos de sombra de los seis alambres de la jaula, que salen del centro como los de una rueda.
     */
    private fun DrawScope.charcoDeLaLampara(v: VistaRefugio, b: V3, eje: V3) {
        val color = C.LAMPARA
        // Dónde toca el eje: el suelo, o la pared del fondo si llega antes.
        val tSuelo = if (eje.y < -0.05f) -b.y / eje.y else Float.MAX_VALUE
        val tPared = if (eje.z > 0.05f) (S.FONDO - b.z) / eje.z else Float.MAX_VALUE
        if (tPared < tSuelo) {
            val c = b + eje * tPared
            clipPath(paredDelFondo()) {
                val pc = K.p(c); val k = K.k(c)
                charco(pc, 1.25f * k, 1.0f * k, color, 0.42f)
                charco(pc + Offset(0f, 0.25f * k), 0.55f * k, 0.45f * k, lerp(color, Color.White, 0.3f), 0.35f)
                // Los rayos de sombra de la jaula en la pared, alrededor de donde toca el eje.
                for (j in 0 until 6) { val a = 2f * PI.toFloat() * j / 6 + 0.5f; val d = V3(cos(a), sin(a), 0f)
                    Trazo3.run { barra(c + d * 0.12f, c + d * 1.1f, 0.05f, lerp(C.SOMBRA, C.TINTA, 0.2f).copy(alpha = 0.22f), StrokeCap.Butt) } }
            }
            // Y su derrame en el suelo, al pie de la pared.
            clipPath(sueloDeLaSala()) { val q = V3(c.x, 0f, S.FONDO - 0.9f); charco(K.p(q), 1.6f * K.k(q), 0.45f * K.k(q), color, 0.3f) }
            return
        }
        val c = b + eje * tSuelo
        val radio = b.y * 0.85f
        clipPath(sueloDeLaSala()) {
            val borde = K.circulo(c, K.ARRIBA, radio, 28)
            val forma = K.forma(borde)
            for (ancho in listOf(14f, 8f, 4f)) drawPath(forma, color.copy(alpha = 0.045f), style = Stroke(ancho), blendMode = BlendMode.Plus)
            val pc = K.p(c); val dentro = K.p(c + V3(radio, 0f, 0f))
            drawPath(forma, Brush.radialGradient(listOf(lerp(color, Color.White, 0.3f).copy(alpha = 0.5f), color.copy(alpha = 0.3f), color.copy(alpha = 0.12f)), pc, (dentro - pc).getDistance().coerceAtLeast(4f) * 1.3f), blendMode = BlendMode.Plus)
            reja(c, radio, 0.22f)
        }
    }

    /**
     * El charco de la lámpara en la tapa de la bancada (de [x0] a [x1], de [z0] a [z1], a la altura [y]): donde su cono la
     * toca, la madera se enciende, con la reja de sombra de los alambres de la jaula. Va con la tapa, antes de lo de encima.
     */
    fun DrawScope.charcoEnLaBancada(v: VistaRefugio, x0: Float, x1: Float, z0: Float, z1: Float, y: Float) {
        val tapa = K.forma(listOf(V3(x0, y, z0), V3(x1, y, z0), V3(x1, y, z1), V3(x0, y, z1)))
        for (o in v.obras) {
            val b = S.bombilla(o); val e = S.haciaDonde(o); if (e.y > -0.3f) continue
            val t = (y - b.y) / e.y; val c = b + e * t
            val alto = b.y - y; val radio = alto * 0.95f
            clipPath(tapa) {
                val pc = K.p(c); val borde = K.circulo(c, K.ARRIBA, radio, 26); val forma = K.forma(borde)
                for (ancho in listOf(10f, 5f)) drawPath(forma, C.LAMPARA.copy(alpha = 0.05f), style = Stroke(ancho), blendMode = BlendMode.Plus)
                drawPath(forma, Brush.radialGradient(listOf(lerp(C.LAMPARA, Color.White, 0.35f).copy(alpha = 0.42f), C.LAMPARA.copy(alpha = 0.22f), C.LAMPARA.copy(alpha = 0.06f)), pc, ((K.p(c + V3(radio, 0f, 0f)) - pc).getDistance() * 1.2f).coerceAtLeast(4f)), blendMode = BlendMode.Plus)
                reja(c, radio, 0.3f)
            }
        }
    }

    /** La reja de sombra de los seis alambres de la jaula: rayos que salen de [c] (en el plano del suelo o de una tapa), hasta [radio]. */
    private fun DrawScope.reja(c: V3, radio: Float, fuerza: Float) {
        for (k in 0 until 6) {
            val a = 2f * PI.toFloat() * k / 6 + 0.3f
            val d = V3(cos(a), 0f, sin(a))
            val p0 = K.p(c + d * (radio * 0.1f)); val p1 = K.p(c + d * (radio * 1.05f))
            val camino = Path().apply { val w0 = 0.012f * K.k(c); val w1 = 0.07f * K.k(c + d * radio); val nx = -(p1.y - p0.y); val ny = p1.x - p0.x; val l = sqrt(nx * nx + ny * ny).coerceAtLeast(0.01f)
                moveTo(p0.x + nx / l * w0, p0.y + ny / l * w0); lineTo(p1.x + nx / l * w1, p1.y + ny / l * w1); lineTo(p1.x - nx / l * w1, p1.y - ny / l * w1); lineTo(p0.x - nx / l * w0, p0.y - ny / l * w0); close() }
            drawPath(camino, lerp(C.SOMBRA, C.TINTA, 0.2f).copy(alpha = fuerza))
        }
        // El aro de la jaula: un anillo de sombra más tenue.
        val anillo = K.circulo(c, K.ARRIBA, radio * 0.62f, 24).map { K.p(it) }
        val camino = Path().apply { moveTo(anillo[0].x, anillo[0].y); for (i in 1 until anillo.size) lineTo(anillo[i].x, anillo[i].y) }
        drawPath(camino, lerp(C.SOMBRA, C.TINTA, 0.2f).copy(alpha = fuerza * 0.6f), style = Stroke(0.04f * K.k(c)))
    }

    // =================================================================
    //  Las sombras
    // =================================================================

    /** El punto [p] llevado desde la luz [desde] hasta el suelo. */
    private fun alSuelo(p: V3, desde: V3): V3 {
        val y = p.y.coerceAtMost(desde.y - 0.15f); val k = (desde.y / (desde.y - y)).coerceAtMost(8f)
        return V3(desde.x + (p.x - desde.x) * k, 0f, desde.z + (p.z - desde.z) * k)
    }

    /**
     * Las sombras de las cosas en el suelo, en una sola capa: cada lámpara de trabajo tumba las de lo que tiene cerca, lejos
     * de ella y abiertas en abanico; la luz que entra por el portón tumba las suyas hacia los ojos, más suaves; de noche, la
     * lucecita de guardia, las de lo que está junto a la pared. Debajo de cada cosa, su sombra de contacto.
     */
    fun DrawScope.sombras(v: VistaRefugio, cosas: List<CosaRefugio>) {
        val suelo = sueloDeLaSala()
        val conCuerpo = cosas.filter { it.esqueleto.size >= 3 }
        // La de contacto, siempre.
        clipPath(suelo) {
            for (k in conCuerpo) { val pies = k.esqueleto.filter { it.y < 0.25f }; if (pies.size < 3) continue
                drawPath(poligono(K.casco(pies.map { K.p(it) })), C.SOMBRA.copy(alpha = 0.34f), blendMode = BlendMode.Multiply)
                drawPath(poligono(K.casco(pies.map { K.p(it) })), C.SOMBRA.copy(alpha = 0.2f), style = Stroke(3f), blendMode = BlendMode.Multiply) }
        }
        val tumban = v.tumban
        if (tumban.isEmpty()) return
        capaSombra(rayoFijo(-0.5f, 45f, 0.8f, C.SOMBRA)) { c ->
            clipPath(suelo) {
                for (f in tumban) for (k in conCuerpo) {
                    val centro = K.centro(k.esqueleto)
                    if (k.esqueleto.maxOf { it.y } >= f.p.y - 0.1f) continue
                    val d = K.largo(f.p - centro)
                    var alfa = f.fuerza / (1f + d * d / (f.alcance * f.alcance))
                    f.cono?.let { e -> val dd = centro - f.p; val cs = K.punto(K.unidad(dd), e); alfa *= ((cs - f.coseFuera) / (f.coseDentro - f.coseFuera)).coerceIn(0f, 1f) }
                    alfa = (alfa * 1.1f).coerceIn(0f, 0.9f)
                    if (alfa < 0.05f) continue
                    drawPath(poligono(K.casco(k.esqueleto.map { K.p(alSuelo(it, f.p)) })), c.copy(alpha = alfa))
                }
            }
        }
    }

    // =================================================================
    //  El aire y la penumbra
    // =================================================================

    /**
     * La luz en el aire: el cono de cada lámpara encendida, con su polvo quieto; el haz frío (o azul) que entra por el portón;
     * y el resplandor de la baliza si suena la alarma.
     */
    fun DrawScope.enElAire(v: VistaRefugio) {
        for (o in v.obras) {
            val b = S.bombilla(o); val e = S.haciaDonde(o)
            val tSuelo = if (e.y < -0.05f) -b.y / e.y else 9f
            val tPared = if (e.z > 0.05f) (S.FONDO - b.z) / e.z else 9f
            val t = minOf(tSuelo, tPared)
            val fin = b + e * t
            val radio = t * 0.75f
            val u = K.unidad(K.cruz(e, if (abs(e.y) > 0.9f) K.FRENTE else K.ARRIBA))
            val boca = b + e * 0.05f
            val cono = listOf(boca - u * 0.13f, boca + u * 0.13f, fin + u * radio, fin - u * radio)
            val forma = K.forma(cono)
            drawPath(forma, Brush.linearGradient(listOf(C.LAMPARA.copy(alpha = 0.12f), C.LAMPARA.copy(alpha = 0.02f)), start = K.p(b), end = K.p(fin)), blendMode = BlendMode.Plus)
            // El halo de la bombilla.
            val pb = K.p(b); val kb = K.k(b)
            drawCircle(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.35f), C.LAMPARA.copy(alpha = 0.08f), Color.Transparent), pb, 0.9f * kb), 0.9f * kb, pb, blendMode = BlendMode.Plus)
            // El polvo quieto en el cono.
            val az = Azar(Semilla.de("refugio", "polvo", o.ordinal))
            repeat(26) { val k = az.entre(0.1f, 0.95f); val lado = az.entre(-0.8f, 0.8f); val q = b + (fin - b) * k + u * (lado * radio * k); drawCircle(lerp(C.LAMPARA, Color.White, 0.5f).copy(alpha = 0.45f * az.real()), az.entre(0.3f, 0.7f), K.p(q)) }
        }
        // El haz del portón: el aire alumbrado entre el hueco y su mancha en el suelo.
        val f = FUENTE_DEL_PORTON
        val arriba = S.arcoDelPorton(S.FONDO).filter { it.y > S.ARRANQUE - 0.1f }
        if (arriba.size >= 2) {
            val a = arriba.first(); val b = arriba.last()
            fun alSueloDe(q: V3): V3 { val d = q - f; val t = (-f.y / d.y.coerceAtMost(-0.01f)).coerceAtMost(12f); return f + d * t }
            val haz = K.forma(listOf(a, b, alSueloDe(b), alSueloDe(a)))
            drawPath(haz, Brush.linearGradient(listOf(v.colorTubo.copy(alpha = if (v.dia) 0.06f else 0.08f), Color.Transparent), start = K.p(K.medio(a, b)), end = K.p(alSueloDe(K.medio(a, b)))), blendMode = BlendMode.Plus)
        }
        if (v.alarma) { val b = K.p(S.BALIZA); drawCircle(Brush.radialGradient(listOf(C.ALARMA.copy(alpha = 0.3f), Color.Transparent), b, 40f), 40f, b, blendMode = BlendMode.Plus) }
    }

    /**
     * La penumbra: los rincones de la sala (el techo de la izquierda, las esquinas de abajo) más hondos, en verde azulado y
     * violeta, nunca en gris; de noche, más. Así lo encendido destaca.
     */
    fun DrawScope.penumbra(v: VistaRefugio) {
        val k = 0.28f + 0.2f * v.noche
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to C.PENUMBRA.copy(alpha = k), center = Offset(250f, 150f), radius = 290f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
        drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.3f + 0.15f * v.noche), 1f to Color.Transparent, startY = 0f, endY = 70f), Offset.Zero, Size(ANCHO, 70f), blendMode = BlendMode.Multiply)
    }

    // =================================================================
    //  Lo que se mueve
    // =================================================================

    /**
     * Lo que se mueve: las burbujas de los tanques tras el ventanal, la gota de la junta de la bomba grande (con su anillo en
     * el cubo), el parpadeo del piloto verde, el polvo que flota en el cono de la lámpara; y según el día, la baliza que gira,
     * la luz de la compuerta que late, las lucecitas del árbol, el fuego de fuera, la lluvia por la Puerta, el pájaro.
     */
    fun DrawScope.vivo(v: VistaRefugio, reloj: Float) {
        // Las burbujas de los tanques.
        val ventanal = K.forma(listOf(V3(P.IZQ, P.VENTANAL_Y0, P.VENTANAL_Z0), V3(P.IZQ, P.VENTANAL_Y0, P.VENTANAL_Z1), V3(P.IZQ, P.VENTANAL_Y1, P.VENTANAL_Z1), V3(P.IZQ, P.VENTANAL_Y1, P.VENTANAL_Z0)))
        clipPath(ventanal) {
            val az = Azar(Semilla.de("refugio", "burbujas"))
            repeat(16) {
                val z = az.entre(P.VENTANAL_Z0 + 0.3f, P.VENTANAL_Z1 - 0.3f); val fase = ((reloj * az.entre(0.08f, 0.16f) + az.real()) % 1f + 1f) % 1f
                val q = V3(P.IZQ - 0.9f, P.VENTANAL_Y0 + (P.VENTANAL_Y1 - P.VENTANAL_Y0) * fase, z)
                drawCircle(lerp(C.VIOLETA, Color.White, 0.6f).copy(alpha = 0.55f), az.entre(0.3f, 0.6f), K.p(q) + Offset(0.4f * sin(reloj * 1.3f + it), 0f))
            }
        }
        // La gota de la junta de la bomba grande, y su anillo en el cubo.
        val gota0 = V3(S.BOMBA.x + 0.53f, 1.0f, 3.14f); val gota1 = PiezasSala.CUBO + V3(0f, 0.29f, 0f)
        val fase = (reloj / 2.6f) % 1f
        if (fase < 0.55f) {
            val k = (fase / 0.55f).let { it * it }
            val q = gota0 + (gota1 - gota0) * k + V3((gota1.x - gota0.x) * 0f, 0f, 0f)
            drawCircle(lerp(C.PISCINA, Color.White, 0.6f).copy(alpha = 0.85f), 0.018f * K.k(q), K.p(q))
        } else {
            val k = (fase - 0.55f) / 0.45f
            val anillo = K.circulo(gota1, K.ARRIBA, 0.03f + 0.12f * k, 14).map { K.p(it) }
            val camino = Path().apply { moveTo(anillo[0].x, anillo[0].y); for (i in 1 until anillo.size) lineTo(anillo[i].x, anillo[i].y) }
            drawPath(camino, lerp(C.PISCINA, Color.White, 0.6f).copy(alpha = 0.5f * (1f - k)), style = Stroke(0.5f))
        }
        // El piloto verde del cuadro de palancas, que parpadea.
        if (sin(reloj * 2.2f) > 0.6f) { val w = V3(S.ARMARIO_X0 + 0.25f, 1.62f, S.FONDO - 0.44f); drawCircle(Color.White.copy(alpha = 0.6f), 0.02f * K.k(w), K.p(w)) }
        // El polvo que flota en el cono de la lámpara.
        for (o in v.obras) {
            val b = S.bombilla(o); val e = S.haciaDonde(o); val az = Azar(Semilla.de("refugio", "motas", o.ordinal))
            repeat(10) {
                val k = ((az.real() + reloj * az.entre(0.01f, 0.03f)) % 1f) * 0.8f + 0.1f
                val q = b + e * (1.8f * k) + V3(0.6f * k * sin(reloj * 0.3f + it * 1.7f), 0f, 0.4f * k * cos(reloj * 0.23f + it))
                drawCircle(lerp(C.LAMPARA, Color.White, 0.6f).copy(alpha = 0.6f), 0.5f, K.p(q))
            }
        }
        // La baliza de la alarma, que gira.
        if (v.alarma) {
            val b = K.p(S.BALIZA); val giro = (reloj * 3.2f) % (2f * PI.toFloat())
            val haz = Path().apply { moveTo(b.x, b.y); lineTo(b.x + cos(giro - 0.25f) * 160f, b.y + sin(giro - 0.25f) * 50f); lineTo(b.x + cos(giro + 0.25f) * 160f, b.y + sin(giro + 0.25f) * 50f); close() }
            drawPath(haz, Brush.radialGradient(listOf(C.ALARMA.copy(alpha = 0.22f), Color.Transparent), b, 160f), blendMode = BlendMode.Plus)
            drawCircle(lerp(C.ALARMA, Color.White, 0.4f).copy(alpha = 0.5f + 0.5f * sin(reloj * 6f).coerceAtLeast(0f)), 2.2f, b)
        }
        // La luz de la compuerta bajada, que late.
        if (v.compuerta == Compuerta.BAJADA) { val p = K.p(P.LUZ_COMPUERTA); val late = 0.5f + 0.5f * sin(reloj * 4f); drawCircle(Brush.radialGradient(listOf(C.ALARMA.copy(alpha = 0.4f * late), Color.Transparent), p, 12f), 12f, p, blendMode = BlendMode.Plus) }
        // Las lucecitas del árbol de Nochebuena, que titilan.
        if (R.lucesDelArbol(v.m)) {
            val az = Azar(Semilla.de("refugio", "lucecitas"))
            repeat(14) { val q = P.ARBOL + V3(az.entre(-0.45f, 0.45f), az.entre(0.3f, 1.6f), az.entre(-0.3f, 0.3f)); val brillo = 0.5f + 0.5f * sin(reloj * az.entre(1.5f, 3.5f) + it)
                drawCircle(listOf(C.GUARDIA, C.ROJO, C.AMARILLO, C.PISCINA)[it % 4].copy(alpha = 0.4f + 0.6f * brillo), 0.7f, K.p(q)) }
        }
        val c = K.p(P.PUERTA_C)
        // El fuego de fuera, que tiembla.
        if (v.fuego > 0.02f) {
            val pf = Offset(c.x + 3f, K.CY + 10f); val late = 0.75f + 0.25f * sin(reloj * 7f) * sin(reloj * 3.1f + 1f)
            drawCircle(Brush.radialGradient(listOf(C.FUEGO.copy(alpha = 0.6f * v.fuego * late), Color.Transparent), pf, 5f), 5f, pf, blendMode = BlendMode.Plus)
            repeat(4) { val s = ((reloj * 0.6f + it * 0.27f) % 1f); drawCircle(lerp(C.FUEGO, Color.White, 0.5f).copy(alpha = (1f - s) * v.fuego), 0.4f, pf + Offset(sin(reloj + it) * 2f, -s * 9f)) }
        }
        // La lluvia por el hueco de la Puerta.
        if (v.puerta == Puerta.ABIERTA && R.climaDeFuera(v.m) == Clima.LLUVIA) {
            val agujero = K.forma(K.circulo(V3(P.PUERTA_C.x, P.PUERTA_C.y, P.PUERTA_Z + 0.05f), K.FRENTE, P.PUERTA_R, 30))
            clipPath(agujero) { val az = Azar(Semilla.de("refugio", "lluvia")); repeat(30) { val x = c.x + az.entre(-18f, 18f); val y = c.y - 18f + ((az.real() * 36f + reloj * 60f) % 36f); drawLine(Color(0xFFC8D0D8).copy(alpha = 0.5f), Offset(x, y), Offset(x - 0.6f, y + 3f), 0.35f) } }
        }
        // El pájaro posado en el tubo gordo, junto al portón, que de vez en cuando salta.
        if (R.pajaro(v.m)) {
            val salto = if ((reloj % 7f) > 6.4f) 0.25f else 0f
            val q = V3(-1.45f, 2.7f + salto * 0.3f, 6.9f - salto); val p = K.p(q); val k = K.k(q)
            drawOval(lerp(C.TINTA, C.COBRE, 0.3f), Offset(p.x - 0.09f * k, p.y - 0.05f * k), Size(0.18f * k, 0.09f * k))
            drawCircle(lerp(C.TINTA, C.COBRE, 0.3f), 0.035f * k, p + Offset(0.08f * k, -0.05f * k))
            drawLine(lerp(C.TINTA, C.COBRE, 0.2f), p + Offset(-0.08f * k, 0f), p + Offset(-0.16f * k, 0.03f * k), 0.03f * k)
        }
    }
}
