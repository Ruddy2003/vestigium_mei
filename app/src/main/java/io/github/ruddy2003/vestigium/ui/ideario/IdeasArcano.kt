package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Horror cósmico
// =====================================================================

/**
 * Horror cósmico: el rincón del archivo municipal de un pueblo pesquero, hacia 1920, donde alguien lleva meses juntando
 * indicios. La pared es de yeso con papel pintado de rayas y ramitos, despegado por la humedad (en la esquina se cayó a tiras
 * y una punta cuelga doblada), con una **marca de agua horizontal** y su línea vieja, como si el mar hubiera entrado alguna vez, y un
 * friso de tablas abajo. No hay ventana: de día la luz es gris y fría, de arriba a la izquierda, fuera del cuadro. Lo primero
 * que se ve es el tablero de indicios clavado en la pared: una carta náutica con su costa, sus sondas y su rosa, y un punto
 * rodeado a lápiz; dos recortes de periódico amarillos, tres fotografías sepia, la hoja con una espiral a lápiz que no se
 * parece a nada, y el hilo rojo de chincheta a chincheta. El mueble es una estantería de pino oscurecido, hinchada por la
 * humedad: baldas combadas, salitre en los cantos, clavos con su chorrera de óxido, una tabla remendada y el zócalo comido.
 * Encima, de izquierda a derecha: una pila de libros de registro (uno con el lomo roto) con la caja de placas fotográficas, la
 * piedra tallada con surcos en espiral y la lupa apoyada en ella, el frasco con algo en conserva que no se distingue, el
 * quinqué de petróleo y el catalejo de latón, tumbado y cortado por el borde. Del poste del centro cuelga un manojo de
 * llaves. Al pie, el arcón de marinero con su asa de cabo y el rollo de cuerda, cortados por los bordes, sobre el piso de
 * tablas con una mancha oscura de humedad. Cada tema, según la semilla, es una caja de archivo de cartón atada con bramante y
 * sellada con lacre, una caja de caudales de hojalata negra con candado y la pintura saltada, o un fajo de libros de registro
 * sujeto con una correa; en las tres, el tema va en una tarjeta dentro de un portaetiquetas de latón verdoso, y asoman más
 * papeles cuantas más fichas. Lo que no tiene tema es una caja de puros entreabierta con papeles sueltos y la tarjeta clavada
 * con una tachuela, sin escribir, con una interrogación a lápiz; un hueco sin tema es la tabla con un cerco de humedad, arena
 * y una concha, o una telaraña con un recorte caído. De noche todo se hunde en un azul verdoso muy hondo: alumbra el quinqué
 * (halo cálido y pequeño en el papel pintado y en el tablero: el hilo rojo se enciende cerca y se pierde lejos), lo del frasco
 * tiene una fosforescencia verdosa muy tenue, y hay jirones de bruma a ras de las tablas; las cajas se apagan con la distancia
 * a la llama hacia su propio oscuro, y las de abajo quedan casi perdidas. Por dentro, el cartón kraft con manchas, un cerco
 * de taza, arena y una conchita. Las fichas son fichas de evidencia: etiquetas de manila con su ojal y su cordel.
 */
