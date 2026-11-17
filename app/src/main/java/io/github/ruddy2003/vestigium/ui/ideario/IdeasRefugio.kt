package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Refugio
// =====================================================================

/**
 * Refugio: el rincón de registros de un refugio subterráneo de mediados del siglo XX, limpio, ordenado y un poco ingenuo. Es
 * un interior sin ventana: un muro de bloques de hormigón pintado de crema, con el zócalo verde azulado, desconchones, una
 * flecha y un número de sector pintados con plantilla, la canaleta de cables con sus grapas de lado a lado y una tubería que
 * baja hasta el armario con su llave. De día (el «día» del refugio) la luz es la de las luminarias del techo, fuera del cuadro
 * arriba a la izquierda, y el armario deja su sombra en el muro. El armario es de acero verde oliva remachado: arriba, un
 * panel con la rejilla de ventilación, la placa de inventario, la pegatina de revisión con sus casillas y tres pilotos ámbar;
 * cada ranura con su marco; la pintura saltada en los cantos, un golpe y el zócalo con su rejilla. Encima, la terminal de tubo
 * de fósforo verde (el punto focal, encendida con sus renglones ilegibles) con su teclado delante; a su izquierda, una pila de
 * latas de ración sin marca; a la derecha, el contador de radiación ocre con su aguja, su sonda y el cable en espiral, un termo
 * con la taza delante y la fiambrera de chapa. En la pared, el casco colgado de un gancho, un cartel de seguridad de formas
 * geométricas pegado con cinta y, a la derecha, la repisa con una tomatera en una lata bajo la lámpara de cultivo colgada de la
 * canaleta. Al pie, la caja de herramientas y una bombona con su manómetro, cortadas por los bordes, sobre el piso de rejilla.
 * Cada tema es, según la semilla, un cartucho de cinta en su ranura (con su ventana de carretes: más cinta cuanto más guarda,
 * y su LED), una pila de latas de bobina (más latas cuanto más guarda) o un módulo de válvulas con asa (se ven las válvulas
 * tras la rejilla, encendidas en naranja, más cuanto más guarda); todos con la etiqueta del tema. Lo que no tiene tema va en una
 * caja de tarjetas perforadas sin rotular; una ranura sin tema lleva su tapa ciega y un piloto apagado. Al abrir uno, sale de la
 * ranura hacia arriba. De noche (el turno de noche, con las luces bajas) todo se hunde en verde negro azulado: alumbra el
 * fósforo de la terminal (el teclado, la pared y las primeras filas, cada fila más apagada), la lámpara de cultivo deja un
 * charco rosa violeta sobre la tomatera, y brillan los pilotos ámbar y las válvulas; lo de abajo casi se pierde. Por dentro, una
 * pantalla de fósforo verde con su bisel. Las fichas son registros en una pantalla CRT de esquinas redondas.
 */
