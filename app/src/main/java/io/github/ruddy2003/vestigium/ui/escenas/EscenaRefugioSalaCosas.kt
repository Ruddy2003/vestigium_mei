package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Obra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Las cosas de la sala de bombas, cada una con su luz, su materia y su uso: la bomba grande con su rueda roja, la fila de
 * bombas viejas, el filtro grande, la bancada y lo de encima, la pared de manómetros, la columna de nivel, el cuadro de
 * palancas, los carteles, los cubos, la manguera, la caja de herramientas, la lámpara de jaula; y lo que dejan los
 * estados y los días del cuento. Todo en metros, en la sala de [SalaRefugio].
 */
internal object PiezasSala {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio
    private val S = SalaRefugio

    // ---------------------------------------------------------------- Sitios que el gato necesita saber

    /** La pata de delante de la bancada, en el suelo del gato. */
    val PATA_BANCADA_X: Float get() = K.p(V3(S.BANCADA_X0 + 0.06f, 0f, S.BANCADA_Z0 + 0.06f)).x
    /** Dónde deja ella las piezas de la bomba tres el día que la desmonta: en el suelo despejado, junto a la bancada. */
    val PIEZAS = V3(0.15f, 0f, 3.75f)
    /** El cubo de la gotera de la bomba grande. */
    val CUBO = V3(-2.3f, 0f, 3.65f)
    val CUBO_X: Float get() = K.p(CUBO).x.coerceAtLeast(40f)
    /** Encima de la voluta de la bomba grande, a la izquierda de la rueda roja: el sitio alto del gato. */
    val MIRADOR: Offset get() = K.p(V3(-3.45f, 1.34f, 3.2f))
    /** Debajo de la bancada. */
    val ABRIGO_X: Float get() = K.p(V3(-1.0f, 0f, S.BANCADA_Z1)).x
    /** La guirnalda o el árbol: el adorno que el gato mira. */
    val ADORNO_X: Float get() = 300f

    // ---------------------------------------------------------------- Lo de la pared del fondo

    /**
     * Lo de la pared: los carteles, el cuadro de palancas, la pared de manómetros con sus tubitos, la columna de nivel de
     * la cisterna, la lucecita de guardia, la baliza; en el quicio, el interruptor, el extintor, las linternas y el gancho.
     */
    fun DrawScope.enLaPared(v: VistaRefugio) {
        cartel(v, -8.4f, -7.55f, 1.45f, 2.55f, 0)
        cartel(v, 1.12f, 1.62f, 1.95f, 2.55f, 1)
        cartel(v, -9.8f, -9.0f, 1.25f, 2.2f, 2)
        armario(v)
        tablero(v)
        columna(v)
        // La lucecita de guardia: una jaulita con su bombilla ámbar.
        val g = S.GUARDIA
        Trazo3.run {
            disco(v, g + K.FRENTE * 0.02f, K.FRENTE, 0.11f, C.HIERRO, lados = 12)
            disco(v, g + K.FRENTE * 0.08f, K.FRENTE, 0.07f, lerp(C.GUARDIA, Color.White, 0.3f * v.guardia), lados = 12, luz = false)
        }
        drawCircle(Brush.radialGradient(listOf(C.GUARDIA.copy(alpha = 0.55f * v.guardia), Color.Transparent), K.p(g), 0.5f * K.k(g)), 0.5f * K.k(g), K.p(g), blendMode = BlendMode.Plus)
        // La baliza de la alarma, apagada o encendida (su giro va en la parte viva).
        val b = S.BALIZA
        Trazo3.run {
            cilindro(v, b + V3(0f, -0.08f, 0f), b + V3(0f, 0.08f, 0f), 0.09f, if (v.alarma) lerp(C.ALARMA, Color.White, 0.25f) else lerp(C.ROJO, C.TINTA, 0.35f), lados = 10)
            caja(v, b.x - 0.1f, b.x + 0.1f, b.y - 0.13f, b.y - 0.08f, b.z - 0.08f, b.z + 0.1f, C.HIERRO)
        }
        quicio(v)
    }

    /**
     * Un cartel de antes de la guerra: papel amarillo, roto por una esquina, con una forma de color y sin letras ni figuras
     * que se reconozcan ([cual]: un sol sobre unas franjas, una gota grande, unos círculos).
     */
    private fun DrawScope.cartel(v: VistaRefugio, x0: Float, x1: Float, y0: Float, y1: Float, cual: Int) {
        val z = S.FONDO - 0.012f; val n = K.FRENTE
        val roto = 0.12f
        val papel = listOf(V3(x0, y0, z), V3(x1, y0, z), V3(x1, y1 - roto, z), V3(x1 - roto * 0.8f, y1, z), V3(x0, y1, z))
        Trazo3.run { cara(v, papel, n, lerp(C.PAPEL, C.AMARILLO, 0.18f)) }
        val w = x1 - x0; val h = y1 - y0
        fun q(u: Float, t: Float) = V3(x0 + w * u, y0 + h * t, z - 0.003f)
        when (cual % 3) {
            0 -> {
                Trazo3.run { cara(v, listOf(q(0.08f, 0.08f), q(0.92f, 0.08f), q(0.92f, 0.45f), q(0.08f, 0.45f)), n, C.CHAPA) }
                Trazo3.run { cara(v, listOf(q(0.08f, 0.28f), q(0.92f, 0.36f), q(0.92f, 0.45f), q(0.08f, 0.45f)), n, C.VERDE) }
                Trazo3.run { disco(v, q(0.5f, 0.66f), n, w * 0.2f, C.NARANJA, lados = 16) }
                for (k in 0 until 8) { val a = PI.toFloat() * k / 7f; Trazo3.run { barra(q(0.5f, 0.66f) + V3(cos(a) * w * 0.25f, sin(a) * w * 0.25f, 0f), q(0.5f, 0.66f) + V3(cos(a) * w * 0.36f, sin(a) * w * 0.36f, 0f), 0.015f, v.c(q(0.5f, 0.7f), n, C.NARANJA)) } }
            }
            1 -> {
                val c = q(0.5f, 0.45f)
                val gota = listOf(c + V3(0f, h * 0.38f, 0f), c + V3(w * 0.22f, -h * 0.05f, 0f), c + V3(w * 0.16f, -h * 0.22f, 0f), c + V3(0f, -h * 0.28f, 0f), c + V3(-w * 0.16f, -h * 0.22f, 0f), c + V3(-w * 0.22f, -h * 0.05f, 0f))
                Trazo3.run { cara(v, gota, n, C.AZUL) }
                Trazo3.run { barra(q(0.15f, 0.1f), q(0.85f, 0.1f), 0.02f, v.c(q(0.5f, 0.1f), n, C.ROJO)) }
            }
            else -> {
                for ((u, t, r, col) in listOf(Cuatro(0.32f, 0.62f, 0.2f, C.ROJO), Cuatro(0.62f, 0.42f, 0.24f, C.AZUL), Cuatro(0.4f, 0.25f, 0.12f, C.AMARILLO))) Trazo3.run { disco(v, q(u, t), n, w * r, col, lados = 14) }
            }
        }
        // La mancha de humedad y el cerco amarillo del papel viejo.
        Trazo3.run { cara(v, listOf(q(0f, 0f), q(0.5f, 0f), q(0.3f, 0.18f), q(0f, 0.25f)), n, lerp(C.ROCA, C.AMARILLO, 0.3f), alfa = 0.35f) }
    }

    private class Cuatro(val a: Float, val b: Float, val c: Float, val d: Color) { operator fun component1() = a; operator fun component2() = b; operator fun component3() = c; operator fun component4() = d }

    /** El cuadro de palancas: un armario de chapa con tres palancas, pilotos de colores y un reloj. */
    private fun DrawScope.armario(v: VistaRefugio) {
        val x0 = S.ARMARIO_X0; val x1 = S.ARMARIO_X1; val z1 = S.FONDO; val z0 = z1 - 0.42f; val alto = 1.95f
        Trazo3.run { caja(v, x0, x1, 0.05f, alto, z0, z1, C.CHAPA, lerp(C.CHAPA, C.ACERO, 0.3f)) }
        Trazo3.run { caja(v, x0 - 0.03f, x1 + 0.03f, 0f, 0.08f, z0 - 0.03f, z1, C.HIERRO) }
        // Las palancas: el eje, el mango rojo.
        for (k in 0..2) {
            val x = x0 + 0.2f + 0.27f * k; val arriba = (k + v.m.ficha.semilla.toInt()) % 3 != 0
            val a = V3(x, 1.2f, z0 - 0.02f); val b = a + V3(0f, if (arriba) 0.28f else -0.22f, -0.12f)
            Trazo3.run { caja(v, x - 0.05f, x + 0.05f, 1.1f, 1.3f, z0 - 0.04f, z0, C.HIERRO); barra(a, b, 0.035f, v.c(a, K.FRENTE, C.ACERO)); disco(v, b, K.FRENTE, 0.045f, C.ROJO, lados = 8) }
        }
        // Los pilotos: el verde de siempre, el ámbar de aviso (encendido en el racionamiento y la cuarentena), el rojo.
        val aviso = v.estado == EscenaRefugio.Estado.RACIONAMIENTO || v.estado == EscenaRefugio.Estado.CUARENTENA || v.alarma
        for ((k, col) in listOf(lerp(C.ALGA, Color.White, 0.3f), C.GUARDIA, C.ALARMA).withIndex()) {
            val w = V3(x0 + 0.25f + 0.22f * k, 1.62f, z0 - 0.02f)
            val prendido = k == 0 || (k == 1 && aviso) || (k == 2 && v.alarma)
            Trazo3.run { disco(v, w, K.FRENTE, 0.035f, if (prendido) col else lerp(col, C.TINTA, 0.6f), lados = 8, luz = !prendido) }
            if (prendido) drawCircle(Brush.radialGradient(listOf(col.copy(alpha = 0.6f), Color.Transparent), K.p(w), 0.18f * K.k(w)), 0.18f * K.k(w), K.p(w), blendMode = BlendMode.Plus)
        }
        // Un reloj redondo de caudal.
        manometro(v, V3((x0 + x1) / 2f, 0.75f, z0 - 0.02f), 0.13f, 0.4f)
    }

    /**
     * La pared de manómetros: una chapa verde azulada con nueve manómetros de latón, cada uno de su tamaño, con su aguja
     * (sin números), los tubitos de cobre que los alimentan y su colector. Las agujas leen el agua: bajan con la reserva.
     */
    private fun DrawScope.tablero(v: VistaRefugio) {
        val x0 = S.TABLERO_X0; val x1 = S.TABLERO_X1; val z = S.FONDO - 0.04f
        Trazo3.run { caja(v, x0, x1, 1.05f, 2.6f, z - 0.03f, z, lerp(C.CHAPA, C.TINTA, 0.15f)) }
        val az = Azar(Semilla.de("refugio", "manometros"))
        val gauges = listOf(
            Triple(0.12f, 2.3f, 0.17f), Triple(0.33f, 2.32f, 0.13f), Triple(0.52f, 2.28f, 0.19f), Triple(0.76f, 2.34f, 0.14f), Triple(0.92f, 2.25f, 0.11f),
            Triple(0.2f, 1.72f, 0.21f), Triple(0.45f, 1.66f, 0.15f), Triple(0.66f, 1.74f, 0.18f), Triple(0.88f, 1.68f, 0.14f),
        )
        // El colector de abajo y los tubitos a cada manómetro.
        Trazo3.run { cilindro(v, V3(x0 + 0.05f, 1.25f, z - 0.12f), V3(x1 - 0.05f, 1.25f, z - 0.12f), 0.045f, C.COBRE, lados = 8) }
        for ((u, y, r) in gauges) {
            val x = x0 + (x1 - x0) * u
            Trazo3.run { barra(V3(x, 1.25f, z - 0.12f), V3(x, y - r, z - 0.08f), 0.022f, v.c(V3(x, (y + 1.25f) / 2f, z), K.FRENTE, C.COBRE)) }
            Trazo3.run { disco(v, V3(x, 1.25f, z - 0.12f) + K.FRENTE * 0.05f, K.FRENTE, 0.03f, C.ROJO, lados = 8) }
        }
        for ((i, g) in gauges.withIndex()) {
            val (u, y, r) = g
            val lee = (v.reserva * (0.7f + 0.3f * az.real()) + 0.1f * az.real()).coerceIn(0.05f, 0.95f)
            manometro(v, V3(x0 + (x1 - x0) * u, y, z - 0.08f), r, if (i % 3 == 0) lee else (0.3f + 0.6f * az.real()))
        }
        // La placa de latón de arriba, sin letras.
        Trazo3.run { cara(v, listOf(V3(x0 + 0.6f, 2.5f, z - 0.035f), V3(x1 - 0.6f, 2.5f, z - 0.035f), V3(x1 - 0.6f, 2.57f, z - 0.035f), V3(x0 + 0.6f, 2.57f, z - 0.035f)), K.FRENTE, C.LATON) }
    }

