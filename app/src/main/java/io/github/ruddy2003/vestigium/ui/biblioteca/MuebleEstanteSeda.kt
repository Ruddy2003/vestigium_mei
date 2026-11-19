package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * La paleta del almacén de Nanai-vandak (sale del cuadro de la Ruta de la Seda): la cal del muro y el barro del fondo de la
 * alacena, el álamo reseco, el papel, el vidrio, el bronce, el fieltro y la arcilla; y las luces del cuarto.
 */
internal object SedaTinta {
    /** La cal del muro, el barro sin encalar del fondo de la alacena y el adobe con paja que asoma donde la cal saltó. */
    val CAL = Color(0xFFEFDDBA)
    val BARRO = Color(0xFF8E6A4A)
    val ADOBE = Color(0xFFC29C70)
    val PAJA = Color(0xFFDCC48E)
    /** El álamo reseco del dintel y de las tablas: su luz y su sombra honda. */
    val ALAMO = Color(0xFFA48A68)
    val ALAMO_OSC = Color(0xFF574131)
    /** El papel de las cartas y de los rótulos, el hueso de las etiquetas y la tinta. */
    val PAPEL = Color(0xFFEFE4C6)
    /** El papel viejo de las tiras del mes: no blanco, para que no sumen blancos en la repisa. */
    val PAPEL_VIEJO = Color(0xFFDCC8A0)
    val HUESO = Color(0xFFEDE3CB)
    val TINTA = Color(0xFF2A221E)
    /** La arcilla de los sellos y de la tinaja, el bermellón de los sellos de tinta y el bronce de la lámpara. */
    val ARCILLA = Color(0xFFB27450)
    val BERMELLON = Color(0xFFB0372A)
    val BRONCE = Color(0xFFC89A46)
    /** Las sedas teñidas que compra: azafrán y verde (el bermellón y el añil son los de arriba). */
    val AZAFRAN = Color(0xFFDE9A2C)
    val VERDE = Color(0xFF5A8650)
    /** El hollín de la lámpara en la cal. */
    val HOLLIN = Color(0xFF3A2E28)
    /** El vidrio verde pálido, el fieltro rojo vino, el añil y el cuero sin teñir. */
    val VIDRIO = Color(0xFFBFDCCB)
    val FIELTRO = Color(0xFF8E3A32)
    val ANIL = Color(0xFF3E6682)
    val CUERO = Color(0xFF7C5636)
    /** La tierra apisonada del suelo. */
    val TIERRA = Color(0xFFB79A74)

    // Las luces: la noche del cuarto, la de los Sueños, el atardecer de los Sueños en claro, la llama, la luna y el sol.
    val NOCHE = Color(0xFF16182A)
    val NOCHE_SUENOS = Color(0xFF0E1838)
    val ATARDECER = Color(0xFF4B3C6A)
    val LLAMA = Color(0xFFFFD58A)
    val LUNA = Color(0xFFBFD0F2)
    /** La luna de los Sueños de noche, más azul. */
    val LUNA_SUENOS = Color(0xFF9DB8F6)
    val SOL = Color(0xFFFFF0C8)
    val OCASO = Color(0xFFFFB47A)
    /** El brasero del patio, que en los Sueños de noche se refleja en la jamba. */
    val BRASA = Color(0xFFFF9A4A)
}

/**
 * El aire del momento en el almacén: de día (sol por la reja de la izquierda), de noche (el modo oscuro: la lámpara de barro
 * a la derecha y la luna por la misma reja) y en los Sueños ([noche]: el atardecer en claro, con el color de cada cosa; la
 * noche honda en oscuro, con la lámpara en rescoldo, la luna mandando y el brasero del patio reflejado en la jamba). [cerca]
 * es cuánto llega de la luz cálida (0 a 1) y [frio] cuánto de la luna.
 */
