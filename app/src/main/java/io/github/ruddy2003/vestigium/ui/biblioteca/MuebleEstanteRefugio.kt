package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * La paleta del estante de Refugio: la del Ideario del mismo tema (el cuarto de registros del refugio), con lo que trae la sala
 * de lectura. Del Ideario: el muro de bloques pintado de crema, el zócalo verde azulado, el acero oliva del armario, el acero gris,
 * el ocre, el naranja, el ámbar de los pilotos, el fósforo verde, la hoja de la tomatera, el papel de las etiquetas con su tinta,
 * el rosa violeta de la lámpara de cultivo y el verde negro azulado de la noche. Lo nuevo: el esmalte verde agua de la librería,
 * el cromo, el coral, la baquelita, el vidrio con agua y la tarjeta crema tostada de los rótulos.
 */
internal object PaletaRefugio {
    /** El muro de bloques pintado de crema, su sombra y el hormigón donde saltó la pintura (los del Ideario). */
    val CREMA = Color(0xFFE2D6B6)
    val CREMA_OSC = Color(0xFFBFB08A)
    val HORMIGON = Color(0xFF8F8B7E)
    /** El verde azulado del zócalo y de la tubería, y el de la carcasa de la terminal. */
    val TEAL = Color(0xFF3E7470)
    val CARCASA = Color(0xFF6F837B)
    /** El acero oliva del armario del Ideario: aquí, el fondo de los huecos y el casco. */
    val OLIVA = Color(0xFF4A5A3A)
    val OLIVA_LUZ = Color(0xFF6A7A52)
    val OLIVA_OSC = Color(0xFF2A3420)
    /** El acero gris de grapas, tornillos y rejillas, y su sombra. */
    val ACERO = Color(0xFF7A8088)
    val ACERO_OSC = Color(0xFF4A5056)
    /** El ámbar de los pilotos (y de la luz de guardia), el ocre y el naranja. */
    val AMBAR = Color(0xFFFFB23F)
    val OCRE = Color(0xFFC79B3C)
    val NARANJA = Color(0xFFD9652C)
    /** El fósforo verde de las pantallas, su claro y el vidrio apagado. */
    val FOSFORO = Color(0xFF3DFF7F)
    val FOSFORO_CLARO = Color(0xFF9CF7B4)
    val PANTALLA = Color(0xFF0E2D19)
    /** La hoja de la tomatera y de las lechugas, y el rojo del tomate. */
    val HOJA = Color(0xFF5C7F3E)
    val TOMATE = Color(0xFFC9442A)
    /** El papel de las etiquetas, la tinta, y la tarjeta crema tostada de los rótulos (de valor medio). */
    val ETIQUETA = Color(0xFFE8E0C8)
    val TINTA = Color(0xFF1A1A14)
    val TARJETA = Color(0xFFC6B386)
    /** El rosa violeta del tubo de la lámpara de cultivo. */
    val LAMPARA = Color(0xFFE99BD9)
    /** La noche del refugio: verde negro azulado (la del Ideario), y la de los Sueños, un poco más fría. */
    val NOCHE = Color(0xFF0A1614)
    val NOCHE_SUENOS = Color(0xFF0A1418)

    /** El esmalte verde agua de la librería: su tono, su luz y su sombra. */
    val AGUA = Color(0xFF86C2B4)
    val AGUA_LUZ = Color(0xFFBCE2D6)
    val AGUA_OSC = Color(0xFF4F8C80)
    /** El cromo: su gris de reflejo, el brillo y el reflejo oscuro. */
    val CROMO = Color(0xFFC3CDCF)
    val CROMO_LUZ = Color(0xFFF6F8F2)
    val CROMO_OSC = Color(0xFF3B474C)
    /** El coral del esmalte de la época, el amarillo mantequilla, la baquelita parda y el vinilo crema. */
    val CORAL = Color(0xFFE07658)
    val MANTEQUILLA = Color(0xFFF0D388)
    val BAQUELITA = Color(0xFF3B2A22)
    val VINILO = Color(0xFFE9DDBF)
    /** El agua purificada en el vidrio: su verde claro y su hondo. */
    val AGUA_VIDRIO = Color(0xFF9FD3C8)
    val AGUA_HONDA = Color(0xFF2E6D68)
    /** La luz de las luminarias del techo; la del anochecer del refugio y sus sombras; la pintura fosforescente. */
    val LUZ_TECHO = Color(0xFFFFF5DE)
    val MELOCOTON = Color(0xFFF6C49C)
    val TARDE = Color(0xFFF0A574)
    val ANOCHECER = Color(0xFF3B3354)
    val FOSFORESCENTE = Color(0xFFD8F8C6)

