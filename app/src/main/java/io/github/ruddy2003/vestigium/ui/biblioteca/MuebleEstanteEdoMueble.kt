package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SHIBU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SHIBU_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.VETA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WASHI
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.floor

// El estante de castaño de la casa de té (MuebleEstanteEdo): los postes, las tablas pasantes con sus cuñas, la tabla de arriba,
// la hondura de cada hueco con su fondo de papel de caqui, el cuerpo bajo de puertas correderas, los herrajes y los rótulos.

// ---------------------------------------------------------------- los postes

/**
 * Los dos postes de castaño, de [ya] a [yb]: la cara con su veta a lo largo, los chaflanes (el de la izquierda recibe la luz
 * del ranma; el de la derecha, el farol), la cabeza cortada en pirámide que asoma sobre la tabla de arriba, el pie y, en cada
 * ensamble con una tabla de [tablas], una chapa de hierro en rombo con su clavo. De noche, por tramos.
 */
internal fun DrawScope.postesEdo(a: AireEdo, g: MarcoEdo, ya: Float, yb: Float, tablas: List<Rect>) {
    val u = density; val w = g.w
    if (yb - ya < 1f) return
    val cabeza = g.ch - g.gTapa - w * 0.035f
    val pie = g.pisan
    val y0 = maxOf(ya, cabeza); val y1 = minOf(yb, pie)
    if (y1 <= y0) return
    val tramos = if (a.lumbre) ((y1 - y0) / (g.fila * 0.2f)).toInt().coerceIn(2, 120) else 1
    listOf(g.posteIzq to g.x0, g.x1 to g.posteDer).forEachIndexed { i, (xa, xb) ->
        val an = xb - xa
        // La sombra del poste en la pared, a su derecha (de día) o lejos del farol (de noche).
        if (!a.oscuro) drawRect(a.sombra(0.2f), Offset(xb, y0), Size(w * 0.012f, y1 - y0))
        for (t in 0 until tramos) {
            val ta = y0 + (y1 - y0) * t / tramos; val tb = y0 + (y1 - y0) * (t + 1) / tramos + 0.6f
            val p = Offset((xa + xb) / 2, (ta + tb) / 2)
            val k = g.cerca(p); val f = g.frio(p)
            drawRect(a.luz(KURI, k, f), Offset(xa, ta), Size(an, tb - ta))
            // Los chaflanes: el de la izquierda mira al ranma, el de la derecha al farol (que está a la derecha de casi todo).
            drawRect(a.luz(mezcla(KURI, KURI_LUZ, 0.6f), if (a.oscuro) k * 0.4f else 1f, f), Offset(xa, ta), Size(an * 0.12f, tb - ta))
            drawRect(a.luz(KURI_OSC, k, f), Offset(xb - an * 0.12f, ta), Size(an * 0.12f, tb - ta))
            if (a.oscuro) {
                drawRect(a.brillo(0.6f, k), Offset(xb - an * 0.12f, ta), Size(1.2f * u, tb - ta))
                if (a.lunar && f > 0.04f) drawRect(a.helado(0.45f * f), Offset(xa, ta), Size(1.2f * u, tb - ta))
            }
        }
        clipRect(xa, y0, xb, y1) {
            vetas(Rect(xa + an * 0.12f, y0, xb - an * 0.12f, y1), a.luz(VETA, 0.4f).copy(alpha = if (a.oscuro) 0.4f else 0.42f), 4001 + i, densidad = 1.1f, vertical = true, grosor = 0.6f, ondulacion = 0.35f)
            pinceladas(Rect(xa, y0, xb, y1), a.luz(KURI_LUZ, 0.4f), 4003 + i, ((y1 - y0) / (w * 0.05f)).toInt().coerceIn(2, 60), u, 0.12f)
        }
        if (!a.oscuro) drawRect(a.claro(0.5f), Offset(xa + an * 0.12f, y0), Size(1f * u, y1 - y0))
        drawRect(a.sombra(0.3f), Offset(xb - an * 0.12f - 1f * u, y0), Size(1f * u, y1 - y0))
        // La cabeza en pirámide, que asoma sobre la tabla de arriba.
        if (ya <= cabeza + 1f) {
            val k = g.cerca(Offset(xa, cabeza))
            val pico = an * 0.32f
            drawPath(poligonoEdo(xa, cabeza, xa + an * 0.5f, cabeza - pico, xb, cabeza), a.luz(mezcla(KURI, KURI_LUZ, 0.5f), k))
            drawPath(poligonoEdo(xa + an * 0.5f, cabeza - pico, xb, cabeza, xa + an * 0.5f, cabeza), a.luz(KURI_OSC, k))
            drawLine(a.brillo(0.5f, k), Offset(xa, cabeza), Offset(xa + an * 0.5f, cabeza - pico), 0.8f * u)
        }
        // El pie: la madera gastada por las escobas y su sombra en el suelo.
        if (yb >= pie - 1f) {
            drawRect(a.sombra(0.3f), Offset(xa, pie - w * 0.012f), Size(an, w * 0.012f))
            drawOval(a.sombra(0.5f), Offset(xa - 2f * u, pie - 2f * u), Size(an + 7f * u, 4.4f * u))
        }
        // La chapa de hierro en rombo en cada ensamble con una tabla.
        tablas.forEach { t ->
            val c = Offset((xa + xb) / 2, t.center.y)
            val k = g.cerca(c)
            val s = an * 0.26f
            chapaEdo(poligonoEdo(c.x, c.y - s, c.x + s * 0.8f, c.y, c.x, c.y + s, c.x - s * 0.8f, c.y), a, k)
            remacheEdo(c, s * 0.24f, a, k)
        }
    }
}

