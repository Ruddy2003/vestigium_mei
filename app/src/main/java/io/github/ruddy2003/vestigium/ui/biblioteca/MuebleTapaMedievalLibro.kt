package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

/**
 * El códice del libro del día de Scriptorium ([MuebleTapaMedieval]): su sombra en la mesa, el canto de las hojas con los
 * registros, la primera hoja (al abrirse), la tapa de badana con su gofrado, la fecha dorada, la cédula suelta, los bollones,
 * los broches y el lomo con la cuenta de palabras.
 */
internal object LibroTapaMedieval {
    private val P = PaletaMedieval
    private val TINTES = listOf(P.CUERO, mezcla(P.ALMAGRE, P.NOGAL_OSC, 0.3f), mezcla(P.VERDE, P.NOGAL_OSC, 0.38f), mezcla(P.CUERO, P.OCRE, 0.32f))
    private val ORO = Color(0xFFD9B865)
    private val LATON = Color(0xFFB08A48)
    private val SEDAS = listOf(P.RUBRICA, mezcla(P.VERDE, P.OCRE, 0.2f), P.OCRE_PINTURA)

    fun DrawScope.libro(s: SitioTapaMedieval, a: AireMedieval, e: EstadoTapa, medidor: TextMeasurer, ab: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(s.tapa.center)
        val cuero = a.luz(TINTES[Math.floorMod(e.semilla, TINTES.size)], k)
        sombra(s, a)
        canto(s, a, cuero, e, k)
        if (ab > 0.01f) hoja(s, a, e, medidor, k)
        val sx = 1f - 0.9f * ab
        scale(sx, 1f, Offset(s.tapa.left, s.tapa.center.y)) {
            tapaCuero(s, a, cuero, e, k)
            gofrado(s, a, cuero)
            titulo(s, a, cuero, e, medidor, k)
            cedula(s, a, e, cerca(s.cedula.center))
            bollones(s, a, cuero, e, k)
            broches(s, a, cuero, k)
            if (ab > 0f) drawRect(a.sombra(0.45f * ab), s.tapa.topLeft, s.tapa.size)
        }
        if (ab > 0.01f) {
            val borde = s.tapa.left + s.tapa.width * sx
            for (j in 0 until 3) drawRect(a.sombra(0.12f * (3 - j) / 3f), Offset(borde, s.tapa.top + 2f * u), Size((4 + 4 * j) * u, s.tapa.height - 3f * u))
        }
        lomo(s, a, cuero, e, medidor, k)
    }

    /** La sombra del libro en la mesa: violeta, corrida a la derecha y abajo (lejos de la ventana o de la lucerna). */
    private fun DrawScope.sombra(s: SitioTapaMedieval, a: AireMedieval) {
        val u = density
        val l = s.libro
        val fondo = l.bottom + s.grueso + 4f * u
        drawPath(poligonoMedieval(l.left + 8f * u, l.top + 5f * u, l.right + 10f * u, l.top + 9f * u, l.right + 13f * u, fondo + 5f * u, l.left + 12f * u, fondo + 6f * u),
            a.sombra(if (a.oscuro) 0.42f else 0.3f))
        drawRect(a.sombra(0.3f), Offset(l.left + 2f * u, fondo - 1f * u), Size(l.width + 3f * u, 2.6f * u))
    }

    /** Lo que se ve por delante: la tabla de arriba, el bloque de las hojas (engorda con las palabras) y la de abajo; los registros. */
    private fun DrawScope.canto(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, k: Float) {
        val u = density
        val l = s.libro
        val y0 = l.bottom
        val tabla = 1.8f * u
        drawRect(mezcla(cuero, a.hondo, 0.3f), Offset(l.left + 3f * u, y0), Size(l.width - 3f * u, tabla))
        val bloque = Rect(s.lomo.right - 2f * u, y0 + tabla, l.right - 2.5f * u, y0 + tabla + s.grueso)
        drawRect(a.etiqueta(k * 0.85f), bloque.topLeft, bloque.size)
        val lineas = Path()
        var y = bloque.top + 1.1f * u
        while (y < bloque.bottom - 0.5f * u) { lineas.moveTo(bloque.left, y); lineas.lineTo(bloque.right, y); y += 1.3f * u }
        drawPath(lineas, a.luz(P.TINTA, k).copy(alpha = 0.16f), style = Stroke(0.5f * u))
        drawRect(a.frio(0.18f), Offset(bloque.left, bloque.bottom - bloque.height * 0.4f), Size(bloque.width, bloque.height * 0.4f))
        drawRect(mezcla(cuero, a.hondo, 0.4f), Offset(l.left + 3f * u, bloque.bottom), Size(l.width - 2f * u, tabla))
        // Los registros de seda que cuelgan del bloque: uno más cada 400 palabras, hasta tres.
        if (e.vacio || e.palabras <= 0) return
        val n = (1 + e.palabras / 400).coerceAtMost(3)
        for (j in 0 until n) {
            val x = l.left + l.width * (0.36f + 0.09f * j) + 4f * u * azar(e.semilla, 40 + j)
            val largo = (7f + 6f * azar(e.semilla, 50 + j)) * u
            val seda = a.luz(SEDAS[j], k)
            drawLine(a.sombra(0.3f), Offset(x + 1.2f * u, bloque.bottom), Offset(x + 2.4f * u, bloque.bottom + largo + 1f * u), 2f * u)
            drawLine(seda, Offset(x, bloque.top), Offset(x + 1f * u, bloque.bottom + largo), 2f * u, StrokeCap.Square)
            drawLine(mezcla(seda, a.hondo, 0.3f), Offset(x + 0.8f * u, bloque.bottom + largo - 1.4f * u), Offset(x + 1.6f * u, bloque.bottom + largo + 1.2f * u), 1.2f * u)
        }
    }

    /** La primera hoja, que se ve al abrirse la tapa: dos columnas pautadas en rojo, los renglones y la capital azul. */
    private fun DrawScope.hoja(s: SitioTapaMedieval, a: AireMedieval, e: EstadoTapa, medidor: TextMeasurer, k: Float) {
        val u = density
        val pag = Rect(s.tapa.left + 1f * u, s.tapa.top + 2f * u, s.tapa.right - 1f * u, s.tapa.bottom)
        val hoja = a.etiqueta(maxOf(k, 0.6f))
        drawRect(hoja, pag.topLeft, pag.size)
        drawRect(a.sombra(0.14f), pag.topLeft, Size(10f * u, pag.height))
        val m = Rect(pag.left + 18f * u, pag.top + 12f * u, pag.right - 14f * u, pag.bottom - 12f * u)
        val col = (m.width - 12f * u) / 2
        val renglones = Path(); val reglas = Path()
        for (q in 0..1) {
            val x0 = m.left + q * (col + 12f * u)
            reglas.moveTo(x0, m.top); reglas.lineTo(x0, m.bottom); reglas.moveTo(x0 + col, m.top); reglas.lineTo(x0 + col, m.bottom)
            var y = m.top + 4f * u; var j = 0
            while (y < m.bottom) {
                var x = x0 + if (q == 0 && j < 3) 17f * u else 2f * u
                val fin = x0 + col * (if (j % 6 == 5) 0.6f else 1f) - 2f * u
                var p = 0
                while (x < fin - 2f * u) {
                    val largo = minOf((4f + 10f * azar(e.semilla + q * 50 + j, p)) * u, fin - x)
                    renglones.moveTo(x, y); renglones.lineTo(x + largo, y)
                    x += largo + 2.4f * u; p++
                }
                y += 6f * u; j++
            }
        }
        drawPath(reglas, a.luz(P.RUBRICA, k).copy(alpha = 0.4f), style = Stroke(0.5f * u))
        drawPath(renglones, a.luz(P.TINTA, k).copy(alpha = 0.6f), style = Stroke(1.4f * u))
        val capital = Rect(m.left + 1f * u, m.top, m.left + 15f * u, m.top + 15f * u)
        drawRect(a.luz(Color(0xFF2E4C88), k), capital.topLeft, capital.size)
        drawRect(a.luz(ORO, k), capital.topLeft, capital.size, style = Stroke(0.8f * u))
        textoEn(medidor, "D", capital, LetraExtra.gotica, hoja, 13f)
    }

