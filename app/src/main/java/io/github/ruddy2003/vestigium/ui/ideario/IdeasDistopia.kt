package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Distopía
// =====================================================================

/**
 * Distopía: el rincón del archivo en un sótano del ministerio, donde un funcionario pasa a máquina los expedientes. La pared
 * es de oficina pública, pintada a dos alturas (verde gris institucional abajo, crema sucio arriba, con la raya oliva que las
 * separa), con desconchones, una tubería con su abrazadera oxidada y la mancha de humedad que baja de ella, y el rodapié
 * rozado. No hay ventana: de día la luz es la de un tubo fluorescente fuera del cuadro, arriba a la izquierda, fría y un poco
 * verdosa, y deja la sombra del archivero y de cada cosa en la pared. El mueble es un archivero doble de acero pintado de gris
 * verdoso, con abolladuras, la pintura saltada en los cantos por el roce, el número de inventario estarcido, una pegatina de
 * precinto rota y un zócalo con rejilla. Encima, lo primero que se ve: la máquina de escribir de carro ancho, con una hoja a
 * medio pasar; a su izquierda, el carrusel de sellos de goma y el tampón rojo abierto; a su derecha, el flexo de pantalla
 * metálica doblado sobre ella, el teléfono de disco, una taza de esmalte desportillada y una pila de carpetas de manila atada
 * con cinta. En la pared, el reloj de oficina, el cartel del ojo (régimen inventado, sin texto legible) medio despegado, con
 * una esquina rota y la solapa doblada, clavado con chinchetas; el altavoz de megafonía en su caja y la cámara de vigilancia
 * en su brazo, con su luz roja. Al pie, la papelera de rejilla con tiras de papel y una caja de archivo de cartón cortada por
 * el borde. Cada tema es una gaveta con su tarjeta en el tarjetero, según la semilla: con tirador de barra y la franja roja
 * de «clasificado»; con pasador, candado y un precinto de plomo con su cordel; o doble, de fichero, con dos tiradores de
 * concha y una hoja pillada en la rendija. Las más llenas quedan entreabiertas y asoman las carpetas (más cuantas más
 * fichas). Lo que no tiene tema lleva el tarjetero vacío y un papel cuadriculado pegado con cinta; un hueco sin tema es una
 * gaveta precintada en cruz o el hueco sin gaveta, con los rieles a la vista. De noche el sótano se hunde en gris azulado:
 * alumbra el flexo (cono cálido sobre la máquina, halo en la pared, la hoja encendida) y la luz roja de la cámara deja su
 * resplandor pequeño; las gavetas se apagan con la distancia al flexo hacia su propio oscuro, y las de abajo quedan casi
 * perdidas, con un filo en los tiradores. Por dentro, la gaveta de acero con sus rieles para carpetas colgantes. Las fichas
 * son expedientes en su carpeta de manila con pestaña.
 */
