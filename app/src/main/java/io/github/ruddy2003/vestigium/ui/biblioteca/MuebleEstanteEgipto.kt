package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * La paleta del almacén de la Tumba (Deir el-Medina, hacia 1200 a. C.): el adobe de limo del Nilo, gris pardo, y la bóveda
 * que se aleja en sombra; la palmera de los postes, la acacia de los tablones y la soga de fibra; el papiro, el lino y el
 * yeso de las tablillas; el cobre de los cinceles con su verdín y la fayenza; el grano, el pan y las cebollas de las raciones.
 */
internal object EgiptoTinta {
    /** El adobe de limo del Nilo (gris pardo, con paja), su junta de barro y la bóveda, más honda. */
    val ADOBE = Color(0xFF977B5E)
    val BOVEDA = Color(0xFF6E5946)
    val ENLUCIDO = Color(0xFFB39672)
    val PAJA = Color(0xFFD6BC86)
    /** El tronco de palmera de los postes, su sombra y la soga de fibra de palma. */
    val PALMA = Color(0xFF94704A)
    val PALMA_OSC = Color(0xFF4A3424)
    val FIBRA = Color(0xFFBC965F)
    /** Los tablones de acacia, desbastados a azuela. */
    val ACACIA = Color(0xFFB88A57)
    val ACACIA_OSC = Color(0xFF5C3E28)
    /** El papiro, el lino crudo, el yeso de las tablillas, la tinta negra y el ocre rojo de las rúbricas. */
    val PAPIRO = Color(0xFFE7D6A6)
    val PAPIRO_OSC = Color(0xFFB09566)
    val LINO = Color(0xFFE2D7BA)
    val YESO = Color(0xFFF2ECDC)
    val TINTA = Color(0xFF221A14)
    val ROJO = Color(0xFF93301F)
    /** El barro de los sellos, de la lámpara y de su pie. */
    val BARRO = Color(0xFF8C5839)
    /** El cobre de los cinceles, su verdín y la fayenza azul verdosa: lo frío de la paleta. */
    val COBRE = Color(0xFFBF7545)
    val VERDIN = Color(0xFF5E9C88)
    val FAYENZA = Color(0xFF3F90A4)
    /** La piedra gris de las pesas. */
    val PIEDRA = Color(0xFF8D8A84)
    /** El trigo de las raciones, el pan cocido y la piel de las cebollas. */
    val GRANO = Color(0xFFD8B663)
    val PAN = Color(0xFFC28A4A)
    val CEBOLLA = Color(0xFFB4664A)
    /** La tierra apisonada del suelo. */
    val TIERRA = Color(0xFFA48A69)

    // Las luces: la noche del almacén, la de los Sueños, el atardecer de los Sueños en claro, la llama, la luna y el sol.
    val NOCHE = Color(0xFF151A2C)
    val NOCHE_SUENOS = Color(0xFF0D1636)
    val ATARDECER = Color(0xFF5A3D68)
    val LLAMA = Color(0xFFFFD27E)
    val LUNA = Color(0xFFBDD0F2)
    val SOL = Color(0xFFFFEDC0)
}

/**
 * El aire del momento en el almacén: de día, el sol que entra por la puerta (a la izquierda, fuera del cuadro) y deja su
 * mancha en el adobe; de noche (el modo oscuro), la lámpara del suelo, a la derecha, alumbra de abajo arriba y la luna entra
 * por la misma puerta; en los Sueños ([noche]), el atardecer en claro y, en oscuro ([lunar]), la lámpara baja y la luna fuerte.
 * [cerca] es cuánto llega de la llama (0 a 1) y [frio], de la luna.
 */
internal class AireEgipto(val oscuro: Boolean, val noche: Boolean) {
    val lumbre: Boolean get() = oscuro || noche
    val hondo: Color get() = if (oscuro) (if (noche) EgiptoTinta.NOCHE_SUENOS else EgiptoTinta.NOCHE) else SOMBRA_MUEBLE
    val lunar: Boolean get() = oscuro && noche
    private val luna: Float get() = if (lunar) 1.4f else 1f

    private fun lunada(c: Color, f: Float): Color =
        if (f <= 0f) c else Color(c.red * (1f - 0.3f * f), c.green * (1f - 0.1f * f), (c.blue * (1f + 0.16f * f)).coerceAtMost(1f), c.alpha)