internal class AireSeda(val oscuro: Boolean, val noche: Boolean) {
    /** Si arde la lámpara. */
    val lumbre: Boolean get() = oscuro || noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) SedaTinta.NOCHE_SUENOS else SedaTinta.NOCHE) else SOMBRA_MUEBLE
    /**
     * Los Sueños de noche: la lámpara está en rescoldo, manda la luna por la reja y lo único cálido es el brasero del patio,
     * que se refleja en la jamba de la izquierda ([cerca] es entonces cuánto llega de él).
     */
    val brasa: Boolean get() = oscuro && noche
    private val luna: Float get() = if (brasa) 1.7f else 1f

    fun luz(c: Color, cerca: Float, frio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        // Al atardecer cada cosa conserva su color: solo un poco de la luz lila del cielo y el sol bajo en lo que mira a la reja.
        if (!oscuro) return if (noche) mezcla(mezcla(c, SedaTinta.ATARDECER, 0.13f), FILO_MUEBLE, 0.08f * k) else c
        val f = (frio * luna).coerceIn(0f, 1f) * (1f - k)
        val base = mezcla(c, hondo, 0.87f - 0.77f * k.pow(0.95f) - 0.18f * f)
        val calido = if (brasa) SedaTinta.BRASA else FILO_MUEBLE
        return mezcla(mezcla(base, calido, 0.2f * k), if (brasa) SedaTinta.LUNA_SUENOS else SedaTinta.LUNA, (if (brasa) 0.26f else 0.14f) * f)
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(if (noche) a * 1.15f else a)
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.15f else if (noche) a * 0.55f else a)
    fun filo(a: Float): Color = FILO_MUEBLE.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    /** El filo frío de la luna en lo que la mira (de noche). */
    fun helado(a: Float): Color = SedaTinta.LUNA.copy(alpha = if (oscuro) (a * luna).coerceIn(0f, 1f) else 0f)

    /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga adivinando en la repisa más lejana. */
    fun animo(t: Color, cerca: Float, frio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(t, SedaTinta.ATARDECER, 0.07f), FILO_MUEBLE, 0.05f * k) else t
        val f = (frio * luna).coerceIn(0f, 1f) * (1f - k)
        val calido = if (brasa) SedaTinta.BRASA else FILO_MUEBLE
        return mezcla(mezcla(mezcla(t, hondo, 0.58f - 0.48f * k - 0.14f * f), calido, 0.1f * k), if (brasa) SedaTinta.LUNA_SUENOS else SedaTinta.LUNA, (if (brasa) 0.16f else 0.1f) * f)
    }
    /** El hueso o el papel donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(cerca: Float, base: Color = SedaTinta.HUESO): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, SedaTinta.ATARDECER, 0.08f) else base
        return mezcla(mezcla(base, hondo, 0.4f - 0.32f * k), if (brasa) SedaTinta.BRASA else FILO_MUEBLE, 0.14f * k)
    }
    val tinta: Color get() = if (oscuro) mezcla(SedaTinta.TINTA, hondo, 0.4f) else SedaTinta.TINTA
}

/**
 * El dibujo del estante del diario de la Ruta de la Seda (lo usa `EstanteSeda`, en `BibliotecaMedievo.kt`): el almacén de
 * Nanai-vandak, mercader sogdiano, en un caravasar hacia el año 750. Es una alacena honda abierta en el muro de adobe: el muro
 * encalado (la cal tibia, con el adobe y la paja donde saltó y el zócalo de barro), las jambas redondeadas a mano, un dintel
 * de álamo desbastado a azuela que hace de vasar, tablas de álamo reseco empotradas en el barro y, al fondo de cada hueco, el
 * barro sin encalar. Arriba, las vigas del techo con su estera de caña. La luz de día entra por una reja de palos que queda
 * fuera del cuadro, a la izquierda, y deja su dibujo en la cal; de noche, por la misma reja entra la luna y alumbra la lámpara
 * de barro sobre su ménsula de palo, a la derecha. Todo el dibujo sale de [estante] y de [volumen]; la luz de la noche de los
 * dos sale de las mismas curvas ([alcanceSeda], [brasaSeda] y [frioSeda]). El muro está en `MuebleEstanteSedaMuro.kt`, lo de
 * arriba en `MuebleEstanteSedaArriba.kt`, lo del pie y de los huecos en `MuebleEstanteSedaPiezas.kt` y los volúmenes en
 * `MuebleEstanteSedaVolumenes.kt`.
 */
