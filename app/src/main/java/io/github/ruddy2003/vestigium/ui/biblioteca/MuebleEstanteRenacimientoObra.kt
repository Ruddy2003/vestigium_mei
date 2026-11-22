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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El estante de nogal tallado y dorado de Renacimiento, con oficio de entallador: la cornisa con su cimasa, el junquillo
 * dorado, el vuelo con su sombra, las ovas y dardos y el friso de trenza con sus rosetas sobre las pilastras; las pilastras
 * estriadas con capitel de volutas doradas y basa; dentro de cada repisa, el fondo pintado de verde de cobre, los cantos de los
 * costados y de las tablas en su perspectiva y la sombra de la tabla de arriba; las tablas con su bocel; y abajo el zócalo con
 * los dos cajones del papel (cuarterones realzados, anillas de hierro, bocallaves) y las garras de león sobre las losas.
 */
internal object ObraEstanteRenacimiento {
    private val P = PaletaRenacimiento
    private val M = MuebleEstanteRenacimiento

    /**
     * Un tramo de madera (o de pintura) con la luz que le llega a lo largo: de día, liso; con la lucerna, un solo degradado con
     * [n] muestras de la luz (así no se ven escalones entre tramos y es una sola operación).
     */
    fun DrawScope.tramo(r: Rect, c: Color, a: AireRenacimiento, vertical: Boolean, n: Int, k: (Float) -> Float) {
        if (r.width < 0.5f || r.height < 0.5f) return
        if (!a.lumbre) { drawRect(c, r.topLeft, r.size); return }
        val m = maxOf(2, n)
        val cols = List(m) { j ->
            val f = j / (m - 1f)
            a.luz(c, k(if (vertical) r.top + r.height * f else r.left + r.width * f))
        }
        val b = if (vertical) Brush.verticalGradient(cols, r.top, r.bottom) else Brush.horizontalGradient(cols, r.left, r.right)
        drawRect(b, r.topLeft, r.size)
    }

    /** Debajo del zócalo, entre las garras: el suelo que se mete bajo el mueble, a oscuras (de lo hondo a la losa en sombra). */
    fun DrawScope.bajoMueble(a: AireRenacimiento, b: Rect) {
        val w = size.width
        val zoc = b.top + b.height * M.ZOCALO - 1f
        val pisan = b.top + b.height * M.PISAN
        if (pisan <= zoc) return
        val hondo = if (a.lumbre) a.hundido(P.SERENA_OSC) else mezcla(P.SERENA_OSC, P.NOCHE, 0.6f)
        val losa = if (a.lumbre) a.luz(P.SERENA_OSC, 0.05f) else mezcla(P.SERENA_OSC, P.NOCHE, 0.3f)
        drawRect(Brush.verticalGradient(listOf(hondo, losa), zoc, pisan), Offset(0f, zoc), Size(w, pisan - zoc))
    }

    // ---------------------------------------------------------------- dentro de cada repisa

    /**
     * El hondo de una repisa: el fondo de tablas pintadas de verde de cobre (más gastado donde se rozan los libros), los cantos
     * de los costados en su perspectiva (el de la izquierda en sombra, el de la derecha con luz), la cara de la tabla de abajo,
     * la panza de la de arriba y la sombra que ésta echa sobre el fondo.
     */
    fun DrawScope.interior(k: Int, r: Rect, oc: Float, a: AireRenacimiento, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val luzX: (Float) -> Float = { x -> cerca(Offset(x, r.center.y)) }
        // El verde del fondo, en un solo degradado: de día, la luz de la ventana que entra por la izquierda y se pierde hacia la
        // derecha; con la lucerna, la luz que le llega. Detrás de los volúmenes (hasta [oc]) queda en su sombra: entre uno y
        // otro se ve el hondo.
        val muestras = 7
        val paradas = ArrayList<Pair<Float, Color>>(muestras + 2)
        val fOc = ((oc - r.left) / maxOf(1f, r.width)).coerceIn(0f, 1f)
        val fOc2 = (fOc + w * 0.06f / maxOf(1f, r.width)).coerceIn(0f, 1f)
        fun fondo(f: Float): Color {
            val x = r.left + r.width * f
            val base = if (!a.lumbre) {
                if (f < 0.5f) mezcla(mezcla(P.VERDE_FONDO, P.VERDE_LUZ, 0.45f), P.VERDE_FONDO, f * 2f) else mezcla(P.VERDE_FONDO, mezcla(P.VERDE_FONDO, P.NOGAL_OSC, 0.35f), f * 2f - 1f)
            } else a.luz(P.VERDE_FONDO, luzX(x))
            val tras = if (f <= fOc) 1f else if (f >= fOc2 || fOc2 <= fOc) 0f else 1f - (f - fOc) / (fOc2 - fOc)
            return if (tras > 0f) mezcla(base, a.hondo, 0.3f * tras) else base
        }
        for (j in 0 until muestras) {
            val f = j / (muestras - 1f)
            paradas.add(f to fondo(f))
        }
        if (fOc > 0.005f && fOc < 0.995f) { paradas.add(fOc to fondo(fOc)); paradas.add(fOc2 to fondo(fOc2)) }
        paradas.sortBy { it.first }
        drawRect(Brush.horizontalGradient(*paradas.toTypedArray(), startX = r.left, endX = r.right), r.topLeft, r.size)
        // Las juntas de las tablas del fondo y el roce de la pintura.
        val tablones = 6
        val juntas = Path()
        for (j in 1 until tablones) { val x = r.left + r.width * j / tablones + (azar(800 + k, j) - 0.5f) * 4f * u; juntas.addRect(Rect(x, r.top, x + 1f * u, r.bottom)) }
        drawPath(juntas, a.sombra(0.45f))
        pinceladas(r, if (a.oscuro) a.hondo else P.VERDE_LUZ, 830 + k, 8, u, if (a.oscuro) 0.1f else 0.18f)
        pinceladas(Rect(r.left, r.top + r.height * 0.55f, r.right, r.bottom), mezcla(P.NOGAL, P.VERDE_FONDO, 0.4f), 840 + k, 5, u, 0.14f)
        // La sombra de la tabla de arriba: honda junto a ella y luego más suave (un solo degradado).
        drawRect(Brush.verticalGradient(0f to a.sombra(0.62f), 0.25f to a.sombra(0.3f), 1f to Color.Transparent, startY = r.top, endY = r.top + r.height * 0.34f),
            r.topLeft, Size(r.width, r.height * 0.34f))
        // Los cantos en perspectiva: el costado izquierdo, el derecho, la panza de arriba y la cara de la tabla de abajo.
        val dx = w * 0.035f; val dy = r.height * 0.07f
        val izq = poligonoRenacimiento(r.left, r.top, r.left + dx, r.top + dy, r.left + dx, r.bottom - dy * 0.7f, r.left, r.bottom)
        val der = poligonoRenacimiento(r.right, r.top, r.right - dx, r.top + dy, r.right - dx, r.bottom - dy * 0.7f, r.right, r.bottom)
        val panza = poligonoRenacimiento(r.left, r.top, r.right, r.top, r.right - dx, r.top + dy, r.left + dx, r.top + dy)
        val tL = a.en(luzX(r.left)); val tR = a.en(luzX(r.right))
        drawPath(izq, tL.c(mezcla(P.NOGAL, P.NOGAL_OSC, 0.45f)))
        drawPath(der, tR.c(P.NOGAL))
        drawPath(panza, a.en(luzX(r.center.x)).c(P.NOGAL_OSC))
        tramo(Rect(r.left, r.bottom - dy * 0.7f, r.right, r.bottom), mezcla(P.NOGAL, P.NOGAL_LUZ, 0.55f), a, false, 6, luzX)
        if (!a.lumbre) {
            drawPath(izq, a.sombra(0.3f))
            drawPath(der, a.claro(0.14f))
        } else {
            // De noche lo que mira a la lucerna (el canto izquierdo, que mira a la derecha) coge su luz.
            drawPath(izq, tL.f(0.35f))
            drawPath(der, a.sombra(0.3f))
        }
        drawRect(a.claro(0.3f), Offset(r.left + dx, r.bottom - dy * 0.7f), Size(r.width - 2 * dx, 0.8f * u))
        drawLine(a.sombra(0.4f), Offset(r.left + dx, r.top + dy), Offset(r.left + dx, r.bottom - dy * 0.7f), 0.8f * u)
    }

