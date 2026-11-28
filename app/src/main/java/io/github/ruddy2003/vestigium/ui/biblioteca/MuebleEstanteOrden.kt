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
 * Lo fijo de arriba del estante de Orden (el muro de placas con su arquería, el bodegón y la cornisa con sus torrecillas),
 * pintado una vez por tamaño y momento en una imagen guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de
 * la corona, sobre un fondo opaco, así la junta con el cuerpo no deja asomar el fondo. Se guarda solo la última.
 */
internal object CapaOrden {
    private var clave: String? = null
    private var imagen: ImageBitmap? = null

    /** Pega la imagen de las filas [arriba] a [abajo] (enteras) de todo el ancho; la pinta antes con [pintar] si cambió. */
    fun DrawScope.pegar(nueva: String, arriba: Int, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = abajo - arriba
        if (ancho < 1 || alto < 1) return
        var img = imagen
        if (img == null || clave != nueva) {
            val lienzo = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(lienzo), Size(ancho.toFloat(), alto.toFloat())) {
                translate(0f, -arriba.toFloat()) { pintar() }
            }
            imagen = lienzo; clave = nueva; img = lienzo
        }
        drawImage(img, Offset(0f, arriba.toFloat()))
    }
}

/**
 * El dibujo del estante del diario de Orden (lo usa `EstanteOrden`, en `BibliotecaFuturo.kt`): la librería encadenada de la sala
 * capitular, en el arco de al lado del de la cajonera de planos del Ideario, contra el mismo muro de placas de acero con su
 * arquería ciega. Pilares de viga remachada con capitel y basa de latón, la cornisa honda con su friso de esmalte carmesí y
 * sus torrecillas, repisas de perfil en U con la barra de latón de las cadenas y el guardapolvo calado en arquitos apuntados;
 * entre tabla y tabla, el muro en su sombra. Arriba, la reliquia bajo su fanal, los manuales con el candelero, el rollo de
 * planos apoyado y las llaves del archivo; al pie, el farol de ronda, la aceitera y el bidón. De día, la luminaria enrejada de
 * arriba a la izquierda; de noche, las velas (la del candelero, una en un hueco y la del farol) y el piloto rojo del pilar,
 * con lo lejano en el azul hondo del acero. La paleta y la luz están en [PaletaOrden] y [AireOrden]; la pared, en
 * [MuroEstanteOrden]; el mueble, en [ObraEstanteOrden]; el bodegón, en [CoronaEstanteOrden]; los huecos, en
 * [HuecosEstanteOrden]; el pie, en [PieEstanteOrden]; las entradas, en [VolumenEstanteOrden].
 */
