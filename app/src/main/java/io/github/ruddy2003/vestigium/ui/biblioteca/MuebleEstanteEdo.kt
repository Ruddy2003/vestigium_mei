package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * La paleta del cuarto de la casa de té de la posta (Japón Edo, hacia 1830): el enlucido de tierra clara y la madera ahumada
 * de la viga, el pilar y el zócalo de tablas; el castaño rojizo del estante y el hierro negro de sus herrajes; el papel de
 * caqui del fondo de los huecos; el washi, la tinta, el bermellón, el añil, el verde de té y el caqui de lo que se guarda; la
 * paja, el bambú y el cobre; y las luces del cuarto.
 */
internal object EdoTinta {
    /** El enlucido de tierra clara de la pared, su sombra y las pajas del amasado. */
    val KABE = Color(0xFFEAD8B6)
    val KABE_OSC = Color(0xFFC2A57A)
    val PAJA = Color(0xFFD8BE84)
    /** La madera ahumada de la viga, del pilar y del travesaño de la pared, y las tablas del zócalo de la pared. */
    val SUGI = Color(0xFF4A3326)
    val SUGI_LUZ = Color(0xFF7D5C43)
    val ITA = Color(0xFF6A4A35)
    /** El castaño (kuri) del estante: su cara, su luz, su sombra y su veta. */
    val KURI = Color(0xFF94582F)
    val KURI_LUZ = Color(0xFFC6874F)
    val KURI_OSC = Color(0xFF5A321B)
    val VETA = Color(0xFF3B2013)
    /** El papel de caqui (shibugami) con que se forró el fondo de los huecos: pardo rojo y lustroso. */
    val SHIBU = Color(0xFF5B2F1E)
    val SHIBU_LUZ = Color(0xFF8A5234)
    /** El hierro negro de los herrajes, con su luz. */
    val HIERRO = Color(0xFF1E1B1A)
    val HIERRO_LUZ = Color(0xFF5C5650)
    /** El washi, el papel viejo y la tinta. */
    val WASHI = Color(0xFFF4EEDF)
    val KAMI = Color(0xFFD9C9A3)
    val SUMI = Color(0xFF1B1A1E)
    /** Los tintes: bermellón, añil (y su claro), verde de té, caqui; el oro de los hilos. */
    val SHU = Color(0xFFC9442E)
    val AI = Color(0xFF1E3A5F)
    val AI_CLARO = Color(0xFF4A6E96)
    val MATCHA = Color(0xFF6F8444)
    val KAKI = Color(0xFFD27536)
    val ORO = Color(0xFFC9A24C)
    /** La paja, el bambú, el cobre, el hinoki claro de la cubeta y la tela sin teñir. */
    val WARA = Color(0xFFD2BC80)
    val BAMBU = Color(0xFFA39D5E)
    val COBRE = Color(0xFFB36F3D)
    val HINOKI = Color(0xFFE2C795)
    val KINARI = Color(0xFFD8CCAE)
    /** Las tablas del suelo, pulidas por los pasos. */
    val SUELO = Color(0xFF7A5034)

    // Las luces: la noche del cuarto, la de los Sueños, el anochecer de los Sueños en claro, la vela, la brasa, la luna y el ocaso.
    val NOCHE = Color(0xFF0C1226)
    val NOCHE_SUENOS = Color(0xFF0B1438)
    val ATARDECER = Color(0xFF5B4572)
    val LLAMA = Color(0xFFFFC56E)
    val BRASA = Color(0xFFE9683A)
    val LUNA = Color(0xFFC2D1F2)
    val SOL = Color(0xFFFFF2D2)
    val OCASO = Color(0xFFFFB27E)
}

/**
 * El aire del momento en el cuarto: de día (la luz del ranma de papel, arriba a la izquierda), de noche (el modo oscuro: el
 * farol de viaje colgado a la derecha, y la brasa de la bandeja del tabaco al pie) y en los Sueños ([noche]: el anochecer en
 * claro, con el color de cada cosa; la noche honda en oscuro, [lunar], con la luna en el papel del ranma). Como en el Ideario
 * del tema: de noche cada cosa va de su color hondo (oscuro, de su mismo tono, con el añil de la noche) a su color según lo que
 * le llega de las luces ([k], de 0 a 1), teñida de llama (o de brasa, en la parte [br] que viene de ella).
 */
internal class AireEdo(val oscuro: Boolean, val noche: Boolean) {
    /** Si arde el farol. */
    val lumbre: Boolean get() = oscuro || noche
    /** Los Sueños de noche: el farol hace un charco más corto y la luna entra por el ranma. */
    val lunar: Boolean get() = oscuro && noche
    val hondo: Color get() = if (oscuro) (if (noche) EdoTinta.NOCHE_SUENOS else EdoTinta.NOCHE) else SOMBRA_MUEBLE

