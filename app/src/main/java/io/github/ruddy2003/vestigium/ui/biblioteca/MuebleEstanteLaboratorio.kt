package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El dibujo del estante del diario de Laboratorio (lo usa `EstanteLaboratorio`, en `BibliotecaFuturo.kt`): la estantería de
 * los cuadernos en el despacho pequeño que hay junto al laboratorio del instituto subterráneo, donde se escribe y no se
 * pipetea. La pared no tiene ventana: arriba, pintura mate gris verdosa con la canaleta de los cables y su regleta; de media
 * altura abajo, el azulejo blanco con su remate, que sigue detrás de la encimera. Contra ella, una estantería de cremallera
 * (postes de acero pintado con sus ranuras y sus tornillos, ménsulas, tablas de melamina con canto de PVC, una desportillada)
 * y, abajo, la encimera de resina negra sobre el mueble de cajones. Todo el dibujo sale de [estante] y de [volumen]; la luz
 * de la noche de los dos sale de la misma curva, [alcance].
 */
internal object MuebleEstanteLaboratorio {
    // ---------------------------------------------------------------- la paleta (sale de la del Ideario del laboratorio)
    /** La pintura mate gris verdosa de la pared, el azulejo y sus juntas. */
    private val PINTURA = Color(0xFF9DAEA6)
    private val AZULEJO = Color(0xFFE2E9EA)
    private val JUNTA = Color(0xFFA6B4B8)
    /** El acero pintado de los postes y las ménsulas, y su sombra (también la lámpara). */
    private val ACERO = Color(0xFF8E9CA4)
    private val ACERO_OSC = Color(0xFF4C5A63)
    /** La melamina de las tablas, su canto de PVC y el aglomerado que asoma donde saltó. */
    private val MELAMINA = Color(0xFFEDEBE3)
    private val PVC = Color(0xFFA4ACAF)
    private val KRAFT = Color(0xFFB98D5C)
    /** La resina negra de la encimera, la tela negra de los cuadernos y la tinta. */
    private val RESINA = Color(0xFF262C2F)
    private val TINTA = Color(0xFF1B2733)
    /** El blanco del papel, de la bata y de la centrífuga. */
    private val BLANCO = Color(0xFFF3F5F2)
    /** Los colores de trabajo, los mismos del Ideario: cian, violeta, rosa, ámbar de aviso y el verde de la única planta. */
    private val CIAN = Color(0xFF0B8FA3)
    private val VIOLETA = Color(0xFF7A5AC8)
    private val ROSA = Color(0xFFE0557A)
    private val AMARILLO = Color(0xFFF2C84A)
    private val VERDE = Color(0xFF4F8B4A)
    /** El vinilo del suelo y el laminado del mueble de abajo. */
    private val VINILO = Color(0xFF8B9895)

    // Las luces: la noche (azul petróleo, la del Ideario), la de los Sueños, su anochecer en claro, el flexo, la luz negra.
    private val NOCHE = Color(0xFF0A1A20)
    private val NOCHE_SUENOS = Color(0xFF140F2C)
    private val ANOCHECER = Color(0xFF2D3158)
    /** El aire de los Sueños en claro: un violeta que no ensucia, para que lo fluorescente destaque sin oscurecer. */
    private val VIOLETA_SUENOS = Color(0xFF5A3F9E)
    private val FLEXO = Color(0xFFFFCF8A)
    private val UV = Color(0xFFA77BFF)
    private val FLUOR = Color(0xFF8CFFB8)
    private val PAPEL_UV = Color(0xFFE8E0FF)
    private val PANTALLA = Color(0xFF7FE6D8)
    private val PILOTO = Color(0xFF63D98E)

    private val LETRA: FontFamily = LetraExtra.jost

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.56f
    const val BASE = 0.29f
    const val LADO = 0.05f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 15.dp
    val SEPARA: Dp = 2.dp

