package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Camino
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Refugio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.lucecita
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.runa
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIEDRAS
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIEDRA_PARTIDA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIE_CARA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIE_DERECHA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.REFUGIO_ARN
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.TORRENTE
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.ZETAS
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.alturaEn
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.sin

/**
 * La ladera del paso, debajo del collado, lo que se ve «desde la puerta de la posada»: abajo, la ladera de los alerces;
 * en medio, la pedrera, «donde el camino se borra entre las piedras sueltas y solo las runas dicen por dónde ir»; arriba,
 * la cornisa, una banda de roca con su repisa, «donde el camino tiene el ancho de una mula y media»; y el collado. El
 * camino sube en zetas, con una piedra marcada en cada vuelta (los hitos claros que el ojo sigue); a media subida, el
 * refugio de la Piedra Partida; en el collado, el refugio de Arn según el arco. Con compañía, una fila de mulas pequeñas.
 */
internal object PasoAntiguas {
    private val C = ColorAntiguas
    private const val LEJOS = 0.22f

    /** La forma de la ladera: del pie de la cara norte al collado, del collado a la esquina de la posada y, abajo, la orilla de allá del torrente. */
    val FORMA: Path by lazy {
        val orilla = TORRENTE.filter { it.first.x in 160f..345f }.map { (q, w) -> q + Offset(0f, -w - 0.6f) }
        poligono(PIE_CARA + PIE_DERECHA.drop(1) + listOf(Offset(342f, 192f)) + orilla)
    }

    /** La cornisa: la banda de roca de arriba (su canto de arriba y el de abajo). */
    private val CORNISA_ARRIBA = pts(256f, 129.6f, 266f, 125.4f, 276f, 121.4f, 286f, 117.4f, 296f, 113.6f, 306f, 112f, 316f, 114.6f, 328f, 119f, 342f, 125f)
    private val CORNISA_ABAJO = pts(259f, 137.6f, 270f, 134.2f, 282f, 130.4f, 292f, 128f, 304f, 127.6f, 316f, 129.4f, 328f, 132.4f, 342f, 136f)

    /** La pedrera: el abanico de piedra suelta que baja de la cornisa y del pie de la cara norte. */
    private val PEDRERA = pts(236f, 140f, 252f, 136f, 268f, 136.6f, 290f, 131f, 312f, 131.6f, 334f, 136f, 342f, 140f, 342f, 156f, 320f, 162f, 296f, 166.4f, 272f, 164f, 248f, 166f, 224f, 163f, 210f, 159f)

    fun DrawScope.paso(v: VistaAntiguas) {
        val az = fijo("paso")
        clipPath(FORMA) {
            suelo(v, az)
            cornisa(v, az)
            val zanja = v.camino == EscenaAntiguas.Camino.ZANJA
            if (!zanja) camino(v)
            alerces(v, az)
            if (!zanja) piedras(v)
            refugioPiedraPartida(v)
            nieve(v, az)
            // Recién abierto, la zanja y sus piedras se abren en la nieve: van encima.
            if (zanja) { camino(v); piedras(v) }
            filaDeMulas(v)
        }
        refugioDeArn(v)
        cascada(v, az)
    }

    // ---------------------------------------------------------------- El suelo

