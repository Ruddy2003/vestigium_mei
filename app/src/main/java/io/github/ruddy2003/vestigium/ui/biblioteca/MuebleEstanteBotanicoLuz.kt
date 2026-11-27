package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * La paleta del estante de Botánico: el gabinete de trabajo del naturalista de vuelta del viaje, hacia 1835, el mismo cuarto
 * del Ideario (`IdeasBotanico`). La pared de verde salvia, el roble claro de la librería (el mismo del gabinete), el nogal de
 * la moldura y del piso, el latón, el papel, el cuero y la tinta; el vidrio de la caja de Ward y de los frascos, las hojas, el
 * barro de las macetas, el pino de la caja de embarque, la lana de la alfombra, las conchas y las piedras. Dos temperaturas:
 * lo cálido es el roble, el latón, el cuero y la lumbre; lo frío, la salvia, el vidrio, las hojas y la noche verde petróleo.
 */
internal object PaletaBotanico {
    /** El enlucido de debajo de la pintura (donde se desconchó) y el ladrillo que asoma. */
    val ENLUCIDO = Color(0xFFCDBB9A)
    val LADRILLO = Color(0xFFA0644A)
    /** La pared verde salvia (la del Ideario): su cara, su luz, su sombra y lo hondo de los casilleros. */
    val SALVIA = Color(0xFFA3B08F)
    val SALVIA_LUZ = Color(0xFFC4CCAA)
    val SALVIA_OSC = Color(0xFF7C8B6C)
    val SALVIA_HONDO = Color(0xFF56644C)
    /** El roble claro de la librería, su luz, su sombra y lo hondo; el nogal de la moldura, el friso y el piso. */
    val ROBLE = Color(0xFFB88C58)
    val ROBLE_LUZ = Color(0xFFD8B27C)
    val ROBLE_OSC = Color(0xFF7A5632)
    val ROBLE_HONDO = Color(0xFF4A3220)
    val NOGAL = Color(0xFF5A3E26)
    val NOGAL_OSC = Color(0xFF3A2716)
    /** El latón (portaetiquetas, quinqué, microscopio, clavos), su luz y su sombra. */
    val LATON = Color(0xFFC2A055)
    val LATON_LUZ = Color(0xFFF0DC98)
    val LATON_OSC = Color(0xFF7E6428)
    /** El papel, la cartulina de las etiquetas (de valor medio), el kraft, la tinta y el lacre. */
    val PAPEL = Color(0xFFF1E9D2)
    val SECANTE = Color(0xFFE4D8BC)
    val CARTULINA = Color(0xFFD6C397)
    val CARTULINA_OSC = Color(0xFFA88F62)
    val KRAFT = Color(0xFFAE8B58)
    val TINTA = Color(0xFF2A2318)
    val LACRE = Color(0xFF9A3A2A)
    /** El cuero de las correas y del fleco de las repisas. */
    val CUERO = Color(0xFF7A4A28)
    val CUERO_OSC = Color(0xFF4A2A14)
    /** El verde botella del vidrio y de la pantalla del quinqué, el vidrio claro y el resplandor de la pantalla encendida. */
    val VERDE = Color(0xFF34533A)
    val VIDRIO = Color(0xFFDDEBE6)
    val VIDRIO_OSC = Color(0xFF6F8E80)
    val VIDRIO_VERDE = Color(0xFF7CC99A)
    /** Las hojas: la fronda, la hoja clara, el musgo y el helecho hondo. */
    val HOJA = Color(0xFF5A7A3A)
    val HOJA_CLARA = Color(0xFF8BAA5C)
    val MUSGO = Color(0xFF6B8A3F)
    val HOJA_OSC = Color(0xFF34502A)
    /** El barro de las macetas, el pino de la caja de embarque, la paja y el bramante. */
    val TERRACOTA = Color(0xFFB0603A)
    val TERRACOTA_OSC = Color(0xFF7A3E24)
    val PINO = Color(0xFFC29C64)
    val PINO_OSC = Color(0xFF8E6E40)
    val PAJA = Color(0xFFD8BE76)
    val BRAMANTE = Color(0xFFC8AE7A)
    /** El lienzo crudo del salacot y la tela verde de los folios. */
    val CRUDO = Color(0xFFD9C9A0)
    val TELA = Color(0xFF4E6A3A)
    /** El nácar y las franjas del nautilo, el coral, la piedra, la pizarra y el cuarzo. */
    val NACAR = Color(0xFFEDE2CC)
    val FRANJA = Color(0xFF9A5A34)
    val CORAL = Color(0xFFC8644A)
    val PIEDRA = Color(0xFF8E887A)
    val PIZARRA = Color(0xFF4E5558)
    val CUARZO = Color(0xFFD9DCE6)
    val AMATISTA = Color(0xFF8A6A9E)
    /** La lana de la alfombra: la granza, el índigo, el ocre y el crudo. */
    val GRANZA = Color(0xFF8A4232)
    val INDIGO = Color(0xFF34425C)
    val OCRE = Color(0xFFC49A4A)
    /** La hojalata del farol y el hierro de los flejes. */
    val HOJALATA = Color(0xFFA2A7A2)
    val HIERRO = Color(0xFF3E403C)
    /** La noche del gabinete (verde petróleo, la del Ideario), la del cuerpo de la librería y la de los Sueños. */
    val NOCHE = Color(0xFF0B1C1D)
    val NOCHE_CUERPO = Color(0xFF0C1D1B)
    val NOCHE_SUENOS = Color(0xFF0A1A1C)
    /** La llama del quinqué (la del Ideario), su corazón, la luna y su roce frío. */
    val LLAMA = Color(0xFFFFC56E)
    val FUEGO = Color(0xFFFFF0CF)
    val LUNA = Color(0xFFDCE6F2)
    val LUNA_RAS = Color(0xFFA6C2DA)
    /** El ocaso de los Sueños claros: la luz melocotón y la sombra violeta. */
    val OCASO = Color(0xFFF4BE92)
    val SOMBRA_OCASO = Color(0xFF3E2C58)

