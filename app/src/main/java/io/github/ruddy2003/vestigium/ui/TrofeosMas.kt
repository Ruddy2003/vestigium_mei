package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trofeos del lote 2: Al-Ándalus (lacería, azulejos, fuentes, astrolabio, arcos), la Ruta de la Seda (sellos de
 * cinabrio, jade, seda, farolillos, porcelana azul y brújula de cuchara), el Mongol (rollo de órdenes, paiza, tamga,
 * yurta, estandarte, ovoo), los Colonos (hacha, yunque, barril, campana, telar, brújula), el Botánico (láminas, hojas,
 * semillas, frutos, lupas), la Navidad (adornos que cuelgan) y Tierras Antiguas (runas inventadas, gemas, yelmos,
 * mapas, llaves y cristales). Sin figuras humanas ni animales, sin letras reales: los signos son inventados.
 */
object TrofeosMas {
    val TEMAS = setOf(TemaId.ANDALUS, TemaId.SEDA, TemaId.MONGOL, TemaId.COLONOS, TemaId.BOTANICO, TemaId.NAVIDAD, TemaId.ANTIGUAS)

    val NOMBRES: Map<TemaId, Map<String, String>> = mapOf(
        TemaId.ANDALUS to mapOf("Diario" to "Libro de lacería", "Mundo" to "Astrolabio", "Cuerpo" to "Fuente del patio", "Mente" to "Estrella de ocho puntas",
            "Cultura" to "Arco de herradura", "Gente" to "Azulejo", "Constancia" to "Lámpara de bronce", "Curiosos" to "Celosía", "Tuyos" to "Granada"),
        TemaId.SEDA to mapOf("Diario" to "Sello de cinabrio", "Mundo" to "Brújula de cuchara", "Cuerpo" to "Disco de jade", "Mente" to "Porcelana azul",
            "Cultura" to "Carrete de seda", "Gente" to "Farolillo", "Constancia" to "Moneda de cobre", "Curiosos" to "Ábaco", "Tuyos" to "Ventana lunar"),
        TemaId.MONGOL to mapOf("Diario" to "Rollo de órdenes", "Mundo" to "Mapa de la estepa", "Cuerpo" to "Estribo", "Mente" to "Tamga",
            "Cultura" to "Yurta", "Gente" to "Placa de mensajero", "Constancia" to "Estandarte de nueve colas", "Curiosos" to "Ovoo", "Tuyos" to "Rueda del techo"),
        TemaId.COLONOS to mapOf("Diario" to "Diario de a bordo", "Mundo" to "Brújula de bolsillo", "Cuerpo" to "Hacha", "Mente" to "Telar",
            "Cultura" to "Linterna", "Gente" to "Campana del pueblo", "Constancia" to "Yunque", "Curiosos" to "Barril", "Tuyos" to "Rueda de carreta"),
        TemaId.BOTANICO to mapOf("Diario" to "Lámina botánica", "Mundo" to "Caja de herborizar", "Cuerpo" to "Hoja de roble", "Mente" to "Lupa",
            "Cultura" to "Helecho", "Gente" to "Cerezas", "Constancia" to "Semilla que brota", "Curiosos" to "Campana de cristal", "Tuyos" to "Flor prensada"),
        TemaId.NAVIDAD to mapOf("Diario" to "Vela", "Mundo" to "Estrella", "Cuerpo" to "Bastón de caramelo", "Mente" to "Copo de nieve",
            "Cultura" to "Esfera", "Gente" to "Corona", "Constancia" to "Campana", "Curiosos" to "Regalo", "Tuyos" to "Piña"),
        TemaId.ANTIGUAS to mapOf("Diario" to "Libro de runas", "Mundo" to "Mapa de la travesía", "Cuerpo" to "Yelmo", "Mente" to "Cristal",
            "Cultura" to "Arpa", "Gente" to "Estandarte", "Constancia" to "Llave de hierro", "Curiosos" to "Gema", "Tuyos" to "Linterna de mina"),
    )

    private fun TrofeosTema.Pincel.c(c: Color): Color = if (hecho) c else sombra
    private fun TrofeosTema.Pincel.b(c: Color): Brush = SolidColor(if (hecho) c else sombra)
    private fun TrofeosTema.Pincel.o(fx: Float, fy: Float) = Offset(x(fx), y(fy))

    // ================================================================== pedestales