// ---------------------------------------------------------------- las tablas

/**
 * Una tabla pasante (nuki) de castaño: atraviesa los dos postes y sale por fuera, con su testa a la vista y la cuña que la
 * traba. Su cara de arriba se ve un poco; el canto lleva su veta, su arista gastada y su sombra.
 */
internal fun DrawScope.tablaPasanteEdo(k: Int, r: Rect, a: AireEdo, g: MarcoEdo) {
    // Su cara de arriba y su canto se tocan en r.top, que puede caer a medio píxel: debajo, esa fila del castaño del canto.
    filaJuntaEdo(r.top, g.w * 0.006f, g.w * 0.994f, a.luz(KURI, g.cerca(r.center), g.frio(r.center)))
    tablaEdo(Rect(g.w * 0.006f, r.top, g.w * 0.994f, r.bottom), g.w * 0.012f, 4100 + 7 * k, a, g, true)
}

/** Una tabla de castaño en [cara] (su canto), con [fondo] de cara de arriba a la vista; con [cunas], sus testas y cuñas. */
internal fun DrawScope.tablaEdo(cara: Rect, fondo: Float, semilla: Int, a: AireEdo, g: MarcoEdo, cunas: Boolean) {
    val u = density; val w = g.w
    val tramos = if (a.lumbre) 22 else 1
    for (t in 0 until tramos) {
        val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
        val p = Offset((xa + xb) / 2, cara.center.y)
        val k = g.cerca(p); val f = g.frio(p)
        drawRect(a.luz(mezcla(KURI, KURI_LUZ, 0.45f), k, f), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
        drawRect(a.luz(KURI, k, f), Offset(xa, cara.top), Size(xb - xa, cara.height))
        if (a.oscuro) {
            drawRect(a.brillo(0.7f, k), Offset(xa, cara.top), Size(xb - xa, 1.2f * u))
            if (a.lunar && f > 0.04f) drawRect(a.helado(0.4f * f), Offset(xa, cara.top + 1.2f * u), Size(xb - xa, 0.8f * u))
        }
    }
    if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(LUZ_MUEBLE.copy(alpha = 0.16f), Color.Transparent, a.sombra(0.12f)), cara.left, cara.right), Offset(cara.left, cara.top - fondo), Size(cara.width, cara.height + fondo))
    clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
        vetas(cara, a.luz(VETA, 0.4f).copy(alpha = if (a.oscuro) 0.38f else 0.4f), semilla, densidad = 0.8f, grosor = 0.6f, ondulacion = 0.35f)
        vetas(Rect(cara.left, cara.top - fondo, cara.right, cara.top), a.luz(VETA, 0.4f).copy(alpha = 0.25f), semilla + 1, densidad = 1.2f, grosor = 0.5f, ondulacion = 0.3f)
        // La arista de delante, gastada: mellas cortas.
        for (j in 0 until 6) {
            val x = cara.left + cara.width * azar(semilla, 11 + j)
            drawPath(poligonoEdo(x, cara.top, x + 3f * u, cara.top, x + 1.4f * u, cara.top + 1.8f * u), a.sombra(0.3f))
        }
    }
    if (!a.oscuro) drawRect(a.claro(0.55f), cara.topLeft, Size(cara.width, 1.1f * u))
    drawLine(a.sombra(0.35f), Offset(cara.left, cara.top - fondo), Offset(cara.right, cara.top - fondo), 0.7f * u)
    drawRect(a.sombra(0.4f), Offset(cara.left, cara.bottom - 1.6f * u), Size(cara.width, 1.6f * u))
    drawRect(a.sombra(0.24f), Offset(cara.left, cara.bottom), Size(cara.width, 3.4f * u))
    if (!cunas) return
    // Las testas, fuera de los postes: la madera de punta, con sus anillos, y la cuña que la traba, con sus dos caras.
    listOf(cara.left to 1f, cara.right to -1f).forEach { (xb, s) ->
        val p = Offset(xb, cara.center.y)
        val k = g.cerca(p); val f = g.frio(p)
        val testa = Rect(minOf(xb, xb + s * 3f * u), cara.top - fondo, maxOf(xb, xb + s * 3f * u), cara.bottom)
        drawRect(a.luz(KURI_OSC, k, f), testa.topLeft, testa.size)
        drawArc(a.luz(KURI, k, f), if (s > 0) 90f else -90f, 180f, false, Offset(testa.left - 2f * u, testa.top + 2f * u), Size(4f * u, testa.height - 4f * u), style = Stroke(0.6f * u))
        val xc = xb + s * g.salida * 0.5f
        val arriba = cara.top - fondo - w * 0.014f; val abajo = cara.bottom + w * 0.012f
        val cara1 = poligonoEdo(xc - w * 0.008f, arriba, xc + w * 0.004f, arriba, xc + w * 0.0025f, abajo, xc - w * 0.005f, abajo)
        val cara2 = poligonoEdo(xc + w * 0.004f, arriba, xc + w * 0.009f, arriba + w * 0.004f, xc + w * 0.0055f, abajo, xc + w * 0.0025f, abajo)
        drawPath(corridoEdo(cara1, 1.4f * u, 1.2f * u), a.sombra(0.45f))
        drawPath(cara1, a.luz(mezcla(KURI_LUZ, HINOKI, 0.3f), k, f))
        drawPath(cara2, a.luz(KURI_OSC, k, f))
        drawPath(poligonoEdo(xc - w * 0.008f, arriba, xc + w * 0.004f, arriba, xc + w * 0.009f, arriba + w * 0.004f, xc - w * 0.003f, arriba + w * 0.004f), a.luz(mezcla(HINOKI, LUZ_MUEBLE, 0.3f), k, f))
        drawLine(a.brillo(0.5f, k), Offset(xc - w * 0.008f, arriba), Offset(xc - w * 0.005f, abajo), 0.7f * u)
        if (a.lunar && f > 0.03f) drawLine(a.helado(0.7f * f), Offset(xc - w * 0.0085f, arriba), Offset(xc - w * 0.0055f, abajo), 1f * u)
    }
}