    fun ancho(palabras: Int): Dp = (22 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el cuaderno de tapa dura; 1, el archivador de palanca; 2, la caja de archivo. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** El cuaderno, mediano; el archivador, más bajo; la caja de cartón, la más alta. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.79f + 0.08f * azar(semilla, 1)
        1 -> 0.63f + 0.07f * azar(semilla, 1)
        else -> 0.9f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde está la bombilla del flexo y dónde el tubo de luz negra (bajo la tabla alta), en anchos del mueble. */
    private const val LAMP_X = 0.262f
    private const val LAMP_Y = CORONA - 0.172f
    private const val UV_X = 0.5f
    private const val UV_Y = CORONA + 0.014f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble: el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 127f

    /** Hacia dónde mira la pantalla del flexo: abajo a la derecha, a 58° de la horizontal. */
    private const val EJE_X = 0.53f
    private const val EJE_Y = 0.848f

    /**
     * La curva de la luz del flexo, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que está
     * a [dx] y [dy] de la bombilla, en anchos del mueble. Es un cono: dentro de él (hasta unos 45° del eje) llega entera, y
     * fuera cae deprisa hasta lo que rebota (un tercio). Con la distancia se apaga al cuadrado, para que la primera repisa
     * quede encendida, la segunda a media luz, la tercera tenue y lo de abajo se pierda.
     */
    fun alcance(dx: Float, dy: Float, repisas: Int): Float {
        val d = sqrt(dx * dx * 0.56f + dy * dy)
        val base = (1f - d / 1.3f).coerceIn(0f, 1f).let { it * it }
        val cosEje = (dx * EJE_X + dy * EJE_Y) / sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val t = ((cosEje - 0.09f) / 0.62f).coerceIn(0f, 1f)
        val cono = 0.35f + 0.65f * t * t * (3f - 2f * t)
        return (base * cono * 2.2f).coerceIn(0f, 1f)
    }

    /** La curva del tubo de luz negra de los Sueños: ancho y bajo la tabla alta, alumbra parejo hacia abajo. */
    fun alcanceUV(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 1.28f + 0.07f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx * 0.3f + dy * dy) / radio).coerceIn(0f, 1f).pow(1.35f)
    }

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba; en los Sueños manda el tubo ultravioleta. */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float, suenos: Boolean): Float =
        if (suenos) alcanceUV(fx - UV_X, CORONA - UV_Y + pos * FILA / ANCHO_NOMINAL, repisas)
        else alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL, repisas)

    /**
     * El aire del momento: de día, de noche (el modo oscuro, con el flexo) y en los Sueños ([noche]: la luz negra encendida,
     * al anochecer en claro y en la noche violeta en oscuro).
     */
    private class Aire(val oscuro: Boolean, val noche: Boolean) {
        val lumbre: Boolean get() = oscuro || noche
        val hondo: Color get() = if (oscuro) (if (noche) NOCHE_SUENOS else NOCHE) else SOMBRA_MUEBLE
        /** El color de la luz que manda: el flexo o, en los Sueños, la luz negra. */
        val foco: Color get() = if (noche) UV else FLEXO

        fun luz(c: Color, cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (!oscuro) return if (noche) mezcla(mezcla(c, VIOLETA_SUENOS, 0.25f - 0.1f * k), UV, 0.07f + 0.15f * k) else c
            if (noche) return mezcla(mezcla(c, hondo, 0.93f - 0.8f * k.pow(0.9f)), foco, 0.16f * k)
            return mezcla(mezcla(c, hondo, 0.93f - 0.86f * k.pow(0.75f)), foco, 0.18f * k)
        }
        fun sombra(a: Float): Color = when {
            oscuro -> hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
            noche -> mezcla(SOMBRA_MUEBLE, ANOCHECER, 0.3f).copy(alpha = a.coerceIn(0f, 1f))
            else -> sombraMueble(a)
        }
        fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.5f else a)
        fun filo(a: Float): Color = foco.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.45f).coerceIn(0f, 1f) else 0f)
        /** El color del ánimo: de noche se apaga menos que lo demás, para que se vea también en la repisa más lejana. */
        fun animo(t: Color, cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (!oscuro) return if (noche) mezcla(t, VIOLETA_SUENOS, 0.2f) else t
            return mezcla(mezcla(t, hondo, 0.68f - 0.58f * k), foco, 0.08f * k)
        }
        /** El papel de las etiquetas: casi no se apaga; en los Sueños fluoresce bajo la luz negra. */
        fun etiqueta(cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (noche) return if (oscuro) mezcla(PAPEL_UV, hondo, 0.2f * (1f - k)) else mezcla(PAPEL_UV, Color.White, 0.5f * k)
            if (!oscuro) return BLANCO
            return mezcla(mezcla(BLANCO, hondo, 0.42f - 0.32f * k), FLEXO, 0.12f * k)
        }
        /** Lo que fluoresce en los Sueños (líquidos, franjas, rotulador) o su color de día. */
        fun fluor(c: Color, brillo: Color, cerca: Float): Color =
            if (noche) (if (oscuro) brillo else mezcla(brillo, Color.White, 0.35f * cerca.coerceIn(0f, 1f))) else luz(c, cerca)
    }

    // ---------------------------------------------------------------- ayudantes

    private fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    private fun poligono(vararg xy: Float) = Path().apply {
        moveTo(xy[0], xy[1])
        for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
        close()
    }

    /** El cono de luz del flexo, desde [o] y de [largo]: unos 45° a cada lado del eje de la pantalla. */
    private fun cono(o: Offset, largo: Float): Path {
        fun dir(g: Double) = Offset(cos(Math.toRadians(g)).toFloat(), sin(Math.toRadians(g)).toFloat()) * largo
        return Path().apply {
            moveTo(o.x, o.y)
            for (g in listOf(10.0, 40.0, 70.0, 103.0)) { val p = o + dir(g); lineTo(p.x, p.y) }
            close()
        }
    }

    private fun caja(r: Rect, radio: Float) = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(radio))) }

    /** La sombra de lo que pisa: un óvalo corrido a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Aire, fuerza: Float = 0.34f) =
        drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /**
     * El volumen de gouache sobre algo redondo ya pintado: la media luna de sombra del lado contrario a la luz, el claro del
     * otro y, de noche, el filo de la luz que manda del lado de donde viene ([haciaX]: 1 a la derecha, -1 a la izquierda).
     */
    private fun DrawScope.redondo(forma: Path, a: Aire, cerca: Float, haciaX: Float, fuerza: Float = 1f) {
        val r = forma.getBounds(); val an = r.width; val hh = r.height
        val sl = if (a.oscuro) haciaX else -1f
        drawPath(borde(forma, sl * an * 0.24f, -hh * 0.02f), a.sombra(fuerza * 0.2f))
        drawPath(borde(forma, sl * an * 0.1f, -hh * 0.01f), a.sombra(fuerza * 0.18f))
        clipPath(forma) {
            drawOval(if (a.oscuro) a.filo(0.24f * cerca) else a.claro(0.22f), Offset(if (sl < 0) r.left + an * 0.1f else r.right - an * 0.34f, r.top + hh * 0.08f), Size(an * 0.24f, hh * 0.7f))
        }
        if (a.lumbre && cerca > 0.03f) drawPath(borde(forma, -sl * an * 0.04f, hh * 0.03f), a.filo((0.06f + 0.6f * cerca).coerceAtMost(0.65f)))
    }

    /** Un tornillo de cabeza redonda, con su ranura y la luz arriba a la izquierda. */
    private fun DrawScope.tornillo(p: Offset, r: Float, a: Aire, cerca: Float) {
        drawCircle(a.sombra(0.4f), r * 1.1f, p + Offset(r * 0.35f, r * 0.45f))
        drawCircle(a.luz(mezcla(ACERO, BLANCO, 0.25f), cerca), r, p)
        drawLine(a.luz(ACERO_OSC, cerca), Offset(p.x - r * 0.6f, p.y + r * 0.3f), Offset(p.x + r * 0.6f, p.y - r * 0.3f), r * 0.35f)
        drawCircle(a.claro(0.5f), r * 0.35f, p - Offset(r * 0.35f, r * 0.35f))
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = Aire(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val luzEn = if (noche) Offset(w * UV_X, w * UV_Y) else Offset(w * LAMP_X, w * LAMP_Y)
        fun cerca(p: Offset): Float = if (p.y <= ch) {
            if (noche) alcanceUV((p.x - luzEn.x) / w, (p.y - luzEn.y) / w, n) else alcance((p.x - luzEn.x) / w, (p.y - luzEn.y) / w, n)
        } else cercaCuerpo((p.y - ch) / fila, n, p.x / w, noche)
        fun hacia(x: Float) = if (luzEn.x >= x) 1f else -1f
        /** Hacia dónde cae la sombra de algo en [p] sobre la pared: de día, corta, a la derecha y abajo; de noche, lejos de la luz. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!a.lumbre) return Offset(largo * 1.1f, largo * 0.75f)
            val v = p - luzEn; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 2.6f)
        }
        val gT = w * 0.032f            // el canto de la tabla alta
        val fT = w * 0.012f            // lo que se ve de su cara de arriba
        val apoyo = ch - gT - fT * 0.45f
        val lado = w * LADO
        val yAz = if (n > 0) ch + fila * maxOf(1f, (n * 0.5f).toInt().toFloat()) - 6f * u else ch + w * 0.2f
        val yMesa = b.top + w * 0.062f      // la cara de arriba de la encimera

        pared(a, ch, yAz, yMesa, apoyo)
        // El halo de la luz que manda: unas pocas manchas, no un velo.
        if (a.lumbre) {
            val f = if (oscuro) 1f else 1.1f
            if (noche) {
                mancha(luzEn + Offset(0f, w * 0.08f), w * 1.0f, UV.copy(alpha = 0.2f * f))
                mancha(luzEn + Offset(0f, w * 0.04f), w * 0.45f, UV.copy(alpha = 0.3f * f))
            } else {
                // Lo que rebota alrededor de la bombilla.
                mancha(luzEn, w * 0.9f, FLEXO.copy(alpha = 0.12f))
                mancha(luzEn + Offset(w * 0.02f, w * 0.05f), w * 0.22f, LUZ_MUEBLE.copy(alpha = 0.32f))
                // El cono de la pantalla, abajo a la derecha: mucha luz dentro y el borde marcado.
                clipPath(cono(luzEn, w * 1.5f)) {
                    mancha(luzEn, w * 1.15f, FLEXO.copy(alpha = 0.32f))
                    mancha(luzEn + Offset(w * EJE_X, w * EJE_Y) * 0.3f, w * 0.75f, LUZ_MUEBLE.copy(alpha = 0.28f))
                }
            }
        }
        canaleta(a, w * 0.042f, { p -> cerca(p) })

        // ---- lo que cuelga de la pared: el póster, las notas y el reloj
        val cPoster = Offset(w * 0.215f, w * 0.158f)
        poster(cPoster, w * 0.17f, a, cerca(cPoster), caida(cPoster, w * 0.006f))
        notas(Offset(w * 0.335f, w * 0.2f), w * 0.042f, a, cerca(Offset(w * 0.34f, w * 0.22f)), caida(Offset(w * 0.34f, w * 0.22f), w * 0.004f))
        val cReloj = Offset(w * 0.452f, w * 0.15f)
        reloj(cReloj, w * 0.05f, a, cerca(cReloj), caida(cReloj, w * 0.007f))

        // ---- al pie: la encimera, el mueble de cajones, el suelo y el taburete
        pie(d, a, yMesa) { p -> cerca(p) }

        // ---- las sombras del mueble en la pared: a la derecha del poste derecho y bajo la última tabla
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.24f), Offset(w - lado * 0.15f, ch), Size(lado * 0.15f, yMesa - ch))
        drawRect(a.sombra(if (a.lumbre) 0.38f else 0.26f), Offset(lado, b.top), Size(w - 2 * lado + w * 0.01f, 5f * u))
        drawRect(a.sombra(0.12f), Offset(lado, b.top + 5f * u), Size(w - 2 * lado + w * 0.01f, 7f * u))

        // ---- el fondo de cada repisa: la pared en sombra, con la luz que entra por la izquierda
        d.repisas.forEachIndexed { k, r ->
            clipRect(lado, r.top, w - lado, r.bottom) {
                val an = w - 2 * lado
                val f = if (oscuro) 1.2f else 1f
                drawRect(a.sombra(if (oscuro) 0.22f else 0.16f), Offset(lado, r.top), Size(an, r.height))
                // La sombra de la tabla de arriba: honda junto a ella y deshecha hacia abajo.
                drawRect(a.sombra(0.32f * f), Offset(lado, r.top), Size(an, 7f * u))
                drawRect(a.sombra(0.18f * f), Offset(lado, r.top), Size(an, 16f * u))
                drawRect(a.sombra(0.09f * f), Offset(lado, r.top), Size(an, 30f * u))
                pinceladas(Rect(lado, r.top + 20f * u, w - lado, r.top + 40f * u), a.sombra(1f), 951 + k, 10, u, 0.07f)
                // La sombra del poste izquierdo y el claro que deja pasar a su lado.
                drawRect(a.sombra(0.24f), Offset(lado, r.top), Size(7f * u, r.height))
                if (!oscuro) {
                    pinceladas(Rect(lado + 8f * u, r.top + r.height * 0.3f, lado + an * 0.6f, r.bottom), luzMueble(1f), 955 + k, 16, u, if (noche) 0.04f else 0.08f)
                    pinceladas(Rect(lado + 8f * u, r.top + r.height * 0.5f, lado + an * 0.32f, r.bottom), luzMueble(1f), 959 + k, 10, u, if (noche) 0.04f else 0.08f)
                }
                // De noche, el cono del flexo entra en el fondo de las repisas de arriba.
                if (oscuro && !noche) {
                    val kc = cerca(Offset(w * 0.45f, r.center.y))
                    if (kc > 0.15f) clipPath(cono(luzEn, w * 2f)) { mancha(Offset(w * 0.45f, r.center.y), w * 0.7f, FLEXO.copy(alpha = 0.34f * kc)) }
                }
                // El costado derecho, más hondo.
                pinceladas(Rect(w - lado - an * 0.34f, r.top, w - lado, r.bottom), a.sombra(1f), 963 + k, 20, u, 0.07f)
                drawRect(a.sombra(0.12f), Offset(w - lado - 11f * u, r.top), Size(11f * u, r.height))
                drawRect(a.sombra(0.2f), Offset(w - lado - 5f * u, r.top), Size(5f * u, r.height))
                // La sombra que arroja el último volumen sobre el fondo, a su derecha.
                val oc = d.ocupado.getOrNull(k) ?: r.left
                if (oc > r.left + 6f * u) {
                    val largo = (if (a.lumbre) 20f else 11f) * u
                    drawPath(poligono(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.22f, oc + largo, r.top + r.height * 0.3f, oc + largo, r.bottom), a.sombra(if (oscuro) 0.4f else 0.26f))
                    drawPath(poligono(oc + largo, r.bottom, oc + largo, r.top + r.height * 0.3f, oc + largo * 1.5f, r.top + r.height * 0.36f, oc + largo * 1.5f, r.bottom), a.sombra(0.12f))
                }
            }
        }

        // ---- los postes de cremallera, con su sombra en la pared
        listOf(d.izquierda, d.derecha).forEachIndexed { i, r ->
            val p = Rect(r.left + r.width * 0.16f, ch - gT, r.right - r.width * 0.16f, yMesa)
            poste(p, i, a, n, ch, fila)
        }

        // ---- las tablas con sus ménsulas y su rótulo, y lo de los huecos libres
        d.tablas.forEachIndexed { k, r ->
            tabla(Rect(lado - 2f * u, r.top, w - lado + 2f * u, r.bottom), 3f * u, k, a) { x -> cerca(Offset(x, r.center.y)) }
            listOf(lado to 1f, w - lado to -1f).forEach { (x, s) -> mensula(Offset(x, r.bottom), s, 11f * u, a, cerca(Offset(x, r.bottom))) }
        }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoLibre(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)), hacia((oc + r.right) / 2))
        }
        d.placas.forEach { p -> rotulo(p, medidor, a, cerca(p.r.center)) }

        // ---- la tabla alta, con el tubo de luz negra atornillado debajo
        tabla(Rect(-2f * u, ch - gT, w + 2f * u, ch), fT, 99, a) { x -> cerca(Offset(x, ch - gT)) }
        if (a.lumbre && !noche) clipRect(0f, ch - gT - fT, w, ch) { mancha(Offset(luzEn.x + w * 0.04f, ch - gT - fT), w * 0.36f, FLEXO.copy(alpha = if (oscuro) 0.4f else 0.2f)) }
        drawRect(a.sombra(if (oscuro) 0.5f else 0.34f), Offset(lado, ch), Size(w - 2 * lado, 3.5f * u))
        tuboUV(Rect(w * 0.3f, ch + 1.2f * u, w * 0.7f, ch + 6.2f * u), a, cerca(Offset(w * 0.5f, ch)))

        // ---- lo que hay sobre la tabla alta, de atrás adelante
        val cCentri = Offset(w * 0.672f, apoyo)
        centrifuga(cCentri, w * 0.25f, a, { p -> cerca(p) }, hacia(cCentri.x), caida(cCentri - Offset(0f, w * 0.1f), w * 0.012f))
        val pieGuantes = Offset(w * 0.16f, apoyo)
        cajaGuantes(pieGuantes, w * 0.112f, w * 0.066f, a, cerca(pieGuantes - Offset(0f, w * 0.04f)), caida(pieGuantes, w * 0.008f))
        val cCinta = Offset(w * 0.245f, apoyo - w * 0.021f)
        cintaEtiquetas(cCinta, w * 0.021f, apoyo, ch, a, cerca(cCinta))
        val pieTaza = Offset(w * 0.315f, apoyo)
        taza(pieTaza, w * 0.044f, a, cerca(pieTaza - Offset(0f, w * 0.03f)), hacia(pieTaza.x), caida(pieTaza, w * 0.006f))
        val piePlanta = Offset(w * 0.432f, apoyo)
        planta(piePlanta, w * 0.068f, ch, a, cerca(piePlanta - Offset(0f, w * 0.06f)), hacia(piePlanta.x), caida(piePlanta - Offset(0f, w * 0.05f), w * 0.01f))
        tubitos(Offset(w * 0.508f, apoyo), w * 0.03f, a, cerca(Offset(w * 0.51f, apoyo)))
        bata(Offset(w * 0.935f, w * 0.168f), w, apoyo, a, cerca(Offset(w * 0.93f, w * 0.33f)), caida(Offset(w * 0.93f, w * 0.33f), w * 0.012f))
        lampara(Offset(w * 0.078f, ch - gT), a, caida(Offset(w * 0.1f, w * 0.3f), w * 0.01f))

        // ---- lo que alumbra por sí mismo, encima de todo
        if (a.lumbre) brillos(cCentri, w * 0.25f, a)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- la pared

    /** La pintura mate de arriba, el azulejo de abajo con su remate, la mancha de la luz de día y el uso. */
    private fun DrawScope.pared(a: Aire, ch: Float, yAz: Float, yMesa: Float, apoyo: Float) {
        val u = density; val w = size.width
        val lejos = 0.09f
        drawRect(a.luz(PINTURA, lejos), Offset.Zero, Size(w, yAz))
        val veces = (yAz / (w * 0.5f)).coerceIn(1f, 12f)
        val pano = Rect(0f, 0f, w, yAz)
        pinceladas(pano, a.luz(mezcla(PINTURA, SOMBRA_MUEBLE, 0.35f), lejos), 901, (40 * veces).toInt(), u, 0.09f)
        pinceladas(pano, a.luz(mezcla(PINTURA, LUZ_MUEBLE, 0.5f), lejos), 902, (30 * veces).toInt(), u, 0.1f)
        pinceladas(pano, a.luz(mezcla(PINTURA, CIAN, 0.25f), lejos), 903, (12 * veces).toInt(), u, 0.06f)
        // El azulejo: piezas de 14 dp, cada una con su tono, y las juntas grises.
        val pieza = maxOf(1f, 14f * u)
        drawRect(a.luz(AZULEJO, lejos), Offset(0f, yAz), Size(w, yMesa - yAz))
        var fy = 0
        var y = yAz
        while (y < yMesa) {
            var fx = 0
            var x = 0f
            while (x < w) {
                val z = azar(911 + fy, fx)
                if (z < 0.3f) drawRect(a.luz(mezcla(AZULEJO, if (z < 0.12f) JUNTA else LUZ_MUEBLE, 0.25f + z), lejos).copy(alpha = 0.6f), Offset(x + 1f * u, y + 1f * u), Size(pieza - 2f * u, pieza - 2f * u))
                // El brillo del vidriado, arriba a la izquierda de cada pieza.
                if (!a.oscuro) drawRect(luzMueble(0.22f), Offset(x + 1.5f * u, y + 1.5f * u), Size(pieza * 0.35f, 0.8f * u))
                x += pieza; fx++
            }
            y += pieza; fy++
        }
        y = yAz
        while (y < yMesa) { drawLine(a.luz(JUNTA, lejos), Offset(0f, y), Offset(w, y), 0.8f * u); y += pieza }
        var x = 0f
        while (x < w) { drawLine(a.luz(JUNTA, lejos), Offset(x, yAz), Offset(x, yMesa), 0.8f * u); x += pieza }
        // Una pieza rajada y la humedad sobre la encimera.
        val rj = Offset(w * 0.83f, yAz + pieza * 2.4f)
        drawLine(a.sombra(0.4f), rj, rj + Offset(pieza * 0.6f, pieza * 0.5f), 0.7f * u)
        drawLine(a.sombra(0.3f), rj + Offset(pieza * 0.3f, pieza * 0.25f), rj + Offset(pieza * 0.15f, pieza * 0.9f), 0.6f * u)
        // El remate del azulejo: una moldura redonda, con su luz arriba y su sombra debajo.
        drawRect(a.luz(mezcla(AZULEJO, PVC, 0.2f), lejos), Offset(0f, yAz - 3.5f * u), Size(w, 3.5f * u))
        drawRect(a.claro(0.5f), Offset(0f, yAz - 3.5f * u), Size(w, 1f * u))
        drawRect(a.sombra(0.25f), Offset(0f, yAz), Size(w, 1.6f * u))
        // La mancha de luz de los tubos del techo, arriba a la izquierda; hacia la derecha y abajo, penumbra.
        if (!a.oscuro) mancha(Offset(w * 0.02f, w * 0.0f), w * 1.0f, luzMueble(if (a.noche) 0.12f else 0.48f))
        // Y lejos de los tubos, abajo a la derecha, la pared se queda en penumbra.
        if (!a.oscuro) mancha(Offset(w * 1.05f, yMesa), w * 1.1f, sombraMueble(if (a.noche) 0.1f else 0.2f))
        pinceladas(Rect(w * 0.6f, 0f, w, ch), a.sombra(1f), 908, 18, u, 0.06f)
        // El uso: los agujeros de una repisa que ya no está, rellenos y repintados, y restos de cinta vieja.
        for (k in 0 until 3) {
            val p = Offset(w * (0.58f + 0.05f * k), w * 0.27f + w * 0.004f * k)
            drawCircle(a.luz(mezcla(PINTURA, LUZ_MUEBLE, 0.35f), lejos), 2.2f * u, p)
            drawCircle(a.sombra(0.28f), 0.8f * u, p + Offset(0.3f * u, 0.3f * u))
        }
        listOf(Offset(w * 0.06f, w * 0.13f), Offset(w * 0.36f, w * 0.34f)).forEachIndexed { i, o ->
            rotate(if (i == 0) -12f else 8f, o) { drawRect(a.luz(mezcla(PINTURA, AMARILLO, 0.35f), lejos).copy(alpha = 0.5f), o, Size(w * 0.03f, w * 0.009f)) }
        }
        // Las rozaduras de las sillas en el azulejo bajo la encimera no se ven; sí la del borde de las carpetas en la pintura.
        pinceladas(Rect(0f, apoyo - w * 0.03f, w, apoyo), a.sombra(1f), 909, 12, u, 0.07f)
    }

    /** La canaleta de los cables de lado a lado, con sus tapas, la regleta colgada y los cables que bajan a la lámpara y a la centrífuga. */
    private fun DrawScope.canaleta(a: Aire, y0: Float, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val alto = w * 0.026f
        val k = cerca(Offset(w * 0.3f, y0))
        // Los cables, que salen de la canaleta y de la regleta (antes que ellas: quedan detrás).
        val cable = a.luz(mezcla(TINTA, ACERO_OSC, 0.3f), k)
        val c1 = Path().apply { moveTo(w * 0.058f, y0 + alto); cubicTo(w * 0.06f, y0 + w * 0.1f, w * 0.03f, y0 + w * 0.16f, w * 0.045f, w * 0.29f) }
        val c2 = Path().apply { moveTo(w * 0.7f, y0 + alto + w * 0.03f); cubicTo(w * 0.71f, w * 0.17f, w * 0.75f, w * 0.2f, w * 0.74f, w * 0.34f) }
        listOf(c1, c2).forEach { c ->
            drawPath(corrido(c, 2.2f * u, 2f * u), a.sombra(0.25f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(c, cable, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.24f), Offset(0f, y0 + alto), Size(w, 2.6f * u))
        drawRect(a.luz(mezcla(BLANCO, PVC, 0.35f), k), Offset(0f, y0), Size(w, alto))
        drawRect(a.claro(0.5f), Offset(0f, y0), Size(w, 1f * u))
        drawRect(a.sombra(0.16f), Offset(0f, y0 + alto * 0.62f), Size(w, alto * 0.38f))
        drawRect(a.sombra(0.3f), Offset(0f, y0 + alto - 0.8f * u), Size(w, 0.8f * u))
        listOf(0.27f, 0.79f).forEach { f ->
            drawLine(a.sombra(0.4f), Offset(w * f, y0), Offset(w * f, y0 + alto), 0.8f * u)
            drawLine(a.claro(0.4f), Offset(w * f + 0.9f * u, y0), Offset(w * f + 0.9f * u, y0 + alto), 0.6f * u)
        }
        // La salida del cable de la lámpara: un pasacables.
        drawRoundRect(a.luz(PVC, k), Offset(w * 0.05f, y0 + alto - 1f * u), Size(w * 0.016f, 4f * u), CornerRadius(1.2f * u))
        // La regleta, atornillada bajo la canaleta: cuatro enchufes, dos ocupados, y su interruptor.
        val r = Rect(w * 0.6f, y0 + alto + 1.5f * u, w * 0.78f, y0 + alto + 1.5f * u + w * 0.03f)
        val kr = cerca(r.center)
        drawRoundRect(a.sombra(0.3f), r.topLeft + Offset(2f * u, 2f * u), r.size, CornerRadius(2f * u))
        drawRoundRect(a.luz(BLANCO, kr), r.topLeft, r.size, CornerRadius(2f * u))
        drawRect(a.sombra(0.16f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
        drawRect(a.claro(0.5f), Offset(r.left + 2f * u, r.top), Size(r.width - 4f * u, 0.8f * u))
        for (j in 0 until 4) {
            val c = Offset(r.left + r.width * (0.2f + 0.17f * j), r.center.y)
            drawCircle(a.luz(mezcla(BLANCO, PVC, 0.4f), kr), r.height * 0.32f, c)
            if (j == 1 || j == 2) {
                drawRoundRect(a.luz(TINTA, kr), Offset(c.x - r.height * 0.26f, c.y - r.height * 0.3f), Size(r.height * 0.52f, r.height * 0.6f), CornerRadius(1.5f * u))
                drawRect(a.claro(0.3f), Offset(c.x - r.height * 0.22f, c.y - r.height * 0.26f), Size(0.8f * u, r.height * 0.5f))
            } else {
                drawCircle(a.sombra(0.5f), 0.7f * u, c - Offset(r.height * 0.1f, 0f))
                drawCircle(a.sombra(0.5f), 0.7f * u, c + Offset(r.height * 0.1f, 0f))
            }
        }
        drawRoundRect(a.fluor(ROSA, ROSA, kr), Offset(r.right - r.width * 0.12f, r.top + r.height * 0.25f), Size(r.width * 0.07f, r.height * 0.5f), CornerRadius(1f * u))
        if (a.lumbre) drawCircle(ROSA.copy(alpha = 0.8f), 0.8f * u, Offset(r.right - r.width * 0.085f, r.top + r.height * 0.5f))
    }

    /** El póster chico de la tabla periódica, torcido, con su cinta en las esquinas de arriba y la de abajo levantada. */
    private fun DrawScope.poster(c: Offset, an: Float, a: Aire, cerca: Float, caida: Offset) {
        val u = density
        val al = an * 0.68f
        val r = Rect(c.x - an / 2, c.y - al / 2, c.x + an / 2, c.y + al / 2)
        val bloques = listOf(Color(0xFFE6A3A0), Color(0xFFEFC79D), Color(0xFFA9C4E0), Color(0xFFB2D6AA), Color(0xFFCDBDE6), Color(0xFFEAD9A2))
        rotate(-4f, c) {
            val hoja = poligono(r.left, r.top, r.right, r.top, r.right, r.bottom - an * 0.06f, r.right - an * 0.07f, r.bottom, r.left, r.bottom)
            translate(caida.x, caida.y) { drawPath(hoja, a.sombra(if (a.lumbre) 0.4f else 0.2f)) }
            drawPath(hoja, a.luz(mezcla(BLANCO, AMARILLO, 0.08f), cerca))
            val ins = Rect(r.left + an * 0.05f, r.top + al * 0.2f, r.right - an * 0.05f, r.bottom - al * 0.08f)
            val cw = ins.width / 18f; val chh = ins.height / 9.6f
            for (fila in 0 until 7) for (col in 0 until 18) {
                val hay = when (fila) { 0 -> col == 0 || col == 17; 1, 2 -> col <= 1 || col >= 12; else -> true }
                if (!hay) continue
                if (fila >= 5 && col >= 16) continue
                val kb = when { col <= 1 -> col; col <= 11 -> 2; col <= 16 -> 3; else -> 4 }
                drawRect(a.luz(bloques[kb], cerca), Offset(ins.left + col * cw + 0.25f * u, ins.top + fila * chh + 0.25f * u), Size(cw - 0.5f * u, chh - 0.5f * u))
            }
            for (col in 2 until 16) { drawRect(a.luz(bloques[5], cerca), Offset(ins.left + col * cw + 0.25f * u, ins.top + 7.7f * chh), Size(cw - 0.5f * u, chh * 0.9f)) }
            // El título: un trazo, no letras.
            drawRect(a.luz(TINTA, cerca).copy(alpha = 0.7f), Offset(ins.left + 3 * cw, r.top + al * 0.07f), Size(cw * 8, al * 0.05f))
            drawRect(a.luz(TINTA, cerca).copy(alpha = 0.35f), Offset(ins.left + 3 * cw, r.top + al * 0.14f), Size(cw * 5, al * 0.025f))
            // La esquina levantada: el doblez que coge la luz y su sombra debajo.
            drawPath(poligono(r.right, r.bottom - an * 0.06f, r.right - an * 0.07f, r.bottom, r.right - an * 0.075f, r.bottom - an * 0.07f), a.luz(mezcla(BLANCO, PVC, 0.35f), cerca))
            drawPath(poligono(r.right, r.bottom - an * 0.06f, r.right - an * 0.075f, r.bottom - an * 0.07f, r.right - an * 0.02f, r.bottom - an * 0.06f), a.sombra(0.2f))
            drawPath(hoja, a.sombra(0.25f), style = Stroke(0.5f * u))
            drawRect(a.sombra(0.1f), Offset(r.left + an * 0.6f, r.top), Size(an * 0.4f, al))
            // La cinta de las dos esquinas de arriba.
            listOf(Offset(r.left + an * 0.02f, r.top), Offset(r.right - an * 0.02f, r.top)).forEachIndexed { i, o ->
                rotate(if (i == 0) -38f else 34f, o) {
                    drawRect(a.luz(mezcla(AMARILLO, BLANCO, 0.6f), cerca).copy(alpha = 0.75f), Offset(o.x - an * 0.07f, o.y - an * 0.018f), Size(an * 0.14f, an * 0.036f))
                }
            }
        }
    }

    /** Tres notas adhesivas pegadas en la pared, una con la esquina doblada, con trazos de rotulador que no se leen. */
    private fun DrawScope.notas(o: Offset, lado: Float, a: Aire, cerca: Float, caida: Offset) {
        val u = density
        val colores = listOf(mezcla(AMARILLO, BLANCO, 0.25f), mezcla(ROSA, BLANCO, 0.45f), mezcla(CIAN, BLANCO, 0.6f))
        val sitios = listOf(Offset(0f, 0f) to -6f, Offset(lado * 0.62f, lado * 0.42f) to 5f, Offset(lado * 0.05f, lado * 1.12f) to 2f)
        sitios.forEachIndexed { i, (d, giro) ->
            val c = o + d + Offset(lado / 2, lado / 2)
            rotate(giro, c) {
                val r = Rect(c.x - lado / 2, c.y - lado / 2, c.x + lado / 2, c.y + lado / 2)
                val cuerpo = if (i == 1) poligono(r.left, r.top, r.right, r.top, r.right, r.bottom - lado * 0.25f, r.right - lado * 0.25f, r.bottom, r.left, r.bottom)
                else poligono(r.left, r.top, r.right, r.top, r.right, r.bottom, r.left, r.bottom)
                translate(caida.x, caida.y + (if (i == 2) 1.5f * u else 0f)) { drawPath(cuerpo, a.sombra(if (a.lumbre) 0.38f else 0.18f)) }
                drawPath(cuerpo, a.luz(colores[i], cerca))
                // La franja de pegamento, arriba, un poco más oscura; y el pie, que se despega y coge sombra.
                drawRect(a.sombra(0.08f), r.topLeft, Size(lado, lado * 0.22f))
                drawRect(a.claro(0.3f), Offset(r.left, r.bottom - lado * 0.16f), Size(lado * 0.9f, 0.8f * u))
                for (j in 0 until 3) {
                    val yy = r.top + lado * (0.36f + 0.18f * j)
                    drawLine(a.fluor(TINTA, if (j == 0) FLUOR else PAPEL_UV, cerca).copy(alpha = 0.7f), Offset(r.left + lado * 0.14f, yy), Offset(r.right - lado * (0.16f + 0.2f * azar(77 + i, j)), yy + lado * 0.03f), 0.8f * u, StrokeCap.Round)
                }
                if (i == 1) drawPath(poligono(r.right, r.bottom - lado * 0.25f, r.right - lado * 0.25f, r.bottom, r.right - lado * 0.28f, r.bottom - lado * 0.28f), a.luz(mezcla(colores[i], BLANCO, 0.3f), cerca))
            }
        }
    }

    /** El reloj de pared: aro de acero, la esfera con sus marcas, las agujas y el segundero rosa. */
    private fun DrawScope.reloj(c: Offset, r: Float, a: Aire, cerca: Float, caida: Offset) {
        val u = density
        drawCircle(a.sombra(if (a.lumbre) 0.42f else 0.22f), r, c + caida)
        drawCircle(a.luz(ACERO_OSC, cerca), r, c)
        drawCircle(a.luz(BLANCO, cerca), r * 0.86f, c)
        drawArc(a.sombra(0.2f), -10f, 180f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.1f))
        for (j in 0 until 12) {
            val ang = Math.toRadians(30.0 * j).toFloat()
            val l = if (j % 3 == 0) 0.2f else 0.1f
            drawLine(a.luz(TINTA, cerca), Offset(c.x + r * 0.76f * cos(ang), c.y + r * 0.76f * sin(ang)), Offset(c.x + r * (0.76f - l) * cos(ang), c.y + r * (0.76f - l) * sin(ang)), (if (j % 3 == 0) 1.4f else 0.8f) * u)
        }
        fun aguja(grados: Float, largo: Float, grueso: Float, col: Color) {
            val ang = Math.toRadians(grados - 90.0).toFloat()
            drawLine(a.sombra(0.25f), c + Offset(1f * u, 1.2f * u), Offset(c.x + r * largo * cos(ang) + 1f * u, c.y + r * largo * sin(ang) + 1.2f * u), grueso, StrokeCap.Round)
            drawLine(col, c, Offset(c.x + r * largo * cos(ang), c.y + r * largo * sin(ang)), grueso, StrokeCap.Round)
        }
        aguja(if (a.lumbre) 335f else 125f, 0.42f, 2f * u, a.luz(TINTA, cerca))
        aguja(if (a.lumbre) 60f else 230f, 0.64f, 1.4f * u, a.luz(TINTA, cerca))
        aguja(if (a.lumbre) 180f else 20f, 0.7f, 0.6f * u, a.luz(ROSA, cerca))
        drawCircle(a.luz(ROSA, cerca), 1.4f * u, c)
        // El cristal: un reflejo en arco arriba a la izquierda.
        drawArc(a.claro(0.55f), 200f, 60f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        if (a.lumbre) drawArc(a.filo(0.5f * cerca), 120f, 100f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1f * u))
    }

    // ---------------------------------------------------------------- el mueble

    /** Un poste de cremallera: acero pintado con su columna de ranuras, la arista de la luz, el lado de la sombra y los tornillos a la pared. */
    private fun DrawScope.poste(p: Rect, semilla: Int, a: Aire, repisas: Int, ch: Float, fila: Float) {
        val u = density; val w = size.width
        val fx = p.center.x / w
        val tramos = if (a.lumbre) (p.height / maxOf(1f, fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        for (t in 0 until tramos) {
            val ya = p.top + p.height * t / tramos; val yb = p.top + p.height * (t + 1) / tramos + 0.6f
            val k = cercaCuerpo(((ya + yb) / 2 - ch) / fila, repisas, fx, a.noche)
            drawRect(a.luz(ACERO, k), Offset(p.left, ya), Size(p.width, yb - ya))
        }
        drawRect(a.claro(0.3f), p.topLeft, Size(p.width * 0.18f, p.height))
        drawRect(a.sombra(0.3f), Offset(p.right - p.width * 0.26f, p.top), Size(p.width * 0.26f, p.height))
        drawRect(a.sombra(0.35f), Offset(p.right - 0.8f * u, p.top), Size(0.8f * u, p.height))
        // Las ranuras de la cremallera, de dos en dos, con su labio de abajo a la luz.
        val paso = maxOf(1f, 8f * u)
        var y = p.top + 6f * u
        var j = 0
        while (y < p.bottom - 6f * u) {
            val k = cercaCuerpo((y - ch) / fila, repisas, fx, a.noche)
            drawRoundRect(a.luz(TINTA, k), Offset(p.center.x - 1.1f * u, y), Size(2.2f * u, 4.4f * u), CornerRadius(1.1f * u))
            drawLine(a.claro(0.4f), Offset(p.center.x - 1f * u, y + 4.6f * u), Offset(p.center.x + 1f * u, y + 4.6f * u), 0.6f * u)
            // Cada tanto, un tornillo a la pared, y la pintura saltada.
            if (j % 9 == 4) tornillo(Offset(p.center.x, y + 2.2f * u - paso * 0.5f), 1.6f * u, a, k)
            if (azar(semilla + 40, j) < 0.06f) drawOval(a.luz(mezcla(ACERO, BLANCO, 0.45f), k), Offset(p.left + p.width * 0.1f, y), Size(p.width * 0.3f, 2f * u))
            y += paso; j++
        }
        if (a.lumbre) {
            val k = cercaCuerpo(0.3f, repisas, fx, a.noche)
            val x = if ((if (a.noche) UV_X else LAMP_X) > fx) p.right - 1.2f * u else p.left
            drawRect(a.filo(0.5f * k), Offset(x, p.top), Size(1.2f * u, fila * 1.2f))
        }
    }

    /**
     * Una tabla de melamina: su cara de arriba, vista apenas ([fondo]), y el canto de PVC con su luz, sus rozaduras y, en
     * alguna, un desportillado donde se ve el aglomerado. De noche se pinta por tramos, cada uno con la luz que le llega.
     */
    private fun DrawScope.tabla(cara: Rect, fondo: Float, semilla: Int, a: Aire, cerca: (Float) -> Float) {
        val u = density
        val tramos = if (a.lumbre) 28 else 1
        for (t in 0 until tramos) {
            val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
            val k = cerca((xa + xb) / 2)
            drawRect(a.luz(MELAMINA, k), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(mezcla(PVC, MELAMINA, 0.25f), k), Offset(xa, cara.top), Size(xb - xa, cara.height))
            if (a.lumbre) drawRect(a.filo(0.08f + 0.5f * k), Offset(xa, cara.top), Size(xb - xa, 1f * u))
        }
        clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
            // La cara de arriba: rayas de carpetas arrastradas, una mancha de café seco.
            for (j in 0 until 4) {
                val x = cara.left + cara.width * azar(semilla + 300, j)
                drawLine(a.sombra(0.12f), Offset(x, cara.top - fondo * 0.5f), Offset(x + cara.width * 0.12f, cara.top - fondo * 0.4f), 0.5f * u)
            }
            // El canto: el lomo redondeado del PVC (claro arriba, sombra abajo) y sus rozaduras.
            drawRect(a.claro(0.35f), Offset(cara.left, cara.top + 1f * u), Size(cara.width, cara.height * 0.25f))
            drawRect(a.sombra(0.14f), Offset(cara.left, cara.top + cara.height * 0.6f), Size(cara.width, cara.height * 0.4f))
            for (j in 0 until 6) {
                val x = cara.left + cara.width * azar(semilla + 310, j)
                drawOval(a.sombra(0.14f), Offset(x, cara.top + cara.height * (0.3f + 0.4f * azar(semilla + 320, j))), Size(cara.width * (0.01f + 0.03f * azar(semilla + 330, j)), 1.2f * u))
            }
            // El desportillado: el PVC saltó y asoma el aglomerado con su grano.
            if (semilla % 3 == 1) {
                val x = cara.left + cara.width * (0.3f + 0.4f * azar(semilla + 340, 0))
                val m = poligono(x, cara.top, x + 15f * u, cara.top, x + 12f * u, cara.top + cara.height * 0.55f, x + 6f * u, cara.top + cara.height * 0.7f, x + 1.5f * u, cara.top + cara.height * 0.4f)
                val k = cerca(x)
                drawPath(m, a.luz(KRAFT, k))
                clipPath(m) { for (j in 0 until 14) drawCircle(a.luz(if (j % 2 == 0) mezcla(KRAFT, TINTA, 0.4f) else mezcla(KRAFT, BLANCO, 0.4f), k), 0.6f * u, Offset(x + 15f * u * azar(semilla + 350, j), cara.top + cara.height * 0.7f * azar(semilla + 360, j))) }
                drawPath(borde(m, -0.8f * u, 1f * u), a.sombra(0.4f))
            }
        }
        drawLine(a.sombra(0.3f), Offset(cara.left, cara.top - fondo), Offset(cara.right, cara.top - fondo), 0.6f * u)
        drawRect(a.sombra(0.4f), Offset(cara.left, cara.bottom - 1.4f * u), Size(cara.width, 1.4f * u))
    }

    /** Una ménsula de chapa en escuadra bajo la tabla, con su sombra en la pared. */
    private fun DrawScope.mensula(esq: Offset, lado: Float, s: Float, a: Aire, cerca: Float) {
        val u = density
        val forma = poligono(esq.x, esq.y, esq.x + lado * s, esq.y, esq.x + lado * s * 0.8f, esq.y + s * 0.25f, esq.x + lado * s * 0.15f, esq.y + s, esq.x, esq.y + s)
        drawPath(corrido(forma, 2.2f * u, 2.4f * u), a.sombra(if (a.lumbre) 0.4f else 0.26f))
        drawPath(forma, a.luz(mezcla(ACERO, ACERO_OSC, 0.3f), cerca))
        drawPath(borde(forma, -lado * 1.2f * u, -1.2f * u), a.sombra(0.3f))
        drawLine(a.claro(0.35f), Offset(esq.x + lado * s * 0.15f, esq.y + s), Offset(esq.x + lado * s * 0.8f, esq.y + s * 0.25f), 0.6f * u)
    }

    /** El rótulo de un mes: la cinta de la etiquetadora pegada en el canto, con su cuadro de color y una punta despegada. */
    private fun DrawScope.rotulo(p: PlacaEstante, medidor: TextMeasurer, a: Aire, cerca: Float) {
        val u = density
        val r = Rect(p.r.left, p.r.top - 1f * u, p.r.right, p.r.bottom + 1f * u)
        val forma = poligono(r.left, r.top + 0.5f * u, r.right - 5f * u, r.top, r.right, r.top - 1.5f * u, r.right - 0.5f * u, r.bottom - 1f * u, r.left + 0.4f * u, r.bottom)
        drawPath(corrido(forma, 1.2f * u, 1.6f * u), a.sombra(0.35f))
        drawPath(forma, a.etiqueta(maxOf(cerca, 0.2f)))
        clipPath(forma) {
            drawRect(a.fluor(CIAN, FLUOR, cerca), r.topLeft, Size(r.height * 0.9f, r.height))
            drawRect(a.sombra(0.12f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
            drawRect(a.claro(0.5f), r.topLeft, Size(r.width, 1f * u))
            // La punta despegada, que coge la luz.
            drawRect(a.claro(0.4f), Offset(r.right - 6f * u, r.top - 2f * u), Size(6f * u, r.height + 2f * u))
        }
        textoEn(medidor, p.texto, Rect(r.left + r.height * 1.15f, r.top + 1.5f * u, r.right - 7f * u, r.bottom - 1.5f * u), LETRA, TINTA, 10.5f, peso = FontWeight.Medium, espaciado = 0.4f, minimo = 6f)
    }

    /** El tubo de luz negra bajo la tabla alta: la regleta del tubo y el vidrio violeta, encendido en los Sueños. */
    private fun DrawScope.tuboUV(r: Rect, a: Aire, cerca: Float) {
        val u = density
        drawRect(a.sombra(0.35f), r.topLeft + Offset(1.5f * u, 2f * u), r.size)
        drawRect(a.luz(ACERO_OSC, cerca), r.topLeft, r.size)
        drawRect(a.claro(0.3f), r.topLeft, Size(r.width, 0.8f * u))
        val tubo = Rect(r.left + 4f * u, r.top + r.height * 0.4f, r.right - 4f * u, r.bottom + 1.2f * u)
        if (a.noche) {
            drawRoundRect(UV.copy(alpha = 0.5f), Offset(tubo.left - 2f * u, tubo.top - 1f * u), Size(tubo.width + 4f * u, tubo.height + 2f * u), CornerRadius(tubo.height))
            drawRoundRect(Color(0xFFD7C2FF), tubo.topLeft, tubo.size, CornerRadius(tubo.height / 2))
            drawRect(Color.White.copy(alpha = 0.6f), Offset(tubo.left + 2f * u, tubo.center.y - 0.4f * u), Size(tubo.width - 4f * u, 0.8f * u))
        } else {
            drawRoundRect(a.luz(mezcla(VIOLETA, TINTA, 0.55f), cerca), tubo.topLeft, tubo.size, CornerRadius(tubo.height / 2))
            drawRect(a.claro(0.35f), Offset(tubo.left + 2f * u, tubo.top + 0.4f * u), Size(tubo.width * 0.4f, 0.6f * u))
        }
        listOf(r.left + 1.5f * u, r.right - 3.5f * u).forEach { x -> drawRect(a.luz(TINTA, cerca), Offset(x, r.top + r.height * 0.3f), Size(2f * u, r.height * 0.9f)) }
    }

    // ---------------------------------------------------------------- lo de arriba

    /**
     * La centrífuga de sobremesa con la tapa abierta: el cuerpo de esmalte blanco con su frente inclinado de mandos, la boca
     * con el rotor y los tubos, la tapa levantada sobre sus bisagras con su junta, la pantalla, los pilotos y las rejillas.
     */
    private fun DrawScope.centrifuga(pie: Offset, an: Float, a: Aire, cerca: (Offset) -> Float, haciaX: Float, caida: Offset) {
        val u = density
        val cx = pie.x
        val alto = an * 0.42f
        val x0 = cx - an / 2; val x1 = cx + an / 2
        val yTop = pie.y - alto
        val cara = an * 0.07f                  // lo que se ve de la cara de arriba
        val tapaAl = an * 0.34f
        val bis = yTop - cara                  // la bisagra, en el borde de atrás
        val k = cerca(Offset(cx, pie.y - alto * 0.5f))
        val kt = cerca(Offset(cx, bis - tapaAl * 0.5f))
        val cuerpo = caja(Rect(x0, yTop - cara, x1, pie.y), an * 0.07f)
        // La tapa es una concha abombada: abierta hacia atrás, se le ve la cara de dentro, cóncava.
        fun concha(m: Float, alto0: Float) = Path().apply {
            moveTo(x0 + an * m, bis + 1f * u)
            cubicTo(x0 + an * m, bis - alto0 * 0.75f, x0 + an * (m + 0.12f), bis - alto0, cx, bis - alto0)
            cubicTo(x1 - an * (m + 0.12f), bis - alto0, x1 - an * m, bis - alto0 * 0.75f, x1 - an * m, bis + 1f * u)
            close()
        }
        val tapa = concha(0.07f, tapaAl)
        // Su sombra en la pared y en la tabla.
        translate(caida.x, caida.y) {
            val s = a.sombra(if (a.lumbre) 0.42f else 0.22f)
            drawPath(tapa, s); drawPath(cuerpo, s)
        }
        contacto(Offset(cx + an * 0.06f, pie.y), an * 1.12f, an * 0.06f, a, 0.4f)
        // La tapa abierta: el borde de esmalte y, dentro, la concavidad gris, más honda arriba, con su junta y el cierre.
        val esmalte = mezcla(BLANCO, ACERO, 0.12f)
        drawPath(tapa, a.luz(esmalte, kt))
        val dentro = concha(0.12f, tapaAl * 0.84f)
        drawPath(dentro, a.luz(mezcla(esmalte, ACERO, 0.42f), kt))
        clipPath(dentro) {
            drawOval(a.sombra(0.26f), Offset(x0 + an * 0.1f, bis - tapaAl * 1.05f), Size(an * 0.8f, tapaAl * 0.55f))
            drawRect(a.sombra(0.16f), Offset(x1 - an * 0.3f, bis - tapaAl), Size(an * 0.3f, tapaAl))
            drawOval(a.claro(0.3f), Offset(x0 + an * 0.18f, bis - tapaAl * 0.55f), Size(an * 0.18f, tapaAl * 0.5f))
        }
        drawPath(concha(0.155f, tapaAl * 0.72f), a.luz(TINTA, kt), style = Stroke(1.8f * u))
        drawRoundRect(a.luz(ACERO_OSC, kt), Offset(cx - an * 0.05f, bis - tapaAl * 0.97f), Size(an * 0.1f, tapaAl * 0.1f), CornerRadius(1.5f * u))
        drawPath(borde(tapa, 1.2f * u, 0f), a.claro(0.45f))
        if (a.lumbre) drawPath(borde(tapa, -haciaX * 1.2f * u, -1f * u), a.filo(0.5f * kt))
        // Las bisagras.
        listOf(x0 + an * 0.22f, x1 - an * 0.3f).forEach { x -> drawRoundRect(a.luz(ACERO_OSC, k), Offset(x, bis - 2.5f * u), Size(an * 0.08f, 5f * u), CornerRadius(1.5f * u)) }
        // El cuerpo.
        drawPath(cuerpo, a.luz(esmalte, k))
        clipPath(cuerpo) {
            // La cara de arriba, más clara (la ve la luz del techo), con la boca y el rotor.
            drawRect(a.luz(mezcla(esmalte, LUZ_MUEBLE, 0.4f), k), Offset(x0, yTop - cara), Size(an, cara))
            val boca = Rect(x0 + an * 0.13f, yTop - cara * 0.88f, x1 - an * 0.13f, yTop - cara * 0.06f)
            drawOval(a.luz(mezcla(TINTA, ACERO_OSC, 0.3f), k * 0.6f), boca.topLeft, boca.size)
            val rotor = boca.deflate(boca.height * 0.16f)
            drawOval(a.luz(ACERO, k * 0.8f), rotor.topLeft, rotor.size)
            drawOval(a.claro(0.4f), Offset(rotor.left + rotor.width * 0.1f, rotor.top), Size(rotor.width * 0.5f, rotor.height * 0.4f))
            for (j in 0 until 10) {
                val ang = Math.toRadians(36.0 * j + 10).toFloat()
                val p = Offset(rotor.center.x + rotor.width * 0.4f * cos(ang), rotor.center.y + rotor.height * 0.36f * sin(ang))
                val col = listOf(CIAN, ROSA, AMARILLO, VIOLETA, BLANCO)[j % 5]
                drawOval(a.fluor(col, if (j % 2 == 0) FLUOR else Color(0xFFFF8BE6), k), Offset(p.x - 1.8f * u, p.y - 1.1f * u), Size(3.6f * u, 2.2f * u))
            }
            drawCircle(a.luz(ACERO_OSC, k), rotor.height * 0.22f, rotor.center)
            drawArc(a.sombra(0.4f), 180f, 180f, false, boca.topLeft, boca.size, style = Stroke(1.6f * u))
            // El frente inclinado de mandos, abajo, y el bulto: luz a la izquierda, sombra a la derecha.
            val panel = yTop + alto * 0.42f
            drawRect(a.sombra(0.1f), Offset(x0, panel), Size(an, pie.y - panel))
            drawLine(a.claro(0.5f), Offset(x0, panel), Offset(x1, panel), 0.8f * u)
            drawRect(a.claro(0.3f), Offset(x0 + an * 0.03f, yTop), Size(an * 0.06f, alto))
            drawRect(a.sombra(0.16f), Offset(x1 - an * 0.16f, yTop - cara), Size(an * 0.16f, alto + cara))
            drawRect(a.sombra(0.14f), Offset(x1 - an * 0.06f, yTop - cara), Size(an * 0.06f, alto + cara))
            drawLine(a.sombra(0.3f), Offset(x0, yTop), Offset(x1, yTop), 0.8f * u)
            // Las rejillas de ventilación del costado del frente.
            for (j in 0 until 5) drawLine(a.sombra(0.35f), Offset(x1 - an * 0.24f, yTop + alto * (0.1f + 0.06f * j)), Offset(x1 - an * 0.08f, yTop + alto * (0.1f + 0.06f * j)), 1f * u, StrokeCap.Round)
            // El uso: una mancha amarillenta y una rozadura.
            drawOval(a.luz(mezcla(esmalte, AMARILLO, 0.25f), k).copy(alpha = 0.5f), Offset(x0 + an * 0.32f, yTop + alto * 0.12f), Size(an * 0.14f, alto * 0.1f))
            drawLine(a.sombra(0.2f), Offset(x0 + an * 0.5f, yTop + alto * 0.3f), Offset(x0 + an * 0.62f, yTop + alto * 0.27f), 0.6f * u)
        }
        // La pantalla, los botones, la perilla y los pilotos.
        val pan = Rect(x0 + an * 0.08f, pie.y - alto * 0.47f, x0 + an * 0.4f, pie.y - alto * 0.2f)
        drawRoundRect(a.luz(mezcla(esmalte, TINTA, 0.25f), k), pan.topLeft - Offset(1.4f * u, 1.4f * u), Size(pan.width + 2.8f * u, pan.height + 2.8f * u), CornerRadius(2f * u))
        drawRoundRect(if (a.lumbre) mezcla(TINTA, PANTALLA, 0.14f) else a.luz(mezcla(TINTA, CIAN, 0.18f), k), pan.topLeft, pan.size, CornerRadius(1.2f * u))
        val seg = if (a.lumbre) PANTALLA else a.luz(mezcla(PANTALLA, TINTA, 0.35f), k)
        for (j in 0 until 4) drawRect(seg, Offset(pan.left + pan.width * (0.1f + 0.2f * j), pan.top + pan.height * 0.25f), Size(pan.width * 0.14f, pan.height * 0.14f))
        drawRect(seg.copy(alpha = 0.45f), Offset(pan.left + pan.width * 0.1f, pan.top + pan.height * 0.62f), Size(pan.width * 0.8f, pan.height * 0.12f))
        drawRect(seg, Offset(pan.left + pan.width * 0.1f, pan.top + pan.height * 0.62f), Size(pan.width * 0.5f, pan.height * 0.12f))
        drawLine(a.claro(0.4f), Offset(pan.left + 1f * u, pan.top + 1f * u), Offset(pan.left + pan.width * 0.5f, pan.top + 1f * u), 0.7f * u)
        val perilla = Offset(x0 + an * 0.56f, pie.y - alto * 0.32f)
        drawCircle(a.sombra(0.35f), alto * 0.13f, perilla + Offset(1.2f * u, 1.5f * u))
        drawCircle(a.luz(ACERO_OSC, k), alto * 0.13f, perilla)
        drawCircle(a.luz(ACERO, k), alto * 0.09f, perilla)
        drawLine(a.claro(0.6f), perilla, perilla + Offset(-alto * 0.05f, -alto * 0.07f), 1f * u, StrokeCap.Round)
        listOf(PILOTO, AMARILLO).forEachIndexed { i, c ->
            val p = Offset(x0 + an * (0.7f + 0.07f * i), pie.y - alto * 0.38f)
            drawCircle(a.luz(TINTA, k), 2f * u, p)
            drawCircle(if (a.lumbre) c else a.luz(mezcla(c, TINTA, if (i == 0) 0.1f else 0.55f), k), 1.4f * u, p)
        }
        drawRoundRect(a.luz(mezcla(esmalte, ACERO, 0.4f), k), Offset(x0 + an * 0.68f, pie.y - alto * 0.24f), Size(an * 0.14f, alto * 0.08f), CornerRadius(1.5f * u))
        // Las patas de goma.
        listOf(x0 + an * 0.08f, x1 - an * 0.16f).forEach { x -> drawRect(a.luz(TINTA, k), Offset(x, pie.y - 1.2f * u), Size(an * 0.08f, 2f * u)) }
        if (a.lumbre) drawPath(borde(cuerpo, -haciaX * 1.2f * u, 1.2f * u), a.filo((0.1f + 0.5f * k).coerceAtMost(0.6f)))
    }

    /** Lo que alumbra por sí mismo, de noche: la pantalla y los pilotos de la centrífuga, y la bombilla del flexo. */
    private fun DrawScope.brillos(pie: Offset, an: Float, a: Aire) {
        val w = size.width
        val alto = an * 0.42f
        val pan = Offset(pie.x - an / 2 + an * 0.24f, pie.y - alto * 0.33f)
        mancha(pan, an * 0.32f, PANTALLA.copy(alpha = 0.22f))
        mancha(Offset(pie.x - an / 2 + an * 0.7f, pie.y - alto * 0.38f), an * 0.08f, PILOTO.copy(alpha = 0.5f))
        if (!a.noche) {
            val b = Offset(w * LAMP_X, w * LAMP_Y)
            drawCircle(FLEXO.copy(alpha = 0.35f), w * 0.024f, b)
            drawCircle(Color(0xFFFFF4DC), w * 0.012f, b)
        }
    }

    /**
     * El flexo de brazo articulado, apretado al canto de la tabla alta: la mordaza, los dos brazos de varillas con su muelle,
     * las rótulas y la pantalla cónica que mira hacia abajo, a las repisas. De noche (fuera de los Sueños), encendido.
     */
    private fun DrawScope.lampara(mordaza: Offset, a: Aire, caida: Offset) {
        val u = density; val w = size.width
        val pintura = Color(0xFF3E5A5E)
        val bombilla = Offset(w * LAMP_X, w * LAMP_Y)
        val ang = Math.toRadians(58.0)
        val dir = Offset(cos(ang).toFloat(), sin(ang).toFloat())
        val nrm = Offset(-dir.y, dir.x)
        val cabeza = bombilla - dir * (w * 0.05f)
        val codo = Offset(w * 0.042f, mordaza.y - w * 0.27f)
        val base = Offset(mordaza.x, mordaza.y - w * 0.01f)
        val encendido = a.oscuro && !a.noche
        val k = if (encendido) 0.9f else 0.6f
        fun brazo(p0: Offset, p1: Offset) {
            val l = (p1 - p0); val nn = Offset(-l.y, l.x) / l.getDistance().coerceAtLeast(1f) * (1.6f * u)
            drawLine(a.sombra(0.25f), p0 + caida, p1 + caida, 2.2f * u)
            drawLine(a.luz(pintura, k), p0 + nn, p1 + nn, 1.4f * u, StrokeCap.Round)
            drawLine(a.luz(pintura, k), p0 - nn, p1 - nn, 1.4f * u, StrokeCap.Round)
            drawLine(a.claro(0.4f), p0 + nn - Offset(0.4f * u, 0f), p1 + nn - Offset(0.4f * u, 0f), 0.5f * u)
            if (encendido) drawLine(a.filo(0.5f), p0 - nn + Offset(0.5f * u, 0f), p1 - nn + Offset(0.5f * u, 0f), 0.5f * u)
        }
        brazo(base, codo)
        brazo(codo, cabeza)
        // El muelle del brazo de abajo: una espiral apretada a su lado.
        val m0 = base + (codo - base) * 0.2f; val m1 = base + (codo - base) * 0.75f
        val mm = Offset(-(codo - base).y, (codo - base).x) / (codo - base).getDistance().coerceAtLeast(1f) * (3.6f * u)
        for (j in 0..10) {
            val p = m0 + (m1 - m0) * (j / 10f) + mm
            drawLine(a.luz(ACERO, k), p - Offset(1.6f * u, 0.8f * u), p + Offset(1.6f * u, 0.8f * u), 0.7f * u)
        }
        // Las rótulas.
        listOf(codo, cabeza).forEach { p ->
            drawCircle(a.luz(TINTA, k), 3f * u, p)
            drawCircle(a.luz(ACERO, k), 1.5f * u, p)
        }
        // La pantalla cónica: estrecha atrás, abierta en la boca.
        val r0 = w * 0.011f; val r1 = w * 0.034f
        val boca = bombilla + dir * (w * 0.006f)
        val pantalla = poligono((cabeza + nrm * r0).x, (cabeza + nrm * r0).y, (boca + nrm * r1).x, (boca + nrm * r1).y, (boca - nrm * r1).x, (boca - nrm * r1).y, (cabeza - nrm * r0).x, (cabeza - nrm * r0).y)
        drawPath(corrido(pantalla, caida.x, caida.y), a.sombra(if (a.lumbre) 0.35f else 0.22f))
        drawPath(pantalla, a.luz(pintura, k))
        clipPath(pantalla) {
            // Luz del techo en el lomo de arriba a la izquierda, sombra en el otro.
            drawPath(corrido(pantalla, nrm.x * r1 * 0.9f, nrm.y * r1 * 0.9f), a.sombra(0.3f))
            drawPath(corrido(pantalla, -nrm.x * r1 * 1.4f, -nrm.y * r1 * 1.4f), a.claro(0.25f))
        }
        drawCircle(a.luz(pintura, k), r0 * 1.2f, cabeza)
        // La boca: el aro y, dentro, la bombilla (encendida o no).
        rotate(Math.toDegrees(ang).toFloat() - 90f, boca) {
            drawOval(a.luz(mezcla(pintura, TINTA, 0.4f), k), Offset(boca.x - r1, boca.y - r1 * 0.32f), Size(2 * r1, r1 * 0.64f))
            drawOval(if (encendido) Color(0xFFFFE9C2) else a.luz(mezcla(BLANCO, AMARILLO, 0.2f), k), Offset(boca.x - r1 * 0.8f, boca.y - r1 * 0.24f), Size(1.6f * r1, r1 * 0.48f))
        }
        // La mordaza, apretada al canto de la tabla, con su tornillo.
        val mz = Rect(mordaza.x - w * 0.014f, mordaza.y - w * 0.012f, mordaza.x + w * 0.014f, mordaza.y + w * 0.032f)
        drawRect(a.sombra(0.35f), mz.topLeft + Offset(1.5f * u, 1.5f * u), mz.size)
        drawRect(a.luz(pintura, k), mz.topLeft, mz.size)
        drawRect(a.claro(0.3f), mz.topLeft, Size(mz.width * 0.25f, mz.height))
        drawRect(a.sombra(0.25f), Offset(mz.right - mz.width * 0.3f, mz.top), Size(mz.width * 0.3f, mz.height))
        drawRect(a.luz(ACERO, k), Offset(mordaza.x - 1.2f * u, mz.bottom - w * 0.012f), Size(2.4f * u, w * 0.012f))
        drawRect(a.luz(ACERO_OSC, k), Offset(mordaza.x - 4f * u, mz.bottom - 1.6f * u), Size(8f * u, 1.6f * u))
        drawRect(a.luz(pintura, k), Offset(base.x - 2.5f * u, base.y - 3f * u), Size(5f * u, mordaza.y - base.y + 3f * u))
    }

    /** La caja de guantes de nitrilo, con la boca de cartón troquelado y un guante lila que asoma y cae por el lado. */
    private fun DrawScope.cajaGuantes(pie: Offset, an: Float, alto: Float, a: Aire, cerca: Float, caida: Offset) {
        val u = density
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        val tapa = an * 0.12f
        val cuerpo = poligono(r.left, r.top, r.left + an * 0.05f, r.top - tapa, r.right + an * 0.05f, r.top - tapa, r.right + an * 0.05f, r.bottom - tapa, r.right, r.bottom, r.left, r.bottom)
        translate(caida.x, caida.y) { drawPath(cuerpo, a.sombra(if (a.lumbre) 0.4f else 0.2f)) }
        contacto(Offset(pie.x + an * 0.06f, pie.y), an * 1.1f, an * 0.07f, a)
        drawRect(a.luz(BLANCO, cerca), r.topLeft, r.size)
        // La cara de arriba y el costado derecho, vistos apenas.
        drawPath(poligono(r.left, r.top, r.left + an * 0.05f, r.top - tapa, r.right + an * 0.05f, r.top - tapa, r.right, r.top), a.luz(mezcla(BLANCO, LUZ_MUEBLE, 0.5f), cerca))
        drawPath(poligono(r.right, r.top, r.right + an * 0.05f, r.top - tapa, r.right + an * 0.05f, r.bottom - tapa, r.right, r.bottom), a.luz(mezcla(BLANCO, SOMBRA_MUEBLE, 0.3f), cerca))
        // El diseño impreso: una franja lila y un bloque cian, sin letras.
        drawRect(a.luz(mezcla(VIOLETA, BLANCO, 0.3f), cerca), Offset(r.left, r.bottom - alto * 0.32f), Size(an, alto * 0.14f))
        drawRect(a.luz(CIAN, cerca), Offset(r.left + an * 0.1f, r.top + alto * 0.18f), Size(an * 0.26f, alto * 0.22f))
        for (j in 0 until 2) drawRect(a.luz(TINTA, cerca).copy(alpha = 0.5f), Offset(r.left + an * 0.45f, r.top + alto * (0.2f + 0.12f * j)), Size(an * (0.4f - 0.14f * j), alto * 0.05f))
        drawRect(a.sombra(0.12f), Offset(r.right - an * 0.18f, r.top), Size(an * 0.18f, alto))
        // La boca troquelada de arriba, y el guante: el puño y los dedos blandos que caen por el lado.
        drawOval(a.luz(mezcla(TINTA, VIOLETA, 0.2f), cerca * 0.6f), Offset(r.left + an * 0.22f, r.top - tapa * 0.75f), Size(an * 0.62f, tapa * 0.55f))
        val lila = a.luz(Color(0xFFA899E2), cerca)
        val lilaOsc = a.luz(Color(0xFF7C6CB8), cerca)
        // El guante: la palma que sale de la boca, el pulgar hacia atrás y cuatro dedos blandos que caen por el lado.
        val palma = Offset(r.left + an * 0.6f, r.top - tapa * 2.0f)
        val dedo = an * 0.085f
        val punta = listOf(Offset(an * 0.2f, -an * 0.12f), Offset(an * 0.3f, -an * 0.02f), Offset(an * 0.34f, an * 0.12f), Offset(an * 0.3f, an * 0.25f))
        val raiz = listOf(Offset(an * 0.04f, -an * 0.1f), Offset(an * 0.09f, -an * 0.05f), Offset(an * 0.11f, an * 0.01f), Offset(an * 0.1f, an * 0.07f))
        for (j in 0 until 4) drawLine(a.sombra(0.25f), palma + raiz[j] + Offset(1.5f * u, 2f * u), palma + punta[j] + Offset(1.5f * u, 2f * u), dedo, StrokeCap.Round)
        drawLine(a.sombra(0.25f), palma + Offset(-an * 0.06f, 0f), palma + Offset(-an * 0.22f, -an * 0.14f), dedo, StrokeCap.Round)
        drawLine(lilaOsc, palma + Offset(-an * 0.06f, 0f), palma + Offset(-an * 0.22f, -an * 0.14f), dedo, StrokeCap.Round)
        drawPath(poligono(r.left + an * 0.36f, r.top - tapa * 0.5f, r.left + an * 0.74f, r.top - tapa * 0.5f, palma.x + an * 0.13f, palma.y + an * 0.04f, palma.x - an * 0.08f, palma.y - an * 0.1f, palma.x - an * 0.16f, palma.y + an * 0.02f), lila)
        drawCircle(lila, an * 0.11f, palma)
        for (j in 0 until 4) {
            drawLine(if (j % 2 == 0) lila else mezcla(lila, lilaOsc, 0.4f), palma + raiz[j], palma + punta[j], dedo, StrokeCap.Round)
            drawLine(a.claro(0.35f), palma + raiz[j] + Offset(0f, -dedo * 0.25f), palma + punta[j] + Offset(-dedo * 0.1f, -dedo * 0.3f), dedo * 0.22f, StrokeCap.Round)
        }
        // El puño enrollado en la boca.
        drawOval(lilaOsc, Offset(r.left + an * 0.34f, r.top - tapa * 0.95f), Size(an * 0.42f, tapa * 0.6f))
        drawOval(lila, Offset(r.left + an * 0.36f, r.top - tapa * 1.05f), Size(an * 0.38f, tapa * 0.45f))
    }

    /** El rollo de cinta de etiquetas, tumbado de cara, con su cola que corre por la tabla y cuelga por el canto con una etiqueta a medio despegar. */
    private fun DrawScope.cintaEtiquetas(c: Offset, r: Float, apoyo: Float, ch: Float, a: Aire, cerca: Float) {
        val u = density
        val cola = Path().apply {
            moveTo(c.x + r * 0.2f, c.y + r)
            lineTo(c.x + r * 1.5f, apoyo + r * 0.1f)
            cubicTo(c.x + r * 1.9f, apoyo + r * 0.1f, c.x + r * 2.1f, apoyo + r * 0.2f, c.x + r * 2.1f, apoyo + r * 0.6f)
            lineTo(c.x + r * 2.0f, ch - 2f * u)
        }
        drawPath(corrido(cola, 1.5f * u, 1.5f * u), a.sombra(0.25f), style = Stroke(r * 0.32f))
        drawPath(cola, a.luz(BLANCO, cerca), style = Stroke(r * 0.32f))
        // La etiqueta pegada en la cola, que cuelga del canto.
        val e = Rect(c.x + r * 1.86f, ch - r * 1.05f, c.x + r * 2.16f, ch - r * 0.55f)
        drawRect(a.luz(CIAN, cerca), e.topLeft, e.size)
        contacto(Offset(c.x + r * 0.2f, apoyo), r * 2.2f, r * 0.3f, a)
        drawCircle(a.sombra(0.3f), r, c + Offset(r * 0.18f, r * 0.12f))
        drawCircle(a.luz(BLANCO, cerca), r, c)
        for (j in 1..3) drawCircle(a.luz(mezcla(BLANCO, PVC, 0.4f), cerca), r * (1f - 0.12f * j), c, style = Stroke(0.5f * u))
        drawCircle(a.luz(mezcla(KRAFT, BLANCO, 0.3f), cerca), r * 0.5f, c)
        drawCircle(a.luz(mezcla(TINTA, ACERO_OSC, 0.3f), cerca * 0.7f), r * 0.36f, c)
        drawArc(a.claro(0.5f), 190f, 80f, false, Offset(c.x - r * 0.85f, c.y - r * 0.85f), Size(r * 1.7f, r * 1.7f), style = Stroke(1f * u))
        drawArc(a.sombra(0.25f), 10f, 120f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.2f * u))
    }

    /** La taza con la bolsita de té, el hilo por el borde y la etiqueta colgando; un cerco de té en la tabla. */
    private fun DrawScope.taza(pie: Offset, an: Float, a: Aire, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val alto = an * 1.05f
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        val cuerpo = Path().apply {
            moveTo(r.left, r.top)
            lineTo(r.right, r.top)
            lineTo(r.right - an * 0.04f, r.bottom - an * 0.1f)
            cubicTo(r.right - an * 0.05f, r.bottom, r.right - an * 0.1f, r.bottom, r.right - an * 0.16f, r.bottom)
            lineTo(r.left + an * 0.16f, r.bottom)
            cubicTo(r.left + an * 0.1f, r.bottom, r.left + an * 0.05f, r.bottom, r.left + an * 0.04f, r.bottom - an * 0.1f)
            close()
        }
        val color = a.luz(mezcla(CIAN, BLANCO, 0.45f), cerca)
        translate(caida.x, caida.y) { drawPath(cuerpo, a.sombra(if (a.lumbre) 0.4f else 0.2f)) }
        drawOval(a.luz(mezcla(KRAFT, TINTA, 0.3f), cerca).copy(alpha = 0.4f), Offset(r.left - an * 0.55f, pie.y - an * 0.08f), Size(an * 0.6f, an * 0.12f), style = Stroke(0.8f * u))
        contacto(Offset(pie.x + an * 0.08f, pie.y), an * 1.2f, an * 0.14f, a)
        // El asa, a la derecha.
        drawArc(color, -80f, 160f, false, Offset(r.right - an * 0.22f, r.top + alto * 0.18f), Size(an * 0.48f, alto * 0.5f), style = Stroke(an * 0.11f, cap = StrokeCap.Round))
        drawArc(a.sombra(0.25f), -40f, 120f, false, Offset(r.right - an * 0.22f, r.top + alto * 0.18f), Size(an * 0.48f, alto * 0.5f), style = Stroke(an * 0.05f))
        drawPath(cuerpo, color)
        redondo(cuerpo, a, cerca, haciaX)
        // La boca, con el té.
        drawOval(a.luz(mezcla(CIAN, TINTA, 0.25f), cerca), Offset(r.left, r.top - an * 0.1f), Size(an, an * 0.2f))
        drawOval(a.luz(Color(0xFF8A5A2B), cerca), Offset(r.left + an * 0.07f, r.top - an * 0.06f), Size(an * 0.86f, an * 0.13f))
        // El hilo de la bolsita y su etiqueta, por fuera, a la izquierda.
        val et = Offset(r.left - an * 0.06f, r.top + alto * 0.42f)
        drawLine(a.luz(BLANCO, cerca), Offset(r.left + an * 0.3f, r.top - an * 0.02f), Offset(r.left + an * 0.02f, r.top), 0.6f * u)
        drawLine(a.luz(BLANCO, cerca), Offset(r.left + an * 0.02f, r.top), et, 0.6f * u)
        drawRect(a.luz(AMARILLO, cerca), Offset(et.x - an * 0.1f, et.y), Size(an * 0.2f, an * 0.22f))
        drawRect(a.sombra(0.2f), Offset(et.x - an * 0.1f, et.y + an * 0.18f), Size(an * 0.2f, an * 0.04f))
        // Un desconchón en el esmalte del borde.
        drawOval(a.luz(BLANCO, cerca), Offset(r.left + an * 0.62f, r.top), Size(an * 0.1f, an * 0.06f))
    }

    /** Un potos en un vaso de precipitados con agua, las raíces dentro, tallos que suben y otros que caen por el canto. */
    private fun DrawScope.planta(pie: Offset, an: Float, ch: Float, a: Aire, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val alto = an * 1.18f
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        val vaso = poligono(r.left - an * 0.08f, r.top, r.left, r.top + an * 0.06f, r.left, r.bottom - 1f * u, r.right, r.bottom - 1f * u, r.right, r.top)
        // Las hojas: (tallo desde la boca, punto final, tamaño), las de atrás primero.
        val boca = Offset(pie.x, r.top + an * 0.04f)
        class Hoja(val p: Offset, val s: Float, val giro: Float, val tono: Float)
        val tallos = listOf(
            Offset(-an * 0.9f, -an * 1.15f), Offset(-an * 0.1f, -an * 1.55f), Offset(an * 0.75f, -an * 1.0f),
            Offset(an * 0.95f, an * 0.5f), Offset(-an * 0.7f, ch - boca.y - 2f * u),
        )
        val hojas = mutableListOf<Hoja>()
        tallos.forEachIndexed { i, t ->
            val nh = 4
            for (j in 1..nh) {
                val f = j / nh.toFloat()
                val p = boca + Offset(t.x * f, t.y * f + (if (t.y < 0) -an * 0.25f * sin(3.14f * f) else an * 0.2f * sin(3.14f * f)))
                hojas.add(Hoja(p, an * (0.27f + 0.1f * azar(970 + i, j)), (if (t.x < 0) -1f else 1f) * (30f + 40f * azar(971 + i, j)) + (if (t.y > 0) 90f else 0f), azar(972 + i, j)))
            }
        }
        fun hojaPath(h: Hoja) = Path().apply {
            val s = h.s
            moveTo(h.p.x, h.p.y)
            cubicTo(h.p.x - s * 0.7f, h.p.y - s * 0.2f, h.p.x - s * 0.6f, h.p.y - s * 1.0f, h.p.x, h.p.y - s * 1.3f)
            cubicTo(h.p.x + s * 0.6f, h.p.y - s * 1.0f, h.p.x + s * 0.7f, h.p.y - s * 0.2f, h.p.x, h.p.y)
            close()
        }
        // La sombra de todo en la pared.
        translate(caida.x, caida.y) {
            hojas.forEach { hh -> rotate(hh.giro, hh.p) { drawPath(hojaPath(hh), a.sombra(if (a.lumbre) 0.34f else 0.16f)) } }
            drawPath(vaso, a.sombra(if (a.lumbre) 0.3f else 0.14f))
        }
        contacto(Offset(pie.x + an * 0.06f, pie.y), an * 1.15f, an * 0.1f, a)
        // Los tallos.
        tallos.forEach { t ->
            val tallo = Path().apply {
                moveTo(boca.x, boca.y)
                cubicTo(boca.x + t.x * 0.3f, boca.y + (if (t.y < 0) t.y * 0.6f else -an * 0.2f), boca.x + t.x * 0.7f, boca.y + t.y * 0.8f, boca.x + t.x, boca.y + t.y)
            }
            drawPath(tallo, a.luz(mezcla(VERDE, AMARILLO, 0.3f), cerca), style = Stroke(1f * u, cap = StrokeCap.Round))
        }
        hojas.forEach { hh ->
            rotate(hh.giro, hh.p) {
                val p = hojaPath(hh)
                val c = mezcla(VERDE, if (hh.tono > 0.6f) AMARILLO else Color(0xFF2F5E3A), 0.12f + 0.25f * hh.tono)
                drawPath(p, a.luz(c, cerca))
                clipPath(p) { drawRect(a.claro(0.25f), Offset(hh.p.x - hh.s, hh.p.y - hh.s * 1.4f), Size(hh.s * 0.9f, hh.s * 1.4f)) }
                drawLine(a.luz(mezcla(VERDE, AMARILLO, 0.5f), cerca), hh.p, Offset(hh.p.x, hh.p.y - hh.s * 1.1f), 0.5f * u)
            }
        }
        // El vaso: el agua con las raíces, las marcas de graduación, el pico y los brillos.
        val agua = r.top + alto * 0.3f
        clipPath(vaso) {
            drawRect(a.luz(mezcla(CIAN, BLANCO, 0.55f), cerca).copy(alpha = 0.45f), Offset(r.left, agua), Size(an, r.bottom - agua))
            for (j in 0 until 6) {
                val x = pie.x + an * (azar(980, j) - 0.5f) * 0.6f
                drawLine(a.luz(mezcla(KRAFT, BLANCO, 0.4f), cerca), Offset(pie.x, r.top), Offset(x, r.bottom - an * 0.1f * azar(981, j)), 0.6f * u)
            }
            drawOval(a.luz(mezcla(CIAN, BLANCO, 0.7f), cerca).copy(alpha = 0.6f), Offset(r.left, agua - 1f * u), Size(an, 2f * u))
        }
        drawPath(vaso, a.luz(mezcla(BLANCO, CIAN, 0.2f), cerca).copy(alpha = 0.22f))
        drawPath(vaso, a.luz(ACERO_OSC, cerca).copy(alpha = 0.55f), style = Stroke(0.9f * u))
        for (j in 0 until 4) drawLine(a.luz(BLANCO, cerca).copy(alpha = 0.8f), Offset(r.left + an * 0.6f, r.bottom - alto * (0.2f + 0.17f * j)), Offset(r.left + an * (if (j % 2 == 0) 0.85f else 0.75f), r.bottom - alto * (0.2f + 0.17f * j)), 0.7f * u)
        drawLine(a.claro(0.7f), Offset(r.left + an * 0.15f, r.top + alto * 0.12f), Offset(r.left + an * 0.15f, r.bottom - alto * 0.1f), 1.2f * u, StrokeCap.Round)
        if (a.lumbre) drawLine(a.filo(0.5f * cerca), Offset(if (haciaX < 0) r.left + 1f * u else r.right - 1f * u, r.top + 2f * u), Offset(if (haciaX < 0) r.left + 1f * u else r.right - 1f * u, r.bottom - 2f * u), 0.8f * u)
    }

    /** Dos tubos cónicos sueltos sobre la tabla, uno tumbado, con su tapón de color. */
    private fun DrawScope.tubitos(pie: Offset, largo: Float, a: Aire, cerca: Float) {
        val u = density
        listOf(0 to ROSA, 1 to CIAN).forEach { (i, col) ->
            val p0 = pie + Offset(largo * 0.6f * i, -1.6f * u - i * 0.4f * u)
            rotate(if (i == 0) -6f else 4f, p0) {
                val tubo = poligono(p0.x, p0.y - 2f * u, p0.x + largo * 0.75f, p0.y - 2f * u, p0.x + largo, p0.y, p0.x + largo * 0.75f, p0.y + 2f * u, p0.x, p0.y + 2f * u)
                drawPath(corrido(tubo, 1f * u, 1.8f * u), a.sombra(0.3f))
                drawPath(tubo, a.luz(mezcla(BLANCO, CIAN, 0.15f), cerca).copy(alpha = 0.75f))
                drawRect(a.fluor(col, if (i == 0) Color(0xFFFF8BE6) else FLUOR, cerca), Offset(p0.x - 3f * u, p0.y - 2.6f * u), Size(3.4f * u, 5.2f * u))
                drawLine(a.claro(0.6f), Offset(p0.x + 1f * u, p0.y - 1.2f * u), Offset(p0.x + largo * 0.6f, p0.y - 1.2f * u), 0.6f * u)
            }
        }
    }

    /**
     * La bata blanca colgada de su gancho, cortada por el borde derecho: los hombros, el cuello y la solapa, la manga que cae,
     * el bolsillo con un bolígrafo, los pliegues con su peso y el bajo arrugado.
     */
    private fun DrawScope.bata(gancho: Offset, w: Float, bajo: Float, a: Aire, cerca: Float, caida: Offset) {
        val u = density
        val hom = gancho.y + w * 0.06f
        val izq = w * 0.862f
        val cuerpo = Path().apply {
            moveTo(gancho.x - w * 0.012f, gancho.y + w * 0.012f)
            cubicTo(gancho.x - w * 0.03f, gancho.y + w * 0.03f, izq + w * 0.01f, hom - w * 0.01f, izq, hom + w * 0.01f)
            cubicTo(izq - w * 0.012f, hom + w * 0.1f, izq - w * 0.006f, bajo - w * 0.12f, izq - w * 0.016f, bajo - w * 0.006f)
            cubicTo(izq + w * 0.03f, bajo + w * 0.004f, izq + w * 0.06f, bajo - w * 0.012f, izq + w * 0.09f, bajo)
            cubicTo(w * 1.0f, bajo + w * 0.008f, w * 1.02f, bajo - w * 0.004f, w * 1.04f, bajo)
            lineTo(w * 1.04f, hom)
            cubicTo(w * 1.0f, gancho.y + w * 0.02f, gancho.x + w * 0.03f, gancho.y + w * 0.02f, gancho.x + w * 0.012f, gancho.y + w * 0.012f)
            close()
        }
        translate(caida.x, caida.y) { drawPath(cuerpo, a.sombra(if (a.lumbre) 0.42f else 0.22f)) }
        // El gancho de la pared.
        drawCircle(a.luz(ACERO_OSC, cerca), 2.6f * u, gancho)
        drawArc(a.luz(ACERO, cerca), 0f, 180f, false, Offset(gancho.x - 3f * u, gancho.y - 1f * u), Size(6f * u, 8f * u), style = Stroke(1.6f * u))
        val tela = a.luz(BLANCO, cerca)
        drawPath(cuerpo, tela)
        clipPath(cuerpo) {
            // Los pliegues con peso: bandas de sombra que bajan desde los hombros y se abren hacia el bajo.
            listOf(0.875f to 0.04f, 0.905f to 0.03f, 0.955f to 0.05f, 0.99f to 0.03f).forEachIndexed { i, (x, an) ->
                val pl = poligono(w * x, hom + w * 0.03f, w * (x + an * 0.4f), hom + w * 0.03f, w * (x + an * 1.1f), bajo + w * 0.01f, w * (x - an * 0.2f), bajo + w * 0.01f)
                drawPath(pl, a.sombra(0.08f + 0.04f * (i % 2)))
                drawLine(a.claro(0.4f), Offset(w * x - 1f * u, hom + w * 0.05f), Offset(w * (x - an * 0.2f) - 1f * u, bajo), 0.8f * u)
            }
            // La sombra del lado derecho y del pecho, bajo el cuello.
            drawRect(a.sombra(0.14f), Offset(w * 0.975f, gancho.y), Size(w * 0.07f, bajo))
            drawOval(a.sombra(0.12f), Offset(gancho.x - w * 0.03f, gancho.y + w * 0.02f), Size(w * 0.07f, w * 0.05f))
        }
        // La solapa y la abertura del frente.
        val solapa = poligono(gancho.x - w * 0.012f, gancho.y + w * 0.012f, gancho.x - w * 0.026f, hom + w * 0.03f, gancho.x - w * 0.006f, hom + w * 0.1f, gancho.x + w * 0.004f, gancho.y + w * 0.03f)
        drawPath(corrido(solapa, 1.5f * u, 1.5f * u), a.sombra(0.2f))
        drawPath(solapa, a.luz(mezcla(BLANCO, LUZ_MUEBLE, 0.4f), cerca))
        drawLine(a.sombra(0.3f), Offset(gancho.x - w * 0.006f, hom + w * 0.1f), Offset(gancho.x - w * 0.008f, bajo), 0.9f * u)
        for (j in 0 until 3) {
            val p = Offset(gancho.x - w * 0.016f, hom + w * (0.13f + 0.08f * j))
            drawCircle(a.sombra(0.3f), 1.6f * u, p + Offset(0.5f * u, 0.6f * u))
            drawCircle(a.luz(mezcla(BLANCO, PVC, 0.4f), cerca), 1.4f * u, p)
        }
        // La manga izquierda, que cuelga a lo largo, con su puño doblado.
        val manga = Path().apply {
            moveTo(izq + w * 0.002f, hom + w * 0.008f)
            cubicTo(izq - w * 0.012f, hom + w * 0.08f, izq - w * 0.014f, hom + w * 0.16f, izq - w * 0.006f, hom + w * 0.22f)
            lineTo(izq + w * 0.024f, hom + w * 0.225f)
            cubicTo(izq + w * 0.026f, hom + w * 0.15f, izq + w * 0.024f, hom + w * 0.07f, izq + w * 0.02f, hom + w * 0.01f)
            close()
        }
        drawPath(corrido(manga, 2f * u, 1.5f * u), a.sombra(0.22f))
        drawPath(manga, tela)
        drawPath(borde(manga, -1.2f * u, 0f), a.sombra(0.2f))
        drawRect(a.luz(mezcla(BLANCO, PVC, 0.25f), cerca), Offset(izq - w * 0.008f, hom + w * 0.2f), Size(w * 0.032f, w * 0.02f))
        // El bolsillo del pecho con un bolígrafo cian, y el de abajo con unas hojas dobladas.
        val bol = Rect(gancho.x + w * 0.01f, hom + w * 0.05f, gancho.x + w * 0.045f, hom + w * 0.09f)
        drawRect(a.luz(CIAN, cerca), Offset(bol.left + bol.width * 0.3f, bol.top - w * 0.018f), Size(1.6f * u, w * 0.024f))
        drawRect(a.luz(ACERO, cerca), Offset(bol.left + bol.width * 0.3f + 1.6f * u, bol.top - w * 0.012f), Size(0.8f * u, w * 0.014f))
        drawRect(tela, bol.topLeft, bol.size)
        drawLine(a.sombra(0.3f), bol.topLeft, Offset(bol.right, bol.top), 0.8f * u)
        drawRect(a.sombra(0.1f), Offset(bol.left, bol.bottom - 1.2f * u), Size(bol.width, 1.2f * u))
        val bo2 = Rect(izq + w * 0.012f, bajo - w * 0.1f, izq + w * 0.06f, bajo - w * 0.05f)
        drawRect(a.luz(mezcla(AMARILLO, BLANCO, 0.4f), cerca), Offset(bo2.left + 2f * u, bo2.top - 3f * u), Size(bo2.width * 0.6f, 6f * u))
        drawRect(tela, bo2.topLeft, bo2.size)
        drawLine(a.sombra(0.3f), bo2.topLeft, Offset(bo2.right, bo2.top), 0.8f * u)
        // El cuello sobre el gancho y una mancha vieja de tinta en el bajo.
        drawOval(a.luz(mezcla(BLANCO, PVC, 0.2f), cerca), Offset(gancho.x - w * 0.016f, gancho.y + w * 0.002f), Size(w * 0.032f, w * 0.018f))
        drawOval(a.luz(VIOLETA, cerca).copy(alpha = 0.35f), Offset(izq + w * 0.07f, bajo - w * 0.03f), Size(w * 0.012f, w * 0.008f))
        if (a.lumbre) drawPath(borde(cuerpo, 1.2f * u, 0.6f * u), a.filo(0.45f * cerca))
    }

    // ---------------------------------------------------------------- al pie

    /** La encimera de resina negra con el teclado y la tableta, el mueble de cajones con uno entreabierto, el vinilo y el taburete. */
    private fun DrawScope.pie(d: DisposicionEstante, a: Aire, yMesa: Float, cerca: (Offset) -> Float) {
        val u = density; val w = size.width; val h = size.height
        val cara = w * 0.022f; val canto = w * 0.02f
        val yCanto = yMesa + cara
        val ySuelo = h - w * 0.036f
        val kM = maxOf(cerca(Offset(w * 0.3f, yMesa)), 0.05f)
        // La tableta, apoyada en el azulejo, y su sombra.
        val tab = Rect(w * 0.6f, yMesa - w * 0.052f, w * 0.73f, yMesa + cara * 0.4f)
        rotate(4f, Offset(tab.center.x, tab.bottom)) {
            drawRect(a.sombra(0.3f), tab.topLeft + Offset(3f * u, 1f * u), tab.size)
            drawRoundRect(a.luz(TINTA, kM), tab.topLeft, tab.size, CornerRadius(2f * u))
            val pantalla = tab.deflate(2.2f * u)
            drawRect(a.luz(mezcla(TINTA, CIAN, 0.18f), kM), pantalla.topLeft, pantalla.size)
            drawPath(poligono(pantalla.left, pantalla.top + pantalla.height * 0.2f, pantalla.left + pantalla.width * 0.35f, pantalla.top, pantalla.left + pantalla.width * 0.55f, pantalla.top, pantalla.left, pantalla.top + pantalla.height * 0.6f), a.claro(0.18f))
        }
        // La encimera: la cara de arriba (reflejos de los tubos) y el canto.
        drawRect(a.luz(mezcla(RESINA, PVC, 0.12f), kM), Offset(0f, yMesa), Size(w, cara))
        pinceladas(Rect(0f, yMesa, w, yCanto), a.claro(1f), 991, 10, u, 0.06f)
        drawRect(a.claro(0.18f), Offset(w * 0.05f, yMesa + cara * 0.3f), Size(w * 0.3f, 1f * u))
        drawRect(a.sombra(0.3f), Offset(0f, yMesa), Size(w, 1.2f * u))
        drawRect(a.luz(RESINA, kM * 0.8f), Offset(0f, yCanto), Size(w, canto))
        drawRect(if (a.lumbre) a.filo(0.3f * kM + 0.05f) else a.claro(0.35f), Offset(0f, yCanto), Size(w, 1f * u))
        drawRect(a.sombra(0.4f), Offset(0f, yCanto + canto), Size(w, 3f * u))
        // El teclado, plano sobre la encimera, con sus filas de teclas, y un bolígrafo.
        val tec = Rect(w * 0.17f, yMesa + cara * 0.12f, w * 0.45f, yMesa + cara * 0.85f)
        drawRect(a.sombra(0.35f), tec.topLeft + Offset(2f * u, 1.5f * u), tec.size)
        drawRoundRect(a.luz(mezcla(ACERO, BLANCO, 0.4f), kM), tec.topLeft, tec.size, CornerRadius(1.5f * u))
        for (f in 0 until 3) {
            val y = tec.top + tec.height * (0.16f + 0.27f * f)
            var x = tec.left + 2f * u + f * 1.2f * u
            val paso = maxOf(1f, tec.width / 15f)
            while (x < tec.right - paso) {
                drawRect(a.luz(mezcla(TINTA, ACERO_OSC, 0.4f), kM), Offset(x, y), Size(paso - 1f * u, tec.height * 0.18f))
                x += paso
            }
        }
        drawLine(a.luz(CIAN, kM), Offset(w * 0.48f, yMesa + cara * 0.6f), Offset(w * 0.55f, yMesa + cara * 0.4f), 1.4f * u, StrokeCap.Round)
        // El mueble de cajones.
        val mu = Rect(w * 0.025f, yCanto + canto, w * 0.975f, ySuelo - w * 0.012f)
        drawRect(a.luz(mezcla(BLANCO, PVC, 0.3f), kM * 0.6f), mu.topLeft, mu.size)
        val cols = 3
        for (c in 0 until cols) {
            val x0 = mu.left + mu.width * c / cols + 2f * u
            val x1 = mu.left + mu.width * (c + 1) / cols - 2f * u
            val k = maxOf(cerca(Offset((x0 + x1) / 2, mu.center.y)), 0.02f)
            val cajon = Rect(x0, mu.top + 3f * u, x1, mu.top + mu.height * 0.32f)
            val puerta = Rect(x0, cajon.bottom + 3f * u, x1, mu.bottom - 2f * u)
            if (c == 1) {
                // El cajón entreabierto: el hueco oscuro de arriba con las carpetas que asoman, y el frente adelantado.
                drawRect(a.luz(TINTA, k * 0.4f), Offset(x0, cajon.top - 1f * u), Size(cajon.width, 5f * u))
                listOf(CIAN, AMARILLO, ROSA).forEachIndexed { j, col -> drawRect(a.luz(col, k * 0.7f), Offset(x0 + cajon.width * (0.2f + 0.22f * j), cajon.top - 0.5f * u), Size(cajon.width * 0.14f, 3f * u)) }
                val fr = Rect(x0 - 1.5f * u, cajon.top + 4f * u, x1 + 1.5f * u, cajon.bottom + 3f * u)
                drawRect(a.sombra(0.4f), Offset(fr.left + 2f * u, fr.bottom), Size(fr.width, 3f * u))
                drawRect(a.luz(MELAMINA, k), fr.topLeft, fr.size)
                drawRect(a.claro(0.4f), fr.topLeft, Size(fr.width, 1f * u))
                drawRoundRect(a.luz(ACERO_OSC, k), Offset(fr.center.x - 9f * u, fr.center.y - 1f * u), Size(18f * u, 2.4f * u), CornerRadius(1.2f * u))
            } else {
                drawRect(a.luz(MELAMINA, k), cajon.topLeft, cajon.size)
                drawRect(a.sombra(0.2f), Offset(cajon.left, cajon.bottom - 1f * u), Size(cajon.width, 1f * u))
                drawRoundRect(a.luz(ACERO_OSC, k), Offset(cajon.center.x - 9f * u, cajon.center.y - 1f * u), Size(18f * u, 2.4f * u), CornerRadius(1.2f * u))
            }
            drawRect(a.luz(MELAMINA, k), puerta.topLeft, puerta.size)
            drawRect(a.claro(0.3f), puerta.topLeft, Size(2f * u, puerta.height))
            drawRect(a.sombra(0.12f), Offset(puerta.right - puerta.width * 0.15f, puerta.top), Size(puerta.width * 0.15f, puerta.height))
            drawRoundRect(a.luz(ACERO_OSC, k), Offset(if (c % 2 == 0) puerta.right - 6f * u else puerta.left + 4f * u, puerta.top + 4f * u), Size(2.2f * u, 10f * u), CornerRadius(1f * u))
            // Las rozaduras de los zapatos en el bajo de las puertas.
            for (j in 0 until 3) drawOval(a.sombra(0.12f), Offset(x0 + (x1 - x0) * azar(992 + c, j), puerta.bottom - 5f * u * azar(993 + c, j) - 2f * u), Size(6f * u, 1.4f * u))
        }
        drawRect(a.luz(mezcla(TINTA, ACERO_OSC, 0.3f), 0.05f), Offset(mu.left + 3f * u, mu.bottom), Size(mu.width - 6f * u, ySuelo - mu.bottom))
        // El suelo de vinilo, con la media caña que sube por el zócalo.
        drawRect(a.luz(VINILO, 0.05f), Offset(0f, ySuelo), Size(w, h - ySuelo))
        pinceladas(Rect(0f, ySuelo, w, h), a.luz(mezcla(VINILO, LUZ_MUEBLE, 0.4f), 0.05f), 994, 12, u, 0.12f)
        for (j in 0 until 30) drawCircle(a.luz(if (j % 2 == 0) BLANCO else TINTA, 0.05f).copy(alpha = 0.35f), 0.7f * u, Offset(w * azar(995, j), ySuelo + (h - ySuelo) * azar(996, j)))
        drawRect(a.sombra(0.45f), Offset(0f, ySuelo), Size(w, 2f * u))
        // El taburete con ruedas, cortado por el borde izquierdo.
        taburete(Offset(w * 0.035f, ySuelo + (h - ySuelo) * 0.62f), w * 0.15f, a, maxOf(cerca(Offset(w * 0.05f, ySuelo)), 0.05f))
    }

    /** El taburete de laboratorio: el asiento redondo tapizado, la columna de gas y la base de estrella con sus ruedas. */
    private fun DrawScope.taburete(pie: Offset, an: Float, a: Aire, cerca: Float) {
        val u = density
        val asiento = pie.y - an * 0.68f
        drawOval(a.sombra(0.4f), Offset(pie.x - an * 0.45f, pie.y - an * 0.03f), Size(an * 1.1f, an * 0.1f))
        val acero = a.luz(ACERO_OSC, cerca)
        // La estrella: tres patas visibles con su rueda.
        listOf(-0.45f, 0.1f, 0.46f).forEachIndexed { i, f ->
            val fin = Offset(pie.x + an * f, pie.y - an * (if (i == 1) 0.0f else 0.03f))
            drawLine(acero, Offset(pie.x, pie.y - an * 0.1f), fin - Offset(0f, an * 0.04f), 3f * u, StrokeCap.Round)
            drawCircle(a.luz(TINTA, cerca), an * 0.035f, fin)
            drawCircle(a.claro(0.3f), an * 0.012f, fin - Offset(an * 0.01f, an * 0.01f))
        }
        // La columna de gas, con su funda.
        drawRect(acero, Offset(pie.x - an * 0.03f, asiento + an * 0.04f), Size(an * 0.06f, pie.y - asiento - an * 0.12f))
        drawRect(a.claro(0.35f), Offset(pie.x - an * 0.025f, asiento + an * 0.04f), Size(an * 0.012f, pie.y - asiento - an * 0.14f))
        drawRect(a.luz(TINTA, cerca), Offset(pie.x - an * 0.045f, pie.y - an * 0.32f), Size(an * 0.09f, an * 0.2f))
        // El asiento: el canto tapizado y la cara de arriba, vista un poco desde arriba.
        drawRoundRect(a.luz(mezcla(TINTA, CIAN, 0.3f), cerca), Offset(pie.x - an * 0.5f, asiento - an * 0.02f), Size(an, an * 0.09f), CornerRadius(an * 0.04f))
        drawOval(a.luz(mezcla(TINTA, CIAN, 0.45f), cerca), Offset(pie.x - an * 0.5f, asiento - an * 0.08f), Size(an, an * 0.12f))
        drawOval(a.claro(0.25f), Offset(pie.x - an * 0.35f, asiento - an * 0.065f), Size(an * 0.4f, an * 0.05f))
        drawLine(a.luz(mezcla(TINTA, CIAN, 0.2f), cerca), Offset(pie.x + an * 0.1f, asiento - an * 0.04f), Offset(pie.x + an * 0.25f, asiento - an * 0.03f), 0.8f * u)
    }

    // ---------------------------------------------------------------- los huecos libres

    /**
     * Lo del despacho en lo que queda libre de la repisa [k], a la derecha de [oc]: según cuánto hueco haya, el sujetalibros
     * de chapa pegado al último volumen, una pila de archivadores tumbados, la caja de puntas de pipeta y la lata de los
     * bolígrafos. Cada repisa escoge lo suyo, para que no se repita.
     */
    private fun DrawScope.huecoLibre(k: Int, r: Rect, oc: Float, a: Aire, cerca: Float, haciaX: Float) {
        val u = density
        val libre = (r.right - oc) / u
        val y = r.bottom - 1.5f * u
        when {
            libre >= 150f -> if ((k + k / 2) % 2 == 1) {
                sujetalibros(oc + 1f * u, y, r.height * 0.62f, a, cerca + 0.04f)
                archivadoresTumbados(Offset(oc + 30f * u, y), 70f * u, 3, a, cerca, k)
                lata(Offset(oc + 52f * u, y - 3 * 10f * u), 22f * u, a, cerca, haciaX, k)
                cajaPuntas(Offset(r.right - 46f * u, y), 56f * u, a, cerca - 0.03f, k)
            } else {
                sujetalibros(oc + 1f * u, y, r.height * 0.62f, a, cerca + 0.04f)
                cajaPuntas(Offset(oc + 44f * u, y), 52f * u, a, cerca, k)
                archivadoresTumbados(Offset(r.right - 92f * u, y), 76f * u, 2, a, cerca - 0.03f, k + 1)
                lata(Offset(r.right - 60f * u, y - 2 * 10f * u), 22f * u, a, cerca - 0.03f, haciaX, k)
            }
            libre >= 74f -> when (k % 3) {
                0 -> { archivadoresTumbados(Offset(oc + 6f * u, y), 62f * u, 2, a, cerca, k); lata(Offset(oc + 24f * u, y - 2 * 10f * u), 20f * u, a, cerca, haciaX, k) }
                1 -> { sujetalibros(oc + 1f * u, y, r.height * 0.6f, a, cerca); cajaPuntas(Offset((oc + r.right) / 2 + 12f * u, y), 46f * u, a, cerca, k) }
                else -> { sujetalibros(oc + 1f * u, y, r.height * 0.6f, a, cerca); lata(Offset(oc + 40f * u, y), 22f * u, a, cerca, haciaX, k) }
            }
            libre >= 46f -> when (k % 3) {
                0 -> sujetalibros(oc + 1f * u, y, r.height * 0.6f, a, cerca)
                1 -> lata(Offset((oc + r.right) / 2, y), 20f * u, a, cerca, haciaX, k)
                else -> cajaPuntas(Offset((oc + r.right) / 2, y), 38f * u, a, cerca, k)
            }
            libre >= 22f -> sujetalibros(oc + 1f * u, y, r.height * 0.55f, a, cerca)
        }
    }

    /** El sujetalibros de chapa doblada, pegado al último volumen: la placa con su ventana troquelada y la lengüeta del pie. */
    private fun DrawScope.sujetalibros(xIzq: Float, y: Float, alto: Float, a: Aire, cerca: Float) {
        val u = density
        val an = 9f * u
        val placa = Path().apply {
            moveTo(xIzq, y); lineTo(xIzq, y - alto + an * 0.6f)
            cubicTo(xIzq, y - alto, xIzq + an, y - alto, xIzq + an, y - alto + an * 0.6f)
            lineTo(xIzq + an, y); close()
        }
        drawPath(corrido(placa, if (a.lumbre) 7f * u else 5f * u, 2f * u), a.sombra(if (a.lumbre) 0.36f else 0.22f))
        // La lengüeta del pie, que entra bajo los libros y asoma a la derecha.
        drawRect(a.luz(ACERO_OSC, cerca), Offset(xIzq, y - 1.6f * u), Size(an + 8f * u, 1.6f * u))
        drawPath(placa, a.luz(mezcla(ACERO_OSC, CIAN, 0.2f), cerca))
        drawRect(a.claro(0.3f), Offset(xIzq, y - alto + an * 0.5f), Size(1.4f * u, alto - an * 0.5f))
        drawRect(a.sombra(0.25f), Offset(xIzq + an - 2f * u, y - alto + an * 0.5f), Size(2f * u, alto - an * 0.5f))
        drawRoundRect(a.luz(TINTA, cerca * 0.5f), Offset(xIzq + an * 0.3f, y - alto * 0.72f), Size(an * 0.4f, alto * 0.36f), CornerRadius(an * 0.2f))
        // La pintura saltada en el canto.
        drawOval(a.luz(ACERO, cerca), Offset(xIzq + an * 0.1f, y - alto + an * 0.9f), Size(an * 0.5f, 2.4f * u))
        if (a.lumbre) drawRect(a.filo(0.5f * cerca), Offset(xIzq, y - alto + an * 0.4f), Size(0.9f * u, alto - an * 0.4f))
    }

    /** Una pila de archivadores tumbados, vistos por el lomo, con su ojal de lado. */
    private fun DrawScope.archivadoresTumbados(izq: Offset, an: Float, cuantos: Int, a: Aire, cerca: Float, semilla: Int) {
        val u = density
        val g = 10f * u
        val colores = listOf(mezcla(CIAN, ACERO, 0.5f), mezcla(ACERO, BLANCO, 0.2f), mezcla(VIOLETA, ACERO, 0.55f), mezcla(KRAFT, ACERO, 0.3f))
        contacto(Offset(izq.x + an * 0.55f, izq.y), an * 1.1f, 4f * u, a, 0.36f)
        drawRect(a.sombra(if (a.lumbre) 0.34f else 0.2f), Offset(izq.x + 7f * u, izq.y - cuantos * g + 3f * u), Size(an, cuantos * g - 3f * u))
        for (j in 0 until cuantos) {
            val dx = (azar(semilla + 50, j) - 0.5f) * 8f * u
            val r = Rect(izq.x + dx, izq.y - (j + 1) * g, izq.x + dx + an * (0.9f + 0.1f * azar(semilla + 51, j)), izq.y - j * g)
            val col = a.luz(colores[(semilla + j) % colores.size], cerca)
            drawRoundRect(col, r.topLeft, Size(r.width, r.height - 0.6f * u), CornerRadius(1.5f * u))
            drawRect(a.claro(0.35f), Offset(r.left + 1f * u, r.top), Size(r.width - 2f * u, 1f * u))
            drawRect(a.sombra(0.25f), Offset(r.left, r.bottom - 2.2f * u), Size(r.width, 1.6f * u))
            // La etiqueta del lomo y el ojal.
            drawRect(a.etiqueta(cerca).copy(alpha = 0.9f), Offset(r.left + r.width * 0.12f, r.top + 2.4f * u), Size(r.width * 0.42f, r.height - 5f * u))
            drawLine(a.luz(TINTA, cerca).copy(alpha = 0.5f), Offset(r.left + r.width * 0.16f, r.center.y - 0.5f * u), Offset(r.left + r.width * 0.42f, r.center.y - 0.5f * u), 0.7f * u)
            val oj = Offset(r.right - r.width * 0.16f, r.center.y - 0.3f * u)
            drawCircle(a.luz(ACERO, cerca), 3f * u, oj)
            drawCircle(a.luz(TINTA, cerca * 0.5f), 2f * u, oj)
            if (a.lumbre && j == cuantos - 1) drawRect(a.filo(0.5f * cerca), r.topLeft, Size(r.width, 0.9f * u))
        }
    }

    /** La lata de los bolígrafos: una lata reciclada con su etiqueta, y dentro bolígrafos, un rotulador, unas tijeras y una regla. */
    private fun DrawScope.lata(pie: Offset, an: Float, a: Aire, cerca: Float, haciaX: Float, semilla: Int) {
        val u = density
        val alto = an * 1.2f
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        contacto(Offset(pie.x + an * 0.1f, pie.y), an * 1.3f, 4f * u, a, 0.36f)
        drawRect(a.sombra(if (a.lumbre) 0.34f else 0.2f), Offset(r.left + 6f * u, r.top + 2f * u), Size(an, alto - 2f * u))
        // Lo que asoma, detrás del borde.
        val cosas = listOf(Triple(-0.25f, -1.1f, CIAN), Triple(0.05f, -1.3f, TINTA), Triple(0.25f, -1.0f, ROSA), Triple(-0.05f, -0.9f, AMARILLO))
        cosas.forEachIndexed { i, (dx, dy, col) ->
            val p0 = Offset(pie.x + an * dx * 0.6f, r.top + 2f * u)
            val p1 = Offset(pie.x + an * dx * 1.4f + an * 0.1f * (azar(semilla + 70, i) - 0.5f), r.top + an * dy)
            drawLine(a.luz(col, cerca), p0, p1, (if (i == 3) 3.6f else 2f) * u, StrokeCap.Round)
            drawLine(a.claro(0.35f), p0 - Offset(0.6f * u, 0f), p1 - Offset(0.6f * u, 0f), 0.5f * u)
        }
        // La regla, más alta, de acero.
        drawLine(a.luz(ACERO, cerca), Offset(r.right - an * 0.2f, r.top + 2f * u), Offset(r.right + an * 0.12f, r.top - an * 1.5f), 3f * u)
        // Las tijeras: dos ojos negros asomando.
        drawCircle(a.luz(TINTA, cerca), an * 0.12f, Offset(r.left + an * 0.15f, r.top - an * 0.5f), style = Stroke(1.6f * u))
        drawCircle(a.luz(TINTA, cerca), an * 0.12f, Offset(r.left + an * 0.32f, r.top - an * 0.42f), style = Stroke(1.6f * u))
        val lata = caja(r, 1.5f * u)
        drawPath(lata, a.luz(mezcla(ACERO, BLANCO, 0.3f), cerca))
        drawRect(a.luz(mezcla(AMARILLO, KRAFT, 0.3f), cerca), Offset(r.left, r.top + alto * 0.25f), Size(an, alto * 0.5f))
        drawRect(a.luz(mezcla(ROSA, KRAFT, 0.3f), cerca), Offset(r.left, r.top + alto * 0.42f), Size(an, alto * 0.08f))
        for (j in 0 until 3) drawLine(a.sombra(0.2f), Offset(r.left, r.top + alto * (0.1f + 0.4f * j)), Offset(r.right, r.top + alto * (0.1f + 0.4f * j)), 0.6f * u)
        redondo(lata, a, cerca, haciaX)
        drawRect(a.luz(mezcla(ACERO, BLANCO, 0.5f), cerca), Offset(r.left - 0.5f * u, r.top), Size(an + 1f * u, 1.6f * u))
    }

    /** La caja de puntas de pipeta: la base de color, la placa con las puntas amarillas en filas y la tapa transparente con su bisagra. */
    private fun DrawScope.cajaPuntas(pie: Offset, an: Float, a: Aire, cerca: Float, semilla: Int) {
        val u = density
        val alto = an * 0.5f
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        val col = if (semilla % 2 == 0) CIAN else VIOLETA
        contacto(Offset(pie.x + an * 0.08f, pie.y), an * 1.1f, 4f * u, a, 0.36f)
        drawRect(a.sombra(if (a.lumbre) 0.34f else 0.2f), Offset(r.left + 6f * u, r.top + 2f * u), r.size)
        // La base.
        val base = Rect(r.left, r.top + alto * 0.45f, r.right, r.bottom)
        drawRoundRect(a.luz(col, cerca), base.topLeft, base.size, CornerRadius(1.5f * u))
        drawRect(a.claro(0.3f), base.topLeft, Size(an * 0.1f, base.height))
        drawRect(a.sombra(0.2f), Offset(base.right - an * 0.15f, base.top), Size(an * 0.15f, base.height))
        drawRect(a.luz(mezcla(col, TINTA, 0.3f), cerca), Offset(base.left, base.top), Size(an, 1.6f * u))
        // Las puntas, en dos filas que se ven por la tapa.
        val n = 9
        for (f in 0 until 2) for (j in 0 until n) {
            val x = r.left + an * (0.08f + 0.84f * j / (n - 1)) + f * an * 0.04f
            val yb = base.top + 0.5f * u - f * 2.5f * u
            drawPath(poligono(x - 1.4f * u, yb - alto * 0.32f, x + 1.4f * u, yb - alto * 0.32f, x + 0.3f * u, yb, x - 0.3f * u, yb), a.fluor(AMARILLO, FLUOR, cerca).copy(alpha = if (f == 0) 0.95f else 0.6f))
        }
        // La tapa transparente, con su reflejo y su bisagra.
        val tapa = Rect(r.left - 1f * u, r.top, r.right + 1f * u, base.top + 1.5f * u)
        drawRoundRect(a.luz(mezcla(BLANCO, CIAN, 0.2f), cerca).copy(alpha = 0.3f), tapa.topLeft, tapa.size, CornerRadius(1.5f * u))
        drawRoundRect(a.luz(ACERO_OSC, cerca).copy(alpha = 0.6f), tapa.topLeft, tapa.size, CornerRadius(1.5f * u), style = Stroke(0.7f * u))
        drawLine(a.claro(0.7f), Offset(tapa.left + an * 0.08f, tapa.top + 1.5f * u), Offset(tapa.left + an * 0.4f, tapa.top + 1.5f * u), 0.8f * u)
        if (a.lumbre) drawLine(a.filo(0.5f * cerca), tapa.topLeft, Offset(tapa.right, tapa.top), 0.8f * u)
    }

    // ---------------------------------------------------------------- cada entrada

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val a = Aire(oscuro, v.noche)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, v.repisas, LADO + v.centro * (1f - 2 * LADO), v.noche) else 1f
        when (clase(v.semilla)) {
            0 -> cuaderno(v, medidor, a, k)
            1 -> archivador(v, medidor, a, k)
            else -> cajaArchivo(v, medidor, a, k)
        }
    }

    /** La marca de riesgo: un rombo de borde rojo con su señal; en los Sueños, la luna sobre un redondel de papel fluorescente. */
    private fun DrawScope.rombo(c: Offset, rs: Float, a: Aire, k: Float, luna: Boolean) {
        val u = density
        if (luna) {
            drawCircle(a.etiqueta(k), rs * 0.9f, c)
            drawCircle(mezcla(UV, TINTA, 0.25f), rs * 0.62f, c)
            drawCircle(a.etiqueta(k), rs * 0.52f, Offset(c.x + rs * 0.3f, c.y - rs * 0.2f))
        } else {
            val p = poligono(c.x, c.y - rs, c.x + rs, c.y, c.x, c.y + rs, c.x - rs, c.y)
            // De noche, la pegatina se apaga con lo demás: lo que se lee siempre es la etiqueta del día.
            drawPath(p, if (a.oscuro) mezcla(a.etiqueta(k), a.hondo, 0.5f * (1f - k)) else a.etiqueta(k))
            drawPath(p, a.luz(Color(0xFFD8312A), k), style = Stroke(1.3f * u))
            drawRect(TINTA, Offset(c.x - 0.65f * u, c.y - rs * 0.5f), Size(1.3f * u, rs * 0.62f))
            drawCircle(TINTA, 0.75f * u, Offset(c.x, c.y + rs * 0.42f))
        }
    }

    /** El filo de la luz en lo que la mira: la cabeza y el costado izquierdo del volumen. */
    private fun DrawScope.filoVolumen(a: Aire, k: Float, arriba: Float) {
        if (!a.lumbre || k < 0.08f) return
        val u = density
        drawRect(a.filo(0.6f * k), Offset(1f * u, arriba), Size(size.width - 2f * u, 1f * u))
        drawRect(a.filo(0.32f * k), Offset(0f, arriba + 1.5f * u), Size(0.9f * u, size.height - arriba - 1.5f * u))
    }

    /** La etiqueta impresa con su franja de color y el día grande; el mes debajo, más chico. */
    private fun DrawScope.etiquetaDia(v: Volumen, medidor: TextMeasurer, r: Rect, a: Aire, k: Float, franja: Boolean = true) {
        val u = density
        drawRect(a.sombra(0.35f), r.topLeft + Offset(0.8f * u, 1f * u), r.size)
        drawRect(a.etiqueta(k), r.topLeft, r.size)
        if (franja) drawRect(a.fluor(CIAN, FLUOR, maxOf(k, 0.3f)), r.topLeft, Size(r.width, r.height * 0.13f))
        val y0 = r.top + r.height * (if (franja) 0.14f else 0.04f)
        textoEn(medidor, v.dia, Rect(r.left + 0.4f * u, y0, r.right - 0.4f * u, r.top + r.height * 0.7f), LETRA, TINTA, 15f, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes, Rect(r.left + 0.6f * u, r.top + r.height * 0.68f, r.right - 0.6f * u, r.bottom - 0.6f * u), LETRA, TINTA.copy(alpha = 0.85f), 7.5f, minimo = 4.5f)
    }

    /**
     * El cuaderno de laboratorio de tapa dura (el de la tapa): la tapa del color del ánimo, la tela negra arriba y abajo, la
     * etiqueta impresa con su franja cian, el día y el mes, el código de barras, el rombo de riesgo (en los Sueños, la luna) y,
     * asomando por arriba, las pestañas de índice: más cuanto más escribiste.
     */
    private fun DrawScope.cuaderno(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 5f * u
        // Las pestañas que asoman por la cabeza.
        val n = 1 + (v.lleno * 3.2f).toInt()
        val colores = listOf(CIAN, AMARILLO, ROSA, VERDE)
        for (j in 0 until n) {
            val x = w * (0.1f + 0.75f * ((j + s % 3) % 4) / 4f)
            drawRect(a.fluor(colores[(j + s) % 4], if (j % 2 == 0) FLUOR else Color(0xFFFF8BE6), k), Offset(x, top * (0.1f + 0.4f * (j % 2))), Size(w * 0.17f, top + 2f * u))
        }
        val tapa = v.tono?.let { a.animo(it, k) } ?: a.luz(Color(0xFF4A5662), k)
        drawRoundRect(tapa, Offset(0f, top), Size(w, h - top), CornerRadius(1.2f * u))
        // El bulto: luz a la izquierda, sombra a la derecha.
        drawRect(a.claro(0.22f), Offset(w * 0.05f, top), Size(w * 0.1f, h - top))
        drawRect(a.sombra(0.2f), Offset(w * 0.82f, top), Size(w * 0.18f, h - top))
        drawRect(a.sombra(0.3f), Offset(w - 1f * u, top), Size(1f * u, h - top))
        // La tela negra de la cabeza y del pie, con su trama.
        val tela = a.luz(Color(0xFF262B31), k)
        drawRect(tela, Offset(0f, top), Size(w, h * 0.05f))
        drawRect(tela, Offset(0f, h * 0.885f), Size(w, h * 0.115f))
        drawLine(a.claro(0.18f), Offset(0f, h * 0.9f), Offset(w, h * 0.9f), 0.5f * u)
        drawLine(a.claro(0.4f), Offset(1f * u, top + 0.5f * u), Offset(w - 1f * u, top + 0.5f * u), 0.7f * u)
        // La etiqueta impresa con el día.
        val eti = Rect(w * 0.11f, top + h * 0.085f, w * 0.85f, top + h * 0.34f)
        etiquetaDia(v, medidor, eti, a, k)
        // El código de barras.
        val cb = Rect(w * 0.15f, eti.bottom + h * 0.035f, w * 0.81f, eti.bottom + h * 0.11f)
        drawRect(a.etiqueta(k), cb.topLeft, cb.size)
        var bx = cb.left + 1f * u; var j = 0
        val fin = cb.right - 1f * u
        while (bx < fin && j < 14) {
            val g = (if (azar(s, 50 + j) > 0.5f) 1.1f else 0.55f) * u
            drawRect(TINTA.copy(alpha = 0.85f), Offset(bx, cb.top + 1f * u), Size(minOf(g, fin - bx), cb.height - 2f * u))
            bx += g + (0.55f + azar(s, 80 + j)) * u; j++
        }
        // El rombo de riesgo (la luna, en los Sueños).
        rombo(Offset(w * 0.47f, h * 0.73f), minOf(w * 0.28f, 7f * u), a, k, v.noche)
        // El uso: la esquina de abajo gastada y una rozadura.
        drawPath(poligono(0f, h * 0.885f, 0f, h * 0.85f, w * 0.12f, h * 0.885f), a.luz(mezcla(Color(0xFF262B31), BLANCO, 0.2f), k))
        drawLine(a.claro(0.2f), Offset(w * 0.2f, h * 0.58f), Offset(w * 0.4f, h * 0.6f), 0.6f * u)
        filoVolumen(a, k, top)
    }

    /**
     * El archivador de palanca: más bajo, con el lomo del color del ánimo, la ventanilla con la etiqueta del día, el ojal
     * metálico para sacarlo, el canto de metal del pie y, asomando por arriba, los separadores (más cuanto más escribiste).
     */
    private fun DrawScope.archivador(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 6f * u
        // Los separadores de colores, de alturas distintas.
        val n = 2 + (v.lleno * 3.4f).toInt()
        val sep = listOf(mezcla(CIAN, BLANCO, 0.4f), mezcla(AMARILLO, BLANCO, 0.3f), mezcla(ROSA, BLANCO, 0.4f), mezcla(VERDE, BLANCO, 0.4f), BLANCO)
        for (j in 0 until n) {
            val x = w * (0.08f + 0.84f * j / n)
            val alto = top * (0.5f + 0.5f * azar(s, 20 + j))
            drawRect(a.fluor(sep[(j + s) % sep.size], if (j % 2 == 0) FLUOR else PAPEL_UV, k), Offset(x, top - alto), Size(w * 0.84f / n - 0.6f * u, alto + 2f * u))
        }
        // El lomo, de cartón forrado del color del ánimo.
        val lomo = v.tono?.let { a.animo(it, k) } ?: a.luz(Color(0xFF6E7B80), k)
        drawRoundRect(lomo, Offset(0f, top), Size(w, h - top), CornerRadius(2.4f * u))
        drawRect(a.claro(0.24f), Offset(w * 0.06f, top + 1f * u), Size(w * 0.12f, h - top - 2f * u))
        drawRect(a.sombra(0.2f), Offset(w * 0.78f, top), Size(w * 0.22f, h - top))
        drawRect(a.sombra(0.14f), Offset(w * 0.9f, top), Size(w * 0.1f, h - top))
        // El jaspeado del forro: unas manchas del mismo color.
        for (j in 0 until 4) drawOval(mezcla(lomo, a.hondo, 0.25f).copy(alpha = 0.5f), Offset(w * azar(s, 30 + j) - 2f * u, top + (h - top) * azar(s, 40 + j)), Size(4f * u, 2f * u))
        // La ventanilla de plástico con la etiqueta.
        val ven = Rect(w * 0.12f, top + h * 0.07f, w * 0.88f, top + h * 0.4f)
        drawRect(a.sombra(0.35f), ven.topLeft - Offset(0.6f * u, 0.6f * u), Size(ven.width + 1.2f * u, ven.height + 1.2f * u))
        etiquetaDia(v, medidor, ven, a, k, franja = false)
        drawLine(a.claro(0.6f), Offset(ven.left + 1f * u, ven.top + 1f * u), Offset(ven.left + ven.width * 0.5f, ven.top + 1f * u), 0.8f * u)
        // La marca: el rombo chico, o la luna en los Sueños.
        rombo(Offset(w * 0.5f, ven.bottom + h * 0.1f), minOf(w * 0.2f, 5f * u), a, k, v.noche)
        // El ojal: el aro de metal y el agujero.
        val oj = Offset(w * 0.5f, h * 0.79f)
        val ro = minOf(w * 0.25f, 6.5f * u)
        drawCircle(a.luz(mezcla(ACERO, BLANCO, 0.3f), maxOf(k, 0.2f)), ro, oj)
        drawCircle(a.luz(TINTA, k * 0.5f), ro * 0.66f, oj)
        drawArc(a.claro(0.6f), 180f, 90f, false, Offset(oj.x - ro, oj.y - ro), Size(2 * ro, 2 * ro), style = Stroke(1f * u))
        drawArc(a.sombra(0.4f), 0f, 90f, false, Offset(oj.x - ro * 0.66f, oj.y - ro * 0.66f), Size(ro * 1.32f, ro * 1.32f), style = Stroke(1f * u))
        // El canto de metal del pie.
        drawRect(a.luz(ACERO, maxOf(k, 0.15f)), Offset(0f, h - 3f * u), Size(w, 3f * u))
        drawRect(a.claro(0.4f), Offset(0f, h - 3f * u), Size(w, 0.7f * u))
        filoVolumen(a, k, top)
    }

    /**
     * La caja de archivo de cartón: la más alta, de cartón kraft con su agarradero ovalado, la etiqueta del día, la faja ancha
     * del color del ánimo, el rombo (la luna, en los Sueños) y las hojas que asoman por arriba: más cuanto más escribiste.
     */
    private fun DrawScope.cajaArchivo(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = (3f + 5f * v.lleno) * u
        // Las hojas que asoman, desiguales y un poco torcidas.
        val n = 2 + (v.lleno * 3f).toInt()
        for (j in 0 until n) {
            val x = w * (0.06f + 0.12f * j)
            val y = top * (0.05f + 0.6f * azar(s, 60 + j))
            drawPath(poligono(x, y + 1f * u, x + w * 0.62f, y, x + w * 0.64f, top + 2f * u, x, top + 2f * u), a.luz(if (j % 3 == 2) mezcla(AMARILLO, BLANCO, 0.5f) else BLANCO, k))
            drawLine(a.sombra(0.3f), Offset(x + w * 0.62f, y), Offset(x + w * 0.64f, top + 2f * u), 0.6f * u)
        }
        val kraft = a.luz(KRAFT, k)
        drawRect(kraft, Offset(0f, top), Size(w, h - top))
        drawRect(a.claro(0.26f), Offset(w * 0.04f, top), Size(w * 0.1f, h - top))
        drawRect(a.sombra(0.22f), Offset(w * 0.8f, top), Size(w * 0.2f, h - top))
        drawRect(mezcla(kraft, a.hondo, 0.3f), Offset(w * 0.5f, top), Size(0.6f * u, h - top))
        drawRect(a.sombra(0.3f), Offset(0f, top), Size(w, 1.2f * u))
        // El agarradero ovalado.
        val ag = Rect(w * 0.26f, top + h * 0.04f, w * 0.74f, top + h * 0.04f + 4.5f * u)
        drawOval(a.luz(mezcla(TINTA, KRAFT, 0.25f), k * 0.5f), ag.topLeft, ag.size)
        drawArc(a.claro(0.4f), 20f, 140f, false, ag.topLeft, ag.size, style = Stroke(0.8f * u))
        // La etiqueta pegada con el día.
        val eti = Rect(w * 0.12f, ag.bottom + h * 0.04f, w * 0.88f, ag.bottom + h * 0.26f)
        etiquetaDia(v, medidor, eti, a, k, franja = false)
        // La faja ancha del color del ánimo (cinta de papel crudo si no se marcó), con sus bordes rotos.
        val faja = v.tono?.let { a.animo(it, k) } ?: a.luz(mezcla(KRAFT, BLANCO, 0.45f), k)
        val fy = eti.bottom + h * 0.05f; val fh = h * 0.17f
        drawRect(a.sombra(0.25f), Offset(0f, fy + fh), Size(w, 1.2f * u))
        drawRect(faja, Offset(0f, fy), Size(w, fh))
        drawRect(a.claro(0.25f), Offset(0f, fy), Size(w * 0.6f, 0.8f * u))
        drawRect(a.sombra(0.18f), Offset(w * 0.8f, fy), Size(w * 0.2f, fh))
        // La marca, al pie.
        rombo(Offset(w * 0.5f, fy + fh + (h - fy - fh) * 0.45f), minOf(w * 0.24f, 6f * u), a, k, v.noche)
        // El pie aplastado y una esquina rota.
        drawRect(a.sombra(0.28f), Offset(0f, h - 2f * u), Size(w, 2f * u))
        drawPath(poligono(w, h, w, h - 6f * u, w - 3f * u, h), a.luz(mezcla(KRAFT, BLANCO, 0.3f), k))
        filoVolumen(a, k, top)
    }
}
