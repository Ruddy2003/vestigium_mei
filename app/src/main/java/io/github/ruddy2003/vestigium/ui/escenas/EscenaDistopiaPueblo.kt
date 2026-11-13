package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.briznas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cipres
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El pueblo de la hermana (estado `provincia`, dos días de la temporada 4). No es un cuadro nuevo (decisión 21): es la
 * misma plaza con su poste y su cartel, pero detrás no hay bloques, ni cámaras, ni farolas: una calle de tierra con
 * tres casas de teja, la cooperativa con sus silos, el campo de remolacha hasta el horizonte y el cielo entero, sin humo.
 */
internal object PuebloDistopia {
    private val C = ColorDistopia
    private const val H = EscenaDistopia.H
    private const val BORDE = CiudadDistopia.BORDE

    private val TEJA = Color(0xFFA85A3A)
    private val ADOBE = Color(0xFFB88A62)
    private val TIERRA = Color(0xFFC6B48C)
    private val TIERRA_OSC = Color(0xFF8A7658)

    private val CHIMENEA = Offset(31f, 131f)
    private val TREN = Offset(206f, 119f)

    /** Un punto de la calle de tierra: a [X] de su eje (de −71 a 71 al llegar a la plaza), a la profundidad [u]. */
    private fun rc(X: Float, u: Float) = Offset(156.5f + (22.5f + X) * u, H + 2f + (BORDE - 1f - H) * u)