    fun luz(c: Color, cerca: Float, frio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, EgiptoTinta.ATARDECER, 0.15f), FILO_MUEBLE, 0.1f * k) else c
        val f = (frio * luna).coerceIn(0f, 1f) * (1f - k)
        if (lunar) return lunada(mezcla(mezcla(c, hondo, 0.9f - 0.8f * k.pow(0.9f) - 0.24f * f), FILO_MUEBLE, 0.32f * k), f)
        val base = mezcla(c, hondo, 0.88f - 0.78f * k.pow(0.9f) - 0.2f * f)
        return mezcla(mezcla(base, FILO_MUEBLE, 0.22f * k), EgiptoTinta.LUNA, 0.16f * f)
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(if (noche) a * 1.15f else a)
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.55f else a)
    fun filo(a: Float): Color = FILO_MUEBLE.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    fun helado(a: Float): Color = EgiptoTinta.LUNA.copy(alpha = if (oscuro) (a * luna).coerceIn(0f, 1f) else 0f)

    /** El color del ánimo: de noche se apaga menos que lo demás y no cambia de color, para que se adivine en lo lejano. */
    fun animo(t: Color, cerca: Float, frio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(t, EgiptoTinta.ATARDECER, 0.08f), FILO_MUEBLE, 0.06f * k) else t
        val f = (frio * luna).coerceIn(0f, 1f) * (1f - k)
        val d = (if (lunar) 0.6f else 0.54f) - (if (lunar) 0.6f else 0.5f) * k
        val s = 1f - 0.72f * d
        return lunada(mezcla(mezcla(Color(t.red * s, t.green * s, t.blue * s), hondo, 0.38f * d), FILO_MUEBLE, 0.26f * k), f * 0.7f)
    }
    /** El papiro o el yeso donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(cerca: Float, base: Color = EgiptoTinta.YESO): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, EgiptoTinta.ATARDECER, 0.08f) else base
        if (lunar) return mezcla(mezcla(base, hondo, 0.46f - 0.42f * k), FILO_MUEBLE, 0.2f * k)
        return mezcla(mezcla(base, hondo, 0.4f - 0.32f * k), FILO_MUEBLE, 0.14f * k)
    }
    val tinta: Color get() = if (oscuro) mezcla(EgiptoTinta.TINTA, hondo, 0.35f) else EgiptoTinta.TINTA
    val rubrica: Color get() = if (oscuro) mezcla(EgiptoTinta.ROJO, hondo, 0.3f) else EgiptoTinta.ROJO
}

/**
 * El dibujo del estante del diario de Egipto (lo usa `EstanteEgipto`, en `BibliotecaAntiguedad.kt`): el almacén de la Tumba,
 * donde Pay, escriba de la cuadrilla, guarda sus cuentas junto a lo que reparte. Es una nave de adobe con bóveda de cañón de
 * ladrillos inclinados; contra su testero, un anaquel de dos troncos de palmera hincados en el suelo, con tablones de acacia
 * apoyados en estacas y atados con soga de fibra. Arriba, sobre el último tablón, la balanza del cobre con los cinceles en un
 * platillo y las pesas en el otro; colgados del muro, los manojos de mechas de lino de la tumba y la ristra de cebollas; al
 * pie, el costal de trigo con su medida de madera y la lámpara en su pie de barro. De día manda el sol de la puerta (fuera
 * del cuadro, a la izquierda), que deja su mancha recortada en el testero; de noche, la lámpara del suelo alumbra de abajo
 * arriba y la luna entra por la misma puerta, arriba a la izquierda. Lo de arriba, el pie y los huecos van en
 * `MuebleEstanteEgiptoPiezas.kt`; los volúmenes, en `MuebleEstanteEgiptoVolumenes.kt`.
 */
