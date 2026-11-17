package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.H
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.Vista
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Costa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.abs

/**
 * El puerto de cerca: cuando el bergantín está fondeado frente a un puerto ([EscenaBotanico.puerto]), la costa no es una
 * franja en el horizonte sino la de la rada, a unos doscientos metros: a la izquierda, la loma con el pueblo en hileras
 * que suben (cada una más chica), sus iglesias y sus árboles; abajo, los almacenes del muelle, el muelle de piedra con
 * sus escalones y sus bolardos, las lanchas amarradas; a la derecha, la punta con su fuerte; entre las dos, la boca de la
 * rada, por donde se ve el horizonte (y salen el sol y la luna); en la rada, otros barcos fondeados con su reflejo. Cada
 * costa lo viste a su modo ([estilo]): el trópico verde con la ciudad blanca de tejados rojos; las islas volcánicas
 * negras y secas; el Plata bajo y pardo; Valparaíso con sus cerros secos y las casas subidas a ellos; las colinas;
 * Falmouth de piedra gris. **[Diseño propio]**: la ficha solo dice el puerto; cómo es cada uno, la memoria de la tanda
 * **[sin verificar]**.
 *
 * Navegando, la costa sigue lejos ([MundoBarco]).
 */
internal object PuertoBarco {
    private val C = ColorBotanico

    /** Dónde está la orilla de la rada en el lienzo: a unos 200 metros. */
    const val ORILLA = H + 6f

    /** El aire de cada puerto: el color de la loma, de las paredes y de los tejados, y cuánto sube la loma. */
    private class Estilo(val loma: Color, val pared: Color, val tejado: Color, val alto: Float, val seco: Boolean)

    private fun estilo(c: Costa): Estilo = when (c) {
        Costa.TROPICAL -> Estilo(lerp(C.SELVA, C.HOJA, 0.35f), C.CAL, C.TEJA, 1f, false)
        Costa.VOLCANICA -> Estilo(lerp(C.BREA, C.CUERO, 0.35f), lerp(C.CAL, C.ARENA, 0.3f), lerp(C.TEJA, C.CUERO, 0.3f), 0.9f, true)
        Costa.PATAGONIA -> Estilo(lerp(C.ARENA, C.CUERO, 0.35f), lerp(C.CAL, C.ARENA, 0.2f), lerp(C.TEJA, C.ROCA, 0.3f), 0.45f, true)
        Costa.ANDES -> Estilo(lerp(C.ARENA, C.CUERO, 0.45f), C.CAL, C.TEJA, 1.05f, true)
        Costa.COLINAS -> Estilo(lerp(C.HOJA, C.SELVA, 0.45f), lerp(C.CAL, C.ARENA, 0.15f), lerp(C.TEJA, C.ROCA, 0.2f), 0.75f, false)
        Costa.INGLESA -> Estilo(lerp(C.HOJA, C.SELVA, 0.3f), lerp(C.ROCA, C.LONA, 0.5f), lerp(C.ROCA, C.BREA, 0.35f), 0.7f, false)
        Costa.FUEGO -> Estilo(lerp(C.SELVA_OSC, C.ROCA, 0.3f), lerp(C.ROCA, C.LONA, 0.5f), lerp(C.ROCA, C.BREA, 0.3f), 0.8f, false)
        Costa.MAR_ABIERTO, Costa.ATOLON -> Estilo(lerp(C.SELVA, C.HOJA, 0.35f), C.CAL, C.TEJA, 0.6f, false)
    }

    /** La cresta de la loma del pueblo (de x 92 a 266) y la de la punta de la derecha (de 334 al borde), en el lienzo. */
    internal fun lomaDelPueblo(alto: Float, az: Azar): List<Offset> {
        val xs = listOf(92f, 106f, 124f, 142f, 160f, 178f, 196f, 212f, 228f, 242f, 254f, 262f, 267f)
        val hs = listOf(0f, 0.3f, 0.6f, 0.8f, 0.92f, 1f, 0.94f, 0.84f, 0.66f, 0.46f, 0.26f, 0.1f, 0f)
        return xs.mapIndexed { i, x -> Offset(x + az.entre(-2f, 2f), ORILLA - 2f - 58f * alto * hs[i] * az.entre(0.88f, 1.06f)) }
    }

