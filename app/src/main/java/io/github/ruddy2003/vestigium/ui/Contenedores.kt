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
import androidx.compose.ui.graphics.drawscope.rotate
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
    BOLETOS("Cuaderno de boletos", "página"),
    TABLILLA("Tablillas de momentos", "tablilla"),
    ESCRIBA("Papiro del escriba", "columna"),
    CODICE("Códice de momentos", "folio"),
    REGLA("Regla de la casa", "artículo"),
    HORAS("Libro de horas", "folio"),
    RECORTES("Diario de recortes", "recorte"),
    TALLER("Cuaderno de taller", "hoja"),
    SINGLADURAS("Bitácora de a bordo", "singladura"),
    LEGAJO("Legajo de despachos", "despacho"),
    LACA("Libreta de laca", "página del carnet"),
    CAHIER("Cahier de momentos", "página"),
    MANUSCRITO("Manuscrito de momentos", "folio"),
    BIOMBO("Códice en biombo", "página pintada"),
    ACORDEON("Libro plegado de momentos", "pliegue"),
    FUKURO("Cuaderno cosido de momentos", "hoja doblada"),
    ORDENES("Rollo de órdenes", "mandato"),
    BITACORA("Cuaderno del pueblo", "página"),
    HERBARIO("Herbario de momentos", "pliego"),
    SUPERVIVIENTE("Cuaderno del superviviente", "hoja prensada"),
    ADVIENTO("Calendario de adviento", "casilla"),
    MAPAS("Libro de mapas", "mapa"),
    BOLSA("Bolsa de piel", "piel"),
    EXPEDIENTE("Expediente de momentos", "folio"),
    CONSOLA("Consola de registro", "entrada"),
    TICKETS("Pincho de tickets", "ticket"),
    CARPETA("Carpeta del supervisor", "informe"),
    TECNICO("Códice técnico", "lámina"),
    PATENTES("Cuaderno de patentes", "patente"),
    CUADERNO("Cuaderno de laboratorio", "experimento"),
    VUELO("Bitácora de vuelo", "órbita"),
    INMERSION("Bitácora del batiscafo", "inmersión"),
    TETRAD("Cuaderno escolar", "hoja"),
    /** Básico: una libreta lisa, sin dibujo propio (el papel es la superficie del esquema). */
    SIMPLE("Libreta de momentos", "página"),
}

fun contenedorDe(t: TemaId): Contenedor = when (t) {
    TemaId.ROMANO -> Contenedor.ROLLO
    TemaId.URBE -> Contenedor.BOLETOS
    TemaId.HELENO -> Contenedor.TABLILLA
    TemaId.EGIPTO -> Contenedor.ESCRIBA
    TemaId.MEDIEVAL -> Contenedor.CODICE
    TemaId.TEMPLARIO -> Contenedor.REGLA
    TemaId.GOTICO -> Contenedor.HORAS
    TemaId.ARCANO -> Contenedor.RECORTES
    TemaId.RENACIMIENTO -> Contenedor.TALLER
    TemaId.PIRATA -> Contenedor.SINGLADURAS
    TemaId.NAPOLEON -> Contenedor.LEGAJO
    TemaId.DECO -> Contenedor.LACA
    TemaId.REVOLUCION -> Contenedor.CAHIER
    TemaId.ANDALUS -> Contenedor.MANUSCRITO
    TemaId.MESO -> Contenedor.BIOMBO
    TemaId.SEDA -> Contenedor.ACORDEON
    TemaId.EDO -> Contenedor.FUKURO
    TemaId.MONGOL -> Contenedor.ORDENES
    TemaId.COLONOS -> Contenedor.BITACORA
    TemaId.BOTANICO -> Contenedor.HERBARIO
    TemaId.RUINAS -> Contenedor.SUPERVIVIENTE
    TemaId.NAVIDAD -> Contenedor.ADVIENTO
    TemaId.ANTIGUAS -> Contenedor.MAPAS
    TemaId.CUEVA -> Contenedor.BOLSA
    TemaId.BASICO -> Contenedor.SIMPLE
    TemaId.DISTOPIA -> Contenedor.EXPEDIENTE
    TemaId.CIBER -> Contenedor.CONSOLA
    TemaId.NEON -> Contenedor.TICKETS
    TemaId.REFUGIO -> Contenedor.CARPETA
    TemaId.ORDEN -> Contenedor.TECNICO
    TemaId.VAPOR -> Contenedor.PATENTES
    TemaId.LABORATORIO -> Contenedor.CUADERNO
    TemaId.ESPACIO -> Contenedor.VUELO
    TemaId.ABISMO -> Contenedor.INMERSION
    TemaId.URSS -> Contenedor.TETRAD
}