    /** La hierba de abajo, la pedrera clara de en medio y la roca de arriba, con sus caras de luz y sombra. */
    private fun DrawScope.suelo(v: VistaAntiguas, az: Azar) {
        val p = v.p; val hierba = EscenaAntiguas.hierba(v.m)
        val roca = lerp(C.ROCA, C.PIEDRA, 0.55f)
        drawRect(Brush.verticalGradient(0f to p.medio(roca, LEJOS), 0.32f to p.medio(lerp(roca, hierba, 0.4f), LEJOS), 0.62f to p.medio(hierba, LEJOS), 1f to p.medio(lerp(hierba, C.PINO, 0.3f), LEJOS * 0.8f), startY = 108f, endY = 205f), Offset(160f, 104f), Size(190f, 110f))
        // Tres lomos anchos: el de la izquierda mira a la derecha, el del medio de frente y el de la derecha a la izquierda.
        val lomos = listOf(pts(214f, 150f, 236f, 158f, 250f, 175f, 258f, 200f) to V3(0.5f, 0.45f, -0.75f), pts(276f, 140f, 290f, 160f, 296f, 182f, 300f, 200f) to V3(-0.45f, 0.45f, -0.77f))
        for ((eje, n) in lomos) {
            val t = ((v.sol.t(n) - 0.3f) * 1.4f).coerceIn(-1f, 1f)
            val forma = poligono(eje + eje.reversed().map { it + Offset(22f, 6f) })
            drawPath(forma, (if (t > 0f) p.luz(hierba, LEJOS) else p.sombra(hierba, LEJOS)).copy(alpha = 0.32f * kotlin.math.abs(t)))
        }
        // La pedrera: piedra suelta, clara, con sus bloques y sus regueros.
        val pedrera = TrazosValle.cerrada(PEDRERA)
        drawPath(pedrera, Brush.verticalGradient(listOf(p.por(lerp(roca, C.NIEVE, 0.18f), 0.2f, LEJOS), p.por(roca, 0f, LEJOS).copy(alpha = 0.7f)), 130f, 168f))
        clipPath(pedrera) {
            val piedras = (0 until 260).map { Offset(az.entre(206f, 344f), az.entre(130f, 168f)) }
            drawPoints(piedras, PointMode.Points, p.sombra(lerp(roca, C.ESCORIA, 0.3f), LEJOS).copy(alpha = 0.55f), 1.1f, StrokeCap.Round)
            drawPoints(piedras.map { it + Offset(if (p.solALaDerecha) 0.4f else -0.4f, -0.4f) }, PointMode.Points, p.luz(lerp(roca, C.NIEVE, 0.3f), LEJOS).copy(alpha = 0.5f), 0.8f, StrokeCap.Round)
            for (x in listOf(232f, 258f, 284f, 318f)) drawPath(curva(rota(pts(x, 134f, x - 4f, 148f, x - 9f, 166f), az, 1.2f, 2)), p.sombra(roca, LEJOS).copy(alpha = 0.3f), style = Stroke(1.2f))
        }
        // La hierba: pinceladas cortas con su luz, más grandes abajo; matas de rododendro y de enebro en manchas.
        val claros = ArrayList<Offset>(); val oscuros = ArrayList<Offset>()
        repeat(420) { val q = Offset(az.entre(168f, 344f), az.entre(150f, 208f)); if (az.real() < 0.5f) claros += q else oscuros += q }
        drawPoints(claros, PointMode.Points, p.luz(lerp(hierba, C.ORO, 0.2f), LEJOS).copy(alpha = 0.45f), 0.9f, StrokeCap.Round)
        drawPoints(oscuros, PointMode.Points, p.sombra(lerp(hierba, C.PINO, 0.4f), LEJOS).copy(alpha = 0.45f), 0.9f, StrokeCap.Round)
        repeat(18) {
            val q = Offset(az.entre(176f, 340f), az.entre(140f, 200f)); val w = az.entre(4f, 10f)
            drawPath(borron(q, w, w * 0.32f, az, 9, 0.35f), p.sombra(lerp(hierba, C.PINO, 0.55f), LEJOS).copy(alpha = 0.5f))
        }
    }

    /** La cornisa: la banda de roca con su escarpe en sombra y la repisa del camino encima. */
    private fun DrawScope.cornisa(v: VistaAntiguas, az: Azar) {
        val p = v.p; val roca = lerp(C.ROCA, C.PIEDRA_OSC, 0.3f)
        val banda = poligono(rota(CORNISA_ARRIBA, az, 0.7f, 2) + rota(CORNISA_ABAJO, az, 0.9f, 2).reversed())
        drawPath(banda, p.por(roca, v.sol.t(V3(-0.1f, 0.15f, -1f)) - 0.15f, LEJOS))
        clipPath(banda) {
            // Las grietas verticales del escarpe y su musgo.
            var x = 258f
            while (x < 342f) { val y0 = alturaEn(CORNISA_ARRIBA, x) + 1f; drawLine(p.sombra(lerp(roca, C.ESCORIA, 0.5f), LEJOS).copy(alpha = 0.6f), Offset(x, y0), Offset(x + az.entre(-1.5f, 1.5f), y0 + az.entre(5f, 10f)), az.entre(0.5f, 1.1f)); x += az.entre(3f, 8f) }
        }
        drawPath(curva(CORNISA_ARRIBA), p.luz(lerp(roca, C.NIEVE, 0.3f), LEJOS).copy(alpha = 0.6f), style = Stroke(1f))
    }

    // ---------------------------------------------------------------- El camino

    /** El ancho del camino a la altura [y]: más ancho cerca, fino arriba. */
    private fun ancho(y: Float): Float = 0.8f + ((y - 108f) / 92f).coerceIn(0f, 1f) * 2.8f