    internal fun puntaDeLaDerecha(alto: Float, az: Azar): List<Offset> =
        listOf(Offset(336f, ORILLA - 1f), Offset(342f, ORILLA - 14f * alto), Offset(352f, ORILLA - 28f * alto), Offset(364f, ORILLA - 34f * alto * az.entre(0.9f, 1.1f)), Offset(378f, ORILLA - 38f * alto), Offset(400f, ORILLA - 40f * alto))

    /** La altura de una cresta en [x]. */
    private fun en(cresta: List<Offset>, x: Float): Float {
        for (i in 0 until cresta.size - 1) { val a = cresta[i]; val b = cresta[i + 1]; if (x >= a.x && x <= b.x) return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x).coerceAtLeast(0.001f)) }
        return ORILLA
    }

    fun DrawScope.puerto(v: Vista) {
        val l = v.l; val m = v.m; val c = EscenaBotanico.costa(m); val e = estilo(c)
        val az = EscenaBotanico.aguas(m, "puerto")
        val ruinas = EscenaBotanico.ruinas(m)
        val loma = lomaDelPueblo(e.alto, az); val punta = puntaDeLaDerecha(e.alto, az)
        // La loma y la punta, con su cara de luz y de sombra, su monte y su bruma al pie.
        with(MundoBarco) { cerroDe(v, loma, ORILLA + 1f, e.loma, 0.36f, az, e.seco) }
        with(MundoBarco) { cerroDe(v, punta, ORILLA + 1f, lerp(e.loma, C.ROCA, 0.25f), 0.38f, az, e.seco) }
        // El fuerte de la punta, en lo alto, con sus cañoneras y su bandera; y su faro.
        fuerte(v, 360f, punta[3].y + 3f, e)
        // El pueblo: hileras de casas que suben la loma, de la de arriba (más lejos) a la de abajo.
        val filas = 7
        for (r in filas - 1 downTo 0) {
            val esc = 1f - 0.075f * r; val base = ORILLA - 3f - r * 6.6f * e.alto
            var x = 98f + az.entre(0f, 9f)
            while (x < 262f) {
                val w = 7.5f * esc * az.entre(0.7f, 1.6f); val h = 6f * esc * (if (az.real() > 0.8f) az.entre(1.3f, 1.6f) else az.entre(0.7f, 1.1f))
                val cresta = en(loma, x + w / 2f); val b = base + az.entre(-2f, 2f) + 1.5f * kotlin.math.sin(x * 0.07f + r)
                val que = az.real()
                if (b - h - 2f > cresta && que > 0.26f) casa(v, x, b, w, h, e, 0.34f + 0.025f * r, az, ruinas && az.real() > 0.15f)
                else if (b > cresta + 1.5f && que > 0.08f) { arbol(v, x + w * 0.3f, b, 3.4f * esc, e, 0.34f + 0.025f * r, az); if (az.real() > 0.5f) arbol(v, x + w * 0.8f, b + 0.6f, 2.8f * esc, e, 0.34f + 0.025f * r, az) }
                x += w * az.entre(1.02f, 1.6f)
            }
        }
        // Las iglesias: una grande en lo alto de la loma y otra junto al muelle.
        if (!ruinas) { iglesia(v, 172f + az.entre(-6f, 6f), ORILLA - 3f - 5f * 6.6f * e.alto, 1.15f, e, 0.42f); iglesia(v, 236f, ORILLA - 3f - 6.6f * e.alto, 0.95f, e, 0.36f) }
        else repeat(10) { mancha(Offset(az.entre(110f, 255f), ORILLA - az.entre(4f, 40f) * e.alto), az.entre(8f, 18f), az.entre(3f, 7f), l.tono(lerp(C.ARENA, C.LONA, 0.3f), 0.38f), 0.35f) }
        // El reflejo de la loma y del pueblo en el agua quieta de la rada, y el de la punta.
        reflejoRoto(96f, 262f, ORILLA + 1.2f, 18f, l.tono(e.loma, 0.36f), 0.42f, az, 0.9f)
        reflejoRoto(150f, 255f, ORILLA + 1.2f, 10f, l.tono(e.pared, 0.36f), 0.28f, az, 0.9f)
        reflejoRoto(338f, ANCHO, ORILLA + 1.2f, 16f, l.tono(lerp(e.loma, C.ROCA, 0.25f), 0.38f), 0.4f, az, 0.9f)
        // Los almacenes del muelle y el muelle de piedra con sus escalones, bolardos, fardos y barriles; las lanchas amarradas.
        almacenes(v, 146f, 250f, e, az)
        muelle(v, 140f, 256f, e, az)
        lanchas(v, az)
        // Los otros barcos fondeados en la rada, con su reflejo y, de noche, su farol.
        val p = v.p; val farol = if (l.noche > 0.3f) l.noche else 0f
        with(PiezasBotanico) {
            bergantinLejano(p, Offset(352f, ORILLA + 3.5f), 26f, 1f, 0.42f, az, farol)
            bergantinLejano(p, Offset(206f, ORILLA + 11f), 44f, -1f, 0.32f, az, farol)
        }
        // De noche, las ventanas del pueblo y los faroles del muelle se miran en el agua.
        if (l.noche > 0.3f) {
            val nz = EscenaBotanico.aguas(m, "luces del muelle")
            for (x in listOf(152f, 196f, 244f)) { resplandor(Offset(x, ORILLA - 2f), 10f, 0.7f * l.noche, C.LUMBRE); reflejoRoto(x - 2f, x + 2f, ORILLA + 2f, 16f, C.LUMBRE, 0.5f * l.noche, nz, 0.8f) }
        }
    }

    /** Una casa: el muro con su lado de luz y de sombra, el tejado a cuatro aguas con su alero, la puerta, las ventanas; de noche, encendidas y con su reflejo. */
    private fun DrawScope.casa(v: Vista, x: Float, base: Float, w: Float, h: Float, e: Estilo, lejos: Float, az: Azar, ruina: Boolean) {
        val l = v.l; val solDer = v.p.solALaDerecha; val dia = v.r.dia * (1f - 0.6f * l.nublado)
        // Cada casa de su color: encalada, ocre, rosada o de un azul gastado; cada una con su tejado.
        val pared = lerp(e.pared, listOf(C.LONA, C.ARENA, C.TEJA, C.MAR, C.LONA)[az.entero(5)], az.entre(0.05f, 0.3f))
        val sombra = l.tono(mult(pared, Color(0xFF7A78B8), 0.55f + 0.2f * dia), lejos)
        if (ruina) {
            val alto = h * az.entre(0.3f, 0.7f)
            drawPath(poligono(listOf(Offset(x, base), Offset(x, base - alto), Offset(x + w * 0.3f, base - alto * 0.7f), Offset(x + w * 0.5f, base - alto), Offset(x + w * 0.8f, base - alto * 0.4f), Offset(x + w, base))), l.tono(pared, lejos))
            drawOval(l.tono(lerp(pared, C.ARENA, 0.4f), lejos), Offset(x - w * 0.15f, base - 1.2f), Size(w * 1.3f, 2.4f))
            return
        }
        val lado = w * 0.32f
        drawRect(l.tono(lerp(pared, Color(0xFFFFF0D8), 0.25f * dia), lejos), Offset(x, base - h), Size(w, h))
        drawRect(sombra, Offset(if (solDer) x else x + w - lado, base - h), Size(lado, h))
        // El tejado, con su lado de luz.
        val t = l.tono(e.tejado, lejos); val ts = l.tono(mult(e.tejado, Color(0xFF7A78B8), 0.5f), lejos)
        val alero = w * 0.08f; val cumbre = h * 0.45f
        drawPath(poligono(Offset(x - alero, base - h), Offset(x + w + alero, base - h), Offset(x + w * 0.7f, base - h - cumbre), Offset(x + w * 0.3f, base - h - cumbre)), t)
        drawPath(poligono(Offset(if (solDer) x - alero else x + w * 0.7f, base - h), Offset(if (solDer) x + w * 0.3f else x + w + alero, base - h), Offset(if (solDer) x + w * 0.3f else x + w * 0.7f, base - h - cumbre)), ts)
        drawLine(l.tono(mult(e.tejado, Color(0xFF5A5888), 0.6f), lejos), Offset(x - alero, base - h + 0.4f), Offset(x + w + alero, base - h + 0.4f), 0.6f)
        // La puerta y las ventanas.
        val oscuro = l.tono(lerp(C.BREA, pared, 0.25f), lejos)
        drawRect(oscuro, Offset(x + w * 0.42f, base - h * 0.5f), Size(w * 0.16f, h * 0.5f))
        val enc = l.noche > 0.35f && az.real() > 0.4f
        val ventanas = when { w > 9f -> listOf(0.1f, 0.68f, 0.82f); w > 6.5f -> listOf(0.15f, 0.68f); else -> listOf(0.7f) }
        for (fx in ventanas) {
            val q = Offset(x + w * fx, base - h * (if (h > 7f) 0.82f else 0.72f)); val s = Size(w * 0.13f, h * 0.2f)
            drawRect(if (enc) C.LUMBRE.copy(alpha = (0.95f * l.noche).coerceIn(0f, 1f)) else oscuro, q, s)
        }
        if (enc) reflejoRoto(x + w * 0.2f, x + w * 0.8f, ORILLA + 2f, (ORILLA - base) * 0.8f + 6f, C.LUMBRE, 0.35f * l.noche, az, 0.9f)
        // Su sombra corta en la loma.
        if (v.r.fuerza > 0.05f) drawOval(Color(0xFF6A68A8).copy(alpha = 0.25f * v.r.fuerza), Offset(if (solDer) x - w * 0.4f else x + w * 0.4f, base - 1f), Size(w, 2f))
    }

    /** Un árbol del pueblo: una copa oscura con su lado de luz. */
    private fun DrawScope.arbol(v: Vista, x: Float, base: Float, r: Float, e: Estilo, lejos: Float, az: Azar) {
        val l = v.l; val verde = if (e.seco) lerp(C.SELVA, C.ARENA, 0.25f) else C.SELVA
        val lado = if (v.p.solALaDerecha) 1f else -1f
        drawLine(l.tono(C.MADERA_OSC, lejos), Offset(x, base), Offset(x, base - r), 0.7f)
        drawOval(l.tono(mult(verde, Color(0xFF5A5C98), 0.5f), lejos), Offset(x - r, base - r * 2.1f), Size(r * 2f, r * 1.6f))
        drawOval(l.tono(lerp(verde, C.HOJA, 0.4f), lejos), Offset(x - r * 0.8f + lado * r * 0.25f, base - r * 2.2f), Size(r * 1.3f, r * 1.0f))
        az.real()
    }

    /** Una iglesia: la nave con su tejado, la fachada y la torre con su campanario y su chapitel; de noche, sus ventanas. */
    private fun DrawScope.iglesia(v: Vista, x: Float, base: Float, k: Float, e: Estilo, lejos: Float) {
        val l = v.l; val solDer = v.p.solALaDerecha; val dia = v.r.dia
        val pared = l.tono(lerp(e.pared, Color(0xFFFFF0D8), 0.2f * dia), lejos); val sombra = l.tono(mult(e.pared, Color(0xFF7A78B8), 0.6f), lejos)
        val w = 20f * k; val h = 11f * k
        drawRect(pared, Offset(x, base - h), Size(w, h)); drawRect(sombra, Offset(if (solDer) x else x + w * 0.7f, base - h), Size(w * 0.3f, h))
        drawPath(poligono(Offset(x - 1f, base - h), Offset(x + w + 1f, base - h), Offset(x + w * 0.5f, base - h - 5f * k)), l.tono(e.tejado, lejos))
        // Las dos torres de la fachada.
        for (tx in listOf(x - 4f * k, x + w - 1f * k)) {
            val tw = 5f * k; val th = 22f * k
            drawRect(pared, Offset(tx, base - th), Size(tw, th)); drawRect(sombra, Offset(if (solDer) tx else tx + tw * 0.6f, base - th), Size(tw * 0.4f, th))
            drawRect(l.tono(lerp(C.BREA, e.pared, 0.2f), lejos), Offset(tx + tw * 0.3f, base - th + 3f * k), Size(tw * 0.4f, 3f * k))
            drawPath(Path().apply { moveTo(tx - 0.5f, base - th); cubicTo(tx + tw * 0.1f, base - th - 3f * k, tx + tw * 0.4f, base - th - 5f * k, tx + tw * 0.5f, base - th - 7f * k); cubicTo(tx + tw * 0.6f, base - th - 5f * k, tx + tw * 0.9f, base - th - 3f * k, tx + tw + 0.5f, base - th); close() }, l.tono(lerp(e.tejado, C.LATON, 0.3f), lejos))
            if (l.noche > 0.35f) drawRect(C.LUMBRE.copy(alpha = (0.6f * l.noche).coerceIn(0f, 1f)), Offset(tx + tw * 0.3f, base - th * 0.5f), Size(tw * 0.4f, 2f * k))
        }
        drawRect(l.tono(lerp(C.BREA, e.pared, 0.2f), lejos), Offset(x + w * 0.42f, base - h * 0.55f), Size(w * 0.16f, h * 0.55f))
    }

    /** El fuerte de la punta: un muro bajo con cañoneras, su garita y su bandera; y una linterna de faro, encendida de noche. */
    private fun DrawScope.fuerte(v: Vista, x: Float, base: Float, e: Estilo) {
        val l = v.l; val solDer = v.p.solALaDerecha
        val piedra = lerp(C.ROCA, C.LONA, 0.4f)
        drawRect(l.tono(piedra, 0.4f), Offset(x - 2f, base - 7f), Size(26f, 7f)); drawRect(l.tono(mult(piedra, Color(0xFF7A78B8), 0.6f), 0.4f), Offset(if (solDer) x - 2f else x + 18f, base - 7f), Size(6f, 7f))
        for (i in 0..4) drawRect(l.tono(C.BREA, 0.4f), Offset(x + 1f + i * 4.6f, base - 5f), Size(1.4f, 1.4f))
        drawRect(l.tono(piedra, 0.4f), Offset(x + 18f, base - 13f), Size(5f, 6f))
        drawLine(l.tono(C.MADERA_OSC, 0.4f), Offset(x + 4f, base - 7f), Offset(x + 4f, base - 18f), 0.6f)
        drawRect(l.tono(C.TEJA, 0.4f), Offset(x + 4.3f, base - 18f), Size(4.5f, 2.6f))
        if (l.noche > 0.3f) resplandor(Offset(x + 20.5f, base - 14f), 9f, 0.8f * l.noche, C.LUMBRE)
        e.alto.let { }
    }

    /** Los almacenes del muelle: edificios largos de piedra encalada con su soportal de arcos oscuros y su tejado. */
    private fun DrawScope.almacenes(v: Vista, x0: Float, x1: Float, e: Estilo, az: Azar) {
        val l = v.l; val solDer = v.p.solALaDerecha
        var x = x0
        while (x < x1) {
            val w = az.entre(16f, 26f); val h = az.entre(7f, 10f); val base = ORILLA - 0.5f
            val pared = l.tono(lerp(e.pared, C.ARENA, 0.2f), 0.33f)
            drawRect(pared, Offset(x, base - h), Size(w, h))
            drawRect(l.tono(mult(e.pared, Color(0xFF7A78B8), 0.55f), 0.33f), Offset(if (solDer) x else x + w - 4f, base - h), Size(4f, h))
            var ax = x + 2f
            while (ax < x + w - 3f) { drawPath(Path().apply { moveTo(ax, base); lineTo(Offset(ax, base - h * 0.42f)); cubicTo(ax, base - h * 0.62f, ax + 2.6f, base - h * 0.62f, ax + 2.6f, base - h * 0.42f); lineTo(Offset(ax + 2.6f, base)); close() }, l.tono(lerp(C.BREA, e.pared, 0.2f), 0.33f)); ax += 4.2f }
            if (l.noche > 0.35f && az.real() > 0.4f) drawRect(C.LUMBRE.copy(alpha = (0.8f * l.noche).coerceIn(0f, 1f)), Offset(x + w * 0.3f, base - h * 0.8f), Size(2f, 1.6f))
            drawPath(poligono(Offset(x - 1f, base - h), Offset(x + w + 1f, base - h), Offset(x + w - 3f, base - h - 4f), Offset(x + 3f, base - h - 4f)), l.tono(e.tejado, 0.33f))
            x += w + az.entre(1f, 5f)
        }
    }

    /**
     * El muelle de piedra, a lo largo de la orilla: el frente de sillares con sus juntas y su línea de agua verdosa, el
     * canto de arriba al sol, los escalones que bajan al agua, los bolardos, unos fardos y barriles.
     */
    private fun DrawScope.muelle(v: Vista, x0: Float, x1: Float, e: Estilo, az: Azar) {
        val l = v.l; val y0 = ORILLA - 0.6f; val y1 = ORILLA + 3.4f
        val piedra = lerp(C.ROCA, C.ARENA, 0.45f)
        val frente = poligono(Offset(x0, y0), Offset(x1, y0), Offset(x1 + 1f, y1), Offset(x0 - 1f, y1))
        drawPath(frente, Brush.verticalGradient(listOf(l.tono(piedra, 0.3f), l.tono(mult(piedra, Color(0xFF6A78A0), 0.5f), 0.3f)), y0, y1))
        clipPath(frente) {
            for (j in 1..2) drawLine(l.tono(mult(piedra, Color(0xFF5A5888), 0.6f), 0.3f).copy(alpha = 0.6f), Offset(x0, y0 + j * 1.2f), Offset(x1, y0 + j * 1.2f), 0.4f)
            var x = x0; var fila = 0
            while (x < x1) { drawLine(l.tono(mult(piedra, Color(0xFF5A5888), 0.6f), 0.3f).copy(alpha = 0.5f), Offset(x, y0), Offset(x, y1), 0.4f); x += 3.2f + (fila % 2) * 0.8f; fila++ }
            drawRect(l.tono(lerp(C.SELVA, C.MAR, 0.5f), 0.3f).copy(alpha = 0.6f), Offset(x0, y1 - 1.1f), Size(x1 - x0, 1.2f))
        }
        drawLine(l.tono(lerp(piedra, Color.White, 0.35f), 0.3f), Offset(x0, y0), Offset(x1, y0), 0.8f)
        // Los escalones, en un extremo.
        for (i in 0..3) drawRect(l.tono(lerp(piedra, if (i % 2 == 0) Color.White else C.ROCA, 0.2f), 0.3f), Offset(x1 - 10f + i * 1.6f, y0 + i * 0.9f), Size(10f - i * 1.6f, 0.9f))
        // Los bolardos y lo que espera en el muelle: fardos, barriles, cajas.
        for (i in 0..5) { val bx = x0 + 8f + i * (x1 - x0 - 20f) / 5f; drawRect(l.tono(C.BREA, 0.3f), Offset(bx, y0 - 1.6f), Size(1.2f, 1.6f)) }
        repeat(7) {
            val bx = az.entre(x0 + 4f, x1 - 14f); val s = az.entre(1.6f, 2.8f)
            if (az.real() > 0.5f) { drawOval(l.tono(C.MADERA, 0.3f), Offset(bx, y0 - s * 1.1f), Size(s, s * 1.1f)); drawLine(l.tono(C.HIERRO, 0.3f), Offset(bx, y0 - s * 0.6f), Offset(bx + s, y0 - s * 0.6f), 0.3f) }
            else drawRect(l.tono(lerp(C.LONA, C.ARENA, 0.4f), 0.3f), Offset(bx, y0 - s), Size(s * 1.4f, s))
        }
    }

    /** Las lanchas amarradas al muelle y una que va o viene por la rada (sin gente: con los remos recogidos). */
    private fun DrawScope.lanchas(v: Vista, az: Azar) {
        val l = v.l
        for ((x, y, w) in listOf(Triple(162f, ORILLA + 5f, 12f), Triple(186f, ORILLA + 4.6f, 10f), Triple(286f, ORILLA + 9f, 13f))) {
            val casco = l.tono(lerp(C.LONA, C.ESCARABAJO, az.entre(0f, 0.6f)), 0.28f)
            drawPath(Path().apply { moveTo(x - w / 2f, y - 2f); lineTo(Offset(x + w / 2f, y - 2.3f)); cubicTo(x + w * 0.45f, y - 0.5f, x + w * 0.3f, y, x + w * 0.2f, y); lineTo(Offset(x - w * 0.4f, y)); close() }, casco)
            drawLine(l.tono(C.MADERA_OSC, 0.28f), Offset(x - w / 2f, y - 2f), Offset(x + w / 2f, y - 2.3f), 0.6f)
            drawLine(l.tono(C.MADERA, 0.28f), Offset(x - w * 0.3f, y - 2.4f), Offset(x + w * 0.35f, y - 2.6f), 0.5f)
            reflejoRoto(x - w / 2f, x + w / 2f, y + 0.4f, 5f, casco, 0.35f, az, 0.7f)
            if (abs(x - 286f) > 1f) drawLine(l.tono(C.CANAMO, 0.28f).copy(alpha = 0.8f), Offset(x - w / 2f, y - 2f), Offset(x - w / 2f - 4f, ORILLA - 0.6f), 0.4f, StrokeCap.Round)
        }
        drawPath(Path().apply { moveTo(Offset(280f, ORILLA + 9.5f)); cubicTo(276f, ORILLA + 10f, 270f, ORILLA + 10.3f, 262f, ORILLA + 10.4f) }, l.tono(Color.White).copy(alpha = 0.4f * (1f - 0.6f * l.noche)), style = Stroke(0.6f))
    }
}
