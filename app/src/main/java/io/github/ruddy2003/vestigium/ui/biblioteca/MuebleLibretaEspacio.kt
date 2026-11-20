package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/*
 * La libreta de momentos de Espacio (lo usa `EstanteEspacio`, en `BibliotecaFuturo.kt`): la carpeta de anillas de la mesa
 * del comedor del eje, donde no hay peso y todo va sujeto. Este archivo tiene la luz, las medidas, la pared, la mesa y la goma;
 * la carpeta está en `MuebleLibretaEspacioCarpeta.kt`, las piezas de alrededor en `MuebleLibretaEspacioPiezas.kt` y el índice
 * (los separadores de cada día y la hoja de cada mes) en `MuebleLibretaEspacioIndice.kt`. La paleta y la manera de alumbrar
 * son las del estante (`PaletaEstanteEspacio`, `AireEstanteEspacio`); la caída de la luz, un mapa (`MuebleEspacioLuz.kt`).
 */

/**
 * La luz de la mesa del comedor. De día, la de las luminarias del techo, fuera del cuadro arriba a la izquierda. En oscuro (el
 * turno de descanso), la lámpara de pinza sujeta al pasamanos arriba a la derecha: un halo junto a ella y un charco cálido en la
 * mesa, sobre la carpeta, adonde mira; lo demás se pierde hacia el piloto azul de guardia, arriba a la izquierda, que da el
 * contrapunto frío.
 */
internal class LuzLibretaEspacio(private val w: Float, private val h: Float, private val u: Float, private val a: AireEstanteEspacio) {
    val lampara = Offset(w * 0.855f, h * 0.27f)
    val charco = Offset(w * 0.47f, h * 0.66f)
    val guardia = Offset(w * 0.035f, h * 0.11f)
    private val rx = 235f * u
    private val ry = 118f * u

    /** Con [plano], todo lleva la luz entera: la caída la pone después, encima, el mapa de luz ([colorEscena]). */
    var plano = false

    fun deLampara(p: Offset): Float {
        val halo = (1f - (p - lampara).getDistance() / (150f * u)).coerceIn(0f, 1f).pow(1.6f)
        val dx = (p.x - charco.x) / rx; val dy = (p.y - charco.y) / ry
        val pozo = (1f - sqrt(dx * dx + dy * dy)).coerceIn(0f, 1f).pow(1.1f)
        return maxOf(halo, pozo)
    }

    fun deGuardia(p: Offset): Float = (1f - (p - guardia).getDistance() / (150f * u)).coerceIn(0f, 1f).pow(1.5f)

    fun real(p: Offset): LuzEstanteEspacio = if (!a.lumbre) LuzEstanteEspacio(1f, 0f) else LuzEstanteEspacio(deLampara(p), 0.6f * deGuardia(p))
    fun en(p: Offset): LuzEstanteEspacio = if (plano && a.lumbre) LuzEstanteEspacio(1f, 0f) else real(p)

    /** Hacia dónde cae la sombra de algo en [p], de [largo]: de día, abajo a la derecha; de noche, lejos de la lámpara. */
    fun caida(p: Offset, largo: Float): Offset {
        if (!a.lumbre) return Offset(largo, largo * 0.8f)
        val v = p - lampara
        val d = v.getDistance().coerceAtLeast(1f)
        return Offset(v.x / d * largo * 1.4f, maxOf(0.3f, v.y / d) * largo)
    }

    val modo: Int get() = if (a.lumbre) 1 else 0

    /**
     * El mapa de luz en la fracción ([fx], [fy]) del lienzo: de día, crema arriba a la izquierda y la sombra violeta abajo a la
     * derecha; en oscuro, el hondo de la noche, más tapado cuanto menos luz llega (la cuenta de [AireEstanteEspacio.luz]).
     */
    fun colorEscena(fx: Float, fy: Float): Color {
        if (!a.lumbre) {
            val t = fx * 0.6f + fy * 0.4f
            return if (t < 0.45f) LUZ_MUEBLE.copy(alpha = 0.22f * (1f - t / 0.45f)) else SOMBRA_MUEBLE.copy(alpha = 0.16f * ((t - 0.45f) / 0.55f))
        }
        val l = real(Offset(fx * w, fy * h))
        val m = (0.9f - 0.84f * l.k.pow(0.75f) - 0.28f * l.f).coerceIn(0.07f, 0.9f)
        return a.hondo.copy(alpha = ((m - 0.06f) / 0.94f).coerceIn(0f, 1f))
    }
}

