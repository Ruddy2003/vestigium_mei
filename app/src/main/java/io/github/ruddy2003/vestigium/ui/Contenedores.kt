package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * La unidad que guarda tus momentos, según el tema: la misma lógica (una hoja por día, que engorda con lo escrito),
 * con otra forma. Un rollo de papiro en Roma, tablillas de cera en Grecia, un códice en el scriptorium, un libro de
 * horas en la catedral, el cuaderno de taller del Renacimiento, la bitácora atada con un cabo del pirata, el legajo de despachos del Imperio y el cahier de la
 * Revolución; los demás, en [ContenedoresMas].
 */
enum class Contenedor(val nombre: String, val hoja: String) {
    ROLLO("Rollo de momentos", "columna"),
    TABLILLA("Tablillas de momentos", "tablilla"),
    CODICE("Códice de momentos", "folio"),
    HORAS("Libro de horas", "folio"),
    TALLER("Cuaderno de taller", "hoja"),
    SINGLADURAS("Bitácora de a bordo", "singladura"),
    LEGAJO("Legajo de despachos", "despacho"),
    CAHIER("Cahier de momentos", "página"),
    MANUSCRITO("Manuscrito de momentos", "folio"),
    ACORDEON("Libro plegado de momentos", "pliegue"),
    ORDENES("Rollo de órdenes", "mandato"),
    BITACORA("Cuaderno del pueblo", "página"),
    HERBARIO("Herbario de momentos", "pliego"),
    ADVIENTO("Calendario de adviento", "casilla"),
    MAPAS("Libro de mapas", "mapa"),
    EXPEDIENTE("Expediente de momentos", "folio"),
    CONSOLA("Consola de registro", "entrada"),
    CARPETA("Carpeta del supervisor", "informe"),
    TECNICO("Códice técnico", "lámina"),
    CUADERNO("Cuaderno de laboratorio", "experimento"),
    VUELO("Bitácora de vuelo", "órbita"),
    TETRAD("Cuaderno escolar", "hoja"),
}

fun contenedorDe(t: TemaId): Contenedor = when (t) {
    TemaId.ROMANO -> Contenedor.ROLLO
    TemaId.HELENO -> Contenedor.TABLILLA
    TemaId.MEDIEVAL -> Contenedor.CODICE
    TemaId.GOTICO -> Contenedor.HORAS
    TemaId.RENACIMIENTO -> Contenedor.TALLER
    TemaId.PIRATA -> Contenedor.SINGLADURAS
    TemaId.NAPOLEON -> Contenedor.LEGAJO
    TemaId.REVOLUCION -> Contenedor.CAHIER
    TemaId.ANDALUS -> Contenedor.MANUSCRITO
    TemaId.SEDA -> Contenedor.ACORDEON
    TemaId.MONGOL -> Contenedor.ORDENES
    TemaId.COLONOS -> Contenedor.BITACORA
    TemaId.BOTANICO -> Contenedor.HERBARIO
    TemaId.NAVIDAD -> Contenedor.ADVIENTO
    TemaId.ANTIGUAS -> Contenedor.MAPAS
    TemaId.DISTOPIA -> Contenedor.EXPEDIENTE
    TemaId.CIBER -> Contenedor.CONSOLA
    TemaId.NEON -> Contenedor.CONSOLA
    TemaId.REFUGIO -> Contenedor.CARPETA
    TemaId.ORDEN -> Contenedor.TECNICO
    TemaId.LABORATORIO -> Contenedor.CUADERNO
    TemaId.ESPACIO -> Contenedor.VUELO
    TemaId.URSS -> Contenedor.TETRAD
}

object Contenedores {
    private val MADERA = Color(0xFF7A5232)
    private val MADERA_OSCURA = Color(0xFF4A3020)
    private val PAPIRO = Color(0xFFE9D9A9)
    private val CERA = Color(0xFFE2C482)
    private val PERGAMINO = Color(0xFFF1E6C8)
    /** La vitela fina y blanca del libro de horas (el códice va en pergamino más grueso y amarillo). */
    private val VITELA = Color(0xFFF7F1E3)
    private val AZUR = Color(0xFF1F3A9E)
    private val ORO = Color(0xFFD4AF37)
    private val ORO_OSCURO = Color(0xFF8E6A1F)
    private val LACRE = Color(0xFFA3172E)
    /** El pergamino de la bitácora, verdoso de humedad, y el cuero ennegrecido de brea de sus tapas. */
    private val PERGAMINO_HUMEDO = Color(0xFFE6DCBA)
    private val CUERO_BREA = Color(0xFF4A3424)
    private val CANAMO = Color(0xFFB59A62)

    /** El color del papel de una hoja abierta (de día y de noche). */
    fun papel(c: Contenedor, cs: ColorScheme, oscuro: Boolean): Color = when (c) {
        Contenedor.ROLLO -> if (oscuro) lerp(PAPIRO, Color.Black, 0.72f) else PAPIRO
        Contenedor.TABLILLA -> if (oscuro) lerp(CERA, Color.Black, 0.75f) else CERA
        Contenedor.CODICE -> if (oscuro) lerp(PERGAMINO, Color.Black, 0.76f) else PERGAMINO
        Contenedor.HORAS -> if (oscuro) lerp(VITELA, Color.Black, 0.8f) else VITELA
        Contenedor.TALLER -> if (oscuro) lerp(Color(0xFFEAD9B0), Color.Black, 0.74f) else Color(0xFFEAD9B0)
        Contenedor.SINGLADURAS -> if (oscuro) lerp(PERGAMINO_HUMEDO, Color.Black, 0.76f) else PERGAMINO_HUMEDO
        Contenedor.LEGAJO -> if (oscuro) lerp(Color(0xFFF3EEDF), Color.Black, 0.78f) else Color(0xFFF7F2E4)
        Contenedor.CAHIER -> if (oscuro) lerp(Color(0xFFF8F6EE), Color.Black, 0.8f) else Color(0xFFF8F6EE)
        // Las libretas del lote 2 tienen su papel en [ContenedoresMas].
        Contenedor.MANUSCRITO, Contenedor.ACORDEON, Contenedor.ORDENES, Contenedor.BITACORA, Contenedor.HERBARIO, Contenedor.ADVIENTO, Contenedor.MAPAS,
            Contenedor.EXPEDIENTE, Contenedor.CONSOLA, Contenedor.CARPETA, Contenedor.TECNICO, Contenedor.CUADERNO, Contenedor.VUELO, Contenedor.TETRAD -> ContenedoresMas.papel(c, cs, oscuro)
    }

