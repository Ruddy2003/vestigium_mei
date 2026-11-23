package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
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
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * La paleta del cuarto del encargado del depósito de máquinas (Vapor victoriano, un pueblo ferroviario del norte de Inglaterra
 * hacia 1880): el enlucido pintado de azul de Prusia con su friso de plantilla en ocre y almagre; el pino tea barnizado de la
 * estantería, dorado y de veta muy marcada; el latón, el hierro, la hojalata y el plomo; el papel, la cartulina de las
 * etiquetas y la tinta; el granate y el bermellón de la maqueta; las baldosas del suelo; y las luces del cuarto. Dos
 * temperaturas: lo frío es la pared y la noche; lo cálido, la madera, el latón, el gas y la estufa.
 */
internal object PaletaVapor {
    /** El enlucido pintado de azul de Prusia: su cara, su luz y su hondo. */
    val AZUL = Color(0xFF34506B)
    val AZUL_CLARO = Color(0xFF557796)
    val AZUL_OSC = Color(0xFF1C2D41)
    /** La plantilla del friso: ocre y almagre. */
    val OCRE = Color(0xFFC79440)
    val ALMAGRE = Color(0xFF8E3626)
    /** El pino tea barnizado: su cara, su luz, su sombra y su veta resinosa. */
    val PINO = Color(0xFFB27A3E)
    val PINO_LUZ = Color(0xFFDDAA62)
    val PINO_OSC = Color(0xFF6C421F)
    val VETA = Color(0xFF5E2F12)
    /** La madera oscura de la moldura de colgar, de la peana y de las varillas de la lámina. */
    val NOGAL = Color(0xFF3E2618)
    val NOGAL_LUZ = Color(0xFF6E4A30)
    /** El latón, su luz y su sombra; el cobre. */
    val LATON = Color(0xFFC9A04A)
    val LATON_LUZ = Color(0xFFF0D488)
    val LATON_OSC = Color(0xFF7E5E22)
    val COBRE = Color(0xFFB0663A)
    /** El hierro (la ventana, la estufa, los rieles), su luz; la hojalata y el plomo. */
    val HIERRO = Color(0xFF2B2C33)
    val HIERRO_LUZ = Color(0xFF6C6F79)
    val HOJALATA = Color(0xFFB9BBB4)
    val PLOMO = Color(0xFF7D8086)
    /** El papel, la cartulina de las etiquetas de paquete, la loza y la tinta. */
    val PAPEL = Color(0xFFF1E7CC)
    val PAPEL_VIEJO = Color(0xFFDCCBA2)
    val CARTULINA = Color(0xFFDCBE82)
    val LOZA = Color(0xFFF4F1E8)
    val TINTA = Color(0xFF201A16)
    /** El granate de la maqueta, el bermellón de sus topes, el negro de la caja de humos, el amarillo de los filetes. */
    val GRANATE = Color(0xFF7C2229)
    val BERMELLON = Color(0xFFC8432B)
    val NEGRO = Color(0xFF1E1C1E)
    val FILETE = Color(0xFFE0B54A)
    /** Las baldosas del suelo: almagre, negro y ante. */
    val BARRO = Color(0xFF9A4A30)
    val BARRO_OSC = Color(0xFF2B2422)
    val ANTE = Color(0xFFCDB68C)
    /** El vidrio tiznado de la ventana, de día. */
    val VIDRIO = Color(0xFFD3D8CF)
    val TIZNE = Color(0xFF4A4A46)
    /** La lona sin teñir, el cuero sin teñir y el verde de la bandera del guarda. */
    val LONA = Color(0xFFC6B48C)
    val CUERO = Color(0xFF6A4228)
    val VERDE = Color(0xFF2E6A48)