    // ---------------------------------------------------------------- las tablas y sus rótulos

    /** Una tabla: el canto de nogal con su bocel de arriba (la luz en su lomo), el filete y la sombra de abajo. */
    fun DrawScope.tabla(r: Rect, semilla: Int, a: AireRenacimiento, cerca: (Offset) -> Float) {
        val u = density
        val luzX: (Float) -> Float = { x -> cerca(Offset(x, r.center.y)) }
        tramo(r, P.NOGAL, a, false, 6, luzX)
        vetas(r, a.luz(P.VETA, 0.5f).copy(alpha = 0.4f), semilla, densidad = 0.6f, grosor = 0.6f)
        val bocel = r.height * 0.36f
        tramo(Rect(r.left, r.top, r.right, r.top + bocel), P.NOGAL_LUZ, a, false, 6, luzX)
        drawRect(a.claro(0.5f), Offset(r.left, r.top + bocel * 0.25f), Size(r.width, 1f * u))
        drawRect(a.sombra(0.35f), Offset(r.left, r.top + bocel - 0.8f * u), Size(r.width, 1.2f * u))
        drawRect(a.claro(0.16f), Offset(r.left, r.top + bocel + 1.6f * u), Size(r.width, 0.8f * u))
        // La fusarola tallada y dorada: una cuenta larga y dos carretes, a todo lo largo.
        val yf = r.top + r.height * 0.62f
        val hf = r.height * 0.13f
        val cuentas = Path(); val carretes = Path()
        var x = r.left + 2f * u
        var j = 0
        while (x < r.right - 3f * u && j < 120) {
            cuentas.addOval(Rect(x, yf - hf, x + hf * 3.2f, yf + hf))
            x += hf * 3.6f
            carretes.addOval(Rect(x - hf * 0.1f, yf - hf * 0.8f, x + hf * 0.5f, yf + hf * 0.8f))
            carretes.addOval(Rect(x + hf * 0.7f, yf - hf * 0.8f, x + hf * 1.3f, yf + hf * 0.8f))
            x += hf * 1.8f; j++
        }
        val oroT = if (a.lumbre) Brush.horizontalGradient(List(6) { q -> a.luz(P.ORO, luzX(r.left + r.width * q / 5f)) }, r.left, r.right) else null
        drawPath(Path().apply { addPath(cuentas, Offset(0.5f * u, 0.7f * u)); addPath(carretes, Offset(0.5f * u, 0.7f * u)) }, a.sombra(0.4f))
        if (oroT != null) { drawPath(cuentas, oroT); drawPath(carretes, oroT) } else { drawPath(cuentas, P.ORO); drawPath(carretes, P.ORO_OSC) }
        drawRect(a.claro(0.3f), Offset(r.left, yf - hf * 0.6f), Size(r.width, 0.6f * u))
        drawRect(a.sombra(0.5f), Offset(r.left, r.bottom - 1.8f * u), Size(r.width, 1.8f * u))
        if (a.lumbre) {
            val lx = size.width * M.LAMP_X
            val kk = luzX(lx)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.en(kk).f(0.7f), Color.Transparent), lx - size.width * 0.5f, lx + size.width * 0.3f),
                Offset(r.left, r.top + bocel * 0.15f), Size(r.width, 1.3f * u))
        }
    }

    /** El rótulo del mes: un cartellino de papel clavado con dos tachuelas al canto de la tabla, algo torcido. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireRenacimiento, k: Float) {
        val u = density
        val r = p.r
        val s = p.texto.length * 37
        val giro = (azar(s, 1) - 0.5f) * 2.2f
        rotate(giro, r.center) {
            val hoja = poligonoRenacimiento(
                r.left - 3f * u, r.top + 0.6f * u * azar(s, 2), r.right + 3f * u, r.top - 0.3f * u, r.right + 3.4f * u, r.bottom - 0.5f * u * azar(s, 3),
                r.center.x + r.width * 0.1f, r.bottom + 0.6f * u, r.left - 3f * u, r.bottom,
            )
            drawPath(Path().apply { addPath(hoja, Offset(1.3f * u, 1.7f * u)) }, a.sombra(0.5f))
            drawPath(hoja, a.etiqueta(maxOf(k, 0.2f)))
            drawRect(a.claro(0.45f), Offset(r.left - 3f * u, r.top), Size(r.width + 6f * u, 1f * u))
            drawRect(a.sombra(0.1f), Offset(r.right - r.width * 0.15f, r.top), Size(r.width * 0.15f + 3f * u, r.height))
            val roja = a.luz(P.RUBRICA, maxOf(k, 0.4f)).copy(alpha = 0.6f)
            drawLine(roja, Offset(r.left + 6f * u, r.top + 1.8f * u), Offset(r.right - 6f * u, r.top + 1.8f * u), 0.5f * u)
            drawLine(roja, Offset(r.left + 6f * u, r.bottom - 1.8f * u), Offset(r.right - 6f * u, r.bottom - 1.8f * u), 0.5f * u)
            listOf(r.left + 1.5f * u, r.right - 1.5f * u).forEach { x ->
                drawCircle(a.sombra(0.4f), 1.4f * u, Offset(x + 0.4f * u, r.center.y + 0.5f * u))
                drawCircle(a.luz(P.HIERRO_LUZ, maxOf(k, 0.3f)), 1.2f * u, Offset(x, r.center.y))
            }
            textoEn(medidor, p.texto, Rect(r.left + 5f * u, r.top + 1.5f * u, r.right - 5f * u, r.bottom - 1.5f * u), P.LETRA, a.tinta, 11.5f, minimo = 5f)
        }
    }

    // ---------------------------------------------------------------- las pilastras

    /**
     * Una pilastra del frente: el costado de nogal con su resalto, el fuste con cuatro estrías (la sombra en un labio de cada
     * una y la luz en el otro) que abajo se llenan con sus junquillos, el filete dorado que lo enmarca, el capitel de volutas
     * doradas bajo la cornisa, la basa de dos boceles y el junquillo dorado que corre por el canto de dentro.
     */
    fun DrawScope.pilastra(r: Rect, derecho: Boolean, a: AireRenacimiento, repisas: Int, fila: Float) {
        val u = density
        val w = size.width
        if (r.height < 2f) return
        val fx = r.center.x / w
        val luzY: (Float) -> Float = { y -> M.cercaCuerpo((y - r.top) / maxOf(1f, fila), repisas, fx) }
        val nY = 2 + 2 * minOf(repisas, 6)
        tramo(r, P.NOGAL, a, true, nY, luzY)
        vetas(r, a.luz(P.VETA, 0.5f).copy(alpha = 0.45f), if (derecho) 71 else 73, densidad = 0.8f, vertical = true, grosor = 0.6f)
        val cap = w * 0.088f
        val basa = w * 0.05f
        val fuste = Rect(r.left + r.width * 0.15f, r.top + cap + w * 0.006f, r.right - r.width * 0.15f, r.bottom - basa)
        if (fuste.height > 4f * u) {
            tramo(fuste, mezcla(P.NOGAL, P.NOGAL_LUZ, 0.35f), a, true, nY, luzY)
            drawRect(a.sombra(0.45f), Offset(fuste.right, fuste.top), Size(1.6f * u, fuste.height))
            drawRect(a.claro(0.35f), Offset(fuste.left - 1f * u, fuste.top), Size(1f * u, fuste.height))
            drawRect(a.luz(P.ORO, luzY(fuste.top)).copy(alpha = 0.8f), fuste.topLeft, fuste.size, style = Stroke(0.8f * u))
            val estrias = 4
            val paso = fuste.width / (estrias + 1)
            val corte = fuste.bottom - fuste.height * 0.3f
            val surcos = Path(); val labios = Path(); val junquillos = Path()
            for (j in 1..estrias) {
                val x = fuste.left + paso * j
                val y0 = fuste.top + paso * 0.7f; val y1 = fuste.bottom - paso * 0.7f
                if (y1 <= y0) continue
                surcos.addRoundRect(androidx.compose.ui.geometry.RoundRect(x - paso * 0.32f, y0, x + paso * 0.06f, y1, paso * 0.2f, paso * 0.2f))
                labios.addRect(Rect(x + paso * 0.06f, y0, x + paso * 0.06f + 0.8f * u, y1))
                // Abajo, cada estría lleva su junquillo (el tercio de abajo del fuste, como en las pilastras florentinas).
                if (corte in y0..y1) junquillos.addRoundRect(androidx.compose.ui.geometry.RoundRect(x - paso * 0.24f, corte, x - paso * 0.02f, y1, paso * 0.12f, paso * 0.12f))
            }
            drawPath(surcos, a.sombra(0.45f))
            drawPath(labios, a.claro(0.3f))
            drawPath(junquillos, a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.6f), luzY(corte)))
            drawPath(junquillos, a.claro(0.18f))
            if (a.lumbre && !derecho) drawPath(labios, a.en(luzY(r.top + fila * 0.5f)).f(0.4f))
        }
        capitel(Rect(r.left + r.width * 0.04f, r.top, r.right - r.width * 0.04f, r.top + cap), a, a.en(luzY(r.top + cap * 0.5f)))
        // La basa: dos boceles con su escocia en sombra y un junquillo dorado.
        val bz = Rect(r.left + r.width * 0.04f, r.bottom - basa, r.right - r.width * 0.04f, r.bottom)
        val tb = a.en(luzY(bz.center.y))
        drawRect(a.sombra(0.4f), Offset(bz.left, bz.top - 1.6f * u), Size(bz.width, 1.6f * u))
        drawRect(tb.c(P.NOGAL_LUZ), bz.topLeft, bz.size)
        drawRect(a.sombra(0.45f), Offset(bz.left, bz.top + basa * 0.36f), Size(bz.width, basa * 0.18f))
        drawRect(tb.b(0.4f), Offset(bz.left, bz.top + 0.6f * u), Size(bz.width, 0.9f * u))
        drawRect(tb.b(0.35f), Offset(bz.left, bz.top + basa * 0.62f), Size(bz.width, 0.9f * u))
        drawRect(tb.c(P.ORO), Offset(bz.left, bz.top + basa * 0.28f), Size(bz.width, 1f * u))
        // El canto: la luz del lado de la ventana y la sombra del otro; por dentro, el junquillo dorado del vano.
        if (!derecho) drawRect(a.claro(0.24f), Offset(r.left, r.top), Size(1.2f * u, r.height))
        else drawRect(a.sombra(0.35f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        val xd = if (derecho) r.left else r.right - 2.2f * u
        tramo(Rect(xd, r.top + cap, xd + 2.2f * u, r.bottom - basa), P.ORO, a, true, nY, luzY)
        drawRect(a.sombra(0.4f), Offset(if (derecho) xd + 2.2f * u else xd - 0.8f * u, r.top + cap), Size(0.8f * u, r.height - cap - basa))
        if (a.lumbre && derecho) {
            val k0 = luzY(r.top + fila * 0.5f)
            drawRect(Brush.verticalGradient(listOf(a.en(k0).f(0.6f), Color.Transparent), r.top, r.top + fila * 2.5f), Offset(r.left, r.top), Size(1.3f * u, r.height))
        }
    }

    /**
     * El capitel jónico de la pilastra: el ábaco, el canal que se enrosca en dos volutas doradas (cada una con su espiral y su
     * ojo), el equino de ovas entre ellas, el astrágalo de cuentas y el collarino con su roseta.
     */
    private fun DrawScope.capitel(c: Rect, a: AireRenacimiento, t: TonoRenacimiento) {
        val u = density
        val h = c.height
        drawRect(a.sombra(0.45f), Offset(c.left, c.bottom), Size(c.width, 2.6f * u))
        drawRect(t.c(P.NOGAL), c.topLeft, c.size)
        // El ábaco.
        drawRect(t.c(P.NOGAL_LUZ), c.topLeft, Size(c.width, h * 0.16f))
        drawRect(t.b(0.45f), c.topLeft, Size(c.width, 0.9f * u))
        drawRect(a.sombra(0.4f), Offset(c.left, c.top + h * 0.16f), Size(c.width, 1f * u))
        // El canal, entre dos filetes dorados.
        drawRect(t.c(P.NOGAL_OSC), Offset(c.left, c.top + h * 0.2f), Size(c.width, h * 0.16f))
        drawRect(t.c(P.ORO), Offset(c.left, c.top + h * 0.19f), Size(c.width, 0.9f * u))
        drawRect(t.c(P.ORO), Offset(c.left, c.top + h * 0.36f), Size(c.width, 0.9f * u))
        // El equino de ovas, entre las volutas.
        val rv = c.width * 0.2f
        val e0 = c.left + rv * 1.9f; val e1 = c.right - rv * 1.9f
        val ovas = Path(); val dardos = Path()
        val n = 3
        for (j in 0 until n) {
            val x = e0 + (e1 - e0) * (j + 0.5f) / n
            val ow = (e1 - e0) / n * 0.4f
            ovas.addOval(Rect(x - ow, c.top + h * 0.4f, x + ow, c.top + h * 0.64f))
            if (j > 0) { val xd = e0 + (e1 - e0) * j / n; dardos.moveTo(xd - 0.8f * u, c.top + h * 0.4f); dardos.lineTo(xd + 0.8f * u, c.top + h * 0.4f); dardos.lineTo(xd, c.top + h * 0.62f); dardos.close() }
        }
        drawPath(Path().apply { addPath(ovas, Offset(0.6f * u, 0.9f * u)) }, a.sombra(0.35f))
        drawPath(ovas, t.c(P.NOGAL_LUZ))
        drawPath(dardos, t.c(P.ORO))
        // Las dos volutas: el disco dorado, la espiral en sombra y el ojo.
        listOf(c.left + rv * 1.02f, c.right - rv * 1.02f).forEachIndexed { j, x ->
            val o = Offset(x, c.top + h * 0.44f)
            drawCircle(a.sombra(0.4f), rv, o + Offset(0.8f * u, 1f * u))
            drawCircle(t.c(P.ORO), rv, o)
            drawCircle(t.c(P.ORO_LUZ), rv, o, style = Stroke(0.7f * u))
            val giro = if (j == 0) 0f else 180f
            drawArc(t.c(P.ORO_OSC), giro + 20f, 300f, false, o - Offset(rv * 0.72f, rv * 0.72f), Size(rv * 1.44f, rv * 1.44f), style = Stroke(1f * u, cap = StrokeCap.Round))
            drawArc(t.c(P.ORO_OSC), giro + 60f, 260f, false, o - Offset(rv * 0.42f, rv * 0.42f), Size(rv * 0.84f, rv * 0.84f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            drawCircle(t.c(P.ORO_LUZ), rv * 0.16f, o)
            destelloRenacimiento(o - Offset(rv * 0.55f, rv * 0.55f), 0.9f * u, t)
        }
        // El astrágalo de cuentas y el collarino.
        val ya = c.top + h * 0.72f
        val cuentas = Path()
        var x = c.left + 2f * u
        var k = 0
        while (x < c.right - 2f * u && k < 30) { cuentas.addOval(Rect(x, ya - 1.1f * u, x + 2.6f * u, ya + 1.1f * u)); x += 3.6f * u; k++ }
        drawPath(cuentas, t.c(P.ORO))
        drawRect(t.c(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.3f)), Offset(c.left + c.width * 0.08f, ya + 1.6f * u), Size(c.width * 0.84f, c.bottom - ya - 1.6f * u))
        roseta(Offset(c.center.x, (ya + c.bottom) / 2 + 0.8f * u), minOf(c.width * 0.1f, (c.bottom - ya) * 0.36f), t, a)
    }

    // ---------------------------------------------------------------- el zócalo y las garras

    /** El zócalo: la moldura de arriba, los dos cajones con su cuarterón realzado, anillas y bocallaves, la moldura de abajo y las garras. */
    fun DrawScope.zocalo(b: Rect, a: AireRenacimiento, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val hb = b.height
        val zoc = b.top + hb * M.ZOCALO
        val luzX: (Float) -> Float = { x -> cerca(Offset(x, b.top + hb * 0.2f)) }
        val cuerpo = Rect(0f, b.top, w, zoc)
        tramo(cuerpo, P.NOGAL, a, false, 6, luzX)
        vetas(cuerpo, a.luz(P.VETA, 0.5f).copy(alpha = 0.4f), 655, densidad = 0.5f, grosor = 0.6f)
        // La moldura de arriba, con su junquillo dorado.
        val mo = Rect(0f, b.top, w, b.top + hb * 0.07f)
        tramo(mo, P.NOGAL_LUZ, a, false, 6, luzX)
        drawRect(a.claro(0.45f), mo.topLeft, Size(w, 1f * u))
        drawRect(a.luz(P.ORO, luzX(w * 0.5f)), Offset(0f, mo.bottom - 2.2f * u), Size(w, 1.2f * u))
        drawRect(a.sombra(0.45f), Offset(0f, mo.bottom), Size(w, 2.2f * u))
        // Los dados bajo las pilastras, con su roseta dorada.
        val lado = w * M.LADO
        listOf(0f, w - lado).forEach { x ->
            val dado = Rect(x + lado * 0.08f, mo.bottom + hb * 0.04f, x + lado * 0.92f, zoc - hb * 0.1f)
            val t = a.en(luzX(dado.center.x))
            drawRect(t.c(P.NOGAL_LUZ), dado.topLeft, dado.size)
            drawRect(a.sombra(0.35f), Offset(dado.right - 1.4f * u, dado.top), Size(1.4f * u, dado.height))
            drawRect(a.claro(0.3f), dado.topLeft, Size(dado.width, 1f * u))
            roseta(dado.center, minOf(dado.width, dado.height) * 0.3f, t, a)
        }
        // Los dos cajones del papel.
        val m0 = lado + w * 0.025f
        val medio = w / 2
        listOf(Rect(m0, mo.bottom + hb * 0.06f, medio - w * 0.018f, zoc - hb * 0.1f), Rect(medio + w * 0.018f, mo.bottom + hb * 0.06f, w - m0, zoc - hb * 0.1f))
            .forEach { c -> cajon(c, a, a.en(luzX(c.center.x))) }
        roseta(Offset(medio, (mo.bottom + zoc) / 2), hb * 0.07f, a.en(luzX(medio)), a)
        // La moldura de abajo: el bocel que vuela, con su luz y su sombra.
        val mb = Rect(-w * 0.01f, zoc - hb * 0.08f, w * 1.01f, zoc)
        tramo(mb, P.NOGAL_LUZ, a, false, 6, luzX)
        drawRect(a.claro(0.45f), Offset(mb.left, mb.top + mb.height * 0.2f), Size(mb.width, 1.2f * u))
        drawRect(a.sombra(0.4f), Offset(mb.left, mb.top + mb.height * 0.65f), Size(mb.width, mb.height * 0.35f))
        // Las garras de león, en las dos esquinas.
        val pisan = b.top + hb * M.PISAN
        listOf(false, true).forEach { der ->
            val pw = w * 0.11f
            val x0 = if (der) w - pw - w * 0.005f else w * 0.005f
            garra(Rect(x0, zoc, x0 + pw, pisan), a, a.en(cerca(Offset(x0 + pw / 2, pisan))), der)
        }
        pinceladas(cuerpo, a.sombra(1f), 666, 12, u, 0.07f)
    }

    /** Un cajón: el marco de nogal, el cuarterón realzado con sus cuatro biseles, la anilla de hierro colgada y la bocallave. */
    private fun DrawScope.cajon(c: Rect, a: AireRenacimiento, t: TonoRenacimiento) {
        val u = density
        if (c.width < 6f * u || c.height < 6f * u) return
        drawRect(a.sombra(0.55f), Offset(c.left - 1.4f * u, c.top - 1.4f * u), Size(c.width + 2.8f * u, c.height + 2.8f * u))
        drawRect(t.c(P.NOGAL), c.topLeft, c.size)
        drawRect(a.claro(0.3f), Offset(c.left, c.bottom - 1f * u), Size(c.width, 1f * u))
        val pn = c.deflate(minOf(c.width, c.height) * 0.16f)
        val bis = minOf(pn.width, pn.height) * 0.22f
        val cen = pn.deflate(bis)
        drawPath(poligonoRenacimiento(pn.left, pn.top, pn.right, pn.top, cen.right, cen.top, cen.left, cen.top), t.c(P.NOGAL_LUZ))
        drawPath(poligonoRenacimiento(pn.left, pn.top, cen.left, cen.top, cen.left, cen.bottom, pn.left, pn.bottom), t.c(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.6f)))
        drawPath(poligonoRenacimiento(pn.left, pn.bottom, cen.left, cen.bottom, cen.right, cen.bottom, pn.right, pn.bottom), t.c(P.NOGAL_OSC))
        drawPath(poligonoRenacimiento(pn.right, pn.top, pn.right, pn.bottom, cen.right, cen.bottom, cen.right, cen.top), t.c(mezcla(P.NOGAL, P.NOGAL_OSC, 0.5f)))
        drawRect(t.c(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.3f)), cen.topLeft, cen.size)
        vetas(cen, a.luz(P.VETA, 0.5f).copy(alpha = 0.35f), (c.left / u).toInt(), densidad = 0.9f, grosor = 0.5f)
        drawRect(a.claro(0.3f), cen.topLeft, Size(cen.width, 0.8f * u))
        drawRect(t.f(0.4f), Offset(cen.left, cen.top), Size(cen.width, 0.8f * u))
        // La anilla, colgada de su argolla, y su sombra; la bocallave a un lado.
        val o = Offset(cen.center.x, cen.top + cen.height * 0.2f)
        val ra = cen.height * 0.32f
        val hierro = t.c(P.HIERRO)
        drawCircle(a.sombra(0.45f), ra, Offset(o.x + 1.4f * u, o.y + ra + 1.8f * u), style = Stroke(1.6f * u))
        drawCircle(hierro, ra, Offset(o.x, o.y + ra), style = Stroke(1.6f * u))
        drawArc(a.luz(P.HIERRO_LUZ, maxOf(t.k, 0.3f)), 200f, 70f, false, Offset(o.x - ra, o.y), Size(2 * ra, 2 * ra), style = Stroke(0.7f * u))
        drawCircle(hierro, 2f * u, o)
        drawCircle(a.luz(P.HIERRO_LUZ, maxOf(t.k, 0.3f)), 0.8f * u, o - Offset(0.5f * u, 0.5f * u))
        val bl = Offset(c.right - c.width * 0.1f, c.center.y)
        drawCircle(t.c(P.ORO_OSC), 2.6f * u, bl)
        drawCircle(t.c(P.NOGAL_OSC), 0.9f * u, bl - Offset(0f, 0.4f * u))
        drawRect(t.c(P.NOGAL_OSC), Offset(bl.x - 0.4f * u, bl.y), Size(0.8f * u, 1.8f * u))
    }

    /** Una roseta tallada y dorada: ocho pétalos alrededor de un botón. */
    private fun DrawScope.roseta(c: Offset, r: Float, t: TonoRenacimiento, a: AireRenacimiento) {
        if (r < 1f) return
        val petalos = Path()
        for (j in 0 until 8) {
            val ang = j * 0.785f
            val p = Offset(c.x + r * 0.62f * kotlin.math.cos(ang), c.y + r * 0.62f * kotlin.math.sin(ang))
            petalos.addOval(Rect(p.x - r * 0.34f, p.y - r * 0.34f, p.x + r * 0.34f, p.y + r * 0.34f))
        }
        drawPath(Path().apply { addPath(petalos, Offset(r * 0.12f, r * 0.15f)) }, a.sombra(0.35f))
        drawPath(petalos, t.c(P.ORO))
        drawCircle(t.c(P.ORO_OSC), r * 0.36f, c)
        drawCircle(t.c(P.ORO_LUZ), r * 0.2f, c - Offset(r * 0.06f, r * 0.06f))
        destelloRenacimiento(c - Offset(r * 0.45f, r * 0.45f), r * 0.12f, t)
    }

    /** Una garra de león: la hoja de acanto que baja del zócalo y la pata con tres dedos sobre la losa. */
    private fun DrawScope.garra(r: Rect, a: AireRenacimiento, t: TonoRenacimiento, der: Boolean) {
        val u = density
        if (r.height < 3f * u) return
        val cx = r.center.x
        val pw = r.width
        // La hoja de acanto, en voluta, que sale del zócalo.
        val hoja = Path().apply {
            moveTo(r.left + pw * 0.08f, r.top)
            lineTo(r.right - pw * 0.08f, r.top)
            cubicTo(r.right - pw * 0.05f, r.top + r.height * 0.3f, cx + pw * 0.28f, r.top + r.height * 0.45f, cx + pw * 0.22f, r.top + r.height * 0.55f)
            lineTo(cx - pw * 0.22f, r.top + r.height * 0.55f)
            cubicTo(cx - pw * 0.28f, r.top + r.height * 0.45f, r.left + pw * 0.05f, r.top + r.height * 0.3f, r.left + pw * 0.08f, r.top)
            close()
        }
        drawPath(hoja, t.c(P.NOGAL))
        drawPath(poligonoRenacimiento(cx, r.top, cx + pw * 0.04f, r.top + r.height * 0.5f, cx - pw * 0.04f, r.top + r.height * 0.5f), a.sombra(0.3f))
        drawArc(t.c(P.NOGAL_LUZ), 180f, 200f, false, Offset(r.left + pw * 0.1f, r.top + r.height * 0.05f), Size(pw * 0.3f, r.height * 0.3f), style = Stroke(1.4f * u))
        drawArc(t.c(P.NOGAL_LUZ), 160f, 200f, false, Offset(r.right - pw * 0.4f, r.top + r.height * 0.05f), Size(pw * 0.3f, r.height * 0.3f), style = Stroke(1.4f * u))
        // La pata y los tres dedos, con sus uñas.
        val y0 = r.top + r.height * 0.5f
        val pata = Path().apply {
            moveTo(cx - pw * 0.24f, y0)
            lineTo(cx + pw * 0.24f, y0)
            cubicTo(cx + pw * 0.4f, r.bottom - r.height * 0.25f, cx + pw * 0.46f, r.bottom - r.height * 0.05f, cx + pw * 0.36f, r.bottom)
            lineTo(cx - pw * 0.36f, r.bottom)
            cubicTo(cx - pw * 0.46f, r.bottom - r.height * 0.05f, cx - pw * 0.4f, r.bottom - r.height * 0.25f, cx - pw * 0.24f, y0)
            close()
        }
        drawPath(pata, t.c(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.3f)))
        val dedos = Path()
        for (j in -1..1) {
            val x = cx + j * pw * 0.22f
            dedos.addOval(Rect(x - pw * 0.13f, r.bottom - r.height * 0.32f, x + pw * 0.13f, r.bottom))
        }
        drawPath(dedos, t.c(P.NOGAL_LUZ))
        val surcos = Path()
        for (j in 0..1) { val x = cx - pw * 0.11f + j * pw * 0.22f; surcos.addRect(Rect(x - 0.5f * u, r.bottom - r.height * 0.3f, x + 0.5f * u, r.bottom)) }
        drawPath(surcos, a.sombra(0.5f))
        // La luz a la izquierda, la sombra a la derecha y el filo de la lucerna en la que la mira.
        drawRect(a.claro(0.2f), Offset(cx - pw * 0.32f, y0), Size(pw * 0.1f, r.bottom - y0))
        drawRect(a.sombra(0.3f), Offset(cx + pw * 0.16f, y0), Size(pw * 0.2f, r.bottom - y0))
        if (der) drawRect(t.f(0.3f), Offset(cx - pw * 0.3f, y0), Size(1f * u, r.bottom - y0))
        drawOval(a.sombra(0.5f), Offset(cx - pw * 0.42f, r.bottom - 2f * u), Size(pw * 0.95f, 4f * u))
    }

    // ---------------------------------------------------------------- la cornisa

    /**
     * La cornisa: la cara de arriba de la cimasa (donde pisa lo de arriba), la gola, el junquillo dorado, el vuelo con su
     * panza en sombra, las ovas y dardos, el friso con la trenza tallada y las rosetas sobre las pilastras, y el arquitrabe.
     */
    fun DrawScope.cornisa(r: Rect, a: AireRenacimiento, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val hT = r.height
        val luzX: (Float) -> Float = { x -> cerca(Offset(x, r.top)) }
        fun y(f: Float) = r.top + hT * f
        // La cara de arriba, vista un poco desde arriba.
        tramo(Rect(-w * 0.01f, y(0f), w * 1.01f, y(0.1f)), mezcla(P.NOGAL_LUZ, P.NOGAL, 0.3f), a, false, 7, luzX)
        drawRect(a.claro(0.45f), Offset(0f, y(0.1f) - 1f * u), Size(w, 1f * u))
        // La gola de la cimasa: la luz arriba y la sombra en su vientre.
        tramo(Rect(-w * 0.01f, y(0.1f), w * 1.01f, y(0.24f)), P.NOGAL_LUZ, a, false, 7, luzX)
        drawRect(a.sombra(0.4f), Offset(0f, y(0.17f)), Size(w, hT * 0.07f))
        // El junquillo dorado.
        tramo(Rect(0f, y(0.24f), w, y(0.28f)), P.ORO, a, false, 7, luzX)
        drawRect(a.claro(0.5f), Offset(0f, y(0.24f)), Size(w, 0.7f * u))
        // El vuelo: la cara lisa y su panza en sombra.
        tramo(Rect(0f, y(0.28f), w, y(0.42f)), P.NOGAL, a, false, 7, luzX)
        drawRect(a.claro(0.25f), Offset(0f, y(0.29f)), Size(w, 0.8f * u))
        drawRect(a.sombra(0.6f), Offset(0f, y(0.42f)), Size(w, hT * 0.04f))
        // Las ovas y dardos.
        ovasYDardos(Rect(w * 0.005f, y(0.44f), w * 0.995f, y(0.62f)), a, luzX)
        // El friso con su trenza y las rosetas sobre las pilastras.
        val fr = Rect(0f, y(0.62f), w, y(0.86f))
        tramo(fr, P.NOGAL, a, false, 7, luzX)
        drawRect(a.sombra(0.45f), Offset(0f, fr.top), Size(w, 1.4f * u))
        trenza(Rect(w * M.LADO + w * 0.02f, fr.top, w - w * M.LADO - w * 0.02f, fr.bottom), a, luzX)
        listOf(w * M.LADO / 2, w - w * M.LADO / 2).forEach { x ->
            val t = a.en(luzX(x))
            val bloque = Rect(x - w * M.LADO * 0.42f, fr.top + 1.4f * u, x + w * M.LADO * 0.42f, fr.bottom)
            drawRect(t.c(P.NOGAL_LUZ), bloque.topLeft, bloque.size)
            drawRect(a.sombra(0.35f), Offset(bloque.right - 1.2f * u, bloque.top), Size(1.2f * u, bloque.height))
            roseta(bloque.center, fr.height * 0.36f, t, a)
        }
        // El arquitrabe: dos fajas y el junquillo dorado de abajo.
        tramo(Rect(0f, y(0.86f), w, y(1f)), P.NOGAL_LUZ, a, false, 7, luzX)
        drawRect(a.sombra(0.3f), Offset(0f, y(0.93f)), Size(w, 1f * u))
        drawRect(a.luz(P.ORO, luzX(w / 2)), Offset(0f, y(1f) - 1.6f * u), Size(w, 1.2f * u))
        drawRect(a.sombra(0.5f), Offset(0f, y(1f)), Size(w, 2.4f * u))
        // El filo de la lucerna en los cantos que la miran.
        if (a.lumbre) {
            val lx = w * M.LAMP_X
            listOf(0.1f, 0.24f, 0.29f, 0.86f).forEach { f ->
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.en(luzX(lx)).f(0.8f), Color.Transparent), lx - w * 0.5f, lx + w * 0.35f),
                    Offset(0f, y(f) - 0.5f * u), Size(w, 1.2f * u))
            }
        }
        pinceladas(Rect(0f, r.top, w, r.bottom), a.sombra(1f), 699, 10, u, 0.05f)
    }

    /** Las ovas y dardos: cada ova en su cáscara, con la luz arriba a la izquierda y la sombra abajo; los dardos, dorados. */
    private fun DrawScope.ovasYDardos(r: Rect, a: AireRenacimiento, luzX: (Float) -> Float) {
        val u = density
        val paso = r.height * 1.1f
        val n = (r.width / paso).toInt().coerceIn(1, 80)
        val p = r.width / n
        val cascaras = Path(); val ovas = Path(); val luces = Path(); val dardos = Path()
        for (j in 0 until n) {
            val x = r.left + p * j + p / 2
            cascaras.addOval(Rect(x - p * 0.42f, r.top, x + p * 0.42f, r.bottom + r.height * 0.15f))
            ovas.addOval(Rect(x - p * 0.3f, r.top + r.height * 0.12f, x + p * 0.3f, r.bottom))
            luces.addOval(Rect(x - p * 0.22f, r.top + r.height * 0.2f, x - p * 0.02f, r.top + r.height * 0.55f))
            val xd = x + p / 2
            dardos.moveTo(xd - p * 0.06f, r.top); dardos.lineTo(xd + p * 0.06f, r.top); dardos.lineTo(xd, r.bottom); dardos.close()
        }
        tramo(r, P.NOGAL_OSC, a, false, 7, luzX)
        drawPath(cascaras, a.luz(P.NOGAL_OSC, luzX(r.center.x)))
        if (a.lumbre) {
            val cols = List(7) { j -> a.luz(P.NOGAL_LUZ, luzX(r.left + r.width * j / 6f)) }
            drawPath(ovas, Brush.horizontalGradient(cols, r.left, r.right))
        } else drawPath(ovas, mezcla(P.NOGAL_LUZ, P.ORO, 0.25f))
        drawPath(Path().apply { addPath(ovas, Offset(0.6f * u, 0.9f * u)) }, a.sombra(0.25f))
        drawPath(luces, a.claro(0.45f))
        if (a.lumbre) {
            val cols = List(7) { j -> a.luz(P.ORO, luzX(r.left + r.width * j / 6f)) }
            drawPath(dardos, Brush.horizontalGradient(cols, r.left, r.right))
        } else drawPath(dardos, P.ORO)
    }

    /** La trenza del friso: dos cintas que se cruzan y un botón en cada ojo, tallados y con el oro gastado. */
    private fun DrawScope.trenza(r: Rect, a: AireRenacimiento, luzX: (Float) -> Float) {
        val u = density
        if (r.width < 10f * u) return
        val cy = r.center.y
        val amp = r.height * 0.3f
        val per = r.height * 2.2f
        val n = (r.width / per).toInt().coerceIn(1, 60)
        val p = r.width / n
        val c1 = Path(); val c2 = Path(); val botones = Path()
        c1.moveTo(r.left, cy); c2.moveTo(r.left, cy)
        for (j in 0 until n) {
            val x = r.left + p * j
            c1.cubicTo(x + p * 0.25f, cy - amp * 1.3f, x + p * 0.75f, cy - amp * 1.3f, x + p, cy)
            c2.cubicTo(x + p * 0.25f, cy + amp * 1.3f, x + p * 0.75f, cy + amp * 1.3f, x + p, cy)
            val o = Offset(x + p / 2, cy)
            botones.addOval(Rect(o.x - amp * 0.45f, o.y - amp * 0.45f, o.x + amp * 0.45f, o.y + amp * 0.45f))
        }
        val oro = if (a.lumbre) Brush.horizontalGradient(List(7) { j -> a.luz(P.ORO, luzX(r.left + r.width * j / 6f)) }, r.left, r.right) else null
        drawPath(Path().apply { addPath(c1, Offset(0.6f * u, 0.9f * u)); addPath(c2, Offset(0.6f * u, 0.9f * u)) }, a.sombra(0.4f), style = Stroke(2.2f * u))
        if (oro != null) {
            drawPath(c1, oro, style = Stroke(2f * u)); drawPath(c2, oro, style = Stroke(2f * u)); drawPath(botones, oro)
        } else {
            drawPath(c1, P.ORO, style = Stroke(2f * u)); drawPath(c2, P.ORO, style = Stroke(2f * u)); drawPath(botones, P.ORO)
        }
        drawPath(c1, a.claro(0.35f), style = Stroke(0.6f * u))
    }
}