    // ================================================================== cerrado

    /** El contenedor cerrado, que engorda con las [hojas] escritas. [tapa] es el color del tema. */
    fun DrawScope.cerrado(c: Contenedor, hojas: Int, tapa: Color, secundario: Color, oscuro: Boolean) {
        val d = density
        val grosor = (3f + min(11f, hojas / 12f)) * d
        when (c) {
            Contenedor.ROLLO -> rolloCerrado(hojas, d)
            Contenedor.TABLILLA -> tablillasCerradas(grosor, d)
            Contenedor.CODICE -> libroCerrado(grosor, d, Color(0xFF6B3A1F), ORO_OSCURO, bandas = true, broches = true)
            Contenedor.HORAS -> horasCerrado(grosor, d)
            Contenedor.TALLER -> tallerCerrado(grosor, d, oscuro)
            Contenedor.SINGLADURAS -> bitacoraCerrada(grosor, d, oscuro)
            Contenedor.LEGAJO -> legajoCerrado(grosor, d, tapa)
            Contenedor.CAHIER -> cahierCerrado(grosor, d)
            Contenedor.MANUSCRITO, Contenedor.ACORDEON, Contenedor.ORDENES, Contenedor.BITACORA, Contenedor.HERBARIO, Contenedor.ADVIENTO, Contenedor.MAPAS,
            Contenedor.EXPEDIENTE, Contenedor.CONSOLA, Contenedor.CARPETA, Contenedor.TECNICO, Contenedor.CUADERNO, Contenedor.VUELO, Contenedor.TETRAD -> with(ContenedoresMas) { cerradoMas(c, hojas, tapa, oscuro) }
        }
    }

    private fun DrawScope.sombra(o: Offset, s: Size, r: Float) = drawRoundRect(Color.Black.copy(alpha = 0.18f), o, s, CornerRadius(r))

    /** Un rollo de papiro de pie: más grueso cuanto más escrito, con sus varillas y la etiqueta colgando. */
    private fun DrawScope.rolloCerrado(hojas: Int, d: Float) {
        val w = size.width; val h = size.height
        val ancho = w * (0.34f + min(0.22f, hojas / 400f))
        val x0 = (w - ancho) / 2; val y0 = h * 0.10f; val y1 = h * 0.88f
        sombra(Offset(x0 + 6 * d, y0 + 8 * d), Size(ancho, y1 - y0), ancho / 2)
        // El cilindro de papiro, con su luz.
        drawRect(Brush.horizontalGradient(listOf(lerp(PAPIRO, Color.Black, 0.25f), PAPIRO, lerp(PAPIRO, Color.White, 0.35f), PAPIRO, lerp(PAPIRO, Color.Black, 0.3f)), x0, x0 + ancho),
            Offset(x0, y0), Size(ancho, y1 - y0))
        // Las vueltas del rollo, vistas arriba.
        drawOval(lerp(PAPIRO, Color.Black, 0.08f), Offset(x0, y0 - ancho * 0.16f), Size(ancho, ancho * 0.32f))
        for (k in 1..5) { val f = k / 6f; drawOval(lerp(PAPIRO, Color.Black, 0.3f), Offset(x0 + ancho * f / 2, y0 - ancho * 0.16f * (1 - f)), Size(ancho * (1 - f), ancho * 0.32f * (1 - f)), style = Stroke(0.8f * d)) }
        drawOval(lerp(PAPIRO, Color.Black, 0.2f), Offset(x0, y1 - ancho * 0.16f), Size(ancho, ancho * 0.32f))
        // Fibras.
        var y = y0 + 6 * d; while (y < y1) { drawLine(Color(0x1A5A4020), Offset(x0, y), Offset(x0 + ancho, y), 0.7f * d); y += 5 * d }
        // Las varillas (umbilici) con sus perillas.
        listOf(y0 - ancho * 0.16f - 10 * d, y1 + ancho * 0.1f).forEach { yy ->
            drawRoundRect(MADERA, Offset(w / 2 - 3 * d, yy), Size(6 * d, 14 * d), CornerRadius(3 * d))
            drawCircle(MADERA_OSCURA, 6 * d, Offset(w / 2, if (yy < y0) yy else yy + 14 * d))
        }
        // La cinta roja que lo ata y la etiqueta (titulus).
        drawRect(LACRE, Offset(x0 - 2 * d, h * 0.47f), Size(ancho + 4 * d, 5 * d))
        drawLine(LACRE, Offset(x0 + ancho, h * 0.49f), Offset(x0 + ancho + 14 * d, h * 0.58f), 2 * d)
        drawRoundRect(PERGAMINO, Offset(x0 + ancho + 6 * d, h * 0.57f), Size(26 * d, 14 * d), CornerRadius(2 * d))
    }