/**
 * La tabla de arriba, volada sobre los postes, más gruesa: su cara de arriba con la veta ancha y un cerco de taza, el canto
 * con su veta, y las cantoneras de hierro en las dos esquinas de delante.
 */
internal fun DrawScope.tapaEstanteEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val cara = Rect(w * 0.004f, g.ch - g.gTapa, w * 0.996f, g.ch)
    // Su sombra en la pared, de la cara de arriba hacia atrás (de día, a la derecha).
    tablaEdo(cara, g.fTapa, 4200, a, g, false)
    val k0 = g.cerca(Offset(w * 0.5f, cara.top))
    drawOval(a.sombra(0.12f), Offset(w * 0.62f, cara.top - g.fTapa * 0.7f), Size(w * 0.05f, g.fTapa * 0.45f), style = Stroke(1f * u))
    drawLine(a.sombra(0.3f), Offset(cara.left, cara.top + cara.height * 0.55f), Offset(cara.right, cara.top + cara.height * 0.55f), 0.5f * u)
    pinceladas(cara, a.luz(KURI_LUZ, k0), 4201, 14, u, 0.12f)
    // Las cantoneras de hierro de las dos esquinas, que abrazan el canto.
    val l = w * 0.07f; val gg = w * 0.02f
    cantoneraEdo(Offset(cara.left, cara.top), 1f, 1f, l, minOf(gg, cara.height * 0.55f), a, g.cerca(Offset(cara.left, cara.top)))
    cantoneraEdo(Offset(cara.right, cara.top), -1f, 1f, l, minOf(gg, cara.height * 0.55f), a, g.cerca(Offset(cara.right, cara.top)))
}