    /**
     * El ánimo en vinilo de carpeta (de lo malo a lo bueno): coral, mantequilla, verde azulado. En medio, la mantequilla de la
     * época y no el crema de la escala: así un ánimo de en medio tiene color y no se confunde con la pared ni con la tarjeta.
     */
    val VINILOS = listOf(Color(0xFFD24E36), Color(0xFFEBC25E), Color(0xFF1C7A72))
    /** El ánimo en las piezas de la conserva: tomate, albaricoque, judía verde. */
    val CONSERVAS = listOf(Color(0xFFD2472F), Color(0xFFE9AC4A), Color(0xFF2F8A6E))
    /** El ánimo en papel de bolsa: coral, mantequilla, verde azulado. */
    val PAPELES = listOf(Color(0xFFD65A3C), Color(0xFFE9C873), Color(0xFF2E7F76))

    /** La letra del día y del mes: la de terminal de los rótulos del Ideario. */
    val LETRA: FontFamily = LetraExtra.vt323
}

/**
 * El aire del momento en el estante de Refugio: de día (las luminarias del techo), de noche (el modo oscuro, el turno de noche con
 * las luces bajas: arden la pantalla del monitor, la luz de guardia y la lámpara de cultivo) y en los Sueños ([noche]: en claro,
 * el anochecer del refugio, cuando las luminarias bajan; en oscuro, las luces apagadas, la pantalla en reposo y la guardia baja).
 */
internal class AireRefugio(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaRefugio
    /** Cuánto alumbra la pantalla del monitor (de día brilla, pero no alumbra). */
    val pantalla: Float get() = if (oscuro && !noche) 1f else if (oscuro) 0.62f else if (noche) 0.3f else 0f
    /** Cuánto arde la luz de guardia ámbar del costado derecho. */
    val guardia: Float get() = if (oscuro && !noche) 1f else if (oscuro) 0.42f else if (noche) 0.35f else 0f
    /** Cuánto arde el tubo de la lámpara de cultivo bajo el mueble. */
    val cultivo: Float get() = if (oscuro && !noche) 1f else if (oscuro) 0.25f else if (noche) 0.5f else 0f
    /** Si alumbran las luminarias del techo a pleno (solo de día). */
    val techo: Boolean get() = !oscuro && !noche
    /** El anochecer del refugio: los Sueños en claro. */
    val tarde: Boolean get() = !oscuro && noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else if (noche) p.ANOCHECER else SOMBRA_MUEBLE

    fun en(k: Float, tinte: Color, amb: Float, lav: Float = 0f) = LuzRefugio(this, k.coerceIn(0f, 1f), tinte, amb.coerceIn(0f, 1f), lav.coerceIn(0f, 1f))

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche del refugio. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.17f + n.red * 0.8f, c.green * 0.17f + n.green * 0.8f, c.blue * 0.17f + n.blue * 0.8f, c.alpha)
    }

    /** El color del ánimo hundido: guarda más de su tono, para que las entradas lejanas se sigan distinguiendo. */
    fun hundidoAnimo(c: Color): Color {
        val n = hondo
        val f = if (noche) 0.25f else 0.28f
        return Color(c.red * f + n.red * (1f - f), c.green * f + n.green * (1f - f), c.blue * f + n.blue * (1f - f), c.alpha)
    }

    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, p.ANOCHECER, 0.6f).copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
        else -> mezcla(SOMBRA_MUEBLE, Color(0xFF203234), 0.3f).copy(alpha = a.coerceIn(0f, 1f))
    }
}

/**
 * La luz de una cosa en un sitio: [k], cuánto le llega de las luces del cuadro (la pantalla, la guardia o la lámpara de cultivo)
 * y de qué color ([tinte]); [amb], cuánto le llega de las luminarias del techo (1 arriba a la izquierda, menos hacia abajo y a la
 * derecha); [lav], de noche y en la pared, el rebote ancho y flojo del fósforo en el cuarto. [c] da el color de algo con esa luz,
 * [s] la sombra, [b] el plano de luz y [filo] el borde encendido por una luz.
 */
internal class LuzRefugio(val a: AireRefugio, val k: Float, val tinte: Color, val amb: Float, val lav: Float = 0f, val pano: Float = 0f) {
    private val p = PaletaRefugio

    /** Cuánto enciende la luz lo que toca: poco lejos y de golpe cerca, para que manden los charcos. */
    private val q: Float get() = (1.15f * k * k).coerceAtMost(0.95f)

    /** La misma luz dentro (1) o fuera (0) del paño de las luminarias bajadas (los Sueños en claro). */
    fun conPano(v: Float): LuzRefugio = if (!a.tarde) this else LuzRefugio(a, k, tinte, amb, lav, v.coerceIn(0f, 1f))

    fun c(col: Color): Color = when {
        a.oscuro -> {
            val base = if (lav > 0.01f) mezcla(a.hundido(col), apagado(col), lav) else a.hundido(col)
            mezcla(base, alaLuz(col), q)
        }
        a.noche -> {
            // El anochecer: lo que mira a las luminarias, melocotón; lo de lejos, hacia el violeta, sin perder su color.
            val hondo = (0.36f + 0.38f * (1f - amb)).coerceIn(0.1f, 0.64f)
            val fuera = mezcla(mezcla(anochecer(col, hondo), alOcaso(col), ((amb - 0.62f) * 1.9f).coerceIn(0f, 0.75f)), alaLuz(col), 0.6f * q)
            // En el paño: el mismo color, encendido de melocotón (más claro y cálido), con borde neto.
            if (pano > 0.01f) mezcla(fuera, enPano(col), pano) else fuera
        }
        else -> alTecho(col)
    }

