package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/*
 * El dibujo del estante del diario de Espacio (lo usa `EstanteEspacio`, en `BibliotecaFuturo.kt`): el módulo de estiba del
 * taller del eje de la nave que va a Alfa Centauri, donde no hay peso y todo se sujeta. Está repartido en cinco archivos:
 * este (la paleta, las medidas, la luz y el módulo), `MuebleEstanteEspacioPiezas.kt` (la pared, el ojo de buey y la
 * lámpara), `MuebleEstanteEspacioArriba.kt` (lo que está sujeto arriba), `MuebleEstanteEspacioHuecos.kt` (los huecos y el
 * pie) y `MuebleEstanteEspacioVolumenes.kt` (las tres clases de entrada).
 */

/** La paleta del estante de Espacio: sale de la del cuadro del tema (`ColorEspacio`), con la noche y las luces propias. */
internal object PaletaEstanteEspacio {
    /** La pintura crema cálida del módulo; el aluminio cepillado que asoma donde saltó; el gris azulado de las cuadernas. */
    val CREMA = Color(0xFFE2D4B4)
    val CASCO = Color(0xFFCFCBC0)
    val ACERO = Color(0xFF9AA3B4)
    val CASCO_OSC = Color(0xFF7C8296)
    /** La chapa de los fondos de las repisas: oscura y quieta, para que lo guardado resalte. */
    val FONDO = Color(0xFF55565C)
    /** La tela gris clara del acolchado de la pared. */
    val ACOLCHADO = Color(0xFFBDBFC1)
    /** La goma y la espuma oscuras. */
    val GOMA = Color(0xFF2E3246)
    /** El naranja de lo que se agarra (el pasamanos, las correas, los cabos). */
    val NARANJA = Color(0xFFE8843C)
    /** La tela cruda (los estribos, el ribete del acolchado, las etiquetas de tela) y la tela azul de la nave. */
    val TELA = Color(0xFFE2D8C4)
    val TELA_AZUL = Color(0xFF4E6E9C)
    /** El rojo de lo que avisa, el trigo de la granja y su tallo. */
    val ROJO = Color(0xFFD24A3C)
    val TRIGO = Color(0xFFD8B45A)
    val VERDE = Color(0xFF5E9A52)
    /** El papel de las etiquetas y la tinta. */
    val PAPEL = Color(0xFFF2ECDC)
    val TINTA = Color(0xFF1E2333)
    /** El negro de afuera: la sombra del cuadro llevada casi a negro. */
    val FUERA = Color(0xFF07081A)

    // Las luces.
    /** La luz fría que entra por el ojo de buey: la de los focos del anillo, de día; la de las estrellas, de noche. */
    val LUZ_FRIA = Color(0xFFE6EEFF)
    val ESTRELLAS = Color(0xFF9FB8FF)
    /** La lámpara de trabajo, cálida: la que manda de noche. */
    val AMBAR = Color(0xFFFFB25E)
    /** El piloto azul de guardia: tenue de noche, el que manda en los Sueños. */
    val GUARDIA = Color(0xFF6A8CE8)
    /** La noche de la nave (el turno de descanso), la de los Sueños y su anochecer violeta, en claro. */
    val NOCHE = Color(0xFF0C1124)
    val NOCHE_SUENOS = Color(0xFF0A0F30)
    val ANOCHECER = Color(0xFF3A3570)
    /** La luz del anochecer de la nave que entra por el ojo de buey en los Sueños en claro: lila. */
    val CREPUSCULO = Color(0xFFB79AE0)
    /** Lo que multiplica cada color en los Sueños en claro: en la penumbra del anochecer y donde llega alguna luz. */
    val PENUMBRA_SUENOS = Color(0xFF6E679C)
    val LUZ_SUENOS = Color(0xFFF1EDFF)
}

/**
 * Cuánta luz le llega a un punto: [k] de la luz que manda (la lámpara, o el piloto de guardia en los Sueños) y [f] de la
 * luz fría (el ojo de buey y, de noche, el piloto de guardia, tenue).
 */
internal class LuzEstanteEspacio(val k: Float, val f: Float)

/**
 * El aire del momento en el módulo: de día (las luces del turno y la luz fría del ojo de buey), de noche (el modo oscuro: la
 * lámpara de trabajo encendida, la ventana con sus estrellas y el piloto de guardia tenue) y en los Sueños ([noche]: la
 * lámpara apagada, el piloto de guardia que manda y la Vía Láctea en el ojo de buey; en claro, el anochecer violeta).
 */
internal class AireEstanteEspacio(val oscuro: Boolean, val noche: Boolean) {
    private val P = PaletaEstanteEspacio
    val lumbre: Boolean get() = oscuro || noche
    val hondo: Color get() = if (oscuro) (if (noche) P.NOCHE_SUENOS else P.NOCHE) else SOMBRA_MUEBLE
    /** El color de la luz que manda: la lámpara de trabajo o, en los Sueños, la de guardia. */
    val foco: Color get() = if (noche) P.GUARDIA else P.AMBAR