    /**
     * El camino en zetas: una cinta de tierra clara con su borde de abajo en sombra (el talud) y el de arriba con hierba;
     * en la pedrera se pierde. Recién abierto, una zanja azul en la nieve con huellas; el día que se cierra, solo abajo.
     */
    private fun DrawScope.camino(v: VistaAntiguas) {
        val p = v.p; val tierra = lerp(C.TIERRA, C.LANA, 0.42f)
        val hasta = when (v.camino) { Camino.ABIERTO, Camino.ZANJA -> 1f; Camino.TAPANDOSE -> 0.35f; Camino.BORRADO -> return }
        val zanja = v.camino == Camino.ZANJA
        for (i in 0 until ZETAS.size - 1) {
            val a = ZETAS[i]; val b = ZETAS[i + 1]
            val f = i / (ZETAS.size - 1f); if (f > hasta) break
            val enPedrera = (a.y + b.y) / 2f in 128f..166f
            val w = ancho((a.y + b.y) / 2f)
            val col = if (zanja) p.sombra(lerp(C.NIEVE, C.SOMBRA, 0.3f), LEJOS) else p.por(tierra, 0.25f, LEJOS)
            val alfa = (if (enPedrera && !zanja) 0.5f else 0.95f) * (if (v.camino == Camino.TAPANDOSE) 1f - f / hasta * 0.7f else 1f)
            // El talud de abajo, en sombra, y la cinta del camino.
            drawLine(p.sombra(lerp(tierra, C.ESCORIA, 0.3f), LEJOS).copy(alpha = 0.55f * alfa), a + Offset(0f, w * 0.7f), b + Offset(0f, w * 0.7f), w * 0.6f, StrokeCap.Round)
            drawLine(col.copy(alpha = alfa), a, b, w, StrokeCap.Round)
            if (!zanja) drawLine(p.luz(lerp(tierra, C.NIEVE, 0.3f), LEJOS).copy(alpha = 0.45f * alfa * p.r.dia), a + Offset(0f, -w * 0.25f), b + Offset(0f, -w * 0.25f), w * 0.3f, StrokeCap.Round)
            // Las huellas de los que subieron: en la zanja, siempre; en la travesía, las de la compañía de verano.
            if (zanja || v.lugar == EscenaAntiguas.Lugar.TRAVESIA || Senal.PEREGRINOS in v.senales) {
                val pasos = ((b - a).getDistance() / (2.2f * w + 0.6f)).toInt().coerceAtLeast(2)
                val col2 = p.sombra(if (zanja) C.NIEVE_SOMBRA else lerp(tierra, C.ESCORIA, 0.4f), LEJOS).copy(alpha = 0.6f)
                for (k in 0 until pasos) { val q = a + (b - a) * ((k + 0.5f) / pasos) + Offset(0f, if (k % 2 == 0) -w * 0.18f else w * 0.18f); drawCircle(col2, w * 0.18f, q) }
            }
        }
    }

    /** Las piedras marcadas: una piedra alzada, clara, con su lado en sombra, su liquen y su runa. Los hitos que el ojo sigue hasta el collado. */
    private fun DrawScope.piedras(v: VistaAntiguas) {
        val n = EscenaAntiguas.piedrasALaVista(v.m); if (n == 0) return
        val az = fijo("piedras")
        val frescas = Senal.RUNAS_FRESCAS in v.senales
        for ((i, pr) in PIEDRAS.take(n).withIndex()) {
            val (pie, runa) = pr
            val k = 0.45f + ((pie.y - 108f) / 88f).coerceIn(0f, 1f) * 1.2f
            piedraMarcada(v.p, pie, k, runa, az, LEJOS, gastada = if (i == PIEDRAS.size - 1) 0.45f else 0f, fresca = if (frescas) 0.4f else 0f, nieve = v.nieveLadera, primera = i == PIEDRAS.size - 1 && Senal.PRIMERA_RUNA in v.senales)
        }
    }

    /** Una piedra marcada en [pie], de tamaño [k] (1 ≈ 6 unidades de alto). [primera]: la de la Primera Runa, repasada y untada de sebo, con su cinta. */
    fun DrawScope.piedraMarcada(p: Pincel, pie: Offset, k: Float, runa: Runa, az: Azar, lejos: Float, gastada: Float = 0f, fresca: Float = 0f, nieve: Float = 0f, primera: Boolean = false) {
        val w = 2.6f * k; val h = 6f * k
        val lado = if (p.solALaDerecha) 1f else -1f
        val piedra = lerp(C.PIEDRA, C.NIEVE, 0.32f)
        drawOval(p.r.color.copy(alpha = 0.4f * p.r.fuerza), pie + Offset(-w * 0.8f - lado * w * 0.8f, -0.5f * k), Size(w * 2.6f, 1.2f * k), blendMode = BlendMode.Multiply)
        val forma = poligono(pie + Offset(-w * 0.55f, 0.4f), pie + Offset(w * 0.6f, 0.4f), pie + Offset(w * 0.5f, -h * 0.82f), pie + Offset(w * 0.1f, -h), pie + Offset(-w * 0.45f, -h * 0.9f))
        drawPath(forma, p.por(piedra, 0.15f, lejos))
        drawPath(poligono(pie + Offset(-lado * w * 0.55f, 0.4f), pie + Offset(-lado * w * 0.05f, 0.4f), pie + Offset(-lado * w * 0.02f, -h * 0.95f), pie + Offset(-lado * w * 0.45f, -h * 0.9f)), p.sombra(piedra, lejos).copy(alpha = 0.8f))
        // El liquen, en manchas ocres.
        repeat(3) { drawCircle(p.medio(C.LIQUEN, lejos).copy(alpha = 0.7f), az.entre(0.25f, 0.5f) * k, pie + Offset(az.entre(-0.4f, 0.4f) * w, -az.entre(0.1f, 0.8f) * h)) }
        if (k > 0.7f) runa(p, runa, pie + Offset(lado * w * 0.15f, -h * 0.55f), h * 0.42f, piedra, lejos, fresca = if (primera) 1f else fresca, gastada = gastada)
        if (primera) { drawLine(p.por(C.RUBI, 0.3f, lejos), pie + Offset(-w * 0.5f, -h * 0.3f), pie + Offset(w * 0.6f, -h * 0.35f), 0.5f * k); drawLine(p.luz(C.ORO, lejos).copy(alpha = 0.5f), pie + Offset(-w * 0.4f, -h), pie + Offset(w * 0.3f, -h * 0.95f), 0.4f * k) }
        if (nieve > 0.25f) drawPath(poligono(pie + Offset(-w * 0.5f, -h * 0.86f), pie + Offset(w * 0.12f, -h * 1.02f), pie + Offset(w * 0.52f, -h * 0.8f), pie + Offset(w * 0.1f, -h * 0.7f)), p.por(C.NIEVE, 0.5f, lejos).copy(alpha = nieve))
    }