    val LETRA: FontFamily = LetraExtra.fraunces
}

/**
 * El aire del momento en el estante de Botánico: de día (la luz de arriba a la izquierda, de fuera del cuadro, como en el
 * Ideario), de noche (el modo oscuro: arden el quinqué de pared, la palmatoria de un hueco y el farol del pie) y en los Sueños
 * ([noche]: el ocaso en claro, la luna en oscuro).
 */
internal class AireBotanico(val oscuro: Boolean, val noche: Boolean) {
    /** Si arden las luces (de noche siempre; en los Sueños claros, recién prendidas). */
    val lumbre: Boolean get() = oscuro || noche

    /**
     * El parche de luz que entra por una ventana de la izquierda que queda fuera del cuadro (el Ideario no la enseña), en la
     * pared y en lo que tiene delante: el del último sol en los Sueños claros, tendido y largo, y el de la luna en los oscuros,
     * más alto y más corto. Cuatro puntos en anchos del mueble (arriba izquierda, arriba derecha, abajo derecha, abajo
     * izquierda), o null si no hay.
     */
    val parche: FloatArray? get() = when {
        oscuro && noche -> LUNA_Q
        noche -> OCASO_Q
        else -> null
    }

    /** Cuánto le llega de la luz de día (o del ocaso, o de la luna) a un punto, en anchos del mueble. */
    fun luzDia(x: Float, y: Float): Float {
        val q = parche
        if (q != null) return dentro(q, x, y, BORDE)
        if (oscuro) return 0f
        val dx = x - 0.02f; val dy = y * 0.62f
        return exp(-1.35f * sqrt(dx * dx + dy * dy))
    }

    /** El tono de algo que está en ([x], [y]) (anchos), con la lumbre [k] que le llega; [arriba]: sobre la pantalla verde. */
    fun en(x: Float, y: Float, k: Float, cuerpo: Boolean = false, arriba: Boolean = false): TonoBotanico =
        TonoBotanico(this, k.coerceIn(0f, 1f), luzDia(x, y), cuerpo, arriba)