    /** Un color con la luz que le llega. */
    fun luz(c: Color, l: LuzEstanteEspacio): Color {
        val k = l.k.coerceIn(0f, 1f); val f = l.f.coerceIn(0f, 1f)
        if (!oscuro) {
            if (!noche) return if (f > 0.02f) mezcla(c, P.LUZ_FRIA, 0.1f * f) else c
            // El anochecer de la nave: la luz violeta **multiplica** cada color (como en los cuadros), así el crema sigue crema,
            // la tela gris y las correas naranjas, solo que más bajos; donde da la luz lila del ojo de buey o el charco del
            // piloto, casi a su color de día, y lo demás en penumbra.
            val luz = mezcla(P.PENUMBRA_SUENOS, P.LUZ_SUENOS, (0.95f * k + 0.8f * f).coerceIn(0f, 1f))
            val v = Color(c.red * luz.red, c.green * luz.green, c.blue * luz.blue, c.alpha)
            return mezcla(mezcla(v, P.GUARDIA, 0.12f * k), P.CREPUSCULO, 0.1f * f)
        }
        if (noche) {
            val v = mezcla(c, hondo, (0.92f - 0.86f * k.pow(0.7f) - 0.25f * f).coerceIn(0.08f, 0.92f))
            return mezcla(mezcla(mezcla(v, P.GUARDIA, 0.26f * k), P.LUZ_FRIA, 0.2f * k), P.ESTRELLAS, 0.1f * f)
        }
        val v = mezcla(c, hondo, (0.9f - 0.84f * k.pow(0.75f) - 0.28f * f).coerceIn(0.07f, 0.9f))
        return mezcla(mezcla(v, P.AMBAR, 0.18f * k), P.ESTRELLAS, 0.1f * f)
    }

    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, P.ANOCHECER, 0.4f).copy(alpha = a.coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.4f else a)
    /** El filo de la luz que manda en lo que la mira (de día no hay). */
    fun filo(a: Float): Color = foco.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.6f).coerceIn(0f, 1f) else 0f)
    /** El filo frío del ojo de buey o del piloto en lo que lo mira: de día es la luz crema azulada; de noche, azul. */
    fun filoFrio(a: Float): Color = (if (lumbre) P.ESTRELLAS else P.LUZ_FRIA).copy(alpha = (if (oscuro) a * 0.8f else a).coerceIn(0f, 1f))

    /**
     * El color del ánimo: de noche se apaga mucho menos que lo demás (conserva su tono, solo baja), para que se adivine
     * también en la repisa más lejana; en los Sueños en claro, apenas se tiñe del anochecer.
     */
    fun animo(t: Color, l: LuzEstanteEspacio): Color {
        val k = maxOf(l.k, l.f * 0.8f).coerceIn(0f, 1f)
        if (!oscuro) {
            if (!noche) return t
            val luz = mezcla(P.PENUMBRA_SUENOS, P.LUZ_SUENOS, (0.35f + 0.65f * k).coerceIn(0f, 1f))
            return Color(t.red * luz.red, t.green * luz.green, t.blue * luz.blue, t.alpha)
        }
        val bajo = Color(t.red * (0.42f + 0.58f * k), t.green * (0.42f + 0.58f * k), t.blue * (0.42f + 0.58f * k), t.alpha)
        return mezcla(mezcla(bajo, hondo, 0.22f * (1f - k)), foco, 0.08f * l.k.coerceIn(0f, 1f))
    }
    /** El papel de las etiquetas: casi no se apaga, para que el día se lea siempre. */
    fun etiqueta(l: LuzEstanteEspacio): Color {
        val k = maxOf(l.k, l.f * 0.6f).coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(P.PAPEL, P.PENUMBRA_SUENOS, 0.3f - 0.26f * k) else P.PAPEL
        if (noche) return mezcla(mezcla(P.PAPEL, hondo, 0.4f - 0.3f * k), P.ESTRELLAS, 0.1f + 0.1f * k)
        return mezcla(mezcla(P.PAPEL, hondo, 0.42f - 0.32f * k), P.AMBAR, 0.12f * l.k.coerceIn(0f, 1f))
    }
}

// ---------------------------------------------------------------- trazos que usan los otros archivos

internal fun corridoEspacio(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeEspacio(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoEspacio(p, dx, dy), PathOperation.Difference) }

internal fun poligonoEspacio(vararg xy: Float) = Path().apply { poligonoEnEspacio(this, *xy) }

/** Rehace [p] (sin crear otro) como el polígono de los puntos [xy]; para los trazos que se repiten en cada volumen. */
internal fun poligonoEnEspacio(p: Path, vararg xy: Float): Path {
    p.reset()
    p.moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) p.lineTo(xy[2 * i], xy[2 * i + 1])
    p.close()
    return p
}

/** Un remache: la cabeza redonda con su luz arriba a la izquierda y su sombra corrida. */
internal fun DrawScope.remacheEspacio(p: Offset, r: Float, base: Color, a: AireEstanteEspacio) {
    drawCircle(a.sombra(0.35f), r * 1.05f, p + Offset(r * 0.4f, r * 0.45f))
    drawCircle(base, r, p)
    drawCircle(a.claro(0.55f), r * 0.42f, p - Offset(r * 0.3f, r * 0.3f))
}