object IdeasRefugio : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del refugio
    /** El muro de bloques pintado de crema, su sombra y el hormigón que asoma donde saltó la pintura. */
    private val CREMA = Color(0xFFE2D6B6)
    private val CREMA_OSC = Color(0xFFBFB08A)
    private val HORMIGON = Color(0xFF8F8B7E)
    /** El verde azulado del zócalo, de la fiambrera, de la bombona y de la carcasa de la terminal. */
    private val TEAL = Color(0xFF3E7470)
    private val CARCASA = Color(0xFF6F837B)
    /** El acero oliva del armario (el de siempre) y sus tonos. */
    private val OLIVA = Color(0xFF4A5A3A)
    private val OLIVA_LUZ = Color(0xFF6A7A52)
    private val OLIVA_OSC = Color(0xFF2A3420)
    private val ACERO = Color(0xFF7A8088)
    private val ACERO_OSC = Color(0xFF4A5056)
    /** Los pilotos, el ocre de la plantilla y del contador, el naranja de las válvulas y de la caja de herramientas. */
    private val AMBAR = Color(0xFFFFB23F)
    private val OCRE = Color(0xFFC79B3C)
    private val NARANJA = Color(0xFFD9652C)
    /** El fósforo verde de la terminal y el vidrio apagado de su pantalla. */
    private val FOSFORO = Color(0xFF3DFF7F)
    private val FOSFORO_CLARO = Color(0xFF9CF7B4)
    private val PANTALLA = Color(0xFF0E2D19)
    private val NEGRO = Color(0xFF031A0B)
    /** La hoja de la tomatera. */
    private val HOJA = Color(0xFF5C7F3E)
    /** El papel de las etiquetas y del cartel, y la tinta. */
    private val ETIQUETA = Color(0xFFE8E0C8)
    private val TINTA = Color(0xFF1A1A14)
    /**
     * La etiqueta de noche lejos de la terminal: con [TINTA] da 7:1 en el título y 5:1 en la cuenta (al 78 %); cerca de la
     * terminal sube hacia [ETIQUETA] con un poco de fósforo.
     */
    private val ETIQUETA_LEJOS = Color(0xFFA39E88)
    /** La luz rosa violeta de la lámpara de cultivo. */
    private val LAMPARA = Color(0xFFE99BD9)
    /** La noche del refugio: verde negro azulado. */
    private val NOCHE = Color(0xFF0A1614)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 1.3f
    override val corona = 0.66f
    override val base = 0.26f
    override val lado = 0.09f
    override val hueco = 0.05f

    /** En la corona: el fondo de la tapa del armario, su canto de frente y lo bajo del canto, en fracción de su alto. */
    private const val SUP = 0.87f
    private const val FRENTE = 0.905f
    private const val CANTO = 0.945f
    /** En la base: el rincón del muro con el piso, el fondo del zócalo del armario y donde pisan las cosas del pie. */
    private const val SUELO = 0.32f
    private const val PISAN = 0.52f
    private const val PIE = 0.95f
    /** Los costados del armario, en fracción del ancho. */
    private const val IZQ = 0.04f
    private const val DER = 0.96f
    /** El piso de cada ranura (la guía) y cuánto sube su contenido al abrirla, en fracción de su alto. */
    private const val PISO = 0.93f
    private const val SALIDA = 0.08f

    // ---------------------------------------------------------------- la luz y la noche

    /** Dónde está la pantalla de la terminal y la lámpara de cultivo, en fracciones del ancho. */
    private val TERMINAL = Offset(0.33f, 0.37f)
    private val CULTIVO = Offset(0.79f, 0.1f)

    /** Cuánto le llega del fósforo a un punto [p] (en fracciones del ancho): cae con la distancia, despacio. */
    private fun luzTerminal(p: Offset) = (1.15f * (1f - (p - TERMINAL).getDistance() / 1.3f)).coerceIn(0f, 1f)
    /** Y de la lámpara de cultivo: un charco corto. */
    private fun luzCultivo(p: Offset) = (1f - (p - CULTIVO).getDistance() / 0.36f).coerceIn(0f, 1f)

    /** El color propio de algo, hundido: oscuro, pero de su mismo tono (no gris), con un poco del aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.17f + NOCHE.red * 0.75f, c.green * 0.17f + NOCHE.green * 0.75f, c.blue * 0.17f + NOCHE.blue * 0.75f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro arriba a la izquierda). De noche, de
     * su color hondo hacia el suyo según [kt] (el fósforo de la terminal) y [kc] (la lámpara de cultivo), teñido por la luz que
     * más le llega.
     */
    private class Tono(val o: Boolean, val kt: Float, val kc: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val k = maxOf(kt, kc * 0.85f)
        val v = mezcla(hondo(col), col, 0.6f * k.pow(2.2f))
        return mezcla(mezcla(v, FOSFORO, 0.1f * kt), LAMPARA, 0.32f * kc)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del refugio. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.25f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo verde del fósforo o el rosa de la lámpara. */
    private fun Tono.b(a: Float): Color =
        if (!o) luzMueble(a) else (if (kc > kt) LAMPARA else FOSFORO_CLARO).copy(alpha = (a * 0.8f * maxOf(kt, kc)).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f) = Tono(esNoche, if (esNoche) luzTerminal(p / anchoW) else 0f, if (esNoche) luzCultivo(p / anchoW) else 0f, dia)

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en la pared o en lo que tiene detrás: de día corrida a la derecha y abajo; de noche, hacia fuera de la
     * terminal, tanto más marcada cuanto más cerca está de ella. No baja de [hasta] (el borde de lo que la recibe).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.16f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.022f, w * 0.01f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luzTerminal(desde / w)
                if (k > 0.05f) {
                    val dx = (desde.x / w - TERMINAL.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.035f * dx, w * 0.012f) { drawPath(p, NOCHE.copy(alpha = (0.55f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa o el piso, corrida a la derecha: de día violeta, de noche la noche del refugio. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.2f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r]: el claro a la izquierda con su brillo y la sombra a la derecha (ir dentro de su recorte). */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.2f * fuerza), r.topLeft, Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.32f * fuerza), Offset(r.left + r.width * 0.15f, r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(r.left + r.width * 0.58f, r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(r.left + r.width * 0.8f, r.top), Size(r.width * 0.2f, r.height))
    }

    /** Un desconchón: el hueco con su borde de arriba a la izquierda en sombra y el hormigón que asoma. */
    private fun DrawScope.desconchon(c: Offset, r: Float, semilla: Int) {
        val u = density
        val t = tn(c)
        val p = Path().apply {
            for (j in 0 until 8) {
                val a = j / 8f * 6.283f
                val rr = r * (0.5f + 0.7f * azar(semilla, j))
                val q = Offset(c.x + rr * cos(a) * 1.4f, c.y + rr * sin(a))
                if (j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
            }
            close()
        }
        drawPath(p, t.s(0.16f))
        translate(0.7f * u, 0.7f * u) { clipPath(p) { drawPath(p, t.c(mezcla(CREMA_OSC, HORMIGON, 0.55f))) } }
        for (j in 0 until 3) drawCircle(t.s(0.25f), 0.5f * u, Offset(c.x + r * (azar(semilla + 3, j) - 0.4f), c.y + r * 0.5f * (azar(semilla + 4, j) - 0.3f)))
    }

    /** Las cifras de siete segmentos, para la plantilla del sector (los cortes entre segmentos son los puentes de la plantilla). */
    private val SEGMENTOS = mapOf('0' to "abcdef", '1' to "bc", '2' to "abged", '3' to "abgcd", '4' to "fgbc", '5' to "afgcd", '6' to "afgedc",
        '7' to "abc", '8' to "abcdefg", '9' to "abfgcd")

    private fun DrawScope.cifras(texto: String, x: Float, y: Float, a: Float, al: Float, col: Color) {
        val g = al * 0.24f
        val m = al / 2
        val q = g * 0.12f
        texto.forEachIndexed { i, ch ->
            val x0 = x + i * a * 1.4f
            SEGMENTOS[ch]?.forEach { s ->
                when (s) {
                    'a' -> drawRect(col, Offset(x0 + g + q, y), Size(a - 2 * g - 2 * q, g))
                    'b' -> drawRect(col, Offset(x0 + a - g, y + g + q), Size(g, m - g - 2 * q))
                    'c' -> drawRect(col, Offset(x0 + a - g, y + m + q), Size(g, m - g - 2 * q))
                    'd' -> drawRect(col, Offset(x0 + g + q, y + al - g), Size(a - 2 * g - 2 * q, g))
                    'e' -> drawRect(col, Offset(x0, y + m + q), Size(g, m - g - 2 * q))
                    'f' -> drawRect(col, Offset(x0, y + g + q), Size(g, m - g - 2 * q))
                    'g' -> drawRect(col, Offset(x0 + g + q, y + m - g / 2), Size(a - 2 * g - 2 * q, g))
                }
            }
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE; val canto = ch * CANTO
        val pieA = sup + (frente - sup) * 0.3f
        val pieF = sup + (frente - sup) * 0.92f
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        val xL = w * IZQ; val xR = w * DER
        val dado = maxOf(ch * 1.1f, suelo - w * 0.62f)

        // ---- el muro, el zócalo verde azulado y el piso de rejilla
        pared(w, suelo, ch, dado)
        piso(w, suelo, alto)

        // ---- en el muro: la canaleta, la tubería, la plantilla, el casco, el cartel y la repisa de cultivo
        tuberia(w, ch, sup)
        plantilla(Offset(w * 0.03f, ch * 0.5f), w)
        casco(Offset(w * 0.115f, ch * 0.09f), w)
        cartel(Rect(w * 0.235f, ch * 0.08f, w * 0.455f, ch * 0.53f))
        repisaCultivo(w, ch)
        canaleta(w, ch)

        // ---- la sombra del armario en el muro (a la derecha) y en el piso
        val t0 = tn(Offset(xR, ch * 1.4f))
        drawPath(poligono(xR, canto, xR + w * 0.03f, canto + w * 0.03f, xR + w * 0.03f, suelo, xR, suelo), t0.s(0.26f))
        drawRect(t0.s(0.3f), Offset(xL + w * 0.02f, suelo), Size(xR - xL + w * 0.012f, pisan - suelo))
        drawRect(t0.s(0.22f), Offset(xL + w * 0.03f, pisan), Size(xR - xL, hb * 0.07f))

        // ---- el armario
        armario(d, ch, canto, pisan, b)

        // ---- la tapa del armario y su canto
        val tm = tn(Offset(w * 0.4f, sup))
        val tapa = Rect(w * 0.03f, sup, w * 0.97f, frente)
        drawRect(tm.c(mezcla(OLIVA, OLIVA_LUZ, 0.55f)), tapa.topLeft, tapa.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.35f), Color.Transparent, sombraMueble(0.14f)), tapa.left, tapa.right), tapa.topLeft, tapa.size)
        drawRect(tm.s(0.3f), tapa.topLeft, Size(tapa.width, 1.4f * u))
        // Aros y rozaduras del uso.
        drawOval(tm.s(0.14f), Offset(w * 0.6f, sup + (frente - sup) * 0.25f), Size(w * 0.045f, (frente - sup) * 0.5f), style = Stroke(0.7f * u))
        for (k in 0 until 5) {
            val x = w * (0.08f + 0.85f * azar(2201, k))
            drawLine(tm.b(0.25f), Offset(x, sup + (frente - sup) * (0.3f + 0.4f * azar(2202, k))), Offset(x + w * 0.03f, sup + (frente - sup) * (0.4f + 0.3f * azar(2203, k))), 0.6f * u)
        }
        val labio = Rect(tapa.left, frente, tapa.right, canto)
        drawRect(tm.c(OLIVA), labio.topLeft, labio.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.25f), Color.Transparent, sombraMueble(0.2f)), labio.left, labio.right), labio.topLeft, labio.size)
        drawRect(tm.b(0.8f), Offset(labio.left, frente), Size(labio.width, 1.3f * u))
        drawRect(tm.s(0.35f), Offset(labio.left, canto - 1f * u), Size(labio.width, 1f * u))
        // La pintura saltada en el canto, a la izquierda y en la esquina derecha.
        listOf(0.07f, 0.31f, 0.93f).forEachIndexed { i, fx ->
            drawOval(tm.c(ACERO), Offset(w * fx, frente + 1.4f * u), Size(w * (0.012f + 0.01f * i), (canto - frente) * 0.45f))
        }
        drawRect(tm.s(0.32f), Offset(xL, canto), Size(xR - xL, 2.2f * u))
        drawRect(tm.s(0.14f), Offset(xL, canto + 2.2f * u), Size(xR - xL, 2.6f * u))

        // ---- lo de encima, de atrás adelante
        latas(pieA, sup)
        termo(Offset(w * 0.735f, pieA), sup)
        fiambrera(Offset(w * 0.865f, pieA), sup)
        contador(Offset(w * 0.6f, pieA), pieF, sup)
        terminal(Offset(w * 0.335f, pieA), ch, sup)
        teclado(Rect(w * 0.18f, pieF - ch * 0.075f, w * 0.49f, pieF), ch)
        taza(Offset(w * 0.775f, pieF))

        // ---- al pie: la caja de herramientas y la bombona, cortadas por los bordes
        cajaHerramientas(Offset(w * 0.075f, b.top + hb * PIE), w, hb)
        bombona(Offset(w * 0.945f, b.top + hb * PIE), w, hb)

        // ---- la placa de «y N más»: un portaetiquetas de acero remachado al zócalo
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 2f * u, r.top + 2.5f * u), r.size)
            drawRoundRect(t.c(ACERO), r.topLeft, r.size, CornerRadius(2f * u))
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            val dentro = Rect(r.left + 4f * u, r.top + 3f * u, r.right - 4f * u, r.bottom - 3f * u)
            drawRect(if (o) ETIQUETA_LEJOS else ETIQUETA, dentro.topLeft, dentro.size)
            drawRect(t.c(OCRE), dentro.topLeft, Size(dentro.width, 1.6f * u))
            listOf(r.left + 2f * u, r.right - 2f * u).forEach { x -> drawCircle(t.c(ACERO_OSC), 1f * u, Offset(x, r.center.y)) }
        }

        // ---- la luz de lo que alumbra, por encima de lo que tiene cerca
        val pantalla = TERMINAL * w
        val lampara = Offset(w * 0.79f, ch * 0.13f)
        if (o) {
            mancha(pantalla, w * 0.55f, FOSFORO.copy(alpha = 0.05f))
            mancha(pantalla + Offset(0f, w * 0.05f), w * 0.3f, FOSFORO.copy(alpha = 0.16f))
            mancha(lampara + Offset(0f, w * 0.07f), w * 0.2f, LAMPARA.copy(alpha = 0.42f))
            mancha(Offset(w * 0.83f, canto + (d.huecos.first().top - canto) * 0.45f), w * 0.07f, AMBAR.copy(alpha = 0.4f))
        } else {
            mancha(lampara + Offset(0f, w * 0.05f), w * 0.14f, LAMPARA.copy(alpha = 0.16f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * El muro de bloques pintado de crema: la mancha de luz de las luminarias arriba a la izquierda (de noche, el fósforo), las
     * juntas con su bisel, la pintura gastada, los desconchones y el zócalo verde azulado desde [dado] hasta el piso.
     */
    private fun DrawScope.pared(w: Float, suelo: Float, ch: Float, dado: Float) {
        val u = density
        val o = esNoche
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(CREMA, LUZ_MUEBLE, 0.3f), CREMA, mezcla(CREMA, CREMA_OSC, 0.5f)), 0f, w), Offset.Zero, Size(w, dado))
            mancha(Offset(w * 0.06f, ch * 0.02f), w * 0.8f, luzMueble(0.45f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.14f)), ch * 0.5f, dado), Offset(0f, ch * 0.5f), Size(w, dado - ch * 0.5f))
        } else {
            fun k(x: Float) = Tono(true, x, 0f).c(CREMA)
            drawRect(Brush.radialGradient(0f to k(1f), 0.15f to k(0.8f), 0.35f to k(0.45f), 0.6f to k(0.15f), 1f to k(0f),
                center = TERMINAL * w, radius = w * 1.3f), Offset.Zero, Size(w, dado))
        }
        pinceladas(Rect(0f, 0f, w, dado), if (o) NOCHE else CREMA_OSC, 2101, 34, u, 0.1f)
        // El zócalo verde azulado, con su filete y lo gastado de abajo.
        if (!o) drawRect(TEAL, Offset(0f, dado), Size(w, suelo - dado))
        else drawRect(Brush.verticalGradient(List(5) { i -> tn(Offset(w * 0.04f, dado + (suelo - dado) * i / 4f)).c(TEAL) }, dado, suelo), Offset(0f, dado), Size(w, suelo - dado))
        val tz = tn(Offset(w * 0.03f, dado))
        drawRect(tz.c(mezcla(TEAL, Color.Black, 0.3f)), Offset(0f, dado), Size(w, 2.2f * u))
        drawRect(tz.c(mezcla(CREMA, OCRE, 0.3f)), Offset(0f, dado - 2.4f * u), Size(w, 2.4f * u))
        pinceladas(Rect(0f, dado, w, suelo), if (o) NOCHE else mezcla(TEAL, Color.Black, 0.4f), 2102, 18, u, 0.14f)
        // Las juntas de los bloques, en hiladas trabadas, con su bisel claro debajo.
        val bh = w * 0.078f; val bw = w * 0.2f
        val junta = if (o) NOCHE.copy(alpha = 0.4f) else sombraMueble(0.13f)
        var fila = 0
        var y = bh * 0.55f
        var y0 = 0f
        while (y0 < suelo) {
            if (y < suelo) {
                drawLine(junta, Offset(0f, y), Offset(w, y), 1.2f * u)
                if (!o) drawLine(luzMueble(0.32f), Offset(0f, y + 1.2f * u), Offset(w, y + 1.2f * u), 0.7f * u)
            }
            var x = (if (fila % 2 == 0) 0f else bw / 2) + bw * 0.12f
            while (x < w) {
                drawLine(junta, Offset(x, y0), Offset(x, minOf(y, suelo)), 1.2f * u)
                if (!o) drawLine(luzMueble(0.28f), Offset(x + 1.2f * u, y0), Offset(x + 1.2f * u, minOf(y, suelo)), 0.7f * u)
                x += bw
            }
            y0 = y; y += bh; fila++
        }
        // Los desconchones: donde saltó la pintura asoma el hormigón.
        listOf(Offset(0.975f, 0.5f) to 5f, Offset(0.205f, 0.7f) to 4f, Offset(0.63f, 0.12f) to 3.5f).forEachIndexed { i, (p, r) ->
            desconchon(Offset(w * p.x, ch * p.y), r * u, 2110 + i)
        }
        listOf(0.018f to 1.25f, 0.982f to 1.55f, 0.02f to 1.9f).forEachIndexed { i, (fx, fy) ->
            if (w * fy < suelo - 6f * u) desconchon(Offset(w * fx, w * fy), 3.5f * u, 2120 + i)
        }
        // La mugre de abajo, donde el zócalo empieza.
        pinceladas(Rect(0f, dado - w * 0.06f, w, dado), if (o) NOCHE else CREMA_OSC, 2104, 10, u, 0.12f)
    }

    /** El piso de rejilla: los barrotes que se abren hacia quien mira, los travesaños y la sombra del rincón. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(t.c(mezcla(ACERO_OSC, NOCHE, 0.5f)), Offset(0f, suelo), Size(w, alto - suelo))
        var y = suelo + 2f * u
        var paso = (alto - suelo) * 0.12f
        while (y < alto) {
            drawRect(t.c(ACERO_OSC), Offset(0f, y), Size(w, 1.8f * u))
            drawRect(t.b(0.25f), Offset(0f, y), Size(w, 0.6f * u))
            y += paso; paso *= 1.3f
        }
        for (i in -3..27) {
            val x0 = w * i / 24f
            val x1 = w / 2 + (x0 - w / 2) * 1.3f
            drawLine(t.c(mezcla(ACERO_OSC, ACERO, 0.4f)), Offset(x0, suelo), Offset(x1, alto), 2.2f * u)
            drawLine(t.b(0.3f), Offset(x0 - 0.8f * u, suelo), Offset(x1 - 0.8f * u, alto), 0.7f * u)
        }
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else ACERO_OSC, 2130, 12, u, 0.14f)
        drawRect(tt.s(0.35f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.15f), Offset(0f, suelo + 2.5f * u), Size(w, 4f * u))
    }

    /** La canaleta de cables de lado a lado, arriba: la bandeja de chapa, sus grapas y tres cables que cuelgan un poco entre ellas. */
    private fun DrawScope.canaleta(w: Float, ch: Float) {
        val u = density
        val y = ch * 0.03f; val al = w * 0.018f
        val t = tn(Offset(w * 0.3f, y))
        drawRect(t.s(0.2f), Offset(0f, y + al), Size(w, al * 0.7f))
        drawRect(t.c(ACERO), Offset(0f, y), Size(w, al))
        drawRect(t.b(0.5f), Offset(0f, y), Size(w, 1f * u))
        drawRect(t.s(0.25f), Offset(0f, y + al - 1.2f * u), Size(w, 1.2f * u))
        // Los cables que se salen de la bandeja entre grapa y grapa.
        val grapas = listOf(-0.04f, 0.16f, 0.37f, 0.58f, 0.8f, 1.02f)
        val cables = listOf(mezcla(TINTA, OLIVA_OSC, 0.3f) to 2.4f, TEAL to 2f, OCRE to 1.6f)
        cables.forEachIndexed { j, (col, g) ->
            val yc = y + al + (1.2f + j * 1.8f) * u
            for (s in 0 until grapas.size - 1) {
                val a = w * grapas[s]; val bx = w * grapas[s + 1]
                val cuelga = w * (0.008f + 0.012f * azar(2140 + j, s))
                val p = Path().apply { moveTo(a, yc - 1f * u); cubicTo(a + (bx - a) * 0.3f, yc + cuelga, a + (bx - a) * 0.7f, yc + cuelga, bx, yc - 1f * u) }
                translate(w * 0.01f, w * 0.006f) { drawPath(p, t.s(0.12f), style = Stroke(g * u)) }
                drawPath(p, t.c(col), style = Stroke(g * u, cap = StrokeCap.Round))
                drawPath(p, t.b(0.3f), style = Stroke(g * 0.3f * u))
            }
        }
        // Las grapas, atornilladas al muro.
        grapas.forEach { fx ->
            val x = w * fx
            drawRect(t.s(0.2f), Offset(x - w * 0.006f + 1.5f * u, y - 2f * u + 1.5f * u), Size(w * 0.012f, al + 9f * u))
            drawRect(t.c(ACERO_OSC), Offset(x - w * 0.006f, y - 2f * u), Size(w * 0.012f, al + 9f * u))
            drawRect(t.b(0.4f), Offset(x - w * 0.006f, y - 2f * u), Size(1f * u, al + 9f * u))
            drawCircle(t.c(ACERO), 1.1f * u, Offset(x, y + al + 5.5f * u))
        }
        // El cable de la terminal baja por detrás de ella.
        val tc = tn(Offset(w * 0.47f, ch * 0.3f))
        drawPath(Path().apply { moveTo(w * 0.47f, y + al); cubicTo(w * 0.475f, ch * 0.2f, w * 0.462f, ch * 0.4f, w * 0.468f, ch * 0.55f) }, tc.c(TINTA), style = Stroke(2f * u, cap = StrokeCap.Round))
    }

    /** La tubería que baja del techo hasta el armario, verde azulada, con sus abrazaderas y la llave de paso naranja. */
    private fun DrawScope.tuberia(w: Float, ch: Float, sup: Float) {
        val u = density
        val x = w * 0.535f; val r = w * 0.012f
        val t = tn(Offset(x, ch * 0.5f))
        val tubo = Rect(x - r, 0f, x + r, sup)
        drawRect(t.s(0.18f), Offset(tubo.left + w * 0.02f, 0f), Size(tubo.width, sup))
        drawRect(t.c(mezcla(TEAL, ACERO, 0.25f)), tubo.topLeft, tubo.size)
        clipRect(tubo.left, tubo.top, tubo.right, tubo.bottom) { cilindro(tubo, t, 1.2f) }
        // Una unión y dos abrazaderas.
        listOf(ch * 0.22f, ch * 0.66f).forEach { y ->
            drawRect(t.s(0.2f), Offset(tubo.right, y + 1.5f * u), Size(w * 0.02f, 4f * u))
            drawRect(t.c(ACERO_OSC), Offset(tubo.left - 1.5f * u, y), Size(tubo.width + 3f * u, 4f * u))
            drawRect(t.b(0.45f), Offset(tubo.left - 1.5f * u, y), Size(tubo.width + 3f * u, 1f * u))
            drawCircle(t.c(ACERO), 1.3f * u, Offset(tubo.right + 3f * u, y + 2f * u))
        }
        drawRect(t.c(mezcla(TEAL, ACERO_OSC, 0.4f)), Offset(tubo.left - 1f * u, ch * 0.44f), Size(tubo.width + 2f * u, 6f * u))
        drawRect(t.b(0.35f), Offset(tubo.left - 1f * u, ch * 0.44f), Size(tubo.width + 2f * u, 1f * u))
        // La llave de paso: el volante de radios, visto de frente, un poco a la derecha.
        val c = Offset(x + r * 0.2f, ch * 0.36f)
        val rv = r * 1.6f
        drawRect(t.c(ACERO_OSC), Offset(x - r * 0.5f, c.y - r * 0.3f), Size(r * 1.3f, r * 0.6f))
        drawCircle(t.s(0.25f), rv, c + Offset(2.5f * u, 2.5f * u), style = Stroke(2.6f * u))
        drawCircle(t.c(NARANJA), rv, c, style = Stroke(2.6f * u))
        for (k in 0 until 4) { val a = k * 1.5708f + 0.4f; drawLine(t.c(NARANJA), c, Offset(c.x + rv * cos(a), c.y + rv * sin(a)), 1.6f * u) }
        drawArc(t.b(0.6f), 180f, 90f, false, Offset(c.x - rv, c.y - rv), Size(rv * 2, rv * 2), style = Stroke(1f * u))
        drawCircle(t.c(ACERO), r * 0.35f, c)
    }

    /** La plantilla del sector en el muro: una flecha y «04», en verde azulado, con lo gastado de la pintura. */
    private fun DrawScope.plantilla(p: Offset, w: Float) {
        val t = tn(p)
        val col = t.c(TEAL).copy(alpha = 0.8f)
        val al = w * 0.05f
        val a = al * 0.72f
        cifras("04", p.x, p.y, a, al, col)
        // La flecha: el asta con un puente y la punta.
        val y = p.y + al * 1.45f
        val g = al * 0.2f
        drawRect(col, Offset(p.x, y), Size(w * 0.05f, g))
        drawRect(col, Offset(p.x + w * 0.056f, y), Size(w * 0.03f, g))
        drawPath(poligono(p.x + w * 0.09f, y - g * 1.4f, p.x + w * 0.125f, y + g / 2, p.x + w * 0.09f, y + g * 2.4f), col)
    }

    /** El casco colgado de su gancho por el barboquejo: la calota de acero oliva, el ala, un golpe y su sombra en el muro. */
    private fun DrawScope.casco(gancho: Offset, w: Float) {
        val u = density
        val cw = w * 0.15f
        val c = Offset(gancho.x + w * 0.004f, gancho.y + w * 0.13f)
        val dh = cw * 0.5f
        val t = tn(c)
        val calota = Path().apply {
            moveTo(c.x - cw * 0.42f, c.y)
            cubicTo(c.x - cw * 0.44f, c.y - dh * 1.3f, c.x + cw * 0.44f, c.y - dh * 1.3f, c.x + cw * 0.42f, c.y)
            close()
        }
        val ala = Path().apply { addOval(Rect(c.x - cw / 2, c.y - cw * 0.06f, c.x + cw / 2, c.y + cw * 0.09f)) }
        val todo = Path().apply { addPath(calota); addPath(ala) }
        sombraEnPared(todo, c, size.height, 0.18f)
        // El barboquejo, por detrás, hasta el gancho.
        listOf(-0.33f, 0.3f).forEach { f -> drawLine(t.c(mezcla(NARANJA, TINTA, 0.65f)), Offset(c.x + cw * f, c.y + cw * 0.02f), gancho + Offset(0f, 3f * u), 1.8f * u, StrokeCap.Round) }
        // El gancho: la placa atornillada y la punta.
        drawRect(t.s(0.25f), Offset(gancho.x - 3f * u + 1.5f * u, gancho.y - 5f * u + 1.5f * u), Size(6f * u, 9f * u))
        drawRect(t.c(ACERO_OSC), Offset(gancho.x - 3f * u, gancho.y - 5f * u), Size(6f * u, 9f * u))
        drawRect(t.b(0.5f), Offset(gancho.x - 3f * u, gancho.y - 5f * u), Size(6f * u, 1f * u))
        drawLine(t.c(ACERO), Offset(gancho.x, gancho.y + 1f * u), Offset(gancho.x, gancho.y + 5f * u), 2f * u, StrokeCap.Round)
        // El ala: el canto de delante y lo de debajo en sombra.
        drawPath(ala, t.c(mezcla(OLIVA, OLIVA_OSC, 0.35f)))
        clipPath(ala) { drawRect(t.s(0.3f), Offset(c.x - cw / 2, c.y + cw * 0.02f), Size(cw, cw * 0.1f)) }
        drawPath(calota, t.c(OLIVA))
        clipPath(calota) {
            val r = Rect(c.x - cw * 0.44f, c.y - dh * 1.1f, c.x + cw * 0.44f, c.y)
            drawRect(t.s(0.18f), Offset(r.left + r.width * 0.6f, r.top), Size(r.width * 0.4f, r.height))
            drawRect(t.s(0.15f), Offset(r.left + r.width * 0.82f, r.top), Size(r.width * 0.18f, r.height))
            // El brillo de la calota, arriba a la izquierda, y la franja crema pintada.
            drawOval(t.b(0.35f), Offset(c.x - cw * 0.3f, c.y - dh * 0.92f), Size(cw * 0.3f, dh * 0.4f))
            drawRect(t.c(mezcla(CREMA, OCRE, 0.2f)), Offset(r.left, c.y - dh * 0.32f), Size(r.width, dh * 0.13f))
            drawRect(t.s(0.15f), Offset(r.left + r.width * 0.6f, c.y - dh * 0.32f), Size(r.width * 0.4f, dh * 0.13f))
            // Un golpe: la abolladura con su lado de luz y su lado de sombra, y unos rayones.
            drawOval(t.s(0.22f), Offset(c.x + cw * 0.08f, c.y - dh * 0.75f), Size(cw * 0.12f, dh * 0.22f))
            drawArc(t.b(0.4f), 90f, 140f, false, Offset(c.x + cw * 0.08f, c.y - dh * 0.75f), Size(cw * 0.12f, dh * 0.22f), style = Stroke(0.8f * u))
            for (k in 0 until 3) drawLine(t.b(0.25f), Offset(c.x - cw * (0.2f - 0.08f * k), c.y - dh * (0.5f + 0.1f * k)), Offset(c.x - cw * (0.1f - 0.08f * k), c.y - dh * (0.45f + 0.1f * k)), 0.6f * u)
        }
        drawPath(Path().apply { moveTo(c.x - cw * 0.42f, c.y); lineTo(c.x + cw * 0.42f, c.y) }, t.s(0.3f), style = Stroke(1.2f * u))
    }

    /**
     * El cartel de seguridad, pegado con cinta: papel amarillento con un átomo sobre un sol ocre, tres triángulos y la franja de
     * abajo con renglones sin letra; un doblez y la esquina de abajo levantada.
     */
    private fun DrawScope.cartel(r: Rect) {
        val u = density
        val t = tn(r.center)
        val papel = t.c(mezcla(mezcla(ETIQUETA, Color.White, 0.45f), OCRE, 0.06f))
        drawRect(t.s(0.26f), Offset(r.left + anchoW * 0.01f, r.top + anchoW * 0.012f), r.size)
        val esquina = r.width * 0.16f
        val hoja = poligono(r.left, r.top, r.right, r.top, r.right, r.bottom - esquina, r.right - esquina, r.bottom, r.left, r.bottom)
        drawPath(hoja, papel)
        clipPath(hoja) {
            val m = r.width * 0.07f
            val cx = r.center.x; val cy = r.top + r.height * 0.34f
            val rs = r.width * 0.3f
            // El sol con sus rayos.
            for (k in 0 until 12) {
                val a = k * 0.5236f
                val a1 = a - 0.13f; val a2 = a + 0.13f
                drawPath(poligono(cx + rs * 1.1f * cos(a1), cy + rs * 1.1f * sin(a1), cx + rs * 1.45f * cos(a), cy + rs * 1.45f * sin(a), cx + rs * 1.1f * cos(a2), cy + rs * 1.1f * sin(a2)), t.c(OCRE).copy(alpha = 0.8f))
            }
            drawCircle(t.c(OCRE), rs, Offset(cx, cy))
            // El átomo: tres órbitas y su núcleo.
            listOf(0f, 60f, 120f).forEach { g ->
                rotate(g, Offset(cx, cy)) { drawOval(t.c(TEAL), Offset(cx - rs * 0.85f, cy - rs * 0.3f), Size(rs * 1.7f, rs * 0.6f), style = Stroke(1.5f * u)) }
            }
            drawCircle(t.c(NARANJA), rs * 0.16f, Offset(cx, cy))
            // Los triángulos, como montañas de un dibujo de escuela pero solo geometría.
            val by = r.top + r.height * 0.78f
            drawPath(poligono(r.left + m, by, r.left + r.width * 0.3f, by - r.height * 0.16f, r.left + r.width * 0.52f, by), t.c(TEAL))
            drawPath(poligono(r.left + r.width * 0.36f, by, r.left + r.width * 0.6f, by - r.height * 0.22f, r.right - m, by), t.c(OLIVA_LUZ))
            drawPath(poligono(r.left + r.width * 0.6f, by, r.left + r.width * 0.72f, by - r.height * 0.09f, r.left + r.width * 0.84f, by), t.c(NARANJA))
            // La franja de abajo, con renglones sin letra.
            drawRect(t.c(TEAL), Offset(r.left, by + 2f * u), Size(r.width, r.bottom - by - 2f * u))
            listOf(0.3f to 0.62f, 0.55f to 0.42f).forEach { (fy, fw) ->
                drawRect(papel, Offset(r.left + m, by + 2f * u + (r.bottom - by) * fy), Size(r.width * fw, (r.bottom - by) * 0.12f))
            }
            // El papel gastado: el doblez del medio y manchas.
            val dy = r.top + r.height * 0.5f
            drawLine(t.s(0.14f), Offset(r.left, dy), Offset(r.right, dy), 1f * u)
            drawLine(t.b(0.4f), Offset(r.left, dy + 1f * u), Offset(r.right, dy + 1f * u), 0.7f * u)
            pinceladas(r, if (esNoche) NOCHE else CREMA_OSC, 2160, 8, u, 0.14f)
        }
        // La esquina levantada: el revés más oscuro y su sombra.
        val doblez = poligono(r.right, r.bottom - esquina, r.right - esquina * 0.15f, r.bottom - esquina * 0.15f, r.right - esquina, r.bottom)
        translate(1.5f * u, 2f * u) { drawPath(doblez, t.s(0.22f)) }
        drawPath(doblez, t.c(mezcla(ETIQUETA, CREMA_OSC, 0.6f)))
        // La cinta en las esquinas de arriba.
        listOf(r.left to -30f, r.right to 30f).forEach { (x, g) ->
            rotate(g, Offset(x, r.top)) { drawRect(t.c(mezcla(ETIQUETA, OCRE, 0.35f)).copy(alpha = 0.7f), Offset(x - r.width * 0.09f, r.top - 3f * u), Size(r.width * 0.18f, 6.5f * u)) }
        }
    }

    /**
     * La repisa de cultivo: una tabla de chapa con sus escuadras, la tomatera en una lata con su tutor, hojas y tres tomates, y
     * la lámpara de cultivo colgada de la canaleta con dos cadenitas, con su tubo rosa violeta encendido.
     */
    private fun DrawScope.repisaCultivo(w: Float, ch: Float) {
        val u = density
        val sy = ch * 0.4f; val grosor = w * 0.014f
        val tabla = Rect(w * 0.665f, sy, w * 0.955f, sy + grosor)
        val t = tn(tabla.center)
        // La sombra de la repisa en el muro y sus escuadras.
        drawRect(t.s(0.2f), Offset(tabla.left + w * 0.015f, tabla.bottom), Size(tabla.width, w * 0.022f))
        listOf(w * 0.69f, w * 0.925f).forEach { x ->
            val esc = poligono(x, tabla.bottom, x + w * 0.03f, tabla.bottom, x, tabla.bottom + w * 0.05f)
            translate(w * 0.012f, w * 0.006f) { drawPath(esc, t.s(0.2f)) }
            drawPath(esc, t.c(ACERO_OSC))
            drawLine(t.b(0.4f), Offset(x, tabla.bottom), Offset(x, tabla.bottom + w * 0.05f), 1f * u)
        }
        drawRect(t.c(ACERO), tabla.topLeft, tabla.size)
        drawRect(t.b(0.55f), tabla.topLeft, Size(tabla.width, 1f * u))
        drawRect(t.s(0.3f), Offset(tabla.left, tabla.bottom - 1.2f * u), Size(tabla.width, 1.2f * u))

        // La lata de la tomatera.
        val cx = w * 0.79f
        val a = w * 0.07f; val al = w * 0.062f
        val lata = Rect(cx - a / 2, sy - al, cx + a / 2, sy)
        val tl = tn(lata.center)
        contacto(Offset(cx + a * 0.1f, sy), a * 1.2f, 3f * u, if (esNoche) 0.5f else 0.3f)
        translate(w * 0.018f, w * 0.006f) { clipRect(0f, 0f, size.width, sy) { drawRect(tl.s(0.15f), lata.topLeft, lata.size) } }
        drawRect(tl.c(mezcla(ACERO, CREMA, 0.35f)), lata.topLeft, lata.size)
        clipRect(lata.left, lata.top, lata.right, lata.bottom) {
            cilindro(lata, tl)
            listOf(0.25f, 0.75f).forEach { f -> drawRect(tl.s(0.2f), Offset(lata.left, lata.top + al * f), Size(a, 0.8f * u)) }
            // El óxido de abajo.
            drawRect(tl.c(mezcla(NARANJA, OLIVA_OSC, 0.45f)).copy(alpha = 0.5f), Offset(lata.left, lata.bottom - al * 0.14f), Size(a, al * 0.14f))
        }
        drawOval(tl.c(mezcla(OLIVA_OSC, NARANJA, 0.2f)), Offset(lata.left + 1f * u, lata.top - 2f * u), Size(a - 2f * u, 4f * u))
        drawArc(tl.b(0.5f), 160f, 120f, false, Offset(lata.left, lata.top - 2.2f * u), Size(a, 4.4f * u), style = Stroke(1f * u))

        // El tutor y la mata.
        val suelo = lata.top
        val tope = ch * 0.17f
        drawLine(tl.c(mezcla(OCRE, OLIVA_OSC, 0.4f)), Offset(cx + a * 0.18f, suelo), Offset(cx + a * 0.22f, tope - w * 0.01f), 1.6f * u)
        val tallos = listOf(Offset(cx - a * 0.55f, tope + w * 0.02f), Offset(cx + a * 0.05f, tope), Offset(cx + a * 0.6f, tope + w * 0.03f))
        tallos.forEach { q ->
            val p = Path().apply { moveTo(cx, suelo); cubicTo(cx, suelo - (suelo - q.y) * 0.5f, q.x, q.y + (suelo - q.y) * 0.4f, q.x, q.y) }
            drawPath(p, tn(q).c(mezcla(HOJA, OLIVA_OSC, 0.3f)), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        }
        // Las hojas: de atrás (más oscuras) a delante.
        val hojas = listOf(
            Triple(Offset(-0.55f, 0.15f), -40f, 0.7f), Triple(Offset(0.5f, 0.2f), 30f, 0.7f), Triple(Offset(-0.15f, 0.05f), -10f, 0.75f),
            Triple(Offset(-0.7f, 0.45f), -60f, 1f), Triple(Offset(0.65f, 0.5f), 55f, 1f), Triple(Offset(-0.35f, 0.3f), -25f, 1f),
            Triple(Offset(0.25f, 0.32f), 20f, 1f), Triple(Offset(0.05f, 0.6f), 80f, 1f), Triple(Offset(-0.45f, 0.72f), -75f, 1f), Triple(Offset(0.48f, 0.78f), 70f, 1f))
        hojas.forEach { (f, g, luzH) ->
            val p = Offset(cx + a * f.x, tope + (suelo - tope) * f.y)
            val th = tn(p)
            val l = w * 0.04f
            rotate(g, p) {
                drawOval(th.c(mezcla(HOJA, OLIVA_OSC, 1f - luzH)), Offset(p.x - l * 0.22f, p.y - l * 0.5f), Size(l * 0.44f, l))
                drawOval(th.s(0.2f), Offset(p.x, p.y - l * 0.45f), Size(l * 0.2f, l * 0.9f))
                drawLine(th.b(0.35f), Offset(p.x, p.y - l * 0.4f), Offset(p.x, p.y + l * 0.4f), 0.6f * u)
            }
        }
        // Los tomates: dos maduros y uno verde.
        listOf(Triple(Offset(cx - a * 0.32f, suelo - w * 0.03f), w * 0.016f, mezcla(NARANJA, Color(0xFFC43A24), 0.4f)),
            Triple(Offset(cx + a * 0.36f, suelo - w * 0.045f), w * 0.014f, NARANJA),
            Triple(Offset(cx - a * 0.05f, tope + w * 0.035f), w * 0.011f, mezcla(HOJA, OCRE, 0.5f))).forEach { (p, r, col) ->
            val th = tn(p)
            drawCircle(th.s(0.25f), r, p + Offset(r * 0.3f, r * 0.3f))
            drawCircle(th.c(col), r, p)
            drawCircle(th.s(0.2f), r * 0.55f, p + Offset(r * 0.35f, r * 0.3f))
            drawCircle(th.b(0.6f), r * 0.28f, p - Offset(r * 0.35f, r * 0.35f))
            drawLine(th.c(HOJA), p - Offset(r * 0.5f, r * 0.9f), p + Offset(r * 0.5f, -r * 0.9f), 1.2f * u)
        }

        // La lámpara de cultivo: dos cadenitas desde la canaleta, la pantalla verde azulada y el tubo encendido.
        val lc = Offset(w * 0.79f, ch * 0.075f)
        val pw = w * 0.15f; val ph = w * 0.03f
        val tlam = tn(lc)
        listOf(-0.3f, 0.3f).forEach { f ->
            val x = lc.x + pw * f
            var yy = ch * 0.03f + w * 0.018f
            while (yy < lc.y) { drawOval(tlam.c(ACERO_OSC), Offset(x - 1.2f * u, yy), Size(2.4f * u, 3.2f * u), style = Stroke(0.8f * u)); yy += 2.8f * u }
        }
        val pantalla = poligono(lc.x - pw * 0.3f, lc.y, lc.x + pw * 0.3f, lc.y, lc.x + pw * 0.5f, lc.y + ph, lc.x - pw * 0.5f, lc.y + ph)
        translate(w * 0.015f, w * 0.008f) { drawPath(pantalla, tlam.s(0.16f)) }
        drawPath(pantalla, tlam.c(TEAL))
        clipPath(pantalla) {
            drawRect(tlam.b(0.35f), Offset(lc.x - pw * 0.5f, lc.y), Size(pw * 0.35f, ph))
            drawRect(tlam.s(0.25f), Offset(lc.x + pw * 0.15f, lc.y), Size(pw * 0.35f, ph))
        }
        drawRect(tlam.b(0.5f), Offset(lc.x - pw * 0.3f, lc.y), Size(pw * 0.6f, 1f * u))
        // El tubo: luz propia, del mismo color de día y de noche.
        drawRoundRect(mezcla(LAMPARA, Color.White, 0.35f), Offset(lc.x - pw * 0.44f, lc.y + ph - 1f * u), Size(pw * 0.88f, 3f * u), CornerRadius(1.5f * u))
        drawRect(LAMPARA.copy(alpha = 0.35f), Offset(lc.x - pw * 0.48f, lc.y + ph + 2f * u), Size(pw * 0.96f, 2f * u))
    }

    // ---------------------------------------------------------------- el armario

    /**
     * El cuerpo del armario: el frente de acero oliva (de noche, más encendido arriba, cerca de la terminal), el marco de cada
     * ranura con su grosor, el fondo de cada ranura, los remaches, el panel de arriba, el zócalo con su rejilla, las patas, la
     * pintura saltada y el golpe.
     */
    private fun DrawScope.armario(d: Disposicion, ch: Float, canto: Float, pisan: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val xL = w * IZQ; val xR = w * DER
        val cuerpo = Rect(xL, canto, xR, pisan - 3f * u)
        if (!o) drawRect(OLIVA, cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(6) { i -> tn(Offset(w * 0.38f, cuerpo.top + cuerpo.height * i / 5f)).c(OLIVA) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.16f), Color.Transparent, sombraMueble(0.22f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        pinceladas(cuerpo, if (o) NOCHE else OLIVA_OSC, 2170, 26, u, 0.12f)
        // Los cantos de los costados: el de la izquierda recibe la luz, el de la derecha (el grosor de la chapa) en sombra.
        for (k in 0 until 10) {
            val y0 = canto + cuerpo.height * k / 10f
            drawRect(tn(Offset(xL, y0 + cuerpo.height / 20f)).b(0.4f), Offset(xL, y0), Size(1.3f * u, cuerpo.height / 10f))
        }
        drawRect(tn(Offset(xR, ch * 1.2f)).c(OLIVA_OSC), Offset(xR - w * 0.012f, canto), Size(w * 0.012f, cuerpo.height))

        // Las ranuras: el marco que sobresale, su sombra en el frente y el fondo de cada una.
        val m = d.separacion * 0.22f
        d.huecos.forEachIndexed { i, r ->
            val t = tn(r.center)
            val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
            drawRect(t.s(0.25f), Offset(marco.left + 2.5f * u, marco.top + 2.5f * u), marco.size)
            drawRect(t.c(mezcla(OLIVA, OLIVA_LUZ, 0.3f)), marco.topLeft, marco.size)
            drawRect(t.b(0.55f), marco.topLeft, Size(marco.width, 1.2f * u))
            drawRect(t.b(0.3f), marco.topLeft, Size(1.2f * u, marco.height))
            drawRect(t.s(0.35f), Offset(marco.left, marco.bottom - 1.4f * u), Size(marco.width, 1.4f * u))
            drawRect(t.s(0.28f), Offset(marco.right - 1.4f * u, marco.top), Size(1.4f * u, marco.height))
            // Los cuatro remaches del marco.
            listOf(Offset(marco.left + m * 0.5f, marco.top + m * 0.5f), Offset(marco.right - m * 0.5f, marco.top + m * 0.5f),
                Offset(marco.left + m * 0.5f, marco.bottom - m * 0.5f), Offset(marco.right - m * 0.5f, marco.bottom - m * 0.5f)).forEach { remache(it, t) }
        }
        // Los remaches de los costados.
        val primero = d.huecos.first(); val ultima = d.filas.last()
        var y = canto + w * 0.03f
        while (y < ultima.bottom) {
            remache(Offset(xL + (primero.left - m - xL) * 0.45f, y), tn(Offset(xL, y)))
            remache(Offset(xR - (xR - d.huecos[cols - 1].right - m) * 0.5f, y), tn(Offset(xR, y)))
            y += w * 0.055f
        }

        // ---- el panel de arriba
        val panel = Rect(xL, canto, xR, primero.top - m)
        if (panel.height > 8f * u) panelArriba(panel)

        // ---- la tapa de registro de abajo: chapa atornillada con su tirador y una plantilla
        val reg = Rect(xL + w * 0.07f, ultima.bottom + m + d.separacion * 0.9f, xR - w * 0.07f, b.top + b.height * 0.3f - 5f * u)
        if (reg.height > 10f * u) {
            val tr = tn(reg.center)
            drawRect(tr.s(0.3f), Offset(reg.left + 2f * u, reg.top + 2f * u), reg.size)
            drawRect(tr.c(mezcla(OLIVA, OLIVA_LUZ, 0.22f)), reg.topLeft, reg.size)
            drawRect(tr.b(0.5f), reg.topLeft, Size(reg.width, 1.2f * u))
            drawRect(tr.b(0.3f), reg.topLeft, Size(1.2f * u, reg.height))
            drawRect(tr.s(0.3f), Offset(reg.left, reg.bottom - 1.4f * u), Size(reg.width, 1.4f * u))
            drawRect(tr.s(0.2f), Offset(reg.right - reg.width * 0.05f, reg.top), Size(reg.width * 0.05f, reg.height))
            listOf(Offset(reg.left + 4f * u, reg.top + 4f * u), Offset(reg.right - 4f * u, reg.top + 4f * u), Offset(reg.left + 4f * u, reg.bottom - 4f * u), Offset(reg.right - 4f * u, reg.bottom - 4f * u)).forEach { remache(it, tr) }
            // El tirador de barra, con su sombra.
            val ty = reg.top + reg.height * 0.42f
            drawLine(tr.s(0.3f), Offset(w * 0.42f + 2f * u, ty + 3f * u), Offset(w * 0.58f + 2f * u, ty + 3f * u), 3f * u, StrokeCap.Round)
            drawLine(tr.c(ACERO), Offset(w * 0.42f, ty), Offset(w * 0.58f, ty), 3f * u, StrokeCap.Round)
            drawLine(tr.b(0.6f), Offset(w * 0.42f, ty - 0.8f * u), Offset(w * 0.58f, ty - 0.8f * u), 0.8f * u, StrokeCap.Round)
            // La franja de aviso pintada, gastada, a la izquierda.
            val fr = Rect(reg.left + w * 0.04f, reg.top + reg.height * 0.3f, reg.left + w * 0.2f, reg.top + reg.height * 0.62f)
            clipRect(fr.left, fr.top, fr.right, fr.bottom) {
                drawRect(tr.c(OCRE), fr.topLeft, fr.size)
                var x = fr.left - fr.height
                while (x < fr.right) { drawPath(poligono(x, fr.bottom, x + fr.height, fr.top, x + fr.height * 1.6f, fr.top, x + fr.height * 0.6f, fr.bottom), tr.c(mezcla(TINTA, OLIVA_OSC, 0.3f))); x += fr.height * 1.6f }
                pinceladas(fr, tr.c(mezcla(OLIVA, OLIVA_LUZ, 0.22f)), 2230, 6, u, 0.6f)
            }
        }

        // ---- el zócalo: la chapa con la rejilla, hundida, y las patas
        val tz = tn(Offset(w * 0.5f, pisan))
        val zocalo = Rect(xL + w * 0.03f, b.top + b.height * 0.3f, xR - w * 0.03f, pisan - 3f * u)
        if (zocalo.height > 4f * u) {
            drawRect(tz.c(OLIVA_OSC), zocalo.topLeft, zocalo.size)
            drawRect(tz.s(0.4f), zocalo.topLeft, Size(zocalo.width, 2f * u))
            var x = zocalo.left + w * 0.04f
            while (x < zocalo.right - w * 0.04f) {
                drawRect(tz.c(mezcla(OLIVA_OSC, NOCHE, 0.6f)), Offset(x, zocalo.top + zocalo.height * 0.3f), Size(w * 0.012f, zocalo.height * 0.45f))
                drawRect(tz.b(0.3f), Offset(x, zocalo.top + zocalo.height * 0.75f), Size(w * 0.012f, 0.8f * u))
                x += w * 0.024f
            }
        }
        // Las patas niveladoras, pisando el piso.
        listOf(xL + w * 0.02f, xR - w * 0.05f).forEach { x ->
            drawRect(tz.c(ACERO_OSC), Offset(x, pisan - 4f * u), Size(w * 0.03f, 4f * u))
            drawRect(tz.b(0.4f), Offset(x, pisan - 4f * u), Size(w * 0.03f, 1f * u))
        }

        // ---- la pintura saltada en las esquinas y el golpe del costado derecho
        val saltos = listOf(Offset(xL + w * 0.008f, canto + 4f * u), Offset(xR - w * 0.02f, ultima.bottom - 3f * u), Offset(xL + w * 0.01f, ultima.bottom + w * 0.01f),
            Offset(primero.left - m - 2f * u, primero.bottom), Offset(w * 0.5f, canto + 3f * u))
        saltos.forEachIndexed { k, p ->
            val t = tn(p)
            val r = (1.8f + 2.2f * azar(2180, k)) * u
            val forma = Path().apply {
                for (j in 0 until 7) {
                    val a = j / 7f * 6.283f
                    val rr = r * (0.55f + 0.6f * azar(2181 + k, j))
                    val q = Offset(p.x + rr * cos(a) * 1.3f, p.y + rr * sin(a))
                    if (j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
                }
                close()
            }
            drawPath(forma, t.c(ACERO))
            drawPath(forma, t.s(0.3f), style = Stroke(0.6f * u))
        }
        val golpe = Offset(xR - w * 0.03f, d.filas[minOf(1, d.filas.size - 1)].center.y)
        val tg = tn(golpe)
        // Una abolladura: lo hundido en sombra arriba a la izquierda y el borde de abajo a la derecha que recibe la luz.
        drawOval(tg.s(0.16f), Offset(golpe.x - w * 0.01f, golpe.y - w * 0.014f), Size(w * 0.018f, w * 0.028f))
        drawArc(tg.b(0.3f), 10f, 80f, false, Offset(golpe.x - w * 0.01f, golpe.y - w * 0.014f), Size(w * 0.018f, w * 0.028f), style = Stroke(0.8f * u))
        drawLine(tg.c(ACERO), Offset(golpe.x - w * 0.004f, golpe.y + w * 0.004f), Offset(golpe.x + w * 0.002f, golpe.y + w * 0.01f), 1f * u)
    }

    /**
     * El aura de `Modifier.halo()` calca todo lo que pinta el compartimento y lo corre unos 2.5 dp: sobre el marco de la ranura
     * quedaba un contorno pálido. Este aro, del color del marco y un poco más ancho que el aura, se pinta en una capa con
     * `SrcAtop`: en la capa del aura no agrega nada; en el dibujo normal tapa el aura en reposo con el mismo marco. Al tocar el
     * compartimento el aura crece más allá del aro y se sigue viendo.
     */
    private fun DrawScope.cubreAura(w: Float, h: Float, t: Tono) {
        val a = 1.9f * density
        drawIntoCanvas { it.saveLayer(Rect(-a * 3, -a * 3, w + a * 3, h + a * 3), Paint().apply { blendMode = BlendMode.SrcAtop }) }
        drawRect(t.c(mezcla(OLIVA, OLIVA_LUZ, 0.3f)), Offset(-a, -a), Size(w + 2 * a, h + 2 * a), style = Stroke(2 * a), blendMode = BlendMode.Src)
        drawIntoCanvas { it.restore() }
    }

    private fun DrawScope.remache(p: Offset, t: Tono) {
        val u = density
        drawCircle(t.s(0.35f), 1.5f * u, p + Offset(0.8f * u, 0.8f * u))
        drawCircle(t.c(OLIVA_LUZ), 1.4f * u, p)
        drawCircle(t.b(0.6f), 0.6f * u, p - Offset(0.4f * u, 0.4f * u))
    }

    /**
     * El fondo de una ranura, en [r]: el hueco oscuro del armario con la sombra del marco de arriba en escalones, las guías de
     * los costados y el riel del piso, y las sombras de lo que está metido en ella. De día, la fila de arriba un poco más clara
     * y el lado izquierdo de cada ranura con más luz; de noche, el fósforo de la terminal según lo cerca que esté.
     */
    private fun DrawScope.fondoHueco(r: Rect, fila: Int, filas: Int, col: Int, t: Tono) {
        val u = density
        val w = r.width; val h = r.height
        drawRect(t.c(mezcla(OLIVA_OSC, OLIVA, 0.25f)), r.topLeft, r.size)
        drawRect(t.b(0.08f), r.topLeft, Size(w * 0.35f, h))
        // Las guías de los costados.
        listOf(0.035f, 0.965f).forEach { fx -> drawRect(t.s(0.35f), Offset(r.left + w * fx - 1f * u, r.top), Size(2f * u, h)) }
        // La sombra del marco de arriba, en escalones, y el costado derecho más hondo.
        listOf(0.34f to 0.12f, 0.2f to 0.14f, 0.08f to 0.24f).forEach { (a, al) -> drawRect(t.s(al), r.topLeft, Size(w, h * a)) }
        drawRect(t.s(0.22f), Offset(r.right - w * 0.06f, r.top), Size(w * 0.06f, h))
        drawRect(t.s(0.15f), r.topLeft, Size(w * 0.025f, h))
        // El riel del piso.
        val piso = r.top + h * PISO
        drawRect(t.c(mezcla(OLIVA, ACERO, 0.4f)), Offset(r.left, piso), Size(w, r.bottom - piso))
        drawRect(t.b(0.4f), Offset(r.left, piso), Size(w, 1f * u))
        // La sombra de lo que está metido: en el riel y en el fondo, a la derecha.
        drawRect(t.s(0.32f), Offset(r.left + w * 0.08f, piso), Size(w * 0.9f, (r.bottom - piso) * 0.55f))
        drawRect(t.s(0.24f), Offset(r.right - w * 0.045f, r.top + h * 0.2f), Size(w * 0.045f, piso - r.top - h * 0.2f))
    }

    /** El panel de arriba del armario: la rejilla de ventilación, la placa de inventario, la pegatina de revisión y los pilotos. */
    private fun DrawScope.panelArriba(p: Rect) {
        val u = density
        val w = anchoW
        val h = p.height
        // La rejilla: lamas inclinadas, cada una con su labio de luz.
        val tr = tn(Offset(w * 0.17f, p.center.y))
        val rej = Rect(w * 0.08f, p.top + h * 0.28f, w * 0.27f, p.bottom - h * 0.24f)
        drawRect(tr.s(0.25f), rej.topLeft, rej.size)
        var x = rej.left + 2f * u
        while (x < rej.right - 3f * u) {
            drawRect(tr.c(mezcla(OLIVA_OSC, NOCHE, 0.5f)), Offset(x, rej.top + 1f * u), Size(w * 0.008f, rej.height - 2f * u))
            drawRect(tr.b(0.4f), Offset(x + w * 0.008f, rej.top + 1f * u), Size(0.8f * u, rej.height - 2f * u))
            x += w * 0.017f
        }
        // La placa de inventario: chapa con cuatro remaches y renglones estampados.
        val tp = tn(Offset(w * 0.4f, p.center.y))
        val pl = Rect(w * 0.32f, p.top + h * 0.26f, w * 0.47f, p.bottom - h * 0.26f)
        drawRect(tp.s(0.3f), Offset(pl.left + 1.5f * u, pl.top + 1.5f * u), pl.size)
        drawRect(tp.c(mezcla(ACERO, CREMA, 0.25f)), pl.topLeft, pl.size)
        drawRect(tp.b(0.5f), pl.topLeft, Size(pl.width, 1f * u))
        listOf(0.35f, 0.62f).forEachIndexed { i, fy ->
            drawRect(tp.s(0.4f), Offset(pl.left + pl.width * 0.14f, pl.top + pl.height * fy), Size(pl.width * (if (i == 0) 0.7f else 0.45f), 1.2f * u))
            drawRect(tp.b(0.4f), Offset(pl.left + pl.width * 0.14f, pl.top + pl.height * fy + 1.2f * u), Size(pl.width * (if (i == 0) 0.7f else 0.45f), 0.6f * u))
        }
        listOf(Offset(pl.left + 2f * u, pl.top + 2f * u), Offset(pl.right - 2f * u, pl.top + 2f * u), Offset(pl.left + 2f * u, pl.bottom - 2f * u), Offset(pl.right - 2f * u, pl.bottom - 2f * u))
            .forEach { drawCircle(tp.c(ACERO_OSC), 0.9f * u, it) }
        // La pegatina de revisión: papel con casillas, dos marcadas, y la esquina levantada.
        val pg = Rect(w * 0.52f, p.top + h * 0.2f, w * 0.64f, p.bottom - h * 0.2f)
        val tg = tn(pg.center)
        val esq = pg.height * 0.3f
        drawRect(tg.s(0.2f), Offset(pg.left + 1.2f * u, pg.top + 1.2f * u), pg.size)
        drawPath(poligono(pg.left, pg.top, pg.right - esq, pg.top, pg.right, pg.top + esq, pg.right, pg.bottom, pg.left, pg.bottom), tg.c(mezcla(ETIQUETA, OCRE, 0.15f)))
        drawRect(tg.c(NARANJA), pg.topLeft, Size(pg.width - esq, pg.height * 0.18f))
        for (j in 0 until 3) for (i in 0 until 2) {
            val q = Offset(pg.left + pg.width * (0.12f + 0.42f * i), pg.top + pg.height * (0.32f + 0.22f * j))
            val s = pg.height * 0.14f
            drawRect(tg.c(TINTA).copy(alpha = 0.6f), q, Size(s, s), style = Stroke(0.6f * u))
            if (i == 0 && j < 2) drawLine(tg.c(TINTA).copy(alpha = 0.75f), q + Offset(0f, s * 0.5f), q + Offset(s * 0.9f, -s * 0.2f), 0.8f * u)
            drawLine(tg.c(TINTA).copy(alpha = 0.35f), q + Offset(s * 1.5f, s * 0.6f), q + Offset(s * 3.2f, s * 0.6f), 0.6f * u)
        }
        drawPath(poligono(pg.right - esq, pg.top, pg.right + esq * 0.2f, pg.top - esq * 0.3f, pg.right, pg.top + esq), tg.c(mezcla(ETIQUETA, CREMA_OSC, 0.6f)))
        // Los tres pilotos ámbar: dos encendidos (luz propia) y uno apagado, con su aro cromado.
        for (k in 0 until 3) {
            val c = Offset(w * (0.78f + 0.055f * k), p.center.y)
            val t = tn(c)
            val r = minOf(h * 0.16f, 4f * u)
            drawCircle(t.s(0.35f), r * 1.5f, c + Offset(0.8f * u, 0.8f * u))
            drawCircle(t.c(ACERO), r * 1.45f, c)
            drawCircle(t.b(0.5f), r * 1.45f, c, style = Stroke(0.7f * u))
            val enc = k != 1
            if (enc) {
                drawCircle(AMBAR.copy(alpha = 0.3f), r * 2.4f, c)
                drawCircle(AMBAR, r, c)
                drawCircle(Color.White.copy(alpha = 0.7f), r * 0.35f, c - Offset(r * 0.3f, r * 0.3f))
            } else {
                drawCircle(t.c(mezcla(AMBAR, OLIVA_OSC, 0.75f)), r, c)
                drawCircle(t.b(0.3f), r * 0.3f, c - Offset(r * 0.3f, r * 0.3f))
            }
        }
    }

    // ---------------------------------------------------------------- lo de encima

    /** Tres latas de ración sin marca, pintadas de oliva con una franja ocre: dos en la tapa y una encima, un poco corrida. */
    private fun DrawScope.latas(pie: Float, sup: Float) {
        val w = anchoW
        val a = w * 0.062f; val al = w * 0.07f
        lata(Offset(w * 0.08f, pie), a, al, 0, sup)
        lata(Offset(w * 0.147f, pie + 0.6f * density), a, al, 1, sup)
        lata(Offset(w * 0.118f, pie - al), a, al, 2, sup)
    }

    private fun DrawScope.lata(pie: Offset, a: Float, al: Float, i: Int, sup: Float) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - al / 2))
        val ry = a * 0.14f
        val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
        val cuerpo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.left, r.bottom - ry)
            arcTo(Rect(r.left, r.bottom - 2 * ry, r.right, r.bottom), 180f, -180f, false)
            lineTo(r.right, r.top); close()
        }
        sombraEnPared(cuerpo, r.center, sup, 0.15f)
        if (i < 2) contacto(Offset(pie.x + a * 0.1f, pie.y), a * 1.1f, 3f * u, if (esNoche) 0.5f else 0.32f)
        drawPath(cuerpo, t.c(mezcla(OLIVA_LUZ, OLIVA, 0.3f + 0.2f * i)))
        clipPath(cuerpo) {
            cilindro(r, t)
            drawRect(t.c(OCRE), Offset(r.left, r.top + al * 0.42f), Size(a, al * 0.16f))
            drawRect(t.s(0.18f), Offset(r.left + a * 0.6f, r.top + al * 0.42f), Size(a * 0.4f, al * 0.16f))
            listOf(0.2f, 0.82f).forEach { f -> drawRect(t.s(0.25f), Offset(r.left, r.top + al * f), Size(a, 0.8f * u)); drawRect(t.b(0.25f), Offset(r.left, r.top + al * f + 0.8f * u), Size(a, 0.6f * u)) }
            // Un poco de óxido en el reborde de abajo.
            if (i != 2) drawOval(t.c(mezcla(NARANJA, OLIVA_OSC, 0.5f)).copy(alpha = 0.55f), Offset(r.left + a * (0.2f + 0.4f * i), r.bottom - ry * 2.2f), Size(a * 0.25f, ry * 1.4f))
        }
        // La tapa: el reborde y el fondo hundido.
        drawOval(t.c(mezcla(ACERO, CREMA, 0.2f)), Offset(r.left, r.top - ry), Size(a, 2 * ry))
        drawOval(t.c(ACERO), Offset(r.left + a * 0.09f, r.top - ry * 0.72f), Size(a * 0.82f, ry * 1.44f))
        drawArc(t.b(0.55f), 150f, 110f, false, Offset(r.left + 0.5f * u, r.top - ry + 0.5f * u), Size(a - 1f * u, 2 * ry - 1f * u), style = Stroke(0.9f * u))
    }

    /** El termo de esmalte crema con su franja oliva, sin la taza (está delante): el tapón de corcho y el cuello de chapa. */
    private fun DrawScope.termo(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val a = w * 0.052f; val al = w * 0.19f
        val t = tn(Offset(pie.x, pie.y - al / 2))
        val ry = a * 0.14f
        val r = Rect(pie.x - a / 2, pie.y - al * 0.84f, pie.x + a / 2, pie.y)
        val cuerpo = Path().apply {
            moveTo(r.left, r.top + a * 0.25f)
            cubicTo(r.left, r.top, r.right, r.top, r.right, r.top + a * 0.25f)
            lineTo(r.right, r.bottom - ry)
            arcTo(Rect(r.left, r.bottom - 2 * ry, r.right, r.bottom), 0f, 180f, false)
            close()
        }
        val cuello = Rect(pie.x - a * 0.34f, pie.y - al * 0.93f, pie.x + a * 0.34f, r.top + a * 0.1f)
        val tapon = Rect(pie.x - a * 0.25f, pie.y - al, pie.x + a * 0.25f, cuello.top + 1f * u)
        sombraEnPared(Path().apply { addPath(cuerpo); addRect(cuello) }, r.center, sup, 0.15f)
        contacto(Offset(pie.x + a * 0.15f, pie.y), a * 1.3f, 3f * u, if (esNoche) 0.5f else 0.32f)
        // El tapón de corcho y el cuello de chapa.
        drawRect(t.c(mezcla(OCRE, NARANJA, 0.25f)), tapon.topLeft, tapon.size)
        clipRect(tapon.left, tapon.top, tapon.right, tapon.bottom) { cilindro(tapon, t) }
        drawRect(t.c(ACERO), cuello.topLeft, cuello.size)
        clipRect(cuello.left, cuello.top, cuello.right, cuello.bottom) { cilindro(cuello, t, 1.3f); for (k in 1..3) drawRect(t.s(0.25f), Offset(cuello.left, cuello.top + cuello.height * k / 4f), Size(cuello.width, 0.7f * u)) }
        drawPath(cuerpo, t.c(mezcla(NARANJA, CREMA, 0.3f)))
        clipPath(cuerpo) {
            cilindro(r, t, 1.2f)
            drawRect(t.c(OLIVA), Offset(r.left, r.top + r.height * 0.55f), Size(a, r.height * 0.12f))
            drawRect(t.s(0.2f), Offset(r.left + a * 0.6f, r.top + r.height * 0.55f), Size(a * 0.4f, r.height * 0.12f))
            // El esmalte saltado y un golpe.
            drawOval(t.c(ACERO_OSC), Offset(r.left + a * 0.55f, r.top + r.height * 0.3f), Size(a * 0.18f, a * 0.12f))
            drawOval(t.c(ACERO_OSC), Offset(r.left + a * 0.15f, r.bottom - a * 0.35f), Size(a * 0.12f, a * 0.08f))
        }
    }

    /** La taza del termo, de chapa, delante de él en la tapa. */
    private fun DrawScope.taza(pie: Offset) {
        val u = density
        val a = anchoW * 0.034f; val al = anchoW * 0.03f
        val t = tn(pie)
        val ry = a * 0.18f
        val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
        contacto(Offset(pie.x + a * 0.2f, pie.y), a * 1.4f, 2.5f * u, if (esNoche) 0.5f else 0.34f)
        val cuerpo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.left + a * 0.06f, r.bottom - ry)
            arcTo(Rect(r.left + a * 0.06f, r.bottom - 2 * ry, r.right - a * 0.06f, r.bottom), 180f, -180f, false)
            lineTo(r.right, r.top); close()
        }
        drawPath(cuerpo, t.c(mezcla(ACERO, CREMA, 0.15f)))
        clipPath(cuerpo) { cilindro(r, t, 1.3f) }
        drawOval(t.c(mezcla(ACERO_OSC, NOCHE, 0.3f)), Offset(r.left, r.top - ry), Size(a, 2 * ry))
        drawArc(t.b(0.6f), 160f, 130f, false, Offset(r.left, r.top - ry), Size(a, 2 * ry), style = Stroke(0.9f * u))
    }

    /** La fiambrera de chapa verde azulada: la caja, la tapa abombada, los dos cierres, el asa de alambre y el esmalte saltado. */
    private fun DrawScope.fiambrera(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val fw = w * 0.13f; val fh = w * 0.05f; val dh = w * 0.038f
        val t = tn(Offset(pie.x, pie.y - fh))
        val caja = Rect(pie.x - fw / 2, pie.y - fh, pie.x + fw / 2, pie.y)
        val tapa = Path().apply {
            moveTo(caja.left, caja.top)
            arcTo(Rect(caja.left, caja.top - dh, caja.right, caja.top + dh), 180f, 180f, false)
            close()
        }
        val todo = Path().apply { addRect(caja); addPath(tapa) }
        sombraEnPared(todo, caja.center, sup, 0.15f)
        contacto(Offset(pie.x + fw * 0.08f, pie.y), fw * 1.1f, 3.5f * u, if (esNoche) 0.5f else 0.32f)
        // El asa, por detrás, abatida hacia atrás.
        drawArc(t.c(ACERO_OSC), 200f, 140f, false, Offset(caja.left + fw * 0.2f, caja.top - dh * 1.5f), Size(fw * 0.6f, dh * 1.4f), style = Stroke(1.6f * u))
        drawRect(t.c(TEAL), caja.topLeft, caja.size)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            drawRect(t.b(0.2f), caja.topLeft, Size(fw * 0.2f, fh))
            drawRect(t.s(0.22f), Offset(caja.right - fw * 0.22f, caja.top), Size(fw * 0.22f, fh))
            drawRect(t.s(0.2f), Offset(caja.left, caja.bottom - 2f * u), Size(fw, 2f * u))
        }
        drawPath(tapa, t.c(mezcla(TEAL, CREMA, 0.12f)))
        clipPath(tapa) {
            drawRect(t.b(0.3f), Offset(caja.left, caja.top - dh), Size(fw, dh * 0.4f))
            drawRect(t.b(0.25f), Offset(caja.left, caja.top - dh), Size(fw * 0.3f, dh))
            drawRect(t.s(0.2f), Offset(caja.right - fw * 0.25f, caja.top - dh), Size(fw * 0.25f, dh))
            drawRect(t.s(0.22f), Offset(caja.left, caja.top - dh * 0.25f), Size(fw, dh * 0.25f))
        }
        // La junta de la tapa y los cierres.
        drawRect(t.s(0.35f), Offset(caja.left, caja.top), Size(fw, 1.2f * u))
        drawRect(t.b(0.35f), Offset(caja.left, caja.top + 1.2f * u), Size(fw, 0.7f * u))
        listOf(0.22f, 0.72f).forEach { f ->
            val q = Rect(caja.left + fw * f, caja.top - dh * 0.18f, caja.left + fw * f + fw * 0.07f, caja.top + fh * 0.35f)
            drawRect(t.s(0.3f), Offset(q.left + 1f * u, q.top + 1f * u), q.size)
            drawRect(t.c(mezcla(ACERO, CREMA, 0.3f)), q.topLeft, q.size)
            drawRect(t.b(0.6f), q.topLeft, Size(1f * u, q.height))
        }
        // El esmalte saltado: dos desportilladuras.
        drawOval(t.c(ACERO_OSC), Offset(caja.left + fw * 0.5f, caja.bottom - fh * 0.35f), Size(fw * 0.06f, fh * 0.18f))
        drawOval(t.c(ACERO_OSC), Offset(caja.right - fw * 0.1f, caja.top - dh * 0.5f), Size(fw * 0.05f, dh * 0.2f))
    }

    /**
     * El contador de radiación: la caja de chapa ocre con su correa, el dial de esfera crema con la escala, la zona naranja y la
     * aguja, el mando de la escala, y la sonda tumbada delante con su cable en espiral.
     */
    private fun DrawScope.contador(pie: Offset, pieF: Float, sup: Float) {
        val u = density
        val w = anchoW
        val gw = w * 0.115f; val gh = w * 0.082f
        val t = tn(Offset(pie.x, pie.y - gh / 2))
        val caja = Rect(pie.x - gw / 2, pie.y - gh, pie.x + gw / 2, pie.y)
        val dv = Offset(w * 0.014f, -w * 0.009f)
        val lado = poligono(caja.right, caja.top, caja.right + dv.x, caja.top + dv.y, caja.right + dv.x, caja.bottom + dv.y, caja.right, caja.bottom)
        val arriba = poligono(caja.left, caja.top, caja.left + dv.x, caja.top + dv.y, caja.right + dv.x, caja.top + dv.y, caja.right, caja.top)
        val todo = Path().apply { addRect(caja); addPath(lado); addPath(arriba) }
        sombraEnPared(todo, caja.center, sup, 0.16f)
        contacto(Offset(pie.x + gw * 0.1f, pie.y), gw * 1.15f, 4f * u, if (esNoche) 0.55f else 0.34f)
        // La correa de cuero, por detrás.
        drawPath(Path().apply { moveTo(caja.left + gw * 0.1f, caja.top); cubicTo(caja.left + gw * 0.15f, caja.top - gh * 0.75f, caja.right - gw * 0.05f, caja.top - gh * 0.8f, caja.right + dv.x * 0.5f, caja.top + dv.y) },
            t.c(mezcla(NARANJA, TINTA, 0.62f)), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(lado, t.c(mezcla(OCRE, OLIVA_OSC, 0.45f)))
        drawPath(arriba, t.c(mezcla(OCRE, CREMA, 0.3f)))
        drawRect(t.c(OCRE), caja.topLeft, caja.size)
        drawRect(t.b(0.3f), caja.topLeft, Size(gw * 0.12f, gh))
        drawRect(t.s(0.15f), Offset(caja.right - gw * 0.2f, caja.top), Size(gw * 0.2f, gh))
        drawRect(t.b(0.6f), caja.topLeft, Size(gw, 1f * u))
        // El dial.
        val dc = Offset(caja.left + gw * 0.38f, caja.top + gh * 0.5f)
        val rd = gh * 0.33f
        drawCircle(t.c(ACERO_OSC), rd * 1.18f, dc)
        drawCircle(t.c(ETIQUETA), rd, dc)
        for (k in 0..6) {
            val a = Math.toRadians(200.0 + k * 23.0)
            val c1 = Offset(dc.x + rd * 0.72f * cos(a).toFloat(), dc.y + rd * 0.72f * sin(a).toFloat())
            val c2 = Offset(dc.x + rd * 0.88f * cos(a).toFloat(), dc.y + rd * 0.88f * sin(a).toFloat())
            drawLine(t.c(TINTA), c1, c2, 0.7f * u)
        }
        drawArc(t.c(NARANJA), 292f, 46f, false, Offset(dc.x - rd * 0.8f, dc.y - rd * 0.8f), Size(rd * 1.6f, rd * 1.6f), style = Stroke(1.6f * u))
        val ag = Math.toRadians(238.0)
        drawLine(t.c(TINTA), dc, Offset(dc.x + rd * 0.85f * cos(ag).toFloat(), dc.y + rd * 0.85f * sin(ag).toFloat()), 0.9f * u, StrokeCap.Round)
        drawCircle(t.c(TINTA), 1.2f * u, dc)
        drawArc(t.b(0.6f), 190f, 70f, false, Offset(dc.x - rd * 0.9f, dc.y - rd * 0.9f), Size(rd * 1.8f, rd * 1.8f), style = Stroke(0.9f * u))
        // El mando de la escala y el interruptor.
        val mc = Offset(caja.left + gw * 0.8f, caja.top + gh * 0.36f)
        drawCircle(t.s(0.3f), gh * 0.13f, mc + Offset(1f * u, 1f * u))
        drawCircle(t.c(mezcla(TINTA, OLIVA_OSC, 0.4f)), gh * 0.12f, mc)
        drawLine(t.c(ETIQUETA), mc, mc + Offset(-gh * 0.06f, -gh * 0.09f), 0.8f * u)
        drawCircle(t.b(0.4f), gh * 0.04f, mc - Offset(gh * 0.04f, gh * 0.04f))
        drawRect(t.c(ACERO_OSC), Offset(mc.x - gh * 0.04f, caja.top + gh * 0.66f), Size(gh * 0.08f, gh * 0.16f))
        drawRect(t.b(0.5f), Offset(mc.x - gh * 0.04f, caja.top + gh * 0.66f), Size(gh * 0.08f, 0.8f * u))
        // El cable en espiral y la sonda tumbada delante.
        val ini = Offset(caja.right + dv.x * 0.4f, caja.bottom - gh * 0.25f)
        val sonda0 = Offset(caja.right + w * 0.035f, pieF - 2.5f * u)
        val sonda1 = Offset(sonda0.x + w * 0.075f, pieF - 3.8f * u)
        val n = 7
        for (k in 0 until n) {
            val f = k / (n - 1f)
            val q = Offset(ini.x + (sonda0.x - ini.x) * f, ini.y + (sonda0.y - ini.y) * f)
            drawOval(t.c(mezcla(TINTA, OLIVA_OSC, 0.3f)), Offset(q.x - 1.6f * u, q.y - 2.4f * u), Size(3.2f * u, 4.8f * u), style = Stroke(0.9f * u))
        }
        contacto(Offset((sonda0.x + sonda1.x) / 2 + 2f * u, pieF), w * 0.085f, 2.5f * u, if (esNoche) 0.5f else 0.3f)
        drawLine(t.c(ACERO), sonda0, sonda1, 5f * u, StrokeCap.Round)
        drawLine(t.b(0.55f), sonda0 + Offset(0f, -1.5f * u), sonda1 + Offset(0f, -1.5f * u), 1f * u, StrokeCap.Round)
        drawLine(t.c(mezcla(TINTA, OLIVA_OSC, 0.3f)), sonda0, sonda0 + (sonda1 - sonda0) * 0.3f, 5.6f * u, StrokeCap.Round)
        drawLine(t.s(0.3f), sonda0 + Offset(0f, 1.6f * u), sonda1 + Offset(0f, 1.6f * u), 1f * u)
    }

    /**
     * La terminal: el gabinete verde azulado con su cara, su costado en sombra y la joroba del tubo detrás; el bisel hundido y
     * la pantalla de fósforo abombada, encendida, con renglones sin letra y el cursor; abajo, la rejilla, el mando y el piloto.
     */
    private fun DrawScope.terminal(pie: Offset, ch: Float, sup: Float) {
        val u = density
        val w = anchoW
        val tw = w * 0.27f; val th = ch * 0.42f
        val t = tn(Offset(pie.x, pie.y - th / 2))
        val f = Rect(pie.x - tw / 2, pie.y - th, pie.x + tw / 2, pie.y)
        val dv = Offset(tw * 0.1f, -th * 0.07f)
        val lado = poligono(f.right, f.top, f.right + dv.x, f.top + dv.y, f.right + dv.x, f.bottom + dv.y, f.right, f.bottom)
        val arriba = poligono(f.left, f.top, f.left + dv.x, f.top + dv.y, f.right + dv.x, f.top + dv.y, f.right, f.top)
        val joroba = poligono(f.right + dv.x, f.top + th * 0.1f, f.right + dv.x + tw * 0.11f, f.top + th * 0.24f, f.right + dv.x + tw * 0.11f, f.bottom - th * 0.26f, f.right + dv.x, f.bottom - th * 0.14f)
        val todo = Path().apply { addRect(f); addPath(lado); addPath(arriba); addPath(joroba) }
        // De día deja su sombra en el muro; de noche es la luz, no la deja.
        if (!esNoche) sombraEnPared(todo, f.center, sup, 0.18f)
        contacto(Offset(pie.x + tw * 0.08f, pie.y), tw * 1.12f, 6f * u, if (esNoche) 0.4f else 0.36f)
        drawPath(joroba, t.c(mezcla(CARCASA, OLIVA_OSC, 0.55f)))
        drawPath(lado, t.c(mezcla(CARCASA, OLIVA_OSC, 0.38f)))
        drawPath(arriba, t.c(mezcla(CARCASA, CREMA, 0.3f)))
        // Las ranuras de ventilación del costado.
        for (k in 0 until 5) {
            val y = f.top + th * (0.22f + 0.07f * k)
            drawLine(t.s(0.4f), Offset(f.right + dv.x * 0.25f, y + dv.y * 0.25f), Offset(f.right + dv.x * 0.8f, y + dv.y * 0.8f), 1.2f * u)
        }
        val cara = RoundRect(f, CornerRadius(th * 0.05f))
        val caraP = Path().apply { addRoundRect(cara) }
        drawPath(caraP, t.c(CARCASA))
        clipPath(caraP) {
            drawRect(t.b(0.2f), f.topLeft, Size(tw * 0.14f, th))
            drawRect(t.s(0.16f), Offset(f.right - tw * 0.18f, f.top), Size(tw * 0.18f, th))
            drawRect(t.b(0.55f), f.topLeft, Size(tw, 1.2f * u))
            pinceladas(f, if (esNoche) NOCHE else mezcla(CARCASA, OLIVA_OSC, 0.5f), 2190, 8, u, 0.14f)
        }
        // El bisel hundido y la pantalla abombada.
        val bis = Rect(f.left + tw * 0.07f, f.top + th * 0.07f, f.right - tw * 0.07f, f.bottom - th * 0.27f)
        drawRoundRect(t.c(mezcla(CARCASA, OLIVA_OSC, 0.6f)), bis.topLeft, bis.size, CornerRadius(th * 0.07f))
        drawRoundRect(t.s(0.35f), bis.topLeft, Size(bis.width, 2.4f * u), CornerRadius(th * 0.04f))
        drawRect(t.b(0.4f), Offset(bis.left + th * 0.05f, bis.bottom - 1f * u), Size(bis.width - th * 0.1f, 1f * u))
        val pan = Rect(bis.left + tw * 0.045f, bis.top + tw * 0.04f, bis.right - tw * 0.045f, bis.bottom - tw * 0.04f)
        val panP = Path().apply { addRoundRect(RoundRect(pan, CornerRadius(th * 0.11f))) }
        // La pantalla brilla por sí misma: igual de día y de noche.
        drawPath(panP, Brush.radialGradient(listOf(Color(0xFF1F5E36), PANTALLA), pan.center, pan.width * 0.62f))
        clipPath(panP) {
            val alto = pan.height / 9.5f
            for (k in 0 until 7) {
                var x = pan.left + pan.width * 0.1f
                val fin = pan.left + pan.width * (0.35f + 0.5f * azar(2195, k))
                var j = 0
                while (x < fin) {
                    val largo = pan.width * (0.04f + 0.1f * azar(2196 + k, j))
                    drawRect(FOSFORO.copy(alpha = 0.78f), Offset(x, pan.top + alto * (1.1f + k)), Size(minOf(largo, fin - x), alto * 0.42f))
                    x += largo + pan.width * 0.03f; j++
                }
            }
            drawRect(FOSFORO, Offset(pan.left + pan.width * 0.1f, pan.top + alto * 8.1f), Size(alto * 0.5f, alto * 0.62f))
            // Las líneas de barrido y el reflejo curvo del vidrio, arriba a la izquierda.
            var y = pan.top
            while (y < pan.bottom) { drawLine(NOCHE.copy(alpha = 0.18f), Offset(pan.left, y), Offset(pan.right, y), 0.6f * u); y += 2.2f * u }
            drawPath(Path().apply {
                moveTo(pan.left + pan.width * 0.08f, pan.top + pan.height * 0.5f)
                cubicTo(pan.left + pan.width * 0.08f, pan.top + pan.height * 0.12f, pan.left + pan.width * 0.3f, pan.top + pan.height * 0.08f, pan.left + pan.width * 0.55f, pan.top + pan.height * 0.08f)
            }, (if (esNoche) FOSFORO_CLARO.copy(alpha = 0.25f) else luzMueble(0.4f)), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        }
        // Abajo: la rejilla del altavoz, el mando, el piloto ámbar.
        val y0 = bis.bottom + th * 0.06f
        val y1 = f.bottom - th * 0.05f
        for (k in 0 until 6) drawRect(t.s(0.4f), Offset(f.left + tw * (0.56f + 0.055f * k), y0 + (y1 - y0) * 0.15f), Size(tw * 0.022f, (y1 - y0) * 0.7f))
        val mc = Offset(f.left + tw * 0.16f, (y0 + y1) / 2)
        val rm = (y1 - y0) * 0.36f
        drawCircle(t.s(0.3f), rm, mc + Offset(1f * u, 1f * u))
        drawCircle(t.c(mezcla(TINTA, OLIVA_OSC, 0.4f)), rm, mc)
        drawCircle(t.b(0.45f), rm * 0.35f, mc - Offset(rm * 0.35f, rm * 0.35f))
        val pc = Offset(f.left + tw * 0.3f, (y0 + y1) / 2)
        drawCircle(AMBAR.copy(alpha = 0.3f), rm * 0.8f, pc)
        drawCircle(AMBAR, rm * 0.38f, pc)
    }

    /** El teclado delante de la terminal: el cuerpo, la cara de las teclas vista un poco desde arriba, teclas crema y ámbar. */
    private fun DrawScope.teclado(r: Rect, ch: Float) {
        val u = density
        val t = tn(Offset(r.center.x, r.top))
        val frenteK = Rect(r.left, r.bottom - r.height * 0.32f, r.right, r.bottom)
        val cara = poligono(r.left + r.width * 0.02f, frenteK.top, r.left + r.width * 0.05f, r.top, r.right - r.width * 0.02f, r.top, r.right, frenteK.top)
        contacto(Offset(r.center.x + r.width * 0.06f, r.bottom), r.width * 1.05f, 4f * u, if (esNoche) 0.45f else 0.34f)
        drawPath(cara, t.c(mezcla(CARCASA, OLIVA_OSC, 0.3f)))
        // Las teclas: cuatro hileras, más chicas atrás.
        for (fila in 0 until 4) {
            val fy0 = r.top + (frenteK.top - r.top) * (0.1f + 0.22f * fila)
            val fy1 = fy0 + (frenteK.top - r.top) * 0.17f
            val x0 = r.left + r.width * (0.07f - 0.01f * fila)
            val x1 = r.right - r.width * (0.03f + 0.005f * fila)
            val n = 12
            val paso = (x1 - x0) / n
            for (k in 0 until n) {
                val ancha = fila == 3 && k in 3..7
                if (fila == 3 && k in 4..7) continue
                val kx = x0 + paso * k + (if (fila % 2 == 1) paso * 0.3f else 0f)
                val kw = if (ancha) paso * 4.8f else paso * 0.8f
                if (kx + kw > x1) continue
                val col = if ((fila == 0 && k > 9) || (fila == 2 && k == 0)) OCRE else mezcla(ETIQUETA, CARCASA, 0.25f)
                drawRect(t.s(0.35f), Offset(kx + 0.8f * u, fy0 + 0.8f * u), Size(kw, fy1 - fy0))
                drawRect(t.c(col), Offset(kx, fy0), Size(kw, fy1 - fy0))
                drawRect(t.b(0.5f), Offset(kx, fy0), Size(kw, 0.7f * u))
            }
        }
        drawRect(t.c(CARCASA), frenteK.topLeft, frenteK.size)
        drawRect(t.b(0.5f), frenteK.topLeft, Size(frenteK.width, 1f * u))
        drawRect(t.s(0.2f), Offset(frenteK.right - frenteK.width * 0.15f, frenteK.top), Size(frenteK.width * 0.15f, frenteK.height))
        // El cable en espiral hasta la terminal.
        for (k in 0 until 5) drawOval(t.c(mezcla(TINTA, OLIVA_OSC, 0.3f)), Offset(r.right - r.width * 0.02f + k * 2.2f * u, r.top - 1f * u - k * 1.6f * u), Size(3f * u, 4f * u), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- al pie

    /** La caja de herramientas naranja, cortada por el borde izquierdo: la tapa, el asa, los cierres y lo gastado. */
    private fun DrawScope.cajaHerramientas(pie: Offset, w: Float, hb: Float) {
        val u = density
        val cw = w * 0.24f; val chh = hb * 0.36f
        val caja = Rect(pie.x - cw / 2, pie.y - chh, pie.x + cw / 2, pie.y)
        val t = tn(caja.center)
        val dv = Offset(w * 0.015f, -hb * 0.05f)
        contacto(Offset(pie.x + cw * 0.1f, pie.y), cw * 1.15f, 6f * u, if (esNoche) 0.6f else 0.4f)
        drawPath(poligono(caja.right, caja.top, caja.right + dv.x, caja.top + dv.y, caja.right + dv.x, caja.bottom + dv.y, caja.right, caja.bottom), t.c(mezcla(NARANJA, OLIVA_OSC, 0.45f)))
        drawPath(poligono(caja.left, caja.top, caja.left + dv.x, caja.top + dv.y, caja.right + dv.x, caja.top + dv.y, caja.right, caja.top), t.c(mezcla(NARANJA, CREMA, 0.25f)))
        drawRect(t.c(NARANJA), caja.topLeft, caja.size)
        drawRect(t.s(0.18f), Offset(caja.right - cw * 0.18f, caja.top), Size(cw * 0.18f, chh))
        drawRect(t.s(0.35f), Offset(caja.left, caja.top + chh * 0.3f), Size(cw, 1.2f * u))
        drawRect(t.b(0.35f), Offset(caja.left, caja.top + chh * 0.3f + 1.2f * u), Size(cw, 0.7f * u))
        drawRect(t.b(0.5f), caja.topLeft, Size(cw, 1f * u))
        listOf(0.55f, 0.85f).forEach { fx ->
            drawRect(t.c(ACERO), Offset(caja.left + cw * fx, caja.top + chh * 0.2f), Size(cw * 0.06f, chh * 0.24f))
            drawRect(t.b(0.6f), Offset(caja.left + cw * fx, caja.top + chh * 0.2f), Size(1f * u, chh * 0.24f))
        }
        // El asa, abatida sobre la tapa.
        val asa = Path().apply {
            moveTo(caja.left + cw * 0.3f, caja.top + dv.y * 0.5f); lineTo(caja.left + cw * 0.33f, caja.top - chh * 0.28f)
            lineTo(caja.left + cw * 0.67f, caja.top - chh * 0.28f); lineTo(caja.left + cw * 0.7f, caja.top + dv.y * 0.5f)
        }
        translate(cw * 0.04f, chh * 0.04f) { drawPath(asa, t.s(0.25f), style = Stroke(3f * u)) }
        drawPath(asa, t.c(mezcla(TINTA, OLIVA_OSC, 0.4f)), style = Stroke(3f * u, cap = StrokeCap.Round))
        drawLine(t.b(0.45f), Offset(caja.left + cw * 0.33f, caja.top - chh * 0.28f - 1f * u), Offset(caja.left + cw * 0.67f, caja.top - chh * 0.28f - 1f * u), 0.8f * u)
        // Pintura saltada y rayones.
        for (k in 0 until 4) drawOval(t.c(ACERO_OSC), Offset(caja.left + cw * (0.3f + 0.6f * azar(2210, k)), caja.top + chh * (0.4f + 0.5f * azar(2211, k))), Size(cw * 0.04f, chh * 0.08f))
    }

    /** La bombona verde azulada con su franja ocre, la válvula y el manómetro, cortada por el borde derecho. */
    private fun DrawScope.bombona(pie: Offset, w: Float, hb: Float) {
        val u = density
        val a = w * 0.13f; val al = hb * 0.6f
        val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
        val t = tn(r.center)
        contacto(Offset(pie.x, pie.y), a * 1.3f, 6f * u, if (esNoche) 0.6f else 0.4f)
        val cuerpo = Path().apply {
            moveTo(r.left, r.bottom - a * 0.06f)
            lineTo(r.left, r.top + a * 0.35f)
            cubicTo(r.left, r.top, r.right, r.top, r.right, r.top + a * 0.35f)
            lineTo(r.right, r.bottom - a * 0.06f)
            close()
        }
        drawPath(cuerpo, t.c(TEAL))
        clipPath(cuerpo) {
            cilindro(r, t, 1.2f)
            drawRect(t.c(OCRE), Offset(r.left, r.top + al * 0.32f), Size(a, al * 0.1f))
            drawRect(t.s(0.2f), Offset(r.left + a * 0.6f, r.top + al * 0.32f), Size(a * 0.4f, al * 0.1f))
            drawOval(t.b(0.3f), Offset(r.left + a * 0.15f, r.top + a * 0.05f), Size(a * 0.3f, a * 0.12f))
            for (k in 0 until 3) drawOval(t.c(ACERO_OSC), Offset(r.left + a * (0.2f + 0.2f * k), r.top + al * (0.55f + 0.12f * k)), Size(a * 0.05f, al * 0.03f))
        }
        // La válvula de latón oscuro y el manómetro, a la izquierda.
        val v = Rect(pie.x - a * 0.08f, r.top - hb * 0.1f, pie.x + a * 0.08f, r.top + a * 0.05f)
        drawRect(t.c(mezcla(OCRE, ACERO_OSC, 0.5f)), v.topLeft, v.size)
        drawRect(t.b(0.45f), v.topLeft, Size(1f * u, v.height))
        val mc = Offset(v.left - a * 0.16f, v.top + v.height * 0.35f)
        val rm = a * 0.13f
        drawLine(t.c(ACERO_OSC), mc, Offset(v.left, mc.y), 2.4f * u)
        drawCircle(t.s(0.3f), rm, mc + Offset(1.2f * u, 1.2f * u))
        drawCircle(t.c(ACERO), rm, mc)
        drawCircle(t.c(ETIQUETA), rm * 0.8f, mc)
        drawArc(t.c(NARANJA), 300f, 40f, false, Offset(mc.x - rm * 0.6f, mc.y - rm * 0.6f), Size(rm * 1.2f, rm * 1.2f), style = Stroke(1.2f * u))
        drawLine(t.c(TINTA), mc, mc + Offset(rm * 0.5f, -rm * 0.4f), 0.8f * u)
        drawArc(t.b(0.6f), 190f, 70f, false, Offset(mc.x - rm * 0.8f, mc.y - rm * 0.8f), Size(rm * 1.6f, rm * 1.6f), style = Stroke(0.8f * u))
        drawCircle(t.c(NARANJA), a * 0.07f, Offset(v.center.x, v.top))
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega del fósforo: las filas de
        // arriba cerca de la terminal, encendidas; cada fila hacia abajo, más perdida.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val kt = if (!oscuro) 0f else if (dentro) luzTerminal(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.06f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, kt, 0f, dia)
        val piso = h * PISO
        // El fondo de la ranura y el aro del marco (ver [cubreAura]); luego lo que está metido en ella.
        fondoHueco(Rect(0f, 0f, w, h), c.fila, c.filas, c.col, Tono(oscuro, kt, 0f, dia))
        cubreAura(w, h, t)
        if (c.vacio) { tapaCiega(w, h, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> tarjetas(w, h, piso, c, t)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> cartucho(w, h, piso, c, m, t)
                    1 -> bobinas(w, h, piso, c, m, t)
                    else -> valvulas(w, h, piso, c, t)
                }
            }
        }
    }

    /** La etiqueta del tema: papel crema con su franja de color y su sombra; de noche, más hundida cuanto más lejos de la terminal. */
    private fun DrawScope.etiqueta(w: Float, h: Float, franja: Color?, t: Tono, papel: Color = ETIQUETA) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t.s(0.22f), Offset(r.left + 1f * u, r.top + 1.2f * u), r.size)
        val fondo = if (t.o) mezcla(mezcla(papel, ETIQUETA_LEJOS, 0.9f), mezcla(papel, FOSFORO_CLARO, 0.1f), t.kt.pow(0.8f)) else t.c(papel)
        drawRect(fondo, r.topLeft, r.size)
        if (franja != null) drawRect(t.c(franja), r.topLeft, Size(r.width, 2.4f * u))
        drawRect(t.s(0.12f), Offset(r.right - r.width * 0.05f, r.top), Size(r.width * 0.05f, r.height))
    }

    /**
     * El cartucho de cinta en su ranura: la carcasa de plástico (gris, crema u oliva según la semilla) con sus estrías, la
     * ventana ahumada con los dos carretes (el de la izquierda más lleno cuanto más guarda) y la cinta que pasa entre ellos, el
     * LED arriba a la derecha y la etiqueta.
     */
    private fun DrawScope.cartucho(w: Float, h: Float, piso: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val col = when (Math.floorMod(m ushr 3, 3)) { 0 -> Color(0xFF50544F); 1 -> Color(0xFFCFC6AA); else -> Color(0xFF66705A) }
        val cuerpo = Rect(w * 0.04f, h * 0.1f, w * 0.96f, piso)
        val forma = Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(3f * u))) }
        drawPath(forma, t.c(col))
        clipPath(forma) {
            drawRect(t.b(0.2f), cuerpo.topLeft, Size(cuerpo.width * 0.06f, cuerpo.height))
            drawRect(t.s(0.2f), Offset(cuerpo.right - cuerpo.width * 0.08f, cuerpo.top), Size(cuerpo.width * 0.08f, cuerpo.height))
            drawRect(t.b(0.45f), cuerpo.topLeft, Size(cuerpo.width, 1.2f * u))
            drawRect(t.s(0.3f), Offset(cuerpo.left, cuerpo.bottom - 2f * u), Size(cuerpo.width, 2f * u))
        }
        // Las estrías para agarrarlo, arriba a la izquierda.
        for (k in 0 until 4) {
            val y = cuerpo.top + h * (0.03f + 0.022f * k)
            drawRect(t.s(0.3f), Offset(w * 0.1f, y), Size(w * 0.2f, 0.9f * u))
            drawRect(t.b(0.3f), Offset(w * 0.1f, y + 0.9f * u), Size(w * 0.2f, 0.5f * u))
        }
        // La ventana con los carretes.
        val ven = Rect(w * 0.13f, h * 0.17f, w * 0.87f, h * 0.51f)
        val venP = Path().apply { addRoundRect(RoundRect(ven, CornerRadius(ven.height * 0.2f))) }
        drawPath(venP, t.c(Color(0xFF1C2320)))
        clipPath(venP) {
            val rMax = ven.height * 0.47f
            val lleno = c.n.coerceIn(0, 10) / 10f
            val cintaC = t.c(Color(0xFF4A3524))
            listOf(Offset(ven.left + ven.width * 0.27f, ven.center.y) to (0.42f + 0.55f * lleno), Offset(ven.right - ven.width * 0.27f, ven.center.y) to (0.42f + 0.5f * (1f - lleno))).forEachIndexed { i, (o, f) ->
                drawCircle(cintaC, rMax * f, o)
                drawCircle(t.s(0.3f), rMax * f, o, style = Stroke(0.7f * u))
                drawCircle(t.b(0.12f), rMax * f * 0.75f, o, style = Stroke(0.6f * u))
                drawCircle(t.c(ETIQUETA), rMax * 0.32f, o)
                for (k in 0 until 3) {
                    val a = k * 2.094f + azar(c.semilla, i) * 2f
                    drawCircle(t.c(Color(0xFF1C2320)), rMax * 0.07f, Offset(o.x + rMax * 0.19f * cos(a), o.y + rMax * 0.19f * sin(a)))
                }
            }
            // La cinta que pasa por abajo, de carrete a carrete.
            drawLine(cintaC, Offset(ven.left + ven.width * 0.12f, ven.bottom - ven.height * 0.08f), Offset(ven.right - ven.width * 0.12f, ven.bottom - ven.height * 0.08f), 1.4f * u)
            // El reflejo del plástico ahumado.
            drawPath(poligono(ven.left + ven.width * 0.06f, ven.bottom, ven.left + ven.width * 0.2f, ven.top, ven.left + ven.width * 0.3f, ven.top, ven.left + ven.width * 0.16f, ven.bottom), t.b(0.14f))
        }
        drawRect(t.s(0.35f), Offset(ven.left + 3f * u, ven.top), Size(ven.width - 6f * u, 1.4f * u))
        drawRect(t.b(0.35f), Offset(ven.left + 3f * u, ven.bottom - 0.8f * u), Size(ven.width - 6f * u, 0.8f * u))
        // El LED: verde o ámbar, encendido (luz propia).
        val led = Offset(w * 0.86f, h * 0.135f)
        val lc = if (Math.floorMod(m ushr 6, 2) == 0) FOSFORO else AMBAR
        drawCircle(lc.copy(alpha = 0.25f), 4f * u, led)
        drawCircle(lc, 1.8f * u, led)
        drawCircle(Color.White.copy(alpha = 0.6f), 0.6f * u, led - Offset(0.5f * u, 0.5f * u))
        // Dos tornillos abajo, a los lados de la etiqueta.
        etiqueta(w, h, TEAL, t)
    }

    /**
     * Una pila de latas de bobina tumbadas (una, dos o tres según cuánto guarda), de acero o pintadas de verde azulado según la
     * semilla: la de abajo, honda, con la etiqueta del tema ceñida; las de encima, planas, cada una corrida un poco.
     */
    private fun DrawScope.bobinas(w: Float, h: Float, piso: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val metal = if (Math.floorMod(m ushr 4, 2) == 0) mezcla(ACERO, CREMA, 0.15f) else mezcla(TEAL, ACERO, 0.3f)
        val n = 1 + (if (c.n >= 3) 1 else 0) + (if (c.n >= 7) 1 else 0)
        val rx = w * 0.44f
        val ry = h * 0.055f
        var abajo = piso - ry * 0.6f
        for (i in 0 until n) {
            val alto = if (i == 0) abajo - h * 0.53f else h * 0.1f
            val cx = w * 0.5f + (if (i == 0) 0f else w * 0.025f * (if ((i + m) % 2 == 0) 1f else -1f))
            val arriba = abajo - alto
            val r = Rect(cx - rx, arriba, cx + rx, abajo)
            val lado = Path().apply {
                moveTo(r.left, r.top); lineTo(r.left, r.bottom)
                arcTo(Rect(r.left, r.bottom - ry, r.right, r.bottom + ry), 180f, -180f, false)
                lineTo(r.right, r.top); close()
            }
            drawPath(lado, t.c(metal))
            clipPath(lado) {
                cilindro(Rect(r.left, r.top, r.right, r.bottom + ry), t, 1.1f)
                // Los rebordes de la lata.
                listOf(r.top + 2f * u, r.bottom - 1f * u).forEach { y -> drawArc(t.s(0.3f), 0f, 180f, false, Offset(r.left, y - ry), Size(r.width, ry * 2), style = Stroke(0.8f * u)) }
            }
            // En algunas, la tapa de la de arriba está corrida: se ve la bobina de cinta dentro.
            if (i == n - 1 && Math.floorMod(m ushr 7, 2) == 0) {
                drawOval(t.c(mezcla(metal, NOCHE, 0.55f)), Offset(r.left + rx * 0.04f, r.top - ry * 0.9f), Size(r.width - rx * 0.08f, ry * 1.8f))
                drawOval(t.c(Color(0xFF4A3524)), Offset(cx - rx * 0.78f, r.top - ry * 0.72f), Size(rx * 1.56f, ry * 1.44f))
                drawOval(t.b(0.15f), Offset(cx - rx * 0.6f, r.top - ry * 0.55f), Size(rx * 1.2f, ry * 1.1f), style = Stroke(0.6f * u))
                drawOval(t.c(ETIQUETA), Offset(cx - rx * 0.2f, r.top - ry * 0.2f), Size(rx * 0.4f, ry * 0.4f))
                // La tapa, apoyada de canto contra el fondo de la ranura, detrás.
                val tapa = Rect(cx + rx * 0.15f, r.top - ry * 3.2f, cx + rx * 1.02f, r.top - ry * 0.4f)
                rotate(-8f, tapa.center) {
                    drawOval(t.c(mezcla(metal, Color.White, 0.12f)), tapa.topLeft, tapa.size)
                    drawOval(t.s(0.2f), Offset(tapa.left + tapa.width * 0.5f, tapa.top), Size(tapa.width * 0.5f, tapa.height))
                }
                abajo = arriba
                continue
            }
            // La tapa: el aro, el fondo hundido y el centro.
            drawOval(t.c(mezcla(metal, Color.White, 0.12f)), Offset(r.left, r.top - ry), Size(r.width, ry * 2))
            drawOval(t.c(mezcla(metal, NOCHE, 0.12f)), Offset(r.left + rx * 0.08f, r.top - ry * 0.8f), Size(r.width - rx * 0.16f, ry * 1.6f))
            drawOval(t.c(metal), Offset(cx - rx * 0.25f, r.top - ry * 0.3f), Size(rx * 0.5f, ry * 0.6f))
            drawArc(t.b(0.55f), 160f, 100f, false, Offset(r.left + 0.5f * u, r.top - ry), Size(r.width - 1f * u, ry * 2), style = Stroke(0.9f * u))
            abajo = arriba
        }
        etiqueta(w, h, OCRE, t)
    }

    /**
     * El módulo de válvulas: la caja de chapa con su asa, la ventana con la rejilla y detrás las válvulas de vidrio con su brasa
     * naranja (luz propia; más encendidas cuanto más guarda) y la etiqueta.
     */
    private fun DrawScope.valvulas(w: Float, h: Float, piso: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val caja = Rect(w * 0.05f, h * 0.2f, w * 0.95f, piso)
        // El asa, de varilla, arriba.
        val asa = Path().apply { moveTo(w * 0.32f, caja.top); lineTo(w * 0.34f, h * 0.08f); lineTo(w * 0.66f, h * 0.08f); lineTo(w * 0.68f, caja.top) }
        drawPath(asa, t.c(ACERO), style = Stroke(3f * u, cap = StrokeCap.Round))
        drawLine(t.b(0.6f), Offset(w * 0.34f, h * 0.08f - 1f * u), Offset(w * 0.66f, h * 0.08f - 1f * u), 0.9f * u)
        val metal = mezcla(ACERO, OLIVA, 0.4f)
        drawRect(t.c(metal), caja.topLeft, caja.size)
        drawRect(t.b(0.25f), caja.topLeft, Size(caja.width * 0.06f, caja.height))
        drawRect(t.s(0.2f), Offset(caja.right - caja.width * 0.08f, caja.top), Size(caja.width * 0.08f, caja.height))
        drawRect(t.b(0.5f), caja.topLeft, Size(caja.width, 1.2f * u))
        drawRect(t.s(0.3f), Offset(caja.left, caja.bottom - 2f * u), Size(caja.width, 2f * u))
        listOf(Offset(caja.left + 4f * u, caja.top + 4f * u), Offset(caja.right - 4f * u, caja.top + 4f * u)).forEach { p ->
            drawCircle(t.c(ACERO_OSC), 1.5f * u, p)
            drawLine(t.b(0.4f), p - Offset(1f * u, 0f), p + Offset(1f * u, 0f), 0.6f * u)
        }
        // La ventana: el hueco oscuro, las válvulas, la rejilla.
        val ven = Rect(w * 0.12f, h * 0.27f, w * 0.88f, h * 0.535f)
        drawRect(t.c(Color(0xFF2A2820)), ven.topLeft, ven.size)
        clipRect(ven.left, ven.top, ven.right, ven.bottom) {
            val total = 5
            val encendidas = c.n.coerceIn(1, total)
            val paso = ven.width / total
            for (k in 0 until total) {
                val x = ven.left + paso * (k + 0.5f)
                val vw = paso * 0.58f
                val vh = ven.height * (0.72f + 0.1f * azar(c.semilla, k))
                val vid = Rect(x - vw / 2, ven.bottom - vh, x + vw / 2, ven.bottom + vw * 0.2f)
                val enc = k < encendidas
                if (enc) { drawCircle(NARANJA.copy(alpha = 0.22f), vw * 1.1f, Offset(x, ven.bottom - vh * 0.4f)); drawCircle(NARANJA.copy(alpha = 0.3f), vw * 0.7f, Offset(x, ven.bottom - vh * 0.42f)) }
                drawRoundRect(t.c(mezcla(ACERO, CREMA, 0.55f)).copy(alpha = 0.5f), vid.topLeft, vid.size, CornerRadius(vw / 2))
                // La placa de dentro y el filamento.
                drawRect(t.c(ACERO_OSC), Offset(x - vw * 0.25f, ven.bottom - vh * 0.7f), Size(vw * 0.5f, vh * 0.45f))
                if (enc) {
                    drawRect(NARANJA, Offset(x - vw * 0.08f, ven.bottom - vh * 0.6f), Size(vw * 0.16f, vh * 0.28f))
                    drawRect(AMBAR, Offset(x - vw * 0.04f, ven.bottom - vh * 0.55f), Size(vw * 0.08f, vh * 0.16f))
                }
                drawLine(t.b(0.5f), Offset(vid.left + vw * 0.2f, vid.top + vw * 0.35f), Offset(vid.left + vw * 0.2f, vid.bottom - vh * 0.4f), 0.9f * u)
            }
            var y = ven.top + 2f * u
            while (y < ven.bottom) {
                drawRect(t.c(metal), Offset(ven.left, y), Size(ven.width, 1f * u))
                drawRect(t.b(0.3f), Offset(ven.left, y), Size(ven.width, 0.4f * u))
                y += 5.5f * u
            }
            drawRect(t.s(0.45f), ven.topLeft, Size(ven.width, 2.2f * u))
            drawRect(t.s(0.3f), ven.topLeft, Size(2f * u, ven.height))
        }
        drawRect(t.b(0.4f), Offset(ven.left, ven.bottom), Size(ven.width, 1f * u))
        etiqueta(w, h, NARANJA, t)
    }

    /** Lo que no tiene tema: una caja de cartón con tarjetas perforadas (más cuanto más guarda), una de separador, y una etiqueta en blanco. */
    private fun DrawScope.tarjetas(w: Float, h: Float, piso: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val caja = Rect(w * 0.05f, h * 0.5f, w * 0.95f, piso)
        val kraft = Color(0xFFAE8B58)
        // La pared de atrás de la caja.
        drawRect(t.c(mezcla(kraft, OLIVA_OSC, 0.35f)), Offset(caja.left + w * 0.03f, caja.top - h * 0.06f), Size(caja.width - w * 0.06f, h * 0.08f))
        val capas = (2 + c.n).coerceIn(3, 8)
        for (i in capas - 1 downTo 0) {
            val y = caja.top - h * (0.04f + 0.045f * i)
            val dx = w * 0.02f * (azar(c.semilla, i) - 0.5f)
            val r = Rect(caja.left + w * 0.06f + dx, y, caja.right - w * 0.06f + dx, caja.top + 2f * u)
            val esq = h * 0.035f
            val tarjeta = poligono(r.left + esq, r.top, r.right, r.top, r.right, r.bottom, r.left, r.bottom, r.left, r.top + esq)
            drawPath(tarjeta, t.c(if (i % 2 == 0) ETIQUETA else mezcla(ETIQUETA, OCRE, 0.25f)))
            drawRect(t.s(0.25f), Offset(r.left, r.top + h * 0.03f), Size(r.width, h * 0.02f))
            drawRect(t.b(0.4f), Offset(r.left + esq, r.top), Size(r.width - esq, 0.7f * u))
            // Las perforaciones de la franja que asoma.
            for (k in 0 until 9) {
                val px = r.left + r.width * (0.08f + 0.88f * azar(c.semilla + 7, i * 10 + k))
                drawRect(t.c(TINTA).copy(alpha = 0.55f), Offset(px, r.top + h * 0.012f), Size(1.2f * u, h * 0.018f))
            }
        }
        // Un separador verde azulado, más alto y ladeado.
        rotate(-5f, Offset(w * 0.7f, caja.top)) {
            drawRect(t.c(TEAL), Offset(w * 0.58f, caja.top - h * (0.1f + 0.045f * capas)), Size(w * 0.2f, h * 0.1f))
            drawRect(t.b(0.4f), Offset(w * 0.58f, caja.top - h * (0.1f + 0.045f * capas)), Size(w * 0.2f, 0.8f * u))
        }
        // El frente de cartón.
        drawRect(t.c(kraft), caja.topLeft, caja.size)
        drawRect(t.b(0.25f), caja.topLeft, Size(caja.width * 0.06f, caja.height))
        drawRect(t.s(0.2f), Offset(caja.right - caja.width * 0.08f, caja.top), Size(caja.width * 0.08f, caja.height))
        drawRect(t.c(mezcla(kraft, CREMA, 0.3f)), Offset(caja.left, caja.top - 1.5f * u), Size(caja.width, 3f * u))
        pinceladas(caja, if (t.o) NOCHE else mezcla(kraft, TINTA, 0.4f), c.semilla, 5, u, 0.12f)
        etiqueta(w, h, null, t, mezcla(ETIQUETA, kraft, 0.18f))
    }

    /** Una ranura sin tema: la tapa ciega atornillada, con su rectángulo estampado, unos rayones y el piloto apagado. */
    private fun DrawScope.tapaCiega(w: Float, h: Float, t: Tono) {
        val u = density
        val tapa = Rect(w * 0.02f, h * 0.04f, w * 0.98f, h * 0.97f)
        drawRect(t.c(mezcla(OLIVA, ACERO, 0.35f)), tapa.topLeft, tapa.size)
        drawRect(t.b(0.25f), tapa.topLeft, Size(tapa.width * 0.06f, tapa.height))
        drawRect(t.s(0.22f), Offset(tapa.right - tapa.width * 0.08f, tapa.top), Size(tapa.width * 0.08f, tapa.height))
        drawRect(t.b(0.5f), tapa.topLeft, Size(tapa.width, 1.2f * u))
        drawRect(t.s(0.35f), Offset(tapa.left, tapa.bottom - 2f * u), Size(tapa.width, 2f * u))
        // El estampado: un rectángulo hundido.
        val es = Rect(w * 0.14f, h * 0.24f, w * 0.86f, h * 0.76f)
        drawRect(t.s(0.25f), es.topLeft, Size(es.width, 1.2f * u))
        drawRect(t.s(0.2f), es.topLeft, Size(1.2f * u, es.height))
        drawRect(t.b(0.35f), Offset(es.left, es.bottom - 1f * u), Size(es.width, 1f * u))
        drawRect(t.b(0.25f), Offset(es.right - 1f * u, es.top), Size(1f * u, es.height))
        // Los tornillos de cabeza ranurada.
        listOf(Offset(w * 0.07f, h * 0.1f), Offset(w * 0.93f, h * 0.1f), Offset(w * 0.07f, h * 0.9f), Offset(w * 0.93f, h * 0.9f)).forEachIndexed { i, p ->
            drawCircle(t.s(0.35f), 2.6f * u, p + Offset(0.8f * u, 0.8f * u))
            drawCircle(t.c(ACERO), 2.5f * u, p)
            rotate(30f + 50f * i, p) { drawLine(t.s(0.5f), p - Offset(2f * u, 0f), p + Offset(2f * u, 0f), 0.8f * u) }
        }
        // Rayones.
        for (k in 0 until 3) drawLine(t.b(0.25f), Offset(w * (0.2f + 0.2f * k), h * (0.8f + 0.03f * k)), Offset(w * (0.32f + 0.2f * k), h * (0.78f + 0.03f * k)), 0.6f * u)
        // El piloto apagado, arriba a la derecha.
        val p = Offset(w * 0.8f, h * 0.15f)
        drawCircle(t.c(ACERO), 3.6f * u, p)
        drawCircle(t.c(mezcla(AMBAR, OLIVA_OSC, 0.75f)), 2.4f * u, p)
        drawCircle(t.b(0.35f), 0.8f * u, p - Offset(0.8f * u, 0.8f * u))
    }

    override val rotulo = Rect(0.08f, 0.58f, 0.92f, 0.89f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.vt323
    override val salida = -SALIDA
    override val placa = Rect(0.32f, 0.04f, 0.68f, 0.26f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La pantalla de fósforo verde: viñeta, líneas de barrido y el bisel abombado.
        drawRect(Brush.radialGradient(listOf(Color(0xFF0A3A18), NEGRO), Offset(w / 2, h / 2), maxOf(w, h) * 0.7f))
        var y = 0f
        while (y < h) { drawLine(FOSFORO.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        val b = 16 * u
        drawRoundRect(t(OLIVA_OSC, oscuro), Offset(b / 2, b / 2), Size(w - b, h - b), CornerRadius(28 * u), style = Stroke(b))
        drawRect(t(OLIVA, oscuro), Offset.Zero, size, style = Stroke(6 * u))
        drawRoundRect(Color.White.copy(alpha = 0.06f), Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(22 * u), style = Stroke(2 * u))
    }

    override fun tintaInterior(oscuro: Boolean) = FOSFORO_CLARO
    override val pared: Dp = 20.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(14.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El barrido, la viñeta en los bordes, el bisel y el cursor de bloque.
        var y = 0f
        while (y < h) { drawLine(FOSFORO.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        val v = 10 * u * g
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), 0f, v), Offset.Zero, Size(w, v))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)), h - v, h), Offset(0f, h - v), Size(w, v))
        drawRoundRect(OLIVA_OSC, Offset(1.5f * u * g, 1.5f * u * g), Size(w - 3 * u * g, h - 3 * u * g), CornerRadius(12 * u * g), style = Stroke(3 * u * g))
        drawRect(FOSFORO.copy(alpha = 0.7f), Offset(w - 16 * u * g, h - 18 * u * g), Size(6 * u * g, 10 * u * g))
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 14.dp, 26.dp, 28.dp, 30.dp)
    override val proporcionFicha = 1.15f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(FOSFORO, p.color)
            "convence" -> {
                drawCircle(FOSFORO, s * 0.42f, c, style = Stroke(s * 0.08f))
                drawPath(Path().apply { moveTo(c.x - s * 0.2f, c.y); lineTo(c.x - s * 0.04f, c.y + s * 0.16f); lineTo(c.x + s * 0.22f, c.y - s * 0.16f) }, FOSFORO, style = Stroke(s * 0.1f))
            }
            // El átomo.
            "piensa" -> {
                listOf(0f, 60f, 120f).forEach { a -> rotate(a, c) { drawOval(AMBAR, Offset(c.x - s * 0.44f, c.y - s * 0.16f), Size(s * 0.88f, s * 0.32f), style = Stroke(s * 0.05f)) } }
                drawCircle(AMBAR, s * 0.08f, c)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(AMBAR)
            else -> {}
        }
    }

    /** Una chapa de identificación con su cadenita. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRoundRect(ACERO, Offset(c.x - s * 0.24f, c.y - s * 0.3f), Size(s * 0.48f, s * 0.7f), CornerRadius(s * 0.14f))
        drawCircle(Color.Black.copy(alpha = 0.6f), s * 0.05f, Offset(c.x, c.y - s * 0.2f))
        for (k in 0 until 3) drawCircle(ACERO_OSC, s * 0.04f, Offset(c.x - s * 0.08f * k, c.y - s * 0.34f - s * 0.06f * k))
        for (k in 0 until 3) drawLine(ACERO_OSC, Offset(c.x - s * 0.14f, c.y + s * (0.0f + 0.1f * k)), Offset(c.x + s * 0.14f, c.y + s * (0.0f + 0.1f * k)), s * 0.03f)
    }

    override val desorden = 0f
}
