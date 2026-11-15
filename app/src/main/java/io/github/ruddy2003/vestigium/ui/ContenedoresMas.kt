package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Las libretas del lote 2: el manuscrito andalusí con lacería, el libro plegado en acordeón de la Ruta de la Seda, el
 * rollo de órdenes del yam, el diario de a bordo de los colonos, el herbario de plantas prensadas, el calendario de
 * adviento (cada día escrito abre una casilla) y el libro de mapas con broche de hierro de Tierras Antiguas.
 */
object ContenedoresMas {
    private val ORO = Color(0xFFD4AF37)
    private val HIERRO = Color(0xFF4A4E54)
    private val CUERO = Color(0xFF5A3A22)

    fun papel(c: Contenedor, cs: ColorScheme, oscuro: Boolean): Color {
        val claro = when (c) {
            Contenedor.MANUSCRITO -> Color(0xFFF3EAD6)
            Contenedor.ACORDEON -> Color(0xFFF4EEDF)
            Contenedor.FUKURO -> Color(0xFFF3EDDD)
            Contenedor.ORDENES -> Color(0xFFEFE6D0)
            Contenedor.BITACORA -> Color(0xFFF0E7D2)
            Contenedor.HERBARIO -> Color(0xFFEFEADB)
            // Papel reciclado, gris verdoso, de un cuaderno que se encontró entre los escombros.
            Contenedor.SUPERVIVIENTE -> Color(0xFFE6E3D6)
            Contenedor.ADVIENTO -> Color(0xFFFBF7EE)
            Contenedor.MAPAS -> Color(0xFFE9DCC0)
            // La piel de ciervo curtida, color de ante.
            Contenedor.BOLSA -> Color(0xFFE3D2B4)
            Contenedor.EXPEDIENTE -> Color(0xFFF2EFE6)
            // La pantalla: al oscurecerse queda casi negra (Terminal solo existe en oscuro).
            Contenedor.CONSOLA -> Color(0xFF22303A)
            // El papel térmico de los tickets, con el tinte magenta del letrero de enfrente.
            Contenedor.TICKETS -> Color(0xFFEFE3EA)
            Contenedor.CARPETA -> Color(0xFFF6EFD9)
            Contenedor.TECNICO -> Color(0xFFEDEDE8)
            // El papel de oficio, crema, de las memorias que se presentaban en la oficina de patentes.
            Contenedor.PATENTES -> Color(0xFFF1E7CF)
            // La hoja de lectura del cartucho: el hueso de la consola, que de noche queda en la penumbra pizarra de la sala.
            Contenedor.CARTUCHO -> Color(0xFFEDE8DC)
            // El papiro de las hojas del códice que se guarda en la jarra.
            Contenedor.JARRA -> Color(0xFFEDE2C4)
            // La cera de miel de la tablilla.
            Contenedor.HAZ -> Color(0xFFEADFC1)
            // El papel de dibujo del carnet de bocetos, crema y de grano grueso.
            Contenedor.BOCETOS -> Color(0xFFF4EEDE)
            Contenedor.CUADERNO -> Color(0xFFFBFCFD)
            Contenedor.VUELO -> Color(0xFFF4F6FA)
            // Papel sintético impermeable, gris verdoso, de las bitácoras que se mojan.
            Contenedor.INMERSION -> Color(0xFFEDF2EE)
            Contenedor.TETRAD -> Color(0xFFF7F3E6)
            // El pergamino blanqueado de la regla de la orden.
            Contenedor.REGLA -> Color(0xFFF4EFE2)
            // El papel marfil del carnet de baile, grueso y satinado.
            Contenedor.LACA -> Color(0xFFF6F0E1)
            // El papel de un cuaderno escolar italiano, apenas amarillo.
            Contenedor.BOLETOS -> Color(0xFFF5EEDC)
            // Las libretas de [Contenedores] tienen su papel allá.
            Contenedor.ROLLO, Contenedor.TABLILLA, Contenedor.ESCRIBA, Contenedor.CODICE, Contenedor.HORAS, Contenedor.TALLER, Contenedor.SINGLADURAS, Contenedor.LEGAJO, Contenedor.CAHIER -> return Contenedores.papel(c, cs, oscuro)
            Contenedor.RECORTES -> return Contenedores.papel(c, cs, oscuro)
            Contenedor.BIOMBO -> return Contenedores.papel(c, cs, oscuro)
            Contenedor.SIMPLE -> return Contenedores.papel(c, cs, oscuro)
        }
        return if (oscuro) lerp(claro, Color.Black, 0.76f) else claro
    }

    /** El canto de hojas de un libro cerrado. */
    private fun DrawScope.canto(w: Float, h: Float, grosor: Float, d: Float, papel: Color) {
        drawRoundRect(papel, Offset(grosor, grosor), Size(w, h), CornerRadius(3 * d))
        val n = (grosor / (1.6f * d)).toInt().coerceAtLeast(2)
        for (k in 1..n) { val o = k * grosor / (n + 1); drawLine(Color(0x55806A40), Offset(w + o, grosor + 6 * d), Offset(w + o, h + o - 3 * d), 0.8f * d); drawLine(Color(0x55806A40), Offset(grosor + 6 * d, h + o), Offset(w + o - 3 * d, h + o), 0.8f * d) }
    }

    // ================================================================== abierto

