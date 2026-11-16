package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Revolución francesa
// =====================================================================

/**
 * Revolución: el estante de la sala de la sección del barrio (la asamblea de los vecinos), en una antigua tienda, hacia
 * 1790. Es un interior sin ventana: una pared de yeso viejo y amarillento, con desconchones que dejan ver el listón, manchas
 * de cola y capas de carteles y decretos de imprenta pegados unos sobre otros (la cabecera, la viñeta y los bloques de texto
 * solo sugeridos; uno rasgado, otro con la esquina despegada que hace sombra). La luz de día entra de arriba a la izquierda,
 * fuera del cuadro. El mueble es una estantería de tablas de pino sin cepillar: dos montantes con sus pies sobre el piso, y
 * entre ellos las tablas, algo vencidas, con su canto de luz, sus nudos y sus clavos, apoyadas en el centro en escuadras de
 * hierro forjado. Encima, en la repisa alta: a la izquierda cae una colgadura tricolor recogida con un cordón y su escarapela
 * (cortada por el borde); una pila de periódicos atados con cordel, el tintero de loza con dos plumas de ganso y la
 * salvadera, una campanilla de mano para pedir orden; el punto focal, a la derecha del centro, es el farol de hojalata con su
 * vela, delante del cartel grande del decreto; luego una hogaza empezada con su cuchillo sobre una tabla y una jarra de
 * estaño. En la pared, el gorro frigio de lana roja en su clavija con su escarapela y, en el rincón de la derecha, una pica
 * apoyada, cortada por el borde. Al pie, el piso de tomettes hexagonales con sus juntas y su desgaste, un tambor con sus
 * baquetas cortado a la izquierda, un cesto de ropa (el guiño a la lavandera del barco-lavadero) cortado a la derecha, y la
 * placa de «y N más» como un aviso pegado en la pared. Cada tema es, según la semilla, una caja de listones de pino atada con
 * una cinta (tricolor, roja o azul) con los cuadernos de quejas asomando; un legajo grueso de papeles entre dos tablillas de
 * cartón, atado con cordel y lacrado; o una cesta de mimbre con folletos cosidos de colores (más cuadernos, legajos o
 * folletos cuanto más guarda), y el tema en un cartel impreso pegado al frente. Lo que no tiene tema es un saco de arpillera
 * con papeles sueltos; un hueco libre guarda una cajita volcada y un clavo. Al abrir uno, lo guardado se levanta. De noche
 * todo se hunde en azul pardo hondo: alumbra solo el farol (su charco de luz en el yeso y en el cartel, la luz que sale por su
 * vidrio y sus agujeros, el filo cálido en los cantos de las tablas), con las filas de arriba encendidas, las de abajo y la
 * colgadura casi perdidas, y el rojo del gorro y de las cintas vivo cerca y apagado lejos. Por dentro, listones forrados de
 * papel de periódico. Las fichas son pasquines: papel de imprenta con su doble filete y el borde de abajo rasgado.
 */
object IdeasRevolucion : DisenoIdeas {
    // ---------------------------------------------------------------- los colores de siempre (interior, fichas y marcas)
    private val PINO = Color(0xFFB08A5A)
    private val PINO_OSC = Color(0xFF7A5A36)
    private val AZUL = Color(0xFF1F3A6E)
    private val ROJO = Color(0xFFB22234)
    private val BLANCO = Color(0xFFF4F0E6)
    private val PAPEL = Color(0xFFEFE8D6)
    private val NEGRO = Color(0xFF1C1A17)

    // ---------------------------------------------------------------- la paleta de la sala
    /** El yeso viejo de la pared, su sombra y el listón que asoma en los desconchones. */
    private val YESO = Color(0xFFE0D2AE)
    private val YESO_OSC = Color(0xFFB4A27C)
    private val LISTON = Color(0xFF8C6A44)
    /** El pino sin cepillar de la estantería: el claro de los cantos y su sombra. */
    private val PINO_LUZ = Color(0xFFD6B47E)
    private val PINO_HONDO = Color(0xFF5E4026)
    /** El hierro forjado de las escuadras, los clavos y la pica. */
    private val HIERRO = Color(0xFF33302E)
    /** El papel de cartel, ya amarillo, y la tinta de imprenta. */
    private val CARTEL = Color(0xFFE6D8B2)
    private val TINTA = Color(0xFF221E1A)
    /** Los tres colores de la escarapela, apagados (acento, no fondo): azul, blanco crudo y rojo de lana. */
    private val AZUL_P = Color(0xFF34497A)
    private val CRUDO = Color(0xFFEDE5D0)
    private val LANA = Color(0xFFA9322B)
    /** El barro de las tomettes y sus juntas. */
    private val TOMETTE = Color(0xFF9C5A3E)
    private val JUNTA = Color(0xFF7A6A58)
    /** La hojalata del farol, el estaño de la jarra, la loza del tintero, el pan, el mimbre, la arpillera, el lacre y el cartón. */
    private val HOJALATA = Color(0xFFA3A398)
    private val ESTANO = Color(0xFF8E9090)
    private val LOZA = Color(0xFFE9E4D6)
    private val PAN = Color(0xFFB4743A)
    private val MIGA = Color(0xFFE8D2A0)
    private val MIMBRE = Color(0xFFC29A5A)
    private val ARPILLERA = Color(0xFFAE9466)
    private val LACRE = Color(0xFF8A2620)
    private val CARTON = Color(0xFF6E7B8A)
    private val KRAFT = Color(0xFFA7865A)
    /** La llama de la vela y la noche de la sala: azul pardo hondo. */
    private val LLAMA = Color(0xFFFFC56E)
    private val NOCHE = Color(0xFF17161F)
    /**
     * El cartel del rótulo de noche, lejos del farol: con [NEGRO] da unos 6:1 en el título y 4.6:1 en la cuenta (al 78 %);
     * cerca del farol sube hacia [CARTEL] con un poco de llama.
     */
    private val PAPEL_LEJOS = Color(0xFFA49A84)
    /** Los cuadernos de quejas y los folletos: azul, gris, ocre, rosa, crudo, verde y jaspeado. */
    private val CUADERNOS = listOf(Color(0xFF8C9AB0), Color(0xFFD8B87A), Color(0xFFD3A09A), Color(0xFFE8E0CC), Color(0xFF9CB094), Color(0xFFA9A59B), Color(0xFF6F86A8))

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.25f
    override val corona = 0.6f
    override val base = 0.27f
    override val lado = 0.065f
    override val hueco = 0.045f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la repisa alta, su canto y lo bajo del canto, en fracción de su alto. */
    private const val SUP = 0.815f
    private const val FRENTE = 0.875f
    private const val CANTO = 0.93f
    /** En la base: el rincón de la pared con el piso, donde pisan los montantes y donde pisan las cosas del pie. */
    private const val SUELO = 0.5f
    private const val PISAN = 0.64f
    private const val PIE = 0.97f
    /** Los montantes, en fracción del ancho: borde de fuera y de dentro. */
    private const val M0 = 0.008f
    private const val M1 = 0.05f
    /**
     * En cada hueco (fracción de su alto): la tabla de la fila va dentro del hueco, a los tres cuartos (su cara de arriba,
     * su canto y lo bajo del canto); lo guardado pisa en [PISO] y el cartel cuelga del canto, por debajo. [SALIDA] es cuánto
     * se levanta lo guardado al abrirlo.
     */
    private const val SUP_T = 0.715f
    private const val PISO = 0.75f
    private const val CANTO_T = 0.765f
    private const val BAJO_T = 0.835f
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama del farol, en fracciones del ancho. */
    private val LUZ = Offset(0.655f, 0.44f)