    /** Tablillas de cera atadas por el lomo: más tablillas cuanto más escrito. */
    private fun DrawScope.tablillasCerradas(grosor: Float, d: Float) {
        val w = size.width - grosor; val h = size.height - grosor
        sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 6 * d)
        // El canto: las tablillas apiladas.
        val n = (grosor / (2.4f * d)).toInt().coerceAtLeast(2)
        for (k in n downTo 1) { val o = k * grosor / (n + 1); drawRoundRect(lerp(MADERA, Color.Black, 0.15f + 0.1f * (k % 2)), Offset(o, o), Size(w, h), CornerRadius(5 * d)) }
        // La tapa de madera con su marco y la cera adentro.
        drawRoundRect(Brush.linearGradient(listOf(lerp(MADERA, Color.White, 0.12f), MADERA, MADERA_OSCURA), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(5 * d))
        val m = w * 0.1f
        drawRoundRect(lerp(CERA, Color.Black, 0.55f), Offset(m, m), Size(w - 2 * m, h - 2 * m), CornerRadius(2 * d))
        drawRoundRect(MADERA_OSCURA, Offset(m, m), Size(w - 2 * m, h - 2 * m), CornerRadius(2 * d), style = Stroke(2 * d))
        // Un meandro grabado en la cera.
        val u = (w - 2 * m) / 12; val yy = h * 0.22f
        val p = Path(); var x = m + u
        while (x + 3 * u <= w - m) { p.moveTo(x, yy + 2 * u); p.lineTo(x, yy); p.lineTo(x + 2 * u, yy); p.lineTo(x + 2 * u, yy + u); p.lineTo(x + u, yy + u); x += 3 * u }
        drawPath(p, CERA.copy(alpha = 0.7f), style = Stroke(1.2f * d))
        // Los cordones del lomo.
        listOf(0.3f, 0.7f).forEach { f -> drawLine(Color(0xFFCDB58A), Offset(-2 * d, h * f), Offset(m * 0.8f, h * f), 2.4f * d, StrokeCap.Round); drawCircle(MADERA_OSCURA, 2.2f * d, Offset(m * 0.6f, h * f)) }
    }

