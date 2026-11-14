package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaPirata.Bomba
import io.github.ruddy2003.vestigium.ui.escenas.EscenaPirata.Vela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano del cuadro de a bordo: la cubierta del «Fortuna», vista de una banda a la otra. De atrás adelante:
 * los obenques, la amurada (roja por dentro, gastada) con su regala y su cabillero, la tablazón, y las cosas de a
 * bordo, cada una con su silueta para que arroje su sombra: la chalupa volcada, el fogón, el cañón trincado, la bomba,
 * el palo, la escotilla con el objeto del día y el tonel del agua. Por arriba, la botavara y la vela mayor.
 */
internal object CubiertaPirata {
    private val C = ColorPirata

    // La amurada: lo alto de la regala, dónde empieza su cara y dónde toca la cubierta.
    const val CAP = 200f
    const val CARA = 204.5f
    const val BASE = 236f

    /** El palo, que cruza el cuadro de abajo arriba. */
    const val PALO = 252f
    const val PIE_PALO = 262f

    /** La botavara: de la boca, en el palo, hacia popa, que queda a la izquierda. */
    private val BOCA = Offset(244f, 86f)
    private val PENOL = Offset(-14f, 64f)

    val FOGON = Offset(130f, 277f)
    private const val KF = 1.55f
    private val CANON = Offset(210f, 252f)
    private const val KC = 1.3f
    private const val KB = 1.35f
    private val BOMBA = Offset(233f, 266f)
    private val TONEL = Offset(374f, 268f)
    private val FAROL = Offset(98f, 83f)
    const val RON_X = 196f

    // La escotilla y dónde va el objeto del día.
    private const val EX0 = 268f
    private const val EX1 = 356f
    private const val EYT = 246.4f
    private const val EYF = 258.6f
    private const val EY1 = 279f
    val SOBRE = Offset(312f, 253.6f)
    private const val KO = 1.7f

    private fun botavaraEn(x: Float): Float = PENOL.y + (BOCA.y - PENOL.y) * ((x - PENOL.x) / (BOCA.x - PENOL.x))

    // ---------------------------------------------------------------- La cubierta, entera

