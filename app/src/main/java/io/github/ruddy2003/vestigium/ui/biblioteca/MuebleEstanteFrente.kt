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
 * Lo fijo de arriba del estante de Frente (la pared, la cúpula, la carta, el vidrio, la radio, las latas y la capota con su
 * friso), pintado una vez por tamaño y modo en una imagen guardada que luego se pega. Va de la fila 0 a la fila entera de debajo
 * de la corona, sobre un fondo opaco, así la junta con el cuerpo no deja asomar el fondo. Se guarda solo la última.
 */
internal object CapaFrente {
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
 * El dibujo del estante del diario de Frente exterior (lo usa `EstanteFrente`, en `BibliotecaFrente.kt`): el archivo de la
 * célula, en la misma sala de radar de la base escondida en las ruinas donde está el panel falso del Ideario, contra el mismo
 * muro de sillares con sus raíces y su tubo de cobre. Un estante abierto de chapa esmaltada color hueso, de la misma serie que la
 * consola: la capota curva con sus molduras redondas, el friso de consola oscura con sus pilotos, los costados gruesos de canto
 * redondo, las repisas de chapa doblada con su labio y su portaetiquetas, y el zócalo de consola oscura con sus rejillas. Por
 * detrás de cada casillero, la piedra en sombra. Arriba, la radio de maleta, el vidrio grabado, las latas de microfilm y la
 * cúpula colgada; al pie, el cargador de válvulas, la caja de equipo y el rollo de cable. De noche arden la cúpula, el aplique
 * rojo de un hueco y las válvulas del cargador, y el vidrio brilla en turquesa. La paleta y la luz están en [PaletaFrente] y [AireFrente]; la pared y la
 * piedra, en [MuroEstanteFrente]; el mueble, en [ObraEstanteFrente]; el bodegón, en [CoronaEstanteFrente]; lo de los huecos, en
 * [HuecosEstanteFrente]; el pie, en [PieEstanteFrente]; las entradas, en [VolumenEstanteFrente].
 */
internal object MuebleEstanteFrente {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteFrente` las lee al dibujar)

    val CORONA = 0.64f
    val BASE = 0.3f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el cartucho de datos; 1, el tubo de la carta estelar; 2, la valija de lona. */
    fun clase(semilla: Int): Int {
        var x = semilla * 0x27D4EB2F
        x = x xor (x ushr 15)
        val k = (x and 0x7fffffff) % 7
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Qué alto tiene cada una, en fracción del casillero: el tubo, el más alto; el cartucho, casi; la valija, la más baja. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.83f + 0.07f * azar(semilla, 1)
        1 -> 0.87f + 0.08f * azar(semilla, 1)
        else -> 0.8f + 0.08f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde está la boca de la cúpula colgada, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val CUPULA_X = 0.17f
    const val CUPULA_Y = 0.205f
    /** Dónde brilla el vidrio grabado (el centro del panel). */
    const val VIDRIO_X = 0.79f
    const val VIDRIO_Y = 0.32f
    /** Dónde brillan las válvulas del cargador del pie: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val CARGADOR_X = 0.13f
    const val CARGADOR_Y = 0.66f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 132f
    /** La capota y su friso, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CAPOTA = 0.16f
    /** La cara de arriba de la capota, en anchos: dónde pisan las cosas de delante (desde su canto de atrás). */
    const val PISA = 0.014f
    /** En la base: dónde acaba el zócalo y empieza el suelo, en fracción de su alto. */
    const val ZOCALO = 0.3f
    /** El foco de un hueco, colgado de la tabla de arriba: a qué alto de su repisa alumbra (en fracción del hueco, desde abajo). */
    const val FOCO_Y = 0.84f

    /** La luz de la cúpula en el cuerpo: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de su boca, en anchos. */
    fun cupula(dx: Float, dy: Float): Float =
        (1.55f * exp(-2.8f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.52f else 1f)))).coerceIn(0f, 1f)

    /** La luz de la cúpula en la pared y en lo de arriba: más corta que en el cuerpo, para que lo lejano se hunda. */
    fun cupulaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        if (suenos) 0.03f else (1.5f * exp(-3.3f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.03f, 1f)

    /** La luz de las válvulas del cargador del pie: naranja y baja, para las repisas de abajo; en los Sueños, más baja. */
    fun cargador(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.8f else 1.45f) * exp(-2.5f * sqrt(dx * dx * 1.2f + dy * dy * 1.3f))).coerceIn(0f, 1f)

    /** La luz del foco rojo de un hueco: corta, para sus vecinos, el fondo y el canto de la tabla de arriba. */
    fun foco(dx: Float, dy: Float): Float = (1.0f * exp(-4.3f * sqrt(dx * dx + dy * dy * (if (dy > 0f) 0.8f else 1.8f)))).coerceIn(0f, 1f)

    /** El resplandor frío del vidrio grabado: corto de noche; en los Sueños oscuros, la luz que manda. */
    fun vidrio(dx: Float, dy: Float, suenos: Boolean): Float =
        if (suenos) (1.25f * exp(-2.7f * sqrt(dx * dx * 0.9f + dy * dy * 0.8f))).coerceIn(0f, 1f)
        else (0.75f * exp(-4.2f * sqrt(dx * dx + dy * dy))).coerceIn(0f, 1f)

    /** El foco de hueco que más alumbra ([x], [y]) entre [focos] (x, y, x, y… en anchos). */
    private fun focoMayor(x: Float, y: Float, focos: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < focos.size) { m = maxOf(m, foco(x - focos[i], y - focos[i + 1])); i += 2 }
        return m
    }

    /**
     * Cuánta luz cálida llega a ([x], [y]) (anchos): la cúpula (si no está [apagada], como en los Sueños oscuros), el cargador a
     * la altura [yCargador] (más bajo en los [suenos]) y los [focos] de los huecos.
     */
    fun calor(x: Float, y: Float, yCargador: Float, suenos: Boolean, focos: FloatArray, apagada: Boolean = suenos): Float {
        val kc = if (apagada) 0f else cupula(x - CUPULA_X, y - CUPULA_Y)
        val ke = cargador(x - CARGADOR_X, y - yCargador, suenos)
        val kf = focoMayor(x, y, focos) * (if (suenos) 0.6f else 1f)
        return (1f - (1f - kc) * (1f - ke) * (1f - kf)).coerceIn(0.03f, 1f)
    }

    /** Dónde brilla el cargador, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yCargadorNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * CARGADOR_Y

    /**
     * Dónde va el foco del hueco de cada repisa (en x del lienzo), o -1 si no lleva. Alumbra en los huecos con sitio, nunca en
     * dos repisas seguidas ni en la primera (que ya alumbra la cúpula), para que no haya una reja de luces.
     */
    fun focosEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            if (k == 0) continue
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.02f; val x1 = d.derecha.left - w * 0.03f
            if (x1 - x0 < w * 0.2f || (k > 0 && xs[k - 1] >= 0f)) continue
            xs[k] = x0 + w * 0.07f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireFrente(oscuro, noche)
        val ac = AireFrente(oscuro, noche, cuerpo = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CAPOTA
        val yE = (b.top + b.height * CARGADOR_Y) / w
        val suenos = noche
        // Los focos de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(d.repisas.size) { -1f }
        val focL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        val xs = focosEn(d, w)
        d.repisas.forEachIndexed { k, r ->
            val x = xs[k]
            if (x < 0f) return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            focL.add(x / w); focL.add((r.bottom - r.height * FOCO_Y) / w)
        }
        val focos = if (a.lumbre) focL.toFloatArray() else FloatArray(0)
        LucesHuecoFrente.poner(LucesHuecoFrente.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < ch) cupulaArriba(p.x / w - CUPULA_X, p.y / w - CUPULA_Y, a.dormida)
            else calor(p.x / w, p.y / w, yE, suenos, focos, a.dormida)
        }
        val frio: (Offset) -> Float = { p -> if (!a.oscuro) 0f else vidrio(p.x / w - VIDRIO_X, p.y / w - VIDRIO_Y, a.dormida) }
        val clave = "${w.toInt()}:$ch:$oscuro:$noche:$density"

        // Lo de arriba: la pared, la cúpula, el bodegón y la capota con su friso, en una imagen que llega al píxel entero de
        // debajo de la corona.
        with(CapaFrente) {
            pegar("c:$clave", 0, ceil(ch).toInt()) {
                drawRect(ac.luz(PaletaFrente.CONSOLA_OSC, 0.2f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteFrente) { pared(Rect(0f, 0f, w, apoyo + 4f), a, cerca, frio) }
                with(CoronaEstanteFrente) { atras(w, apoyo, a, cerca, frio) }
                with(ObraEstanteFrente) { capota(Rect(0f, apoyo, w, ch + 2f), ac, cerca, frio) }
                // Lo de delante pisa la cara de arriba de la capota, un poco más adelante que su canto de atrás.
                with(CoronaEstanteFrente) { delante(w, apoyo + w * PISA, ch, a, cerca, frio) }
                // La raíz que cae por delante de la capota, por el costado de la izquierda (como las que caen por los costados de
                // la consola del Ideario), hasta el friso.
                with(MuroEstanteFrente) { raices(listOf(Triple(w * LADO * 0.3f, ch - apoyo + w * 0.06f, 2.8f * density)), apoyo - w * 0.06f, ac, cerca, 41) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, lo de los huecos, las tablas, los costados y las etiquetas.
        with(ObraEstanteFrente) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, ac, cerca, frio, xs[k]) }
        }
        with(HuecosEstanteFrente) {
            // Cada hueco sabe cuántos grupos se pusieron antes, para no repetir lo que acompaña de una repisa a otra.
            var n = if (noche) 2 else 0
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                n += hueco(n, r, oc, d.derecha.left, xs[k], ac, cerca, frio)
            }
        }
        with(ObraEstanteFrente) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, ac, cerca, frio) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, ac, cerca, frio)
            costado(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, ac, cerca, frio)
            d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center), frio(p.r.center)) }
        }
        // Lo de abajo, en vivo: el zócalo, el suelo y lo que hay en él, desde el píxel entero de encima y sobre un fondo
        // opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(ac.luz(PaletaFrente.CONSOLA_OSC, cerca(Offset(w / 2, b.top))), Offset(0f, arriba), Size(w, 3f))
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yE, suenos, FloatArray(0), a.dormida) }
        with(PieEstanteFrente) { alPie(Rect(0f, arriba, w, h), b.top, b.top + b.height * ZOCALO, ac, cercaPie) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireFrente(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val focos = if (a.lumbre) LucesHuecoFrente.focos(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yEn = yCargadorNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yEn, v.noche, focos, a.dormida) else 1f
        val kv = if (a.oscuro) vidrio(fx - VIDRIO_X, fy - VIDRIO_Y, a.dormida) else 0f
        // De qué lado le llega la luz: de día, de la cúpula (izquierda); de noche, de la luz que más alumbra.
        val kc = if (a.dormida) 0f else cupula(fx - CUPULA_X, fy - CUPULA_Y)
        val ke = cargador(fx - CARGADOR_X, fy - yEn, v.noche)
        var kf = 0f; var xf = fx
        var i = 0
        while (i + 1 < focos.size) { val e = foco(fx - focos[i], fy - focos[i + 1]); if (e > kf) { kf = e; xf = focos[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            a.dormida && kv > maxOf(ke, kf) -> if (fx < VIDRIO_X) 1f else -1f
            kf >= kc && kf >= ke -> if (xf >= fx) 1f else -1f
            kc >= ke -> if (fx < CUPULA_X) 1f else -1f
            else -> if (fx < CARGADOR_X) 1f else -1f
        }
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val material = materialFrente(v.tono, oscuro)
        with(VolumenEstanteFrente) {
            // El rojo del foco del hueco, si es lo que más le alumbra: su filo en el canto que lo mira.
            rojo = if (a.oscuro && kf > 0.12f && kf >= kc && kf >= ke) (kf * (if (v.noche) 0.6f else 1f)).coerceAtMost(1f) else 0f
            when (clase(v.semilla)) {
                0 -> cartucho(v, medidor, a, kd, kv, hacia, material)
                // Alguno, recostado contra el vecino de la izquierda.
                1 -> if (azar(v.semilla, 22) > 0.7f) rotate(-5f, Offset(0f, size.height)) { tubo(v, medidor, a, kd, kv, hacia, material) }
                    else tubo(v, medidor, a, kd, kv, hacia, material)
                else -> valija(v, medidor, a, kd, kv, hacia, material)
            }
        }
    }
}

/**
 * Dónde alumbran los focos de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en
 * su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro del foco en la misma medida
 * que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoFrente {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Los focos de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguno si no se apuntaron. */
    fun focos(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val m = MuebleEstanteFrente
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(m.LADO + c[k] * (1f - 2 * m.LADO))
            l.add(m.CORONA + k * m.FILA / m.ANCHO_NOMINAL + (m.ALTO_REPISA.value * (1f - m.FOCO_Y)) / m.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
