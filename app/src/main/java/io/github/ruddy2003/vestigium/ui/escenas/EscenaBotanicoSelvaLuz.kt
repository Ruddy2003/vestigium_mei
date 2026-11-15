package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La luz de la selva, que es medio cuadro (`mejores_referencias.md`; la lógica de la capilla de Roma, Scriptorium y
 * Renacimiento traída a un exterior bajo hojas):
 *
 * 1. **La luz tiene forma y cae sobre penumbra.** Bajo la bóveda, la penumbra es verde y azulada ([Luz.sombra]); el sol
 *    que entra deja en las tablas, en la mesa y en las macetas manchas con la forma de lo que lo deja pasar: el canto del
 *    alero, el poste, las hojas del plátano, lo que hay en el tablado ([luzEnPlano]).
 * 2. **La mancha camina con la hora**, porque cada cosa está puesta en metros y su sombra se tumba con el sol del
 *    momento ([sombras]): por la mañana el sol bajo de la derecha cruza todo el tablado; a mediodía cae a plomo entre
 *    las hojas; por la tarde el tejado de la casa deja casi todo en sombra.
 * 3. **La luz se ve en el aire húmedo:** tres o cuatro haces anchos y blandos bajan en diagonal entre las copas, con
 *    su polvo, encienden lo que tocan y cambian de sitio con la hora ([rayosEntreCopas], [enHaz]).
 * 4. **La luz enciende lo pintado:** lo que está al sol se pinta con el pincel del sol; lo demás, con el de la penumbra
 *    ([pincelEn], [alumbrado]).
 * 5. **De noche, dos luces:** la lámpara de la mesa, naranja, que enciende por debajo las hojas y las macetas cercanas, y
 *    la luna, que entra por el claro y deja su mancha fría en las tablas.
 *
 * **[Aproximación]**: el sol del cuadro (`Rayo`, que entra de lado y un poco desde delante), no el de Río a esa hora.
 */
internal object LuzSelva {
    private val C = ColorBotanico

    /** El color de las sombras bajo las hojas: el violeta de siempre, verdeado por la luz que pasa por ellas. */
    val SOMBRA_VERDE: Color = lerp(ColorBotanico.SOMBRA, ColorBotanico.ESCARABAJO, 0.35f)

