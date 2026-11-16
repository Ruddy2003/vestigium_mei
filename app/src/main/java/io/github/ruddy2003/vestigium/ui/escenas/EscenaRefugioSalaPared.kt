package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo de la pared del fondo de la sala de bombas: los carteles de antes de la guerra, el cuadro de palancas con sus pilotos,
 * la pared de manómetros (seis esferas grandes de latón), la columna de nivel de la cisterna, la llave de paso de las
 * duchas, la lucecita de guardia y la baliza; en el quicio de la derecha, el interruptor, las linternas, el mono, las
 * mascarillas y el extintor. Todo en metros, en la pared de [SalaRefugio.FONDO].
 */
internal object PiezasPared {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio
    private val S = SalaRefugio

    /** Dónde está la llave de paso de las duchas (la del candado), junto a la jamba izquierda del portón. */
    val LLAVE_DE_PASO = V3(-3.1f, 1.05f, S.FONDO - 0.1f)

    fun DrawScope.enLaPared(v: VistaRefugio) {
        cartel(v, -8.4f, -7.55f, 1.45f, 2.55f, 0)
        cartel(v, 1.12f, 1.62f, 1.95f, 2.55f, 1)
        cartel(v, -9.8f, -9.0f, 1.25f, 2.2f, 2)
        armario(v)
        tablero(v)
        columna(v)
        llaveDePaso(v)
        guardia(v)
        quicio(v)
    }