    // Las luces: la noche del cuarto (azul de hollín), la de los Sueños, el anochecer de los Sueños en claro y su ocaso, la
    // llama del gas y su corazón, la brasa de la estufa y la luna.
    val NOCHE = Color(0xFF0D1220)
    val NOCHE_SUENOS = Color(0xFF080D28)
    val ATARDECER = Color(0xFF4E3E70)
    val OCASO = Color(0xFFF2A270)
    val GAS = Color(0xFFFFC870)
    val GAS_CORAZON = Color(0xFFFFF3D2)
    val BRASA = Color(0xFFF0692E)
    val LUNA = Color(0xFFC4D3F2)

    val LETRA: FontFamily = LetraExtra.caldera
}

/**
 * El aire del momento en el cuarto: de día (la luz lechosa de la ventana tiznada, arriba a la izquierda), de noche (el modo
 * oscuro: arde el mechero de gas de la pared, arriba a la derecha, y al pie la estufa con su mica encendida) y en los Sueños
 * ([noche]: el anochecer en claro, con el ocaso en los vidrios y el gas ya encendido; la noche honda en oscuro, [lunar], con el
 * gas bajo y la luna detrás de un vidrio). Como en el Ideario del tema, de noche cada cosa va de su color hondo (oscuro, de su
 * mismo tono, con el azul de hollín) a su color según lo que le llega de las luces ([k], de 0 a 1), teñida de llama (o de
 * brasa, en la parte [br] que viene de la estufa). De día, [k] es la luz de la ventana: aclara lo cercano y apaga un poco lo
 * lejano, sin velo.
 */
internal class AireVapor(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaVapor
    /** Si arde el gas. */
    val lumbre: Boolean get() = oscuro || noche
    /** Los Sueños de noche: el gas está bajo y la luna entra por la ventana. */
    val lunar: Boolean get() = oscuro && noche
    /** Los Sueños en claro: anochece. */
    val ocaso: Boolean get() = noche && !oscuro
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else SOMBRA_MUEBLE

    /** Lo hondo de un color de noche: oscuro, de su mismo tono y con el azul de la noche (no un gris). */
    private fun abismo(c: Color): Color { val n = hondo; return Color(c.red * 0.13f + n.red * 0.78f, c.green * 0.14f + n.green * 0.8f, c.blue * 0.2f + n.blue * 0.85f, c.alpha) }
    private fun calor(br: Float) = if (br > 0.02f) mezcla(p.GAS, p.BRASA, br) else p.GAS

    fun luz(c: Color, k: Float, f: Float = 0f, br: Float = 0f): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) {
            if (noche) {
                // El anochecer: lo que está lejos de la ventana se va al violeta y se apaga; lo de cerca recibe el ocaso;
                // cerca del gas, su charco cálido.
                val ff = f.coerceIn(0f, 1f)
                val v = mezcla(mezcla(c, p.ATARDECER, 0.16f + 0.3f * (1f - ff)), p.OCASO, 0.18f * ff)
                return mezcla(v, p.GAS, 0.26f * kk)
            }
            return if (kk > 0.5f) mezcla(c, LUZ_MUEBLE, 0.24f * (kk - 0.5f) * 2f) else mezcla(c, SOMBRA_MUEBLE, 0.16f * (0.5f - kk) * 2f)
        }
        var v = mezcla(abismo(c), c, (1.05f * kk).coerceAtMost(0.92f))
        v = mezcla(v, calor(br), 0.2f * kk)
        if (lunar && f > 0.01f) v = mezcla(v, mezcla(v, p.LUNA, 0.35f), (0.3f * f * (1f - kk)).coerceIn(0f, 1f))
        return v
    }

    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(if (noche) a * 1.15f else a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama (o de la brasa) según lo que le llega. */
    fun brillo(a: Float, k: Float, br: Float = 0f): Color = when {
        !oscuro -> luzMueble(if (noche) a * 0.55f else a)
        else -> calor(br).copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))
    }

    /** El roce frío de la luna en lo que mira a la ventana (los Sueños de noche); si no, nada. */
    fun helado(a: Float): Color = p.LUNA.copy(alpha = if (lunar) a.coerceIn(0f, 1f) else 0f)

    /** El color del ánimo: de noche se hunde menos que lo demás, para que se siga adivinando lejos de las luces. */
    fun animo(t: Color, k: Float, f: Float = 0f, br: Float = 0f): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) {
            if (noche) { val ff = f.coerceIn(0f, 1f); return mezcla(mezcla(mezcla(t, p.ATARDECER, 0.1f + 0.18f * (1f - ff)), p.OCASO, 0.1f * ff), p.GAS, 0.14f * kk) }
            return if (kk > 0.5f) mezcla(t, LUZ_MUEBLE, 0.12f * (kk - 0.5f) * 2f) else mezcla(t, SOMBRA_MUEBLE, 0.1f * (0.5f - kk) * 2f)
        }
        val n = hondo
        val suave = Color(t.red * 0.22f + n.red * 0.7f, t.green * 0.22f + n.green * 0.7f, t.blue * 0.22f + n.blue * 0.7f, t.alpha)
        var v = mezcla(suave, t, (0.06f + 0.98f * kk).coerceAtMost(0.95f))
        v = mezcla(v, calor(br), 0.13f * kk)
        if (lunar && f > 0.01f) v = mezcla(v, mezcla(v, p.LUNA, 0.42f), (0.7f * f * (1f - kk)).coerceIn(0f, 1f))
        return v
    }

    /** La cartulina donde va el día: casi no se apaga, para que el número se lea siempre (contraste de 4.5 a 1 o más). */
    fun etiqueta(k: Float, base: Color = p.PAPEL): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, p.ATARDECER, 0.1f) else base
        val ke = maxOf(kk, 0.26f)
        return mezcla(mezcla(base, hondo, 0.46f - 0.42f * ke), p.GAS, 0.14f * ke)
    }

    val tinta: Color get() = if (oscuro) mezcla(p.TINTA, hondo, 0.25f) else p.TINTA

    /** Lo que oscurece una superficie (la veta, el polvo, el tizne): de día, su color; de noche, el hondo de la noche. */
    fun grano(c: Color, alfa: Float): Color = if (oscuro) hondo.copy(alpha = alfa.coerceIn(0f, 1f)) else c.copy(alpha = alfa.coerceIn(0f, 1f))
}

