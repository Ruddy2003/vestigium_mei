package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TrazosCiudad
import io.github.ruddy2003.vestigium.ui.TrazosTinta
import io.github.ruddy2003.vestigium.ui.TrazosTinta.Tinta
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El dibujo del estante del diario de Psicodelia v2 (lo usa `EstantePsicodelia`, en `BibliotecaPsicodelia.kt`): la repisa
 * de tablas sobre ladrillos del cuarto de David, contra una pared de yeso pintado de departamento victoriano viejo, sin
 * ventana. Un solo dibujo (la geometría: qué hay y dónde) con dos pintores, uno por cara ([Pintor]):
 * - **la ciudad** (cara BASE): gouache con color de película vieja; de día, la luz de la tarde de arriba a la izquierda,
 *   sombras café y grano; de noche, la lámpara de pantalla encendida sobre la tabla de arriba es la única luz y baja por
 *   las repisas;
 * - **la psicodelia** (cara INTENSA): el mismo mueble impreso a tintas planas (magenta, bermellón y ultramar, más el papel y
 *   la tinta que graba), con filete y trama en las sombras; los rayos que ondulan salen del disco del tocadiscos. De noche,
 *   sobre terciopelo negro, la luz son las manchas de las luces líquidas proyectadas en la pared, que encienden los cantos.
 *   La intensidad ([EstadoTema.intensidad]) sube el croma, corre el registro, agranda las manchas y suma anillos.
 * La luz del mueble y la de los volúmenes salen de la misma cuenta ([Pintor.luz]).
 */
internal object MuebleEstantePsicodelia {
    // ---------------------------------------------------------------- la paleta de la ciudad (de `TrazosCiudad`, pasada por la película)
    /** El yeso pintado de la pared, crema gris de foto vieja; la moldura de colgar cuadros, más clara. */
    private val PARED = Color(0xFFD3C6AC)
    private val MOLDURA = TrazosCiudad.AL_SOL
    /** El pino sin pintar de las tablas y la madera oscura. */
    private val PINO = Color(0xFFB58C5E)
    private val MADERA = TrazosCiudad.MADERA
    /** El ladrillo de las pilas (el rojo apagado de la foto). */
    private val LADRILLO = TrazosCiudad.COCHE
    /** El cartón de las carpetas y la caja, el papel, el vinilo y el metal gastado. */
    private val CARTON = Color(0xFFB09670)
    private val PAPEL = Color(0xFFEDE3CF)
    private val VINILO = Color(0xFF2A211C)
    private val METAL = TrazosCiudad.PASAMANOS
    /** El forro del tocadiscos: el verde azul desteñido de la casa pintada. */
    private val TELA = TrazosCiudad.CASA_PINTADA
    /** El naranja quemado, el verde botella, el ocre y el rojo de letrero (los de la paleta de la ciudad). */
    private val NARANJA = TrazosCiudad.ETIQUETA
    private val VERDE = Color(0xFF3D5838)
    private val OCRE = Color(0xFF8A6420)
    private val ROJO = TrazosCiudad.LETRERO
    /** La pantalla plisada apagada y encendida, y su luz. */
    private val PANTALLA = Color(0xFFE2CFA6)
    private val LAMPARA = TrazosCiudad.LAMPARA
    private val LAMPARA_LUZ = TrazosCiudad.LAMPARA_LUZ
    /** El suelo de tabla. */
    private val SUELO = Color(0xFF8A6040)
    /** La tinta de escribir de la ciudad (café casi negro) y la sombra café. */
    private val TINTA_C = Color(0xFF2A2119)
    private val SOMBRA = TrazosCiudad.SOMBRA
    private val AL_SOL = TrazosCiudad.AL_SOL
    /** La noche del cuarto, la de los Sueños (más azul) y el anochecer de los Sueños en claro. */
    private val NOCHE = TrazosCiudad.PARED_NOCHE
    private val NOCHE_SUENOS = Color(0xFF141826)
    private val ANOCHECER = Color(0xFF3B3A5A)
    /** La luna de los Sueños. */
    private val LUNA = Color(0xFFF1E6C6)

    private val DIA: FontFamily = LetraExtra.franklinTitular
    private val ROTULO: FontFamily = LetraExtra.franklinRotulo

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.6f
    const val BASE = 0.21f
    const val LADO = 0.085f
    val ALTO_REPISA: Dp = 110.dp
    val TABLA: Dp = 14.dp
    val SEPARA: Dp = 1.5.dp