internal object MuebleEstanteEgipto {
    val LETRA: FontFamily = Letra.messiri

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.52f
    const val BASE = 0.3f
    const val LADO = 0.085f
    val ALTO_REPISA: Dp = 110.dp
    val TABLA: Dp = 14.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (24 + (palabras / 36).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el rollo en su funda de lino; 1, la tablilla de madera pintada; 2, el fardo de lino. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 93)
        return if (x < 0.4f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El rollo, el más alto; la tablilla, mediana; el fardo, el más bajo y blando. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.08f * azar(semilla, 1)
        1 -> 0.76f + 0.06f * azar(semilla, 1)
        else -> 0.74f + 0.06f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la lámpara, en anchos del mueble: [LAMP_X] desde la izquierda y [LAMP_BAJO] por debajo del último tablón. */
    const val LAMP_X = 0.865f
    const val LAMP_BAJO = 0.05f
    /** El centro de la mancha de la puerta en el testero (de día el sol, de noche la luna), en anchos desde arriba a la izquierda. */
    const val PUERTA_X = 0.17f
    const val PUERTA_Y = 0.44f
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 124f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.42f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireEgipto(oscuro, noche)
        val ch = d.corona.bottom
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val llama = Offset(w * LAMP_X, b.top + w * LAMP_BAJO)
        fun cerca(p: Offset): Float = if (a.lumbre) alcanceEgipto((p.x - llama.x) / w, (p.y - llama.y) / w, a.lunar) else 1f
        fun frio(p: Offset): Float = if (a.oscuro) frioEgipto(p.x / w - PUERTA_X, p.y / w - PUERTA_Y) else 0f
        val poste = w * 0.062f
        val pI = Rect(w * 0.016f, ch - w * 0.07f, w * 0.016f + poste, suelo + w * 0.012f)
        val pD = Rect(w - w * 0.016f - poste, ch - w * 0.064f, w - w * 0.016f, suelo + w * 0.016f)
        val gT = w * 0.034f
        val apoyo = ch - gT
        val fondo = if (d.tablas.isNotEmpty()) d.tablas.last().bottom else ch

        // ---- el testero de adobe y la bóveda
        muroEgipto(a, ch, suelo, llama) { p -> cerca(p) to frio(p) }
        bovedaEgipto(a, ch)
        puertaEgipto(a, ch)

        // ---- la sombra del anaquel en el muro: detrás de cada repisa, la del tablón de arriba y la de los postes
        d.repisas.forEachIndexed { k, r -> sombraHuecoEgipto(r, pI, pD, a, k) }
        drawRect(a.sombra(if (oscuro) 0.3f else 0.14f), Offset(pI.right, fondo), Size(pD.left - pI.right, w * 0.022f))

        // ---- el suelo de tierra apisonada
        sueloEgipto(a, suelo, llama) { p -> cerca(p) }

        // ---- los postes de palmera y los tablones de acacia, atados
        posteEgipto(pI, a, 41, cerca(pI.center), frio(Offset(pI.center.x, ch)))
        posteEgipto(pD, a, 47, cerca(pD.center), frio(Offset(pD.center.x, ch)))
        val tablonA = Rect(w * 0.004f, apoyo, w * 0.996f, ch)
        tablonEgipto(tablonA, a, 300) { x -> cerca(Offset(x, ch)) to frio(Offset(x, ch)) }
        ataduraEgipto(Offset(pI.center.x, tablonA.center.y), poste, tablonA.height, a, cerca(Offset(pI.center.x, ch)))
        ataduraEgipto(Offset(pD.center.x, tablonA.center.y), poste, tablonA.height, a, cerca(Offset(pD.center.x, ch)))
        d.tablas.forEachIndexed { k, r ->
            val t = Rect(w * (0.006f + 0.006f * azar(k, 3)), r.top, w * (0.994f - 0.006f * azar(k, 4)), r.bottom)
            tablonEgipto(t, a, 310 + 7 * k) { x -> cerca(Offset(x, r.center.y)) to frio(Offset(x, r.center.y)) }
            ataduraEgipto(Offset(pI.center.x, r.center.y), poste, r.height, a, cerca(Offset(pI.center.x, r.center.y)))
            ataduraEgipto(Offset(pD.center.x, r.center.y), poste, r.height, a, cerca(Offset(pD.center.x, r.center.y)))
        }
        // La sombra del tablón de arriba en el primer hueco.
        if (d.repisas.isNotEmpty()) {
            val r0 = d.repisas[0]
            drawRect(a.sombra(if (oscuro) 0.5f else 0.36f), Offset(pI.right, r0.top), Size(pD.left - pI.right, 3.5f * u))
        }

        // ---- lo que hay en los huecos libres
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            val c = Offset((oc + r.right) / 2, r.center.y)
            huecoLibreEgipto(k, r, oc, pD.left, a, cerca(c), frio(c))
        }

        // ---- el rótulo de cada mes: una tablilla de yeso atada al canto del tablón
        d.placas.forEach { p -> rotuloEgipto(p, medidor, a, cerca(p.r.center), frio(p.r.center)) }

        // ---- lo de arriba, de atrás adelante
        val hacia = { p: Offset -> if (a.lumbre) (if (llama.x >= p.x) 1f else -1f) else -1f }
        val clavoM = Offset(w * 0.115f, ch * 0.3f)
        mechasEgipto(clavoM, w, a, cerca(clavoM + Offset(0f, w * 0.1f)), frio(clavoM + Offset(0f, w * 0.1f)))
        val clavoC = Offset(w * 0.955f, ch * 0.34f)
        cebollasEgipto(clavoC, w, a, cerca(clavoC + Offset(0f, w * 0.08f)), frio(clavoC))
        val pieCin = Offset(w * 0.2f, apoyo)
        cincelesEgipto(pieCin, w, a, cerca(pieCin), frio(pieCin))
        val pieBal = Offset(w * 0.47f, apoyo)
        balanzaEgipto(pieBal, w, a, cerca(pieBal - Offset(0f, w * 0.12f)), frio(pieBal - Offset(0f, w * 0.12f)), hacia(pieBal))
        val piePan = Offset(w * 0.77f, apoyo)
        panesEgipto(piePan, w, a, cerca(piePan), frio(piePan), hacia(piePan))

        // ---- al pie: el costal de trigo con su medida y la lámpara en su pie de barro
        val pieCostal = Offset(w * 0.2f, suelo + w * 0.07f)
        costalEgipto(pieCostal, w, a, maxOf(cerca(pieCostal - Offset(0f, w * 0.05f)), if (a.lumbre) 0.1f else 1f), frio(pieCostal))
        lamparaEgipto(llama, suelo + w * 0.065f, w, a)
        if (a.lumbre) llamaEgipto(llama, w, a)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- el muro y la bóveda

