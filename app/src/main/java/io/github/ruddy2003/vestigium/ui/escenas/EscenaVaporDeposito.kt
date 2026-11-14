package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.briznas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cable
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.corona
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoMojado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * El patio del depósito: el suelo de balasto y carbonilla con sus vías, la cochera, la torre del agua, la carbonera, la
 * caseta del guardagujas y, delante de la máquina, la señal, el arca de las herramientas y lo demás del patio.
 */
internal object DepositoVapor {
    private val C = ColorVapor
    private const val H = EscenaVapor.H
    private const val RIEL = EscenaVapor.RIEL
    private val FUGA = EscenaVapor.FUGA

    /** Donde empieza el balasto del patio. */
    private const val BORDE = ValleVapor.BORDE

    /** Hasta dónde huyen las vías: hasta el pie del viaducto, por debajo del cual pasan. */
    private val T_FIN = (RIEL - ValleVapor.PIE) / (RIEL - H)

    /** La base de la cochera. */
    private const val COCHERA = 206f

    private val ARCA = Rect(16f, 246f, 86f, 276f)
    private val SOBRE = Offset(52f, 241.6f)
    private const val KO = 1.7f
    private val SENAL = Offset(366f, 267f)
    private const val SENAL_CIMA = 24f
    private val TRAVIESAS = Rect(316f, 255f, 356f, 283f)
    private val TORRE = Rect(138f, 136f, 164f, 222f)
    private val GARITA = Rect(350f, 174f, 378f, 199f)
    private val TANQUE = Rect(133f, 107f, 169f, 136f)

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.deposito(m: Momento, p: PincelVapor) {
        val l = p.l; val r = p.r
        suelo(m, p)
        val cosas = cosasDelPatio(m, p).sortedBy { it.pie.y }
        val hayMaquina = EscenaVapor.enElDeposito(m)
        // Las sombras del sol en el suelo: la de la cochera, la torre, la máquina y cada cosa del patio.
        clipRect(0f, BORDE, ANCHO, ALTO) {
            capaSombra(r) { c ->
                sombraArrojada(r, cocheraForma(), Offset(40f, COCHERA), c, BORDE, BORDE)
                sombraArrojada(r, Path().apply { addRect(Rect(TANQUE.left, TANQUE.top, TANQUE.right, TORRE.bottom)) }, Offset(151f, TORRE.bottom), c, BORDE, BORDE)
                sombraArrojada(r, Path().apply { addRect(GARITA) }, Offset(GARITA.center.x, GARITA.bottom), c, BORDE, BORDE)
                if (hayMaquina) sombraArrojada(r, MaquinaVapor.silueta(), Offset(MaquinaVapor.X0 + 20f, RIEL + 2f), c, BORDE, BORDE)
                cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, BORDE, BORDE) }
            }
            // De noche, cada cosa arroja la sombra de la luz que tiene más cerca.
            if (p.deNoche) capaSombra(rayoFijo(0.5f, 50f, 0.5f * l.noche, C.SOMBRA)) { c ->
                cosas.forEach { k -> val f = p.a.focos.maxByOrNull { it.fuerza / (1f + ((it.p.x - k.pie.x) * (it.p.x - k.pie.x)) / (it.alcance * it.alcance)) }!!; sombraArrojada(rayoDeFoco(f, k.pie, 0.5f, c), k.forma, k.pie, c, BORDE, BORDE) }
            }
        }
        if (p.deNoche) lucesEnElSuelo(m, p)
        caseta(m, p)
        telegrafo(m, p)
        vagones(m, p)
        cochera(m, p)
        torreDelAgua(m, p)
        garita(m, p)
        if (!EscenaVapor.descarrilado(m)) vagonDelApartadero(m, p)
        farolDelPoste(m, p)
        if (EscenaVapor.gala(m)) estandartes(m, p)
        if (EscenaVapor.descarrilado(m)) vagonDescarrilado(m, p)
        if (hayMaquina) with(MaquinaVapor) { maquina(m, p) }
        if (hayMaquina && p.deNoche) { val k = EscenaVapor.lumbre(m) * l.noche; val h = EscenaVapor.HOGAR
            resplandor(h, 34f, 0.9f * k, C.FUEGO); resplandor(h + Offset(-16f, 2f), 22f, 0.5f * k, C.FUEGO)
            drawPath(poligono(h + Offset(-12f, 30f), h + Offset(2f, 30f), h + Offset(14f, 52f), h + Offset(-34f, 52f)), Brush.verticalGradient(0f to C.FUEGO.copy(alpha = 0.34f * k), 1f to Color.Transparent, startY = h.y + 30f, endY = h.y + 52f)) }
        manga(m, p)
        // Cada cosa del patio se asienta en el suelo con su sombra de contacto.
        cosas.forEach { val b = it.forma.getBounds(); mancha(Offset(b.center.x, it.pie.y + 0.6f), b.width * 0.62f + 3f, 2.6f, l.tono(lerp(C.HOLLIN, C.SOMBRA, 0.3f)), 0.42f) }
        cosas.forEach { it.pintar(this) }
        adornos(m, p)
    }

    /** La x en el lienzo del punto de una vía que huye: [x0] es su sitio a la altura del carril de la máquina; [t], de 0 (ahí) a 1 (en el punto de fuga). */
    private fun huye(x0: Float, t: Float) = Offset(x0 + (FUGA.x - x0) * t, RIEL + (FUGA.y - RIEL) * t)

    private fun DrawScope.suelo(m: Momento, p: PincelVapor) {
        val l = p.l
        val az = Azar(Semilla.de("vapor", "suelo"))
        val mojado = EscenaVapor.mojado(m); val nieve = EscenaVapor.nevado(m)
        val base = if (nieve) lerp(C.BALASTO, C.NIEVE, 0.72f) else if (mojado) lerp(C.BALASTO, C.CARBONILLA, 0.5f) else lerp(C.BALASTO, C.CARBONILLA, 0.2f)
        // El patio, más claro y más velado al fondo, más oscuro y con más grano cerca.
        // De noche el suelo es el de la noche: la luz de cada farol la pone su charco, donde está (`lucesEnElSuelo`).
        fun piso(c: Color, lejos: Float = 0f) = l.tono(lerp(c, C.LUZ, 0.09f * p.r.dia * p.r.contraste), lejos)
        drawRect(Brush.verticalGradient(0f to piso(lerp(base, C.HUESO, 0.14f), 0.26f), 0.45f to piso(base), 1f to piso(lerp(base, C.CARBONILLA, 0.22f)), startY = BORDE, endY = ALTO), Offset(0f, BORDE), Size(ANCHO, ALTO - BORDE))
        // La explanación de la línea, que sale del patio y pasa bajo el viaducto.
        drawPath(poligono(huye(134f, T_FIN), huye(456f, T_FIN), huye(456f, 0.74f), huye(134f, 0.74f)), piso(lerp(base, C.HUESO, 0.14f), 0.3f))
        // Manchas de carbonilla y de ceniza, y los charcos de aceite.
        repeat(26) { val c = Offset(az.entre(0f, ANCHO), az.entre(BORDE + 6f, ALTO)); val k = (c.y - BORDE) / (ALTO - BORDE); mancha(c, az.entre(8f, 26f) * (0.4f + k), az.entre(1.6f, 5f) * (0.4f + k), p.cara(c, if (nieve) lerp(C.CARBONILLA, C.NIEVE, 0.3f) else if (it % 3 == 0) lerp(C.HUESO, C.BALASTO, 0.5f) else C.CARBONILLA), 0.3f) }
        repeat(30) { val c = Offset(az.entre(0f, ANCHO), az.entre(BORDE + 4f, RIEL - 4f)); val k = (c.y - BORDE) / (RIEL - BORDE); mancha(c, az.entre(10f, 30f) * (0.3f + k), az.entre(0.8f, 2.4f) * (0.4f + k), p.cara(c, if (nieve) lerp(C.CARBONILLA, C.NIEVE, 0.4f) else if (it % 2 == 0) lerp(C.HOLLIN, C.BALASTO, 0.3f) else lerp(C.HUESO, C.BALASTO, 0.4f)), 0.26f) }
        // La carbonilla y el aceite que gotean entre los carriles oscurecen el centro de cada vía.
        for (x0 in listOf(150f, 252f, 344f, 440f)) drawPath(poligono(huye(x0 - 6f, 0f), huye(x0 + 6f, 0f), huye(x0 + 6f, T_FIN), huye(x0 - 6f, T_FIN)), p.umbra(huye(x0, 0.3f), if (nieve) lerp(C.CARBONILLA, C.NIEVE, 0.3f) else C.CARBONILLA).copy(alpha = 0.3f))
        // Las vías que huyen hacia el valle: los carriles y sus traviesas, cada vez más juntas.
        for (x0 in listOf(150f, 252f, 344f, 440f)) {
            var t = 0.03f
            while (t < T_FIN) { val a = huye(x0 - 13f, t); val b = huye(x0 + 13f, t); drawLine(p.cara(a, lerp(C.MADERA, C.HOLLIN, 0.5f), lejos = 0.3f * t), a, b, (2.4f * (1f - t)).coerceAtLeast(0.4f)); t += 0.045f * (1f - t) + 0.006f }
            for (d in listOf(-9f, 9f)) {
                val a = huye(x0 + d, 0f); val b = huye(x0 + d, T_FIN)
                drawLine(p.umbra(a, C.HOLLIN), a + Offset(0f, 0.8f), b + Offset(0f, 0.4f), 1.6f)
                drawLine(p.arriba(a, lerp(C.HIERRO, Color.White, 0.5f)), a, b, 0.9f)
            }
        }
        // El foso de la cochera y los carriles que salen de su puerta a la vía de la máquina.
        for (x in listOf(44f, 68f)) { drawLine(p.umbra(Offset(x, 218f), C.HOLLIN), Offset(x, COCHERA) + Offset(0f, 0.6f), Offset(x + (x - 56f) * 0.5f, RIEL), 1.8f); drawLine(p.arriba(Offset(x, 218f), lerp(C.HIERRO, Color.White, 0.45f)), Offset(x, COCHERA), Offset(x + (x - 56f) * 0.5f, RIEL - 0.6f), 0.9f) }
        drawPath(poligono(Offset(48f, COCHERA + 2f), Offset(64f, COCHERA + 2f), Offset(67f, RIEL - 6f), Offset(45f, RIEL - 6f)), p.umbra(Offset(56f, 218f), C.HOLLIN).copy(alpha = 0.7f))
        // La vía de la máquina, de lado: la banqueta de balasto, las cabezas de las traviesas y el carril.
        drawRect(piso(lerp(base, C.HUESO, 0.1f)), Offset(0f, RIEL - 1f), Size(ANCHO, 10f))
        drawRect(l.tono(mult(base, p.r.color, 0.6f)).copy(alpha = 0.5f), Offset(0f, RIEL + 8f), Size(ANCHO, 2.4f))
        desvio(m, p, nieve)
        var x = -2f; var i = 0
        while (x < ANCHO) { drawRect(p.cara(Offset(x, RIEL + 3f), lerp(C.MADERA, C.HOLLIN, 0.5f + 0.1f * (i % 3))), Offset(x, RIEL + 1.2f), Size(4.6f, 3.4f)); drawRect(p.arriba(Offset(x, RIEL + 1f), if (nieve) C.NIEVE else lerp(C.MADERA, C.HUESO, 0.2f)), Offset(x, RIEL + 1.2f), Size(4.6f, 0.7f)); x += 7.4f; i++ }
        drawRect(p.umbra(Offset(190f, RIEL), C.HOLLIN), Offset(0f, RIEL - 0.2f), Size(ANCHO, 1.8f)); drawRect(p.arriba(Offset(190f, RIEL), lerp(C.HIERRO, Color.White, 0.6f)), Offset(0f, RIEL - 1f), Size(ANCHO, 0.9f))
        // Delante: piedras del balasto, trozos de carbón caídos y la hierba que sale entre las vías.
        val piedras = ArrayList<Offset>(); val carbones = ArrayList<Offset>()
        repeat(220) { piedras += Offset(az.entre(0f, ANCHO), az.entre(RIEL + 10f, ALTO)) }; repeat(60) { carbones += Offset(az.entre(0f, ANCHO), az.entre(RIEL + 10f, ALTO)) }
        drawPoints(piedras, androidx.compose.ui.graphics.PointMode.Points, piso(lerp(base, C.HUESO, 0.5f)).copy(alpha = 0.6f), 1.3f, StrokeCap.Round)
        drawPoints(carbones, androidx.compose.ui.graphics.PointMode.Points, l.tono(C.HOLLIN).copy(alpha = 0.7f), 1.7f, StrokeCap.Round)
        // La otra vía del patio, en el borde de abajo, y el paso de tablas que la cruza.
        var xv = -3f; var iv = 0
        while (xv < ANCHO) { drawRect(p.cara(Offset(xv, 283f), lerp(C.MADERA, C.HOLLIN, 0.5f + 0.1f * (iv % 3))), Offset(xv, 281.6f), Size(6f, 4f)); drawRect(p.arriba(Offset(xv, 281.6f), if (nieve) C.NIEVE else lerp(C.MADERA, C.HUESO, 0.2f)), Offset(xv, 281.6f), Size(6f, 0.8f)); xv += 9.6f; iv++ }
        drawRect(p.umbra(Offset(190f, 281f), C.HOLLIN), Offset(0f, 279.8f), Size(ANCHO, 2.2f)); drawRect(p.arriba(Offset(190f, 280f), lerp(C.HIERRO, Color.White, 0.6f)), Offset(0f, 278.8f), Size(ANCHO, 1.1f))
        // La senda de carbonilla pisada, más clara, a lo largo de la vía de la máquina.
        for (i in 0..8) mancha(Offset(20f + i * 44f + az.entre(-8f, 8f), RIEL + 16f + az.entre(-2f, 3f)), az.entre(24f, 40f), az.entre(2.6f, 4.6f), piso(lerp(base, C.HUESO, 0.36f)), 0.3f)
        if (!nieve) for ((x0, x1, y) in listOf(Triple(2f, 18f, 282f), Triple(108f, 132f, 284f), Triple(262f, 280f, 248f), Triple(300f, 330f, 284f), Triple(176f, 190f, 246f))) briznas(l, x0, x1, y, lerp(hierbaDe(m.ficha.estacion), C.BALASTO, 0.3f), az, 4.4f, 2.6f)
        if (nieve) repeat(9) { val c = Offset(az.entre(0f, ANCHO), az.entre(RIEL + 12f, ALTO)); mancha(c, az.entre(16f, 36f), az.entre(3f, 6f), p.arriba(c, C.NIEVE), 0.7f) }
        // Los charcos devuelven el cielo: siempre hay uno donde rebosa la aguada y otro delante de la máquina, donde
        // purga; con lluvia, más. De noche, cada uno recoge la luz del farol que tiene cerca.
        val charcos = buildList { if (!nieve) { add(Offset(126f, 243f) to 13f); add(Offset(298f, 244f) to 15f); add(Offset(372f, 262f) to 8f) }
            if (mojado) { add(Offset(120f, 262f) to 22f); add(Offset(248f, 280f) to 30f); add(Offset(30f, 244f) to 12f); add(Offset(346f, 246f) to 13f) } }
        for ((c, rx) in charcos) {
            val ry = rx * 0.17f
            drawOval(l.tono(lerp(C.HOLLIN, C.BALASTO, 0.2f)).copy(alpha = 0.7f), Offset(c.x - rx - 1f, c.y - ry - 0.4f), Size(rx * 2f + 2f, ry * 2f + 1f))
            drawOval(lerp(l.medio, l.tono(C.HOLLIN), 0.3f), Offset(c.x - rx, c.y - ry), Size(rx * 2f, ry * 2f))
            drawOval(lerp(l.alto, l.tono(C.HOLLIN), 0.3f).copy(alpha = 0.7f), Offset(c.x - rx * 0.8f, c.y - ry * 0.2f), Size(rx * 1.5f, ry * 1.1f))
            drawLine(Color.White.copy(alpha = 0.36f * (1f - l.noche)), c + Offset(-rx * 0.5f, -ry * 0.5f), c + Offset(rx * 0.3f, -ry * 0.5f), 0.45f)
            if (p.deNoche) { val f = p.a.focos.maxByOrNull { it.fuerza / (1f + ((it.p.x - c.x) * (it.p.x - c.x)) / (it.alcance * it.alcance)) }!!
                val k = f.fuerza / (1f + ((f.p.x - c.x) * (f.p.x - c.x)) / (f.alcance * f.alcance)); drawOval(f.color.copy(alpha = (0.75f * k).coerceIn(0f, 0.8f)), Offset(c.x - rx * 0.5f, c.y - ry * 0.6f), Size(rx, ry * 1.2f)) }
        }
        if (mojado && p.deNoche) for (f in p.a.focos) reflejoMojado(Offset(f.p.x, maxOf(f.suelo, RIEL + 10f)), 30f, 9f, f.color, 0.5f * f.fuerza, az)
    }

    /** El charco de luz que cada farol, el hogar y el brasero dejan en el suelo, cada uno en su sitio. En la nieve, que ya es clara, menos. */
    private fun DrawScope.lucesEnElSuelo(m: Momento, p: PincelVapor) {
        val base = lerp(C.BALASTO, C.CARBONILLA, 0.3f); val cuanto = if (EscenaVapor.nevado(m)) 0.5f else 1.7f
        for (f in p.a.focos) { val c = Offset(f.p.x, f.suelo + 2f)
            charcoDeLuz(f, c, f.alcance * 1.5f, f.alcance * 0.36f, base, cuanto * 0.7f); charcoDeLuz(f, c, f.alcance * 0.7f, f.alcance * 0.18f, base, cuanto) }
    }

    /** El cambio de agujas: de la vía de la máquina sale, hacia quien mira, la vía que baja al otro lado del patio. */
    private fun DrawScope.desvio(m: Momento, p: PincelVapor, nieve: Boolean) {
        fun carril(x0: Float, y1: Float, t: Float) = Offset(x0 + (386f - x0) * t, RIEL + (y1 - RIEL) * t * (0.35f + 0.65f * t))
        // Las traviesas largas del cambio, en abanico, y encima los dos carriles que se apartan.
        for (i in 0..11) { val t = 0.04f + i * 0.085f; val a = carril(340f, RIEL + 13f, t) + Offset(3f, -1.6f); val b = carril(316f, RIEL + 27f, t) + Offset(-3f, 1.8f)
            drawLine(p.cara(a, lerp(C.MADERA, C.HOLLIN, 0.5f + 0.1f * (i % 3))), a, b, 3f); drawLine(p.arriba(a, if (nieve) C.NIEVE else lerp(C.MADERA, C.HUESO, 0.2f)).copy(alpha = 0.7f), a + Offset(-1.2f, -0.2f), b + Offset(-1.2f, -0.2f), 0.6f) }
        for ((x0, y1) in listOf(340f to RIEL + 13f, 316f to RIEL + 27f)) {
            val via = Path().apply { val a = carril(x0, y1, 0f); moveTo(a.x, a.y); for (k in 1..12) { val q = carril(x0, y1, k / 12f); lineTo(q.x, q.y) } }
            drawPath(via, p.umbra(carril(x0, y1, 0.5f), C.HOLLIN), style = Stroke(1.9f)); drawPath(Path().apply { addPath(via, Offset(0f, -0.8f)) }, p.arriba(carril(x0, y1, 0.5f), lerp(C.HIERRO, Color.White, 0.6f)), style = Stroke(0.9f))
        }
        // La varilla que va de la palanca a las agujas.
        drawLine(p.umbra(Offset(310f, RIEL + 10f), C.HIERRO), Offset(305f, 250f), Offset(318f, RIEL + 3f), 0.7f)
    }

    // ---------------------------------------------------------------- Lo de detrás de la máquina

    /** El frente de la cochera: el hastial de ladrillo, que el borde del cuadro corta por la izquierda. */
    private fun cocheraForma(): Path = poligono(listOf(Offset(-40f, COCHERA), Offset(-40f, 118f), Offset(28f, 76f), Offset(96f, 118f), Offset(132f, 118.6f), Offset(132f, 184f), Offset(96f, COCHERA)))

    /** Un paño de ladrillo: el color, las hiladas y unos ladrillos sueltos más claros o más quemados. */
    private fun DrawScope.ladrillo(p: PincelVapor, zona: Rect, forma: Path, mira: Float, az: Azar, lejos: Float = 0f) {
        drawPath(forma, Brush.verticalGradient(0f to p.cara(Offset(zona.center.x, zona.top), lerp(C.LADRILLO, C.HOLLIN, 0.3f), mira, lejos), 0.4f to p.cara(zona.center, C.LADRILLO, mira, lejos), 1f to p.cara(Offset(zona.center.x, zona.bottom), lerp(C.LADRILLO, C.LADRILLO_OSC, 0.5f), mira, lejos), startY = zona.top, endY = zona.bottom))
        clipPath(forma) {
            var y = zona.top + 2f; while (y < zona.bottom) { drawLine(p.umbra(Offset(zona.center.x, y), C.LADRILLO_OSC, lejos).copy(alpha = 0.3f), Offset(zona.left, y), Offset(zona.right, y), 0.35f); y += 2.6f }
            repeat((zona.width * zona.height / 60f).toInt()) { val c = Offset(az.entre(zona.left, zona.right), zona.top + 2.6f * az.entero((zona.height / 2.6f).toInt().coerceAtLeast(1)) + 0.4f); drawRect(p.cara(c, if (az.real() < 0.5f) lerp(C.LADRILLO, C.HUESO, 0.22f) else lerp(C.LADRILLO_OSC, C.HOLLIN, 0.4f), mira, lejos).copy(alpha = 0.6f), c, Size(4.4f, 1.9f)) }
        }
    }

    private fun DrawScope.cochera(m: Momento, p: PincelVapor) {
        val l = p.l; val az = Azar(Semilla.de("vapor", "cochera")); val nieve = EscenaVapor.nevado(m)
        // El costado, que huye, con sus ventanas de arco; y encima el faldón del tejado, de cristal negro de hollín.
        val costado = poligono(Offset(96f, 118f), Offset(132f, 118.6f), Offset(132f, 184f), Offset(96f, COCHERA))
        ladrillo(p, Rect(96f, 118f, 132f, COCHERA), costado, 1f, az)
        for ((x, w) in listOf(101f to 8f, 113f to 6.6f, 123f to 5.4f)) { val y0 = 140f - (x - 96f) * 0.1f; val y1 = 176f - (x - 96f) * 0.42f
            val vent = Path().apply { moveTo(x, y1); lineTo(x, y0 + w * 0.5f); cubicTo(x, y0, x + w, y0, x + w, y0 + w * 0.5f); lineTo(x + w, y1 - 2f); close() }
            drawPath(vent, if (p.deNoche) lerp(C.GAS, C.FUEGO, 0.2f) else p.umbra(Offset(x, y0), lerp(C.HOLLIN, C.PIZARRA, 0.3f))); drawPath(vent, p.cara(Offset(x, y0), C.PIEDRA, 1f), style = Stroke(0.8f))
            if (p.deNoche) { clipPath(vent) { drawRect(lerp(C.GAS, Color.White, 0.5f).copy(alpha = 0.5f), Offset(x, y0), Size(w, (y1 - y0) * 0.4f)) }; resplandor(Offset(x + w / 2f, (y0 + y1) / 2f), w * 2.4f, 0.5f * l.noche, C.GAS) }
            drawLine((if (p.deNoche) l.tono(C.HOLLIN) else p.cara(Offset(x, y0), C.HUESO, 1f)).copy(alpha = 0.6f), Offset(x + w / 2f, y0 + 1f), Offset(x + w / 2f, y1 - 1f), 0.4f)
            for (k in 1..2) drawLine((if (p.deNoche) l.tono(C.HOLLIN) else p.cara(Offset(x, y0), C.HUESO, 1f)).copy(alpha = 0.5f), Offset(x, y0 + (y1 - y0) * k / 3f), Offset(x + w, y0 + (y1 - y0) * k / 3f - 1f), 0.35f) }
        val faldon = poligono(Offset(28f, 76f), Offset(96f, 118f), Offset(132f, 118.6f), Offset(81.5f, 87.4f))
        drawPath(faldon, p.cara(Offset(90f, 100f), if (nieve) lerp(C.HOLLIN, C.NIEVE, 0.7f) else lerp(C.HOLLIN, C.PIZARRA, 0.35f), 1f))
        clipPath(faldon) {
            // Las tiras de cristal entre los baquetones, con algún vidrio que todavía brilla.
            for (i in 0..12) { val t = i / 12f; drawLine(p.umbra(Offset(60f + 40f * t, 90f), C.HOLLIN).copy(alpha = 0.7f), Offset(28f + 53.5f * t, 76f + 11.4f * t), Offset(96f + 36f * t, 118f + 0.6f * t), 0.5f) }
            for (k in listOf(0.3f, 0.62f)) drawLine(p.umbra(Offset(80f, 100f), C.HOLLIN).copy(alpha = 0.6f), Offset(28f + 68f * k, 76f + 42f * k), Offset(81.5f + 50.5f * k, 87.4f + 31.2f * k), 0.5f)
            // De noche, la luz de dentro se trasluce en los cristales menos sucios.
            if (!nieve) repeat(9) { val t = az.entre(0.1f, 0.9f); val k = az.entre(0.2f, 0.9f); val c = Offset(28f + 53.5f * t + (68f - 17.5f * t) * k, 76f + 11.4f * t + (42f - 10.8f * t) * k); drawRect(if (p.deNoche) C.GAS.copy(alpha = 0.42f) else lerp(l.medio, Color.White, 0.2f).copy(alpha = 0.34f), c, Size(3f, 1.6f)) }
            if (p.deNoche) mancha(Offset(92f, 104f), 30f, 9f, C.GAS, 0.14f)
        }
        // El lucernario de la cumbrera, por donde sale el humo de las máquinas que se encienden.
        drawPath(poligono(Offset(34f, 77.2f), Offset(34f, 70f), Offset(76f, 79f), Offset(76f, 86.2f)), p.cara(Offset(55f, 78f), lerp(C.HOLLIN, C.HIERRO, 0.35f), 1f)); drawPath(poligono(Offset(31f, 70.6f), Offset(34f, 66f), Offset(77f, 75.2f), Offset(79f, 80f)), p.arriba(Offset(55f, 72f), if (nieve) C.NIEVE else lerp(C.PIZARRA, C.HOLLIN, 0.3f)))
        for (i in 0..6) drawLine(p.umbra(Offset(40f + i * 6f, 78f), C.HOLLIN).copy(alpha = 0.6f), Offset(37f + i * 6f, 72.4f + i * 1.28f), Offset(37f + i * 6f, 77.6f + i * 1.28f), 0.6f)
        // El hastial: ladrillo, con su faja de piedra, su óculo y las dos puertas de arco.
        val frente = poligono(listOf(Offset(-40f, COCHERA), Offset(-40f, 118f), Offset(28f, 76f), Offset(96f, 118f), Offset(96f, COCHERA)))
        ladrillo(p, Rect(-40f, 76f, 96f, COCHERA), frente, 0f, az)
        clipPath(frente) {
            // El hollín, que sube de las puertas y chorrea de la cornisa.
            for (x in listOf(-2f, 56f)) mancha(Offset(x, 132f), 24f, 20f, p.umbra(Offset(x, 132f), C.HOLLIN), 0.5f)
            repeat(8) { val x = az.entre(-30f, 92f); drawLine(p.umbra(Offset(x, 130f), C.HOLLIN).copy(alpha = 0.22f), Offset(x, 120f), Offset(x + az.entre(-1f, 1f), az.entre(140f, 176f)), az.entre(1f, 2.6f)) }
            drawRect(p.cara(Offset(28f, 120f), C.PIEDRA), Offset(-40f, 118f), Size(136f, 3f)); drawRect(p.umbra(Offset(28f, 122f), C.LADRILLO_OSC).copy(alpha = 0.5f), Offset(-40f, 121f), Size(136f, 1.4f))
            drawRect(p.cara(Offset(28f, 200f), lerp(C.PIEDRA, C.HOLLIN, 0.3f)), Offset(-40f, 198f), Size(136f, 8f))
        }
        // Los bordes del hastial: la albardilla de piedra, que da su sombra.
        for ((a, b) in listOf(Offset(-40f, 118f) to Offset(28f, 76f), Offset(28f, 76f) to Offset(96f, 118f))) { drawLine(p.umbra(a, C.LADRILLO_OSC).copy(alpha = 0.5f), a + Offset(0f, 2.6f), b + Offset(0f, 2.6f), 1.6f); drawLine(p.arriba(a, if (nieve) C.NIEVE else C.PIEDRA), a, b, 2.4f) }
        drawRect(p.cara(Offset(94f, 160f), lerp(C.LADRILLO, C.LADRILLO_OSC, 0.4f), 1f), Offset(92.6f, 118f), Size(3.4f, 88f))
        val oculo = Offset(28f, 102f)
        drawCircle(p.cara(oculo, C.PIEDRA), 8.6f, oculo); drawCircle(if (p.deNoche) lerp(C.GAS, C.FUEGO, 0.2f) else p.umbra(oculo, lerp(C.HOLLIN, C.PIZARRA, 0.3f)), 6.4f, oculo); if (p.deNoche) resplandor(oculo, 16f, 0.5f * l.noche, C.GAS)
        for (k in 0..3) { val a = k * 0.785f; drawLine((if (p.deNoche) l.tono(C.HOLLIN) else p.cara(oculo, C.HUESO)).copy(alpha = 0.7f), oculo + Offset(kotlin.math.cos(a), kotlin.math.sin(a)) * 6.4f, oculo - Offset(kotlin.math.cos(a), kotlin.math.sin(a)) * 6.4f, 0.5f) }
        for ((x0, x1) in listOf(-26f to 18f, 34f to 78f)) puerta(m, p, x0, x1, az)
        // La farola de gas, en su brazo de hierro, junto a la puerta.
        val f = EscenaVapor.FAROLA
        drawLine(p.umbra(f, C.HIERRO), Offset(95f, f.y - 4f), f + Offset(0f, -4f), 1f); drawLine(p.umbra(f, C.HIERRO), Offset(95f, f.y + 4f), f + Offset(-2f, -3f), 0.6f)
        val encendida = l.noche > 0.2f
        drawPath(poligono(f + Offset(-3f, -4f), f + Offset(3f, -4f), f + Offset(2f, 3.4f), f + Offset(-2f, 3.4f)), if (encendida) lerp(C.GAS, Color.White, 0.4f) else p.cara(f, lerp(C.HUESO, C.PIZARRA, 0.5f)))
        drawPath(poligono(f + Offset(-3.6f, -4f), f + Offset(0f, -7f), f + Offset(3.6f, -4f)), p.umbra(f, C.HIERRO)); drawRect(p.umbra(f, C.HIERRO), f + Offset(-2.2f, 3.4f), Size(4.4f, 1f))
        if (encendida) { resplandor(f, 22f, 0.8f * l.noche, C.GAS); resplandor(f, 58f, 0.3f * l.noche, C.GAS) }
        if (EscenaVapor.navidad(m)) corona(l, Offset(86f, 172f), 6f, cinta = C.ROJO)
        if (EscenaVapor.luto(m)) { drawLine(p.umbra(f, C.HOLLIN), f + Offset(-2f, 4.4f), f + Offset(-4f, 13f), 1.4f); drawLine(p.umbra(f, C.HOLLIN), f + Offset(2f, 4.4f), f + Offset(3f, 14f), 1.4f) }
        if (EscenaVapor.elecciones(m)) for ((x, c) in listOf(82f to C.HUESO, 86.4f to lerp(C.LATON, C.HUESO, 0.4f))) { drawRect(p.cara(Offset(x, 182f), c), Offset(x, 178f), Size(4f, 6f)); drawRect(p.cara(Offset(x, 180f), C.AZUL_PANA).copy(alpha = 0.6f), Offset(x + 0.8f, 179f), Size(2.4f, 2f)) }
    }

    /** Una puerta de la cochera, de arco, con sus dovelas de piedra y sus hojas abiertas. Dentro, a oscuras, el frente de otra máquina. */
    private fun DrawScope.puerta(m: Momento, p: PincelVapor, x0: Float, x1: Float, az: Azar) {
        val l = p.l; val xm = (x0 + x1) / 2f; val arranque = 160f; val clave = 140f
        val hueco = Path().apply { moveTo(x0, COCHERA); lineTo(x0, arranque); cubicTo(x0, clave - 6f, x1, clave - 6f, x1, arranque); lineTo(x1, COCHERA); close() }
        drawPath(hueco, p.umbra(Offset(xm, 180f), lerp(C.HOLLIN, C.LADRILLO_OSC, 0.12f)))
        val dentro = if (l.noche > 0.2f || EscenaVapor.fuego(m) == EscenaVapor.Fuego.ENCENDIENDO) 0.5f + 0.3f * l.noche else 0.12f
        clipPath(hueco) {
            // La luz de dentro, las columnas de hierro y el frente redondo de la máquina que duerme al fondo.
            resplandor(Offset(xm, 186f), 34f, 0.6f * dentro, C.GAS)
            for (x in listOf(x0 + 9f, x1 - 9f)) drawLine(lerp(C.HIERRO, C.GAS, 0.2f * dentro), Offset(x, 150f), Offset(x, COCHERA), 1.2f)
            val c = Offset(xm, 184f)
            drawCircle(lerp(C.HOLLIN, C.HIERRO, 0.3f + 0.3f * dentro), 10f, c); drawCircle(lerp(C.HIERRO, C.HUESO, 0.2f * dentro), 10f, c, style = Stroke(0.8f)); drawCircle(lerp(C.HOLLIN, C.HIERRO, 0.5f), 1.2f, c)
            drawRect(lerp(C.HOLLIN, C.HIERRO, 0.3f), Offset(xm - 2.4f, 162f), Size(4.8f, 13f)); drawRect(lerp(C.ROJO, C.HOLLIN, 0.5f - 0.3f * dentro), Offset(xm - 14f, 195f), Size(28f, 4f))
            drawRect(lerp(C.HOLLIN, C.HIERRO, 0.2f), Offset(xm - 12f, 199f), Size(24f, 7f))
        }
        drawPath(Path().apply { moveTo(x0, COCHERA); lineTo(x0, arranque); cubicTo(x0, clave - 6f, x1, clave - 6f, x1, arranque); lineTo(x1, COCHERA) }, p.cara(Offset(xm, 150f), C.PIEDRA), style = Stroke(2.4f))
        for (k in 1..5) { val t = k / 6f; val a = Math.PI.toFloat() * t; drawLine(p.umbra(Offset(xm, 145f), C.LADRILLO_OSC).copy(alpha = 0.5f), Offset(xm - kotlin.math.cos(a) * 21f, arranque - kotlin.math.sin(a) * 14f), Offset(xm - kotlin.math.cos(a) * 23.6f, arranque - kotlin.math.sin(a) * 16.4f), 0.4f) }
        // Las hojas de madera, abiertas contra el muro.
        for ((x, mira) in listOf(x0 - 7f to -1f, x1 + 1f to 1f)) { if (x < -40f || x > 90f) continue
            drawRect(p.cara(Offset(x, 180f), lerp(C.VERDE, C.HOLLIN, 0.45f), mira), Offset(x, 156f), Size(6f, 50f)); for (y in listOf(166f, 186f)) drawLine(p.umbra(Offset(x, y), C.HIERRO), Offset(x, y), Offset(x + 6f, y), 0.7f) }
    }

    private fun DrawScope.torreDelAgua(m: Momento, p: PincelVapor) {
        val az = Azar(Semilla.de("vapor", "torre"))
        // La torre de ladrillo, con su puerta y su ventana, y encima el tanque de planchas de hierro.
        ladrillo(p, TORRE, Path().apply { addRect(TORRE) }, 0f, az)
        val sombraLado = if (p.r.lat >= 0f) TORRE.left else TORRE.right - 6f
        drawRect(p.umbra(Offset(sombraLado, 180f), C.LADRILLO_OSC).copy(alpha = 0.55f * p.r.dia), Offset(sombraLado, TORRE.top), Size(6f, TORRE.height))
        drawRect(p.cara(TORRE.center, C.PIEDRA), Offset(TORRE.left - 1.4f, TORRE.top), Size(TORRE.width + 2.8f, 2.4f))
        val arco = Path().apply { moveTo(146f, 176f); lineTo(146f, 158f); cubicTo(146f, 152f, 156f, 152f, 156f, 158f); lineTo(156f, 176f); close() }
        drawPath(arco, if (p.deNoche) lerp(C.GAS, C.HOLLIN, 0.6f) else p.umbra(Offset(151f, 164f), C.HOLLIN)); drawPath(arco, p.cara(Offset(151f, 160f), C.PIEDRA), style = Stroke(0.8f))
        val tanque = Path().apply { addRect(TANQUE) }
        drawPath(tanque, Brush.horizontalGradient(0f to p.cara(Offset(TANQUE.left, 120f), lerp(C.HIERRO, C.COBRE, 0.14f), -1f), 0.5f to p.cara(Offset(151f, 120f), lerp(C.HIERRO, C.COBRE, 0.14f)), 1f to p.cara(Offset(TANQUE.right, 120f), lerp(C.HIERRO, C.COBRE, 0.14f), 1f), startX = TANQUE.left, endX = TANQUE.right))
        clipPath(tanque) {
            for (x in listOf(142f, 151f, 160f)) drawLine(p.umbra(Offset(x, 120f), C.HOLLIN).copy(alpha = 0.5f), Offset(x, TANQUE.top), Offset(x, TANQUE.bottom), 0.5f); drawLine(p.umbra(Offset(151f, 121f), C.HOLLIN).copy(alpha = 0.5f), Offset(TANQUE.left, 121.6f), Offset(TANQUE.right, 121.6f), 0.5f)
            val remaches = ArrayList<Offset>(); for (x in listOf(137.6f, 146.6f, 155.6f, 164.6f)) for (i in 0..8) remaches += Offset(x, TANQUE.top + 2f + i * 3.2f)
            drawPoints(remaches, androidx.compose.ui.graphics.PointMode.Points, p.arriba(Offset(151f, 120f), lerp(C.HIERRO, C.HUESO, 0.4f)).copy(alpha = 0.6f), 0.7f, StrokeCap.Round)
            // Las chorreras de óxido y de cal.
            repeat(6) { val x = az.entre(TANQUE.left, TANQUE.right); drawLine(p.cara(Offset(x, 126f), if (it % 2 == 0) C.COBRE else C.HUESO).copy(alpha = 0.3f), Offset(x, az.entre(108f, 122f)), Offset(x, TANQUE.bottom), az.entre(0.6f, 1.6f)) }
        }
        drawRect(p.arriba(Offset(151f, TANQUE.top), if (EscenaVapor.nevado(m)) C.NIEVE else lerp(C.HIERRO, C.HUESO, 0.3f)), Offset(TANQUE.left - 1f, TANQUE.top - 1.4f), Size(TANQUE.width + 2f, 1.8f))
        drawRect(p.umbra(Offset(151f, TANQUE.bottom), C.HOLLIN).copy(alpha = 0.55f), Offset(TORRE.left, TANQUE.bottom), Size(TORRE.width, 3f))
        // La escalera de hierro y el indicador del nivel, con su flotador.
        for (x in listOf(160.6f, 163.4f)) drawLine(p.umbra(Offset(x, 170f), C.HIERRO), Offset(x, TANQUE.top), Offset(x, TORRE.bottom), 0.5f); var y = TANQUE.top + 3f; while (y < TORRE.bottom) { drawLine(p.umbra(Offset(162f, y), C.HIERRO), Offset(160.6f, y), Offset(163.4f, y), 0.4f); y += 4f }
        drawRect(p.cara(Offset(170.6f, 122f), C.HUESO, 1f), Offset(169.6f, 110f), Size(2.2f, 24f)); drawRect(p.cara(Offset(170.6f, 122f), C.ROJO, 1f), Offset(169f, 114f + 6f * (m.azar("nivel").real())), Size(3.4f, 2f))
        // Con la helada, carámbanos en el borde del tanque.
        if (EscenaVapor.helada(m) || EscenaVapor.nevado(m)) for (i in 0..9) { val x = TANQUE.left + 2f + i * 3.6f; drawPath(poligono(Offset(x - 0.7f, TANQUE.bottom), Offset(x + 0.7f, TANQUE.bottom), Offset(x, TANQUE.bottom + 3f + (i * 7 % 5))), lerp(C.NIEVE, p.l.medio, 0.3f).copy(alpha = 0.9f)) }
    }

    /** La manga de cuero de la aguada: recogida contra la torre o, cuando cargan agua, metida en el ténder. */
    private fun DrawScope.manga(m: Momento, p: PincelVapor) {
        val codo = Offset(134f, 142f)
        drawLine(p.umbra(codo, C.HIERRO), Offset(140f, 140f), codo, 2.6f, StrokeCap.Round)
        if (EscenaVapor.aguada(m)) {
            val boca = Offset(124f, RIEL - 58f)
            drawPath(Path().apply { moveTo(codo.x, codo.y); cubicTo(126f, 146f, 122f, 160f, boca.x, boca.y) }, p.cara(Offset(126f, 160f), lerp(C.MADERA, C.HOLLIN, 0.35f)), style = Stroke(3.4f, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(codo.x, codo.y); cubicTo(126f, 146f, 122f, 160f, boca.x, boca.y) }, p.arriba(Offset(126f, 160f), lerp(C.MADERA, C.HUESO, 0.3f)).copy(alpha = 0.5f), style = Stroke(0.8f, cap = StrokeCap.Round))
            // El agua que rebosa y chorrea por el costado del ténder.
            for (dx in listOf(-3f, 2f, 5f)) drawLine(lerp(p.l.medio, Color.White, 0.5f).copy(alpha = 0.6f), boca + Offset(dx, 1f), boca + Offset(dx + 0.6f, 14f + dx), 0.6f)
        } else {
            drawPath(Path().apply { moveTo(codo.x, codo.y); cubicTo(130f, 150f, 131f, 164f, 133.4f, 174f) }, p.cara(Offset(131f, 160f), lerp(C.MADERA, C.HOLLIN, 0.35f)), style = Stroke(3.4f, cap = StrokeCap.Round))
            drawLine(p.umbra(Offset(133f, 170f), C.HIERRO), Offset(133.4f, 172f), Offset(138f, 168f), 0.6f)
        }
    }

    /** Los vagones de carbón, en el apartadero del fondo: una fila que se achica con la distancia. */
    private fun DrawScope.vagones(m: Momento, p: PincelVapor) {
        val az = Azar(Semilla.de("vapor", "vagones"))
        for ((i, t) in listOf(0.62f, 0.52f, 0.4f, 0.26f).withIndex()) vagon(m, p, az, i, t, 0.42f)
    }

    /**
     * El vagón de carbón que espera en el apartadero, a la derecha de la máquina, con su lona a medio echar. Con el
     * vendaval la lona se suelta y vuela, sujeta por una sola esquina.
     */
    private fun DrawScope.vagonDelApartadero(m: Momento, p: PincelVapor) {
        val caja = vagon(m, p, Azar(Semilla.de("vapor", "vagon")), 1, 0.12f, 0.06f)
        val lona = lerp(C.PRADO, C.HOLLIN, 0.55f); val en = caja.center
        if (EscenaVapor.ventarron(m)) {
            val a = Offset(caja.right - 1f, caja.top)
            val vuela = Path().apply { moveTo(a.x, a.y); cubicTo(a.x - 8f, a.y - 16f, a.x - 20f, a.y - 4f, a.x - 30f, a.y - 17f); cubicTo(a.x - 34f, a.y - 10f, a.x - 30f, a.y - 4f, a.x - 36f, a.y + 1f); cubicTo(a.x - 24f, a.y + 2f, a.x - 14f, a.y + 8f, a.x - 4f, a.y + 5f); close() }
            drawPath(vuela, Brush.verticalGradient(0f to p.arriba(en, lerp(lona, C.HUESO, 0.2f)), 1f to p.umbra(en, lona), startY = a.y - 17f, endY = a.y + 8f))
            drawPath(Path().apply { moveTo(a.x - 6f, a.y - 7f); cubicTo(a.x - 14f, a.y - 4f, a.x - 20f, a.y + 2f, a.x - 30f, a.y - 6f) }, p.umbra(en, C.HOLLIN).copy(alpha = 0.5f), style = Stroke(0.5f))
            for (q in listOf(Offset(a.x - 30f, a.y - 17f), Offset(a.x - 36f, a.y + 1f))) drawLine(p.cara(en, C.HUESO), q, q + Offset(-5f, 2f), 0.4f)
        } else {
            val echada = Path().apply { moveTo(caja.left + caja.width * 0.42f, caja.top - caja.height * 0.2f); cubicTo(caja.left + caja.width * 0.6f, caja.top - caja.height * 0.46f, caja.right - 4f, caja.top - caja.height * 0.4f, caja.right + 1f, caja.top - 1f)
                lineTo(caja.right + 1f, caja.top + caja.height * 0.5f); lineTo(caja.left + caja.width * 0.7f, caja.top + caja.height * 0.62f); lineTo(caja.left + caja.width * 0.5f, caja.top + caja.height * 0.3f); close() }
            drawPath(echada, Brush.verticalGradient(0f to p.arriba(en, if (EscenaVapor.nevado(m)) C.NIEVE else lerp(lona, C.HUESO, 0.2f)), 0.5f to p.cara(en, lona), 1f to p.umbra(en, lona), startY = caja.top - caja.height * 0.46f, endY = caja.top + caja.height * 0.62f))
            for (k in 1..2) drawLine(p.umbra(en, C.HOLLIN).copy(alpha = 0.4f), Offset(caja.left + caja.width * (0.5f + 0.17f * k), caja.top - caja.height * 0.36f), Offset(caja.left + caja.width * (0.56f + 0.17f * k), caja.top + caja.height * 0.56f), 0.4f)
            for (x in listOf(caja.left + caja.width * 0.72f, caja.right - 2f)) drawLine(p.cara(en, C.HUESO).copy(alpha = 0.8f), Offset(x, caja.top + caja.height * 0.56f), Offset(x + 0.6f, caja.bottom - 1f), 0.4f)
        }
    }

    /** Un vagón de carbón en la vía del apartadero, a la distancia [t]; [izq]: cuánto de su ancho queda a la izquierda del eje de la vía. Devuelve su caja. */
    private fun DrawScope.vagon(m: Momento, p: PincelVapor, az: Azar, i: Int, t: Float, izq: Float): Rect {
        run {
            val s = 1f - t; val c = huye(344f, t); val w = 44f * s; val h = 17f * s; val lejos = 0.3f * t
            val caja = Rect(c.x - w * izq, c.y - h - 3.6f * s, c.x + w * (1f - izq), c.y - 3.6f * s)
            for (dx in listOf(0.1f, 0.78f)) { drawCircle(p.umbra(c, C.HIERRO, lejos), 3.4f * s, Offset(caja.left + w * dx, c.y - 3f * s)); drawCircle(p.cara(c, lerp(C.HIERRO, C.HUESO, 0.3f), 0f, lejos), 3.4f * s, Offset(caja.left + w * dx, c.y - 3f * s), style = Stroke(0.7f * s)) }
            drawRect(p.umbra(c, C.HOLLIN, lejos), Offset(caja.left - 1f * s, caja.bottom - 1f * s), Size(w + 2f * s, 2.4f * s))
            val madera = lerp(C.MADERA, if (i % 2 == 0) C.LADRILLO_OSC else C.HOLLIN, 0.4f)
            drawRect(Brush.verticalGradient(0f to p.cara(c, lerp(madera, C.HUESO, 0.1f), 0f, lejos), 1f to p.umbra(c, madera, lejos), startY = caja.top, endY = caja.bottom + 6f), caja.topLeft, caja.size)
            for (k in 1..3) drawLine(p.umbra(c, C.HOLLIN, lejos).copy(alpha = 0.4f), Offset(caja.left, caja.top + h * k / 4f), Offset(caja.right, caja.top + h * k / 4f), 0.35f)
            for (k in listOf(0.05f, 0.5f, 0.95f)) drawLine(p.cara(c, C.HIERRO, 0f, lejos), Offset(caja.left + w * k, caja.top), Offset(caja.left + w * k, caja.bottom), (0.9f * s).coerceAtLeast(0.4f))
            // El carbón, colmado, con su nieve si ha nevado.
            val colmo = Path().apply { moveTo(caja.left + 1f, caja.top); cubicTo(caja.left + w * 0.25f, caja.top - h * az.entre(0.3f, 0.5f), caja.left + w * 0.7f, caja.top - h * az.entre(0.3f, 0.5f), caja.right - 1f, caja.top); close() }
            drawPath(colmo, p.umbra(c, C.HOLLIN, lejos)); if (EscenaVapor.nevado(m)) drawPath(colmo, p.arriba(c, C.NIEVE, lejos).copy(alpha = 0.7f))
            drawLine(p.arriba(c, lerp(C.HOLLIN, C.PIZARRA, 0.7f), lejos), Offset(caja.left + w * 0.3f, caja.top - h * 0.24f), Offset(caja.left + w * 0.6f, caja.top - h * 0.28f), 0.5f)
            return caja
        }
    }

    /** La caseta del guardagujas: abajo ladrillo, arriba madera y cristal, y el tejado de pizarra a cuatro aguas. */
    private fun DrawScope.caseta(m: Momento, p: PincelVapor) {
        val l = p.l; val az = Azar(Semilla.de("vapor", "caseta")); val x0 = 180f; val x1 = 220f; val xm = 200f
        val pie = 176f; val piso = 152f; val alero = 132f; val cumbre = 121f
        ladrillo(p, Rect(x0, piso, x1, pie), Path().apply { addRect(Rect(x0, piso, x1, pie)) }, 0f, az, 0.1f)
        drawRect(p.cara(Offset(xm, 142f), lerp(C.HUESO, C.MADERA, 0.25f), 0f, 0.1f), Offset(x0 - 1.4f, alero), Size(x1 - x0 + 2.8f, piso - alero))
        val luz = l.noche > 0.2f
        for (i in 0..4) { val x = x0 + 1.6f + i * 7.8f
            drawRect(if (luz) lerp(C.GAS, Color.White, 0.2f) else lerp(p.umbra(Offset(x, 140f), lerp(C.PIZARRA, C.HOLLIN, 0.3f), 0.1f), l.medio, 0.25f), Offset(x, alero + 3f), Size(6f, 11f))
            drawLine(p.cara(Offset(x, 140f), C.HUESO, 0f, 0.1f), Offset(x + 3f, alero + 3f), Offset(x + 3f, alero + 14f), 0.4f); drawLine(p.cara(Offset(x, 140f), C.HUESO, 0f, 0.1f), Offset(x, alero + 8.4f), Offset(x + 6f, alero + 8.4f), 0.4f)
            if (EscenaVapor.luto(m)) drawRect(p.umbra(Offset(x, 137f), C.HOLLIN, 0.1f).copy(alpha = 0.75f), Offset(x, alero + 3f), Size(6f, 6f)) }
        if (luz) resplandor(Offset(xm, 141f), 34f, 0.5f * l.noche, C.GAS)
        drawRect(p.umbra(Offset(xm, 151f), C.MADERA, 0.1f), Offset(x0 - 1.4f, piso - 2f), Size(x1 - x0 + 2.8f, 2f))
        val tejado = poligono(Offset(x0 - 4f, alero), Offset(x0 + 6f, cumbre), Offset(x1 - 6f, cumbre), Offset(x1 + 4f, alero))
        drawPath(tejado, p.arriba(Offset(xm, 126f), if (EscenaVapor.nevado(m)) lerp(C.PIZARRA, C.NIEVE, 0.8f) else C.PIZARRA, 0.1f))
        clipPath(tejado) { for (y in listOf(124f, 127f, 130f)) drawLine(p.umbra(Offset(xm, y), C.HOLLIN, 0.1f).copy(alpha = 0.3f), Offset(x0 - 4f, y), Offset(x1 + 4f, y), 0.35f) }
        drawRect(p.umbra(Offset(xm, 133f), C.HOLLIN, 0.1f).copy(alpha = 0.5f), Offset(x0 - 1.4f, alero), Size(x1 - x0 + 2.8f, 1.6f))
        drawRect(p.cara(Offset(212f, 118f), lerp(C.LADRILLO_OSC, C.HOLLIN, 0.3f), 0f, 0.1f), Offset(210f, 114f), Size(4f, 8f)); for (x in listOf(x0 + 6f, x1 - 6f)) drawLine(p.cara(Offset(x, 118f), C.HIERRO), Offset(x, cumbre), Offset(x, cumbre - 4.4f), 0.6f)
        // La puerta y la escalera de fuera.
        drawRect(p.cara(Offset(x1 - 4f, 144f), lerp(C.VERDE, C.HOLLIN, 0.4f), 0f, 0.1f), Offset(x1 - 6.4f, alero + 4f), Size(4.4f, 14f))
        for (i in 0..5) drawLine(p.cara(Offset(x1 + 6f, 160f), lerp(C.MADERA, C.HOLLIN, 0.3f), 0f, 0.1f), Offset(x1 + 1f + i * 1.8f, piso + i * 3.4f), Offset(x1 + 5f + i * 1.8f, piso + i * 3.4f), 0.8f)
        drawLine(p.cara(Offset(x1 + 8f, 160f), lerp(C.MADERA, C.HOLLIN, 0.3f), 0f, 0.1f), Offset(x1 + 5f, piso - 6f), Offset(x1 + 15f, piso + 14f), 0.6f)
    }

    private fun DrawScope.telegrafo(m: Momento, p: PincelVapor) {
        val postes = listOf(0.62f, 0.47f, 0.33f, 0.2f).map { t -> huye(470f, t) to 62f * (1f - t) }
        for ((pie, alto) in postes) {
            val lejos = 0.34f * (1f - alto / 62f)
            drawLine(p.cara(pie, lerp(C.MADERA, C.HOLLIN, 0.4f), 0f, lejos), pie, pie + Offset(0f, -alto), (alto * 0.03f).coerceAtLeast(0.5f))
            for (k in listOf(0.94f, 0.86f)) drawLine(p.cara(pie, lerp(C.MADERA, C.HOLLIN, 0.4f), 0f, lejos), pie + Offset(-alto * 0.09f, -alto * k), pie + Offset(alto * 0.09f, -alto * k), (alto * 0.02f).coerceAtLeast(0.4f))
        }
        for (i in 0 until postes.size - 1) { val (a, ha) = postes[i]; val (b, hb) = postes[i + 1]
            for (k in listOf(0.94f, 0.86f)) for (s in listOf(-1f, 1f)) cable(a + Offset(s * ha * 0.08f, -ha * k), b + Offset(s * hb * 0.08f, -hb * k), 2f + ha * 0.04f, p.l.tono(C.HOLLIN, 0.3f).copy(alpha = 0.6f), 0.35f) }
        // El vendaval tumbó un poste: los hilos del último, rotos, cuelgan.
        if (EscenaVapor.vendaval(m)) { val (a, ha) = postes.last(); cable(a + Offset(0f, -ha * 0.94f), a + Offset(18f, -2f), 6f, p.l.tono(C.HOLLIN, 0.3f), 0.4f) }
    }

    /** La garita de la brigada de vía: tablas embreadas, tejado de cartón piedra, su tubo de estufa y, al lado, unos carriles apilados. */
    private fun DrawScope.garita(m: Momento, p: PincelVapor) {
        val g = GARITA; val brea = lerp(C.HOLLIN, C.MADERA, 0.35f); val c = g.center
        drawRect(Brush.verticalGradient(0f to p.cara(Offset(c.x, g.top), brea), 1f to p.umbra(Offset(c.x, g.bottom), brea), startY = g.top, endY = g.bottom + 8f), g.topLeft, g.size)
        var x = g.left + 3.6f; while (x < g.right) { drawLine(p.umbra(Offset(x, c.y), C.HOLLIN).copy(alpha = 0.5f), Offset(x, g.top), Offset(x, g.bottom), 0.4f); x += 3.6f }
        drawRect(p.cara(c, lerp(C.VERDE, C.HOLLIN, 0.5f)), Offset(g.left + 4f, g.top + 6f), Size(8f, g.height - 6f)); drawCircle(p.cara(c, C.LATON), 0.6f, Offset(g.left + 10.6f, g.top + 15f))
        val vent = Rect(g.left + 17f, g.top + 6f, g.left + 26f, g.top + 13f)
        drawRect(if (p.l.noche > 0.2f && EscenaVapor.turno(m).not() && m.hora < 23.0) lerp(C.GAS, C.HOLLIN, 0.3f) else p.umbra(c, lerp(C.PIZARRA, C.HOLLIN, 0.4f)), vent.topLeft, vent.size); drawRect(p.cara(c, C.HUESO), vent.topLeft, vent.size, style = Stroke(0.6f)); drawLine(p.cara(c, C.HUESO), Offset(vent.center.x, vent.top), Offset(vent.center.x, vent.bottom), 0.4f)
        val techo = poligono(Offset(g.left - 3f, g.top + 1f), Offset(g.left + 1f, g.top - 5f), Offset(g.right + 4f, g.top - 5f), Offset(g.right + 3f, g.top + 1f))
        drawPath(techo, p.arriba(Offset(c.x, g.top - 2f), if (EscenaVapor.nevado(m)) C.NIEVE else lerp(C.HOLLIN, C.PIZARRA, 0.4f))); drawRect(p.umbra(c, C.HOLLIN).copy(alpha = 0.5f), Offset(g.left, g.top + 1f), Size(g.width, 1.6f))
        drawLine(p.cara(Offset(g.right - 5f, g.top - 8f), C.HIERRO), Offset(g.right - 5f, g.top - 4f), Offset(g.right - 5f, g.top - 14f), 1.6f); drawLine(p.cara(Offset(g.right - 5f, g.top - 14f), C.HIERRO), Offset(g.right - 7.4f, g.top - 14f), Offset(g.right - 2.6f, g.top - 14f), 1f)
        // Una pala y un pico apoyados, y los carriles apilados.
        drawLine(p.cara(c, C.MADERA), Offset(g.left - 2f, g.bottom), Offset(g.left + 1.6f, g.top + 6f), 0.8f); drawLine(p.cara(c, C.MADERA), Offset(g.left - 5f, g.bottom), Offset(g.left - 0.4f, g.top + 9f), 0.8f); drawLine(p.umbra(c, C.HIERRO), Offset(g.left - 3f, g.top + 8.6f), Offset(g.left + 2.4f, g.top + 9.6f), 1f, StrokeCap.Round)

    }

    private fun DrawScope.farolDelPoste(m: Momento, p: PincelVapor) {
        val f = EscenaVapor.FAROL_POSTE; val pie = Offset(f.x, 229f)
        drawLine(p.umbra(pie, C.HIERRO), pie, f + Offset(0f, 4f), 1.6f); drawRect(p.cara(pie, C.HIERRO), pie + Offset(-2f, -5f), Size(4f, 5f)); drawLine(p.cara(f, C.HIERRO), f + Offset(-4f, 8f), f + Offset(4f, 8f), 0.7f)
        val encendida = p.l.noche > 0.2f
        drawPath(poligono(f + Offset(-3.4f, -4f), f + Offset(3.4f, -4f), f + Offset(2.2f, 4f), f + Offset(-2.2f, 4f)), if (encendida) lerp(C.GAS, Color.White, 0.4f) else p.cara(f, lerp(C.HUESO, C.PIZARRA, 0.5f)))
        drawPath(poligono(f + Offset(-4f, -4f), f + Offset(0f, -7.6f), f + Offset(4f, -4f)), p.umbra(f, C.HIERRO))
        if (encendida) { resplandor(f, 22f, 0.8f * p.l.noche, C.GAS); resplandor(f, 58f, 0.3f * p.l.noche, C.GAS) }
    }

    /** Los estandartes de los mineros, el día de la gala: grandes, con flecos y su emblema pintado, sin letras. */
    private fun DrawScope.estandartes(m: Momento, p: PincelVapor) {
        for ((x, c) in listOf(232f to C.ROJO, 262f to C.AZUL_PANA)) {
            val y0 = 128f; val y1 = 158f
            for (dx in listOf(0f, 22f)) drawLine(p.cara(Offset(x + dx, 160f), C.MADERA), Offset(x + dx, 176f), Offset(x + dx, y0 - 4f), 0.9f)
            drawRect(p.cara(Offset(x + 11f, 150f), c), Offset(x, y0), Size(22f, y1 - y0)); drawRect(p.cara(Offset(x + 11f, 150f), C.LATON), Offset(x + 1.4f, y0 + 1.4f), Size(19.2f, y1 - y0 - 2.8f), style = Stroke(0.7f))
            drawOval(p.cara(Offset(x + 11f, 150f), C.HUESO), Offset(x + 5f, y0 + 6f), Size(12f, 13f)); drawPath(poligono(Offset(x + 8f, y0 + 15f), Offset(x + 11f, y0 + 9f), Offset(x + 14f, y0 + 15f)), p.cara(Offset(x + 11f, 150f), lerp(c, C.HOLLIN, 0.3f)))
            for (i in 0..7) drawLine(p.cara(Offset(x + 11f, y1), C.LATON), Offset(x + 1.4f + i * 2.8f, y1), Offset(x + 1.4f + i * 2.8f, y1 + 2.4f), 0.6f)
            drawLine(p.cara(Offset(x + 11f, y0), C.MADERA), Offset(x - 1f, y0), Offset(x + 23f, y0), 0.8f)
        }
    }

    /** El vagón de carbón descarrilado, delante de la máquina, en su misma vía: ladeado, con el carbón derramado. */
    private fun DrawScope.vagonDescarrilado(m: Momento, p: PincelVapor) {
        val c = Offset(360f, RIEL + 1f)
        mancha(c + Offset(-16f, 3f), 20f, 5f, p.umbra(c, C.HOLLIN), 0.95f)
        for (dx in listOf(-13f, 10f)) { drawCircle(p.umbra(c, C.HIERRO), 5f, c + Offset(dx, -2f + dx * 0.24f)); drawCircle(p.cara(c, lerp(C.HIERRO, C.HUESO, 0.3f)), 5f, c + Offset(dx, -2f + dx * 0.24f), style = Stroke(1f)) }
        val caja = poligono(c + Offset(-22f, -5f), c + Offset(-27f, -23f), c + Offset(16f, -32f), c + Offset(22f, -9f))
        drawPath(caja, Brush.verticalGradient(0f to p.cara(c, lerp(C.MADERA, C.LADRILLO_OSC, 0.4f)), 1f to p.umbra(c, lerp(C.MADERA, C.LADRILLO_OSC, 0.4f)), startY = c.y - 32f, endY = c.y))
        for (k in 1..3) drawLine(p.umbra(c, C.HOLLIN).copy(alpha = 0.5f), c + Offset(-23f - k * 1.2f, -5f - k * 4.4f), c + Offset(21f - k * 1.4f, -9f - k * 5.6f), 0.5f)
        for (k in listOf(0.06f, 0.5f, 0.94f)) drawLine(p.cara(c, C.HIERRO), c + Offset(-22f + 44f * k, -5f - 4f * k), c + Offset(-27f + 43f * k, -23f - 9f * k), 1f)
        drawPath(poligono(c + Offset(-27f, -23f), c + Offset(-14f, -32f), c + Offset(4f, -34f), c + Offset(16f, -32f)), p.umbra(c, C.HOLLIN))
    }

    // ---------------------------------------------------------------- El primer plano

    private fun cosasDelPatio(m: Momento, p: PincelVapor): List<Cosa> = buildList {
        add(arca(m, p)); add(bidones(m, p)); add(brasero(m, p)); add(monton(m, p)); add(palanca(m, p)); add(senal(m, p)); add(traviesas(m, p))
        if (EscenaVapor.limpieza(m)) add(cubo(m, p))
    }

    /** El arca de las herramientas, con el objeto del día encima; su chaqueta y su lata, cuando está de turno. */
    private fun arca(m: Momento, p: PincelVapor): Cosa {
        val a = ARCA; val f = 7f                                                 // el fondo de la tapa
        val forma = poligono(listOf(Offset(a.left, a.bottom), Offset(a.left, a.top), Offset(a.left + 5f, a.top - f), Offset(a.right + 5f, a.top - f), Offset(a.right + 5f, a.bottom - f), Offset(a.right, a.bottom)))
        return Cosa(forma, Offset(a.center.x, a.bottom)) {
            val madera = lerp(C.MADERA, C.VERDE, 0.28f)
            // Los atizadores del fogonero, apoyados en el arca.
            for ((x, alto) in listOf(11f to 56f, 15f to 48f)) { drawLine(p.umbra(Offset(x, 250f), C.HIERRO), Offset(x - 5f, a.bottom + 2f), Offset(x + 5f, a.bottom - alto), 1.1f); drawCircle(p.umbra(Offset(x, 240f), C.HIERRO), 2f, Offset(x + 5.6f, a.bottom - alto - 1.8f), style = Stroke(0.9f)) }
            drawPath(poligono(Offset(a.right, a.bottom), Offset(a.right, a.top), Offset(a.right + 5f, a.top - f), Offset(a.right + 5f, a.bottom - f)), p.cara(Offset(a.right + 2f, 264f), madera, 1f))
            drawRect(Brush.verticalGradient(0f to p.cara(Offset(a.center.x, a.top), madera), 1f to p.umbra(Offset(a.center.x, a.bottom), madera), startY = a.top, endY = a.bottom + 10f), a.topLeft, a.size)
            drawPath(poligono(Offset(a.left, a.top), Offset(a.left + 5f, a.top - f), Offset(a.right + 5f, a.top - f), Offset(a.right, a.top)), p.arriba(Offset(a.center.x, a.top - 3f), if (EscenaVapor.nevado(m)) lerp(madera, C.NIEVE, 0.7f) else lerp(madera, C.HUESO, 0.16f)))
            drawLine(p.umbra(Offset(a.center.x, a.top), C.HOLLIN).copy(alpha = 0.5f), Offset(a.left, a.top + 0.6f), Offset(a.right, a.top + 0.6f), 0.8f)
            // Las tablas, los herrajes y los desconchones de la pintura.
            for (y in listOf(a.top + 10f, a.top + 20f)) drawLine(p.umbra(Offset(a.center.x, y), C.HOLLIN).copy(alpha = 0.4f), Offset(a.left, y), Offset(a.right, y), 0.45f)
            for (x in listOf(a.left + 8f, a.right - 8f)) { drawRect(p.cara(Offset(x, 264f), C.HIERRO), Offset(x - 1.6f, a.top), Size(3.2f, a.height)); for (y in listOf(a.top + 3f, a.top + 14f, a.top + 26f)) drawCircle(p.arriba(Offset(x, y), lerp(C.HIERRO, C.HUESO, 0.4f)), 0.6f, Offset(x, y)) }
            val az = Azar(Semilla.de("vapor", "arca")); repeat(9) { val c = Offset(az.entre(a.left + 12f, a.right - 12f), az.entre(a.top + 3f, a.bottom - 2f)); mancha(c, az.entre(2f, 5f), az.entre(0.8f, 1.8f), p.cara(c, C.MADERA), 0.7f) }
            // Las asas de cuerda y la cerradura de latón.
            for (x in listOf(a.left + 16f, a.right - 22f)) drawPath(Path().apply { moveTo(x, a.top + 10f); cubicTo(x + 1f, a.top + 15f, x + 5f, a.top + 15f, x + 6f, a.top + 10f) }, p.cara(Offset(x, a.top + 12f), lerp(C.HUESO, C.MADERA, 0.4f)), style = Stroke(0.9f, cap = StrokeCap.Round))
            val cer = Offset(a.center.x, a.top + 6f)
            drawRect(p.cara(cer, C.LATON), cer + Offset(-2.6f, -3f), Size(5.2f, 6.4f)); drawCircle(p.umbra(cer, C.HOLLIN), 0.8f, cer)
            if (EscenaVapor.deBaja(m)) {
                // De baja: el arca, con candado.
                drawRoundRect(p.cara(cer, C.HIERRO), cer + Offset(-3.4f, 2f), Size(6.8f, 6f), CornerRadius(1f)); drawArc(p.cara(cer, lerp(C.HIERRO, C.HUESO, 0.5f)), 180f, 180f, false, cer + Offset(-2.2f, -1.6f), Size(4.4f, 6.4f), style = Stroke(1.1f))
            }
            if (EscenaVapor.susCosas(m)) {
                // Su chaqueta de pana (la de otro, ocre, cuando él es maquinista) colgada de la esquina, y la lata del té.
                val tela = if (EscenaVapor.maquinista(m)) lerp(C.MADERA, C.LATON, 0.3f) else C.AZUL_PANA
                val e = Offset(a.right + 1f, a.top - f)
                val ch = poligono(listOf(e + Offset(-8f, -0.6f), e + Offset(6f, 0.6f), e + Offset(8f, 22f), e + Offset(2f, 25f), e + Offset(-3f, 20f), e + Offset(-9f, 24f)))
                drawPath(ch, p.cara(e + Offset(0f, 12f), tela, 1f)); drawLine(p.umbra(e, tela), e + Offset(-1f, 2f), e + Offset(-2f, 21f), 0.6f); drawLine(p.arriba(e, lerp(tela, C.HUESO, 0.3f)), e + Offset(-8f, -0.4f), e + Offset(6f, 0.8f), 0.9f)
                for (y in listOf(7f, 13f)) drawCircle(p.cara(e, C.LATON), 0.6f, e + Offset(0.6f, y))
                if (!EscenaVapor.maquinista(m)) drawPath(poligono(e + Offset(-6f, 0.4f), e + Offset(-1f, 7f), e + Offset(-3f, 0.6f)), p.cara(e, C.ROJO))
                if (m.ficha.objeto != "lata") { val t = Offset(a.left + 13f, a.top - 3f); drawRoundRect(p.cara(t, lerp(C.HIERRO, C.HUESO, 0.5f)), t + Offset(-3.4f, -9f), Size(6.8f, 9f), CornerRadius(1f)); drawRect(p.arriba(t, C.HUESO), t + Offset(-3.4f, -9f), Size(6.8f, 1.1f)); drawArc(p.umbra(t, C.HIERRO), 200f, 140f, false, t + Offset(-3.2f, -13f), Size(6.4f, 8f), style = Stroke(0.6f)) }
            }
            if (EscenaVapor.maquinista(m) && m.ficha.objeto != "estopa") {
                // Maquinista: la aceitera de pico largo, que es lo suyo ahora.
                val t = Offset(a.right - 6f, a.top - 3f)
                drawPath(poligono(t + Offset(-4f, 0f), t + Offset(-2.6f, -7f), t + Offset(2.6f, -7f), t + Offset(4f, 0f)), p.cara(t, C.COBRE)); drawLine(p.cara(t, C.COBRE), t + Offset(2f, -6f), t + Offset(-12f, -14f), 0.9f); drawArc(p.umbra(t, C.HIERRO), 270f, 180f, false, t + Offset(2f, -6.4f), Size(4.4f, 5f), style = Stroke(0.6f))
            }
            with(ObjetosVapor) { objetoDelDia(m, p, SOBRE, KO, ARCA) }
        }
    }

    private fun bidones(m: Momento, p: PincelVapor): Cosa {
        val pie = Offset(106f, 275f)
        val forma = Path().apply { addRect(Rect(97f, 242f, 117f, 275f)) }
        return Cosa(forma, pie) {
            val hierro = lerp(C.HIERRO, C.COBRE, 0.3f)
            val cuerpo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(97f, 244f, 117f, 275f), CornerRadius(2f))) }
            drawPath(cuerpo, Brush.horizontalGradient(0f to p.cara(Offset(97f, 262f), hierro, -1f), 0.5f to p.cara(Offset(107f, 262f), hierro), 1f to p.cara(Offset(117f, 262f), hierro, 1f), startX = 97f, endX = 117f))
            for (y in listOf(253f, 266f)) { drawLine(p.umbra(Offset(106f, y), C.HOLLIN).copy(alpha = 0.5f), Offset(97f, y), Offset(117f, y), 0.9f); drawLine(p.arriba(Offset(106f, y), lerp(hierro, C.HUESO, 0.3f)).copy(alpha = 0.5f), Offset(97f, y - 0.9f), Offset(117f, y - 0.9f), 0.4f) }
            drawOval(p.arriba(Offset(106f, 244f), lerp(C.HIERRO, C.HUESO, 0.3f)), Offset(97f, 241.6f), Size(20f, 5f)); drawOval(p.umbra(Offset(106f, 244f), C.HOLLIN), Offset(102f, 242.8f), Size(4.4f, 1.8f))
            mancha(Offset(104f, 266f), 4f, 8f, p.umbra(Offset(104f, 266f), C.HOLLIN), 0.45f)
            // La alcuza del aceite y el embudo.
            val t = Offset(128f, 276f)
            drawPath(poligono(t + Offset(-4.4f, 0f), t + Offset(-3f, -10f), t + Offset(3f, -10f), t + Offset(4.4f, 0f)), p.cara(t, lerp(C.HIERRO, C.HUESO, 0.3f))); drawLine(p.cara(t, lerp(C.HIERRO, C.HUESO, 0.3f)), t + Offset(2.6f, -9f), t + Offset(10f, -15f), 1f); drawArc(p.umbra(t, C.HIERRO), 90f, 180f, false, t + Offset(-7f, -8f), Size(5f, 6f), style = Stroke(0.7f))
            drawRect(p.arriba(t, lerp(C.HIERRO, Color.White, 0.5f)), t + Offset(-0.6f, -9f), Size(1f, 8f))
        }
    }

    /** El brasero: un cesto de hierro sobre tres patas, donde arde carbón las madrugadas frías. */
    private fun brasero(m: Momento, p: PincelVapor): Cosa {
        val b = EscenaVapor.BRASERO; val s = 1.3f
        fun o(x: Float, y: Float) = b + Offset(x * s, y * s)
        val forma = poligono(listOf(o(-11f, 0f), o(-10f, -29f), o(10f, -29f), o(11f, 0f)))
        return Cosa(forma, b) {
            val arde = EscenaVapor.brasero(m)
            for (dx in listOf(-8f, 0f, 8f)) drawLine(p.umbra(b, C.HIERRO), o(dx * 0.7f, -13f), o(dx * 1.25f, 0f), 1.6f)
            val cesto = poligono(o(-8.4f, -13f), o(-10.4f, -29f), o(10.4f, -29f), o(8.4f, -13f))
            drawPath(cesto, if (arde) lerp(C.FUEGO, C.HOLLIN, 0.45f) else p.umbra(b, C.HOLLIN))
            clipPath(cesto) { if (arde) for (i in 0..14) drawCircle(lerp(C.FUEGO, Color(0xFFFFE0A0), 0.15f + 0.1f * (i % 4)), 2.5f, o(-7.4f + (i % 5) * 3.7f, -15.4f - (i / 5) * 4.4f))
                else for (i in 0..9) drawCircle(p.cara(b, lerp(C.HOLLIN, C.HIERRO, 0.4f)), 2.3f, o(-7f + (i % 5) * 3.5f, -15.4f - (i / 5) * 4f)) }
            for (dx in listOf(-7f, -3.5f, 0f, 3.5f, 7f)) drawLine(p.cara(b, C.HIERRO), o(dx * 1.14f, -13f), o(dx * 1.42f, -29f), 1.2f)
            for ((y, w) in listOf(-13f to 8.6f, -21f to 9.6f, -29f to 10.6f)) { drawLine(p.cara(b, lerp(C.HIERRO, C.COBRE, 0.3f)), o(-w, y), o(w, y), 1.6f); drawLine(p.arriba(b, lerp(C.HIERRO, C.HUESO, 0.4f)).copy(alpha = 0.6f), o(-w, y - 0.5f), o(w * 0.2f, y - 0.5f), 0.4f) }
            drawOval(p.umbra(b, C.HOLLIN).copy(alpha = 0.5f), o(-12f, -2f), Size(24f * s, 4f * s))
            if (arde) resplandor(o(0f, -21f), 36f, 0.5f + 0.3f * p.l.noche, C.FUEGO)
        }
    }

    /** El montón de carbón y la carretilla. */
    private fun monton(m: Momento, p: PincelVapor): Cosa {
        val az = Azar(Semilla.de("vapor", "monton"))
        val forma = Path().apply { moveTo(202f, 273f); cubicTo(212f, 254f, 228f, 246f, 242f, 254f); cubicTo(252f, 259f, 260f, 267f, 264f, 273f); close() }
        return Cosa(Path().apply { addPath(forma); addRect(Rect(256f, 252f, 296f, 275f)) }, Offset(246f, 275f)) {
            drawPath(forma, p.umbra(Offset(232f, 262f), C.HOLLIN))
            clipPath(forma) { repeat(54) { val c = Offset(az.entre(204f, 262f), az.entre(248f, 273f)); val s = az.entre(0.8f, 1.5f); drawPath(poligono(c, c + Offset(3f * s, -2.2f * s), c + Offset(5f * s, 0.7f * s)), p.arriba(c, lerp(C.HOLLIN, C.PIZARRA, 0.5f + 0.3f * az.real())).copy(alpha = 0.85f)) }
                if (EscenaVapor.nevado(m)) drawRect(p.arriba(Offset(232f, 252f), C.NIEVE).copy(alpha = 0.7f), Offset(202f, 244f), Size(66f, 10f)) }
            // La carretilla, de madera, con su rueda de hierro y su carga.
            val madera = lerp(C.MADERA, C.HOLLIN, 0.25f)
            drawLine(p.umbra(Offset(280f, 268f), madera), Offset(282f, 268f), Offset(285f, 275f), 1.4f)
            drawPath(poligono(Offset(262f, 252f), Offset(270f, 245f), Offset(284f, 246f), Offset(290f, 252f)), p.umbra(Offset(276f, 250f), C.HOLLIN))
            val caja = poligono(Offset(264f, 267f), Offset(258f, 252f), Offset(294f, 252f), Offset(288f, 267f))
            drawPath(caja, Brush.verticalGradient(0f to p.cara(Offset(276f, 254f), madera), 1f to p.umbra(Offset(276f, 266f), madera), startY = 252f, endY = 270f)); drawLine(p.arriba(Offset(276f, 252f), lerp(C.MADERA, C.HUESO, 0.3f)), Offset(258f, 252f), Offset(294f, 252f), 1f)
            for (x in listOf(270f, 282f)) drawLine(p.umbra(Offset(x, 260f), C.HOLLIN).copy(alpha = 0.4f), Offset(x - 1f, 252.6f), Offset(x, 267f), 0.4f)
            drawLine(p.cara(Offset(290f, 262f), C.MADERA), Offset(284f, 265f), Offset(306f, 257f), 1.4f, StrokeCap.Round)
            drawCircle(p.umbra(Offset(262f, 270f), C.HIERRO), 5f, Offset(262f, 270f), style = Stroke(1.5f)); for (k in 0..3) { val an = k * 0.785f; drawLine(p.cara(Offset(262f, 270f), C.HIERRO), Offset(262f, 270f) - Offset(kotlin.math.cos(an), kotlin.math.sin(an)) * 4.4f, Offset(262f, 270f) + Offset(kotlin.math.cos(an), kotlin.math.sin(an)) * 4.4f, 0.5f) }
        }
    }

    /** La palanca de las agujas, con su contrapeso pintado de blanco. */
    private fun palanca(m: Momento, p: PincelVapor): Cosa {
        val pie = Offset(304f, 252f)
        val forma = poligono(listOf(pie + Offset(-5f, 0f), pie + Offset(-3f, -4f), pie + Offset(8f, -17f), pie + Offset(10f, -15f), pie + Offset(5f, 0f)))
        return Cosa(forma, pie) {
            drawRect(p.cara(pie, C.HIERRO), pie + Offset(-6f, -3f), Size(12f, 3f))
            drawLine(p.cara(pie, C.HIERRO), pie + Offset(0f, -2f), pie + Offset(9f, -17f), 1.4f, StrokeCap.Round); drawCircle(p.cara(pie + Offset(9f, -17f), C.HUESO), 1.6f, pie + Offset(9f, -17f))
            drawLine(p.umbra(pie, C.HIERRO), pie + Offset(0f, -2f), pie + Offset(-8f, -5f), 1.2f); drawCircle(p.cara(pie + Offset(-9f, -5f), C.HUESO, -1f), 3f, pie + Offset(-9f, -5.4f)); drawArc(p.cara(pie, C.ROJO), 0f, 180f, true, pie + Offset(-12f, -8.4f), Size(6f, 6f))
        }
    }

    /** Si el brazo [i] de la señal (0 el de arriba) da vía libre: cambia con las horas. */
    internal fun viaLibre(m: Momento, i: Int): Boolean = EscenaVapor.turno(m) && m.azar("senal", i, m.hora.toInt()).entero(5) < (if (i == 0) 3 else 2)

    /**
     * La señal de brazos: el poste blanco, que cruza el cuadro de abajo arriba, con sus dos brazos, sus faroles y su escalera.
     * **[Validado]** (Wikipedia, «Railway semaphore signal», leída el 14 nov 2026): en los semáforos ingleses de cuadrante
     * inferior el brazo en horizontal es alto, y caído hasta unos 60°, vía libre; los brazos de parada eran rojos.
     * **[Aproximación]**: de noche la vía libre se da aquí con cristal verde; no se pudo comprobar con qué color se daba
     * en una línea del noreste en 1880. **El cuento dice lo contrario** («arriba vía libre, abajo peligro»): se dibujó
     * como era; si manda el cuento, basta cambiar el signo de `ang`.
     */
    private fun senal(m: Momento, p: PincelVapor): Cosa {
        val pie = SENAL; val cima = Offset(pie.x, SENAL_CIMA)
        val forma = poligono(listOf(pie + Offset(-2.6f, 0f), cima + Offset(-1.4f, 0f), cima + Offset(1.4f, 0f), pie + Offset(2.6f, 0f)))
        return Cosa(forma, pie) {
            val l = p.l
            fun en(y: Float) = Offset(pie.x, y)
            // La escalera, por detrás, y el poste: negro abajo, blanco arriba, con su remate.
            for (dx in listOf(5f, 9f)) drawLine(p.umbra(en(150f), C.HIERRO), Offset(pie.x + dx + 3f, pie.y), Offset(pie.x + dx - 2.4f, 44f), 0.5f)
            var y = pie.y - 5f; var k = 0; while (y > 46f) { val t = (pie.y - y) / (pie.y - 44f); drawLine(p.umbra(en(y), C.HIERRO), Offset(pie.x + 8f - 5.4f * t, y), Offset(pie.x + 12f - 5.4f * t, y), 0.4f); y -= 6f; k++ }
            drawPath(forma, Brush.horizontalGradient(0f to p.cara(en(140f), C.HUESO, -1f), 0.5f to p.cara(en(140f), C.HUESO), 1f to p.cara(en(140f), C.HUESO, 1f), startX = pie.x - 2.6f, endX = pie.x + 2.6f))
            clipPath(forma) { drawRect(p.umbra(en(250f), C.HOLLIN), Offset(pie.x - 4f, pie.y - 22f), Size(8f, 22f)); for (yy in listOf(120f, 180f, 86f)) mancha(en(yy), 3f, 8f, p.umbra(en(yy), C.CARBONILLA), 0.3f) }
            drawPath(poligono(cima + Offset(-2.4f, 0f), cima + Offset(0f, -9f), cima + Offset(2.4f, 0f)), p.cara(cima, C.ROJO)); drawCircle(p.cara(cima, C.HUESO), 1.6f, cima + Offset(0f, -1f))
            // El balcón del farolero.
            drawRect(p.cara(en(48f), lerp(C.MADERA, C.HOLLIN, 0.3f)), Offset(pie.x - 3f, 46f), Size(14f, 1.4f)); drawLine(p.umbra(en(44f), C.HIERRO), Offset(pie.x + 11f, 46f), Offset(pie.x + 11f, 38f), 0.5f); drawLine(p.umbra(en(44f), C.HIERRO), Offset(pie.x + 1f, 38f), Offset(pie.x + 11f, 38f), 0.5f)
            // Los dos brazos: en horizontal, alto; caídos, vía libre. Con su cristal rojo y su cristal verde y, detrás, el farol.
            for ((i, yb) in listOf(36f, 62f).withIndex()) {
                val libre = viaLibre(m, i); val eje = Offset(pie.x, yb)
                val ang = if (libre) 0.86f else 0f
                val d = Offset(-kotlin.math.cos(ang), kotlin.math.sin(ang)); val n = Offset(d.y, -d.x)
                fun pt(a: Float, b: Float) = eje + d * a + n * b
                val brazo = poligono(pt(-2f, -2.6f), pt(30f, -3.4f), pt(30f, 3.4f), pt(-2f, 2.6f))
                drawPath(brazo, p.cara(eje + Offset(-14f, 0f), C.ROJO, -1f)); drawPath(poligono(pt(21f, -3.2f), pt(25f, -3.3f), pt(25f, 3.3f), pt(21f, 3.2f)), p.cara(eje + Offset(-22f, 0f), C.HUESO, -1f))
                drawPath(brazo, p.umbra(eje, C.HOLLIN).copy(alpha = 0.4f), style = Stroke(0.5f))
                // Los cristales, del otro lado del eje, y el farol.
                val farol = eje + Offset(4.6f, 0f)
                drawRect(p.umbra(farol, C.HOLLIN), farol + Offset(-2f, -2.6f), Size(4f, 5.2f))
                val color = if (libre) C.VERDE_SENAL else C.ROJO_SENAL
                val enc = l.noche > 0.2f
                drawCircle(if (enc) color else p.cara(farol, lerp(color, C.HOLLIN, 0.45f)), 1.7f, farol + Offset(-0.4f, 0f))
                if (enc) { resplandor(farol, 13f, 0.9f * l.noche, color); drawCircle(Color.White.copy(alpha = 0.8f), 0.6f, farol + Offset(-0.4f, 0f)) }
                drawCircle(p.cara(eje, C.HIERRO), 1.4f, eje)
            }
            // El contrapeso y el alambre, al pie.
            drawLine(p.umbra(pie, C.HIERRO), pie + Offset(-10f, -12f), pie + Offset(6f, -8f), 1f); drawRect(p.cara(pie, C.HIERRO), pie + Offset(-13f, -15f), Size(5f, 5f)); drawLine(p.umbra(pie, C.HOLLIN), pie + Offset(5f, -8f), Offset(pie.x + 1.6f, 66f), 0.3f)
        }
    }

    /** Las traviesas apiladas, donde se sube el gato. */
    private fun traviesas(m: Momento, p: PincelVapor): Cosa {
        val forma = Path().apply { addRect(TRAVIESAS) }
        return Cosa(forma, Offset(TRAVIESAS.center.x, TRAVIESAS.bottom)) {
            for (fila in 0..3) { val y = TRAVIESAS.bottom - 7f * (fila + 1)
                if (fila % 2 == 0) for (i in 0..4) { val x = TRAVIESAS.left + i * 8f; drawRect(p.cara(Offset(x, y), lerp(C.MADERA, C.HOLLIN, 0.45f + 0.08f * ((i + fila) % 3))), Offset(x, y), Size(7.2f, 6.6f)); drawRect(p.arriba(Offset(x, y), lerp(C.MADERA, C.HUESO, 0.14f)), Offset(x, y), Size(7.2f, 0.9f)); drawCircle(p.umbra(Offset(x, y), C.HOLLIN).copy(alpha = 0.5f), 1.6f, Offset(x + 3.6f, y + 3.4f), style = Stroke(0.4f)) }
                else { drawRect(p.cara(Offset(336f, y), lerp(C.MADERA, C.HOLLIN, 0.5f)), Offset(TRAVIESAS.left - 2f, y), Size(TRAVIESAS.width + 2f, 6.6f)); drawRect(p.arriba(Offset(336f, y), lerp(C.MADERA, C.HUESO, 0.14f)), Offset(TRAVIESAS.left - 2f, y), Size(TRAVIESAS.width + 2f, 0.9f)); for (k in 0..3) drawLine(p.umbra(Offset(336f, y), C.HOLLIN).copy(alpha = 0.3f), Offset(TRAVIESAS.left + 4f + k * 9f, y + 3.2f), Offset(TRAVIESAS.left + 10f + k * 9f, y + 3.4f), 0.3f) } }
            if (EscenaVapor.nevado(m)) drawRect(p.arriba(Offset(336f, TRAVIESAS.top), C.NIEVE), Offset(TRAVIESAS.left, TRAVIESAS.top - 1.6f), Size(TRAVIESAS.width, 2.4f))
        }
    }

    /** El cubo y la estopa de la limpieza. */
    private fun cubo(m: Momento, p: PincelVapor): Cosa {
        val pie = Offset(150f, 252f)
        val forma = poligono(pie + Offset(-4f, 0f), pie + Offset(-5f, -8f), pie + Offset(5f, -8f), pie + Offset(4f, 0f))
        return Cosa(forma, pie) {
            drawPath(forma, Brush.horizontalGradient(0f to p.cara(pie, lerp(C.HIERRO, C.HUESO, 0.4f), -1f), 1f to p.cara(pie, lerp(C.HIERRO, C.HUESO, 0.4f), 1f), startX = pie.x - 5f, endX = pie.x + 5f)); drawOval(p.umbra(pie, C.HOLLIN), pie + Offset(-5f, -9f), Size(10f, 2.2f))
            drawArc(p.umbra(pie, C.HIERRO), 180f, 180f, false, pie + Offset(-5f, -13f), Size(10f, 9f), style = Stroke(0.5f)); mancha(pie + Offset(9f, -1f), 5f, 2f, p.cara(pie, C.HUESO), 0.9f)
        }
    }

    /** Los adornos de los días señalados, de la cochera a la torre del agua y de ahí a la señal. */
    private fun DrawScope.adornos(m: Momento, p: PincelVapor) {
        if (!(EscenaVapor.excursion(m) || EscenaVapor.fiesta(m))) return
        val colores = if (EscenaVapor.excursion(m)) listOf(C.ROJO, C.HUESO, C.AZUL_PANA, C.LATON) else listOf(C.HUESO, C.ROJO, C.VERDE)
        banderines(p.l, Offset(30f, 82f), Offset(151f, 106f), 12f, colores, 5f)
        banderines(p.l, Offset(151f, 106f), Offset(SENAL.x, 74f), 24f, colores, 5f)
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.depositoVivo(m: Momento, l: Luces, r: Rayo, reloj: Float) {
        val viento = EscenaVapor.soplo(m)
        with(HumoVapor) {
            // El humo que sale por el lucernario de la cochera (mucho, cuando encienden las máquinas), el de la caseta y el de la garita.
            val enciende = EscenaVapor.fuego(m) == EscenaVapor.Fuego.ENCENDIENDO
            bocanadas(l, r, Offset(58f, 71f), if (enciende) 50f else 28f, if (enciende) 34f else 16f, reloj * 0.8f, viento, if (enciende) 0.8f else 0.6f, 21, if (enciende) 8 else 5, fuerza = if (enciende) 0.9f else 0.5f, ritmo = 0.05f)
            hilo(Offset(212f, 114f), 14f, viento, reloj, l.tono(Color(0xFFCFC8BC), 0.1f), 22)
            if (EscenaVapor.brasero(m) || l.noche > 0.3f) hilo(Offset(GARITA.right - 5f, GARITA.top - 14f), 13f, viento, reloj, l.tono(Color(0xFFCFC8BC)), 24)
            if (EscenaVapor.enElDeposito(m)) with(MaquinaVapor) { maquinaViva(m, l, r, reloj) }
            if (EscenaVapor.brasero(m)) { val b = EscenaVapor.BRASERO; llama(b + Offset(0f, -35f), 14f, reloj, 3); brasas(l, b + Offset(0f, -26f), 18f, reloj); bocanadas(l, r, b + Offset(0f, -48f), 26f, 11f, reloj, viento, 0.45f, 23, 4, fuerza = 0.5f, ritmo = 0.09f, tinte = C.FUEGO, cuanto = 0.4f * l.noche) }
        }
        // Los faroles de gas tiemblan un poco.
        if (l.noche > 0.2f) for (f in listOf(EscenaVapor.FAROLA, EscenaVapor.FAROL_POSTE)) resplandor(f, 12f, 0.24f * l.noche * (0.7f + 0.3f * sin(reloj * 7f + f.x)), C.GAS)
    }

    // ---------------------------------------------------------------- El gato

    fun estorbos(m: Momento): List<Float> = listOf(EscenaVapor.BRASERO.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 16f, EscenaVapor.PISO, hasta = 310f))
        add(Ancla(TipoAncla.MIRADOR, TRAVIESAS.center.x - 2f, TRAVIESAS.top))                       // encima de las traviesas apiladas
        add(Ancla(TipoAncla.OBJETO, SOBRE.x, EscenaVapor.PISO))
        add(Ancla(TipoAncla.ABRIGO, 136f, EscenaVapor.PISO))                                        // al socaire del arca y los bidones
        if (EscenaVapor.brasero(m)) add(Ancla(TipoAncla.FUEGO, EscenaVapor.BRASERO.x + 36f, EscenaVapor.PISO, hasta = EscenaVapor.BRASERO.x))
        if (EscenaVapor.excursion(m) || EscenaVapor.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 244f, 150f))
    }
}
