package io.github.ruddy2003.vestigium.ui.vida

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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Distopía: Vida en el edificio del Ministerio, con los materiales de su archivo del Ideario y de su cuaderno de incidencias:
 * hormigón en bloques, acero gris cepillado con remaches y biseles, el rojo de cartel con sus estrellas negras, el ojo que todo
 * lo ve en su medallón, carpetas de manila, papel de oficio y la luz roja de la cámara.
 * - Social: la sala del comité. El muro de bloques con el medallón del ojo y sus rayos, las bandas rojas con estrellas, el
 *   altavoz y la cámara; los respaldos de las sillas al fondo y la mesa de acero en perspectiva, con su canto remachado, sus
 *   patas y su sombra. El grafo va en el tapete de la mesa; en las esquinas, el vaso de agua, los expedientes, el sello y el
 *   micrófono del comité.
 * - Mapas: la mesa de planificación vista desde arriba. El marco de acero con escuadras atornilladas, el pliego sujeto con
 *   cantoneras y la regla; alrededor, los expedientes, el tampón y el sello, la lupa, el teléfono de disco, la bandeja de
 *   chinchetas de sector, el cenicero con su cigarro y el lápiz.
 * - Otras vidas: el gabinete de pruebas. La cornisa de acero con el ojo y la banda roja con estrellas; cinco casilleros iguales
 *   separados por montantes remachados, cada uno con su etiqueta de inventario; los costados con rejillas y el zócalo ranurado.
 * - Íconos: la pirámide escalonada del Ministerio con sus reflectores, el expediente con la barra de censura que se raspa con
 *   una moneda, y el plano de la ciudad con chinchetas rojas unidas por el hilo de la investigación.
 * - Notas: el tablón de expedientes del Ministerio, de fieltro gris en su marco de acero, con la cabecera roja del ojo y la
 *   repisa del sello. Las notas son fichas, carpetillas, formularios y copias rosadas; las fijadas llevan chincheta roja y la
 *   franja de «clasificado»; las demás, un clip.
 */
internal object VidaDistopia : DisenoVida {
    private val GRIS = Color(0xFF6E7378)
    private val GRIS_LUZ = Color(0xFF9AA0A6)
    private val GRIS_OSC = Color(0xFF3E4246)
    private val NEGRO = Color(0xFF141414)
    private val ROJO = Color(0xFFC8102E)
    private val MANILA = Color(0xFFD9C48E)
    private val PAPEL = Color(0xFFECE8DC)
    private val HORMIGON = Color(0xFFB8B6AE)
    private val LINOLEO = Color(0xFF7D827B)
    private val MESA = Color(0xFF8D948E)
    private val ROSA = Color(0xFFF0D2D0)
    private val LAPIZ = Color(0xFFE2B13C)
    private val NOCHE = Color(0xFF0B0C0D)

