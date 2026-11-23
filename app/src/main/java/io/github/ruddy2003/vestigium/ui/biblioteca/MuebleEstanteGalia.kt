package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Lo fijo de arriba del estante (el alero, la pared, la cenefa, la tea, lo colgado, el dintel y el bodegón), pintado una vez
 * por tamaño y modo en una imagen guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de la corona, sobre
 * un fondo opaco, así la junta con el cuerpo no deja asomar el fondo. Se guarda solo la última (cambia de tamaño o de modo
 * muy pocas veces). El pie se pinta en vivo.
 */
internal object CapaGalia {
    private val claves = arrayOfNulls<String>(1)
    private val imagenes = arrayOfNulls<ImageBitmap>(1)

    /** Pega la imagen de las filas [arriba] a [abajo] (enteras) de todo el ancho; la pinta antes con [pintar] si cambió. */
    fun DrawScope.pegar(ranura: Int, clave: String, arriba: Int, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = abajo - arriba
        if (ancho < 1 || alto < 1 || ranura !in imagenes.indices) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                translate(0f, -arriba.toFloat()) { pintar() }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave; img = nueva
        }
        drawImage(img, Offset(0f, arriba.toFloat()))
    }
}

/**
 * El dibujo del estante del diario de Galia (lo usa `EstanteGalia`, en `BibliotecaGalia.kt`): el estante de las cuentas de la
 * casa grande de un caballero arverno, en Gergovia, en el 52 a. C. Un bastidor de roble labrado a azuela contra la pared de
 * barro: los postes gruesos con su chaflán y su acanaladura, las tablas que los pasan a caja y espiga con su cuña, las
 * escuadras de hierro forjado, el dintel tallado a punta de cuchillo y la solera sobre calzos de piedra. Detrás, el barro
 * bañado de almagra; arriba, el barro blanqueado con su cenefa y el alero por dentro, con los cabrios ahumados. Sin ventana:
 * de día entra el sol por la puerta (a la izquierda, fuera del cuadro); de noche arden la tea de pino (arriba a la derecha),
 * la lamparilla de sebo de los huecos con sitio y el hogar del pie. La paleta y la luz están en [PaletaGalia] y [AireGalia];
 * el alero y la pared, en [MuroEstanteGalia]; el dintel, los casilleros, las tablas, los postes, los rótulos y la solera, en
 * [ObraEstanteGalia]; el bodegón de arriba, en [CoronaEstanteGalia]; lo de los huecos, en [HuecosEstanteGalia]; el pie, en
 * [PieEstanteGalia]; las entradas, en [VolumenEstanteGalia].
 */