    fun DrawScope.pueblo(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("distopia", "pueblo"))
        val est = m.ficha.estacion
        monte(l, perfil(fijo, -40f, 430f, H + 2f, 13f, 0.25f, 28), H + 3f, Color(0xFF7C8AA0), 0.84f)
        monte(l, perfil(fijo, 180f, 430f, H + 2f, 7f, 0.2f, 20), H + 3f, lerp(hierbaDe(est), Color(0xFF6C7C7A), 0.5f), 0.7f)
        // El campo de remolacha: surcos que huyen hasta el horizonte.
        val verde = lerp(hierbaDe(est), Color(0xFF5E7A4A), 0.5f); val tierra = Color(0xFF9A8460)
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(verde, tierra, 0.3f), 0.6f), l.tono(lerp(verde, tierra, 0.4f), 0.1f)), H, BORDE), Offset(0f, H), Size(ANCHO, BORDE - H + 1f))
        for (k in -22..22) {
            val x1 = 156f + k * 26f
            drawPath(poligono(Offset(156f + k * 1.2f, H + 1f), Offset(x1 - 6f, BORDE + 1f), Offset(x1 + 6f, BORDE + 1f)), l.tono(lerp(verde, Color(0xFF3E5A36), 0.4f), 0.2f).copy(alpha = 0.6f))
            drawPath(poligono(Offset(156f + k * 1.2f, H + 1f), Offset(x1 + 6f, BORDE + 1f), Offset(x1 + 9f, BORDE + 1f)), l.tono(tierra, 0.2f).copy(alpha = 0.45f))
        }
        repeat(8) { mancha(Offset(fijo.entre(0f, ANCHO), fijo.entre(H + 6f, 190f)), fijo.entre(30f, 70f), fijo.entre(2f, 6f), l.tono(if (it % 2 == 0) Color(0xFFB8C07A) else Color(0xFF4E6A40), 0.3f), 0.2f) }
        // La hilera de chopos del río y, lejos, la estación con su depósito de agua.
        for (i in 0..8) cipres(l, Offset(250f + i * 15f + fijo.entre(-3f, 3f), H + 2f), fijo.entre(15f, 23f), 0.6f)
        drawRect(l.tono(C.HORMIGON, 0.6f), Offset(190f, 121f), Size(20f, 8f)); drawRect(l.tono(TEJA, 0.6f), Offset(188f, 119f), Size(24f, 2.4f))
        drawRect(l.tono(C.HIERRO, 0.6f), Offset(174f, 116f), Size(1f, 13f)); drawRect(l.tono(C.HIERRO, 0.6f), Offset(179f, 116f), Size(1f, 13f)); drawRoundRect(l.tono(C.OXIDO, 0.6f), Offset(172f, 109f), Size(10f, 8f), CornerRadius(2f))
        drawLine(l.tono(C.HOLLIN, 0.6f), Offset(110f, H + 0.6f), Offset(300f, H + 0.6f), 0.5f)
        calle(l, r, verde, fijo)
        // Los postes del telégrafo, que se alejan con la calle, y su hilo.
        val palo = Color(0xFF6A5A46); val cimas = ArrayList<Offset>()
        var u = 0.94f
        while (u > 0.1f) { val pie = rc(80f, u); val alto = 74f * u; val lejos = (0.5f - 0.5f * u).coerceAtLeast(0.04f)
            drawLine(l.tono(palo, lejos), pie, pie + Offset(0f, -alto), 1.7f * u + 0.3f); drawLine(l.tono(palo, lejos), pie + Offset(-6f * u, -alto * 0.92f), pie + Offset(6f * u, -alto * 0.92f), 1f * u + 0.2f)
            for (s in listOf(-5f, 5f)) drawCircle(l.tono(C.HUESO, lejos), 0.9f * u + 0.1f, pie + Offset(s * u, -alto * 0.95f))
            cimas += pie + Offset(-5f * u, -alto * 0.95f); u *= 0.6f }
        for (i in 0 until cimas.size - 1) { val a = cimas[i]; val b = cimas[i + 1]; val medio = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f + (a.y - b.y).let { kotlin.math.abs(it) } * 0.12f + 1f)
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(medio.x, medio.y, medio.x, medio.y, b.x, b.y) }, l.tono(C.HOLLIN, 0.3f).copy(alpha = 0.7f), style = Stroke(0.4f)) }
        // Las casas, de lejos a cerca; y enfrente, la cooperativa.
        val encendida = l.noche > 0.3f && m.hacer != Hacer.DORMIR
        casa(m, l, r, 110f, 130f, 152f, 13f, Color(0xFFDCD6C6), 0.36f, 3, encendida, 1)
        casa(m, l, r, 68f, 102f, 174f, 21f, Color(0xFFD4C8A8), 0.22f, 2, encendida, 1)
        casa(m, l, r, 2f, 58f, 199f, 33f, Color(0xFFE2D8BE), 0.08f, 1, encendida, 2)
        cooperativa(m, l, r, encendida)
        // El huerto de la hermana, junto a la casa, con sus cañas; y las matas del borde de la plaza.
        for (i in 0..6) { val p = Offset(78f + i * 4.6f, 200f - i * 0.5f); drawLine(l.tono(Color(0xFF8A7A52), 0.08f), p, p + Offset(0.4f, -8f - (i % 3) * 1.6f), 0.5f); drawCircle(l.tono(Color(0xFF4E6A40), 0.08f), 1.8f + 0.5f * (i % 2), p + Offset(0f, -3f - (i % 3))) }
        repeat(14) { val x = fijo.entre(0f, ANCHO); mancha(Offset(x, BORDE - fijo.entre(0f, 3f)), fijo.entre(5f, 12f), 2f, l.tono(lerp(verde, mult(verde, r.color, 0.6f), 0.4f), 0.06f), 0.7f) }
    }

    /**
     * La calle de tierra que sale de la plaza: las dos rodadas de los carros, la hierba que crece en el centro y se mete
     * por los bordes, los charcos ya secos, con su barro cuarteado, y las piedras sueltas.
     */
    private fun DrawScope.calle(l: Luces, r: Rayo, verde: Color, az: Azar) {
        val calle = poligono(rc(-71f, 1.02f), rc(-71f, 0f), rc(71f, 0f), rc(71f, 1.02f))
        drawPath(calle, Brush.verticalGradient(listOf(l.tono(TIERRA, 0.6f), l.tono(TIERRA, 0.3f), l.tono(lerp(TIERRA, TIERRA_OSC, 0.15f), 0.05f)), H, BORDE))
        fun lejos(u: Float) = (0.55f - 0.55f * u).coerceAtLeast(0.04f)
        clipPath(calle) {
            // Lo pisado y lo sin pisar.
            repeat(26) { val u = sqrt(az.real()); val c = rc(az.entre(-66f, 66f), u); mancha(c, az.entre(8f, 24f) * u, az.entre(1f, 2.6f) * u, l.tono(if (it % 2 == 0) TIERRA_OSC else Color(0xFFE2D4B0), lejos(u)), 0.12f + 0.16f * u) }
            // Las rodadas: dos surcos hondos y, al lado, las de los días en que el carro se arrimó.
            for ((X, w, fuerza) in listOf(Triple(-25f, 3.4f, 1f), Triple(25f, 3.4f, 1f), Triple(-33f, 2f, 0.5f), Triple(19f, 2f, 0.5f))) {
                val pts = (0..10).map { i -> val u = 1.02f - i * 0.1f; rc(X + 3f * sin(i * 0.9f + X), u.coerceAtLeast(0.02f)) to u.coerceAtLeast(0.02f) }
                for (i in 0 until pts.size - 1) { val (a, ua) = pts[i]; val (b, ub) = pts[i + 1]
                    drawPath(poligono(a + Offset(-w * ua, 0f), a + Offset(w * ua, 0f), b + Offset(w * ub, 0f), b + Offset(-w * ub, 0f)), l.tono(TIERRA_OSC, lejos(ua)).copy(alpha = fuerza * (0.2f + 0.4f * ua)))
                    drawLine(l.tono(Color(0xFFE6DAB8), lejos(ua)).copy(alpha = 0.4f * fuerza * ua), a + Offset(if (r.lat >= 0f) -w * ua else w * ua, 0f), b + Offset(if (r.lat >= 0f) -w * ub else w * ub, 0f), 0.5f * ua + 0.1f) }
            }
            // Los charcos secos: el barro claro, cuarteado.
            for ((X, u) in listOf(-44f to 0.9f, 40f to 0.64f, -10f to 0.44f)) { val c = rc(X, u); val w = 15f * u
                val charco = curva((0..8).map { val a = it / 8f * 6.283f; Offset(c.x + cos(a) * w * (0.8f + 0.3f * sin(it * 2.3f + X)), c.y + sin(a) * w * 0.2f) }).apply { close() }
                drawPath(charco, l.tono(TIERRA_OSC, lejos(u)).copy(alpha = 0.5f), style = Stroke(0.9f * u)); drawPath(charco, l.tono(Color(0xFFDDD0AC), lejos(u)))
                clipPath(charco) { repeat(7) { var q = c + Offset(az.entre(-w, w), az.entre(-w * 0.2f, w * 0.2f)); val g = Path().apply { moveTo(q.x, q.y); repeat(3) { q += Offset(az.entre(-4f, 4f) * u, az.entre(-1.4f, 1.4f) * u); lineTo(q.x, q.y) } }; drawPath(g, l.tono(TIERRA_OSC, lejos(u)).copy(alpha = 0.7f), style = Stroke(0.35f + 0.2f * u)) } } }
            // La hierba del centro, entre las rodadas, y la que se mete por los bordes.
            repeat(46) { val u = sqrt(az.real()).coerceIn(0.06f, 1f); val c = rc(az.entre(-9f, 9f), u)
                mancha(c, az.entre(3f, 7f) * u, 1.1f * u, l.tono(lerp(verde, TIERRA, 0.3f), lejos(u)), 0.6f)
                repeat(3) { drawLine(l.tono(if (it == 0) lerp(verde, C.LUZ, 0.25f) else verde, lejos(u)), c + Offset((it - 1) * 1.6f * u, 0.4f * u), c + Offset((it - 1) * 1.6f * u + az.entre(-1.2f, 1.2f) * u, -az.entre(2f, 4.6f) * u), 0.5f * u + 0.15f) } }
            repeat(40) { val u = sqrt(az.real()).coerceIn(0.06f, 1f); val s = if (it % 2 == 0) -1f else 1f; val c = rc(s * az.entre(60f, 73f), u)
                mancha(c, az.entre(5f, 11f) * u, 1.6f * u, l.tono(lerp(verde, mult(verde, r.color, 0.5f), 0.3f), lejos(u)), 0.75f)
                repeat(2) { drawLine(l.tono(verde, lejos(u)), c + Offset(it * 2f * u, 0f), c + Offset(it * 2f * u + az.entre(-1f, 1f) * u, -az.entre(2f, 5f) * u), 0.5f * u + 0.15f) } }
            // Las piedras sueltas, con su sombra corta.
            repeat(30) { val u = sqrt(az.real()).coerceIn(0.1f, 1f); val c = rc(az.entre(-64f, 64f), u); val rad = az.entre(0.6f, 1.5f) * u
                drawOval(l.tono(TIERRA_OSC, lejos(u)).copy(alpha = 0.6f), Offset(c.x - rad + r.ax * 0.6f * rad, c.y - rad * 0.2f), Size(rad * 2.2f, rad * 0.8f)); drawCircle(l.tono(if (it % 3 == 0) C.HORMIGON else Color(0xFF9E8E72), lejos(u)), rad, c + Offset(0f, -rad * 0.4f)) }
        }
    }

    /** Un desconchón: donde se cayó el enlucido asoma el adobe, con sus hiladas. */
    private fun DrawScope.desconchon(l: Luces, r: Rayo, c: Offset, rad: Float, lejos: Float, az: Azar) {
        val pts = (0 until 9).map { val a = it / 9f * 6.283f; val d = rad * az.entre(0.5f, 1.15f); Offset(c.x + cos(a) * d * 1.5f, c.y + sin(a) * d * 0.8f) }
        val forma = curva(pts + pts.first()).apply { close() }
        val junta = l.tono(Color(0xFF8A6244), lejos)
        drawPath(forma, l.tono(ADOBE, lejos))
        clipPath(forma) {
            var y = c.y - rad; var fila = 0
            while (y < c.y + rad) {
                drawLine(junta.copy(alpha = 0.7f), Offset(c.x - rad * 2f, y), Offset(c.x + rad * 2f, y), 0.35f)
                var x = c.x - rad * 2f + (fila % 2) * 2.4f
                while (x < c.x + rad * 2f) { drawLine(junta.copy(alpha = 0.6f), Offset(x, y), Offset(x, y + 2.2f), 0.3f); x += 4.8f }
                y += 2.2f; fila++
            }
            // El canto del enlucido da sombra dentro, arriba y del lado de la luz.
            translate(-r.lat * 0.8f, 0.9f) { drawPath(forma, mult(Color.White, r.color).copy(alpha = 0.5f * (0.4f + 0.6f * r.dia)), style = Stroke(1.4f), blendMode = BlendMode.Multiply) }
        }
        drawPath(forma, l.tono(Color.White, lejos).copy(alpha = 0.5f), style = Stroke(0.35f))
    }

    /**
     * Una casa del pueblo, de [x0] a [x1] y con el pie en [base]: la fachada enlucida, con sus brochazos, sus chorreras y
     * sus desconchones; el costado que da a la calle, con su hastial, a la luz o en sombra según la hora; el tejado de
     * teja, con sus hiladas, sus tejas nuevas y viejas y su cumbrera; el zócalo, la puerta y las ventanas con postigos.
     */
    private fun DrawScope.casa(m: Momento, l: Luces, r: Rayo, x0: Float, x1: Float, base: Float, alto: Float, pared: Color, lejos: Float, n: Int, encendida: Boolean, ventanas: Int) {
        val az = Azar(Semilla.de("distopia", "casa del pueblo", n))
        val k = alto / 30f
        val top = base - alto; val d = alto * 0.42f; val cumbre = top - alto * 0.46f
        val cFrente = CiudadDistopia.cara(l, r, pared, CiudadDistopia.frente(r), lejos)
        val cCostado = CiudadDistopia.cara(l, r, pared, CiudadDistopia.lado(r, 1f), lejos)
        val sucio = l.tono(Color(0xFF8A7A62), lejos)
        // La sombra que tumba en el suelo.
        if (r.fuerza > 0.05f) drawPath(poligono(Offset(x0, base), Offset(x1 + d, base - d * 0.14f), Offset(x1 + d + r.ax * alto * 0.5f, base - d * 0.14f + r.ay * alto * 0.3f), Offset(x0 + r.ax * alto * 0.5f, base + r.ay * alto * 0.3f)), r.color, alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
        // El costado y su hastial.
        val cumbreDer = Offset(x1 + d * 0.5f, cumbre - d * 0.1f)
        val costado = poligono(listOf(Offset(x1, base), Offset(x1, top), cumbreDer, Offset(x1 + d, top - d * 0.2f), Offset(x1 + d, base - d * 0.14f)))
        drawPath(costado, Brush.verticalGradient(listOf(cCostado, lerp(cCostado, sucio, 0.35f)), cumbre, base))
        clipPath(costado) {
            repeat(3) { val x = az.entre(x1, x1 + d); drawRect(Brush.verticalGradient(listOf(sucio.copy(alpha = 0.34f), Color.Transparent), top - d * 0.2f, top + az.entre(4f, 12f) * k), Offset(x, top - d * 0.2f), Size(az.entre(0.6f, 1.6f) * k, 16f * k)) }
            desconchon(l, r, Offset(x1 + d * 0.55f, base - alto * 0.3f), 3f * k, lejos, az)
            drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(x1, base - 4.4f * k), Size(d, 4.4f * k))
            // El ventanuco del desván.
            drawRect(l.tono(C.HOLLIN, lejos), Offset(x1 + d * 0.42f, top - alto * 0.2f), Size(2.4f * k, 3f * k))
        }
        // La fachada.
        drawRect(Brush.verticalGradient(listOf(lerp(cFrente, l.tono(C.LUZ, lejos), 0.1f), cFrente, lerp(cFrente, sucio, 0.3f)), top, base), Offset(x0, top), Size(x1 - x0, alto))
        clipRect(x0, top, x1, base) {
            repeat((14 * k).toInt() + 6) { val p = Offset(az.entre(x0, x1), az.entre(top, base)); drawLine((if (az.real() > 0.5f) l.tono(Color.White, lejos) else sucio).copy(alpha = 0.13f), p, p + Offset(az.entre(3f, 10f) * k, az.entre(-1f, 1f)), az.entre(0.8f, 2f) * k, StrokeCap.Round) }
            repeat(4) { val x = az.entre(x0, x1); val h = az.entre(4f, 12f) * k; drawRect(Brush.verticalGradient(listOf(sucio.copy(alpha = 0.34f), Color.Transparent), top, top + h), Offset(x, top), Size(az.entre(0.6f, 1.8f) * k, h)) }
            repeat(2 + n % 2) { desconchon(l, r, Offset(az.entre(x0 + 5f * k, x1 - 5f * k), az.entre(top + alto * 0.34f, base - 6f * k)), az.entre(2.4f, 4.4f) * k, lejos, az) }
            // La humedad que sube del suelo y el zócalo, con la pintura saltada.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sucio.copy(alpha = 0.3f)), base - 10f * k, base), Offset(x0, base - 10f * k), Size(x1 - x0, 10f * k))
            drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(x0, base - 4.4f * k), Size(x1 - x0, 4.4f * k))
            repeat((6 * k).toInt() + 3) { drawOval(cFrente.copy(alpha = az.entre(0.3f, 0.6f)), Offset(az.entre(x0, x1), base - az.entre(1f, 4.6f) * k), Size(az.entre(1.5f, 5f) * k, az.entre(0.6f, 1.4f) * k)) }
        }
        // La puerta, con su escalón, y las ventanas con sus postigos.
        val madera = l.tono(Color(0xFF5A4630), lejos); val postigo = CiudadDistopia.cara(l, r, lerp(C.VERDE_GRIS, Color(0xFF4A6A52), 0.5f), CiudadDistopia.frente(r), lejos)
        val px = x0 + (x1 - x0) * 0.6f
        drawRect(l.tono(C.HOLLIN, lejos), Offset(px - 0.6f * k, base - 14.6f * k), Size(7.2f * k, 14.6f * k)); drawRect(madera, Offset(px, base - 14f * k), Size(6f * k, 14f * k))
        for (i in 1..2) drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.5f), Offset(px + 2f * k * i, base - 14f * k), Offset(px + 2f * k * i, base), 0.3f * k + 0.1f)
        drawRect(l.tono(C.HORMIGON, lejos), Offset(px - 1.6f * k, base - 1.2f * k), Size(9.2f * k, 1.6f * k))
        val luzCasa = Color(0xFFFFD48A)
        for (i in 0 until ventanas) { val vx = x0 + (x1 - x0) * (if (i == 0) 0.2f else 0.84f) - 2.6f * k; val vy = top + alto * 0.3f
            drawRect(if (encendida) luzCasa else CiudadDistopia.cara(l, r, lerp(C.CRISTAL, C.HOLLIN, 0.3f), CiudadDistopia.frente(r), lejos), Offset(vx, vy), Size(5.2f * k, 6.4f * k))
            drawLine(l.tono(C.HUESO, lejos), Offset(vx + 2.6f * k, vy), Offset(vx + 2.6f * k, vy + 6.4f * k), 0.4f * k + 0.1f)
            drawRect(postigo, Offset(vx - 2.6f * k, vy - 0.3f * k), Size(2.4f * k, 7f * k)); drawRect(postigo, Offset(vx + 5.4f * k, vy - 0.3f * k), Size(2.4f * k, 7f * k))
            drawRect(l.tono(C.HORMIGON, lejos), Offset(vx - 0.8f * k, vy + 6.4f * k), Size(6.8f * k, 1f * k)) }
        // El alero da sombra en lo alto de la pared.
        drawRect(r.color, Offset(x0, top), Size(x1 - x0, 2f * k), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
        // El tejado: el faldón de delante, que recibe la luz de lleno.
        val teja = if (EscenaDistopia.nevado(m)) lerp(TEJA, C.NIEVE, 0.75f) else TEJA
        val a = Offset(x0 - 2f * k, top + 1.2f * k); val b = Offset(x1 + 1f * k, top + 1.2f * k); val cumbreIzq = Offset(x0 + d * 0.5f, cumbre)
        val faldon = poligono(a, b, cumbreDer, cumbreIzq)
        val tejaLuz = CiudadDistopia.cara(l, r, teja, 0.7f * r.dia, lejos); val tejaOsc = l.tono(mult(teja, r.color, 0.7f), lejos)
        drawPath(faldon, Brush.verticalGradient(listOf(tejaLuz, CiudadDistopia.cara(l, r, teja, 0.2f * r.dia, lejos)), cumbre, top))
        clipPath(faldon) {
            // Las hiladas, las canales y las tejas sueltas: unas nuevas, otras negras de verdín.
            val hiladas = (alto / 5.5f).toInt().coerceIn(2, 6)
            for (j in 1 until hiladas) { val t = j / hiladas.toFloat(); drawLine(tejaOsc.copy(alpha = 0.55f), a + (cumbreIzq - a) * t, b + (cumbreDer - b) * t + Offset(0f, az.entre(-0.4f, 0.4f)), 0.35f * k + 0.15f) }
            var x = 0.02f
            while (x < 1f) { val pa = a + (b - a) * x; val pb = cumbreIzq + (cumbreDer - cumbreIzq) * x; drawLine(tejaOsc.copy(alpha = 0.22f), pa, pb, 0.3f * k + 0.1f); x += az.entre(0.035f, 0.07f) / k.coerceAtLeast(0.5f) }
            repeat((22 * k).toInt() + 6) { val tx = az.real(); val ty = az.real(); val q = (a + (b - a) * tx) * (1f - ty) + (cumbreIzq + (cumbreDer - cumbreIzq) * tx) * ty
                drawOval(l.tono(listOf(Color(0xFFC87A52), Color(0xFF7A4630), Color(0xFF6A6A4A))[it % 3], lejos).copy(alpha = 0.55f), q, Size(az.entre(1.4f, 3f) * k, az.entre(0.8f, 1.4f) * k)) }
        }
        drawLine(tejaOsc, a + Offset(0f, 0.5f * k), b + Offset(0f, 0.5f * k), 0.8f * k + 0.2f)                                                     // el alero
        drawLine(l.tono(lerp(teja, Color(0xFFFFE0B0), 0.4f), lejos), cumbreIzq, cumbreDer, 1.1f * k + 0.2f, StrokeCap.Round)                     // la cumbrera
        drawLine(if (r.lat >= 0f) tejaLuz else tejaOsc, b, cumbreDer, 1.2f * k + 0.2f, StrokeCap.Round); drawLine(tejaOsc, cumbreDer, Offset(x1 + d + 1f * k, top - d * 0.2f + 1f * k), 1f * k + 0.2f, StrokeCap.Round)
        // La chimenea.
        val ch = cumbreIzq + (cumbreDer - cumbreIzq) * 0.3f
        drawRect(CiudadDistopia.cara(l, r, Color(0xFF8A5A40), CiudadDistopia.frente(r), lejos), Offset(ch.x - 2f * k, ch.y - 6f * k), Size(4f * k, 7f * k)); drawRect(CiudadDistopia.cara(l, r, Color(0xFF8A5A40), CiudadDistopia.lado(r, 1f), lejos), Offset(ch.x + 0.8f * k, ch.y - 6f * k), Size(1.2f * k, 7f * k))
        drawRect(l.tono(C.HOLLIN, lejos), Offset(ch.x - 2.6f * k, ch.y - 7f * k), Size(5.2f * k, 1.2f * k))
        // La hierba del pie del muro.
        briznas(l, x0, x1, base + 0.6f, lerp(hierbaDe(m.ficha.estacion), Color(0xFF7C8A56), 0.4f), az, 3.6f * k, 2.2f)
    }

    /**
     * La cooperativa, a media distancia, a la derecha de la calle: la nave de bloques con su tejado de chapa, su portón y
     * su muelle, y detrás los dos silos, que suben por encima del horizonte.
     */
    private fun DrawScope.cooperativa(m: Momento, l: Luces, r: Rayo, encendida: Boolean) {
        val az = Azar(Semilla.de("distopia", "cooperativa")); val lejos = 0.14f
        val solDer = r.lat >= 0f
        fun silo(z: Rect, propio: Color) {
            if (r.fuerza > 0.05f) drawOval(r.color, Offset(z.left - 3f + r.ax * 9f, z.bottom - 2.4f), Size(z.width + 6f, 5f), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
            val luz = CiudadDistopia.cara(l, r, propio, 0.8f, lejos); val sombra = CiudadDistopia.cara(l, r, lerp(propio, C.HORMIGON_OSC, 0.5f), -0.8f, lejos)
            val cuerpo = Path().apply { addRect(z) }
            drawPath(cuerpo, Brush.horizontalGradient(if (solDer) listOf(sombra, lerp(sombra, luz, 0.6f), luz) else listOf(luz, lerp(sombra, luz, 0.6f), sombra), z.left, z.right))
            clipPath(cuerpo) {
                var y = z.top + 7f; while (y < z.bottom) { drawLine(l.tono(C.HIERRO, lejos).copy(alpha = 0.5f), Offset(z.left, y), Offset(z.right, y), 0.45f); y += az.entre(8f, 13f) }
                repeat(5) { val x = az.entre(z.left, z.right); val h = az.entre(10f, 34f); drawRect(Brush.verticalGradient(listOf(l.tono(C.OXIDO, lejos).copy(alpha = 0.4f), Color.Transparent), z.top, z.top + h), Offset(x, z.top), Size(az.entre(0.8f, 2.2f), h)) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.HOLLIN, lejos).copy(alpha = 0.3f)), z.bottom - 12f, z.bottom), Offset(z.left, z.bottom - 12f), Size(z.width, 12f))
            }
            // El sombrerete cónico y el tubo de carga.
            drawPath(poligono(Offset(z.left - 1f, z.top), Offset(z.right + 1f, z.top), Offset(z.center.x + 1.6f, z.top - z.width * 0.3f), Offset(z.center.x - 1.6f, z.top - z.width * 0.3f)), CiudadDistopia.cara(l, r, C.HORMIGON_OSC, 0.4f * r.dia, lejos))
            drawLine(l.tono(C.HIERRO, lejos), Offset(z.center.x, z.top - z.width * 0.3f), Offset(z.center.x, z.top - z.width * 0.3f - 4f), 0.8f)
        }
        silo(Rect(262f, 114f, 278f, 186f), Color(0xFFCFCAB8))
        silo(Rect(274f, 98f, 297f, 188f), C.CAL)
        drawLine(l.tono(C.HIERRO, lejos), Offset(276f, 110f), Offset(250f, 142f), 1.4f)                           // el tubo que baja a la nave
        // La nave.
        val nave = Rect(206f, 150f, 268f, 189f)
        if (r.fuerza > 0.05f) drawPath(poligono(Offset(nave.left, nave.bottom), Offset(nave.right, nave.bottom), Offset(nave.right + r.ax * 16f, nave.bottom + r.ay * 9f), Offset(nave.left + r.ax * 16f, nave.bottom + r.ay * 9f)), r.color, alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
        val pared = CiudadDistopia.cara(l, r, C.HORMIGON, CiudadDistopia.frente(r), lejos)
        drawRect(Brush.verticalGradient(listOf(pared, lerp(pared, l.tono(C.HORMIGON_OSC, lejos), 0.45f)), nave.top, nave.bottom), nave.topLeft, nave.size)
        clipRect(nave.left, nave.top, nave.right, nave.bottom) {
            // Los bloques vistos, las chorreras y un paño enlucido que se cae.
            var y = nave.top + 3f; var fila = 0
            while (y < nave.bottom) { drawLine(l.tono(C.HORMIGON_OSC, lejos).copy(alpha = 0.3f), Offset(nave.left, y), Offset(nave.right, y), 0.3f); var x = nave.left + (fila % 2) * 4f; while (x < nave.right) { if (az.real() > 0.4f) drawLine(l.tono(C.HORMIGON_OSC, lejos).copy(alpha = 0.26f), Offset(x, y), Offset(x, y + 3.6f), 0.3f); x += 8f }; y += 3.6f; fila++ }
            repeat(5) { val x = az.entre(nave.left, nave.right); drawRect(Brush.verticalGradient(listOf(l.tono(C.HOLLIN, lejos).copy(alpha = 0.3f), Color.Transparent), nave.top, nave.top + az.entre(6f, 20f)), Offset(x, nave.top), Size(az.entre(0.8f, 2f), 20f)) }
            drawRect(l.tono(Color(0xFFDCD6C6), lejos).copy(alpha = 0.8f), Offset(nave.left + 38f, nave.top + 4f), Size(20f, 16f)); desconchon(l, r, Offset(nave.left + 53f, nave.top + 17f), 3.4f, lejos, az)
            // El emblema, pintado hace años.
            with(CiudadDistopia) { ojo(Offset(nave.left + 47f, nave.top + 11f), 14f, l.tono(C.ROJO, lejos), l.tono(C.ROJO_OSC, lejos), 0.6f) }
        }
        // El portón corredero, a medio abrir, y su muelle.
        drawRect(l.tono(C.HOLLIN, lejos), Offset(nave.left + 8f, nave.top + 13f), Size(24f, 26f)); drawRect(CiudadDistopia.cara(l, r, C.VERDE_GRIS, CiudadDistopia.frente(r), lejos), Offset(nave.left + 4f, nave.top + 13f), Size(13f, 26f))
        for (i in 1..3) drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.4f), Offset(nave.left + 4f + i * 3.2f, nave.top + 13f), Offset(nave.left + 4f + i * 3.2f, nave.bottom), 0.3f)
        drawLine(l.tono(C.HIERRO, lejos), Offset(nave.left + 2f, nave.top + 12.4f), Offset(nave.left + 36f, nave.top + 12.4f), 0.9f)
        if (encendida) drawRect(Color(0xFFFFD48A).copy(alpha = 0.5f), Offset(nave.left + 17f, nave.top + 13f), Size(15f, 26f))
        for (i in 0..1) drawRect(if (encendida) Color(0xFFFFD48A) else CiudadDistopia.cara(l, r, lerp(C.CRISTAL, C.HOLLIN, 0.2f), CiudadDistopia.frente(r), lejos), Offset(nave.left + 40f + i * 10f, nave.top + 25f), Size(7f, 5f))
        drawRect(CiudadDistopia.cara(l, r, C.HORMIGON, 0.6f * r.dia, lejos), Offset(nave.left - 2f, nave.bottom - 3f), Size(40f, 3.6f)); drawRect(l.tono(C.HOLLIN, lejos).copy(alpha = 0.3f), Offset(nave.left - 2f, nave.bottom + 0.4f), Size(40f, 0.6f))
        // El tejado de chapa, con sus ondas y su óxido.
        val chapa = poligono(Offset(nave.left - 3f, nave.top + 1f), Offset(nave.right + 3f, nave.top + 1f), Offset(nave.right - 3f, nave.top - 10f), Offset(nave.left + 5f, nave.top - 10f))
        drawPath(chapa, Brush.verticalGradient(listOf(CiudadDistopia.cara(l, r, Color(0xFF9A9A8E), 0.7f * r.dia, lejos), CiudadDistopia.cara(l, r, Color(0xFF8A8676), 0.2f * r.dia, lejos)), nave.top - 10f, nave.top))
        clipPath(chapa) { var x = nave.left - 3f; while (x < nave.right + 3f) { drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.2f), Offset(x, nave.top + 1f), Offset(x + (nave.center.x - x) * 0.1f + 4f, nave.top - 10f), 0.35f); x += az.entre(2.2f, 3.6f) }
            repeat(6) { mancha(Offset(az.entre(nave.left, nave.right), az.entre(nave.top - 9f, nave.top)), az.entre(3f, 8f), az.entre(1.2f, 2.6f), l.tono(C.OXIDO, lejos), 0.5f) } }
        drawRect(r.color, Offset(nave.left, nave.top + 1f), Size(nave.width, 2f), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
        // Unas cajas de remolacha en el muelle y la hierba del pie.
        for ((i, x) in listOf(nave.left + 1f, nave.left + 9f, nave.left + 5f).withIndex()) { val y = nave.bottom - 3f - (if (i == 2) 5.4f else 0f); drawRect(CiudadDistopia.cara(l, r, C.MADERA, CiudadDistopia.frente(r), lejos), Offset(x, y - 5.4f), Size(7.4f, 5.4f)); drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.5f), Offset(x, y - 2.8f), Offset(x + 7.4f, y - 2.8f), 0.3f) }
        briznas(l, nave.left + 40f, nave.right + 28f, nave.bottom + 0.6f, lerp(hierbaDe(m.ficha.estacion), Color(0xFF7C8A56), 0.4f), az, 4f, 2.4f)
    }

    fun DrawScope.puebloVivo(m: Momento, l: Luces, reloj: Float) {
        val viento = -(0.25f + 0.75f * m.viento)
        if (m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE) humo(l, CHIMENEA, 34f, 8f, reloj, viento, 0.2f, 0.1f, 2, 8)
        humo(l, TREN, 20f, 6f, reloj, viento, 0.3f, 0.6f, 4, 6, 0.6f)
    }
}
