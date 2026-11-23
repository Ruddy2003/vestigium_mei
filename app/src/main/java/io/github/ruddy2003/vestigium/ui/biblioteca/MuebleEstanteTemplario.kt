package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
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
 * Lo fijo de arriba del estante (la pared, el ajimez, la cornisa y el bodegón), pintado una vez por tamaño y modo en una
 * imagen guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de la corona, sobre un fondo opaco, así la
 * junta con el cuerpo no deja asomar el fondo. Se guarda solo la última (cambia de tamaño o de modo muy pocas veces). El
 * pie se pinta en vivo.
 */
internal object CapaTemplario {
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
 * La mancha de luz que entra por el ajimez (en el costado de la izquierda) y cae en la pared del fondo y en el armario: las dos
 * luces del ajimez con su remate de medio punto y, entre ellas, la sombra de la columnilla. [q] es el cuadrilátero de la mancha
 * en el plano de la pared, en anchos (arriba izquierda, arriba derecha, abajo derecha, abajo izquierda); lo que está más
 * adelante (la cornisa, las tablas, los costados) la recibe corrida [FRENTE] a la izquierda. [fuerza] es lo que aclara.
 */
internal class ManchaTemplario(val q: FloatArray, val color: Color, val fuerza: Float) {
    /** Un punto de la mancha: [fx] a lo ancho, [fy] a lo alto, de 0 a 1, en anchos, corrido [corrido]. */
    fun en(fx: Float, fy: Float, corrido: Float = 0f): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset(tx + (bx - tx) * fy + corrido, ty + (by - ty) * fy)
    }

    /** Las dos luces del ajimez proyectadas, en un camino, en un lienzo de ancho [w] (sin la columnilla); [crece], la penumbra. */
    fun forma(w: Float, corrido: Float = 0f, crece: Float = 0f): Path {
        val p = Path()
        for (i in 0 until 2) {
            val x0 = (if (i == 0) 0f else 0.53f) - crece; val x1 = (if (i == 0) 0.47f else 1f) + crece
            val arco = 0.17f
            val n = 7
            var primero = true
            for (j in 0..n) {
                val t = j / n.toFloat()
                val fx = x0 + (x1 - x0) * t
                val fy = arco * (1f - sqrt((1f - (2f * t - 1f) * (2f * t - 1f)).coerceAtLeast(0f))) - crece
                val o = en(fx, fy, corrido)
                if (primero) { p.moveTo(o.x * w, o.y * w); primero = false } else p.lineTo(o.x * w, o.y * w)
            }
            val b1 = en(x1, 1f + crece, corrido); val b0 = en(x0, 1f + crece, corrido)
            p.lineTo(b1.x * w, b1.y * w); p.lineTo(b0.x * w, b0.y * w); p.close()
        }
        return p
    }

    /** Cuánto le toca a ([x], [y]) (anchos) del plano corrido [corrido]: 1 dentro de una de las dos luces, 0 fuera. */
    fun toca(x: Float, y: Float, corrido: Float = 0f): Float {
        // Se busca (fx, fy) con el cuadrilátero aproximado como un paralelogramo (sus lados de arriba y abajo).
        val ax = q[0] + corrido; val ay = q[1]
        val ux = q[2] - q[0]; val uy = q[3] - q[1]
        val vx = q[6] - q[0]; val vy = q[7] - q[1]
        val det = ux * vy - uy * vx
        if (kotlin.math.abs(det) < 1e-6f) return 0f
        val px = x - ax; val py = y - ay
        val fx = (px * vy - py * vx) / det
        val fy = (ux * py - uy * px) / det
        if (fx < 0f || fx > 1f || fy < 0f || fy > 1f) return 0f
        if (fx > 0.47f && fx < 0.53f) return 0f
        return 1f
    }

    companion object {
        /** Cuánto se corre el plano del frente del mueble respecto de la pared, en anchos (la luz entra desde la izquierda). */
        const val FRENTE = -0.03f
    }
}