/** La sombra de lo que pisa: un óvalo corrido a un lado. */
internal fun DrawScope.contactoEspacio(centro: Offset, ancho: Float, alto: Float, a: AireEstanteEspacio, fuerza: Float = 0.34f, lado: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * (0.5f - 0.08f * lado), centro.y - alto / 2), Size(ancho, alto))

/**
 * El módulo de estiba del taller del eje (MUEBLES.md, versión 2): un mueble de chapa remachada pintada de crema cálida, con
 * la pintura saltada en los cantos, los fondos de chapa oscura y lisa, las tablas de aluminio con su riel de anclajes y un
 * cabo elástico por repisa que sujeta lo guardado (sin peso, todo flota si no se ata). Contra el acolchado gris del casco,
 * con el ojo de buey a un costado: de día entra por él la luz fría de los focos que alumbran el anillo; de noche (el turno de
 * descanso) se ven el anillo y las estrellas y manda la lámpara de trabajo, cálida, sujeta a la derecha.
 */
internal object MuebleEstanteEspacio {
    private val P = PaletaEstanteEspacio

    val LETRA: FontFamily = LetraExtra.grotesk
    val LETRA_PLACA: FontFamily = LetraExtra.orbita

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.56f
    const val BASE = 0.2f
    const val LADO = 0.055f
    val ALTO_REPISA: Dp = 110.dp
    val TABLA: Dp = 16.dp
    val SEPARA: Dp = 2.dp

    fun ancho(palabras: Int): Dp = (28 + (palabras / 36).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, la bitácora de tela con su goma; 1, el cartucho de datos; 2, la bolsa sellada. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla * 31) xor (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** La bitácora, alta y delgada; el cartucho, bajo y ancho con su asa; la bolsa, blanda, en medio. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.88f + 0.06f * azar(semilla, 1)
        1 -> 0.68f + 0.05f * azar(semilla, 1)
        else -> 0.8f + 0.07f * azar(semilla, 1)
    }

    /** A qué altura del pie de la repisa pasa el cabo elástico, en dp (el mismo para el mueble y para cada volumen). */
    const val CABO = 21f

    // ---------------------------------------------------------------- la luz

    /** El centro del ojo de buey y el radio de su vidrio, en anchos del mueble. */
    const val VENT_X = 0.172f
    const val VENT_Y = 0.255f
    const val VENT_R = 0.098f
    /** Dónde está la boca de la lámpara de trabajo, en anchos del mueble. */
    const val LAMP_X = 0.842f
    const val LAMP_Y = 0.2f
    /** Hacia dónde mira la lámpara: abajo a la izquierda, a 122° (el eje y crece hacia abajo). */
    private val EJE_X = cos(Math.toRadians(122.0)).toFloat()
    private val EJE_Y = sin(Math.toRadians(122.0)).toFloat()
    /** El piloto azul de guardia: en el canto del costado izquierdo; su x, en anchos del mueble. */
    const val GUARDIA_X = LADO * 0.62f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble: el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 126f

    /** A qué altura va el piloto de guardia, en repisas desde la de arriba: a media altura del módulo. */
    fun guardiaPos(repisas: Int): Float = (repisas * 0.55f - 0.1f).coerceIn(0.45f, 2.6f)

    /**
     * La curva de la lámpara de trabajo, la misma para el mueble y para los volúmenes: cuánta luz llega (de 1 a 0) a un punto
     * que está a [dx] y [dy] de la boca, en anchos del mueble. Es un cono hacia abajo a la izquierda; fuera de él cae hasta un
     * tercio. Con la distancia se apaga al cuadrado: la primera repisa encendida, la segunda a media luz, lo de abajo perdido.
     */
    fun alcance(dx: Float, dy: Float): Float {
        val d = sqrt(dx * dx * 0.56f + dy * dy)
        val base = (1f - d / 1.45f).coerceIn(0f, 1f).let { it * it }
        val cosEje = (dx * EJE_X + dy * EJE_Y) / sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val t = ((cosEje - 0.1f) / 0.6f).coerceIn(0f, 1f)
        val cono = 0.33f + 0.67f * t * t * (3f - 2f * t)
        return (base * cono * 2.2f).coerceIn(0f, 1f)
    }

    /** La luz fría del ojo de buey: redonda y ancha, más fuerte junto al vidrio. */
    fun alcanceVentana(dx: Float, dy: Float): Float = (1f - sqrt(dx * dx * 0.8f + dy * dy) / 0.95f).coerceIn(0f, 1f).pow(1.4f)

    /** La luz del piloto de guardia: redonda, desde el costado izquierdo; se pierde hacia la derecha y lejos de su altura. */
    fun alcanceGuardia(dx: Float, dy: Float): Float = (1f - sqrt(dx * dx * 0.5f + dy * dy) / 0.74f).coerceIn(0f, 1f).pow(1.4f)

