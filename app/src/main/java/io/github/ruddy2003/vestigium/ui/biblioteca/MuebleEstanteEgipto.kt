package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * La paleta del almacén de la Tumba (Deir el-Medina, hacia 1200 a. C.): el enlucido de barro con paja, ocre tostado, y el
 * adobe de limo que asoma en los desconchones y en las roscas de la bóveda; la palmera de los postes, la acacia de los
 * tablones y la soga de fibra; el papiro, el lino y el yeso; la marga crema de las tinajas con su azul pálido y su rojo; el
 * bronce, la fayenza y el grano.
 */
internal object EgiptoTinta {
    /** El enlucido de barro con paja (ocre tostado), su luz al sol y el zócalo oscurecido por la humedad. */
    val ENLUCIDO = Color(0xFFBE8F58)
    val ENLUCIDO_SOL = Color(0xFFF0CC8E)
    val ZOCALO = Color(0xFF8A6240)
    /** El adobe de limo del Nilo, gris pardo, que asoma; la bóveda que se aleja en sombra; la paja. */
    val ADOBE = Color(0xFF8C7660)
    val BOVEDA = Color(0xFF8A643E)
    val PAJA = Color(0xFFE2C58C)
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
    /** El barro de los sellos y de la lámpara; la marga crema de las tinajas y el azul pálido con que se pintan. */
    val BARRO = Color(0xFF8C5839)
    val MARGA = Color(0xFFE6D3AE)
    val AZUL = Color(0xFF6FA9C4)
    /** El bronce de la balanza, el cobre de los cinceles, su verdín y la fayenza azul verdosa: lo frío de la paleta. */
    val BRONCE = Color(0xFFC49045)
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

    // Las luces: la noche del almacén, la de los Sueños, la sombra lila del atardecer, la llama, la luna, el sol y el ocaso.
    val NOCHE = Color(0xFF151A2C)
    val NOCHE_SUENOS = Color(0xFF0D1636)
    val ATARDECER = Color(0xFF6A4E7A)
    val LLAMA = Color(0xFFFFD27E)
    val LUNA = Color(0xFFBDD0F2)
    val SOL = Color(0xFFFFE6B0)
    val OCASO = Color(0xFFFF9A52)
}

/**
 * El aire del momento en el almacén: de día, el sol que entra por la puerta (a la izquierda, fuera del cuadro) y deja su
 * mancha recortada en el enlucido; de noche (el modo oscuro), la lámpara del suelo, a la derecha, alumbra de abajo arriba y
 * la luna entra por la misma puerta y deja su mancha fría donde de día está el sol; en los Sueños ([noche]) en claro, el sol
 * ya bajo (la mancha larga y anaranjada) con el resto en sombra lila y, en oscuro ([lunar]), la lámpara baja y la luna fuerte.
 * [cerca] es cuánto llega de la llama (0 a 1) y [frio], de la luna (o, de día, del sol: ver [puerta]).
 */
internal class AireEgipto(val oscuro: Boolean, val noche: Boolean) {
    val lumbre: Boolean get() = oscuro || noche
    val hondo: Color get() = if (oscuro) (if (noche) EgiptoTinta.NOCHE_SUENOS else EgiptoTinta.NOCHE) else SOMBRA_MUEBLE
    val lunar: Boolean get() = oscuro && noche
    /** El atardecer de los Sueños en claro. */
    val ocaso: Boolean get() = noche && !oscuro
    private val luna: Float get() = if (lunar) 1.3f else 1f

    private fun lunada(c: Color, f: Float): Color =
        if (f <= 0f) c else Color(c.red * (1f - 0.28f * f), c.green * (1f - 0.08f * f), (c.blue * (1f + 0.2f * f)).coerceAtMost(1f), c.alpha)

