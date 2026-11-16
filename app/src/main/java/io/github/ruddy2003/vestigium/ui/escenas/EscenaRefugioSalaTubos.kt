package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real

/**
 * Los tubos del techo de la sala de bombas, «tubos de todos los tamaños»: de acero, de cobre, pintados de verde y forrados
 * de tela con sus bandas, con sus bridas y sus abrazaderas colgadas del techo. Bajan en diagonal desde la esquina de arriba
 * a la izquierda hacia el portón (la contradiagonal del suelo) y uno gordo cruza el cuadro y sale por el portón al pasillo,
 * donde sube a la bóveda (lo que cruza y enmarca, regla 6). Dos cruzan la sala a lo ancho, y de ellos bajan los tubos de la
 * columna de nivel y de las bombas.
 */
internal object PiezasTubos {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val S = SalaRefugio
    private val P = PasilloRefugio

    /** Un tubo recto de [a] a [b], de radio [r], de su [color]: con bridas cada [bridas] metros (0: ninguna) y, si está [forrado], con bandas. */
    private class Tubo(val a: V3, val b: V3, val r: Float, val color: Color, val bridas: Float = 0f, val forrado: Boolean = false, val pasillo: Boolean = false)

    /** El tubo gordo: hasta dónde llega en la sala (pasa por el portón) y por dónde sigue en el pasillo. */
    private const val GORDO_X = -1.45f
    private const val GORDO_Y = 2.56f
    private const val GORDO_R = 0.16f
    private const val GORDO_SUBE = 8.75f
    private const val GORDO_ARRIBA = 4.08f

    private val TUBOS: List<Tubo> by lazy {
        listOf(
            Tubo(V3(-3.75f, 2.98f, -1.5f), V3(-3.75f, 2.98f, S.FONDO), 0.1f, C.ACERO, 1.7f),
            Tubo(V3(-3.25f, 3.07f, -1.5f), V3(-3.25f, 3.07f, S.FONDO), 0.05f, C.COBRE),
            Tubo(V3(-2.45f, 2.82f, -1.5f), V3(-2.45f, 2.82f, S.FONDO), 0.14f, C.VERDE, 2.1f),
            Tubo(V3(-0.35f, 2.97f, -1.5f), V3(-0.35f, 2.97f, S.FONDO), 0.13f, lerp(C.PAPEL, C.ROCA, 0.3f), 0f, forrado = true),
            Tubo(V3(0.55f, 3.04f, -1.5f), V3(0.55f, 3.04f, S.FONDO), 0.075f, lerp(C.COBRE, C.TINTA, 0.25f)),
            Tubo(V3(GORDO_X, GORDO_Y, -1.5f), V3(GORDO_X, GORDO_Y, P.BOCA), GORDO_R, lerp(C.ACERO, C.PAPEL, 0.25f), 2.4f),
            // Los que cruzan la sala a lo ancho, cerca de la pared del fondo y por en medio.
            Tubo(V3(S.IZQ, 2.86f, 6.85f), V3(S.COLUMNA_X - 0.05f, 2.86f, 6.85f), 0.09f, C.VERDE, 2.2f),
            Tubo(V3(S.IZQ, 3.04f, 4.35f), V3(S.DER, 3.04f, 4.35f), 0.055f, C.COBRE),
        )
    }