/** Una cantonera de hierro en L en la esquina [o] (sx, sy: hacia dónde abre), con su punta en flecha y sus remaches. */
internal fun DrawScope.cantoneraEdo(o: Offset, sx: Float, sy: Float, l: Float, gr: Float, a: AireEdo, k: Float) {
    val p = Path().apply {
        moveTo(o.x, o.y)
        lineTo(o.x + sx * l, o.y)
        lineTo(o.x + sx * (l + gr * 0.45f), o.y + sy * gr * 0.5f)
        lineTo(o.x + sx * l, o.y + sy * gr)
        lineTo(o.x + sx * gr, o.y + sy * gr)
        lineTo(o.x + sx * gr, o.y + sy * l * 0.6f)
        lineTo(o.x + sx * gr * 0.5f, o.y + sy * (l * 0.6f + gr * 0.45f))
        lineTo(o.x, o.y + sy * l * 0.6f)
        close()
    }
    chapaEdo(p, a, k)
    val r = gr * 0.14f
    remacheEdo(Offset(o.x + sx * gr * 0.5f, o.y + sy * gr * 0.5f), r, a, k)
    remacheEdo(Offset(o.x + sx * l * 0.85f, o.y + sy * gr * 0.5f), r, a, k)
    remacheEdo(Offset(o.x + sx * l * 0.45f, o.y + sy * gr * 0.5f), r, a, k)
}

// ---------------------------------------------------------------- la hondura de cada hueco

/**
 * La junta entre una tabla y el hueco de debajo, que empieza en [r].top: si cae a medio píxel, ni la tabla ni el hueco cubren
 * del todo esa fila y asomaba el fondo (una línea a lo ancho). Debajo de los dos va la fila entera del color de lo de arriba
 * del hueco (la tabla vista por debajo): solo se ve en esa fila, donde los dos se quedan cortos.
 */
internal fun DrawScope.juntaHuecoEdo(r: Rect, a: AireEdo, g: MarcoEdo) =
    filaJuntaEdo(r.top, g.x0, g.x1, a.luz(KURI_OSC, g.cerca(Offset(r.center.x, r.top)) * 0.5f))

/** La fila de la junta en [y], de [x0] a [x1], de [c], debajo de las dos piezas que se tocan ahí; si [y] es entero, nada. */
internal fun DrawScope.filaJuntaEdo(y: Float, x0: Float, x1: Float, c: Color) {
    val f = floor(y)
    if (f == y) return
    drawRect(c, Offset(x0, f), Size(x1 - x0, 1f))
}

/**
 * Un hueco con su hondura: al fondo, la tabla forrada con papel de caqui (shibugami), pardo rojo y lustroso, en hojas
 * encimadas con sus orillas, su brillo y algún roce; arriba, la tabla de encima vista por debajo; a los lados, las caras de
 * dentro de los postes (la de la derecha recibe la luz del ranma de día); y las sombras de la tabla de arriba, del poste y del
 * último volumen. De día, la luz del ranma aclara el fondo arriba a la izquierda.
 */