    fun luz(c: Color, cerca: Float, frio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, EgiptoTinta.ATARDECER, 0.26f * (1f - frio)), FILO_MUEBLE, 0.1f * k) else c
        val f = (frio * luna).coerceIn(0f, 1f) * (1f - k)
        if (lunar) return lunada(mezcla(mezcla(c, hondo, 0.9f - 0.8f * k.pow(0.9f) - 0.4f * f), FILO_MUEBLE, 0.32f * k), f)
        val base = mezcla(c, hondo, 0.86f - 0.76f * k.pow(0.9f) - 0.4f * f)
        return lunada(mezcla(base, FILO_MUEBLE, 0.22f * k), f * 0.8f)
    }
    /** La luz de la puerta sobre lo que toca: el sol (de día), el ocaso (Sueños en claro) o la luna (de noche). */
    fun puerta(a: Float): Color = when {
        !lumbre -> EgiptoTinta.SOL.copy(alpha = a.coerceIn(0f, 1f))
        !oscuro -> EgiptoTinta.OCASO.copy(alpha = (a * 0.85f).coerceIn(0f, 1f))
        else -> EgiptoTinta.LUNA.copy(alpha = (a * (if (lunar) 0.75f else 0.6f)).coerceIn(0f, 1f))
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(if (noche) a * 1.15f else a)
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.55f else a)
    fun filo(a: Float): Color = FILO_MUEBLE.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    fun helado(a: Float): Color = EgiptoTinta.LUNA.copy(alpha = if (oscuro) (a * luna).coerceIn(0f, 1f) else 0f)

    /** El color del ánimo (ya llevado a su color de día): de noche se apaga con la distancia a la lámpara, sin invertirse. */
    fun animo(t: Color, cerca: Float, frio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(t, EgiptoTinta.ATARDECER, 0.12f * (1f - frio)), FILO_MUEBLE, 0.06f * k) else t
        val f = (frio * luna).coerceIn(0f, 1f) * (1f - k)
        val d = (if (lunar) 0.62f else 0.56f) - (if (lunar) 0.6f else 0.52f) * k - 0.2f * f
        val s = 1f - 0.7f * d
        return lunada(mezcla(mezcla(Color(t.red * s, t.green * s, t.blue * s), hondo, 0.36f * d), FILO_MUEBLE, 0.24f * k), f * 0.7f)
    }
    /** El papiro o el yeso donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(cerca: Float, base: Color = EgiptoTinta.YESO): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, EgiptoTinta.ATARDECER, 0.1f) else base
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
        fun frio(p: Offset): Float = luzPuertaEgipto(p.x / w, p.y / w, a.ocaso)
        // Lo de arriba nunca se pierde del todo: un poco de la noche de fuera lo dibuja aunque quede lejos de la lámpara.
        fun arriba(p: Offset): Float = maxOf(cerca(p), if (a.oscuro) 0.1f else 0f)
        val poste = w * 0.062f
        val pI = Rect(w * 0.016f, ch - w * 0.07f, w * 0.016f + poste, suelo + w * 0.012f)
        val pD = Rect(w - w * 0.016f - poste, ch - w * 0.064f, w - w * 0.016f, suelo + w * 0.016f)
        val gT = w * 0.034f
        val apoyo = ch - gT
        val fondo = if (d.tablas.isNotEmpty()) d.tablas.last().bottom else ch
        val forma = formaPuertaEgipto(w, a.ocaso)
        val pieBal = Offset(w * 0.4f, apoyo)

        // ---- el testero de enlucido, con la mancha de la puerta, y la bóveda
        muroEgipto(a, ch, suelo, llama, forma)
        bovedaEgipto(a, ch)
        sombraBalanzaEgipto(pieBal, w, a, forma)

        // ---- la sombra del anaquel en el muro: detrás de cada repisa, la del tablón de arriba y la de los postes
        d.repisas.forEachIndexed { k, r -> sombraHuecoEgipto(r, pI, pD, a, k) }
        drawRect(a.sombra(if (oscuro) 0.3f else 0.14f), Offset(pI.right, fondo), Size(pD.left - pI.right, w * 0.022f))

        // ---- el suelo de tierra apisonada
        sueloEgipto(a, suelo, llama) { p -> cerca(p) }

        // ---- los postes de palmera y los tablones de acacia, atados
        posteEgipto(pI, a, 41, cerca(pI.center), frio(Offset(pI.center.x, ch)))
        posteEgipto(pD, a, 47, cerca(pD.center), frio(Offset(pD.center.x, ch)))
        val tablonA = Rect(w * 0.004f, apoyo, w * 0.996f, ch)
        tablonEgipto(tablonA, a, 300) { x -> arriba(Offset(x, ch)) to frio(Offset(x, ch)) }
        ataduraEgipto(Offset(pI.center.x, tablonA.center.y), poste, tablonA.height, a, arriba(Offset(pI.center.x, ch)))
        ataduraEgipto(Offset(pD.center.x, tablonA.center.y), poste, tablonA.height, a, arriba(Offset(pD.center.x, ch)))
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

        // ---- lo de arriba, de atrás adelante: la estera, la tinaja grande, la balanza, el cesto, la tinaja chica, las cebollas
        val hacia = { p: Offset -> if (a.oscuro) (if (llama.x >= p.x) 1f else -1f) else -1f }
        val pieEst = Offset(w * 0.075f, apoyo)
        esteraEgipto(pieEst, w, a, arriba(pieEst), frio(pieEst - Offset(0f, w * 0.12f)))
        val pieT1 = Offset(w * 0.175f, apoyo)
        tinajaEgipto(pieT1, w * 0.19f, w, a, arriba(pieT1), frio(pieT1 - Offset(0f, w * 0.1f)), hacia(pieT1), 0)
        balanzaEgipto(pieBal, w, a, arriba(pieBal), frio(pieBal - Offset(0f, w * 0.2f)), hacia(pieBal))
        val pieCesto = Offset(w * 0.7f, apoyo)
        cestoEgipto(pieCesto, w, a, arriba(pieCesto), frio(pieCesto - Offset(0f, w * 0.06f)), hacia(pieCesto))
        val pieT2 = Offset(w * 0.845f, apoyo)
        tinajaEgipto(pieT2, w * 0.14f, w, a, arriba(pieT2), frio(pieT2 - Offset(0f, w * 0.07f)), hacia(pieT2), 1)
        val clavoC = Offset(w * 0.958f, ch * 0.3f)
        cebollasEgipto(clavoC, w, a, arriba(clavoC + Offset(0f, w * 0.08f)), frio(clavoC))
        // La luz de la puerta también da en lo de arriba que cae dentro de su mancha.
        clipRect(0f, 0f, w, ch) { drawPath(forma, a.puerta(if (oscuro) 0.16f else 0.14f)) }

        // ---- al pie: el costal de trigo, la medida colmada, la tinaja medio enterrada y la lámpara en su pie de barro
        val pieCostal = Offset(w * 0.18f, suelo + w * 0.08f)
        costalEgipto(pieCostal, w, a, maxOf(cerca(pieCostal - Offset(0f, w * 0.06f)), if (a.lumbre) 0.1f else 1f), frio(pieCostal))
        val pieMedida = Offset(w * 0.38f, suelo + w * 0.1f)
        medidaEgipto(pieMedida, w, a, maxOf(cerca(pieMedida), if (a.lumbre) 0.12f else 1f), frio(pieMedida))
        val pieEnterrada = Offset(w * 0.58f, suelo + w * 0.085f)
        tinajaEnterradaEgipto(pieEnterrada, w, a, maxOf(cerca(pieEnterrada - Offset(0f, w * 0.05f)), if (a.lumbre) 0.12f else 1f), frio(pieEnterrada), hacia(pieEnterrada))
        lamparaEgipto(llama, suelo + w * 0.075f, w, a)
        if (a.lumbre) llamaEgipto(llama, w, a)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- el muro y la bóveda

    /**
     * El testero: enlucido de barro con paja, liso y cálido (ocre tostado), con las pasadas de la mano, la paja, dos grietas y
     * dos desconchones donde asoma el adobe; abajo, el zócalo oscurecido por la humedad. Encima, la mancha de la puerta: de día
     * el sol, recortado en diagonal por el dintel y la jamba; en los Sueños claros, el ocaso, más largo y más bajo; de noche, la
     * luna. De noche, además, la lámpara del suelo lo enciende de abajo arriba.
     */
    private fun DrawScope.muroEgipto(a: AireEgipto, ch: Float, suelo: Float, llama: Offset, forma: Path) {
        val u = density
        val w = size.width
        val muro = Rect(0f, 0f, w, suelo)
        drawRect(a.luz(EgiptoTinta.ENLUCIDO, 0f), muro.topLeft, muro.size)
        if (a.oscuro) {
            // De noche: el enlucido hundido y, encima, la luz de la lámpara en tres pasos (no un velo: se enciende el muro).
            val r = if (a.lunar) 0.95f else 1.35f
            mancha(llama, w * r, a.luz(EgiptoTinta.ENLUCIDO, 0.55f).copy(alpha = 0.9f))
            mancha(llama, w * r * 0.55f, a.luz(EgiptoTinta.ENLUCIDO, 0.85f).copy(alpha = 0.8f))
            mancha(llama, w * r * 0.22f, EgiptoTinta.LLAMA.copy(alpha = 0.32f))
        } else if (a.noche) {
            // El ocaso: la lámpara recién encendida, apenas un charco tibio.
            mancha(llama, w * 0.32f, EgiptoTinta.LLAMA.copy(alpha = 0.32f))
        }
        val k0 = if (a.oscuro) 0.2f else 1f
        // Las pasadas de la mano al alisar el barro: arcos anchos y tenues, claros y oscuros.
        val pasadas = Path()
        for (j in 0 until 22) {
            val c = Offset(w * azar(j, 501), suelo * azar(j, 502))
            val l = (22f + 30f * azar(j, 503)) * u
            pasadas.moveTo(c.x - l, c.y + l * 0.1f); pasadas.quadraticTo(c.x, c.y - l * (0.18f + 0.1f * azar(j, 504)), c.x + l, c.y + l * 0.06f)
        }
        drawPath(pasadas, a.luz(mezcla(EgiptoTinta.ENLUCIDO, EgiptoTinta.PAJA, 0.45f), k0).copy(alpha = 0.18f), style = Stroke(5f * u, cap = StrokeCap.Round))
        drawPath(corridoEgipto(pasadas, 2f * u, 4f * u), a.sombra(0.07f), style = Stroke(3f * u, cap = StrokeCap.Round))
        pinceladas(muro, a.luz(mezcla(EgiptoTinta.ENLUCIDO, EgiptoTinta.ZOCALO, 0.5f), k0), 617, 22, u, 0.12f)
        // La paja del barro: briznas cortas, en un solo camino.
        val paja = Path()
        for (j in 0 until 150) {
            val q = Offset(w * azar(j, 511), suelo * azar(j, 512))
            val l = (2f + 3.5f * azar(j, 513)) * u; val g = (azar(j, 514) - 0.5f) * 1.4f
            paja.moveTo(q.x, q.y); paja.lineTo(q.x + l, q.y + l * g)
        }
        drawPath(paja, a.luz(EgiptoTinta.PAJA, k0).copy(alpha = 0.55f), style = Stroke(0.7f * u))
        // Dos grietas finas del enlucido.
        val grietas = Path()
        listOf(Offset(w * 0.86f, ch * 0.62f) to 0.1f, Offset(w * 0.3f, ch * 0.3f) to -0.06f).forEachIndexed { j, (o, d) ->
            grietas.moveTo(o.x, o.y)
            var q = o
            for (i in 1..5) { q = Offset(q.x + (d + (azar(j * 7 + i, 521) - 0.5f) * 0.06f) * w * 0.3f, q.y + w * 0.022f); grietas.lineTo(q.x, q.y) }
        }
        drawPath(grietas, a.sombra(0.4f), style = Stroke(0.8f * u))
        // Los desconchones, donde asoma el adobe.
        desconchonEgipto(Offset(w * 0.79f, ch * 0.44f), w * 0.075f, a, 3, k0)
        desconchonEgipto(Offset(w * 0.47f, suelo - w * 0.07f), w * 0.06f, a, 9, k0)
        // El zócalo oscurecido por la humedad, con su borde desigual y la línea blanquecina del salitre.
        val zTop = suelo - w * 0.1f
        val zocalo = Path().apply {
            moveTo(0f, suelo)
            var x = 0f
            lineTo(0f, zTop)
            while (x < w) { x += w * 0.04f; lineTo(x, zTop + (azar((x / u).toInt(), 531) - 0.5f) * w * 0.025f) }
            lineTo(w, suelo); close()
        }
        drawPath(zocalo, a.luz(EgiptoTinta.ZOCALO, k0).copy(alpha = 0.55f))
        drawPath(zocalo, a.claro(0.12f), style = Stroke(1f * u))
        // La mancha de la puerta en el enlucido.
        when {
            !a.lumbre -> {
                drawPath(forma, EgiptoTinta.ENLUCIDO_SOL.copy(alpha = 0.78f))
                drawPath(forma, a.puerta(0.18f))
            }
            a.ocaso -> drawPath(forma, mezcla(EgiptoTinta.OCASO, EgiptoTinta.ENLUCIDO_SOL, 0.35f).copy(alpha = 0.62f))
            else -> drawPath(forma, a.luz(EgiptoTinta.ENLUCIDO, 0f, 1f).copy(alpha = 0.92f))
        }
    }

    /** Un desconchón del enlucido: el adobe gris pardo con sus juntas, el labio del enlucido con su sombra y su luz. */
    private fun DrawScope.desconchonEgipto(c: Offset, r: Float, a: AireEgipto, semilla: Int, k: Float) {
        val u = density
        val forma = Path()
        for (i in 0 until 9) {
            val ang = i / 9f * 6.283f
            val rr = r * (0.65f + 0.45f * azar(semilla, i)) * (if (i % 2 == 0) 1f else 0.8f)
            val x = c.x + rr * kotlin.math.cos(ang) * 1.3f; val y = c.y + rr * kotlin.math.sin(ang) * 0.75f
            if (i == 0) forma.moveTo(x, y) else forma.lineTo(x, y)
        }
        forma.close()
        drawPath(forma, a.luz(EgiptoTinta.ADOBE, k))
        clipPath(forma) {
            val juntas = Path()
            var y = c.y - r
            var f = 0
            while (y < c.y + r) {
                juntas.moveTo(c.x - r * 1.4f, y); juntas.lineTo(c.x + r * 1.4f, y)
                var x = c.x - r * 1.4f + (if (f % 2 == 0) 0f else r * 0.35f)
                while (x < c.x + r * 1.4f) { juntas.moveTo(x, y); juntas.lineTo(x, y + r * 0.34f); x += r * 0.7f }
                y += r * 0.34f; f++
            }
            drawPath(juntas, a.sombra(0.35f), style = Stroke(0.9f * u))
            drawPath(corridoEgipto(forma, 1.6f * u, 1.8f * u), a.sombra(0.3f), style = Stroke(2.4f * u))
        }
        drawPath(corridoEgipto(forma, -0.6f * u, -0.6f * u), a.claro(0.3f), style = Stroke(0.9f * u))
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
        drawPath(fuera, a.luz(EgiptoTinta.BOVEDA, 0f, 0f))
        drawPath(fuera, a.sombra(if (a.oscuro) 0.3f else 0.2f))
        for (j in 1..3) drawPath(arco(rosca * 2 + j * w * 0.035f, w * 0.03f * j), a.sombra(0.22f), style = Stroke(1.1f * u))
        // Las dos roscas de ladrillo: su cara, más clara, y las juntas que abren en abanico.
        val banda = Path().apply {
            moveTo(-1f, arranque); quadraticTo(w / 2, 2 * clave - arranque, w + 1f, arranque)
            lineTo(w + 1f, arranque - rosca * 2 * 0.25f)
            quadraticTo(w / 2, 2 * (clave - rosca * 2) - (arranque - rosca * 2 * 0.25f), -1f, arranque - rosca * 2 * 0.25f)
            close()
        }
        drawPath(banda, a.luz(EgiptoTinta.ADOBE, if (a.oscuro) 0.15f else 1f, if (a.oscuro) 0.5f else 0f))
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
     * La sombra de la balanza en el muro, dentro de la mancha de la puerta: el poste y el astil corridos hacia abajo y a la
     * derecha (la luz viene de arriba a la izquierda). De noche, la de la luna, más tenue.
     */
    private fun DrawScope.sombraBalanzaEgipto(pie: Offset, w: Float, a: AireEgipto, forma: Path) {
        val u = density
        val dx = w * 0.06f; val dy = w * 0.035f
        val alto = w * 0.34f
        val eje = Offset(pie.x + dx, pie.y - alto + dy)
        val brazo = w * 0.17f
        val c = a.sombra(if (a.oscuro) 0.3f else 0.26f)
        clipPath(forma) {
            drawRect(c, Offset(pie.x - w * 0.013f + dx, eje.y), Size(w * 0.026f, alto - dy - w * 0.03f))
            drawLine(c, Offset(eje.x - brazo, eje.y + brazo * 0.07f), Offset(eje.x + brazo, eje.y - brazo * 0.07f), 4.4f * u)
            drawLine(c, Offset(eje.x - brazo, eje.y + brazo * 0.07f), Offset(eje.x - brazo, eje.y + w * 0.13f), 1.2f * u)
            drawOval(c, Offset(eje.x - brazo - w * 0.06f, eje.y + w * 0.12f), Size(w * 0.12f, w * 0.03f))
        }
    }

    /** El fondo de un hueco: el enlucido del testero en la sombra del anaquel (la del tablón de arriba y la de los postes). */
    private fun DrawScope.sombraHuecoEgipto(r: Rect, pI: Rect, pD: Rect, a: AireEgipto, k: Int) {
        val u = density
        val x0 = pI.right; val x1 = pD.left
        val f = if (a.oscuro) 1.15f else 1f
        drawRect(a.sombra(0.26f * f), Offset(x0, r.top), Size(x1 - x0, r.height))
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

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/**
 * Cuánta luz de la lámpara del suelo llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos del mueble. La lámpara
 * está abajo: lo de arriba del anaquel queda lejos y se hunde. Con [corto] (los Sueños de noche), el charco es más chico.
 */
internal fun alcanceEgipto(dx: Float, dy: Float, corto: Boolean = false): Float =
    (1f - sqrt(dx * dx * 0.8f + dy * dy) / (if (corto) 0.95f else 1.3f)).coerceIn(0f, 1f)

/**
 * La mancha de la puerta en el testero, en anchos del mueble desde su esquina de arriba a la izquierda: un cuadrilátero con
 * el borde de arriba (el dintel) y el de la derecha (la jamba) recortados en diagonal. Con [ocaso] (los Sueños en claro), el
 * sol ya bajo: más larga y más baja.
 */
private fun puntosPuertaEgipto(ocaso: Boolean): FloatArray =
    if (ocaso) floatArrayOf(0f, 0.36f, 0.62f, 0.5f, 1.0f, 1.38f, 0f, 1.26f)
    else floatArrayOf(0f, 0.16f, 0.5f, 0.24f, 0.72f, MuebleEstanteEgipto.CORONA + 0.42f, 0f, MuebleEstanteEgipto.CORONA + 0.5f)

internal fun formaPuertaEgipto(w: Float, ocaso: Boolean): Path {
    val q = puntosPuertaEgipto(ocaso)
    return poligonoEgipto(-1f, q[1] * w, q[2] * w, q[3] * w, q[4] * w, q[5] * w, -1f, q[7] * w)
}

/** Cuánto da la luz de la puerta (de 0 a 1) en un punto a [x] e [y] anchos de la esquina: 1 dentro, con el borde un poco suave. */
internal fun luzPuertaEgipto(x: Float, y: Float, ocaso: Boolean): Float {
    val q = puntosPuertaEgipto(ocaso)
    if (x < 0f) return 0f
    val arriba = q[1] + (q[3] - q[1]) * x / q[2]
    val abajo = q[7] + (q[5] - q[7]) * x / q[4]
    val derecha = q[2] + (q[4] - q[2]) * ((y - q[3]) / (q[5] - q[3])).coerceIn(0f, 1f)
    return minOf((y - arriba) / 0.03f, (abajo - y) / 0.04f, (derecha - x) / 0.035f).coerceIn(0f, 1f)
}

/** La luz de la lámpara ([first]) y de la puerta ([second]) en un volumen: su repisa de cuántas y dónde cae su centro. */
internal fun luzVolumenEgipto(v: Volumen, oscuro: Boolean): Pair<Float, Float> {
    val m = MuebleEstanteEgipto
    val fx = m.LADO + v.centro * (1f - 2 * m.LADO)
    val desdeAbajo = ((v.repisas - v.repisa - 0.5f) * m.FILA) / m.ANCHO_NOMINAL + m.LAMP_BAJO
    val lunar = oscuro && v.noche
    val k = if (oscuro || v.noche) alcanceEgipto(fx - m.LAMP_X, desdeAbajo, lunar) else 1f
    val y = m.CORONA + (v.repisa + 0.55f) * m.FILA / m.ANCHO_NOMINAL
    return k to luzPuertaEgipto(fx, y, v.noche && !oscuro)
}

/** Las escalas del ánimo de Egipto, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_EGIPTO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.EGIPTO, false) to juegoTonos(TemaId.EGIPTO, true) }

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el neutro pasa del crema a
 * un casi negro, pensado para la interfaz) y un volumen claro se volvía negro de noche. Aquí el volumen es un objeto del
 * almacén: conserva su color de día y la luz lo apaga con la distancia a la lámpara. Se ubica el color en la escala de noche
 * (el tramo y la fracción que más se le acercan) y se toma el mismo punto en la de día; si no está en la escala, queda igual.
 */
internal fun tonoDeDiaEgipto(t: Color, oscuro: Boolean): Color {
    if (!oscuro) return t
    val (dia, noche) = TONOS_EGIPTO
    if (dia.size < 3 || noche.size < 3) return t
    var mejor = t
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = noche[seg]; val a1 = noche[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoEgipto(t.red - a0.red - dr * f) + cuadradoEgipto(t.green - a0.green - dg * f) + cuadradoEgipto(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = mezcla(dia[seg], dia[seg + 1], f) }
    }
    return mejor
}

private fun cuadradoEgipto(x: Float) = x * x

// ---------------------------------------------------------------- ayudantes de forma

internal fun poligonoEgipto(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

internal fun corridoEgipto(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }
