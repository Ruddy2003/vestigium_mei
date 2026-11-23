package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.floor

/**
 * La alacena de alerce del lado de la dueña (el estante de Mongol): el fondo ahumado de cada casillero con sus juntas, sus
 * costados de dentro y la sombra de la tabla de arriba; las tablas gruesas con el canto pintado de bermellón entre filetes de
 * oro, sus clavos y lo gastado; la tablilla de abedul de cada mes clavada en el canto; los costados con su chaflán, su
 * entrecalle y las escuadras de hierro; la cornisa volada con su moldura, su caveto y el friso de añil con rombos
 * escalonados; y abajo, el cuerpo cerrado de dos portezuelas pintadas con sus argollas, sus pernios y sus pies.
 */
internal object AlacenaEstanteMongol {

    // ---------------------------------------------------------------- los casilleros

    /** El fondo de cada casillero (va primero; la corona se pega encima). */
    fun DrawScope.cuerpo(g: MarcoMongol, d: DisposicionEstante) {
        d.repisas.forEachIndexed { k, r -> casillero(k, r, g) }
    }

    private fun DrawScope.casillero(k: Int, r0: Rect, g: MarcoMongol) {
        val u = density
        val w = g.w
        val a = g.a
        // El primero arranca en el píxel entero de arriba: así la junta con la corona no deja ver el fondo.
        val r = Rect(g.x0 - 2f, if (k == 0) floor(r0.top) else r0.top - 1f, g.x1 + 2f, r0.bottom + 1f)
        val l = g.luz(r.center)
        // Las tablas ahumadas del fondo, de arriba abajo, cada una de su tono, con sus juntas. La sombra va ya en el tono de cada
        // tabla (un solo degradado por tabla, sin capas encima): de día, la de la tabla de arriba, honda arriba y que se abre;
        // de noche, el hogar alumbra desde abajo y delante: lo alto encendido desde abajo y el pie en la sombra del canto.
        val tablas = 7
        val anchoT = (g.x1 - g.x0) / tablas
        for (i in 0 until tablas) {
            val x = g.x0 + anchoT * i
            val lx = g.luz(Offset(x + anchoT / 2, r.center.y))
            val c0 = lx.c(mezcla(P.HOLLIN, P.HOLLIN_LUZ, 0.15f + 0.45f * azar(600 + k, i)))
            // Cada tabla pisa 1.5 px la siguiente: la junta puede caer a medio píxel y, si no, asomaría el fondo en una línea.
            drawRect(c0, Offset(if (i == 0) r.left else x, r.top), Size(if (i == 0) anchoT + 3.5f else if (i == tablas - 1) anchoT + 2f else anchoT + 1.5f, r.height))
            if (i % 2 == 0) vetas(Rect(x, r.top, x + anchoT, r.bottom), lx.c(P.ALERCE_OSC).copy(alpha = 0.3f), 610 + k * 7 + i, 0.35f, vertical = true, grosor = 0.8f)
            if (i > 0) {
                drawLine(lx.s(0.6f), Offset(x, r.top), Offset(x, r.bottom), 1.3f * u)
                drawLine(lx.b(0.12f), Offset(x + 1.3f * u, r.top), Offset(x + 1.3f * u, r.bottom), 0.7f * u)
            }
        }
        // La sombra: de día, la de la tabla de arriba (honda arriba, que se abre) y la del costado izquierdo; de noche, lo alto
        // encendido desde abajo y el pie en la sombra del canto de la tabla.
        val alto = r0.height
        if (!a.oscuro) {
            drawRect(Brush.verticalGradient(listOf(l.s(0.55f), l.s(0.2f), Color.Transparent), r.top, r.top + alto * 0.36f), Offset(r.left, r.top), Size(r.width, alto * 0.36f))
            drawRect(Brush.horizontalGradient(listOf(l.s(0.4f), Color.Transparent), g.x0, g.x0 + w * 0.06f), Offset(g.x0, r.top), Size(w * 0.06f, r.height))
        } else {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.s(0.5f)), r.bottom - alto * 0.24f, r.bottom), Offset(r.left, r.bottom - alto * 0.24f), Size(r.width, alto * 0.24f))
            drawRect(Brush.verticalGradient(listOf(l.filo(0.3f), Color.Transparent), r0.top, r0.top + alto * 0.18f), Offset(g.x0, r0.top), Size(g.x1 - g.x0, alto * 0.18f))
        }
        // La cara de abajo de la tabla de arriba (se ve desde abajo), con su filo.
        val bajoTabla = w * 0.011f
        drawRect(l.c(mezcla(P.ALERCE_OSC, P.HOLLIN, 0.3f)), Offset(g.x0, r0.top), Size(g.x1 - g.x0, bajoTabla))
        drawRect(if (a.oscuro) l.filo(0.6f) else l.s(0.3f), Offset(g.x0, r0.top + bajoTabla - 1f * u), Size(g.x1 - g.x0, 1f * u))
        // Los costados de dentro: el de la izquierda en sombra, el de la derecha a la luz (de noche, al revés: el hogar está
        // a la derecha).
        val cd = w * 0.016f
        val izq = poligonoMongol(g.x0, r0.top, g.x0 + cd, r0.top + bajoTabla, g.x0 + cd, r0.bottom - w * 0.012f, g.x0, r0.bottom)
        val der = poligonoMongol(g.x1, r0.top, g.x1 - cd, r0.top + bajoTabla, g.x1 - cd, r0.bottom - w * 0.012f, g.x1, r0.bottom)
        val li = g.luz(Offset(g.x0, r0.center.y)); val ld = g.luz(Offset(g.x1, r0.center.y))
        drawPath(izq, li.c(mezcla(P.ALERCE, P.ALERCE_OSC, if (a.oscuro) 0.75f else 0.55f)))
        drawPath(der, ld.c(mezcla(P.ALERCE, P.ALERCE_LUZ, if (a.oscuro) 0f else 0.25f)))
        if (a.oscuro) drawPath(der, ld.filo(0.4f))
        // La cara de arriba de la tabla de este casillero, donde pisan las entradas (se ve desde arriba).
        val cara = w * 0.014f
        val lc = g.luz(Offset(w * 0.5f, r0.bottom))
        drawRect(lc.c(mezcla(P.ALERCE, P.ALERCE_LUZ, 0.35f)), Offset(g.x0, r0.bottom - cara), Size(g.x1 - g.x0, cara))
        vetas(Rect(g.x0, r0.bottom - cara, g.x1, r0.bottom), lc.c(P.ALERCE_OSC).copy(alpha = 0.3f), 630 + k, 0.6f, grosor = 0.7f)
        drawRect(lc.s(0.45f), Offset(g.x0, r0.bottom - cara), Size(g.x1 - g.x0, 1.2f * u))
        if (a.oscuro) drawRect(lc.filo(0.5f), Offset(g.x0, r0.bottom - 1.2f * u), Size(g.x1 - g.x0, 1.2f * u))
    }

    // ---------------------------------------------------------------- las tablas y los rótulos

    /** El canto de una tabla: alerce con su filo de luz, la banda pintada de bermellón entre filetes de oro, los clavos y lo gastado. */
    fun DrawScope.tabla(k: Int, r: Rect, g: MarcoMongol) {
        val u = density
        val w = g.w
        val a = g.a
        val t = Rect(r.left - 1f, r.top, r.right + 1f, r.bottom)
        val l = g.luz(t.center)
        // Debajo, la fila de la junta con el casillero de abajo, entera.
        drawRect(l.c(P.ALERCE_OSC), Offset(t.left, floor(t.bottom) - 1f), Size(t.width, 2f))
        if (a.oscuro) {
            drawRect(Brush.horizontalGradient(List(5) { i -> g.luz(Offset(t.left + t.width * i / 4f, t.center.y)).c(P.ALERCE) }, t.left, t.right), t.topLeft, t.size)
        } else drawRect(l.c(P.ALERCE), t.topLeft, t.size)
        vetas(t, l.c(P.ALERCE_OSC).copy(alpha = 0.45f), 640 + k, 0.9f, grosor = 0.8f)
        // La banda pintada.
        val b0 = t.top + t.height * 0.3f; val b1 = t.top + t.height * 0.74f
        if (a.oscuro) {
            drawRect(Brush.horizontalGradient(List(5) { i -> g.luz(Offset(t.left + t.width * i / 4f, t.center.y)).c(P.BERMELLON) }, t.left, t.right), Offset(t.left, b0), Size(t.width, b1 - b0))
        } else drawRect(l.c(P.BERMELLON), Offset(t.left, b0), Size(t.width, b1 - b0))
        pinceladas(Rect(t.left, b0, t.right, b1), l.c(P.BERMELLON_OSC), 650 + k, 10, u, 0.18f)
        drawRect(l.c(P.ORO), Offset(t.left, b0 - 1.2f * u), Size(t.width, 1.1f * u))
        drawRect(l.c(P.ORO), Offset(t.left, b1), Size(t.width, 1.1f * u))
        // Lo gastado de la pintura: asoma el alerce donde se apoyan las manos y donde rozan las cosas.
        for (q in 0 until 4) {
            val x = t.left + t.width * (0.08f + 0.84f * azar(660 + k, q))
            val ancho = w * (0.012f + 0.02f * azar(661 + k, q))
            drawOval(l.c(mezcla(P.ALERCE, P.ALERCE_LUZ, 0.3f)).copy(alpha = 0.8f), Offset(x, b0 + (b1 - b0) * 0.2f * azar(662 + k, q)), Size(ancho, (b1 - b0) * (0.35f + 0.4f * azar(663 + k, q))))
        }
        // Los clavos de forja, en la banda, cada tanto.
        var x = t.left + w * 0.05f
        while (x < t.right - w * 0.03f) {
            clavoMongol(Offset(x, (b0 + b1) / 2), 1.6f * u, g.luz(Offset(x, t.center.y)))
            x += w * 0.16f
        }
        // El filo: la luz arriba (de noche, la sombra: el hogar alumbra de abajo), la sombra abajo.
        if (!a.oscuro) {
            drawRect(l.b(0.6f), Offset(t.left, t.top), Size(t.width, 1.5f * u))
            drawRect(l.s(0.45f), Offset(t.left, t.bottom - 1.6f * u), Size(t.width, 1.6f * u))
        } else {
            drawRect(l.s(0.4f), Offset(t.left, t.top), Size(t.width, 1.5f * u))
            drawRect(Brush.horizontalGradient(List(5) { i -> g.luz(Offset(t.left + t.width * i / 4f, t.bottom)).filo(0.8f) }, t.left, t.right), Offset(t.left, t.bottom - 1.6f * u), Size(t.width, 1.6f * u))
        }
    }

    /** La tablilla de abedul de cada mes, clavada en el canto de la tabla: corteza con sus lenticelas, dos clavos y el rótulo. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, g: MarcoMongol) {
        val u = density
        val w = g.w
        val ancho = (g.x1 - g.x0) * 0.74f
        val r = Rect(w / 2 - ancho / 2, p.r.top - 1.2f * u, w / 2 + ancho / 2, p.r.bottom + 1.2f * u)
        val l = g.luz(r.center)
        val forma = Path().apply {
            moveTo(r.left + 3f * u, r.top); lineTo(r.right - 5f * u, r.top + 0.6f * u)
            lineTo(r.right, r.top + r.height * 0.3f); lineTo(r.right - 1.5f * u, r.bottom - 0.5f * u)
            lineTo(r.left + 4f * u, r.bottom); lineTo(r.left, r.bottom - r.height * 0.4f); lineTo(r.left + 1.2f * u, r.top + 2f * u); close()
        }
        drawPath(corridoMongol(forma, 1.6f * u, 1.8f * u), l.s(0.45f))
        val fondo = l.etiqueta()
        drawPath(forma, fondo)
        clipPath(forma) {
            // Las lenticelas de la corteza, hacia los extremos (lejos de las letras), y la corteza que se levanta en un canto.
            for (q in 0 until 10) {
                val lado = if (q % 2 == 0) r.left + r.width * (0.02f + 0.1f * azar(670, q)) else r.right - r.width * (0.02f + 0.1f * azar(671, q))
                val y = r.top + r.height * (0.15f + 0.7f * azar(672, q))
                drawLine(l.c(P.LENTICELA).copy(alpha = 0.55f), Offset(lado, y), Offset(lado + w * (0.008f + 0.01f * azar(673, q)), y), 0.9f * u)
            }
            drawRect(l.c(P.ABEDUL_OSC).copy(alpha = 0.6f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
        }
        drawPath(forma, l.s(0.35f), style = Stroke(0.8f * u))
        listOf(r.left + 5f * u, r.right - 5f * u).forEach { x -> clavoMongol(Offset(x, r.center.y), 1.5f * u, l) }
        textoEn(medidor, p.texto, Rect(r.left + 10f * u, r.top + 0.5f * u, r.right - 10f * u, r.bottom - 0.5f * u), P.LETRA,
            mezcla(P.TINTA, g.a.hondo, if (g.a.oscuro) 0.1f else 0f), 9.5f, minimo = 5.5f)
    }

    // ---------------------------------------------------------------- los costados

    /**
     * Los dos costados: tablones de alerce con su chaflán a la luz, la entrecalle de en medio con su filete de oro, la veta, y en
     * cada tabla la escuadra de hierro forjado con sus clavos. De noche el de la derecha se enciende desde abajo.
     */
    fun DrawScope.costados(g: MarcoMongol, d: DisposicionEstante) {
        listOf(d.izquierda, d.derecha).forEachIndexed { i, r0 ->
            val r = Rect(r0.left, floor(r0.top), r0.right, r0.bottom + 1f)
            costado(r, i == 1, g, d)
        }
    }

    private fun DrawScope.costado(r: Rect, derecho: Boolean, g: MarcoMongol, d: DisposicionEstante) {
        val u = density
        val w = g.w
        val a = g.a
        // Por tramos de color lleno, cada uno con la luz de su altura (de noche cambia mucho de arriba abajo); los tramos se
        // solapan un píxel. Es mucho más barato de pintar que degradados de todo el alto.
        val ent = Rect(r.left + r.width * 0.3f, r.top + w * 0.02f, r.right - r.width * 0.3f, r.bottom - w * 0.02f)
        val ch = r.width * 0.12f
        val luzIzq = !a.oscuro
        val tramo = maxOf(8f, w * 0.05f)
        var y = r.top
        while (y < r.bottom) {
            val y1 = minOf(r.bottom, y + tramo)
            val l = g.luz(Offset(r.center.x, (y + y1) / 2))
            val alto = y1 - y + 1f
            drawRect(l.c(P.ALERCE), Offset(r.left, y), Size(r.width, alto))
            val e0 = maxOf(y, ent.top); val e1 = minOf(y1, ent.bottom)
            if (e1 > e0) {
                drawRect(l.c(mezcla(P.ALERCE, P.ALERCE_OSC, 0.35f)), Offset(ent.left, e0), Size(ent.width, e1 - e0 + 1f))
                drawRect(l.c(P.ORO), Offset(ent.left - 1.4f * u, e0), Size(1f * u, e1 - e0 + 1f))
                drawRect(l.c(P.ORO), Offset(ent.right + 0.4f * u, e0), Size(1f * u, e1 - e0 + 1f))
            }
            drawRect(l.b(0.5f), Offset(if (luzIzq) r.left else r.right - ch, y), Size(ch, alto))
            drawRect(l.s(0.35f), Offset(if (luzIzq) r.right - ch else r.left, y), Size(ch, alto))
            if (a.oscuro && derecho) drawRect(l.filo(0.7f), Offset(r.left, y), Size(1.4f * u, alto))
            y = y1
        }
        vetas(r, g.luz(r.center).c(P.ALERCE_OSC).copy(alpha = 0.5f), if (derecho) 681 else 680, 0.55f, vertical = true, grosor = 0.9f)
        drawRect(g.luz(ent.center).s(0.4f), Offset(if (a.oscuro && derecho) ent.left else ent.right - 1.6f * u, ent.top), Size(1.6f * u, ent.height))
        // La sombra del costado en la pared de detrás, del lado contrario a la luz (asoma en el borde del lienzo).
        if (!derecho && !a.oscuro) drawRect(g.luz(r.center).s(0.25f), Offset(r.right, r.top), Size(1.4f * u, r.height))
        // Las escuadras de hierro forjado en cada tabla, con sus clavos.
        d.tablas.forEach { t ->
            val yc = t.center.y
            val lx = g.luz(Offset(r.center.x, yc))
            val e = Path().apply {
                val x0 = if (derecho) r.left + r.width * 0.12f else r.right - r.width * 0.12f
                val s = if (derecho) 1f else -1f
                val largo = r.width * 0.7f
                val g2 = w * 0.012f
                moveTo(x0, yc - g2); lineTo(x0 + s * largo, yc - g2 * 0.8f); lineTo(x0 + s * (largo + g2 * 0.6f), yc)
                lineTo(x0 + s * largo, yc + g2 * 0.8f); lineTo(x0, yc + g2); close()
            }
            drawPath(corridoMongol(e, 1.2f * u, 1.4f * u), lx.s(0.45f))
            drawPath(e, lx.c(P.HIERRO))
            drawPath(e, lx.b(0.25f), style = Stroke(0.8f * u))
            val b = e.getBounds()
            listOf(0.25f, 0.6f).forEach { f -> clavoMongol(Offset(b.left + b.width * f, yc), 1.5f * u, lx, P.HIERRO_LUZ) }
        }
    }

    // ---------------------------------------------------------------- la cornisa

    /**
     * La cornisa volada, de [g.apoyo] a [g.ch]: la cara de arriba (donde pisa el bodegón), el canto de la tabla de arriba con su
     * bocel, el caveto en sombra, el friso de añil con los rombos escalonados de bermellón y crema entre filetes de oro, y el
     * junquillo rojo de abajo. Gastada en los cantos.
     */
    fun DrawScope.cornisa(g: MarcoMongol) {
        val u = density
        val w = g.w
        val a = g.a
        val y0 = g.apoyo
        val l = g.luz(Offset(w * 0.5f, y0 + w * 0.05f))
        // La cara de arriba, vista desde arriba.
        val cara = Rect(0f, y0 - w * 0.016f, w, y0 + 1f)
        drawRect(l.c(mezcla(P.ALERCE, P.ALERCE_LUZ, 0.45f)), cara.topLeft, cara.size)
        vetas(cara, l.c(P.ALERCE_OSC).copy(alpha = 0.3f), 690, 0.8f, grosor = 0.7f)
        drawRect(l.s(0.3f), Offset(0f, cara.top), Size(w, 1.2f * u))
        // El canto de la tabla de arriba, con su bocel: filo de luz, la media caña y la sombra de abajo.
        val canto = Rect(0f, y0, w, y0 + w * 0.032f)
        drawRect(l.c(P.ALERCE), canto.topLeft, canto.size)
        vetas(canto, l.c(P.ALERCE_OSC).copy(alpha = 0.45f), 691, 0.9f, grosor = 0.8f)
        drawRect(l.b(0.6f), canto.topLeft, Size(w, 1.6f * u))
        drawRect(l.s(0.35f), Offset(0f, canto.top + canto.height * 0.45f), Size(w, 1.2f * u))
        drawRect(l.b(0.25f), Offset(0f, canto.top + canto.height * 0.45f + 1.2f * u), Size(w, 1f * u))
        drawRect(l.s(0.45f), Offset(0f, canto.bottom - 1.6f * u), Size(w, 1.6f * u))
        // El caveto, bajo el vuelo: en sombra arriba, más claro abajo.
        val cav = Rect(w * 0.008f, canto.bottom, w * 0.992f, canto.bottom + w * 0.018f)
        drawRect(l.c(mezcla(P.ALERCE, P.ALERCE_OSC, 0.4f)), cav.topLeft, cav.size)
        drawRect(Brush.verticalGradient(listOf(l.s(0.55f), Color.Transparent), cav.top, cav.bottom), cav.topLeft, cav.size)
        // El friso de añil.
        val fr = Rect(w * 0.014f, cav.bottom, w * 0.986f, g.ch - w * 0.016f)
        drawRect(l.c(P.ANIL), fr.topLeft, fr.size)
        pinceladas(fr, l.c(P.ANIL_LUZ), 692, 14, u, 0.14f)
        drawRect(l.c(P.ORO), Offset(fr.left, fr.top + 1.6f * u), Size(fr.width, 1.1f * u))
        drawRect(l.c(P.ORO), Offset(fr.left, fr.bottom - 2.7f * u), Size(fr.width, 1.1f * u))
        // Los rombos escalonados, alternados con dos puntos de oro (ningún signo: solo escalones y puntos).
        val rr = (fr.height - 7f * u) * 0.48f
        val paso = rr * 3.4f
        var x = fr.left + paso * 0.55f
        var q = 0
        while (x < fr.right - rr) {
            val c = Offset(x, fr.center.y)
            val lr = g.luz(c)
            escalonado(c, rr, lr, if (q % 2 == 0) P.BERMELLON else P.CREMA, if (q % 2 == 0) P.CREMA else P.BERMELLON)
            listOf(-1f, 1f).forEach { s -> drawCircle(lr.c(P.ORO), 1.4f * u, Offset(x + s * paso * 0.5f, fr.center.y)) }
            x += paso; q++
        }
        // Lo gastado del friso: la pintura saltada que deja ver el alerce.
        for (k in 0 until 5) {
            val o = Offset(fr.left + fr.width * azar(693, k), fr.top + fr.height * (0.2f + 0.6f * azar(694, k)))
            drawOval(l.c(P.ALERCE).copy(alpha = 0.75f), Offset(o.x, o.y), Size(w * (0.008f + 0.014f * azar(695, k)), w * 0.006f))
        }
        drawRect(l.s(0.35f), Offset(fr.left, fr.top), Size(fr.width, 1.4f * u))
        // El junquillo rojo de abajo, con su filete y su sombra en el primer casillero.
        val jq = Rect(0f, fr.bottom, w, g.ch + 1f)
        drawRect(l.c(P.BERMELLON), jq.topLeft, jq.size)
        drawRect(l.b(0.45f), jq.topLeft, Size(w, 1.2f * u))
        drawRect(l.c(P.ORO), Offset(0f, jq.center.y), Size(w, 1f * u))
        drawRect(l.s(0.4f), Offset(0f, jq.bottom - 1.4f * u), Size(w, 1.4f * u))
        if (a.oscuro) drawRect(l.filo(0.4f), Offset(0f, jq.bottom - 1.4f * u), Size(w, 1.4f * u))
    }

    /** Un rombo escalonado pintado (escalones chicos e iguales: se lee rombo), de [fondo], con su hueco de [hueco] y su contorno. */
    internal fun DrawScope.escalonado(c: Offset, r: Float, l: LuzMongol, fondo: Color, hueco: Color) {
        val u = density
        val giro = romboEscalonadoMongol(c, r, 3)
        drawPath(corridoMongol(giro, 0.8f * u, 0.9f * u), l.s(0.35f))
        drawPath(giro, l.c(fondo))
        val dentro = poligonoMongol(c.x, c.y - r * 0.36f, c.x + r * 0.36f, c.y, c.x, c.y + r * 0.36f, c.x - r * 0.36f, c.y)
        drawPath(dentro, l.c(hueco))
        drawPath(giro, l.c(mezcla(P.ORO, P.ALERCE_OSC, 0.3f)).copy(alpha = 0.7f), style = Stroke(0.7f * u))
    }

    // ---------------------------------------------------------------- el cuerpo cerrado de abajo

    /**
     * El cuerpo cerrado de abajo, de [g.base] a [g.suelo]: el larguero de arriba con su banda pintada, dos portezuelas de alerce
     * con su tablero pintado de añil (un rombo escalonado de bermellón con su orla de puntos), el montante de en medio, las
     * argollas de hierro, los pernios forjados de los lados, el zócalo y los pies cortos que pisan el fieltro.
     */
    fun DrawScope.cuerpoBajo(g: MarcoMongol) {
        val u = density
        val w = g.w
        val a = g.a
        val y0 = floor(g.base); val y1 = g.suelo
        if (y1 - y0 < 4f) return
        val pies = w * 0.03f
        val caja = Rect(0f, y0, w, y1 - pies)
        val l = g.luz(caja.center)
        fun grad(c: Color): Brush = if (!a.oscuro) androidx.compose.ui.graphics.SolidColor(l.c(c))
            else Brush.horizontalGradient(List(4) { i -> g.luz(Offset(w * i / 3f, caja.center.y)).c(c) }, 0f, w)
        drawRect(grad(P.ALERCE), caja.topLeft, caja.size)
        vetas(caja, l.c(P.ALERCE_OSC).copy(alpha = 0.45f), 700, 0.7f, grosor = 0.9f)
        // El larguero de arriba, con su banda pintada (sigue a las tablas).
        val lar = Rect(0f, y0, w, y0 + w * 0.032f)
        drawRect(grad(P.BERMELLON), Offset(0f, lar.top + lar.height * 0.28f), Size(w, lar.height * 0.46f))
        drawRect(l.c(P.ORO), Offset(0f, lar.top + lar.height * 0.28f - 1.1f * u), Size(w, 1f * u))
        drawRect(l.c(P.ORO), Offset(0f, lar.top + lar.height * 0.74f), Size(w, 1f * u))
        drawRect(if (a.oscuro) l.s(0.35f) else l.b(0.55f), Offset(0f, lar.top), Size(w, 1.4f * u))
        // Las dos portezuelas, con su montante en medio.
        val mx = w * 0.5f
        val p0 = lar.bottom + w * 0.008f; val p1 = caja.bottom - w * 0.022f
        listOf(Rect(g.x0 + w * 0.004f, p0, mx - w * 0.022f, p1), Rect(mx + w * 0.022f, p0, g.x1 - w * 0.004f, p1)).forEachIndexed { i, pr ->
            portezuela(pr, i == 1, g)
        }
        // El montante de en medio y los costados (siguen a los de arriba).
        val lm = g.luz(Offset(mx, caja.center.y))
        drawRect(lm.c(P.ALERCE), Offset(mx - w * 0.02f, p0 - w * 0.008f), Size(w * 0.04f, p1 - p0 + w * 0.016f))
        drawRect(lm.b(0.35f), Offset(mx - w * 0.02f, p0), Size(1.4f * u, p1 - p0))
        drawRect(lm.s(0.35f), Offset(mx + w * 0.02f - 1.4f * u, p0), Size(1.4f * u, p1 - p0))
        // El zócalo, con su moldura y su sombra.
        val zo = Rect(0f, p1 + w * 0.004f, w, caja.bottom)
        drawRect(grad(mezcla(P.ALERCE, P.ALERCE_OSC, 0.4f)), zo.topLeft, zo.size)
        drawRect(if (a.oscuro) l.filo(0.5f) else l.b(0.4f), zo.topLeft, Size(w, 1.2f * u))
        drawRect(l.s(0.4f), Offset(0f, zo.top + zo.height * 0.5f), Size(w, 1f * u))
        // Lo de debajo del mueble, entre los pies: el hueco en sombra (hondo hacia atrás), de punta a punta y solapado con la caja
        // y con el suelo, para que ninguna junta deje ver el fondo.
        val bajo = g.luz(Offset(w * 0.5f, y1))
        drawRect(bajo.c(mezcla(P.HOLLIN, P.FIELTRO_GRIS, 0.25f)), Offset(0f, caja.bottom - 2f), Size(w, y1 - caja.bottom + 4f))
        drawRect(bajo.s(0.5f), Offset(0f, caja.bottom - 2f), Size(w, (y1 - caja.bottom) * 0.5f + 2f))
        // Los pies cortos y su sombra en el fieltro.
        listOf(w * 0.01f, w * 0.47f, w * 0.92f).forEach { x ->
            val lp = g.luz(Offset(x, y1))
            drawRect(lp.s(0.45f), Offset(x + w * 0.02f, y1 - w * 0.004f), Size(w * 0.08f, w * 0.008f))
            drawRect(lp.c(P.ALERCE_OSC), Offset(x, caja.bottom - 1f), Size(w * 0.07f, pies + 1f))
            drawRect(lp.b(0.3f), Offset(x, caja.bottom), Size(1.4f * u, pies))
        }
        drawRect(l.s(0.5f), Offset(0f, caja.bottom - 1.4f * u), Size(w, 1.4f * u))
    }

    /** Una portezuela: el bastidor de alerce, el tablero pintado de añil con su rombo, la argolla y el pernio. */
    private fun DrawScope.portezuela(r: Rect, derecha: Boolean, g: MarcoMongol) {
        val u = density
        val w = g.w
        val l = g.luz(r.center)
        drawRect(l.s(0.4f), Offset(r.left + 1.6f * u, r.top + 1.6f * u), r.size)
        drawRect(l.c(mezcla(P.ALERCE, P.ALERCE_LUZ, 0.1f)), r.topLeft, r.size)
        vetas(r, l.c(P.ALERCE_OSC).copy(alpha = 0.4f), if (derecha) 711 else 710, 0.8f, grosor = 0.8f)
        val t = Rect(r.left + w * 0.024f, r.top + w * 0.018f, r.right - w * 0.024f, r.bottom - w * 0.018f)
        drawRect(l.c(P.ANIL), t.topLeft, t.size)
        pinceladas(t, l.c(P.ANIL_LUZ), if (derecha) 713 else 712, 8, u, 0.16f)
        drawRect(l.c(P.ORO), t.topLeft, t.size, style = Stroke(1.1f * u))
        // El tablero hundido: sombra arriba e izquierda (de noche, abajo y a la derecha).
        if (!g.a.oscuro) {
            drawRect(l.s(0.35f), t.topLeft, Size(t.width, 1.6f * u))
            drawRect(l.s(0.25f), t.topLeft, Size(1.6f * u, t.height))
        } else drawRect(l.filo(0.4f), Offset(t.left, t.bottom - 1.4f * u), Size(t.width, 1.4f * u))
        // El rombo escalonado de bermellón y la orla de puntos de oro.
        with(this@AlacenaEstanteMongol) { escalonado(t.center, minOf(t.height * 0.36f, t.width * 0.2f), l, P.BERMELLON, P.CREMA) }
        var x = t.left + w * 0.02f
        while (x < t.right - w * 0.01f) {
            drawCircle(l.c(P.ORO), 1.1f * u, Offset(x, t.top + w * 0.014f))
            drawCircle(l.c(P.ORO), 1.1f * u, Offset(x, t.bottom - w * 0.014f))
            x += w * 0.028f
        }
        // Lo gastado: la pintura saltada junto a la argolla.
        val ax = if (derecha) r.left + w * 0.035f else r.right - w * 0.035f
        drawOval(l.c(P.ALERCE).copy(alpha = 0.8f), Offset(ax - w * 0.02f, r.center.y - w * 0.012f), Size(w * 0.04f, w * 0.03f))
        // La argolla de hierro, colgando de su cáncamo.
        val ca = Offset(ax, r.center.y - w * 0.012f)
        drawCircle(l.s(0.4f), w * 0.016f, ca + Offset(1.4f * u, w * 0.018f), style = Stroke(w * 0.005f))
        drawCircle(l.c(P.HIERRO), w * 0.016f, ca + Offset(0f, w * 0.016f), style = Stroke(w * 0.005f))
        drawArc(l.b(0.6f), 160f, 90f, false, Offset(ca.x - w * 0.016f, ca.y), Size(w * 0.032f, w * 0.032f), style = Stroke(w * 0.0018f))
        clavoMongol(ca, 2f * u, l, P.HIERRO)
        // El pernio forjado del lado de fuera, con sus clavos.
        val px = if (derecha) r.right else r.left
        val s = if (derecha) -1f else 1f
        listOf(r.top + r.height * 0.22f, r.bottom - r.height * 0.22f).forEach { y ->
            val pe = poligonoMongol(px - s * w * 0.012f, y - w * 0.008f, px + s * w * 0.07f, y - w * 0.005f, px + s * w * 0.085f, y,
                px + s * w * 0.07f, y + w * 0.005f, px - s * w * 0.012f, y + w * 0.008f)
            drawPath(corridoMongol(pe, 1.2f * u, 1.4f * u), l.s(0.4f))
            drawPath(pe, l.c(P.HIERRO))
            listOf(0.02f, 0.05f).forEach { f -> clavoMongol(Offset(px + s * w * f, y), 1.2f * u, l, P.HIERRO_LUZ) }
        }
    }

    /**
     * La franja del sol de la puerta (de día; en los Sueños en claro, el anochecer): un polígono de borde neto que sube por la
     * izquierda, sobre [r]. Lo que está debajo de la línea del dintel queda alumbrado.
     */
    internal fun DrawScope.solPuerta(g: MarcoMongol, r: Rect) {
        if (g.a.oscuro) return
        val luz = poligonoSolMongol(g)
        val col = if (g.a.noche) P.CREPUSCULO else P.SOL
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawPath(luz, col.copy(alpha = if (g.a.noche) 0.16f else 0.24f))
            drawPath(luz, luzMueble(if (g.a.noche) 0.03f else 0.07f))
        }
    }

}