    /** La luz de un momento en la selva. */
    class Luz(val m: Momento) {
        /** La luz de fuera (la del cielo y del sol). */
        val l: Luces = EscenaBotanico.luces(m)
        /** El sol o, de noche, la luna. */
        val r: Rayo = EscenaBotanico.rayo(l)
        val sol: Sol3 = Sol3(r)
        val deLuna: Boolean = r.dia < 0.1f
        /** Cuánto se notan las manchas de luz directa, de 0 (cubierto, o sin luna) a 1. */
        val directa: Float = if (deLuna) (r.fuerza / 0.42f * 0.9f).coerceIn(0f, 1f) else (r.fuerza / 0.72f).coerceIn(0f, 1f)
        /** El color de las manchas: de sol, cálido; de luna, frío. */
        val color: Color = if (deLuna) EscenaBotanico.LUZ_DE_LUNA else lerp(lerp(l.solColor, Color.White, 0.2f), Color(0xFFFFD49A), 0.4f)
        /** El pincel de lo que está al sol. */
        val p: Pincel = Pincel(l, r)
        /** La luz de la penumbra, bajo las hojas. */
        val lSombra: Luces = penumbra(l, directa)
        /** El pincel de lo que está en la penumbra: le llega la luz del claro, de la derecha y de arriba, suave. */
        val sombra: Pincel = Pincel(lSombra, if (deLuna || r.fuerza < 0.05f) Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, 0f, SOMBRA_VERDE, r.contraste * 0.7f) else rayoFijo(0.55f, 48f, 0f, SOMBRA_VERDE, 0.45f))
        /** La lámpara de la mesa, de 0 a 1. */
        val lampara: Float = SelvaBotanico.lampara(m)
        /** Cuánta bruma hay entre los planos. */
        val bruma: Float = SelvaBotanico.bruma(m)
        /** Las sombras ya tumbadas en cada plano (se piden muchas veces en un mismo dibujo). */
        internal val sombrasEn = HashMap<Float, List<List<Offset>>>()
        /** Los haces entre las copas, ya en el lienzo (se piden muchas veces en un mismo dibujo). */
        internal var haces: List<Haz>? = null
    }

    /** La luz de la penumbra: la de fuera, más baja y más verde y azul cuanto más fuerte es el sol (de noche, igual). */
    fun penumbra(l: Luces, directa: Float): Luces {
        val k = 1f - 0.42f * directa * (1f - l.noche)
        val a = l.ambiente
        val amb = Color((a.red * k * (1f - 0.16f * directa)).coerceIn(0f, 1f), (a.green * k * (1f - 0.02f * directa)).coerceIn(0f, 1f), (a.blue * k * (1f + 0.04f * directa)).coerceIn(0f, 1f))
        return Luces(l.alto, l.medio, l.bajo, amb, lerp(l.bruma, Color(0xFF6A8478), 0.3f), l.brumaFuerza, l.solX, l.solAltura, l.solColor, l.solFuerza, l.lunaX, l.lunaAltura, l.estrellas, l.noche, l.nublado, 0f)
    }

    // ---------------------------------------------------------------- Dónde da la luz

    /** El punto de las tablas que se ve en [en] (en el lienzo), en metros: x, 0, z. */
    fun enElSuelo(en: Offset): V3 { val z = (SelvaBotanico.F * SelvaBotanico.OJO / (en.y - SelvaBotanico.H).coerceAtLeast(1f)); return V3((en.x - SelvaBotanico.FUGA) * z / SelvaBotanico.F, 0f, z) }

    /** Lleva un punto [a] (en metros) por la luz del momento hasta el plano de altura [hp]: dónde cae su sombra. */
    fun cae(luz: Luz, a: V3, hp: Float): Offset { val h = (a.y - hp).coerceAtLeast(0f); return Offset(a.x + luz.sol.sx * h, a.z + luz.sol.sz * h) }

    /** Las cajas que dan sombra en el tablado y no son de la bóveda: el tejado de la galería, la casa y el poste. */
    private fun cajas(): List<List<V3>> {
        val s = SelvaBotanico; val g = s.GRUESO_POSTE / 2f
        return listOf(
            // El tejado de la galería (sube hacia la casa).
            listOf(V3(s.ALERO_X, s.ALTO_ALERO, s.ALERO_Z), V3(s.ALERO_X, s.ALTO_ALERO, -5f), V3(s.PARED_X, s.ALTO_CUMBRE, -5f), V3(s.PARED_X, s.ALTO_CUMBRE, s.ALERO_Z)),
            // La casa, con su tejado alto.
            listOf(-14f, s.PARED_X).flatMap { x -> listOf(0f, 4.8f).flatMap { y -> listOf(-8f, s.ALERO_Z + 0.3f).map { z -> V3(x, y, z) } } },
            // El poste.
            listOf(s.POSTE_X - g, s.POSTE_X + g).flatMap { x -> listOf(0f, s.ALTO_ALERO).flatMap { y -> listOf(s.POSTE_Z - g, s.POSTE_Z + g).map { z -> V3(x, y, z) } } },
            // Los falsos troncos de los dos plátanos de la bóveda.
            tronco(BovedaSelva.ESQUINA_CIMA, 0.16f), tronco(BovedaSelva.PLATANO_CIMA, 0.17f),
        )
    }

    /** Un tronco de [radio] metros que sube del jardín hasta [cima]. */
    private fun tronco(cima: V3, radio: Float): List<V3> = listOf(-radio, radio).flatMap { dx -> listOf(SelvaBotanico.sueloEn(cima.x, cima.z), cima.y).flatMap { y -> listOf(-radio, radio).map { dz -> V3(cima.x + dx, y, cima.z + dz) } } }

    /**
     * Las sombras en el plano de altura [hp] (las tablas, 0; la mesa, su tapa), en metros (x, z): el tejado, la casa,
     * el poste y cada hoja de la bóveda, tumbadas con la luz del momento. Las cajas, como su envolvente; las hojas, con
     * su contorno.
     */
    fun sombras(luz: Luz, hp: Float): List<List<Offset>> = luz.sombrasEn.getOrPut(hp) { tumbar(luz, hp) }

    private fun tumbar(luz: Luz, hp: Float): List<List<Offset>> {
        val r = ArrayList<List<Offset>>(rectas(luz, hp))
        for (h in BovedaSelva.HOJAS) r += h.contorno().map { cae(luz, it, hp) }
        return r
    }

    /** Solo las sombras rectas en el plano [hp]: el tejado, la casa, el poste y los troncos (sin las hojas). */
    private fun rectas(luz: Luz, hp: Float): List<List<Offset>> {
        val r = ArrayList<List<Offset>>()
        for (caja in cajas()) { val pts = caja.filter { it.y > hp }.map { cae(luz, it, hp) } + caja.filter { it.y <= hp + 0.001f }.map { Offset(it.x, it.z) }; if (pts.size >= 3) r += envolvente(pts) }
        return r
    }

    /** Si un punto de un polígono en (x, z) cae dentro (el rayo que cruza los lados). */
    private fun dentro(pol: List<Offset>, x: Float, z: Float): Boolean {
        var c = false; var j = pol.size - 1
        for (i in pol.indices) { val a = pol[i]; val b = pol[j]
            if ((a.y > z) != (b.y > z) && x < (b.x - a.x) * (z - a.y) / (b.y - a.y) + a.x) c = !c
            j = i }
        return c
    }

    /** Cuánta luz directa le llega al punto ([x], [h], [z]): 0 si está a la sombra del tejado, del poste o de una hoja. */
    fun alumbrado(luz: Luz, x: Float, h: Float, z: Float): Float {
        if (luz.directa < 0.04f) return 0f
        // La altura, de diez en diez centímetros: así las sombras de cada plano se tumban una sola vez por dibujo.
        val hq = kotlin.math.round(h * 10f) / 10f
        for (pol in sombras(luz, hq)) if (dentro(pol, x, z)) return 0f
        return luz.directa
    }

    /** Recorta un polígono en (x, z) para que no llegue más acá de [zMin] (lo que queda detrás de los ojos no se proyecta). */
    fun recorta(pol: List<Offset>, zMin: Float): List<Offset> {
        if (pol.isEmpty()) return pol
        val r = ArrayList<Offset>()
        for (i in pol.indices) {
            val a = pol[i]; val b = pol[(i + 1) % pol.size]
            val ina = a.y >= zMin; val inb = b.y >= zMin
            if (ina) r += a
            if (ina != inb) { val t = (zMin - a.y) / (b.y - a.y); r += Offset(a.x + (b.x - a.x) * t, zMin) }
        }
        return r
    }

    /** Un polígono en (x, z) del plano [hp], en el lienzo. */
    fun enLienzo(pol: List<Offset>, hp: Float): Path? { val q = recorta(pol, 0.6f); return if (q.size < 3) null else poligono(q.map { pc(it.x, hp, it.y) }) }

    // ---------------------------------------------------------------- El pincel de cada cosa

    /**
     * El pincel de lo que está en [en] (en el lienzo), con [alumbrado] de 0 a 1 (cuánto le da la luz directa): al sol,
     * el del sol; en la penumbra, el verde y azulado. De noche, junto a la lámpara, con su calor por el lado que la mira
     * (como en Renacimiento); lo demás queda del lado de la luna.
     */
    fun pincelEn(luz: Luz, en: Offset, alumbrado: Float, alcance: Float = 110f): Pincel {
        val f = luz.lampara
        if (f > 0.05f && luz.l.noche > 0.25f) {
            val d = (CercaSelva.LAMPARA - en).getDistance(); val calor = f / (1f + (d / alcance) * (d / alcance))
            if (calor > 0.06f) {
                val rr = rayoDeFoco(Foco(CercaSelva.LAMPARA, C.LUMBRE, 1f, alcance, suelo = en.y), en, 0.45f, C.SOMBRA)
                return Pincel(if (alumbrado > 0.5f) luz.l else luz.lSombra, rr, calor.coerceIn(0f, 0.92f), C.LUMBRE)
            }
        }
        return if (alumbrado > 0.5f) luz.p else luz.sombra
    }

    // ---------------------------------------------------------------- Las manchas

    /**
     * La luz directa en un plano de altura [hp] dentro de [zona] (en el lienzo): un moteado de hojas, no bloques. Llena la
     * zona del color de la luz y le quita la sombra de cada cosa, siempre con el borde blando (la penumbra de un sol que
     * no es un punto): la del tejado, la casa y el poste, rectas como son; la de cada hoja de la bóveda con su forma de
     * hoja, redondeada y con los rasgones por donde se cuela una raya de sol; las de las frondas de encima, en peine; y
     * las de [extra] (lo que hay en el plano), ya en el lienzo. Dentro de las sombras quedan los claros redondos que deja
     * el sol al colarse por los huecos de la bóveda (la imagen del sol, estirada si está bajo). Todo está puesto en
     * metros y se tumba con la luz del momento: las manchas caminan con la hora. Va en una sola capa que se suma a lo de
     * debajo.
     */
    fun DrawScope.luzEnPlano(luz: Luz, zona: Path, hp: Float, extra: List<Path>, fuerza: Float = 1f) {
        if (luz.directa < 0.03f) return
        val a = ((if (luz.deLuna) 0.62f else 0.47f) * luz.directa * fuerza).coerceIn(0f, 1f)
        drawIntoCanvas { c ->
            c.saveLayer(Rect(0f, 0f, ANCHO, ALTO), Paint().apply { blendMode = BlendMode.Plus; alpha = a })
            if (luz.deLuna) {
                // La luna no entra por todas partes: solo por el claro grande de la bóveda sobre el tablado. Su mancha tiene la
                // forma de ese hueco (con los bocados de las hojas en su orilla), clara y fría, y la reja de hojas la cruza.
                clipPath(zona) { for ((g, r) in CLARO_DE_LUNA) {
                    val c0 = Offset(g.x + 0.15f * luz.sol.sx, g.z + 0.15f * luz.sol.sz)
                    val pts = (0 until 22).map { i -> val t = (2 * kotlin.math.PI * i / 22).toFloat(); val rr = r * (1f + 0.16f * sin(3f * t + 1f) + 0.09f * sin(7f * t + g.x) - (if (i % 5 == 2) 0.14f else 0f))
                        pc(c0.x + cos(t) * rr * 1.25f, hp, c0.y + sin(t) * rr * 0.8f) }
                    val hueco = cerrada(pts)
                    drawPath(hueco, luz.color)
                    drawPath(hueco, Color.Black.copy(alpha = 0.45f), style = Stroke(2.6f), blendMode = BlendMode.DstOut)
                    // La reja: las hojas rotas y las frondas que cruzan el hueco, en sombra, con la plata entre ellas.
                    for ((i, t) in REJA.withIndex()) { val (a0, b0, w) = t; if (r < 0.6f && i > 0) continue
                        sombraDeHoja(luz, BovedaSelva.Hoja3(V3(c0.x, hp + 0.01f, c0.y), V3(c0.x + a0.x * r, hp + 0.01f, c0.y + a0.y * r), V3(c0.x + b0.x * r, hp + 0.01f, c0.y + b0.y * r), w * r, 0f, 0f, 0.75f, 0f, Semilla.de("botanico", "reja", i)), hp) }
                    sombraDeFronda(luz, Fronda(V3(c0.x - 1.35f * r, hp + 0.01f, c0.y + 0.6f * r), V3(c0.x + 0.2f * r, hp + 0.01f, c0.y - 0.7f * r), 0f, 0.32f * r), hp)
                } }
            } else drawPath(zona, luz.color)
            // Todo lo demás, dentro del plano: las rayas de sol de los rasgones no se salen de él.
            clipPath(zona) {
            // Las sombras rectas: el tejado, la casa, el poste y los troncos de los plátanos, con su penumbra.
            for (pol in rectas(luz, hp)) enLienzo(pol, hp)?.let { blanda(it, 1.4f + 0.8f * hp) }
            // Las hojas de la bóveda, con su forma y sus rasgones.
            for (h in BovedaSelva.HOJAS) sombraDeHoja(luz, h, hp)
            // Las frondas de encima, en peine.
            for (f in FRONDAS) sombraDeFronda(luz, f, hp)
            for (s in extra) blanda(s, 1.2f)
            // Los claros redondos: solo se ven donde hay sombra (se pintan por detrás de la luz que ya hay).
            if (!luz.deLuna) for (g in HUECOS) claro(luz, g, hp)
            }
            c.restore()
        }
    }

    /** Las hojas que cruzan el claro de la luna (del centro del claro, en radios del claro): de dónde a dónde y su ancho. **[Diseño propio]**. */
    private val REJA = listOf(Triple(Offset(-1.35f, -0.25f), Offset(0.4f, 0.5f), 0.4f), Triple(Offset(1.3f, -0.65f), Offset(-0.15f, 0.2f), 0.34f), Triple(Offset(1.35f, 0.55f), Offset(0.35f, 0.95f), 0.3f))

    /** Los claros grandes de la bóveda por donde entra la luna: el centro de su mancha en el plano (en metros) y su radio. **[Diseño propio]**. */
    private val CLARO_DE_LUNA = listOf(V3(0.3f, 0f, 3.55f) to 0.95f, V3(-0.45f, 0f, 2.5f) to 0.42f)

    /** Quita la luz dentro de [s] con el borde blando, de [w] unidades: casi toda dentro, la mitad en la orilla. */
    private fun DrawScope.blanda(s: Path, w: Float) {
        drawPath(s, Color.Black.copy(alpha = 0.9f), blendMode = BlendMode.DstOut)
        drawPath(s, Color.Black.copy(alpha = 0.45f), style = Stroke(w), blendMode = BlendMode.DstOut)
        drawPath(s, Color.Black.copy(alpha = 0.22f), style = Stroke(w * 2.2f), blendMode = BlendMode.DstOut)
    }

    /** Un trazo cerrado y suave que pasa por [pts] (Catmull-Rom cerrado). */
    fun cerrada(pts: List<Offset>): Path {
        val p = Path(); val n = pts.size; if (n < 3) return p
        p.moveTo(pts[0])
        for (i in 0 until n) { val a = pts[(i - 1 + n) % n]; val b = pts[i]; val c = pts[(i + 1) % n]; val d = pts[(i + 2) % n]
            p.cubicTo(b + (c - a) / 6f, c - (d - b) / 6f, c) }
        p.close(); return p
    }

    /** La sombra de una hoja de la bóveda en el plano [hp]: su forma, blanda, y una raya de sol en cada rasgón. */
    private fun DrawScope.sombraDeHoja(luz: Luz, h: BovedaSelva.Hoja3, hp: Float) {
        val c = h.contorno(7); val pts = recorta((c.subList(0, 8) + c.subList(9, c.size - 1)).map { cae(luz, it, hp) }, 0.6f)
        if (pts.size < 3) return
        val s = cerrada(pts.map { pc(it.x, hp, it.y) })
        blanda(s, 1.3f + 0.7f * hp)
        // Los rasgones: del borde hacia el nervio, sesgados hacia la punta; el sol pasa por ellos.
        if (h.rasgones.isEmpty()) return
        val rayas = ArrayList<Offset>()
        for ((i, r) in h.rasgones.withIndex()) for (l in listOf(-1f, 1f)) {
            if ((i + (if (l > 0f) 1 else 0)) % 3 == 2) continue
            val a = cae(luz, h.borde(r + 0.025f, l), hp); val b = cae(luz, h.nervio(r) + (h.borde(r, l) - h.nervio(r)) * 0.18f, hp)
            if (a.y < 0.7f || b.y < 0.7f) continue
            rayas += pc(a.x, hp, a.y); rayas += pc(b.x, hp, b.y)
        }
        if (rayas.isNotEmpty()) drawPoints(rayas, PointMode.Lines, luz.color.copy(alpha = 0.85f), 0.9f + 0.4f * hp, StrokeCap.Round)
    }

    /**
     * Una fronda de encima, en metros: del arranque [a] a la punta [b], arqueada [cae] metros hacia abajo en medio, con
     * pinnas de [pinna] metros. Son las de la palmera de la esquina, que se tienden sobre el tablado por fuera del cuadro.
     */
    class Fronda(val a: V3, val b: V3, val cae: Float, val pinna: Float) {
        fun en(t: Float): V3 = a + (b - a) * t + V3(0f, -cae * 4f * t * (1f - t), 0f)
    }

    /** Las frondas de la palmera de la esquina (fuera del cuadro, a la derecha, delante): su sombra en peine cruza el tablado. **[Diseño propio]**. */
    private val FRONDAS = listOf(
        Fronda(V3(3.1f, 3.6f, 2.2f), V3(0.6f, 3.0f, 1.3f), 0.35f, 0.42f),
        Fronda(V3(3.1f, 3.6f, 2.2f), V3(1.1f, 3.15f, 3.2f), 0.3f, 0.4f),
        Fronda(V3(3.1f, 3.6f, 2.2f), V3(1.9f, 2.7f, 4.3f), 0.45f, 0.36f),
        Fronda(V3(3.1f, 3.6f, 2.2f), V3(-0.4f, 3.25f, 2.3f), 0.4f, 0.44f),
    )

    /** La sombra de una fronda: el raquis y las pinnas, como un peine que se afina hacia la punta. */
    private fun DrawScope.sombraDeFronda(luz: Luz, f: Fronda, hp: Float) {
        val raquis = ArrayList<Offset>(); val pinnas = ArrayList<Offset>()
        var ant: Offset? = null
        for (i in 0..22) {
            val t = i / 22f; val q = f.en(t); val s = cae(luz, q, hp)
            if (s.y < 0.7f) { ant = null; continue }
            val e = pc(s.x, hp, s.y); ant?.let { raquis += it; raquis += e }; ant = e
            if (i < 2) continue
            // Las pinnas, a los dos lados, inclinadas hacia la punta; más cortas al final.
            val d = f.en((t + 0.04f).coerceAtMost(1f)) - f.en((t - 0.04f).coerceAtLeast(0f)); val dl = sqrt(d.x * d.x + d.z * d.z).coerceAtLeast(0.001f)
            val n = V3(-d.z / dl, 0f, d.x / dl); val u = V3(d.x / dl, 0f, d.z / dl); val l = f.pinna * (1f - 0.65f * t)
            for (lado in listOf(-1f, 1f)) { val p = cae(luz, q + n * (lado * l) + u * (l * 0.35f) + V3(0f, -l * 0.25f, 0f), hp); if (p.y < 0.7f) continue
                pinnas += e; pinnas += pc(p.x, hp, p.y) }
        }
        val k = SelvaBotanico.k(f.en(0.5f).let { cae(luz, it, hp).y }.coerceAtLeast(0.7f))
        drawPoints(pinnas, PointMode.Lines, Color.Black.copy(alpha = 0.55f), (0.07f * k).coerceIn(0.8f, 4f), StrokeCap.Round, blendMode = BlendMode.DstOut)
        drawPoints(pinnas, PointMode.Lines, Color.Black.copy(alpha = 0.8f), (0.035f * k).coerceIn(0.5f, 2f), StrokeCap.Round, blendMode = BlendMode.DstOut)
        drawPoints(raquis, PointMode.Lines, Color.Black.copy(alpha = 0.8f), (0.03f * k).coerceIn(0.5f, 2f), StrokeCap.Round, blendMode = BlendMode.DstOut)
    }

    /** Los huecos de la bóveda por donde se cuela el sol (en metros, a la altura de las hojas). **[Diseño propio]**. */
    private val HUECOS: List<V3> by lazy {
        val az = Azar(Semilla.de("botanico", "huecos"))
        List(46) { V3(az.entre(-2.6f, 2.6f), az.entre(2.7f, 3.8f), az.entre(1.2f, 6.4f)) }
    }

    /**
     * Un claro redondo: la imagen del sol que pasa por el hueco [g] y cae en el plano [hp], redonda con el sol alto y
     * estirada con el sol bajo, de borde blando. Más grande cuanto más lejos está el hueco del plano (como en una cámara
     * oscura). **[Aproximación]**: el tamaño se exagera para que se lea.
     */
    private fun DrawScope.claro(luz: Luz, g: V3, hp: Float) {
        val c = cae(luz, g, hp); if (c.y < 1.0f) return
        val alto = (g.y - hp).coerceAtLeast(0.3f)
        val r = 0.022f * alto + 0.03f
        val estira = 1f + 0.35f * sqrt(luz.sol.sx * luz.sol.sx + luz.sol.sz * luz.sol.sz).coerceAtMost(2.5f)
        val q = pc(c.x, hp, c.y); val k = SelvaBotanico.k(c.y)
        val rx = r * k * (1f + (estira - 1f) * abs(luz.sol.sx) / (abs(luz.sol.sx) + abs(luz.sol.sz) + 0.01f))
        val ry = r * k * (SelvaBotanico.OJO - hp).coerceAtLeast(0.3f) / c.y * (1f + (estira - 1f) * abs(luz.sol.sz) / (abs(luz.sol.sx) + abs(luz.sol.sz) + 0.01f))
        if (rx < 0.3f || ry < 0.15f) return
        scale(1f, ry / rx, q) {
            drawCircle(Brush.radialGradient(listOf(luz.color, luz.color.copy(alpha = 0.85f), luz.color.copy(alpha = 0f)), q, rx), rx, q, blendMode = BlendMode.DstOver)
        }
    }

    // ---------------------------------------------------------------- Los haces entre las copas

    /** Un haz de sol que baja por el aire húmedo, en el lienzo: de [a] (arriba) a [b] (donde llega), de [w0] a [w1] de ancho. */
    class Haz(val a: Offset, val b: Offset, val w0: Float, val w1: Float) {
        private val d = b - a; private val l2 = (d.x * d.x + d.y * d.y).coerceAtLeast(1f)
        /** Cuánto queda [q] dentro del haz, de 0 (fuera) a 1 (en medio). */
        fun dentro(q: Offset): Float {
            val t = (((q.x - a.x) * d.x + (q.y - a.y) * d.y) / l2); if (t < 0.05f || t > 1.05f) return 0f
            val c = a + d * t; val w = w0 + (w1 - w0) * t.coerceIn(0f, 1f)
            return (1f - (q - c).getDistance() / w).coerceIn(0f, 1f)
        }
    }

    /**
     * Los huecos altos entre las copas del jardín por donde baja la luz (en metros): de cada uno sale un haz que sigue el
     * sol del momento, así que el haz cambia de sitio con la hora. **[Diseño propio]**: puestos para que caigan entre el
     * árbol grande, el helecho y los plátanos.
     */
    private val HUECOS_ALTOS = listOf(V3(-1.2f, 9f, 13.5f), V3(1.6f, 8.2f, 11.5f), V3(3.6f, 7.6f, 9.5f), V3(0.3f, 7.4f, 7.6f))

    /** Los haces del momento (ninguno sin sol directo ni de noche). */
    fun hacesDeCopas(luz: Luz): List<Haz> = luz.haces ?: run {
        val r = ArrayList<Haz>()
        if (luz.directa >= 0.15f && !luz.deLuna) {
            // El sol del cuadro, con la inclinación del haz contenida (con el sol muy bajo no se ven horizontales).
            val hz = sqrt(luz.sol.sx * luz.sol.sx + luz.sol.sz * luz.sol.sz); val tope = if (hz > 1.15f) 1.15f / hz else 1f
            val sx = luz.sol.sx * tope; val sz = luz.sol.sz * tope
            for (g in HUECOS_ALTOS) {
                val baja = g.y - SelvaBotanico.sueloEn(g.x, g.z)
                var fin = V3(g.x + sx * baja, g.y - baja, g.z + sz * baja)
                if (fin.z < 3.2f) { val t = (g.z - 3.2f) / (g.z - fin.z); fin = g + (fin - g) * t }
                r += Haz(pc(g), pc(fin), 0.42f * SelvaBotanico.k(g.z), 0.62f * SelvaBotanico.k(fin.z))
            }
        }
        luz.haces = r; r
    }

    /** Cuánto le da un haz al punto [q] del lienzo, de 0 a 1: lo que encienden (las hojas casi amarillas, una heliconia). */
    fun enHaz(luz: Luz, q: Offset): Float = hacesDeCopas(luz).maxOfOrNull { it.dentro(q) } ?: 0f

    /**
     * Los haces anchos y blandos que bajan en diagonal por la humedad entre las copas: cada uno en tres franjas, de la
     * más ancha y tenue a la más estrecha y viva, que se encienden donde el aire tiene más humedad (abajo) y se pierden
     * arriba entre las hojas; con su polvo y, donde llegan, el resplandor de lo que tocan.
     */
    fun DrawScope.rayosEntreCopas(luz: Luz) {
        val haces = hacesDeCopas(luz); if (haces.isEmpty()) return
        val k = (luz.directa * (0.55f + 0.5f * luz.bruma)).coerceIn(0f, 1f)
        val c = lerp(luz.color, Color(0xFFFFF0C0), 0.3f)
        val az = Azar(Semilla.de("botanico", "polvo de los haces"))
        for (h in haces) {
            val d = h.b - h.a; val l = d.getDistance().coerceAtLeast(1f); val n = Offset(-d.y / l, d.x / l)
            for ((ancho, f) in listOf(1.45f to 0.3f, 1f to 0.5f, 0.5f to 0.85f)) {
                val p = poligono(h.a - n * (h.w0 * ancho), h.a + n * (h.w0 * ancho), h.b + n * (h.w1 * ancho), h.b - n * (h.w1 * ancho))
                drawPath(p, Brush.linearGradient(listOf(Color.Transparent, c.copy(alpha = (0.12f * k * f).coerceIn(0f, 1f)), c.copy(alpha = (0.2f * k * f).coerceIn(0f, 1f)), c.copy(alpha = (0.08f * k * f).coerceIn(0f, 1f))), h.a, h.b), blendMode = BlendMode.Plus)
            }
            val polvo = ArrayList<Offset>()
            repeat(14) { val u = az.entre(0.25f, 0.95f); val v = az.entre(-0.8f, 0.8f); polvo += h.a + d * u + n * (v * (h.w0 + (h.w1 - h.w0) * u)) }
            drawPoints(polvo, PointMode.Points, c.copy(alpha = (0.6f * k).coerceIn(0f, 1f)), 0.7f, StrokeCap.Round, blendMode = BlendMode.Plus)
            // Donde llega: el follaje se enciende en verde amarillo.
            brillo(h.b - d * 0.04f, h.w1 * 1.5f, h.w1 * 0.55f, lerp(c, ColorBotanico.HOJA_LUZ, 0.35f), (0.32f * k).coerceIn(0f, 1f))
        }
    }

    /** Una mancha de borde blando que se suma a lo de debajo (un resplandor, un claro de sol). */
    fun DrawScope.brillo(centro: Offset, rx: Float, ry: Float, color: Color, alfa: Float) {
        if (alfa <= 0.004f || rx <= 0f || ry <= 0f) return
        scale(rx / ry, 1f, centro) { drawCircle(Brush.radialGradient(listOf(color.copy(alpha = alfa), color.copy(alpha = alfa * 0.55f), color.copy(alpha = 0f)), centro, ry), ry, centro, blendMode = BlendMode.Plus) }
    }

    // ---------------------------------------------------------------- El remate y lo que se mueve

    /** La llama de la lámpara, en metros. */
    val LLAMA: V3 by lazy { CercaSelva.enLaMesa(0.84f, 0.52f) + V3(0f, 0.3f, 0f) }

    /**
     * Las sombras que la lámpara tumba en lo que está a [z] metros (las hojas de la bóveda): la del poste, que tiene al
     * lado, una franja; y la de la bromelia atada al poste, encima de la llama, una estrella de hojas enorme (la lámpara
     * está baja y las agranda). En el lienzo.
     */
    fun sombrasDeLaLampara(z: Float): Path {
        val l = LLAMA; val s = SelvaBotanico; val g = s.GRUESO_POSTE / 2f
        fun lleva(a: V3): Offset { val t = ((z - l.z) / (a.z - l.z).coerceAtLeast(0.05f)).coerceIn(1f, 12f); return pc(l + (a - l) * t) }
        val r = Path()
        if (z > s.POSTE_Z) r.addPath(poligono(listOf(lleva(V3(s.POSTE_X - g, l.y + 0.05f, s.POSTE_Z)), lleva(V3(s.POSTE_X - g, s.ALTO_ALERO, s.POSTE_Z)), lleva(V3(s.POSTE_X + g, s.ALTO_ALERO, s.POSTE_Z)), lleva(V3(s.POSTE_X + g, l.y + 0.05f, s.POSTE_Z)))))
        // La bromelia del poste: la roseta, de hojas tiesas en abanico hacia arriba.
        val b = V3(s.POSTE_X + g * 0.6f, 1.55f, s.POSTE_Z - g)
        if (z > b.z + 0.3f) { val c = lleva(b); val pts = ArrayList<Offset>()
            for (j in 0 until 11) { val a = -kotlin.math.PI.toFloat() / 2f + (j - 5f) * 0.27f; val punta = lleva(b + V3(cos(a) * 0.2f, -sin(a) * 0.2f + 0.02f, 0f)); val raiz = lleva(b + V3(cos(a + 0.13f) * 0.05f, -sin(a + 0.13f) * 0.05f, 0f))
                pts += punta; pts += raiz }
            r.addPath(poligono(listOf(c) + pts)) }
        return r
    }

    /**
     * De noche, la lámpara enciende por debajo una hoja de la bóveda (su contorno, [pts], en el lienzo, a [z] metros): el
     * envés se vuelve naranja, más cuanto más cerca de la llama; y lo que está junto a la llama (el poste, la bromelia
     * del poste) tumba en ella su sombra grande, como en la tercera hora de Renacimiento. Se pinta en el turno de la
     * hoja, para que lo de delante la tape.
     */
    fun DrawScope.lamparaEnLaHoja(luz: Luz, pts: List<Offset>, z: Float) {
        val f = luz.lampara; if (f < 0.05f || luz.l.noche < 0.25f) return
        val q = CercaSelva.LAMPARA; val alcance = 240f
        if (pts.minOf { (it - q).getDistance() } > alcance) return
        val calido = lerp(C.LUMBRE, C.NARANJA, 0.6f); val k = (f * (0.5f + 0.5f * luz.l.noche)).coerceIn(0f, 1f)
        val brocha = Brush.radialGradient(listOf(calido.copy(alpha = 0.95f * k), calido.copy(alpha = 0.62f * k), calido.copy(alpha = 0.24f * k), calido.copy(alpha = 0f)), q, alcance)
        clipPath(cerrada(pts)) { clipPath(sombrasDeLaLampara(z), ClipOp.Difference) { drawCircle(brocha, alcance, q, blendMode = BlendMode.Plus) } }
    }

    /**
     * Lo que va encima de todo: la lámpara de noche, con su halo grande y con forma (el núcleo blanco, el anillo cálido
     * y el aire alumbrado, más alto que ancho porque el tubo echa la luz arriba y abajo) en el aire húmedo.
     */
    fun DrawScope.remate(m: Momento, luz: Luz) {
        val f = luz.lampara
        if (f > 0.05f) {
            val q = CercaSelva.LAMPARA
            val n = 0.4f + 0.6f * luz.l.noche
            brillo(q + Offset(0f, -18f), 150f, 120f, C.LUMBRE, 0.2f * f * n)
            brillo(q + Offset(0f, -6f), 52f, 64f, lerp(C.LUMBRE, C.NARANJA, 0.2f), 0.3f * f * n)
            haloDeBruma(q, 22f, 26f, C.LUMBRE, 0.95f * f)
            brillo(q, 8f, 11f, Color(0xFFFFF4D0), 0.75f * f)
        }
    }

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val luz = Luz(m); val l = luz.l
        destellos(l, Rect(268f, SelvaBotanico.H + 1f, ANCHO, 156f), reloj, m.semilla("destellos bahia"), 14)
        // Las mariposas azules de brillo de metal, entre las flores; de día y con buen tiempo.
        if (EscenaBotanico.mariposas(m)) {
            val az = Azar(Semilla.de("botanico", "mariposas"))
            repeat(4) { i ->
                val c = Offset(az.entre(150f, 300f) + 26f * sin(reloj * az.entre(0.25f, 0.45f) + i * 2f), az.entre(110f, 215f) + 12f * sin(reloj * az.entre(0.6f, 1f) + i))
                val bate = abs(sin(reloj * 8f + i * 1.7f)); val tam = az.entre(2.6f, 3.6f)
                val azul = lerp(Color(0xFF3E78C8), C.ESCARABAJO, 0.12f)
                for (s in listOf(-1f, 1f)) {
                    val w = tam * (0.25f + 0.75f * bate)
                    drawOval(lerp(azul, Color.White, 0.25f * bate).copy(alpha = 0.95f), Offset(if (s < 0f) c.x - w else c.x, c.y - tam * 0.75f), androidx.compose.ui.geometry.Size(w, tam * 0.9f))
                    drawOval(lerp(azul, C.BREA, 0.35f), Offset(if (s < 0f) c.x - w * 0.8f else c.x, c.y), androidx.compose.ui.geometry.Size(w * 0.8f, tam * 0.6f))
                }
                drawLine(C.BREA, c + Offset(0f, -tam * 0.5f), c + Offset(0f, tam * 0.5f), 0.6f)
            }
        }
        // Las luciérnagas, entre las plantas y en el tablado, de noche.
        if (EscenaBotanico.luciernagas(m)) {
            val az = Azar(Semilla.de("botanico", "luciernagas"))
            val verde = Color(0xFFE6F08A)
            repeat(46) { i ->
                val x = az.entre(0f, ANCHO) + 6f * sin(reloj * az.entre(0.3f, 0.8f) + i); val y = az.entre(90f, 270f) + 4f * cos(reloj * az.entre(0.3f, 0.7f) + i * 1.3f)
                val a = (0.5f + 0.5f * sin(reloj * az.entre(1.2f, 2.6f) + i)).let { it * it }
                drawCircle(verde.copy(alpha = 0.9f * a), 0.8f, Offset(x, y)); drawCircle(verde.copy(alpha = 0.22f * a), 3.2f, Offset(x, y), blendMode = BlendMode.Plus)
            }
        }
        // Las polillas alrededor de la lámpara encendida: «a la luz de una lámpara rodeada de insectos».
        if (luz.lampara > 0.3f) {
            val az = Azar(Semilla.de("botanico", "polillas")); val q = CercaSelva.LAMPARA + Offset(0f, -6f)
            repeat(7) { i -> val r = az.entre(5f, 16f); val a = reloj * az.entre(1.5f, 3.2f) * (if (i % 2 == 0) 1f else -1f) + i; val c = q + Offset(cos(a) * r, sin(a * 1.3f) * r * 0.6f)
                drawCircle(lerp(C.LONA, C.LUMBRE, 0.5f).copy(alpha = 0.8f), 0.7f, c) }
        }
        // Las gotas que caen de las puntas de las hojas y del alero, con la lluvia y con la niebla.
        if (SelvaBotanico.mojado(m)) {
            val az = Azar(Semilla.de("botanico", "gotas")); val gota = lerp(l.tono(Color(0xFFE2ECF6)), Color.White, 0.25f)
            val desde = BovedaSelva.HOJAS.map { pc(it.punta) } + (0..8).map { pc(SelvaBotanico.ALERO_X - 0.4f * it, SelvaBotanico.ALTO_ALERO + 0.08f * it, SelvaBotanico.ALERO_Z) }
            for ((i, q) in desde.withIndex()) {
                val ciclo = az.entre(0.8f, 1.8f); val fase = az.entre(0f, ciclo); val t = ((reloj + fase) % ciclo) / ciclo
                val y = q.y + t * t * 70f
                if (y < ALTO) drawLine(gota.copy(alpha = 0.7f * (1f - t * 0.5f)), Offset(q.x, y), Offset(q.x, y + 2.4f), 0.8f, StrokeCap.Round)
                if (i % 3 == 0) drawCircle(gota.copy(alpha = 0.6f), 0.7f, q + Offset(0f, 0.8f))
            }
        }
        tiempoVivo(l, m.ficha.clima, reloj, SelvaBotanico.H, m.semilla("tiempo"))
    }

    /** Si hoy hay sol (o luna) que deje mancha: para las pruebas y para quien quiera saberlo. */
    fun hayMancha(m: Momento): Boolean = Luz(m).directa > 0.15f

    /** Hacia dónde se tumban las sombras ahora (metros por metro de alto, de lado y hacia el fondo), o null sin luz directa. */
    fun tumba(m: Momento): Offset? { val luz = Luz(m); return if (luz.directa < 0.15f) null else Offset(luz.sol.sx, luz.sol.sz) }

    /** Si la luz directa le da a la tapa de la mesa de trabajo (en su centro) ahora. */
    fun solEnLaMesa(m: Momento): Boolean { val luz = Luz(m); val c = CercaSelva.CENTRO_MESA; return !luz.deLuna && alumbrado(luz, c.x, c.y + 0.02f, c.z) > 0.5f }

    /** El clima no deja luz directa. */
    fun cubierto(c: Clima): Boolean = c == Clima.LLUVIA || c == Clima.TORMENTA || c == Clima.NIEBLA || c == Clima.NIEVE
}
