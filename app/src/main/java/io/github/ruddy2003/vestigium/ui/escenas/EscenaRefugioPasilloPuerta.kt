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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La Puerta, al fondo del pasillo grande: «un círculo de acero de cuatro metros, con un número pintado y una rueda en el
 * centro como la de una caja fuerte. Hay una línea amarilla en el suelo que nadie pisa». Cerrada, entornada (con el filo de
 * luz de fuera) o abierta (la hoja girada hacia el pasillo); y, por su hueco, lo de fuera: el cielo de la hora, la ladera,
 * el valle con el arroyo y las ruinas, las montañas; de noche, las estrellas y el fuego. Sin número: la placa, en blanco.
 */
internal object PuertaRefugio {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio
    private val P = PasilloRefugio

    /** El muro del fondo, la Puerta (cerrada, entornada o abierta) y, si está abierta, lo de fuera. */
    fun DrawScope.fondo(v: VistaRefugio) {
        val z = P.PUERTA_Z
        // El muro de hormigón del fondo, manchado de humedad, con el marco de acero de la Puerta.
        val muro = P.SECCION.map { V3(it.first, it.second, z) }
        Trazo3.run { caraDegradada(v, muro, K.FRENTE, lerp(C.HORMIGON, C.ROCA, 0.45f), V3(P.CENTRO_X, P.BOVEDA, z), V3(P.CENTRO_X, 0f, z), true, 3) }
        val c = P.PUERTA_C
        Trazo3.run {
            disco(v, c + K.FRENTE * 0.02f, K.FRENTE, P.PUERTA_R + 0.4f, lerp(C.ACERO, C.TINTA, 0.35f), true, 32)
            aro(c + K.FRENTE * 0.03f, K.FRENTE, P.PUERTA_R + 0.22f, 0.07f, v.c(c, K.FRENTE, lerp(C.ACERO, C.OXIDO, 0.25f), true), 32)
        }
        for (i in 0 until 16) { val a = 2f * PI.toFloat() * i / 16; val w = V3(c.x + cos(a) * (P.PUERTA_R + 0.31f), c.y + sin(a) * (P.PUERTA_R + 0.31f), z - 0.04f); drawCircle(v.c(w, K.FRENTE, C.LATON, true), 0.05f * K.k(w), K.p(w)) }
        // Las chorreras de óxido que bajan del marco.
        for (k in 0..4) { val x = c.x - 1.6f + 0.8f * k; val y0 = c.y - (P.PUERTA_R + 0.1f) * (1f - 0.15f * abs(k - 2)); Trazo3.run { barra(V3(x, y0, z - 0.03f), V3(x + 0.02f, y0 - 0.5f - 0.1f * k, z - 0.03f), 0.06f, v.c(V3(x, y0, z), K.FRENTE, C.OXIDO, true).copy(alpha = 0.45f)) } }
        when (v.puerta) {
            Puerta.CERRADA -> hoja(v, 0f)
            Puerta.ENTORNADA -> { fuera(v); hoja(v, 9f) }
            Puerta.ABIERTA -> { fuera(v); hoja(v, 80f) }
        }
        // La línea amarilla que nadie pisa, gastada en los bordes.
        val l0 = P.LINEA_Z
        Trazo3.run { cara(v, listOf(V3(P.IZQ + 0.15f, 0.003f, l0), V3(P.DER - 0.15f, 0.003f, l0), V3(P.DER - 0.15f, 0.003f, l0 + 0.2f), V3(P.IZQ + 0.15f, 0.003f, l0 + 0.2f)), K.ARRIBA, C.AMARILLO, true) }
    }

    /** El giro de un punto de la hoja alrededor del gozne (en el canto de la derecha), hacia el pasillo. */
    private fun gira(p: V3, gozne: V3, g: Float): V3 { val dx = p.x - gozne.x; val dz = p.z - gozne.z; return V3(gozne.x + dx * cos(g) - dz * sin(g), p.y, gozne.z + dx * sin(g) + dz * cos(g)) }