/**
 * Dónde va cada cosa, en píxeles: la pared hasta [mesa] (el borde de atrás de la mesa), la mesa hasta [canto]; el pasamanos a
 * [pasa]. La carpeta va tumbada en la mesa y un poco girada ([giro]), vista desde arriba y de frente: [ancho] a lo largo de las
 * anillas, [hondo] hacia atrás y [grueso] de alto (más con más hojas); [p] lleva un punto de la carpeta (x a lo ancho desde el
 * lomo, y hacia delante desde el canto de atrás, z hacia arriba desde la mesa) a la pantalla.
 */
internal class GeoLibretaEspacio(w: Float, h: Float, u: Float, hojas: Int) {
    val mesa = h * 0.36f
    val canto = h * 0.875f
    val pasa = h * 0.305f
    val ancho = minOf(w * 0.6f, 232f * u)
    val hondo = 106f * u
    val grueso = (7f + 1.5f * sqrt(hojas.coerceAtLeast(0).toFloat())).coerceAtMost(22f) * u
    /** El lomo (la franja de las anillas) y la vista: cuánto se acorta lo hondo y lo alto. */
    val lomo = 15f * u
    private val giro = Math.toRadians(-5.0)
    val cs = cos(giro).toFloat()
    val sn = sin(giro).toFloat()
    val f = 0.8f
    val cz = 0.95f
    val origen = Offset(w * 0.065f, h * 0.53f)

    fun p(x: Float, y: Float, z: Float) = Offset(origen.x + cs * x - sn * y, origen.y + f * (sn * x + cs * y) - z * cz)

    /** La matriz del plano de la tapa a la altura [z], girada [fi] radianes sobre las anillas (la tapa que se abre). */
    fun plano(z: Float, fi: Float = 0f): Matrix {
        val cf = cos(fi); val sf = sin(fi)
        return Matrix(floatArrayOf(cs * cf, f * sn * cf - cz * sf, 0f, 0f, -sn, f * cs, 0f, 0f, 0f, 0f, 1f, 0f, origen.x, origen.y - z * cz, 0f, 1f))
    }

    /** La matriz de la cara de delante: x a lo ancho, y hacia abajo desde el canto de arriba (0) hasta la mesa ([grueso]). */
    fun frente(): Matrix =
        Matrix(floatArrayOf(cs, f * sn, 0f, 0f, 0f, cz, 0f, 0f, 0f, 0f, 1f, 0f, origen.x - sn * hondo, origen.y + f * cs * hondo - grueso * cz, 0f, 1f))

    /** A qué fondo (y) de la carpeta cruza la goma, y su altura en pantalla sobre la mesa. */
    val yGoma = hondo * 0.74f
    val goma: Float get() = p(0f, yGoma, 0f).y
}

internal object MuebleLibretaEspacio {
    private val P = PaletaEstanteEspacio

    val ALTO: Dp = 266.dp

    /** La tela de la pared del comedor (nomex blanco roto) y la mesa de aluminio anodizado gris pizarra. */
    private val NOMEX = Color(0xFFE4E0D6)
    private val MESA = Color(0xFF555C6E)