    // ---------------------------------------------------------------- Los alerces

    /** Los alerces de la ladera baja: unas filas de árboles cónicos y plumosos, cada uno con su tamaño y su color, con claros donde pasa el camino. */
    private fun DrawScope.alerces(v: VistaAntiguas, az: Azar) {
        val agujas = EscenaAntiguas.agujas(v.m); val amarillo = EscenaAntiguas.amarillo(v.m)
        val arboles = ArrayList<Offset>()
        repeat(150) {
            val x = az.entre(166f, 346f); val y = az.entre(158f, 210f)
            val fondo = alturaEn(listOf(Offset(160f, 200f), Offset(346f, 190f)), x)
            if (y > fondo + 6f) return@repeat
            if (y < 160f + (x - 166f) * 0.04f) return@repeat
            if (cercaDelCamino(Offset(x, y), 3.4f)) return@repeat
            arboles += Offset(x, y)
        }
        for (q in arboles.sortedBy { it.y }) {
            val alto = 4f + (q.y - 156f) * 0.2f + az.entre(-1f, 1.6f)
            val tono = az.real()
            alerce(v, q, alto, agujas, (amarillo * (0.75f + 0.5f * ((200f - q.y) / 44f)) + az.entre(-0.15f, 0.15f)).coerceIn(0f, 1f), tono)
        }
    }

    /** Si [q] está a menos de [margen] unidades del camino (para dejar el claro). */
    private fun cercaDelCamino(q: Offset, margen: Float): Boolean {
        for (i in 0 until ZETAS.size - 1) {
            val a = ZETAS[i]; val b = ZETAS[i + 1]; val ab = b - a; val t = (((q - a).x * ab.x + (q - a).y * ab.y) / (ab.x * ab.x + ab.y * ab.y)).coerceIn(0f, 1f)
            if ((a + ab * t - q).getDistance() < margen + ancho(q.y)) return true
        }
        return false
    }

    /**
     * Un alerce de [alto] unidades: un huso de pisos plumosos (no un triángulo), con su lado de luz y de sombra; con poca
     * aguja, las ramas grises asoman; amarillo «de arriba abajo» en otoño.
     */
    private fun DrawScope.alerce(v: VistaAntiguas, pie: Offset, alto: Float, agujas: Float, amarillo: Float, tono: Float) {
        val p = v.p; val lado = if (p.solALaDerecha) 1f else -1f
        val verde = lerp(lerp(C.ALERCE, C.PINO, 0.3f + 0.2f * tono), C.ORO, amarillo)
        val w = alto * 0.34f
        val tronco = p.medio(lerp(C.MADERA, C.PIEDRA, 0.4f), LEJOS)
        drawLine(tronco, pie, pie + Offset(0f, -alto), 0.45f, StrokeCap.Round)
        drawOval(p.r.color.copy(alpha = 0.35f * p.r.fuerza), pie + Offset(-w - lado * alto * 0.25f, -0.5f), Size(w * 2f + alto * 0.3f, 1.3f), blendMode = BlendMode.Multiply)
        if (agujas < 0.1f) {
            // Pelado: las ramas grises, caídas, en pisos.
            for (j in 1..4) { val y = pie.y - alto * j / 5f; val r = w * (1f - j / 5.5f); drawLine(tronco.copy(alpha = 0.7f), Offset(pie.x, y - 0.4f), Offset(pie.x - r, y + 0.8f), 0.3f); drawLine(tronco.copy(alpha = 0.7f), Offset(pie.x, y - 0.4f), Offset(pie.x + r, y + 0.8f), 0.3f) }
            return
        }
        val forma = Path().apply {
            moveTo(pie + Offset(0f, -alto)); val pisos = 4
            for (j in 1..pisos) { val y = pie.y - alto + alto * j / pisos * 0.88f; val r = w * (0.25f + 0.75f * j / pisos); lineTo(Offset(pie.x + r, y + 0.6f)); lineTo(Offset(pie.x + r * 0.55f, y - 0.4f)) }
            lineTo(pie + Offset(w * 0.3f, -alto * 0.12f)); lineTo(pie + Offset(-w * 0.3f, -alto * 0.12f))
            for (j in pisos downTo 1) { val y = pie.y - alto + alto * j / pisos * 0.88f; val r = w * (0.25f + 0.75f * j / pisos); lineTo(Offset(pie.x - r * 0.55f, y - 0.4f)); lineTo(Offset(pie.x - r, y + 0.6f)) }
            close()
        }
        val a = (0.35f + 0.65f * agujas)
        drawPath(forma, p.medio(verde, LEJOS).copy(alpha = a))
        clipPath(forma) { drawRect(p.luz(lerp(verde, C.LANA, 0.15f), LEJOS).copy(alpha = 0.6f * a), Offset(if (lado > 0f) pie.x else pie.x - w, pie.y - alto), Size(w, alto)); drawRect(p.sombra(verde, LEJOS).copy(alpha = 0.55f * a), Offset(if (lado > 0f) pie.x - w else pie.x, pie.y - alto), Size(w, alto)) }
    }