    /** Un libro encuadernado: tapa, canto de hojas, nervios en el lomo, esquineras y broches. */
    private fun DrawScope.libroCerrado(grosor: Float, d: Float, piel: Color, metal: Color, bandas: Boolean, broches: Boolean, roseta: Boolean = false) {
        val w = size.width - grosor; val h = size.height - grosor
        sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 6 * d)
        drawRoundRect(PERGAMINO, Offset(grosor, grosor), Size(w, h), CornerRadius(4 * d))
        val n = (grosor / (1.6f * d)).toInt().coerceAtLeast(2)
        for (k in 1..n) { val o = k * grosor / (n + 1); drawLine(Color(0x55806A40), Offset(w + o, grosor + 8 * d), Offset(w + o, h + o - 3 * d), 0.8f * d); drawLine(Color(0x55806A40), Offset(grosor + 8 * d, h + o), Offset(w + o - 3 * d, h + o), 0.8f * d) }
        drawRoundRect(Brush.linearGradient(listOf(lerp(piel, Color.White, 0.12f), piel, lerp(piel, Color.Black, 0.25f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(4 * d))
        // El marco gofrado.
        val m = w * 0.08f
        drawRect(metal.copy(alpha = 0.55f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1.4f * d))
        drawRect(metal.copy(alpha = 0.35f), Offset(m * 1.6f, m * 1.6f), Size(w - 3.2f * m, h - 3.2f * m), style = Stroke(0.8f * d))
        if (bandas) for (k in 1..4) { val y = h * k / 5f; drawRoundRect(lerp(piel, Color.Black, 0.35f), Offset(-1 * d, y - 3 * d), Size(10 * d, 6 * d), CornerRadius(2 * d)) }
        // Esquineras de metal.
        val e = w * 0.12f
        listOf(Offset(0f, 0f) to 0f, Offset(w, 0f) to 90f, Offset(w, h) to 180f, Offset(0f, h) to 270f).forEach { (o, a) ->
            val r = Math.toRadians(a.toDouble())
            fun pt(x: Float, y: Float) = Offset(o.x + (x * cos(r) - y * sin(r)).toFloat(), o.y + (x * sin(r) + y * cos(r)).toFloat())
            drawPath(Path().apply { moveTo(pt(0f, 0f).x, pt(0f, 0f).y); lineTo(pt(e, 0f).x, pt(e, 0f).y); lineTo(pt(0f, e).x, pt(0f, e).y); close() }, metal)
        }
        if (roseta) {
            // Medallón central: una roseta de ocho pétalos.
            val c = Offset(w / 2, h * 0.45f); val R = w * 0.18f
            drawCircle(metal, R, c, style = Stroke(1.6f * d))
            for (k in 0 until 8) { val a = k * PI / 4; drawOval(metal.copy(alpha = 0.85f), Offset(c.x + (R * 0.5f * cos(a)).toFloat() - R * 0.22f, c.y + (R * 0.5f * sin(a)).toFloat() - R * 0.22f), Size(R * 0.44f, R * 0.44f)) }
            drawCircle(lerp(metal, Color.White, 0.3f), R * 0.18f, c)
        } else {
            // Un rombo con una cruz de nudos en el centro.
            val c = Offset(w / 2, h * 0.45f); val R = w * 0.2f
            drawPath(Path().apply { moveTo(c.x, c.y - R); lineTo(c.x + R, c.y); lineTo(c.x, c.y + R); lineTo(c.x - R, c.y); close() }, metal.copy(alpha = 0.7f), style = Stroke(1.4f * d))
            drawCircle(metal.copy(alpha = 0.7f), R * 0.25f, c, style = Stroke(1.2f * d))
        }
        if (broches) listOf(0.32f, 0.68f).forEach { f ->
            drawRect(lerp(piel, Color.Black, 0.3f), Offset(w - 4 * d, h * f - 4 * d), Size(10 * d + grosor, 8 * d))
            drawRoundRect(metal, Offset(w - 2 * d, h * f - 5 * d), Size(9 * d, 10 * d), CornerRadius(2 * d))
        }
    }

    /**
     * El libro de horas cerrado: terciopelo azul con la roseta y los broches de oro, el canto dorado y las tres cintas de
     * seda (los registros) que asoman por abajo para marcar las horas.
     */
    private fun DrawScope.horasCerrado(grosor: Float, d: Float) {
        val w = size.width - grosor; val h = size.height - grosor
        libroCerrado(grosor, d, Color(0xFF1F2E6E), ORO, bandas = false, broches = true, roseta = true)
        // El canto dorado.
        drawRect(ORO.copy(alpha = 0.55f), Offset(w, grosor + 6 * d), Size(grosor, h - 6 * d))
        drawRect(ORO.copy(alpha = 0.45f), Offset(grosor + 6 * d, h), Size(w - 6 * d, grosor))
        // Los registros: tres cintas de seda con la punta cortada en V.
        listOf(0.36f to LACRE, 0.44f to ORO, 0.52f to AZUR).forEachIndexed { i, (f, col) ->
            val x = w * f; val largo = 16 * d + i * 5 * d; val ancho = 4 * d
            drawPath(Path().apply { moveTo(x, h - 2 * d); lineTo(x + ancho, h - 2 * d); lineTo(x + ancho, h + grosor + largo); lineTo(x + ancho / 2, h + grosor + largo - 3 * d); lineTo(x, h + grosor + largo); close() }, col)
        }
    }

    /** El cuaderno de taller: tapas de cartón con un cordón enrollado y figuras de construcción a sepia. */
    private fun DrawScope.tallerCerrado(grosor: Float, d: Float, oscuro: Boolean) {
        val w = size.width - grosor; val h = size.height - grosor
        val carton = Color(0xFFB8956A)
        sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 4 * d)
        drawRect(Color(0xFFEAD9B0), Offset(grosor, grosor), Size(w, h))
        for (k in 1..(grosor / (1.6f * d)).toInt().coerceAtLeast(2)) { val o = k * 1.6f * d; drawLine(Color(0x55806A40), Offset(w + o, grosor + 6 * d), Offset(w + o, h + o), 0.7f * d) }
        drawRect(Brush.linearGradient(listOf(lerp(carton, Color.White, 0.1f), carton, lerp(carton, Color.Black, 0.18f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h))
        val tinta = Color(0xFF5A3A1C).copy(alpha = 0.75f)
        // Construcción: círculo, cuadrado inscrito y diagonales, como en los cuadernos de Leonardo.
        val c = Offset(w / 2, h * 0.42f); val R = w * 0.27f
        drawCircle(tinta, R, c, style = Stroke(1f * d)); drawRect(tinta, Offset(c.x - R * 0.707f, c.y - R * 0.707f), Size(R * 1.414f, R * 1.414f), style = Stroke(1f * d))
        drawLine(tinta, Offset(c.x - R, c.y), Offset(c.x + R, c.y), 0.7f * d); drawLine(tinta, Offset(c.x, c.y - R), Offset(c.x, c.y + R), 0.7f * d)
        drawCircle(tinta, R * 0.5f, c, style = Stroke(0.7f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * d, 3 * d))))
        // El cordón que da vueltas y cierra el cuaderno.
        drawLine(Color(0xFF4A2A14), Offset(w * 0.62f, -2 * d), Offset(w * 0.62f, h + 2 * d), 2.2f * d)
        drawLine(Color(0xFF4A2A14), Offset(-2 * d, h * 0.66f), Offset(w + grosor, h * 0.66f), 2.2f * d)
        drawCircle(Color(0xFF4A2A14), 4 * d, Offset(w * 0.62f, h * 0.66f))
        if (oscuro) drawRect(Color.Black.copy(alpha = 0.12f), Offset.Zero, Size(w, h))
    }

    /**
     * La bitácora cerrada: tapas de cuero ennegrecido de brea con cantoneras de latón, la rosa de los vientos gofrada, una
     * mancha de agua de mar y el cabo de cáñamo que la ata en cruz y remata en un nudo con sus dos chicotes.
     */
    private fun DrawScope.bitacoraCerrada(grosor: Float, d: Float, oscuro: Boolean) {
        val w = size.width - grosor; val h = size.height - grosor
        sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 4 * d)
        // El canto de las hojas, onduladas por la humedad.
        drawRoundRect(PERGAMINO_HUMEDO, Offset(grosor, grosor), Size(w, h), CornerRadius(3 * d))
        val n = (grosor / (1.6f * d)).toInt().coerceAtLeast(2)
        for (k in 1..n) { val o = k * grosor / (n + 1); drawLine(Color(0x557A6A40), Offset(w + o, grosor + 6 * d), Offset(w + o, h + o - 3 * d), 0.8f * d) }
        drawRoundRect(Brush.linearGradient(listOf(lerp(CUERO_BREA, Color.White, 0.1f), CUERO_BREA, lerp(CUERO_BREA, Color.Black, 0.35f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(3 * d))
        // La mancha de agua de mar, que aclaró el cuero.
        val m = Offset(w * 0.28f, h * 0.78f)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFE6DCBA).copy(alpha = 0.12f), Color.Transparent), m, w * 0.24f), w * 0.24f, m)
        // La rosa de los vientos gofrada.
        val c = Offset(w * 0.5f, h * 0.36f); val R = w * 0.22f
        val gofrado = Color(0xFFC9A04A).copy(alpha = 0.55f)
        drawCircle(gofrado, R * 0.78f, c, style = Stroke(1f * d))
        for (k in 0 until 8) {
            val a = -PI / 2 + k * PI / 4; val largo = if (k % 2 == 0) R else R * 0.6f
            val punta = Offset(c.x + (largo * cos(a)).toFloat(), c.y + (largo * sin(a)).toFloat())
            val izq = Offset(c.x + (R * 0.14f * cos(a - PI / 2)).toFloat(), c.y + (R * 0.14f * sin(a - PI / 2)).toFloat())
            val der = Offset(c.x + (R * 0.14f * cos(a + PI / 2)).toFloat(), c.y + (R * 0.14f * sin(a + PI / 2)).toFloat())
            drawPath(Path().apply { moveTo(izq.x, izq.y); lineTo(punta.x, punta.y); lineTo(der.x, der.y); close() }, gofrado, style = Stroke(1f * d))
        }
        // Las cantoneras de latón.
        val e = w * 0.13f
        listOf(Offset(0f, 0f) to 0f, Offset(w, 0f) to 90f, Offset(w, h) to 180f, Offset(0f, h) to 270f).forEach { (o, a) ->
            val r = Math.toRadians(a.toDouble())
            fun pt(x: Float, y: Float) = Offset(o.x + (x * cos(r) - y * sin(r)).toFloat(), o.y + (x * sin(r) + y * cos(r)).toFloat())
            drawPath(Path().apply { moveTo(pt(0f, 0f).x, pt(0f, 0f).y); lineTo(pt(e, 0f).x, pt(e, 0f).y); lineTo(pt(0f, e).x, pt(0f, e).y); close() }, Color(0xFFC9A04A))
            drawCircle(Color(0xFF5A4520), 1.3f * d, pt(e * 0.3f, e * 0.3f))
        }
        // El cabo en cruz y el nudo con sus chicotes.
        val nudo = Offset(w * 0.5f, h * 0.68f)
        drawLine(lerp(CANAMO, Color.Black, 0.35f), Offset(w * 0.5f + 1.2f * d, -2 * d), Offset(w * 0.5f + 1.2f * d, h + grosor), 3.4f * d)
        drawLine(CANAMO, Offset(w * 0.5f, -2 * d), Offset(w * 0.5f, h + grosor), 3 * d)
        drawLine(lerp(CANAMO, Color.Black, 0.35f), Offset(-2 * d, h * 0.68f + 1.2f * d), Offset(w + grosor, h * 0.68f + 1.2f * d), 3.4f * d)
        drawLine(CANAMO, Offset(-2 * d, h * 0.68f), Offset(w + grosor, h * 0.68f), 3 * d)
        var t = 0f; while (t < w + grosor) { drawLine(lerp(CANAMO, Color.Black, 0.3f), Offset(t, h * 0.68f - 1.4f * d), Offset(t + 2 * d, h * 0.68f + 1.4f * d), 0.6f * d); t += 3 * d }
        drawCircle(CANAMO, 5 * d, nudo); drawCircle(lerp(CANAMO, Color.Black, 0.3f), 5 * d, nudo, style = Stroke(1 * d))
        listOf(-1f, 1f).forEach { s -> drawLine(CANAMO, nudo, nudo + Offset(s * 9 * d, 16 * d), 2.4f * d, StrokeCap.Round) }
        if (oscuro) drawRect(Color.Black.copy(alpha = 0.1f), Offset.Zero, Size(w, h))
    }

    /** El legajo: carpeta atada con cordón carmesí y un sello de lacre con su corona de laurel. */
    private fun DrawScope.legajoCerrado(grosor: Float, d: Float, tapa: Color) {
        val w = size.width - grosor; val h = size.height - grosor
        sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 3 * d)
        val n = (grosor / (1.8f * d)).toInt().coerceAtLeast(2)
        for (k in n downTo 1) { val o = k * grosor / (n + 1); drawRect(lerp(Color(0xFFF3EEDF), Color.Black, 0.04f * (k % 3)), Offset(o, o), Size(w, h)) }
        drawRect(Brush.linearGradient(listOf(lerp(tapa, Color.White, 0.12f), tapa, lerp(tapa, Color.Black, 0.25f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h))
        // Filete dorado.
        drawRect(ORO.copy(alpha = 0.8f), Offset(w * 0.06f, w * 0.06f), Size(w * 0.88f, h - w * 0.12f), style = Stroke(1.2f * d))
        // El cordón, en cruz.
        drawLine(LACRE, Offset(w / 2, -2 * d), Offset(w / 2, h + grosor), 3 * d); drawLine(LACRE, Offset(-2 * d, h * 0.55f), Offset(w + grosor, h * 0.55f), 3 * d)
        // El sello de lacre con la corona de laurel.
        val c = Offset(w / 2, h * 0.55f); val R = w * 0.13f
        drawCircle(lerp(LACRE, Color.Black, 0.1f), R, c); drawCircle(lerp(LACRE, Color.White, 0.18f), R * 0.82f, c, style = Stroke(1.2f * d))
        laurel(c, R * 0.6f, ORO, d)
    }

    /** El cahier: tapa de papel con su etiqueta, el lomo cosido con hilo y una escarapela tricolor. */
    private fun DrawScope.cahierCerrado(grosor: Float, d: Float) {
        val w = size.width - grosor; val h = size.height - grosor
        val tapa = Color(0xFFB9C7D6)
        sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 3 * d)
        drawRect(Color(0xFFF8F6EE), Offset(grosor, grosor), Size(w, h))
        for (k in 1..(grosor / (1.6f * d)).toInt().coerceAtLeast(2)) { val o = k * 1.6f * d; drawLine(Color(0x55606A80), Offset(w + o, grosor + 6 * d), Offset(w + o, h + o), 0.7f * d) }
        drawRect(Brush.linearGradient(listOf(lerp(tapa, Color.White, 0.2f), tapa, lerp(tapa, Color.Black, 0.12f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h))
        // El lomo cosido.
        drawRect(Color(0xFF1F3A6E), Offset(0f, 0f), Size(w * 0.07f, h))
        var y = h * 0.08f; while (y < h * 0.95f) { drawLine(Color(0xFFF8F6EE), Offset(w * 0.035f, y), Offset(w * 0.035f, y + 6 * d), 1.4f * d); y += 14 * d }
        // La etiqueta con marco.
        drawRect(Color(0xFFF8F6EE), Offset(w * 0.2f, h * 0.2f), Size(w * 0.62f, h * 0.24f))
        drawRect(Color(0xFF1F3A6E), Offset(w * 0.2f + 3 * d, h * 0.2f + 3 * d), Size(w * 0.62f - 6 * d, h * 0.24f - 6 * d), style = Stroke(1 * d))
        // La escarapela: azul, blanco y rojo.
        escarapela(Offset(w * 0.78f, h * 0.8f), w * 0.09f)
    }

    fun DrawScope.escarapela(c: Offset, r: Float) {
        drawCircle(Color(0xFFB22234), r, c); drawCircle(Color.White, r * 0.68f, c); drawCircle(Color(0xFF1F3A6E), r * 0.38f, c)
        for (k in 0 until 16) { val a = k * PI / 8; drawLine(Color.Black.copy(alpha = 0.12f), Offset(c.x + (r * 0.4f * cos(a)).toFloat(), c.y + (r * 0.4f * sin(a)).toFloat()), Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()), r * 0.04f) }
    }

    /** Una corona de laurel: dos ramas de hojas que casi se cierran arriba. */
    fun DrawScope.laurel(c: Offset, r: Float, color: Color, d: Float) {
        for (lado in listOf(-1f, 1f)) {
            for (k in 0 until 7) {
                val a = (PI / 2 + lado * (0.35 + k * 0.36)).toFloat()
                val o = Offset(c.x + r * cos(a), c.y + r * sin(a))
                val giro = a + lado * 1.2f
                val l = r * 0.36f; val ww = r * 0.14f
                val p = Path().apply {
                    moveTo(o.x - l / 2 * cos(giro), o.y - l / 2 * sin(giro))
                    quadraticTo(o.x - ww * sin(giro), o.y + ww * cos(giro), o.x + l / 2 * cos(giro), o.y + l / 2 * sin(giro))
                    quadraticTo(o.x + ww * sin(giro), o.y - ww * cos(giro), o.x - l / 2 * cos(giro), o.y - l / 2 * sin(giro)); close()
                }
                drawPath(p, color)
            }
        }
        drawCircle(color, r * 0.07f, Offset(c.x, c.y + r))
    }

    // ================================================================== abierto: el fondo de la hoja

    /** Lo que va detrás del texto de una hoja abierta: renglones, fibras, cera, reglado o cuadrícula. */
    fun DrawScope.fondoHoja(c: Contenedor, cs: ColorScheme, oscuro: Boolean, renglon: Float, margen: Float) {
        val d = density
        val tinta = if (oscuro) Color.White else Color.Black
        when (c) {
            Contenedor.ROLLO -> {
                // Fibras de papiro: horizontales y verticales, como las dos capas de tiras.
                var y = 0f; var k = 0
                while (y < size.height) { drawLine(Color(0xFF6E5530).copy(alpha = if (k % 3 == 0) 0.10f else 0.05f), Offset(0f, y), Offset(size.width, y), 0.8f * d); y += 4.5f * d; k++ }
                var x = 0f; while (x < size.width) { drawLine(Color(0xFF6E5530).copy(alpha = 0.04f), Offset(x, 0f), Offset(x, size.height), 1.4f * d); x += 9 * d }
                // Los bordes de la columna de texto.
                drawLine(Color(0xFF6E5530).copy(alpha = 0.25f), Offset(margen - 8 * d, 0f), Offset(margen - 8 * d, size.height), 0.8f * d)
            }
            Contenedor.TABLILLA -> {
                // Marco de madera y la cera con rasguños del estilo.
                val m = 10 * d
                drawRect(MADERA, Offset.Zero, Size(size.width, m)); drawRect(MADERA, Offset(0f, size.height - m), Size(size.width, m))
                drawRect(MADERA, Offset(size.width - m, 0f), Size(m, size.height))
                var y = 96 * d; while (y < size.height - m) { drawLine(tinta.copy(alpha = 0.06f), Offset(margen, y), Offset(size.width - m, y), 0.8f * d); y += renglon }
            }
            Contenedor.CODICE -> {
                // Los pinchazos del margen y el reglado a punta seca, con las dos líneas de justificación a cada lado.
                val regla = Color(0xFF6E5530).copy(alpha = if (oscuro) 0.2f else 0.12f)
                var y = 96 * d; while (y < size.height) { drawLine(regla, Offset(margen - 10 * d, y), Offset(size.width - 18 * d, y), 0.7f * d); drawCircle(Color(0xFF6E5530).copy(alpha = 0.3f), 1 * d, Offset(size.width - 6 * d, y)); y += renglon }
                listOf(margen - 10 * d, margen - 14 * d, size.width - 18 * d, size.width - 14 * d).forEach { x -> drawLine(regla, Offset(x, 0f), Offset(x, size.height), 0.7f * d) }
            }
            Contenedor.HORAS -> {
                // Reglado en tinta roja, la barra de oro y azul del margen interior y la cenefa de hiedra del exterior.
                val regla = Color(0xFFA3172E).copy(alpha = 0.22f)
                var y = 96 * d; while (y < size.height) { drawLine(regla, Offset(margen - 6 * d, y), Offset(size.width - 22 * d, y), 0.7f * d); y += renglon }
                drawRect(ORO.copy(alpha = 0.7f), Offset(margen - 16 * d, 60 * d), Size(3 * d, size.height - 100 * d))
                drawRect(AZUR.copy(alpha = 0.6f), Offset(margen - 12 * d, 60 * d), Size(2 * d, size.height - 100 * d))
                // La cenefa: un tallo que ondula con hojas de hiedra de oro y bayas azules a lo largo del margen exterior.
                val x = size.width - 10 * d
                val tallo = Path().apply { moveTo(x, 20 * d); var yy = 20 * d; var s = 1f; while (yy < size.height - 30 * d) { quadraticTo(x + s * 6 * d, yy + 11 * d, x, yy + 22 * d); yy += 22 * d; s = -s } }
                drawPath(tallo, ORO_OSCURO.copy(alpha = 0.6f), style = Stroke(1.1f * d))
                var yy = 31 * d; var i = 0
                while (yy < size.height - 30 * d) {
                    val c = Offset(x + if (i % 2 == 0) -5 * d else 5 * d, yy)
                    if (i % 2 == 0) drawPath(Path().apply { moveTo(c.x, c.y - 4 * d); lineTo(c.x + 3.5f * d, c.y); lineTo(c.x, c.y + 4 * d); lineTo(c.x - 3.5f * d, c.y); close() }, ORO.copy(alpha = 0.85f))
                    else drawCircle(AZUR.copy(alpha = 0.7f), 2.4f * d, c)
                    yy += 22 * d; i++
                }
            }
            Contenedor.TALLER -> {
                // Trazos de construcción tenues: arcos y una retícula ligera, como en un cuaderno de estudio.
                val t = Color(0xFF5A3A1C).copy(alpha = if (oscuro) 0.16f else 0.08f)
                drawCircle(t, size.width * 0.32f, Offset(size.width * 0.85f, size.height * 0.15f), style = Stroke(0.8f * d))
                drawCircle(t, size.width * 0.18f, Offset(size.width * 0.85f, size.height * 0.15f), style = Stroke(0.6f * d))
                drawLine(t, Offset(size.width * 0.4f, 0f), Offset(size.width, size.height * 0.4f), 0.6f * d)
                var y = 96 * d; while (y < size.height) { drawLine(t, Offset(margen, y), Offset(size.width, y), 0.5f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 6 * d))); y += renglon }
            }
            Contenedor.SINGLADURAS -> {
                // La hoja de la bitácora: el encabezado con sus columnas (hora, nudos, rumbo, viento), el renglón de cada
                // asiento, el margen doble en rojo y una mancha de agua de mar en una esquina.
                val tintaB = Color(0xFF2A1C0F).copy(alpha = if (oscuro) 0.22f else 0.13f)
                val rojo = Color(0xFF9A2A1C).copy(alpha = if (oscuro) 0.4f else 0.35f)
                drawLine(tintaB, Offset(0f, 84 * d), Offset(size.width, 84 * d), 1.2f * d)
                listOf(0.58f, 0.68f, 0.8f, 0.9f).forEach { f -> drawLine(tintaB, Offset(size.width * f, 70 * d), Offset(size.width * f, 84 * d), 0.8f * d) }
                var y = 96 * d; while (y < size.height) { drawLine(tintaB, Offset(margen - 6 * d, y), Offset(size.width - 8 * d, y), 0.6f * d); y += renglon }
                listOf(margen - 10 * d, margen - 13 * d).forEach { x -> drawLine(rojo, Offset(x, 0f), Offset(x, size.height), 0.8f * d) }
                val m = Offset(size.width * 0.82f, size.height * 0.88f); val rm = size.width * 0.2f
                drawCircle(Brush.radialGradient(listOf(Color.Transparent, Color(0xFF7A6A40).copy(alpha = if (oscuro) 0.12f else 0.09f), Color.Transparent), m, rm), rm, m)
            }
            Contenedor.LEGAJO -> {
                // Membrete con doble filete y margen ancho a la izquierda.
                drawLine(cs.primary.copy(alpha = 0.6f), Offset(margen, 84 * d), Offset(size.width - 12 * d, 84 * d), 1.4f * d)
                drawLine(cs.primary.copy(alpha = 0.3f), Offset(margen, 88 * d), Offset(size.width - 12 * d, 88 * d), 0.6f * d)
                drawLine(cs.primary.copy(alpha = 0.18f), Offset(margen, 0f), Offset(margen, size.height), 0.8f * d)
            }
            Contenedor.CAHIER -> {
                // Cuadrícula Seyès: líneas finas cada cuarto y gruesas cada renglón, con el margen rojo.
                val malva = Color(0xFF8E7CC3)
                val paso = renglon / 4
                var y = 96 * d; var k = 0
                while (y < size.height) { drawLine(malva.copy(alpha = if (k % 4 == 0) 0.35f else 0.14f), Offset(0f, y), Offset(size.width, y), if (k % 4 == 0) 0.9f * d else 0.5f * d); y += paso; k++ }
                var x = margen; while (x < size.width) { drawLine(malva.copy(alpha = 0.14f), Offset(x, 96 * d), Offset(x, size.height), 0.5f * d); x += renglon }
                drawLine(Color(0xFFB22234).copy(alpha = 0.5f), Offset(margen - 6 * d, 0f), Offset(margen - 6 * d, size.height), 1.2f * d)
            }
            Contenedor.MANUSCRITO, Contenedor.ACORDEON, Contenedor.ORDENES, Contenedor.BITACORA, Contenedor.HERBARIO, Contenedor.ADVIENTO, Contenedor.MAPAS,
            Contenedor.EXPEDIENTE, Contenedor.CONSOLA, Contenedor.CARPETA, Contenedor.TECNICO, Contenedor.CUADERNO, Contenedor.VUELO, Contenedor.TETRAD -> with(ContenedoresMas) { fondoHojaMas(c, cs, oscuro, renglon, margen) }
        }
    }

    // ================================================================== abierto: la encuadernación a la izquierda

    /** Lo que se ve encima del borde izquierdo de la hoja abierta: varilla, cordones, costura o cinta. */
    fun DrawScope.encuadernacion(c: Contenedor, cs: ColorScheme, oscuro: Boolean) {
        val d = density
        when (c) {
            Contenedor.ROLLO -> {
                // La parte enrollada a la izquierda, como si la columna se desenrollara hacia la izquierda.
                drawRect(Brush.horizontalGradient(listOf(lerp(PAPIRO, Color.Black, 0.35f), PAPIRO, lerp(PAPIRO, Color.Black, 0.3f)), 4 * d, 30 * d), Offset(4 * d, 6 * d), Size(26 * d, size.height - 12 * d))
                drawRoundRect(MADERA, Offset(13 * d, 0f), Size(8 * d, 10 * d), CornerRadius(3 * d)); drawRoundRect(MADERA, Offset(13 * d, size.height - 10 * d), Size(8 * d, 10 * d), CornerRadius(3 * d))
                if (oscuro) drawRect(Color.Black.copy(alpha = 0.55f), Offset(4 * d, 6 * d), Size(26 * d, size.height - 12 * d))
            }
            Contenedor.TABLILLA -> {
                // El borde de madera izquierdo con los agujeros y cordones que unen las tablillas.
                drawRect(MADERA, Offset(10 * d, 4 * d), Size(18 * d, size.height - 8 * d))
                listOf(0.25f, 0.75f).forEach { f -> val y = size.height * f; drawCircle(MADERA_OSCURA, 3.4f * d, Offset(19 * d, y)); drawLine(Color(0xFFCDB58A), Offset(0f, y), Offset(19 * d, y), 2.4f * d) }
            }
            Contenedor.CODICE -> {
                // El pliegue del cuadernillo: sombra del lomo y las puntadas.
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), 10 * d, 34 * d), Offset(10 * d, 4 * d), Size(24 * d, size.height - 8 * d))
                var y = 30 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFF8A6A44), Offset(16 * d, y), Offset(16 * d, y + 10 * d), 1.6f * d); y += 48 * d }
            }
            Contenedor.HORAS -> {
                // El lomo de terciopelo azul con su cadenilla de oro y el registro de seda roja que cruza la esquina.
                val terciopelo = if (oscuro) Color(0xFF141E4A) else Color(0xFF1F2E6E)
                drawRect(Brush.horizontalGradient(listOf(lerp(terciopelo, Color.White, 0.12f), terciopelo, lerp(terciopelo, Color.Black, 0.3f)), 6 * d, 24 * d), Offset(6 * d, 0f), Size(18 * d, size.height))
                var y = 12 * d; var i = 0; while (y < size.height - 8 * d) { drawCircle(ORO.copy(alpha = if (i % 2 == 0) 0.9f else 0.6f), (if (i % 2 == 0) 2.2f else 1.4f) * d, Offset(15 * d, y)); y += 9 * d; i++ }
                drawPath(Path().apply { moveTo(20 * d, 0f); lineTo(26 * d, 0f); lineTo(70 * d, 60 * d); lineTo(64 * d, 64 * d); close() }, LACRE.copy(alpha = 0.85f))
            }
            Contenedor.TALLER -> {
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), 10 * d, 30 * d), Offset(10 * d, 4 * d), Size(20 * d, size.height - 8 * d))
                var y = 26 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFF4A2A14), Offset(12 * d, y), Offset(22 * d, y + 6 * d), 1.4f * d); y += 40 * d }
            }
            Contenedor.SINGLADURAS -> {
                // El lomo cosido con cabo: ojales de latón y el cabo de cáñamo que entra y sale de ellos en diagonal.
                drawRect(Brush.horizontalGradient(listOf(lerp(CUERO_BREA, Color.Black, if (oscuro) 0.4f else 0f), CUERO_BREA, Color.Transparent), 4 * d, 30 * d), Offset(4 * d, 0f), Size(26 * d, size.height))
                var y = 18 * d; var k = 0
                while (y < size.height - 18 * d) {
                    drawCircle(Color(0xFFC9A04A), 3 * d, Offset(17 * d, y), style = Stroke(1.4f * d))
                    drawLine(CANAMO, Offset(17 * d, y), Offset(if (k % 2 == 0) 4 * d else 30 * d, y + 15 * d), 2.4f * d, StrokeCap.Round)
                    y += 30 * d; k++
                }
            }
            Contenedor.LEGAJO -> {
                // La cinta carmesí que pasa por los ojales.
                listOf(0.22f, 0.78f).forEach { f -> val y = size.height * f; drawCircle(ORO_OSCURO, 4 * d, Offset(20 * d, y), style = Stroke(1.6f * d)); drawLine(LACRE, Offset(0f, y), Offset(20 * d, y), 3 * d) }
                drawLine(LACRE, Offset(4 * d, size.height * 0.22f), Offset(4 * d, size.height * 0.78f), 3 * d)
            }
            Contenedor.CAHIER -> {
                // El hilo de la costura del cuaderno.
                var y = 20 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFF1F3A6E).copy(alpha = 0.7f), Offset(16 * d, y), Offset(16 * d, y + 8 * d), 1.4f * d); y += 22 * d }
            }
            Contenedor.MANUSCRITO, Contenedor.ACORDEON, Contenedor.ORDENES, Contenedor.BITACORA, Contenedor.HERBARIO, Contenedor.ADVIENTO, Contenedor.MAPAS,
            Contenedor.EXPEDIENTE, Contenedor.CONSOLA, Contenedor.CARPETA, Contenedor.TECNICO, Contenedor.CUADERNO, Contenedor.VUELO, Contenedor.TETRAD -> with(ContenedoresMas) { encuadernacionMas(c, cs, oscuro) }
        }
    }
}