internal object MuebleEstanteSeda {
    val LETRA: FontFamily = LetraExtra.almendra

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.6f
    const val BASE = 0.32f
    const val LADO = 0.08f
    val ALTO_REPISA: Dp = 110.dp
    val TABLA: Dp = 15.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (28 + (palabras / 36).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el rollo de papel en su envoltura; 1, el fajo de cartas; 2, la bolsa de cuero. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 91)
        return if (x < 0.4f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El rollo, el más alto; el fajo, mediano; la bolsa, la más baja. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.88f + 0.07f * azar(semilla, 1)
        1 -> 0.73f + 0.06f * azar(semilla, 1)
        else -> 0.63f + 0.06f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la lámpara de barro (la boca de su pico), en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.835f
    const val LAMP_Y = 0.292f
    /** El centro de la mancha de la reja en el muro (de día el sol, de noche la luna), en anchos del mueble. */
    const val REJA_X = 0.2f
    const val REJA_Y = 0.3f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 125f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.5f
    /** El reflejo del brasero del patio en la jamba izquierda (los Sueños de noche), en anchos del mueble. */
    const val BRASA_X = -0.05f
    const val BRASA_Y = CORONA + 0.35f * FILA / ANCHO_NOMINAL

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireSeda(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        fun cerca(p: Offset): Float = when {
            a.brasa -> brasaSeda(p.x / w - BRASA_X, p.y / w - BRASA_Y)
            p.y <= ch -> alcanceSeda((p.x - llama.x) / w, (p.y - llama.y) / w, n)
            else -> cercaCuerpoSeda((p.y - ch) / fila, n, p.x / w)
        }
        fun frio(p: Offset): Float =
            if (p.y <= ch) frioSeda(p.x / w - REJA_X, p.y / w - REJA_Y) else frioCuerpoSeda((p.y - ch) / fila, n, p.x / w)
        val x0 = d.izquierda.right; val x1 = d.derecha.left
        val gD = w * 0.052f            // el canto del dintel
        val fD = w * 0.016f            // lo que se ve de su cara de arriba
        val apoyo = ch - gD - fD * 0.45f
        val fondo = if (n > 0) d.tablas.last().bottom else ch

        muroSeda(a, ch, fondo, suelo, x0, x1)
        luzRejaSeda(a, apoyo)
        if (a.lumbre && !a.brasa) {
            // El halo de la lámpara en la cal: unas pocas manchas, no un velo.
            val f = if (oscuro) 1f else 0.45f
            mancha(llama, w * 1.2f, FILO_MUEBLE.copy(alpha = 0.16f * f))
            mancha(llama, w * 0.55f, FILO_MUEBLE.copy(alpha = 0.26f * f))
            mancha(llama, w * 0.2f, SedaTinta.LLAMA.copy(alpha = 0.36f * f))
        }

        // ---- el suelo de tierra apisonada, con la paja que se cae de los fardos
        sueloSeda(a, suelo) { p -> cerca(p) }
        // La sombra del vano en el muro de abajo: el zócalo de la alacena echa la suya hacia la derecha.
        drawRect(a.sombra(if (oscuro) 0.3f else 0.12f), Offset(x0 + w * 0.02f, fondo), Size(x1 - x0, w * 0.02f))

        // ---- el fondo de cada hueco: el barro sin encalar, en sombra
        d.repisas.forEachIndexed { k, r -> fondoHueco(k, r, d.ocupado.getOrNull(k) ?: r.left, a, x0, x1) { p -> cerca(p) to frio(p) } }

        // ---- las tablas de álamo, empotradas en las jambas
        d.tablas.forEachIndexed { k, r ->
            tablaAlamo(Rect(x0 - 3f * u, r.top, x1 + 3f * u, r.bottom), 3.6f * u, 830 + 7 * k, a) { x -> cerca(Offset(x, r.center.y)) to frio(Offset(x, r.center.y)) }
        }
        // ---- las jambas: la cal redondeada a mano en las aristas del vano, gastada donde roza
        jambasSeda(a, d, x0, x1, ch, fondo, n, fila)
        // En los Sueños de noche, el brasero del patio se refleja en la jamba de la izquierda: lo único cálido, y lejano.
        if (a.brasa) {
            val b0 = Offset(w * BRASA_X, w * BRASA_Y)
            mancha(b0, w * 0.75f, SedaTinta.BRASA.copy(alpha = 0.16f))
            mancha(b0, w * 0.3f, SedaTinta.BRASA.copy(alpha = 0.3f))
        }

        // ---- lo que hay en los huecos libres
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            val c = Offset((oc + r.right) / 2, r.center.y)
            huecoLibreSeda(k, r, oc, a, cerca(c), frio(c))
        }

        // ---- el rótulo de cada mes: una tira de papel pegada al canto de la tabla con engrudo
        d.placas.forEach { p -> rotuloSeda(p, medidor, a, cerca(p.r.center)) }

        // ---- el dintel de álamo, que hace de vasar, y su sombra en el primer hueco
        dintelSeda(Rect(x0 - w * 0.07f, ch - gD, x1 + w * 0.07f, ch), fD, a) { x -> cerca(Offset(x, ch - gD)) to frio(Offset(x, ch - gD)) }
        drawRect(a.sombra(if (oscuro) 0.5f else 0.38f), Offset(x0, ch), Size(x1 - x0, 3.5f * u))
        drawRect(a.sombra(0.16f), Offset(x0, ch + 3.5f * u), Size(x1 - x0, 6f * u))

        // ---- lo de arriba, de atrás adelante: el cayado, las sedas, la lámpara, los vidrios al sol, las cartas, los melocotones
        val lado = { p: Offset -> if (a.brasa) -1f else if (oscuro || noche) (if (llama.x >= p.x) 1f else -1f) else -1f }
        val pieCayado = Offset(w * 0.035f, apoyo)
        cayadoSeda(pieCayado, Offset(w * 0.2f, w * 0.075f), w, a, { p -> cerca(p) }, { p -> frio(p) })
        // El hollín de la lámpara en la cal, que sube hasta el techo, y las jornadas del camino rayadas con carbón.
        hollinSeda(llama, w, a)
        // A la derecha, bajo el techo, lo que cuelga de una estaca a media sombra: la soga enrollada y la calabaza del agua.
        val estacaD = Offset(w * 0.975f, w * 0.085f)
        colgadoSeda(estacaD, w, a, cerca(estacaD + Offset(-w * 0.05f, w * 0.08f)), frio(estacaD + Offset(-w * 0.05f, w * 0.08f)))
        cuentasSeda(Offset(w * 0.6f, w * 0.385f), w * 0.016f, a)
        // Las sedas: un largo de seda teñida echado sobre una vara colgada del muro y, en el vasar, las piezas dobladas.
        val pV = Offset(w * 0.47f, w * 0.135f)
        sedaColgadaSeda(pV, Offset(w * 0.72f, w * 0.12f), w, a, cerca(pV + Offset(w * 0.12f, w * 0.1f)), frio(pV + Offset(w * 0.12f, w * 0.1f)))
        lamparaBarroSeda(llama, w, a)
        val pieSedas = Offset(w * 0.775f, apoyo)
        sedasDobladasSeda(pieSedas, w, a, cerca(pieSedas - Offset(0f, w * 0.05f)), frio(pieSedas - Offset(0f, w * 0.05f)), lado(pieSedas))
        val pieVidrio = Offset(w * 0.355f, apoyo)
        vidriosSeda(pieVidrio, w, a, cerca(pieVidrio - Offset(0f, w * 0.1f)), frio(pieVidrio - Offset(0f, w * 0.1f)), lado(pieVidrio))
        val pieCarta = Offset(w * 0.565f, apoyo)
        cartaSeda(pieCarta, w, a, cerca(pieCarta - Offset(0f, w * 0.03f)), frio(pieCarta), medidor)
        val pieFruta = Offset(w * 0.13f, apoyo)
        melocotonesSeda(pieFruta, w, a, cerca(pieFruta - Offset(0f, w * 0.03f)), frio(pieFruta - Offset(0f, w * 0.03f)))

        // ---- al pie: la tinaja del vino y el fieltro enrollado
        val pieTinaja = Offset(w * 0.125f, suelo + w * 0.075f)
        tinajaSeda(pieTinaja, w, a, maxOf(cerca(pieTinaja - Offset(0f, w * 0.1f)), 0.12f), frio(pieTinaja))
        val pieFieltro = Offset(w * 0.68f, suelo + w * 0.105f)
        fieltroSeda(pieFieltro, w, a, maxOf(cerca(pieFieltro + Offset(w * 0.15f, -w * 0.05f)), 0.14f))

        // ---- la llama, encima de todo, con su propio color
        if (a.lumbre) llamaSeda(llama, w, a)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- la alacena

    /**
     * El fondo de un hueco: el barro sin encalar, con su paja, en sombra; la sombra de la tabla de arriba, la de la jamba que
     * le quita la luz y el derrame de la otra jamba, que la recibe. De noche se pinta por tramos, con la luz que le llega.
     */
    private fun DrawScope.fondoHueco(k: Int, r: Rect, oc: Float, a: AireSeda, x0: Float, x1: Float, luz: (Offset) -> Pair<Float, Float>) {
        val u = density
        clipRect(x0, r.top, x1, r.bottom) {
            val an = x1 - x0
            val tramos = if (a.lumbre) 8 else 1
            for (t in 0 until tramos) {
                val xa = x0 + an * t / tramos; val xb = x0 + an * (t + 1) / tramos + 0.6f
                val (kc, kf) = luz(Offset((xa + xb) / 2, r.center.y))
                drawRect(a.luz(SedaTinta.BARRO, kc * 0.85f, kf), Offset(xa, r.top), Size(xb - xa, r.height))
            }
            val (kc, kf) = luz(r.center)
            pinceladas(r, a.luz(mezcla(SedaTinta.BARRO, SedaTinta.ADOBE, 0.5f), kc, kf), 750 + k, 16, u, 0.12f)
            pinceladas(r, a.luz(mezcla(SedaTinta.BARRO, SOMBRA_MUEBLE, 0.4f), kc, kf), 760 + k, 14, u, 0.12f)
            // La paja del barro: briznas cortas y claras, pocas.
            for (j in 0 until 18) {
                val q = Offset(x0 + an * azar(770 + k, j), r.top + r.height * (0.25f + 0.7f * azar(780 + k, j)))
                drawLine(a.luz(mezcla(SedaTinta.PAJA, SedaTinta.BARRO, 0.35f), kc, kf).copy(alpha = 0.55f), q, q + Offset((2f + 3f * azar(790 + k, j)) * u, (azar(800 + k, j) - 0.5f) * 2f * u), 0.6f * u)
            }
            // De día, la luz de la ventana entra por la izquierda y da en el fondo de la derecha.
            if (!a.oscuro) drawRect(luzMueble(if (a.noche) 0.06f else 0.1f), Offset(x0 + an * 0.35f, r.top), Size(an * 0.65f, r.height))
            // La sombra de la tabla (o del dintel) de arriba: honda junto a ella y deshecha hacia abajo.
            val f = if (a.oscuro) 1.2f else 1f
            drawRect(a.sombra(0.34f * f), Offset(x0, r.top), Size(an, 8f * u))
            drawRect(a.sombra(0.2f * f), Offset(x0, r.top), Size(an, 18f * u))
            drawRect(a.sombra(0.1f * f), Offset(x0, r.top), Size(an, 32f * u))
            // De día, la jamba izquierda le quita la luz al borde de ese lado; el derrame de la derecha, en cambio, la recibe.
            if (!a.oscuro) {
                drawRect(a.sombra(0.3f), Offset(x0, r.top), Size(11f * u, r.height))
                drawRect(a.sombra(0.16f), Offset(x0 + 11f * u, r.top), Size(9f * u, r.height))
                drawRect(a.luz(mezcla(SedaTinta.CAL, SedaTinta.ADOBE, 0.35f), 0f), Offset(x1 - 5f * u, r.top), Size(5f * u, r.height))
                drawRect(a.sombra(0.2f), Offset(x1 - 5f * u, r.top), Size(5f * u, 7f * u))
                drawRect(a.claro(0.25f), Offset(x1 - 5f * u, r.top + 7f * u), Size(1.2f * u, r.height))
            } else {
                // De noche la lámpara está a la derecha y arriba: el derrame de la izquierda la recibe, el de la derecha no.
                drawRect(a.luz(mezcla(SedaTinta.CAL, SedaTinta.ADOBE, 0.35f), luz(Offset(x0, r.center.y)).first, 0f), Offset(x0, r.top), Size(5f * u, r.height))
                drawRect(a.sombra(0.3f), Offset(x1 - 12f * u, r.top), Size(12f * u, r.height))
                drawRect(a.sombra(0.18f), Offset(x1 - 22f * u, r.top), Size(10f * u, r.height))
            }
            // La sombra que el último volumen echa en el fondo, a su derecha (de noche, a la izquierda no hace falta: lo tapa él).
            if (oc > r.left + 6f * u) {
                val largo = (if (a.oscuro) 16f else 12f) * u
                drawPath(poligonoSeda(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.22f, oc + largo, r.top + r.height * 0.32f, oc + largo, r.bottom), a.sombra(if (a.oscuro) 0.36f else 0.26f))
            }
        }
    }

    /**
     * Una tabla de álamo reseco: su cara de arriba, vista apenas ([fondo]), y el canto con su veta, sus rajas a lo largo, el
     * borde gastado de tanto sacar y meter, y su sombra. De noche se pinta por tramos.
     */
    private fun DrawScope.tablaAlamo(cara: Rect, fondo: Float, semilla: Int, a: AireSeda, luz: (Float) -> Pair<Float, Float>) {
        val u = density
        val tramos = if (a.lumbre) 24 else 1
        for (t in 0 until tramos) {
            val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
            val (k, f) = luz((xa + xb) / 2)
            drawRect(a.luz(mezcla(SedaTinta.ALAMO, LUZ_MUEBLE, 0.32f), k, f), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(SedaTinta.ALAMO, k, f), Offset(xa, cara.top), Size(xb - xa, cara.height))
            if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.06f + 0.5f * k), Offset(xa, cara.top), Size(xb - xa, 1.1f * u))
        }
        clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
            vetas(cara, SedaTinta.ALAMO_OSC.copy(alpha = if (a.oscuro) 0.45f else 0.36f), semilla, densidad = 0.7f, grosor = 0.7f, ondulacion = 0.5f)
            // Las rajas del álamo seco, a lo largo, con su labio de luz.
            for (j in 0 until 2) {
                val xr = cara.left + cara.width * (0.1f + 0.7f * azar(semilla, 20 + j))
                val yr = cara.top + cara.height * (0.35f + 0.35f * azar(semilla, 22 + j))
                val lr = cara.width * (0.08f + 0.12f * azar(semilla, 24 + j))
                drawLine(a.sombra(0.55f), Offset(xr, yr), Offset(xr + lr, yr + (azar(semilla, 26 + j) - 0.5f) * 2f * u), 0.9f * u, StrokeCap.Round)
                drawLine(a.claro(0.3f), Offset(xr, yr + 0.9f * u), Offset(xr + lr * 0.9f, yr + 0.9f * u), 0.5f * u)
            }
            // El canto gastado: la arista de arriba, roma y clara, mellada a trechos.
            for (j in 0 until 6) {
                val x = cara.left + cara.width * azar(semilla, 11 + j)
                drawPath(poligonoSeda(x, cara.top, x + 3f * u, cara.top, x + 1.4f * u, cara.top + 2f * u), a.sombra(0.35f))
            }
        }
        if (!a.oscuro) drawRect(a.claro(0.45f), cara.topLeft, Size(cara.width, 1.1f * u))
        drawLine(a.sombra(0.4f), Offset(cara.left, cara.top - fondo), Offset(cara.right, cara.top - fondo), 0.7f * u)
        drawRect(a.sombra(0.38f), Offset(cara.left, cara.bottom - 1.6f * u), Size(cara.width, 1.6f * u))
        drawRect(a.sombra(0.22f), Offset(cara.left, cara.bottom), Size(cara.width, 3f * u))
    }

    /**
     * Las jambas del vano: la cal redondeada a mano en las aristas (clara en la que mira a la luz, en sombra en la otra), con
     * el barro que asoma donde roza, y el barro con que se tapó la cabeza de cada tabla al empotrarla.
     */
    private fun DrawScope.jambasSeda(a: AireSeda, d: DisposicionEstante, x0: Float, x1: Float, ch: Float, fondo: Float, n: Int, fila: Float) {
        val u = density; val w = size.width
        val r = 11f * u
        val tramos = if (a.lumbre) ((fondo - ch) / (fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        for (t in 0 until tramos) {
            val ya = ch + (fondo - ch) * t / tramos; val yb = ch + (fondo - ch) * (t + 1) / tramos + 0.6f
            val pos = ((ya + yb) / 2 - ch) / fila
            val kl = cercaCuerpoSeda(pos, n, x0 / w, a.brasa); val kr = cercaCuerpoSeda(pos, n, x1 / w, a.brasa)
            val fl = frioCuerpoSeda(pos, n, x0 / w); val fr = frioCuerpoSeda(pos, n, x1 / w)
            // La arista de la izquierda se va hacia dentro (en sombra de día); la de la derecha mira a la ventana.
            // Los cantos redondeados a mano: en tres pasos, de la cara del muro hacia dentro del vano.
            drawRect(a.luz(mezcla(SedaTinta.CAL, LUZ_MUEBLE, if (a.oscuro) 0.1f else 0.3f), kl, fl), Offset(x0 - r, ya), Size(r * 0.35f, yb - ya))
            drawRect(a.luz(mezcla(SedaTinta.CAL, SOMBRA_MUEBLE, if (a.oscuro) 0.08f else 0.14f), kl, fl), Offset(x0 - r * 0.65f, ya), Size(r * 0.3f, yb - ya))
            drawRect(a.luz(mezcla(SedaTinta.CAL, SOMBRA_MUEBLE, 0.3f), kl, fl), Offset(x0 - r * 0.35f, ya), Size(r * 0.35f, yb - ya))
            drawRect(a.luz(mezcla(SedaTinta.CAL, SOMBRA_MUEBLE, 0.08f), kr, fr), Offset(x1 + r * 0.65f, ya), Size(r * 0.35f, yb - ya))
            drawRect(a.luz(mezcla(SedaTinta.CAL, LUZ_MUEBLE, 0.35f), kr, fr), Offset(x1 + r * 0.3f, ya), Size(r * 0.35f, yb - ya))
            drawRect(a.luz(mezcla(SedaTinta.CAL, LUZ_MUEBLE, 0.6f), kr, fr), Offset(x1, ya), Size(r * 0.3f, yb - ya))
            if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.45f * kl), Offset(x0 - r * 0.4f, ya), Size(1.2f * u, yb - ya))
        }
        drawRect(a.claro(0.4f), Offset(x1 + 0.6f * u, ch), Size(1.4f * u, fondo - ch))
        // El barro que tapa la cabeza de cada tabla en la jamba, y donde la cal se gastó con el roce.
        d.tablas.forEachIndexed { k, t ->
            listOf(x0 - 2.5f * u, x1 + 2.5f * u).forEachIndexed { i, x ->
                val c = Offset(x, t.center.y)
                val p = Path().apply {
                    for (j in 0 until 8) {
                        val ang = 6.283f * j / 8
                        val rr = t.height * (0.62f + 0.25f * azar(850 + k * 2 + i, j))
                        val q = Offset(c.x + rr * 0.55f * kotlin.math.cos(ang), c.y + rr * kotlin.math.sin(ang))
                        if (j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
                    }
                    close()
                }
                val kk = cercaCuerpoSeda((t.center.y - ch) / fila, n, x / w, a.brasa)
                drawPath(p, a.luz(mezcla(SedaTinta.ADOBE, SedaTinta.CAL, 0.25f), kk, frioCuerpoSeda((t.center.y - ch) / fila, n, x / w)))
                drawPath(bordeSeda(p, 0.8f * u, 1f * u), a.sombra(0.3f))
            }
        }
        // Los rozones de la arista: la cal gastada, el barro a la vista.
        for (j in 0 until (n * 2).coerceAtMost(30)) {
            val izq = j % 2 == 0
            val y = ch + (fondo - ch) * azar(860, j)
            val x = if (izq) x0 - r * 0.5f else x1 + r * 0.4f
            val pos = (y - ch) / fila
            val l = (6f + 10f * azar(861, j)) * u
            drawRoundRect(a.luz(SedaTinta.ADOBE, cercaCuerpoSeda(pos, n, x / w, a.brasa), frioCuerpoSeda(pos, n, x / w)).copy(alpha = 0.75f), Offset(x - 1.4f * u, y), Size(2.8f * u, l), CornerRadius(1.4f * u))
        }
    }

    /** El dintel: un tronco de álamo desbastado a azuela que hace de vasar; su cara de arriba, las facetas de la azuela y las rajas. */
    private fun DrawScope.dintelSeda(cara: Rect, fondo: Float, a: AireSeda, luz: (Float) -> Pair<Float, Float>) {
        val u = density
        // La cabeza del dintel, metida en el muro: el barro con que se tapó, a cada lado.
        val tramos = if (a.lumbre) 30 else 1
        for (t in 0 until tramos) {
            val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
            val (k, f) = luz((xa + xb) / 2)
            drawRect(a.luz(mezcla(SedaTinta.ALAMO, LUZ_MUEBLE, 0.36f), k, f), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(mezcla(SedaTinta.ALAMO, SedaTinta.ALAMO_OSC, 0.12f), k, f), Offset(xa, cara.top), Size(xb - xa, cara.height))
            if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.08f + 0.55f * k), Offset(xa, cara.top - fondo), Size(xb - xa, 1.2f * u))
        }
        clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
            vetas(cara, SedaTinta.ALAMO_OSC.copy(alpha = if (a.oscuro) 0.45f else 0.38f), 812, densidad = 0.6f, grosor = 0.8f, ondulacion = 0.6f)
            // Las facetas de la azuela: golpes cortos, cada uno con su lado de luz.
            var x = cara.left + 4f * u; var j = 0
            while (x < cara.right) {
                val l = (9f + 9f * azar(813, j)) * u
                val y = cara.top + cara.height * (0.15f + 0.6f * azar(814, j))
                drawOval(a.sombra(0.12f), Offset(x, y), Size(l, cara.height * 0.32f))
                drawOval(a.claro(0.12f), Offset(x - 1f * u, y - 1f * u), Size(l * 0.7f, cara.height * 0.14f))
                x += l * 0.9f; j++
            }
            // Las rajas largas del tronco seco.
            for (k in 0 until 3) {
                val xr = cara.left + cara.width * (0.12f + 0.3f * k + 0.1f * azar(815, k))
                val yr = cara.top + cara.height * (0.3f + 0.4f * azar(816, k))
                val lr = cara.width * (0.1f + 0.1f * azar(817, k))
                drawLine(a.sombra(0.6f), Offset(xr, yr), Offset(xr + lr, yr + (azar(818, k) - 0.5f) * 3f * u), 1f * u, StrokeCap.Round)
                drawLine(a.claro(0.3f), Offset(xr + 2f * u, yr + 1.1f * u), Offset(xr + lr * 0.9f, yr + 1.1f * u), 0.5f * u)
            }
        }
        if (!a.oscuro) drawRect(a.claro(0.5f), Offset(cara.left, cara.top - fondo), Size(cara.width, 1.1f * u))
        drawLine(a.sombra(0.45f), Offset(cara.left, cara.top), Offset(cara.right, cara.top), 0.8f * u)
        drawRect(a.sombra(0.3f), Offset(cara.left, cara.bottom - 2f * u), Size(cara.width, 2f * u))
        // Las cabezas, tapadas con barro donde entran en el muro.
        listOf(cara.left, cara.right).forEachIndexed { i, x ->
            val p = Path().apply {
                val s = if (i == 0) 1f else -1f
                moveTo(x - s * 3f * u, cara.top - fondo - 3f * u)
                cubicTo(x + s * 6f * u, cara.top - fondo, x + s * 7f * u, cara.bottom - 2f * u, x - s * 2f * u, cara.bottom + 3f * u)
                lineTo(x - s * 9f * u, cara.bottom + 2f * u); lineTo(x - s * 9f * u, cara.top - fondo - 2f * u); close()
            }
            val (k, f) = luz(x)
            drawPath(p, a.luz(mezcla(SedaTinta.ADOBE, SedaTinta.CAL, 0.3f), k, f))
            drawPath(bordeSeda(p, -1f * u, 1f * u), a.sombra(0.25f))
        }
    }

    /** El rótulo de un mes: una tira de papel pegada al canto de la tabla, con su sello de bermellón a un lado. */
    private fun DrawScope.rotuloSeda(p: PlacaEstante, medidor: TextMeasurer, a: AireSeda, cerca: Float) {
        val u = density
        val r = Rect(p.r.left, p.r.top - 1f * u, p.r.right, p.r.bottom + 1f * u)
        val forma = poligonoSeda(r.left, r.top + 0.6f * u, r.left + r.width * 0.5f, r.top - 0.3f * u, r.right, r.top + 0.4f * u, r.right - 0.5f * u, r.bottom, r.left + r.width * 0.4f, r.bottom + 0.4f * u, r.left + 0.6f * u, r.bottom - 0.3f * u)
        drawPath(corridoSeda(forma, 1.2f * u, 1.5f * u), a.sombra(0.36f))
        drawPath(forma, a.etiqueta(maxOf(cerca, 0.18f), SedaTinta.PAPEL))
        clipPath(forma) {
            pinceladas(r, a.sombra(1f), 870 + p.texto.length, 8, u, 0.05f)
            // El engrudo que asoma por los bordes y la esquina que se despega.
            drawRect(a.sombra(0.12f), Offset(r.left, r.bottom - 1.8f * u), Size(r.width, 1.8f * u))
        }
        drawPath(poligonoSeda(r.right - 6f * u, r.top + 0.4f * u, r.right, r.top + 0.4f * u, r.right - 1f * u, r.top + 4.5f * u), a.etiqueta(maxOf(cerca, 0.18f), mezcla(SedaTinta.PAPEL, SOMBRA_MUEBLE, 0.18f)))
        // El sello cuadrado de bermellón, a la izquierda.
        val s = minOf(r.height * 0.62f, 7f * u)
        val sello = Rect(r.left + 3f * u, r.center.y - s / 2, r.left + 3f * u + s, r.center.y + s / 2)
        drawRect(a.luz(SedaTinta.BERMELLON, maxOf(cerca, 0.3f)), sello.topLeft, sello.size)
        drawRect(a.etiqueta(maxOf(cerca, 0.18f), SedaTinta.PAPEL), sello.deflate(s * 0.22f).topLeft, sello.deflate(s * 0.22f).size, style = Stroke(0.6f * u))
        textoEn(medidor, p.texto, Rect(sello.right + 2f * u, r.top + 1.5f * u, r.right - 3f * u, r.bottom - 1.5f * u), LETRA, a.tinta, 10f, peso = FontWeight.Bold, minimo = 5f)
    }

    // ---------------------------------------------------------------- cada entrada

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenSeda(v, medidor, oscuro)
}

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/**
 * La curva de la luz de la lámpara: cuánta llega (de 1 a 0) a un punto que está a [dx] y [dy] de la llama, en anchos del
 * mueble. En un estante corto la luz se acaba antes, para que la última repisa quede siempre en penumbra.
 */