    fun DrawScope.pedestalMas(tema: TemaId, p: TrofeosTema.Pincel, ped: Color) {
        val w = size.width; val h = size.height
        fun piedra(c: Color) = Brush.verticalGradient(listOf(lerp(c, Color.White, 0.18f), c, lerp(c, Color.Black, 0.2f)), h * 0.77f, h * 0.96f)
        when (tema) {
            TemaId.ANDALUS -> {
                // Zócalo de azulejo: rombos verdes y azules entre filetes de yeso.
                val c = if (p.hecho) Color(0xFFEDE3CF) else ped
                drawRect(piedra(c), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.16f))
                if (p.hecho) for (k in 0 until 6) {
                    val cx = w * (0.22f + k * 0.112f); val cy = h * 0.87f; val r = w * 0.045f
                    drawPath(Path().apply { moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy + r); lineTo(cx - r, cy); close() }, if (k % 2 == 0) Color(0xFF1E6B5A) else Color(0xFF1E4F8A))
                }
                drawRect(piedra(c), Offset(w * 0.13f, h * 0.775f), Size(w * 0.74f, h * 0.02f))
            }
            TemaId.SEDA -> {
                // Peana de laca roja con patas curvas.
                val c = if (p.hecho) Color(0xFF8E1B1B) else ped
                drawRoundRect(piedra(c), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.06f), CornerRadius(w * 0.02f))
                listOf(0.22f, 0.78f).forEach { x -> drawPath(Path().apply { moveTo(w * (x - 0.04f), h * 0.85f); quadraticTo(w * x, h * 0.9f, w * (x + if (x < 0.5f) -0.05f else 0.05f), h * 0.955f); lineTo(w * (x + 0.02f), h * 0.955f); quadraticTo(w * (x + 0.02f), h * 0.9f, w * (x + 0.04f), h * 0.85f); close() }, c) }
                if (p.hecho) drawRect(Color(0xFFD4AF37), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.008f))
            }
            TemaId.MONGOL -> {
                // Fieltros apilados, el de arriba con su greca roja.
                val fieltro = if (p.hecho) Color(0xFFEDE6D6) else ped
                for (k in 0 until 3) drawRoundRect(lerp(fieltro, Color.Black, k * 0.06f), Offset(w * (0.2f - k * 0.03f), h * (0.79f + k * 0.055f)), Size(w * (0.6f + k * 0.06f), h * 0.055f), CornerRadius(w * 0.03f))
                if (p.hecho) { var x = w * 0.24f; while (x < w * 0.74f) { drawRect(Color(0xFFB0281E), Offset(x, h * 0.81f), Size(w * 0.03f, h * 0.015f)); x += w * 0.06f } }
            }
            TemaId.COLONOS -> {
                // Un tocón de árbol con sus anillos.
                val c = if (p.hecho) Color(0xFF7A5232) else ped
                drawRect(Brush.horizontalGradient(listOf(lerp(c, Color.Black, 0.3f), c, lerp(c, Color.White, 0.1f), c, lerp(c, Color.Black, 0.3f)), w * 0.2f, w * 0.8f), Offset(w * 0.2f, h * 0.8f), Size(w * 0.6f, h * 0.15f))
                drawOval(if (p.hecho) Color(0xFFC9A57A) else ped, Offset(w * 0.2f, h * 0.775f), Size(w * 0.6f, h * 0.05f))
                if (p.hecho) for (k in 1..3) drawOval(Color(0xFF7A5232).copy(alpha = 0.5f), Offset(w * (0.5f - 0.3f * k / 4), h * (0.8f - 0.025f * k / 4)), Size(w * 0.6f * k / 4, h * 0.05f * k / 4), style = Stroke(w * 0.004f))
            }
            TemaId.BOTANICO -> {
                // Una mesita de hierro forjado con tablero de madera.
                val hierro = if (p.hecho) Color(0xFF2F3A33) else ped
                drawRect(if (p.hecho) Color(0xFF8A6A44) else ped, Offset(w * 0.14f, h * 0.78f), Size(w * 0.72f, h * 0.035f))
                listOf(0.22f, 0.78f).forEach { x -> drawLine(hierro, Offset(w * x, h * 0.815f), Offset(w * x, h * 0.955f), w * 0.02f) }
                drawPath(Path().apply { moveTo(w * 0.22f, h * 0.9f); quadraticTo(w * 0.5f, h * 0.83f, w * 0.78f, h * 0.9f) }, hierro, style = Stroke(w * 0.012f))
                drawCircle(hierro, w * 0.03f, Offset(w * 0.5f, h * 0.87f), style = Stroke(w * 0.01f))
            }
            TemaId.NAVIDAD -> {
                // Un montoncito de nieve con una rama de acebo.
                val nieve = if (p.hecho) Color(0xFFF4F7FB) else ped
                drawPath(Path().apply { moveTo(w * 0.1f, h * 0.955f); cubicTo(w * 0.15f, h * 0.8f, w * 0.4f, h * 0.76f, w * 0.5f, h * 0.79f); cubicTo(w * 0.62f, h * 0.76f, w * 0.88f, h * 0.8f, w * 0.9f, h * 0.955f); close() }, nieve)
                if (p.hecho) {
                    listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(w * 0.5f, h * 0.83f); quadraticTo(w * (0.5f + s * 0.1f), h * 0.78f, w * (0.5f + s * 0.2f), h * 0.84f); quadraticTo(w * (0.5f + s * 0.1f), h * 0.86f, w * 0.5f, h * 0.83f); close() }, Color(0xFF1F6B3A)) }
                    listOf(-0.03f, 0.0f, 0.03f).forEach { dx -> drawCircle(Color(0xFFB3122E), w * 0.022f, Offset(w * (0.5f + dx), h * 0.825f)) }
                }
            }
            TemaId.ANTIGUAS -> {
                // Un sillar con runas inventadas.
                val c = if (p.hecho) Color(0xFF7D7A72) else ped
                drawRect(piedra(c), Offset(w * 0.18f, h * 0.79f), Size(w * 0.64f, h * 0.16f))
                if (p.hecho) runas(Offset(w * 0.26f, h * 0.85f), w * 0.48f, h * 0.05f, Color(0xFF2B2A28).copy(alpha = 0.55f), w * 0.006f, 5)
            }
            else -> {}
        }
    }

    /** Signos inventados: trazos rectos y ángulos que parecen runas sin ser ningún alfabeto. */
    fun DrawScope.runas(o: Offset, ancho: Float, alto: Float, color: Color, grosor: Float, n: Int, semilla: Int = 3) {
        val az = java.util.Random(semilla.toLong())
        val paso = ancho / n
        for (k in 0 until n) {
            val x = o.x + k * paso + paso * 0.2f; val ww = paso * 0.6f
            drawLine(color, Offset(x + ww / 2, o.y - alto / 2), Offset(x + ww / 2, o.y + alto / 2), grosor, StrokeCap.Round)
            when (az.nextInt(4)) {
                0 -> drawLine(color, Offset(x + ww / 2, o.y - alto / 2), Offset(x + ww, o.y - alto / 6), grosor, StrokeCap.Round)
                1 -> { drawLine(color, Offset(x, o.y - alto / 4), Offset(x + ww / 2, o.y), grosor, StrokeCap.Round); drawLine(color, Offset(x + ww / 2, o.y), Offset(x + ww, o.y - alto / 4), grosor, StrokeCap.Round) }
                2 -> drawLine(color, Offset(x, o.y + alto / 6), Offset(x + ww, o.y - alto / 6), grosor, StrokeCap.Round)
                else -> { drawLine(color, Offset(x + ww / 2, o.y - alto / 6), Offset(x + ww, o.y + alto / 6), grosor, StrokeCap.Round); drawLine(color, Offset(x + ww / 2, o.y - alto / 6), Offset(x, o.y + alto / 6), grosor, StrokeCap.Round) }
            }
        }
    }

    /** Una estrella de [puntas] hecha con [capas] cuadrados girados (la lacería). */
    fun DrawScope.estrellaLazo(c: Offset, r: Float, b: Brush, contorno: Color?, grosor: Float) {
        val p = Path().apply {
            fillType = PathFillType.NonZero
            for (k in 0 until 2) {
                val g = k * PI / 4
                for (j in 0 until 4) { val a = g + j * PI / 2 + PI / 4; val x = c.x + (r * cos(a)).toFloat(); val y = c.y + (r * sin(a)).toFloat(); if (j == 0) moveTo(x, y) else lineTo(x, y) }
                close()
            }
        }
        drawPath(p, b)
        if (contorno != null) drawPath(p, contorno, style = Stroke(grosor))
    }

    // ================================================================== figuras

    fun DrawScope.figuraMas(tema: TemaId, cat: String, p: TrofeosTema.Pincel): Boolean {
        when (tema) {
            TemaId.ANDALUS -> andalus(cat, p)
            TemaId.SEDA -> seda(cat, p)
            TemaId.MONGOL -> mongol(cat, p)
            TemaId.COLONOS -> colonos(cat, p)
            TemaId.BOTANICO -> botanico(cat, p)
            TemaId.NAVIDAD -> navidad(cat, p)
            TemaId.ANTIGUAS -> antiguas(cat, p)
            else -> return false
        }
        return true
    }

    // ---------------------------------------------------------------- Al-Ándalus

    private fun DrawScope.andalus(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val azul = p.c(Color(0xFF1E4F8A)); val verde = p.c(Color(0xFF1E6B5A)); val rojo = p.c(Color(0xFF9E2B25)); val yeso = p.c(Color(0xFFF1E8D4))
        when (cat) {
            "Diario" -> {
                // Libro encuadernado en cuero con la estrella de lazo en oro.
                caja(p, 0.24f, 0.1f, 0.76f, 0.74f, p.b(Color(0xFF6B2E1F)))
                drawRect(p.metal(0.24f, 0.76f), Offset(p.x(0.28f), p.y(0.14f)), Size(p.x(0.44f), p.y(0.56f)), style = Stroke(lw))
                estrellaLazo(p.o(0.5f, 0.42f), p.w * 0.16f, p.metal(0.3f, 0.7f), p.linea, lw * 0.5f)
                drawCircle(p.c(Color(0xFF6B2E1F)), p.w * 0.04f, p.o(0.5f, 0.42f))
                listOf(0.32f to 0.18f, 0.68f to 0.18f, 0.32f to 0.66f, 0.68f to 0.66f).forEach { (x, y) -> estrellaLazo(p.o(x, y), p.w * 0.035f, p.metal(x - 0.05f, x + 0.05f), null, 0f) }
            }
            "Mundo" -> {
                // Astrolabio con su «trono» calado y la red de estrellas en punta.
                val c = p.o(0.5f, 0.46f); val R = p.w * 0.3f
                drawPath(Path().apply { moveTo(c.x - R * 0.35f, c.y - R * 0.92f); quadraticTo(c.x, c.y - R * 1.5f, c.x + R * 0.35f, c.y - R * 0.92f); close() }, p.metal(0.38f, 0.62f))
                drawCircle(p.metal(0.48f, 0.52f), p.w * 0.04f, Offset(c.x, c.y - R * 1.38f), style = Stroke(lw))
                drawCircle(p.metal(0.2f, 0.8f), R, c); drawCircle(p.linea, R * 0.88f, c, style = Stroke(lw * 0.5f))
                for (k in 0 until 36) { val a = k * PI / 18; drawLine(p.linea, Offset(c.x + (R * 0.88f * cos(a)).toFloat(), c.y + (R * 0.88f * sin(a)).toFloat()), Offset(c.x + (R * 0.97f * cos(a)).toFloat(), c.y + (R * 0.97f * sin(a)).toFloat()), lw * 0.35f) }
                // La red: un anillo excéntrico y punteros de estrella.
                drawCircle(p.linea, R * 0.55f, Offset(c.x, c.y - R * 0.12f), style = Stroke(lw * 0.6f))
                for (k in 0 until 7) { val a = -PI / 2 + k * 2 * PI / 7; val t = Offset(c.x + (R * 0.78f * cos(a)).toFloat(), c.y + (R * 0.78f * sin(a)).toFloat())
                    drawPath(Path().apply { moveTo(t.x, t.y); quadraticTo(c.x + (R * 0.4f * cos(a + 0.4)).toFloat(), c.y + (R * 0.4f * sin(a + 0.4)).toFloat(), c.x, c.y) }, p.linea, style = Stroke(lw * 0.5f)) }
                drawCircle(p.c(Color(0xFF6B2E1F)), p.w * 0.025f, c)
            }
            "Cuerpo" -> {
                // La fuente del patio: taza de doce lados, el surtidor y el agua que cae.
                val taza = Path().apply { moveTo(p.x(0.16f), p.y(0.46f)); lineTo(p.x(0.84f), p.y(0.46f)); lineTo(p.x(0.74f), p.y(0.58f)); lineTo(p.x(0.26f), p.y(0.58f)); close() }
                drawPath(taza, p.metal(0.16f, 0.84f))
                for (k in 1 until 6) drawLine(p.linea.copy(alpha = 0.4f), Offset(p.x(0.16f + k * 0.113f), p.y(0.46f)), Offset(p.x(0.26f + k * 0.08f), p.y(0.58f)), lw * 0.4f)
                caja(p, 0.44f, 0.58f, 0.56f, 0.74f); caja(p, 0.34f, 0.72f, 0.66f, 0.76f)
                caja(p, 0.47f, 0.3f, 0.53f, 0.46f)
                val agua = p.c(Color(0xFF5FA8D8))
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.22f)); quadraticTo(p.x(0.5f + s * 0.2f), p.y(0.12f), p.x(0.5f + s * 0.3f), p.y(0.44f)) }, agua, style = Stroke(lw * 0.8f, cap = StrokeCap.Round))
                    drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.22f)); quadraticTo(p.x(0.5f + s * 0.1f), p.y(0.16f), p.x(0.5f + s * 0.16f), p.y(0.44f)) }, agua, style = Stroke(lw * 0.6f, cap = StrokeCap.Round))
                }
                drawLine(agua, p.o(0.5f, 0.3f), p.o(0.5f, 0.12f), lw)
                drawOval(agua.copy(alpha = if (p.hecho) 0.5f else 1f), Offset(p.x(0.2f), p.y(0.44f)), Size(p.x(0.6f), p.y(0.04f)))
            }
            "Mente" -> {
                // La estrella de ocho puntas con su lazo interior.
                val c = p.o(0.5f, 0.4f)
                estrellaLazo(c, p.w * 0.36f, p.metal(0.14f, 0.86f), p.linea, lw * 0.6f)
                estrellaLazo(c, p.w * 0.25f, SolidColor(azul), yeso, lw)
                estrellaLazo(c, p.w * 0.12f, p.metal(0.38f, 0.62f), null, 0f)
                drawCircle(verde, p.w * 0.04f, c)
                caja(p, 0.47f, 0.68f, 0.53f, 0.77f)
            }
            "Cultura" -> {
                // Arco de herradura con dovelas rojas y blancas, sobre dos columnas.
                val c = p.o(0.5f, 0.36f); val R = p.w * 0.3f; val r = p.w * 0.19f
                val n = 14
                for (k in 0 until n) {
                    val a0 = 160f + k * (220f / n)
                    drawArc(if (k % 2 == 0) rojo else yeso, a0, 220f / n + 0.6f, false, Offset(c.x - (R + r) / 2, c.y - (R + r) / 2), Size(R + r, R + r), style = Stroke(R - r))
                }
                drawArc(p.metal(0.2f, 0.8f), 160f, 220f, false, Offset(c.x - R, c.y - R), Size(2 * R, 2 * R), style = Stroke(lw * 0.8f))
                listOf(0.26f, 0.74f).forEach { x -> columna(p, x, 0.07f, 0.48f, 0.74f, 0); caja(p, x - 0.05f, 0.46f, x + 0.05f, 0.49f) }
                caja(p, 0.14f, 0.74f, 0.86f, 0.77f)
                // El alfiz: el marco recto alrededor del arco.
                drawRect(p.metal(0.12f, 0.88f), Offset(p.x(0.12f), c.y - R - p.w * 0.04f), Size(p.x(0.76f), R * 2.1f), style = Stroke(lw * 0.8f))
            }
            "Gente" -> {
                // Azulejo de lazo: el fondo azul, la estrella verde y blanca, cuartos de estrella en las esquinas.
                val x0 = 0.16f; val x1 = 0.84f; val y0 = 0.1f; val lado = p.x(x1 - x0)
                drawRect(p.metal(x0, x1), Offset(p.x(x0) - lw, p.y(y0) - lw), Size(lado + 2 * lw, lado + 2 * lw))
                drawRect(azul, Offset(p.x(x0), p.y(y0)), Size(lado, lado))
                val c = Offset(p.x(0.5f), p.y(y0) + lado / 2)
                estrellaLazo(c, lado * 0.36f, SolidColor(yeso), null, 0f)
                estrellaLazo(c, lado * 0.26f, SolidColor(verde), null, 0f)
                drawCircle(p.c(Color(0xFFE2B34A)), lado * 0.07f, c)
                clipPath(Path().apply { addRect(Rect(p.x(x0), p.y(y0), p.x(x1), p.y(y0) + lado)) }) {
                    listOf(Offset(p.x(x0), p.y(y0)), Offset(p.x(x1), p.y(y0)), Offset(p.x(x0), p.y(y0) + lado), Offset(p.x(x1), p.y(y0) + lado)).forEach { e -> estrellaLazo(e, lado * 0.2f, SolidColor(yeso), null, 0f) }
                }
                caja(p, 0.47f, p.y(y0).let { (it + lado) / p.h } + 0.01f, 0.53f, 0.77f)
            }
            "Constancia" -> {
                // Lámpara de bronce calada que cuelga de su cadena, con su luz.
                for (k in 0 until 6) drawOval(p.metal(0.47f, 0.53f), Offset(p.x(0.48f), p.y(0.02f + k * 0.035f)), Size(p.x(0.04f), p.y(0.03f)), style = Stroke(lw * 0.5f))
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.5f), Color.Transparent), p.o(0.5f, 0.48f), p.w * 0.4f), p.w * 0.4f, p.o(0.5f, 0.48f))
                drawPath(Path().apply { moveTo(p.x(0.3f), p.y(0.42f)); quadraticTo(p.x(0.32f), p.y(0.24f), p.x(0.5f), p.y(0.22f)); quadraticTo(p.x(0.68f), p.y(0.24f), p.x(0.7f), p.y(0.42f)); close() }, p.metal(0.3f, 0.7f))
                drawPath(Path().apply { moveTo(p.x(0.24f), p.y(0.42f)); lineTo(p.x(0.76f), p.y(0.42f)); quadraticTo(p.x(0.7f), p.y(0.62f), p.x(0.5f), p.y(0.64f)); quadraticTo(p.x(0.3f), p.y(0.62f), p.x(0.24f), p.y(0.42f)); close() }, p.metal(0.24f, 0.76f))
                if (p.hecho) for (k in 0 until 5) { estrellaLazo(p.o(0.32f + k * 0.09f, 0.5f), p.w * 0.025f, SolidColor(Color(0xFFFFE6A3)), null, 0f) }
                drawPath(Path().apply { moveTo(p.x(0.46f), p.y(0.64f)); lineTo(p.x(0.54f), p.y(0.64f)); lineTo(p.x(0.5f), p.y(0.72f)); close() }, p.metal(0.46f, 0.54f))
            }
            "Curiosos" -> {
                // Celosía: ventana de arco con su enrejado de rombos.
                val ventana = Path().apply { moveTo(p.x(0.2f), p.y(0.76f)); lineTo(p.x(0.2f), p.y(0.36f)); cubicTo(p.x(0.2f), p.y(0.1f), p.x(0.8f), p.y(0.1f), p.x(0.8f), p.y(0.36f)); lineTo(p.x(0.8f), p.y(0.76f)); close() }
                drawPath(ventana, p.b(Color(0xFF3A2414)))
                clipPath(ventana) {
                    val paso = p.w * 0.07f; var k = -20
                    while (k < 30) { val x = p.x(0.2f) + k * paso
                        drawLine(p.c(Color(0xFFD9B566)), Offset(x, p.y(0.08f)), Offset(x + p.h, p.y(0.08f) + p.h), lw * 0.7f)
                        drawLine(p.c(Color(0xFFD9B566)), Offset(x, p.y(0.08f)), Offset(x - p.h, p.y(0.08f) + p.h), lw * 0.7f); k++ }
                }
                drawPath(ventana, p.metal(0.2f, 0.8f), style = Stroke(lw * 1.4f))
            }
            else -> {
                // La granada, con su corona y una hoja.
                val c = p.o(0.5f, 0.46f)
                drawPath(Path().apply { moveTo(c.x, c.y - p.w * 0.26f); lineTo(c.x + p.w * 0.06f, c.y - p.w * 0.36f); lineTo(c.x + p.w * 0.02f, c.y - p.w * 0.28f); lineTo(c.x, c.y - p.w * 0.38f)
                    lineTo(c.x - p.w * 0.02f, c.y - p.w * 0.28f); lineTo(c.x - p.w * 0.06f, c.y - p.w * 0.36f); close() }, p.metal(0.42f, 0.58f))
                drawCircle(Brush.radialGradient(listOf(lerp(rojo, Color.White, 0.3f), rojo, lerp(rojo, Color.Black, 0.3f)), Offset(c.x - p.w * 0.08f, c.y - p.w * 0.08f), p.w * 0.34f), p.w * 0.28f, c)
                drawCircle(p.metal(0.2f, 0.8f), p.w * 0.28f, c, style = Stroke(lw * 0.6f))
                drawPath(Path().apply { moveTo(c.x + p.w * 0.04f, c.y - p.w * 0.28f); quadraticTo(c.x + p.w * 0.3f, c.y - p.w * 0.42f, c.x + p.w * 0.34f, c.y - p.w * 0.28f); quadraticTo(c.x + p.w * 0.2f, c.y - p.w * 0.22f, c.x + p.w * 0.04f, c.y - p.w * 0.28f); close() }, verde)
                caja(p, 0.47f, 0.7f, 0.53f, 0.77f)
            }
        }
    }

    // ---------------------------------------------------------------- Ruta de la Seda

    private fun DrawScope.seda(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val cinabrio = p.c(Color(0xFFC0392B)); val jade = p.c(Color(0xFF4E9A72)); val lapis = p.c(Color(0xFF1F3A8A)); val seda = p.c(Color(0xFFF2E6C9))
        when (cat) {
            "Diario" -> {
                // El sello de piedra y su impresión de cinabrio (signos inventados, en cuadrícula).
                drawRoundRect(p.b(Color(0xFF4E9A72)), Offset(p.x(0.14f), p.y(0.3f)), Size(p.x(0.26f), p.y(0.44f)), CornerRadius(p.w * 0.02f))
                drawCircle(p.metal(0.18f, 0.36f), p.w * 0.1f, p.o(0.27f, 0.3f))
                val q = Rect(p.x(0.46f), p.y(0.26f), p.x(0.86f), p.y(0.26f) + p.x(0.4f))
                drawRect(cinabrio, q.topLeft, q.size)
                drawRect(seda, Offset(q.left + lw, q.top + lw), Size(q.width - 2 * lw, q.height - 2 * lw), style = Stroke(lw * 0.8f))
                if (p.hecho) {
                    val u = q.width / 6
                    for (i in 0 until 2) for (j in 0 until 2) {
                        val o = Offset(q.left + u * (0.8f + i * 2.4f), q.top + u * (0.8f + j * 2.4f))
                        drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + u * 1.6f, o.y); lineTo(o.x + u * 1.6f, o.y + u * 0.8f); lineTo(o.x + u * 0.5f, o.y + u * 0.8f); lineTo(o.x + u * 0.5f, o.y + u * 1.6f); lineTo(o.x + u * 1.6f, o.y + u * 1.6f) }, Color(0xFFF2E6C9), style = Stroke(lw * 0.9f))
                    }
                }
            }
            "Mundo" -> {
                // Brújula de cuchara: la placa cuadrada con sus anillos y la cuchara de imán que apunta al sur.
                caja(p, 0.12f, 0.2f, 0.88f, 0.74f)
                val c = p.o(0.5f, 0.47f)
                drawCircle(p.c(Color(0xFF2B2B33)).copy(alpha = if (p.hecho) 0.25f else 1f), p.w * 0.25f, c)
                listOf(0.25f, 0.18f).forEach { r -> drawCircle(p.linea, p.w * r, c, style = Stroke(lw * 0.5f)) }
                for (k in 0 until 24) { val a = k * PI / 12; drawLine(p.linea, Offset(c.x + (p.w * 0.25f * cos(a)).toFloat(), c.y + (p.w * 0.25f * sin(a)).toFloat()), Offset(c.x + (p.w * 0.3f * cos(a)).toFloat(), c.y + (p.w * 0.3f * sin(a)).toFloat()), lw * 0.4f) }
                drawOval(p.b(Color(0xFF2B2B33)), Offset(c.x - p.w * 0.1f, c.y - p.w * 0.08f), Size(p.w * 0.2f, p.w * 0.16f))
                drawLine(p.c(Color(0xFF2B2B33)), Offset(c.x, c.y + p.w * 0.06f), Offset(c.x, c.y + p.w * 0.24f), lw * 1.4f, StrokeCap.Round)
            }
            "Cuerpo" -> {
                // Disco de jade (bi), con su borla roja.
                val c = p.o(0.5f, 0.38f)
                drawCircle(Brush.radialGradient(listOf(lerp(jade, Color.White, 0.3f), jade, lerp(jade, Color.Black, 0.25f)), c, p.w * 0.32f), p.w * 0.3f, c)
                drawCircle(Color.Transparent, p.w * 0.1f, c)
                drawCircle(p.c(Color(0xFF1E3A2E)).copy(alpha = 0.6f), p.w * 0.1f, c)
                if (p.hecho) for (k in 0 until 12) { val a = k * PI / 6; drawCircle(Color.White.copy(alpha = 0.3f), p.w * 0.02f, Offset(c.x + (p.w * 0.2f * cos(a)).toFloat(), c.y + (p.w * 0.2f * sin(a)).toFloat())) }
                drawLine(cinabrio, Offset(c.x, c.y + p.w * 0.3f), Offset(c.x, p.y(0.66f)), lw)
                drawPath(Path().apply { moveTo(c.x - p.w * 0.04f, p.y(0.66f)); lineTo(c.x + p.w * 0.04f, p.y(0.66f)); lineTo(c.x + p.w * 0.06f, p.y(0.76f)); lineTo(c.x - p.w * 0.06f, p.y(0.76f)); close() }, cinabrio)
            }
            "Mente" -> {
                // El jarrón de porcelana azul y blanca, con olas y nubes.
                val v = Path().apply {
                    moveTo(p.x(0.42f), p.y(0.06f)); lineTo(p.x(0.58f), p.y(0.06f)); lineTo(p.x(0.57f), p.y(0.12f))
                    cubicTo(p.x(0.82f), p.y(0.16f), p.x(0.8f), p.y(0.4f), p.x(0.66f), p.y(0.6f)); lineTo(p.x(0.62f), p.y(0.74f)); lineTo(p.x(0.38f), p.y(0.74f)); lineTo(p.x(0.34f), p.y(0.6f))
                    cubicTo(p.x(0.2f), p.y(0.4f), p.x(0.18f), p.y(0.16f), p.x(0.43f), p.y(0.12f)); close()
                }
                drawPath(v, p.b(Color(0xFFF4F1EA)))
                if (p.hecho) clipPath(v) {
                    drawRect(lapis, Offset(p.x(0.2f), p.y(0.06f)), Size(p.x(0.6f), p.y(0.06f)))
                    for (k in 0 until 6) drawArc(lapis, 180f, 180f, false, Offset(p.x(0.2f + k * 0.1f), p.y(0.6f)), Size(p.x(0.1f), p.y(0.06f)), style = Stroke(lw))
                    drawLine(lapis, p.o(0.2f, 0.56f), p.o(0.8f, 0.56f), lw * 0.6f); drawLine(lapis, p.o(0.2f, 0.2f), p.o(0.8f, 0.2f), lw * 0.6f)
                    listOf(0.36f to 0.32f, 0.6f to 0.42f).forEach { (x, y) -> for (j in 0 until 3) drawCircle(lapis, p.w * (0.05f - j * 0.012f), p.o(x + j * 0.04f, y), style = Stroke(lw * 0.7f)) }
                }
                drawPath(v, p.linea, style = Stroke(lw * 0.5f))
            }
            "Cultura" -> {
                // Un carrete de seda: discos de madera, hilos de colores y la hebra suelta.
                val madera = p.c(Color(0xFF7A5232))
                drawOval(madera, Offset(p.x(0.2f), p.y(0.1f)), Size(p.x(0.6f), p.y(0.08f)))
                val hilos = listOf(Color(0xFFC0392B), Color(0xFFE2B34A), Color(0xFF1F3A8A), Color(0xFF4E9A72), Color(0xFFB0447A))
                for (k in 0 until 10) drawRect(p.c(hilos[k % hilos.size]), Offset(p.x(0.27f), p.y(0.15f + k * 0.05f)), Size(p.x(0.46f), p.y(0.05f)))
                if (p.hecho) for (k in 0 until 40) drawLine(Color.White.copy(alpha = 0.15f), Offset(p.x(0.27f), p.y(0.15f + k * 0.0125f)), Offset(p.x(0.73f), p.y(0.152f + k * 0.0125f)), lw * 0.2f)
                drawOval(madera, Offset(p.x(0.2f), p.y(0.64f)), Size(p.x(0.6f), p.y(0.08f)))
                drawPath(Path().apply { moveTo(p.x(0.73f), p.y(0.4f)); cubicTo(p.x(0.95f), p.y(0.45f), p.x(0.8f), p.y(0.7f), p.x(0.96f), p.y(0.76f)) }, p.c(Color(0xFFC0392B)), style = Stroke(lw * 0.6f))
            }
            "Gente" -> {
                // El farolillo rojo con sus costillas, tapas de oro y la borla.
                drawLine(p.c(Color(0xFF3A2414)), p.o(0.5f, 0.02f), p.o(0.5f, 0.12f), lw)
                caja(p, 0.38f, 0.12f, 0.62f, 0.17f)
                if (p.hecho) drawOval(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.4f), Color.Transparent), p.o(0.5f, 0.4f), p.w * 0.45f), Offset(p.x(0.05f), p.y(0.1f)), Size(p.x(0.9f), p.y(0.6f)))
                drawOval(Brush.horizontalGradient(listOf(lerp(cinabrio, Color.Black, 0.3f), cinabrio, lerp(cinabrio, Color.White, 0.25f), cinabrio, lerp(cinabrio, Color.Black, 0.3f)), p.x(0.16f), p.x(0.84f)),
                    Offset(p.x(0.16f), p.y(0.16f)), Size(p.x(0.68f), p.y(0.46f)))
                for (k in 1..4) drawArc(p.c(Color(0xFF7A1A12)), -90f, 180f, false, Offset(p.x(0.5f - k * 0.08f), p.y(0.16f)), Size(p.x(k * 0.16f), p.y(0.46f)), style = Stroke(lw * 0.4f))
                caja(p, 0.38f, 0.6f, 0.62f, 0.65f)
                for (k in 0 until 5) drawLine(p.c(Color(0xFFE2B34A)), p.o(0.46f + k * 0.02f, 0.65f), p.o(0.45f + k * 0.025f, 0.77f), lw * 0.5f)
            }
            "Constancia" -> {
                // La moneda de cobre con agujero cuadrado y cuatro signos inventados.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.32f
                drawCircle(p.metal(0.18f, 0.82f), R, c); drawCircle(p.linea, R * 0.9f, c, style = Stroke(lw * 0.6f))
                drawRect(p.c(Color(0xFF1E1A16)), Offset(c.x - R * 0.24f, c.y - R * 0.24f), Size(R * 0.48f, R * 0.48f))
                drawRect(p.linea, Offset(c.x - R * 0.32f, c.y - R * 0.32f), Size(R * 0.64f, R * 0.64f), style = Stroke(lw * 0.6f))
                if (p.hecho) listOf(0.0, PI / 2, PI, 3 * PI / 2).forEachIndexed { i, a ->
                    val o = Offset(c.x + (R * 0.62f * cos(a - PI / 2)).toFloat(), c.y + (R * 0.62f * sin(a - PI / 2)).toFloat())
                    runas(Offset(o.x - R * 0.12f, o.y), R * 0.24f, R * 0.22f, p.linea, lw * 0.6f, 1, i + 7)
                }
                caja(p, 0.47f, 0.4f + 0.32f * p.w / p.h, 0.53f, 0.77f)
            }
            "Curiosos" -> {
                // El ábaco: siete varillas, dos cuentas arriba y cinco abajo.
                caja(p, 0.1f, 0.12f, 0.9f, 0.16f, p.b(Color(0xFF5A3A22))); caja(p, 0.1f, 0.66f, 0.9f, 0.7f, p.b(Color(0xFF5A3A22)))
                caja(p, 0.1f, 0.12f, 0.14f, 0.7f, p.b(Color(0xFF5A3A22))); caja(p, 0.86f, 0.12f, 0.9f, 0.7f, p.b(Color(0xFF5A3A22)))
                caja(p, 0.14f, 0.3f, 0.86f, 0.325f, p.b(Color(0xFF5A3A22)))
                for (k in 0 until 7) {
                    val x = 0.2f + k * 0.1f
                    drawLine(p.linea, p.o(x, 0.16f), p.o(x, 0.66f), lw * 0.4f)
                    for (j in 0 until 2) drawOval(p.metal(x - 0.04f, x + 0.04f), Offset(p.x(x - 0.04f), p.y(0.17f + j * 0.055f)), Size(p.x(0.08f), p.y(0.045f)))
                    for (j in 0 until 5) drawOval(p.metal(x - 0.04f, x + 0.04f), Offset(p.x(x - 0.04f), p.y(0.38f + j * 0.055f) - (if (j < k % 3) p.y(0.03f) else 0f)), Size(p.x(0.08f), p.y(0.045f)))
                }
                caja(p, 0.3f, 0.7f, 0.34f, 0.77f); caja(p, 0.66f, 0.7f, 0.7f, 0.77f)
            }
            else -> {
                // La ventana lunar: un muro con un vano redondo y una rama florida al otro lado.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.25f
                val muro = Path().apply { fillType = PathFillType.EvenOdd; addRect(Rect(p.x(0.1f), p.y(0.1f), p.x(0.9f), p.y(0.74f))); addOval(Rect(c, R)) }
                drawPath(muro, p.b(Color(0xFFE9E1CF)))
                if (p.hecho) {
                    drawRect(Color(0xFF3A3A3A), Offset(p.x(0.08f), p.y(0.07f)), Size(p.x(0.84f), p.y(0.04f)))
                    clipPath(Path().apply { addOval(Rect(c, R)) }) {
                        drawRect(Color(0xFFBFD8E8), Offset(c.x - R, c.y - R), Size(2 * R, 2 * R))
                        drawPath(Path().apply { moveTo(c.x - R, c.y + R * 0.4f); quadraticTo(c.x, c.y - R * 0.2f, c.x + R, c.y + R * 0.5f); lineTo(c.x + R, c.y + R); lineTo(c.x - R, c.y + R); close() }, Color(0xFF7FA38A))
                        drawPath(Path().apply { moveTo(c.x - R, c.y - R * 0.5f); quadraticTo(c.x - R * 0.2f, c.y - R * 0.4f, c.x + R * 0.4f, c.y - R * 0.8f) }, Color(0xFF3A2414), style = Stroke(lw * 0.8f))
                        listOf(-0.6f to -0.48f, -0.2f to -0.46f, 0.15f to -0.62f, 0.35f to -0.76f).forEach { (dx, dy) -> drawCircle(Color(0xFFE8A0B4), p.w * 0.025f, Offset(c.x + R * dx, c.y + R * dy)) }
                    }
                }
                drawCircle(p.metal(0.25f, 0.75f), R, c, style = Stroke(lw * 1.2f))
            }
        }
    }

    // ---------------------------------------------------------------- Mongol

    private fun DrawScope.mongol(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val cielo = p.c(Color(0xFF2E6DB4)); val rojo = p.c(Color(0xFFB0281E)); val fieltro = p.c(Color(0xFFEDE6D6)); val madera = p.c(Color(0xFF8A5A2B))
        when (cat) {
            "Diario" -> {
                // Rollo de órdenes del yam, con escritura vertical inventada y sus varillas.
                drawRect(p.b(Color(0xFFF1E6C8)), Offset(p.x(0.2f), p.y(0.12f)), Size(p.x(0.6f), p.y(0.58f)))
                if (p.hecho) for (k in 0 until 6) {
                    val x = p.x(0.28f + k * 0.09f); val path = Path().apply { moveTo(x, p.y(0.18f)); var y = p.y(0.18f); var s = 1f; while (y < p.y(0.62f)) { quadraticTo(x + s * p.w * 0.02f, y + p.h * 0.02f, x, y + p.h * 0.04f); y += p.h * 0.04f; s = -s } }
                    drawPath(path, Color(0xFF2A1E0E).copy(alpha = 0.7f), style = Stroke(lw * 0.6f))
                }
                listOf(0.12f, 0.7f).forEach { y -> drawRoundRect(madera, Offset(p.x(0.14f), p.y(y) - p.h * 0.02f), Size(p.x(0.72f), p.h * 0.04f), CornerRadius(p.h * 0.02f)) }
                drawCircle(rojo, p.w * 0.06f, p.o(0.7f, 0.6f))
            }
            "Mundo" -> {
                // Mapa de la estepa: montes, el río, la ruta punteada y las yurtas de las postas.
                drawPath(Path().apply { moveTo(p.x(0.08f), p.y(0.18f)); lineTo(p.x(0.92f), p.y(0.14f)); lineTo(p.x(0.9f), p.y(0.7f)); lineTo(p.x(0.1f), p.y(0.72f)); close() }, p.b(Color(0xFFE9DCC0)))
                if (p.hecho) {
                    for (k in 0 until 4) drawPath(Path().apply { val x = p.x(0.2f + k * 0.08f); moveTo(x - p.w * 0.05f, p.y(0.34f)); lineTo(x, p.y(0.24f)); lineTo(x + p.w * 0.05f, p.y(0.34f)) }, Color(0xFF6B5A44), style = Stroke(lw * 0.7f))
                    drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.6f)); cubicTo(p.x(0.4f), p.y(0.5f), p.x(0.5f), p.y(0.68f), p.x(0.9f), p.y(0.56f)) }, Color(0xFF5FA8D8), style = Stroke(lw))
                    drawPath(Path().apply { moveTo(p.x(0.18f), p.y(0.48f)); cubicTo(p.x(0.4f), p.y(0.36f), p.x(0.6f), p.y(0.5f), p.x(0.82f), p.y(0.3f)) }, Color(0xFFB0281E), style = Stroke(lw * 0.8f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(lw * 2, lw * 1.5f))))
                    listOf(0.18f to 0.48f, 0.5f to 0.43f, 0.82f to 0.3f).forEach { (x, y) -> drawArc(Color(0xFFF7F2E4), 180f, 180f, true, Offset(p.x(x) - p.w * 0.04f, p.y(y) - p.w * 0.04f), Size(p.w * 0.08f, p.w * 0.06f)); drawRect(Color(0xFFF7F2E4), Offset(p.x(x) - p.w * 0.04f, p.y(y) - p.w * 0.01f), Size(p.w * 0.08f, p.w * 0.03f)) }
                }
                drawPath(Path().apply { moveTo(p.x(0.08f), p.y(0.18f)); lineTo(p.x(0.92f), p.y(0.14f)); lineTo(p.x(0.9f), p.y(0.7f)); lineTo(p.x(0.1f), p.y(0.72f)); close() }, p.metal(0.08f, 0.92f), style = Stroke(lw * 0.8f))
            }
            "Cuerpo" -> {
                // El estribo de hierro con su correa.
                drawRect(p.b(Color(0xFF6B4A2A)), Offset(p.x(0.45f), p.y(0.04f)), Size(p.x(0.1f), p.y(0.18f)))
                drawPath(Path().apply { moveTo(p.x(0.28f), p.y(0.66f)); cubicTo(p.x(0.2f), p.y(0.36f), p.x(0.36f), p.y(0.2f), p.x(0.5f), p.y(0.2f)); cubicTo(p.x(0.64f), p.y(0.2f), p.x(0.8f), p.y(0.36f), p.x(0.72f), p.y(0.66f)) }, p.metal(0.2f, 0.8f), style = Stroke(lw * 2.4f, cap = StrokeCap.Round))
                caja(p, 0.24f, 0.64f, 0.76f, 0.7f)
                drawRect(p.metal(0.42f, 0.58f), Offset(p.x(0.42f), p.y(0.18f)), Size(p.x(0.16f), p.y(0.04f)))
                if (p.hecho) for (k in 0 until 5) drawCircle(p.linea, p.w * 0.01f, p.o(0.3f + k * 0.1f, 0.67f))
            }
            "Mente" -> {
                // Una tamga (marca de clan) inventada sobre un parche de fieltro azul.
                val c = p.o(0.5f, 0.38f)
                drawCircle(cielo, p.w * 0.32f, c); drawCircle(p.metal(0.18f, 0.82f), p.w * 0.32f, c, style = Stroke(lw * 1.2f))
                val m = p.metal(0.3f, 0.7f)
                drawLine(m, Offset(c.x, c.y - p.w * 0.2f), Offset(c.x, c.y + p.w * 0.2f), lw * 1.6f, StrokeCap.Round)
                drawArc(m, 200f, 140f, false, Offset(c.x - p.w * 0.16f, c.y - p.w * 0.2f), Size(p.w * 0.32f, p.w * 0.2f), style = Stroke(lw * 1.6f, cap = StrokeCap.Round))
                drawLine(m, Offset(c.x - p.w * 0.12f, c.y + p.w * 0.1f), Offset(c.x + p.w * 0.12f, c.y + p.w * 0.1f), lw * 1.6f, StrokeCap.Round)
                drawCircle(m, p.w * 0.035f, Offset(c.x, c.y + p.w * 0.2f))
                caja(p, 0.47f, 0.38f + 0.32f * p.w / p.h, 0.53f, 0.77f)
            }
            "Cultura" -> {
                // La yurta: el techo de fieltro, la pared, la puerta naranja y su banda.
                drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.42f)); quadraticTo(p.x(0.5f), p.y(0.06f), p.x(0.9f), p.y(0.42f)); close() }, p.b(Color(0xFFEDE6D6)))
                drawRect(p.b(Color(0xFFE2D9C4)), Offset(p.x(0.12f), p.y(0.42f)), Size(p.x(0.76f), p.y(0.32f)))
                drawRect(cielo, Offset(p.x(0.12f), p.y(0.42f)), Size(p.x(0.76f), p.y(0.04f)))
                drawRect(p.b(Color(0xFFD9772A)), Offset(p.x(0.42f), p.y(0.52f)), Size(p.x(0.16f), p.y(0.22f)))
                if (p.hecho) { drawRect(Color(0xFF7A3A12), Offset(p.x(0.42f), p.y(0.52f)), Size(p.x(0.16f), p.y(0.22f)), style = Stroke(lw * 0.5f)); for (k in 0 until 5) drawLine(Color(0xFF8A7A5C).copy(alpha = 0.5f), p.o(0.5f, 0.1f), p.o(0.14f + k * 0.18f, 0.42f), lw * 0.4f) }
                drawCircle(p.metal(0.44f, 0.56f), p.w * 0.05f, p.o(0.5f, 0.12f))
                drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.42f)); quadraticTo(p.x(0.5f), p.y(0.06f), p.x(0.9f), p.y(0.42f)) }, p.metal(0.1f, 0.9f), style = Stroke(lw * 0.8f))
            }
            "Gente" -> {
                // La paiza: tablilla de oro con agujero y cordón, con escritura vertical inventada.
                drawLine(rojo, p.o(0.5f, 0.02f), p.o(0.5f, 0.12f), lw * 1.2f)
                drawRoundRect(p.metal(0.3f, 0.7f), Offset(p.x(0.3f), p.y(0.1f)), Size(p.x(0.4f), p.y(0.64f)), CornerRadius(p.w * 0.2f))
                drawCircle(p.c(Color(0xFF1E1A16)), p.w * 0.04f, p.o(0.5f, 0.17f))
                if (p.hecho) for (k in 0 until 3) {
                    val x = p.x(0.4f + k * 0.1f); val path = Path().apply { moveTo(x, p.y(0.26f)); var y = p.y(0.26f); var s = 1f; while (y < p.y(0.66f)) { quadraticTo(x + s * p.w * 0.025f, y + p.h * 0.025f, x, y + p.h * 0.05f); y += p.h * 0.05f; s = -s } }
                    drawPath(path, p.linea, style = Stroke(lw * 0.7f))
                }
            }
            "Constancia" -> {
                // El estandarte de nueve colas: el tridente arriba y las colas que caen.
                caja(p, 0.485f, 0.06f, 0.515f, 0.76f, p.b(Color(0xFF6B4A2A)))
                drawPath(Path().apply { moveTo(p.x(0.4f), p.y(0.14f)); lineTo(p.x(0.4f), p.y(0.06f)); moveTo(p.x(0.5f), p.y(0.14f)); lineTo(p.x(0.5f), p.y(0.0f)); moveTo(p.x(0.6f), p.y(0.14f)); lineTo(p.x(0.6f), p.y(0.06f)); moveTo(p.x(0.4f), p.y(0.14f)); lineTo(p.x(0.6f), p.y(0.14f)) }, p.metal(0.38f, 0.62f), style = Stroke(lw * 1.4f))
                caja(p, 0.36f, 0.15f, 0.64f, 0.19f)
                for (k in 0 until 9) {
                    val a = -0.6 + k * 0.15
                    drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.19f)); quadraticTo(p.x(0.5f + (0.12 * sin(a)).toFloat()), p.y(0.32f), p.x(0.5f + (0.26 * sin(a)).toFloat()), p.y(0.5f)) },
                        if (k % 3 == 1) fieltro else p.c(Color(0xFF2A2420)), style = Stroke(lw * 1.1f, cap = StrokeCap.Round))
                }
            }
            "Curiosos" -> {
                // El ovoo: piedras apiladas, una vara y la bufanda azul al viento.
                listOf(Triple(0.22f, 0.66f, 0.56f), Triple(0.28f, 0.56f, 0.44f), Triple(0.34f, 0.46f, 0.32f), Triple(0.4f, 0.37f, 0.2f)).forEachIndexed { i, (x0, y, ww) ->
                    drawOval(p.metal(x0, x0 + ww).let { if (p.hecho) SolidColor(lerp(Color(0xFF8C8478), Color.Black, i * 0.06f)) else it }, Offset(p.x(x0), p.y(y)), Size(p.x(ww), p.y(0.1f)))
                }
                drawLine(p.c(Color(0xFF6B4A2A)), p.o(0.5f, 0.38f), p.o(0.5f, 0.06f), lw * 1.2f)
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.1f)); cubicTo(p.x(0.64f), p.y(0.06f), p.x(0.7f), p.y(0.2f), p.x(0.86f), p.y(0.14f)); lineTo(p.x(0.84f), p.y(0.2f)); cubicTo(p.x(0.7f), p.y(0.26f), p.x(0.62f), p.y(0.14f), p.x(0.5f), p.y(0.17f)); close() }, cielo)
                caja(p, 0.18f, 0.74f, 0.82f, 0.77f)
            }
            else -> {
                // La rueda del techo (toono): aro, rayos y travesaños, pintada de naranja.
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.3f
                drawCircle(p.c(Color(0xFFD9772A)), R, c, style = Stroke(lw * 2.4f))
                drawCircle(p.metal(0.2f, 0.8f), R * 1.08f, c, style = Stroke(lw * 0.6f))
                for (k in 0 until 16) { val a = k * PI / 8; drawLine(p.c(Color(0xFFD9772A)), Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat()), Offset(c.x + (R * 1.4f * cos(a)).toFloat(), c.y + (R * 1.4f * sin(a)).toFloat()), lw * 0.7f) }
                drawLine(rojo, Offset(c.x - R, c.y), Offset(c.x + R, c.y), lw * 1.4f); drawLine(rojo, Offset(c.x, c.y - R), Offset(c.x, c.y + R), lw * 1.4f)
                listOf(-0.5f, 0.5f).forEach { f -> drawLine(rojo, Offset(c.x + R * f, c.y - R * 0.86f), Offset(c.x + R * f, c.y + R * 0.86f), lw); drawLine(rojo, Offset(c.x - R * 0.86f, c.y + R * f), Offset(c.x + R * 0.86f, c.y + R * f), lw) }
            }
        }
    }

    // ---------------------------------------------------------------- Colonos

    private fun DrawScope.colonos(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val madera = p.c(Color(0xFF8A5A2B)); val maderaOsc = p.c(Color(0xFF5A3A22)); val hierro = p.c(Color(0xFF4A4E54)); val cuero = p.c(Color(0xFF7A4A28))
        when (cat) {
            "Diario" -> {
                // El diario de a bordo: tapa de cuero, correa y hebilla.
                caja(p, 0.26f, 0.12f, 0.74f, 0.74f, p.b(Color(0xFFF1E6C8)))
                caja(p, 0.24f, 0.1f, 0.72f, 0.72f, SolidColor(cuero))
                drawRect(p.linea, Offset(p.x(0.28f), p.y(0.14f)), Size(p.x(0.4f), p.y(0.54f)), style = Stroke(lw * 0.5f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(lw * 1.2f, lw))))
                drawRect(p.c(Color(0xFF4A2A14)), Offset(p.x(0.24f), p.y(0.38f)), Size(p.x(0.56f), p.y(0.06f)))
                drawRect(p.metal(0.62f, 0.74f), Offset(p.x(0.62f), p.y(0.365f)), Size(p.x(0.1f), p.y(0.09f)), style = Stroke(lw))
                drawRect(p.metal(0.3f, 0.66f), Offset(p.x(0.34f), p.y(0.2f)), Size(p.x(0.28f), p.y(0.1f)))
            }
            "Mundo" -> {
                // La brújula de bolsillo: caja de latón, rosa y aguja roja y azul.
                val c = p.o(0.5f, 0.44f); val R = p.w * 0.3f
                drawCircle(p.metal(0.48f, 0.52f), p.w * 0.05f, Offset(c.x, c.y - R - p.w * 0.05f), style = Stroke(lw))
                drawCircle(p.metal(0.2f, 0.8f), R, c)
                drawCircle(p.b(Color(0xFFF4EEDF)), R * 0.84f, c)
                for (k in 0 until 8) { val a = k * PI / 4; val rr = if (k % 2 == 0) R * 0.7f else R * 0.45f; drawLine(p.linea, c, Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat()), lw * 0.5f) }
                drawPath(Path().apply { moveTo(c.x, c.y - R * 0.66f); lineTo(c.x + R * 0.08f, c.y); lineTo(c.x - R * 0.08f, c.y); close() }, p.c(Color(0xFFB0281E)))
                drawPath(Path().apply { moveTo(c.x, c.y + R * 0.66f); lineTo(c.x + R * 0.08f, c.y); lineTo(c.x - R * 0.08f, c.y); close() }, p.c(Color(0xFF1F3A6E)))
                drawCircle(p.metal(0.48f, 0.52f), p.w * 0.025f, c)
            }
            "Cuerpo" -> {
                // El hacha: el mango de madera y la cabeza de hierro.
                drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.76f)); quadraticTo(p.x(0.44f), p.y(0.4f), p.x(0.6f), p.y(0.08f)) }, madera, style = Stroke(lw * 2.6f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p.x(0.54f), p.y(0.12f)); lineTo(p.x(0.68f), p.y(0.16f)); quadraticTo(p.x(0.86f), p.y(0.14f), p.x(0.9f), p.y(0.3f)); quadraticTo(p.x(0.76f), p.y(0.3f), p.x(0.62f), p.y(0.3f)); lineTo(p.x(0.5f), p.y(0.24f)); close() }, p.metal(0.5f, 0.9f))
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.86f), p.y(0.16f)); quadraticTo(p.x(0.9f), p.y(0.22f), p.x(0.9f), p.y(0.3f)) }, Color.White.copy(alpha = 0.7f), style = Stroke(lw * 0.6f))
            }
            "Mente" -> {
                // El telar de marco: la urdimbre y una franja ya tejida.
                caja(p, 0.16f, 0.08f, 0.22f, 0.76f, SolidColor(madera)); caja(p, 0.78f, 0.08f, 0.84f, 0.76f, SolidColor(madera))
                caja(p, 0.14f, 0.1f, 0.86f, 0.15f, SolidColor(maderaOsc)); caja(p, 0.14f, 0.62f, 0.86f, 0.67f, SolidColor(maderaOsc))
                for (k in 0 until 12) drawLine(p.c(Color(0xFFE9E1CF)), p.o(0.26f + k * 0.044f, 0.15f), p.o(0.26f + k * 0.044f, 0.62f), lw * 0.4f)
                val tejido = listOf(Color(0xFFB0281E), Color(0xFF1F3A6E), Color(0xFFE2B34A), Color(0xFF2E6B3A))
                for (k in 0 until 6) drawRect(p.c(tejido[k % 4]), Offset(p.x(0.24f), p.y(0.44f + k * 0.03f)), Size(p.x(0.52f), p.y(0.03f)))
                caja(p, 0.22f, 0.36f, 0.78f, 0.38f)
            }
            "Cultura" -> {
                // La linterna de hojalata: cuerpo perforado, techo cónico, asa y la luz.
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.5f), Color.Transparent), p.o(0.5f, 0.45f), p.w * 0.42f), p.w * 0.42f, p.o(0.5f, 0.45f))
                drawCircle(p.metal(0.42f, 0.58f), p.w * 0.07f, p.o(0.5f, 0.1f), style = Stroke(lw))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.16f)); lineTo(p.x(0.72f), p.y(0.3f)); lineTo(p.x(0.28f), p.y(0.3f)); close() }, p.metal(0.28f, 0.72f))
                caja(p, 0.3f, 0.3f, 0.7f, 0.7f)
                if (p.hecho) for (i in 0 until 5) for (j in 0 until 6) { val x = 0.35f + i * 0.075f; val y = 0.35f + j * 0.06f; drawCircle(Color(0xFFFFE6A3), p.w * (if ((i + j) % 2 == 0) 0.012f else 0.008f), p.o(x, y)) }
                caja(p, 0.27f, 0.7f, 0.73f, 0.74f)
            }
            "Gente" -> {
                // La campana del pueblo en su yugo de madera.
                caja(p, 0.16f, 0.1f, 0.84f, 0.15f, SolidColor(maderaOsc))
                caja(p, 0.18f, 0.15f, 0.23f, 0.76f, SolidColor(madera)); caja(p, 0.77f, 0.15f, 0.82f, 0.76f, SolidColor(madera))
                drawPath(Path().apply { moveTo(p.x(0.36f), p.y(0.58f)); lineTo(p.x(0.38f), p.y(0.3f)); quadraticTo(p.x(0.4f), p.y(0.18f), p.x(0.5f), p.y(0.18f)); quadraticTo(p.x(0.6f), p.y(0.18f), p.x(0.62f), p.y(0.3f)); lineTo(p.x(0.64f), p.y(0.58f)); quadraticTo(p.x(0.5f), p.y(0.62f), p.x(0.36f), p.y(0.58f)); close() }, p.metal(0.36f, 0.64f))
                drawCircle(hierro, p.w * 0.035f, p.o(0.5f, 0.63f))
                drawLine(p.linea, p.o(0.37f, 0.52f), p.o(0.63f, 0.52f), lw * 0.6f)
            }
            "Constancia" -> {
                // El yunque sobre su base.
                drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.3f)); quadraticTo(p.x(0.2f), p.y(0.4f), p.x(0.36f), p.y(0.42f)); lineTo(p.x(0.36f), p.y(0.48f)); lineTo(p.x(0.3f), p.y(0.6f)); lineTo(p.x(0.74f), p.y(0.6f)); lineTo(p.x(0.68f), p.y(0.48f)); lineTo(p.x(0.68f), p.y(0.42f)); lineTo(p.x(0.86f), p.y(0.42f)); lineTo(p.x(0.86f), p.y(0.3f)); close() }, p.metal(0.1f, 0.86f))
                caja(p, 0.24f, 0.6f, 0.8f, 0.66f, SolidColor(hierro))
                drawRect(Brush.horizontalGradient(listOf(lerp(madera, Color.Black, 0.3f), madera, lerp(madera, Color.Black, 0.3f)), p.x(0.3f), p.x(0.74f)), Offset(p.x(0.3f), p.y(0.66f)), Size(p.x(0.44f), p.y(0.1f)))
                if (p.hecho) drawLine(Color.White.copy(alpha = 0.6f), p.o(0.14f, 0.305f), p.o(0.84f, 0.305f), lw * 0.6f)
            }
            "Curiosos" -> {
                // El barril: duelas, aros de hierro y el tapón.
                val barril = Path().apply { moveTo(p.x(0.28f), p.y(0.12f)); lineTo(p.x(0.72f), p.y(0.12f)); quadraticTo(p.x(0.86f), p.y(0.43f), p.x(0.72f), p.y(0.74f)); lineTo(p.x(0.28f), p.y(0.74f)); quadraticTo(p.x(0.14f), p.y(0.43f), p.x(0.28f), p.y(0.12f)); close() }
                drawPath(barril, Brush.horizontalGradient(listOf(lerp(madera, Color.Black, 0.35f), madera, lerp(madera, Color.White, 0.12f), madera, lerp(madera, Color.Black, 0.35f)), p.x(0.18f), p.x(0.82f)))
                clipPath(barril) { for (k in 1 until 6) { val x = p.x(0.18f + k * 0.107f); drawLine(maderaOsc, Offset(x, p.y(0.1f)), Offset(x, p.y(0.76f)), lw * 0.5f) } }
                listOf(0.2f, 0.32f, 0.54f, 0.66f).forEach { y -> val dx = if (y < 0.43f) (0.43f - y) * 0.35f else (y - 0.43f) * 0.35f
                    drawRect(p.metal(0.2f, 0.8f), Offset(p.x(0.18f + dx), p.y(y)), Size(p.x(0.64f - 2 * dx), p.y(0.03f))) }
                drawCircle(maderaOsc, p.w * 0.03f, p.o(0.5f, 0.43f))
            }
            else -> {
                // La rueda de carreta: cubo, doce rayos de madera y el aro de hierro.
                val c = p.o(0.5f, 0.42f); val R = p.w * 0.34f
                for (k in 0 until 12) { val a = k * PI / 6; drawLine(madera, c, Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat()), lw * 1.3f) }
                drawCircle(madera, R * 0.92f, c, style = Stroke(lw * 2.2f))
                drawCircle(p.metal(0.16f, 0.84f), R, c, style = Stroke(lw * 1.2f))
                drawCircle(p.metal(0.42f, 0.58f), p.w * 0.07f, c); drawCircle(hierro, p.w * 0.025f, c)
                caja(p, 0.46f, 0.42f + 0.34f * p.w / p.h, 0.54f, 0.77f, SolidColor(maderaOsc))
            }
        }
    }

    // ---------------------------------------------------------------- Botánico

    private fun DrawScope.botanico(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val hoja = p.c(Color(0xFF3F7A3A)); val hojaClara = p.c(Color(0xFF7FA35A)); val tinta = p.c(Color(0xFF4A3A22)); val papel = p.c(Color(0xFFF1E9D2))
        when (cat) {
            "Diario" -> {
                // La lámina botánica enmarcada: tallo, hojas, flor y la etiqueta.
                caja(p, 0.16f, 0.06f, 0.84f, 0.74f)
                drawRect(papel, Offset(p.x(0.2f), p.y(0.09f)), Size(p.x(0.6f), p.y(0.62f)))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.6f)); cubicTo(p.x(0.48f), p.y(0.45f), p.x(0.54f), p.y(0.3f), p.x(0.5f), p.y(0.2f)) }, tinta, style = Stroke(lw * 0.6f))
                listOf(0.5f to -1f, 0.42f to 1f, 0.34f to -1f).forEach { (y, s) -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(y)); quadraticTo(p.x(0.5f + s * 0.12f), p.y(y - 0.08f), p.x(0.5f + s * 0.2f), p.y(y - 0.02f)); quadraticTo(p.x(0.5f + s * 0.1f), p.y(y + 0.02f), p.x(0.5f), p.y(y)); close() }, hoja) }
                for (k in 0 until 5) { val a = k * 2 * PI / 5 - PI / 2; drawCircle(p.c(Color(0xFFC86A7A)), p.w * 0.04f, Offset(p.x(0.5f) + (p.w * 0.05f * cos(a)).toFloat(), p.y(0.18f) + (p.w * 0.05f * sin(a)).toFloat())) }
                drawCircle(p.c(Color(0xFFE2B34A)), p.w * 0.025f, p.o(0.5f, 0.18f))
                drawRect(tinta.copy(alpha = 0.5f), Offset(p.x(0.32f), p.y(0.63f)), Size(p.x(0.36f), p.y(0.04f)), style = Stroke(lw * 0.4f))
            }
            "Mundo" -> {
                // La caja de herborizar: cilindro de hojalata verde, correa y pestillo.
                drawPath(Path().apply { moveTo(p.x(0.2f), p.y(0.36f)); quadraticTo(p.x(0.5f), p.y(0.04f), p.x(0.8f), p.y(0.36f)) }, p.c(Color(0xFF5A3A22)), style = Stroke(lw * 1.2f))
                val cuerpo = Rect(p.x(0.12f), p.y(0.34f), p.x(0.88f), p.y(0.66f))
                drawRoundRect(Brush.verticalGradient(listOf(lerp(hoja, Color.White, 0.2f), hoja, lerp(hoja, Color.Black, 0.3f)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size, CornerRadius(cuerpo.height / 2))
                drawRect(p.metal(0.3f, 0.7f), Offset(p.x(0.3f), p.y(0.36f)), Size(p.x(0.4f), p.y(0.28f)), style = Stroke(lw * 0.6f))
                drawRect(p.metal(0.46f, 0.54f), Offset(p.x(0.47f), p.y(0.45f)), Size(p.x(0.06f), p.y(0.06f)))
                caja(p, 0.3f, 0.66f, 0.34f, 0.76f); caja(p, 0.66f, 0.66f, 0.7f, 0.76f)
            }
            "Cuerpo" -> {
                // Una hoja de roble con sus lóbulos y nervaduras.
                val c = p.o(0.5f, 0.4f)
                val path = Path().apply {
                    moveTo(c.x, p.y(0.06f))
                    val lob = listOf(0.14f to 0.12f, 0.24f to 0.18f, 0.36f to 0.22f, 0.5f to 0.2f, 0.62f to 0.12f)
                    lob.forEach { (y, a) -> quadraticTo(c.x + p.w * (a + 0.08f), p.y(y), c.x + p.w * 0.04f, p.y(y + 0.06f)) }
                    lineTo(c.x, p.y(0.72f))
                    lob.reversed().forEach { (y, a) -> quadraticTo(c.x - p.w * (a + 0.08f), p.y(y + 0.06f), c.x - p.w * 0.04f, p.y(y)) }
                    close()
                }
                drawPath(path, Brush.verticalGradient(listOf(hojaClara, hoja), p.y(0.06f), p.y(0.72f)))
                drawLine(p.c(Color(0xFF2A4A22)), Offset(c.x, p.y(0.08f)), Offset(c.x, p.y(0.77f)), lw * 0.8f)
                for (k in 0 until 5) { val y = p.y(0.18f + k * 0.1f); listOf(-1f, 1f).forEach { s -> drawLine(p.c(Color(0xFF2A4A22)).copy(alpha = 0.6f), Offset(c.x, y + p.h * 0.03f), Offset(c.x + s * p.w * 0.16f, y), lw * 0.4f) } }
            }
            "Mente" -> {
                // La lupa: aro de latón, el cristal con su brillo y el mango.
                val c = p.o(0.44f, 0.34f); val R = p.w * 0.24f
                drawLine(p.c(Color(0xFF5A3A22)), Offset(c.x + R * 0.7f, c.y + R * 0.7f), p.o(0.84f, 0.74f), lw * 3, StrokeCap.Round)
                drawCircle(p.c(Color(0xFFCFE6F0)).copy(alpha = if (p.hecho) 0.7f else 1f), R, c)
                if (p.hecho) drawArc(Color.White.copy(alpha = 0.8f), 200f, 60f, false, Offset(c.x - R * 0.7f, c.y - R * 0.7f), Size(R * 1.4f, R * 1.4f), style = Stroke(lw))
                drawCircle(p.metal(0.2f, 0.68f), R, c, style = Stroke(lw * 1.6f))
            }
            "Cultura" -> {
                // Un helecho: el raquis curvo y sus foliolos, más chicos hacia la punta.
                val n = 11
                val raquis = Path().apply { moveTo(p.x(0.5f), p.y(0.76f)); cubicTo(p.x(0.46f), p.y(0.5f), p.x(0.56f), p.y(0.2f), p.x(0.68f), p.y(0.06f)) }
                drawPath(raquis, p.c(Color(0xFF2A4A22)), style = Stroke(lw * 0.8f))
                for (k in 0 until n) {
                    val t = k / n.toFloat()
                    val x = p.x(0.5f + 0.18f * t * t - 0.02f * t); val y = p.y(0.72f - 0.64f * t)
                    val l = p.w * 0.2f * (1f - t * 0.8f)
                    listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(x, y); quadraticTo(x + s * l * 0.5f, y - l * 0.35f, x + s * l, y - l * 0.1f); quadraticTo(x + s * l * 0.5f, y + l * 0.1f, x, y); close() }, if (k % 2 == 0) hoja else hojaClara) }
                }
            }
            "Gente" -> {
                // Dos cerezas unidas por el tallo, con su hoja.
                drawPath(Path().apply { moveTo(p.x(0.34f), p.y(0.5f)); quadraticTo(p.x(0.4f), p.y(0.2f), p.x(0.56f), p.y(0.1f)) }, p.c(Color(0xFF4A3A22)), style = Stroke(lw * 0.7f))
                drawPath(Path().apply { moveTo(p.x(0.66f), p.y(0.52f)); quadraticTo(p.x(0.64f), p.y(0.24f), p.x(0.56f), p.y(0.1f)) }, p.c(Color(0xFF4A3A22)), style = Stroke(lw * 0.7f))
                drawPath(Path().apply { moveTo(p.x(0.56f), p.y(0.1f)); quadraticTo(p.x(0.74f), p.y(0.0f), p.x(0.86f), p.y(0.12f)); quadraticTo(p.x(0.72f), p.y(0.2f), p.x(0.56f), p.y(0.1f)); close() }, hoja)
                val rojo = p.c(Color(0xFFA3172E))
                listOf(0.34f to 0.56f, 0.66f to 0.58f).forEach { (x, y) ->
                    drawCircle(Brush.radialGradient(listOf(lerp(rojo, Color.White, 0.35f), rojo, lerp(rojo, Color.Black, 0.3f)), Offset(p.x(x - 0.05f), p.y(y - 0.04f)), p.w * 0.2f), p.w * 0.14f, p.o(x, y))
                    drawCircle(p.metal(x - 0.14f, x + 0.14f), p.w * 0.14f, p.o(x, y), style = Stroke(lw * 0.4f))
                }
            }
            "Constancia" -> {
                // Una semilla que brota: la raíz hacia abajo y los dos cotiledones.
                drawRect(p.c(Color(0xFF6B4A2A)).copy(alpha = if (p.hecho) 0.5f else 1f), Offset(p.x(0.1f), p.y(0.5f)), Size(p.x(0.8f), p.y(0.26f)))
                drawLine(p.c(Color(0xFF6B4A2A)), p.o(0.1f, 0.5f), p.o(0.9f, 0.5f), lw)
                drawOval(p.metal(0.38f, 0.62f), Offset(p.x(0.38f), p.y(0.5f)), Size(p.x(0.24f), p.y(0.1f)))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.6f)); cubicTo(p.x(0.46f), p.y(0.66f), p.x(0.54f), p.y(0.7f), p.x(0.48f), p.y(0.75f)) }, p.c(Color(0xFFE9E1CF)), style = Stroke(lw * 0.6f))
                drawLine(hojaClara, p.o(0.5f, 0.5f), p.o(0.5f, 0.24f), lw * 1.2f)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.26f)); quadraticTo(p.x(0.5f + s * 0.12f), p.y(0.08f), p.x(0.5f + s * 0.24f), p.y(0.18f)); quadraticTo(p.x(0.5f + s * 0.12f), p.y(0.3f), p.x(0.5f), p.y(0.26f)); close() }, hoja) }
            }
            "Curiosos" -> {
                // La campana de cristal sobre una plantita.
                caja(p, 0.2f, 0.7f, 0.8f, 0.76f)
                drawLine(hoja, p.o(0.5f, 0.7f), p.o(0.5f, 0.42f), lw)
                listOf(0.6f, 0.5f).forEachIndexed { i, y -> val s = if (i == 0) -1f else 1f; drawPath(Path().apply { moveTo(p.x(0.5f), p.y(y)); quadraticTo(p.x(0.5f + s * 0.1f), p.y(y - 0.08f), p.x(0.5f + s * 0.16f), p.y(y - 0.02f)); quadraticTo(p.x(0.5f + s * 0.08f), p.y(y + 0.02f), p.x(0.5f), p.y(y)); close() }, hoja) }
                drawCircle(p.c(Color(0xFFE8A0B4)), p.w * 0.05f, p.o(0.5f, 0.4f))
                val domo = Path().apply { moveTo(p.x(0.24f), p.y(0.7f)); lineTo(p.x(0.24f), p.y(0.3f)); quadraticTo(p.x(0.24f), p.y(0.1f), p.x(0.5f), p.y(0.1f)); quadraticTo(p.x(0.76f), p.y(0.1f), p.x(0.76f), p.y(0.3f)); lineTo(p.x(0.76f), p.y(0.7f)) }
                drawPath(domo, p.c(Color(0xFFCFE6F0)).copy(alpha = if (p.hecho) 0.25f else 1f))
                drawPath(domo, p.metal(0.24f, 0.76f), style = Stroke(lw * 0.8f))
                drawCircle(p.metal(0.46f, 0.54f), p.w * 0.04f, p.o(0.5f, 0.07f))
                if (p.hecho) drawLine(Color.White.copy(alpha = 0.7f), p.o(0.3f, 0.3f), p.o(0.3f, 0.6f), lw * 0.8f)
            }
            else -> {
                // Una flor prensada en papel, con sus cintas en las esquinas.
                drawRect(papel, Offset(p.x(0.14f), p.y(0.08f)), Size(p.x(0.72f), p.y(0.66f)))
                listOf(0.14f to 0.08f, 0.86f to 0.08f, 0.14f to 0.74f, 0.86f to 0.74f).forEach { (x, y) -> drawRect(p.metal(x - 0.05f, x + 0.05f), Offset(p.x(x) - p.w * 0.05f, p.y(y) - p.w * 0.02f), Size(p.w * 0.1f, p.w * 0.04f)) }
                val c = p.o(0.5f, 0.34f)
                drawPath(Path().apply { moveTo(c.x, c.y); cubicTo(c.x - p.w * 0.04f, p.y(0.5f), c.x + p.w * 0.06f, p.y(0.6f), c.x, p.y(0.68f)) }, p.c(Color(0xFF6B7A3A)), style = Stroke(lw * 0.6f))
                for (k in 0 until 5) { val a = k * 2 * PI / 5 - PI / 2
                    drawOval(p.c(Color(0xFF9A6AA8)).copy(alpha = if (p.hecho) 0.8f else 1f), Offset(c.x + (p.w * 0.09f * cos(a)).toFloat() - p.w * 0.07f, c.y + (p.w * 0.09f * sin(a)).toFloat() - p.w * 0.07f), Size(p.w * 0.14f, p.w * 0.14f)) }
                drawCircle(p.c(Color(0xFFE2B34A)), p.w * 0.04f, c)
                drawPath(Path().apply { moveTo(c.x, p.y(0.56f)); quadraticTo(c.x + p.w * 0.14f, p.y(0.5f), c.x + p.w * 0.2f, p.y(0.56f)); quadraticTo(c.x + p.w * 0.1f, p.y(0.6f), c.x, p.y(0.56f)); close() }, hojaClara)
            }
        }
    }

    // ---------------------------------------------------------------- Navidad

    private fun DrawScope.navidad(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val rojo = p.c(Color(0xFFB3122E)); val verde = p.c(Color(0xFF1F6B3A)); val oro = p.c(Color(0xFFD4AF37)); val nieve = p.c(Color(0xFFF4F7FB))
        fun cordel(x: Float, hasta: Float) = drawLine(p.c(Color(0xFF8A6A44)), p.o(x, 0f), p.o(x, hasta), lw * 0.5f)
        when (cat) {
            "Diario" -> {
                // La vela con su flama y el acebo.
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.55f), Color.Transparent), p.o(0.5f, 0.16f), p.w * 0.3f), p.w * 0.3f, p.o(0.5f, 0.16f))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.04f)); cubicTo(p.x(0.58f), p.y(0.12f), p.x(0.56f), p.y(0.2f), p.x(0.5f), p.y(0.22f)); cubicTo(p.x(0.44f), p.y(0.2f), p.x(0.42f), p.y(0.12f), p.x(0.5f), p.y(0.04f)); close() },
                    if (p.hecho) Brush.verticalGradient(listOf(Color(0xFFFFE27A), Color(0xFFFF8A2E)), p.y(0.04f), p.y(0.22f)) else SolidColor(p.sombra))
                drawRect(Brush.horizontalGradient(listOf(lerp(rojo, Color.Black, 0.25f), rojo, lerp(rojo, Color.White, 0.2f), rojo), p.x(0.38f), p.x(0.62f)), Offset(p.x(0.38f), p.y(0.24f)), Size(p.x(0.24f), p.y(0.46f)))
                drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.24f)); lineTo(p.x(0.62f), p.y(0.24f)); lineTo(p.x(0.62f), p.y(0.32f)); quadraticTo(p.x(0.58f), p.y(0.38f), p.x(0.56f), p.y(0.3f)); lineTo(p.x(0.38f), p.y(0.3f)); close() }, p.c(Color(0xFFE85A6A)))
                caja(p, 0.28f, 0.7f, 0.72f, 0.76f)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.7f)); quadraticTo(p.x(0.5f + s * 0.14f), p.y(0.6f), p.x(0.5f + s * 0.3f), p.y(0.68f)); quadraticTo(p.x(0.5f + s * 0.14f), p.y(0.72f), p.x(0.5f), p.y(0.7f)); close() }, verde) }
                listOf(-0.03f, 0.03f).forEach { dx -> drawCircle(rojo, p.w * 0.025f, p.o(0.5f + dx, 0.69f)) }
            }
            "Mundo" -> {
                // La estrella dorada que cuelga, con su resplandor.
                cordel(0.5f, 0.12f)
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE27A).copy(alpha = 0.45f), Color.Transparent), p.o(0.5f, 0.42f), p.w * 0.45f), p.w * 0.45f, p.o(0.5f, 0.42f))
                estrella(p.o(0.5f, 0.42f), p.w * 0.36f, p.w * 0.15f, 5, p.metal(0.14f, 0.86f))
                estrella(p.o(0.5f, 0.42f), p.w * 0.2f, p.w * 0.08f, 5, SolidColor(oro.copy(alpha = if (p.hecho) 0.6f else 1f)))
            }
            "Cuerpo" -> {
                // El bastón de caramelo, rayado de rojo y blanco.
                val baston = Path().apply { moveTo(p.x(0.4f), p.y(0.76f)); lineTo(p.x(0.4f), p.y(0.28f)); cubicTo(p.x(0.4f), p.y(0.04f), p.x(0.74f), p.y(0.04f), p.x(0.74f), p.y(0.26f)) }
                drawPath(baston, nieve, style = Stroke(p.w * 0.11f, cap = StrokeCap.Round))
                if (p.hecho) clipPath(Path().apply { addRect(Rect(0f, 0f, size.width, size.height)) }) {
                    drawPath(baston, rojo, style = Stroke(p.w * 0.11f, cap = StrokeCap.Butt, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(p.w * 0.05f, p.w * 0.05f))))
                }
                drawPath(baston, p.metal(0.3f, 0.8f), style = Stroke(lw * 0.4f))
            }
            "Mente" -> {
                // Un copo de nieve de seis brazos, con sus ramitas.
                cordel(0.5f, 0.06f)
                val c = p.o(0.5f, 0.4f); val R = p.w * 0.36f
                val b = p.metal(0.14f, 0.86f)
                for (k in 0 until 6) {
                    val a = -PI / 2 + k * PI / 3
                    fun pt(r: Float, da: Double = 0.0) = Offset(c.x + (r * cos(a + da)).toFloat(), c.y + (r * sin(a + da)).toFloat())
                    drawLine(b, c, pt(R), lw * 1.4f, StrokeCap.Round)
                    listOf(0.45f, 0.72f).forEach { f -> val base = pt(R * f); listOf(-0.6, 0.6).forEach { da -> drawLine(b, base, Offset(base.x + (R * 0.22f * cos(a + da)).toFloat(), base.y + (R * 0.22f * sin(a + da)).toFloat()), lw, StrokeCap.Round) } }
                }
                drawCircle(b, p.w * 0.05f, c)
                if (p.hecho) drawCircle(nieve, p.w * 0.025f, c)
            }
            "Cultura" -> {
                // La esfera: su casquillo, franjas y el brillo.
                cordel(0.5f, 0.08f)
                drawCircle(p.metal(0.46f, 0.54f), p.w * 0.04f, p.o(0.5f, 0.08f), style = Stroke(lw * 0.6f))
                caja(p, 0.44f, 0.1f, 0.56f, 0.15f)
                val c = p.o(0.5f, 0.42f); val R = p.w * 0.3f
                drawCircle(Brush.radialGradient(listOf(lerp(rojo, Color.White, 0.4f), rojo, lerp(rojo, Color.Black, 0.35f)), Offset(c.x - R * 0.3f, c.y - R * 0.3f), R * 1.4f), R, c)
                clipPath(Path().apply { addOval(Rect(c, R)) }) {
                    drawRect(oro, Offset(c.x - R, c.y - R * 0.15f), Size(2 * R, R * 0.3f))
                    if (p.hecho) for (k in 0 until 8) estrella(Offset(c.x - R + k * R * 0.28f, c.y), R * 0.08f, R * 0.035f, 5, SolidColor(Color(0xFFB3122E)))
                    drawRect(verde, Offset(c.x - R, c.y - R * 0.5f), Size(2 * R, R * 0.06f)); drawRect(verde, Offset(c.x - R, c.y + R * 0.44f), Size(2 * R, R * 0.06f))
                }
                if (p.hecho) drawArc(Color.White.copy(alpha = 0.7f), 200f, 50f, false, Offset(c.x - R * 0.75f, c.y - R * 0.75f), Size(R * 1.5f, R * 1.5f), style = Stroke(lw * 1.2f, cap = StrokeCap.Round))
            }
            "Gente" -> {
                // La corona: un aro de hojas, bayas rojas y el moño.
                val c = p.o(0.5f, 0.38f); val R = p.w * 0.28f
                for (k in 0 until 26) { val a = k * 2 * PI / 26; val o = Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat())
                    drawOval(if (k % 2 == 0) verde else lerp(verde, Color.Black, 0.25f), Offset(o.x - p.w * 0.07f, o.y - p.w * 0.04f), Size(p.w * 0.14f, p.w * 0.08f)) }
                if (p.hecho) for (k in 0 until 7) { val a = k * 2 * PI / 7 + 0.3; drawCircle(Color(0xFFB3122E), p.w * 0.025f, Offset(c.x + (R * 1.05f * cos(a)).toFloat(), c.y + (R * 1.05f * sin(a)).toFloat())) }
                val m = Offset(c.x, c.y + R)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(m.x, m.y); quadraticTo(m.x + s * p.w * 0.12f, m.y - p.w * 0.1f, m.x + s * p.w * 0.14f, m.y + p.w * 0.02f); close() }, rojo)
                    drawLine(rojo, m, Offset(m.x + s * p.w * 0.08f, m.y + p.w * 0.18f), lw * 1.2f) }
                drawCircle(oro, p.w * 0.025f, m)
            }
            "Constancia" -> {
                // La campana dorada con su listón rojo.
                cordel(0.5f, 0.1f)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.14f)); quadraticTo(p.x(0.5f + s * 0.16f), p.y(0.06f), p.x(0.5f + s * 0.2f), p.y(0.16f)); quadraticTo(p.x(0.5f + s * 0.1f), p.y(0.18f), p.x(0.5f), p.y(0.14f)); close() }, rojo) }
                drawPath(Path().apply { moveTo(p.x(0.24f), p.y(0.62f)); quadraticTo(p.x(0.32f), p.y(0.56f), p.x(0.34f), p.y(0.34f)); quadraticTo(p.x(0.38f), p.y(0.16f), p.x(0.5f), p.y(0.16f)); quadraticTo(p.x(0.62f), p.y(0.16f), p.x(0.66f), p.y(0.34f)); quadraticTo(p.x(0.68f), p.y(0.56f), p.x(0.76f), p.y(0.62f)); close() }, p.metal(0.24f, 0.76f))
                drawCircle(p.metal(0.44f, 0.56f), p.w * 0.06f, p.o(0.5f, 0.66f))
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.4f), p.y(0.26f)); quadraticTo(p.x(0.36f), p.y(0.4f), p.x(0.36f), p.y(0.52f)) }, Color.White.copy(alpha = 0.6f), style = Stroke(lw * 0.8f))
            }
            "Curiosos" -> {
                // El regalo: caja, listón en cruz y el moño.
                caja(p, 0.18f, 0.34f, 0.82f, 0.76f, SolidColor(verde))
                caja(p, 0.14f, 0.28f, 0.86f, 0.36f, SolidColor(lerp(verde, Color.White, 0.08f)))
                drawRect(rojo, Offset(p.x(0.45f), p.y(0.28f)), Size(p.x(0.1f), p.y(0.48f)))
                drawRect(rojo, Offset(p.x(0.18f), p.y(0.5f)), Size(p.x(0.64f), p.y(0.06f)))
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.28f)); cubicTo(p.x(0.5f + s * 0.24f), p.y(0.06f), p.x(0.5f + s * 0.3f), p.y(0.3f), p.x(0.5f), p.y(0.28f)); close() }, rojo) }
                drawCircle(oro, p.w * 0.04f, p.o(0.5f, 0.28f))
                if (p.hecho) for (k in 0 until 10) drawCircle(oro.copy(alpha = 0.6f), p.w * 0.012f, p.o(0.22f + (k % 5) * 0.14f, 0.42f + (k / 5) * 0.22f))
            }
            else -> {
                // La piña con sus escamas.
                cordel(0.5f, 0.1f)
                val c = p.o(0.5f, 0.42f)
                drawOval(Brush.verticalGradient(listOf(p.c(Color(0xFF8A5A2B)), p.c(Color(0xFF5A3A22))), p.y(0.12f), p.y(0.74f)), Offset(p.x(0.28f), p.y(0.12f)), Size(p.x(0.44f), p.y(0.62f)))
                clipPath(Path().apply { addOval(Rect(p.x(0.28f), p.y(0.12f), p.x(0.72f), p.y(0.74f))) }) {
                    var fila = 0; var y = p.y(0.16f)
                    while (y < p.y(0.76f)) {
                        var x = p.x(0.24f) + (if (fila % 2 == 0) 0f else p.w * 0.05f)
                        while (x < p.x(0.76f)) { drawArc(p.c(Color(0xFF3A2414)), 0f, 180f, false, Offset(x, y), Size(p.w * 0.1f, p.w * 0.08f), style = Stroke(lw * 0.6f)); x += p.w * 0.1f }
                        y += p.w * 0.06f; fila++
                    }
                }
                if (p.hecho) for (k in 0 until 6) drawCircle(nieve, p.w * 0.015f, p.o(0.36f + k * 0.055f, 0.16f + (k % 2) * 0.02f))
            }
        }
    }

    // ---------------------------------------------------------------- Tierras Antiguas

    private fun DrawScope.antiguas(cat: String, p: TrofeosTema.Pincel) = with(TrofeosTema) {
        val lw = p.w * 0.01f
        val hierro = p.c(Color(0xFF4A4E54)); val cuero = p.c(Color(0xFF5A3A22)); val pergamino = p.c(Color(0xFFE9DCC0))
        val rubi = p.c(Color(0xFFA3172E)); val esmeralda = p.c(Color(0xFF1E7A5A)); val cristal = p.c(Color(0xFF8FB8E0))
        when (cat) {
            "Diario" -> {
                // El libro de runas: tapa de cuero, esquinas de hierro y signos inventados.
                caja(p, 0.22f, 0.1f, 0.78f, 0.74f, SolidColor(cuero))
                listOf(0.22f to 0.1f, 0.78f to 0.1f, 0.22f to 0.74f, 0.78f to 0.74f).forEach { (x, y) -> drawCircle(hierro, p.w * 0.06f, p.o(x, y)) }
                drawRect(p.metal(0.26f, 0.74f), Offset(p.x(0.27f), p.y(0.15f)), Size(p.x(0.46f), p.y(0.54f)), style = Stroke(lw * 0.8f))
                if (p.hecho) { runas(p.o(0.32f, 0.3f), p.x(0.36f), p.y(0.06f), p.brillo, lw * 0.7f, 4, 1); runas(p.o(0.32f, 0.54f), p.x(0.36f), p.y(0.06f), p.brillo, lw * 0.7f, 4, 5) }
                drawCircle(rubi, p.w * 0.05f, p.o(0.5f, 0.42f)); drawCircle(p.metal(0.44f, 0.56f), p.w * 0.05f, p.o(0.5f, 0.42f), style = Stroke(lw * 0.6f))
            }
            "Mundo" -> {
                // El mapa de la travesía: montañas, bosque, el camino y la fortaleza al final.
                drawPath(Path().apply { moveTo(p.x(0.08f), p.y(0.14f)); lineTo(p.x(0.92f), p.y(0.18f)); lineTo(p.x(0.9f), p.y(0.72f)); lineTo(p.x(0.1f), p.y(0.7f)); close() }, SolidColor(pergamino))
                if (p.hecho) {
                    for (k in 0 until 4) drawPath(Path().apply { val x = p.x(0.48f + k * 0.08f); moveTo(x - p.w * 0.05f, p.y(0.36f)); lineTo(x, p.y(0.24f)); lineTo(x + p.w * 0.05f, p.y(0.36f)) }, Color(0xFF5A4A3A), style = Stroke(lw * 0.7f))
                    for (k in 0 until 8) drawCircle(Color(0xFF3F6A3A), p.w * 0.022f, p.o(0.16f + (k % 4) * 0.05f, 0.42f + (k / 4) * 0.05f))
                    drawPath(Path().apply { moveTo(p.x(0.14f), p.y(0.64f)); cubicTo(p.x(0.36f), p.y(0.6f), p.x(0.42f), p.y(0.44f), p.x(0.6f), p.y(0.5f)); cubicTo(p.x(0.72f), p.y(0.54f), p.x(0.74f), p.y(0.36f), p.x(0.84f), p.y(0.3f)) },
                        Color(0xFF7A1A12), style = Stroke(lw * 0.8f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(lw * 2, lw * 1.4f))))
                    drawRect(Color(0xFF3A3A3A), Offset(p.x(0.8f), p.y(0.22f)), Size(p.x(0.08f), p.y(0.08f)))
                    drawLine(Color(0xFF3A3A3A), p.o(0.84f, 0.22f), p.o(0.84f, 0.16f), lw * 0.5f)
                }
                drawPath(Path().apply { moveTo(p.x(0.08f), p.y(0.14f)); lineTo(p.x(0.92f), p.y(0.18f)); lineTo(p.x(0.9f), p.y(0.72f)); lineTo(p.x(0.1f), p.y(0.7f)); close() }, p.metal(0.08f, 0.92f), style = Stroke(lw * 0.8f))
            }
            "Cuerpo" -> {
                // El yelmo: cúpula, nasal, carrilleras y remaches.
                val yelmo = Path().apply { moveTo(p.x(0.22f), p.y(0.66f)); lineTo(p.x(0.22f), p.y(0.38f)); cubicTo(p.x(0.22f), p.y(0.06f), p.x(0.78f), p.y(0.06f), p.x(0.78f), p.y(0.38f)); lineTo(p.x(0.78f), p.y(0.66f)); lineTo(p.x(0.62f), p.y(0.66f)); lineTo(p.x(0.6f), p.y(0.46f)); lineTo(p.x(0.4f), p.y(0.46f)); lineTo(p.x(0.38f), p.y(0.66f)); close() }
                drawPath(yelmo, p.metal(0.22f, 0.78f))
                caja(p, 0.47f, 0.38f, 0.53f, 0.62f)
                drawLine(p.linea, p.o(0.22f, 0.38f), p.o(0.78f, 0.38f), lw)
                if (p.hecho) for (k in 0 until 7) drawCircle(p.linea, p.w * 0.012f, p.o(0.26f + k * 0.08f, 0.36f))
                drawLine(p.linea, p.o(0.5f, 0.1f), p.o(0.5f, 0.36f), lw * 0.6f)
                caja(p, 0.3f, 0.7f, 0.7f, 0.76f)
            }
            "Mente" -> {
                // Un racimo de cristales: prismas hexagonales con sus brillos.
                listOf(Triple(0.5f, 0.06f, 0.14f), Triple(0.34f, 0.2f, 0.11f), Triple(0.66f, 0.24f, 0.1f), Triple(0.24f, 0.36f, 0.08f), Triple(0.76f, 0.4f, 0.08f)).forEachIndexed { i, (x, y, ww) ->
                    val prisma = Path().apply { moveTo(p.x(x), p.y(y)); lineTo(p.x(x + ww / 2), p.y(y + 0.08f)); lineTo(p.x(x + ww / 2), p.y(0.66f)); lineTo(p.x(x - ww / 2), p.y(0.66f)); lineTo(p.x(x - ww / 2), p.y(y + 0.08f)); close() }
                    val col = if (i % 2 == 0) cristal else p.c(Color(0xFFB79AD8))
                    drawPath(prisma, Brush.horizontalGradient(listOf(lerp(col, Color.White, 0.4f), col, lerp(col, Color.Black, 0.25f)), p.x(x - ww / 2), p.x(x + ww / 2)))
                    drawPath(prisma, p.linea, style = Stroke(lw * 0.4f))
                    drawLine(p.linea.copy(alpha = 0.4f), p.o(x, y), p.o(x, 0.66f), lw * 0.3f)
                }
                drawPath(Path().apply { moveTo(p.x(0.12f), p.y(0.76f)); quadraticTo(p.x(0.2f), p.y(0.62f), p.x(0.5f), p.y(0.64f)); quadraticTo(p.x(0.8f), p.y(0.62f), p.x(0.88f), p.y(0.76f)); close() }, p.b(Color(0xFF6B6F7A)))
            }
            "Cultura" -> {
                // Un arpa pequeña: caja de resonancia, cuello curvo, columna y cuerdas.
                val madera = p.c(Color(0xFF8A5A2B))
                drawLine(madera, p.o(0.26f, 0.74f), p.o(0.26f, 0.12f), lw * 2.2f, StrokeCap.Round)
                drawPath(Path().apply { moveTo(p.x(0.26f), p.y(0.12f)); cubicTo(p.x(0.44f), p.y(0.04f), p.x(0.6f), p.y(0.28f), p.x(0.78f), p.y(0.2f)) }, madera, style = Stroke(lw * 2, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p.x(0.26f), p.y(0.74f)); lineTo(p.x(0.84f), p.y(0.24f)); lineTo(p.x(0.74f), p.y(0.2f)); lineTo(p.x(0.2f), p.y(0.66f)); close() }, p.metal(0.2f, 0.84f))
                for (k in 0 until 7) { val t = 0.1f + k * 0.12f; val x = 0.26f + 0.5f * t; val yTop = 0.12f + 0.08f * t; val yBot = 0.74f - 0.5f * t + 0.02f
                    drawLine(p.c(Color(0xFFE9E1CF)), p.o(x, yTop + 0.03f), p.o(x, yBot), lw * 0.35f) }
                if (p.hecho) runas(p.o(0.3f, 0.52f), p.x(0.18f), p.y(0.04f), p.linea, lw * 0.4f, 2, 9)
            }
            "Gente" -> {
                // El estandarte: travesaño, tela con dos colas y un emblema inventado (monte sobre un círculo).
                caja(p, 0.485f, 0.04f, 0.515f, 0.77f, SolidColor(p.c(Color(0xFF5A3A22))))
                caja(p, 0.2f, 0.1f, 0.8f, 0.13f)
                val tela = Path().apply { moveTo(p.x(0.24f), p.y(0.13f)); lineTo(p.x(0.76f), p.y(0.13f)); lineTo(p.x(0.76f), p.y(0.62f)); lineTo(p.x(0.63f), p.y(0.54f)); lineTo(p.x(0.5f), p.y(0.64f)); lineTo(p.x(0.37f), p.y(0.54f)); lineTo(p.x(0.24f), p.y(0.62f)); close() }
                drawPath(tela, SolidColor(p.c(Color(0xFF1F3A5A))))
                drawPath(tela, p.metal(0.24f, 0.76f), style = Stroke(lw * 0.8f))
                drawCircle(p.metal(0.38f, 0.62f), p.w * 0.12f, p.o(0.5f, 0.32f), style = Stroke(lw))
                drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.4f)); lineTo(p.x(0.46f), p.y(0.26f)); lineTo(p.x(0.5f), p.y(0.32f)); lineTo(p.x(0.55f), p.y(0.22f)); lineTo(p.x(0.62f), p.y(0.4f)); close() }, p.metal(0.38f, 0.62f))
            }
            "Constancia" -> {
                // La llave de hierro con su ojo de tres lóbulos.
                val c = p.o(0.5f, 0.18f)
                listOf(0.0, 2 * PI / 3, 4 * PI / 3).forEach { a -> drawCircle(p.metal(0.3f, 0.7f), p.w * 0.08f, Offset(c.x + (p.w * 0.08f * cos(a - PI / 2)).toFloat(), c.y + (p.w * 0.08f * sin(a - PI / 2)).toFloat()), style = Stroke(lw * 1.6f)) }
                drawCircle(rubi, p.w * 0.035f, c)
                caja(p, 0.47f, 0.28f, 0.53f, 0.72f)
                caja(p, 0.44f, 0.32f, 0.56f, 0.35f)
                caja(p, 0.53f, 0.58f, 0.66f, 0.62f); caja(p, 0.53f, 0.66f, 0.62f, 0.72f); caja(p, 0.6f, 0.62f, 0.66f, 0.66f)
            }
            "Curiosos" -> {
                // Una gema tallada en brillante, con sus facetas.
                val c = p.o(0.5f, 0.42f); val R = p.w * 0.3f
                val corona = listOf(Offset(c.x - R, c.y - R * 0.2f), Offset(c.x - R * 0.55f, c.y - R * 0.6f), Offset(c.x + R * 0.55f, c.y - R * 0.6f), Offset(c.x + R, c.y - R * 0.2f))
                val gema = Path().apply { moveTo(corona[0].x, corona[0].y); corona.drop(1).forEach { lineTo(it.x, it.y) }; lineTo(c.x, c.y + R * 0.9f); close() }
                drawPath(gema, Brush.linearGradient(listOf(lerp(esmeralda, Color.White, 0.4f), esmeralda, lerp(esmeralda, Color.Black, 0.4f)), Offset(c.x - R, c.y - R), Offset(c.x + R, c.y + R)))
                val l = p.linea.copy(alpha = if (p.hecho) 0.5f else 0.8f)
                drawLine(l, corona[0], corona[3], lw * 0.5f)
                listOf(-0.55f, -0.18f, 0.18f, 0.55f).forEach { f -> drawLine(l, Offset(c.x + R * f, c.y - R * 0.2f), Offset(c.x, c.y + R * 0.9f), lw * 0.4f) }
                drawLine(l, corona[1], Offset(c.x - R * 0.18f, c.y - R * 0.2f), lw * 0.4f); drawLine(l, corona[2], Offset(c.x + R * 0.18f, c.y - R * 0.2f), lw * 0.4f)
                drawPath(gema, p.metal(0.2f, 0.8f), style = Stroke(lw * 0.8f))
                if (p.hecho) estrella(Offset(c.x - R * 0.35f, c.y - R * 0.38f), p.w * 0.05f, p.w * 0.012f, 4, SolidColor(Color.White))
            }
            else -> {
                // La linterna de mina: jaula de hierro, el cristal con la luz y el asa.
                if (p.hecho) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.5f), Color.Transparent), p.o(0.5f, 0.44f), p.w * 0.4f), p.w * 0.4f, p.o(0.5f, 0.44f))
                drawArc(hierro, 180f, 180f, false, Offset(p.x(0.36f), p.y(0.04f)), Size(p.x(0.28f), p.y(0.14f)), style = Stroke(lw * 1.2f))
                caja(p, 0.32f, 0.16f, 0.68f, 0.22f)
                drawRect(p.c(Color(0xFFFFE6A3)).copy(alpha = if (p.hecho) 0.75f else 1f), Offset(p.x(0.36f), p.y(0.22f)), Size(p.x(0.28f), p.y(0.38f)))
                if (p.hecho) drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.3f)); cubicTo(p.x(0.55f), p.y(0.38f), p.x(0.54f), p.y(0.44f), p.x(0.5f), p.y(0.46f)); cubicTo(p.x(0.46f), p.y(0.44f), p.x(0.45f), p.y(0.38f), p.x(0.5f), p.y(0.3f)); close() }, Color(0xFFFF8A2E))
                listOf(0.36f, 0.5f, 0.64f).forEach { x -> drawLine(hierro, p.o(x, 0.22f), p.o(x, 0.6f), lw * 0.9f) }
                caja(p, 0.3f, 0.6f, 0.7f, 0.68f)
                caja(p, 0.36f, 0.68f, 0.64f, 0.74f, SolidColor(hierro))
            }
        }
    }
}