    companion object {
        /**
         * El último sol: entra bajo por la izquierda y cae en la pared de la corona, la cornisa y lo alto de la librería; su borde
         * de la derecha, tendido, se quiebra en la moldura, la cornisa y los objetos.
         */
        val OCASO_Q = floatArrayOf(-0.06f, 0.17f, 0.7f, 0.25f, 0.5f, 1.02f, -0.06f, 1.1f)
        /**
         * La luna: el rectángulo de la ventana, alto y estrecho, en la pared entre los ramilletes, el tejado de la caja de Ward y
         * la cornisa; a su izquierda y a su derecha, la noche.
         */
        val LUNA_Q = floatArrayOf(0.04f, 0.075f, 0.29f, 0.1f, 0.35f, 0.6f, 0.1f, 0.64f)
        /** El ancho del borde del parche (su penumbra), en anchos: un borde decidido, no un velo. */
        const val BORDE = 0.014f

        /** De 0 a 1, cuánto está ([x], [y]) dentro del cuadrilátero [q] (en el orden de las agujas del reloj), con su borde. */
        fun dentro(q: FloatArray, x: Float, y: Float, borde: Float): Float {
            var m = Float.MAX_VALUE
            for (i in 0 until 4) {
                val ax = q[2 * i]; val ay = q[2 * i + 1]
                val bx = q[(2 * i + 2) % 8]; val by = q[(2 * i + 3) % 8]
                val dx = bx - ax; val dy = by - ay
                val l = sqrt(dx * dx + dy * dy).coerceAtLeast(1e-5f)
                m = minOf(m, ((x - ax) * -dy + (y - ay) * dx) / l)
            }
            val f = (m / borde).coerceIn(0f, 1f)
            return f * f * (3f - 2f * f)
        }
    }
}

/**
 * El tono de un sitio: cómo se pinta lo que está ahí. De día, su color con la luz de arriba a la izquierda ([sol] la cuenta:
 * más claro y tibio cerca, más hondo lejos). De noche, de su hondo (oscuro, de su mismo tono, con el verde petróleo) a su
 * color según [k] (la lumbre que le llega), con la llama encima (o el verde de la pantalla, [arriba]). Al ocaso, más bajo y
 * con el melocotón donde toca [sol]; con la luna, la plata fría donde toca.
 */
internal class TonoBotanico(val a: AireBotanico, val k: Float, val sol: Float, val cuerpo: Boolean, val arriba: Boolean) {
    private val p = PaletaBotanico

    /** El aire en que se hunde lo que no se alumbra. */
    private val hondo: Color get() = when {
        a.oscuro && cuerpo -> p.NOCHE_CUERPO
        a.oscuro && a.noche -> p.NOCHE_SUENOS
        a.oscuro -> p.NOCHE
        a.noche -> p.SOMBRA_OCASO
        else -> SOMBRA_MUEBLE
    }

    /** El color propio hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hundido(c: Color): Color {
        val n = hondo
        val f = if (cuerpo) 0.18f else 0.13f
        return Color(c.red * f + n.red * 0.8f, c.green * f + n.green * 0.8f, c.blue * f + n.blue * 0.8f, c.alpha)
    }

    /** El color a la luz de la llama: el rojo sube y el azul baja. */
    private fun aLaLlama(c: Color): Color = Color((c.red * 1.08f).coerceAtMost(1f), c.green * 0.9f, c.blue * 0.66f, c.alpha)

    /** Al anochecer de los Sueños claros: cada cosa con su mismo color (la salvia sigue salvia), solo un poco más baja. */
    private fun alOcaso(c: Color): Color = Color(c.red * 0.9f, c.green * 0.9f, c.blue * 0.92f, c.alpha)

