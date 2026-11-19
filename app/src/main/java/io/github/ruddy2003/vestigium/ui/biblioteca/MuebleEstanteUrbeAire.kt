package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ANOCHECER
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CREMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CREPUSCULO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LAMPARA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOCHE_SUENOS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TINTA

// =====================================================================
//  Urbe: la paleta, el aire de cada momento y las formas comunes de la biblioteca
// =====================================================================

/**
 * La paleta de la biblioteca de Urbe (sale de la de los cuadros de Roma, en otra disposición que el Ideario): el estuco de
 * almagra con su capa de ocre debajo y el enlucido, el nogal viejo, el azul «papel de azúcar» del fondo, el blanco de la
 * ventana, el verde de la persiana, el travertino, el cielo y la teja de los tejados, el papel, el latón y la lona.
 */
internal object PaletaUrbe {
    /** El estuco de almagra del muro, la mano de ocre que asoma debajo, el enlucido de yeso y la teja (también el ladrillo y el barro). */
    val ALMAGRA = Color(0xFFAE5638)
    val OCRE = Color(0xFFD99B55)
    val YESO = Color(0xFFE3D7BE)
    val TEJA = Color(0xFFB8633D)
    /** El nogal viejo de la librería, encerado, y su sombra honda. */
    val NOGAL = Color(0xFF6E4529)
    val NOGAL_OSC = Color(0xFF3B2416)
    /** El fondo de tablillas machihembradas, pintado de azul «papel de azúcar» hace mucho. */
    val AZUCAR = Color(0xFF8EA2AE)
    /** La pintura de la ventana y del papel (crema), el verde de la persiana y el de las hojas, y el travertino del alféizar. */
    val CREMA = Color(0xFFEFE6D1)
    val PERSIANA = Color(0xFF3D5E47)
    val HOJA = Color(0xFF5C8A44)
    val TRAVERTINO = Color(0xFFE2D6BC)
    /** El cielo de Roma de día. */
    val CIELO = Color(0xFF9DC3DD)
    /** El yeso de los calcos, la tierra verde del pigmento y el vidriado verde cobre de la lámpara. */
    val GESSO = Color(0xFFE9E0CC)
    val TIERRA_VERDE = Color(0xFF6F8A62)
    val VIDRIADO = Color(0xFF3F7C6E)
    /** La tinta, el latón, la lona del bolso y el azul de ultramar del pigmento. */
    val TINTA = Color(0xFF2A2320)
    val LATON = Color(0xFFC19A4C)
    val TELA = Color(0xFFB3A27C)
    val ULTRAMAR = Color(0xFF2F4F93)

    // Las luces: el sol que entra por la persiana, la lámpara, la luna, la noche del cuarto y la de los Sueños, el anochecer.
    val SOL = Color(0xFFFFE4A8)
    val LAMPARA = Color(0xFFFFC77A)
    val LUNA = Color(0xFFC6D5F4)
    val NOCHE = Color(0xFF181429)
    val NOCHE_SUENOS = Color(0xFF0E1531)
    val ANOCHECER = Color(0xFF5B4A80)
    val CREPUSCULO = Color(0xFF3A3463)
    val OCASO = Color(0xFFF0A585)
    /** El frío de la ciudad que entra de noche por la ventana. */
    val FRIO = Color(0xFF4F63A0)
}

/**
 * El aire del momento en el cuarto de Giulia. De día, la luz del sol por la ventana; de noche (el modo oscuro), la lámpara de
 * la cornisa; en los Sueños en claro ([ocaso]), el anochecer por la ventana con la lámpara recién encendida; en los Sueños en
 * oscuro ([luna]), la luna por la ventana con la lámpara apagada. De él salen el color de cada cosa según cuánta luz le llega
 * (de 0 a 1), la sombra, el claro y el filo.
 */
internal class AireUrbe(val oscuro: Boolean, val noche: Boolean) {
    private companion object {
        /** El aire del anochecer, que multiplica: baja y enfría cada color sin quitarle el suyo. */
        val AIRE_OCASO = Color(0xFF8A82B4)
        fun multiplica(c: Color, m: Color) = Color(c.red * m.red, c.green * m.green, c.blue * m.blue, c.alpha)
    }

