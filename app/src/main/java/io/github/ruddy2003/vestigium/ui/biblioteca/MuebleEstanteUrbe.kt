package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ALMAGRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ANOCHECER
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.AZUCAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CREMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CREPUSCULO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LAMPARA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOCHE_SUENOS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOGAL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.OCASO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.SOL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.YESO
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

// =====================================================================
//  Urbe: la librería del piso de Giulia
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
        return mezcla(mezcla(CREMA, hondo, 0.4f - 0.32f * k), foco, 0.14f * k)
    }
    /** La tinta de lo escrito. */
    val tinta: Color get() = if (oscuro) mezcla(TINTA, hondo, 0.3f) else TINTA
    /**
     * La luz que dibuja la ventana en el mueble, que se pinta en modo pantalla (aclara y calienta sin velar ni borrar el color
     * de lo que toca): el sol de día y la luna en los Sueños en oscuro. Al anochecer la luz es difusa y de noche no entra.
     */
    val rayo: Color? get() = when {
        luna -> Color(0xFF7E96D0).copy(alpha = 0.46f)
        lumbre -> null
        else -> Color(0xFFFFC070).copy(alpha = 0.52f)
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

/**
 * El dibujo del estante del diario de Urbe (lo usa `EstanteUrbe`, en `BibliotecaContemporanea.kt`): la librería del piso de
 * Giulia, la restauradora de frescos, en un edificio viejo de Roma. Una librería de nogal ya sin barniz en los cantos, con su
 * cornisa, los costados gruesos, el fondo de tablillas machihembradas pintado de azul «papel de azúcar» y las baldas combadas
 * por el peso; abajo, el mueble bajo de dos puertas sobre el suelo de baldosas hidráulicas. Detrás, el muro de estuco de
 * almagra desconchado, con el ocre de antes y el enlucido asomando. Arriba a la izquierda, la ventana honda del muro grueso,
 * con su marco blanco y su persiana verde: la hoja izquierda cerrada, la derecha abierta a los tejados. De día es la luz: el
 * sol entra sesgado y deja en la cornisa y en la primera repisa la forma de la ventana, rayada por las lamas. De noche alumbra
 * la lámpara de pantalla plisada de la cornisa, a la derecha, y por la ventana se ven las ventanas encendidas y las antenas.
 * En los Sueños, en claro, el anochecer rosa por la ventana con la lámpara recién encendida; en oscuro, la luna, que dibuja
 * la ventana en frío, con la lámpara apagada. Las entradas son el libro de bolsillo, la caja de fotos forrada de papel y la
 * carpeta de cartón con su goma, todas del color del ánimo. Todo el dibujo sale de [estante] y de [volumen]; la luz de los
 * dos sale de las mismas curvas ([cerca]) y del mismo dibujo de la ventana ([RAYOS]). La pared, la ventana, los objetos y el
 * pie, en `MuebleEstanteUrbePiezas.kt` y `MuebleEstanteUrbeObjetos.kt`; lo de los huecos libres, en `MuebleEstanteUrbeHuecos.kt`, y las
 * entradas, en `MuebleEstanteUrbeVolumenes.kt`.
 */
internal object MuebleEstanteUrbe {
    internal val LETRA: FontFamily = Letra.placa

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.6f
    const val BASE = 0.27f
    const val LADO = 0.062f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 14.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (26 + (palabras / 36).coerceAtMost(20)).dp

    /** Cuál de los tres es una entrada: 0, el libro de bolsillo; 1, la caja de fotos; 2, la carpeta con su goma. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** El libro, mediano; la caja, la más baja y ancha de hombros; la carpeta, la más alta (con los papeles que asoman). */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.79f + 0.07f * azar(semilla, 1)
        1 -> 0.66f + 0.06f * azar(semilla, 1)
        else -> 0.84f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    internal const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    internal const val FILA = 126f
    /** El costado de la librería: desde dónde, en anchos (antes, el muro). */
    internal const val COSTADO = 0.014f

    /** El hueco de la ventana con vidrio (dentro del marco), en anchos desde la esquina de arriba a la izquierda. */
    internal const val VEN_IZQ = 0.095f
    internal const val VEN_DER = 0.305f
    internal const val VEN_ARR = 0.085f
    internal const val VEN_ABA = 0.435f
    /** El montante del medio y el travesaño de las hojas, en anchos. */
    internal const val VEN_MEDIO = (VEN_IZQ + VEN_DER) / 2
    internal const val VEN_TRAVESANO = VEN_ARR + (VEN_ABA - VEN_ARR) * 0.36f
    /** Cuántas lamas tiene la hoja cerrada de la persiana. */
    internal const val LAMAS = 13
    /** Cuánto se corre la forma de la ventana al caer en el mueble: el sol entra alto y desde la izquierda. */
    internal const val RAYO_DX = 0.105f
    internal const val RAYO_DY = 0.47f

    /** Dónde está la bombilla de la lámpara (el medio de la pantalla), en anchos. */
    internal const val LAMP_X = 0.8f
    internal const val LAMP_Y = 0.432f
    /** Dónde manda la luna en los Sueños: el medio de la ventana dibujada en el mueble. */
    internal const val LUNA_X = (VEN_IZQ + VEN_DER) / 2 + RAYO_DX
    internal const val LUNA_Y = (VEN_ARR + VEN_ABA) / 2 + RAYO_DY

    /**
     * Los trozos de luz que la ventana deja en el mueble, en anchos: la hoja abierta (dos paños, partidos por la sombra del
     * travesaño) y, en la hoja cerrada, la luz que pasa entre las lamas. Es la ventana corrida ([RAYO_DX], [RAYO_DY]): sobre un
     * plano paralelo al muro, la sombra de algo plano no se deforma. Se calcula una sola vez y la usan el mueble y cada volumen.
     */
    internal val RAYOS: List<Rect> = buildList {
        val alto = VEN_ABA - VEN_ARR
        val medio = 0.008f
        add(Rect(VEN_MEDIO + medio, VEN_ARR, VEN_DER, VEN_TRAVESANO - 0.004f))
        add(Rect(VEN_MEDIO + medio, VEN_TRAVESANO + 0.004f, VEN_DER, VEN_ABA))
        val paso = alto / LAMAS
        for (i in 0 until LAMAS) add(Rect(VEN_IZQ, VEN_ARR + paso * (i + 0.56f), VEN_MEDIO - medio, VEN_ARR + paso * (i + 1)))
    }.map { it.translate(RAYO_DX, RAYO_DY) }

    /** Lo que ocupa toda la forma de la ventana en el mueble, en anchos. */
    internal val RAYO_TODO = Rect(VEN_IZQ + RAYO_DX, VEN_ARR + RAYO_DY, VEN_DER + RAYO_DX, VEN_ABA + RAYO_DY)

    /**
     * Cuánto alumbra la lámpara (de 1 a 0) un punto que está a [dx] y [dy] de la bombilla, en anchos del mueble. En un estante
     * corto la luz se acaba antes, para que la última repisa quede siempre en penumbra; de cinco repisas en adelante no crece.
     */
    fun alcanceLampara(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.8f + 0.1f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx * 0.85f + dy * dy) / radio).coerceIn(0f, 1f).pow(1.3f)
    }

    /** Cuánto alumbra la luna, desde la forma de la ventana en el mueble: llega menos lejos y más fría. */
    fun alcanceLuna(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.7f + 0.08f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx * 0.75f + dy * dy) / radio).coerceIn(0f, 1f).pow(1.4f)
    }

    /**
     * La luz que llega a un punto en anchos ([fx], [fy]): de la luna en los Sueños en oscuro; al anochecer, de la lámpara o de la
     * ventana, la que llegue más; de noche, de la lámpara.
     */
    fun cerca(fx: Float, fy: Float, repisas: Int, a: AireUrbe): Float = when {
        a.luna -> alcanceLuna(fx - LUNA_X, fy - LUNA_Y, repisas)
        a.ocaso -> maxOf(alcanceLampara(fx - LAMP_X, fy - LAMP_Y, repisas), 0.8f * alcanceLuna(fx - LUNA_X, fy - LUNA_Y, repisas))
        else -> alcanceLampara(fx - LAMP_X, fy - LAMP_Y, repisas)
    }

    /** Dónde cae en anchos el pie de la repisa [repisa]. */
    fun pieRepisa(repisa: Int): Float = CORONA + (repisa * FILA + ALTO_REPISA.value) / ANCHO_NOMINAL

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w <= 1f || h <= 1f) return
        val a = AireUrbe(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        /** De la altura en el lienzo a anchos: arriba, tal cual; en el cuerpo, por repisas, como lo cuentan los volúmenes. */
        fun fy(y: Float): Float = if (y <= ch) y / w else CORONA + (y - ch) / fila * FILA / ANCHO_NOMINAL
        /** Y al revés. */
        fun py(f: Float): Float = if (f <= CORONA) f * w else ch + (f - CORONA) * ANCHO_NOMINAL / FILA * fila
        fun cerca(p: Offset): Float = if (!a.lumbre) 1f else cerca(p.x / w, fy(p.y), n, a)
        val gT = w * 0.05f               // el alto de la cornisa
        val fT = w * 0.011f              // lo que se ve de su cara de arriba
        val apoyo = ch - gT - fT * 0.4f
        val suelo = d.base.top + d.base.height * 0.68f
        val costado = w * COSTADO
        val lado = w * LADO

        paredUrbe(a, ch, suelo, { p -> cerca(p) })
        ventanaUrbe(a, { p -> cerca(p) })
        cableadoUrbe(a, apoyo, { p -> cerca(p) })

        // ---- la sombra de la librería en el muro, a su derecha
        drawRect(a.sombra(if (a.lumbre) 0.42f else 0.3f), Offset(w - costado, ch - gT * 0.4f), Size(costado, suelo - ch + gT * 0.4f))

        // ---- el fondo de cada casillero: las tablillas azules, quietas, con la sombra de la balda de arriba
        d.repisas.forEachIndexed { k, r -> casillero(k, r, d.ocupado.getOrNull(k), a, { p -> cerca(p) }) }

        // ---- los costados, las baldas combadas con su rótulo y lo de los huecos libres
        costados(d, a, ch - gT, d.base.top, n, fila, { y -> fy(y) })
        val combas = d.tablas.mapIndexed { k, _ -> comba(k, d.ocupado.getOrNull(k), d.repisas.getOrNull(k)) * u }
        d.tablas.forEachIndexed { k, r -> balda(r, combas[k], k, a, { p -> cerca(p) }) }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoUrbe(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)), if (a.luna) -1f else 1f)
        }
        d.placas.forEachIndexed { i, p ->
            val k = d.tablas.indexOfFirst { it.top <= p.r.center.y && it.bottom >= p.r.center.y }.coerceAtLeast(0)
            rotulo(p, combas.getOrElse(k) { 0f } * 0.9f, medidor, a, cerca(p.r.center), i)
        }

        // ---- la cornisa, con su sombra sobre la primera repisa
        cornisa(Rect(0f, ch - gT, w, ch), fT, a, { p -> cerca(p) })

        // ---- lo que hay sobre la cornisa, de atrás adelante
        cornisaUrbe(apoyo, ch, a, { p -> cerca(p) })

        // ---- al pie: el mueble bajo, el suelo y lo que hay en él
        pieUrbe(d, suelo, a, { p -> cerca(p) })

        // ---- la luz de la ventana en el mueble y su haz en el aire
        a.rayo?.let { c -> rayoUrbe(c, a, { f -> py(f) }, ch - gT - fT) }

        // ---- lo que alumbra por sí mismo, encima de todo
        if (a.lampara) brilloLamparaUrbe(a, apoyo)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    /** Cuánto se comba la balda [k], en dp: más cuanto más cargada (lo ocupado de la repisa de encima) y un poco al azar. */
    private fun comba(k: Int, oc: Float?, r: Rect?): Float {
        val carga = if (oc != null && r != null && r.width > 1f) ((oc - r.left) / r.width).coerceIn(0f, 1f) else 0.3f
        return (0.8f + 2.6f * carga) * (0.75f + 0.5f * azar(431, k))
    }

    /**
     * El fondo de un casillero: las tablillas machihembradas pintadas de azul, quietas (para que los volúmenes resalten), con la
     * sombra de la balda de arriba, la del costado izquierdo y la del último volumen. De noche se pinta por tramos, cada uno con
     * la luz que le llega.
     */
    private fun DrawScope.casillero(k: Int, r: Rect, oc: Float?, a: AireUrbe, cerca: (Offset) -> Float) {
        val u = density
        clipRect(r.left, r.top, r.right, r.bottom) {
            val tramos = if (a.lumbre) 10 else 1
            for (t in 0 until tramos) {
                val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
                drawRect(a.luz(AZUCAR, cerca(Offset((xa + xb) / 2, r.center.y))), Offset(xa, r.top), Size(xb - xa, r.height))
            }
            // Las juntas de las tablillas: una línea fina y su labio de luz, cada 24 dp.
            val paso = maxOf(1f, 24f * u)
            var x = r.left + paso * (0.4f + 0.3f * azar(611, k))
            while (x < r.right) {
                drawLine(a.sombra(0.16f), Offset(x, r.top), Offset(x, r.bottom), 0.9f * u)
                drawLine(a.claro(0.18f), Offset(x + 1f * u, r.top), Offset(x + 1f * u, r.bottom), 0.6f * u)
                x += paso
            }
            // El fondo, en sombra: la balda de arriba lo tapa (honda junto a ella y deshecha hacia abajo).
            val f = if (a.oscuro) 1.2f else 1f
            drawRect(a.sombra(if (a.oscuro) 0.2f else 0.12f), r.topLeft, r.size)
            drawRect(a.sombra(0.3f * f), r.topLeft, Size(r.width, 6f * u))
            drawRect(a.sombra(0.17f * f), r.topLeft, Size(r.width, 15f * u))
            drawRect(a.sombra(0.09f * f), r.topLeft, Size(r.width, 30f * u))
            // El costado izquierdo hace sombra; el derecho, más hondo.
            drawRect(a.sombra(0.22f), r.topLeft, Size(7f * u, r.height))
            drawRect(a.sombra(0.12f), Offset(r.right - 12f * u, r.top), Size(12f * u, r.height))
            drawRect(a.sombra(0.18f), Offset(r.right - 5f * u, r.top), Size(5f * u, r.height))
            if (!a.oscuro) pinceladas(Rect(r.left + 8f * u, r.top + r.height * 0.4f, r.left + r.width * 0.55f, r.bottom), luzMueble(1f), 621 + k, 10, u, if (a.noche) 0.03f else 0.06f)
            // De noche, la luz de la lámpara que se derrama por delante de la cornisa y entra en las repisas de arriba, a la derecha.
            if (a.lampara) {
                val w = size.width
                val kc = cerca(Offset(w * LAMP_X, r.center.y))
                if (kc > 0.12f) mancha(Offset(w * (LAMP_X - 0.04f), r.top + r.height * 0.3f), w * 0.55f, LAMPARA.copy(alpha = (if (a.oscuro) 0.42f else 0.2f) * kc))
            }
            // La sombra que arroja el último volumen sobre el fondo, hacia donde no está la luz.
            if (oc != null && oc > r.left + 6f * u) {
                val largo = (if (a.lumbre) 18f else 11f) * u
                val izq = a.lampara && !a.ocaso
                if (!izq) {
                    drawPath(TrazoUrbe.poligono(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.22f, oc + largo, r.top + r.height * 0.3f, oc + largo, r.bottom), a.sombra(if (a.oscuro) 0.4f else 0.24f))
                }
            }
        }
    }

    /** Los dos costados: el muro que asoma afuera y el canto grueso del costado, de nogal, gastado donde se agarra. */
    private fun DrawScope.costados(d: DisposicionEstante, a: AireUrbe, arriba: Float, abajo: Float, n: Int, fila: Float, fy: (Float) -> Float) {
        val u = density; val w = size.width
        val costado = w * COSTADO; val lado = w * LADO
        listOf(Rect(costado, arriba, lado, abajo), Rect(w - lado, arriba, w - costado, abajo)).forEachIndexed { i, p ->
            val fx = p.center.x / w
            val tramos = if (a.lumbre) (p.height / maxOf(1f, fila * 0.25f)).toInt().coerceIn(4, 80) else 1
            for (t in 0 until tramos) {
                val ya = p.top + p.height * t / tramos; val yb = p.top + p.height * (t + 1) / tramos + 0.6f
                val k = if (a.lumbre) cerca(fx, fy((ya + yb) / 2), n, a) else 1f
                drawRect(a.luz(NOGAL, k), Offset(p.left, ya), Size(p.width, yb - ya))
                if (a.lumbre && k > 0.05f) {
                    // El filo de la luz en el canto que la mira.
                    val haciaLuz = if (a.luna) 0 else 1
                    if (i == haciaLuz) drawRect(a.filo(0.5f * k), Offset(if (i == 0) p.right - 1.4f * u else p.left, ya), Size(1.4f * u, yb - ya))
                }
            }
            // Las vetas, a lo largo, y la acanaladura del frente.
            for (j in 0 until 6) {
                val x = p.left + p.width * (0.12f + 0.15f * j + 0.05f * azar(640 + i, j))
                drawLine(a.sombra(0.12f + 0.06f * azar(641 + i, j)), Offset(x, p.top), Offset(x + (azar(642 + i, j) - 0.5f) * 2f * u, p.bottom), (0.5f + 0.5f * azar(643 + i, j)) * u)
            }
            drawRect(a.sombra(0.3f), Offset(p.left + p.width * 0.42f, p.top + 4f * u), Size(1.4f * u, p.height - 8f * u))
            drawRect(a.claro(0.3f), Offset(p.left + p.width * 0.42f + 1.4f * u, p.top + 4f * u), Size(0.8f * u, p.height - 8f * u))
            // La luz arriba a la izquierda y la sombra del lado contrario: el canto es redondo.
            drawRect(a.claro(0.28f), Offset(p.left, p.top), Size(1.6f * u, p.height))
            drawRect(a.sombra(0.3f), Offset(p.right - 2f * u, p.top), Size(2f * u, p.height))
            // El barniz gastado a la altura de la mano: más claro y sin brillo.
            val mano = p.top + p.height * 0.42f
            drawRect(a.luz(mezcla(NOGAL, YESO, 0.25f), if (a.lumbre) cerca(fx, fy(mano), n, a) else 1f).copy(alpha = 0.5f), Offset(p.left + p.width * 0.15f, mano), Size(p.width * 0.5f, p.height * 0.16f))
        }
    }

    /**
     * Una balda de nogal vista por su canto, combada en el medio por el peso ([comba], en píxeles): el canto con su luz arriba y
     * su sombra abajo, el gastado del medio y su sombra en el fondo. De noche se pinta por tramos.
     */
    private fun DrawScope.balda(r: Rect, comba: Float, k: Int, a: AireUrbe, cerca: (Offset) -> Float) {
        val u = density
        val c3 = comba * 4f / 3f
        val cara = Path().apply {
            moveTo(r.left, r.top)
            cubicTo(r.left + r.width / 3, r.top + c3, r.left + r.width * 2 / 3, r.top + c3, r.right, r.top)
            lineTo(r.right, r.bottom)
            cubicTo(r.left + r.width * 2 / 3, r.bottom + c3 * 1.25f, r.left + r.width / 3, r.bottom + c3 * 1.25f, r.left, r.bottom)
            close()
        }
        // Su sombra en el casillero de abajo: la balda vuela un poco sobre el fondo.
        drawPath(TrazoUrbe.corrido(cara, 3f * u, 4f * u), a.sombra(if (a.lumbre) 0.42f else 0.28f))
        clipPath(cara) {
            val tramos = if (a.lumbre) 24 else 1
            for (t in 0 until tramos) {
                val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
                val kk = cerca(Offset((xa + xb) / 2, r.center.y))
                drawRect(a.luz(NOGAL, kk), Offset(xa, r.top), Size(xb - xa, r.height + comba * 1.4f))
                if (a.lumbre) drawRect(a.filo(0.06f + 0.4f * kk), Offset(xa, r.top), Size(xb - xa, 1f * u))
            }
            // Las vetas, a lo largo del canto.
            for (j in 0 until 5) {
                val y = r.top + r.height * (0.2f + 0.15f * j) + comba * 0.6f
                drawLine(a.sombra(0.12f), Offset(r.left + r.width * azar(650 + k, j) * 0.3f, y), Offset(r.right - r.width * azar(651 + k, j) * 0.3f, y + (azar(652 + k, j) - 0.5f) * 2f * u), 0.6f * u)
            }
            // El canto redondeado: claro arriba, sombra abajo; el gastado del medio, más claro.
            drawRect(a.claro(0.32f), Offset(r.left, r.top), Size(r.width, r.height * 0.22f + comba))
            drawRect(a.sombra(0.22f), Offset(r.left, r.top + r.height * 0.62f), Size(r.width, r.height * 0.38f + comba * 1.3f))
            drawOval(a.luz(mezcla(NOGAL, YESO, 0.2f), cerca(r.center)).copy(alpha = 0.35f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.15f + comba * 0.8f), Size(r.width * 0.4f, r.height * 0.4f))
            // Mellas y rozaduras.
            for (j in 0 until 4) {
                val x = r.left + r.width * azar(653 + k, j)
                drawOval(a.sombra(0.2f), Offset(x, r.top + r.height * (0.25f + 0.4f * azar(654 + k, j))), Size(r.width * (0.01f + 0.02f * azar(655 + k, j)), 1.2f * u))
            }
        }
        drawPath(cara, a.sombra(0.35f), style = Stroke(0.6f * u))
    }

    /**
     * El rótulo de un mes: una tira de cinta de papel pegada en el canto de la balda, un poco torcida, con los extremos rasgados
     * a mano y el mes escrito encima. [baja] la corre hacia abajo con la comba.
     */
    private fun DrawScope.rotulo(p: PlacaEstante, baja: Float, medidor: TextMeasurer, a: AireUrbe, cerca: Float, i: Int) {
        val u = density
        val r = Rect(p.r.left, p.r.top + baja - 0.6f * u, p.r.right, p.r.bottom + baja + 0.6f * u)
        val dientes = 4
        val forma = Path().apply {
            moveTo(r.left + 2f * u, r.top + 0.5f * u)
            lineTo(r.right - 1.5f * u, r.top)
            for (j in 0 until dientes) {
                val y = r.top + r.height * (j + 0.5f) / dientes
                lineTo(r.right + (if (j % 2 == 0) 1.6f else -0.4f) * u, y)
            }
            lineTo(r.right - 1f * u, r.bottom - 0.6f * u)
            lineTo(r.left + 1.5f * u, r.bottom)
            for (j in dientes - 1 downTo 0) {
                val y = r.top + r.height * (j + 0.5f) / dientes
                lineTo(r.left + (if ((j + i) % 2 == 0) -1.2f else 0.8f) * u, y)
            }
            close()
        }
        drawPath(TrazoUrbe.corrido(forma, 1.2f * u, 1.5f * u), a.sombra(0.35f))
        drawPath(forma, mezcla(a.etiqueta(maxOf(cerca, 0.2f)), mezcla(CREMA, PaletaUrbe.OCRE, 0.35f), 0.4f))
        clipPath(forma) {
            drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 1f * u))
            drawRect(a.sombra(0.1f), Offset(r.left, r.bottom - 1.8f * u), Size(r.width, 1.8f * u))
            // Una arruga de la cinta.
            drawLine(a.sombra(0.12f), Offset(r.left + r.width * 0.72f, r.top), Offset(r.left + r.width * 0.75f, r.bottom), 0.6f * u)
        }
        textoEn(medidor, p.texto, Rect(r.left + 6f * u, r.top + 1f * u, r.right - 6f * u, r.bottom - 1f * u), LETRA, a.tinta, 10f, peso = FontWeight.SemiBold, espaciado = 0.8f, minimo = 5.5f)
    }

    /**
     * La cornisa de la librería: el filete de arriba, que vuela, la gola y el friso liso; con la cara de arriba apenas vista
     * ([fondo]) y su sombra en el muro y en la primera repisa. De noche, por tramos.
     */
    private fun DrawScope.cornisa(r: Rect, fondo: Float, a: AireUrbe, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        // La sombra en la primera repisa (debajo) y en el muro (a la derecha y debajo del vuelo).
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.34f), Offset(0f, r.bottom), Size(w, 4f * u))
        drawRect(a.sombra(0.16f), Offset(0f, r.bottom + 4f * u), Size(w, 6f * u))
        val filete = r.height * 0.24f
        val gola = r.height * 0.3f
        val tramos = if (a.lumbre) 24 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            val k = cerca(Offset((xa + xb) / 2, r.top))
            val madera = a.luz(NOGAL, k)
            drawRect(a.luz(mezcla(NOGAL, YESO, 0.12f), k), Offset(xa, r.top - fondo), Size(xb - xa, fondo))
            drawRect(madera, Offset(xa, r.top), Size(xb - xa, r.height))
            if (a.lumbre) drawRect(a.filo(0.08f + 0.5f * k), Offset(xa, r.top - fondo), Size(xb - xa, 1f * u))
        }
        // El filete, con su luz; la gola, que pasa de la luz a la sombra; el friso, con su veta.
        drawRect(a.claro(0.3f), Offset(r.left, r.top), Size(r.width, filete * 0.35f))
        drawRect(a.sombra(0.3f), Offset(r.left, r.top + filete - 1.2f * u), Size(r.width, 1.2f * u))
        drawRect(a.claro(0.22f), Offset(r.left, r.top + filete), Size(r.width, gola * 0.4f))
        drawRect(a.sombra(0.26f), Offset(r.left, r.top + filete + gola * 0.55f), Size(r.width, gola * 0.45f))
        drawRect(a.sombra(0.4f), Offset(r.left, r.top + filete + gola), Size(r.width, 1f * u))
        for (j in 0 until 6) {
            val y = r.top + filete + gola + (r.height - filete - gola) * (0.2f + 0.13f * j)
            drawLine(a.sombra(0.1f), Offset(w * azar(660, j) * 0.4f, y), Offset(w - w * azar(661, j) * 0.4f, y + (azar(662, j) - 0.5f) * 2f * u), 0.6f * u)
        }
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        drawLine(a.sombra(0.3f), Offset(r.left, r.top - fondo), Offset(r.right, r.top - fondo), 0.6f * u)
        // Un golpe en el filete, donde asoma la madera clara.
        val g = Offset(w * 0.57f, r.top + filete * 0.5f)
        drawOval(a.luz(mezcla(NOGAL, YESO, 0.45f), cerca(g)), Offset(g.x, g.y - 1.2f * u), Size(7f * u, 2.4f * u))
        if (!a.lumbre) pinceladas(Rect(r.left, r.top, r.right, r.bottom), NOGAL_OSC, 671, 10, u, 0.08f)
    }

    // ---------------------------------------------------------------- cada entrada

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width <= 1f || size.height <= 1f) return
        val a = AireUrbe(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val k = if (a.lumbre) cerca(fx, CORONA + (v.repisa * FILA + ALTO_REPISA.value * 0.55f) / ANCHO_NOMINAL, v.repisas, a) else 1f
        val arriba = when (clase(v.semilla)) {
            0 -> libroUrbe(v, medidor, a, k)
            1 -> cajaFotosUrbe(v, medidor, a, k)
            else -> carpetaUrbe(v, medidor, a, k)
        }
        a.rayo?.let { rayoEnVolumen(v, it, arriba) }
    }

    /** La luz de la ventana sobre un volumen de la primera repisa: los mismos trozos que en el mueble, pasados a su lienzo. */
    private fun DrawScope.rayoEnVolumen(v: Volumen, color: Color, arriba: Float) {
        val wn = ANCHO_NOMINAL * density
        val x0 = LADO + v.centro * (1f - 2 * LADO) - size.width / 2 / wn
        val y1 = pieRepisa(v.repisa)
        val y0 = y1 - (size.height - arriba) / wn
        val x1 = x0 + size.width / wn
        if (RAYO_TODO.right <= x0 || RAYO_TODO.left >= x1 || RAYO_TODO.bottom <= y0 || RAYO_TODO.top >= y1) return
        for (t in RAYOS) {
            val l = maxOf(t.left, x0); val r = minOf(t.right, x1)
            val a = maxOf(t.top, y0); val b = minOf(t.bottom, y1)
            if (r > l && b > a) {
                val o = Offset((l - x0) * wn, arriba + (a - y0) * wn); val t2 = Size((r - l) * wn, (b - a) * wn)
                val m = 0.7f * density
                drawRect(color.copy(alpha = color.alpha * 0.4f), o - Offset(m, m), Size(t2.width + 2 * m, t2.height + 2 * m), blendMode = BlendMode.Screen)
                drawRect(color, o, t2, blendMode = BlendMode.Screen)
            }
        }
    }
}
