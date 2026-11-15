package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Frente exterior: Vida en la sala de mando escondida de la red, una sala de control de ciencia ficción de los años 70 hecha
 * a mano: consolas curvas de fibra color hueso con sus cantos redondeados, paneles de gris cálido, filas de interruptores de
 * palanca, pilotos ámbar y rojos con su bisel de cromo, perillas de baquelita con su escala, pantallas redondas verdes con la
 * retícula y el barrido del radar, visores de segmentos ámbar, cartuchos de datos con su etiqueta y su muesca, y el vinilo
 * acolchado de los apoyabrazos. La luz viene de arriba a la izquierda y la sombra tiene color (petróleo hondo, no negro).
 * De noche (modo oscuro) es la misma sala con las luces bajas: la fibra y el gris se hunden hacia la pizarra y el petróleo, y
 * solo alumbran las pantallas, los pilotos y los visores, cada uno con su halo.
 * - Social: el rincón de reunión de la célula. La pared de paneles moldeados bajo el plafón con sus tiras de luz; el reloj
 *   doble (la hora de la capital y la local) sobre el tablero de trazado de vidrio esmerilado, en su marco grueso de fibra (el
 *   grafo va en el vidrio); a la izquierda, la torre del radar con sus interruptores, perillas y pilotos; a la derecha, la
 *   torre del visor de la ventana de transmisión, el estante de cartuchos y el botón rojo con su guarda; abajo, la consola
 *   curva con sus mandos y el apoyabrazos acolchado, y dos sillas giratorias de concha vistas de espaldas.
 * - Mapas: la mesa de trazado vista desde arriba. El tablero de fibra de esquinas redondas con la carta impresa en su pozo
 *   (el nicho) sujeta por pinzas; arriba, la franja del visor de segmentos y los pilotos; a la izquierda, la pantalla de radar,
 *   la perilla grande y los interruptores; a la derecha, la radio de maleta abierta (la tapa con su tabla de frecuencias, el
 *   dial ámbar, el altavoz, las perillas y el auricular); abajo, los pulsadores iluminados, los cartuchos sueltos, el lápiz
 *   graso y el cerco de una taza.
 * - Otras vidas: el armario de equipo de la célula. La cabecera con su visor de segmentos, el emblema de la red en su escudo
 *   de fibra y la pantallita de radar; cinco bahías iguales de bordes redondos (el nicho) con su piloto encima y su etiqueta
 *   debajo; a los lados, los interruptores, la perilla, la cerradura con la llave de cifrado y la columna de pilotos; abajo,
 *   el zócalo con sus ranuras.
 * - Íconos: la capital del Directorio (mármol y cúpula de bronce) con la nave patrulla de anillo encima; la pantalla de radar
 *   con la película plateada que raspa una ficha de la red; la carta estelar impresa con tres pilotos unidos por la ruta de
 *   salto.
 * - Notas: el tablero magnético de la célula en su marco de fibra, bajo la cabecera de pilotos, el emblema y el visor de la
 *   ventana; abajo, la repisa con cartuchos, el bote de microfilm y el lápiz graso. Las notas son la tira del teletipo, la
 *   ficha de la célula, la orden de misión y el despacho urgente; se sujetan con un imán de baquelita y, si están fijadas, con
 *   un imán rojo y una cinta ámbar.
 */
internal object VidaFrente : DisenoVida {
    private val HUESO = Color(0xFFE7DFCB)
    private val HUESO_LUZ = Color(0xFFF6F1E4)
    private val HUESO_OSC = Color(0xFFC2B79F)
    private val PARED = Color(0xFFD5CDBB)
    private val GRIS = Color(0xFF8C857A)
    private val GRIS_CLARO = Color(0xFFB4AC9E)
    private val GRIS_OSC = Color(0xFF5B564E)
    private val PIZARRA = Color(0xFF2E3339)
    private val BAQUELITA = TrazosFrente.BAQUELITA
    private val CROMO = TrazosFrente.CROMO
    private val CROMO_LUZ = Color(0xFFE4E2DC)
    private val PANTALLA = TrazosFrente.PANTALLA
    private val RADAR = TrazosFrente.RADAR
    private val AMBAR = TrazosFrente.PILOTO_AMBAR
    private val ROJO = TrazosFrente.PILOTO_ROJO
    private val PAPEL = Color(0xFFEFE8D5)
    private val TELETIPO = Color(0xFFF2E7C2)
    private val SALVIA = Color(0xFFDFE5D2)
    private val DESPACHO = Color(0xFFF6F1E8)
    private val VIDRIO = Color(0xFFDCE2D2)
    private val IMAN = Color(0xFFCAC6BB)
    private val VINILO = Color(0xFF3B3430)
    private val TAPIZ = Color(0xFF9A4F24)
    private val MALETA = Color(0xFF4B4A3F)
    private val PLASTICO = Color(0xFF3A3733)
    private val MARMOL = TrazosFrente.MARMOL
    private val BRONCE = TrazosFrente.BRONCE
    private val NOCHE = Color(0xFF0A1417)
    private val SOMBRA = Color(0xFF16222A)
    private val LUZ = Color(0xFFFFF3DC)
    private val TINTA = Color(0xFF23252A)