    // ---------------------------------------------------------------- Carteles y armario

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
                val formas = listOf(Triple(0.32f, 0.62f, 0.2f), Triple(0.62f, 0.42f, 0.24f), Triple(0.4f, 0.25f, 0.12f))
                val colores = listOf(C.ROJO, C.AZUL, C.AMARILLO)
                for ((i, f) in formas.withIndex()) Trazo3.run { disco(v, q(f.first, f.second), n, w * f.third, colores[i], lados = 14) }
            }
        }
        // La mancha de humedad y el cerco amarillo del papel viejo.
        Trazo3.run { cara(v, listOf(q(0f, 0f), q(0.5f, 0f), q(0.3f, 0.18f), q(0f, 0.25f)), n, lerp(C.ROCA, C.AMARILLO, 0.3f), alfa = 0.35f) }
    }

    /** El cuadro de palancas: un armario de chapa con tres palancas, los pilotos (verde de siempre, ámbar, rojo) y un reloj. */
    private fun DrawScope.armario(v: VistaRefugio) {
        val x0 = S.ARMARIO_X0; val x1 = S.ARMARIO_X1; val z1 = S.FONDO; val z0 = z1 - 0.42f; val alto = 1.95f
        Trazo3.run { caja(v, x0, x1, 0.05f, alto, z0, z1, C.CHAPA, lerp(C.CHAPA, C.ACERO, 0.3f)) }
        Trazo3.run { caja(v, x0 - 0.03f, x1 + 0.03f, 0f, 0.08f, z0 - 0.03f, z1, C.HIERRO) }
        for (k in 0..2) {
            val x = x0 + 0.2f + 0.27f * k; val arriba = (k + v.m.ficha.semilla.toInt()) % 3 != 0
            val a = V3(x, 1.2f, z0 - 0.02f); val b = a + V3(0f, if (arriba) 0.28f else -0.22f, -0.12f)
            Trazo3.run { caja(v, x - 0.05f, x + 0.05f, 1.1f, 1.3f, z0 - 0.04f, z0, C.HIERRO); barra(a, b, 0.035f, v.c(a, K.FRENTE, C.ACERO)); disco(v, b, K.FRENTE, 0.045f, C.ROJO, lados = 8) }
        }
        val aviso = v.estado == EscenaRefugio.Estado.RACIONAMIENTO || v.estado == EscenaRefugio.Estado.CUARENTENA || v.alarma
        for ((k, col) in listOf(lerp(C.ALGA, Color.White, 0.3f), C.GUARDIA, C.ALARMA).withIndex()) {
            val prendido = k == 0 || (k == 1 && (aviso || !v.dia)) || (k == 2 && v.alarma)
            piloto(v, V3(x0 + 0.25f + 0.22f * k, 1.62f, z0 - 0.02f), K.FRENTE, col, prendido, 0.045f)
        }
        PiezasMaquinas.run { manometro(v, V3((x0 + x1) / 2f, 0.75f, z0 - 0.02f), 0.15f, 0.4f) }
    }

    /**
     * Un piloto de [color] en [c], mirando a [n], de radio [r]: el casquillo negro y la lente; encendido, con su halo (más
     * grande de noche, cuando los pilotos son de las pocas luces cálidas de la sala).
     */
    fun DrawScope.piloto(v: VistaRefugio, c: V3, n: V3, color: Color, prendido: Boolean, r: Float = 0.04f) {
        Trazo3.run { disco(v, c - n * 0.005f, n, r * 1.45f, C.TINTA, lados = 10) }
        Trazo3.run { disco(v, c, n, r, if (prendido) lerp(color, Color.White, 0.3f) else lerp(color, C.TINTA, 0.6f), lados = 10, luz = !prendido) }
        if (!prendido) return
        val p = K.p(c); val halo = (if (v.dia) 0.1f else 0.2f) * K.k(c)
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = 0.7f), color.copy(alpha = 0.18f), Color.Transparent), p, halo), halo, p, blendMode = BlendMode.Plus)
    }

    // ---------------------------------------------------------------- La pared de manómetros

    /** Las seis esferas del tablero: (de 0 a 1 a lo ancho, altura, radio). */
    private val ESFERAS = listOf(Triple(0.17f, 2.24f, 0.24f), Triple(0.5f, 2.2f, 0.28f), Triple(0.83f, 2.25f, 0.22f), Triple(0.2f, 1.58f, 0.25f), Triple(0.52f, 1.53f, 0.21f), Triple(0.84f, 1.6f, 0.24f))

    /**
     * La pared de manómetros: una chapa verde azulada remachada con seis manómetros grandes de latón, cada uno de su tamaño,
     * con su aguja (sin números), los tubitos de cobre que los alimentan desde el colector y la placa de latón sin letras.
     * Las agujas de la primera fila leen el agua: bajan con la reserva.
     */
    private fun DrawScope.tablero(v: VistaRefugio) {
        val x0 = S.TABLERO_X0; val x1 = S.TABLERO_X1; val z = S.FONDO - 0.04f
        Trazo3.run { caja(v, x0, x1, 1.08f, 2.66f, z - 0.04f, z, lerp(C.CHAPA, C.TINTA, 0.12f)) }
        for (x in listOf(x0 + 0.06f, x1 - 0.06f)) for (y in listOf(1.14f, 2.6f)) drawCircle(v.c(V3(x, y, z - 0.05f), K.FRENTE, C.ACERO), 0.022f * K.k(V3(x, y, z)), K.p(V3(x, y, z - 0.05f)))
        Trazo3.run { cilindro(v, V3(x0 + 0.05f, 1.2f, z - 0.13f), V3(x1 - 0.05f, 1.2f, z - 0.13f), 0.05f, C.COBRE, lados = 10) }
        for ((u, y, r) in ESFERAS) {
            val x = x0 + (x1 - x0) * u
            Trazo3.run { barra(V3(x, 1.2f, z - 0.13f), V3(x, y - r, z - 0.1f), 0.026f, v.c(V3(x, (y + 1.2f) / 2f, z), K.FRENTE, C.COBRE)) }
        }
        val az = SalaRefugio.azar("manometros")
        for ((i, e) in ESFERAS.withIndex()) {
            val (u, y, r) = e
            val lee = if (i < 3) (v.reserva * (0.75f + 0.25f * az.real())).coerceIn(0.04f, 0.96f) else (0.3f + 0.55f * az.real())
            PiezasMaquinas.run { manometro(v, V3(x0 + (x1 - x0) * u, y, z - 0.1f), r, lee) }
        }
        Trazo3.run { cara(v, listOf(V3(x0 + 0.55f, 2.55f, z - 0.045f), V3(x1 - 0.55f, 2.55f, z - 0.045f), V3(x1 - 0.55f, 2.63f, z - 0.045f), V3(x0 + 0.55f, 2.63f, z - 0.045f)), K.FRENTE, C.LATON) }
    }

    // ---------------------------------------------------------------- La columna de nivel y la llave de paso

    /**
     * La columna de nivel de la cisterna grande: un tubo de vidrio grueso y alto entre sus dos llaves de latón, delante de
     * una regla esmaltada con rayitas (sin números) y la marca roja de punta a punta; dentro, el agua clara hasta su nivel
     * (el arco `reserva`) y el flotador rojo, y encima del nivel el vidrio vacío, oscuro. Con la cisterna baja, el piloto
     * rojo de arriba se enciende.
     */
    private fun DrawScope.columna(v: VistaRefugio) {
        val x = S.COLUMNA_X; val z = S.FONDO - 0.02f; val y0 = 0.25f; val y1 = 2.75f
        val n = K.FRENTE
        Trazo3.run { cara(v, listOf(V3(x - 0.2f, y0 - 0.06f, z), V3(x + 0.2f, y0 - 0.06f, z), V3(x + 0.2f, y1 + 0.06f, z), V3(x - 0.2f, y1 + 0.06f, z)), n, C.HIERRO) }
        Trazo3.run { cara(v, listOf(V3(x + 0.02f, y0, z - 0.01f), V3(x + 0.18f, y0, z - 0.01f), V3(x + 0.18f, y1, z - 0.01f), V3(x + 0.02f, y1, z - 0.01f)), n, lerp(C.PAPEL, Color.White, 0.3f)) }
        for (k in 0..20) { val y = y0 + (y1 - y0) * k / 20f; val largo = if (k % 5 == 0) 0.12f else 0.06f; Trazo3.run { barra(V3(x + 0.18f - largo, y, z - 0.015f), V3(x + 0.18f, y, z - 0.015f), 0.016f, v.c(V3(x, y, z), n, C.TINTA)) } }
        // La marca roja: una franja que cruza la regla y el tubo.
        val ym = y0 + (y1 - y0) * EscenaRefugio.MARCA_ROJA
        Trazo3.run { cara(v, listOf(V3(x - 0.2f, ym - 0.04f, z - 0.02f), V3(x + 0.2f, ym - 0.04f, z - 0.02f), V3(x + 0.2f, ym + 0.04f, z - 0.02f), V3(x - 0.2f, ym + 0.04f, z - 0.02f)), n, C.ROJO) }
        val tz = z - 0.2f; val tx = x - 0.06f; val r = 0.1f
        val nivel = y0 + (y1 - y0) * v.reserva
        Trazo3.run {
            cilindro(v, V3(tx, y0, tz), V3(tx, nivel, tz), r, lerp(C.PISCINA, Color.White, 0.35f), lados = 14)
            cilindro(v, V3(tx, nivel, tz), V3(tx, y1, tz), r, lerp(C.TINTA, C.CHAPA, 0.35f), lados = 14, tapas = false)
            barra(V3(tx - 0.05f, y0, tz - 0.09f), V3(tx - 0.05f, y1, tz - 0.09f), 0.016f, v.c(V3(tx, 1.5f, tz), n, Color.White).copy(alpha = 0.55f))
            aro(V3(tx, ym, tz), K.ARRIBA, r + 0.01f, 0.03f, v.c(V3(tx, ym, tz), n, C.ROJO), 14)
            // El flotador: un disco rojo y naranja, gordo, en la raya del agua.
            cilindro(v, V3(tx, nivel - 0.03f, tz), V3(tx, nivel + 0.09f, tz), r * 0.85f, lerp(C.ROJO, C.NARANJA, 0.45f), lados = 12)
            cilindro(v, V3(tx, y0 - 0.16f, tz), V3(tx, y0, tz), 0.11f, C.LATON, lados = 12)
            cilindro(v, V3(tx, y1, tz), V3(tx, y1 + 0.16f, tz), 0.11f, C.LATON, lados = 12)
            cilindro(v, V3(tx, y0 - 0.08f, tz), V3(tx, y0 - 0.08f, z), 0.05f, C.COBRE, lados = 8, tapas = false)
            cilindro(v, V3(tx, y1 + 0.08f, tz), V3(tx, y1 + 0.08f, z), 0.05f, C.COBRE, lados = 8, tapas = false)
            for (dx in listOf(-0.11f, 0.1f)) barra(V3(tx + dx, y0, tz - 0.06f), V3(tx + dx, y1, tz - 0.06f), 0.016f, v.c(V3(tx, 1.5f, tz), n, C.ACERO))
        }
        // El brillo del agua en la raya del nivel.
        Trazo3.run { barra(V3(tx - r, nivel, tz - 0.05f), V3(tx + r, nivel, tz - 0.05f), 0.02f, lerp(C.PISCINA, Color.White, 0.7f)) }
        val bajo = EscenaRefugio.tramoReserva(v.m) == EscenaRefugio.TramoReserva.BAJA
        piloto(v, V3(x + 0.08f, y1 + 0.22f, z - 0.02f), n, C.ALARMA, bajo, 0.055f)
    }

    /**
     * La llave de paso de las duchas, junto a la jamba izquierda del portón, con su rueda roja y su grifo: en el
     * racionamiento, la rueda trabada con una cadena gruesa y un candado grande de latón («las duchas cerradas con candado»).
     */
    private fun DrawScope.llaveDePaso(v: VistaRefugio) {
        val c = LLAVE_DE_PASO
        Trazo3.run {
            cilindro(v, V3(c.x, 2.8f, S.FONDO - 0.08f), V3(c.x, 0.2f, S.FONDO - 0.08f), 0.055f, C.VERDE, lados = 10)
            cilindro(v, c + V3(0f, -0.12f, 0f), c + V3(0f, 0.12f, 0f), 0.1f, lerp(C.VERDE, C.HIERRO, 0.3f), lados = 12)
            cilindro(v, c + V3(0f, -0.35f, 0f), c + V3(0f, -0.35f, -0.22f), 0.03f, C.LATON, lados = 8)
        }
        PiezasMaquinas.run { rueda(v, c + V3(0f, 0f, -0.18f), K.FRENTE, 0.17f) }
        if (!R.candado(v.m)) return
        val k = c + V3(0f, 0f, -0.21f); val acero = v.c(k, K.FRENTE, lerp(C.ACERO, Color.White, 0.1f))
        Trazo3.run {
            comba(k + V3(-0.17f, 0.08f, 0f), k + V3(0.15f, -0.1f, 0f), 0.08f, 0.026f, acero)
            comba(k + V3(-0.17f, 0.08f, 0f), V3(c.x - 0.04f, 0.6f, S.FONDO - 0.12f), 0.05f, 0.026f, acero)
            aro(k + V3(0.12f, -0.17f, -0.01f), K.FRENTE, 0.05f, 0.02f, acero, 12)
            caja(v, k.x + 0.04f, k.x + 0.2f, k.y - 0.36f, k.y - 0.2f, k.z - 0.03f, k.z + 0.03f, C.LATON, lerp(C.LATON, Color.White, 0.3f))
            disco(v, V3(k.x + 0.12f, k.y - 0.3f, k.z - 0.035f), K.FRENTE, 0.018f, C.TINTA, lados = 8)
        }
    }

    // ---------------------------------------------------------------- La guardia y la baliza

    /** La lucecita de guardia (una jaulita con su bombilla ámbar) y la baliza de la alarma, apagada o encendida. */
    private fun DrawScope.guardia(v: VistaRefugio) {
        val g = S.GUARDIA
        Trazo3.run {
            disco(v, g + K.FRENTE * 0.02f, K.FRENTE, 0.12f, C.HIERRO, lados = 12)
            disco(v, g + K.FRENTE * 0.08f, K.FRENTE, 0.075f, lerp(C.GUARDIA, Color.White, 0.3f * v.guardia), lados = 12, luz = false)
            for (k in 0 until 4) { val a = PI.toFloat() * k / 4f; barra(g + K.FRENTE * 0.1f + V3(cos(a) * 0.1f, sin(a) * 0.1f, 0f), g + K.FRENTE * 0.1f - V3(cos(a) * 0.1f, sin(a) * 0.1f, 0f), 0.008f, C.TINTA) }
        }
        val halo = (if (v.dia) 0.5f else 0.9f) * K.k(g)
        drawCircle(Brush.radialGradient(listOf(C.GUARDIA.copy(alpha = 0.55f * v.guardia), C.GUARDIA.copy(alpha = 0.12f * v.guardia), Color.Transparent), K.p(g), halo), halo, K.p(g), blendMode = BlendMode.Plus)
        val b = S.BALIZA
        Trazo3.run {
            cilindro(v, b + V3(0f, -0.08f, 0f), b + V3(0f, 0.08f, 0f), 0.09f, if (v.alarma) lerp(C.ALARMA, Color.White, 0.25f) else lerp(C.ROJO, C.TINTA, 0.35f), lados = 10)
            caja(v, b.x - 0.1f, b.x + 0.1f, b.y - 0.13f, b.y - 0.08f, b.z - 0.08f, b.z + 0.1f, C.HIERRO)
        }
    }

    // ---------------------------------------------------------------- El quicio

    /**
     * El quicio de la derecha del portón (cortado por el borde): el interruptor de la sala, la percha de las linternas de
     * dinamo, el gancho del mono azul (cuando cuelga) y, en la cuarentena, las mascarillas de tela en su cuerda; abajo, el
     * extintor.
     */
    private fun DrawScope.quicio(v: VistaRefugio) {
        val z = S.FONDO - 0.04f; val n = K.FRENTE
        Trazo3.run { caja(v, 1.22f, 1.42f, 1.35f, 1.62f, z - 0.1f, z, C.HIERRO); barra(V3(1.32f, 1.5f, z - 0.11f), V3(1.32f, 1.6f, z - 0.16f), 0.025f, v.c(V3(1.32f, 1.5f, z), n, C.ACERO)) }
        Trazo3.run { barra(V3(1.32f, 1.62f, z - 0.03f), V3(1.32f, S.TECHO, z - 0.03f), 0.03f, v.c(V3(1.32f, 2.4f, z), n, C.ACERO)) }
        Trazo3.run { barra(V3(1.12f, 2.05f, z - 0.05f), V3(1.75f, 2.05f, z - 0.05f), 0.025f, v.c(V3(1.4f, 2.05f, z), n, C.ACERO)) }
        if (R.laSalaPinta(v.m, "linterna")) for (x in listOf(1.2f, 1.36f)) linterna(v, V3(x, 2.0f, z - 0.1f))
        val g = S.GANCHO
        Trazo3.run { barra(g, g + V3(0f, 0.04f, -0.08f), 0.025f, v.c(g, n, C.ACERO)) }
        if (R.monoColgado(v.m) && R.laSalaPinta(v.m, "mono")) mono(v, g + V3(0f, 0f, -0.1f))
        if (R.mascarillas(v.m)) {
            // La cuerda que cruza la jamba, con las mascarillas colgadas a secar.
            val a = V3(S.PORTON_X1 + 0.05f, 2.3f, z - 0.08f); val b = V3(1.7f, 2.32f, z - 0.08f)
            Trazo3.run { comba(a, b, 0.08f, 0.01f, v.c(a, n, C.PAPEL)) }
            for (k in 0..2) { val t = (k + 0.7f) / 3.4f; mascarilla(v, V3(a.x + (b.x - a.x) * t, a.y - 0.08f * 4f * t * (1f - t), z - 0.09f), k) }
        }
        if (R.luto(v.m)) {
            val pts = listOf(g + V3(-0.12f, 0f, -0.12f), g + V3(0.12f, 0f, -0.12f), g + V3(0.16f, -0.6f, -0.14f), g + V3(-0.1f, -0.55f, -0.14f))
            Trazo3.run { cara(v, pts, n, lerp(C.TINTA, C.AZUL, 0.25f)); disco(v, g + V3(0f, -0.1f, -0.16f), n, 0.06f, lerp(C.PAPEL, C.ROJO, 0.3f), lados = 8) }
        }
        Trazo3.run { cilindro(v, V3(1.62f, 0.12f, z - 0.2f), V3(1.62f, 0.78f, z - 0.2f), 0.11f, C.ROJO, lados = 12); cilindro(v, V3(1.62f, 0.78f, z - 0.2f), V3(1.62f, 0.86f, z - 0.2f), 0.04f, C.HIERRO, lados = 8) }
    }

    /** Una linterna de dinamo colgada: el cuerpo, la manivela plegada y el cristal. */
    private fun DrawScope.linterna(v: VistaRefugio, c: V3) {
        Trazo3.run {
            barra(c + V3(0f, 0.05f, 0f), c, 0.01f, v.c(c, K.FRENTE, C.TINTA))
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
            cara(v, listOf(g + V3(-0.12f, -1.0f, -0.035f), g + V3(-0.04f, -1.0f, -0.035f), g + V3(-0.04f, -1.12f, -0.035f), g + V3(-0.12f, -1.12f, -0.035f)), n, lerp(C.AZUL, C.PAPEL, 0.3f))
            cara(v, listOf(g + V3(0.06f, -0.3f, -0.03f), g + V3(0.16f, -0.3f, -0.03f), g + V3(0.16f, -0.42f, -0.03f), g + V3(0.06f, -0.42f, -0.03f)), n, lerp(C.AZUL, C.TINTA, 0.35f))
            barra(g + V3(0f, -0.08f, -0.02f), g + V3(0f, -0.8f, -0.03f), 0.012f, v.c(g, n, lerp(C.ACERO, C.LATON, 0.4f)))
        }
    }

    /** Una mascarilla de tela colgada de su cinta: grande, de tela clara con sus pliegues. */
    private fun DrawScope.mascarilla(v: VistaRefugio, c: V3, k: Int) {
        val n = K.FRENTE; val tela = lerp(C.PAPEL, if (k % 2 == 0) C.AZUL else C.ALGA, 0.22f)
        Trazo3.run {
            barra(c, c + V3(-0.08f, -0.1f, 0f), 0.008f, v.c(c, n, C.PAPEL)); barra(c, c + V3(0.08f, -0.1f, 0f), 0.008f, v.c(c, n, C.PAPEL))
            caraDegradada(v, listOf(c + V3(-0.11f, -0.1f, -0.01f), c + V3(0.11f, -0.1f, -0.01f), c + V3(0.1f, -0.26f, -0.02f), c + V3(0f, -0.28f, -0.025f), c + V3(-0.1f, -0.26f, -0.02f)), n, tela, c + V3(0f, -0.1f, 0f), c + V3(0f, -0.28f, 0f))
            for (j in 1..2) barra(c + V3(-0.1f, -0.1f - 0.055f * j, -0.022f), c + V3(0.1f, -0.1f - 0.055f * j, -0.022f), 0.007f, v.c(c, n, lerp(tela, C.TINTA, 0.3f)))
        }
    }

    /** El halo de una luz en el aire, en [c], de [radio] metros (lo usan el piloto y la guardia). */
    internal fun DrawScope.halo(c: V3, radio: Float, color: Color, fuerza: Float) {
        val p = K.p(c); val r = radio * K.k(c)
        if (r < 0.5f || fuerza <= 0.01f) return
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = fuerza.coerceIn(0f, 1f)), Color.Transparent), p, r), r, p, blendMode = BlendMode.Plus)
    }
}