    /** Si alumbra una luz dibujada (la lámpara o la luna). */
    val lumbre: Boolean get() = oscuro || noche
    /** Los Sueños en oscuro: la luna entra por la ventana y la lámpara está apagada. */
    val luna: Boolean get() = oscuro && noche
    /** Los Sueños en claro: el anochecer. */
    val ocaso: Boolean get() = !oscuro && noche
    /** Si la lámpara está encendida: de noche y al anochecer. */
    val lampara: Boolean get() = lumbre && !luna
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) NOCHE_SUENOS else NOCHE) else SOMBRA_MUEBLE
    /** El color de la luz que manda. */
    val foco: Color get() = if (luna) LUNA else LAMPARA

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(multiplica(c, mezcla(Color.White, AIRE_OCASO, 0.9f * (1f - k))), LAMPARA, 0.12f * k) else c
        return mezcla(mezcla(c, hondo, 0.92f - 0.84f * k.pow(0.75f)), foco, (if (luna) 0.08f else 0.24f) * k)
    }
    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, CREPUSCULO, 0.4f).copy(alpha = a.coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.5f else a)
    fun filo(a: Float): Color = foco.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.5f).coerceIn(0f, 1f) else 0f)
    /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga adivinando en la repisa más lejana. */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(multiplica(t, mezcla(Color.White, AIRE_OCASO, 0.55f * (1f - k))), LAMPARA, 0.06f * k) else t
        return mezcla(mezcla(t, hondo, 0.6f - 0.52f * k), foco, (if (luna) 0.06f else 0.2f) * k)
    }
    /** El papel de las etiquetas, donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(CREMA, CREPUSCULO, 0.22f * (1f - k)) else CREMA
        return mezcla(mezcla(CREMA, hondo, 0.46f - 0.38f * k), foco, 0.16f * k)
    }
    /** La tinta de lo escrito. */
    val tinta: Color get() = if (oscuro) mezcla(TINTA, hondo, 0.3f) else TINTA
    /** La tinta del día en su etiqueta: casi negra, para que el número guarde su contraste aunque el papel se apague. */
    val tintaDia: Color get() = if (oscuro) Color(0xFF14100D) else TINTA
    /**
     * La luz que dibuja la ventana en el mueble, que se pinta en modo pantalla (aclara y calienta sin velar ni borrar el color
     * de lo que toca): el sol de día y la luna en los Sueños en oscuro. Al anochecer, el rosa bajo y tenue; de noche no entra.
     */
    val rayo: Color? get() = when {
        luna -> Color(0xFF8299D4).copy(alpha = 0.5f)
        ocaso -> Color(0xFFF4987E).copy(alpha = 0.34f)
        oscuro -> null
        else -> Color(0xFFFFC274).copy(alpha = 0.46f)
    }
}

/** Las formas comunes de la biblioteca de Urbe. */
internal object TrazoUrbe {
    fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    fun poligono(vararg xy: Float) = Path().apply {
        moveTo(xy[0], xy[1])
        for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
        close()
    }

    /** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz. */
    fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: AireUrbe, fuerza: Float = 0.34f) =
        drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /**
     * El volumen de gouache sobre algo redondo ya pintado: la media luna de sombra del lado contrario a la luz y el claro del otro.
     * De día la luz viene de la izquierda; de noche, de donde esté la luz que manda ([haciaX]: 1 a la derecha, -1 a la izquierda).
     */
    fun DrawScope.redondo(forma: Path, a: AireUrbe, cerca: Float, haciaX: Float, fuerza: Float = 1f) {
        val r = forma.getBounds(); val an = r.width; val hh = r.height
        val sl = if (a.oscuro) -haciaX else 1f
        drawPath(borde(forma, -sl * an * 0.24f, -hh * 0.02f), a.sombra(fuerza * 0.2f))
        drawPath(borde(forma, -sl * an * 0.1f, -hh * 0.01f), a.sombra(fuerza * 0.16f))
        clipPath(forma) {
            drawOval(if (a.oscuro) a.filo(0.24f * cerca) else a.claro(0.24f), Offset(if (sl > 0) r.left + an * 0.1f else r.right - an * 0.34f, r.top + hh * 0.08f), Size(an * 0.24f, hh * 0.7f))
        }
        if (a.lumbre && cerca > 0.04f) drawPath(borde(forma, sl * an * 0.05f, hh * 0.03f), a.filo((0.06f + 0.55f * cerca).coerceAtMost(0.6f)))
    }
}