object Contenedores {
    private val MADERA = Color(0xFF7A5232)
    private val MADERA_OSCURA = Color(0xFF4A3020)
    private val PAPIRO = Color(0xFFE9D9A9)
    private val CERA = Color(0xFFE2C482)
    /** El papiro egipcio, más amarillo y verdoso que el del rollo romano; el barro del Nilo del sello, el hilo de lino, el lapislázuli y la cornalina. */
    private val PAPIRO_NILO = Color(0xFFE8D59C)
    private val BARRO = Color(0xFF6E4A2E)
    private val LINO = Color(0xFFCDBB8E)
    private val LAPIS = Color(0xFF1D2F7A)
    private val CORNALINA = Color(0xFFB8471E)
    private val PERGAMINO = Color(0xFFF1E6C8)
    /** El códice maya: la tira de amate cubierta de estuco blanco, el cinabrio de los renglones, la piel de jaguar de las tapas y el henequén. */
    private val ESTUCO = Color(0xFFF2E8D2)
    private val CINABRIO = Color(0xFFA3301E)
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
    /** El papel del diario de recortes: amarillento, de libreta barata, con manchas de agua de mar. */
    private val LIBRETA = Color(0xFFE8DDC0)
    private val TELA_VERDE = Color(0xFF2E4A3E)
    private val PERIODICO = Color(0xFFD9D2BC)

    /** El color del papel de una hoja abierta (de día y de noche). */
    fun papel(c: Contenedor, cs: ColorScheme, oscuro: Boolean): Color = when (c) {
        Contenedor.ROLLO -> if (oscuro) lerp(PAPIRO, Color.Black, 0.72f) else PAPIRO
        Contenedor.TABLILLA -> if (oscuro) lerp(CERA, Color.Black, 0.75f) else CERA
        Contenedor.ESCRIBA -> if (oscuro) lerp(PAPIRO_NILO, Color.Black, 0.74f) else PAPIRO_NILO
        Contenedor.CODICE -> if (oscuro) lerp(PERGAMINO, Color.Black, 0.76f) else PERGAMINO
        Contenedor.REGLA -> ContenedoresMas.papel(c, cs, oscuro)
        Contenedor.HORAS -> if (oscuro) lerp(VITELA, Color.Black, 0.8f) else VITELA
        Contenedor.RECORTES -> if (oscuro) lerp(LIBRETA, Color(0xFF07100D), 0.8f) else LIBRETA
        Contenedor.TALLER -> if (oscuro) lerp(Color(0xFFEAD9B0), Color.Black, 0.74f) else Color(0xFFEAD9B0)
        Contenedor.SINGLADURAS -> if (oscuro) lerp(PERGAMINO_HUMEDO, Color.Black, 0.76f) else PERGAMINO_HUMEDO
        Contenedor.LEGAJO -> if (oscuro) lerp(Color(0xFFF3EEDF), Color.Black, 0.78f) else Color(0xFFF7F2E4)
        Contenedor.LACA -> ContenedoresMas.papel(c, cs, oscuro)
        Contenedor.CAHIER -> if (oscuro) lerp(Color(0xFFF8F6EE), Color.Black, 0.8f) else Color(0xFFF8F6EE)
        Contenedor.BIOMBO -> if (oscuro) lerp(ESTUCO, Color.Black, 0.78f) else ESTUCO
        // Las libretas del lote 2 tienen su papel en [ContenedoresMas].
        Contenedor.MANUSCRITO, Contenedor.ACORDEON, Contenedor.FUKURO, Contenedor.ORDENES, Contenedor.BITACORA, Contenedor.HERBARIO, Contenedor.ADVIENTO, Contenedor.MAPAS,
            Contenedor.EXPEDIENTE, Contenedor.CONSOLA, Contenedor.CARPETA, Contenedor.TECNICO, Contenedor.CUADERNO, Contenedor.VUELO, Contenedor.INMERSION, Contenedor.TETRAD, Contenedor.BOLETOS -> ContenedoresMas.papel(c, cs, oscuro)
        Contenedor.BOLSA -> ContenedoresMas.papel(c, cs, oscuro)
        Contenedor.SUPERVIVIENTE -> ContenedoresMas.papel(c, cs, oscuro)
        Contenedor.TICKETS -> ContenedoresMas.papel(c, cs, oscuro)
        Contenedor.PATENTES -> ContenedoresMas.papel(c, cs, oscuro)
        Contenedor.SIMPLE -> cs.surface
    }