internal fun DrawScope.huecoEdo(k: Int, r: Rect, oc: Float, a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val x0 = g.x0; val x1 = g.x1
    val dT = w * 0.016f; val dI = w * 0.028f
    val fondo = Rect(x0 + dI, r.top + dT, x1 - dI, r.bottom)
    clipRect(x0, r.top, x1, r.bottom) {
        // La junta del fondo con la tabla vista por debajo (en fondo.top), que puede caer a medio píxel: debajo, esa fila.
        filaJuntaEdo(fondo.top, fondo.left, fondo.right, a.luz(KURI_OSC, g.cerca(Offset(r.center.x, r.top)) * 0.5f))
        // El fondo de papel de caqui, por tramos.
        val tramos = if (a.lumbre) 8 else 1
        for (t in 0 until tramos) {
            val xa = fondo.left + fondo.width * t / tramos; val xb = fondo.left + fondo.width * (t + 1) / tramos + 0.6f
            val p = Offset((xa + xb) / 2, r.center.y)
            drawRect(a.luz(SHIBU, g.cerca(p) * 0.85f, g.frio(p)), Offset(xa, fondo.top), Size(xb - xa, fondo.height))
        }
        if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(a.sombra(0.16f), Color.Transparent, a.sombra(0.12f)), fondo.left, fondo.right), fondo.topLeft, fondo.size)
        // Las hojas encimadas: cada una con su orilla clara y lustrosa y su sombra fina; algún roce que dejó el papel más claro.
        var x = fondo.left - w * 0.05f * azar(4300 + k, 0); var j = 0
        while (x < fondo.right && j < 14) {
            val sw = w * (0.11f + 0.12f * azar(4301 + k, j))
            val dy = fondo.height * (0.3f + 0.4f * azar(4302 + k, j))
            val p = Offset(x + sw / 2, fondo.top + dy)
            val kk = g.cerca(p) * 0.85f; val ff = g.frio(p)
            drawLine(a.luz(SHIBU_LUZ, kk, ff), Offset(x, fondo.top), Offset(x, fondo.bottom), 1.2f * u)
            drawLine(a.sombra(0.3f), Offset(x + 1.2f * u, fondo.top), Offset(x + 1.2f * u, fondo.bottom), 0.8f * u)
            drawLine(a.luz(SHIBU_LUZ, kk, ff).copy(alpha = 0.7f), Offset(x, fondo.top + dy), Offset(x + sw, fondo.top + dy + (azar(4303 + k, j) - 0.5f) * 3f * u), 1f * u)
            // El lustre del papel: una veladura alargada, en diagonal.
            if (azar(4304 + k, j) > 0.45f) {
                val y = fondo.top + fondo.height * (0.2f + 0.5f * azar(4305 + k, j))
                drawPath(poligonoEdo(x + sw * 0.2f, y, x + sw * 0.5f, y - fondo.height * 0.1f, x + sw * 0.62f, y - fondo.height * 0.06f, x + sw * 0.32f, y + fondo.height * 0.05f), a.brillo(0.1f, kk).copy(alpha = if (a.oscuro) 0.08f * kk else 0.08f))
            }
            x += sw; j++
        }
        pinceladas(fondo, a.luz(SHIBU_LUZ, g.cerca(fondo.center) * 0.8f), 4306 + k, 10, u, 0.16f)
        // De día, la luz del ranma: más clara arriba a la izquierda, pasada la sombra del poste.
        if (!a.oscuro) {
            val l = fondo.left + 12f * u
            drawPath(poligonoEdo(l, fondo.top, l + fondo.width * 0.55f, fondo.top, l + fondo.width * 0.25f, fondo.bottom, l, fondo.bottom), LUZ_MUEBLE.copy(alpha = if (a.noche) 0.05f else 0.09f))
        }
        // La tabla de arriba vista por debajo, oscura, y las caras de dentro de los postes.
        val kT = g.cerca(Offset(r.center.x, r.top))
        drawPath(poligonoEdo(x0, r.top, x1, r.top, x1 - dI, r.top + dT, x0 + dI, r.top + dT), a.luz(KURI_OSC, kT * 0.5f))
        val kI = g.cerca(Offset(x0, r.center.y)); val kD = g.cerca(Offset(x1, r.center.y))
        val izq = poligonoEdo(x0, r.top, x0 + dI, r.top + dT, x0 + dI, r.bottom, x0, r.bottom)
        val der = poligonoEdo(x1, r.top, x1 - dI, r.top + dT, x1 - dI, r.bottom, x1, r.bottom)
        drawPath(izq, a.luz(mezcla(KURI, KURI_OSC, 0.6f), kI * 0.6f, g.frio(Offset(x0, r.center.y))))
        drawPath(der, a.luz(if (a.oscuro) KURI else mezcla(KURI, KURI_LUZ, 0.35f), kD))
        clipPath(der) { vetas(Rect(x1 - dI, r.top, x1, r.bottom), a.sombra(0.3f), 4310 + k, densidad = 1.2f, vertical = true, grosor = 0.5f, ondulacion = 0.3f) }
        drawLine(a.sombra(0.45f), Offset(x0 + dI, r.top + dT), Offset(x0 + dI, r.bottom), 0.8f * u)
        drawLine(a.sombra(0.35f), Offset(x1 - dI, r.top + dT), Offset(x1 - dI, r.bottom), 0.8f * u)
        drawLine(a.sombra(0.5f), Offset(x0 + dI, r.top + dT), Offset(x1 - dI, r.top + dT), 0.8f * u)
        // La sombra de la tabla de arriba en el fondo: honda junto a ella y deshecha hacia abajo.
        val fz = if (a.oscuro) 1.15f else 1f
        drawRect(a.sombra(0.4f * fz), Offset(fondo.left, fondo.top), Size(fondo.width, 6f * u))
        drawRect(a.sombra(0.22f * fz), Offset(fondo.left, fondo.top), Size(fondo.width, 15f * u))
        drawRect(a.sombra(0.1f * fz), Offset(fondo.left, fondo.top), Size(fondo.width, 28f * u))
        // El poste de la izquierda le quita la luz a ese lado de día; de noche, la sombra está del lado contrario al farol.
        if (!a.oscuro) drawRect(a.sombra(0.26f), Offset(fondo.left, fondo.top), Size(10f * u, fondo.height))
        else drawRect(a.sombra(0.22f), Offset(fondo.left, fondo.top), Size(fondo.width * 0.3f, fondo.height))
        // La sombra que el último volumen echa en el fondo, a su derecha (de día) o a su izquierda (de noche, ya la tapa él).
        if (oc > r.left + 6f * u && !a.oscuro) {
            val largo = 12f * u
            drawPath(poligonoEdo(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.22f, oc + largo, r.top + r.height * 0.32f, oc + largo, r.bottom), a.sombra(0.26f))
        }
    }
}