    fun DrawScope.cubierta(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("pirata", "cubierta"))
        val mojada = EscenaPirata.baldeando(m) || m.ficha.clima == Clima.LLUVIA || EscenaPirata.tormenta(m)
        obenques(l, r)
        pedrero(m, l, r)
        if (EscenaPirata.tormenta(m)) rociones(l)
        amurada(m, l, r, az)
        tablazon(l, r, az, mojada)
        val cosas = cosasDeCubierta(m, l, r).sortedBy { it.pie.y }
        // Las sombras sobre la amurada y la cubierta: la de la regala, la de las adujas del cabillero y la de cada cosa.
        capaSombra(r) { c ->
            drawRect(c, Offset(0f, CARA), Size(ANCHO, (3f * r.tan * 0.5f).coerceIn(0.8f, 4.4f)))
            translate(r.ax * 1.4f, 1.2f + 0.5f * r.tan) { clipRect(0f, CARA, ANCHO, BASE) { adujasDelCabillero { p, w, h -> drawOval(c, Offset(p.x - w / 2f, p.y), Size(w, h)) } } }
            cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, BASE, CAP) }
        }
        cabillero(l, r)
        colgaduras(m, l)
        velaMayor(m, l, r)
        cosas.forEach { it.pintar(this) }
        farol(m, l)
        if (EscenaPirata.fiesta(m)) empavesado(m, l)
        if (EscenaPirata.tormenta(m)) andariveles(l)
    }

    // ---------------------------------------------------------------- La amurada y la tablazón

    /** La amurada, por dentro: tres tracas pintadas de almagre, los barraganetes, las portas cerradas, el trancanil con sus imbornales y, encima, la regala. */
    private fun DrawScope.amurada(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val rojo = l.tono(C.ROJO); val rojoLuz = l.tono(lerp(C.ROJO, Color(0xFFE8A070), 0.3f)); val rojoSombra = l.tono(mult(C.ROJO, r.color, 0.6f))
        drawRect(Brush.verticalGradient(listOf(rojo, rojoLuz, rojo), CARA, BASE), Offset(0f, CARA), Size(ANCHO, BASE - CARA))
        clipRect(0f, CARA, ANCHO, BASE) {
            // La pintura comida: donde asoma la madera, el salitre y las chorreras de los pernos.
            repeat(44) { val p = Offset(az.entre(0f, ANCHO), az.entre(CARA, BASE)); mancha(p, az.entre(2f, 9f), az.entre(0.8f, 2.6f), l.tono(if (it % 3 == 0) C.MADERA else lerp(C.ROJO, Color(0xFF5A2A20), 0.5f)), az.entre(0.25f, 0.6f)) }
            repeat(16) { val x = az.entre(0f, ANCHO); val y = az.entre(CARA + 2f, BASE - 12f); drawLine(l.tono(Color(0xFF4A2A1C)).copy(alpha = 0.3f), Offset(x, y), Offset(x + az.entre(-0.6f, 0.6f), y + az.entre(5f, 14f)), az.entre(0.5f, 1.1f)) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFFE8DCC8)).copy(alpha = 0.3f)), BASE - 9f, BASE), Offset(0f, BASE - 9f), Size(ANCHO, 9f))
            // Las costuras de las tracas.
            for (y in listOf(215f, 225.4f)) { drawLine(rojoSombra.copy(alpha = 0.9f), Offset(0f, y), Offset(ANCHO, y), 0.7f); drawLine(rojoLuz.copy(alpha = 0.7f), Offset(0f, y + 0.8f), Offset(ANCHO, y + 0.8f), 0.4f) }
        }
        // Los barraganetes: los pies que sostienen la regala, cada uno con su canto de luz y de sombra.
        var x = 12f
        while (x < ANCHO) {
            drawRect(l.tono(lerp(C.ROJO, C.MADERA, 0.25f)), Offset(x, CARA), Size(5.4f, BASE - CARA - 2f))
            drawRect(if (r.lat >= 0f) rojoLuz else rojoSombra, Offset(x + 4f, CARA), Size(1.4f, BASE - CARA - 2f)); drawRect(if (r.lat >= 0f) rojoSombra else rojoLuz, Offset(x, CARA), Size(1.2f, BASE - CARA - 2f))
            x += 38f
        }
        // Las portas de los cañones, cerradas: su marco, las dos bisagras de hierro y la argolla.
        for (px in listOf(26f, 166f)) {
            val porta = Rect(px, 210f, px + 30f, 231f)
            drawRect(l.tono(lerp(C.ROJO, Color(0xFF7A3024), 0.45f)), porta.topLeft, porta.size)
            drawRect(rojoSombra, porta.topLeft, porta.size, style = Stroke(0.9f)); drawLine(rojoLuz, porta.bottomLeft + Offset(0.6f, -0.6f), porta.bottomRight + Offset(-0.6f, -0.6f), 0.5f)
            for (bx in listOf(px + 7f, px + 23f)) drawRect(l.tono(C.HIERRO), Offset(bx - 1f, 208.6f), Size(2f, 9f))
            drawCircle(l.tono(C.HIERRO), 2f, porta.center + Offset(0f, 3f), style = Stroke(0.9f))
        }
        // Los cáncamos de las trincas.
        for (cx in listOf(140f, 262f, 338f)) { drawCircle(l.tono(C.HIERRO), 1.7f, Offset(cx, 229f), style = Stroke(0.9f)); drawCircle(l.tono(Color(0xFF7A3A22)).copy(alpha = 0.5f), 0.9f, Offset(cx, 232f)) }
        // El trancanil y los imbornales, con su chorrera.
        drawRect(Brush.verticalGradient(listOf(l.tono(C.MADERA_OSC), l.tono(lerp(C.MADERA_OSC, C.MADERA, 0.4f))), BASE - 3.4f, BASE), Offset(0f, BASE - 3.4f), Size(ANCHO, 3.4f))
        for (ix in listOf(18f, 144f, 236f, 330f)) drawRoundRect(l.tono(Color(0xFF1A1512)), Offset(ix, BASE - 2.6f), Size(7f, 2f), CornerRadius(1f))
        // La regala: un tablón corrido, gastado donde apoyan los codos.
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(C.MADERA, Color.White, 0.3f)), l.tono(C.MADERA), l.tono(C.MADERA_OSC)), CAP, CARA), Offset(0f, CAP), Size(ANCHO, CARA - CAP))
        repeat(18) { val xx = az.entre(0f, ANCHO); drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.4f), Offset(xx, CAP + 1.4f), Offset(xx + az.entre(6f, 22f), CAP + 1.4f + az.entre(-0.4f, 0.8f)), 0.4f) }
        for (xx in listOf(76f, 190f, 304f)) drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.7f), Offset(xx, CAP), Offset(xx + 1.2f, CARA), 0.5f)      // las juntas del tablón
    }

    /** La tablazón: tablas de popa a proa, lavadas con arena, con sus costuras de brea, sus topes y sus clavijas. */
    private fun DrawScope.tablazon(l: Luces, r: Rayo, az: Azar, mojada: Boolean) {
        val lejos = l.tono(lerp(C.CUBIERTA, Color(0xFFA89472), 0.45f)); val cerca = l.tono(lerp(C.CUBIERTA, Color(0xFFF0E2C0), 0.3f))
        drawRect(Brush.verticalGradient(listOf(lejos, l.tono(C.CUBIERTA), cerca), BASE, ALTO), Offset(0f, BASE), Size(ANCHO, ALTO - BASE))
        val costuras = listOf(BASE, 241.2f, 247.4f, 255f, 264.6f, 276.6f, 292f)
        val brea = l.tono(C.BREA)
        clipRect(0f, BASE, ANCHO, ALTO) {
            for (i in 0 until costuras.size - 1) {
                val y0 = costuras[i]; val y1 = costuras[i + 1]; val k = (i + 1) / 6f
                // Cada tabla, de un tono, con su veta.
                var x = az.entre(-60f, 0f)
                while (x < ANCHO) {
                    val largo = az.entre(70f, 150f)
                    drawRect(l.tono(if (az.real() > 0.5f) Color(0xFF9C8660) else Color(0xFFF2E6C8)).copy(alpha = az.entre(0.05f, 0.2f)), Offset(x, y0), Size(largo, y1 - y0))
                    repeat(3) { val yy = az.entre(y0 + 0.6f, y1 - 0.6f); val xx = az.entre(x, x + largo * 0.7f); drawLine(l.tono(Color(0xFF8E7858)).copy(alpha = 0.25f), Offset(xx, yy), Offset(xx + az.entre(14f, 44f), yy + az.entre(-0.3f, 0.3f)), 0.3f + 0.3f * k) }
                    x += largo
                    // El tope entre dos tablas y sus clavijas.
                    drawLine(brea.copy(alpha = 0.6f), Offset(x, y0), Offset(x, y1), 0.4f + 0.4f * k)
                    for (s in listOf(-2.2f, 2.2f)) { drawCircle(l.tono(C.MADERA_OSC).copy(alpha = 0.6f), 0.35f + 0.3f * k, Offset(x + s * (0.6f + k), y0 + (y1 - y0) * 0.3f)); drawCircle(l.tono(C.MADERA_OSC).copy(alpha = 0.6f), 0.35f + 0.3f * k, Offset(x + s * (0.6f + k), y0 + (y1 - y0) * 0.72f)) }
                }
                drawLine(brea.copy(alpha = 0.55f + 0.2f * k), Offset(0f, y0), Offset(ANCHO, y0), 0.45f + 0.55f * k)
                drawLine(cerca.copy(alpha = 0.5f), Offset(0f, y0 + 0.5f + 0.5f * k), Offset(ANCHO, y0 + 0.5f + 0.5f * k), 0.3f)
            }
            // Lo que el uso le deja: el paso gastado, manchas de brea y de grasa, y la ceniza junto al fogón.
            repeat(12) { mancha(Offset(az.entre(0f, ANCHO), az.entre(BASE + 3f, ALTO)), az.entre(8f, 30f), az.entre(1.4f, 4f), l.tono(if (it % 3 == 0) Color(0xFF6A5A48) else Color(0xFFF4EAD0)), 0.2f) }
            repeat(7) { val p = Offset(az.entre(10f, ANCHO), az.entre(BASE + 4f, ALTO - 3f)); drawOval(brea.copy(alpha = 0.45f), p, Size(az.entre(1.6f, 4.6f), az.entre(0.7f, 1.6f))) }
            repeat(5) { mancha(Offset(FOGON.x + az.entre(-30f, 34f), FOGON.y + az.entre(-2f, 6f)), az.entre(5f, 12f), az.entre(1.2f, 2.4f), l.tono(Color(0xFF8E8880)), 0.4f) }
            // Bajo cada imbornal, la humedad.
            for (ix in listOf(18f, 144f, 236f, 330f)) mancha(Offset(ix + 3.5f, BASE + 2.4f), 9f, 2.4f, l.tono(Color(0xFF5E5448)), 0.4f)
            if (mojada) {
                // Recién baldeada, o con lluvia: el agua corre hacia los imbornales y devuelve el cielo.
                val cieloEnElAgua = lerp(l.medio, l.alto, 0.4f)
                repeat(14) { val p = Offset(az.entre(0f, ANCHO), az.entre(BASE + 3f, ALTO - 2f)); val k = (p.y - BASE) / (ALTO - BASE); mancha(p, az.entre(16f, 46f) * (0.5f + k), az.entre(1.2f, 3.6f) * (0.5f + k), cieloEnElAgua, 0.34f) }
                repeat(22) { val p = Offset(az.entre(0f, ANCHO), az.entre(BASE + 3f, ALTO - 2f)); drawLine(Color.White.copy(alpha = 0.3f * (1f - 0.6f * l.noche)), p, p + Offset(az.entre(3f, 12f), 0f), 0.5f, StrokeCap.Round) }
            }
        }
    }

    // ---------------------------------------------------------------- La jarcia

    /** Dónde muere cada obenque en la mesa de guarnición, por fuera de la regala. */
    private val OBENQUES = listOf(268f, 282f, 296f, 310f)
    private val TOPE = Offset(253f, -16f)
    private fun obenqueEn(i: Int, y: Float): Float = TOPE.x + (OBENQUES[i] - TOPE.x) * ((y - TOPE.y) / (CAP - 13f - TOPE.y))

    /** Los obenques de la banda de enfrente, con sus flechastes, sus vigotas y sus acolladores: suben de la regala al tope del palo. */
    private fun DrawScope.obenques(l: Luces, r: Rayo) {
        val brea = l.tono(C.BREA); val filo = l.tono(lerp(C.BREA, C.CABO, 0.6f))
        // Los flechastes: los peldaños de cabo, que cuelgan un poco.
        var y = 18f
        while (y < CAP - 22f) {
            val a = Offset(obenqueEn(0, y), y); val b = Offset(obenqueEn(3, y), y)
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + (b.x - a.x) * 0.3f, a.y + 0.9f, a.x + (b.x - a.x) * 0.7f, a.y + 0.9f, b.x, b.y) }, brea.copy(alpha = 0.85f), style = Stroke(0.55f))
            y += 9.4f
        }
        OBENQUES.forEachIndexed { i, x ->
            val pie = Offset(x, CAP - 13f)
            drawLine(brea, TOPE, pie, 1.4f, StrokeCap.Round)
            drawLine(filo.copy(alpha = 0.55f), TOPE + Offset(r.lat * 0.5f, 0f), pie + Offset(r.lat * 0.5f, 0f), 0.4f)
            // Las dos vigotas y, entre ellas, el acollador de tres vueltas.
            for (d in listOf(-0.9f, 0f, 0.9f)) drawLine(l.tono(C.CABO).copy(alpha = 0.9f), pie + Offset(d, 1.6f), Offset(x + d, CAP - 3.4f), 0.4f)
            for (vy in listOf(CAP - 12.4f, CAP - 2.6f)) { drawCircle(l.tono(C.MADERA_OSC), 2.3f, Offset(x, vy)); drawCircle(filo.copy(alpha = 0.7f), 2.3f, Offset(x, vy), style = Stroke(0.4f)); for (d in listOf(-0.8f, 0f, 0.8f)) drawCircle(l.tono(Color(0xFF1A1512)), 0.35f, Offset(x + d, vy + if (d == 0f) 0.7f else -0.3f)) }
        }
        // La jareta: el listón que mantiene los obenques en su sitio.
        drawLine(l.tono(C.MADERA_OSC), Offset(obenqueEn(0, CAP - 17f) - 2f, CAP - 17f), Offset(obenqueEn(3, CAP - 17f) + 2f, CAP - 17f), 1.1f, StrokeCap.Round)
    }

    private inline fun adujasDelCabillero(pinta: (Offset, Float, Float) -> Unit) {
        pinta(Offset(273f, 211f), 9f, 17f); pinta(Offset(287f, 211f), 11f, 22f); pinta(Offset(301f, 211f), 8f, 14f); pinta(Offset(315f, 211f), 10f, 19f)
    }

    /** El cabillero: el tablón con sus cabillas, donde se amarra cada cabo y se cuelga lo que sobra, hecho aduja. */
    private fun DrawScope.cabillero(l: Luces, r: Rayo) {
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(C.MADERA, Color.White, 0.2f)), l.tono(C.MADERA_OSC)), 206.4f, 210f), Offset(264f, 206.4f), Size(60f, 3.6f))
        val cabo = l.tono(C.CABO); val caboLuz = l.tono(lerp(C.CABO, Color.White, 0.35f)); val caboSombra = l.tono(mult(C.CABO, r.color, 0.8f))
        var n = 0
        adujasDelCabillero { p, w, h ->
            // La cabilla, y el cabo que baja de arriba a tomar vuelta en ella.
            drawLine(l.tono(lerp(C.MADERA, Color.White, 0.25f)), Offset(p.x, 201.4f), Offset(p.x, 213.4f), 1.7f, StrokeCap.Round)
            drawLine(caboSombra, Offset(p.x + 0.6f, 150f - n * 8f), Offset(p.x + 0.4f, 206f), 0.6f)
            // La aduja colgada: tres o cuatro vueltas, y la que la abraza por arriba.
            for (i in 0..3) { val k = i / 3f; drawOval(if (i % 2 == 0) cabo else caboSombra, Offset(p.x - w / 2f + k * 1.2f, p.y + k * 1.4f), Size(w - k * 2.4f, h - k * 2.6f), style = Stroke(1.5f)) }
            drawOval(caboLuz.copy(alpha = 0.8f), Offset(p.x - w / 2f + (if (r.lat >= 0f) 0.6f else -0.6f), p.y), Size(w, h), style = Stroke(0.4f))
            drawLine(cabo, Offset(p.x - 1.6f, p.y + 0.6f), Offset(p.x + 1.6f, p.y + 3.4f), 1.4f, StrokeCap.Round)
            n++
        }
    }

    /** El pedrero: el cañoncito de horquilla, montado en la regala. El día del abordaje apunta a la presa. */
    private fun DrawScope.pedrero(m: Momento, l: Luces, r: Rayo) {
        val pie = Offset(346f, CAP)
        val hierro = l.tono(C.HIERRO); val luz = l.tono(lerp(C.HIERRO, Color(0xFFE8E2D0), 0.4f)); val bronce = l.tono(C.BRONCE)
        drawLine(hierro, pie, pie + Offset(0f, -9f), 2f, StrokeCap.Round)
        drawPath(Path().apply { moveTo(pie.x - 3.4f, pie.y - 14f); cubicTo(pie.x - 3.4f, pie.y - 8.4f, pie.x + 3.4f, pie.y - 8.4f, pie.x + 3.4f, pie.y - 14f) }, hierro, style = Stroke(1.6f, cap = StrokeCap.Round))
        val giro = if (EscenaPirata.abordaje(m)) 168f else -12f
        rotate(giro, pie + Offset(0f, -13.6f)) {
            val c = pie + Offset(0f, -13.6f)
            val tubo = Path().apply { moveTo(c.x - 11f, c.y - 3.8f); lineTo(c.x - 8f, c.y - 4.4f); lineTo(c.x + 16f, c.y - 2.8f); lineTo(c.x + 17.4f, c.y - 3.8f); lineTo(c.x + 19.4f, c.y - 3.8f); lineTo(c.x + 19.4f, c.y + 3.8f); lineTo(c.x + 17.4f, c.y + 3.8f); lineTo(c.x + 16f, c.y + 2.8f); lineTo(c.x - 8f, c.y + 4.4f); lineTo(c.x - 11f, c.y + 3.8f); close() }
            drawPath(tubo, Brush.verticalGradient(listOf(luz, hierro, l.tono(mult(C.HIERRO, r.color, 0.9f))), c.y - 4.4f, c.y + 4.4f))
            for (x in listOf(-7f, 3f, 13f)) drawLine(l.tono(mult(C.HIERRO, r.color, 0.95f)), Offset(c.x + x, c.y - 4.2f), Offset(c.x + x, c.y + 4.2f), 1f)
            drawCircle(bronce, 1.4f, c)
            // La rabiza, por donde se apunta.
            drawPath(Path().apply { moveTo(c.x - 11f, c.y); cubicTo(c.x - 15f, c.y + 0.4f, c.x - 18f, c.y + 3f, c.x - 21f, c.y + 6.4f) }, hierro, style = Stroke(1.4f, cap = StrokeCap.Round))
            drawCircle(l.tono(C.MADERA), 1.7f, Offset(c.x - 21.4f, c.y + 6.8f))
        }
    }

    /** La tormenta echa rociones por encima de la borda. */
    private fun DrawScope.rociones(l: Luces) {
        val az = Azar(Semilla.de("pirata", "rociones")); val blanco = l.tono(Color.White)
        for (cx in listOf(64f, 204f, 334f)) {
            val alto = az.entre(18f, 34f)
            // La pluma de agua que salta, en jirones, y las gotas que se lleva el viento hacia popa.
            repeat(9) { val x = cx + az.entre(-16f, 16f); val h = alto * az.entre(0.4f, 1f) * (1f - abs(x - cx) / 22f).coerceAtLeast(0.2f)
                drawPath(Path().apply { moveTo(x - 2.4f, CAP); cubicTo(x - 3f, CAP - h * 0.5f, x - 5f, CAP - h * 0.9f, x - 7f - h * 0.2f, CAP - h); cubicTo(x - 2f, CAP - h * 0.8f, x + 1.4f, CAP - h * 0.4f, x + 2.4f, CAP); close() }, blanco.copy(alpha = az.entre(0.3f, 0.6f))) }
            repeat(30) { val a = az.entre(-2.9f, -0.9f); val d = az.entre(6f, alto * 1.4f); drawCircle(blanco.copy(alpha = az.entre(0.3f, 0.8f)), az.entre(0.4f, 1.3f), Offset(cx + cos(a) * d * 1.3f - 6f, CAP + sin(a) * d)) }
        }
    }

    /** Con mal tiempo se tienden andariveles: dos cabos de popa a proa, para agarrarse. */
    private fun DrawScope.andariveles(l: Luces) {
        val cabo = l.tono(C.CABO)
        for ((a, b) in listOf(Offset(-4f, 229f) to Offset(PALO - 8f, 224f), Offset(PALO + 8f, 224f) to Offset(ANCHO + 4f, 231f)))
            drawPath(Path().apply { val p = cuerda(a, b, 3.4f, 12); moveTo(p[0].x, p[0].y); for (i in 1 until p.size) lineTo(p[i].x, p[i].y) }, cabo, style = Stroke(1.5f, cap = StrokeCap.Round))
    }

    /** Lo que cuelga de la borda según el día: el paño negro del luto. */
    private fun DrawScope.colgaduras(m: Momento, l: Luces) {
        if (EscenaPirata.luto(m)) pano(l, Offset(164f, CAP), Offset(226f, CAP), 30f, C.NEGRO)
    }

    /** El empavesado de las fiestas: banderas de todos los países robados, del palo a la borda. */
    private fun DrawScope.empavesado(m: Momento, l: Luces) {
        val colores = listOf(Color(0xFFB8382E), Color(0xFFE8E2D0), Color(0xFF2E5C88), C.OCRE, C.NEGRO, Color(0xFF3E7A52))
        banderines(l, Offset(PALO - 7f, 118f), Offset(40f, CAP - 1f), 12f, colores, 9f, punta = false)
        banderines(l, Offset(PALO + 7f, 124f), Offset(378f, 168f), 9f, colores.reversed(), 8f, punta = true)
    }

    // ---------------------------------------------------------------- La vela mayor

    /** La vela mayor y su botavara, que cruzan el cuadro por arriba. Cómo va la vela lo dice [EscenaPirata.vela]. */
    private fun DrawScope.velaMayor(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("pirata", "vela"))
        val vela = EscenaPirata.vela(m)
        val lona = l.tono(C.LONA); val lonaSombra = l.tono(mult(C.LONA_OSC, r.color, 0.55f * r.contraste)); val lonaLuz = l.tono(lerp(C.LONA, Color(0xFFFFF6DC), 0.5f))
        val costura = l.tono(lerp(C.LONA_OSC, Color(0xFF6A5A44), 0.4f))
        val pujamen = 4.6f                                                       // cuánto queda la vela por encima de la botavara
        if (vela != Vela.AFERRADA) {
            val enrollada = if (vela == Vela.RIZADA) 10f else 0f
            val forma = Path().apply {
                moveTo(PENOL.x, PENOL.y - pujamen - enrollada)
                if (vela == Vela.CAIDA) {
                    // Sin viento, la relinga cuelga en bolsas entre vuelta y vuelta de la envergadura.
                    var x = PENOL.x
                    while (x < BOCA.x - 2f) { val x1 = minOf(x + 32f, BOCA.x - 2f); val y0 = botavaraEn(x) - pujamen; val y1 = botavaraEn(x1) - pujamen; cubicTo(x + 9f, y0 + 6f, x1 - 9f, y1 + 6f, x1, y1); x = x1 }
                } else lineTo(BOCA.x - 2f, BOCA.y - pujamen - enrollada)
                lineTo(BOCA.x - 2f, -14f); lineTo(PENOL.x, -14f); close()
            }
            // El paño: más claro donde lo atraviesa el sol, en sombra hacia el palo.
            drawPath(forma, Brush.horizontalGradient(if (r.lat >= 0f) listOf(lona, lonaLuz, lona, lonaSombra) else listOf(lonaSombra, lona, lonaLuz, lona), -14f, BOCA.x))
            clipPath(forma) {
                // El sol, detrás de la lona, la enciende: una mancha cálida que se mueve con la hora.
                val sol = Offset(l.solX * ANCHO - 120f, 26f)
                if (r.dia > 0.3f) mancha(sol, 110f, 52f, lerp(l.solColor, Color.White, 0.5f), 0.26f * r.dia * (1f - l.nublado))
                drawRect(Brush.verticalGradient(listOf(lonaSombra.copy(alpha = 0.5f), Color.Transparent), -14f, 40f), Offset(-14f, -14f), Size(280f, 54f))
                // Los paños cosidos, de la relinga de abajo hacia arriba, cada uno de un tono.
                var x = -8f; var n = 0
                while (x < BOCA.x) {
                    val w = az.entre(22f, 27f)
                    val a = Offset(x, botavaraEn(x)); val b = Offset(x + (if (vela == Vela.CAIDA) az.entre(-5f, 5f) else 5f), -14f)
                    drawPath(poligono(a, Offset(a.x + w, botavaraEn(a.x + w)), Offset(b.x + w, -14f), b), (if (n % 2 == 0) lonaSombra else lonaLuz).copy(alpha = az.entre(0.06f, 0.2f)))
                    drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + 2f, a.y - 30f, b.x - 2f, 20f, b.x, b.y) }, costura.copy(alpha = 0.75f), style = Stroke(0.6f))
                    drawPath(Path().apply { moveTo(a.x + 1.2f, a.y); cubicTo(a.x + 3.2f, a.y - 30f, b.x - 0.8f, 20f, b.x + 1.2f, b.y) }, lonaLuz.copy(alpha = 0.5f), style = Stroke(0.4f))
                    x += w; n++
                }
                // Los pliegues: con la vela caída, largos y hondos; con viento, apenas la panza.
                val pliegues = if (vela == Vela.CAIDA) 9 else 4
                repeat(pliegues) {
                    val px = az.entre(0f, BOCA.x - 14f); val largo = az.entre(40f, 86f); val y0 = botavaraEn(px) - 6f
                    drawPath(Path().apply { moveTo(px, y0); cubicTo(px + 5f, y0 - largo * 0.4f, px - 5f, y0 - largo * 0.7f, px + az.entre(-6f, 6f), y0 - largo) }, lonaSombra.copy(alpha = if (vela == Vela.CAIDA) 0.5f else 0.22f), style = Stroke(az.entre(2f, 5f), cap = StrokeCap.Round))
                }
                // La faja de rizos, con sus matafiones colgando de dos en dos.
                for (banda in listOf(22f, 44f)) {
                    if (banda - enrollada < 6f) continue
                    val a = Offset(PENOL.x, PENOL.y - banda + enrollada); val b = Offset(BOCA.x, BOCA.y - banda + enrollada)
                    drawLine(lonaSombra.copy(alpha = 0.6f), a, b, 3.4f); drawLine(costura.copy(alpha = 0.6f), a + Offset(0f, -1.7f), b + Offset(0f, -1.7f), 0.4f); drawLine(costura.copy(alpha = 0.6f), a + Offset(0f, 1.7f), b + Offset(0f, 1.7f), 0.4f)
                    var xx = 2f
                    while (xx < BOCA.x - 6f) { val y = botavaraEn(xx) - banda + enrollada; drawLine(l.tono(C.CABO), Offset(xx, y), Offset(xx - 0.6f, y + az.entre(5f, 7.4f)), 0.8f, StrokeCap.Round); drawCircle(costura, 0.7f, Offset(xx, y)); xx += 12.6f }
                }
                // Los remiendos de Tomás, cada uno con su puntada, y el moho de la lona vieja.
                for ((p, w, h) in listOf(Triple(Offset(64f, 34f), 15f, 11f), Triple(Offset(178f, 50f), 11f, 14f), Triple(Offset(120f, 8f), 18f, 9f))) {
                    drawRect(l.tono(lerp(C.LONA, Color(0xFFCDBE9A), 0.6f)), p, Size(w, h)); drawRect(costura.copy(alpha = 0.8f), p, Size(w, h), style = Stroke(0.5f))
                    var s = 1.2f; while (s < w) { drawLine(costura.copy(alpha = 0.7f), p + Offset(s, 0.4f), p + Offset(s + 0.8f, 1.6f), 0.3f); drawLine(costura.copy(alpha = 0.7f), p + Offset(s, h - 1.6f), p + Offset(s + 0.8f, h - 0.4f), 0.3f); s += 2.2f }
                }
                repeat(26) { mancha(Offset(az.entre(-10f, BOCA.x), az.entre(-10f, 76f)), az.entre(2f, 8f), az.entre(1.4f, 5f), l.tono(if (it % 2 == 0) Color(0xFF9A8E72) else Color(0xFF7E866A)), 0.14f) }
                // Lo que la faja enrollada deja ver cuando se toman rizos.
                if (vela == Vela.RIZADA) repeat(7) { val px = az.entre(10f, BOCA.x - 20f); val y0 = botavaraEn(px) - 14f; drawLine(lonaSombra.copy(alpha = 0.5f), Offset(px, y0), Offset(px + az.entre(14f, 30f), y0 - az.entre(30f, 60f)), az.entre(1f, 2.4f), StrokeCap.Round) }
            }
            // La relinga: el cabo cosido al borde de abajo.
            if (vela != Vela.CAIDA) drawLine(l.tono(C.CABO), Offset(PENOL.x, PENOL.y - pujamen - enrollada), Offset(BOCA.x - 2f, BOCA.y - pujamen - enrollada), 1f)
        }
        botavara(m, l, r, vela, az)
    }

    /** La botavara: el palo tendido sobre la cubierta, con la envergadura que le cose la vela. Con la vela aferrada o rizada, lleva encima el rollo de lona. */
    private fun DrawScope.botavara(m: Momento, l: Luces, r: Rayo, vela: Vela, az: Azar) {
        val madera = Color(0xFFBE9A64)
        val grueso = 7.4f
        val forma = poligono(PENOL + Offset(0f, -grueso / 2f + 0.6f), BOCA + Offset(0f, -grueso / 2f), BOCA + Offset(0f, grueso / 2f), PENOL + Offset(0f, grueso / 2f - 0.6f))
        val lona = l.tono(C.LONA); val lonaSombra = l.tono(mult(C.LONA_OSC, r.color, 0.7f * r.contraste)); val lonaLuz = l.tono(lerp(C.LONA, Color.White, 0.35f))
        if (vela == Vela.AFERRADA) {
            // La vela aferrada: todo el paño recogido en un rollo gordo sobre la botavara, con el pico dentro. Tiene bulto
            // (más en el medio que en los penoles), la luz por arriba, la sombra por debajo y un talle en cada tomador.
            val tomadores = listOf(14f, 50f, 88f, 126f, 164f, 202f, 232f)
            fun gordoEn(x: Float): Float { val t = ((x - PENOL.x) / (BOCA.x - PENOL.x)).coerceIn(0f, 1f); val panza = sin(3.14f * t).coerceAtLeast(0f); val talle = tomadores.minOf { abs(it - x) }.let { if (it < 5f) 2.2f * (1f - it / 5f) else 0f }; return 8f + 10f * kotlin.math.sqrt(panza) - talle }
            val rollo = Path().apply {
                moveTo(PENOL.x, PENOL.y - 2f)
                var x = PENOL.x; while (x <= BOCA.x - 4f) { lineTo(x, botavaraEn(x) - 2f - gordoEn(x) + (if ((x.toInt() / 4) % 3 == 0) 0.5f else 0f)); x += 4f }
                lineTo(BOCA.x - 4f, BOCA.y - 2f); close()
            }
            drawPath(rollo, Brush.verticalGradient(listOf(lonaLuz, lona, lona, lonaSombra), PENOL.y - 20f, BOCA.y - 1f))
            clipPath(rollo) {
                // La panza en sombra, por debajo, y los pliegues del paño, que van de un tomador al siguiente.
                var x = PENOL.x; while (x <= BOCA.x) { val y = botavaraEn(x); drawLine(lonaSombra.copy(alpha = 0.55f), Offset(x, y - 6f), Offset(x, y), 4.4f); x += 4f }
                repeat(46) { val px = az.entre(-12f, BOCA.x - 8f); val g = gordoEn(px); val y0 = botavaraEn(px) - 3f - az.entre(0f, g * 0.8f); val w = az.entre(10f, 26f)
                    drawPath(Path().apply { moveTo(px, y0); cubicTo(px + w * 0.3f, y0 - 2.4f, px + w * 0.7f, y0 + 1.2f, px + w, y0 - 0.6f + 0.085f * w) }, (if (it % 3 == 0) lonaLuz else lonaSombra).copy(alpha = az.entre(0.3f, 0.6f)), style = Stroke(az.entre(0.5f, 1.3f), cap = StrokeCap.Round)) }
                repeat(10) { mancha(Offset(az.entre(-10f, BOCA.x), botavaraEn(az.entre(0f, BOCA.x)) - az.entre(4f, 14f)), az.entre(3f, 8f), az.entre(1.4f, 3f), l.tono(Color(0xFF8E8468)), 0.16f) }
            }
            drawPath(rollo, lonaSombra.copy(alpha = 0.8f), style = Stroke(0.5f, join = StrokeJoin.Round))
            // Los tomadores: cada uno ciñe el rollo con dos vueltas, curvadas por el bulto, y su nudo.
            for (x in tomadores) {
                val arriba = Offset(x, botavaraEn(x) - 2f - gordoEn(x)); val abajo = Offset(x + 1.4f, botavaraEn(x) + grueso / 2f)
                for (d in listOf(0f, 2.4f)) {
                    drawPath(Path().apply { moveTo(arriba.x + d, arriba.y - 0.4f); cubicTo(arriba.x + d + 3.4f, arriba.y + 4f, abajo.x + d + 3.4f, abajo.y - 6f, abajo.x + d, abajo.y) }, l.tono(mult(C.CABO, r.color, 0.6f)), style = Stroke(1.5f, cap = StrokeCap.Round))
                    drawPath(Path().apply { moveTo(arriba.x + d - 0.5f, arriba.y - 0.4f); cubicTo(arriba.x + d + 2.9f, arriba.y + 4f, abajo.x + d + 2.9f, abajo.y - 6f, abajo.x + d - 0.5f, abajo.y) }, l.tono(lerp(C.CABO, Color.White, 0.2f)).copy(alpha = 0.7f), style = Stroke(0.4f, cap = StrokeCap.Round))
                }
                drawCircle(l.tono(C.CABO), 1.2f, arriba + Offset(1.4f, 0.6f)); drawLine(l.tono(C.CABO), arriba + Offset(1.4f, 0.6f), arriba + Offset(-1.6f, -3f), 0.7f, StrokeCap.Round)
            }
            // El penol del pico, que asoma del rollo junto al palo, con su driza.
            drawLine(l.tono(mult(madera, r.color, 0.4f)), Offset(BOCA.x - 26f, BOCA.y - 17f), Offset(BOCA.x - 3f, BOCA.y - 15.4f), 4f, StrokeCap.Round)
            drawLine(l.tono(lerp(madera, Color.White, 0.3f)), Offset(BOCA.x - 26f, BOCA.y - 18.2f), Offset(BOCA.x - 4f, BOCA.y - 16.6f), 0.9f, StrokeCap.Round)
        }
        if (vela == Vela.RIZADA) {
            // La faja de rizos tomada: un rollo delgado de lona sobre la botavara, con sus matafiones.
            val gordo = 8f
            val rollo = Path().apply {
                moveTo(PENOL.x, PENOL.y - gordo * 0.74f); var x = PENOL.x
                while (x < BOCA.x - 6f) { val x1 = minOf(x + 24f, BOCA.x - 6f); cubicTo(x + 6f, botavaraEn(x) - gordo - 1.6f - az.entre(0f, 1.6f), x1 - 6f, botavaraEn(x1) - gordo - 1.6f - az.entre(0f, 1.6f), x1, botavaraEn(x1) - gordo * 0.74f); x = x1 }
                lineTo(BOCA.x - 6f, BOCA.y); lineTo(PENOL.x, PENOL.y + 1f); close()
            }
            drawPath(rollo, Brush.verticalGradient(listOf(lonaLuz, lona, lonaSombra), PENOL.y - gordo - 4f, BOCA.y))
            clipPath(rollo) { repeat(30) { val x = az.entre(-10f, BOCA.x); val y = botavaraEn(x) - az.entre(2f, gordo); drawLine(lonaSombra.copy(alpha = 0.6f), Offset(x, y), Offset(x + az.entre(6f, 20f), y + az.entre(-1.4f, 1.4f)), 0.5f) } }
            var x = 8f
            while (x < BOCA.x - 10f) { val y = botavaraEn(x); drawLine(l.tono(mult(C.CABO, r.color, 0.5f)), Offset(x, y - gordo - 1.4f), Offset(x + 1.4f, y + grueso / 2f), 1.2f, StrokeCap.Round); x += 12.6f }
        }
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(madera, Color(0xFFFFF0CC), 0.4f)), l.tono(madera), l.tono(mult(madera, r.color, 0.85f))), PENOL.y - 4f, BOCA.y + 4f))
        clipPath(forma) { repeat(12) { val x = az.entre(-10f, BOCA.x); val y = botavaraEn(x) + az.entre(-3f, 3f); drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.35f), Offset(x, y), Offset(x + az.entre(14f, 46f), y + 0.08f * az.entre(14f, 46f)), 0.4f) } }
        // La envergadura: el cabo que cose la vela a la botavara, vuelta a vuelta.
        if (vela == Vela.LARGA || vela == Vela.CAIDA) { var x = -6f; while (x < BOCA.x - 8f) { val y = botavaraEn(x); drawLine(l.tono(C.CABO), Offset(x, y - 5.4f), Offset(x + 3f, y + grueso / 2f), 0.9f, StrokeCap.Round); x += 10.6f } }
        // La boca de cangrejo, que abraza el palo, y un motón con su escota que baja a popa.
        drawRoundRect(l.tono(C.MADERA_OSC), Offset(BOCA.x - 4f, BOCA.y - 6.4f), Size(22f, 12.8f), CornerRadius(2.4f))
        drawRoundRect(l.tono(lerp(madera, Color.White, 0.2f)), Offset(BOCA.x - 3f, BOCA.y - 5.4f), Size(20f, 3f), CornerRadius(1.4f))
        val moton = Offset(46f, botavaraEn(46f) + grueso / 2f + 5f)
        drawLine(l.tono(C.HIERRO), Offset(46f, botavaraEn(46f) + grueso / 2f - 1f), moton + Offset(0f, -3f), 1.1f)
        drawOval(l.tono(C.MADERA_OSC), moton + Offset(-3.2f, -3.6f), Size(6.4f, 8.4f)); drawOval(l.tono(lerp(C.MADERA, Color.White, 0.3f)), moton + Offset(-2f, -2.8f), Size(2.4f, 5f))
        for (d in listOf(-1.4f, 1.4f)) drawLine(l.tono(C.CABO), moton + Offset(d, 4f), Offset(-6f + d * 4f, 132f + d * 3f), 0.9f)
    }

    /** El farol, colgado de la botavara: de latón, con sus vidrios de cuerno. */
    private fun DrawScope.farol(m: Momento, l: Luces) {
        val encendido = EscenaPirata.farol(m)
        val c = FAROL; val arriba = Offset(c.x, botavaraEn(c.x) + 3f)
        drawLine(l.tono(C.HIERRO), arriba, c + Offset(0f, 1f), 0.8f)
        if (encendido) { mancha(c + Offset(-6f, -22f), 70f, 20f, LLAMA, 0.2f * l.noche); mancha(Offset(c.x, 266f), 90f, 16f, LLAMA, 0.16f * l.noche); mancha(Offset(c.x, 222f), 70f, 14f, LLAMA, 0.12f * l.noche) }
        val laton = l.tono(C.LATON); val latonSombra = l.tono(lerp(C.LATON, Color(0xFF5A4420), 0.6f))
        drawPath(poligono(c + Offset(-3.6f, 3.4f), c + Offset(3.6f, 3.4f), c + Offset(0f, 0f)), laton)
        drawRect(if (encendido) lerp(LUZ_DE_CASA, Color.White, 0.2f) else l.tono(Color(0xFFCDBE96)), c + Offset(-3.2f, 3.4f), Size(6.4f, 8.6f))
        for (x in listOf(-3.2f, 0f, 3.2f)) drawLine(latonSombra, c + Offset(x, 3.4f), c + Offset(x, 12f), 0.8f)
        drawRect(laton, c + Offset(-4f, 12f), Size(8f, 1.6f)); drawLine(latonSombra, c + Offset(-4f, 3.4f), c + Offset(4f, 3.4f), 0.9f)
    }

    // ---------------------------------------------------------------- Las cosas de a bordo

    private fun cosasDeCubierta(m: Momento, l: Luces, r: Rayo): List<Cosa> = buildList {
        add(chalupaCosa(l, r))
        add(canonCosa(m, l, r))
        add(bombaCosa(m, l, r))
        add(paloCosa(m, l, r))
        add(fogonCosa(m, l, r))
        add(escotillaCosa(m, l, r))
        add(tonelCosa(l, r))
        add(barrileteCosa(l, r, Offset(338f, 281f)))
        add(cuboCosa(m, l, r))
        add(adujaCosa(l, r, Offset(170f, 279f)))
        if (EscenaPirata.baldeando(m)) add(lampazoCosa(l, r))
        if (EscenaPirata.tortugas(m)) { add(tortugaCosa(l, r, Offset(40f, 279f), 1f)); add(tortugaCosa(l, r, Offset(84f, 284f), -1f)) }
        if (EscenaPirata.fiesta(m)) add(ronCosa(l, r))
        if (EscenaPirata.abordaje(m)) add(rezonCosa(l, r))
        if (m.ficha.objeto == "mosquete") add(mosqueteCosa(l, r))
    }

    /** La chalupa, volcada sobre sus calzos y trincada: la quilla al aire, el forro en tingladillo, embreada, con su faja ocre. */
    private fun chalupaCosa(l: Luces, r: Rayo): Cosa {
        val forma = Path().apply { moveTo(-26f, 262f); lineTo(-26f, 229f); cubicTo(0f, 214f, 44f, 211f, 72f, 222f); cubicTo(86f, 229f, 95f, 246f, 97f, 259f); lineTo(93f, 262f); close() }
        return Cosa(forma, Offset(40f, 267f)) {
            // El hueco de debajo, entre calzo y calzo.
            drawRect(Color.Black.copy(alpha = 0.5f), Offset(-10f, 261f), Size(100f, 5.4f))
            for (cx in listOf(6f, 64f)) { drawRect(l.tono(C.MADERA_OSC), Offset(cx, 260.6f), Size(12f, 7f)); drawRect(l.tono(lerp(C.MADERA, Color.White, 0.2f)), Offset(cx, 260.6f), Size(12f, 1.4f)) }
            volumen(l, r, forma, -26f, 97f, lerp(C.BREA, Color(0xFF6A5444), 0.5f))
            clipPath(forma) {
                // De la quilla para abajo, cada traca monta sobre la siguiente.
                for (i in 1..5) {
                    val d = i * 7.4f
                    val traca = Path().apply { moveTo(-26f, 229f + d); cubicTo(0f, 214f + d, 44f, 211f + d, 72f, 222f + d * 0.9f); cubicTo(84f, 228f + d * 0.8f, 92f, 240f + d * 0.5f, 96f, 252f + d * 0.3f) }
                    drawPath(traca, l.tono(Color(0xFF1A1512)).copy(alpha = 0.55f), style = Stroke(0.8f)); translate(0f, 1f) { drawPath(traca, l.tono(Color(0xFFB8A68A)).copy(alpha = 0.3f), style = Stroke(0.5f)) }
                }
                // La faja ocre de la cinta, abajo, y lo que la brea ha chorreado.
                drawRect(l.tono(C.OCRE), Offset(-26f, 255.6f), Size(124f, 4f)); drawRect(l.tono(C.MADERA_OSC), Offset(-26f, 259.6f), Size(124f, 2.6f))
                val az = Azar(Semilla.de("pirata", "chalupa"))
                repeat(12) { mancha(Offset(az.entre(-20f, 90f), az.entre(222f, 254f)), az.entre(3f, 10f), az.entre(2f, 5f), l.tono(if (it % 3 == 0) Color(0xFFCDBE9E) else Color(0xFF14100E)), 0.2f) }
            }
            // La quilla, con su zapata de hierro, y la trinca que la sujeta a la cubierta.
            val quilla = Path().apply { moveTo(-26f, 229f); cubicTo(0f, 214f, 44f, 211f, 72f, 222f); cubicTo(86f, 229f, 95f, 246f, 97f, 259f) }
            drawPath(quilla, l.tono(C.MADERA_OSC), style = Stroke(3f, cap = StrokeCap.Round)); translate(0f, -0.9f) { drawPath(quilla, l.tono(lerp(C.MADERA, Color.White, 0.3f)), style = Stroke(0.8f, cap = StrokeCap.Round)) }
            val cabo = l.tono(C.CABO)
            for (x in listOf(22f, 26f)) drawPath(Path().apply { moveTo(x - 6f, 266f); cubicTo(x - 3f, 240f, x, 220f, x + 3f, 212.6f) }, cabo, style = Stroke(1.2f, cap = StrokeCap.Round))
            drawCircle(l.tono(C.HIERRO), 2f, Offset(17f, 267f), style = Stroke(1f))
        }
    }

    /** Un cañón de a cuatro, trincado a son de mar: de costado, pegado a la amurada, sobre su curena de dos ejes. */
    private fun canonCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = CANON
        fun tuboDe(k: Float): Path { fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
            return Path().apply {
            moveTo(p(-36f, -26.6f)); lineTo(p(-33f, -31.6f)); lineTo(p(34f, -28.6f)); lineTo(p(36f, -30f)); lineTo(p(40f, -30f)); lineTo(p(40f, -20f)); lineTo(p(36f, -20f)); lineTo(p(34f, -21.4f)); lineTo(p(-33f, -18.4f)); lineTo(p(-36f, -23.4f)); close()
        } }
        fun curenaDe(k: Float): Path { fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
            return poligono(listOf(p(24f, -7f), p(24f, -20f), p(4f, -20f), p(4f, -17f), p(-8f, -17f), p(-8f, -14f), p(-19f, -14f), p(-19f, -11f), p(-29f, -11f), p(-29f, -7f))) }
        val forma = Path().apply { addPath(tuboDe(KC)); addPath(curenaDe(KC)); addOval(Rect(pie.x - 23.5f * KC, pie.y - 11.6f * KC, pie.x - 11.5f * KC, pie.y + 0.4f * KC)); addOval(Rect(pie.x + 9f * KC, pie.y - 11.6f * KC, pie.x + 21f * KC, pie.y + 0.4f * KC)) }
        return Cosa(forma, pie) { scale(KC, KC, pie) {
            fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
            val tubo = tuboDe(1f); val curena = curenaDe(1f)
            drawOval(Color.Black.copy(alpha = 0.26f), p(-30f, -3f), Size(60f, 6f))
            val madera = l.tono(lerp(C.ROJO, C.MADERA_OSC, 0.4f)); val luz = l.tono(lerp(C.ROJO, Color(0xFFE8B080), 0.35f)); val sombra = l.tono(mult(lerp(C.ROJO, C.MADERA_OSC, 0.4f), r.color, 0.8f))
            drawPath(curena, Brush.verticalGradient(listOf(luz, madera, sombra), pie.y - 20f, pie.y - 7f))
            drawPath(curena, sombra, style = Stroke(0.6f))
            for (x in listOf(-17.5f, 15f)) { drawCircle(l.tono(C.MADERA_OSC), 6f, p(x, -5.6f)); drawCircle(l.tono(lerp(C.MADERA, Color.White, 0.2f)), 6f, p(x, -5.6f), style = Stroke(0.8f)); drawCircle(l.tono(C.HIERRO), 1.6f, p(x, -5.6f)) }
            // El tubo de hierro: la culata con su cascabel, los refuerzos, el muñón y el brocal, tapado con su taco.
            drawCircle(l.tono(C.HIERRO), 2.8f, p(-39f, -25f))
            drawPath(tubo, Brush.verticalGradient(listOf(l.tono(lerp(C.HIERRO, Color(0xFFE2E4EA), 0.45f)), l.tono(C.HIERRO), l.tono(mult(C.HIERRO, r.color, 0.9f))), pie.y - 32f, pie.y - 18f))
            for (x in listOf(-27f, -10f, 12f, 30f)) drawLine(l.tono(mult(C.HIERRO, r.color, 0.9f)), p(x, -31.4f + (x + 33f) * 0.045f), p(x, -18.6f - (x + 33f) * 0.045f), 1.3f)
            drawCircle(l.tono(lerp(C.HIERRO, Color.White, 0.2f)), 3f, p(8f, -22f)); drawCircle(l.tono(C.BREA), 1.2f, p(8f, -22f))
            drawRect(l.tono(lerp(C.MADERA, Color.White, 0.2f)), p(39.4f, -28.6f), Size(2.4f, 7.2f))
            // Mohos del salitre y la trinca que lo abraza al costado, de cáncamo a cáncamo.
            repeat(6) { mancha(p(-30f + it * 12f, -21f), 4f, 1.6f, l.tono(Color(0xFF8A5A3A)), 0.3f) }
            val cabo = l.tono(C.CABO); val caboSombra = l.tono(mult(C.CABO, r.color, 0.8f))
            for (x in listOf(-22f, 22f)) { drawPath(Path().apply { moveTo(p(x - 3f, -17f)); cubicTo(p(x - 3.6f, -26f), p(x - 2f, -34f), p(x, -34f)); cubicTo(p(x + 2f, -34f), p(x + 3.6f, -26f), p(x + 3f, -17f)) }, cabo, style = Stroke(1.5f, cap = StrokeCap.Round)); drawLine(caboSombra, p(x + 3f, -18f), p(x + (if (x < 0f) -14f else 16f), -17f), 1.1f) }
            // La cuña, bajo la rueda de atrás.
            drawPath(poligono(p(-30f, 0f), p(-22f, 0f), p(-22f, -3f)), l.tono(C.MADERA))
        } }
    }

    /** La bomba de achique, al pie del palo: el tubo de madera con sus zunchos y el guimbalete. Cuando achica, echa el agua por el pico. */
    private fun bombaCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = BOMBA
        val achica = EscenaPirata.achicando(m)
        fun formaDe(k: Float): Path { fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
            val puno = if (achica) p(-27f, -44f) else p(-27f, -22f)
            return Path().apply { addRect(Rect(p(-5f, -31f), p(5f, 0f))); moveTo(p(3f, -31f)); lineTo(p(3f, -38f)); lineTo(p(7f, -38f)); lineTo(p(7f, -31f)); close(); moveTo(puno); lineTo(puno + Offset(1f, -3f)); lineTo(p(8f, -37f)); lineTo(p(8f, -34f)); close() } }
        return Cosa(formaDe(KB), pie) { scale(KB, KB, pie) {
            fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
            val tubo = Path().apply { addRect(Rect(p(-5f, -31f), p(5f, 0f))) }
            val puno = if (achica) p(-27f, -44f) else p(-27f, -22f)
            drawOval(Color.Black.copy(alpha = 0.24f), p(-8f, -2f), Size(17f, 4f))
            if (achica) {
                // El chorro, que cae en la cubierta y corre hacia el imbornal.
                val agua = lerp(l.tono(Color(0xFFBFE0E4)), Color.White, 0.3f)
                mancha(p(-20f, 2f), 22f, 3.4f, lerp(l.medio, l.alto, 0.4f), 0.5f)
                drawPath(Path().apply { moveTo(p(-11f, -19f)); cubicTo(p(-16f, -19f), p(-19f, -12f), p(-20f, 0f)) }, agua.copy(alpha = 0.85f), style = Stroke(2.6f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p(-11f, -19.6f)); cubicTo(p(-15f, -19.6f), p(-18f, -12f), p(-19f, 0f)) }, Color.White.copy(alpha = 0.6f), style = Stroke(0.8f, cap = StrokeCap.Round))
                repeat(5) { drawCircle(Color.White.copy(alpha = 0.6f), 0.7f, p(-24f + it * 2.4f, -1f - (it % 2) * 1.6f)) }
            }
            volumen(l, r, tubo, pie.x - 5f, pie.x + 5f, C.MADERA)
            for (y in listOf(-28f, -15f, -3f)) drawRect(l.tono(C.HIERRO), p(-5.4f, y), Size(10.8f, 1.8f))
            // El pico, el horcón y el guimbalete.
            drawPath(poligono(p(-5f, -22f), p(-12f, -20.6f), p(-12f, -17.6f), p(-5f, -17f)), l.tono(C.MADERA_OSC))
            drawRect(l.tono(C.MADERA_OSC), p(3f, -38f), Size(4f, 8f))
            drawLine(l.tono(lerp(C.MADERA, Color(0xFF7A5A3A), 0.4f)), puno, p(9f, -35.6f), 2.6f, StrokeCap.Round)
            drawLine(l.tono(lerp(C.MADERA, Color.White, 0.3f)), puno + Offset(0f, -0.9f), p(9f, -36.5f), 0.6f, StrokeCap.Round)
            drawCircle(l.tono(C.HIERRO), 1.3f, p(5f, -35.6f))
            drawLine(l.tono(C.HIERRO), p(0f, -34f), p(0f, -31f), 1.2f)
        } }
    }

    /** El palo: un pino entero, con su capa embreada al pie, la cincha de hierro con sus cabillas, la campana y los aros de la vela. */
    private fun paloCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val forma = poligono(Offset(PALO - 6.4f, -16f), Offset(PALO + 6.4f, -16f), Offset(PALO + 7.8f, PIE_PALO), Offset(PALO - 7.8f, PIE_PALO))
        return Cosa(forma, Offset(PALO, PIE_PALO), bajoElFollaje = false) {
            val madera = Color(0xFFC49C62)
            drawOval(Color.Black.copy(alpha = 0.26f), Offset(PALO - 15f, PIE_PALO - 3f), Size(30f, 7f))
            volumen(l, r, forma, PALO - 7.8f, PALO + 7.8f, madera)
            val az = Azar(Semilla.de("pirata", "palo"))
            clipPath(forma) {
                repeat(34) { val x = az.entre(PALO - 7f, PALO + 7f); val y = az.entre(-10f, PIE_PALO - 20f); drawLine(l.tono(C.MADERA_OSC).copy(alpha = az.entre(0.15f, 0.4f)), Offset(x, y), Offset(x + az.entre(-0.8f, 0.8f), y + az.entre(14f, 50f)), az.entre(0.3f, 0.8f)) }
                // El sebo y la grasa por donde suben y bajan los aros.
                drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF5E4630)).copy(alpha = 0.4f), Color.Transparent), -16f, 60f), Offset(PALO - 8f, -16f), Size(16f, 76f))
            }
            // Los aros que llevan el grátil de la vela, por encima de la boca.
            if (EscenaPirata.vela(m) != Vela.AFERRADA) { var y = 66f; while (y > -12f) { drawRoundRect(l.tono(C.MADERA_OSC), Offset(PALO - 8.4f, y), Size(16.8f, 2.4f), CornerRadius(1.2f)); drawLine(l.tono(lerp(C.MADERA, Color.White, 0.35f)), Offset(PALO - 7f, y + 0.5f), Offset(PALO + 4f, y + 0.5f), 0.5f); y -= 17f } }
            else for (i in 0..4) drawRoundRect(l.tono(C.MADERA_OSC), Offset(PALO - 8.4f, 60f - i * 2.9f), Size(16.8f, 2.4f), CornerRadius(1.2f))
            // Las drizas, que bajan pegadas al palo.
            for (d in listOf(-9.6f, 10f)) drawLine(l.tono(C.CABO), Offset(PALO + d * 0.8f, -16f), Offset(PALO + d, 226f), 0.8f)
            // La cincha con sus cabillas y, en cada una, su aduja.
            drawRect(l.tono(C.HIERRO), Offset(PALO - 8.4f, 224f), Size(16.8f, 3.4f)); drawLine(l.tono(lerp(C.HIERRO, Color.White, 0.4f)), Offset(PALO - 8f, 224.6f), Offset(PALO + 6f, 224.6f), 0.5f)
            val cabo = l.tono(C.CABO); val caboSombra = l.tono(mult(C.CABO, r.color, 0.8f))
            for ((d, w, h) in listOf(Triple(-11.4f, 8f, 17f), Triple(11.6f, 9.4f, 21f))) {
                drawLine(l.tono(lerp(C.MADERA, Color.White, 0.25f)), Offset(PALO + d, 220.6f), Offset(PALO + d, 231f), 1.6f, StrokeCap.Round)
                for (i in 0..2) drawOval(if (i % 2 == 0) cabo else caboSombra, Offset(PALO + d - w / 2f + i * 0.6f, 228f + i * 1.2f), Size(w - i * 1.2f, h - i * 2.2f), style = Stroke(1.4f))
            }
            // El día de luto, un crespón negro anudado al palo.
            if (EscenaPirata.luto(m)) {
                drawRect(l.tono(C.NEGRO), Offset(PALO - 8.6f, 150f), Size(17.2f, 5f))
                for (d in listOf(Offset(-9f, 18f), Offset(-4f, 24f))) drawPath(Path().apply { moveTo(PALO - 7f, 154f); cubicTo(PALO - 9f, 160f, PALO + d.x - 2f, 150f + d.y * 0.6f, PALO + d.x - 4f, 154f + d.y) }, l.tono(C.NEGRO), style = Stroke(2.4f, cap = StrokeCap.Round))
            }
            // La campana, en su ménsula (el día que es el objeto, está sobre la escotilla).
            drawLine(l.tono(C.HIERRO), Offset(PALO - 7f, 190f), Offset(PALO - 15f, 190f), 1.4f, StrokeCap.Round)
            if (m.ficha.objeto != "campana") scale(0.72f, 0.72f, Offset(PALO - 15f, 191f)) { campana(l, r, Offset(PALO - 15f, 205f)) }
            // La capa: la lona embreada que tapa la fogonadura, con sus cuñas.
            drawPath(poligono(Offset(PALO - 8f, PIE_PALO - 9f), Offset(PALO + 8f, PIE_PALO - 9f), Offset(PALO + 12.4f, PIE_PALO), Offset(PALO - 12.4f, PIE_PALO)), Brush.horizontalGradient(if (r.lat >= 0f) listOf(l.tono(C.BREA), l.tono(lerp(C.BREA, Color(0xFF8A7A66), 0.5f))) else listOf(l.tono(lerp(C.BREA, Color(0xFF8A7A66), 0.5f)), l.tono(C.BREA)), PALO - 12f, PALO + 12f))
            drawLine(cabo, Offset(PALO - 8.6f, PIE_PALO - 8.4f), Offset(PALO + 8.6f, PIE_PALO - 8.4f), 1.4f)
        }
    }

    /** El fogón: una caja de madera llena de arena, con su hogar de ladrillo y la trébede de hierro de la que cuelga el caldero. */
    private fun fogonCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = FOGON; val k = KF
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val apagado = EscenaPirata.tormenta(m)
        val caja = poligono(p(-21f, 0f), p(-19f, -12f), p(19f, -12f), p(21f, 0f))
        val caldero = Path().apply { moveTo(p(-11f, -31f)); lineTo(p(11f, -31f)); cubicTo(p(13f, -24f), p(9f, -17.4f), p(0f, -17f)); cubicTo(p(-9f, -17.4f), p(-13f, -24f), p(-11f, -31f)); close() }
        val forma = Path().apply { addPath(caja); addPath(caldero); addRect(Rect(p(-16.4f, -42f), p(-14f, -12f))); addRect(Rect(p(14f, -42f), p(16.4f, -12f))); addRect(Rect(p(-16.4f, -42f), p(16.4f, -39.6f))); addRect(Rect(p(-14f, -19f), p(14f, -12f))) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.26f), p(-22f, -2.4f), Size(45f * k, 6f * k))
            // La caja, con sus cantoneras de hierro, y la arena que asoma.
            drawPath(caja, Brush.horizontalGradient(if (r.lat >= 0f) listOf(l.tono(mult(C.MADERA, r.color, 0.8f)), l.tono(C.MADERA), l.tono(lerp(C.MADERA, Color(0xFFFFF0CC), 0.35f))) else listOf(l.tono(lerp(C.MADERA, Color(0xFFFFF0CC), 0.35f)), l.tono(C.MADERA), l.tono(mult(C.MADERA, r.color, 0.8f))), p(-21f, 0f).x, p(21f, 0f).x))
            clipPath(caja) { for (y in listOf(-8f, -4f)) drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.6f), p(-22f, y), p(22f, y), 0.5f); val az = Azar(Semilla.de("pirata", "fogon")); repeat(5) { mancha(p(az.entre(-16f, 16f), az.entre(-10f, -1f)), az.entre(3f, 7f) * k, 2f * k, l.tono(Color(0xFF2E2622)), 0.3f) } }
            for (x in listOf(-20f, 18f)) drawRect(l.tono(C.HIERRO), p(x, -12f), Size(2.4f * k, 12f * k))
            drawOval(l.tono(C.ARENA), p(-18.4f, -13.6f), Size(36.8f * k, 3.4f * k)); drawOval(l.tono(Color(0xFF8E8880)).copy(alpha = 0.6f), p(-12f, -13.4f), Size(24f * k, 2.4f * k))
            // El hogar: dos hiladas de ladrillo, con la boca tiznada.
            for (fila in 0..1) for (i in 0..4) {
                val x = -14f + i * 5.6f + (if (fila == 1) 0f else 0f); val y = -19.4f + fila * 3.4f
                if (fila == 1 && i in 1..3) continue
                drawRect(l.tono(if ((i + fila) % 2 == 0) Color(0xFFB0553A) else Color(0xFF964630)), p(x, y), Size(5.2f * k, 3f * k)); drawRect(l.tono(Color(0xFF4A2A1E)).copy(alpha = 0.6f), p(x, y), Size(5.2f * k, 3f * k), style = Stroke(0.4f))
            }
            drawRect(l.tono(Color(0xFF14100E)), p(-8.4f, -16f), Size(16.8f * k, 3.6f * k))
            // La trébede y el caldero, colgado de su cadena.
            val hierro = l.tono(C.HIERRO); val hierroLuz = l.tono(lerp(C.HIERRO, Color(0xFFE2E4EA), 0.4f))
            for (x in listOf(-15.2f, 15.2f)) { drawLine(hierro, p(x, -12f), p(x, -41f), 2f * k); drawLine(hierroLuz, p(x - 0.5f * r.lat, -12f), p(x - 0.5f * r.lat, -41f), 0.5f) }
            drawLine(hierro, p(-16.4f, -41f), p(16.4f, -41f), 2f * k, StrokeCap.Round)
            if (!apagado) {
                for (i in 0..3) drawOval(hierro, p(-0.9f, -39.6f + i * 1.9f), Size(1.8f * k, 2.2f * k), style = Stroke(0.6f))
                drawPath(Path().apply { moveTo(p(-10.6f, -30.6f)); cubicTo(p(-8f, -37f), p(8f, -37f), p(10.6f, -30.6f)) }, hierro, style = Stroke(0.9f))
                volumen(l, r, caldero, p(-12f, 0f).x, p(12f, 0f).x, Color(0xFF3A3A40))
                drawOval(l.tono(Color(0xFF1E1C1E)), p(-11f, -32.4f), Size(22f * k, 3f * k)); drawOval(hierroLuz.copy(alpha = 0.7f), p(-11f, -32.4f), Size(22f * k, 3f * k), style = Stroke(0.5f))
                // El cucharón, que asoma.
                drawLine(l.tono(C.MADERA), p(5f, -31.6f), p(13f, -40f), 1.2f * k, StrokeCap.Round)
                // El tizne de debajo.
                clipPath(caldero) { drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)), p(0f, -24f).y, p(0f, -17f).y), p(-13f, -24f), Size(26f * k, 7f * k)) }
            } else {
                // Con tormenta no se enciende: el caldero, boca abajo en la arena, y el hogar tapado con su lona.
                drawPath(Path().apply { moveTo(p(-9f, -19f)); cubicTo(p(-9f, -30f), p(9f, -30f), p(9f, -19f)); close() }, l.tono(Color(0xFF3A3A40)))
            }
        }
    }

    /** La escotilla: la brazola y, encima, el enjaretado, donde queda el objeto del día. Con tormenta va tapada con su encerado. */
    private fun escotillaCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val tapa = poligono(Offset(EX0 + 6f, EYT), Offset(EX1 - 6f, EYT), Offset(EX1, EYF), Offset(EX0, EYF))
        val forma = poligono(listOf(Offset(EX0 + 6f, EYT), Offset(EX1 - 6f, EYT), Offset(EX1, EYF), Offset(EX1, EY1), Offset(EX0, EY1), Offset(EX0, EYF)))
        return Cosa(forma, Offset((EX0 + EX1) / 2f, EY1)) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(EX0 - 2f, EY1 - 2.6f), Size(EX1 - EX0 + 4f, 5.6f))
            val madera = l.tono(C.MADERA); val luz = l.tono(lerp(C.MADERA, Color(0xFFFFF0CC), 0.45f)); val sombra = l.tono(mult(C.MADERA, r.color, 0.85f * r.contraste))
            // La brazola: la cara de delante, con sus cabezas de perno y el canto comido por los pies.
            drawRect(Brush.verticalGradient(listOf(madera, lerp(madera, sombra, 0.75f)), EYF, EY1), Offset(EX0, EYF), Size(EX1 - EX0, EY1 - EYF))
            drawRect(if (r.lat >= 0f) luz else sombra, Offset(EX1 - 3f, EYF), Size(3f, EY1 - EYF)); drawRect(if (r.lat >= 0f) sombra else luz, Offset(EX0, EYF), Size(2.6f, EY1 - EYF))
            val az = Azar(Semilla.de("pirata", "escotilla"))
            repeat(6) { val x = az.entre(EX0 + 4f, EX1 - 6f); drawPath(poligono(Offset(x, EYF), Offset(x + az.entre(2f, 5f), EYF), Offset(x + 1.4f, EYF + az.entre(0.8f, 2f))), sombra) }
            for (x in listOf(EX0 + 10f, EX0 + 30f, EX1 - 30f, EX1 - 10f)) drawCircle(l.tono(C.HIERRO), 0.9f, Offset(x, EYF + 5f))
            drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.6f), Offset(EX0, EYF + 9f), Offset(EX1, EYF + 9f), 0.5f)
            if (EscenaPirata.tormenta(m)) {
                // El encerado, tirante, con sus listones clavados.
                drawPath(tapa, Brush.verticalGradient(listOf(l.tono(lerp(C.BREA, C.LONA_OSC, 0.4f)), l.tono(lerp(C.BREA, C.LONA_OSC, 0.6f))), EYT, EYF))
                drawLine(l.tono(C.MADERA), Offset(EX0 + 4f, EYT + 2f), Offset(EX1 - 4f, EYT + 2f), 1.3f); drawLine(l.tono(C.MADERA), Offset(EX0 + 0.6f, EYF - 1.4f), Offset(EX1 - 0.6f, EYF - 1.4f), 1.6f)
            } else {
                // El enjaretado: una rejilla de listones, con lo oscuro de la bodega debajo.
                drawPath(tapa, l.tono(Color(0xFF1A1512)))
                clipPath(tapa) {
                    for (i in 0..18) { val k = i / 18f; drawLine(Brush.verticalGradient(listOf(madera, luz), EYT, EYF), Offset(EX0 + 6f + (EX1 - EX0 - 12f) * k, EYT), Offset(EX0 + (EX1 - EX0) * k, EYF), 2.6f) }
                    for (y in listOf(EYT + 1.2f, EYT + 3.8f, EYT + 6.8f, EYF - 0.6f)) drawLine(luz, Offset(EX0, y), Offset(EX1, y), 1.5f)
                }
                drawPath(tapa, sombra, style = Stroke(0.6f))
            }
            drawLine(luz, Offset(EX0, EYF), Offset(EX1, EYF), 0.6f)
            // El objeto del día, encima.
            val sobre = SOBRE
            val largo = m.ficha.objeto == "mosquete"
            if (!largo) capaSombra(r) { c -> clipPath(tapa) { drawOval(c, Offset(sobre.x - 21f + r.ax * 4f, sobre.y - 2.4f), Size(42f, 5.4f)) } }
            with(ObjetosPirata) { scale(KO, KO, sobre) { objeto(m, l, r, sobre) } }
            if (EscenaPirata.conGente(m) && EscenaPirata.farol(m)) with(ObjetosPirata) { scale(1.2f, 1.2f, sobre) { if (m.ficha.objeto != "dados") dados(l, sobre + Offset(-24f, 1f), conCubilete = false); jarro(l, r, sobre + Offset(24f, 0.6f)) } }
        }
    }

    /** El tonel del agua, trincado a la amurada: duelas, zunchos, la tapa con su cazo. Lo corta el borde del cuadro. */
    private fun tonelCosa(l: Luces, r: Rayo): Cosa {
        val pie = TONEL
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { moveTo(p(-19f, 0f)); cubicTo(p(-26f, -16f), p(-26f, -42f), p(-19f, -58f)); lineTo(p(19f, -58f)); cubicTo(p(26f, -42f), p(26f, -16f), p(19f, 0f)); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.26f), p(-24f, -3f), Size(48f, 7f))
            volumen(l, r, forma, pie.x - 25f, pie.x + 25f, Color(0xFFA9824E))
            clipPath(forma) {
                for (i in -4..4) { val x = i * 5.4f; drawPath(Path().apply { moveTo(p(x * 0.78f, 0f)); cubicTo(p(x * 1.08f, -16f), p(x * 1.08f, -42f), p(x * 0.78f, -58f)) }, l.tono(C.MADERA_OSC).copy(alpha = 0.5f), style = Stroke(0.5f)) }
                for (y in listOf(-52f, -43f, -15f, -6f)) { drawPath(Path().apply { moveTo(p(-27f, y)); cubicTo(p(-10f, y + 2.4f), p(10f, y + 2.4f), p(27f, y)) }, l.tono(C.HIERRO), style = Stroke(2.6f)); drawPath(Path().apply { moveTo(p(-27f, y - 1f)); cubicTo(p(-10f, y + 1.4f), p(10f, y + 1.4f), p(27f, y - 1f)) }, l.tono(lerp(C.HIERRO, Color.White, 0.35f)).copy(alpha = 0.7f), style = Stroke(0.5f)) }
                val az = Azar(Semilla.de("pirata", "tonel")); repeat(8) { mancha(p(az.entre(-20f, 20f), az.entre(-50f, -4f)), az.entre(3f, 8f), az.entre(3f, 8f), l.tono(if (it % 2 == 0) Color(0xFF5E4630) else Color(0xFFE6D2B0)), 0.16f) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF3E4A3A)).copy(alpha = 0.4f)), pie.y - 10f, pie.y), p(-27f, -10f), Size(54f, 10f))
            }
            // La tapa, corrida, con el cazo de hojalata encima.
            drawOval(l.tono(C.MADERA_OSC), p(-19.6f, -61.6f), Size(39.2f, 7.4f)); drawOval(l.tono(lerp(C.MADERA, Color.White, 0.2f)), p(-18f, -61.6f), Size(36f, 5.6f))
            drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.5f), p(-17f, -58.8f), p(17f, -58.8f), 0.4f)
            drawOval(l.tono(Color(0xFF9AA0A8)), p(-12f, -64.6f), Size(9f, 4.6f)); drawOval(l.tono(Color(0xFF50545C)), p(-11f, -64.2f), Size(7f, 3f)); drawLine(l.tono(Color(0xFF9AA0A8)), p(-4f, -62.4f), p(4f, -65.6f), 1.1f, StrokeCap.Round)
            // La trinca, que lo abraza y va a un cáncamo de la amurada.
            drawPath(Path().apply { moveTo(p(-26f, -30f)); cubicTo(p(-10f, -26.6f), p(10f, -26.6f), p(26f, -30f)) }, l.tono(C.CABO), style = Stroke(1.5f)); drawLine(l.tono(C.CABO), p(-25f, -30f), Offset(338f, 229f), 1.1f)
        }
    }

    /** Un barrilete tumbado, calzado para que no ruede. */
    private fun barrileteCosa(l: Luces, r: Rayo, pie: Offset): Cosa {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { moveTo(p(-13f, -2f)); cubicTo(p(-15f, -6f), p(-15f, -12f), p(-13f, -15f)); cubicTo(p(-4f, -17.6f), p(4f, -17.6f), p(13f, -15f)); cubicTo(p(15f, -12f), p(15f, -6f), p(13f, -2f)); cubicTo(p(4f, 0.6f), p(-4f, 0.6f), p(-13f, -2f)); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), p(-14f, -2f), Size(28f, 4.6f))
            drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(C.MADERA, Color(0xFFFFF0CC), 0.4f)), l.tono(C.MADERA), l.tono(mult(C.MADERA, r.color, 0.9f))), pie.y - 17f, pie.y))
            clipPath(forma) { for (y in listOf(-12.6f, -8.4f, -4.4f)) drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.5f), p(-15f, y), p(15f, y), 0.4f); for (x in listOf(-9.4f, -5.4f, 5.4f, 9.4f)) drawLine(l.tono(C.HIERRO), p(x, -18f), p(x, 1f), 1.4f) }
            drawOval(l.tono(lerp(C.MADERA_OSC, C.MADERA, 0.4f)), p(-15.4f, -14.6f), Size(4.4f, 12.4f)); drawCircle(l.tono(C.MADERA_OSC), 0.9f, p(0f, -15.6f))
            for (s in listOf(-1f, 1f)) drawPath(poligono(p(s * 9f, 0.4f), p(s * 15f, 0.4f), p(s * 10.6f, -3.4f)), l.tono(C.MADERA_OSC))
        }
    }

    /** El cubo de baldear, de duelas, con su asa de cabo. En fiesta le deja el sitio al barril de ron. */
    private fun cuboCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = if (EscenaPirata.fiesta(m)) Offset(244f, 280f) else Offset(RON_X - 2f, 274f)
        val k = 1.35f
        val silueta = poligono(Offset(pie.x - 6f * k, pie.y), Offset(pie.x - 7.6f * k, pie.y - 12.6f * k), Offset(pie.x + 7.6f * k, pie.y - 12.6f * k), Offset(pie.x + 6f * k, pie.y))
        return Cosa(silueta, pie) { scale(k, k, pie) {
            fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
            val forma = poligono(p(-6f, 0f), p(-7.6f, -12.6f), p(7.6f, -12.6f), p(6f, 0f))
            drawOval(Color.Black.copy(alpha = 0.24f), p(-8f, -1.6f), Size(16f, 3.6f))
            volumen(l, r, forma, pie.x - 7.6f, pie.x + 7.6f, lerp(C.MADERA, Color(0xFF8E7858), 0.4f))
            for (y in listOf(-10f, -3f)) drawLine(l.tono(C.HIERRO), p(-7.4f, y), p(7.4f, y), 1.2f)
            drawOval(l.tono(Color(0xFF3E5A66)), p(-7.4f, -14f), Size(14.8f, 3f))
            drawPath(Path().apply { moveTo(p(-7f, -12.6f)); cubicTo(p(-6f, -22f), p(6f, -22f), p(7f, -12.6f)) }, l.tono(C.CABO), style = Stroke(1.1f, cap = StrokeCap.Round))
        } }
    }

    /** El lampazo: un manojo de filásticas en un palo, apoyado en la amurada mientras se baldea. */
    private fun lampazoCosa(l: Luces, r: Rayo): Cosa {
        val a = Offset(232f, 283f); val b = Offset(206f, 209f)
        val forma = Path().apply { moveTo(a.x - 1.4f, a.y); lineTo(b.x - 1.2f, b.y); lineTo(b.x + 1.2f, b.y); lineTo(a.x + 1.4f, a.y); close(); addOval(Rect(a.x - 9f, a.y - 7f, a.x + 9f, a.y + 2f)) }
        return Cosa(forma, a) {
            drawLine(l.tono(mult(C.MADERA, r.color, 0.7f)), a, b, 2.4f, StrokeCap.Round); drawLine(l.tono(lerp(C.MADERA, Color.White, 0.3f)), a + Offset(-0.6f * r.lat, 0f), b + Offset(-0.6f * r.lat, 0f), 0.7f)
            val az = Azar(Semilla.de("pirata", "lampazo"))
            repeat(16) { val d = az.entre(-9f, 9f); drawPath(Path().apply { moveTo(a.x, a.y - 6f); cubicTo(a.x + d * 0.4f, a.y - 3f, a.x + d * 0.9f, a.y - 1f, a.x + d, a.y + az.entre(-1f, 2f)) }, l.tono(if (it % 2 == 0) C.CABO else lerp(C.CABO, Color(0xFF6A5E48), 0.5f)), style = Stroke(1.2f, cap = StrokeCap.Round)) }
            drawLine(l.tono(C.CABO), a + Offset(-2.4f, -6.6f), a + Offset(2.4f, -6.6f), 1.8f, StrokeCap.Round)
        }
    }

    /** Un cabo adujado en la cubierta, a la holandesa: una espiral plana. */
    private fun adujaCosa(l: Luces, r: Rayo, pie: Offset): Cosa {
        val forma = Path().apply { addOval(Rect(pie.x - 17f, pie.y - 9f, pie.x + 17f, pie.y + 1f)) }
        return Cosa(forma, pie) {
            val cabo = l.tono(C.CABO); val sombra = l.tono(mult(C.CABO, r.color, 0.85f)); val luz = l.tono(lerp(C.CABO, Color.White, 0.3f))
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 18f, pie.y - 8f), Size(36f, 10.4f))
            for (i in 0..5) { val k = 1f - i / 6f; drawOval(if (i % 2 == 0) cabo else sombra, Offset(pie.x - 17f * k, pie.y - 4f - 5f * k), Size(34f * k, 10f * k), style = Stroke(2f)) }
            drawOval(luz.copy(alpha = 0.6f), Offset(pie.x - 17f, pie.y - 9.4f), Size(34f, 10f), style = Stroke(0.5f))
            // El chicote, que se va hacia la amurada.
            drawPath(Path().apply { moveTo(pie.x - 16f, pie.y - 4f); cubicTo(pie.x - 26f, pie.y - 8f, pie.x - 22f, pie.y - 20f, pie.x - 30f, pie.y - 30f) }, cabo, style = Stroke(1.8f, cap = StrokeCap.Round))
        }
    }

    /** Una tortuga, panza arriba, como se guardan vivas para la olla. */
    private fun tortugaCosa(l: Luces, r: Rayo, pie: Offset, lado: Float): Cosa {
        val k = 1.5f
        val forma = Path().apply { addOval(Rect(pie.x - 17f * k, pie.y - 12f * k, pie.x + 17f * k, pie.y + 1f)) }
        return Cosa(forma, pie) { scale(k, k, pie) {
            fun p(dx: Float, dy: Float) = Offset(pie.x + lado * dx, pie.y + dy)
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(pie.x - 17f, pie.y - 3f), Size(34f, 5.4f))
            // Las aletas, la cabeza y el canto del caparazón, que asoma por debajo.
            val piel = l.tono(Color(0xFF6E8052)); val pielLuz = l.tono(Color(0xFFA8B47A))
            for ((a, b) in listOf(p(-11f, -4f) to p(-21f, -0.6f), p(10f, -3f) to p(19f, 0.6f), p(-9f, -9f) to p(-18f, -12.6f), p(9f, -9f) to p(17f, -13.4f))) { drawLine(piel, a, b, 4f, StrokeCap.Round); drawLine(pielLuz, a + Offset(0f, -1f), b + Offset(0f, -1f), 0.8f, StrokeCap.Round) }
            drawOval(piel, Offset(p(17f, -8f).x - 4f, pie.y - 10.6f), Size(8f, 5.6f)); drawCircle(l.tono(Color(0xFF1E1A1C)), 0.6f, p(18.4f, -8.6f))
            drawOval(l.tono(Color(0xFF4A563A)), Offset(pie.x - 15.4f, pie.y - 9.6f), Size(30.8f, 10.6f))
            // El peto, claro, con sus placas.
            val peto = Path().apply { addOval(Rect(pie.x - 13f, pie.y - 11.6f, pie.x + 13f, pie.y - 2f)) }
            volumen(l, r, peto, pie.x - 13f, pie.x + 13f, Color(0xFFEADB9E))
            clipPath(peto) { for (x in listOf(-6.6f, 0f, 6.6f)) drawLine(l.tono(Color(0xFF9A8A56)).copy(alpha = 0.8f), Offset(pie.x + x, pie.y - 12f), Offset(pie.x + x, pie.y), 0.5f); for (y in listOf(-8.6f, -5.2f)) drawLine(l.tono(Color(0xFF9A8A56)).copy(alpha = 0.8f), Offset(pie.x - 14f, pie.y + y), Offset(pie.x + 14f, pie.y + y), 0.5f) }
        } }
    }

    /** El barril de ron de las fiestas, de pie, con su canilla y los jarros encima. */
    private fun ronCosa(l: Luces, r: Rayo): Cosa {
        val pie = Offset(RON_X, 272f)
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { moveTo(p(-10f, 0f)); cubicTo(p(-14f, -8f), p(-14f, -22f), p(-10f, -30f)); lineTo(p(10f, -30f)); cubicTo(p(14f, -22f), p(14f, -8f), p(10f, 0f)); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.26f), p(-13f, -2f), Size(26f, 5f))
            volumen(l, r, forma, pie.x - 13.4f, pie.x + 13.4f, Color(0xFF8E5E36))
            clipPath(forma) { for (y in listOf(-26f, -20f, -9f, -3.6f)) drawPath(Path().apply { moveTo(p(-15f, y)); cubicTo(p(-5f, y + 1.6f), p(5f, y + 1.6f), p(15f, y)) }, l.tono(C.HIERRO), style = Stroke(1.6f)); for (x in listOf(-6f, 0f, 6f)) drawLine(l.tono(Color(0xFF4A2E1C)).copy(alpha = 0.5f), p(x, -30f), p(x, 0f), 0.4f) }
            drawOval(l.tono(Color(0xFF6E4628)), p(-10.4f, -32f), Size(20.8f, 4.4f)); drawOval(l.tono(Color(0xFFA9764A)), p(-9.4f, -32f), Size(18.8f, 3.2f))
            drawLine(l.tono(C.LATON), p(-13f, -8f), p(-17.6f, -7f), 1.6f, StrokeCap.Round); drawLine(l.tono(C.LATON), p(-16.4f, -9.6f), p(-16.4f, -6f), 1f)
            with(ObjetosPirata) { jarro(l, r, p(-4f, -30.4f)); jarro(l, r, p(4.4f, -29.6f)) }
        }
    }

    /** El rezón del abordaje, listo en la cubierta: cuatro uñas de hierro y su cabo, adujado. */
    private fun rezonCosa(l: Luces, r: Rayo): Cosa {
        val pie = Offset(244f, 281f)
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { addOval(Rect(p(-13f, -9f), p(13f, 1f))) }
        return Cosa(forma, pie) {
            val cabo = l.tono(C.CABO); val hierro = l.tono(C.HIERRO)
            drawOval(Color.Black.copy(alpha = 0.2f), p(-14f, -7f), Size(28f, 9f))
            for (i in 0..2) { val k = 1f - i / 4f; drawOval(if (i % 2 == 0) cabo else l.tono(mult(C.CABO, r.color, 0.85f)), Offset(pie.x - 12f * k, pie.y - 3.6f - 4f * k), Size(24f * k, 8f * k), style = Stroke(1.8f)) }
            drawLine(hierro, p(6f, -4f), p(16f, -15f), 1.8f, StrokeCap.Round)
            for (a in listOf(-0.3f, 0.7f, 1.7f, 2.9f)) drawPath(Path().apply { val c = p(16f, -15f); moveTo(c.x, c.y); cubicTo(c.x + cos(a) * 4f, c.y - 2f + sin(a) * 2f, c.x + cos(a) * 7f, c.y + sin(a) * 3f, c.x + cos(a) * 6f, c.y + 4f + sin(a) * 3f) }, hierro, style = Stroke(1.4f, cap = StrokeCap.Round))
        }
    }

    /** El mosquete del día: largo, apoyado en la brazola de la escotilla, con la culata en la cubierta. */
    private fun mosqueteCosa(l: Luces, r: Rayo): Cosa {
        val a = Offset(288f, 283f); val b = Offset(346f, 224f)
        val d = (b - a) / (b - a).getDistance(); val n = Offset(-d.y, d.x)
        val forma = Path().apply { moveTo(a.x - n.x * 3f, a.y - n.y * 3f); lineTo(b.x - n.x * 1f, b.y - n.y * 1f); lineTo(b.x + n.x * 1f, b.y + n.y * 1f); lineTo(a.x + n.x * 3f, a.y + n.y * 3f); close() }
        return Cosa(forma, a) {
            val madera = l.tono(Color(0xFF7A4E2E)); val luz = l.tono(Color(0xFFB0794A)); val hierro = l.tono(C.HIERRO)
            // La culata, la caja y el cañón, con su llave de chispa y su baqueta.
            val culata = Path().apply { moveTo(a.x - n.x * 4f - d.x * 2f, a.y - n.y * 4f - d.y * 2f); lineTo(a.x + n.x * 3.6f - d.x * 2f, a.y + n.y * 3.6f - d.y * 2f); lineTo(a.x + d.x * 20f + n.x * 1.6f, a.y + d.y * 20f + n.y * 1.6f); lineTo(a.x + d.x * 20f - n.x * 1.6f, a.y + d.y * 20f - n.y * 1.6f); close() }
            drawPath(culata, madera); drawLine(luz, a + d * 1f - n * 2.6f, a + d * 19f - n * 1.2f, 0.7f)
            drawLine(madera, a + d * 18f, a + d * 58f, 3.2f, StrokeCap.Round)
            drawLine(hierro, a + d * 20f - n * 1.2f, b - n * 1.2f, 1.6f, StrokeCap.Round); drawLine(l.tono(lerp(C.HIERRO, Color.White, 0.5f)), a + d * 20f - n * 1.8f, b - n * 1.8f, 0.4f)
            drawLine(l.tono(Color(0xFF5A3A22)), a + d * 30f + n * 1.6f, a + d * 76f + n * 0.6f, 0.7f)
            for (k in listOf(34f, 52f, 68f)) drawLine(l.tono(C.LATON), a + d * k - n * 2f, a + d * k + n * 1.8f, 1.2f)
            drawPath(poligono(a + d * 21f - n * 2f, a + d * 26f - n * 4.6f, a + d * 28f - n * 2f), hierro); drawCircle(l.tono(C.LATON), 1.1f, a + d * 23f + n * 0.4f)
            drawPath(Path().apply { val c = a + d * 20f + n * 2f; moveTo(c.x, c.y); cubicTo(c.x + n.x * 4f, c.y + n.y * 4f, c.x + d.x * 6f + n.x * 4f, c.y + d.y * 6f + n.y * 4f, c.x + d.x * 7f, c.y + d.y * 7f) }, l.tono(C.LATON), style = Stroke(0.8f))
        }
    }

    /** La campana de a bordo: de bronce, con su badajo y su rabiza trenzada. */
    fun DrawScope.campana(l: Luces, r: Rayo, pie: Offset) {
        val forma = Path().apply {
            moveTo(pie.x - 7.4f, pie.y); cubicTo(pie.x - 6f, pie.y - 2.4f, pie.x - 5.4f, pie.y - 9f, pie.x - 3f, pie.y - 12.4f); lineTo(pie.x + 3f, pie.y - 12.4f)
            cubicTo(pie.x + 5.4f, pie.y - 9f, pie.x + 6f, pie.y - 2.4f, pie.x + 7.4f, pie.y); close()
        }
        drawRect(l.tono(C.HIERRO), Offset(pie.x - 1.4f, pie.y - 15.4f), Size(2.8f, 3.4f))
        volumen(l, r, forma, pie.x - 7.4f, pie.x + 7.4f, C.BRONCE)
        drawLine(l.tono(lerp(C.BRONCE, Color(0xFF5A3A1A), 0.5f)), Offset(pie.x - 6.6f, pie.y - 1.6f), Offset(pie.x + 6.6f, pie.y - 1.6f), 0.7f); drawLine(l.tono(lerp(C.BRONCE, Color(0xFF5A3A1A), 0.5f)), Offset(pie.x - 4.4f, pie.y - 9.4f), Offset(pie.x + 4.4f, pie.y - 9.4f), 0.5f)
        drawCircle(l.tono(C.HIERRO), 1.3f, Offset(pie.x, pie.y + 0.6f))
        drawPath(Path().apply { moveTo(pie.x, pie.y + 1.6f); cubicTo(pie.x + 1.4f, pie.y + 4f, pie.x - 1.4f, pie.y + 6f, pie.x + 0.6f, pie.y + 8.6f) }, l.tono(C.CABO), style = Stroke(1.1f, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.cubiertaViva(m: Momento, l: Luces, reloj: Float, viento: Float) {
        // El fogón: llama y humo a la hora de comer; brasas después. Con tormenta no se enciende.
        val boca = FOGON + Offset(0f, -15f * KF)
        if (!EscenaPirata.tormenta(m)) {
            if (EscenaPirata.comiendo(m)) {
                llama(boca + Offset(-5f * KF, 1f), 6.4f * KF, reloj, 1); llama(boca + Offset(4.6f * KF, 1f), 5.6f * KF, reloj, 2)
                resplandor(boca, 30f * KF, 0.25f + 0.5f * l.noche)
                humo(l, FOGON + Offset(0f, -34f * KF), 74f, 18f, reloj, viento, 0.16f, 0f, 4, 10, 0.8f)
            } else {
                brasas(l, boca + Offset(0f, 1.6f), 14f * KF, reloj)
                if (l.noche > 0.3f) resplandor(boca, 26f * KF, 0.42f * l.noche, Color(0xFFFF5A1E))
            }
        }
        // El farol de la botavara.
        if (EscenaPirata.farol(m)) { llama(FAROL + Offset(0f, 10.4f), 4f, reloj, 3); resplandor(FAROL + Offset(0f, 7f), 46f, 0.5f * l.noche) }
        // El pedrero, recién disparado.
        if (EscenaPirata.abordaje(m)) humo(l, Offset(330f, 184f), 26f, 12f, reloj, viento * 0.8f, 0.2f, 0f, 9, 8, 0.9f)
    }

    // ---------------------------------------------------------------- El gato

    fun estorbos(m: Momento): List<Float> = buildList { add(FOGON.x); if (EscenaPirata.fiesta(m)) add(RON_X) }

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 104f, 281f, hasta = 330f))
        add(Ancla(TipoAncla.MIRADOR, 110f, CAP))                               // en la regala, mirando el mar
        add(Ancla(TipoAncla.OBJETO, SOBRE.x, 283f))
        add(Ancla(TipoAncla.ABRIGO, 104f, 281f))                               // al socaire de la chalupa
        if (EscenaPirata.comiendo(m) && !EscenaPirata.tormenta(m)) add(Ancla(TipoAncla.FUEGO, 184f, 283f, hasta = FOGON.x))
        if (EscenaPirata.fiesta(m)) add(Ancla(TipoAncla.ADORNO, RON_X + 22f, 283f))
    }
}