    /** Los tubos del techo de la sala, de los más lejanos a los más cercanos, y las bajadas. */
    fun DrawScope.tubos(v: VistaRefugio) {
        // Las bajadas: a la columna de nivel y al tablero de manómetros, desde el tubo que cruza.
        Trazo3.run {
            cilindro(v, V3(S.COLUMNA_X - 0.05f, 2.86f, 6.85f), V3(S.COLUMNA_X - 0.05f, 2.86f, S.FONDO - 0.16f), 0.05f, C.COBRE, lados = 10)
            cilindro(v, V3(S.COLUMNA_X - 0.05f, 2.82f, S.FONDO - 0.16f), V3(S.COLUMNA_X - 0.05f, 2.92f, S.FONDO - 0.16f), 0.07f, C.LATON, lados = 10)
            cilindro(v, V3(-5.0f, 2.86f, 6.85f), V3(-5.0f, 2.62f, 6.85f), 0.06f, C.VERDE, lados = 10)
            cilindro(v, V3(-5.0f, 2.62f, 6.85f), V3(-5.0f, 2.62f, S.FONDO - 0.06f), 0.06f, C.VERDE, lados = 10)
        }
        // El tubo que sube de la bomba grande y entra, con una te, en el tubo verde.
        Trazo3.run { cilindro(v, V3(S.BOMBA.x + 0.35f, 2.82f, 3.27f), V3(-2.45f, 2.82f, 3.27f), 0.13f, C.VERDE, lados = 14) }
        for (t in TUBOS.sortedByDescending { K.hondo(K.medio(it.a, it.b)) }) tubo(v, t)
        // Las abrazaderas: varillas del techo a cada tubo, cada tanto (solo las que quedan lejos de los ojos).
        val varilla = v.c(V3(-2f, 3f, 4f), K.NADA, lerp(C.HIERRO, C.ACERO, 0.3f))
        for (t in TUBOS) {
            var z = 2.2f
            while (z < S.FONDO - 0.3f) {
                val q = if (t.a.z == t.b.z) V3(t.a.x + (t.b.x - t.a.x) * ((z - 2.2f) / 9f).coerceIn(0f, 1f), t.a.y, t.a.z) else V3(t.a.x, t.a.y, z)
                if (q.y + t.r < S.TECHO - 0.02f && K.hondo(q) > 2f) Trazo3.run { barra(q + V3(0f, t.r, 0f), V3(q.x, S.TECHO, q.z), 0.018f, varilla) }
                z += 1.6f
            }
        }
    }

    private fun DrawScope.tubo(v: VistaRefugio, t: Tubo) {
        val eje = K.unidad(t.b - t.a); val largo = K.largo(t.b - t.a)
        // Por tramos entre brida y brida, del más lejano al más cercano: así el tramo cercano tapa la brida que tiene detrás.
        val cortes = buildList { add(0f); if (t.bridas > 0f) { var d = t.bridas * 0.6f; while (d < largo - 0.2f) { add(d); d += t.bridas } }; add(largo) }
        val piezas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        for (i in 0 until cortes.size - 1) {
            val p0 = t.a + eje * cortes[i]; val p1 = t.a + eje * cortes[i + 1]
            piezas += K.hondo(K.medio(p0, p1)) to { Trazo3.run { cilindro(v, p0, p1, t.r, t.color, lerp(t.color, C.TINTA, 0.3f), lados = if (t.r > 0.1f) 16 else 10, enPasillo = t.pasillo, tapas = false) } }
        }
        for (d in cortes.drop(1).dropLast(1)) {
            val c = t.a + eje * d
            if (K.hondo(c) < 1.2f) continue
            piezas += K.hondo(c) to {
                Trazo3.run { cilindro(v, c - eje * 0.03f, c + eje * 0.03f, t.r * 1.45f, lerp(t.color, C.HIERRO, 0.3f), lados = 14, enPasillo = t.pasillo) }
                for (q in K.circulo(c + eje * 0.035f, eje, t.r * 1.2f, 6).dropLast(1)) if (K.seVe(q, K.unidad(q - c))) drawCircle(v.c(q, K.NADA, C.OXIDO, t.pasillo), 0.018f * K.k(q), K.p(q))
            }
        }
        for ((_, pinta) in piezas.sortedByDescending { it.first }) pinta()
        if (t.forrado) {
            var d = 0.4f
            while (d < largo) {
                val c = t.a + eje * d
                // Las bandas del forro: solo la mitad de abajo, la que se ve, y no en lo muy cercano.
                if (K.hondo(c) > 2.6f) {
                    val medio = K.circulo(c, eje, t.r + 0.006f, 10, PI.toFloat(), 2f * PI.toFloat()).map { K.p(it) }
                    val camino = Path().apply { moveTo(medio[0].x, medio[0].y); for (i in 1 until medio.size) lineTo(medio[i].x, medio[i].y) }
                    drawPath(camino, v.c(c + V3(0f, -t.r, 0f), K.ABAJO, lerp(C.PAPEL, C.TINTA, 0.35f)), style = Stroke(0.035f * K.k(c)))
                }
                d += 0.55f
            }
        }
        // El filo de luz de abajo: lo que recibe de la lámpara y del portón.
        val bajo = V3(0f, -t.r * 0.92f, 0f)
        val ritmo = -eje.x * K.SIN + eje.z * K.COS
        val desde = if (K.hondo(t.a) < 1f && ritmo > 0.01f) t.a + eje * ((1f - K.hondo(t.a)) / ritmo) else t.a
        Trazo3.run { barra(desde + bajo, t.b + bajo, t.r * 0.18f, v.c(K.medio(desde, t.b) + bajo, K.ABAJO, lerp(t.color, Color.White, 0.3f), t.pasillo).copy(alpha = 0.55f), StrokeCap.Butt) }
    }