// ---------------------------------------------------------------- el cuerpo bajo

/**
 * El cuerpo bajo del estante, bajo la última tabla: el bastidor de castaño con su riel de dos canales, dos puertas correderas
 * de tabla (la de la izquierda por delante) con la veta en catedral, sus tiradores de hierro hundidos, la cerradura en los
 * largueros que se juntan, las cantoneras de las cuatro esquinas, y el zócalo remetido sobre el suelo.
 */
internal fun DrawScope.cuerpoBajoEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val x0 = g.x0; val x1 = g.x1
    val top = g.fondo; val bot = g.cuerpoBajo
    val alto = bot - top
    if (alto < 4f * u) return
    // El zócalo remetido, entre el cuerpo y el suelo, en sombra.
    val kz = g.cerca(Offset(w * 0.5f, bot))
    drawRect(a.luz(KURI_OSC, kz * 0.6f), Offset(x0, bot), Size(x1 - x0, g.pisan - bot))
    drawRect(a.sombra(0.5f), Offset(x0, bot), Size(x1 - x0, 3f * u))
    // El bastidor: el travesaño de arriba (bajo la última tabla) y el riel de abajo.
    val riel = w * 0.024f; val tope = w * 0.016f
    for (t in 0 until 10) {
        val xa = x0 + (x1 - x0) * t / 10f; val xb = x0 + (x1 - x0) * (t + 1) / 10f + 0.6f
        val k = g.cerca(Offset((xa + xb) / 2, top + alto / 2))
        drawRect(a.luz(KURI, k, 0f, g.brasaEn(Offset((xa + xb) / 2, bot)) / (k + 0.001f)), Offset(xa, top), Size(xb - xa, alto))
    }
    drawRect(a.sombra(0.35f), Offset(x0, top + tope), Size(x1 - x0, 2f * u))
    drawLine(a.sombra(0.45f), Offset(x0, bot - riel), Offset(x1, bot - riel), 1f * u)
    drawLine(a.sombra(0.35f), Offset(x0, bot - riel * 0.5f), Offset(x1, bot - riel * 0.5f), 0.8f * u)
    drawRect(a.claro(0.4f), Offset(x0, bot - riel + 1f * u), Size(x1 - x0, 0.8f * u))
    // Las dos puertas: la de la derecha va en el canal de atrás (un poco más honda), la de la izquierda la monta por delante.
    val medio = (x0 + x1) / 2
    val solapa = w * 0.02f
    val y0 = top + tope + 2f * u; val y1 = bot - riel
    puertaEdo(Rect(medio - solapa, y0, x1 - 2f * u, y1), a, g, 4400, false)
    puertaEdo(Rect(x0 + 2f * u, y0, medio + solapa, y1), a, g, 4410, true)
    // Las cantoneras de hierro de las cuatro esquinas del cuerpo.
    val l = w * 0.05f; val gg = w * 0.018f
    cantoneraEdo(Offset(x0, top), 1f, 1f, l, gg, a, g.cerca(Offset(x0, top)))
    cantoneraEdo(Offset(x1, top), -1f, 1f, l, gg, a, g.cerca(Offset(x1, top)))
    cantoneraEdo(Offset(x0, bot), 1f, -1f, l, gg, a, g.cerca(Offset(x0, bot)))
    cantoneraEdo(Offset(x1, bot), -1f, -1f, l, gg, a, g.cerca(Offset(x1, bot)))
}