    private fun abismo(c: Color): Color { val n = hondo; return Color(c.red * 0.13f + n.red * 0.8f, c.green * 0.13f + n.green * 0.8f, c.blue * 0.13f + n.blue * 0.8f, c.alpha) }
    private fun calor(br: Float) = if (br > 0.02f) mezcla(EdoTinta.LLAMA, EdoTinta.BRASA, br) else EdoTinta.LLAMA

    fun luz(c: Color, k: Float, f: Float = 0f, br: Float = 0f): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, EdoTinta.ATARDECER, 0.2f), FILO_MUEBLE, 0.14f * kk) else c
        var v = mezcla(abismo(c), c, (1.05f * kk).coerceAtMost(0.9f))
        v = mezcla(v, calor(br), 0.16f * kk)
        // En los Sueños, lo que mira al ranma se enfría con la luna (sin velar lo demás).
        if (lunar && f > 0.01f) v = mezcla(v, mezcla(v, EdoTinta.LUNA, 0.45f), (0.8f * f * (1f - kk)).coerceIn(0f, 1f))
        return v
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(if (noche) a * 1.15f else a)
    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama según lo que le llega. */
    fun brillo(a: Float, k: Float, br: Float = 0f): Color = when {
        !oscuro -> luzMueble(if (noche) a * 0.6f else a)
        else -> calor(br).copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))
    }
    fun claro(a: Float): Color = brillo(a, 0.35f)
    fun helado(a: Float): Color = EdoTinta.LUNA.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else 0f)

    /** El color del ánimo: de noche se hunde menos que lo demás, para que se siga adivinando lejos del farol. */
    fun animo(t: Color, k: Float, f: Float = 0f): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(t, EdoTinta.ATARDECER, 0.1f), FILO_MUEBLE, 0.06f * kk) else t
        val n = hondo
        val suave = Color(t.red * 0.32f + n.red * 0.6f, t.green * 0.32f + n.green * 0.6f, t.blue * 0.32f + n.blue * 0.6f, t.alpha)
        var v = mezcla(suave, t, (0.25f + 0.85f * kk).coerceAtMost(0.95f))
        v = mezcla(v, EdoTinta.LLAMA, 0.12f * kk)
        if (lunar && f > 0.01f) v = mezcla(v, mezcla(v, EdoTinta.LUNA, 0.4f), (0.7f * f * (1f - kk)).coerceIn(0f, 1f))
        return v
    }
    /** El papel donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(k: Float, base: Color = EdoTinta.WASHI): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, EdoTinta.ATARDECER, 0.08f) else base
        val ke = maxOf(kk, 0.25f)
        return mezcla(mezcla(base, hondo, 0.48f - 0.44f * ke), EdoTinta.LLAMA, 0.14f * ke)
    }
    val tinta: Color get() = if (oscuro) mezcla(EdoTinta.SUMI, hondo, 0.3f) else EdoTinta.SUMI
}

/**
 * El dibujo del estante del diario de Japón Edo (lo usa `EstanteEdo`, en `BibliotecaModerna.kt`): el cuarto de atrás de una
 * casa de té en una posta del camino real, hacia 1830, donde se guardan las cuentas, las cartas que dejan los viajeros y los
 * cuadernos que presta el librero ambulante. Es un estante del mismo carpintero que el tansu del Ideario: de castaño rojizo,
 * con dos postes gruesos achaflanados, tablas pasantes (nuki) que salen por fuera de los postes con su cuña, la tabla de arriba
 * volada con sus cantoneras de hierro, los herrajes en cada ensamble y, abajo, un cuerpo bajo con dos puertas correderas de
 * tabla con sus tiradores y su cerradura de hierro. Cada hueco tiene su hondura: los costados de dentro, la tabla de arriba
 * vista por debajo y, al fondo, el papel de caqui pardo rojo. Detrás, la pared de enlucido claro enmarcada por la viga (el
 * nageshi), el pilar y un zócalo de tablas ahumadas; arriba a la izquierda, el ranma de papel por donde entra la luz de día.
 * Encima del estante, un bodegón: los cojines de los huéspedes, el shamisen que dejó uno de ellos (que cruza la viga), el
 * ábaco de las cuentas, la botella de sake de la casa, las sandalias de paja colgadas de un clavo, el paraguas de papel
 * aceitado apoyado en el pilar y, del nageshi, el farol de viaje plegable, que de noche arde y es la luz del cuarto. Al pie, el
 * suelo de tablas, la cubeta del arroz, la bandeja del tabaco con su brasa y la escoba de la mañana. El muro y el suelo están
 * en `MuebleEstanteEdoMuro.kt`, el mueble en `MuebleEstanteEdoMueble.kt`, lo de arriba y del pie en
 * `MuebleEstanteEdoArriba.kt`, lo de los huecos en `MuebleEstanteEdoHuecos.kt` y los volúmenes en `MuebleEstanteEdoVolumenes.kt`.
 */