    private fun DrawScope.sombra(o: Offset, s: Size, r: Float) = drawRoundRect(Color.Black.copy(alpha = 0.18f), o, s, CornerRadius(r))

    /**
     * El signo del tema (invención propia): un heptágono que no cierra, con tres trazos que lo cruzan, y un ojo en el
     * centro con la pupila vertical. Lo usan la libreta, el emblema y los sellos.
     */
    fun DrawScope.signoOjo(c: Offset, r: Float, color: Color, d: Float, abierto: Float = 1f) {
        val p = Path()
        for (k in 0..6) {
            val a = -PI / 2 + k * 2 * PI / 7
            val q = Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat())
            if (k == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
        }
        drawPath(p, color, style = Stroke(1.2f * d))
        // Tres trazos que se cruzan fuera del heptágono, como ganchos.
        listOf(0, 2, 5).forEach { k ->
            val a = -PI / 2 + k * 2 * PI / 7
            val q = Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat())
            val q2 = Offset(c.x + (r * 1.35f * cos(a + 0.25)).toFloat(), c.y + (r * 1.35f * sin(a + 0.25)).toFloat())
            drawLine(color, q, q2, 1.1f * d)
        }
        // El ojo.
        val ao = r * 0.62f; val alto = r * 0.34f * abierto
        val ojo = Path().apply { moveTo(c.x - ao, c.y); quadraticTo(c.x, c.y - alto * 2, c.x + ao, c.y); quadraticTo(c.x, c.y + alto * 2, c.x - ao, c.y); close() }
        drawPath(ojo, color, style = Stroke(1.1f * d))
        if (abierto > 0.2f) {
            drawCircle(color, alto * 0.95f, c, style = Stroke(0.9f * d))
            drawOval(color, Offset(c.x - alto * 0.22f, c.y - alto * 0.8f), Size(alto * 0.44f, alto * 1.6f))
        }
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
            Contenedor.ESCRIBA -> {
                // Fibras en dos capas, las uniones de las hojas pegadas que forman el rollo, la cenefa de colores arriba y,
                // en el margen, el punto rojo con que los escribas marcaban cada verso.
                val fibra = Color(0xFF6E5530)
                var y = 0f; var k = 0
                while (y < size.height) { drawLine(fibra.copy(alpha = if (k % 4 == 0) 0.09f else 0.04f), Offset(0f, y), Offset(size.width, y), 0.7f * d); y += 3.5f * d; k++ }
                var x = 140 * d; while (x < size.width) { drawRect(fibra.copy(alpha = 0.07f), Offset(x, 0f), Size(5 * d, size.height)); x += 140 * d }
                val colores = listOf(LAPIS, ORO, CORNALINA, ORO)
                var xc = 0f; var j = 0
                while (xc < size.width) { drawRect(colores[j % 4].copy(alpha = if (oscuro) 0.5f else 0.6f), Offset(xc, 70 * d), Size(minOf(7 * d, size.width - xc), 4 * d)); xc += 9 * d; j++ }
                var yy = 96 * d; while (yy < size.height) { drawCircle(CORNALINA.copy(alpha = if (oscuro) 0.6f else 0.5f), 1.6f * d, Offset(margen - 12 * d, yy - 4 * d)); yy += maxOf(renglon, 1f) }
            }
            Contenedor.BIOMBO -> {
                // La página del códice: el estuco con alguna grieta, el marco rojo y los renglones rojos que la parten en
                // registros; en el margen, la cuenta de cada renglón en puntos y barras, en rojo y negro como los escribas.
                val rojo = CINABRIO.copy(alpha = if (oscuro) 0.55f else 0.45f)
                val m = 6 * d
                drawRect(rojo, Offset(m, m), Size(size.width - 2 * m, size.height - 2 * m), style = Stroke(1.2f * d))
                val azar = java.util.Random(13)
                repeat(6) { val x = azar.nextFloat() * size.width; val y = azar.nextFloat() * size.height
                    drawLine(tinta.copy(alpha = 0.06f), Offset(x, y), Offset(x + (azar.nextFloat() - 0.5f) * 30 * d, y + azar.nextFloat() * 18 * d), 0.6f * d) }
                var y = 96 * d; var k = 1
                while (y < size.height - m) {
                    if (k % 4 == 0) drawLine(rojo, Offset(m, y + 3 * d), Offset(size.width - m, y + 3 * d), 1.1f * d)
                    numeralMaya(k, Offset(margen - 12 * d, y - 6 * d), 9 * d, if (k % 2 == 0) rojo else tinta.copy(alpha = if (oscuro) 0.4f else 0.35f))
                    y += maxOf(renglon, 1f); k++
                }
            }
            Contenedor.CODICE -> {
                // Los pinchazos del margen y el reglado a punta seca, con las dos líneas de justificación a cada lado.
                val regla = Color(0xFF6E5530).copy(alpha = if (oscuro) 0.2f else 0.12f)
                var y = 96 * d; while (y < size.height) { drawLine(regla, Offset(margen - 10 * d, y), Offset(size.width - 18 * d, y), 0.7f * d); drawCircle(Color(0xFF6E5530).copy(alpha = 0.3f), 1 * d, Offset(size.width - 6 * d, y)); y += renglon }
                listOf(margen - 10 * d, margen - 14 * d, size.width - 18 * d, size.width - 14 * d).forEach { x -> drawLine(regla, Offset(x, 0f), Offset(x, size.height), 0.7f * d) }
            }
            Contenedor.REGLA -> with(ContenedoresMas) { fondoHojaMas(c, cs, oscuro, renglon, margen) }
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
            Contenedor.RECORTES -> {
                // Renglones azul gris de libreta barata, el margen rojo desvaído y, pegado en la esquina, un recorte de
                // periódico torcido con sus dos tiras de cinta; abajo, el cerco de una taza.
                val regla = Color(0xFF5A6E7A).copy(alpha = if (oscuro) 0.22f else 0.16f)
                var y = 96 * d; while (y < size.height) { drawLine(regla, Offset(0f, y), Offset(size.width, y), 0.7f * d); y += maxOf(renglon, 1f) }
                drawLine(Color(0xFF9A3A2E).copy(alpha = 0.35f), Offset(margen - 8 * d, 0f), Offset(margen - 8 * d, size.height), 1 * d)
                val esq = Offset(size.width - 92 * d, 18 * d)
                rotate(4f, esq + Offset(40 * d, 26 * d)) {
                    drawRect(PERIODICO.copy(alpha = if (oscuro) 0.16f else 0.55f), esq, Size(80 * d, 54 * d))
                    drawRect(tinta.copy(alpha = 0.12f), esq + Offset(6 * d, 5 * d), Size(68 * d, 6 * d))
                    for (col in 0..1) { var yy = esq.y + 16 * d; while (yy < esq.y + 50 * d) { drawLine(tinta.copy(alpha = 0.12f), Offset(esq.x + 6 * d + col * 36 * d, yy), Offset(esq.x + 38 * d + col * 36 * d, yy), 0.8f * d); yy += 3.2f * d } }
                    listOf(esq + Offset(-6 * d, -3 * d), esq + Offset(64 * d, 46 * d)).forEach { o -> rotate(-35f, o + Offset(11 * d, 4 * d)) { drawRect(Color(0xFFE9E1B0).copy(alpha = if (oscuro) 0.12f else 0.45f), o, Size(22 * d, 8 * d)) } }
                }
                val taza = Offset(size.width * 0.78f, size.height - 70 * d)
                drawCircle(Color(0xFF7A5A2E).copy(alpha = if (oscuro) 0.14f else 0.12f), 26 * d, taza, style = Stroke(2.6f * d))
                drawCircle(Color(0xFF7A5A2E).copy(alpha = 0.05f), 24 * d, taza + Offset(3 * d, -2 * d))
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
                var y = 96 * d; while (y < size.height) { drawLine(tintaB, Offset(margen - 6 * d, y), Offset(size.width - 8 * d, y), 0.6f * d); y += maxOf(renglon, 1f) }
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
            Contenedor.LACA -> with(ContenedoresMas) { fondoHojaMas(c, cs, oscuro, renglon, margen) }
            Contenedor.CAHIER -> {
                // Cuadrícula Seyès: líneas finas cada cuarto y gruesas cada renglón, con el margen rojo.
                val malva = Color(0xFF8E7CC3)
                val paso = renglon / 4
                var y = 96 * d; var k = 0
                while (y < size.height) { drawLine(malva.copy(alpha = if (k % 4 == 0) 0.35f else 0.14f), Offset(0f, y), Offset(size.width, y), if (k % 4 == 0) 0.9f * d else 0.5f * d); y += paso; k++ }
                var x = margen; while (x < size.width) { drawLine(malva.copy(alpha = 0.14f), Offset(x, 96 * d), Offset(x, size.height), 0.5f * d); x += renglon }
                drawLine(Color(0xFFB22234).copy(alpha = 0.5f), Offset(margen - 6 * d, 0f), Offset(margen - 6 * d, size.height), 1.2f * d)
            }
            Contenedor.MANUSCRITO, Contenedor.ACORDEON, Contenedor.FUKURO, Contenedor.ORDENES, Contenedor.BITACORA, Contenedor.HERBARIO, Contenedor.ADVIENTO, Contenedor.MAPAS,
            Contenedor.EXPEDIENTE, Contenedor.CONSOLA, Contenedor.CARPETA, Contenedor.TECNICO, Contenedor.CUADERNO, Contenedor.VUELO, Contenedor.INMERSION, Contenedor.TETRAD, Contenedor.BOLETOS -> with(ContenedoresMas) { fondoHojaMas(c, cs, oscuro, renglon, margen) }
            Contenedor.BOLSA -> with(ContenedoresMas) { fondoHojaMas(c, cs, oscuro, renglon, margen) }
            Contenedor.SUPERVIVIENTE -> with(ContenedoresMas) { fondoHojaMas(c, cs, oscuro, renglon, margen) }
            Contenedor.TICKETS -> with(ContenedoresMas) { fondoHojaMas(c, cs, oscuro, renglon, margen) }
            Contenedor.PATENTES -> with(ContenedoresMas) { fondoHojaMas(c, cs, oscuro, renglon, margen) }
            Contenedor.SIMPLE -> {}   // la hoja lisa
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
            Contenedor.ESCRIBA -> {
                // Lo que queda enrollado a la izquierda, con el hilo de lino suelto y el terrón de barro del sello, ya roto.
                drawRect(Brush.horizontalGradient(listOf(lerp(PAPIRO_NILO, Color.Black, 0.4f), PAPIRO_NILO, lerp(PAPIRO_NILO, Color.Black, 0.25f)), 2 * d, 24 * d), Offset(2 * d, 2 * d), Size(22 * d, size.height - 4 * d))
                var y = 8 * d; while (y < size.height - 8 * d) { drawLine(Color(0x225A4020), Offset(4 * d, y), Offset(22 * d, y + 2 * d), 0.7f * d); y += 7 * d }
                val yh = size.height * 0.3f
                drawLine(LINO, Offset(13 * d, yh), Offset(40 * d, yh + 22 * d), 1.8f * d, StrokeCap.Round)
                drawLine(LINO, Offset(13 * d, yh + 4 * d), Offset(34 * d, yh + 34 * d), 1.8f * d, StrokeCap.Round)
                drawOval(BARRO, Offset(30 * d, yh + 24 * d), Size(16 * d, 12 * d))
                drawLine(lerp(BARRO, Color.Black, 0.4f), Offset(34 * d, yh + 26 * d), Offset(40 * d, yh + 34 * d), 1 * d)
                if (oscuro) drawRect(Color.Black.copy(alpha = 0.5f), Offset(2 * d, 2 * d), Size(22 * d, size.height - 4 * d))
            }
            Contenedor.BIOMBO -> {
                // La página anterior, doblada hacia atrás: una franja que se va a la sombra en el pliegue, con su filo rojo.
                drawRect(Brush.horizontalGradient(listOf(lerp(ESTUCO, Color.Black, 0.35f), ESTUCO, lerp(ESTUCO, Color.Black, 0.2f)), 2 * d, 20 * d), Offset(2 * d, 0f), Size(18 * d, size.height))
                drawLine(CINABRIO.copy(alpha = 0.6f), Offset(19 * d, 0f), Offset(19 * d, size.height), 1 * d)
                var y = 12 * d; while (y < size.height - 12 * d) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(4 * d, y), Offset(18 * d, y + 6 * d), 0.7f * d); y += 16 * d }
                if (oscuro) drawRect(Color.Black.copy(alpha = 0.5f), Offset(2 * d, 0f), Size(18 * d, size.height))
            }
            Contenedor.CODICE -> {
                // El pliegue del cuadernillo: sombra del lomo y las puntadas.
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), 10 * d, 34 * d), Offset(10 * d, 4 * d), Size(24 * d, size.height - 8 * d))
                var y = 30 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFF8A6A44), Offset(16 * d, y), Offset(16 * d, y + 10 * d), 1.6f * d); y += 48 * d }
            }
            Contenedor.REGLA -> with(ContenedoresMas) { encuadernacionMas(c, cs, oscuro) }
            Contenedor.HORAS -> {
                // El lomo de terciopelo azul con su cadenilla de oro y el registro de seda roja que cruza la esquina.
                val terciopelo = if (oscuro) Color(0xFF141E4A) else Color(0xFF1F2E6E)
                drawRect(Brush.horizontalGradient(listOf(lerp(terciopelo, Color.White, 0.12f), terciopelo, lerp(terciopelo, Color.Black, 0.3f)), 6 * d, 24 * d), Offset(6 * d, 0f), Size(18 * d, size.height))
                var y = 12 * d; var i = 0; while (y < size.height - 8 * d) { drawCircle(ORO.copy(alpha = if (i % 2 == 0) 0.9f else 0.6f), (if (i % 2 == 0) 2.2f else 1.4f) * d, Offset(15 * d, y)); y += 9 * d; i++ }
                drawPath(Path().apply { moveTo(20 * d, 0f); lineTo(26 * d, 0f); lineTo(70 * d, 60 * d); lineTo(64 * d, 64 * d); close() }, LACRE.copy(alpha = 0.85f))
            }
            Contenedor.RECORTES -> {
                // El lomo de tela verde con su costura y las grapas oxidadas que sujetan los recortes más gruesos.
                val tela = if (oscuro) lerp(TELA_VERDE, Color.Black, 0.45f) else TELA_VERDE
                drawRect(Brush.horizontalGradient(listOf(lerp(tela, Color.White, 0.1f), tela, lerp(tela, Color.Black, 0.3f)), 4 * d, 22 * d), Offset(4 * d, 0f), Size(18 * d, size.height))
                var y = 14 * d; while (y < size.height - 10 * d) { drawLine(Color(0xFFCFC6A8).copy(alpha = 0.7f), Offset(13 * d, y), Offset(13 * d, y + 6 * d), 1.2f * d); y += 16 * d }
                listOf(0.3f, 0.66f).forEach { f ->
                    val yy = size.height * f
                    drawLine(Color(0xFF8C5A34), Offset(26 * d, yy), Offset(26 * d, yy + 12 * d), 1.6f * d)
                    drawLine(Color(0xFF8C5A34).copy(alpha = 0.4f), Offset(27.5f * d, yy + 1 * d), Offset(29 * d, yy + 14 * d), 2.4f * d)
                }
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
            Contenedor.LACA -> with(ContenedoresMas) { encuadernacionMas(c, cs, oscuro) }
            Contenedor.CAHIER -> {
                // El hilo de la costura del cuaderno.
                var y = 20 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFF1F3A6E).copy(alpha = 0.7f), Offset(16 * d, y), Offset(16 * d, y + 8 * d), 1.4f * d); y += 22 * d }
            }
            Contenedor.MANUSCRITO, Contenedor.ACORDEON, Contenedor.FUKURO, Contenedor.ORDENES, Contenedor.BITACORA, Contenedor.HERBARIO, Contenedor.ADVIENTO, Contenedor.MAPAS,
            Contenedor.EXPEDIENTE, Contenedor.CONSOLA, Contenedor.CARPETA, Contenedor.TECNICO, Contenedor.CUADERNO, Contenedor.VUELO, Contenedor.INMERSION, Contenedor.TETRAD, Contenedor.BOLETOS -> with(ContenedoresMas) { encuadernacionMas(c, cs, oscuro) }
            Contenedor.BOLSA -> with(ContenedoresMas) { encuadernacionMas(c, cs, oscuro) }
            Contenedor.SUPERVIVIENTE -> with(ContenedoresMas) { encuadernacionMas(c, cs, oscuro) }
            Contenedor.TICKETS -> with(ContenedoresMas) { encuadernacionMas(c, cs, oscuro) }
            Contenedor.PATENTES -> with(ContenedoresMas) { encuadernacionMas(c, cs, oscuro) }
            Contenedor.SIMPLE -> {}   // sin encuadernación
        }
    }
}