/**
 * El dibujo del estante del diario de Vapor victoriano (lo usa `EstanteVapor`, en `BibliotecaContemporanea.kt`): la estantería
 * del cuarto del encargado del depósito de máquinas de un pueblo ferroviario del norte de Inglaterra hacia 1880, donde de noche
 * estudian los fogoneros que quieren ser maquinistas. Es de pino tea barnizado, hecha por el carpintero de la compañía con el
 * gusto de esos años: la cornisa volada con su moldura, el friso con acanaladuras y rosetas incisas, los montantes gruesos con
 * chaflanes de remate en punta, las tablas gruesas de canto boleado con el portaetiquetas de latón del mes y el zócalo con sus
 * golpes. Es abierta por detrás: el fondo de cada hueco es la pared, el enlucido azul de Prusia en sombra. Arriba, la pared con
 * su friso de plantilla y la moldura de colgar, la ventana de fundición de vidrios tiznados con su persiana (la luz del día), la
 * lámina de las señales colgada de su cordón, el tablero de las fichas de latón, el caño y el mechero de gas (la luz de la
 * noche) y, encima del mueble, un bodegón: la maqueta de locomotora sobre su tramo de vía, los manuales, el tintero, el atril
 * con el manual abierto, la taza de té sobre los libros y la campana de mano. Al pie, el suelo de baldosas geométricas, la
 * estufa de hierro con su mica (la segunda luz de la noche) y el paragüero con los planos y las banderas del guarda. Lo fijo
 * (la corona y el suelo) se pinta una vez por tamaño y modo ([CapaVapor]); lo demás, en cada dibujo. La pared y lo colgado
 * están en `MuebleEstanteVaporMuro.kt`; el mueble, en `MuebleEstanteVaporMueble.kt`; la maqueta, en
 * `MuebleEstanteVaporMaqueta.kt`; lo de encima y del pie, en `MuebleEstanteVaporArriba.kt`; lo de los huecos libres, en
 * `MuebleEstanteVaporHuecos.kt`; las entradas, en `MuebleEstanteVaporVolumenes.kt`. Distinto del Ideario, que es el casillero
 * de caoba de la caseta de los maquinistas contra el ladrillo y la tabla verde, con el farol de señales.
 */