    /**
     * La luz que llega a un punto del cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la
     * segunda), [fx] es la x en anchos del mueble. De noche manda la lámpara y el piloto de guardia da un filo frío tenue; en
     * los Sueños manda el piloto.
     */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float, suenos: Boolean): LuzEstanteEspacio {
        val y = CORONA + pos * FILA / ANCHO_NOMINAL
        val v = alcanceVentana(fx - VENT_X, y - VENT_Y)
        val g = alcanceGuardia(fx - GUARDIA_X, (pos - guardiaPos(repisas)) * FILA / ANCHO_NOMINAL)
        return if (suenos) LuzEstanteEspacio(g, v * 0.85f)
        else LuzEstanteEspacio(alcance(fx - LAMP_X, y - LAMP_Y), maxOf(v * 0.55f, g * 0.5f))
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = AireEstanteEspacio(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val guardia = Offset(w * GUARDIA_X, ch + fila * guardiaPos(n))
        /** La luz en un punto del lienzo: arriba, por distancia a cada luz; en el cuerpo, por repisas, como los volúmenes. */
        fun luzEn(p: Offset): LuzEstanteEspacio {
            if (!a.lumbre) return LuzEstanteEspacio(1f, alcanceVentana((p.x / w) - VENT_X, (p.y / w) - VENT_Y))
            val v = alcanceVentana(p.x / w - VENT_X, p.y / w - VENT_Y)
            val g = alcanceGuardia((p.x - guardia.x) / w, (p.y - guardia.y) / w)
            return if (noche) LuzEstanteEspacio(g, v * 0.85f) else LuzEstanteEspacio(alcance(p.x / w - LAMP_X, p.y / w - LAMP_Y), maxOf(v * 0.55f, g * 0.5f))
        }
        val luzEnCuerpo: (Offset) -> LuzEstanteEspacio = { p -> if (p.y <= ch || p.y >= b.top || !a.lumbre) luzEn(p) else cercaCuerpo((p.y - ch) / fila, n, p.x / w, noche) }
        val lado = w * LADO
        val gT = w * 0.034f            // el canto de la tabla alta
        val fT = w * 0.014f            // lo que se ve de su cara de arriba
        val apoyo = ch - gT - fT * 0.45f
        val vent = Offset(w * VENT_X, w * VENT_Y)
        val lamp = Offset(w * LAMP_X, w * LAMP_Y)
        /** Hacia dónde cae la sombra de algo en [p] sobre la pared: de día, corta, a la derecha y abajo; de noche, lejos de la lámpara. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!a.lumbre || noche) return Offset(largo * 1.1f, largo * 0.8f)
            val v = p - lamp; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 2.4f)
        }

        with(PiezasEstanteEspacio) {
            // ---- la pared: el acolchado gris del casco entre sus cuadernas, arriba y al pie
            pared(a, ch, b, lado) { p -> luzEn(p) }
            // La luz que entra por el ojo de buey y el halo de la lámpara en la pared: unas pocas manchas, no un velo.
            luces(a, vent, lamp, ch)
            ventana(vent, w * VENT_R, a, caida(vent, w * 0.01f))
        }
        // ---- lo que está sujeto a la pared, detrás de lo de la tabla alta
        with(ArribaEstanteEspacio) {
            cintaTornillos(Offset(w * 0.43f, w * 0.115f), w * 0.12f, a, luzEn(Offset(w * 0.49f, w * 0.12f)), caida(Offset(w * 0.49f, w * 0.12f), w * 0.004f))
            trigo(Offset(w * 0.392f, w * 0.33f), w * 0.2f, a, luzEn(Offset(w * 0.39f, w * 0.24f)), caida(Offset(w * 0.39f, w * 0.24f), w * 0.006f))
            pasamanos(Rect(w * 0.79f, w * 0.335f, w * 1.03f, w * 0.356f), a, luzEn(Offset(w * 0.88f, w * 0.34f)), caida(Offset(w * 0.88f, w * 0.34f), w * 0.006f))
            auriculares(Offset(w * 0.885f, w * 0.345f), w * 0.1f, apoyo, a, luzEn(Offset(w * 0.87f, w * 0.42f)), caida(Offset(w * 0.87f, w * 0.42f), w * 0.008f))
        }
        with(PiezasEstanteEspacio) { lampara(lamp, a, caida(lamp, w * 0.008f)) }

        // ---- las sombras del módulo en la pared, al pie: bajo la última tabla, sobre el zócalo
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.24f), Offset(0f, b.top), Size(w, 5f * u))

        // ---- el fondo de cada repisa: la chapa oscura y lisa, con la sombra de la tabla de arriba
        d.repisas.forEachIndexed { k, r -> fondoRepisa(k, r, d.ocupado.getOrNull(k) ?: r.left, a) { p -> luzEnCuerpo(p) } }
        // El charco del piloto de guardia en los fondos de alrededor: fuerte en los Sueños, tenue de noche.
        if (a.lumbre && n > 0) clipRect(lado, ch, w - lado, b.top) {
            val f = if (noche) 1f else 0.4f
            mancha(guardia, w * 0.8f, P.GUARDIA.copy(alpha = 0.58f * f))
            mancha(guardia, w * 0.34f, P.LUZ_FRIA.copy(alpha = 0.5f * f))
        }

        // ---- los costados del módulo
        listOf(d.izquierda, d.derecha).forEachIndexed { i, r -> costado(Rect(r.left, ch - gT, r.right, b.top), i == 0, a, n, ch, fila, d.repisas) }

        // ---- las tablas con su riel y su placa, lo de los huecos libres y el cabo de cada repisa
        d.tablas.forEachIndexed { k, r -> tabla(Rect(lado - 1f * u, r.top, w - lado + 1f * u, r.bottom), 0f, k, a) { x -> luzEnCuerpo(Offset(x, r.center.y)) } }
        with(HuecosEstanteEspacio) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: r.left
                huecoLibre(k, r, oc, a, luzEnCuerpo(Offset((oc + r.right) / 2, r.center.y)))
            }
        }
        d.repisas.forEachIndexed { k, r -> cabo(r, d.ocupado.getOrNull(k) ?: r.left, a) { x -> luzEnCuerpo(Offset(x, r.bottom - CABO * u)) } }
        d.placas.forEach { p -> placa(p, medidor, a, luzEnCuerpo(p.r.center)) }

        // ---- la tabla alta, con su sombra en el primer fondo y, de día, la luz fría de la ventana en su cara de arriba
        tabla(Rect(-2f * u, ch - gT, w + 2f * u, ch), fT, 99, a) { x -> luzEn(Offset(x, ch - gT)) }
        if (!a.lumbre) clipRect(0f, ch - gT - fT, w, ch - gT) { mancha(Offset(w * 0.26f, ch - gT - fT * 0.5f), w * 0.3f, P.LUZ_FRIA.copy(alpha = 0.7f)) }
        drawRect(a.sombra(if (oscuro) 0.5f else 0.32f), Offset(lado, ch), Size(w - 2 * lado, 3.5f * u))

        // ---- lo que hay sobre la tabla alta, de atrás adelante: la rejilla al aire, el ventilador, el rotulador delante
        with(ArribaEstanteEspacio) {
            val r = w * 0.122f
            val cv = Offset(w * 0.485f, apoyo - r)
            rejilla(Offset(w * 0.665f, w * 0.2f), w * 0.085f, Offset(cv.x + r * 0.3f, cv.y - r * 0.9f), a, luzEn(Offset(w * 0.665f, w * 0.2f)), caida(Offset(w * 0.665f, w * 0.2f), w * 0.006f))
            ventilador(cv, r, a, { p -> luzEn(p) }, caida(cv, w * 0.01f))
            rotulador(Offset(w * 0.33f, apoyo), Offset(w * 0.335f, w * 0.43f), a, luzEn(Offset(w * 0.34f, w * 0.43f)), caida(Offset(w * 0.34f, w * 0.43f), w * 0.008f))
        }
        // ---- al pie: el zócalo con su rejilla y su cinta al aire, el riel de los estribos
        with(HuecosEstanteEspacio) { pie(b, a) { p -> luzEn(p) } }
        // ---- lo que alumbra por sí mismo, encima de todo
        if (a.lumbre) with(PiezasEstanteEspacio) { brillos(lamp, a) }
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- el módulo

    /**
     * El fondo de una repisa: la chapa oscura y lisa del módulo, quieta (para que lo guardado resalte), con dos o tres costuras
     * verticales remachadas, un desgaste y la sombra honda de la tabla de arriba; de día, la luz fría que entra por la
     * izquierda. De noche se pinta por tramos, cada uno con la luz que le llega.
     */
    private fun DrawScope.fondoRepisa(k: Int, r: Rect, oc: Float, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio) {
        val u = density; val w = size.width
        val tramos = if (a.lumbre) 18 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            drawRect(a.luz(P.FONDO, luz(Offset((xa + xb) / 2, r.center.y))), Offset(xa, r.top), Size(xb - xa, r.height))
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            val f = if (a.oscuro) 1.2f else 1f
            // De día, la luz fría del ojo de buey entra por la izquierda y se pierde hacia la derecha y hacia abajo.
            if (!a.lumbre) mancha(Offset(r.left, r.top + r.height * 0.3f), w * (0.7f - 0.1f * k.coerceAtMost(3)), P.LUZ_FRIA.copy(alpha = 0.22f - 0.05f * k.coerceAtMost(3)))
            // Dos o tres costuras verticales con sus remaches, cada repisa en otro sitio.
            val juntas = 2 + (k % 2)
            for (j in 0 until juntas) {
                val x = r.left + r.width * ((j + 0.55f + 0.3f * azar(k + 300, j)) / (juntas + 0.6f))
                val l = luz(Offset(x, r.center.y))
                drawLine(a.sombra(0.4f), Offset(x, r.top), Offset(x, r.bottom), 1.2f * u)
                drawLine(a.claro(0.14f), Offset(x + 1.2f * u, r.top), Offset(x + 1.2f * u, r.bottom), 0.7f * u)
                for (m in 0 until 2) remacheEspacio(Offset(x - 4f * u, r.top + r.height * (0.38f + 0.3f * m)), 1.1f * u, a.luz(mezcla(P.FONDO, P.ACERO, 0.45f), l), a)
            }
            // El desgaste: donde se meten y se sacan las cosas, la pintura más clara; y una mancha de grasa.
            pinceladas(Rect(r.left, r.top + r.height * 0.55f, r.right, r.bottom), a.claro(1f), 951 + k, 10, u, 0.05f)
            pinceladas(Rect(r.left + r.width * 0.5f, r.top + r.height * 0.3f, r.right, r.bottom), a.sombra(1f), 955 + k, 8, u, 0.07f)
            // La sombra de la tabla de arriba: honda junto a ella y deshecha hacia abajo.
            drawRect(a.sombra(0.34f * f), Offset(r.left, r.top), Size(r.width, 7f * u))
            drawRect(a.sombra(0.18f * f), Offset(r.left, r.top), Size(r.width, 17f * u))
            // Los costados, más hondos.
            drawRect(a.sombra(0.2f), Offset(r.right - 8f * u, r.top), Size(8f * u, r.height))
            drawRect(a.sombra(0.16f), Offset(r.left, r.top), Size(5f * u, r.height))
            // De noche, el cono de la lámpara entra en el fondo de la primera repisa.
            if (a.oscuro && !a.noche && k == 0) {
                val p = Offset(w * 0.62f, r.center.y)
                val kc = luz(p).k
                if (kc > 0.1f) mancha(p, w * 0.55f, P.AMBAR.copy(alpha = 0.26f * kc))
            }
            // La sombra que el último volumen deja en el fondo, a su derecha.
            if (oc > r.left + 6f * u) {
                val largo = (if (a.lumbre) 16f else 11f) * u
                drawPath(poligonoEspacio(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.18f, oc + largo, r.top + r.height * 0.26f, oc + largo, r.bottom), a.sombra(if (a.oscuro) 0.4f else 0.3f))
            }
        }
    }

    /**
     * Un costado del módulo: la chapa crema doblada en U, con su columna de remaches, la pintura saltada en el canto, el
     * lomo de luz del lado de la ventana, en cada repisa la argolla del cabo elástico y, en el izquierdo, el piloto de guardia.
     */
    private fun DrawScope.costado(p: Rect, izq: Boolean, a: AireEstanteEspacio, repisas: Int, ch: Float, fila: Float, huecos: List<Rect>) {
        val u = density; val w = size.width
        val fx = p.center.x / w
        val tramos = if (a.lumbre) (p.height / maxOf(1f, fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        for (t in 0 until tramos) {
            val ya = p.top + p.height * t / tramos; val yb = p.top + p.height * (t + 1) / tramos + 0.6f
            val l = if (a.lumbre) cercaCuerpo(((ya + yb) / 2 - ch) / fila, repisas, fx, a.noche) else LuzEstanteEspacio(1f, 0f)
            drawRect(a.luz(P.CREMA, l), Offset(p.left, ya), Size(p.width, yb - ya))
        }
        // El doblez de la chapa: el canto de dentro es una cara que mira hacia el hueco.
        val canto = p.width * 0.22f
        val xc = if (izq) p.right - canto else p.left
        drawRect(a.sombra(if (izq) 0.24f else 0.08f), Offset(xc, p.top), Size(canto, p.height))
        drawRect(if (izq) a.sombra(0.12f) else a.claro(0.3f), Offset(if (izq) p.left else p.right - 2f * u, p.top), Size(2f * u, p.height))
        drawRect(a.claro(if (izq) 0.4f else 0.2f), Offset(if (izq) p.left + 2.5f * u else p.left + canto + 1f * u, p.top), Size(1.2f * u, p.height))
        pinceladas(p, a.sombra(1f), if (izq) 971 else 972, (p.height / (24f * u)).toInt().coerceIn(6, 200), u, 0.07f)
        // La columna de remaches y la pintura saltada que deja ver el aluminio.
        val xr = if (izq) p.left + p.width * 0.36f else p.right - p.width * 0.36f
        val paso = maxOf(1f, 14f * u)
        var y = p.top + 8f * u
        var j = 0
        while (y < p.bottom - 6f * u) {
            val l = if (a.lumbre) cercaCuerpo((y - ch) / fila, repisas, fx, a.noche) else LuzEstanteEspacio(1f, 0f)
            remacheEspacio(Offset(xr, y), 1.3f * u, a.luz(mezcla(P.CREMA, P.ACERO, 0.35f), l), a)
            if (azar(if (izq) 61 else 62, j) < 0.1f) {
                val sx = if (izq) p.right - canto - 4f * u else p.left + canto
                drawOval(a.luz(P.ACERO, l), Offset(sx, y + 3f * u), Size(4f * u, (3f + 5f * azar(63, j)) * u))
                drawOval(a.luz(mezcla(P.ACERO, P.CREMA, 0.5f), l), Offset(sx + 0.8f * u, y + 3.6f * u), Size(1.4f * u, 2f * u))
            }
            y += paso; j++
        }
        // La argolla del cabo de cada repisa, atornillada al canto de dentro.
        huecos.forEachIndexed { k, r ->
            val yc = r.bottom - CABO * u
            val l = if (a.lumbre) cercaCuerpo(k + 0.8f, repisas, fx, a.noche) else LuzEstanteEspacio(1f, 0f)
            val xa = if (izq) p.right - canto * 0.5f else p.left + canto * 0.5f
            drawRoundRect(a.luz(P.ACERO, l), Offset(xa - 2.4f * u, yc - 4f * u), Size(4.8f * u, 8f * u), CornerRadius(1.5f * u))
            drawCircle(a.luz(mezcla(P.ACERO, P.TINTA, 0.5f), l), 1.4f * u, Offset(xa, yc))
            drawLine(a.claro(0.5f), Offset(xa - 2f * u, yc - 3.4f * u), Offset(xa + 1f * u, yc - 3.4f * u), 0.6f * u)
        }
        // El piloto de guardia, en el canto del costado izquierdo: apagado de día, tenue de noche, encendido en los Sueños.
        if (izq && repisas > 0) {
            val pos = guardiaPos(repisas)
            val g = Offset(w * GUARDIA_X, ch + fila * pos)
            val l = if (a.lumbre) cercaCuerpo(pos, repisas, GUARDIA_X, a.noche) else LuzEstanteEspacio(1f, 0f)
            drawRoundRect(a.sombra(0.4f), Offset(g.x - 4.4f * u, g.y - 8f * u), Size(10f * u, 17f * u), CornerRadius(3f * u))
            drawRoundRect(a.luz(P.GOMA, LuzEstanteEspacio(0.5f, 0f)), Offset(g.x - 5f * u, g.y - 9f * u), Size(10f * u, 18f * u), CornerRadius(3f * u))
            if (a.lumbre) {
                val f = if (a.noche) 1f else 0.55f
                drawCircle(P.GUARDIA.copy(alpha = 0.3f * f), 10f * u, g)
                drawCircle(P.GUARDIA.copy(alpha = 0.5f * f), 6f * u, g)
                drawCircle(mezcla(P.GUARDIA, Color.White, 0.3f + 0.35f * f), 3.4f * u, g)
            } else {
                drawCircle(a.luz(mezcla(P.GUARDIA, P.TINTA, 0.45f), l), 3.4f * u, g)
                drawCircle(a.claro(0.5f), 1.1f * u, g - Offset(1.2f * u, 1.2f * u))
            }
        }
        // El filo de la luz que manda, de noche, en el canto que la mira.
        if (a.lumbre) {
            val l = cercaCuerpo(0.4f, repisas, fx, a.noche)
            val luzX = if (a.noche) GUARDIA_X else LAMP_X
            val x = if (luzX > fx) p.right - 1.2f * u else p.left
            drawRect(a.filo(0.5f * l.k), Offset(x, p.top), Size(1.2f * u, fila * 1.3f))
            if (izq) drawRect(a.filoFrio(0.5f * l.f), Offset(p.left + 2.5f * u, p.top), Size(1f * u, fila * 1.2f))
        }
    }

    /**
     * Una tabla de aluminio: su cara de arriba vista apenas ([fondo]), el canto con el riel de anclajes (la ranura con sus
     * agujeros redondos) arriba y el labio cepillado abajo, con sus rayas de uso. De noche se pinta por tramos.
     */
    private fun DrawScope.tabla(cara: Rect, fondo: Float, semilla: Int, a: AireEstanteEspacio, luz: (Float) -> LuzEstanteEspacio) {
        val u = density
        val tramos = if (a.lumbre) 28 else 1
        val riel = cara.top + cara.height * 0.44f
        for (t in 0 until tramos) {
            val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
            val l = luz((xa + xb) / 2)
            if (fondo > 0f) drawRect(a.luz(mezcla(P.CASCO, P.ACERO, 0.3f), l), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(P.ACERO, l), Offset(xa, cara.top), Size(xb - xa, riel - cara.top))
            drawRect(a.luz(mezcla(P.ACERO, P.CASCO, 0.35f), l), Offset(xa, riel), Size(xb - xa, cara.bottom - riel))
            if (a.lumbre) drawRect(a.filo(0.06f + 0.5f * l.k), Offset(xa, cara.top - fondo), Size(xb - xa, 1f * u))
        }
        clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
            // La ranura del riel y sus agujeros redondos, cada 12 dp.
            val ry = cara.top + (riel - cara.top) * 0.5f
            drawRect(a.sombra(0.55f), Offset(cara.left, ry - 0.9f * u), Size(cara.width, 1.8f * u))
            val paso = maxOf(1f, 12f * u)
            var x = cara.left + 6f * u
            while (x < cara.right) {
                drawCircle(a.claro(0.45f), 2.1f * u, Offset(x + 0.5f * u, ry + 0.6f * u))
                drawCircle(a.sombra(0.6f), 2f * u, Offset(x, ry))
                x += paso
            }
            // Las aristas del perfil: luz arriba, la sombra bajo el riel, el labio redondeado abajo.
            drawRect(a.claro(0.45f), Offset(cara.left, cara.top), Size(cara.width, 1f * u))
            drawRect(a.sombra(0.22f), Offset(cara.left, riel), Size(cara.width, 1f * u))
            drawRect(a.claro(0.25f), Offset(cara.left, riel + 1.2f * u), Size(cara.width, 1f * u))
            // El cepillado del aluminio y las rayas de uso.
            for (j in 0 until 7) {
                val y = riel + (cara.bottom - riel) * (0.25f + 0.1f * j)
                drawLine(a.claro(0.08f + 0.06f * azar(semilla + 200, j)), Offset(cara.left, y), Offset(cara.right, y), 0.5f * u)
            }
            for (j in 0 until 5) {
                val x0 = cara.left + cara.width * azar(semilla + 210, j)
                drawLine(a.sombra(0.16f), Offset(x0, riel + 2f * u + 4f * u * azar(semilla + 211, j)), Offset(x0 + cara.width * 0.05f, riel + 3f * u + 4f * u * azar(semilla + 212, j)), 0.6f * u)
            }
        }
        drawRect(a.sombra(0.42f), Offset(cara.left, cara.bottom - 1.4f * u), Size(cara.width, 1.4f * u))
    }

    /** La placa del mes: una chapita de aluminio claro remachada al canto, con el mes grabado en tinta oscura. */
    private fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireEstanteEspacio, l: LuzEstanteEspacio) {
        val u = density
        val r = Rect(p.r.left, p.r.top - 0.6f * u, p.r.right, p.r.bottom + 0.6f * u)
        val lp = LuzEstanteEspacio(maxOf(l.k, 0.14f), l.f)
        drawRoundRect(a.sombra(0.4f), r.topLeft + Offset(1.2f * u, 1.4f * u), r.size, CornerRadius(1.5f * u))
        drawRoundRect(if (a.oscuro) mezcla(a.etiqueta(lp), a.hondo, 0.12f) else mezcla(P.PAPEL, P.ACERO, 0.22f), r.topLeft, r.size, CornerRadius(1.5f * u))
        drawRect(a.claro(0.5f), Offset(r.left + 2f * u, r.top + 0.6f * u), Size(r.width - 4f * u, 0.8f * u))
        drawRect(a.sombra(0.14f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
        val m = minOf(9f * u, r.width * 0.14f)
        listOf(r.left + m * 0.45f, r.right - m * 0.45f).forEach { x -> remacheEspacio(Offset(x, r.center.y), 1.3f * u, a.luz(P.ACERO, lp), a) }
        // Si no cabe ni con la letra más chica, se abrevia: solo el mes, lo de antes del « · ». Nunca se corta con «…».
        val caja = Rect(r.left + m, r.top + 1f * u, r.right - m, r.bottom - 1f * u)
        var texto = p.texto.uppercase()
        val estilo = TextStyle(fontFamily = LETRA_PLACA, fontSize = 5.5f.sp, letterSpacing = 1f.sp, fontWeight = FontWeight.Medium)
        if (texto.contains(" · ") && medidor.measure(texto, estilo, maxLines = 1, softWrap = false).size.width > caja.width) texto = texto.substringBefore(" · ")
        textoEn(medidor, texto, caja, LETRA_PLACA, P.TINTA, 9f, peso = FontWeight.Medium, espaciado = 1f, minimo = 4.5f)
    }

    /**
     * El cabo elástico de una repisa, de argolla a argolla, a [CABO] dp del pie: trenzado crudo con su hilo oscuro. Pasa por
     * delante de lo de los huecos y por detrás de los volúmenes, que dibujan su propio tramo (así se ve continuo); por eso el
     * trenzado solo se pinta donde no hay volúmenes, a la derecha de [oc].
     */
    private fun DrawScope.cabo(r: Rect, oc: Float, a: AireEstanteEspacio, luz: (Float) -> LuzEstanteEspacio) {
        val u = density
        val y = r.bottom - CABO * u
        val tramos = if (a.lumbre) 16 else 1
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.25f), Offset(r.left, y + 1.6f * u), Size(r.width, 2f * u))
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            caboTramoEspacio(xa, xb, y, a, luz((xa + xb) / 2), trenzaDesde = oc)
        }
    }
}