    /**
     * Un manómetro de latón mirando hacia la sala, en [c], de radio [r], con la aguja en [lee] (de 0 a 1): el aro, la
     * esfera crema con su arco de rayitas y su tramo rojo, la aguja negra y el brillo del vidrio.
     */
    fun DrawScope.manometro(v: VistaRefugio, c: V3, r: Float, lee: Float) {
        val n = K.FRENTE
        Trazo3.run {
            disco(v, c, n, r, C.LATON, lados = 18)
            disco(v, c + n * 0.01f, n, r * 0.82f, lerp(C.PAPEL, Color.White, 0.25f), lados = 18)
        }
        val u = K.DER; val arriba = K.ARRIBA
        // Las rayitas del arco (sin números) y el tramo rojo del final.
        for (k in 0..8) {
            val a = (PI * (1.25 - 1.5 * k / 8.0)).toFloat()
            val d = u * cos(a) + arriba * sin(a)
            Trazo3.run { barra(c + n * 0.015f + d * (r * 0.62f), c + n * 0.015f + d * (r * 0.74f), r * 0.05f, v.c(c, n, if (k >= 7) C.ROJO else C.TINTA)) }
        }
        val a = (PI * (1.25 - 1.5 * lee)).toFloat()
        val d = u * cos(a) + arriba * sin(a)
        Trazo3.run {
            barra(c + n * 0.02f - d * (r * 0.12f), c + n * 0.02f + d * (r * 0.66f), r * 0.07f, v.c(c, n, C.TINTA))
            disco(v, c + n * 0.025f, n, r * 0.1f, C.TINTA, lados = 8)
        }
        // El brillo del vidrio: de la luz que le da.
        val brillo = v.c(c, n, Color.White)
        val p = K.p(c + n * 0.03f + (u * -0.4f + arriba * 0.45f) * r)
        drawCircle(brillo.copy(alpha = 0.35f), r * 0.16f * K.k(c), p)
    }

    /**
     * La columna de nivel de la cisterna grande: un tubo de vidrio alto entre sus dos llaves de latón, delante de una
     * regla esmaltada con rayitas (sin números) y la marca roja; dentro, el agua hasta su nivel (el arco `reserva`) y el
     * flotador rojo. Con la cisterna baja, el piloto rojo de al lado se enciende.
     */
    private fun DrawScope.columna(v: VistaRefugio) {
        val x = S.COLUMNA_X; val z = S.FONDO - 0.02f; val y0 = 0.3f; val y1 = 2.7f
        val n = K.FRENTE
        // La regla esmaltada, con su marco.
        Trazo3.run { cara(v, listOf(V3(x - 0.13f, y0 - 0.05f, z), V3(x + 0.13f, y0 - 0.05f, z), V3(x + 0.13f, y1 + 0.05f, z), V3(x - 0.13f, y1 + 0.05f, z)), n, C.HIERRO) }
        Trazo3.run { cara(v, listOf(V3(x + 0.0f, y0, z - 0.01f), V3(x + 0.11f, y0, z - 0.01f), V3(x + 0.11f, y1, z - 0.01f), V3(x + 0.0f, y1, z - 0.01f)), n, lerp(C.PAPEL, Color.White, 0.3f)) }
        for (k in 0..20) { val y = y0 + (y1 - y0) * k / 20f; val largo = if (k % 5 == 0) 0.08f else 0.045f; Trazo3.run { barra(V3(x + 0.11f - largo, y, z - 0.015f), V3(x + 0.11f, y, z - 0.015f), 0.012f, v.c(V3(x, y, z), n, C.TINTA)) } }
        val ym = y0 + (y1 - y0) * EscenaRefugio.MARCA_ROJA
        Trazo3.run { cara(v, listOf(V3(x - 0.01f, ym - 0.025f, z - 0.016f), V3(x + 0.12f, ym - 0.025f, z - 0.016f), V3(x + 0.12f, ym + 0.025f, z - 0.016f), V3(x - 0.01f, ym + 0.025f, z - 0.016f)), n, C.ROJO) }
        // El tubo de vidrio con el agua hasta su nivel.
        val tz = z - 0.16f; val tx = x - 0.05f
        val nivel = y0 + (y1 - y0) * v.reserva
        Trazo3.run {
            cilindro(v, V3(tx, y0, tz), V3(tx, nivel, tz), 0.065f, lerp(C.PISCINA, C.PAPEL, 0.35f), lados = 12)
            cilindro(v, V3(tx, nivel, tz), V3(tx, y1, tz), 0.065f, lerp(C.HIERRO, C.CHAPA, 0.25f), lados = 12, tapas = false)
            barra(V3(tx - 0.035f, y0, tz - 0.06f), V3(tx - 0.035f, y1, tz - 0.06f), 0.012f, v.c(V3(tx, 1.5f, tz), n, Color.White).copy(alpha = 0.55f))
            // El flotador.
            disco(v, V3(tx, nivel + 0.04f, tz - 0.07f), n, 0.075f, lerp(C.ROJO, C.NARANJA, 0.5f), lados = 12)
            disco(v, V3(tx - 0.02f, nivel + 0.06f, tz - 0.08f), n, 0.025f, lerp(C.NARANJA, Color.White, 0.5f), lados = 8)
            // Las llaves de latón de arriba y de abajo, con su tubo a la pared.
            cilindro(v, V3(tx, y0 - 0.12f, tz), V3(tx, y0, tz), 0.07f, C.LATON, lados = 10)
            cilindro(v, V3(tx, y1, tz), V3(tx, y1 + 0.12f, tz), 0.07f, C.LATON, lados = 10)
            cilindro(v, V3(tx, y0 - 0.06f, tz), V3(tx, y0 - 0.06f, z), 0.04f, C.COBRE, lados = 8, tapas = false)
            cilindro(v, V3(tx, y1 + 0.06f, tz), V3(tx, y1 + 0.06f, z), 0.04f, C.COBRE, lados = 8, tapas = false)
            // La guarda: dos varillas.
            for (dx in listOf(-0.07f, 0.06f)) barra(V3(tx + dx, y0, tz - 0.05f), V3(tx + dx, y1, tz - 0.05f), 0.012f, v.c(V3(tx, 1.5f, tz), n, C.ACERO))
        }
        // El piloto rojo de la cisterna baja.
        val piloto = V3(x + 0.06f, y1 + 0.2f, z - 0.02f)
        val bajo = EscenaRefugio.tramoReserva(v.m) == EscenaRefugio.TramoReserva.BAJA
        Trazo3.run { disco(v, piloto, n, 0.045f, if (bajo) lerp(C.ALARMA, Color.White, 0.3f) else lerp(C.ROJO, C.TINTA, 0.55f), lados = 10, luz = !bajo) }
        if (bajo) drawCircle(Brush.radialGradient(listOf(C.ALARMA.copy(alpha = 0.6f), Color.Transparent), K.p(piloto), 0.3f * K.k(piloto)), 0.3f * K.k(piloto), K.p(piloto), blendMode = BlendMode.Plus)
    }

    /**
     * El quicio de la derecha del portón (cortado por el borde): el interruptor de la sala, la percha de las linternas de
     * dinamo, el gancho del mono azul (cuando cuelga) y, en la cuarentena, las mascarillas de tela; abajo, el extintor.
     */
    private fun DrawScope.quicio(v: VistaRefugio) {
        val z = S.FONDO - 0.04f; val n = K.FRENTE
        // El interruptor: una caja de baquelita negra con su palanca.
        Trazo3.run { caja(v, 1.22f, 1.42f, 1.35f, 1.62f, z - 0.1f, z, C.HIERRO); barra(V3(1.32f, 1.5f, z - 0.11f), V3(1.32f, 1.6f, z - 0.16f), 0.025f, v.c(V3(1.32f, 1.5f, z), n, C.ACERO)) }
        Trazo3.run { barra(V3(1.32f, 1.62f, z - 0.03f), V3(1.32f, S.TECHO, z - 0.03f), 0.03f, v.c(V3(1.32f, 2.4f, z), n, C.ACERO)) }
        // La percha de las linternas: dos linternas de manivela colgadas.
        Trazo3.run { barra(V3(1.12f, 2.05f, z - 0.05f), V3(1.75f, 2.05f, z - 0.05f), 0.025f, v.c(V3(1.4f, 2.05f, z), n, C.ACERO)) }
        if (R.laSalaPinta(v.m, "linterna")) for (x in listOf(1.2f, 1.36f)) linterna(v, V3(x, 2.0f, z - 0.1f))
        // El mono azul en su gancho, con sus remiendos (sin número), cuando ella no lo lleva puesto.
        val g = S.GANCHO
        Trazo3.run { barra(g, g + V3(0f, 0.04f, -0.08f), 0.025f, v.c(g, n, C.ACERO)) }
        if (R.monoColgado(v.m) && R.laSalaPinta(v.m, "mono")) mono(v, g + V3(0f, 0f, -0.1f))
        if (R.mascarillas(v.m)) for ((k, x) in listOf(1.15f, 1.3f, 1.47f).withIndex()) mascarilla(v, V3(x, 2.25f, z - 0.06f), k)
        if (R.luto(v.m)) {
            // Un paño oscuro en el gancho y una flor de papel prendida.
            val pts = listOf(g + V3(-0.12f, 0f, -0.12f), g + V3(0.12f, 0f, -0.12f), g + V3(0.16f, -0.6f, -0.14f), g + V3(-0.1f, -0.55f, -0.14f))
            Trazo3.run { cara(v, pts, n, lerp(C.TINTA, C.AZUL, 0.25f)); disco(v, g + V3(0f, -0.1f, -0.16f), n, 0.05f, lerp(C.PAPEL, C.ROJO, 0.3f), lados = 8) }
        }
        // El extintor, abajo, en su soporte (cortado por el borde).
        Trazo3.run { cilindro(v, V3(1.62f, 0.12f, z - 0.2f), V3(1.62f, 0.78f, z - 0.2f), 0.11f, C.ROJO, lados = 12); cilindro(v, V3(1.62f, 0.78f, z - 0.2f), V3(1.62f, 0.86f, z - 0.2f), 0.04f, C.HIERRO, lados = 8) }
    }

    /** Una linterna de dinamo colgada: el cuerpo, la manivela plegada y el cristal. */
    private fun DrawScope.linterna(v: VistaRefugio, c: V3) {
        Trazo3.run {
            barra(c + V3(0f, 0.05f, 0f), c + V3(0f, 0.0f, 0f), 0.01f, v.c(c, K.FRENTE, C.TINTA))
            caja(v, c.x - 0.05f, c.x + 0.05f, c.y - 0.2f, c.y - 0.02f, c.z - 0.04f, c.z + 0.04f, lerp(C.VERDE, C.TINTA, 0.25f))
            disco(v, V3(c.x, c.y - 0.21f, c.z), K.ABAJO, 0.045f, C.ACERO, lados = 8)
            barra(c + V3(0.05f, -0.06f, -0.03f), c + V3(0.08f, -0.14f, -0.04f), 0.012f, v.c(c, K.FRENTE, C.ACERO))
        }
    }