    /**
     * Un color a la luz de las luminarias: cerca (arriba a la izquierda) se aclara hacia su crema; lejos se apaga hacia su propio
     * oscuro, algo frío, sin agrisar. Como el armario del Ideario, más claro a la izquierda.
     */
    private fun alTecho(c: Color): Color =
        if (amb >= 0.55f) mezcla(c, p.LUZ_TECHO, (amb - 0.55f) * 0.55f)
        else {
            val f = (0.55f - amb) * 0.72f
            Color(c.red * (1f - f), c.green * (1f - f * 0.95f), c.blue * (1f - f * 0.86f), c.alpha)
        }

    /** Un color dentro del paño de las luminarias bajadas: su ocaso, un paso más claro, sin lavar el color propio. */
    private fun enPano(c: Color): Color =
        // Las luminarias están bajadas: el paño es más apagado que el día, melocotón y no amarillo.
        mezcla(Color((c.red * 0.94f + 0.03f).coerceAtMost(1f), (c.green * 0.8f + 0.01f).coerceAtMost(1f), (c.blue * 0.68f).coerceAtMost(1f), c.alpha), p.MELOCOTON, 0.1f)

    /** Un color bajo las luminarias del anochecer: melocotón, más claro, sin lavar el color propio. */
    private fun alOcaso(c: Color): Color = mezcla(Color((c.red * 1.14f + 0.07f).coerceAtMost(1f), (c.green * 1.0f + 0.04f).coerceAtMost(1f), (c.blue * 0.84f + 0.02f).coerceAtMost(1f), c.alpha), p.MELOCOTON, 0.22f)

    /** Un color al anochecer, lejos de las luminarias: su mismo color más hondo, apenas más frío (el violeta va solo en las sombras). */
    private fun anochecer(c: Color, hondo: Float): Color =
        Color(c.red * (1f - hondo), c.green * (1f - hondo * 0.99f), c.blue * (1f - hondo * 0.95f), c.alpha)

    /**
     * Un color bajo la luz que le toca: su propio color con el tinte de esa luz encima (el fósforo lo enverdece, la guardia lo
     * dora, la lámpara de cultivo lo rosa) y algo más claro: lo de junto a la luz es lo más claro del cuadro de noche.
     */
    private fun alaLuz(c: Color): Color {
        val t = tinte
        return Color((c.red * (0.5f + 0.72f * t.red) + 0.04f * t.red).coerceAtMost(1f),
            (c.green * (0.5f + 0.7f * t.green) + 0.04f * t.green).coerceAtMost(1f),
            (c.blue * (0.5f + 0.66f * t.blue) + 0.03f * t.blue).coerceAtMost(1f), c.alpha)
    }

    /** Un color apagado por el rebote del fósforo en el cuarto: su propio color a media luz, con algo del verde de la noche. */
    private fun apagado(c: Color): Color {
        val n = a.hondo
        return Color(c.red * 0.36f + n.red * 0.32f, c.green * 0.42f + n.green * 0.34f, c.blue * 0.4f + n.blue * 0.34f, c.alpha)
    }

    fun s(alfa: Float): Color = a.sombra(if (a.tarde) alfa * (1.15f + 0.35f * pano) else alfa)

    fun b(alfa: Float): Color = when {
        a.oscuro -> tinte.copy(alpha = (alfa * 0.95f * k).coerceIn(0f, 1f))
        a.noche -> mezcla(luzMueble(1f), p.TARDE, 0.45f).copy(alpha = (alfa * (0.3f + 0.6f * maxOf(amb - 0.2f, k) + 0.4f * pano)).coerceIn(0f, 1f))
        else -> mezcla(luzMueble(1f), p.LUZ_TECHO, 0.6f).copy(alpha = (alfa * (0.55f + 0.5f * amb)).coerceIn(0f, 1f))
    }

    fun filo(alfa: Float): Color = if (k < 0.03f || (!a.oscuro && !a.noche)) Color.Transparent
        else tinte.copy(alpha = (alfa * k * (if (a.oscuro) 1.25f else 0.7f)).coerceIn(0f, 0.92f))

    /** El color del ánimo con la luz que le llega; de noche se apaga algo menos que lo demás y conserva su color. */
    fun animo(base: Color): Color = when {
        a.oscuro -> mezcla(a.hundidoAnimo(base), mezcla(base, alaLuz(base), 0.55f), (0.1f + 0.9f * q).coerceAtMost(0.95f))
        a.noche -> mezcla(mezcla(anochecer(base, (0.2f + 0.34f * (1f - amb)).coerceIn(0.05f, 0.5f)), p.TARDE, 0.16f * (amb - 0.55f).coerceIn(0f, 0.45f)), alaLuz(base), 0.45f * q)
        else -> if (amb >= 0.55f) mezcla(base, p.LUZ_TECHO, (amb - 0.55f) * 0.28f)
            else { val f = (0.55f - amb) * 0.4f; Color(base.red * (1f - f), base.green * (1f - f), base.blue * (1f - f * 0.9f), base.alpha) }
    }

    /**
     * La tarjeta crema tostada de los rótulos: un valor medio, teñida de noche por la luz que le toca (cerca, de su color; lejos,
     * hundida); nunca tanto que el número deje de leerse.
     */
    fun tarjeta(): Color = when {
        // De noche, del color de la luz que le toca (verdosa junto a la pantalla, ámbar junto a la guardia, rosada junto al tubo) y
        // hundida lejos de las tres.
        a.oscuro -> mezcla(mezcla(p.TARJETA, a.hondo, 0.68f), mezcla(alaLuz(mezcla(p.TARJETA, Color.White, 0.1f)), tinte, 0.24f), (k * 1.45f).coerceAtMost(1f))
        a.noche -> mezcla(mezcla(p.TARJETA, p.ANOCHECER, 0.2f - 0.1f * maxOf(amb, k)), p.TARDE, 0.1f * amb)
        else -> c(p.TARJETA)
    }

    /** La tinta sobre un papel de color [fondo]: oscura donde el papel tiene luz; donde se hundió, un claro que se lee. */
    fun tinta(fondo: Color): Color {
        if (!a.oscuro) return p.TINTA
        return if (luminanciaRefugio(fondo) < 0.09f) mezcla(Color(0xFF9AA294), tinte, 0.15f * k) else Color(0xFF0B0F0C)
    }
}