internal object MuebleEstanteVapor {
    val LETRA: FontFamily get() = PaletaVapor.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteVapor` las lee al dibujar)

    val CORONA = 0.56f
    val BASE = 0.28f
    val LADO = 0.075f
    val ALTO_REPISA: Dp = 110.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el libro de registro; 1, las cajas de hojalata lacada; 2, la saca de lona. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 71)
        return if (x < 0.42f) 0 else if (x < 0.72f) 1 else 2
    }

    /** El libro, el más alto; las cajas apiladas; la saca, asentada y más llena cuanto más escribiste. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.07f * azar(semilla, 1)
        1 -> 0.82f + 0.06f * azar(semilla, 1)
        else -> 0.8f + 0.04f * (palabras / 800f).coerceIn(0f, 1f) + 0.03f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- las luces (en anchos del mueble)

    /** El centro de la ventana: de día, la luz del cuarto; en los Sueños, el ocaso o la luna. */
    const val VENTANA_X = 0.13f
    const val VENTANA_Y = 0.22f
    /** La llama del mechero de gas, en su globo. */
    const val GAS_X = 0.87f
    const val GAS_Y = 0.3f
    /** La mica de la estufa: su x y lo que baja desde el pie de la última tabla. */
    const val ESTUFA_X = 0.085f
    const val ESTUFA_DY = 0.18f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 130f
    /** En la corona: el borde de delante de la tapa del mueble (donde se apoya lo de encima), en anchos. */
    val APOYO: Float get() = CORONA - 0.125f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireVapor(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoVapor(w, h, ch, d.izquierda.right, d.derecha.left, fondo, fila, n, a)

        // ---- debajo de todo el cuerpo, el azul hondo del fondo: lo que asome en una junta a medio píxel es fondo de hueco.
        val y0 = floor(ch); val y1 = minOf(h, ceil(fondo) + 1f)
        if (y1 > y0) drawRect(a.luz(mezcla(PaletaVapor.AZUL_OSC, PaletaVapor.NOCHE, 0.3f), 0.2f), Offset(0f, y0), Size(w, y1 - y0))

        // ---- lo fijo de arriba (la pared, la ventana, lo colgado, la cornisa y el bodegón) y el suelo de baldosas, pintados una
        // vez por tamaño y modo, en píxeles enteros: la corona desde arriba hasta el píxel entero de debajo de su borde; el suelo
        // desde el píxel entero de encima del zócalo (lo que sobra lo tapa el zócalo, que va después). La corona no cambia con las
        // repisas; el suelo, sí.
        val modo = "${w.toInt()}|$oscuro|$noche|$u"
        val ranura = if (noche) 2 else 0
        with(CapaVapor) {
            pegar(ranura, "a$modo|$ch", 0f, minOf(h, ceil(ch))) {
                // Debajo de todo, opaco: lo que dos franjas dejen a medio píxel no deja ver el fondo de la pantalla.
                drawRect(a.luz(PaletaVapor.PINO_OSC, 0.3f), Offset(0f, g.fondoTapa - 2f), Size(w, size.height - g.fondoTapa + 2f))
                drawRect(a.luz(PaletaVapor.AZUL, 0.3f), Offset.Zero, Size(w, g.fondoTapa))
                with(MuroEstanteVapor) { coronaVapor(a, g) }
                with(ArribaEstanteVapor) { bodegonVapor(a, g) }
                granoMueble(Rect(0f, 0f, w, ch), oscuro)
            }
            val arriba = floor(g.zocalo)
            if (h - arriba >= 1f) pegar(ranura + 1, "b$modo|$fondo|$h", arriba, h) {
                drawRect(a.luz(PaletaVapor.BARRO_OSC, 0.2f), Offset(0f, arriba), Size(w, h - arriba))
                with(ArribaEstanteVapor) { sueloVapor(a, g) }
                granoMueble(Rect(0f, arriba, w, h), oscuro)
            }
        }
        if (fondo - ch >= 1f) cuerpo(d, medidor, a, g)
        // ---- el zócalo, la estufa y el paragüero, que se pintan en cada dibujo encima del suelo guardado
        with(ArribaEstanteVapor) { pieVivoVapor(a, g); paraguero(a, g) }
        granoMueble(Rect(0f, ch, w, g.zocalo), oscuro)
        // ---- de noche, el resplandor de la estufa en lo bajo del mueble
        if (oscuro && fondo - ch >= 1f) {
            val e = Offset(g.estufa.x * w, g.estufa.y * w)
            clipRect(0f, ch, w, fondo) { mancha(e, w * 0.55f, PaletaVapor.BRASA.copy(alpha = if (noche) 0.1f else 0.16f)) }
        }
    }

    /** El cuerpo, entre la corona y el zócalo: el fondo de cada hueco, las tablas, los montantes, los portaetiquetas y los huecos. */
    private fun DrawScope.cuerpo(d: DisposicionEstante, medidor: TextMeasurer, a: AireVapor, g: MarcoVapor) {
        with(MuebleCuerpoVapor) {
            d.repisas.forEachIndexed { k, r -> huecoVapor(k, r, a, g) }
            d.tablas.forEachIndexed { k, r -> tablaVapor(k, r, a, g) }
            montantesVapor(a, g, d.tablas)
            d.placas.forEach { p -> portaetiquetasVapor(p, medidor, a, g.cerca(p.r.center), g.brasa(p.r.center)) }
        }
        // ---- lo que hay en los huecos libres
        with(HuecosEstanteVapor) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                val c = Offset((oc + r.right) / 2, r.center.y)
                huecoLibreVapor(k, r, oc, a, g, g.cerca(c), g.frio(c), g.brasa(c))
            }
        }
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = with(VolumenEstanteVapor) { volumenVapor(v, medidor, oscuro) }
}

/**
 * Las medidas del cuarto que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona
 * [ch], el borde de dentro de los montantes ([x0], [x1]), el pie de la última tabla ([fondo]), una repisa con su tabla ([fila])
 * y cuántas repisas hay ([n]). Y la luz que llega a cada punto: de las luces cálidas ([cerca]; de día, de la ventana), la parte
 * de la estufa ([brasa]) y la fría de la ventana en los Sueños ([frio]).
 */
internal class MarcoVapor(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val fila: Float, val n: Int,
                          val a: AireVapor) {
    /** Los bordes de fuera del mueble y la línea donde se apoya lo de encima (el canto de delante de la tapa). */
    val xa: Float get() = w * 0.012f
    val xb: Float get() = w * 0.988f
    val apoyo: Float get() = w * MuebleEstanteVapor.APOYO
    /** Lo que se ve de la tapa desde un poco arriba: de [fondoTapa] (contra la pared) a [apoyo]. */
    val fondoTapa: Float get() = apoyo - w * 0.022f
    /** El zócalo y la línea del suelo con la pared. */
    val zocalo: Float get() = fondo + w * 0.085f
    /** Dónde está la mica de la estufa, en anchos del mueble. */
    val estufa: Offset get() = Offset(MuebleEstanteVapor.ESTUFA_X, fondo / w + MuebleEstanteVapor.ESTUFA_DY)

    fun cerca(p: Offset): Float = cercaVapor(p.x / w, p.y / w, a, estufa)
    fun brasa(p: Offset): Float = brasaVapor(p.x / w, p.y / w, a, estufa)
    fun frio(p: Offset): Float = if (a.lunar) lunaVapor(p.x / w, p.y / w) else if (a.ocaso) ventanaVapor(p.x / w, p.y / w) else 0f
}

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/**
 * Lo que llega del gas a un punto ([fx], [fy] en anchos del mueble): alrededor del mechero cae con la distancia (la pared, la
 * tapa y lo de encima); sobre el frente del mueble, por repisas, como la caída del farol en el Ideario: la primera encendida,
 * la segunda todavía clara, la tercera a media luz y las de abajo casi perdidas; un poco menos lejos del mechero, a lo ancho.
 * Con [bajo] (los Sueños), el gas está bajo: menos luz y un charco más corto.
 */
internal fun gasVapor(fx: Float, fy: Float, bajo: Boolean): Float {
    val dx = fx - MuebleEstanteVapor.GAS_X
    val dy = (fy - MuebleEstanteVapor.GAS_Y) * (if (fy > MuebleEstanteVapor.GAS_Y) 0.85f else 1.1f)
    val radial = (if (bajo) 1f else 1.5f) * exp(-(if (bajo) 3.9f else 2.9f) * sqrt(dx * dx + dy * dy))
    val y = fy - MuebleEstanteVapor.CORONA
    var cuerpo = 0f
    if (y >= 0f) {
        cuerpo = CAIDA_VAPOR.last().second
        for (i in 0 until CAIDA_VAPOR.size - 1) {
            val (y0, k0) = CAIDA_VAPOR[i]; val (y1, k1) = CAIDA_VAPOR[i + 1]
            if (y in y0..y1) { cuerpo = k0 + (k1 - k0) * (y - y0) / (y1 - y0); break }
        }
        cuerpo *= (1f - 0.45f * (kotlin.math.abs(dx) - 0.1f).coerceAtLeast(0f)).coerceIn(0.66f, 1f)
        if (bajo) cuerpo *= 0.45f
    }
    return maxOf(radial, cuerpo).coerceIn(0f, 1f)
}

/** La caída del gas sobre el frente del mueble: (anchos por debajo de la corona, luz). Los centros de las repisas, a 0.19, 0.53… */
private val CAIDA_VAPOR = listOf(0f to 0.97f, 0.19f to 0.88f, 0.53f to 0.56f, 0.87f to 0.25f, 1.21f to 0.085f, 1.55f to 0.035f, 2.3f to 0.02f)

/** El rescoldo de la estufa en [e]: rojo y cerca. */
internal fun estufaVapor(fx: Float, fy: Float, e: Offset, bajo: Boolean): Float {
    val dx = fx - e.x; val dy = (fy - e.y) * 0.9f
    return ((if (bajo) 0.6f else 0.95f) * exp(-5f * sqrt(dx * dx + dy * dy))).coerceIn(0f, 1f)
}

/** La luz lechosa de la ventana (de día; en los Sueños, el ocaso o la luna). */
internal fun ventanaVapor(fx: Float, fy: Float): Float {
    val dx = fx - MuebleEstanteVapor.VENTANA_X; val dy = fy - MuebleEstanteVapor.VENTANA_Y
    return (1.3f * exp(-1.8f * sqrt(dx * dx * 0.9f + dy * dy))).coerceIn(0f, 1f)
}

/** La luna que entra por la ventana en los Sueños de noche: fría y corta, solo cerca de ella. */
internal fun lunaVapor(fx: Float, fy: Float): Float {
    val dx = fx - MuebleEstanteVapor.VENTANA_X; val dy = fy - MuebleEstanteVapor.VENTANA_Y
    return (1.1f * exp(-3.2f * sqrt(dx * dx + dy * dy))).coerceIn(0f, 1f)
}

/** Lo que alumbra un punto: de día, la ventana; de noche, el gas y la estufa; al anochecer (los Sueños en claro), el gas bajo. */
internal fun cercaVapor(fx: Float, fy: Float, a: AireVapor, e: Offset): Float = when {
    !a.lumbre -> ventanaVapor(fx, fy)
    a.ocaso -> (gasVapor(fx, fy, true) * 0.85f + estufaVapor(fx, fy, e, true) * 0.6f).coerceIn(0f, 1f)
    else -> (gasVapor(fx, fy, a.noche) + estufaVapor(fx, fy, e, a.noche)).coerceIn(0.02f, 1f)
}

/** La parte de la luz que viene de la estufa (para teñirla de brasa), de 0 a 1. */
internal fun brasaVapor(fx: Float, fy: Float, a: AireVapor, e: Offset): Float {
    if (!a.lumbre) return 0f
    val s = estufaVapor(fx, fy, e, a.noche); val g = gasVapor(fx, fy, a.noche)
    return if (s + g < 1e-4f) 0f else s / (s + g)
}

/** Para un volumen: dónde cae en anchos del mueble ([repisa] desde arriba y su centro [centro] de 0 a 1). */
internal fun lugarVolumenVapor(repisa: Int, centro: Float): Offset {
    val fx = MuebleEstanteVapor.LADO + centro * (1f - 2 * MuebleEstanteVapor.LADO)
    val fy = MuebleEstanteVapor.CORONA + (repisa + 0.55f) * MuebleEstanteVapor.FILA / MuebleEstanteVapor.ANCHO_NOMINAL
    return Offset(fx, fy)
}

/** Dónde está la estufa para un estante de [repisas] repisas, en anchos (lo que un volumen puede saber). */
internal fun estufaNominalVapor(repisas: Int): Offset =
    Offset(MuebleEstanteVapor.ESTUFA_X, MuebleEstanteVapor.CORONA + repisas * MuebleEstanteVapor.FILA / MuebleEstanteVapor.ANCHO_NOMINAL + MuebleEstanteVapor.ESTUFA_DY)

// ---------------------------------------------------------------- lo fijo pintado una vez

/**
 * Lo fijo pintado una vez: cuatro imágenes (la corona y el suelo, del diario y de los Sueños), cada una con la clave de su
 * tamaño y su modo; si la clave cambia, se vuelve a pintar. Cada franja va de una fila entera de píxeles a otra ([desde],
 * [hasta]) y se pega sin correrla, así no se remuestrea ni deja costura. Con un teléfono de 1080 px, unos 3 MB por modo (la
 * corona, ~2.2 MB; el suelo, ~0.8 MB).
 */
internal object CapaVapor {
    private val claves = arrayOfNulls<String>(4)
    private val imagenes = arrayOfNulls<ImageBitmap>(4)

    fun DrawScope.pegar(ranura: Int, clave: String, desde: Float, hasta: Float, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = (hasta - desde).toInt()
        if (ancho < 1 || alto < 1 || ranura !in 0..3) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                translate(0f, -desde) { clipRect(0f, desde, size.width, desde + alto) { pintar() } }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave; img = nueva
        }
        drawImage(img, Offset(0f, desde))
    }
}

// ---------------------------------------------------------------- ayudantes de forma

internal fun poligonoVapor(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

internal fun corridoVapor(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/** La fila de la junta en [y], de [x0] a [x1], de [c], debajo de las dos piezas que se tocan ahí; si [y] es entero, nada. */
internal fun DrawScope.filaJuntaVapor(y: Float, x0: Float, x1: Float, c: Color) {
    val f = floor(y)
    if (f == y) return
    drawRect(c, Offset(x0, f), Size(x1 - x0, 1f))
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la sombra ([lado]: 1 a la derecha, -1 a la izquierda). */
internal fun DrawScope.contactoVapor(centro: Offset, ancho: Float, alto: Float, a: AireVapor, fuerza: Float = 0.34f, lado: Float = 1f) =
    drawOval(a.sombra(fuerza), Offset(centro.x - ancho * (if (lado >= 0f) 0.42f else 0.58f), centro.y - alto / 2), Size(ancho, alto))