internal fun alcanceSeda(dx: Float, dy: Float, repisas: Int): Float {
    val radio = 1.0f + 0.1f * repisas.coerceIn(2, 5)
    return (1f - sqrt(dx * dx + dy * dy) / radio).coerceIn(0f, 1f)
}

/**
 * La luz cálida en el cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). Es la
 * de la lámpara; con [brasa] (los Sueños de noche), la del brasero del patio reflejada en la jamba.
 */
internal fun cercaCuerpoSeda(pos: Float, repisas: Int, fx: Float, brasa: Boolean = false): Float {
    val y = MuebleEstanteSeda.CORONA + pos * MuebleEstanteSeda.FILA / MuebleEstanteSeda.ANCHO_NOMINAL
    return if (brasa) brasaSeda(fx - MuebleEstanteSeda.BRASA_X, y - MuebleEstanteSeda.BRASA_Y)
    else alcanceSeda(fx - MuebleEstanteSeda.LAMP_X, y - MuebleEstanteSeda.LAMP_Y, repisas)
}

/** Lo que llega del brasero del patio (los Sueños de noche) a un punto a [dx] y [dy] de su reflejo: poco y cerca. */
internal fun brasaSeda(dx: Float, dy: Float): Float = ((1f - sqrt(dx * dx + dy * dy) / 0.85f) * 0.66f).coerceIn(0f, 1f)

/** Cuánta luna (o sol bajo) entra por la reja hasta un punto a [dx] y [dy] del centro de su mancha, en anchos del mueble. */
internal fun frioSeda(dx: Float, dy: Float): Float = ((1f - sqrt(dx * dx * 0.8f + dy * dy) / 0.95f) * 0.9f).coerceIn(0f, 1f)

/** La luna en el cuerpo del mueble, con la misma cuenta de repisas que [cercaCuerpoSeda]. */
internal fun frioCuerpoSeda(pos: Float, repisas: Int, fx: Float): Float =
    frioSeda(fx - MuebleEstanteSeda.REJA_X, MuebleEstanteSeda.CORONA - MuebleEstanteSeda.REJA_Y + pos * MuebleEstanteSeda.FILA / MuebleEstanteSeda.ANCHO_NOMINAL)

// ---------------------------------------------------------------- ayudantes de forma

internal fun corridoSeda(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeSeda(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoSeda(p, dx, dy), PathOperation.Difference) }

internal fun poligonoSeda(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}