/** Un tramo del cabo elástico de [xa] a [xb] a la altura [y]: el trenzado crudo, su luz arriba, su sombra y el hilo oscuro. */
internal fun DrawScope.caboTramoEspacio(xa: Float, xb: Float, y: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, trenzaDesde: Float = xa) {
    val u = density
    val P = PaletaEstanteEspacio
    val g = 2.8f * u
    drawRect(a.luz(mezcla(P.TELA, P.CASCO_OSC, 0.15f), l), Offset(xa, y - g / 2), Size(xb - xa, g))
    drawRect(a.claro(0.45f), Offset(xa, y - g / 2), Size(xb - xa, 0.7f * u))
    drawRect(a.sombra(0.3f), Offset(xa, y + g / 2 - 0.8f * u), Size(xb - xa, 0.8f * u))
    // El trenzado: rayitas oblicuas cada 3 dp, alineadas al lienzo para que casen de un tramo a otro.
    val paso = maxOf(1f, 3f * u)
    var x = kotlin.math.floor(maxOf(xa, trenzaDesde) / paso) * paso
    while (x < xb) {
        if (x >= xa - paso) drawLine(a.luz(mezcla(P.TINTA, P.CASCO_OSC, 0.4f), l).copy(alpha = 0.55f), Offset(x, y + g / 2 - 0.6f * u), Offset(x + 1.6f * u, y - g / 2 + 0.6f * u), 0.6f * u)
        x += paso
    }
    if (a.lumbre) drawRect(a.filo(0.4f * l.k), Offset(xa, y - g / 2), Size(xb - xa, 0.6f * u))
}
