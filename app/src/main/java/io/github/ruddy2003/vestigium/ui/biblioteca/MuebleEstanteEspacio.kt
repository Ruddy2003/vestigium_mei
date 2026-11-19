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
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
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
 * taller del eje de la nave que va a Alfa Centauri, donde no hay peso y todo se sujeta. Está repartido en tres archivos:
 * este (la paleta, las medidas, la luz, la pared con el ojo de buey y el módulo), `MuebleEstanteEspacioPiezas.kt` (lo que
 * hay arriba, en los huecos y al pie) y `MuebleEstanteEspacioVolumenes.kt` (las tres clases de entrada).
 */

/** La paleta del estante de Espacio: sale de la del cuadro del tema (`ColorEspacio`), con la noche y las luces propias. */
internal object PaletaEstanteEspacio {
    /** La pintura crema del módulo, desgastada; el aluminio cepillado que asoma donde saltó; la chapa gris azulada de los fondos. */
    val CASCO = Color(0xFFCFCBC0)
    val ACERO = Color(0xFF9AA3B4)
    val CASCO_OSC = Color(0xFF7C8296)
    /** La goma y la espuma oscuras. */
    val GOMA = Color(0xFF2E3246)
    /** El naranja de lo que se agarra (el pasamanos, las correas, los cabos). */
    val NARANJA = Color(0xFFE8843C)
    /** La tela cruda (los estribos, el ribete del acolchado, las etiquetas de tela) y la tela azul del acolchado de la pared. */
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
    /** La luz fría que entra por el ojo de buey: la de los focos del casco, de día; la de las estrellas, de noche. */
    val LUZ_FRIA = Color(0xFFE6EEFF)
    val ESTRELLAS = Color(0xFF9FB8FF)
    /** La lámpara de trabajo, cálida: la que manda de noche. */
    val AMBAR = Color(0xFFFFB25E)
    /** La luz azul de guardia, la de los Sueños. */
    val GUARDIA = Color(0xFF6A8CE8)
    /** La noche de la nave (el turno de descanso), la de los Sueños y su anochecer en claro. */
    val NOCHE = Color(0xFF0C1124)
    val NOCHE_SUENOS = Color(0xFF0A0F30)
    val ANOCHECER = Color(0xFF34407A)
}

/** Cuánta luz le llega a un punto: [k] de la luz que manda (la lámpara, o la de guardia en los Sueños) y [f] del ojo de buey. */
internal class LuzEstanteEspacio(val k: Float, val f: Float)

/**
 * El aire del momento en el módulo: de día (las luces del turno y la luz fría del ojo de buey), de noche (el modo oscuro: la
 * lámpara de trabajo encendida y la ventana con sus estrellas) y en los Sueños ([noche]: la luz azul de guardia al pie y la
 * Vía Láctea en el ojo de buey; en claro, al anochecer de la nave).
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
            val v = mezcla(c, P.ANOCHECER, 0.32f - 0.12f * maxOf(k, f))
            return mezcla(mezcla(v, P.GUARDIA, 0.05f + 0.13f * k), P.ESTRELLAS, 0.1f * f)
        }
        if (noche) {
            val v = mezcla(c, hondo, (0.93f - 0.72f * k.pow(0.9f) - 0.34f * f).coerceIn(0.12f, 0.93f))
            return mezcla(mezcla(v, P.GUARDIA, 0.16f * k), P.ESTRELLAS, 0.14f * f)
        }
        val v = mezcla(c, hondo, (0.93f - 0.86f * k.pow(0.75f) - 0.26f * f).coerceIn(0.07f, 0.93f))
        return mezcla(mezcla(v, P.AMBAR, 0.18f * k), P.LUZ_FRIA, 0.1f * f)
    }

    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, P.ANOCHECER, 0.35f).copy(alpha = a.coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.5f else a)
    /** El filo de la luz que manda en lo que la mira (de día no hay). */
    fun filo(a: Float): Color = foco.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.45f).coerceIn(0f, 1f) else 0f)
    /** El filo frío del ojo de buey en lo que lo mira: de día es la luz crema-azulada; de noche, más tenue. */
    fun filoFrio(a: Float): Color = (if (lumbre) P.ESTRELLAS else P.LUZ_FRIA).copy(alpha = (if (oscuro) a * 0.7f else a).coerceIn(0f, 1f))

    /** El color del ánimo: de noche se apaga menos que lo demás, para que se vea también en la repisa más lejana. */
    fun animo(t: Color, l: LuzEstanteEspacio): Color {
        val k = maxOf(l.k, l.f * 0.6f).coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(t, P.ANOCHECER, 0.2f) else t
        return mezcla(mezcla(t, hondo, 0.66f - 0.56f * k), foco, 0.08f * l.k.coerceIn(0f, 1f))
    }
    /** El papel de las etiquetas: casi no se apaga, para que el día se lea siempre. */
    fun etiqueta(l: LuzEstanteEspacio): Color {
        val k = maxOf(l.k, l.f * 0.6f).coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(P.PAPEL, P.ESTRELLAS, 0.12f) else P.PAPEL
        if (noche) return mezcla(mezcla(P.PAPEL, hondo, 0.4f - 0.3f * k), P.ESTRELLAS, 0.1f + 0.1f * k)
        return mezcla(mezcla(P.PAPEL, hondo, 0.44f - 0.34f * k), P.AMBAR, 0.12f * l.k.coerceIn(0f, 1f))
    }
}