    /**
     * La hoja de la Puerta, girada [giro] grados sobre su gozne (a la derecha, hacia el pasillo): un disco de acero grueso
     * con sus anillos, sus pasadores en el canto y, en el centro, la rueda «como la de una caja fuerte». Sin número.
     */
    private fun DrawScope.hoja(v: VistaRefugio, giro: Float) {
        val c = P.PUERTA_C; val r = P.PUERTA_R
        val g = Math.toRadians(giro.toDouble()).toFloat()
        val gozne = V3(c.x + r, c.y, P.PUERTA_Z - 0.05f)
        val n0 = K.unidad(V3(sin(g), 0f, -cos(g)))
        val cc = gira(V3(c.x, c.y, P.PUERTA_Z - 0.05f), gozne, g)
        val n = if (K.seVe(cc, n0)) n0 else n0 * -1f
        if (giro > 1f) {
            val borde = K.circulo(cc, n, r, 30); val atras = borde.map { it - n * 0.55f }
            drawPath(poligono(K.casco((borde + atras).map { K.p(it) })), v.c(cc, K.IZQ, lerp(C.ACERO, C.TINTA, 0.35f), true))
        }
        Trazo3.run {
            disco(v, cc, n, r, lerp(C.ACERO, C.ROCA, 0.15f), true, 36)
            aro(cc + n * 0.01f, n, r * 0.86f, 0.05f, v.c(cc, n, lerp(C.ACERO, C.TINTA, 0.4f), true), 32)
            aro(cc + n * 0.01f, n, r * 0.62f, 0.035f, v.c(cc, n, lerp(C.ACERO, C.TINTA, 0.35f), true), 28)
            aro(cc + n * 0.015f, n, r * 0.97f, 0.04f, v.c(cc, n, lerp(C.ACERO, Color.White, 0.2f), true), 36)
        }
        // Las manchas de óxido de la hoja y la placa pintada donde iría el número: una franja amarilla gastada, sin cifras.
        val u = K.unidad(K.cruz(n, K.ARRIBA))
        for (k in 0..3) { val a = 0.6f + 1.4f * k; val q = cc + (u * cos(a) + K.ARRIBA * sin(a)) * (r * 0.75f) + n * 0.012f; Trazo3.run { disco(v, q, n, 0.18f + 0.05f * k, C.OXIDO, true, 9) } }
        val pl = cc + K.ARRIBA * (r * 0.74f)
        Trazo3.run { cara(v, listOf(pl - u * 0.42f - K.ARRIBA * 0.12f, pl + u * 0.42f - K.ARRIBA * 0.12f, pl + u * 0.42f + K.ARRIBA * 0.12f, pl - u * 0.42f + K.ARRIBA * 0.12f).map { it + n * 0.02f }, n, lerp(C.AMARILLO, C.ACERO, 0.35f), true) }
        // La rueda del centro: el aro, seis radios y el cubo.
        val rc = cc + n * 0.18f
        Trazo3.run {
            barra(cc, rc, 0.1f, v.c(cc, K.NADA, C.HIERRO, true))
            for (i in 0 until 6) { val a = PI.toFloat() * i / 3f; val d = u * cos(a) + K.ARRIBA * sin(a); barra(rc, rc + d * 0.5f, 0.05f, v.c(rc, n, lerp(C.ACERO, Color.White, 0.15f), true)); disco(v, rc + d * 0.56f, n, 0.06f, C.ACERO, true, 8) }
            aro(rc, n, 0.5f, 0.06f, v.c(rc, n, lerp(C.ACERO, Color.White, 0.2f), true), 24)
            disco(v, rc + n * 0.02f, n, 0.12f, C.HIERRO, true, 12)
        }
        for (i in 0 until 12) { val a = 2f * PI.toFloat() * i / 12 + 0.26f; val d = u * cos(a) + K.ARRIBA * sin(a); val w = cc + d * (r * 0.92f) + n * 0.02f; drawCircle(v.c(w, n, lerp(C.ACERO, C.TINTA, 0.4f), true), 0.07f * K.k(w), K.p(w)) }
        if (giro in 1f..30f) rendija(v)
    }

    /** Entornada: el filo de luz de fuera por la rendija de la izquierda. */
    private fun DrawScope.rendija(v: VistaRefugio) {
        val f = v.fuera ?: return
        val luz = lerp(lerp(f.medio, Color.White, 0.5f), f.bajo, 0.3f).copy(alpha = (0.5f + 0.5f * (1f - f.noche)).coerceIn(0f, 1f))
        val arco = K.circulo(V3(P.PUERTA_C.x, P.PUERTA_C.y, P.PUERTA_Z), K.FRENTE, P.PUERTA_R * 0.99f, 20, (PI * 0.55).toFloat(), (PI * 1.45).toFloat()).map { K.p(it) }
        val camino = Path().apply { moveTo(arco[0].x, arco[0].y); for (i in 1 until arco.size) lineTo(arco[i].x, arco[i].y) }
        drawPath(camino, luz, style = Stroke(1.3f, cap = StrokeCap.Round))
        drawPath(camino, luz.copy(alpha = 0.25f), style = Stroke(4f, cap = StrokeCap.Round), blendMode = BlendMode.Plus)
    }

    /**
     * Lo de fuera, por el hueco de la Puerta: el cielo de la hora, las montañas en dos filas con su bruma, el valle con el
     * arroyo y las ruinas del pueblo, la ladera con un árbol torcido y piedras; de noche, las estrellas y el fuego. Poco
     * detalle, pero en capas (regla 10). Con lluvia, la cortina gris.
     */
    private fun DrawScope.fuera(v: VistaRefugio) {
        val f = v.fuera ?: return
        val agujero = K.forma(K.circulo(V3(P.PUERTA_C.x, P.PUERTA_C.y, P.PUERTA_Z + 0.05f), K.FRENTE, P.PUERTA_R, 36))
        clipPath(agujero) {
            val h = K.CY; val c = K.p(P.PUERTA_C)
            cielo(f, h)
            val az = Azar(Semilla.de(v.m.ficha.semilla, "fuera"))
            if (f.estrellas > 0.05f) repeat(26) { drawCircle(Color(0xFFF6F2E6).copy(alpha = (f.estrellas * az.entre(0.35f, 1f)).coerceIn(0f, 1f)), az.entre(0.25f, 0.55f), Offset(c.x + az.entre(-18f, 18f), c.y + az.entre(-18f, -1f))) }
            v.sol?.let { s -> if (s.elevacion < 22f && abs(s.azimut) < 35f) { val ps = Offset(c.x + s.azimut * 0.45f, h - s.elevacion * 0.8f)
                drawCircle(Brush.radialGradient(listOf(f.solColor.copy(alpha = 0.7f), Color.Transparent), ps, 12f), 12f, ps, blendMode = BlendMode.Screen); drawCircle(lerp(f.solColor, Color.White, 0.6f), 2.2f, ps) } }
            if (f.nublado > 0.2f || az.real() < 0.5f) for (k in 0..1) nube(f, c.x - 24f + 22f * k + az.entre(-4f, 4f), c.y - 6f - 6f * k, 14f + 6f * az.real(), az, carga = f.nublado, lejos = 0.6f)
            paisaje(v, f, c, h, az)
            if (v.fuego > 0.02f) { val pf = Offset(c.x + 3f, h + 11f)
                drawCircle(Brush.radialGradient(listOf(C.FUEGO.copy(alpha = 0.7f * v.fuego), Color.Transparent), pf, 20f), 20f, pf, blendMode = BlendMode.Plus)
                drawCircle(lerp(C.FUEGO, Color.White, 0.4f).copy(alpha = v.fuego), 2.4f, pf) }
            if (R.climaDeFuera(v.m) == Clima.LLUVIA) drawRect(Color(0xFFA8B0B8).copy(alpha = 0.28f), Offset(c.x - 26f, c.y - 26f), Size(52f, 52f))
        }
        Trazo3.run { aro(V3(P.PUERTA_C.x, P.PUERTA_C.y, P.PUERTA_Z + 0.02f), K.FRENTE, P.PUERTA_R, 0.08f, lerp(C.TINTA, C.ACERO, 0.3f), 36) }
    }

    /** Las montañas, el valle con el arroyo y las ruinas, la ladera con piedras y el árbol torcido. */
    private fun DrawScope.paisaje(v: VistaRefugio, f: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces, c: Offset, h: Float, az: Azar) {
        monte(f, perfil(Azar(Semilla.de("refugio", "montes", 1)), c.x - 24f, c.x + 24f, h + 0.5f, 7f, 0.5f, 16), h + 6f, Color(0xFF6C7A8C), 0.85f)
        monte(f, perfil(Azar(Semilla.de("refugio", "montes", 2)), c.x - 24f, c.x + 24f, h + 3f, 5f, 0.6f, 14), h + 8f, Color(0xFF5E6E5A), 0.55f)
        drawRect(f.tono(Color(0xFF6C7A4E), 0.4f), Offset(c.x - 24f, h + 3f), Size(48f, 6f))
        drawLine(lerp(f.tono(Color(0xFF9CB4C0), 0.3f), lerp(f.bajo, Color.White, 0.3f), 0.6f), Offset(c.x - 14f, h + 6.5f), Offset(c.x + 10f, h + 5f), 0.6f)
        for (k in 0..4) { val x = c.x + 4f + k * 2.6f; val alto = az.entre(1f, 2.6f); drawRect(f.tono(Color(0xFFB8A888), 0.35f), Offset(x, h + 4.5f - alto), Size(1.6f, alto)); drawRect(f.tono(Color(0xFF4A4438), 0.35f), Offset(x + 1.1f, h + 4.5f - alto), Size(0.5f, alto)) }
        val ladera = Path().apply { moveTo(c.x - 26f, h + 9f); cubicTo(c.x - 10f, h + 7f, c.x + 6f, h + 10f, c.x + 26f, h + 8f); lineTo(c.x + 26f, h + 30f); lineTo(c.x - 26f, h + 30f); close() }
        drawPath(ladera, f.tono(Color(0xFF7E8A4E), 0.1f))
        repeat(6) { val p = Offset(c.x + az.entre(-16f, 16f), h + az.entre(9.5f, 13f)); drawCircle(f.tono(Color(0xFF9A9480), 0.05f), az.entre(0.5f, 1.1f), p) }
        val t0 = Offset(c.x - 9f, h + 10f); val tronco = f.tono(Color(0xFF3E3428), 0f)
        drawLine(tronco, t0, t0 + Offset(2f, -6f), 0.9f, StrokeCap.Round); drawLine(tronco, t0 + Offset(2f, -6f), t0 + Offset(-1.5f, -10f), 0.6f, StrokeCap.Round); drawLine(tronco, t0 + Offset(2f, -6f), t0 + Offset(5f, -8.5f), 0.5f, StrokeCap.Round)
        mancha(t0 + Offset(-1f, -10.5f), 3f, 1.8f, f.tono(Color(0xFF4E6038), 0f), 0.9f); mancha(t0 + Offset(5f, -9f), 2.4f, 1.5f, f.tono(Color(0xFF4E6038), 0f), 0.9f)
    }
}