    // ---------------------------------------------------------------- Los refugios

    /**
     * El refugio de la Piedra Partida, a media subida: un bloque enorme partido en dos, con su raja, y una caseta de piedra
     * con tejado de losa arrimada a una de las mitades. «Dentro de cada uno hay leña y una vela para quien llegue».
     */
    private fun DrawScope.refugioPiedraPartida(v: VistaAntiguas) {
        val p = v.p; val q = PIEDRA_PARTIDA; val roca = lerp(C.PIEDRA, C.ROCA, 0.3f)
        val lado = if (p.solALaDerecha) 1f else -1f
        drawOval(p.r.color.copy(alpha = 0.45f * p.r.fuerza), q + Offset(-12f - lado * 6f, -1f), Size(26f, 3f), blendMode = BlendMode.Multiply)
        val izq = poligono(q + Offset(-11f, 0.5f), q + Offset(-1.2f, 0.5f), q + Offset(-0.6f, -9.6f), q + Offset(-4f, -11.4f), q + Offset(-9.4f, -8.6f))
        val der = poligono(q + Offset(0.8f, 0.5f), q + Offset(8.6f, 0.5f), q + Offset(9.6f, -6f), q + Offset(6f, -10.6f), q + Offset(1.4f, -9.8f))
        drawPath(izq, p.por(roca, v.sol.t(V3(-0.6f, 0.3f, -0.7f)), LEJOS)); drawPath(der, p.por(roca, v.sol.t(V3(0.6f, 0.3f, -0.7f)), LEJOS))
        drawLine(p.medio(C.ESCORIA, LEJOS), q + Offset(0f, 0.4f), q + Offset(0.4f, -10f), 1.1f)
        // La caseta, a la derecha del bloque.
        val frente = poligono(q + Offset(8.6f, 0.6f), q + Offset(15.6f, 0.4f), q + Offset(15.6f, -4.6f), q + Offset(8.6f, -4.4f))
        drawPath(frente, p.por(C.PIEDRA, v.sol.t(V3(0f, 0f, -1f)), LEJOS))
        drawPath(poligono(q + Offset(7.6f, -4.2f), q + Offset(16.6f, -4.6f), q + Offset(15f, -7.2f), q + Offset(9f, -6.8f)), p.por(C.LOSA, v.sol.t(V3(0f, 0.8f, -0.5f)), LEJOS))
        drawRect(p.medio(C.ESCORIA, LEJOS), q + Offset(11f, -3.2f), Size(1.8f, 3.4f))
        if (v.nieveLadera > 0.3f) drawPath(poligono(q + Offset(7.4f, -6.8f), q + Offset(16.4f, -7.4f), q + Offset(15.4f, -8.6f), q + Offset(8.4f, -8f)), p.por(C.NIEVE, 0.5f, LEJOS).copy(alpha = v.nieveLadera))
        // La noche de la tormenta, la vela del nicho encendida (la compañía encerrada dentro).
        if (v.clima == io.github.ruddy2003.vestigium.vidas.Clima.TORMENTA && EscenaAntiguas.hoyEs(v.m, "tormenta") && v.noche > 0.3f) lucecita(q + Offset(12f, -2f), 0.7f, v.noche)
    }