    /** La unidad de dibujo: la centésima parte del lado menor del lienzo. */
    private fun DrawScope.u() = size.minDimension / 100f

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en oscuro: hundido hacia el negro del archivo, como en su Ideario. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.42f) else c

    /** El papel se hunde menos: en oscuro queda en penumbra, pero se lee la tinta negra encima. */
    private fun papel(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.36f) else c

    /** El rojo de cartel: el acento del esquema, hundido en oscuro como el de su archivo. */
    private fun rojo(e: Color, oscuro: Boolean): Color = if (oscuro) mezcla(e, NOCHE, 0.3f) else e

    /** El fondo del nicho: el material apenas mezclado con el fondo del esquema, para que se lea lo de encima. */
    private fun nicho(fondo: Color, material: Color, oscuro: Boolean): Color = mezcla(fondo, material, if (oscuro) 0.2f else 0.24f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.15f, 0.31f, 0.85f, 0.84f)
        MuebleVida.MAPAS -> Rect(0.19f, 0.12f, 0.81f, 0.84f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.05f, 0.30f, 0.95f, 0.87f)
        MuebleVida.NOTAS -> Rect(0.07f, 0.15f, 0.93f, 0.90f)
    }

    // ---------------------------------------------------------------- Ayudas de dibujo

    /** Una sombra suave bajo una pieza: varias capas cada vez más anchas y transparentes. */
    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.3f), CornerRadius(e))
        }
    }

    /** El bisel de una chapa: luz arriba y a la izquierda, sombra abajo y a la derecha. */
    private fun DrawScope.bisel(r: Rect, luz: Color, sombra: Color, ancho: Float) {
        drawRect(luz, r.topLeft, Size(r.width, ancho))
        drawRect(luz.copy(alpha = luz.alpha * 0.7f), r.topLeft, Size(ancho, r.height))
        drawRect(sombra, Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(sombra.copy(alpha = sombra.alpha * 0.8f), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    /** Una chapa de acero con su degradado de luz y su bisel. */
    private fun DrawScope.chapa(r: Rect, base: Color, oscuro: Boolean, u: Float) {
        drawRect(Brush.verticalGradient(listOf(mezcla(base, Color.White, 0.18f), base, mezcla(base, Color.Black, 0.22f)), r.top, r.bottom), r.topLeft, r.size)
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.12f else 0.3f), Color.Black.copy(alpha = 0.4f), 0.5f * u)
    }

    /** Un tornillo de cabeza ranurada. */
    private fun DrawScope.tornillo(c: Offset, r: Float, oscuro: Boolean, giro: Float = 30f) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r * 1.15f, Offset(c.x + r * 0.2f, c.y + r * 0.25f))
        drawCircle(t(GRIS_LUZ, oscuro), r, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.15f else 0.45f), r * 0.45f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
        rotate(giro, c) { drawLine(t(GRIS_OSC, oscuro), Offset(c.x - r * 0.7f, c.y), Offset(c.x + r * 0.7f, c.y), r * 0.3f) }
    }

    /** Un remache: la cabeza redonda con su brillo. */
    private fun DrawScope.remache(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + r * 0.25f, c.y + r * 0.3f))
        drawCircle(t(GRIS_LUZ, oscuro), r, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.18f else 0.5f), r * 0.4f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
    }

    /** Una estrella de [puntas] puntas con centro [c] y radio [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int = 5, hondo: Float = 0.42f): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /** El ojo que todo lo ve: la almendra, el iris (rojo y encendido en oscuro), la pupila, el brillo y el contorno. */
    private fun DrawScope.ojo(c: Offset, a: Float, oscuro: Boolean, acento: Color) {
        val almendra = Path().apply {
            moveTo(c.x - a, c.y); quadraticTo(c.x, c.y - a * 0.92f, c.x + a, c.y); quadraticTo(c.x, c.y + a * 0.92f, c.x - a, c.y); close()
        }
        drawPath(almendra, if (oscuro) Color(0xFFD8D4CA) else Color.White)
        clipPath(almendra) {
            if (oscuro) drawCircle(Brush.radialGradient(listOf(acento.copy(alpha = 0.6f), Color.Transparent), c, a * 0.75f), a * 0.75f, c)
            drawCircle(if (oscuro) acento else t(GRIS, false), a * 0.4f, c)
            drawCircle(Color.Black.copy(alpha = 0.25f), a * 0.4f, c, style = Stroke(a * 0.06f))
            drawCircle(NEGRO, a * 0.19f, c)
            drawCircle(Color.White.copy(alpha = 0.85f), a * 0.07f, Offset(c.x - a * 0.13f, c.y - a * 0.13f))
        }
        drawPath(almendra, NEGRO, style = Stroke(a * 0.1f))
    }

    /** El medallón oscuro con el ojo y sus rayos detrás, como sobre el archivo del Ideario. */
    private fun DrawScope.medallon(c: Offset, rx: Float, ry: Float, rayos: Float, oscuro: Boolean, acento: Color, u: Float) {
        if (rayos > 0f) for (k in 0 until 24) {
            val a = k * Math.PI / 12
            drawLine(t(GRIS_OSC, oscuro).copy(alpha = 0.38f), c, Offset(c.x + rayos * cos(a).toFloat(), c.y + rayos * 0.8f * sin(a).toFloat()), 0.4f * u)
        }
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(c.x - rx + 0.5f * u, c.y - ry + 0.7f * u), Size(rx * 2, ry * 2))
        drawOval(t(GRIS_OSC, oscuro), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(t(GRIS, oscuro), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(0.5f * u))
        ojo(c, minOf(rx * 0.74f, ry * 1.25f), oscuro, acento)
    }

    /** La banda roja de cartel con su estrella negra en la punta de afuera y la fila de rectángulos claros. */
    private fun DrawScope.banda(r: Rect, rojo: Color, oscuro: Boolean, estrellaIzq: Boolean, estrellaDer: Boolean, u: Float) {
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 0.4f * u, r.top + 0.7f * u), r.size)
        drawRect(Brush.verticalGradient(listOf(mezcla(rojo, Color.White, 0.08f), rojo, mezcla(rojo, Color.Black, 0.18f)), r.top, r.bottom), r.topLeft, r.size)
        drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, r.height * 0.12f))
        val sr = r.height * 0.36f
        val izq = r.left + (if (estrellaIzq) sr * 2.6f else sr * 0.8f)
        val der = r.right - (if (estrellaDer) sr * 2.6f else sr * 0.8f)
        if (estrellaIzq) drawPath(estrella(Offset(r.left + sr * 1.4f, r.center.y), sr), t(NEGRO, oscuro))
        if (estrellaDer) drawPath(estrella(Offset(r.right - sr * 1.4f, r.center.y), sr), t(NEGRO, oscuro))
        val paso = r.height * 0.62f
        val n = ((der - izq) / paso).toInt()
        if (n > 0) {
            val x0 = (izq + der) / 2 - n * paso / 2
            for (k in 0 until n) drawRect(t(PAPEL, oscuro), Offset(x0 + k * paso + paso * 0.14f, r.center.y - r.height * 0.15f), Size(paso * 0.72f, r.height * 0.3f))
        }
    }

    /** Una rejilla de ranuras redondeadas a lo ancho de [r]. */
    private fun DrawScope.ranuras(r: Rect, paso: Float, color: Color) {
        if (paso < 1f) return
        var x = r.left + paso * 0.5f
        while (x < r.right - paso * 0.3f) {
            drawRoundRect(color, Offset(x, r.top), Size(paso * 0.42f, r.height), CornerRadius(paso * 0.21f))
            x += paso
        }
    }

    /** Una carpeta de manila cerrada, con su pestaña, la franja roja y la etiqueta mecanografiada (girada [giro] grados). */
    private fun DrawScope.carpeta(c: Offset, an: Float, al: Float, giro: Float, oscuro: Boolean, rojo: Color, u: Float) {
        rotate(giro, c) {
            val r = Rect(c.x - an / 2, c.y - al / 2, c.x + an / 2, c.y + al / 2)
            drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
            drawRect(t(mezcla(MANILA, Color.Black, 0.1f), oscuro), Offset(r.left + an * 0.08f, r.top - al * 0.1f), Size(an * 0.32f, al * 0.14f))
            drawRect(t(MANILA, oscuro), r.topLeft, r.size)
            drawLine(Color.Black.copy(alpha = 0.18f), Offset(r.left, r.top + al * 0.12f), Offset(r.right, r.top + al * 0.12f), 0.3f * u)
            drawRect(t(PAPEL, oscuro), Offset(r.left + an * 0.22f, r.top + al * 0.32f), Size(an * 0.56f, al * 0.26f))
            drawRect(t(NEGRO, oscuro).copy(alpha = 0.7f), Offset(r.left + an * 0.27f, r.top + al * 0.42f), Size(an * 0.4f, al * 0.06f))
            clipRect(r.left, r.top, r.right, r.bottom) {
                drawLine(rojo, Offset(r.right - an * 0.3f, r.bottom), Offset(r.right, r.bottom - al * 0.45f), al * 0.12f)
            }
            drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(0.3f * u))
        }
    }

    /** Un sello de goma de oficina visto de lado: el mango rojo, el cuello y la base con su caucho. */
    private fun DrawScope.selloDeLado(base: Offset, alto: Float, oscuro: Boolean, rojo: Color) {
        val a = alto
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(base.x - a * 0.45f, base.y - a * 0.06f), Size(a * 0.9f, a * 0.14f))
        drawRect(t(NEGRO, oscuro), Offset(base.x - a * 0.36f, base.y - a * 0.14f), Size(a * 0.72f, a * 0.1f))
        drawRect(Brush.horizontalGradient(listOf(t(GRIS_OSC, oscuro), t(GRIS_LUZ, oscuro), t(GRIS_OSC, oscuro)), base.x - a * 0.36f, base.x + a * 0.36f), Offset(base.x - a * 0.36f, base.y - a * 0.3f), Size(a * 0.72f, a * 0.17f))
        drawRect(t(GRIS_OSC, oscuro), Offset(base.x - a * 0.07f, base.y - a * 0.62f), Size(a * 0.14f, a * 0.33f))
        drawCircle(Brush.radialGradient(listOf(mezcla(rojo, Color.White, 0.35f), rojo, mezcla(rojo, Color.Black, 0.35f)), Offset(base.x - a * 0.07f, base.y - a * 0.8f), a * 0.26f), a * 0.22f, Offset(base.x, base.y - a * 0.76f))
    }

    // ---------------------------------------------------------------- Social: la sala del comité

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val rojo = rojo(e.acento, oscuro)
        val n = enLienzo(area(MuebleVida.SOCIAL))
        val yP = h * 0.29f

        // El muro de bloques de hormigón, más oscuro hacia el suelo, con sus juntas trabadas.
        drawRect(Brush.verticalGradient(listOf(c(HORMIGON), mezcla(c(HORMIGON), Color.Black, 0.14f)), 0f, yP), Offset.Zero, Size(w, yP))
        val juntas = Path(); val juntasLuz = Path()
        val alto = h * 0.072f; val largo = w * 0.115f
        var fy = 0f; var k = 0
        while (fy < yP && k < 12) {
            juntas.moveTo(0f, fy); juntas.lineTo(w, fy); juntasLuz.moveTo(0f, fy + 0.5f * u); juntasLuz.lineTo(w, fy + 0.5f * u)
            var x = if (k % 2 == 0) largo * 0.3f else largo * 0.8f; var j = 0
            while (x < w && j < 30) { juntas.moveTo(x, fy); juntas.lineTo(x, minOf(fy + alto, yP)); x += largo; j++ }
            fy += alto; k++
        }
        drawPath(juntas, Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f), style = Stroke(0.35f * u))
        drawPath(juntasLuz, Color.White.copy(alpha = if (oscuro) 0.04f else 0.14f), style = Stroke(0.3f * u))

        // Las bandas rojas de cartel a los lados del medallón del ojo.
        val bt = h * 0.07f; val bb = h * 0.165f
        banda(Rect(w * 0.12f, bt, w * 0.405f, bb), rojo, oscuro, estrellaIzq = true, estrellaDer = false, u = u)
        banda(Rect(w * 0.595f, bt, w * 0.88f, bb), rojo, oscuro, estrellaIzq = false, estrellaDer = true, u = u)
        medallon(Offset(w / 2, h * 0.118f), w * 0.075f, h * 0.088f, h * 0.15f, oscuro, e.acento, u)

        // El altavoz del muro, a la izquierda, en su ménsula.
        val av = Offset(w * 0.055f, h * 0.1f)
        drawLine(c(GRIS_OSC), Offset(w * 0.02f, av.y - 3f * u), av, 0.9f * u)
        rotate(28f, av) {
            val bocina = Path().apply { moveTo(av.x, av.y - 1.2f * u); lineTo(av.x + 6.5f * u, av.y - 3.4f * u); lineTo(av.x + 6.5f * u, av.y + 3.4f * u); lineTo(av.x, av.y + 1.2f * u); close() }
            drawPath(bocina, Brush.verticalGradient(listOf(c(GRIS_LUZ), c(GRIS), c(GRIS_OSC)), av.y - 3.4f * u, av.y + 3.4f * u))
            drawOval(c(NEGRO), Offset(av.x + 5.8f * u, av.y - 3.4f * u), Size(1.5f * u, 6.8f * u))
            drawRoundRect(c(GRIS_OSC), Offset(av.x - 2.6f * u, av.y - 1.8f * u), Size(3f * u, 3.6f * u), CornerRadius(0.8f * u))
        }
        // La cámara de vigilancia en la esquina derecha, con su luz roja encendida.
        val cam = Offset(w * 0.935f, h * 0.085f)
        drawLine(c(GRIS_OSC), Offset(w - 0.5f * u, cam.y - 4f * u), Offset(cam.x + 2f * u, cam.y - 0.5f * u), 1f * u)
        drawRect(c(GRIS_OSC), Offset(w - 1.6f * u, cam.y - 6f * u), Size(1.6f * u, 4f * u))
        rotate(24f, cam) {
            drawRoundRect(Brush.verticalGradient(listOf(c(GRIS_LUZ), c(GRIS)), cam.y - 2.3f * u, cam.y + 2.3f * u), Offset(cam.x - 7.5f * u, cam.y - 2.3f * u), Size(9.5f * u, 4.6f * u), CornerRadius(0.8f * u))
            drawRect(c(GRIS_OSC), Offset(cam.x - 8.6f * u, cam.y - 2.8f * u), Size(2.2f * u, 5.6f * u))
            drawCircle(c(NEGRO), 1.3f * u, Offset(cam.x - 8.8f * u, cam.y))
            val led = Offset(cam.x - 0.4f * u, cam.y - 0.9f * u)
            drawCircle(Brush.radialGradient(listOf(e.acento.copy(alpha = if (oscuro) 0.7f else 0.4f), Color.Transparent), led, 3f * u), 3f * u, led)
            drawCircle(e.acento, 0.6f * u, led)
        }

        // El zócalo y el piso de linóleo en losetas, que fugan hacia el fondo.
        drawRect(Brush.verticalGradient(listOf(c(LINOLEO), mezcla(c(LINOLEO), Color.Black, 0.25f)), yP, h), Offset(0f, yP), Size(w, h - yP))
        val fuga = Offset(w / 2, -h * 0.9f)
        val losetas = Path()
        for (i in -9..9) {
            val xb = w / 2 + i * w * 0.12f
            val s = (yP - fuga.y) / (h - fuga.y)
            losetas.moveTo(fuga.x + (xb - fuga.x) * s, yP); losetas.lineTo(xb, h)
        }
        listOf(0.06f, 0.16f, 0.3f, 0.48f, 0.72f).forEach { f -> val y = yP + (h - yP) * f; losetas.moveTo(0f, y); losetas.lineTo(w, y) }
        drawPath(losetas, Color.Black.copy(alpha = if (oscuro) 0.25f else 0.13f), style = Stroke(0.3f * u))
        drawRect(c(GRIS_OSC), Offset(0f, yP - h * 0.018f), Size(w, h * 0.022f))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), Offset(0f, yP - h * 0.018f), Offset(w, yP - h * 0.018f), 0.35f * u)

        // Los respaldos de las sillas del fondo, que asoman detrás de la mesa.
        listOf(0.255f, 0.42f, 0.58f, 0.745f).forEach { f ->
            val cx = w * f
            val rb = Rect(cx - w * 0.048f, h * 0.18f, cx + w * 0.048f, h * 0.27f)
            listOf(-0.7f, 0.7f).forEach { s -> drawLine(c(GRIS_LUZ), Offset(cx + s * rb.width * 0.5f, rb.bottom - 1f * u), Offset(cx + s * rb.width * 0.5f, h * 0.3f), 0.6f * u) }
            drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(rb.left + 0.4f * u, rb.top + 0.6f * u), rb.size, CornerRadius(1.2f * u))
            val vinilo = c(mezcla(GRIS_OSC, e.secundario, 0.45f))
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(vinilo, Color.White, 0.16f), vinilo, mezcla(vinilo, Color.Black, 0.3f)), rb.top, rb.bottom), rb.topLeft, rb.size, CornerRadius(1.2f * u))
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(rb.left + 1.2f * u, rb.center.y), Offset(rb.right - 1.2f * u, rb.center.y), 0.3f * u)
            drawRoundRect(c(GRIS_LUZ).copy(alpha = 0.6f), rb.topLeft, rb.size, CornerRadius(1.2f * u), style = Stroke(0.3f * u))
        }

        // La mesa de acero del comité en perspectiva: la sombra, el tablero cepillado y el tapete.
        val yB = h * 0.285f; val yF = h * 0.87f
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.35f), Color.Transparent), yF, h), Offset(w * 0.02f, yF), Size(w * 0.96f, h - yF))
        val tablero = Path().apply { moveTo(w * 0.1f, yB); lineTo(w * 0.9f, yB); lineTo(w * 0.975f, yF); lineTo(w * 0.025f, yF); close() }
        drawPath(tablero, Brush.verticalGradient(listOf(c(GRIS_LUZ), c(GRIS)), yB, yF))
        val rnd = Random(e.semilla.toLong())
        clipPath(tablero) {
            for (i in 0 until 26) {
                val y = yB + (yF - yB) * rnd.nextFloat()
                drawLine(Color.White.copy(alpha = 0.05f + 0.06f * rnd.nextFloat()), Offset(0f, y), Offset(w, y), 0.3f * u)
            }
        }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.5f), Offset(w * 0.1f, yB), Offset(w * 0.9f, yB), 0.5f * u)
        // El tapete: hundido en el tablero, con su borde oscuro y una sombra bajo el canto de arriba.
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(n.left - 0.7f * u, n.top - 0.7f * u), Size(n.width + 1.4f * u, n.height + 1.4f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(n.left - 0.7f * u, n.bottom), Size(n.width + 1.4f * u, 0.7f * u))
        drawRect(nicho(e.fondo, MESA, oscuro), n.topLeft, n.size)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent), n.top, n.top + 2.5f * u), n.topLeft, Size(n.width, 2.5f * u))

        // El canto remachado y las patas.
        val canto = Rect(w * 0.025f, yF, w * 0.975f, yF + h * 0.05f)
        chapa(canto, c(GRIS_OSC), oscuro, u)
        var rx = canto.left + w * 0.04f
        while (rx < canto.right - w * 0.02f) { remache(Offset(rx, canto.center.y), 0.55f * u, oscuro); rx += w * 0.07f }
        listOf(0.055f, 0.945f).forEach { f ->
            val p = Rect(w * f - w * 0.013f, canto.bottom, w * f + w * 0.013f, h)
            drawRect(Brush.horizontalGradient(listOf(c(GRIS), c(GRIS_OSC)), p.left, p.right), p.topLeft, p.size)
        }

        // Sobre la mesa, fuera del tapete: el vaso de agua y los expedientes a la izquierda; el sello y el micrófono a la derecha.
        val vaso = Offset(w * 0.085f, h * 0.6f)
        val va = 8.5f * u; val vw = 4.2f * u
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(vaso.x - vw * 0.5f, vaso.y - 0.4f * u), Size(vw * 1.3f, 1.4f * u))
        val cuerpoVaso = Path().apply { moveTo(vaso.x - vw / 2, vaso.y - va); lineTo(vaso.x + vw / 2, vaso.y - va); lineTo(vaso.x + vw * 0.4f, vaso.y); lineTo(vaso.x - vw * 0.4f, vaso.y); close() }
        drawPath(cuerpoVaso, Color.White.copy(alpha = if (oscuro) 0.12f else 0.35f))
        clipPath(cuerpoVaso) { drawRect(mezcla(c(GRIS_LUZ), Color(0xFF8FB4C8), 0.4f).copy(alpha = 0.7f), Offset(vaso.x - vw, vaso.y - va * 0.6f), Size(vw * 2, va)) }
        drawPath(cuerpoVaso, Color.White.copy(alpha = if (oscuro) 0.3f else 0.8f), style = Stroke(0.35f * u))
        drawOval(Color.White.copy(alpha = if (oscuro) 0.3f else 0.8f), Offset(vaso.x - vw / 2, vaso.y - va - 0.6f * u), Size(vw, 1.2f * u), style = Stroke(0.3f * u))
        drawLine(Color.White.copy(alpha = 0.6f), Offset(vaso.x - vw * 0.28f, vaso.y - va * 0.85f), Offset(vaso.x - vw * 0.22f, vaso.y - va * 0.15f), 0.4f * u)
        carpeta(Offset(w * 0.09f, h * 0.78f), w * 0.1f, h * 0.075f, -6f, oscuro, rojo, u)
        carpeta(Offset(w * 0.085f, h * 0.755f), w * 0.1f, h * 0.075f, 3f, oscuro, rojo, u)
        selloDeLado(Offset(w * 0.888f, h * 0.5f), 7f * u, oscuro, rojo)
        // El micrófono del comité: la base, el cuello de ganso y la cabeza con su rejilla, y el piloto rojo.
        val mb = Offset(w * 0.915f, h * 0.8f)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(mb.x - 4f * u, mb.y - 0.4f * u), Size(8.5f * u, 2f * u))
        drawOval(Brush.verticalGradient(listOf(c(GRIS_LUZ), c(GRIS_OSC)), mb.y - 1.4f * u, mb.y + 1f * u), Offset(mb.x - 3.6f * u, mb.y - 1.4f * u), Size(7.2f * u, 2.4f * u))
        drawCircle(e.acento, 0.45f * u, Offset(mb.x + 2f * u, mb.y - 0.3f * u))
        val cuello = Path().apply { moveTo(mb.x, mb.y - 1f * u); cubicTo(mb.x + 0.5f * u, mb.y - 8f * u, mb.x - 3f * u, mb.y - 11f * u, mb.x - 5f * u, mb.y - 13f * u) }
        drawPath(cuello, c(NEGRO), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        rotate(-40f, Offset(mb.x - 5.5f * u, mb.y - 13.6f * u)) {
            drawRoundRect(Brush.horizontalGradient(listOf(c(GRIS_OSC), c(GRIS_LUZ), c(GRIS_OSC)), mb.x - 7f * u, mb.x - 4f * u), Offset(mb.x - 7f * u, mb.y - 16f * u), Size(3f * u, 4.6f * u), CornerRadius(1.5f * u))
            for (j in 0 until 3) drawLine(Color.Black.copy(alpha = 0.4f), Offset(mb.x - 6.6f * u, mb.y - (15f - j * 1.1f) * u), Offset(mb.x - 4.4f * u, mb.y - (15f - j * 1.1f) * u), 0.25f * u)
        }
    }

    // ---------------------------------------------------------------- Mapas: la mesa de planificación

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val rojo = rojo(e.acento, oscuro)
        val n = enLienzo(area(MuebleVida.MAPAS))
        val b = 3f * u

        // El tablero de la mesa visto desde arriba, de chapa verde gris cepillada, dentro de su marco de acero.
        drawRect(c(MESA))
        val rnd = Random(e.semilla.toLong())
        for (i in 0 until 40) {
            val y = h * rnd.nextFloat()
            drawLine((if (i % 3 == 0) Color.Black else Color.White).copy(alpha = 0.04f + 0.05f * rnd.nextFloat()), Offset(0f, y), Offset(w, y), (0.2f + 0.3f * rnd.nextFloat()) * u)
        }
        val dentro = Rect(b, b, w - b, h - b)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), dentro.top, dentro.top + 3f * u), dentro.topLeft, Size(dentro.width, 3f * u))
        // El marco: cuatro largueros con su moldura (luz por dentro, sombra por fuera) y escuadras atornilladas.
        val marco = c(GRIS_OSC)
        drawRect(marco, Offset.Zero, Size(w, b)); drawRect(marco, Offset(0f, h - b), Size(w, b))
        drawRect(marco, Offset.Zero, Size(b, h)); drawRect(marco, Offset(w - b, 0f), Size(b, h))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.1f else 0.28f), Offset(0f, 0f), Size(w, 0.5f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.2f), Offset(b - 0.6f * u, b - 0.6f * u), Size(w - 2 * b + 1.2f * u, 0.6f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.2f), Offset(b - 0.6f * u, b - 0.6f * u), Size(0.6f * u, h - 2 * b + 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.45f), Offset(0f, h - 0.6f * u), Size(w, 0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(b, h - b), Size(w - 2 * b, 0.5f * u))
        listOf(Offset(0f, 0f) to 0f, Offset(w, 0f) to 90f, Offset(w, h) to 180f, Offset(0f, h) to 270f).forEach { (p, g) ->
            rotate(g, p) {
                val esc = Path().apply { moveTo(p.x, p.y); lineTo(p.x + 9f * u, p.y); lineTo(p.x, p.y + 9f * u); close() }
                drawPath(esc, Brush.linearGradient(listOf(c(GRIS_LUZ), c(GRIS)), p, Offset(p.x + 6f * u, p.y + 6f * u)))
                drawPath(esc, Color.Black.copy(alpha = 0.35f), style = Stroke(0.3f * u))
                tornillo(Offset(p.x + 2.3f * u, p.y + 2.3f * u), 0.8f * u, oscuro)
            }
        }

        // El pliego del mapa: su sombra, el papel y las cantoneras de acero en las esquinas, por fuera.
        sombra(n, 2f * u, if (oscuro) 0.5f else 0.3f)
        drawRect(nicho(e.fondo, PAPEL, oscuro), n.topLeft, n.size)
        drawRect(Color.Black.copy(alpha = 0.3f), n.topLeft, n.size, style = Stroke(0.3f * u))
        val ca = 4.5f * u; val cg = 1.1f * u
        listOf(Offset(n.left, n.top) to Offset(-1f, -1f), Offset(n.right, n.top) to Offset(1f, -1f), Offset(n.right, n.bottom) to Offset(1f, 1f), Offset(n.left, n.bottom) to Offset(-1f, 1f)).forEach { (p, s) ->
            val l = Path().apply {
                moveTo(p.x + s.x * cg, p.y - s.y * ca); lineTo(p.x, p.y - s.y * ca); lineTo(p.x, p.y); lineTo(p.x - s.x * ca, p.y)
                lineTo(p.x - s.x * ca, p.y + s.y * cg); lineTo(p.x + s.x * cg, p.y + s.y * cg); close()
            }
            drawPath(l, c(GRIS_LUZ)); drawPath(l, Color.Black.copy(alpha = 0.4f), style = Stroke(0.25f * u))
            drawCircle(c(GRIS_OSC), 0.4f * u, Offset(p.x + s.x * cg * 0.5f, p.y + s.y * cg * 0.5f))
        }
        // La regla de acero sobre el borde de arriba, con sus marcas.
        val regla = Rect(n.left + 6f * u, n.top - 3.6f * u, n.left + n.width * 0.62f, n.top - 1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(regla.left + 0.3f * u, regla.top + 0.5f * u), regla.size)
        drawRect(Brush.verticalGradient(listOf(c(Color(0xFFD5D9DC)), c(GRIS_LUZ)), regla.top, regla.bottom), regla.topLeft, regla.size)
        var mx = regla.left + 1f * u; var mk = 0
        while (mx < regla.right - 0.5f * u && mk < 200) { drawLine(c(NEGRO).copy(alpha = 0.7f), Offset(mx, regla.bottom), Offset(mx, regla.bottom - (if (mk % 5 == 0) 1.2f else 0.6f) * u), 0.2f * u); mx += 1.1f * u; mk++ }

        // A la izquierda: los expedientes apilados, el tampón con el sello y la lupa.
        val iz = (b + n.left) / 2
        val anchoZona = n.left - b - 2f * u
        carpeta(Offset(iz, h * 0.25f), anchoZona * 0.86f, h * 0.17f, -9f, oscuro, rojo, u)
        carpeta(Offset(iz + 0.6f * u, h * 0.23f), anchoZona * 0.86f, h * 0.17f, -2f, oscuro, rojo, u)
        carpeta(Offset(iz - 0.4f * u, h * 0.21f), anchoZona * 0.86f, h * 0.17f, 6f, oscuro, rojo, u)
        // El tampón: la caja de lata abierta, su tinta y el sello visto desde arriba.
        val tp = Rect(iz - anchoZona * 0.42f, h * 0.44f, iz + anchoZona * 0.1f, h * 0.56f)
        sombra(tp, 1f * u, 0.3f)
        drawRoundRect(c(GRIS_OSC), tp.topLeft, tp.size, CornerRadius(0.8f * u))
        drawRoundRect(mezcla(c(NEGRO), rojo, 0.25f), Offset(tp.left + 0.8f * u, tp.top + 0.8f * u), Size(tp.width - 1.6f * u, tp.height - 1.6f * u), CornerRadius(0.5f * u))
        drawRoundRect(Color.White.copy(alpha = 0.18f), Offset(tp.left + 1.4f * u, tp.top + 1.2f * u), Size(tp.width * 0.5f, 0.5f * u), CornerRadius(0.3f * u))
        val sl = Offset(iz + anchoZona * 0.3f, h * 0.53f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(sl.x - 3.2f * u, sl.y - 2.4f * u), Size(7f * u, 6f * u))
        drawRect(c(GRIS), Offset(sl.x - 3.4f * u, sl.y - 3f * u), Size(6.8f * u, 6f * u))
        bisel(Rect(sl.x - 3.4f * u, sl.y - 3f * u, sl.x + 3.4f * u, sl.y + 3f * u), Color.White.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.35f), 0.4f * u)
        drawCircle(Brush.radialGradient(listOf(mezcla(rojo, Color.White, 0.4f), rojo, mezcla(rojo, Color.Black, 0.4f)), Offset(sl.x - 0.8f * u, sl.y - 0.8f * u), 2.8f * u), 2.3f * u, sl)
        // La lupa: el aro de acero, el cristal con su reflejo y el mango negro.
        val lc = Offset(iz - anchoZona * 0.1f, h * 0.71f); val lr = minOf(anchoZona * 0.3f, 6.5f * u)
        rotate(40f, lc) {
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(lc.x + lr * 0.9f, lc.y - 0.6f * u), Size(lr * 1.6f, 2.4f * u), CornerRadius(1.2f * u))
            drawRoundRect(c(NEGRO), Offset(lc.x + lr * 0.9f, lc.y - 1.1f * u), Size(lr * 1.6f, 2.2f * u), CornerRadius(1.1f * u))
            drawRect(c(GRIS_LUZ), Offset(lc.x + lr * 0.85f, lc.y - 0.8f * u), Size(1f * u, 1.6f * u))
        }
        drawCircle(Color.Black.copy(alpha = 0.25f), lr, Offset(lc.x + 0.6f * u, lc.y + 0.9f * u))
        drawCircle(mezcla(c(MESA), Color.White, if (oscuro) 0.12f else 0.35f), lr, lc)
        drawArc(Color.White.copy(alpha = if (oscuro) 0.3f else 0.7f), 200f, 70f, false, Offset(lc.x - lr * 0.7f, lc.y - lr * 0.7f), Size(lr * 1.4f, lr * 1.4f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
        drawCircle(c(GRIS_LUZ), lr, lc, style = Stroke(1f * u))
        drawCircle(Color.Black.copy(alpha = 0.3f), lr + 0.5f * u, lc, style = Stroke(0.25f * u))

        // A la derecha: el teléfono de disco, la bandeja de chinchetas de sector y el cenicero con su cigarro.
        val de = (n.right + w - b) / 2
        val anchoDer = w - b - n.right - 2f * u
        val tf = Offset(de, h * 0.27f)
        val tw = minOf(anchoDer * 0.82f, 17f * u); val th = tw * 0.95f
        val cuerpo = Rect(tf.x - tw / 2, tf.y - th / 2, tf.x + tw / 2, tf.y + th / 2)
        sombra(cuerpo, 1.5f * u, 0.4f)
        drawRoundRect(Brush.radialGradient(listOf(mezcla(c(NEGRO), Color.White, 0.18f), c(NEGRO)), Offset(tf.x - tw * 0.2f, tf.y - th * 0.2f), tw), cuerpo.topLeft, cuerpo.size, CornerRadius(tw * 0.22f))
        // El auricular, atravesado arriba: las dos copas y el mango.
        val ay = cuerpo.top + th * 0.08f
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(cuerpo.left - tw * 0.06f, ay + 0.5f * u), Size(tw * 1.12f, th * 0.18f), CornerRadius(th * 0.09f))
        drawRoundRect(c(NEGRO), Offset(cuerpo.left + tw * 0.15f, ay - th * 0.03f), Size(tw * 0.7f, th * 0.14f), CornerRadius(th * 0.07f))
        listOf(cuerpo.left - tw * 0.06f, cuerpo.right - tw * 0.24f).forEach { x ->
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(c(NEGRO), Color.White, 0.25f), c(NEGRO)), ay - th * 0.06f, ay + th * 0.18f), Offset(x, ay - th * 0.06f), Size(tw * 0.3f, th * 0.24f), CornerRadius(th * 0.08f))
        }
        // El disco: el plato cromado, los diez agujeros y el centro de papel.
        val dc = Offset(tf.x, tf.y + th * 0.12f); val dr = tw * 0.3f
        drawCircle(c(Color(0xFFCDD1D4)), dr, dc)
        for (j in 0 until 10) {
            val a = Math.toRadians(-60.0 + j * 27.0)
            drawCircle(c(NEGRO), dr * 0.16f, Offset(dc.x + dr * 0.7f * cos(a).toFloat(), dc.y + dr * 0.7f * sin(a).toFloat()))
        }
        drawCircle(papel(PAPEL, oscuro), dr * 0.36f, dc)
        drawCircle(rojo, dr * 0.1f, dc)
        drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(dc.x - dr, dc.y - dr), Size(dr * 2, dr * 2), style = Stroke(0.4f * u))
        // El cordón rizado que sale del teléfono.
        val cordon = Path(); var cx0 = cuerpo.right - tw * 0.1f; var cy0 = cuerpo.bottom
        cordon.moveTo(cx0, cy0)
        for (j in 0 until 7) { cordon.quadraticTo(cx0 + 1.6f * u, cy0 + 0.9f * u, cx0 + 0.6f * u, cy0 + 1.6f * u); cx0 += 0.6f * u; cy0 += 1.6f * u }
        drawPath(cordon, c(NEGRO), style = Stroke(0.5f * u))
        // La bandeja de chinchetas rojas de sector.
        val bj = Rect(de - anchoDer * 0.4f, h * 0.5f, de + anchoDer * 0.4f, h * 0.6f)
        sombra(bj, 1f * u, 0.3f)
        drawRect(c(GRIS_OSC), bj.topLeft, bj.size)
        drawRect(c(GRIS), Offset(bj.left + 0.7f * u, bj.top + 0.7f * u), Size(bj.width - 1.4f * u, bj.height - 1.4f * u))
        val rnd2 = Random(e.semilla * 7L + 3)
        for (j in 0 until 7) {
            val p = Offset(bj.left + 1.8f * u + (bj.width - 3.6f * u) * rnd2.nextFloat(), bj.top + 1.8f * u + (bj.height - 3.6f * u) * rnd2.nextFloat())
            drawCircle(Color.Black.copy(alpha = 0.35f), 1.1f * u, Offset(p.x + 0.3f * u, p.y + 0.4f * u))
            drawCircle(if (j % 3 == 2) c(NEGRO) else rojo, 1f * u, p)
            drawCircle(Color.White.copy(alpha = 0.55f), 0.35f * u, Offset(p.x - 0.3f * u, p.y - 0.3f * u))
        }
        // El cenicero de lata con el cigarro encendido y su humo.
        val ce = Offset(de, h * 0.73f); val cr = minOf(anchoDer * 0.32f, 6f * u)
        drawCircle(Color.Black.copy(alpha = 0.3f), cr, Offset(ce.x + 0.6f * u, ce.y + 0.9f * u))
        drawCircle(Brush.radialGradient(listOf(c(GRIS_LUZ), c(GRIS_OSC)), Offset(ce.x - cr * 0.3f, ce.y - cr * 0.3f), cr * 1.4f), cr, ce)
        drawCircle(mezcla(c(GRIS_OSC), Color.Black, 0.3f), cr * 0.7f, ce)
        drawCircle(c(Color(0xFF8E8E88)), cr * 0.25f, Offset(ce.x - cr * 0.15f, ce.y + cr * 0.1f))
        rotate(-25f, ce) {
            drawRect(c(PAPEL), Offset(ce.x - cr * 0.2f, ce.y - 0.5f * u), Size(cr * 1.4f, 1f * u))
            drawRect(c(Color(0xFFC98A3E)), Offset(ce.x + cr * 0.9f, ce.y - 0.5f * u), Size(cr * 0.3f, 1f * u))
            drawCircle(Color(0xFFFF7A2E).copy(alpha = 0.85f), 0.5f * u, Offset(ce.x - cr * 0.2f, ce.y))
        }
        val humo = Path().apply { moveTo(ce.x - cr * 0.2f, ce.y - 0.3f * u); cubicTo(ce.x - cr * 0.8f, ce.y - cr * 0.6f, ce.x + cr * 0.3f, ce.y - cr * 0.9f, ce.x - cr * 0.4f, ce.y - cr * 1.5f) }
        drawPath(humo, Color.White.copy(alpha = if (oscuro) 0.16f else 0.35f), style = Stroke(0.5f * u, cap = StrokeCap.Round))

        // Abajo: el lápiz amarillo con su goma y la ficha mecanografiada que asoma bajo el pliego.
        val fi = Rect(n.right - n.width * 0.3f, n.bottom, n.right - n.width * 0.08f, n.bottom + 4.5f * u)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(fi.left + 0.3f * u, fi.top), Size(fi.width, fi.height + 0.5f * u))
        drawRect(papel(PAPEL, oscuro), fi.topLeft, fi.size)
        drawLine(rojo, Offset(fi.left, fi.top + 1.2f * u), Offset(fi.right, fi.top + 1.2f * u), 0.3f * u)
        drawRect(c(NEGRO).copy(alpha = 0.6f), Offset(fi.left + 1f * u, fi.top + 2.2f * u), Size(fi.width * 0.5f, 0.6f * u))
        val ly = n.bottom + (h - b - n.bottom) * 0.55f
        val lx0 = n.left + n.width * 0.06f; val lx1 = n.left + n.width * 0.46f
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(lx0 + 0.4f * u, ly + 0.8f * u), Offset(lx1 + 0.4f * u, ly + 0.8f * u), 2f * u)
        drawRect(Brush.verticalGradient(listOf(mezcla(LAPIZ, Color.White, 0.3f), LAPIZ, mezcla(LAPIZ, Color.Black, 0.25f)).map { t(it, oscuro) }, ly - 1f * u, ly + 1f * u), Offset(lx0 + 3f * u, ly - 1f * u), Size(lx1 - lx0 - 5.6f * u, 2f * u))
        drawRect(c(Color(0xFFB9BDC0)), Offset(lx1 - 2.6f * u, ly - 1f * u), Size(1.2f * u, 2f * u))
        drawRoundRect(c(Color(0xFFD98A8A)), Offset(lx1 - 1.4f * u, ly - 1f * u), Size(1.6f * u, 2f * u), CornerRadius(0.5f * u))
        val punta = Path().apply { moveTo(lx0 + 3f * u, ly - 1f * u); lineTo(lx0, ly); lineTo(lx0 + 3f * u, ly + 1f * u); close() }
        drawPath(punta, c(Color(0xFFE6CFA2)))
        drawCircle(c(NEGRO), 0.45f * u, Offset(lx0 + 0.5f * u, ly))
    }

    // ---------------------------------------------------------------- Otras vidas: el gabinete de pruebas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val rojo = rojo(e.acento, oscuro)
        val n = enLienzo(area(MuebleVida.OTRAS_VIDAS))

        // El cuerpo del gabinete: acero gris con el reflejo de la luz cenital, como el archivero del Ideario.
        val cuerpo = Rect(0f, h * 0.24f, w, h * 0.9f)
        drawRect(Brush.horizontalGradient(listOf(c(GRIS_OSC), c(GRIS), c(GRIS_LUZ), c(GRIS), c(GRIS_OSC)), 0f, w), cuerpo.topLeft, cuerpo.size)
        // Los costados: la rejilla de ventilación, la placa de inventario y los remaches.
        listOf(0f to n.left, n.right to w).forEachIndexed { lado, (x0, x1) ->
            val cx = (x0 + x1) / 2; val an = x1 - x0
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(if (lado == 0) x1 - 0.5f * u else x0, cuerpo.top), Size(0.5f * u, cuerpo.height))
            for (j in 0 until 5) drawRoundRect(c(NEGRO).copy(alpha = 0.6f), Offset(cx - an * 0.3f, h * 0.36f + j * 2.4f * u), Size(an * 0.6f, 1.2f * u), CornerRadius(0.6f * u))
            remache(Offset(cx, h * 0.31f), 0.7f * u, oscuro)
            remache(Offset(cx, h * 0.84f), 0.7f * u, oscuro)
            if (lado == 0) drawRect(c(NEGRO).copy(alpha = 0.6f), Offset(cx - an * 0.3f, h * 0.66f), Size(an * 0.6f, 4f * u))
            else {
                drawRoundRect(c(GRIS_LUZ), Offset(cx - an * 0.18f, h * 0.62f), Size(an * 0.36f, 7f * u), CornerRadius(0.5f * u))
                drawCircle(c(NEGRO), 0.5f * u, Offset(cx, h * 0.62f + 2.4f * u))
                drawRect(c(NEGRO), Offset(cx - 0.2f * u, h * 0.62f + 2.4f * u), Size(0.4f * u, 1.6f * u))
            }
        }

        // El nicho: el fondo forrado de los cinco casilleros, cada uno hundido, con montantes remachados entre ellos.
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(n.left - 0.6f * u, n.top - 0.6f * u), Size(n.width + 1.2f * u, n.height + 1.2f * u))
        drawRect(nicho(e.fondo, HORMIGON, oscuro), n.topLeft, n.size)
        val cw = n.width / 5f
        val mont = 1.3f * u
        for (i in 0 until 5) {
            val r = Rect(n.left + cw * i, n.top, n.left + cw * (i + 1), n.bottom)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Color.Transparent), r.top, r.top + 4f * u), r.topLeft, Size(r.width, 4f * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), r.left, r.left + 2.5f * u), r.topLeft, Size(2.5f * u, r.height))
            // La repisa de cada casillero y la etiqueta de inventario de manila arriba a la izquierda.
            drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 0.4f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(r.left, r.bottom - 0.8f * u), Size(r.width, 0.8f * u))
            val et = Rect(r.left + mont + 1f * u, r.top + 1.2f * u, r.left + mont + 1f * u + minOf(7f * u, r.width * 0.2f), r.top + 4.2f * u)
            drawRect(c(MANILA), et.topLeft, et.size)
            drawRect(c(NEGRO).copy(alpha = 0.7f), Offset(et.left + 0.8f * u, et.center.y - 0.3f * u), Size(et.width * 0.6f, 0.6f * u))
            if (i == 2) drawRect(rojo, Offset(et.left, et.top), Size(0.8f * u, et.height))
        }
        for (i in 1 until 5) {
            val x = n.left + cw * i
            val m = Rect(x - mont / 2, n.top, x + mont / 2, n.bottom)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(m.right, m.top), Size(0.6f * u, m.height))
            drawRect(Brush.horizontalGradient(listOf(c(GRIS_LUZ), c(GRIS), c(GRIS_OSC)), m.left, m.right), m.topLeft, m.size)
        }

        // La cornisa de acero con los rayos del ojo, la banda roja con estrellas y el medallón.
        val cor = Rect(0f, 0f, w, h * 0.13f)
        chapa(cor, c(GRIS), oscuro, u)
        ranuras(Rect(w * 0.04f, h * 0.035f, w * 0.36f, h * 0.085f), 2.6f * u, c(NEGRO).copy(alpha = 0.55f))
        ranuras(Rect(w * 0.64f, h * 0.035f, w * 0.96f, h * 0.085f), 2.6f * u, c(NEGRO).copy(alpha = 0.55f))
        val bandaR = Rect(0f, h * 0.13f, w, h * 0.245f)
        banda(bandaR, rojo, oscuro, estrellaIzq = true, estrellaDer = true, u = u)
        drawRect(c(GRIS_OSC), Offset(0f, h * 0.245f), Size(w, h * 0.022f))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.3f), Offset(0f, h * 0.245f), Offset(w, h * 0.245f), 0.3f * u)
        medallon(Offset(w / 2, h * 0.13f), w * 0.07f, h * 0.105f, 0f, oscuro, e.acento, u)

        // El zócalo ranurado y las patas.
        val zo = Rect(0f, h * 0.9f, w, h)
        drawRect(c(GRIS_OSC), zo.topLeft, zo.size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.3f), Offset(0f, zo.top), Offset(w, zo.top), 0.4f * u)
        ranuras(Rect(w * 0.06f, zo.top + zo.height * 0.3f, w * 0.94f, zo.top + zo.height * 0.7f), 2.4f * u, c(NEGRO))
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f // un dp del ícono (el lienzo mide ≈ 70 dp de alto)
        val c = Offset(w / 2, h / 2)
        val rojo = rojo(e.c1, oscuro)
        fun k(x: Color) = t(x, oscuro)
        when (id) {
            "unesco" -> {
                // La pirámide escalonada del Ministerio, de hormigón blanco, con sus reflectores y los estandartes rojos.
                val base = c.y + 27f * s
                listOf(-1f, 1f).forEach { lado ->
                    val haz = Path().apply { moveTo(c.x + lado * 8f * s, base - 2f * s); lineTo(c.x + lado * 44f * s, c.y - 30f * s); lineTo(c.x + lado * 30f * s, c.y - 32f * s); close() }
                    drawPath(haz, Brush.linearGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.22f else 0.6f), Color.Transparent), Offset(c.x, base), Offset(c.x + lado * 40f * s, c.y - 30f * s)))
                }
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(c.x - 34f * s, base - 2f * s), Size(68f * s, 4.5f * s))
                val pisos = 4
                for (p in 0 until pisos) {
                    val an = (62f - p * 13f) * s; val al = 11f * s
                    val r = Rect(c.x - an / 2, base - al * (p + 1), c.x + an / 2, base - al * p)
                    drawRect(Brush.horizontalGradient(listOf(k(Color(0xFFF2F0EA)), k(HORMIGON), k(Color(0xFF8E8C84))), r.left, r.right), r.topLeft, r.size)
                    drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom - 1.2f * s), Size(r.width, 1.2f * s))
                    var x = r.left + 2.5f * s
                    while (x < r.right - 3f * s) { drawRect(k(GRIS_OSC), Offset(x, r.top + al * 0.35f), Size(1.4f * s, al * 0.4f)); x += 3.4f * s }
                }
                // El cubo de arriba con el ojo.
                val cubo = Rect(c.x - 7f * s, base - 44f * s - 10f * s, c.x + 7f * s, base - 44f * s)
                drawRect(k(GRIS_OSC), cubo.topLeft, cubo.size)
                ojo(Offset(c.x, cubo.center.y), 5.2f * s, oscuro, e.c1)
                // Los estandartes rojos que cuelgan por el frente.
                listOf(-17f, 17f).forEach { dx ->
                    val st = Path().apply { moveTo(c.x + dx * s - 3f * s, base - 33f * s); lineTo(c.x + dx * s + 3f * s, base - 33f * s); lineTo(c.x + dx * s + 3f * s, base - 13f * s); lineTo(c.x + dx * s, base - 15.5f * s); lineTo(c.x + dx * s - 3f * s, base - 13f * s); close() }
                    drawPath(st, rojo)
                    drawPath(estrella(Offset(c.x + dx * s, base - 28f * s), 1.8f * s), k(NEGRO))
                }
                drawRect(k(GRIS_OSC), Offset(c.x - 4f * s, base - 7f * s), Size(8f * s, 7f * s))
            }
            "rascar" -> {
                // El expediente de manila abierto con el plano dentro y la barra negra de la censura, raspada con una moneda.
                rotate(-6f, c) {
                    val car = Rect(c.x - 38f * s, c.y - 24f * s, c.x + 38f * s, c.y + 26f * s)
                    drawRect(Color.Black.copy(alpha = 0.25f), Offset(car.left + 1.5f * s, car.top + 2f * s), car.size)
                    drawRect(k(mezcla(MANILA, Color.Black, 0.1f)), Offset(car.left + 4f * s, car.top - 4f * s), Size(20f * s, 5f * s))
                    drawRect(k(MANILA), car.topLeft, car.size)
                    val hoja = Rect(car.left + 5f * s, car.top + 4f * s, car.right - 5f * s, car.bottom - 4f * s)
                    drawRect(papel(PAPEL, oscuro), hoja.topLeft, hoja.size)
                    // El plano: la costa y las fronteras, en el secundario.
                    val costa = Path().apply {
                        moveTo(hoja.left + 4f * s, hoja.top + 10f * s); cubicTo(hoja.left + 20f * s, hoja.top + 2f * s, hoja.left + 30f * s, hoja.top + 18f * s, hoja.left + 44f * s, hoja.top + 8f * s)
                        cubicTo(hoja.left + 54f * s, hoja.top + 3f * s, hoja.right - 6f * s, hoja.top + 14f * s, hoja.right - 4f * s, hoja.top + 22f * s)
                        lineTo(hoja.right - 4f * s, hoja.bottom - 4f * s); lineTo(hoja.left + 4f * s, hoja.bottom - 4f * s); close()
                    }
                    drawPath(costa, e.c2.copy(alpha = 0.12f)); drawPath(costa, e.c2.copy(alpha = 0.7f), style = Stroke(0.8f * s))
                    drawLine(e.c2.copy(alpha = 0.5f), Offset(hoja.left + 30f * s, hoja.top + 16f * s), Offset(hoja.left + 34f * s, hoja.bottom - 4f * s), 0.6f * s)
                    // La barra de censura con el tramo ya raspado, de bordes irregulares.
                    val barra = Rect(hoja.left + 2f * s, hoja.top + 22f * s, hoja.right - 2f * s, hoja.top + 33f * s)
                    drawRect(k(NEGRO), barra.topLeft, barra.size)
                    val rasp = Path().apply {
                        moveTo(barra.left + 8f * s, barra.top); lineTo(barra.left + 34f * s, barra.top); lineTo(barra.left + 36f * s, barra.top + 3f * s); lineTo(barra.left + 33f * s, barra.top + 6f * s)
                        lineTo(barra.left + 37f * s, barra.bottom); lineTo(barra.left + 10f * s, barra.bottom); lineTo(barra.left + 6f * s, barra.top + 5f * s); close()
                    }
                    drawPath(rasp, papel(PAPEL, oscuro))
                    clipPath(rasp) { drawPath(costa, e.c2.copy(alpha = 0.12f)); drawCircle(rojo, 2.2f * s, Offset(barra.left + 22f * s, barra.center.y)) }
                    for (j in 0 until 4) drawCircle(k(NEGRO).copy(alpha = 0.7f), 0.6f * s, Offset(barra.left + (38f + j * 2.2f) * s, barra.top + (2f + j * 2f) * s))
                    drawRect(rojo, Offset(car.right - 18f * s, car.top + 1.5f * s), Size(14f * s, 2f * s))
                }
                // La moneda del Estado, con su estrella, que raspa.
                val mo = Offset(c.x + 22f * s, c.y + 8f * s)
                drawCircle(Color.Black.copy(alpha = 0.3f), 8.5f * s, Offset(mo.x + 1f * s, mo.y + 1.5f * s))
                drawCircle(Brush.radialGradient(listOf(k(Color(0xFFE4E6E8)), k(GRIS_LUZ), k(GRIS)), Offset(mo.x - 3f * s, mo.y - 3f * s), 11f * s), 8.5f * s, mo)
                drawCircle(k(GRIS_OSC), 8.5f * s, mo, style = Stroke(0.8f * s))
                drawCircle(k(GRIS_OSC), 6.6f * s, mo, style = Stroke(0.4f * s))
                drawPath(estrella(mo, 4.2f * s), k(GRIS_OSC))
            }
            else -> {
                // El plano de la ciudad en su placa de acero, con las chinchetas rojas unidas por el hilo de la investigación.
                val pl = Rect(c.x - 40f * s, c.y - 27f * s, c.x + 40f * s, c.y + 27f * s)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(pl.left + 1.5f * s, pl.top + 2f * s), pl.size)
                drawRect(k(GRIS), pl.topLeft, pl.size)
                bisel(pl, Color.White.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.35f), 1f * s)
                listOf(Offset(pl.left + 2.5f * s, pl.top + 2.5f * s), Offset(pl.right - 2.5f * s, pl.top + 2.5f * s), Offset(pl.left + 2.5f * s, pl.bottom - 2.5f * s), Offset(pl.right - 2.5f * s, pl.bottom - 2.5f * s)).forEach { remache(it, 1f * s, oscuro) }
                val ma = Rect(pl.left + 6f * s, pl.top + 5f * s, pl.right - 6f * s, pl.bottom - 5f * s)
                drawRect(papel(PAPEL, oscuro), ma.topLeft, ma.size)
                clipRect(ma.left, ma.top, ma.right, ma.bottom) {
                    // Las manzanas en retícula, el río y la avenida diagonal.
                    for (i in 0 until 9) for (j in 0 until 5) drawRect(k(Color(0xFFC9C6BC)), Offset(ma.left + 1f * s + i * 7.6f * s, ma.top + 1f * s + j * 9f * s), Size(6.2f * s, 7.4f * s))
                    drawLine(e.c2.copy(alpha = 0.7f), Offset(ma.left, ma.bottom - 10f * s), Offset(ma.right, ma.top + 14f * s), 4f * s)
                    drawLine(papel(PAPEL, oscuro), Offset(ma.left, ma.top + 6f * s), Offset(ma.right, ma.bottom - 4f * s), 2f * s)
                }
                val pins = listOf(Offset(ma.left + 12f * s, ma.top + 10f * s), Offset(ma.left + 40f * s, ma.top + 30f * s), Offset(ma.right - 10f * s, ma.top + 9f * s), Offset(ma.left + 26f * s, ma.bottom - 6f * s))
                val hilo = Path().apply { moveTo(pins[0].x, pins[0].y); pins.drop(1).forEach { lineTo(it.x, it.y) } }
                drawPath(hilo, rojo, style = Stroke(0.7f * s))
                pins.forEach { p ->
                    drawCircle(Color.Black.copy(alpha = 0.35f), 3.2f * s, Offset(p.x + 1f * s, p.y + 1.4f * s))
                    drawCircle(Brush.radialGradient(listOf(mezcla(rojo, Color.White, 0.45f), rojo, mezcla(rojo, Color.Black, 0.3f)), Offset(p.x - 1f * s, p.y - 1f * s), 3.6f * s), 3f * s, p)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el tablón de expedientes

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val rojo = rojo(e.acento, oscuro)
        val n = enLienzo(area(MuebleVida.NOTAS))

        // El marco de acero del tablón, con su moldura y los tornillos en las esquinas.
        val marco = Rect(0f, h * 0.09f, w, h * 0.935f)
        drawRect(Brush.horizontalGradient(listOf(c(GRIS_OSC), c(GRIS), c(GRIS_LUZ), c(GRIS), c(GRIS_OSC)), 0f, w), marco.topLeft, marco.size)
        bisel(marco, Color.White.copy(alpha = if (oscuro) 0.08f else 0.25f), Color.Black.copy(alpha = 0.4f), 0.6f * u)
        listOf(n.left / 2, w - (w - n.right) / 2).forEach { x ->
            for (j in 0 until 4) drawRoundRect(c(NEGRO).copy(alpha = 0.55f), Offset(x - 0.5f * u, h * 0.42f + j * 3f * u), Size(1f * u, 2f * u), CornerRadius(0.5f * u))
            tornillo(Offset(x, h * 0.2f), 0.9f * u, oscuro)
            tornillo(Offset(x, h * 0.86f), 0.9f * u, oscuro, 70f)
        }
        // El fieltro gris del tablón: sólido, con la fibra apenas marcada y la sombra del marco por dentro.
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(n.left - 0.6f * u, n.top - 0.6f * u), Size(n.width + 1.2f * u, n.height + 1.2f * u))
        drawRect(nicho(e.fondo, GRIS_LUZ, oscuro), n.topLeft, n.size)
        val rnd = Random(e.semilla.toLong())
        for (i in 0 until 160) {
            val p = Offset(n.left + n.width * rnd.nextFloat(), n.top + n.height * rnd.nextFloat())
            drawLine((if (i % 2 == 0) Color.Black else Color.White).copy(alpha = 0.06f), p, Offset(p.x + (rnd.nextFloat() - 0.5f) * 1.6f * u, p.y + (rnd.nextFloat() - 0.5f) * 1.6f * u), 0.2f * u)
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Color.Transparent), n.top, n.top + 2.5f * u), n.topLeft, Size(n.width, 2.5f * u))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), Color.Transparent), n.left, n.left + 2f * u), n.topLeft, Size(2f * u, n.height))

        // La cabecera: la banda roja con estrellas y el medallón del ojo.
        val cab = Rect(0f, 0f, w, h * 0.105f)
        banda(cab, rojo, oscuro, estrellaIzq = true, estrellaDer = true, u = u)
        drawRect(c(GRIS_OSC), Offset(0f, cab.bottom - 0.4f * u), Size(w, 1f * u))
        medallon(Offset(w / 2, h * 0.07f), w * 0.07f, h * 0.06f, 0f, oscuro, e.acento, u)

        // La repisa de abajo, con el sello, el tampón y el lápiz.
        val rep = Rect(0f, h * 0.935f, w, h)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, rep.top), Size(w, 0.8f * u))
        chapa(rep, c(GRIS), oscuro, u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f), Offset(0f, rep.top + 0.6f * u), Offset(w, rep.top + 0.6f * u), 0.3f * u)
        selloDeLado(Offset(w * 0.2f, rep.top + 0.4f * u), 5.4f * u, oscuro, rojo)
        drawRoundRect(c(GRIS_OSC), Offset(w * 0.25f, rep.top - 1.4f * u), Size(7f * u, 1.6f * u), CornerRadius(0.4f * u))
        drawRect(mezcla(c(NEGRO), rojo, 0.3f), Offset(w * 0.25f + 0.6f * u, rep.top - 1.3f * u), Size(5.8f * u, 0.5f * u))
        val ly = rep.top - 0.7f * u
        drawRect(c(LAPIZ), Offset(w * 0.62f, ly - 0.7f * u), Size(w * 0.18f, 1.4f * u))
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(w * 0.62f, ly + 0.2f * u), Size(w * 0.18f, 0.5f * u))
        drawPath(Path().apply { moveTo(w * 0.62f, ly - 0.7f * u); lineTo(w * 0.62f - 2.2f * u, ly); lineTo(w * 0.62f, ly + 0.7f * u); close() }, c(Color(0xFFE6CFA2)))
        drawRoundRect(c(Color(0xFFD98A8A)), Offset(w * 0.8f, ly - 0.7f * u), Size(1.4f * u, 1.4f * u), CornerRadius(0.4f * u))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        val u = minOf(r0.width, r0.height) / 100f
        val rojo = rojo(e.acento, oscuro)
        val gir = if (e.fijada) 0f else ((e.indice * 37) % 5 - 2) * 0.6f
        val r = Rect(r0.left + r0.width * 0.03f, r0.top + r0.height * 0.03f, r0.right - r0.width * 0.03f, r0.bottom - r0.height * 0.04f)
        val base = when (e.tipo) {
            "pensamiento" -> MANILA
            "recordatorio" -> ROSA
            "proposito" -> Color(0xFFF4F1E8)
            else -> PAPEL
        }
        val hoja = papel(base, oscuro)
        val tinta = t(NEGRO, oscuro)
        rotate(gir, r.center) {
            // La sombra sobre el fieltro y el papel.
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(r.left + 1.2f * u, r.top + 2f * u), r.size)
            when (e.tipo) {
                "pensamiento" -> {
                    // La carpetilla de manila: la pestaña arriba a la derecha y el doblez.
                    drawRect(mezcla(hoja, Color.Black, 0.1f), Offset(r.right - r.width * 0.4f, r.top - 0f), Size(r.width * 0.32f, r.height * 0.08f))
                    drawRect(hoja, Offset(r.left, r.top + r.height * 0.05f), Size(r.width, r.height * 0.95f))
                    drawLine(Color.Black.copy(alpha = 0.15f), Offset(r.left, r.top + r.height * 0.11f), Offset(r.right, r.top + r.height * 0.11f), 0.5f * u)
                }
                else -> drawRect(hoja, r.topLeft, r.size)
            }
            when (e.tipo) {
                "idea" -> {
                    // La ficha rayada: la línea roja de arriba y las rayas azul gris, muy tenues.
                    drawLine(rojo.copy(alpha = 0.8f), Offset(r.left, r.top + r.height * 0.13f), Offset(r.right, r.top + r.height * 0.13f), 0.8f * u)
                    var y = r.top + r.height * 0.26f
                    while (y < r.bottom - r.height * 0.1f) { drawLine(t(mezcla(Color(0xFF7FA0B8), e.fondo, 0.2f), oscuro).copy(alpha = 0.3f), Offset(r.left, y), Offset(r.right, y), 0.4f * u); y += r.height * 0.13f }
                }
                "proposito" -> {
                    // El formulario: la franja negra de cabecera y las casillas del margen.
                    drawRect(tinta, Offset(r.left, r.top), Size(r.width, r.height * 0.07f))
                    for (j in 0 until 4) drawRect(tinta.copy(alpha = 0.55f), Offset(r.left + r.width * 0.025f, r.top + r.height * (0.15f + j * 0.15f)), Size(r.width * 0.045f, r.width * 0.045f), style = Stroke(0.45f * u))
                    drawLine(tinta.copy(alpha = 0.3f), Offset(r.left + r.width * 0.12f, r.bottom - r.height * 0.08f), Offset(r.right - r.width * 0.1f, r.bottom - r.height * 0.08f), 0.4f * u)
                }
                "recordatorio" -> {
                    // La copia rosada: el sello circular de «urgente» en la esquina de abajo.
                    val sc = Offset(r.right - r.width * 0.13f, r.bottom - r.height * 0.1f)
                    drawCircle(rojo.copy(alpha = 0.55f), r.width * 0.075f, sc, style = Stroke(0.7f * u))
                    drawLine(rojo.copy(alpha = 0.55f), Offset(sc.x - r.width * 0.05f, sc.y), Offset(sc.x + r.width * 0.05f, sc.y), 0.7f * u)
                    for (j in 0 until 3) drawLine(Color.Black.copy(alpha = 0.06f), Offset(r.left, r.top + r.height * (0.3f + j * 0.2f)), Offset(r.right, r.top + r.height * (0.32f + j * 0.2f)), 0.3f * u)
                }
                else -> {
                    // La etiqueta censurada de la pestaña.
                    drawRect(tinta.copy(alpha = 0.75f), Offset(r.right - r.width * 0.36f, r.top + r.height * 0.015f), Size(r.width * 0.2f, r.height * 0.03f))
                }
            }
            drawRect(Color.Black.copy(alpha = 0.18f), r.topLeft, r.size, style = Stroke(0.3f * u))
            if (e.fijada) {
                // La franja roja de «clasificado» en la esquina y la chincheta roja arriba.
                clipRect(r.left, r.top, r.right, r.bottom) {
                    drawLine(rojo.copy(alpha = 0.85f), Offset(r.right - r.width * 0.22f, r.top - 1f * u), Offset(r.right + 1f * u, r.top + r.height * 0.2f), r.width * 0.06f)
                }
                val pc = Offset(r.center.x, r.top + r.height * 0.065f)
                drawCircle(Color.Black.copy(alpha = 0.35f), 3.6f * u, Offset(pc.x + 1.2f * u, pc.y + 1.8f * u))
                drawCircle(Brush.radialGradient(listOf(mezcla(rojo, Color.White, 0.45f), rojo, mezcla(rojo, Color.Black, 0.35f)), Offset(pc.x - 1.2f * u, pc.y - 1.2f * u), 4.5f * u), 3.6f * u, pc)
                drawCircle(Color.White.copy(alpha = 0.6f), 1f * u, Offset(pc.x - 1.2f * u, pc.y - 1.2f * u))
            } else {
                // Un clip de acero sobre el borde de arriba, a la izquierda.
                val cx = r.left + r.width * 0.18f
                val clip = Path().apply {
                    moveTo(cx, r.top + r.height * 0.1f); lineTo(cx, r.top - r.height * 0.02f)
                    quadraticTo(cx, r.top - r.height * 0.05f, cx + r.width * 0.04f, r.top - r.height * 0.05f)
                    quadraticTo(cx + r.width * 0.08f, r.top - r.height * 0.05f, cx + r.width * 0.08f, r.top - r.height * 0.02f)
                    lineTo(cx + r.width * 0.08f, r.top + r.height * 0.13f)
                    quadraticTo(cx + r.width * 0.08f, r.top + r.height * 0.16f, cx + r.width * 0.05f, r.top + r.height * 0.16f)
                    quadraticTo(cx + r.width * 0.02f, r.top + r.height * 0.16f, cx + r.width * 0.02f, r.top + r.height * 0.13f)
                    lineTo(cx + r.width * 0.02f, r.top + r.height * 0.02f)
                }
                drawPath(clip, Color.Black.copy(alpha = 0.3f), style = Stroke(1.1f * u), alpha = 1f)
                drawPath(clip, t(Color(0xFFC5CACE), oscuro), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            }
        }
    }

    /** Tinta negra de máquina de escribir: el papel de las notas queda claro de día y en penumbra de noche. */
    override fun tintaNota(oscuro: Boolean): Color = NEGRO
}