/**
 * Una puerta corredera de tabla en [r]: el marco de largueros y peinazos, la tabla de dentro con la veta en catedral (arcos
 * apuntados unos dentro de otros), el tirador de hierro hundido y, en la de delante ([delante]), la cerradura en el larguero.
 */
private fun DrawScope.puertaEdo(r: Rect, a: AireEdo, g: MarcoEdo, semilla: Int, delante: Boolean) {
    val u = density; val w = g.w
    val k = g.cerca(r.center); val br = g.brasaEn(Offset(r.center.x, r.bottom)) / (k + 0.001f)
    val marco = w * 0.016f
    // La sombra que la de delante echa sobre la de atrás.
    if (delante) drawRect(a.sombra(0.4f), Offset(r.right, r.top), Size(4f * u, r.height))
    drawRect(a.luz(if (delante) KURI else mezcla(KURI, KURI_OSC, 0.2f), k, 0f, br), r.topLeft, r.size)
    val tabla = Rect(r.left + marco, r.top + marco, r.right - marco, r.bottom - marco)
    drawRect(a.luz(mezcla(KURI, KURI_LUZ, 0.25f), k, 0f, br), tabla.topLeft, tabla.size)
    clipRect(tabla.left, tabla.top, tabla.right, tabla.bottom) {
        // La veta en catedral: arcos apuntados, unos dentro de otros, que suben desde abajo.
        val veta = a.luz(VETA, k).copy(alpha = if (a.oscuro) 0.4f else 0.38f)
        val cx = tabla.left + tabla.width * (0.4f + 0.2f * azar(semilla, 1))
        for (j in 0 until 9) {
            val an = tabla.width * (0.08f + 0.1f * j)
            val alto = tabla.height * (0.4f + 0.12f * j)
            val p = Path().apply {
                moveTo(cx - an, tabla.bottom)
                quadraticTo(cx - an * 0.9f, tabla.bottom - alto * 0.7f, cx, tabla.bottom - alto)
                quadraticTo(cx + an * 0.9f, tabla.bottom - alto * 0.7f, cx + an, tabla.bottom)
            }
            drawPath(p, veta, style = Stroke((0.6f + 0.5f * azar(semilla, 10 + j)) * u))
        }
        vetas(tabla, veta.copy(alpha = veta.alpha * 0.5f), semilla + 2, densidad = 0.5f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
        if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(LUZ_MUEBLE.copy(alpha = 0.16f), Color.Transparent, a.sombra(0.14f)), tabla.left, tabla.right), tabla.topLeft, tabla.size)
    }
    // El rebaje de la tabla dentro del marco: la sombra arriba y a la izquierda, la luz abajo y a la derecha.
    drawRect(a.sombra(0.4f), tabla.topLeft, Size(tabla.width, 1.6f * u))
    drawRect(a.sombra(0.3f), tabla.topLeft, Size(1.6f * u, tabla.height))
    drawRect(a.brillo(0.35f, k), Offset(tabla.left, tabla.bottom - 1f * u), Size(tabla.width, 1f * u))
    vetas(Rect(r.left, r.top, r.left + marco, r.bottom), a.luz(VETA, k).copy(alpha = 0.35f), semilla + 3, densidad = 1.2f, vertical = true, grosor = 0.5f, ondulacion = 0.3f)
    drawRect(a.claro(0.4f), r.topLeft, Size(1f * u, r.height))
    drawRect(a.sombra(0.4f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
    // El tirador de hierro hundido, cerca del larguero de fuera (por donde se empuja cada puerta).
    val tx = if (delante) r.left + w * 0.07f else r.right - w * 0.07f
    val tc = Offset(tx, r.center.y)
    val tr = minOf(r.height * 0.13f, w * 0.022f)
    drawCircle(a.sombra(0.4f), tr * 1.08f, tc + Offset(0.8f * u, 1f * u))
    drawCircle(a.luz(HIERRO, k), tr, tc)
    drawCircle(a.luz(mezcla(HIERRO, SUMI, 0.6f), k), tr * 0.68f, tc + Offset(tr * 0.06f, tr * 0.08f))
    drawArc(a.brillo(0.45f, k), 200f, 110f, false, Offset(tc.x - tr * 0.92f, tc.y - tr * 0.92f), Size(tr * 1.84f, tr * 1.84f), style = Stroke(0.8f * u))
    drawArc(a.brillo(0.3f, k), 20f, 100f, false, Offset(tc.x - tr * 0.6f, tc.y - tr * 0.6f), Size(tr * 1.2f, tr * 1.2f), style = Stroke(0.7f * u))
    if (delante) {
        // La cerradura: una chapa de hierro redondeada con su bocallave, en el larguero.
        val ch = Rect(r.right - marco * 1.4f, r.center.y - r.height * 0.2f, r.right + marco * 0.4f, r.center.y + r.height * 0.2f)
        val p = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(ch, androidx.compose.ui.geometry.CornerRadius(ch.width * 0.4f))) }
        chapaEdo(p, a, k)
        val bc = Offset(ch.center.x, ch.center.y)
        drawCircle(a.luz(SUMI, k * 0.4f), ch.width * 0.16f, bc - Offset(0f, ch.width * 0.1f))
        drawRect(a.luz(SUMI, k * 0.4f), Offset(bc.x - ch.width * 0.07f, bc.y - ch.width * 0.05f), Size(ch.width * 0.14f, ch.width * 0.36f))
        remacheEdo(Offset(ch.center.x, ch.top + ch.width * 0.35f), ch.width * 0.1f, a, k)
        remacheEdo(Offset(ch.center.x, ch.bottom - ch.width * 0.35f), ch.width * 0.1f, a, k)
    }
}