object IdeasDistopia : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del sótano (el mueble), sacada del cuadro
    /** El crema sucio de arriba, el verde gris institucional de abajo y el oliva de la raya, del esmalte de la máquina y del flexo. */
    private val CREMA = Color(0xFFDCD5BA)
    private val VERDE = Color(0xFF7E8B76)
    private val OLIVA = Color(0xFF545E40)
    /** La pintura del archivero, gris verdosa, con su luz y su sombra. */
    private val ACERO = Color(0xFF8C948A)
    private val ACERO_LUZ = Color(0xFFBEC2B4)
    private val ACERO_OSC = Color(0xFF4E5550)
    /** El hierro que asoma donde salta la pintura; la tubería y los herrajes. */
    private val HIERRO = Color(0xFF3E4246)
    /** La manila de las carpetas, el cartón (y la madera de los sellos). */
    private val CARPETA = Color(0xFFD6BB80)
    private val CARTON = Color(0xFFA5845A)
    /** El papel hueso: hojas, tarjetas, el esmalte de la taza y lo que asoma en los desconchones. */
    private val HUESO = Color(0xFFEDE6D0)
    /** El rojo de cartel (y del tampón), y el burdeos de la cinta. */
    private val CARTEL = Color(0xFFB5372B)
    private val BURDEOS = Color(0xFF7C2820)
    /** La baquelita del teléfono y del rodillo; también la tinta de los rótulos. */
    private val BAQUELITA = Color(0xFF1E2024)
    /** El óxido de la abrazadera y la humedad. */
    private val OXIDO = Color(0xFF8E5C3C)
    /** La luz del tubo fluorescente (crema fría), la noche del sótano (gris azulado hondo), la bombilla del flexo y el piloto de la cámara. */
    private val LUZ_TUBO = Color(0xFFF2F2DA)
    private val NOCHE = Color(0xFF121822)
    private val FLEXO = Color(0xFFFFCF86)
    private val PILOTO = Color(0xFFFF4A3A)

    /**
     * La tarjeta de los rótulos de noche: de [TARJETA_NOCHE] (lejos del flexo) a [TARJETA_LUZ] (cerca). Con la tinta
     * [BAQUELITA], la de lejos da unos 7.4:1 en el título y unos 4.8:1 en la cuenta (78 %).
     */
    private val TARJETA_NOCHE = Color(0xFFB6B09E)
    private val TARJETA_LUZ = Color(0xFFF0E2C2)

    // ---------------------------------------------------------------- de la paleta de siempre (el interior y las fichas)
    private val GRIS = Color(0xFF6E7378)
    private val GRIS_LUZ = Color(0xFF9AA0A6)
    private val GRIS_OSC = Color(0xFF3E4246)
    private val ROJO = Color(0xFFC8102E)
    private val NEGRO = Color(0xFF141414)
    private val MANILA = Color(0xFFD9C48E)
    private val PAPEL = Color(0xFFECE8DC)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- la luz y la noche

    /** La boca del flexo y la luz roja de la cámara, en fracciones del ancho. */
    private val BOMBILLA = Offset(0.457f, 0.352f)
    private val LED = Offset(0.821f, 0.208f)

    /** Cuánto alumbra el flexo el punto [p] (fracciones del ancho): cae con la distancia; la última fila de gavetas, a oscuras. */
    private fun deFlexo(p: Offset) = (1.1f * (1f - (p - BOMBILLA).getDistance() / 1.25f)).coerceIn(0f, 1f)

    /** Cuánto tiñe la luz roja de la cámara el punto [p]: solo muy cerca. */
    private fun deLed(p: Offset) = (1f - (p - LED).getDistance() / 0.13f).coerceIn(0f, 1f)

    /**
     * Un color a [k] (0 a 1) del flexo: de día, tal cual. De noche se apaga hacia **su propio** oscuro (baja el valor y
     * conserva el matiz: la manila se vuelve parda, el verde oliva hondo), con un poco del gris azulado del sótano para unir
     * la paleta; cerca del flexo sube y se entibia. [rojo] es lo que le llega de la luz de la cámara.
     */
    private fun n(c: Color, o: Boolean, k: Float, rojo: Float = 0f): Color {
        if (!o) return c
        val q = k.coerceIn(0f, 1f)
        val v = 0.2f + 0.68f * q.pow(1.15f)
        val bajo = Color(c.red * v, c.green * v, c.blue * v, c.alpha)
        val r = mezcla(mezcla(bajo, NOCHE, 0.36f * (1f - q)), FLEXO, 0.18f * q)
        return if (rojo > 0f) mezcla(r, PILOTO, 0.3f * rojo) else r
    }

    /** El color [col] en el punto (x, y) del mueble, en fracciones del ancho. */
    private fun cc(col: Color, o: Boolean, x: Float, y: Float) =
        n(col, o, kk(o, x, y), if (o) deLed(Offset(x, y)) else 0f)

    /**
     * Cuánto alumbra el flexo lo que hay en (x, y): como [deFlexo], pero la pantalla tapa la luz hacia arriba, así que lo que
     * queda por encima de la boca (el reloj, el altavoz, lo alto del cartel) recibe mucho menos.
     */
    private fun kk(o: Boolean, x: Float, y: Float): Float {
        if (!o) return 0f
        val tapa = (1f - 2.6f * (BOMBILLA.y - y).coerceAtLeast(0f)).coerceIn(0.2f, 1f)
        return deFlexo(Offset(x, y)) * tapa
    }

    /** La sombra: de día, violeta gris; de noche, gris azulado casi negro. */
    private fun sombra(o: Boolean, a: Float) = if (o) Color(0xFF0A0D14).copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** Lo que mira a la luz: de día, la crema fría del tubo; de noche, el filo cálido del flexo según lo cerca que esté. */
    private fun brillo(o: Boolean, a: Float, k: Float = 0f) =
        if (o) FLEXO.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f)) else LUZ_TUBO.copy(alpha = a.coerceIn(0f, 1f))

    private fun tarjeta(o: Boolean, k: Float) = if (o) mezcla(TARJETA_NOCHE, TARJETA_LUZ, k * 1.4f) else HUESO

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 1.85f
    override val corona = 0.64f
    override val base = 0.24f
    override val lado = 0.075f
    override val hueco = 0.03f

    /** La raya entre las dos pinturas, el tablero del archivero (fondo, canto de frente y bajo del canto), en fracciones del ancho. */
    private const val RAYA = 0.392f
    private const val TAPA0 = 0.563f
    private const val TAPA1 = 0.585f
    private const val CANTO = 0.607f
    /** Los costados del archivero, en fracciones del ancho. */
    private const val IZQ = 0.035f
    private const val DER = 0.965f
    /** En la base: la línea de la pared con el suelo, el arranque del zócalo y donde pisa el archivero, en fracciones de su alto. */
    private const val SUELO = 0.3f
    private const val ZOCALO = 0.07f
    private const val PISA = 0.5f
    /** Cuánto baja el frente de la gaveta al abrirla, en fracciones de su alto. */
    private const val SALIDA = 0.16f

    // ---------------------------------------------------------------- ayudantes

    /** Un polígono con los puntos dados como x, y seguidos. */
    private fun pl(vararg v: Float) = Path().apply {
        moveTo(v[0], v[1]); var i = 2
        while (i < v.size) { lineTo(v[i], v[i + 1]); i += 2 }
        close()
    }

    /** Una forma irregular alrededor de [c], de radios [rx] y [ry]: desconchones, manchas, pintura saltada. */
    private fun irregular(c: Offset, rx: Float, ry: Float, semilla: Int, puntos: Int = 9, hondo: Float = 0.5f) = Path().apply {
        for (j in 0 until puntos) {
            val a = j * 6.2832f / puntos + 0.4f * azar(semilla, 40 + j)
            val f = 1f - hondo + hondo * 1.2f * azar(semilla, j)
            val p = Offset(c.x + rx * f * cos(a), c.y + ry * f * sin(a))
            if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /** La sombra de [p] en la pared: de día, a la derecha y abajo (el tubo está arriba a la izquierda); de noche, lejos del flexo. */
    private fun DrawScope.sombraEnPared(p: Path, x: Float, y: Float, o: Boolean, a: Float = 0.22f, largo: Float = 1f) {
        val w = size.width
        val dd = if (!o) Offset(0.016f, 0.011f) * largo else {
            val v = Offset(x, y) - BOMBILLA; val l = v.getDistance().coerceAtLeast(0.01f)
            v / l * (0.024f * largo)
        }
        val alfa = if (o) a * 1.6f * kk(true, x, y) + 0.12f else a
        translate(dd.x * w, dd.y * w) { drawPath(p, sombra(o, alfa)) }
    }

    /** Un desconchón: la capa vieja que asoma (más clara) con la sombra del borde de la pintura arriba a la izquierda. */
    private fun DrawScope.desconchon(x: Float, y: Float, r: Float, semilla: Int, o: Boolean) {
        val w = size.width; val u = density
        val p = irregular(Offset(x * w, y * w), r * w, r * w * 0.7f, semilla, 10, 0.55f)
        drawPath(p, sombra(o, 0.32f))
        translate(1.1f * u, 1.3f * u) { drawPath(p, cc(mezcla(HUESO, CREMA, 0.4f), o, x, y)) }
        for (j in 0 until 3) drawCircle(cc(mezcla(HUESO, CREMA, 0.4f), o, x, y), (0.6f + azar(semilla, 70 + j)) * u,
            Offset((x + r * (1.2f + 0.6f * azar(semilla, 80 + j)) * (if (j == 1) -1f else 1f)) * w, (y + r * (0.8f * azar(semilla, 90 + j) - 0.3f)) * w))
    }

    /** Pintura saltada por el roce: el hierro que asoma, con el borde de la pintura cogiendo la luz. */
    private fun DrawScope.saltado(c: Offset, r: Float, semilla: Int, o: Boolean, k: Float) {
        val u = density
        val p = irregular(c, r, r * 0.65f, semilla, 7, 0.6f)
        drawPath(p, n(HIERRO, o, k))
        translate(0.7f * u, 0.7f * u) { drawPath(p, brillo(o, 0.35f, k), style = Stroke(0.6f * u)) }
    }

    /** Las cifras de un número estarcido (siete trazos con sus puentes, sin letras). */
    private val SEGMENTOS = mapOf('0' to "abcdef", '1' to "bc", '2' to "abged", '3' to "abgcd", '4' to "fgbc", '5' to "afgcd", '6' to "afgedc",
        '7' to "abc", '8' to "abcdefg", '9' to "abfgcd", '-' to "g")

    private fun DrawScope.estarcido(texto: String, x: Float, y: Float, a: Float, al: Float, col: Color) {
        val g = maxOf(al * 0.19f, 1f * density)
        val m = al / 2
        val p = g * 0.22f
        texto.forEachIndexed { i, ch ->
            val x0 = x + i * a * 1.45f
            SEGMENTOS[ch]?.forEach { s ->
                when (s) {
                    'a' -> drawRect(col, Offset(x0 + g + p, y), Size(a - 2 * g - 2 * p, g))
                    'b' -> drawRect(col, Offset(x0 + a - g, y + g + p), Size(g, m - g - 2 * p))
                    'c' -> drawRect(col, Offset(x0 + a - g, y + m + p), Size(g, m - g - 2 * p))
                    'd' -> drawRect(col, Offset(x0 + g + p, y + al - g), Size(a - 2 * g - 2 * p, g))
                    'e' -> drawRect(col, Offset(x0, y + m + p), Size(g, m - g - 2 * p))
                    'f' -> drawRect(col, Offset(x0, y + g + p), Size(g, m - g - 2 * p))
                    'g' -> drawRect(col, Offset(x0 + g + p, y + m - g / 2), Size(a - 2 * g - 2 * p, g))
                }
            }
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        val w = size.width; val alto = size.height
        val b = d.base; val hb = b.height
        val suelo = b.top + hb * SUELO
        val pisa = b.top + hb * PISA

        // ---- la pared, el suelo y lo colgado
        muro(suelo, o)
        piso(suelo, alto, o)
        tuberia(o)
        reloj(o)
        cartel(o)
        altavoz(o)
        camara(o)

        // ---- el archivero
        archivero(d, o, suelo, pisa)

        // ---- encima, de atrás adelante
        carpetas(o)
        scale(1.28f, Offset(0.092f * w, 0.574f * w)) { carrusel(o) }
        telefono(o)
        maquina(o)
        flexo(o)
        taza(o)
        tampon(o)

        // ---- al pie, cortados por los bordes
        papelera(Rect(0.022f * w, alto - 0.165f * w, 0.2f * w, alto - 0.012f * w), o)
        caja(Rect(0.79f * w, alto - 0.14f * w, w + 0.06f * w, alto - 0.008f * w), o)

        // ---- de noche: la luz del flexo y la de la cámara, por encima de lo que tienen cerca
        if (o) {
            val m = Offset(BOMBILLA.x * w, BOMBILLA.y * w)
            val dir = Offset(-0.447f, 0.894f); val perp = Offset(0.894f, 0.447f)
            val m1 = BOMBILLA + perp * 0.036f; val m2 = BOMBILLA - perp * 0.036f
            val p1 = m1 + dir * 0.25f + perp * 0.06f; val p2 = m2 + dir * 0.25f - perp * 0.055f
            val cono = pl(m1.x * w, m1.y * w, p1.x * w, p1.y * w, p2.x * w, p2.y * w, m2.x * w, m2.y * w)
            drawPath(cono, Brush.radialGradient(listOf(FLEXO.copy(alpha = 0.26f), FLEXO.copy(alpha = 0.1f), FLEXO.copy(alpha = 0f)), m, w * 0.28f))
            mancha(Offset(0.34f * w, 0.57f * w), w * 0.2f, FLEXO.copy(alpha = 0.2f))
            mancha(m, w * 0.07f, FLEXO.copy(alpha = 0.45f))
            mancha(Offset(LED.x * w, LED.y * w), w * 0.05f, PILOTO.copy(alpha = 0.4f))
        } else mancha(Offset(LED.x * w, LED.y * w), w * 0.012f, PILOTO.copy(alpha = 0.35f))
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * La pared: crema sucio arriba y verde gris abajo, con la raya oliva; de día, la mancha de luz del tubo arriba a la izquierda;
     * de noche, el halo del flexo y lo demás hundido. Pinceladas, desconchones, la humedad y el rodapié rozado.
     */
    private fun DrawScope.muro(suelo: Float, o: Boolean) {
        val w = size.width; val u = density
        val yR = RAYA * w
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(CREMA, LUZ_TUBO, 0.45f), CREMA, mezcla(CREMA, VERDE, 0.28f)), 0f, w), Offset.Zero, Size(w, yR))
            drawRect(Brush.horizontalGradient(listOf(mezcla(VERDE, LUZ_TUBO, 0.2f), VERDE, mezcla(VERDE, OLIVA, 0.4f)), 0f, w), Offset(0f, yR), Size(w, suelo - yR))
            mancha(Offset(w * 0.06f, -w * 0.03f), w * 0.6f, LUZ_TUBO.copy(alpha = 0.55f))
        } else {
            val hc = Offset(BOMBILLA.x * w - w * 0.04f, BOMBILLA.y * w + w * 0.09f)
            drawRect(Brush.radialGradient(listOf(n(CREMA, true, 1f), n(CREMA, true, 0.62f), n(CREMA, true, 0.26f), n(CREMA, true, 0f)), hc, w * 0.72f),
                Offset.Zero, Size(w, yR))
            drawRect(Brush.radialGradient(listOf(n(VERDE, true, 1f), n(VERDE, true, 0.62f), n(VERDE, true, 0.26f), n(VERDE, true, 0f)), hc, w * 0.72f),
                Offset(0f, yR), Size(w, suelo - yR))
        }
        // La pintura dada a brochazos, y lo sucio de abajo.
        pinceladas(Rect(0f, 0f, w, yR), if (o) NOCHE else mezcla(CREMA, OXIDO, 0.35f), 5101, 28, u, 0.07f)
        pinceladas(Rect(0f, yR, w, suelo), if (o) NOCHE else OLIVA, 5102, 22, u, 0.08f)
        pinceladas(Rect(0f, suelo - w * 0.06f, w, suelo), if (o) NOCHE else OLIVA, 5103, 14, u, 0.12f)
        // La raya: un filete oliva dado a mano, un poco ondulado.
        val raya = Path().apply {
            moveTo(0f, yR - 0.005f * w)
            for (j in 1..8) lineTo(w * j / 8f, yR - 0.005f * w + (azar(5104, j) - 0.5f) * 1.2f * u)
            for (j in 8 downTo 0) lineTo(w * j / 8f, yR + 0.006f * w + (azar(5105, j) - 0.5f) * 1.2f * u)
            close()
        }
        drawPath(raya, if (o) n(OLIVA, true, 0.5f) else OLIVA)
        if (o) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, FLEXO.copy(alpha = 0.18f), Color.Transparent), w * 0.15f, w * 0.8f),
            Offset(0f, yR - 0.005f * w), Size(w, 0.011f * w))
        // La humedad que baja de la abrazadera de la tubería: manchas con su cerco.
        val humedad = Path()
        for (j in 0 until 6) {
            val c = Offset(w * (0.08f + 0.035f * azar(5110, j)), w * (0.245f + 0.045f * j))
            val rx = w * (0.022f + 0.012f * azar(5111, j) + 0.004f * j)
            humedad.addPath(irregular(c, rx, rx * 1.2f, 5113 + j, 14, 0.25f))
        }
        val tinte = cc(mezcla(OXIDO, OLIVA, 0.3f), o, 0.09f, 0.3f)
        drawPath(humedad, tinte.copy(alpha = if (o) 0.16f else 0.1f))
        translate(0f, -0.006f * w) { drawPath(humedad, tinte.copy(alpha = if (o) 0.08f else 0.06f)) }
        drawPath(humedad, tinte.copy(alpha = if (o) 0.16f else 0.12f), style = Stroke(1.4f * u))
        // Los desconchones.
        listOf(Triple(0.31f, 0.27f, 0.02f), Triple(0.5f, 0.375f, 0.013f), Triple(0.02f, 0.46f, 0.016f), Triple(0.79f, 0.34f, 0.013f),
            Triple(0.975f, 0.5f, 0.014f), Triple(0.41f, 0.02f, 0.012f)).forEachIndexed { j, (x, y, r) -> desconchon(x, y, r, 5120 + j * 7, o) }
        // El rodapié de baldosa oliva, con su canto y sus golpes.
        val rd = suelo - 0.018f * w
        drawRect(cc(mezcla(OLIVA, VERDE, 0.3f), o, 0.5f, 1f), Offset(0f, rd), Size(w, suelo - rd))
        drawRect(brillo(o, 0.4f, 0.2f), Offset(0f, rd), Size(w, 1f * u))
        drawRect(sombra(o, 0.3f), Offset(0f, suelo - 1.4f * u), Size(w, 1.4f * u))
    }

    /** El suelo de linóleo gris verdoso, con sus juntas en fuga, sus rozaduras y la mugre junto a la pared. */
    private fun DrawScope.piso(suelo: Float, alto: Float, o: Boolean) {
        val w = size.width; val u = density
        val lin = mezcla(VERDE, ACERO_OSC, 0.35f)
        if (!o) drawRect(Brush.verticalGradient(listOf(mezcla(lin, ACERO_LUZ, 0.25f), lin, mezcla(lin, OLIVA, 0.25f)), suelo, alto), Offset(0f, suelo), Size(w, alto - suelo))
        else drawRect(Brush.verticalGradient(listOf(n(lin, true, 0.35f), n(lin, true, 0.12f), n(lin, true, 0f)), suelo, alto), Offset(0f, suelo), Size(w, alto - suelo))
        val junta = sombra(o, 0.22f)
        listOf(0.16f, 0.4f, 0.72f).forEach { f -> val y = suelo + (alto - suelo) * f; drawLine(junta, Offset(0f, y), Offset(w, y), 0.9f * u) }
        val fuga = Offset(w * 0.5f, suelo - w * 0.9f)
        for (j in -4..4) {
            val xb = w * (0.5f + j * 0.19f)
            val xa = fuga.x + (xb - fuga.x) * (w * 0.9f) / (w * 0.9f + (alto - suelo))
            drawLine(junta, Offset(xa, suelo), Offset(xb, alto), 0.9f * u)
        }
        pinceladas(Rect(0f, suelo, w, alto), if (o) NOCHE else mezcla(lin, OLIVA, 0.5f), 5130, 18, u, 0.14f)
        for (j in 0 until 6) {
            val x = w * azar(5131, j); val y = suelo + (alto - suelo) * (0.3f + 0.6f * azar(5132, j))
            drawLine(brillo(o, 0.18f, 0.1f), Offset(x, y), Offset(x + w * (0.03f + 0.05f * azar(5133, j)), y + 1f * u), 0.8f * u, StrokeCap.Round)
        }
    }

    /** La tubería que baja por la izquierda, pintada como la pared, con su collarín, su abrazadera oxidada y el chorreón. */
    private fun DrawScope.tuberia(o: Boolean) {
        val w = size.width; val u = density
        val x0 = 0.04f * w; val x1 = 0.064f * w; val y1 = TAPA0 * w
        sombraEnPared(pl(x0, 0f, x1, 0f, x1, y1, x0, y1), 0.052f, 0.3f, o, 0.26f, 0.8f)
        val tubo = mezcla(CREMA, ACERO, 0.45f)
        drawRect(cc(tubo, o, 0.05f, 0.3f), Offset(x0, 0f), Size(x1 - x0, y1))
        drawRect(brillo(o, 0.55f, 0.3f), Offset(x0 + (x1 - x0) * 0.18f, 0f), Size((x1 - x0) * 0.2f, y1))
        drawRect(sombra(o, 0.3f), Offset(x0 + (x1 - x0) * 0.7f, 0f), Size((x1 - x0) * 0.3f, y1))
        // El collarín de arriba y el de abajo.
        listOf(0.04f, 0.47f).forEach { fy ->
            val y = fy * w
            drawRect(cc(mezcla(tubo, ACERO_OSC, 0.25f), o, 0.05f, fy), Offset(x0 - 0.004f * w, y), Size(x1 - x0 + 0.008f * w, 0.014f * w))
            drawRect(brillo(o, 0.5f, 0.3f), Offset(x0 - 0.004f * w, y), Size(x1 - x0 + 0.008f * w, 1f * u))
            drawRect(sombra(o, 0.35f), Offset(x0 - 0.004f * w, y + 0.014f * w - 1.2f * u), Size(x1 - x0 + 0.008f * w, 1.2f * u))
        }
        // La abrazadera con su oreja atornillada a la pared, y el óxido que chorrea.
        val ya = 0.21f * w
        drawRect(sombra(o, 0.3f), Offset(x1 + 0.004f * w, ya + 0.004f * w), Size(0.03f * w, 0.012f * w))
        drawRect(cc(OXIDO, o, 0.06f, 0.21f), Offset(x0 - 0.003f * w, ya), Size(x1 - x0 + 0.006f * w, 0.014f * w))
        drawRect(cc(mezcla(OXIDO, HIERRO, 0.4f), o, 0.08f, 0.21f), Offset(x1, ya + 0.002f * w), Size(0.026f * w, 0.009f * w))
        drawCircle(cc(HIERRO, o, 0.09f, 0.21f), 0.0035f * w, Offset(x1 + 0.019f * w, ya + 0.0065f * w))
        drawCircle(brillo(o, 0.5f, 0.3f), 0.0012f * w, Offset(x1 + 0.018f * w, ya + 0.0055f * w))
        drawRect(brillo(o, 0.4f, 0.3f), Offset(x0 - 0.003f * w, ya), Size(x1 - x0 + 0.006f * w, 1f * u))
        val ch = Path().apply {
            moveTo(x1 + 0.006f * w, ya + 0.012f * w)
            cubicTo(x1 + 0.009f * w, ya + 0.04f * w, x1 + 0.004f * w, ya + 0.06f * w, x1 + 0.008f * w, ya + 0.1f * w)
        }
        drawPath(ch, cc(OXIDO, o, 0.07f, 0.26f).copy(alpha = 0.55f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
    }

    /** El reloj de pared de oficina: aro de baquelita, esfera hueso con sus marcas, las agujas y la del segundero roja. */
    private fun DrawScope.reloj(o: Boolean) {
        val w = size.width; val u = density
        val cx = 0.215f; val cy = 0.118f; val rf = 0.064f
        val c = Offset(cx * w, cy * w); val r = rf * w
        val k = kk(o, cx, cy)
        sombraEnPared(Path().apply { addOval(Rect(c, r)) }, cx, cy, o, 0.24f, 0.8f)
        drawCircle(n(mezcla(BAQUELITA, OLIVA, 0.35f), o, k), r, c)
        drawArc(brillo(o, 0.5f, k), 160f, 110f, false, Offset(c.x - r * 0.96f, c.y - r * 0.96f), Size(r * 1.92f, r * 1.92f), style = Stroke(r * 0.07f))
        drawCircle(n(HUESO, o, k), r * 0.84f, c)
        // La sombra del aro sobre la esfera, arriba a la izquierda.
        drawArc(sombra(o, 0.22f), 150f, 140f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.09f))
        val tinta = n(BAQUELITA, o, k)
        for (i in 0 until 60) {
            val a = i * 6.2832f / 60f
            val dir = Offset(cos(a), sin(a))
            if (i % 5 == 0) drawLine(tinta, c + dir * (r * 0.74f), c + dir * (r * 0.6f), if (i % 15 == 0) 2.6f * u else 1.6f * u)
            else if (i % 2 == 0) drawCircle(tinta.copy(alpha = 0.6f), 0.5f * u, c + dir * (r * 0.72f))
        }
        // Las diez y nueve: la hora de todos los días.
        fun aguja(grados: Float, largo: Float, grueso: Float, col: Color) {
            val a = Math.toRadians(grados.toDouble() - 90.0).toFloat()
            val p = c + Offset(cos(a), sin(a)) * (r * largo)
            drawLine(sombra(o, 0.25f), c + Offset(1.2f * u, 1.6f * u), p + Offset(1.2f * u, 1.6f * u), grueso, StrokeCap.Round)
            drawLine(col, c, p, grueso, StrokeCap.Round)
        }
        aguja(304f, 0.42f, 3f * u, tinta)
        aguja(54f, 0.62f, 2f * u, tinta)
        aguja(200f, 0.66f, 0.9f * u, n(CARTEL, o, k))
        drawCircle(tinta, 2.2f * u, c)
        // El brillo del vidrio.
        drawArc(Color.White.copy(alpha = if (o) 0.1f * k else 0.35f), 200f, 50f, false, Offset(c.x - r * 0.66f, c.y - r * 0.66f), Size(r * 1.32f, r * 1.32f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /**
     * El cartel del ojo: papel amarillento con el ojo y sus rayos, dos renglones de letras sin leer, la banda roja con sus
     * galones; arriba a la derecha, la esquina rota y la solapa doblada hacia delante, y la chincheta que quedó clavada sola.
     */
    private fun DrawScope.cartel(o: Boolean) {
        val w = size.width; val u = density
        val l = 0.535f; val t = 0.045f; val r = 0.765f; val bt = 0.335f
        val cx = (l + r) / 2; val cy = (t + bt) / 2
        val k = kk(o, cx, cy)
        val papel = Path().apply {
            moveTo(l * w, t * w); lineTo((r - 0.06f) * w, t * w)
            lineTo((r - 0.05f) * w, (t + 0.012f) * w); lineTo((r - 0.038f) * w, (t + 0.008f) * w); lineTo((r - 0.03f) * w, (t + 0.03f) * w)
            lineTo((r - 0.015f) * w, (t + 0.036f) * w); lineTo((r - 0.008f) * w, (t + 0.058f) * w); lineTo(r * w, (t + 0.066f) * w)
            lineTo(r * w, bt * w); lineTo((l + 0.12f) * w, (bt + 0.002f) * w); lineTo(l * w, bt * w); close()
        }
        rotate(-1.5f, Offset(cx * w, cy * w)) {
            sombraEnPared(papel, cx, cy, o, 0.14f, 0.45f)
            val fondo = mezcla(HUESO, CARPETA, 0.22f)
            drawPath(papel, n(fondo, o, k))
            clipPath(papel) {
                // Lo amarillento y lo sucio, más hacia abajo.
                pinceladas(Rect(l * w, t * w, r * w, bt * w), if (o) NOCHE else CARTON, 5140, 14, u, 0.1f)
                // Los rayos y el ojo.
                val e = Offset(0.65f * w, 0.135f * w)
                for (j in 0 until 16) {
                    val a = 3.1416f + j * 3.1416f / 15f
                    val dir = Offset(cos(a), sin(a) * 0.9f)
                    drawLine(n(BAQUELITA, o, k).copy(alpha = 0.7f), e + dir * (0.068f * w), e + dir * ((0.085f + 0.01f * (j % 2)) * w), 1.5f * u)
                }
                val a = 0.058f * w
                val almendra = Path().apply {
                    moveTo(e.x - a, e.y); cubicTo(e.x - a * 0.45f, e.y - a * 0.62f, e.x + a * 0.45f, e.y - a * 0.62f, e.x + a, e.y)
                    cubicTo(e.x + a * 0.45f, e.y + a * 0.62f, e.x - a * 0.45f, e.y + a * 0.62f, e.x - a, e.y); close()
                }
                drawPath(almendra, n(HUESO, o, k))
                clipPath(almendra) {
                    drawCircle(n(CARTEL, o, k), a * 0.36f, e)
                    drawCircle(n(BURDEOS, o, k), a * 0.36f, e, style = Stroke(a * 0.06f))
                    drawCircle(n(BAQUELITA, o, k), a * 0.16f, e)
                    drawCircle(n(HUESO, o, k), a * 0.06f, e - Offset(a * 0.1f, a * 0.1f))
                }
                drawPath(almendra, n(BAQUELITA, o, k), style = Stroke(a * 0.11f))
                // Dos renglones que no se leen.
                listOf(0.212f, 0.232f).forEachIndexed { i, fy ->
                    var x = 0.565f
                    var q = 0
                    while (x < 0.735f) {
                        val ancho = 0.012f + 0.022f * azar(5141 + i, q)
                        drawRect(n(BAQUELITA, o, k).copy(alpha = 0.75f), Offset(x * w, fy * w), Size(minOf(ancho, 0.735f - x) * w, (if (i == 0) 0.009f else 0.006f) * w))
                        x += ancho + 0.007f; q++
                    }
                }
                // La banda roja con sus galones.
                drawRect(n(CARTEL, o, k), Offset(l * w, 0.258f * w), Size((r - l) * w, 0.06f * w))
                for (j in 0 until 2) {
                    val y = (0.274f + j * 0.018f) * w
                    drawPath(Path().apply { moveTo(0.62f * w, y + 0.014f * w); lineTo(0.65f * w, y); lineTo(0.68f * w, y + 0.014f * w) },
                        n(HUESO, o, k).copy(alpha = 0.85f), style = Stroke(0.0055f * w))
                }
                // Los pliegues de cuando estaba doblado en cuatro, y el borde de abajo despegándose.
                drawLine(sombra(o, 0.16f), Offset(l * w, 0.19f * w), Offset(r * w, 0.192f * w), 1f * u)
                drawLine(brillo(o, 0.35f, k), Offset(l * w, 0.19f * w + 1f * u), Offset(r * w, 0.192f * w + 1f * u), 0.8f * u)
                drawLine(sombra(o, 0.12f), Offset(0.651f * w, t * w), Offset(0.649f * w, bt * w), 1f * u)
                drawRect(brillo(o, 0.25f, k), Offset(l * w, t * w), Size(0.02f * w, (bt - t) * w))
            }
            // La solapa doblada: el revés del papel, más oscuro, con su sombra en el cartel.
            val solapa = pl((r - 0.06f) * w, t * w, r * w, (t + 0.066f) * w, (r - 0.012f) * w, (t + 0.094f) * w, (r - 0.05f) * w, (t + 0.07f) * w)
            translate(0.006f * w, 0.008f * w) { drawPath(solapa, sombra(o, 0.3f)) }
            drawPath(solapa, n(mezcla(HUESO, CARTON, 0.45f), o, k))
            drawPath(solapa, sombra(o, 0.12f), style = Stroke(0.8f * u))
            drawLine(brillo(o, 0.5f, k), Offset((r - 0.06f) * w, t * w), Offset((r - 0.05f) * w, (t + 0.07f) * w), 0.9f * u)
            // Las chinchetas: tres en su sitio y una sola en la esquina que se fue, con un jirón de papel.
            listOf(Offset(l + 0.01f, t + 0.01f), Offset(l + 0.01f, bt - 0.012f), Offset(r - 0.01f, bt - 0.012f), Offset(r - 0.01f, t + 0.012f)).forEachIndexed { i, p ->
                val c = Offset(p.x * w, p.y * w)
                if (i == 3) drawPath(irregular(c, 0.007f * w, 0.006f * w, 5150, 6, 0.5f), n(mezcla(HUESO, CARPETA, 0.22f), o, k))
                drawCircle(sombra(o, 0.4f), 0.0055f * w, c + Offset(1.6f * u, 1.8f * u))
                drawCircle(n(mezcla(CARPETA, OXIDO, 0.4f), o, k), 0.0055f * w, c)
                drawCircle(brillo(o, 0.7f, k + 0.2f), 0.0018f * w, c - Offset(0.0018f * w, 0.0018f * w))
            }
        }
    }

    /** El altavoz de megafonía: una caja con su rejilla de lamas, colgada alta, con el tubo del cable que sube al techo. */
    private fun DrawScope.altavoz(o: Boolean) {
        val w = size.width; val u = density
        val l = 0.83f; val t = 0.03f; val r = 0.948f; val bt = 0.112f
        val k = kk(o, 0.89f, 0.07f)
        fun c(col: Color) = n(col, o, k, if (o) deLed(Offset(0.89f, 0.1f)) else 0f)
        drawRect(c(HIERRO), Offset(0.885f * w, 0f), Size(0.008f * w, t * w))
        drawRect(brillo(o, 0.4f, k), Offset(0.886f * w, 0f), Size(1f * u, t * w))
        sombraEnPared(pl(l * w, t * w, r * w, t * w, r * w, bt * w, l * w, bt * w), 0.89f, 0.07f, o, 0.24f, 0.8f)
        val caja = mezcla(OLIVA, VERDE, 0.4f)
        drawRect(c(caja), Offset(l * w, t * w), Size((r - l) * w, (bt - t) * w))
        // El lado de abajo (se ve desde abajo) y el canto con luz.
        drawRect(c(mezcla(caja, BAQUELITA, 0.45f)), Offset(l * w, (bt - 0.008f) * w), Size((r - l) * w, 0.008f * w))
        drawRect(brillo(o, 0.45f, k), Offset(l * w, t * w), Size((r - l) * w, 1.2f * u))
        drawRect(brillo(o, 0.3f, k), Offset(l * w, t * w), Size(1.2f * u, (bt - t) * w))
        drawRect(sombra(o, 0.25f), Offset((r - 0.012f) * w, t * w), Size(0.012f * w, (bt - t) * w))
        // Las lamas: cada una con su sombra debajo.
        for (j in 0 until 6) {
            val y = (t + 0.012f + j * 0.011f) * w
            drawRect(c(mezcla(caja, BAQUELITA, 0.6f)), Offset((l + 0.01f) * w, y), Size((r - l - 0.02f) * w, 0.006f * w))
            drawRect(brillo(o, 0.3f, k), Offset((l + 0.01f) * w, y + 0.006f * w), Size((r - l - 0.02f) * w, 0.8f * u))
        }
        listOf(l + 0.005f, r - 0.005f).forEach { x -> drawCircle(c(HIERRO), 1.2f * u, Offset(x * w, (t + 0.005f) * w)) }
    }

    /** La cámara de vigilancia en su brazo, mirando hacia abajo, con su visera y su luz roja. */
    private fun DrawScope.camara(o: Boolean) {
        val w = size.width; val u = density
        val k = kk(o, 0.86f, 0.2f)
        val rojo = if (o) 0.8f else 0f
        fun c(col: Color) = n(col, o, k, rojo)
        // La placa en la pared y el brazo.
        val brazo = Path().apply { moveTo(0.935f * w, 0.172f * w); lineTo(0.87f * w, 0.196f * w) }
        translate(0.012f * w, 0.012f * w) { drawPath(brazo, sombra(o, 0.25f), style = Stroke(0.008f * w)) }
        drawRect(sombra(o, 0.25f), Offset(0.934f * w, 0.162f * w), Size(0.022f * w, 0.034f * w))
        drawRect(c(HIERRO), Offset(0.928f * w, 0.155f * w), Size(0.02f * w, 0.034f * w))
        drawRect(brillo(o, 0.4f, k), Offset(0.928f * w, 0.155f * w), Size(0.02f * w, 1f * u))
        drawPath(brazo, c(HIERRO), style = Stroke(0.008f * w, cap = StrokeCap.Round))
        drawPath(brazo, brillo(o, 0.4f, k), style = Stroke(0.002f * w))
        // El cuerpo, girado hacia abajo a la izquierda.
        val cc0 = Offset(0.85f * w, 0.205f * w)
        val L = 0.088f * w; val H = 0.03f * w
        val cuerpo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(cc0.x - L / 2, cc0.y - H / 2, cc0.x + L / 2, cc0.y + H / 2), CornerRadius(3f * u))) }
        rotate(-20f, cc0) {
            translate(0.016f * w, 0.012f * w) { drawPath(cuerpo, sombra(o, if (o) 0.4f else 0.2f)) }
        }
        rotate(-20f, cc0) {
            val claro = mezcla(ACERO_LUZ, HUESO, 0.4f)
            drawPath(cuerpo, c(claro))
            drawRect(sombra(o, 0.28f), Offset(cc0.x - L / 2, cc0.y + H * 0.15f), Size(L, H * 0.35f))
            drawRect(brillo(o, 0.6f, k), Offset(cc0.x - L / 2 + 2f * u, cc0.y - H / 2), Size(L - 4f * u, 1.2f * u))
            // La visera, que sale por delante.
            drawRect(c(mezcla(claro, ACERO, 0.4f)), Offset(cc0.x - L / 2 - 0.012f * w, cc0.y - H / 2 - 0.004f * w), Size(L * 0.9f, 0.006f * w))
            // El frente con el cristal del objetivo.
            drawRect(c(BAQUELITA), Offset(cc0.x - L / 2 - 0.002f * w, cc0.y - H * 0.38f), Size(0.008f * w, H * 0.76f))
            drawOval(Color.White.copy(alpha = if (o) 0.2f else 0.4f), Offset(cc0.x - L / 2 - 0.001f * w, cc0.y - H * 0.3f), Size(0.003f * w, H * 0.25f))
            // La luz roja, siempre encendida.
            val led = Offset(cc0.x - L * 0.32f, cc0.y - H * 0.22f)
            drawCircle(PILOTO.copy(alpha = 0.45f), 0.006f * w, led)
            drawCircle(PILOTO, 0.0032f * w, led)
            drawCircle(Color.White.copy(alpha = 0.7f), 0.0011f * w, led - Offset(0.001f * w, 0.001f * w))
            // El cable que sale por detrás.
            drawPath(Path().apply { moveTo(cc0.x + L / 2, cc0.y); cubicTo(cc0.x + L / 2 + 0.02f * w, cc0.y, cc0.x + L / 2 + 0.01f * w, cc0.y - 0.03f * w, cc0.x + L / 2 + 0.03f * w, cc0.y - 0.04f * w) },
                c(BAQUELITA), style = Stroke(1.6f * u))
        }
    }

    /**
     * El archivero: el cuerpo de chapa pintada, su sombra en la pared y en el suelo, el fondo de cada hueco (la sombra que deja
     * en el cuerpo el frente de la gaveta), el tablero con su canto despintado, el número estarcido, el precinto roto, las
     * abolladuras y el zócalo con su rejilla y sus patas.
     */
    private fun DrawScope.archivero(d: Disposicion, o: Boolean, suelo: Float, pisa: Float) {
        val w = size.width; val u = density
        val xL = IZQ * w; val xR = DER * w
        val t0 = TAPA0 * w; val t1 = TAPA1 * w; val t2 = CANTO * w
        val b = d.base; val hb = b.height
        // La sombra en la pared, a la derecha, y en el suelo.
        drawPath(pl(xR + 0.006f * w, t0, xR + 0.034f * w, t0 + 0.028f * w, xR + 0.034f * w, suelo, xR, suelo), sombra(o, 0.3f))
        drawPath(pl(xL + 0.01f * w, pisa, xR + 0.006f * w, pisa, xR + 0.03f * w, pisa + 0.012f * w, xL + 0.03f * w, pisa + 0.014f * w), sombra(o, if (o) 0.55f else 0.34f))
        drawRect(sombra(o, if (o) 0.4f else 0.2f), Offset(xL + 0.02f * w, pisa + 0.012f * w), Size(xR - xL + 0.02f * w, 0.012f * w))
        // El cuerpo.
        val cuerpo = Rect(xL, t2, xR, pisa)
        if (!o) drawRect(Brush.horizontalGradient(listOf(mezcla(ACERO, LUZ_TUBO, 0.28f), ACERO, mezcla(ACERO, ACERO_OSC, 0.4f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(6) { i -> n(ACERO, true, deFlexo(Offset(0.5f, (t2 + (pisa - t2) * i / 5f) / w))) }, t2, pisa), cuerpo.topLeft, cuerpo.size)
        if (o) drawRect(sombra(true, 0.22f), Offset(xR - (xR - xL) * 0.3f, t2), Size((xR - xL) * 0.3f, pisa - t2))
        pinceladas(cuerpo, if (o) NOCHE else ACERO_OSC, 5160, 26, u, 0.08f)
        // Los cantos del cuerpo: luz a la izquierda, sombra a la derecha.
        drawRect(brillo(o, 0.5f, 0.4f), Offset(xL, t2), Size(1.4f * u, pisa - t2))
        drawRect(sombra(o, 0.35f), Offset(xR - 2f * u, t2), Size(2f * u, pisa - t2))
        // El fondo de cada hueco y la sombra que el frente de la gaveta deja en el cuerpo.
        d.huecos.forEach { r ->
            val k = kk(o, r.center.x / w, r.center.y / w)
            drawRect(sombra(o, 0.42f), Offset(r.left + 1.6f * u, r.top + 2.2f * u), r.size)
            drawRect(n(mezcla(HIERRO, BAQUELITA, 0.4f), o, k * 0.5f), r.topLeft, r.size)
            drawLine(brillo(o, 0.45f, k), Offset(r.left, r.bottom + 2.8f * u), Offset(r.right + 1.6f * u, r.bottom + 2.8f * u), 0.9f * u)
        }
        // Las abolladuras: hundidas, con la sombra arriba a la izquierda y la luz abajo a la derecha.
        listOf(Triple(0.44f, (t2 + d.huecos[0].top) / 2f / w, 0.03f), Triple(0.5f, (d.filas.getOrNull(2)?.top ?: d.filas.last().bottom) / w - 0.016f, 0.02f),
            Triple(0.052f, (d.filas.getOrNull(1)?.center?.y ?: t2) / w, 0.012f)).forEachIndexed { j, (x, y, r) ->
            abolladura(Offset(x * w, y * w), r * w, r * w * 0.5f, o, kk(o, x, y))
        }
        // La pintura saltada en las esquinas y en los bordes de los huecos.
        for (j in 0 until 12) {
            val r = d.huecos[(j * 5) % d.huecos.size]
            val esquina = j % 4
            val p = Offset(if (esquina % 2 == 0) r.left - 0.004f * w else r.right + 0.004f * w, if (esquina < 2) r.top - 0.004f * w else r.bottom + 0.004f * w)
            if (azar(5161, j) < 0.6f) saltado(p, (0.004f + 0.006f * azar(5162, j)) * w, 5163 + j, o, kk(o, p.x / w, p.y / w))
        }
        listOf(Offset(xL + 0.006f * w, t2 + 0.012f * w), Offset(xR - 0.008f * w, t2 + 0.02f * w), Offset(xL + 0.005f * w, pisa - 0.03f * w)).forEachIndexed { j, p ->
            saltado(p, 0.008f * w, 5170 + j, o, kk(o, p.x / w, p.y / w))
        }
        // El número de inventario estarcido, gastado.
        val ye = t2 + (d.huecos[0].top - t2) * 0.24f
        val ae = (d.huecos[0].top - t2) * 0.44f
        val ke = kk(o, 0.15f, ye / w)
        estarcido("0417-23", 0.085f * w, ye, ae * 0.6f, ae, n(HUESO, o, ke).copy(alpha = 0.9f))
        pinceladas(Rect(0.08f * w, ye, 0.3f * w, ye + ae), n(ACERO, o, ke), 5175, 5, u, 0.2f)
        // El precinto roto: la mitad que queda, con su borde rasgado y la goma de la otra mitad.
        val pr = Rect(0.6f * w, ye - ae * 0.12f, 0.73f * w, ye + ae * 1.12f)
        val kp = kk(o, 0.66f, ye / w)
        drawRect(n(mezcla(ACERO, HUESO, 0.25f), o, kp).copy(alpha = 0.6f), pr.topLeft, pr.size)
        val roto = Path().apply {
            moveTo(pr.left, pr.top); lineTo(pr.left + pr.width * 0.55f, pr.top)
            for (j in 1..6) lineTo(pr.left + pr.width * (0.55f - 0.12f * (j % 2) - 0.03f * j), pr.top + pr.height * j / 6f)
            lineTo(pr.left, pr.bottom); close()
        }
        translate(0.8f * u, 1f * u) { drawPath(roto, sombra(o, 0.3f)) }
        drawPath(roto, n(HUESO, o, kp))
        clipPath(roto) {
            drawRect(n(CARTEL, o, kp), pr.topLeft, Size(pr.width, pr.height * 0.18f))
            drawRect(n(CARTEL, o, kp), Offset(pr.left, pr.bottom - pr.height * 0.18f), Size(pr.width, pr.height * 0.18f))
            for (j in 0 until 3) drawRect(n(BAQUELITA, o, kp).copy(alpha = 0.6f), Offset(pr.left + pr.width * (0.06f + 0.14f * j), pr.top + pr.height * 0.36f), Size(pr.width * 0.1f, pr.height * 0.28f))
        }
        // El tablero, visto un poco desde arriba, y su canto redondeado.
        val tapa = Rect(xL - 0.008f * w, t0, xR + 0.008f * w, t1)
        val gris = mezcla(ACERO, ACERO_LUZ, 0.4f)
        if (!o) drawRect(Brush.horizontalGradient(listOf(mezcla(gris, LUZ_TUBO, 0.35f), gris, mezcla(gris, ACERO, 0.5f)), tapa.left, tapa.right), tapa.topLeft, tapa.size)
        else drawRect(Brush.horizontalGradient(List(6) { i -> n(gris, true, deFlexo(Offset(i / 5f, TAPA0))) }, tapa.left, tapa.right), tapa.topLeft, tapa.size)
        drawRect(sombra(o, 0.3f), tapa.topLeft, Size(tapa.width, 1.4f * u))
        pinceladas(tapa, if (o) NOCHE else ACERO_OSC, 5180, 10, u, 0.1f)
        // El cerco de un café y lo gastado donde se apoya la máquina.
        drawOval(cc(OXIDO, o, 0.77f, TAPA0).copy(alpha = 0.3f), Offset(0.755f * w, t0 + (t1 - t0) * 0.25f), Size(0.03f * w, (t1 - t0) * 0.5f), style = Stroke(1f * u))
        val canto = Rect(tapa.left, t1, tapa.right, t2)
        if (!o) drawRect(Brush.horizontalGradient(listOf(mezcla(ACERO, LUZ_TUBO, 0.2f), ACERO, mezcla(ACERO, ACERO_OSC, 0.45f)), canto.left, canto.right), canto.topLeft, canto.size)
        else drawRect(Brush.horizontalGradient(List(6) { i -> n(ACERO, true, deFlexo(Offset(i / 5f, TAPA1))) }, canto.left, canto.right), canto.topLeft, canto.size)
        drawRect(brillo(o, 0.8f, 0.9f), Offset(canto.left, t1), Size(canto.width, 1.3f * u))
        drawRect(brillo(o, 0.25f, 0.6f), Offset(canto.left, t1 + 1.3f * u), Size(canto.width, (t2 - t1) * 0.3f))
        drawRect(sombra(o, 0.35f), Offset(canto.left, t2 - 1.4f * u), Size(canto.width, 1.4f * u))
        // El canto despintado por el roce de las mangas.
        for (j in 0 until 9) {
            val x = canto.left + canto.width * (0.05f + 0.9f * azar(5181, j))
            saltado(Offset(x, t1 + (t2 - t1) * (0.25f + 0.5f * azar(5182, j))), (0.003f + 0.005f * azar(5183, j)) * w, 5184 + j, o, kk(o, x / w, TAPA1))
        }
        drawRect(sombra(o, 0.32f), Offset(xL, t2), Size(xR - xL, 2.4f * u))
        drawRect(sombra(o, 0.12f), Offset(xL, t2 + 2.4f * u), Size(xR - xL, 3f * u))

        // El zócalo: metido hacia dentro, con la rejilla a los lados y las patas.
        val kt = b.top + hb * ZOCALO
        drawRect(sombra(o, 0.4f), Offset(xL, kt), Size(xR - xL, 2f * u))
        val zoc = Rect(xL + 0.014f * w, kt + 2f * u, xR - 0.014f * w, pisa)
        drawRect(n(ACERO_OSC, o, 0.15f), zoc.topLeft, zoc.size)
        drawRect(sombra(o, 0.3f), zoc.topLeft, Size(zoc.width, zoc.height * 0.3f))
        listOf(0.06f to 0.3f, 0.7f to 0.94f).forEach { (a, z) ->
            var x = a * w
            while (x < z * w) {
                val s = Rect(x, zoc.top + zoc.height * 0.3f, x + 0.006f * w, zoc.bottom - zoc.height * 0.24f)
                drawRoundRect(n(BAQUELITA, o, 0f), s.topLeft, s.size, CornerRadius(1.5f * u))
                drawLine(brillo(o, 0.3f, 0.3f), Offset(s.left, s.bottom + 0.8f * u), Offset(s.right, s.bottom + 0.8f * u), 0.8f * u)
                x += 0.014f * w
            }
        }
        listOf(xL + 0.016f * w, xR - 0.04f * w).forEach { x ->
            drawRect(n(BAQUELITA, o, 0f), Offset(x, pisa - 1f * u), Size(0.024f * w, 0.007f * w))
        }
        drawRect(brillo(o, 0.3f, 0.2f), Offset(zoc.left, zoc.bottom - 1f * u), Size(zoc.width, 1f * u))
        // La placa con «y N más»: un tarjetero grande atornillado al zócalo.
        if (d.mas > 0) placa.let { p ->
            val r = d.enBase(p)
            drawRect(sombra(o, 0.4f), Offset(r.left + 2f * u, r.top + 2.4f * u), r.size)
            drawRect(n(ACERO_LUZ, o, 0.25f), r.topLeft, r.size)
            val dentro = Rect(r.left + 3f * u, r.top + 3f * u, r.right - 3f * u, r.bottom - 2f * u)
            drawRect(tarjeta(o, 0f), dentro.topLeft, dentro.size)
            drawRect(brillo(o, 0.6f, 0.4f), r.topLeft, Size(r.width, 1f * u))
            listOf(r.left + 1.5f * u, r.right - 1.5f * u).forEach { x -> drawCircle(n(HIERRO, o, 0.2f), 1f * u, Offset(x, r.center.y)) }
        }
    }

    /** La pila de carpetas de manila contra la pared, desalineadas, con hojas que asoman, atada con cinta burdeos y su lazo. */
    private fun DrawScope.carpetas(o: Boolean) {
        val w = size.width; val u = density
        val x0 = 0.805f; val x1 = 0.958f; val pie = 0.572f
        val th = 0.0155f; val nn = 9
        val top = pie - th * nn
        val k = kk(o, 0.88f, 0.5f)
        sombraEnPared(pl(x0 * w, (top - 0.016f) * w, x1 * w, (top - 0.016f) * w, (x1 + 0.006f) * w, pie * w, x0 * w, pie * w), 0.88f, 0.5f, o, 0.26f)
        for (i in 0 until nn) {
            val yb = (pie - i * th) * w
            val dx = (azar(5200, i) - 0.5f) * 0.012f
            val col = if (i == 3) mezcla(CARPETA, CARTEL, 0.3f) else mezcla(CARPETA, CARTON, 0.25f * azar(5201, i))
            val r = Rect((x0 + dx) * w, yb - th * w, (x1 + dx) * w, yb)
            if (azar(5202, i) < 0.5f) drawRect(n(HUESO, o, k), Offset(r.right - 0.004f * w, r.top + th * w * 0.3f), Size(0.01f * w, th * w * 0.45f))
            drawRect(cc(col, o, r.center.x / w, r.center.y / w), r.topLeft, r.size)
            drawRect(sombra(o, 0.35f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            drawRect(brillo(o, 0.35f, k), r.topLeft, Size(r.width, 0.8f * u))
            drawRect(sombra(o, 0.18f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
        }
        // La cara de arriba de la última carpeta, con su etiqueta.
        val dxt = (azar(5200, nn - 1) - 0.5f) * 0.012f
        val cara = pl((x0 + dxt + 0.006f) * w, (top - 0.016f) * w, (x1 + dxt - 0.004f) * w, (top - 0.016f) * w, (x1 + dxt) * w, top * w, (x0 + dxt) * w, top * w)
        drawPath(cara, n(mezcla(CARPETA, LUZ_TUBO, 0.25f), o, k))
        drawRect(n(HUESO, o, k), Offset((x0 + 0.03f) * w, (top - 0.012f) * w), Size(0.05f * w, 0.008f * w))
        drawRect(n(BAQUELITA, o, k).copy(alpha = 0.5f), Offset((x0 + 0.034f) * w, (top - 0.009f) * w), Size(0.034f * w, 0.0018f * w))
        // La cinta: por delante, por encima, y el lazo.
        val xc = 0.874f
        drawRect(n(BURDEOS, o, k), Offset(xc * w, (top - 0.012f) * w), Size(0.011f * w, (pie - top + 0.012f) * w))
        drawRect(brillo(o, 0.3f, k), Offset(xc * w, (top - 0.012f) * w), Size(1f * u, (pie - top + 0.012f) * w))
        val nudo = Offset((xc + 0.0055f) * w, (top - 0.008f) * w)
        listOf(-1f, 1f).forEach { s ->
            rotate(s * 28f, nudo) {
                drawOval(sombra(o, 0.3f), Offset(nudo.x + (if (s < 0) -0.024f else 0.002f) * w, nudo.y - 0.004f * w + 1.5f * u), Size(0.022f * w, 0.011f * w))
                drawOval(n(CARTEL, o, k), Offset(nudo.x + (if (s < 0) -0.024f else 0.002f) * w, nudo.y - 0.006f * w), Size(0.022f * w, 0.011f * w), style = Stroke(0.0035f * w))
            }
        }
        drawLine(n(BURDEOS, o, k), nudo, nudo + Offset(-0.012f * w, 0.02f * w), 0.004f * w)
        drawLine(n(BURDEOS, o, k), nudo, nudo + Offset(0.016f * w, 0.017f * w), 0.004f * w)
        drawCircle(n(BURDEOS, o, k), 0.0045f * w, nudo)
    }

    /** El carrusel de sellos de goma: la base redonda, el poste, el disco de arriba y los sellos colgados, de mango de madera. */
    private fun DrawScope.carrusel(o: Boolean) {
        val w = size.width; val u = density
        val cx = 0.092f; val pie = 0.574f
        val k = kk(o, cx, 0.53f)
        fun c(col: Color) = n(col, o, k)
        val metal = mezcla(BAQUELITA, OLIVA, 0.35f)
        sombraEnPared(pl((cx - 0.04f) * w, 0.47f * w, (cx + 0.04f) * w, 0.47f * w, (cx + 0.04f) * w, pie * w, (cx - 0.04f) * w, pie * w), cx, 0.52f, o, 0.2f)
        drawOval(sombra(o, 0.35f), Offset((cx - 0.04f) * w, (pie - 0.006f) * w), Size(0.09f * w, 0.012f * w))
        drawOval(c(metal), Offset((cx - 0.043f) * w, (pie - 0.014f) * w), Size(0.086f * w, 0.018f * w))
        drawArc(brillo(o, 0.5f, k), 180f, 90f, false, Offset((cx - 0.043f) * w, (pie - 0.014f) * w), Size(0.086f * w, 0.018f * w), style = Stroke(1f * u))
        // El poste.
        drawRect(c(ACERO_LUZ), Offset((cx - 0.0035f) * w, (pie - 0.1f) * w), Size(0.007f * w, 0.092f * w))
        drawRect(sombra(o, 0.3f), Offset((cx + 0.001f) * w, (pie - 0.1f) * w), Size(0.0025f * w, 0.092f * w))
        // Los sellos: los de atrás, más chicos y oscuros; los de delante, enteros.
        val disco = pie - 0.1f
        val sellos = listOf(-0.03f to 0.7f, 0.03f to 0.7f, -0.017f to 1f, 0.016f to 1f, 0f to 1.05f)
        sellos.forEachIndexed { i, (dx, e) ->
            val x = (cx + dx) * w
            val y = (disco + (if (e < 1f) -0.002f else 0.004f)) * w
            val gris = if (e < 1f) 0.25f else 0f
            val madera = c(mezcla(CARTON, BAQUELITA, gris))
            drawCircle(madera, 0.0062f * w * e, Offset(x, y - 0.006f * w))
            drawCircle(brillo(o, 0.4f, k), 0.002f * w * e, Offset(x - 0.002f * w, y - 0.008f * w))
            drawRect(madera, Offset(x - 0.0035f * w * e, y), Size(0.007f * w * e, 0.03f * w * e))
            drawRect(sombra(o, 0.25f), Offset(x + 0.001f * w * e, y), Size(0.0025f * w * e, 0.03f * w * e))
            drawRect(c(mezcla(BAQUELITA, OLIVA, 0.2f)), Offset(x - 0.009f * w * e, y + 0.03f * w * e), Size(0.018f * w * e, 0.007f * w * e))
            drawRect(c(if (i % 2 == 0) CARTEL else BURDEOS), Offset(x - 0.008f * w * e, y + 0.037f * w * e), Size(0.016f * w * e, 0.0025f * w * e))
        }
        drawOval(c(metal), Offset((cx - 0.04f) * w, (disco - 0.006f) * w), Size(0.08f * w, 0.012f * w))
        drawArc(brillo(o, 0.55f, k), 180f, 120f, false, Offset((cx - 0.04f) * w, (disco - 0.006f) * w), Size(0.08f * w, 0.012f * w), style = Stroke(1f * u))
        drawCircle(c(ACERO_LUZ), 0.005f * w, Offset(cx * w, (disco - 0.006f) * w))
    }

    /** El tampón: la cajita de lata abierta con la almohadilla roja, la tapa levantada detrás, delante de todo a la izquierda. */
    private fun DrawScope.tampon(o: Boolean) {
        val w = size.width; val u = density
        val l = 0.03f; val r = 0.118f; val pie = 0.588f
        val k = kk(o, 0.07f, pie)
        fun c(col: Color) = n(col, o, k)
        val lata = mezcla(BAQUELITA, OLIVA, 0.25f)
        // La tapa abierta, inclinada hacia atrás, con el revés manchado de tinta.
        val tapa = pl((l + 0.004f) * w, (pie - 0.022f) * w, (r - 0.004f) * w, (pie - 0.022f) * w, (r - 0.008f) * w, (pie - 0.05f) * w, (l + 0.008f) * w, (pie - 0.05f) * w)
        drawPath(tapa, c(mezcla(lata, ACERO_LUZ, 0.35f)))
        drawOval(c(CARTEL).copy(alpha = 0.35f), Offset((l + 0.03f) * w, (pie - 0.044f) * w), Size(0.035f * w, 0.014f * w))
        drawLine(brillo(o, 0.6f, k), Offset((l + 0.008f) * w, (pie - 0.05f) * w), Offset((r - 0.008f) * w, (pie - 0.05f) * w), 1f * u)
        drawOval(sombra(o, 0.35f), Offset((l + 0.004f) * w, (pie - 0.004f) * w), Size((r - l + 0.008f) * w, 0.01f * w))
        // La almohadilla, vista desde arriba, y el canto de la lata.
        drawRect(c(lata), Offset(l * w, (pie - 0.023f) * w), Size((r - l) * w, 0.013f * w))
        drawRect(c(CARTEL), Offset((l + 0.004f) * w, (pie - 0.021f) * w), Size((r - l - 0.008f) * w, 0.009f * w))
        drawRect(c(BURDEOS).copy(alpha = 0.5f), Offset((l + 0.05f) * w, (pie - 0.019f) * w), Size(0.025f * w, 0.005f * w))
        drawRect(brillo(o, 0.3f, k), Offset((l + 0.004f) * w, (pie - 0.021f) * w), Size((r - l - 0.008f) * w, 0.8f * u))
        drawRect(c(mezcla(lata, ACERO_LUZ, 0.2f)), Offset(l * w, (pie - 0.01f) * w), Size((r - l) * w, 0.01f * w))
        drawRect(brillo(o, 0.55f, k), Offset(l * w, (pie - 0.01f) * w), Size((r - l) * w, 0.9f * u))
        drawRect(sombra(o, 0.25f), Offset((r - 0.02f) * w, (pie - 0.01f) * w), Size(0.02f * w, 0.01f * w))
    }

    /**
     * La máquina de escribir de carro ancho, el punto focal: el carro con su rodillo de caucho, sus perillas y la palanca de
     * retorno; la hoja a medio pasar con sus renglones; el cuerpo de esmalte oliva, la cesta de las barras, la cinta roja y
     * negra, las tapas de los carretes y las cuatro filas de teclas redondas con su aro y la barra espaciadora.
     */
    private fun DrawScope.maquina(o: Boolean) {
        val w = size.width; val u = density
        val pie = 0.583f
        val k = kk(o, 0.33f, 0.5f)
        fun c(col: Color, x: Float = 0.33f, y: Float = 0.5f) = n(col, o, kk(o, x, y))
        fun P(x: Float, y: Float) = Offset(x * w, y * w)
        val esmalte = mezcla(OLIVA, BAQUELITA, 0.42f)
        // La sombra en la pared y la de contacto en el tablero.
        val silueta = Path().apply {
            addRect(Rect(0.135f * w, 0.448f * w, 0.52f * w, 0.478f * w))
            addRect(Rect(0.237f * w, 0.34f * w, 0.413f * w, 0.46f * w))
            addPath(pl(0.16f * w, pie * w, 0.5f * w, pie * w, 0.49f * w, 0.5f * w, 0.465f * w, 0.476f * w, 0.195f * w, 0.476f * w, 0.17f * w, 0.5f * w))
        }
        sombraEnPared(silueta, 0.33f, 0.46f, o, 0.24f)
        drawOval(sombra(o, if (o) 0.6f else 0.38f), Offset(0.165f * w, (pie - 0.008f) * w), Size(0.36f * w, 0.014f * w))
        // La hoja, detrás del rodillo, con su borde de arriba algo curvado.
        val hoja = Path().apply {
            moveTo(0.237f * w, 0.46f * w); lineTo(0.235f * w, 0.345f * w)
            cubicTo(0.29f * w, 0.336f * w, 0.36f * w, 0.342f * w, 0.415f * w, 0.337f * w)
            lineTo(0.413f * w, 0.46f * w); close()
        }
        drawPath(hoja, c(HUESO, 0.33f, 0.38f))
        clipPath(hoja) {
            drawRect(brillo(o, 0.3f, k), P(0.235f, 0.33f), Size(0.05f * w, 0.14f * w))
            drawRect(sombra(o, 0.14f), P(0.39f, 0.33f), Size(0.03f * w, 0.14f * w))
            // Los renglones ya escritos, que suben con el papel.
            for (j in 0 until 10) {
                val y = (0.356f + j * 0.0088f) * w
                var x = 0.252f + (if (j == 0) 0.04f else if (j % 4 == 1) 0.018f else 0f)
                val fin = if (j == 9) 0.33f else 0.395f - 0.03f * azar(5210, j)
                var q = 0
                while (x < fin) {
                    val ancho = 0.008f + 0.02f * azar(5211 + j, q)
                    drawRect(c(BAQUELITA, 0.33f, 0.38f).copy(alpha = 0.62f), Offset(x * w, y), Size(minOf(ancho, fin - x) * w, 0.0026f * w))
                    x += ancho + 0.005f; q++
                }
            }
            // El sello rojo, desvaído, arriba a la derecha.
            drawCircle(c(CARTEL, 0.37f, 0.36f).copy(alpha = 0.35f), 0.013f * w, P(0.385f, 0.36f), style = Stroke(1.4f * u))
        }
        // El carro: el riel, el rodillo y las perillas.
        drawRect(c(ACERO_LUZ), P(0.135f, 0.47f), Size(0.385f * w, 0.009f * w))
        drawRect(brillo(o, 0.7f, k), P(0.135f, 0.47f), Size(0.385f * w, 1f * u))
        drawRect(sombra(o, 0.35f), P(0.135f, 0.476f), Size(0.385f * w, 0.003f * w))
        for (j in 0 until 14) drawLine(sombra(o, 0.35f), P(0.17f + j * 0.022f, 0.471f), P(0.17f + j * 0.022f, 0.474f), 0.7f * u)
        drawRect(c(BAQUELITA), P(0.165f, 0.449f), Size(0.325f * w, 0.021f * w))
        drawRect(brillo(o, 0.35f, k), P(0.165f, 0.452f), Size(0.325f * w, 0.004f * w))
        drawRect(sombra(o, 0.4f), P(0.165f, 0.464f), Size(0.325f * w, 0.006f * w))
        // La barra sujetapapeles con sus dos rodillitos.
        drawLine(c(ACERO_LUZ), P(0.225f, 0.447f), P(0.425f, 0.447f), 1.6f * u)
        listOf(0.27f, 0.38f).forEach { x -> drawRect(c(BAQUELITA), P(x, 0.444f), Size(0.012f * w, 0.006f * w)) }
        listOf(0.152f, 0.503f).forEach { x ->
            val p = P(x, 0.4595f)
            drawCircle(sombra(o, 0.3f), 0.013f * w, p + Offset(1.5f * u, 1.5f * u))
            drawCircle(c(BAQUELITA), 0.013f * w, p)
            for (j in 0 until 6) drawLine(sombra(o, 0.4f), p + Offset((-0.009f + j * 0.0036f) * w, -0.011f * w), p + Offset((-0.009f + j * 0.0036f) * w, 0.011f * w), 0.7f * u)
            drawArc(brillo(o, 0.7f, k), 190f, 70f, false, p - Offset(0.011f * w, 0.011f * w), Size(0.022f * w, 0.022f * w), style = Stroke(1.2f * u))
        }
        // La palanca de retorno, cromada, a la izquierda.
        val pal = Path().apply { moveTo(0.15f * w, 0.448f * w); cubicTo(0.145f * w, 0.432f * w, 0.13f * w, 0.428f * w, 0.112f * w, 0.424f * w) }
        drawPath(pal, sombra(o, 0.3f), style = Stroke(0.005f * w, cap = StrokeCap.Round))
        drawPath(pal, c(ACERO_LUZ), style = Stroke(0.004f * w, cap = StrokeCap.Round))
        drawCircle(c(BAQUELITA), 0.005f * w, P(0.11f, 0.424f))
        drawPath(pal, brillo(o, 0.8f, k), style = Stroke(0.0012f * w))
        // El cuerpo de esmalte, con su costado derecho en sombra.
        val cuerpo = pl(0.16f * w, pie * w, 0.5f * w, pie * w, 0.49f * w, 0.5f * w, 0.465f * w, 0.478f * w, 0.195f * w, 0.478f * w, 0.17f * w, 0.5f * w)
        drawPath(cuerpo, c(esmalte))
        clipPath(cuerpo) {
            drawRect(brillo(o, 0.18f, k), P(0.16f, 0.47f), Size(0.07f * w, 0.12f * w))
            drawPath(pl(0.465f * w, 0.478f * w, 0.49f * w, 0.5f * w, 0.5f * w, pie * w, 0.47f * w, pie * w), sombra(o, 0.3f))
            pinceladas(Rect(0.16f * w, 0.478f * w, 0.5f * w, pie * w), if (o) NOCHE else BAQUELITA, 5212, 6, u, 0.12f)
        }
        drawLine(brillo(o, 0.7f, k), P(0.195f, 0.478f), P(0.465f, 0.478f), 1.2f * u)
        drawLine(brillo(o, 0.45f, k), P(0.17f, 0.5f), P(0.195f, 0.478f), 1f * u)
        // La cesta de las barras de tipos, honda, con su abanico.
        val cesta = Rect(0.255f * w, 0.47f * w, 0.405f * w, 0.512f * w)
        drawArc(c(BAQUELITA), 0f, 180f, true, cesta.topLeft, cesta.size)
        for (j in 0 until 17) {
            val a = 3.1416f * (j + 0.5f) / 17f
            val p = Offset(cesta.center.x + cos(a) * cesta.width * 0.47f, cesta.center.y + sin(a) * cesta.height * 0.47f)
            drawLine(c(ACERO_LUZ).copy(alpha = 0.55f), p, Offset(cesta.center.x, cesta.center.y - 0.002f * w), 0.6f * u)
        }
        // La guía de la cinta, roja y negra.
        drawRect(c(CARTEL), P(0.318f, 0.47f), Size(0.024f * w, 0.0035f * w))
        drawRect(c(BAQUELITA), P(0.318f, 0.4735f), Size(0.024f * w, 0.0035f * w))
        drawRect(c(ACERO_LUZ), P(0.326f, 0.468f), Size(0.008f * w, 0.01f * w))
        // Las tapas de los carretes.
        listOf(0.218f, 0.442f).forEach { x ->
            drawOval(c(ACERO_LUZ), Offset((x - 0.018f) * w, 0.482f * w), Size(0.036f * w, 0.012f * w))
            drawOval(sombra(o, 0.25f), Offset((x - 0.008f) * w, 0.485f * w), Size(0.016f * w, 0.006f * w))
            drawArc(brillo(o, 0.7f, k), 180f, 100f, false, Offset((x - 0.018f) * w, 0.482f * w), Size(0.036f * w, 0.012f * w), style = Stroke(1f * u))
        }
        // Las teclas: cuatro filas en escalera, cada una con su aro, su sombra y su letra clara.
        for (fila in 0 until 4) {
            val y = 0.514f + fila * 0.0155f
            val cuantas = 11 - (fila / 2)
            val paso = 0.0245f
            val x0 = 0.33f - paso * (cuantas - 1) / 2f + fila * 0.004f
            for (j in 0 until cuantas) {
                val p = P(x0 + j * paso, y)
                val r = 0.0082f * w
                drawCircle(sombra(o, 0.4f), r, p + Offset(1.2f * u, 1.8f * u))
                drawLine(c(BAQUELITA), p, p + Offset(0f, 0.008f * w), 1.4f * u)
                drawCircle(c(ACERO_LUZ, x0 + j * paso, y), r, p)
                drawCircle(c(BAQUELITA, x0 + j * paso, y), r * 0.76f, p)
                drawRect(c(HUESO, x0 + j * paso, y).copy(alpha = 0.7f), p - Offset(r * 0.18f, r * 0.22f), Size(r * 0.36f, r * 0.44f))
                drawArc(brillo(o, 0.75f, kk(o, x0 + j * paso, y)), 190f, 80f, false, p - Offset(r, r), Size(2 * r, 2 * r), style = Stroke(0.8f * u))
            }
        }
        // La barra espaciadora y las patas de goma.
        drawRoundRect(sombra(o, 0.35f), P(0.237f, 0.574f), Size(0.19f * w, 0.0065f * w), CornerRadius(2f * u))
        drawRoundRect(c(ACERO_LUZ), P(0.235f, 0.571f), Size(0.19f * w, 0.0055f * w), CornerRadius(2f * u))
        drawLine(brillo(o, 0.7f, k), P(0.238f, 0.5715f), P(0.42f, 0.5715f), 0.7f * u)
        listOf(0.165f, 0.48f).forEach { x -> drawRect(c(BAQUELITA), P(x, pie - 0.004f), Size(0.016f * w, 0.006f * w)) }
    }

    /** El flexo de base pesada y pantalla metálica, doblado sobre la máquina. De día, apagado; de noche, la luz que manda. */
    private fun DrawScope.flexo(o: Boolean) {
        val w = size.width; val u = density
        fun P(x: Float, y: Float) = Offset(x * w, y * w)
        val k = kk(o, 0.55f, 0.45f)
        val metal = mezcla(OLIVA, ACERO_OSC, 0.45f)
        fun c(col: Color, x: Float, y: Float) = n(col, o, kk(o, x, y))
        val base = Offset(0.583f, 0.578f)
        val codo = Offset(0.622f, 0.372f)
        val cabeza = Offset(0.483f, 0.303f)
        // Las sombras del brazo en la pared (de día; de noche el brazo está detrás de la luz).
        if (!o) {
            val s = Path().apply { moveTo(base.x * w, (base.y - 0.01f) * w); lineTo(codo.x * w, codo.y * w); lineTo(cabeza.x * w, cabeza.y * w) }
            translate(0.018f * w, 0.012f * w) { drawPath(s, sombra(false, 0.2f), style = Stroke(0.007f * w, cap = StrokeCap.Round)) }
        }
        // La base, un domo pesado.
        drawOval(sombra(o, 0.4f), P(base.x - 0.034f, base.y - 0.004f), Size(0.074f * w, 0.012f * w))
        drawOval(c(metal, base.x, base.y), P(base.x - 0.035f, base.y - 0.014f), Size(0.07f * w, 0.018f * w))
        drawArc(c(metal, base.x, base.y), 180f, 180f, true, P(base.x - 0.022f, base.y - 0.026f), Size(0.044f * w, 0.028f * w))
        drawArc(brillo(o, 0.55f, k), 200f, 60f, false, P(base.x - 0.022f, base.y - 0.026f), Size(0.044f * w, 0.028f * w), style = Stroke(1.2f * u))
        drawCircle(c(BAQUELITA, base.x, base.y), 0.0035f * w, P(base.x + 0.018f, base.y - 0.008f))
        // Los dos tramos del brazo, cada uno de dos varillas, con el muelle y las tuercas.
        fun brazo(a: Offset, b: Offset) {
            val v = b - a; val l = v.getDistance(); val nn = Offset(-v.y / l, v.x / l) * 0.005f
            listOf(nn, -nn).forEachIndexed { i, s ->
                drawLine(c(metal, a.x, a.y), P(a.x + s.x, a.y + s.y), P(b.x + s.x, b.y + s.y), (if (i == 0) 2.2f else 1.6f) * u, StrokeCap.Round)
            }
            drawLine(brillo(o, 0.6f, kk(o, (a.x + b.x) / 2, (a.y + b.y) / 2)), P(a.x + nn.x, a.y + nn.y), P(b.x + nn.x, b.y + nn.y), 0.7f * u)
            // El muelle: una espiral corta a un tercio.
            for (j in 0 until 7) {
                val f = 0.25f + j * 0.025f
                val p = a + v * f
                drawLine(c(ACERO_LUZ, p.x, p.y), P(p.x + nn.x * 1.6f, p.y + nn.y * 1.6f), P(p.x - nn.x * 1.6f + v.x * 0.012f, p.y - nn.y * 1.6f + v.y * 0.012f), 0.8f * u)
            }
        }
        brazo(Offset(base.x, base.y - 0.024f), codo)
        brazo(codo, cabeza)
        listOf(codo, cabeza, Offset(base.x, base.y - 0.024f)).forEach { p ->
            drawCircle(c(metal, p.x, p.y), 0.0065f * w, P(p.x, p.y))
            drawCircle(c(ACERO_LUZ, p.x, p.y), 0.0028f * w, P(p.x, p.y))
        }
        // La pantalla, un cono de chapa: por fuera oliva oscuro con su brillo; la boca, oscura de día y encendida de noche.
        val dir = Offset(-0.447f, 0.894f); val perp = Offset(0.894f, 0.447f)
        val ap = cabeza - dir * 0.008f
        val b1 = ap + perp * 0.011f; val b2 = ap - perp * 0.011f
        val m1 = BOMBILLA + perp * 0.036f; val m2 = BOMBILLA - perp * 0.036f
        val cono = Path().apply {
            moveTo(b2.x * w, b2.y * w); lineTo(b1.x * w, b1.y * w)
            cubicTo((b1.x + 0.012f) * w, (b1.y + 0.02f) * w, (m1.x + 0.004f) * w, (m1.y - 0.02f) * w, m1.x * w, m1.y * w)
            lineTo(m2.x * w, m2.y * w)
            cubicTo((m2.x - 0.006f) * w, (m2.y - 0.024f) * w, (b2.x - 0.012f) * w, (b2.y + 0.008f) * w, b2.x * w, b2.y * w)
            close()
        }
        if (!o) translate(0.016f * w, 0.012f * w) { drawPath(cono, sombra(false, 0.18f)) }
        drawPath(cono, c(metal, cabeza.x, cabeza.y + 0.03f))
        clipPath(cono) {
            drawPath(Path().apply { moveTo(b2.x * w, b2.y * w); lineTo(m2.x * w, m2.y * w); lineTo((m2.x + 0.02f) * w, (m2.y + 0.004f) * w); lineTo((b2.x + 0.006f) * w, b2.y * w); close() },
                brillo(o, if (o) 0.3f else 0.4f, 1f))
            drawPath(Path().apply { moveTo(b1.x * w, b1.y * w); lineTo(m1.x * w, m1.y * w); lineTo((m1.x - 0.018f) * w, (m1.y - 0.004f) * w); close() }, sombra(o, 0.3f))
        }
        drawCircle(c(metal, cabeza.x, cabeza.y), 0.007f * w, P(ap.x, ap.y))
        val bocaC = P(BOMBILLA.x, BOMBILLA.y)
        rotate(26.6f, bocaC) {
            val boca = Rect(bocaC.x - 0.036f * w, bocaC.y - 0.009f * w, bocaC.x + 0.036f * w, bocaC.y + 0.009f * w)
            if (o) {
                drawOval(FLEXO, boca.topLeft, boca.size)
                drawOval(Color(0xFFFFF4DC), Offset(bocaC.x - 0.016f * w, bocaC.y - 0.005f * w), Size(0.032f * w, 0.01f * w))
            } else {
                drawOval(n(mezcla(BAQUELITA, metal, 0.3f), false, 0f), boca.topLeft, boca.size)
                drawOval(mezcla(HUESO, ACERO_LUZ, 0.5f), Offset(bocaC.x - 0.012f * w, bocaC.y - 0.004f * w), Size(0.024f * w, 0.008f * w))
            }
            drawOval(c(ACERO_LUZ, BOMBILLA.x, BOMBILLA.y), boca.topLeft, boca.size, style = Stroke(1.2f * u))
        }
    }

    /** El teléfono de disco, de baquelita negra brillante: el cuerpo, el disco con sus agujeros, el auricular y el cordón rizado. */
    private fun DrawScope.telefono(o: Boolean) {
        val w = size.width; val u = density
        fun P(x: Float, y: Float) = Offset(x * w, y * w)
        val k = kk(o, 0.705f, 0.55f)
        fun c(col: Color) = n(col, o, k)
        val pie = 0.581f
        val cuerpo = Path().apply {
            moveTo(0.64f * w, pie * w); lineTo(0.772f * w, pie * w); lineTo(0.763f * w, 0.545f * w)
            cubicTo(0.76f * w, 0.535f * w, 0.75f * w, 0.532f * w, 0.74f * w, 0.532f * w)
            lineTo(0.672f * w, 0.532f * w)
            cubicTo(0.662f * w, 0.532f * w, 0.652f * w, 0.535f * w, 0.649f * w, 0.545f * w); close()
        }
        sombraEnPared(Path().apply { addPath(cuerpo); addRect(Rect(0.63f * w, 0.51f * w, 0.785f * w, 0.535f * w)) }, 0.705f, 0.54f, o, 0.24f)
        drawOval(sombra(o, if (o) 0.6f else 0.38f), P(0.642f, pie - 0.006f), Size(0.14f * w, 0.012f * w))
        drawPath(cuerpo, c(BAQUELITA))
        clipPath(cuerpo) {
            drawRect(brillo(o, 0.22f, k), P(0.64f, 0.53f), Size(0.025f * w, 0.06f * w))
            drawRect(sombra(o, 0.35f), P(0.75f, 0.53f), Size(0.03f * w, 0.06f * w))
        }
        drawLine(brillo(o, 0.6f, k), P(0.672f, 0.533f), P(0.74f, 0.533f), 1f * u)
        // El disco: el plato con los números, la rueda con sus diez agujeros, el centro con su etiqueta y el tope.
        val dc = P(0.706f, 0.559f); val dr = 0.0205f * w
        drawCircle(c(HUESO), dr * 0.98f, dc)
        drawCircle(c(mezcla(BAQUELITA, ACERO_OSC, 0.25f)), dr, dc, style = Stroke(1f * u))
        val rueda = Path().apply {
            addOval(Rect(dc, dr))
            for (j in 0 until 10) {
                val a = (-60f - j * 27f) * 3.1416f / 180f
                addOval(Rect(dc + Offset(cos(a), sin(a)) * (dr * 0.68f), dr * 0.2f))
            }
            fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        }
        drawPath(rueda, c(mezcla(BAQUELITA, ACERO_LUZ, 0.12f)).copy(alpha = 0.92f))
        drawCircle(c(HUESO), dr * 0.32f, dc)
        drawLine(c(BAQUELITA).copy(alpha = 0.5f), dc + Offset(-dr * 0.18f, 0f), dc + Offset(dr * 0.18f, 0f), 0.7f * u)
        drawArc(brillo(o, 0.6f, k), 200f, 70f, false, dc - Offset(dr, dr), Size(2 * dr, 2 * dr), style = Stroke(1f * u))
        drawLine(c(ACERO_LUZ), dc + Offset(dr * 0.75f, dr * 0.5f), dc + Offset(dr * 1.05f, dr * 0.7f), 1.4f * u, StrokeCap.Round)
        // Las horquillas y el auricular, con sus dos cazoletas.
        listOf(0.668f, 0.744f).forEach { x -> drawRoundRect(c(BAQUELITA), P(x - 0.006f, 0.524f), Size(0.012f * w, 0.012f * w), CornerRadius(2f * u)) }
        val aur = Path().apply {
            moveTo(0.636f * w, 0.53f * w)
            cubicTo(0.632f * w, 0.512f * w, 0.652f * w, 0.508f * w, 0.665f * w, 0.515f * w)
            lineTo(0.75f * w, 0.515f * w)
            cubicTo(0.763f * w, 0.508f * w, 0.784f * w, 0.512f * w, 0.78f * w, 0.53f * w)
            cubicTo(0.776f * w, 0.538f * w, 0.758f * w, 0.538f * w, 0.75f * w, 0.526f * w)
            lineTo(0.665f * w, 0.526f * w)
            cubicTo(0.657f * w, 0.538f * w, 0.64f * w, 0.538f * w, 0.636f * w, 0.53f * w); close()
        }
        translate(1.2f * u, 2f * u) { drawPath(aur, sombra(o, 0.4f)) }
        drawPath(aur, c(BAQUELITA))
        drawLine(brillo(o, 0.75f, k), P(0.666f, 0.5165f), P(0.749f, 0.5165f), 1.1f * u, StrokeCap.Round)
        drawArc(brillo(o, 0.6f, k), 190f, 70f, false, P(0.637f, 0.512f), Size(0.03f * w, 0.024f * w), style = Stroke(1f * u))
        drawArc(brillo(o, 0.6f, k), 190f, 70f, false, P(0.752f, 0.512f), Size(0.03f * w, 0.024f * w), style = Stroke(1f * u))
        // El cordón rizado, que cae al tablero y vuelve al costado del cuerpo.
        val a = P(0.638f, 0.533f); val b = P(0.645f, 0.57f)
        for (j in 0..15) {
            val f = j / 15f
            val p = Offset(a.x + (b.x - a.x) * f - sin(f * 3.1416f) * 0.03f * w, a.y + (b.y - a.y) * f + sin(f * 3.1416f) * 0.012f * w)
            drawOval(c(BAQUELITA), Offset(p.x - 0.0035f * w, p.y - 0.0025f * w), Size(0.007f * w, 0.005f * w), style = Stroke(1.1f * u))
            if (j % 2 == 0) drawCircle(brillo(o, 0.4f, k), 0.6f * u, p - Offset(0.002f * w, 0.0015f * w))
        }
    }

    /** La taza de esmalte hueso con el borde oliva, desportillada, delante del teléfono. */
    private fun DrawScope.taza(o: Boolean) {
        val w = size.width; val u = density
        val cx = 0.793f; val pie = 0.587f
        val k = kk(o, cx, 0.57f)
        fun c(col: Color) = n(col, o, k)
        val r = 0.016f * w; val h = 0.038f * w
        val x = cx * w; val yb = pie * w
        drawOval(sombra(o, 0.4f), Offset(x - r * 0.8f, yb - 0.004f * w), Size(r * 2.6f, 0.009f * w))
        // El asa.
        drawArc(c(HUESO), -80f, 170f, false, Offset(x + r * 0.55f, yb - h * 0.82f), Size(r * 1.1f, h * 0.55f), style = Stroke(0.0045f * w))
        drawArc(sombra(o, 0.3f), 0f, 90f, false, Offset(x + r * 0.55f, yb - h * 0.82f), Size(r * 1.1f, h * 0.55f), style = Stroke(0.002f * w))
        // El cuerpo cilíndrico.
        drawRect(c(HUESO), Offset(x - r, yb - h), Size(2 * r, h))
        drawOval(c(HUESO), Offset(x - r, yb - 0.004f * w), Size(2 * r, 0.006f * w))
        drawRect(brillo(o, 0.5f, k), Offset(x - r * 0.7f, yb - h), Size(r * 0.3f, h))
        drawRect(sombra(o, 0.22f), Offset(x + r * 0.35f, yb - h), Size(r * 0.65f, h))
        // El borde oliva y la boca con el café.
        drawRect(c(OLIVA), Offset(x - r, yb - h), Size(2 * r, 0.004f * w))
        drawOval(c(OLIVA), Offset(x - r, yb - h - 0.004f * w), Size(2 * r, 0.008f * w))
        drawOval(c(mezcla(CARTON, BAQUELITA, 0.6f)), Offset(x - r * 0.82f, yb - h - 0.0025f * w), Size(r * 1.64f, 0.005f * w))
        // Los desportillados: el hierro negro que asoma.
        saltado(Offset(x - r * 0.4f, yb - h + 0.002f * w), 0.0035f * w, 5220, o, k)
        saltado(Offset(x + r * 0.2f, yb - h * 0.35f), 0.003f * w, 5221, o, k)
    }

    /** La papelera de rejilla metálica, con tiras de papel cortado que rebosan y una bola de papel arrugado. */
    private fun DrawScope.papelera(r: Rect, o: Boolean) {
        val w = size.width; val u = density
        val k = kk(o, r.center.x / w, r.center.y / w)
        fun c(col: Color) = n(col, o, k)
        val ry = 0.014f * w
        val dentro = 0.014f * w
        drawOval(sombra(o, 0.42f), Offset(r.left + dentro, r.bottom - 0.006f * w), Size(r.width + 0.02f * w, 0.014f * w))
        val cuerpo = pl(r.left, r.top, r.right, r.top, r.right - dentro, r.bottom, r.left + dentro, r.bottom)
        // Lo de dentro: oscuro, con el papel que se ve entre los alambres.
        drawPath(cuerpo, sombra(o, 0.55f))
        clipPath(cuerpo) {
            for (j in 0 until 14) {
                val x = r.left + r.width * (0.08f + 0.84f * azar(5230, j))
                val y = r.top + r.height * (0.1f + 0.5f * azar(5231, j))
                drawLine(c(if (j % 3 == 0) CARPETA else HUESO).copy(alpha = 0.7f), Offset(x, y), Offset(x + (azar(5232, j) - 0.5f) * 0.03f * w, r.bottom), 1.8f * u)
            }
        }
        // Las tiras que rebosan por arriba y la bola arrugada.
        for (j in 0 until 16) {
            val x = r.left + r.width * (0.1f + 0.8f * azar(5233, j))
            val alto = (0.015f + 0.035f * azar(5234, j)) * w
            val p = Path().apply {
                moveTo(x, r.top + ry * 0.4f)
                cubicTo(x - 0.008f * w, r.top - alto * 0.4f, x + 0.01f * w, r.top - alto * 0.7f, x + (azar(5235, j) - 0.5f) * 0.02f * w, r.top - alto)
            }
            drawPath(p, c(if (j % 4 == 0) CARPETA else HUESO), style = Stroke(1.9f * u))
            drawPath(p, sombra(o, 0.18f), style = Stroke(0.6f * u))
        }
        val bola = irregular(Offset(r.left + r.width * 0.68f, r.top - 0.008f * w), 0.022f * w, 0.017f * w, 5236, 11, 0.35f)
        translate(1.5f * u, 2f * u) { drawPath(bola, sombra(o, 0.3f)) }
        drawPath(bola, c(HUESO))
        clipPath(bola) {
            drawRect(sombra(o, 0.18f), Offset(r.left + r.width * 0.68f, r.top - 0.03f * w), Size(0.03f * w, 0.05f * w))
            for (j in 0 until 4) drawLine(sombra(o, 0.25f), Offset(r.left + r.width * (0.6f + 0.04f * j), r.top - 0.022f * w), Offset(r.left + r.width * (0.64f + 0.03f * j), r.top + 0.004f * w), 0.7f * u)
        }
        // La rejilla: alambres que bajan estrechándose y aros, oscuros, con luz a la izquierda.
        val alambre = c(mezcla(OLIVA, BAQUELITA, 0.5f))
        for (j in 0..12) {
            val f = j / 12f
            val a = Offset(r.left + r.width * f, r.top + ry * 0.5f * sin(f * 3.1416f))
            val b = Offset(r.left + dentro + (r.width - 2 * dentro) * f, r.bottom)
            drawLine(alambre, a, b, 1f * u)
            if (f < 0.4f) drawLine(brillo(o, 0.3f, k), a + Offset(0.6f * u, 0f), b + Offset(0.6f * u, 0f), 0.5f * u)
        }
        for (j in 1..4) {
            val f = j / 5f
            val x0 = r.left + dentro * f; val x1 = r.right - dentro * f
            val y = r.top + r.height * f
            drawArc(alambre, 0f, 180f, false, Offset(x0, y - ry * 0.5f), Size(x1 - x0, ry), style = Stroke(1f * u))
        }
        drawOval(alambre, Offset(r.left, r.top - ry * 0.5f), Size(r.width, ry), style = Stroke(2.2f * u))
        drawArc(brillo(o, 0.5f, k), 150f, 80f, false, Offset(r.left, r.top - ry * 0.5f), Size(r.width, ry), style = Stroke(1f * u))
        drawRect(alambre, Offset(r.left + dentro, r.bottom - 0.01f * w), Size(r.width - 2 * dentro, 0.01f * w))
    }

    /** La caja de archivo de cartón, cortada por el borde: su tapa con la cinta de embalar, la etiqueta y el agujero para la mano. */
    private fun DrawScope.caja(r: Rect, o: Boolean) {
        val w = size.width; val u = density
        val k = kk(o, 0.88f, r.center.y / w)
        fun c(col: Color) = n(col, o, k)
        drawRect(sombra(o, 0.45f), Offset(r.left + 0.01f * w, r.bottom - 0.004f * w), Size(r.width, 0.01f * w))
        // La cara de arriba (se ve desde arriba), la tapa y su canto.
        val tapaAlto = 0.03f * w
        drawPath(pl(r.left + 0.012f * w, r.top - 0.014f * w, r.right, r.top - 0.014f * w, r.right, r.top, r.left - 0.004f * w, r.top), c(mezcla(CARTON, HUESO, 0.25f)))
        drawRect(c(mezcla(CARTON, BURDEOS, 0.15f)).copy(alpha = 0.7f), Offset(r.left + 0.06f * w, r.top - 0.014f * w), Size(0.03f * w, 0.014f * w))
        drawRect(c(CARTON), Offset(r.left - 0.004f * w, r.top), Size(r.width + 0.004f * w, tapaAlto))
        drawRect(brillo(o, 0.45f, k), Offset(r.left - 0.004f * w, r.top), Size(r.width, 1f * u))
        drawRect(sombra(o, 0.35f), Offset(r.left, r.top + tapaAlto), Size(r.width, 2f * u))
        // El cuerpo, con la luz a la izquierda y un golpe en la esquina.
        val cuerpo = Rect(r.left, r.top + tapaAlto, r.right, r.bottom)
        drawRect(c(mezcla(CARTON, OXIDO, 0.15f)), cuerpo.topLeft, cuerpo.size)
        drawRect(brillo(o, 0.25f, k), cuerpo.topLeft, Size(0.012f * w, cuerpo.height))
        pinceladas(cuerpo, if (o) NOCHE else OXIDO, 5240, 6, u, 0.14f)
        drawLine(sombra(o, 0.3f), Offset(r.left, r.top + tapaAlto + 0.02f * w), Offset(r.left + 0.015f * w, r.top + tapaAlto + 0.008f * w), 1f * u)
        // La etiqueta con su raya roja y el número, y el agujero para la mano.
        val et = Rect(r.left + 0.03f * w, cuerpo.top + cuerpo.height * 0.22f, r.left + 0.12f * w, cuerpo.top + cuerpo.height * 0.72f)
        drawRect(sombra(o, 0.2f), Offset(et.left + 1f * u, et.top + 1.2f * u), et.size)
        drawRect(c(HUESO), et.topLeft, et.size)
        drawRect(c(CARTEL), et.topLeft, Size(et.width, et.height * 0.16f))
        estarcido("31", et.left + et.width * 0.12f, et.top + et.height * 0.32f, et.height * 0.28f, et.height * 0.48f, c(BAQUELITA).copy(alpha = 0.8f))
        drawLine(c(BAQUELITA).copy(alpha = 0.5f), Offset(et.left + et.width * 0.5f, et.top + et.height * 0.45f), Offset(et.right - et.width * 0.1f, et.top + et.height * 0.45f), 0.8f * u)
        drawLine(c(BAQUELITA).copy(alpha = 0.5f), Offset(et.left + et.width * 0.5f, et.top + et.height * 0.65f), Offset(et.right - et.width * 0.2f, et.top + et.height * 0.65f), 0.8f * u)
        drawRoundRect(c(mezcla(CARTON, BAQUELITA, 0.7f)), Offset(r.left + 0.15f * w, cuerpo.top + cuerpo.height * 0.2f), Size(0.04f * w, 0.012f * w), CornerRadius(0.006f * w))
        drawLine(brillo(o, 0.35f, k), Offset(r.left + 0.152f * w, cuerpo.top + cuerpo.height * 0.2f + 0.013f * w), Offset(r.left + 0.188f * w, cuerpo.top + cuerpo.height * 0.2f + 0.013f * w), 0.8f * u)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Cuánto alumbra el flexo una gaveta, según su fila y su columna (el centro de su hueco, en fracciones del ancho). */
    private fun cercaDe(c: EstadoCajon): Float {
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        return deFlexo(centro)
    }

    /** El frente de chapa de una gaveta: la pintura con su luz, el reborde prensado, la pintura saltada y las rozaduras. */
    private fun DrawScope.frente(f: Rect, semilla: Int, o: Boolean, k: Float, dia: Float) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(n(ACERO, o, k), f.topLeft, f.size)
        drawRect(if (o) FLEXO.copy(alpha = 0.08f * k) else LUZ_TUBO.copy(alpha = 0.16f + dia), f.topLeft, Size(f.width * 0.34f, f.height))
        drawRect(sombra(o, 0.14f), Offset(f.right - f.width * 0.32f, f.top), Size(f.width * 0.32f, f.height))
        drawRect(sombra(o, 0.1f), Offset(f.right - f.width * 0.1f, f.top), Size(f.width * 0.1f, f.height))
        if (dia > 0f) drawRect(LUZ_TUBO.copy(alpha = dia), f.topLeft, f.size)
        // El reborde: el canto de arriba con luz, el de abajo y el derecho en sombra.
        drawRect(brillo(o, 0.6f, k + 0.1f), f.topLeft, Size(f.width, 1.4f * u))
        drawRect(brillo(o, 0.3f, k), f.topLeft, Size(1.2f * u, f.height))
        drawRect(sombra(o, 0.45f), Offset(f.left, f.bottom - 1.8f * u), Size(f.width, 1.8f * u))
        drawRect(sombra(o, 0.3f), Offset(f.right - 1.4f * u, f.top), Size(1.4f * u, f.height))
        // El panel prensado: un escalón fino a todo alrededor.
        val p = Rect(f.left + w * 0.035f, f.top + h * 0.09f, f.right - w * 0.035f, f.bottom - h * 0.09f)
        drawRect(sombra(o, 0.22f), p.topLeft, Size(p.width, 1f * u))
        drawRect(sombra(o, 0.18f), p.topLeft, Size(1f * u, p.height))
        drawRect(brillo(o, 0.4f, k), Offset(p.left, p.bottom - 1f * u), Size(p.width, 1f * u))
        drawRect(brillo(o, 0.3f, k), Offset(p.right - 1f * u, p.top), Size(1f * u, p.height))
        // Lo gastado: pintura saltada en las esquinas y abajo, rozaduras y a veces una abolladura.
        val cuantos = 3 + Math.floorMod(semilla, 3)
        for (j in 0 until cuantos) {
            val esq = Math.floorMod(semilla + j * 3, 4)
            val x = if (esq % 2 == 0) f.left + w * (0.01f + 0.05f * azar(semilla, 500 + j)) else f.right - w * (0.01f + 0.05f * azar(semilla, 500 + j))
            val y = if (esq < 2) f.top + h * (0.03f + 0.05f * azar(semilla, 510 + j)) else f.bottom - h * (0.03f + 0.06f * azar(semilla, 510 + j))
            saltado(Offset(x, y), w * (0.006f + 0.01f * azar(semilla, 520 + j)), semilla + j, o, k)
        }
        for (j in 0 until 3) {
            val x = f.left + f.width * (0.15f + 0.7f * azar(semilla, 530 + j)); val y = f.top + f.height * (0.65f + 0.25f * azar(semilla, 540 + j))
            drawLine(brillo(o, 0.25f, k), Offset(x, y), Offset(x + w * (0.03f + 0.05f * azar(semilla, 550 + j)), y - 1f * u), 0.7f * u)
        }
        if (Math.floorMod(semilla / 7, 3) == 0) abolladura(Offset(f.left + f.width * 0.18f, f.top + f.height * 0.74f), w * 0.045f, h * 0.07f, o, k)
    }

    /** Una abolladura: un hundido suave, en sombra por arriba a la izquierda y con luz en su borde de abajo a la derecha. */
    private fun DrawScope.abolladura(c: Offset, rx: Float, ry: Float, o: Boolean, k: Float) {
        drawOval(sombra(o, 0.09f), Offset(c.x - rx, c.y - ry), Size(2 * rx, 2 * ry))
        drawOval(sombra(o, 0.07f), Offset(c.x - rx * 0.9f, c.y - ry * 0.9f), Size(rx * 1.3f, ry * 1.2f))
        drawOval(brillo(o, 0.16f, k), Offset(c.x - rx * 0.2f, c.y - ry * 0.05f), Size(rx * 1.1f, ry * 0.95f))
    }

    /** El tarjetero de chapa, con su tarjeta ([tarjetaColor]) o vacío (null): las pestañas que la sujetan y sus remaches. */
    private fun DrawScope.tarjetero(r: Rect, tarjetaColor: Color?, o: Boolean, k: Float) {
        val u = density
        val m = 2.4f * u
        val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
        drawRect(sombra(o, 0.4f), Offset(marco.left + 1.2f * u, marco.top + 1.8f * u), marco.size)
        drawRect(n(ACERO_LUZ, o, k), marco.topLeft, marco.size)
        drawRect(brillo(o, 0.6f, k + 0.2f), marco.topLeft, Size(marco.width, 1f * u))
        drawRect(sombra(o, 0.35f), Offset(marco.left, marco.bottom - 1f * u), Size(marco.width, 1f * u))
        if (tarjetaColor != null) {
            drawRect(tarjetaColor, r.topLeft, r.size)
            // La raya roja de las fichas y la sombra de la pestaña de arriba.
            drawRect(n(CARTEL, o, k).copy(alpha = 0.55f), Offset(r.left, r.top + r.height * 0.1f), Size(r.width, 0.9f * u))
            drawRect(sombra(o, 0.18f), r.topLeft, Size(r.width, 1.4f * u))
        } else {
            // El tarjetero vacío: la chapa del fondo, más oscura, con la marca limpia de la tarjeta que hubo.
            drawRect(n(mezcla(ACERO, ACERO_OSC, 0.55f), o, k), r.topLeft, r.size)
            drawRect(n(mezcla(ACERO, ACERO_OSC, 0.3f), o, k), Offset(r.left + r.width * 0.06f, r.top + r.height * 0.2f), Size(r.width * 0.88f, r.height * 0.7f))
            drawRect(sombra(o, 0.35f), r.topLeft, Size(r.width, r.height * 0.18f))
            drawLine(brillo(o, 0.25f, k), Offset(r.left, r.bottom - 1f * u), Offset(r.right, r.bottom - 1f * u), 0.8f * u)
        }
        // Las pestañas de los lados, que pisan la tarjeta.
        listOf(r.left - m, r.right - 1.4f * u).forEach { x ->
            drawRect(n(ACERO_LUZ, o, k), Offset(x, r.top), Size(m + 1.4f * u, r.height))
            drawRect(sombra(o, 0.2f), Offset(x + m + 1.2f * u, r.top), Size(0.6f * u, r.height))
            drawCircle(n(HIERRO, o, k), 0.9f * u, Offset(x + (m + 1.4f * u) / 2, r.center.y))
        }
    }

    /** Un tirador de barra cromado, con sus dos pies y su sombra. [filo] es la luz mínima que recoge de noche. */
    private fun DrawScope.tiradorBarra(cx: Float, y: Float, ancho: Float, o: Boolean, filo: Float) {
        val u = density
        drawLine(sombra(o, 0.4f), Offset(cx - ancho / 2 + 2f * u, y + 3f * u), Offset(cx + ancho / 2 + 2f * u, y + 3f * u), 3.4f * u, StrokeCap.Round)
        listOf(cx - ancho / 2, cx + ancho / 2).forEach { x -> drawRect(n(HIERRO, o, filo), Offset(x - 1.8f * u, y - 1.5f * u), Size(3.6f * u, 6f * u)) }
        drawLine(n(ACERO_LUZ, o, filo), Offset(cx - ancho / 2, y), Offset(cx + ancho / 2, y), 3.4f * u, StrokeCap.Round)
        drawLine(brillo(o, 0.8f, filo), Offset(cx - ancho / 2, y - 0.9f * u), Offset(cx + ancho / 2, y - 0.9f * u), 0.9f * u, StrokeCap.Round)
    }

    /**
     * Un tirador de concha: una capucha cromada en forma de media luna, abombada (luz arriba a la izquierda), que se abre hacia
     * abajo: debajo de su borde, el hueco oscuro donde entran los dedos. Atornillada por sus dos orejas.
     */
    private fun DrawScope.concha(cx: Float, y: Float, ancho: Float, alto: Float, o: Boolean, filo: Float) {
        val u = density
        val capucha = Rect(cx - ancho / 2, y - alto, cx + ancho / 2, y + alto)
        // Las orejas con sus tornillos.
        listOf(-1f, 1f).forEach { s ->
            val ox = cx + s * ancho * 0.56f
            drawRoundRect(n(ACERO_LUZ, o, filo), Offset(ox - ancho * 0.09f, y - alto * 0.45f), Size(ancho * 0.18f, alto * 0.6f), CornerRadius(alto * 0.2f))
            drawCircle(n(HIERRO, o, filo), 1f * u, Offset(ox, y - alto * 0.15f))
        }
        // La sombra que deja en el frente, a la derecha y abajo.
        drawArc(sombra(o, 0.38f), 180f, 180f, true, Offset(capucha.left + 1.8f * u, capucha.top + 2.2f * u), capucha.size)
        drawOval(sombra(o, 0.4f), Offset(cx - ancho * 0.46f + 1.6f * u, y - alto * 0.12f + 2f * u), Size(ancho * 0.92f, alto * 0.5f))
        // El hueco oscuro, debajo.
        drawOval(n(BAQUELITA, o, 0f), Offset(cx - ancho * 0.46f, y - alto * 0.18f), Size(ancho * 0.92f, alto * 0.5f))
        // La capucha abombada.
        drawArc(n(ACERO_LUZ, o, filo), 180f, 180f, true, capucha.topLeft, capucha.size)
        drawArc(sombra(o, 0.25f), 290f, 70f, true, capucha.topLeft, capucha.size)
        drawArc(brillo(o, 0.75f, filo), 195f, 75f, false, Offset(capucha.left + ancho * 0.08f, capucha.top + alto * 0.2f), Size(ancho * 0.84f, alto * 1.6f), style = Stroke(1.1f * u))
        drawLine(n(mezcla(ACERO_LUZ, ACERO_OSC, 0.4f), o, filo), Offset(capucha.left, y), Offset(capucha.right, y), 1.2f * u, StrokeCap.Round)
    }

    /** Las carpetas que asoman por arriba de una gaveta entreabierta: más cuantas más fichas, con sus pestañas. */
    private fun DrawScope.carpetasAsoman(w: Float, arriba: Float, cuantas: Int, semilla: Int, sinTema: Boolean, o: Boolean, k: Float) {
        val u = density
        drawRect(n(mezcla(HIERRO, BAQUELITA, 0.5f), o, k * 0.5f), Offset.Zero, Size(w, arriba + 2f * u))
        val m = cuantas.coerceIn(1, 7)
        for (i in 0 until m) {
            val y0 = maxOf(arriba * (0.1f + 0.11f * i).coerceAtMost(0.8f), 4f * u)
            val col = when {
                sinTema -> if (i % 2 == 0) HUESO else mezcla(HUESO, CARPETA, 0.4f)
                Math.floorMod(semilla + i, 5) == 0 -> mezcla(CARPETA, CARTEL, 0.3f)
                else -> mezcla(CARPETA, CARTON, 0.25f * azar(semilla, 600 + i))
            }
            val c = n(col, o, k)
            drawRect(c, Offset(w * 0.05f, y0), Size(w * 0.9f, arriba + 3f * u - y0))
            val tx = w * (0.08f + 0.2f * Math.floorMod(i + semilla, 4))
            drawRect(c, Offset(tx, y0 - 3.4f * u), Size(w * 0.14f, 3.6f * u))
            drawRect(brillo(o, 0.4f, k), Offset(tx, y0 - 3.4f * u), Size(w * 0.14f, 0.8f * u))
            drawLine(brillo(o, 0.35f, k), Offset(w * 0.05f, y0), Offset(w * 0.95f, y0), 0.8f * u)
            drawRect(sombra(o, 0.25f), Offset(w * 0.05f, y0 + 0.8f * u), Size(w * 0.9f, 1.2f * u))
        }
        drawRect(sombra(o, 0.3f), Offset(w * 0.75f, 0f), Size(w * 0.25f, arriba + 2f * u))
    }

    /** Una tira de cinta de precinto de [a] a [b]: papel con dos filetes rojos y letras que no se leen, rota en los extremos. */
    private fun DrawScope.tiraPrecinto(a: Offset, b: Offset, ancho: Float, semilla: Int, o: Boolean, k: Float) {
        val u = density
        val d = b - a; val l = d.getDistance().coerceAtLeast(1f)
        val grados = Math.toDegrees(kotlin.math.atan2(d.y, d.x).toDouble()).toFloat()
        val c = (a + b) / 2f
        rotate(grados, c) {
            val r = Rect(c.x - l / 2, c.y - ancho / 2, c.x + l / 2, c.y + ancho / 2)
            val tira = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top)
                for (j in 1..4) lineTo(r.right - (azar(semilla, j) * 0.3f + (j % 2) * 0.3f) * ancho, r.top + ancho * j / 4f)
                lineTo(r.left, r.bottom)
                for (j in 1..3) lineTo(r.left + (azar(semilla, 10 + j) * 0.3f + (j % 2) * 0.3f) * ancho, r.bottom - ancho * j / 4f)
                close()
            }
            translate(1f * u, 1.6f * u) { drawPath(tira, sombra(o, 0.3f)) }
            drawPath(tira, n(HUESO, o, k))
            clipPath(tira) {
                drawRect(n(CARTEL, o, k), r.topLeft, Size(r.width, ancho * 0.16f))
                drawRect(n(CARTEL, o, k), Offset(r.left, r.bottom - ancho * 0.16f), Size(r.width, ancho * 0.16f))
                var x = r.left + ancho * 0.6f; var q = 0
                while (x < r.right - ancho) {
                    drawRect(n(CARTEL, o, k).copy(alpha = 0.7f), Offset(x, r.top + ancho * 0.36f), Size(ancho * (0.5f + azar(semilla, 20 + q)), ancho * 0.28f))
                    x += ancho * 1.4f; q++
                }
            }
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val o = oscuro
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val k = if (o) cercaDe(c) else 0f
        // El filo de los tiradores: aun la gaveta más lejana recoge un poco de luz.
        val filo = if (o) maxOf(k, 0.3f) else 0f
        // De día, la luz de arriba a la izquierda aclara apenas las filas de arriba y la columna de la izquierda.
        val dia = if (o) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)

        if (c.vacio && Math.floorMod(c.semilla / 5, 2) == 1) {
            // Un hueco sin gaveta: el fondo y los costados de chapa, con los rieles a la vista.
            drawRect(n(mezcla(HIERRO, ACERO_OSC, 0.4f), o, k * 0.6f), Offset.Zero, size)
            val fondo = Rect(w * 0.1f, h * 0.14f, w * 0.9f, h * 0.84f)
            drawRect(n(mezcla(ACERO_OSC, HIERRO, 0.3f), o, k * 0.5f), fondo.topLeft, fondo.size)
            listOf(Offset.Zero to fondo.topLeft, Offset(w, 0f) to Offset(fondo.right, fondo.top), Offset(0f, h) to Offset(fondo.left, fondo.bottom), Offset(w, h) to Offset(fondo.right, fondo.bottom))
                .forEach { (a, b) -> drawLine(sombra(o, 0.5f), a, b, 1f * u) }
            drawPath(pl(0f, 0f, w, 0f, fondo.right, fondo.top, fondo.left, fondo.top), sombra(o, 0.45f))
            drawPath(pl(w, 0f, w, h, fondo.right, fondo.bottom, fondo.right, fondo.top), sombra(o, 0.3f))
            drawPath(pl(0f, h, w, h, fondo.right, fondo.bottom, fondo.left, fondo.bottom), brillo(o, 0.1f, k))
            listOf(true, false).forEach { izq ->
                val x0 = if (izq) 0f else w; val x1 = if (izq) fondo.left else fondo.right
                val riel = pl(x0, h * 0.5f, x1, h * 0.5f, x1, h * 0.56f, x0, h * 0.62f)
                drawPath(riel, n(ACERO_LUZ, o, filo))
                drawLine(brillo(o, 0.6f, filo), Offset(x0, h * 0.5f), Offset(x1, h * 0.5f), 0.9f * u)
                for (j in 0 until 4) drawCircle(n(HIERRO, o, filo), 1f * u, Offset(x0 + (x1 - x0) * (0.15f + 0.22f * j), h * (0.535f + 0.012f * (3 - j) / 3f)))
            }
            // Un clip olvidado en el fondo.
            val cp = Offset(w * 0.6f, h * 0.8f)
            drawRoundRect(n(ACERO_LUZ, o, filo), Offset(cp.x, cp.y), Size(w * 0.06f, h * 0.035f), CornerRadius(h * 0.017f), style = Stroke(0.8f * u))
            drawRoundRect(n(ACERO_LUZ, o, filo), Offset(cp.x + w * 0.008f, cp.y + h * 0.007f), Size(w * 0.04f, h * 0.02f), CornerRadius(h * 0.01f), style = Stroke(0.8f * u))
            drawRect(sombra(o, 0.5f), Offset.Zero, Size(w, 2.4f * u))
            return
        }

        val cuantas = if (c.vacio) 0 else c.n
        // Las más llenas quedan entreabiertas; la de las ideas sin tema, un poco más.
        val entre = if (c.vacio || cuantas == 0) 0f else h * (0.03f + 0.014f * cuantas.coerceIn(0, 7)) + (if (c.sinTema) h * 0.015f else 0f)
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) carpetasAsoman(w, arriba, cuantas.coerceAtLeast(1), c.semilla, c.sinTema, o, k)
        // El frente: al abrirse baja y se acerca un poco.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) drawRect(sombra(o, 0.35f * ab), Offset(f.left + 2f * u, f.bottom - 1f * u), Size(f.width, 1f * u + 5f * u * ab))
        frente(f, c.semilla, o, k, dia)
        if (arriba < 0.5f) drawRect(sombra(o, 0.3f), Offset.Zero, Size(w, 1.6f * u))
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        val yT = h * 0.79f + baja

        if (c.vacio) {
            // Una gaveta sin tema: el tarjetero vacío, el tirador y el precinto cruzado.
            tarjetero(r, null, o, k)
            concha(w / 2, yT, w * 0.2f, h * 0.09f, o, filo)
            tiraPrecinto(Offset(w * 0.04f, h * 0.12f + baja), Offset(w * 0.96f, h * 0.9f + baja), h * 0.12f, c.semilla, o, k)
            tiraPrecinto(Offset(w * 0.04f, h * 0.88f + baja), Offset(w * 0.96f, h * 0.14f + baja), h * 0.12f, c.semilla + 3, o, k)
            return
        }

        if (c.sinTema) {
            // Sin tema: el tarjetero vacío y, encima, un papel cuadriculado pegado con cinta, escrito a lápiz.
            tarjetero(r, null, o, k)
            val pr = Rect(r.left - w * 0.012f, r.top - h * 0.03f, r.right + w * 0.008f, r.bottom + h * 0.035f)
            val papel = Path().apply {
                moveTo(pr.left + w * 0.006f, pr.top); lineTo(pr.right, pr.top); lineTo(pr.right, pr.bottom); lineTo(pr.left + w * 0.006f, pr.bottom)
                for (j in 1..9) lineTo(pr.left + (if (j % 2 == 0) w * 0.006f else 0f), pr.bottom - pr.height * j / 10f)
                close()
            }
            translate(1.4f * u, 2f * u) { drawPath(papel, sombra(o, 0.35f)) }
            drawPath(papel, tarjeta(o, k))
            clipPath(papel) {
                var x = pr.left + w * 0.03f
                while (x < pr.right) { drawLine(n(ACERO, o, k).copy(alpha = 0.22f), Offset(x, pr.top), Offset(x, pr.bottom), 0.6f * u); x += w * 0.03f }
                var y = pr.top + w * 0.02f
                while (y < pr.bottom) { drawLine(n(ACERO, o, k).copy(alpha = 0.22f), Offset(pr.left, y), Offset(pr.right, y), 0.6f * u); y += w * 0.03f }
            }
            listOf(-1f, 1f).forEach { s ->
                val p = Offset(if (s < 0) pr.left + w * 0.05f else pr.right - w * 0.05f, pr.top)
                rotate(s * 22f, p) { drawRect(n(CARPETA, o, k).copy(alpha = 0.6f), Offset(p.x - w * 0.05f, p.y - h * 0.035f), Size(w * 0.1f, h * 0.07f)) }
            }
            concha(w / 2, yT, w * 0.2f, h * 0.09f, o, filo)
            return
        }

        // Las tres clases de gaveta.
        when (Math.floorMod(c.semilla / 3, 3)) {
            0 -> {
                // El tirador de barra y la franja roja de «clasificado» en la esquina de abajo a la derecha.
                tiradorBarra(w / 2, yT, w * 0.26f, o, filo)
                clipRect(f.left, f.top, f.right, f.bottom) {
                    val pv = Offset(f.right - w * 0.1f, f.bottom - h * 0.17f)
                    rotate(-32f, pv) {
                        val banda = Rect(pv.x - w * 0.2f, pv.y - h * 0.055f, pv.x + w * 0.25f, pv.y + h * 0.055f)
                        drawRect(sombra(o, 0.3f), Offset(banda.left + 1f * u, banda.top + 1.6f * u), banda.size)
                        drawRect(n(CARTEL, o, k), banda.topLeft, banda.size)
                        drawRect(n(HUESO, o, k).copy(alpha = 0.8f), Offset(banda.left, banda.top + banda.height * 0.14f), Size(banda.width, 0.8f * u))
                        drawRect(n(HUESO, o, k).copy(alpha = 0.8f), Offset(banda.left, banda.bottom - banda.height * 0.14f - 0.8f * u), Size(banda.width, 0.8f * u))
                        for (j in 0 until 4) drawRect(n(HUESO, o, k).copy(alpha = 0.75f), Offset(pv.x - w * 0.08f + j * w * 0.045f, pv.y - banda.height * 0.14f), Size(w * 0.03f, banda.height * 0.28f))
                        drawRect(brillo(o, 0.3f, k), banda.topLeft, Size(banda.width, 0.8f * u))
                    }
                }
            }
            1 -> {
                // El tirador de concha, el pasador con su candado y el precinto de plomo con su cordel.
                concha(w * 0.4f, yT, w * 0.2f, h * 0.09f, o, filo)
                val px = w * 0.8f
                drawRect(sombra(o, 0.4f), Offset(px - w * 0.03f + 1.4f * u, h * 0.62f + baja + 1.8f * u), Size(w * 0.06f, h * 0.12f))
                drawRect(n(HIERRO, o, filo), Offset(px - w * 0.03f, h * 0.62f + baja), Size(w * 0.06f, h * 0.12f))
                drawRect(brillo(o, 0.5f, filo), Offset(px - w * 0.03f, h * 0.62f + baja), Size(w * 0.06f, 0.9f * u))
                val argolla = Offset(px, h * 0.7f + baja)
                drawArc(sombra(o, 0.4f), 180f, 180f, false, Offset(argolla.x - w * 0.026f + 1.4f * u, argolla.y - h * 0.06f + 1.8f * u), Size(w * 0.052f, h * 0.12f), style = Stroke(3f * u))
                drawArc(n(ACERO_LUZ, o, filo), 180f, 180f, false, Offset(argolla.x - w * 0.026f, argolla.y - h * 0.06f), Size(w * 0.052f, h * 0.12f), style = Stroke(3f * u))
                drawArc(brillo(o, 0.7f, filo), 190f, 70f, false, Offset(argolla.x - w * 0.026f, argolla.y - h * 0.06f), Size(w * 0.052f, h * 0.12f), style = Stroke(1f * u))
                val cuerpo = Rect(argolla.x - w * 0.042f, argolla.y, argolla.x + w * 0.042f, argolla.y + h * 0.2f)
                drawRoundRect(sombra(o, 0.45f), Offset(cuerpo.left + 1.6f * u, cuerpo.top + 2f * u), cuerpo.size, CornerRadius(2.4f * u))
                drawRoundRect(n(mezcla(ACERO_LUZ, CARPETA, 0.35f), o, filo), cuerpo.topLeft, cuerpo.size, CornerRadius(2.4f * u))
                drawRect(brillo(o, 0.6f, filo), Offset(cuerpo.left + 1.4f * u, cuerpo.top + 1.4f * u), Size(cuerpo.width * 0.22f, cuerpo.height - 2.8f * u))
                drawRect(sombra(o, 0.25f), Offset(cuerpo.right - cuerpo.width * 0.25f, cuerpo.top), Size(cuerpo.width * 0.25f, cuerpo.height))
                drawCircle(n(BAQUELITA, o, 0f), 1.4f * u, Offset(cuerpo.center.x, cuerpo.center.y))
                drawRect(n(BAQUELITA, o, 0f), Offset(cuerpo.center.x - 0.5f * u, cuerpo.center.y), Size(1f * u, cuerpo.height * 0.25f))
                // El cordel, que pasa por la argolla y cuelga con el plomo.
                val plomo = Offset(argolla.x - w * 0.075f, h * 0.9f + baja)
                drawPath(Path().apply { moveTo(argolla.x - w * 0.01f, argolla.y - h * 0.02f); cubicTo(argolla.x - w * 0.04f, argolla.y, plomo.x + w * 0.01f, plomo.y - h * 0.12f, plomo.x, plomo.y) },
                    n(mezcla(CARPETA, HUESO, 0.4f), o, k), style = Stroke(0.9f * u))
                drawPath(Path().apply { moveTo(argolla.x + w * 0.004f, argolla.y - h * 0.02f); cubicTo(argolla.x - w * 0.02f, argolla.y + h * 0.05f, plomo.x + w * 0.02f, plomo.y - h * 0.08f, plomo.x + w * 0.004f, plomo.y) },
                    n(mezcla(CARPETA, HUESO, 0.4f), o, k), style = Stroke(0.9f * u))
                drawCircle(sombra(o, 0.4f), h * 0.045f, plomo + Offset(1.4f * u, 1.8f * u))
                drawCircle(n(mezcla(HIERRO, ACERO_LUZ, 0.5f), o, filo), h * 0.045f, plomo)
                drawCircle(sombra(o, 0.25f), h * 0.025f, plomo, style = Stroke(0.8f * u))
                drawArc(brillo(o, 0.6f, filo), 190f, 80f, false, plomo - Offset(h * 0.045f, h * 0.045f), Size(h * 0.09f, h * 0.09f), style = Stroke(0.9f * u))
            }
            else -> {
                // La gaveta doble de fichero: dos frentes chicos con su concha, y una hoja pillada en la rendija.
                val yd = h * 0.63f + baja
                drawRect(sombra(o, 0.55f), Offset(f.left + w * 0.035f, yd), Size(f.width - w * 0.07f, 1.6f * u))
                drawRect(brillo(o, 0.45f, k), Offset(f.left + w * 0.035f, yd + 1.6f * u), Size(f.width - w * 0.07f, 0.9f * u))
                drawRect(sombra(o, 0.55f), Offset(w * 0.5f - 0.8f * u, yd), Size(1.6f * u, f.bottom - yd - h * 0.09f))
                drawRect(brillo(o, 0.35f, k), Offset(w * 0.5f + 0.8f * u, yd), Size(0.8f * u, f.bottom - yd - h * 0.09f))
                concha(w * 0.27f, h * 0.8f + baja, w * 0.15f, h * 0.075f, o, filo)
                concha(w * 0.73f, h * 0.8f + baja, w * 0.15f, h * 0.075f, o, filo)
                val hoja = pl(w * 0.07f, yd, w * 0.19f, yd, w * 0.165f, yd + h * 0.14f, w * 0.095f, yd + h * 0.11f)
                translate(1.2f * u, 1.8f * u) { drawPath(hoja, sombra(o, 0.35f)) }
                drawPath(hoja, n(HUESO, o, k))
                clipPath(hoja) {
                    for (j in 0 until 3) drawLine(n(BAQUELITA, o, k).copy(alpha = 0.45f), Offset(w * 0.08f, yd + h * (0.035f + 0.03f * j)), Offset(w * 0.17f, yd + h * (0.03f + 0.03f * j)), 0.8f * u)
                }
            }
        }
        tarjetero(r, tarjeta(o, k), o, k)
        // Al abrirla, sale un poco de la luz que había dentro.
        if (ab > 0f) mancha(Offset(w / 2, arriba), w * 0.4f, if (o) FLEXO.copy(alpha = 0.14f * ab * (0.3f + k)) else LUZ_TUBO.copy(alpha = 0.3f * ab))
    }

    override val rotulo = Rect(0.1f, 0.2f, 0.9f, 0.57f)
    override fun tintaRotulo(oscuro: Boolean) = BAQUELITA
    override val letraRotulo: FontFamily = LetraExtra.oswald
    override val salida = SALIDA
    override val placa = Rect(0.33f, 0.14f, 0.67f, 0.44f)
    override fun tintaPlaca(oscuro: Boolean) = BAQUELITA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    /** El ojo: una almendra blanca con su iris y su pupila. */
    private fun DrawScope.ojo(c: Offset, a: Float, iris: Color, contorno: Color) {
        val almendra = Path().apply { moveTo(c.x - a, c.y); quadraticTo(c.x, c.y - a * 0.9f, c.x + a, c.y); quadraticTo(c.x, c.y + a * 0.9f, c.x - a, c.y); close() }
        drawPath(almendra, Color.White)
        clipPath(almendra) { drawCircle(iris, a * 0.38f, c); drawCircle(NEGRO, a * 0.18f, c) }
        drawPath(almendra, contorno, style = Stroke(a * 0.08f))
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de la gaveta de acero, con los rieles de las carpetas colgantes a los lados.
        drawRect(t(GRIS_OSC, oscuro))
        for (k in 0 until 40) drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, h * azar(71, k)), Offset(w, h * azar(71, k) + 1 * u), 1f * u)
        listOf(20 * u, w - 20 * u).forEach { x ->
            drawLine(t(GRIS_LUZ, oscuro), Offset(x, 0f), Offset(x, h), 3 * u)
            var y = 30 * u
            while (y < h) { drawRect(t(GRIS_LUZ, oscuro), Offset(x - 6 * u, y), Size(12 * u, 2.5f * u)); y += 46 * u }
        }
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(GRIS, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = PAPEL
    override val pared: Dp = 28.dp
    override val columnas = 2
    override val lineas = 5

    /** La carpeta de manila con su pestaña arriba a la izquierda. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val t = minOf(s.width, s.height) * 0.08f
        moveTo(0f, t); lineTo(s.width * 0.06f, t); lineTo(s.width * 0.09f, 0f); lineTo(s.width * 0.42f, 0f); lineTo(s.width * 0.45f, t)
        lineTo(s.width, t); lineTo(s.width, s.height); lineTo(0f, s.height); close()
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        val t = minOf(w, h) * 0.08f
        drawRect(MANILA.copy(alpha = 0.4f))
        // La etiqueta censurada en la pestaña, el sello rojo tenue y un clip.
        drawRect(NEGRO.copy(alpha = 0.8f), Offset(w * 0.13f, t * 0.3f), Size(w * 0.24f, t * 0.45f))
        val sc = Offset(w * 0.78f, h * 0.62f)
        rotate(-15f, sc) {
            drawCircle(ROJO.copy(alpha = 0.16f), minOf(w, h) * 0.16f, sc, style = Stroke(2 * u * g))
            drawCircle(ROJO.copy(alpha = 0.12f), minOf(w, h) * 0.12f, sc, style = Stroke(1 * u * g))
        }
        drawRoundRect(GRIS.copy(alpha = 0.8f), Offset(w - 16 * u * g, t - 4 * u * g), Size(6 * u * g, 16 * u * g), CornerRadius(3 * u * g), style = Stroke(1.2f * u * g))
    }

    override val margenes = MargenesFicha(16.dp, 13.dp, 12.dp, 30.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.74f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello lleno: verificado.
            "hecho" -> selloHecho(ROJO, p.color)
            // El sello de «aprobado».
            "convence" -> {
                drawCircle(ROJO, s * 0.42f, c, style = Stroke(s * 0.08f))
                drawPath(Path().apply { moveTo(c.x - s * 0.22f, c.y); lineTo(c.x - s * 0.05f, c.y + s * 0.17f); lineTo(c.x + s * 0.24f, c.y - s * 0.18f) }, ROJO, style = Stroke(s * 0.1f))
            }
            "piensa" -> ojo(c, s * 0.46f, GRIS, NEGRO)
            "duda" -> circuloDuda(p.tenue)
            // La barra negra de la censura.
            "desacuerdo" -> drawRect(NEGRO, Offset(c.x - s * 0.44f, c.y - s * 0.14f), Size(s * 0.88f, s * 0.28f))
            else -> {}
        }
    }

    /** Una grapa. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y + s * 0.2f); lineTo(c.x - s * 0.36f, c.y - s * 0.06f); lineTo(c.x + s * 0.36f, c.y - s * 0.06f); lineTo(c.x + s * 0.36f, c.y + s * 0.2f) }, GRIS, style = Stroke(s * 0.1f))
    }

    override val desorden = 1f
}