    fun DrawScope.fondoHojaMas(c: Contenedor, cs: ColorScheme, oscuro: Boolean, renglon: Float, margen: Float) {
        val d = density
        val tinta = if (oscuro) Color.White else Color.Black
        when (c) {
            Contenedor.MANUSCRITO -> {
                // Marco de doble filete en oro y la estrella al centro de la cabecera.
                drawRect(ORO.copy(alpha = 0.5f), Offset(margen - 14 * d, 70 * d), Size(size.width - margen + 4 * d, size.height - 80 * d), style = Stroke(1.2f * d))
                drawRect(ORO.copy(alpha = 0.3f), Offset(margen - 10 * d, 74 * d), Size(size.width - margen - 4 * d, size.height - 88 * d), style = Stroke(0.6f * d))
                with(TrofeosMas) { estrellaLazo(Offset(size.width / 2 + margen / 2, 70 * d), 8 * d, Brush.linearGradient(listOf(ORO, ORO)), null, 0f) }
                var y = 96 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFF8A6A44).copy(alpha = 0.1f), Offset(margen, y), Offset(size.width - 16 * d, y), 0.6f * d); y += renglon }
            }
            Contenedor.ACORDEON -> {
                // Los pliegues verticales y un sello de cinabrio en la esquina.
                var x = margen + 70 * d; while (x < size.width) { drawLine(tinta.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, size.height), 1.2f * d); drawRect(Brush.horizontalGradient(listOf(tinta.copy(alpha = 0.05f), Color.Transparent), x, x + 12 * d), Offset(x, 0f), Size(12 * d, size.height)); x += 90 * d }
                drawRect(Color(0xFFC0392B).copy(alpha = 0.35f), Offset(size.width - 34 * d, size.height - 40 * d), Size(20 * d, 20 * d))
            }
            Contenedor.FUKURO -> {
                // El washi con sus fibras y el marco de las páginas impresas en madera (kyōkaku), con sus columnas.
                val r = java.util.Random(72)
                repeat(70) {
                    val x = r.nextFloat() * size.width; val y = r.nextFloat() * size.height; val l = (6 + r.nextFloat() * 14) * d; val a = r.nextFloat() * PI
                    drawLine(tinta.copy(alpha = 0.035f), Offset(x, y), Offset(x + (l * cos(a)).toFloat(), y + (l * sin(a)).toFloat()), 0.6f * d)
                }
                val marco = Rect(margen - 10 * d, 72 * d, size.width - 12 * d, size.height - 14 * d)
                drawRect(tinta.copy(alpha = 0.16f), marco.topLeft, marco.size, style = Stroke(1.4f * d))
                drawRect(tinta.copy(alpha = 0.1f), marco.topLeft + Offset(3 * d, 3 * d), Size(marco.width - 6 * d, marco.height - 6 * d), style = Stroke(0.5f * d))
                var x = marco.left + renglon * 1.5f; while (x < marco.right - 4 * d) { drawLine(tinta.copy(alpha = 0.05f), Offset(x, marco.top + 3 * d), Offset(x, marco.bottom - 3 * d), 0.5f * d); x += maxOf(renglon * 1.5f, 1f) }
            }
            Contenedor.ORDENES -> {
                // Columnas verticales tenues (como la escritura vertical) y el margen de fieltro azul.
                var x = margen; while (x < size.width) { drawLine(Color(0xFF2E6DB4).copy(alpha = 0.08f), Offset(x, 96 * d), Offset(x, size.height), 0.8f * d); x += renglon }
                var y = 96 * d; while (y < size.height) { drawLine(tinta.copy(alpha = 0.05f), Offset(margen, y), Offset(size.width, y), 0.5f * d); y += renglon }
                drawRect(Color(0xFF2E6DB4).copy(alpha = 0.18f), Offset(0f, 0f), Size(size.width, 6 * d))
            }
            Contenedor.BITACORA -> {
                // Renglones azules, la columna de la hora en rojo y la cabecera.
                var y = 96 * d; while (y < size.height) { drawLine(Color(0xFF3A5A8A).copy(alpha = 0.18f), Offset(0f, y), Offset(size.width, y), 0.8f * d); y += renglon }
                drawLine(Color(0xFFB0281E).copy(alpha = 0.4f), Offset(margen - 8 * d, 0f), Offset(margen - 8 * d, size.height), 1 * d)
                drawLine(Color(0xFFB0281E).copy(alpha = 0.25f), Offset(margen + 40 * d, 80 * d), Offset(margen + 40 * d, size.height), 0.8f * d)
            }
            Contenedor.HERBARIO -> {
                // Una hoja como marca de agua y la etiqueta del espécimen abajo a la derecha.
                val c = Offset(size.width * 0.72f, size.height * 0.45f)
                drawPath(Path().apply { moveTo(c.x, c.y + 120 * d); cubicTo(c.x - 60 * d, c.y + 20 * d, c.x - 40 * d, c.y - 80 * d, c.x, c.y - 120 * d); cubicTo(c.x + 40 * d, c.y - 80 * d, c.x + 60 * d, c.y + 20 * d, c.x, c.y + 120 * d); close() },
                    Color(0xFF5A7A3A).copy(alpha = if (oscuro) 0.08f else 0.06f))
                drawLine(Color(0xFF5A7A3A).copy(alpha = 0.08f), Offset(c.x, c.y - 120 * d), Offset(c.x, c.y + 140 * d), 1.4f * d)
                val e = Rect(size.width - 140 * d, size.height - 70 * d, size.width - 16 * d, size.height - 16 * d)
                drawRect(tinta.copy(alpha = 0.12f), e.topLeft, e.size, style = Stroke(0.8f * d))
                for (k in 1..3) drawLine(tinta.copy(alpha = 0.08f), Offset(e.left + 6 * d, e.top + k * e.height / 4), Offset(e.right - 6 * d, e.top + k * e.height / 4), 0.6f * d)
                var y = 96 * d; while (y < size.height - 80 * d) { drawLine(tinta.copy(alpha = 0.04f), Offset(margen, y), Offset(size.width - 16 * d, y), 0.5f * d); y += renglon }
            }
            Contenedor.SUPERVIVIENTE -> {
                // Renglones a lápiz, el aro de agua que dejó una lata, una hoja de hiedra prensada y, en el margen, la cuenta
                // de los días de cinco en cinco.
                var y = 96 * d; while (y < size.height) { drawLine(tinta.copy(alpha = 0.07f), Offset(margen, y), Offset(size.width - 12 * d, y), 0.6f * d); y += maxOf(renglon, 1f) }
                drawCircle(Color(0xFF8A6A44).copy(alpha = if (oscuro) 0.08f else 0.1f), 34 * d, Offset(size.width - 70 * d, 150 * d), style = Stroke(3 * d))
                val c = Offset(size.width - 60 * d, size.height - 90 * d); val s = 60 * d
                drawPath(Path().apply { moveTo(c.x, c.y + s * 0.5f); lineTo(c.x - s * 0.55f, c.y + s * 0.35f); lineTo(c.x - s * 0.8f, c.y - s * 0.05f); lineTo(c.x - s * 0.32f, c.y - s * 0.05f); lineTo(c.x, c.y - s * 0.5f); lineTo(c.x + s * 0.32f, c.y - s * 0.05f); lineTo(c.x + s * 0.8f, c.y - s * 0.05f); lineTo(c.x + s * 0.55f, c.y + s * 0.35f); close() }, Color(0xFF4F7A33).copy(alpha = if (oscuro) 0.12f else 0.14f))
                drawLine(Color(0xFF4F7A33).copy(alpha = 0.2f), Offset(c.x, c.y + s * 0.5f), Offset(c.x - 10 * d, c.y + s * 1.1f), 1.4f * d)
                for (g in 0 until 3) {
                    val x0 = 8 * d; val y0 = 110 * d + g * 22 * d
                    for (k in 0 until 4) drawLine(tinta.copy(alpha = 0.14f), Offset(x0 + k * 4 * d, y0), Offset(x0 + k * 4 * d, y0 + 14 * d), 1 * d)
                    drawLine(tinta.copy(alpha = 0.14f), Offset(x0 - 2 * d, y0 + 12 * d), Offset(x0 + 15 * d, y0 + 2 * d), 1 * d)
                }
            }
            Contenedor.ADVIENTO -> {
                // Una cenefa de copos y pinos arriba, y la cinta roja y verde abajo.
                var x = 8 * d; var k = 0
                while (x < size.width) {
                    if (k % 2 == 0) drawPath(Path().apply { moveTo(x - 6 * d, 30 * d); lineTo(x, 14 * d); lineTo(x + 6 * d, 30 * d); close() }, Color(0xFF1F6B3A).copy(alpha = 0.35f))
                    else for (j in 0 until 3) { val a = j * PI / 3; drawLine(Color(0xFF6FB3E0).copy(alpha = 0.45f), Offset(x - (6 * d * cos(a)).toFloat(), 22 * d - (6 * d * sin(a)).toFloat()), Offset(x + (6 * d * cos(a)).toFloat(), 22 * d + (6 * d * sin(a)).toFloat()), 1 * d) }
                    x += 22 * d; k++
                }
                drawRect(Color(0xFFB3122E).copy(alpha = 0.35f), Offset(0f, size.height - 10 * d), Size(size.width, 4 * d))
                drawRect(Color(0xFF1F6B3A).copy(alpha = 0.35f), Offset(0f, size.height - 6 * d), Size(size.width, 4 * d))
                var y = 96 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFFB3122E).copy(alpha = 0.07f), Offset(margen, y), Offset(size.width - 12 * d, y), 0.6f * d); y += renglon }
            }
            Contenedor.MAPAS -> {
                // Curvas de nivel y una rosa de los vientos tenue.
                val az = java.util.Random(21)
                repeat(4) { k ->
                    val c = Offset(size.width * (0.3f + az.nextFloat() * 0.5f), size.height * (0.2f + az.nextFloat() * 0.6f))
                    for (j in 1..4) drawOval(Color(0xFF6B5A44).copy(alpha = if (oscuro) 0.12f else 0.08f), Offset(c.x - j * 22 * d, c.y - j * 14 * d), Size(j * 44 * d, j * 28 * d), style = Stroke(0.8f * d))
                }
                val r = 30 * d; val c = Offset(size.width - 50 * d, 130 * d)
                for (k in 0 until 8) { val a = k * PI / 4; val rr = if (k % 2 == 0) r else r * 0.5f; drawLine(Color(0xFF6B5A44).copy(alpha = 0.18f), c, Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat()), 1 * d) }
                drawCircle(Color(0xFF6B5A44).copy(alpha = 0.15f), r * 0.7f, c, style = Stroke(0.8f * d))
            }
            Contenedor.BOLSA -> {
                // La piel curtida: el poro, un punto de ocre en el margen por cada renglón (como las hileras de puntos de la
                // pared) y un bisonte de carbón, apenas, abajo a la derecha.
                val az = java.util.Random(13)
                repeat(160) { drawCircle(tinta.copy(alpha = 0.035f), (0.6f + az.nextFloat()) * d, Offset(az.nextFloat() * size.width, az.nextFloat() * size.height)) }
                var y = 96 * d; while (y < size.height) { drawCircle(Color(0xFFA2401F).copy(alpha = if (oscuro) 0.3f else 0.22f), 2 * d, Offset(margen - 12 * d, y - renglon * 0.35f)); y += maxOf(renglon, 1f) }
                drawPath(bisonte(Offset(size.width - 70 * d, size.height - 60 * d), 50 * d), tinta.copy(alpha = if (oscuro) 0.08f else 0.07f))
            }
            Contenedor.EXPEDIENTE -> {
                // Formulario oficial: renglones grises, casillas en el margen, el folio arriba y la franja roja.
                drawRect(Color(0xFFC8102E).copy(alpha = 0.45f), Offset(0f, 0f), Size(size.width, 4 * d))
                drawRect(tinta.copy(alpha = 0.15f), Offset(margen - 10 * d, 74 * d), Size(size.width - margen - 6 * d, size.height - 90 * d), style = Stroke(0.8f * d))
                var y = 96 * d; var k = 0
                while (y < size.height - 24 * d) {
                    drawLine(tinta.copy(alpha = 0.07f), Offset(margen, y), Offset(size.width - 20 * d, y), 0.6f * d)
                    if (k % 4 == 0) drawRect(tinta.copy(alpha = 0.18f), Offset(margen - 26 * d, y - 12 * d), Size(10 * d, 10 * d), style = Stroke(0.8f * d))
                    y += renglon; k++
                }
            }
            Contenedor.CONSOLA -> {
                // La pantalla de registro: líneas de barrido, la columna con el número de cada línea y la barra de estado arriba.
                val cian = Color(0xFF00E5FF)
                var y = 0f; while (y < size.height) { drawLine(cian.copy(alpha = 0.03f), Offset(0f, y), Offset(size.width, y), 1f); y += 3 * d }
                drawRect(cian.copy(alpha = 0.1f), Offset(0f, 0f), Size(size.width, 12 * d))
                with(TrofeosMas) {
                    mono("registro.log  -- solo lectura --", 26 * d, 9 * d, 8 * d, cian.copy(alpha = 0.5f))
                    drawLine(cian.copy(alpha = 0.2f), Offset(24 * d, 12 * d), Offset(24 * d, size.height), 0.8f * d)
                    var yy = 96 * d; var k = 1
                    while (yy < size.height - 8 * d) { mono("%03d".format(k), 21 * d, yy, 8 * d, cian.copy(alpha = 0.35f), android.graphics.Paint.Align.RIGHT); yy += renglon; k++ }
                }
            }
            Contenedor.TICKETS -> {
                // El ticket térmico: la cabecera del puesto, el número de orden, renglones de puntos y el borde dentado abajo.
                val tinta2 = if (oscuro) Color(0xFFFF8AC2) else Color(0xFF3A0D27)
                with(TrofeosMas) {
                    mono("屋台 · PUESTO DE FIDEOS · NIVEL −3", size.width / 2, 22 * d, 9 * d, tinta2.copy(alpha = 0.7f), android.graphics.Paint.Align.CENTER, negrita = true)
                    mono("ORDEN Nº ${(size.height / 7).toInt() % 900 + 100}", size.width / 2, 38 * d, 8 * d, tinta2.copy(alpha = 0.5f), android.graphics.Paint.Align.CENTER)
                }
                drawLine(tinta2.copy(alpha = 0.4f), Offset(12 * d, 50 * d), Offset(size.width - 12 * d, 50 * d), 1 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 3 * d)))
                var y = 96 * d
                while (y < size.height - 16 * d) { drawLine(tinta.copy(alpha = 0.08f), Offset(margen, y), Offset(size.width - 12 * d, y), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1 * d, 3 * d))); y += maxOf(renglon, 1f) }
                var x = 0f; var arriba = true
                val diente = Path().apply { moveTo(0f, size.height); while (x < size.width) { x += 6 * d; lineTo(x, if (arriba) size.height - 5 * d else size.height); arriba = !arriba }; lineTo(size.width, size.height); close() }
                drawPath(diente, Color.Black.copy(alpha = 0.35f))
            }
            Contenedor.CARPETA -> {
                // El formato del refugio: cabecera con casillas de turno y nivel, renglones verde azulado y la franja naranja.
                val linea = if (oscuro) Color(0xFF3DFF7F) else Color(0xFF1C6E6A)
                drawRect(Color(0xFFD9572B).copy(alpha = 0.5f), Offset(0f, 0f), Size(size.width, 5 * d))
                drawRect(linea.copy(alpha = 0.35f), Offset(margen - 10 * d, 72 * d), Size(size.width - margen - 2 * d, 18 * d), style = Stroke(0.9f * d))
                listOf(0.45f, 0.7f).forEach { f -> val x = margen - 10 * d + (size.width - margen - 2 * d) * f; drawLine(linea.copy(alpha = 0.35f), Offset(x, 72 * d), Offset(x, 90 * d), 0.9f * d) }
                var y = 96 * d + renglon; while (y < size.height - 16 * d) { drawLine(linea.copy(alpha = 0.14f), Offset(margen - 10 * d, y), Offset(size.width - 12 * d, y), 0.7f * d); y += renglon }
                with(TrofeosMas) { atomo(Offset(size.width - 40 * d, size.height - 50 * d), 26 * d, linea.copy(alpha = if (oscuro) 0.1f else 0.07f), 2 * d) }
            }
            Contenedor.TECNICO -> {
                // La lámina técnica: cuadrícula fina de dibujo, el margen carmesí y el cajetín de rótulo abajo a la derecha.
                val linea = if (oscuro) Color(0xFF8FB3C8) else Color(0xFF2F4858)
                var x = margen - 10 * d; while (x < size.width) { drawLine(linea.copy(alpha = 0.06f), Offset(x, 70 * d), Offset(x, size.height), 0.5f * d); x += renglon / 3 }
                var y = 70 * d; var k = 0; while (y < size.height) { drawLine(linea.copy(alpha = if (k % 3 == 0) 0.12f else 0.05f), Offset(margen - 10 * d, y), Offset(size.width, y), 0.5f * d); y += renglon / 3; k++ }
                drawLine(Color(0xFF9E1B22).copy(alpha = 0.45f), Offset(margen - 10 * d, 0f), Offset(margen - 10 * d, size.height), 1.2f * d)
                val r = Rect(size.width - 150 * d, size.height - 56 * d, size.width - 12 * d, size.height - 12 * d)
                drawRect(linea.copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(1f * d))
                drawLine(linea.copy(alpha = 0.3f), Offset(r.left, r.top + r.height / 2), Offset(r.right, r.top + r.height / 2), 0.8f * d)
                drawLine(linea.copy(alpha = 0.3f), Offset(r.left + r.width * 0.6f, r.top), Offset(r.left + r.width * 0.6f, r.bottom), 0.8f * d)
            }
            Contenedor.JARRA -> {
                // La hoja de papiro del códice de la jarra: las fibras de sus dos capas, la juntura de dos hojas pegadas, las
                // filas de diplés que apuntan al centro (bajo la cabecera y al pie, como los que enmarcan los títulos de los
                // tratados), cuatro puntos en el margen cada cuatro renglones y unos agujeros que se comió el tiempo en la orilla.
                val fibra = if (oscuro) Color(0xFFD6C49B) else Color(0xFF8E7650)
                val ocre = if (oscuro) Color(0xFFF0B24A) else Color(0xFF87590F)
                val az = java.util.Random(23)
                var y = 2 * d
                while (y < size.height) { drawLine(fibra.copy(alpha = (if (oscuro) 0.04f else 0.07f) + 0.07f * az.nextFloat()), Offset(size.width * 0.2f * az.nextFloat(), y), Offset(size.width * (1f - 0.2f * az.nextFloat()), y), 0.5f * d); y += (2.5f + 2f * az.nextFloat()) * d }
                var x = 20 * d
                while (x < size.width) { drawLine(fibra.copy(alpha = 0.04f), Offset(x, 0f), Offset(x, size.height), 0.7f * d); x += (30f + 40f * az.nextFloat()) * d }
                val xk = size.width * 0.8f
                drawRect(fibra.copy(alpha = 0.06f), Offset(xk, 0f), Size(5 * d, size.height))
                fun diples(yy: Float) {
                    val cx = (margen - 10 * d + size.width - 12 * d) / 2
                    var xx = margen - 10 * d
                    while (xx + 3 * d <= size.width - 12 * d) {
                        if (kotlin.math.abs(xx + 1.5f * d - cx) > 9 * d) {
                            val p = Path().apply { if (xx < cx) { moveTo(xx, yy - 3 * d); lineTo(xx + 3 * d, yy); lineTo(xx, yy + 3 * d) } else { moveTo(xx + 3 * d, yy - 3 * d); lineTo(xx, yy); lineTo(xx + 3 * d, yy + 3 * d) } }
                            drawPath(p, ocre.copy(alpha = 0.55f), style = Stroke(0.7f * d, cap = StrokeCap.Round))
                        }
                        xx += 5.5f * d
                    }
                    drawCircle(ocre.copy(alpha = 0.7f), 1.5f * d, Offset(cx, yy))
                }
                diples(86 * d)
                if (size.height > 200 * d) diples(size.height - 16 * d)
                var yy = 96 * d; var k = 0
                while (yy < size.height - 24 * d) {
                    if (k % 4 == 0) listOf(0f to -2.2f, 0f to 2.2f, -2.2f to 0f, 2.2f to 0f).forEach { (dx, dy) -> drawCircle(ocre.copy(alpha = 0.5f), 0.9f * d, Offset(margen - 14 * d + dx * d, yy - renglon * 0.35f + dy * d)) }
                    yy += maxOf(renglon, 1f); k++
                }
                listOf(0.3f, 0.62f).forEach { f ->
                    val o = Offset(size.width - 3 * d, size.height * f)
                    drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.12f), Offset(o.x - 4 * d, o.y - 3 * d), Size(8 * d, 6 * d))
                    drawOval(fibra.copy(alpha = 0.35f), Offset(o.x - 4 * d, o.y - 3 * d), Size(8 * d, 6 * d), style = Stroke(0.6f * d))
                }
            }
            Contenedor.HAZ -> {
                // La cera de la tablilla: las pasadas anchas de la espátula que la alisó, el rastro de lo escrito antes y borrado,
                // la onda de La Tène bajo la cabecera, una muesca en el margen por cada renglón (de cinco en cinco, la quinta
                // cruzada: la noche cuenta) y, abajo a la derecha, un trozo que se deja liso, con su hoja de roble.
                val oro = if (oscuro) Color(0xFFF2A040) else Color(0xFF7E590C)
                val az = java.util.Random(31)
                repeat(7) {
                    val y = size.height * az.nextFloat()
                    drawLine(Color.White.copy(alpha = if (oscuro) 0.025f else 0.07f), Offset(0f, y), Offset(size.width, y + (az.nextFloat() - 0.5f) * 40 * d), (6f + 10f * az.nextFloat()) * d)
                }
                repeat(14) {
                    val x = margen + (size.width - margen) * 0.8f * az.nextFloat(); val y = 110 * d + (size.height - 200 * d).coerceAtLeast(1f) * az.nextFloat()
                    drawLine(tinta.copy(alpha = 0.035f), Offset(x, y), Offset(x + (10f + 30f * az.nextFloat()) * d, y + (az.nextFloat() - 0.5f) * 2 * d), 1.2f * d, StrokeCap.Round)
                }
                val onda = Path(); var x = margen - 10 * d; var arriba = true
                onda.moveTo(x, 84 * d)
                while (x + 8 * d <= size.width - 12 * d) {
                    val s = if (arriba) -1f else 1f
                    onda.cubicTo(x + 2.4f * d, 84 * d + s * 3 * d, x + 5.6f * d, 84 * d + s * 3 * d, x + 8 * d, 84 * d)
                    drawCircle(oro.copy(alpha = 0.5f), 0.9f * d, Offset(x + 2.4f * d, 84 * d + s * 1 * d))
                    x += 8 * d; arriba = !arriba
                }
                drawPath(onda, oro.copy(alpha = 0.55f), style = Stroke(0.9f * d, cap = StrokeCap.Round))
                var yy = 96 * d; var k = 0
                val xm = margen - 12 * d
                while (yy < size.height - 16 * d) {
                    if (k % 5 == 4) drawLine(tinta.copy(alpha = 0.24f), Offset(xm - 4 * d, yy - renglon * 4.3f), Offset(xm + 4 * d, yy - renglon * 0.3f), 0.9f * d, StrokeCap.Round)
                    else drawLine(tinta.copy(alpha = 0.24f), Offset(xm - 2.5f * d, yy - renglon * 0.5f), Offset(xm + 2.5f * d, yy - renglon * 0.5f), 0.9f * d, StrokeCap.Round)
                    yy += maxOf(renglon, 1f); k++
                }
                val lisa = Offset(size.width - 46 * d, size.height - 46 * d)
                drawCircle(Color.White.copy(alpha = if (oscuro) 0.03f else 0.14f), 30 * d, lisa)
                with(TrazosGalia) { hojaRoble(lisa, 26 * d, 30f, Color(0xFF4E6B2E).copy(alpha = if (oscuro) 0.35f else 0.45f), Color(0xFF2E4219).copy(alpha = 0.4f)) }
            }
            Contenedor.BOCETOS -> {
                // El papel de dibujo: su grano, sin renglones; bajo la cabecera, una raya de lápiz que termina en un rizo de látigo;
                // en el margen, un punto de lápiz por renglón; abajo a la izquierda, tres toques de acuarela de prueba (violeta,
                // salvia y ocre) y, abajo a la derecha, un tallo a lápiz con su sombreado.
                val grafito = if (oscuro) Color(0xFFADA79A) else Color(0xFF5E5A60)
                val az = java.util.Random(37)
                repeat(220) { drawCircle(tinta.copy(alpha = 0.03f), (0.4f + 0.8f * az.nextFloat()) * d, Offset(az.nextFloat() * size.width, az.nextFloat() * size.height)) }
                val y0 = 84 * d
                val raya = Path().apply {
                    moveTo(margen - 10 * d, y0)
                    cubicTo(margen + (size.width - margen) * 0.3f, y0 - 3 * d, margen + (size.width - margen) * 0.6f, y0 + 3 * d, size.width - 30 * d, y0)
                    cubicTo(size.width - 22 * d, y0 - 1 * d, size.width - 22 * d, y0 - 9 * d, size.width - 28 * d, y0 - 8 * d)
                    cubicTo(size.width - 32 * d, y0 - 7 * d, size.width - 31 * d, y0 - 3 * d, size.width - 28 * d, y0 - 4 * d)
                }
                drawPath(raya, grafito.copy(alpha = 0.4f), style = Stroke(0.8f * d, cap = StrokeCap.Round))
                var yy = 96 * d
                while (yy < size.height - 20 * d) { drawCircle(grafito.copy(alpha = 0.25f), 0.9f * d, Offset(margen - 12 * d, yy - renglon * 0.4f)); yy += maxOf(renglon, 1f) }
                listOf(Color(0xFF6D4C82), Color(0xFF8FA67E), Color(0xFFD9A14A)).forEachIndexed { k, col ->
                    val o = Offset(margen + (8 + k * 20) * d, size.height - 30 * d - (k % 2) * 4 * d)
                    drawOval(col.copy(alpha = if (oscuro) 0.22f else 0.3f), Offset(o.x - 9 * d, o.y - 6 * d), Size(18 * d, 12 * d))
                    drawOval(col.copy(alpha = if (oscuro) 0.3f else 0.4f), Offset(o.x - 9 * d, o.y - 6 * d), Size(18 * d, 12 * d), style = Stroke(0.6f * d))
                }
                with(TrazosNouveau) {
                    tallo(Offset(size.width - 40 * d, size.height - 14 * d), 90 * d, 1f, -1f, 1.2f * d, grafito.copy(alpha = 0.22f), grafito.copy(alpha = 0.2f), grafito.copy(alpha = 0.2f))
                }
                for (k in 0 until 7) drawLine(grafito.copy(alpha = 0.12f), Offset(size.width - 30 * d, size.height - 20 * d - k * 4 * d), Offset(size.width - 18 * d, size.height - 30 * d - k * 4 * d), 0.6f * d)
            }
            Contenedor.CARTUCHO -> {
                // La lectura del cartucho: la cabecera con la célula y la pista, la franja ámbar del piloto, a la izquierda la
                // cinta magnética que pasa por la cabeza lectora (una marca por bloque y el contador de cuatro cifras cada
                // cuatro), renglones de puntos como los de la impresora de la consola y, al fondo, la retícula del radar.
                val ambar = if (oscuro) Color(0xFFF0A04B) else Color(0xFFB85C0F)
                val verde = if (oscuro) Color(0xFF9FD8A8) else Color(0xFF4F7A5A)
                drawRect(ambar.copy(alpha = 0.55f), Offset(0f, 0f), Size(size.width, 3 * d))
                with(TrofeosMas) {
                    // La pista arriba a la derecha, donde el título de la hoja no llega; el rótulo de la lectura, al pie.
                    mono("PISTA A", size.width - 12 * d, 16 * d, 8 * d, ambar.copy(alpha = 0.75f), android.graphics.Paint.Align.RIGHT, negrita = true)
                    mono("CARTUCHO K-7 · LECTURA", margen, size.height - 16 * d, 8 * d, tinta.copy(alpha = 0.55f))
                }
                drawLine(tinta.copy(alpha = 0.25f), Offset(margen, size.height - 28 * d), Offset(size.width - 160 * d, size.height - 28 * d), 0.8f * d)
                val cinta = Rect(margen - 30 * d, 0f, margen - 20 * d, size.height)
                drawRect(Color(0xFF4A3A2C).copy(alpha = if (oscuro) 0.5f else 0.28f), cinta.topLeft, cinta.size)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.25f), Offset(cinta.left + 2 * d, 0f), Offset(cinta.left + 2 * d, size.height), 0.6f * d)
                val c = Offset(size.width - 70 * d, size.height - 80 * d); val rr = 54 * d
                listOf(1f, 0.66f, 0.33f).forEach { f -> drawCircle(verde.copy(alpha = if (oscuro) 0.1f else 0.08f), rr * f, c, style = Stroke(0.8f * d)) }
                drawLine(verde.copy(alpha = 0.08f), Offset(c.x - rr, c.y), Offset(c.x + rr, c.y), 0.6f * d)
                drawLine(verde.copy(alpha = 0.08f), Offset(c.x, c.y - rr), Offset(c.x, c.y + rr), 0.6f * d)
                var y = 96 * d; var k = 1
                while (y < size.height - 12 * d) {
                    drawLine(tinta.copy(alpha = 0.1f), Offset(margen - 2 * d, y), Offset(size.width - 12 * d, y), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * d, 2.6f * d)))
                    drawLine(ambar.copy(alpha = 0.6f), Offset(cinta.left, y - 4 * d), Offset(cinta.right, y - 4 * d), 1 * d)
                    if (k % 4 == 0) with(TrofeosMas) { mono(Frente.contador(k), cinta.right + 2 * d, y - 2 * d, 5.5f * d, tinta.copy(alpha = 0.4f)) }
                    y += maxOf(renglon, 1f); k++
                }
            }
            Contenedor.PATENTES -> {
                // La memoria descriptiva de una patente: renglones de tinta sepia, el margen doble de ladrillo, el sello
                // redondo de la oficina arriba a la izquierda y, a la derecha, un pequeño plano azul de cianotipia con su engrane.
                val sepia = if (oscuro) Color(0xFFD9C7A8) else Color(0xFF6E5A3A)
                var y = 96 * d; while (y < size.height) { drawLine(sepia.copy(alpha = 0.16f), Offset(0f, y), Offset(size.width, y), 0.7f * d); y += maxOf(renglon, 1f) }
                listOf(margen - 10 * d, margen - 7 * d).forEach { x -> drawLine(Color(0xFF9C3D24).copy(alpha = 0.45f), Offset(x, 0f), Offset(x, size.height), 0.8f * d) }
                val s = Offset(margen + 22 * d, 40 * d)
                drawCircle(Color(0xFF9C3D24).copy(alpha = 0.35f), 18 * d, s, style = Stroke(1.4f * d))
                drawCircle(Color(0xFF9C3D24).copy(alpha = 0.25f), 13 * d, s, style = Stroke(0.7f * d))
                drawPath(Vapor.engrane(s, 8 * d, 8, 0.74f), Color(0xFF9C3D24).copy(alpha = 0.3f))
                val o = Offset(size.width - 98 * d, 16 * d)
                rotate(-3f, o + Offset(40 * d, 26 * d)) {
                    drawRect(Color(0xFF2B5C8A).copy(alpha = if (oscuro) 0.5f else 0.85f), o, Size(80 * d, 52 * d))
                    val blanco = Color.White.copy(alpha = 0.65f)
                    drawPath(Vapor.engrane(o + Offset(28 * d, 26 * d), 14 * d, 10, 0.8f), blanco, style = Stroke(0.8f * d))
                    drawCircle(blanco, 4 * d, o + Offset(28 * d, 26 * d), style = Stroke(0.8f * d))
                    drawCircle(blanco, 8 * d, o + Offset(58 * d, 18 * d), style = Stroke(0.8f * d))
                    drawLine(blanco, o + Offset(6 * d, 46 * d), o + Offset(74 * d, 46 * d), 0.6f * d)
                    drawLine(blanco, o + Offset(6 * d, 43 * d), o + Offset(6 * d, 49 * d), 0.6f * d); drawLine(blanco, o + Offset(74 * d, 43 * d), o + Offset(74 * d, 49 * d), 0.6f * d)
                }
            }
            Contenedor.CUADERNO -> {
                // La hoja cuadriculada del cuaderno de laboratorio, con el pie de «firma y testigo» y una tira de cinta de muestras.
                val cuadro = if (oscuro) Color(0xFF3CD2E5) else Color(0xFF0B8FA1)
                val paso = renglon / 2
                var x = 0f; while (x < size.width) { drawLine(cuadro.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, size.height), 0.5f * d); x += paso }
                var y = 0f; while (y < size.height) { drawLine(cuadro.copy(alpha = 0.08f), Offset(0f, y), Offset(size.width, y), 0.5f * d); y += paso }
                val pie = size.height - 30 * d
                listOf(0.08f to 0.46f, 0.54f to 0.92f).forEach { (a, b) -> drawLine(tinta.copy(alpha = 0.3f), Offset(size.width * a, pie), Offset(size.width * b, pie), 0.8f * d) }
                drawRect(Color(0xFF5E4FD1).copy(alpha = 0.35f), Offset(size.width - 70 * d, 18 * d), Size(56 * d, 12 * d))
                drawLine(Color.White.copy(alpha = 0.7f), Offset(size.width - 64 * d, 24 * d), Offset(size.width - 22 * d, 24 * d), 1 * d)
            }
            Contenedor.VUELO -> {
                // La hoja de la lista de vuelo: la barra marina con la franja naranja, la casilla de cada renglón y la marca de tiempo.
                val marino = if (oscuro) Color(0xFF8AB4FF) else Color(0xFF1B3A8C)
                drawRect(marino.copy(alpha = 0.22f), Offset(0f, 0f), Size(size.width, 10 * d))
                drawRect(Color(0xFFE8601C).copy(alpha = 0.6f), Offset(0f, 10 * d), Size(size.width, 2 * d))
                with(TrofeosMas) { mono("T+ 00:00:00", size.width - 12 * d, 30 * d, 8 * d, marino.copy(alpha = 0.5f), android.graphics.Paint.Align.RIGHT) }
                var y = 96 * d; while (y < size.height - 12 * d) {
                    drawLine(marino.copy(alpha = 0.1f), Offset(margen - 10 * d, y), Offset(size.width - 12 * d, y), 0.7f * d)
                    drawRect(marino.copy(alpha = 0.25f), Offset(13 * d, y - 12 * d), Size(9 * d, 9 * d), style = Stroke(0.8f * d))
                    y += renglon
                }
            }
            Contenedor.INMERSION -> {
                // La hoja de inmersión: el encabezado con cota y presión por llenar, la regla de profundidad en el margen
                // (cada renglón, diez metros más abajo) y, de fondo, el perfil del fondo marino que trazó el sonar.
                val tinta = if (oscuro) Color(0xFF5CF2C6) else Color(0xFF0A5C8C)
                with(TrofeosMas) { mono("COTA ____ m   PRESIÓN ____ bar", margen, 30 * d, 8 * d, tinta.copy(alpha = 0.5f)) }
                drawLine(tinta.copy(alpha = 0.3f), Offset(margen, 38 * d), Offset(size.width - 12 * d, 38 * d), 0.8f * d)
                val perfil = Path(); var x = margen; perfil.moveTo(margen, size.height * 0.7f)
                while (x <= size.width) { perfil.lineTo(x, size.height * 0.7f + 18 * d * sin(x / (47 * d)) + 9 * d * sin(x / (17 * d))); x += 4 * d }
                drawPath(perfil, tinta.copy(alpha = if (oscuro) 0.12f else 0.08f), style = Stroke(1.4f * d))
                var y = 96 * d; var k = 1
                while (y < size.height - 12 * d) {
                    drawLine(tinta.copy(alpha = 0.1f), Offset(margen - 6 * d, y), Offset(size.width - 12 * d, y), 0.7f * d)
                    drawLine(tinta.copy(alpha = 0.3f), Offset(margen - 18 * d, y), Offset(margen - 8 * d, y), 0.8f * d)
                    if (k % 2 == 0) with(TrofeosMas) { mono("${k * 10}", margen - 20 * d, y - 2 * d, 6 * d, tinta.copy(alpha = 0.35f), android.graphics.Paint.Align.RIGHT) }
                    y += maxOf(renglon, 1f); k++
                }
            }
            Contenedor.TETRAD -> {
                // La hoja «en cuadritos» del cuaderno escolar ruso, con el margen rojo del lado de afuera (a la derecha).
                val cuadro = if (oscuro) Color(0xFF7A9AC8) else Color(0xFF5A7AB0)
                val paso = renglon / 2
                var x = 0f; while (x < size.width) { drawLine(cuadro.copy(alpha = 0.12f), Offset(x, 0f), Offset(x, size.height), 0.5f * d); x += paso }
                var y = 0f; while (y < size.height) { drawLine(cuadro.copy(alpha = 0.12f), Offset(0f, y), Offset(size.width, y), 0.5f * d); y += paso }
                drawLine(Color(0xFFC1121F).copy(alpha = 0.55f), Offset(size.width - 26 * d, 0f), Offset(size.width - 26 * d, size.height), 1.2f * d)
            }
            Contenedor.REGLA -> {
                // La hoja de la regla: reglado rojo, el margen ancho donde va el número de cada artículo en romanos entre
                // puntos, la cruz patada en la cabecera y, abajo a la derecha, la huella del sello de la casa.
                val rojo = Color(0xFFB0191F)
                var y = 96 * d; while (y < size.height) { drawLine(rojo.copy(alpha = if (oscuro) 0.18f else 0.12f), Offset(margen - 10 * d, y), Offset(size.width - 14 * d, y), 0.7f * d); y += maxOf(renglon, 1f) }
                listOf(margen - 12 * d, margen - 15 * d).forEach { x -> drawLine(rojo.copy(alpha = 0.3f), Offset(x, 60 * d), Offset(x, size.height), 0.8f * d) }
                drawPath(cruzPatada(Offset(size.width / 2, 48 * d), 9 * d), rojo.copy(alpha = 0.55f))
                drawLine(tinta.copy(alpha = 0.2f), Offset(size.width * 0.2f, 66 * d), Offset(size.width * 0.8f, 66 * d), 0.8f * d)
                val s = Offset(size.width - 44 * d, size.height - 46 * d)
                drawCircle(rojo.copy(alpha = 0.07f), 26 * d, s); drawCircle(rojo.copy(alpha = 0.12f), 22 * d, s, style = Stroke(1 * d))
                drawPath(cruzPatada(s, 14 * d), rojo.copy(alpha = 0.08f))
            }
            Contenedor.LACA -> {
                // La página del carnet de baile: cabecera con el abanico y tres filetes que bajan en escalones, renglones
                // de oro muy finos, el margen doble y, al pie, el número de la pieza como en los carnets de los bailes.
                val oro = Color(0xFFB8952E)
                val cx = size.width / 2 + margen / 2
                drawArc(oro.copy(alpha = 0.5f), 180f, 180f, true, Offset(cx - 14 * d, 40 * d), Size(28 * d, 28 * d))
                for (i in 1..4) { val a = PI + i * PI / 5; drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.8f), Offset(cx, 54 * d), Offset(cx + 14 * d * cos(a).toFloat(), 54 * d + 14 * d * sin(a).toFloat()), 0.8f * d) }
                listOf(0.9f, 0.6f, 0.3f).forEachIndexed { k, f ->
                    val ancho = (size.width - margen) * f
                    drawLine(oro.copy(alpha = 0.5f - k * 0.1f), Offset(cx - ancho / 2, (60 + k * 4) * d), Offset(cx + ancho / 2, (60 + k * 4) * d), 0.8f * d)
                }
                var y = 96 * d; while (y < size.height) { drawLine(oro.copy(alpha = if (oscuro) 0.16f else 0.13f), Offset(margen - 8 * d, y), Offset(size.width - 12 * d, y), 0.6f * d); y += maxOf(renglon, 1f) }
                listOf(margen - 12 * d, margen - 15 * d).forEach { x -> drawLine(oro.copy(alpha = 0.3f), Offset(x, 70 * d), Offset(x, size.height), 0.7f * d) }
                drawRect(tinta.copy(alpha = 0.06f), Offset(size.width - 54 * d, size.height - 38 * d), Size(40 * d, 22 * d), style = Stroke(0.8f * d))
            }
            Contenedor.BOLETOS -> {
                // Los renglones del cuaderno escolar, con el margen a lápiz; arriba a la derecha, un boleto pegado con cinta.
                val raya = if (oscuro) Color(0xFF9DB0C8) else Color(0xFF6A84A8)
                var y = 96 * d; while (y < size.height) { drawLine(raya.copy(alpha = 0.18f), Offset(0f, y), Offset(size.width, y), 0.7f * d); y += maxOf(renglon, 1f) }
                drawLine(tinta.copy(alpha = 0.18f), Offset(margen - 8 * d, 0f), Offset(margen - 8 * d, size.height), 0.8f * d)
                val o = Offset(size.width - 92 * d, 18 * d)
                rotate(6f, o + Offset(36 * d, 16 * d)) {
                    drawRect(Color(0xFFE6EEDC).copy(alpha = if (oscuro) 0.35f else 0.9f), o, Size(72 * d, 32 * d))
                    drawRect(Color(0xFF3D5E45).copy(alpha = 0.45f), o + Offset(0f, 5 * d), Size(72 * d, 5 * d))
                    for (j in 0 until 2) drawLine(tinta.copy(alpha = 0.25f), o + Offset(8 * d, 17 * d + j * 6 * d), o + Offset((48 - j * 14) * d, 17 * d + j * 6 * d), 0.8f * d)
                    // La marca de la máquina que lo valida, de lado.
                    drawRect(Color(0xFF7A3A6A).copy(alpha = 0.35f), o + Offset(54 * d, 12 * d), Size(12 * d, 16 * d))
                    drawRect(Color(0xFFF2EAC6).copy(alpha = 0.5f), o + Offset(-6 * d, -4 * d), Size(18 * d, 8 * d))
                    drawRect(Color(0xFFF2EAC6).copy(alpha = 0.5f), o + Offset(60 * d, 28 * d), Size(18 * d, 8 * d))
                }
            }
            // Estas las dibuja [Contenedores].
            Contenedor.ROLLO, Contenedor.TABLILLA, Contenedor.ESCRIBA, Contenedor.CODICE, Contenedor.HORAS, Contenedor.TALLER, Contenedor.SINGLADURAS, Contenedor.LEGAJO, Contenedor.CAHIER -> {}
            Contenedor.RECORTES -> {}
            Contenedor.BIOMBO -> {}
            Contenedor.SIMPLE -> {}
        }
    }

    fun DrawScope.encuadernacionMas(c: Contenedor, cs: ColorScheme, oscuro: Boolean) {
        val d = density
        when (c) {
            Contenedor.MANUSCRITO -> {
                // El pliegue del cuadernillo con la cabezada de seda: un zigzag de puntadas rojas y verdes y puntos de oro.
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), 10 * d, 34 * d), Offset(10 * d, 4 * d), Size(24 * d, size.height - 8 * d))
                var y = 14 * d; var k = 0
                while (y < size.height - 14 * d) {
                    val col = if (k % 2 == 0) Color(0xFF9E2B25) else Color(0xFF1E6B5C)
                    drawLine(col.copy(alpha = 0.8f), Offset(12 * d, y), Offset(17 * d, y + 5 * d), 1.4f * d, StrokeCap.Round)
                    drawLine(col.copy(alpha = 0.8f), Offset(17 * d, y + 5 * d), Offset(22 * d, y), 1.4f * d, StrokeCap.Round)
                    if (k % 3 == 0) drawCircle(ORO.copy(alpha = 0.8f), 1.3f * d, Offset(17 * d, y + 9 * d))
                    y += 12 * d; k++
                }
            }
            Contenedor.MAPAS -> {
                // La tira de cuero clavada al lomo, con sus anillas de hierro y los clavos.
                val cuero = if (oscuro) lerp(CUERO, Color.Black, 0.35f) else CUERO
                drawRect(Brush.horizontalGradient(listOf(lerp(cuero, Color.White, 0.1f), cuero, lerp(cuero, Color.Black, 0.3f)), 6 * d, 26 * d), Offset(6 * d, 0f), Size(20 * d, size.height))
                var y = 30 * d
                while (y < size.height - 20 * d) {
                    drawCircle(HIERRO, 5 * d, Offset(24 * d, y), style = Stroke(2 * d))
                    listOf(-12f, 12f).forEach { dy -> drawCircle(Color(0xFF8A8D91), 1.5f * d, Offset(12 * d, y + dy * d)) }
                    y += 70 * d
                }
            }
            Contenedor.BOLSA -> {
                // La tira de tendón que cose la piel por el borde, en zigzag por los agujeros.
                val tendon = if (oscuro) Color(0xFF8A7458) else Color(0xFFCDB48A)
                var y = 16 * d; var k = 0
                while (y < size.height - 10 * d) {
                    drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f), 2.2f * d, Offset(12 * d, y))
                    drawLine(tendon, Offset(12 * d, y), Offset(if (k % 2 == 0) 24 * d else 6 * d, y + 14 * d), 1.8f * d, StrokeCap.Round)
                    y += 28 * d; k++
                }
            }
            Contenedor.EXPEDIENTE -> {
                // Las perforaciones del archivador y el broche metálico.
                var y = 40 * d; while (y < size.height - 20 * d) { drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), 4 * d, Offset(18 * d, y)); y += 110 * d }
                drawRect(Color(0xFF8A8D91).copy(alpha = 0.8f), Offset(12 * d, 30 * d), Size(4 * d, size.height - 60 * d))
            }
            Contenedor.ACORDEON -> {
                // El zigzag del plegado a la izquierda.
                val p = Path(); var y = 0f; var k = 0
                p.moveTo(10 * d, 0f)
                while (y < size.height) { y += 18 * d; p.lineTo(if (k % 2 == 0) 26 * d else 10 * d, y); k++ }
                drawPath(p, Color(0xFF8A6A44).copy(alpha = 0.6f), style = Stroke(1.4f * d))
            }
            Contenedor.FUKURO -> {
                // La costura de cuatro agujeros con hilo de índigo, que pasa por el borde de la hoja.
                val hilo = if (oscuro) Color(0xFF92ABD8) else Color(0xFF1E3A5F); val x = 20 * d
                val ys = listOf(0.12f, 0.37f, 0.63f, 0.88f).map { size.height * it }
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.16f), Color.Transparent), 0f, 30 * d), Offset.Zero, Size(30 * d, size.height))
                drawLine(hilo, Offset(x, 0f), Offset(x, size.height), 1.6f * d)
                ys.forEach { y -> drawLine(hilo, Offset(0f, y), Offset(x, y), 1.6f * d); drawCircle(Color.Black.copy(alpha = 0.35f), 2 * d, Offset(x, y)) }
            }
            Contenedor.ORDENES -> {
                drawRoundRect(Color(0xFF8A5A2B), Offset(10 * d, 0f), Size(10 * d, size.height), CornerRadius(5 * d))
                if (oscuro) drawRect(Color.Black.copy(alpha = 0.4f), Offset(10 * d, 0f), Size(10 * d, size.height))
            }
            Contenedor.BITACORA -> {
                drawRect(Color(0xFF7A4A28), Offset(8 * d, 0f), Size(14 * d, size.height))
                var y = 20 * d; while (y < size.height - 10 * d) { drawLine(Color(0xFFE9DCC0).copy(alpha = 0.7f), Offset(12 * d, y), Offset(18 * d, y + 6 * d), 1.2f * d); y += 26 * d }
            }
            Contenedor.HERBARIO -> listOf(0.2f, 0.8f).forEach { f -> val y = size.height * f; drawLine(Color(0xFFEFE2C0), Offset(0f, y), Offset(24 * d, y), 3 * d, StrokeCap.Round); drawCircle(Color(0xFF6E7A4E), 3 * d, Offset(22 * d, y)) }
            Contenedor.SUPERVIVIENTE -> {
                // El lomo de cinta plateada y el lápiz de carpintero atado con un cordel.
                drawRect(Color(0xFF9EA2A0).copy(alpha = if (oscuro) 0.35f else 0.85f), Offset(6 * d, 0f), Size(16 * d, size.height))
                var y = 18 * d; while (y < size.height) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(6 * d, y), Offset(22 * d, y + 4 * d), 0.8f * d); y += 30 * d }
                drawLine(Color(0xFF8A7F68), Offset(14 * d, 40 * d), Offset(30 * d, 120 * d), 1.2f * d)
                drawRoundRect(Color(0xFFC9A227), Offset(26 * d, 118 * d), Size(6 * d, 52 * d), CornerRadius(1.5f * d))
                drawPath(Path().apply { moveTo(26 * d, 170 * d); lineTo(32 * d, 170 * d); lineTo(29 * d, 180 * d); close() }, Color(0xFF3A2A1A))
            }
            Contenedor.ADVIENTO -> {
                drawRect(Color(0xFFB3122E), Offset(12 * d, 0f), Size(8 * d, size.height))
                val y = 60 * d
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(16 * d, y); quadraticTo(16 * d + s * 14 * d, y - 12 * d, 16 * d + s * 16 * d, y + 4 * d); close() }, Color(0xFFB3122E)) }
                drawCircle(Color(0xFFD4AF37), 3 * d, Offset(16 * d, y))
            }
            Contenedor.CONSOLA -> {
                // La tira de arrastre del papel continuo: los agujeros del tractor y la línea perforada.
                drawRect(Color(0xFFD9DEE3).copy(alpha = if (oscuro) 0.16f else 0.6f), Offset(2 * d, 0f), Size(16 * d, size.height))
                var y = 8 * d; while (y < size.height) { drawCircle(Color.Black.copy(alpha = 0.6f), 2.4f * d, Offset(10 * d, y)); y += 13 * d }
                drawLine(Color(0xFF00E5FF).copy(alpha = 0.3f), Offset(18 * d, 0f), Offset(18 * d, size.height), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 2 * d)))
            }
            Contenedor.TICKETS -> {
                // El agujero por donde la púa atravesó los tickets, con el desgarre alrededor, y la línea de corte.
                val y = size.height * 0.12f
                drawCircle(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.35f), 3.2f * d, Offset(20 * d, y))
                for (k in 0 until 6) { val a = k * PI / 3; drawLine(Color.Black.copy(alpha = 0.3f), Offset(20 * d, y), Offset(20 * d + (6 * d * cos(a)).toFloat(), y + (6 * d * sin(a)).toFloat()), 0.7f * d) }
                drawLine(Color(0xFFFF3D9A).copy(alpha = 0.35f), Offset(30 * d, 0f), Offset(30 * d, size.height), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * d, 3 * d)))
            }
            Contenedor.CARPETA -> {
                // El broche de dos patas que pasa por los agujeros de las hojas, con su placa de metal.
                val metal = if (oscuro) Color(0xFF6E7478) else Color(0xFF9EA3A8)
                listOf(0.2f, 0.8f).forEach { f -> val y = size.height * f; drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), 4 * d, Offset(20 * d, y)); drawLine(metal, Offset(20 * d, y), Offset(20 * d, y + if (f < 0.5f) 24 * d else -24 * d), 2.4f * d) }
                drawRoundRect(metal, Offset(14 * d, size.height * 0.2f - 6 * d), Size(12 * d, size.height * 0.6f + 12 * d), CornerRadius(3 * d), style = Stroke(1.4f * d))
            }
            Contenedor.TECNICO -> {
                // La bisagra de piano de acero del lomo, con sus nudillos y el pasador.
                val acero = if (oscuro) Color(0xFF5A626B) else Color(0xFF8A929B)
                var y = 0f; var k = 0
                while (y < size.height) { drawRoundRect(if (k % 2 == 0) acero else lerp(acero, Color.Black, 0.2f), Offset(10 * d, y), Size(12 * d, 18 * d), CornerRadius(3 * d)); y += 18 * d; k++ }
                drawLine(lerp(acero, Color.White, 0.3f), Offset(16 * d, 0f), Offset(16 * d, size.height), 1f * d)
            }
            Contenedor.JARRA -> {
                // El códice de un solo cuadernillo, como los de Nag Hammadi: la sombra del pliegue, la tira de cuero que forra el
                // lomo y las tres tiras cortas de cuero que lo cosen, con su nudo.
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.26f), Color.Transparent), 10 * d, 34 * d), Offset(10 * d, 4 * d), Size(24 * d, size.height - 8 * d))
                val cuero = if (oscuro) Color(0xFF3A2A1C) else Color(0xFF6B4A2E)
                drawRect(Brush.horizontalGradient(listOf(lerp(cuero, Color.White, 0.1f), cuero, lerp(cuero, Color.Black, 0.3f)), 4 * d, 15 * d), Offset(4 * d, 0f), Size(11 * d, size.height))
                val correa = if (oscuro) Color(0xFF2E2014) else Color(0xFF4A321E)
                listOf(0.18f, 0.5f, 0.82f).forEach { f ->
                    val y = size.height * f
                    listOf(-3.5f, 3.5f).forEach { dy -> drawLine(correa, Offset(8 * d, y + dy * d), Offset(24 * d, y + dy * d), 2 * d, StrokeCap.Round) }
                    drawCircle(correa, 3 * d, Offset(24 * d, y))
                    drawCircle(Color.White.copy(alpha = 0.12f), 1 * d, Offset(23 * d, y - 1 * d))
                }
            }
            Contenedor.HAZ -> {
                // El canto de roble del marco de la tablilla, con su veta, y las bisagras: un tiento de cuero que entra por dos
                // agujeros, da la vuelta al canto y se anuda, con los cabos sueltos.
                val roble = if (oscuro) Color(0xFF3A2C1C) else Color(0xFF8A6A40)
                drawRect(Brush.horizontalGradient(listOf(lerp(roble, Color.White, 0.12f), roble, lerp(roble, Color.Black, 0.35f)), 2 * d, 16 * d), Offset(2 * d, 0f), Size(14 * d, size.height))
                var yv = 6 * d; var kv = 0
                while (yv < size.height) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(4 * d + (kv % 3) * 3 * d, yv), Offset(5 * d + (kv % 3) * 3 * d, yv + 22 * d), 0.6f * d); yv += 9 * d; kv++ }
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(16 * d, 0f), Offset(16 * d, size.height), 1 * d)
                val tiento = if (oscuro) Color(0xFF3A2614) else Color(0xFF5A3A22)
                listOf(0.22f, 0.5f, 0.78f).forEach { f ->
                    val y = size.height * f
                    listOf(-5f, 5f).forEach { dy -> drawCircle(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.45f), 1.8f * d, Offset(10 * d, y + dy * d)) }
                    drawArc(tiento, 90f, 180f, false, Offset(0f, y - 5 * d), Size(20 * d, 10 * d), style = Stroke(2.2f * d, cap = StrokeCap.Round))
                    drawCircle(tiento, 2.6f * d, Offset(12 * d, y + 5 * d))
                    drawLine(tiento, Offset(12 * d, y + 5 * d), Offset(22 * d, y + 14 * d), 1.6f * d, StrokeCap.Round)
                    drawLine(tiento, Offset(12 * d, y + 5 * d), Offset(19 * d, y + 17 * d), 1.6f * d, StrokeCap.Round)
                }
            }
            Contenedor.BOCETOS -> {
                // El lomo de tela verde, con la costura de los cuadernillos que asoma, y la cinta de seda malva del registro, que
                // cae por la orilla y termina en dos puntas.
                val tela = if (oscuro) Color(0xFF1E2A24) else Color(0xFF3D5A48)
                drawRect(Brush.horizontalGradient(listOf(lerp(tela, Color.White, 0.12f), tela, lerp(tela, Color.Black, 0.35f)), 4 * d, 20 * d), Offset(4 * d, 0f), Size(16 * d, size.height))
                var y = 0f; while (y < size.height) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(4 * d, y), Offset(20 * d, y), 0.4f * d); y += 1.6f * d }
                var yy = 18 * d; while (yy < size.height - 12 * d) { drawLine(Color(0xFFF4EEDE).copy(alpha = if (oscuro) 0.35f else 0.7f), Offset(20 * d, yy), Offset(25 * d, yy), 1.2f * d); yy += 24 * d }
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), 20 * d, 36 * d), Offset(20 * d, 0f), Size(16 * d, size.height))
                val seda = if (oscuro) Color(0xFF6E5470) else Color(0xFFB48AB0)
                val fin = size.height * 0.36f
                drawPath(Path().apply {
                    moveTo(26 * d, 0f); lineTo(31 * d, 0f); cubicTo(32 * d, fin * 0.4f, 29 * d, fin * 0.7f, 31 * d, fin); lineTo(28.5f * d, fin - 4 * d); lineTo(26 * d, fin)
                    cubicTo(24 * d, fin * 0.7f, 27 * d, fin * 0.4f, 26 * d, 0f); close()
                }, seda)
            }
            Contenedor.CARTUCHO -> {
                // El canto de la carcasa del cartucho: baquelita con la muesca de lectura, la pestaña ámbar que protege contra
                // escritura y las estrías de agarre abajo.
                val baquelita = if (oscuro) Color(0xFF1C1A17) else Color(0xFF2A2622)
                drawRoundRect(baquelita, Offset(4 * d, 0f), Size(22 * d, size.height), CornerRadius(4 * d))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.16f), Offset(6 * d, 4 * d), Offset(6 * d, size.height - 4 * d), 1 * d)
                val muesca = size.height * 0.16f
                drawRect(papel(c, cs, oscuro), Offset(16 * d, muesca), Size(10 * d, 26 * d))
                drawRect(Color.Black.copy(alpha = 0.35f), Offset(16 * d, muesca), Size(1.4f * d, 26 * d))
                drawRoundRect(Color(0xFFF2A33A).copy(alpha = if (oscuro) 0.75f else 1f), Offset(9 * d, muesca + 40 * d), Size(8 * d, 12 * d), CornerRadius(1.5f * d))
                var y = size.height - 60 * d
                while (y < size.height - 8 * d) { drawLine(Color.Black.copy(alpha = 0.45f), Offset(8 * d, y), Offset(24 * d, y), 1.2f * d); y += 5 * d }
            }
            Contenedor.PATENTES -> {
                // El lomo de tela color ladrillo con tres bandas de latón y la costura que asoma entre ellas.
                val ladrillo = if (oscuro) Color(0xFF4A2014) else Color(0xFF9C3D24)
                drawRect(ladrillo, Offset(8 * d, 0f), Size(16 * d, size.height))
                val laton = Color(0xFFC9A04A)
                listOf(0.18f, 0.5f, 0.82f).forEach { f -> drawRect(laton, Offset(7 * d, size.height * f - 3 * d), Size(18 * d, 6 * d)) }
                var y = 12 * d; while (y < size.height - 12 * d) { drawLine(Color(0xFFF1E7CF).copy(alpha = 0.5f), Offset(16 * d, y), Offset(16 * d, y + 5 * d), 1f * d); y += 14 * d }
            }
            Contenedor.CUADERNO -> {
                // El lomo cosido con tela cian y el hilo de la costura.
                val cian = if (oscuro) Color(0xFF0C3640) else Color(0xFF0B8FA1)
                drawRect(cian, Offset(8 * d, 0f), Size(14 * d, size.height))
                var y = 14 * d; while (y < size.height - 10 * d) { drawLine(Color.White.copy(alpha = 0.7f), Offset(15 * d, y), Offset(15 * d, y + 8 * d), 1.2f * d); y += 20 * d }
            }
            Contenedor.VUELO -> {
                // La tira de velcro a lo largo del borde (así se sujetaba la bitácora en la cabina).
                drawRect(Color(0xFF26324F).copy(alpha = if (oscuro) 0.9f else 0.7f), Offset(6 * d, 0f), Size(16 * d, size.height))
                var y = 0f; while (y < size.height) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(6 * d, y), Offset(22 * d, y), 1f); y += 2 * d }
                drawLine(Color(0xFFE8601C).copy(alpha = 0.7f), Offset(22 * d, 0f), Offset(22 * d, size.height), 1.2f * d)
            }
            Contenedor.INMERSION -> {
                // Los ollaos de latón del borde, con el cabo encerado que pasa de uno a otro en zigzag.
                val laton = if (oscuro) Color(0xFF8C6E2E) else Color(0xFFB8913E)
                val cabo = Color(0xFFCDB88C).copy(alpha = if (oscuro) 0.6f else 0.9f)
                val paso = 34 * d; var y = 18 * d; var k = 0
                val zig = Path().apply { moveTo(16 * d, 18 * d) }
                while (y < size.height - 10 * d) {
                    drawCircle(laton, 4.4f * d, Offset(16 * d, y)); drawCircle(Color.Black.copy(alpha = 0.5f), 2 * d, Offset(16 * d, y))
                    if (k > 0) zig.lineTo(if (k % 2 == 0) 16 * d else 4 * d, y)
                    y += maxOf(paso, 1f); k++
                }
                drawPath(zig, cabo, style = Stroke(1.6f * d, cap = StrokeCap.Round))
            }
            Contenedor.TETRAD -> {
                // El pliegue del cuadernillo con sus dos grapas.
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), 10 * d, 30 * d), Offset(10 * d, 4 * d), Size(20 * d, size.height - 8 * d))
                listOf(0.3f, 0.7f).forEach { f -> val y = size.height * f; drawRect(Color(0xFFC3C6CA), Offset(14 * d, y - 9 * d), Size(3 * d, 18 * d)); drawRect(Color.Black.copy(alpha = 0.25f), Offset(17 * d, y - 9 * d), Size(1 * d, 18 * d)) }
            }
            Contenedor.REGLA -> {
                // El lomo cosido sobre nervios de cuero: tres correas que cruzan el pliegue, sujetas con clavos de hierro.
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.26f), Color.Transparent), 10 * d, 32 * d), Offset(10 * d, 4 * d), Size(22 * d, size.height - 8 * d))
                val correa = if (oscuro) Color(0xFF3E2817) else Color(0xFF5A3A22)
                listOf(0.18f, 0.5f, 0.82f).forEach { f ->
                    val y = size.height * f
                    drawRect(correa, Offset(0f, y - 5 * d), Size(30 * d, 10 * d))
                    drawCircle(HIERRO, 2 * d, Offset(24 * d, y))
                }
            }
            Contenedor.LACA -> {
                // El lomo de laca con tres anillas de oro y, atado a la del medio, el lápiz de baile con su cordón de seda.
                val laca = if (oscuro) Color(0xFF2A2620) else Color(0xFF16130E)
                drawRect(laca, Offset(4 * d, 0f), Size(20 * d, size.height))
                drawRect(Color.White.copy(alpha = 0.06f), Offset(8 * d, 0f), Size(3 * d, size.height))
                listOf(0.2f, 0.5f, 0.8f).forEach { f -> val y = size.height * f; drawOval(ORO, Offset(8 * d, y - 6 * d), Size(14 * d, 12 * d), style = Stroke(2 * d)) }
                val y0 = size.height * 0.5f
                drawLine(Color(0xFFB5523B).copy(alpha = 0.8f), Offset(15 * d, y0 + 6 * d), Offset(18 * d, y0 + 60 * d), 1.2f * d)
                drawRect(ORO, Offset(14 * d, y0 + 60 * d), Size(8 * d, 34 * d))
                drawPath(Path().apply { moveTo(14 * d, y0 + 94 * d); lineTo(22 * d, y0 + 94 * d); lineTo(18 * d, y0 + 104 * d); close() }, Color(0xFF2E2A24))
            }
            Contenedor.BOLETOS -> {
                // El lomo de tela ocre, cosido con hilo grueso, y el elástico rojo que cruza el borde.
                val tela = Color(0xFFC98A3A).copy(alpha = if (oscuro) 0.55f else 0.9f)
                drawRect(tela, Offset(6 * d, 0f), Size(16 * d, size.height))
                var y = 12 * d; while (y < size.height - 8 * d) { drawLine(Color(0xFFFBF6EA).copy(alpha = 0.75f), Offset(14 * d, y), Offset(14 * d, y + 7 * d), 1.4f * d); y += 16 * d }
                drawRect(Color(0xFFA8432A).copy(alpha = 0.85f), Offset(0f, size.height * 0.42f), Size(26 * d, 4 * d))
            }
            // Estas las dibuja [Contenedores].
            Contenedor.ROLLO, Contenedor.TABLILLA, Contenedor.ESCRIBA, Contenedor.CODICE, Contenedor.HORAS, Contenedor.TALLER, Contenedor.SINGLADURAS, Contenedor.LEGAJO, Contenedor.CAHIER -> {}
            Contenedor.RECORTES -> {}
            Contenedor.BIOMBO -> {}
            Contenedor.SIMPLE -> {}
        }
    }
}