// ---------------------------------------------------------------- trazos que usan los tres archivos

internal fun corridoEspacio(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeEspacio(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoEspacio(p, dx, dy), PathOperation.Difference) }

internal fun poligonoEspacio(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
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
 * El módulo de estiba del taller del eje (MUEBLES.md, versión 2): un mueble de chapa remachada pintada de crema, con la
 * pintura saltada en los cantos, los fondos de chapa gris azulada, las tablas de aluminio con su riel de anclajes y un cabo
 * elástico por repisa que sujeta lo guardado (sin peso, todo flota si no se ata). Contra el acolchado azul del casco, con el
 * ojo de buey a un costado: de día entra por él la luz fría de los focos que alumbran el casco por fuera; de noche (el turno
 * de descanso) se ven las estrellas y manda la lámpara de trabajo, cálida, sujeta a la derecha.
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

    fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, la bitácora de tela con su goma; 1, el cartucho de datos; 2, la bolsa sellada. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla * 31) xor (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** La bitácora, mediana; el cartucho, más bajo; la bolsa, la más alta. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.76f + 0.08f * azar(semilla, 1)
        1 -> 0.63f + 0.07f * azar(semilla, 1)
        else -> 0.86f + 0.09f * azar(semilla, 1)
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
    /**
     * La luz azul de guardia de los Sueños: un piloto redondo en el canto del costado izquierdo, a la altura de la segunda
     * repisa ([GUARDIA_POS] cuenta repisas desde la de arriba); su x, en anchos del mueble.
     */
    const val GUARDIA_X = LADO * 0.62f
    const val GUARDIA_POS = 1.3f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble: el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 126f

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

    /** La luz azul de guardia de los Sueños: redonda, desde el costado izquierdo; se pierde hacia la derecha y lejos de su repisa. */
    fun alcanceGuardia(dx: Float, dy: Float): Float = (1f - sqrt(dx * dx * 0.62f + dy * dy) / 0.82f).coerceIn(0f, 1f).pow(1.3f)

    /**
     * La luz que llega a un punto del cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la
     * segunda), [fx] es la x en anchos del mueble. En los Sueños manda la de guardia, en el costado izquierdo.
     */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float, suenos: Boolean): LuzEstanteEspacio {
        val y = CORONA + pos * FILA / ANCHO_NOMINAL
        val f = alcanceVentana(fx - VENT_X, y - VENT_Y) * (if (suenos) 0.85f else 0.55f)
        val k = if (suenos) alcanceGuardia(fx - GUARDIA_X, (pos - GUARDIA_POS) * FILA / ANCHO_NOMINAL)
        else alcance(fx - LAMP_X, y - LAMP_Y)
        return LuzEstanteEspacio(k, f)
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
        val guardia = Offset(w * GUARDIA_X, ch + fila * GUARDIA_POS)
        /** La luz en un punto del lienzo: arriba, por distancia a cada luz; en el cuerpo, por repisas, como los volúmenes. */
        fun luzEn(p: Offset): LuzEstanteEspacio {
            if (!a.lumbre) return LuzEstanteEspacio(1f, alcanceVentana((p.x / w) - VENT_X, (p.y / w) - VENT_Y))
            val fv = alcanceVentana(p.x / w - VENT_X, p.y / w - VENT_Y) * (if (noche) 0.85f else 0.55f)
            val k = if (noche) alcanceGuardia((p.x - guardia.x) / w, (p.y - guardia.y) / w) else alcance(p.x / w - LAMP_X, p.y / w - LAMP_Y)
            return LuzEstanteEspacio(k, fv)
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
            // ---- la pared: el acolchado azul del casco entre sus cuadernas, arriba y al pie
            pared(a, ch, b, lado) { p -> luzEn(p) }
            // La luz que entra por el ojo de buey y el halo de la lámpara en la pared: unas pocas manchas, no un velo.
            luces(a, vent, lamp, ch)
            ventana(vent, w * VENT_R, a, caida(vent, w * 0.01f))
            // ---- lo que está sujeto a la pared
            cintaTornillos(Offset(w * 0.455f, w * 0.165f), w * 0.11f, a, luzEn(Offset(w * 0.5f, w * 0.17f)), caida(Offset(w * 0.5f, w * 0.17f), w * 0.004f))
            trigo(Offset(w * 0.392f, w * 0.43f), w * 0.2f, a, luzEn(Offset(w * 0.39f, w * 0.34f)), caida(Offset(w * 0.39f, w * 0.34f), w * 0.006f))
            pasamanos(Rect(w * 0.76f, w * 0.335f, w * 0.97f, w * 0.355f), a, luzEn(Offset(w * 0.86f, w * 0.34f)), caida(Offset(w * 0.86f, w * 0.34f), w * 0.006f))
            auriculares(Offset(w * 0.865f, w * 0.345f), w * 0.1f, apoyo, a, luzEn(Offset(w * 0.85f, w * 0.42f)), caida(Offset(w * 0.85f, w * 0.42f), w * 0.008f))
            lampara(lamp, a, caida(lamp, w * 0.008f))
        }

        // ---- las sombras del módulo en la pared, al pie: bajo la última tabla, sobre el zócalo
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.24f), Offset(0f, b.top), Size(w, 5f * u))

        // ---- el fondo de cada repisa: la chapa gris azulada, con la sombra de la tabla de arriba
        d.repisas.forEachIndexed { k, r -> fondoRepisa(k, r, d.ocupado.getOrNull(k) ?: r.left, a, fila, n) { p -> luzEnCuerpo(p) } }
        // En los Sueños, el resplandor azul del piloto de guardia en los fondos de alrededor.
        if (noche && n > 0) clipRect(lado, ch, w - lado, b.top) {
            mancha(guardia, w * 0.8f, P.GUARDIA.copy(alpha = if (oscuro) 0.36f else 0.22f))
            mancha(guardia, w * 0.28f, P.LUZ_FRIA.copy(alpha = if (oscuro) 0.2f else 0.12f))
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

        // ---- la tabla alta, con su sombra en el primer fondo
        tabla(Rect(-2f * u, ch - gT, w + 2f * u, ch), fT, 99, a) { x -> luzEn(Offset(x, ch - gT)) }
        drawRect(a.sombra(if (oscuro) 0.5f else 0.32f), Offset(lado, ch), Size(w - 2 * lado, 3.5f * u))

        // ---- lo que hay sobre la tabla alta, de atrás adelante
        with(PiezasEstanteEspacio) {
            val cVent = Offset(w * 0.55f, apoyo)
            ventilador(cVent, w * 0.27f, a, { p -> luzEn(p) }, caida(cVent - Offset(0f, w * 0.07f), w * 0.01f))
            rotulador(Offset(w * 0.725f, apoyo), Offset(w * 0.705f, w * 0.3f), a, luzEn(Offset(w * 0.71f, w * 0.34f)), caida(Offset(w * 0.71f, w * 0.34f), w * 0.008f))
            // ---- al pie: el zócalo con su rejilla y su cinta al aire, el riel de los estribos
            with(HuecosEstanteEspacio) { pie(b, a) { p -> luzEn(p) } }
            // ---- lo que alumbra por sí mismo, encima de todo
            if (a.lumbre) brillos(lamp, a)
        }
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- el módulo

    /**
     * El fondo de una repisa: la chapa gris azulada del módulo, con su nervio a media altura y sus remaches; la sombra honda
     * de la tabla de arriba y, de día, la luz fría que entra por la izquierda. De noche se pinta por tramos, cada uno con la
     * luz que le llega.
     */
    private fun DrawScope.fondoRepisa(k: Int, r: Rect, oc: Float, a: AireEstanteEspacio, fila: Float, n: Int, luz: (Offset) -> LuzEstanteEspacio) {
        val u = density; val w = size.width
        val chapa = mezcla(P.CASCO_OSC, P.CASCO, 0.42f)
        val tramos = if (a.lumbre) 18 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            drawRect(a.luz(chapa, luz(Offset((xa + xb) / 2, r.center.y))), Offset(xa, r.top), Size(xb - xa, r.height))
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            val f = if (a.oscuro) 1.2f else 1f
            // De día, la luz fría del ojo de buey entra por la izquierda y se pierde hacia la derecha y hacia abajo.
            if (!a.lumbre) mancha(Offset(r.left, r.top + r.height * 0.3f), w * (0.75f - 0.1f * k.coerceAtMost(3)), P.LUZ_FRIA.copy(alpha = 0.3f - 0.06f * k.coerceAtMost(3)))
            // La chapa acanalada: estrías horizontales cada 9 dp, cada una con su luz arriba y su sombra abajo.
            val paso = maxOf(1f, 9f * u)
            var y = r.top + paso
            while (y < r.bottom) {
                drawLine(a.claro(0.11f), Offset(r.left, y), Offset(r.right, y), 1f * u)
                drawLine(a.sombra(0.1f), Offset(r.left, y + 2.6f * u), Offset(r.right, y + 2.6f * u), 1.4f * u)
                y += paso
            }
            // Las juntas de los paneles del fondo, cada tanto, con su luz, su sombra y sus remaches.
            var x = r.left + r.width * (0.18f + 0.1f * azar(k + 300, 0))
            var j = 0
            while (x < r.right - 8f * u) {
                drawRect(a.luz(mezcla(chapa, P.CASCO, 0.3f), luz(Offset(x, r.center.y))), Offset(x - 3f * u, r.top), Size(9f * u, r.height))
                drawLine(a.sombra(0.3f), Offset(x + 6f * u, r.top), Offset(x + 6f * u, r.bottom), 1f * u)
                drawLine(a.claro(0.3f), Offset(x - 3f * u, r.top), Offset(x - 3f * u, r.bottom), 0.6f * u)
                for (m in 0 until 3) remacheEspacio(Offset(x + 1.5f * u, r.top + r.height * (0.3f + 0.25f * m)), 1.2f * u, a.luz(mezcla(chapa, P.ACERO, 0.4f), luz(Offset(x, r.center.y))), a)
                x += maxOf(40f * u, r.width * (0.27f + 0.1f * azar(k + 301, j))); j++
            }
            // El uso: rozaduras y una mancha de grasa.
            pinceladas(Rect(r.left, r.top + r.height * 0.5f, r.right, r.bottom), a.sombra(1f), 951 + k, 14, u, 0.07f)
            if (!a.oscuro) pinceladas(Rect(r.left, r.top + r.height * 0.25f, r.left + r.width * 0.55f, r.bottom), luzMueble(1f), 955 + k, 14, u, if (a.noche) 0.03f else 0.07f)
            // La sombra de la tabla de arriba: honda junto a ella y deshecha hacia abajo.
            drawRect(a.sombra(0.34f * f), Offset(r.left, r.top), Size(r.width, 7f * u))
            drawRect(a.sombra(0.18f * f), Offset(r.left, r.top), Size(r.width, 17f * u))
            drawRect(a.sombra(0.09f * f), Offset(r.left, r.top), Size(r.width, 32f * u))
            // El costado derecho, más hondo (de día la luz viene de la izquierda); el izquierdo, con su sombra corta.
            drawRect(a.sombra(0.14f), Offset(r.right - 12f * u, r.top), Size(12f * u, r.height))
            drawRect(a.sombra(0.22f), Offset(r.right - 5f * u, r.top), Size(5f * u, r.height))
            drawRect(a.sombra(0.2f), Offset(r.left, r.top), Size(5f * u, r.height))
            // De noche, el cono de la lámpara entra en el fondo de la primera repisa.
            if (a.oscuro && !a.noche && k == 0) {
                val p = Offset(w * 0.62f, r.center.y)
                val kc = luz(p).k
                if (kc > 0.1f) mancha(p, w * 0.55f, P.AMBAR.copy(alpha = 0.26f * kc))
            }
            // La sombra que el último volumen deja en el fondo, a su derecha (de día) o a su izquierda (de noche, la lámpara a la derecha).
            if (oc > r.left + 6f * u) {
                val largo = (if (a.lumbre) 16f else 11f) * u
                drawPath(poligonoEspacio(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.24f, oc + largo, r.top + r.height * 0.32f, oc + largo, r.bottom), a.sombra(if (a.oscuro) 0.4f else 0.24f))
            }
        }
    }

    /**
     * Un costado del módulo: la chapa crema doblada en U, con su columna de remaches, la pintura saltada en el canto, el
     * lomo de luz del lado de la ventana y, en cada repisa, la argolla del cabo elástico.
     */
    private fun DrawScope.costado(p: Rect, izq: Boolean, a: AireEstanteEspacio, repisas: Int, ch: Float, fila: Float, huecos: List<Rect>) {
        val u = density; val w = size.width
        val fx = p.center.x / w
        val tramos = if (a.lumbre) (p.height / maxOf(1f, fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        for (t in 0 until tramos) {
            val ya = p.top + p.height * t / tramos; val yb = p.top + p.height * (t + 1) / tramos + 0.6f
            val l = if (a.lumbre) cercaCuerpo(((ya + yb) / 2 - ch) / fila, repisas, fx, a.noche) else LuzEstanteEspacio(1f, 0f)
            drawRect(a.luz(P.CASCO, l), Offset(p.left, ya), Size(p.width, yb - ya))
        }
        // El doblez de la chapa: el canto de dentro es una cara que mira hacia el hueco.
        val canto = p.width * 0.22f
        val xc = if (izq) p.right - canto else p.left
        drawRect(a.sombra(if (izq) 0.24f else 0.08f), Offset(xc, p.top), Size(canto, p.height))
        drawRect(if (izq) a.sombra(0.12f) else a.claro(0.3f), Offset(if (izq) p.left else p.right - 2f * u, p.top), Size(2f * u, p.height))
        drawRect(a.claro(if (izq) 0.35f else 0.18f), Offset(if (izq) p.left + 2.5f * u else p.left + canto + 1f * u, p.top), Size(1.2f * u, p.height))
        pinceladas(p, a.sombra(1f), if (izq) 971 else 972, (p.height / (24f * u)).toInt().coerceIn(6, 200), u, 0.07f)
        // La columna de remaches y la pintura saltada que deja ver el aluminio.
        val xr = if (izq) p.left + p.width * 0.36f else p.right - p.width * 0.36f
        val paso = maxOf(1f, 14f * u)
        var y = p.top + 8f * u
        var j = 0
        while (y < p.bottom - 6f * u) {
            val l = if (a.lumbre) cercaCuerpo((y - ch) / fila, repisas, fx, a.noche) else LuzEstanteEspacio(1f, 0f)
            remacheEspacio(Offset(xr, y), 1.3f * u, a.luz(mezcla(P.CASCO, P.ACERO, 0.35f), l), a)
            if (azar(if (izq) 61 else 62, j) < 0.1f) {
                val sx = if (izq) p.right - canto - 4f * u else p.left + canto
                drawOval(a.luz(P.ACERO, l), Offset(sx, y + 3f * u), Size(4f * u, (3f + 5f * azar(63, j)) * u))
                drawOval(a.luz(mezcla(P.ACERO, P.CASCO, 0.5f), l), Offset(sx + 0.8f * u, y + 3.6f * u), Size(1.4f * u, 2f * u))
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
        // El piloto de guardia, en el canto del costado izquierdo: apagado de día; azul y con su halo en los Sueños.
        if (izq && repisas > 0) {
            val g = Offset(w * GUARDIA_X, ch + fila * GUARDIA_POS)
            val l = if (a.lumbre) cercaCuerpo(GUARDIA_POS, repisas, GUARDIA_X, a.noche) else LuzEstanteEspacio(1f, 0f)
            drawRoundRect(a.sombra(0.4f), Offset(g.x - 4.4f * u, g.y - 8f * u), Size(10f * u, 17f * u), CornerRadius(3f * u))
            drawRoundRect(a.luz(P.GOMA, if (a.noche) LuzEstanteEspacio(0.5f, 0f) else l), Offset(g.x - 5f * u, g.y - 9f * u), Size(10f * u, 18f * u), CornerRadius(3f * u))
            if (a.noche) {
                drawCircle(P.GUARDIA.copy(alpha = 0.25f), 9f * u, g)
                drawCircle(P.GUARDIA.copy(alpha = 0.45f), 5.5f * u, g)
                drawCircle(mezcla(P.GUARDIA, Color.White, 0.6f), 3.4f * u, g)
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
        listOf(r.left + 4f * u, r.right - 4f * u).forEach { x -> remacheEspacio(Offset(x, r.center.y), 1.3f * u, a.luz(P.ACERO, lp), a) }
        textoEn(medidor, p.texto.uppercase(), Rect(r.left + 9f * u, r.top + 1f * u, r.right - 9f * u, r.bottom - 1f * u), LETRA_PLACA, P.TINTA, 9f, peso = FontWeight.Medium, espaciado = 1f, minimo = 5.5f)
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
