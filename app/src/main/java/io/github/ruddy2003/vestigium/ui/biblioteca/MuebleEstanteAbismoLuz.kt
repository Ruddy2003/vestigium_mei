package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La paleta del estante de Abismo: el pañol de buceo del barco esponjero, el mismo del Ideario (`IdeasAbismo`). El forro de tablas
 * pintadas de blanco roto con su salitre; la teca del armario y del bao; el latón de las cantoneras, las placas y el ojo de buey;
 * el cobre con su verdín; el hierro y el minio de la bomba; la manila de los cabos; la lona y el hule; el barro del anafe; el mar
 * del ojo de buey. Dos temperaturas: lo cálido es la teca, el latón, el cobre, la manila y las llamas; lo frío, el mar, la sombra
 * de la pared y la noche verde azulada del Ideario.
 */
internal object PaletaAbismo {
    /** El forro pintado de blanco roto (el mamparo del Ideario), su luz y su sombra. */
    val MAMPARO = Color(0xFFE2DBC6)
    val MAMPARO_LUZ = Color(0xFFF2EEE0)
    val MAMPARO_OSC = Color(0xFFB3AC97)
    /** La teca: su cara, su luz, su sombra y lo hondo de las juntas. */
    val TECA = Color(0xFF8E5C34)
    val TECA_LUZ = Color(0xFFB9834F)
    val TECA_OSC = Color(0xFF5A361E)
    val TECA_HONDA = Color(0xFF341E10)
    /** El cobre de la tubería y del cazo, su luz, su sombra y el verdín. */
    val COBRE = Color(0xFFB4663A)
    val COBRE_LUZ = Color(0xFFE39C66)
    val COBRE_OSC = Color(0xFF6E3820)
    val VERDIN = Color(0xFF5E9C84)
    /** El latón: su cara, su luz, su sombra y el de las placas (más claro, para que se lea el mes). */
    val LATON = Color(0xFFC9A04E)
    val LATON_LUZ = Color(0xFFF0D592)
    val LATON_OSC = Color(0xFF7E6128)
    val LATON_PLACA = Color(0xFFD8BE7E)
    /** El hierro de la bomba y el minio de sus volantes. */
    val HIERRO = Color(0xFF34302E)
    val HIERRO_LUZ = Color(0xFF66605A)
    val MINIO = Color(0xFFA23A26)
    val MINIO_LUZ = Color(0xFFD06A44)
    val MINIO_OSC = Color(0xFF5C1E14)
    /** El plomo, la hojalata y el óxido de los clavos. */
    val PLOMO = Color(0xFF6E7279)
    val HOJALATA = Color(0xFF9BA5A7)
    val OXIDO = Color(0xFF9A5530)
    /** El papel de las cartas, el de las etiquetas (de valor medio, tostado) y la tinta. */
    val PAPEL = Color(0xFFEDE3C8)
    val ETIQUETA = Color(0xFFCDB88A)
    val ETIQUETA_OSC = Color(0xFF9C8558)
    val TINTA = Color(0xFF1E2226)
    /** La manila de los cabos, el esparto y el corcho. */
    val MANILA = Color(0xFFC7A66A)
    val MANILA_OSC = Color(0xFF85663A)
    val ESPARTO = Color(0xFFC4A468)
    val CORCHO = Color(0xFFA9794C)
    /** Las esponjas, secas y recién cogidas (las del Ideario). */
    val ESPONJA = Color(0xFFC48C4A)
    val ESPONJA_OSC = Color(0xFF6A4A2C)
    /** La lona cruda, el hule amarillo del sombrero, el verde del cofre y el cuero oscuro de las bitácoras. */
    val LONA = Color(0xFFB09C76)
    val HULE = Color(0xFFD9A73A)
    val HULE_OSC = Color(0xFF8E6418)
    val VERDE_COFRE = Color(0xFF3E5C50)
    val VERDE_COFRE_LUZ = Color(0xFF5E8070)
    val CUERO = Color(0xFF4A2A1C)
    /** El barro del anafe y las brasas; la caracola. */
    val BARRO = Color(0xFFB0603E)
    val BARRO_OSC = Color(0xFF6A3220)
    val BRASA = Color(0xFFFF8E3C)
    val CONCHA = Color(0xFFE9B49A)
    val CONCHA_LUZ = Color(0xFFF8E2CF)
    val CONCHA_ROSA = Color(0xFFD9786A)
    /** La goma de las juntas, el cordón amarillo de las pizarras y el azul verdoso de las cartas. */
    val GOMA = Color(0xFF3B3330)
    val AMARILLO_CORDON = Color(0xFFE0B83A)
    val AZUL_CARTA = Color(0xFF4F7A86)
    val ROJO_TRAPO = Color(0xFFA8322A)
    /** El vidrio oscuro, el mimbre de la garrafa. */
    val VIDRIO = Color(0xFF2B3B40)
    val MIMBRE = Color(0xFFB98F52)
    /** El mar del ojo de buey: de día (el azul claro de Florida), de noche (con la luz viva) y al ocaso. */
    val CIELO = Color(0xFFE4F0EE)
    val CIELO_ALTO = Color(0xFFA8D2DC)
    val MAR = Color(0xFF2F95A8)
    val MAR_HONDO = Color(0xFF125068)
    val ESPUMA = Color(0xFFF2FAF6)
    val CIELO_NOCHE = Color(0xFF0C2230)
    val MAR_NOCHE = Color(0xFF04121A)
    val LUZ_VIVA = Color(0xFF6FF0D4)
    val CIELO_OCASO = Color(0xFFF4BE8C)
    val CIELO_OCASO_ALTO = Color(0xFF9C7AA6)
    val MAR_OCASO = Color(0xFF3A4E72)
    /** La sombra del ocaso de los Sueños claros (violeta), su luz y el sol de la mañana. */
    val SOMBRA_OCASO = Color(0xFF3E2A5E)
    val OCASO = Color(0xFFF2B286)
    val SOL = Color(0xFFFFF0C8)
    /** La llama del farol (la del Ideario), su corazón, la luna y su roce. */
    val LLAMA = Color(0xFFFFC56E)
    val FUEGO = Color(0xFFFFF0CF)
    val LUNA = Color(0xFFE6EEF8)
    val LUNA_RAS = Color(0xFFA2C2DA)
    /** La noche del pañol (la del Ideario: azul verdoso hondo, como el agua de abajo) y la de los Sueños. */
    val NOCHE = Color(0xFF061C21)
    val NOCHE_SUENOS = Color(0xFF071A28)

