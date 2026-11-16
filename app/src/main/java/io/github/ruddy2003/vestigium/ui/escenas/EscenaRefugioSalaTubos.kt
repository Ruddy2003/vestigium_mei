package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import kotlin.math.PI
import kotlin.math.sin

/**
 * Los tubos del techo de la sala de bombas, «tubos de todos los tamaños», pocos y gordos, cada uno a su profundidad: el
 * verde grande (con la gotera que cae al cubo, la junta envuelta en un trapo y su costra de cal), el forrado de tela con sus
 * bandas, el de cobre, y el gordo de acero que cruza el cuadro y sale por el portón al pasillo, donde sube a la bóveda (lo
 * que cruza y enmarca, regla 6); uno cruza la sala junto a la pared del fondo y baja a la columna de nivel. Cada uno con
 * sus bridas y sus abrazaderas colgadas del techo; por debajo, la luz cálida de la lámpara; por arriba, el filo frío.
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

    /** El tubo verde grande y la junta que gotea, encima del cubo. */
    private const val VERDE_X = -2.3f
    private const val VERDE_Y = 2.74f
    private const val VERDE_R = 0.19f
    /** De dónde cae la gota: la junta del tubo verde, encima del cubo (la usa la parte viva). */
    val GOTERA: V3 get() = V3(VERDE_X, VERDE_Y - VERDE_R - 0.05f, PiezasSala.CUBO.z + 0.05f)

    private val TUBOS: List<Tubo> by lazy {
        listOf(
            Tubo(V3(VERDE_X, VERDE_Y, -1.5f), V3(VERDE_X, VERDE_Y, S.FONDO), VERDE_R, C.VERDE, 2.0f),
            Tubo(V3(-0.4f, 2.95f, -1.5f), V3(-0.4f, 2.95f, S.FONDO), 0.14f, lerp(C.PAPEL, C.ROCA, 0.3f), 0f, forrado = true),
            Tubo(V3(0.55f, 3.04f, -1.5f), V3(0.55f, 3.04f, S.FONDO), 0.08f, lerp(C.COBRE, C.TINTA, 0.2f)),
            Tubo(V3(GORDO_X, GORDO_Y, -1.5f), V3(GORDO_X, GORDO_Y, P.BOCA), GORDO_R, lerp(C.ACERO, C.PAPEL, 0.2f), 2.4f),
            Tubo(V3(S.IZQ, 2.86f, 6.85f), V3(S.COLUMNA_X - 0.06f, 2.86f, 6.85f), 0.1f, C.VERDE, 2.2f),
        )
    }

    /** Los tubos del techo de la sala, de los más lejanos a los más cercanos, las bajadas, las abrazaderas y la gotera. */
    fun DrawScope.tubos(v: VistaRefugio) {
        val tx = S.COLUMNA_X - 0.06f
        Trazo3.run {
            cilindro(v, V3(tx, 2.86f, 6.85f), V3(tx, 2.86f, S.FONDO - 0.2f), 0.055f, C.COBRE, lados = 10)
            cilindro(v, V3(tx, 2.81f, S.FONDO - 0.2f), V3(tx, 2.95f, S.FONDO - 0.2f), 0.08f, C.LATON, lados = 10)
            cilindro(v, V3(-5.0f, 2.86f, 6.85f), V3(-5.0f, 2.62f, 6.85f), 0.065f, C.VERDE, lados = 10)
            cilindro(v, V3(-5.0f, 2.62f, 6.85f), V3(-5.0f, 2.62f, S.FONDO - 0.06f), 0.065f, C.VERDE, lados = 10)
        }
        // El tubo que sube de la bomba grande y entra, con una te, en el tubo verde.
        Trazo3.run { cilindro(v, V3(S.BOMBA.x + 0.35f, VERDE_Y, 3.27f), V3(VERDE_X, VERDE_Y, 3.27f), 0.14f, C.VERDE, lados = 14) }
        for (t in TUBOS.sortedByDescending { K.hondo(K.medio(it.a, it.b)) }) tubo(v, t)
        abrazaderas(v)
        gotera(v)
    }

    /** Las abrazaderas: el fleje de hierro alrededor del tubo y la varilla que sube al techo, cada tanto (lejos de los ojos). */
    private fun DrawScope.abrazaderas(v: VistaRefugio) {
        val hierro = v.c(V3(-2f, 3f, 4f), K.NADA, lerp(C.HIERRO, C.ACERO, 0.3f))
        for (t in TUBOS) {
            if (t.a.z == t.b.z) continue
            var z = 2.6f
            while (z < S.FONDO - 0.3f) {
                val q = V3(t.a.x, t.a.y, z)
                if (q.y + t.r < S.TECHO - 0.02f && K.hondo(q) > 2.6f) {
                    Trazo3.run { barra(q + V3(0f, t.r, 0f), V3(q.x, S.TECHO, q.z), 0.02f, hierro) }
                    // El fleje: solo la mitad de abajo, la que abraza el tubo por donde se ve.
                    val fleje = K.circulo(q, K.FONDO, t.r * 1.06f, 10, -PI.toFloat() * 0.05f, PI.toFloat() * 1.05f)
                    Trazo3.run { for (i in 0 until fleje.size - 1) barra(fleje[i], fleje[i + 1], 0.03f, v.c(fleje[i], K.ABAJO, lerp(C.HIERRO, C.OXIDO, 0.35f))) }
                }
                z += 1.7f
            }
        }
    }

    /**
     * La gotera: la junta del tubo verde, encima del cubo, envuelta en un trapo atado que chorrea, con su costra de cal y la
     * gota colgando (la que cae va en la parte viva).
     */
    private fun DrawScope.gotera(v: VistaRefugio) {
        val c = V3(VERDE_X, VERDE_Y, GOTERA.z)
        val tela = lerp(C.PAPEL, C.ROCA, 0.4f)
        val n = K.unidad(V3(0.35f, 0f, -1f))
        // El alambre que ata el trapo a la junta: dos vueltas por debajo del tubo.
        for (dz in listOf(-0.05f, 0.05f)) { val atado = K.circulo(c + K.FONDO * dz, K.FONDO, VERDE_R * 1.06f, 10, 0f, PI.toFloat())
            Trazo3.run { for (i in 0 until atado.size - 1) barra(atado[i], atado[i + 1], 0.012f, v.c(c, K.NADA, C.ACERO)) } }
        // El trapo que cuelga de la junta, con el borde deshilachado, los pliegues y la punta mojada, más oscura.
        val arriba = c + V3(0f, -VERDE_R * 0.9f, 0f)
        val borde = (0..6).map { i -> val t = i / 6f; arriba + V3(0.04f * sin(t * 7f), -0.3f - 0.05f * ((i * 3) % 4) / 3f, -0.1f + 0.2f * t) }
        val pts = listOf(arriba + V3(0f, 0f, -0.09f), arriba + V3(0f, 0f, 0.09f)) + borde.reversed()
        Trazo3.run { caraDegradada(v, pts, n, tela, arriba, arriba + V3(0f, -0.32f, 0f)) }
        for (dz in listOf(-0.04f, 0.03f)) Trazo3.run { barra(arriba + V3(0.002f, -0.03f, dz), arriba + V3(0.01f, -0.28f, dz + 0.01f), 0.014f, v.c(arriba, n, lerp(tela, C.TINTA, 0.35f)).copy(alpha = 0.6f)) }
        Trazo3.run { cara(v, borde.subList(1, 6) + listOf(arriba + V3(0.02f, -0.22f, 0.04f), arriba + V3(0.02f, -0.22f, -0.05f)), n, lerp(tela, C.TINTA, 0.45f), alfa = 0.55f) }
        PiezasMaquinas.run { cal(v, c + V3(-0.04f, -VERDE_R * 0.4f, -VERDE_R * 1.13f), K.FRENTE, 0.16f) }
        val g = GOTERA
        drawCircle(v.c(g, K.NADA, lerp(C.PISCINA, Color.White, 0.6f)), 0.022f * K.k(g), K.p(g))
    }

    private fun DrawScope.tubo(v: VistaRefugio, t: Tubo) {
        val eje = K.unidad(t.b - t.a); val largo = K.largo(t.b - t.a)
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
                Trazo3.run { cilindro(v, c - eje * 0.035f, c + eje * 0.035f, t.r * 1.4f, lerp(t.color, C.HIERRO, 0.3f), lados = 14, enPasillo = t.pasillo) }
                for (q in K.circulo(c + eje * 0.04f, eje, t.r * 1.2f, 8).dropLast(1)) if (K.seVe(q, K.unidad(q - c))) drawCircle(v.c(q, K.NADA, C.OXIDO, t.pasillo), 0.022f * K.k(q), K.p(q))
            }
        }
        for ((_, pinta) in piezas.sortedByDescending { it.first }) pinta()
        if (t.forrado) forro(v, t, eje, largo)
        // El filo de luz de abajo (lo que le llega de la lámpara y del portón) y el filo frío de arriba.
        val ritmo = -eje.x * K.SIN + eje.z * K.COS
        val desde = if (K.hondo(t.a) < 1f && ritmo > 0.01f) t.a + eje * ((1f - K.hondo(t.a)) / ritmo) else t.a
        val bajo = V3(0f, -t.r * 0.92f, 0f); val alto = V3(0.25f * t.r, t.r * 0.88f, 0f)
        Trazo3.run {
            barra(desde + bajo, t.b + bajo, t.r * 0.2f, v.c(K.medio(desde, t.b) + bajo, K.ABAJO, lerp(t.color, Color.White, 0.3f), t.pasillo).copy(alpha = 0.6f), StrokeCap.Butt)
            barra(desde + alto, t.b + alto, t.r * 0.14f, lerp(v.colorTubo, C.VIOLETA, 0.3f + 0.4f * v.noche).copy(alpha = 0.18f + 0.22f * v.noche), StrokeCap.Butt)
        }
    }

    /** El forro de tela de un tubo: las bandas (solo la mitad de abajo, la que se ve) y las costuras, no en lo muy cercano. */
    private fun DrawScope.forro(v: VistaRefugio, t: Tubo, eje: V3, largo: Float) {
        var d = 0.4f
        while (d < largo) {
            val c = t.a + eje * d
            if (K.hondo(c) > 2.6f) {
                val medio = K.circulo(c, eje, t.r + 0.008f, 10, PI.toFloat(), 2f * PI.toFloat()).map { K.p(it) }
                val camino = Path().apply { moveTo(medio[0].x, medio[0].y); for (i in 1 until medio.size) lineTo(medio[i].x, medio[i].y) }
                drawPath(camino, v.c(c + V3(0f, -t.r, 0f), K.ABAJO, lerp(C.PAPEL, C.TINTA, 0.35f)), style = Stroke(0.04f * K.k(c)))
            }
            d += 0.55f
        }
    }

    /**
     * Lo de arriba que cambia de un día a otro: los trapos que ella pone a secar sobre el tubo gordo (de ninguno a dos, cada
     * día en otro sitio): telas con peso, que caen del tubo en pliegues, con el borde deshilachado y sus manchas **[Diseño propio]**.
     */
    fun DrawScope.delante(v: VistaRefugio) {
        val az = v.m.azar("trapos")
        val cuantos = (az.real() * 2.6f).toInt()
        repeat(cuantos) { k -> trapoEnElTubo(v, V3(GORDO_X, GORDO_Y + GORDO_R, 4.2f + 1.3f * k + az.entre(0f, 0.6f)), az.entre(0.28f, 0.4f), az.entre(0.4f, 0.6f), k) }
    }

    /** Un trapo echado sobre el tubo gordo en [c] (lo alto del tubo): el faldón que cae hacia la sala, con su doblez arriba. */
    private fun DrawScope.trapoEnElTubo(v: VistaRefugio, c: V3, ancho: Float, cae: Float, k: Int) {
        val tela = listOf(lerp(C.PAPEL, C.ROCA, 0.3f), lerp(C.ROJO, C.PAPEL, 0.5f), lerp(C.AZUL, C.PAPEL, 0.4f))[k % 3]
        val baja = K.unidad(V3(0.3f, -1f, 0f)); val n = K.unidad(V3(1f, 0.3f, -0.2f))
        val arriba = listOf(c + V3(0.02f, 0f, -ancho / 2f), c + V3(0.02f, 0f, ancho / 2f))
        val lado = c + V3(GORDO_R, -GORDO_R, 0f)
        val borde = (0..7).map { i -> val t = i / 7f; lado + baja * (cae * (0.85f + 0.15f * sin(t * 11f + k)) ) + V3(0f, 0f, ancho / 2f - ancho * t + 0.02f * sin(t * 17f)) }
        Trazo3.run { caraDegradada(v, arriba + borde, n, tela, c, lado + baja * cae) }
        // Los pliegues: bandas oscuras que bajan, y la mancha.
        for (j in 1..3) { val z = ancho / 2f - ancho * j / 4f; Trazo3.run { barra(c + V3(GORDO_R * 0.7f, -0.04f, z), lado + baja * (cae * 0.85f) + V3(0f, 0f, z + 0.02f), 0.02f, v.c(lado, n, lerp(tela, C.TINTA, 0.35f)).copy(alpha = 0.6f)) } }
        Trazo3.run { disco(v, lado + baja * (cae * 0.5f) + V3(0.01f, 0f, 0.05f), n, 0.05f, lerp(tela, C.TINTA, 0.4f), lados = 8) }
        // El doblez sobre el tubo, más claro.
        Trazo3.run { barra(c + V3(0.03f, 0.01f, -ancho / 2f), c + V3(0.03f, 0.01f, ancho / 2f), 0.05f, v.c(c, K.ARRIBA, lerp(tela, Color.White, 0.2f))) }
    }

    /** El tubo gordo en el pasillo: sube detrás del portón y corre por la bóveda hacia la Puerta. Va dentro del hueco. */
    fun DrawScope.enElPasillo(v: VistaRefugio) {
        val color = lerp(C.ACERO, C.OXIDO, 0.25f)
        tubo(v, Tubo(V3(GORDO_X, GORDO_ARRIBA, P.PUERTA_Z - 4f), V3(GORDO_X, GORDO_ARRIBA, GORDO_SUBE), GORDO_R, color, 4.5f, pasillo = true))
        Trazo3.run {
            cilindro(v, V3(GORDO_X, GORDO_Y, P.BOCA - 0.1f), V3(GORDO_X, GORDO_Y, GORDO_SUBE), GORDO_R, color, enPasillo = true, lados = 14, tapas = false)
            cilindro(v, V3(GORDO_X, GORDO_Y - GORDO_R, GORDO_SUBE), V3(GORDO_X, GORDO_ARRIBA, GORDO_SUBE), GORDO_R, color, enPasillo = true, lados = 14)
        }
    }
}