    /** Los trazos que se rehacen en cada dibujo. */
    private val trazo = Path()
    /** El mapa de luz, pintado una vez por tamaño y por modo. */
    private val mapa = MapaLuzEspacio(56, 36)

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f * u || h < 4f * u) return
        val a = AireEstanteEspacio(oscuro, false)
        val g = GeoLibretaEspacio(w, h, u, e.hojas)
        val luz = LuzLibretaEspacio(w, h, u, a)
        val ab = e.abierto.coerceIn(0f, 1f)
        val escena = mapa.de(claveMapaEspacio(w, h, luz.modo)) { fx, fy -> luz.colorEscena(fx, fy) }
        // Todo con la luz entera; encima, el mapa de luz que lo apaga con la distancia a la lámpara.
        luz.plano = true
        pared(g, a, luz)
        with(PiezasLibretaEspacio) {
            iman(Rect(w * 0.36f, g.mesa * 0.17f, w * 0.68f, g.mesa * 0.17f + 7f * u), a, luz)
            placa(Rect(w * 0.76f, g.mesa * 0.1f, w * 0.76f + 46f * u, g.mesa * 0.1f + 26f * u), a, luz)
        }
        pasamanos(g, a, luz)
        with(PiezasLibretaEspacio) {
            bebida(Offset(w * 0.2f, g.pasa), a, luz)
            lampara(luz.lampara, g.pasa, a)
        }
        mesa(g, a, luz)
        with(PiezasLibretaEspacio) { tortillas(Offset(w * 0.845f, g.goma - 6f * u), (w / (411f * u)).coerceIn(0.72f, 1f), a, luz) }
        val giro = (Math.PI.toFloat() * 0.92f * ((ab - 0.08f) / 0.92f).coerceIn(0f, 1f).pow(0.75f))
        with(CarpetaLibretaEspacio) { carpeta(g, e, medidor, a, luz, giro) }
        cordon(g, a, luz, ab)
        ponerMapaEspacio(escena, Rect(0f, 0f, w, h))
        luz.plano = false
        with(CarpetaLibretaEspacio) { rotulos(g, e, medidor, a, luz, giro) }
        with(PiezasLibretaEspacio) {
            gota(Offset(w * 0.27f, g.mesa * 0.12f + ab * 3f * u), 3.2f * u, a, luz)
            piloto(luz.guardia, a)
        }
        brillos(g, a, luz)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- la pared

    /**
     * La pared del comedor: paños de nomex blanco roto cosidos a lo alto (la costura doble y la tela que cede junto a ella), con
     * sus broches de presión a lo largo de las costuras, un parche de velcro vacío con pelusa y el roce de las manos sobre el
     * pasamanos.
     */
    private fun DrawScope.pared(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density; val w = size.width
        val tela = a.luz(NOMEX, luz.en(Offset(w * 0.5f, g.mesa * 0.5f)))
        drawRect(tela, Offset.Zero, Size(w, g.mesa + 2f * u))
        with(TelasEspacio) { trama(Rect(0f, 0f, w, g.mesa), if (a.oscuro) 0.22f else 0.32f) }
        // Las costuras: la tela que cede a un lado, la costura doble, y los broches de presión cada tanto.
        listOf(0.29f, 0.71f).forEachIndexed { i, fx ->
            val x = w * fx
            drawRect(a.sombra(0.1f), Offset(x + 1f * u, 0f), Size(10f * u, g.mesa))
            drawRect(a.claro(0.22f), Offset(x - 6f * u, 0f), Size(5f * u, g.mesa))
            drawLine(a.sombra(0.32f), Offset(x, 0f), Offset(x, g.mesa), 1.2f * u)
            trazo.reset()
            var y = 1f * u; var k = 0
            while (y < g.mesa) {
                val l = (2f + 0.8f * azar(1401 + i, k)) * u
                trazo.moveTo(x - 2.4f * u, y); trazo.lineTo(x - 2.4f * u, y + l)
                trazo.moveTo(x + 2.4f * u, y + 0.8f * u); trazo.lineTo(x + 2.4f * u, y + l + 0.8f * u)
                y += l + maxOf(0.5f, 1.5f * u); k++
            }
            drawPath(trazo, mezcla(tela, P.TINTA, 0.35f).copy(alpha = 0.5f), style = Stroke(0.6f * u))
            listOf(0.14f, 0.5f, 0.86f).forEach { fy -> broche(Offset(x + 7f * u, g.mesa * fy), a, luz) }
        }
        // Un parche de velcro vacío (aquí se pegaba algo) con su pelusa.
        val v = Rect(w * 0.57f, g.mesa * 0.5f, w * 0.57f + 20f * u, g.mesa * 0.5f + 12f * u)
        with(PiezasEstanteEspacio) { velcro(v, a, luz.en(v.center), 1403) }
        // El roce de las manos y la mugre sobre el pasamanos: unos trazos largos y tenues, no manchas redondas.
        trazo.reset()
        for (k in 0 until 5) {
            val x0 = w * (0.08f + 0.17f * k + 0.05f * azar(1404, k)); val y0 = g.pasa - (8f + 10f * azar(1405, k)) * u
            trazo.moveTo(x0, y0); trazo.lineTo(x0 + (18f + 22f * azar(1406, k)) * u, y0 + (azar(1407, k) - 0.5f) * 4f * u)
        }
        drawPath(trazo, a.sombra(0.07f), style = Stroke(5f * u, cap = StrokeCap.Round))
        drawRect(a.sombra(0.12f), Offset(0f, g.mesa - 8f * u), Size(w, 8f * u))
    }

    /** Un broche de presión: el anillo de metal con su luz y su sombra. */
    private fun DrawScope.broche(c: Offset, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        drawCircle(a.sombra(0.3f), 2.8f * u, c + Offset(0.6f * u, 0.8f * u))
        drawCircle(a.luz(P.ACERO, luz.en(c)), 2.6f * u, c)
        drawCircle(a.sombra(0.35f), 1.1f * u, c)
        drawCircle(a.claro(0.6f), 0.8f * u, c - Offset(1f * u, 1f * u))
    }

    /** El pasamanos que cruza la pared de lado a lado, de aluminio anodizado azul, con sus soportes atornillados y su sombra. */
    private fun DrawScope.pasamanos(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density; val w = size.width
        val y = g.pasa
        val gr = 6.5f * u
        drawRect(a.sombra(if (a.lumbre) 0.42f else 0.24f), Offset(0f, y + gr / 2 + 1f * u), Size(w, 3.5f * u))
        drawRect(a.sombra(0.12f), Offset(0f, y + gr / 2), Size(w, 9f * u))
        drawRect(a.luz(mezcla(P.TELA_AZUL, P.ACERO, 0.35f), luz.en(Offset(w * 0.5f, y))), Offset(0f, y - gr / 2), Size(w, gr))
        drawRect(a.claro(0.45f), Offset(0f, y - gr / 2 + 1f * u), Size(w, 1.2f * u))
        drawRect(a.sombra(0.3f), Offset(0f, y + gr / 2 - 1.4f * u), Size(w, 1.4f * u))
        // El anodizado gastado donde se agarra: el aluminio claro asoma a trechos.
        listOf(0.33f, 0.58f).forEach { fx -> drawRect(a.luz(mezcla(P.ACERO, P.CASCO, 0.4f), luz.en(Offset(w * fx, y))), Offset(w * fx, y - gr / 2 + 1f * u), Size(14f * u, gr - 2f * u)) }
        listOf(w * 0.12f, w * 0.5f, w * 0.95f).forEach { x ->
            val l = luz.en(Offset(x, y))
            drawRoundRect(a.sombra(0.3f), Offset(x - 4f * u, y - 8f * u), Size(10f * u, 18f * u), CornerRadius(2f * u))
            drawRoundRect(a.luz(P.ACERO, l), Offset(x - 5f * u, y - 9f * u), Size(10f * u, 18f * u), CornerRadius(2f * u))
            drawRect(a.claro(0.45f), Offset(x - 4.4f * u, y - 8.4f * u), Size(1f * u, 16.8f * u))
            remacheEspacio(Offset(x, y - 6f * u), 1.1f * u, a.luz(P.CASCO, l), a)
            remacheEspacio(Offset(x, y + 6f * u), 1.1f * u, a.luz(P.CASCO, l), a)
        }
    }

    // ---------------------------------------------------------------- la mesa

    /**
     * La mesa del comedor: la cara de arriba de aluminio anodizado gris pizarra, en dos planchas con su junta y sus tornillos,
     * rayada, con sus cuadros de velcro; la sombra que la pared le deja atrás, el canto de delante con su riel de anclajes y una
     * etiqueta con signos, y debajo la penumbra con el cable que corre bajo la mesa.
     */
    private fun DrawScope.mesa(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density; val w = size.width; val h = size.height
        val l = luz.en(Offset(w * 0.5f, (g.mesa + g.canto) / 2))
        drawRect(a.luz(MESA, l), Offset(0f, g.mesa), Size(w, g.canto - g.mesa))
        drawRect(a.sombra(0.4f), Offset(0f, g.mesa), Size(w, 3f * u))
        drawRect(a.sombra(0.15f), Offset(0f, g.mesa), Size(w, 10f * u))
        // La junta entre las dos planchas, que se abre hacia delante, con sus tornillos.
        val xj = w * 0.735f
        drawLine(a.sombra(0.45f), Offset(xj, g.mesa), Offset(xj + 6f * u, g.canto), 1.4f * u)
        drawLine(a.claro(0.18f), Offset(xj + 1.4f * u, g.mesa), Offset(xj + 7.4f * u, g.canto), 0.8f * u)
        listOf(0.12f, 0.88f).forEach { t ->
            val c = Offset(xj + 6f * u * t - 6f * u, g.mesa + (g.canto - g.mesa) * t)
            remacheEspacio(c, 1.4f * u, a.luz(P.ACERO, luz.en(c)), a)
            drawLine(a.sombra(0.5f), c - Offset(0.9f * u, 0f), c + Offset(0.9f * u, 0f), 0.5f * u)
        }
        // Las rayas largas del uso y el roce de las bandejas.
        trazo.reset()
        for (k in 0 until 6) {
            val x0 = w * azar(1412, k); val y0 = g.mesa + (g.canto - g.mesa) * (0.25f + 0.7f * azar(1413, k))
            trazo.moveTo(x0, y0); trazo.lineTo(x0 + w * (0.05f + 0.07f * azar(1414, k)), y0 + 2f * u * (azar(1415, k) - 0.5f))
        }
        drawPath(trazo, a.claro(0.14f), style = Stroke(0.6f * u))
        // El anodizado gastado a lo largo del canto, donde se apoyan los brazos.
        drawRect(a.claro(0.07f), Offset(0f, g.canto - 14f * u), Size(w, 12f * u))
        // Los cuadros de velcro donde se pegan las bandejas (vistos desde arriba, aplanados).
        listOf(Offset(0.035f, 0.47f), Offset(0.7f, 0.43f), Offset(0.96f, 0.8f)).forEachIndexed { i, fr ->
            val c = Offset(w * fr.x, h * fr.y)
            val r = Rect(c.x - 11f * u, c.y - 5f * u, c.x + 11f * u, c.y + 5f * u)
            with(PiezasEstanteEspacio) { velcro(r, a, luz.en(c), 1420 + i) }
        }
        // El canto de delante: el perfil de aluminio con su riel de anclajes y una etiqueta.
        val c0 = g.canto; val c1 = minOf(h, g.canto + 10f * u)
        drawRect(a.luz(P.ACERO, luz.en(Offset(w * 0.5f, c0))), Offset(0f, c0), Size(w, c1 - c0))
        drawRect(a.claro(0.55f), Offset(0f, c0), Size(w, 1f * u))
        val ry = c0 + (c1 - c0) * 0.55f
        drawRect(a.sombra(0.5f), Offset(0f, ry - 0.8f * u), Size(w, 1.6f * u))
        trazo.reset()
        var x = 5f * u
        while (x < w) { trazo.addOval(Rect(Offset(x, ry), 1.7f * u)); x += maxOf(1f, 12f * u) }
        drawPath(trazo, a.sombra(0.6f))
        with(PiezasLibretaEspacio) { etiqueta(Rect(w * 0.05f, c0 + 1.5f * u, w * 0.05f + 30f * u, c1 - 1.5f * u), a, luz) }
        // Debajo de la mesa: la penumbra y el cable que corre por debajo, con una brida.
        if (c1 < h) {
            drawRect(a.luz(mezcla(MESA, P.GOMA, 0.65f), LuzEstanteEspacio(0.12f, 0.1f)), Offset(0f, c1), Size(w, h - c1))
            drawRect(a.sombra(0.5f), Offset(0f, c1), Size(w, 3f * u))
            val yc = c1 + (h - c1) * 0.55f
            drawLine(a.luz(P.GOMA, LuzEstanteEspacio(0.3f, 0.1f)), Offset(0f, yc), Offset(w, yc + 1f * u), 2.4f * u)
            drawLine(a.claro(0.2f), Offset(0f, yc - 0.8f * u), Offset(w, yc + 0.2f * u), 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- la goma

    /**
     * La goma elástica negra que cruza la mesa de lado a lado: sobre la mesa va a su altura, sobre la carpeta sube lo que mide
     * su grueso y la aprieta (con su sombra en la tapa y la tapa hundida a los lados), y sujeta también la bolsa de las
     * tortillas. Al abrir, se suelta: cae delante de la carpeta.
     */
    private fun DrawScope.cordon(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio, ab: Float) {
        val u = density; val w = size.width
        val y = g.goma
        val sube = g.grueso * g.cz
        val iz = g.p(0f, g.yGoma, 0f)
        // Dónde sale por la derecha: por el canto derecho o, si la goma corta antes, por el de delante.
        val yr = ((y - g.origen.y - g.f * g.sn * g.ancho) / (g.f * g.cs)).coerceIn(0f, g.hondo)
        val de = g.p(g.ancho, yr, 0f)
        trazo.reset()
        trazo.moveTo(-2f * u, y)
        if (ab < 0.05f) {
            trazo.lineTo(iz.x - 5f * u, y)
            trazo.lineTo(iz.x + 2f * u, y - sube)
            trazo.lineTo(de.x - 2f * u, y - sube)
            trazo.lineTo(de.x + 5f * u, y)
        } else {
            val yd = g.p(0f, g.hondo, 0f).y + 6f * u
            trazo.lineTo(iz.x - 8f * u, y); trazo.lineTo(iz.x, yd); trazo.lineTo(de.x, yd); trazo.lineTo(de.x + 8f * u, y)
        }
        trazo.lineTo(w + 2f * u, y)
        val d = luz.caida(Offset(w * 0.4f, y), 2.5f * u)
        translate(d.x * 0.5f, d.y + 1.4f * u) { drawPath(trazo, a.sombra(0.45f), style = Stroke(3.6f * u)) }
        drawPath(trazo, a.luz(P.GOMA, luz.en(Offset(w * 0.5f, y))), style = Stroke(3.4f * u))
        drawPath(trazo, a.luz(mezcla(P.GOMA, P.ACERO, 0.3f), luz.en(Offset(w * 0.5f, y))).copy(alpha = 0.6f),
            style = Stroke(3.4f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.6f * u, 1.4f * u))))
        translate(0f, -1.1f * u) { drawPath(trazo, a.claro(0.3f), style = Stroke(0.7f * u)) }
        // Donde aprieta la tapa, la lona se hunde a los dos lados de la goma.
        if (ab < 0.05f) {
            drawLine(a.sombra(0.25f), Offset(iz.x + 4f * u, y - sube - 3f * u), Offset(de.x - 4f * u, y - sube - 3f * u), 1.4f * u)
            drawLine(a.claro(0.2f), Offset(iz.x + 4f * u, y - sube - 5f * u), Offset(de.x - 4f * u, y - sube - 5f * u), 1f * u)
        }
    }

    // ---------------------------------------------------------------- las luces

    /**
     * Lo que alumbra por sí mismo y los filos, encima de todo: en oscuro, la boca de la lámpara, el filo cálido en el canto de
     * la tapa que la mira y el halo del piloto de guardia.
     */
    private fun DrawScope.brillos(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        if (!a.lumbre) return
        // El charco cálido de la lámpara sobre la mesa y la carpeta.
        mancha(luz.charco, size.width * 0.3f, P.AMBAR.copy(alpha = 0.16f))
        val t0 = g.p(g.ancho * 0.25f, 0f, g.grueso); val t1 = g.p(g.ancho, 0f, g.grueso)
        drawLine(a.filo(0.55f * luz.real(t1).k), t0, t1, 1.2f * u)
        val r0 = g.p(g.ancho, 0f, g.grueso); val r1 = g.p(g.ancho, g.hondo, g.grueso)
        drawLine(a.filo(0.6f * luz.real(r1).k), r0, r1, 1.2f * u)
        drawLine(a.filo(0.5f), Offset(size.width * 0.55f, g.pasa - 3f * u), Offset(size.width, g.pasa - 3f * u), 1f * u)
        mancha(luz.guardia, 30f * u, P.GUARDIA.copy(alpha = 0.3f))
        drawCircle(P.AMBAR.copy(alpha = 0.32f), 7f * u, luz.lampara)
        drawCircle(Color(0xFFFFF0D6), 2.6f * u, luz.lampara)
    }
}