    val LETRA: FontFamily = LetraExtra.batiscafo
}

/**
 * El aire del momento en el estante de Abismo: de día (el sol que entra por el ojo de buey), de noche (el modo oscuro: arden el
 * farol, el farolillo de un hueco y las brasas del anafe) y en los Sueños ([noche]: el ocaso en claro, la luna en oscuro). De
 * noche es el mismo pañol que el Ideario a la misma hora y con su misma receta: el color propio hundido en su verde azulado hondo
 * (casi del todo lejos de la luz) y, cuanto más cerca de la llama, más su color, con el calor de la llama encima.
 */
internal class AireAbismo(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false) {
    private val p = PaletaAbismo
    /** Si arden las luces (de noche siempre; en los Sueños claros, recién encendidas). */
    val lumbre: Boolean get() = oscuro || noche
    /** La noche del pañol: la del Ideario (en los Sueños, la azul verdosa de la luna). */
    val nocheCasa: Color get() = if (noche) p.NOCHE_SUENOS else p.NOCHE
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color get() = when {
        oscuro -> nocheCasa
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color hundido del Ideario: oscuro, de su mismo tono (no gris), con el agua de la noche. */
    private fun hundido(c: Color) = Color(c.red * 0.1f + nocheCasa.red * 0.85f, c.green * 0.1f + nocheCasa.green * 0.85f,
        c.blue * 0.1f + nocheCasa.blue * 0.85f, c.alpha)

    /** El color al anochecer de los Sueños claros: el mismo cuarto ya en penumbra, apenas entibiado (el violeta va en la sombra). */
    private fun alOcaso(c: Color): Color =
        if (cuerpo) Color(c.red * 0.87f, c.green * 0.8f, c.blue * 0.75f, c.alpha) else Color(c.red * 0.9f, c.green * 0.83f, c.blue * 0.78f, c.alpha)

    /**
     * De noche, la receta del Ideario: del color hundido al propio según lo que llega de la llama ([cerca], de 0 a 1), teñido de
     * llama. En el cuerpo del armario la luz cae algo más deprisa, para que entre una llama y otra se hunda de verdad.
     */
    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        val f = if (cuerpo) Math.pow(k.toDouble(), 1.25).toFloat() else k
        val v = mezcla(hundido(c), c, (1.05f * f).coerceAtMost(0.92f))
        return mezcla(v, p.LLAMA, (if (noche) 0.16f else 0.22f) * f)
    }

    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (oscuro) return luz(if (f < 0f) mezcla(c, Color.Black, -f * 0.5f) else c, cerca)
        val b = if (noche) alOcaso(c) else c
        return if (f > 0f) mezcla(b, luzMueble(1f), f) else if (f < 0f) mezcla(b, hondo, -f) else b
    }

    /**
     * Lo que toca la mancha del ojo de buey ([f], de 0 a 1): [base] es el color sin esa luz. De día, el sol de la mañana; al
     * ocaso, el melocotón; con la luna, el mismo material virado al frío y un paso más claro. Es el color de la superficie bajo
     * esa luz, opaco: no un velo encima.
     */
    fun sol(c: Color, base: Color, f: Float): Color {
        val k = f.coerceIn(0f, 1f)
        return when {
            oscuro -> {
                val frio = Color((base.red * 1.15f + 0.03f).coerceAtMost(1f), (base.green * 1.32f + 0.06f).coerceAtMost(1f), (base.blue * 1.5f + 0.1f).coerceAtMost(1f), c.alpha)
                mezcla(base, frio, k)
            }
            noche -> {
                val l = Color((base.red * 1.22f + 0.06f).coerceAtMost(1f), (base.green * 1.1f + 0.05f).coerceAtMost(1f), (base.blue * 1.0f + 0.03f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFF6C29C), 0.3f), k)
            }
            else -> {
                val l = Color((base.red * 1.22f + 0.06f).coerceAtMost(1f), (base.green * 1.18f + 0.05f).coerceAtMost(1f), (base.blue * 1.08f + 0.03f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFFFEBB8), 0.2f), k)
            }
        }
    }

    /** El color de [c] con la luz del momento ([f] como en [dia]) y, si [lit] pasa de 0, con la mancha [m] encima. */
    fun en(c: Color, f: Float, cerca: Float, lit: Float, m: ManchaAbismo?): Color {
        val base = dia(c, f, cerca)
        return if (lit > 0f && m != null) sol(c, base, m.fuerza * lit) else base
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el verde azulado del pañol. */
    fun sombra(a: Float): Color =
        if (oscuro) nocheCasa.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La luz crema del día; al ocaso, la naranja; de noche, casi nada. */
    fun claro(a: Float): Color = if (oscuro) luzMueble(a * 0.06f) else if (noche) p.OCASO.copy(alpha = (a * 0.5f).coerceIn(0f, 1f)) else luzMueble(a)
    /** El brillo de lo que mira a la luz: de día crema; al ocaso, naranja; de noche, el filo de la llama según [cerca]. */
    fun brillo(a: Float, cerca: Float): Color =
        if (!oscuro) (if (noche) mezcla(luzMueble(1f), p.OCASO, 0.55f).copy(alpha = (a * 0.75f).coerceIn(0f, 1f)) else luzMueble(a))
        else p.LLAMA.copy(alpha = (a * 0.95f * cerca).coerceIn(0f, 1f))
    /** El filo de la llama en lo que la mira; al ocaso, el del sol rasante; de día no hay. */
    fun filo(a: Float): Color = if (oscuro) p.LLAMA.copy(alpha = a.coerceIn(0f, 1f)) else if (noche) p.OCASO.copy(alpha = (a * 0.6f).coerceIn(0f, 1f)) else Color.Transparent
    /** El roce frío de la luna (en los Sueños oscuros) o del mar del ojo de buey (de noche), en lo que mira a la izquierda. */
    fun lunar(a: Float): Color = if (oscuro) p.LUNA_RAS.copy(alpha = (if (noche) a * 1.3f else a * 0.7f).coerceIn(0f, 1f)) else Color.Transparent

    /**
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (se hunde en valor sin perder el
     * matiz, y cerca de la llama se enciende); al ocaso, tibio.
     */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(Color(t.red * 0.42f, t.green * 0.42f, t.blue * 0.42f, t.alpha), nocheCasa, 0.45f)
        val encendido = mezcla(t, Color((t.red * 1.12f).coerceAtMost(1f), t.green * 0.9f, t.blue * 0.66f, t.alpha), 0.45f)
        return mezcla(apagado, encendido, (1.1f * Math.pow(k.toDouble(), 1.2).toFloat()).coerceAtMost(1f))
    }

    /**
     * El papel de la etiqueta del día: ámbar cerca de la llama y, lejos, el papel en la sombra del pañol (más frío y apagado), sin
     * bajar del 4.5 a 1 con la tinta casi negra.
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        val lejos = if (noche) Color(0xFF7E8682) else Color(0xFF85857A)
        val alumbrado = Color((c.red * 1.05f).coerceAtMost(1f), c.green * 0.93f, c.blue * 0.7f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 1.4).toFloat().coerceAtMost(1f))
    }
}

/**
 * La mancha de luz que baja por la escotilla de arriba a la izquierda (la misma del Ideario, fuera del cuadro) y cae en la pared y
 * en lo que tiene delante: el hueco de la escotilla tendido por el sol, con la sombra de la brazola. [q] es el cuadrilátero de la
 * mancha en el plano de la pared, en anchos (arriba izquierda, arriba derecha, abajo derecha, abajo izquierda); lo que está más
 * adelante (la balda, la bomba) la recibe corrida [FRENTE]. [fuerza] es lo que aclara.
 */
internal class ManchaAbismo(val q: FloatArray, val color: Color, val fuerza: Float) {
    /** Un punto de la mancha: [fx] a lo ancho, [fy] a lo alto, de 0 a 1, en anchos, corrido [corrido]. */
    fun en(fx: Float, fy: Float, corrido: Float = 0f): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset(tx + (bx - tx) * fy + corrido, ty + (by - ty) * fy)
    }

    /** La mancha proyectada en un lienzo de ancho [w]; [crece] la agranda (la penumbra), en fracción de la mancha. */
    fun forma(w: Float, corrido: Float = 0f, crece: Float = 0f): Path {
        val a = en(-crece, -crece, corrido); val b = en(1f + crece, -crece, corrido)
        val c = en(1f + crece, 1f + crece, corrido); val d = en(-crece, 1f + crece, corrido)
        return Path().apply { moveTo(a.x * w, a.y * w); lineTo(b.x * w, b.y * w); lineTo(c.x * w, c.y * w); lineTo(d.x * w, d.y * w); close() }
    }

    /** Cuánto le toca a ([x], [y]) (anchos) del plano corrido [corrido]: 1 dentro, 0 fuera. */
    fun toca(x: Float, y: Float, corrido: Float = 0f): Float {
        val ax = q[0] + corrido; val ay = q[1]
        val ux = q[2] - q[0]; val uy = q[3] - q[1]
        val vx = q[6] - q[0]; val vy = q[7] - q[1]
        val det = ux * vy - uy * vx
        if (kotlin.math.abs(det) < 1e-6f) return 0f
        val px = x - ax; val py = y - ay
        val fx = (px * vy - py * vx) / det
        val fy = (ux * py - uy * px) / det
        return if (fx < 0f || fx > 1f || fy < 0f || fy > 1f) 0f else 1f
    }

    companion object {
        /** Cuánto se corre el plano del frente (la balda, lo de encima) respecto de la pared, en anchos (la luz baja desde la izquierda). */
        const val FRENTE = -0.03f
    }
}

/**
 * La mancha de la escotilla en este momento, o null (de noche manda el farol y la escotilla está cerrada). La de la mañana cae a la
 * derecha del ojo de buey, en la pared y en la bomba; al ocaso, más tendida y más baja; la luna de los Sueños, más angosta y tenue.
 */
internal fun manchaAbismo(a: AireAbismo): ManchaAbismo? = when {
    a.oscuro && a.noche -> ManchaAbismo(floatArrayOf(0.13f, 0.455f, 0.33f, 0.47f, 0.36f, 0.54f, 0.15f, 0.53f), PaletaAbismo.LUNA, 0.6f)
    a.oscuro -> null
    a.noche -> ManchaAbismo(floatArrayOf(0.34f, 0.2f, 0.7f, 0.25f, 0.78f, 0.5f, 0.42f, 0.46f), PaletaAbismo.OCASO, 0.75f)
    else -> ManchaAbismo(floatArrayOf(0.34f, 0.06f, 0.6f, 0.1f, 0.7f, 0.5f, 0.44f, 0.46f), PaletaAbismo.SOL, 0.85f)
}

/**
 * La luna de los Sueños oscuros en el cuerpo del armario (de 0 a 1): entra por el ojo de buey de arriba a la izquierda y roza lo
 * de arriba de ese lado; lejos, nada. [fx] y [fy], en anchos del mueble.
 */
internal fun lunaCuerpoAbismo(a: AireAbismo, fx: Float, fy: Float): Float {
    if (!(a.oscuro && a.noche)) return 0f
    val dx = fx - 0.15f; val dy = (fy - 0.6f) * 0.7f
    return (1.05f - kotlin.math.sqrt(dx * dx + dy * dy) / 0.85f).coerceIn(0f, 1f)
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoAbismo(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** El mismo camino corrido ([dx], [dy]). */
internal fun corridoAbismo(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoAbismo(centro: Offset, ancho: Float, alto: Float, a: AireAbismo, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un perno o un tornillo: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoAbismo(p: Offset, r: Float, a: AireAbismo, k: Float, metal: Color = PaletaAbismo.LATON) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.45f, r * 0.55f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.55f, k), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el latón, el cobre o el vidrio (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloAbismo(c: Offset, r: Float, a: AireAbismo, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaAbismo.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.32f)), r * 2.2f, c)
    drawCircle(PaletaAbismo.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (va dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroAbismo(r: Rect, a: AireAbismo, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.3f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.24f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Las escalas del ánimo de Abismo, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_ABISMO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.ABISMO, false) to juegoTonos(TemaId.ABISMO, true) }

private fun cuadradoAbismo(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaAbismo(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoAbismo(t.red - a0.red - dr * f) + cuadradoAbismo(t.green - a0.green - dg * f) + cuadradoAbismo(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los tintes del ánimo, de bajo a alto (la tela de la bitácora, la lona del petate, el hule de los rollos): lo bajo, el rojo de
 * coral y de minio; lo de en medio, el gris azulado de la lona de vela vieja (nunca blanco); lo alto, el azul del agua honda.
 * Siguen la escala del tema (`juegoTonos`: coral, espuma y azul).
 */
private val TINTES_ABISMO = listOf(
    0f to Color(0xFF6E2116), 0.18f to Color(0xFF953524), 0.32f to Color(0xFFBD5640), 0.43f to Color(0xFFC4876E), 0.5f to Color(0xFFADB9B4),
    0.57f to Color(0xFF8AABB4), 0.66f to Color(0xFF5B90A6), 0.78f to Color(0xFF2E7298), 0.9f to Color(0xFF0E5A88), 1f to Color(0xFF0C3E66),
)

/**
 * El tinte de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [TINTES_ABISMO]; si no
 * está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores (el ánimo de en medio pasa a casi
 * negro): aquí la entrada es un objeto del pañol, así que primero se vuelve al color de día (el mismo punto de la escala de noche
 * en la de día) y después la luz dibujada lo apaga con la distancia.
 */
internal fun tonoDeDiaAbismo(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_ABISMO
    val x = (if (oscuro) enEscalaAbismo(t, noche) else null) ?: enEscalaAbismo(t, dia) ?: return t
    for (i in 0 until TINTES_ABISMO.size - 1) {
        val (f0, c0) = TINTES_ABISMO[i]; val (f1, c1) = TINTES_ABISMO[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return TINTES_ABISMO.last().second
}