internal object MuebleEstanteGalia {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteGalia` las lee al dibujar)

    val CORONA = 0.58f
    val BASE = 0.3f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, la funda de lana a cuadros; 1, el cofre de mimbre; 2, el zurrón de cuero. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 9)) * 29 + (semilla ushr 4), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: la funda, la más alta; el cofre; el zurrón, más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.07f * azar(semilla, 1)
        1 -> 0.8f + 0.08f * azar(semilla, 1)
        else -> 0.79f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la tea, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val TEA_X = 0.866f
    const val TEA_Y = 0.232f
    /** Dónde está el hogar: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val HOGAR_X = 0.27f
    const val HOGAR_Y = 0.62f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 134f
    /** El dintel, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val DINTEL = 0.11f
    /** En la base: dónde acaba la solera y empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.3f
    /** La lamparilla de un hueco: a qué alto de su repisa arde (en fracción del hueco, desde abajo). */
    const val LAMPARILLA_Y = 0.24f

    /** La luz de la tea: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos del mueble. */
    fun tea(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.8f else 1.4f) * exp(-3.4f * sqrt(dx * dx * 1.15f + dy * dy * (if (dy > 0f) 0.6f else 1f)))).coerceIn(0f, 1f)

    /** La luz de la tea en la pared y en lo de arriba: más corta que en el cuerpo, para que lo lejano se hunda. */
    fun teaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.45f) * exp(-3.8f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.02f, 1f)

    /** La luz del hogar del pie: roja y baja, para las repisas de abajo. */
    fun hogar(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.85f else 1.4f) * exp(-2.9f * sqrt(dx * dx * 1.1f + dy * dy))).coerceIn(0f, 1f)

    /** La luz de la lamparilla de un hueco: corta, para sus vecinos, el fondo y el canto de la tabla de arriba. */
    fun lamparilla(dx: Float, dy: Float): Float = (1.0f * exp(-5.6f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** La lamparilla que más alumbra ([x], [y]) entre [lamparillas] (x, y, x, y… en anchos). */
    private fun lamparillaMayor(x: Float, y: Float, lamparillas: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < lamparillas.size) { m = maxOf(m, lamparilla(x - lamparillas[i], y - lamparillas[i + 1])); i += 2 }
        return m
    }

    /** Cuánta luz cálida llega a ([x], [y]) (anchos): la tea, el hogar a la altura [yHogar] y las [lamparillas]. */
    fun calor(x: Float, y: Float, yHogar: Float, suenos: Boolean, lamparillas: FloatArray): Float {
        val kt = tea(x - TEA_X, y - TEA_Y, suenos)
        val kh = hogar(x - HOGAR_X, y - yHogar, suenos)
        val kl = lamparillaMayor(x, y, lamparillas)
        return (1f - (1f - kt) * (1f - kh) * (1f - kl)).coerceIn(0.03f, 1f)
    }

    /** Dónde arde el hogar, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yHogarNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * HOGAR_Y

    /**
     * Dónde va la lamparilla del hueco de cada repisa (en x del lienzo), o -1 si no lleva. Arde en los huecos con sitio, pero
     * nunca en dos repisas seguidas, para que no haya una reja de llamas. Lo usan el estante (para alumbrar) y los huecos.
     */
    fun lamparillasEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.022f; val x1 = d.derecha.left - w * 0.028f
            if (x1 - x0 < w * 0.17f || (k > 0 && xs[k - 1] >= 0f)) continue
            xs[k] = x0 + w * 0.05f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireGalia(oscuro, noche)
        val ac = AireGalia(oscuro, noche, cuerpo = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * DINTEL
        val yH = (b.top + b.height * HOGAR_Y) / w
        // Las lamparillas de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(d.repisas.size) { -1f }
        val lamL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        val xs = lamparillasEn(d, w)
        d.repisas.forEachIndexed { k, r ->
            val x = xs[k]
            if (x < 0f) return@forEachIndexed
            // La llama arde sobre el pico del cuenco, un poco a la derecha de donde empieza la lamparilla.
            val xl = x + w * 0.04f
            centros[k] = ((xl - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            lamL.add(xl / w); lamL.add((r.bottom - r.height * LAMPARILLA_Y) / w)
        }
        val lamparillas = if (a.lumbre) lamL.toFloatArray() else FloatArray(0)
        LucesHuecoGalia.poner(LucesHuecoGalia.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < ch) teaArriba(p.x / w - TEA_X, p.y / w - TEA_Y, noche)
            else calor(p.x / w, p.y / w, yH, noche, lamparillas)
        }
        val m = manchaGalia(a)
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: el alero, la pared, lo colgado, el dintel y el bodegón, en una imagen que llega al píxel entero de debajo.
        with(CapaGalia) {
            pegar(0, "c:$clave", 0, ceil(ch).toInt()) {
                drawRect(a.luz(PaletaGalia.ROBLE_OSC, 0.5f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteGalia) { pared(a, apoyo, cerca, m) }
                with(CoronaEstanteGalia) { colgado(a, apoyo, cerca, m) }
                with(ObraEstanteGalia) { dintel(Rect(0f, apoyo, w, ch + 2f), a, cerca, m) }
                with(CoronaEstanteGalia) { encima(a, apoyo, ch, cerca, m) }
                with(MuroEstanteGalia) { teaEncendida(a, cerca) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, los postes y las tablas.
        with(ObraEstanteGalia) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, ac, cerca, m) }
        }
        with(HuecosEstanteGalia) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xs[k], ac, cerca)
            }
        }
        with(ObraEstanteGalia) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, ac, cerca, m) }
            poste(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, ac, cerca, m)
            poste(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, ac, cerca, m)
            d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center)) }
        }
        with(HuecosEstanteGalia) { d.repisas.forEachIndexed { k, r -> if (xs[k] >= 0f) llamaHueco(xs[k], r, ac) } }
        // Lo de abajo, en vivo: la solera y el suelo con lo que hay en él, desde el píxel entero de encima de la solera y sobre
        // un fondo opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(ac.luz(PaletaGalia.ROBLE_OSC, cerca(Offset(w / 2, b.top))), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val suelo = b.top + b.height * SUELO
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yH, noche, FloatArray(0)) }
        with(ObraEstanteGalia) { solera(Rect(0f, arriba, w, suelo), ac, cercaPie) }
        with(PieEstanteGalia) { alPie(ac, b, suelo, h, cercaPie) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireGalia(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val lamparillas = if (a.lumbre) LucesHuecoGalia.lamparillas(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yHn = yHogarNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yHn, v.noche, lamparillas) else 1f
        // De qué lado le llega la luz: de día, de la puerta (izquierda); de noche, de la luz que más alumbra.
        val kt = tea(fx - TEA_X, fy - TEA_Y, v.noche)
        val kh = hogar(fx - HOGAR_X, fy - yHn, v.noche)
        var kl = 0f; var xl = fx
        var i = 0
        while (i + 1 < lamparillas.size) { val e = lamparilla(fx - lamparillas[i], fy - lamparillas[i + 1]); if (e > kl) { kl = e; xl = lamparillas[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kl >= kt && kl >= kh -> if (xl >= fx) 1f else -1f
            kt >= kh -> if (fx < TEA_X) 1f else -1f
            else -> if (fx < HOGAR_X) 1f else -1f
        }
        // La luz de la puerta (el sol de día, el ocaso o la luna de los Sueños): cuánto le toca a su frente.
        val mz = manchaGalia(a)
        val sol = if (mz != null) mz.toca(fx, fy, ManchaGalia.FRENTE * 0.5f) else 0f
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val tinte = tonoDeDiaGalia(v.tono, oscuro)
        with(VolumenEstanteGalia) {
            when (clase(v.semilla)) {
                // Alguna, ladeada contra la vecina de la izquierda.
                0 -> if (azar(v.semilla, 21) > 0.78f) rotate(-3f, Offset(0f, size.height)) { funda(v, medidor, a, kd, hacia, tinte, sol) }
                    else funda(v, medidor, a, kd, hacia, tinte, sol)
                1 -> cofre(v, medidor, a, kd, hacia, tinte, sol)
                else -> zurron(v, medidor, a, kd, hacia, tinte, sol)
            }
        }
    }
}

/**
 * Dónde arden las lamparillas de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja
 * después, en su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro de la
 * lamparilla en la misma medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoGalia {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Las lamparillas de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun lamparillas(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val lado = MuebleEstanteGalia.LADO
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(lado + c[k] * (1f - 2 * lado))
            l.add(MuebleEstanteGalia.CORONA + k * MuebleEstanteGalia.FILA / MuebleEstanteGalia.ANCHO_NOMINAL +
                (MuebleEstanteGalia.ALTO_REPISA.value * (1f - MuebleEstanteGalia.LAMPARILLA_Y)) / MuebleEstanteGalia.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
