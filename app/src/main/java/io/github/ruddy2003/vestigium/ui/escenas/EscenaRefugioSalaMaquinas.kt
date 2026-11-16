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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Las máquinas de la sala de bombas, con su materia y su uso: la bomba grande (el marco de la izquierda), la fila de bombas
 * viejas del fondo, el filtro grande; sus ruedas rojas (con la etiqueta roja del simulacro), bridas, manómetros, pilotos,
 * placas de latón sin letras, desconchones del verde y costras de cal; la llave de válvulas larga; y lo del día de la bomba
 * tres y de los filtros.
 */
internal object PiezasMaquinas {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio
    private val S = SalaRefugio

    // ---------------------------------------------------------------- Las piezas que se repiten

    /**
     * Una rueda de válvula roja en [c], mirando a [n], de radio [r]: el aro, cinco radios, el cubo y el vástago, con el filo
     * de luz de arriba. El día del simulacro, con su etiqueta roja de cartón colgada del aro (ella las cierra «una por una»).
     */
    fun DrawScope.rueda(v: VistaRefugio, c: V3, n: V3, r: Float, color: Color = C.ROJO) {
        val e = K.unidad(n); val u = K.unidad(K.cruz(e, K.ARRIBA)).let { if (K.largo(it) < 0.5f) K.DER else it }; val w = K.cruz(e, u)
        Trazo3.run {
            barra(c - e * 0.18f, c, r * 0.16f, v.c(c, K.NADA, C.ACERO))
            for (k in 0 until 5) { val a = 2f * PI.toFloat() * k / 5 + 0.3f; val d = u * cos(a) + w * sin(a); barra(c, c + d * (r * 0.92f), r * 0.1f, v.c(c + d * (r * 0.5f), e, lerp(color, C.TINTA, 0.15f))) }
            aro(c, e, r, r * 0.18f, v.c(c + w * r, e, color), 26)
        }
        val pts = K.circulo(c + e * 0.005f, e, r, 20, 0.3f, 2.6f).map { K.p(it) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y) }
        drawPath(camino, v.c(c + w * r, K.ARRIBA, lerp(color, Color.White, 0.45f)).copy(alpha = 0.7f), style = Stroke(r * 0.06f * K.k(c), cap = StrokeCap.Round))
        Trazo3.run { disco(v, c + e * 0.01f, e, r * 0.16f, C.HIERRO, lados = 10) }
        if (R.etiquetasRojas(v.m)) etiqueta(v, c + (u * 0.5f + w * -0.85f) * r + e * 0.02f)
    }

    /** La etiqueta roja de cartón, colgada de su cordel desde [desde]: «cerrada» (sin letras), con su ojal. */
    private fun DrawScope.etiqueta(v: VistaRefugio, desde: V3) {
        val a = desde + V3(0.02f, -0.07f, -0.01f)
        val pts = listOf(a + V3(-0.045f, 0f, 0f), a + V3(0.045f, 0f, 0f), a + V3(0.055f, -0.13f, -0.01f), a + V3(-0.035f, -0.135f, -0.01f))
        Trazo3.run {
            barra(desde, a, 0.006f, v.c(desde, K.NADA, C.PAPEL))
            cara(v, pts, K.unidad(V3(0.2f, 0f, -1f)), lerp(C.ALARMA, C.ROJO, 0.4f))
            disco(v, a + V3(0f, -0.025f, -0.012f), K.FRENTE, 0.012f, C.PAPEL, lados = 6, luz = false)
            barra(a + V3(-0.03f, -0.07f, -0.012f), a + V3(0.035f, -0.075f, -0.012f), 0.008f, v.c(a, K.FRENTE, lerp(C.PAPEL, C.ROJO, 0.4f)))
        }
    }

    /** Una brida (la unión de dos tubos) en [c], en un tubo de eje [eje], de radio [r]: el anillo y sus pernos oxidados. */
    fun DrawScope.brida(v: VistaRefugio, c: V3, eje: V3, r: Float, color: Color = C.VERDE) {
        val e = K.unidad(eje)
        Trazo3.run { cilindro(v, c - e * 0.03f, c + e * 0.03f, r, color, lados = 14) }
        for (p in K.circulo(c + e * 0.035f, e, r * 0.8f, 6).dropLast(1)) if (K.seVe(p, K.unidad(p - c))) drawCircle(v.c(p, K.NADA, C.OXIDO), 0.022f * K.k(p), K.p(p))
    }

    /** Desconchones del verde: manchas de óxido y de hierro en una cara, con el filo claro de la pintura levantada. */
    fun DrawScope.desconchones(v: VistaRefugio, c: V3, n: V3, ancho: Float, alto: Float, semilla: String, cuantos: Int = 5) {
        val az = SalaRefugio.azar("desconchon$semilla")
        val e = K.unidad(n); val u = K.unidad(K.cruz(e, K.ARRIBA)).let { if (K.largo(it) < 0.5f) K.DER else it }; val w = K.cruz(e, u)
        repeat(cuantos) {
            val q = c + u * az.entre(-ancho / 2f, ancho / 2f) + w * az.entre(-alto / 2f, alto / 2f) + e * 0.005f
            val r = az.entre(0.025f, 0.07f)
            val pts = (0 until 7).map { i -> val a = 6.283f * i / 7; q + u * (cos(a) * r * az.entre(0.6f, 1.4f)) + w * (sin(a) * r * 0.7f) }
            Trazo3.run { cara(v, pts, e, if (it % 2 == 0) C.OXIDO else C.HIERRO, alfa = 0.85f) }
            Trazo3.run { barra(pts[1], pts[3], 0.008f, v.c(q, e, C.VERDE_CLARO).copy(alpha = 0.7f)) }
        }
    }

    /** Una costra de cal que baja de [desde] por una cara de normal [n], de [largo] metros: blanca, con su chorrera. */
    fun DrawScope.cal(v: VistaRefugio, desde: V3, n: V3, largo: Float) {
        val e = K.unidad(n)
        val pts = listOf(desde + V3(-0.05f, 0.02f, 0f), desde + V3(0.06f, 0.02f, 0f), desde + V3(0.03f, -largo * 0.6f, 0f), desde + V3(0.01f, -largo, 0f), desde + V3(-0.03f, -largo * 0.7f, 0f))
        Trazo3.run { cara(v, pts.map { it + e * 0.006f }, e, C.CAL, alfa = 0.85f) }
        Trazo3.run { barra(desde + e * 0.008f, desde + V3(0.012f, -largo * 1.4f, 0f) + e * 0.008f, 0.012f, v.c(desde, e, lerp(C.CAL, C.ROCA, 0.3f)).copy(alpha = 0.6f)) }
    }

    /** Una placa de latón sin letras, de [ancho] por [alto] metros, en [c], mirando a [n], con sus cuatro remaches. */
    fun DrawScope.placa(v: VistaRefugio, c: V3, n: V3, ancho: Float, alto: Float) {
        val e = K.unidad(n); val u = K.unidad(K.cruz(e, K.ARRIBA)).let { if (K.largo(it) < 0.5f) K.DER else it }; val w = K.cruz(e, u)
        val q = c + e * 0.006f
        Trazo3.run { cara(v, listOf(q - u * (ancho / 2f) - w * (alto / 2f), q + u * (ancho / 2f) - w * (alto / 2f), q + u * (ancho / 2f) + w * (alto / 2f), q - u * (ancho / 2f) + w * (alto / 2f)), e, C.LATON) }
        for (du in listOf(-1f, 1f)) for (dw in listOf(-1f, 1f)) { val p = q + u * (du * ancho * 0.4f) + w * (dw * alto * 0.32f) + e * 0.003f; drawCircle(v.c(p, e, lerp(C.LATON, C.TINTA, 0.4f)), 0.01f * K.k(p), K.p(p)) }
        Trazo3.run { barra(q - u * (ancho * 0.3f) + e * 0.004f, q + u * (ancho * 0.3f) + e * 0.004f, alto * 0.12f, v.c(q, e, lerp(C.LATON, C.TINTA, 0.35f))) }
    }

    /**
     * Un manómetro de latón mirando hacia la sala, en [c], de radio [r], con la aguja en [lee] (de 0 a 1): el bisel de
     * latón grueso con su brillo, la esfera crema con su arco de rayitas y su tramo rojo, la aguja negra y el brillo del vidrio.
     */
    fun DrawScope.manometro(v: VistaRefugio, c: V3, r: Float, lee: Float) {
        val n = K.FRENTE
        Trazo3.run {
            disco(v, c - n * 0.01f, n, r * 1.04f, lerp(C.LATON, C.TINTA, 0.45f), lados = 20)
            disco(v, c, n, r, C.LATON, lados = 20)
            disco(v, c + n * 0.01f, n, r * 0.8f, lerp(C.PAPEL, Color.White, 0.25f), lados = 20)
        }
        val u = K.DER; val arriba = K.ARRIBA
        // El brillo del latón del bisel, del lado de arriba.
        val brillo = K.circulo(c + n * 0.005f, n, r * 0.9f, 10, 0.5f, 2.3f)
        Trazo3.run { for (i in 0 until brillo.size - 1) barra(brillo[i], brillo[i + 1], r * 0.09f, v.c(c, n, lerp(C.LATON, Color.White, 0.45f)).copy(alpha = 0.75f)) }
        for (k in 0..10) {
            val a = (PI * (1.25 - 1.5 * k / 10.0)).toFloat()
            val d = u * cos(a) + arriba * sin(a)
            Trazo3.run { barra(c + n * 0.015f + d * (r * 0.6f), c + n * 0.015f + d * (r * 0.73f), r * 0.05f, v.c(c, n, if (k >= 8) C.ROJO else C.TINTA)) }
        }
        val a = (PI * (1.25 - 1.5 * lee)).toFloat()
        val d = u * cos(a) + arriba * sin(a)
        Trazo3.run {
            barra(c + n * 0.02f - d * (r * 0.12f), c + n * 0.02f + d * (r * 0.66f), r * 0.07f, v.c(c, n, C.TINTA))
            disco(v, c + n * 0.025f, n, r * 0.1f, C.TINTA, lados = 8)
        }
        val p = K.p(c + n * 0.03f + (u * -0.4f + arriba * 0.45f) * r)
        drawCircle(v.c(c, n, Color.White).copy(alpha = 0.35f), r * 0.16f * K.k(c), p)
    }

    /** Un manómetro pequeño en un tubo, mirando hacia la sala. */
    private fun DrawScope.manometroDeTubo(v: VistaRefugio, c: V3, r: Float, lee: Float) {
        Trazo3.run { barra(c + K.FONDO * 0.12f, c, 0.03f, v.c(c, K.NADA, C.LATON)) }
        manometro(v, c, r, lee)
    }

    /**
     * El cuadrito de mando de una bomba, en [c] (su frente mirando a la sala): la caja de chapa con su placa, un piloto verde
     * arriba y uno ámbar debajo, y el pulsador rojo con su guarda.
     */
    private fun DrawScope.mando(v: VistaRefugio, c: V3, conPie: Boolean) {
        Trazo3.run {
            if (conPie) caja(v, c.x - 0.025f, c.x + 0.025f, 0f, c.y - 0.2f, c.z + 0.02f, c.z + 0.07f, C.HIERRO)
            caja(v, c.x - 0.08f, c.x + 0.08f, c.y - 0.2f, c.y + 0.14f, c.z, c.z + 0.09f, lerp(C.CHAPA, C.HIERRO, 0.45f))
            cara(v, listOf(V3(c.x - 0.06f, c.y + 0.08f, c.z - 0.004f), V3(c.x + 0.06f, c.y + 0.08f, c.z - 0.004f), V3(c.x + 0.06f, c.y + 0.12f, c.z - 0.004f), V3(c.x - 0.06f, c.y + 0.12f, c.z - 0.004f)), K.FRENTE, lerp(C.PAPEL, C.AMARILLO, 0.2f))
            caja(v, c.x - 0.035f, c.x + 0.035f, c.y - 0.17f, c.y - 0.1f, c.z - 0.03f, c.z, C.HIERRO)
            disco(v, V3(c.x, c.y - 0.135f, c.z - 0.032f), K.FRENTE, 0.022f, C.ROJO, lados = 8)
        }
        PiezasPared.run {
            piloto(v, V3(c.x - 0.03f, c.y + 0.02f, c.z - 0.006f), K.FRENTE, lerp(C.ALGA, Color.White, 0.3f), true, 0.018f)
            piloto(v, V3(c.x + 0.03f, c.y - 0.05f, c.z - 0.006f), K.FRENTE, C.GUARDIA, !v.dia || v.estado != EscenaRefugio.Estado.CERRADO, 0.018f)
        }
    }

    // ---------------------------------------------------------------- La bomba grande

    /**
     * La bomba grande, el marco de la izquierda (cortada por el borde): la bancada de fundición, el motor tumbado con sus
     * aletas y su tapa de ventilación de cara a la sala, el acoplamiento con su guarda amarilla, la voluta, el tubo que sube
     * hasta el techo con su válvula y su rueda roja, el manómetro, las bridas, la junta que gotea con su costra de cal, la
     * placa de latón, y su cuadrito de mando en su pie.
     */
    fun DrawScope.bombaGrande(v: VistaRefugio) {
        val b = S.BOMBA
        Trazo3.run { caja(v, b.x - 0.7f, b.x + 0.8f, 0f, 0.22f, 1.85f, 3.65f, C.HIERRO, lerp(C.HIERRO, C.VERDE, 0.3f)) }
        for (z in listOf(1.95f, 2.6f, 3.5f)) drawCircle(v.c(V3(b.x + 0.75f, 0.22f, z), K.ARRIBA, C.OXIDO), 0.03f * K.k(V3(b.x, 0f, z)), K.p(V3(b.x + 0.72f, 0.225f, z)))
        val m0 = V3(b.x - 0.05f, 0.72f, 1.95f); val m1 = V3(b.x - 0.05f, 0.72f, 2.85f)
        Trazo3.run { caja(v, b.x - 0.42f, b.x + 0.32f, 0.22f, 0.38f, 2.05f, 2.75f, lerp(C.VERDE, C.TINTA, 0.25f)) }
        Trazo3.run { cilindro(v, m0, m1, 0.42f, C.VERDE, lerp(C.VERDE, C.TINTA, 0.2f), lados = 22) }
        for (k in 1..5) { val z = 1.98f + 0.15f * k; Trazo3.run { aro(V3(m0.x, m0.y, z), K.FONDO, 0.43f, 0.02f, v.c(V3(m0.x + 0.43f, m0.y + 0.2f, z), K.unidad(V3(1f, 0.5f, 0f)), lerp(C.VERDE, C.PAPEL, 0.15f)), 24) } }
        Trazo3.run { cilindro(v, m0 - K.FONDO * 0.12f, m0, 0.36f, lerp(C.VERDE, C.TINTA, 0.15f), lerp(C.VERDE_CLARO, C.VERDE, 0.4f), lados = 20) }
        for (k in 0 until 12) { val a = 2f * PI.toFloat() * k / 12; val d = V3(cos(a), sin(a), 0f); val c0 = m0 - K.FONDO * 0.125f
            Trazo3.run { barra(c0 + d * 0.09f, c0 + d * 0.3f, 0.028f, v.c(c0, K.FRENTE, lerp(C.VERDE, C.TINTA, 0.7f))) } }
        Trazo3.run { disco(v, m0 - K.FONDO * 0.13f, K.FRENTE, 0.06f, C.VERDE_CLARO, lados = 12) }
        desconchones(v, m1 + V3(0.42f, 0f, -0.45f), K.DER, 0.6f, 0.4f, "motor", 7)
        placa(v, V3(m0.x + 0.43f, 0.82f, 2.3f), K.DER, 0.22f, 0.12f)
        Trazo3.run { cilindro(v, V3(m0.x, 0.75f, 2.85f), V3(m0.x, 0.75f, 3.05f), 0.24f, lerp(C.AMARILLO, C.OXIDO, 0.15f), lados = 14) }
        val v0 = V3(b.x, 0.78f, 3.05f); val v1 = V3(b.x, 0.78f, 3.48f)
        Trazo3.run { cilindro(v, v0, v1, 0.56f, C.VERDE, lerp(C.VERDE, C.TINTA, 0.1f), lados = 24) }
        brida(v, V3(b.x, 0.78f, 3.27f), K.FONDO, 0.6f)
        desconchones(v, V3(b.x + 0.56f, 0.78f, 3.27f), K.DER, 0.4f, 0.6f, "voluta", 5)
        val t = V3(b.x + 0.35f, 0f, 3.27f)
        Trazo3.run { cilindro(v, t + V3(0f, 1.15f, 0f), t + V3(0f, 2.82f, 0f), 0.15f, C.VERDE, lados = 16) }
        brida(v, t + V3(0f, 1.3f, 0f), K.ARRIBA, 0.22f)
        brida(v, t + V3(0f, 1.82f, 0f), K.ARRIBA, 0.22f)
        Trazo3.run { cilindro(v, t + V3(0f, 1.36f, 0f), t + V3(0f, 1.76f, 0f), 0.2f, lerp(C.VERDE, C.HIERRO, 0.3f), lados = 16) }
        rueda(v, t + V3(0.32f, 1.56f, -0.05f), K.unidad(V3(1f, 0.1f, -0.35f)), 0.3f)
        cal(v, t + V3(0.17f, 1.27f, -0.16f), K.FRENTE, 0.35f)
        manometroDeTubo(v, t + V3(-0.02f, 2.2f, -0.16f), 0.12f, 0.55f + 0.35f * v.reserva)
        Trazo3.run { cilindro(v, V3(b.x - 0.2f, 0.45f, 3.48f), V3(b.x - 0.2f, 0.45f, 4.3f), 0.17f, lerp(C.VERDE, C.ACERO, 0.35f), lados = 14) }
        brida(v, V3(b.x - 0.2f, 0.45f, 3.6f), K.FONDO, 0.24f)
        mando(v, S.PILOTO_BOMBA, true)
        if (R.laSalaPinta(v.m, "llave")) llave(v, V3(b.x + 1.05f, 0.0f, 2.05f), V3(b.x + 0.66f, 0.95f, 2.45f))
    }

    /**
     * La llave de válvulas: «de hierro, larga como un brazo, con el mango forrado de cinta», apoyada de [pie] a [punta]: la
     * barra con su brillo, el mango de cinta negra con sus vueltas, y la boca abierta de dos dientes.
     */
    fun DrawScope.llave(v: VistaRefugio, pie: V3, punta: V3) {
        val d = punta - pie
        val hierro = v.c(K.medio(pie, punta), K.NADA, lerp(C.HIERRO, C.ACERO, 0.45f))
        Trazo3.run {
            barra(pie, punta, 0.04f, hierro)
            barra(pie + V3(0.012f, 0f, -0.01f), punta + V3(0.012f, 0f, -0.01f), 0.01f, v.c(K.medio(pie, punta), K.ARRIBA, lerp(C.ACERO, Color.White, 0.35f)).copy(alpha = 0.7f))
            barra(pie, pie + d * 0.38f, 0.058f, v.c(pie + d * 0.2f, K.NADA, lerp(C.TINTA, C.AZUL, 0.3f)))
            for (k in 1..6) { val q = pie + d * (0.05f * k); barra(q + V3(-0.03f, 0.012f, 0f), q + V3(0.03f, -0.012f, 0f), 0.01f, v.c(q, K.NADA, lerp(C.TINTA, C.AZUL, 0.5f))) }
            val boca = punta + d * 0.04f
            barra(boca - K.DER * 0.07f, boca + K.DER * 0.07f, 0.06f, v.c(boca, K.FRENTE, lerp(C.ACERO, Color.White, 0.1f)), StrokeCap.Butt)
            barra(boca - K.DER * 0.07f, boca - K.DER * 0.08f + K.ARRIBA * 0.1f, 0.035f, v.c(boca, K.FRENTE, C.ACERO)); barra(boca + K.DER * 0.07f, boca + K.DER * 0.08f + K.ARRIBA * 0.11f, 0.035f, v.c(boca, K.FRENTE, C.ACERO))
        }
    }

    // ---------------------------------------------------------------- La fila de bombas del fondo

    /** Una bomba vertical: el cuerpo redondo en su base, el motor encima con su placa, la salida con su rueda, su mando y su cal. */
    fun DrawScope.bombaVertical(v: VistaRefugio, b: V3) {
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
        for (y in listOf(0.95f, 1.1f, 1.25f)) Trazo3.run { aro(b + V3(0f, y, 0f), K.ARRIBA, 0.265f, 0.014f, v.c(b + V3(0.26f, y, -0.1f), K.DER, lerp(C.VERDE, C.PAPEL, 0.12f)), 20) }
        brida(v, b + V3(0.5f, 0.45f, 0f), K.DER, 0.16f)
        rueda(v, b + V3(0.68f, 0.45f, 0f), K.DER, 0.2f)
        desconchones(v, b + V3(0.2f, 0.5f, -0.28f), K.unidad(V3(0.6f, 0f, -0.8f)), 0.3f, 0.4f, "vertical", 6)
        placa(v, b + V3(0.12f, 1.1f, -0.24f), K.unidad(V3(0.5f, 0f, -0.86f)), 0.16f, 0.09f)
        cal(v, b + V3(0.42f, 0.38f, -0.1f), K.unidad(V3(0.6f, 0f, -0.8f)), 0.3f)
        manometroDeTubo(v, b + V3(-0.1f, 1.05f, -0.28f), 0.1f, 0.6f)
        mando(v, b + V3(0.55f, 1.15f, -0.38f), false)
    }

    /** La bomba vieja de la fila (la «bomba tres» del cuento): más chica, tumbada, con su motor, su rueda, su mando y su óxido. */
    fun DrawScope.bombaVieja(v: VistaRefugio, b: V3) {
        Trazo3.run {
            caja(v, b.x - 0.55f, b.x + 0.55f, 0f, 0.18f, b.z - 0.35f, b.z + 0.35f, C.HIERRO)
            cilindro(v, b + V3(-0.5f, 0.55f, 0f), b + V3(0.05f, 0.55f, 0f), 0.3f, lerp(C.VERDE, C.CHAPA, 0.3f), lados = 18)
            cilindro(v, b + V3(0.05f, 0.55f, 0f), b + V3(0.35f, 0.55f, 0f), 0.38f, C.VERDE, lados = 20)
            cilindro(v, b + V3(0.2f, 0.9f, 0f), b + V3(0.2f, 1.05f, 0f), 0.1f, C.VERDE, lados = 10)
            cilindro(v, b + V3(0.2f, 0.55f, 0.3f), b + V3(0.2f, 0.55f, S.FONDO - b.z), 0.11f, lerp(C.VERDE, C.ACERO, 0.35f), lados = 12)
        }
        brida(v, b + V3(0.2f, 0.98f, 0f), K.ARRIBA, 0.14f)
        rueda(v, b + V3(0.5f, 0.6f, -0.05f), K.DER, 0.18f)
        desconchones(v, b + V3(0.2f, 0.55f, -0.38f), K.FRENTE, 0.45f, 0.35f, "vieja", 7)
        placa(v, b + V3(-0.25f, 0.62f, -0.3f), K.FRENTE, 0.15f, 0.08f)
        cal(v, b + V3(0.32f, 0.3f, -0.36f), K.FRENTE, 0.22f)
        mando(v, b + V3(0.75f, 0.95f, -0.3f), false)
    }

    /**
     * La bomba tres desmontada «pieza por pieza» (el día `bomba_tres`): la carcasa abierta sobre su base, el rodete fuera, y
     * el cubo de agua marrón.
     */
    fun DrawScope.bombaDesmontada(v: VistaRefugio) {
        val b = S.BOMBA_3
        Trazo3.run {
            caja(v, b.x - 0.55f, b.x + 0.55f, 0f, 0.18f, b.z - 0.35f, b.z + 0.35f, C.HIERRO)
            cilindro(v, b + V3(-0.5f, 0.55f, 0f), b + V3(0.05f, 0.55f, 0f), 0.3f, lerp(C.VERDE, C.CHAPA, 0.3f), lados = 18)
            cilindro(v, b + V3(0.05f, 0.55f, 0f), b + V3(0.15f, 0.55f, 0f), 0.38f, C.VERDE, lados = 20)
            disco(v, b + V3(0.16f, 0.55f, 0f), K.DER, 0.3f, lerp(C.OXIDO, C.TINTA, 0.3f), lados = 18)
        }
        Trazo3.run { cilindro(v, b + V3(0.75f, 0f, 0.15f), b + V3(0.75f, 0.12f, 0.15f), 0.36f, C.VERDE, lados = 18) }
        PiezasSala.run { cubo(v, b + V3(1.2f, 0f, 0.35f), C.ACERO, lerp(C.OXIDO, C.ROCA, 0.4f)) }
    }

    /**
     * Las piezas de la bomba tres en el suelo, sobre un trapo, «pieza por pieza»: el rodete de latón, la tapa de la voluta,
     * el eje partido en dos («gastado por cien años de vueltas»), los pernos y el cubo con el agua marrón.
     */
    fun DrawScope.piezasDeLaBomba(v: VistaRefugio, t: V3) {
        Trazo3.run {
            cara(v, listOf(t + V3(-0.5f, 0.005f, -0.32f), t + V3(0.52f, 0.005f, -0.38f), t + V3(0.48f, 0.005f, 0.36f), t + V3(-0.52f, 0.005f, 0.3f)), K.ARRIBA, lerp(C.PAPEL, C.ROCA, 0.3f))
            cilindro(v, t + V3(-0.2f, 0.01f, -0.05f), t + V3(-0.2f, 0.09f, -0.05f), 0.22f, C.LATON, lados = 16)
            for (k in 0 until 6) { val a = PI.toFloat() * k / 3f; barra(t + V3(-0.2f, 0.095f, -0.05f), t + V3(-0.2f + cos(a) * 0.19f, 0.095f, -0.05f + sin(a) * 0.19f), 0.03f, v.c(t, K.ARRIBA, lerp(C.LATON, C.TINTA, 0.35f))) }
            barra(t + V3(0.08f, 0.035f, -0.22f), t + V3(0.44f, 0.035f, -0.12f), 0.06f, v.c(t, K.ARRIBA, lerp(C.ACERO, Color.White, 0.2f)))
            barra(t + V3(0.12f, 0.035f, 0.02f), t + V3(0.42f, 0.035f, 0.18f), 0.06f, v.c(t, K.ARRIBA, lerp(C.ACERO, Color.White, 0.2f)))
            cilindro(v, t + V3(0.1f, 0f, 0.62f), t + V3(0.12f, 0.1f, 0.62f), 0.3f, C.VERDE, lerp(C.VERDE, C.TINTA, 0.3f), lados = 18)
        }
        val az = SalaRefugio.azar("pernos")
        repeat(9) { val p = t + V3(az.entre(-0.4f, 0.4f), 0.02f, az.entre(0.05f, 0.28f)); drawCircle(v.c(p, K.ARRIBA, C.OXIDO), 0.028f * K.k(p), K.p(p)) }
        PiezasSala.run { cubo(v, t + V3(-0.75f, 0f, 0.15f), C.AZUL, lerp(C.OXIDO, C.ROCA, 0.35f)) }
    }

    // ---------------------------------------------------------------- El filtro

    /**
     * El filtro grande de la depuradora, «un cilindro del tamaño de un niño», en su cuna: la costura remachada, los aros de
     * refuerzo, la placa de latón, la mirilla con el agua que pasa, la tapa abombada con su corona de pernos que chorrean
     * óxido y su manómetro, la entrada y la salida con su rueda, y la llave de purga con su costra de cal. El día de los
     * filtros, abierto: sin tapa, con el barro negro dentro.
     */
    fun DrawScope.filtro(v: VistaRefugio) {
        val f = S.FILTRO; val r = S.FILTRO_R; val h = S.FILTRO_ALTO
        val abierto = R.filtros(v.m)
        Trazo3.run {
            caja(v, f.x - 0.42f, f.x + 0.42f, 0f, 0.08f, f.z - 0.42f, f.z + 0.42f, C.HIERRO)
            caja(v, f.x - 0.36f, f.x + 0.36f, 0.08f, 0.3f, f.z - 0.08f, f.z + 0.08f, lerp(C.HIERRO, C.VERDE, 0.2f))
        }
        Trazo3.run { cilindro(v, f + V3(0f, 0.2f, 0f), f + V3(0f, h - 0.1f, 0f), r, C.VERDE, lados = 24) }
        val frente = K.unidad(V3(0.35f, 0f, -0.94f))
        desconchones(v, f + V3(0.15f, 0.7f, -r), frente, 0.3f, 0.6f, "filtro", 8)
        // La costura remachada, de arriba abajo.
        val costura = f + frente * (r + 0.004f) + V3(-0.12f, 0f, 0f)
        for (k in 0..7) { val p = costura + V3(0f, 0.3f + 0.12f * k, 0f); drawCircle(v.c(p, frente, lerp(C.VERDE, C.TINTA, 0.3f)), 0.014f * K.k(p), K.p(p)) }
        for (y in listOf(0.45f, 0.95f)) Trazo3.run { aro(f + V3(0f, y, 0f), K.ARRIBA, r + 0.008f, 0.03f, v.c(f + V3(r, y, 0f), K.DER, lerp(C.VERDE, C.TINTA, 0.35f)), 26) }
        placa(v, f + frente * r + V3(0.06f, 1.12f, 0f), frente, 0.2f, 0.1f)
        val mira = f + V3(-0.08f, 0.55f, -r - 0.005f)
        Trazo3.run { cara(v, listOf(mira, mira + V3(0.1f, 0f, 0.01f), mira + V3(0.1f, 0.38f, 0.01f), mira + V3(0f, 0.38f, 0f)), K.FRENTE, lerp(C.CHAPA, if (R.racionamiento(v.m) || abierto) C.BARRO else C.PISCINA, 0.45f)) }
        Trazo3.run { barra(mira + V3(0.02f, 0.03f, -0.01f), mira + V3(0.02f, 0.35f, -0.01f), 0.01f, Color.White.copy(alpha = 0.35f)) }
        if (abierto) {
            Trazo3.run { disco(v, f + V3(0f, h - 0.1f, 0f), K.ARRIBA, r, C.BARRO, lados = 24); aro(f + V3(0f, h - 0.1f, 0f), K.ARRIBA, r, 0.04f, v.c(f + V3(0f, h, 0f), K.ARRIBA, C.ACERO), 26) }
        } else {
            Trazo3.run { cilindro(v, f + V3(0f, h - 0.12f, 0f), f + V3(0f, h - 0.04f, 0f), r + 0.06f, lerp(C.VERDE, C.HIERRO, 0.2f), lados = 24) }
            for ((i, p) in K.circulo(f + V3(0f, h - 0.03f, 0f), K.ARRIBA, r + 0.035f, 12).dropLast(1).withIndex()) {
                drawCircle(v.c(p, K.ARRIBA, C.OXIDO), 0.024f * K.k(p), K.p(p))
                if (i % 3 == 0 && K.seVe(p, K.unidad(p - f - V3(0f, p.y, 0f)))) Trazo3.run { barra(p + V3(0f, -0.06f, 0f), p + V3(0f, -0.3f, 0f), 0.02f, v.c(p, K.NADA, C.OXIDO).copy(alpha = 0.55f)) }
            }
            Trazo3.run { cilindro(v, f + V3(0f, h - 0.04f, 0f), f + V3(0f, h + 0.04f, 0f), r * 0.85f, C.VERDE, lados = 22); cilindro(v, f + V3(0f, h + 0.04f, 0f), f + V3(0f, h + 0.1f, 0f), r * 0.45f, lerp(C.VERDE, C.TINTA, 0.2f), lados = 16) }
            Trazo3.run { cilindro(v, f + V3(0f, h + 0.1f, 0f), f + V3(0f, h + 0.2f, 0f), 0.035f, C.LATON, lados = 8) }
            manometro(v, f + V3(0f, h + 0.3f, -0.02f), 0.1f, 0.35f + 0.4f * v.reserva)
        }
        Trazo3.run {
            cilindro(v, f + V3(-r, 1.0f, 0f), f + V3(-r - 0.35f, 1.0f, 0f), 0.09f, C.VERDE, lados = 12)
            cilindro(v, f + V3(-r - 0.35f, 1.0f, 0f), f + V3(-r - 0.35f, 1.0f, S.FONDO - f.z), 0.09f, lerp(C.VERDE, C.ACERO, 0.3f), lados = 12)
            cilindro(v, f + V3(r, 0.42f, 0f), f + V3(r + 0.35f, 0.42f, 0f), 0.09f, C.VERDE, lados = 12)
        }
        brida(v, f + V3(r + 0.08f, 0.42f, 0f), K.DER, 0.13f)
        rueda(v, f + V3(r + 0.25f, 0.62f, 0f), K.ARRIBA, 0.14f)
        Trazo3.run { barra(f + V3(0.1f, 0.25f, -r), f + V3(0.14f, 0.2f, -r - 0.12f), 0.035f, v.c(f, K.FRENTE, C.LATON)) }
        cal(v, f + V3(0.14f, 0.2f, -r - 0.12f), K.FRENTE, 0.12f)
    }

    /** La tapa del filtro, quitada y apoyada en el suelo, el día de los filtros. */
    fun DrawScope.tapaDelFiltro(v: VistaRefugio, c: V3) {
        val r = S.FILTRO_R + 0.06f
        Trazo3.run { cilindro(v, c, c + V3(0.05f, 0.1f, 0f), r, C.VERDE, lerp(C.VERDE, C.TINTA, 0.3f), lados = 22) }
        for (p in K.circulo(c + V3(0.05f, 0.11f, 0f), K.ARRIBA, r - 0.03f, 12).dropLast(1)) drawCircle(v.c(p, K.ARRIBA, C.OXIDO), 0.02f * K.k(p), K.p(p))
    }

    /** Los filtros saturados del racionamiento, apilados: cilindros cortos llenos de barro. */
    fun DrawScope.filtrosSaturados(v: VistaRefugio, c: V3) {
        for ((k, d) in listOf(V3(-0.2f, 0f, 0f), V3(0.2f, 0f, 0.05f), V3(0f, 0.36f, 0.02f)).withIndex()) {
            val q = c + d
            Trazo3.run { cilindro(v, q, q + V3(0f, 0.34f, 0f), 0.17f, lerp(C.PAPEL, C.BARRO, 0.35f + 0.15f * k), lerp(C.BARRO, C.TINTA, 0.2f), lados = 14) }
        }
    }
}