    /**
     * El testero de adobe: el limo gris pardo en hiladas con sus juntas de barro (una baldosa pintada una sola vez), con
     * parches de enlucido; de noche, hundido y encendido de abajo arriba por la lámpara, con la luna arriba a la izquierda.
     */
    private fun DrawScope.muroEgipto(a: AireEgipto, ch: Float, suelo: Float, llama: Offset, luz: (Offset) -> Pair<Float, Float>) {
        val u = density
        val w = size.width
        val muro = Rect(0f, 0f, w, suelo)
        if (!a.lumbre) {
            drawRect(EgiptoTinta.ADOBE, muro.topLeft, muro.size)
        } else if (!a.oscuro) {
            // El atardecer de los Sueños: el adobe con su color, un poco morado, y el charco tibio de la lámpara.
            drawRect(a.luz(EgiptoTinta.ADOBE, 0f), muro.topLeft, muro.size)
            mancha(llama, w * 0.9f, a.luz(EgiptoTinta.ADOBE, 1f).copy(alpha = 0.6f))
        } else {
            // De noche: el adobe hundido y, encima, la luz de la lámpara en tres pasos (no un velo: se enciende el muro).
            drawRect(a.luz(EgiptoTinta.ADOBE, 0f, 0f), muro.topLeft, muro.size)
            val r = if (a.lunar) 0.95f else 1.35f
            mancha(llama, w * r, a.luz(EgiptoTinta.ADOBE, 0.55f).copy(alpha = 0.9f))
            mancha(llama, w * r * 0.55f, a.luz(EgiptoTinta.ADOBE, 0.85f).copy(alpha = 0.8f))
            mancha(llama, w * r * 0.22f, EgiptoTinta.LLAMA.copy(alpha = 0.32f))
        }
        val adobe = AdobeEgipto.de(u)
        clipRect(0f, 0f, w, suelo) {
            var y = 0f
            while (y < suelo) {
                var x = -(adobe.width * 0.37f)
                while (x < w) { drawImage(adobe, Offset(x, y), alpha = if (a.oscuro) 0.55f else 0.9f); x += adobe.width }
                y += adobe.height
            }
        }
        val (k0, f0) = luz(Offset(w * 0.5f, ch))
        pinceladas(Rect(0f, 0f, w, suelo), a.luz(EgiptoTinta.ENLUCIDO, k0, f0), 611, 46, u, 0.16f)
        pinceladas(Rect(0f, 0f, w, suelo), a.luz(EgiptoTinta.BOVEDA, k0, f0), 617, 20, u, 0.12f)
    }

    /**
     * La bóveda de cañón vista desde dentro: sobre el testero, el arco de ladrillos inclinados en dos roscas (con sus juntas
     * en abanico) y, fuera del arco, el intradós que se viene hacia acá, en sombra.
     */
    private fun DrawScope.bovedaEgipto(a: AireEgipto, ch: Float) {
        val u = density
        val w = size.width
        val arranque = ch * 0.86f
        val clave = ch * 0.09f
        val rosca = w * 0.05f
        fun arco(sube: Float, abre: Float) = Path().apply {
            moveTo(-abre, arranque - sube * 0.25f)
            quadraticTo(w / 2, 2 * (clave - sube) - (arranque - sube * 0.25f), w + abre, arranque - sube * 0.25f)
        }
        // El intradós, fuera de la rosca: honda sombra de barro, con las hiladas que se curvan.
        val fuera = Path().apply {
            moveTo(-1f, -1f); lineTo(w + 1f, -1f); lineTo(w + 1f, arranque - rosca * 2 * 0.25f)
            quadraticTo(w / 2, 2 * (clave - rosca * 2) - (arranque - rosca * 2 * 0.25f), -1f, arranque - rosca * 2 * 0.25f)
            close()
        }
        drawPath(fuera, a.luz(EgiptoTinta.BOVEDA, 0f, if (a.oscuro) 0.4f else 0f))
        drawPath(fuera, a.sombra(if (a.oscuro) 0.3f else 0.2f))
        for (j in 1..3) drawPath(arco(rosca * 2 + j * w * 0.035f, w * 0.03f * j), a.sombra(0.22f), style = Stroke(1.1f * u))
        // Las dos roscas de ladrillo: su cara, más clara, y las juntas que abren en abanico.
        val banda = Path().apply {
            moveTo(-1f, arranque); quadraticTo(w / 2, 2 * clave - arranque, w + 1f, arranque)
            lineTo(w + 1f, arranque - rosca * 2 * 0.25f)
            quadraticTo(w / 2, 2 * (clave - rosca * 2) - (arranque - rosca * 2 * 0.25f), -1f, arranque - rosca * 2 * 0.25f)
            close()
        }
        drawPath(banda, a.luz(mezcla(EgiptoTinta.ADOBE, EgiptoTinta.ENLUCIDO, 0.3f), 0f, if (a.oscuro) 0.7f else 0f))
        drawPath(arco(rosca, 0f), a.sombra(0.32f), style = Stroke(1f * u))
        drawPath(arco(0f, 0f), a.sombra(0.5f), style = Stroke(1.6f * u))
        drawPath(arco(rosca * 2, 0f), a.sombra(0.4f), style = Stroke(1.2f * u))
        val juntas = Path()
        val y0 = arranque; val yc = 2 * clave - arranque
        for (anillo in 0..1) {
            val n = 46 + anillo * 4
            for (i in 0..n) {
                val t = i / n.toFloat()
                val x = (1 - t) * (1 - t) * 0f + 2 * (1 - t) * t * (w / 2) + t * t * w
                val y = (1 - t) * (1 - t) * y0 + 2 * (1 - t) * t * yc + t * t * y0
                // La normal del arco (hacia afuera).
                val dx = 2 * (1 - t) * (w / 2) + 2 * t * (w - w / 2)
                val dy = 2 * (1 - t) * (yc - y0) + 2 * t * (y0 - yc)
                val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
                val nx = dy / l; val ny = -dx / l
                val s = if (ny > 0) -1f else 1f
                val d0 = rosca * anillo + 0.6f * u; val d1 = rosca * (anillo + 1) - 0.6f * u
                val desplaza = if (anillo == 1) 0.5f / n else 0f
                val xx = x + (w * desplaza)
                juntas.moveTo(xx + nx * s * d0, y + ny * s * d0)
                juntas.lineTo(xx + nx * s * d1, y + ny * s * d1)
            }
        }
        drawPath(juntas, a.sombra(0.3f), style = Stroke(0.9f * u))
        // La luz rasante en el canto de abajo de la rosca.
        drawPath(arco(-1.2f * u, 0f), a.claro(0.16f), style = Stroke(1.2f * u))
    }