    /**
     * El refugio de Arn, en el collado, según el arco: nada; los muros a medio hacer, «todavía sin tejado, con su andamio de
     * troncos», que suben con la obra; terminado, con el tejado de losa que «brilla» al sol (y a la luna). El día que se
     * empieza, el montón de piedras y los troncos; el día que se termina, una cinta en un palo.
     */
    private fun DrawScope.refugioDeArn(v: VistaAntiguas) {
        val p = v.p; val q = REFUGIO_ARN; val lejos = 0.3f
        val estado = EscenaAntiguas.refugio(v.m)
        if (Senal.CIMIENTOS in v.senales || estado == Refugio.A_MEDIO) for (k in 0..2) drawLine(p.medio(C.MADERA, lejos), q + Offset(-8f + k * 1.2f, 0.4f), q + Offset(-4f + k * 1.6f, -1.6f), 0.6f)
        when (estado) {
            Refugio.NADA -> if (Senal.CIMIENTOS in v.senales) repeat(5) { drawCircle(p.por(C.PIEDRA, 0.2f, lejos), 1f, q + Offset(-2f + it * 1.6f, -0.6f - (it % 2) * 0.8f)) }
            Refugio.A_MEDIO -> {
                val h = 1.2f + 5.4f * (EscenaAntiguas.obra(v.m) / 0.999f)
                drawPath(poligono(q + Offset(-5f, 0.6f), q + Offset(4f, 0.6f), q + Offset(4f, -h), q + Offset(-5f, -h + 0.4f)), p.por(C.PIEDRA, v.sol.t(V3(-0.3f, 0f, -0.95f)), lejos))
                drawPath(poligono(q + Offset(4f, 0.6f), q + Offset(6.6f, -0.4f), q + Offset(6.6f, -h - 0.8f), q + Offset(4f, -h)), p.por(C.PIEDRA, v.sol.t(V3(0.9f, 0f, -0.3f)), lejos))
                for (k in 0..2) drawLine(p.sombra(C.PIEDRA, lejos).copy(alpha = 0.6f), q + Offset(-5f, -h * k / 3f), q + Offset(4f, -h * k / 3f), 0.3f)
                // El andamio de troncos: dos pies, un travesaño y una escalera apoyada.
                val tronco = p.medio(lerp(C.MADERA, C.FRESNO, 0.3f), lejos)
                drawLine(tronco, q + Offset(-6.6f, 0.6f), q + Offset(-6.2f, -h - 4f), 0.6f); drawLine(tronco, q + Offset(7.6f, 0f), q + Offset(7.2f, -h - 4.6f), 0.6f)
                drawLine(tronco, q + Offset(-7f, -h - 2f), q + Offset(8f, -h - 2.6f), 0.5f)
                drawLine(tronco.copy(alpha = 0.8f), q + Offset(-3f, 0.6f), q + Offset(-6f, -h - 1.4f), 0.4f)
            }
            Refugio.TERMINADO -> {
                drawPath(poligono(q + Offset(-5f, 0.6f), q + Offset(4f, 0.6f), q + Offset(4f, -5.4f), q + Offset(-5f, -5f)), p.por(C.PIEDRA, v.sol.t(V3(-0.3f, 0f, -0.95f)), lejos))
                drawPath(poligono(q + Offset(4f, 0.6f), q + Offset(6.6f, -0.4f), q + Offset(6.6f, -6.2f), q + Offset(4f, -5.4f)), p.por(C.PIEDRA, v.sol.t(V3(0.9f, 0f, -0.3f)), lejos))
                val tejado = poligono(q + Offset(-6f, -4.8f), q + Offset(4.6f, -5.6f), q + Offset(7.4f, -6.4f), q + Offset(0.6f, -10.4f), q + Offset(-3.6f, -9.4f))
                drawPath(tejado, p.por(C.LOSA, v.sol.t(V3(0.2f, 0.8f, -0.5f)) + 0.3f, lejos))
                // «Brilla»: el canto de las losas a contraluz o a la luna.
                drawLine(lerp(v.l.solColor, Color.White, 0.5f).copy(alpha = (0.7f * v.p.r.dia + 0.5f * v.luna).coerceIn(0f, 1f)), q + Offset(-5.6f, -5f), q + Offset(0.6f, -10.2f), 0.6f, StrokeCap.Round)
                drawRect(p.medio(C.ESCORIA, lejos), q + Offset(-1.6f, -3f), Size(1.6f, 3.4f))
                if (v.nieveArriba > 0.4f) drawPath(poligono(q + Offset(-5.4f, -5.6f), q + Offset(0.6f, -10.6f), q + Offset(6.8f, -6.8f), q + Offset(0.8f, -9.4f)), p.por(C.NIEVE, 0.5f, lejos).copy(alpha = v.nieveArriba))
                if (Senal.BANDERA_EN_EL_REFUGIO in v.senales) { drawLine(p.medio(C.FRESNO, lejos), q + Offset(6f, -6f), q + Offset(6.4f, -16f), 0.5f); drawPath(poligono(q + Offset(6.4f, -16f), q + Offset(12f, -14.6f), q + Offset(6.3f, -13f)), p.por(C.RUBI, 0.4f, lejos)) }
            }
        }
    }

    // ---------------------------------------------------------------- La compañía

    /**
     * La compañía en el camino, contada por sus mulas: una fila de mulas pequeñas, cargadas y sin nadie **[Diseño propio]**,
     * que sube por las zetas por la mañana y baja por la tarde (`EscenaAntiguas.filaEnElCamino`).
     */
    private fun DrawScope.filaDeMulas(v: VistaAntiguas) {
        val u = EscenaAntiguas.filaEnElCamino(v.m) ?: return
        val n = EscenaAntiguas.mulasEnLaFila(v.m)
        val largo = largoDelCamino()
        for (j in 0 until n) {
            val d = u * largo - j * 5.2f; if (d < 0f) break
            val q = puntoDelCamino(d)
            val k = 0.5f + ((q.y - 108f) / 90f).coerceIn(0f, 1f) * 0.9f
            mulita(v.p, q, k, if (EscenaAntiguas.companiaEnElCamino(v.m) && v.m.hora < 12.5) 1f else -1f, j)
        }
    }

    private fun largoDelCamino(): Float = (0 until ZETAS.size - 1).sumOf { (ZETAS[it + 1] - ZETAS[it]).getDistance().toDouble() }.toFloat()

    private fun puntoDelCamino(d: Float): Offset {
        var resto = d
        for (i in 0 until ZETAS.size - 1) { val a = ZETAS[i]; val b = ZETAS[i + 1]; val l = (b - a).getDistance(); if (resto <= l) return a + (b - a) * (resto / l); resto -= l }
        return ZETAS.last()
    }

    /** Una mula lejana, de lado, con su carga: el lomo con los fardos claros, las patas, la cabeza baja y las orejas. */
    private fun DrawScope.mulita(p: Pincel, pie: Offset, k: Float, va: Float, j: Int) {
        val pelo = if (j % 3 == 1) C.MULA_PARDA else C.MULA
        drawOval(p.r.color.copy(alpha = 0.45f * p.r.fuerza), pie + Offset(-3f * k, -0.4f * k), Size(6f * k, 0.9f * k), blendMode = BlendMode.Multiply)
        for (dx in listOf(-1.6f, -1f, 1f, 1.6f)) drawLine(p.sombra(pelo, LEJOS), pie + Offset(dx * k, -1.6f * k), pie + Offset(dx * k * 1.05f, 0f), 0.35f * k)
        drawOval(p.por(pelo, 0.1f, LEJOS), pie + Offset(-2.3f * k, -3.2f * k), Size(4.6f * k, 1.9f * k))
        val cab = pie + Offset(va * 2.6f * k, -3f * k)
        drawLine(p.por(pelo, 0f, LEJOS), pie + Offset(va * 1.8f * k, -2.6f * k), cab + Offset(va * 0.6f * k, 0.6f * k), 0.9f * k, StrokeCap.Round)
        drawLine(p.sombra(pelo, LEJOS), cab, cab + Offset(-va * 0.2f * k, -1.2f * k), 0.25f * k)
        drawOval(p.por(lerp(C.LANA, C.CUERO, 0.3f + 0.2f * (j % 2)), 0.3f, LEJOS), pie + Offset(-1.7f * k, -4.4f * k), Size(3.2f * k, 1.6f * k))
    }

    // ---------------------------------------------------------------- La nieve de la ladera

    /**
     * La nieve de la ladera ([VistaAntiguas.nieveLadera]): de arriba abajo, con el borde festoneado; con el paso cerrado,
     * toda, «una ladera blanca y lisa que sube hasta el cielo», con los alerces pelados asomando y los lomos marcados por la
     * luz; la ceja de nieve colgada sobre la cornisa.
     */
    private fun DrawScope.nieve(v: VistaAntiguas, az: Azar) {
        val n = v.nieveLadera; if (n <= 0.02f) return
        val p = v.p
        if (n < 0.99f) {
            with(PiezasAntiguas) { manchasDeNieve(p, 160f, 346f, { 100f }, { x -> 112f + 92f * n + 6f * sin(x * 0.09f) + 4f * sin(x * 0.21f) }, az, LEJOS) }
            if (n > 0.55f) ceja(v)
            return
        }
        val manto = poligono(listOf(Offset(160f, 100f), Offset(346f, 100f), Offset(346f, 215f), Offset(160f, 215f)))
        clipPath(manto) {
            drawRect(Brush.verticalGradient(0f to p.por(C.NIEVE, 0.35f, LEJOS), 1f to p.por(C.NIEVE, 0.05f, LEJOS * 0.8f), startY = 108f, endY = 210f), Offset(160f, 100f), Size(190f, 115f))
            // Los lomos y las canales de la ladera bajo la nieve: bajan en abanico desde el collado, cada uno con su lado
            // de sombra azul y su filo de luz; abajo, la nieve se arruga en ventisqueros.
            val lado = if (p.solALaDerecha) -1f else 1f
            for (k in 0 until 7) {
                val pie = Offset(176f + k * 27f + az.entre(-6f, 6f), 206f)
                val arriba = Offset(296f + k * 3f, 116f + kotlin.math.abs(k - 4) * 4f)
                val r = rota(listOf(arriba, arriba + (pie - arriba) * 0.35f + Offset(az.entre(-6f, 6f), 0f), arriba + (pie - arriba) * 0.7f + Offset(az.entre(-6f, 6f), 0f), pie), az, 2.2f, 2)
                drawPath(curva(r.map { it + Offset(lado * 2.2f, 0f) }), p.sombra(C.NIEVE_SOMBRA, LEJOS).copy(alpha = 0.3f), style = Stroke(3.6f + k * 0.3f, cap = StrokeCap.Round))
                drawPath(curva(r), p.luz(C.NIEVE, LEJOS).copy(alpha = 0.45f * p.r.dia + 0.1f), style = Stroke(1f, cap = StrokeCap.Round))
            }
            repeat(9) { val q = Offset(az.entre(176f, 340f), az.entre(176f, 204f)); drawPath(TrazosValle.blando(q, az.entre(6f, 14f), az.entre(1.4f, 2.4f), az, 9, 0.2f), p.sombra(C.NIEVE_SOMBRA, LEJOS).copy(alpha = 0.25f)) }
        }
        ceja(v)
    }

