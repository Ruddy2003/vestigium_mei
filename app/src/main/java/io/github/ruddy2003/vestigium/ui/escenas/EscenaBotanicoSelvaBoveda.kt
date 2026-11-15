package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.k
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La bóveda de hojas de la selva: lo que cubre el cuadro por arriba, como la cubierta de un invernadero. Las hojas
 * enormes del plátano que crece junto a la mesa, que se abren por encima del tablado; la enredadera en flor del poste,
 * que sigue el canto del alero y cuelga; y las lianas del árbol grande, que cruzan por arriba. Deja libre el claro de
 * la derecha (el cielo, la bahía y los astros).
 *
 * Cada hoja está puesta en metros ([Hoja3]): así se dibuja en su sitio y, con la misma forma, tumba su sombra en las
 * tablas, en la mesa y en las macetas, que camina con la hora ([LuzSelva]). **[Diseño propio]**.
 */
internal object BovedaSelva {
    private val C = ColorBotanico

    /**
     * Una hoja grande en metros: de la [base] (donde acaba el pecíolo) a la [punta], de [ancho] metros; el nervio cae
     * [caida] metros en medio. [vuelta], [rasgado] y [seca], como en `hojaDePlatano`. [desde]: de dónde sale el
     * pecíolo (la cima de la mata).
     */
    class Hoja3(val desde: V3, val base: V3, val punta: V3, val ancho: Float, val caida: Float, val vuelta: Float, val rasgado: Float, val seca: Float, val semilla: Long) {
        fun nervio(s: Float): V3 = base + (punta - base) * s + V3(0f, -caida * 4f * s * (1f - s), 0f)
        private val lado: V3 = (punta - base).let { d -> val n = sqrt(d.x * d.x + d.z * d.z).coerceAtLeast(0.001f); V3(-d.z / n, 0f, d.x / n) }
        private fun media(s: Float) = ancho * 0.5f * sin(PI.toFloat() * s).coerceAtLeast(0f).let { sqrt(it) } * 0.85f

        /** El borde de la hoja en [s] (de 0, la base, a 1, la punta), del lado [l] (−1 o 1), en metros. */
        fun borde(s: Float, l: Float): V3 = nervio(s) + lado * (l * media(s) * (0.6f + 0.4f * (l * vuelta).coerceAtLeast(0f)))

        /** El contorno de la hoja (un lado de ida y el otro de vuelta), en metros: para su sombra. */
        fun contorno(pasos: Int = 8): List<V3> {
            val ida = (0..pasos).map { i -> borde(i / pasos.toFloat(), 1f) }
            val vuelta2 = (pasos downTo 0).map { i -> borde(i / pasos.toFloat(), -1f) }
            return ida + vuelta2
        }

        /** Dónde está rota la hoja (de 0 a 1 a lo largo del nervio): los rasgones que dejan pasar el sol en su sombra. */
        val rasgones: List<Float> by lazy { val az = Azar(semilla + 7L); val r = ArrayList<Float>(); var s = 0.14f + az.entre(0f, 0.08f); while (s < 0.9f) { if (az.real() < rasgado) r += s; s += az.entre(0.08f, 0.17f) }; r }

        /** El punto medio de la hoja: para ordenar de atrás adelante. */
        val medio: V3 get() = nervio(0.5f)
    }

    /** La mata de plátano junto a la mesa: pisa en el jardín, detrás del poste; su cima. */
    val PLATANO_PIE = V3(-1.6f, SelvaBotanico.sueloEn(-1.6f, 6.8f), 6.8f)
    val PLATANO_CIMA = V3(-1.6f, 2.3f, 6.8f)

    /**
     * Las hojas de la bóveda, fijas: las del plátano junto a la mesa, que se abren hacia quien mira y hacia la derecha,
     * unas altas y tiesas y otras vencidas; alguna rota y colgando.
     */
    val HOJAS: List<Hoja3> by lazy {
        val az = Azar(Semilla.de("botanico", "boveda"))
        fun mata(c: V3, puntas: List<V3>, desde: Int) = puntas.mapIndexed { i, t ->
            val d = t - c
            val base = c + d * 0.24f + V3(0f, 0.15f, 0f)
            Hoja3(c, base, t, az.entre(0.5f, 0.66f), az.entre(0.08f, 0.3f) + (if (t.y < 2.6f) 0.25f else 0f), az.entre(-0.6f, 0.6f), az.entre(0.35f, 0.85f), if (desde + i == 6) 0.8f else az.entre(0f, 0.25f), Semilla.de("botanico", "hoja boveda", desde + i))
        }
        mata(PLATANO_CIMA, listOf(
            // (las de en medio, altas: dejan ver el árbol grande y el helecho por debajo)
            V3(0.75f, 3.35f, 4.85f), V3(-0.25f, 3.65f, 4.75f), V3(-1.25f, 3.5f, 5.05f), V3(1.25f, 3.15f, 5.7f),
            V3(-2.75f, 3.05f, 5.7f), V3(-0.55f, 3.05f, 4.45f), V3(-2.15f, 1.95f, 7.7f), V3(0.2f, 3.9f, 6.2f),
        ), 0) + mata(ESQUINA_CIMA, listOf(
            // (ninguna baja por delante de la luna ni del sol, que salen por el claro de la derecha)
            V3(-0.35f, 2.95f, 3.95f), V3(0.35f, 3.05f, 3.25f), V3(1.0f, 2.95f, 4.2f), V3(0.9f, 3.5f, 2.45f), V3(1.8f, 3.7f, 3.4f),
            // Las que se tienden por encima de quien mira (fuera del cuadro, por arriba): dan la luz rota en las tablas de delante.
            V3(0.55f, 3.15f, 1.55f), V3(1.3f, 3.0f, 1.15f), V3(-0.45f, 2.95f, 2.2f), V3(0.15f, 3.45f, 1.95f), V3(-1.1f, 3.1f, 2.7f),
            // Las bajas del otro lado de la mata (fuera del cuadro, a la derecha): con el sol bajo tienden su sombra por el tablado.
            V3(3.6f, 1.6f, 2.3f), V3(3.25f, 1.85f, 4.0f), V3(3.95f, 2.2f, 3.3f), V3(3.0f, 1.4f, 1.7f),
        ), 8)
    }