    /** La unidad de dibujo: la centésima parte del lado menor del lienzo. */
    private fun DrawScope.u() = size.minDimension / 100f

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en oscuro: la misma sala con las luces bajas, hundida hacia la pizarra petróleo. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.64f) else c

    /** El papel se hunde menos: queda en penumbra, pero la tinta se lee encima. */
    private fun papel(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.3f) else c

    /** La sombra con color: petróleo hondo en lugar de negro. */
    private fun som(a: Float): Color = SOMBRA.copy(alpha = a.coerceIn(0f, 1f))

    /** La luz que da en lo que mira arriba a la izquierda: crema cálida, no blanco. */
    private fun luz(a: Float): Color = LUZ.copy(alpha = a.coerceIn(0f, 1f))

    /** El fondo del nicho: el material apenas mezclado con el fondo del esquema, para que se lea lo de encima. */
    private fun nicho(fondo: Color, material: Color, oscuro: Boolean): Color = mezcla(fondo, material, if (oscuro) 0.16f else 0.26f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.215f, 0.17f, 0.785f, 0.70f)
        MuebleVida.MAPAS -> Rect(0.20f, 0.155f, 0.80f, 0.80f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.065f, 0.31f, 0.935f, 0.83f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.16f, 0.925f, 0.875f)
    }

    // ---------------------------------------------------------------- Ayudas de dibujo

    private fun redondo(r: Rect, radio: Float): Path = Path().apply { addRoundRect(RoundRect(r, CornerRadius(radio))) }

    /** El halo de una luz encendida: cinco discos cada vez más chicos que se suman hacia el centro, sin degradado. */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color, fuerza: Float) {
        for (k in 0 until 5) drawCircle(color.copy(alpha = (0.05f + 0.035f * k) * fuerza), r * (1f - k * 0.17f), c)
    }

    /** La sombra blanda de una pieza sobre lo que tiene detrás, corrida abajo a la derecha. */
    private fun DrawScope.sombraPieza(r: Rect, radio: Float, alcance: Float, fuerza: Float) {
        for (k in 3 downTo 1) {
            val e = alcance * k / 3f
            drawRoundRect(som(fuerza / 3f), Offset(r.left + e * 0.35f, r.top + e * 0.55f), Size(r.width + e * 0.3f, r.height + e * 0.2f), CornerRadius(radio + e))
        }
    }

    /**
     * Una pieza de fibra moldeada: el cuerpo, el canto que mira a la luz (arriba a la izquierda) en crema, el que queda en
     * sombra en petróleo y el filo de la pieza.
     */
    private fun DrawScope.fibra(r: Rect, radio: Float, base: Color, oscuro: Boolean, u: Float, degradado: Boolean = false) {
        if (degradado) drawRoundRect(Brush.verticalGradient(listOf(mezcla(base, HUESO_LUZ, if (oscuro) 0.08f else 0.4f), base, mezcla(base, SOMBRA, 0.14f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(radio))
        else drawRoundRect(base, r.topLeft, r.size, CornerRadius(radio))
        val g = 0.55f * u
        val dentro = Rect(r.left + g, r.top + g, r.right - g, r.bottom - g)
        clipRect(r.left, r.top, r.right, r.top + r.height * 0.45f) {
            drawRoundRect(luz(if (oscuro) 0.12f else 0.6f), dentro.topLeft, dentro.size, CornerRadius((radio - g).coerceAtLeast(0f)), style = Stroke(0.7f * u))
        }
        clipRect(r.left, r.top + r.height * 0.55f, r.right, r.bottom) {
            drawRoundRect(som(if (oscuro) 0.5f else 0.24f), dentro.topLeft, dentro.size, CornerRadius((radio - g).coerceAtLeast(0f)), style = Stroke(0.8f * u))
        }
        drawRoundRect(som(if (oscuro) 0.6f else 0.38f), r.topLeft, r.size, CornerRadius(radio), style = Stroke(0.28f * u))
    }

    /**
     * Un hueco en la fibra: el labio de luz abajo a la derecha, la sombra que el canto de arriba y el de la izquierda echan
     * hacia dentro y el fondo [fondo].
     */
    private fun DrawScope.hundido(r: Rect, radio: Float, fondo: Color, oscuro: Boolean, u: Float, hondo: Float = 1f) {
        drawRoundRect(luz(if (oscuro) 0.1f else 0.55f), Offset(r.left + 0.45f * u, r.top + 0.45f * u), r.size, CornerRadius(radio))
        drawRoundRect(som(if (oscuro) 0.6f else 0.4f), Offset(r.left - 0.35f * u, r.top - 0.35f * u), r.size, CornerRadius(radio))
        drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(radio))
        clipPath(redondo(r, radio)) {
            val b = 1.3f * u * hondo
            drawRoundRect(som((if (oscuro) 0.55f else 0.3f) * hondo.coerceAtMost(1f)), Offset(r.left + b, r.top + b), Size(r.width + 4f * b, r.height + 4f * b), CornerRadius(radio + b), style = Stroke(2f * b))
        }
    }

    /** Un piloto de consola: el bisel de cromo, el aro de baquelita, la lente y, si está encendido, su halo y su brillo. */
    private fun DrawScope.piloto(c: Offset, r: Float, color: Color, encendido: Boolean, oscuro: Boolean) {
        if (encendido) halo(c, r * (if (oscuro) 4.2f else 2.6f), color, if (oscuro) 1f else 0.5f)
        drawCircle(som(0.4f), r * 1.42f, Offset(c.x + r * 0.2f, c.y + r * 0.3f))
        drawCircle(t(CROMO, oscuro), r * 1.42f, c)
        drawArc(luz(if (oscuro) 0.25f else 0.8f), 160f, 110f, false, Offset(c.x - r * 1.25f, c.y - r * 1.25f), Size(r * 2.5f, r * 2.5f), style = Stroke(r * 0.22f))
        drawCircle(BAQUELITA, r * 1.1f, c)
        if (encendido) {
            drawCircle(color, r, c)
            drawCircle(mezcla(color, Color.White, 0.5f), r * 0.55f, Offset(c.x - r * 0.12f, c.y - r * 0.12f))
        } else drawCircle(mezcla(color, BAQUELITA, if (oscuro) 0.72f else 0.55f), r, c)
        drawCircle(Color.White.copy(alpha = if (encendido) 0.75f else 0.35f), r * 0.24f, Offset(c.x - r * 0.38f, c.y - r * 0.38f))
    }

    /**
     * Un interruptor de palanca visto de frente: la placa de cromo, la tuerca y la palanca, arriba o abajo. Con [punta], la
     * palanca lleva su capuchón de color (de baquelita roja o ámbar); si no, es de cromo con su bolita.
     */
    private fun DrawScope.interruptor(c: Offset, a: Float, arriba: Boolean, oscuro: Boolean, punta: Color? = null) {
        val placa = Rect(c.x - a * 0.3f, c.y - a * 0.44f, c.x + a * 0.3f, c.y + a * 0.44f)
        drawRoundRect(som(0.35f), Offset(placa.left + a * 0.05f, placa.top + a * 0.07f), placa.size, CornerRadius(a * 0.1f))
        drawRoundRect(t(CROMO, oscuro), placa.topLeft, placa.size, CornerRadius(a * 0.1f))
        drawLine(luz(if (oscuro) 0.2f else 0.7f), Offset(placa.left + a * 0.06f, placa.top + a * 0.05f), Offset(placa.right - a * 0.06f, placa.top + a * 0.05f), a * 0.05f)
        drawCircle(t(GRIS_OSC, oscuro), a * 0.04f, Offset(c.x, placa.top + a * 0.1f))
        drawCircle(t(GRIS_OSC, oscuro), a * 0.04f, Offset(c.x, placa.bottom - a * 0.1f))
        drawCircle(t(GRIS_OSC, oscuro), a * 0.2f, c)
        drawCircle(t(CROMO_LUZ, oscuro), a * 0.16f, c)
        val s = if (arriba) -1f else 1f
        val tip = Offset(c.x + a * 0.04f, c.y + s * a * 0.62f)
        drawLine(som(0.35f), Offset(c.x + a * 0.08f, c.y + a * 0.06f), Offset(tip.x + a * 0.1f, tip.y + a * 0.1f), a * 0.14f, StrokeCap.Round)
        drawLine(t(CROMO, oscuro), c, tip, a * 0.13f, StrokeCap.Round)
        drawLine(luz(if (oscuro) 0.2f else 0.75f), Offset(c.x - a * 0.025f, c.y), Offset(tip.x - a * 0.025f, tip.y), a * 0.04f, StrokeCap.Round)
        if (punta != null) {
            val cap = Rect(tip.x - a * 0.11f, tip.y - a * 0.16f, tip.x + a * 0.11f, tip.y + a * 0.16f)
            drawRoundRect(t(punta, oscuro), cap.topLeft, cap.size, CornerRadius(a * 0.1f))
            drawLine(luz(if (oscuro) 0.2f else 0.6f), Offset(cap.left + a * 0.05f, cap.top + a * 0.05f), Offset(cap.left + a * 0.05f, cap.bottom - a * 0.06f), a * 0.04f, StrokeCap.Round)
        } else {
            drawCircle(t(CROMO_LUZ, oscuro), a * 0.1f, tip)
            drawCircle(luz(if (oscuro) 0.25f else 0.85f), a * 0.035f, Offset(tip.x - a * 0.03f, tip.y - a * 0.03f))
        }
    }

    /** Una fila de [n] interruptores entre [x0] y [x1] con su tira de rótulo grabado encima; las posiciones salen de [semilla]. */
    private fun DrawScope.filaInterruptores(x0: Float, x1: Float, y: Float, n: Int, a: Float, oscuro: Boolean, semilla: Int, rotulos: Boolean = true) {
        val paso = (x1 - x0) / n
        for (k in 0 until n) {
            val x = x0 + paso * (k + 0.5f)
            if (rotulos) {
                drawRoundRect(t(PAPEL, oscuro), Offset(x - a * 0.28f, y - a * 1.02f), Size(a * 0.56f, a * 0.18f), CornerRadius(a * 0.04f))
                drawLine(t(TINTA, oscuro).copy(alpha = 0.6f), Offset(x - a * 0.18f, y - a * 0.93f), Offset(x + a * 0.12f, y - a * 0.93f), a * 0.05f)
            }
            val arriba = (semilla * 7 + k * 13) % 3 != 0
            interruptor(Offset(x, y), a, arriba, oscuro, if ((semilla + k) % 5 == 2) ROJO else null)
        }
    }

    /** Una perilla de baquelita con su falda moleteada, la escala grabada alrededor y la raya que marca [giro] grados. */
    private fun DrawScope.perilla(c: Offset, r: Float, giro: Float, oscuro: Boolean, escala: Color) {
        for (k in 0..10) {
            val a = Math.toRadians(135.0 + k * 27.0)
            val l = if (k % 5 == 0) 0.5f else 0.28f
            drawLine(escala, Offset(c.x + r * 1.22f * cos(a).toFloat(), c.y + r * 1.22f * sin(a).toFloat()),
                Offset(c.x + r * (1.22f + l) * cos(a).toFloat(), c.y + r * (1.22f + l) * sin(a).toFloat()), r * 0.09f, StrokeCap.Round)
        }
        drawCircle(som(0.45f), r, Offset(c.x + r * 0.16f, c.y + r * 0.24f))
        drawCircle(BAQUELITA, r, c)
        for (k in 0 until 20) {
            val a = Math.toRadians(k * 18.0)
            drawLine(luz(if (oscuro) 0.06f else 0.16f), Offset(c.x + r * 0.84f * cos(a).toFloat(), c.y + r * 0.84f * sin(a).toFloat()),
                Offset(c.x + r * 0.98f * cos(a).toFloat(), c.y + r * 0.98f * sin(a).toFloat()), r * 0.06f)
        }
        drawCircle(mezcla(BAQUELITA, Color.White, 0.1f), r * 0.72f, c)
        drawArc(luz(if (oscuro) 0.14f else 0.4f), 190f, 80f, false, Offset(c.x - r * 0.6f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.2f), style = Stroke(r * 0.12f, cap = StrokeCap.Round))
        rotate(giro, c) {
            drawLine(if (oscuro) AMBAR else t(HUESO_LUZ, false), Offset(c.x, c.y - r * 0.18f), Offset(c.x, c.y - r * 0.68f), r * 0.14f, StrokeCap.Round)
        }
    }

    /**
     * La pantalla redonda de radar en su bisel de fibra: el aro de goma, el vidrio verde casi negro con su resplandor, la
     * retícula, la estela del barrido que apunta a [angulo] grados, los ecos y el reflejo del vidrio. De noche alumbra.
     */
    private fun DrawScope.radar(c: Offset, r: Float, angulo: Float, oscuro: Boolean, semilla: Int, u: Float, bisel: Color = HUESO) {
        val rb = r * 1.3f
        if (oscuro) halo(c, r * 2.3f, RADAR, 0.55f)
        drawCircle(som(0.4f), rb, Offset(c.x + 0.35f * u + r * 0.03f, c.y + 0.55f * u + r * 0.05f))
        drawCircle(t(bisel, oscuro), rb, c)
        drawArc(luz(if (oscuro) 0.14f else 0.7f), 150f, 130f, false, Offset(c.x - rb * 0.92f, c.y - rb * 0.92f), Size(rb * 1.84f, rb * 1.84f), style = Stroke(rb * 0.07f, cap = StrokeCap.Round))
        drawArc(som(if (oscuro) 0.5f else 0.28f), -30f, 120f, false, Offset(c.x - rb * 0.92f, c.y - rb * 0.92f), Size(rb * 1.84f, rb * 1.84f), style = Stroke(rb * 0.07f, cap = StrokeCap.Round))
        drawCircle(BAQUELITA, r * 1.08f, c)
        drawCircle(PANTALLA, r, c)
        val brillo = if (oscuro) 1f else 0.75f
        drawCircle(Color(0xFF16271F).copy(alpha = brillo), r * 0.8f, c)
        drawCircle(Color(0xFF1C3427).copy(alpha = brillo), r * 0.5f, c)
        with(TrazosFrente) { reticula(c, r * 0.92f, RADAR.copy(alpha = 0.4f), maxOf(r * 0.025f, 0.18f * u), 3) }
        // La estela del barrido: cuñas que se apilan y se encienden hacia la línea.
        val caja = Offset(c.x - r * 0.92f, c.y - r * 0.92f); val lado = Size(r * 1.84f, r * 1.84f)
        for (k in 0 until 6) drawArc(RADAR.copy(alpha = 0.055f * brillo), angulo - 66f + k * 11f, 66f - k * 11f, true, caja, lado)
        val a = Math.toRadians(angulo.toDouble())
        drawLine(RADAR.copy(alpha = 0.9f), c, Offset(c.x + r * 0.92f * cos(a).toFloat(), c.y + r * 0.92f * sin(a).toFloat()), maxOf(r * 0.04f, 0.25f * u), StrokeCap.Round)
        val rnd = Random(semilla * 31L + 5)
        for (k in 0 until 3) {
            val ang = Math.toRadians(angulo - 20.0 - rnd.nextInt(200)); val d = r * (0.3f + 0.55f * rnd.nextFloat())
            val p = Offset(c.x + d * cos(ang).toFloat(), c.y + d * sin(ang).toFloat())
            drawCircle(RADAR.copy(alpha = 0.3f), r * 0.09f, p)
            drawCircle(RADAR, r * 0.04f, p)
        }
        drawArc(Color.White.copy(alpha = if (oscuro) 0.12f else 0.22f), 200f, 55f, false, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.08f else 0.14f), r * 0.12f, Offset(c.x - r * 0.42f, c.y - r * 0.5f))
    }

    /** Los segmentos que se encienden en cada cifra (a arriba, b y c a la derecha, d abajo, e y f a la izquierda, g en medio). */
    private val SEGMENTOS = mapOf('0' to "abcdef", '1' to "bc", '2' to "abged", '3' to "abgcd", '4' to "fgbc", '5' to "afgcd",
        '6' to "afgedc", '7' to "abc", '8' to "abcdefg", '9' to "abcdfg")

    /**
     * Un visor de segmentos ámbar: el cristal ahumado en su marco y las [cifras] (y los dos puntos) con sus segmentos
     * apagados detrás. De noche las cifras echan su resplandor.
     */
    private fun DrawScope.visor(r: Rect, cifras: String, oscuro: Boolean, u: Float) {
        hundido(r, 0.8f * u, Color(0xFF1B120A), oscuro, u, 0.6f)
        val pad = r.height * 0.18f
        val anchos = cifras.map { if (it == ':') 0.45f else 1f }
        val cw = (r.width - pad * 2) / anchos.sum()
        val dh = r.height - pad * 2
        val th = minOf(cw * 0.17f, dh * 0.1f)
        var x = r.left + pad
        val y = r.top + pad
        fun seg(p0: Offset, p1: Offset, on: Boolean) {
            val inc = 0.12f
            val a = Offset(p0.x + (y + dh - p0.y) * inc, p0.y); val b = Offset(p1.x + (y + dh - p1.y) * inc, p1.y)
            if (on && oscuro) drawLine(AMBAR.copy(alpha = 0.3f), a, b, th * 2.4f, StrokeCap.Round)
            drawLine(if (on) AMBAR else AMBAR.copy(alpha = 0.09f), a, b, th, StrokeCap.Round)
        }
        cifras.forEachIndexed { i, ch ->
            val ancho = cw * anchos[i]
            if (ch == ':') {
                listOf(0.32f, 0.7f).forEach { f -> drawCircle(AMBAR, th * 0.6f, Offset(x + ancho * 0.5f + (1 - f) * dh * 0.12f, y + dh * f)) }
            } else {
                val on = SEGMENTOS[ch] ?: ""
                val dw = ancho * 0.66f; val x0 = x + ancho * 0.1f; val g = th * 0.75f
                seg(Offset(x0 + g, y), Offset(x0 + dw - g, y), 'a' in on)
                seg(Offset(x0 + dw, y + g), Offset(x0 + dw, y + dh / 2 - g), 'b' in on)
                seg(Offset(x0 + dw, y + dh / 2 + g), Offset(x0 + dw, y + dh - g), 'c' in on)
                seg(Offset(x0 + g, y + dh), Offset(x0 + dw - g, y + dh), 'd' in on)
                seg(Offset(x0, y + dh / 2 + g), Offset(x0, y + dh - g), 'e' in on)
                seg(Offset(x0, y + g), Offset(x0, y + dh / 2 - g), 'f' in on)
                seg(Offset(x0 + g, y + dh / 2), Offset(x0 + dw - g, y + dh / 2), 'g' in on)
            }
            x += ancho
        }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.12f), Offset(r.left + r.width * 0.06f, r.top + r.height * 0.16f), Offset(r.left + r.width * 0.5f, r.top + r.height * 0.16f), r.height * 0.06f, StrokeCap.Round)
    }

    /**
     * Un cartucho de datos (girado [giro] grados): el bloque de plástico con la esquina cortada de la muesca, la ventanita,
     * la etiqueta crema con su [franja] de color y sus renglones, la lengüeta de protección y las estrías de agarre.
     */
    private fun DrawScope.cartucho(c: Offset, an: Float, al: Float, giro: Float, franja: Color, oscuro: Boolean, u: Float) {
        rotate(giro, c) {
            val r = Rect(c.x - an / 2, c.y - al / 2, c.x + an / 2, c.y + al / 2)
            val m = an * 0.16f; val q = an * 0.05f
            val cuerpo = Path().apply {
                moveTo(r.left + q, r.top); lineTo(r.right - m, r.top); lineTo(r.right, r.top + m); lineTo(r.right, r.bottom - q)
                quadraticTo(r.right, r.bottom, r.right - q, r.bottom); lineTo(r.left + q, r.bottom); quadraticTo(r.left, r.bottom, r.left, r.bottom - q)
                lineTo(r.left, r.top + q); quadraticTo(r.left, r.top, r.left + q, r.top); close()
            }
            translate(0.45f * u, 0.7f * u) { drawPath(cuerpo, som(0.4f)) }
            drawPath(cuerpo, t(PLASTICO, oscuro))
            drawLine(luz(if (oscuro) 0.12f else 0.35f), Offset(r.left + q, r.top + al * 0.03f), Offset(r.right - m, r.top + al * 0.03f), al * 0.035f)
            drawLine(luz(if (oscuro) 0.08f else 0.22f), Offset(r.left + al * 0.03f, r.top + q), Offset(r.left + al * 0.03f, r.bottom - q), al * 0.03f)
            val et = Rect(r.left + an * 0.12f, r.top + al * 0.14f, r.right - an * 0.12f, r.top + al * 0.62f)
            drawRect(papel(PAPEL, oscuro), et.topLeft, et.size)
            drawRect(t(franja, oscuro), et.topLeft, Size(et.width, et.height * 0.24f))
            for (k in 0 until 2) drawLine(t(TINTA, oscuro).copy(alpha = 0.55f), Offset(et.left + et.width * 0.1f, et.top + et.height * (0.48f + k * 0.24f)), Offset(et.left + et.width * (if (k == 0) 0.82f else 0.55f), et.top + et.height * (0.48f + k * 0.24f)), al * 0.03f)
            val ve = Rect(r.left + an * 0.3f, r.top + al * 0.7f, r.right - an * 0.3f, r.top + al * 0.84f)
            drawRoundRect(t(Color(0xFF151413), oscuro), ve.topLeft, ve.size, CornerRadius(al * 0.03f))
            drawCircle(t(CROMO, oscuro), ve.height * 0.32f, Offset(ve.left + ve.width * 0.25f, ve.center.y))
            drawCircle(t(CROMO, oscuro), ve.height * 0.32f, Offset(ve.right - ve.width * 0.25f, ve.center.y))
            drawRect(t(AMBAR, oscuro), Offset(r.left + an * 0.05f, r.top + al * 0.03f), Size(an * 0.07f, al * 0.07f))
            for (k in 0 until 4) {
                val x = r.left + an * (0.14f + k * 0.04f)
                drawLine(som(0.4f), Offset(x, r.bottom - al * 0.1f), Offset(x, r.bottom - al * 0.03f), an * 0.012f)
            }
        }
    }

    /**
     * Una silla giratoria de concha vista de espaldas: el tapiz que asoma por el borde, la concha de fibra con su brillo a la
     * izquierda, el asiento, la columna de cromo con su fuelle y el arranque de las patas en estrella con sus ruedas. [base] es
     * donde la columna toca el piso; [e], el alto; [ladeo] (−1 a 1) gira la concha hacia un lado.
     */
    private fun DrawScope.silla(base: Offset, e: Float, ladeo: Float, oscuro: Boolean, u: Float) {
        val x = base.x
        val yS = base.y - e * 0.36f
        // Las patas en estrella y sus ruedas.
        drawOval(som(0.35f), Offset(x - e * 0.44f + e * 0.05f, base.y - e * 0.035f), Size(e * 0.95f, e * 0.09f))
        val cubo = Offset(x, base.y - e * 0.06f)
        listOf(Offset(-0.42f, 0.0f), Offset(0.42f, 0.0f), Offset(-0.17f, 0.04f), Offset(0.2f, 0.035f)).forEach { p ->
            val fin = Offset(x + p.x * e, base.y + p.y * e - e * 0.01f)
            drawLine(t(CROMO, oscuro), cubo, fin, e * 0.032f, StrokeCap.Round)
            drawLine(luz(if (oscuro) 0.15f else 0.6f), Offset(cubo.x, cubo.y - e * 0.008f), Offset(fin.x, fin.y - e * 0.008f), e * 0.008f)
            drawCircle(BAQUELITA, e * 0.028f, Offset(fin.x, fin.y + e * 0.022f))
        }
        // La columna de cromo y el fuelle negro.
        val col = Rect(x - e * 0.03f, yS, x + e * 0.03f, cubo.y)
        drawRect(t(CROMO, oscuro), col.topLeft, col.size)
        drawRect(luz(if (oscuro) 0.2f else 0.75f), Offset(col.left + col.width * 0.18f, col.top), Size(col.width * 0.22f, col.height))
        drawRect(som(0.35f), Offset(col.right - col.width * 0.28f, col.top), Size(col.width * 0.28f, col.height))
        val fu = Rect(x - e * 0.042f, yS + col.height * 0.45f, x + e * 0.042f, cubo.y - e * 0.01f)
        drawRoundRect(BAQUELITA, fu.topLeft, fu.size, CornerRadius(e * 0.01f))
        var fy = fu.top + e * 0.012f
        while (fy < fu.bottom - e * 0.004f) { drawLine(luz(if (oscuro) 0.05f else 0.14f), Offset(fu.left, fy), Offset(fu.right, fy), e * 0.006f); fy += e * 0.016f }
        // El asiento visto por detrás: el canto del cojín sobre la concha.
        val asiento = Rect(x - e * 0.33f, yS - e * 0.05f, x + e * 0.33f, yS + e * 0.035f)
        drawOval(t(HUESO_OSC, oscuro), asiento.topLeft, asiento.size)
        drawOval(som(0.3f), Offset(asiento.left, asiento.center.y), Size(asiento.width, asiento.height * 0.5f))
        // Los brazos acolchados que asoman a los lados del respaldo.
        val cx = x + ladeo * e * 0.04f
        val yBr = base.y - e * 0.5f
        listOf(-1f, 1f).forEach { lado ->
            val br = Rect(cx + lado * e * 0.2f - e * 0.17f, yBr - e * 0.045f, cx + lado * e * 0.2f + e * 0.17f, yBr + e * 0.045f)
            drawRoundRect(t(VINILO, oscuro), br.topLeft, br.size, CornerRadius(e * 0.045f))
            drawLine(luz(if (oscuro) 0.08f else 0.25f), Offset(br.left + e * 0.03f, br.top + e * 0.015f), Offset(br.right - e * 0.03f, br.top + e * 0.015f), e * 0.012f, StrokeCap.Round)
            drawRect(t(CROMO, oscuro), Offset(cx + lado * e * 0.3f - e * 0.012f, br.bottom), Size(e * 0.024f, yS - br.bottom))
        }
        // La concha del respaldo: ancha en los hombros, angosta en la cintura, con el tapiz que asoma por el borde.
        fun concha(k: Float, dx: Float): Path = Path().apply {
            val m = cx + dx
            val bot = yS - e * 0.03f; val top = base.y - e * (0.97f + 0.03f * (k - 1f) * 10f)
            val hombro = e * 0.31f * k; val cintura = e * 0.21f * k
            moveTo(m - cintura, bot)
            cubicTo(m - cintura - e * 0.04f, bot - e * 0.18f, m - hombro - e * 0.02f, top + e * 0.24f, m - hombro, top + e * 0.08f)
            cubicTo(m - hombro + e * 0.01f, top + e * 0.01f, m - hombro * 0.5f, top - e * 0.015f, m, top - e * 0.015f)
            cubicTo(m + hombro * 0.5f, top - e * 0.015f, m + hombro - e * 0.01f, top + e * 0.01f, m + hombro, top + e * 0.08f)
            cubicTo(m + hombro + e * 0.02f, top + e * 0.24f, m + cintura + e * 0.04f, bot - e * 0.18f, m + cintura, bot)
            quadraticTo(m, bot + e * 0.05f, m - cintura, bot)
            close()
        }
        val tapiz = concha(1.07f, ladeo * e * 0.03f)
        translate(e * 0.03f, e * 0.025f) { drawPath(tapiz, som(0.35f)) }
        drawPath(tapiz, t(TAPIZ, oscuro))
        val cuerpo = concha(1f, -ladeo * e * 0.012f)
        drawPath(cuerpo, t(HUESO, oscuro))
        clipPath(cuerpo) {
            // El lado de la sombra a la derecha, el brillo largo de la izquierda y la costura del molde.
            drawOval(som(if (oscuro) 0.4f else 0.22f), Offset(cx + e * 0.08f - ladeo * e * 0.04f, base.y - e * 1.02f), Size(e * 0.46f, e * 0.72f))
            drawOval(luz(if (oscuro) 0.08f else 0.55f), Offset(cx - e * 0.25f, base.y - e * 0.9f), Size(e * 0.1f, e * 0.34f))
            drawLine(som(0.22f), Offset(cx - e * 0.4f, base.y - e * 0.62f), Offset(cx + e * 0.4f, base.y - e * 0.62f), e * 0.008f)
            drawLine(luz(if (oscuro) 0.05f else 0.3f), Offset(cx - e * 0.4f, base.y - e * 0.615f), Offset(cx + e * 0.4f, base.y - e * 0.615f), e * 0.006f)
        }
        drawPath(cuerpo, som(if (oscuro) 0.55f else 0.35f), style = Stroke(0.22f * u))
    }

    /** Una pinza de cromo que muerde el borde de la carta, vista desde arriba. */
    private fun DrawScope.pinza(c: Offset, a: Float, giro: Float, oscuro: Boolean) {
        rotate(giro, c) {
            val r = Rect(c.x - a * 0.5f, c.y - a * 0.3f, c.x + a * 0.5f, c.y + a * 0.3f)
            drawRoundRect(som(0.35f), Offset(r.left + a * 0.08f, r.top + a * 0.12f), r.size, CornerRadius(a * 0.12f))
            drawRoundRect(t(CROMO, oscuro), r.topLeft, r.size, CornerRadius(a * 0.12f))
            drawLine(luz(if (oscuro) 0.2f else 0.8f), Offset(r.left + a * 0.1f, r.top + a * 0.1f), Offset(r.right - a * 0.1f, r.top + a * 0.1f), a * 0.07f, StrokeCap.Round)
            drawLine(som(0.4f), Offset(r.left + a * 0.15f, c.y + a * 0.06f), Offset(r.right - a * 0.15f, c.y + a * 0.06f), a * 0.05f)
            drawCircle(t(GRIS_OSC, oscuro), a * 0.08f, Offset(r.right - a * 0.18f, c.y - a * 0.08f))
        }
    }

    /** El lápiz graso rojo de las consolas: el papel enrollado, el cordón para pelarlo y la punta de cera. */
    private fun DrawScope.lapizGraso(p0: Offset, p1: Offset, grosor: Float, oscuro: Boolean) {
        drawLine(som(0.35f), Offset(p0.x + grosor * 0.4f, p0.y + grosor * 0.6f), Offset(p1.x + grosor * 0.4f, p1.y + grosor * 0.6f), grosor, StrokeCap.Round)
        val dx = p1.x - p0.x; val dy = p1.y - p0.y
        val punta = Offset(p0.x + dx * 0.1f, p0.y + dy * 0.1f)
        drawLine(t(Color(0xFFB8352A), oscuro), p0, punta, grosor * 0.7f, StrokeCap.Round)
        drawLine(t(Color(0xFFD9CBB0), oscuro), punta, p1, grosor, StrokeCap.Butt)
        for (k in 1 until 9) {
            val f = 0.1f + k * 0.1f
            val q = Offset(p0.x + dx * f, p0.y + dy * f)
            drawLine(som(0.3f), Offset(q.x - dy / 60f - grosor * 0.2f, q.y - grosor * 0.5f), Offset(q.x + grosor * 0.2f, q.y + grosor * 0.5f), grosor * 0.12f)
        }
        drawLine(t(Color(0xFFB8352A), oscuro).copy(alpha = 0.85f), Offset(p0.x + dx * 0.88f, p0.y + dy * 0.88f), p1, grosor, StrokeCap.Butt)
        drawLine(luz(if (oscuro) 0.12f else 0.4f), Offset(punta.x, punta.y - grosor * 0.25f), Offset(p1.x, p1.y - grosor * 0.25f), grosor * 0.18f)
    }

    // ---------------------------------------------------------------- Social: el rincón de reunión de la célula

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.SOCIAL))

        // La pared de paneles moldeados, cada uno en relieve con su canto de luz y su junta de sombra.
        drawRect(c(PARED))
        val pw = w / 7f
        for (i in 0 until 7) {
            val r = Rect(i * pw + 0.7f * u, 7.5f * u, (i + 1) * pw - 0.7f * u, 78f * u)
            drawRoundRect(som(if (oscuro) 0.5f else 0.24f), Offset(r.left + 0.5f * u, r.top + 0.7f * u), r.size, CornerRadius(4f * u))
            drawRoundRect(luz(if (oscuro) 0.08f else 0.5f), Offset(r.left - 0.3f * u, r.top - 0.3f * u), r.size, CornerRadius(4f * u))
            drawRoundRect(c(mezcla(PARED, HUESO_LUZ, 0.22f)), r.topLeft, r.size, CornerRadius(4f * u))
            drawLine(som(if (oscuro) 0.35f else 0.14f), Offset(r.left + 2f * u, r.top + r.height * 0.56f), Offset(r.right - 2f * u, r.top + r.height * 0.56f), 0.35f * u)
            drawLine(luz(if (oscuro) 0.05f else 0.35f), Offset(r.left + 2f * u, r.top + r.height * 0.56f + 0.4f * u), Offset(r.right - 2f * u, r.top + r.height * 0.56f + 0.4f * u), 0.3f * u)
        }

        // El plafón con sus dos tiras de luz (bajas y ámbar de noche) y la luz que cae en la pared.
        drawRect(c(GRIS_CLARO), Offset.Zero, Size(w, 6.5f * u))
        drawRect(som(if (oscuro) 0.5f else 0.3f), Offset(0f, 6.5f * u), Size(w, 0.8f * u))
        drawLine(luz(if (oscuro) 0.1f else 0.45f), Offset(0f, 0.4f * u), Offset(w, 0.4f * u), 0.4f * u)
        listOf(0.06f to 0.35f, 0.65f to 0.94f).forEach { (a, b) ->
            val tira = Rect(w * a, 2f * u, w * b, 4.4f * u)
            drawRoundRect(c(GRIS_OSC), Offset(tira.left - 0.5f * u, tira.top - 0.5f * u), Size(tira.width + u, tira.height + u), CornerRadius(1.6f * u))
            if (oscuro) {
                drawRoundRect(mezcla(AMBAR, NOCHE, 0.45f), tira.topLeft, tira.size, CornerRadius(1.2f * u))
                drawRect(AMBAR.copy(alpha = 0.06f), Offset(tira.left, 7.3f * u), Size(tira.width, 10f * u))
            } else {
                drawRoundRect(Color(0xFFFFF8E8), tira.topLeft, tira.size, CornerRadius(1.2f * u))
                drawRect(luz(0.18f), Offset(tira.left, 7.3f * u), Size(tira.width, 14f * u))
            }
        }

        // La torre del radar, a la izquierda: el cuerpo de fibra con su visera, el panel de gris cálido y sus mandos.
        val tl = Rect(1.5f * u, 12f * u, n.left - 4.8f * u, 80f * u)
        torre(tl, oscuro, u)
        val fl = Rect(tl.left + 2f * u, tl.top + 4f * u, tl.right - 2f * u, tl.bottom - 3f * u)
        val cxL = fl.center.x
        val rr = minOf(fl.width * 0.36f, 8.2f * u)
        radar(Offset(cxL, fl.top + rr * 1.3f + 1.6f * u), rr, -38f, oscuro, e.semilla, u)
        val yIntL = fl.top + rr * 2.6f + 8.5f * u
        filaInterruptores(fl.left + 0.8f * u, fl.right - 0.8f * u, yIntL, 3, minOf(fl.width / 3f * 0.95f, 4.6f * u), oscuro, e.semilla)
        val yPer = yIntL + 8.6f * u
        perilla(Offset(cxL - fl.width * 0.24f, yPer), 2.5f * u, -40f, oscuro, c(TINTA).copy(alpha = 0.6f))
        perilla(Offset(cxL + fl.width * 0.24f, yPer), 2.5f * u, 55f, oscuro, c(TINTA).copy(alpha = 0.6f))
        val yPil = yPer + 6.4f * u
        listOf(AMBAR to true, AMBAR to true, ROJO to true, AMBAR to false).forEachIndexed { k, (col, on) ->
            piloto(Offset(fl.left + fl.width * (0.17f + k * 0.22f), yPil), 0.85f * u, col, on, oscuro)
        }
        for (k in 0 until 3) drawRoundRect(c(GRIS_OSC), Offset(fl.left + fl.width * 0.14f, yPil + (2.8f + k * 1.5f) * u), Size(fl.width * 0.72f, 0.7f * u), CornerRadius(0.35f * u))

        // La torre de la derecha: el visor de la ventana de transmisión, los pilotos, el estante de cartuchos, el botón rojo
        // con su guarda y dos interruptores de capuchón.
        val tr = Rect(n.right + 4.8f * u, 12f * u, w - 1.5f * u, 80f * u)
        torre(tr, oscuro, u)
        val fr = Rect(tr.left + 2f * u, tr.top + 4f * u, tr.right - 2f * u, tr.bottom - 3f * u)
        visor(Rect(fr.left + 1f * u, fr.top + 1.4f * u, fr.right - 1f * u, fr.top + 1.4f * u + minOf(fr.width * 0.3f, 6.4f * u)), "21:40", oscuro, u)
        listOf(ROJO to false, AMBAR to true, AMBAR to true).forEachIndexed { k, (col, on) ->
            piloto(Offset(fr.left + fr.width * (0.25f + k * 0.25f), fr.top + 11.6f * u), 0.85f * u, col, on, oscuro)
        }
        val est = Rect(fr.left + 1.2f * u, fr.top + 15f * u, fr.right - 1.2f * u, fr.top + 35f * u)
        hundido(est, 1.4f * u, c(PIZARRA), oscuro, u)
        val cw = (est.width - 2f * u) / 3f
        listOf(AMBAR, ROJO, RADAR).forEachIndexed { k, col ->
            cartucho(Offset(est.left + 1f * u + cw * (k + 0.5f), est.top + est.height * 0.58f + (if (k == 1) 1.2f * u else 0f)), cw * 0.86f, est.height * 0.8f, 0f, col, oscuro, u)
        }
        drawRect(som(if (oscuro) 0.5f else 0.3f), Offset(est.left, est.top), Size(est.width, 1.4f * u))
        // El botón rojo bajo su guarda de cromo.
        val bc = Offset(fr.center.x, fr.top + 43f * u)
        drawCircle(som(0.4f), 3.6f * u, Offset(bc.x + 0.5f * u, bc.y + 0.8f * u))
        drawCircle(c(GRIS_OSC), 3.6f * u, bc)
        drawCircle(c(CROMO), 3.1f * u, bc)
        drawCircle(if (oscuro) mezcla(ROJO, NOCHE, 0.25f) else ROJO, 2.3f * u, bc)
        drawCircle(mezcla(ROJO, Color.White, 0.35f).copy(alpha = if (oscuro) 0.4f else 0.8f), 1.1f * u, Offset(bc.x - 0.6f * u, bc.y - 0.7f * u))
        drawArc(c(CROMO_LUZ), 200f, 140f, false, Offset(bc.x - 4.4f * u, bc.y - 4.8f * u), Size(8.8f * u, 8.8f * u), style = Stroke(0.6f * u, cap = StrokeCap.Round))
        listOf(-1f, 1f).forEach { s -> drawCircle(c(GRIS_OSC), 0.6f * u, Offset(bc.x + s * 4.1f * u, bc.y - 1.6f * u)) }
        filaInterruptores(fr.left + 1f * u, fr.right - 1f * u, fr.top + 54.5f * u, 2, minOf(fr.width / 2f * 0.7f, 4.4f * u), oscuro, e.semilla + 3)

        // El tablero de trazado: el marco grueso de fibra, el labio de pizarra y el vidrio esmerilado (el nicho).
        val marco = Rect(n.left - 3.2f * u, n.top - 3.2f * u, n.right + 3.2f * u, n.bottom + 3.2f * u)
        sombraPieza(marco, 5f * u, 2.6f * u, if (oscuro) 0.6f else 0.32f)
        fibra(marco, 5f * u, c(HUESO), oscuro, u, degradado = true)
        drawRoundRect(c(PIZARRA), Offset(n.left - 1f * u, n.top - 1f * u), Size(n.width + 2f * u, n.height + 2f * u), CornerRadius(2.4f * u))
        drawRoundRect(nicho(e.fondo, VIDRIO, oscuro), n.topLeft, n.size, CornerRadius(1.5f * u))
        clipPath(redondo(n, 1.5f * u)) {
            val paso = 6f * u
            var gx = n.left + paso
            while (gx < n.right) { drawLine(e.tinta.copy(alpha = 0.045f), Offset(gx, n.top), Offset(gx, n.bottom), 0.25f * u); gx += paso }
            var gy = n.top + paso
            while (gy < n.bottom) { drawLine(e.tinta.copy(alpha = 0.045f), Offset(n.left, gy), Offset(n.right, gy), 0.25f * u); gy += paso }
            drawRoundRect(som(if (oscuro) 0.45f else 0.18f), Offset(n.left + 1.2f * u, n.top + 1.2f * u), Size(n.width + 4f * u, n.height + 4f * u), CornerRadius(2.6f * u), style = Stroke(2.4f * u))
            if (oscuro) drawRect(RADAR.copy(alpha = 0.04f), n.topLeft, Size(n.width, n.height * 0.3f))
        }
        listOf(Offset(marco.left + 1.8f * u, marco.top + 1.8f * u), Offset(marco.right - 1.8f * u, marco.top + 1.8f * u),
            Offset(marco.left + 1.8f * u, marco.bottom - 1.8f * u), Offset(marco.right - 1.8f * u, marco.bottom - 1.8f * u)).forEach { p ->
            drawCircle(som(0.35f), 0.75f * u, Offset(p.x + 0.2f * u, p.y + 0.3f * u))
            drawCircle(c(CROMO), 0.7f * u, p)
            drawLine(c(GRIS_OSC), Offset(p.x - 0.45f * u, p.y - 0.2f * u), Offset(p.x + 0.45f * u, p.y + 0.2f * u), 0.2f * u)
        }
        // El reloj doble sobre el tablero: la hora de la capital y la local, en su caja de pizarra con canto de fibra.
        relojDoble(Rect(w / 2 - 14f * u, 3.6f * u, w / 2 + 14f * u, n.top - 1.4f * u), oscuro, u)

        // La consola curva de enfrente: el tablero inclinado con sus mandos, que sigue la curva, y el apoyabrazos acolchado.
        val yA = 77f * u; val yM = 74f * u; val yA2 = 87f * u; val yM2 = 82.5f * u
        fun arco(x: Float, ext: Float, med: Float): Float { val f = (x - w / 2) / (w / 2); return ext + (med - ext) * (1f - f * f) }
        val tablero = Path().apply {
            moveTo(-1f, yA); quadraticTo(w / 2, 2 * yM - yA, w + 1f, yA)
            lineTo(w + 1f, yA2); quadraticTo(w / 2, 2 * yM2 - yA2, -1f, yA2); close()
        }
        drawPath(tablero, Brush.verticalGradient(listOf(c(HUESO_LUZ), c(HUESO), c(HUESO_OSC)), yM, yA2))
        drawPath(Path().apply { moveTo(-1f, yA); quadraticTo(w / 2, 2 * yM - yA, w + 1f, yA) }, luz(if (oscuro) 0.12f else 0.7f), style = Stroke(0.5f * u))
        // Los mandos sobre el tablero: interruptores a los lados, pulsadores iluminados en medio y pilotos.
        val yMedio = { x: Float -> (arco(x, yA, yM) + arco(x, yA2, yM2)) / 2f }
        for (k in 0 until 6) {
            val x = w * (0.035f + k * 0.04f)
            interruptor(Offset(x, yMedio(x)), 3.4f * u, (k + e.semilla) % 3 != 1, oscuro, if (k == 4) ROJO else null)
            val x2 = w - x
            interruptor(Offset(x2, yMedio(x2)), 3.4f * u, (k * 2 + e.semilla) % 3 != 0, oscuro, if (k == 2) AMBAR else null)
        }
        val colores = listOf(HUESO_LUZ, AMBAR, HUESO_LUZ, ROJO, HUESO_LUZ, RADAR, AMBAR, HUESO_LUZ, HUESO_LUZ, ROJO)
        for (k in 0 until 10) {
            val x = w * (0.3f + k * 0.0444f)
            val y = yMedio(x)
            val lado = 2.3f * u
            val on = colores[k] != HUESO_LUZ || k % 3 == 0
            if (on && oscuro) halo(Offset(x, y), lado * 1.6f, colores[k], 0.6f)
            drawRoundRect(som(0.4f), Offset(x - lado / 2 + 0.3f * u, y - lado / 2 + 0.45f * u), Size(lado, lado), CornerRadius(0.5f * u))
            drawRoundRect(c(GRIS_OSC), Offset(x - lado / 2, y - lado / 2), Size(lado, lado), CornerRadius(0.5f * u))
            val lente = if (on) (if (oscuro) colores[k] else mezcla(colores[k], Color.White, 0.2f)) else c(colores[k])
            drawRoundRect(lente, Offset(x - lado / 2 + 0.35f * u, y - lado / 2 + 0.35f * u), Size(lado - 0.7f * u, lado - 0.7f * u), CornerRadius(0.35f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.6f), Offset(x - lado * 0.28f, y - lado * 0.28f), Offset(x + lado * 0.1f, y - lado * 0.28f), 0.25f * u, StrokeCap.Round)
        }
        // El apoyabrazos de vinilo acolchado, con sus costuras y sus botones.
        val brazo = Path().apply {
            moveTo(-1f, yA2 - 0.3f * u); quadraticTo(w / 2, 2 * yM2 - yA2 - 0.3f * u, w + 1f, yA2 - 0.3f * u)
            lineTo(w + 1f, yA2 + 4.6f * u); quadraticTo(w / 2, 2 * yM2 - yA2 + 4.6f * u, -1f, yA2 + 4.6f * u); close()
        }
        drawPath(brazo, c(VINILO))
        drawPath(Path().apply { moveTo(-1f, yA2 + 0.4f * u); quadraticTo(w / 2, 2 * yM2 - yA2 + 0.4f * u, w + 1f, yA2 + 0.4f * u) }, luz(if (oscuro) 0.12f else 0.35f), style = Stroke(0.7f * u))
        var bx = w * 0.04f
        while (bx < w) {
            val by = arco(bx, yA2, yM2) + 2.3f * u
            drawLine(som(0.6f), Offset(bx, by - 2f * u), Offset(bx, by + 2.2f * u), 0.3f * u)
            drawCircle(mezcla(c(VINILO), Color.White, 0.12f), 0.4f * u, Offset(bx + w * 0.035f, by))
            bx += w * 0.07f
        }
        // El frente de la consola y su zócalo con las ranuras.
        val frente = Path().apply {
            moveTo(-1f, yA2 + 4.5f * u); quadraticTo(w / 2, 2 * yM2 - yA2 + 4.5f * u, w + 1f, yA2 + 4.5f * u)
            lineTo(w + 1f, h + 1f); lineTo(-1f, h + 1f); close()
        }
        drawPath(frente, c(GRIS))
        drawRect(som(if (oscuro) 0.5f else 0.3f), Offset(0f, 96f * u), Size(w, h - 96f * u))
        var rx = w * 0.32f
        while (rx < w * 0.68f) { drawRoundRect(c(GRIS_OSC), Offset(rx, arco(rx, yA2, yM2) + 7f * u), Size(1.6f * u, 3.2f * u), CornerRadius(0.8f * u)); rx += 2.8f * u }

        // Las dos sillas giratorias de espaldas, delante de la consola.
        silla(Offset(w * 0.115f, h + 3f * u), 30f * u, -0.6f, oscuro, u)
        silla(Offset(w * 0.885f, h + 3f * u), 30f * u, 0.8f, oscuro, u)
    }

    /** Una torre de consola: el cuerpo de fibra, la visera de arriba con su sombra y el panel de gris cálido. */
    private fun DrawScope.torre(r: Rect, oscuro: Boolean, u: Float) {
        sombraPieza(r, 5f * u, 2.4f * u, if (oscuro) 0.6f else 0.3f)
        fibra(r, 5f * u, t(HUESO, oscuro), oscuro, u, degradado = true)
        val cara = Rect(r.left + 2f * u, r.top + 4f * u, r.right - 2f * u, r.bottom - 3f * u)
        hundido(cara, 2.4f * u, t(mezcla(GRIS_CLARO, HUESO, 0.3f), oscuro), oscuro, u, 0.8f)
        val visera = Rect(r.left - 0.6f * u, r.top - 1.4f * u, r.right + 0.6f * u, r.top + 3.4f * u)
        drawRoundRect(som(if (oscuro) 0.5f else 0.25f), Offset(cara.left, visera.bottom - 0.2f * u), Size(cara.width, 1.6f * u), CornerRadius(0.8f * u))
        fibra(visera, 2.4f * u, t(HUESO_LUZ, oscuro), oscuro, u)
    }

    /** El reloj doble: dos esferas crema con sus horas (la de la capital y la local), separadas por un piloto ámbar. */
    private fun DrawScope.relojDoble(r: Rect, oscuro: Boolean, u: Float) {
        sombraPieza(r, 3f * u, 1.6f * u, if (oscuro) 0.6f else 0.35f)
        fibra(r, 3f * u, t(HUESO, oscuro), oscuro, u)
        val cara = Rect(r.left + 1.2f * u, r.top + 1.1f * u, r.right - 1.2f * u, r.bottom - 1.1f * u)
        drawRoundRect(t(PIZARRA, oscuro), cara.topLeft, cara.size, CornerRadius(2f * u))
        val rd = minOf(cara.height * 0.42f, cara.width * 0.2f)
        listOf(0.27f to (21f to 40f), 0.73f to (3f to 10f)).forEach { (f, hora) ->
            val c = Offset(cara.left + cara.width * f, cara.center.y)
            if (oscuro) halo(c, rd * 1.6f, AMBAR, 0.35f)
            drawCircle(t(CROMO, oscuro), rd * 1.1f, c)
            drawCircle(if (oscuro) mezcla(PAPEL, NOCHE, 0.42f) else PAPEL, rd, c)
            for (k in 0 until 12) {
                val a = Math.toRadians(k * 30.0)
                drawLine(TINTA.copy(alpha = 0.7f), Offset(c.x + rd * 0.78f * cos(a).toFloat(), c.y + rd * 0.78f * sin(a).toFloat()),
                    Offset(c.x + rd * (if (k % 3 == 0) 0.6f else 0.7f) * cos(a).toFloat(), c.y + rd * (if (k % 3 == 0) 0.6f else 0.7f) * sin(a).toFloat()), rd * 0.06f)
            }
            val ah = Math.toRadians((hora.first % 12 + hora.second / 60f) * 30.0 - 90.0)
            val am = Math.toRadians(hora.second * 6.0 - 90.0)
            drawLine(TINTA, c, Offset(c.x + rd * 0.42f * cos(ah).toFloat(), c.y + rd * 0.42f * sin(ah).toFloat()), rd * 0.11f, StrokeCap.Round)
            drawLine(TINTA, c, Offset(c.x + rd * 0.66f * cos(am).toFloat(), c.y + rd * 0.66f * sin(am).toFloat()), rd * 0.07f, StrokeCap.Round)
            drawCircle(ROJO, rd * 0.08f, c)
            drawArc(Color.White.copy(alpha = if (oscuro) 0.1f else 0.35f), 200f, 60f, false, Offset(c.x - rd * 0.85f, c.y - rd * 0.85f), Size(rd * 1.7f, rd * 1.7f), style = Stroke(rd * 0.08f))
        }
        piloto(cara.center, 0.6f * u, AMBAR, true, oscuro)
    }

    // ---------------------------------------------------------------- Mapas: la mesa de trazado

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.MAPAS))

        // El piso de goma en losetas, que asoma por las esquinas redondas de la mesa.
        drawRect(c(Color(0xFF4A4740)))
        val pasoL = 12f * u
        var lx = 0f
        while (lx < w) { drawLine(som(0.4f), Offset(lx, 0f), Offset(lx, h), 0.3f * u); lx += pasoL }
        var ly = 0f
        while (ly < h) { drawLine(som(0.4f), Offset(0f, ly), Offset(w, ly), 0.3f * u); ly += pasoL }

        // El tablero de fibra de la mesa, con su sombra, su labio moldeado y los cantos que miran a la luz.
        val mesa = Rect(1f * u, 1f * u, w - 1f * u, h - 1f * u)
        sombraPieza(mesa, 9f * u, 2f * u, 0.5f)
        fibra(mesa, 9f * u, c(HUESO), oscuro, u, degradado = true)
        drawRoundRect(som(if (oscuro) 0.3f else 0.12f), Offset(mesa.left + 2.6f * u, mesa.top + 2.6f * u), Size(mesa.width - 5.2f * u, mesa.height - 5.2f * u), CornerRadius(7f * u), style = Stroke(0.35f * u))
        drawRoundRect(luz(if (oscuro) 0.06f else 0.4f), Offset(mesa.left + 2.9f * u, mesa.top + 2.9f * u), Size(mesa.width - 5.2f * u, mesa.height - 5.2f * u), CornerRadius(7f * u), style = Stroke(0.3f * u))

        // Arriba: la franja de instrumentos, con el visor de segmentos, la fila de pilotos y la rejilla del altavoz.
        val franja = Rect(n.left, 3.6f * u, n.right, 11.4f * u)
        hundido(franja, 2f * u, c(PIZARRA), oscuro, u)
        visor(Rect(franja.left + 1.4f * u, franja.top + 1.3f * u, franja.left + 1.4f * u + 20f * u, franja.bottom - 1.3f * u), "0417", oscuro, u)
        val pilotos = listOf(AMBAR to true, AMBAR to false, ROJO to true, AMBAR to true, RADAR to true, ROJO to false)
        val px0 = franja.left + 26f * u; val px1 = franja.right - 16f * u
        pilotos.forEachIndexed { k, (col, on) -> piloto(Offset(px0 + (px1 - px0) * (k + 0.5f) / pilotos.size, franja.center.y), 1f * u, col, on, oscuro) }
        for (k in 0 until 5) drawRoundRect(c(BAQUELITA), Offset(franja.right - 13f * u + k * 2.4f * u, franja.top + 1.6f * u), Size(1.1f * u, franja.height - 3.2f * u), CornerRadius(0.55f * u))

        // El pozo de la carta: el marco de gris cálido hundido y la carta impresa (el nicho), sujeta por cuatro pinzas.
        val pozo = Rect(n.left - 2.2f * u, n.top - 2.2f * u, n.right + 2.2f * u, n.bottom + 2.2f * u)
        hundido(pozo, 2.6f * u, c(GRIS), oscuro, u, 1.2f)
        drawRect(som(if (oscuro) 0.5f else 0.3f), Offset(n.left + 0.5f * u, n.top + 0.7f * u), n.size)
        drawRect(nicho(e.fondo, PAPEL, oscuro), n.topLeft, n.size)
        drawRect(som(0.25f), n.topLeft, n.size, style = Stroke(0.25f * u))
        listOf(Offset(n.left + 3f * u, n.top) to 0f, Offset(n.right - 3f * u, n.top) to 0f, Offset(n.left + 3f * u, n.bottom) to 180f, Offset(n.right - 3f * u, n.bottom) to 180f).forEach { (p, g) ->
            pinza(Offset(p.x, p.y + (if (g == 0f) -1.3f else 1.3f) * u), 4f * u, g, oscuro)
        }

        // A la izquierda: la pantalla de radar empotrada, la perilla grande de alcance y los interruptores.
        val iz = (mesa.left + n.left - 2.2f * u) / 2 + 0.6f * u
        val anchoIz = n.left - 2.2f * u - mesa.left
        val rr = minOf(anchoIz * 0.32f, 9.5f * u)
        radar(Offset(iz, 26f * u), rr, 30f, oscuro, e.semilla, u)
        drawRoundRect(c(PAPEL), Offset(iz - 5f * u, 26f * u + rr * 1.3f + 1.6f * u), Size(10f * u, 2.2f * u), CornerRadius(0.4f * u))
        drawLine(c(TINTA).copy(alpha = 0.6f), Offset(iz - 3.6f * u, 26f * u + rr * 1.3f + 2.7f * u), Offset(iz + 2.4f * u, 26f * u + rr * 1.3f + 2.7f * u), 0.35f * u)
        perilla(Offset(iz, 54f * u), minOf(anchoIz * 0.15f, 4.4f * u), 70f, oscuro, c(TINTA).copy(alpha = 0.55f))
        filaInterruptores(iz - anchoIz * 0.42f, iz + anchoIz * 0.42f, 70f * u, 3, minOf(anchoIz * 0.84f / 3f * 0.9f, 4.4f * u), oscuro, e.semilla + 1)

        // A la derecha: la radio de maleta, abierta.
        val de = (n.right + 2.2f * u + mesa.right) / 2 - 0.6f * u
        val anchoDe = minOf(mesa.right - n.right - 2.2f * u - 4f * u, 27f * u)
        radioMaleta(Rect(de - anchoDe / 2, 14f * u, de + anchoDe / 2, 79f * u), oscuro, u)

        // Abajo: los pulsadores iluminados bajo la carta, los cartuchos sueltos, el lápiz graso y el cerco de una taza.
        val yP = n.bottom + 2.2f * u + (mesa.bottom - n.bottom - 2.2f * u) * 0.48f
        val colores = listOf(HUESO_LUZ, HUESO_LUZ, AMBAR, HUESO_LUZ, ROJO, HUESO_LUZ, AMBAR, RADAR)
        val pp0 = n.left + n.width * 0.24f; val pp1 = n.right - n.width * 0.24f
        val lado = minOf(3.6f * u, (pp1 - pp0) / colores.size * 0.75f)
        colores.forEachIndexed { k, col ->
            val x = pp0 + (pp1 - pp0) * (k + 0.5f) / colores.size
            val on = col != HUESO_LUZ
            if (on && oscuro) halo(Offset(x, yP), lado * 1.5f, col, 0.6f)
            drawRoundRect(som(0.4f), Offset(x - lado / 2 + 0.35f * u, yP - lado / 2 + 0.55f * u), Size(lado, lado), CornerRadius(0.7f * u))
            drawRoundRect(c(GRIS_OSC), Offset(x - lado / 2, yP - lado / 2), Size(lado, lado), CornerRadius(0.7f * u))
            drawRoundRect(if (on) (if (oscuro) col else mezcla(col, Color.White, 0.15f)) else c(col), Offset(x - lado / 2 + 0.4f * u, yP - lado / 2 + 0.4f * u), Size(lado - 0.8f * u, lado - 0.8f * u), CornerRadius(0.45f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.6f), Offset(x - lado * 0.26f, yP - lado * 0.26f), Offset(x + lado * 0.1f, yP - lado * 0.26f), 0.3f * u, StrokeCap.Round)
        }
        val ca = minOf(anchoIz * 0.36f, 9f * u)
        cartucho(Offset(n.left + n.width * 0.1f, yP), ca, ca * 1.15f, -14f, AMBAR, oscuro, u)
        cartucho(Offset(n.left + n.width * 0.17f, yP + 0.6f * u), ca, ca * 1.15f, 9f, ROJO, oscuro, u)
        lapizGraso(Offset(n.right - n.width * 0.17f, yP - 2.2f * u), Offset(n.right - n.width * 0.02f, yP + 2f * u), 1.3f * u, oscuro)
        val taza = Offset(mesa.right - 9f * u, mesa.bottom - 8.5f * u)
        drawCircle(t(Color(0xFF8A5A2E), oscuro).copy(alpha = 0.32f), 4.2f * u, taza, style = Stroke(0.45f * u))
        drawArc(t(Color(0xFF8A5A2E), oscuro).copy(alpha = 0.22f), 20f, 220f, false, Offset(taza.x - 3.7f * u, taza.y - 3.7f * u), Size(7.4f * u, 7.4f * u), style = Stroke(0.25f * u))
    }

    /**
     * La radio de maleta abierta, vista desde arriba: la tapa con su asa, su forro y la tabla de frecuencias bajo las cintas;
     * las bisagras; el panel con el dial ámbar y su aguja, el altavoz, las perillas, el interruptor y el auricular con su cable.
     */
    private fun DrawScope.radioMaleta(r: Rect, oscuro: Boolean, u: Float) {
        fun c(x: Color) = t(x, oscuro)
        val corte = r.top + r.height * 0.4f
        val tapa = Rect(r.left, r.top, r.right, corte - 0.5f * u)
        val caja = Rect(r.left, corte + 0.5f * u, r.right, r.bottom)
        // El asa de baquelita en el canto de la tapa.
        val asa = Rect(r.center.x - r.width * 0.22f, r.top - 2.2f * u, r.center.x + r.width * 0.22f, r.top + 0.6f * u)
        drawRoundRect(som(0.35f), Offset(asa.left + 0.4f * u, asa.top + 0.6f * u), asa.size, CornerRadius(1.2f * u))
        drawRoundRect(BAQUELITA, asa.topLeft, asa.size, CornerRadius(1.2f * u))
        drawLine(luz(if (oscuro) 0.08f else 0.25f), Offset(asa.left + 1.2f * u, asa.top + 0.5f * u), Offset(asa.right - 1.2f * u, asa.top + 0.5f * u), 0.4f * u, StrokeCap.Round)
        listOf(tapa, caja).forEachIndexed { k, p ->
            sombraPieza(p, 1.6f * u, 1.6f * u, if (oscuro) 0.6f else 0.4f)
            drawRoundRect(c(MALETA), p.topLeft, p.size, CornerRadius(1.6f * u))
            val rnd = Random(77L + k)
            repeat(50) { drawCircle((if (it % 2 == 0) luz(0.07f) else som(0.12f)), 0.25f * u, Offset(p.left + p.width * rnd.nextFloat(), p.top + p.height * rnd.nextFloat())) }
            drawRoundRect(luz(if (oscuro) 0.06f else 0.25f), Offset(p.left + 0.3f * u, p.top + 0.3f * u), Size(p.width - 0.6f * u, p.height - 0.6f * u), CornerRadius(1.4f * u), style = Stroke(0.3f * u))
            // Las cantoneras de cromo.
            listOf(Offset(p.left, p.top), Offset(p.right, p.top), Offset(p.left, p.bottom), Offset(p.right, p.bottom)).forEach { q ->
                drawCircle(c(CROMO), 1.4f * u, Offset(q.x + (if (q.x == p.left) 1.1f else -1.1f) * u, q.y + (if (q.y == p.top) 1.1f else -1.1f) * u))
            }
        }
        // El forro de la tapa y la tabla de frecuencias impresa bajo dos cintas.
        val forro = Rect(tapa.left + 2.2f * u, tapa.top + 2.2f * u, tapa.right - 2.2f * u, tapa.bottom - 2.2f * u)
        drawRoundRect(c(PIZARRA), forro.topLeft, forro.size, CornerRadius(0.8f * u))
        val tabla = Rect(forro.left + forro.width * 0.12f, forro.top + forro.height * 0.12f, forro.right - forro.width * 0.12f, forro.bottom - forro.height * 0.1f)
        drawRect(som(0.4f), Offset(tabla.left + 0.3f * u, tabla.top + 0.5f * u), tabla.size)
        drawRect(papel(PAPEL, oscuro), tabla.topLeft, tabla.size)
        drawRect(c(AMBAR), tabla.topLeft, Size(tabla.width, tabla.height * 0.14f))
        for (k in 0 until 5) {
            val y = tabla.top + tabla.height * (0.3f + k * 0.14f)
            drawLine(c(TINTA).copy(alpha = 0.55f), Offset(tabla.left + tabla.width * 0.08f, y), Offset(tabla.left + tabla.width * 0.4f, y), 0.3f * u)
            drawLine(c(TINTA).copy(alpha = 0.55f), Offset(tabla.left + tabla.width * 0.55f, y), Offset(tabla.right - tabla.width * (if (k % 2 == 0) 0.1f else 0.25f), y), 0.3f * u)
        }
        listOf(0.22f, 0.78f).forEach { f -> drawRect(c(BAQUELITA).copy(alpha = 0.85f), Offset(forro.left, forro.top + forro.height * f - 0.7f * u), Size(forro.width, 1.4f * u)) }
        // Las bisagras.
        listOf(0.25f, 0.75f).forEach { f -> drawRoundRect(c(CROMO), Offset(r.left + r.width * f - 2f * u, corte - 0.9f * u), Size(4f * u, 1.8f * u), CornerRadius(0.4f * u)) }
        // El panel de mandos.
        val panel = Rect(caja.left + 2f * u, caja.top + 2f * u, caja.right - 2f * u, caja.bottom - 2f * u)
        drawRoundRect(c(GRIS_CLARO), panel.topLeft, panel.size, CornerRadius(0.8f * u))
        drawRoundRect(luz(if (oscuro) 0.06f else 0.35f), panel.topLeft, panel.size, CornerRadius(0.8f * u), style = Stroke(0.3f * u))
        // El dial ámbar con su escala y la aguja roja.
        val dial = Rect(panel.left + 1.6f * u, panel.top + 1.6f * u, panel.right - 1.6f * u, panel.top + 1.6f * u + panel.height * 0.2f)
        if (oscuro) drawRoundRect(AMBAR.copy(alpha = 0.18f), Offset(dial.left - 1f * u, dial.top - 1f * u), Size(dial.width + 2f * u, dial.height + 2f * u), CornerRadius(1.4f * u))
        hundido(dial, 0.6f * u, if (oscuro) mezcla(AMBAR, Color(0xFF3A2208), 0.35f) else Color(0xFFF2D69A), oscuro, u, 0.5f)
        for (k in 0..14) {
            val x = dial.left + dial.width * (0.06f + k * 0.0628f)
            drawLine(TINTA.copy(alpha = 0.7f), Offset(x, dial.bottom - dial.height * 0.15f), Offset(x, dial.bottom - dial.height * (if (k % 5 == 0) 0.55f else 0.35f)), 0.22f * u)
        }
        drawLine(ROJO, Offset(dial.left + dial.width * 0.62f, dial.top + 0.3f * u), Offset(dial.left + dial.width * 0.62f, dial.bottom - 0.3f * u), 0.4f * u)
        // El altavoz: la rejilla de agujeros en su círculo.
        val al = Offset(panel.left + panel.width * 0.33f, panel.top + panel.height * 0.5f)
        val ar = minOf(panel.width * 0.25f, panel.height * 0.22f)
        drawCircle(c(GRIS_OSC), ar, al)
        drawCircle(som(0.4f), ar, al, style = Stroke(0.35f * u))
        for (i in -3..3) for (j in -3..3) {
            val p = Offset(al.x + i * ar * 0.27f, al.y + j * ar * 0.27f)
            if ((p - al).getDistance() < ar * 0.86f) drawCircle(c(BAQUELITA), ar * 0.08f, p)
        }
        // Las perillas y el interruptor de encendido con su piloto.
        perilla(Offset(panel.right - panel.width * 0.22f, panel.top + panel.height * 0.4f), ar * 0.42f, -60f, oscuro, c(TINTA).copy(alpha = 0.55f))
        perilla(Offset(panel.right - panel.width * 0.22f, panel.top + panel.height * 0.6f), ar * 0.34f, 30f, oscuro, c(TINTA).copy(alpha = 0.55f))
        piloto(Offset(panel.right - panel.width * 0.12f, panel.top + panel.height * 0.29f), 0.65f * u, ROJO, true, oscuro)
        // El auricular de baquelita sobre el panel, con su cable rizado.
        val y0 = panel.bottom - panel.height * 0.14f
        val auri = Path().apply {
            moveTo(panel.left + panel.width * 0.1f, y0 - 1.2f * u)
            lineTo(panel.right - panel.width * 0.1f, y0 - 1.2f * u)
            quadraticTo(panel.right - panel.width * 0.02f, y0, panel.right - panel.width * 0.1f, y0 + 1.6f * u)
            lineTo(panel.left + panel.width * 0.1f, y0 + 1.6f * u)
            quadraticTo(panel.left + panel.width * 0.02f, y0, panel.left + panel.width * 0.1f, y0 - 1.2f * u)
            close()
        }
        translate(0.4f * u, 0.7f * u) { drawPath(auri, som(0.45f)) }
        drawPath(auri, BAQUELITA)
        listOf(0.16f, 0.84f).forEach { f -> drawOval(mezcla(BAQUELITA, Color.White, 0.12f), Offset(panel.left + panel.width * f - 2.1f * u, y0 - 1.8f * u), Size(4.2f * u, 3.8f * u)) }
        drawLine(luz(if (oscuro) 0.1f else 0.3f), Offset(panel.left + panel.width * 0.22f, y0 - 0.7f * u), Offset(panel.right - panel.width * 0.22f, y0 - 0.7f * u), 0.35f * u, StrokeCap.Round)
        val cable = Path(); var cx0 = panel.left + panel.width * 0.08f; var cy0 = y0 + 1.2f * u
        cable.moveTo(cx0, cy0)
        repeat(6) { cable.quadraticTo(cx0 - 1.4f * u, cy0 - 1.2f * u, cx0 - 0.2f * u, cy0 - 1.8f * u); cx0 += 0.15f * u; cy0 -= 1.8f * u }
        drawPath(cable, BAQUELITA, style = Stroke(0.45f * u))
    }

    // ---------------------------------------------------------------- Otras vidas: el armario de equipo

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.OTRAS_VIDAS))

        // El cuerpo del armario: fibra hueso, de cantos redondos arriba, con su sombra.
        val cuerpo = Rect(0.5f * u, 1.5f * u, w - 0.5f * u, 93f * u)
        fibra(cuerpo, 8f * u, c(HUESO), oscuro, u, degradado = true)

        // La cabecera: el panel de gris cálido con el visor, los pilotos, el escudo del emblema, los interruptores y el radar.
        val cab = Rect(n.left, 5f * u, n.right, 24.5f * u)
        hundido(cab, 3.6f * u, c(mezcla(GRIS_CLARO, HUESO, 0.3f)), oscuro, u, 0.8f)
        visor(Rect(cab.left + 3f * u, cab.top + 4.6f * u, cab.left + 3f * u + 25f * u, cab.bottom - 4.6f * u), "0005", oscuro, u)
        val pi0 = cab.left + 32f * u; val pi1 = w / 2 - 13f * u
        listOf(AMBAR to true, ROJO to true, AMBAR to false, AMBAR to true).forEachIndexed { k, (col, on) ->
            piloto(Offset(pi0 + (pi1 - pi0) * (k + 0.5f) / 4f, cab.center.y), 1.1f * u, col, on, oscuro)
        }
        // El escudo de fibra con el emblema de la red pintado a plantilla.
        val em = Offset(w / 2, cab.center.y + 0.4f * u)
        val er = 8.6f * u
        drawCircle(som(if (oscuro) 0.55f else 0.35f), er, Offset(em.x + 0.6f * u, em.y + 0.9f * u))
        drawCircle(c(HUESO_LUZ), er, em)
        drawArc(som(if (oscuro) 0.4f else 0.2f), -20f, 150f, false, Offset(em.x - er * 0.9f, em.y - er * 0.9f), Size(er * 1.8f, er * 1.8f), style = Stroke(0.8f * u))
        drawCircle(c(PIZARRA), er * 0.78f, em)
        with(TrazosFrente) { emblemaRed(em, er * 0.52f, if (oscuro) AMBAR else mezcla(e.acento, AMBAR, 0.35f), 0.95f * u) }
        if (oscuro) halo(em, er * 0.9f, AMBAR, 0.25f)
        filaInterruptores(w / 2 + 13f * u, w / 2 + 34f * u, cab.center.y + 1.4f * u, 3, 4.6f * u, oscuro, e.semilla + 2, rotulos = false)
        radar(Offset(cab.right - 9f * u, cab.center.y), 5.6f * u, 210f, oscuro, e.semilla + 4, u)

        // La visera de la cabecera, que echa su sombra sobre las bahías.
        val vis = Rect(n.left - 1.2f * u, 25.5f * u, n.right + 1.2f * u, 28.6f * u)
        fibra(vis, 1.5f * u, c(HUESO_LUZ), oscuro, u)

        // Las cinco bahías (el nicho): el forro sólido, la sombra de la visera, el canto redondo de cada boca y los montantes.
        drawRoundRect(som(if (oscuro) 0.6f else 0.4f), Offset(n.left - 0.6f * u, n.top - 0.6f * u), Size(n.width + 1.2f * u, n.height + 1.2f * u), CornerRadius(3f * u))
        drawRect(nicho(e.fondo, PARED, oscuro), n.topLeft, n.size)
        val bw = n.width / 5f
        val mont = 1.8f * u
        for (i in 0 until 5) {
            val b = Rect(n.left + bw * i + (if (i == 0) 0f else mont / 2), n.top, n.left + bw * (i + 1) - (if (i == 4) 0f else mont / 2), n.bottom)
            clipRect(b.left, b.top, b.right, b.bottom) {
                drawRoundRect(som(if (oscuro) 0.45f else 0.22f), Offset(b.left + 1.4f * u, b.top + 1.6f * u), Size(b.width + 5f * u, b.height + 5f * u), CornerRadius(4.4f * u), style = Stroke(2.8f * u))
                drawRect(som(if (oscuro) 0.35f else 0.14f), b.topLeft, Size(b.width, 2.6f * u))
            }
            val boca = Path().apply { addRect(b); addRoundRect(RoundRect(b, CornerRadius(3.4f * u))); fillType = PathFillType.EvenOdd }
            drawPath(boca, c(HUESO))
            drawRoundRect(luz(if (oscuro) 0.06f else 0.4f), Offset(b.left + 0.2f * u, b.top + 0.2f * u), b.size, CornerRadius(3.4f * u), style = Stroke(0.35f * u))
        }
        for (i in 1 until 5) {
            val x = n.left + bw * i
            val m = Rect(x - mont / 2, n.top, x + mont / 2, n.bottom)
            drawRect(c(HUESO), m.topLeft, m.size)
            drawRect(luz(if (oscuro) 0.06f else 0.45f), m.topLeft, Size(mont * 0.3f, m.height))
            drawRect(som(if (oscuro) 0.35f else 0.18f), Offset(m.right - mont * 0.25f, m.top), Size(mont * 0.25f, m.height))
        }
        // Un piloto sobre cada bahía, en la visera.
        for (i in 0 until 5) {
            val col = if (i == 2) ROJO else AMBAR
            piloto(Offset(n.left + bw * (i + 0.5f), vis.center.y), 0.9f * u, col, i != 3, oscuro)
        }

        // Los costados: a la izquierda, tres interruptores en columna y una perilla; a la derecha, la cerradura con la llave
        // de cifrado y la columna de pilotos.
        val xi = (cuerpo.left + n.left) / 2
        for (k in 0 until 3) interruptor(Offset(xi, n.top + 7f * u + k * 11f * u), minOf((n.left - cuerpo.left) * 0.62f, 5.6f * u), k != 1, oscuro, if (k == 2) ROJO else null)
        perilla(Offset(xi, n.bottom - 6f * u), minOf((n.left - cuerpo.left) * 0.22f, 2.4f * u), 20f, oscuro, c(TINTA).copy(alpha = 0.55f))
        val xd = (cuerpo.right + n.right) / 2
        val ce = Offset(xd, n.top + 9f * u)
        val cr = minOf((cuerpo.right - n.right) * 0.3f, 3.2f * u)
        drawCircle(som(0.4f), cr * 1.1f, Offset(ce.x + 0.4f * u, ce.y + 0.6f * u))
        drawCircle(c(CROMO), cr, ce)
        drawCircle(c(CROMO_LUZ), cr * 0.72f, ce)
        drawArc(luz(if (oscuro) 0.2f else 0.8f), 190f, 80f, false, Offset(ce.x - cr * 0.85f, ce.y - cr * 0.85f), Size(cr * 1.7f, cr * 1.7f), style = Stroke(cr * 0.12f))
        drawRect(c(GRIS_OSC), Offset(ce.x - cr * 0.1f, ce.y - cr * 0.48f), Size(cr * 0.2f, cr * 0.96f))
        // La llave de cifrado puesta, con su cabeza de latón y su cordón.
        val llave = c(Color(0xFFC9A55A))
        rotate(-12f, ce) {
            drawRoundRect(som(0.4f), Offset(ce.x - cr * 0.16f + cr * 0.15f, ce.y - cr * 0.15f + cr * 0.2f), Size(cr * 0.32f, cr * 0.9f), CornerRadius(cr * 0.08f))
            drawRoundRect(llave, Offset(ce.x - cr * 0.16f, ce.y - cr * 0.15f), Size(cr * 0.32f, cr * 0.9f), CornerRadius(cr * 0.08f))
            val cab = Rect(ce.x - cr * 0.62f, ce.y + cr * 0.65f, ce.x + cr * 0.62f, ce.y + cr * 1.75f)
            drawRoundRect(som(0.4f), Offset(cab.left + cr * 0.15f, cab.top + cr * 0.2f), cab.size, CornerRadius(cr * 0.4f))
            drawRoundRect(llave, cab.topLeft, cab.size, CornerRadius(cr * 0.4f))
            drawRoundRect(luz(if (oscuro) 0.15f else 0.5f), Offset(cab.left + cr * 0.12f, cab.top + cr * 0.1f), Size(cab.width * 0.6f, cr * 0.16f), CornerRadius(cr * 0.08f))
            drawCircle(c(HUESO), cr * 0.17f, Offset(cab.center.x, cab.bottom - cr * 0.32f))
            val cordon = Path().apply { moveTo(cab.center.x, cab.bottom - cr * 0.32f); quadraticTo(cab.center.x - cr * 0.3f, cab.bottom + cr * 1.2f, cab.center.x + cr * 0.4f, cab.bottom + cr * 2f) }
            drawPath(cordon, c(ROJO), style = Stroke(cr * 0.12f, cap = StrokeCap.Round))
        }
        listOf(AMBAR to true, AMBAR to true, ROJO to false, RADAR to true).forEachIndexed { k, (col, on) ->
            piloto(Offset(xd, n.top + 24f * u + k * 6.5f * u), 1f * u, col, on, oscuro)
        }

        // Debajo de cada bahía, su etiqueta crema con la franja de color en su porta de cromo.
        for (i in 0 until 5) {
            val cx = n.left + bw * (i + 0.5f)
            val et = Rect(cx - bw * 0.26f, n.bottom + 2f * u, cx + bw * 0.26f, n.bottom + 7f * u)
            drawRoundRect(c(CROMO), Offset(et.left - 0.6f * u, et.top - 0.6f * u), Size(et.width + 1.2f * u, et.height + 1.2f * u), CornerRadius(0.8f * u))
            drawRect(papel(PAPEL, oscuro), et.topLeft, et.size)
            drawRect(c(if (i % 2 == 0) mezcla(e.acento, AMBAR, 0.3f) else PIZARRA), et.topLeft, Size(et.width * 0.14f, et.height))
            drawLine(c(TINTA).copy(alpha = 0.6f), Offset(et.left + et.width * 0.24f, et.center.y), Offset(et.right - et.width * 0.18f, et.center.y), 0.45f * u)
        }

        // El zócalo de gris con su filo de cromo y sus ranuras.
        val zo = Rect(0f, 93f * u, w, h)
        drawRect(c(GRIS_OSC), zo.topLeft, zo.size)
        drawLine(c(CROMO), Offset(0f, zo.top + 0.3f * u), Offset(w, zo.top + 0.3f * u), 0.6f * u)
        var sx = w * 0.08f
        while (sx < w * 0.92f) { drawRoundRect(som(0.6f), Offset(sx, zo.top + 2.4f * u), Size(1.4f * u, zo.height - 4f * u), CornerRadius(0.7f * u)); sx += 3f * u }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f // un dp del ícono (el lienzo mide ≈ 70 dp de alto)
        val c = Offset(w / 2, h / 2)
        fun k(x: Color) = t(x, oscuro)
        val marmol = if (oscuro) mezcla(MARMOL, NOCHE, 0.4f) else MARMOL
        when (id) {
            "unesco" -> {
                // La capital del Directorio: la escalinata, la columnata de mármol, el tambor y la cúpula de bronce; encima pasa
                // la nave patrulla, un bloque largo ceñido por su anillo, con sus luces.
                val base = c.y + 27f * s
                drawOval(som(0.3f), Offset(c.x - 38f * s, base - 2f * s), Size(76f * s, 4.5f * s))
                listOf(78f, 72f, 66f).forEachIndexed { i, an ->
                    val y = base - 2.4f * s * (i + 1)
                    drawRect(mezcla(marmol, SOMBRA, 0.12f + i * 0.04f), Offset(c.x - an / 2 * s, y), Size(an * s, 2.4f * s))
                    drawLine(luz(0.6f), Offset(c.x - an / 2 * s, y), Offset(c.x + an / 2 * s, y), 0.5f * s)
                }
                val yC = base - 7.2f * s; val altoC = 19f * s
                drawRect(mezcla(marmol, SOMBRA, 0.45f), Offset(c.x - 28f * s, yC - altoC), Size(56f * s, altoC))
                for (i in 0 until 8) {
                    val x = c.x - 26f * s + i * (52f / 7f) * s
                    drawRect(marmol, Offset(x - 1.6f * s, yC - altoC), Size(3.2f * s, altoC))
                    drawRect(som(0.22f), Offset(x + 0.6f * s, yC - altoC), Size(1f * s, altoC))
                }
                val ent = Rect(c.x - 31f * s, yC - altoC - 5f * s, c.x + 31f * s, yC - altoC)
                drawRect(marmol, ent.topLeft, ent.size)
                drawRect(k(BRONCE), Offset(ent.left, ent.top + 1.6f * s), Size(ent.width, 1f * s))
                drawRect(som(0.25f), Offset(ent.left, ent.bottom - 0.8f * s), Size(ent.width, 0.8f * s))
                val tam = Rect(c.x - 15f * s, ent.top - 6f * s, c.x + 15f * s, ent.top)
                drawRect(mezcla(marmol, SOMBRA, 0.06f), tam.topLeft, tam.size)
                for (i in 0 until 6) drawRect(som(0.35f), Offset(tam.left + 2.6f * s + i * 4.6f * s, tam.top + 1.5f * s), Size(1.4f * s, 3.2f * s))
                // La cúpula de bronce con sus nervios y su linterna.
                val cup = Rect(c.x - 15f * s, tam.top - 15f * s, c.x + 15f * s, tam.top + 15f * s)
                clipRect(cup.left - 1f, cup.top - 1f, cup.right + 1f, tam.top) {
                    drawOval(Brush.linearGradient(listOf(k(mezcla(BRONCE, Color.White, 0.35f)), k(BRONCE), k(mezcla(BRONCE, SOMBRA, 0.45f))), Offset(cup.left, cup.top), Offset(cup.right, tam.top)), cup.topLeft, cup.size)
                    listOf(-0.6f, -0.2f, 0.2f, 0.6f).forEach { f ->
                        drawArc(som(0.3f), 180f, 180f, false, Offset(c.x - 15f * s * kotlin.math.abs(f) - 0.5f, cup.top), Size(30f * s * kotlin.math.abs(f) + 1f, 30f * s), style = Stroke(0.4f * s))
                    }
                    drawArc(luz(0.5f), 200f, 50f, false, Offset(cup.left + 3f * s, cup.top + 3f * s), Size(24f * s, 24f * s), style = Stroke(1f * s))
                }
                drawRect(k(BRONCE), Offset(c.x - 1.8f * s, cup.top - 3.4f * s), Size(3.6f * s, 3.6f * s))
                drawLine(k(BRONCE), Offset(c.x, cup.top - 3.4f * s), Offset(c.x, cup.top - 8f * s), 0.7f * s)
                // La nave patrulla: el casco largo de bloques, el puente escalonado, el anillo que lo ciñe, las toberas y sus luces.
                val nv = Offset(c.x + 26f * s, c.y - 22f * s)
                rotate(-6f, nv) {
                    val casco = k(Color(0xFF6E6C66)); val oscuroC = k(Color(0xFF4F4D48))
                    val bl = Rect(nv.x - 17f * s, nv.y - 3.4f * s, nv.x + 17f * s, nv.y + 3.4f * s)
                    drawRoundRect(som(0.22f), Offset(bl.left + 1.5f * s, bl.top + 2.6f * s), bl.size, CornerRadius(1.2f * s))
                    drawOval(oscuroC, Offset(nv.x - 4.2f * s, nv.y - 10f * s), Size(8.4f * s, 20f * s), style = Stroke(2.4f * s))
                    drawRoundRect(casco, bl.topLeft, bl.size, CornerRadius(1.2f * s))
                    drawRect(casco, Offset(bl.left + 5f * s, bl.top - 2.6f * s), Size(12f * s, 2.8f * s))
                    drawRect(casco, Offset(bl.left + 8f * s, bl.top - 4.4f * s), Size(5.5f * s, 2f * s))
                    drawRect(luz(0.4f), Offset(bl.left + 5f * s, bl.top - 2.6f * s), Size(12f * s, 0.6f * s))
                    drawRect(luz(0.35f), Offset(bl.left + 1f * s, bl.top + 0.5f * s), Size(bl.width - 2f * s, 0.8f * s))
                    drawRect(som(0.3f), Offset(bl.left, bl.bottom - 1.6f * s), Size(bl.width, 1.6f * s))
                    for (i in 0 until 7) drawRect(som(0.4f), Offset(bl.left + 2.6f * s + i * 4.2f * s, nv.y - 0.4f * s), Size(2.4f * s, 0.9f * s))
                    for (i in 0 until 3) drawRect(AMBAR.copy(alpha = 0.9f), Offset(bl.left + 6.2f * s + i * 3.4f * s, bl.top - 1.6f * s), Size(1.4f * s, 0.8f * s))
                    drawRect(oscuroC, Offset(bl.right - 0.5f * s, nv.y - 2.6f * s), Size(2.6f * s, 5.2f * s))
                    // La mitad de delante del anillo, que pasa por encima del casco.
                    drawArc(oscuroC, -90f, 180f, false, Offset(nv.x - 4.2f * s, nv.y - 10f * s), Size(8.4f * s, 20f * s), style = Stroke(2.4f * s))
                    drawArc(luz(0.45f), -80f, 60f, false, Offset(nv.x - 3.6f * s, nv.y - 9.4f * s), Size(7.2f * s, 18.8f * s), style = Stroke(0.6f * s))
                    piloto(Offset(bl.left + 1.6f * s, nv.y + 0.6f * s), 0.75f * s, ROJO, true, oscuro)
                    if (oscuro) halo(Offset(bl.right + 3f * s, nv.y), 5f * s, AMBAR, 0.7f)
                    drawCircle(AMBAR, 1f * s, Offset(bl.right + 2.6f * s, nv.y))
                }
            }
            "rascar" -> {
                // La pantalla de radar en su bisel con la película plateada a medio raspar: debajo, la costa en verde.
                val pc = Offset(c.x - 8f * s, c.y)
                val pr = 23f * s
                drawCircle(som(0.3f), pr * 1.22f, Offset(pc.x + 1.2f * s, pc.y + 1.8f * s))
                drawCircle(k(HUESO), pr * 1.22f, pc)
                drawArc(luz(if (oscuro) 0.15f else 0.7f), 150f, 130f, false, Offset(pc.x - pr * 1.12f, pc.y - pr * 1.12f), Size(pr * 2.24f, pr * 2.24f), style = Stroke(1.2f * s))
                drawCircle(BAQUELITA, pr * 1.06f, pc)
                drawCircle(PANTALLA, pr, pc)
                with(TrazosFrente) { reticula(pc, pr * 0.94f, RADAR.copy(alpha = 0.4f), 0.5f * s, 3) }
                val costa = Path().apply {
                    moveTo(pc.x - pr * 0.8f, pc.y - pr * 0.1f)
                    cubicTo(pc.x - pr * 0.5f, pc.y - pr * 0.55f, pc.x - pr * 0.1f, pc.y - pr * 0.2f, pc.x + pr * 0.1f, pc.y - pr * 0.6f)
                    cubicTo(pc.x + pr * 0.4f, pc.y - pr * 0.3f, pc.x + pr * 0.2f, pc.y + pr * 0.3f, pc.x + pr * 0.6f, pc.y + pr * 0.45f)
                    cubicTo(pc.x + pr * 0.2f, pc.y + pr * 0.75f, pc.x - pr * 0.5f, pc.y + pr * 0.5f, pc.x - pr * 0.8f, pc.y - pr * 0.1f)
                    close()
                }
                drawPath(costa, RADAR.copy(alpha = 0.18f))
                drawPath(costa, RADAR, style = Stroke(0.8f * s))
                drawCircle(e.c1, 1.8f * s, Offset(pc.x - pr * 0.2f, pc.y - pr * 0.1f))
                // La película plateada: todo el vidrio menos el tramo raspado, de borde irregular, y sus virutas.
                val raspado = Path().apply {
                    moveTo(pc.x - pr * 0.95f, pc.y - pr * 0.4f); lineTo(pc.x - pr * 0.1f, pc.y - pr * 0.8f); lineTo(pc.x + pr * 0.05f, pc.y - pr * 0.55f)
                    lineTo(pc.x - pr * 0.05f, pc.y - pr * 0.3f); lineTo(pc.x + pr * 0.15f, pc.y - pr * 0.05f); lineTo(pc.x + pr * 0.02f, pc.y + pr * 0.2f)
                    lineTo(pc.x + pr * 0.1f, pc.y + pr * 0.42f); lineTo(pc.x - pr * 0.3f, pc.y + pr * 0.85f); lineTo(pc.x - pr * 0.95f, pc.y + pr * 0.4f); close()
                }
                val pelicula = Path().apply { addOval(Rect(pc.x - pr, pc.y - pr, pc.x + pr, pc.y + pr)); addPath(raspado); fillType = PathFillType.EvenOdd }
                clipPath(Path().apply { addOval(Rect(pc.x - pr, pc.y - pr, pc.x + pr, pc.y + pr)) }) {
                    drawPath(pelicula, k(Color(0xFFB8B5AD)))
                    for (i in 0 until 7) drawLine(luz(0.35f), Offset(pc.x + pr * (0.2f + i * 0.1f), pc.y - pr), Offset(pc.x + pr * (-0.2f + i * 0.1f), pc.y + pr), 0.5f * s)
                    drawPath(raspado, som(0.35f), style = Stroke(0.6f * s))
                }
                val rnd = Random(11L)
                repeat(8) { drawCircle(k(Color(0xFFB8B5AD)), (0.5f + rnd.nextFloat()) * s, Offset(pc.x + pr * (0.1f + 0.3f * rnd.nextFloat()), pc.y + pr * (0.5f + 0.4f * rnd.nextFloat()))) }
                // La ficha de la red, de acero, con el emblema grabado, que raspa.
                val fc = Offset(c.x + 25f * s, c.y + 13f * s); val fr = 10f * s
                drawCircle(som(0.35f), fr, Offset(fc.x + 1f * s, fc.y + 1.6f * s))
                drawCircle(k(CROMO), fr, fc)
                drawCircle(k(CROMO_LUZ), fr * 0.82f, fc)
                drawCircle(k(GRIS_OSC), fr * 0.82f, fc, style = Stroke(0.5f * s))
                with(TrazosFrente) { emblemaRed(fc, fr * 0.5f, k(GRIS_OSC), 0.9f * s) }
                drawArc(luz(0.7f), 200f, 60f, false, Offset(fc.x - fr * 0.9f, fc.y - fr * 0.9f), Size(fr * 1.8f, fr * 1.8f), style = Stroke(0.8f * s))
            }
            else -> {
                // La carta estelar impresa, sujeta con cinta: la retícula, las estrellas, las órbitas, y tres pilotos clavados
                // en tus lugares, unidos por la ruta de salto.
                rotate(-3f, c) {
                    val hoja = Rect(c.x - 40f * s, c.y - 26f * s, c.x + 40f * s, c.y + 26f * s)
                    drawRect(som(0.3f), Offset(hoja.left + 1.5f * s, hoja.top + 2f * s), hoja.size)
                    drawRect(papel(PAPEL, oscuro), hoja.topLeft, hoja.size)
                    clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                        var x = hoja.left + 6f * s
                        while (x < hoja.right) { drawLine(k(GRIS).copy(alpha = 0.35f), Offset(x, hoja.top), Offset(x, hoja.bottom), 0.3f * s); x += 10f * s }
                        var y = hoja.top + 6f * s
                        while (y < hoja.bottom) { drawLine(k(GRIS).copy(alpha = 0.35f), Offset(hoja.left, y), Offset(hoja.right, y), 0.3f * s); y += 10f * s }
                        drawCircle(e.c2.copy(alpha = 0.45f), 30f * s, Offset(hoja.left + 8f * s, hoja.bottom + 4f * s), style = Stroke(0.4f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * s, 1.5f * s))))
                        val rnd = Random(23L)
                        repeat(34) { drawCircle(TINTA.copy(alpha = 0.55f), (0.3f + 0.6f * rnd.nextFloat()) * s, Offset(hoja.left + hoja.width * rnd.nextFloat(), hoja.top + hoja.height * rnd.nextFloat())) }
                    }
                    drawRect(som(0.25f), hoja.topLeft, hoja.size, style = Stroke(0.4f * s))
                    listOf(Offset(hoja.left + 4f * s, hoja.top), Offset(hoja.right - 4f * s, hoja.top)).forEachIndexed { i, p ->
                        rotate(if (i == 0) -35f else 35f, p) { drawRect(AMBAR.copy(alpha = 0.55f), Offset(p.x - 5f * s, p.y - 1.8f * s), Size(10f * s, 3.6f * s)) }
                    }
                    val paradas = listOf(Offset(hoja.left + 14f * s, hoja.top + 34f * s), Offset(hoja.left + 40f * s, hoja.top + 14f * s), Offset(hoja.right - 13f * s, hoja.top + 30f * s))
                    with(TrazosFrente) { rutaSalto(paradas, e.c1, 1.1f * s, 3.4f * s) }
                    listOf(AMBAR, ROJO, RADAR).forEachIndexed { i, col -> piloto(paradas[i], 2f * s, col, true, oscuro) }
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el tablero magnético de la célula

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.NOTAS))

        // El marco de fibra del tablero, con su sombra en la pared y sus cuatro pernos.
        val marco = Rect(0.6f * u, 7.5f * u, w - 0.6f * u, 92.5f * u)
        sombraPieza(marco, 7f * u, 2f * u, if (oscuro) 0.6f else 0.3f)
        fibra(marco, 7f * u, c(HUESO), oscuro, u, degradado = true)
        pinceladas(marco, c(HUESO_OSC), e.semilla, 18, u, if (oscuro) 0.05f else 0.08f)
        listOf(Offset(marco.left + 3.2f * u, marco.top + 3.2f * u), Offset(marco.right - 3.2f * u, marco.top + 3.2f * u),
            Offset(marco.left + 3.2f * u, marco.bottom - 3.2f * u), Offset(marco.right - 3.2f * u, marco.bottom - 3.2f * u)).forEach { p ->
            drawCircle(som(0.35f), 0.95f * u, Offset(p.x + 0.25f * u, p.y + 0.35f * u))
            drawCircle(c(CROMO), 0.9f * u, p)
            drawCircle(luz(if (oscuro) 0.2f else 0.7f), 0.3f * u, Offset(p.x - 0.3f * u, p.y - 0.3f * u))
            drawLine(c(GRIS_OSC), Offset(p.x - 0.55f * u, p.y + 0.2f * u), Offset(p.x + 0.55f * u, p.y - 0.2f * u), 0.22f * u)
        }

        // El tablero de chapa pintada (el nicho): sólido, con las pasadas del rodillo y la sombra del marco por dentro.
        drawRoundRect(som(if (oscuro) 0.6f else 0.35f), Offset(n.left - 0.6f * u, n.top - 0.6f * u), Size(n.width + 1.2f * u, n.height + 1.2f * u), CornerRadius(2.2f * u))
        drawRoundRect(luz(if (oscuro) 0.06f else 0.45f), Offset(n.left + 0.3f * u, n.top + 0.3f * u), n.size, CornerRadius(1.8f * u))
        drawRoundRect(nicho(e.fondo, IMAN, oscuro), n.topLeft, n.size, CornerRadius(1.8f * u))
        pinceladas(n, if (oscuro) LUZ else HUESO_LUZ, e.semilla + 5, 22, u * 0.6f, if (oscuro) 0.02f else 0.1f)
        clipPath(redondo(n, 1.8f * u)) {
            drawRoundRect(som(if (oscuro) 0.4f else 0.16f), Offset(n.left + 1.2f * u, n.top + 1.2f * u), Size(n.width + 4f * u, n.height + 4f * u), CornerRadius(3f * u), style = Stroke(2.4f * u))
        }

        // La cabecera: la visera de fibra con su franja de gris, los pilotos, el escudo con el emblema y el visor de la ventana.
        val cab = Rect(2.5f * u, 1f * u, w - 2.5f * u, 12.6f * u)
        sombraPieza(cab, 5f * u, 1.6f * u, if (oscuro) 0.5f else 0.25f)
        fibra(cab, 5f * u, c(HUESO_LUZ), oscuro, u)
        val fr = Rect(cab.left + 4f * u, cab.top + 2.2f * u, cab.right - 4f * u, cab.bottom - 2.2f * u)
        hundido(fr, 2f * u, c(mezcla(GRIS_CLARO, HUESO, 0.25f)), oscuro, u, 0.7f)
        listOf(AMBAR to true, AMBAR to false, ROJO to true).forEachIndexed { k, (col, on) ->
            piloto(Offset(fr.left + 3.6f * u + k * 4.2f * u, fr.center.y), 1f * u, col, on, oscuro)
        }
        for (k in 0 until 4) drawRoundRect(c(GRIS_OSC), Offset(fr.left + 16f * u + k * 2.2f * u, fr.top + 1.6f * u), Size(1f * u, fr.height - 3.2f * u), CornerRadius(0.5f * u))
        val em = Offset(w / 2, cab.center.y)
        val er = cab.height * 0.46f
        drawCircle(som(if (oscuro) 0.5f else 0.3f), er, Offset(em.x + 0.4f * u, em.y + 0.6f * u))
        drawCircle(c(HUESO_LUZ), er, em)
        drawCircle(c(PIZARRA), er * 0.8f, em)
        with(TrazosFrente) { emblemaRed(em, er * 0.52f, if (oscuro) AMBAR else mezcla(e.acento, AMBAR, 0.35f), 0.6f * u) }
        visor(Rect(fr.right - 1.2f * u - 17f * u, fr.top + 1f * u, fr.right - 1.2f * u, fr.bottom - 1f * u), "21:40", oscuro, u)

        // La repisa de abajo, con los cartuchos acostados, el bote de microfilm y el lápiz graso.
        val rep = Rect(0f, 92.5f * u, w, 99.5f * u)
        drawRect(som(if (oscuro) 0.5f else 0.3f), Offset(rep.left + 1f * u, rep.bottom - 0.5f * u), Size(rep.width - 2f * u, 1f * u))
        fibra(rep, 1.6f * u, c(HUESO), oscuro, u)
        drawLine(luz(if (oscuro) 0.1f else 0.6f), Offset(rep.left + 1.5f * u, rep.top + 1.6f * u), Offset(rep.right - 1.5f * u, rep.top + 1.6f * u), 0.3f * u)
        val yR = rep.top + 0.4f * u
        listOf(0.16f to AMBAR, 0.27f to RADAR).forEach { (f, col) ->
            val r = Rect(w * f - 5f * u, yR - 2.6f * u, w * f + 5f * u, yR)
            drawRect(som(0.3f), Offset(r.left + 0.5f * u, r.bottom - 0.3f * u), Size(r.width, 0.7f * u))
            drawRoundRect(c(PLASTICO), r.topLeft, r.size, CornerRadius(0.3f * u))
            drawRect(papel(PAPEL, oscuro), Offset(r.left + 1.2f * u, r.top + 0.5f * u), Size(r.width * 0.6f, r.height - 1f * u))
            drawRect(c(col), Offset(r.left + 1.2f * u, r.top + 0.5f * u), Size(1.2f * u, r.height - 1f * u))
            drawLine(luz(if (oscuro) 0.1f else 0.35f), Offset(r.left + 0.3f * u, r.top + 0.2f * u), Offset(r.right - 0.3f * u, r.top + 0.2f * u), 0.25f * u)
        }
        val bo = Rect(w * 0.42f - 2.6f * u, yR - 3.6f * u, w * 0.42f + 2.6f * u, yR)
        drawRect(som(0.3f), Offset(bo.left + 0.5f * u, bo.bottom - 0.3f * u), Size(bo.width, 0.7f * u))
        drawRect(Brush.horizontalGradient(listOf(c(CROMO_LUZ), c(CROMO), c(GRIS)), bo.left, bo.right), bo.topLeft, bo.size)
        drawRect(c(GRIS_OSC), bo.topLeft, Size(bo.width, 0.9f * u))
        drawRect(c(AMBAR), Offset(bo.left, bo.top + 1.5f * u), Size(bo.width, 0.9f * u))
        lapizGraso(Offset(w * 0.6f, yR - 0.8f * u), Offset(w * 0.8f, yR - 0.8f * u), 1.4f * u, oscuro)

        // El grano fino de la pintura, al final.
        granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        val u = minOf(r0.width, r0.height) / 100f
        val gir = if (e.fijada) 0f else ((e.indice * 41) % 7 - 3) * 0.45f
        val r = Rect(r0.left + r0.width * 0.035f, r0.top + r0.height * 0.06f, r0.right - r0.width * 0.035f, r0.bottom - r0.height * 0.04f)
        val base = when (e.tipo) {
            "idea" -> TELETIPO
            "pensamiento" -> SALVIA
            "recordatorio" -> DESPACHO
            else -> PAPEL
        }
        val hoja = papel(base, oscuro)
        val tinta = if (oscuro) mezcla(TINTA, NOCHE, 0.2f) else TINTA
        val rojo = if (oscuro) mezcla(ROJO, NOCHE, 0.2f) else ROJO
        rotate(gir, r.center) {
            // La forma de cada papel: la tira del teletipo con sus bordes rasgados; la ficha y la orden, de cantos redondos;
            // el despacho, con la esquina cortada.
            val forma = when (e.tipo) {
                "idea" -> Path().apply {
                    moveTo(r.left, r.top + 2f * u)
                    var x = r.left; var k = 0
                    while (x < r.right) { x = minOf(x + r.width / 14f, r.right); lineTo(x, r.top + (if (k % 2 == 0) 0f else 2.4f) * u); k++ }
                    lineTo(r.right, r.bottom - 2f * u)
                    x = r.right; k = 0
                    while (x > r.left) { x = maxOf(x - r.width / 14f, r.left); lineTo(x, r.bottom - (if (k % 2 == 0) 0f else 2.2f) * u); k++ }
                    close()
                }
                "recordatorio" -> Path().apply {
                    moveTo(r.left, r.top); lineTo(r.right - r.width * 0.1f, r.top); lineTo(r.right, r.top + r.width * 0.1f); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
                }
                else -> redondo(r, 2.6f * u)
            }
            translate(1.2f * u, 2f * u) { drawPath(forma, som(if (oscuro) 0.55f else 0.28f)) }
            drawPath(forma, hoja)
            clipPath(forma) {
                // La luz de arriba a la izquierda y la sombra tenue del otro lado.
                for (k in 1..4) {
                    drawRect(luz((if (oscuro) 0.012f else 0.07f)), r.topLeft, Size(r.width, r.height * 0.08f * k))
                    drawRect(som((if (oscuro) 0.03f else 0.014f)), Offset(r.left, r.bottom - r.height * 0.08f * k), Size(r.width, r.height * 0.08f * k))
                }
                when (e.tipo) {
                    "idea" -> {
                        // La tira del teletipo: la cabecera de guiones impresa en ámbar y la fila de control abajo.
                        var x = r.left + r.width * 0.12f
                        while (x < r.right - r.width * 0.12f) { drawLine(t(Color(0xFFB85C0F), oscuro).copy(alpha = 0.7f), Offset(x, r.top + r.height * 0.09f), Offset(x + r.width * 0.035f, r.top + r.height * 0.09f), 0.8f * u); x += r.width * 0.06f }
                        for (k in 0 until 6) drawRect(tinta.copy(alpha = 0.45f), Offset(r.left + r.width * (0.12f + k * 0.07f), r.bottom - r.height * 0.1f), Size(r.width * 0.045f, 1.2f * u))
                    }
                    "pensamiento" -> {
                        // La ficha de la célula: el renglón de cabecera, el margen verde y el emblema impreso en la esquina.
                        drawLine(t(Color(0xFF4F7A5A), oscuro).copy(alpha = 0.7f), Offset(r.left, r.top + r.height * 0.11f), Offset(r.right, r.top + r.height * 0.11f), 0.6f * u)
                        drawLine(t(Color(0xFF4F7A5A), oscuro).copy(alpha = 0.45f), Offset(r.left + r.width * 0.08f, r.top), Offset(r.left + r.width * 0.08f, r.bottom), 0.5f * u)
                        with(TrazosFrente) { emblemaRed(Offset(r.right - r.width * 0.08f, r.bottom - r.height * 0.08f), r.width * 0.035f, t(Color(0xFF4F7A5A), oscuro).copy(alpha = 0.7f), 0.5f * u) }
                    }
                    "proposito" -> {
                        // La orden de misión: la franja ámbar de cabecera, las casillas del margen y la línea de la firma.
                        drawRect(t(AMBAR, oscuro), r.topLeft, Size(r.width, r.height * 0.075f))
                        drawLine(t(Color(0xFFB85C0F), oscuro), Offset(r.left, r.top + r.height * 0.075f), Offset(r.right, r.top + r.height * 0.075f), 0.5f * u)
                        for (j in 0 until 3) drawRect(tinta.copy(alpha = 0.55f), Offset(r.left + r.width * 0.03f, r.top + r.height * (0.2f + j * 0.18f)), Size(r.width * 0.05f, r.width * 0.05f), style = Stroke(0.5f * u))
                        drawLine(tinta.copy(alpha = 0.6f), Offset(r.left + r.width * 0.035f, r.top + r.height * 0.225f), Offset(r.left + r.width * 0.07f, r.top + r.height * 0.245f), 0.6f * u, StrokeCap.Round)
                        drawLine(tinta.copy(alpha = 0.3f), Offset(r.left + r.width * 0.55f, r.bottom - r.height * 0.08f), Offset(r.right - r.width * 0.08f, r.bottom - r.height * 0.08f), 0.4f * u)
                    }
                    else -> {
                        // El despacho urgente: la franja de aviso en rojo arriba y el sello redondo abajo a la derecha.
                        clipRect(r.left, r.top, r.right, r.top + r.height * 0.07f) {
                            var x = r.left - r.height * 0.07f
                            while (x < r.right) { drawLine(rojo.copy(alpha = 0.85f), Offset(x, r.top + r.height * 0.07f), Offset(x + r.height * 0.07f, r.top), r.width * 0.025f); x += r.width * 0.06f }
                        }
                        val sc = Offset(r.right - r.width * 0.12f, r.bottom - r.height * 0.1f)
                        drawCircle(rojo.copy(alpha = 0.55f), r.width * 0.065f, sc, style = Stroke(0.6f * u))
                        drawCircle(rojo.copy(alpha = 0.55f), r.width * 0.045f, sc, style = Stroke(0.35f * u))
                        drawLine(rojo.copy(alpha = 0.55f), Offset(sc.x - r.width * 0.03f, sc.y), Offset(sc.x + r.width * 0.03f, sc.y), 0.6f * u)
                    }
                }
            }
            drawPath(forma, som(if (oscuro) 0.35f else 0.22f), style = Stroke(0.3f * u))
            if (e.fijada) {
                // La cinta ámbar en la esquina y el imán rojo de baquelita.
                rotate(-38f, Offset(r.left + r.width * 0.06f, r.top + r.height * 0.06f)) {
                    drawRect(AMBAR.copy(alpha = if (oscuro) 0.4f else 0.55f), Offset(r.left + r.width * 0.06f - r.width * 0.12f, r.top + r.height * 0.06f - 2.6f * u), Size(r.width * 0.24f, 5.2f * u))
                }
                iman(Offset(r.center.x, r.top + r.height * 0.06f), 4.4f * u, rojo, oscuro)
            } else iman(Offset(r.center.x, r.top + r.height * 0.06f), 3.8f * u, BAQUELITA, oscuro)
        }
    }

    /** El imán redondo de baquelita que sujeta una nota: su sombra, el canto y el brillo de arriba a la izquierda. */
    private fun DrawScope.iman(c: Offset, r: Float, color: Color, oscuro: Boolean) {
        drawCircle(som(if (oscuro) 0.6f else 0.4f), r, Offset(c.x + r * 0.3f, c.y + r * 0.45f))
        drawCircle(mezcla(color, SOMBRA, 0.35f), r, c)
        drawCircle(color, r * 0.82f, Offset(c.x - r * 0.06f, c.y - r * 0.08f))
        drawArc(Color.White.copy(alpha = if (oscuro) 0.25f else 0.5f), 195f, 70f, false, Offset(c.x - r * 0.6f, c.y - r * 0.62f), Size(r * 1.1f, r * 1.1f), style = Stroke(r * 0.16f, cap = StrokeCap.Round))
    }

    /** La tinta de las notas: pizarra casi negra, sobre papel claro de día y en penumbra de noche. */
    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