    /** Cuánto le llega del farol a un punto [p] (en fracciones del ancho): cae con la distancia y, en el cuerpo, fila a fila. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        val radial = 1.4f * exp(-3.3f * sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que el farol echa hacia abajo sobre la estantería: la primera fila encendida, la segunda a medias, la tercera
     * tenue y las de abajo casi perdidas; un poco menos en la columna de la izquierda, más lejos del farol. Los puntos son los
     * centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.6f to 0.9f, 0.81f to 0.8f, 1.185f to 0.52f, 1.56f to 0.26f, 1.935f to 0.1f, 2.31f to 0.05f, 2.9f to 0.035f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.6f * (abs(p.x - LUZ.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.65f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono, con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.1f + NOCHE.red * 0.82f, c.green * 0.1f + NOCHE.green * 0.82f, c.blue * 0.1f + NOCHE.blue * 0.82f, c.alpha)

    /** Cómo se pinta algo según dónde está: de día tal cual (con [dia], algo más claro); de noche, según [k], lo que le llega del farol. */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, LLAMA, 0.16f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche de la sala. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f) = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f, dia)

    /** De noche la luz viene del farol: lo que está a su izquierda la recibe por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.03f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** La sombra de algo en la pared: de día, corrida a la derecha y abajo; de noche, hacia fuera del farol. No baja de [hasta]. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.18f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.02f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.022f * dx, w * 0.01f) { drawPath(p, NOCHE.copy(alpha = (0.32f * k).coerceAtMost(0.26f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa una tabla o el piso, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz, la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.2f * fuerza), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.34f * fuerza), Offset(x(0.15f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** El filo cálido del farol en algo, de noche: el canto de arriba y el costado que mira a la luz. De día, nada. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** Un clavo de forja: la cabeza con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.5f, r * 0.6f))
        drawCircle(t.c(HIERRO), r, p)
        drawCircle(t.b(0.45f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una escarapela plisada: azul afuera, blanco y rojo al centro, con su sombra, sus pliegues y su brillo. */
    private fun DrawScope.escarapelaPintada(c: Offset, r: Float, t: Tono) {
        val u = density
        drawCircle(t.s(0.3f), r, c + Offset(r * 0.25f, r * 0.3f))
        drawCircle(t.c(AZUL_P), r, c)
        drawCircle(t.c(CRUDO), r * 0.66f, c)
        drawCircle(t.c(LANA), r * 0.36f, c)
        for (k in 0 until 12) {
            val a = k * Math.PI / 6
            drawLine(t.s(0.22f), Offset(c.x + r * 0.42f * cos(a).toFloat(), c.y + r * 0.42f * sin(a).toFloat()), Offset(c.x + r * 0.96f * cos(a).toFloat(), c.y + r * 0.96f * sin(a).toFloat()), 0.5f * u)
        }
        drawArc(t.s(0.25f), -30f, 150f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(r * 0.18f))
        drawCircle(t.b(0.5f), r * 0.16f, c - Offset(r * 0.42f, r * 0.42f))
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
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN

        // ---- la pared de yeso, el piso de tomettes
        pared(w, ch, suelo)
        piso(w, suelo, alto)

        // ---- en la pared: los carteles en capas, los desconchones y las manchas de cola
        carteles(d, w, ch)

        // ---- la estantería: los montantes, las tablas de cada fila y la repisa alta
        montantes(w, ch * CANTO, pisan, suelo)
        d.filas.forEachIndexed { i, f ->
            val altoH = f.height
            tabla(w * M1, w * (1f - M1), f.top + altoH * SUP_T, f.top + altoH * CANTO_T, f.top + altoH * BAJO_T, 9100 + i, fuerte = true)
            escuadra(Offset(w * 0.5f, f.top + altoH * BAJO_T), w)
        }
        tabla(0f, w, ch * SUP, ch * FRENTE, ch * CANTO, 9000, alta = true)
        escuadra(Offset(w * 0.5f, ch * CANTO), w)

        // ---- en la pared de arriba: la colgadura, el gorro y la pica
        colgadura(w, ch)
        gorro(Offset(w * 0.815f, ch * 0.2f), w)
        pica(w, ch)

        // ---- en la repisa alta, de atrás adelante
        val pieA = ch * (SUP + (FRENTE - SUP) * 0.3f)
        val pieF = ch * (SUP + (FRENTE - SUP) * 0.88f)
        periodicos(Offset(w * 0.3f, pieA), w)
        farol(Offset(w * LUZ.x, pieA), w, ch)
        jarra(Offset(w * 0.875f, pieA), w)
        tintero(Offset(w * 0.435f, pieF), w)
        campanilla(Offset(w * 0.53f, pieF), w)
        hogaza(Offset(w * 0.775f, pieF), w)

        // ---- al pie: el tambor y el cesto de ropa, cortados por los bordes
        tambor(Offset(w * 0.12f, b.top + hb * PIE), w)
        cesto(Offset(w * 0.9f, b.top + hb * PIE), w)

        // ---- la placa de «y N más»: un aviso pegado en la pared baja
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.22f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRect(if (o) mezcla(PAPEL_LEJOS, CARTEL, (1.3f * t.k).coerceAtMost(1f)) else CARTEL, r.topLeft, r.size)
            drawRect(t.c(TINTA).copy(alpha = 0.8f), Offset(r.left + 3f * u, r.top + 2.5f * u), Size(r.width - 6f * u, 1.4f * u))
            drawRect(t.c(TINTA).copy(alpha = 0.6f), Offset(r.left + 3f * u, r.bottom - 3.5f * u), Size(r.width - 6f * u, 0.6f * u))
            drawRect(t.s(0.12f), Offset(r.right - r.width * 0.06f, r.top), Size(r.width * 0.06f, r.height))
            listOf(Offset(r.left + 2.5f * u, r.top + 2f * u), Offset(r.right - 2.5f * u, r.top + 2f * u)).forEach { clavo(it, 1.1f * u, t) }
        }

        // ---- la luz del farol, por encima de lo que tiene cerca
        val llama = LUZ * w
        if (o) {
            mancha(llama + Offset(0f, w * 0.03f), w * 0.5f, LLAMA.copy(alpha = 0.1f))
            mancha(llama, w * 0.2f, LLAMA.copy(alpha = 0.2f))
            mancha(llama + Offset(0f, w * 0.12f), w * 0.3f, LLAMA.copy(alpha = 0.08f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /** La pared de yeso: la mancha de luz arriba a la izquierda (de noche, el halo del farol), pinceladas, el desgaste de abajo. */
    private fun DrawScope.pared(w: Float, ch: Float, suelo: Float) {
        val u = density
        val o = esNoche
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(YESO, LUZ_MUEBLE, 0.3f), YESO, mezcla(YESO, YESO_OSC, 0.45f)), 0f, w), Offset.Zero, Size(w, suelo))
            mancha(Offset(w * 0.05f, 0f), w * 0.8f, luzMueble(0.4f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.12f)), ch, suelo), Offset(0f, ch), Size(w, suelo - ch))
        } else {
            fun k(dd: Float) = Tono(true, luz(LUZ + Offset(dd, 0f)), 0f).c(YESO)
            drawRect(Brush.radialGradient(0f to k(0.03f), 0.08f to k(0.12f), 0.18f to k(0.26f), 0.32f to k(0.45f), 0.55f to k(0.75f), 1f to k(1.3f),
                center = LUZ * w, radius = w * 1.4f), Offset.Zero, Size(w, suelo))
        }
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else YESO_OSC, 9201, 70, u, 0.11f)
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else mezcla(YESO, LUZ_MUEBLE, 0.5f), 9202, 30, u, 0.1f)
        // El desgaste de abajo: el yeso sucio y rozado junto al piso.
        val tz = tn(Offset(w * 0.5f, suelo))
        drawRect(tz.c(mezcla(YESO_OSC, KRAFT, 0.3f)).copy(alpha = 0.45f), Offset(0f, suelo - w * 0.05f), Size(w, w * 0.05f))
        pinceladas(Rect(0f, suelo - w * 0.07f, w, suelo), if (o) NOCHE else mezcla(YESO_OSC, KRAFT, 0.5f), 9203, 18, u, 0.16f)
        // El zócalo pintado de ocre, gastado a rozones, con su filete.
        val zt = suelo - w * 0.05f
        drawRect(tz.c(mezcla(Color(0xFF8C6440), YESO_OSC, 0.35f)), Offset(0f, zt), Size(w, suelo - zt))
        pinceladas(Rect(0f, zt, w, suelo), if (o) NOCHE else YESO, 9204, 16, u, 0.14f)
        for (k in 0 until 3) drawOval(tz.c(mezcla(YESO_OSC, YESO, 0.3f)).copy(alpha = 0.3f), Offset(w * (0.3f + 0.22f * k + 0.05f * azar(9205, k)), zt + w * 0.012f), Size(w * 0.07f, w * 0.01f))
        drawRect(tz.c(mezcla(Color(0xFF6E4A30), YESO_OSC, 0.2f)), Offset(0f, zt - w * 0.004f), Size(w, w * 0.004f))
        drawRect(tz.b(0.3f), Offset(0f, zt - w * 0.004f), Size(w, 0.8f * u))
    }

    /** Un desconchón: el yeso caído deja ver el listón de madera; el borde de arriba a la izquierda hace sombra adentro. */
    private fun DrawScope.desconchon(r: Rect, semilla: Int) {
        val u = density
        val t = tn(r.center)
        val p = contornoIrregular(r, semilla, 22, 0.12f)
        drawPath(p, t.c(mezcla(YESO_OSC, Color(0xFF9A9286), 0.4f)))
        pinceladas(r, if (esNoche) NOCHE else Color(0xFF7E776C), semilla, 5, u, 0.2f)
        val dentro = contornoIrregular(Rect(r.left + r.width * 0.16f, r.top + r.height * 0.2f, r.right - r.width * 0.12f, r.bottom - r.height * 0.16f), semilla + 1, 16, 0.14f)
        clipPath(dentro) {
            // Las tiras de listón, con la holgura oscura entre una y otra.
            val paso = maxOf(2f * u, r.height / 4.5f)
            var y = r.top + paso * 0.3f
            var k = 0
            while (y < r.bottom) {
                drawRect(t.c(mezcla(LISTON, KRAFT, 0.25f * azar(semilla, k))), Offset(r.left, y), Size(r.width, paso * 0.66f))
                drawRect(t.b(0.25f), Offset(r.left, y), Size(r.width, 0.8f * u))
                drawRect(t.s(0.3f), Offset(r.left, y + paso * 0.66f - 1f * u), Size(r.width, 1f * u))
                y += paso; k++
            }
            translate(r.width * 0.03f, r.height * 0.06f) { drawPath(dentro, t.s(0.35f), style = Stroke(r.height * 0.12f)) }
        }
        // El canto del yeso roto: claro abajo a la derecha, donde le da la luz.
        translate(-0.8f * u, -0.8f * u) { drawPath(p, t.b(0.35f), style = Stroke(0.9f * u)) }
        drawPath(p, t.s(0.25f), style = Stroke(0.6f * u))
    }

    /**
     * Un cartel de imprenta pegado: el papel con su sombra, la cabecera en letra gruesa, la viñeta, los bloques de texto
     * sugeridos a dos columnas, las manchas de cola; [rasgado] rompe el borde de abajo, [esquina] despega la de abajo a la derecha.
     */
    private fun DrawScope.cartel(r: Rect, giro: Float, semilla: Int, papel: Color, rasgado: Boolean = false, esquina: Boolean = false, vineta: Int = 0) {
        val u = density
        val t = tn(r.center)
        rotate(giro, r.center) {
            val e = r.width * 0.16f
            val hoja = if (rasgado) Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - r.height * 0.1f)
                val dientes = 9
                for (k in dientes downTo 0) lineTo(r.left + r.width * k / dientes, r.bottom - r.height * (0.02f + 0.16f * azar(semilla, k)))
                close()
            } else if (esquina) poligono(r.left, r.top, r.right, r.top, r.right, r.bottom - e, r.right - e, r.bottom, r.left, r.bottom)
            else poligono(r.left, r.top, r.right, r.top, r.right, r.bottom, r.left, r.bottom)
            sombraEnPared(hoja, r.center, size.height, 0.13f)
            drawPath(hoja, t.c(papel))
            clipPath(hoja) {
                val m = r.width * 0.09f
                val tinta = t.c(TINTA)
                // La cabecera: una línea gruesa de capitulares y una fina debajo.
                drawRect(tinta.copy(alpha = 0.8f), Offset(r.left + m, r.top + r.height * 0.06f), Size(r.width - 2 * m, r.height * 0.045f))
                drawRect(tinta.copy(alpha = 0.55f), Offset(r.left + m * 1.6f, r.top + r.height * 0.13f), Size(r.width - 3.2f * m, r.height * 0.018f))
                // La viñeta: un óvalo con su orla, o un haz con su gorro, sugeridos a trazo.
                val vc = Offset(r.center.x, r.top + r.height * 0.24f)
                val vr = r.width * 0.11f
                when (vineta) {
                    0 -> { drawOval(tinta.copy(alpha = 0.5f), Offset(vc.x - vr, vc.y - vr * 0.7f), Size(vr * 2, vr * 1.4f), style = Stroke(0.7f * u))
                        drawOval(tinta.copy(alpha = 0.3f), Offset(vc.x - vr * 0.6f, vc.y - vr * 0.4f), Size(vr * 1.2f, vr * 0.8f)) }
                    1 -> { drawLine(tinta.copy(alpha = 0.55f), Offset(vc.x - vr * 1.3f, vc.y), Offset(vc.x + vr * 1.3f, vc.y), 0.6f * u)
                        drawCircle(tinta.copy(alpha = 0.45f), vr * 0.45f, vc) }
                    else -> drawRect(tinta.copy(alpha = 0.35f), Offset(vc.x - vr, vc.y - vr * 0.5f), Size(vr * 2, vr))
                }
                // El texto: dos columnas de renglones, con alguno más corto.
                val y0 = r.top + r.height * 0.34f
                val col = (r.width - 2 * m - r.width * 0.05f) / 2
                val paso = maxOf(2.2f * u, r.height * 0.036f)
                for (c in 0 until 2) {
                    var y = y0
                    var k = 0
                    while (y < r.bottom - r.height * 0.08f) {
                        val largo = if (azar(semilla + c, k) < 0.15f) 0.55f else 0.92f + 0.08f * azar(semilla + 9, k)
                        drawLine(tinta.copy(alpha = 0.2f), Offset(r.left + m + c * (col + r.width * 0.05f), y), Offset(r.left + m + c * (col + r.width * 0.05f) + col * largo, y), maxOf(0.6f * u, paso * 0.22f))
                        y += paso; k++
                    }
                }
                pinceladas(r, if (esNoche) NOCHE else KRAFT, semilla, 6, u, 0.12f)
                // Las manchas de cola en las esquinas de arriba.
                drawOval(t.c(KRAFT).copy(alpha = 0.22f), Offset(r.left - r.width * 0.05f, r.top - r.height * 0.03f), Size(r.width * 0.25f, r.height * 0.1f))
                drawOval(t.c(KRAFT).copy(alpha = 0.18f), Offset(r.right - r.width * 0.2f, r.top - r.height * 0.02f), Size(r.width * 0.25f, r.height * 0.09f))
                drawRect(t.b(0.12f), r.topLeft, Size(r.width * 0.3f, r.height))
            }
            if (esquina) {
                // La esquina despegada: el revés del papel y la sombra que echa en la pared.
                val doblez = poligono(r.right, r.bottom - e, r.right - e * 0.25f, r.bottom - e * 0.2f, r.right - e, r.bottom)
                translate(2f * u, 2.5f * u) { drawPath(doblez, t.s(0.3f)) }
                drawPath(doblez, t.c(mezcla(papel, KRAFT, 0.45f)))
                drawPath(doblez, t.b(0.3f), style = Stroke(0.6f * u))
            }
        }
    }

    /** Los carteles en capas de la pared, los desconchones y las manchas de humedad, también entre los huecos y bajo el mueble. */
    private fun DrawScope.carteles(d: Disposicion, w: Float, ch: Float) {
        val u = density
        // Arriba: restos viejos debajo, el decreto grande detrás del farol, un bando rasgado y un aviso chico encimado.
        cartel(Rect(w * 0.47f, ch * 0.02f, w * 0.6f, ch * 0.3f), -3f, 9301, mezcla(CARTEL, KRAFT, 0.35f), rasgado = true, vineta = 2)
        cartel(Rect(w * 0.53f, ch * 0.09f, w * 0.79f, ch * 0.66f), 1.2f, 9302, CARTEL, vineta = 0)
        cartel(Rect(w * 0.27f, ch * 0.13f, w * 0.45f, ch * 0.6f), -1.8f, 9303, mezcla(CARTEL, Color(0xFFD8C79A), 0.6f), rasgado = true, vineta = 1)
        cartel(Rect(w * 0.19f, ch * 0.33f, w * 0.29f, ch * 0.58f), 3.5f, 9304, mezcla(CARTEL, Color(0xFFC9D0D4), 0.45f), esquina = true, vineta = 2)
        desconchon(Rect(w * 0.815f, ch * 0.44f, w * 0.93f, ch * 0.57f), 9311)
        desconchon(Rect(w * 0.66f, ch * 0.68f, w * 0.72f, ch * 0.76f), 9312)
        seccion(d, w)
        // Las manchas de humedad y de cola.
        listOf(Offset(0.18f, 0.5f), Offset(0.86f, 0.6f), Offset(0.12f, 0.9f)).forEach { f ->
            val p = Offset(w * f.x, ch * f.y)
            drawOval(tn(p).c(mezcla(YESO_OSC, KRAFT, 0.4f)).copy(alpha = 0.28f), Offset(p.x - w * 0.03f, p.y - w * 0.015f), Size(w * 0.06f, w * 0.035f))
            drawOval(tn(p).s(0.06f), Offset(p.x - w * 0.03f, p.y - w * 0.015f), Size(w * 0.06f, w * 0.035f), style = Stroke(0.8f * u))
        }
    }

    /**
     * En la pared entre las dos columnas, cosas de la sección: un aviso chico clavado, la lista de turnos de guardia con sus
     * palotes y la mancha clara de un cartel arrancado, con los restos de papel en las esquinas.
     */
    private fun DrawScope.seccion(d: Disposicion, w: Float) {
        val u = density
        val h = d.huecos
        val x0 = w * 0.458f; val x1 = w * 0.542f
        if (h.size >= 2) {
            val r = h[0]
            val p = Rect(x0, r.top + r.height * 0.14f, x1, r.top + r.height * 0.5f)
            val t = tn(p.center)
            rotate(3f, p.center) {
                sombraEnPared(Path().apply { addRect(p) }, p.center, size.height, 0.2f)
                drawRect(t.c(mezcla(CARTEL, CRUDO, 0.4f)), p.topLeft, p.size)
                drawRect(t.c(TINTA).copy(alpha = 0.75f), Offset(p.left + p.width * 0.15f, p.top + p.height * 0.14f), Size(p.width * 0.7f, p.height * 0.06f))
                for (k in 0 until 6) drawLine(t.c(TINTA).copy(alpha = 0.25f), Offset(p.left + p.width * 0.15f, p.top + p.height * (0.32f + 0.1f * k)), Offset(p.left + p.width * (if (k == 5) 0.5f else 0.85f), p.top + p.height * (0.32f + 0.1f * k)), 0.7f * u)
                drawRect(t.b(0.3f), p.topLeft, Size(p.width * 0.3f, p.height))
                clavo(Offset(p.center.x, p.top + p.height * 0.05f), 1.3f * u, t)
            }
        }
        if (h.size >= 4) {
            val r = h[2]
            val p = Rect(x0 - w * 0.004f, r.top + r.height * 0.1f, x1 + w * 0.004f, r.top + r.height * 0.6f)
            val t = tn(p.center)
            rotate(-2f, p.center) {
                sombraEnPared(Path().apply { addRect(p) }, p.center, size.height, 0.2f)
                drawRect(t.c(mezcla(CARTEL, Color(0xFFC9D0D4), 0.35f)), p.topLeft, p.size)
                drawRect(t.c(TINTA).copy(alpha = 0.6f), Offset(p.left + p.width * 0.12f, p.top + p.height * 0.08f), Size(p.width * 0.76f, p.height * 0.035f))
                // Los renglones: un nombre a pluma y sus palotes, de cuatro en cuatro con el tachón.
                val tinta = t.c(TINTA).copy(alpha = 0.55f)
                for (k in 0 until 6) {
                    val y = p.top + p.height * (0.2f + 0.13f * k)
                    drawLine(tinta.copy(alpha = 0.3f), Offset(p.left + p.width * 0.1f, y + p.height * 0.05f), Offset(p.left + p.width * 0.4f, y + p.height * 0.05f), 0.6f * u)
                    val grupos = 1 + Math.floorMod(k * 7 + 3, 3)
                    for (g in 0 until grupos) {
                        val gx = p.left + p.width * (0.46f + 0.2f * g)
                        val palotes = if (g < grupos - 1) 4 else 1 + Math.floorMod(k * 5, 4)
                        for (q in 0 until palotes) drawLine(tinta, Offset(gx + p.width * 0.035f * q, y), Offset(gx + p.width * 0.035f * q, y + p.height * 0.07f), 0.7f * u)
                        if (palotes == 4) drawLine(tinta, Offset(gx - p.width * 0.02f, y + p.height * 0.06f), Offset(gx + p.width * 0.13f, y + p.height * 0.01f), 0.7f * u)
                    }
                }
                drawRect(t.b(0.3f), p.topLeft, Size(p.width, 0.8f * u))
                listOf(Offset(p.left + 2.5f * u, p.top + 2.5f * u), Offset(p.right - 2.5f * u, p.top + 2.5f * u)).forEach { clavo(it, 1.1f * u, t) }
            }
        }
        if (h.size >= 6) {
            val r = h[4]
            val p = Rect(x0 - w * 0.008f, r.top + r.height * 0.08f, x1 + w * 0.008f, r.top + r.height * 0.6f)
            val t = tn(p.center)
            // Donde estuvo el cartel, el yeso quedó más limpio; en las esquinas, restos de papel y de cola.
            drawPath(contornoIrregular(p, 9341, 20, 0.05f), t.c(mezcla(YESO, LUZ_MUEBLE, 0.25f)).copy(alpha = 0.7f))
            drawPath(contornoIrregular(p, 9341, 20, 0.05f), t.c(KRAFT).copy(alpha = 0.18f), style = Stroke(1.5f * u))
            listOf(p.topLeft to 1f, Offset(p.right, p.top) to -1f, Offset(p.left, p.bottom) to 1f).forEachIndexed { i, (q, s) ->
                val dy = if (i == 2) -1f else 1f
                val resto = poligono(q.x, q.y, q.x + s * p.width * (0.4f - 0.1f * i), q.y, q.x + s * p.width * 0.15f, q.y + dy * p.height * 0.12f, q.x, q.y + dy * p.height * (0.2f - 0.05f * i))
                drawPath(resto, t.c(mezcla(CARTEL, KRAFT, 0.25f)))
                drawPath(resto, t.s(0.18f), style = Stroke(0.6f * u))
                if (i == 0) drawLine(t.c(TINTA).copy(alpha = 0.4f), Offset(q.x + p.width * 0.05f, q.y + p.height * 0.04f), Offset(q.x + p.width * 0.3f, q.y + p.height * 0.04f), 1.2f * u)
            }
            drawOval(t.c(KRAFT).copy(alpha = 0.2f), Offset(p.left + p.width * 0.3f, p.top + p.height * 0.4f), Size(p.width * 0.5f, p.height * 0.12f))
        }
    }

    /** El piso de tomettes hexagonales en perspectiva suave: juntas, cada barro de su tono, el desgaste y el rincón en sombra. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t0 = tn(Offset(w * 0.5f, suelo))
        drawRect(t0.c(JUNTA), Offset(0f, suelo), Size(w, alto - suelo))
        val hw = w * 0.062f
        var y = suelo - w * 0.006f
        var paso = w * 0.013f
        var fila = 0
        var guarda = 0
        while (y < alto && guarda < 40) {
            val alto6 = paso / 0.75f
            val q = alto6 * 0.25f
            val t = tn(Offset(w * 0.5f, y + alto6 / 2))
            var cx = if (fila % 2 == 0) 0f else hw / 2
            var j = 0
            while (cx < w + hw) {
                val g = hw * 0.47f
                val p = poligono(cx, y + 0.6f * u, cx + g, y + q, cx + g, y + alto6 - q, cx, y + alto6 - 0.6f * u, cx - g, y + alto6 - q, cx - g, y + q)
                val tono = mezcla(TOMETTE, if (azar(fila, j) > 0.5f) Color(0xFF7E4632) else Color(0xFFB07A5A), 0.45f * azar(fila + 50, j))
                drawPath(p, t.c(tono))
                clipPath(p) {
                    // El desgaste del centro y la luz de la izquierda.
                    drawOval(t.b(0.06f + 0.08f * azar(fila + 70, j)), Offset(cx - g * 0.7f, y + alto6 * 0.2f), Size(g * 1.2f, alto6 * 0.55f))
                    drawRect(t.s(0.14f), Offset(cx + g * 0.45f, y), Size(g * 0.6f, alto6))
                }
                cx += hw; j++
            }
            y += paso; paso *= 1.25f; fila++; guarda++
        }
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else KRAFT, 9401, 14, u, 0.12f)
        // El rincón de la pared con el piso.
        drawRect(t0.s(0.35f), Offset(0f, suelo), Size(w, 2.2f * u))
        drawRect(t0.s(0.16f), Offset(0f, suelo + 2.2f * u), Size(w, w * 0.012f))
    }

    // ---------------------------------------------------------------- la estantería

    /** Los dos montantes de pino: la cara con su veta y sus nudos, el canto de luz a la izquierda, sus pies y su sombra. */
    private fun DrawScope.montantes(w: Float, arriba: Float, pisan: Float, suelo: Float) {
        val u = density
        listOf(M0 to M1, (1f - M1) to (1f - M0)).forEachIndexed { i, (a, z) ->
            val r = Rect(w * a, arriba, w * z, pisan)
            // La sombra en la pared, a la derecha, y en el piso.
            val tp = tn(Offset(r.right, (arriba + suelo) / 2))
            drawRect(tp.s(0.2f), Offset(r.right, arriba), Size(w * 0.014f, suelo - arriba))
            contacto(Offset(r.center.x + w * 0.01f, pisan), r.width * 1.6f, w * 0.012f, 0.4f)
            // La cara, a trozos para que de noche le llegue la luz de cada fila.
            val n = 14
            for (k in 0 until n) {
                val y0 = r.top + r.height * k / n
                val t = tn(Offset(r.center.x, y0 + r.height / (2 * n)))
                drawRect(t.c(mezcla(PINO, PINO_OSC, 0.15f + 0.1f * i)), Offset(r.left, y0), Size(r.width, r.height / n + 0.5f))
                drawRect(t.b(0.4f), Offset(r.left, y0), Size(1.3f * u, r.height / n + 0.5f))
                drawRect(t.s(0.3f), Offset(r.right - r.width * 0.2f, y0), Size(r.width * 0.2f, r.height / n + 0.5f))
                if (esNoche && i == 1) drawRect(t.b(0.45f), Offset(r.left, y0), Size(1.5f * u, r.height / n + 0.5f))
            }
            vetas(r, (if (esNoche) NOCHE else PINO_HONDO).copy(alpha = 0.32f), 9500 + i, 0.6f, vertical = true, grosor = 0.6f, ondulacion = 0.6f)
            // Los nudos.
            for (k in 0 until 3) {
                val p = Offset(r.left + r.width * (0.3f + 0.4f * azar(9510 + i, k)), r.top + r.height * (0.15f + 0.3f * k + 0.1f * azar(9520 + i, k)))
                val t = tn(p)
                drawOval(t.c(PINO_HONDO), Offset(p.x - r.width * 0.16f, p.y - r.width * 0.28f), Size(r.width * 0.32f, r.width * 0.56f))
                drawOval(t.c(mezcla(PINO_HONDO, PINO, 0.4f)), Offset(p.x - r.width * 0.07f, p.y - r.width * 0.14f), Size(r.width * 0.14f, r.width * 0.28f))
            }
            // El pie: la madera rozada y sucia de abajo.
            val tb = tn(Offset(r.center.x, pisan))
            drawRect(tb.s(0.25f), Offset(r.left, pisan - w * 0.03f), Size(r.width, w * 0.03f))
        }
    }

    /**
     * Una tabla de pino sobre la que se apoyan las cosas: su cara de arriba vista un poco desde arriba (de [ySup] a
     * [yCanto]), el canto de frente con su luz (de [yCanto] a [yBajo]), vencido entre los apoyos, sus nudos, sus clavos y la
     * sombra que echa en la pared. [alta] es la repisa de arriba, que va de lado a lado y vuela sobre los montantes.
     */
    private fun DrawScope.tabla(x0: Float, x1: Float, ySup: Float, yCanto: Float, yBajo: Float, semilla: Int, alta: Boolean = false, fuerte: Boolean = false) {
        val u = density
        val w = anchoW
        val vence = w * 0.0045f
        // Cuánto se vence en x: cero en los montantes y en la escuadra del centro, lo más en medio de cada tramo.
        fun baja(x: Float): Float {
            val f = ((x / w) - M1) / (0.5f - M1)
            val g = if (f <= 1f) f else ((x / w) - 0.5f) / (1f - M1 - 0.5f)
            return if (g < 0f || g > 1f) 0f else vence * sin(Math.PI.toFloat() * g)
        }
        val pasos = 24
        // La sombra en la pared, bajo el canto.
        val sombra = Path().apply {
            moveTo(x0, yBajo)
            for (k in 0..pasos) { val x = x0 + (x1 - x0) * k / pasos; lineTo(x, yBajo + baja(x)) }
            for (k in pasos downTo 0) { val x = x0 + (x1 - x0) * k / pasos; lineTo(x + w * 0.01f, yBajo + baja(x) + w * (if (fuerte) 0.036f else 0.026f)) }
            close()
        }
        val ts = tn(Offset((x0 + x1) / 2, yBajo))
        drawPath(sombra, ts.s(if (fuerte) 0.3f else 0.2f))
        translate(0f, -w * 0.012f) { drawPath(sombra, ts.s(if (fuerte) 0.2f else 0.12f)) }
        // La cara de arriba y el canto, por tramos (de noche, cada uno con su luz).
        for (k in 0 until pasos) {
            val a = x0 + (x1 - x0) * k / pasos; val z = x0 + (x1 - x0) * (k + 1) / pasos
            val t = tn(Offset((a + z) / 2, yCanto))
            val da = baja(a); val dz = baja(z)
            drawPath(poligono(a, ySup + da, z, ySup + dz, z, yCanto + dz, a, yCanto + da), t.c(mezcla(PINO_LUZ, PINO, 0.35f)))
            drawPath(poligono(a, yCanto + da, z, yCanto + dz, z, yBajo + dz, a, yBajo + da), t.c(mezcla(PINO, PINO_LUZ, if (alta) 0.15f else 0.3f)))
            drawLine(t.b(0.55f), Offset(a, yCanto + da), Offset(z, yCanto + dz), 1.1f * u)
            drawLine(t.s(0.3f), Offset(a, yBajo + da - 0.6f * u), Offset(z, yBajo + dz - 0.6f * u), 1.2f * u)
            if (esNoche) drawLine(t.b(0.6f), Offset(a, yCanto + da + 0.6f * u), Offset(z, yCanto + dz + 0.6f * u), 0.8f * u)
        }
        // La veta del canto, los nudos y la sombra del fondo de la cara de arriba, junto a la pared.
        val tc = tn(Offset((x0 + x1) / 2, yCanto))
        vetas(Rect(x0, yCanto + 1f * u, x1, yBajo - 1f * u), (if (esNoche) NOCHE else PINO_HONDO).copy(alpha = 0.3f), semilla, 0.7f, grosor = 0.5f, ondulacion = 0.5f)
        drawRect(tc.s(0.22f), Offset(x0, ySup), Size(x1 - x0, (yCanto - ySup) * 0.3f))
        vetas(Rect(x0, ySup + (yCanto - ySup) * 0.3f, x1, yCanto), (if (esNoche) NOCHE else PINO_HONDO).copy(alpha = 0.18f), semilla + 1, 0.8f, grosor = 0.4f, ondulacion = 0.3f)
        for (k in 0 until 2) {
            val x = x0 + (x1 - x0) * (0.15f + 0.6f * azar(semilla, k) + 0.1f * k)
            val y = (yCanto + yBajo) / 2 + baja(x)
            val t = tn(Offset(x, y))
            drawOval(t.c(PINO_HONDO), Offset(x - w * 0.01f, y - (yBajo - yCanto) * 0.22f), Size(w * 0.02f, (yBajo - yCanto) * 0.44f))
            drawOval(t.c(mezcla(PINO_HONDO, PINO, 0.45f)), Offset(x - w * 0.004f, y - (yBajo - yCanto) * 0.1f), Size(w * 0.008f, (yBajo - yCanto) * 0.2f))
        }
        // Los clavos: en los montantes y en la escuadra.
        val r = maxOf(0.9f * u, (yBajo - yCanto) * 0.12f)
        listOf(w * (M0 + M1) / 2, w * 0.5f, w * (1f - (M0 + M1) / 2)).forEach { x ->
            if (x in x0..x1) clavo(Offset(x, (yCanto + yBajo) / 2 + baja(x)), r, tn(Offset(x, yCanto)))
        }
        // Una mella en el canto de la repisa alta.
        if (alta) {
            val x = w * 0.38f
            val t = tn(Offset(x, yCanto))
            drawPath(poligono(x, yCanto, x + w * 0.03f, yCanto, x + w * 0.012f, yCanto + (yBajo - yCanto) * 0.45f), t.c(mezcla(PINO_LUZ, CRUDO, 0.4f)))
        }
    }

    /** La escuadra de hierro forjado bajo una tabla: el fleje contra la pared con sus clavos, el tornapunta y la voluta, y su sombra. */
    private fun DrawScope.escuadra(p: Offset, w: Float) {
        val u = density
        val t = tn(p + Offset(0f, w * 0.04f))
        val alto = w * 0.085f
        val ancho = w * 0.011f
        val forma = Path().apply {
            addRect(Rect(p.x - ancho / 2, p.y, p.x + ancho / 2, p.y + alto))
        }
        val tornapunta = Path().apply { moveTo(p.x, p.y + alto * 0.78f); cubicTo(p.x + w * 0.012f, p.y + alto * 0.5f, p.x + w * 0.03f, p.y + alto * 0.18f, p.x + w * 0.04f, p.y + 0.5f * u) }
        // La punta de abajo del fleje, forjada en punta de lanza.
        val voluta = poligono(p.x - ancho / 2, p.y + alto, p.x + ancho / 2, p.y + alto, p.x, p.y + alto + w * 0.014f)
        translate(w * 0.01f, w * 0.008f) {
            drawPath(forma, t.s(0.3f)); drawPath(tornapunta, t.s(0.3f), style = Stroke(2.2f * u)); drawPath(voluta, t.s(0.3f))
        }
        drawPath(forma, t.c(HIERRO))
        drawRect(t.b(0.3f), Offset(p.x - ancho / 2, p.y), Size(1f * u, alto))
        drawPath(tornapunta, t.c(HIERRO), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(tornapunta, t.b(0.25f), style = Stroke(0.6f * u))
        drawPath(voluta, t.c(HIERRO))
        listOf(0.3f, 0.65f).forEach { f -> drawCircle(t.c(mezcla(HIERRO, TINTA, 0.5f)), 1.2f * u, Offset(p.x, p.y + alto * f)); drawCircle(t.b(0.4f), 0.5f * u, Offset(p.x - 0.4f * u, p.y + alto * f - 0.4f * u)) }
        // La herrumbre.
        drawOval(t.c(Color(0xFF7A4A2A)).copy(alpha = 0.5f), Offset(p.x - ancho * 0.4f, p.y + alto * 0.42f), Size(ancho * 0.8f, alto * 0.12f))
    }

    // ---------------------------------------------------------------- en la pared de arriba

    /**
     * La colgadura tricolor que cae desde una barra en el rincón de la izquierda, recogida a media altura con un cordón y una
     * escarapela: tres paños (azul, blanco y rojo) en pliegues que se juntan en el recogido y se abren al caer sobre la repisa.
     */
    private fun DrawScope.colgadura(w: Float, ch: Float) {
        val u = density
        val y0 = ch * 0.05f; val y1 = ch * (SUP + 0.01f)
        val vr = 0.47f
        fun suave(a: Float, b: Float, f: Float): Float { val s = f * f * (3f - 2f * f); return a + (b - a) * s }
        fun izq(v: Float) = if (v < vr) suave(-0.03f, -0.006f, v / vr) else suave(-0.006f, -0.025f, (v - vr) / (1f - vr))
        fun der(v: Float) = if (v < vr) suave(0.26f, 0.085f, v / vr) else suave(0.085f, 0.19f, (v - vr) / (1f - vr))
        // La forma entera (para la sombra en la pared).
        val n = 16
        val silueta = Path().apply {
            for (k in 0..n) { val v = k / n.toFloat(); val x = w * der(v); val y = y0 + (y1 - y0) * v; if (k == 0) moveTo(x, y) else lineTo(x, y) }
            for (k in n downTo 0) { val v = k / n.toFloat(); lineTo(w * izq(v), y0 + (y1 - y0) * v) }
            close()
        }
        sombraEnPared(silueta, Offset(w * 0.1f, ch * 0.4f), ch * SUP, 0.24f)
        // Los paños, en tiras: cuatro por color; cada tira con la luz de su pliegue.
        val tiras = 12
        val colores = listOf(AZUL_P, CRUDO, LANA)
        for (s in 0 until tiras) {
            val u0 = s / tiras.toFloat(); val u1 = (s + 1) / tiras.toFloat()
            val p = Path()
            for (k in 0..n) {
                val v = k / n.toFloat()
                val a = izq(v); val z = der(v)
                val x = w * (a + (z - a) * u0)
                val y = y0 + (y1 - y0) * v
                if (k == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            // El ruedo: cada pliegue acaba a su altura.
            val ruedo = (if (s % 2 == 0) 0f else -ch * 0.02f) + (if (s % 4 == 1) -ch * 0.012f else 0f)
            p.lineTo(w * (izq(1f) + (der(1f) - izq(1f)) * (u0 + u1) / 2), y1 + ruedo + ch * 0.008f)
            for (k in n downTo 0) {
                val v = k / n.toFloat()
                val a = izq(v); val z = der(v)
                p.lineTo(w * (a + (z - a) * u1), y0 + (y1 - y0) * v)
            }
            p.close()
            val cx = w * (izq(0.3f) + (der(0.3f) - izq(0.3f)) * (u0 + u1) / 2)
            val t = tn(Offset(cx, ch * 0.4f))
            val col = colores[s / 4]
            drawPath(p, t.c(col))
            // El pliegue: claro, medio, hondo, medio; y todo un poco más oscuro hacia la derecha.
            val fase = cos((s + 0.5f) * 2f * Math.PI.toFloat() / 4f)
            if (fase > 0.2f) drawPath(p, t.b(0.28f * fase))
            if (fase < -0.2f) drawPath(p, t.s(0.3f * -fase))
            drawPath(p, t.s(0.12f * u0))
        }
        // El recogido: el cordón enrollado con sus vueltas y la borla que cuelga, y la escarapela prendida.
        val yg = y0 + (y1 - y0) * vr
        val tg = tn(Offset(w * 0.04f, yg))
        val g0 = w * izq(vr) - w * 0.005f; val g1 = w * der(vr) + w * 0.006f
        for (k in 0 until 3) {
            val y = yg - w * 0.008f + k * w * 0.007f
            drawLine(tg.s(0.3f), Offset(g0, y + 1.2f * u), Offset(g1, y + 1.6f * u), 2.4f * u)
            drawLine(tg.c(mezcla(KRAFT, LANA, 0.35f)), Offset(g0, y), Offset(g1, y + 0.4f * u), 2.2f * u, StrokeCap.Round)
            drawLine(tg.b(0.4f), Offset(g0, y - 0.6f * u), Offset(g1, y - 0.2f * u), 0.6f * u)
        }
        val cordon = Path().apply { moveTo(g1, yg + w * 0.006f); cubicTo(g1 + w * 0.015f, yg + w * 0.03f, g1 + w * 0.005f, yg + w * 0.05f, g1 + w * 0.012f, yg + w * 0.07f) }
        drawPath(cordon, tg.c(mezcla(KRAFT, LANA, 0.35f)), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        val borla = Offset(g1 + w * 0.012f, yg + w * 0.07f)
        drawPath(poligono(borla.x - w * 0.006f, borla.y, borla.x + w * 0.006f, borla.y, borla.x + w * 0.01f, borla.y + w * 0.03f, borla.x - w * 0.01f, borla.y + w * 0.03f), tg.c(mezcla(KRAFT, LANA, 0.4f)))
        for (k in 0 until 4) drawLine(tg.s(0.3f), Offset(borla.x - w * 0.006f + w * 0.004f * k, borla.y + w * 0.008f), Offset(borla.x - w * 0.008f + w * 0.0055f * k, borla.y + w * 0.03f), 0.5f * u)
        escarapelaPintada(Offset(w * 0.045f, yg), w * 0.03f, tg)
        // La barra de madera de arriba, con su remate, y el clavo que la sostiene.
        val tb = tn(Offset(w * 0.12f, y0))
        drawRect(tb.s(0.25f), Offset(0f, y0 + 1.5f * u), Size(w * 0.29f, w * 0.012f))
        drawRect(tb.c(mezcla(PINO, PINO_HONDO, 0.35f)), Offset(0f, y0 - w * 0.006f), Size(w * 0.28f, w * 0.012f))
        drawRect(tb.b(0.4f), Offset(0f, y0 - w * 0.006f), Size(w * 0.28f, 1f * u))
        drawCircle(tb.c(mezcla(PINO, PINO_HONDO, 0.25f)), w * 0.012f, Offset(w * 0.285f, y0))
        drawCircle(tb.b(0.45f), w * 0.004f, Offset(w * 0.28f, y0 - w * 0.004f))
        // Las anillas que fruncen la tela en la barra.
        for (k in 0 until 6) {
            val x = w * (0.01f + 0.045f * k)
            drawOval(tb.c(HIERRO), Offset(x - w * 0.007f, y0 - w * 0.009f), Size(w * 0.014f, w * 0.018f), style = Stroke(1f * u))
        }
    }

    /**
     * El gorro frigio de lana roja en su clavija: el ruedo vuelto con su borde, la copa abultada y la punta que cae hacia
     * delante, los pliegues de la lana, unas puntadas y la escarapela prendida en el costado.
     */
    private fun DrawScope.gorro(clavija: Offset, w: Float) {
        val u = density
        val t = tn(clavija + Offset(0f, w * 0.06f))
        val der = luzDerecha(clavija.x)
        val s = w * 0.16f
        val c = clavija
        // La copa: sube por delante, se abomba arriba por detrás y baja por la nuca.
        val copa = Path().apply {
            moveTo(c.x - s * 0.4f, c.y + s * 0.6f)
            cubicTo(c.x - s * 0.43f, c.y + s * 0.3f, c.x - s * 0.32f, c.y + s * 0.02f, c.x - s * 0.1f, c.y - s * 0.16f)
            cubicTo(c.x + s * 0.06f, c.y - s * 0.32f, c.x + s * 0.38f, c.y - s * 0.32f, c.x + s * 0.43f, c.y + s * 0.02f)
            cubicTo(c.x + s * 0.47f, c.y + s * 0.28f, c.x + s * 0.45f, c.y + s * 0.45f, c.x + s * 0.41f, c.y + s * 0.6f)
            close()
        }
        // La punta, que se dobla hacia delante y cae por el lado de la cara.
        val punta = Path().apply {
            moveTo(c.x + s * 0.08f, c.y - s * 0.27f)
            cubicTo(c.x - s * 0.22f, c.y - s * 0.36f, c.x - s * 0.55f, c.y - s * 0.24f, c.x - s * 0.62f, c.y + s * 0.08f)
            cubicTo(c.x - s * 0.65f, c.y + s * 0.22f, c.x - s * 0.52f, c.y + s * 0.27f, c.x - s * 0.47f, c.y + s * 0.16f)
            cubicTo(c.x - s * 0.42f, c.y - s * 0.02f, c.x - s * 0.24f, c.y - s * 0.12f, c.x - s * 0.02f, c.y - s * 0.1f)
            close()
        }
        val ruedoP = Path().apply {
            moveTo(c.x - s * 0.44f, c.y + s * 0.52f)
            cubicTo(c.x - s * 0.2f, c.y + s * 0.47f, c.x + s * 0.2f, c.y + s * 0.47f, c.x + s * 0.44f, c.y + s * 0.52f)
            lineTo(c.x + s * 0.43f, c.y + s * 0.7f)
            cubicTo(c.x + s * 0.2f, c.y + s * 0.76f, c.x - s * 0.2f, c.y + s * 0.76f, c.x - s * 0.43f, c.y + s * 0.7f)
            close()
        }
        val ruedo = Rect(c.x - s * 0.44f, c.y + s * 0.47f, c.x + s * 0.44f, c.y + s * 0.76f)
        val todo = Path().apply { addPath(copa); addPath(punta); addPath(ruedoP) }
        sombraEnPared(todo, c, size.height, 0.24f)
        // La clavija de madera.
        drawRect(t.c(PINO_HONDO), Offset(c.x - w * 0.006f, c.y - s * 0.02f), Size(w * 0.012f, w * 0.01f))
        drawPath(copa, t.c(LANA))
        clipPath(copa) {
            cilindro(Rect(c.x - s * 0.48f, c.y - s * 0.32f, c.x + s * 0.48f, c.y + s * 0.7f), t, 1.2f, der)
            // Los pliegues de la lana: arrugas que bajan de la punta doblada.
            listOf(-0.15f, 0.08f, 0.26f).forEachIndexed { i, f ->
                val pl = Path().apply { moveTo(c.x + s * (f + 0.1f), c.y - s * 0.22f); cubicTo(c.x + s * (f + 0.12f), c.y + s * 0.05f, c.x + s * (f - 0.02f), c.y + s * 0.3f, c.x + s * (f + 0.05f), c.y + s * 0.52f) }
                drawPath(pl, t.s(0.26f), style = Stroke(1.6f * u))
                translate(-1.4f * u, 0f) { drawPath(pl, t.b(0.2f + 0.05f * i), style = Stroke(0.8f * u)) }
            }
            // La lana: un punteado de puntadas.
            for (k in 0 until 14) drawLine(t.s(0.14f), Offset(c.x - s * 0.4f + s * 0.06f * k, c.y + s * (0.1f + 0.3f * azar(9601, k))), Offset(c.x - s * 0.38f + s * 0.06f * k, c.y + s * (0.13f + 0.3f * azar(9601, k))), 0.6f * u)
        }
        // La sombra que la punta deja en la copa.
        clipPath(copa) { translate(s * 0.08f, s * 0.06f) { drawPath(punta, t.s(0.3f)) } }
        drawPath(punta, t.c(mezcla(LANA, Color(0xFF7A1E1A), 0.15f)))
        clipPath(punta) {
            drawRect(t.b(0.25f), Offset(c.x - s * 0.66f, c.y - s * 0.36f), Size(s * 0.7f, s * 0.14f))
            drawRect(t.s(0.25f), Offset(c.x - s * 0.66f, c.y + s * 0.1f), Size(s * 0.4f, s * 0.2f))
        }
        // El ruedo vuelto, con su borde y su lado de sombra.
        drawPath(ruedoP, t.c(mezcla(LANA, Color(0xFF7A1E1A), 0.25f)))
        clipPath(ruedoP) {
            drawRect(t.b(0.25f), ruedo.topLeft, Size(ruedo.width * 0.3f, ruedo.height))
            drawRect(t.s(0.28f), Offset(ruedo.left, ruedo.bottom - ruedo.height * 0.3f), Size(ruedo.width, ruedo.height * 0.3f))
            drawRect(t.s(0.22f), Offset(ruedo.right - ruedo.width * 0.25f, ruedo.top), Size(ruedo.width * 0.25f, ruedo.height))
            for (k in 0 until 9) drawLine(t.s(0.2f), Offset(ruedo.left + ruedo.width * (0.05f + 0.11f * k), ruedo.top), Offset(ruedo.left + ruedo.width * (0.07f + 0.11f * k), ruedo.bottom), 0.6f * u)
        }
        drawPath(ruedoP, t.b(0.3f), style = Stroke(0.8f * u))
        escarapelaPintada(Offset(c.x + s * 0.2f, c.y + s * 0.3f), s * 0.13f, t)
    }

    /** La pica apoyada de canto en el rincón de la derecha: el asta de fresno con su brillo, la moharra de hierro, cortadas por el borde. */
    private fun DrawScope.pica(w: Float, ch: Float) {
        val u = density
        val a = Offset(w * 0.935f, ch * SUP + ch * 0.005f)
        val z = Offset(w * 0.982f, ch * 0.3f)
        val t = tn(Offset(w * 0.96f, ch * 0.5f))
        val asta = Path().apply { moveTo(a.x, a.y); lineTo(z.x, z.y) }
        translate(w * 0.014f, w * 0.006f) { drawPath(asta, t.s(0.22f), style = Stroke(w * 0.018f)) }
        drawPath(asta, t.c(mezcla(PINO, PINO_HONDO, 0.3f)), style = Stroke(w * 0.018f))
        translate(-w * 0.003f, 0f) { drawPath(asta, t.b(0.35f), style = Stroke(w * 0.004f)) }
        translate(w * 0.004f, 0f) { drawPath(asta, t.s(0.25f), style = Stroke(w * 0.004f)) }
        // La moharra: el cubo con su anilla y la hoja de hoja de laurel, que se sale por arriba.
        val dir = Offset(z.x - a.x, z.y - a.y)
        val largo = sqrt(dir.x * dir.x + dir.y * dir.y)
        val n = Offset(dir.x / largo, dir.y / largo)
        val p = Offset(-n.y, n.x)
        val cubo = z
        val hoja0 = cubo + n * (w * 0.045f)
        val tip = hoja0 + n * (w * 0.2f)
        val hoja = Path().apply {
            moveTo(hoja0.x + p.x * w * 0.005f, hoja0.y + p.y * w * 0.005f)
            val m = hoja0 + n * (w * 0.05f)
            cubicTo(m.x + p.x * w * 0.042f, m.y + p.y * w * 0.042f, tip.x + p.x * w * 0.012f, tip.y + p.y * w * 0.01f, tip.x, tip.y)
            cubicTo(tip.x - p.x * w * 0.012f, tip.y - p.y * w * 0.012f, m.x - p.x * w * 0.042f, m.y - p.y * w * 0.042f, hoja0.x - p.x * w * 0.005f, hoja0.y - p.y * w * 0.005f)
            close()
        }
        translate(w * 0.014f, w * 0.006f) { drawPath(hoja, t.s(0.2f)) }
        drawLine(t.c(HIERRO), cubo, hoja0, w * 0.015f)
        drawLine(t.c(mezcla(HIERRO, HOJALATA, 0.3f)), cubo + n * (w * 0.006f), cubo + n * (w * 0.014f), w * 0.022f)
        drawPath(hoja, t.c(mezcla(HIERRO, HOJALATA, 0.55f)))
        drawLine(t.b(0.6f), hoja0, tip, 1.1f * u)
        clipPath(hoja) { drawRect(t.s(0.25f), Offset(hoja0.x, tip.y), Size(w * 0.05f, hoja0.y - tip.y + w * 0.02f)) }
        contacto(a + Offset(w * 0.006f, 0f), w * 0.03f, w * 0.006f, 0.4f)
    }

    // ---------------------------------------------------------------- en la repisa alta

    /** La pila de periódicos doblados, desparejos, atada con cordel cruzado y su nudo; el de encima con su cabecera y sus columnas. */
    private fun DrawScope.periodicos(pie: Offset, w: Float) {
        val u = density
        val t = tn(pie - Offset(0f, w * 0.05f))
        val der = luzDerecha(pie.x)
        val capa = w * 0.014f
        val n = 6
        val ancho = w * 0.15f
        val silueta = Path().apply { addRect(Rect(pie.x - ancho / 2 - w * 0.01f, pie.y - capa * n - w * 0.03f, pie.x + ancho / 2 + w * 0.01f, pie.y)) }
        sombraEnPared(silueta, pie, pie.y, 0.2f)
        contacto(Offset(pie.x + w * 0.01f, pie.y), ancho * 1.15f, w * 0.01f, 0.4f)
        var y = pie.y
        for (k in 0 until n) {
            val dx = w * 0.012f * (azar(9701, k) - 0.5f)
            val aw = ancho * (0.94f + 0.08f * azar(9702, k))
            val r = Rect(pie.x - aw / 2 + dx, y - capa, pie.x + aw / 2 + dx, y)
            val papel = mezcla(CARTEL, if (k % 2 == 0) KRAFT else CRUDO, 0.2f + 0.15f * azar(9703, k))
            drawRect(t.c(papel), r.topLeft, r.size)
            // El lomo doblado a la izquierda (redondeado) y las hojas sueltas a la derecha.
            drawRect(t.b(0.3f), r.topLeft, Size(r.width, 0.8f * u))
            drawRect(t.s(0.28f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
            for (j in 1..2) drawLine(t.s(0.15f), Offset(r.right - r.width * 0.3f, r.top + capa * j / 3f), Offset(r.right, r.top + capa * j / 3f), 0.5f * u)
            drawRect(if (der) t.b(0.2f) else t.s(0.18f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
            y -= capa
        }
        // El de encima, un poco girado: se le ve la cara con la cabecera.
        val top = Rect(pie.x - ancho * 0.48f, y - w * 0.012f, pie.x + ancho * 0.5f, y + 1f * u)
        rotate(-2f, top.center) {
            drawRect(t.c(mezcla(CARTEL, CRUDO, 0.4f)), top.topLeft, top.size)
            drawRect(t.c(TINTA).copy(alpha = 0.7f), Offset(top.left + top.width * 0.1f, top.top + top.height * 0.2f), Size(top.width * 0.8f, top.height * 0.18f))
            for (j in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(top.left + top.width * (0.1f + 0.28f * j), top.top + top.height * 0.55f), Offset(top.left + top.width * (0.32f + 0.28f * j), top.top + top.height * 0.55f), 1.2f * u)
            drawRect(t.b(0.4f), top.topLeft, Size(top.width, 0.9f * u))
        }
        // El cordel: una vuelta por delante, otra a lo ancho, y el nudo con sus cabos.
        val cordel = t.c(mezcla(KRAFT, CRUDO, 0.3f))
        val cx = pie.x + ancho * 0.12f
        drawLine(t.s(0.3f), Offset(cx + 1f * u, top.top), Offset(cx + 1f * u, pie.y), 1.6f * u)
        drawLine(cordel, Offset(cx, top.top), Offset(cx, pie.y), 1.3f * u)
        val cy = pie.y - capa * 3.5f
        drawLine(cordel, Offset(pie.x - ancho / 2, cy), Offset(pie.x + ancho / 2, cy + 1f * u), 1.3f * u)
        drawCircle(cordel, 2.2f * u, Offset(cx, top.top))
        drawPath(Path().apply { moveTo(cx, top.top); cubicTo(cx + w * 0.012f, top.top - w * 0.012f, cx + w * 0.02f, top.top - w * 0.004f, cx + w * 0.026f, top.top + w * 0.006f) }, cordel, style = Stroke(1.1f * u))
        drawPath(Path().apply { moveTo(cx, top.top); cubicTo(cx - w * 0.01f, top.top - w * 0.014f, cx - w * 0.02f, top.top - w * 0.01f, cx - w * 0.024f, top.top - w * 0.002f) }, cordel, style = Stroke(1.1f * u))
        filo(Rect(pie.x - ancho / 2, top.top, pie.x + ancho / 2, pie.y), t, der)
    }

    /**
     * El farol de hojalata con su vela: el pie, la caja con su vidrio por delante (y su puerta con bisagra y pestillo), el
     * costado de chapa con su calado de agujeros, el tejadillo cónico calado y la anilla. De noche la vela brilla y la luz sale
     * por el vidrio y por los agujeros.
     */
    private fun DrawScope.farol(pie: Offset, w: Float, ch: Float) {
        val u = density
        val o = esNoche
        val t = if (o) Tono(true, 0.95f) else tn(pie)
        val bw = w * 0.1f
        val caja = Rect(pie.x - bw / 2, pie.y - w * 0.15f, pie.x + bw / 2, pie.y - w * 0.018f)
        val lado = w * 0.022f
        val tejado = Path().apply {
            moveTo(caja.left - w * 0.008f, caja.top); lineTo(caja.right + lado + w * 0.004f, caja.top)
            lineTo(pie.x + w * 0.014f, caja.top - w * 0.06f); lineTo(pie.x - w * 0.01f, caja.top - w * 0.06f); close()
        }
        val anilla = Rect(pie.x - w * 0.016f, caja.top - w * 0.093f, pie.x + w * 0.016f, caja.top - w * 0.057f)
        val silueta = Path().apply { addRect(Rect(caja.left - w * 0.006f, caja.top, caja.right + lado, pie.y)); addPath(tejado); addOval(anilla) }
        if (!o) sombraEnPared(silueta, Offset(pie.x, pie.y - w * 0.1f), pie.y, 0.2f)
        contacto(Offset(pie.x + w * 0.012f, pie.y), bw * 1.4f, w * 0.012f, 0.42f)
        val lata = t.c(HOJALATA)
        // El pie: una bandeja de chapa con su reborde.
        val pb = Rect(caja.left - w * 0.008f, pie.y - w * 0.02f, caja.right + lado + w * 0.004f, pie.y)
        drawRect(lata, pb.topLeft, pb.size)
        drawRect(t.b(0.45f), pb.topLeft, Size(pb.width, 1f * u))
        drawRect(t.s(0.3f), Offset(pb.left, pb.bottom - pb.height * 0.35f), Size(pb.width, pb.height * 0.35f))
        // El costado derecho, en escorzo, con su calado de agujeros.
        val costado = poligono(caja.right, caja.top, caja.right + lado, caja.top - w * 0.006f, caja.right + lado, caja.bottom - w * 0.004f, caja.right, caja.bottom)
        drawPath(costado, if (o) mezcla(hondo(HOJALATA), HOJALATA, 0.35f) else mezcla(HOJALATA, Color(0xFF6E6F6A), 0.45f))
        for (fy in 0 until 6) for (fx in 0 until 2) {
            val p = Offset(caja.right + lado * (0.3f + 0.4f * fx), caja.top + (caja.height) * (0.15f + 0.14f * fy) - w * 0.003f * fx)
            if (o) { drawCircle(LLAMA.copy(alpha = 0.5f), 2.2f * u, p); drawCircle(Color(0xFFFFE6B0), 0.9f * u, p) }
            else drawCircle(sombraMueble(0.55f), 0.9f * u, p)
        }
        // El vidrio: de día deja ver la vela apagada; de noche, encendido.
        val vid = Rect(caja.left + w * 0.012f, caja.top + w * 0.012f, caja.right - w * 0.012f, caja.bottom - w * 0.01f)
        drawRect(lata, caja.topLeft, caja.size)
        cilindro(caja, t, 1.1f)
        if (!o) {
            drawRect(mezcla(YESO, Color(0xFF6E6F6A), 0.55f), vid.topLeft, vid.size)
            drawRect(sombraMueble(0.25f), Offset(vid.right - vid.width * 0.35f, vid.top), Size(vid.width * 0.35f, vid.height))
        } else {
            drawRect(Brush.radialGradient(listOf(Color(0xFFFFE2A8), LLAMA, Color(0xFFC07A30)), Offset(vid.center.x, vid.bottom - vid.height * 0.42f), vid.width * 0.75f), vid.topLeft, vid.size)
        }
        // La vela de sebo, a medio gastar, con su chorrera, y la llama.
        val vela = Rect(pie.x - w * 0.011f, vid.bottom - vid.height * 0.45f, pie.x + w * 0.011f, vid.bottom)
        drawRect(if (o) Color(0xFFF6E2B0) else LOZA, vela.topLeft, vela.size)
        drawRect(if (o) LLAMA.copy(alpha = 0.4f) else sombraMueble(0.2f), Offset(vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height))
        drawOval(if (o) Color(0xFFF6E2B0) else LOZA, Offset(vela.left - w * 0.003f, vela.top + vela.height * 0.2f), Size(w * 0.006f, vela.height * 0.35f))
        drawLine(TINTA, Offset(pie.x, vela.top), Offset(pie.x + w * 0.001f, vela.top - w * 0.006f), 0.8f * u)
        if (o) {
            val lc = Offset(pie.x, vela.top - w * 0.014f)
            drawCircle(Color(0xFFFFE8B8).copy(alpha = 0.6f), w * 0.016f, lc)
            drawOval(LLAMA, Offset(lc.x - w * 0.006f, lc.y - w * 0.013f), Size(w * 0.012f, w * 0.024f))
            drawOval(Color(0xFFFFF6DC), Offset(lc.x - w * 0.003f, lc.y - w * 0.006f), Size(w * 0.006f, w * 0.014f))
        }
        // El hollín arriba del vidrio, el reflejo y el marco con su travesaño, la bisagra y el pestillo.
        drawRect(Color(0xFF2E2A28).copy(alpha = if (o) 0.35f else 0.25f), vid.topLeft, Size(vid.width, vid.height * 0.16f))
        drawPath(poligono(vid.left + vid.width * 0.08f, vid.bottom, vid.left + vid.width * 0.3f, vid.top, vid.left + vid.width * 0.4f, vid.top, vid.left + vid.width * 0.18f, vid.bottom), luzMueble(if (o) 0.18f else 0.3f))
        drawRect(lata, Offset(vid.left, vid.center.y - w * 0.0025f), Size(vid.width, w * 0.005f))
        drawRect(t.s(0.35f), vid.topLeft, vid.size, style = Stroke(1.2f * u))
        drawRect(t.b(0.5f), Offset(caja.left, caja.top), Size(caja.width, 1.1f * u))
        drawRect(t.c(HIERRO), Offset(caja.left + w * 0.002f, caja.top + caja.height * 0.2f), Size(w * 0.006f, caja.height * 0.12f))
        drawRect(t.c(HIERRO), Offset(caja.left + w * 0.002f, caja.top + caja.height * 0.68f), Size(w * 0.006f, caja.height * 0.12f))
        drawCircle(t.c(HIERRO), w * 0.004f, Offset(caja.right - w * 0.006f, caja.center.y))
        // El tejadillo cónico con su calado y la anilla.
        drawPath(tejado, lata)
        clipPath(tejado) {
            drawRect(t.b(0.3f), Offset(caja.left - w * 0.01f, caja.top - w * 0.06f), Size(bw * 0.35f, w * 0.06f))
            drawRect(t.s(0.3f), Offset(pie.x + w * 0.01f, caja.top - w * 0.06f), Size(bw, w * 0.06f))
        }
        for (fy in 0 until 2) for (fx in 0 until 5 - fy * 2) {
            val p = Offset(pie.x - w * 0.026f + w * 0.013f * (fx + fy) , caja.top - w * 0.018f - w * 0.02f * fy)
            if (o) { drawCircle(LLAMA.copy(alpha = 0.55f), 2f * u, p); drawCircle(Color(0xFFFFE6B0), 0.9f * u, p) }
            else drawCircle(sombraMueble(0.5f), 0.9f * u, p)
        }
        drawOval(t.c(HIERRO), anilla.topLeft, anilla.size, style = Stroke(1.8f * u))
        drawArc(t.b(0.5f), 160f, 90f, false, anilla.topLeft, anilla.size, style = Stroke(0.7f * u))
    }

    /** La jarra de estaño: el cuerpo panzudo, el asa a la derecha, la tapa con su pulgar, golpes y el brillo apagado del metal. */
    private fun DrawScope.jarra(pie: Offset, w: Float) {
        val u = density
        val t = tn(pie - Offset(0f, w * 0.05f))
        val der = luzDerecha(pie.x)
        val jw = w * 0.07f; val jh = w * 0.105f
        val cuerpo = Path().apply {
            moveTo(pie.x - jw * 0.42f, pie.y)
            cubicTo(pie.x - jw * 0.62f, pie.y - jh * 0.35f, pie.x - jw * 0.42f, pie.y - jh * 0.75f, pie.x - jw * 0.36f, pie.y - jh * 0.9f)
            lineTo(pie.x + jw * 0.36f, pie.y - jh * 0.9f)
            cubicTo(pie.x + jw * 0.42f, pie.y - jh * 0.75f, pie.x + jw * 0.62f, pie.y - jh * 0.35f, pie.x + jw * 0.42f, pie.y)
            close()
        }
        val asa = Path().apply { moveTo(pie.x + jw * 0.38f, pie.y - jh * 0.8f); cubicTo(pie.x + jw * 0.95f, pie.y - jh * 0.82f, pie.x + jw * 0.92f, pie.y - jh * 0.3f, pie.x + jw * 0.46f, pie.y - jh * 0.22f) }
        sombraEnPared(Path().apply { addPath(cuerpo) }, pie, pie.y, 0.2f)
        contacto(Offset(pie.x + w * 0.008f, pie.y), jw * 1.3f, w * 0.01f, 0.42f)
        translate(w * 0.006f, w * 0.004f) { drawPath(asa, t.s(0.25f), style = Stroke(w * 0.009f)) }
        drawPath(asa, t.c(ESTANO), style = Stroke(w * 0.009f, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.3f), style = Stroke(w * 0.002f))
        drawPath(cuerpo, t.c(ESTANO))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - jw * 0.6f, pie.y - jh, pie.x + jw * 0.6f, pie.y), t, 1.4f, der)
            listOf(0.25f, 0.62f).forEach { f -> drawRect(t.s(0.22f), Offset(pie.x - jw, pie.y - jh * f), Size(jw * 2, 1.2f * u)); drawRect(t.b(0.25f), Offset(pie.x - jw, pie.y - jh * f - 1f * u), Size(jw * 2, 0.7f * u)) }
            drawOval(t.s(0.18f), Offset(pie.x + jw * 0.05f, pie.y - jh * 0.5f), Size(jw * 0.18f, jh * 0.1f))
        }
        // La tapa con su bisagra y el pulgar.
        val tapa = Rect(pie.x - jw * 0.4f, pie.y - jh, pie.x + jw * 0.4f, pie.y - jh * 0.88f)
        drawOval(t.c(mezcla(ESTANO, LUZ_MUEBLE, 0.15f)), tapa.topLeft, tapa.size)
        drawArc(t.s(0.3f), 0f, 180f, false, tapa.topLeft, tapa.size, style = Stroke(1f * u))
        drawRect(t.c(ESTANO), Offset(pie.x + jw * 0.3f, pie.y - jh * 1.04f), Size(jw * 0.18f, jh * 0.08f))
        drawCircle(t.b(0.5f), 1f * u, Offset(pie.x - jw * 0.15f, pie.y - jh * 0.96f))
    }

    /** El tintero de loza con su filete azul, la tinta en la boca y dos plumas de ganso; al lado, la salvadera de estaño. */
    private fun DrawScope.tintero(pie: Offset, w: Float) {
        val u = density
        val t = tn(pie - Offset(0f, w * 0.03f))
        val der = luzDerecha(pie.x)
        val tw = w * 0.05f; val th = w * 0.032f
        // Las plumas, por detrás: el cañón y las barbas, una a cada lado.
        listOf(Triple(-1f, 0.11f, 0.03f), Triple(1f, 0.13f, 0.045f)).forEachIndexed { i, (s, alto, abre) ->
            val a = Offset(pie.x + s * tw * 0.1f, pie.y - th * 0.9f)
            val z = Offset(pie.x - w * 0.02f + s * w * abre, pie.y - th - w * alto)
            val pluma = Path().apply {
                moveTo(a.x, a.y)
                cubicTo(a.x - s * w * 0.004f, a.y - w * 0.04f, z.x - s * w * 0.03f, z.y + w * 0.04f, z.x, z.y)
                cubicTo(z.x + s * w * 0.016f, z.y + w * 0.03f, a.x + s * w * 0.012f, a.y - w * 0.045f, a.x + s * w * 0.003f, a.y - w * 0.02f)
                close()
            }
            translate(w * 0.012f, w * 0.006f) { drawPath(pluma, t.s(0.16f)) }
            drawPath(pluma, t.c(if (i == 0) CRUDO else mezcla(CRUDO, Color(0xFF8C8A84), 0.45f)))
            clipPath(pluma) { for (k in 0 until 7) drawLine(t.s(0.2f), Offset(z.x - w * 0.02f, z.y + w * 0.012f * k), Offset(z.x + w * 0.02f, z.y + w * 0.012f * k - w * 0.012f), 0.5f * u) }
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x - s * w * 0.004f, a.y - w * 0.04f, z.x - s * w * 0.024f, z.y + w * 0.035f, z.x, z.y) }, t.c(mezcla(KRAFT, TINTA, 0.3f)), style = Stroke(1f * u))
        }
        contacto(Offset(pie.x + w * 0.006f, pie.y), tw * 1.3f, w * 0.008f, 0.42f)
        val cuerpo = Path().apply {
            moveTo(pie.x - tw / 2, pie.y); lineTo(pie.x - tw / 2, pie.y - th * 0.7f)
            cubicTo(pie.x - tw / 2, pie.y - th, pie.x - tw * 0.2f, pie.y - th, pie.x - tw * 0.16f, pie.y - th)
            lineTo(pie.x + tw * 0.16f, pie.y - th)
            cubicTo(pie.x + tw * 0.2f, pie.y - th, pie.x + tw / 2, pie.y - th, pie.x + tw / 2, pie.y - th * 0.7f)
            lineTo(pie.x + tw / 2, pie.y); close()
        }
        drawPath(cuerpo, t.c(LOZA))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - tw / 2, pie.y - th, pie.x + tw / 2, pie.y), t, 1.3f, der)
            drawRect(t.c(AZUL_P), Offset(pie.x - tw / 2, pie.y - th * 0.35f), Size(tw, th * 0.1f))
            drawRect(t.c(AZUL_P), Offset(pie.x - tw / 2, pie.y - th * 0.18f), Size(tw, th * 0.04f))
            // La chorrera de tinta.
            drawRect(t.c(TINTA).copy(alpha = 0.7f), Offset(pie.x + tw * 0.08f, pie.y - th), Size(tw * 0.05f, th * 0.4f))
        }
        drawOval(t.c(TINTA), Offset(pie.x - tw * 0.16f, pie.y - th - w * 0.003f), Size(tw * 0.32f, w * 0.006f))
        // La salvadera: un reloj de arena de estaño con su tapa calada.
        val s = Offset(pie.x + w * 0.05f, pie.y)
        val sw = w * 0.026f; val sh = w * 0.04f
        contacto(Offset(s.x + w * 0.004f, s.y), sw * 1.4f, w * 0.007f, 0.42f)
        val sal = Path().apply {
            moveTo(s.x - sw / 2, s.y); lineTo(s.x - sw * 0.3f, s.y - sh * 0.45f); lineTo(s.x - sw / 2, s.y - sh)
            lineTo(s.x + sw / 2, s.y - sh); lineTo(s.x + sw * 0.3f, s.y - sh * 0.45f); lineTo(s.x + sw / 2, s.y); close()
        }
        drawPath(sal, t.c(ESTANO))
        clipPath(sal) { cilindro(Rect(s.x - sw / 2, s.y - sh, s.x + sw / 2, s.y), t, 1.3f, der) }
        drawOval(t.c(mezcla(ESTANO, TINTA, 0.3f)), Offset(s.x - sw / 2, s.y - sh - w * 0.003f), Size(sw, w * 0.006f))
        for (k in 0 until 3) drawCircle(t.c(TINTA), 0.5f * u, Offset(s.x - sw * 0.25f + sw * 0.25f * k, s.y - sh))
    }

    /** La campanilla de mano para pedir orden: la copa de bronce con su borde, el mango de madera torneado y su brillo. */
    private fun DrawScope.campanilla(pie: Offset, w: Float) {
        val u = density
        val t = tn(pie - Offset(0f, w * 0.04f))
        val der = luzDerecha(pie.x)
        val bw = w * 0.05f; val bh = w * 0.04f
        val copa = Path().apply {
            moveTo(pie.x - bw / 2, pie.y)
            cubicTo(pie.x - bw * 0.42f, pie.y - bh * 0.25f, pie.x - bw * 0.32f, pie.y - bh * 0.6f, pie.x - bw * 0.28f, pie.y - bh)
            lineTo(pie.x + bw * 0.28f, pie.y - bh)
            cubicTo(pie.x + bw * 0.32f, pie.y - bh * 0.6f, pie.x + bw * 0.42f, pie.y - bh * 0.25f, pie.x + bw / 2, pie.y)
            close()
        }
        contacto(Offset(pie.x + w * 0.006f, pie.y), bw * 1.3f, w * 0.008f, 0.42f)
        val bronce = Color(0xFFB08A3E)
        drawPath(copa, t.c(bronce))
        clipPath(copa) { cilindro(Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y), t, 1.6f, der) }
        drawOval(t.c(mezcla(bronce, HIERRO, 0.3f)), Offset(pie.x - bw / 2, pie.y - w * 0.004f), Size(bw, w * 0.007f))
        val mango = Rect(pie.x - w * 0.007f, pie.y - bh - w * 0.045f, pie.x + w * 0.007f, pie.y - bh)
        drawRect(t.c(PINO_HONDO), mango.topLeft, mango.size)
        drawOval(t.c(PINO_HONDO), Offset(pie.x - w * 0.011f, mango.top - w * 0.012f), Size(w * 0.022f, w * 0.018f))
        clipRect(mango.left, mango.top - w * 0.012f, mango.right + w * 0.004f, mango.bottom) { cilindro(Rect(mango.left - w * 0.004f, mango.top - w * 0.012f, mango.right + w * 0.004f, mango.bottom), t, 1.2f, der) }
        drawRect(t.c(bronce), Offset(pie.x - w * 0.009f, pie.y - bh - w * 0.004f), Size(w * 0.018f, w * 0.004f))
    }

    /** La hogaza empezada sobre su tabla: la corteza con sus cortes, la cara del corte con la miga, una rebanada y el cuchillo. */
    private fun DrawScope.hogaza(pie: Offset, w: Float) {
        val u = density
        val t = tn(pie - Offset(0f, w * 0.03f))
        val der = luzDerecha(pie.x)
        // La tabla de cortar.
        val tb = Rect(pie.x - w * 0.085f, pie.y - w * 0.012f, pie.x + w * 0.07f, pie.y)
        contacto(Offset(tb.center.x + w * 0.01f, pie.y), tb.width * 1.1f, w * 0.01f, 0.42f)
        drawRect(t.c(mezcla(PINO, PINO_LUZ, 0.3f)), tb.topLeft, tb.size)
        drawRect(t.b(0.4f), tb.topLeft, Size(tb.width, 0.9f * u))
        drawRect(t.s(0.25f), Offset(tb.left, tb.bottom - 1.2f * u), Size(tb.width, 1.2f * u))
        // La hogaza: un domo de corteza, cortado por la derecha.
        val hw = w * 0.11f; val hh = w * 0.06f
        val base = tb.top
        val domo = Path().apply {
            moveTo(pie.x - hw * 0.62f, base)
            cubicTo(pie.x - hw * 0.66f, base - hh * 0.9f, pie.x - hw * 0.1f, base - hh * 1.1f, pie.x + hw * 0.2f, base - hh * 0.9f)
            lineTo(pie.x + hw * 0.24f, base); close()
        }
        drawPath(domo, t.c(PAN))
        clipPath(domo) {
            drawRect(t.b(0.25f), Offset(pie.x - hw * 0.7f, base - hh * 1.1f), Size(hw * 0.5f, hh * 0.6f))
            drawRect(t.s(0.25f), Offset(pie.x - hw * 0.7f, base - hh * 0.3f), Size(hw, hh * 0.3f))
            drawOval(t.c(mezcla(PAN, MIGA, 0.4f)).copy(alpha = 0.5f), Offset(pie.x - hw * 0.4f, base - hh * 1.0f), Size(hw * 0.4f, hh * 0.25f))
            // Los cortes del greñado.
            listOf(-0.4f, -0.1f).forEach { f ->
                val p = Path().apply { moveTo(pie.x + hw * f, base - hh * 0.96f); cubicTo(pie.x + hw * (f + 0.06f), base - hh * 0.7f, pie.x + hw * (f + 0.04f), base - hh * 0.5f, pie.x + hw * (f + 0.12f), base - hh * 0.35f) }
                drawPath(p, t.c(mezcla(PAN, MIGA, 0.6f)), style = Stroke(1.8f * u))
                drawPath(p, t.s(0.3f), style = Stroke(0.6f * u))
            }
        }
        // La cara del corte: la miga clara con sus ojos.
        val corte = Path().apply { moveTo(pie.x + hw * 0.2f, base - hh * 0.9f); cubicTo(pie.x + hw * 0.32f, base - hh * 0.85f, pie.x + hw * 0.34f, base - hh * 0.3f, pie.x + hw * 0.26f, base); lineTo(pie.x + hw * 0.24f, base); close() }
        drawPath(corte, t.c(MIGA))
        drawPath(corte, t.c(PAN), style = Stroke(1.2f * u))
        for (k in 0 until 5) drawCircle(t.s(0.25f), 0.7f * u, Offset(pie.x + hw * (0.24f + 0.05f * azar(9801, k)), base - hh * (0.2f + 0.6f * azar(9802, k))))
        // Una rebanada tumbada delante.
        val reb = Rect(pie.x + hw * 0.32f, base - w * 0.012f, pie.x + hw * 0.62f, base)
        drawOval(t.c(PAN), reb.topLeft, reb.size)
        drawOval(t.c(MIGA), Offset(reb.left + 1.2f * u, reb.top + 1.2f * u), Size(reb.width - 2.4f * u, reb.height - 2.4f * u))
        // El cuchillo, en diagonal delante: la hoja con su filo y el mango de madera con sus remaches.
        val a = Offset(pie.x - hw * 0.75f, pie.y - w * 0.004f)
        val z = Offset(pie.x + hw * 0.1f, pie.y - w * 0.016f)
        drawLine(t.s(0.3f), a + Offset(w * 0.006f, w * 0.004f), z + Offset(w * 0.006f, w * 0.004f), w * 0.009f)
        drawLine(t.c(mezcla(HOJALATA, LOZA, 0.2f)), a, z, w * 0.008f, StrokeCap.Round)
        drawLine(t.b(0.6f), a - Offset(0f, w * 0.002f), z - Offset(0f, w * 0.002f), 0.7f * u)
        val m0 = Offset(pie.x + hw * 0.1f, pie.y - w * 0.016f)
        val m1 = Offset(pie.x + hw * 0.62f, pie.y - w * 0.026f)
        drawLine(t.c(PINO_HONDO), m0, m1, w * 0.012f, StrokeCap.Round)
        drawLine(t.b(0.3f), m0 - Offset(0f, w * 0.003f), m1 - Offset(0f, w * 0.003f), 0.7f * u)
        listOf(0.3f, 0.7f).forEach { f -> drawCircle(t.c(HOJALATA), 0.9f * u, Offset(m0.x + (m1.x - m0.x) * f, m0.y + (m1.y - m0.y) * f)) }
    }

    // ---------------------------------------------------------------- al pie

    /** El tambor, cortado por el borde: la caja pintada, los aros, las cuerdas en zigzag con sus tirantes y las dos baquetas encima. */
    private fun DrawScope.tambor(pie: Offset, w: Float) {
        val u = density
        val t = tn(pie - Offset(0f, w * 0.06f))
        val dw = w * 0.24f; val dh = w * 0.13f
        val r = Rect(pie.x - dw / 2, pie.y - dh, pie.x + dw / 2, pie.y)
        val e = w * 0.035f
        contacto(Offset(pie.x + w * 0.02f, pie.y), dw * 1.15f, w * 0.03f, 0.4f)
        drawRect(t.c(AZUL_P), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            cilindro(r, t, 1.3f)
            // Las cuerdas en zigzag y sus tirantes de cuero.
            val vueltas = 7
            for (k in 0 until vueltas) {
                val x0 = r.left + r.width * k / vueltas
                val x1 = r.left + r.width * (k + 0.5f) / vueltas
                val x2 = r.left + r.width * (k + 1) / vueltas
                listOf(Offset(x0, r.top + e * 0.6f) to Offset(x1, r.bottom - e * 0.6f), Offset(x1, r.bottom - e * 0.6f) to Offset(x2, r.top + e * 0.6f)).forEach { (p, q) ->
                    drawLine(t.s(0.3f), p + Offset(1f * u, 1f * u), q + Offset(1f * u, 1f * u), 1.4f * u)
                    drawLine(t.c(mezcla(KRAFT, CRUDO, 0.55f)), p, q, 1.2f * u)
                }
                drawRect(t.c(KRAFT), Offset(x1 - w * 0.006f, r.bottom - e * 1.6f), Size(w * 0.012f, e * 0.7f))
            }
            // Los aros de arriba y de abajo, de madera pintada de rojo.
            drawRect(t.c(LANA), Offset(r.left, r.top), Size(r.width, e * 0.55f))
            drawRect(t.c(LANA), Offset(r.left, r.bottom - e * 0.55f), Size(r.width, e * 0.55f))
            drawRect(t.b(0.35f), Offset(r.left, r.top), Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - e * 0.55f - 1f * u), Size(r.width, 1f * u))
            pinceladas(r, if (esNoche) NOCHE else CRUDO, 9901, 6, u, 0.1f)
        }
        // El parche de piel, visto desde arriba.
        drawOval(t.c(mezcla(CRUDO, KRAFT, 0.25f)), Offset(r.left, r.top - e * 0.5f), Size(r.width, e))
        drawOval(t.c(LANA), Offset(r.left, r.top - e * 0.5f), Size(r.width, e), style = Stroke(2f * u))
        drawOval(t.c(KRAFT).copy(alpha = 0.3f), Offset(r.center.x - r.width * 0.2f, r.top - e * 0.2f), Size(r.width * 0.3f, e * 0.4f))
        // Las baquetas cruzadas encima.
        listOf(Offset(-0.35f, -0.1f) to Offset(0.4f, 0.12f), Offset(-0.25f, 0.2f) to Offset(0.42f, -0.25f)).forEach { (a, z) ->
            val p = Offset(r.center.x + r.width * a.x, r.top + e * a.y)
            val q = Offset(r.center.x + r.width * z.x, r.top + e * z.y)
            drawLine(t.s(0.3f), p + Offset(1.5f * u, 2f * u), q + Offset(1.5f * u, 2f * u), w * 0.009f)
            drawLine(t.c(mezcla(PINO, PINO_HONDO, 0.4f)), p, q, w * 0.008f, StrokeCap.Round)
            drawCircle(t.c(mezcla(PINO, PINO_HONDO, 0.4f)), w * 0.007f, q)
            drawLine(t.b(0.35f), p - Offset(0f, w * 0.002f), q - Offset(0f, w * 0.002f), 0.6f * u)
        }
    }

    /** El cesto de ropa, cortado por el borde: el mimbre trenzado y la ropa blanca amontonada, con un paño de rayas que cuelga. */
    private fun DrawScope.cesto(pie: Offset, w: Float) {
        val u = density
        val t = tn(pie - Offset(0f, w * 0.06f))
        val cw = w * 0.26f; val chh = w * 0.1f
        val cuerpo = poligono(pie.x - cw / 2, pie.y - chh, pie.x + cw / 2, pie.y - chh, pie.x + cw * 0.44f, pie.y, pie.x - cw * 0.44f, pie.y)
        contacto(Offset(pie.x, pie.y), cw * 1.1f, w * 0.028f, 0.4f)
        // La ropa amontonada: lomos de tela blanca con sus pliegues, y el paño de rayas azules.
        val ropa = listOf(Offset(-0.32f, 0.6f), Offset(-0.08f, 0.85f), Offset(0.18f, 0.7f), Offset(0.38f, 0.55f))
        ropa.forEachIndexed { i, f ->
            val c = Offset(pie.x + cw * f.x, pie.y - chh - w * 0.01f)
            val r = cw * 0.2f * f.y + cw * 0.08f
            drawOval(t.c(if (i == 2) mezcla(CRUDO, AZUL_P, 0.15f) else CRUDO), Offset(c.x - r, c.y - r * 0.6f), Size(r * 2, r * 1.2f))
            drawArc(t.s(0.22f), 10f, 160f, false, Offset(c.x - r, c.y - r * 0.6f), Size(r * 2, r * 1.2f), style = Stroke(1.2f * u))
            drawArc(t.b(0.4f), 190f, 90f, false, Offset(c.x - r * 0.8f, c.y - r * 0.5f), Size(r * 1.6f, r), style = Stroke(1f * u))
            if (i == 2) for (k in 0 until 3) drawLine(t.c(AZUL_P), Offset(c.x - r * 0.7f, c.y - r * 0.3f + r * 0.25f * k), Offset(c.x + r * 0.6f, c.y - r * 0.35f + r * 0.25f * k), 1.2f * u)
        }
        drawPath(cuerpo, t.c(MIMBRE))
        clipPath(cuerpo) {
            // El trenzado: hileras de mimbre que pasan por delante y por detrás de las varas.
            val filas = 6
            for (k in 0 until filas) {
                val y = pie.y - chh + chh * k / filas
                drawRect(t.s(0.25f), Offset(pie.x - cw / 2, y + chh / filas - 1f * u), Size(cw, 1f * u))
                for (j in 0 until 12) {
                    val x = pie.x - cw / 2 + cw * j / 12f
                    if ((j + k) % 2 == 0) drawRect(t.b(0.2f), Offset(x, y), Size(cw / 12f, chh / filas * 0.6f))
                    else drawRect(t.s(0.12f), Offset(x, y), Size(cw / 12f, chh / filas))
                }
            }
            drawRect(t.s(0.25f), Offset(pie.x + cw * 0.1f, pie.y - chh), Size(cw * 0.4f, chh))
        }
        // El borde trenzado de arriba.
        drawRect(t.c(mezcla(MIMBRE, PINO_HONDO, 0.2f)), Offset(pie.x - cw / 2 - w * 0.004f, pie.y - chh - w * 0.008f), Size(cw + w * 0.008f, w * 0.012f))
        for (j in 0 until 18) drawLine(t.s(0.3f), Offset(pie.x - cw / 2 + cw * j / 18f, pie.y - chh - w * 0.008f), Offset(pie.x - cw / 2 + cw * (j + 0.6f) / 18f, pie.y - chh + w * 0.004f), 0.7f * u)
        // Un paño que cuelga por el borde.
        val pano = poligono(pie.x - cw * 0.2f, pie.y - chh, pie.x - cw * 0.05f, pie.y - chh, pie.x - cw * 0.07f, pie.y - chh * 0.45f, pie.x - cw * 0.16f, pie.y - chh * 0.4f)
        drawPath(pano, t.c(CRUDO))
        drawPath(pano, t.s(0.15f))
        drawLine(t.c(AZUL_P), Offset(pie.x - cw * 0.18f, pie.y - chh * 0.7f), Offset(pie.x - cw * 0.06f, pie.y - chh * 0.72f), 1.2f * u)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = (s + 435545) * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega del farol.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.06f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro && (!dentro || centro.x < LUZ.x)
        esNoche = oscuro
        if (c.vacio) { vacio(w, h, c, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> saco(w, h, c, t, der)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> cajaListones(w, h, c, m, t, der)
                    1 -> cesta(w, h, c, m, t, der)
                    else -> legajo(w, h, c, m, t, der)
                }
            }
        }
    }

    /**
     * La sombra de lo guardado: su silueta [p] corrida a la derecha y abajo sobre la pared (sin pasar de la tabla) y la
     * sombra de contacto en la tabla, de [ancho] centrada en [cx].
     */
    private fun DrawScope.sombraDe(p: Path, w: Float, h: Float, t: Tono, ancho: Float, cx: Float) {
        clipRect(-w, -h, w * 2f, h * PISO) {
            translate(w * 0.04f, h * 0.03f) { drawPath(p, t.s(0.2f)) }
            translate(w * 0.065f, h * 0.05f) { drawPath(p, t.s(0.1f)) }
        }
        contacto(Offset(cx + w * 0.03f, h * PISO), ancho * 1.06f, h * 0.045f, 0.42f)
    }

    /**
     * El cartel impreso con el tema, colgado del canto de la tabla por debajo de lo guardado: el papel con su sombra en la
     * pared, dos tachuelas en el canto, el doble filete entre ellas (encima del texto) y una esquina de abajo levantada, del
     * lado que diga la semilla. De noche, más hundido cuanto más lejos del farol, sin bajar de 4.5:1 con la tinta.
     */
    private fun DrawScope.cartelTema(w: Float, h: Float, t: Tono, semilla: Int, papel: Color = CARTEL) {
        val u = density
        val rr = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val r = Rect(rr.left - w * 0.006f, h * (CANTO_T + 0.012f), rr.right + w * 0.006f, rr.bottom + h * 0.01f)
        val giro = (azar(semilla, 1) - 0.5f) * 1.2f
        val izq = azar(semilla, 2) > 0.5f
        val e = r.height * 0.24f
        val hoja = if (izq) poligono(r.left, r.top, r.right, r.top, r.right, r.bottom, r.left + e, r.bottom, r.left, r.bottom - e * 0.7f)
        else poligono(r.left, r.top, r.right, r.top, r.right, r.bottom - e * 0.7f, r.right - e, r.bottom, r.left, r.bottom)
        rotate(giro, Offset(r.center.x, r.top)) {
            // La sombra en la pared y en el canto: el papel cuelga separado arriba y más abajo.
            translate(w * 0.014f, h * 0.035f) { drawPath(hoja, t.s(0.26f)) }
            val fondo = if (t.o) mezcla(mezcla(papel, PAPEL_LEJOS, 0.9f), mezcla(papel, LLAMA, 0.1f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(papel)
            drawPath(hoja, fondo)
            clipPath(hoja) {
                if (!t.o) pinceladas(r, KRAFT, semilla, 4, u, 0.06f)
                drawRect(t.s(0.1f), Offset(r.right - r.width * 0.035f, r.top), Size(r.width * 0.035f, r.height))
                drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
                // La mancha de engrudo donde va cada tachuela.
                listOf(r.left + w * 0.03f, r.right - w * 0.03f).forEach { x -> drawOval(t.c(KRAFT).copy(alpha = if (t.o) 0.08f else 0.18f), Offset(x - w * 0.03f, r.top - h * 0.01f), Size(w * 0.06f, h * 0.05f)) }
            }
            // El doble filete de imprenta, entre las tachuelas, por encima del texto.
            val fy = r.top + (rr.top - r.top) * 0.45f
            drawRect(NEGRO.copy(alpha = 0.72f), Offset(r.left + w * 0.065f, fy - 0.8f * u), Size(r.width - w * 0.13f, 1.4f * u))
            drawRect(NEGRO.copy(alpha = 0.5f), Offset(r.left + w * 0.065f, fy + 1.4f * u), Size(r.width - w * 0.13f, 0.5f * u))
            // La esquina levantada: el revés del papel, que hace su sombra.
            val doblez = if (izq) poligono(r.left, r.bottom - e * 0.7f, r.left + e, r.bottom, r.left + e * 0.35f, r.bottom - e * 0.15f)
            else poligono(r.right, r.bottom - e * 0.7f, r.right - e, r.bottom, r.right - e * 0.35f, r.bottom - e * 0.15f)
            translate(1.5f * u, 2.5f * u) { drawPath(doblez, t.s(0.3f)) }
            drawPath(doblez, t.c(mezcla(KRAFT, papel, 0.45f)))
            drawPath(doblez, t.b(0.35f), style = Stroke(0.6f * u))
            // Las dos tachuelas, clavadas en el canto.
            listOf(r.left + w * 0.03f, r.right - w * 0.03f).forEach { x -> clavo(Offset(x, fy), 1.5f * u, t) }
        }
    }

    /**
     * La caja de listones de pino, alta: los cuadernos de quejas de pie dentro (se ven por las rendijas y asoman por arriba,
     * de alturas y colores distintos, alguno ladeado), cuatro listones con su rendija, los montantes de las esquinas con sus
     * clavos, las asas de cuerda y la cinta (tricolor, roja o azul) con su lazo grande en el frente.
     */
    private fun DrawScope.cajaListones(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val caja = Rect(w * 0.1f, h * 0.27f, w * 0.9f, piso)
        val pino = when (Math.floorMod(m ushr 10, 3)) { 0 -> PINO; 1 -> mezcla(PINO, Color(0xFF9C9488), 0.45f); else -> mezcla(PINO, Color(0xFFB0683A), 0.3f) }
        val n = c.n.coerceIn(1, 10)
        val cuantos = (3 + n).coerceAtMost(10)
        val cw = (caja.width - w * 0.08f) / cuantos
        val ladeado = Math.floorMod(m ushr 11, cuantos)
        // La silueta (para la sombra): la caja y los cuadernos que asoman.
        val sil = Path().apply { addRect(caja); addRect(Rect(caja.left + w * 0.04f, caja.top - h * (0.1f + 0.012f * n), caja.right - w * 0.04f, caja.top)) }
        sombraDe(sil, w, h, t, caja.width, caja.center.x)
        // Las asas de cuerda, a los costados.
        listOf(-1f, 1f).forEach { s ->
            val x = if (s < 0) caja.left else caja.right
            val asa = Path().apply { moveTo(x, caja.top + h * 0.07f); cubicTo(x + s * w * 0.06f, caja.top + h * 0.05f, x + s * w * 0.06f, caja.top + h * 0.22f, x, caja.top + h * 0.2f) }
            translate(w * 0.008f, h * 0.012f) { drawPath(asa, t.s(0.3f), style = Stroke(3f * u)) }
            drawPath(asa, t.c(mezcla(KRAFT, CRUDO, 0.35f)), style = Stroke(2.6f * u, cap = StrokeCap.Round))
            drawPath(asa, t.s(0.3f), style = Stroke(2.6f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1f * u, 2.2f * u))))
        }
        // El interior oscuro de la caja.
        drawRect(t.c(PINO_HONDO), caja.topLeft, caja.size)
        drawRect(t.s(0.4f), caja.topLeft, Size(caja.width, caja.height * 0.4f))
        // Los cuadernos, de pie dentro de la caja.
        for (i in 0 until cuantos) {
            val x = caja.left + w * 0.04f + cw * i
            val alto = h * (0.06f + 0.014f * n) * (0.45f + 0.75f * azar(c.semilla, i))
            val top = caja.top - alto
            val col = CUADERNOS[Math.floorMod(i * 3 + (m ushr 5), CUADERNOS.size)]
            val g = if (i == ladeado) (if (i < cuantos / 2) -11f else 11f) else (azar(c.semilla, i + 20) - 0.5f) * 4f
            rotate(g, Offset(x + cw / 2, piso)) {
                drawRect(t.c(col), Offset(x, top), Size(cw - 1.2f * u, piso - top - h * 0.01f))
                drawRect(t.b(0.4f), Offset(x, top), Size(cw - 1.2f * u, 0.9f * u))
                drawRect(t.s(0.25f), Offset(x + cw * 0.7f, top), Size(cw * 0.3f - 1.2f * u, piso - top))
                // La etiqueta escrita a mano y la costura del lomo.
                if (cw > 6f * u) drawRect(t.c(CRUDO).copy(alpha = 0.8f), Offset(x + cw * 0.2f, top + h * 0.025f), Size(cw * 0.45f, h * 0.022f))
                drawLine(t.s(0.25f), Offset(x + 1.2f * u, top + h * 0.06f), Offset(x + 1.2f * u, piso), 0.6f * u)
            }
        }
        // Los listones, con las rendijas por donde se ven los cuadernos.
        val lis = 4
        val rendija = h * 0.035f
        val lh = (caja.height - rendija * (lis - 1)) / lis
        for (j in 0 until lis) {
            val r = Rect(caja.left, caja.top + (lh + rendija) * j, caja.right, caja.top + (lh + rendija) * j + lh)
            if (j < lis - 1) drawRect(t.s(0.45f), Offset(r.left, r.bottom), Size(r.width, rendija * 0.45f))
            drawRect(t.c(mezcla(pino, PINO_LUZ, 0.12f * j)), r.topLeft, r.size)
            vetas(r, (if (t.o) NOCHE else PINO_HONDO).copy(alpha = 0.35f), c.semilla + j, 0.6f, grosor = 0.5f, ondulacion = 0.5f)
            drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            if (j == 1 + Math.floorMod(m ushr 13, 2)) drawOval(t.c(PINO_HONDO), Offset(r.left + r.width * (0.2f + 0.2f * azar(m, j)), r.center.y - lh * 0.18f), Size(w * 0.025f, lh * 0.36f))
        }
        if (der) drawRect(t.b(0.16f), Offset(caja.right - caja.width * 0.18f, caja.top), Size(caja.width * 0.18f, caja.height))
        else { drawRect(t.b(0.1f), caja.topLeft, Size(caja.width * 0.15f, caja.height)); drawRect(t.s(0.22f), Offset(caja.right - caja.width * 0.12f, caja.top), Size(caja.width * 0.12f, caja.height)) }
        // Los montantes de las esquinas con sus clavos.
        val mw = w * 0.045f
        listOf(caja.left, caja.right - mw).forEachIndexed { i, x ->
            drawRect(t.c(mezcla(pino, PINO_HONDO, 0.35f)), Offset(x, caja.top), Size(mw, caja.height))
            drawRect(if (i == 0) t.b(0.35f) else t.s(0.25f), Offset(x, caja.top), Size(1.2f * u, caja.height))
            for (j in 0 until lis) clavo(Offset(x + mw / 2, caja.top + (lh + rendija) * j + lh / 2), 1.2f * u, t)
        }
        // La cinta, de arriba abajo por el frente, con su lazo grande.
        val clase = Math.floorMod(m ushr 9, 3)
        val cx = w * (0.62f + 0.12f * azar(c.semilla, 5))
        val cintas = when (clase) { 0 -> listOf(AZUL_P, CRUDO, LANA); 1 -> listOf(LANA, LANA, LANA); else -> listOf(AZUL_P, AZUL_P, AZUL_P) }
        drawRect(t.s(0.3f), Offset(cx - 3f * u, caja.top), Size(10f * u, caja.height))
        cintas.forEachIndexed { i, col -> drawRect(t.c(col), Offset(cx - 4.5f * u + i * 3f * u, caja.top), Size(3f * u, caja.height)) }
        drawRect(t.b(0.3f), Offset(cx - 4.5f * u, caja.top), Size(1f * u, caja.height))
        val lazo = Offset(cx, caja.top + h * 0.06f)
        val col0 = t.c(cintas[0]); val col2 = t.c(cintas[2])
        listOf(-1f, 1f).forEach { s ->
            val col = if (s < 0) col0 else col2
            // El ala del lazo, con su vuelta por dentro.
            val ala = Path().apply {
                moveTo(lazo.x, lazo.y)
                cubicTo(lazo.x + s * w * 0.05f, lazo.y - h * 0.15f, lazo.x + s * w * 0.13f, lazo.y - h * 0.1f, lazo.x + s * w * 0.11f, lazo.y + h * 0.01f)
                cubicTo(lazo.x + s * w * 0.09f, lazo.y + h * 0.06f, lazo.x + s * w * 0.03f, lazo.y + h * 0.03f, lazo.x, lazo.y)
                close()
            }
            translate(w * 0.008f, h * 0.015f) { drawPath(ala, t.s(0.28f)) }
            drawPath(ala, col)
            clipPath(ala) { drawOval(t.s(0.3f), Offset(lazo.x + s * w * 0.025f - w * 0.03f, lazo.y - h * 0.08f), Size(w * 0.06f, h * 0.07f)) }
            drawPath(ala, t.b(0.3f), style = Stroke(0.7f * u))
            // La caída, con el corte en cola de milano.
            val a = lazo + Offset(s * w * 0.01f, h * 0.01f)
            val z = lazo + Offset(s * w * 0.06f, h * 0.24f)
            val caida = Path().apply {
                moveTo(a.x - 2.5f * u, a.y); lineTo(a.x + 2.5f * u, a.y)
                lineTo(z.x + 3f * u, z.y); lineTo(z.x, z.y - h * 0.025f); lineTo(z.x - 3f * u, z.y); close()
            }
            translate(w * 0.008f, h * 0.012f) { drawPath(caida, t.s(0.25f)) }
            drawPath(caida, col)
            drawPath(caida, t.s(0.15f), style = Stroke(0.6f * u))
        }
        drawRect(t.c(cintas[1]), Offset(lazo.x - w * 0.015f, lazo.y - h * 0.025f), Size(w * 0.03f, h * 0.05f))
        drawRect(t.s(0.25f), Offset(lazo.x + w * 0.006f, lazo.y - h * 0.025f), Size(w * 0.009f, h * 0.05f))
        drawRect(t.b(0.4f), Offset(lazo.x - w * 0.015f, lazo.y - h * 0.025f), Size(w * 0.03f, 0.8f * u))
        filo(caja, t, der)
        cartelTema(w, h, t, c.semilla)
    }

    /**
     * Un legajo de frente en [r]: la tablilla de abajo, la pila de papeles con sus cantos desparejos y de varios tonos, la
     * tablilla de arriba de [carton] jaspeado, el cordel en cruz con su nudo arriba y, si [sello], el lacre grande en el cruce.
     */
    private fun DrawScope.fajo(r: Rect, t: Tono, carton: Color, semilla: Int, sello: Boolean, der: Boolean) {
        val u = density
        val tb = maxOf(2.5f * u, r.height * 0.09f)
        drawRect(t.c(mezcla(carton, TINTA, 0.18f)), Offset(r.left + r.width * 0.01f, r.bottom - tb), Size(r.width, tb))
        drawRect(t.b(0.3f), Offset(r.left, r.bottom - tb), Size(r.width, 0.8f * u))
        // Las hojas: rayas de canto, cada una de su largo y su tono.
        val pila = Rect(r.left + r.width * 0.015f, r.top + tb, r.right - r.width * 0.01f, r.bottom - tb)
        drawRect(t.c(mezcla(CARTEL, CRUDO, 0.3f)), pila.topLeft, pila.size)
        val hojas = (pila.height / (2.4f * u)).toInt().coerceIn(4, 40)
        val alto = pila.height / hojas
        for (k in 0 until hojas) {
            val y = pila.top + alto * k
            val izq = r.width * 0.035f * azar(semilla, k)
            val dch = r.width * 0.03f * azar(semilla + 1, k)
            val tono = if (azar(semilla + 3, k) > 0.78f) CUADERNOS[Math.floorMod(k + semilla, CUADERNOS.size)] else mezcla(CARTEL, KRAFT, 0.35f * azar(semilla + 5, k))
            drawRect(t.c(tono), Offset(pila.left - izq, y), Size(pila.width + izq + dch, alto * 0.82f))
            drawRect(t.s(0.2f), Offset(pila.left - izq, y + alto * 0.82f), Size(pila.width + izq + dch, maxOf(0.5f * u, alto * 0.18f)))
        }
        if (der) drawRect(t.b(0.14f), Offset(pila.right - pila.width * 0.15f, pila.top), Size(pila.width * 0.15f, pila.height))
        else drawRect(t.s(0.18f), Offset(pila.right - pila.width * 0.12f, pila.top), Size(pila.width * 0.12f, pila.height))
        // La tablilla de arriba, jaspeada, con su esquina gastada.
        val arriba = Rect(r.left - r.width * 0.008f, r.top, r.right + r.width * 0.005f, r.top + tb)
        drawRect(t.c(carton), arriba.topLeft, arriba.size)
        for (k in 0 until 7) drawOval(t.c(mezcla(carton, if (k % 2 == 0) CRUDO else TINTA, 0.3f)).copy(alpha = 0.5f), Offset(arriba.left + arriba.width * azar(semilla + 7, k), arriba.top + arriba.height * 0.2f), Size(r.width * 0.06f, arriba.height * 0.5f))
        drawRect(t.b(0.5f), arriba.topLeft, Size(arriba.width, 1f * u))
        drawRect(t.s(0.3f), Offset(arriba.left, arriba.bottom - 1f * u), Size(arriba.width, 1f * u))
        drawCircle(t.c(mezcla(carton, CRUDO, 0.5f)), tb * 0.6f, Offset(arriba.right, arriba.top))
        // El cordel en cruz y su nudo arriba.
        val cordel = t.c(mezcla(KRAFT, CRUDO, 0.35f))
        val cx = r.center.x + r.width * 0.06f * (azar(semilla, 50) - 0.5f)
        val cy = pila.center.y
        drawLine(t.s(0.3f), Offset(cx + 1.2f * u, r.top), Offset(cx + 1.2f * u, r.bottom), 2f * u)
        drawLine(cordel, Offset(cx, r.top), Offset(cx, r.bottom), 1.6f * u)
        drawLine(t.s(0.3f), Offset(r.left, cy + 1.2f * u), Offset(r.right, cy + 1.2f * u), 2f * u)
        drawLine(cordel, Offset(r.left, cy), Offset(r.right, cy), 1.6f * u)
        val nudo = Offset(cx, r.top)
        listOf(-1f, 1f).forEach { s ->
            val bucle = Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + s * r.width * 0.06f, nudo.y - r.height * 0.22f, nudo.x + s * r.width * 0.13f, nudo.y - r.height * 0.08f, nudo.x, nudo.y) }
            drawPath(bucle, cordel, style = Stroke(1.5f * u))
            drawLine(cordel, nudo, nudo + Offset(s * r.width * 0.07f, r.height * 0.12f), 1.4f * u, StrokeCap.Round)
        }
        drawCircle(cordel, 2.4f * u, nudo)
        if (sello) {
            val rs = minOf(r.height * 0.26f, r.width * 0.09f)
            val lc = Offset(cx, cy)
            drawCircle(t.s(0.3f), rs, lc + Offset(1.8f * u, 2.4f * u))
            drawPath(contornoIrregular(Rect(lc.x - rs, lc.y - rs, lc.x + rs, lc.y + rs), semilla, 14, 0.1f), t.c(LACRE))
            drawCircle(t.c(mezcla(LACRE, TINTA, 0.25f)), rs * 0.62f, lc)
            drawCircle(t.b(0.35f), rs * 0.62f, lc, style = Stroke(0.8f * u))
            // El sello: un haz de líneas con su gorro, sugerido.
            drawLine(t.b(0.3f), lc - Offset(0f, rs * 0.4f), lc + Offset(0f, rs * 0.4f), 0.8f * u)
            drawCircle(t.b(0.3f), rs * 0.14f, lc - Offset(0f, rs * 0.42f))
            drawCircle(t.b(0.6f), rs * 0.16f, lc - Offset(rs * 0.45f, rs * 0.45f))
        }
    }

    /**
     * El legajo: una pila alta de papeles entre dos tablillas de cartón (gris azulado, kraft, verde o rojizo), atada en cruz
     * y lacrada; con más fichas, otro legajo encima y, con más todavía, uno apoyado de canto al lado y un tercero arriba.
     */
    private fun DrawScope.legajo(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val piso = h * PISO
        val n = c.n.coerceIn(1, 10)
        val cartones = listOf(CARTON, KRAFT, Color(0xFF6E7F5E), Color(0xFF8A5A48))
        val carton = cartones[Math.floorMod(m ushr 5, 4)]
        val otro = cartones[Math.floorMod((m ushr 5) + 1 + Math.floorMod(m ushr 9, 3), 4)]
        val apoyado = n >= 6
        val principal = Rect(w * 0.09f, h * (if (n >= 3) 0.42f else 0.24f), w * (if (apoyado) 0.78f else 0.91f), piso)
        val sil = Path().apply { addRect(principal); if (n >= 3) addRect(Rect(w * 0.15f, h * 0.2f, w * 0.72f, principal.top)) }
        sombraDe(sil, w, h, t, principal.width, principal.center.x)
        // El apoyado de canto, detrás a la derecha.
        if (apoyado) {
            val largo = h * 0.66f; val grueso = w * 0.11f
            translate(w * 0.012f, 0f) {
                rotate(-82f, Offset(w * 0.9f, piso)) { fajo(Rect(w * 0.9f, piso - grueso, w * 0.9f + largo, piso), t, otro, c.semilla + 31, false, der) }
            }
        }
        fajo(principal, t, carton, c.semilla, true, der)
        if (n >= 3) {
            val s = Rect(w * 0.15f, h * 0.22f, w * 0.72f, principal.top)
            rotate(-2.5f, Offset(s.center.x, s.bottom)) { fajo(s, t, otro, c.semilla + 17, false, der) }
        }
        if (n >= 8) {
            val s = Rect(w * 0.22f, h * 0.08f, w * 0.6f, h * 0.22f)
            rotate(3f, Offset(s.center.x, s.bottom)) { fajo(s, t, carton, c.semilla + 23, false, der) }
        }
        filo(principal, t, der)
        cartelTema(w, h, t, c.semilla, papel = mezcla(CARTEL, CRUDO, 0.3f))
    }

    /**
     * La cesta de mimbre, alta: el asa en arco que sube por encima de todo, el cuerpo trenzado, el borde y los folletos
     * cosidos de colores (azul, gris, jaspeado, crudo…), de pie y abiertos en abanico, más cuantas más fichas.
     */
    private fun DrawScope.cesta(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val top = h * 0.3f
        val mimbre = when (Math.floorMod(m ushr 6, 3)) { 0 -> MIMBRE; 1 -> mezcla(MIMBRE, PINO_HONDO, 0.35f); else -> mezcla(MIMBRE, CRUDO, 0.3f) }
        val cuerpo = poligono(w * 0.14f, top, w * 0.86f, top, w * 0.8f, piso, w * 0.2f, piso)
        val asa = Path().apply { moveTo(w * 0.27f, top); cubicTo(w * 0.27f, -h * 0.17f, w * 0.73f, -h * 0.17f, w * 0.73f, top) }
        val sil = Path().apply { addPath(cuerpo); addRect(Rect(w * 0.22f, h * 0.06f, w * 0.78f, top)) }
        sombraDe(sil, w, h, t, w * 0.66f, w * 0.5f)
        clipRect(-w, -h, w * 2f, h * PISO) { translate(w * 0.04f, h * 0.03f) { drawPath(asa, t.s(0.18f), style = Stroke(4.5f * u)) } }
        // Los folletos, abiertos en abanico desde el fondo de la cesta.
        val n = c.n.coerceIn(1, 10)
        val cuantos = (3 + n).coerceAtMost(9)
        val eje = Offset(w * 0.5f, top + h * 0.25f)
        for (i in 0 until cuantos) {
            val f = if (cuantos == 1) 0.5f else i / (cuantos - 1f)
            val g = -30f + 60f * f + (azar(c.semilla, i + 11) - 0.5f) * 6f
            val largo = h * (0.42f + 0.014f * n) * (0.8f + 0.25f * azar(c.semilla, i + 3))
            val fw = w * 0.12f
            val col = CUADERNOS[Math.floorMod(i * 2 + (m ushr 6), CUADERNOS.size)]
            rotate(g, eje) {
                val r = Rect(eje.x - fw / 2, eje.y - largo, eje.x + fw / 2, eje.y)
                drawRect(t.s(0.2f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
                drawRect(t.c(col), r.topLeft, r.size)
                if (Math.floorMod(i + m, 3) == 0) for (k in 0 until 5) drawOval(t.c(mezcla(col, TINTA, 0.3f)).copy(alpha = 0.4f), Offset(r.left + r.width * azar(c.semilla, k + 40) * 0.7f, r.top + r.height * 0.08f * k + h * 0.01f), Size(r.width * 0.35f, h * 0.025f))
                else drawRect(t.c(TINTA).copy(alpha = 0.45f), Offset(r.left + r.width * 0.25f, r.top + h * 0.03f), Size(r.width * 0.55f, h * 0.018f))
                drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.9f * u))
                drawRect(t.s(0.22f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
                // La costura del lomo.
                for (k in 0 until 3) drawLine(t.c(CRUDO), Offset(r.left + r.width * 0.12f, r.top + r.height * (0.12f + 0.12f * k)), Offset(r.left + r.width * 0.12f, r.top + r.height * (0.18f + 0.12f * k)), 0.9f * u)
            }
        }
        // El asa en arco, de mimbre retorcido, por encima de los folletos.
        drawPath(asa, t.c(mezcla(mimbre, PINO_HONDO, 0.25f)), style = Stroke(4.5f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.4f), style = Stroke(1.6f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3f * u, 2.5f * u))))
        drawPath(asa, t.s(0.3f), style = Stroke(4.5f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 4.3f * u))))
        // El cuerpo trenzado.
        drawPath(cuerpo, t.c(mimbre))
        clipPath(cuerpo) {
            val filas = 9
            val fh = (piso - top) / filas
            for (k in 0 until filas) {
                val y = top + fh * k
                drawRect(t.s(0.3f), Offset(0f, y + fh - 0.9f * u), Size(w, 0.9f * u))
                for (j in 0 until 12) {
                    val x = w * 0.14f + w * 0.72f * j / 12f
                    if ((j + k) % 2 == 0) drawRect(t.b(0.22f), Offset(x, y + fh * 0.12f), Size(w * 0.72f / 12f, fh * 0.5f))
                    else drawRect(t.s(0.14f), Offset(x, y), Size(w * 0.72f / 12f, fh))
                }
            }
            if (der) drawRect(t.b(0.15f), Offset(w * 0.68f, top), Size(w * 0.2f, piso - top))
            else { drawRect(t.b(0.12f), Offset(w * 0.14f, top), Size(w * 0.14f, piso - top)); drawRect(t.s(0.22f), Offset(w * 0.72f, top), Size(w * 0.16f, piso - top)) }
            drawRect(t.s(0.25f), Offset(0f, piso - h * 0.05f), Size(w, h * 0.05f))
        }
        // El borde trenzado.
        val borde = Rect(w * 0.13f, top - h * 0.025f, w * 0.87f, top + h * 0.025f)
        drawRect(t.c(mezcla(mimbre, PINO_HONDO, 0.25f)), borde.topLeft, borde.size)
        for (j in 0 until 22) drawLine(t.s(0.3f), Offset(borde.left + borde.width * j / 22f, borde.top), Offset(borde.left + borde.width * (j + 0.7f) / 22f, borde.bottom), 0.8f * u)
        drawRect(t.b(0.45f), borde.topLeft, Size(borde.width, 1f * u))
        filo(Rect(w * 0.14f, borde.top, w * 0.86f, piso), t, der)
        cartelTema(w, h, t, c.semilla, papel = mezcla(CARTEL, Color(0xFFD9D4C6), 0.5f))
    }

    /**
     * Lo que no tiene tema: un saco de arpillera con la boca abierta y fruncida, papeles revueltos que asoman, la cuerda del
     * cuello, la panza que pesa sobre la tabla con sus pliegues, la trama y un remiendo.
     */
    private fun DrawScope.saco(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val cuello = h * 0.36f
        val cuerpo = Path().apply {
            moveTo(w * 0.32f, cuello)
            cubicTo(w * 0.12f, h * 0.42f, w * 0.03f, h * 0.56f, w * 0.06f, h * 0.68f)
            cubicTo(w * 0.08f, piso - h * 0.01f, w * 0.2f, piso, w * 0.3f, piso)
            lineTo(w * 0.74f, piso)
            cubicTo(w * 0.86f, piso, w * 0.97f, piso - h * 0.02f, w * 0.95f, h * 0.64f)
            cubicTo(w * 0.93f, h * 0.5f, w * 0.85f, h * 0.41f, w * 0.68f, cuello)
            close()
        }
        val boca = Rect(w * 0.26f, h * 0.19f, w * 0.74f, h * 0.3f)
        val sil = Path().apply { addPath(cuerpo); addRect(Rect(boca.left, h * 0.08f, boca.right, cuello)) }
        sombraDe(sil, w, h, t, w * 0.86f, w * 0.5f)
        // La boca abierta: el hueco oscuro.
        drawOval(t.c(mezcla(PINO_HONDO, NOCHE, 0.4f)), boca.topLeft, boca.size)
        // Los papeles revueltos que asoman.
        val n = c.n.coerceIn(1, 10)
        for (i in 0 until (2 + n).coerceAtMost(8)) {
            val x = boca.left + boca.width * (0.12f + 0.76f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 7) - 0.5f) * 60f
            val alto = h * (0.12f + 0.012f * n) * (0.7f + 0.4f * azar(c.semilla, i + 3))
            rotate(g, Offset(x, boca.center.y)) {
                val r = Rect(x - w * 0.07f, boca.center.y - alto, x + w * 0.07f, boca.center.y + h * 0.04f)
                drawRect(t.s(0.2f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
                drawRect(t.c(if (i % 3 == 0) PAPEL else mezcla(CARTEL, CUADERNOS[i % CUADERNOS.size], 0.35f)), r.topLeft, r.size)
                for (j in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(r.left + w * 0.015f, r.top + h * (0.03f + 0.025f * j)), Offset(r.right - w * 0.02f, r.top + h * (0.03f + 0.025f * j)), 0.6f * u)
                drawRect(t.b(0.35f), r.topLeft, Size(r.width, 0.8f * u))
                if (i % 2 == 1) drawPath(poligono(r.right, r.top, r.right - w * 0.03f, r.top, r.right, r.top + h * 0.035f), t.s(0.25f))
            }
        }
        // La panza del saco, con su trama, sus pliegues que caen y la luz de la izquierda.
        drawPath(cuerpo, t.c(ARPILLERA))
        clipPath(cuerpo) {
            var x = 0f
            while (x < w) { drawLine(t.s(0.1f), Offset(x, cuello), Offset(x + w * 0.01f, piso), 0.7f * u); x += maxOf(1f, w * 0.02f) }
            var y = cuello
            while (y < piso) { drawLine(t.b(0.1f), Offset(0f, y), Offset(w, y), 0.7f * u); y += maxOf(1f, h * 0.035f) }
            listOf(0.36f to -0.12f, 0.5f to 0f, 0.62f to 0.14f).forEach { (f, a) ->
                val p = Path().apply { moveTo(w * f, cuello); cubicTo(w * (f + a * 0.5f), h * 0.5f, w * (f + a * 1.4f), h * 0.6f, w * (f + a * 2.2f), piso - h * 0.04f) }
                drawPath(p, t.s(0.28f), style = Stroke(2.4f * u))
                translate(-1.6f * u, 0f) { drawPath(p, t.b(0.22f), style = Stroke(1f * u)) }
            }
            drawOval(t.b(0.16f), Offset(w * 0.06f, h * 0.48f), Size(w * 0.32f, h * 0.22f))
            if (der) drawRect(t.b(0.12f), Offset(w * 0.78f, cuello), Size(w * 0.22f, piso - cuello))
            else drawRect(t.s(0.26f), Offset(w * 0.78f, cuello), Size(w * 0.22f, piso - cuello))
            drawRect(t.s(0.25f), Offset(0f, piso - h * 0.06f), Size(w, h * 0.06f))
            // El remiendo con sus puntadas.
            val rm = Rect(w * 0.62f, h * 0.55f, w * 0.76f, h * 0.66f)
            drawRect(t.c(mezcla(ARPILLERA, KRAFT, 0.45f)), rm.topLeft, rm.size)
            drawRect(t.c(CRUDO).copy(alpha = 0.7f), rm.topLeft, rm.size, style = Stroke(0.8f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f * u, 2f * u))))
        }
        // El frunce de la boca: la tela recogida en pliegues, con el borde ondulado.
        val frunce = Path().apply {
            moveTo(boca.left - w * 0.02f, boca.center.y)
            val ondas = 9
            for (k in 0..ondas) {
                val x = boca.left - w * 0.02f + (boca.width + w * 0.04f) * k / ondas
                val yb = boca.center.y + boca.height * 0.15f + (if (k % 2 == 0) 0f else boca.height * 0.3f)
                lineTo(x, yb)
            }
            lineTo(w * 0.69f, cuello + h * 0.01f)
            lineTo(w * 0.31f, cuello + h * 0.01f)
            close()
        }
        drawPath(frunce, t.c(mezcla(ARPILLERA, KRAFT, 0.3f)))
        clipPath(frunce) {
            for (k in 0 until 9) {
                val x0 = boca.left + boca.width * (k + 0.5f) / 9f
                drawLine(t.s(0.3f), Offset(x0, boca.center.y), Offset(w * 0.5f + (x0 - w * 0.5f) * 0.4f, cuello + h * 0.01f), 1.2f * u)
            }
            drawRect(t.b(0.15f), Offset(boca.left - w * 0.02f, boca.center.y), Size(w * 0.15f, h * 0.2f))
        }
        drawPath(frunce, t.b(0.3f), style = Stroke(0.7f * u))
        // La cuerda del cuello, con su nudo y los cabos.
        val cuerda = t.c(mezcla(KRAFT, CRUDO, 0.3f))
        drawLine(t.s(0.3f), Offset(w * 0.3f, cuello + h * 0.015f), Offset(w * 0.7f, cuello + h * 0.015f), 2.6f * u)
        drawLine(cuerda, Offset(w * 0.3f, cuello), Offset(w * 0.7f, cuello), 2.2f * u, StrokeCap.Round)
        val nudo = Offset(w * 0.64f, cuello)
        drawCircle(cuerda, 2.8f * u, nudo)
        drawLine(cuerda, nudo, nudo + Offset(w * 0.04f, h * 0.14f), 1.6f * u, StrokeCap.Round)
        drawLine(cuerda, nudo, nudo + Offset(w * 0.075f, h * 0.11f), 1.6f * u, StrokeCap.Round)
        filo(Rect(w * 0.08f, cuello, w * 0.94f, piso), t, der)
        cartelTema(w, h, t, c.semilla, papel = mezcla(CARTEL, KRAFT, 0.15f))
    }

    /** Un hueco libre: una cajita de pino volcada de lado, unos papeles que se le salen y un clavo grande de forja tirado. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        // Según la semilla, la caja cae a la izquierda o a la derecha, y el clavo al otro lado.
        val izq = Math.floorMod(c.semilla ushr 2, 2) == 0
        val x0 = if (izq) 0.16f else 0.5f
        val caja = Rect(w * x0, piso - h * 0.42f, w * (x0 + 0.34f), piso)
        sombraDe(Path().apply { addRect(caja) }, w, h, t, caja.width, caja.center.x)
        // La boca de la caja, de frente: el interior oscuro, y los papeles que se salen por ella.
        drawRect(t.c(mezcla(PINO, PINO_HONDO, 0.2f)), caja.topLeft, caja.size)
        val dentro = Rect(caja.left + w * 0.02f, caja.top + w * 0.02f, caja.right - w * 0.02f, caja.bottom - w * 0.015f)
        drawRect(t.c(PINO_HONDO), dentro.topLeft, dentro.size)
        drawRect(t.s(0.4f), dentro.topLeft, Size(dentro.width, dentro.height * 0.3f))
        drawRect(t.s(0.3f), dentro.topLeft, Size(dentro.width * 0.25f, dentro.height))
        drawRect(t.b(0.45f), caja.topLeft, Size(caja.width, 1f * u))
        drawRect(t.b(0.3f), caja.topLeft, Size(1f * u, caja.height))
        drawRect(t.s(0.3f), Offset(caja.right - 1.2f * u, caja.top), Size(1.2f * u, caja.height))
        vetas(Rect(caja.left, caja.top, caja.right, dentro.top), (if (t.o) NOCHE else PINO_HONDO).copy(alpha = 0.3f), c.semilla, 0.6f, grosor = 0.5f)
        listOf(Offset(caja.left + w * 0.01f, caja.top + w * 0.01f), Offset(caja.right - w * 0.01f, caja.bottom - w * 0.008f)).forEach { clavo(it, 1f * u, t) }
        val fx0 = if (izq) 0.5f else 0.36f
        listOf(Triple(fx0, -6f, CARTEL), Triple(fx0 + 0.12f, 8f, mezcla(CARTEL, CUADERNOS[Math.floorMod(c.semilla, CUADERNOS.size)], 0.4f))).forEach { (fx, g, col) ->
            val r = Rect(w * (fx - 0.12f), piso - h * 0.05f, w * (fx + 0.14f), piso - h * 0.005f)
            rotate(if (izq) g else -g, r.center) {
                drawRect(t.s(0.25f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
                drawRect(t.c(col), r.topLeft, r.size)
                drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
                drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(r.left + w * 0.02f, r.center.y), Offset(r.right - w * 0.03f, r.center.y), 0.7f * u)
            }
        }
        // El clavo de forja, tumbado: la cabeza martillada, la caña cuadrada que se afina hasta la punta, y su sombra.
        val cx = if (izq) 0.76f else 0.12f
        val y = piso - h * 0.03f
        val a = w * cx; val z = w * (cx + 0.15f)
        val g = h * 0.022f
        val cana = poligono(a, y - g / 2, z, y - g * 0.08f, z, y + g * 0.08f, a, y + g / 2)
        translate(1.5f * u, 2f * u) { drawPath(cana, t.s(0.35f)) }
        contacto(Offset((a + z) / 2, y + g * 0.6f), z - a, h * 0.03f, 0.3f)
        drawPath(cana, t.c(HIERRO))
        drawLine(t.b(0.4f), Offset(a, y - g / 2 + 0.5f * u), Offset(z, y - g * 0.08f + 0.3f * u), 0.7f * u)
        val cabeza = Rect(a - w * 0.014f, y - h * 0.04f, a, y + h * 0.04f)
        drawRect(t.s(0.35f), Offset(cabeza.left + 1.5f * u, cabeza.top + 2f * u), cabeza.size)
        drawRect(t.c(mezcla(HIERRO, Color(0xFF6A4A32), 0.2f)), cabeza.topLeft, cabeza.size)
        drawRect(t.b(0.35f), cabeza.topLeft, Size(1f * u, cabeza.height))
        drawRect(t.b(0.3f), cabeza.topLeft, Size(cabeza.width, 0.8f * u))
    }

    override val rotulo = Rect(0.025f, 0.81f, 0.975f, 1.07f)
    override fun tintaRotulo(oscuro: Boolean) = NEGRO
    override val letraRotulo: FontFamily = LetraExtra.convencion
    override val salida = -SALIDA
    override val placa = Rect(0.29f, 0.1f, 0.71f, 0.44f)
    override fun tintaPlaca(oscuro: Boolean) = NEGRO

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    /** Una escarapela: azul afuera, blanco y rojo al centro, con su plisado. */
    private fun DrawScope.escarapela(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(t(AZUL, oscuro), r, c)
        drawCircle(t(BLANCO, oscuro), r * 0.66f, c)
        drawCircle(t(ROJO, oscuro), r * 0.36f, c)
        for (k in 0 until 16) {
            val a = k * Math.PI / 8
            drawLine(Color.Black.copy(alpha = 0.15f), Offset(c.x + r * 0.4f * cos(a).toFloat(), c.y + r * 0.4f * sin(a).toFloat()), Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), 0.6f * density)
        }
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Listones de pino forrados de papel de periódico: columnas de renglones grises.
        drawRect(t(PINO, oscuro))
        val hoja = if (oscuro) mezcla(PAPEL, Color.Black, 0.58f) else PAPEL
        val pl = 14 * u
        val hojas = listOf(Rect(pl, pl, w * 0.62f, h * 0.48f), Rect(w * 0.45f, h * 0.3f, w - pl, h * 0.78f), Rect(pl, h * 0.55f, w * 0.6f, h - pl))
        hojas.forEachIndexed { i, r ->
            rotate(if (i == 1) 2f else -1.5f, r.center) {
                drawRect(hoja, r.topLeft, r.size)
                drawRect(NEGRO.copy(alpha = 0.4f), Offset(r.left + 8 * u, r.top + 8 * u), Size(r.width - 16 * u, 6 * u))
                val col = 4
                val cw = (r.width - 16 * u) / col
                for (j in 0 until col) {
                    var y = r.top + 22 * u
                    while (y < r.bottom - 8 * u) { drawLine(NEGRO.copy(alpha = 0.16f), Offset(r.left + 8 * u + cw * j + 2 * u, y), Offset(r.left + 8 * u + cw * (j + 1) - 4 * u, y), 1.2f * u); y += 5 * u }
                }
            }
        }
        // Los montantes de pino y las rendijas de la caja.
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(PINO_OSC, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) PAPEL else NEGRO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(rasgadoRevolucion(s.width, s.height, 22, minOf(s.width, s.height) * 0.035f, semilla)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PAPEL.copy(alpha = 0.45f))
        // El doble filete de imprenta arriba, y uno fino abajo.
        val m = 8 * u * g
        drawRect(NEGRO.copy(alpha = 0.85f), Offset(m, 4 * u * g), Size(w - 2 * m, 2.4f * u * g))
        drawRect(NEGRO.copy(alpha = 0.85f), Offset(m, 8 * u * g), Size(w - 2 * m, 0.7f * u * g))
        drawRect(NEGRO.copy(alpha = 0.5f), Offset(m, h - 12 * u * g), Size(w - 2 * m, 0.7f * u * g))
        // El engrudo en las esquinas de arriba.
        mancha(Offset(4 * u, 4 * u), 14 * u * g, Color.White.copy(alpha = 0.35f))
        mancha(Offset(w - 4 * u, 4 * u), 14 * u * g, Color.White.copy(alpha = 0.35f))
    }

    override val margenes = MargenesFicha(16.dp, 12.dp, 18.dp, 28.dp, 24.dp, 32.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La escarapela.
            "hecho" -> { drawCircle(AZUL, s * 0.44f, c); drawCircle(BLANCO, s * 0.3f, c); drawCircle(ROJO, s * 0.16f, c) }
            // Las tres franjas: azul, blanco y rojo.
            "convence" -> {
                listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(col, Offset(c.x - s * 0.39f + i * s * 0.26f, c.y - s * 0.3f), Size(s * 0.26f, s * 0.6f)) }
                drawRect(NEGRO.copy(alpha = 0.4f), Offset(c.x - s * 0.39f, c.y - s * 0.3f), Size(s * 0.78f, s * 0.6f), style = Stroke(s * 0.04f))
            }
            // Una pluma de ave.
            "piensa" -> {
                drawLine(NEGRO, Offset(c.x - s * 0.36f, c.y + s * 0.42f), Offset(c.x + s * 0.3f, c.y - s * 0.38f), s * 0.05f, StrokeCap.Round)
                drawPath(Path().apply { moveTo(c.x - s * 0.1f, c.y + s * 0.08f); quadraticTo(c.x - s * 0.12f, c.y - s * 0.4f, c.x + s * 0.34f, c.y - s * 0.44f); quadraticTo(c.x + s * 0.24f, c.y - s * 0.02f, c.x - s * 0.1f, c.y + s * 0.08f); close() }, NEGRO.copy(alpha = 0.75f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Una escarapela tricolor. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension
        escarapela(center, s * 0.46f, false)
    }

    override val desorden = 2.5f
}

/** Un rectángulo de [w] por [h] con el borde de abajo rasgado en [dientes] picos de hasta [hondo] (el pasquín). */
private fun rasgadoRevolucion(w: Float, h: Float, dientes: Int, hondo: Float, semilla: Int) = Path().apply {
    moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - hondo)
    for (k in dientes downTo 0) lineTo(w * k / dientes, h - hondo * (if (k % 2 == 0) 1f else 0.2f + 0.4f * azar(semilla, k)))
    close()
}
