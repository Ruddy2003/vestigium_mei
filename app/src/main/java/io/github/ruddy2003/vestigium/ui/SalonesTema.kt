package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * El hall de los temas, cada uno con su escena: aquí, el atrio romano con su impluvio, la stoa pintada griega, la sala de gestas con tapices,
 * la nave gótica con su rosetón, la galería renacentista con su artesonado, la galería imperial y el panteón de la Revolución.
 * Las de los otros catorce temas están en [SalonesMas]. Todo se dibuja tenue para que las piezas se lean encima.
 */
object SalonesTema {
    /**
     * La escena detrás de las piezas. [columnas] = cuántas piezas hay por fila: la arquitectura (columnas, arcos,
     * hornacinas) se acomoda para enmarcar cada columna de piezas y nunca pasar por detrás de una.
     */
    fun DrawScope.escena(tema: TemaId, cs: ColorScheme, columnas: Int = 2) {
        val oscuro = cs.background.luminance() < 0.5f
        val n = columnas.coerceAtLeast(1)
        when (tema) {
            TemaId.ROMANO -> atrio(cs, oscuro, n)
            TemaId.URBE -> facciata(cs, oscuro, n)
            TemaId.HELENO -> stoa(cs, oscuro, n)
            TemaId.EGIPTO -> hipostila(cs, oscuro, n)
            TemaId.MEDIEVAL -> salaGestas(cs, oscuro, n)
            TemaId.TEMPLARIO -> salaArmas(cs, oscuro, n)
            TemaId.GOTICO -> nave(cs, oscuro, n)
            TemaId.ARCANO -> sotano(cs, oscuro, n)
            TemaId.RENACIMIENTO -> galeria(cs, oscuro, n)
            TemaId.PIRATA -> camaraPopa(cs, oscuro, n)
            TemaId.NAPOLEON -> imperial(cs, oscuro, n)
            TemaId.DECO -> salonBaile(cs, oscuro, n)
            TemaId.REVOLUCION -> panteon(cs, oscuro, n)
            TemaId.ANDALUS -> with(SalonesMas) { patio(cs, oscuro, n) }
            TemaId.MESO -> with(SalonesMas) { plazaMaya(cs, oscuro, n) }
            TemaId.SEDA -> with(SalonesMas) { caravasar(cs, oscuro, n) }
            TemaId.EDO -> with(SalonesMas) { posada(cs, oscuro, n) }
            TemaId.MONGOL -> with(SalonesMas) { yurta(cs, oscuro, n) }
            TemaId.COLONOS -> with(SalonesMas) { almacen(cs, oscuro, n) }
            TemaId.BOTANICO -> with(SalonesMas) { invernadero(cs, oscuro, n) }
            TemaId.RUINAS -> with(SalonesMas) { bibliotecaInvadida(cs, oscuro, n) }
            TemaId.NAVIDAD -> with(SalonesMas) { chimenea(cs, oscuro, n) }
            TemaId.ANTIGUAS -> with(SalonesMas) { salonPiedra(cs, oscuro, n) }
            TemaId.CUEVA -> with(SalonesMas) { salonCueva(cs, oscuro, n) }
            TemaId.DISTOPIA -> with(SalonesMas) { archivo(cs, oscuro, n) }
            TemaId.CIBER -> with(SalonesMas) { salaCentral(cs, oscuro, n) }
            TemaId.NEON -> with(SalonesMas) { callejon(cs, oscuro, n) }
            TemaId.REFUGIO -> with(SalonesMas) { salaComun(cs, oscuro, n) }
            TemaId.ORDEN -> with(SalonesMas) { salaCapitular(cs, oscuro, n) }
            TemaId.VAPOR -> with(SalonesMas) { naveExposicion(cs, oscuro, n) }
            TemaId.LABORATORIO -> with(SalonesMas) { laboratorioLimpio(cs, oscuro, n) }
            TemaId.ESPACIO -> with(SalonesMas) { observatorio(cs, oscuro, n) }
            TemaId.ABISMO -> with(SalonesMas) { salaAbisal(cs, oscuro, n) }
            TemaId.URSS -> with(SalonesMas) { estacionMetro(cs, oscuro, n) }
        }
    }