internal fun luminanciaRefugio(c: Color): Float {
    fun ch(v: Float) = if (v <= 0.04045f) v / 12.92f else Math.pow(((v + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
    return 0.2126f * ch(c.red) + 0.7152f * ch(c.green) + 0.0722f * ch(c.blue)
}

/**
 * El dibujo del estante del diario de Refugio (lo usa `EstanteRefugio`, en `BibliotecaFuturo.kt`): la librería de pared de la
 * sala de lectura del refugio, en la otra pared del mismo cuarto de registros del Ideario (el muro de bloques crema, el zócalo
 * verde azulado, la canaleta, la rejilla del suelo). Es de chapa esmaltada verde agua, de los años cincuenta: la cornisa de canto
 * redondo con su banda cromada y el friso con la rejilla del altavoz, las esquinas de arriba redondeadas, los costados con su
 * junquillo cromado, las tablas con canto de aluminio acanalado y su portarrótulos, el fondo de melamina oliva con bumeranes, el
 * faldón cromado y las patas cónicas abiertas. Arriba, el monitor de las bombas con su pantalla redonda de fósforo, la garrafa de
 * agua bajo el grifo, los discos y el casco de frutero; en la pared, el reloj de rayos y el esquema de la depuradora. Al pie, bajo
 * el mueble, la bandeja hidropónica con su lámpara de cultivo; la regadera y el taburete de vinilo.
 *
 * De día alumbran las luminarias del techo, arriba a la izquierda (fuera del cuadro). De noche arden la pantalla del monitor
 * (fósforo verde, arriba), la luz de guardia ámbar del costado derecho y el tubo rosa de la lámpara de cultivo bajo el mueble;
 * entre sus charcos, el verde negro azulado del Ideario. En los Sueños en claro, el anochecer del refugio; en oscuro, la pantalla en
 * reposo y la guardia baja.
 *
 * La pared, el fondo de los huecos y los trazos comunes están en `MuebleEstanteRefugioPared.kt`, el mueble en
 * `MuebleEstanteRefugioMueble.kt`, lo de arriba en `MuebleEstanteRefugioCorona.kt`, lo que llena los huecos en
 * `MuebleEstanteRefugioHuecos.kt`, el pie en `MuebleEstanteRefugioPie.kt` y las entradas en `MuebleEstanteRefugioVolumenes.kt`.
 */
internal object MuebleEstanteRefugio {
    val LETRA: FontFamily = PaletaRefugio.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteRefugio` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.34f
    val LADO = 0.082f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    /** Más ancho cuanto más escribiste: crece rápido en las entradas cortas y se llena hacia las 120 palabras, con algo de azar. */
    fun ancho(palabras: Int): Dp {
        val crece = (2.2f * sqrt(palabras.coerceAtLeast(0).toFloat())).coerceAtMost(24f)
        return (25f + crece + Math.floorMod(palabras * 37 + 11, 6)).dp
    }

    /** Cuál de las tres es una entrada: 0, la carpeta de vinilo; 1, el sifón; 2, la bolsa de papel del economato. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla xor 0x5e3a, 67)
        return if (x < 0.42f) 0 else if (x < 0.71f) 1 else 2
    }

    /** La carpeta, la más alta (con sus pestañas); el sifón, de dos medidas; la bolsa, más llena y más alta cuanto más escribiste. */
    fun alto(semilla: Int, palabras: Int): Float {
        val lleno = (palabras / 800f).coerceIn(0f, 1f)
        return when (clase(semilla)) {
            0 -> 0.85f + 0.03f * lleno + 0.08f * azar(semilla, 1)
            1 -> (if (azar(semilla, 3) < 0.35f) 0.8f else 0.88f) + 0.03f * lleno + 0.05f * azar(semilla, 1)
            else -> 0.74f + 0.1f * lleno + 0.07f * azar(semilla, 1)
        }
    }

    // ---------------------------------------------------------------- la luz (todo en anchos del mueble)

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 130f
    /** Donde pisa lo de arriba (la cara de arriba de la cornisa), en anchos desde arriba. */
    const val APOYO = 0.47f
    /** La pantalla del monitor, arriba a la izquierda del centro. */
    const val PANTALLA_X = 0.175f
    const val PANTALLA_Y = 0.356f
    /** La luz de guardia del costado derecho: su x y a qué fracción del cuerpo está. */
    const val GUARDIA_X = 0.959f
    const val GUARDIA_T = 0.56f
    /** El tubo de la lámpara de cultivo, bajo el mueble: su x y su altura sobre el pie del lienzo (en anchos). */
    const val CULTIVO_X = 0.47f
    const val CULTIVO_Y = 0.255f

    /** El alto del cuerpo (de la corona al pie de la última repisa) con [repisas] repisas, en anchos. */
    fun cuerpo(repisas: Int): Float = repisas * FILA / ANCHO_NOMINAL

    /** Dónde arde la guardia, en anchos desde arriba, en un estante de [repisas] repisas. */
    fun guardiaY(repisas: Int): Float = CORONA + cuerpo(repisas) * GUARDIA_T

    /**
     * La luz de un punto del lienzo ([x], [y] en anchos desde arriba a la izquierda) en un estante de [repisas] repisas y [alto]
     * anchos. [delante]: cuánto está por delante de la pared (0, la pared; 0.03, los cantos; 0.05, las entradas).
     */
    fun luz(a: AireRefugio, x: Float, y: Float, alto: Float, repisas: Int, delante: Float = 0f): LuzRefugio {
        val kp = if (a.pantalla > 0f) a.pantalla * kPantalla(x, y) else 0f
        val lav = if (a.pantalla > 0f && a.oscuro && delante < 0.015f) a.pantalla * lavadoPantalla(x, y) else 0f
        val kg = if (a.guardia > 0f) a.guardia * kGuardia(x, y, repisas) else 0f
        val kc = if (a.cultivo > 0f) a.cultivo * kCultivo(x, y, alto) else 0f
        val k = (kp + kg + kc).coerceIn(0f, 1f)
        val suma = kp + kg + kc + 0.0001f
        val tinte = if (k < 0.001f) PaletaRefugio.FOSFORO_CLARO
            else mezcla(mezcla(PaletaRefugio.FOSFORO_CLARO, PaletaRefugio.AMBAR, kg / (kp + kg + 0.0001f)), PaletaRefugio.LAMPARA, kc / suma)
        // Las luminarias, fuera del cuadro arriba a la izquierda: más luz cerca, menos hacia abajo y a la derecha.
        val amb = (1.02f - 0.6f * x - 0.07f * y - 0.16f * maxOf(0f, y - 0.6f) / maxOf(0.5f, alto)).coerceIn(0.2f, 1f)
        return a.en(k, tinte, amb, lav)
    }

    /** Cuánto alumbra la pantalla un punto: un charco que se apaga despacio (la pantalla mira adelante y alumbra a los lados). */
    fun kPantalla(x: Float, y: Float): Float {
        val dy = (y - PANTALLA_Y) * (if (y > PANTALLA_Y) 0.78f else 1f)
        return (1.9f * exp(-4.3f * dist((x - PANTALLA_X) * 0.92f, dy))).coerceAtMost(1f)
    }

    /** El rebote del fósforo en la pared: ancho y flojo (como el halo de la pared del Ideario). */
    fun lavadoPantalla(x: Float, y: Float): Float = (0.7f * exp(-2.1f * dist((x - PANTALLA_X) * 0.85f, (y - PANTALLA_Y) * 0.9f))).coerceAtMost(0.7f)

    /** Cuánto alumbra la guardia un punto: su vidrio mira hacia dentro del mueble, a la izquierda. */
    fun kGuardia(x: Float, y: Float, repisas: Int): Float {
        val dx = (x - GUARDIA_X) * 0.86f
        return (1.85f * exp(-5.2f * dist(dx, (y - guardiaY(repisas)) * 0.95f))).coerceAtMost(1f)
    }

    /** Cuánto alumbra el tubo de cultivo un punto: hacia abajo y a los lados; lo de encima del faldón recibe poco. */
    fun kCultivo(x: Float, y: Float, alto: Float): Float {
        val yc = alto - CULTIVO_Y
        val arriba = (yc - y).coerceAtLeast(0f)
        val tapa = (1f - 4.5f * arriba).coerceIn(0.18f, 1f)
        return (1.75f * exp(-5.6f * dist((x - CULTIVO_X) * 0.8f, (y - yc) * 0.9f))).coerceAtMost(1f) * tapa
    }

    private fun dist(dx: Float, dy: Float) = sqrt(dx * dx + dy * dy)

    /**
     * De qué lado le llega la luz a un punto: -1 si de la izquierda, 1 si de la derecha. De día y en los Sueños en claro, de las
     * luminarias (izquierda); de noche, de la luz que más lo alumbra.
     */
    fun hacia(a: AireRefugio, x: Float, y: Float, alto: Float, repisas: Int): Float {
        if (!a.oscuro) return -1f
        val kp = kPantalla(x, y) * a.pantalla
        val kg = kGuardia(x, y, repisas) * a.guardia
        val kc = kCultivo(x, y, alto) * a.cultivo
        return when {
            kp >= kg && kp >= kc -> if (x < PANTALLA_X) 1f else -1f
            kg >= kc -> 1f
            else -> if (x < CULTIVO_X) 1f else -1f
        }
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireRefugio(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoRefugio(w, h, ch, d.izquierda.right, d.derecha.left, fondo, n, a)

        // ---- debajo de la corona guardada, la fila de la junta (que puede caer a medio píxel): el esmalte del pie de la cornisa
        // desde el píxel entero de arriba, así no asoma el fondo.
        drawRect(g.luz(w * 0.5f, ch, 0.03f).c(PaletaRefugio.AGUA_OSC), Offset(0f, floor(ch) - 1f), Size(w, 3f))
        val clave = "${w.toInt()}|${ch.toInt()}|$oscuro|$noche|$density"
        CapaRefugio.pegar(this, if (noche) 1 else 0, clave, ch) {
            with(ParedEstanteRefugio) { paredArriba(g) }
            with(CoronaEstanteRefugio) { detras(g) }
            with(MuebleRefugio) { cornisa(g) }
            with(CoronaEstanteRefugio) { delante(g) }
            // Al anochecer, lo mismo otra vez dentro del paño de las luminarias bajadas: la pared con el paño, y lo que está delante
            // (lo de encima y la cornisa) con el paño corrido hacia la luz, así el borde se quiebra al cambiar de plano.
            if (a.tarde) pintarPano(g)
            granoMueble(Rect(0f, 0f, w, ch), oscuro)
        }
        if (n == 0) return
        with(ParedEstanteRefugio) { d.repisas.forEachIndexed { k, r -> fondoHueco(k, r, g) } }
        with(HuecosEstanteRefugio) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, g)
            }
        }
        with(MuebleRefugio) {
            juntas(g, d)
            d.tablas.forEachIndexed { k, r -> tabla(k, r, g) }
            d.placas.forEach { p -> placa(p, medidor, g) }
            costados(g, d)
        }
        with(PieEstanteRefugio) { pie(g) }
        with(MuebleRefugio) { guardia(g) }
        // El grano fino, solo en lo que se ve del mueble y del pie: el de los huecos ya va en su baldosa (y casi todo lo tapan las
        // entradas), así no se pega encima de todo el cuerpo.
        granoMueble(Rect(0f, ch, d.izquierda.right, fondo), oscuro)
        granoMueble(Rect(d.derecha.left, ch, w, fondo), oscuro)
        d.tablas.forEach { t -> granoMueble(Rect(t.left, t.top, t.right, t.bottom), oscuro) }
        granoMueble(Rect(0f, fondo, w, h), oscuro)
    }

    /** Cuántas bandas tiene la penumbra del paño. */
    private const val BANDAS = 6

    /**
     * El paño de las luminarias bajadas (los Sueños en claro), pintado encima de la corona ya pintada sin él: la pared otra vez
     * dentro del paño, con su núcleo entero y la penumbra en bandas (cada una con algo menos de paño), que se ensancha hacia abajo,
     * lejos de la luminaria; y lo de delante otra vez donde la pared se repintó, con el paño corrido hacia la luz según lo lejos que
     * esté de la pared (el cable apenas, lo que pisa el tablero más y la cornisa, que vuela, más aún), así el borde no sigue en
     * línea recta. Cada zona se pinta una sola vez: nada translúcido queda pintado dos veces.
     */
    private fun DrawScope.pintarPano(g: MarcoRefugio) {
        val w = g.w
        fun t(i: Int) = -0.5f + i / BANDAS.toFloat()
        fun valor(i: Int) = if (i == 0) 1f else 1f - (i - 0.5f) / BANDAS
        val fuera = panoRefugio(w, 0f, 0f, t(BANDAS))
        fun DrawScope.porBandas(dx: Float, dy: Float, pieza: DrawScope.() -> Unit) {
            for (i in 0..BANDAS) {
                g.pano = valor(i)
                val dentro = panoRefugio(w, dx, dy, t(i))
                if (i == 0) clipPath(dentro) { pieza() }
                else clipPath(dentro) { clipPath(panoRefugio(w, dx, dy, t(i - 1)), androidx.compose.ui.graphics.ClipOp.Difference) { pieza() } }
            }
        }
        // La pared, con su paño y su penumbra.
        porBandas(0f, 0f) { with(ParedEstanteRefugio) { paredArriba(g) } }
        // Lo de delante, solo donde la pared se repintó: fuera de su paño corrido, sin paño; dentro, por bandas.
        listOf<Triple<Float, Float, DrawScope.() -> Unit>>(
            Triple(-0.01f, -0.004f) { with(CoronaEstanteRefugio) { detras(g) } },
            Triple(-0.045f, -0.018f) { with(MuebleRefugio) { cornisa(g) } },
            Triple(-0.026f, -0.01f) { with(CoronaEstanteRefugio) { delante(g) } },
        ).forEach { (dx, dy, pieza) ->
            clipPath(fuera) {
                g.pano = 0f
                clipPath(panoRefugio(w, dx, dy, t(BANDAS)), androidx.compose.ui.graphics.ClipOp.Difference) { pieza() }
                porBandas(dx, dy, pieza)
            }
        }
        g.pano = null
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenRefugio(v, medidor, oscuro)
}

/**
 * Las medidas que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]) y cuántas repisas hay ([n]). Y la luz de cada punto.
 */
internal class MarcoRefugio(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val n: Int, val a: AireRefugio) {
    private val m = MuebleEstanteRefugio
    /** Donde pisa lo de arriba: la cara de arriba de la cornisa. */
    val apoyo: Float get() = w * m.APOYO
    /** La luz de un punto en píxeles, a [delante] anchos de la pared. */
    fun luz(p: Offset, delante: Float = 0f): LuzRefugio = luz(p.x, p.y, delante)
    fun luz(x: Float, y: Float, delante: Float = 0f): LuzRefugio {
        val l = m.luz(a, x / w, y / w, h / w, n, delante)
        return pano?.let { l.conPano(it) } ?: l
    }

    /** El paño de las luminarias forzado para todo lo que se pinte mientras no sea null (1 dentro del paño; ver [pintarConPano]). */
    var pano: Float? = null

    /**
     * Hacia dónde cae la sombra de lo que cuelga de la pared, en píxeles, por [largo]: de día, a la derecha y abajo (las
     * luminarias); al anochecer, más larga y más tendida; de noche, corta y hacia abajo.
     */
    fun arrojada(largo: Float): Offset = when {
        !a.noche && !a.oscuro -> Offset(0.014f, 0.011f)
        !a.oscuro -> Offset(0.026f, 0.014f)
        else -> Offset(0.004f, 0.008f)
    } * (w * largo)
    /** De qué lado le llega la luz a un punto en píxeles. */
    fun hacia(x: Float, y: Float): Float = m.hacia(a, x / w, y / w, h / w, n)
    /** Dónde arde cada luz, en píxeles. */
    val pantalla: Offset get() = Offset(w * m.PANTALLA_X, w * m.PANTALLA_Y)
    val guardia: Offset get() = Offset(w * m.GUARDIA_X, w * m.guardiaY(n))
    val cultivo: Offset get() = Offset(w * m.CULTIVO_X, h - w * m.CULTIVO_Y)
}

/**
 * El paño de luz de las luminarias bajadas (los Sueños en claro): entra de arriba a la izquierda, fuera del cuadro, y cae hacia la
 * derecha y abajo, cortado por la campana de la luminaria en un borde inclinado. En píxeles, corrido [dx] y [dy] anchos.
 */
internal fun panoRefugio(w: Float, dx: Float, dy: Float, t: Float = 0f): androidx.compose.ui.graphics.Path = androidx.compose.ui.graphics.Path().apply {
    fun p(x: Float, y: Float) = Offset((x + dx) * w, (y + dy) * w)
    // El borde inclinado, movido [t] (de -0.5, el núcleo, a 0.5, lo último de la penumbra): estrecho arriba, cerca de la
    // luminaria, y ancho abajo.
    val a = p(-0.1f, 0.035f); val b = p(0.43f + 0.012f * t, 0.035f); val c = p(0.74f + 0.042f * t, 0.62f); val d = p(-0.1f, 0.62f)
    moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); lineTo(d.x, d.y); close()
}

/**
 * Lo de arriba del estante, pintado una sola vez por tamaño y modo en una imagen que luego se pega (en dos ranuras: el diario y
 * los Sueños). La imagen llega hasta el píxel entero de abajo y se pega en un píxel entero: su última fila, que puede ser medio
 * píxel, deja ver lo que se pintó debajo (el pie de la cornisa), no el fondo.
 */
internal object CapaRefugio {
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    fun pegar(ds: DrawScope, ranura: Int, clave: String, alto: Float, pintar: DrawScope.() -> Unit) {
        val w = ceil(ds.size.width).toInt()
        val h = ceil(alto).toInt().coerceAtMost(ceil(ds.size.height).toInt())
        if (w < 1 || h < 1 || ranura !in 0..1) return
        val img = imagenes[ranura].takeIf { claves[ranura] == clave } ?: ImageBitmap(w, h).also { nueva ->
            CanvasDrawScope().draw(Density(ds.density, ds.fontScale), ds.layoutDirection, Canvas(nueva), Size(w.toFloat(), h.toFloat())) {
                clipRect(0f, 0f, ds.size.width, alto) { pintar() }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave
        }
        ds.drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), dstOffset = IntOffset.Zero, dstSize = IntSize(img.width, img.height))
    }
}

// ---------------------------------------------------------------- el ánimo

/** Las escalas del ánimo de Refugio, la de día y la de noche (bajo, medio, alto). */
private val TONOS_REFUGIO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.REFUGIO, false) to juegoTonos(TemaId.REFUGIO, true) }

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaRefugio(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val ex = t.red - a0.red - dr * f; val ey = t.green - a0.green - dg * f; val ez = t.blue - a0.blue - db * f
        val er = ex * ex + ey * ey + ez * ez
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia (el ánimo de en medio pasa de crema a casi
 * negro, y los extremos a ámbar y a fósforo); aquí la entrada es un objeto del cuarto: conserva su color de día y la luz lo apaga
 * con la distancia. Se ubica el color en la escala de noche y se toma el mismo punto en la de día; si no está, queda igual.
 */
internal fun tonoDeDiaRefugio(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_REFUGIO
    val x = enEscalaRefugio(t, noche) ?: return t
    return if (x < 0.5f) mezcla(dia[0], dia[1], x * 2) else mezcla(dia[1], dia[2], (x - 0.5f) * 2)
}

/**
 * El material del ánimo: se ubica el color (de día) en la escala del tema y se toma el mismo punto en [materiales] (vinilo,
 * esmalte o goma, de lo malo a lo bueno). Si el color no está en la escala, queda igual.
 */
internal fun materialDelAnimoRefugio(t: Color, materiales: List<Color>): Color {
    val x = enEscalaRefugio(t, TONOS_REFUGIO.first) ?: return t
    val c = if (x < 0.5f) porMatizRefugio(materiales[0], materiales[1], x * 2) else porMatizRefugio(materiales[1], materiales[2], (x - 0.5f) * 2)
    return vivoRefugio(c, 1f + 0.15f * (kotlin.math.abs(x - 0.5f) * 2.5f).coerceAtMost(1f))
}

/**
 * La mezcla de dos colores por su matiz, no por sus canales: de la mantequilla al verde azulado se pasa por el verde manzana, no por
 * el caqui; del coral a la mantequilla, por el naranja. Así los ánimos de en medio tienen el color limpio de la época.
 */
internal fun porMatizRefugio(a: Color, b: Color, f: Float): Color {
    val ha = hsvRefugio(a); val hb = hsvRefugio(b)
    var dh = hb[0] - ha[0]
    if (dh > 180f) dh -= 360f
    if (dh < -180f) dh += 360f
    val h = ((ha[0] + dh * f) % 360f + 360f) % 360f
    // A medio camino, algo menos de color: el verde manzana queda en pistacho y no chilla.
    val s = (ha[1] + (hb[1] - ha[1]) * f) * (1f - 0.34f * kotlin.math.sin(f * 3.1416f))
    val v = ha[2] + (hb[2] - ha[2]) * f
    return Color.hsv(h, s.coerceIn(0f, 1f), v.coerceIn(0f, 1f), mezclaAlfa(a, b, f))
}

private fun mezclaAlfa(a: Color, b: Color, f: Float) = (a.alpha + (b.alpha - a.alpha) * f).coerceIn(0f, 1f)

private fun hsvRefugio(c: Color): FloatArray {
    val mx = maxOf(c.red, c.green, c.blue); val mn = minOf(c.red, c.green, c.blue)
    val d = mx - mn
    val h = when {
        d < 1e-5f -> 0f
        mx == c.red -> 60f * (((c.green - c.blue) / d) % 6f)
        mx == c.green -> 60f * ((c.blue - c.red) / d + 2f)
        else -> 60f * ((c.red - c.green) / d + 4f)
    }
    return floatArrayOf((h + 360f) % 360f, if (mx < 1e-5f) 0f else d / mx, mx)
}

/** [c] con más color: se aleja de su gris (del mismo valor) en [f]; así los tonos de en medio no se lavan. */
internal fun vivoRefugio(c: Color, f: Float): Color {
    val g = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue
    return Color((g + (c.red - g) * f).coerceIn(0f, 1f), (g + (c.green - g) * f).coerceIn(0f, 1f), (g + (c.blue - g) * f).coerceIn(0f, 1f), c.alpha)
}