internal object MuebleEstanteEdo {
    val LETRA: FontFamily = LetraExtra.shippori

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.64f
    const val BASE = 0.42f
    const val LADO = 0.09f
    val ALTO_REPISA: Dp = 108.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 32).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el estuche de tela (chitsu); 1, la pila de cuadernos cosidos; 2, el hatillo de furoshiki. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 93)
        return if (x < 0.4f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El estuche, el más alto; el hatillo, panzón; la pila, la más baja (y más alta cuanto más escribiste). */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.88f + 0.06f * azar(semilla, 1)
        1 -> 0.7f + 0.14f * (palabras / 800f).coerceIn(0f, 1f) + 0.03f * azar(semilla, 1)
        else -> 0.8f + 0.06f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el farol (el centro de su papel), en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.815f
    const val LAMP_Y = 0.285f
    /** El centro del ranma (de día, el sol del cuarto de al lado; en los Sueños de noche, la luna). */
    const val RANMA_X = 0.2f
    const val RANMA_Y = 0.04f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 126f
    /** La brasa de la bandeja del tabaco: su x y su altura bajo la última tabla, en anchos del mueble. */
    const val BRASA_X = 0.575f
    const val BRASA_DY = 0.33f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireEdo(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoEdo(w, h, ch, d.izquierda.right, d.derecha.left, fondo, fila, n, a.lunar)

        // ---- lo fijo de arriba (la pared, la viga, el ranma, el farol y el bodegón) y lo fijo de abajo (el cuerpo bajo del
        // estante, el suelo y lo que hay al pie), pintados una vez por tamaño y modo
        val clave = "${w.toInt()}|${h.toInt()}|${ch.toInt()}|${fondo.toInt()}|${fila.toInt()}|$n|$oscuro|$noche|$u"
        val ranura = if (noche) 2 else 0
        with(CapaEdo) {
            pegar(ranura, "a$clave", Rect(0f, 0f, w, ch)) { coronaEdo(a, g); granoMueble(Rect(0f, 0f, w, ch), oscuro) }
            pegar(ranura + 1, "b$clave", Rect(0f, fondo, w, h)) { pieEdo(a, g); granoMueble(Rect(0f, fondo, w, h), oscuro) }
        }
        if (fondo - ch < 1f) return

        // ---- el cuerpo, entre la corona y el pie: la pared de los lados, el fondo de cada hueco, las tablas y los postes
        paredCostadosEdo(a, g)
        d.repisas.forEachIndexed { k, r -> huecoEdo(k, r, d.ocupado.getOrNull(k) ?: r.left, a, g) }
        d.tablas.forEachIndexed { k, r -> tablaPasanteEdo(k, r, a, g) }
        postesEdo(a, g, ch, fondo, d.tablas)
        escobaEdo(a, g, ch, fondo)

        // ---- lo que hay en los huecos libres
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            val c = Offset((oc + r.right) / 2, r.center.y)
            huecoLibreEdo(k, r, oc, a, g.cerca(c), g.frio(c))
        }
        // ---- el rótulo de cada mes: una tablilla de hinoki clavada en el canto
        d.placas.forEach { p -> rotuloEdo(p, medidor, a, g.cerca(p.r.center)) }
        granoMueble(Rect(0f, ch, w, fondo), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenEdo(v, medidor, oscuro)
}

/**
 * Las medidas del cuarto que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona
 * [ch], el borde de dentro de los postes ([x0], [x1]), el pie de la última tabla ([fondo]), una repisa con su tabla ([fila]) y
 * cuántas repisas hay ([n]). Y la luz que llega a cada punto: del farol y de la brasa ([cerca]), y de la luna por el ranma ([frio]).
 */
internal class MarcoEdo(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val fila: Float, val n: Int, val lunar: Boolean) {
    /** Lo que sale cada tabla por fuera de los postes, y dónde empieza y acaba cada poste. */
    val salida: Float get() = w * 0.03f
    val posteIzq: Float get() = salida
    val posteDer: Float get() = w - salida
    /** El canto de la tabla de arriba, lo que se ve de su cara y dónde se apoya lo de encima. */
    val gTapa: Float get() = w * 0.044f
    val fTapa: Float get() = w * 0.022f
    val apoyo: Float get() = ch - gTapa - fTapa * 0.45f
    /** El nageshi (la viga) y el travesaño (dōbuchi) donde empieza el zócalo de tablas ahumadas. */
    val nageshiArriba: Float get() = w * 0.084f
    val nageshiAbajo: Float get() = w * 0.118f
    val zocalo: Float get() = ch - w * 0.2f
    /** El cuerpo bajo de puertas correderas, el rincón de la pared con el suelo y donde pisan los postes. */
    val cuerpoBajo: Float get() = fondo + w * 0.2f
    val suelo: Float get() = fondo + w * 0.205f
    val pisan: Float get() = fondo + w * 0.235f
    /** Dónde está la brasa de la bandeja del tabaco. */
    val brasa: Offset get() = Offset(MuebleEstanteEdo.BRASA_X, fondo / w + MuebleEstanteEdo.BRASA_DY)

    fun cerca(p: Offset): Float = luzEdo(p.x / w, p.y / w, lunar, brasa)
    fun brasaEn(p: Offset): Float = brasaEdo(p.x / w, p.y / w, brasa)
    fun frio(p: Offset): Float = frioEdo(p.x / w, p.y / w)
}

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/** Lo que llega del farol a un punto ([fx], [fy] en anchos del mueble): cae con la distancia; con [corto] (los Sueños), antes. */
internal fun farolEdo(fx: Float, fy: Float, corto: Boolean): Float {
    val dx = fx - MuebleEstanteEdo.LAMP_X
    val dy = (fy - MuebleEstanteEdo.LAMP_Y) * (if (fy > MuebleEstanteEdo.LAMP_Y) 0.82f else 1f)
    return (1.6f * exp(-(if (corto) 3.4f else 2.75f) * sqrt(dx * dx + dy * dy))).coerceIn(0f, 1f)
}

/** El rescoldo de la brasa, en [b]: poco y muy cerca. */
internal fun brasaEdo(fx: Float, fy: Float, b: Offset): Float {
    val dx = fx - b.x; val dy = fy - b.y
    return 0.7f * exp(-3.4f * sqrt(dx * dx + dy * dy))
}

/** Toda la luz cálida que llega a un punto (farol y brasa), de 0 a 1. */
internal fun luzEdo(fx: Float, fy: Float, corto: Boolean, b: Offset): Float = (farolEdo(fx, fy, corto) + brasaEdo(fx, fy, b)).coerceIn(0.02f, 1f)

/** Cuánta luna (o luz del cuarto de al lado) entra por el ranma hasta un punto. */
internal fun frioEdo(fx: Float, fy: Float): Float {
    val dx = fx - MuebleEstanteEdo.RANMA_X; val dy = fy - MuebleEstanteEdo.RANMA_Y
    return (1.15f * exp(-2.3f * sqrt(dx * dx * 0.8f + dy * dy))).coerceIn(0f, 1f)
}

/** Para un volumen: dónde cae en anchos del mueble ([repisa] desde arriba, de [repisas], y su centro [centro] de 0 a 1). */
internal fun lugarVolumenEdo(repisa: Int, repisas: Int, centro: Float): Offset {
    val fx = MuebleEstanteEdo.LADO + centro * (1f - 2 * MuebleEstanteEdo.LADO)
    val fy = MuebleEstanteEdo.CORONA + (repisa + 0.55f) * MuebleEstanteEdo.FILA / MuebleEstanteEdo.ANCHO_NOMINAL
    return Offset(fx, fy)
}

internal fun brasaNominalEdo(repisas: Int): Offset =
    Offset(MuebleEstanteEdo.BRASA_X, MuebleEstanteEdo.CORONA + repisas * MuebleEstanteEdo.FILA / MuebleEstanteEdo.ANCHO_NOMINAL + MuebleEstanteEdo.BRASA_DY)

// ---------------------------------------------------------------- lo fijo pintado una vez

/**
 * Lo fijo pintado una vez: cuatro imágenes (la franja de arriba y la de abajo, del diario y de los Sueños), cada una con la
 * clave de su tamaño y su modo; si la clave cambia, se vuelve a pintar. Pegar una imagen cuesta mucho menos que repetir los
 * degradados y las capas de la pared y del bodegón en cada dibujo. Las franjas no crecen con las repisas.
 */
internal object CapaEdo {
    private val claves = arrayOfNulls<String>(4)
    private val imagenes = arrayOfNulls<ImageBitmap>(4)

    fun DrawScope.pegar(ranura: Int, clave: String, r: Rect, pintar: DrawScope.() -> Unit) {
        val ancho = r.width.roundToInt(); val alto = r.height.roundToInt()
        if (ancho < 1 || alto < 1 || ranura !in 0..3) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                translate(-r.left, -r.top) { clipRect(r.left, r.top, r.right, r.bottom) { pintar() } }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave; img = nueva
        }
        drawImage(img, Offset(r.left, r.top))
    }
}

// ---------------------------------------------------------------- ayudantes de forma

internal fun corridoEdo(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeEdo(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoEdo(p, dx, dy), PathOperation.Difference) }

internal fun poligonoEdo(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}