    /** La ceja de nieve colgada sobre la cornisa, «que cualquier día caerá». */
    private fun DrawScope.ceja(v: VistaAntiguas) {
        val p = v.p
        run {
            val ceja = CORNISA_ARRIBA.map { it + Offset(0f, 1.6f) }
            val labio = ceja.mapIndexed { i, q -> q + Offset(-1.4f, 2.6f + 1.4f * sin(i * 1.3f)) }
            drawPath(poligono(ceja.map { it + Offset(0f, -2.4f) } + labio.reversed()), p.por(C.NIEVE, 0.45f, LEJOS))
            drawPath(curva(labio), p.sombra(C.NIEVE_SOMBRA, LEJOS).copy(alpha = 0.7f), style = Stroke(1.3f))
            for ((i, q) in labio.withIndex()) if (i % 2 == 1) drawLine(p.luz(lerp(C.NIEVE, C.AGUA, 0.2f), LEJOS).copy(alpha = 0.6f), q, q + Offset(0f, 1.6f + i % 3), 0.5f)
        }
    }

    /** La cascada de la garganta, entre la cara norte y la ladera de las vetas: un hilo blanco que baja al torrente; helada con el paso cerrado. */
    private fun DrawScope.cascada(v: VistaAntiguas, az: Azar) {
        val p = v.p
        val hilo = rota(pts(192f, 166f, 190f, 178f, 191f, 190f, 188f, 200f, 189f, 207f), az, 0.8f, 2)
        val agua = if (v.invierno) p.por(lerp(C.NIEVE, C.AGUA, 0.3f), 0.2f, LEJOS) else p.luz(lerp(C.NIEVE, C.AGUA, 0.2f), LEJOS)
        drawPath(curva(hilo), p.sombra(C.ESCORIA, LEJOS).copy(alpha = 0.4f), style = Stroke(2.6f, cap = StrokeCap.Round))
        drawPath(curva(hilo), agua.copy(alpha = 0.85f), style = Stroke(1.2f, cap = StrokeCap.Round))
        if (!v.invierno) drawPoints(hilo.map { it + Offset(az.entre(-1f, 1f), 0f) }, PointMode.Points, lerp(v.l.bajo, Color.White, 0.6f).copy(alpha = 0.7f * (1f - 0.6f * v.noche)), 0.9f, StrokeCap.Round)
    }

    /**
     * Las linternas que se mueven en la ladera de noche (parte viva): las de la búsqueda de Arn por todo el collado y las de
     * los de rescate que suben por la cara norte con la linterna de mina.
     */
    fun DrawScope.linternasEnLaLadera(v: VistaAntiguas, reloj: Float) {
        if (v.noche < 0.4f) return
        val busqueda = EscenaAntiguas.hoyEs(v.m, "busqueda") || EscenaAntiguas.hoyEs(v.m, "arn_falta")
        val rescate = EscenaAntiguas.hoyEs(v.m, "rescate")
        val f = v.noche
        if (busqueda) for (j in 0 until 7) {
            val base = listOf(Offset(240f, 150f), Offset(262f, 136f), Offset(290f, 122f), Offset(302f, 112f), Offset(226f, 162f), Offset(278f, 146f), Offset(312f, 128f))[j]
            val q = base + Offset(3f * sin(reloj * 0.17f + j * 1.9f), 1.2f * sin(reloj * 0.23f + j))
            lucecita(q, 0.65f, f * (0.75f + 0.25f * sin(reloj * 1.3f + j)))
        }
        if (rescate) for (j in 0 until 5) {
            val t = ((reloj * 0.012f + j * 0.16f) % 1f)
            val q = Offset(186f - 30f * t, 170f - 40f * t)
            lucecita(q, 0.6f, f * 0.9f)
        }
    }
}