    /**
     * La mancha de la puerta en el testero: de día, el sol recortado por el dintel y la jamba (arriba a la izquierda); de
     * noche, la luna por la misma puerta; en los Sueños claros, el último sol, rosado.
     */
    private fun DrawScope.puertaEgipto(a: AireEgipto, ch: Float) {
        val w = size.width
        val y0 = ch * 0.3f
        val forma = poligonoEgipto(-1f, y0, w * 0.36f, y0 + w * 0.13f, w * 0.43f, ch + w * 0.5f, w * 0.1f, ch + w * 0.62f, -1f, ch + w * 0.55f)
        when {
            !a.lumbre -> {
                drawPath(forma, EgiptoTinta.SOL.copy(alpha = 0.26f))
                drawPath(forma, luzMueble(0.1f))
            }
            !a.oscuro -> drawPath(forma, Color(0xFFFFB98A).copy(alpha = 0.18f))
            else -> drawPath(forma, EgiptoTinta.LUNA.copy(alpha = if (a.lunar) 0.2f else 0.13f))
        }
    }

    /** El fondo de un hueco: el adobe del testero en la sombra del anaquel (la del tablón de arriba y la de los postes). */
    private fun DrawScope.sombraHuecoEgipto(r: Rect, pI: Rect, pD: Rect, a: AireEgipto, k: Int) {
        val u = density
        val x0 = pI.right; val x1 = pD.left
        val f = if (a.oscuro) 1.15f else 1f
        drawRect(a.sombra(0.2f * f), Offset(x0, r.top), Size(x1 - x0, r.height))
        drawRect(a.sombra(0.34f * f), Offset(x0, r.top), Size(x1 - x0, 9f * u))
        drawRect(a.sombra(0.16f * f), Offset(x0, r.top + 9f * u), Size(x1 - x0, 10f * u))
        // La sombra de cada poste en el muro, corrida a la derecha (la luz viene de la izquierda).
        drawRect(a.sombra(0.22f * f), Offset(pI.right, r.top), Size(7f * u, r.height))
        drawRect(a.sombra(0.12f * f), Offset(pD.right, r.top), Size(minOf(5f * u, size.width - pD.right), r.height))
        if (!a.oscuro && !a.noche) drawRect(luzMueble(0.05f + 0.03f * azar(k, 9)), Offset(x0 + (x1 - x0) * 0.5f, r.top + r.height * 0.4f), Size((x1 - x0) * 0.5f, r.height * 0.6f))
    }

    /** El suelo de tierra apisonada, con su zócalo de barro, las rodadas de la escoba y el trigo que se cae del costal. */
    private fun DrawScope.sueloEgipto(a: AireEgipto, suelo: Float, llama: Offset, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width; val h = size.height
        if (h - suelo < 1f) return
        val piso = Rect(0f, suelo, w, h)
        drawRect(a.luz(EgiptoTinta.TIERRA, if (a.lumbre) 0.15f else 1f), piso.topLeft, piso.size)
        if (a.lumbre) mancha(Offset(llama.x, suelo + (h - suelo) * 0.6f), w * 0.55f, a.luz(EgiptoTinta.TIERRA, 0.9f).copy(alpha = 0.85f))
        pinceladas(piso, a.luz(mezcla(EgiptoTinta.TIERRA, EgiptoTinta.PAJA, 0.4f), cerca(piso.center)), 641, 14, u, 0.14f)
        pinceladas(piso, a.sombra(1f), 647, 10, u, 0.08f)
        // Las rodadas de la escoba de palma: arcos finos y claros.
        val escoba = Path()
        for (j in 0 until 7) {
            val cx = w * (0.15f + 0.12f * j + 0.05f * azar(j, 61)); val cy = suelo + (h - suelo) * (0.45f + 0.4f * azar(j, 62))
            escoba.moveTo(cx - 14f * u, cy); escoba.quadraticTo(cx, cy - 4f * u, cx + 14f * u, cy + 1f * u)
        }
        drawPath(escoba, a.claro(0.12f), style = Stroke(0.6f * u))
        // El zócalo de barro donde el muro pisa el suelo, y la sombra del muro.
        drawRect(a.luz(EgiptoTinta.BOVEDA, cerca(Offset(w / 2, suelo)) * 0.8f), Offset(0f, suelo - 3f * u), Size(w, 3f * u))
        drawRect(a.sombra(0.3f), Offset(0f, suelo), Size(w, 2.5f * u))
    }