    /** El pedestal de cada pieza en el hall de estos temas. */
    fun DrawScope.pedestal(tema: TemaId, cs: ColorScheme, metal: Color) {
        val w = size.width; val h = size.height
        fun piedra(c: Color) = Brush.verticalGradient(listOf(lerp(c, Color.White, 0.2f), c, lerp(c, Color.Black, 0.25f)), 0f, h)
        when (tema) {
            TemaId.ROMANO -> { drawRect(piedra(Color(0xFF7C2433).copy(alpha = 0.75f)), Offset(w * 0.2f, h * 0.15f), Size(w * 0.6f, h * 0.7f)); drawRect(piedra(Color(0xFFE7E3DA)), Offset(w * 0.14f, 0f), Size(w * 0.72f, h * 0.18f)); drawRect(piedra(Color(0xFFE7E3DA)), Offset(w * 0.1f, h * 0.82f), Size(w * 0.8f, h * 0.18f)) }
            // El pilón de travertino de una fuente de barrio, con la placa de mármol al frente.
            TemaId.URBE -> {
                drawRect(piedra(Color(0xFFE6D8BC)), Offset(w * 0.14f, h * 0.12f), Size(w * 0.72f, h * 0.88f))
                drawRect(piedra(Color(0xFFCDBA92)), Offset(w * 0.08f, 0f), Size(w * 0.84f, h * 0.16f))
                drawRect(Color(0xFFF4EEE2), Offset(w * 0.3f, h * 0.36f), Size(w * 0.4f, h * 0.36f))
                drawRect(Color(0xFF2B2420).copy(alpha = 0.6f), Offset(w * 0.3f, h * 0.36f), Size(w * 0.4f, h * 0.36f), style = Stroke(h * 0.04f))
                drawLine(Color(0xFF2B2420).copy(alpha = 0.6f), Offset(w * 0.38f, h * 0.54f), Offset(w * 0.62f, h * 0.54f), h * 0.05f)
            }
            TemaId.HELENO -> for (k in 0 until 3) drawRect(piedra(Color(0xFFEDE7DA)), Offset(w * (0.24f - k * 0.07f), h * k / 3f), Size(w * (0.52f + k * 0.14f), h / 3f))
            // La mesa de ofrendas sobre su bloque: la losa con el filo de lapislázuli y el signo hotep (el pan sobre la estera) tallado.
            TemaId.EGIPTO -> {
                drawRect(piedra(Color(0xFFC9AE7A)), Offset(w * 0.18f, h * 0.3f), Size(w * 0.64f, h * 0.7f))
                drawRect(piedra(Color(0xFF9A7650)), Offset(w * 0.1f, h * 0.12f), Size(w * 0.8f, h * 0.2f))
                drawRect(Color(0xFF1D2F7A).copy(alpha = 0.6f), Offset(w * 0.1f, h * 0.12f), Size(w * 0.8f, h * 0.04f))
                drawArc(Color(0xFF6E4A2E).copy(alpha = 0.6f), 180f, 180f, true, Offset(w * 0.4f, h * 0.38f), Size(w * 0.2f, h * 0.2f))
                drawLine(Color(0xFF6E4A2E).copy(alpha = 0.6f), Offset(w * 0.32f, h * 0.49f), Offset(w * 0.68f, h * 0.49f), h * 0.03f)
            }
            TemaId.MEDIEVAL -> { drawRect(piedra(Color(0xFF5A3A22)), Offset(w * 0.12f, h * 0.1f), Size(w * 0.76f, h * 0.35f)); drawRect(Color(0xFF5A3A22), Offset(w * 0.45f, h * 0.45f), Size(w * 0.1f, h * 0.55f)) }
            // Un sillar de arenisca con la cruz patada grabada en el frente.
            TemaId.TEMPLARIO -> { drawRect(piedra(Color(0xFFC2AE84)), Offset(w * 0.14f, 0f), Size(w * 0.72f, h)); drawRect(Color(0xFF7A6A4C).copy(alpha = 0.5f), Offset(w * 0.14f, 0f), Size(w * 0.72f, h), style = Stroke(1.2f))
                drawPath(cruzPatada(Offset(w / 2, h * 0.5f), minOf(w, h) * 0.32f), Color(0xFFB0191F).copy(alpha = 0.75f)) }
            TemaId.GOTICO -> { drawRect(piedra(Color(0xFF6B6F7A)), Offset(w * 0.18f, 0f), Size(w * 0.64f, h)); for (k in 1..3) drawLine(Color(0xFF3A3E48).copy(alpha = 0.5f), Offset(w * (0.18f + 0.16f * k), 0f), Offset(w * (0.18f + 0.16f * k), h), 1.2f) }
            // Dos cajas de archivo apiladas, con sus fichas y el cordel.
            TemaId.ARCANO -> {
                drawRect(piedra(Color(0xFF8C7A55)), Offset(w * 0.16f, h * 0.45f), Size(w * 0.68f, h * 0.55f))
                drawRect(piedra(Color(0xFF7A6A48)), Offset(w * 0.22f, 0f), Size(w * 0.56f, h * 0.45f))
                listOf(0.12f to 0.3f, 0.6f to 0.36f).forEach { (y, x) -> drawRect(Color(0xFFE8DDC0), Offset(w * x, h * y), Size(w * 0.28f, h * 0.18f)) }
                drawLine(Color(0xFF8A7A58), Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), 1.4f)
            }
            TemaId.RENACIMIENTO -> { drawRect(piedra(Color(0xFF8C9188)), Offset(w * 0.16f, h * 0.2f), Size(w * 0.68f, h * 0.8f)); drawRect(piedra(Color(0xFFB5B9AE)), Offset(w * 0.1f, 0f), Size(w * 0.8f, h * 0.22f)) }
            // Un barril de ron puesto de pie: duelas de roble combadas y tres aros de hierro.
            TemaId.PIRATA -> {
                val roble = Brush.horizontalGradient(listOf(Color(0xFF4A3220), Color(0xFF8A6440), Color(0xFFA77C52), Color(0xFF8A6440), Color(0xFF4A3220)), w * 0.16f, w * 0.84f)
                val barril = Path().apply {
                    moveTo(w * 0.22f, 0f); lineTo(w * 0.78f, 0f); quadraticTo(w * 0.9f, h * 0.5f, w * 0.78f, h); lineTo(w * 0.22f, h); quadraticTo(w * 0.1f, h * 0.5f, w * 0.22f, 0f); close()
                }
                drawPath(barril, roble)
                for (k in 1..4) { val x = w * (0.22f + 0.112f * k); drawLine(Color.Black.copy(alpha = 0.25f), Offset(x, 0f), Offset(x + (x - w / 2) * 0.12f, h * 0.5f), 1f); drawLine(Color.Black.copy(alpha = 0.25f), Offset(x + (x - w / 2) * 0.12f, h * 0.5f), Offset(x, h), 1f) }
                listOf(0.12f, 0.5f, 0.88f).forEach { y -> val a = if (y == 0.5f) 0.115f else 0.07f; drawRect(Color(0xFF3A3A3A), Offset(w * (0.22f - a), h * y - h * 0.035f), Size(w * (0.56f + 2 * a), h * 0.07f)) }
                drawLine(metal, Offset(w * 0.3f, h * 0.5f), Offset(w * 0.7f, h * 0.5f), 1.2f)
            }
            TemaId.NAPOLEON -> { drawRect(piedra(Color(0xFF2F5D4A)), Offset(w * 0.16f, 0f), Size(w * 0.68f, h)); drawRect(Color(0xFFD4AF37), Offset(w * 0.16f, 0f), Size(w * 0.68f, h * 0.12f)); drawRect(Color(0xFFD4AF37), Offset(w * 0.16f, h * 0.88f), Size(w * 0.68f, h * 0.12f)) }
            // Una columna de laca negra que se estrecha en dos escalones, con su abanico de oro arriba y la base de jade.
            TemaId.DECO -> {
                val oro = Color(0xFFD4AF37)
                drawRect(piedra(Color(0xFF1C1A17)), Offset(w * 0.2f, h * 0.18f), Size(w * 0.6f, h * 0.72f))
                drawRect(piedra(Color(0xFF1C1A17)), Offset(w * 0.28f, h * 0.08f), Size(w * 0.44f, h * 0.1f))
                drawRect(piedra(Color(0xFF1F6B57)), Offset(w * 0.14f, h * 0.88f), Size(w * 0.72f, h * 0.12f))
                for (k in 1..3) drawLine(oro.copy(alpha = 0.6f), Offset(w * (0.2f + 0.15f * k), h * 0.22f), Offset(w * (0.2f + 0.15f * k), h * 0.86f), 1.2f)
                val r = w * 0.2f; val b = Offset(w * 0.5f, h * 0.08f)
                for (i in 0 until 7) if (i % 2 == 0) drawArc(oro, 180f + i * 180f / 7, 180f / 7, true, Offset(b.x - r, b.y - r), Size(2 * r, 2 * r))
                drawRect(oro, Offset(w * 0.2f, h * 0.18f), Size(w * 0.6f, h * 0.02f))
            }
            TemaId.REVOLUCION -> {
                drawRect(piedra(Color(0xFFD8D1C2)), Offset(w * 0.18f, 0f), Size(w * 0.64f, h))
                listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234)).forEachIndexed { i, c -> drawRect(c, Offset(w * (0.18f + i * 0.2133f), 0f), Size(w * 0.2133f, h * 0.22f)) }
            }
            TemaId.ANDALUS -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.MESO -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.SEDA -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.EDO -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.MONGOL -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.COLONOS -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.BOTANICO -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.RUINAS -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.NAVIDAD -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.ANTIGUAS -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.CUEVA -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.DISTOPIA -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.CIBER -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.NEON -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.REFUGIO -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.ORDEN -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.VAPOR -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.LABORATORIO -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.ESPACIO -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.ABISMO -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
            TemaId.URSS -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
        }
    }

    // ------------------------------------------------------------------ utilidades

    private fun DrawScope.columnaClasica(x: Float, y0: Float, y1: Float, ancho: Float, c: Color, capitel: Int) {
        drawRect(c, Offset(x - ancho / 2, y0), Size(ancho, y1 - y0))
        for (k in 1..3) drawLine(lerp(c, Color.Black, 0.25f), Offset(x - ancho / 2 + ancho * k / 4, y0), Offset(x - ancho / 2 + ancho * k / 4, y1), 1f)
        when (capitel) {
            // Jónico: ábaco y volutas.
            1 -> { drawRect(c, Offset(x - ancho * 0.85f, y0 - ancho * 0.25f), Size(ancho * 1.7f, ancho * 0.25f)); listOf(-1f, 1f).forEach { s -> drawCircle(c, ancho * 0.28f, Offset(x + s * ancho * 0.7f, y0 + ancho * 0.05f), style = Stroke(2f)) } }
            // Corintio: hojas.
            2 -> for (k in 0 until 3) drawPath(Path().apply { val cx = x - ancho * 0.5f + k * ancho * 0.5f; moveTo(cx - ancho * 0.3f, y0); quadraticTo(cx, y0 - ancho * 0.7f, cx + ancho * 0.3f, y0); close() }, c)
            // Dórico / toscano.
            else -> drawRect(c, Offset(x - ancho * 0.75f, y0 - ancho * 0.22f), Size(ancho * 1.5f, ancho * 0.22f))
        }
        drawRect(c, Offset(x - ancho * 0.7f, y1), Size(ancho * 1.4f, ancho * 0.2f))
    }

    private fun DrawScope.greca(y: Float, alto: Float, c: Color) {
        val u = alto / 3f; val paso = u * 4; var x = 0f; val p = Path()
        while (x + paso <= size.width + paso) {
            p.moveTo(x, y + alto); p.lineTo(x, y); p.lineTo(x + u * 3, y); p.lineTo(x + u * 3, y + u * 2); p.lineTo(x + u, y + u * 2); p.lineTo(x + u, y + u)
            p.moveTo(x, y + alto); p.lineTo(x + paso, y + alto); x += paso
        }
        drawPath(p, c, style = Stroke(1.6f))
    }

    private fun DrawScope.estrella(c: Offset, R: Float, r: Float, color: Color, puntas: Int = 5) {
        val p = Path()
        for (k in 0 until puntas * 2) { val a = -PI / 2 + k * PI / puntas; val rr = if (k % 2 == 0) R else r; val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat(); if (k == 0) p.moveTo(x, y) else p.lineTo(x, y) }
        p.close(); drawPath(p, color)
    }

    // ------------------------------------------------------------------ las escenas

    /**
     * El atrio: arriba, el compluvio abierto al cielo con sus antefijas de barro y el entablamento con la greca; pilastras de
     * mármol con capitel corintio entre las piezas y, entre ellas, paneles de pintura pompeyana (rojo con marco de ocre, una
     * guirnalda y el zócalo negro); abajo, el piso de mosaico en blanco y negro, el zócalo de pórfido y el impluvio con su agua.
     */
    private fun DrawScope.atrio(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val marmol = (if (oscuro) Color(0xFF2A2F34) else Color(0xFFDCDAD2)).copy(alpha = 0.6f)
        val porfido = Color(0xFF7C2433).copy(alpha = if (oscuro) 0.3f else 0.18f)
        val pompeya = Color(0xFF9E2A22).copy(alpha = if (oscuro) 0.16f else 0.09f)
        val ocre = Color(0xFFC9A04A).copy(alpha = if (oscuro) 0.3f else 0.35f)
        val barro = Color(0xFFB0643A).copy(alpha = if (oscuro) 0.4f else 0.35f)
        val paso = w / n
        val techo = 34 * d; val abajo = h - 26 * d
        // El cielo del compluvio y las antefijas en forma de palmeta sobre el borde del tejado.
        drawRect(Brush.verticalGradient(listOf(Color(0xFF6FA8DC).copy(alpha = if (oscuro) 0.14f else 0.2f), Color.Transparent), 0f, 12 * d), Offset.Zero, Size(w, 12 * d))
        var x = 9 * d
        while (x < w) {
            drawPath(Path().apply { moveTo(x - 3 * d, 11 * d); lineTo(x - 3 * d, 8 * d); quadraticTo(x - 4.5f * d, 3 * d, x, 2 * d); quadraticTo(x + 4.5f * d, 3 * d, x + 3 * d, 8 * d); lineTo(x + 3 * d, 11 * d); close() }, barro)
            x += 18 * d
        }
        // Los paneles de pintura pompeyana entre las pilastras.
        for (i in 0 until n) {
            val x0 = i * paso + 11 * d; val x1 = (i + 1) * paso - 11 * d
            val y0 = techo + 10 * d; val y1 = abajo - 10 * d
            if (x1 - x0 < 24 * d || y1 - y0 < 60 * d) continue
            drawRect(pompeya, Offset(x0, y0), Size(x1 - x0, y1 - y0))
            drawRect(ocre, Offset(x0 + 3 * d, y0 + 3 * d), Size(x1 - x0 - 6 * d, y1 - y0 - 24 * d), style = Stroke(1 * d))
            drawPath(Path().apply { moveTo(x0 + 6 * d, y0 + 8 * d); quadraticTo((x0 + x1) / 2, y0 + 28 * d, x1 - 6 * d, y0 + 8 * d) }, Color(0xFF3F6A3A).copy(alpha = if (oscuro) 0.3f else 0.22f), style = Stroke(2.4f * d))
            listOf(x0 + 6 * d, x1 - 6 * d).forEach { xx -> drawLine(Color(0xFF3F6A3A).copy(alpha = if (oscuro) 0.3f else 0.22f), Offset(xx, y0 + 8 * d), Offset(xx, y0 + 16 * d), 1.2f * d) }
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.22f else 0.07f), Offset(x0, y1 - 18 * d), Size(x1 - x0, 18 * d))
        }
        // El piso de mosaico, el zócalo de pórfido y el impluvio al centro.
        var k = 0; x = 0f
        while (x < w) { if (k % 2 == 0) drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(x, abajo - 6 * d), Size(3 * d, 3 * d)) else drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(x, abajo - 3 * d), Size(3 * d, 3 * d)); x += 3 * d; k++ }
        drawRect(porfido, Offset(0f, abajo), Size(w, 26 * d))
        drawRect(cs.tertiary.copy(alpha = 0.35f), Offset(0f, abajo), Size(w, 1.5f * d))
        val pila = Rect(w * 0.3f, abajo + 7 * d, w * 0.7f, h - 5 * d)
        drawRect(marmol, Offset(pila.left - 2 * d, pila.top - 2 * d), Size(pila.width + 4 * d, pila.height + 4 * d))
        drawRect(Color(0xFF4F86B8).copy(alpha = if (oscuro) 0.35f else 0.3f), pila.topLeft, pila.size)
        for (j in 1..3) drawLine(Color.White.copy(alpha = 0.3f), Offset(pila.left + pila.width * j / 4 - 6 * d, pila.top + pila.height * 0.5f), Offset(pila.left + pila.width * j / 4 + 6 * d, pila.top + pila.height * 0.5f), 1 * d)
        // Las pilastras y el entablamento con la greca.
        for (i in 0..n) columnaClasica(i * paso, techo, abajo - 6 * d, 7 * d, marmol, 2)
        drawRect(marmol, Offset(0f, 12 * d), Size(w, 14 * d))
        greca(15 * d, 8 * d, cs.tertiary.copy(alpha = 0.45f))
    }

    /**
     * Roma de hoy: la fachada de un palacio del centro, de revoque ocre, con sus ventanas de persianas verdes entre las
     * piezas (una entreabierta), el alféizar con macetas de geranios, la cornisa de travertino arriba con las tejas y el
     * cielo de la tarde, y abajo la acera de sampietrini con el «nasone», la fuentecita de hierro de las esquinas.
     */
    private fun DrawScope.facciata(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val ocre = Color(0xFFC98A3A).copy(alpha = if (oscuro) 0.16f else 0.14f)
        val trav = (if (oscuro) Color(0xFF6A5A44) else Color(0xFFE6D8BC)).copy(alpha = 0.7f)
        val persiana = Color(0xFF3D5E45).copy(alpha = if (oscuro) 0.35f else 0.28f)
        val paso = w / n
        val techo = 30 * d; val abajo = h - 24 * d
        // El cielo de la tarde y las tejas.
        drawRect(Brush.verticalGradient(listOf(Color(0xFFE8955A).copy(alpha = if (oscuro) 0.14f else 0.22f), Color.Transparent), 0f, 14 * d), Offset.Zero, Size(w, 14 * d))
        var x = 0f; while (x < w) { drawArc(Color(0xFFA8432A).copy(alpha = 0.35f), 180f, 180f, false, Offset(x, 9 * d), Size(8 * d, 6 * d), style = Stroke(1.4f * d)); x += 8 * d }
        // El muro ocre.
        drawRect(ocre, Offset(0f, techo), Size(w, abajo - techo))
        // Entre cada dos piezas, una ventana con su marco de travertino y las persianas abiertas a los lados.
        for (i in 0..n) {
            val cx = i * paso
            val vw = 14 * d; val y0 = techo + 22 * d; val y1 = min(abajo - 40 * d, y0 + 70 * d)
            if (y1 - y0 < 30 * d) continue
            drawRect(trav, Offset(cx - vw / 2 - 3 * d, y0 - 4 * d), Size(vw + 6 * d, y1 - y0 + 8 * d))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(cx - vw / 2, y0), Size(vw, y1 - y0))
            listOf(-1f, 1f).forEach { s ->
                val px = cx + s * (vw / 2 + 3 * d) + (if (s < 0) -7 * d else 0f)
                drawRect(persiana, Offset(px, y0), Size(7 * d, y1 - y0))
                var y = y0 + 3 * d; while (y < y1) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(px, y), Offset(px + 7 * d, y), 0.8f * d); y += 4 * d }
            }
            // El alféizar con sus geranios.
            drawRect(trav, Offset(cx - vw / 2 - 6 * d, y1 + 4 * d), Size(vw + 12 * d, 3 * d))
            listOf(-4f, 0f, 4f).forEach { dx -> drawCircle(Color(0xFFC8364A).copy(alpha = if (oscuro) 0.4f else 0.5f), 2.2f * d, Offset(cx + dx * d, y1 + 1.5f * d)) }
        }
        // La cornisa de travertino con su moldura.
        drawRect(trav, Offset(0f, 15 * d), Size(w, techo - 15 * d))
        drawLine(Color.Black.copy(alpha = 0.15f), Offset(0f, techo - 3 * d), Offset(w, techo - 3 * d), 1 * d)
        // La acera de sampietrini y el nasone al centro.
        drawRect(Color(0xFF3A3530).copy(alpha = if (oscuro) 0.4f else 0.2f), Offset(0f, abajo), Size(w, h - abajo))
        var k = 0; x = 0f
        while (x < w) { drawRect(Color.Black.copy(alpha = 0.12f), Offset(x, abajo + (if (k % 2 == 0) 4 * d else 12 * d)), Size(6 * d, 6 * d), style = Stroke(0.6f * d)); x += 7 * d; k++ }
        val nx = w / 2; val hierro = Color(0xFF3F4A44).copy(alpha = if (oscuro) 0.6f else 0.5f)
        drawRect(hierro, Offset(nx - 4 * d, abajo - 22 * d), Size(8 * d, 22 * d))
        drawCircle(hierro, 4.5f * d, Offset(nx, abajo - 22 * d))
        drawLine(hierro, Offset(nx + 4 * d, abajo - 14 * d), Offset(nx + 9 * d, abajo - 12 * d), 2 * d)
        drawLine(Color(0xFF6FB3D9).copy(alpha = 0.6f), Offset(nx + 9 * d, abajo - 12 * d), Offset(nx + 10 * d, abajo), 1 * d)
    }

    /**
     * La stoa pintada: el cielo y el mar del Egeo al fondo, entre columnas jónicas; los escudos de bronce colgados en el muro,
     * con la estrella de Macedonia; el entablamento con su cornisa, los dentículos, el friso con la greca y las tres fajas del
     * arquitrabe; abajo, el estilóbato de tres escalones.
     */
    private fun DrawScope.stoa(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        drawRect(Brush.verticalGradient(listOf(cs.secondary.copy(alpha = if (oscuro) 0.18f else 0.14f), Color.Transparent), 0f, h * 0.4f), Offset.Zero, Size(w, h * 0.4f))
        val marmol = (if (oscuro) Color(0xFF3A3550) else Color(0xFFEDE7DA)).copy(alpha = 0.75f)
        val bronce = Color(0xFFB07A3A).copy(alpha = if (oscuro) 0.4f else 0.32f)
        val paso = w / n
        val techo = 34 * d; val piso = h - 10 * d
        // El mar al fondo, con sus islas y sus olas.
        val mar = Color(0xFF4F86B8).copy(alpha = if (oscuro) 0.16f else 0.12f)
        drawRect(mar, Offset(0f, piso - 30 * d), Size(w, 30 * d))
        val arena = Color(0xFFE6D3A6).copy(alpha = if (oscuro) 0.25f else 0.5f)
        listOf(0.18f to 34f, 0.62f to 48f, 0.86f to 26f).forEach { (fx, ancho) -> drawArc(arena, 180f, 180f, true, Offset(w * fx - ancho / 2 * d, piso - 36 * d), Size(ancho * d, 12 * d)) }
        val az = java.util.Random(11)
        repeat(10) { val c = Offset(az.nextFloat() * w, piso - (6 + az.nextFloat() * 20) * d); drawArc(Color.White.copy(alpha = 0.35f), 200f, 140f, false, Offset(c.x - 5 * d, c.y - 3 * d), Size(10 * d, 6 * d), style = Stroke(1f * d)) }
        // Los escudos de bronce, uno entre cada par de columnas.
        for (i in 0 until n) {
            val c = Offset((i + 0.5f) * paso, techo + 22 * d); val r = min(11 * d, paso * 0.14f)
            drawCircle(bronce, r, c)
            drawCircle(bronce, r * 0.78f, c, style = Stroke(1 * d))
            estrella(c, r * 0.62f, r * 0.2f, Color(0xFFD4AF37).copy(alpha = if (oscuro) 0.6f else 0.5f), 8)
            drawLine(bronce, Offset(c.x, c.y - r), Offset(c.x, c.y - r - 6 * d), 1 * d)
        }
        // Las columnas jónicas.
        for (i in 0..n) columnaClasica(i * paso, techo, piso, 7 * d, marmol, 1)
        // El entablamento: cornisa, dentículos, friso con greca y las tres fajas del arquitrabe.
        drawRect(marmol, Offset(0f, 0f), Size(w, 5 * d))
        var x = 1 * d; while (x < w) { drawRect(marmol, Offset(x, 6 * d), Size(2.4f * d, 3 * d)); x += 4 * d }
        drawRect(marmol.copy(alpha = marmol.alpha * 0.5f), Offset(0f, 10 * d), Size(w, 11 * d))
        greca(11.5f * d, 8 * d, cs.primary.copy(alpha = 0.45f))
        listOf(22f, 26f, 30f).forEachIndexed { k, y -> drawRect(marmol, Offset(0f, y * d), Size(w, (3.2f - k * 0.4f) * d)) }
        // El estilóbato.
        for (k in 0 until 3) drawRect(marmol, Offset(0f, h - 10 * d + k * 3.4f * d), Size(w, 3 * d))
    }

    /**
     * La sala hipóstila: el techo azul con estrellas de cinco puntas, el arquitrabe con el friso jeker, columnas
     * papiriformes de capitel abierto entre cada columna de piezas y, al fondo, el muro con sus registros pintados.
     */
    private fun DrawScope.hipostila(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val techo = 30 * d; val piso = h - 10 * d
        val piedra = (if (oscuro) Color(0xFF4A3C2C) else Color(0xFFE3CFA0)).copy(alpha = 0.75f)
        val lapis = Color(0xFF1D2F7A).copy(alpha = if (oscuro) 0.5f else 0.35f)
        val oro = Color(0xFFC9971C).copy(alpha = if (oscuro) 0.6f else 0.55f)
        val cornalina = Color(0xFFB8471E).copy(alpha = if (oscuro) 0.45f else 0.35f)
        drawRect(lapis, Offset.Zero, Size(w, techo - 8 * d))
        val az = java.util.Random(17)
        repeat((w / (14 * d)).toInt().coerceAtLeast(4)) {
            val c = Offset(az.nextFloat() * w, 3 * d + az.nextFloat() * (techo - 14 * d)); val r = 2.2f * d
            val p = Path()
            for (k in 0 until 10) {
                val a = -PI / 2 + PI * k / 5; val rr = if (k % 2 == 0) r else r * 0.4f
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (k == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close(); drawPath(p, oro)
        }
        drawRect(piedra, Offset(0f, techo - 8 * d), Size(w, 8 * d))
        var x = 2 * d; var k = 0
        while (x < w) {
            val col = if (k % 2 == 0) lapis else cornalina
            drawRect(col, Offset(x - 1 * d, techo - 6.5f * d), Size(2 * d, 5 * d)); drawCircle(col, 1.4f * d, Offset(x, techo - 7 * d))
            x += 5 * d; k++
        }
        listOf(0.42f, 0.62f).forEach { f -> drawLine(cornalina, Offset(0f, h * f), Offset(w, h * f), 1 * d) }
        val paso = w / n
        for (i in 0..n) {
            val cx = i * paso; val r = 6 * d
            drawPath(Path().apply {
                moveTo(cx - r, piso); quadraticTo(cx - r * 1.3f, piso - 12 * d, cx - r * 0.8f, techo + 14 * d)
                lineTo(cx + r * 0.8f, techo + 14 * d); quadraticTo(cx + r * 1.3f, piso - 12 * d, cx + r, piso); close()
            }, piedra)
            for (j in 0 until 3) drawLine(oro, Offset(cx - r * 0.85f, techo + (16 + j * 2.5f) * d), Offset(cx + r * 0.85f, techo + (16 + j * 2.5f) * d), 1 * d)
            drawPath(Path().apply {
                moveTo(cx - r * 0.8f, techo + 14 * d); quadraticTo(cx - r * 2.2f, techo + 6 * d, cx - r * 2f, techo)
                lineTo(cx + r * 2f, techo); quadraticTo(cx + r * 2.2f, techo + 6 * d, cx + r * 0.8f, techo + 14 * d); close()
            }, piedra)
            drawLine(lapis, Offset(cx - r * 1.6f, techo + 4 * d), Offset(cx + r * 1.6f, techo + 4 * d), 1 * d)
        }
        drawRect(piedra, Offset(0f, piso), Size(w, h - piso))
        drawLine(cornalina, Offset(0f, piso + 2 * d), Offset(w, piso + 2 * d), 1 * d)
    }

    /** La sala de gestas: sillares, un tapiz colgado entre cada par de piezas y antorchas en los muros de los lados. */
    private fun DrawScope.salaGestas(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val junta = (if (oscuro) Color.Black else Color(0xFF8A7A5C)).copy(alpha = 0.18f)
        val sw = 26 * d; val sh = 10 * d
        var y = 0f; var k = 0
        while (y < h) { val off = if (k % 2 == 0) 0f else sw / 2; var x = -off; while (x < w) { drawRect(junta, Offset(x, y), Size(sw, sh), style = Stroke(1f)); x += sw }; y += sh; k++ }
        val tapices = listOf(Color(0xFFB0281E), Color(0xFF1F3F8F))
        val paso = w / n
        for (i in 1 until n) {
            val cx = i * paso; val an = 11 * d; val yb = h * 0.9f
            val tela = Path().apply { moveTo(cx - an, 6 * d); lineTo(cx + an, 6 * d); lineTo(cx + an, yb); lineTo(cx, yb - 8 * d); lineTo(cx - an, yb); close() }
            drawPath(tela, tapices[i % 2].copy(alpha = 0.32f))
            drawPath(tela, Color(0xFFD4A93C).copy(alpha = 0.55f), style = Stroke(1.2f * d))
            drawLine(Color(0xFF5A3A22).copy(alpha = 0.6f), Offset(cx - an - 3 * d, 6 * d), Offset(cx + an + 3 * d, 6 * d), 2 * d)
            var yy = 40 * d; while (yy < yb - 30 * d) { estrella(Offset(cx, yy), 5 * d, 2 * d, Color(0xFFD4A93C).copy(alpha = 0.55f), 6); yy += 120 * d }
        }
        // Antorchas en los muros de los lados.
        listOf(6 * d, w - 6 * d).forEach { x ->
            var yy = 30 * d
            while (yy < h) {
                drawLine(Color(0xFF5A3A22).copy(alpha = 0.5f), Offset(x, yy + 14 * d), Offset(x, yy), 2 * d)
                drawPath(Path().apply { moveTo(x, yy - 10 * d); quadraticTo(x + 4 * d, yy - 4 * d, x, yy); quadraticTo(x - 4 * d, yy - 4 * d, x, yy - 10 * d); close() }, Color(0xFFFFB347).copy(alpha = 0.6f))
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.16f), Color.Transparent), Offset(x, yy - 5 * d), 40 * d), 40 * d, Offset(x, yy - 5 * d))
                yy += 230 * d
            }
        }
    }

    /**
     * La sala de armas del castillo: muros encalados, la viga del techo con sus canes, un arco de medio punto sobre cada
     * columna de piezas y, en cada pilar, el escudo de la casa colgado sobre dos lanzas cruzadas. Abajo, las losas.
     */
    private fun DrawScope.salaArmas(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val piedra = (if (oscuro) Color(0xFF8A8270) else Color(0xFF9C8B66)).copy(alpha = 0.4f)
        val madera = Color(0xFF4A3524).copy(alpha = if (oscuro) 0.7f else 0.5f)
        drawRect(madera, Offset(0f, 0f), Size(w, 8 * d))
        var x = 10 * d; while (x < w) { drawRect(madera, Offset(x, 8 * d), Size(6 * d, 6 * d)); x += 40 * d }
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 8 * d; val x1 = (i + 1) * paso - 8 * d; val yArr = 44 * d
            drawPath(Path().apply { moveTo(x0, h); lineTo(x0, yArr); arcTo(Rect(x0, yArr - (x1 - x0) / 2, x1, yArr + (x1 - x0) / 2), 180f, 180f, false); lineTo(x1, h) }, piedra, style = Stroke(2 * d))
        }
        for (i in 0..n) drawRect(piedra, Offset(i * paso - 4 * d, 14 * d), Size(8 * d, h - 14 * d))
        for (i in 1 until n) {
            val c = Offset(i * paso, 40 * d); val r = 11 * d
            listOf(-1f, 1f).forEach { s -> drawLine(Color(0xFF5A3A22).copy(alpha = 0.6f), Offset(c.x - s * 16 * d, c.y + 18 * d), Offset(c.x + s * 16 * d, c.y - 20 * d), 1.6f * d) }
            val escudo = Path().apply { moveTo(c.x - r * 0.8f, c.y - r * 0.85f); lineTo(c.x + r * 0.8f, c.y - r * 0.85f); quadraticTo(c.x + r * 0.86f, c.y + r * 0.3f, c.x, c.y + r); quadraticTo(c.x - r * 0.86f, c.y + r * 0.3f, c.x - r * 0.8f, c.y - r * 0.85f); close() }
            drawPath(escudo, Color(0xFFF4EFE2).copy(alpha = if (oscuro) 0.5f else 0.8f)); drawPath(escudo, piedra, style = Stroke(1.2f * d))
            drawPath(cruzPatada(c + Offset(0f, -r * 0.1f), r * 0.55f), Color(0xFFB0191F).copy(alpha = 0.7f))
        }
        var y = h * 0.92f; while (y < h) { drawLine(piedra, Offset(0f, y), Offset(w, y), 1f * d); y += 10 * d }
        var xl = 0f; while (xl < w) { drawLine(piedra, Offset(xl, h * 0.92f), Offset(xl, h), 1f * d); xl += 34 * d }
    }

    /** La nave gótica: un arco apuntado sobre cada columna de piezas, manchas de luz de colores y el rosetón entre los arcos. */
    private fun DrawScope.nave(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val piedra = (if (oscuro) Color(0xFF2E3A7A) else Color(0xFFC9BC9F)).copy(alpha = 0.6f)
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 6 * d; val x1 = (i + 1) * paso - 6 * d; val yArr = 34 * d
            drawPath(Path().apply { moveTo(x0, h); lineTo(x0, yArr); quadraticTo(x0, yArr - 20 * d, (x0 + x1) / 2, 4 * d); quadraticTo(x1, yArr - 20 * d, x1, yArr); lineTo(x1, h) }, piedra, style = Stroke(2 * d))
        }
        for (i in 0..n) drawRect(piedra, Offset(i * paso - 3 * d, 20 * d), Size(6 * d, h - 20 * d))
        // Luz de los vitrales.
        val luces = listOf(Color(0xFFB3122E), Color(0xFF1F3A9E), Color(0xFFE2B34A), Color(0xFF2E7D4A))
        val az = java.util.Random(4)
        repeat(10) { k ->
            val c = Offset(az.nextFloat() * w, h * (0.2f + az.nextFloat() * 0.75f))
            drawCircle(Brush.radialGradient(listOf(luces[k % 4].copy(alpha = if (oscuro) 0.2f else 0.12f), Color.Transparent), c, 34 * d), 34 * d, c)
        }
        // Rosetones entre los arcos.
        for (i in 1 until n) {
            val c = Offset(i * paso, 24 * d); val R = 14 * d
            drawCircle(Color(0xFF1F3A9E).copy(alpha = 0.4f), R, c)
            for (k in 0 until 12) { val a = k * PI / 6; drawCircle(luces[k % 4].copy(alpha = 0.5f), R * 0.22f, Offset(c.x + (R * 0.68f * cos(a)).toFloat(), c.y + (R * 0.68f * sin(a)).toFloat())) }
            drawCircle(Color(0xFFE2B34A).copy(alpha = 0.65f), R * 0.25f, c)
            drawCircle(piedra, R, c, style = Stroke(1.5f * d))
        }
    }

    /**
     * El sótano del archivo: un estante de legajos y cajas detrás de cada columna de piezas, la bombilla que cuelga entre
     * ellos con su cerco de luz, los hilos verdes del pizarrón de recortes arriba y la niebla que entra a ras del suelo.
     */
    private fun DrawScope.sotano(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val madera = (if (oscuro) Color(0xFF2B3D37) else Color(0xFFB3A683)).copy(alpha = 0.55f)
        val lomo = (if (oscuro) Color(0xFF3E5A50) else Color(0xFF8C7A55)).copy(alpha = 0.35f)
        val paso = w / n
        val az = java.util.Random(26)
        for (i in 0 until n) {
            val x0 = i * paso + 8 * d; val x1 = (i + 1) * paso - 8 * d
            drawRect(madera, Offset(x0, 40 * d), Size(3 * d, h - 40 * d)); drawRect(madera, Offset(x1 - 3 * d, 40 * d), Size(3 * d, h - 40 * d))
            var y = 40 * d
            while (y < h) {
                drawRect(madera, Offset(x0, y), Size(x1 - x0, 2.5f * d))
                // Los legajos de pie, de alturas y gruesos distintos; a veces una caja.
                var x = x0 + 4 * d
                while (x < x1 - 8 * d) {
                    val g = (4 + az.nextInt(6)) * d; val a = (18 + az.nextInt(16)) * d
                    if (az.nextInt(5) == 0) { drawRect(lomo, Offset(x, y - 20 * d), Size(18 * d, 20 * d)); x += 20 * d }
                    else { drawRect(lomo.copy(alpha = lomo.alpha * (0.6f + az.nextFloat() * 0.6f)), Offset(x, y - a), Size(g, a)); x += maxOf(g + 1 * d, 1f) }
                }
                y += 64 * d
            }
        }
        // El pizarrón de recortes: hilos verdes entre alfileres, de punta a punta.
        val alfileres = (0..n * 2).map { Offset(it * w / (n * 2) + (az.nextFloat() - 0.5f) * 10 * d, 10 * d + az.nextFloat() * 18 * d) }
        alfileres.zipWithNext().forEachIndexed { k, (a, b) -> drawLine(cs.secondary.copy(alpha = 0.35f), a, if (k % 3 == 0) alfileres[(k + 3).coerceAtMost(alfileres.size - 1)] else b, 1 * d) }
        alfileres.forEach { drawCircle(cs.tertiary.copy(alpha = 0.55f), 2.2f * d, it) }
        // Las bombillas entre los estantes, con su cerco amarillo.
        for (i in 1 until n) {
            val c = Offset(i * paso, 46 * d)
            drawLine(lomo, Offset(c.x, 0f), Offset(c.x, c.y - 4 * d), 1 * d)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFF2E3A0).copy(alpha = if (oscuro) 0.22f else 0.16f), Color.Transparent), c, 60 * d), 60 * d, c)
            drawCircle(Color(0xFFF2E3A0).copy(alpha = 0.7f), 4 * d, c)
        }
        // La niebla que entra por abajo, en bandas.
        for (k in 0 until 3) drawRect(Brush.verticalGradient(listOf(Color.Transparent, (if (oscuro) Color(0xFF9AB0A6) else Color.White).copy(alpha = 0.12f), Color.Transparent), h - (70 - k * 16) * d, h - (20 - k * 6) * d),
            Offset(0f, h - (70 - k * 16) * d), Size(w, (50 - k * 10) * d))
    }

    /** La galería: artesonado arriba, una hornacina que enmarca cada columna de piezas y el pavimento en damero que huye. */
    private fun DrawScope.galeria(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val tinta = (if (oscuro) Color(0xFFEFE0BE) else Color(0xFF3A2614)).copy(alpha = 0.16f)
        val fuga = Offset(w / 2, 26 * d)
        val alto = 24 * d
        for (k in 0..8) { val x = w * k / 8; drawLine(tinta, Offset(x, 0f), Offset(fuga.x + (x - fuga.x) * 0.7f, alto), 1f) }
        listOf(0f, alto * 0.5f, alto).forEach { yy -> drawLine(tinta, Offset(0f, yy), Offset(w, yy), 1f) }
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 10 * d; val x1 = (i + 1) * paso - 10 * d; val r = (x1 - x0) / 2
            drawPath(Path().apply { moveTo(x0, h - 30 * d); lineTo(x0, alto + 8 * d + r); arcTo(Rect(x0, alto + 8 * d, x1, alto + 8 * d + 2 * r), 180f, 180f, false); lineTo(x1, h - 30 * d) }, tinta, style = Stroke(1.4f * d))
            drawArc(cs.primary.copy(alpha = 0.07f), 180f, 180f, true, Offset(x0 + 4 * d, alto + 12 * d), Size(x1 - x0 - 8 * d, 2 * r - 8 * d))
        }
        // Pavimento en damero.
        val y0 = h - 30 * d
        for (k in -10..10) drawLine(tinta, Offset(w / 2 + k * w / 10, h), Offset(w / 2 + k * w / 10 * 0.6f, y0), 1f)
        var yy = y0; var pasoY = 4 * d
        while (yy < h) { drawLine(tinta, Offset(0f, yy), Offset(w, yy), 1f); yy += pasoY; pasoY *= 1.35f }
    }

    /**
     * La cámara del capitán, en la popa: arriba, la galería de ventanas con sus vidrios emplomados por donde se ve el mar y
     * el cielo; tablazón de roble en los costados, un bao curvo (la cuaderna) entre cada columna de piezas con su farol
     * colgado, un cabo de barandal tendido de bao a bao, y abajo la cubierta de tablones que huyen hacia el fondo.
     */
    private fun DrawScope.camaraPopa(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val roble = (if (oscuro) Color(0xFF3A2A1A) else Color(0xFF8A6440)).copy(alpha = if (oscuro) 0.55f else 0.22f)
        val junta = Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f)
        val laton = Color(0xFFC9A04A).copy(alpha = if (oscuro) 0.55f else 0.5f)
        val cabo = Color(0xFFB59A62).copy(alpha = if (oscuro) 0.5f else 0.55f)
        // La tablazón de los costados: tablas verticales con sus juntas de brea.
        var x = 0f
        while (x < w) { drawRect(roble, Offset(x, 0f), Size(13 * d, h)); drawLine(junta, Offset(x, 0f), Offset(x, h), 1f); x += 14 * d }
        // La galería de popa: ventanas de medio punto con el mar al fondo y los vidrios en cuadrícula.
        val alto = 40 * d; val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 12 * d; val x1 = (i + 1) * paso - 12 * d
            if (x1 - x0 < 20 * d) continue
            val marco = Rect(x0, 6 * d, x1, alto)
            val ventana = Path().apply { moveTo(x0, alto); lineTo(x0, 6 * d + (x1 - x0) * 0.25f); quadraticTo((x0 + x1) / 2, 6 * d - (x1 - x0) * 0.1f, x1, 6 * d + (x1 - x0) * 0.25f); lineTo(x1, alto); close() }
            drawPath(ventana, Brush.verticalGradient(listOf(Color(0xFF8FB7D6).copy(alpha = if (oscuro) 0.16f else 0.28f), Color(0xFF2F5D62).copy(alpha = if (oscuro) 0.3f else 0.35f)), marco.top, marco.bottom))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.35f), Offset(x0, alto - 10 * d), Offset(x1, alto - 10 * d), 1f)
            var vx = x0 + (x1 - x0) / 4
            while (vx < x1 - 1f) { drawLine(laton, Offset(vx, 8 * d), Offset(vx, alto), 1f); vx += maxOf((x1 - x0) / 4, 1f) }
            var vy = 14 * d
            while (vy < alto) { drawLine(laton, Offset(x0, vy), Offset(x1, vy), 1f); vy += 9 * d }
            drawPath(ventana, laton, style = Stroke(1.6f * d))
        }
        drawRect(roble.copy(alpha = roble.alpha * 1.8f), Offset(0f, alto), Size(w, 5 * d))
        // Los baos curvos entre las piezas, con su farol y el cabo del barandal de uno a otro.
        val piso = h - 28 * d
        for (i in 0..n) {
            val cx = i * paso
            drawPath(Path().apply { moveTo(cx - 4 * d, alto + 5 * d); quadraticTo(cx - 10 * d, (alto + piso) / 2, cx - 4 * d, piso); lineTo(cx + 4 * d, piso); quadraticTo(cx - 2 * d, (alto + piso) / 2, cx + 4 * d, alto + 5 * d); close() },
                roble.copy(alpha = roble.alpha * 2.2f))
            if (i in 1 until n) {
                val f = Offset(cx, alto + 28 * d)
                drawLine(laton, Offset(cx, alto + 5 * d), f - Offset(0f, 7 * d), 1f * d)
                drawRoundRect(laton, f - Offset(5 * d, 7 * d), Size(10 * d, 14 * d), androidx.compose.ui.geometry.CornerRadius(2 * d), style = Stroke(1.2f * d))
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFC266).copy(alpha = if (oscuro) 0.3f else 0.18f), Color.Transparent), f, 36 * d), 36 * d, f)
                drawCircle(Color(0xFFFFC266).copy(alpha = 0.7f), 2.4f * d, f)
            }
        }
        val yb = piso - 34 * d
        for (i in 0 until n) drawPath(Path().apply { moveTo(i * paso + 4 * d, yb); quadraticTo((i + 0.5f) * paso, yb + 12 * d, (i + 1) * paso - 4 * d, yb) }, cabo, style = Stroke(2 * d))
        // La cubierta: tablones que huyen hacia el centro y sus juntas, más apretadas al fondo.
        drawRect(roble.copy(alpha = roble.alpha * 1.4f), Offset(0f, piso), Size(w, h - piso))
        for (k in -8..8) drawLine(junta, Offset(w / 2 + k * w / 8, h), Offset(w / 2 + k * w / 8 * 0.55f, piso), 1f)
        var yy = piso; var pasoY = 4 * d
        while (yy < h) { drawLine(junta, Offset(0f, yy), Offset(w, yy), 1f); yy += maxOf(pasoY, 1f); pasoY *= 1.4f }
    }

    /** La galería imperial: pilastras de mármol verde entre las piezas, guirnaldas de laurel en oro y el cortinaje carmesí arriba. */
    private fun DrawScope.imperial(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val verde = Color(0xFF2F5D4A).copy(alpha = if (oscuro) 0.45f else 0.3f)
        val oro = Color(0xFFD4AF37).copy(alpha = 0.6f)
        val paso = w / n; val arriba = 26 * d
        for (i in 0..n) { val x = i * paso; drawRect(verde, Offset(x - 4 * d, arriba), Size(8 * d, h - arriba)); drawRect(oro, Offset(x - 6 * d, arriba - 2 * d), Size(12 * d, 2.5f * d)); drawRect(oro, Offset(x - 6 * d, h - 4 * d), Size(12 * d, 2.5f * d)) }
        for (i in 0 until n) {
            val x0 = i * paso + 6 * d; val x1 = (i + 1) * paso - 6 * d
            val y0 = arriba + 4 * d; val caida = 14 * d
            drawPath(Path().apply { moveTo(x0, y0); quadraticTo((x0 + x1) / 2, y0 + 2 * caida, x1, y0) }, oro, style = Stroke(1.2f * d))
            for (k in 1..9) { val t = k / 10f; val x = x0 + (x1 - x0) * t; val y = y0 + 2 * caida * 2 * t * (1 - t)
                drawOval(oro, Offset(x - 2.5f * d, y - 1.2f * d), Size(5 * d, 2.4f * d)) }
            estrella(Offset((x0 + x1) / 2, y0 + caida * 2.2f), 4 * d, 1.6f * d, oro)
        }
        // Cortinaje.
        val carmesi = Color(0xFFA3172E).copy(alpha = if (oscuro) 0.5f else 0.35f)
        val tela = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, 10 * d); var x = w; while (x > 0f) { val x2 = (x - w / (3 * n)).coerceAtLeast(0f); quadraticTo((x + x2) / 2, 20 * d, x2, 10 * d); x = x2 }; close() }
        drawPath(tela, carmesi)
        drawLine(oro, Offset(0f, 10 * d), Offset(w, 10 * d), 1 * d)
    }


    /**
     * El salón de baile de un trasatlántico de los años veinte: el gran sol de rayos del techo, pilastras de laca con
     * estrías de oro que suben en escalones entre las piezas, apliques de abanico y, al pie, el parqué en espiga.
     */
    private fun DrawScope.salonBaile(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val oro = Color(0xFFD4AF37).copy(alpha = if (oscuro) 0.4f else 0.5f)
        val laca = (if (oscuro) Color(0xFF2A2620) else Color(0xFF16130E)).copy(alpha = if (oscuro) 0.6f else 0.22f)
        // El techo: un sol de rayos que nace arriba al centro, con su arco escalonado.
        val sol = Offset(w / 2, 0f)
        for (k in 0 until 19) {
            val a = PI * (k + 0.5) / 19
            drawLine(oro.copy(alpha = oro.alpha * (if (k % 2 == 0) 0.5f else 0.25f)), sol, Offset(sol.x + (w * 0.8f * cos(a)).toFloat(), sol.y + (w * 0.8f * sin(a)).toFloat() * 0.35f), (if (k % 2 == 0) 2f else 1f) * d)
        }
        listOf(0.18f, 0.12f, 0.06f).forEachIndexed { i, f -> drawArc(oro, 0f, 180f, false, Offset(w / 2 - w * f, -w * f * 0.35f), Size(2 * w * f, 2 * w * f * 0.35f), style = Stroke((1.6f - i * 0.4f) * d)) }
        // Las pilastras escalonadas, una entre cada columna de piezas.
        val paso = w / n; val arriba = 34 * d
        for (i in 0..n) {
            val x = i * paso
            drawRect(laca, Offset(x - 7 * d, arriba), Size(14 * d, h - arriba))
            drawRect(laca, Offset(x - 5 * d, arriba - 6 * d), Size(10 * d, 6 * d)); drawRect(laca, Offset(x - 3 * d, arriba - 11 * d), Size(6 * d, 5 * d))
            listOf(-3f, 0f, 3f).forEach { dx -> drawLine(oro, Offset(x + dx * d, arriba + 4 * d), Offset(x + dx * d, h - 10 * d), 0.8f * d) }
            // El aplique de abanico, a media altura.
            val b = Offset(x, arriba + (h - arriba) * 0.32f); val r = 9 * d
            for (k in 0 until 5) if (k % 2 == 0) drawArc(oro, 180f + k * 36f, 36f, true, Offset(b.x - r, b.y - r), Size(2 * r, 2 * r))
            drawCircle(Brush.radialGradient(listOf(Color(0xFFF2D27A).copy(alpha = if (oscuro) 0.22f else 0.12f), Color.Transparent), b, 30 * d), 30 * d, b)
        }
        // El parqué en espiga, al pie.
        val suelo = h - 8 * d; val pa = 10 * d
        var x = 0f; var k = 0
        while (x < w) {
            val y0 = if (k % 2 == 0) suelo else suelo + 4 * d
            drawLine(oro.copy(alpha = oro.alpha * 0.6f), Offset(x, y0), Offset(x + pa, if (k % 2 == 0) suelo + 4 * d else suelo), 1f * d)
            x += maxOf(pa, 1f); k++
        }
        drawLine(oro, Offset(0f, suelo - 2 * d), Offset(w, suelo - 2 * d), 1f * d)
    }
    /** El panteón: la cúpula artesonada arriba, columnas lisas entre las piezas y estandartes tricolores en los muros. */
    private fun DrawScope.panteon(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val piedra = (if (oscuro) Color(0xFF3A3F4A) else Color(0xFFD8D1C2)).copy(alpha = 0.65f)
        // La cúpula: arcos concéntricos con casetones, apoyada en la cornisa.
        val base = min(w * 0.42f, h * 0.4f); val c = Offset(w / 2, base)
        for (k in 0 until 4) {
            val r = base * (0.96f - k * 0.2f)
            drawArc(piedra, 180f, 180f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.5f * d))
            if (k < 3) for (j in 1 until 10) { val a = PI + j * PI / 10; val r2 = base * (0.96f - (k + 1) * 0.2f)
                drawLine(piedra, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()), Offset(c.x + (r2 * cos(a)).toFloat(), c.y + (r2 * sin(a)).toFloat()), 1f) }
        }
        drawRect(piedra, Offset(0f, c.y), Size(w, 4 * d))
        val paso = w / n
        for (i in 0..n) columnaClasica(i * paso, c.y + 10 * d, h - 6 * d, 6 * d, piedra, 0)
        // Estandartes tricolores en los muros de los lados.
        val tri = listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234))
        val an = 3.2f * d
        listOf(8 * d, w - 8 * d - 3 * an).forEach { x0 ->
            var y0 = c.y + 18 * d
            while (y0 < h - 60 * d) {
                tri.forEachIndexed { k, col -> drawRect(col.copy(alpha = if (k == 1) 0.6f else 0.45f), Offset(x0 + k * an, y0), Size(an, 46 * d)) }
                drawLine(Color(0xFFC9A04A).copy(alpha = 0.6f), Offset(x0 - 2 * d, y0), Offset(x0 + 3 * an + 2 * d, y0), 1.5f * d)
                y0 += 220 * d
            }
        }
        // La escarapela en lo alto de la cúpula.
        val e = Offset(c.x, c.y - base * 0.96f + 8 * d)
        drawCircle(tri[2].copy(alpha = 0.6f), 6 * d, e); drawCircle(tri[1].copy(alpha = 0.85f), 4 * d, e); drawCircle(tri[0].copy(alpha = 0.7f), 2.2f * d, e)
    }
}