    /** El mono azul colgado de su gancho: los hombros, las mangas caídas, las perneras, los remiendos de otro azul. */
    fun DrawScope.mono(v: VistaRefugio, g: V3) {
        val n = K.FRENTE
        val cuerpo = listOf(g + V3(-0.2f, -0.06f, 0f), g + V3(0.2f, -0.06f, 0f), g + V3(0.22f, -0.75f, -0.02f), g + V3(0.19f, -1.45f, -0.03f), g + V3(0.02f, -1.45f, -0.03f), g + V3(0f, -0.85f, -0.02f), g + V3(-0.04f, -1.45f, -0.03f), g + V3(-0.21f, -1.45f, -0.03f), g + V3(-0.22f, -0.75f, -0.02f))
        Trazo3.run { caraDegradada(v, cuerpo, n, C.AZUL, g + V3(0f, -0.1f, 0f), g + V3(0f, -1.4f, 0f)) }
        Trazo3.run {
            cara(v, listOf(g + V3(0.2f, -0.06f, 0f), g + V3(0.27f, -0.1f, -0.01f), g + V3(0.27f, -0.7f, -0.03f), g + V3(0.21f, -0.7f, -0.03f)), n, lerp(C.AZUL, C.TINTA, 0.2f))
            cara(v, listOf(g + V3(-0.2f, -0.06f, 0f), g + V3(-0.27f, -0.1f, -0.01f), g + V3(-0.27f, -0.68f, -0.03f), g + V3(-0.21f, -0.68f, -0.03f)), n, lerp(C.AZUL, C.TINTA, 0.25f))
            // Los remiendos y el bolsillo del contador.
            cara(v, listOf(g + V3(-0.12f, -1.0f, -0.035f), g + V3(-0.04f, -1.0f, -0.035f), g + V3(-0.04f, -1.12f, -0.035f), g + V3(-0.12f, -1.12f, -0.035f)), n, lerp(C.AZUL, C.PAPEL, 0.3f))
            cara(v, listOf(g + V3(0.06f, -0.3f, -0.03f), g + V3(0.16f, -0.3f, -0.03f), g + V3(0.16f, -0.42f, -0.03f), g + V3(0.06f, -0.42f, -0.03f)), n, lerp(C.AZUL, C.TINTA, 0.35f))
            barra(g + V3(0f, -0.08f, -0.02f), g + V3(0f, -0.8f, -0.03f), 0.012f, v.c(g, n, lerp(C.ACERO, C.LATON, 0.4f)))
        }
    }

    /** Una mascarilla de tela colgada de su cinta. */
    private fun DrawScope.mascarilla(v: VistaRefugio, c: V3, k: Int) {
        val n = K.FRENTE; val tela = lerp(C.PAPEL, if (k % 2 == 0) C.AZUL else C.ALGA, 0.2f)
        Trazo3.run {
            barra(c, c + V3(-0.06f, -0.12f, 0f), 0.006f, v.c(c, n, C.TINTA)); barra(c, c + V3(0.06f, -0.12f, 0f), 0.006f, v.c(c, n, C.TINTA))
            cara(v, listOf(c + V3(-0.08f, -0.12f, -0.01f), c + V3(0.08f, -0.12f, -0.01f), c + V3(0.07f, -0.24f, -0.02f), c + V3(-0.07f, -0.24f, -0.02f)), n, tela)
        }
    }

    // ---------------------------------------------------------------- Las cosas del suelo

    /** Las cosas de la sala de este momento, cada una con su hondo, su esqueleto (para la sombra) y su pintura. */
    fun cosas(v: VistaRefugio): List<CosaRefugio> = buildList {
        val m = v.m
        add(cosa(S.BOMBA, listOf(V3(-3.95f, 0f, 1.85f), V3(-2.45f, 0f, 1.85f), V3(-2.45f, 0f, 3.65f), V3(-3.95f, 0f, 3.65f), V3(-3.7f, 1.15f, 2.0f), V3(-2.85f, 1.15f, 2.0f), V3(-2.85f, 1.3f, 3.4f), V3(-3.7f, 1.3f, 3.4f))) { bombaGrande(v) })
        add(cosa(S.BOMBA_2, listOf(V3(-4.1f, 0f, 4.3f), V3(-3.3f, 0f, 4.3f), V3(-3.3f, 0f, 5.1f), V3(-4.1f, 0f, 5.1f), V3(-3.95f, 1.4f, 4.5f), V3(-3.45f, 1.4f, 4.9f))) { bombaVertical(v, S.BOMBA_2) })
        if (R.bombaTres(m)) {
            add(cosa(S.BOMBA_3, listOf(V3(-4.7f, 0f, 6.0f), V3(-3.6f, 0f, 6.0f), V3(-3.6f, 0f, 6.8f), V3(-4.7f, 0f, 6.8f), V3(-4.3f, 0.7f, 6.4f))) { bombaDesmontada(v) })
            add(cosa(PIEZAS, listOf(PIEZAS + V3(-0.5f, 0f, -0.35f), PIEZAS + V3(0.5f, 0f, -0.35f), PIEZAS + V3(0.5f, 0f, 0.35f), PIEZAS + V3(-0.5f, 0f, 0.35f), PIEZAS + V3(0f, 0.15f, 0f))) { piezasDeLaBomba(v, PIEZAS) })
        }
        else add(cosa(S.BOMBA_3, listOf(V3(-4.8f, 0f, 6.0f), V3(-3.7f, 0f, 6.0f), V3(-3.7f, 0f, 6.75f), V3(-4.8f, 0f, 6.75f), V3(-4.6f, 1.05f, 6.2f), V3(-3.9f, 1.05f, 6.6f))) { bombaVieja(v, S.BOMBA_3) })
        add(cosa(S.FILTRO, K.circulo(V3(S.FILTRO.x, 0f, S.FILTRO.z), K.ARRIBA, S.FILTRO_R + 0.1f, 8) + K.circulo(V3(S.FILTRO.x, S.FILTRO_ALTO, S.FILTRO.z), K.ARRIBA, S.FILTRO_R, 8)) { filtro(v) })
        add(cosa(CUBO, K.circulo(CUBO, K.ARRIBA, 0.16f, 6) + K.circulo(CUBO + V3(0f, 0.32f, 0f), K.ARRIBA, 0.18f, 6)) { cubo(v, CUBO, C.ACERO, if (R.bombaTres(m)) lerp(C.OXIDO, C.ROCA, 0.4f) else lerp(C.CHAPA, C.PISCINA, 0.3f)) })
        add(cosa(V3(0.5f, 0f, 3.8f), K.circulo(V3(0.5f, 0.05f, 3.8f), K.ARRIBA, 0.32f, 8)) { manguera(v, V3(0.5f, 0f, 3.8f)) })
        add(cosa(V3(0.55f, 0f, 4.75f), listOf(V3(0.25f, 0f, 4.6f), V3(0.85f, 0f, 4.6f), V3(0.85f, 0f, 4.9f), V3(0.25f, 0f, 4.9f), V3(0.25f, 0.28f, 4.6f), V3(0.85f, 0.28f, 4.9f))) { cajaDeHerramientas(v, V3(0.55f, 0f, 4.75f)) })
        add(cosa(V3(0.85f, 0f, 6.3f), K.circulo(V3(0.85f, 0f, 6.3f), K.ARRIBA, 0.3f, 8) + K.circulo(V3(0.85f, 0.88f, 6.3f), K.ARRIBA, 0.3f, 8)) { bidon(v, V3(0.85f, 0f, 6.3f), C.VERDE) })
        add(cosa(V3(-0.98f, 0f, 2.9f), listOf(V3(S.BANCADA_X0, 0f, S.BANCADA_Z0), V3(S.BANCADA_X1, 0f, S.BANCADA_Z0), V3(S.BANCADA_X1, 0f, S.BANCADA_Z1), V3(S.BANCADA_X0, 0f, S.BANCADA_Z1), V3(S.BANCADA_X0, S.BANCADA_Y, S.BANCADA_Z0), V3(S.BANCADA_X1, S.BANCADA_Y, S.BANCADA_Z0), V3(S.BANCADA_X1, S.BANCADA_Y, S.BANCADA_Z1), V3(S.BANCADA_X0, S.BANCADA_Y, S.BANCADA_Z1))) { bancada(v) })
        // Los días de obra y los estados.
        if (R.filtros(m)) {
            add(cosa(V3(-0.75f, 0f, 5.1f), K.circulo(V3(-0.75f, 0f, 5.1f), K.ARRIBA, 0.42f, 8)) { tapaDelFiltro(v, V3(-0.75f, 0f, 5.1f)) })
            for ((k, q) in listOf(V3(-2.45f, 0f, 4.6f), V3(-0.45f, 0f, 4.2f), V3(-0.95f, 0f, 4.05f)).withIndex()) add(cosa(q, K.circulo(q, K.ARRIBA, 0.17f, 6) + K.circulo(q + V3(0f, 0.32f, 0f), K.ARRIBA, 0.17f, 6)) { cubo(v, q, if (k == 1) C.AZUL else C.ACERO, if (k == 2) lerp(C.PAPEL, C.ROCA, 0.4f) else C.BARRO) })
        }
        if (R.candado(m)) add(cosa(V3(-0.6f, 0f, 6.6f), listOf(V3(-1.0f, 0f, 6.4f), V3(-0.2f, 0f, 6.4f), V3(-0.2f, 0f, 6.8f), V3(-1.0f, 0f, 6.8f), V3(-0.6f, 0.75f, 6.6f))) { filtrosSaturados(v, V3(-0.6f, 0f, 6.6f)) })
        if (R.hojasSecas(m)) for ((k, q) in listOf(V3(1.35f, 0f, 6.0f), V3(1.25f, 0f, 5.35f)).withIndex()) add(cosa(q, K.circulo(q, K.ARRIBA, 0.3f, 8) + K.circulo(q + V3(0f, 0.88f, 0f), K.ARRIBA, 0.3f, 8)) { bidon(v, q, if (k == 0) C.AZUL else C.ACERO) })
        // El objeto grande del día: de pie junto a la punta de la bancada.
        ObjetosRefugio.grande(m, v.pincel(S.PIE_GRANDE), K.p(S.PIE_GRANDE), K.p(S.APOYO_GRANDE), K.k(S.PIE_GRANDE) / 120f)?.let { o ->
            add(CosaRefugio(K.hondo(S.PIE_GRANDE), listOf(S.PIE_GRANDE)) { o.pintar(this) })
        }
    }

    private fun cosa(donde: V3, esqueleto: List<V3>, pintar: DrawScope.() -> Unit) = CosaRefugio(K.hondo(donde), esqueleto, pintar)

    // ---------------------------------------------------------------- Las máquinas