    /**
     * La cima del plátano de la esquina, en el jardín a la derecha del tablado, fuera del cuadro: solo se ven sus hojas,
     * que se tienden por encima de las tablas y dan la luz rota del mediodía.
     */
    val ESQUINA_CIMA = V3(2.5f, 2.6f, 2.75f)

    /** Las hojas de la bóveda que quedan detrás del alero (se pintan antes que él) y las que quedan delante. */
    private fun detras(h: Hoja3): Boolean = h.medio.z > SelvaBotanico.ALERO_Z

    /** Cuánto sol le pasa a una hoja por detrás, de 0 a 1: las que están cerca del sol en el cuadro o debajo del sol alto. */
    fun contraluz(luz: LuzSelva.Luz, q: Offset): Float {
        if (luz.deLuna) return 0f
        val l = luz.l
        val sol = Offset(l.solX * 380f, SelvaBotanico.H * (1f - l.solAltura))
        val cerca = (1f - (q - sol).getDistance() / 230f).coerceIn(0f, 1f)
        val alto = (l.solAltura / 1.35f).coerceIn(0f, 1f) * (1f - q.y / 170f).coerceIn(0f, 1f)
        return (luz.directa * (0.4f + 0.6f * maxOf(cerca, alto * 0.7f))).coerceIn(0f, 1f)
    }

    // ---------------------------------------------------------------- Lo que se pinta

    /** Las hojas de la bóveda que quedan detrás del alero y el pecíolo de cada una: van antes que la galería. */
    fun DrawScope.bovedaDeDetras(m: Momento, luz: LuzSelva.Luz) {
        for (h in HOJAS.filter { detras(it) }.sortedByDescending { it.medio.z }) hoja(m, luz, h)
    }

    /** Las hojas de la bóveda que quedan delante del alero, la enredadera del poste y las lianas que cruzan cerca: lo último. */
    fun DrawScope.boveda(m: Momento, luz: LuzSelva.Luz) {
        for (h in HOJAS.filter { !detras(it) }.sortedByDescending { it.medio.z }) hoja(m, luz, h)
        enredadera(m, luz)
    }

    private fun DrawScope.hoja(m: Momento, luz: LuzSelva.Luz, h: Hoja3) {
        val az = Azar(h.semilla)
        val b = pc(h.base); val t = pc(h.punta); val mid = pc(h.nervio(0.5f))
        val d = t - b; val l = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y / l, d.x / l)
        val curva = ((mid - (b + t) * 0.5f).let { it.x * n.x + it.y * n.y })
        val zm = h.medio.z
        val pin = LuzSelva.pincelEn(luz, mid, LuzSelva.alumbrado(luz, h.medio.x, h.medio.y + 0.4f, h.medio.z), alcance = 45f)
        // El pecíolo, de la cima de la mata a la hoja.
        val desde = pc(h.desde)
        drawLine(pin.medio(lerp(C.HOJA, C.ARENA, 0.2f)), desde, b, (0.05f * k(h.desde.z)).coerceAtLeast(0.6f))
        // No todas se encienden igual: las que el sol cruza de lleno, mucho; las que quedan detrás de otras, poco.
        val cl = contraluz(luz, mid) * (0.3f + 0.7f * Azar(h.semilla + 1L).real())
        // A contraluz la luz que pasa por la lámina es la del sol, no la de la penumbra.
        val pinH = if (cl > 0.3f) luz.p else pin
        // De noche, las que miran a la luna (las de la derecha, hacia el claro) se platean por su cara.
        val t0 = if (luz.deLuna) ((mid.x - 170f) / 160f).coerceIn(-0.4f, 0.6f) * luz.directa.coerceAtLeast(0.3f) else -0.15f + 0.5f * cl
        with(PiezasBotanico) {
            hojaDePlatano(pinH, b, t, h.ancho * k(zm), curva, h.vuelta, h.rasgado, h.seca, t0.coerceIn(-1f, 1f), az, 0f, cl)
        }
        // De noche, la lámpara la enciende por debajo.
        with(LuzSelva) { lamparaEnLaHoja(luz, h.contorno(7).let { it.subList(0, 8) + it.subList(9, it.size - 1) }.map { pc(it) }, zm) }
        // Mojada, la hoja brilla: dos filos de luz a lo largo de la lámina, y la gota que cuelga de la punta.
        if (SelvaBotanico.mojado(m)) {
            val brillo = lerp(luz.l.alto, Color.White, 0.5f).copy(alpha = 0.35f); val w = h.ancho * k(zm) * 0.25f
            for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(b + n * (s * w * 0.3f)); cubicTo(b + d * 0.3f + n * (s * w + curva * 0.6f), b + d * 0.7f + n * (s * w * 0.9f + curva * 0.6f), t + n * (s * w * 0.2f)) }, brillo, style = Stroke(0.6f, cap = StrokeCap.Round))
            drawCircle(lerp(luz.l.alto, Color.White, 0.4f).copy(alpha = 0.7f), 0.9f, t + Offset(0f, 1.2f))
        }
    }

    /**
     * La enredadera en flor del poste: sube enroscada, se abre en el canto del alero y cuelga en guirnaldas; flores
     * naranjas en racimos (la del poste es de fuego, como las que cubren las casas de Río en invierno) **[Diseño propio]**.
     */
    private fun DrawScope.enredadera(m: Momento, luz: LuzSelva.Luz) {
        val az = Azar(Semilla.de("botanico", "enredadera"))
        val x = SelvaBotanico.POSTE_X; val z = SelvaBotanico.POSTE_Z; val g = SelvaBotanico.GRUESO_POSTE * 0.5f
        val pin = LuzSelva.pincelEn(luz, pc(x, 1.6f, z), LuzSelva.alumbrado(luz, x + 0.2f, 1.6f, z - 0.2f))
        // El tallo, enroscado en el poste de abajo arriba, del lado que se ve.
        val tallo = (0..18).map { i -> val h = 0.15f + 2.3f * i / 18f; val a = i * 1.1f; pc(x + g * 1.2f * cos(a), h, z - g * 1.1f * (0.4f + 0.6f * abs(sin(a)))) }
        with(PiezasBotanico) { liana(pin, tallo, 1.1f, az, 0f, hojas = true) }
        // Por el canto del alero: una guirnalda que sale del poste hacia la derecha y otra hacia el fondo, con colgajos.
        val canto = (0..10).map { i -> val s = i / 10f; pc(SelvaBotanico.ALERO_X + 0.05f + 1.2f * s, SelvaBotanico.ALTO_ALERO - 0.12f - 0.18f * sin(PI.toFloat() * s), SelvaBotanico.ALERO_Z - 0.05f - 0.3f * s) }
        val flor = lerp(C.NARANJA, C.TEJA, 0.25f)
        val hojas = ArrayList<Offset>(); val claras = ArrayList<Offset>()
        for ((i, q) in (canto + tallo.drop(10)).withIndex()) {
            repeat(5) { val c = q + Offset(az.entre(-6f, 6f), az.entre(-3f, 5f)); val a = az.entre(0f, 6.28f); val r = az.entre(1.4f, 2.6f)
                (if (az.real() > 0.6f) claras else hojas).apply { add(c); add(c + Offset(cos(a) * r, sin(a) * r)) } }
            if (i < canto.size && az.real() < 0.6f) { val largo = az.entre(10f, 28f); val pts = (0..5).map { j -> q + Offset(az.entre(-1.5f, 1.5f) + sin(j * 0.9f) * 1.5f, largo * j / 5f) }
                with(PiezasBotanico) { liana(pin, pts, 0.5f, az, 0f, hojas = true) } }
        }
        drawPoints(hojas, androidx.compose.ui.graphics.PointMode.Lines, pin.sombra(lerp(C.SELVA, C.HOJA, 0.3f)), 1.6f, androidx.compose.ui.graphics.StrokeCap.Round)
        drawPoints(claras, androidx.compose.ui.graphics.PointMode.Lines, pin.luz(lerp(C.SELVA, C.HOJA, 0.55f)), 1.5f, androidx.compose.ui.graphics.StrokeCap.Round)
        // Los racimos de flores: tubitos naranjas en manojos que cuelgan.
        for (q in canto.filterIndexed { i, _ -> i % 2 == 0 } + listOf(tallo[14], tallo[17])) {
            val c = q + Offset(az.entre(-4f, 4f), az.entre(1f, 6f))
            repeat(7) { val a = PI.toFloat() / 2f + az.entre(-1.1f, 1.1f); val r = az.entre(2.5f, 5f); val fin = c + Offset(cos(a) * r, sin(a) * r)
                drawLine(pin.por(flor, (cos(a) * 0.6f).let { if (pin.solALaDerecha) it else -it }), c, fin, 1.1f, androidx.compose.ui.graphics.StrokeCap.Round)
                drawCircle(pin.luz(lerp(flor, C.LATON, 0.4f)), 0.5f, fin) }
        }
    }
}
