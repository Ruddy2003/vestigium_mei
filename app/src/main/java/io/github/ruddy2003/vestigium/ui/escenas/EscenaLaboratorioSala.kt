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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Jardines
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.FRENTE
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.IZQ
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.caraGrande
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.halo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La sala del laboratorio y lo que se ve por su puerta (`composicion_laboratorio.md`): el techo de losas con los paneles
 * de la luz general y el conducto del aire; la pared del fondo, de hormigón pintado de blanco, con las juntas del
 * encofrado, el estante de los cuadernos, la percha con la bata, los guantes, el tubo del riego, el reloj y la puerta de
 * vidrio; por ella, el pasillo de Botánica y, al final, los jardines según el arco; la pared de la derecha, con el marco
 * de la pantalla de la Vigía y el estante de las semillas; y el suelo de hormigón pulido, con sus juntas, la flecha y la
 * rejilla del desagüe.
 */
internal object SalaLab {
    private val C = ColorLaboratorio

    // ---------------------------------------------------------------- Las medidas (metros)
    const val XI = -4.6f                                                        // la pared de la izquierda (fuera del cuadro)
    const val XD = 3.0f                                                         // la pared de la derecha, la de la pantalla
    const val ZF = 7.0f                                                         // la pared del fondo
    const val TECHO = 3.3f
    const val Z_CERCA = 0.9f

    /** La puerta de vidrio, de dos hojas, en la pared del fondo. */
    const val PX0 = -0.85f
    const val PX1 = 0.55f
    const val PH = 2.3f

    /** El pasillo de Botánica, detrás de la puerta, y su final: la pared de vidrio de los jardines. */
    const val PZ1 = 10.6f
    const val PXI = -1.5f
    const val PXD = 1.05f
    const val PT = 2.7f

    /** La pantalla de la Vigía, en la pared de la derecha: de [PZ_CERCA] a [PZ_LEJOS] de hondo, de [PY0] a [PY1] de alto. */
    const val PZ_CERCA = 2.25f
    const val PZ_LEJOS = 4.7f
    const val PY0 = 1.12f
    const val PY1 = 2.95f

    /** El estante de las semillas, bajo la pantalla. */
    const val EZ0 = 2.7f
    const val EZ1 = 4.6f
    const val EX = 2.6f
    const val EY = 0.94f

    /** El estante de los cuadernos y la percha, en la pared del fondo, a la izquierda de la puerta. */
    const val CX0 = -2.15f
    const val CX1 = -1.15f

    /** El conducto del aire de arriba, redondo, que cruza el techo a lo hondo y baja a la campana. */
    const val CONDUCTO_X = 1.6f
    const val CONDUCTO_Y = 3.07f
    const val CONDUCTO_R = 0.2f
    const val CY0 = 1.62f
    const val CY1 = 2.08f

    // ---------------------------------------------------------------- Las formas

    fun formaDelSuelo(): Path = CamLab.forma(listOf(V3(XI, 0f, ZF), V3(XD, 0f, ZF), V3(XD, 0f, Z_CERCA), V3(XI, 0f, Z_CERCA)))
    fun formaDelFondo(): Path = CamLab.forma(listOf(V3(XI, 0f, ZF), V3(XD, 0f, ZF), V3(XD, TECHO, ZF), V3(XI, TECHO, ZF)))
    fun formaDerecha(): Path = CamLab.forma(listOf(V3(XD, 0f, ZF), V3(XD, 0f, Z_CERCA), V3(XD, TECHO, Z_CERCA), V3(XD, TECHO, ZF)))
    fun formaDelTecho(): Path = CamLab.forma(listOf(V3(XI, TECHO, ZF), V3(XD, TECHO, ZF), V3(XD, TECHO, Z_CERCA), V3(XI, TECHO, Z_CERCA)))
    fun formaDeLaPuerta(): Path = CamLab.forma(listOf(V3(PX0, 0f, ZF), V3(PX1, 0f, ZF), V3(PX1, PH, ZF), V3(PX0, PH, ZF)))

    /** La pared del fondo sin el hueco de la puerta: lo que alumbran los halos de la pared, que no tiñen el vidrio. */
    fun formaDelFondoSinPuerta(): Path = Path().apply {
        addPath(formaDelFondo()); addPath(formaDeLaPuerta()); fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
    }

    /** Un punto de la pantalla: [u] de 0 (el borde lejano) a 1 (el cercano), [v] de 0 (arriba) a 1 (abajo). */
    fun enLaPantalla(u: Float, v: Float, metido: Float = 0.015f): V3 = V3(XD - metido, PY1 + (PY0 - PY1) * v, PZ_LEJOS + (PZ_CERCA - PZ_LEJOS) * u)

    fun formaDeLaPantalla(): Path = CamLab.forma(listOf(enLaPantalla(0f, 0f), enLaPantalla(1f, 0f), enLaPantalla(1f, 1f), enLaPantalla(0f, 1f)))

    // ---------------------------------------------------------------- El techo

    /**
     * El techo: losas de hormigón con sus vigas, los dos paneles de la luz general (encendidos de día, apagados de noche)
     * y, por la pared de la derecha, el conducto del aire, que baja en diagonal hacia el rincón (la contradiagonal).
     */
    fun DrawScope.techo(v: VistaLab) {
        val abajo = V3(0f, -1f, 0f)
        caraGrande(v, listOf(V3(XI, TECHO, ZF), V3(XD, TECHO, ZF), V3(XD, TECHO, Z_CERCA), V3(XI, TECHO, Z_CERCA)), lerp(C.HORMIGON, C.TIERRA, 0.12f), abajo, a = V3(0f, TECHO, ZF), b = V3(0f, TECHO, 2.5f))
        // Las vigas, de pared a pared: un canto que mira a los ojos y la cara de abajo.
        for (z in listOf(3.2f, 5.1f)) {
            val y0 = TECHO - 0.32f
            cara(v, listOf(V3(XI, y0, z), V3(XD, y0, z), V3(XD, TECHO, z), V3(XI, TECHO, z)), lerp(C.HORMIGON, C.TIERRA, 0.08f), FRENTE)
            cara(v, listOf(V3(XI, y0, z), V3(XD, y0, z), V3(XD, y0, z + 0.3f), V3(XI, y0, z + 0.3f)), lerp(C.HORMIGON, C.TIERRA, 0.18f), abajo)
            drawLine(v.c(V3(0f, y0, z), C.SOMBRA, FRENTE).copy(alpha = 0.35f), CamLab.p(XI, y0, z), CamLab.p(XD, y0, z), 0.6f)
        }
        // Los paneles de la luz general: dos tiras largas, a lo hondo, que brillan de día.
        val luz = v.general
        // Solo la fila de la derecha: a la izquierda va la banderita, sobre el techo liso.
        for (x in listOf(1.45f)) for ((z0, z1) in listOf(5.35f to 6.85f, 3.45f to 4.95f)) {
            val ps = listOf(V3(x - 0.28f, TECHO - 0.02f, z0), V3(x + 0.28f, TECHO - 0.02f, z0), V3(x + 0.28f, TECHO - 0.02f, z1), V3(x - 0.28f, TECHO - 0.02f, z1))
            drawPath(CamLab.forma(ps), v.c(V3(x, TECHO, (z0 + z1) / 2f), C.ACERO, abajo))
            val dentro = listOf(V3(x - 0.22f, TECHO - 0.03f, z0 + 0.05f), V3(x + 0.22f, TECHO - 0.03f, z0 + 0.05f), V3(x + 0.22f, TECHO - 0.03f, z1 - 0.05f), V3(x - 0.22f, TECHO - 0.03f, z1 - 0.05f))
            val apagado = v.c(V3(x, TECHO, z0), lerp(C.VIDRIO, C.NEGRO, 0.35f), abajo)
            drawPath(CamLab.forma(dentro), lerp(apagado, lerp(C.LUZ_DIA, Color.White, 0.4f), luz))
            if (luz > 0.05f) halo(V3(x, TECHO - 0.05f, (z0 + z1) / 2f), 1.2f, 0.4f, C.LUZ_DIA, 0.25f * luz)
            // Las rejillas del difusor.
            for (i in 1..5) { val z = z0 + (z1 - z0) * i / 6f; drawLine(lerp(apagado, C.ACERO, 0.5f).copy(alpha = 0.5f), CamLab.p(x - 0.22f, TECHO - 0.03f, z), CamLab.p(x + 0.22f, TECHO - 0.03f, z), 0.4f) }
        }
    }

    // ---------------------------------------------------------------- Una pared de hormigón

    /**
     * Las juntas del encofrado de una pared y sus agujeros de los latiguillos, los desconchones y las manchas: [punto] lleva
     * (a lo largo, alto) de la pared a la sala; [n] es hacia dónde mira. Nada es liso (regla 7).
     */
    private fun DrawScope.hormigon(v: VistaLab, punto: (Float, Float) -> V3, s0: Float, s1: Float, n: V3, semilla: String) {
        val junta = lerp(C.HORMIGON, C.SOMBRA, 0.35f)
        // Las juntas de los tableros: cada 1.2 m a lo largo y cada 0.6 m de alto.
        var y = 0.6f
        while (y < TECHO - 0.1f) { val a = punto(s0, y); val b = punto(s1, y); drawLine(v.c(a, junta, n).copy(alpha = 0.55f), CamLab.p(a), CamLab.p(b), 0.45f); y += 0.6f }
        var s = s0 + 0.3f
        while (s < s1) { val a = punto(s, 0f); val b = punto(s, TECHO); drawLine(v.c(a, junta, n).copy(alpha = 0.45f), CamLab.p(a), CamLab.p(b), 0.45f); s += 1.2f }
        val az = Azar(Semilla.de("laboratorio", semilla))
        // Los agujeros de los latiguillos: dos por tablero, tapados con mortero.
        s = s0 + 0.6f
        while (s < s1) {
            var yy = 0.3f
            while (yy < TECHO) {
                for (d in listOf(-0.3f, 0.3f)) { val q = punto(s + d, yy); val p = CamLab.p(q); val k = CamLab.k(q)
                    drawCircle(v.c(q, lerp(C.HORMIGON, C.TIERRA, 0.45f), n).copy(alpha = 0.7f), (0.018f * k).coerceIn(0.35f, 1.4f), p) }
                yy += 0.6f
            }
            s += 1.2f
        }
        // Los desconchones: la pintura saltada y el hormigón gris debajo, con su canto de luz.
        repeat(9) {
            val ss = az.entre(s0 + 0.2f, s1 - 0.2f); val yy = if (it < 4) az.entre(0.05f, 0.5f) else az.entre(0.6f, TECHO - 0.3f)
            val w = az.entre(0.05f, 0.16f); val h = az.entre(0.03f, 0.09f)
            val ps = (0 until 7).map { i -> val a = i * 2f * PI.toFloat() / 7f; val r = az.entre(0.6f, 1f); punto(ss + cos(a) * w * r, yy + sin(a) * h * r) }
            drawPath(CamLab.forma(ps), v.c(punto(ss, yy), lerp(C.TIERRA, C.HORMIGON, 0.4f), n))
            drawLine(v.c(punto(ss, yy), Color.White, n).copy(alpha = 0.35f), CamLab.p(ps[1]), CamLab.p(ps[3]), 0.4f)
        }
        // Las manchas grandes del lavado de la pintura, que no queda pareja, y las chorreras bajo los tubos y las juntas.
        repeat(8) {
            val ss = az.entre(s0, s1); val yy = az.entre(0.3f, TECHO - 0.3f); val q = punto(ss, yy); val k = CamLab.k(q)
            mancha(CamLab.p(q), az.entre(0.6f, 1.5f) * k, az.entre(0.35f, 0.9f) * k, if (it % 3 == 0) lerp(C.HORMIGON, C.TIERRA, 0.5f) else lerp(C.HORMIGON, C.SOMBRA, 0.35f), 0.07f)
        }
        repeat(10) {
            val ss = az.entre(s0 + 0.2f, s1 - 0.2f); val y0 = az.entre(1.2f, TECHO - 0.2f); val largo = az.entre(0.4f, 1.4f)
            val a = punto(ss, y0); val b = punto(ss + az.entre(-0.03f, 0.03f), (y0 - largo).coerceAtLeast(0.05f))
            drawLine(v.c(a, lerp(C.HORMIGON, C.TIERRA, 0.55f), n).copy(alpha = 0.22f), CamLab.p(a), CamLab.p(b), (0.02f * CamLab.k(a)).coerceAtLeast(0.6f), StrokeCap.Round)
        }
        // El zócalo gastado: la pintura sucia de rozaduras y de la fregona, más oscura junto al suelo.
        for (i in 0..14) { val ss = s0 + (s1 - s0) * i / 14f; val a = punto(ss, 0f); val b = punto(ss + (s1 - s0) / 14f, 0f)
            val alto = 0.1f + 0.08f * az.real()
            drawPath(CamLab.forma(listOf(a, b, punto(ss + (s1 - s0) / 14f, alto * 0.7f), punto(ss, alto))), v.c(a, lerp(C.HORMIGON, C.TIERRA, 0.5f), n).copy(alpha = 0.32f)) }
    }

    // ---------------------------------------------------------------- La pared del fondo

    fun DrawScope.paredDelFondo(v: VistaLab) {
        val n = FRENTE
        caraGrande(v, listOf(V3(XI, 0f, ZF), V3(XD, 0f, ZF), V3(XD, TECHO, ZF), V3(XI, TECHO, ZF)), C.HORMIGON, n, a = V3(-2.5f, 1.2f, ZF), b = V3(XD, 1.2f, ZF), pasos = 6)
        // Más claro arriba, junto a los paneles; más oscuro abajo, donde no llega.
        clipPath(formaDelFondo()) {
            val arriba = CamLab.p(0f, TECHO, ZF).y; val abajo = CamLab.p(0f, 0f, ZF).y
            drawRect(Brush.verticalGradient(0f to Color.White.copy(alpha = 0.07f * v.general), 0.6f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.12f), startY = arriba, endY = abajo), Offset(0f, arriba), Size(260f, abajo - arriba))
            hormigon(v, { s, y -> V3(s, y, ZF - 0.002f) }, XI, XD, n, "fondo")
        }
        tuboDelRiego(v)
        estanteDeCuadernos(v)
        reloj(v)
    }

    /**
     * El tubo del riego, que baja por la pared junto a la puerta y entra en el suelo, con su llave de latón; y el de arriba,
     * que corre bajo el techo de lado a lado. «Por los tubos de la pared pasan los riegos de la noche.»
     */
    private fun DrawScope.tuboDelRiego(v: VistaLab) {
        val x = PX0 - 0.2f; val z = ZF - 0.07f
        palo(v, V3(XI, TECHO - 0.18f, z), V3(XD - 0.05f, TECHO - 0.18f, z), 0.07f, C.ACERO)
        palo(v, V3(x, TECHO - 0.18f, z), V3(x, 0.16f, z), 0.06f, C.ACERO)
        palo(v, V3(x, 0.16f, z), V3(x, 0.16f, z - 0.25f), 0.06f, C.ACERO)
        // Las abrazaderas.
        for (y in listOf(0.6f, 1.6f, 2.6f)) { val p = CamLab.p(x, y, z - 0.03f); val k = CamLab.k(x, z); drawLine(v.c(V3(x, y, z), lerp(C.ACERO, C.NEGRO, 0.3f), FRENTE), p + Offset(-0.05f * k, 0f), p + Offset(0.05f * k, 0f), (0.02f * k).coerceAtLeast(0.6f)) }
        // La llave de latón, con su volante.
        val llave = V3(x, 1.05f, z - 0.05f); val p = CamLab.p(llave); val k = CamLab.k(llave)
        drawCircle(v.c(llave, C.LATON, V3(0.4f, 0.3f, -1f)), 0.055f * k, p)
        drawCircle(v.c(llave, lerp(C.LATON, C.NEGRO, 0.35f), FRENTE), 0.055f * k, p, style = Stroke(0.014f * k))
        drawLine(v.c(llave, lerp(C.LATON, Color.White, 0.3f), FRENTE), p + Offset(-0.05f * k, 0f), p + Offset(0.05f * k, 0f), 0.012f * k)
        if (EscenaLaboratorio.memoria(v.m)) {
            // El riego cerrado: la etiqueta roja colgada del volante, y un lazo negro.
            drawLine(v.c(llave, C.NEGRO, FRENTE), p + Offset(0.03f * k, 0.02f * k), p + Offset(0.05f * k, 0.1f * k), 0.5f)
            drawRect(v.c(llave, C.AVISO, FRENTE), p + Offset(0.03f * k, 0.1f * k), Size(0.05f * k, 0.08f * k))
            drawLine(v.c(llave, C.NEGRO, FRENTE), p + Offset(-0.04f * k, -0.04f * k), p + Offset(-0.07f * k, 0.12f * k), (0.02f * k).coerceAtLeast(0.6f))
            drawLine(v.c(llave, C.NEGRO, FRENTE), p + Offset(-0.04f * k, -0.04f * k), p + Offset(-0.01f * k, 0.13f * k), (0.02f * k).coerceAtLeast(0.6f))
        }
        // La mancha de humedad que deja bajo la llave.
        mancha(CamLab.p(x, 0.55f, ZF - 0.01f), 0.07f * k, 0.32f * k, lerp(C.TIERRA, C.SOMBRA, 0.4f), 0.16f)
    }

    /**
     * El estante de los cuadernos, con «cuarenta y tantos, todos en un estante, en orden», con el lomo a ras; desde que muere
     * Hilda, en la tabla de abajo, sus veinte cuadernos de tapas de tela. Y debajo, la percha con la bata cuando no la lleva
     * puesta y, desde que se va Lauro, la de repuesto de él, con la franja gris.
     */
    private fun DrawScope.estanteDeCuadernos(v: VistaLab) {
        val z1 = ZF - 0.01f; val z0 = ZF - 0.26f
        for (y in listOf(CY0, CY1)) caja(v, CX0, CX1, y - 0.03f, y, z0, z1, lerp(C.MADERA, C.HORMIGON, 0.15f))
        // Las ménsulas de hierro.
        for (x in listOf(CX0 + 0.15f, CX1 - 0.15f)) for (y in listOf(CY0, CY1)) palo(v, V3(x, y - 0.03f, z1), V3(x, y - 0.18f, z1), 0.02f, C.ACERO, filo = false)
        // Los cuadernos de ella: papel de fibra de los jardines, lomos iguales, a ras del canto.
        val az = Azar(Semilla.de("laboratorio", "cuadernos"))
        val n = 44; val ancho = (CX1 - CX0 - 0.08f) / n
        for (i in 0 until n) {
            val x0 = CX0 + 0.04f + i * ancho; val alto = 0.27f + 0.012f * az.real()
            val col = lerp(lerp(C.TELA, C.TIERRA, 0.22f + 0.1f * az.real()), C.GRAMA, 0.08f * az.real())
            drawPath(CamLab.forma(listOf(V3(x0, CY1, z0), V3(x0 + ancho * 0.92f, CY1, z0), V3(x0 + ancho * 0.92f, CY1 + alto, z0), V3(x0, CY1 + alto, z0))), v.c(V3(x0, CY1 + 0.12f, z0), col, FRENTE))
            // Una cinta cian en cada décimo lomo: el año.
            if (i % 10 == 9) drawLine(v.c(V3(x0, CY1, z0), C.CIAN, FRENTE), CamLab.p(x0 + ancho * 0.46f, CY1 + 0.05f, z0 - 0.002f), CamLab.p(x0 + ancho * 0.46f, CY1 + 0.09f, z0 - 0.002f), 0.6f)
        }
        // La tabla de abajo: los de Hilda (tela de colores apagados, más gruesos); antes, botes y cajas de muestras.
        if (EscenaLaboratorio.cuadernosDeHilda(v.m)) {
            val telas = listOf(lerp(C.AVISO, C.TIERRA, 0.5f), lerp(C.CIAN, C.TIERRA, 0.5f), lerp(C.AMARILLO, C.TIERRA, 0.45f), lerp(C.GRAMA, C.TIERRA, 0.45f), lerp(C.BARRO, C.TIERRA, 0.3f))
            val an = (CX1 - CX0 - 0.1f) / 20f
            for (i in 0 until 20) { val x0 = CX0 + 0.05f + i * an; val alto = 0.25f + 0.02f * ((i * 7) % 3)
                drawPath(CamLab.forma(listOf(V3(x0, CY0, z0), V3(x0 + an * 0.94f, CY0, z0), V3(x0 + an * 0.94f, CY0 + alto, z0), V3(x0, CY0 + alto, z0))), v.c(V3(x0, CY0 + 0.1f, z0), telas[i % telas.size], FRENTE)) }
        } else {
            for (i in 0..5) { val x = CX0 + 0.12f + i * 0.23f
                cilindro(v, V3(x, CY0, z0 + 0.1f), 0.06f, 0.16f + 0.03f * (i % 2), if (i % 3 == 0) C.BARRO else lerp(C.VIDRIO, C.TIERRA, 0.3f)) }
        }
        // La percha, debajo del estante, con sus ganchos.
        val yP = 1.52f; val zP = ZF - 0.04f
        palo(v, V3(CX0 + 0.1f, yP, zP), V3(CX1 - 0.05f, yP, zP), 0.03f, C.ACERO)
        if (!EscenaLaboratorio.enLaMesa(v.m) || v.tarea == EscenaLaboratorio.Tarea.MUSICA || EscenaLaboratorio.arriba(v.m) || v.tarea == EscenaLaboratorio.Tarea.DESAYUNO) {
            if (laSalaPinta(v.m, "bata")) bata(v, V3(CX0 + 0.55f, yP, zP - 0.05f), false)
        }
        if (EscenaLaboratorio.bataDeLauro(v.m) && !EscenaLaboratorio.cierre(v.m)) bata(v, V3(CX0 + 0.25f, yP, zP - 0.07f), true)
        if (laSalaPinta(v.m, "guantes")) guantes(v, V3(CX1 - 0.14f, yP, zP - 0.04f))
    }

    /**
     * Los guantes amarillos de ella, de goma larga, colgados de la percha con sus pinzas de madera, los dedos abajo: el
     * retrato de Hilda. Uno algo vuelto, con el pulgar hacia fuera.
     */
    private fun DrawScope.guantes(v: VistaLab, pinza: V3) {
        for ((i, dx) in listOf(-0.12f, 0f).withIndex()) {
            val g = pinza + V3(dx, 0f, 0f); val k = CamLab.k(g); val p = CamLab.p(g)
            fun q(a: Float, b: Float) = p + Offset(a * k, -b * k)
            val s = if (i == 0) -1f else 1f
            val luz = v.c(g + V3(0f, -0.2f, -0.05f), C.AMARILLO, V3(-0.4f, 0.2f, -1f)); val sombra = v.c(g + V3(0f, -0.2f, -0.05f), lerp(C.AMARILLO, C.BARRO, 0.35f), V3(0.8f, -0.2f, -0.5f))
            // El puño ancho, la palma y los cuatro dedos que cuelgan, algo abiertos.
            val guante = Path().apply {
                val a = q(-0.055f, -0.01f); moveTo(a.x, a.y)
                val b = q(0.055f, -0.01f); lineTo(b.x, b.y)
                val c = q(0.047f, -0.2f); val c1 = q(0.06f, -0.1f); cubicTo(c1.x, c1.y, c1.x, c1.y, c.x, c.y)
                val d = q(0.044f, -0.3f); lineTo(d.x, d.y)
                val e = q(-0.044f, -0.3f); val e1 = q(0f, -0.315f); cubicTo(e1.x, e1.y, e1.x, e1.y, e.x, e.y)
                val f = q(-0.047f, -0.2f); lineTo(f.x, f.y)
                val f1 = q(-0.06f, -0.1f); cubicTo(f1.x, f1.y, f1.x, f1.y, a.x, a.y)
                close()
            }
            drawPath(guante, Brush.horizontalGradient(0f to luz, 0.6f to luz, 1f to sombra, startX = q(-0.06f, 0f).x, endX = q(0.06f, 0f).x))
            for (j in 0..3) { val x = -0.03f + j * 0.02f
                drawLine(if (j < 3) luz else sombra, q(x, -0.29f), q(x + 0.004f * (j - 1.5f), -0.39f + 0.012f * kotlin.math.abs(j - 1.5f)), (0.019f * k).coerceAtLeast(0.6f), StrokeCap.Round) }
            // El pulgar, de lado.
            drawLine(sombra, q(s * 0.04f, -0.2f), q(s * 0.085f, -0.29f), (0.022f * k).coerceAtLeast(0.6f), StrokeCap.Round)
            // El canto vuelto del puño y el brillo de la goma.
            drawLine(lerp(sombra, C.BARRO, 0.2f), q(-0.056f, -0.035f), q(0.056f, -0.035f), (0.012f * k).coerceAtLeast(0.5f))
            drawLine(Color.White.copy(alpha = 0.3f + 0.2f * v.general), q(-0.025f, -0.08f), q(-0.022f, -0.25f), (0.008f * k).coerceAtLeast(0.4f), StrokeCap.Round)
            // La pinza de madera, sobre la barra.
            drawRect(v.c(g, lerp(C.MADERA, C.TELA, 0.3f), FRENTE), q(-0.012f, 0.05f), androidx.compose.ui.geometry.Size(0.024f * k, 0.09f * k))
            drawLine(v.c(g, C.ACERO, FRENTE), q(-0.012f, 0.0f), q(0.012f, 0.0f), 0.5f)
        }
    }

    /**
     * Una bata colgada de su gancho en [gancho]: blanca, con el cuello verde de Botánica y el canto cian; la de ella con la
     * quemadura de ácido en la manga y los bolsillos dados de sí; la de Lauro, con la franja gris en la manga.
     */
    fun DrawScope.bata(v: VistaLab, gancho: V3, deLauro: Boolean) {
        val k = CamLab.k(gancho); val p = CamLab.p(gancho)
        fun q(x: Float, y: Float) = p + Offset(x * k, -y * k)
        val largo = 1.0f
        val cuerpo = Path().apply {
            moveTo(q(-0.05f, 0f).x, q(-0.05f, 0f).y)
            cubicTo(q(-0.2f, -0.04f).x, q(-0.2f, -0.04f).y, q(-0.22f, -0.3f).x, q(-0.22f, -0.3f).y, q(-0.21f, -largo * 0.55f).x, q(-0.21f, -largo * 0.55f).y)
            lineTo(q(-0.25f, -largo).x, q(-0.25f, -largo).y)
            lineTo(q(0.22f, -largo + 0.03f).x, q(0.22f, -largo + 0.03f).y)
            lineTo(q(0.19f, -largo * 0.55f).x, q(0.19f, -largo * 0.55f).y)
            cubicTo(q(0.2f, -0.3f).x, q(0.2f, -0.3f).y, q(0.18f, -0.04f).x, q(0.18f, -0.04f).y, q(0.05f, 0f).x, q(0.05f, 0f).y)
            close()
        }
        val tela = C.TELA
        val luz = v.c(gancho + V3(0f, -0.4f, -0.1f), tela, V3(-0.3f, 0.1f, -1f)); val sombra = v.c(gancho + V3(0.15f, -0.4f, -0.1f), tela, V3(0.8f, 0f, -0.4f))
        drawPath(cuerpo, Brush.horizontalGradient(0f to luz, 0.55f to lerp(luz, sombra, 0.4f), 1f to sombra, startX = q(-0.25f, 0f).x, endX = q(0.22f, 0f).x))
        // Los pliegues que caen del gancho.
        for (x in listOf(-0.1f, 0.02f, 0.11f)) drawLine(sombra.copy(alpha = 0.5f), q(x * 0.4f, -0.08f), q(x, -largo + 0.04f), 0.012f * k)
        // El cuello verde, el canto cian por delante y los bolsillos.
        drawPath(poligono(q(-0.08f, -0.01f), q(0.08f, -0.01f), q(0.03f, -0.16f), q(0f, -0.12f), q(-0.03f, -0.16f)), v.c(gancho, C.CUELLO, FRENTE))
        drawLine(v.c(gancho, C.CIAN, FRENTE), q(0.01f, -0.15f), q(0.0f, -largo + 0.03f), 0.012f * k)
        for (x in listOf(-0.14f, 0.07f)) drawPath(poligono(q(x, -0.6f), q(x + 0.1f, -0.6f), q(x + 0.11f, -0.74f), q(x - 0.01f, -0.75f)), sombra.copy(alpha = 0.35f), style = Stroke(0.01f * k))
        // Una manga que cuelga de lado, con la quemadura (la de ella) o la franja gris (la de Lauro).
        val manga = poligono(q(0.12f, -0.08f), q(0.22f, -0.12f), q(0.26f, -0.62f), q(0.17f, -0.63f))
        drawPath(manga, sombra)
        if (deLauro) drawPath(poligono(q(0.17f, -0.36f), q(0.25f, -0.36f), q(0.255f, -0.42f), q(0.172f, -0.42f)), v.c(gancho, lerp(C.ACERO, C.TELA, 0.3f), FRENTE))
        else drawOval(v.c(gancho, lerp(C.TIERRA, C.BARRO, 0.4f), FRENTE).copy(alpha = 0.7f), q(0.18f, -0.5f), Size(0.05f * k, 0.035f * k))
    }

    /** El reloj de la pared, sobre la puerta: sin números, con sus marcas, y las agujas a la hora del cuadro («el día lo hacen las lámparas y el reloj»). */
    private fun DrawScope.reloj(v: VistaLab) {
        val c = V3(-0.22f, 2.72f, ZF - 0.03f); val p = CamLab.p(c); val k = CamLab.k(c); val r = 0.17f * k
        drawCircle(v.c(c, lerp(C.ACERO, C.NEGRO, 0.2f), FRENTE), r * 1.12f, p)
        drawCircle(v.c(c, C.TELA, FRENTE), r, p)
        for (i in 0 until 12) { val a = i * PI.toFloat() / 6f; drawLine(v.c(c, C.NEGRO, FRENTE), p + Offset(sin(a), -cos(a)) * (r * 0.78f), p + Offset(sin(a), -cos(a)) * (r * (if (i % 3 == 0) 0.6f else 0.7f)), if (i % 3 == 0) 0.7f else 0.4f) }
        val h = v.m.hora.toFloat(); val ah = (h % 12f) / 12f * 2f * PI.toFloat(); val am = (h % 1f) * 2f * PI.toFloat()
        drawLine(v.c(c, C.NEGRO, FRENTE), p, p + Offset(sin(ah), -cos(ah)) * (r * 0.5f), 0.9f, StrokeCap.Round)
        drawLine(v.c(c, C.NEGRO, FRENTE), p, p + Offset(sin(am), -cos(am)) * (r * 0.76f), 0.6f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- La puerta, el pasillo y los jardines

    /** El pasillo de Botánica y los jardines al fondo, vistos por el hueco de la puerta; luego, el marco y las hojas de vidrio. */
    fun DrawScope.puerta(v: VistaLab) {
        clipPath(formaDeLaPuerta()) { pasillo(v) }
        // El marco de acero.
        val z = ZF - 0.01f; val g = 0.07f
        for ((a, b) in listOf(V3(PX0, 0f, z) to V3(PX0, PH, z), V3(PX1, 0f, z) to V3(PX1, PH, z), V3(PX0, PH, z) to V3(PX1, PH, z), V3((PX0 + PX1) / 2f, 0f, z) to V3((PX0 + PX1) / 2f, PH, z)))
            palo(v, a, b, g, lerp(C.ACERO, C.HORMIGON, 0.2f))
        // El vidrio: un velo muy leve, los reflejos al sesgo y la barra de empujar de cada hoja.
        val vidrio = formaDeLaPuerta()
        drawPath(vidrio, v.c(V3(0f, 1.2f, z), C.VIDRIO, FRENTE).copy(alpha = 0.1f))
        for ((x0, x1) in listOf(PX0 + 0.12f to PX0 + 0.22f, 0.05f to 0.12f, 0.3f to 0.34f)) drawPath(CamLab.forma(listOf(V3(x0, 0.3f, z), V3(x1, 0.3f, z), V3(x1 + 0.25f, PH - 0.1f, z), V3(x0 + 0.25f, PH - 0.1f, z))), Color.White.copy(alpha = 0.05f + 0.04f * v.general))
        for (x in listOf(PX0 + 0.1f, PX1 - 0.1f)) palo(v, V3(x, 1.0f, z - 0.05f), V3(x + (if (x < 0f) 0.55f else -0.55f), 1.0f, z - 0.05f), 0.03f, C.ACERO)
        // La noche larga: «se cuelgan telas en las puertas».
        if (EscenaLaboratorio.nocheLarga(v.m)) {
            val tela = Path().apply { val a = CamLab.p(PX0 + 0.05f, PH - 0.05f, z - 0.06f); val b = CamLab.p(PX1 - 0.05f, PH - 0.05f, z - 0.06f); moveTo(a.x, a.y)
                val c1 = CamLab.p(PX0 + 0.4f, PH - 0.4f, z - 0.06f); val c2 = CamLab.p(PX1 - 0.4f, PH - 0.4f, z - 0.06f); cubicTo(c1.x, c1.y, c2.x, c2.y, b.x, b.y); lineTo(b.x, b.y - 2f); lineTo(a.x, a.y - 2f); close() }
            drawPath(tela, v.c(V3(0f, PH - 0.2f, z), lerp(C.AMARILLO, C.BARRO, 0.5f), FRENTE))
        }
    }

    /**
     * Lo que hay al otro lado del vidrio: el suelo del pasillo con las flechas de color que llevan a los jardines, el techo
     * con su fila de luces y, al fondo, la pared de vidrio de la nave de los jardines, con los tanques según el arco. De
     * día, el blanco de las lámparas que imitan el día; de noche, el violeta; con aviso, la luz roja del pasillo.
     */
    private fun DrawScope.pasillo(v: VistaLab) {
        val m = v.m; val noche = v.noche; val roja = v.roja
        val luzPasillo = when { roja -> lerp(C.AVISO, C.SOMBRA, 0.42f); noche -> lerp(C.SOMBRA, C.VIOLETA, 0.35f * v.violeta + 0.1f); else -> lerp(C.HORMIGON, C.LUZ_DIA, 0.4f) }
        val oscuro = if (EscenaLaboratorio.nocheLarga(m) && noche || EscenaLaboratorio.apagon(m) && noche) 0.25f else 1f
        fun tinte(c: Color, k: Float = 1f) = Color((c.red * luzPasillo.red * k * oscuro).coerceIn(0f, 1f), (c.green * luzPasillo.green * k * oscuro).coerceIn(0f, 1f), (c.blue * luzPasillo.blue * k * oscuro).coerceIn(0f, 1f))
        // El fondo: los jardines (se pintan primero; el pasillo los enmarca).
        jardines(v)
        // El suelo, las paredes y el techo del pasillo.
        val fin = PZ1
        drawPath(CamLab.forma(listOf(V3(PXI, 0f, ZF), V3(PXD, 0f, ZF), V3(PXD, 0f, fin), V3(PXI, 0f, fin))), Brush.verticalGradient(0f to tinte(C.PISO, 1.05f), 1f to tinte(C.PISO, 0.8f), startY = CamLab.p(0f, 0f, fin).y, endY = CamLab.p(0f, 0f, ZF).y))
        drawPath(CamLab.forma(listOf(V3(PXI, 0f, ZF), V3(PXI, 0f, fin), V3(PXI, PT, fin), V3(PXI, PT, ZF))), tinte(C.HORMIGON, 0.85f))
        drawPath(CamLab.forma(listOf(V3(PXD, 0f, ZF), V3(PXD, 0f, fin), V3(PXD, PT, fin), V3(PXD, PT, ZF))), tinte(C.HORMIGON, 0.75f))
        drawPath(CamLab.forma(listOf(V3(PXI, PT, ZF), V3(PXD, PT, ZF), V3(PXD, PT, fin), V3(PXI, PT, fin))), tinte(C.HORMIGON, 0.6f))
        // Las luces del techo del pasillo (rojas con aviso), y su reflejo en el suelo.
        for (i in 0..2) { val z = ZF + 0.5f + i * 1.15f; val col = if (roja) C.AVISO else if (noche) lerp(C.VIOLETA, Color.White, 0.3f) else Color.White
            val k = if (oscuro < 1f) 0.15f else 1f
            drawPath(CamLab.forma(listOf(V3(-0.5f, PT - 0.01f, z), V3(0.1f, PT - 0.01f, z), V3(0.1f, PT - 0.01f, z + 0.5f), V3(-0.5f, PT - 0.01f, z + 0.5f))), col.copy(alpha = (if (noche && !roja) 0.5f else 0.9f) * k))
            mancha(CamLab.p(-0.2f, 0f, z + 0.25f), 6f, 1.2f, col, 0.22f * k) }
        // Las flechas de color en el suelo, hacia los jardines: verde de Botánica y cian.
        for ((i, z) in listOf(7.5f, 8.5f, 9.5f).withIndex()) { val col = if (i % 2 == 0) C.GRAMA else C.CIAN
            drawPath(CamLab.forma(listOf(V3(-0.55f, 0.002f, z), V3(-0.2f, 0.002f, z + 0.5f), V3(0.15f, 0.002f, z), V3(0.15f, 0.002f, z + 0.18f), V3(-0.2f, 0.002f, z + 0.68f), V3(-0.55f, 0.002f, z + 0.18f))), tinte(col, 0.9f)) }
        // Las otras puertas de vidrio de Botánica, en la pared del pasillo, y un banco.
        for (z in listOf(7.9f)) drawPath(CamLab.forma(listOf(V3(PXI + 0.01f, 0f, z), V3(PXI + 0.01f, 0f, z + 1.0f), V3(PXI + 0.01f, 2.1f, z + 1.0f), V3(PXI + 0.01f, 2.1f, z))), tinte(lerp(C.VIDRIO, C.HORMIGON, 0.4f), 1.05f))
        // La siega: cestas y cajas con hojas en el pasillo, camino del abono.
        if (EscenaLaboratorio.siega(m) > 0) for ((i, z) in listOf(8.3f, 9.6f).withIndex()) {
            val x0 = 0.35f - 0.1f * i
            drawPath(CamLab.forma(listOf(V3(x0, 0f, z), V3(x0 + 0.5f, 0f, z), V3(x0 + 0.5f, 0.38f, z), V3(x0, 0.38f, z))), tinte(C.MADERA, 0.9f))
            drawPath(CamLab.forma(listOf(V3(x0 - 0.03f, 0.38f, z), V3(x0 + 0.53f, 0.38f, z), V3(x0 + 0.45f, 0.55f, z), V3(x0 + 0.05f, 0.55f, z))), tinte(C.GRAMA, 1.1f))
        }
        // La cuarentena: la compuerta del ala, bajada al final del pasillo, de acero con su franja de aviso y su ventanilla, por
        // la que se ven los jardines «con sus lámparas encendidas para nadie»; encima, su luz roja.
        if (EscenaLaboratorio.cuarentena(m)) {
            val z = PZ1 - 0.8f
            val hoja = Path().apply {
                addPath(CamLab.forma(listOf(V3(PXI, 0f, z), V3(PXD, 0f, z), V3(PXD, PT, z), V3(PXI, PT, z))))
                addPath(CamLab.forma(listOf(V3(-0.5f, 1.0f, z), V3(0.25f, 1.0f, z), V3(0.25f, 1.75f, z), V3(-0.5f, 1.75f, z))))
                fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
            }
            // El acero de la hoja, gris oscuro, con la luz roja que le cae desde arriba.
            val acero = lerp(C.ACERO, C.NEGRO, 0.35f)
            drawPath(hoja, Brush.verticalGradient(0f to lerp(acero, C.AVISO, 0.3f), 0.3f to lerp(acero, C.AVISO, 0.1f), 1f to lerp(acero, C.NEGRO, 0.45f), startY = CamLab.p(0f, PT, z).y, endY = CamLab.p(0f, 0f, z).y))
            // El marco de la ventanilla, grueso, y las juntas de las planchas.
            drawPath(CamLab.forma(listOf(V3(-0.5f, 1.0f, z), V3(0.25f, 1.0f, z), V3(0.25f, 1.75f, z), V3(-0.5f, 1.75f, z))), lerp(acero, C.NEGRO, 0.4f), style = Stroke(1.6f))
            for (x in listOf(-0.75f, 0.5f)) drawLine(lerp(acero, C.NEGRO, 0.45f), CamLab.p(x, 0.3f, z), CamLab.p(x, PT - 0.1f, z), 0.5f)
            // Los remaches y el volante de cierre.
            for (x in listOf(PXI + 0.12f, PXD - 0.12f)) for (y in listOf(0.3f, 1.2f, 2.2f)) drawCircle(lerp(acero, Color.White, 0.25f), 0.45f, CamLab.p(x, y, z - 0.01f))
            val volante = CamLab.p(0.55f, 1.2f, z - 0.02f); val kv = CamLab.k(0.55f, z)
            drawCircle(lerp(acero, C.AVISO, 0.25f), 0.12f * kv, volante, style = Stroke(0.025f * kv))
            drawLine(lerp(acero, C.AVISO, 0.25f), volante + Offset(-0.12f * kv, 0f), volante + Offset(0.12f * kv, 0f), 0.02f * kv)
            for (y in listOf(0.55f, 1.95f)) drawLine(tinte(lerp(C.ACERO, C.NEGRO, 0.3f), 1.1f), CamLab.p(PXI, y, z), CamLab.p(PXD, y, z), 0.5f)
            for (s in 0..7) { val x = PXI + s * (PXD - PXI) / 7f; drawLine(Color(0xFF1E1A16).copy(alpha = 0.55f), CamLab.p(x, 0.04f, z - 0.01f), CamLab.p(x + 0.15f, 0.22f, z - 0.01f), 1.1f) }
            for (s in 0..7) { val x = PXI + 0.15f + s * (PXD - PXI) / 7f; drawLine(tinte(C.AMARILLO, 1.3f).copy(alpha = 0.8f), CamLab.p(x, 0.04f, z - 0.01f), CamLab.p(x + 0.15f, 0.22f, z - 0.01f), 1.1f) }
            val luzRoja = CamLab.p(-0.12f, PT - 0.12f, z - 0.05f)
            drawCircle(lerp(C.AVISO, Color.White, 0.3f), 1.1f, luzRoja); mancha(luzRoja, 9f, 6f, C.AVISO, 0.7f)
            // Su luz en el suelo y las paredes del pasillo: más fuerte junto a la compuerta.
            drawPath(CamLab.forma(listOf(V3(PXI, 0.001f, ZF), V3(PXD, 0.001f, ZF), V3(PXD, 0.001f, z), V3(PXI, 0.001f, z))),
                Brush.verticalGradient(0f to C.AVISO.copy(alpha = 0.4f), 1f to C.AVISO.copy(alpha = 0.05f), startY = CamLab.p(0f, 0f, z).y, endY = CamLab.p(0f, 0f, ZF).y), blendMode = BlendMode.Plus)
        }
    }

    /**
     * La nave de los jardines, al final del pasillo: una pared de vidrio y, detrás, las filas de tanques con las raíces
     * colgando en el agua y, encima, lo que crece según el arco `jardines`: los tanques vacíos y lavados con los hilos
     * colgando; una pelusa verde de brotes; verdes y a media altura, con las judías a mitad de los hilos; o llenos, con los
     * tomates rojos. El primer día de la siega, a medio segar; el segundo y el tercero, «una nave de vidrio limpio». El
     * techo «se pierde en el resplandor de las lámparas».
     */
    private fun DrawScope.jardines(v: VistaLab) {
        val m = v.m; val noche = v.noche
        val apagado = (EscenaLaboratorio.nocheLarga(m) || EscenaLaboratorio.apagon(m)) && noche
        val luz = when { apagado -> lerp(C.SOMBRA, Color.Black, 0.5f); v.jardinBlanco || !noche -> lerp(C.LUZ_DIA, Color.White, 0.3f); else -> C.VIOLETA }
        val roja = v.roja
        val fondoY = CamLab.p(0f, 0f, PZ1).y
        val cieloArriba = CamLab.p(0f, PT, PZ1).y
        // El resplandor de la nave, que llena todo el fondo.
        val claro = if (roja) lerp(luz, C.SOMBRA, 0.45f) else luz
        drawRect(Brush.verticalGradient(0f to lerp(claro, Color.White, if (apagado) 0f else 0.4f), 0.7f to claro, 1f to lerp(claro, C.SOMBRA, 0.3f), startY = cieloArriba - 20f, endY = fondoY + 6f), Offset(30f, cieloArriba - 30f), Size(110f, fondoY - cieloArriba + 40f))
        // Las filas de tanques, de lejos a cerca; cada fila con su agua, sus raíces y lo que crece encima.
        val tramo = EscenaLaboratorio.tramoJardines(m); val siega = EscenaLaboratorio.siega(m)
        val alto = when { siega >= 2 -> 0f; tramo == Jardines.VACIOS -> 0f; tramo == Jardines.BROTES -> 0.07f; tramo == Jardines.MEDIO -> 0.85f; else -> 1.7f }
        val az = Azar(Semilla.de("laboratorio", "jardines"))
        for (fila in 3 downTo 0) {
            val z = PZ1 + 0.7f + fila * 2.0f
            val lejos = 0.08f + 0.12f * fila
            fun col(c: Color) = lerp(Color(c.red * claro.red, c.green * claro.green, c.blue * claro.blue), claro, lejos)
            for (x in listOf(-3.2f, -1.2f, 0.8f, 2.8f)) {
                val x0 = x - 0.75f; val x1 = x + 0.75f
                // El tanque de vidrio con su agua.
                drawPath(CamLab.forma(listOf(V3(x0, 0f, z), V3(x1, 0f, z), V3(x1, 0.75f, z), V3(x0, 0.75f, z))), col(lerp(C.VIDRIO, C.CIAN, 0.25f)))
                drawLine(col(Color.White), CamLab.p(x0, 0.75f, z), CamLab.p(x1, 0.75f, z), 0.5f)
                // Las raíces colgando en el agua, «como barbas blancas» (si hay algo que tenga raíz).
                if (alto > 0.05f) for (i in 0..5) { val xr = x0 + 0.15f + i * 0.24f; drawLine(col(C.TELA).copy(alpha = 0.7f), CamLab.p(xr, 0.72f, z), CamLab.p(xr + 0.03f, 0.72f - 0.45f * (0.5f + az.real()) * (alto / 1.15f).coerceIn(0.3f, 1f), z), 0.45f) }
                // Los hilos de las judías, de la viga al tanque.
                for (i in 0..3) { val xh = x0 + 0.2f + i * 0.37f; drawLine(col(C.ACERO).copy(alpha = 0.5f), CamLab.p(xh, 0.75f, z), CamLab.p(xh, 2.6f, z), 0.3f) }
                // Lo que crece, según el tramo: la pelusa de brotes; las lechugas y las judías por los hilos, a media altura o
                // hasta arriba; llenos, una masa con los tomates rojos. El primer día de la siega, la mitad ya segada.
                val cortado = siega == 1 && x < 0f
                val kz = CamLab.k(x, z)
                if (alto > 0.05f && alto < 0.1f && !cortado) {
                    drawLine(col(lerp(C.GRAMA, Color.White, 0.25f)), CamLab.p(x0 + 0.05f, 0.78f, z), CamLab.p(x1 - 0.05f, 0.78f, z), (0.05f * kz).coerceAtLeast(0.8f))
                } else if (alto > 0.1f && !cortado) {
                    val h = alto * (0.85f + 0.3f * az.real())
                    if (tramo == Jardines.LLENOS) {
                        val mata = Path().apply {
                            val a = CamLab.p(x0, 0.75f, z); moveTo(a.x, a.y)
                            for (i in 0..8) { val xx = x0 + (x1 - x0) * i / 8f; val yy = 0.75f + h * (0.75f + 0.25f * az.real()) * (if (i % 2 == 0) 1f else 0.86f); val q = CamLab.p(xx, yy, z); lineTo(q.x, q.y) }
                            val b = CamLab.p(x1, 0.75f, z); lineTo(b.x, b.y); close()
                        }
                        drawPath(mata, col(C.HOJA_OSC))
                    }
                    // Las judías, por cada hilo, hasta la altura del tramo.
                    for (i in 0..3) { val xh = x0 + 0.2f + i * 0.37f
                        drawLine(col(lerp(C.HOJA_OSC, C.GRAMA, 0.4f)), CamLab.p(xh, 0.75f, z), CamLab.p(xh, 0.75f + h, z), (0.1f * kz).coerceAtLeast(1f), StrokeCap.Round)
                        repeat(5) { val yy = 0.8f + az.entre(0f, h); drawCircle(col(C.GRAMA), kz * 0.055f, CamLab.p(xh + az.entre(-0.06f, 0.06f), yy, z)) } }
                    // Las lechugas, sobre el tanque.
                    for (i in 0..3) { val xl = x0 + 0.2f + i * 0.37f + 0.18f; drawCircle(col(lerp(C.GRAMA, Color.White, 0.2f)), kz * 0.09f, CamLab.p(xl, 0.82f, z)) }
                    if (tramo == Jardines.LLENOS && siega == 0) repeat(9) { val xx = az.entre(x0 + 0.1f, x1 - 0.1f); val yy = 0.9f + az.entre(0f, h * 0.75f); drawCircle(col(C.TOMATE), kz * 0.07f, CamLab.p(xx, yy, z)) }
                }
            }
            // La viga de la que cuelgan las lámparas de la nave.
            drawLine(col(C.ACERO), CamLab.p(-4f, 2.6f, z), CamLab.p(4f, 2.6f, z), 0.8f)
        }
        // La pared de vidrio de la nave, con sus montantes, al final del pasillo.
        for (x in listOf(-0.95f, -0.2f, 0.55f)) drawLine(lerp(claro, C.ACERO, 0.6f), CamLab.p(x, 0f, PZ1), CamLab.p(x, PT, PZ1), 0.7f)
        drawLine(lerp(claro, C.ACERO, 0.6f), CamLab.p(PXI, 1.2f, PZ1), CamLab.p(PXD, 1.2f, PZ1), 0.5f)
    }

    // ---------------------------------------------------------------- La pared de la derecha

    /** La pared de la derecha, en fuga: el hormigón con sus juntas y, en ella, el marco de la pantalla de la Vigía con su repisa. */
    fun DrawScope.paredDerecha(v: VistaLab) {
        val n = IZQ
        caraGrande(v, listOf(V3(XD, 0f, ZF), V3(XD, 0f, Z_CERCA), V3(XD, TECHO, Z_CERCA), V3(XD, TECHO, ZF)), C.HORMIGON, n, a = V3(XD, 1.4f, ZF), b = V3(XD, 1.4f, 1.6f), pasos = 6)
        clipPath(formaDerecha()) {
            hormigon(v, { s, y -> V3(XD - 0.002f, y, s) }, Z_CERCA, ZF, n, "derecha")
            // La pilastra del rincón.
            cara(v, listOf(V3(XD - 0.12f, 0f, ZF - 0.12f), V3(XD - 0.12f, 0f, ZF - 0.002f), V3(XD - 0.12f, TECHO, ZF - 0.002f), V3(XD - 0.12f, TECHO, ZF - 0.12f)), lerp(C.HORMIGON, C.TIERRA, 0.06f), FRENTE)
        }
        // El canal de cables que corre bajo el techo hacia el rincón.
        palo(v, V3(XD - 0.05f, TECHO - 0.12f, 1.0f), V3(XD - 0.05f, TECHO - 0.12f, ZF - 0.1f), 0.06f, lerp(C.ACERO, C.HORMIGON, 0.3f))
    }

    /**
     * Los cables de la pared de pantallas, que suben del marco al canal del techo: gruesos, de colores gastados, cada uno
     * con su comba, y una caja de empalmes. Van después del marco.
     */
    fun DrawScope.cables(v: VistaLab) {
        val arriba = PY1 + 0.12f; val canal = TECHO - 0.15f
        val cables = listOf(Triple(2.55f, 2.75f, C.NEGRO), Triple(2.85f, 3.05f, lerp(C.ACERO, C.NEGRO, 0.5f)), Triple(3.25f, 3.1f, lerp(C.AMARILLO, C.NEGRO, 0.45f)),
            Triple(3.6f, 3.3f, C.NEGRO), Triple(4.1f, 3.6f, lerp(C.CIAN, C.NEGRO, 0.55f)), Triple(4.45f, 3.9f, lerp(C.ACERO, C.NEGRO, 0.4f)))
        for ((i, c) in cables.withIndex()) {
            val (z0, z1, col) = c
            val a = CamLab.p(XD - 0.07f, arriba, z0); val b = CamLab.p(XD - 0.07f, canal, z1)
            val comba = (b.x - a.x) * 0.35f + 1.5f + (i % 3)
            val ancho = (0.022f * CamLab.k(XD, (z0 + z1) / 2f)).coerceAtLeast(0.7f)
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x - comba, a.y - (a.y - b.y) * 0.3f, b.x - comba * 0.6f, b.y + (a.y - b.y) * 0.3f, b.x, b.y) },
                v.c(V3(XD - 0.1f, (arriba + canal) / 2f, (z0 + z1) / 2f), col, IZQ), style = Stroke(ancho, cap = StrokeCap.Round))
        }
        caja(v, XD - 0.16f, XD - 0.01f, canal - 0.12f, canal + 0.04f, 3.15f, 3.55f, lerp(C.ACERO, C.HORMIGON, 0.2f))
    }

    /**
     * El conducto del aire de arriba: un tubo redondo de chapa que cruza el techo a lo hondo, del borde de arriba hacia la
     * pared del fondo, y que recoge el aire de la campana (la contradiagonal y el marco de arriba). Con sus anillos de unión,
     * los tirantes que lo cuelgan del techo y una rejilla. Se ve su cara de abajo y la de la izquierda.
     */
    fun DrawScope.conducto(v: VistaLab) {
        val x = CONDUCTO_X; val y = CONDUCTO_Y; val r = CONDUCTO_R
        val z0 = 1.6f; val z1 = ZF - 0.02f
        val chapa = lerp(C.ACERO, C.HORMIGON, 0.35f)
        val arribaIzq = { z: Float -> CamLab.p(x - r * 0.75f, y + r * 0.66f, z) }
        val abajoDer = { z: Float -> CamLab.p(x + r * 0.55f, y - r * 0.85f, z) }
        val forma = poligono(listOf(arribaIzq(z0), arribaIzq(z1), abajoDer(z1), abajoDer(z0)))
        // El costado (de la izquierda a la cara de abajo): más claro abajo, donde le llega la luz de la sala.
        val luz = v.c(V3(x, y - r, 3.5f), chapa, V3(-0.3f, -1f, 0f)); val lado = v.c(V3(x - r, y, 3.5f), chapa, V3(-1f, 0f, 0f))
        val a = arribaIzq(3.5f); val b = abajoDer(3.5f)
        drawPath(forma, Brush.linearGradient(0f to lerp(lado, C.SOMBRA, 0.25f), 0.45f to lado, 0.8f to luz, 1f to lerp(luz, Color.White, 0.15f), start = a, end = b))
        drawLine(lerp(luz, Color.White, 0.3f).copy(alpha = 0.6f), CamLab.p(x + r * 0.2f, y - r, z0), CamLab.p(x + r * 0.2f, y - r, z1), 0.6f)
        // Los anillos de unión y los tirantes al techo.
        var z = 2.0f
        while (z < z1) {
            drawLine(v.c(V3(x, y, z), lerp(chapa, C.NEGRO, 0.3f), V3(-0.5f, -1f, 0f)).copy(alpha = 0.7f), arribaIzq(z), abajoDer(z), (0.02f * CamLab.k(x, z)).coerceAtLeast(0.6f))
            drawLine(v.c(V3(x, y, z), lerp(chapa, C.NEGRO, 0.4f), V3(-1f, 0f, 0f)), CamLab.p(x - r * 0.4f, y + r * 0.9f, z), CamLab.p(x - r * 0.4f, TECHO, z), 0.6f)
            z += 1.1f
        }
        // La rejilla de abajo, que sopla el aire a la sala.
        val g0 = 3.1f; val g1 = 3.55f
        drawPath(CamLab.forma(listOf(V3(x - 0.1f, y - r * 0.95f, g0), V3(x + 0.12f, y - r * 0.95f, g0), V3(x + 0.12f, y - r * 0.95f, g1), V3(x - 0.1f, y - r * 0.95f, g1))), v.c(V3(x, y - r, g0), lerp(chapa, C.NEGRO, 0.5f), V3(0f, -1f, 0f)))
        for (i in 1..5) { val zz = g0 + (g1 - g0) * i / 6f; drawLine(v.c(V3(x, y - r, zz), chapa, V3(0f, -1f, 0f)), CamLab.p(x - 0.1f, y - r * 0.95f, zz), CamLab.p(x + 0.12f, y - r * 0.95f, zz), 0.5f) }
    }

    /**
     * El marco de la pared de pantallas: un bastidor grueso de metal viejo atornillado a la pared, con su canto, sus
     * tornillos a lo largo, la repisa y el brillo propio de la imagen que se desborda por su borde de dentro; la imagen la
     * pinta [PantallaLab].
     */
    fun DrawScope.marcoDeLaPantalla(v: VistaLab) {
        val x = XD - 0.01f; val g = 0.12f; val hondo = 0.11f
        val m0 = PZ_LEJOS + g; val m1 = PZ_CERCA - g; val y0 = PY0 - 0.08f; val y1 = PY1 + g
        // El canto del marco, que sale de la pared (se ve el de abajo y el lejano).
        cara(v, listOf(V3(x, y0, m0), V3(x - hondo, y0, m0), V3(x - hondo, y1, m0), V3(x, y1, m0)), lerp(C.ACERO, C.NEGRO, 0.2f), V3(0f, 0f, 1f))
        cara(v, listOf(V3(x, y0, m0), V3(x - hondo, y0, m0), V3(x - hondo, y0, m1), V3(x, y0, m1)), lerp(C.ACERO, C.NEGRO, 0.35f), V3(0f, -1f, 0f))
        // El frente del marco, con el hueco de la imagen.
        val frente = Path().apply {
            addPath(CamLab.forma(listOf(V3(x - hondo, y0, m0), V3(x - hondo, y0, m1), V3(x - hondo, y1, m1), V3(x - hondo, y1, m0))))
            addPath(CamLab.forma(listOf(V3(x - hondo, PY0, PZ_LEJOS), V3(x - hondo, PY1, PZ_LEJOS), V3(x - hondo, PY1, PZ_CERCA), V3(x - hondo, PY0, PZ_CERCA))))
            fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        }
        drawPath(frente, Brush.linearGradient(0f to v.c(V3(x, 2f, m0), lerp(C.ACERO, C.NEGRO, 0.5f), IZQ), 1f to v.c(V3(x, 2f, m1), lerp(C.ACERO, C.NEGRO, 0.5f), IZQ), start = CamLab.p(x, 2f, m0), end = CamLab.p(x, 2f, m1)))
        // El canto de arriba del bastidor, con algo de luz del techo.
        drawPath(CamLab.forma(listOf(V3(x - hondo, y1, m0), V3(x - hondo, y1, m1), V3(x - hondo, y1 - 0.025f, m1), V3(x - hondo, y1 - 0.025f, m0))), v.c(V3(x, y1, 3.5f), lerp(C.ACERO, Color.White, 0.1f), V3(-0.5f, 1f, 0f)).copy(alpha = 0.6f))
        // La junta entre el bastidor y la imagen: un filete oscuro.
        drawPath(CamLab.forma(listOf(V3(x - hondo, PY0, PZ_LEJOS), V3(x - hondo, PY1, PZ_LEJOS), V3(x - hondo, PY1, PZ_CERCA), V3(x - hondo, PY0, PZ_CERCA))), C.NEGRO.copy(alpha = 0.8f), style = Stroke(1.6f))
        // El brillo de la imagen que se desborda por el borde de dentro del marco.
        val brillo = (0.3f * v.pantalla + if (v.noche) 0.1f else 0f).coerceIn(0f, 1f)
        if (brillo > 0.02f) for ((ancho, a) in listOf(6f to 0.18f, 3f to 0.3f)) drawPath(CamLab.forma(listOf(V3(x - hondo, PY0, PZ_LEJOS), V3(x - hondo, PY1, PZ_LEJOS), V3(x - hondo, PY1, PZ_CERCA), V3(x - hondo, PY0, PZ_CERCA))),
            v.colorPantalla.copy(alpha = a * brillo), style = Stroke(ancho), blendMode = BlendMode.Plus)
        // Los tornillos del marco, a lo largo, y un piloto (verde si está encendida).
        val tornillo = v.c(V3(x, 2f, 3.5f), lerp(C.ACERO, Color.White, 0.3f), IZQ); val sombra = lerp(C.NEGRO, C.ACERO, 0.2f)
        for (i in 0..8) { val z = m1 + 0.06f + (m0 - m1 - 0.12f) * i / 8f
            for (y in listOf(y0 + 0.04f, y1 - 0.06f)) { val p = CamLab.p(x - hondo, y, z); drawCircle(sombra, 0.75f, p + Offset(0.25f, 0.3f)); drawCircle(tornillo, 0.6f, p) } }
        for (i in 0..4) { val y = y0 + 0.1f + (y1 - y0 - 0.2f) * i / 4f
            for (z in listOf(m0 - 0.06f, m1 + 0.06f)) { val p = CamLab.p(x - hondo, y, z); drawCircle(sombra, 0.75f, p + Offset(0.25f, 0.3f)); drawCircle(tornillo, 0.6f, p) } }
        val piloto = CamLab.p(x - hondo, y0 + 0.035f, PZ_LEJOS + 0.2f)
        drawCircle(if (EscenaLaboratorio.pantallaApagada(v.m)) C.AVISO.copy(alpha = 0.7f) else lerp(C.GRAMA, Color.White, 0.3f), 0.8f, piloto)
        if (EscenaLaboratorio.memoria(v.m)) {
            // El crespón negro, atado en la esquina de arriba del marco, con sus dos colas.
            val e = V3(x - hondo - 0.01f, y1 - 0.05f, m0 - 0.06f); val k = CamLab.k(e); val p = CamLab.p(e)
            val negro = v.c(e, lerp(C.NEGRO, C.SOMBRA, 0.15f), IZQ)
            drawPath(poligono(p + Offset(-0.06f * k, -0.04f * k), p + Offset(0.07f * k, -0.03f * k), p + Offset(0.02f * k, 0.03f * k)), negro)
            for ((dx, largo) in listOf(-0.03f to 0.32f, 0.03f to 0.26f)) drawLine(negro, p + Offset(dx * k, 0f), p + Offset((dx + 0.02f) * k, largo * k), (0.035f * k).coerceAtLeast(0.8f))
        }
        // La repisa de abajo.
        caja(v, x - 0.22f, x - 0.01f, y0 - 0.06f, y0 - 0.03f, m1 + 0.05f, m0 - 0.05f, lerp(C.ACERO, C.HORMIGON, 0.25f))
    }

    // ---------------------------------------------------------------- El suelo

    /**
     * El suelo de hormigón pulido: más oscuro cerca y más claro lejos (devuelve la claridad de las paredes), con las juntas
     * cortadas que marcan la fuga, las rozaduras, la flecha cian que va a la puerta y la rejilla del desagüe.
     */
    fun DrawScope.suelo(v: VistaLab) {
        val forma = formaDelSuelo()
        caraGrande(v, listOf(V3(XI, 0f, ZF), V3(XD, 0f, ZF), V3(XD, 0f, Z_CERCA), V3(XI, 0f, Z_CERCA)), C.PISO, ARRIBA, a = V3(0.8f, 0f, ZF), b = V3(0.8f, 0f, 2.2f), pasos = 6)
        clipPath(forma) {
            // El brillo del pulido: la pared del fondo reflejada, una franja clara junto a ella.
            val pie = CamLab.p(0f, 0f, ZF).y
            drawRect(Brush.verticalGradient(0f to v.c(V3(0f, 1f, ZF), C.HORMIGON, FRENTE).copy(alpha = 0.45f), 1f to Color.Transparent, startY = pie, endY = pie + 34f), Offset(0f, pie), Size(380f, 34f))
            // Las juntas cortadas en losas de dos metros.
            val junta = lerp(C.PISO, C.SOMBRA, 0.45f)
            for (z in listOf(2.6f, 4.6f, 6.6f)) drawLine(v.c(V3(0f, 0f, z), junta, ARRIBA).copy(alpha = 0.6f), CamLab.p(XI, 0f, z), CamLab.p(XD, 0f, z), 0.5f)
            for (x in listOf(-3.0f, -1.0f, 1.0f)) drawLine(v.c(V3(x, 0f, 4f), junta, ARRIBA).copy(alpha = 0.6f), CamLab.p(x, 0f, Z_CERCA), CamLab.p(x, 0f, ZF), 0.5f)
            // Las manchas del pulido y las rozaduras de las ruedas.
            val az = Azar(Semilla.de("laboratorio", "suelo"))
            repeat(22) { val q = V3(az.entre(-2.5f, XD - 0.2f), 0f, az.entre(1.8f, ZF - 0.2f)); val k = CamLab.k(q)
                mancha(CamLab.p(q), az.entre(0.2f, 0.6f) * k, az.entre(0.03f, 0.08f) * k, if (az.real() > 0.5f) lerp(C.PISO, Color.White, 0.3f) else lerp(C.PISO, C.SOMBRA, 0.4f), 0.12f) }
            repeat(8) { val x = az.entre(0.6f, 2.6f); val z = az.entre(2f, 4f); val l = az.entre(0.4f, 1.2f)
                drawLine(v.c(V3(x, 0f, z), lerp(C.PISO, C.NEGRO, 0.4f), ARRIBA).copy(alpha = 0.18f), CamLab.p(x, 0f, z), CamLab.p(x + l * 0.3f, 0f, z + l), 0.5f) }
            // La flecha cian pintada, gastada, que lleva a la puerta.
            val fl = listOf(V3(0.85f, 0.001f, 4.6f), V3(0.6f, 0.001f, 5.6f), V3(0.35f, 0.001f, 4.6f), V3(0.5f, 0.001f, 4.6f), V3(0.52f, 0.001f, 3.7f), V3(0.68f, 0.001f, 3.7f), V3(0.7f, 0.001f, 4.6f))
            drawPath(CamLab.forma(fl), v.c(V3(0.6f, 0f, 4.5f), lerp(C.CIAN, C.PISO, 0.3f), ARRIBA).copy(alpha = 0.75f))
            // La rejilla del desagüe, junto a la campana.
            val r = listOf(V3(2.15f, 0.001f, 3.35f), V3(2.55f, 0.001f, 3.35f), V3(2.55f, 0.001f, 3.6f), V3(2.15f, 0.001f, 3.6f))
            drawPath(CamLab.forma(r), v.c(V3(2.35f, 0f, 3.5f), lerp(C.ACERO, C.NEGRO, 0.3f), ARRIBA))
            for (i in 1..6) { val x = 2.15f + 0.4f * i / 7f; drawLine(v.c(V3(x, 0f, 3.5f), C.NEGRO, ARRIBA), CamLab.p(x, 0f, 3.38f), CamLab.p(x, 0f, 3.57f), 0.6f) }
            mancha(CamLab.p(2.35f, 0f, 3.5f), 16f, 3f, lerp(C.PISO, C.SOMBRA, 0.6f), 0.18f)
        }
    }

    // ---------------------------------------------------------------- Lo que se quita el día que es el objeto

    /**
     * Si la sala pinta hoy su [pieza] (la bata de la percha, los guantes, la taza…): no la pinta el día que es el objeto
     * y el contrato de objetos ya la dibuja él mismo ([ObjetosLaboratorio.DIBUJADOS]), para que no salga dos veces.
     */
    fun laSalaPinta(m: Momento, pieza: String): Boolean = !(m.ficha.objeto == pieza && pieza in ObjetosLaboratorio.DIBUJADOS)
}