    /**
     * Lo de arriba que cambia de un día a otro (regla 2, «la mitad de arriba… cambia de un día a otro»): los trapos que ella
     * pone a secar colgados del tubo gordo (de ninguno a tres, cada día en otro sitio) **[Diseño propio]**.
     */
    fun DrawScope.delante(v: VistaRefugio) {
        val az = v.m.azar("trapos")
        val cuantos = (az.real() * 3.6f).toInt()
        val colores = listOf(lerp(C.PAPEL, C.ROCA, 0.3f), lerp(C.ROJO, C.PAPEL, 0.45f), lerp(C.AZUL, C.PAPEL, 0.35f))
        repeat(cuantos) { k ->
            val z = 3.4f + 1.15f * k + az.entre(0f, 0.6f)
            val c = V3(GORDO_X, GORDO_Y + GORDO_R, z)
            val ancho = az.entre(0.22f, 0.34f); val cae = az.entre(0.35f, 0.55f)
            val n = K.unidad(V3(1f, 0f, -0.5f))
            val pts = listOf(c + V3(0f, 0.01f, -ancho / 2f), c + V3(0f, 0.01f, ancho / 2f), c + V3(0.2f, -cae, ancho / 2f + 0.03f), c + V3(0.17f, -cae * 0.9f, -ancho / 2f - 0.02f))
            val pts2 = listOf(c + V3(0f, 0.01f, -ancho / 2f), c + V3(0f, 0.01f, ancho / 2f), c + V3(-0.2f, -cae * 0.7f, ancho / 2f), c + V3(-0.21f, -cae * 0.75f, -ancho / 2f))
            Trazo3.run { caraDegradada(v, pts2, n * -1f, lerp(colores[k % 3], C.TINTA, 0.2f), c, c + V3(0f, -cae, 0f)); caraDegradada(v, pts, n, colores[k % 3], c, c + V3(0f, -cae, 0f)) }
        }
    }

    /** El tubo gordo en el pasillo: sube detrás del portón y corre por la bóveda hacia la Puerta. Va dentro del hueco. */
    fun DrawScope.enElPasillo(v: VistaRefugio) {
        val color = lerp(C.ACERO, C.CHAPA, 0.25f)
        tubo(v, Tubo(V3(GORDO_X, GORDO_ARRIBA, P.PUERTA_Z - 4f), V3(GORDO_X, GORDO_ARRIBA, GORDO_SUBE), GORDO_R, color, 6f, pasillo = true))
        Trazo3.run {
            cilindro(v, V3(GORDO_X, GORDO_Y, P.BOCA - 0.1f), V3(GORDO_X, GORDO_Y, GORDO_SUBE), GORDO_R, color, enPasillo = true, lados = 14, tapas = false)
            cilindro(v, V3(GORDO_X, GORDO_Y - GORDO_R, GORDO_SUBE), V3(GORDO_X, GORDO_ARRIBA, GORDO_SUBE), GORDO_R, color, enPasillo = true, lados = 14)
        }
    }
}