/** La mancha del ajimez en este momento, o null (de noche manda la lumbre y el ajimez solo enseña el cielo). */
internal fun manchaTemplario(a: AireTemplario): ManchaTemplario? = when {
    // Las luces proyectadas van tendidas y alargadas por el ángulo de la luz: la cabeza de arco arriba, junto al ajimez, y el
    // cuerpo corrido abajo a la derecha. La luna, más alta en el cielo y más tenue, cae más abajo y más empinada.
    a.oscuro && a.noche -> ManchaTemplario(floatArrayOf(0.36f, 0.2f, 0.5f, 0.24f, 0.78f, 0.96f, 0.64f, 0.92f), PaletaTemplario.LUNA, 0.6f)
    a.oscuro -> null
    // Al ocaso, el sol bajo: las luces se tienden casi horizontales y muy largas.
    a.noche -> ManchaTemplario(floatArrayOf(0.17f, 0.09f, 0.31f, 0.17f, 0.78f, 0.36f, 0.64f, 0.28f), PaletaTemplario.OCASO, 1f)
    else -> ManchaTemplario(floatArrayOf(0.165f, 0.115f, 0.385f, 0.16f, 0.6f, 0.6f, 0.38f, 0.555f), PaletaTemplario.SOL, 0.9f)
}

/**
 * La luz del ajimez sobre un objeto de silueta [sil] (lo de encima del armario, lo colgado): solo donde la mancha lo alcanza,
 * con el borde de la mancha cortándolo, en el plano corrido [corrido]. Se suma a lo que ya está pintado (de día, la cal
 * entibiada; al ocaso, el naranja; con la luna, un plata frío).
 */
internal fun DrawScope.luzDelAjimez(sil: Path, a: AireTemplario, m: ManchaTemplario?, corrido: Float = ManchaTemplario.FRENTE * 0.6f) {
    if (m == null) return
    val (col, alfa) = when {
        a.oscuro -> Color(0xFF6A82BE) to 0.3f
        a.noche -> PaletaTemplario.OCASO to 0.42f
        else -> Color(0xFFFFC978) to 0.34f
    }
    clipPath(sil) { clipPath(m.forma(size.width, corrido)) { drawRect(col.copy(alpha = alfa * m.fuerza), Offset.Zero, size, blendMode = BlendMode.Screen) } }
}

/**
 * El dibujo del estante del diario de Templario (lo usa `EstanteTemplario`, en `BibliotecaMedievo.kt`): el armario-estante de
 * cedro de la cámara alta de la torre, donde el comendador guarda los libros y las cartas de la casa. La cornisa volada con su
 * taqueado y el friso tallado en dientes de sierra, los costados gruesos con su baquetón, las tablas molduradas con sus
 * escuadras de hierro, el fondo de verde tierra y el zócalo de arquillos ciegos. Contra el enlucido de cal con su despiece
 * fingido a la almagra, en la esquina con el costado donde está el ajimez (de día, su mancha de sol en la cal; de noche, el
 * cielo de la vigilia). De noche arden la lámpara de vidrio (arriba a la derecha), el candil de barro de un hueco y el brasero
 * del pie. La paleta y la luz están en [PaletaTemplario] y [AireTemplario]; la pared y el ajimez, en [MuroEstanteTemplario];
 * el mueble, en [ObraEstanteTemplario]; el bodegón, en [CoronaEstanteTemplario] y lo colgado (la bocina y la lámpara), en
 * [ColgadoEstanteTemplario]; lo de los huecos, en [HuecosEstanteTemplario]; el pie, en [PieEstanteTemplario]; las entradas,
 * en [VolumenEstanteTemplario].
 */
internal object MuebleEstanteTemplario {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteTemplario` las lee al dibujar)

    val CORONA = 0.56f
    val BASE = 0.3f
    val LADO = 0.08f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el libro de la casa; 1, el estuche de cuero hervido; 2, la escarcela de las cartas. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 11)) * 31 + (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el libro, el más alto; el estuche; la escarcela, más baja. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.06f * azar(semilla, 1)
        1 -> 0.83f + 0.07f * azar(semilla, 1)
        else -> 0.78f + 0.06f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la lámpara de vidrio, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.79f
    const val LAMP_Y = 0.3f
    /** Dónde está el brasero: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val BRASERO_X = 0.25f
    const val BRASERO_Y = 0.6f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 134f
    /** La cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.105f
    /** En la base: dónde acaba el zócalo y empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.34f
    /** El candil de un hueco: a qué alto de su repisa arde (en fracción del hueco, desde abajo). */
    const val CANDIL_Y = 0.5f

    /** La luz de la lámpara: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos del mueble. */
    fun lampara(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.9f else 1.55f) * exp(-2.9f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.5f else 1f)))).coerceIn(0f, 1f)

    /** La luz de la lámpara en la pared y en lo de arriba: más corta que en el cuerpo, para que lo lejano se hunda. */
    fun lamparaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.8f else 1.5f) * exp(-3.7f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.02f, 1f)

    /** La luz del brasero del pie: roja y baja, para las repisas de abajo. */
    fun brasero(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.9f else 1.45f) * exp(-2.15f * sqrt(dx * dx * 1.15f + dy * dy))).coerceIn(0f, 1f)

    /** La luz del candil de un hueco: corta, para sus vecinos, el fondo y el canto de la tabla de arriba. */
    fun candil(dx: Float, dy: Float): Float = (1.05f * exp(-4.4f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** El candil que más alumbra ([x], [y]) entre [candiles] (x, y, x, y… en anchos). */
    private fun candilMayor(x: Float, y: Float, candiles: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < candiles.size) { m = maxOf(m, candil(x - candiles[i], y - candiles[i + 1])); i += 2 }
        return m
    }

    /** Cuánta luz cálida llega a ([x], [y]) (anchos): la lámpara, el brasero a la altura [yBrasero] y los [candiles]. */
    fun calor(x: Float, y: Float, yBrasero: Float, suenos: Boolean, candiles: FloatArray): Float {
        val kl = lampara(x - LAMP_X, y - LAMP_Y, suenos)
        val kb = brasero(x - BRASERO_X, y - yBrasero, suenos)
        val kc = candilMayor(x, y, candiles)
        return (1f - (1f - kl) * (1f - kb) * (1f - kc)).coerceIn(0.03f, 1f)
    }

    /** Dónde arde el brasero, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yBraseroNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * BRASERO_Y

    /**
     * Dónde va el candil del hueco de cada repisa (en x del lienzo), o -1 si no lleva. Arde en los huecos con sitio, pero nunca
     * en dos repisas seguidas, para que no haya una reja de llamas. Lo usan el estante (para alumbrar) y los huecos.
     */
    fun candilesEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.022f; val x1 = d.derecha.left - w * 0.028f
            if (x1 - x0 < w * 0.16f || (k > 0 && xs[k - 1] >= 0f)) continue
            xs[k] = x0 + w * 0.04f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireTemplario(oscuro, noche)
        val ac = AireTemplario(oscuro, noche, cuerpo = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yB = (b.top + b.height * BRASERO_Y) / w
        // Los candiles de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(d.repisas.size) { -1f }
        val candL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        val xs = candilesEn(d, w)
        d.repisas.forEachIndexed { k, r ->
            val x = xs[k]
            if (x < 0f) return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            candL.add(x / w); candL.add((r.bottom - r.height * CANDIL_Y) / w)
        }
        val candiles = if (a.lumbre) candL.toFloatArray() else FloatArray(0)
        LucesHuecoTemplario.poner(LucesHuecoTemplario.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < ch) lamparaArriba(p.x / w - LAMP_X, p.y / w - LAMP_Y, noche)
            else calor(p.x / w, p.y / w, yB, noche, candiles)
        }
        val m = manchaTemplario(a)
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, el ajimez, la cornisa y el bodegón, en una imagen que llega al píxel entero de debajo.
        with(CapaTemplario) {
            pegar(0, "c:$clave", 0, ceil(ch).toInt()) {
                drawRect(a.luz(PaletaTemplario.CEDRO_OSC, 0.5f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteTemplario) { pared(a, apoyo, cerca, m) }
                with(CoronaEstanteTemplario) { colgado(a, apoyo, cerca, m) }
                with(ObraEstanteTemplario) { cornisa(Rect(0f, apoyo, w, ch + 2f), a, cerca, m) }
                with(CoronaEstanteTemplario) { encima(a, apoyo, ch, cerca, m) }
                with(MuroEstanteTemplario) { luzDeNoche(a, ch) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, los costados y las tablas.
        with(ObraEstanteTemplario) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, d, ac, cerca, m) }
        }
        with(HuecosEstanteTemplario) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xs[k], ac, cerca)
            }
        }
        with(ObraEstanteTemplario) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, d, ac, cerca, m) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, ac, cerca, m)
            costado(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, ac, cerca, m)
            d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center)) }
        }
        with(HuecosEstanteTemplario) { d.repisas.forEachIndexed { k, r -> if (xs[k] >= 0f) llamaHueco(xs[k], r, ac) } }
        // Lo de abajo, en vivo (así lo guardado queda en unos 2 MB por modo): el zócalo y el suelo con lo que hay en él, desde
        // el píxel entero de encima del zócalo y sobre un fondo opaco, para que la junta con la última tabla no deje asomar
        // nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(ac.luz(PaletaTemplario.CEDRO_OSC, cerca(Offset(w / 2, b.top))), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val suelo = b.top + b.height * SUELO
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yB, noche, FloatArray(0)) }
        with(ObraEstanteTemplario) { zocalo(Rect(0f, arriba, w, suelo), ac, cercaPie) }
        with(PieEstanteTemplario) { alPie(ac, b, suelo, h, cercaPie) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireTemplario(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val candiles = if (a.lumbre) LucesHuecoTemplario.candiles(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yBn = yBraseroNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yBn, v.noche, candiles) else 1f
        // De qué lado le llega la luz: de día, del ajimez (izquierda); de noche, de la luz que más alumbra.
        val kl = lampara(fx - LAMP_X, fy - LAMP_Y, v.noche)
        val kb = brasero(fx - BRASERO_X, fy - yBn, v.noche)
        var kc = 0f; var xc = fx
        var i = 0
        while (i + 1 < candiles.size) { val e = candil(fx - candiles[i], fy - candiles[i + 1]); if (e > kc) { kc = e; xc = candiles[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kc >= kl && kc >= kb -> if (xc >= fx) 1f else -1f
            kl >= kb -> if (fx < LAMP_X) 1f else -1f
            else -> if (fx < BRASERO_X) 1f else -1f
        }
        // La luna de los Sueños: cuánto le toca a su frente.
        val mz = manchaTemplario(a)
        val luna = if (a.oscuro && mz != null) mz.toca(fx, fy, ManchaTemplario.FRENTE * 0.5f) else 0f
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val piel = cueroTemplario(v.tono, oscuro)
        with(VolumenEstanteTemplario) {
            when (clase(v.semilla)) {
                // Alguno, ladeado contra el vecino de la izquierda.
                0 -> if (azar(v.semilla, 21) > 0.8f) rotate(-3.2f, Offset(0f, size.height)) { libro(v, medidor, a, kd, hacia, piel, luna) }
                    else libro(v, medidor, a, kd, hacia, piel, luna)
                1 -> estuche(v, medidor, a, kd, hacia, piel, luna)
                else -> escarcela(v, medidor, a, kd, hacia, piel, luna)
            }
        }
    }
}

/**
 * Dónde arden los candiles de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en
 * su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro del candil en la misma
 * medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoTemplario {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Los candiles de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguno si no se apuntaron. */
    fun candiles(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val lado = MuebleEstanteTemplario.LADO
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(lado + c[k] * (1f - 2 * lado))
            l.add(MuebleEstanteTemplario.CORONA + k * MuebleEstanteTemplario.FILA / MuebleEstanteTemplario.ANCHO_NOMINAL +
                (MuebleEstanteTemplario.ALTO_REPISA.value * (1f - MuebleEstanteTemplario.CANDIL_Y)) / MuebleEstanteTemplario.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