object IdeasArcano : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del archivo (el mueble), sacada del cuadro
    /** El pino oscurecido de la estantería, del friso y del piso. */
    private val PINO = Color(0xFF6A4E34)
    private val PINO_LUZ = Color(0xFF9A7A52)
    private val PINO_OSC = Color(0xFF33251A)
    /** El papel pintado verde gris, el yeso que asoma y el salobre de la marca de agua. */
    private val PAPEL_PARED = Color(0xFF9DA68C)
    private val YESO = Color(0xFFCFCDB8)
    private val SALOBRE = Color(0xFF6C5C40)
    /** El salitre blanco de los cantos. */
    private val SALITRE = Color(0xFFE8E6D8)
    /** El papel viejo (hojas, recortes, la carta), el sepia de las fotos y el rojo del hilo y del lacre. */
    private val PAPEL = Color(0xFFE7DEC4)
    private val SEPIA = Color(0xFF7C5C3E)
    private val HILO = Color(0xFFB3312A)
    /** El gris de mar: el arcón, la piedra, el agua del frasco. */
    private val MAR = Color(0xFF56716C)
    /** El latón, con su verdín. */
    private val VERDIN = Color(0xFF6E9A86)
    /** La hojalata negra y el óxido. */
    private val HOJALATA = Color(0xFF25292A)
    private val OXIDO = Color(0xFF8A5536)
    /** La luz gris crema del día, el azul verdoso de la noche, la llama del quinqué y la fosforescencia del frasco. */
    private val LUZ_DIA = Color(0xFFE8E9E0)
    private val NOCHE = Color(0xFF081A20)
    /** El violeta azulado de la sombra de día, para los degradados. */
    private val SOMBRA_DIA = Color(0xFF3A3850)
    private val LLAMA = Color(0xFFFFC572)
    private val FOSFORO = Color(0xFFA4EAC2)

    /** La tarjeta de los rótulos de noche: lejos de la llama y cerca. Con [TINTA], la de lejos da unos 4.7:1 en el título. */
    private val TARJETA_NOCHE = Color(0xFFA19879)
    private val TARJETA_LUZ = Color(0xFFF2DFB2)

    // ---------------------------------------------------------------- de la paleta de siempre (el interior y las fichas)
    private val MOHO = Color(0xFF4A6B5C)
    private val CARTON = Color(0xFF9C8C74)
    private val CARTON_LUZ = Color(0xFFBFB096)
    private val CARTON_OSC = Color(0xFF6E604C)
    private val BRAMANTE = Color(0xFFC9B58A)
    private val LACRE = Color(0xFF7A2C24)
    private val LATON = Color(0xFFB59A5E)
    private val LATON_OSC = Color(0xFF7A6638)
    private val MANILA = Color(0xFFE2CFA0)
    private val TINTA = Color(0xFF1E3329)
    private val KRAFT = Color(0xFFB8986A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama del quinqué, en fracciones del ancho. */
    private val LUZ = Offset(0.71f, 0.375f)
    /** El frasco, por su fosforescencia. */
    private val FRASCO = Offset(0.47f, 0.445f)

    /** Cuánto alumbra el quinqué el punto [p] (fracciones del ancho): cae con la distancia; las filas de abajo, a oscuras. */
    private fun deQuinque(p: Offset) = maxOf((1.15f * (1f - (p - LUZ).getDistance() / 0.82f)).coerceIn(0f, 1f), luzCuerpo(p))

    /**
     * La luz que el quinqué echa hacia abajo sobre la estantería: cae despacio de fila en fila (la primera, encendida; la
     * segunda, legible; la tercera, tenue; las dos últimas, casi perdidas), un poco menos en la columna de la izquierda, más
     * lejos de la llama. Los puntos son los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.6f to 0.82f, 0.77f to 0.76f, 1.08f to 0.5f, 1.38f to 0.25f, 1.69f to 0.08f, 2f to 0.04f, 2.4f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.5f * (kotlin.math.abs(p.x - LUZ.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.72f, 1f)
        return k * lado
    }

    /**
     * Un color a [k] (0 a 1) de la llama: de día, tal cual. De noche baja hacia **su propio** oscuro (conserva el matiz: el
     * pino se vuelve pardo hondo, el papel verde gris un verde de fondo de agua), con un poco del azul verdoso del cuarto para
     * unir la paleta; cerca de la llama sube y se entibia.
     */
    private fun n(c: Color, o: Boolean, k: Float): Color {
        if (!o) return c
        val q = k.coerceIn(0f, 1f)
        val v = 0.18f + 0.72f * q.pow(1.2f)
        val bajo = Color(c.red * v, c.green * v, c.blue * v, c.alpha)
        return mezcla(mezcla(bajo, NOCHE, 0.46f * (1f - q)), LLAMA, 0.2f * q)
    }

    private fun kk(o: Boolean, x: Float, y: Float) = if (o) deQuinque(Offset(x, y)) else 0f

    /** El color [col] en el punto (x, y) del mueble, en fracciones del ancho. */
    private fun cc(col: Color, o: Boolean, x: Float, y: Float) = n(col, o, kk(o, x, y))

    /** La sombra: de día, violeta azulada; de noche, azul verdoso casi negro. */
    private fun sombra(o: Boolean, a: Float) = if (o) Color(0xFF040C0E).copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** Lo que mira a la luz: de día, el gris crema; de noche, el filo cálido de la llama según lo cerca que esté. */
    private fun brillo(o: Boolean, a: Float, k: Float = 0f) =
        if (o) LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f)) else LUZ_DIA.copy(alpha = a.coerceIn(0f, 1f))

    /**
     * El filo cálido del quinqué en una caja, de noche: en su canto de arriba y en el costado que mira a la luz ([der]: el
     * derecho), tanto más vivo cuanto más le llega. De día no pinta nada.
     */
    private fun DrawScope.filo(r: Rect, o: Boolean, k: Float, der: Boolean) {
        if (!o || k < 0.05f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.28f * k).coerceAtMost(0.28f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello del quinqué en el latón, el lacre o el vidrio, de noche (de día, la luz gris crema). */
    private fun DrawScope.destello(c: Offset, r: Float, o: Boolean, k: Float) {
        if (o) { if (k > 0.05f) { drawCircle(LLAMA.copy(alpha = (0.35f * k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(LUZ_DIA.copy(alpha = 0.8f), r, c)
    }

    private fun tarjeta(o: Boolean, k: Float) = if (o) mezcla(TARJETA_NOCHE, TARJETA_LUZ, (1.15f * k.pow(1.4f)).coerceAtMost(1f)) else MANILA

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 1.5f
    override val corona = 0.6f
    override val base = 0.27f
    override val lado = 0.075f
    override val hueco = 0.035f

    /** Los costados de la estantería, el poste del centro y el tablero de arriba (fondo, canto y bajo del canto). */
    private const val IZQ = 0.045f
    private const val DER = 0.955f
    private const val TAPA0 = 0.532f
    private const val TAPA1 = 0.548f
    private const val CANTO = 0.564f
    /** La marca de agua en la pared. */
    private const val MAREA = 0.468f

    // ---------------------------------------------------------------- ayudantes

    private fun pl(vararg v: Float) = Path().apply {
        moveTo(v[0], v[1]); var i = 2
        while (i < v.size) { lineTo(v[i], v[i + 1]); i += 2 }
        close()
    }

    /** Una forma irregular alrededor de [c], de radios [rx] y [ry]: desconchones, manchas, salitre. */
    private fun irregular(c: Offset, rx: Float, ry: Float, semilla: Int, puntos: Int = 9, hondo: Float = 0.5f) = Path().apply {
        for (j in 0 until puntos) {
            val a = j * 6.2832f / puntos + 0.4f * azar(semilla, 40 + j)
            val f = 1f - hondo + hondo * 1.2f * azar(semilla, j)
            val p = Offset(c.x + rx * f * cos(a), c.y + ry * f * sin(a))
            if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /** La sombra de [p] en la pared: de día, a la derecha y abajo; de noche, lejos de la llama y más corta cuanto más lejos. */
    private fun DrawScope.sombraEnPared(p: Path, x: Float, y: Float, o: Boolean, a: Float = 0.22f, largo: Float = 1f) {
        val w = size.width
        val dd = if (!o) Offset(0.011f, 0.009f) * largo else {
            val v = Offset(x, y) - LUZ; val l = v.getDistance().coerceAtLeast(0.01f)
            v / l * (0.02f * largo)
        }
        val alfa = if (o) a * 1.5f * kk(true, x, y) + 0.1f else a
        translate(dd.x * w, dd.y * w) { drawPath(p, sombra(o, alfa)) }
    }

    /** Un rectángulo [r] de luz y sombra de cara plana: el tercio de la izquierda con luz, el de la derecha en sombra, cantos. */
    private fun DrawScope.cara(r: Rect, col: Color, o: Boolean, k: Float, dia: Float, redondo: Float = 0f) {
        val u = density
        val base = n(col, o, k)
        val luz = if (o) mezcla(base, LLAMA, 0.1f * k) else mezcla(col, LUZ_DIA, 0.14f + dia)
        val osc = mezcla(base, if (o) NOCHE else SOMBRA_DIA, 0.28f)
        val b = Brush.horizontalGradient(listOf(luz, base, base, osc), r.left, r.right)
        if (redondo > 0f) drawRoundRect(b, r.topLeft, r.size, CornerRadius(redondo)) else drawRect(b, r.topLeft, r.size)
        drawRect(brillo(o, 0.45f, k + 0.1f), r.topLeft, Size(r.width, 1.2f * u))
        drawRect(sombra(o, 0.35f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        drawRect(sombra(o, 0.25f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
    }

    /** Un clavo con su cabeza y la chorrera de óxido que baja. */
    private fun DrawScope.clavo(c: Offset, o: Boolean, k: Float, chorrera: Float) {
        val u = density
        if (chorrera > 0f) {
            val ch = Path().apply { moveTo(c.x, c.y); cubicTo(c.x + 1.5f * u, c.y + chorrera * 0.4f, c.x - 1f * u, c.y + chorrera * 0.7f, c.x + 0.6f * u, c.y + chorrera) }
            drawPath(ch, n(OXIDO, o, k).copy(alpha = 0.55f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            drawPath(ch, n(OXIDO, o, k).copy(alpha = 0.3f), style = Stroke(4f * u, cap = StrokeCap.Round))
        }
        drawCircle(sombra(o, 0.4f), 2f * u, c + Offset(0.8f * u, 1f * u))
        drawCircle(n(mezcla(OXIDO, HOJALATA, 0.45f), o, k), 1.9f * u, c)
        drawCircle(brillo(o, 0.5f, k + 0.1f), 0.7f * u, c - Offset(0.6f * u, 0.6f * u))
    }

    /** Una chincheta: sombra, cabeza abombada y brillo. [roja] para las del hilo. */
    private fun DrawScope.chincheta(c: Offset, o: Boolean, k: Float, roja: Boolean) {
        val w = size.width; val u = density
        val r = 0.0058f * w
        drawCircle(sombra(o, 0.4f), r, c + Offset(1.4f * u, 1.8f * u))
        drawCircle(n(if (roja) mezcla(HILO, LACRE, 0.3f) else LATON, o, k), r, c)
        drawCircle(sombra(o, 0.2f), r * 0.6f, c + Offset(r * 0.25f, r * 0.25f))
        drawCircle(brillo(o, 0.75f, k + 0.25f), r * 0.32f, c - Offset(r * 0.35f, r * 0.35f))
    }

    /** Las líneas de letra que no se leen: renglones grises en columnas, para recortes y etiquetas. */
    private fun DrawScope.renglones(r: Rect, columnas: Int, col: Color, semilla: Int, paso: Float, grueso: Float) {
        val anchoC = r.width / columnas
        for (cI in 0 until columnas) {
            var y = r.top; var q = 0
            while (y < r.bottom - grueso) {
                val f = if (azar(semilla + cI, q) < 0.15f) 0.5f else 0.9f + 0.1f * azar(semilla + cI, q + 50)
                drawRect(col, Offset(r.left + cI * anchoC + anchoC * 0.06f, y), Size(anchoC * 0.86f * f, grueso))
                y += paso; q++
            }
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val o = oscuro
        val w = size.width; val alto = size.height
        val pisa = d.base.top + 0.06f * w

        // ---- la pared, el piso y el tablero de indicios
        pared(d, o, pisa)
        piso(pisa, alto, o)
        tablero(o)

        // ---- la estantería y, encima, de atrás adelante
        estanteria(d, o, pisa)
        scale(1.25f, Offset(0.15f * w, TAPA1 * w)) { libros(o) }
        scale(1.2f, Offset(0.33f * w, TAPA1 * w)) { piedra(o); lupa(o) }
        scale(1.45f, Offset(0.47f * w, TAPA1 * w)) { frasco(o) }
        scale(1.55f, Offset(LUZ.x * w, TAPA1 * w)) { quinque(o) }
        catalejo(o)
        llaves(d, o)

        // ---- al pie, cortados por los bordes
        cuerda(alto, o)
        arcon(alto, o)

        // ---- de noche: el halo de la llama, la fosforescencia y la bruma a ras de las tablas
        if (o) {
            val m = Offset(LUZ.x * w, LUZ.y * w)
            mancha(m + Offset(0f, 0.04f * w), w * 0.42f, LLAMA.copy(alpha = 0.16f))
            mancha(m, w * 0.1f, LLAMA.copy(alpha = 0.5f))
            mancha(Offset(FRASCO.x * w, FRASCO.y * w), w * 0.11f, FOSFORO.copy(alpha = 0.16f))
            // Lo que el frasco tiñe a su lado: la tapa de la estantería y la cara de la piedra que lo mira.
            val pie = Offset(FRASCO.x * w, TAPA1 * w)
            scale(1f, 0.2f, pie) { mancha(pie, w * 0.14f, FOSFORO.copy(alpha = 0.32f)) }
            mancha(Offset(0.39f * w, 0.515f * w), w * 0.035f, FOSFORO.copy(alpha = 0.14f))
            bruma(pisa, alto)
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * La pared: el papel pintado de rayas con ramitos, sus juntas, la mancha de luz arriba a la izquierda (de noche, el halo
     * de la llama), la marca de agua con su línea vieja y el salobre debajo, el papel despegado donde asoma el yeso, y el friso.
     */
    private fun DrawScope.pared(d: Disposicion, o: Boolean, pisa: Float) {
        val w = size.width; val u = density
        val friso = pisa - 0.3f * w
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(PAPEL_PARED, LUZ_DIA, 0.32f), PAPEL_PARED, mezcla(PAPEL_PARED, MAR, 0.3f)), 0f, w), Offset.Zero, Size(w, friso))
            mancha(Offset(w * 0.04f, -w * 0.06f), w * 0.7f, LUZ_DIA.copy(alpha = 0.42f))
        } else {
            val hc = Offset(LUZ.x * w, LUZ.y * w - 0.04f * w)
            drawRect(Brush.radialGradient(listOf(n(PAPEL_PARED, true, 1f), n(PAPEL_PARED, true, 0.62f), n(PAPEL_PARED, true, 0.28f), n(PAPEL_PARED, true, 0.08f), n(PAPEL_PARED, true, 0f)), hc, w * 0.85f),
                Offset.Zero, Size(w, friso))
        }
        pinceladas(Rect(0f, 0f, w, friso), if (o) NOCHE else mezcla(PAPEL_PARED, SALOBRE, 0.4f), 6101, 34, u, 0.07f)
        // Dónde se ve la pared: arriba, sobre la estantería, y en las franjas de los lados.
        fun seVe(x: Float, y: Float) = y < TAPA0 + 0.01f || x < IZQ + 0.004f || x > DER - 0.004f
        // Las rayas del papel, de dos en dos, por tramos (de noche, cada tramo con su luz).
        var x = 0.018f
        while (x < 1f) {
            var y = 0f
            while (y < friso / w) {
                val y1 = minOf(y + 0.18f, friso / w)
                if (seVe(x, y)) {
                    val col = cc(mezcla(PAPEL_PARED, MAR, 0.5f), o, x, (y + y1) / 2).copy(alpha = 0.55f)
                    drawLine(col, Offset(x * w, y * w), Offset(x * w, y1 * w), 1f * u)
                    drawLine(col.copy(alpha = 0.35f), Offset(x * w + 3.2f * u, y * w), Offset(x * w + 3.2f * u, y1 * w), 0.6f * u)
                }
                y = y1
            }
            x += 0.068f
        }
        // Los ramitos entre las rayas: un botón y dos hojitas, al tresbolillo.
        var fila = 0
        var y = 0.03f
        while (y < friso / w) {
            var xx = 0.052f + (if (fila % 2 == 0) 0f else 0.034f)
            while (xx < 1f) {
                if (seVe(xx, y)) {
                    val col = cc(mezcla(PAPEL_PARED, SALOBRE, 0.45f), o, xx, y).copy(alpha = 0.5f)
                    val c = Offset(xx * w, y * w)
                    drawCircle(col, 1.4f * u, c)
                    drawOval(col, Offset(c.x - 4.6f * u, c.y + 0.6f * u), Size(3.6f * u, 1.6f * u))
                    drawOval(col, Offset(c.x + 1f * u, c.y + 0.6f * u), Size(3.6f * u, 1.6f * u))
                }
                xx += 0.068f
            }
            y += 0.052f; fila++
        }
        // Las juntas de los paños, con el borde un poco levantado.
        listOf(0.255f, 0.53f, 0.79f).forEach { xj ->
            drawLine(sombra(o, 0.16f), Offset(xj * w + 1f * u, 0f), Offset(xj * w + 1f * u, friso), 1f * u)
            drawLine(brillo(o, 0.3f, kk(o, xj, 0.3f)), Offset(xj * w, 0f), Offset(xj * w, friso), 0.8f * u)
        }
        // La marca de agua: el salobre debajo, la línea de la marea con sus lóbulos, y otra más vieja y más tenue encima.
        val marea = Path().apply {
            moveTo(0f, MAREA * w)
            for (j in 1..28) lineTo(w * j / 28f, (MAREA + 0.006f * sin(j * 1.7f) + 0.005f * (azar(6110, j) - 0.5f)) * w)
            lineTo(w, friso); lineTo(0f, friso); close()
        }
        drawPath(marea, cc(SALOBRE, o, 0.4f, 0.5f).copy(alpha = if (o) 0.22f else 0.17f))
        translate(0f, 0.01f * w) { drawPath(marea, cc(SALOBRE, o, 0.4f, 0.5f).copy(alpha = 0.08f)) }
        val linea = Path().apply {
            moveTo(0f, MAREA * w)
            for (j in 1..28) lineTo(w * j / 28f, (MAREA + 0.006f * sin(j * 1.7f) + 0.005f * (azar(6110, j) - 0.5f)) * w)
        }
        drawPath(linea, cc(SALOBRE, o, 0.4f, 0.47f).copy(alpha = 0.5f), style = Stroke(1.6f * u))
        val vieja = Path().apply {
            moveTo(0f, (MAREA - 0.024f) * w)
            for (j in 1..20) lineTo(w * j / 20f, (MAREA - 0.024f + 0.004f * sin(j * 2.3f + 1f)) * w)
        }
        drawPath(vieja, cc(SALOBRE, o, 0.4f, 0.45f).copy(alpha = 0.18f), style = Stroke(1.2f * u))
        // Las flores de humedad que suben desde la marca.
        listOf(Triple(0.12f, 0.44f, 0.035f), Triple(0.6f, 0.45f, 0.03f), Triple(0.88f, 0.43f, 0.04f)).forEachIndexed { j, (mx, my, mr) ->
            drawPath(irregular(Offset(mx * w, my * w), mr * w, mr * w * 0.6f, 6120 + j, 11, 0.3f), cc(SALOBRE, o, mx, my).copy(alpha = 0.1f))
            drawPath(irregular(Offset(mx * w, my * w), mr * w, mr * w * 0.6f, 6120 + j, 11, 0.3f), cc(SALOBRE, o, mx, my).copy(alpha = 0.18f), style = Stroke(1f * u))
        }
        // El papel despegado: la esquina caída a tiras, con su punta doblada.
        esquinaDespegada(o)
        // El friso de tablas: la moldura de arriba, las tablas de machihembra, el pie más oscuro de humedad.
        drawRect(cc(mezcla(PINO, MAR, 0.3f), o, 0.5f, friso / w + 0.1f), Offset(0f, friso), Size(w, pisa - friso))
        var xt = 0f
        while (xt < w) {
            drawLine(sombra(o, 0.4f), Offset(xt, friso), Offset(xt, pisa), 1.2f * u)
            drawLine(brillo(o, 0.25f, 0.1f), Offset(xt + 1.4f * u, friso), Offset(xt + 1.4f * u, pisa), 0.8f * u)
            xt += 0.026f * w
        }
        drawRect(sombra(o, 0.25f), Offset(0f, pisa - 0.06f * w), Size(w, 0.06f * w))
        drawRect(cc(mezcla(PINO, MAR, 0.2f), o, 0.5f, friso / w), Offset(0f, friso - 0.012f * w), Size(w, 0.014f * w))
        drawRect(brillo(o, 0.5f, 0.1f), Offset(0f, friso - 0.012f * w), Size(w, 1.2f * u))
        drawRect(sombra(o, 0.35f), Offset(0f, friso + 0.002f * w), Size(w, 1.6f * u))
        // Lo hinchado del pie: una línea de salitre.
        drawLine(cc(SALITRE, o, 0.5f, pisa / w).copy(alpha = 0.35f), Offset(0f, pisa - 0.03f * w), Offset(w, pisa - 0.034f * w), 1.4f * u)
    }

    /**
     * La esquina de arriba a la derecha, donde el papel se cayó a tiras: el yeso que asoma, el borde roto con sus fibras
     * blancas y su sombra, y una punta del papel doblada hacia abajo (el revés a la vista) con la sombra que deja.
     */
    private fun DrawScope.esquinaDespegada(o: Boolean) {
        val w = size.width; val u = density
        val bordeRoto = listOf(0.74f to 0f, 0.752f to 0.03f, 0.77f to 0.045f, 0.768f to 0.07f, 0.79f to 0.09f, 0.81f to 0.088f, 0.83f to 0.118f,
            0.86f to 0.125f, 0.875f to 0.15f, 0.9f to 0.148f, 0.92f to 0.172f, 0.95f to 0.168f, 0.965f to 0.19f, 1f to 0.195f)
        val zona = Path().apply {
            moveTo(bordeRoto[0].first * w, 0f)
            bordeRoto.forEach { (x, y) -> lineTo(x * w, y * w) }
            lineTo(w, 0f); close()
        }
        val borde = Path().apply { bordeRoto.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x * w, y * w) else lineTo(x * w, y * w) } }
        drawPath(zona, cc(mezcla(YESO, PAPEL_PARED, if (o) 0.5f else 0.25f), o, 0.88f, 0.08f))
        clipPath(zona) {
            pinceladas(Rect(0.74f * w, 0f, w, 0.2f * w), if (o) NOCHE else SALOBRE, 6131, 10, u, 0.07f)
            // Restos de cola y una grieta del yeso.
            drawPath(irregular(Offset(0.9f * w, 0.06f * w), 0.03f * w, 0.012f * w, 6132, 9, 0.4f), cc(SALOBRE, o, 0.9f, 0.06f).copy(alpha = 0.18f))
            drawPath(Path().apply { moveTo(0.83f * w, 0.02f * w); lineTo(0.86f * w, 0.05f * w); lineTo(0.85f * w, 0.08f * w); lineTo(0.88f * w, 0.11f * w) }, sombra(o, 0.3f), style = Stroke(0.8f * u))
            // La sombra del grosor del papel sobre el yeso.
            translate(-1.6f * u, -2.4f * u) { drawPath(borde, sombra(o, 0.3f), style = Stroke(3f * u)) }
        }
        // El borde roto: las fibras blancas del papel.
        drawPath(borde, cc(PAPEL, o, 0.86f, 0.12f).copy(alpha = 0.85f), style = Stroke(1.6f * u))
        // La punta doblada hacia abajo, con su sombra en la pared.
        val a = Offset(0.81f * w, 0.088f * w); val b = Offset(0.875f * w, 0.15f * w); val punta = Offset(0.818f * w, 0.175f * w)
        val doblez = pl(a.x, a.y, b.x, b.y, punta.x, punta.y)
        translate(0.012f * w, 0.012f * w) { drawPath(doblez, sombra(o, if (o) 0.4f else 0.24f)) }
        drawPath(doblez, cc(mezcla(PAPEL, SALOBRE, 0.2f), o, 0.84f, 0.13f))
        drawPath(pl(a.x, a.y, b.x, b.y, (a.x + b.x) / 2 - 0.004f * w, (a.y + b.y) / 2 + 0.012f * w), sombra(o, 0.14f))
        drawLine(brillo(o, 0.5f, kk(o, 0.84f, 0.12f)), a, b, 1f * u)
        drawPath(doblez, cc(SALOBRE, o, 0.84f, 0.13f).copy(alpha = 0.25f), style = Stroke(0.8f * u))
    }

    /** El piso de tablas que van hacia el fondo, la sombra del zócalo, la mancha oscura de humedad. */
    private fun DrawScope.piso(pisa: Float, alto: Float, o: Boolean) {
        val w = size.width; val u = density
        val tabla = mezcla(PINO, MAR, 0.3f)
        if (!o) drawRect(Brush.verticalGradient(listOf(mezcla(tabla, PINO_OSC, 0.3f), tabla, mezcla(tabla, LUZ_DIA, 0.12f)), pisa, alto), Offset(0f, pisa), Size(w, alto - pisa))
        else drawRect(Brush.verticalGradient(listOf(n(tabla, true, 0.22f), n(tabla, true, 0.06f), n(tabla, true, 0f)), pisa, alto), Offset(0f, pisa), Size(w, alto - pisa))
        val junta = sombra(o, 0.45f)
        val fuga = Offset(w * 0.55f, pisa - w * 1.1f)
        for (j in -6..6) {
            val xb = w * (0.55f + j * 0.12f)
            val xa = fuga.x + (xb - fuga.x) * (w * 1.1f) / (w * 1.1f + (alto - pisa))
            drawLine(junta, Offset(xa, pisa), Offset(xb, alto), 1.1f * u)
            drawLine(brillo(o, 0.14f, 0.05f), Offset(xa + 1.4f * u, pisa), Offset(xb + 1.4f * u, alto), 0.7f * u)
        }
        pinceladas(Rect(0f, pisa, w, alto), if (o) NOCHE else PINO_OSC, 6160, 20, u, 0.12f)
        // Las cabezas de los clavos de las tablas, en fila.
        listOf(0.42f, 0.78f).forEach { fy ->
            val y = pisa + (alto - pisa) * fy
            for (j in -6..6) {
                val xb = w * (0.55f + j * 0.12f) + w * 0.06f
                val xa = fuga.x + (xb - fuga.x) * (w * 1.1f) / (w * 1.1f + (alto - pisa))
                drawCircle(sombra(o, 0.4f), 1.1f * u, Offset(xa + (xb - xa) * fy, y))
            }
        }
        // La mancha de humedad: oscura, con su cerco.
        val m = irregular(Offset(0.56f * w, pisa + 0.07f * w), 0.2f * w, 0.05f * w, 6170, 14, 0.3f)
        drawPath(m, cc(mezcla(PINO_OSC, NOCHE, 0.3f), o, 0.56f, pisa / w + 0.07f).copy(alpha = 0.45f))
        drawPath(m, cc(mezcla(PINO_OSC, SALOBRE, 0.3f), o, 0.56f, pisa / w + 0.07f).copy(alpha = 0.5f), style = Stroke(1.6f * u))
        drawPath(irregular(Offset(0.5f * w, pisa + 0.06f * w), 0.1f * w, 0.025f * w, 6171, 10, 0.3f), cc(PINO_OSC, o, 0.5f, pisa / w + 0.06f).copy(alpha = 0.35f))
        // La sombra del mueble en el piso, junto al zócalo.
        drawRect(sombra(o, if (o) 0.5f else 0.32f), Offset(IZQ * w + 0.01f * w, pisa), Size((DER - IZQ) * w + 0.02f * w, 0.012f * w))
        drawRect(sombra(o, 0.14f), Offset(IZQ * w + 0.02f * w, pisa + 0.012f * w), Size((DER - IZQ) * w + 0.03f * w, 0.016f * w))
    }

    // ---------------------------------------------------------------- el tablero de indicios

    /** Un papel clavado en la pared, girado [giro] grados: su sombra, el papel y lo que se le dibuja encima ([dentro]). */
    private fun DrawScope.clavado(r: Rect, giro: Float, col: Color, o: Boolean, dentro: DrawScope.(Rect, Float) -> Unit) {
        val w = size.width; val u = density
        val cx = r.center.x / w; val cy = r.center.y / w
        val k = kk(o, cx, cy)
        val p = Path().apply { addRect(r) }
        rotate(giro, r.center) {
            sombraEnPared(p, cx, cy, o, 0.2f, 0.55f)
            drawPath(p, n(col, o, k))
            clipPath(p) {
                dentro(r, k)
                // Lo amarillento de los bordes y la luz del lado izquierdo.
                drawRect(sombra(o, 0.1f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
                drawRect(brillo(o, 0.25f, k), r.topLeft, Size(r.width * 0.08f, r.height))
            }
            drawRect(sombra(o, 0.18f), r.topLeft, r.size, style = Stroke(0.8f * u))
        }
    }

    /**
     * El tablero: la carta náutica, los recortes, las fotos y la hoja de la espiral, clavados; el hilo rojo de chincheta a
     * chincheta, que se enciende cerca de la llama y se pierde lejos.
     */
    private fun DrawScope.tablero(o: Boolean) {
        val w = size.width; val u = density
        fun P(x: Float, y: Float) = Offset(x * w, y * w)
        fun R(l: Float, t: Float, r: Float, b: Float) = Rect(l * w, t * w, r * w, b * w)

        // La carta náutica: tierra ocre a la izquierda con su costa de puntos a mano, el mar claro con sondas, las isóbatas
        // punteadas, una isla, la rosa con sus rumbos, los pliegues y el punto rodeado a lápiz.
        clavado(R(0.13f, 0.065f, 0.47f, 0.33f), -2f, mezcla(PAPEL, LUZ_DIA, 0.2f), o) { r, k ->
            val costa = listOf(0.0f to 0.0f, 0.34f to 0f, 0.31f to 0.08f, 0.36f to 0.14f, 0.3f to 0.2f, 0.33f to 0.3f, 0.26f to 0.36f, 0.3f to 0.44f,
                0.38f to 0.48f, 0.34f to 0.55f, 0.27f to 0.6f, 0.29f to 0.7f, 0.22f to 0.78f, 0.25f to 0.88f, 0.19f to 1f, 0f to 1f)
            val tierra = Path().apply {
                costa.forEachIndexed { i, (fx, fy) -> val q = Offset(r.left + r.width * fx, r.top + r.height * fy); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
                close()
            }
            drawPath(tierra, n(mezcla(PAPEL, SEPIA, 0.32f), o, k))
            drawPath(tierra, n(SEPIA, o, k).copy(alpha = 0.7f), style = Stroke(1.2f * u))
            // Las isóbatas: la costa repetida mar adentro, punteada.
            listOf(0.05f, 0.11f).forEach { dx ->
                val iso = Path().apply {
                    costa.drop(1).dropLast(1).forEachIndexed { i, (fx, fy) -> val q = Offset(r.left + r.width * (fx + dx), r.top + r.height * fy); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
                }
                drawPath(iso, n(MAR, o, k).copy(alpha = 0.5f), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f * u, 2.4f * u))))
            }
            // Las sondas: cifras diminutas hechas de puntos.
            for (j in 0 until 26) {
                val q = Offset(r.left + r.width * (0.45f + 0.5f * azar(6200, j)), r.top + r.height * (0.06f + 0.88f * azar(6201, j)))
                drawRect(n(MAR, o, k).copy(alpha = 0.65f), q, Size(2.6f * u, 1.3f * u))
            }
            // La isla.
            val isla = irregular(Offset(r.left + r.width * 0.62f, r.top + r.height * 0.7f), r.width * 0.05f, r.height * 0.05f, 6202, 8, 0.4f)
            drawPath(isla, n(mezcla(PAPEL, SEPIA, 0.32f), o, k)); drawPath(isla, n(SEPIA, o, k).copy(alpha = 0.7f), style = Stroke(1f * u))
            // La rosa y sus rumbos.
            val rosa = Offset(r.left + r.width * 0.8f, r.top + r.height * 0.3f); val rr = r.width * 0.075f
            for (j in 0 until 8) {
                val a = j * 0.785f
                drawLine(n(MAR, o, k).copy(alpha = 0.3f), rosa, rosa + Offset(cos(a), sin(a)) * (r.width * 0.6f), 0.7f * u)
            }
            drawCircle(n(MAR, o, k).copy(alpha = 0.7f), rr, rosa, style = Stroke(0.9f * u))
            for (j in 0 until 4) {
                val a = j * 1.5708f - 1.5708f
                val dir = Offset(cos(a), sin(a)); val per = Offset(-dir.y, dir.x)
                drawPath(pl(rosa.x + dir.x * rr * 1.25f, rosa.y + dir.y * rr * 1.25f, rosa.x + per.x * rr * 0.22f, rosa.y + per.y * rr * 0.22f,
                    rosa.x - per.x * rr * 0.22f, rosa.y - per.y * rr * 0.22f), n(if (j == 0) HILO else SEPIA, o, k).copy(alpha = 0.85f))
            }
            // Los pliegues de cuando estaba doblada en cuatro.
            drawLine(sombra(o, 0.18f), Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 1f * u)
            drawLine(brillo(o, 0.4f, k), Offset(r.center.x + 1f * u, r.top), Offset(r.center.x + 1f * u, r.bottom), 0.8f * u)
            drawLine(sombra(o, 0.15f), Offset(r.left, r.center.y), Offset(r.right, r.center.y), 1f * u)
            // El punto rodeado a lápiz, en el mar, más allá de la punta.
            val marca = Offset(r.left + r.width * 0.5f, r.top + r.height * 0.47f)
            drawOval(n(HOJALATA, o, k).copy(alpha = 0.6f), Offset(marca.x - r.width * 0.07f, marca.y - r.height * 0.07f), Size(r.width * 0.14f, r.height * 0.14f), style = Stroke(1.1f * u))
            drawOval(n(HOJALATA, o, k).copy(alpha = 0.4f), Offset(marca.x - r.width * 0.065f, marca.y - r.height * 0.08f), Size(r.width * 0.135f, r.height * 0.15f), style = Stroke(0.8f * u))
            drawLine(n(HOJALATA, o, k).copy(alpha = 0.7f), marca - Offset(3f * u, 3f * u), marca + Offset(3f * u, 3f * u), 1.1f * u)
            drawLine(n(HOJALATA, o, k).copy(alpha = 0.7f), marca + Offset(-3f * u, 3f * u), marca + Offset(3f * u, -3f * u), 1.1f * u)
            // Lo sucio, más abajo.
            pinceladas(r, if (o) NOCHE else SEPIA, 6203, 12, u, 0.07f)
        }
        // El recorte grande, arriba a la izquierda: el titular y tres columnas.
        clavado(R(0.045f, 0.035f, 0.158f, 0.19f), 4f, mezcla(PAPEL, SEPIA, 0.2f), o) { r, k ->
            drawRect(n(HOJALATA, o, k).copy(alpha = 0.75f), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.07f), Size(r.width * 0.84f, r.height * 0.07f))
            drawRect(n(HOJALATA, o, k).copy(alpha = 0.5f), Offset(r.left + r.width * 0.18f, r.top + r.height * 0.17f), Size(r.width * 0.64f, r.height * 0.035f))
            renglones(Rect(r.left + r.width * 0.04f, r.top + r.height * 0.26f, r.right - r.width * 0.04f, r.bottom - r.height * 0.04f), 3, n(SEPIA, o, k).copy(alpha = 0.55f), 6210, 2.6f * u, 1.1f * u)
        }
        // El recorte chico, arriba a la derecha, con su foto de periódico.
        clavado(R(0.485f, 0.04f, 0.585f, 0.15f), -5f, mezcla(PAPEL, SEPIA, 0.26f), o) { r, k ->
            drawRect(n(HOJALATA, o, k).copy(alpha = 0.7f), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.08f), Size(r.width * 0.84f, r.height * 0.08f))
            drawRect(n(mezcla(SEPIA, HOJALATA, 0.4f), o, k).copy(alpha = 0.7f), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.24f), Size(r.width * 0.4f, r.height * 0.3f))
            renglones(Rect(r.left + r.width * 0.52f, r.top + r.height * 0.24f, r.right - r.width * 0.06f, r.bottom - r.height * 0.05f), 1, n(SEPIA, o, k).copy(alpha = 0.55f), 6211, 2.6f * u, 1.1f * u)
            renglones(Rect(r.left + r.width * 0.06f, r.top + r.height * 0.6f, r.left + r.width * 0.5f, r.bottom - r.height * 0.05f), 1, n(SEPIA, o, k).copy(alpha = 0.55f), 6212, 2.6f * u, 1.1f * u)
        }
        // Las fotos sepia con su borde blanco: el espigón con el horizonte, una fachada, la piedra.
        clavado(R(0.035f, 0.225f, 0.125f, 0.325f), -6f, mezcla(PAPEL, LUZ_DIA, 0.4f), o) { r, k ->
            val f = Rect(r.left + r.width * 0.08f, r.top + r.height * 0.08f, r.right - r.width * 0.08f, r.bottom - r.height * 0.2f)
            drawRect(n(mezcla(SEPIA, PAPEL, 0.45f), o, k), f.topLeft, f.size)
            drawRect(n(mezcla(SEPIA, HOJALATA, 0.3f), o, k), Offset(f.left, f.top + f.height * 0.55f), Size(f.width, f.height * 0.45f))
            drawPath(pl(f.left, f.top + f.height * 0.7f, f.left + f.width * 0.7f, f.top + f.height * 0.62f, f.left + f.width * 0.72f, f.top + f.height * 0.68f, f.left, f.top + f.height * 0.82f), n(HOJALATA, o, k).copy(alpha = 0.8f))
            drawOval(n(HOJALATA, o, k).copy(alpha = 0.55f), Offset(f.left + f.width * 0.62f, f.top + f.height * 0.48f), Size(f.width * 0.18f, f.height * 0.12f))
        }
        clavado(R(0.475f, 0.17f, 0.552f, 0.265f), 5f, mezcla(PAPEL, LUZ_DIA, 0.4f), o) { r, k ->
            val f = Rect(r.left + r.width * 0.08f, r.top + r.height * 0.08f, r.right - r.width * 0.08f, r.bottom - r.height * 0.2f)
            drawRect(n(mezcla(SEPIA, PAPEL, 0.5f), o, k), f.topLeft, f.size)
            drawPath(pl(f.left + f.width * 0.15f, f.bottom, f.left + f.width * 0.15f, f.top + f.height * 0.35f, f.center.x, f.top + f.height * 0.12f,
                f.right - f.width * 0.15f, f.top + f.height * 0.35f, f.right - f.width * 0.15f, f.bottom), n(mezcla(SEPIA, HOJALATA, 0.25f), o, k))
            for (j in 0 until 2) drawRect(n(HOJALATA, o, k).copy(alpha = 0.7f), Offset(f.left + f.width * (0.3f + 0.25f * j), f.top + f.height * 0.5f), Size(f.width * 0.13f, f.height * 0.2f))
        }
        clavado(R(0.2f, 0.325f, 0.268f, 0.405f), 3f, mezcla(PAPEL, LUZ_DIA, 0.4f), o) { r, k ->
            val f = Rect(r.left + r.width * 0.09f, r.top + r.height * 0.09f, r.right - r.width * 0.09f, r.bottom - r.height * 0.22f)
            drawRect(n(mezcla(SEPIA, HOJALATA, 0.2f), o, k), f.topLeft, f.size)
            drawOval(n(mezcla(SEPIA, PAPEL, 0.4f), o, k), Offset(f.left + f.width * 0.18f, f.top + f.height * 0.2f), Size(f.width * 0.64f, f.height * 0.68f))
            drawCircle(n(HOJALATA, o, k).copy(alpha = 0.5f), f.width * 0.16f, f.center, style = Stroke(1f * u))
            drawCircle(n(HOJALATA, o, k).copy(alpha = 0.5f), f.width * 0.07f, f.center, style = Stroke(1f * u))
        }
        // La hoja de la espiral, la más cercana a la llama: una espiral a lápiz repasada, que no cierra, con lo borrado alrededor.
        clavado(R(0.565f, 0.165f, 0.665f, 0.315f), 3f, mezcla(PAPEL, LUZ_DIA, 0.5f), o) { r, k ->
            val c = Offset(r.left + r.width * 0.5f, r.top + r.height * 0.48f)
            drawOval(n(HOJALATA, o, k).copy(alpha = 0.08f), Offset(c.x - r.width * 0.4f, c.y - r.height * 0.3f), Size(r.width * 0.8f, r.height * 0.6f))
            listOf(0f to 1.6f, 0.4f to 0.9f).forEach { (fase, g) ->
                val sp = Path().apply {
                    moveTo(c.x, c.y)
                    for (j in 1..46) {
                        val a = j * 0.34f + fase
                        val rr = r.width * 0.0085f * j * (1f + 0.08f * sin(j * 1.3f))
                        lineTo(c.x + rr * cos(a), c.y + rr * sin(a) * 1.15f)
                    }
                }
                drawPath(sp, n(HOJALATA, o, k).copy(alpha = if (g > 1f) 0.75f else 0.35f), style = Stroke(g * u, cap = StrokeCap.Round))
            }
            // Las rayitas que la rodean, como si alguien hubiera contado vueltas.
            for (j in 0 until 9) {
                val a = j * 0.7f + 0.3f
                val p0 = c + Offset(cos(a), sin(a) * 1.15f) * (r.width * 0.43f)
                drawLine(n(HOJALATA, o, k).copy(alpha = 0.5f), p0, p0 + Offset(cos(a), sin(a)) * (r.width * 0.05f), 0.8f * u)
            }
            renglones(Rect(r.left + r.width * 0.1f, r.bottom - r.height * 0.13f, r.right - r.width * 0.3f, r.bottom - r.height * 0.04f), 1, n(HOJALATA, o, k).copy(alpha = 0.4f), 6220, 2.4f * u, 0.8f * u)
        }

        // Las chinchetas del hilo y el hilo, de una a otra, un poco caído.
        val pins = listOf(P(0.098f, 0.052f), P(0.296f, 0.19f), P(0.074f, 0.24f), P(0.234f, 0.335f), P(0.612f, 0.172f), P(0.514f, 0.178f), P(0.536f, 0.052f))
        val tramos = listOf(0 to 1, 1 to 2, 1 to 3, 1 to 4, 4 to 5, 5 to 6, 6 to 1)
        tramos.forEach { (a, b) ->
            val pa = pins[a]; val pb = pins[b]
            val m = (pa + pb) / 2f
            val k = kk(o, m.x / w, m.y / w)
            val cae = (pb - pa).getDistance() * 0.04f
            val hilo = Path().apply { moveTo(pa.x, pa.y); cubicTo(pa.x + (pb.x - pa.x) * 0.33f, pa.y + (pb.y - pa.y) * 0.33f + cae, pa.x + (pb.x - pa.x) * 0.66f, pa.y + (pb.y - pa.y) * 0.66f + cae, pb.x, pb.y) }
            translate(1.2f * u, 1.8f * u) { drawPath(hilo, sombra(o, 0.25f), style = Stroke(1.2f * u)) }
            drawPath(hilo, n(HILO, o, k), style = Stroke(1.4f * u))
            if (o && k > 0.4f) drawPath(hilo, LLAMA.copy(alpha = 0.35f * (k - 0.4f)), style = Stroke(0.6f * u))
        }
        // Las chinchetas: rojas las del hilo; de latón las de las esquinas.
        pins.forEach { p -> chincheta(p, o, kk(o, p.x / w, p.y / w), true) }
        listOf(P(0.14f, 0.075f), P(0.455f, 0.07f), P(0.46f, 0.31f), P(0.142f, 0.322f), P(0.04f, 0.32f), P(0.47f, 0.248f), P(0.262f, 0.398f), P(0.66f, 0.305f)).forEach { p ->
            chincheta(p, o, kk(o, p.x / w, p.y / w), false)
        }
    }

    // ---------------------------------------------------------------- la estantería

    /**
     * La estantería: su sombra en la pared, el fondo de cada hueco (tablas en sombra, con la sombra de la balda de arriba), los
     * postes, las baldas combadas con salitre y clavos, la tabla remendada, el tablero de arriba con su moldura y el zócalo.
     */
    private fun DrawScope.estanteria(d: Disposicion, o: Boolean, pisa: Float) {
        val w = size.width; val u = density
        val xL = IZQ * w; val xR = DER * w
        val t0 = TAPA0 * w; val t1 = TAPA1 * w; val t2 = CANTO * w
        val fz = d.huecos[0].top
        val sep = d.separacion
        // La sombra en la pared, a la derecha.
        drawPath(pl(xR, t0 + 0.004f * w, xR + 0.03f * w, t0 + 0.03f * w, xR + 0.03f * w, pisa, xR, pisa), sombra(o, 0.3f))
        // El fondo de cada hueco: tablas verticales, en sombra arriba, con su veta y alguna chorrera de humedad.
        d.huecos.forEachIndexed { i, r ->
            val k = kk(o, r.center.x / w, r.center.y / w)
            val fondo = mezcla(PINO_OSC, PINO, 0.35f)
            if (!o) drawRect(fondo, r.topLeft, r.size)
            else drawRect(n(fondo, true, k * 0.85f), r.topLeft, r.size)
            drawRect(if (o) Color.Transparent else LUZ_DIA.copy(alpha = 0.06f), r.topLeft, Size(r.width * 0.4f, r.height))
            var x = r.left + r.width * (0.1f + 0.1f * azar(6300, i))
            while (x < r.right) {
                drawLine(sombra(o, 0.4f), Offset(x, r.top), Offset(x, r.bottom), 1.2f * u)
                x += r.width * 0.24f
            }
            vetas(r, sombra(o, 0.18f), 6310 + i, 0.25f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
            if (azar(6320, i) < 0.6f) {
                val cx = r.left + r.width * (0.2f + 0.6f * azar(6321, i))
                drawPath(Path().apply { moveTo(cx, r.top); cubicTo(cx + 2f * u, r.top + r.height * 0.3f, cx - 2f * u, r.top + r.height * 0.6f, cx + 1f * u, r.top + r.height * 0.85f) },
                    n(mezcla(PINO_OSC, MAR, 0.5f), o, k).copy(alpha = 0.4f), style = Stroke(5f * u, cap = StrokeCap.Round))
            }
            // La sombra de la balda de arriba, y la del poste de la izquierda.
            drawRect(sombra(o, 0.45f), r.topLeft, Size(r.width, r.height * 0.1f))
            drawRect(sombra(o, 0.22f), Offset(r.left, r.top + r.height * 0.1f), Size(r.width, r.height * 0.14f))
            drawRect(sombra(o, 0.2f), r.topLeft, Size(r.width * 0.04f, r.height))
        }
        // Los postes: el de cada lado y el del centro.
        val postes = listOf(Rect(xL, t2, lado * w, pisa), Rect((1f - lado) * w, t2, xR, pisa),
            Rect(d.huecos[0].right, fz, d.huecos[1].left, d.huecos.last().bottom))
        postes.forEachIndexed { i, p ->
            if (!o) drawRect(Brush.verticalGradient(listOf(mezcla(PINO, LUZ_DIA, 0.1f), PINO, mezcla(PINO, PINO_OSC, 0.25f)), p.top, p.bottom), p.topLeft, p.size)
            else drawRect(Brush.verticalGradient(List(6) { j -> n(PINO, true, deQuinque(Offset(p.center.x / w, (p.top + p.height * j / 5f) / w))) }, p.top, p.bottom), p.topLeft, p.size)
            vetas(p, sombra(o, 0.3f), 6330 + i, 0.8f, vertical = true, grosor = 0.6f, ondulacion = 0.4f)
            drawRect(brillo(o, 0.4f, maxOf(kk(o, p.center.x / w, 0.8f), 0.15f)), p.topLeft, Size(1.6f * u, p.height))
            drawRect(sombra(o, 0.4f), Offset(p.right - p.width * 0.22f, p.top), Size(p.width * 0.22f, p.height))
            // El salitre que sube desde abajo.
            for (j in 0 until 4) {
                val y = p.bottom - p.height * (0.02f + 0.18f * azar(6340 + i, j))
                drawPath(irregular(Offset(p.left + p.width * (0.2f + 0.6f * azar(6341 + i, j)), y), p.width * 0.3f, 0.01f * w, 6342 + i * 9 + j, 7, 0.5f),
                    n(SALITRE, o, kk(o, p.center.x / w, y / w)).copy(alpha = 0.35f))
            }
        }
        // Las baldas, combadas en el medio de cada vano, con su salitre y sus clavos.
        val vanos = listOf(lado to (lado + (d.huecos[0].width / w)), (d.huecos[1].left / w) to (1f - lado))
        fun comba(fx: Float): Float {
            vanos.forEach { (a, b) -> if (fx in a..b) return 0.006f * sin(3.1416f * (fx - a) / (b - a)) }
            return 0f
        }
        val filas = d.huecos.chunked(cols).map { it[0].bottom }
        filas.forEachIndexed { i, y0 ->
            val grueso = sep
            val path = Path().apply {
                for (j in 0..30) { val fx = IZQ + (DER - IZQ) * j / 30f; val y = y0 + comba(fx) * w; if (j == 0) moveTo(fx * w, y) else lineTo(fx * w, y) }
                for (j in 30 downTo 0) { val fx = IZQ + (DER - IZQ) * j / 30f; lineTo(fx * w, y0 + grueso + comba(fx) * w) }
                close()
            }
            val fy = (y0 + grueso / 2) / w
            if (!o) drawPath(path, Brush.horizontalGradient(listOf(mezcla(PINO_LUZ, LUZ_DIA, 0.15f), PINO_LUZ, mezcla(PINO, PINO_OSC, 0.2f)), xL, xR))
            else drawPath(path, Brush.horizontalGradient(List(5) { j -> n(PINO_LUZ, true, deQuinque(Offset(IZQ + (DER - IZQ) * j / 4f, fy))) }, xL, xR))
            clipPath(path) {
                vetas(Rect(xL, y0, xR, y0 + grueso + 0.008f * w), sombra(o, 0.28f), 6350 + i, 0.6f, grosor = 0.6f, ondulacion = 0.3f)
                // El canto de arriba con luz y el de abajo en sombra, siguiendo la comba.
                translate(0f, grueso * 0.82f) { drawPath(path, sombra(o, 0.38f)) }
            }
            val borde = Path().apply { for (j in 0..30) { val fx = IZQ + (DER - IZQ) * j / 30f; val y = y0 + comba(fx) * w; if (j == 0) moveTo(fx * w, y) else lineTo(fx * w, y) } }
            drawPath(borde, brillo(o, 0.55f, maxOf(kk(o, 0.7f, fy), 0.2f)), style = Stroke(1.3f * u))
            // El salitre de los cantos.
            for (j in 0 until 6) {
                val fx = IZQ + (DER - IZQ) * (0.05f + 0.9f * azar(6360 + i, j))
                drawPath(irregular(Offset(fx * w, y0 + comba(fx) * w + 1.5f * u), (0.012f + 0.02f * azar(6361 + i, j)) * w, 1.6f * u, 6362 + i * 11 + j, 7, 0.5f),
                    n(SALITRE, o, kk(o, fx, fy)).copy(alpha = 0.5f))
            }
            // Los clavos en los postes, con su chorrera.
            listOf(IZQ + 0.015f, 0.5f, DER - 0.015f).forEachIndexed { j, fx ->
                clavo(Offset(fx * w, y0 + comba(fx) * w + grueso * 0.5f), o, kk(o, fx, fy), if (azar(6370 + i, j) < 0.65f) grueso * (1.2f + 1.4f * azar(6371, i + j)) else 0f)
            }
            // La tabla remendada: un trozo más nuevo, clavado encima.
            if (i == 1) {
                val r = Rect(0.58f * w, y0 + comba(0.7f) * w - 0.002f * w, 0.84f * w, y0 + comba(0.7f) * w + grueso * 0.92f)
                drawRect(sombra(o, 0.4f), Offset(r.left + 1.6f * u, r.top + 2f * u), r.size)
                cara(r, mezcla(PINO_LUZ, LUZ_DIA, 0.15f), o, kk(o, 0.7f, fy), 0f)
                vetas(r, sombra(o, 0.25f), 6380, 0.5f, grosor = 0.6f, ondulacion = 0.2f)
                listOf(0.04f, 0.96f).forEach { f -> listOf(0.3f, 0.7f).forEach { g -> clavo(Offset(r.left + r.width * f, r.top + r.height * g), o, kk(o, 0.7f, fy), 0f) } }
            }
        }
        // El tablero de arriba: la cara de encima, el canto con luz y la moldura del remate.
        val tapa = Rect(xL - 0.012f * w, t0, xR + 0.012f * w, t1)
        if (!o) drawRect(Brush.horizontalGradient(listOf(mezcla(PINO_LUZ, LUZ_DIA, 0.3f), PINO_LUZ, mezcla(PINO_LUZ, PINO, 0.5f)), tapa.left, tapa.right), tapa.topLeft, tapa.size)
        else drawRect(Brush.horizontalGradient(List(6) { j -> n(PINO_LUZ, true, deQuinque(Offset(j / 5f, TAPA0))) }, tapa.left, tapa.right), tapa.topLeft, tapa.size)
        vetas(tapa, sombra(o, 0.22f), 6390, 0.8f, grosor = 0.5f, ondulacion = 0.2f)
        drawRect(sombra(o, 0.3f), tapa.topLeft, Size(tapa.width, 1.2f * u))
        // El cerco de un vaso en el tablero.
        drawOval(cc(SALOBRE, o, 0.6f, TAPA0).copy(alpha = 0.35f), Offset(0.565f * w, t0 + (t1 - t0) * 0.2f), Size(0.04f * w, (t1 - t0) * 0.6f), style = Stroke(1f * u))
        val canto = Rect(tapa.left, t1, tapa.right, t2)
        if (!o) drawRect(mezcla(PINO, PINO_LUZ, 0.4f), canto.topLeft, canto.size)
        else drawRect(Brush.horizontalGradient(List(6) { j -> n(mezcla(PINO, PINO_LUZ, 0.4f), true, deQuinque(Offset(j / 5f, TAPA1))) }, canto.left, canto.right), canto.topLeft, canto.size)
        drawRect(brillo(o, 0.75f, 0.95f), canto.topLeft, Size(canto.width, 1.3f * u))
        drawRect(sombra(o, 0.4f), Offset(canto.left, t2 - 1.4f * u), Size(canto.width, 1.4f * u))
        // El remate: un friso con su moldura y una ranura, entre el canto y la primera fila.
        val remate = Rect(xL, t2, xR, fz)
        if (!o) drawRect(Brush.horizontalGradient(listOf(mezcla(PINO, LUZ_DIA, 0.12f), PINO, mezcla(PINO, PINO_OSC, 0.4f)), xL, xR), remate.topLeft, remate.size)
        else drawRect(Brush.horizontalGradient(List(6) { j -> n(PINO, true, deQuinque(Offset(j / 5f, (t2 + fz) / 2f / w))) }, xL, xR), remate.topLeft, remate.size)
        drawRect(sombra(o, 0.45f), remate.topLeft, Size(remate.width, 2.4f * u))
        val ym = remate.top + remate.height * 0.45f
        drawLine(sombra(o, 0.4f), Offset(xL, ym), Offset(xR, ym), 1.4f * u)
        drawLine(brillo(o, 0.45f, kk(o, 0.7f, ym / w)), Offset(xL, ym + 1.6f * u), Offset(xR, ym + 1.6f * u), 0.9f * u)
        vetas(remate, sombra(o, 0.2f), 6391, 0.6f, grosor = 0.5f, ondulacion = 0.3f)
        for (j in 0 until 7) {
            val fx = IZQ + (DER - IZQ) * (0.04f + 0.92f * azar(6392, j))
            drawPath(irregular(Offset(fx * w, remate.bottom - 2f * u), (0.012f + 0.016f * azar(6393, j)) * w, 1.8f * u, 6394 + j, 7, 0.5f), n(SALITRE, o, kk(o, fx, ym / w)).copy(alpha = 0.4f))
        }
        listOf(IZQ + 0.015f, DER - 0.015f).forEachIndexed { j, fx -> clavo(Offset(fx * w, ym - 0.008f * w), o, kk(o, fx, ym / w), 0.04f * w * (1 - j) + 0.02f * w) }
        // El zócalo: más metido, hinchado y comido abajo, con sus patas y la placa de «y N más».
        val b = d.base
        val zoc = Rect(xL + 0.01f * w, b.top, xR - 0.01f * w, pisa)
        drawRect(cc(mezcla(PINO, PINO_OSC, 0.4f), o, 0.5f, zoc.center.y / w), zoc.topLeft, zoc.size)
        drawRect(sombra(o, 0.45f), zoc.topLeft, Size(zoc.width, zoc.height * 0.25f))
        vetas(zoc, sombra(o, 0.25f), 6395, 0.6f, grosor = 0.6f, ondulacion = 0.3f)
        drawRect(n(mezcla(PINO_OSC, SALOBRE, 0.3f), o, 0f).copy(alpha = 0.45f), Offset(zoc.left, pisa - zoc.height * 0.35f), Size(zoc.width, zoc.height * 0.35f))
        drawLine(n(SALITRE, o, 0.05f).copy(alpha = 0.45f), Offset(zoc.left, pisa - zoc.height * 0.36f), Offset(zoc.right, pisa - zoc.height * 0.33f), 1.2f * u)
        listOf(xL, xR - 0.04f * w).forEach { x ->
            val pata = Rect(x, b.top, x + 0.04f * w, pisa + 0.004f * w)
            drawRect(cc(PINO, o, x / w, pata.center.y / w), pata.topLeft, pata.size)
            drawRect(brillo(o, 0.35f, 0.05f), pata.topLeft, Size(1.4f * u, pata.height))
            drawRect(sombra(o, 0.35f), Offset(pata.right - pata.width * 0.3f, pata.top), Size(pata.width * 0.3f, pata.height))
        }
        if (d.mas > 0) placa.let { p ->
            val r = d.enBase(p)
            drawRect(sombra(o, 0.4f), Offset(r.left + 2f * u, r.top + 2.4f * u), r.size)
            drawRect(n(mezcla(LATON, VERDIN, 0.35f), o, 0.2f), r.topLeft, r.size)
            val dentro = Rect(r.left + 3f * u, r.top + 3f * u, r.right - 3f * u, r.bottom - 3f * u)
            drawRect(tarjeta(o, 0f), dentro.topLeft, dentro.size)
            drawRect(brillo(o, 0.6f, 0.4f), r.topLeft, Size(r.width, 1f * u))
            listOf(r.left + 1.5f * u, r.right - 1.5f * u).forEach { x -> drawCircle(n(HOJALATA, o, 0.2f), 1f * u, Offset(x, r.center.y)) }
        }
    }

    // ---------------------------------------------------------------- encima de la estantería

    /** La pila de libros de registro, uno con el lomo roto, y encima la caja de placas fotográficas. */
    private fun DrawScope.libros(o: Boolean) {
        val w = size.width; val u = density
        val k = kk(o, 0.15f, 0.5f)
        fun c(col: Color) = n(col, o, k)
        val pila = listOf(
            Triple(0.052f, 0.25f, 0.027f) to mezcla(PINO_OSC, OXIDO, 0.35f),
            Triple(0.064f, 0.238f, 0.024f) to mezcla(MAR, HOJALATA, 0.25f),
            Triple(0.046f, 0.226f, 0.021f) to mezcla(LACRE, PINO_OSC, 0.3f),
            Triple(0.07f, 0.244f, 0.019f) to mezcla(SEPIA, PINO, 0.3f),
        )
        val total = pila.sumOf { it.first.third.toDouble() }.toFloat()
        sombraEnPared(pl(0.05f * w, (TAPA1 - total - 0.035f) * w, 0.25f * w, (TAPA1 - total) * w, 0.25f * w, TAPA1 * w, 0.05f * w, TAPA1 * w), 0.15f, 0.5f, o, 0.24f)
        drawOval(sombra(o, 0.4f), Offset(0.06f * w, TAPA1 * w - 0.006f * w), Size(0.21f * w, 0.012f * w))
        var y = TAPA1 * w
        pila.forEachIndexed { i, (m, col) ->
            val (x0, x1, g) = m
            val r = Rect(x0 * w, y - g * w, x1 * w, y)
            drawRect(c(col), r.topLeft, r.size)
            // El lomo redondeado: luz arriba, sombra abajo; las esquinas de piel, los nervios y el tejuelo.
            drawRect(brillo(o, 0.35f, k), r.topLeft, Size(r.width, r.height * 0.22f))
            drawRect(sombra(o, 0.35f), Offset(r.left, r.bottom - r.height * 0.28f), Size(r.width, r.height * 0.28f))
            drawRect(sombra(o, 0.25f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
            listOf(r.left, r.right - r.width * 0.12f).forEach { x -> drawRect(c(mezcla(PINO_OSC, OXIDO, 0.2f)), Offset(x, r.top), Size(r.width * 0.12f, r.height)) }
            listOf(0.2f, 0.26f, 0.74f, 0.8f).forEach { f ->
                drawLine(sombra(o, 0.4f), Offset(r.left + r.width * f, r.top), Offset(r.left + r.width * f, r.bottom), 1.2f * u)
                drawLine(brillo(o, 0.3f, k), Offset(r.left + r.width * f + 1.2f * u, r.top), Offset(r.left + r.width * f + 1.2f * u, r.bottom), 0.7f * u)
            }
            if (i != 1) {
                val tj = Rect(r.left + r.width * 0.36f, r.top + r.height * 0.25f, r.left + r.width * 0.6f, r.bottom - r.height * 0.25f)
                drawRect(c(mezcla(PAPEL, SEPIA, 0.25f)), tj.topLeft, tj.size)
                drawRect(c(SEPIA).copy(alpha = 0.6f), Offset(tj.left + tj.width * 0.15f, tj.center.y - 0.6f * u), Size(tj.width * 0.7f, 1.2f * u))
            } else {
                // El lomo roto: falta la piel y se ven los cuadernillos cosidos; el jirón cuelga sobre el libro de abajo.
                val roto = Rect(r.left + r.width * 0.4f, r.top + 1f * u, r.left + r.width * 0.72f, r.bottom - 1f * u)
                drawRect(c(mezcla(PAPEL, SEPIA, 0.3f)), roto.topLeft, roto.size)
                var x = roto.left
                while (x < roto.right) { drawLine(sombra(o, 0.35f), Offset(x, roto.top), Offset(x, roto.bottom), 0.8f * u); x += 2.6f * u }
                drawLine(c(BRAMANTE), Offset(roto.left, roto.center.y), Offset(roto.right, roto.center.y), 0.8f * u)
                val jiron = Path().apply {
                    moveTo(roto.right, roto.bottom); lineTo(roto.right + r.width * 0.12f, roto.bottom)
                    cubicTo(roto.right + r.width * 0.12f, roto.bottom + 0.012f * w, roto.right + r.width * 0.06f, roto.bottom + 0.02f * w, roto.right + r.width * 0.02f, roto.bottom + 0.024f * w)
                    close()
                }
                translate(1.2f * u, 1.6f * u) { drawPath(jiron, sombra(o, 0.4f)) }
                drawPath(jiron, c(mezcla(col, PAPEL, 0.12f)))
                drawPath(jiron, brillo(o, 0.3f, k), style = Stroke(0.7f * u))
            }
            y -= g * w
        }
        // La caja de placas: cartón negro verdoso, la tapa que monta y la etiqueta de papel.
        val caja = Rect(0.088f * w, y - 0.034f * w, 0.19f * w, y)
        drawRect(sombra(o, 0.35f), Offset(caja.left + 2f * u, caja.bottom - 1f * u), Size(caja.width, 2.4f * u))
        cara(caja, mezcla(HOJALATA, MAR, 0.35f), o, k, 0f)
        val tapa = Rect(caja.left - 0.003f * w, caja.top - 0.002f * w, caja.right + 0.003f * w, caja.top + caja.height * 0.42f)
        cara(tapa, mezcla(HOJALATA, MAR, 0.45f), o, k, 0.04f)
        val et = Rect(caja.left + caja.width * 0.22f, caja.top + caja.height * 0.52f, caja.left + caja.width * 0.7f, caja.bottom - caja.height * 0.12f)
        drawRect(c(mezcla(PAPEL, SEPIA, 0.15f)), et.topLeft, et.size)
        drawRect(c(HILO).copy(alpha = 0.7f), Offset(et.left, et.top + et.height * 0.18f), Size(et.width, 0.8f * u))
        drawRect(c(HOJALATA).copy(alpha = 0.5f), Offset(et.left + et.width * 0.15f, et.top + et.height * 0.5f), Size(et.width * 0.6f, 1f * u))
    }

    /** La piedra tallada: redonda y pesada, con los surcos en espiral (la ranura en sombra y su labio con luz). */
    private fun DrawScope.piedra(o: Boolean) {
        val w = size.width; val u = density
        val k = kk(o, 0.33f, 0.51f)
        val c = Offset(0.328f * w, 0.507f * w)
        val forma = pl(0.286f * w, TAPA1 * w, 0.281f * w, 0.502f * w, 0.29f * w, 0.47f * w, 0.316f * w, 0.456f * w, 0.35f * w, 0.459f * w,
            0.371f * w, 0.476f * w, 0.379f * w, 0.51f * w, 0.377f * w, TAPA1 * w)
        sombraEnPared(forma, 0.33f, 0.5f, o, 0.24f, 0.8f)
        drawOval(sombra(o, 0.45f), Offset(0.29f * w, TAPA1 * w - 0.005f * w), Size(0.105f * w, 0.01f * w))
        val gris = mezcla(MAR, PINO_OSC, 0.35f)
        drawPath(forma, n(gris, o, k))
        clipPath(forma) {
            // Las caras: la de arriba y la de la izquierda con luz, la de la derecha en sombra.
            drawPath(pl(0.27f * w, 0.5f * w, 0.29f * w, 0.47f * w, 0.316f * w, 0.456f * w, 0.35f * w, 0.459f * w, 0.371f * w, 0.476f * w, 0.33f * w, 0.474f * w, 0.3f * w, 0.49f * w, 0.296f * w, TAPA1 * w, 0.27f * w, TAPA1 * w),
                n(mezcla(gris, LUZ_DIA, 0.28f), o, k))
            drawPath(pl(0.362f * w, 0.47f * w, 0.39f * w, 0.47f * w, 0.39f * w, TAPA1 * w, 0.364f * w, TAPA1 * w), sombra(o, 0.32f))
            pinceladas(Rect(0.28f * w, 0.455f * w, 0.38f * w, TAPA1 * w), if (o) NOCHE else SALITRE, 6400, 9, u, 0.14f)
            // La espiral tallada en la cara de delante, con rayas cortas alrededor y un surco que la enmarca.
            val sp = Path().apply {
                moveTo(c.x, c.y)
                for (j in 1..44) { val a = j * 0.33f; val rr = 0.0005f * w * j; lineTo(c.x + rr * cos(a), c.y + rr * sin(a)) }
            }
            drawPath(sp, sombra(o, 0.6f), style = Stroke(2f * u, cap = StrokeCap.Round))
            translate(0.9f * u, 1f * u) { drawPath(sp, brillo(o, 0.45f, k), style = Stroke(0.6f * u, cap = StrokeCap.Round)) }
            for (j in 0 until 7) {
                val a = j * 0.9f + 0.4f
                val p0 = c + Offset(cos(a), sin(a)) * (0.027f * w)
                drawLine(sombra(o, 0.5f), p0, p0 + Offset(cos(a), sin(a)) * (0.008f * w), 1.4f * u, StrokeCap.Round)
            }
            // El verdín y el salitre de haber estado en el agua.
            drawPath(irregular(Offset(0.3f * w, 0.535f * w), 0.02f * w, 0.008f * w, 6401, 8, 0.4f), n(VERDIN, o, k).copy(alpha = 0.45f))
            drawPath(irregular(Offset(0.365f * w, 0.465f * w), 0.012f * w, 0.006f * w, 6402, 8, 0.4f), n(VERDIN, o, k).copy(alpha = 0.4f))
        }
        drawPath(forma, brillo(o, 0.3f, k), style = Stroke(0.8f * u))
    }

    /** La lupa, apoyada en la piedra: el aro de latón, el cristal que agranda y el mango de madera. */
    private fun DrawScope.lupa(o: Boolean) {
        val w = size.width; val u = density
        val k = kk(o, 0.28f, 0.52f)
        val c = Offset(0.272f * w, 0.512f * w); val r = 0.021f * w
        val mango0 = Offset(0.258f * w, 0.53f * w); val mango1 = Offset(0.226f * w, TAPA1 * w - 0.003f * w)
        drawLine(sombra(o, 0.35f), mango0 + Offset(2f * u, 2.4f * u), mango1 + Offset(3f * u, 1f * u), 0.009f * w, StrokeCap.Round)
        drawCircle(sombra(o, 0.3f), r * 1.05f, c + Offset(3f * u, 3f * u), style = Stroke(r * 0.3f))
        drawLine(n(mezcla(PINO_OSC, LACRE, 0.3f), o, k), mango0, mango1, 0.008f * w, StrokeCap.Round)
        drawLine(brillo(o, 0.4f, k), mango0 - Offset(1.4f * u, 0.6f * u), mango1 - Offset(1.4f * u, 0.4f * u), 1f * u, StrokeCap.Round)
        drawLine(n(LATON, o, k), mango0, mango0 + (mango1 - mango0) * 0.18f, 0.01f * w, StrokeCap.Butt)
        // El cristal: lo de detrás un poco más claro y grande, el reflejo de la luz.
        drawCircle(n(mezcla(MAR, LUZ_DIA, 0.45f), o, k).copy(alpha = 0.55f), r, c)
        drawArc(Color.White.copy(alpha = if (o) 0.3f * k else 0.55f), 200f, 60f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawCircle(n(mezcla(LATON, VERDIN, 0.25f), o, k), r, c, style = Stroke(0.0048f * w))
        drawArc(brillo(o, 0.7f, k + 0.1f), 190f, 80f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1f * u))
        drawArc(sombra(o, 0.4f), 20f, 90f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.4f * u))
    }

    /** El frasco: vidrio con su tapa y su pinza, el agua turbia y unas formas blandas que no se distinguen; de noche, apenas fosforecen. */
    private fun DrawScope.frasco(o: Boolean) {
        val w = size.width; val u = density
        val k = kk(o, 0.47f, 0.47f)
        val l = 0.414f * w; val r = 0.528f * w; val top = 0.402f * w; val pie = TAPA1 * w
        val cuerpo = Path().apply {
            moveTo(l, pie - 0.006f * w)
            lineTo(l, top + 0.018f * w)
            cubicTo(l, top + 0.004f * w, l + 0.014f * w, top, l + 0.024f * w, top)
            lineTo(r - 0.024f * w, top)
            cubicTo(r - 0.014f * w, top, r, top + 0.004f * w, r, top + 0.018f * w)
            lineTo(r, pie - 0.006f * w)
            cubicTo(r, pie, r - 0.004f * w, pie, r - 0.01f * w, pie)
            lineTo(l + 0.01f * w, pie)
            cubicTo(l + 0.004f * w, pie, l, pie, l, pie - 0.006f * w); close()
        }
        sombraEnPared(cuerpo, 0.47f, 0.47f, o, 0.2f, 0.8f)
        drawOval(sombra(o, 0.45f), Offset(l + 0.004f * w, pie - 0.005f * w), Size(r - l + 0.01f * w, 0.01f * w))
        // El agua turbia y lo de dentro.
        val agua = mezcla(MAR, SALOBRE, 0.35f)
        drawPath(cuerpo, n(agua, o, k * 0.8f).copy(alpha = 0.92f))
        clipPath(cuerpo) {
            drawRect(n(mezcla(agua, LUZ_DIA, 0.2f), o, k).copy(alpha = 0.6f), Offset(l, top), Size(r - l, 0.012f * w))
            // Lo de dentro: formas blandas sin detalle; de noche, con una fosforescencia verde pálida que tiñe el agua.
            if (o) mancha(Offset(0.47f * w, 0.49f * w), 0.06f * w, FOSFORO.copy(alpha = 0.3f))
            val blando = if (o) mezcla(FOSFORO, NOCHE, 0.38f) else mezcla(PAPEL, MAR, 0.45f)
            listOf(Triple(0.462f, 0.47f, 0.032f), Triple(0.49f, 0.495f, 0.026f), Triple(0.45f, 0.515f, 0.022f)).forEachIndexed { j, (x, y, rr) ->
                val forma = irregular(Offset(x * w, y * w), rr * w, rr * w * 0.75f, 6410 + j, 10, 0.22f)
                drawPath(forma, blando.copy(alpha = if (o) 0.5f else 0.42f))
                if (o) scale(0.55f, Offset(x * w - rr * w * 0.15f, y * w - rr * w * 0.1f)) { drawPath(forma, FOSFORO.copy(alpha = 0.2f)) }
            }
            // El poso del fondo y lo verdoso del vidrio a los lados.
            drawRect(n(SALOBRE, o, k).copy(alpha = 0.5f), Offset(l, pie - 0.012f * w), Size(r - l, 0.012f * w))
            drawRect(sombra(o, 0.25f), Offset(r - 0.018f * w, top), Size(0.018f * w, pie - top))
            drawRect(n(VERDIN, o, k).copy(alpha = 0.18f), Offset(l, top), Size(0.008f * w, pie - top))
        }
        // El brillo del vidrio, del lado de la luz.
        drawRect(Color.White.copy(alpha = if (o) 0.06f + 0.2f * k else 0.42f), Offset(l + 0.007f * w, top + 0.02f * w), Size(0.006f * w, pie - top - 0.035f * w))
        drawRect(Color.White.copy(alpha = if (o) 0.04f + 0.1f * k else 0.2f), Offset(l + 0.016f * w, top + 0.02f * w), Size(0.002f * w, pie - top - 0.05f * w))
        drawPath(cuerpo, n(mezcla(VERDIN, LUZ_DIA, 0.4f), o, k).copy(alpha = 0.5f), style = Stroke(1.1f * u))
        // De noche, el vidrio teñido por dentro de verde pálido.
        if (o) drawPath(cuerpo, FOSFORO.copy(alpha = 0.32f), style = Stroke(1.4f * u))
        // El cuello, la tapa de vidrio y la pinza de alambre.
        val cuello = Rect(l + 0.016f * w, top - 0.012f * w, r - 0.016f * w, top + 0.002f * w)
        drawRect(n(mezcla(VERDIN, LUZ_DIA, 0.3f), o, k).copy(alpha = 0.7f), cuello.topLeft, cuello.size)
        val tapa = Rect(l + 0.012f * w, top - 0.026f * w, r - 0.012f * w, top - 0.012f * w)
        drawRoundRect(n(mezcla(VERDIN, LUZ_DIA, 0.35f), o, k), tapa.topLeft, tapa.size, CornerRadius(4f * u))
        if (o) drawRoundRect(FOSFORO.copy(alpha = 0.14f), tapa.topLeft, tapa.size, CornerRadius(4f * u))
        drawRect(brillo(o, 0.6f, k), Offset(tapa.left + 2f * u, tapa.top + 1f * u), Size(tapa.width * 0.5f, 1f * u))
        drawRect(sombra(o, 0.3f), Offset(tapa.left, tapa.bottom - 1.4f * u), Size(tapa.width, 1.4f * u))
        drawPath(Path().apply { moveTo(cuello.left, cuello.bottom); lineTo(tapa.left + tapa.width * 0.3f, tapa.top); lineTo(tapa.right - tapa.width * 0.3f, tapa.top); lineTo(cuello.right, cuello.bottom) },
            n(mezcla(HOJALATA, LUZ_DIA, 0.3f), o, k), style = Stroke(0.7f * u))
        // La etiqueta atada al cuello, escrita a mano.
        val et = Rect(0.44f * w, 0.452f * w, 0.497f * w, 0.488f * w)
        rotate(-4f, et.center) {
            drawRect(sombra(o, 0.3f), Offset(et.left + 1.4f * u, et.top + 1.6f * u), et.size)
            drawRect(n(mezcla(PAPEL, SEPIA, 0.2f), o, k), et.topLeft, et.size)
            // De noche, el papel trasluce un poco el verde de detrás, más por sus bordes.
            if (o) { drawRect(FOSFORO.copy(alpha = 0.12f), et.topLeft, et.size); drawRect(FOSFORO.copy(alpha = 0.18f), et.topLeft, et.size, style = Stroke(0.9f * u)) }
            listOf(0.3f, 0.55f, 0.78f).forEach { f -> drawLine(n(SEPIA, o, k).copy(alpha = 0.7f), Offset(et.left + et.width * 0.12f, et.top + et.height * f), Offset(et.right - et.width * (0.15f + 0.2f * f), et.top + et.height * f), 0.8f * u) }
        }
        drawLine(n(BRAMANTE, o, k), Offset(cuello.left + 2f * u, cuello.bottom), Offset(et.left + et.width * 0.4f, et.top), 0.8f * u)
        drawLine(n(BRAMANTE, o, k), Offset(cuello.right - 2f * u, cuello.bottom), Offset(et.left + et.width * 0.5f, et.top), 0.8f * u)
    }

    /** El quinqué: pie de latón, depósito de vidrio con el petróleo, el mechero, el tubo de vidrio y, de noche, la llama. */
    private fun DrawScope.quinque(o: Boolean) {
        val w = size.width; val u = density
        val k = if (o) 1f else 0f
        fun c(col: Color) = n(col, o, 0.95f)
        fun P(x: Float, y: Float) = Offset(x * w, y * w)
        val cx = LUZ.x
        val silueta = Path().apply {
            moveTo((cx - 0.042f) * w, TAPA1 * w); lineTo((cx - 0.026f) * w, 0.532f * w); lineTo((cx - 0.04f) * w, 0.505f * w)
            lineTo((cx - 0.02f) * w, 0.474f * w); lineTo((cx - 0.016f) * w, 0.44f * w); lineTo((cx - 0.011f) * w, 0.34f * w)
            lineTo((cx + 0.011f) * w, 0.34f * w); lineTo((cx + 0.016f) * w, 0.44f * w); lineTo((cx + 0.02f) * w, 0.474f * w)
            lineTo((cx + 0.04f) * w, 0.505f * w); lineTo((cx + 0.026f) * w, 0.532f * w); lineTo((cx + 0.042f) * w, TAPA1 * w); close()
        }
        if (!o) sombraEnPared(silueta, cx, 0.45f, o, 0.2f, 1f)
        drawOval(sombra(o, if (o) 0.25f else 0.45f), Offset((cx - 0.04f) * w, TAPA1 * w - 0.005f * w), Size(0.095f * w, 0.01f * w))
        val laton = mezcla(LATON, VERDIN, 0.15f)
        // El pie escalonado.
        drawPath(pl((cx - 0.042f) * w, TAPA1 * w, (cx + 0.042f) * w, TAPA1 * w, (cx + 0.032f) * w, 0.54f * w, (cx - 0.032f) * w, 0.54f * w), c(laton))
        drawRect(c(mezcla(laton, PINO_OSC, 0.3f)), Offset((cx - 0.026f) * w, 0.528f * w), Size(0.052f * w, 0.012f * w))
        drawRect(c(laton), Offset((cx - 0.008f) * w, 0.52f * w), Size(0.016f * w, 0.01f * w))
        // El depósito de vidrio verdoso, con el petróleo y su brillo.
        val dep = Rect((cx - 0.04f) * w, 0.48f * w, (cx + 0.04f) * w, 0.524f * w)
        drawOval(c(mezcla(VERDIN, LUZ_DIA, 0.25f)).copy(alpha = 0.85f), dep.topLeft, dep.size)
        clipPath(Path().apply { addOval(dep) }) {
            drawRect(c(mezcla(LLAMA, SALOBRE, 0.45f)).copy(alpha = 0.8f), Offset(dep.left, dep.top + dep.height * 0.48f), Size(dep.width, dep.height))
            drawRect(sombra(o, 0.3f), Offset(dep.right - dep.width * 0.3f, dep.top), Size(dep.width * 0.3f, dep.height))
        }
        drawArc(Color.White.copy(alpha = if (o) 0.45f else 0.55f), 190f, 60f, false, Offset(dep.left + dep.width * 0.1f, dep.top + dep.height * 0.12f), Size(dep.width * 0.8f, dep.height * 0.76f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        // El mechero: el collar, la galería calada y la rueda de la mecha.
        drawRect(c(laton), Offset((cx - 0.02f) * w, 0.472f * w), Size(0.04f * w, 0.01f * w))
        drawRect(c(mezcla(laton, LUZ_DIA, 0.3f)), Offset((cx - 0.02f) * w, 0.472f * w), Size(0.04f * w, 1f * u))
        drawRect(c(mezcla(laton, PINO_OSC, 0.2f)), Offset((cx - 0.024f) * w, 0.46f * w), Size(0.048f * w, 0.012f * w))
        for (j in 0 until 6) drawCircle(c(PINO_OSC), 0.9f * u, Offset((cx - 0.019f + j * 0.0076f) * w, 0.466f * w))
        drawLine(c(laton), P(cx + 0.024f, 0.476f), P(cx + 0.036f, 0.476f), 1.2f * u)
        drawCircle(c(laton), 0.006f * w, P(cx + 0.038f, 0.476f))
        drawCircle(sombra(o, 0.3f), 0.003f * w, P(cx + 0.039f, 0.477f))
        // El tubo: la panza abajo y el cuello largo, con el tizne arriba.
        val tubo = Path().apply {
            moveTo((cx - 0.013f) * w, 0.46f * w)
            cubicTo((cx - 0.024f) * w, 0.45f * w, (cx - 0.02f) * w, 0.425f * w, (cx - 0.011f) * w, 0.418f * w)
            lineTo((cx - 0.009f) * w, 0.342f * w); lineTo((cx + 0.009f) * w, 0.342f * w); lineTo((cx + 0.011f) * w, 0.418f * w)
            cubicTo((cx + 0.02f) * w, 0.425f * w, (cx + 0.024f) * w, 0.45f * w, (cx + 0.013f) * w, 0.46f * w); close()
        }
        if (o) {
            drawPath(tubo, LLAMA.copy(alpha = 0.42f))
            drawRect(LLAMA.copy(alpha = 0.25f), Offset((cx - 0.009f) * w, 0.36f * w), Size(0.018f * w, 0.06f * w))
        } else drawPath(tubo, LUZ_DIA.copy(alpha = 0.22f))
        drawRect(n(HOJALATA, o, 0.6f).copy(alpha = 0.35f), Offset((cx - 0.009f) * w, 0.342f * w), Size(0.018f * w, 0.012f * w))
        drawPath(tubo, Color.White.copy(alpha = if (o) 0.4f else 0.5f), style = Stroke(1f * u))
        drawLine(Color.White.copy(alpha = if (o) 0.5f else 0.65f), P(cx - 0.005f, 0.35f), P(cx - 0.006f, 0.414f), 1.2f * u)
        // La mecha y, de noche, la llama.
        drawRect(c(PINO_OSC), Offset((cx - 0.004f) * w, 0.452f * w), Size(0.008f * w, 0.006f * w))
        if (o) {
            val f = Path().apply {
                moveTo((cx - 0.006f) * w, 0.454f * w)
                cubicTo((cx - 0.008f) * w, 0.44f * w, (cx - 0.002f) * w, 0.43f * w, cx * w, 0.418f * w)
                cubicTo((cx + 0.002f) * w, 0.43f * w, (cx + 0.008f) * w, 0.44f * w, (cx + 0.006f) * w, 0.454f * w); close()
            }
            drawPath(f, mezcla(LLAMA, OXIDO, 0.25f))
            translate(0f, 0.003f * w) { scaleTo(f, 0.6f, Offset(cx * w, 0.452f * w)) }
        }
    }

    /** El corazón pálido de la llama: la misma forma, más chica y más clara. */
    private fun DrawScope.scaleTo(f: Path, s: Float, pivote: Offset) {
        scale(s, pivote) { drawPath(f, Color(0xFFFFF0C8)) }
    }

    /** El catalejo de latón, tumbado, cortado por el borde: el cañón forrado de cuero y los tramos que se recogen. */
    private fun DrawScope.catalejo(o: Boolean) {
        val w = size.width; val u = density
        val eje = TAPA1 * w - 0.021f * w
        val k = kk(o, 0.85f, 0.53f)
        drawOval(sombra(o, 0.42f), Offset(0.82f * w, TAPA1 * w - 0.006f * w), Size(0.24f * w, 0.012f * w))
        val tramos = listOf(Triple(0.792f, 0.812f, 0.0095f), Triple(0.812f, 0.86f, 0.012f), Triple(0.86f, 0.91f, 0.015f), Triple(0.91f, 1.03f, 0.021f))
        sombraEnPared(pl(0.79f * w, eje - 0.01f * w, 0.91f * w, eje - 0.015f * w, 1.03f * w, eje - 0.021f * w, 1.03f * w, eje + 0.021f * w, 0.79f * w, eje + 0.01f * w), 0.88f, 0.53f, o, 0.2f, 0.6f)
        tramos.forEachIndexed { i, (a, b, r) ->
            val rect = Rect(a * w, eje - r * w, b * w, eje + r * w)
            val col = if (i == 3) mezcla(PINO_OSC, OXIDO, 0.4f) else mezcla(LATON, VERDIN, 0.12f * i)
            drawRect(n(col, o, k), rect.topLeft, rect.size)
            drawRect(brillo(o, if (i == 3) 0.3f else 0.6f, k), Offset(rect.left, rect.top + rect.height * 0.15f), Size(rect.width, rect.height * 0.18f))
            drawRect(sombra(o, 0.4f), Offset(rect.left, rect.bottom - rect.height * 0.32f), Size(rect.width, rect.height * 0.32f))
            if (i == 3) {
                // Las costuras del cuero.
                var x = rect.left + 0.01f * w
                while (x < rect.right) { drawLine(sombra(o, 0.3f), Offset(x, rect.top), Offset(x - 0.006f * w, rect.bottom), 0.8f * u); x += 0.012f * w }
            }
            // El aro del extremo de cada tramo.
            drawRect(n(mezcla(LATON, PINO_OSC, 0.15f), o, k), Offset(rect.left, rect.top - 0.0015f * w), Size(0.004f * w, rect.height + 0.003f * w))
            drawRect(brillo(o, 0.6f, k), Offset(rect.left, rect.top - 0.0015f * w), Size(1f * u, rect.height + 0.003f * w))
        }
        // El ocular con su tapa.
        drawRect(n(PINO_OSC, o, k), Offset(0.785f * w, eje - 0.007f * w), Size(0.008f * w, 0.014f * w))
    }

    /** El manojo de llaves colgado de un clavo en el poste del centro. */
    private fun DrawScope.llaves(d: Disposicion, o: Boolean) {
        val w = size.width; val u = density
        val cx = (d.huecos[0].right + d.huecos[1].left) / 2f
        val cy = d.huecos[0].top + d.huecos[0].height * 0.22f
        val k = kk(o, cx / w, cy / w)
        val hierro = mezcla(HOJALATA, OXIDO, 0.35f)
        val aro = Offset(cx, cy + 0.012f * w)
        drawCircle(sombra(o, 0.35f), 0.011f * w, aro + Offset(2f * u, 2.6f * u), style = Stroke(1.6f * u))
        listOf(-14f, 4f, 18f).forEachIndexed { i, g ->
            rotate(g, Offset(aro.x, aro.y + 0.011f * w)) {
                val top = aro.y + 0.011f * w
                val largo = (0.042f + 0.01f * i) * w
                translate(2f * u, 2.6f * u) {
                    drawLine(sombra(o, 0.35f), Offset(aro.x, top + 0.008f * w), Offset(aro.x, top + largo), 2.2f * u)
                }
                drawCircle(n(hierro, o, k), 0.0065f * w, Offset(aro.x, top + 0.007f * w), style = Stroke(1.8f * u))
                drawLine(n(hierro, o, k), Offset(aro.x, top + 0.013f * w), Offset(aro.x, top + largo), 2.2f * u)
                drawLine(brillo(o, 0.5f, k), Offset(aro.x - 0.8f * u, top + 0.014f * w), Offset(aro.x - 0.8f * u, top + largo * 0.9f), 0.6f * u)
                drawRect(n(hierro, o, k), Offset(aro.x, top + largo - 0.012f * w), Size(0.008f * w, 0.005f * w))
                drawRect(n(hierro, o, k), Offset(aro.x, top + largo - 0.005f * w), Size(0.005f * w, 0.005f * w))
            }
        }
        drawCircle(n(hierro, o, k), 0.011f * w, aro, style = Stroke(1.6f * u))
        drawArc(brillo(o, 0.5f, k), 190f, 70f, false, Offset(aro.x - 0.011f * w, aro.y - 0.011f * w), Size(0.022f * w, 0.022f * w), style = Stroke(0.7f * u))
        clavo(Offset(cx, cy), o, k, 0f)
    }

    // ---------------------------------------------------------------- al pie

    /** El arcón de marinero: pintado de gris de mar y gastado hasta el pino, más angosto arriba, con herrajes y su asa de cabo. */
    private fun DrawScope.arcon(alto: Float, o: Boolean) {
        val w = size.width; val u = density
        val y1 = alto - 0.014f * w; val y0 = alto - 0.15f * w
        val x0 = -0.03f * w; val x1 = 0.3f * w
        val k = kk(o, 0.14f, y0 / w) + if (o) 0.16f else 0f
        fun c(col: Color) = n(col, o, k)
        // La sombra en el piso, a la derecha.
        drawPath(pl(x1 - 0.01f * w, y0 + 0.02f * w, x1 + 0.06f * w, y0 + 0.05f * w, x1 + 0.07f * w, y1 + 0.004f * w, x1, y1), sombra(o, 0.35f))
        val cuerpo = pl(x0, y1, x1, y1, x1 - 0.012f * w, y0, x0 + 0.012f * w, y0)
        drawPath(cuerpo, c(MAR))
        clipPath(cuerpo) {
            drawRect(if (o) Color.Transparent else LUZ_DIA.copy(alpha = 0.14f), Offset(x0, y0), Size((x1 - x0) * 0.4f, y1 - y0))
            drawRect(sombra(o, 0.22f), Offset(x1 - (x1 - x0) * 0.25f, y0), Size((x1 - x0) * 0.25f, y1 - y0))
            pinceladas(Rect(x0, y0, x1, y1), if (o) NOCHE else PINO_OSC, 6500, 14, u, if (o) 0.05f else 0.1f)
            // La pintura gastada hasta el pino en los cantos y abajo.
            for (j in 0 until 7) {
                val p = Offset(x0 + (x1 - x0) * azar(6501, j), y0 + (y1 - y0) * (if (j % 2 == 0) 0.9f else 0.1f + 0.7f * azar(6502, j)))
                drawPath(irregular(p, (0.008f + 0.014f * azar(6503, j)) * w, 0.006f * w, 6504 + j, 8, 0.5f), c(PINO))
            }
            // Las tablas del frente.
            listOf(0.33f, 0.66f).forEach { f -> drawLine(sombra(o, 0.35f), Offset(x0, y0 + (y1 - y0) * f), Offset(x1, y0 + (y1 - y0) * f), 1.2f * u) }
            // El salitre del pie.
            drawRect(c(SALITRE).copy(alpha = 0.3f), Offset(x0, y1 - 0.016f * w), Size(x1 - x0, 0.004f * w))
        }
        // Los cantoneros de hierro y la aldaba.
        listOf(x1 - 0.034f * w).forEach { x ->
            drawPath(pl(x, y0, x1 - 0.012f * w, y0, x1 - 0.008f * w, y0 + 0.03f * w, x + 0.02f * w, y0 + 0.03f * w), c(HOJALATA))
            drawPath(pl(x - 0.006f * w, y1, x1, y1, x1 - 0.004f * w, y1 - 0.03f * w, x + 0.016f * w, y1 - 0.03f * w), c(HOJALATA))
        }
        val al = Rect(0.12f * w, y0, 0.15f * w, y0 + 0.045f * w)
        drawRect(sombra(o, 0.35f), Offset(al.left + 1.6f * u, al.top + 2f * u), al.size)
        drawRect(c(HOJALATA), al.topLeft, al.size)
        drawRect(brillo(o, 0.4f, k), al.topLeft, Size(1f * u, al.height))
        drawCircle(c(mezcla(HOJALATA, OXIDO, 0.4f)), 0.008f * w, Offset(al.center.x, al.bottom - 0.01f * w), style = Stroke(1.6f * u))
        // La tapa, que vuela un poco: la cara de encima con luz y el canto.
        val tapa = Rect(x0, y0 - 0.026f * w, x1 - 0.004f * w, y0)
        drawRect(c(mezcla(MAR, LUZ_DIA, 0.12f)), Offset(tapa.left, tapa.top - 0.014f * w), Size(tapa.width, 0.016f * w))
        drawRect(brillo(o, 0.45f, k + 0.2f), Offset(tapa.left, tapa.top - 0.014f * w), Size(tapa.width, 1.2f * u))
        if (o) drawRect(LLAMA.copy(alpha = 0.16f), Offset(tapa.left, tapa.top - 0.014f * w), Size(tapa.width, 1.4f * u))
        drawRect(c(mezcla(MAR, PINO_OSC, 0.2f)), tapa.topLeft, tapa.size)
        drawRect(brillo(o, 0.4f, k + 0.2f), tapa.topLeft, Size(tapa.width, 1f * u))
        drawRect(sombra(o, 0.45f), Offset(tapa.left, tapa.bottom), Size(tapa.width, 2.4f * u))
        for (j in 0 until 4) {
            val x = tapa.left + tapa.width * (0.2f + 0.2f * j)
            drawPath(irregular(Offset(x, tapa.top + 1.2f * u), (0.01f + 0.01f * azar(6510, j)) * w, 1.6f * u, 6511 + j, 7, 0.5f), c(PINO))
        }
        // El asa de cabo, en el costado de la derecha: el tojino de madera y el cabo con sus vueltas.
        val ty = y0 + (y1 - y0) * 0.35f
        drawRect(c(PINO), Offset(x1 - 0.01f * w, ty - 0.012f * w), Size(0.014f * w, 0.024f * w))
        val asa = Path().apply {
            moveTo(x1 - 0.002f * w, ty - 0.008f * w)
            cubicTo(x1 + 0.04f * w, ty - 0.02f * w, x1 + 0.052f * w, ty + 0.03f * w, x1 + 0.02f * w, ty + 0.04f * w)
            cubicTo(x1 + 0.005f * w, ty + 0.045f * w, x1 - 0.002f * w, ty + 0.02f * w, x1 - 0.002f * w, ty + 0.008f * w)
        }
        translate(2f * u, 3f * u) { drawPath(asa, sombra(o, 0.35f), style = Stroke(0.009f * w, cap = StrokeCap.Round)) }
        drawPath(asa, c(BRAMANTE), style = Stroke(0.009f * w, cap = StrokeCap.Round))
        drawPath(asa, sombra(o, 0.35f), style = Stroke(0.009f * w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 2.4f * u))))
        drawPath(asa, brillo(o, 0.4f, k), style = Stroke(0.8f * u))
        listOf(ty - 0.008f * w, ty + 0.008f * w).forEach { y -> drawRect(c(mezcla(BRAMANTE, SEPIA, 0.4f)), Offset(x1 - 0.004f * w, y - 0.004f * w), Size(0.01f * w, 0.008f * w)) }
        // De noche, un reflejo frío (el verde pálido de la bruma) en los herrajes, la argolla, el asa y los cantos: se lee el arcón.
        if (o) {
            val frio = mezcla(LUZ_DIA, FOSFORO, 0.45f)
            clipPath(cuerpo) { drawRect(frio.copy(alpha = 0.07f), Offset(x0, y0), Size((x1 - x0) * 0.55f, y1 - y0)) }
            drawRect(frio.copy(alpha = 0.12f), Offset(tapa.left, tapa.top - 0.014f * w), Size(tapa.width, 0.016f * w))
            drawLine(frio.copy(alpha = 0.3f), Offset(x1 - 0.034f * w, y0 + 1f * u), Offset(x1 - 0.012f * w, y0 + 1f * u), 1.2f * u)
            drawLine(frio.copy(alpha = 0.22f), Offset(x1 - 0.03f * w, y1 - 0.03f * w), Offset(x1 - 0.004f * w, y1 - 0.03f * w), 1.1f * u)
            drawLine(frio.copy(alpha = 0.18f), Offset(x1 - 0.012f * w, y0), Offset(x1 - 0.001f * w, y1), 1.2f * u)
            drawRect(frio.copy(alpha = 0.25f), al.topLeft, Size(1.2f * u, al.height))
            drawArc(frio.copy(alpha = 0.35f), 180f, 140f, false, Offset(al.center.x - 0.008f * w, al.bottom - 0.018f * w), Size(0.016f * w, 0.016f * w), style = Stroke(1.2f * u))
            drawPath(asa, frio.copy(alpha = 0.22f), style = Stroke(1f * u))
            drawRect(frio.copy(alpha = 0.3f), Offset(tapa.left, tapa.top - 0.014f * w), Size(tapa.width, 1.2f * u))
            drawRect(frio.copy(alpha = 0.18f), Offset(tapa.left, tapa.top), Size(tapa.width, 1f * u))
        }
    }

    /** El rollo de cuerda en el piso, cortado por el borde, con su chicote suelto. */
    private fun DrawScope.cuerda(alto: Float, o: Boolean) {
        val w = size.width; val u = density
        val c0 = Offset(0.9f * w, alto - 0.05f * w)
        val k = if (o) 0.05f else 0f
        val col = mezcla(BRAMANTE, SEPIA, 0.25f)
        drawOval(sombra(o, 0.4f), Offset(c0.x - 0.11f * w, c0.y - 0.026f * w), Size(0.25f * w, 0.07f * w))
        // El chicote, que se va hacia la izquierda.
        val chicote = Path().apply { moveTo(c0.x - 0.1f * w, c0.y + 0.01f * w); cubicTo(c0.x - 0.15f * w, c0.y + 0.03f * w, c0.x - 0.2f * w, c0.y + 0.0f * w, c0.x - 0.26f * w, c0.y + 0.035f * w) }
        translate(1.6f * u, 2.6f * u) { drawPath(chicote, sombra(o, 0.4f), style = Stroke(0.012f * w, cap = StrokeCap.Round)) }
        drawPath(chicote, n(col, o, k), style = Stroke(0.012f * w, cap = StrokeCap.Round))
        drawPath(chicote, sombra(o, 0.3f), style = Stroke(0.012f * w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 2.6f * u))))
        // Las vueltas: dos capas, de afuera adentro.
        listOf(0f, -0.014f * w).forEachIndexed { capa, dy ->
            listOf(0.11f, 0.086f, 0.062f, 0.038f).forEachIndexed { i, rx ->
                if (capa == 1 && i == 0) return@forEachIndexed
                val r = Rect(c0.x - rx * w, c0.y + dy - rx * w * 0.3f, c0.x + rx * w, c0.y + dy + rx * w * 0.3f)
                drawOval(sombra(o, 0.45f), r.topLeft + Offset(0f, 2.4f * u), r.size, style = Stroke(0.012f * w))
                drawOval(n(col, o, k + 0.04f * capa), r.topLeft, r.size, style = Stroke(0.012f * w))
                drawOval(sombra(o, 0.3f), r.topLeft, r.size, style = Stroke(0.012f * w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 2.6f * u))))
                drawArc(brillo(o, 0.45f, 0.4f), 190f, 120f, false, r.topLeft - Offset(0f, 0.004f * w), r.size, style = Stroke(1f * u))
            }
        }
        drawOval(n(mezcla(PINO_OSC, NOCHE, 0.3f), o, 0f), Offset(c0.x - 0.022f * w, c0.y - 0.022f * w), Size(0.044f * w, 0.013f * w))
    }

    /** Los jirones de bruma, a ras de las tablas, muy tenues (solo de noche). */
    private fun DrawScope.bruma(pisa: Float, alto: Float) {
        val w = size.width
        val niebla = mezcla(LUZ_DIA, MAR, 0.45f)
        // Jirones: manchas alargadas y muy tenues, a distintas alturas.
        for (j in 0 until 4) {
            val y = pisa + (alto - pisa) * (0.2f + 0.7f * azar(6600, j))
            val x = w * (-0.1f + 0.8f * azar(6601, j))
            val largo = w * (0.35f + 0.25f * azar(6602, j))
            // Una mancha redonda aplastada: borde que se deshace, sin escalones.
            val c = Offset(x + largo / 2, y)
            scale(1f, 0.12f, c) { mancha(c, largo / 2, niebla.copy(alpha = 0.13f)) }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, niebla.copy(alpha = 0.05f)), pisa - 0.04f * w, alto), Offset(0f, pisa - 0.04f * w), Size(w, alto - pisa + 0.04f * w))
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Cuánto alumbra la llama un compartimento, según su fila y su columna (el centro de su hueco, en fracciones del ancho). */
    private fun cercaDe(c: EstadoCajon): Float {
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        return deQuinque(centro)
    }

    /** El portaetiquetas de latón verdoso con su tarjeta: el marco, el verdín, las pestañas y los remaches. */
    private fun DrawScope.portaetiquetas(r: Rect, o: Boolean, kc: Float, semilla: Int) {
        val u = density
        val m = 2.6f * u
        val k = if (o) maxOf(kc, 0.12f) else 0f
        val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
        drawRect(sombra(o, 0.42f), Offset(marco.left + 1.4f * u, marco.top + 2f * u), marco.size)
        drawRect(n(mezcla(LATON, VERDIN, 0.3f), o, k), marco.topLeft, marco.size)
        drawRect(brillo(o, 0.6f, k + 0.2f), marco.topLeft, Size(marco.width, 1f * u))
        drawRect(sombra(o, 0.35f), Offset(marco.left, marco.bottom - 1f * u), Size(marco.width, 1f * u))
        for (j in 0 until 3) {
            val x = marco.left + marco.width * azar(semilla, 700 + j)
            drawRect(n(mezcla(VERDIN, LATON, 0.3f), o, k).copy(alpha = 0.55f), Offset(x, if (j % 2 == 0) marco.top else marco.bottom - m), Size(marco.width * 0.05f, m * 0.7f))
        }
        drawRect(tarjeta(o, kc), r.topLeft, r.size)
        // De noche, lejos de la llama, la tarjeta se apaga por los bordes (el centro, con el texto, conserva su contraste).
        if (o && kc < 0.5f) drawRect(sombra(true, 0.3f * (1f - 2f * kc)), Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3f * u, r.height - 3f * u), style = Stroke(3f * u))
        // La raya roja de la ficha y la sombra del marco sobre la tarjeta.
        drawRect(n(HILO, o, k).copy(alpha = 0.45f), Offset(r.left + 2f * u, r.top + r.height * 0.13f), Size(r.width - 4f * u, 0.8f * u))
        drawRect(sombra(o, 0.18f), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(sombra(o, 0.12f), r.topLeft, Size(1.4f * u, r.height))
        listOf(marco.left + m / 2, marco.right - m / 2).forEach { x ->
            drawCircle(n(mezcla(LATON, VERDIN, 0.15f), o, k), 1.2f * u, Offset(x, r.center.y))
            drawCircle(brillo(o, 0.6f, k), 0.5f * u, Offset(x - 0.3f * u, r.center.y - 0.3f * u))
        }
        // El filo de la llama en el canto de arriba del marco y el destello en un remache.
        filo(marco, o, kc, false)
        destello(Offset(marco.right - m / 2, r.center.y), 1f * u, o, kc)
    }

    /** Los papeles que asoman: hojas de distintos papeles, ladeadas, entre [arriba] y [abajo]; más cuantas más fichas. */
    private fun DrawScope.papeles(x0: Float, x1: Float, arriba: Float, abajo: Float, cuantas: Int, semilla: Int, o: Boolean, k: Float, sueltos: Boolean = false) {
        val u = density
        val m = cuantas.coerceIn(1, 7)
        for (i in 0 until m) {
            val ancho = (x1 - x0) * (0.2f + 0.14f * azar(semilla, 800 + i))
            val x = x0 + (x1 - x0 - ancho) * ((i + 0.5f * azar(semilla, 810 + i)) / m)
            val y = arriba + (abajo - arriba) * (0.05f + 0.55f * azar(semilla, 820 + i))
            val col = when (Math.floorMod(semilla + i, 4)) { 0 -> PAPEL; 1 -> mezcla(PAPEL, SEPIA, 0.28f); 2 -> MANILA; else -> mezcla(PAPEL, LUZ_DIA, 0.4f) }
            val giro = (azar(semilla, 830 + i) - 0.5f) * (if (sueltos) 26f else 10f)
            rotate(giro, Offset(x + ancho / 2, abajo)) {
                val hoja = Rect(x, y, x + ancho, abajo + 2f * u)
                drawRect(sombra(o, 0.3f), Offset(hoja.left + 1.4f * u, hoja.top + 1.4f * u), hoja.size)
                drawRect(n(col, o, k), hoja.topLeft, hoja.size)
                drawRect(brillo(o, 0.45f, k), hoja.topLeft, Size(hoja.width, 0.9f * u))
                drawRect(sombra(o, 0.15f), Offset(hoja.right - hoja.width * 0.2f, hoja.top), Size(hoja.width * 0.2f, hoja.height))
                if (hoja.height > 6f * u) drawLine(n(SEPIA, o, k).copy(alpha = 0.5f), Offset(hoja.left + hoja.width * 0.15f, hoja.top + 3.4f * u), Offset(hoja.right - hoja.width * 0.25f, hoja.top + 3.4f * u), 0.8f * u)
            }
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val o = oscuro
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val k = if (o) cercaDe(c) else 0f
        // Aun la caja más lejana recoge un filo de luz en los cantos y en el latón.
        val filo = if (o) maxOf(k, 0.1f) else 0f
        // De día, la luz de arriba a la izquierda aclara apenas las filas de arriba y la columna de la izquierda.
        val dia = if (o) 0f else 0.06f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f)

        if (c.vacio) { huecoVacio(c, o, k, filo); return }
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        // La sombra de la caja en el fondo, a la derecha, y en la balda.
        drawRect(sombra(o, 0.3f), Offset(w * 0.95f, h * 0.38f), Size(w * 0.05f, h * 0.62f))
        drawRect(sombra(o, 0.4f), Offset(w * 0.08f, h - 1.6f * u), Size(w * 0.92f, 3.6f * u))

        if (c.sinTema) { cajaPuros(c, r, o, k, filo, dia, ab); return }

        when (Math.floorMod(c.semilla / 17, 3)) {
            0 -> cajaCarton(c, o, k, filo, dia, ab)
            1 -> cajaCaudales(c, o, k, filo, dia, ab)
            else -> fajoLibros(c, o, k, filo, dia, ab)
        }
        portaetiquetas(r, o, k, c.semilla)
        if (ab > 0f) mancha(Offset(w / 2, h * 0.3f), w * 0.4f, if (o) LLAMA.copy(alpha = 0.14f * ab * (0.3f + k)) else LUZ_DIA.copy(alpha = 0.3f * ab))
    }

    /** La caja de archivo de cartón: la tapa que sube con los papeles, el bramante en cruz, el lacre y la marca del agua. */
    private fun DrawScope.cajaCarton(c: EstadoCajon, o: Boolean, k: Float, filo: Float, dia: Float, ab: Float) {
        val u = density
        val w = size.width; val h = size.height
        val cuerpo = Rect(w * 0.05f, h * 0.36f, w * 0.95f, h)
        val sube = h * 0.016f * c.n.coerceIn(0, 7) + ab * h * 0.16f
        if (sube > 1f) {
            drawRect(n(mezcla(PINO_OSC, NOCHE, 0.4f), o, k * 0.5f), Offset(cuerpo.left + 2f * u, cuerpo.top - sube), Size(cuerpo.width - 4f * u, sube + 2f * u))
            papeles(cuerpo.left + 3f * u, cuerpo.right - 3f * u, cuerpo.top - sube - h * 0.04f, cuerpo.top, c.n, c.semilla, o, k)
        }
        cara(cuerpo, CARTON, o, k, dia)
        // El filo de la llama en el canto de arriba y en el costado que la mira (en la columna de la izquierda, el derecho).
        filo(cuerpo, o, k, c.col == 0)
        pinceladas(cuerpo, if (o) NOCHE else CARTON_OSC, c.semilla, 8, u, 0.12f)
        // La solapa del costado y las esquinas gastadas.
        listOf(cuerpo.left + w * 0.04f, cuerpo.right - w * 0.04f).forEach { x -> drawLine(sombra(o, 0.25f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 0.9f * u) }
        listOf(Offset(cuerpo.left, cuerpo.bottom), Offset(cuerpo.right, cuerpo.bottom), Offset(cuerpo.left, cuerpo.top)).forEachIndexed { j, p ->
            drawPath(irregular(p, w * 0.03f, h * 0.04f, c.semilla + 900 + j, 8, 0.4f), n(CARTON_LUZ, o, k).copy(alpha = 0.7f))
        }
        // La marca del agua abajo: como en la pared, el mar entró también aquí.
        val ym = cuerpo.bottom - cuerpo.height * (0.16f + 0.08f * azar(c.semilla, 910))
        val marca = Path().apply {
            moveTo(cuerpo.left, ym)
            for (j in 1..10) lineTo(cuerpo.left + cuerpo.width * j / 10f, ym + h * 0.012f * sin(j * 1.9f + c.semilla))
            lineTo(cuerpo.right, cuerpo.bottom); lineTo(cuerpo.left, cuerpo.bottom); close()
        }
        drawPath(marca, n(SALOBRE, o, k).copy(alpha = 0.22f))
        drawPath(marca, n(SALOBRE, o, k).copy(alpha = 0.35f), style = Stroke(1f * u))
        // La tapa, que monta y vuela un poco, con el número del legajo a lápiz.
        val tapa = Rect(w * 0.035f, cuerpo.top - sube - h * 0.15f, w * 0.965f, cuerpo.top - sube + h * 0.012f)
        drawRect(sombra(o, 0.35f), Offset(tapa.left + 1.4f * u, tapa.bottom), Size(tapa.width, 2.6f * u))
        cara(tapa, mezcla(CARTON, CARTON_LUZ, 0.4f), o, k, dia + 0.03f)
        filo(tapa, o, k, c.col == 0)
        for (j in 0 until 3) drawLine(n(HOJALATA, o, k).copy(alpha = 0.55f), Offset(tapa.left + w * (0.08f + 0.04f * j), tapa.top + tapa.height * (0.35f + 0.1f * j)), Offset(tapa.left + w * (0.11f + 0.04f * j), tapa.top + tapa.height * (0.6f - 0.1f * j)), 1.1f * u)
        // El bramante en cruz, flojo al abrirse, con su nudo y el lacre.
        val nudo = Offset(w * 0.88f, h * 0.885f)
        val flojo = ab * 5f * u
        drawLine(n(BRAMANTE, o, filo), Offset(nudo.x, tapa.top), Offset(nudo.x + flojo, cuerpo.bottom), 1.6f * u)
        drawLine(sombra(o, 0.3f), Offset(nudo.x + 1.6f * u, tapa.top), Offset(nudo.x + flojo + 1.6f * u, cuerpo.bottom), 0.8f * u)
        drawLine(n(BRAMANTE, o, filo), Offset(cuerpo.left, nudo.y), Offset(cuerpo.right, nudo.y + flojo), 1.6f * u)
        drawLine(sombra(o, 0.3f), Offset(cuerpo.left, nudo.y + 1.6f * u), Offset(cuerpo.right, nudo.y + flojo + 1.6f * u), 0.8f * u)
        val lacre = irregular(nudo, w * 0.03f, w * 0.028f, c.semilla + 920, 9, 0.25f)
        translate(1.4f * u, 1.8f * u) { drawPath(lacre, sombra(o, 0.4f)) }
        drawPath(lacre, if (o) mezcla(n(LACRE, true, filo), LACRE, 0.5f * k) else LACRE)
        drawCircle(n(mezcla(LACRE, HOJALATA, 0.35f), o, filo), w * 0.016f, nudo, style = Stroke(0.9f * u))
        drawArc(brillo(o, 0.5f, filo), 190f, 70f, false, nudo - Offset(w * 0.02f, w * 0.02f), Size(w * 0.04f, w * 0.04f), style = Stroke(1f * u))
        destello(nudo - Offset(w * 0.01f, w * 0.01f), 1.3f * u, o, k)
    }

    /** La caja de caudales de hojalata negra: tapa abombada con su asa, filete dorado, la pintura saltada y el candado. */
    private fun DrawScope.cajaCaudales(c: EstadoCajon, o: Boolean, k: Float, filo: Float, dia: Float, ab: Float) {
        val u = density
        val w = size.width; val h = size.height
        val cuerpo = Rect(w * 0.06f, h * 0.38f, w * 0.94f, h)
        val sube = h * 0.012f * c.n.coerceIn(0, 7) + ab * h * 0.16f
        if (sube > 1f) {
            drawRect(n(HOJALATA, o, k * 0.4f), Offset(cuerpo.left + 2f * u, cuerpo.top - sube), Size(cuerpo.width - 4f * u, sube + 2f * u))
            papeles(cuerpo.left + 4f * u, cuerpo.right - w * 0.14f, cuerpo.top - sube - h * 0.03f, cuerpo.top, c.n, c.semilla, o, k)
        }
        cara(cuerpo, HOJALATA, o, maxOf(k, filo * 0.6f), dia, 3f * u)
        filo(cuerpo, o, k, c.col == 0)
        // El filete dorado, desvaído.
        val fil = Rect(cuerpo.left + w * 0.03f, cuerpo.top + h * 0.05f, cuerpo.right - w * 0.03f, cuerpo.bottom - h * 0.06f)
        drawRoundRect(n(LATON, o, filo).copy(alpha = if (o) 0.4f + 0.4f * k else 0.45f), fil.topLeft, fil.size, CornerRadius(4f * u), style = Stroke(0.9f * u))
        // La pintura saltada: la hojalata clara con su cerco de óxido.
        val saltos = 4 + Math.floorMod(c.semilla, 3)
        for (j in 0 until saltos) {
            val esq = Math.floorMod(c.semilla + j, 4)
            val p = Offset(if (esq % 2 == 0) cuerpo.left + w * (0.02f + 0.08f * azar(c.semilla, 930 + j)) else cuerpo.right - w * (0.02f + 0.12f * azar(c.semilla, 930 + j)),
                if (esq < 2) cuerpo.top + h * (0.04f + 0.06f * azar(c.semilla, 940 + j)) else cuerpo.bottom - h * (0.04f + 0.1f * azar(c.semilla, 940 + j)))
            val pz = irregular(p, w * (0.012f + 0.016f * azar(c.semilla, 950 + j)), h * 0.025f, c.semilla + 960 + j, 8, 0.5f)
            drawPath(pz, n(OXIDO, o, filo).copy(alpha = 0.8f))
            scaleRel(pz, p, 0.6f, n(mezcla(LUZ_DIA, MAR, 0.4f), o, filo))
        }
        // La tapa abombada, con su asa plegada encima.
        val tapa = Rect(w * 0.05f, cuerpo.top - sube - h * 0.15f, w * 0.95f, cuerpo.top - sube + h * 0.01f)
        drawRect(sombra(o, 0.4f), Offset(tapa.left + 1.4f * u, tapa.bottom), Size(tapa.width, 2.6f * u))
        val curva = Path().apply {
            moveTo(tapa.left, tapa.bottom); lineTo(tapa.left, tapa.top + tapa.height * 0.4f)
            cubicTo(tapa.left + tapa.width * 0.1f, tapa.top, tapa.right - tapa.width * 0.1f, tapa.top, tapa.right, tapa.top + tapa.height * 0.4f)
            lineTo(tapa.right, tapa.bottom); close()
        }
        drawPath(curva, n(HOJALATA, o, maxOf(k, filo * 0.6f)))
        clipPath(curva) {
            drawRect(brillo(o, 0.3f, filo), Offset(tapa.left, tapa.top + tapa.height * 0.15f), Size(tapa.width * 0.5f, tapa.height * 0.2f))
            drawRect(sombra(o, 0.3f), Offset(tapa.left, tapa.bottom - tapa.height * 0.3f), Size(tapa.width, tapa.height * 0.3f))
            drawRect(sombra(o, 0.2f), Offset(tapa.right - tapa.width * 0.25f, tapa.top), Size(tapa.width * 0.25f, tapa.height))
        }
        val asa = Rect(w * 0.38f, tapa.top + tapa.height * 0.06f, w * 0.62f, tapa.top + tapa.height * 0.4f)
        drawArc(sombra(o, 0.4f), 180f, 180f, false, asa.topLeft + Offset(1.4f * u, 2f * u), asa.size, style = Stroke(1.8f * u))
        drawArc(n(mezcla(HOJALATA, LUZ_DIA, 0.4f), o, filo), 180f, 180f, false, asa.topLeft, asa.size, style = Stroke(1.8f * u))
        // La aldaba y el candado colgando, a la derecha.
        val ax = w * 0.952f
        drawRect(sombra(o, 0.4f), Offset(ax - w * 0.018f + 1.4f * u, tapa.bottom - h * 0.04f + 1.8f * u), Size(w * 0.036f, h * 0.1f))
        drawRect(n(mezcla(HOJALATA, LUZ_DIA, 0.25f), o, filo), Offset(ax - w * 0.018f, tapa.bottom - h * 0.04f), Size(w * 0.036f, h * 0.1f))
        drawRect(brillo(o, 0.5f, filo), Offset(ax - w * 0.018f, tapa.bottom - h * 0.04f), Size(w * 0.036f, 0.9f * u))
        val argolla = Offset(ax, tapa.bottom + h * 0.05f)
        drawArc(n(mezcla(LATON, VERDIN, 0.3f), o, filo), 180f, 180f, false, Offset(argolla.x - w * 0.02f, argolla.y - h * 0.04f), Size(w * 0.04f, h * 0.08f), style = Stroke(2.4f * u))
        val cand = Rect(argolla.x - w * 0.026f, argolla.y, argolla.x + w * 0.026f, argolla.y + h * 0.12f)
        drawRoundRect(sombra(o, 0.45f), cand.topLeft + Offset(1.6f * u, 2f * u), cand.size, CornerRadius(2.4f * u))
        drawRoundRect(n(mezcla(LATON, VERDIN, 0.25f), o, filo), cand.topLeft, cand.size, CornerRadius(2.4f * u))
        drawRect(brillo(o, 0.6f, filo), Offset(cand.left + 1.2f * u, cand.top + 1.2f * u), Size(cand.width * 0.22f, cand.height - 2.4f * u))
        drawRect(sombra(o, 0.25f), Offset(cand.right - cand.width * 0.28f, cand.top), Size(cand.width * 0.28f, cand.height))
        drawCircle(n(HOJALATA, o, 0f), 1.3f * u, Offset(cand.center.x, cand.center.y - cand.height * 0.08f))
        drawRect(n(HOJALATA, o, 0f), Offset(cand.center.x - 0.5f * u, cand.center.y - cand.height * 0.08f), Size(1f * u, cand.height * 0.25f))
        drawRect(n(VERDIN, o, filo).copy(alpha = 0.6f), Offset(cand.left, cand.bottom - cand.height * 0.2f), Size(cand.width * 0.5f, cand.height * 0.15f))
        filo(tapa, o, k, c.col == 0)
        destello(Offset(cand.left + cand.width * 0.3f, cand.top + cand.height * 0.25f), 1.2f * u, o, k)
        destello(Offset(w * 0.4f, asa.top + 1f * u), 1f * u, o, k)
    }

    /** Una forma [p] escalada alrededor de [c], rellena de [col]: el centro claro de la pintura saltada. */
    private fun DrawScope.scaleRel(p: Path, c: Offset, s: Float, col: Color) {
        scale(s, c) { drawPath(p, col) }
    }

    /** El fajo de libros de registro sujeto con una correa: tres lomos de piel y tela, los papeles metidos y la hebilla. */
    private fun DrawScope.fajoLibros(c: EstadoCajon, o: Boolean, k: Float, filo: Float, dia: Float, ab: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sube = ab * h * 0.12f
        // Encima del fajo, las hojas sueltas, más cuantas más fichas.
        val l3 = Rect(w * 0.06f, h * 0.27f - sube, w * 0.9f, h * 0.43f - sube)
        if (c.n > 0) papeles(l3.left + w * 0.06f, l3.right - w * 0.1f, l3.top - h * (0.06f + 0.012f * c.n.coerceIn(0, 7)), l3.top + 1f * u, c.n, c.semilla, o, k, sueltos = true)
        // Las pieles y telas de los tres, en otro orden según la semilla, y cada uno un poco corrido.
        val telas = listOf(mezcla(PINO_OSC, OXIDO, 0.35f), mezcla(MAR, HOJALATA, 0.2f), mezcla(LACRE, PINO_OSC, 0.3f), mezcla(SEPIA, PINO, 0.25f))
        val g = Math.floorMod(c.semilla / 7, 4)
        fun corre(j: Int) = (azar(c.semilla, 980 + j) - 0.5f) * w * 0.04f
        val lomos = listOf(
            Rect(w * 0.04f, h * 0.8f, w * 0.96f, h) to telas[g],
            Rect(w * 0.07f + corre(1), h * 0.43f, w * 0.94f + corre(1), h * 0.8f) to telas[(g + 1) % 4],
            l3.translate(corre(2), 0f) to telas[(g + 2) % 4],
        )
        lomos.forEachIndexed { i, (r, col) ->
            drawRect(sombra(o, 0.35f), Offset(r.left + 1.4f * u, r.bottom - 1f * u), Size(r.width, 2.6f * u))
            cara(r, col, o, maxOf(k, filo * 0.5f), dia, 2.4f * u)
            filo(r, o, k * (1f - 0.25f * i), c.col == 0)
            // El lomo redondeado: el brillo de arriba y la piel de las puntas.
            drawRect(brillo(o, 0.25f, filo), Offset(r.left, r.top + r.height * 0.1f), Size(r.width, r.height * 0.14f))
            listOf(r.left, r.right - r.width * 0.1f).forEach { x -> drawRect(n(mezcla(PINO_OSC, OXIDO, 0.25f), o, maxOf(k, filo * 0.5f)), Offset(x, r.top), Size(r.width * 0.1f, r.height)) }
            listOf(0.14f, 0.19f, 0.81f, 0.86f).forEach { f ->
                drawLine(sombra(o, 0.4f), Offset(r.left + r.width * f, r.top), Offset(r.left + r.width * f, r.bottom), 1.3f * u)
                drawLine(brillo(o, 0.3f, filo), Offset(r.left + r.width * f + 1.3f * u, r.top), Offset(r.left + r.width * f + 1.3f * u, r.bottom), 0.7f * u)
            }
            // El roce de la piel en las esquinas.
            drawPath(irregular(Offset(r.left + r.width * 0.05f, r.top + r.height * 0.2f), w * 0.02f, r.height * 0.2f, c.semilla + 970 + i, 7, 0.5f), n(mezcla(col, LUZ_DIA, 0.3f), o, filo).copy(alpha = 0.4f))
            // Los papeles metidos entre las hojas, que asoman por la punta de la derecha.
            if (i < 2) for (j in 0 until (c.n / 3 + 1).coerceAtMost(3)) {
                val y = r.top + h * 0.03f + j * h * 0.05f
                val hoja = Rect(r.right - w * 0.03f, y, r.right + w * 0.025f, y + h * 0.035f)
                rotate(-6f + 8f * j, Offset(hoja.left, hoja.center.y)) {
                    drawRect(sombra(o, 0.3f), hoja.topLeft + Offset(1f * u, 1.4f * u), hoja.size)
                    drawRect(n(if (j % 2 == 0) PAPEL else MANILA, o, k), hoja.topLeft, hoja.size)
                    drawRect(brillo(o, 0.4f, k), hoja.topLeft, Size(hoja.width, 0.8f * u))
                }
            }
        }
        // La correa con su hebilla, que abraza el fajo.
        val cx = w * 0.905f
        val correa = Rect(cx - w * 0.02f, l3.top - 1f * u, cx + w * 0.02f, h)
        drawRect(sombra(o, 0.4f), Offset(correa.left + 1.6f * u, correa.top), correa.size)
        drawRect(n(mezcla(PINO_OSC, OXIDO, 0.5f), o, filo), correa.topLeft, correa.size)
        drawRect(brillo(o, 0.35f, filo), correa.topLeft, Size(1.2f * u, correa.height))
        listOf(0.2f, 0.8f).forEach { f -> drawLine(sombra(o, 0.3f), Offset(correa.left + correa.width * f, correa.top), Offset(correa.left + correa.width * f, correa.bottom), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.6f * u))) }
        val heb = Rect(cx - w * 0.03f, l3.top + h * 0.02f, cx + w * 0.03f, l3.top + h * 0.12f)
        drawRect(sombra(o, 0.4f), heb.topLeft + Offset(1.4f * u, 1.8f * u), heb.size)
        drawRect(n(mezcla(LATON, VERDIN, 0.3f), o, filo), heb.topLeft, heb.size, style = Stroke(2.4f * u))
        drawLine(n(mezcla(LATON, VERDIN, 0.3f), o, filo), Offset(heb.center.x, heb.top), Offset(heb.center.x, heb.bottom), 1.6f * u)
        drawLine(brillo(o, 0.6f, filo), heb.topLeft, Offset(heb.right, heb.top), 0.9f * u)
        destello(heb.topLeft + Offset(1.2f * u, 1.2f * u), 1.2f * u, o, k)
    }

    /** Sin tema: la caja de puros de cedro entreabierta, con papeles sueltos; la tarjeta clavada con una tachuela y un «?» a lápiz. */
    private fun DrawScope.cajaPuros(c: EstadoCajon, r: Rect, o: Boolean, k: Float, filo: Float, dia: Float, ab: Float) {
        val u = density
        val w = size.width; val h = size.height
        val cedro = mezcla(PINO_LUZ, LACRE, 0.35f)
        val cuerpo = Rect(w * 0.06f, h * 0.38f, w * 0.94f, h)
        val sube = h * 0.1f + ab * h * 0.12f
        drawRect(n(mezcla(PINO_OSC, NOCHE, 0.3f), o, k * 0.5f), Offset(cuerpo.left + 2f * u, cuerpo.top - sube), Size(cuerpo.width - 4f * u, sube + 2f * u))
        papeles(cuerpo.left + 3f * u, cuerpo.right - 3f * u, cuerpo.top - sube - h * 0.05f, cuerpo.top, c.n.coerceAtLeast(3), c.semilla, o, k, sueltos = true)
        cara(cuerpo, cedro, o, k, dia)
        vetas(cuerpo, sombra(o, 0.2f), c.semilla, 0.5f, grosor = 0.6f, ondulacion = 0.4f)
        // La tapa, levantada y ladeada, con la banda de papel con su orla (sin marca).
        val tapa = Rect(w * 0.05f, cuerpo.top - sube - h * 0.1f, w * 0.95f, cuerpo.top - sube)
        rotate(-2.5f, Offset(tapa.right, tapa.bottom)) {
            drawRect(sombra(o, 0.4f), Offset(tapa.left + 1.4f * u, tapa.bottom), Size(tapa.width, 2.4f * u))
            cara(tapa, cedro, o, k, dia + 0.03f)
            val banda = Rect(tapa.left + tapa.width * 0.3f, tapa.top + 1.4f * u, tapa.left + tapa.width * 0.7f, tapa.bottom - 1.4f * u)
            drawRect(n(mezcla(PAPEL, SEPIA, 0.15f), o, k), banda.topLeft, banda.size)
            drawRect(n(LACRE, o, k), Offset(banda.left, banda.top + banda.height * 0.15f), Size(banda.width, banda.height * 0.18f))
            drawRect(n(LACRE, o, k), Offset(banda.left, banda.bottom - banda.height * 0.33f), Size(banda.width, banda.height * 0.18f))
            drawRect(n(LATON, o, filo), Offset(banda.center.x - banda.width * 0.12f, banda.top + banda.height * 0.38f), Size(banda.width * 0.24f, banda.height * 0.22f))
        }
        // La tarjeta sin escribir, clavada con una tachuela, y la pregunta a lápiz en la esquina.
        drawRect(sombra(o, 0.4f), Offset(r.left + 1.6f * u, r.top + 2f * u), r.size)
        drawRect(tarjeta(o, k), r.topLeft, r.size)
        if (o && k < 0.5f) drawRect(sombra(true, 0.3f * (1f - 2f * k)), Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3f * u, r.height - 3f * u), style = Stroke(3f * u))
        drawRect(sombra(o, 0.1f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
        val q = Offset(r.right - r.width * 0.07f, r.top + r.height * 0.3f)
        val qs = r.height * 0.18f
        val pregunta = Path().apply {
            moveTo(q.x - qs * 0.5f, q.y - qs * 0.3f)
            cubicTo(q.x - qs * 0.5f, q.y - qs, q.x + qs * 0.5f, q.y - qs, q.x + qs * 0.5f, q.y - qs * 0.35f)
            cubicTo(q.x + qs * 0.5f, q.y, q.x, q.y, q.x, q.y + qs * 0.45f)
        }
        drawPath(pregunta, n(HOJALATA, o, k).copy(alpha = 0.55f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawCircle(n(HOJALATA, o, k).copy(alpha = 0.55f), 0.9f * u, Offset(q.x, q.y + qs * 0.8f))
        chinchetaChica(Offset(r.left + 5f * u, r.top + 5f * u), o, filo)
        if (ab > 0f) mancha(Offset(w / 2, h * 0.3f), w * 0.4f, if (o) LLAMA.copy(alpha = 0.14f * ab * (0.3f + k)) else LUZ_DIA.copy(alpha = 0.3f * ab))
    }

    /** Una tachuela de latón, a la escala de un compartimento. */
    private fun DrawScope.chinchetaChica(c: Offset, o: Boolean, k: Float) {
        val u = density
        drawCircle(sombra(o, 0.4f), 2.6f * u, c + Offset(1f * u, 1.4f * u))
        drawCircle(n(mezcla(LATON, VERDIN, 0.25f), o, k), 2.6f * u, c)
        drawCircle(brillo(o, 0.7f, k), 0.9f * u, c - Offset(0.8f * u, 0.8f * u))
    }

    /** Un hueco sin tema: la tabla con un cerco de humedad, arena y una concha; o una telaraña con un recorte caído. */
    private fun DrawScope.huecoVacio(c: EstadoCajon, o: Boolean, k: Float, filo: Float) {
        val u = density
        val w = size.width; val h = size.height
        if (Math.floorMod(c.semilla / 5, 2) == 0) {
            // El cerco de humedad en el fondo, la arena y la concha.
            val cerco = irregular(Offset(w * 0.42f, h * 0.62f), w * 0.2f, h * 0.24f, c.semilla, 24, 0.08f)
            drawPath(cerco, n(SALOBRE, o, k).copy(alpha = 0.14f))
            drawPath(cerco, n(SALOBRE, o, k).copy(alpha = if (o) 0.1f else 0.22f), style = Stroke(3f * u))
            drawPath(cerco, n(SALITRE, o, filo).copy(alpha = if (o) 0.08f else 0.18f), style = Stroke(1f * u))
            val arena = Path().apply {
                moveTo(w * 0.18f, h); cubicTo(w * 0.26f, h * 0.9f, w * 0.4f, h * 0.86f, w * 0.5f, h * 0.9f)
                cubicTo(w * 0.62f, h * 0.94f, w * 0.7f, h * 0.97f, w * 0.78f, h); close()
            }
            drawPath(arena, n(mezcla(PAPEL, SEPIA, 0.35f), o, k))
            clipPath(arena) {
                drawRect(sombra(o, 0.2f), Offset(w * 0.5f, h * 0.85f), Size(w * 0.3f, h * 0.15f))
                for (j in 0 until 14) drawCircle(sombra(o, 0.3f), 0.8f * u, Offset(w * (0.22f + 0.5f * azar(c.semilla, 990 + j)), h * (0.92f + 0.07f * azar(c.semilla, 995 + j))))
            }
            // La concha de abanico, de pie contra la arena, con sus costillas.
            val cc0 = Offset(w * 0.62f, h * 0.9f)
            val rr = h * 0.2f
            val concha = Path().apply {
                moveTo(cc0.x, cc0.y)
                arcTo(Rect(cc0.x - rr, cc0.y - rr, cc0.x + rr, cc0.y + rr), 200f, 140f, false)
                close()
            }
            translate(2f * u, 2.4f * u) { drawPath(concha, sombra(o, 0.4f)) }
            drawPath(concha, n(mezcla(PAPEL, LACRE, 0.18f), o, filo))
            clipPath(concha) {
                for (j in 0 until 9) {
                    val a = Math.toRadians(205.0 + j * 16.0)
                    drawLine(sombra(o, 0.3f), cc0, cc0 + Offset(cos(a).toFloat(), sin(a).toFloat()) * rr, 1.2f * u)
                }
                drawRect(sombra(o, 0.2f), Offset(cc0.x, cc0.y - rr), Size(rr, rr))
            }
            drawPath(Path().apply { moveTo(cc0.x - rr * 0.94f, cc0.y - rr * 0.34f); arcTo(Rect(cc0.x - rr, cc0.y - rr, cc0.x + rr, cc0.y + rr), 200f, 70f, false) }, brillo(o, 0.6f, filo), style = Stroke(1f * u))
            drawPath(pl(cc0.x - rr * 0.18f, cc0.y + 1f * u, cc0.x + rr * 0.18f, cc0.y + 1f * u, cc0.x + rr * 0.12f, cc0.y - rr * 0.12f, cc0.x - rr * 0.12f, cc0.y - rr * 0.12f), n(mezcla(PAPEL, LACRE, 0.3f), o, filo))
        } else {
            // La telaraña en la esquina de arriba a la izquierda y el recorte caído.
            val e = Offset(0f, 0f)
            val radios = listOf(0.02f, 0.3f, 0.6f, 0.9f, 1.2f, 1.5f)
            val largo = h * 0.55f
            radios.forEach { a -> drawLine(n(LUZ_DIA, o, filo).copy(alpha = 0.45f), e, e + Offset(cos(a), sin(a)) * largo, 0.6f * u) }
            for (j in 1..5) {
                val p = Path()
                radios.forEachIndexed { i, a ->
                    val q = e + Offset(cos(a), sin(a)) * (largo * j / 5.5f)
                    if (i == 0) p.moveTo(q.x, q.y) else {
                        val prev = e + Offset(cos(radios[i - 1]), sin(radios[i - 1])) * (largo * j / 5.5f)
                        val mid = (prev + q) / 2f * 0.94f
                        p.cubicTo(mid.x, mid.y, mid.x, mid.y, q.x, q.y)
                    }
                }
                drawPath(p, n(LUZ_DIA, o, filo).copy(alpha = 0.4f), style = Stroke(0.5f * u))
            }
            val rec = Rect(w * 0.42f, h * 0.84f, w * 0.72f, h * 0.98f)
            rotate(-7f, rec.center) {
                drawRect(sombra(o, 0.4f), rec.topLeft + Offset(1.6f * u, 2f * u), rec.size)
                drawRect(n(mezcla(PAPEL, SEPIA, 0.3f), o, k), rec.topLeft, rec.size)
                renglones(Rect(rec.left + 3f * u, rec.top + 3f * u, rec.right - 3f * u, rec.bottom - 2f * u), 3, n(SEPIA, o, k).copy(alpha = 0.55f), c.semilla, 2.6f * u, 1f * u)
                drawRect(brillo(o, 0.4f, k), rec.topLeft, Size(rec.width, 0.9f * u))
            }
        }
    }

    override val rotulo = Rect(0.075f, 0.45f, 0.925f, 0.77f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.castoro
    override val salida = 0f
    override val placa = Rect(0.34f, 0.06f, 0.66f, 0.19f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de la caja: cartón kraft con manchas, un cerco de taza, granos de arena y una conchita.
        drawRect(Brush.verticalGradient(listOf(t(KRAFT, oscuro), t(mezcla(KRAFT, CARTON_OSC, 0.3f), oscuro))))
        for (k in 0 until 40) mancha(Offset(w * azar(41, k), h * azar(42, k)), (10f + 30f * azar(43, k)) * u, t(CARTON_OSC, oscuro).copy(alpha = 0.1f))
        val taza = Offset(w * 0.72f, h * 0.3f)
        drawCircle(Color(0xFF5A3A20).copy(alpha = 0.3f), 30 * u, taza, style = Stroke(3 * u))
        drawCircle(Color(0xFF5A3A20).copy(alpha = 0.15f), 28 * u, taza, style = Stroke(1.5f * u))
        for (k in 0 until 50) drawCircle(Color(0xFFD8C8A0).copy(alpha = 0.8f), (0.6f + 0.8f * azar(44, k)) * u, Offset(w * (0.1f + 0.3f * azar(45, k)), h * (0.75f + 0.15f * azar(46, k))))
        val concha = Offset(w * 0.2f, h * 0.82f)
        for (k in 0 until 7) {
            val a = Math.toRadians(-160.0 + k * 23.0)
            drawLine(Color(0xFFE8DCC6), concha, Offset(concha.x + 12 * u * cos(a).toFloat(), concha.y + 12 * u * sin(a).toFloat()), 2.4f * u, StrokeCap.Round)
        }
        // Las paredes de cartón.
        val pl = 10 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CARTON, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 20 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFEDE6D3) else TINTA
    override val pared: Dp = 12.dp
    override val columnas = 2
    override val lineas = 5

    /** La etiqueta de evidencia: un rectángulo con las esquinas de arriba cortadas. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val k = minOf(s.width, s.height) * 0.12f
        moveTo(k, 0f); lineTo(s.width - k, 0f); lineTo(s.width, k); lineTo(s.width, s.height); lineTo(0f, s.height); lineTo(0f, k); close()
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(MANILA.copy(alpha = 0.4f))
        // El ojal reforzado y su cordel, arriba al centro.
        val hoyo = Offset(w / 2, 9 * u * g)
        drawCircle(Color(0xFFF2E8D0), 5 * u * g, hoyo)
        drawCircle(LATON_OSC.copy(alpha = 0.5f), 5 * u * g, hoyo, style = Stroke(0.8f * u))
        drawCircle(Color.Black.copy(alpha = 0.6f), 2 * u * g, hoyo)
        drawPath(Path().apply { moveTo(hoyo.x, hoyo.y); cubicTo(hoyo.x - 10 * u * g, hoyo.y - 6 * u * g, hoyo.x - 20 * u * g, 2 * u, hoyo.x - 34 * u * g, 0f) }, BRAMANTE, style = Stroke(1.3f * u * g))
        // La oreja doblada abajo a la izquierda y una mancha de agua.
        val o = 12 * u * g
        drawPath(Path().apply { moveTo(0f, h - o); lineTo(o, h); lineTo(0f, h); close() }, Color.Black.copy(alpha = 0.18f))
        drawPath(Path().apply { moveTo(0f, h - o); lineTo(o, h - o); lineTo(o, h); close() }, Color(0xFFE9DCB8))
        mancha(Offset(w * (0.5f + 0.4f * azar(semilla, 1)), h * (0.4f + 0.4f * azar(semilla, 2))), minOf(w, h) * 0.3f, Color(0xFF8A7A58).copy(alpha = 0.14f))
    }

    override val margenes = MargenesFicha(20.dp, 12.dp, 14.dp, 36.dp, 26.dp, 30.dp)
    override val proporcionFicha = 0.92f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El lacre del archivo.
            "hecho" -> selloHecho(LACRE, p.color)
            "convence" -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y); lineTo(c.x - s * 0.08f, c.y + s * 0.3f); lineTo(c.x + s * 0.4f, c.y - s * 0.36f) }, TINTA, style = Stroke(s * 0.13f, cap = StrokeCap.Round))
            // Una espiral: algo que da vueltas y no termina de cerrar.
            "piensa" -> drawPath(Path().apply {
                moveTo(c.x, c.y)
                for (k in 1..24) { val a = k * 0.5f; val r = s * 0.02f * k; lineTo(c.x + r * cos(a), c.y + r * sin(a)) }
            }, MOHO, style = Stroke(s * 0.08f, cap = StrokeCap.Round))
            "duda" -> { drawCircle(p.tenue, s * 0.32f, c, style = Stroke(s * 0.1f)); drawLine(p.tenue, Offset(c.x - s * 0.3f, c.y + s * 0.3f), Offset(c.x + s * 0.3f, c.y - s * 0.3f), s * 0.1f) }
            // Una X de sello rojo, dentro de su recuadro.
            "desacuerdo" -> {
                drawRect(LACRE, Offset(c.x - s * 0.4f, c.y - s * 0.4f), Size(s * 0.8f, s * 0.8f), style = Stroke(s * 0.08f))
                drawLine(LACRE, Offset(c.x - s * 0.25f, c.y - s * 0.25f), Offset(c.x + s * 0.25f, c.y + s * 0.25f), s * 0.1f)
                drawLine(LACRE, Offset(c.x + s * 0.25f, c.y - s * 0.25f), Offset(c.x - s * 0.25f, c.y + s * 0.25f), s * 0.1f)
            }
            else -> {}
        }
    }

    /** Un clip de latón. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        val clip = Path().apply {
            moveTo(c.x + s * 0.12f, c.y - s * 0.1f); lineTo(c.x + s * 0.12f, c.y + s * 0.36f)
            quadraticTo(c.x + s * 0.12f, c.y + s * 0.48f, c.x, c.y + s * 0.48f); quadraticTo(c.x - s * 0.12f, c.y + s * 0.48f, c.x - s * 0.12f, c.y + s * 0.36f)
            lineTo(c.x - s * 0.12f, c.y - s * 0.36f); quadraticTo(c.x - s * 0.12f, c.y - s * 0.5f, c.x + s * 0.04f, c.y - s * 0.5f)
            quadraticTo(c.x + s * 0.22f, c.y - s * 0.5f, c.x + s * 0.22f, c.y - s * 0.36f); lineTo(c.x + s * 0.22f, c.y + s * 0.2f)
        }
        drawPath(clip, LATON_OSC, style = Stroke(s * 0.08f, cap = StrokeCap.Round))
    }

    override val desorden = 1.5f
}