    /** Una rueda de válvula roja en [c], mirando a [n], de radio [r]: el aro, cinco radios, el cubo y el vástago. */
    fun DrawScope.rueda(v: VistaRefugio, c: V3, n: V3, r: Float, color: Color = C.ROJO) {
        val e = K.unidad(n); val u = K.unidad(K.cruz(e, K.ARRIBA)).let { if (K.largo(it) < 0.5f) K.DER else it }; val w = K.cruz(e, u)
        Trazo3.run {
            barra(c - e * 0.18f, c, r * 0.16f, v.c(c, K.NADA, C.ACERO))
            for (k in 0 until 5) { val a = 2f * PI.toFloat() * k / 5 + 0.3f; val d = u * cos(a) + w * sin(a); barra(c, c + d * (r * 0.92f), r * 0.1f, v.c(c + d * (r * 0.5f), e, lerp(color, C.TINTA, 0.15f))) }
            aro(c, e, r, r * 0.18f, v.c(c + w * r, e, color), 26)
        }
        // El filo de luz del aro por el lado de arriba.
        val pts = K.circulo(c + e * 0.005f, e, r, 20, 0.3f, 2.6f).map { K.p(it) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y) }
        drawPath(camino, v.c(c + w * r, K.ARRIBA, lerp(color, Color.White, 0.45f)).copy(alpha = 0.7f), style = Stroke(r * 0.06f * K.k(c), cap = StrokeCap.Round))
        Trazo3.run { disco(v, c + e * 0.01f, e, r * 0.16f, C.HIERRO, lados = 10) }
    }

    /** Una brida (la unión de dos tubos) en [c], en un tubo de eje [eje], de radio [r]: el anillo y sus pernos. */
    private fun DrawScope.brida(v: VistaRefugio, c: V3, eje: V3, r: Float, color: Color = C.VERDE) {
        val e = K.unidad(eje)
        Trazo3.run { cilindro(v, c - e * 0.03f, c + e * 0.03f, r, color, lados = 14) }
        for (p in K.circulo(c + e * 0.035f, e, r * 0.8f, 6).dropLast(1)) if (K.seVe(p, K.unidad(p - c))) drawCircle(v.c(p, K.NADA, C.OXIDO), 0.022f * K.k(p), K.p(p))
    }

    /** Desconchones del verde: manchas de óxido y de hierro en una cara. */
    private fun DrawScope.desconchones(v: VistaRefugio, c: V3, n: V3, ancho: Float, alto: Float, semilla: String, cuantos: Int = 5) {
        val az = Azar(Semilla.de("refugio", "desconchon", semilla))
        val e = K.unidad(n); val u = K.unidad(K.cruz(e, K.ARRIBA)).let { if (K.largo(it) < 0.5f) K.DER else it }; val w = K.cruz(e, u)
        repeat(cuantos) {
            val q = c + u * az.entre(-ancho / 2f, ancho / 2f) + w * az.entre(-alto / 2f, alto / 2f) + e * 0.005f
            val r = az.entre(0.02f, 0.06f)
            val pts = (0 until 6).map { i -> val a = 6.283f * i / 6; q + u * (cos(a) * r * az.entre(0.6f, 1.4f)) + w * (sin(a) * r * 0.7f) }
            Trazo3.run { cara(v, pts, e, if (it % 2 == 0) C.OXIDO else C.HIERRO, alfa = 0.8f) }
        }
    }

    /**
     * La bomba grande, el marco de la izquierda (cortada por el borde): la bancada de fundición, el motor tumbado con sus
     * aletas y su tapa de ventilación de cara a la sala, el acoplamiento con su guarda amarilla, la voluta, el tubo que
     * sube hasta el techo con su válvula y su rueda roja, el manómetro, las bridas y la junta que gotea, con su costra de cal.
     */
    private fun DrawScope.bombaGrande(v: VistaRefugio) {
        val b = S.BOMBA
        Trazo3.run { caja(v, b.x - 0.7f, b.x + 0.8f, 0f, 0.22f, 1.85f, 3.65f, C.HIERRO, lerp(C.HIERRO, C.VERDE, 0.3f)) }
        // El motor: tumbado a lo largo, con sus aletas.
        val m0 = V3(b.x - 0.05f, 0.72f, 1.95f); val m1 = V3(b.x - 0.05f, 0.72f, 2.85f)
        Trazo3.run { caja(v, b.x - 0.42f, b.x + 0.32f, 0.22f, 0.38f, 2.05f, 2.75f, lerp(C.VERDE, C.TINTA, 0.25f)) }
        Trazo3.run { cilindro(v, m0, m1, 0.42f, C.VERDE, lerp(C.VERDE, C.TINTA, 0.2f), lados = 22) }
        for (k in 1..5) { val z = 1.98f + 0.15f * k; Trazo3.run { aro(V3(m0.x, m0.y, z), K.FONDO, 0.43f, 0.02f, v.c(V3(m0.x + 0.43f, m0.y + 0.2f, z), K.unidad(V3(1f, 0.5f, 0f)), lerp(C.VERDE, C.PAPEL, 0.15f)), 24) } }
        // La tapa de ventilación, de cara a la sala, con su rejilla.
        Trazo3.run { cilindro(v, m0 - K.FONDO * 0.12f, m0, 0.36f, lerp(C.VERDE, C.TINTA, 0.15f), lerp(C.VERDE_CLARO, C.VERDE, 0.4f), lados = 20) }
        // La rejilla de la tapa: ranuras que salen del centro.
        for (k in 0 until 12) { val a = 2f * PI.toFloat() * k / 12; val d = V3(cos(a), sin(a), 0f); val c0 = m0 - K.FONDO * 0.125f
            Trazo3.run { barra(c0 + d * 0.09f, c0 + d * 0.3f, 0.028f, v.c(c0, K.FRENTE, lerp(C.VERDE, C.TINTA, 0.7f))) } }
        Trazo3.run { disco(v, m0 - K.FONDO * 0.13f, K.FRENTE, 0.06f, C.VERDE_CLARO, lados = 12) }
        desconchones(v, m1 + V3(0.42f, 0f, -0.45f), K.DER, 0.6f, 0.4f, "motor", 6)
        // El acoplamiento con su guarda amarilla.
        Trazo3.run { cilindro(v, V3(m0.x, 0.75f, 2.85f), V3(m0.x, 0.75f, 3.05f), 0.24f, lerp(C.AMARILLO, C.OXIDO, 0.15f), lados = 14) }
        // La voluta: el caracol de la bomba.
        val v0 = V3(b.x, 0.78f, 3.05f); val v1 = V3(b.x, 0.78f, 3.48f)
        Trazo3.run { cilindro(v, v0, v1, 0.56f, C.VERDE, lerp(C.VERDE, C.TINTA, 0.1f), lados = 24) }
        brida(v, V3(b.x, 0.78f, 3.27f), K.FONDO, 0.6f)
        desconchones(v, V3(b.x + 0.56f, 0.78f, 3.27f), K.DER, 0.4f, 0.6f, "voluta", 5)
        // La salida: el tubo que sube por la derecha de la voluta hasta el techo, con su válvula y su rueda roja.
        val t = V3(b.x + 0.35f, 0f, 3.27f)
        Trazo3.run { cilindro(v, t + V3(0f, 1.15f, 0f), t + V3(0f, 2.82f, 0f), 0.15f, C.VERDE, lados = 16) }
        brida(v, t + V3(0f, 1.3f, 0f), K.ARRIBA, 0.22f)
        brida(v, t + V3(0f, 1.82f, 0f), K.ARRIBA, 0.22f)
        Trazo3.run { cilindro(v, t + V3(0f, 1.36f, 0f), t + V3(0f, 1.76f, 0f), 0.2f, lerp(C.VERDE, C.HIERRO, 0.3f), lados = 16) }
        rueda(v, t + V3(0.32f, 1.56f, -0.05f), K.unidad(V3(1f, 0.1f, -0.35f)), 0.3f)
        // La costra de cal de la junta que gotea, y el manómetro de la salida.
        Trazo3.run { cara(v, listOf(t + V3(0.12f, 1.25f, -0.16f), t + V3(0.22f, 1.25f, -0.12f), t + V3(0.2f, 1.0f, -0.14f), t + V3(0.14f, 0.92f, -0.16f)), K.FRENTE, C.CAL, alfa = 0.85f) }
        manometroDeTubo(v, t + V3(-0.02f, 2.2f, -0.16f), 0.11f, 0.55f + 0.35f * v.reserva)
        // La entrada: un tubo que va hacia la pared del fondo, a ras de la bancada.
        Trazo3.run { cilindro(v, V3(b.x - 0.2f, 0.45f, 3.48f), V3(b.x - 0.2f, 0.45f, 4.3f), 0.17f, lerp(C.VERDE, C.ACERO, 0.35f), lados = 14) }
        brida(v, V3(b.x - 0.2f, 0.45f, 3.6f), K.FONDO, 0.24f)
        // La llave de válvulas larga, apoyada en la bancada de la bomba, si hoy no es el objeto.
        if (R.laSalaPinta(v.m, "llave")) llave(v, V3(b.x + 1.05f, 0.0f, 2.05f), V3(b.x + 0.62f, 0.9f, 2.5f))
    }

    /** Un manómetro pequeño en un tubo, mirando hacia la sala. */
    private fun DrawScope.manometroDeTubo(v: VistaRefugio, c: V3, r: Float, lee: Float) {
        Trazo3.run { barra(c + K.FONDO * 0.12f, c, 0.03f, v.c(c, K.NADA, C.LATON)) }
        manometro(v, c, r, lee)
    }

    /** La llave de válvulas: «de hierro, larga como un brazo, con el mango forrado de cinta», apoyada de [pie] a [punta]. */
    fun DrawScope.llave(v: VistaRefugio, pie: V3, punta: V3) {
        val d = punta - pie
        Trazo3.run {
            barra(pie, punta, 0.035f, v.c(K.medio(pie, punta), K.NADA, lerp(C.HIERRO, C.ACERO, 0.45f)))
            barra(pie, pie + d * 0.4f, 0.05f, v.c(pie + d * 0.2f, K.NADA, lerp(C.TINTA, C.AZUL, 0.35f)))
            barra(punta - K.DER * 0.06f, punta + K.DER * 0.06f + K.ARRIBA * 0.03f, 0.05f, v.c(punta, K.FRENTE, lerp(C.ACERO, Color.White, 0.15f)), StrokeCap.Butt)
            barra(punta - K.DER * 0.06f, punta - K.DER * 0.07f + K.ARRIBA * 0.08f, 0.03f, v.c(punta, K.FRENTE, C.ACERO)); barra(punta + K.DER * 0.06f, punta + K.DER * 0.07f + K.ARRIBA * 0.09f, 0.03f, v.c(punta, K.FRENTE, C.ACERO))
        }
    }

    /** Una bomba vertical: el cuerpo redondo en su base, el motor encima, la salida con su rueda y la entrada a la pared. */
    private fun DrawScope.bombaVertical(v: VistaRefugio, b: V3) {
        Trazo3.run {
            caja(v, b.x - 0.4f, b.x + 0.4f, 0f, 0.16f, b.z - 0.4f, b.z + 0.4f, C.HIERRO)
            cilindro(v, b + V3(0f, 0.16f, 0f), b + V3(0f, 0.8f, 0f), 0.33f, C.VERDE, lados = 20)
        }
        brida(v, b + V3(0f, 0.8f, 0f), K.ARRIBA, 0.36f)
        Trazo3.run {
            cilindro(v, b + V3(0f, 0.84f, 0f), b + V3(0f, 1.32f, 0f), 0.26f, lerp(C.VERDE, C.CHAPA, 0.4f), lerp(C.VERDE, C.TINTA, 0.2f), lados = 18)
            cilindro(v, b + V3(0f, 1.32f, 0f), b + V3(0f, 1.4f, 0f), 0.12f, C.HIERRO, lados = 10)
            cilindro(v, b + V3(0.3f, 0.45f, 0f), b + V3(0.62f, 0.45f, 0f), 0.11f, C.VERDE, lados = 12)
            cilindro(v, b + V3(0f, 0.45f, 0.3f), b + V3(0f, 0.45f, S.FONDO - b.z), 0.12f, lerp(C.VERDE, C.ACERO, 0.4f), lados = 12)
        }
        rueda(v, b + V3(0.66f, 0.45f, 0f), K.DER, 0.2f)
        desconchones(v, b + V3(0.2f, 0.5f, -0.28f), K.unidad(V3(0.6f, 0f, -0.8f)), 0.3f, 0.4f, "vertical", 4)
        manometroDeTubo(v, b + V3(-0.1f, 1.05f, -0.28f), 0.08f, 0.6f)
    }

    /** La bomba vieja de la fila (la «bomba tres» del cuento): más chica, tumbada, con su motor y su rueda. */
    private fun DrawScope.bombaVieja(v: VistaRefugio, b: V3) {
        Trazo3.run {
            caja(v, b.x - 0.55f, b.x + 0.55f, 0f, 0.18f, b.z - 0.35f, b.z + 0.35f, C.HIERRO)
            cilindro(v, b + V3(-0.5f, 0.55f, 0f), b + V3(0.05f, 0.55f, 0f), 0.3f, lerp(C.VERDE, C.CHAPA, 0.3f), lados = 18)
            cilindro(v, b + V3(0.05f, 0.55f, 0f), b + V3(0.35f, 0.55f, 0f), 0.38f, C.VERDE, lados = 20)
            cilindro(v, b + V3(0.2f, 0.9f, 0f), b + V3(0.2f, 1.05f, 0f), 0.1f, C.VERDE, lados = 10)
            cilindro(v, b + V3(0.2f, 0.55f, 0.3f), b + V3(0.2f, 0.55f, S.FONDO - b.z), 0.11f, lerp(C.VERDE, C.ACERO, 0.35f), lados = 12)
        }
        rueda(v, b + V3(0.5f, 0.6f, -0.05f), K.DER, 0.18f)
        desconchones(v, b + V3(0.2f, 0.55f, -0.38f), K.FRENTE, 0.4f, 0.3f, "vieja", 4)
    }

    /**
     * La bomba tres desmontada «pieza por pieza» (el día `bomba_tres`): la carcasa abierta sobre su base, el rodete en el
     * suelo sobre un trapo, el eje partido, los pernos, y el cubo de agua marrón.
     */
    private fun DrawScope.bombaDesmontada(v: VistaRefugio) {
        val b = S.BOMBA_3
        Trazo3.run {
            caja(v, b.x - 0.55f, b.x + 0.55f, 0f, 0.18f, b.z - 0.35f, b.z + 0.35f, C.HIERRO)
            cilindro(v, b + V3(-0.5f, 0.55f, 0f), b + V3(0.05f, 0.55f, 0f), 0.3f, lerp(C.VERDE, C.CHAPA, 0.3f), lados = 18)
            cilindro(v, b + V3(0.05f, 0.55f, 0f), b + V3(0.15f, 0.55f, 0f), 0.38f, C.VERDE, lados = 20)
        }
        Trazo3.run { cilindro(v, b + V3(0.75f, 0f, 0.15f), b + V3(0.75f, 0.12f, 0.15f), 0.36f, C.VERDE, lados = 18) }
        cubo(v, b + V3(1.2f, 0f, 0.35f), C.ACERO, lerp(C.OXIDO, C.ROCA, 0.4f))
    }

    /**
     * Las piezas de la bomba tres en el suelo, sobre un trapo, «pieza por pieza»: el rodete de latón, la tapa de la voluta,
     * el eje partido en dos («gastado por cien años de vueltas»), los pernos y el cubo con el agua marrón.
     */
    private fun DrawScope.piezasDeLaBomba(v: VistaRefugio, t: V3) {
        Trazo3.run {
            cara(v, listOf(t + V3(-0.5f, 0.005f, -0.32f), t + V3(0.52f, 0.005f, -0.38f), t + V3(0.48f, 0.005f, 0.36f), t + V3(-0.52f, 0.005f, 0.3f)), K.ARRIBA, lerp(C.PAPEL, C.ROCA, 0.3f))
            cilindro(v, t + V3(-0.2f, 0.01f, -0.05f), t + V3(-0.2f, 0.09f, -0.05f), 0.22f, C.LATON, lados = 16)
            for (k in 0 until 6) { val a = PI.toFloat() * k / 3f; barra(t + V3(-0.2f, 0.095f, -0.05f), t + V3(-0.2f + cos(a) * 0.19f, 0.095f, -0.05f + sin(a) * 0.19f), 0.03f, v.c(t, K.ARRIBA, lerp(C.LATON, C.TINTA, 0.35f))) }
            barra(t + V3(0.08f, 0.035f, -0.22f), t + V3(0.44f, 0.035f, -0.12f), 0.06f, v.c(t, K.ARRIBA, lerp(C.ACERO, Color.White, 0.2f)))
            barra(t + V3(0.12f, 0.035f, 0.02f), t + V3(0.42f, 0.035f, 0.18f), 0.06f, v.c(t, K.ARRIBA, lerp(C.ACERO, Color.White, 0.2f)))
            cilindro(v, t + V3(0.1f, 0f, 0.62f), t + V3(0.12f, 0.1f, 0.62f), 0.3f, C.VERDE, lerp(C.VERDE, C.TINTA, 0.3f), lados = 18)
        }
        val az = Azar(Semilla.de("refugio", "pernos"))
        repeat(9) { val p = t + V3(az.entre(-0.4f, 0.4f), 0.02f, az.entre(0.05f, 0.28f)); drawCircle(v.c(p, K.ARRIBA, C.OXIDO), 0.028f * K.k(p), K.p(p)) }
        cubo(v, t + V3(-0.75f, 0f, 0.15f), C.AZUL, lerp(C.OXIDO, C.ROCA, 0.35f))
    }

    /**
     * El filtro grande de la depuradora, «un cilindro del tamaño de un niño», en su cuna: la tapa abombada con su corona de
     * pernos, la mirilla, la entrada y la salida con su rueda, la llave de purga con su cubo. El día de los filtros, abierto:
     * sin tapa, con el barro negro dentro.
     */
    private fun DrawScope.filtro(v: VistaRefugio) {
        val f = S.FILTRO; val r = S.FILTRO_R; val h = S.FILTRO_ALTO
        val abierto = R.filtros(v.m)
        // La cuna: dos apoyos y la base.
        Trazo3.run {
            caja(v, f.x - 0.42f, f.x + 0.42f, 0f, 0.08f, f.z - 0.42f, f.z + 0.42f, C.HIERRO)
            caja(v, f.x - 0.36f, f.x + 0.36f, 0.08f, 0.3f, f.z - 0.08f, f.z + 0.08f, lerp(C.HIERRO, C.VERDE, 0.2f))
        }
        Trazo3.run { cilindro(v, f + V3(0f, 0.2f, 0f), f + V3(0f, h - 0.1f, 0f), r, C.VERDE, lados = 24) }
        desconchones(v, f + V3(0.15f, 0.7f, -r), K.unidad(V3(0.4f, 0f, -0.9f)), 0.3f, 0.6f, "filtro", 6)
        // Los aros de refuerzo.
        for (y in listOf(0.45f, 0.95f)) Trazo3.run { aro(f + V3(0f, y, 0f), K.ARRIBA, r + 0.008f, 0.025f, v.c(f + V3(r, y, 0f), K.DER, lerp(C.VERDE, C.TINTA, 0.35f)), 26) }
        // La mirilla: una ranura de vidrio en el frente, con el agua que pasa.
        val mira = f + V3(-0.08f, 0.55f, -r - 0.005f)
        Trazo3.run { cara(v, listOf(mira, mira + V3(0.1f, 0f, 0.01f), mira + V3(0.1f, 0.4f, 0.01f), mira + V3(0f, 0.4f, 0f)), K.FRENTE, lerp(C.CHAPA, if (R.racionamiento(v.m) || abierto) C.BARRO else C.PISCINA, 0.4f)) }
        if (abierto) {
            // Abierto: la boca con el barro negro dentro.
            Trazo3.run { disco(v, f + V3(0f, h - 0.1f, 0f), K.ARRIBA, r, C.BARRO, lados = 24); aro(f + V3(0f, h - 0.1f, 0f), K.ARRIBA, r, 0.04f, v.c(f + V3(0f, h, 0f), K.ARRIBA, C.ACERO), 26) }
        } else {
            // La brida de la tapa con su corona de pernos y la tapa abombada.
            Trazo3.run { cilindro(v, f + V3(0f, h - 0.12f, 0f), f + V3(0f, h - 0.04f, 0f), r + 0.06f, lerp(C.VERDE, C.HIERRO, 0.2f), lados = 24) }
            for (p in K.circulo(f + V3(0f, h - 0.03f, 0f), K.ARRIBA, r + 0.035f, 12).dropLast(1)) drawCircle(v.c(p, K.ARRIBA, C.OXIDO), 0.022f * K.k(p), K.p(p))
            Trazo3.run { cilindro(v, f + V3(0f, h - 0.04f, 0f), f + V3(0f, h + 0.04f, 0f), r * 0.85f, C.VERDE, lados = 22); cilindro(v, f + V3(0f, h + 0.04f, 0f), f + V3(0f, h + 0.1f, 0f), r * 0.45f, lerp(C.VERDE, C.TINTA, 0.2f), lados = 16) }
            Trazo3.run { cilindro(v, f + V3(0f, h + 0.1f, 0f), f + V3(0f, h + 0.18f, 0f), 0.04f, C.LATON, lados = 8) }
        }
        // La entrada por la izquierda (va a la pared) y la salida por la derecha, con su rueda.
        Trazo3.run {
            cilindro(v, f + V3(-r, 1.0f, 0f), f + V3(-r - 0.35f, 1.0f, 0f), 0.09f, C.VERDE, lados = 12)
            cilindro(v, f + V3(-r - 0.35f, 1.0f, 0f), f + V3(-r - 0.35f, 1.0f, S.FONDO - f.z), 0.09f, lerp(C.VERDE, C.ACERO, 0.3f), lados = 12)
            cilindro(v, f + V3(r, 0.42f, 0f), f + V3(r + 0.35f, 0.42f, 0f), 0.09f, C.VERDE, lados = 12)
        }
        rueda(v, f + V3(r + 0.25f, 0.62f, 0f), K.ARRIBA, 0.13f)
        // La llave de purga, abajo, y la gota.
        Trazo3.run { barra(f + V3(0.1f, 0.25f, -r), f + V3(0.14f, 0.2f, -r - 0.12f), 0.03f, v.c(f, K.FRENTE, C.LATON)) }
    }

    /** La tapa del filtro, quitada y apoyada en el suelo, el día de los filtros. */
    private fun DrawScope.tapaDelFiltro(v: VistaRefugio, c: V3) {
        val r = S.FILTRO_R + 0.06f
        Trazo3.run { cilindro(v, c, c + V3(0.05f, 0.1f, 0f), r, C.VERDE, lerp(C.VERDE, C.TINTA, 0.3f), lados = 22) }
        for (p in K.circulo(c + V3(0.05f, 0.11f, 0f), K.ARRIBA, r - 0.03f, 12).dropLast(1)) drawCircle(v.c(p, K.ARRIBA, C.OXIDO), 0.02f * K.k(p), K.p(p))
    }

    /** Los filtros saturados del racionamiento, apilados: cilindros cortos llenos de barro. */
    private fun DrawScope.filtrosSaturados(v: VistaRefugio, c: V3) {
        for ((k, d) in listOf(V3(-0.2f, 0f, 0f), V3(0.2f, 0f, 0.05f), V3(0f, 0.36f, 0.02f)).withIndex()) {
            val q = c + d
            Trazo3.run { cilindro(v, q, q + V3(0f, 0.34f, 0f), 0.17f, lerp(C.PAPEL, C.BARRO, 0.35f + 0.15f * k), lerp(C.BARRO, C.TINTA, 0.2f), lados = 14) }
        }
    }

    /** Un cubo de chapa con su asa y lo que tiene dentro ([dentro]: agua, barro, arena). */
    fun DrawScope.cubo(v: VistaRefugio, c: V3, chapa: Color, dentro: Color) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.32f, 0f), 0.17f, chapa, lerp(chapa, C.TINTA, 0.3f), lados = 16, tapas = false)
            disco(v, c + V3(0f, 0.28f, 0f), K.ARRIBA, 0.165f, dentro, lados = 16)
            aro(c + V3(0f, 0.32f, 0f), K.ARRIBA, 0.17f, 0.012f, v.c(c + V3(0f, 0.32f, 0f), K.ARRIBA, lerp(chapa, Color.White, 0.3f)), 18)
            comba(c + V3(-0.17f, 0.3f, 0f), c + V3(0.17f, 0.3f, 0f), -0.12f, 0.01f, v.c(c, K.NADA, C.ACERO))
        }
    }

    /** Un bidón de chapa, de pie, con sus aros y su tapón. */
    private fun DrawScope.bidon(v: VistaRefugio, c: V3, color: Color) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.88f, 0f), 0.29f, color, lerp(color, C.TINTA, 0.2f), lados = 18)
            for (y in listOf(0.29f, 0.59f)) aro(c + V3(0f, y, 0f), K.ARRIBA, 0.295f, 0.02f, v.c(c + V3(0.29f, y, 0f), K.DER, lerp(color, C.TINTA, 0.35f)), 20)
            disco(v, c + V3(0.12f, 0.885f, -0.05f), K.ARRIBA, 0.04f, C.HIERRO, lados = 8)
        }
        desconchones(v, c + V3(0f, 0.45f, -0.29f), K.FRENTE, 0.3f, 0.6f, "bidon${c.z}", 3)
    }

    /** La manguera enrollada en el suelo. */
    private fun DrawScope.manguera(v: VistaRefugio, c: V3) {
        for (k in 0..3) Trazo3.run { aro(c + V3(0.02f * k, 0.04f + 0.035f * k, 0f), K.ARRIBA, 0.3f - 0.025f * k, 0.045f, v.c(c + V3(0f, 0.1f, -0.3f), K.ARRIBA, lerp(C.TINTA, C.ROJO, 0.4f)), 22) }
        Trazo3.run { comba(c + V3(0.25f, 0.06f, -0.15f), c + V3(0.6f, 0.02f, -0.55f), 0.0f, 0.045f, v.c(c, K.ARRIBA, lerp(C.TINTA, C.ROJO, 0.4f))) }
    }

    /** La caja de herramientas abierta, roja, con sus herramientas asomando. */
    private fun DrawScope.cajaDeHerramientas(v: VistaRefugio, c: V3) {
        Trazo3.run {
            caja(v, c.x - 0.3f, c.x + 0.3f, 0f, 0.22f, c.z - 0.13f, c.z + 0.13f, C.ROJO, lerp(C.ROJO, C.TINTA, 0.5f))
            cara(v, listOf(V3(c.x - 0.3f, 0.22f, c.z + 0.13f), V3(c.x + 0.3f, 0.22f, c.z + 0.13f), V3(c.x + 0.3f, 0.36f, c.z + 0.28f), V3(c.x - 0.3f, 0.36f, c.z + 0.28f)), K.unidad(V3(0f, 0.7f, -0.7f)), lerp(C.ROJO, C.TINTA, 0.15f))
            barra(V3(c.x - 0.18f, 0.2f, c.z), V3(c.x - 0.05f, 0.36f, c.z - 0.02f), 0.025f, v.c(c, K.NADA, C.ACERO))
            barra(V3(c.x + 0.05f, 0.2f, c.z + 0.02f), V3(c.x + 0.2f, 0.32f, c.z + 0.04f), 0.03f, v.c(c, K.NADA, lerp(C.TINTA, C.AMARILLO, 0.5f)))
        }
    }

    // ---------------------------------------------------------------- La bancada

    /**
     * La bancada de trabajo, de chapa, en tres cuartos (cortada por el borde de abajo): las patas de ángulo, el estante de
     * abajo con las cajas de piezas, la tapa gastada con su canto, el tornillo de banco y, encima, el turno: el cuaderno, la
     * gradilla de tubitos de muestra, el microscopio, la fiambrera, la botella, el tocadiscos pequeño, los trapos y, en la
     * esquina de delante, el objeto del día. Y lo de cada día: la guirnalda, las gafas, la flor, la radio, el tocadiscos roto.
     */
    private fun DrawScope.bancada(v: VistaRefugio) {
        val x0 = S.BANCADA_X0; val x1 = S.BANCADA_X1; val z0 = S.BANCADA_Z0; val z1 = S.BANCADA_Z1; val y = S.BANCADA_Y
        val m = v.m
        // Las patas y el estante de abajo.
        for ((x, z) in listOf(x0 + 0.04f to z1 - 0.04f, x1 - 0.04f to z1 - 0.04f)) Trazo3.run { caja(v, x - 0.03f, x + 0.03f, 0f, y - 0.05f, z - 0.03f, z + 0.03f, C.HIERRO) }
        Trazo3.run { caja(v, x0 + 0.05f, x1 - 0.05f, 0.2f, 0.24f, z0 + 0.05f, z1 - 0.05f, lerp(C.CHAPA, C.HIERRO, 0.4f)) }
        // Abajo, la caja de piezas (de madera vieja, con tubos y bridas sobrantes asomando) y un rollo de alambre.
        Trazo3.run { caja(v, x0 + 0.12f, x0 + 0.62f, 0.24f, 0.46f, z0 + 0.2f, z1 - 0.1f, lerp(C.COBRE, C.TINTA, 0.45f), lerp(C.COBRE, C.TINTA, 0.3f)) }
        Trazo3.run { barra(V3(x0 + 0.2f, 0.46f, 2.95f), V3(x0 + 0.35f, 0.62f, 3.05f), 0.04f, v.c(V3(x0, 0.5f, 3f), K.NADA, C.ACERO)); barra(V3(x0 + 0.42f, 0.46f, 3.0f), V3(x0 + 0.5f, 0.58f, 2.9f), 0.03f, v.c(V3(x0, 0.5f, 3f), K.NADA, C.COBRE)) }
        for (k in 0..4) Trazo3.run { aro(V3(x0 + 0.95f, 0.26f + 0.02f * k, 3.0f), K.ARRIBA, 0.1f - 0.004f * k, 0.018f, v.c(V3(x0 + 0.95f, 0.3f, 2.9f), K.FRENTE, lerp(C.COBRE, if (k % 2 == 0) C.TINTA else C.LATON, 0.25f)), 16) }
        for ((x, z) in listOf(x0 + 0.04f to z0 + 0.04f, x1 - 0.04f to z0 + 0.04f)) Trazo3.run { caja(v, x - 0.03f, x + 0.03f, 0f, y - 0.05f, z - 0.03f, z + 0.03f, C.HIERRO) }
        // La tapa: la chapa gastada, con su canto y las marcas de uso.
        Trazo3.run { caja(v, x0, x1, y - 0.1f, y - 0.04f, z0, z1, lerp(C.CHAPA, C.HIERRO, 0.2f), lerp(C.CHAPA, C.HIERRO, 0.2f)) }
        // El tablero de madera gastada, con sus vetas y el canto comido.
        val madera = lerp(lerp(C.COBRE, C.AMARILLO, 0.25f), C.HORMIGON, 0.28f)
        Trazo3.run { caja(v, x0 - 0.02f, x1 + 0.02f, y - 0.04f, y, z0 - 0.02f, z1 + 0.01f, lerp(madera, C.TINTA, 0.25f), madera) }
        with(LuzRefugio) { charcoEnLaBancada(v, x0, x1, z0, z1, y) }
        val vz = Azar(Semilla.de("refugio", "vetas"))
        for (k in 1..6) { val z = z0 + (z1 - z0) * k / 7f + vz.entre(-0.02f, 0.02f); Trazo3.run { barra(V3(x0 + vz.entre(0f, 0.3f), y + 0.002f, z), V3(x1 - vz.entre(0f, 0.3f), y + 0.002f, z + vz.entre(-0.03f, 0.03f)), 0.008f, v.c(V3(-1f, y, z), K.ARRIBA, lerp(madera, C.TINTA, 0.35f)).copy(alpha = 0.6f)) } }
        val az = Azar(Semilla.de("refugio", "bancada"))
        repeat(8) { val a = V3(az.entre(x0 + 0.1f, x1 - 0.1f), y + 0.002f, az.entre(z0 + 0.05f, z1 - 0.05f)); Trazo3.run { barra(a, a + V3(az.entre(-0.2f, 0.2f), 0f, az.entre(-0.05f, 0.05f)), 0.006f, v.c(a, K.ARRIBA, lerp(C.ACERO, Color.White, 0.3f)).copy(alpha = 0.5f)) } }
        Trazo3.run { cara(v, listOf(V3(x0 + 0.5f, y + 0.002f, z0 + 0.2f), V3(x0 + 0.85f, y + 0.002f, z0 + 0.15f), V3(x0 + 0.8f, y + 0.002f, z0 + 0.4f), V3(x0 + 0.45f, y + 0.002f, z0 + 0.42f)), K.ARRIBA, C.TINTA, alfa = 0.25f) }
        // El tornillo de banco, en la punta de la izquierda.
        Trazo3.run {
            caja(v, x0 + 0.02f, x0 + 0.2f, y, y + 0.12f, z0 - 0.05f, z0 + 0.15f, lerp(C.VERDE, C.HIERRO, 0.4f))
            caja(v, x0 + 0.02f, x0 + 0.2f, y + 0.02f, y + 0.12f, z0 - 0.12f, z0 - 0.07f, lerp(C.VERDE, C.HIERRO, 0.4f))
            barra(V3(x0 + 0.11f, y + 0.06f, z0 - 0.14f), V3(x0 + 0.11f, y + 0.06f, z0 - 0.3f), 0.02f, v.c(V3(x0, y, z0), K.NADA, C.ACERO))
            barra(V3(x0 + 0.02f, y + 0.06f, z0 - 0.3f), V3(x0 + 0.2f, y + 0.06f, z0 - 0.3f), 0.016f, v.c(V3(x0, y, z0), K.NADA, C.ACERO))
        }
        // Lo de encima, de atrás adelante.
        val zb = 3.08f; val zf = 2.78f
        microscopio(v, V3(-1.5f, y, zb))
        if (R.senal(m)) radio(v, V3(-1.05f, y, zb)) else gradilla(v, V3(-1.08f, y, zb))
        if (R.tocadiscosRoto(m)) tocadiscosGrande(v, V3(-0.6f, y, zb)) else if (R.laSalaPinta(m, "tocadiscos")) tocadiscos(v, V3(-0.62f, y, zb), R.lampara(m) == Obra.BANCADA || R.racionamiento(m) && R.lampara(m) != null)
        botella(v, V3(-0.27f, y, zb + 0.02f), if (R.racionamiento(m)) 0.18f else 0.75f, R.bombaTres(m))
        if (R.florDeVerdad(m)) florEnFrasco(v, V3(-0.3f, y, 2.92f), R.florSeca(m))
        if (R.laSalaPinta(m, "cuaderno")) cuaderno(v, V3(-1.18f, y, zf), R.cuadernoAbierto(m))
        fiambrera(v, V3(-0.84f, y, zf), R.fiambreraAbierta(m))
        if (R.gafasDeCarton(m)) gafas(v, V3(-1.45f, y, 2.7f))
        if (R.sellado(m) || R.luto(m)) Trazo3.run { for (k in 0 until 5) { val a = 2f * PI.toFloat() * k / 5; disco(v, V3(-1.42f + cos(a) * 0.035f, y + 0.02f, 2.72f + sin(a) * 0.03f), K.ARRIBA, 0.03f, lerp(C.PAPEL, C.ROJO, 0.35f), lados = 7) }; disco(v, V3(-1.42f, y + 0.025f, 2.72f), K.ARRIBA, 0.018f, C.AMARILLO, lados = 6) }
        // El soporte del objeto chico del día: en la esquina de delante, a la derecha.
        with(ObjetosRefugio) { sobreElSoporte(m, v.pincel(S.SOPORTE), K.p(S.SOPORTE), K.k(S.SOPORTE) / 120f) }
        // Los trapos colgados del canto.
        Trazo3.run {
            cara(v, listOf(V3(-0.35f, y - 0.02f, z0 - 0.01f), V3(-0.18f, y - 0.02f, z0 - 0.01f), V3(-0.2f, y - 0.32f, z0 - 0.02f), V3(-0.33f, y - 0.28f, z0 - 0.02f)), K.FRENTE, lerp(C.PAPEL, C.ROCA, 0.35f))
            cara(v, listOf(V3(-1.35f, y - 0.02f, z0 - 0.01f), V3(-1.22f, y - 0.02f, z0 - 0.01f), V3(-1.24f, y - 0.22f, z0 - 0.02f), V3(-1.36f, y - 0.2f, z0 - 0.02f)), K.FRENTE, lerp(C.ROJO, C.PAPEL, 0.4f))
        }
        if (R.guirnalda(m)) guirnalda(v, V3(x0 + 0.05f, y - 0.02f, z0 - 0.02f), V3(x1 - 0.05f, y - 0.02f, z0 - 0.02f))
    }

    /** El microscopio: la base, el brazo, el tubo y la platina. */
    private fun DrawScope.microscopio(v: VistaRefugio, c: V3) {
        Trazo3.run {
            caja(v, c.x - 0.08f, c.x + 0.08f, c.y, c.y + 0.03f, c.z - 0.06f, c.z + 0.08f, C.TINTA)
            caja(v, c.x - 0.025f, c.x + 0.025f, c.y + 0.03f, c.y + 0.24f, c.z + 0.04f, c.z + 0.08f, lerp(C.TINTA, C.CHAPA, 0.3f))
            caja(v, c.x - 0.06f, c.x + 0.06f, c.y + 0.1f, c.y + 0.115f, c.z - 0.05f, c.z + 0.05f, C.ACERO)
            cilindro(v, c + V3(0f, 0.14f, -0.01f), c + V3(0f, 0.3f, 0.03f), 0.022f, lerp(C.TINTA, C.ACERO, 0.4f), lados = 10)
            cilindro(v, c + V3(0f, 0.3f, 0.03f), c + V3(0f, 0.33f, 0.035f), 0.026f, C.TINTA, lados = 10)
        }
    }

    /** La gradilla de tubitos de muestra: «tubitos de cristal numerados» (sin números), con el agua de cada punto. */
    private fun DrawScope.gradilla(v: VistaRefugio, c: V3) {
        Trazo3.run { caja(v, c.x - 0.14f, c.x + 0.14f, c.y, c.y + 0.07f, c.z - 0.04f, c.z + 0.04f, lerp(C.AMARILLO, C.COBRE, 0.4f)) }
        val aguas = listOf(lerp(C.PISCINA, Color.White, 0.5f), lerp(C.PISCINA, C.ROCA, 0.4f), lerp(C.BARRO, C.ROCA, 0.4f), lerp(C.PISCINA, Color.White, 0.6f), lerp(C.ALGA, C.PISCINA, 0.4f))
        for (k in 0..4) {
            val x = c.x - 0.11f + 0.055f * k
            Trazo3.run {
                cilindro(v, V3(x, c.y + 0.01f, c.z), V3(x, c.y + 0.1f, c.z), 0.012f, aguas[k], lados = 6)
                cilindro(v, V3(x, c.y + 0.1f, c.z), V3(x, c.y + 0.15f, c.z), 0.012f, lerp(Color.White, C.PISCINA, 0.2f).copy(alpha = 0.7f), lados = 6, tapas = false)
                disco(v, V3(x, c.y + 0.155f, c.z), K.ARRIBA, 0.013f, C.ROJO, lados = 6)
            }
        }
    }

    /** La radio de la sala, encendida el día de la señal: la caja, el dial verde, la antena. */
    private fun DrawScope.radio(v: VistaRefugio, c: V3) {
        Trazo3.run {
            caja(v, c.x - 0.15f, c.x + 0.15f, c.y, c.y + 0.17f, c.z - 0.07f, c.z + 0.07f, lerp(C.COBRE, C.TINTA, 0.45f))
            cara(v, listOf(V3(c.x - 0.12f, c.y + 0.05f, c.z - 0.072f), V3(c.x + 0.02f, c.y + 0.05f, c.z - 0.072f), V3(c.x + 0.02f, c.y + 0.13f, c.z - 0.072f), V3(c.x - 0.12f, c.y + 0.13f, c.z - 0.072f)), K.FRENTE, lerp(C.ALGA, Color.White, 0.5f))
            barra(c + V3(0.1f, 0.17f, 0f), c + V3(0.2f, 0.55f, 0.05f), 0.006f, v.c(c, K.NADA, C.ACERO))
        }
        val d = K.p(V3(c.x - 0.05f, c.y + 0.09f, c.z - 0.075f))
        drawCircle(Brush.radialGradient(listOf(C.ALGA.copy(alpha = 0.5f), Color.Transparent), d, 0.25f * K.k(c)), 0.25f * K.k(c), d, blendMode = BlendMode.Plus)
    }

    /** El tocadiscos pequeño de la sala: la caja de madera con la tapa abierta, el plato con su disco negro y el brazo. */
    private fun DrawScope.tocadiscos(v: VistaRefugio, c: V3, sonando: Boolean) {
        val madera = lerp(C.COBRE, C.TINTA, 0.25f)
        Trazo3.run {
            caja(v, c.x - 0.17f, c.x + 0.17f, c.y, c.y + 0.1f, c.z - 0.12f, c.z + 0.12f, madera, lerp(madera, C.PAPEL, 0.15f))
            cara(v, listOf(V3(c.x - 0.17f, c.y + 0.1f, c.z + 0.12f), V3(c.x + 0.17f, c.y + 0.1f, c.z + 0.12f), V3(c.x + 0.17f, c.y + 0.36f, c.z + 0.16f), V3(c.x - 0.17f, c.y + 0.36f, c.z + 0.16f)), K.FRENTE, lerp(madera, C.TINTA, 0.15f))
            disco(v, c + V3(-0.03f, 0.105f, 0f), K.ARRIBA, 0.1f, C.TINTA, lados = 18)
            disco(v, c + V3(-0.03f, 0.108f, 0f), K.ARRIBA, 0.03f, if (sonando) C.ROJO else C.PAPEL, lados = 10)
            barra(c + V3(0.12f, 0.12f, 0.08f), c + V3(0.02f, 0.12f, -0.02f), 0.008f, v.c(c, K.ARRIBA, C.ACERO))
        }
        // Un disco en su funda de papel, apoyado.
        Trazo3.run { cara(v, listOf(V3(c.x + 0.19f, c.y, c.z + 0.1f), V3(c.x + 0.21f, c.y, c.z - 0.12f), V3(c.x + 0.21f, c.y + 0.25f, c.z - 0.1f), V3(c.x + 0.19f, c.y + 0.25f, c.z + 0.12f)), K.DER, lerp(C.PAPEL, C.AZUL, 0.25f)) }
    }

    /** El tocadiscos del comedor, roto, abierto en la bancada para arreglarlo (el día `tocadiscos`): la caja grande, el plato fuera, las piezas. */
    private fun DrawScope.tocadiscosGrande(v: VistaRefugio, c: V3) {
        val madera = lerp(C.COBRE, C.TINTA, 0.15f)
        Trazo3.run {
            caja(v, c.x - 0.28f, c.x + 0.22f, c.y, c.y + 0.16f, c.z - 0.17f, c.z + 0.15f, madera, lerp(C.TINTA, madera, 0.3f))
            cara(v, listOf(V3(c.x - 0.28f, c.y + 0.16f, c.z + 0.15f), V3(c.x + 0.22f, c.y + 0.16f, c.z + 0.15f), V3(c.x + 0.22f, c.y + 0.5f, c.z + 0.2f), V3(c.x - 0.28f, c.y + 0.5f, c.z + 0.2f)), K.FRENTE, lerp(madera, C.TINTA, 0.2f))
            cilindro(v, c + V3(0.32f, 0f, -0.05f), c + V3(0.32f, 0.02f, -0.05f), 0.15f, C.ACERO, lados = 18)
            for (k in 0..3) disco(v, c + V3(-0.1f + 0.07f * k, 0.165f, -0.05f + 0.03f * (k % 2)), K.ARRIBA, 0.025f, if (k % 2 == 0) C.LATON else C.COBRE, lados = 8)
        }
    }

    /** La botella de agua, de vidrio, con [cuanta] agua (de 0 a 1): en el racionamiento, «dos dedos». */
    private fun DrawScope.botella(v: VistaRefugio, c: V3, cuanta: Float, marron: Boolean = false) {
        val alto = 0.26f
        Trazo3.run {
            cilindro(v, c, c + V3(0f, alto * cuanta, 0f), 0.04f, if (marron) lerp(C.OXIDO, C.ROCA, 0.3f) else lerp(C.CHAPA, C.PISCINA, 0.4f), lados = 10)
            cilindro(v, c + V3(0f, alto * cuanta, 0f), c + V3(0f, alto, 0f), 0.04f, lerp(Color.White, C.PISCINA, 0.3f).copy(alpha = 0.55f), lados = 10, tapas = false)
            cilindro(v, c + V3(0f, alto, 0f), c + V3(0f, alto + 0.07f, 0f), 0.017f, lerp(Color.White, C.PISCINA, 0.3f).copy(alpha = 0.6f), lados = 8)
            disco(v, c + V3(0f, alto + 0.072f, 0f), K.ARRIBA, 0.018f, C.ROJO, lados = 8)
            barra(c + V3(-0.022f, 0.02f, -0.03f), c + V3(-0.022f, alto - 0.02f, -0.03f), 0.006f, Color.White.copy(alpha = 0.4f))
        }
    }

    /** La flor de verdad que trajo Noa, en un frasco (seca, un mes después). */
    private fun DrawScope.florEnFrasco(v: VistaRefugio, c: V3, seca: Boolean) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.12f, 0f), 0.035f, lerp(Color.White, C.PISCINA, 0.3f).copy(alpha = 0.6f), lados = 10)
            val tallo = c + V3(0.02f, 0.3f, -0.01f)
            barra(c + V3(0f, 0.1f, 0f), tallo, 0.006f, v.c(c, K.NADA, if (seca) lerp(C.ALGA, C.COBRE, 0.6f) else C.ALGA))
            for (k in 0 until 5) { val a = 2f * PI.toFloat() * k / 5; disco(v, tallo + V3(cos(a) * 0.022f, sin(a) * 0.012f, -0.005f), K.FRENTE, 0.017f, if (seca) lerp(C.COBRE, C.PAPEL, 0.3f) else lerp(C.AMARILLO, Color.White, 0.2f), lados = 8) }
            disco(v, tallo + V3(0f, 0f, -0.01f), K.FRENTE, 0.012f, C.NARANJA, lados = 8)
        }
    }

    /** El cuaderno del turno: abierto, con sus rayas sin letras y el lápiz; o cerrado, con su tapa verde gastada. */
    private fun DrawScope.cuaderno(v: VistaRefugio, c: V3, abierto: Boolean) {
        val y = c.y + 0.012f
        if (abierto) {
            for (lado in listOf(-1f, 1f)) {
                val xa = c.x + (if (lado < 0f) -0.2f else 0f); val xb = c.x + (if (lado < 0f) 0f else 0.2f)
                Trazo3.run { cara(v, listOf(V3(xa, y, c.z - 0.13f), V3(xb, y + 0.004f * -lado, c.z - 0.13f), V3(xb, y + 0.004f * -lado, c.z + 0.13f), V3(xa, y, c.z + 0.13f)), K.ARRIBA, lerp(C.PAPEL, Color.White, 0.25f)) }
                for (k in 1..7) { val z = c.z - 0.13f + 0.26f * k / 8f; Trazo3.run { barra(V3(xa + 0.02f, y + 0.002f, z), V3(xb - 0.02f, y + 0.002f, z), 0.004f, v.c(c, K.ARRIBA, lerp(C.AZUL, C.PAPEL, 0.4f)).copy(alpha = 0.7f)) } }
                // Lo escrito: rayitas de lápiz, sin letras.
                for (k in 1..5) { val z = c.z - 0.13f + 0.26f * k / 8f - 0.01f; val largo = 0.06f + 0.1f * ((k * 7 + (if (lado < 0f) 3 else 1)) % 5) / 5f; Trazo3.run { barra(V3(xa + 0.025f, y + 0.003f, z), V3(xa + 0.025f + largo, y + 0.003f, z), 0.005f, v.c(c, K.ARRIBA, C.TINTA).copy(alpha = 0.55f)) } }
            }
            Trazo3.run { barra(V3(c.x + 0.05f, y + 0.008f, c.z + 0.05f), V3(c.x + 0.22f, y + 0.008f, c.z - 0.06f), 0.012f, v.c(c, K.ARRIBA, C.AMARILLO)) }
        } else {
            Trazo3.run { caja(v, c.x - 0.13f, c.x + 0.13f, c.y, c.y + 0.03f, c.z - 0.11f, c.z + 0.11f, lerp(C.VERDE, C.TINTA, 0.35f), lerp(C.VERDE, C.PAPEL, 0.15f)) }
        }
    }

    /** La fiambrera de lata: cerrada, o abierta con su tapa al lado (la hora de comer). */
    private fun DrawScope.fiambrera(v: VistaRefugio, c: V3, abierta: Boolean) {
        Trazo3.run { caja(v, c.x - 0.1f, c.x + 0.1f, c.y, c.y + 0.07f, c.z - 0.07f, c.z + 0.07f, C.ACERO, if (abierta) lerp(C.ALGA, C.TINTA, 0.35f) else lerp(C.ACERO, Color.White, 0.2f)) }
        if (abierta) Trazo3.run { caja(v, c.x + 0.12f, c.x + 0.32f, c.y, c.y + 0.015f, c.z - 0.06f, c.z + 0.08f, lerp(C.ACERO, Color.White, 0.15f)); barra(c + V3(0.02f, 0.072f, 0f), c + V3(0.2f, 0.03f, -0.1f), 0.01f, v.c(c, K.NADA, C.ACERO)) }
    }

    /** Las gafas de cartón y celofán del sol falso. */
    private fun DrawScope.gafas(v: VistaRefugio, c: V3) {
        Trazo3.run {
            for (dx in listOf(-0.045f, 0.045f)) { disco(v, c + V3(dx, 0.006f, 0f), K.ARRIBA, 0.035f, lerp(C.PAPEL, C.TINTA, 0.1f), lados = 10); disco(v, c + V3(dx, 0.008f, 0f), K.ARRIBA, 0.025f, lerp(C.NARANJA, C.AMARILLO, 0.4f), lados = 10) }
            barra(c + V3(-0.08f, 0.006f, 0f), c + V3(-0.1f, 0.006f, 0.12f), 0.008f, v.c(c, K.ARRIBA, C.PAPEL))
        }
    }

    /** La guirnalda de flores de papel colgada del canto de la bancada, de [a] a [b]. */
    private fun DrawScope.guirnalda(v: VistaRefugio, a: V3, b: V3) {
        Trazo3.run { comba(a, b, 0.12f, 0.006f, v.c(K.medio(a, b), K.NADA, C.TINTA)) }
        val colores = listOf(C.ROJO, C.AMARILLO, C.AZUL, lerp(C.PAPEL, C.NARANJA, 0.4f), C.ALGA)
        for (k in 1..8) {
            val t = k / 9f; val q = V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t - 0.12f * 4f * t * (1f - t), a.z - 0.01f)
            Trazo3.run { disco(v, q, K.FRENTE, 0.035f, colores[k % colores.size], lados = 7); disco(v, q + K.FRENTE * 0.005f, K.FRENTE, 0.012f, C.PAPEL, lados = 6) }
        }
    }

    // ---------------------------------------------------------------- Lo del pasillo

    /**
     * Lo que hay en el suelo del pasillo según el día: el árbol de cable con sus lucecitas, el grifo medidor y las botellas
     * en fila del racionamiento, la ventanilla con botellas de la cuarentena, las cestas de algas de la cosecha, las flores
     * de papel del sellado y el barro de las botas, en la línea amarilla, y las hojas secas del «abierto».
     */
    fun DrawScope.delPasillo(v: VistaRefugio) {
        val m = v.m; val P = PasilloRefugio
        if (R.sellado(m)) {
            // Las flores de papel delante de la Puerta, en la línea que nadie pisa, y a lo largo de la línea amarilla, por donde pasa todo el refugio.
            for (k in 0..7) { val q = V3(P.CENTRO_X - 1.5f + 0.42f * k, 0.05f, P.LINEA_Z - 0.2f); Trazo3.run { disco(v, q, K.unidad(V3(0f, 0.6f, -0.8f)), 0.13f, listOf(C.ROJO, C.PAPEL, C.AMARILLO)[k % 3], true, 7) } }
            for (k in 0..14) { val q = V3(P.IZQ + 0.62f + 0.08f * (k % 2), 0.05f, P.BOCA + 0.5f + 1.6f * k); Trazo3.run { disco(v, q, K.unidad(V3(0.2f, 0.7f, -0.7f)), 0.17f, listOf(C.ROJO, C.PAPEL, C.AMARILLO, C.AZUL)[k % 4], true, 7); disco(v, q + V3(0f, 0.01f, -0.01f), K.unidad(V3(0.2f, 0.7f, -0.7f)), 0.04f, C.AMARILLO, true, 6) } }
        }
        if (R.barroDeBotas(m)) { val az = Azar(Semilla.de("refugio", "barro")); repeat(14) { val q = V3(P.IZQ + 0.6f + az.entre(-0.15f, 0.25f), 0.006f, P.BOCA + 3f + 1.4f * it); Trazo3.run { cara(v, listOf(q, q + V3(0.12f, 0f, 0f), q + V3(0.12f, 0f, 0.26f), q + V3(0f, 0f, 0.26f)), K.ARRIBA, C.BARRO, true, 0.7f) } } }
        if (R.hojasSecas(m)) { val az = Azar(Semilla.de(m.ficha.semilla, "hojas")); repeat(40) { val q = V3(az.entre(P.IZQ + 0.2f, P.DER - 0.2f), 0.005f, az.entre(P.BOCA, P.PUERTA_Z - 1f)); Trazo3.run { disco(v, q, K.ARRIBA, az.entre(0.05f, 0.11f), lerp(C.COBRE, C.AMARILLO, az.real()), true, 5) } } }
        if (R.cosecha(m)) for ((k, q) in listOf(V3(P.DER - 0.55f, 0f, 11.2f), V3(P.DER - 0.6f, 0f, 11.9f), V3(P.IZQ + 0.6f, 0f, 13.0f), V3(P.IZQ + 0.55f, 0f, 14.2f), V3(P.DER - 0.5f, 0f, 14.6f), V3(P.IZQ + 0.6f, 0f, P.VENTANAL_Z0 - 0.6f)).sortedByDescending { it.z }.withIndex()) cesta(v, q + V3(0.12f * (k % 2), 0f, 0f))
        if (R.botellasEnFila(m)) {
            // La cola sale hasta el umbral de la sala.
            for (k in 0..3) botellaEnSuelo(v, V3(S.PORTON_X0 + 0.3f + 0.42f * k, 0f, S.FONDO + 0.25f), k + 3)
            // El grifo medidor, en la pared de la izquierda, junto a la boca, y la fila de botellas que espera.
            val g = V3(P.IZQ + 0.1f, 1.0f, P.BOCA + 0.5f)
            Trazo3.run { caja(v, g.x, g.x + 0.18f, g.y - 0.15f, g.y + 0.25f, g.z - 0.15f, g.z + 0.15f, C.LATON, enPasillo = true); barra(g + V3(0.18f, 0f, 0f), g + V3(0.3f, -0.1f, 0f), 0.04f, v.c(g, K.NADA, C.LATON), StrokeCap.Butt); disco(v, g + V3(0.12f, 0.12f, -0.16f), K.FRENTE, 0.07f, lerp(C.PAPEL, Color.White, 0.3f), true, 12) }
            for (k in 0..11) botellaEnSuelo(v, V3(P.IZQ + 0.45f + 0.06f * (k % 2), 0f, P.BOCA + 0.7f + 0.5f * k), k)
        }
        if (R.mascarillas(m)) {
            // La ventanilla de la compuerta, con botellas y un paquete en su repisa.
            for (k in 0..2) botellaEnSuelo(v, V3(P.IZQ + 0.35f, 0f, P.COMPUERTA_Z1 + 0.3f + 0.3f * k), k)
            Trazo3.run { caja(v, P.IZQ + 0.2f, P.IZQ + 0.6f, 0f, 0.25f, P.COMPUERTA_Z1 + 1.3f, P.COMPUERTA_Z1 + 1.7f, lerp(C.PAPEL, C.COBRE, 0.4f), enPasillo = true) }
        }
        if (R.arbolDeCable(m)) arbol(v, P.ARBOL, R.lucesDelArbol(m))
    }

    /** Una botella en el suelo del pasillo (vacía o con agua). */
    private fun DrawScope.botellaEnSuelo(v: VistaRefugio, c: V3, k: Int) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.36f, 0f), 0.06f, lerp(Color.White, C.PISCINA, 0.35f).copy(alpha = 0.8f), lados = 8, enPasillo = true)
            cilindro(v, c + V3(0f, 0.36f, 0f), c + V3(0f, 0.45f, 0f), 0.022f, lerp(Color.White, C.PISCINA, 0.35f), lados = 6, enPasillo = true)
            disco(v, c + V3(0f, 0.452f, 0f), K.ARRIBA, 0.025f, if (k % 3 == 0) C.ROJO else C.AZUL, true, 6)
        }
    }

    /** Una cesta de algas de la cosecha, en el suelo del pasillo. */
    private fun DrawScope.cesta(v: VistaRefugio, c: V3) {
        Trazo3.run {
            cilindro(v, c, c + V3(0f, 0.32f, 0f), 0.25f, lerp(C.COBRE, C.PAPEL, 0.35f), lados = 14, enPasillo = true, tapas = false)
            disco(v, c + V3(0f, 0.33f, 0f), K.ARRIBA, 0.24f, lerp(C.ALGA, C.TINTA, 0.2f), true, 14)
        }
        val az = Azar(Semilla.de("refugio", "algas", (c.z * 10).toInt()))
        repeat(6) { val q = c + V3(az.entre(-0.15f, 0.15f), 0.36f, az.entre(-0.12f, 0.12f)); drawCircle(v.c(q, K.ARRIBA, C.ALGA, true), 0.05f * K.k(q), K.p(q)) }
    }

    /**
     * El árbol de Nochebuena: «hecho de cable verde y trozos de tubo, con luces pequeñas que solo se encienden esta noche y
     * bolas de papel de colores». Un tronco de tubo, ramas de cable en pisos, bolas de papel.
     */
    private fun DrawScope.arbol(v: VistaRefugio, pie: V3, luces: Boolean) {
        Trazo3.run { cilindro(v, pie, pie + V3(0f, 1.7f, 0f), 0.05f, C.ACERO, lados = 8, enPasillo = true) }
        val colores = listOf(C.ROJO, C.AMARILLO, C.AZUL, C.NARANJA)
        for (piso in 0..5) {
            val y = 0.35f + 0.24f * piso; val r = 0.55f - 0.08f * piso
            for (k in 0 until 7) {
                val a = 2f * PI.toFloat() * k / 7 + 0.4f * piso
                val punta = pie + V3(cos(a) * r, y - 0.12f, sin(a) * r)
                Trazo3.run { barra(pie + V3(0f, y, 0f), punta, 0.025f, v.c(punta, K.NADA, lerp(C.ALGA, C.CHAPA, 0.3f), true)) }
                if ((k + piso) % 3 == 0) Trazo3.run { disco(v, punta + V3(0f, -0.05f, 0f), K.FRENTE, 0.05f, colores[(k + piso) % 4], true, 8) }
                if (luces && (k + piso) % 2 == 0) { val p = K.p(punta); drawCircle(lerp(C.GUARDIA, Color.White, 0.5f), 0.02f * K.k(punta), p); drawCircle(Brush.radialGradient(listOf(C.GUARDIA.copy(alpha = 0.5f), Color.Transparent), p, 0.1f * K.k(punta)), 0.1f * K.k(punta), p, blendMode = BlendMode.Plus) }
            }
        }
        Trazo3.run { disco(v, pie + V3(0f, 1.75f, 0f), K.FRENTE, 0.07f, C.AMARILLO, true, 5) }
    }

    // ---------------------------------------------------------------- Las lámparas de trabajo

    /**
     * La lámpara de jaula: la pantallita de chapa, la bombilla y su jaula de alambre, el gancho, y el cable que sube a un
     * tubo y cuelga en comba hasta el enchufe. Encendida, donde está la obra; apagada, colgada donde quedó (sobre el filtro).
     */
    fun DrawScope.lamparas(v: VistaRefugio) {
        val encendidas = v.obras
        val sitios = if (encendidas.isEmpty()) listOf(Obra.FILTRO) else encendidas
        for (o in sitios) lampara(v, S.bombilla(o), S.haciaDonde(o), encendidas.isNotEmpty())
    }

    private fun DrawScope.lampara(v: VistaRefugio, b: V3, eje: V3, encendida: Boolean) {
        val gancho = V3(b.x, 2.84f, b.z)
        val hierro = v.c(b, K.NADA, C.HIERRO)
        Trazo3.run {
            // El cable: del gancho a la lámpara, y en comba hasta el enchufe del tubo.
            barra(gancho, b + V3(0f, 0.17f, 0f), 0.012f, hierro)
            comba(gancho, V3(b.x + 0.9f, 2.88f, b.z + 0.5f), 0.35f, 0.012f, hierro)
            aro(gancho + V3(0f, 0.03f, 0f), K.FRENTE, 0.03f, 0.008f, v.c(gancho, K.NADA, C.ACERO), 8)
        }
        // La pantallita de chapa (un cono chato), del lado contrario a donde alumbra, pintada de naranja por fuera.
        val atras = b - eje * 0.15f; val boca = b - eje * 0.05f
        val borde = K.circulo(boca, eje, 0.14f, 18); val cima = K.circulo(atras, eje, 0.045f, 18)
        val pantalla = poligono(K.casco((borde + cima).map { K.p(it) }))
        val bocaSeVe = K.seVe(boca, eje)
        // La bombilla y la jaula (detrás de la pantallita si la boca no mira a los ojos).
        val pb = K.p(b); val kb = K.k(b)
        val alambre = if (encendida) lerp(C.TINTA, C.LAMPARA, 0.25f) else v.c(b, K.NADA, C.LATON)
        fun jaula() {
            for (k in 0 until 6) {
                val a = 2f * PI.toFloat() * k / 6 + 0.3f
                val u = K.unidad(K.cruz(eje, if (kotlin.math.abs(eje.y) > 0.9f) K.DER else K.ARRIBA)); val w = K.cruz(eje, u)
                val d = u * cos(a) + w * sin(a)
                val arriba = b - eje * 0.05f + d * 0.13f; val medio = b + eje * 0.08f + d * 0.08f; val abajo = b + eje * 0.16f
                Trazo3.run { barra(arriba, medio, 0.008f, alambre); barra(medio, abajo, 0.008f, alambre) }
            }
            Trazo3.run { aro(b + eje * 0.05f, eje, 0.105f, 0.008f, alambre, 14) }
        }
        fun bombilla() {
            if (encendida) drawCircle(Brush.radialGradient(listOf(Color.White, lerp(C.LAMPARA, Color.White, 0.5f), C.LAMPARA.copy(alpha = 0f)), pb, 0.09f * kb), 0.09f * kb, pb)
            else drawCircle(v.c(b, K.NADA, lerp(C.PAPEL, C.TINTA, 0.35f)), 0.045f * kb, pb)
        }
        if (bocaSeVe) {
            drawPath(pantalla, v.c(atras, eje * -1f, lerp(C.NARANJA, C.TINTA, 0.2f)))
            if (encendida) drawPath(K.forma(borde), lerp(C.LAMPARA, Color.White, 0.25f).copy(alpha = 0.9f))
            bombilla(); jaula()
        } else {
            // La boca mira hacia la pared: se ve la pantallita por detrás, con la luz que se escapa por el borde.
            bombilla(); jaula()
            if (encendida) drawCircle(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.55f), Color.Transparent), pb, 0.32f * kb), 0.32f * kb, pb, blendMode = BlendMode.Plus)
            drawPath(pantalla, v.c(atras, eje * -1f, lerp(C.NARANJA, C.TINTA, 0.2f)))
            if (encendida) drawPath(pantalla, lerp(C.LAMPARA, Color.White, 0.4f).copy(alpha = 0.6f), style = Stroke(0.9f))
        }
    }
}