    /** El color de [c] con la luz del momento; [f] más claro (positivo) o más hondo (negativo), como lo pinta la luz de día. */
    fun c(c: Color, f: Float = 0f): Color {
        if (!a.oscuro) {
            val b = if (a.noche) alOcaso(c) else c
            // Lo hondo, de día, hacia la sombra violeta; al ocaso, hacia su mismo color más bajo (el violeta queda para las sombras).
            val d = if (f > 0f) mezcla(b, LUZ_MUEBLE, f)
                else if (f < 0f) (if (a.noche) mezcla(b, Color(b.red * 0.5f, b.green * 0.52f, b.blue * 0.6f, b.alpha), -f * 1.3f) else mezcla(b, hondo, -f))
                else b
            if (a.noche) {
                // El ocaso pinta solo donde cae: el mismo color virado a melocotón y un paso más claro (sin irse al naranja); el
                // violeta va en las sombras ([s]), no aquí. Las luces recién prendidas, un poco.
                val l = Color((d.red * 1.14f + 0.07f).coerceAtMost(1f), (d.green * 1.04f + 0.035f).coerceAtMost(1f), (d.blue * 0.96f + 0.02f).coerceAtMost(1f), c.alpha)
                return mezcla(mezcla(d, mezcla(l, p.OCASO, 0.34f), sol), p.LLAMA, 0.1f * k)
            }
            // De día: lo de arriba a la izquierda, más claro y tibio; lo lejano, un poco más hondo.
            val luz = (sol - 0.42f)
            return if (luz > 0f) mezcla(d, LUZ_MUEBLE, 0.34f * luz) else mezcla(d, SOMBRA_MUEBLE, -0.2f * luz)
        }
        val base = if (f > 0f) mezcla(c, LUZ_MUEBLE, f * 0.4f) else if (f < 0f) mezcla(c, hondo, -f * 0.6f) else c
        val e = if (cuerpo) 2.1 else 1.6
        val kk = (1.12f * Math.pow(k.toDouble(), e).toFloat()).coerceAtMost(0.93f)
        var v = mezcla(hundido(base), mezcla(base, aLaLlama(base), 0.6f), kk)
        v = mezcla(v, if (arriba) p.VIDRIO_VERDE else p.LLAMA, 0.13f * k)
        if (sol > 0f) {
            // La luna: el mismo color, un poco más claro y apenas frío, en lo que toca (la salvia sigue salvia; no vela lo demás).
            val q = 0.4f * sol * sol
            v = Color((v.red + (c.red * 0.96f + 0.02f) * q).coerceAtMost(1f), (v.green + (c.green * 0.95f + 0.02f) * q).coerceAtMost(1f),
                (v.blue + (c.blue * 0.88f + 0.03f) * q).coerceAtMost(1f), c.alpha)
        }
        return v
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun s(al: Float): Color =
        if (a.oscuro) hondo.copy(alpha = (al * 1.3f).coerceIn(0f, 1f)) else if (a.noche) hondo.copy(alpha = (al * 1.1f).coerceIn(0f, 1f)) else sombraMueble(al)

    /** El brillo de lo que mira a la luz: de día, crema; al ocaso, melocotón; de noche, el filo de la llama según [k]. */
    fun b(al: Float): Color = when {
        !a.oscuro && a.noche -> mezcla(LUZ_MUEBLE, p.OCASO, 0.55f).copy(alpha = (al * (0.2f + 0.75f * sol)).coerceIn(0f, 1f))
        !a.oscuro -> luzMueble(al * (0.75f + 0.5f * sol))
        sol > 0.05f && sol > k -> p.LUNA_RAS.copy(alpha = (al * 0.8f * sol).coerceIn(0f, 1f))
        arriba -> mezcla(p.VIDRIO_VERDE, LUZ_MUEBLE, 0.4f).copy(alpha = (al * 0.9f * k).coerceIn(0f, 1f))
        else -> p.LLAMA.copy(alpha = (al * 0.95f * k).coerceIn(0f, 1f))
    }

    /** El filo de la llama en lo que la mira, de noche; al ocaso, el del sol rasante; de día no hay. */
    fun filo(al: Float): Color = when {
        a.oscuro -> p.LLAMA.copy(alpha = (al * k).coerceIn(0f, 1f))
        a.noche -> p.OCASO.copy(alpha = (al * 0.6f * sol).coerceIn(0f, 1f))
        else -> Color.Transparent
    }

    /** El roce frío de la luna (solo en los Sueños oscuros). */
    fun lunar(al: Float): Color = if (a.oscuro && a.noche) p.LUNA_RAS.copy(alpha = (al * sol).coerceIn(0f, 1f)) else Color.Transparent

    /**
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (el óxido sigue óxido, pardo
     * lejos y encendido cerca); al ocaso, tibio; con la luna, plateado donde toca.
     */
    fun animo(t: Color): Color {
        if (!a.oscuro) return c(t)
        val apagado = mezcla(Color(t.red * 0.24f, t.green * 0.24f, t.blue * 0.22f, t.alpha), hondo, 0.3f)
        val encendido = mezcla(t, aLaLlama(t), 0.5f)
        var v = mezcla(apagado, encendido, (1.12f * Math.pow(k.toDouble(), 1.4).toFloat()).coerceAtMost(1f))
        if (sol > 0f) v = mezcla(v, mezcla(t, p.LUNA, 0.35f), 0.4f * sol)
        return v
    }

    /**
     * La cartulina o el papel donde va el día, teñido por la luz que le llega: ámbar cerca de la llama, pardo hondo lejos (nunca
     * más claro que lo alumbrado) y siempre con contraste para la tinta.
     */
    fun etiqueta(col: Color): Color {
        if (!a.oscuro) return c(col)
        val lejos = Color(col.red * 0.42f, col.green * 0.37f, col.blue * 0.27f, col.alpha)
        val alumbrado = Color((col.red * 1.04f).coerceAtMost(1f), col.green * 0.93f, col.blue * 0.72f, col.alpha)
        var v = mezcla(lejos, alumbrado, (1.1f * Math.pow(k.toDouble(), 1.8).toFloat()).coerceAtMost(1f))
        if (sol > 0f) v = mezcla(v, mezcla(col, p.LUNA, 0.3f).let { Color(it.red * 0.72f, it.green * 0.76f, it.blue * 0.82f, it.alpha) }, 0.45f * sol)
        return v
    }

    /** La tinta del día sobre [etiqueta]: siempre oscura y legible. */
    fun tinta(): Color = if (a.oscuro) Color(0xFF120C06) else c(p.TINTA)
}

// ---------------------------------------------------------------- ayudantes de dibujo

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoBotanico(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoBotanico(centro: Offset, ancho: Float, alto: Float, t: TonoBotanico, fuerza: Float = 0.36f, hacia: Float = 1f) =
    drawOval(t.s(fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo o tornillo de latón: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoBotanico(p: Offset, r: Float, t: TonoBotanico, metal: Color = PaletaBotanico.LATON) {
    drawCircle(t.s(0.4f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(t.c(metal), r, p)
    drawCircle(t.b(0.6f), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el latón o el vidrio (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloBotanico(c: Offset, r: Float, t: TonoBotanico) {
    if (!t.a.oscuro) { drawCircle(luzMueble(0.85f), r, c); return }
    if (t.k < 0.05f) return
    drawCircle(PaletaBotanico.LLAMA.copy(alpha = (0.4f * t.k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaBotanico.FUEGO.copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroBotanico(r: Rect, t: TonoBotanico, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(t.b(0.18f * fuerza), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(t.b(0.32f * fuerza), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(t.s(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(t.s(0.24f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Una fronda de helecho: un raquis curvo con pares de pinnas que se achican hacia la punta (el oficio del Ideario). */
internal fun DrawScope.frondaBotanico(base: Offset, largo: Float, giro: Float, color: Color, grosor: Float, pasos: Int = 11) {
    var px = base.x; var py = base.y
    var an = Math.toRadians(giro.toDouble())
    val n = pasos.coerceIn(3, 20)
    for (k in 0 until n) {
        val f = 1f - k / n.toFloat()
        val l = largo / n
        val qx = px + l * kotlin.math.cos(an).toFloat(); val qy = py + l * kotlin.math.sin(an).toFloat()
        drawLine(color, Offset(px, py), Offset(qx, qy), grosor * (0.4f + 0.6f * f))
        val hoja = largo * 0.17f * f
        for (da in doubleArrayOf(-1.1, 1.1)) {
            val b = an + da
            drawLine(color, Offset(qx, qy), Offset(qx + hoja * kotlin.math.cos(b).toFloat(), qy + hoja * kotlin.math.sin(b).toFloat()), grosor * 1.6f * f, StrokeCap.Round)
        }
        px = qx; py = qy; an += 0.06
    }
}

/** Una hoja: el óvalo con su lado en sombra y su nervio, girada [giro] grados alrededor de su centro. */
internal fun DrawScope.hojaBotanico(c: Offset, largo: Float, giro: Float, col: Color, t: TonoBotanico) {
    val u = density
    val a = largo * 0.24f
    val l = largo / 2
    val r = Math.toRadians(giro.toDouble())
    val cs = kotlin.math.cos(r).toFloat(); val sn = kotlin.math.sin(r).toFloat()
    fun p(x: Float, y: Float) = Offset(c.x + x * cs - y * sn, c.y + x * sn + y * cs)
    val forma = Path().apply {
        val a0 = p(0f, l); moveTo(a0.x, a0.y)
        val b1 = p(a * 1.3f, l * 0.45f); val b2 = p(a * 0.9f, -l * 0.55f); val b3 = p(0f, -l)
        cubicTo(b1.x, b1.y, b2.x, b2.y, b3.x, b3.y)
        val c1 = p(-a * 0.9f, -l * 0.55f); val c2 = p(-a * 1.3f, l * 0.45f)
        cubicTo(c1.x, c1.y, c2.x, c2.y, a0.x, a0.y)
        close()
    }
    drawPath(forma, col)
    val mitad = Path().apply {
        val a0 = p(0f, l); moveTo(a0.x, a0.y)
        val b1 = p(a * 1.3f, l * 0.45f); val b2 = p(a * 0.9f, -l * 0.55f); val b3 = p(0f, -l)
        cubicTo(b1.x, b1.y, b2.x, b2.y, b3.x, b3.y)
        close()
    }
    drawPath(mitad, t.s(0.2f))
    drawLine(t.b(0.4f), p(0f, -l * 0.85f), p(0f, l * 1.12f), 0.6f * u)
}

// ---------------------------------------------------------------- el ánimo

/** Las escalas del ánimo de Botánico, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_BOTANICO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.BOTANICO, false) to juegoTonos(TemaId.BOTANICO, true) }

private fun cuadradoBotanico(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaBotanico(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoBotanico(t.red - a0.red - dr * f) + cuadradoBotanico(t.green - a0.green - dg * f) + cuadradoBotanico(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los tintes del ánimo, de bajo a alto (la piel de los cuadernos, las semillas y el lienzo de las talegas): lo bajo, óxido y
 * granza; lo de en medio, lienzo crudo y paja (nunca blanco); lo alto, verde musgo. Siguen la escala del tema (`juegoTonos`:
 * óxido, papel y musgo).
 */
private val TINTES_BOTANICO = listOf(
    0f to Color(0xFF6A2E14), 0.2f to Color(0xFF8E4620), 0.36f to Color(0xFFAE6A34), 0.45f to Color(0xFFC9A066), 0.5f to Color(0xFFDCCBA2),
    0.56f to Color(0xFFC3C08A), 0.66f to Color(0xFF94A462), 0.78f to Color(0xFF67803E), 0.9f to Color(0xFF4B6630), 1f to Color(0xFF384F26),
)

/**
 * El tinte de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [TINTES_BOTANICO]; si
 * no está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores: aquí la entrada es un objeto del
 * cuarto, así que primero se vuelve al color de día (el mismo punto de la escala de noche en la de día) y después la luz
 * dibujada lo apaga con la distancia.
 */
internal fun tonoDeDiaBotanico(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_BOTANICO
    val x = (if (oscuro) enEscalaBotanico(t, noche) else null) ?: enEscalaBotanico(t, dia) ?: return t
    for (i in 0 until TINTES_BOTANICO.size - 1) {
        val (f0, c0) = TINTES_BOTANICO[i]; val (f1, c1) = TINTES_BOTANICO[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return TINTES_BOTANICO.last().second
}