    /** De 22 a 38 dp: caben de 9 a 15 por repisa en un teléfono (14 con los días cortos del arnés). */
    fun ancho(palabras: Int): Dp = (22 + (palabras / 45).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el disco en su funda (el principal); 1, la carpeta de carteles; 2, el cuaderno negro. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 8)
        return if (k < 4) 0 else if (k < 6) 1 else 2
    }

    /** El disco, el más alto (y le asoma la funda interior); la carpeta, la más baja; el cuaderno, en medio. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.88f + 0.06f * azar(semilla, 4)
        1 -> 0.68f + 0.08f * azar(semilla, 4)
        else -> 0.77f + 0.07f * azar(semilla, 4)
    }

    // ---------------------------------------------------------------- la luz (la misma cuenta para el mueble y los volúmenes)

    /** Dónde está la bombilla de la lámpara de pantalla, en anchos del mueble desde la esquina de arriba a la izquierda. */
    private const val LAMP_X = 0.135f
    private const val LAMP_Y = 0.305f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble: el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 124f
    /** Dónde gira el disco del tocadiscos (el sol de la cara psicodelia), en anchos. */
    private const val DISCO_X = 0.47f
    private const val DISCO_Y = 0.475f

    /** La altura (en anchos) del medio de la repisa [pos] (0.5 es media altura de la de arriba). */
    private fun fyCuerpo(pos: Float): Float = CORONA + pos * FILA / ANCHO_NOMINAL

    private fun alcance(dx: Float, dy: Float, radio: Float): Float = (1f - sqrt(dx * dx + dy * dy) / radio.coerceAtLeast(0.05f)).coerceIn(0f, 1f)

    /** Una luz de la psicodelia de noche: una mancha de luz líquida proyectada en la pared, con su tinta y su peso (0 a 1). */
    private class Foco(val fx: Float, val fy: Float, val radio: Float, val tinta: Tinta, val peso: Float)

    /** Lo que materia es cada cosa, para que cada pintor le dé su color. */
    private enum class Mat { PARED, MOLDURA, PINO, MADERA, LADRILLO, CARTON, PAPEL, VINILO, METAL, TELA, NARANJA, VERDE, OCRE, ROJO, PANTALLA, SUELO }

    /**
     * El pintor: la cara (ciudad o psicodelia), el claro o el oscuro, los Sueños ([noche]) y la intensidad. De él salen el
     * color de cada materia según cuánta luz le llega ([luz]), la sombra (café en la ciudad, trama en la tinta), la luz, el
     * filete y el filo que enciende los cantos.
     */
    private class Pintor(val oscuro: Boolean, val noche: Boolean, cara: Cara, intensidad: Float, val repisas: Int) {
        val tinta = cara == Cara.INTENSA
        val i = if (tinta) (if (intensidad.isNaN()) 0f else intensidad.coerceIn(0f, 1f)) else 0f
        val m = TrazosTinta.medidas(i)
        /** Cuánto se imprime la tercera tinta (0 con i = 0; 1 desde i = 0.5). */
        val berm = m.bermellon
        /** Si la lámpara de pantalla está encendida (la ciudad de noche y los Sueños de la ciudad). */
        val lampara = !tinta && (oscuro || noche)
        /** Si hay una luz que baja por el mueble (la lámpara, o las luces líquidas sobre el negro). */
        val alumbra = lampara || (tinta && oscuro)
        /** El color hacia donde se hunde lo que no se alumbra, y el de la sombra. */
        val hondo: Color = when {
            tinta -> if (oscuro) TrazosTinta.NEGRO else TrazosTinta.GRABAR
            oscuro -> if (noche) NOCHE_SUENOS else NOCHE
            else -> SOMBRA
        }
        val grabado: Color = TrazosTinta.grabado(oscuro)
        fun ink(t: Tinta): Color = TrazosTinta.tinta(t, oscuro, i)
        /** El papel de un objeto impreso: crema sobre papel; sobre el negro, el crema hace de cuarta tinta. */
        val papelObj: Color = if (oscuro) TrazosTinta.CREMA else TrazosTinta.PAPEL

        private val ancho = repisas.coerceIn(2, 5)
        val focos: List<Foco> = if (tinta && oscuro) listOf(
            Foco(0.17f, 0.17f, 1.05f + 0.08f * ancho + 0.35f * i, Tinta.MAGENTA, 1f),
            Foco(0.82f, 0.12f, 1.0f + 0.08f * ancho + 0.35f * i, Tinta.ULTRAMAR, 1f),
            Foco(0.55f, 0.33f, 0.85f + 0.08f * ancho + 0.45f * i, Tinta.BERMELLON, berm),
        ) else emptyList()

        /** Cuánta luz llega (de 0 a 1) a un punto a [fx], [fy] (en anchos del mueble). De día y en la tinta clara, toda. */
        fun luz(fx: Float, fy: Float): Float = when {
            lampara -> alcance(fx - LAMP_X, fy - LAMP_Y, 1.05f + 0.1f * ancho)
            tinta && oscuro -> focos.maxOf { it.peso * alcance(fx - it.fx, fy - it.fy, it.radio) }
            else -> 1f
        }

        /** El color del filo que deja la luz en los cantos que la miran, en [fx], [fy]. */
        fun filo(fx: Float, fy: Float): Color = when {
            tinta && oscuro -> ink(focos.maxByOrNull { it.peso * alcance(fx - it.fx, fy - it.fy, it.radio) }?.tinta ?: Tinta.MAGENTA)
            tinta -> TrazosTinta.PAPEL
            else -> LAMPARA_LUZ
        }

        /** La materia [mat] impresa: en qué tinta (o papel) sale. */
        fun enTinta(mat: Mat): Color = when (mat) {
            Mat.PARED -> if (oscuro) TrazosTinta.NEGRO else TrazosTinta.PAPEL
            Mat.MOLDURA -> if (oscuro) Color(0xFF241D30) else TrazosTinta.PAPEL
            Mat.PINO, Mat.CARTON, Mat.PAPEL, Mat.METAL, Mat.PANTALLA -> papelObj
            Mat.MADERA, Mat.TELA, Mat.VERDE, Mat.SUELO -> ink(Tinta.ULTRAMAR)
            Mat.LADRILLO, Mat.ROJO -> ink(Tinta.MAGENTA)
            Mat.NARANJA -> mezcla(ink(Tinta.MAGENTA), ink(Tinta.BERMELLON), berm)
            Mat.OCRE -> TrazosTinta.sobreimpresion(ink(Tinta.MAGENTA), ink(Tinta.ULTRAMAR), oscuro)
            Mat.VINILO -> if (oscuro) Color(0xFF231C2E) else TrazosTinta.GRABAR
        }

        /** En qué tinta se corre el registro de [mat] (null: papel o tinta de grabar, que no se corren). */
        fun tintaDe(mat: Mat): Tinta? = when (mat) {
            Mat.LADRILLO, Mat.ROJO -> Tinta.MAGENTA
            Mat.NARANJA -> if (berm > 0.5f) Tinta.BERMELLON else Tinta.MAGENTA
            Mat.MADERA, Mat.TELA, Mat.VERDE, Mat.SUELO -> Tinta.ULTRAMAR
            else -> null
        }

        private fun base(mat: Mat): Color = when (mat) {
            Mat.PARED -> PARED; Mat.MOLDURA -> MOLDURA; Mat.PINO -> PINO; Mat.MADERA -> MADERA; Mat.LADRILLO -> LADRILLO
            Mat.CARTON -> CARTON; Mat.PAPEL -> PAPEL; Mat.VINILO -> VINILO; Mat.METAL -> METAL; Mat.TELA -> TELA
            Mat.NARANJA -> NARANJA; Mat.VERDE -> VERDE; Mat.OCRE -> OCRE; Mat.ROJO -> ROJO; Mat.PANTALLA -> PANTALLA; Mat.SUELO -> SUELO
        }

        /** Un color cualquiera con la luz [k] que le llega y el filo [fl]. */
        fun con(c: Color, k: Float, fl: Color = LAMPARA_LUZ): Color {
            val kk = k.coerceIn(0f, 1f)
            if (tinta) return when {
                oscuro -> mezcla(mezcla(c, TrazosTinta.NEGRO, 0.84f - 0.6f * kk), fl, 0.14f * kk)
                noche -> mezcla(c, ink(Tinta.ULTRAMAR), 0.1f)
                else -> c
            }
            return when {
                oscuro -> mezcla(mezcla(c, hondo, 0.9f - 0.8f * kk.pow(0.9f)), LAMPARA_LUZ, 0.2f * kk)
                noche -> mezcla(mezcla(c, ANOCHECER, 0.42f - 0.24f * kk), LAMPARA_LUZ, 0.14f * kk)
                else -> c
            }
        }

        /** El color de [mat] con la luz [k]. */
        fun c(mat: Mat, k: Float, fl: Color = LAMPARA_LUZ): Color = con(if (tinta) enTinta(mat) else base(mat), k, fl)

        /** El color del ánimo: se apaga menos que lo demás, para que se vea también en la repisa más lejana. */
        fun animo(t: Color, k: Float, fl: Color): Color {
            val kk = k.coerceIn(0f, 1f)
            return when {
                oscuro && tinta -> mezcla(mezcla(t, TrazosTinta.NEGRO, 0.5f - 0.42f * kk), fl, 0.1f * kk)
                oscuro -> mezcla(mezcla(t, hondo, 0.58f - 0.5f * kk), LAMPARA_LUZ, 0.1f * kk)
                noche && !tinta -> mezcla(t, ANOCHECER, 0.2f)
                else -> t
            }
        }

        /** La etiqueta clara donde va el día: casi no se apaga, para que el número se lea siempre (contraste ≥ 4.5 con [letra]). */
        fun etiqueta(k: Float): Color {
            val kk = k.coerceIn(0f, 1f)
            val b = if (tinta) papelObj else PAPEL
            return when {
                oscuro -> mezcla(b, hondo, 0.34f - 0.3f * kk)
                noche && !tinta -> mezcla(b, ANOCHECER, 0.12f)
                else -> b
            }
        }
        /** La letra del día sobre la etiqueta. */
        val letra: Color get() = if (tinta) (if (oscuro) TrazosTinta.NEGRO else TrazosTinta.GRABAR) else TINTA_C

        fun sombra(a: Float): Color = hondo.copy(alpha = (if (oscuro) a * 1.2f else a).coerceIn(0f, 1f))
        /** La luz que da en lo que mira arriba a la izquierda: crema de día; de noche, casi nada (la pone el filo). */
        fun claro(a: Float): Color = when {
            tinta -> TrazosTinta.PAPEL.copy(alpha = if (oscuro) 0f else (a * 0.6f).coerceIn(0f, 1f))
            oscuro -> AL_SOL.copy(alpha = (a * 0.12f).coerceIn(0f, 1f))
            noche -> AL_SOL.copy(alpha = (a * 0.5f).coerceIn(0f, 1f))
            else -> AL_SOL.copy(alpha = a.coerceIn(0f, 1f))
        }
    }

    private fun pintor(oscuro: Boolean, noche: Boolean, repisas: Int) =
        Pintor(oscuro, noche, EstadoTema.cara, EstadoTema.intensidad, repisas)

    // ---------------------------------------------------------------- ayudantes de forma y de pintor

    private fun rect(r: Rect) = Path().apply { addRect(r) }
    private fun redondo(r: Rect, radio: Float) = Path().apply { addRoundRect(RoundRect(r, CornerRadius(radio))) }
    private fun ovalo(r: Rect) = Path().apply { addOval(r) }
    private fun corrido(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }
    private fun poligono(vararg xy: Float) = Path().apply {
        moveTo(xy[0], xy[1])
        for (k in 1 until xy.size / 2) lineTo(xy[2 * k], xy[2 * k + 1])
        close()
    }

    /** Llena [forma] con la materia [mat]: en la tinta, corrida por el registro de su tinta. */
    private fun DrawScope.relleno(p: Pintor, forma: Path, mat: Mat, k: Float, fl: Color = LAMPARA_LUZ) {
        val color = p.c(mat, k, fl)
        val t = if (p.tinta) p.tintaDe(mat) else null
        if (t == null || p.m.registro <= 0f) drawPath(forma, color)
        else {
            val d = TrazosTinta.corrimiento(t, p.m.registro) * density
            translate(d.x, d.y) { drawPath(forma, color) }
        }
    }

    /** La sombra sobre [forma]: café en la ciudad; en la tinta, trama de líneas (más cerrada cuanto más fuerte [a]). */
    private fun DrawScope.sombra(p: Pintor, forma: Path, a: Float) {
        if (a <= 0f) return
        if (p.tinta && !p.oscuro) {
            val paso = maxOf(2.1f, 6.4f - 9f * a) * density
            with(TrazosTinta) { trama(forma, paso, 0.8f * density, TrazosTinta.GRABAR.copy(alpha = 0.85f), 45f) }
        } else drawPath(forma, p.sombra(a))
    }

    /** El filete de tinta de la cara psicodelia (en la ciudad no hay contornos: es gouache). */
    private fun DrawScope.filete(p: Pintor, forma: Path, grueso: Float = 0.9f) {
        if (!p.tinta) return
        drawPath(forma, if (p.oscuro) TrazosTinta.NEGRO.copy(alpha = 0.75f) else TrazosTinta.GRABAR, style = Stroke(grueso * density, join = StrokeJoin.Round))
    }

    /** El filo de la luz en un canto (de [a] a [b]), tanto más fuerte cuanto más [k]. Solo con una luz que alumbra. */
    private fun DrawScope.filoLinea(p: Pintor, a: Offset, b: Offset, k: Float, fl: Color, grueso: Float = 1.1f) {
        if (!p.alumbra || k < 0.04f) return
        drawLine(fl.copy(alpha = (0.1f + 0.65f * k).coerceAtMost(0.8f)), a, b, grueso * density)
    }

    // =================================================================
    //  El estante
    // =================================================================

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w <= 0f || h <= 0f) return
        val n = d.repisas.size
        val p = pintor(oscuro, noche, n)
        val ch = d.corona.bottom
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val tablaAlto = if (d.tablas.isNotEmpty()) d.tablas[0].height else TABLA.toPx()
        val arriba = ch - tablaAlto
        val piso = d.base.top + d.base.height * 0.5f
        // De un punto del lienzo a anchos del mueble: arriba, tal cual; en el cuerpo, por repisas (la misma cuenta que los volúmenes).
        fun fy(y: Float): Float = if (y <= ch) y / w else fyCuerpo((y - ch) / fila)
        fun k(o: Offset): Float = p.luz(o.x / w, fy(o.y))
        fun fl(o: Offset): Color = p.filo(o.x / w, fy(o.y))
        /** Hacia dónde cae la sombra de algo en [o] sobre la pared: de día, abajo a la derecha; de noche, lejos de la luz. */
        fun caida(o: Offset, largo: Float): Offset {
            if (!p.alumbra) return Offset(largo * 1.1f, largo * 0.8f)
            val luz = if (p.lampara) Offset(LAMP_X * w, LAMP_Y * w) else {
                val f = p.focos.maxByOrNull { it.peso * alcance(o.x / w - it.fx, fy(o.y) - it.fy, it.radio) } ?: p.focos.first()
                Offset(f.fx * w, f.fy * w)
            }
            val v = o - luz; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 2.6f)
        }

        pared(p, d, piso, ch, ::k)
        luces(p, d, ch)
        cartel(p, Rect(w * 0.25f, w * 0.05f, w * 0.52f, w * 0.37f), medidor, noche, k(Offset(w * 0.39f, w * 0.2f)), fl(Offset(w * 0.39f, w * 0.2f)), caida(Offset(w * 0.39f, w * 0.2f), 4f * u))
        boceto(p, Rect(w * 0.6f, w * 0.085f, w * 0.775f, w * 0.29f), k(Offset(w * 0.69f, w * 0.19f)), fl(Offset(w * 0.69f, w * 0.19f)), caida(Offset(w * 0.69f, w * 0.19f), 3f * u))

        // ---- la sombra del mueble en la pared, a la derecha y bajo las tablas
        val ladoI = d.izquierda; val ladoD = d.derecha
        val sd = caida(Offset(ladoD.center.x, (ch + piso) / 2), 3f * u)
        drawRect(p.sombra(if (oscuro) 0.38f else 0.2f), Offset(ladoD.right - 2f * u + maxOf(0f, sd.x), arriba + 4f * u), Size(maxOf(5f * u, sd.x * 1.8f), piso - arriba))

        // ---- el fondo de cada repisa: la pared en sombra, con la luz que entra por la izquierda
        d.repisas.forEachIndexed { r, rr ->
            val zona = Rect(ladoI.right, rr.top, ladoD.left, rr.bottom)
            clipRect(zona.left, zona.top, zona.right, zona.bottom) {
                fondoRepisa(p, zona, r, k(zona.center), fl(zona.center))
                // La sombra que arroja el último volumen sobre el fondo, a su derecha.
                val oc = d.ocupado.getOrNull(r) ?: zona.left
                if (oc > zona.left + 6f * u) {
                    val largo = (if (p.alumbra) 20f else 12f) * u
                    sombra(p, poligono(oc - 2f * u, zona.bottom, oc - 2f * u, zona.top + zona.height * 0.18f, oc + largo, zona.top + zona.height * 0.3f, oc + largo, zona.bottom), if (oscuro) 0.42f else 0.3f)
                }
            }
        }

        // ---- las pilas de ladrillo, del suelo a la tabla de arriba
        pila(p, Rect(ladoI.left + w * 0.012f, arriba, ladoI.right - w * 0.006f, piso + 2f * u), 11, ch, fila, w)
        pila(p, Rect(ladoD.left + w * 0.006f, arriba, ladoD.right - w * 0.012f, piso + 2f * u), 13, ch, fila, w)

        // ---- las tablas: la de arriba y la de cada repisa
        val tablas = listOf(Rect(0f, arriba, w, ch)) + d.tablas
        tablas.forEachIndexed { t, r -> tablon(p, Rect(r.left - 1f * u, r.top, r.right + 1f * u, r.bottom), 71 + 7 * t, { x -> k(Offset(x, r.center.y)) }, { x -> fl(Offset(x, r.center.y)) }) }
        // Lo que se deja en los huecos libres, sobre su tabla.
        d.repisas.forEachIndexed { r, rr ->
            val oc = d.ocupado.getOrNull(r) ?: return@forEachIndexed
            val zona = Rect(ladoI.right, rr.top, ladoD.left, rr.bottom)
            val c = Offset((oc + zona.right) / 2, zona.center.y)
            huecoLibre(p, r, zona, oc, k(c), fl(c), { o -> caida(o, 3f * u) })
        }
        // El rótulo de cada mes: un trozo de cinta de pintor pegado en el canto, escrito con plumón.
        d.placas.forEach { pl -> cintaMes(p, pl, medidor, k(pl.r.center), fl(pl.r.center)) }

        // ---- sobre la tabla de arriba, de atrás adelante
        val reglaPie = Offset(w * 0.955f, arriba)
        reglaT(p, reglaPie, Offset(w * 0.875f, w * 0.115f), w, k(Offset(w * 0.92f, w * 0.3f)), fl(Offset(w * 0.92f, w * 0.3f)), caida(Offset(w * 0.92f, w * 0.3f), 4f * u))
        fundaApoyada(p, Rect(w * 0.205f, arriba - w * 0.125f, w * 0.33f, arriba), noche, k(Offset(w * 0.27f, arriba - w * 0.06f)), fl(Offset(w * 0.27f, arriba - w * 0.06f)), caida(Offset(w * 0.27f, arriba - w * 0.06f), 4f * u))
        lampara(p, Offset(w * LAMP_X, arriba), w, caida(Offset(w * LAMP_X, w * 0.4f), 4f * u))
        tocadiscos(p, Rect(w * 0.315f, arriba - w * 0.245f, w * 0.69f, arriba), noche, { o -> k(o) }, { o -> fl(o) }, caida(Offset(w * 0.5f, arriba - w * 0.12f), 5f * u))
        cintaRollo(p, Offset(w * 0.722f, arriba), w * 0.03f, k(Offset(w * 0.72f, arriba - w * 0.03f)), fl(Offset(w * 0.72f, arriba - w * 0.03f)))
        lata(p, Offset(w * 0.79f, arriba), w * 0.062f, w * 0.088f, k(Offset(w * 0.79f, arriba - w * 0.05f)), fl(Offset(w * 0.79f, arriba - w * 0.05f)), caida(Offset(w * 0.79f, arriba - w * 0.05f), 3.5f * u))
        frasco(p, Offset(w * 0.865f, arriba), w * 0.042f, true, k(Offset(w * 0.865f, arriba - w * 0.03f)), fl(Offset(w * 0.865f, arriba - w * 0.03f)), caida(Offset(w * 0.865f, arriba), 3f * u))
        frasco(p, Offset(w * 0.925f, arriba), w * 0.038f, false, k(Offset(w * 0.925f, arriba - w * 0.03f)), fl(Offset(w * 0.925f, arriba - w * 0.03f)), caida(Offset(w * 0.925f, arriba), 3f * u))
        // El pase de autobús, metido en la rendija del canto de la tabla de arriba, a la izquierda.
        paseAutobus(p, Offset(w * 0.03f, arriba + tablaAlto * 0.35f), w * 0.068f, k(Offset(w * 0.05f, arriba)), fl(Offset(w * 0.05f, arriba)))
        // La sombra del canto de la tabla de arriba sobre la primera repisa.
        if (n > 0) drawRect(p.sombra(if (oscuro) 0.4f else 0.24f), Offset(ladoI.right, ch), Size(ladoD.left - ladoI.right, 3f * u))

        // ---- al pie: el suelo de tabla y la caja de leche con discos, cortada por el borde
        suelo(p, d, piso, ::k)
        discosDeCanto(p, Rect(d.izquierda.right + w * 0.02f, d.base.top + d.base.height * 0.08f, w * 0.44f, piso + d.base.height * 0.12f), k(Offset(w * 0.25f, piso)), fl(Offset(w * 0.25f, piso)))
        cajaLeche(p, Rect(w * 0.7f, d.base.top + d.base.height * 0.22f, w * 1.06f, h - d.base.height * 0.06f), k(Offset(w * 0.85f, piso)), fl(Offset(w * 0.85f, piso)))

        // ---- la bombilla, encima de todo, con su color propio; el grano y la viñeta, al final
        if (p.lampara) bombilla(p, w)
        if (p.tinta) granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
        else {
            // El grano de película: el de la lámina de día; de noche, más fino (sobre lo oscuro, el de la lámina se vuelve ruido).
            with(TrazosCiudad) { grano(if (oscuro) TrazosCiudad.GRANO_NOCHE * 0.35f else TrazosCiudad.GRANO_DIA * 0.85f, 29, 1.5f) }
            with(TrazosCiudad) { vineta(if (oscuro) TrazosCiudad.VINETA_NOCHE else TrazosCiudad.VINETA_DIA * 0.7f) }
        }
    }

    // ---------------------------------------------------------------- la pared

    /** El yeso pintado de la pared hasta el suelo, con su moldura de colgar cuadros, sus grietas y los clavos de antes. */
    private fun DrawScope.pared(p: Pintor, d: DisposicionEstante, piso: Float, ch: Float, k: (Offset) -> Float) {
        val u = density; val w = size.width
        // De noche, la pared parte de lo más hondo y la encienden los halos de la luz ([luces]).
        val lejos = if (p.alumbra) 0f else 1f
        val zona = Rect(0f, 0f, w, piso)
        drawRect(p.c(Mat.PARED, lejos), Offset.Zero, zona.size)
        val veces = (piso / (w * 0.5f)).coerceIn(1f, 14f)
        if (!p.tinta) {
            pinceladas(zona, p.con(mezcla(PARED, SOMBRA, 0.35f), lejos), 501, (40 * veces).toInt(), u, 0.09f)
            pinceladas(zona, p.con(mezcla(PARED, AL_SOL, 0.6f), lejos), 502, (30 * veces).toInt(), u, 0.1f)
            pinceladas(zona, p.con(mezcla(PARED, TELA, 0.35f), lejos), 503, (14 * veces).toInt(), u, 0.07f)
        } else if (!p.oscuro) {
            // El papel del cartel: unas fibras apenas.
            pinceladas(zona, mezcla(TrazosTinta.PAPEL, TrazosTinta.GRABAR, 0.12f), 504, (20 * veces).toInt(), u, 0.05f)
        }
        // La luz de la tarde: una mancha clara arriba a la izquierda, y la pared que se va quedando en penumbra a la derecha.
        if (!p.tinta && !p.oscuro) {
            mancha(Offset(w * 0.06f, w * 0.02f), w * 0.95f, AL_SOL.copy(alpha = if (p.noche) 0.18f else 0.6f))
            // La mancha de sol de la tarde, que entra sesgada desde fuera del cuadro y da en la pared, tras la lámpara.
            if (!p.noche) {
                drawPath(poligono(w * 0.0f, w * 0.06f, w * 0.3f, w * 0.06f, w * 0.52f, w * 0.5f, w * 0.12f, w * 0.5f), AL_SOL.copy(alpha = 0.22f))
                drawPath(poligono(w * 0.03f, w * 0.1f, w * 0.24f, w * 0.1f, w * 0.42f, w * 0.46f, w * 0.15f, w * 0.46f), AL_SOL.copy(alpha = 0.16f))
            }
            pinceladas(Rect(w * 0.6f, 0f, w, piso), SOMBRA.copy(alpha = 1f), 505, (16 * veces).toInt(), u, if (p.noche) 0.08f else 0.06f)
        }
        // La moldura de colgar cuadros de la casa victoriana: la cara a la luz, el canto y su sombra en la pared.
        val ym = w * 0.032f
        val mold = Rect(0f, ym, w, ym + 5f * u)
        drawRect(p.sombra(if (p.oscuro) 0.4f else 0.22f), Offset(0f, mold.bottom), Size(w, 3f * u))
        relleno(p, rect(mold), Mat.MOLDURA, k(mold.center))
        drawRect(p.claro(0.5f), mold.topLeft, Size(w, 1.2f * u))
        sombra(p, rect(Rect(0f, mold.bottom - 1.6f * u, w, mold.bottom)), 0.4f)
        filete(p, rect(mold), 0.7f)
        // Los arreglos del yeso, las grietas finas y los agujeros de clavos viejos.
        if (!p.tinta || !p.oscuro) {
            val linea = if (p.tinta) TrazosTinta.GRABAR.copy(alpha = 0.6f) else p.sombra(0.32f)
            fun grieta(desde: Offset, largo: Float, semilla: Int) {
                val g = Path().apply { moveTo(desde.x, desde.y) }
                var q = desde
                for (j in 0 until 7) { q += Offset((azar(semilla, j) - 0.45f) * largo * 0.35f, largo / 7f); g.lineTo(q.x, q.y) }
                drawPath(g, linea, style = Stroke(0.7f * u, cap = StrokeCap.Round))
                if (!p.tinta) drawPath(corrido(g, -0.8f * u, 0f), p.claro(0.25f), style = Stroke(0.5f * u))
            }
            grieta(Offset(w * 0.6f, mold.bottom + 2f * u), w * 0.1f, 511)
            grieta(Offset(w * 0.04f, w * 0.42f), w * 0.12f, 512)
            listOf(Offset(w * 0.56f, w * 0.12f), Offset(w * 0.69f, w * 0.07f), Offset(w * 0.16f, w * 0.1f)).forEach { o ->
                drawCircle(p.sombra(0.5f), 1.1f * u, o)
                drawCircle(p.claro(0.4f), 0.6f * u, o + Offset(0.6f * u, 0.7f * u))
            }
        }
        // El zócalo de madera pintada sobre el suelo.
        val zoc = Rect(0f, piso - w * 0.035f, w, piso)
        relleno(p, rect(zoc), Mat.MOLDURA, k(Offset(w * 0.3f, zoc.center.y)) * 0.8f)
        drawRect(p.claro(0.45f), zoc.topLeft, Size(w, 1.4f * u))
        sombra(p, rect(Rect(0f, zoc.top + 1.4f * u, w, zoc.top + 3.4f * u)), 0.3f)
        filete(p, rect(zoc), 0.7f)
    }

    /**
     * La luz en la pared: de noche en la ciudad, el halo de la lámpara con sus dos conos (arriba y abajo de la pantalla); en
     * la psicodelia clara, los rayos que ondulan desde el disco del tocadiscos, con su trama, y los anillos con la intensidad;
     * en la oscura, las manchas planas de las luces líquidas proyectadas.
     */
    private fun DrawScope.luces(p: Pintor, d: DisposicionEstante, ch: Float) {
        val u = density; val w = size.width
        val centro = Offset(w * DISCO_X, w * DISCO_Y)
        if (p.lampara) {
            val l = Offset(w * LAMP_X, w * LAMP_Y)
            val f = if (p.oscuro) 1f else 0.55f
            mancha(l, w * 1.3f, LAMPARA_LUZ.copy(alpha = 0.16f * f))
            mancha(l, w * 0.62f, LAMPARA_LUZ.copy(alpha = 0.26f * f))
            mancha(l, w * 0.25f, LAMPARA.copy(alpha = 0.32f * f))
            // Los dos conos que deja la pantalla en la pared: el de arriba se abre hasta la moldura; el de abajo, a la tabla.
            val ar = Offset(l.x, l.y - w * 0.07f)
            val cono = poligono(ar.x - w * 0.055f, ar.y, ar.x + w * 0.055f, ar.y, ar.x + w * 0.16f, w * 0.04f, ar.x - w * 0.16f, w * 0.04f)
            drawPath(cono, LAMPARA.copy(alpha = 0.13f * f))
            drawPath(poligono(ar.x - w * 0.05f, ar.y, ar.x + w * 0.05f, ar.y, ar.x + w * 0.11f, w * 0.1f, ar.x - w * 0.11f, w * 0.1f), LAMPARA.copy(alpha = 0.1f * f))
            return
        }
        if (!p.tinta) return
        val m = p.m
        clipRect(0f, w * 0.04f, w, ch) {
            if (!p.oscuro) {
                // Los rayos que ondulan, grabados en la tinta azul negra, como el cartel de una sola tinta.
                val rayos = TrazosTinta.rayos(centro, w * 0.11f, w * 0.9f, 36, (3f + m.amplitud * 0.7f) * u, 2.2f, 0.4f, 0.55f)
                with(TrazosTinta) { trama(rayos, 2.6f * u, 0.85f * u, TrazosTinta.GRABAR.copy(alpha = 0.62f), -20f) }
                drawPath(rayos, TrazosTinta.GRABAR.copy(alpha = 0.5f), style = Stroke(0.6f * u))
            } else {
                // Las manchas de las luces líquidas: tres capas planas de cada una (no degradados), que se enciman y se mezclan.
                p.focos.forEachIndexed { j, f ->
                    if (f.peso <= 0.01f) return@forEachIndexed
                    val c = Offset(f.fx * w, f.fy * w)
                    val r0 = w * (0.17f + 0.07f * p.i) * (if (j == 2) 0.85f else 1f)
                    for (capa in 0 until 3) {
                        val r = r0 * (1.9f - 0.55f * capa)
                        drawPath(gota(c, r, 13 * j + capa, 0.16f + 0.1f * p.i), p.ink(f.tinta).copy(alpha = f.peso * (0.1f + 0.08f * capa + 0.06f * p.i)))
                    }
                }
            }
            // Los anillos alrededor del disco, con la intensidad: alternan las tintas, corridos de registro.
            for (a in 0 until m.anillos) {
                val t = when (a % 3) { 0 -> Tinta.MAGENTA; 1 -> Tinta.ULTRAMAR; else -> if (p.berm > 0.5f) Tinta.BERMELLON else Tinta.MAGENTA }
                val r = w * (0.15f + 0.05f * a)
                with(TrazosTinta) {
                    conRegistro(t, m.registro) {
                        drawPath(gota(centro, r, 70 + a, 0.012f + 0.005f * a), p.ink(t).copy(alpha = if (p.oscuro) 0.6f else 0.8f), style = Stroke(m.grosor * 0.8f * u))
                    }
                }
            }
            // Con mucha intensidad, las bandas que fluyen salen del disco y suben por la pared.
            if (p.i > 0.2f) with(TrazosTinta) {
                conRegistro(Tinta.MAGENTA, m.registro) {
                    bandas(centro, w * 0.9f, (m.bandasPorLado * 0.5f).toInt().coerceAtLeast(1), w * 0.05f, m.amplitud * u * 0.6f, m.grosor * 0.45f * u,
                        p.ink(Tinta.MAGENTA).copy(alpha = 0.45f * ((p.i - 0.2f) / 0.8f).coerceIn(0f, 1f)), 1.2f, 0.1f, 0.9f)
                }
            }
        }
    }

    /** Una mancha de contorno blando y ondulado (las luces líquidas, los anillos): un círculo de radio [r] deformado [onda]. */
    private fun gota(c: Offset, r: Float, semilla: Int, onda: Float): Path = Path().apply {
        val pasos = 48
        val f1 = 2 + Math.floorMod(semilla, 3); val f2 = 5 + Math.floorMod(semilla, 2)
        val a1 = azar(semilla, 1) * 6.28f; val a2 = azar(semilla, 2) * 6.28f
        for (s in 0..pasos) {
            val t = 2f * PI.toFloat() * s / pasos
            val rr = r * (1f + onda * sin(f1 * t + a1) + onda * 0.45f * sin(f2 * t + a2))
            val x = c.x + rr * cos(t); val y = c.y + rr * 0.86f * sin(t)
            if (s == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * El cartel propio de la semana, clavado con cuatro chinchetas (a tres tintas, sin texto legible ni obra reconocible): el
     * marco de arco, el disco como sol con sus rayos que ondulan, las bandas que se abren alrededor, y abajo un título de
     * bloques que llenan su forma. En la ciudad sale con el color de la foto; en los Sueños, el disco es una luna creciente.
     */
    private fun DrawScope.cartel(p: Pintor, r: Rect, medidor: TextMeasurer, suenos: Boolean, k: Float, fl: Color, caida: Offset) {
        val u = density
        val i = p.i
        fun tinta(t: Tinta): Color {
            val base = if (p.tinta) p.ink(t) else TrazosCiudad.pelicula(TrazosTinta.tinta(t, false, 0f))
            return p.con(if (p.tinta) base else mezcla(base, PAPEL, 0.12f), maxOf(k, 0.25f), fl)
        }
        val papel = p.con(if (p.tinta) p.papelObj else mezcla(PAPEL, AL_SOL, 0.3f), maxOf(k, 0.2f), fl)
        val grabar = if (p.tinta) p.con(p.grabado, maxOf(k, 0.35f), fl) else p.con(Color(0xFF2E2840), maxOf(k, 0.2f), fl)
        rotate(-1.6f, r.center) {
            // La sombra del papel en la pared y una esquina que se levanta.
            drawRect(p.sombra(if (p.oscuro) 0.45f else 0.24f), r.topLeft + caida, r.size)
            drawRect(papel, r.topLeft, r.size)
            val marco = Rect(r.left + r.width * 0.07f, r.top + r.width * 0.07f, r.right - r.width * 0.07f, r.bottom - r.height * 0.27f)
            val arco = TrazosTinta.arco(marco, marco.width * 0.42f, 1.5f * u)
            val c = Offset(marco.center.x, marco.top + marco.height * 0.56f)
            clipPath(arco) {
                // Las bandas que se abren alrededor del disco (magenta), y los rayos de una sola tinta que graba.
                with(TrazosTinta) {
                    conRegistro(Tinta.MAGENTA, p.m.registro) {
                        bandas(c, marco.height * 1.15f, 4 + (3 * i).toInt(), marco.width * 0.06f, (2f + 6f * i) * u, (1.4f + i) * u, tinta(Tinta.MAGENTA), 1.3f, 0.2f, 0.9f)
                    }
                }
                val ray = TrazosTinta.rayos(c, marco.width * 0.2f, marco.width * 0.8f, 20, (1.5f + 3f * i) * u, 1.8f)
                drawPath(ray, grabar.copy(alpha = 0.85f))
                if (p.berm > 0f) with(TrazosTinta) {
                    conRegistro(Tinta.BERMELLON, p.m.registro) {
                        drawCircle(tinta(Tinta.BERMELLON).copy(alpha = p.berm), marco.width * 0.27f, c, style = Stroke(marco.width * 0.035f))
                    }
                }
            }
            drawPath(arco, grabar, style = Stroke(1.2f * u))
            // El disco (de noche, la luna) en el centro, en ultramar sobre papel.
            val rd = marco.width * 0.19f
            drawCircle(papel, rd * 1.12f, c)
            if (suenos) {
                drawCircle(tinta(Tinta.ULTRAMAR), rd, c)
                drawCircle(papel, rd * 0.86f, c + Offset(rd * 0.42f, -rd * 0.24f))
            } else {
                drawCircle(tinta(Tinta.ULTRAMAR), rd, c)
                for (j in 1..3) drawCircle(papel.copy(alpha = 0.5f), rd * (1f - 0.18f * j), c, style = Stroke(0.5f * u))
                drawCircle(tinta(Tinta.MAGENTA), rd * 0.36f, c)
                drawCircle(papel, rd * 0.07f, c)
            }
            // El título: bloques que llenan su forma (letras sin leer), en bermellón o magenta, con su sombra corrida.
            val tit = Rect(r.left + r.width * 0.08f, r.bottom - r.height * 0.24f, r.right - r.width * 0.08f, r.bottom - r.height * 0.09f)
            val bloques = 5
            val tinte = if (p.berm > 0.3f) tinta(Tinta.BERMELLON) else tinta(Tinta.MAGENTA)
            for (j in 0 until bloques) {
                val x0 = tit.left + tit.width * j / bloques + 1f * u
                val x1 = tit.left + tit.width * (j + 1) / bloques - 1f * u
                val f = sin(PI.toFloat() * (j + 0.5f) / bloques)
                val alto = tit.height * (0.62f + 0.38f * f)
                val b = Rect(x0, tit.center.y - alto / 2, x1, tit.center.y + alto / 2)
                drawRoundRect(grabar, b.topLeft + Offset(0.9f * u, 0.9f * u), b.size, CornerRadius(1.6f * u))
                drawRoundRect(tinte, b.topLeft, b.size, CornerRadius(1.6f * u))
                // El hueco casi cerrado de la letra de bloque.
                drawRect(papel, Offset(b.left + b.width * (0.3f + 0.2f * azar(j, 3)), b.top + b.height * 0.3f), Size(b.width * 0.18f, b.height * 0.36f))
            }
            // Una línea de letra chica, en trazos (no se lee).
            for (j in 0 until 2) drawLine(grabar.copy(alpha = 0.7f), Offset(r.left + r.width * (0.2f + 0.05f * j), r.bottom - r.height * (0.055f - 0.03f * j)),
                Offset(r.right - r.width * (0.2f + 0.08f * j), r.bottom - r.height * (0.055f - 0.03f * j)), 0.8f * u)
            filete(p, rect(r), 0.8f)
            // El papel de cartel: un poco de luz arriba a la izquierda, sombra a la derecha, y la esquina que se levanta.
            if (!p.tinta) {
                drawRect(p.claro(0.2f), r.topLeft, Size(r.width * 0.4f, r.height))
                drawRect(p.sombra(0.12f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
            } else sombra(p, rect(Rect(r.right - r.width * 0.08f, r.top, r.right, r.bottom)), 0.25f)
            val esq = poligono(r.right, r.bottom - r.width * 0.14f, r.right, r.bottom, r.right - r.width * 0.14f, r.bottom)
            drawPath(esq, p.c(Mat.PARED, 0.1f))
            val doblez = poligono(r.right, r.bottom - r.width * 0.14f, r.right - r.width * 0.14f, r.bottom, r.right - r.width * 0.1f, r.bottom - r.width * 0.1f)
            drawPath(doblez, mezcla(papel, p.hondo, 0.22f)); filete(p, doblez, 0.6f)
            // Las chinchetas.
            listOf(r.topLeft + Offset(4f * u, 4f * u), r.topRight + Offset(-4f * u, 4f * u), r.bottomLeft + Offset(4f * u, -4f * u)).forEachIndexed { j, q ->
                drawCircle(p.sombra(0.4f), 2.2f * u, q + Offset(0.8f * u, 1.1f * u))
                drawCircle(if (p.tinta) tinta(if (j == 1) Tinta.ULTRAMAR else Tinta.MAGENTA) else p.con(listOf(ROJO, OCRE, VERDE)[j], maxOf(k, 0.2f), fl), 2.1f * u, q)
                drawCircle(Color.White.copy(alpha = if (p.oscuro) 0.2f else 0.55f), 0.7f * u, q - Offset(0.6f * u, 0.6f * u))
            }
        }
    }

    /**
     * Un boceto a lápiz en papel de dibujo, pegado en la pared con dos trozos de cinta de pintor: una victoriana con su
     * mirador y su cornisa, los postes y los cables (la calle de David), con unas líneas de construcción.
     */
    private fun DrawScope.boceto(p: Pintor, r: Rect, k: Float, fl: Color, caida: Offset) {
        val u = density
        rotate(2.5f, r.center) {
            drawRect(p.sombra(if (p.oscuro) 0.4f else 0.2f), r.topLeft + caida, r.size)
            val papel = p.con(if (p.tinta) p.papelObj else Color(0xFFEFE8D8), maxOf(k, 0.18f), fl)
            drawRect(papel, r.topLeft, r.size)
            val lapiz = (if (p.tinta) p.con(if (p.oscuro) TrazosTinta.NEGRO else p.grabado, maxOf(k, 0.3f), fl) else p.con(Color(0xFF5A544E), maxOf(k, 0.2f), fl)).copy(alpha = 0.8f)
            val g = 0.8f * u
            fun l(x0: Float, y0: Float, x1: Float, y1: Float) = drawLine(lapiz, Offset(r.left + r.width * x0, r.top + r.height * y0), Offset(r.left + r.width * x1, r.top + r.height * y1), g)
            // La casa: el frontón, la cornisa, el mirador de tres ventanas y la puerta con su escalera.
            l(0.22f, 0.32f, 0.52f, 0.14f); l(0.52f, 0.14f, 0.82f, 0.32f)
            l(0.2f, 0.33f, 0.84f, 0.33f); l(0.2f, 0.36f, 0.84f, 0.36f)
            l(0.24f, 0.36f, 0.24f, 0.9f); l(0.8f, 0.36f, 0.8f, 0.9f)
            l(0.3f, 0.44f, 0.56f, 0.44f); l(0.27f, 0.47f, 0.27f, 0.7f); l(0.59f, 0.47f, 0.59f, 0.7f); l(0.27f, 0.7f, 0.59f, 0.7f)
            l(0.36f, 0.47f, 0.36f, 0.7f); l(0.5f, 0.47f, 0.5f, 0.7f)
            l(0.64f, 0.56f, 0.76f, 0.56f); l(0.64f, 0.56f, 0.64f, 0.9f); l(0.76f, 0.56f, 0.76f, 0.9f)
            for (j in 0 until 3) l(0.6f - 0.03f * j, 0.9f + 0.025f * j, 0.8f + 0.03f * j, 0.9f + 0.025f * j)
            // El sombreado a la derecha, en rayitas, y el poste con su cable.
            for (j in 0 until 7) l(0.68f + 0.016f * j, 0.4f, 0.64f + 0.016f * j, 0.52f)
            l(0.08f, 0.1f, 0.08f, 0.95f); l(0.0f, 0.2f, 0.4f, 0.08f)
            // Las líneas de construcción, muy tenues.
            drawLine(lapiz.copy(alpha = 0.25f), Offset(r.left, r.top + r.height * 0.9f), Offset(r.right, r.top + r.height * 0.9f), 0.5f * u)
            drawLine(lapiz.copy(alpha = 0.25f), Offset(r.left + r.width * 0.52f, r.top), Offset(r.left + r.width * 0.52f, r.bottom), 0.5f * u)
            if (!p.tinta) drawRect(p.sombra(0.1f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
            filete(p, rect(r), 0.6f)
            // Los dos trozos de cinta que lo sostienen.
            listOf(Offset(r.left + r.width * 0.12f, r.top), Offset(r.right - r.width * 0.12f, r.top)).forEachIndexed { j, c ->
                rotate(if (j == 0) -20f else 16f, c) {
                    val t = Rect(c.x - 9f * u, c.y - 3.5f * u, c.x + 9f * u, c.y + 3.5f * u)
                    drawRect(p.con(if (p.tinta) p.papelObj else Color(0xFFE2CC92), maxOf(k, 0.25f), fl).copy(alpha = 0.92f), t.topLeft, t.size)
                    filete(p, rect(t), 0.5f)
                }
            }
            filoLinea(p, r.topLeft, r.bottomLeft, k, fl, 0.8f)
        }
    }

    /** Los discos de canto en el suelo, bajo la última tabla, apoyados contra la pared y unos sobre otros, ladeados. */
    private fun DrawScope.discosDeCanto(p: Pintor, r: Rect, k: Float, fl: Color) {
        val u = density
        val alto = r.height
        val mats = listOf(Mat.TELA, Mat.NARANJA, Mat.PAPEL, Mat.VINILO, Mat.OCRE, Mat.ROJO, Mat.CARTON, Mat.VERDE, Mat.PAPEL)
        drawRect(p.sombra(if (p.oscuro) 0.45f else 0.28f), Offset(r.left, r.bottom - 3f * u), Size(r.width + 8f * u, 4f * u))
        // Apretados, ladeados hacia la pila de ladrillo de la izquierda, en la que se apoyan.
        val n = 11
        val g = 5.2f * u
        for (j in n - 1 downTo 0) {
            val x = r.left + 3f * u + j * g * 1.12f
            val giro = -(12f - 0.5f * j) - 2f * azar(j, 81)
            val lado = alto * (0.9f + 0.1f * azar(j, 82))
            val pie = Offset(x + g, r.bottom)
            rotate(giro, pie) {
                val f = Rect(pie.x - g, pie.y - lado, pie.x, pie.y)
                // El canto de cada funda, con su lomo impreso y una franja de papel.
                relleno(p, rect(f), mats[j % mats.size], k, fl)
                drawRect(p.sombra(0.4f), Offset(f.right - 1.2f * u, f.top), Size(1.2f * u, f.height))
                if (!p.tinta) drawRect(p.claro(0.25f), f.topLeft, Size(f.width * 0.25f, f.height))
                drawRect(p.c(Mat.PAPEL, k, fl).copy(alpha = 0.7f), Offset(f.left + 1f * u, f.top + f.height * 0.18f), Size(f.width * 0.5f, f.height * 0.22f))
                filete(p, rect(f), 0.5f)
            }
        }
        // La última, de frente: la funda con la portada del disco del día a dos tintas, apoyada sobre las demás.
        val x0 = r.left + 3f * u + n * 5.2f * u * 1.12f
        val ultima = Rect(x0, r.bottom - alto * 0.95f, x0 + alto * 0.95f, r.bottom)
        rotate(-10f, Offset(ultima.left, ultima.bottom)) {
            drawRect(p.sombra(0.3f), ultima.topLeft + Offset(5f * u, 2f * u), ultima.size)
            relleno(p, rect(ultima), Mat.NARANJA, k, fl)
            val c = Offset(ultima.center.x, ultima.top + ultima.height * 0.45f)
            for (j in 0 until 3) drawCircle(p.c(Mat.PAPEL, k, fl).copy(alpha = 0.7f), ultima.width * (0.38f - 0.1f * j), c, style = Stroke(1.6f * u))
            drawCircle(p.c(Mat.VINILO, k, fl), ultima.width * 0.1f, c)
            if (!p.tinta) drawRect(p.sombra(0.18f), Offset(ultima.right - ultima.width * 0.2f, ultima.top), Size(ultima.width * 0.2f, ultima.height))
            else sombra(p, rect(Rect(ultima.right - ultima.width * 0.15f, ultima.top, ultima.right, ultima.bottom)), 0.3f)
            filete(p, rect(ultima), 0.7f)
            filoLinea(p, ultima.topLeft, ultima.topRight, k, fl)
        }
    }

    // ---------------------------------------------------------------- el cuerpo del mueble

    /** El fondo de una repisa: la pared en sombra, con la sombra de la tabla de arriba, la luz por la izquierda y el lado derecho más hondo. */
    private fun DrawScope.fondoRepisa(p: Pintor, z: Rect, r: Int, k: Float, fl: Color) {
        val u = density
        drawRect(p.c(Mat.PARED, k * 0.7f, fl), z.topLeft, z.size)
        if (p.tinta && p.oscuro) {
            // El reflejo de las luces líquidas que llega hasta aquí, una mancha plana del color del foco.
            drawPath(gota(Offset(z.left + z.width * (0.3f + 0.4f * azar(r, 5)), z.center.y), z.height * 0.9f, 31 + r, 0.12f), fl.copy(alpha = 0.14f * k))
        }
        if (p.tinta && !p.oscuro) {
            sombra(p, rect(Rect(z.left, z.top, z.right, z.top + 14f * u)), 0.55f)
            sombra(p, rect(Rect(z.left, z.top + 14f * u, z.right, z.top + 30f * u)), 0.15f)
            sombra(p, rect(Rect(z.right - z.width * 0.2f, z.top, z.right, z.bottom)), 0.2f)
            return
        }
        drawRect(p.sombra(if (p.oscuro) 0.3f else 0.2f), z.topLeft, z.size)
        drawRect(p.sombra(0.32f), z.topLeft, Size(z.width, 7f * u))
        drawRect(p.sombra(0.18f), z.topLeft, Size(z.width, 16f * u))
        drawRect(p.sombra(0.1f), z.topLeft, Size(z.width, 30f * u))
        if (!p.oscuro) {
            pinceladas(Rect(z.left, z.top + z.height * 0.3f, z.left + z.width * 0.6f, z.bottom), AL_SOL, 601 + r, 14, u, if (p.noche) 0.04f else 0.08f)
        }
        pinceladas(Rect(z.right - z.width * 0.34f, z.top, z.right, z.bottom), p.hondo, 611 + r, 18, u, 0.07f)
        drawRect(p.sombra(0.14f), Offset(z.right - 12f * u, z.top), Size(12f * u, z.height))
        drawRect(p.sombra(0.22f), Offset(z.left, z.top), Size(6f * u, z.height))
    }

    /**
     * Una pila de ladrillos en [r], de las que sostienen las tablas: cada ladrillo de su tono, con su arista de luz arriba, la
     * junta en sombra, desconchones y un poco corrido, porque se apilaron a mano. De noche, cada uno con la luz que le toca.
     */
    private fun DrawScope.pila(p: Pintor, r: Rect, semilla: Int, ch: Float, fila: Float, w: Float) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        val alto = maxOf(r.width * 0.44f, 4f * u)
        var y = r.bottom; var j = 0
        clipRect(r.left - 4f * u, r.top, r.right + 4f * u, r.bottom) {
            // Detrás, la pared en sombra entre ladrillo y ladrillo (no hay mortero: se apilaron en seco).
            drawRect(p.c(Mat.PARED, 0.05f), r.topLeft, r.size)
            drawRect(p.sombra(if (p.oscuro) 0.5f else 0.42f), r.topLeft, r.size)
            while (y > r.top - alto) {
                val corre = (azar(semilla, j) - 0.5f) * 3.5f * u
                val b = Rect(r.left + corre, y - alto + 1.2f * u, r.right + corre, y - 0.6f * u)
                val fyB = if (b.center.y <= ch) b.center.y / w else fyCuerpo((b.center.y - ch) / fila)
                val k = p.luz(b.center.x / w, fyB)
                val fl = p.filo(b.center.x / w, fyB)
                val forma = redondo(b, 1.3f * u)
                // La junta: el mortero que no hay, la sombra entre ladrillo y ladrillo.
                drawRect(p.sombra(0.5f), Offset(b.left, b.bottom), Size(b.width, 0.6f * u))
                val alterno = p.tinta && p.berm > 0f && Math.floorMod(j + semilla, 4) == 0
                if (alterno) {
                    relleno(p, forma, Mat.LADRILLO, k, fl)
                    with(TrazosTinta) { conRegistro(Tinta.BERMELLON, p.m.registro) { drawPath(forma, p.con(p.ink(Tinta.BERMELLON), k, fl).copy(alpha = 0.7f * p.berm)) } }
                } else if (p.tinta) relleno(p, forma, Mat.LADRILLO, k, fl)
                else drawPath(forma, p.con(mezcla(LADRILLO, listOf(Color(0xFF5E2A1E), Color(0xFF94523A), Color(0xFF6E3222), Color(0xFF8A4632))[j % 4], 0.3f + 0.4f * azar(semilla, j + 11)), k, fl))
                if (!p.tinta) {
                    // La arcilla: granos y la cara de arriba a la luz.
                    for (q in 0 until 5) drawCircle((if (q % 2 == 0) p.hondo else AL_SOL).copy(alpha = 0.16f), (0.5f + 0.8f * azar(semilla, j * 7 + q)) * u,
                        Offset(b.left + b.width * azar(semilla, j * 7 + q + 3), b.top + b.height * azar(semilla, j * 7 + q + 5)))
                    drawRect(p.claro(0.32f), Offset(b.left + 1f * u, b.top), Size(b.width - 2f * u, 1.2f * u))
                    drawRect(p.sombra(0.3f), Offset(b.left, b.bottom - 1.8f * u), Size(b.width, 1.8f * u))
                    drawRect(p.sombra(0.22f), Offset(b.right - b.width * 0.22f, b.top), Size(b.width * 0.22f, b.height))
                } else {
                    sombra(p, rect(Rect(b.right - b.width * 0.3f, b.top, b.right, b.bottom)), 0.35f)
                    filete(p, forma, 0.7f)
                }
                filoLinea(p, Offset(b.left + 1f * u, b.top + 0.6f * u), Offset(b.right - 1f * u, b.top + 0.6f * u), k, fl, 0.9f)
                // Un desconchón en una esquina.
                if (azar(semilla, j + 40) > 0.55f) {
                    val izq = azar(semilla, j + 41) > 0.5f
                    val e = if (izq) b.topLeft else b.topRight
                    val s = if (izq) 1f else -1f
                    drawPath(poligono(e.x, e.y, e.x + s * 4.5f * u, e.y, e.x, e.y + 3f * u), p.c(Mat.PARED, k * 0.5f, fl))
                }
                y -= alto; j++
            }
        }
    }

    /**
     * Una tabla de pino en [cara]: la cara de arriba vista apenas, el canto con su veta y sus nudos, su luz y su sombra debajo.
     * De noche se pinta por tramos, cada uno con la luz que le llega.
     */
    private fun DrawScope.tablon(p: Pintor, cara: Rect, semilla: Int, k: (Float) -> Float, fl: (Float) -> Color) {
        val u = density
        val fondo = 3f * u
        // La sombra que echa abajo, en la pared o en la repisa.
        drawRect(p.sombra(if (p.oscuro) 0.45f else 0.28f), Offset(cara.left + 2f * u, cara.bottom), Size(cara.width - 2f * u, 4f * u))
        val tramos = if (p.alumbra) 26 else 1
        for (t in 0 until tramos) {
            val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
            val kk = k((xa + xb) / 2); val f = fl((xa + xb) / 2)
            drawRect(p.con(if (p.tinta) p.papelObj else mezcla(PINO, AL_SOL, 0.3f), kk, f), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
            drawRect(p.c(Mat.PINO, kk, f), Offset(xa, cara.top), Size(xb - xa, cara.height))
            if (p.alumbra) drawRect(f.copy(alpha = (0.06f + 0.6f * kk).coerceAtMost(0.7f)), Offset(xa, cara.top - fondo), Size(xb - xa, 1.1f * u))
        }
        val veta = if (p.tinta) p.grabado.copy(alpha = if (p.oscuro) 0.25f else 0.5f) else MADERA.copy(alpha = if (p.oscuro) 0.45f else 0.42f)
        clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
            vetas(cara, veta, semilla, densidad = 0.7f, grosor = 0.7f, ondulacion = 0.6f)
            // Un nudo y una mancha de tinta vieja.
            val nx = cara.left + cara.width * (0.15f + 0.7f * azar(semilla, 7))
            val nr = cara.height * 0.2f
            drawOval(veta.copy(alpha = 0.6f), Offset(nx - nr * 1.3f, cara.center.y - nr * 0.6f), Size(2.6f * nr, 1.2f * nr))
            drawOval(veta.copy(alpha = 0.35f), Offset(nx - nr * 2.2f, cara.center.y - nr), Size(4.4f * nr, 2f * nr), style = Stroke(0.6f * u))
            val mx = cara.left + cara.width * (0.1f + 0.8f * azar(semilla, 9))
            drawOval((if (p.tinta) p.ink(Tinta.ULTRAMAR) else TINTA_C).copy(alpha = 0.3f), Offset(mx, cara.top + cara.height * 0.25f), Size(cara.height * 0.8f, cara.height * 0.45f))
        }
        if (!p.tinta) {
            drawRect(p.claro(0.45f), cara.topLeft, Size(cara.width, 1.1f * u))
            drawRect(p.sombra(0.32f), Offset(cara.left, cara.bottom - 2f * u), Size(cara.width, 2f * u))
        } else {
            sombra(p, rect(Rect(cara.left, cara.bottom - cara.height * 0.32f, cara.right, cara.bottom)), 0.45f)
            filete(p, rect(Rect(cara.left, cara.top - fondo, cara.right, cara.bottom)), 0.9f)
            drawLine(if (p.oscuro) TrazosTinta.NEGRO else TrazosTinta.GRABAR, Offset(cara.left, cara.top), Offset(cara.right, cara.top), 0.7f * u)
        }
        // Las puntas cortadas a serrucho.
        drawLine(p.sombra(0.4f), Offset(cara.left + 1f * u, cara.top - fondo), Offset(cara.left + 1f * u, cara.bottom), 0.8f * u)
        drawLine(p.sombra(0.4f), Offset(cara.right - 1f * u, cara.top - fondo), Offset(cara.right - 1f * u, cara.bottom), 0.8f * u)
    }

    /** El rótulo del mes: un trozo de cinta de pintor pegado en el canto, con las puntas rotas, escrito con plumón. */
    private fun DrawScope.cintaMes(p: Pintor, pl: PlacaEstante, medidor: TextMeasurer, k: Float, fl: Color) {
        val u = density
        val r = Rect(pl.r.left, pl.r.top - 1f * u, pl.r.right, pl.r.bottom + 1f * u)
        val dientes = 5
        val forma = Path().apply {
            moveTo(r.left, r.top)
            lineTo(r.right, r.top + 0.5f * u)
            for (j in 0..dientes) lineTo(r.right + (if (j % 2 == 0) 0f else -1.6f * u), r.top + r.height * j / dientes)
            lineTo(r.left, r.bottom - 0.4f * u)
            for (j in dientes downTo 0) lineTo(r.left + (if (j % 2 == 0) 0f else 1.6f * u), r.top + r.height * j / dientes)
            close()
        }
        rotate(-0.8f, r.center) {
            drawPath(corrido(forma, 1f * u, 1.4f * u), p.sombra(0.35f))
            val cinta = if (p.tinta) p.etiqueta(maxOf(k, 0.4f)) else mezcla(p.etiqueta(maxOf(k, 0.4f)), Color(0xFFE2CC92), 0.45f)
            drawPath(forma, cinta)
            clipPath(forma) {
                if (!p.tinta) pinceladas(r, OCRE, 621 + pl.texto.length, 6, u, 0.07f)
                drawRect(p.sombra(0.1f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
            }
            filete(p, forma, 0.6f)
            textoEn(medidor, pl.texto, Rect(r.left + 7f * u, r.top + 1.5f * u, r.right - 7f * u, r.bottom - 1.5f * u), ROTULO, p.letra, 10f, peso = FontWeight.Medium, espaciado = 0.4f, minimo = 5f)
        }
    }

    /** El pase de autobús metido en la rendija del canto: la cartulina con su franja, sus perforaciones y unos renglones. */
    private fun DrawScope.paseAutobus(p: Pintor, pie: Offset, an: Float, k: Float, fl: Color) {
        val u = density
        val alto = an * 0.62f
        rotate(-9f, pie) {
            val r = Rect(pie.x - an * 0.15f, pie.y - alto, pie.x + an * 0.85f, pie.y)
            drawRect(p.sombra(0.3f), r.topLeft + Offset(1.4f * u, 1.4f * u), r.size)
            drawRect(p.c(Mat.PAPEL, maxOf(k, 0.3f), fl), r.topLeft, r.size)
            drawRect(p.c(Mat.VERDE, maxOf(k, 0.3f), fl), r.topLeft, Size(r.width, alto * 0.28f))
            for (j in 0 until 3) drawLine(p.c(Mat.VINILO, 1f).copy(alpha = 0.5f), Offset(r.left + an * 0.08f, r.top + alto * (0.48f + 0.17f * j)), Offset(r.left + an * (0.6f - 0.12f * j), r.top + alto * (0.48f + 0.17f * j)), 0.7f * u)
            // Las perforaciones del revisor.
            for (j in 0 until 2) drawCircle(p.c(Mat.PINO, k * 0.6f, fl), 1.1f * u, Offset(r.right - an * (0.12f + 0.14f * j), r.top + alto * 0.62f))
            filete(p, rect(r), 0.6f)
            if (!p.tinta) drawRect(p.claro(0.3f), r.topLeft, Size(r.width, 0.9f * u))
        }
    }

    // ---------------------------------------------------------------- lo que hay sobre la tabla de arriba

    /** La regla T apoyada contra la pared: la hoja larga de [pie] (en la tabla) a [tope], y el cabezal, cortado por el borde. */
    private fun DrawScope.reglaT(p: Pintor, pie: Offset, tope: Offset, w: Float, k: Float, fl: Color, caida: Offset) {
        val u = density
        val ancho = w * 0.024f
        val v = tope - pie; val largo = v.getDistance()
        val ang = Math.toDegrees(kotlin.math.atan2(v.y.toDouble(), v.x.toDouble())).toFloat() + 90f
        rotate(ang, pie) {
            val hoja = Rect(pie.x - ancho / 2, pie.y - largo, pie.x + ancho / 2, pie.y)
            val cabezal = Rect(pie.x - ancho * 2.2f, pie.y - largo - ancho * 1.2f, pie.x + ancho * 5.5f, pie.y - largo + ancho * 0.2f)
            translate(caida.x, caida.y) { drawRect(p.sombra(if (p.oscuro) 0.35f else 0.2f), hoja.topLeft, hoja.size); drawRect(p.sombra(if (p.oscuro) 0.35f else 0.2f), cabezal.topLeft, cabezal.size) }
            relleno(p, rect(hoja), Mat.PINO, k, fl)
            // El filo de celuloide transparente, y las marcas de las pulgadas.
            drawRect(p.c(Mat.PAPEL, k, fl).copy(alpha = 0.7f), Offset(hoja.left, hoja.top), Size(ancho * 0.22f, largo))
            for (j in 0 until (largo / (6f * u)).toInt().coerceAtMost(60)) drawLine(p.sombra(0.5f), Offset(hoja.left, hoja.bottom - j * 6f * u), Offset(hoja.left + ancho * (if (j % 4 == 0) 0.45f else 0.25f), hoja.bottom - j * 6f * u), 0.5f * u)
            relleno(p, rect(cabezal), Mat.MADERA, k, fl)
            if (!p.tinta) {
                drawRect(p.sombra(0.22f), Offset(hoja.right - ancho * 0.3f, hoja.top), Size(ancho * 0.3f, largo))
                drawRect(p.claro(0.35f), cabezal.topLeft, Size(cabezal.width, 1.2f * u))
            } else { sombra(p, rect(Rect(hoja.right - ancho * 0.35f, hoja.top, hoja.right, hoja.bottom)), 0.4f); filete(p, rect(hoja), 0.7f); filete(p, rect(cabezal), 0.7f) }
            drawCircle(p.c(Mat.METAL, k, fl), 1.4f * u, Offset(cabezal.left + ancho * 0.9f, cabezal.center.y))
            drawCircle(p.c(Mat.METAL, k, fl), 1.4f * u, Offset(cabezal.left + ancho * 3.6f, cabezal.center.y))
            filoLinea(p, hoja.topLeft, hoja.bottomLeft, k, fl, 0.8f)
        }
    }

    /**
     * La funda del disco que suena, apoyada contra la pared junto a la lámpara: la portada con el disco del día como sol y sus
     * anillos (la misma de la tapa del diario, en chico); en los Sueños, la luna.
     */
    private fun DrawScope.fundaApoyada(p: Pintor, r: Rect, suenos: Boolean, k: Float, fl: Color, caida: Offset) {
        val u = density
        val lado = r.height
        val f = Rect(r.left, r.bottom - lado, r.left + lado, r.bottom)
        rotate(-7f, Offset(f.left, f.bottom)) {
            drawRect(p.sombra(if (p.oscuro) 0.42f else 0.24f), f.topLeft + caida, f.size)
            val fondo = if (p.tinta) p.con(p.ink(Tinta.ULTRAMAR), k, fl) else p.con(TrazosCiudad.pelicula(Color(0xFF3F4C9E)), k, fl)
            val anillo = if (p.tinta) p.con(p.ink(Tinta.MAGENTA), k, fl) else p.con(TrazosCiudad.pelicula(Color(0xFFA94870)), k, fl)
            val papel = p.c(Mat.PAPEL, k, fl)
            drawRect(fondo, f.topLeft, f.size)
            val c = Offset(f.center.x, f.top + f.height * 0.44f)
            clipRect(f.left, f.top, f.right, f.bottom) {
                for (j in 0 until 6) drawPath(gota(c, f.width * (0.62f - 0.08f * j), 40 + j, 0.05f), if (j % 2 == 0) anillo else fondo)
            }
            drawCircle(papel, f.width * 0.17f, c)
            if (suenos) drawCircle(fondo, f.width * 0.14f, c + Offset(f.width * 0.06f, -f.width * 0.04f))
            else drawCircle(anillo, f.width * 0.05f, c)
            drawRect(papel, Offset(f.left, f.bottom - f.height * 0.16f), Size(f.width, f.height * 0.16f))
            for (j in 0 until 2) drawLine(p.c(Mat.VINILO, maxOf(k, 0.4f), fl).copy(alpha = 0.6f), Offset(f.left + f.width * 0.12f, f.bottom - f.height * (0.11f - 0.05f * j)), Offset(f.right - f.width * (0.25f + 0.2f * j), f.bottom - f.height * (0.11f - 0.05f * j)), 0.7f * u)
            if (!p.tinta) {
                drawRect(p.claro(0.18f), f.topLeft, Size(f.width * 0.3f, f.height))
                drawRect(p.sombra(0.2f), Offset(f.right - f.width * 0.22f, f.top), Size(f.width * 0.22f, f.height))
            } else sombra(p, rect(Rect(f.right - f.width * 0.12f, f.top, f.right, f.bottom)), 0.3f)
            filete(p, rect(f), 0.8f)
            filoLinea(p, f.topLeft, f.topRight, k, fl)
        }
    }

    /**
     * La lámpara de pantalla, de pie en [pie] sobre la tabla: el pie redondo de metal, la varilla y la pantalla plisada de
     * tambor. Apagada de día; encendida, la pantalla se vuelve de luz y alumbra la pared (sus conos los pinta [luces]).
     */
    private fun DrawScope.lampara(p: Pintor, pie: Offset, w: Float, caida: Offset) {
        val u = density
        val k = p.luz(pie.x / w, LAMP_Y)
        val fl = p.filo(pie.x / w, LAMP_Y)
        val bombilla = Offset(w * LAMP_X, w * LAMP_Y)
        val arriba = bombilla.y - w * 0.075f; val abajo = bombilla.y + w * 0.035f
        val pantalla = poligono(bombilla.x - w * 0.055f, arriba, bombilla.x + w * 0.055f, arriba, bombilla.x + w * 0.085f, abajo, bombilla.x - w * 0.085f, abajo)
        // La sombra en la pared (de día).
        if (!p.alumbra) {
            translate(caida.x, caida.y) { drawPath(pantalla, p.sombra(0.2f)) }
            drawLine(p.sombra(0.2f), Offset(pie.x + caida.x, abajo + caida.y), Offset(pie.x + caida.x, pie.y - 4f * u), 2f * u)
        }
        // El pie, la varilla y la rosca.
        val metal = p.c(Mat.METAL, if (p.lampara) 0.9f else k, fl)
        drawOval(p.sombra(0.35f), Offset(pie.x - w * 0.05f, pie.y - 2.5f * u), Size(w * 0.11f, 5f * u))
        val base = Path().apply { addOval(Rect(pie.x - w * 0.042f, pie.y - w * 0.022f, pie.x + w * 0.042f, pie.y + 1f * u)) }
        drawPath(base, metal)
        if (!p.tinta) drawPath(borde(base, -w * 0.012f, 0f), p.sombra(0.3f)) else sombra(p, borde(base, -w * 0.02f, 0f), 0.4f)
        filete(p, base, 0.7f)
        drawLine(metal, Offset(pie.x, pie.y - w * 0.015f), Offset(pie.x, abajo), 2.2f * u)
        drawLine(p.claro(0.5f), Offset(pie.x - 0.6f * u, pie.y - w * 0.015f), Offset(pie.x - 0.6f * u, abajo), 0.6f * u)
        if (p.tinta) filete(p, rect(Rect(pie.x - 1.1f * u, abajo, pie.x + 1.1f * u, pie.y - w * 0.015f)), 0.5f)
        // La pantalla plisada.
        val tela = when {
            p.lampara -> LAMPARA
            else -> p.c(Mat.PANTALLA, k, fl)
        }
        drawPath(pantalla, tela)
        clipPath(pantalla) {
            val pliegues = 14
            for (j in 0 until pliegues) {
                val f = j / pliegues.toFloat()
                val xa = bombilla.x - w * 0.085f + w * 0.17f * f; val xt = bombilla.x - w * 0.055f + w * 0.11f * f
                val col = if (p.lampara) LAMPARA_LUZ.copy(alpha = 0.45f) else if (p.tinta) (if (p.oscuro) TrazosTinta.NEGRO else TrazosTinta.GRABAR).copy(alpha = 0.6f) else p.sombra(0.16f)
                drawLine(col, Offset(xt, arriba), Offset(xa, abajo), (if (j % 2 == 0) 1.2f else 0.6f) * u)
            }
            if (p.lampara) {
                // Encendida: más clara en el medio, donde está la bombilla, y en el borde de abajo.
                drawOval(Color(0xFFFFF0D2).copy(alpha = 0.55f), Offset(bombilla.x - w * 0.05f, bombilla.y - w * 0.04f), Size(w * 0.1f, w * 0.09f))
                drawRect(LAMPARA_LUZ.copy(alpha = 0.35f), Offset(bombilla.x - w * 0.09f, abajo - 2.5f * u), Size(w * 0.18f, 2.5f * u))
            } else if (!p.tinta) {
                drawRect(p.claro(0.3f), Offset(bombilla.x - w * 0.09f, arriba), Size(w * 0.05f, abajo - arriba))
                drawRect(p.sombra(0.2f), Offset(bombilla.x + w * 0.035f, arriba), Size(w * 0.06f, abajo - arriba))
            } else sombra(p, poligono(bombilla.x + w * 0.02f, arriba, bombilla.x + w * 0.055f, arriba, bombilla.x + w * 0.085f, abajo, bombilla.x + w * 0.035f, abajo), 0.4f)
        }
        // Los ribetes de arriba y de abajo.
        drawLine(if (p.lampara) Color(0xFFB0703A) else p.c(Mat.NARANJA, k, fl), Offset(bombilla.x - w * 0.055f, arriba + 0.8f * u), Offset(bombilla.x + w * 0.055f, arriba + 0.8f * u), 1.6f * u)
        drawLine(if (p.lampara) Color(0xFFB0703A) else p.c(Mat.NARANJA, k, fl), Offset(bombilla.x - w * 0.085f, abajo - 0.8f * u), Offset(bombilla.x + w * 0.085f, abajo - 0.8f * u), 1.6f * u)
        filete(p, pantalla, 0.9f)
    }

    /** La bombilla, que asoma bajo la pantalla encendida, con su propio color. */
    private fun DrawScope.bombilla(p: Pintor, w: Float) {
        val b = Offset(w * LAMP_X, w * LAMP_Y + w * 0.04f)
        drawCircle(Color(0xFFFFF4DC), w * 0.016f, b)
        drawCircle(Color(0xFFFFF4DC).copy(alpha = 0.3f), w * 0.03f, b)
    }

    /**
     * El tocadiscos de maleta (el foco del estante), en [r]: la caja forrada con su rejilla y su perilla, la tapa levantada
     * detrás con el parlante, el plato con un disco a medio tocar (el brazo a la mitad del surco) y las esquineras de metal.
     */
    private fun DrawScope.tocadiscos(p: Pintor, r: Rect, suenos: Boolean, k: (Offset) -> Float, fl: (Offset) -> Color, caida: Offset) {
        val u = density
        val cajaAlto = r.height * 0.36f
        val caja = Rect(r.left, r.bottom - cajaAlto, r.right, r.bottom)
        val tapa = Rect(r.left + r.width * 0.02f, r.top, r.right - r.width * 0.02f, caja.top + cajaAlto * 0.15f)
        val kt = k(tapa.center); val ft = fl(tapa.center)
        val kc = k(caja.center); val fc = fl(caja.center)
        // Las sombras en la pared y en la tabla.
        translate(caida.x, caida.y) { drawRoundRect(p.sombra(if (p.oscuro) 0.42f else 0.24f), tapa.topLeft, Size(tapa.width, r.height), CornerRadius(4f * u)) }
        drawOval(p.sombra(0.4f), Offset(caja.left, caja.bottom - 3f * u), Size(caja.width + 6f * u, 6f * u))
        // La tapa levantada: el forro por fuera, el forro claro por dentro y el parlante.
        val formaTapa = redondo(tapa, 4f * u)
        relleno(p, formaTapa, Mat.TELA, kt, ft)
        val dentro = tapa.deflate(5f * u)
        val formaDentro = redondo(dentro, 3f * u)
        drawPath(formaDentro, if (p.tinta) p.c(Mat.PAPEL, kt, ft) else p.con(mezcla(TELA, AL_SOL, 0.45f), kt, ft))
        val rej = Rect(dentro.center.x - dentro.width * 0.3f, dentro.top + dentro.height * 0.16f, dentro.center.x + dentro.width * 0.3f, dentro.bottom - dentro.height * 0.18f)
        val formaRej = redondo(rej, rej.height / 2)
        drawPath(formaRej, p.con(if (p.tinta) p.papelObj else Color(0xFFCDBF9E), kt, ft))
        clipPath(formaRej) {
            var yy = rej.top + 2f * u
            while (yy < rej.bottom) { drawLine(if (p.tinta) p.grabado.copy(alpha = 0.7f) else p.sombra(0.3f), Offset(rej.left, yy), Offset(rej.right, yy), 0.7f * u); yy += 2.3f * u }
        }
        filete(p, formaRej, 0.7f)
        if (!p.tinta) {
            drawPath(borde(formaDentro, 3f * u, 3f * u), p.sombra(0.3f))
            drawRect(p.sombra(0.18f), Offset(tapa.right - tapa.width * 0.18f, tapa.top), Size(tapa.width * 0.18f, tapa.height))
            drawRect(p.claro(0.25f), Offset(tapa.left, tapa.top), Size(tapa.width, 1.5f * u))
        } else {
            sombra(p, rect(Rect(dentro.left, dentro.top, dentro.right, dentro.top + dentro.height * 0.18f)), 0.4f)
            filete(p, formaDentro, 0.7f)
        }
        filete(p, formaTapa)
        filoLinea(p, Offset(tapa.left + 4f * u, tapa.top + 0.8f * u), Offset(tapa.right - 4f * u, tapa.top + 0.8f * u), kt, ft)
        // La caja: el forro, la franja crema, la rejilla del frente, la perilla y el piloto.
        val formaCaja = redondo(caja, 3.5f * u)
        relleno(p, formaCaja, Mat.TELA, kc, fc)
        val franja = Rect(caja.left, caja.top, caja.right, caja.top + cajaAlto * 0.42f)
        drawRect(if (p.tinta) p.c(Mat.PAPEL, kc, fc) else p.con(Color(0xFFE6DAC0), kc, fc), franja.topLeft, franja.size)
        sombra(p, rect(Rect(caja.left, franja.bottom, caja.right, franja.bottom + 1.6f * u)), 0.5f)
        for (j in 0 until 9) drawCircle(p.c(Mat.VINILO, kc, fc), 1f * u, Offset(caja.left + caja.width * 0.08f + j * 4f * u, caja.top + cajaAlto * 0.72f))
        val perilla = Offset(caja.right - caja.width * 0.12f, caja.top + cajaAlto * 0.7f)
        drawCircle(p.c(Mat.VINILO, kc, fc), 4f * u, perilla)
        drawLine(p.c(Mat.PAPEL, kc, fc), perilla, perilla + Offset(-2f * u, -2.6f * u), 0.8f * u)
        val piloto = Offset(caja.right - caja.width * 0.24f, caja.top + cajaAlto * 0.72f)
        if (p.lampara || (p.tinta && p.oscuro)) { drawCircle(Color(0xFFFF7A3A).copy(alpha = 0.3f), 4f * u, piloto); drawCircle(Color(0xFFFF8A4A), 1.4f * u, piloto) }
        else drawCircle(p.c(Mat.ROJO, kc, fc), 1.4f * u, piloto)
        // Las esquineras de metal.
        listOf(caja.bottomLeft, caja.bottomRight).forEachIndexed { j, e ->
            val s = if (j == 0) 1f else -1f
            drawPath(poligono(e.x, e.y, e.x + s * 6f * u, e.y, e.x, e.y - 6f * u), p.c(Mat.METAL, kc, fc))
        }
        if (!p.tinta) {
            drawRect(p.claro(0.3f), Offset(caja.left, caja.top), Size(caja.width * 0.35f, 1.4f * u))
            drawRect(p.sombra(0.2f), Offset(caja.right - caja.width * 0.14f, caja.top), Size(caja.width * 0.14f, cajaAlto))
        } else sombra(p, rect(Rect(caja.right - caja.width * 0.1f, caja.top, caja.right, caja.bottom)), 0.35f)
        filete(p, formaCaja)
        // El plato, en perspectiva, con el disco a medias: los surcos, el brillo y la etiqueta naranja.
        val pc = Offset(caja.left + caja.width * 0.42f, caja.top - cajaAlto * 0.02f)
        val rx = caja.width * 0.33f; val ry = cajaAlto * 0.42f
        val kp = k(pc); val fp = fl(pc)
        drawOval(p.c(Mat.METAL, kp, fp), Offset(pc.x - rx * 1.05f, pc.y - ry * 1.05f + 1.5f * u), Size(rx * 2.1f, ry * 2.1f))
        val disco = ovalo(Rect(pc.x - rx, pc.y - ry, pc.x + rx, pc.y + ry))
        drawPath(disco, p.c(Mat.VINILO, kp, fp))
        var s = 0.94f
        val surco = if (p.tinta) p.papelObj.copy(alpha = if (p.oscuro) 0.25f else 0.35f) else AL_SOL.copy(alpha = if (p.oscuro) 0.05f else 0.1f)
        while (s > 0.4f) { drawOval(surco, Offset(pc.x - rx * s, pc.y - ry * s), Size(rx * 2 * s, ry * 2 * s), style = Stroke(0.45f * u)); s -= 0.07f }
        // La parte ya tocada brilla distinto: la mitad de afuera del surco.
        drawArc((if (p.tinta) p.papelObj else AL_SOL).copy(alpha = if (p.oscuro && !p.tinta) 0.08f else 0.2f), 195f, 40f, true, Offset(pc.x - rx * 0.95f, pc.y - ry * 0.95f), Size(rx * 1.9f, ry * 1.9f))
        val etiq = Rect(pc.x - rx * 0.32f, pc.y - ry * 0.32f, pc.x + rx * 0.32f, pc.y + ry * 0.32f)
        if (suenos) {
            drawOval(p.con(if (p.tinta) p.ink(Tinta.ULTRAMAR) else Color(0xFF34406E), kp, fp), etiq.topLeft, etiq.size)
            drawOval(p.con(LUNA, maxOf(kp, 0.5f), fp), Offset(etiq.left + etiq.width * 0.3f, etiq.top + etiq.height * 0.2f), Size(etiq.width * 0.32f, etiq.height * 0.55f))
        } else {
            drawOval(p.c(Mat.NARANJA, kp, fp), etiq.topLeft, etiq.size)
            drawOval(p.c(Mat.PAPEL, kp, fp).copy(alpha = 0.6f), Offset(etiq.left + etiq.width * 0.15f, etiq.center.y - 0.5f * u), Size(etiq.width * 0.7f, 1f * u))
        }
        drawCircle(p.c(Mat.METAL, kp, fp), 1.1f * u, pc)
        filete(p, disco, 0.6f)
        // El brazo, a la mitad del surco, con su contrapeso y la cápsula.
        val eje = Offset(caja.right - caja.width * 0.12f, caja.top - cajaAlto * 0.12f)
        val codo = Offset(eje.x - rx * 0.15f, eje.y + ry * 0.75f)
        val punta = Offset(pc.x + rx * 0.66f, pc.y + ry * 0.12f)
        val metal = p.c(Mat.METAL, k(eje), fl(eje))
        drawLine(p.sombra(0.35f), codo + Offset(2f * u, 3f * u), punta + Offset(2f * u, 3f * u), 1.4f * u)
        drawCircle(p.c(Mat.VINILO, k(eje), fl(eje)), 3.2f * u, eje)
        drawRect(p.c(Mat.VINILO, k(eje), fl(eje)), Offset(eje.x + 1.5f * u, eje.y - 3f * u), Size(5f * u, 5f * u))
        drawLine(metal, eje, codo, 1.6f * u, StrokeCap.Round)
        drawLine(metal, codo, punta, 1.6f * u, StrokeCap.Round)
        drawRect(p.c(Mat.VINILO, k(punta), fl(punta)), Offset(punta.x - 3f * u, punta.y - 1.8f * u), Size(5f * u, 3.6f * u))
        if (!p.tinta) drawLine(p.claro(0.6f), eje, codo, 0.5f * u)
        filoLinea(p, Offset(caja.left + 3f * u, caja.top + 0.6f * u), Offset(caja.right - 3f * u, caja.top + 0.6f * u), kc, fc)
    }

    /** El rollo de cinta de pintor, de canto sobre la tabla: el papel crema enrollado y el hueco del cartón. */
    private fun DrawScope.cintaRollo(p: Pintor, pie: Offset, r: Float, k: Float, fl: Color) {
        val u = density
        val c = Offset(pie.x, pie.y - r)
        drawOval(p.sombra(0.35f), Offset(c.x - r * 0.7f, pie.y - 2f * u), Size(r * 2f, 4f * u))
        val rollo = ovalo(Rect(c.x - r * 0.82f, c.y - r, c.x + r * 0.82f, c.y + r))
        drawPath(rollo, p.con(if (p.tinta) p.papelObj else Color(0xFFE2CC92), k, fl))
        drawOval(p.c(Mat.CARTON, k, fl), Offset(c.x - r * 0.5f, c.y - r * 0.62f), Size(r * 1f, r * 1.24f))
        drawOval(p.c(Mat.PARED, k * 0.4f, fl), Offset(c.x - r * 0.38f, c.y - r * 0.48f), Size(r * 0.76f, r * 0.96f))
        for (j in 1..2) drawOval(p.sombra(0.15f), Offset(c.x - r * (0.82f - 0.11f * j), c.y - r * (1f - 0.13f * j)), Size(r * (1.64f - 0.22f * j), r * (2f - 0.26f * j)), style = Stroke(0.5f * u))
        if (!p.tinta) drawPath(borde(rollo, -r * 0.18f, 0f), p.sombra(0.25f)) else sombra(p, borde(rollo, -r * 0.25f, 0f), 0.4f)
        filete(p, rollo, 0.7f)
        // La punta despegada que cuelga.
        drawPath(poligono(c.x + r * 0.5f, c.y + r * 0.82f, c.x + r * 0.9f, c.y + r * 0.7f, c.x + r * 1.05f, pie.y - 0.5f * u, c.x + r * 0.7f, pie.y - 0.5f * u), p.con(if (p.tinta) p.papelObj else Color(0xFFD9C384), k, fl))
    }

    /**
     * La lata de café vacía que hace de portaplumas: la hojalata con sus aros y su franja de color (sin marca), y saliendo de
     * ella, ladeados, los mangos de las plumillas con su pluma y los pinceles con su virola.
     */
    private fun DrawScope.lata(p: Pintor, pie: Offset, an: Float, alto: Float, k: Float, fl: Color, caida: Offset) {
        val u = density
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        val boca = r.top
        // Lo que hay dentro: cinco plumillas y pinceles, de alturas y giros distintos.
        val mangos = listOf(-14f to 0.95f, -5f to 1.35f, 4f to 1.15f, 11f to 1.5f, 19f to 0.85f, -21f to 1.1f)
        mangos.forEachIndexed { j, (giro, largo) ->
            val o = Offset(pie.x + (j - 2.5f) * an * 0.12f, boca + alto * 0.3f)
            rotate(giro, o) {
                val l = alto * largo
                val esPincel = j % 2 == 1
                translate(caida.x, caida.y) { drawLine(p.sombra(if (p.oscuro) 0.3f else 0.16f), o, o - Offset(0f, l), 2f * u) }
                val mango = if (esPincel) p.c(if (j == 3) Mat.ROJO else Mat.PINO, k, fl) else p.c(if (j == 0) Mat.VERDE else Mat.MADERA, k, fl)
                drawLine(mango, o, o - Offset(0f, l), (if (esPincel) 2.2f else 1.8f) * u, StrokeCap.Round)
                if (!p.tinta) drawLine(p.claro(0.4f), o - Offset(0.5f * u, 0f), o - Offset(0.5f * u, l), 0.5f * u)
                val tope = o - Offset(0f, l)
                if (esPincel) {
                    drawRect(p.c(Mat.METAL, k, fl), Offset(tope.x - 1.3f * u, tope.y - 4f * u), Size(2.6f * u, 4.5f * u))
                    drawPath(poligono(tope.x - 1.4f * u, tope.y - 4f * u, tope.x + 1.4f * u, tope.y - 4f * u, tope.x, tope.y - 10f * u), p.c(if (j == 3) Mat.VINILO else Mat.CARTON, k, fl))
                } else {
                    drawRect(p.c(Mat.METAL, k, fl), Offset(tope.x - 1.1f * u, tope.y - 3f * u), Size(2.2f * u, 3.4f * u))
                    drawPath(poligono(tope.x - 0.9f * u, tope.y - 3f * u, tope.x + 0.9f * u, tope.y - 3f * u, tope.x, tope.y - 7f * u), p.c(Mat.VINILO, k, fl))
                }
            }
        }
        // La lata.
        drawOval(p.sombra(0.35f), Offset(r.left, pie.y - 2.5f * u), Size(an * 1.2f, 5f * u))
        translate(caida.x, caida.y) { drawRect(p.sombra(if (p.oscuro) 0.35f else 0.16f), r.topLeft, Size(an, alto - 3f * u)) }
        val cuerpo = rect(r)
        relleno(p, cuerpo, Mat.METAL, k, fl)
        val franja = Rect(r.left, r.top + alto * 0.28f, r.right, r.top + alto * 0.72f)
        relleno(p, rect(franja), Mat.NARANJA, k, fl)
        drawRect(p.c(Mat.PAPEL, k, fl), Offset(franja.left, franja.center.y - 2f * u), Size(an, 4f * u))
        for (j in 0 until 3) drawLine(p.sombra(0.3f), Offset(r.left, r.top + alto * (0.12f + 0.38f * j)), Offset(r.right, r.top + alto * (0.12f + 0.38f * j)), 0.6f * u)
        if (!p.tinta) {
            drawRect(p.claro(0.45f), Offset(r.left + an * 0.12f, r.top), Size(an * 0.12f, alto))
            drawRect(p.sombra(0.3f), Offset(r.right - an * 0.28f, r.top), Size(an * 0.28f, alto))
        } else sombra(p, rect(Rect(r.right - an * 0.3f, r.top, r.right, r.bottom)), 0.45f)
        drawOval(p.c(Mat.METAL, k, fl), Offset(r.left, r.top - 2f * u), Size(an, 4f * u))
        drawOval(p.sombra(0.6f), Offset(r.left + 1.5f * u, r.top - 1.2f * u), Size(an - 3f * u, 2.4f * u))
        filete(p, cuerpo)
        filoLinea(p, Offset(r.left + 0.6f * u, r.top), Offset(r.left + 0.6f * u, r.bottom), k, fl)
    }

    /**
     * Un frasco de tinta china, de vidrio de hombros cuadrados: la tinta negra dentro, la etiqueta de papel y el tapón de
     * baquelita; [destapado], el tapón tumbado al lado y la plumilla apoyada en la boca.
     */
    private fun DrawScope.frasco(p: Pintor, pie: Offset, an: Float, destapado: Boolean, k: Float, fl: Color, caida: Offset) {
        val u = density
        val alto = an * 1.15f
        val cuerpo = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        translate(caida.x, caida.y) { drawRect(p.sombra(if (p.oscuro) 0.35f else 0.18f), cuerpo.topLeft, cuerpo.size) }
        drawOval(p.sombra(0.35f), Offset(cuerpo.left, pie.y - 2f * u), Size(an * 1.25f, 4f * u))
        val forma = redondo(cuerpo, an * 0.14f)
        drawPath(forma, p.c(Mat.VINILO, k, fl))
        // El vidrio: un reflejo a la izquierda y el nivel de la tinta.
        drawRect(if (p.tinta) p.papelObj.copy(alpha = 0.6f) else p.claro(0.5f), Offset(cuerpo.left + an * 0.12f, cuerpo.top + alto * 0.12f), Size(an * 0.08f, alto * 0.7f))
        val etiq = Rect(cuerpo.left + an * 0.18f, cuerpo.top + alto * 0.38f, cuerpo.right - an * 0.12f, cuerpo.top + alto * 0.78f)
        drawRect(p.c(Mat.PAPEL, k, fl), etiq.topLeft, etiq.size)
        drawRect(p.c(if (destapado) Mat.ROJO else Mat.TELA, k, fl), etiq.topLeft, Size(etiq.width, etiq.height * 0.3f))
        drawLine(p.c(Mat.VINILO, k, fl).copy(alpha = 0.6f), Offset(etiq.left + 1.5f * u, etiq.bottom - etiq.height * 0.3f), Offset(etiq.right - 2f * u, etiq.bottom - etiq.height * 0.3f), 0.6f * u)
        filete(p, forma, 0.8f)
        // El cuello.
        val cuello = Rect(pie.x - an * 0.2f, cuerpo.top - an * 0.2f, pie.x + an * 0.2f, cuerpo.top + 1f * u)
        drawRect(p.c(Mat.VINILO, k, fl), cuello.topLeft, cuello.size)
        if (!destapado) {
            val tapon = Rect(pie.x - an * 0.26f, cuello.top - an * 0.34f, pie.x + an * 0.26f, cuello.top + 1f * u)
            drawRoundRect(p.c(Mat.VINILO, k * 0.9f, fl), tapon.topLeft, tapon.size, CornerRadius(1.2f * u))
            for (j in 0 until 4) drawLine(p.claro(0.25f), Offset(tapon.left + tapon.width * (0.2f + 0.2f * j), tapon.top + 1f * u), Offset(tapon.left + tapon.width * (0.2f + 0.2f * j), tapon.bottom - 1f * u), 0.5f * u)
            filoLinea(p, tapon.topLeft, tapon.topRight, k, fl)
        } else {
            // El tapón, tumbado delante, y la plumilla apoyada en la boca, con su punta manchada.
            val t = Rect(pie.x + an * 0.4f, pie.y - an * 0.3f, pie.x + an * 0.82f, pie.y)
            drawOval(p.sombra(0.3f), Offset(t.left, pie.y - 1.5f * u), Size(t.width + 3f * u, 3f * u))
            drawRoundRect(p.c(Mat.VINILO, k * 0.9f, fl), t.topLeft, t.size, CornerRadius(1.2f * u))
            filete(p, redondo(t, 1.2f * u), 0.6f)
            val boca = Offset(pie.x, cuello.top)
            val fin = boca + Offset(-an * 0.9f, -an * 1.6f)
            drawLine(p.c(Mat.MADERA, k, fl), boca + Offset(-an * 0.12f, -an * 0.2f), fin, 2f * u, StrokeCap.Round)
            drawLine(p.c(Mat.METAL, k, fl), boca, boca + Offset(-an * 0.12f, -an * 0.2f), 1.6f * u)
        }
        filoLinea(p, Offset(cuerpo.left + 0.8f * u, cuerpo.top + 2f * u), Offset(cuerpo.left + 0.8f * u, cuerpo.bottom - 2f * u), k, fl, 0.8f)
    }

    // ---------------------------------------------------------------- al pie

    /** El suelo de tablas, que se aleja hacia la pared: las juntas, las vetas y la sombra del mueble encima. */
    private fun DrawScope.suelo(p: Pintor, d: DisposicionEstante, piso: Float, k: (Offset) -> Float) {
        val u = density; val w = size.width; val h = size.height
        val z = Rect(0f, piso, w, h)
        val kz = k(Offset(w * 0.4f, piso))
        relleno(p, rect(z), Mat.SUELO, kz)
        clipRect(z.left, z.top, z.right, z.bottom) {
            var y = piso; var j = 0
            while (y < h) {
                val g = (4f + 3.5f * j) * u
                drawLine(if (p.tinta) (if (p.oscuro) TrazosTinta.NEGRO else TrazosTinta.GRABAR) else p.sombra(0.4f), Offset(0f, y), Offset(w, y), 0.8f * u)
                // Las juntas de testa de cada tabla, a trechos.
                for (q in 0 until 3) {
                    val x = w * azar(700 + j, q)
                    drawLine(p.sombra(0.35f), Offset(x, y), Offset(x, y + g), 0.7f * u)
                }
                if (!p.tinta) drawRect(p.claro(0.12f), Offset(0f, y + 0.8f * u), Size(w, 1f * u))
                y += g; j++
            }
            if (!p.tinta) vetas(z, MADERA.copy(alpha = if (p.oscuro) 0.3f else 0.35f), 711, densidad = 0.8f, grosor = 0.6f, ondulacion = 0.4f)
            else if (!p.oscuro) with(TrazosTinta) { trama(rect(Rect(0f, piso, w, piso + 10f * u)), 2.4f * u, 0.8f * u, TrazosTinta.PAPEL.copy(alpha = 0.6f), 0f) }
            // La sombra del mueble en el suelo, corrida a la derecha (de día) o lejos de la luz.
            drawRect(p.sombra(if (p.oscuro) 0.5f else 0.32f), Offset(d.izquierda.left + w * 0.02f, piso), Size(w * 0.96f, 6f * u))
            if (!p.tinta && !p.oscuro) mancha(Offset(w * 0.1f, piso + w * 0.02f), w * 0.5f, AL_SOL.copy(alpha = if (p.noche) 0.06f else 0.16f))
        }
    }

    /**
     * La caja de leche de madera, con discos de canto, cortada por el borde derecho: las tablillas con su hueco, los
     * cantos de las fundas que asoman (de colores de la foto) y la letra quemada de la lechería (trazos, no se lee).
     */
    private fun DrawScope.cajaLeche(p: Pintor, r: Rect, k: Float, fl: Color) {
        val u = density
        drawOval(p.sombra(0.4f), Offset(r.left - 4f * u, r.bottom - 4f * u), Size(r.width + 8f * u, 8f * u))
        // Los discos que asoman.
        var x = r.left + 4f * u; var j = 0
        val cols = listOf(Mat.NARANJA, Mat.PAPEL, Mat.TELA, Mat.VINILO, Mat.OCRE, Mat.ROJO, Mat.CARTON, Mat.VERDE)
        while (x < r.right - 3f * u) {
            val alto = r.height * (0.18f + 0.1f * azar(j, 5))
            val g = (2.6f + 1.4f * azar(j, 7)) * u
            val fr = Rect(x, r.top - alto, x + g, r.top + 4f * u)
            rotate((azar(j, 9) - 0.5f) * 6f, Offset(fr.center.x, fr.bottom)) {
                relleno(p, rect(fr), cols[Math.floorMod(j * 3 + 1, cols.size)], k, fl)
                drawLine(p.sombra(0.4f), fr.topRight, fr.bottomRight, 0.5f * u)
                filete(p, rect(fr), 0.4f)
            }
            x += g + 0.6f * u; j++
        }
        val tab = r.height / 3f
        for (q in 0 until 3) {
            val tr = Rect(r.left, r.top + q * tab + 1f * u, r.right, r.top + (q + 1) * tab - 1.2f * u)
            relleno(p, rect(tr), Mat.PINO, k * (0.9f - 0.12f * q), fl)
            clipRect(tr.left, tr.top, tr.right, tr.bottom) { vetas(tr, (if (p.tinta) p.grabado else MADERA).copy(alpha = 0.35f), 731 + q, densidad = 0.6f, grosor = 0.6f) }
            if (!p.tinta) {
                drawRect(p.claro(0.35f), tr.topLeft, Size(tr.width, 1f * u))
                drawRect(p.sombra(0.25f), Offset(tr.left, tr.bottom - 1.5f * u), Size(tr.width, 1.5f * u))
            } else sombra(p, rect(Rect(tr.left, tr.bottom - tr.height * 0.3f, tr.right, tr.bottom)), 0.4f)
            filete(p, rect(tr), 0.7f)
            // El hueco entre tablillas, en sombra.
            drawRect(p.sombra(0.6f), Offset(tr.left, tr.bottom), Size(tr.width, 2.2f * u))
        }
        // El listón del costado y la letra quemada (trazos).
        val liston = Rect(r.left, r.top, r.left + 7f * u, r.bottom)
        relleno(p, rect(liston), Mat.MADERA, k, fl)
        filete(p, rect(liston), 0.7f)
        val tinta = if (p.tinta) p.ink(Tinta.MAGENTA) else p.con(ROJO, k, fl)
        for (q in 0 until 5) drawRoundRect(tinta.copy(alpha = 0.75f), Offset(r.left + 16f * u + q * 9f * u, r.top + tab * 1.3f), Size(6f * u, tab * 0.4f), CornerRadius(1f * u))
        for (q in 0 until 3) drawCircle(p.sombra(0.6f), 0.8f * u, Offset(liston.center.x, r.top + (q + 0.5f) * tab))
        filoLinea(p, Offset(r.left, r.top), Offset(r.right, r.top), k, fl)
    }

    // ---------------------------------------------------------------- lo que se deja en los huecos libres

    /**
     * Lo del tema en lo que queda libre de la repisa [r], a la derecha de [oc]: según cuánto hueco haya, el sobre interior de
     * un disco apoyado en el último volumen (hace de sujetalibros), la pila de discos tumbados, la taza con pinceles y el
     * frasco con pinceles en agua. Cada repisa escoge lo suyo, para que no se repita.
     */
    private fun DrawScope.huecoLibre(p: Pintor, r: Int, z: Rect, oc: Float, k: Float, fl: Color, caida: (Offset) -> Offset) {
        val u = density
        val libre = (z.right - oc) / u
        val y = z.bottom
        val lado = minOf(z.height * 0.72f, 78f * u)
        when {
            libre >= 150f -> when (r % 4) {
                1 -> {
                    val fin = sobreApoyado(p, oc + 1f * u, y, lado, k + 0.03f, fl)
                    taza(p, Offset(fin + 22f * u, y), 26f * u, k, fl, caida(Offset(fin + 22f * u, y - 12f * u)))
                    pilaDiscos(p, Offset(z.right - 80f * u, y), 70f * u, 5, r, k - 0.03f, fl)
                }
                3 -> {
                    pilaDiscos(p, Offset(oc + 8f * u, y), 72f * u, 6, r, k + 0.03f, fl)
                    taza(p, Offset(oc + 40f * u, y - 6 * 4.5f * u), 24f * u, k + 0.03f, fl, caida(Offset(oc + 40f * u, y - 40f * u)))
                    frascoAgua(p, Offset(z.right - 34f * u, y), 22f * u, k - 0.03f, fl, caida(Offset(z.right - 34f * u, y - 20f * u)))
                }
                else -> {
                    pilaDiscos(p, Offset(oc + 8f * u, y), 70f * u, 4, r, k + 0.03f, fl)
                    frascoAgua(p, Offset(oc + 44f * u, y - 4 * 4.5f * u), 22f * u, k + 0.03f, fl, caida(Offset(oc + 44f * u, y - 40f * u)))
                    taza(p, Offset(z.right - 40f * u, y), 26f * u, k - 0.03f, fl, caida(Offset(z.right - 40f * u, y - 12f * u)))
                }
            }
            libre >= 74f -> when (r % 3) {
                0 -> { pilaDiscos(p, Offset(oc + 7f * u, y), 62f * u, 3, r, k, fl); frascoAgua(p, Offset(oc + 40f * u, y - 3 * 4.5f * u), 20f * u, k, fl, caida(Offset(oc + 40f * u, y - 30f * u))) }
                1 -> {
                    val l = minOf(lado, (libre - 30f) * u / 1.25f)
                    val fin = sobreApoyado(p, oc + 1f * u, y, l, k, fl)
                    if (z.right - fin > 30f * u) taza(p, Offset(fin + 15f * u, y), 22f * u, k, fl, caida(Offset(fin + 15f * u, y - 12f * u)))
                }
                else -> { taza(p, Offset(oc + 20f * u, y), 24f * u, k, fl, caida(Offset(oc + 20f * u, y - 12f * u))); frascoAgua(p, Offset(oc + 52f * u, y), 20f * u, k, fl, caida(Offset(oc + 52f * u, y - 20f * u))) }
            }
            libre >= 46f -> when (r % 3) {
                0 -> sobreApoyado(p, oc + 1f * u, y, minOf(lado, (libre - 6f) * u / 1.2f), k, fl)
                1 -> taza(p, Offset((oc + z.right) / 2, y), 24f * u, k, fl, caida(Offset((oc + z.right) / 2, y - 12f * u)))
                else -> frascoAgua(p, Offset((oc + z.right) / 2, y), 20f * u, k, fl, caida(Offset((oc + z.right) / 2, y - 20f * u)))
            }
            libre >= 26f -> frascoAgua(p, Offset(oc + 11f * u, y), 15f * u, k, fl, caida(Offset(oc + 11f * u, y - 16f * u)))
        }
    }

    /** El sobre interior de papel de un disco, apoyado de lado en el último volumen: pisa con una esquina y deja su cuña de sombra. */
    private fun DrawScope.sobreApoyado(p: Pintor, xIzq: Float, y: Float, lado: Float, k: Float, fl: Color): Float {
        val u = density
        val giro = 11f
        val rad = Math.toRadians(giro.toDouble())
        // Pisa con la esquina de abajo a la izquierda y apoya la de arriba en el último volumen.
        val pivote = Offset(xIzq + lado * sin(rad).toFloat(), y)
        val derecha = pivote.x + lado * cos(rad).toFloat()
        // La cuña de sombra bajo el canto levantado y la sombra en el fondo.
        drawPath(poligono(pivote.x, y, derecha, y, derecha, y - lado * sin(rad).toFloat()), p.sombra(if (p.oscuro) 0.5f else 0.36f))
        rotate(-giro, pivote) {
            // El sobre de papel, cuadrado, con el agujero redondo que deja ver la etiqueta del disco que guarda.
            val cara = Rect(pivote.x, y - lado, pivote.x + lado, y)
            drawRect(p.sombra(0.25f), cara.topLeft + Offset(5f * u, 2f * u), cara.size)
            val papel = p.c(Mat.PAPEL, k, fl)
            drawRect(papel, cara.topLeft, cara.size)
            val c = Offset(cara.center.x, cara.top + cara.height * 0.45f)
            drawCircle(p.sombra(0.35f), cara.width * 0.19f, c + Offset(0.8f * u, 0.8f * u))
            drawCircle(p.c(Mat.NARANJA, k, fl), cara.width * 0.17f, c)
            drawCircle(p.c(Mat.PAPEL, k, fl).copy(alpha = 0.7f), cara.width * 0.17f, c, style = Stroke(0.6f * u))
            drawCircle(p.c(Mat.VINILO, k, fl), cara.width * 0.02f, c)
            // El sobre usado: una arruga en diagonal y la esquina doblada.
            drawLine(p.sombra(0.2f), Offset(cara.left + cara.width * 0.1f, cara.top + cara.height * 0.2f), Offset(cara.left + cara.width * 0.45f, cara.top + cara.height * 0.05f), 0.6f * u)
            drawPath(poligono(cara.right, cara.top + cara.width * 0.12f, cara.right - cara.width * 0.12f, cara.top, cara.right, cara.top), p.c(Mat.PARED, k * 0.5f, fl))
            for (j in 0 until 3) drawLine(p.sombra(0.25f), Offset(cara.left + 3f * u, cara.bottom - (6f + 4f * j) * u), Offset(cara.right - (4f + 6f * j) * u, cara.bottom - (6f + 4f * j) * u), 0.5f * u)
            if (!p.tinta) drawRect(p.sombra(0.15f), Offset(cara.right - cara.width * 0.2f, cara.top), Size(cara.width * 0.2f, cara.height))
            else sombra(p, rect(Rect(cara.right - cara.width * 0.18f, cara.top, cara.right, cara.bottom)), 0.3f)
            filete(p, rect(cara), 0.7f)
            filoLinea(p, cara.topLeft, cara.topRight, k, fl)
        }
        return derecha
    }

    /** Una pila de discos tumbados en sus fundas, vistos de canto, un poco corridos. */
    private fun DrawScope.pilaDiscos(p: Pintor, izq: Offset, an: Float, cuantos: Int, semilla: Int, k: Float, fl: Color) {
        val u = density
        val g = 4.5f * u
        drawOval(p.sombra(0.35f), Offset(izq.x + an * 0.05f, izq.y - 2f * u), Size(an * 1.1f, 5f * u))
        val mats = listOf(Mat.TELA, Mat.PAPEL, Mat.NARANJA, Mat.VINILO, Mat.OCRE, Mat.CARTON)
        for (j in 0 until cuantos) {
            val dx = (azar(semilla + 50, j) - 0.5f) * 8f * u
            val r = Rect(izq.x + dx, izq.y - (j + 1) * g, izq.x + dx + an * (0.92f + 0.08f * azar(semilla + 51, j)), izq.y - j * g)
            relleno(p, rect(r), mats[Math.floorMod(semilla + j * 2, mats.size)], k, fl)
            drawRect(p.sombra(0.3f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
            if (!p.tinta) drawRect(p.claro(0.3f), r.topLeft, Size(r.width, 0.8f * u))
            filete(p, rect(r), 0.5f)
            if (j == cuantos - 1) filoLinea(p, r.topLeft, r.topRight, k, fl)
        }
    }

    /** La taza de loza desportillada con pinceles, de pie en [pie]. */
    private fun DrawScope.taza(p: Pintor, pie: Offset, an: Float, k: Float, fl: Color, caida: Offset) {
        val u = density
        val alto = an * 0.9f
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        listOf(-12f to 1.5f, 8f to 1.8f, 22f to 1.3f).forEachIndexed { j, (giro, largo) ->
            val o = Offset(pie.x + (j - 1) * an * 0.18f, r.top + alto * 0.3f)
            rotate(giro, o) {
                val tope = o - Offset(0f, alto * largo)
                drawLine(p.c(if (j == 1) Mat.ROJO else Mat.PINO, k, fl), o, tope, 1.8f * u, StrokeCap.Round)
                drawRect(p.c(Mat.METAL, k, fl), Offset(tope.x - 1.2f * u, tope.y - 3.5f * u), Size(2.4f * u, 4f * u))
                drawPath(poligono(tope.x - 1.3f * u, tope.y - 3.5f * u, tope.x + 1.3f * u, tope.y - 3.5f * u, tope.x, tope.y - 9f * u), p.c(Mat.CARTON, k, fl))
            }
        }
        translate(caida.x, caida.y) { drawRoundRect(p.sombra(if (p.oscuro) 0.35f else 0.18f), r.topLeft, r.size, CornerRadius(an * 0.2f)) }
        drawOval(p.sombra(0.35f), Offset(r.left, pie.y - 2f * u), Size(an * 1.2f, 4f * u))
        // El asa a la derecha.
        drawArc(p.c(Mat.PAPEL, k, fl), -80f, 160f, false, Offset(r.right - an * 0.18f, r.top + alto * 0.2f), Size(an * 0.4f, alto * 0.5f), style = Stroke(2.2f * u))
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(1f * u), CornerRadius(1f * u), CornerRadius(an * 0.3f), CornerRadius(an * 0.3f))) }
        drawPath(forma, p.c(Mat.PAPEL, k, fl))
        drawRect(p.c(Mat.TELA, k, fl), Offset(r.left, r.top + alto * 0.18f), Size(an, 2f * u))
        // La desportilladura del borde.
        drawPath(poligono(r.left + an * 0.6f, r.top, r.left + an * 0.78f, r.top, r.left + an * 0.7f, r.top + 2.5f * u), p.c(Mat.PARED, k * 0.5f, fl))
        if (!p.tinta) {
            drawRect(p.claro(0.4f), Offset(r.left + an * 0.1f, r.top + 2f * u), Size(an * 0.12f, alto * 0.7f))
            drawPath(borde(forma, -an * 0.3f, 0f), p.sombra(0.25f))
        } else sombra(p, borde(forma, -an * 0.3f, 0f), 0.4f)
        filete(p, forma, 0.7f)
        filoLinea(p, r.topLeft, r.topRight, k, fl)
    }

    /** El frasco de vidrio con agua turbia de enjuagar y dos pinceles dentro. */
    private fun DrawScope.frascoAgua(p: Pintor, pie: Offset, an: Float, k: Float, fl: Color, caida: Offset) {
        val u = density
        val alto = an * 1.3f
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        listOf(-9f to 1.3f, 12f to 1.55f).forEachIndexed { j, (giro, largo) ->
            val o = Offset(pie.x + (j - 0.5f) * an * 0.3f, pie.y - 2f * u)
            rotate(giro, o) {
                val tope = o - Offset(0f, alto * largo)
                drawLine(p.c(if (j == 0) Mat.VERDE else Mat.PINO, k, fl), o, tope, 1.6f * u, StrokeCap.Round)
                drawPath(poligono(tope.x - 1.2f * u, tope.y, tope.x + 1.2f * u, tope.y, tope.x, tope.y - 6f * u), p.c(Mat.VINILO, k, fl))
            }
        }
        translate(caida.x, caida.y) { drawRoundRect(p.sombra(if (p.oscuro) 0.3f else 0.14f), r.topLeft, r.size, CornerRadius(an * 0.18f)) }
        val forma = redondo(r, an * 0.18f)
        drawPath(forma, p.c(Mat.PAPEL, k, fl).copy(alpha = 0.35f))
        // El agua: turbia, del color de la última tinta enjuagada.
        val agua = Rect(r.left, r.top + alto * 0.35f, r.right, r.bottom)
        clipPath(forma) { drawRect(p.c(Mat.TELA, k, fl).copy(alpha = 0.55f), agua.topLeft, agua.size) }
        drawLine(p.c(Mat.PAPEL, k, fl).copy(alpha = 0.7f), Offset(r.left + an * 0.18f, r.top + alto * 0.14f), Offset(r.left + an * 0.18f, r.bottom - alto * 0.14f), 1f * u)
        drawRect(p.c(Mat.METAL, k, fl), Offset(r.left - 0.5f * u, r.top), Size(an + 1f * u, 2.5f * u))
        drawOval(p.sombra(0.3f), Offset(r.left, pie.y - 1.5f * u), Size(an * 1.15f, 3f * u))
        drawPath(forma, (if (p.tinta) p.grabado else p.hondo).copy(alpha = 0.4f), style = Stroke(0.6f * u))
        filoLinea(p, Offset(r.left + 0.6f * u, r.top + 3f * u), Offset(r.left + 0.6f * u, r.bottom - 3f * u), k, fl, 0.8f)
    }

    // =================================================================
    //  Cada entrada
    // =================================================================

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val p = pintor(oscuro, v.noche, v.repisas)
        val fx = LADO + v.centro * (1f - 2f * LADO)
        val fy = fyCuerpo(v.repisa + 0.55f)
        val k = if (p.alumbra) p.luz(fx, fy) else 1f
        val fl = p.filo(fx, fy)
        when (clase(v.semilla)) {
            0 -> disco(v, medidor, p, k, fl)
            1 -> carpeta(v, medidor, p, k, fl)
            else -> cuaderno(v, medidor, p, k, fl)
        }
    }

    /** El color del ánimo de una entrada, o el neutro (crema gastado) si no se marcó. */
    private fun animoDe(v: Volumen, p: Pintor, k: Float, fl: Color, neutro: Color): Color = v.tono?.let { p.animo(it, k, fl) } ?: p.con(neutro, k, fl)

    /** La luna de los Sueños, en [c]: el creciente recortado sobre [fondo]. */
    private fun DrawScope.luna(c: Offset, r: Float, color: Color, fondo: Color) {
        drawCircle(color, r, c)
        drawCircle(fondo, r * 0.84f, Offset(c.x + r * 0.44f, c.y - r * 0.26f))
    }

    /** El mes, de abajo arriba a lo largo de [r]. */
    private fun DrawScope.mes(medidor: TextMeasurer, v: Volumen, r: Rect, color: Color) {
        if (r.height < 16f * density || r.width < 4f * density) return
        val corto = v.mes.substringAfter("Álbum de ").substringBefore(" ").trim().ifBlank { v.mes }
        textoEn(medidor, corto, r, ROTULO, color, 8.5f, vertical = true, espaciado = 0.5f, minimo = 4f)
    }

    /** Los lados del lomo: la franja de luz a la izquierda y la de sombra a la derecha (trama en la tinta). */
    private fun DrawScope.lados(p: Pintor, forma: Path, w: Float, h: Float, arriba: Float) {
        val u = density
        if (p.tinta) {
            if (!p.oscuro) clipPath(forma) {
                clipRect(w * 0.82f, arriba, w, h) {
                    // La trama de la sombra, a 45°, solo en la franja de la derecha.
                    var y = arriba - w
                    while (y < h) { drawLine(TrazosTinta.GRABAR.copy(alpha = 0.75f), Offset(w * 0.82f, y + w * 0.18f), Offset(w, y), 0.7f * u); y += 2.8f * u }
                }
            } else drawRect(TrazosTinta.NEGRO.copy(alpha = 0.4f), Offset(w * 0.8f, arriba), Size(w * 0.2f, h - arriba))
            return
        }
        drawRect(p.claro(0.28f), Offset(0.6f * u, arriba), Size(w * 0.1f, h - arriba))
        drawRect(p.sombra(0.16f), Offset(w * 0.78f, arriba), Size(w * 0.22f, h - arriba))
        drawRect(p.sombra(0.3f), Offset(w - 1f * u, arriba), Size(1f * u, h - arriba))
    }

    /** El filo de la luz en lo que la mira: la cabeza y el costado izquierdo. */
    private fun DrawScope.filoVolumen(p: Pintor, k: Float, fl: Color, arriba: Float, ancho: Float) {
        if (!p.alumbra || k < 0.08f) return
        val u = density
        drawRect(fl.copy(alpha = (0.65f * k).coerceAtMost(0.7f)), Offset(1f * u, arriba), Size(ancho - 2f * u, 1f * u))
        drawRect(fl.copy(alpha = (0.4f * k).coerceAtMost(0.5f)), Offset(0f, arriba + 1.5f * u), Size(0.9f * u, size.height - arriba - 1.5f * u))
    }

    /**
     * El disco de larga duración en su funda impresa a dos tintas (el mismo objeto que la tapa): el lomo del color del ánimo,
     * los arcos de los anillos impresos en la segunda tinta, la etiqueta redonda del disco con el día, el mes a lo largo y,
     * al pie, el disquito impreso (en los Sueños, la luna). Arriba asoma la funda interior de papel, más cuanto más escribiste.
     */
    private fun DrawScope.disco(v: Volumen, medidor: TextMeasurer, p: Pintor, k: Float, fl: Color) {
        val u = density
        val w = size.width; val h = size.height
        val asoma = (2.5f + 6.5f * v.lleno) * u
        // La funda interior, que asoma por arriba con la curva del disco detrás.
        val interior = Rect(w * 0.1f, 0f, w * 0.9f, asoma + 3f * u)
        drawRoundRect(p.c(Mat.PAPEL, k, fl), interior.topLeft, interior.size, CornerRadius(2f * u))
        drawRect(p.sombra(0.22f), Offset(interior.right - w * 0.18f, 0f), Size(w * 0.08f, asoma + 3f * u))
        val cuerpo = Rect(0f, asoma, w, h)
        val forma = rect(cuerpo)
        val ani = animoDe(v, p, k, fl, Color(0xFFB8AA90))
        drawPath(forma, ani)
        // La segunda tinta: los arcos de los anillos que vienen de la portada, y una franja.
        val segunda = if (p.tinta) p.con(if (p.oscuro) TrazosTinta.CREMA else TrazosTinta.GRABAR, k, fl).copy(alpha = 0.55f) else p.con(TINTA_C, k, fl).copy(alpha = 0.35f)
        val cA = Offset(w * 1.3f, h * 0.62f)
        clipPath(forma) {
            for (j in 0 until 5) drawCircle(segunda, w * (0.7f + 0.32f * j), cA, style = Stroke(1.1f * u))
        }
        drawRect(segunda, Offset(0f, h * 0.955f), Size(w, h * 0.045f))
        // La etiqueta redonda del disco, con el día.
        val rd = minOf(w * 0.44f, 17f * u)
        val ce = Offset(w / 2, asoma + rd + 4f * u)
        drawCircle(p.sombra(0.3f), rd, ce + Offset(0.6f * u, 0.9f * u))
        drawCircle(p.etiqueta(k), rd, ce)
        val anillo = p.con(if (p.tinta) p.ink(Tinta.MAGENTA) else NARANJA, k, fl).copy(alpha = 0.6f + 0.3f * p.i)
        if (p.tinta && p.m.registro > 0f) {
            // El registro corrido de la segunda tinta (con la intensidad), dentro del lomo.
            val dd = TrazosTinta.corrimiento(Tinta.MAGENTA, p.m.registro * 0.5f) * u
            drawCircle(anillo, rd, ce + dd, style = Stroke((1.2f + 1.2f * p.i) * u))
        } else drawCircle(anillo, rd, ce, style = Stroke(1.2f * u))
        textoEn(medidor, v.dia, Rect(ce.x - rd * 0.9f, ce.y - rd * 0.75f, ce.x + rd * 0.9f, ce.y + rd * 0.75f), DIA, p.letra, 15f, minimo = 6f)
        // El mes, sobre una franja de papel.
        val tiraMes = Rect(w * 0.26f, ce.y + rd + 5f * u, w * 0.74f, h * 0.84f)
        if (tiraMes.height > 16f * u) {
            drawRect(p.etiqueta(k).copy(alpha = 0.92f), tiraMes.topLeft, tiraMes.size)
            mes(medidor, v, tiraMes, p.letra)
        }
        // Al pie: el disquito impreso; en los Sueños, la luna.
        val pc = Offset(w / 2, h * 0.9f)
        val rr = minOf(w * 0.24f, 5f * u)
        if (v.noche) luna(pc, rr, p.con(LUNA, maxOf(k, 0.55f), fl), ani)
        else { drawCircle(segunda.copy(alpha = 0.9f), rr, pc); drawCircle(ani, rr * 0.35f, pc) }
        lados(p, forma, w, h, asoma)
        // El roce de las esquinas y la costura de arriba.
        drawRect(p.sombra(0.3f), Offset(0f, asoma), Size(w, 1f * u))
        if (azar(v.semilla, 61) > 0.5f) drawPath(poligono(w * 0.4f, asoma, w * 0.48f, asoma + 3.5f * u, w * 0.56f, asoma), p.sombra(0.3f))
        if (p.tinta) drawPath(forma, if (p.oscuro) TrazosTinta.NEGRO.copy(alpha = 0.7f) else TrazosTinta.GRABAR, style = Stroke(0.8f * u))
        filoVolumen(p, k, fl, asoma, w)
    }

    /**
     * La carpeta de cartón con carteles, atada con cinta: el cartón pardo que se abomba, el lomo de tela del color del ánimo,
     * los bordes de los carteles que asoman por arriba (más cuanto más escribiste), la cinta crema con su lazo, la etiqueta
     * con el día y, al pie, el sello (en los Sueños, la luna).
     */
    private fun DrawScope.carpeta(v: Volumen, medidor: TextMeasurer, p: Pintor, k: Float, fl: Color) {
        val u = density
        val w = size.width; val h = size.height
        val hojas = 1 + (v.lleno * 3.4f).toInt()
        val asoma = 7f * u
        // Los carteles que asoman: cada uno de una tinta, de alturas desparejas.
        val tintas = listOf(Mat.NARANJA, Mat.PAPEL, Mat.TELA, Mat.ROJO)
        for (j in 0 until hojas) {
            val x0 = w * (0.1f + 0.18f * j); val alto = asoma * (0.4f + 0.6f * azar(v.semilla, 20 + j))
            val hoja = Rect(x0, asoma - alto, minOf(w * 0.95f, x0 + w * 0.5f), asoma + 2f * u)
            drawRect(p.c(tintas[Math.floorMod(v.semilla + j, tintas.size)], k, fl), hoja.topLeft, hoja.size)
            drawRect(p.sombra(0.25f), Offset(hoja.right - 1f * u, hoja.top), Size(1f * u, hoja.height))
        }
        // El cartón, un poco abombado.
        val forma = Path().apply {
            moveTo(w * 0.04f, asoma); lineTo(w * 0.96f, asoma)
            cubicTo(w * 1.0f, h * 0.4f, w * 1.0f, h * 0.7f, w, h); lineTo(0f, h)
            cubicTo(0f, h * 0.7f, 0f, h * 0.4f, w * 0.04f, asoma); close()
        }
        drawPath(forma, p.c(Mat.CARTON, k, fl))
        // El lomo de tela, del color del ánimo.
        val tela = Rect(w * 0.18f, asoma, w * 0.82f, h)
        val ani = animoDe(v, p, k, fl, Color(0xFF8E8070))
        drawRect(ani, tela.topLeft, tela.size)
        drawLine(p.sombra(0.25f), Offset(tela.left, asoma), Offset(tela.left, h), 0.7f * u)
        drawLine(p.sombra(0.25f), Offset(tela.right, asoma), Offset(tela.right, h), 0.7f * u)
        // La etiqueta pegada con el día.
        val et = Rect(w * 0.12f, asoma + 4f * u, w * 0.88f, asoma + 4f * u + minOf(w * 0.78f, 22f * u))
        drawRect(p.sombra(0.25f), et.topLeft + Offset(0.8f * u, 0.8f * u), et.size)
        drawRect(p.etiqueta(k), et.topLeft, et.size)
        drawRect(p.con(if (p.tinta) p.ink(Tinta.ULTRAMAR) else ROJO, k, fl).copy(alpha = 0.7f), et.topLeft, Size(et.width, 1.4f * u))
        textoEn(medidor, v.dia, et.deflate(1f * u), DIA, p.letra, 14f, minimo = 6f)
        // La cinta crema que la ata, con su lazo al costado.
        val yc = h * 0.62f
        val cinta = p.c(Mat.PAPEL, maxOf(k, 0.2f), fl)
        drawRect(p.sombra(0.3f), Offset(0f, yc + 3f * u), Size(w, 1f * u))
        drawRect(cinta, Offset(0f, yc), Size(w, 3f * u))
        drawCircle(cinta, 2.2f * u, Offset(w * 0.78f, yc + 1.5f * u))
        drawLine(cinta, Offset(w * 0.78f, yc + 1.5f * u), Offset(w * 0.68f, yc + 9f * u), 1.4f * u, StrokeCap.Round)
        drawLine(cinta, Offset(w * 0.78f, yc + 1.5f * u), Offset(w * 0.9f, yc + 8f * u), 1.4f * u, StrokeCap.Round)
        // El mes entre la etiqueta y la cinta, en una tira de papel.
        val tiraMes = Rect(w * 0.3f, et.bottom + 3f * u, w * 0.7f, yc - 3f * u)
        if (tiraMes.height > 16f * u) {
            drawRect(p.etiqueta(k).copy(alpha = 0.9f), tiraMes.topLeft, tiraMes.size)
            mes(medidor, v, tiraMes, p.letra)
        }
        // Al pie, el sello redondo (en los Sueños, la luna).
        val pc = Offset(w / 2, h * 0.86f)
        val rr = minOf(w * 0.22f, 4.5f * u)
        if (v.noche) luna(pc, rr, p.con(LUNA, maxOf(k, 0.55f), fl), ani)
        else drawCircle(p.c(Mat.PAPEL, k, fl).copy(alpha = 0.75f), rr, pc, style = Stroke(1.2f * u))
        lados(p, forma, w, h, asoma)
        if (p.tinta) drawPath(forma, if (p.oscuro) TrazosTinta.NEGRO.copy(alpha = 0.7f) else TrazosTinta.GRABAR, style = Stroke(0.8f * u))
        filoVolumen(p, k, fl, asoma, w)
    }

    /**
     * El cuaderno de bocetos de tapa negra, de esquinas redondas: el canto de las hojas arriba y los bocetos que asoman (más
     * cuanto más escribiste), la goma ancha y el marcapáginas del color del ánimo, la pegatina con el día, el mes en crema a lo
     * largo y, al pie, la estrella de lápiz (en los Sueños, la luna).
     */
    private fun DrawScope.cuaderno(v: Volumen, medidor: TextMeasurer, p: Pintor, k: Float, fl: Color) {
        val u = density
        val w = size.width; val h = size.height
        val asoma = (1f + 5f * v.lleno) * u
        // Los bocetos sueltos que asoman, ladeados.
        val bocetos = (v.lleno * 3f).toInt()
        for (j in 0 until bocetos) rotate(-6f + 9f * j, Offset(w * (0.3f + 0.2f * j), asoma + 4f * u)) {
            drawRect(p.c(Mat.PAPEL, k, fl), Offset(w * (0.16f + 0.2f * j), 0f), Size(w * 0.42f, asoma + 6f * u))
            drawLine(p.sombra(0.35f), Offset(w * (0.2f + 0.2f * j), asoma * 0.5f + 1f * u), Offset(w * (0.45f + 0.2f * j), asoma * 0.5f + 2f * u), 0.5f * u)
        }
        val tapa = Rect(0f, asoma, w, h)
        val forma = Path().apply { addRoundRect(RoundRect(tapa, CornerRadius(3.5f * u), CornerRadius(3.5f * u), CornerRadius(1.5f * u), CornerRadius(1.5f * u))) }
        val negro = if (p.tinta) p.con(if (p.oscuro) Color(0xFF2A2236) else TrazosTinta.GRABAR, k, fl) else p.con(Color(0xFF2B2522), k, fl)
        drawPath(forma, negro)
        // El canto de las hojas, en la cabeza.
        drawRect(p.c(Mat.PAPEL, k, fl), Offset(w * 0.08f, asoma + 1.2f * u), Size(w * 0.84f, 2f * u))
        // La pegatina con el día.
        val et = Rect(w * 0.14f, asoma + 6f * u, w * 0.86f, asoma + 6f * u + minOf(w * 0.8f, 22f * u))
        drawRoundRect(p.etiqueta(k), et.topLeft, et.size, CornerRadius(2f * u))
        textoEn(medidor, v.dia, et.deflate(1f * u), DIA, p.letra, 14f, minimo = 6f)
        // El mes, en crema sobre el negro.
        val crema = p.con(if (p.tinta) p.papelObj else PAPEL, maxOf(k, 0.55f), fl)
        mes(medidor, v, Rect(w * 0.22f, et.bottom + 3f * u, w * 0.78f, h * 0.6f), crema)
        // La goma ancha y el marcapáginas, del color del ánimo.
        val ani = animoDe(v, p, k, fl, Color(0xFF9A8E7A))
        val yg = h * 0.64f
        val gg = minOf(10f * u, h * 0.1f)
        drawRect(p.sombra(0.4f), Offset(0f, yg + gg), Size(w, 1.2f * u))
        drawRect(ani, Offset(0f, yg), Size(w, gg))
        drawLine(mezcla(ani, p.hondo, 0.3f), Offset(0f, yg + gg * 0.5f), Offset(w, yg + gg * 0.5f), 0.5f * u)
        drawRect(ani, Offset(w * 0.62f, h * 0.84f), Size(w * 0.14f, h * 0.16f))
        drawPath(poligono(w * 0.62f, h, w * 0.69f, h * 0.965f, w * 0.76f, h), negro)
        // Al pie: una estrella de lápiz; en los Sueños, la luna.
        val pc = Offset(w * 0.36f, h * 0.84f)
        val rr = minOf(w * 0.18f, 4.5f * u)
        if (v.noche) luna(pc, rr, p.con(LUNA, maxOf(k, 0.55f), fl), negro)
        else for (j in 0 until 4) {
            val a = PI.toFloat() * j / 4
            drawLine(crema.copy(alpha = 0.7f), pc + Offset(cos(a), sin(a)) * rr, pc - Offset(cos(a), sin(a)) * rr, 0.7f * u)
        }
        // El lomo gastado: luz a la izquierda, la tela rozada en las esquinas.
        lados(p, forma, w, h, asoma)
        drawRect(crema.copy(alpha = 0.18f), Offset(0f, h - 2f * u), Size(w, 2f * u))
        if (p.tinta && !p.oscuro) drawPath(forma, TrazosTinta.GRABAR, style = Stroke(0.8f * u))
        filoVolumen(p, k, fl, asoma, w)
    }
}