internal object MuebleEstanteOrden {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteOrden` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.3f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 114.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 45).coerceAtMost(16)).dp

    /** Cuál de las tres es una entrada: 0, el manual encadenado; 1, el carcaj de planos; 2, la carpeta de campaña. */
    fun clase(semilla: Int): Int {
        var x = semilla * 0x2C1B3C6D
        x = x xor (x ushr 13)
        val k = (x and 0x7fffffff) % 7
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Qué alto tiene cada una, en fracción del casillero: el carcaj con sus rollos, el más alto; la carpeta, la más baja. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.83f + 0.08f * azar(semilla, 1)
        1 -> 0.86f + 0.06f * azar(semilla, 1)
        else -> 0.8f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la vela del candelero de la corona (el centro de la llama), en anchos del mueble. */
    const val VELA_X = 0.672f
    const val VELA_Y = 0.262f
    /** Dónde arde el farol de ronda del pie: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val FAROL_X = 0.135f
    const val FAROL_Y = 0.56f
    /** El ancho con que un volumen pasa su repisa a anchos si el estante no apuntó el suyo: el de un teléfono, en dp. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 136f
    /** La cornisa, en anchos: de donde pisan las cosas de arriba hasta el primer casillero. */
    const val CORNISA = 0.135f
    /** En la base: dónde acaba el zócalo y empieza el suelo, en fracción de su alto. */
    const val ZOCALO = 0.36f
    /** La vela de un hueco: a qué alto de su repisa arde la llama (en fracción del hueco, desde abajo). */
    const val VELA_HUECO_Y = 0.56f

    /** La luz de la vela del candelero en el cuerpo: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos. */
    fun vela(dx: Float, dy: Float): Float =
        (1.45f * exp(-3.3f * sqrt(dx * dx + dy * dy * (if (dy > 0f) 0.55f else 1f)))).coerceIn(0f, 1f)

    /** La misma vela en el muro y en lo de arriba: más corta, para que lo lejano se hunda. */
    fun velaArriba(dx: Float, dy: Float): Float = (1.4f * exp(-4.2f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0f, 1f)

    /** La luz del farol del pie: baja, para las repisas de abajo. */
    fun farol(dx: Float, dy: Float): Float = (1.4f * exp(-3.3f * sqrt(dx * dx * 1.15f + dy * dy * 1.25f))).coerceIn(0f, 1f)

    /** La vela de un hueco: corta, para sus vecinos, el fondo y el canto de las tablas de arriba y de abajo. */
    fun velaHueco(dx: Float, dy: Float): Float = (1.3f * exp(-4.6f * sqrt(dx * dx + dy * dy * 1.5f))).coerceIn(0f, 1f)

    /** La luz de guardia de la luminaria (los Sueños oscuros): baja de arriba a la izquierda y se apaga hacia abajo y a la derecha. */
    fun guardia(x: Float, y: Float): Float = (0.95f * exp(-1.7f * sqrt(x * x * 1.2f + y * y * 0.5f))).coerceIn(0.06f, 0.85f)

    /** La mancha de la luminaria de día (y de la vigilia): cuánto alumbra ([x], [y]) en anchos, desde arriba a la izquierda. */
    fun luminaria(x: Float, y: Float): Float {
        val dx = x - 0.06f; val dy = y + 0.04f
        return (0.95f * exp(-1.7f * sqrt(dx * dx + dy * dy * 0.7f))).coerceIn(0f, 1f)
    }

    /** La vela de hueco que más alumbra ([x], [y]) entre [velas] (x, y, x, y… en anchos). */
    private fun velaHuecoMayor(x: Float, y: Float, velas: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < velas.size) { m = maxOf(m, velaHueco(x - velas[i], y - velas[i + 1])); i += 2 }
        return m
    }

    /**
     * Cuánta luz de vela llega a ([x], [y]) (anchos): el candelero, el farol a la altura [yFarol] y las [velas] de los huecos. En
     * la guardia de los Sueños oscuros ([baja]), arden bajas.
     */
    fun calor(x: Float, y: Float, yFarol: Float, velas: FloatArray, baja: Boolean): Float {
        val f = if (baja) 0.5f else 1f
        val kv = vela(x - VELA_X, y - VELA_Y) * f
        val kf = farol(x - FAROL_X, y - yFarol) * f
        val kh = velaHuecoMayor(x, y, velas) * f
        return (1f - (1f - kv) * (1f - kf) * (1f - kh)).coerceIn(0.02f, 1f)
    }

    /**
     * Dónde va la vela de hueco de cada repisa (en x del lienzo), o -1 si no lleva: en las repisas de en medio con sitio libre,
     * nunca en dos seguidas, ni en la primera (que ya alumbra el candelero) ni en la última (el farol); hasta dos.
     */
    fun velasEn(d: DisposicionEstante, w: Float): FloatArray {
        val n = d.repisas.size
        val xs = FloatArray(n) { -1f }
        if (n < 3) return xs
        val medio = (n - 1) / 2f
        val orden = (1 until n - 1).sortedBy { kotlin.math.abs(it - medio) }
        var puestas = 0
        for (k in orden) {
            if (puestas >= (if (n >= 6) 2 else 1)) break
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.02f; val x1 = d.derecha.left - w * 0.03f
            if (x1 - x0 < w * 0.16f) continue
            if ((k > 0 && xs[k - 1] >= 0f) || (k + 1 < n && xs[k + 1] >= 0f)) continue
            xs[k] = x0 + w * 0.055f
            puestas++
        }
        return xs
    }

    /**
     * En qué repisa va el piloto rojo del pilar de la izquierda: en la más lejos de todas las luces (el candelero arriba, el farol
     * abajo y las velas de los huecos, [velas] por repisa, -1 si no lleva), para que su rojo rompa el tramo más hondo; a igual
     * distancia, la de más abajo. Sin [velas], la penúltima de un estante alto.
     */
    fun repisaPiloto(repisas: Int, velas: FloatArray? = null): Int {
        if (repisas < 3) return minOf(1, repisas - 1).coerceAtLeast(0)
        if (velas == null) return if (repisas >= 4) repisas - 2 else 1
        var mejor = 1; var lejos = -1
        for (k in 1 until repisas - 1) {
            var d = minOf(k, repisas - 1 - k)
            for (j in velas.indices) if (velas[j] >= 0f) d = minOf(d, kotlin.math.abs(k - j))
            if (d >= lejos) { lejos = d; mejor = k }
        }
        return mejor
    }

    /** Cuánto rojo del piloto llega a un punto a [dx] y [dy] de él, en anchos: corto. */
    fun piloto(dx: Float, dy: Float): Float = (1.1f * exp(-6.5f * sqrt(dx * dx + dy * dy * 1.3f))).coerceIn(0f, 1f)

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireOrden(oscuro, noche)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yF = (b.top + b.height * FAROL_Y) / w
        // Las velas de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(d.repisas.size) { -1f }
        val velL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        val xs = velasEn(d, w)
        d.repisas.forEachIndexed { k, r ->
            val x = xs[k]
            if (x < 0f) return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            velL.add(x / w); velL.add((r.bottom - r.height * VELA_HUECO_Y) / w)
        }
        val velas = if (a.lumbre) velL.toFloatArray() else FloatArray(0)
        LucesOrden.poner(LucesOrden.clave(d.repisas.size, oscuro, noche), centros, w / density, repisaPiloto(d.repisas.size, xs))
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 0f
            else if (p.y < apoyo) velaArriba(p.x / w - VELA_X, p.y / w - VELA_Y) * (if (a.dormida) 0.5f else 1f)
            else calor(p.x / w, p.y / w, yF, velas, a.dormida)
        }
        val frio: (Offset) -> Float = { p -> if (!a.dormida) 0f else guardia(p.x / w, p.y / w) }
        val clave = "${w.toInt()}:$ch:$oscuro:$noche:$density"

        // Lo de arriba: el muro, el bodegón y la cornisa, en una imagen que llega al píxel entero de debajo de la corona.
        with(CapaOrden) {
            pegar("c:$clave", 0, ceil(ch).toInt()) {
                drawRect(a.luz(PaletaOrden.PAVON, 0.1f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteOrden) { pared(Rect(0f, 0f, w, apoyo + 4f), a, cerca, frio) }
                with(CoronaEstanteOrden) { atras(w, apoyo, a, cerca, frio) }
                with(ObraEstanteOrden) { cornisa(Rect(0f, apoyo, w, ch + 2f), a, cerca, frio) }
                with(CoronaEstanteOrden) { delante(w, apoyo, a, cerca, frio) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, lo de los huecos, las tablas, los pilares y las placas.
        with(ObraEstanteOrden) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, a, cerca, frio, xs[k]) }
        }
        with(HuecosEstanteOrden) {
            // Cada hueco sabe cuántos grupos se pusieron antes, para no repetir lo que acompaña de una repisa a otra.
            var n = if (noche) 1 else 0
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                n += hueco(n, r, oc, d.derecha.left, xs[k], a, cerca, frio)
            }
        }
        with(ObraEstanteOrden) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, a, cerca, frio) }
            pilar(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, a, cerca, frio)
            pilar(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, a, cerca, frio)
            d.placas.forEach { p -> placa(p, medidor, a, cerca(p.r.center), frio(p.r.center)) }
        }
        // Lo de abajo, en vivo: el zócalo, el suelo y lo que hay en él, desde el píxel entero de encima.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 0f else calor(p.x / w, p.y / w, yF, FloatArray(0), a.dormida) }
        with(PieEstanteOrden) { alPie(Rect(0f, arriba, w, h), b.top, b.top + b.height * ZOCALO, a, cercaPie, frio) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireOrden(oscuro, v.noche)
        val anchoDp = LucesOrden.anchoDp(v.repisas, oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / anchoDp
        val velas = if (a.lumbre) LucesOrden.velas(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yF = CORONA + v.repisas * FILA / anchoDp + BASE * FAROL_Y
        val k = if (a.lumbre) calor(fx, fy, yF, velas, a.dormida) else 0f
        val kf = if (a.dormida) guardia(fx, fy) else 0f
        // De qué lado le llega la luz: de día y en la guardia, de la luminaria (izquierda); de noche, de la vela que más alumbra.
        val kv = vela(fx - VELA_X, fy - VELA_Y)
        val kp = farol(fx - FAROL_X, fy - yF)
        var kh = 0f; var xh = fx
        var i = 0
        while (i + 1 < velas.size) { val e = velaHueco(fx - velas[i], fy - velas[i + 1]); if (e > kh) { kh = e; xh = velas[i] }; i += 2 }
        val hacia = when {
            !a.oscuro || a.dormida -> -1f
            kh >= kv && kh >= kp -> if (xh >= fx) 1f else -1f
            kv >= kp -> if (fx < VELA_X) 1f else -1f
            else -> if (fx < FAROL_X) 1f else -1f
        }
        val sol = if (!a.oscuro) luminaria(fx, fy) else 0f
        val material = materialOrden(v.tono, oscuro)
        with(VolumenEstanteOrden) {
            // El rojo del piloto del pilar, si está cerca: su filo en el canto que lo mira (de noche; en los Sueños, más bajo).
            val pr = LucesOrden.piloto(v.repisas, oscuro, v.noche)
            val py = CORONA + (pr + 0.42f * ALTO_REPISA.value / FILA) * FILA / anchoDp
            rojo = if (a.oscuro) piloto(fx - LADO * 0.55f, fy - py) * (if (a.dormida) 0.6f else 1f) else 0f
            when (clase(v.semilla)) {
                0 -> {
                    // Alguno, recostado contra el vecino de la izquierda.
                    val r = azar(v.semilla, 22)
                    if (r > 0.78f) rotate(-3.5f, Offset(0f, size.height)) { manual(v, medidor, a, k, kf, hacia, sol, material) }
                    else manual(v, medidor, a, k, kf, hacia, sol, material)
                }
                1 -> carcaj(v, medidor, a, k, kf, hacia, sol, material)
                else -> {
                    // Blanda: casi siempre recostada, contra la vecina de un lado o del otro.
                    val r = azar(v.semilla, 23)
                    if (r > 0.6f) rotate(-5f, Offset(0f, size.height)) { carpeta(v, medidor, a, k, kf, hacia, sol, material) }
                    else if (r > 0.22f) rotate(4f, Offset(size.width, size.height)) { carpeta(v, medidor, a, k, kf, hacia, sol, material) }
                    else carpeta(v, medidor, a, k, kf, hacia, sol, material)
                }
            }
        }
    }
}

/**
 * Dónde arden las velas de los huecos y cuánto mide el estante, apuntado por el estante al dibujarse para que cada volumen (que
 * se dibuja después, en su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro de la
 * vela en la misma medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesOrden {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)
    @Volatile private var ancho = MuebleEstanteOrden.ANCHO_NOMINAL

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    @Volatile private var piloto = -1

    fun poner(clave: String, c: FloatArray, anchoDp: Float, repisaPiloto: Int) { centros = c; ancho = anchoDp.coerceAtLeast(100f); piloto = repisaPiloto; this.clave = clave }

    /** En qué repisa está el piloto rojo, si se apuntó para este estante; si no, la que diría el estante sin velas. */
    fun piloto(repisas: Int, oscuro: Boolean, noche: Boolean): Int =
        if (clave == clave(repisas, oscuro, noche) && piloto >= 0) piloto else MuebleEstanteOrden.repisaPiloto(repisas)

    /** El ancho del estante en dp, si se apuntó para este estante; si no, el de un teléfono. */
    fun anchoDp(repisas: Int, oscuro: Boolean, noche: Boolean): Float =
        if (clave == clave(repisas, oscuro, noche)) ancho else MuebleEstanteOrden.ANCHO_NOMINAL

    /** Las velas de los huecos de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun velas(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val m = MuebleEstanteOrden
        val an = ancho
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(m.LADO + c[k] * (1f - 2 * m.LADO))
            l.add(m.CORONA + k * m.FILA / an + (m.ALTO_REPISA.value * (1f - m.VELA_HUECO_Y)) / an)
        }
        return l.toFloatArray()
    }
}
