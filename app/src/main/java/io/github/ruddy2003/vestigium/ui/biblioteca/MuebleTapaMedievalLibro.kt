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
 * El códice del libro del día de Scriptorium ([MuebleTapaMedieval]): su sombra en la mesa, lo grueso del libro (las tablas y
 * el bloque de las hojas, abajo y a la derecha) con los registros, la primera hoja (al abrirse), la tapa de badana con su
 * gofrado, la fecha dorada, la cédula suelta, los bollones, las manecillas y el lomo con la cuenta de palabras.
 */
internal object LibroTapaMedieval {
    private val P = PaletaMedieval
    /** La badana del diario: carmesí de quermes, siempre la misma; la de los sueños, índigo. */
    private val DIARIO = Color(0xFF7C2434)
    private val SUENO = Color(0xFF2D3A62)
    private val ORO = Color(0xFFD9B865)
    private val LATON = Color(0xFFB8924C)
    private val SEDAS = listOf(P.RUBRICA, mezcla(P.VERDE, P.OCRE, 0.2f), P.OCRE_PINTURA)

    private fun cerca(p: Offset, s: SitioTapaMedieval, a: AireMedieval) = MuebleTapaMedieval.cerca(p, s, a)

    fun DrawScope.libro(s: SitioTapaMedieval, a: AireMedieval, e: EstadoTapa, medidor: TextMeasurer, ab: Float) {
        val u = density
        val k = cerca(s.tapa.center, s, a)
        val cuero = a.luz(if (e.noche) SUENO else DIARIO, k)
        sombra(s, a)
        grueso(s, a, cuero, e, k)
        if (ab > 0.01f) hoja(s, a, e, medidor, k)
        val sx = 1f - 0.9f * ab
        scale(sx, 1f, Offset(s.tapa.left, s.tapa.center.y)) {
            tapaCuero(s, a, cuero, e)
            gofrado(s, a, cuero, k)
            titulo(s, a, cuero, e, medidor, k)
            cedula(s, a, e, cerca(s.cedula.center, s, a))
            bollones(s, a, cuero, e, k)
            manecillas(s, a, cuero, k)
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
        val l = s.libro; val f = s.fondo
        val sombra = a.sombra(if (a.oscuro) 0.45f else 0.3f)
        // Solo la parte que asoma (a la derecha y abajo): debajo del libro no hace falta pintarla.
        val x0 = l.right + f.x - 1f * u; val y0 = l.bottom + f.y - 1f * u
        drawPath(poligonoMedieval(x0, l.top + 7f * u, x0 + 12f * u, l.top + f.y + 9f * u, x0 + 13f * u, y0 + 7f * u, l.left + 12f * u, y0 + 8f * u,
            l.left + 9f * u, y0, x0, y0), sombra)
        drawRect(a.sombra(0.32f), Offset(l.left + f.x, l.bottom + f.y - 1f * u), Size(l.width + 2f * u, 2.4f * u))
    }

    /**
     * Lo grueso del libro, abajo y a la derecha: la tabla de atrás, el bloque de las hojas (el canto de delante y el de abajo,
     * con sus hojas) y el borde de la tabla de arriba; las correas que dan la vuelta por el canto; los registros que cuelgan.
     */
    private fun DrawScope.grueso(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, k: Float) {
        val u = density
        val c = s.tapa; val f = s.fondo; val izq = s.lomo.left + 4f * u
        val total = s.grueso + 2 * s.tabla
        fun en(t: Float) = Offset(f.x * t / total, f.y * t / total)
        val hondo = mezcla(cuero, a.hondo, 0.35f)
        // Las tablas (su canto de cuero) y, entre ellas, el bloque de las hojas.
        drawPath(poligonoMedieval(c.right, c.top + 2f * u, c.right + f.x, c.top + f.y, c.right + f.x, c.bottom + f.y, izq + f.x, c.bottom + f.y, izq, c.bottom, c.right, c.bottom), hondo)
        val t0 = en(s.tabla); val t1 = en(s.tabla + s.grueso)
        val hojas = a.etiqueta(k * 0.9f)
        drawPath(poligonoMedieval(c.right + t0.x, c.top + 2f * u + t0.y, c.right + t1.x, c.top + 2f * u + t1.y, c.right + t1.x, c.bottom + t1.y, c.right + t0.x, c.bottom + t0.y), mezcla(hojas, a.hondo, 0.12f))
        drawPath(poligonoMedieval(izq + 1f * u + t0.x, c.bottom + t0.y, c.right + t0.x, c.bottom + t0.y, c.right + t1.x, c.bottom + t1.y, izq + 1f * u + t1.x, c.bottom + t1.y), hojas)
        val lineas = Path()
        val n = (s.grueso / (1.4f * u)).toInt().coerceIn(1, 8)
        for (j in 1..n) {
            val p = en(s.tabla + s.grueso * j / (n + 1f))
            lineas.moveTo(c.right + p.x, c.top + 3f * u + p.y); lineas.lineTo(c.right + p.x, c.bottom + p.y)
            lineas.moveTo(izq + 2f * u + p.x, c.bottom + p.y); lineas.lineTo(c.right + p.x, c.bottom + p.y)
        }
        drawPath(lineas, a.luz(P.TINTA, k).copy(alpha = 0.18f), style = Stroke(0.5f * u))
        // El canto de la derecha queda en la sombra (de día, lejos de la ventana; de noche, de espaldas a la lucerna).
        drawPath(poligonoMedieval(c.right, c.top + 2f * u, c.right + f.x, c.top + f.y, c.right + f.x, c.bottom + f.y, c.right, c.bottom), a.frio(if (a.oscuro) 0.3f else 0.22f))
        // Las correas de las manecillas, que vienen de la tabla de atrás por el canto.
        for (fr in MANECILLAS) {
            val y = c.top + c.height * fr
            drawPath(poligonoMedieval(c.right, y - 3f * u, c.right + f.x, y - 3f * u + f.y, c.right + f.x, y + 3f * u + f.y, c.right, y + 3f * u), mezcla(cuero, a.hondo, 0.25f))
        }
        if (e.vacio || e.palabras <= 0) return
        // Los registros de seda que cuelgan del bloque: uno más cada 400 palabras, hasta tres.
        val m = (1 + e.palabras / 400).coerceAtMost(3)
        for (j in 0 until m) {
            val x = c.left + c.width * (0.24f + 0.09f * j) + 4f * u * azar(e.semilla, 40 + j)
            val largo = (6f + 6f * azar(e.semilla, 50 + j)) * u
            val y0 = c.bottom + t0.y; val y1 = c.bottom + f.y + largo
            val seda = a.luz(SEDAS[j], k)
            drawLine(a.sombra(0.3f), Offset(x + f.x + 1.4f * u, c.bottom + f.y), Offset(x + f.x + 2.6f * u, y1 + 1f * u), 2f * u)
            drawLine(seda, Offset(x + t0.x, y0), Offset(x + f.x + 1f * u, y1), 2f * u, StrokeCap.Square)
            drawLine(mezcla(seda, a.hondo, 0.3f), Offset(x + f.x + 0.8f * u, y1 - 1.4f * u), Offset(x + f.x + 1.6f * u, y1 + 1.2f * u), 1.2f * u)
        }
    }

    /** La primera hoja, que se ve al abrirse la tapa: dos columnas pautadas en rojo, los renglones y la capital azul. */
    private fun DrawScope.hoja(s: SitioTapaMedieval, a: AireMedieval, e: EstadoTapa, medidor: TextMeasurer, k: Float) {
        val u = density
        val pag = Rect(s.tapa.left + 1f * u, s.tapa.top + 1f * u, s.tapa.right, s.tapa.bottom)
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
     * La badana sobre la tabla: la luz que viene de la ventana (o de la lucerna) por la izquierda y se pierde hacia el broche,
     * el grano del cuero, las manchas de las manos, los roces claros de las esquinas y del filo del lomo, y el canto redondeado
     * de la tabla, que toma la luz arriba y a la izquierda.
     */
    private fun DrawScope.tapaCuero(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa) {
        val u = density
        val t = s.tapa
        // La luz va en el mismo relleno: claro arriba a la izquierda (la ventana o la lucerna), hondo y frío abajo a la derecha.
        val claro = if (a.oscuro) mezcla(cuero, P.LLAMA, 0.18f) else mezcla(cuero, mezcla(P.SOL, P.LLAMA, 0.3f), 0.24f)
        val hondo = if (a.oscuro) MuebleTapaMedieval.lejano(mezcla(cuero, a.hondo, 0.15f), a) else mezcla(cuero, P.FRIO, 0.26f)
        drawRoundRect(Brush.linearGradient(listOf(claro, hondo), t.topLeft, t.bottomRight), t.topLeft, t.size, CornerRadius(3f * u))
        pinceladas(t, mezcla(cuero, a.hondo, 0.4f), 2201, 14, u, 0.16f)
        pinceladas(t, mezcla(cuero, P.BADANA, 0.3f), 2202, 8, u, 0.08f)
        for (j in 0 until 3) {
            val c = Offset(t.left + t.width * (0.15f + 0.7f * azar(2210, j)), t.top + t.height * (0.2f + 0.6f * azar(2211, j)))
            val rx = t.width * (0.05f + 0.05f * azar(2212, j)); val ry = rx * 0.5f
            drawPath(borronTapa(c, rx, ry, 2213 + j), mezcla(cuero, a.hondo, 0.25f).copy(alpha = 0.45f))
        }
        // Los roces: las esquinas y el filo junto al lomo, donde la badana se gasta y aclara.
        val roce = mezcla(cuero, P.OCRE, 0.4f).copy(alpha = if (a.oscuro) 0.28f else 0.42f)
        for ((esq, sg) in listOf(t.topLeft to Offset(1f, 1f), t.topRight to Offset(-1f, 1f), t.bottomLeft to Offset(1f, -1f), t.bottomRight to Offset(-1f, -1f))) {
            drawPath(borronTapa(esq + Offset(sg.x * 6f * u, sg.y * 5f * u), 9f * u, 6f * u, 2220 + (esq.x + esq.y).toInt()), roce)
        }
        drawRect(roce.copy(alpha = roce.alpha * 0.7f), Offset(t.left, t.top + 6f * u), Size(3.5f * u, t.height - 12f * u))
        // El canto de la tabla.
        drawRect(if (a.oscuro) a.filo(0.35f) else a.claro(0.42f), t.topLeft, Size(t.width, 1.4f * u))
        drawRect(a.claro(0.2f), t.topLeft, Size(1.4f * u, t.height))
        drawRect(a.frio(0.4f), Offset(t.left, t.bottom - 1.6f * u), Size(t.width, 1.6f * u))
        drawRect(a.frio(0.32f), Offset(t.right - 1.6f * u, t.top), Size(1.6f * u, t.height))
    }

    /** Una mancha de contorno irregular. */
    private fun borronTapa(c: Offset, rx: Float, ry: Float, sem: Int): Path = Path().apply {
        val n = 9
        for (i in 0 until n) {
            val ang = i * 6.2832f / n
            val f = 0.72f + 0.42f * azar(sem, 140 + i)
            val x = c.x + cos(ang) * rx * f; val y = c.y + sin(ang) * ry * f
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * El gofrado en seco, que la luz rasante levanta: los filetes triples del marco de fuera y el de dentro, unidos en las
     * esquinas por un filete en diagonal, y entre los dos una hilera de florones estampados uno por uno. Cada huella es un surco:
     * la pared que da a la luz, en sombra; la de enfrente, con su labio claro.
     */
    private fun DrawScope.gofrado(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, k: Float) {
        val u = density
        val fuera = s.tapa.deflate(6.5f * u)
        val dentro = fuera.deflate(9.5f * u)
        val huella = mezcla(cuero, a.hondo, 0.6f)
        val labio = if (a.oscuro) a.filo(0.45f * k + 0.05f) else mezcla(cuero, P.SOL, 0.45f).copy(alpha = 0.75f)
        val filetes = Path()
        for (r in listOf(fuera, fuera.deflate(1.9f * u), dentro, dentro.deflate(1.9f * u))) filetes.addRect(r)
        for ((p, q) in listOf(fuera.topLeft to dentro.topLeft, fuera.topRight to dentro.topRight, fuera.bottomLeft to dentro.bottomLeft, fuera.bottomRight to dentro.bottomRight)) {
            filetes.moveTo(p.x, p.y); filetes.lineTo(q.x, q.y)
        }
        drawPath(corridoMedieval(filetes, 0.75f * u, 0.85f * u), labio, style = Stroke(0.7f * u))
        drawPath(filetes, huella, style = Stroke(0.9f * u))
        val banda = fuera.deflate(4.75f * u)
        val florones = Path(); val botones = Path()
        fun floron(c: Offset, r: Float) {
            florones.addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r))
            botones.addOval(Rect(c.x - r * 0.35f, c.y - r * 0.35f, c.x + r * 0.35f, c.y + r * 0.35f))
        }
        val paso = 13f * u
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
        // Los florones, con un solo trazo (su labio de luz es el botón claro, corrido): pintar cien anillos dos veces pesaba.
        drawPath(florones, huella, style = Stroke(0.9f * u))
        drawPath(corridoMedieval(botones, 0.5f * u, 0.5f * u), labio)
        drawPath(botones, huella)
    }

    /** El compartimento del título: hundido en la badana (sombra arriba, labio de luz abajo), con su filete dorado y la fecha en oro. */
    private fun DrawScope.titulo(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, medidor: TextMeasurer, k: Float) {
        val u = density
        val r = s.titulo
        if (r.height < 4f * u) return
        drawRect(mezcla(cuero, a.hondo, 0.38f), r.topLeft, r.size)
        drawRect(a.frio(0.4f), r.topLeft, Size(r.width, 1.6f * u))
        drawRect(a.frio(0.25f), r.topLeft, Size(1.4f * u, r.height))
        drawRect(if (a.oscuro) a.filo(0.3f * k) else mezcla(cuero, P.SOL, 0.4f).copy(alpha = 0.6f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        val oro = mezcla(a.luz(ORO, k), ORO, if (a.oscuro) 0.6f else 0.2f)
        drawRect(oro.copy(alpha = 0.7f), r.deflate(1.8f * u).topLeft, r.deflate(1.8f * u).size, style = Stroke(0.7f * u))
        textoEn(medidor, e.fecha, r.deflate(2.8f * u), P.LETRA, oro, 13f, peso = FontWeight.Bold, minimo = 7f)
    }

    /**
     * La cédula suelta: pergamino de orillas desiguales con su sombra en la badana, la pauta a punta seca (los picados de las
     * orillas y las dos reglas del margen) y la esquina de abajo levantada. Escrita, con el punto de rúbrica en el margen.
     */
    private fun DrawScope.cedula(s: SitioTapaMedieval, a: AireMedieval, e: EstadoTapa, kc: Float) {
        val u = density
        val r = s.cedula
        val dobla = 9f * u
        val p = poligonoMedieval(r.left + 0.6f * u, r.top + 0.6f * u, r.left + r.width * 0.45f, r.top + 0.3f * u,
            r.right, r.top, r.right - 0.5f * u, r.bottom - dobla, r.right - dobla, r.bottom - 0.3f * u,
            r.left + r.width * 0.5f, r.bottom - 0.6f * u, r.left, r.bottom, r.left + 0.6f * u, r.top + r.height * 0.5f)
        drawPath(poligonoMedieval(r.right - 1f * u, r.top + 2.6f * u, r.right + 2.2f * u, r.top + 3f * u, r.right + 2.2f * u, r.bottom + 2.6f * u,
            r.left + 2.2f * u, r.bottom + 2.6f * u, r.left + 2f * u, r.bottom - 1f * u, r.right - 1f * u, r.bottom - 1f * u), a.sombra(if (a.oscuro) 0.5f else 0.36f))
        val perg = a.etiqueta(maxOf(kc, 0.66f))
        drawPath(p, perg)
        drawOval(mezcla(P.PERG_OSC, perg, 0.5f).copy(alpha = 0.16f), Offset(r.left + r.width * 0.55f, r.top + r.height * 0.55f), Size(r.width * 0.42f, r.height * 0.4f))
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
        val esquina = poligonoMedieval(r.right - 0.5f * u, r.bottom - dobla, r.right - dobla, r.bottom - 0.3f * u, r.right - dobla * 1.1f, r.bottom - dobla * 1.15f)
        drawPath(corridoMedieval(esquina, 1f * u, 1.4f * u), a.sombra(0.3f))
        drawPath(esquina, mezcla(perg, P.PERG_OSC, 0.55f))
        drawPath(esquina, a.frio(0.25f), style = Stroke(0.5f * u))
        drawPath(p, a.frio(0.22f), style = Stroke(0.6f * u))
    }

    /**
     * Los cuatro bollones de las esquinas y el de abajo: la sombra que echan (de día abajo a la derecha; de noche, lejos de la
     * lucerna), el anillo de la base, la cúpula y su brillo. En los Sueños, el de abajo es un creciente de oro.
     */
    private fun DrawScope.bollones(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, k: Float) {
        val u = density
        val m = s.tapa.deflate(11f * u)
        val laton = a.luz(LATON, maxOf(k, 0.25f))
        val caida = if (a.oscuro) Offset(2.6f * u, 1.2f * u) else Offset(2f * u, 2.6f * u)
        fun bollon(c: Offset, r: Float) {
            drawCircle(a.sombra(0.5f), r * 1.02f, c + caida)
            drawCircle(mezcla(laton, a.hondo, 0.4f), r, c)
            drawCircle(laton, r * 0.8f, c - Offset(r * 0.08f, r * 0.08f))
            drawArc(a.frio(0.4f), 0f, 120f, false, c - Offset(r * 0.72f, r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(r * 0.22f))
            drawCircle(if (a.oscuro) a.filo(0.9f * k + 0.1f) else Color(0xFFFFF4D8).copy(alpha = 0.85f), r * 0.24f, c - Offset(r * 0.32f, r * 0.32f))
        }
        listOf(m.topLeft, m.topRight, m.bottomLeft, m.bottomRight).forEach { bollon(it, 5.4f * u) }
        val cb = Offset(s.cedula.center.x, (s.cedula.bottom + s.tapa.bottom) / 2 + 1f * u)
        val rb = minOf(4.6f * u, (s.tapa.bottom - s.cedula.bottom) * 0.32f)
        if (rb < 1.5f * u) return
        if (e.noche) {
            val oro = mezcla(a.luz(ORO, k), ORO, 0.55f)
            drawCircle(a.sombra(0.4f), rb * 1.2f, cb + caida * 0.6f)
            drawCircle(oro, rb * 1.2f, cb)
            drawCircle(cuero, rb * 1.02f, Offset(cb.x + rb * 0.55f, cb.y - rb * 0.35f))
            for (sg in listOf(-1f, 1f)) drawCircle(oro, 1.1f * u, Offset(cb.x + sg * 14f * u, cb.y))
        } else bollon(cb, rb)
    }

    /** Dónde van las dos manecillas, en fracción del alto de la tapa. */
    private val MANECILLAS = listOf(0.27f, 0.73f)

    /**
     * Las dos manecillas: la correa que viene de la tabla de atrás por el canto, la chapa de latón remachada en su punta, con
     * su lengüeta, y el pasador clavado en la tapa donde se engancha.
     */
    private fun DrawScope.manecillas(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, k: Float) {
        val u = density
        val t = s.tapa
        val correa = mezcla(cuero, a.hondo, 0.2f)
        val laton = a.luz(LATON, maxOf(k, 0.3f))
        for (f in MANECILLAS) {
            val y = t.top + t.height * f
            val x0 = t.right - 18f * u
            drawRect(a.sombra(0.38f), Offset(x0 + 1f * u, y - 3f * u + 1.6f * u), Size(t.right - x0, 6f * u))
            drawRect(correa, Offset(x0 + 5f * u, y - 3f * u), Size(t.right - x0 - 5f * u, 6f * u))
            drawRect(a.claro(0.2f), Offset(x0 + 5f * u, y - 3f * u), Size(t.right - x0 - 5f * u, 0.8f * u))
            // La chapa con su lengüeta en punta, que monta sobre el pasador.
            val chapa = poligonoMedieval(x0 + 9f * u, y - 3.8f * u, x0 + 2f * u, y - 3.8f * u, x0 - 2.5f * u, y, x0 + 2f * u, y + 3.8f * u, x0 + 9f * u, y + 3.8f * u)
            drawPath(chapa, laton)
            drawPath(chapa, mezcla(laton, a.hondo, 0.45f), style = Stroke(0.6f * u))
            drawLine(a.claro(0.55f), Offset(x0 + 2f * u, y - 3.2f * u), Offset(x0 + 8.5f * u, y - 3.2f * u), 0.7f * u)
            for (dx in listOf(4.5f, 7.2f)) drawCircle(mezcla(laton, a.hondo, 0.5f), 0.7f * u, Offset(x0 + dx * u, y))
            drawCircle(a.sombra(0.4f), 1.5f * u, Offset(x0 - 3.2f * u, y + 0.8f * u))
            drawCircle(a.luz(P.HIERRO, maxOf(k, 0.3f)), 1.3f * u, Offset(x0 - 3.6f * u, y))
        }
    }

    /**
     * El lomo redondo, a la izquierda: la luz de la ventana (o de la lucerna) en su lado, el roce claro de la cresta, los cuatro
     * nervios en relieve (luz arriba, sombra abajo), las cabezadas de hilo en los extremos y, escrito, la cuenta dorada.
     */
    private fun DrawScope.lomo(s: SitioTapaMedieval, a: AireMedieval, cuero: Color, e: EstadoTapa, medidor: TextMeasurer, k: Float) {
        val u = density
        val l = s.lomo
        val piel = mezcla(cuero, a.hondo, 0.1f)
        for (y in listOf(l.top - 1.6f * u, l.bottom - 1.4f * u)) {
            drawRoundRect(a.luz(P.LINO, k), Offset(l.left + 3f * u, y), Size(l.width - 5f * u, 3f * u), CornerRadius(1.5f * u))
            drawLine(a.luz(P.RUBRICA, k), Offset(l.left + 5f * u, y + 1.5f * u), Offset(l.right - 4f * u, y + 1.5f * u), 0.8f * u)
        }
        drawRoundRect(piel, l.topLeft, l.size, CornerRadius(4f * u))
        val luz = if (a.oscuro) P.LLAMA.copy(alpha = 0.3f) else mezcla(P.SOL, P.LLAMA, 0.3f).copy(alpha = 0.32f)
        drawRect(Brush.horizontalGradient(listOf(luz, Color.Transparent, a.frio(0.45f)), l.left, l.right), l.topLeft, l.size)
        drawRect(mezcla(cuero, P.BADANA, 0.35f).copy(alpha = 0.35f), Offset(l.left + l.width * 0.22f, l.top + 4f * u), Size(l.width * 0.14f, l.height - 8f * u))
        val oro = mezcla(a.luz(ORO, k), ORO, if (a.oscuro) 0.55f else 0.15f)
        for (f in listOf(0.1f, 0.27f, 0.73f, 0.9f)) {
            val y = l.top + l.height * f
            drawRect(a.frio(0.5f), Offset(l.left + 1f * u, y + 1f * u), Size(l.width - 2.5f * u, 1.6f * u))
            drawRoundRect(mezcla(piel, P.BADANA, 0.08f), Offset(l.left + 0.5f * u, y - 2.4f * u), Size(l.width - 2f * u, 3.6f * u), CornerRadius(1.6f * u))
            drawRect(if (a.oscuro) a.filo(0.5f * k + 0.1f) else a.claro(0.4f), Offset(l.left + 1.5f * u, y - 2.2f * u), Size(l.width - 4f * u, 0.8f * u))
            drawLine(oro.copy(alpha = 0.45f), Offset(l.left + 2.5f * u, y + 3.6f * u), Offset(l.right - 4f * u, y + 3.6f * u), 0.6f * u)
        }
        if (!e.vacio && e.palabras > 0) {
            textoEn(medidor, palabrasTapa(e.palabras), Rect(l.left + 1f * u, l.top + l.height * 0.31f, l.right - 3f * u, l.top + l.height * 0.69f),
                P.LETRA, oro, 10f, vertical = true, peso = FontWeight.Bold, minimo = 6f)
        }
    }

    fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"
}