// ---------------------------------------------------------------- los rótulos

/** El rótulo de un mes: una tablilla de hinoki nuevo, con las esquinas cortadas, clavada en el canto de la tabla. */
internal fun DrawScope.rotuloEdo(p: PlacaEstante, medidor: TextMeasurer, a: AireEdo, k: Float) {
    val u = density
    val r = Rect(p.r.left, p.r.top - 0.6f * u, p.r.right, p.r.bottom + 0.6f * u)
    val c = minOf(r.height * 0.3f, 3f * u)
    val forma = poligonoEdo(r.left + c, r.top, r.right - c, r.top, r.right, r.top + c, r.right, r.bottom - c, r.right - c, r.bottom, r.left + c, r.bottom, r.left, r.bottom - c, r.left, r.top + c)
    drawPath(corridoEdo(forma, 1.2f * u, 1.6f * u), a.sombra(0.45f))
    drawPath(forma, a.etiqueta(maxOf(k, 0.25f), mezcla(HINOKI, WASHI, 0.5f)))
    clipRect(r.left, r.top, r.right, r.bottom) {
        vetas(r, a.luz(VETA, 0.6f).copy(alpha = 0.2f), 4500 + p.texto.length, densidad = 0.6f, grosor = 0.5f, ondulacion = 0.3f)
    }
    drawRect(a.claro(0.5f), Offset(r.left + c, r.top + 0.4f * u), Size(r.width - 2 * c, 0.8f * u))
    drawRect(a.sombra(0.18f), Offset(r.left + c, r.bottom - 1.2f * u), Size(r.width - 2 * c, 1.2f * u))
    // Los dos clavos de hierro, con su punto de luz.
    listOf(r.left + 4f * u, r.right - 4f * u).forEach { x -> remacheEdo(Offset(x, r.center.y), 1.3f * u, a, maxOf(k, 0.3f)) }
    textoEn(medidor, p.texto, Rect(r.left + 8f * u, r.top + 1.2f * u, r.right - 8f * u, r.bottom - 1.2f * u), MuebleEstanteEdo.LETRA, a.tinta, 10.5f, peso = FontWeight.Bold, minimo = 5f)
}

/** Una raya de luz cálida en un canto que mira al farol (de noche). */
internal fun DrawScope.filoFarolEdo(desde: Offset, hasta: Offset, k: Float, a: AireEdo) {
    if (a.oscuro && k > 0.05f) drawLine(a.brillo(0.8f, k), desde, hasta, 1.1f * density, StrokeCap.Round)
}