    /**
     * La badana teñida sobre la tabla: la luz que viene de la ventana (o de la lucerna), las manchas y los roces del uso, el
     * canto redondeado de la tabla que toma la luz arriba y a la izquierda.
     */
    private fun DrawScope.tapaCuero(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, k: Float) {
        val u = density
        val t = s.tapa
        drawRoundRect(cuero, t.topLeft, t.size, CornerRadius(3f * u))
        // La luz de la ventana (de día) o de la lucerna (de noche) entra por la izquierda y se pierde hacia el broche.
        val luz = if (a.oscuro) P.LLAMA.copy(alpha = 0.16f) else a.claro(0.2f)
        drawRect(Brush.linearGradient(listOf(luz, Color.Transparent, a.frio(if (a.oscuro) 0.1f else 0.2f)), t.topLeft, t.bottomRight), t.topLeft, t.size)
        // El uso: manchas más hondas de la grasa de las manos y los roces claros.
        val sem = e.semilla
        for (j in 0 until 5) {
            val c = Offset(t.left + t.width * (0.08f + 0.84f * azar(sem, 60 + j)), t.top + t.height * (0.1f + 0.8f * azar(sem, 70 + j)))
            val rx = t.width * (0.05f + 0.07f * azar(sem, 80 + j)); val ry = rx * (0.4f + 0.3f * azar(sem, 90 + j))
            drawPath(borronTapa(c, rx, ry, sem + j), mezcla(cuero, a.hondo, 0.22f).copy(alpha = 0.5f))
        }
        // El grano de la badana: pinceladas cortas más hondas y más claras.
        pinceladas(t, mezcla(cuero, a.hondo, 0.35f), sem + 7, 16, u, 0.14f)
        pinceladas(t, a.claro(1f), sem + 8, 8, u, 0.06f)
        drawOval(a.claro(0.08f), Offset(t.left + t.width * 0.12f, t.bottom - t.height * 0.2f), Size(t.width * 0.3f, t.height * 0.12f))
        for (j in 0 until 3) {
            val x = t.left + t.width * (0.15f + 0.7f * azar(sem, 100 + j)); val y = t.top + t.height * (0.15f + 0.7f * azar(sem, 110 + j))
            drawLine(a.claro(0.18f), Offset(x, y), Offset(x + (6f + 8f * azar(sem, 120 + j)) * u, y + (1f + 3f * azar(sem, 130 + j)) * u), 0.6f * u)
        }
        // El canto de la tabla: luz arriba y a la izquierda, sombra abajo y a la derecha, y el roce de las orillas.
        drawRect(a.claro(0.3f), t.topLeft, Size(t.width, 1.4f * u))
        drawRect(a.frio(0.35f), Offset(t.left, t.bottom - 1.6f * u), Size(t.width, 1.6f * u))
        drawRect(a.frio(0.28f), Offset(t.right - 1.6f * u, t.top), Size(1.6f * u, t.height))
        drawRoundRect(mezcla(cuero, P.BADANA, 0.22f).copy(alpha = 0.5f), t.topLeft + Offset(1.4f * u, 1.4f * u), Size(t.width - 2.8f * u, t.height - 2.8f * u),
            CornerRadius(2.5f * u), style = Stroke(1.2f * u))
    }

    /** Una mancha de contorno irregular. */
    private fun borronTapa(c: Offset, rx: Float, ry: Float, sem: Int): Path = Path().apply {
        val n = 9
        for (i in 0 until n) {
            val ang = i * 6.2832f / n
            val f = 0.75f + 0.4f * azar(sem, 140 + i)
            val x = c.x + cos(ang) * rx * f; val y = c.y + sin(ang) * ry * f
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * El gofrado en seco: los filetes triples del marco de fuera y el de dentro, unidos en las esquinas por un filete en
     * diagonal, y entre los dos una hilera de florones estampados a mano uno por uno (no del todo parejos). Hundido: la
     * huella en sombra y su labio de luz debajo.
     */
    private fun DrawScope.gofrado(s: SitioTapaMedieval, a: AireMedieval, cuero: Color) {
        val u = density
        val fuera = s.tapa.deflate(6.5f * u)
        val dentro = fuera.deflate(9.5f * u)
        val huella = mezcla(cuero, a.hondo, 0.45f)
        val labio = a.claro(0.22f)
        val filetes = Path()
        for (r in listOf(fuera, fuera.deflate(1.8f * u), dentro, dentro.deflate(1.8f * u))) filetes.addRect(r)
        for ((p, q) in listOf(fuera.topLeft to dentro.topLeft, fuera.topRight to dentro.topRight, fuera.bottomLeft to dentro.bottomLeft, fuera.bottomRight to dentro.bottomRight)) {
            filetes.moveTo(p.x, p.y); filetes.lineTo(q.x, q.y)
        }
        drawPath(corridoMedieval(filetes, 0.7f * u, 0.8f * u), labio, style = Stroke(0.7f * u))
        drawPath(filetes, huella, style = Stroke(0.8f * u))
        // Los florones de la banda: cuatro pétalos y un botón, a mano (un poco corridos), salvo donde van los bollones.
        val banda = fuera.deflate(4.75f * u)
        val florones = Path()
        val botones = Path()
        fun floron(c: Offset, r: Float) {
            florones.addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r))
            botones.addOval(Rect(c.x - r * 0.35f, c.y - r * 0.35f, c.x + r * 0.35f, c.y + r * 0.35f))
        }
        val paso = 11f * u
        val r = 2.1f * u
        var x = banda.left + 15f * u; var i = 0
        while (x < banda.right - 14f * u) {
            floron(Offset(x + 0.8f * u * azar(i, 1), banda.top + 0.6f * u * azar(i, 2)), r)
            floron(Offset(x + 0.8f * u * azar(i, 3), banda.bottom + 0.6f * u * azar(i, 4)), r)
            x += paso; i++
        }
        var y = banda.top + 15f * u
        while (y < banda.bottom - 14f * u) {
            floron(Offset(banda.left + 0.6f * u * azar(i, 5), y), r)
            floron(Offset(banda.right + 0.6f * u * azar(i, 6), y), r)
            y += paso; i++
        }
        drawPath(corridoMedieval(florones, 0.5f * u, 0.6f * u), labio, style = Stroke(0.6f * u))
        drawPath(florones, huella, style = Stroke(0.8f * u))
        drawPath(botones, huella)
    }

    /** El compartimento del título: hundido en la badana, con su filete dorado y la fecha en oro. */
    private fun DrawScope.titulo(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, medidor: TextMeasurer, k: Float) {
        val u = density
        val r = s.titulo
        if (r.height < 4f * u) return
        drawRect(mezcla(cuero, a.hondo, 0.3f), r.topLeft, r.size)
        drawRect(a.frio(0.3f), r.topLeft, Size(r.width, 1.2f * u))
        drawRect(a.claro(0.2f), Offset(r.left, r.bottom - 0.8f * u), Size(r.width, 0.8f * u))
        val oro = mezcla(a.luz(ORO, k), ORO, if (a.oscuro) 0.55f else 0.2f)
        drawRect(oro.copy(alpha = 0.7f), r.deflate(1.6f * u).topLeft, r.deflate(1.6f * u).size, style = Stroke(0.7f * u))
        textoEn(medidor, e.fecha, r.deflate(2.6f * u), P.LETRA, oro, 13f, peso = FontWeight.Bold, minimo = 7f)
    }

    /**
     * La cédula suelta: pergamino de orillas desiguales con su sombra en la badana, la pauta a punta seca (los picados de las
     * orillas y las dos reglas del margen) y la esquina de abajo levantada. Escrita, con el punto de rúbrica en el margen.
     */
    private fun DrawScope.cedula(s: SitioTapaMedieval, a: AireMedieval, e: EstadoTapa, kc: Float) {
        val u = density
        val r = s.cedula
        val sem = e.semilla
        val dobla = 9f * u
        val p = poligonoMedieval(r.left + 0.8f * u * azar(sem, 1), r.top + 0.6f * u, r.left + r.width * 0.45f, r.top + 0.9f * u * azar(sem, 2),
            r.right, r.top, r.right - 0.5f * u, r.bottom - dobla, r.right - dobla, r.bottom - 0.3f * u,
            r.left + r.width * 0.5f, r.bottom - 0.8f * u * azar(sem, 3), r.left, r.bottom, r.left + 0.6f * u, r.top + r.height * 0.5f)
        drawPath(corridoMedieval(p, 2f * u, 2.4f * u), a.sombra(if (a.oscuro) 0.5f else 0.32f))
        val perg = a.etiqueta(maxOf(kc, 0.62f))
        drawPath(p, perg)
        // La cara de la cédula: más clara arriba a la izquierda, algo amarilla hacia la orilla.
        drawRect(Brush.linearGradient(listOf(a.claro(0.18f), Color.Transparent, mezcla(P.PERG_OSC, perg, 0.5f).copy(alpha = 0.35f)), r.topLeft, r.bottomRight), r.topLeft, Size(r.width - dobla, r.height - dobla))
        // La pauta a punta seca: los picados y las dos reglas, apenas.
        val pauta = Path()
        var y = r.top + 5f * u
        while (y < r.bottom - 4f * u) {
            pauta.addOval(Rect(r.left + 3f * u, y - 0.5f * u, r.left + 4f * u, y + 0.5f * u))
            pauta.addOval(Rect(r.right - 4f * u, y - 0.5f * u, r.right - 3f * u, y + 0.5f * u))
            y += 6.5f * u
        }
        drawPath(pauta, a.luz(P.TINTA, kc).copy(alpha = 0.3f))
        val reglas = a.luz(P.RUBRICA, kc).copy(alpha = if (e.vacio) 0.18f else 0.3f)
        drawLine(reglas, Offset(r.left + 8f * u, r.top + 3f * u), Offset(r.left + 8f * u, r.bottom - 3f * u), 0.5f * u)
        drawLine(reglas, Offset(r.right - 8f * u, r.top + 3f * u), Offset(r.right - 8f * u, r.bottom - dobla - 1f * u), 0.5f * u)
        if (!e.vacio) drawCircle(a.luz(P.RUBRICA, maxOf(kc, 0.5f)), 1.7f * u, Offset(r.left + 8f * u, r.top + 7f * u))
        // La esquina levantada: el revés, más amarillo, y su sombra.
        val esquina = poligonoMedieval(r.right - 0.5f * u, r.bottom - dobla, r.right - dobla, r.bottom - 0.3f * u, r.right - dobla * 1.1f, r.bottom - dobla * 1.15f)
        drawPath(corridoMedieval(esquina, 1f * u, 1.4f * u), a.sombra(0.3f))
        drawPath(esquina, mezcla(perg, P.PERG_OSC, 0.55f))
        drawPath(esquina, a.frio(0.25f), style = Stroke(0.5f * u))
        drawPath(p, a.frio(0.22f), style = Stroke(0.6f * u))
    }

    /** Los cuatro bollones de las esquinas y el de abajo; en los Sueños, el de abajo es un creciente de oro con dos estrellitas. */
    private fun DrawScope.bollones(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, k: Float) {
        val u = density
        val m = s.tapa.deflate(11f * u)
        val laton = a.luz(LATON, k)
        fun bollon(c: Offset, r: Float) {
            drawCircle(a.sombra(0.4f), r, c + Offset(1.2f * u, 1.6f * u))
            drawCircle(mezcla(laton, a.hondo, 0.25f), r, c)
            drawCircle(laton, r * 0.78f, c - Offset(r * 0.12f, r * 0.12f))
            drawCircle(a.claro(0.6f), r * 0.26f, c - Offset(r * 0.34f, r * 0.34f))
            if (a.oscuro) drawCircle(a.filo(0.45f * k), r * 0.3f, c - Offset(r * 0.4f, r * 0.1f))
        }
        listOf(m.topLeft, m.topRight, m.bottomLeft, m.bottomRight).forEach { bollon(it, 5.4f * u) }
        val cb = Offset(s.cedula.center.x, (s.cedula.bottom + s.tapa.bottom) / 2 + 1f * u)
        val rb = minOf(4.6f * u, (s.tapa.bottom - s.cedula.bottom) * 0.3f)
        if (rb < 1.5f * u) return
        if (e.noche) {
            val oro = mezcla(a.luz(ORO, k), ORO, 0.5f)
            drawCircle(oro, rb * 1.2f, cb)
            drawCircle(cuero, rb * 1.02f, Offset(cb.x + rb * 0.55f, cb.y - rb * 0.35f))
            for (sg in listOf(-1f, 1f)) drawCircle(oro, 1.1f * u, Offset(cb.x + sg * 14f * u, cb.y))
        } else bollon(cb, rb)
    }

    /** Las dos correas que vienen de la tabla de abajo por el canto y se enganchan en su pasador de latón. */
    private fun DrawScope.broches(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, k: Float) {
        val u = density
        val t = s.tapa
        val correa = mezcla(cuero, a.hondo, 0.18f)
        val laton = a.luz(LATON, k)
        for (f in listOf(0.27f, 0.73f)) {
            val y = t.top + t.height * f
            val x0 = t.right - 17f * u
            drawRect(a.sombra(0.35f), Offset(x0 + 1f * u, y - 3f * u + 1.4f * u), Size(t.right - x0, 6f * u))
            drawRect(correa, Offset(x0 + 4f * u, y - 3f * u), Size(t.right - x0 - 2f * u, 6f * u))
            drawRect(a.claro(0.2f), Offset(x0 + 4f * u, y - 3f * u), Size(t.right - x0 - 2f * u, 0.8f * u))
            // La chapa del extremo y el pasador donde se engancha.
            val chapa = Rect(x0, y - 3.6f * u, x0 + 7f * u, y + 3.6f * u)
            drawRect(laton, chapa.topLeft, chapa.size)
            drawRect(mezcla(laton, a.hondo, 0.4f), chapa.deflate(1.5f * u).topLeft, chapa.deflate(1.5f * u).size, style = Stroke(0.6f * u))
            drawCircle(a.luz(P.HIERRO, k), 1.4f * u, Offset(x0 - 2.6f * u, y))
            drawLine(laton, Offset(x0 - 2.6f * u, y), Offset(x0, y), 1.4f * u)
            drawCircle(a.claro(0.5f), 0.7f * u, Offset(x0 + 1.6f * u, y - 2f * u))
            // La correa da la vuelta por el canto, por fuera del libro.
            drawRect(mezcla(correa, a.hondo, 0.25f), Offset(t.right - 1f * u, y - 3f * u), Size(2.4f * u, 6f * u))
        }
    }

    /**
     * El lomo redondo, a la izquierda: la luz de la ventana en su lado, los cuatro nervios dobles, las cabezadas de hilo de
     * colores en los extremos y, escrito, la cuenta de palabras dorada a lo largo.
     */
    private fun DrawScope.lomo(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, medidor: TextMeasurer, k: Float) {
        val u = density
        val l = s.lomo
        val piel = mezcla(cuero, a.hondo, 0.12f)
        // Las cabezadas que asoman arriba y abajo.
        for (y in listOf(l.top - 1.6f * u, l.bottom - 1.4f * u)) {
            drawRoundRect(a.luz(P.LINO, k), Offset(l.left + 3f * u, y), Size(l.width - 5f * u, 3f * u), CornerRadius(1.5f * u))
            drawLine(a.luz(P.RUBRICA, k), Offset(l.left + 5f * u, y + 1.5f * u), Offset(l.right - 4f * u, y + 1.5f * u), 0.8f * u)
        }
        drawRoundRect(piel, l.topLeft, l.size, CornerRadius(4f * u))
        drawRect(Brush.horizontalGradient(listOf(if (a.oscuro) P.LLAMA.copy(alpha = 0.22f) else a.claro(0.3f), Color.Transparent, a.frio(0.4f)), l.left, l.right), l.topLeft, l.size)
        drawRect(a.frio(0.4f), Offset(l.right - 1.8f * u, l.top), Size(1.8f * u, l.height))
        val oro = mezcla(a.luz(ORO, k), ORO, if (a.oscuro) 0.5f else 0.15f)
        for (f in listOf(0.1f, 0.27f, 0.73f, 0.9f)) {
            val y = l.top + l.height * f
            drawRect(a.claro(0.22f), Offset(l.left + 1f * u, y - 2.4f * u), Size(l.width - 2.5f * u, 1.4f * u))
            drawRect(a.frio(0.45f), Offset(l.left + 1f * u, y - 0.8f * u), Size(l.width - 2.5f * u, 1.8f * u))
            drawLine(oro.copy(alpha = 0.5f), Offset(l.left + 2.5f * u, y + 2.4f * u), Offset(l.right - 4f * u, y + 2.4f * u), 0.6f * u)
        }
        if (!e.vacio && e.palabras > 0) {
            textoEn(medidor, palabrasTapa(e.palabras), Rect(l.left + 1f * u, l.top + l.height * 0.31f, l.right - 3f * u, l.top + l.height * 0.69f),
                P.LETRA, oro, 10f, vertical = true, peso = FontWeight.Bold, minimo = 6f)
        }
    }

    fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"
}