    // ---------------------------------------------------------------- el anaquel

    /**
     * Un poste de tronco de palmera: el cilindro con su luz a la izquierda, las cicatrices de las pencas en rombo, la fibra
     * que asoma y el corte de arriba, astillado.
     */
    private fun DrawScope.posteEgipto(r: Rect, a: AireEgipto, semilla: Int, k: Float, f: Float) {
        val u = density
        drawRect(a.sombra(0.3f), Offset(r.right, r.top + 4f * u), Size(4f * u, r.height - 4f * u))
        drawRect(a.luz(EgiptoTinta.PALMA, k, f), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las cicatrices de las pencas: dos tramas en diagonal, en un solo camino.
            val rombos = Path()
            val paso = maxOf(2f, r.width * 0.42f)
            var y = r.top - r.width
            var fila = 0
            while (y < r.bottom + r.width) {
                rombos.moveTo(r.left, y); rombos.lineTo(r.right, y + r.width * 0.45f)
                rombos.moveTo(r.right, y + (if (fila % 2 == 0) 0f else paso * 0.5f)); rombos.lineTo(r.left, y + r.width * 0.45f + (if (fila % 2 == 0) 0f else paso * 0.5f))
                y += paso; fila++
            }
            drawPath(rombos, a.luz(EgiptoTinta.PALMA_OSC, k, f).copy(alpha = 0.55f), style = Stroke(1.1f * u))
            drawPath(rombos, a.claro(0.14f), style = Stroke(0.5f * u))
            // El cilindro: luz a la izquierda, sombra a la derecha (de noche, el filo cálido del lado de la lámpara).
            drawRect(a.claro(0.22f), Offset(r.left + r.width * 0.14f, r.top), Size(r.width * 0.16f, r.height))
            drawRect(a.sombra(0.22f), Offset(r.left + r.width * 0.58f, r.top), Size(r.width * 0.42f, r.height))
            drawRect(a.sombra(0.22f), Offset(r.right - r.width * 0.18f, r.top), Size(r.width * 0.18f, r.height))
            if (a.oscuro) drawRect(a.filo(0.35f * k), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
            // La fibra que asoma, en hebras.
            for (j in 0 until 8) {
                val yy = r.top + r.height * azar(semilla, j)
                drawLine(a.luz(EgiptoTinta.FIBRA, k, f).copy(alpha = 0.5f), Offset(r.left + r.width * azar(semilla, j + 20), yy), Offset(r.left + r.width * azar(semilla, j + 40), yy + 6f * u), 0.6f * u)
            }
        }
        // El corte de arriba: la cara del tronco, astillada.
        val tapa = Rect(r.left - 0.5f * u, r.top - r.width * 0.14f, r.right + 0.5f * u, r.top + r.width * 0.14f)
        drawOval(a.luz(mezcla(EgiptoTinta.FIBRA, EgiptoTinta.PAPIRO, 0.3f), k, f), tapa.topLeft, tapa.size)
        drawOval(a.luz(EgiptoTinta.PALMA_OSC, k, f).copy(alpha = 0.5f), tapa.topLeft, tapa.size, style = Stroke(0.8f * u))
        for (j in 0 until 4) {
            val x = r.left + r.width * (0.15f + 0.22f * j)
            drawLine(a.luz(EgiptoTinta.PALMA_OSC, k, f).copy(alpha = 0.4f), Offset(x, tapa.center.y - 1f * u), Offset(x + 2f * u, tapa.center.y + 1.5f * u), 0.6f * u)
        }
    }

    /**
     * Un tablón de acacia desbastado a azuela: la cara de arriba que se ve apenas, el canto con sus vetas y los golpes de la
     * azuela, la arista gastada, una grieta en una punta y la sombra que echa abajo. De noche se pinta por tramos.
     */
    private fun DrawScope.tablonEgipto(t: Rect, a: AireEgipto, semilla: Int, luz: (Float) -> Pair<Float, Float>) {
        val u = density
        val tramos = if (a.oscuro) 6 else 1
        for (j in 0 until tramos) {
            val xa = t.left + t.width * j / tramos; val xb = t.left + t.width * (j + 1) / tramos + 0.6f
            val (k, f) = luz((xa + xb) / 2)
            drawRect(a.luz(EgiptoTinta.ACACIA, k, f), Offset(xa, t.top), Size(xb - xa, t.height))
        }
        val (k, f) = luz(t.center.x)
        vetas(t, a.luz(EgiptoTinta.ACACIA_OSC, k, f).copy(alpha = 0.32f), semilla, densidad = 1.2f, grosor = 0.8f)
        // Los golpes de la azuela: medias lunas cortas en el canto.
        val golpes = Path()
        var x = t.left + 6f * u
        var i = 0
        while (x < t.right - 6f * u) {
            val y = t.top + t.height * (0.3f + 0.4f * azar(semilla, i))
            golpes.moveTo(x, y); golpes.quadraticTo(x + 4f * u, y - 1.6f * u, x + 8f * u, y)
            x += (14f + 16f * azar(semilla, i + 50)) * u; i++
        }
        drawPath(golpes, a.luz(EgiptoTinta.ACACIA_OSC, k, f).copy(alpha = 0.3f), style = Stroke(0.7f * u))
        // La cara de arriba (una tira de luz), la arista gastada y la cara de abajo en sombra.
        drawRect(a.claro(0.3f), t.topLeft, Size(t.width, 1.6f * u))
        if (a.oscuro) drawRect(a.filo(0.3f * k), t.topLeft, Size(t.width, 1.1f * u))
        drawRect(a.sombra(0.3f), Offset(t.left, t.bottom - 2.2f * u), Size(t.width, 2.2f * u))
        // Una grieta en la punta de la derecha y la testa de la izquierda, más oscura.
        val gy = t.top + t.height * 0.55f
        drawLine(a.sombra(0.45f), Offset(t.right - 1f, gy), Offset(t.right - t.width * 0.05f, gy + 1.2f * u), 0.8f * u)
        drawRect(a.sombra(0.22f), t.topLeft, Size(2.5f * u, t.height))
        drawRect(a.sombra(0.3f), Offset(t.right - 2.5f * u, t.top), Size(2.5f * u, t.height))
    }

    /** La atadura de soga de fibra que sujeta el tablón al poste: vueltas en aspa, con su luz y su sombra. */
    private fun DrawScope.ataduraEgipto(c: Offset, ancho: Float, alto: Float, a: AireEgipto, k: Float) {
        val u = density
        val hw = ancho * 0.62f; val hh = alto * 0.85f
        val soga = Path().apply {
            for (j in 0 until 3) {
                val dx = (j - 1) * 2.2f * u
                moveTo(c.x - hw + dx, c.y - hh); lineTo(c.x + hw + dx, c.y + hh)
                moveTo(c.x + hw + dx, c.y - hh); lineTo(c.x - hw + dx, c.y + hh)
            }
        }
        drawPath(soga, a.sombra(0.5f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(soga, a.luz(EgiptoTinta.FIBRA, k), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        drawPath(soga, a.claro(0.3f), style = Stroke(0.5f * u))
    }

    /** El mes en una tablilla de sicomoro enyesada, atada al canto del tablón con dos vueltas de cordel. */
    private fun DrawScope.rotuloEgipto(p: PlacaEstante, medidor: TextMeasurer, a: AireEgipto, k: Float, f: Float) {
        val u = density
        val r = p.r
        val t = Rect(r.left, r.top - 0.6f * u, r.right, r.bottom + 0.6f * u)
        drawRoundRect(a.sombra(0.4f), Offset(t.left + 1.4f * u, t.top + 1.6f * u), t.size, CornerRadius(2f * u))
        drawRoundRect(a.luz(EgiptoTinta.ACACIA, k, f), t.topLeft, t.size, CornerRadius(2f * u))
        val cara = t.deflate(1.4f * u)
        drawRoundRect(a.etiqueta(k, EgiptoTinta.YESO), cara.topLeft, cara.size, CornerRadius(1.4f * u))
        // El yeso gastado en las esquinas, donde asoma la madera.
        drawCircle(a.luz(EgiptoTinta.ACACIA, k, f).copy(alpha = 0.5f), 2.2f * u, Offset(cara.right - 2f * u, cara.bottom - 1.5f * u))
        drawRect(a.claro(0.4f), cara.topLeft, Size(cara.width, 0.8f * u))
        // Los cordeles que dan la vuelta al tablón.
        listOf(cara.left + 5f * u, cara.right - 5f * u).forEach { x ->
            drawLine(a.sombra(0.4f), Offset(x + 0.8f * u, t.top - 1f * u), Offset(x + 0.8f * u, t.bottom + 1f * u), 1.6f * u)
            drawLine(a.luz(EgiptoTinta.FIBRA, k, f), Offset(x, t.top - 1.5f * u), Offset(x, t.bottom + 1.5f * u), 1.2f * u)
        }
        textoEn(medidor, p.texto, Rect(cara.left + 8f * u, cara.top + 0.5f * u, cara.right - 8f * u, cara.bottom - 0.5f * u), LETRA, a.tinta, 10.5f,
            peso = FontWeight.Medium, minimo = 5f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenEgipto(v, medidor, oscuro)
}

/**
 * La baldosa del adobe, pintada una sola vez por densidad: hiladas de ladrillos de limo a soga, trabados, cada uno con su
 * tono, con la junta de barro gruesa y desigual y la paja que asoma. Se pega en mosaico sobre el color del muro (que lleva
 * la luz), así el muro entero son unas decenas de imágenes, no miles de ladrillos.
 */
private object AdobeEgipto {
    private var densidad = 0f
    private var baldosa: ImageBitmap? = null
    fun de(u: Float): ImageBitmap {
        if (u != densidad || baldosa == null) { baldosa = pintar(u); densidad = u }
        return baldosa!!
    }
    private fun pintar(u: Float): ImageBitmap {
        val ancho = (176f * u).roundToInt().coerceAtLeast(8)
        val filas = 14
        val alto = (filas * 7.6f * u).roundToInt().coerceAtLeast(8)
        val img = ImageBitmap(ancho, alto)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ancho.toFloat(), alto.toFloat())) {
            val ph = alto / filas.toFloat()
            val juntas = Path()
            for (f in 0 until filas) {
                val y = f * ph
                // Los ladrillos de la hilada, de largos desiguales (de molde y secados al sol), que cierran justo en el ancho
                // de la baldosa para que el mosaico no se note en la junta.
                val largos = FloatArray(8) { 0.7f + 0.6f * azar(f * 17 + it, 5) }
                val total = largos.sum()
                var x = -ancho * 0.07f * azar(f, 6)
                for (c in largos.indices) {
                    val l = largos[c] / total * ancho
                    val t = azar(f * 31 + c, 3)
                    val tono = when { t > 0.72f -> LUZ_MUEBLE; t < 0.3f -> SOMBRA_MUEBLE; else -> Color(0xFFC9A57A) }
                    // El ladrillo, gastado en las aristas: un poco más chico que su sitio, con las esquinas romas.
                    drawRoundRect(tono.copy(alpha = 0.05f + 0.1f * azar(f, c + 40)), Offset(x + 0.9f * u, y + 0.9f * u), Size(l - 1.8f * u, ph - 1.6f * u),
                        CornerRadius(1.6f * u))
                    if (azar(f, c + 60) > 0.45f) drawRect(LUZ_MUEBLE.copy(alpha = 0.09f), Offset(x + 1.6f * u, y + 1.1f * u), Size(l * (0.4f + 0.5f * azar(f, c + 61)), 0.6f * u))
                    val jx = x + l
                    juntas.moveTo(jx, y + 0.5f * u); juntas.lineTo(jx + (azar(f, c + 9) - 0.5f) * 1.2f * u, y + ph - 0.3f * u)
                    if (x < 0f) { juntas.moveTo(jx + ancho, y + 0.5f * u); juntas.lineTo(jx + ancho, y + ph) }
                    x += l
                }
                // La junta de asiento: barro, desigual.
                juntas.moveTo(0f, y + 0.4f * u)
                var jx = 0f
                while (jx < ancho) { jx += 9f * u; juntas.lineTo(jx, y + (0.2f + 0.5f * azar(f, (jx / u).toInt())) * u) }
            }
            drawPath(juntas, SOMBRA_MUEBLE.copy(alpha = 0.22f), style = Stroke(1.1f * u))
            // La paja del limo: briznas claras y cortas.
            for (j in 0 until 80) {
                val q = Offset(ancho * azar(j, 81), alto * azar(j, 82))
                drawLine(Color(0xFFE2CB94).copy(alpha = 0.32f), q, q + Offset((2f + 3f * azar(j, 83)) * u, (azar(j, 84) - 0.5f) * 2f * u), 0.55f * u)
            }
        }
        return img
    }
}

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/**
 * Cuánta luz de la lámpara del suelo llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos del mueble. La lámpara
 * está abajo: lo de arriba del anaquel queda lejos y se hunde. Con [corto] (los Sueños de noche), el charco es más chico.
 */
internal fun alcanceEgipto(dx: Float, dy: Float, corto: Boolean = false): Float =
    (1f - sqrt(dx * dx * 0.8f + dy * dy) / (if (corto) 0.95f else 1.3f)).coerceIn(0f, 1f)

/** Cuánta luna entra por la puerta hasta un punto a [dx] y [dy] del centro de su mancha, en anchos del mueble. */
internal fun frioEgipto(dx: Float, dy: Float): Float = ((1f - sqrt(dx * dx * 1.1f + dy * dy) / 0.8f) * 0.95f).coerceIn(0f, 1f)

/** La luz de la lámpara y de la luna en un volumen: su repisa de cuántas y dónde cae su centro a lo ancho. */
internal fun luzVolumenEgipto(v: Volumen, oscuro: Boolean): Pair<Float, Float> {
    val m = MuebleEstanteEgipto
    val fx = m.LADO + v.centro * (1f - 2 * m.LADO)
    val desdeAbajo = ((v.repisas - v.repisa - 0.5f) * m.FILA) / m.ANCHO_NOMINAL + m.LAMP_BAJO
    val lunar = oscuro && v.noche
    val k = if (oscuro || v.noche) alcanceEgipto(fx - m.LAMP_X, desdeAbajo, lunar) else 1f
    val y = m.CORONA + (v.repisa + 0.5f) * m.FILA / m.ANCHO_NOMINAL
    val f = if (oscuro) frioEgipto(fx - m.PUERTA_X, y - m.PUERTA_Y) else 0f
    return k to f
}

// ---------------------------------------------------------------- ayudantes de forma

internal fun poligonoEgipto(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

internal fun corridoEgipto(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }
